package com.asm.component;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.asm.model.Customer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor{
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		 String uri = request.getRequestURI();
	        Customer customer = (Customer) request.getSession().getAttribute("customer");
	        if (uri.contains("admin") || uri.equals("/auth/changepassword") || uri.equals("/auth/updateprofile") || uri.contains("cart")) {
	            if (customer == null) {
	                response.sendRedirect("/auth/login"); 
	                return false;
	            } else if (uri.contains("admin") && !customer.isAdmin()) {
	                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Không có quyền truy cập");
	                return false;
	            }
	        }
	        return true;
	    }	
	}	
