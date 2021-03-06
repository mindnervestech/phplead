package com.mnt.report.service;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
/*import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;*/


import com.mnt.businessApp.service.Utils;
import com.mnt.entities.authentication.AuthUser;

@Controller
@RequestMapping(value="/api/report")
public class ReportMDService {

	@Autowired
	private JdbcTemplate jt;

	@Autowired
	NamedParameterJdbcTemplate namedJdbcTemplate;


	@RequestMapping(value="/reports/drildownreport",method=RequestMethod.POST)
	@ResponseBody
	public JSONObject drildownreport(@RequestBody String report) {
		JSONObject resp = new JSONObject();
		try {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject)parser.parse(report);
			System.out.println("id:"+((JSONObject)jsonObject.get("searchCriteria")).get("id"));
			final Map<String,Object> mdResult = jt.queryForMap("Select columns,query,hiddenpivotcol from reportmd where id =" + ((JSONObject)jsonObject.get("searchCriteria")).get("id"));
			String columns = mdResult.get("columns").toString();
			JSONArray colArr = ((JSONArray)parser.parse(columns));
			JSONObject filterObj = (JSONObject)jsonObject.get("filters");
			String drilFilter = "";
			for(String filterKey:((Set<String>)filterObj.keySet())) {
				for(Object column :colArr) {
					JSONObject colObj = (JSONObject)column;
					if(colObj.get("data").equals(filterKey)) {
						if(drilFilter.isEmpty()) 
							drilFilter += colObj.get("column")+"='"+filterObj.get(filterKey)+"' ";
						else 
							drilFilter += "and "+ colObj.get("column")+"='"+filterObj.get(filterKey)+"' ";
					}
				}
			}
			String query = mdResult.get("query").toString();	
			String[] namedParameters =  query.split(":");
			Map<String, Object> parameters = new HashMap<String, Object>();

			// This is imp to set all named params to null 
			for(String param : namedParameters) {
				for(int i = 0 ; i < param.length() ; i++) {
					if(param.charAt(i) == ' ' || param.charAt(i) == ')' || param.charAt(i) == '\n' || param.charAt(i) == '\t'|| param.charAt(i) == '\r') {
						parameters.put(param.substring(0, i), null);
						break;
					}
				}
			}

			for(Object key : ((JSONObject)jsonObject.get("searchCriteria")).keySet()){
				Object value = ((JSONObject)jsonObject.get("searchCriteria")).get(key);
				if (value instanceof String) {
					if(!value.equals(""))
						parameters.put(key.toString(), value);
				}

				if (value instanceof JSONArray) {
					JSONArray jsonArray1 = (JSONArray) value;
					int len = jsonArray1.size();
					if(len > 0) {
						List<String> inValues = new ArrayList<String>();
						for (int i=0;i<len;i++){ 
							inValues.add(jsonArray1.get(i).toString());
						}
						parameters.put(key.toString()+"in", inValues);
						parameters.put(key.toString(), "");
					}
				}
			}
			if(query.indexOf("and")!=-1 || query.indexOf("AND")!=-1) {
				query +=" and "+drilFilter;
			} else {
				query += drilFilter;
			}
			List<Map<String, Object>> rs = namedJdbcTemplate.queryForList(query,parameters);
			resp.put("data" , rs);

			JSONArray columns1 = ((JSONArray)new JSONParser().parse(mdResult.get("columns").toString()));
			resp.put("columns" , columns1);

			JSONArray hiddenpivotcol = ((JSONArray)new JSONParser().parse(mdResult.get("hiddenpivotcol").toString()));
			resp.put("hiddenpivotcol",hiddenpivotcol);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return resp;
	}
	@RequestMapping(value="/saveTemplate",method=RequestMethod.POST)
	@ResponseBody
	public JSONObject saveTemplate(@RequestBody final ReportTemplateVM reportTemplateVM) {
		final AuthUser user = Utils.getLoggedInUser();
		JSONObject resp = new JSONObject();
		try {
			final Map<String,Object> mdResult = jt.queryForMap("Select query,columns,hiddenpivotcol,jsonForm,jsonSchema from reportmd where id =" + reportTemplateVM.parentId);//.query("Select query from reportmd where id = ?",new Object[]{id},);		
			KeyHolder keyHolder=new GeneratedKeyHolder();
			jt.update(new PreparedStatementCreator(){
				public PreparedStatement createPreparedStatement(    Connection connection) throws SQLException {
					PreparedStatement ps=connection.prepareStatement("Insert into reportmd(columns,description,jsonForm,jsonSchema,name,query,hiddenpivotcol,pivotConfig,searchCriteria, userId, access) values(?,?,?,?,?,?,?,?,?,?,?)",new String[]{"id"});
					int index=1;
					ps.setString(index++,mdResult.get("columns").toString());
					if(reportTemplateVM.data==null)
						ps.setString(index++,"Saved table template");
					else
						ps.setString(index++,"Saved pivot template");
					ps.setString(index++,mdResult.get("jsonForm").toString());
					ps.setString(index++,mdResult.get("jsonSchema").toString());
					ps.setString(index++,reportTemplateVM.templateName);
					ps.setString(index++,mdResult.get("query").toString());
					ps.setString(index++,mdResult.get("hiddenpivotcol").toString());
					if(reportTemplateVM.data==null)
						ps.setNull(index++, java.sql.Types.NULL);
					else
						ps.setString(index++,reportTemplateVM.data);
					ps.setString(index++,reportTemplateVM.searchCriteria);
					ps.setLong(index++,user.getEntityId());
					ps.setString(index++,user.getEntityName());
					return ps;
				}
			}
			,keyHolder);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}
	
	@RequestMapping(value="/run",method=RequestMethod.GET)
	@ResponseBody
	public JSONObject runReports(@RequestParam String filter) {
		JSONObject resp = new JSONObject();
		try {
			JSONObject jsonObject = (JSONObject)new JSONParser().parse(filter);
			Long id = Long.parseLong(jsonObject.get("id").toString());
			Map<String,Object> mdResult = jt.queryForMap("Select query,columns,hiddenpivotcol from reportmd where id =" + id);//.query("Select query from reportmd where id = ?",new Object[]{id},);
			String query = mdResult.get("query").toString();	
			String[] namedParameters =  query.split(":");
			Map<String, Object> parameters = new HashMap<String, Object>();

			// This is imp to set all named params to null 
			for(String param : namedParameters) {
				for(int i = 0 ; i < param.length() ; i++) {
					if(param.charAt(i) == ' ' || param.charAt(i) == ')' || param.charAt(i) == '\n' || param.charAt(i) == '\t'|| param.charAt(i) == '\r') {
						parameters.put(param.substring(0, i), null);
						break;
					}
				}
			}

			for(Object key : jsonObject.keySet()){
				Object value = jsonObject.get(key);
				if (value instanceof String) {
					if(!value.equals(""))
						parameters.put(key.toString(), value);
				}

				if (value instanceof JSONArray) {
					JSONArray jsonArray = (JSONArray) value;
					int len = jsonArray.size();
					if(len > 0) {
						List<String> inValues = new ArrayList<String>();
						for (int i=0;i<len;i++){ 
							inValues.add(jsonArray.get(i).toString());
						}
						parameters.put(key.toString()+"in", inValues);
						parameters.put(key.toString(), "");
					} else {
						List<String> inValues = new ArrayList<String>();
						inValues.add("none");
						parameters.put(key.toString()+"in", inValues);
						parameters.put(key.toString(), "");
					}
				}
			}
			
			
			List<Map<String, Object>> rs = namedJdbcTemplate.queryForList(mdResult.get("query").toString(),parameters);

			resp.put("data" , rs);

			//generateCSV(rs);

			//generatePDF(rs);

			JSONArray columns = ((JSONArray)new JSONParser().parse(mdResult.get("columns").toString()));
			resp.put("columns" , columns);

			JSONArray hiddenpivotcol = ((JSONArray)new JSONParser().parse(mdResult.get("hiddenpivotcol").toString()));
			resp.put("hiddenpivotcol",hiddenpivotcol);
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}

	@RequestMapping(value="/deleteReport",method=RequestMethod.GET)
	@ResponseBody
	@Transactional
	public List<ReportMDVM> deleteReport(@RequestParam("id") final Long id) {
		try {
			KeyHolder keyHolder=new GeneratedKeyHolder();
			jt.update(new PreparedStatementCreator(){
				public PreparedStatement createPreparedStatement(    Connection connection) throws SQLException {
					PreparedStatement ps=connection.prepareStatement("delete from reportmd where id=?",new String[]{"id"});
					int index=1;
					ps.setLong(index++,id);
					return ps;
				}
			}
			,keyHolder);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getReports();
	}
	@RequestMapping(value="reports/md",method=RequestMethod.GET)
	@ResponseBody
	@Transactional
	public List<ReportMDVM> getReports() {
		//return sessionFactory.getCurrentSession().createQuery("FROM ReportMD1").list();
		AuthUser user = Utils.getLoggedInUser();
		return jt.query("Select * from reportmd where access Like '%"+user.getEntityName()+"%' and ( userId is null or userId = "+user.getEntityId()+" ) and (type is null or type Like '%" + user.getType() + "%')", new RowMapper<ReportMDVM>(){

			public ReportMDVM mapRow(ResultSet arg0, int arg1)
					throws SQLException {
				try {
					ReportMDVM reportMD = new ReportMDVM();
					reportMD.id = (arg0.getLong("id"));
					reportMD.jsonForm = ((JSONArray)new JSONParser().parse(buildTitleMap(arg0.getString("jsonForm"))));
					reportMD.jsonSchema = ((JSONObject)new JSONParser().parse(arg0.getString("jsonSchema")));
					reportMD.name = (arg0.getString("name"));
					reportMD.description = (arg0.getString("description"));
					if(arg0.getString("pivotConfig")!=null) 
						reportMD.pivotConfig = ((JSONObject)new JSONParser().parse(arg0.getString("pivotConfig")));
					if(arg0.getString("searchCriteria")!=null) 
						reportMD.searchCriteria = arg0.getString("searchCriteria");
					return reportMD;
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}

			}

		});

	}

	private String  buildTitleMap(String jsonStr) {
		String titleMapFn[] =  jsonStr.split("titleMapFn_");
		for(int i = 1 ; i < titleMapFn.length ;i++) {
			String functionName = titleMapFn[i].split("\"")[0].trim();
			try {
				String o = (String)TitleMapHelper.class.getMethod(functionName,JdbcTemplate.class).invoke(null,jt);
				jsonStr = jsonStr.replace("\"titleMapFn_"+functionName+"\"", o);
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		return jsonStr;
	}

	@RequestMapping(value="reports/md/{reportid}",method=RequestMethod.GET)
	@ResponseBody
	public Object execute(Long id) {
		return null;
	}

	public static class  ReportMDVM {
		public JSONArray jsonForm;
		public JSONObject jsonSchema;
		public JSONObject pivotConfig;
		public String searchCriteria;
		public String name;
		public String description;
		public Long id;
	}

	/*public void generatePDF(List<Map<String, Object>> rs){

		 Document document = new Document();

	        try {
	            PdfWriter.getInstance(document,
	                new FileOutputStream("HelloWorld-Table.pdf"));

	            Font fontheader = new Font(Font.FontFamily.TIMES_ROMAN  , 8, Font.BOLD);
	            Font fontvalues = new Font(Font.FontFamily.TIMES_ROMAN  , 8);
	            document.open();
	            PdfPTable table = new PdfPTable(rs.get(0).size()); // total columns.
	            table.setWidthPercentage(100);
	            //this code for header in pdf table
	            for ( String key : rs.get(0).keySet() ) {
	            	 PdfPCell cell1 = new PdfPCell(new Paragraph(key,fontheader));
	            	 cell1.setBorder(Rectangle.NO_BORDER);
	            	 cell1.setBackgroundColor( (new BaseColor(249, 249, 249)));
	            	 table.addCell(cell1);
	    		 }

	            // Header  pdf table End
	            boolean b = true;

	            //Table values start
	            for(Map<String, Object> columns :rs){

	            	for (Map.Entry<String, Object> entry : columns.entrySet()) {
	            			if(entry.getValue() != null){
	            	    		PdfPCell cell1 = new PdfPCell(new Paragraph(entry.getValue().toString(),fontvalues));
	            	    		cell1.setBorder(Rectangle.NO_BORDER);
	            	    		cell1.setBackgroundColor(b ? BaseColor.WHITE :  (new BaseColor(249, 249, 249)));
	            	    		table.addCell(cell1);
	            			}else{
	            	    		PdfPCell cell1 = new PdfPCell(new Paragraph(" ",fontvalues));
	            	    		cell1.setBorder(Rectangle.NO_BORDER);
	            	    		cell1.setBackgroundColor(b ? BaseColor.WHITE : (new BaseColor(249, 249, 249)));
	            	    		table.addCell(cell1);
	            			}
	            	}
	            	//alternate color to row 
	            	 b = !b;
			 	}

	            //Table values Code end

	            document.add(table);
	            document.close();
	            System.out.println("pdf written successfully on disk.");
	        } catch(Exception e){

	        }

	}
	 */	
	@RequestMapping(value="/generateCSV",method=RequestMethod.GET)
	@ResponseBody
	public FileSystemResource generateCSV( final HttpServletResponse response, @RequestParam String filter){
		JSONObject resp = new JSONObject();
		File file = null;
		try {
			JSONObject jsonObject = (JSONObject)new JSONParser().parse(filter);
			Long id = Long.parseLong(jsonObject.get("id").toString());
			Map<String,Object> mdResult = jt.queryForMap("Select query,columns,hiddenpivotcol from reportmd where id =" + id);//.query("Select query from reportmd where id = ?",new Object[]{id},);
			String query = mdResult.get("query").toString();	
			String[] namedParameters =  query.split(":");
			Map<String, Object> parameters = new HashMap<String, Object>();

			// This is imp to set all named params to null 
			for(String param : namedParameters) {
				for(int i = 0 ; i < param.length() ; i++) {
					if(param.charAt(i) == ' ' || param.charAt(i) == ')' || param.charAt(i) == '\n' || param.charAt(i) == '\t'|| param.charAt(i) == '\r') {
						parameters.put(param.substring(0, i), null);
						break;
					}
				}
			}

			for(Object key : jsonObject.keySet()){
				Object value = jsonObject.get(key);
				if (value instanceof String) {
					if(!value.equals(""))
						parameters.put(key.toString(), value);
				}

				if (value instanceof JSONArray) {
					JSONArray jsonArray = (JSONArray) value;
					int len = jsonArray.size();
					if(len > 0) {
						List<String> inValues = new ArrayList<String>();
						for (int i=0;i<len;i++){ 
							inValues.add(jsonArray.get(i).toString());
						}
						parameters.put(key.toString()+"in", inValues);
						parameters.put(key.toString(), "");
					}
				}
			}
			List<Map<String, Object>> rs = namedJdbcTemplate.queryForList(mdResult.get("query").toString(),parameters);

			XSSFWorkbook workbook = new XSSFWorkbook();
			//Create a blank sheet
			XSSFSheet sheet = workbook.createSheet("Employee Data");
			int cellnum = 0;
			int rowid = 0;
			// This code is to create header Start
			Row rowHeader = sheet.createRow(0);
			for ( String key : rs.get(0).keySet() ) {
				Cell cell = rowHeader.createCell(cellnum++);
				cell.setCellValue(key);
			}
			//create header End

			rowid = 1;
			//This code create columns values
			for(Map<String, Object> columns :rs){
				cellnum = 0;
				rowHeader = sheet.createRow(rowid++);
				for (Map.Entry<String, Object> entry : columns.entrySet()) {
					if(entry.getValue() != null){
						Cell cell = rowHeader.createCell(cellnum++);
						cell.setCellValue(entry.getValue().toString());
					}else{
						Cell cell = rowHeader.createCell(cellnum++);
						cell.setCellValue("");
					}
				}
			}


			file = new File("/home/phpbsh/reports/data.xlsx");
			FileOutputStream out = new FileOutputStream(file);
			workbook.write(out);
			out.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		response.setContentType("application/x-download");
		response.setHeader("Content-Transfer-Encoding", "binary"); 
		response.setHeader("Content-disposition","attachment; filename=\""+file.getName());
		return new FileSystemResource(file);
	}

}
