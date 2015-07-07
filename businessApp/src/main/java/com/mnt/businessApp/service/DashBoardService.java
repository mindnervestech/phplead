package com.mnt.businessApp.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.mnt.businessApp.viewmodel.DashBoardProgressBarVm;
import com.mnt.businessApp.viewmodel.SplineVM;
import com.mnt.businessApp.viewmodel.ZoneVM;
import com.mnt.entities.authentication.AuthUser;
import com.mnt.entities.businessApp.User;

@Service
public class DashBoardService {
	

	@Autowired
	private DealerService dealerService;

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private JdbcTemplate jt;

	private Map getLeadProgressBarVM(String sql, String description, String iconClass, String type, int total,List<Long> ids) {
		if(ids.size() == 0){
			return null;
		}
		Map<String, List<Long>> param = Collections.singletonMap("ids",ids); 
		NamedParameterJdbcTemplate  namedParameterJdbcTemplate = new  
				NamedParameterJdbcTemplate(jt.getDataSource());
		int actualValue = namedParameterJdbcTemplate.queryForInt(sql, param);
		return getList(description, iconClass, type, total, actualValue);
	}

	private Map getList(String description, String iconClass, String type,
			int total, int actualValue) {
		Map<String, DashBoardProgressBarVm> map = new HashMap<>();
		DashBoardProgressBarVm vm = new DashBoardProgressBarVm();
		vm.setActualValue(actualValue+"");
		vm.setDescription(description+" Leads");
		vm.setIconClass(iconClass);
		vm.setType(type);
		vm.setHref("escalated-leads({leadType : '"+description+"', editId : 'All' })");
		if(actualValue != 0 && total != 0){
			vm.setValue(((Double)(((double)actualValue/(double)total) * 100)).intValue());
		}
		map.put("initValue", vm);
		return map;
	}

	public List<Map> getDashboardProgressbar(Date start, Date end, String zone, String state,
			Long product, Long dealer) {
		AuthUser user = Utils.getLoggedInUser();
		String sql = "";
		List<Long> ids = new ArrayList<>();
		String sqlDate = " and l.lastDispo1ModifiedDate >= '"+new SimpleDateFormat("yyyy-MM-dd").format(start)+"' and  l.lastDispo1ModifiedDate <= '"+new SimpleDateFormat("yyyy-MM-dd").format(getDate(end))+"'";
		String proZone = "";
		String escalatationlevel = "";
		if(dealer != 0 || product != 0 || !zone.equals("0") || !state.equals("0")){
			String zoneState = "";
			
			if(!zone.equals("0") && !state.equals("0")){
				zoneState = " where dealer.zone = '"+zone+"' and dealer.state = '"+state+"'";
			} else if(!state.equals("0")){
				zoneState = " where dealer.state = '"+state+"'";
			} else if(!zone.equals("0")){
				zoneState = " where dealer.zone = '"+zone+"'";
			}
			if(product != 0 ){
				proZone = " and ld.product_id ="+product;
			} else {
				if(user.getEntityName().equals("Category Manager") || user.getEntityName().equals("Sellout-Regional") || user.getEntityName().equals("RSM") || user.getEntityName().equals("TSR") || user.getEntityName().equals("Sales Consultant")){
					sql = "select product.id from product where product.id IN (SELECT user_product.products_id from user_product WHERE user_product.User_id = "+user.getEntityId()+") ";
					proZone = " and ld.product_id IN ("+sql+")";
				}
			}
			if(user.getEntityName().equals("RSM") || user.getEntityName().equals("TSR") || user.getEntityName().equals("Sales Consultant")){
				if(dealer != 0){
					ids.add(dealer);
				} else {
					ids = jt.queryForList("SELECT d.dealer_id from dealer_user as d where d.user_id = "+user.getEntityId(), Long.class);
				}
			} else {
				ids = jt.queryForList("Select dealer.id FROM dealer"+zoneState, Long.class);
			}
		}
		else if(user.getEntityName().equals("Dealer")){
			ids.add(user.getEntityId());
		}  
		else if(user.getEntityName().equals("RSM") || user.getEntityName().equals("TSR") || user.getEntityName().equals("Sales Consultant")){
			ids = jt.queryForList("SELECT d.dealer_id from dealer_user as d where d.user_id = "+user.getEntityId(), Long.class);
			sql = "select product.id from product where product.id IN (SELECT user_product.products_id from user_product WHERE user_product.User_id = "+user.getEntityId()+") ";
			proZone = " and ld.product_id IN ("+sql+")";
			escalatationlevel = " and l.escalatedLevel = 1 ";
		}
		else if(user.getEntityName().equals("ZSM")  || user.getEntityName().equals("Sellout Manager")){
			User user1 = (User) sessionFactory.getCurrentSession().get(User.class, user.getEntityId());
			ids = jt.queryForList("select d.id  from dealer as d where d.zone = '"+user1.getZone().getName()+"'", Long.class);
			escalatationlevel = " and l.escalatedLevel = 2 ";
		}
		else if(user.getEntityName().equals("Category Manager") || user.getEntityName().equals("Sellout-Regional")){
			sql = "select product.id from product where product.id IN (SELECT user_product.products_id from user_product WHERE user_product.User_id = "+user.getEntityId()+") ";
			proZone = " and ld.product_id IN ("+sql+")";
			ids = jt.queryForList("Select dealer.id FROM dealer", Long.class);
		}
		else if(user.getEntityName().equals("Admin") || user.getEntityName().equals("CEO") || user.getEntityName().equals("General Manager")){
			ids = jt.queryForList("Select dealer.id FROM dealer", Long.class);
		}  

		List<Map> list = new ArrayList<>();
		if(ids.size() == 0){
			list.add(getList("Escalated", "icon fa fa-suitcase", "warning", 0, 0));
			list.add(getList("Open", "icon fa fa-folder-open", "warning", 0, 0));
			list.add(getList("Won", "icon fa fa-thumbs-up", "success", 0, 0));
			list.add(getList("Lost", "icon fa fa-thumbs-down", "danger", 0, 0));
			return list;
		}
		sql = "SELECT COUNT(*) FROM lead l, leadDetails ld  where ld.id = l.leadDetails_id and l.dealer_id IN (:ids)"+sqlDate
				+ proZone;
		Map<String, List<Long>> param = Collections.singletonMap("ids",ids); 
		NamedParameterJdbcTemplate  namedParameterJdbcTemplate = new  
				NamedParameterJdbcTemplate(jt.getDataSource());
		int total = namedParameterJdbcTemplate.queryForInt(sql, param);
		sql = "SELECT COUNT(*) FROM lead l, leadDetails ld  where ld.id = l.leadDetails_id"
				+" and l.disposition1 = 'Escalated' and l.dealer_id IN (:ids) "
				+ escalatationlevel + proZone+sqlDate;
		list.add(getLeadProgressBarVM(sql, "Escalated", "icon fa fa-suitcase", "warning", total,ids));
		sql = "SELECT COUNT(*) FROM lead l, leadDetails ld  where ld.id = l.leadDetails_id and l.dealer_id IN (:ids) "
				+ "and (disposition1 = 'New' or disposition2 IN('Call Back','Quote Sent','Visiting Store','Not Contacted'))"
				+proZone+sqlDate;
		list.add(getLeadProgressBarVM(sql, "Open", "icon fa fa-folder-open", "warning", total,ids));
		sql = "SELECT COUNT(*) FROM lead l, leadDetails ld  where ld.id = l.leadDetails_id and disposition2 = 'Won' and l.dealer_id IN (:ids) "
				+ proZone+sqlDate;
		list.add(getLeadProgressBarVM(sql, "Won", "icon fa fa-thumbs-up", "success", total,ids));
		sql = "SELECT COUNT(*) FROM lead l, leadDetails ld  where ld.id = l.leadDetails_id and disposition2 = 'Lost' and l.dealer_id IN (:ids) "
				+ proZone+sqlDate;

		list.add(getLeadProgressBarVM(sql, "Lost", "icon fa fa-thumbs-down", "danger", total,ids));
		return list;
	}
	
	public Map getZoneSplineBetweenDates(Date start, Date end, String zone, String state, Long product) {
		List<SplineVM> splineVMs = new ArrayList<>(); 
		AuthUser user = Utils.getLoggedInUser();
		if(user.getEntityName().equals("ZSM") || user.getEntityName().equals("Sellout Manager")){
			List<String> zones = jt.queryForList("Select name from zone WHERE id = (select user.zone_id from user where user.id = "+user.getEntityId()+")", String.class); 
			if (zones.isEmpty()) {
				return null;
			} else {
				zone = zones.get(0);
			}
		}
		splineVMs.add(getSplineVMforZone(start, end, zone, state, product, " and disposition2 = 'Won' ", "Won", "#01c6ad"));
		splineVMs.add(getSplineVMforZone(start, end, zone, state, product,  " and disposition2 = 'Lost' ", "Lost", "#FF0000"));
		splineVMs.add(getSplineVMforZone(start, end,  zone, state, product, " and (disposition1 = 'New' or disposition2 IN('Call Back','Quote Sent','Visiting Store','Not Contacted')) ", "Open", "#ffce54"));
		Map<String, List<SplineVM>> map = new HashMap<>();
		map.put("dataset", splineVMs);
		return map;
	}
	
	private SplineVM getSplineVMforZone(Date start, Date end, String zone, String state, Long product, String query, String cat, String color) {
		AuthUser user = Utils.getLoggedInUser();
		System.out.println("ZONE :: "+zone);
		SplineVM vm = new SplineVM();
		String productSql = "";
		String zoneState = "";
		String select = "SELECT COUNT(*) as count, d.zone as name ";
		String gropBy = " GROUP BY d.zone ORDER BY d.zone asc";
		String all = "Select * from zone where zone.name != 'Corporate' ORDER BY name asc";
		if(product == 0){
			if(user.getEntityName().equals("Category Manager") || user.getEntityName().equals("Sellout-Regional")){
				productSql = " and ld.product_id IN (SELECT user_product.products_id from user_product WHERE user_product.User_id = "+user.getEntityId()+") ";
				
			} else if(user.getEntityName().equals("General Manager") || user.getEntityName().equals("CEO") || user.getEntityName().equals("Admin")){
				productSql = " and ld.product_id IN (select id from product) ";
			} 
		} else {
			productSql = " and ld.product_id IN ("+product+")";
		}
		
		
		if(!zone.equals("0") && !state.equals("0")){
			zoneState = "and d.zone = '"+zone+"' and d.state = '"+state+"'";
			select = "SELECT COUNT(*) as count, d.state as name ";
			gropBy = " GROUP BY d.state ORDER BY d.state asc";
			all = "Select id, name from state where state.name = '"+state+"'";
		} else if(!state.equals("0")){
			zoneState = " and d.state = '"+state+"'";
			select = "SELECT COUNT(*) as count, d.state as name ";
			gropBy = " GROUP BY d.state ORDER BY d.state asc";
			all = "Select id, name from state where state.name = '"+state+"'";
		} else if(!zone.equals("0")){
			zoneState = " and d.zone = '"+zone+"'";
			select = "SELECT COUNT(*) as count, d.state as name ";
			gropBy = " GROUP BY d.state ORDER BY d.state asc";
			all = "Select id, name from state where state.zone_id = (SELECT id from zone WHERE zone.name = '"+zone+"') ORDER BY name asc";
		}  
		
		
		List<Map<String, Object>> rows = jt.queryForList(select+" from lead as l, leaddetails as ld, product as p, "
				+ " dealer as d where l.lastDispo1ModifiedDate > '"+new SimpleDateFormat("yyyy-MM-dd").format(start)+"' "
				+ " and  l.lastDispo1ModifiedDate < '"+new SimpleDateFormat("yyyy-MM-dd").format(getDate(end))+"' "
				+ " and ld.product_id = p.id and ld.id = l.leadDetails_id and l.dealer_id = d.id "
				+ productSql + zoneState + query + gropBy);
		
				
		List<Map<String, Object>>  allProducts = jt.queryForList(all);
		List<ZoneVM> productList = new ArrayList<ZoneVM>();
		int i = 0;
		List<List> list = new ArrayList<>();
		for(Map row : allProducts) {
			List innerlist = new ArrayList(2);
			innerlist.add(row.get("name"));
			if(i < rows.size()){
				Map data = rows.get(i);
				if(data.get("name").equals(row.get("name"))){
					innerlist.add(data.get("count"));
					i++;
				} else {
					innerlist.add(0+"");
				}
			} else {
				innerlist.add(0+"");
			}
			list.add(innerlist);
		}
		vm.setData(list);
		vm.setColor(color);
		vm.setLabel(cat);
		return vm;
	}

	public Map getProductSplineBetweenDates(Date start, Date end, String zone, String state, Long product) {
		List<SplineVM> splineVMs = new ArrayList<>(); 
		splineVMs.add(getSplineVMforProduct(start, end, zone, state, product, " and disposition2 = 'Won' ", "Won", "#01c6ad"));
		splineVMs.add(getSplineVMforProduct(start, end, zone, state, product,  " and disposition2 = 'Lost' ", "Lost", "#FF0000"));
		splineVMs.add(getSplineVMforProduct(start, end,  zone, state, product, " and (disposition1 = 'New' or disposition2 IN('Call Back','Quote Sent','Visiting Store','Not Contacted')) ", "Open", "#ffce54"));
		Map<String, List<SplineVM>> map = new HashMap<>();
		map.put("dataset", splineVMs);
		return map;
	}
	
	private SplineVM getSplineVMforProduct(Date start, Date end, String zone, String state, Long product, String query, String cat, String color) {
		SplineVM vm = new SplineVM();
		AuthUser user = Utils.getLoggedInUser();
		String productSql = "";
		String zoneState = "";
		if(product == 0){
			if(user.getEntityName().equals("Category Manager") || user.getEntityName().equals("Sellout-Regional")){
				productSql = "SELECT user_product.products_id from user_product WHERE user_product.User_id = "+user.getEntityId();
			} else 
			if(user.getEntityName().equals("General Manager") || user.getEntityName().equals("CEO") || user.getEntityName().equals("Admin")){
				productSql = "select id from product";
			} else {
				return vm; 
			}
		} else {
			productSql = ""+product;
		}
		
		if(!zone.equals("0") && !state.equals("0")){
			zoneState = " and d.zone = '"+zone+"' and d.state = '"+state+"'";
		} else if(!state.equals("0")){
			zoneState = " and d.state = '"+state+"'";
		} else if(!zone.equals("0")){
			zoneState = " and d.zone = '"+zone+"'";
		}
		
		
		List<Map<String, Object>> rows = jt.queryForList("SELECT COUNT(*) as count, p.id as id from lead as l, leaddetails as ld, product as p, "
				+ " dealer as d where l.lastDispo1ModifiedDate > '"+new SimpleDateFormat("yyyy-MM-dd").format(start)+"' "
				+ " and  l.lastDispo1ModifiedDate < '"+new SimpleDateFormat("yyyy-MM-dd").format(getDate(end))+"' "
				+ " and ld.product_id = p.id and ld.product_id IN ("+productSql+") and ld.id = l.leadDetails_id and l.dealer_id = d.id"
				+ zoneState + query
				+ " GROUP BY ld.product_id ORDER BY ld.product_id asc");
		
				
		List<Map<String, Object>>  allProducts = jt.queryForList("Select * from product where id In ("+productSql+") ORDER BY id asc");
		List<ZoneVM> productList = new ArrayList<ZoneVM>();
		int i = 0;
		List<List> list = new ArrayList<>();
		for(Map row : allProducts) {
			List innerlist = new ArrayList(2);
			innerlist.add(row.get("name"));
			if(i < rows.size()){
				Map data = rows.get(i);
				if(data.get("id").equals(row.get("id"))){
					innerlist.add(data.get("count"));
					i++;
				} else {
					innerlist.add(0+"");
				}
			} else {
				innerlist.add(0+"");
			}
			list.add(innerlist);
		}
		vm.setData(list);
		vm.setColor(color);
		vm.setLabel(cat);
		return vm;
	}
	
	public Map getDealerSplineBetweenDates(Date start, Date end, String zone, String state, Long product, Long dealer) {
		List<SplineVM> splineVMs = new ArrayList<>(); 
		splineVMs.add(getSplineDataForDealer(start, end, " and disposition2 = 'Won' ", "Won", "#01c6ad", state, product, dealer));
		splineVMs.add(getSplineDataForDealer(start, end, " and disposition2 = 'Lost' ", "Lost", "#FF0000", state, product, dealer));
		splineVMs.add(getSplineDataForDealer(start, end, " and (disposition1 = 'New' or disposition2 IN('Call Back','Quote Sent','Visiting Store','Not Contacted')) ", "Open", "#ffce54", state, product, dealer));
		Map<String, List<SplineVM>> map = new HashMap<>();
		map.put("dataset", splineVMs);
		return map;
	}

	private SplineVM getSplineDataForDealer(Date start, Date end, String query, String cat, String color, String state, Long product, Long dealer) {
		AuthUser user = Utils.getLoggedInUser();
		
		if(product != 0 && dealer != 0){
			query += " and ld.product_id = "+product+"  and l.dealer_id = "+dealer;
		} else if(product != 0){
			if(user.getEntityName().equals("Dealer")){
				query = query + " and l.dealer_id IN ("+user.getEntityId()+") ";
			} else if(user.getEntityName().equals("RSM") || user.getEntityName().equals("TSR") || user.getEntityName().equals("Sales Consultant")){
				query = query + " and l.dealer_id IN (SELECT d.dealer_id from dealer_user as d where d.user_id = "+user.getEntityId()+") ";
			} 
			query += " and ld.product_id = "+product;
		} else if(dealer != 0){
			if(user.getEntityName().equals("RSM") || user.getEntityName().equals("TSR") || user.getEntityName().equals("Sales Consultant")){
				query +=  "and ld.product_id IN ( select products_id  from user_product  where User_id = "+user.getEntityId()+" )";
			}
			query += "  and l.dealer_id = "+dealer;
		} else if(user.getEntityName().equals("Dealer")){
			query = query + " and l.dealer_id IN ("+user.getEntityId()+") ";
		} else if(user.getEntityName().equals("RSM") || user.getEntityName().equals("TSR") || user.getEntityName().equals("Sales Consultant")){
			query = query + " and l.dealer_id IN (SELECT d.dealer_id from dealer_user as d where d.user_id = "+user.getEntityId()+") "
					+ " and ld.id = l.leadDetails_id and ld.product_id IN ( select products_id  from user_product  where User_id = "+user.getEntityId()+" )";
		} else if(user.getEntityName().equals("ZSM") || user.getEntityName().equals("Sellout Manager")){
			User user1 = (User) sessionFactory.getCurrentSession().get(User.class, user.getEntityId());
			String stateSql = "";
			if(!state.equals("0")){
				stateSql = " and d.state = '"+state+"'";
			}
			query = query + " and l.dealer_id IN (select d.id  from dealer as d where d.zone = '"+user1.getZone().getName()+"' "+stateSql+" ) ";
		}
		return getDealerSplineVM(start, end, cat, color, query);
	}

	private SplineVM getDealerSplineVM(Date startDate, Date endDate, String cat,
			String color, String query) {
		SplineVM vm = new SplineVM();
		endDate = getDate(endDate);
		List<List> list = new ArrayList<>();
		String sql = "SELECT COUNT(*) as count, l.lastDispo1ModifiedDate as date from lead as l, leaddetails as ld "
				+ " where l.lastDispo1ModifiedDate > '"+new SimpleDateFormat("yyyy-MM-dd").format(startDate)+"' "
				+ " and  l.lastDispo1ModifiedDate < '"+new SimpleDateFormat("yyyy-MM-dd").format(endDate)+"' "
				+ " and  l.leadDetails_id = ld.id "
				+  query
				+ " GROUP BY CAST(l.lastDispo1ModifiedDate  AS DATE) "
				+ " ORDER BY CAST(l.lastDispo1ModifiedDate  AS DATE) asc";
		System.out.println("sql :: "+sql);
		List<Map<String, Object>> rows = jt.queryForList(sql);
		
		Calendar start = Calendar.getInstance();
		start.setTime(startDate);
		Calendar end = Calendar.getInstance();
		end.setTime(endDate);
		SimpleDateFormat format = new SimpleDateFormat("dd-MM");
		int i = 0;
		for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
			List innerlist = new ArrayList(2);
			innerlist.add(format.format(date)+"");
			if(i < rows.size()){
				if(format.format(date).compareTo(format.format((Date)rows.get(i).get("date"))) == 0){
					innerlist.add(rows.get(i).get("count"));
					i++;
				} else {
					innerlist.add(0+"");
				}
			} else {
				innerlist.add(0+"");
			}
			list.add(innerlist);
		}
		vm.setData(list);
		vm.setColor(color);
		vm.setLabel(cat);
		return vm;
	}

	private Date getDate(Date end){
		Calendar cal = Calendar.getInstance();
		cal.setTime(end);
		cal.add(Calendar.DATE, 1);
		return cal.getTime();
	}
}
