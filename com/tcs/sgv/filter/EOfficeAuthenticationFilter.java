package com.tcs.sgv.filter;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.security.Authentication;
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

public class EOfficeAuthenticationFilter extends AuthenticationProcessingFilter
{
  public Authentication attemptAuthentication(HttpServletRequest request)
  {
	  
	  Map paramMap = null;
		
		
		Enumeration paramEnum = request.getParameterNames();
		while(paramEnum.hasMoreElements()){
			paramMap = new HashMap();
			Object obj = paramEnum.nextElement();
			logger.info(" EOfficeAuthenticationFilter "+obj.toString());
			logger.info(" EOfficeAuthenticationFilter "+request.getParameter(obj.toString()));
			paramMap.put(obj, request.getParameter(obj.toString()));
		}
	  
	  RequestWrapper wrappedRequest  = new RequestWrapper(request,"ESTBVNM6701","ifms123",paramMap);
	  wrappedRequest.setParameter("j_username", "ESTBVNM6701");
	  wrappedRequest.setParameter("j_password", "ifms123");
		wrappedRequest.setParameter("locale", "en_US");
		//paramHttpServletRequest =wrappedRequest;
		//Enumeration attributeNames = request.getParameterNames();
		  
    String strlocale = wrappedRequest.getParameter("locale");
    wrappedRequest.getSession().setAttribute("locale", strlocale);
    Locale locale = null;
    System.out.println(" in  attemptAuthentication " + strlocale);

    if ((strlocale == null) || (strlocale.startsWith("en")))
    {
      locale = new Locale("en", "US");
    }
    else
    {
      locale = new Locale(strlocale);
    }
    SessionLocaleResolver localeResolver = new SessionLocaleResolver();
    localeResolver.setLocale(wrappedRequest, null, locale);
    return super.attemptAuthentication(wrappedRequest);
  }
}