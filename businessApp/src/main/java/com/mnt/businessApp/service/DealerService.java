package com.mnt.businessApp.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.mnt.businessApp.viewmodel.DealerConfigurationVM;
import com.mnt.businessApp.viewmodel.DealerVM;
import com.mnt.businessApp.viewmodel.GeneralConfigVM;
import com.mnt.businessApp.viewmodel.PinsVM;
import com.mnt.businessApp.viewmodel.ProductVM;
import com.mnt.businessApp.viewmodel.SaveUserVM;
import com.mnt.businessApp.viewmodel.UserVM;
import com.mnt.businessApp.viewmodel.ZoneVM;
import com.mnt.entities.authentication.AuthUser;
import com.mnt.entities.authentication.District;
import com.mnt.entities.authentication.Role;
import com.mnt.entities.authentication.State;
import com.mnt.entities.authentication.Zone;
import com.mnt.entities.businessApp.Dealer;
import com.mnt.entities.businessApp.DealerConfiguration;
import com.mnt.entities.businessApp.GeneralConfig;
import com.mnt.entities.businessApp.Lead;
import com.mnt.entities.businessApp.User;
import com.mnt.entities.businessApp.ZipCode;

@Service
public class DealerService {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private JdbcTemplate jt;


	public Map getZones() {
		AuthUser user = ((AuthUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		
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

		Session session = sessionFactory.getCurrentSession();
		if(user.getEntityName().equals("RSM")){
			sql = "FROM Dealer where rsm_id = "+user.getEntityId();
		}
		if(user.getEntityName().equals("ZSM") || user.getEntityName().equals("Sellout Contact") || user.getEntityName().equals("Sellout Manager")){
			User user2 = (User) sessionFactory.getCurrentSession().get(User.class, user.getEntityId());
			sql = "FROM Dealer where zone = "+user2.getZone().getId();
		}
		List<DealerVM> vms = new ArrayList<DealerVM>();
		Query query = session.createQuery(sql);
		List<Dealer> dealers = query.list();  
		for (Dealer dealer : dealers){
			DealerVM vm = new DealerVM(dealer);
			vm.setPins(getAllDealerConfig(dealer.getId()));
			for(ZoneVM zone : zoneList){
				if(dealer.getZone().equals(zone.id+"")){
					vm.setZone(zone);
				}
			}
			for(ZoneVM zone : territoryList){
				if(dealer.getTerritory().equals(zone.id+"")){
					vm.setTerritory(zone);
				}
			}
			vms.add(vm);
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
			vm.roleId =  (int) map.get("role_id");
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
		} catch (Exception e) {
			//e.printStackTrace();
		}
		user.phone = userVM.getPhone();
		user.address = userVM.getAddress();
		user.postCode = userVM.getPostCode();
		user.zone = (Zone) sessionFactory.getCurrentSession().get(Zone.class, Long.parseLong(userVM.getZone()));
		user.state = (State) sessionFactory.getCurrentSession().get(State.class, Long.parseLong(userVM.getState()));
		user.district = (District) sessionFactory.getCurrentSession().get(District.class, Long.parseLong(userVM.getDistrict()));
		sessionFactory.getCurrentSession().save(user);
		
		Session session = sessionFactory.getCurrentSession();
		System.out.println("userVM.getRoleName() :: "+userVM.getRole());
		Query query = session.createQuery("FROM Role Where role_id = :id"); 
		query.setParameter("id", userVM.getRole());
		List results = query.list();
		Role role = (Role) results.get(0);

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
		List<Role> roles = new ArrayList<>();
		roles.add(role);
		authUser.setRoles(roles);
		sessionFactory.getCurrentSession().save(authUser);

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
		dealer.zone = dealerVM.getZone().getName();
		dealer.territory = dealerVM.getTerritory().getName();
		dealer.rsm = (User) sessionFactory.getCurrentSession().get(User.class, Long.valueOf( dealerVM.getRsm()).longValue());
		dealer.address = dealerVM.getAddress();
		dealer.state = dealerVM.getState();
		dealer.district = dealerVM.getDistrict();
		dealer.subDistrict = dealerVM.getSubdist();
		dealer.zipCode = dealerVM.getZipCode();
		sessionFactory.getCurrentSession().save(dealer);
		
		for(PinsVM vm : dealerVM.getPins()){
			DealerConfiguration configuration = new DealerConfiguration();
			configuration.zipCode = getZipCodeById(vm.getPin());
			configuration.dealer = dealer;
			sessionFactory.getCurrentSession().save(configuration);
		}
		
		Session session = sessionFactory.getCurrentSession();
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
		authUser.setUsername(dealer.getDealerCode());
		authUser.setEntityName("Dealer");
		List<Role> roles = new ArrayList<>();
		roles.add(role);
		authUser.setRoles(roles);
		sessionFactory.getCurrentSession().save(authUser);



	}


	public void updateDealer(DealerVM dealerVM) {

		Dealer dealer = (Dealer) sessionFactory.getCurrentSession().get(Dealer.class, dealerVM.getId());
		dealer.dealerCode = dealerVM.getCode();
		dealer.dealerName = dealerVM.getName();
		dealer.customerGroup = dealerVM.getCustomerGroup();
		dealer.phone = dealerVM.getPhone();
		dealer.email = dealerVM.getEmail();
		dealer.zone = dealerVM.getZone().getId()+"";
		dealer.territory = dealerVM.getTerritory().getId()+"";
		dealer.rsm = (User) sessionFactory.getCurrentSession().get(User.class, Long.valueOf( dealerVM.getRsm()).longValue());
		dealer.address = dealerVM.getAddress();
		dealer.state = dealerVM.getState();
		dealer.district = dealerVM.getDistrict();
		dealer.subDistrict = dealerVM.getSubdist();
		dealer.zipCode = dealerVM.getZipCode();
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
		Session session = sessionFactory.getCurrentSession();
		List<Map<String, Object>> rows=  jt.queryForList("Select * FROM user"); 
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");   
		List<UserVM> userList = new ArrayList<UserVM>();
		for(Map row : rows) {
			UserVM vm = new UserVM();
			vm.id = (Long) row.get("id");
			vm.name = (String) row.get("name");
			vm.address = (String) row.get("address");
            // TODO: Shahank handle null pointer in case of no birthdae
			vm.birthday = df.format((Date) row.get("birthday"));
			vm.email = (String) row.get("email");
			vm.gender = (String) row.get("gender");
			vm.phone = (String) row.get("phone");
			vm.postCode = (String) row.get("postCode");
			vm.products = new ArrayList<ProductVM>();
			String sql = "Select * from zone as z where z.id = ?";
			rows = jt.queryForList(sql,new Object[] { (Long) row.get("zone_id")});
			vm.zone =   new ZoneVM(rows.get(0));
			sql = "Select * from state as s where s.id = ?";
			rows = jt.queryForList(sql,new Object[] { (Long) row.get("state_id")});
			vm.state =   new ZoneVM(rows.get(0));
			sql = "Select * from district as d where d.id = ?";
			rows = jt.queryForList(sql,new Object[] { (Long) row.get("district_id")});
			vm.district =   new ZoneVM(rows.get(0));
			sql = "Select * from userrole as ur where ur.user_id = (Select au.auth_id from authusers as au where au.entityId = "+(Long) row.get("id")+")";
			List<Map<String, Object>> productRows=  jt.queryForList(sql);
			
			if(productRows.size() != 0){
				Map map = productRows.get(0);
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


	public List<UserVM> getRSMByZone(Long zone) {
		
		String sql = "Select * from user WHERE user.zone = ? and user.role = ?";
		List<Map<String,Object>> rows = jt.queryForList(sql,new Object[] {zone, 7L});
		List<UserVM> userVMs = new ArrayList<UserVM>();
		for(Map mapUser : rows) {
			UserVM uvm = new UserVM();
			uvm.id = (Long) mapUser.get("id");
			uvm.name = (String) mapUser.get("name");
			userVMs.add(uvm);
		}
		
		return userVMs;
		
	}
	
}
