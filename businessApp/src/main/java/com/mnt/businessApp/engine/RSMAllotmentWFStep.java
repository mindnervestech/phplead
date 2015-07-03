package com.mnt.businessApp.engine;

public class RSMAllotmentWFStep extends AbstractAllotmentEngine {

	public RSMAllotmentWFStep(String zip, String product, Long lead_id) {
		super(zip, product, lead_id, "RSM");
		System.out.println("RSM");
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
