package com.mnt.entities.businessApp;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import com.mnt.entities.authentication.District;

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
	
	@ManyToOne
	public Zone zone;
	
	@ManyToOne
	public State state;
	
	@ManyToOne
	public District district;

	@ManyToMany(cascade=CascadeType.ALL, mappedBy = "user")
	public List<Dealer> dealer;
	
	@ManyToMany
	public List<Product> products;
	
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
	public Zone getZone() {
		return zone;
	}
	public void setZone(Zone zone) {
		this.zone = zone;
	}
	public State getState() {
		return state;
	}
	public void setState(State state) {
		this.state = state;
	}
	public District getDistrict() {
		return district;
	}
	public void setDistrict(District district) {
		this.district = district;
	}
	public String getPostCode() {
		return postCode;
	}
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
	public List<Dealer> getDealers() {
		return dealer;
	}
	public void setDealers(List<Dealer> dealers) {
		this.dealer = dealers;
	}
	public void addDealer(Dealer dealer) {
		if(this.dealer == null){
			this.dealer = new ArrayList<>();
		}
		this.dealer.add(dealer);
	}
	public void updateDealer(Dealer dealer) {
		this.dealer = new ArrayList<>();
		this.dealer.add(dealer);
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
	public void setEntityName(String role) {
		this.entityName = role;
	}
	public List<Dealer> getDealer() {
		return dealer;
	}
	public void setDealer(List<Dealer> dealer) {
		this.dealer = dealer;
	}
	
}
