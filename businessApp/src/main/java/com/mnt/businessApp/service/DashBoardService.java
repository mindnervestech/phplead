package com.mnt.businessApp.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.mnt.businessApp.viewmodel.DashBoardProgressBarVm;
import com.mnt.businessApp.viewmodel.SplineVM;
import com.mnt.businessApp.viewmodel.ZoneVM;
import com.mnt.entities.authentication.AuthUser;

@Service
public class DashBoardService {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private JdbcTemplate jt;

	public List<Map> getDashboardProgressbar() {
		AuthUser user = ((AuthUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		switch(user.getEntityName()){
			case "Dealer":
				return getProgressBarForDealer(user);
				
			case "RSM":
				return getProgressBarForRSM(user);
				
			case "ZSM":
			case "Sellout Manager":
				return getProgressBarForZSM(user);
				
			case "Sellout Contact":
				return getProgressBarForSelloutContact(user);
				
			case "Category Manager":
				return getProgressBarForCM(user);
					
		}
		return null;
	}
	
	private List<Map> getProgressBarForDealer(AuthUser user){
		String sql = "SELECT COUNT(*) FROM Lead where dealer_id = ?";
		List<Map> list = new ArrayList<>();
		int total = jt.queryForInt(sql, new Object[] { user.getEntityId() });
		List<Long> ids = new ArrayList<>();
		ids.add(user.getEntityId());
		sql = "SELECT COUNT(*) FROM Lead where disposition1 = 'Escalated' and dealer_id IN (:ids)";
		list.add(getLeadProgressBarVM(sql, "Escalated", "icon fa fa-suitcase", "warning", total,ids));
		return getProgressBarByDealers(list,total,ids);
	}
	
	private List<Map> getProgressBarForRSM(AuthUser user){
		List<Long> ids = new ArrayList<>();
		ids = jt.queryForList("Select dealer.id FROM dealer where dealer.rsm_id = "+user.getEntityId(), Long.class);
		String sql = "SELECT COUNT(*) FROM Lead where dealer_id IN (:ids)";
		List<Map> list = new ArrayList<>();
		Map<String, List<Long>> param = Collections.singletonMap("ids",ids); 
		NamedParameterJdbcTemplate  namedParameterJdbcTemplate = new  
				NamedParameterJdbcTemplate(jt.getDataSource());
		int total = namedParameterJdbcTemplate.queryForInt(sql, param);
		sql = "SELECT COUNT(*) FROM Lead where disposition1 = 'Escalated' and escalatedLevel = 2 and"
				+ " dealer_id IN (:ids)";
		list.add(getLeadProgressBarVM(sql, "Escalated", "icon fa fa-suitcase", "warning", total,ids));
		return getProgressBarByDealers(list,total,ids);
	}
	
	private List<Map> getProgressBarForZSM(AuthUser user){
		List<Long> ids = new ArrayList<>();
		ids = jt.queryForList("Select dealer.id FROM dealer where dealer.zone = (Select user.zone_id from user WHERE user.id = "+user.getEntityId()+")", Long.class);
		String sql = "SELECT COUNT(*) FROM Lead where dealer_id IN (:ids)";
		List<Map> list = new ArrayList<>();
		Map<String, List<Long>> param = Collections.singletonMap("ids",ids); 
		NamedParameterJdbcTemplate  namedParameterJdbcTemplate = new  
				NamedParameterJdbcTemplate(jt.getDataSource());
		int total = namedParameterJdbcTemplate.queryForInt(sql, param);
		sql = "SELECT COUNT(*) FROM Lead where disposition1 = 'Escalated' and escalatedLevel = 3 and"
				+ " dealer_id IN (:ids)";
		list.add(getLeadProgressBarVM(sql, "Escalated", "icon fa fa-suitcase", "warning", total,ids));
		return getProgressBarByDealers(list,total,ids);
	}
	
	private List<Map> getProgressBarForSelloutContact(AuthUser user){
		List<Long> ids = new ArrayList<>();
		ids = jt.queryForList("Select dealer.id FROM dealer where dealer.zone = "
				+ "(Select user.zone_id from user WHERE user.id = "+user.getEntityId()+")", Long.class);
		String sql = "SELECT COUNT(*) FROM Lead where dealer_id IN (:ids)";
		List<Map> list = new ArrayList<>();
		Map<String, List<Long>> param = Collections.singletonMap("ids",ids); 
		NamedParameterJdbcTemplate  namedParameterJdbcTemplate = new  
				NamedParameterJdbcTemplate(jt.getDataSource());
		int total = namedParameterJdbcTemplate.queryForInt(sql, param);
		sql = "SELECT COUNT(*) FROM Lead where disposition1 = 'Escalated' and escalatedLevel = 2"
				+ " and dealer_id IN (:ids)";
		list.add(getLeadProgressBarVM(sql, "Escalated", "icon fa fa-suitcase", "warning", total,ids));
		return getProgressBarByDealers(list,total,ids);
	}
	
	private List<Map> getProgressBarForCM(AuthUser user){
		List<Long> ids = new ArrayList<>();
		ids = jt.queryForList(" select products_id  from user_product  where User_id = "+user.getEntityId(), Long.class);
		String sql = "SELECT COUNT(*) FROM lead l, leadDetails ld  where ld.id = l.leadDetails_id"
				+ " and ld.product_id IN (:ids)";
		List<Map> list = new ArrayList<>();
		Map<String, List<Long>> param = Collections.singletonMap("ids",ids); 
		NamedParameterJdbcTemplate  namedParameterJdbcTemplate = new  
				NamedParameterJdbcTemplate(jt.getDataSource());
		int total = namedParameterJdbcTemplate.queryForInt(sql, param);
		sql = "SELECT COUNT(*) FROM lead l, leadDetails ld  where ld.id = l.leadDetails_id"
				+ " and l.disposition1 = 'Escalated' and ld.product_id IN (:ids)";
		list.add(getLeadProgressBarVM(sql, "Escalated", "icon fa fa-suitcase", "warning", total,ids));
		return getProgressBarByProducts(list,total,ids);
	}
	
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

	private List<Map> getProgressBarByDealers(List<Map> list, int total, List<Long> ids){
		String sql = "SELECT COUNT(*) FROM Lead where ( disposition1 = 'New' "
				+ "or disposition2 IN('Call Back','Quote Sent','Visiting Store','Not Contacted') ) and dealer_id IN (:ids)";
		list.add(getLeadProgressBarVM(sql, "Open", "icon fa fa-folder-open", "warning", total,ids));
		sql = "SELECT COUNT(*) FROM Lead where disposition2 = 'Won' and dealer_id IN (:ids)";
		list.add(getLeadProgressBarVM(sql, "Won", "icon fa fa-thumbs-up", "success", total,ids));
		sql = "SELECT COUNT(*) FROM Lead where disposition2 = 'Lost' and dealer_id IN (:ids)";
		list.add(getLeadProgressBarVM(sql, "Lost", "icon fa fa-thumbs-down", "danger", total,ids));
		return list;
	}


	private Map getLeadProgressBarVM(String sql, String description, String iconClass, String type, int total,List<Long> ids) {
		Map<String, DashBoardProgressBarVm> map = new HashMap<>();
		if(ids.size() == 0){
			return null;
		}
		Map<String, List<Long>> param = Collections.singletonMap("ids",ids); 
		DashBoardProgressBarVm vm = new DashBoardProgressBarVm();
		NamedParameterJdbcTemplate  namedParameterJdbcTemplate = new  
				NamedParameterJdbcTemplate(jt.getDataSource());
		int actualValue = namedParameterJdbcTemplate.queryForInt(sql, param);
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

	public List<Map> getDashboardProgressbar(Long zone, Long product) {
		List<Long> ids = new ArrayList<>();
		String proZone;
		ids = jt.queryForList("Select dealer.id FROM dealer where dealer.zone = "+zone, Long.class);
		
		if(product == 0 ){
			proZone = " and l.dealer_id IN (:ids)";
		} else{
			proZone = " and ld.product_id ="+product+" and l.dealer_id IN (:ids)";
		}
		if(ids.size() == 0){
			ids = jt.queryForList("Select dealer.id FROM dealer", Long.class);
		}
		String sql = "SELECT COUNT(*) FROM lead l, leadDetails ld  where ld.id = l.leadDetails_id"
				+ proZone;
		List<Map> list = new ArrayList<>();
		Map<String, List<Long>> param = Collections.singletonMap("ids",ids); 
		NamedParameterJdbcTemplate  namedParameterJdbcTemplate = new  
				NamedParameterJdbcTemplate(jt.getDataSource());
		int total = namedParameterJdbcTemplate.queryForInt(sql, param);
		sql = "SELECT COUNT(*) FROM lead l, leadDetails ld  where ld.id = l.leadDetails_id"
				+ " and l.disposition1 = 'Escalated' "
				+ proZone;
		list.add(getLeadProgressBarVM(sql, "Escalated", "icon fa fa-suitcase", "warning", total,ids));
		sql = "SELECT COUNT(*) FROM lead l, leadDetails ld  where ld.id = l.leadDetails_id "
				+ "and (disposition1 = 'New' or disposition2 IN('Call Back','Quote Sent','Visiting Store','Not Contacted'))"
				+proZone;
		list.add(getLeadProgressBarVM(sql, "Open", "icon fa fa-folder-open", "warning", total,ids));
		sql = "SELECT COUNT(*) FROM lead l, leadDetails ld  where ld.id = l.leadDetails_id and disposition2 = 'Won'"
				+ proZone;
		list.add(getLeadProgressBarVM(sql, "Won", "icon fa fa-thumbs-up", "success", total,ids));
		sql = "SELECT COUNT(*) FROM lead l, leadDetails ld  where ld.id = l.leadDetails_id and disposition2 = 'Lost'"
				+ proZone;
		list.add(getLeadProgressBarVM(sql, "Lost", "icon fa fa-thumbs-down", "danger", total,ids));
		return list;
	}

	public List<Map> getDashboardProgressbar(Date start, Date end, Long zone,
			Long product) {
		AuthUser user = ((AuthUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		String sql = "";
		List<Long> ids = new ArrayList<>();
		String sqlDate = " and l.lastDispo1ModifiedDate > '"+new SimpleDateFormat("yyyy-MM-dd").format(start)+"' and  l.lastDispo1ModifiedDate < '"+new SimpleDateFormat("yyyy-MM-dd").format(end)+"'";
		String proZone = "";
		String escalatationlevel = "";
		if(user.getEntityName().equals("Dealer")){
			ids.add(user.getEntityId());
		}  
		if(user.getEntityName().equals("RSM")){
			ids = jt.queryForList("select d.id  from dealer as d  where d.rsm_id = "+user.getEntityId(), Long.class);
			escalatationlevel = " and l.escalatedLevel = 1 ";
		}
		if(user.getEntityName().equals("ZSM") || user.getEntityName().equals("Sellout Contact") || user.getEntityName().equals("Sellout Manager")){
			ids = jt.queryForList("select d.id  from dealer as d where d.zone = (Select user.zone_id from user WHERE user.id = "+user.getEntityId()+")", Long.class);
			escalatationlevel = " and l.escalatedLevel = 2 ";
		}
		if(user.getEntityName().equals("Category Manager") || user.getEntityName().equals("Sellout-Regional")){
			sql = "select product.id from product where product.id IN (SELECT user_product.products_id from user_product WHERE user_product.User_id = "+user.getEntityId()+") ";
			proZone = " and ld.product_id IN ("+sql+")";
			ids = jt.queryForList("Select dealer.id FROM dealer where dealer.zone = "+zone, Long.class);
			if(ids.size() == 0){
				ids = jt.queryForList("Select dealer.id FROM dealer", Long.class);
			}
		}
		if(user.getEntityName().equals("General Manager") || user.getEntityName().equals("CEO")){
			sql = "select product.id from product";
			proZone = " and ld.product_id IN ("+sql+")";
			ids = jt.queryForList("Select dealer.id FROM dealer where dealer.zone = "+zone, Long.class);
			if(ids.size() == 0){
				ids = jt.queryForList("Select dealer.id FROM dealer", Long.class);
			}
		}
		if(product != 0){
			proZone = " and ld.product_id ="+product;
			ids = jt.queryForList("Select dealer.id FROM dealer where dealer.zone = "+zone, Long.class);
			if(ids.size() == 0){
				ids = jt.queryForList("Select dealer.id FROM dealer", Long.class);
			}
		}
		sql = "SELECT COUNT(*) FROM lead l, leadDetails ld  where ld.id = l.leadDetails_id and l.dealer_id IN (:ids)"+sqlDate
				+ proZone;
		List<Map> list = new ArrayList<>();
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
		list.add(getLeadProgressBarVM(sql, "Lost Leads", "icon fa fa-thumbs-down", "danger", total,ids));
		return list;
	}

	public Map getZoneSplineBetweenDates(Date start, Date end) {
		List<Map<String, Object>> rows = jt.queryForList("select * from zone");
		List<ZoneVM> zoneList = new ArrayList<ZoneVM>();
		Map<String,List> dataList = new HashMap<String, List>();
		for(Map map : rows) {
			if(map.get("name").equals("Corporate"))
				continue;
			ZoneVM vm = new ZoneVM();
			vm.id = (Long) map.get("id");
			vm.name = (String) map.get("name");
			zoneList.add(vm);
		}
		List<SplineVM> splineVMs = new ArrayList<>(); 
		splineVMs.add(getSplineData(start, end, zoneList, "Won", "#01c6ad", "zone"));
		splineVMs.add(getSplineData(start, end, zoneList, "Lost", "#FF0000", "zone"));
		splineVMs.add(getSplineData(start, end, zoneList, "Open", "#ffce54", "zone"));
		Map<String, List<SplineVM>> map = new HashMap<>();
		map.put("dataset", splineVMs);
		return map;
	}
	
	public Map getProductSplineBetweenDates(Date start, Date end) {
		AuthUser user = ((AuthUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		Map<String, List<SplineVM>> map = new HashMap<>();
		String sql = "";
		if(user.getEntityName().equals("Category Manager") || user.getEntityName().equals("Sellout-Regional")){
			sql = "select * from product where product.id IN (SELECT user_product.products_id from user_product WHERE user_product.User_id = "+user.getEntityId()+") ";
		} else 
		if(user.getEntityName().equals("General Manager") || user.getEntityName().equals("CEO")){
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
		AuthUser user = ((AuthUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		List<Long> ids = new ArrayList<>();
		if(user.getEntityName().equals("Dealer")){
			ids.add(user.getEntityId());
		}
		if(user.getEntityName().equals("RSM")){
			ids = jt.queryForList("Select dealer.id FROM dealer where dealer.rsm_id = "+user.getEntityId(), Long.class);
		}
		return getDealerSplineVM(start, end, cat, color, ids, query);
	}

	private SplineVM getDealerSplineVM(Date startDate, Date endDate, String cat,
			String color, List<Long> ids, String query) {
		Map<String, List<Long>> param = Collections.singletonMap("ids",ids); 
		NamedParameterJdbcTemplate  namedParameterJdbcTemplate = new  
				NamedParameterJdbcTemplate(jt.getDataSource());
		SplineVM vm = new SplineVM();
		List<List> list = new ArrayList<>();
		List<Map<String, Object>> rows = namedParameterJdbcTemplate.queryForList("SELECT COUNT(*) as count, l.lastDispo1ModifiedDate as date from lead as l "
				+ " where l.lastDispo1ModifiedDate > '"+new SimpleDateFormat("yyyy-MM-dd").format(startDate)+"' "
				+ " and  l.lastDispo1ModifiedDate < '"+new SimpleDateFormat("yyyy-MM-dd").format(endDate)+"' "
				+ " and l.dealer_id IN (:ids)"+query
				+ " GROUP BY CAST(l.lastDispo1ModifiedDate  AS DATE) "
				+ " ORDER BY CAST(l.lastDispo1ModifiedDate  AS DATE) asc",param);
		List<ZoneVM> zoneList = new ArrayList<ZoneVM>();
		Map<String,List> dataList = new HashMap<String, List>();
		
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
				innerlist.add(getSplineCountForOpen(zoneVM.getId(), start, end, category)); 
			} else {
				innerlist.add(getSplineCount(zoneVM.getId(), start, end, leadCat, category)); 
			}
			list.add(innerlist);
		}
		vm.setData(list);
		vm.setColor(color);
		vm.setLabel(leadCat);
		return vm;
	}

	private Object getSplineCountForOpen(Long id, Date start, Date end, String category) {
		Map<String, List<Long>> param = null;
		String sqlQuery = "";
		if(category.equals("product")){
			List<Long> ids = new ArrayList<>();
			ids.add(id);
			param = Collections.singletonMap("ids",ids); 
			sqlQuery = " and ld.product_id IN (:ids)";
		} 
		if(category.equals("zone")){
			param = Collections.singletonMap("ids",getDealersByZoneId(id)); 
			sqlQuery = " and l.dealer_id IN (:ids)";
		}
		NamedParameterJdbcTemplate  namedParameterJdbcTemplate = new  
				NamedParameterJdbcTemplate(jt.getDataSource());
		String sql = "SELECT COUNT(*) FROM lead l, leadDetails ld  where ld.id = l.leadDetails_id "
				+ "and (disposition1 = 'New' or disposition2 IN('Call Back','Quote Sent','Visiting Store','Not Contacted'))"
				+ sqlQuery+" and l.lastDispo1ModifiedDate > '"+new SimpleDateFormat("yyyy-MM-dd").format(start)+"' and  l.lastDispo1ModifiedDate < '"+new SimpleDateFormat("yyyy-MM-dd").format(end)+"'";
		return namedParameterJdbcTemplate.queryForInt(sql, param);
	}

	private Integer getSplineCount(Long id, Date start, Date end, String status, String category) {
		return getSplineCountDealerIds(getDealersByZoneId(id), start, end, status, category);
	}
	
	private Integer getSplineCountDealerIds(List<Long> ids, Date start, Date end, String status, String category){
		Map<String, List<Long>> param = Collections.singletonMap("ids",ids); 
		NamedParameterJdbcTemplate  namedParameterJdbcTemplate = new  
				NamedParameterJdbcTemplate(jt.getDataSource());
		String sql = "SELECT COUNT(*) FROM lead l, leadDetails ld  where ld.id = l.leadDetails_id and disposition2 = '"+status+"'"
				+ "and l.dealer_id IN (:ids) and l.lastDispo1ModifiedDate > '"+new SimpleDateFormat("yyyy-MM-dd").format(start)+"' and  l.lastDispo1ModifiedDate < '"+new SimpleDateFormat("yyyy-MM-dd").format(end)+"'";
		return namedParameterJdbcTemplate.queryForInt(sql, param);
	}
	
	private List<Long> getDealersByZoneId(Long zone){
		List<Long> ids = jt.queryForList("Select dealer.id FROM dealer where dealer.zone = "+zone, Long.class);
		if(ids.size() == 0){
			ids.add(0L);
		}
		return ids;
	}

}
