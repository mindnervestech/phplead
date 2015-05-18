package com.mnt.authentication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import com.mnt.entities.authentication.AuthUser;

public class AuthenticationHelperImpl implements AuthenticationHelper {

	public Collection<GrantedAuthority> getRoles(AuthUser user) {
		List<GrantedAuthority> roles =  new ArrayList<GrantedAuthority>();
		roles.addAll(user.getRoles());
		return roles;
	}

}
