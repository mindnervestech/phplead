package com.mnt.authentication;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mnt.entities.authentication.AuthUser;

@Service
public class UserManagementSrv {

	@Autowired
    private SessionFactory sessionFactory;
	
	public void getUser(Long userid) {
		AuthUser u = (AuthUser) sessionFactory.getCurrentSession().createCriteria(AuthUser.class)
		.add(Restrictions.eq("id", userid)).uniqueResult();
	}

	
}
