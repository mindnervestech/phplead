package com.mnt.businessApp.viewmodel;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.mnt.entities.businessApp.User;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ZoneVM {

	public Long id;
	public int roleId;
	public String name;
	
	public ZoneVM() {}
	
	public ZoneVM(Map<String, Object> map) {
		this.id = (Long) map.get("id");
		this.name = (String) map.get("name");
	}
	
	public ZoneVM(User user) {
		this.id = user.getId();
		this.name = user.getName();
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getRoleId() {
		return roleId;
	}
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	
	
}
