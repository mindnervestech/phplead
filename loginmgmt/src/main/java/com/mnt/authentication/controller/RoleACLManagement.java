package com.mnt.authentication.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/role/{roleid}/resource/{resourseid}/access/{accessid}")
public class RoleACLManagement {

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.PUT)
	@ResponseBody
	public void grantPermission() {
		
	}
	
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.DELETE)
	@ResponseBody
	public void revokePermission() {
		
	}
	
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public void permission(@PathVariable Integer roleid,
			@PathVariable Integer resourseid, @PathVariable Integer accessid) {
		String sql="Select * FROM authusers , roles , actionable , user_action";
	}
}
