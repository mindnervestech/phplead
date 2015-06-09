package com.mnt.businessApp.viewmodel;

import java.util.List;

import com.mnt.entities.businessApp.Dealer;

public class DealerVM {

	public Long id;
	public String code;
	public String name;
	public String customerGroup;
	public String phone;
	public String email;
	public ZoneVM zone;
	public ZoneVM territory;
	public String rsm;
	public List<PinsVM> pins;
	public String address;
	public String state;
	public String district;
	public String subdist;
	public String zipCode;
	
	
	public DealerVM() {
	}
	
	public DealerVM(Dealer dealer) {
		this.id = dealer.getId();
		this.name = dealer.getDealerName();
		this.address = dealer.getAddress();
		this.customerGroup = dealer.getCustomerGroup();
		this.code = dealer.getDealerCode();
		this.district = dealer.getDistrict();
		this.email = dealer.getEmail();
		this.phone =dealer.getPhone();
		this.rsm = dealer.getRsm().getName();
		this.state = dealer.getState();
		this.subdist = dealer.getSubDistrict();
		//this.territory = dealer.getTerritory();
		//this.zone = dealer.getZone();
		this.zipCode = dealer.getZipCode();
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCustomerGroup() {
		return customerGroup;
	}
	public void setCustomerGroup(String customerGroup) {
		this.customerGroup = customerGroup;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public ZoneVM getZone() {
		return zone;
	}
	public void setZone(ZoneVM zone) {
		this.zone = zone;
	}
	public ZoneVM getTerritory() {
		return territory;
	}
	public void setTerritory(ZoneVM territory) {
		this.territory = territory;
	}
	public String getRsm() {
		return rsm;
	}
	public void setRsm(String rsm) {
		this.rsm = rsm;
	}
	public List<PinsVM> getPins() {
		return pins;
	}
	public void setPins(List<PinsVM> pins) {
		this.pins = pins;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getSubdist() {
		return subdist;
	}
	public void setSubdist(String subdist) {
		this.subdist = subdist;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
}
