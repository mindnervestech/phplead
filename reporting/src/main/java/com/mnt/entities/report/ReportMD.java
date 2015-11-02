package com.mnt.entities.report;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity

public class ReportMD {

	@Id
	private Long id;
	
	private String name;
	
	@Column(length=500)
	private String description;
	
	@Column(name="query")
	private String sql;
	
	
	@Column(name="jsonSchema",length=500)
	private String jsonSchema;
	
	
	@Column(name="jsonForm",length=500)
	private String jsonForm;
	
	@Column(length=500)
	private String access;
	
	@Column(length=500)
	private String columns;
	
	private Date emailSendDate;
	
	private String frequency;
	
	private Boolean isMail;
	
	private String userName;
	
	private Long userId;
	
	private String userEmail;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public Date getEmailSendDate() {
		return emailSendDate;
	}

	public void setEmailSendDate(Date emailSendDate) {
		this.emailSendDate = emailSendDate;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public Boolean getIsMail() {
		return isMail;
	}

	public void setIsMail(Boolean isMail) {
		this.isMail = isMail;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getJsonSchema() {
		return jsonSchema;
	}

	public void setJsonSchema(String jsonSchema) {
		this.jsonSchema = jsonSchema;
	}

	public String getJsonForm() {
		return jsonForm;
	}

	public void setJsonForm(String jsonForm) {
		this.jsonForm = jsonForm;
	}

	public String getAccess() {
		return access;
	}

	public void setAccess(String access) {
		this.access = access;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	
	
}
