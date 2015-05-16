package com.mnt.authentication;

import org.springframework.security.authentication.AuthenticationServiceException;

import com.mnt.authentication.model.AuthUser;

public abstract class DefaultLoginHandler implements LoginHandler{

	@Override
	public Object onBadCredentials(Exception e) {
		return new LoginStatus(false,e.getMessage());
	}

	@Override
	public Object onOtherError(Exception e) {
		return new LoginStatus(false,e.getMessage());
	}

	@Override
	public Object onSuccess(AuthUser u) {
		return new LoginStatus(true,u.username);
	}
	
	  public class LoginStatus {
		  
		    private final boolean loggedIn;
		    private final String username;
		 
		    public LoginStatus(boolean loggedIn, String username) {
		      this.loggedIn = loggedIn;
		      this.username = username;
		    }
		 
		    public boolean isLoggedIn() {
		      return loggedIn;
		    }
		 
		    public String getUsername() {
		      return username;
		    }
	  }

	@Override
	public Object onAuthenticate(AuthUser u) {
		return new LoginStatus(true,u.username);
		
	}

	@Override
	public Object onUnAuthenticate() {
		return new LoginStatus(false,"no user logged");
	}

	@Override
	public Object onAuthenticationError(AuthenticationServiceException e) {
		return new LoginStatus(false,e.getMessage());
	}
}
