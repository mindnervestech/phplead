package com.mnt.authentication;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import com.mnt.entities.authentication.AuthUser;

public interface AuthenticationHelper {
	Collection<GrantedAuthority> getRoles(AuthUser user) ;
}
