package com.mnt.businessApp.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.mnt.businessApp.engine.AllotmentEngineCache;
import com.mnt.businessApp.engine.DealerAllotmentWFStep;
import com.mnt.businessApp.engine.SelloutExecutiveAllotmentWFStep;


@Service
public class AssignLeadsService
{
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private JdbcTemplate jt;
	
	public Map<String,Map<String, List<Long>>> getProductUserMapping() {
		String sql = "select up.products_id as product , entityName, user.id from user, user_product up   where user.id = up.user_id and entityName IN ('RSM', 'Sellout-Regional', 'Sales Consultant', 'Dealer', 'ZSM', 'Sales Executive', 'Sellout Manager')  ";
		return getUserMapping(sql);
	}

	public Map<String,Map<String, List<Long>>> getZipCodeUserMapping() {
		String sql = "select up.zipCodes_id as product , entityName, user.id from user, user_zipcode up where user.id = up.user_id and entityName IN ('RSM', 'Sellout-Regional', 'Sales Consultant', 'Dealer', 'ZSM', 'Sales Executive', 'Sellout Manager')  ";
		return getUserMapping(sql);
	}
	
	private Map<String, Map<String, List<Long>>> getUserMapping(String sql) {
		List<Map<String, Object>> rows = jt.queryForList(sql);
		Map<String,Map<String, List<Long>>> result = new HashMap<>();
		for(Map map : rows) {
			String product = (String) map.get("product").toString();
			String entityName = (String) map.get("entityName");
			Map<String, List<Long>> productMap = result.get(product);
			if(productMap == null){
				productMap = new HashMap<>();
			}
			List<Long> ids = productMap.get(entityName);
			if(ids == null || ids.size() == 0){
				ids = new ArrayList<>(); 
			}
			ids.add((Long) map.get("id"));
			productMap.put(entityName, ids);
			result.put(product, productMap);
		}
		return result;
	}
	
	public void assignDealer(){
		List<Map<String, Object>> rows = jt.queryForList("select ld.pinCode as zipcode, l.id as id, ld.product_id as product from lead l, leaddetails ld where ld.id = l.leadDetails_id and ld.categorization IN ('Warm', 'Hot', 'Cold')");
		for(Map<String, Object> row : rows){
			DealerAllotmentWFStep allotmentWFStep = new DealerAllotmentWFStep((String) row.get("zipcode"), (String) row.get("product").toString(), (Long) row.get("id"));
			allotmentWFStep.jt = jt;
			allotmentWFStep.status = "assignment";
			allotmentWFStep.startAssignment();
		}
	}
	
	
 
}
