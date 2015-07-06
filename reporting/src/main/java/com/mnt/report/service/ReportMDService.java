package com.mnt.report.service;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/api/report")
public class ReportMDService {
	
	@Autowired
    private JdbcTemplate jt;
	
	@Autowired
	NamedParameterJdbcTemplate namedJdbcTemplate;

	
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
				    }
				}
			}
			List<Map<String, Object>> rs = namedJdbcTemplate.queryForList(mdResult.get("query").toString(),parameters);
			
			resp.put("data" , rs);
			
			 generateCSV(rs);
			 
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
    
	@RequestMapping(value="reports/md",method=RequestMethod.GET)
	@ResponseBody
	@Transactional
	public List<ReportMDVM> getReports() {
		//return sessionFactory.getCurrentSession().createQuery("FROM ReportMD1").list();
		
		return jt.query("Select * from reportmd", new RowMapper<ReportMDVM>(){

			public ReportMDVM mapRow(ResultSet arg0, int arg1)
					throws SQLException {
				try {
				ReportMDVM reportMD = new ReportMDVM();
				reportMD.id = (arg0.getLong("id"));
				reportMD.jsonForm = ((JSONArray)new JSONParser().parse(buildTitleMap(arg0.getString("jsonForm"))));
				reportMD.jsonSchema = ((JSONObject)new JSONParser().parse(arg0.getString("jsonSchema")));
				reportMD.name = (arg0.getString("name"));
				reportMD.description = (arg0.getString("description"));
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
		public String name;
		public String description;
		public Long id;
	}
/*	
	public void generatePDF(List<Map<String, Object>> rs){

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
	public void generateCSV(List<Map<String, Object>> rs){
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
		      //Code  columns values End
		try{
           //Write the workbook in file system
           FileOutputStream out = new FileOutputStream(new File("data.xlsx"));
           workbook.write(out);
           out.close();
           System.out.println("data.xlsx written successfully on disk.");
       }
       catch (Exception e)
       {
           e.printStackTrace();
       }
		 
	}
	
	
	/*
	select B.DD_ISSUE_DATE from tbl_de_data A,tbl_parent_image B  WHERE 
	(A.DC_AD_SIZE IN (:DC_AD_SIZEin) OR :DC_AD_SIZE IS NULL) AND
	(A.DC_AD_TYPE IN  (:DC_AD_TYPEin) OR :DC_AD_TYPE IS NULL) AND 
	(A.DC_AD_ORIENTATION IN  (:DC_AD_ORIENTATIONin) OR :DC_AD_ORIENTATION IS NULL) AND
	(A.DC_JOB_DENSITY IN  (:DC_JOB_DENSITYin) OR :DC_JOB_DENSITY IS NULL) AND
	(A.DC_AD_CATEGORY IN  (:DC_AD_CATEGORYin) OR :DC_AD_CATEGORY IS NULL) AND
	(A.DC_SEARCH_ADVERTISER_TYPE IN  (:DC_SEARCH_ADVERTISER_TYPEin) OR :DC_SEARCH_ADVERTISER_TYPE IS NULL) AND
	(A.DC_ADVERTISER_TYPE IN  (:DC_ADVERTISER_TYPEin) OR :DC_ADVERTISER_TYPE IS NULL) AND
	(A.DN_DECOMPANY_ID  IN  (:DN_DECOMPANY_IDin) OR :DN_DECOMPANY_ID IS NULL) AND 
	(B.DN_ID = A.DN_PARENT_IMAGE_ID ) AND
	(B.DC_PUBLICATION_TITLE IN (:DC_PUBLICATION_TITLEin) OR :DC_PUBLICATION_TITLE IS NULL)
	
	
	
SELECT 
 A.DN_ID,PIM.DC_PUBLICATION_TITLE,PIM.DN_ID,P2.DC_PUBLICATION_TITLE
FROM tbl_de_data A LEFT JOIN 
(SELECT P.DN_ID, P.DC_PUBLICATION_TITLE FROM tbl_publication P where 
P.DC_PUBLICATION_TYPE = 4) P4 ON A.DC_AD_CATEGORY = P4.DN_ID LEFT JOIN
(SELECT P.DN_ID, P.DC_PUBLICATION_TITLE FROM tbl_publication P where 
P.DC_PUBLICATION_TYPE = 5) P5 ON A.DC_ADVERTISER_TYPE = P5.DN_ID LEFT JOIN 
(SELECT P.DN_ID, P.DC_PUBLICATION_TITLE FROM tbl_publication P where 
P.DC_PUBLICATION_TYPE = 6) P6 ON A.DC_SEARCH_ADVERTISER_TYPE = P6.DN_ID LEFT JOIN
tbl_parent_image PIM ON PIM.DN_ID = A.DN_PARENT_IMAGE_ID
 LEFT JOIN 
(SELECT P.DN_ID, P.DC_PUBLICATION_TITLE FROM tbl_publication P where 
P.DC_PUBLICATION_TYPE = 2) P2 ON P2.DN_ID = PIM.DC_PUBLICATION_TITLE
	*
	*
	*/

}
