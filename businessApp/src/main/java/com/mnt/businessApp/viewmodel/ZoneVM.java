package com.mnt.businessApp.viewmodel;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.mnt.entities.authentication.District;
import com.mnt.entities.businessApp.State;
import com.mnt.entities.businessApp.User;
import com.mnt.entities.businessApp.Zone;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ZoneVM {

	public Long id;
	public int roleId;
	public String name;
	
	public ZoneVM() {}
	
	public ZoneVM(Zone zone) {
		this.id = zone.getId();
		this.name = zone.getName();
	}
	
	public ZoneVM(State state) {
		this.id = state.getId();
		this.name = state.getName();
	}
	
	public ZoneVM(District district) {
		this.id = district.getId();
		this.name = district.getName();
	}
	
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
