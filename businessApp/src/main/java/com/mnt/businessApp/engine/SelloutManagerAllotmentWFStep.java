package com.mnt.businessApp.engine;

public class SelloutManagerAllotmentWFStep extends AbstractAllotmentEngine {

	public SelloutManagerAllotmentWFStep(String zip, String product, Long lead_id) {
		super(zip, product, lead_id, "Sales Consultant");
		System.out.println("Sales Consultant");
	}

	@Override
	protected void assignLeadIfNoProductAndZipServicable() {
		TSRAllotmentWFStep allotmentWFStep = new TSRAllotmentWFStep(zip, product, lead_id);
		allotmentWFStep.jt = jt;
		allotmentWFStep.startAssignment();
	}

	@Override
	protected void assignLeadIfMultipleUser() {
		assignLeadIfSingleUser();
	}

	@Override
	protected void assignLeadIfSingleUser() {
		jt.update("UPDATE lead SET lead.user_id = "+userPresent.get(0)+", lead.dealer_id = NULL where lead.id = "+lead_id);
	}
	
	@Override
	protected void assignLeadIfNoProductServicable() {
		TSRAllotmentWFStep allotmentWFStep = new TSRAllotmentWFStep(zip, product, lead_id);
		allotmentWFStep.jt = jt;
		allotmentWFStep.startAssignment();
	}

	@Override
	protected void assignLeadIfNoZipServicable() {
		TSRAllotmentWFStep allotmentWFStep = new TSRAllotmentWFStep(zip, product, lead_id);
		allotmentWFStep.jt = jt;
		allotmentWFStep.startAssignment();
	}

}
