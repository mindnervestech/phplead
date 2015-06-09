package com.mnt.entities.businessApp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class LeadAgeing {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;
	
	public Long lead_id;
	
	public Long dealer_id;
	
	public String zone;
	
	public String product;
	
	public String status;
	
	public Long ageing;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getLead_id() {
		return lead_id;
	}

	public void setLead_id(Long lead_id) {
		this.lead_id = lead_id;
	}

	public Long getDealer_id() {
		return dealer_id;
	}

	public void setDealer_id(Long dealer_id) {
		this.dealer_id = dealer_id;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getAgeing() {
		return ageing;
	}

	public void setAgeing(Long ageing) {
		this.ageing = ageing;
	}
	
	
}
