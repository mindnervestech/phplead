package com.mnt.entities.authentication;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name="roles")
public class Role implements java.io.Serializable, GrantedAuthority {
    public int id;
    public String name;
    public String report_freq;

    public List<PermissionMatrix> permissionMatrix = new ArrayList<PermissionMatrix>();
    
    @OneToMany(fetch=FetchType.LAZY)
    @JoinColumn(name="role_id")
    public List<PermissionMatrix> getPermissionMatrix() { return permissionMatrix; }
    
    public void setPermissionMatrix(List<PermissionMatrix> permissionMatrix) {
    	this.permissionMatrix = permissionMatrix;
    } 
    
    @Transient
    public String getAuthority() {
    	return name;
    }
    
    @Id
    @Column(name="role_id")
    public int getId() { return id; }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() { return name; }
    
    public void setName(String name) {
        this.name = name;
    }
	
    
	public String getReport_freq() {
		return report_freq;
	}

	public void setReport_freq(String report_freq) {
		this.report_freq = report_freq;
	}

	@Override
	public boolean equals(Object obj) {
		return id == ((Role)obj).id;
	}
}
