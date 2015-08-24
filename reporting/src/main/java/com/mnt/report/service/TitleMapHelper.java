package com.mnt.report.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.mnt.businessApp.service.Utils;
import com.mnt.entities.authentication.AuthUser;
import com.mnt.entities.businessApp.User;

public class TitleMapHelper {


	public static String allZONE_PT(JdbcTemplate jt) {
		AuthUser user = Utils.getLoggedInUser();
		if(user.getEntityName().equalsIgnoreCase("ZSM") || user.getEntityName().equalsIgnoreCase("Sellout Manager")){
			List<JSONObject> list = jt.query("select user.zone AS value, user.zone AS name  from user where user.id = "+user.getEntityId(), new ProductZoneRowMapper());
			return JSONArray.toJSONString(list);
		}
    	List<JSONObject> list = jt.query("select DISTINCT(Z.zone) AS value, Z.zone AS name  from zipcode Z", new ProductZoneRowMapper());
    	return JSONArray.toJSONString(list);
	}
	
	public static String allPRODUCT_PT(JdbcTemplate jt) {
		AuthUser user = Utils.getLoggedInUser();
		if(user.getEntityName().equalsIgnoreCase("ZSM")  || user.getEntityName().equalsIgnoreCase("Sellout Manager") || user.getEntityName().equalsIgnoreCase("Category Manager")){
			List<JSONObject> list = jt.query("select P.id AS value, P.name AS name  from product P, user_product where P.id = user_product.products_id  and user_product.User_id = "+user.getEntityId(), new ProductZoneRowMapper());
			return JSONArray.toJSONString(list);
		}
    	List<JSONObject> list = jt.query("select P.id AS value, P.name AS name  from product P", new ProductZoneRowMapper());
		return JSONArray.toJSONString(list);
	}
	
	public static List<JSONObject> ZONE_PT(JdbcTemplate jt) {
		AuthUser user = Utils.getLoggedInUser();
		if(user.getEntityName().equalsIgnoreCase("ZSM") || user.getEntityName().equalsIgnoreCase("Sellout Manager")){
			List<JSONObject> list = jt.query("select user.zone AS value, user.zone AS name  from user where user.id = "+user.getEntityId(), new ProductZoneRowMapper());
			return list;
		}
    	List<JSONObject> list = jt.query("select DISTINCT(Z.zone) AS value, Z.zone AS name  from zipcode Z", new ProductZoneRowMapper());
    	return list;
	}
	
	public static List<JSONObject> PRODUCT_PT(JdbcTemplate jt) {
		AuthUser user = Utils.getLoggedInUser();
		if(user.getEntityName().equalsIgnoreCase("ZSM")  || user.getEntityName().equalsIgnoreCase("Sellout Manager") || user.getEntityName().equalsIgnoreCase("Category Manager")){
			List<JSONObject> list = jt.query("select P.id AS value, P.name AS name  from product P, user_product where P.id = user_product.products_id  and user_product.User_id = "+user.getEntityId(), new ProductZoneRowMapper());
			return list;
		}
    	List<JSONObject> list = jt.query("select P.id AS value, P.name AS name  from product P", new ProductZoneRowMapper());
		return list;
	}
	
	static class ProductZoneRowMapper implements RowMapper
	{
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			JSONObject nameValue = new JSONObject();
			nameValue.put("name", rs.getObject("name").toString());
			nameValue.put("value",  rs.getObject("value").toString());
			return nameValue;
		}
		
	}
	
}