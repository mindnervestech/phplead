package com.mnt.authentication.controller;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mnt.entities.authentication.AuthUser;

@Controller
@RequestMapping("/api/user/{userid}")
public class UserManagement {

	@Autowired
    private JdbcTemplate jt;

    @Autowired
    private SessionFactory sessionFactory;
	
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.PUT, value="role/{roleid}")
	@ResponseBody
	public int grantRole(@PathVariable Long roleid, @PathVariable Long userid) {
		
		return jt.update("insert into userrole (role_id, user_id) values (?,?)",
			      new Object[] {roleid, userid});		
	}
	
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.DELETE,value="role/{roleid}")
	@ResponseBody
	public int revokeRole(@PathVariable Long roleid, @PathVariable Long userid) {
		return jt.update("delete from userrole where role_id = ? and user_id = ?",
				  new Object[] {roleid, userid});
	}
	
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.PUT, value="group/{groupid}")
	@ResponseBody
	public int grantGroup(@PathVariable Long groupid, @PathVariable Long userid) {
		return jt.update("insert into authusers_permissiongroup (groups_group_id, authusers_auth_id) values (?,?)",
			      new Object[] {groupid, userid});
	}
	
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.DELETE,value="group/{groupid}")
	@ResponseBody
	public int revokeGroup(@PathVariable Long groupid, @PathVariable Long userid) {
		return jt.update("delete from authusers_permissiongroup where groups_group_id = ? and authusers_auth_id = ?",
				new Object[] {groupid, userid});
	}
	
	
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public int getUser(@PathVariable Long userid) {
		
		return 1;
	}
	
	
	
	
}
