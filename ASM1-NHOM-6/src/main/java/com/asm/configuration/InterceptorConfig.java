package com.asm.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.asm.component.AuthInterceptor;


@Configuration

public class InterceptorConfig implements WebMvcConfigurer{
	@Autowired
	AuthInterceptor interceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(interceptor).addPathPatterns("/admin/**").addPathPatterns("/auth/changepassword").addPathPatterns("/auth/updateprofile").addPathPatterns("/cart/**");
	}
}
