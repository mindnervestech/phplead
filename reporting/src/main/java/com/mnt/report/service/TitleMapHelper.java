package com.mnt.report.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class TitleMapHelper {


	public static String allDN_DECOMPANY_ID(JdbcTemplate jt) {
    	List<JSONObject> list = jt.query("select P.DN_ID AS value, P.DC_COMPANY_NAME AS name  from tbl_de_company P", new RowMapper<JSONObject>(){

			public JSONObject mapRow(ResultSet rs, int arg1)
					throws SQLException {
				JSONObject nameValue = new JSONObject();
				nameValue.put("name", rs.getObject("name").toString());
				nameValue.put("value",  rs.getObject("value").toString());
				return nameValue;
			}
		});
		return JSONArray.toJSONString(list);
	}
	
	public static String allDC_ADVERTISER_TYPE(JdbcTemplate jt) {
    	List<JSONObject> list = jt.query("SELECT P.DN_ID AS value, P.DC_PUBLICATION_TITLE AS name FROM tbl_publication P where P.DC_PUBLICATION_TYPE = 5", new RowMapper<JSONObject>(){

			public JSONObject mapRow(ResultSet rs, int arg1)
					throws SQLException {
				JSONObject nameValue = new JSONObject();
				nameValue.put("name", rs.getObject("name").toString());
				nameValue.put("value",  rs.getObject("value").toString());
				return nameValue;
			}
		});
		return JSONArray.toJSONString(list);
	}
	
	public static String allDC_PUBLICATION_TITLE(JdbcTemplate jt) {
    	List<JSONObject> list = jt.query("SELECT P.DN_ID AS value, P.DC_PUBLICATION_TITLE AS name FROM tbl_publication P where P.DC_PUBLICATION_TYPE = 2", new RowMapper<JSONObject>(){

			public JSONObject mapRow(ResultSet rs, int arg1)
					throws SQLException {
				JSONObject nameValue = new JSONObject();
				nameValue.put("name", rs.getObject("name").toString());
				nameValue.put("value",  rs.getObject("value").toString());
				return nameValue;
			}
		});
		return JSONArray.toJSONString(list);
	}
    
	public static String allDC_AD_CATEGORY(JdbcTemplate jt) {
    	List<JSONObject> list = jt.query("SELECT P.DN_ID AS value, P.DC_PUBLICATION_TITLE AS name FROM tbl_publication P where P.DC_PUBLICATION_TYPE = 4", new RowMapper<JSONObject>(){

			public JSONObject mapRow(ResultSet rs, int arg1)
					throws SQLException {
				JSONObject nameValue = new JSONObject();
				nameValue.put("name", rs.getObject("name").toString());
				nameValue.put("value",  rs.getObject("value").toString());
				return nameValue;
			}
		});
		return JSONArray.toJSONString(list);
	}
	
	public static String allDC_SEARCH_ADVERTISER_TYPE(JdbcTemplate jt) {
		List<JSONObject> list = jt.query("SELECT P.DN_ID AS value, P.DC_PUBLICATION_TITLE AS name FROM tbl_publication P where P.DC_PUBLICATION_TYPE = 6", new RowMapper<JSONObject>(){

			public JSONObject mapRow(ResultSet rs, int arg1)
					throws SQLException {
				JSONObject nameValue = new JSONObject();
				nameValue.put("name", rs.getObject("name").toString());
				nameValue.put("value",  rs.getObject("value").toString());
				return nameValue;
			}
		});
		return JSONArray.toJSONString(list);
	}
}
