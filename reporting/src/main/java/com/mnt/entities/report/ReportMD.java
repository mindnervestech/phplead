package com.mnt.entities.report;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

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
	
	
	
}
