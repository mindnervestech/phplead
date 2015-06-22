package com.mnt.businessApp.viewmodel;

import java.util.List;

public class ReassignVM {

	public List<Long> ids;
	public ReassignUserVM reassign;
	
	public ReassignVM() {
	}

	public List<Long> getIds() {
		return ids;
	}

	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

	public ReassignUserVM getUserVM() {
		return reassign;
	}

	public void setUserVM(ReassignUserVM userVM) {
		this.reassign = userVM;
	}

	
}
