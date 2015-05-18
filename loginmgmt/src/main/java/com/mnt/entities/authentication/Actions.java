package com.mnt.entities.authentication;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="actionable")
public class Actions {

	
	@Id
	@Column(name="action_url")
	private String actionUrl;
	
	@Column(name="action_name")
	private String actionName;
	
	

	public Actions(){}
	
	public Actions(String actionUrl) {
		this.actionUrl = actionUrl;
	}



	public String getActionUrl() {
		return actionUrl;
	}

	public void setActionUrl(String actionUrl) {
		this.actionUrl = actionUrl;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	
	

	
	
}
