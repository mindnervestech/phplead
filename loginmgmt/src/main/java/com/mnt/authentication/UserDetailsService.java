package com.mnt.authentication;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import com.mnt.entities.authentication.AuthUser;


@Transactional
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    @Autowired
    private JdbcTemplate jt;

    @Autowired
    private SessionFactory sessionFactory;
    
    @Override
    public UserDetails loadUserByUsername(String username) {
    	
    	AuthUser usr = (AuthUser) sessionFactory.getCurrentSession().createQuery("from AuthUser where username = :username").setString("username", username).uniqueResult();
    	return usr;
    	
    }

    
	public void ChangeRolePermission(boolean mode, int role, int permission) {
		/*Actions action = (Actions) sf.getCurrentSession().createQuery("from Actions where id = :id")
			    .setInteger("id", permission).uniqueResult();
		Role r = (Role) sf.getCurrentSession().createQuery("Select r from Role r , IN(r.permisions) p where r.id =:id and p.id = :action")
	    .setLong("action", action.getId()).setInteger("id", role).uniqueResult();*/
		
		int count = jt.queryForInt("select count(*) from role_action  where role_id = ? and action_id = ?",
				new Object[] {role, permission});
		
		if(count == 0 && mode == true) {
			jt.update("insert into role_action (role_id, action_id) values (?,?)",
				      new Object[] {role, permission});
		}
		
		if(count == 1 && mode == false) {
			jt.update("delete from role_action where role_id = ? and action_id = ?",
					  new Object[] {role, permission});
		}
		
	}
	
	/*
	 * This method is written for ACL  
	 */
	/*public void ChangeUserPermission(boolean mode, int user, int permission) {
		
		int count = jt.queryForInt("select count(*) from user_action  where user_id = ? and action_id = ?",
				new Object[] {user, permission});
		
		if(count == 0) {
			jt.update("insert into user_action (user_id, action_id, state) values (?,?,?)",
				      new Object[] {user, permission, mode?"S":"H"});
		}
		
		if(count == 1) {
			jt.update("delete from user_action where user_id = ? and action_id = ?",
					  new Object[] {user, permission});
			
			jt.update("insert into user_action (user_id, action_id, state) values (?,?,?)",
				      new Object[] {user, permission, mode?"S":"H"});
		}
		
	}*/
	
	public void ChangeUserRole(boolean mode, int user, int role) {
		
		int count = jt.queryForInt("select count(*) from authorities  where user_id = ? and role_id = ?",
				new Object[] {user, role});
		
		if(count == 0 && mode == true) {
			jt.update("insert into authorities (role_id, user_id) values (?,?)",
				      new Object[] {role, user});
		}
		
		if(count == 1 && mode == false) {
			jt.update("delete from authorities where role_id = ? and user_id = ?",
					  new Object[] {role, user});
		}
		
	}
	
	

	

	public User getUserByUnamePassword(String user, String password) {
		User _user =(User) sessionFactory.getCurrentSession().createQuery("from User where username = :username and password =:password").
		setString("username", user).setString("password", password).uniqueResult();
		return _user;
	}

	
}