package com.mnt.businessApp.service;


import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;


@Service
public class MailService
{
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private VelocityEngine velocityEngine;
    
    @Autowired
    private JdbcTemplate jt;
     
    public void sendMail(String to, String subject, String body)
    {
    	sendSuggestPodcastNotification(to,subject);
    }
    
    public void sendSuggestPodcastNotification(final String to, final String subject) {
	      MimeMessagePreparator preparator = new MimeMessagePreparator() {
		        @SuppressWarnings({ "rawtypes", "unchecked" })
				public void prepare(MimeMessage mimeMessage) throws Exception {
		             MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);
		             message.setTo(to);
		             message.setFrom("admin@bsh-lms.com");
		             message.setSubject(subject);
		             Map velocityContext = new HashMap();
		             velocityContext.put("firstName", "Yashwant");
		             velocityContext.put("lastName", "Chavan");
		             velocityContext.put("location", "Pune");
		             velocityContext.put("message", "Message");
		             String text = VelocityEngineUtils.mergeTemplateIntoString(
		                     velocityEngine, "email-template.vm", "UTF-8", velocityContext);
		             File file = getAttachement();
		             message.addAttachment(file.getName(), file);
		             message.setText(text, true);
		          }
		       };
		       mailSender.send(preparator);			
	}
    
    public File getAttachement(){
    	List<Map<String, Object>> rs = jt.queryForList("Select * from lead");
		return generateCSV(rs);
    	
    }
    
    public File generateCSV(List<Map<String, Object>> rs){
		 XSSFWorkbook workbook = new XSSFWorkbook();
	     //Create a blank sheet
	     XSSFSheet sheet = workbook.createSheet("Employee Data");
       int cellnum = 0;
       int rowid = 0;
       // This code is to create header Start
       XSSFCellStyle my_style = workbook.createCellStyle();
       XSSFFont my_font = workbook.createFont();
       my_font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
       my_style.setFont(my_font);
       Row rowHeader = sheet.createRow(0);
		 for ( String key : rs.get(0).keySet() ) {
           Cell cell = rowHeader.createCell(cellnum++);
           cell.setCellValue(key);
           cell.setCellStyle(my_style);
		 }
		//create header End
		        
		        rowid = 1;
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
			File f = new File("data.xlsx");
          FileOutputStream out = new FileOutputStream(new File("data.xlsx"));
          workbook.write(out);
          out.close();
          System.out.println("data.xlsx written successfully on disk.");
          return f;
      }
      catch (Exception e)
      {
          e.printStackTrace();
          return null;
      }
	}
}
