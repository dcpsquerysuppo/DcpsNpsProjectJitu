package com.tcs.sgv.filter.service;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.log4j.Logger;

import com.tcs.sgv.acl.acegilogin.filter.MetaCharFilter;

public class UrlEncodingFilter extends MetaCharFilter implements Filter {
	Logger logger = Logger.getLogger(UrlEncodingFilter.class);
	public void doFilterHttp(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, 
	ServletException { 
		
		logger.info("Url Encoding");
		if (!(request instanceof HttpServletRequest)) { 
			chain.doFilter(request, response); 
			return; 
		} 
		HttpServletResponseWrapper wrappedResponse = new HttpServletResponseWrapper(response) { 
			public String encodeRedirectUrl(String url) { 
				return url; 
			} 
			public String encodeRedirectURL(String url) { 
				return url; 
			} 
			public String encodeUrl(String url) { 
				return url; 
			} 
			public String encodeURL(String url) { 
				return url; 
			} 
		}; 
		chain.doFilter(request, wrappedResponse); 
	} 
}
