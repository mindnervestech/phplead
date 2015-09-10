package com.mnt.businessApp.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.mnt.businessApp.viewmodel.DealerConfigurationVM;
import com.mnt.businessApp.viewmodel.GeneralConfigVM;
import com.mnt.businessApp.viewmodel.PinsVM;
import com.mnt.businessApp.viewmodel.ProductVM;
import com.mnt.businessApp.viewmodel.RolesVM;
import com.mnt.businessApp.viewmodel.SaveUserVM;
import com.mnt.businessApp.viewmodel.UserVM;
import com.mnt.businessApp.viewmodel.ZoneVM;
import com.mnt.entities.authentication.AuthUser;
import com.mnt.entities.authentication.Role;
import com.mnt.entities.businessApp.GeneralConfig;
import com.mnt.entities.businessApp.User;
import com.mnt.entities.businessApp.ZipCode;

@Service
public class DealerService {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private JdbcTemplate jt;


	public Map getZones() {
		Map<String,List> dataList = new HashMap<String, List>();
		List<ZoneVM> zoneList = getZone();
		//List<ZoneVM> territoryList = getTerritory();
		dataList.put("zoneList", zoneList);
		//dataList.put("territoryList", territoryList);
		dataList.put("stateList", getStates());
		dataList.put("districtList", getDistricts());
		dataList.put("productList", getProductList());
		dataList.put("dealerList", getDealers(zoneList));
		return dataList;
	}

	private List<UserVM> getDealers(List<ZoneVM> zoneList) {
		AuthUser authUser = Utils.getLoggedInUser();
		Session session = sessionFactory.getCurrentSession();
		Query query;
		if(authUser.getEntityName().equals("RSM") || authUser.getEntityName().equals("TSR") ){
			List<Long> ids = jt.queryForList("Select DISTINCT(a.User_id) from user_zipcode as a, user_zipcode as b where a.zipCodes_id = b.zipCodes_id and b.User_id = "+authUser.getEntityId(), Long.class);
			query = session.createQuery("FROM User where id IN (:ids) and entityName = 'Dealer'");
			query.setParameterList("ids", ids);
		}
		else if(authUser.getEntityName().equals("ZSM") || authUser.getEntityName().equals("Sellout Manager")){
			User user2 = (User) sessionFactory.getCurrentSession().get(User.class, authUser.getEntityId());
			query = session.createQuery("FROM User where zone = '"+user2.getZone()+"' and entityName = 'Dealer'");
		}
		else if(authUser.getEntityName().equals("Admin") || authUser.getEntityName().equals("CEO") || authUser.getEntityName().equals("General Manager")){
			User user2 = (User) sessionFactory.getCurrentSession().get(User.class, authUser.getEntityId());
			query = session.createQuery("FROM User where entityName = 'Dealer'");
		}
		else {
			return null;
		}
		List<UserVM> vms = new ArrayList<UserVM>();
		List<User> users = query.list();  
		
		for (User user : users){
			UserVM vm = new UserVM(user);
			
			String sql1 = "Select * FROM user as u where u.entityName = 'Dealer'";		
			List<Map<String, Object>> rows1=  jt.queryForList(sql1);    
			for(Map row : rows1) {
				vm.status =  (Boolean) row.get("status") == false  ? "Inactive" : "Active";
			}
			
			String sql = "Select * from product pp left join (select products_id from user_product up where user_id = ?) a on pp.id = a.products_id";
			List<Map<String, Object>> rows = jt.queryForList(sql,new Object[] {user.getId()});
			List<ProductVM> products = new ArrayList<ProductVM>();
			for(Map mapProduct : rows) {
				ProductVM pvm = new ProductVM();
				pvm.id = (Long) mapProduct.get("id");
				pvm.name = (String) mapProduct.get("name");
				if(mapProduct.get("products_id") != null){
					pvm.setSelected(true);
				}
				products.add(pvm);
			}
			vm.products = products;
			vm.setIds(getAllDealerConfig(user.getId()));
			//vm.setPins(getAllDealerConfig(user.getId()));
			vms.add(vm);
		}
		return vms;
	}

	public List<ZoneVM> getDistricts() {
		List<Map<String, Object>> rows = jt.queryForList("Select DISTINCT(zipcode.district) as name from zipcode where zipcode.district IS NOT NULL");
		List<ZoneVM> districtList = new ArrayList<ZoneVM>();
		for(Map map : rows) {
			ZoneVM vm = new ZoneVM();
			vm.name = (String) map.get("name");
			districtList.add(vm);
		}
		return districtList;
	}

	public List<ZoneVM> getStates() {
		List<Map<String, Object>> rows = jt.queryForList("Select DISTINCT(zipcode.state) as name from zipcode where zipcode.state IS NOT NULL");
		List<ZoneVM> stateList = new ArrayList<ZoneVM>();
		for(Map map : rows) {
			ZoneVM vm = new ZoneVM();
			vm.name = (String) map.get("name");
			stateList.add(vm);
		}
		return stateList;
	}

	/*private List<ZoneVM> getTerritory() {
		List<Map<String, Object>> rows = jt.queryForList("select * from territory");
		List<ZoneVM> territoryList = new ArrayList<ZoneVM>();
		for(Map map : rows) {
			ZoneVM vm = new ZoneVM();
			vm.id = (Long) map.get("id");
			vm.name = (String) map.get("name");
			territoryList.add(vm);
		}
		return territoryList;
	}*/

	public List<ZoneVM> getZone() {
		List<ZoneVM> zoneList = new ArrayList<ZoneVM>();
		String sql = "Select DISTINCT(zipcode.zone) as name from zipcode where zipcode.zone IS NOT NULL";
		List<Map<String, Object>> rows = jt.queryForList(sql);
		for(Map map : rows) {
			ZoneVM vm = new ZoneVM();
			vm.name = (String) map.get("name");
			zoneList.add(vm);
		}
		return zoneList;
	}

	public List<ProductVM> getProductList() {
		String sql = "select * from product";
		List<Map<String, Object>> rows = jt.queryForList(sql);
		List<ProductVM> productList = new ArrayList<ProductVM>();
		for(Map map : rows) {
			ProductVM vm = new ProductVM();
			vm.id = (Long) map.get("id");
			vm.name = (String) map.get("name");
			productList.add(vm);
		}
		return productList;
	}

	public Map getDetailsForUser() {
		Map<String,List> dataList = new HashMap<String, List>();
		List<ZoneVM> zoneList = getZone();
		dataList.put("zoneList", getZone());
		dataList.put("stateList", getStates());
		dataList.put("districtList", getDistricts());
		dataList.put("roleList", getRoles());
		dataList.put("productList", getProductList());
		dataList.put("userList", getUserDetails());
		dataList.put("dealerList", getDealers(zoneList));
		return dataList;
	}

	private List<ZoneVM> getRoles() {
		String sql = "select * from roles";
		List<Map<String, Object>> rows = jt.queryForList(sql);
		List<ZoneVM> roleList = new ArrayList<ZoneVM>();
		for(Map map : rows) {
			ZoneVM vm = new ZoneVM();
			vm.roleId =  (int) map.get("role_id");
			vm.name = (String) map.get("name");
			roleList.add(vm);
		}
		return roleList;
	}


	public List<ZoneVM> getPinCodes(String query) {

		String sql = "select * from zipcode WHERE id LIKE '"+query+"%' or town LIKE '%"+query+"%'";

		List<Map<String, Object>> rows = jt.queryForList(sql);
		List<ZoneVM> pinsList = new ArrayList<ZoneVM>();
		for(Map map : rows) {
			ZoneVM vm = new ZoneVM();
			vm.id = (Long) map.get("id");
			vm.name = (String) map.get("town");
			pinsList.add(vm);
		}

		return pinsList;
	}


	public List<UserVM> saveUser(SaveUserVM userVM) {
		AuthUser authUser1 = Utils.getLoggedInUser();
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM Role Where role_id = :id"); 
		query.setParameter("id", userVM.getRole());
		List results = query.list();
		Role role = (Role) results.get(0);
		
		User user = new User();
		user.name = userVM.getName();
		user.email = userVM.getEmail();
		user.gender = userVM.getGender();
		user.birthday = userVM.getBirthday();
		user.phone = userVM.getPhone();
		user.address = userVM.getAddress();
		user.postCode = userVM.getPostCode();
		user.zone = userVM.getZone();
		user.state = userVM.getState();
		user.district = userVM.getDistrict();
		user.setEntityName(role.getName());
		user.setStatus(true);
		List<ZipCode> codes = new ArrayList<>();
		for(ZoneVM vm : userVM.getIds()){
			codes.add((ZipCode) sessionFactory.getCurrentSession().get(ZipCode.class, vm.getId()));
		}
		user.setZipCodes(codes);
		
		if(authUser1.getEntityName().equals("General Manager") || authUser1.getEntityName().equals("Sellout-Regional")){
			System.out.println("User : " + Long.valueOf(userVM.user));
			User dealer = (User) sessionFactory.getCurrentSession().get(User.class,Long.valueOf(userVM.user));
			user.setUser(dealer);
		}
		session.save(user);
		
		String randomStr = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random rnd = new Random();

		StringBuilder sb = new StringBuilder( 8 );
		for( int i = 0; i < 8; i++ ) 
			sb.append( randomStr.charAt( rnd.nextInt(randomStr.length()) ) );
		
		AuthUser authUser = new AuthUser();
		authUser.setEmail(userVM.getEmail());
		authUser.setPassword(sb.toString());
		authUser.setEntityId(user.getId());
		authUser.setUsername(userVM.getEmail());
		authUser.setEntityName(role.getName());
		authUser.setName(user.getName());
		List<Role> roles = new ArrayList<>();
		roles.add(role);
		authUser.setRoles(roles);
		session.save(authUser);

		for(String productId : userVM.getProductlist()) {
			jt.update("insert into user_product (user_product.User_id,user_product.products_id) values (?,?)",
					new Object[] {user.getId(), Integer.parseInt(productId)});
		}
		session.flush();
		return getUserDetails();
	}

	public ZipCode getZipCodeById(Long id){
		return (ZipCode) sessionFactory.getCurrentSession().get(ZipCode.class, id);
	}
	
	public void removeAllDealerConfig(Long id){
		jt.update("DELETE FROM user_zipcode WHERE user_id = ?", new Object[] { id});
	}
	
	public void removeAlluserProductMapping(Long id){
		jt.update("DELETE FROM user_product WHERE User_id = ?", new Object[] {id});
	}
	
	public List<ZoneVM> getAllDealerConfig(Long id){
		String sql = "select * from user_zipcode WHERE user_id = ?";

		List<Map<String, Object>> rows = jt.queryForList(sql,new Object[] { id});
		List<ZoneVM> vms = new ArrayList<ZoneVM>();
		Map<String,List> dataList = new HashMap<String, List>();
		for(Map map : rows) {
			ZoneVM vm = new ZoneVM();
			vm.id = (Long) map.get("zipCodes_id");
			vms.add(vm);
		}
		return vms;
	}
	
	public void saveDealer(UserVM userVM) {
		AuthUser authUser1 = Utils.getLoggedInUser();
		User user = new User();
		user.name = userVM.getName();
		user.email = userVM.getEmail();
		user.gender = userVM.getGender();
		user.birthday = userVM.getBirthday();
		user.phone = userVM.getPhone();
		user.zone = userVM.getZone();
		user.address = userVM.getAddress();
		user.state = userVM.getState();
		user.district = userVM.getDistrict();
		user.postCode = userVM.getPostCode();
		user.customerGroup = userVM.getCustomerGroup();
		user.setEntityName("Dealer");
		user.setStatus(true);
		List<ZipCode> codes = new ArrayList<>();
		for(ZoneVM vm : userVM.getIds()){
			codes.add((ZipCode) sessionFactory.getCurrentSession().get(ZipCode.class, vm.getId()));
		}
		user.setZipCodes(codes);
		
		sessionFactory.getCurrentSession().save(user);
		sessionFactory.getCurrentSession().flush();
		removeAlluserProductMapping(userVM.getId());
		for(ProductVM productVM : userVM.getProducts()) {
			if(productVM.getSelected() == true){
				jt.update("insert into user_product (user_product.User_id,user_product.products_id) values (?,?)",
						new Object[] {user.getId(), productVM.getId()});
			}
		}
		
		String randomStr = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random rnd = new Random();

		StringBuilder sb = new StringBuilder( 8 );
		for( int i = 0; i < 8; i++ ) 
			sb.append( randomStr.charAt( rnd.nextInt(randomStr.length()) ) );
		
		AuthUser authUser = new AuthUser();
		authUser.setEmail(userVM.getEmail());
		authUser.setPassword(sb.toString());
		authUser.setEntityId(user.getId());
		authUser.setUsername(userVM.getEmail());
		authUser.setEntityName("Dealer");
		Role role = (Role) sessionFactory.getCurrentSession().get(Role.class, 9);
		List<Role> roles = new ArrayList<>();
		roles.add(role);
		authUser.setRoles(roles);
		authUser.setName(user.getName());
		sessionFactory.getCurrentSession().save(authUser);
	}


	public void updateDealer(UserVM userVM) {
		User user = (User)sessionFactory.getCurrentSession().get(User.class, userVM.getId());
		user.name = userVM.getName();
		user.email = userVM.getEmail();
		user.gender = userVM.getGender();
		user.birthday = userVM.getBirthday();
		user.phone = userVM.getPhone();
		user.zone = userVM.getZone();
		user.address = userVM.getAddress();
		user.state = userVM.getState();
		user.district = userVM.getDistrict();
		user.postCode = userVM.getPostCode();
		user.customerGroup = userVM.getCustomerGroup();
		List<ZipCode> codes = new ArrayList<>();
		for(ZoneVM vm : userVM.getIds()){
			codes.add((ZipCode) sessionFactory.getCurrentSession().get(ZipCode.class, vm.getId()));
		}
		user.setZipCodes(codes);
		for(ProductVM productVM : userVM.getProducts()) {
			if(productVM.getSelected() == true){
				jt.update("insert into user_product (user_product.User_id,user_product.products_id) values (?,?)",
						new Object[] {user.getId(), productVM.getId()});
			}
		}
		
		Query query = sessionFactory.getCurrentSession().createQuery("FROM AuthUser Where entityId = :id"); 
		query.setParameter("id", userVM.getId());
		List results = query.list();
		AuthUser authUser = (AuthUser) results.get(0);
		authUser.setEmail(userVM.getEmail());
		authUser.setUsername(userVM.getEmail());
		authUser.setName(userVM.getName());
		sessionFactory.getCurrentSession().update(authUser);
		
		sessionFactory.getCurrentSession().update(user);
		sessionFactory.getCurrentSession().flush();
		/*Session session = sessionFactory.getCurrentSession();
		Dealer dealer = (Dealer) session.get(Dealer.class, dealerVM.getId());
		dealer.dealerCode = dealerVM.getCode();
		dealer.dealerName = dealerVM.getName();
		dealer.customerGroup = dealerVM.getCustomerGroup();
		dealer.phone = dealerVM.getPhone();
		dealer.email = dealerVM.getEmail();
		dealer.zone = dealerVM.getZone().getName();
		//dealer.territory = dealerVM.getTerritory().getName();
		List<User> user = new ArrayList<>();
		for(ZoneVM rsm : dealerVM.getRsm()){
			user.add((User) session.get(User.class, Long.valueOf( rsm.getId()).longValue()));
		}
		for(ZoneVM tsr : dealerVM.getTsr()){
			user.add((User) session.get(User.class, Long.valueOf( tsr.getId()).longValue()));
		}
		List<Product> products = new ArrayList<>();
		for(ProductVM productVM : dealerVM.getProducts()) {
			if(productVM.getSelected() == true){
				products.add((Product)sessionFactory.getCurrentSession().get(Product.class, productVM.getId()));
			}
		}
		dealer.setProducts(products);
		dealer.setUser(user);
		dealer.address = dealerVM.getAddress();
		dealer.state = dealerVM.getState();
		dealer.district = dealerVM.getDistrict();
		dealer.subDistrict = dealerVM.getSubdist();
		dealer.zipCode = dealerVM.getZipCode();
		session.update(dealer);
		session.flush();
		
		removeAllDealerConfig(dealer.getId());
		for(PinsVM vm : dealerVM.getPins()){
			DealerConfiguration configuration = new DealerConfiguration();
			configuration.zipCode = getZipCodeById(vm.getPin());
			configuration.dealer = dealer;
			sessionFactory.getCurrentSession().save(configuration);
		}*/
		
	}


	public List<UserVM> updateUser(UserVM userVM) {
		Query query = sessionFactory.getCurrentSession().createQuery("FROM Role Where role_id = :id"); 
		query.setParameter("id", userVM.getRole().getRoleId());
		List results = query.list();
		Role role = (Role) results.get(0);
		
		AuthUser authUser1 = Utils.getLoggedInUser();
		User user = (User)sessionFactory.getCurrentSession().get(User.class, userVM.getId());
		user.name = userVM.getName();
		user.email = userVM.getEmail();
		user.gender = userVM.getGender();
		user.birthday = userVM.getBirthday();
		user.phone = userVM.getPhone();
		user.zone = userVM.getZone();
		user.address = userVM.getAddress();
		user.state = userVM.getState();
		user.district = userVM.getDistrict();
		user.postCode = userVM.getPostCode();
		user.setEntityName(role.getName());
		List<ZipCode> codes = new ArrayList<>();
		for(ZoneVM vm : userVM.getIds()){
			codes.add((ZipCode) sessionFactory.getCurrentSession().get(ZipCode.class, vm.getId()));
		}
		user.setZipCodes(codes);
		
		if(authUser1.getEntityName().equals("General Manager") || authUser1.getEntityName().equals("Sellout-Regional")){
			System.out.println("User : " + Long.valueOf(userVM.user));
			User dealer = (User) sessionFactory.getCurrentSession().get(User.class,Long.valueOf(userVM.user));
			user.setUser(dealer);
		}
		
		removeAlluserProductMapping(userVM.getId());
		for(ProductVM productVM : userVM.getProducts()) {
			if(productVM.getSelected() == true){
				jt.update("insert into user_product (user_product.User_id,user_product.products_id) values (?,?)",
						new Object[] {user.getId(), productVM.getId()});
			}
		}
		query = sessionFactory.getCurrentSession().createQuery("FROM AuthUser Where entityId = :id"); 
		query.setParameter("id", userVM.getId());
		results = query.list();
		AuthUser authUser = (AuthUser) results.get(0);
		authUser.setEntityName(role.getName());
		List<Role> roles = new ArrayList<>();
		roles.add(role);
		authUser.setRoles(roles);
		authUser.setEmail(userVM.getEmail());
		authUser.setUsername(userVM.getEmail());
		authUser.setName(userVM.getName());
		sessionFactory.getCurrentSession().update(authUser);
		
		sessionFactory.getCurrentSession().update(user);
		sessionFactory.getCurrentSession().flush();
		return getUserDetails();
	}
	
	public List<UserVM> changeStatusOfUser(UserVM userVM) {
		User user = (User)sessionFactory.getCurrentSession().get(User.class, userVM.getId());
		user.name = userVM.getName();
		user.email = userVM.getEmail();
		user.gender = userVM.getGender();
		user.birthday = userVM.getBirthday();
		user.phone = userVM.getPhone();
		user.zone = userVM.getZone();
		user.address = userVM.getAddress();
		user.state = userVM.getState();
		user.district = userVM.getDistrict();
		user.postCode = userVM.getPostCode();
		removeAlluserProductMapping(userVM.getId());
		for(ProductVM productVM : userVM.getProducts()) {
			if(productVM.getSelected() == true){
				jt.update("insert into user_product (user_product.User_id,user_product.products_id) values (?,?)",
						new Object[] {user.getId(), productVM.getId()});
			}
		}
		sessionFactory.getCurrentSession().update(user);
		sessionFactory.getCurrentSession().flush();
		return getUserDetails();
	}


	public List<DealerConfigurationVM> getDealersByZipCode(Long zipCode) {
		List<DealerConfigurationVM> vms = new ArrayList<DealerConfigurationVM>();
		String sql = "select u.name as name, uz.percentage as percentage, u.address as address, u.id as id from user as u,user_zipcode as uz where uz.zipCodes_id = ? and uz.user_id = u.id and u.entityname = 'Dealer'";

		List<Map<String, Object>> rows = jt.queryForList(sql,new Object[] { zipCode});

		Map<String,List> dataList = new HashMap<String, List>();
		if(rows.size() != 0 ){
			Float percentage =(float)( Math.round((100/(float)rows.size())*100)/100D );
			for(Map map : rows) {
				DealerConfigurationVM vm = new DealerConfigurationVM();
				vm.id =  (Long) map.get("id");
				vm.zipCode = zipCode;
				vm.dealerName = (String) map.get("name");
				vm.dealerAddress = (String) map.get("address");
				vm.percentage =  (Float) map.get("percentage");
				if(map.get("percentage") == null){
					vm.percentage = percentage;
				}
				vms.add(vm);
			}
		}
		return vms;
		
	}
	
	public List<UserVM> getUserDetails(){
		Session session = sessionFactory.getCurrentSession();
		AuthUser user = Utils.getLoggedInUser();
		String sql = "Select * FROM user as u where u.entityName != 'Dealer'";
		/*if(user.getEntityName().equals("General Manager") || user.getEntityName().equals("Sellout-Regional")){
			sql = "Select * FROM user as u, dealer_user as ud where u.id = ud.User_id and u.id IN (Select au.entityId from authusers as au where au.entityName = 'Sales Consultant')";
		}*/
		
		List<Map<String, Object>> rows=  jt.queryForList(sql); 
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");   
		List<UserVM> userList = new ArrayList<UserVM>();
		for(Map row : rows) {
			UserVM vm = new UserVM();
			vm.id = (Long) row.get("id");
			vm.name = (String) row.get("name");
			vm.address = (String) row.get("address");
			vm.birthday = (String) row.get("birthday");
			vm.email = (String) row.get("email");
			vm.gender = (String) row.get("gender");
			vm.phone = (String) row.get("phone");
			vm.postCode = (String) row.get("postCode");
			if(!user.getEntityName().equals("Admin"))
			//vm.dealer = (Long) row.get("dealer_id");
			vm.products = new ArrayList<ProductVM>();
			vm.status =  (Boolean) row.get("status") == false  ? "Inactive" : "Active";			
			vm.zone = (String) row.get("zone");
			vm.state =  (String) row.get("state");
			vm.district =  (String) row.get("district");
			vm.user = (Long) row.get("user_id");
			sql = "Select * from userrole as ur,  roles as r where r.role_id = ur.role_id and ur.user_id = (Select au.auth_id from authusers as au where au.entityId = "+(Long) row.get("id")+")";
			List<Map<String, Object>> roles=  jt.queryForList(sql);
			if(roles.size() != 0){
				Map map = roles.get(0);
				ZoneVM zoneVM = new ZoneVM();
				zoneVM.setName((String)map.get("name"));
				zoneVM.setRoleId((int)map.get("role_id"));
				vm.role = zoneVM;
			}
			sql = "Select * from product pp left join (select products_id from user_product up where user_id = ?) a on pp.id = a.products_id";
			rows = jt.queryForList(sql,new Object[] { (Long) row.get("id")});
			List<ProductVM> products = new ArrayList<ProductVM>();
			for(Map mapProduct : rows) {
				ProductVM pvm = new ProductVM();
				pvm.id = (Long) mapProduct.get("id");
				pvm.name = (String) mapProduct.get("name");
				if(mapProduct.get("products_id") != null){
					pvm.setSelected(true);
				}
				products.add(pvm);
			}
			vm.products = products;
			vm.setIds(getAllDealerConfig((Long) row.get("id")));
			userList.add(vm);
		}
		
		/*AuthUser auth = new AuthUser();
		User product = new User();
		product.setId((long) 96);
		auth.setId((long) 96);
		session.delete(product);
		session.delete(auth);
		session.flush();*/
		
		return userList;
	}
	
	public List<RolesVM> getDetailsForRoles(){
		System.out.println("Roles");
		String sql = "select role_id,name,report_freq from roles";
		
		List<Map<String, Object>> rows=  jt.queryForList(sql);
		List<RolesVM> roleList = new ArrayList<RolesVM>();
		for(Map row : rows){
			RolesVM r = new RolesVM();
			r.role_id =  (int) row.get("role_id");
			r.name = (String) row.get("name");
			r.report_freq = (String) row.get("report_freq");
			roleList.add(r);
		}
		
		return roleList;
	}
	
	
	public void updateDealerConfig(List<DealerConfigurationVM> configurationVMs) {
		for(DealerConfigurationVM configurationVM : configurationVMs){
			jt.update("UPDATE user_zipcode SET user_zipcode.percentage = "+configurationVM.percentage+" where user_zipcode.zipcodes_id = "+configurationVM.getZipCode()+" and user_zipcode.User_id="+configurationVM.getId());
		}
	}


	public void updateGeneralConfig(GeneralConfigVM configurationVM) {
		GeneralConfig configuration = (GeneralConfig) sessionFactory.getCurrentSession().get(GeneralConfig.class, configurationVM.getId());
		configuration.setFirstEscalationTime(configurationVM.getFirstEscalationTime());
		configuration.setFollowUpReminder(configurationVM.getFollowUpReminder());
		configuration.setFrequencyReport(configurationVM.getFrequencyReport());
		configuration.setSubsequentEscalationTime(configurationVM.getSubsequentEscalationTime());
		configuration.setFollowUpReminderCount(configurationVM.getFollowUpReminderCount());
		sessionFactory.getCurrentSession().update(configuration);
		
	}
	public void updateReportFrequency(List<RolesVM> role){
		Session session = sessionFactory.openSession();
		
		Transaction tx = null;
	      try{
	         tx = session.beginTransaction();
	          
	         for(RolesVM r : role){
	 			System.out.println(r.getRole_id());
	 			Role rol = (Role) session.get(Role.class, r.getRole_id());
	 			//rol.setName(r.getName());
	 			//rol.setReport_freq(r.getReport_freq());
	 			
	 			rol.name = r.getName();
	 			rol.report_freq = r.getReport_freq();
	 			session.update(rol);
	 		}
	         tx.commit();
	      }catch (HibernateException e) {
	         if (tx!=null) tx.rollback();
	         e.printStackTrace(); 
	      }finally {
	         session.close(); 
	      }
		
	}
	


	public List<ZoneVM> getRSMByZone(String state, String query) {
		
		String sql = "Select id,name from user as u WHERE u.name LIKE ('"+query+"%') and u.state_id = (Select id from state where name = '"+state+"') and u.entityName = 'RSM'";
		List<Map<String,Object>> rows = jt.queryForList(sql);
		List<ZoneVM> userVMs = new ArrayList<ZoneVM>();
		for(Map mapUser : rows) {
			ZoneVM uvm = new ZoneVM();
			uvm.id = (Long) mapUser.get("id");
			uvm.name = (String) mapUser.get("name");
			userVMs.add(uvm);
		}
		
		return userVMs;
		
	}

	public void changeStatus(String sql, List<Long> ids) {
		Map<String, List<Long>> param = Collections.singletonMap("ids",ids); 
		NamedParameterJdbcTemplate  namedParameterJdbcTemplate = new  
				NamedParameterJdbcTemplate(jt.getDataSource());
		namedParameterJdbcTemplate.update(sql, param);
		
	}

	public List<ZoneVM> getDealersByDistrict(Long district) {
		String sql = "Select id,dealerName from dealer WHERE district = (Select d.name from district d where d.id = ?)";
		List<Map<String,Object>> rows = jt.queryForList(sql,new Object[] {district});
		List<ZoneVM> vms = new ArrayList<ZoneVM>();
		for(Map mapUser : rows) {
			ZoneVM vm = new ZoneVM();
			vm.id = (Long) mapUser.get("id");
			vm.name = (String) mapUser.get("dealerName");
			vms.add(vm);
		}
		return vms;
	}

	public List<ZoneVM> getTSRByZone(String state, String query) {

		String sql = "Select id,name from user as u WHERE u.name LIKE ('"+query+"%') and u.state_id = (Select id from state where name = '"+state+"') and u.entityName = 'TSR'";
		List<Map<String,Object>> rows = jt.queryForList(sql);
		List<ZoneVM> userVMs = new ArrayList<ZoneVM>();
		for(Map mapUser : rows) {
			ZoneVM uvm = new ZoneVM();
			uvm.id = (Long) mapUser.get("id");
			uvm.name = (String) mapUser.get("name");
			userVMs.add(uvm);
		}
		
		return userVMs;
	}

	public Map getZoneStateProduct() {
		AuthUser user = Utils.getLoggedInUser();
		String sql = "";
		Map<String,List> dataList = new HashMap<String, List>();
		if(!(user.getEntityName().equals("Category Manager") || user.getEntityName().equals("Sellout-Regional") || 
				user.getEntityName().equals("TSR") || user.getEntityName().equals("Sales Executive") || user.getEntityName().equals("RSM") || user.getEntityName().equals("Admin") || user.getEntityName().equals("CEO") || user.getEntityName().equals("General Manager"))){
			return dataList;
		}
		dataList.put("zoneList", getZone());
		if(user.getEntityName().equals("Category Manager") || user.getEntityName().equals("TSR") || user.getEntityName().equals("Sales Executive") || user.getEntityName().equals("RSM") || user.getEntityName().equals("Sellout-Regional")){
			sql = "select id,name from product where product.id IN (SELECT user_product.products_id from user_product WHERE user_product.User_id = "+user.getEntityId()+") ";
			if( user.getEntityName().equals("TSR") || user.getEntityName().equals("Sales Executive") || user.getEntityName().equals("RSM") ){
				String dealersql = "select id,name from user where entityName = 'Dealer' and id In ( SELECT DISTINCT(a.User_id) from user_zipcode as a, user_zipcode as b where"
					+" a.zipCodes_id = b.zipCodes_id and b.User_id = "+user.getEntityId()+")";
				List<ZoneVM> dealerList = new ArrayList<ZoneVM>();
				List<Map<String, Object>> rows = jt.queryForList(dealersql);
				for(Map map : rows) {
					ZoneVM vm = new ZoneVM();
					vm.id = (Long) map.get("id");
					vm.name = (String) map.get("name");
					dealerList.add(vm);
				}
				
				dataList.put("dealerList", dealerList);
			}
		}
		if(user.getEntityName().equals("Admin") || user.getEntityName().equals("CEO") || user.getEntityName().equals("General Manager")){
			sql = "select id,name from product";
		}
		List<Map<String,Object>> rows = jt.queryForList(sql);
		List<ProductVM> productList = new ArrayList<ProductVM>();
		for(Map map : rows) {
			ProductVM vm = new ProductVM();
			vm.id = (Long) map.get("id");
			vm.name = (String) map.get("name");
			productList.add(vm);
		}
		dataList.put("productList", productList);
		return dataList;
	}

	public List<ZoneVM> getStateByZone(String zone) {
		List<Map<String, Object>> rows = jt.queryForList(getStateByZoneSql(zone));
		List<ZoneVM> stateList = new ArrayList<ZoneVM>();
		for(Map map : rows) {
			ZoneVM vm = new ZoneVM();
			vm.id = (Long) map.get("id");
			vm.name = (String) map.get("name");
			stateList.add(vm);
		}
		return stateList;
	}
	
	public String getStateByZoneSql(String zone){
		if(zone.equals("user")){
			AuthUser authUser = Utils.getLoggedInUser();
			return "Select DISTINCT(ld.state) as name from lead l, leaddetails ld where ld.id = l.leadDetails_id and l.zone = (select user.zone from user where user.id = "+authUser.getEntityId()+")";
		}
		return "Select DISTINCT(ld.state) as name from lead l, leaddetails ld where ld.id = l.leadDetails_id and l.zone ='"+zone+"'";
	}

	
}
