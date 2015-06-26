package com.mnt.businessApp.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.soap.Detail;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.mnt.businessApp.viewmodel.ReassignUserVM;
import com.mnt.businessApp.viewmodel.LeadDetailsVM;
import com.mnt.businessApp.viewmodel.LeadHistoryVM;
import com.mnt.businessApp.viewmodel.LeadVM;
import com.mnt.businessApp.viewmodel.ProductVM;
import com.mnt.businessApp.viewmodel.ZoneVM;
import com.mnt.entities.authentication.AuthUser;
import com.mnt.entities.businessApp.ActivityStream;
import com.mnt.entities.businessApp.Dealer;
import com.mnt.entities.businessApp.Lead;
import com.mnt.entities.businessApp.LeadAgeing;
import com.mnt.entities.businessApp.LeadDetails;
import com.mnt.entities.businessApp.Product;

@Service
public class LeadService {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private DealerService dealerService;

	@Autowired
	private JdbcTemplate jt;

	public List<LeadDetailsVM> getAllLeadDetails() {
		AuthUser user = Utils.getLoggedInUser();
		List<LeadDetailsVM> vms = new ArrayList<LeadDetailsVM>();
		String sql = "";
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		String userSql = "";
		if(user.getEntityName().equals("Dealer")){
			sql =  "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,ld.state as state,l.disposition1 as dispo1,"
					+ "l.disposition2 as dispo2,l.followUpDate as date ,d.dealerName as dealerName "
					+ "FROM lead as l, leaddetails as ld, dealer as d, product as p  where p.id = ld.product_id and d.id = l.dealer_id and "
					+ "ld.id = l.leadDetails_id and dealer_id = ?";
		}  
		else if(user.getEntityName().equals("RSM")  || user.getEntityName().equals("TSR")){
			sql = "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,ld.state as state,l.disposition1 as dispo1,"
					+ "l.disposition2 as dispo2,l.followUpDate as date ,d.dealerName as dealerName "
					+ "FROM lead as l, leaddetails as ld, dealer as d, product as p  where p.id = ld.product_id and d.id = l.dealer_id and "
					+ " ld.id = l.leadDetails_id and dealer_id IN ( SELECT du.dealer_id from dealer_user as du where du.user_id = ? )"
					+ " and ld.product_id IN ( select products_id  from user_product  where User_id = "+user.getEntityId()+" )";
			userSql = "Select ld.sr as srNo, ld.name as name, "
					+" l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+" ld.pinCode as pincode,p.name as product,ld.state as state,l.disposition1 as dispo1,"
					+" l.disposition2 as dispo2,l.followUpDate as date , u.name as dealerName"
					+" FROM lead as l, leaddetails as ld, product as p,  user as u where p.id = ld.product_id and "
					+" ld.id = l.leadDetails_id and l.user_id = u.id and l.user_id = ? and l.dealer_id IS NULL";
			rows = jt.queryForList(userSql,new Object[] {user.getEntityId()});
			for(Map map : rows) {
				vms.add(new LeadDetailsVM(map));
			}
			
		}
		else if(user.getEntityName().equals("ZSM") || user.getEntityName().equals("Sellout Manager")){
			sql = "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,ld.state as state,l.disposition1 as dispo1,"
					+ "l.disposition2 as dispo2,l.followUpDate as date ,d.dealerName as dealerName "
					+ "FROM lead as l, leaddetails as ld, dealer as d, product as p  where p.id = ld.product_id and d.id = l.dealer_id and"
					+ " ld.id = l.leadDetails_id and dealer_id IN ( select id  from dealer as d where state = "
					+ "(Select state.name from user , state WHERE user.id = ? and state.id = user.state_id))";
			userSql = "Select ld.sr as srNo, ld.name as name, "
					+" l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+" ld.pinCode as pincode,p.name as product,ld.state as state,l.disposition1 as dispo1,"
					+" l.disposition2 as dispo2,l.followUpDate as date , u.name as dealerName"
					+" FROM lead as l, leaddetails as ld, product as p,  user as u where p.id = ld.product_id and "
					+" ld.id = l.leadDetails_id and l.user_id = u.id and l.user_id IN  ( select id  from user as u where u.state_id = "
					+ "(Select user.state_id from user WHERE user.id = ? )) and l.dealer_id IS NULL";
			rows = jt.queryForList(userSql,new Object[] {user.getEntityId()});
			for(Map map : rows) {
				vms.add(new LeadDetailsVM(map));
			}
		}
		else if(user.getEntityName().equals("Category Manager") || user.getEntityName().equals("Sellout-Regional")){
			sql = "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,ld.state as state,l.disposition1 as dispo1,"
					+ "l.disposition2 as dispo2,l.followUpDate as date ,d.dealerName as dealerName "
					+ "FROM lead as l, leaddetails as ld, dealer as d, product as p  where p.id = ld.product_id and d.id = l.dealer_id and"
					+ " ld.id = l.leadDetails_id and ld.product_id IN ( select products_id  from user_product  where User_id = ? )";
		}
		else if(user.getEntityName().equals("Admin") || user.getEntityName().equals("CEO") || user.getEntityName().equals("General Manager")){
			sql = "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,ld.state as state,l.disposition1 as dispo1,"
					+ "l.disposition2 as dispo2,l.followUpDate as date ,d.dealerName as dealerName "
					+ "FROM lead as l, leaddetails as ld, dealer as d, product as p  where p.id = ld.product_id and d.id = l.dealer_id and"
					+ " ld.id = l.leadDetails_id";
			rows = jt.queryForList(sql);
			for(Map map : rows) {
				vms.add(new LeadDetailsVM(map));
			}
			return vms;
		}
		else if(user.getEntityName().equals("Sales Consultant")){
			sql = "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,ld.state as state,l.disposition1 as dispo1,"
					+ "l.disposition2 as dispo2,l.followUpDate as date ,d.dealerName as dealerName "
					+ "FROM lead as l, leaddetails as ld, dealer as d, product as p  where p.id = ld.product_id and d.id = l.dealer_id and "
					+ " ld.id = l.leadDetails_id and dealer_id IN ( SELECT du.dealer_id from dealer_user as du where du.user_id = ? )";			
		}
		else {
			System.out.println("Unknown Role");
		}
		System.out.println(sql);
		rows = jt.queryForList(sql,new Object[] {user.getEntityId()});
		for(Map map : rows) {
			vms.add(new LeadDetailsVM(map));
		}
		System.out.println(sql);
		return vms;
	}

	

	public LeadVM getLeadVMById(Long id) {
		Lead lead = getLeadById(id);
		LeadVM leadVM = new LeadVM(lead); 
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
		ActivityStream activityStream = new ActivityStream();
		activityStream.setNewDisposition1(vm.getDisposition1());
		activityStream.setNewDisposition2(vm.getDisposition2());
		activityStream.setOldDisposition1(lead.getDisposition1());
		activityStream.setOldDisposition2(lead.getDisposition2());
		activityStream.setLead(lead);
		activityStream.setReason(vm.getReason());
		activityStream.setCreatedDate(new Date());
		sessionFactory.getCurrentSession().save(activityStream);
		if(lead.getDisposition2() == null || (!lead.getDisposition2().equals("Won") && !lead.getDisposition2().equals("Lost"))){
			LeadAgeing ageing = getLeadAgeing(lead.getId(), vm.getDisposition1());
			long secs = (new Date().getTime() - lead.getLastDispo1ModifiedDate().getTime()) / 1000;
			Integer hours = (int) (secs / 3600);    
			ageing.setAgeing(ageing.getAgeing() + hours);
			ageing.setProduct(lead.getLeadDetails().getProduct().getName());
			if(lead.getDealer() != null){
				ageing.setZone(lead.getDealer().getZone());
				ageing.setDealer_id(lead.getDealer().getId());
			}
			sessionFactory.getCurrentSession().update(ageing);
			if(vm.getDisposition2().equals("Won") || vm.getDisposition2().equals("Lost")){
				ageing = getLeadAgeing(lead.getId(), vm.getDisposition2());
				secs = (new Date().getTime() - lead.getUploadDate().getTime() ) / 1000;
				hours = (int) (secs / 3600);  
				ageing.setAgeing(Long.valueOf(hours));
				ageing.setProduct(lead.getLeadDetails().getProduct().getName());
				if(lead.getDealer() != null){
					ageing.setZone(lead.getDealer().getZone());
					ageing.setDealer_id(lead.getDealer().getId());
				}
				sessionFactory.getCurrentSession().update(ageing);
			}
		}
		lead.setDisposition1(vm.getDisposition1());
		lead.setDisposition2(vm.getDisposition2());
		lead.setFollowUpDate(vm.getFollowUpDate());
		lead.setReason(vm.getReason());
		lead.addActivityStream(activityStream);
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

	public List<LeadDetailsVM> getAllEscalatedLeadDetails() {
		AuthUser user = Utils.getLoggedInUser();
		List<LeadDetailsVM> vms = new ArrayList<LeadDetailsVM>();
		String sql = "";
		String userSql = "";
		int escalatedLevel = 0;
		if(user.getEntityName().equals("ZSM")) escalatedLevel = 3;
		if(user.getEntityName().equals("Sellout Manager")) escalatedLevel = 2;
		if(user.getEntityName().equals("TSR")) escalatedLevel = 1;
		if(user.getEntityName().equals("Dealer") ){
			sql =  "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,ld.state as state,ld.state as state,l.disposition1 as dispo1,"
					+ "l.disposition2 as dispo2,l.followUpDate as date ,d.dealerName as dealerName "
					+ "FROM lead as l, leaddetails as ld, dealer as d, product as p  where p.id = ld.product_id and d.id = l.dealer_id and disposition1 = 'Escalated'  "
					+ "and ld.id = l.leadDetails_id and dealer_id = ?";
		}  
		else if(user.getEntityName().equals("RSM")  || user.getEntityName().equals("TSR")){
			sql = "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,ld.state as state,l.disposition1 as dispo1,"
					+ "l.disposition2 as dispo2,l.followUpDate as date ,d.dealerName as dealerName "
					+ "FROM lead as l, leaddetails as ld, dealer as d, product as p  where p.id = ld.product_id and"
					+ " d.id = l.dealer_id and disposition1 = 'Escalated' "
					+ "and ld.id = l.leadDetails_id and dealer_id IN ( SELECT du.dealer_id from dealer_user as du where du.user_id = ? )"
					+ " and ld.product_id IN ( select products_id  from user_product  where User_id = "+user.getEntityId()+" )";
			userSql = "Select ld.sr as srNo, ld.name as name, "
					+" l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+" ld.pinCode as pincode,p.name as product,ld.state as state,l.disposition1 as dispo1,"
					+" l.disposition2 as dispo2,l.followUpDate as date , u.name as dealerName"
					+" FROM lead as l, leaddetails as ld, product as p,  user as u where p.id = ld.product_id and "
					+"  disposition1 = 'Escalated' and"
					+" ld.id = l.leadDetails_id and l.user_id = u.id and l.user_id = ? and l.dealer_id IS NULL";
			List<Map<String, Object>> rows = jt.queryForList(userSql,new Object[] {user.getEntityId()});
			for(Map map : rows) {
				vms.add(new LeadDetailsVM(map));
			}
		}
		else if(user.getEntityName().equals("ZSM") || user.getEntityName().equals("Sellout Manager")){
			sql = "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,ld.state as state,l.disposition1 as dispo1,"
					+ "l.disposition2 as dispo2,l.followUpDate as date ,d.dealerName as dealerName "
					+ "FROM lead as l, leaddetails as ld, dealer as d, product as p  where p.id = ld.product_id and"
					+ " d.id = l.dealer_id and"
					+ " disposition1 = 'Escalated' and l.escalatedLevel = " + escalatedLevel 
					+ " and ld.id = l.leadDetails_id and dealer_id IN ( select id  from dealer where zone = (Select zone.name from user,zone WHERE user.id = ? and zone.id = user.zone_id) )";
			userSql = "Select ld.sr as srNo, ld.name as name, "
					+" l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+" ld.pinCode as pincode,p.name as product,ld.state as state,l.disposition1 as dispo1,"
					+" l.disposition2 as dispo2,l.followUpDate as date , u.name as dealerName"
					+" FROM lead as l, leaddetails as ld, product as p,  user as u where p.id = ld.product_id and "
					+ " disposition1 = 'Escalated' and l.escalatedLevel = " + escalatedLevel 
					+" and ld.id = l.leadDetails_id and l.user_id = u.id and l.user_id IN  ( select id  from user as u where u.state_id = "
					+ "(Select user.state_id from user WHERE user.id = ? )) and l.dealer_id IS NULL";
			List<Map<String, Object>> rows = jt.queryForList(userSql,new Object[] {user.getEntityId()});
			for(Map map : rows) {
				vms.add(new LeadDetailsVM(map));
			}
		}
		else if(user.getEntityName().equals("Category Manager") || user.getEntityName().equals("Sellout-Regional")){
			sql = "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,ld.state as state,l.disposition1 as dispo1,"
					+ "l.disposition2 as dispo2,l.followUpDate as date ,d.dealerName as dealerName "
					+ "FROM lead as l, leaddetails as ld, dealer as d, product as p  where p.id = ld.product_id and"
					+ " d.id = l.dealer_id and"
					+ " disposition1 = 'Escalated' "
					+ " and ld.id = l.leadDetails_id"
					+ " and  ld.product_id IN ( select products_id  from user_product  where User_id = ? )";
		}
		else if(user.getEntityName().equals("Admin") || user.getEntityName().equals("CEO") || user.getEntityName().equals("General Manager")){
			sql = "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,ld.state as state,l.disposition1 as dispo1,"
					+ "l.disposition2 as dispo2,l.followUpDate as date ,d.dealerName as dealerName "
					+ "FROM lead as l, leaddetails as ld, dealer as d, product as p  where p.id = ld.product_id and"
					+ " d.id = l.dealer_id and"
					+ " disposition1 = 'Escalated' "
					+ " and ld.id = l.leadDetails_id";
			List<Map<String, Object>> rows = jt.queryForList(sql);
			for(Map map : rows) {
				vms.add(new LeadDetailsVM(map));
			}
			System.out.println(sql);
			return vms;
		}
		else if(user.getEntityName().equals("Sales Consultant")){
			sql = "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,ld.state as state,l.disposition1 as dispo1,"
					+ "l.disposition2 as dispo2,l.followUpDate as date ,d.dealerName as dealerName "
					+ "FROM lead as l, leaddetails as ld, dealer as d, product as p  where p.id = ld.product_id and"
					+ " d.id = l.dealer_id and disposition1 = 'Escalated' "
					+ "and ld.id = l.leadDetails_id and dealer_id IN ( SELECT du.dealer_id from dealer_user as du where du.user_id = ? )";			
		}
		List<Map<String, Object>> rows = jt.queryForList(sql,new Object[] {user.getEntityId()});
		for(Map map : rows) {
			vms.add(new LeadDetailsVM(map));
		}
		System.out.println(sql);
		return vms;
	}

	public List<LeadDetailsVM> getFollowUpLeads() {
		AuthUser user = Utils.getLoggedInUser();
		List<LeadDetailsVM> vms = new ArrayList<LeadDetailsVM>();
		String sql = "";
		String userSql = "";
		
		if(user.getEntityName().equals("Dealer")){
			sql =  "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,ld.state as state,l.disposition1 as dispo1,"
					+ "l.disposition2 as dispo2,l.followUpDate as date ,d.dealerName as dealerName "
					+ "FROM lead as l, leaddetails as ld, dealer as d, product as p  where p.id = ld.product_id and"
					+ " d.id = l.dealer_id and l.followUpDate IS NOT NULL "
					+ "and ld.id = l.leadDetails_id and dealer_id = ?";
		}  
		else if(user.getEntityName().equals("RSM")|| user.getEntityName().equals("TSR") ){
			sql = "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,ld.state as state,l.disposition1 as dispo1,"
					+ "l.disposition2 as dispo2,l.followUpDate as date ,d.dealerName as dealerName "
					+ "FROM lead as l, leaddetails as ld, dealer as d, product as p  where p.id = ld.product_id and d.id = l.dealer_id and l.followUpDate IS NOT NULL "
					+ "and ld.id = l.leadDetails_id and dealer_id IN ( SELECT du.dealer_id from dealer_user as du where du.user_id = ? )"
					+ "and ld.product_id IN ( select products_id  from user_product  where User_id = "+user.getEntityId()+" )";
			userSql = "Select ld.sr as srNo, ld.name as name, "
					+" l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+" ld.pinCode as pincode,p.name as product,ld.state as state,l.disposition1 as dispo1,"
					+" l.disposition2 as dispo2,l.followUpDate as date , u.name as dealerName"
					+" FROM lead as l, leaddetails as ld, product as p,  user as u where p.id = ld.product_id and "
					+" and l.followUpDate IS NOT NULL "
					+" ld.id = l.leadDetails_id and l.user_id = u.id and l.user_id = ? and l.dealer_id IS NULL";
			List<Map<String, Object>> rows = jt.queryForList(userSql,new Object[] {user.getEntityId()});
			for(Map map : rows) {
				vms.add(new LeadDetailsVM(map));
			}
		}
		else if(user.getEntityName().equals("ZSM") || user.getEntityName().equals("Sellout Manager")){
			sql = "Select ld.sr as srNo, "
					+ "ld.name as name, "
					+ "l.id as id,ld.email as email, "
					+ "ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,"
					+ "p.name as product,"
					+ "ld.state as state,"
					+ "l.disposition1 as dispo1,"
					+ "l.disposition2 as dispo2,"
					+ "l.followUpDate as date ,"
					+ "d.dealerName as dealerName "
					+ "FROM lead as l, leaddetails as ld, dealer as d, product as p"
					+ " where p.id = ld.product_id"
					+ " and d.id = l.dealer_id"
					+ " and l.followUpDate IS NOT NULL "
					+ " and ld.id = l.leadDetails_id"
					+ " and dealer_id IN ("
					+ " select id  from dealer"
					+ " where zone = (Select zone.name from user,zone WHERE user.id = ? and zone.id = user.zone_id))";
			userSql = "Select ld.sr as srNo, ld.name as name, "
					+" l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+" ld.pinCode as pincode,p.name as product,ld.state as state,l.disposition1 as dispo1,"
					+" l.disposition2 as dispo2,l.followUpDate as date , u.name as dealerName"
					+" FROM lead as l, leaddetails as ld, product as p,  user as u where p.id = ld.product_id and "
					+" and l.followUpDate IS NOT NULL "
					+" and ld.id = l.leadDetails_id and l.user_id = u.id and l.user_id IN  ( select id  from user as u where u.state_id = "
					+ "(Select user.state_id from user WHERE user.id = ? )) and l.dealer_id IS NULL";
			List<Map<String, Object>> rows = jt.queryForList(userSql,new Object[] {user.getEntityId()});
			for(Map map : rows) {
				vms.add(new LeadDetailsVM(map));
			}
		}
		else if(user.getEntityName().equals("Category Manager") || user.getEntityName().equals("Sellout-Regional")){
			sql = "Select ld.sr as srNo, "
					+ "ld.name as name, "
					+ "l.id as id,ld.email as email, "
					+ "ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,"
					+ "p.name as product,"
					+ "ld.state as state,"
					+ "l.disposition1 as dispo1,"
					+ "l.disposition2 as dispo2,"
					+ "l.followUpDate as date ,"
					+ "d.dealerName as dealerName "
					+ "FROM lead as l, leaddetails as ld, dealer as d, product as p"
					+ " where p.id = ld.product_id"
					+ " and d.id = l.dealer_id"
					+ " and l.followUpDate IS NOT NULL "
					+ " and ld.id = l.leadDetails_id"
					+ " and ld.product_id IN ( select products_id  from user_product  where User_id = ? )";
		}
		else if(user.getEntityName().equals("Admin") || user.getEntityName().equals("CEO") || user.getEntityName().equals("General Manager")){
			sql = "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,ld.state as state,l.disposition1 as dispo1,"
					+ "l.disposition2 as dispo2,l.followUpDate as date ,d.dealerName as dealerName "
					+ "FROM lead as l, leaddetails as ld, dealer as d, product as p  where p.id = ld.product_id and d.id = l.dealer_id and l.followUpDate IS NOT NULL "
					+ "and ld.id = l.leadDetails_id";
			List<Map<String, Object>> rows = jt.queryForList(sql);
			for(Map map : rows) {
				vms.add(new LeadDetailsVM(map));
			}
						System.out.println(sql);
			return vms;
		}
		else if(user.getEntityName().equals("Sales Consultant")){
			sql = "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,ld.state as state,l.disposition1 as dispo1,"
					+ "l.disposition2 as dispo2,l.followUpDate as date ,d.dealerName as dealerName "
					+ "FROM lead as l, leaddetails as ld, dealer as d, product as p  where p.id = ld.product_id and d.id = l.dealer_id and l.followUpDate IS NOT NULL "
					+ "and ld.id = l.leadDetails_id and dealer_id IN ( SELECT du.dealer_id from dealer_user as du where du.user_id = ? )";			
		}
		List<Map<String, Object>> rows = jt.queryForList(sql,new Object[] {user.getEntityId()});
		for(Map map : rows) {
			vms.add(new LeadDetailsVM(map));
		}
		System.out.println(sql);
		return vms;
		
	}

	public List<LeadDetailsVM> getOpenLeads() {
		AuthUser user = Utils.getLoggedInUser();
		List<LeadDetailsVM> vms = new ArrayList<LeadDetailsVM>();
		String proZone = "" ;
		if(user.getEntityName().equals("Dealer")){
			proZone =  " and dealer_id = ?";
		}  
		if(user.getEntityName().equals("RSM") || user.getEntityName().equals("TSR")  || user.getEntityName().equals("Sales Consultant")){
			proZone = " and dealer_id IN ( SELECT du.dealer_id from dealer_user as du where du.user_id = ? )";
		}
		if(user.getEntityName().equals("ZSM") || user.getEntityName().equals("Sellout Manager")){
			proZone = " and dealer_id IN ( select id  from dealer as d where zone = (Select zone.name from user,zone WHERE user.id = ? and zone.id = user.zone_id))";
		}
		if(user.getEntityName().equals("Category Manager") || user.getEntityName().equals("Sellout-Regional")){
			proZone = " and ld.product_id IN ( select products_id  from user_product  where User_id = ? )";
		}
		if(user.getEntityName().equals("Admin") || user.getEntityName().equals("CEO") || user.getEntityName().equals("General Manager")){
			String sql = "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,ld.state as state,l.disposition1 as dispo1,"
					+ "l.disposition2 as dispo2,l.followUpDate as date ,d.dealerName as dealerName "
					+ "FROM lead as l, leaddetails as ld, dealer as d, product as p  where p.id = ld.product_id and d.id = l.dealer_id and "
					+ " ld.id = l.leadDetails_id "
					+ " and (l.disposition1 = 'New' or l.disposition2 IN('Call Back','Quote Sent','Visiting Store','Not Contacted'))";
			List<Map<String, Object>> rows = jt.queryForList(sql);
			for(Map map : rows) {
				vms.add(new LeadDetailsVM(map));
			}
			System.out.println(sql);
			return vms;
		}
		
		String sql = "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,ld.state as state,l.disposition1 as dispo1,"
					+ "l.disposition2 as dispo2,l.followUpDate as date ,d.dealerName as dealerName "
					+ "FROM lead as l, leaddetails as ld, dealer as d, product as p  where p.id = ld.product_id and d.id = l.dealer_id and "
					+ " ld.id = l.leadDetails_id "
					+ " and (l.disposition1 = 'New' or l.disposition2 IN('Call Back','Quote Sent','Visiting Store','Not Contacted'))"
					+ proZone;
		List<Map<String, Object>> rows = jt.queryForList(sql,new Object[] {user.getEntityId()});
		for(Map map : rows) {
			vms.add(new LeadDetailsVM(map));
		}
		System.out.println(sql);
		return vms;
	}
	
	public List<LeadDetailsVM> getWonLeads() {
		AuthUser user = Utils.getLoggedInUser();
		List<LeadDetailsVM> vms = new ArrayList<LeadDetailsVM>();
		String proZone = "" ;
		if(user.getEntityName().equals("Dealer")){
			proZone =  " and dealer_id = ?";
		}  
		if(user.getEntityName().equals("RSM") || user.getEntityName().equals("TSR")  || user.getEntityName().equals("Sales Consultant")){
			proZone = " and dealer_id IN ( SELECT du.dealer_id from dealer_user as du where du.user_id = ? )";
		}
		if(user.getEntityName().equals("ZSM") || user.getEntityName().equals("Sellout Manager")){
			proZone = " and dealer_id IN ( select id  from dealer as d where zone = (Select zone.name from user,zone WHERE user.id = ? and zone.id = user.zone_id))";
		}
		if(user.getEntityName().equals("Category Manager") || user.getEntityName().equals("Sellout-Regional")){
			proZone = " and ld.product_id IN ( select products_id  from user_product  where User_id = ? )";
		}
		if(user.getEntityName().equals("Admin") || user.getEntityName().equals("CEO") || user.getEntityName().equals("General Manager")){
			String sql = "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,ld.state as state,l.disposition1 as dispo1,"
					+ "l.disposition2 as dispo2,l.followUpDate as date ,d.dealerName as dealerName "
					+ "FROM lead as l, leaddetails as ld, dealer as d, product as p  where p.id = ld.product_id and d.id = l.dealer_id and "
					+ " ld.id = l.leadDetails_id "
					+ " and l.disposition2 = 'Won'";
			List<Map<String, Object>> rows = jt.queryForList(sql);
			for(Map map : rows) {
				vms.add(new LeadDetailsVM(map));
			}
			System.out.println(sql);
			return vms;
		}
		String sql = "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,ld.state as state,l.disposition1 as dispo1,"
					+ "l.disposition2 as dispo2,l.followUpDate as date ,d.dealerName as dealerName "
					+ "FROM lead as l, leaddetails as ld, dealer as d, product as p  where p.id = ld.product_id and d.id = l.dealer_id and "
					+ " ld.id = l.leadDetails_id "
					+ " and l.disposition2 = 'Won' "
					+ proZone;
		List<Map<String, Object>> rows = jt.queryForList(sql,new Object[] {user.getEntityId()});
		for(Map map : rows) {
			vms.add(new LeadDetailsVM(map));
		}
		System.out.println(sql);
		return vms;
	}
	
	public List<LeadDetailsVM> getLostLeads() {
		AuthUser user = Utils.getLoggedInUser();
		List<LeadDetailsVM> vms = new ArrayList<LeadDetailsVM>();
		String proZone = "" ;
		if(user.getEntityName().equals("Dealer")){
			proZone =  " and dealer_id = ?";
		}  
		if(user.getEntityName().equals("RSM")  || user.getEntityName().equals("TSR")  || user.getEntityName().equals("Sales Consultant")){
			proZone = " and dealer_id IN ( SELECT du.dealer_id from dealer_user as du where du.user_id = ? )";
		}
		if(user.getEntityName().equals("ZSM") || user.getEntityName().equals("Sellout Manager")){
			proZone = " and dealer_id IN ( select id  from dealer as d where zone = (Select zone.name from user,zone WHERE user.id = ? and zone.id = user.zone_id))";
		}
		if(user.getEntityName().equals("Category Manager") || user.getEntityName().equals("Sellout-Regional")){
			proZone = " and ld.product_id IN ( select products_id  from user_product  where User_id = ? )";
		}
		if(user.getEntityName().equals("Admin") || user.getEntityName().equals("CEO") || user.getEntityName().equals("General Manager")){
			String sql = "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,ld.state as state,l.disposition1 as dispo1,"
					+ "l.disposition2 as dispo2,l.followUpDate as date ,d.dealerName as dealerName "
					+ "FROM lead as l, leaddetails as ld, dealer as d, product as p  where p.id = ld.product_id and d.id = l.dealer_id and "
					+ " ld.id = l.leadDetails_id "
					+ " and l.disposition2 = 'Lost'";
			List<Map<String, Object>> rows = jt.queryForList(sql);
			for(Map map : rows) {
				vms.add(new LeadDetailsVM(map));
			}
			System.out.println(sql);
			return vms;
		}
		String sql = "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,ld.state as state,l.disposition1 as dispo1,"
					+ "l.disposition2 as dispo2,l.followUpDate as date ,d.dealerName as dealerName "
					+ "FROM lead as l, leaddetails as ld, dealer as d, product as p  where p.id = ld.product_id and d.id = l.dealer_id and "
					+ " ld.id = l.leadDetails_id "
					+ " and l.disposition2 = 'Lost' "
					+ proZone;
		List<Map<String, Object>> rows = jt.queryForList(sql,new Object[] {user.getEntityId()});
		for(Map map : rows) {
			vms.add(new LeadDetailsVM(map));
		}
		System.out.println(sql);
		return vms;
	}



	public Map getNewLeadData() {
		AuthUser user = Utils.getLoggedInUser();
		Map<String, List<ZoneVM>> map = new HashMap<>();
		map.put("productList", getProductList());
		map.put("stateList", dealerService.getStates());
		map.put("cityList", dealerService.getDistricts());
		if(user.getEntityName().equals("Dealer") || user.getEntityName().equals("Sales Consultant")){
			String dealersql = "";
			switch(user.getEntityName()){
				case "Dealer":
					 dealersql = "select * from Dealer where id = "+user.getEntityId();
					 break;
				case "Sales Consultant":
					 dealersql = "select * from Dealer where id IN ( SELECT du.dealer_id from dealer_user as du where du.user_id = "+user.getEntityId()+")";
					 break;
			}
			
			List<Map<String, Object>> rows = jt.queryForList(dealersql);
			List<ZoneVM> dealerList = new ArrayList<ZoneVM>();
			for(Map result : rows) {
				ZoneVM vm = new ZoneVM();
				vm.id = (Long) result.get("id");
				vm.name = (String) result.get("dealerName");
				dealerList.add(vm);
			}
			map.put("dealerList", dealerList);
		}
		
		return map;
	}
	
	public List<ReassignUserVM> getReassignList() {
		AuthUser user = Utils.getLoggedInUser();
		String dealersql = "";
		String usersql = "";
		if(user.getEntityName().equals("RSM") || user.getEntityName().equals("TSR")){
			dealersql = "select * from Dealer where id In ( SELECT du.dealer_id from dealer_user as du where du.user_id = "+user.getEntityId()+" )";
			usersql = "SELECT * FROM user WHERE user.entityName In ('RSM', 'TSR') and user.zone_id = (SELECT zone_id FROM user where id = "+user.getEntityId()+")";
		} 
		List<Map<String, Object>> rows = jt.queryForList(dealersql);
		List<ReassignUserVM> dealerList = new ArrayList<ReassignUserVM>();
		for(Map map : rows) {
			ReassignUserVM vm = new ReassignUserVM();
			vm.id = (Long) map.get("id");
			String address = (String) map.get("address");
			address = address.length() > 25 ? address.substring(0,25) : address;
			vm.name = (String) map.get("dealerName") +" : " +  address;
			vm.entity = "Dealer";
			dealerList.add(vm);
		}
		
		rows = jt.queryForList(usersql);
		for(Map map : rows) {
			ReassignUserVM vm = new ReassignUserVM();
			vm.id = (Long) map.get("id");
			vm.name = (String) map.get("name") +" : (" + (String) map.get("entityName")+")";
			vm.entity = (String) map.get("entityName");
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
		details.setPinCode(vm.getPinCode());
		details.setType(vm.getType());
		session.save(details);
		
		Lead lead = new Lead();
		lead.setDealer((Dealer) session.get(Dealer.class, vm.getDealer()));
		lead.setDisposition1("New");
		lead.setLeadDetails(details);
		lead.setOrigin("Walk-In");
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
		ageing.setZone(lead.getDealer().getZone());
		ageing.setProduct(product.getName());
		ageing.setDealer_id(lead.getDealer().getId());
		ageing.setLead_id(lead.getId());
		session.save(ageing);
	}



	public void reassignDealers(ReassignUserVM reassign, List<Long> ids) {
		String hql = "";
		if(reassign.getEntity().equals("Dealer")){
			hql = "UPDATE lead SET lead.dealer_id = "+reassign.getId()+", lead.user_id = NULL where lead.id IN(:ids)";
		}
		else if(reassign.getEntity().equals("RSM") || reassign.getEntity().equals("TSR")){
			hql = "UPDATE lead SET lead.user_id = "+reassign.getId()+",  lead.dealer_id = NULL where lead.id IN(:ids)";
		}
		else {
			return ;
		}
		Map<String, List<Long>> param = Collections.singletonMap("ids",ids); 
		NamedParameterJdbcTemplate  namedParameterJdbcTemplate = new  
				NamedParameterJdbcTemplate(jt.getDataSource());
		namedParameterJdbcTemplate.update(hql, param);
	}
}
