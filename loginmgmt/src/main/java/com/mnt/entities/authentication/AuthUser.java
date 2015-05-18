package com.mnt.entities.authentication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.transaction.annotation.Transactional;
@Entity
@Table(name="authusers")
public class AuthUser implements UserDetails {
	
	public Long id;
    public String username;
    public String password;
    public String email;
    
    public List<Role> roles = new ArrayList<Role>();
    
    public List<Group> groups = new ArrayList<Group>();
    
    public List<PermissionMatrix> permissionMatrix = new ArrayList<PermissionMatrix>();
    
    
    @Transient
    //@Transactional
    public Collection<GrantedAuthority> getAuthorities() {
    	List<GrantedAuthority> roles =  new ArrayList<GrantedAuthority>();
		roles.addAll(getRoles());
		return roles;
    }

    @Transient
    public boolean isAccountNonExpired() {
    	return true;
    }

    @Transient
    public boolean isAccountNonLocked() {
    	return true;
    }

    @Transient
    public boolean isCredentialsNonExpired() {
    	return true;
    }


    /* non UserDetails methods */
    @Id
    @Column(name="auth_id")
    public Long getId() { return id; }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() { return username; }
    
    public void setUsername(String username) {
        this.username = username;
    }

    //@Override
    public String getPassword() { return password; }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    @Column(name="email_id")
    public String getEmail() { return email; }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    

    
   
    /*
     * Below code is Written for ACL , Not in Scope for now (08-2014).
     * 
    public List<UserPermissionAction> allowedpermisions;
	public List<UserPermissionAction> hiddenpermisions; 
    
    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
    @JoinColumn(name="user_id")
    @Where(clause="state='S'")
    @Fetch(value = FetchMode.SUBSELECT)
	public List<UserPermissionAction> getAllowedpermisions() {
		return allowedpermisions;
	}
    
    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
    @JoinColumn(name="user_id")
    @Where(clause="state='H'")
    @Fetch(value = FetchMode.SUBSELECT)//http://stackoverflow.com/questions/4334970/hibernate-cannot-simultaneously-fetch-multiple-bags
    public List<UserPermissionAction> getHiddenpermisions() {
		return hiddenpermisions;
	}

	public void setAllowedpermisions(List<UserPermissionAction> allowedpermisions) {
		this.allowedpermisions = allowedpermisions;
	}

	public void setHiddenpermisions(List<UserPermissionAction
			
			> hiddenpermisions) {
		this.hiddenpermisions = hiddenpermisions;
	}*/
    
    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="userrole",
	       joinColumns=@JoinColumn(name="user_id"),
	       inverseJoinColumns=@JoinColumn(name="role_id"))
    public List<Role> getRoles() { return roles; }

    public void setRoles(List<Role> roles) {
    	this.roles = roles;
    }
    
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id")
    public List<Group> getGroups() { return groups; }
    
    public void setGroups(List<Group> groups) {
    	this.groups = groups;
    }
    
    @OneToMany
    @JoinTable(
	       joinColumns=@JoinColumn(name="user_id"),
	       inverseJoinColumns=@JoinColumn(name="permisionmatrix_id"))
    public List<PermissionMatrix> getPermissionMatrix() { return permissionMatrix; }
    
    public void setPermissionMatrix(List<PermissionMatrix> permissionMatrix) {
    	this.permissionMatrix = permissionMatrix;
    }
   
    @Transient
	//@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	
}
