package com.mnt.businessApp.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.app.VelocityEngine;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mnt.businessApp.service.DashBoardService;
import com.mnt.businessApp.service.DealerService;
import com.mnt.businessApp.service.LeadService;
import com.mnt.businessApp.service.MailService;
import com.mnt.businessApp.service.SchedularService;
import com.mnt.businessApp.viewmodel.DealerConfigurationVM;
import com.mnt.businessApp.viewmodel.DealerVM;
import com.mnt.businessApp.viewmodel.GeneralConfigVM;
import com.mnt.businessApp.viewmodel.LeadDetailsVM;
import com.mnt.businessApp.viewmodel.LeadHistoryVM;
import com.mnt.businessApp.viewmodel.LeadVM;
import com.mnt.businessApp.viewmodel.PinsVM;
import com.mnt.businessApp.viewmodel.ReassignUserVM;
import com.mnt.businessApp.viewmodel.ReassignVM;
import com.mnt.businessApp.viewmodel.RolesVM;
import com.mnt.businessApp.viewmodel.SaveUserVM;
import com.mnt.businessApp.viewmodel.UserVM;
import com.mnt.businessApp.viewmodel.ZoneVM;
import com.mnt.entities.businessApp.Roles;

@Controller
@RequestMapping(value="/api/business")
@PreAuthorize("isAuthenticated()")
public class BusinessController {

	@Autowired
	private SchedularService schedularService;
	
	@Autowired
	private LeadService leadService;
	
	@Autowired
	private DealerService dealerService;

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private DashBoardService dashBoardService;

	@Autowired
	private MailService mailService;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private VelocityEngine velocityEngine;
	
	@Autowired
    private JdbcTemplate jt;
	
	@Transactional
	@RequestMapping(value="/uploadLeads",method=RequestMethod.GET)
	public @ResponseBody String home() {
		schedularService.uploadandStoreExcel();
		return "HERE";
	}
	
	@Transactional
	@RequestMapping(value="/getLeads",method=RequestMethod.GET)
	public @ResponseBody List<LeadDetailsVM> getLeads() {
		return leadService.getAllLeadDetails();
	}
	
	@Transactional
	@RequestMapping(value="/getEscalatedLeads",method=RequestMethod.GET)
	public @ResponseBody List<LeadDetailsVM> getEscalatedLeads() {
		return leadService.getAllEscalatedLeadDetails();
	}
	
	@Transactional
	@RequestMapping(value="/getOpenLeads",method=RequestMethod.GET)
	public @ResponseBody List<LeadDetailsVM> getOpenLeads() {
		return leadService.getOpenLeads();
	}
	
	@Transactional
	@RequestMapping(value="/getWonLeads",method=RequestMethod.GET)
	public @ResponseBody List<LeadDetailsVM> getWonLeads() {
		return leadService.getWonLeads();
	}
	
	@Transactional
	@RequestMapping(value="/getLostLeads",method=RequestMethod.GET)
	public @ResponseBody List<LeadDetailsVM> getLostLeads() {
		return leadService.getLostLeads();
	}
	
	@Transactional
	@RequestMapping(value="/getFollowUpLeads",method=RequestMethod.GET)
	public @ResponseBody List<LeadDetailsVM> getFollowUpLeads() {
		return leadService.getFollowUpLeads();
	}
	
	
	@Transactional
	@RequestMapping(value="/lead/{id}",method=RequestMethod.GET)
	public @ResponseBody LeadVM getLeadInfoById(@PathVariable("id") Long id) {
		return leadService.getLeadVMById(id);
	}
	
	@Transactional
	@RequestMapping(value="/updateLead",method=RequestMethod.POST)
	public @ResponseBody void updateLead(@RequestBody LeadVM vm) {
		leadService.updateLead(vm);
	}
	
	@Transactional
	@RequestMapping(value="/createLead",method=RequestMethod.POST)
	public @ResponseBody void createLead(@RequestBody LeadVM vm) {
		leadService.createLead(vm);
	}

	@Transactional
	@RequestMapping(value="/lead/history/{id}",method=RequestMethod.GET)
	public @ResponseBody List<LeadHistoryVM> getLeadHistory(@PathVariable("id") Long id) {
		return leadService.getLeadHistory(id);
	}
	
	@Transactional
	@RequestMapping(value="/getNewLeadData",method=RequestMethod.GET)
	public @ResponseBody Map getNewLeadData() {
		return leadService.getNewLeadData();
	}

	@Transactional
	@RequestMapping(value="/getDealersByZipCode/{zipCode}",method=RequestMethod.GET)
	public @ResponseBody List<DealerConfigurationVM> getDealersByZipCode(@PathVariable("zipCode") Long zipCode) {
		return dealerService.getDealersByZipCode(zipCode);
	}
	
	@Transactional
	@RequestMapping(value="/updateDealerConfig", method = RequestMethod.POST)
	public @ResponseBody void updateDealerConfig(ModelMap model,@RequestBody List<DealerConfigurationVM> configurationVMs,HttpServletRequest request){
		dealerService.updateDealerConfig(configurationVMs);		   
	}
	
	@Transactional
	@RequestMapping(value="/updateReportFrequency", method = RequestMethod.POST)
	@ResponseBody void updateReportFrequency(ModelMap model,@RequestBody List<RolesVM> role,HttpServletRequest request){
		dealerService.updateReportFrequency(role);		   
	}
	
	@Transactional
	@RequestMapping(value="/getDetailsForUser", method = RequestMethod.GET)
	public @ResponseBody Map getDetailsForUser(ModelMap model,HttpServletRequest request){
		return dealerService.getDetailsForUser();
	}
	
	@Transactional
	@RequestMapping(value="/getDetailsForRoles", method = RequestMethod.GET)
	public @ResponseBody List<RolesVM> getDetailsForRoles(){
		return dealerService.getDetailsForRoles();
	}
	
	@Transactional
	@RequestMapping(value="/getZones", method = RequestMethod.GET)
	public @ResponseBody Map getZones(ModelMap model,HttpServletRequest request){
		return dealerService.getZones();
	}	
	
	
	@Transactional
	@RequestMapping(value="/getPincodes", method = RequestMethod.GET)
	public @ResponseBody List<PinsVM> getPincodes(ModelMap model ,HttpServletRequest request){
		String query = request.getParameter("query");
		return dealerService.getPinCodes(query);
	}	
	
	@Transactional
	@RequestMapping(value="/saveUser", method = RequestMethod.POST)
	public @ResponseBody List<UserVM> saveUser(ModelMap model,@RequestBody SaveUserVM userVM,HttpServletRequest request){
		return dealerService.saveUser(userVM);
	}	
	
	@Transactional
	@RequestMapping(value="/saveDealer", method = RequestMethod.POST)
	public @ResponseBody void saveDealer(ModelMap model,@RequestBody DealerVM dealerVM,HttpServletRequest request){
		dealerService.saveDealer(dealerVM);		   
	}
	
	@Transactional
	@RequestMapping(value="/updateDealer", method = RequestMethod.POST)
	public @ResponseBody void updateDealer(ModelMap model,@RequestBody DealerVM dealerVM,HttpServletRequest request){
		dealerService.updateDealer(dealerVM);
	
	}	
	
	@Transactional
	@RequestMapping(value="/updateUser", method = RequestMethod.POST)
	public @ResponseBody List<UserVM> updateUser(ModelMap model,@RequestBody UserVM userVM,HttpServletRequest request){
		return dealerService.updateUser(userVM);
	}	
	
	
	@Transactional
	@RequestMapping(value="/changeUserStatus/{status}", method = RequestMethod.POST)
	public @ResponseBody void changeUserStatus(@PathVariable("status") Long status, ModelMap model,@RequestBody List<Long> ids,HttpServletRequest request){
		String sql = "UPDATE User u SET u.status = "+status+" WHERE u.id in (:ids)";
		dealerService.changeStatus(sql, ids);
	}	
	
	@Transactional
	@RequestMapping(value="/changeDealerStatus/{status}", method = RequestMethod.POST)
	public @ResponseBody void changeDealerStatus(@PathVariable("status") Long status, ModelMap model,@RequestBody List<Long> ids,HttpServletRequest request){
		String sql = "UPDATE dealer d SET d.status = "+status+" WHERE d.id in (:ids)";
		dealerService.changeStatus(sql, ids);
	}	
	
	@Transactional
	@RequestMapping(value="/getGeneralConfig",method=RequestMethod.GET)
	public @ResponseBody Map getGeneralConfig() {
		return jt.queryForList("select * from generalconfig").get(0);
	}
	
	@Transactional
	@RequestMapping(value="/reassignDealerToLead",method=RequestMethod.POST)
	public @ResponseBody void reassignDealerToLead(ModelMap model,@RequestBody ReassignVM reassignVM, HttpServletRequest request) {
		leadService.reassignDealers(reassignVM.getUserVM(), reassignVM.getIds());
	}
	
	@Transactional
	@RequestMapping(value="/getReassignList",method=RequestMethod.GET)
	public @ResponseBody List<ReassignUserVM> getReassignList() {
		return leadService.getReassignList();
	}
	
	
	@Transactional
	@RequestMapping(value="/updateGeneralConfig", method = RequestMethod.POST)
	public @ResponseBody void updateGeneralConfig(ModelMap model,@RequestBody GeneralConfigVM configurationVM,HttpServletRequest request){
		dealerService.updateGeneralConfig(configurationVM);		   
	}
	
	@Transactional
	@RequestMapping(value="/escalationScheduler", method = RequestMethod.GET)
	public @ResponseBody void escalationScheduler(){
		schedularService.escalationScheduler();
	}
	
	@Transactional
	@RequestMapping(value="/getZoneSplineBetweenDates", method = RequestMethod.GET)
	public @ResponseBody Map getZoneSplineBetweenDates(@RequestParam("start") @DateTimeFormat(pattern="MMddyyyy") Date start,
			@RequestParam("end") @DateTimeFormat(pattern="MMddyyyy") Date end,
			@RequestParam("zone") String zone, @RequestParam("state") String state, @RequestParam("product") Long product){
		return dashBoardService.getZoneSplineBetweenDates(start, end, zone, state);
	}
	
	@Transactional
	@RequestMapping(value="/getStateSplineBetweenDates", method = RequestMethod.GET)
	public @ResponseBody Map getStateSplineBetweenDates(@RequestParam("start") @DateTimeFormat(pattern="MMddyyyy") Date start,
			@RequestParam("end") @DateTimeFormat(pattern="MMddyyyy") Date end, @RequestParam("state") String state){
		return dashBoardService.getZoneSplineBetweenDates(start, end, "state", state);
	}
	
	@Transactional
	@RequestMapping(value="/getProductSplineBetweenDates", method = RequestMethod.GET)
	public @ResponseBody Map getProductSplineBetweenDates(@RequestParam("start") @DateTimeFormat(pattern="MMddyyyy") Date start,
			@RequestParam("end") @DateTimeFormat(pattern="MMddyyyy") Date end){
		return dashBoardService.getProductSplineBetweenDates(start, end);
	}
	
	@Transactional
	@RequestMapping(value="/getDealerSplineBetweenDates", method = RequestMethod.GET)
	public @ResponseBody Map getDealerSplineBetweenDates(@RequestParam("start") @DateTimeFormat(pattern="MMddyyyy") Date start,
			@RequestParam("end") @DateTimeFormat(pattern="MMddyyyy") Date end){
		return dashBoardService.getDealerSplineBetweenDates(start, end);
	}
	
	@Transactional
	@RequestMapping(value="/getDashboardProgressbarAll", method = RequestMethod.GET)
	public @ResponseBody List<Map> getDashboardProgressbarAll(@RequestParam("start") @DateTimeFormat(pattern="MMddyyyy") Date start,
			@RequestParam("end") @DateTimeFormat(pattern="MMddyyyy") Date end,
			@RequestParam("zone") String zone, @RequestParam("state") String state, @RequestParam("product") Long product){
		return dashBoardService.getDashboardProgressbar(start, end, zone, state, product);
	}
	
	@Transactional
	@RequestMapping(value="/getZoneStateProduct", method = RequestMethod.GET)
	public @ResponseBody Map getZoneStateProduct(){
		return dealerService.getZoneStateProduct();
	}
	
	@Transactional
	@RequestMapping(value="/getStateByZone/{zone}", method = RequestMethod.GET)
	public @ResponseBody List<ZoneVM> getStateByZone(@PathVariable("zone") String zone){
		return dealerService.getStateByZone(zone);
	}
	
	@Transactional
	@RequestMapping(value="/sendMail", method = RequestMethod.GET)
	public @ResponseBody void sendMail(){
		mailService.sendMail("shwashank12@gmail.com", "SUBJECT", "BODY");
	}
	
	@Transactional
	@RequestMapping(value="/getRSMByZone/{zone}",method = RequestMethod.GET)
	public @ResponseBody List<ZoneVM> getRSMByZone(@PathVariable("zone") Long zone) {
		return dealerService.getRSMByZone(zone);
	}
	
	@Transactional
	@RequestMapping(value="/getTSRByZone/{zone}",method = RequestMethod.GET)
	public @ResponseBody List<ZoneVM> getTSRByZone(@PathVariable("zone") Long zone) {
		return dealerService.getTSRByZone(zone);
	}
	
	@Transactional
	@RequestMapping(value="/getDealersByDistrict/{district}",method = RequestMethod.GET)
	public @ResponseBody List<ZoneVM> getDealersByDistrict(@PathVariable("district") Long district) {
		return dealerService.getDealersByDistrict(district);
	}

}
