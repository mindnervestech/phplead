package com.mnt.businessApp.viewmodel;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
public class RolesVM {
	
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
