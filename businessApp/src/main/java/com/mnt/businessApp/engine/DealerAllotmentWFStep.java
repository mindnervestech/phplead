package com.mnt.businessApp.engine;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.mnt.businessApp.service.MailService;
import com.mnt.businessApp.viewmodel.ReassignUserVM;



public class DealerAllotmentWFStep extends AbstractAllotmentEngine {
	
	
	public DealerAllotmentWFStep(String zip, String product, long lead_id) {
		super(zip, product, lead_id, "Dealer");
	}

	@Override
	protected void assignLeadIfNoProductAndZipServicable() {
		SelloutConultantAllotmentWFStep allotmentWFStep = new SelloutConultantAllotmentWFStep(zip, product, lead_id);
		allotmentWFStep.jt = jt;
		allotmentWFStep.status = status;
		allotmentWFStep.startAssignment();
	}

	@Override
	protected void assignLeadIfMultipleUser() {
		Math.random();
		Long dealer =  userPresent.get((int)(Math.random()*userPresent.size()));
		System.out.println("Lead.User_id = " + dealer );
		jt.update("UPDATE lead SET lead.user_id = "+dealer+" where lead.id = "+lead_id);
		System.out.println("In Multiple User :: ");
		System.out.println("Lead.User_id = " + dealer );
		/*String usersql = "";
		usersql = "SELECT * FROM user as u where u.id ="+dealer;
		List<Map<String, Object>> rows = jt.queryForList(usersql);
		String email;
		for(Map map : rows) {
			email = (String) map.get("email");
			System.out.println("Email : " + email);
			mailService.sendMail(email, "SUBJECT", "Body");
			
		}*/
		
	/*	for(Long dealer : userPresent){
			Long count = jt.queryForLong("SELECT COUNT(*) FROM lead as l, leaddetails, user where l.leadDetails_id = leaddetails.id"
					+ " and l.status = 'Open' "
					+ " and leaddetails.pinCode = '"+zip+"' and leaddetails.product_id = "+product+" and l.user_id = "+dealer+" and user.id = l.user_id and user.entityName = 'Dealer'");
			if(count == 0){
				jt.update("UPDATE lead SET lead.user_id = "+dealer+" where lead.id = "+lead_id);
				break;
			}
			Long persecntage = jt.queryForLong("SELECT uz.percentage from user_zipcode uz where uz.user_id = "+dealer+" and uz.zipCodes_id = "+zip);
			System.out.println("count :: "+count+" :: total :: "+total);
			System.out.println("persentage :: "+persecntage);
			System.out.println("count :: "+((count*100)/total));
			if(count != 0 && persecntage < ((count*100)/total)){
				jt.update("UPDATE lead SET lead.user_id = "+dealer+", percentage = "+((count*100)/total)+" where lead.id = "+lead_id);
				break;
			}
		}*/
	}

	@Override
	protected void assignLeadIfSingleUser() {
		jt.update("UPDATE lead SET lead.user_id = "+userPresent.get(0)+" where lead.id = "+lead_id);
		System.out.println("In Single User :: ");
		System.out.println("Lead.User_id = " + userPresent.get(0) );
		String usersql = "";
		usersql = "SELECT email FROM user as u where u.id ="+userPresent.get(0);
		List<Map<String, Object>> rows = jt.queryForList(usersql);
		String email = "";
		System.out.println("Email row : " + rows.get(0).toString());
		for(Map map : rows) {
			email = (String) map.get("email");
			System.out.println("Email : " + email);
		}
		System.out.println("Email 1 :: " + email);
		System.out.println("Mail Service :: " + mailService);
		mailService.sendMail("ahadansari09@gmail", "SUBJECT", "BODY");
		System.out.println("After sending......");
		
	}

	@Override
	protected void assignLeadIfNoProductServicable() {
		SelloutConultantAllotmentWFStep allotmentWFStep = new SelloutConultantAllotmentWFStep(zip, product, lead_id);
		allotmentWFStep.jt = jt;
		allotmentWFStep.status = status;
		allotmentWFStep.startAssignment();
		
	}

	@Override
	protected void assignLeadIfNoZipServicable() {
		SelloutConultantAllotmentWFStep allotmentWFStep = new SelloutConultantAllotmentWFStep(zip, product, lead_id);
		allotmentWFStep.jt = jt;
		allotmentWFStep.status = status;
		allotmentWFStep.startAssignment();
		
	}

}
