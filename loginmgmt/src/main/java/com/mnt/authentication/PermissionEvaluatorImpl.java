package com.mnt.authentication;

import java.io.Serializable;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.mnt.entities.authentication.AuthUser;


@Component("permissionEvaluator")
/**
 * Permission at user level take precedence over role level
 * @author jagbirs
 *
 */
public class PermissionEvaluatorImpl  implements PermissionEvaluator  {

	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		AuthUser user = ((AuthUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		
		
		/*for(Role _r : user.getRoles()) {
			List<Actions> actions = _r.getPermisions();
			for(Actions _a : actions) {
				if(_a.getActionUrl().equalsIgnoreCase(permission.toString())) {
					return true;
				}
			}
			
		}*/
			
		return false;
	}

	//@Override
	public boolean hasPermission(Authentication arg0, Serializable arg1,
			String arg2, Object arg3) {
		return false;
	}

}
