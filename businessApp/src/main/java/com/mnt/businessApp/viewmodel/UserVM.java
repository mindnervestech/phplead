package com.mnt.businessApp.viewmodel;

import java.util.Date;
import java.util.List;

public class UserVM {

	public Long id;
	public String name;
	public String email;
	public String gender;
	public String birthday;
	public String phone;
	public String zone;
	public String role;
	public String address;
	public String state;
	public String district;
	public String postCode;
	public String roleName;
	public String zoneName;
	public List<ProductVM> products;
	private List<String> productlist;
	
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
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
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
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getZoneName() {
		return zoneName;
	}
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}
	public List<ProductVM> getProducts() {
		return products;
	}
	public void setProducts(List<ProductVM> productList) {
		this.products = productList;
	}
	public List<String> getProductList() {
		return productlist;
	}
	public void setProductList(List<String> productList) {
		this.productlist = productList;
	}
	
	
}
