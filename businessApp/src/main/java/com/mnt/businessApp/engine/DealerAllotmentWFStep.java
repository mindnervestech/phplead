package com.mnt.businessApp.engine;



public class DealerAllotmentWFStep extends AbstractAllotmentEngine {
	
	public DealerAllotmentWFStep(String zip, String product, long lead_id) {
		super(zip, product, lead_id, "Dealer");
		System.out.println("Dealer");
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
		Long total = jt.queryForLong("SELECT COUNT(*) FROM lead as l, leaddetails where lead.leadDetails_id = leaddetails.id "
				+ " and l.status = 'Open' "
				+ " and leaddetails.pinCode = '"+zip+"' and leaddetails.product_id = "+product);
		for(Long dealer : userPresent){
			Long count = jt.queryForLong("SELECT COUNT(*) FROM lead as l, leaddetails, user where lead.leadDetails_id = leaddetails.id"
					+ " and l.status = 'Open' "
					+ " and leaddetails.pinCode = '"+zip+"' and leaddetails.product_id = "+product+" and user_id = "+dealer+" and user.id = user_id and user.entityName = 'Dealer'");
			Long persecntage = jt.queryForLong("SELECT uz.percentage from user_zipcode uz where uz.user_id = "+dealer+" and uz.zipCodes_id = "+zip);
			if(persecntage > ((count*100)/total)){
				jt.update("UPDATE lead SET lead.user_id = "+dealer+" where lead.id = "+lead_id);
				break;
			}
		}
	}

	@Override
	protected void assignLeadIfSingleUser() {
		jt.update("UPDATE lead SET lead.user_id = "+userPresent.get(0)+" where lead.id = "+lead_id);
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
		SelloutManagerAllotmentWFStep allotmentWFStep = new SelloutManagerAllotmentWFStep(zip, product, lead_id);
		allotmentWFStep.jt = jt;
		allotmentWFStep.status = status;
		allotmentWFStep.startAssignment();
	}

}
