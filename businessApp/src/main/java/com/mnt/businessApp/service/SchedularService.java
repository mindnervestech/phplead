package com.mnt.businessApp.service;

import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.mnt.businessApp.engine.SelloutExecutiveAllotmentWFStep;
import com.mnt.businessApp.viewmodel.UserInfoVM;
import com.mnt.entities.businessApp.ActivityStream;
import com.mnt.entities.businessApp.GeneralConfig;
import com.mnt.entities.businessApp.Lead;
import com.mnt.entities.businessApp.LeadAgeing;
import com.mnt.entities.businessApp.LeadDetails;
import com.mnt.entities.businessApp.Product;

@Service
public class SchedularService {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
    private JdbcTemplate jt;
	
	public UserInfoVM getUserInfo() {
		UserInfoVM infoVM = new UserInfoVM();
		infoVM.setFollowUpCount(sessionFactory.getCurrentSession().createQuery("FROM Lead where disposition1 = 'Escalated' and DATE(followUpDate) = CURRENT_DATE()").list().size());
		return infoVM;
	}
	
	public UserInfoVM followUpSchedular() {
		UserInfoVM infoVM = new UserInfoVM();
		infoVM.setFollowUpCount(sessionFactory.getCurrentSession().createQuery("FROM Lead where disposition1 = 'Escalated' and DATE(followUpDate) = CURRENT_DATE()").list().size());
		return infoVM;
	}
	
	public void escalationScheduler() {
		List<Map<String, Object>> rows = jt.queryForList("select ld.pinCode as zipcode, l.id as id, ld.product_id as product from lead l, leaddetails ld where ld.id = l.leadDetails_id");
		for(Map<String, Object> row : rows){
			SelloutExecutiveAllotmentWFStep allotmentWFStep = new SelloutExecutiveAllotmentWFStep((String) row.get("zipcode"), (String) row.get("product").toString(), (Long) row.get("id"));
			allotmentWFStep.jt = jt;
			allotmentWFStep.session = sessionFactory.getCurrentSession();
			allotmentWFStep.status = "escalation";
			GeneralConfig config = (GeneralConfig) sessionFactory.getCurrentSession().get(GeneralConfig.class, 1L);
			Integer escalationTime = Integer.valueOf(config.getFirstEscalationTime());
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -escalationTime);
			allotmentWFStep.configDate =   cal.getTime();
			allotmentWFStep.startAssignment();
		}
	}

	// upload the excel
	public void uploadandStoreExcel() {

		File excelfile = new File("F://Leads with call centre feedback.(13082015 manipulated).xls");
		String filename = excelfile.getName();
		Workbook wb_xssf; //Declare XSSF WorkBook
		Workbook wb_hssf;//Declare HSSf WorkBook

		int newRows = 0;
		int updatedRows = 0;
		Sheet sheet = null;
		String  jobNum = ""; 
		String userPosition = "";
		//Lead.removeAll();
		try {

			FileInputStream file = new FileInputStream(excelfile);
			String fileExtn = FilenameUtils.getExtension(filename);
			if (fileExtn.equalsIgnoreCase("xlsx")){
				wb_xssf = new XSSFWorkbook(file);

				sheet = wb_xssf.getSheetAt(0);
			}

			if (fileExtn.equalsIgnoreCase("xls")){
				POIFSFileSystem fs = new POIFSFileSystem(file);
				wb_hssf = new HSSFWorkbook(fs);
				sheet = wb_hssf.getSheetAt(0);
			}


			Row row;
			String reqNo = null;
			String posName = "";
			String level = "" ;

			Iterator<Row> rowIterator = sheet.iterator();
			rowIterator.next();
			while (rowIterator.hasNext()) {
				reqNo = null;
				row = rowIterator.next();
				if (!row.getZeroHeight()) {

					LeadDetails leadDetails = new LeadDetails();
					Cell c = row.getCell(0);

					switch (c.getCellType()) {
					case Cell.CELL_TYPE_NUMERIC:
						leadDetails.filter = c.getNumericCellValue() + "";
						break;
					case Cell.CELL_TYPE_STRING:
						//System.out.println("nukmberString");
						leadDetails.filter = c.getStringCellValue();
						break;
					}

					Date date = null;
					Date time = null;
					c = row.getCell(1);

					switch (c.getCellType()) {
					case Cell.CELL_TYPE_NUMERIC:
						if (DateUtil.isCellDateFormatted(c)) {
							date = c.getDateCellValue();
						}
						break;
					}

					c = row.getCell(2);
					switch (c.getCellType()) {
					case Cell.CELL_TYPE_NUMERIC:
						if (DateUtil.isCellDateFormatted(c)) {
							time = c.getDateCellValue();
						}
					}

					leadDetails.uploadDate = dateTime(date, time);


					c = row.getCell(3);
					switch (c.getCellType()) {
					case Cell.CELL_TYPE_NUMERIC:
						if (DateUtil.isCellDateFormatted(c)) {
							date = c.getDateCellValue();
						}
					}

					c = row.getCell(4);
					if (c != null) {
						switch (c.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC:
							if (DateUtil.isCellDateFormatted(c)) {
								time = c.getDateCellValue();
							}
						}
					}

					leadDetails.firstCallDate = dateTime(date, time);

					c = row.getCell(5);
					if (c != null) {
						switch (c.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC:
							leadDetails.sr = (long) c.getNumericCellValue();
							break;
						case Cell.CELL_TYPE_STRING:
							leadDetails.sr = Long.parseLong(c.getStringCellValue());
							break;
						}
					}

					c = row.getCell(6);
					if (c != null) {
						switch (c.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC:
							leadDetails.srNo = (long) c.getNumericCellValue();
							break;
						case Cell.CELL_TYPE_STRING:
							leadDetails.srNo =  Long.parseLong(c.getStringCellValue());
							break;
						}
					}

					c = row.getCell(7);
					if (c != null) {
						switch (c.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC:
							leadDetails.title = c.getNumericCellValue() + "";
							break;
						case Cell.CELL_TYPE_STRING:
							leadDetails.title = c.getStringCellValue();
							break;
						}
					}

					c = row.getCell(8);

					if (c != null) {
						switch (c.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC:
							leadDetails.name = c.getNumericCellValue() + "";
							break;
						case Cell.CELL_TYPE_STRING:
							leadDetails.name = c.getStringCellValue();
							break;
						}
					}

					c = row.getCell(9);
					if (c != null) {
						switch (c.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC:
							leadDetails.email = c.getNumericCellValue() + "";
							break;
						case Cell.CELL_TYPE_STRING:
							leadDetails.email = c.getStringCellValue();
							break;
						}
					}

					c = row.getCell(10);
					if (c != null) {
						switch (c.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC:
							leadDetails.contactNo = (long) c.getNumericCellValue();
							break;
						case Cell.CELL_TYPE_STRING:
							leadDetails.contactNo = Long.parseLong(c
									.getStringCellValue());
							break;
						}
					}

					c = row.getCell(13);
					if (c != null) {
						ZipCode zipcode = null;
						switch (c.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC:
							zipcode = (ZipCode) sessionFactory.getCurrentSession().get(ZipCode.class, Long.valueOf((long) c.getNumericCellValue()));
							break;
						case Cell.CELL_TYPE_STRING:
							zipcode = (ZipCode) sessionFactory.getCurrentSession().get(ZipCode.class, Long.valueOf(Long.parseLong(c.getStringCellValue())));
							break;
						}
						if(zipcode != null){
							leadDetails.pinCode = zipcode.getId()+"";
							leadDetails.state = zipcode.getState();
							leadDetails.city = zipcode.getDistrict();
						}
					}
					/*
					 c = row.getCell(14);
					if (c != null) {
						switch (c.getCellType()) {
						case Cell.CELL_TYPE_STRING:
							leadDetails.product = getProductByName(c.getStringCellValue());
							break;
						}
					}
					*/

					c = row.getCell(15);
					if (c != null) {
						switch (c.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC:
							leadDetails.type = c.getNumericCellValue() + "";
							break;
						case Cell.CELL_TYPE_STRING:
							leadDetails.type = c.getStringCellValue();
							break;
						}
					}

					c = row.getCell(16);
					if (c != null) {
						switch (c.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC:
							leadDetails.contactMe = c.getNumericCellValue() + "";
							break;
						case Cell.CELL_TYPE_STRING:
							leadDetails.contactMe = c.getStringCellValue();
							break;
						}
					}

					c = row.getCell(17);
					if (c != null) {
						switch (c.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC:
							leadDetails.campaignName = c.getNumericCellValue() + "";
							break;
						case Cell.CELL_TYPE_STRING:
							leadDetails.campaignName = c.getStringCellValue();
							break;
						}
					}
					c = row.getCell(18);
					if (c != null) {
						switch (c.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC:
							leadDetails.siteId = c.getNumericCellValue() + "";
							break;
						case Cell.CELL_TYPE_STRING:
							leadDetails.siteId = c.getStringCellValue();
							break;
						}
					}

					c = row.getCell(19);
					if (c != null) {
						switch (c.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC:
							leadDetails.creativeId = c.getNumericCellValue() + "";
							break;
						case Cell.CELL_TYPE_STRING:
							leadDetails.creativeId = c.getStringCellValue();
							break;
						}
					}

					c = row.getCell(20);
					if (c != null) {
						switch (c.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC:
							leadDetails.page = c.getNumericCellValue() + "";
							break;
						case Cell.CELL_TYPE_STRING:
							leadDetails.page = c.getStringCellValue();
							break;
						}
					}
					c = row.getCell(21);
					if (c != null) {
						switch (c.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC:
							leadDetails.lms = c.getNumericCellValue() + "";
							break;
						case Cell.CELL_TYPE_STRING:
							leadDetails.lms = c.getStringCellValue();
							break;
						}
					}

					c = row.getCell(22);
					if (c != null) {
						switch (c.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC:
							if (DateUtil.isCellDateFormatted(c)) {
								leadDetails.leadDate = c.getDateCellValue();
							}
							break;
						}
					}
					c = row.getCell(24);
					if (c != null) {
						switch (c.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC:
							leadDetails.ipAddress = c.getNumericCellValue() + "";
							break;
						case Cell.CELL_TYPE_STRING:
							leadDetails.ipAddress = c.getStringCellValue();
							break;
						}
					}
					c = row.getCell(25);
					if (c != null) {
						switch (c.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC:
							//lead.lastCallDate = new Date(c.getNumericCellValue() + "");
							break;
						case Cell.CELL_TYPE_STRING:
							//lead.lastCallDate = new Date(c.getStringCellValue());
							break;
						}
					}
					c = row.getCell(26);
					if (c != null) {
						switch (c.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC:
							leadDetails.lastCallStatus = c.getNumericCellValue() + "";
							break;
						case Cell.CELL_TYPE_STRING:
							leadDetails.lastCallStatus = c.getStringCellValue();
							break;
						}
					}
					c = row.getCell(27);
					if (c != null) {
						switch (c.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC:
							leadDetails.categorization = c.getNumericCellValue() + "";
							break;
						case Cell.CELL_TYPE_STRING:
							leadDetails.categorization = c.getStringCellValue();
							break;
						}
					}
					c = row.getCell(28);
					if (c != null) {
						switch (c.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC:
							leadDetails.salesPersonContact = c.getNumericCellValue() + "";
							break;
						case Cell.CELL_TYPE_STRING:
							leadDetails.salesPersonContact = c.getStringCellValue();
							break;
						}
					}
					c = row.getCell(29);
					if (c != null) {
						switch (c.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC:
							leadDetails.areaofInterest1 = c.getNumericCellValue() + "";
							break;
						case Cell.CELL_TYPE_STRING:
							leadDetails.areaofInterest1 = c.getStringCellValue();
							break;
						}
					}
					c = row.getCell(30);
					if (c != null) {
						switch (c.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC:
							leadDetails.areaofInterest2 = c.getNumericCellValue() + "";
							break;
						case Cell.CELL_TYPE_STRING:
							leadDetails.areaofInterest3 = c.getStringCellValue();
							break;
						}
					}
					c = row.getCell(31);
					if (c != null) {
						switch (c.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC:
							leadDetails.areaofInterest3 = c.getNumericCellValue() + "";
							break;
						case Cell.CELL_TYPE_STRING:
							leadDetails.areaofInterest3 = c.getStringCellValue();
							break;
						}
					}
					c = row.getCell(32);
					if (c != null) {
						switch (c.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC:
							leadDetails.remarks1 = c.getNumericCellValue() + "";
							break;
						case Cell.CELL_TYPE_STRING:
							leadDetails.remarks1 = c.getStringCellValue();
							break;
						}
					}
					c = row.getCell(33);
					if (c != null) {
						switch (c.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC:
							leadDetails.remarks2 = c.getNumericCellValue() + "";
							break;
						case Cell.CELL_TYPE_STRING:
							leadDetails.remarks2 = c.getStringCellValue();
							break;
						}
					}
					Transaction tx = null;
					Session session = sessionFactory.openSession();
					try{
						tx = session.beginTransaction();
						 c = row.getCell(14);
							if (c != null) {
								switch (c.getCellType()) {
								case Cell.CELL_TYPE_STRING:
									leadDetails.product = getProductByName(c.getStringCellValue(),session);
									break;
								}
						    }
						session.save(leadDetails);
						tx.commit();
					} catch(ConstraintViolationException e){
						tx.rollback();
						session.clear();
						System.out.println("here ==== 1");
						
						
						
						LeadDetails oldLeadDetails = getLeadDetailsBySrNo(leadDetails.sr,session);
						 
						if(oldLeadDetails.categorization.equals("Warm") || oldLeadDetails.categorization.equals("Hot") || oldLeadDetails.categorization.equals("Cold")){
							session.close();
							continue;
						}
						Transaction tx1 = session.beginTransaction();
						
						Lead oldLead = getLeadByLeadDetails(oldLeadDetails.id,session);
						
						deleteActivityByLeadId(oldLead.id);
						deleteLeadByLeadDetailId(oldLeadDetails.id);
						deleteLeadDetalById(oldLeadDetails.id);
						
						//session.delete(oldLeadDetails);
						//session.delete(oldLead);
						//tx1.commit();
						
						//tx1 = session.beginTransaction();
						session.save(leadDetails);
						tx1.commit();
						System.out.println("here ==== 2");
						
					}
					System.out.println("here ==== 3");
					
					tx = session.beginTransaction();
					Lead lead = new Lead();
					lead.setZone(getZoneFromState(leadDetails.state));
					lead.setLeadDetails(leadDetails);
					lead.setUploadDate(new Date());
					lead.setLastDispo1ModifiedDate(new Date());
					lead.setDisposition1("New");
					lead.setStatus("Open");
					lead.setOrigin("Call-center");
					session.save(lead);
					
					if(leadDetails.categorization.equals("Warm") || leadDetails.categorization.equals("Hot") || leadDetails.categorization.equals("Cold")){
						ActivityStream activityStream = new ActivityStream();
						activityStream.setNewDisposition1("New");
						activityStream.setLead(lead);
						activityStream.setCreatedDate(new Date());
						session.save(activityStream);

						LeadAgeing ageing = new LeadAgeing();
						Long secs = (leadDetails.getFirstCallDate().getTime() - leadDetails.getUploadDate().getTime()) / 1000;
						Long hours = (Long)(secs / 3600) ;
						ageing.setAgeing(hours);
						ageing.setStatus("Call Center Ageing");
						ageing.setProduct(leadDetails.getProduct().getName());
						ageing.setLead_id(lead.getId());
						ageing.setIsCurrent(false);
						session.save(ageing);
					}
					if (leadDetails.filter != null) {
						System.out.println("storeExcelFile"
								);
						newRows = newRows + 1;
					}
					tx.commit();
					session.close();

				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	private void deleteLeadDetalById(Long id) {
		jt.update("Delete from leaddetails where leaddetails.id = ?",
				new Object[] {id});
	}

	private void deleteActivityByLeadId(Long id) {
		jt.update("Delete from activitystream where lead_id = ?",
				new Object[] {id});
	}
	
	private void deleteLeadByLeadDetailId(Long id) {
		jt.update("Delete from lead where lead.leadDetails_id = ?",
				new Object[] {id});
	}

	private LeadDetails getLeadDetailsBySrNo(Long sr, Session session) {
		
		Query query = session.createQuery("From LeadDetails where sr = "+sr);
		List list = query.list();
		return list.size() == 0 ? null : (com.mnt.entities.businessApp.LeadDetails) list.get(0); 
	}
	
    private Lead getLeadByLeadDetails(Long leadDetailId, Session session) {
		
		Query query = session.createQuery("From Lead where leadDetails.id = "+leadDetailId);
		List list = query.list();
		return list.size() == 0 ? null : (com.mnt.entities.businessApp.Lead) list.get(0); 
	}

	private String getZoneFromState(String state) {
		try{
			String name = (String)jt.queryForObject(
					"Select DISTINCT(zipcode.zone) from zipcode where zipcode.state = ?", new Object[] { state.toUpperCase() }, String.class);
			return name;
		} catch(Exception e){
			return null;
		}
	}

	private Product getProductByName(String productName,Session session) {
		
		Query query = session.createQuery("From Product where name = '"+productName+"'");
		List<Product> products = query.list();  
		Product product;
		if(products.size() == 0){
			product = new Product();
			product.setName(productName);
			session.save(product);
		} else {
			product =  products.get(0);
		}
		
		return product;
	}

	private static Date dateTime(Date date, Date time) {
		
		Calendar calendarA = Calendar.getInstance();
		calendarA.setTime(date);

		Calendar calendarB = Calendar.getInstance();
		calendarB.setTime(time);

		calendarA.set(Calendar.HOUR_OF_DAY, calendarB.get(Calendar.HOUR_OF_DAY));
		calendarA.set(Calendar.MINUTE, calendarB.get(Calendar.MINUTE));
		calendarA.set(Calendar.SECOND, calendarB.get(Calendar.SECOND));
		calendarA.set(Calendar.MILLISECOND, calendarB.get(Calendar.MILLISECOND));
		
		return calendarA.getTime();
	}
	
}
