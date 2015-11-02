package com.mnt.authentication.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mnt.authentication.LoginHandler;
import com.mnt.entities.authentication.AuthUser;
import com.mnt.entities.authentication.PermissionMatrix;

@Controller
@RequestMapping("/api/login")
public class LoginController {
	
	 /*@Autowired(required = false)
	 @Qualifier("authenticationManager")
	 AuthenticationManager authenticationManager;*/
	
	@Autowired
    private SessionFactory sessionFactory;
	
	@Autowired(required = true)
	@Qualifier("loginHandler")
	LoginHandler loginHandler;
	
	@Autowired
	SecurityContextRepository repository;
	
	@Autowired
	AuthenticationProvider authenticationProvider;  
	 
  @RequestMapping(method = RequestMethod.GET,headers={"Accept=text/xml, application/json"})
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
  @Transactional
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
      fetchUserPermissionMap(user);
      return loginHandler.onSuccess(user); 
    } catch (BadCredentialsException e) {
    	return loginHandler.onBadCredentials(e);
      
    } catch (AuthenticationServiceException e) {
    	return loginHandler.onAuthenticationError(e);
    } catch (Exception e) {
    	return loginHandler.onOtherError(e);
    }
    
  }
  
 
  private void fetchUserPermissionMap(AuthUser u) {
	  SQLQuery query = sessionFactory.getCurrentSession().
			  createSQLQuery("select * from permissionmatrix pm "
			  		+ " WHERE pm.user_id = :user_id OR "
			  		+ " pm.role_id in (select role_id from userrole ur where ur.user_id = :user_id) OR "
			  		+ " pm.group_id in (select groups_group_id from authusers_permissiongroup ug where ug.authusers_auth_id = :user_id)"
			  		+ "");
	  query.setParameter("user_id", u.id);
	  query.addEntity(PermissionMatrix.class);
	  List<PermissionMatrix> permissions = query.list();
	  System.out.println("Permision got " + permissions.size());
	  
	  Map<String,Integer> privResourceMap = new HashMap<String,Integer>();
	  
	  for(PermissionMatrix matrix : permissions) {
		  String resource = matrix.getAction().getActionUrl();
		  int accessLevelFromDB = matrix.getAccessLevel();
		  Integer accessLevel = privResourceMap.get(resource);
		  if(accessLevel == null ||
				  accessLevel < accessLevelFromDB ) {
			  privResourceMap.put(resource,accessLevelFromDB);
		  } 
	  }
	  privResourceMap.put("role",u.getRoles().get(0).getId());
	  int type = 0;
	  if(u.getType() != null)
	  {
		  if(u.getType().equals("Built-In") || u.getType().equals("Both")){
			  type = 1;
			  privResourceMap.put("type", type);
		  }
		  else{
			  privResourceMap.put("type", type);
		  }
	  }
	  else{
		  privResourceMap.put("type", type);
	  }
	  u.privResourceMap = privResourceMap;
	  System.out.println("u.privResourceMap :: " + u.privResourceMap);
	  
	  /*"select * from permissionmatrix pm
	  where 
	  pm.user_id = ? or
	  pm.role_id in (
	  select role_id userrole ur where ur.user_id = ?  
	  ) or
	  pm.group_id in (
	  select group_id usergroup ug where ug.user_id = ?
	  )*/  
  }
  

	
}