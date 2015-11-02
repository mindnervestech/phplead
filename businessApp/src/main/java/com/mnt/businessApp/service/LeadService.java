package com.mnt.businessApp.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.mnt.businessApp.viewmodel.BuildinLeadDetailsVM;
import com.mnt.businessApp.viewmodel.BuildinLeadVM;
import com.mnt.businessApp.viewmodel.LeadDetailsVM;
import com.mnt.businessApp.viewmodel.LeadHistoryVM;
import com.mnt.businessApp.viewmodel.LeadVM;
import com.mnt.businessApp.viewmodel.ReassignUserVM;
import com.mnt.businessApp.viewmodel.ZoneVM;
import com.mnt.entities.authentication.AuthUser;
import com.mnt.entities.businessApp.ActivityStream;
import com.mnt.entities.businessApp.Lead;
import com.mnt.entities.businessApp.LeadAgeing;
import com.mnt.entities.businessApp.LeadDetails;
import com.mnt.entities.businessApp.Product;
import com.mnt.entities.businessApp.User;

@Service
public class LeadService {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private DealerService dealerService;

	@Autowired
	private MailService mailService;
	
	@Autowired
	private SmsSender smsSender;
	
	@Autowired
	private JdbcTemplate jt;

	public List<LeadDetailsVM> getAllLeadDetails() {
		return getLeadDetailVM(null, null, " and u.id = l.user_id ", "0", "0", 0L, 0L, "0");
	}
	public List<BuildinLeadDetailsVM> getAllBuildinLeadDetails() {
		return getBuildinLeadDetailVM(null, null, " and l.origin = 'Built-In' ", "0", "0", 0L, 0L, "0");
	}
	
	public LeadVM getLeadVMById(Long id) {
		Lead lead = getLeadById(id);
		LeadVM leadVM = new LeadVM(lead);
		//BuildinLeadVM leadVM = new BuildinLeadVM(lead);
		int i = 0;
		Map map = jt.queryForList("select * from generalconfig").get(0);
		int followUpReminderCount =  Integer.parseInt((String)map.get("followUpReminderCount"));
		for (ActivityStream activityStream : lead.getActivityStream()){
			if(activityStream.newDisposition2.equals("Not Contacted")){
				i++;
				if(i>followUpReminderCount){
					leadVM.isLost = true;
					break;
				}
			}
		}
		return leadVM;
	}
	
	public BuildinLeadVM getBuildinLeadVMById(Long id) {
		Lead lead = getLeadById(id);
		//LeadVM leadVM = new LeadVM(lead);
		BuildinLeadVM leadVM = new BuildinLeadVM(lead);
		int i = 0;
		Map map = jt.queryForList("select * from generalconfig").get(0);
		int followUpReminderCount =  Integer.parseInt((String)map.get("followUpReminderCount"));
		for (ActivityStream activityStream : lead.getActivityStream()){
			if(activityStream.newDisposition2.equals("Not Contacted")){
				i++;
				if(i>followUpReminderCount){
					leadVM.isLost = true;
					break;
				}
			}
		}
		return leadVM;
	}

	public Lead getLeadById(Long id) {
		return (Lead) sessionFactory.getCurrentSession().get(Lead.class, id);
	}



	public void updateLead(LeadVM vm) {
		Lead lead = getLeadById(vm.getId());
		lead.setFollowUpDate(null);
		lead.setBrand(null);
		lead.setModalNo(null);
		lead.setDisposition3(null);
		ActivityStream activityStream = new ActivityStream();
		activityStream.setNewDisposition1(vm.getDisposition1());
		activityStream.setNewDisposition2(vm.getDisposition2());
		activityStream.setNewDisposition3(vm.getDisposition3());
		activityStream.setOldDisposition1(lead.getDisposition1());
		activityStream.setOldDisposition2(lead.getDisposition2());
		activityStream.setOldDisposition3(lead.getDisposition3());
		Integer notInterestedCount = jt.queryForObject("Select count(*) from activitystream where activitystream.newDisposition1 = 'Tried Contacted' and activitystream.lead_id = "+lead.getId(),Integer.class);
		if(notInterestedCount >= 5){
			activityStream.setNewDisposition2("Lost");
			vm.setDisposition2("Lost");
			vm.setBrand("");
			sendTriedContactedSms(vm,lead.getLeadDetails().getLms());
		}
		activityStream.setLead(lead);
		activityStream.setReason(vm.getReason());
		activityStream.setCreatedDate(new Date());
		sessionFactory.getCurrentSession().save(activityStream);
		if(lead.getDisposition2() == null || (!lead.getDisposition2().equals("Won") && !lead.getDisposition2().equals("Lost"))){
			LeadAgeing ageing;
			long secs ;
			Integer hours;
			if(vm.getDisposition2().equals("Purchased") || vm.getDisposition2().equals("Lost")){
				if(vm.getBrand().equals("Bosch") || vm.getBrand().equals("Siemens")){
					ageing = getLeadAgeing(lead.getId(),"Won");
				} else {
					ageing = getLeadAgeing(lead.getId(),"Lost");
				}
				secs = (new Date().getTime() - lead.getUploadDate().getTime() ) / 1000;
				hours = (int) (secs / 3600);
				ageing.setIsCurrent(false);
			} else {
				ageing = getLeadAgeing(lead.getId(), lead.getDisposition1());
				secs = (new Date().getTime() - lead.getLastDispo1ModifiedDate().getTime()) / 1000;
				hours =  (int) ((secs / 3600)+ageing.getAgeing()) ;
				LeadAgeing currentAgeing = getLeadAgeing(lead.getId(), vm.getDisposition1());
				currentAgeing.setAgeing(0L);
				currentAgeing.setProduct(lead.getLeadDetails().getProduct().getName());
				currentAgeing.setIsCurrent(true);
				sessionFactory.getCurrentSession().update(currentAgeing);
				if(ageing.getStatus() != currentAgeing.getStatus()){
					ageing.setIsCurrent(false);
				}
			}
			ageing.setAgeing(Long.valueOf(hours));
			ageing.setProduct(lead.getLeadDetails().getProduct().getName());
			sessionFactory.getCurrentSession().update(ageing);
		}
		sessionFactory.getCurrentSession().flush();
		lead.setDisposition1(vm.getDisposition1());
		lead.setDisposition2(vm.getDisposition2());
		lead.setReason(vm.getReason());
		lead.addActivityStream(activityStream);
		switch(vm.getDisposition2()){
		case "Purchased":
			if(vm.getBrand().equals("Bosch") || vm.getBrand().equals("Siemens")){
				lead.setStatus("Won");
			} else {
				lead.setStatus("Lost");
			}
			lead.setBrand(vm.getBrand());
			lead.setModalNo(vm.getModalNo());
			break;
		case "Lost":
			lead.setStatus("Lost");
		case "Not Interested":
			System.out.println("Dispostion2 : " + vm.getDisposition2());
			System.out.println("Status : " + vm.getStatus());
			lead.setStatus("Lost");
			lead.setDisposition3(vm.getDisposition3());
			break;
		default:
			lead.setStatus("Open");
			lead.setDisposition3(vm.getDisposition3());
		}
		if(lead.getDisposition1().equals("Tried Contacted") || lead.getDisposition2().equals("Call back/Follow up") || 
				(lead.getDisposition2().equals("Interested") && (lead.getDisposition3().equals("Call back-want to purchase later") || lead.getDisposition3().equals("Call back-evaluating")))){
			lead.setFollowUpDate(vm.getFollowUpDate());
		}
		lead.setLastDispo1ModifiedDate(new Date());
		sessionFactory.getCurrentSession().update(lead);
		if(vm.getDisposition2().equals("Interested")){
			sendInterestedSms(vm,lead.getLeadDetails().getLms());
		}
		if(vm.getDisposition3() != null){
			if(vm.getDisposition3().equals("Call back-evaluating") || vm.getDisposition3().equals("Call back-want to purchase later")){
				
				sendCallbackSms(vm,lead.getLeadDetails().getLms());
			}
		}
		
	}
	
	

	private void sendTriedContactedSms(LeadVM vm,String brand) {
		String phone = vm.getContactNo().toString();
		String message = "";
		message = "Dear Customer, we missed you. If you wish to get more information about " + brand + " " + vm.getProduct() + ", please call back on <9899989889>. Thank you." + brand + " Home Appliances";
		smsSender.sendSms(phone,message);
		
	}

	private void sendCallbackSms(LeadVM vm,String brand) {
		String phone = vm.getContactNo().toString();
		String message = "";
		message = "Dear Customer, we missed you. To get more information about " + brand + " " + vm.getProduct() + ", please call back on <9899989889>. Thank you." + brand + " Home Appliances";
		System.out.println("Message :: " + message);
		smsSender.sendSms(phone,message);
		
	}

	private void sendInterestedSms(LeadVM vm,String brand) {
		System.out.println("In Interested Sms");
		String phone = vm.getContactNo().toString();
		String message = "";
		message = "Dear Customer, Thank You for showing interest in " + brand + " " + vm.getProduct() + ". For any further information, please call on <9899989889>. Thank you " + brand + " Home Appliances";
		smsSender.sendSms(phone,message);
	}
	
	public void updateBuildinLead(BuildinLeadVM vm) {
		AuthUser authUser = Utils.getLoggedInUser();
		Lead lead = getLeadById(vm.getId());
		
		Session session = sessionFactory.getCurrentSession();
		LeadDetails details = (LeadDetails) sessionFactory.getCurrentSession().get(LeadDetails.class, lead.getLeadDetails().getId());
		
		
		
		details.setId(lead.getLeadDetails().getId());
		details.setCity(vm.getCity());
		details.setContactNo(vm.getContactNo());
		details.setEmail(vm.getEmail());
		details.setName(vm.getName());
		
		
		Product product = (Product)session.get(Product.class, Long.parseLong(vm.getProductId()));
		details.setProduct(product);
		
		details.setUploadDate(new Date());
		details.setState(vm.getState());
		details.setSr(vm.getLeadNumber());
		details.setSrNo(vm.getLeadNumber());
		details.setArea(vm.getArea());
		details.setKitchenmake(vm.getKitchenmake());
		details.setSource(vm.getSource());
		details.setBudget(vm.getBudget());
		details.setPurchase(vm.getPurchase());
		details.setLms(vm.getLms());
		session.update(details);
		
		lead.setFollowUpDate(null);
		lead.setBrand(null);
		lead.setModalNo(null);
		lead.setDisposition3(null);
		lead.setOrigin("Built-In");
		ActivityStream activityStream = new ActivityStream();
		activityStream.setNewDisposition1(vm.getDisposition1());
		activityStream.setNewDisposition2(vm.getDisposition2());
		activityStream.setNewDisposition3(vm.getDisposition3());
		activityStream.setOldDisposition1(lead.getDisposition1());
		activityStream.setOldDisposition2(lead.getDisposition2());
		activityStream.setOldDisposition3(lead.getDisposition3());
		Integer notInterestedCount = jt.queryForObject("Select count(*) from activitystream where activitystream.newDisposition1 = 'Tried Contacted' and activitystream.lead_id = "+lead.getId(),Integer.class);
		if(notInterestedCount >= 5){
			activityStream.setNewDisposition2("Lost");
			vm.setDisposition2("Lost");
			vm.setBrand("");
		}
		activityStream.setLead(lead);
		activityStream.setReason(vm.getReason());
		activityStream.setCreatedDate(new Date());
		sessionFactory.getCurrentSession().save(activityStream);
		if(lead.getDisposition2() == null || (!lead.getDisposition2().equals("Won") && !lead.getDisposition2().equals("Lost"))){
			LeadAgeing ageing;
			long secs ;
			Integer hours;
			if(vm.getDisposition2().equals("Purchased") || vm.getDisposition2().equals("Lost")){
				if(vm.getBrand().equals("Bosch") || vm.getBrand().equals("Siemens")){
					ageing = getLeadAgeing(lead.getId(),"Won");
				} else {
					ageing = getLeadAgeing(lead.getId(),"Lost");
				}
				secs = (new Date().getTime() - lead.getUploadDate().getTime() ) / 1000;
				hours = (int) (secs / 3600);
				ageing.setIsCurrent(false);
			} else {
				ageing = getLeadAgeing(lead.getId(), lead.getDisposition1());
				secs = (new Date().getTime() - lead.getLastDispo1ModifiedDate().getTime()) / 1000;
				hours =  (int) ((secs / 3600)+ageing.getAgeing()) ;
				LeadAgeing currentAgeing = getLeadAgeing(lead.getId(), vm.getDisposition1());
				currentAgeing.setAgeing(0L);
				currentAgeing.setProduct(lead.getLeadDetails().getProduct().getName());
				currentAgeing.setIsCurrent(true);
				sessionFactory.getCurrentSession().update(currentAgeing);
				if(ageing.getStatus() != currentAgeing.getStatus()){
					ageing.setIsCurrent(false);
				}
			}
			ageing.setAgeing(Long.valueOf(hours));
			ageing.setProduct(lead.getLeadDetails().getProduct().getName());
			sessionFactory.getCurrentSession().update(ageing);
		}
		sessionFactory.getCurrentSession().flush();
		lead.setDisposition1(vm.getDisposition1());
		lead.setDisposition2(vm.getDisposition2());
		lead.setReason(vm.getReason());
		lead.addActivityStream(activityStream);
		switch(vm.getDisposition2()){
		case "Purchased":
			if(vm.getBrand().equals("Bosch") || vm.getBrand().equals("Siemens")){
				lead.setStatus("Won");
			} else {
				lead.setStatus("Lost");
			}
			lead.setBrand(vm.getBrand());
			lead.setModalNo(vm.getModalNo());
			break;
		case "Lost":
			lead.setStatus("Lost");
		case "Not Interested":
			lead.setStatus("Lost");
			lead.setDisposition3(vm.getDisposition3());
			break;
		default:
			lead.setStatus("Open");
			lead.setDisposition3(vm.getDisposition3());
		}
		if(lead.getDisposition1().equals("Tried Contacted") || lead.getDisposition2().equals("Call back/Follow up") || 
				(lead.getDisposition2().equals("Interested") && (lead.getDisposition3().equals("Call back-want to purchase later") || lead.getDisposition3().equals("Call back-evaluating")))){
			lead.setFollowUpDate(vm.getFollowUpDate());
		}
		lead.setLastDispo1ModifiedDate(new Date());
		sessionFactory.getCurrentSession().update(lead);
	}

	private LeadAgeing getLeadAgeing(Long id, String disposition1) {
		Query query = sessionFactory.getCurrentSession().createQuery("from LeadAgeing as la where la.lead_id = "+id+" and la.status  = '"+disposition1+"'");
		List<LeadAgeing> ageings = query.list();
		if(ageings.size() != 0){
			return ageings.get(0);
		}
		LeadAgeing ageing = new LeadAgeing();
		ageing.setLead_id(id);
		ageing.setStatus(disposition1);
		ageing.setAgeing(0L);
		sessionFactory.getCurrentSession().save(ageing);
		return ageing;
	}

	public List<LeadHistoryVM> getLeadHistory(Long id) {
		Lead lead = getLeadById(id);
		List<LeadHistoryVM> leadHistoryVMs = new ArrayList<LeadHistoryVM>();
		for(ActivityStream stream : lead.getActivityStream()){
			LeadHistoryVM leadHistoryVM = new LeadHistoryVM(stream);

			leadHistoryVMs.add(leadHistoryVM);
		}
		return leadHistoryVMs;
	}

	public List<LeadDetailsVM> getAllEscalatedLeadDetails(Date start, Date end, String zone, String state, Long product, Long dealer, String brand) {
		AuthUser user = Utils.getLoggedInUser();
		String escalationSql = " and u.id = l.user_id and l.status = 'Escalated' ";
		if(start!=null){
			
			if(dealer != 0 || product != 0 || !zone.equals("0") || !state.equals("0")){
				if(!zone.equals("0") && !state.equals("0")){
					escalationSql += " and l.zone = '"+zone+"' and ld.state = '"+state+"'";
				} else if(!zone.equals("0")){
					escalationSql += " and l.zone = '"+zone+"'";
				} else if(!state.equals("0")){
					escalationSql += " and ld.state = '"+state+"' and l.zone = (Select user.zone from user where user.id = "+user.getEntityId()+" )";
				}
				if(product != 0 ){
					escalationSql += " and ld.product_id ="+product;
				} 
				if(dealer != 0){
					escalationSql += " and l.user_id = "+dealer;	
				}  
			} 

			/*if(product != 0 ){
				escalationSql += " and ld.product_id ="+product;
			}
			if(dealer != 0){
				escalationSql += " and l.user_id = "+dealer;
			}
			if(!state.equals("0")){
				escalationSql += " and ld.state = '"+state+"' and l.zone = (Select user.zone from user where user.id = "+user.getEntityId()+" )";
			}*/
		}
		if(user.getEntityName().equals("ZSM") || user.getEntityName().equals("Sellout Manager") || 
				user.getEntityName().equals("TSR") || user.getEntityName().equals("RSM") ||  user.getEntityName().equals("Sales Executive")) {
			escalationSql +=" and l.escalatedTo_id =  "+user.getEntityId();
		}
		return getLeadDetailVM(start, end, escalationSql, "0", "0", 0L, 0L, brand);
	}

	public List<LeadDetailsVM> getFollowUpLeads() {
		AuthUser user = Utils.getLoggedInUser();
		List<LeadDetailsVM> vms = new ArrayList<LeadDetailsVM>();
		String sql = "";
		String userSql = "";

		return getLeadDetailVM(null, null, " and l.followUpDate IS NOT NULL and u.id = l.user_id ", "0", "0", 0L, 0L, "0");

	}
	
	public List<BuildinLeadDetailsVM> getBuiltinFollowUpLeads() {
		AuthUser user = Utils.getLoggedInUser();
		List<BuildinLeadDetailsVM> vms = new ArrayList<BuildinLeadDetailsVM>();
		String sql = "";
		String userSql = "";

		return getBuildinLeadDetailVM(null, null, " and l.followUpDate IS NOT NULL and l.origin = 'Built-In' ", "0", "0", 0L, 0L, "0");

	}

	public List<LeadDetailsVM> getOpenLeads(Date start, Date end, String zone, String state, Long product, Long dealer, String brand) {
		return  getLeadDetailVM(start, end, " and l.status = 'Open' and u.id = l.user_id", zone, state, product, dealer, brand);
	}

	public List<LeadDetailsVM> getWonLeads(Date start, Date end, String zone, String state, Long product, Long dealer, String brand) {
		return getLeadDetailVM(start, end, " and l.status = 'Won' and u.id = l.user_id", zone, state, product, dealer, brand);
	}

	public List<LeadDetailsVM> getLostLeads(Date start, Date end, String zone, String state, Long product, Long dealer, String brand) {
		return getLeadDetailVM(start, end," and l.status = 'Lost' and u.id = l.user_id", zone, state, product, dealer, brand);
	}
	
	public List<LeadDetailsVM> getOverviewLeads(Date start, Date end, String zone, String state, Long product, Long dealer, String brand) {
		return getLeadDetailVM(start, end," and l.user_id is null ", zone, state, product, dealer, brand);
	}
	
	private List<LeadDetailsVM> getLeadDetailVM(Date start, Date end, String query, String zone, String state, Long product, Long dealer, String brand) {
		List<LeadDetailsVM> vms = new ArrayList<>();
		AuthUser user = Utils.getLoggedInUser();
		String sql = "";
		String date = "";
		String userQuery = "";
		if(start != null)
			date = " and l.lastDispo1ModifiedDate > '"+new SimpleDateFormat("yyyy-MM-dd").format(start)+"' "
					+ " and  l.lastDispo1ModifiedDate < '"+new SimpleDateFormat("yyyy-MM-dd").format(getDate(end))+"' ";
		if(dealer != 0 || product != 0 || !zone.equals("0") || !state.equals("0") || !brand.equals("0")){
			if(!zone.equals("0") && !state.equals("0")){
				userQuery += " and l.zone = '"+zone+"' and ld.state = '"+state+"'";
			} else if(!zone.equals("0")){
				userQuery += " and l.zone = '"+zone+"'";
			} else if(!state.equals("0")){
				if(user.getEntityName().equals("ZSM") || user.getEntityName().equals("Sellout Manager"))
				userQuery += " and ld.state = '"+state+"' and l.zone = (Select user.zone from user where user.id = "+user.getEntityId()+" )";
			}
			if(!brand.equals("0")){
				query += " and ld.lms = '"+brand+"' ";
			}
			if(product != 0 ){
				userQuery += " and ld.product_id ="+product;
			} else {
				if(user.getEntityName().equals("Category Manager") || user.getEntityName().equals("Sellout-Regional") || user.getEntityName().equals("RSM") || user.getEntityName().equals("TSR") || user.getEntityName().equals("Sales Consultant")){
					sql = "select product.id from product where product.id IN (SELECT user_product.products_id from user_product WHERE user_product.User_id = "+user.getEntityId()+") ";
					userQuery += " and ld.product_id IN ("+sql+")";
				}
			}
			if(dealer != 0){
				userQuery += " and l.user_id = "+dealer;
				if(user.getEntityName().equals("RSM") || user.getEntityName().equals("TSR")|| user.getEntityName().equals("Sales Consultant") || user.getEntityName().equals("Sales Executive")){
					userQuery += " and ld.pinCode IN (Select uz.zipcodes_id from user_zipcode uz where uz.user_id = "+user.getEntityId()+" ) ";
				} 
			} 
			if(user.getEntityName().equals("RSM") || user.getEntityName().equals("TSR")|| user.getEntityName().equals("Sales Consultant") || user.getEntityName().equals("Sales Executive")){
				userQuery += " and (l.user_id = "+user.getEntityId()+"  or ld.pinCode IN (Select uz.zipcodes_id from user_zipcode uz where uz.user_id = "+user.getEntityId()+" ) ) ";
			} 
		} else if(user.getEntityName().equals("Dealer") ){
			userQuery = " and l.user_id = "+user.getEntityId()+"";
			query += " and ld.product_id IN ( select products_id  from user_product  where User_id = "+user.getEntityId()+" )";
		} else if(user.getEntityName().equals("RSM") || user.getEntityName().equals("TSR") || user.getEntityName().equals("Sales Consultant") || user.getEntityName().equals("Sales Executive")){
			query += " and ld.product_id IN ( select products_id  from user_product  where User_id = "+user.getEntityId()+" )";
			userQuery = "and (l.user_id = "+user.getEntityId()+"";
			userQuery += " or ld.pinCode IN (Select uz.zipCodes_id from user_zipcode uz where uz.user_id = "+user.getEntityId()+" ) )";
		} else if(user.getEntityName().equals("ZSM") || user.getEntityName().equals("Sellout Manager")){
			userQuery = " and (l.user_id = "+user.getEntityId()+"";
			userQuery += " or l.zone = (Select user.zone from user where user.id = "+user.getEntityId()+" ) )";
		} else {
			userQuery = "";
			if(user.getEntityName().equals("Category Manager") || user.getEntityName().equals("Sellout-Regional")){
				query += " and ld.product_id IN ( select products_id  from user_product  where User_id = "+user.getEntityId()+" )";
			}
		}
		sql = "Select ld.sr as srNo, ld.name as name,  ld.lms as lms, "
				+" l.id as id,ld.email as email, ld.contactNo as contactNo,"
				+" ld.pinCode as pincode,p.name as product,ld.state as state,l.disposition1 as dispo1,"
				+" l.disposition2 as dispo2,l.disposition3 as dispo3,l.status as status,l.followUpDate as date,l.assignLeadDate as assignDate"
				+" FROM lead as l, leaddetails as ld, product as p, user as u where p.id = ld.product_id"
				+" and ld.id = l.leadDetails_id and l.origin != 'Built-In' "+query+userQuery+date;
		if(query.contains("l.user_id is null")){
			sql = "Select ld.sr as srNo, ld.name as name,  ld.lms as lms, "
					+" l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+" ld.pinCode as pincode,p.name as product,ld.state as state,l.disposition1 as dispo1,"
					+" l.disposition2 as dispo2,l.disposition3 as dispo3,l.status as status,l.followUpDate as date,l.assignLeadDate as assignDate"
					+" FROM lead as l, leaddetails as ld, product as p where p.id = ld.product_id"
					+" and ld.id = l.leadDetails_id and l.origin != 'Built-In' "+query+userQuery+date;
		}
		System.out.println("SQL : " + sql);
		
		List<Map<String, Object>> rows = jt.queryForList(sql);
		for(Map map : rows) {
			vms.add(new LeadDetailsVM(map));
		}
		return vms;
	}
	
	private List<BuildinLeadDetailsVM> getBuildinLeadDetailVM(Date start, Date end, String query, String zone, String state, Long product, Long dealer, String brand) {
		List<BuildinLeadDetailsVM> vms = new ArrayList<>();
		AuthUser user = Utils.getLoggedInUser();
		String sql = "";
		String date = "";
		String userQuery = "";
		String select = "";
		String from = "";
		if(start != null)
			date = " and l.lastDispo1ModifiedDate > '"+new SimpleDateFormat("yyyy-MM-dd").format(start)+"' "
					+ " and  l.lastDispo1ModifiedDate < '"+new SimpleDateFormat("yyyy-MM-dd").format(getDate(end))+"' ";
		if(dealer != 0 || product != 0 || !zone.equals("0") || !state.equals("0") || !brand.equals("0")){
			if(!zone.equals("0") && !state.equals("0")){
				userQuery += " and l.zone = '"+zone+"' and ld.state = '"+state+"'";
			} else if(!zone.equals("0")){
				userQuery += " and l.zone = '"+zone+"'";
			} else if(!state.equals("0")){
				if(user.getEntityName().equals("ZSM") || user.getEntityName().equals("Sellout Manager"))
				userQuery += " and ld.state = '"+state+"' and l.zone = (Select user.zone from user where user.id = "+user.getEntityId()+" )";
			}
			if(!brand.equals("0")){
				query += " and ld.lms = '"+brand+"' ";
			}
			if(product != 0 ){
				userQuery += " and ld.product_id ="+product;
			} else {
				if(user.getEntityName().equals("Category Manager") || user.getEntityName().equals("Sellout-Regional") || user.getEntityName().equals("RSM") || user.getEntityName().equals("TSR") || user.getEntityName().equals("Sales Consultant")){
					sql = "select product.id from product where product.id IN (SELECT user_product.products_id from user_product WHERE user_product.User_id = "+user.getEntityId()+") ";
					userQuery += " and ld.product_id IN ("+sql+")";
				}
			}
			if(dealer != 0){
				userQuery += " and l.user_id = "+dealer;
				if(user.getEntityName().equals("RSM") || user.getEntityName().equals("TSR")|| user.getEntityName().equals("Sales Consultant") || user.getEntityName().equals("Sales Executive")){
					userQuery += " and ld.pinCode IN (Select uz.zipcodes_id from user_zipcode uz where uz.user_id = "+user.getEntityId()+" ) ";
				} 
			} 
			if(user.getEntityName().equals("RSM") || user.getEntityName().equals("TSR")|| user.getEntityName().equals("Sales Consultant") || user.getEntityName().equals("Sales Executive")){
				userQuery += " and (l.user_id = "+user.getEntityId()+"  or ld.pinCode IN (Select uz.zipcodes_id from user_zipcode uz where uz.user_id = "+user.getEntityId()+" ) ) ";
			} 
		} else if(user.getEntityName().equals("Dealer") ){
			select = "u.name as userName,";
			from = ", user as u";
			userQuery = " and l.user_id = "+user.getEntityId()+"";
			query += " and u.id = l.user_id and ld.product_id IN ( select products_id  from user_product  where User_id = "+user.getEntityId()+" )";
		} else if(user.getEntityName().equals("RSM") || user.getEntityName().equals("TSR") || user.getEntityName().equals("Sales Consultant") || user.getEntityName().equals("Sales Executive")){
			select = " u.name as userName,";
			from = ", user as u";
			query += " and u.id = l.user_id and ld.product_id IN ( select products_id  from user_product  where User_id = "+user.getEntityId()+" )";
			userQuery = "and (l.user_id = "+user.getEntityId()+"";
			userQuery += " or ld.pinCode IN (Select uz.zipCodes_id from user_zipcode uz where uz.user_id = "+user.getEntityId()+" ) )";
		} else if(user.getEntityName().equals("ZSM") || user.getEntityName().equals("Sellout Manager")){
			select = " u.name as userName,";
			from = ", user as u";
			query += " and u.id = l.user_id";
			userQuery = " and l.user_id = "+user.getEntityId()+"";
			//userQuery += " or l.zone = (Select user.zone from user where user.id = "+user.getEntityId()+" ) )";
		} else {
			userQuery = "";
			if(user.getEntityName().equals("Category Manager") || user.getEntityName().equals("Sellout-Regional")){
				select = " u.name as userName,";
				from = ", user as u";
				query += " and u.id = l.user_id and ld.product_id IN ( select products_id  from user_product  where User_id = "+user.getEntityId()+" )";
			}
		}
		
		sql = "Select ld.sr as srNo, ld.name as name,  ld.lms as lms,"+select
				+" l.id as id,ld.email as email, ld.contactNo as contactNo,"
				+" p.name as product,p.id as productId,ld.state as state,l.disposition1 as dispo1,"
				+" l.disposition2 as dispo2,l.disposition3 as dispo3,l.status as status,l.followUpDate as date,"
				+" ld.city as city,ld.area as area "
				+" FROM lead as l, leaddetails as ld, product as p "+from+" where p.id = ld.product_id"
				+" and ld.id = l.leadDetails_id "+query+userQuery+date;
		if(query.contains("l.user_id is null")){
			sql = "Select ld.sr as srNo, ld.name as name,  ld.lms as lms,"+select
					+" l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+" p.name as product,p.id as productId,ld.state as state,l.disposition1 as dispo1,"
					+" l.disposition2 as dispo2,l.disposition3 as dispo3,l.status as status,l.followUpDate as date,"
					+" ld.city as city,ld.area as area "
					+" FROM lead as l, leaddetails as ld, product as p "+from+" where p.id = ld.product_id"
					+" and ld.id = l.leadDetails_id "+query+userQuery+date;
		
		}
		System.out.println("SQL : " + sql);
		
		List<Map<String, Object>> rows = jt.queryForList(sql);
		for(Map map : rows) {
			//vms.add(new LeadDetailsVM(map));
			BuildinLeadDetailsVM leadVM = new BuildinLeadDetailsVM();
			leadVM.id = (Long) map.get("id");
			leadVM.leadNumber =  (Long) map.get("srNo");
			leadVM.contactNumber = (Long) map.get("contactNo");
			leadVM.contactName = (String) map.get("name");
			leadVM.email = (String) map.get("email");
			leadVM.product = (String) map.get("product");
			leadVM.productId = ((Long) map.get("productId")).toString();
			leadVM.state = (String) map.get("state");
			leadVM.disposition1 = (String) map.get("dispo1");
			leadVM.disposition2 = (String) map.get("dispo2");
			leadVM.disposition3 = (String) map.get("dispo3");
			leadVM.status = (String) map.get("status");
			leadVM.followUpDate = (Date) map.get("date");
			leadVM.lms = (String) map.get("lms");
			leadVM.userName = (String) map.get("userName");
			
			vms.add(leadVM);
		}
		return vms;
	}



	public Map getNewLeadData() {
		AuthUser user = Utils.getLoggedInUser();
		Map<String, List<ZoneVM>> map = new HashMap<>();
		map.put("productList", getProductList());
		map.put("stateList", dealerService.getStates());
		map.put("cityList", dealerService.getDistricts());
		return map;
	}

	public List<ReassignUserVM> getReassignList() {
		AuthUser user = Utils.getLoggedInUser();
		List<ReassignUserVM> dealerList = new ArrayList<ReassignUserVM>();
		String usersql = "";
		if(user.getEntityName().equals("Sellout Manager") || user.getEntityName().equals("RSM") || user.getEntityName().equals("TSR") || user.getEntityName().equals("Sellout-Regional")){
			usersql = "Select * from user where( user.id In (Select DISTINCT(a.User_id) from user_zipcode as a, user_zipcode as b where"
					+" a.zipCodes_id = b.zipCodes_id and b.User_id = "+user.getEntityId()+")" 
					+" and user.entityName In ('RSM', 'TSR', 'Dealer', 'Sales Consultant', 'Sales Executive'))"
					+" or ( user.zone = (select user.zone  from user where user.id = "+user.getEntityId()+")  and user.entityName In ('ZSM', 'Sellout Manager'))";
		} 
		if(user.getEntityName().equals("Dealer") || user.getEntityName().equals("Sales Executive")){
			ReassignUserVM vm = new ReassignUserVM();
			vm.setEntity(user.getEntityName());
			vm.setId(user.getEntityId());
			vm.setName(user.getEntityName());
			vm.setEmail(user.getEmail());
			dealerList.add(vm);
			return dealerList;
		} 
		System.out.println("Ress : " + usersql);
		String address = "";
		List<Map<String, Object>> rows = jt.queryForList(usersql);
		for(Map map : rows) {
			ReassignUserVM vm = new ReassignUserVM();
			vm.id = (Long) map.get("id");
			if(((String) map.get("entityName")).equals("Dealer")){
				address = (String) map.get("address");
				if(address != null){
					address = address.length() > 25 ? address.substring(0,25) : address;
					vm.name = (String) map.get("name") +" : " +  address;
				}else{
					address = "";
					vm.name = (String) map.get("name") +" : " +  address;
				}
			} else {
				vm.name = (String) map.get("name") +" : (" + (String) map.get("entityName")+")";
			}
			vm.entity = (String) map.get("entityName");
			vm.email = (String) map.get("email");
			dealerList.add(vm);
		}
		return dealerList;
	}
	
	

	private List<ZoneVM> getProductList() {
		String sql = "select * from product";
		List<Map<String, Object>> rows = jt.queryForList(sql);
		List<ZoneVM> productList = new ArrayList<ZoneVM>();
		for(Map map : rows) {
			ZoneVM vm = new ZoneVM();
			vm.id = (Long) map.get("id");
			vm.name = (String) map.get("name");
			productList.add(vm);
		}
		return productList;
	}



	public void createLead(LeadVM vm) {
		System.out.println("LEAD NAME"+vm.name);
		Session session = sessionFactory.getCurrentSession();
		LeadDetails details = new LeadDetails();
		details.setAreaofInterest1(vm.getAreaofInterest1());
		details.setAreaofInterest2(vm.getAreaofInterest2());
		details.setCampaignName(vm.getCampaignName());
		details.setCategorization(vm.getCategorization());
		details.setCity(vm.getCity());
		details.setContactNo(vm.getContactNo());
		details.setEmail(vm.getEmail());
		details.setName(vm.getName());
		Product product = (Product)session.get(Product.class, Long.parseLong(vm.getProduct()));
		details.setProduct(product);
		details.setRemarks1(vm.getRemarks1());
		details.setUploadDate(new Date());
		details.setState(vm.getState());
		details.setSr(vm.getLeadNumber());
		details.setSrNo(vm.getLeadNumber());
		details.setPinCode(vm.getPinCode());
		details.setType(vm.getType());
		session.save(details);

		Lead lead = new Lead();
		User user = (User) session.get(User.class, vm.getDealer());
		lead.setUser(user);
		lead.setZone(user.getZone());
		lead.setDisposition1("New");
		lead.setLeadDetails(details);
		lead.setOrigin("Walk-In");
		lead.setStatus("Open");
		lead.setLastDispo1ModifiedDate(new Date());
		lead.setUploadDate(new Date());
		session.save(lead);

		ActivityStream activityStream = new ActivityStream();
		activityStream.setNewDisposition1("New");
		activityStream.setLead(lead);
		activityStream.setCreatedDate(new Date());
		session.save(activityStream);
		session.flush();

		LeadAgeing ageing = new LeadAgeing();
		ageing.setAgeing(0L);
		ageing.setStatus("New");
		//ageing.setZone(lead.getZone());
		ageing.setProduct(product.getName());
		//ageing.setDealer_id(lead.getId());
		ageing.setLead_id(lead.getId());
		session.save(ageing);
	}
	
	
	public void createBuildinLead(LeadVM vm) {
		System.out.println("LEAD NAME"+vm.name);
		AuthUser authUser = Utils.getLoggedInUser();
		Session session = sessionFactory.getCurrentSession();
		LeadDetails details = new LeadDetails();
		
		
		details.setCity(vm.getCity());
		details.setContactNo(vm.getContactNo());
		details.setEmail(vm.getEmail());
		details.setName(vm.getName());
		
		Product product = (Product)session.get(Product.class, Long.parseLong(vm.getProduct()));
		details.setProduct(product);
		
		details.setUploadDate(new Date());
		details.setState(vm.getState());
		details.setSr(vm.getLeadNumber());
		details.setSrNo(vm.getLeadNumber());
		details.setArea(vm.getArea());
		details.setKitchenmake(vm.getKitchenmake());
		details.setSource(vm.getSource());
		details.setBudget(vm.getBudget());
		details.setPurchase(vm.getPurchase());
		details.setLms(vm.getLms());
		session.save(details);

		Lead lead = new Lead();
		System.out.println("EntityID :: " + authUser.getEntityId());
		User user = (User) session.get(User.class, authUser.getEntityId());
		lead.setUser(user);
		lead.setZone(user.getZone());
		
		lead.setDisposition1("New");
		lead.setLeadDetails(details);
		lead.setOrigin("Built-In");
		lead.setStatus("Open");
		lead.setLastDispo1ModifiedDate(new Date());
		lead.setUploadDate(new Date());
		session.save(lead);

		ActivityStream activityStream = new ActivityStream();
		activityStream.setNewDisposition1("New");
		activityStream.setLead(lead);
		activityStream.setCreatedDate(new Date());
		session.save(activityStream);
		session.flush();

		LeadAgeing ageing = new LeadAgeing();
		ageing.setAgeing(0L);
		ageing.setStatus("New");
		//ageing.setZone(lead.getZone());
		ageing.setProduct(product.getName());
		//ageing.setDealer_id(lead.getId());
		ageing.setLead_id(lead.getId());
		session.save(ageing);
	}



	public void reassignDealers(ReassignUserVM reassign, List<Long> ids) {
		System.out.println(" Reassign ID : " + reassign.getId());
		String hql = "";
		if(reassign.getEntity().equals("Sales Executive") || reassign.getEntity().equals("Sales Consultant") || reassign.getEntity().equals("Dealer") || reassign.getEntity().equals("RSM") || reassign.getEntity().equals("TSR")){
			hql = "UPDATE lead SET lead.user_id = "+reassign.getId()+" where lead.id IN(:ids)";
		}
		else {
			return ;
		}
		Map<String, List<Long>> param = Collections.singletonMap("ids",ids); 
		NamedParameterJdbcTemplate  namedParameterJdbcTemplate = new  
				NamedParameterJdbcTemplate(jt.getDataSource());
		
		namedParameterJdbcTemplate.update(hql, param);
		
		
		/*Query query = sessionFactory.getCurrentSession().createQuery("FROM Lead where id IN (:ids)");
		query.setParameterList("ids", ids);
		List<Lead> lead = query.list();
		for(Lead l : lead){
			mailService.sendReassignMail(reassign.getEmail(),l);
		}*/
		String str = "";
		for(Long lg : ids){
			if(str.isEmpty()){
				str = lg.toString();
			}
			else{
				str+= ","+lg.toString();
			}
		}
		//List<Map<String, Object>> rows = jt.queryForList("select ld.id,ld.name,ld.city from lead as l,leaddetails as ld where l.leadDetails_id=ld.id and l.id IN("+ str +")");
		List<Map<String, Object>> rows = jt.queryForList("select ld.name,ld.contactNo as number,ld.lms as brand,p.name as productName from lead as l,leaddetails as ld,product as p where ld.product_id = p.id and l.leadDetails_id=ld.id and l.id IN("+ str +")");
		for(Map map : rows){
			mailService.sendReassignMail(reassign.getEmail(),map);
		}
		
		
		
		
		/*System.out.println("IDS : " + str);
		List<LeadDetails> productList = new ArrayList<LeadDetails>();
		String lms = "Bosch";
		String productName = "Washing Machine";
		String roleName = "ZSM";
		String sql = "SELECT " +
		"L.`status` as 'Status', " +
		"LD.state as 'State', " +
		"count(*) AS 'Count' " +
		"FROM lead L,leaddetails LD , product P, roles R, userrole UR, user_zipcode UZ " +
		"WHERE L.leadDetails_id=LD.id AND P.id = LD.product_id AND " +
		//"L.lastDispo1ModifiedDate >= ? AND " +
		//"L.lastDispo1ModifiedDate <= ? AND " +
		"LD.lms = '" + lms + "' AND " +
		"P.name = '" + productName + "' AND " +
		"R.name = '" + roleName + "' AND " +
		"LD.pinCode = UZ.zipCodes_id AND " +
		"UZ.User_id = UR.user_id AND " +
		"UR.role_id = R.role_id " +
		"group by status, state " +
		"order by state";
		
		
		List<Map<String, Object>> rows = jt.queryForList(sql);
		Map<String, Map<String,String>> stateMap = new HashMap<String, Map<String,String>>();
		for(Map map : rows){
			String state = map.get("State").toString();
			System.out.println("State : " + state);
			Map<String,String> metric = stateMap.get(state);
			if(metric == null) {
				metric = new HashMap<String,String>();
				System.out.println("Status : " + map.get("Status").toString());
				System.out.println("Count : " + map.get("Count").toString());
				if (map.get("Count").toString().equals(""))
					{
					metric.put(map.get("Status").toString(), "0");
				} else{
				metric.put(map.get("Status").toString(), map.get("Count").toString());
				}
			} else {
				System.out.println("Status 1 : " + map.get("Status").toString());
				System.out.println("Count 1 : " + map.get("Count").toString());
				if (map.get("Count").toString().equals(""))
				{
				metric.put(map.get("Status").toString(), "0");
				} else{
					metric.put(map.get("Status").toString(), map.get("Count").toString());
				}
				//metric.put(map.get("Status").toString(), map.get("Count").toString());
			}
			stateMap.put(state, metric);
		}
		mailService.sendReassignData(reassign.getEmail(), stateMap);*/
		
		
		System.out.println(" Reassign Email : " + reassign.getEmail());
	}

	private Date getDate(Date end){
		Calendar cal = Calendar.getInstance();
		cal.setTime(end);
		cal.add(Calendar.DATE, 1);
		return cal.getTime();
	}
	
	public List<ZoneVM> getBrands() {
		List<Map<String, Object>> rows = jt.queryForList("Select DISTINCT(name) as name from brand");
		
		List<ZoneVM> branList = new ArrayList<ZoneVM>();
		for(Map map : rows) {
			ZoneVM vm = new ZoneVM();
			vm.name = (String) map.get("name");
			branList.add(vm);
		}
		return branList;
	}
	
	public List<ZoneVM> getModalNumbers(String brand) {
		List<Map<String, Object>> rows = jt.queryForList("Select item as name from modal_no where modal_no.division = '"+brand+"'");
		List<ZoneVM> branList = new ArrayList<ZoneVM>();
		for(Map map : rows) {
			ZoneVM vm = new ZoneVM();
			vm.name = (String) map.get("name");
			branList.add(vm);
		}
		return branList;
	}
}
