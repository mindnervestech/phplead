package com.mnt.authentication;

import org.springframework.security.authentication.AuthenticationServiceException;

import com.mnt.authentication.model.AuthUser;

public interface LoginHandler {
	
	Object onBadCredentials(Exception e);
	
	Object onOtherError(Exception e);
	
	Object onSuccess(AuthUser u);
	
	Object onAuthenticate(AuthUser u);
	
	Object onUnAuthenticate();

	Object onAuthenticationError(AuthenticationServiceException e);

}
