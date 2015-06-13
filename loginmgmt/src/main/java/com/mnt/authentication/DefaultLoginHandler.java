package com.mnt.authentication;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.context.request.RequestContextHolder;

import com.mnt.entities.authentication.AuthUser;

public abstract class DefaultLoginHandler implements LoginHandler{

	@Override
	public Object onBadCredentials(Exception e) {
		return new LoginStatus(e.getMessage());
	}

	@Override
	public Object onOtherError(Exception e) {
		return new LoginStatus(e.getMessage());
	}

	@Override
	public Object onSuccess(AuthUser u) {
		return new LoginStatus(u.username, 
				RequestContextHolder.currentRequestAttributes().getSessionId(),u.privResourceMap,u.name);
	}
	
	public class LoginStatus {
		  
		    private final boolean loggedIn;
		    private final String username;
		    private final String error;
		    private final String accessToken;
		    private final Object payload;
		    private final String name;
		 
		    public LoginStatus(boolean loggedIn, String username,String error, 
		    		String accessToken,Object payload, String name) {
		      this.loggedIn = loggedIn;
		      this.username = username;
		      this.error = error;
		      this.accessToken = accessToken;
		      this.payload = payload;
		      this.name = name;
		     
		    }
		    
		    public LoginStatus(String error) {
		    	this(false,"",error,"",null,"");
			}
		    
		    public LoginStatus(String username,String accessToken, Object payload,String name) {
		    	this(true,username,"",accessToken,payload,name);
			}
		 
		    public boolean isLoggedIn() {
		      return loggedIn;
		    }
		 
		    public String getUsername() {
		      return username;
		    }

			public String getAccessToken() {
				return accessToken;
			}

			public String getError() {
				return error;
			}

			public Object getPayload() {
				return payload;
			}

			public String getName() {
				return name;
			}
	  }

	@Override
	public Object onAuthenticate(AuthUser u) {
		return new LoginStatus(u.username,u.id+"","",u.name);
		
	}

	@Override
	public Object onUnAuthenticate() {
		return new LoginStatus("no user logged");
	}

	@Override
	public Object onAuthenticationError(AuthenticationServiceException e) {
		return new LoginStatus(e.getMessage());
	}
}
