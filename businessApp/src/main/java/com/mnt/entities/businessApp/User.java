package com.mnt.entities.businessApp;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

@Entity
public class User {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long id;
	public String address;
	public String birthday;
	public String email;
	public String gender;
	public String name;
	public String phone;
	public String postCode;
	public Boolean status = true;
	public String entityName;
	public String zone;
	public String state;
	public String district;
	public String customerGroup;
	
	@ManyToMany
	public List<Product> products;
	
	@ManyToMany
	public List<ZipCode> zipCodes;
	
	@OneToOne
	public User dealer;
	
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
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public List<Product> getProducts() {
		return products;
	}
	public void setProducts(List<Product> products) {
		this.products = products;
	}
	public String getPostCode() {
		return postCode;
	}
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
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
	public List<ZipCode> getZipCodes() {
		return zipCodes;
	}
	public void setZipCodes(List<ZipCode> zipCodes) {
		this.zipCodes = zipCodes;
	}
	public String getCustomerGroup() {
		return customerGroup;
	}
	public void setCustomerGroup(String customerGroup) {
		this.customerGroup = customerGroup;
	}
	public User getDealer() {
		return dealer;
	}
	public void setDealer(User dealer) {
		this.dealer = dealer;
	}
	public void addZipCode(ZipCode zipCode) {
		if(this.zipCodes == null){
			this.zipCodes = new ArrayList<>();
		}
		System.out.println("zipcode contains "+this.zipCodes.contains(zipCode));
		if(this.zipCodes.contains(zipCode)){
		} else {
			this.zipCodes.add(zipCode);
		}
	}
	public void addProducts(Product product) {
		if(this.products == null){
			this.products = new ArrayList<>();
		}
		if(!this.products.contains(product))
			this.products.add(product);
		
	}
	
	
}
