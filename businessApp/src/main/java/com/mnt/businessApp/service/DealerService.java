package com.mnt.businessApp.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.stereotype.Service;

import com.mnt.businessApp.viewmodel.DealerConfigurationVM;
import com.mnt.businessApp.viewmodel.DealerVM;
import com.mnt.businessApp.viewmodel.GeneralConfigVM;
import com.mnt.businessApp.viewmodel.PinsVM;
import com.mnt.businessApp.viewmodel.ProductVM;
import com.mnt.businessApp.viewmodel.SaveUserVM;
import com.mnt.businessApp.viewmodel.UserVM;
import com.mnt.businessApp.viewmodel.ZoneVM;
import com.mnt.entities.businessApp.Dealer;
import com.mnt.entities.businessApp.DealerConfiguration;
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
		String sql = "select * from zone";

		List<Map<String, Object>> rows = jt.queryForList(sql);
		List<ZoneVM> zoneList = new ArrayList<ZoneVM>();
		Map<String,List> dataList = new HashMap<String, List>();
		for(Map map : rows) {
			ZoneVM vm = new ZoneVM();
			vm.id = (Long) map.get("id");
			vm.name = (String) map.get("name");
			zoneList.add(vm);
		}
		dataList.put("zoneList", zoneList);
		sql = "select * from territory";
		rows = jt.queryForList(sql);
		List<ZoneVM> territoryList = new ArrayList<ZoneVM>();
		for(Map map : rows) {
			ZoneVM vm = new ZoneVM();
			vm.id = (Long) map.get("id");
			vm.name = (String) map.get("name");
			territoryList.add(vm);
		}
		dataList.put("territoryList", territoryList);

		sql = "select * from state";
		rows = jt.queryForList(sql);
		List<ZoneVM> stateList = new ArrayList<ZoneVM>();
		for(Map map : rows) {
			ZoneVM vm = new ZoneVM();
			vm.id = (Long) map.get("id");
			vm.name = (String) map.get("name");
			stateList.add(vm);
		}
		dataList.put("stateList", stateList);

		sql = "select * from district";
		rows = jt.queryForList(sql);
		List<ZoneVM> districtList = new ArrayList<ZoneVM>();
		for(Map map : rows) {
			ZoneVM vm = new ZoneVM();
			vm.id = (Long) map.get("id");
			vm.name = (String) map.get("name");
			districtList.add(vm);
		}
		dataList.put("districtList", districtList);

		Session session = sessionFactory.openSession();
		Transaction tx = null;
		List<DealerVM> vms = new ArrayList<DealerVM>();
		try{
			tx = session.beginTransaction();
			Query query = session.createQuery("FROM Dealer"); 
			List<Dealer> dealers = query.list();  
			for (Dealer dealer : dealers){
				DealerVM vm = new DealerVM(dealer);
				vm.setPins(getAllDealerConfig(dealer.getId()));
				vms.add(vm);
			}
			tx.commit();
		}catch (HibernateException e) {
			if (tx!=null) tx.rollback();
			e.printStackTrace(); 
		}finally {
			session.close(); 
		}
		dataList.put("dealerList", vms);
		return dataList;
	}


	public Map getDetailsForUser() {
		String sql = "select * from zone";

		List<Map<String, Object>> rows = jt.queryForList(sql);
		List<ZoneVM> zoneList = new ArrayList<ZoneVM>();
		Map<String,List> dataList = new HashMap<String, List>();
		for(Map map : rows) {
			ZoneVM vm = new ZoneVM();
			vm.id = (Long) map.get("id");
			vm.name = (String) map.get("name");
			zoneList.add(vm);
		}
		dataList.put("zoneList", zoneList);

		sql = "select * from state";
		rows = jt.queryForList(sql);
		List<ZoneVM> stateList = new ArrayList<ZoneVM>();
		for(Map map : rows) {
			ZoneVM vm = new ZoneVM();
			vm.id = (Long) map.get("id");
			vm.name = (String) map.get("name");
			stateList.add(vm);
		}
		dataList.put("stateList", stateList);

		sql = "select * from district";
		rows = jt.queryForList(sql);
		List<ZoneVM> districtList = new ArrayList<ZoneVM>();
		for(Map map : rows) {
			ZoneVM vm = new ZoneVM();
			vm.id = (Long) map.get("id");
			vm.name = (String) map.get("name");
			districtList.add(vm);
		}
		dataList.put("districtList", districtList);

		sql = "select * from roles";
		rows = jt.queryForList(sql);
		List<ZoneVM> roleList = new ArrayList<ZoneVM>();
		for(Map map : rows) {
			ZoneVM vm = new ZoneVM();
			vm.roleId =  (Integer) map.get("role_id");
			vm.name = (String) map.get("name");
			roleList.add(vm);
		}
		dataList.put("roleList", roleList);

		sql = "select * from product";
		rows = jt.queryForList(sql);
		List<ProductVM> productList = new ArrayList<ProductVM>();
		for(Map map : rows) {
			ProductVM vm = new ProductVM();
			vm.id = (Long) map.get("id");
			vm.name = (String) map.get("name");
			productList.add(vm);
		}
		dataList.put("productList", productList);

		dataList.put("userList", getUserDetails());

		return dataList;
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
		User user = new User();
		user.name = userVM.getName();
		user.email = userVM.getEmail();
		user.gender = userVM.getGender();
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		try {
			user.birthday = df.parse(userVM.getBirthday());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		user.phone = userVM.getPhone();
		user.role = userVM.getRoleName();
		user.address = userVM.getAddress();
		user.state = userVM.getState();
		user.district = userVM.getDistrict();
		user.postCode = userVM.getPostCode();
		sessionFactory.getCurrentSession().save(user);



		String randomStr = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random rnd = new Random();

		StringBuilder sb = new StringBuilder( 8 );
		for( int i = 0; i < 8; i++ ) 
			sb.append( randomStr.charAt( rnd.nextInt(randomStr.length()) ) );

		jt.update("insert into authusers (authusers.email_id,authusers.password,authusers.username) values (?,?,?)",
				new Object[] {userVM.getEmail(), sb.toString(), userVM.getEmail()});

		for(String productId : userVM.getProductlist()) {
			jt.update("insert into user_product (user_product.User_id,user_product.products_id) values (?,?)",
					new Object[] {user.getId(), Integer.parseInt(productId)});
		}
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

		Dealer dealer = new Dealer();
		dealer.dealerCode = dealerVM.getCode();
		dealer.dealerName = dealerVM.getName();
		dealer.customerGroup = dealerVM.getCustomerGroup();
		dealer.phone = dealerVM.getPhone();
		dealer.email = dealerVM.getEmail();
		dealer.zone = dealerVM.getZone();
		dealer.territory = dealerVM.getTerritory();
		dealer.rsm = dealerVM.getRsm();
		dealer.address = dealerVM.getAddress();
		dealer.state = dealerVM.getState();
		dealer.district = dealerVM.getDistrict();
		dealer.subDistrict = dealerVM.getSubdist();
		sessionFactory.getCurrentSession().save(dealer);
		
		for(PinsVM vm : dealerVM.getPins()){
			DealerConfiguration configuration = new DealerConfiguration();
			configuration.zipCode = getZipCodeById(vm.getPin());
			configuration.dealer = dealer;
			sessionFactory.getCurrentSession().save(configuration);
		}

		String randomStr = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random rnd = new Random();

		StringBuilder sb = new StringBuilder( 8 );
		for( int i = 0; i < 8; i++ ) 
			sb.append( randomStr.charAt( rnd.nextInt(randomStr.length()) ) );

		jt.update("insert into authusers (authusers.email_id,authusers.password,authusers.username,authusers.entityId) values (?,?,?,?)",
				new Object[] {dealerVM.getEmail(), sb.toString(), dealerVM.getCode(), dealer.getId()});

		String sql = "select roles.role_id from roles where roles.name = ?";

		Long roleId = (Long)jt.queryForObject(
				sql, new Object[] { "Dealer" }, Long.class);

		sql = "select authusers.auth_id from authusers where authusers.entityId = ?";

		Long authId = (Long)jt.queryForObject(
				sql, new Object[] { dealer.getId() }, Long.class);

		jt.update("insert into userrole (userrole.user_id,userrole.role_id) values (?,?)",
				new Object[] {authId,roleId});

		

	}


	public void updateDealer(DealerVM dealerVM) {

		Dealer dealer = (Dealer) sessionFactory.getCurrentSession().get(Dealer.class, dealerVM.getId());
		dealer.dealerCode = dealerVM.getCode();
		dealer.dealerName = dealerVM.getName();
		dealer.customerGroup = dealerVM.getCustomerGroup();
		dealer.phone = dealerVM.getPhone();
		dealer.email = dealerVM.getEmail();
		dealer.zone = dealerVM.getZone();
		dealer.territory = dealerVM.getTerritory();
		dealer.rsm = dealerVM.getRsm();
		dealer.address = dealerVM.getAddress();
		dealer.state = dealerVM.getState();
		dealer.district = dealerVM.getDistrict();
		dealer.subDistrict = dealerVM.getSubdist();
		sessionFactory.getCurrentSession().update(dealer);
		
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
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		try {
			user.birthday = df.parse(userVM.getBirthday());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		user.phone = userVM.getPhone();
		user.role = userVM.getRole();
		user.zone = userVM.getZone();
		user.address = userVM.getAddress();
		user.state = userVM.getState();
		user.district = userVM.getDistrict();
		user.postCode = userVM.getPostCode();
		removeAlluserProductMapping(userVM.getId());
		for(String productId : userVM.getProductList()) {
			jt.update("insert into user_product (user_product.User_id,user_product.products_id) values (?,?)",
					new Object[] {user.getId(), Integer.parseInt(productId)});
		}
		sessionFactory.getCurrentSession().update(user);
		return getUserDetails();
	}


	public List<DealerConfigurationVM> getDealersByZipCode(Long zipCode) {
		String sql = "select * from dealerconfiguration WHERE zipCode_id = ?";

		List<Map<String, Object>> rows = jt.queryForList(sql,new Object[] { zipCode});
		List<DealerConfigurationVM> vms = new ArrayList<DealerConfigurationVM>();
		Map<String,List> dataList = new HashMap<String, List>();
		if(rows.size() != 0 ){
			Float percentage =(float)( Math.round((100/(float)rows.size())*100)/100D );
			for(Map map : rows) {
				DealerConfigurationVM vm = new DealerConfigurationVM();
				vm.id = (Long) map.get("id");
				vm.zipCode = (Long) map.get("zipCode_id");
				Dealer dealer = (Dealer) sessionFactory.getCurrentSession().get(Dealer.class, (Long) map.get("dealer_id"));
				vm.dealerName = dealer.getDealerName();
				vm.dealerAddress = dealer.getAddress();
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
		String sql = "select * from user";
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");   
		List<Map<String,Object>> rows = jt.queryForList(sql);
		List<UserVM> userList = new ArrayList<UserVM>();
		for(Map map : rows) {
			UserVM vm = new UserVM();
			vm.id = (Long) map.get("id");
			vm.name = (String) map.get("name");
			vm.address = (String) map.get("address");

			vm.birthday = df.format((Date) map.get("birthday"));
			vm.email = (String) map.get("email");
			vm.gender = (String) map.get("gender");
			vm.phone = (String) map.get("phone");
			vm.postCode = (String) map.get("postCode");
			sql = "select roles.name from roles where roles.role_id = ?";
			String roleName = (String)jt.queryForObject(
					sql, new Object[] { Integer.parseInt((String) map.get("role")) }, String.class);
			vm.role = (String) map.get("role");
			vm.roleName = roleName;
			vm.products = new ArrayList<ProductVM>();
			sql = "select zone.name from zone where zone.id = ?";
			vm.zone = (String) map.get("name");
			vm.zoneName = (String) map.get("name");
			vm.state = (String) map.get("state");
			vm.district = (String) map.get("district");
			//Select * from product pp left join (select products_id from user_product up where user_id = 2) a on pp.id = a.products_id
			sql = "Select * from product pp left join (select products_id from user_product up where user_id = ?) a on pp.id = a.products_id";
			rows = jt.queryForList(sql,new Object[] { (Long) map.get("id")});
			List<ProductVM> products = new ArrayList<ProductVM>();
			for(Map mapProduct : rows) {
				ProductVM pvm = new ProductVM();
				pvm.id = (Long) mapProduct.get("id");
				pvm.name = (String) mapProduct.get("name");
				System.out.println((Long) map.get("id")+ " :: "+mapProduct.get("products_id"));
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


}
