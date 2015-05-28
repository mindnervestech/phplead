package com.mnt.businessApp.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mnt.businessApp.service.DealerService;
import com.mnt.businessApp.service.ManageLeadService;
import com.mnt.businessApp.service.SchedularService;
import com.mnt.businessApp.viewmodel.DealerConfigurationVM;
import com.mnt.businessApp.viewmodel.DealerVM;
import com.mnt.businessApp.viewmodel.GeneralConfigVM;
import com.mnt.businessApp.viewmodel.LeadDetailsVM;
import com.mnt.businessApp.viewmodel.LeadHistoryVM;
import com.mnt.businessApp.viewmodel.LeadVM;
import com.mnt.businessApp.viewmodel.PinsVM;
import com.mnt.businessApp.viewmodel.SaveUserVM;
import com.mnt.businessApp.viewmodel.UserVM;

@Controller
@RequestMapping(value="/api/business")
public class BusinessController {

	@Autowired
	private SchedularService schedularService;
	
	@Autowired
	private ManageLeadService manageLeadService;
	
	@Autowired
	private DealerService dealerService;

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
    private JdbcTemplate jt;
	
	@Transactional
	@RequestMapping(value="/testURL",method=RequestMethod.GET)
	public @ResponseBody String home() {
		schedularService.uploadandStoreExcel();
		return "HERE";
	}
	
	@Transactional
	@RequestMapping(value="/getLeads",method=RequestMethod.GET)
	public @ResponseBody List<LeadDetailsVM> getLeads() {
		return manageLeadService.getAllLeadDetails();
	}
	
	@Transactional
	@RequestMapping(value="/getEscalatedLeads",method=RequestMethod.GET)
	public @ResponseBody List<LeadDetailsVM> getEscalatedLeads() {
		return manageLeadService.getAllEscalatedLeadDetails();
	}
	
	@Transactional
	@RequestMapping(value="/getFollowUpLeads",method=RequestMethod.GET)
	public @ResponseBody List<LeadDetailsVM> getFollowUpLeads() {
		return manageLeadService.getFollowUpLeads();
	}
	
	
	@Transactional
	@RequestMapping(value="/lead/{id}",method=RequestMethod.GET)
	public @ResponseBody LeadVM getLeadInfoById(@PathVariable("id") Long id) {
		return manageLeadService.getLeadVMById(id);
	}
	
	@Transactional
	@RequestMapping(value="/updateLead",method=RequestMethod.POST)
	public @ResponseBody void updateLead(@RequestBody LeadVM vm) {
		manageLeadService.updateLead(vm);
	}

	@Transactional
	@RequestMapping(value="/lead/history/{id}",method=RequestMethod.GET)
	public @ResponseBody List<LeadHistoryVM> getLeadHistory(@PathVariable("id") Long id) {
		return manageLeadService.getLeadHistory(id);
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
	@RequestMapping(value="/getDetailsForUser", method = RequestMethod.GET)
	public @ResponseBody Map getDetailsForUser(ModelMap model,HttpServletRequest request){
		return dealerService.getDetailsForUser();
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
	@RequestMapping(value="/getGeneralConfig",method=RequestMethod.GET)
	public @ResponseBody Map getGeneralConfig() {
		return jt.queryForList("select * from generalconfig").get(0);
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
	

}
