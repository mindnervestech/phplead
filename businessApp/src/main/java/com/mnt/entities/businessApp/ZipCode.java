package com.mnt.entities.businessApp;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ZipCode {

	@Id
	public Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	
}