package com.upc.photo.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.upc.photo.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutHandler;


public class TokenClearLogoutHandler implements LogoutHandler {
	
	private UserService jwtUserService;
	
	public TokenClearLogoutHandler(UserService jwtUserService) {
		this.jwtUserService = jwtUserService;
	}

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		clearToken(authentication);
	}
	
	protected void clearToken(Authentication authentication) {
		if(authentication == null) {
			return;
		}
		UserDetails user = (UserDetails)authentication.getPrincipal();
		if(user!=null && user.getUsername()!=null) {
			jwtUserService.deleteUserLoginInfo(user.getUsername());
		}
	}

}
