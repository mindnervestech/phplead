package com.mnt.businessApp.engine;

import java.util.Date;

public class ZSMAllotmentWFStep extends AbstractAllotmentEngine {

	public ZSMAllotmentWFStep(String zip, String product, Long lead_id) {
		super(zip, product,lead_id, "ZSM");
	}

	@Override
	protected void assignLeadIfNoProductAndZipServicable() {
	}

	@Override
	protected void assignLeadIfMultipleUser() {
		assignLeadIfSingleUser();
	}

	@Override
	protected void assignLeadIfSingleUser() {
		if(status .equals("assignment")){
			int count = jt.update("UPDATE lead SET lead.user_id = "+userPresent.get(0)+", lead.assignLeadDate = ? where lead.id = "+lead_id+" and lead.user_id is null ", new Date());
			if(count == 1){
				sendMail(userPresent.get(0));
			}
		} else if(status .equals("escalation")){
			String dateInterval = " DATE_SUB(CURDATE(), INTERVAL (Select generalconfig.firstEscalationTime from generalconfig where id = 1) DAY )";
			int count = jt.update("UPDATE lead SET lead.status = 'Escalated', lead.disposition1 = 'Escalated',lead.escalatedLevel = lead.escalatedLevel + 1, lead.escalatedDate = NOW(), lead.lastDispo1ModifiedDate = NOW(), "
					+ " lead.escalatedTo_id = "+userPresent.get(0)+" WHERE "
					+ " lead.disposition1 = 'New' and lead.id = "+lead_id+" and lead.lastDispo1ModifiedDate < "+dateInterval,
					new Object[] {});
			if(count == 1){
				sendMail(userPresent.get(0));
			}
		}

	}
	
	@Override
	protected void assignLeadIfNoProductServicable() {
	}

	@Override
	protected void assignLeadIfNoZipServicable() {
	}

}
