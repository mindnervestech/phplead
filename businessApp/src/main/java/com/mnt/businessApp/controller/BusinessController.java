package com.mnt.businessApp.controller;

import java.util.Date;
import java.util.HashMap;
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

import com.mnt.businessApp.engine.AllotmentEngineCache;
import com.mnt.businessApp.service.AssignLeadsService;
import com.mnt.businessApp.service.DashBoardService;
import com.mnt.businessApp.service.DealerService;
import com.mnt.businessApp.service.LeadService;
import com.mnt.businessApp.service.MailService;
import com.mnt.businessApp.service.ReadExcelService;
import com.mnt.businessApp.service.SchedularService;
import com.mnt.businessApp.viewmodel.DealerConfigurationVM;
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
	private AssignLeadsService assignLeadsService;

	@Autowired
	private MailService mailService;
	
	@Autowired
	private ReadExcelService readExcelService;
	
	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private VelocityEngine velocityEngine;
	
	@Autowired
    private JdbcTemplate jt;
	
	//@Transactional
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
	public @ResponseBody List<LeadDetailsVM> getEscalatedLeads(@RequestParam(value="start", required=false)  @DateTimeFormat(pattern="MMddyyyy") Date start,
			@RequestParam(value="end", required=false) @DateTimeFormat(pattern="MMddyyyy") Date end,  @RequestParam(value="dealer", required=false) Long dealer,
			@RequestParam(value="zone", required=false) String zone, @RequestParam(value="state", required=false) String state, @RequestParam(value="product", required=false) Long product, @RequestParam("brand") String brand ){
		return leadService.getAllEscalatedLeadDetails(start, end, zone, state, product, dealer, brand);
	}
	
	@Transactional
	@RequestMapping(value="/getOpenLeads",method=RequestMethod.GET)
	public @ResponseBody List<LeadDetailsVM> getOpenLeads(@RequestParam("start") @DateTimeFormat(pattern="MMddyyyy") Date start,
			@RequestParam("end") @DateTimeFormat(pattern="MMddyyyy") Date end,  @RequestParam("dealer") Long dealer,
			@RequestParam("zone") String zone, @RequestParam("state") String state, @RequestParam("product") Long product, @RequestParam("brand") String brand ){
		return leadService.getOpenLeads(start, end, zone, state, product, dealer, brand);
	}
	
	@Transactional
	@RequestMapping(value="/getWonLeads",method=RequestMethod.GET)
	public @ResponseBody List<LeadDetailsVM> getWonLeads(@RequestParam("start") @DateTimeFormat(pattern="MMddyyyy") Date start,
			@RequestParam("end") @DateTimeFormat(pattern="MMddyyyy") Date end,  @RequestParam("dealer") Long dealer,
			@RequestParam("zone") String zone, @RequestParam("state") String state, @RequestParam("product") Long product, @RequestParam("brand") String brand ){
		return leadService.getWonLeads(start, end, zone, state, product, dealer, brand);
	}
	
	@Transactional
	@RequestMapping(value="/getLostLeads",method=RequestMethod.GET)
	public @ResponseBody List<LeadDetailsVM> getLostLeads(@RequestParam("start") @DateTimeFormat(pattern="MMddyyyy") Date start,
			@RequestParam("end") @DateTimeFormat(pattern="MMddyyyy") Date end,  @RequestParam("dealer") Long dealer,
			@RequestParam("zone") String zone, @RequestParam("state") String state, @RequestParam("product") Long product, @RequestParam("brand") String brand ){
		return leadService.getLostLeads(start, end, zone, state, product, dealer, brand);
	}
	
	
	@Transactional
	@RequestMapping(value="/getOverviewLeads",method=RequestMethod.GET)
	public @ResponseBody List<LeadDetailsVM> getOverviewLeads(@RequestParam("start") @DateTimeFormat(pattern="MMddyyyy") Date start,
			@RequestParam("end") @DateTimeFormat(pattern="MMddyyyy") Date end,  @RequestParam("dealer") Long dealer,
			@RequestParam("zone") String zone, @RequestParam("state") String state, @RequestParam("product") Long product, @RequestParam("brand") String brand ){
		return leadService.getOverviewLeads(start, end, zone, state, product, dealer, brand);
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
	public @ResponseBody List<ZoneVM> getPincodes(ModelMap model ,HttpServletRequest request){
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
	public @ResponseBody void saveDealer(ModelMap model,@RequestBody UserVM userVM,HttpServletRequest request){
		dealerService.saveDealer(userVM);		   
	}
	
	@Transactional
	@RequestMapping(value="/updateDealer", method = RequestMethod.POST)
	public @ResponseBody void updateDealer(ModelMap model,@RequestBody UserVM userVM,HttpServletRequest request){
		dealerService.updateDealer(userVM);
	
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
			@RequestParam("zone") String zone, @RequestParam("state") String state, @RequestParam("product") Long product, @RequestParam("brand") String brand ){
		return dashBoardService.getZoneSplineBetweenDates(start, end, zone, state, product, brand);
	}
	
	@Transactional
	@RequestMapping(value="/getProductSplineBetweenDates", method = RequestMethod.GET)
	public @ResponseBody Map getProductSplineBetweenDates(@RequestParam("start") @DateTimeFormat(pattern="MMddyyyy") Date start,
			@RequestParam("end") @DateTimeFormat(pattern="MMddyyyy") Date end,
			@RequestParam("zone") String zone, @RequestParam("state") String state, @RequestParam("product") Long product, @RequestParam("brand") String brand ){
		return dashBoardService.getProductSplineBetweenDates(start, end, zone, state, product, brand);
	}
	
	@Transactional
	@RequestMapping(value="/getDealerSplineBetweenDates", method = RequestMethod.GET)
	public @ResponseBody Map getDealerSplineBetweenDates(@RequestParam("start") @DateTimeFormat(pattern="MMddyyyy") Date start,
			@RequestParam("end") @DateTimeFormat(pattern="MMddyyyy") Date end, 
			@RequestParam("zone") String zone, @RequestParam("state") String state,
			@RequestParam("product") Long product,  @RequestParam("dealer") Long dealer, @RequestParam("brand") String brand ){
		return dashBoardService.getDealerSplineBetweenDates(start, end, zone, state, product, dealer, brand);
	}
	
	@Transactional
	@RequestMapping(value="/getDashboardProgressbarAll", method = RequestMethod.GET)
	public @ResponseBody List<Map> getDashboardProgressbarAll(@RequestParam("start") @DateTimeFormat(pattern="MMddyyyy") Date start,
			@RequestParam("end") @DateTimeFormat(pattern="MMddyyyy") Date end,
			@RequestParam("zone") String zone, @RequestParam("state") String state, @RequestParam("product") Long product,  @RequestParam("dealer") Long dealer, @RequestParam("brand") String brand ){
		return dashBoardService.getDashboardProgressbar(start, end, zone, state, product, dealer, brand);
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
	@RequestMapping(value="/getRSMByZone/{state}",method = RequestMethod.GET)
	public @ResponseBody List<ZoneVM> getRSMByZone(@PathVariable("state") String state,  @RequestParam("query") String query) {
		return dealerService.getRSMByZone(state, query);
	}
	
	@Transactional
	@RequestMapping(value="/getTSRByZone/{state}",method = RequestMethod.GET)
	public @ResponseBody List<ZoneVM> getTSRByZone(@PathVariable("state") String state,  @RequestParam("query") String query) {
		return dealerService.getTSRByZone(state, query);
	}
	
	@Transactional
	@RequestMapping(value="/getDealersByDistrict/{district}",method = RequestMethod.GET)
	public @ResponseBody List<ZoneVM> getDealersByDistrict(@PathVariable("district") Long district) {
		return dealerService.getDealersByDistrict(district);
	}
	

	@Transactional
	@RequestMapping(value="/test",method = RequestMethod.GET)
	public @ResponseBody Map test() {
		Map<String, Map<String, Map<String, List<Long>>>> map = new HashMap<String, Map<String,Map<String,List<Long>>>>();
		try{
			AllotmentEngineCache allotmentEngineCache = AllotmentEngineCache.getInstance();
			map.put("brand", allotmentEngineCache.brandCache);
			map.put("product", allotmentEngineCache.productCache);
			map.put("zipCode", allotmentEngineCache.zipCache);
			
		} catch (Exception e){
			AllotmentEngineCache.invalidate();
			AllotmentEngineCache.build(assignLeadsService.getZipCodeUserMapping(), assignLeadsService.getProductUserMapping(),assignLeadsService.getBrandUserMapping());
		}
		return map;
	}
	

	@Transactional
	@RequestMapping(value="/assignDealer",method = RequestMethod.GET)
	public @ResponseBody void assignDealer() {
		assignLeadsService.assignDealer();
	}
	
	@Transactional
	@RequestMapping(value="/getBrands",method = RequestMethod.GET)
	public @ResponseBody List<ZoneVM> getBrands() {
		return leadService.getBrands();
	}
	
	@Transactional
	@RequestMapping(value="/getModalNumbers",method = RequestMethod.GET)
	public @ResponseBody List<ZoneVM> getModalNumbers(@RequestParam("brand") String brand) {
		return leadService.getModalNumbers(brand);
	}
	
	@Transactional
	@RequestMapping(value="/readExcel",method=RequestMethod.GET)
	public @ResponseBody String readExcel() {
		readExcelService.readExcel();
		return "HERE";
	}

}
