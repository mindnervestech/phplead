package com.mnt.businessApp.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.ListUtils;

public abstract class AbstractAllotmentEngine {
	
	
	String zip; String product; String userType;
	
	protected List<Long> userPresent;
	protected List<Long> userZipPresent;
	protected List<Long> userProductPresent;
	
	// Maybe we need to have lead object in constructor
	public AbstractAllotmentEngine(String zip, String product, String userType) {
		super();
		this.zip = zip;
		this.product = product;
		this.userType = userType;
	}

	private List<Long> getQualifiedCadidates() {
		Map<String, List<Long>> zipPresent = AllotmentEngineCache.getInstance().zipCache.get(zip);
		Map<String, List<Long>> productPresent = AllotmentEngineCache.getInstance().productCache.get(product);
		if(zipPresent != null && productPresent != null) {
			List<Long> userZipPresent = zipPresent.get("userType");
			List<Long> userProductPresent = productPresent.get("userType");
			List<Long> userZipProductPresent = ListUtils.intersection(userZipPresent, userProductPresent);
			return userZipProductPresent;
		}
		return null;	
	}
	
	public void startAssignment() {
		Map<String, List<Long>> zipPresent = AllotmentEngineCache.getInstance().zipCache.get(zip);
		Map<String, List<Long>> productPresent = AllotmentEngineCache.getInstance().productCache.get(product);
		
		if(zipPresent != null && productPresent != null) {
			userPresent = getQualifiedCadidates();
			if(userPresent != null) { // serviceable dealers found for both Z and P
				if(userPresent.size() == 1) {
					assignLeadIfSingleUser();
					
				} else {
					assignLeadIfMultipleUser();
					//TODO: Assign based on configuration
				}
				
			} else {
				userZipPresent = zipPresent.get(userType);
				userProductPresent = productPresent.get(userType);
				
				if(userZipPresent == null && userProductPresent != null) { // serviceable dealers found only for P
					assignLeadIfNoZipServicable();
					//TODO: Find Near dealer and assign
				}
				
				if(userZipPresent != null && userProductPresent == null) { // serviceable dealers found only for Z
					assignLeadIfNoProductServicable();
					//TODO: Find Near dealer and assign
				}
				
				if(userZipPresent == null && userProductPresent == null) { // serviceable dealers not found at all
					assignLeadIfNoProductAndZipServicable();
					//TODO: Find Near dealer and assign
				}
				
			}
		}
		
	}

	protected abstract void assignLeadIfNoProductAndZipServicable();

	protected abstract void assignLeadIfNoProductServicable();

	protected abstract void assignLeadIfNoZipServicable() ;

	protected abstract void assignLeadIfMultipleUser();

	protected abstract void assignLeadIfSingleUser();

}
