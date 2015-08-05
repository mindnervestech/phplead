package com.mnt.report.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class TitleMapHelper {


	public static String allZONE_PT(JdbcTemplate jt) {
    	List<JSONObject> list = jt.query("select Z.id AS value, Z.name AS name  from zone Z", new RowMapper<JSONObject>(){

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
	
	public static String allPRODUCT_PT(JdbcTemplate jt) {
    	List<JSONObject> list = jt.query("select P.id AS value, P.name AS name  from product P", new RowMapper<JSONObject>(){

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