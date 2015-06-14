package com.mnt.businessApp.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.mnt.businessApp.viewmodel.LeadDetailsVM;
import com.mnt.businessApp.viewmodel.LeadHistoryVM;
import com.mnt.businessApp.viewmodel.LeadVM;
import com.mnt.businessApp.viewmodel.ProductVM;
import com.mnt.businessApp.viewmodel.ZoneVM;
import com.mnt.entities.authentication.AuthUser;
import com.mnt.entities.businessApp.ActivityStream;
import com.mnt.entities.businessApp.Lead;
import com.mnt.entities.businessApp.LeadAgeing;

@Service
public class LeadService {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private JdbcTemplate jt;

	public List<LeadDetailsVM> getAllLeadDetails() {
		AuthUser user = Utils.getLoggedInUser();
		List<LeadDetailsVM> vms = new ArrayList<LeadDetailsVM>();
		String sql = "";
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		
		if(user.getEntityName().equals("Dealer")){
			sql =  "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,l.disposition1 as dispo1,"
					+ "l.disposition2 as dispo2,l.followUpDate as date ,d.dealerName as dealerName "
					+ "FROM lead as l, leaddetails as ld, dealer as d, product as p  where p.id = ld.product_id and d.id = l.dealer_id and "
					+ "ld.id = l.leadDetails_id and dealer_id = ?";
		}  
		else if(user.getEntityName().equals("RSM")){
			sql = "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,l.disposition1 as dispo1,"
					+ "l.disposition2 as dispo2,l.followUpDate as date ,d.dealerName as dealerName "
					+ "FROM lead as l, leaddetails as ld, dealer as d, product as p  where p.id = ld.product_id and d.id = l.dealer_id and "
					+ " ld.id = l.leadDetails_id and dealer_id IN ( select id  from dealer  where rsm_id = ? )";
		}
		else if(user.getEntityName().equals("ZSM") || user.getEntityName().equals("Sellout Contact") || user.getEntityName().equals("Sellout Manager")){
			sql = "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,l.disposition1 as dispo1,"
					+ "l.disposition2 as dispo2,l.followUpDate as date ,d.dealerName as dealerName "
					+ "FROM lead as l, leaddetails as ld, dealer as d, product as p  where p.id = ld.product_id and d.id = l.dealer_id and"
					+ " ld.id = l.leadDetails_id and dealer_id IN ( select id  from dealer as d where zone = (Select user.zone_id from user WHERE user.id = ?))";
		}
		else if(user.getEntityName().equals("Category Manager") || user.getEntityName().equals("Sellout-Regional")){
			sql = "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,l.disposition1 as dispo1,"
					+ "l.disposition2 as dispo2,l.followUpDate as date ,d.dealerName as dealerName "
					+ "FROM lead as l, leaddetails as ld, dealer as d, product as p  where p.id = ld.product_id and d.id = l.dealer_id and"
					+ " ld.id = l.leadDetails_id and ld.product_id IN ( select products_id  from user_product  where User_id = ? )";
		}
		else if(user.getEntityName().equals("Admin") || user.getEntityName().equals("CEO") || user.getEntityName().equals("General Manager")){
			sql = "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,l.disposition1 as dispo1,"
					+ "l.disposition2 as dispo2,l.followUpDate as date ,d.dealerName as dealerName "
					+ "FROM lead as l, leaddetails as ld, dealer as d, product as p  where p.id = ld.product_id and d.id = l.dealer_id and"
					+ " ld.id = l.leadDetails_id";
			rows = jt.queryForList(sql);
			for(Map map : rows) {
				vms.add(new LeadDetailsVM(map));
			}
			System.out.println(sql);
			return vms;
		} else {
			System.out.println("Unknown Role");
		}
		
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
		int escalatedLevel = 0;
		if(user.getEntityName().equals("ZSM")) escalatedLevel = 3;
		if(user.getEntityName().equals("Sellout Manager")) escalatedLevel = 2;
		if(user.getEntityName().equals("Sellout Contact")) escalatedLevel = 1;
		if(user.getEntityName().equals("Dealer")){
			sql =  "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,l.disposition1 as dispo1,"
					+ "l.disposition2 as dispo2,l.followUpDate as date ,d.dealerName as dealerName "
					+ "FROM lead as l, leaddetails as ld, dealer as d, product as p  where p.id = ld.product_id and d.id = l.dealer_id and disposition1 = 'Escalated'  "
					+ "and ld.id = l.leadDetails_id and dealer_id = ?";
		}  
		else if(user.getEntityName().equals("RSM")){
			sql = "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,l.disposition1 as dispo1,"
					+ "l.disposition2 as dispo2,l.followUpDate as date ,d.dealerName as dealerName "
					+ "FROM lead as l, leaddetails as ld, dealer as d, product as p  where p.id = ld.product_id and"
					+ " d.id = l.dealer_id and disposition1 = 'Escalated' "
					+ "and ld.id = l.leadDetails_id and dealer_id IN ( select id  from dealer  where rsm_id = ? )";
		}
		else if(user.getEntityName().equals("ZSM") || user.getEntityName().equals("Sellout Contact") || user.getEntityName().equals("Sellout Manager")){
			sql = "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,l.disposition1 as dispo1,"
					+ "l.disposition2 as dispo2,l.followUpDate as date ,d.dealerName as dealerName "
					+ "FROM lead as l, leaddetails as ld, dealer as d, product as p  where p.id = ld.product_id and"
					+ " d.id = l.dealer_id and"
					+ " disposition1 = 'Escalated' and l.escalatedLevel = " + escalatedLevel 
					+ " and ld.id = l.leadDetails_id and dealer_id IN ( select id  from dealer where zone = (Select user.zone_id from user WHERE user.id = ?) )";
		}
		else if(user.getEntityName().equals("Category Manager") || user.getEntityName().equals("Sellout-Regional")){
			sql = "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,l.disposition1 as dispo1,"
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
					+ "ld.pinCode as pincode,p.name as product,l.disposition1 as dispo1,"
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
		if(user.getEntityName().equals("Dealer")){
			sql =  "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,l.disposition1 as dispo1,"
					+ "l.disposition2 as dispo2,l.followUpDate as date ,d.dealerName as dealerName "
					+ "FROM lead as l, leaddetails as ld, dealer as d, product as p  where p.id = ld.product_id and"
					+ " d.id = l.dealer_id and l.followUpDate IS NOT NULL "
					+ "and ld.id = l.leadDetails_id and dealer_id = ?";
		}  
		else if(user.getEntityName().equals("RSM")){
			sql = "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,l.disposition1 as dispo1,"
					+ "l.disposition2 as dispo2,l.followUpDate as date ,d.dealerName as dealerName "
					+ "FROM lead as l, leaddetails as ld, dealer as d, product as p  where p.id = ld.product_id and d.id = l.dealer_id and l.followUpDate IS NOT NULL "
					+ "and ld.id = l.leadDetails_id and dealer_id IN ( select id  from dealer  where rsm_id = ? )";
		}
		else if(user.getEntityName().equals("ZSM") || user.getEntityName().equals("Sellout Contact") || user.getEntityName().equals("Sellout Manager")){
			sql = "Select ld.sr as srNo, "
					+ "ld.name as name, "
					+ "l.id as id,ld.email as email, "
					+ "ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,"
					+ "p.name as product,"
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
					+ " where zone = (Select user.zone_id from user WHERE user.id = ?))";
		}
		else if(user.getEntityName().equals("Category Manager") || user.getEntityName().equals("Sellout-Regional")){
			sql = "Select ld.sr as srNo, "
					+ "ld.name as name, "
					+ "l.id as id,ld.email as email, "
					+ "ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,"
					+ "p.name as product,"
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
					+ "ld.pinCode as pincode,p.name as product,l.disposition1 as dispo1,"
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
		List<Map<String, Object>> rows = jt.queryForList(sql,new Object[] {user.getEntityId()});
		for(Map map : rows) {
			vms.add(new LeadDetailsVM(map));
		}
		System.out.println(sql);
		return vms;
		
	}

	public Map getZoneAndProduct() {
		AuthUser user = Utils.getLoggedInUser();
		String sql = "select * from zone";
		
		List<Map<String, Object>> rows = jt.queryForList(sql);
		List<ZoneVM> zoneList = new ArrayList<ZoneVM>();
		Map<String,List> dataList = new HashMap<String, List>();
		System.out.println(" ::::::::::::::::: "+user.getEntityName());
		if(!(user.getEntityName().equals("Category Manager") || user.getEntityName().equals("Sellout-Regional") || user.getEntityName().equals("Admin") || user.getEntityName().equals("CEO") || user.getEntityName().equals("General Manager"))){
			return dataList;
		}
		for(Map map : rows) {
			ZoneVM vm = new ZoneVM();
			vm.id = (Long) map.get("id");
			vm.name = (String) map.get("name");
			zoneList.add(vm);
		}
		
		dataList.put("zoneList", zoneList);
		if(user.getEntityName().equals("Category Manager") || user.getEntityName().equals("Sellout-Regional")){
			sql = "select * from product where product.id IN (SELECT user_product.products_id from user_product WHERE user_product.User_id = "+user.getEntityId()+") ";
		}
		if(user.getEntityName().equals("Admin") || user.getEntityName().equals("CEO") || user.getEntityName().equals("General Manager")){
			sql = "select * from product";
		}
		rows = jt.queryForList(sql);
		List<ProductVM> productList = new ArrayList<ProductVM>();
		for(Map map : rows) {
			ProductVM vm = new ProductVM();
			vm.id = (Long) map.get("id");
			vm.name = (String) map.get("name");
			productList.add(vm);
		}
		dataList.put("productList", productList);
		System.out.println(sql);
		return dataList;
	}

	public List<LeadDetailsVM> getOpenLeads() {
		AuthUser user = Utils.getLoggedInUser();
		List<LeadDetailsVM> vms = new ArrayList<LeadDetailsVM>();
		String proZone = "" ;
		if(user.getEntityName().equals("Dealer")){
			proZone =  " and dealer_id = ?";
		}  
		if(user.getEntityName().equals("RSM")){
			proZone = " and dealer_id IN ( select id  from dealer  where rsm_id = ? )";
		}
		if(user.getEntityName().equals("ZSM") || user.getEntityName().equals("Sellout Contact") || user.getEntityName().equals("Sellout Manager")){
			proZone = " and dealer_id IN ( select id  from dealer as d where zone = (Select user.zone_id from user WHERE user.id = ?))";
		}
		if(user.getEntityName().equals("Category Manager") || user.getEntityName().equals("Sellout-Regional")){
			proZone = " and ld.product_id IN ( select products_id  from user_product  where User_id = ? )";
		}
		if(user.getEntityName().equals("Admin") || user.getEntityName().equals("CEO") || user.getEntityName().equals("General Manager")){
			String sql = "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,l.disposition1 as dispo1,"
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
					+ "ld.pinCode as pincode,p.name as product,l.disposition1 as dispo1,"
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
		if(user.getEntityName().equals("RSM")){
			proZone = " and dealer_id IN ( select id  from dealer  where rsm_id = ? )";
		}
		if(user.getEntityName().equals("ZSM") || user.getEntityName().equals("Sellout Contact") || user.getEntityName().equals("Sellout Manager")){
			proZone = " and dealer_id IN ( select id  from dealer as d where zone = (Select user.zone_id from user WHERE user.id = ?))";
		}
		if(user.getEntityName().equals("Category Manager") || user.getEntityName().equals("Sellout-Regional")){
			proZone = " and ld.product_id IN ( select products_id  from user_product  where User_id = ? )";
		}
		if(user.getEntityName().equals("Admin") || user.getEntityName().equals("CEO") || user.getEntityName().equals("General Manager")){
			String sql = "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,l.disposition1 as dispo1,"
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
					+ "ld.pinCode as pincode,p.name as product,l.disposition1 as dispo1,"
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
		if(user.getEntityName().equals("RSM")){
			proZone = " and dealer_id IN ( select id  from dealer  where rsm_id = ? )";
		}
		if(user.getEntityName().equals("ZSM") || user.getEntityName().equals("Sellout Contact") || user.getEntityName().equals("Sellout Manager")){
			proZone = " and dealer_id IN ( select id  from dealer as d where zone = (Select user.zone_id from user WHERE user.id = ?))";
		}
		if(user.getEntityName().equals("Category Manager") || user.getEntityName().equals("Sellout-Regional")){
			proZone = " and ld.product_id IN ( select products_id  from user_product  where User_id = ? )";
		}
		if(user.getEntityName().equals("Admin") || user.getEntityName().equals("CEO") || user.getEntityName().equals("General Manager")){
			String sql = "Select ld.sr as srNo, ld.name as name, "
					+ "l.id as id,ld.email as email, ld.contactNo as contactNo,"
					+ "ld.pinCode as pincode,p.name as product,l.disposition1 as dispo1,"
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
					+ "ld.pinCode as pincode,p.name as product,l.disposition1 as dispo1,"
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

}
