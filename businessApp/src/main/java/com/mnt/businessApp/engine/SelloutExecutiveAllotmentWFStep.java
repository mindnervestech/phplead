package com.mnt.businessApp.engine;


public class SelloutExecutiveAllotmentWFStep extends AbstractAllotmentEngine {

	public SelloutExecutiveAllotmentWFStep(String zip, String product, Long lead_id) {
		super(zip, product, lead_id, "Sales Executive");
	}

	@Override
	protected void assignLeadIfNoProductAndZipServicable() {
		TSRAllotmentWFStep allotmentWFStep = new TSRAllotmentWFStep(zip, product, lead_id);
		allotmentWFStep.session = session;
		allotmentWFStep.configDate = configDate;
		allotmentWFStep.jt = jt;
		allotmentWFStep.status = status;
		allotmentWFStep.startAssignment();
	}

	@Override
	protected void assignLeadIfMultipleUser() {
		assignLeadIfSingleUser();
	}

	@Override
	protected void assignLeadIfSingleUser() {
		if(status.equals("assignment")){
			jt.update("UPDATE lead SET lead.user_id = "+userPresent.get(0)+" where lead.id = "+lead_id);
		} else  if(status.equals("escalation")){
			String dateInterval = " DATE_SUB(CURDATE(), INTERVAL (Select generalconfig.firstEscalationTime from generalconfig where id = 1) DAY )";
			jt.update("UPDATE lead SET lead.status = 'Escalated', lead.disposition1 = 'Escalated',lead.escalatedLevel = lead.escalatedLevel + 1, lead.escalatedDate = NOW(), lead.lastDispo1ModifiedDate = NOW(), "
					+ " lead.escalatedTo_id = "+userPresent.get(0)+" WHERE "
					+ " lead.disposition1 = 'New' and lead.id = "+lead_id+" and lead.lastDispo1ModifiedDate < "+dateInterval,
					new Object[] {});
		}

	}
	
	@Override
	protected void assignLeadIfNoProductServicable() {
		TSRAllotmentWFStep allotmentWFStep = new TSRAllotmentWFStep(zip, product, lead_id);
		allotmentWFStep.session = session;
		allotmentWFStep.configDate = configDate;
		allotmentWFStep.jt = jt;
		allotmentWFStep.status = status;
		allotmentWFStep.startAssignment();
	}

	@Override
	protected void assignLeadIfNoZipServicable() {
		TSRAllotmentWFStep allotmentWFStep = new TSRAllotmentWFStep(zip, product, lead_id);
		allotmentWFStep.session = session;
		allotmentWFStep.configDate = configDate;
		allotmentWFStep.jt = jt;
		allotmentWFStep.status = status;
		allotmentWFStep.startAssignment();
	}

}
