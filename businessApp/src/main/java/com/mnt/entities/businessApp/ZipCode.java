package com.mnt.entities.businessApp;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ZipCode {

	@Id
	public Long id;
	
	public Double longitude;
	
	public Double latitude;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	
	
}
