package com.mnt.businessApp.engine;



public class DealerAllotmentWFStep extends AbstractAllotmentEngine {
	
	public DealerAllotmentWFStep(String zip, String product, long lead_id) {
		super(zip, product, lead_id, "Dealer");
		System.out.println("Dealer");
	}

	@Override
	protected void assignLeadIfNoProductAndZipServicable() {
		SelloutManagerAllotmentWFStep allotmentWFStep = new SelloutManagerAllotmentWFStep(zip, product, lead_id);
		allotmentWFStep.jt = jt;
		allotmentWFStep.startAssignment();
	}

	@Override
	protected void assignLeadIfMultipleUser() {
		Long total = jt.queryForLong("SELECT COUNT(*) FROM lead as l, leaddetails where lead.leadDetails_id = leaddetails.id "
				+ " and (l.disposition1 = 'New' or l.disposition2 IN('Call Back','Quote Sent','Visiting Store','Not Contacted')) "
				+ " and leaddetails.pinCode = '"+zip+"' and leaddetails.product_id = "+product);
		for(Long dealer : userPresent){
			Long count = jt.queryForLong("SELECT COUNT(*) FROM lead as l, leaddetails where lead.leadDetails_id = leaddetails.id"
					+ " and (l.disposition1 = 'New' or l.disposition2 IN('Call Back','Quote Sent','Visiting Store','Not Contacted')) "
					+ " and leaddetails.pinCode = '"+zip+"' and leaddetails.product_id = "+product+" and dealer_id = "+dealer);
			Long persecntage = jt.queryForLong("SELECT dc.percentage from dealerconfiguration dc where dc.dealer_id = "+dealer+" and dc.zipCode_id = "+zip);
			if(persecntage > ((count*100)/total)){
				jt.update("UPDATE lead SET lead.dealer_id = "+dealer+", lead.user_id = NULL where lead.id = "+lead_id);
				break;
			}
		}
	}

	@Override
	protected void assignLeadIfSingleUser() {
		jt.update("UPDATE lead SET lead.dealer_id = "+userPresent.get(0)+", lead.user_id = NULL where lead.id = "+lead_id);
	}

	@Override
	protected void assignLeadIfNoProductServicable() {
		SelloutManagerAllotmentWFStep allotmentWFStep = new SelloutManagerAllotmentWFStep(zip, product, lead_id);
		allotmentWFStep.jt = jt;
		allotmentWFStep.startAssignment();
		
	}

	@Override
	protected void assignLeadIfNoZipServicable() {
		SelloutManagerAllotmentWFStep allotmentWFStep = new SelloutManagerAllotmentWFStep(zip, product, lead_id);
		allotmentWFStep.jt = jt;
		allotmentWFStep.startAssignment();
	}

}
