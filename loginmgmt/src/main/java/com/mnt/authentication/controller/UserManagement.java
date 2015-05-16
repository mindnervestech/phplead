package com.mnt.authentication.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/user/{userid}")
public class UserManagement {

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.PUT, value="role/{roleid}")
	@ResponseBody
	public void grantRole() {
		
	}
	
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.DELETE,value="role/{roleid}")
	@ResponseBody
	public void revokeRole() {
		
	}
	
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.PUT, value="role/{groupid}")
	@ResponseBody
	public void grantGroup() {
		
	}
	
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.DELETE,value="role/{groupid}")
	@ResponseBody
	public void revokeGroup() {
		
	}
	
	
}
