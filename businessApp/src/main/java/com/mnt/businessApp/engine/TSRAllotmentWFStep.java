package com.mnt.businessApp.engine;

public class TSRAllotmentWFStep extends AbstractAllotmentEngine {

	public TSRAllotmentWFStep(String zip, String product, Long lead_id) {
		super(zip, product,lead_id, "TSR");
		System.out.println("TSR");
	}

	@Override
	protected void assignLeadIfNoProductAndZipServicable() {
		RSMAllotmentWFStep allotmentWFStep = new RSMAllotmentWFStep(zip, product, lead_id);
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
		RSMAllotmentWFStep allotmentWFStep = new RSMAllotmentWFStep(zip, product, lead_id);
		allotmentWFStep.jt = jt;
		allotmentWFStep.startAssignment();
	}

	@Override
	protected void assignLeadIfNoZipServicable() {
		RSMAllotmentWFStep allotmentWFStep = new RSMAllotmentWFStep(zip, product, lead_id);
		allotmentWFStep.jt = jt;
		allotmentWFStep.startAssignment();
	}

}
