package com.mnt.entities.businessApp;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;		
import javax.persistence.ManyToOne;

@Entity
public class LeadDetails {
	
	
	public Long srNo;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long 	id;
	public String	filter;
	public Date	uploadDate;
	public Date	firstCallDate;
	public Long 	sr;
	public String	title;
	public String	name;
	public String	email;
    public Long		contactNo;
    public String	state;
    public String	city;
    public Long		pinCode;
    
    @ManyToOne
    public Product	product;
    
    public String	type;
    public String	contactMe;
    public String	campaignName;
    public String	siteId;
    public String	creativeId;
    public String	page;
    public String	lms;
    public Date	leadDate;
    public String	ipAddress;
    public Date	lastCallDate;
	public String	lastCallStatus;
	public String	categorization;
	public String	salesPersonContact;
	public String	areaofInterest1;
	public String	areaofInterest2;
	public String	areaofInterest3;
	public String	remarks1;
	public String	remarks2;
	
	public Long getSrNo() {
		return srNo;
	}
	public Long getId() {
		return id;
	}
	public String getFilter() {
		return filter;
	}
	public Date getUploadDate() {
		return uploadDate;
	}
	public Date getFirstCallDate() {
		return firstCallDate;
	}
	public Long getSr() {
		return sr;
	}
	public String getTitle() {
		return title;
	}
	public String getName() {
		return name;
	}
	public String getEmail() {
		return email;
	}
	public Long getContactNo() {
		return contactNo;
	}
	public String getState() {
		return state;
	}
	public String getCity() {
		return city;
	}
	public Long getPinCode() {
		return pinCode;
	}
	public String getType() {
		return type;
	}
	public String getContactMe() {
		return contactMe;
	}
	public String getCampaignName() {
		return campaignName;
	}
	public String getSiteId() {
		return siteId;
	}
	public String getCreativeId() {
		return creativeId;
	}
	public String getPage() {
		return page;
	}
	public String getLms() {
		return lms;
	}
	public Date getLeadDate() {
		return leadDate;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public Date getLastCallDate() {
		return lastCallDate;
	}
	public String getLastCallStatus() {
		return lastCallStatus;
	}
	public String getCategorization() {
		return categorization;
	}
	public String getSalesPersonContact() {
		return salesPersonContact;
	}
	public String getAreaofInterest1() {
		return areaofInterest1;
	}
	public String getAreaofInterest2() {
		return areaofInterest2;
	}
	public String getAreaofInterest3() {
		return areaofInterest3;
	}
	public String getRemarks1() {
		return remarks1;
	}
	public String getRemarks2() {
		return remarks2;
	}
	public void setSrNo(Long srNo) {
		this.srNo = srNo;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}
	public void setFirstCallDate(Date firstCallDate) {
		this.firstCallDate = firstCallDate;
	}
	public void setSr(Long sr) {
		this.sr = sr;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setContactNo(Long contactNo) {
		this.contactNo = contactNo;
	}
	public void setState(String state) {
		this.state = state;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public void setPinCode(Long pinCode) {
		this.pinCode = pinCode;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setContactMe(String contactMe) {
		this.contactMe = contactMe;
	}
	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}
	public void setCreativeId(String creativeId) {
		this.creativeId = creativeId;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public void setLms(String lms) {
		this.lms = lms;
	}
	public void setLeadDate(Date leadDate) {
		this.leadDate = leadDate;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public void setLastCallDate(Date lastCallDate) {
		this.lastCallDate = lastCallDate;
	}
	public void setLastCallStatus(String lastCallStatus) {
		this.lastCallStatus = lastCallStatus;
	}
	public void setCategorization(String categorization) {
		this.categorization = categorization;
	}
	public void setSalesPersonContact(String salesPersonContact) {
		this.salesPersonContact = salesPersonContact;
	}
	public void setAreaofInterest1(String areaofInterest1) {
		this.areaofInterest1 = areaofInterest1;
	}
	public void setAreaofInterest2(String areaofInterest2) {
		this.areaofInterest2 = areaofInterest2;
	}
	public void setAreaofInterest3(String areaofInterest3) {
		this.areaofInterest3 = areaofInterest3;
	}
	public void setRemarks1(String remarks1) {
		this.remarks1 = remarks1;
	}
	public void setRemarks2(String remarks2) {
		this.remarks2 = remarks2;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
}
