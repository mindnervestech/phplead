package com.mnt.businessApp.engine;

public class RSMAllotmentWFStep extends AbstractAllotmentEngine {

	public RSMAllotmentWFStep(String zip, String product) {
		super(zip, product, "rsm");
	}

	@Override
	protected void assignLeadIfNoProductAndZipServicable() {
		new SelloutManagerAllotmentWFStep(zip, product).startAssignment();
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
