package com.mnt.authentication.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mnt.authentication.LoginHandler;
import com.mnt.entities.authentication.AuthUser;

@Controller
@RequestMapping("/api/login")
public class LoginController {
	
	 /*@Autowired(required = false)
	 @Qualifier("authenticationManager")
	 AuthenticationManager authenticationManager;*/
	
	@Autowired(required = true)
	@Qualifier("loginHandler")
	LoginHandler loginHandler;
	
	@Autowired
	SecurityContextRepository repository;
	
	@Autowired
	AuthenticationProvider authenticationProvider;  
	 
  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  public Object getStatus() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    AuthUser user = ((AuthUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    
    if (auth != null && !auth.getName().equals("anonymousUser") && auth.isAuthenticated()) {
      return loginHandler.onAuthenticate(user);
    } else {
      return loginHandler.onUnAuthenticate();
    }
  }	
	
  @RequestMapping(method = RequestMethod.POST)
  @ResponseBody
  public Object login(@RequestParam("j_username") String username,
                           @RequestParam("j_password") String password,
                           HttpServletRequest request, HttpServletResponse response) {
 
	UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
    AuthUser details = new AuthUser();
    details.username = username;
    token.setDetails(details);
 
    try {
    	
      Authentication auth = authenticationProvider.authenticate(token);//authenticationManager.authenticate(token);
      SecurityContextHolder.getContext().setAuthentication(auth);
      repository.saveContext(SecurityContextHolder.getContext(), request, response);
      AuthUser user = ((AuthUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
      return loginHandler.onSuccess(user); 
    } catch (BadCredentialsException e) {
    	return loginHandler.onBadCredentials(e);
      
    } catch (AuthenticationServiceException e) {
    	return loginHandler.onAuthenticationError(e);
    } catch (Exception e) {
    	return loginHandler.onOtherError(e);
    }
    
  }
  

	
}