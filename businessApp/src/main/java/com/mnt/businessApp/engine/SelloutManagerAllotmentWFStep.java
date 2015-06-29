package com.mnt.businessApp.engine;

public class SelloutManagerAllotmentWFStep extends AbstractAllotmentEngine {

	public SelloutManagerAllotmentWFStep(String zip, String product) {
		super(zip, product, "srm");
	}

	@Override
	protected void assignLeadIfNoProductAndZipServicable() {

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
