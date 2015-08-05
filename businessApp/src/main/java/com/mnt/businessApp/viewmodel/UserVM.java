package com.mnt.businessApp.viewmodel;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.mnt.entities.businessApp.User;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserVM {

	public Long id;
	public String name;
	public String email;
	public String gender;
	public String birthday;
	public String phone;
	public String zone;
	public ZoneVM role;
	public String address;
	public String state;
	public String district;
	public String postCode;
	public List<ProductVM> products;
	public Boolean selected;
	public String status;
	public Long dealer;
	public List<PinsVM> pins;
	public List<ZoneVM> ids;
	public String customerGroup;
	private List<String> productlist;
	
	public UserVM(){}
	
	public UserVM(User user) {
		this.id = user.getId();
		this.name = user.getName();
		this.address = user.getAddress();
		this.birthday = user.getBirthday();
		this.district = user.getDistrict();
		this.email = user.getEmail();
		this.gender = user.getGender();
		this.phone = user.getPhone();
		this.postCode = user.getPostCode();
		this.state = user.getState();
		this.zone = user.getZone();
		this.customerGroup = user.getCustomerGroup();
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
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
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
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}

	public List<PinsVM> getPins() {
		return pins;
	}

	public void setPins(List<PinsVM> pins) {
		this.pins = pins;
	}

	public String getCustomerGroup() {
		return customerGroup;
	}

	public void setCustomerGroup(String customerGroup) {
		this.customerGroup = customerGroup;
	}

	public List<String> getProductlist() {
		return productlist;
	}

	public void setProductlist(List<String> productlist) {
		this.productlist = productlist;
	}
	public List<ZoneVM> getIds() {
		return ids;
	}

	public void setIds(List<ZoneVM> ids) {
		this.ids = ids;
	}
}
