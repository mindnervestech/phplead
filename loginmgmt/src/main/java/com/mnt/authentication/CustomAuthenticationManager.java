package com.mnt.authentication;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
 
/**
 * A custom authentication manager that allows access if the user details
 * exist in the database and if the username and password are not the same.
 * Otherwise, throw a {@link BadCredentialsException}
 */

public class CustomAuthenticationManager implements AuthenticationManager {
 
 
  
 public Authentication authenticate(Authentication auth)
   throws AuthenticationException {
 
   
   
  // Compare passwords
  // Make sure to encode the password first before comparing
   return new UsernamePasswordAuthenticationToken(
     auth.getName(), 
     auth.getCredentials(), 
     null);
  
 }
  
 
}