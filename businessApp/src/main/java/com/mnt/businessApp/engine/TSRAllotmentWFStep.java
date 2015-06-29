package com.mnt.businessApp.engine;

public class TSRAllotmentWFStep extends AbstractAllotmentEngine {

	public TSRAllotmentWFStep(String zip, String product) {
		super(zip, product, "tsr");
	}

	@Override
	protected void assignLeadIfNoProductAndZipServicable() {
		new RSMAllotmentWFStep(zip, product).startAssignment();
	}

	@Override
	protected void assignLeadIfMultipleUser() {

	}

	@Override
	protected void assignLeadIfSingleUser() {

	}
	
	@Override
	protected void assignLeadIfNoProductServicable() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void assignLeadIfNoZipServicable() {
		// TODO Auto-generated method stub
		
	}

}
