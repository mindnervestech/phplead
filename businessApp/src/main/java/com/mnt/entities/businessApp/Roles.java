package com.mnt.entities.businessApp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;



@Entity
public class Roles {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public int role_id;
	public String name;
	public String report_freq;
	
	public int getRole_id() {
		return role_id;
	}
	public void setRole_id(int role_id) {
		this.role_id = role_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getReport_freq() {
		return report_freq;
	}
	public void setReport_freq(String report_freq) {
		this.report_freq = report_freq;
	}
	
	
	
	

	
}
