package com.tcs.sgv.filter;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class RequestWrapper extends HttpServletRequestWrapper {
	
	String usrName = null;
	String password = null;
	Map paramMap = null;
	Map attriMap = null;
	RequestWrapper(ServletRequest  request,String usrName,String password,Map paramMap){
		super((HttpServletRequest)request);
		this.usrName = usrName;
		this.password = password;
		this.paramMap = paramMap;
		this.attriMap = attriMap;
	}
	
	public String getParameter(String paramName) {
		String value = super.getParameter(paramName);
		if ("j_username".equals(paramName)) {
			value = this.usrName;
		}
		if ("j_password".equals(paramName)) {
			value = this.password;
		}
		if ("locale".equals(paramName)) {
			value = "en_US";
		}
		if(this.paramMap.containsKey(paramName))
			value =  this.paramMap.get(paramName).toString();
		System.out.println("value "+value);
		return value;
	}
	
	public void setParameter(String paramName, String paramValue){
		
		this.paramMap.put(paramName, paramValue);
	}


}
