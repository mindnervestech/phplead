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
import com.mnt.businessApp.viewmodel.DealerVM;
import com.mnt.businessApp.viewmodel.GeneralConfigVM;
import com.mnt.businessApp.viewmodel.PinsVM;
import com.mnt.businessApp.viewmodel.ProductVM;
import com.mnt.businessApp.viewmodel.RolesVM;
import com.mnt.businessApp.viewmodel.SaveUserVM;
import com.mnt.businessApp.viewmodel.UserVM;
import com.mnt.businessApp.viewmodel.ZoneVM;
import com.mnt.entities.authentication.AuthUser;
import com.mnt.entities.authentication.District;
import com.mnt.entities.authentication.Role;
import com.mnt.entities.businessApp.Dealer;
import com.mnt.entities.businessApp.DealerConfiguration;
import com.mnt.entities.businessApp.GeneralConfig;
import com.mnt.entities.businessApp.Product;
import com.mnt.entities.businessApp.State;
import com.mnt.entities.businessApp.User;
import com.mnt.entities.businessApp.ZipCode;
import com.mnt.entities.businessApp.Zone;

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

	private List<DealerVM> getDealers(List<ZoneVM> zoneList) {
		AuthUser user = Utils.getLoggedInUser();
		Session session = sessionFactory.getCurrentSession();
		Query query;
		if(user.getEntityName().equals("RSM") || user.getEntityName().equals("TSR") ){
			List<Long> ids = jt.queryForList("SELECT du.dealer_id from dealer_user as du where du.user_id = "+user.getEntityId(), Long.class);
			query = session.createQuery("FROM Dealer where id IN (:ids)");
			query.setParameterList("ids", ids);
		}
		else if(user.getEntityName().equals("ZSM") || user.getEntityName().equals("Sellout Manager")){
			User user2 = (User) sessionFactory.getCurrentSession().get(User.class, user.getEntityId());
			query = session.createQuery("FROM Dealer where zone = "+user2.getZone().getId());
		}
		else if(user.getEntityName().equals("Admin") || user.getEntityName().equals("CEO") || user.getEntityName().equals("General Manager")){
			query = session.createQuery("FROM Dealer");
		}
		else {
			return null;
		}
		List<DealerVM> vms = new ArrayList<DealerVM>();
		List<Dealer> dealers = query.list();  
		List<ProductVM> products = getProductList();
		for (Dealer dealer : dealers){
			DealerVM vm = new DealerVM(dealer);
			vm.setPins(getAllDealerConfig(dealer.getId()));
			for(ZoneVM zone : zoneList){
				if(dealer.getZone().equals(zone.name)){
					vm.setZone(zone);
				}
			}
			/*for(ZoneVM zone : territoryList){
				if(dealer.getTerritory().equals(zone.name)){
					vm.setTerritory(zone);
				}
			}*/
			vm.setProducts(products);
			vms.add(vm);
		}
		return vms;
	}

	public List<ZoneVM> getDistricts() {
		List<Map<String, Object>> rows = jt.queryForList("select * from district");
		List<ZoneVM> districtList = new ArrayList<ZoneVM>();
		for(Map map : rows) {
			ZoneVM vm = new ZoneVM();
			vm.id = (Long) map.get("id");
			vm.name = (String) map.get("name");
			districtList.add(vm);
		}
		return districtList;
	}

	public List<ZoneVM> getStates() {
		List<Map<String, Object>> rows = jt.queryForList("select * from state");
		List<ZoneVM> stateList = new ArrayList<ZoneVM>();
		for(Map map : rows) {
			ZoneVM vm = new ZoneVM();
			vm.id = (Long) map.get("id");
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
		List<Map<String, Object>> rows = jt.queryForList("select * from zone");
		List<ZoneVM> zoneList = new ArrayList<ZoneVM>();
		for(Map map : rows) {
			ZoneVM vm = new ZoneVM();
			vm.id = (Long) map.get("id");
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
		dataList.put("zoneList", getZone());
		dataList.put("stateList", getStates());
		dataList.put("districtList", getDistricts());
		dataList.put("roleList", getRoles());
		dataList.put("productList", getProductList());
		dataList.put("userList", getUserDetails());
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


	public List<PinsVM> getPinCodes(String query) {

		String sql = "select * from zipcode WHERE id LIKE '"+query+"%'";

		List<Map<String, Object>> rows = jt.queryForList(sql);
		List<PinsVM> pinsList = new ArrayList<PinsVM>();
		for(Map map : rows) {
			PinsVM vm = new PinsVM();
			vm.pin = (Long) map.get("id");
			pinsList.add(vm);
		}

		return pinsList;
	}


	public List<UserVM> saveUser(SaveUserVM userVM) {
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
		user.zone = (Zone) sessionFactory.getCurrentSession().get(Zone.class, Long.parseLong(userVM.getZone()));
		user.state = (State) sessionFactory.getCurrentSession().get(State.class, Long.parseLong(userVM.getState()));
		user.district = (District) sessionFactory.getCurrentSession().get(District.class, Long.parseLong(userVM.getDistrict()));
		user.setEntityName(role.getName());
		if(userVM.dealer != null){
			((Dealer) session.get(Dealer.class, userVM.dealer)).addUser(user);
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
		jt.update("DELETE FROM dealerconfiguration WHERE dealer_id = ?", new Object[] { id});
	}
	
	public void removeAlluserProductMapping(Long id){
		jt.update("DELETE FROM user_product WHERE User_id = ?", new Object[] {id});
	}
	
	public List<PinsVM> getAllDealerConfig(Long id){
		String sql = "select * from dealerconfiguration WHERE dealer_id = ?";

		List<Map<String, Object>> rows = jt.queryForList(sql,new Object[] { id});
		List<PinsVM> vms = new ArrayList<PinsVM>();
		Map<String,List> dataList = new HashMap<String, List>();
		for(Map map : rows) {
			PinsVM vm = new PinsVM();
			vm.pin = (Long) map.get("zipCode_id");
			vms.add(vm);
		}
		return vms;
	}
	
	public void saveDealer(DealerVM dealerVM) {
		Session session = sessionFactory.getCurrentSession();
		Dealer dealer = new Dealer();
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
		dealer.setUser(user);
		dealer.address = dealerVM.getAddress();
		dealer.state = dealerVM.getState();
		dealer.district = dealerVM.getDistrict();
		dealer.subDistrict = dealerVM.getSubdist();
		dealer.zipCode = dealerVM.getZipCode();
		List<Product> products = new ArrayList<>();
		for(ProductVM productVM : dealerVM.getProducts()) {
			if(productVM.getSelected() == true){
				products.add((Product)session.get(Product.class, productVM.getId()));
			}
		}
		dealer.setProducts(products);
		session.save(dealer);
		
		for(PinsVM vm : dealerVM.getPins()){
			DealerConfiguration configuration = new DealerConfiguration();
			configuration.zipCode = getZipCodeById(vm.getPin());
			configuration.dealer = dealer;
			session.save(configuration);
		}
		
		Query query = session.createQuery("FROM Role Where name = :name"); 
		query.setParameter("name", "Dealer");
		List results = query.list();
		Role role = (Role) results.get(0);

		String randomStr = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random rnd = new Random();

		StringBuilder sb = new StringBuilder( 8 );
		for( int i = 0; i < 8; i++ ) 
			sb.append( randomStr.charAt( rnd.nextInt(randomStr.length()) ) );
		
		AuthUser authUser = new AuthUser();
		authUser.setEmail(dealer.getEmail());
		authUser.setPassword(sb.toString());
		authUser.setEntityId(dealer.getId());
		authUser.setUsername(dealer.getEmail());
		authUser.setEntityName("Dealer");
		authUser.setName(dealer.getDealerName());
		List<Role> roles = new ArrayList<>();
		roles.add(role);
		authUser.setRoles(roles);
		session.save(authUser);
	}


	public void updateDealer(DealerVM dealerVM) {
		Session session = sessionFactory.getCurrentSession();
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
		}
		
	}


	public List<UserVM> updateUser(UserVM userVM) {
		User user = (User)sessionFactory.getCurrentSession().get(User.class, userVM.getId());
		user.name = userVM.getName();
		user.email = userVM.getEmail();
		user.gender = userVM.getGender();
		user.birthday = userVM.getBirthday();
		user.phone = userVM.getPhone();
		user.zone = (Zone) sessionFactory.getCurrentSession().get(Zone.class, userVM.getZone().getId());
		user.address = userVM.getAddress();
		user.state = (State) sessionFactory.getCurrentSession().get(State.class, userVM.getState().getId());
		user.district = (District) sessionFactory.getCurrentSession().get(District.class, userVM.getDistrict().getId());
		user.postCode = userVM.getPostCode();
		removeAlluserProductMapping(userVM.getId());
		for(ProductVM productVM : userVM.getProducts()) {
			if(productVM.getSelected() == true){
				jt.update("insert into user_product (user_product.User_id,user_product.products_id) values (?,?)",
						new Object[] {user.getId(), productVM.getId()});
			}
		}
		jt.update("DELETE FROM dealer_user WHERE user_id = ? ",userVM.getId());
		if(userVM.dealer != null){
			((Dealer) sessionFactory.getCurrentSession().get(Dealer.class, userVM.dealer)).addUser(user);
		}
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
		user.zone = (Zone) sessionFactory.getCurrentSession().get(Zone.class, userVM.getZone().getId());
		user.address = userVM.getAddress();
		user.state = (State) sessionFactory.getCurrentSession().get(State.class, userVM.getState().getId());
		user.district = (District) sessionFactory.getCurrentSession().get(District.class, userVM.getDistrict().getId());
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
		String sql = "select d.dealerName as name, dc.percentage as percentage, dc.id as id, d.address as address from dealer as d ,dealerconfiguration as dc where dc.zipCode_id = ? and dc.dealer_id = d.id";

		List<Map<String, Object>> rows = jt.queryForList(sql,new Object[] { zipCode});
		List<DealerConfigurationVM> vms = new ArrayList<DealerConfigurationVM>();
		Map<String,List> dataList = new HashMap<String, List>();
		if(rows.size() != 0 ){
			Float percentage =(float)( Math.round((100/(float)rows.size())*100)/100D );
			for(Map map : rows) {
				DealerConfigurationVM vm = new DealerConfigurationVM();
				vm.id = (Long) map.get("id");
				vm.zipCode = zipCode;
				vm.dealerName = (String) map.get("name");
				vm.dealerAddress = (String) map.get("address");
				vm.percentage = (Float) map.get("percentage");
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
		String sql = "Select * FROM user as u where u.id IN (Select au.entityId from authusers as au where au.entityName != 'Sales Consultant')";
		if(user.getEntityName().equals("General Manager") || user.getEntityName().equals("Sellout-Regional")){
			sql = "Select * FROM user as u, dealer_user as ud where u.id = ud.User_id and u.id IN (Select au.entityId from authusers as au where au.entityName = 'Sales Consultant')";
		}
		
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
			vm.dealer = (Long) row.get("dealer_id");
			vm.products = new ArrayList<ProductVM>();
			vm.status =  (Boolean) row.get("status") == false  ? "Inactive" : "Active";			
			sql = "Select * from zone as z where z.id = ?";
			rows = jt.queryForList(sql,new Object[] { (Long) row.get("zone_id")});
			vm.zone =   new ZoneVM(rows.get(0));
			sql = "Select * from state as s where s.id = ?";
			rows = jt.queryForList(sql,new Object[] { (Long) row.get("state_id")});
			vm.state =   new ZoneVM(rows.get(0));
			sql = "Select * from district as d where d.id = ?";
			rows = jt.queryForList(sql,new Object[] { (Long) row.get("district_id")});
			vm.district =   new ZoneVM(rows.get(0));
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
			userList.add(vm);
		}
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
			
			System.out.println("Name" + r.name);
			
			roleList.add(r);
		}
		
		return roleList;
	}
	
	
	public void updateDealerConfig(List<DealerConfigurationVM> configurationVMs) {
		for(DealerConfigurationVM configurationVM : configurationVMs){
			DealerConfiguration configuration = (DealerConfiguration) sessionFactory.getCurrentSession().get(DealerConfiguration.class, configurationVM.getId());
			configuration.percentage = configurationVM.getPercentage();
			sessionFactory.getCurrentSession().update(configuration);
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
		String sql = "select * from zone";
		
		Map<String,List> dataList = new HashMap<String, List>();
		if(!(user.getEntityName().equals("Category Manager") || user.getEntityName().equals("Sellout-Regional") || 
				user.getEntityName().equals("TSR") || user.getEntityName().equals("RSM") || user.getEntityName().equals("Admin") || user.getEntityName().equals("CEO") || user.getEntityName().equals("General Manager"))){
			return dataList;
		}
		dataList.put("zoneList", getZone());
		if(user.getEntityName().equals("Category Manager") || user.getEntityName().equals("TSR") || user.getEntityName().equals("RSM") || user.getEntityName().equals("Sellout-Regional")){
			sql = "select id,name from product where product.id IN (SELECT user_product.products_id from user_product WHERE user_product.User_id = "+user.getEntityId()+") ";
			if( user.getEntityName().equals("TSR") || user.getEntityName().equals("RSM") ){
				String dealersql = "select id,dealerName from Dealer where id In ( SELECT du.dealer_id from dealer_user as du where du.user_id = "+user.getEntityId()+" )";
				List<ZoneVM> dealerList = new ArrayList<ZoneVM>();
				List<Map<String, Object>> rows = jt.queryForList(dealersql);
				for(Map map : rows) {
					ZoneVM vm = new ZoneVM();
					vm.id = (Long) map.get("id");
					vm.name = (String) map.get("dealerName");
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
		String sql = "Select id,name from state WHERE state.zone_id = (SELECT id from zone WHERE zone.name = '"+zone+"')";
		if(zone.equals("user")){
			AuthUser authUser = Utils.getLoggedInUser();
			sql = "Select id,name from state WHERE state.zone_id = (select user.zone_id from user where user.id = "+authUser.getEntityId()+")";
		}
		List<Map<String, Object>> rows = jt.queryForList(sql);
		List<ZoneVM> stateList = new ArrayList<ZoneVM>();
		for(Map map : rows) {
			ZoneVM vm = new ZoneVM();
			vm.id = (Long) map.get("id");
			vm.name = (String) map.get("name");
			stateList.add(vm);
		}
		return stateList;
	}
	
	/*public Map<String,Map<String, List<Long>>> getProductUserMapping() {
		String sql = "select up.product_id,  entityName, id from user, user_product up where user.id = up.user_id";
		String sql = "select up.product_id,  entityName, id from dealer, dealer_product up where dealer.id = up.dealer_id";
		
	}*/
	
}
