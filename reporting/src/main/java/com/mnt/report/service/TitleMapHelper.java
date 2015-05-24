package com.mnt.report.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class TitleMapHelper {

	public static String allusers(JdbcTemplate jt) {
		return department(jt);
	}
	
	public static String department(JdbcTemplate jt) {
		List<JSONObject> list = jt.query("select * from authusers", new RowMapper<JSONObject>(){

			public JSONObject mapRow(ResultSet rs, int arg1)
					throws SQLException {
				JSONObject nameValue = new JSONObject();
				nameValue.put("name", rs.getObject("username").toString());
				nameValue.put("value",  rs.getObject("auth_id").toString());
				return nameValue;
			}
		});
		//JSONArray array = new JSONArray();
		//for(JSONObject obj : list) {
			return JSONArray.toJSONString(list);
		//}
	}
}
