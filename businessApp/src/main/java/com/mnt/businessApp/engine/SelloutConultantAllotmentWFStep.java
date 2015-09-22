package com.mnt.businessApp.engine;

import java.util.Date;

public class SelloutConultantAllotmentWFStep extends AbstractAllotmentEngine {

	public SelloutConultantAllotmentWFStep(String zip, String product, Long lead_id) {
		super(zip, product, lead_id, "Sales Consultant");
	}

	@Override
	protected void assignLeadIfNoProductAndZipServicable() {
		SelloutExecutiveAllotmentWFStep allotmentWFStep = new SelloutExecutiveAllotmentWFStep(zip, product, lead_id);
		allotmentWFStep.jt = jt;
		allotmentWFStep.status = status;
		allotmentWFStep.mailService = mailService;
		allotmentWFStep.startAssignment();
	}

	@Override
	protected void assignLeadIfMultipleUser() {
		assignLeadIfSingleUser();
	}

	@Override
	protected void assignLeadIfSingleUser() {
		jt.update("UPDATE lead SET lead.user_id = "+userPresent.get(0)+", lead.assignLeadDate = ? where lead.id = "+lead_id+" and lead.user_id is null ", new Date());
		//sendMail(userPresent.get(0));
	}
	
	@Override
	protected void assignLeadIfNoProductServicable() {
		SelloutExecutiveAllotmentWFStep allotmentWFStep = new SelloutExecutiveAllotmentWFStep(zip, product, lead_id);
		allotmentWFStep.jt = jt;
		allotmentWFStep.status = status;
		allotmentWFStep.mailService = mailService;
		allotmentWFStep.startAssignment();
	}

	@Override
	protected void assignLeadIfNoZipServicable() {
		SelloutExecutiveAllotmentWFStep allotmentWFStep = new SelloutExecutiveAllotmentWFStep(zip, product, lead_id);
		allotmentWFStep.jt = jt;
		allotmentWFStep.status = status;
		allotmentWFStep.mailService = mailService;
		allotmentWFStep.startAssignment();
	}

}
