package com.mnt.businessApp.engine;

public class DealerAllotmentWFStep extends AbstractAllotmentEngine {

	public DealerAllotmentWFStep(String zip, String product) {
		super(zip, product, "dealer");
	}

	@Override
	protected void assignLeadIfNoProductAndZipServicable() {
		new TSRAllotmentWFStep(zip, product).startAssignment();
	}

	@Override
	protected void assignLeadIfMultipleUser() {

	}

	@Override
	protected void assignLeadIfSingleUser() {

	}

	@Override
	protected void assignLeadIfNoProductServicable() {
		
	}

	@Override
	protected void assignLeadIfNoZipServicable() {
		
	}

}
