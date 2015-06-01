package com.mnt.entities.authentication;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class District {

	@Id
	public Long id;
	public String name;
	
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
	
}