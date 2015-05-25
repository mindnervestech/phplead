package com.mnt.businessApp.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mnt.businessApp.viewmodel.DealerVM;
import com.mnt.businessApp.viewmodel.PinsVM;
import com.mnt.businessApp.viewmodel.UserVM;
import com.mnt.businessApp.viewmodel.ZoneVM;
import com.mnt.entities.businessApp.Dealers;
import com.mnt.entities.businessApp.User;

@Controller
@RequestMapping(value="/api/business")
public class BusinessController {

	@Autowired
    private JdbcTemplate jt;
	
	@Autowired
    private SessionFactory sessionFactory;
	
	@RequestMapping(method=RequestMethod.GET)
	@ResponseBody
	public void businessApp() {
		System.out.println("I m here");
	}
	
	@Transactional
	@RequestMapping(value="/getDetailsForUser", method = RequestMethod.GET)
	public @ResponseBody Map getDetailsForUser(ModelMap model,HttpServletRequest request){
		String sql = "select * from zone";
		   
		List<Map<String, Object>> rows = jt.queryForList(sql);
		List<ZoneVM> zoneList = new ArrayList<>();
		Map<String,List> dataList = new HashMap<>();
		for(Map map : rows) {
			ZoneVM vm = new ZoneVM();
			vm.id = (Long) map.get("id");
			vm.name = (String) map.get("name");
			zoneList.add(vm);
		}
		dataList.put("zoneList", zoneList);
		
		sql = "select * from state";
		rows = jt.queryForList(sql);
		List<ZoneVM> stateList = new ArrayList<>();
		for(Map map : rows) {
			ZoneVM vm = new ZoneVM();
			vm.id = (Long) map.get("id");
			vm.name = (String) map.get("name");
			stateList.add(vm);
		}
		dataList.put("stateList", stateList);
		
		sql = "select * from district";
		rows = jt.queryForList(sql);
		List<ZoneVM> districtList = new ArrayList<>();
		for(Map map : rows) {
			ZoneVM vm = new ZoneVM();
			vm.id = (Long) map.get("id");
			vm.name = (String) map.get("name");
			districtList.add(vm);
		}
		dataList.put("districtList", districtList);
		
		sql = "select * from roles";
		rows = jt.queryForList(sql);
		List<ZoneVM> roleList = new ArrayList<>();
		for(Map map : rows) {
			ZoneVM vm = new ZoneVM();
			vm.roleId = (int) map.get("role_id");
			vm.name = (String) map.get("name");
			roleList.add(vm);
		}
		dataList.put("roleList", roleList);
		
		sql = "select * from product";
		rows = jt.queryForList(sql);
		List<ZoneVM> productList = new ArrayList<>();
		for(Map map : rows) {
			ZoneVM vm = new ZoneVM();
			vm.id = (Long) map.get("id");
			vm.name = (String) map.get("name");
			productList.add(vm);
		}
		dataList.put("productList", productList);
		
		sql = "select * from user";
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");   
		rows = jt.queryForList(sql);
		List<UserVM> userList = new ArrayList<>();
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
			sql = "select zone.name from zone where zone.id = ?";
			String zoneName = (String)jt.queryForObject(
					sql, new Object[] { Long.parseLong((String) map.get("zone")) }, String.class);
			vm.zone = (String) map.get("zone");
			vm.zoneName = zoneName;
			vm.state = (String) map.get("state");
			vm.district = (String) map.get("district");
			
			userList.add(vm);
		}
		dataList.put("userList", userList);
		
		return dataList;
	}
	
	@Transactional
	@RequestMapping(value="/getZones", method = RequestMethod.GET)
	public @ResponseBody Map getZones(ModelMap model,HttpServletRequest request){
		
		String sql = "select * from zone";
		   
		List<Map<String, Object>> rows = jt.queryForList(sql);
		List<ZoneVM> zoneList = new ArrayList<>();
		Map<String,List> dataList = new HashMap<>();
		for(Map map : rows) {
			ZoneVM vm = new ZoneVM();
			vm.id = (Long) map.get("id");
			vm.name = (String) map.get("name");
			zoneList.add(vm);
		}
		dataList.put("zoneList", zoneList);
		sql = "select * from territory";
		rows = jt.queryForList(sql);
		List<ZoneVM> territoryList = new ArrayList<>();
		for(Map map : rows) {
			ZoneVM vm = new ZoneVM();
			vm.id = (Long) map.get("id");
			vm.name = (String) map.get("name");
			territoryList.add(vm);
		}
		dataList.put("territoryList", territoryList);
		
		sql = "select * from state";
		rows = jt.queryForList(sql);
		List<ZoneVM> stateList = new ArrayList<>();
		for(Map map : rows) {
			ZoneVM vm = new ZoneVM();
			vm.id = (Long) map.get("id");
			vm.name = (String) map.get("name");
			stateList.add(vm);
		}
		dataList.put("stateList", stateList);
		
		sql = "select * from district";
		rows = jt.queryForList(sql);
		List<ZoneVM> districtList = new ArrayList<>();
		for(Map map : rows) {
			ZoneVM vm = new ZoneVM();
			vm.id = (Long) map.get("id");
			vm.name = (String) map.get("name");
			districtList.add(vm);
		}
		dataList.put("districtList", districtList);
		
		sql = "select * from dealers";
		rows = jt.queryForList(sql);
		List<DealerVM> dealerList = new ArrayList<>();
		for(Map map : rows) {
			DealerVM dealerVM = new DealerVM();
			dealerVM.id = (Long) map.get("id");
			dealerVM.name = (String) map.get("dealerName");
			dealerVM.address = (String) map.get("address");
			dealerVM.customerGroup = (String) map.get("customerGroup");
			dealerVM.code = (String) map.get("dealerCode");
			dealerVM.district = (String) map.get("district");
			dealerVM.email = (String) map.get("email");
			dealerVM.phone = (String) map.get("phone");
			String pinList = (String)map.get("pins");
			String arr[] = pinList.split(",");
			List<String> lst = new ArrayList<>();
			List<PinsVM> pinsvm = new ArrayList<>();
			for(int i=0;i<arr.length;i++) {
				PinsVM pinObj = new PinsVM();
				if(i==0) {
					lst.add(arr[i].substring(1));
					pinObj.pin = arr[i].substring(1);
				} else {
					if(i == arr.length-1) {
						lst.add(arr[i].substring(0, arr[i].length()-1));
						pinObj.pin = arr[i].substring(0, arr[i].length()-1);
					} else {
						lst.add(arr[i]);
						pinObj.pin = arr[i];
					}
				}
				pinsvm.add(pinObj);
			}
			dealerVM.pinsList = lst;
			dealerVM.pins = pinsvm;
			dealerVM.rsm = (String) map.get("rsm");
			dealerVM.state = (String) map.get("state");
			dealerVM.subdist = (String) map.get("subDistrict");
			dealerVM.territory = (String) map.get("territory");
			dealerVM.zipCode = (String) map.get("zipCode");
			dealerVM.zone = (String) map.get("zone");
			dealerList.add(dealerVM);
		}
		dataList.put("dealerList", dealerList);
		
		return dataList;
	}	
	
	
	@Transactional
	@RequestMapping(value="/getPincodes", method = RequestMethod.GET)
	public @ResponseBody List<PinsVM> getPincodes(ModelMap model,HttpServletRequest request){
		String sql = "select * from pin_codes";
		   
		List<Map<String, Object>> rows = jt.queryForList(sql);
		List<PinsVM> pinsList = new ArrayList<>();
		for(Map map : rows) {
			PinsVM vm = new PinsVM();
			vm.pin = (String) map.get("code");
			pinsList.add(vm);
		}
		
		return pinsList;
	}	
	
	@Transactional
	@RequestMapping(value="/saveUser", method = RequestMethod.POST)
	public @ResponseBody List<UserVM> saveUser(ModelMap model,@RequestBody UserVM userVM,HttpServletRequest request){
	
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
		user.role = userVM.getRole();
		user.zone = userVM.getZone();
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
		
		  for(int i=0;i<userVM.getProductList().size();i++) {
			  jt.update("insert into user_product (user_product.User_id,user_product.products_id) values (?,?)",
				      new Object[] {user.getId(), userVM.getProductList().get(i).getId()});
		  }
		   
		String sql = "select * from user";
		List<Map<String, Object>> rows = jt.queryForList(sql);
		List<UserVM> userList = new ArrayList<>();
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
			sql = "select zone.name from zone where zone.id = ?";
			String zoneName = (String)jt.queryForObject(
					sql, new Object[] { Long.parseLong((String) map.get("zone")) }, String.class);
			vm.zone = (String) map.get("zone");
			vm.zoneName = zoneName;
			vm.state = (String) map.get("state");
			vm.district = (String) map.get("district");
			
			userList.add(vm);
		}
		
		return userList;
	}	
	
	@Transactional
	@RequestMapping(value="/saveDealer", method = RequestMethod.POST)
	public @ResponseBody void saveDealer(ModelMap model,@RequestBody DealerVM dealerVM,HttpServletRequest request){
		
		Dealers dealer = new Dealers();
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
		dealer.zipCode = dealerVM.getZipCode();
		
		List<String> pins = new ArrayList<String>();
		
		for(PinsVM vm : dealerVM.getPins()) {
			pins.add(vm.getPin());
		}
		
		dealer.pins = pins.toString();
		sessionFactory.getCurrentSession().save(dealer);
		
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
	
	@Transactional
	@RequestMapping(value="/updateDealer", method = RequestMethod.POST)
	public @ResponseBody void updateDealer(ModelMap model,@RequestBody DealerVM dealerVM,HttpServletRequest request){
		
		Dealers dealer = (Dealers) sessionFactory.openSession().get(Dealers.class, dealerVM.getId());
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
		dealer.zipCode = dealerVM.getZipCode();
		
		List<String> pins = new ArrayList<String>();
		
		for(PinsVM vm : dealerVM.getPins()) {
			pins.add(vm.getPin());
		}
		
		dealer.pins = pins.toString();
		sessionFactory.getCurrentSession().update(dealer);
	
	}	
	
	@Transactional
	@RequestMapping(value="/updateUser", method = RequestMethod.POST)
	public @ResponseBody List<UserVM> updateUser(ModelMap model,@RequestBody UserVM userVM,HttpServletRequest request){
	
		User user = (User)sessionFactory.openSession().get(User.class, userVM.getId());
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
		
		sessionFactory.getCurrentSession().update(user);
		
		String sql = "select * from user";
		List<Map<String, Object>> rows = jt.queryForList(sql);
		List<UserVM> userList = new ArrayList<>();
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
			sql = "select zone.name from zone where zone.id = ?";
			String zoneName = (String)jt.queryForObject(
					sql, new Object[] { Long.parseLong((String) map.get("zone")) }, String.class);
			vm.zone = (String) map.get("zone");
			vm.zoneName = zoneName;
			vm.state = (String) map.get("state");
			vm.district = (String) map.get("district");
			
			userList.add(vm);
		}
		
		return userList;
	}	
	
}
