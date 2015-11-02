package com.mnt.businessApp.viewmodel;

import java.util.Date;
import java.util.Map;

import com.mnt.entities.businessApp.Lead;

public class BuildinLeadDetailsVM extends LeadDetailsVM{

	public String productId;
	public String area;
	public String kitchenmake;
	public String source;
	public Long budget;
	public String purchase;
	public String city;
	public String userName;
	
	public BuildinLeadDetailsVM(Lead lead){
		super(lead);
		this.productId = lead.getLeadDetails().getProduct().getId().toString();
		this.area = lead.getLeadDetails().getArea();
		this.kitchenmake = lead.getLeadDetails().getKitchenmake();
		this.source = lead.getLeadDetails().getSource();
		this.budget = lead.getLeadDetails().getBudget();
		this.purchase = lead.getLeadDetails().getPurchase();
		this.city = lead.getLeadDetails().getCity();
		this.userName = lead.getUser().getName();
		
	}
	

	public BuildinLeadDetailsVM() {
		
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}


	public String getArea() {
		return area;
	}


	public void setArea(String area) {
		this.area = area;
	}


	public String getKitchenmake() {
		return kitchenmake;
	}


	public void setKitchenmake(String kitchenmake) {
		this.kitchenmake = kitchenmake;
	}


	public String getSource() {
		return source;
	}


	public void setSource(String source) {
		this.source = source;
	}


	public Long getBudget() {
		return budget;
	}


	public void setBudget(Long budget) {
		this.budget = budget;
	}


	public String getPurchase() {
		return purchase;
	}


	public void setPurchase(String purchase) {
		this.purchase = purchase;
	}


	public String getCity() {
		return city;
	}


	public void setCity(String city) {
		this.city = city;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	
	
	

}
