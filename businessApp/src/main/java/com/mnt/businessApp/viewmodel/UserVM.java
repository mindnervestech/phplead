package com.mnt.businessApp.viewmodel;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserVM {

	public Long id;
	public String name;
	public String email;
	public String gender;
	public String birthday;
	public String phone;
	public ZoneVM zone;
	public ZoneVM role;
	public String address;
	public ZoneVM state;
	public ZoneVM district;
	public String postCode;
	public List<ProductVM> products;
	public Boolean selected;
	public String status;
	public Long dealer;
	
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public ZoneVM getZone() {
		return zone;
	}
	public void setZone(ZoneVM zone) {
		this.zone = zone;
	}
	public ZoneVM getRole() {
		return role;
	}
	public void setRole(ZoneVM role) {
		this.role = role;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public ZoneVM getState() {
		return state;
	}
	public void setState(ZoneVM state) {
		this.state = state;
	}
	public ZoneVM getDistrict() {
		return district;
	}
	public void setDistrict(ZoneVM district) {
		this.district = district;
	}
	public String getPostCode() {
		return postCode;
	}
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
	public List<ProductVM> getProducts() {
		return products;
	}
	public void setProducts(List<ProductVM> products) {
		this.products = products;
	}
	public Boolean getSelected() {
		return selected;
	}
	public void setSelected(Boolean selected) {
		this.selected = selected;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getDealer() {
		return dealer;
	}
	public void setDealer(Long dealer) {
		this.dealer = dealer;
	}
}
