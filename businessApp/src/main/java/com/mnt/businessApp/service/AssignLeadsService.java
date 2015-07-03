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


@Service
public class AssignLeadsService
{
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private JdbcTemplate jt;
	
	public Map<String,Map<String, List<Long>>> getProductUserMapping() {
		String sql = "select up.products_id as product , entityName, user.id from user, user_product up   where user.id = up.user_id and entityName IN ('RSM', 'TSR', 'Sales Consultant') UNION ";
		sql += " select up.products_id as product, 'Dealer' as entityName, dealer.id from dealer, dealer_product up where dealer.id = up.dealer_id ";
		return getUserMapping(sql);
	}

	public Map<String,Map<String, List<Long>>> getZipCodeUserMapping() {
		String sql = "select up.zipcode_id as product , entityName, user.id from user, user_zipcode up where user.id = up.user_id and entityName IN ('RSM', 'TSR', 'Sales Consultant') UNION ";
		sql += " select up.zipCode_id as product, 'Dealer' as entityName, dealer.id from dealer, dealerconfiguration up where dealer.id = up.dealer_id ";
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
		DealerAllotmentWFStep allotmentWFStep = new DealerAllotmentWFStep("603502", "5", 81);
		allotmentWFStep.jt = jt;
		allotmentWFStep.startAssignment();
		
	}
	
	
 
}
