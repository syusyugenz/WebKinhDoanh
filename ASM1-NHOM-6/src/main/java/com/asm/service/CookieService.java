package com.asm.service;

import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class CookieService {
	@Autowired
	HttpServletRequest request;
	
	@Autowired 
	HttpServletResponse response;
	
	public Cookie create(String name, String value, int days) {
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(days*24*60*60);
		cookie.setPath("/");
		response.addCookie(cookie);
		return cookie;
	}
	
	public Cookie getCookie(String name) {
		Cookie[]cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if(cookie.getName().equalsIgnoreCase(name)) {
				return cookie;
			}
		}
		return null;
	}
	
	 public void delete(String name) {
	        Cookie cookie = getCookie(name);
	        if (cookie != null) {
	            cookie.setMaxAge(0);
	            cookie.setPath("/");
	            response.addCookie(cookie);
	        }
	    }
}
