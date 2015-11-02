package com.mnt.businessApp.engine;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.ListUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.mnt.businessApp.service.MailService;
import com.mnt.entities.businessApp.Lead;
import com.mnt.entities.businessApp.LeadAgeing;

public abstract class AbstractAllotmentEngine {
	

	String zip; String product; String userType;
	public Long lead_id;
	public JdbcTemplate jt;
	public String status;
	public Session session;
	public MailService mailService;
	
	public Date configDate;
	protected List<Long> userPresent;
	protected List<Long> userZipPresent;
	protected List<Long> userProductPresent;
	public String brand;

	// Maybe we need to have lead object in constructor
	public AbstractAllotmentEngine(String zip, String product, Long lead_id, String userType) {
		super();
		this.lead_id = lead_id;
		this.zip = zip;
		this.product = product;
		this.userType = userType;
	}

	private List<Long> getQualifiedCadidates() {
		Map<String, List<Long>> zipPresent = AllotmentEngineCache.getInstance().zipCache.get(zip);
		Map<String, List<Long>> productPresent = AllotmentEngineCache.getInstance().productCache.get(product);
		if(userType.equalsIgnoreCase("Dealer")){
			Map<String, List<Long>> brandPresent = AllotmentEngineCache.getInstance().brandCache.get(brand);
			if(zipPresent != null && productPresent != null && brandPresent != null) {
				List<Long> userZipPresent = zipPresent.get(userType);
				List<Long> userProductPresent = productPresent.get(userType);
				List<Long> userBrandPresent = brandPresent.get(userType);
				if(userZipPresent != null && userProductPresent != null && userBrandPresent != null) {
					List<Long> userZipProductPresent = ListUtils.intersection(userZipPresent, userProductPresent);
					System.out.println("userZipProductPresent ::: "+userZipProductPresent);
					List<Long> userZipProductBrandPresent = ListUtils.intersection(userZipProductPresent, userBrandPresent);
					System.out.println("userZipProductBrandPresent :: " + userZipProductBrandPresent);
					if(userZipProductBrandPresent.size() > 0)
						return userZipProductBrandPresent;
				}
			}
		} else {
			if(zipPresent != null && productPresent != null) {
				List<Long> userZipPresent = zipPresent.get(userType);
				List<Long> userProductPresent = productPresent.get(userType);
				if(userZipPresent != null && userProductPresent != null) {
					List<Long> userZipProductPresent = ListUtils.intersection(userZipPresent, userProductPresent);
					if(userZipProductPresent.size() > 0)
						return userZipProductPresent;
				}
			}
		}
		return null;	
	}

	public void startAssignment() {
		System.out.println("USERTYPE :: "+userType);
		Map<String, List<Long>> zipPresent = AllotmentEngineCache.getInstance().zipCache.get(zip);
		Map<String, List<Long>> productPresent = AllotmentEngineCache.getInstance().productCache.get(product);
		if(zipPresent != null && productPresent != null) {
			userPresent = getQualifiedCadidates();
			if(userPresent != null) { // serviceable user found for both Z and P
				if(this.status != "assignment"){
					updateLeadAgeing();
				}
				if(userPresent.size() == 1) {
					System.out.println("User Present :: " + userPresent.get(0).toString());
					assignLeadIfSingleUser();
				} else {
					assignLeadIfMultipleUser();
					//TODO: Assign based on configuration
				}
			} else {
				userZipPresent = zipPresent.get(userType);
				userProductPresent = productPresent.get(userType);
				if(userZipPresent == null && userProductPresent != null) { // serviceable user found only for P
					assignLeadIfNoZipServicable();
					//TODO: Find Near dealer and assign
				}

				if(userZipPresent != null && userProductPresent == null) { // serviceable dealers found only for Z
					assignLeadIfNoProductServicable();
					//TODO: Find Near dealer and assign
				}

				if(userZipPresent == null && userProductPresent == null) { // serviceable dealers not found at all
					assignLeadIfNoProductAndZipServicable();
					//TODO: Find Near dealer and assign
				}
				
				if(userZipPresent != null && userProductPresent != null) { // serviceable dealers not found at all
					assignLeadIfNoProductAndZipServicable();
					//TODO: Find Near dealer and assign
				}
				
				

			}
		} else {
			assignLeadToZoneUser();
		}
	}

	private void assignLeadToZoneUser() {
		System.out.println("lead id"+lead_id);
		jt.update("Update lead set lead.user_id = (select user.id from user where user.entityName = 'Sellout Manager' and lead.zone = user.zone  order by user.id limit 1) where lead.id = "+lead_id);
		
	}

	protected void updateLeadAgeing(){
			Lead lead = (Lead) session.get(Lead.class, lead_id);
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
			if(dateFormat.format(lead.getLastDispo1ModifiedDate()).compareTo(dateFormat.format(configDate)) > 0) {
				return;
			}
			LeadAgeing ageing = getLeadAgeing(lead.getId(), "Escalated");
			long secs = (new Date().getTime() - lead.getLastDispo1ModifiedDate().getTime()) / 1000;
			Integer hours = (int) (secs / 3600);    
			ageing.setAgeing(ageing.getAgeing() + hours);
			ageing.setProduct(lead.getLeadDetails().getProduct().getName());
			session.update(ageing);
	}

	private LeadAgeing getLeadAgeing(Long id, String disposition1) {
		Query query = session.createQuery("from LeadAgeing as la where la.lead_id = "+id+" and la.status  = '"+disposition1+"'");
		List<LeadAgeing> ageings = query.list();
		if(ageings.size() != 0){
			return ageings.get(0);
		}
		LeadAgeing ageing = new LeadAgeing();
		ageing.setLead_id(id);
		ageing.setStatus(disposition1);
		ageing.setAgeing(0L);
		ageing.setIsCurrent(true);
		session.save(ageing);
		return ageing;
	}
	
	protected abstract void assignLeadIfNoProductAndZipServicable();

	protected abstract void assignLeadIfNoProductServicable();

	protected abstract void assignLeadIfNoZipServicable() ;

	protected abstract void assignLeadIfMultipleUser();

	protected abstract void assignLeadIfSingleUser();

	public void sendMail(Long id){
		System.out.println("In Single User :: ");
		String usersql = "";
		usersql = "SELECT email FROM user as u where u.id ="+id;
		List<Map<String, Object>> rows = jt.queryForList(usersql);
		String email = "";
		System.out.println("Email row : " + rows.get(0).toString());
		for(Map map : rows) {
			email = (String) map.get("email");
			System.out.println("Email : " + email);
		}
		System.out.println("Email 1 :: " + email);
		System.out.println("Mail Service :: " + mailService);
		mailService.sendMail(email, "SUBJECT", "BODY");
		System.out.println("After sending......");
	}

}
