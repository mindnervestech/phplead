package com.mnt.businessApp.service;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.mnt.entities.authentication.AuthUser;
import com.mnt.entities.businessApp.Product;
import com.mnt.entities.businessApp.User;
import com.mnt.entities.businessApp.ZipCode;

@Service
public class ReadExcelService {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private JdbcTemplate jt;
	
	private static Map<String, User> users = new HashMap<String, User>();
	
	private static Map<String, AuthUser> authUsers = new HashMap<String, AuthUser>();
	
	private static Map<String, Product> products = new HashMap<String, Product>();


	public void readExcel() {

		File excelfile = new File("F://UP Lead Assignment List.xls");
		String filename = excelfile.getName();
		Workbook wb_xssf = null; //Declare XSSF WorkBook
		Workbook wb_hssf = null;//Declare HSSf WorkBook

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
			}
			if (fileExtn.equalsIgnoreCase("xls")){
				POIFSFileSystem fs = new POIFSFileSystem(file);
				wb_xssf = new HSSFWorkbook(fs);
			}

			for(int i = 0 ; i < wb_xssf.getNumberOfSheets(); i++){
				sheet = wb_xssf.getSheetAt(i);
				Row row;
				String reqNo = null;
				String posName = "";
				String level = "" ;

				Iterator<Row> rowIterator = sheet.iterator();
				rowIterator.next();
				rowIterator.next();
				while (rowIterator.hasNext()) {
					reqNo = null;
					row = rowIterator.next();
					if (!row.getZeroHeight()) {
						List<Map<String, Object>> user_zipcode;
						ZipCode zipCode = new ZipCode();
						User user = new User();
						AuthUser authUser = new AuthUser();
						//Pincodes (Cell A)
						Cell c = row.getCell(0);
						switch (c.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC:
							//System.out.println(" Pincode : " + (long) c.getNumericCellValue());
							zipCode = getZipCodeByID((long) c.getNumericCellValue());
							break;
						case Cell.CELL_TYPE_STRING:
							//System.out.println("Pincode" + c.getStringCellValue());
							zipCode = getZipCodeByID(Long.parseLong(c.getStringCellValue()));
							break;
						}

						////Town (Cell B)
						c = row.getCell(1);
						switch (c.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC:
							//System.out.println(" Town : " + (long) c.getNumericCellValue());
							zipCode.setTown(String.valueOf(c.getNumericCellValue()));
							break;
						case Cell.CELL_TYPE_STRING:
							//System.out.println(" Town : " + c.getStringCellValue());
							zipCode.setTown(c.getStringCellValue());
							break;
						}

						//District (Cell C)
						c = row.getCell(2);
						switch (c.getCellType()) {
						case Cell.CELL_TYPE_STRING:
							//System.out.println(" District : " + c.getStringCellValue());
							zipCode.setDistrict(c.getStringCellValue());
							break;
						}

						//State (Cell D)
						c = row.getCell(3);
						switch (c.getCellType()) {
						case Cell.CELL_TYPE_STRING:
							//System.out.println(" State : " + c.getStringCellValue());
							zipCode.setState(c.getStringCellValue());
							break;
						}

						//Zone
						c = row.getCell(19);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_STRING:
								//System.out.println(" Zone : " + c.getStringCellValue());
								zipCode.setZone(c.getStringCellValue());
								break;
							}
						}
						sessionFactory.getCurrentSession().update(zipCode);
						sessionFactory.getCurrentSession().flush();

						//Dealer 1 Data (Cell E)
						c = row.getCell(4);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_STRING:
								user = getUserByEmail(getEmail(c.getStringCellValue()));
								user.setName(c.getStringCellValue());
								break;
							}
						}


						c = row.getCell(5);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_NUMERIC:
								//System.out.println(" Mobile Number : " + (long) c.getNumericCellValue());
								user.setPhone(String.valueOf((long) c.getNumericCellValue()));
								break;
							}
						}


						c = row.getCell(6);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_STRING:
								//System.out.println(" Email Address : " + c.getStringCellValue());
								//user.setEmail(c.getStringCellValue());
								break;
							}
						}


						c = row.getCell(7);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_STRING:
								//System.out.println(" Address : " + c.getStringCellValue());
								user.setAddress(c.getStringCellValue());
								break;
							}
						}

						c = row.getCell(8);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_NUMERIC:
								//System.out.println(" Pincode : " + (long) c.getNumericCellValue());
								user.setPostCode(String.valueOf((long) c.getNumericCellValue()));
								break;
							}
						}
						saveUser(user, authUser, zipCode, "Dealer", sheet.getSheetName());
						//Dealer 2 Data
						c = row.getCell(9);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_STRING:
								System.out.println("Name :: "+c.getStringCellValue());
								user = getUserByEmail(getEmail(c.getStringCellValue()));
								user.setName(c.getStringCellValue());
								break;
							}
						}

						c = row.getCell(10);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_NUMERIC:
								//System.out.println(" Mobile Number : " + (long) c.getNumericCellValue());
								user.setPhone(String.valueOf((long) c.getNumericCellValue()));
								break;
							}
						}

						c = row.getCell(11);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_STRING:
								//System.out.println(" Email Address : " + c.getStringCellValue());
								//user.setEmail(c.getStringCellValue());
								break;
							}
						}


						c = row.getCell(12);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_STRING:
								//System.out.println(" Address : " + c.getStringCellValue());
								user.setAddress(c.getStringCellValue());
								break;
							}
						}

						c = row.getCell(13);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_NUMERIC:
								//System.out.println(" Pincode : " + (long) c.getNumericCellValue());
								user.setPostCode(String.valueOf((long) c.getNumericCellValue()));
								break;
							}
						}
						saveUser(user, authUser, zipCode, "Dealer", sheet.getSheetName());
						//Dealer 3 Data
						c = row.getCell(14);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_STRING:
								//System.out.println(" Name : " + c.getStringCellValue());
								user.setName(c.getStringCellValue());
								break;
							}
						}

						c = row.getCell(15);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_NUMERIC:
								//System.out.println(" Mobile Number : " + (long) c.getNumericCellValue());
								user.setPhone(String.valueOf((long) c.getNumericCellValue()));
								break;
							}
						}

						c = row.getCell(16);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_STRING:
								//System.out.println(" Email Address : " + c.getStringCellValue());
								user.setEmail(c.getStringCellValue());
								break;
							}
						}


						c = row.getCell(17);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_STRING:
								//System.out.println(" Address : " + c.getStringCellValue());
								user.setAddress(c.getStringCellValue());
								break;
							}
						}

						c = row.getCell(18);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_NUMERIC:
								//System.out.println(" Pincode : " + (long) c.getNumericCellValue());
								user.setPostCode(String.valueOf((long) c.getNumericCellValue()));
								break;
							}
						}
						/*					user.addZipCode(zipCode);
					user.setDistrict(zipCode.getDistrict());
					user.setState(zipCode.getState());
					user.setZone(zipCode.getZone());
					user.setEntityName("Dealer");
					sessionFactory.getCurrentSession().save(user);
					sessionFactory.getCurrentSession().flush();

					authUser.setEntityId(user.getId());
					authUser.setEntityName(user.getEntityName());
					authUser.setName(user.getName());
					authUser.setUsername(user.getEmail());
					authUser.setEmail(user.getEmail());
					authUser.setPassword("12345");
					sessionFactory.getCurrentSession().save(authUser);
					sessionFactory.getCurrentSession().flush();
						 */										
						//Sales Consultant Data
						user = new User();
						authUser = new AuthUser();
						c = row.getCell(20);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_STRING:
								//System.out.println(" Name : " + c.getStringCellValue());
								user.setName(c.getStringCellValue());
								break;
							}
						}

						c = row.getCell(21);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_NUMERIC:
								//System.out.println(" Employee Code : " + (long) c.getNumericCellValue());
								break;
							}
						}

						c = row.getCell(22);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_NUMERIC:
								//System.out.println(" Mobile Number : " + (long) c.getNumericCellValue());
								user.setPhone(String.valueOf((long) c.getNumericCellValue()));
								break;
							}
						}
						/*					user.addZipCode(zipCode);
					user.setDistrict(zipCode.getDistrict());
					user.setState(zipCode.getState());
					user.setZone(zipCode.getZone());
					user.setEntityName("Sales Consultant");
					sessionFactory.getCurrentSession().save(user);
					sessionFactory.getCurrentSession().flush();

					authUser.setEntityId(user.getId());
					authUser.setEntityName(user.getEntityName());
					authUser.setName(user.getName());
					authUser.setUsername(user.getEmail());
					authUser.setEmail(user.getEmail());
					authUser.setPassword("12345");
					sessionFactory.getCurrentSession().save(authUser);
					sessionFactory.getCurrentSession().flush();
						 */					
						//TSR Data
						user = new User();
						c = row.getCell(23);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_STRING:
								//System.out.println(" Name : " + c.getStringCellValue());
								user.setName(c.getStringCellValue());

								break;
							}
						}

						c = row.getCell(24);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_NUMERIC:
								//System.out.println(" Employee Code : " + (long) c.getNumericCellValue());
								break;
							}
						}

						c = row.getCell(25);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_NUMERIC:
								//System.out.println(" Mobile Number : " + (long) c.getNumericCellValue());
								user.setPhone(String.valueOf((long) c.getNumericCellValue()));
								break;
							}
						}
						/*					user.addZipCode(zipCode);
					user.setDistrict(zipCode.getDistrict());
					user.setState(zipCode.getState());
					user.setZone(zipCode.getZone());
					user.setEntityName("TSR");
					sessionFactory.getCurrentSession().save(user);
					sessionFactory.getCurrentSession().flush();

					authUser.setEntityId(user.getId());
					authUser.setEntityName(user.getEntityName());
					authUser.setName(user.getName());
					authUser.setUsername(user.getEmail());
					authUser.setEmail(user.getEmail());
					authUser.setPassword("12345");
					sessionFactory.getCurrentSession().save(authUser);
					sessionFactory.getCurrentSession().flush();
						 */					
						//Sales Executive/Manager Data


						c = row.getCell(26);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_STRING:
								//System.out.println(" Name : " + c.getStringCellValue());
								//user.setName(c.getStringCellValue());
								user = getUserByEmail(getEmail(c.getStringCellValue()));
								user.setName(c.getStringCellValue());
								break;
							}
						}

						c = row.getCell(27);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_NUMERIC:
								//System.out.println(" Employee Code : " + (long) c.getNumericCellValue());
								break;
							}
						}

						c = row.getCell(28);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_NUMERIC:
								//System.out.println(" Mobile Number : " + (long) c.getNumericCellValue());
								user.setPhone(String.valueOf((long) c.getNumericCellValue()));
								break;
							}
						}
						saveUser(user, authUser, zipCode, "Sales Executive", sheet.getSheetName());
						//RSM
						//user = new User();
						c = row.getCell(29);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_STRING:
								//System.out.println(" Name : " + c.getStringCellValue());
								user = getUserByEmail(getEmail(c.getStringCellValue()));
								user.setName(c.getStringCellValue());
								break;
							}
						}

						c = row.getCell(30);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_NUMERIC:
								//System.out.println(" Employee Code : " + (long) c.getNumericCellValue());
								break;
							}
						}

						c = row.getCell(31);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_NUMERIC:
								//System.out.println(" Mobile Number : " + (long) c.getNumericCellValue());
								user.setPhone(String.valueOf((long) c.getNumericCellValue()));
								break;
							}
						}
						saveUser(user, authUser, zipCode, "RSM", sheet.getSheetName());

						//Sellout Manager
						//user = new User();
						c = row.getCell(32);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_STRING:
								//System.out.println(" Name : " + c.getStringCellValue());
								user = getUserByEmail(getEmail(c.getStringCellValue()));
								user.setName(c.getStringCellValue());
								break;
							}
						}

						c = row.getCell(33);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_NUMERIC:
								//System.out.println(" Employee Code : " + (long) c.getNumericCellValue());
								break;
							}
						}

						c = row.getCell(34);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_NUMERIC:
								//System.out.println(" Mobile Number : " + (long) c.getNumericCellValue());
								user.setPhone(String.valueOf((long) c.getNumericCellValue()));
								break;
							}
						}
						saveUser(user, authUser, zipCode, "Sellout Manager", sheet.getSheetName());

						//ZSM
						//user = new User();
						c = row.getCell(35);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_STRING:
								//System.out.println(" Name : " + c.getStringCellValue());
								user = getUserByEmail(getEmail(c.getStringCellValue()));
								user.setName(c.getStringCellValue());
								break;
							}
						}

						c = row.getCell(36);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_NUMERIC:
								//System.out.println(" Employee Code : " + (long) c.getNumericCellValue());
								break;
							}
						}

						c = row.getCell(37);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_NUMERIC:
								//System.out.println(" Mobile Number : " + (long) c.getNumericCellValue());
								user.setPhone(String.valueOf((long) c.getNumericCellValue()));
								break;
							}
						}
						saveUser(user, authUser, zipCode, "ZSM", sheet.getSheetName());

						//National head (in case of built-in only)(Sellout-Regional)
						//user = new User();
						c = row.getCell(38);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_STRING:
								//System.out.println(" Name : " + c.getStringCellValue());
								user = getUserByEmail(getEmail(c.getStringCellValue()));
								user.setName(c.getStringCellValue());
								break;
							}
						}

						c = row.getCell(39);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_NUMERIC:
								//System.out.println(" Employee Code : " + (long) c.getNumericCellValue());
								break;
							}
						}

						c = row.getCell(40);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_NUMERIC:
								//System.out.println(" Mobile Number : " + (long) c.getNumericCellValue());
								user.setPhone(String.valueOf((long) c.getNumericCellValue()));
								break;
							}
						}
						
						saveUser(user, authUser, zipCode, "Sellout-Regional", sheet.getSheetName());

						//Product assigned
						c = row.getCell(41);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_STRING:
								//System.out.println(" Washing Machine: " + c.getStringCellValue());
								break;
							}
						}

						//Brand
						c = row.getCell(42);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_STRING:
								//System.out.println(" Bosch : " + c.getStringCellValue());
								break;
							}
						}

						c = row.getCell(43);
						if(c!=null){
							switch (c.getCellType()) {
							case Cell.CELL_TYPE_STRING:
								//System.out.println(" Siemens : " + c.getStringCellValue());
								break;
							}
						}

						System.out.println("Sheet : " + sheet.getSheetName());	
					}
				}
			}	
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}


	}
	
	private void saveUser(User user, AuthUser authUser, ZipCode zipCode, String role, String product){
		if(user.getId() != null){
			if(user.getEntityName() == null){
				user.setDistrict(zipCode.getDistrict());
				user.setState(zipCode.getState());
				user.setZone(zipCode.getZone());
				user.setEntityName(role);
				authUser = getAuthUserByEmail(user.getEmail());
				authUser.setEntityId(user.getId());
				authUser.setEntityName(user.getEntityName());
				authUser.setName(user.getName());
				authUser.setUsername(user.getEmail());
				authUser.setEmail(user.getEmail());
				authUser.setPassword("12345");
				sessionFactory.getCurrentSession().update(authUser);
				sessionFactory.getCurrentSession().flush();
				jt.update("INSERT INTO userrole (user_id, role_id) VALUES (?, (select roles.role_id FROM roles where roles.name = ?))",
						new Object[] {authUser.getId(), role});
			}
			user.addProducts(getProductByName(product));
			sessionFactory.getCurrentSession().update(user);
			sessionFactory.getCurrentSession().flush();
			List<Map<String, Object>> user_zipcode = jt.queryForList("select * from user_zipcode where user_zipcode.User_id = "+user.id+" and user_zipcode.zipCodes_id = "+zipCode.id);
			if(user_zipcode.size() == 0){
				System.out.println("zipcode added");
				jt.update("insert into user_zipcode (user_zipcode.User_id,user_zipcode.zipCodes_id) values (?,?)",
						new Object[] {user.getId(), zipCode.getId()});
			}
		}
	}


	private ZipCode getZipCodeByID(long numericCellValue) {
		Session session = sessionFactory.getCurrentSession();
		Query query;
		query = session.createQuery("from ZipCode where id ="+numericCellValue);

		List<ZipCode> zipCodes = query.list();
		if(zipCodes.size() > 0){
			System.out.println("Already Present");
			return zipCodes.get(0);

		}else{
			ZipCode zipCode =  new ZipCode();
			zipCode.setId(numericCellValue);
			session.save(zipCode);
			session.flush();
			return zipCode;
		}
	}


	private Product getProductByName(String productName) {
		if(products.get(productName) != null){
			return products.get(productName);
		}
		Session session = sessionFactory.getCurrentSession();
		Query query;
		query = session.createQuery("from Product where name ='"+productName+"'");

		List<Product> productList = query.list();
		if(productList.size() > 0){
			products.put(productName, productList.get(0));
			return productList.get(0);
		}
		return null;
	}


	private AuthUser getAuthUserByEmail(String email) {
		if(authUsers.get(email) != null){
			return authUsers.get(email);
		}
		Session session = sessionFactory.getCurrentSession();
		Query query;
		query = session.createQuery("from AuthUser where email ='"+email+"'");

		List<AuthUser> authUserLsit = query.list();
		if(authUserLsit.size() > 0){
			System.out.println("Already Present");
			authUsers.put(email, authUserLsit.get(0));
			return authUserLsit.get(0);
		}else{
			AuthUser authUser =  new AuthUser();
			authUser.setEmail(email);
			sessionFactory.getCurrentSession().save(authUser);
			authUsers.put(email, authUser);
			return authUser;
		}
	}


	public User getUserByEmail(String email){
		//String sql = "Select name from user where email ='"+email+"'";
		if(users.get(email) != null){
			return users.get(email);
		}

		Session session = sessionFactory.getCurrentSession();
		Query query;
		query = session.createQuery("from User where email ='"+email+"'");

		List<User> userList = query.list();
		if(userList.size() > 0){
			System.out.println("Already Present");
			users.put(email, userList.get(0));
			return userList.get(0);

		}else{
			User user =  new User();
			user.setEmail(email);
			sessionFactory.getCurrentSession().save(user);
			users.put(email, user);
			return user;
		}


	}

	public String getEmail(String name){
		String email = name.replaceAll("\\s+","");
		return email.toLowerCase()+"@bshg.com";	
	}
}