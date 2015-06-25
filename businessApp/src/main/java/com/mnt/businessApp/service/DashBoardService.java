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

	private List<Map> getProgressBarByProducts(List<Map> list, int total,List<Long> ids) {
		String sql = "SELECT COUNT(*) FROM lead l, leadDetails ld  where ld.id = l.leadDetails_id "
				+ "and (disposition1 = 'New' or disposition2 IN('Call Back','Quote Sent','Visiting Store','Not Contacted'))"
				+ " and ld.product_id IN (:ids)";
		list.add(getLeadProgressBarVM(sql, "Open", "icon fa fa-folder-open", "warning", total,ids));
		sql = "SELECT COUNT(*) FROM lead l, leadDetails ld  where ld.id = l.leadDetails_id and disposition2 = 'Won'"
				+ " and ld.product_id IN (:ids)";
		list.add(getLeadProgressBarVM(sql, "Won", "icon fa fa-thumbs-up", "success", total,ids));
		sql = "SELECT COUNT(*) FROM lead l, leadDetails ld  where ld.id = l.leadDetails_id and disposition2 = 'Lost'"
				+ " and ld.product_id IN (:ids)";
		list.add(getLeadProgressBarVM(sql, "Lost", "icon fa fa-thumbs-down", "danger", total,ids));
		return list;
	}

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

	public Map getZoneSplineBetweenDates(Date start, Date end, String zone, String state) {
		List<ZoneVM> zoneList = new ArrayList<>();
		String zoneOrState = "zone";
		if(zone.equals("0")){
			zoneList = dealerService.getZone();
			zoneOrState = "zone";
		} else if(zone.equals("state")) {
			zoneList = dealerService.getStateByZone("user");
			zoneOrState = "state";
		}else {
			zoneList = dealerService.getStateByZone(zone);
			zoneOrState = "state";
		}
		List<SplineVM> splineVMs = new ArrayList<>(); 
		splineVMs.add(getSplineData(start, end, zoneList, "Won", "#01c6ad", zoneOrState));
		splineVMs.add(getSplineData(start, end, zoneList, "Lost", "#FF0000", zoneOrState));
		splineVMs.add(getSplineData(start, end, zoneList, "Open", "#ffce54", zoneOrState));
		Map<String, List<SplineVM>> map = new HashMap<>();
		map.put("dataset", splineVMs);
		return map;
	}
	
	public Map getProductSplineBetweenDates(Date start, Date end) {
		AuthUser user = Utils.getLoggedInUser();
		Map<String, List<SplineVM>> map = new HashMap<>();
		String sql = "";
		if(user.getEntityName().equals("Category Manager") || user.getEntityName().equals("Sellout-Regional")){
			sql = "select * from product where product.id IN (SELECT user_product.products_id from user_product WHERE user_product.User_id = "+user.getEntityId()+") ";
		} else 
		if(user.getEntityName().equals("General Manager") || user.getEntityName().equals("CEO") || user.getEntityName().equals("Admin")){
			sql = "select * from product";
		} else {
			return map; 
		}
		List<Map<String, Object>>rows = jt.queryForList(sql);
		
		List<ZoneVM> productList = new ArrayList<ZoneVM>();
		for(Map row : rows) {
			ZoneVM vm = new ZoneVM();
			vm.id = (Long) row.get("id");
			vm.name = (String) row.get("name");
			productList.add(vm);
		}
		List<SplineVM> splineVMs = new ArrayList<SplineVM>(); 
		splineVMs.add(getSplineData(start, end, productList, "Won", "#01c6ad", "product"));
		splineVMs.add(getSplineData(start, end, productList, "Lost", "#FF0000", "product"));
		splineVMs.add(getSplineData(start, end, productList, "Open", "#ffce54", "product"));
		
		map.put("dataset", splineVMs);
		return map;
	}
	
	public Map getDealerSplineBetweenDates(Date start, Date end) {
		List<SplineVM> splineVMs = new ArrayList<>(); 
		splineVMs.add(getSplineDataForDealer(start, end, " and disposition2 = 'Won' ", "Won", "#01c6ad"));
		splineVMs.add(getSplineDataForDealer(start, end, " and disposition2 = 'Lost' ", "Lost", "#FF0000"));
		splineVMs.add(getSplineDataForDealer(start, end, " and (disposition1 = 'New' or disposition2 IN('Call Back','Quote Sent','Visiting Store','Not Contacted')) ", "Open", "#ffce54"));
		Map<String, List<SplineVM>> map = new HashMap<>();
		map.put("dataset", splineVMs);
		return map;
	}

	private SplineVM getSplineDataForDealer(Date start, Date end, String query, String cat, String color) {
		AuthUser user = Utils.getLoggedInUser();
		if(user.getEntityName().equals("Dealer")){
			query = query + " and l.dealer_id IN ("+user.getEntityId()+") ";
		}
		if(user.getEntityName().equals("RSM") || user.getEntityName().equals("TSR") || user.getEntityName().equals("Sales Consultant")){
			query = query + " and l.dealer_id IN (SELECT d.dealer_id from dealer_user as d where d.user_id = "+user.getEntityId()+") "
					+ " and ld.id = l.leadDetails_id and ld.product_id IN ( select products_id  from user_product  where User_id = "+user.getEntityId()+" )";
		}
		else if(user.getEntityName().equals("ZSM") || user.getEntityName().equals("Sellout Manager")){
			User user1 = (User) sessionFactory.getCurrentSession().get(User.class, user.getEntityId());
			query = query + " and l.dealer_id IN (select d.id  from dealer as d where d.zone = '"+user1.getZone().getName()+"' ) ";
		}
		return getDealerSplineVM(start, end, cat, color, query);
	}

	private SplineVM getDealerSplineVM(Date startDate, Date endDate, String cat,
			String color, String query) {
		SplineVM vm = new SplineVM();
		List<List> list = new ArrayList<>();
		List<Map<String, Object>> rows = jt.queryForList("SELECT COUNT(*) as count, l.lastDispo1ModifiedDate as date from lead as l, leaddetails as ld "
				+ " where l.lastDispo1ModifiedDate > '"+new SimpleDateFormat("yyyy-MM-dd").format(startDate)+"' "
				+ " and  l.lastDispo1ModifiedDate < '"+new SimpleDateFormat("yyyy-MM-dd").format(getDate(endDate))+"' "
				+  query
				+ " GROUP BY CAST(l.lastDispo1ModifiedDate  AS DATE) "
				+ " ORDER BY CAST(l.lastDispo1ModifiedDate  AS DATE) asc");
		
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

	private SplineVM getSplineData(Date start, Date end, List<ZoneVM> zoneList, String leadCat, String color, String category) {
		SplineVM vm = new SplineVM();
		List<List> list = new ArrayList<>();
		for(ZoneVM zoneVM : zoneList){
			List innerlist = new ArrayList(2);
			innerlist.add(zoneVM.getName()); 
			if(leadCat.equals("Open")){
				innerlist.add(getSplineCountForOpen(zoneVM, start, end, category)); 
			} else {
				innerlist.add(getSplineCount(zoneVM, start, end, leadCat, category)); 
			}
			list.add(innerlist);
		}
		vm.setData(list);
		vm.setColor(color);
		vm.setLabel(leadCat);
		return vm;
	}

	private Object getSplineCountForOpen(ZoneVM zoneVm, Date start, Date end, String category) {
		Map<String, List<Long>> param = null;
		String sqlQuery = "";
		if(category.equals("product")){
			List<Long> ids = new ArrayList<>();
			ids.add(zoneVm.getId());
			param = Collections.singletonMap("ids",ids); 
			sqlQuery = " and ld.product_id IN (:ids)";
		} 
		else if(category.equals("zone")){
			param = Collections.singletonMap("ids",getDealersByZoneId(zoneVm.getName())); 
			sqlQuery = " and l.dealer_id IN (:ids)";
		}
		else if(category.equals("state")){
			param = Collections.singletonMap("ids",getDealersByStateId(zoneVm.getName())); 
			sqlQuery = " and l.dealer_id IN (:ids)";
		}
		AuthUser user = Utils.getLoggedInUser();
		String proZone = "";
		if(user.getEntityName().equals("Category Manager") || user.getEntityName().equals("Sellout-Regional")){
			String sqlProduct = "select product.id from product where product.id IN (SELECT user_product.products_id from user_product WHERE user_product.User_id = "+user.getEntityId()+") ";
			proZone = " and ld.product_id IN ("+sqlProduct+") ";
		} 
		NamedParameterJdbcTemplate  namedParameterJdbcTemplate = new  
				NamedParameterJdbcTemplate(jt.getDataSource());
		String sql = "SELECT COUNT(*) FROM lead l, leadDetails ld  where ld.id = l.leadDetails_id "
				+ proZone + " and (disposition1 = 'New' or disposition2 IN('Call Back','Quote Sent','Visiting Store','Not Contacted'))"
				+ sqlQuery +" and l.lastDispo1ModifiedDate > '"+new SimpleDateFormat("yyyy-MM-dd").format(start)+"' and  l.lastDispo1ModifiedDate < '"+new SimpleDateFormat("yyyy-MM-dd").format(getDate(end))+"'";
		return namedParameterJdbcTemplate.queryForInt(sql, param);
	}

	private Integer getSplineCount(ZoneVM zoneVM, Date start, Date end, String status, String category) {
		if(category.equals("state")){
			return getSplineCountDealerIds(getDealersByStateId(zoneVM.getName()), start, end, status, category);
		} else if(category.equals("zone")){
			return getSplineCountDealerIds(getDealersByZoneId(zoneVM.getName()), start, end, status, category);
		}
		return null;
		
	}
	
	private Integer getSplineCountDealerIds(List<Long> ids, Date start, Date end, String status, String category){
		Map<String, List<Long>> param = Collections.singletonMap("ids",ids); 
		NamedParameterJdbcTemplate  namedParameterJdbcTemplate = new  
				NamedParameterJdbcTemplate(jt.getDataSource());
		AuthUser user = Utils.getLoggedInUser();
		String proZone = "";
		if(user.getEntityName().equals("Category Manager") || user.getEntityName().equals("Sellout-Regional")){
			String sqlProduct = "select product.id from product where product.id IN (SELECT user_product.products_id from user_product WHERE user_product.User_id = "+user.getEntityId()+") ";
			proZone = " and ld.product_id IN ("+sqlProduct+") ";
		} 
		String sql = "SELECT COUNT(*) FROM lead l, leadDetails ld  where ld.id = l.leadDetails_id and disposition2 = '"+status+"'"
				+ proZone+ " and l.dealer_id IN (:ids) and l.lastDispo1ModifiedDate > '"+new SimpleDateFormat("yyyy-MM-dd").format(start)+"' and  l.lastDispo1ModifiedDate < '"+new SimpleDateFormat("yyyy-MM-dd").format(getDate(end))+"'";
		return namedParameterJdbcTemplate.queryForInt(sql, param);
	}
	
	private List<Long> getDealersByZoneId(String zone){
		List<Long> ids = jt.queryForList("Select dealer.id FROM dealer where dealer.zone = '"+zone+"'", Long.class);
		if(ids.size() == 0){
			ids.add(0L);
		}
		return ids;
	}
	
	private List<Long> getDealersByStateId(String state){
		List<Long> ids = jt.queryForList("Select dealer.id FROM dealer where dealer.state = '"+state+"'", Long.class);
		if(ids.size() == 0){
			ids.add(0L);
		}
		return ids;
	}
	
	private Date getDate(Date end){
		Calendar cal = Calendar.getInstance();
		cal.setTime(end);
		cal.add(Calendar.DATE, 1);
		return cal.getTime();
	}
}
