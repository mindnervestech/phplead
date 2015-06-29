package com.mnt.businessApp.viewmodel;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.mnt.entities.authentication.District;
import com.mnt.entities.businessApp.State;
import com.mnt.entities.businessApp.Zone;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SplineVM {

	public List<List> data;
	public String label;
	public String color;
	
	public SplineVM() {
		// TODO Auto-generated constructor stub
	}

	public List<List> getData() {
		return data;
	}

	public void setData(List<List> maps) {
		this.data = maps;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	
	
}
