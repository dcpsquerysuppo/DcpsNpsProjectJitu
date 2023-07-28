package com.tcs.sgv.filter.service;

import com.tcs.sgv.acl.acegilogin.filter.MetaCharFilter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Random;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.security.context.SecurityContext;

public class CSRFProcessingFilter extends MetaCharFilter implements Filter {
   Logger logger = Logger.getLogger(CSRFProcessingFilter.class);
   private final String loginPageName = "/login.jsp";
   private final String projectName = "/";
   private final String springCheck = "j_spring_security_check";
   private String errorPage;

   public void doFilterHttp(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
      HttpSession sess = request.getSession();
      String str = null;
      String str1 = null;
      String csrfTokenNum = "";
      String actionFlagg="";
      String viewName="";
      Enumeration localEnumeration = request.getParameterNames();
      SecurityContext securityContext = (SecurityContext)sess.getAttribute("SPRING_SECURITY_CONTEXT");
      while(localEnumeration.hasMoreElements()) {
         str = (String)localEnumeration.nextElement();
         str1 = request.getParameter(str);
         this.logger.info("str1 is ::::::" + str1);
         this.logger.info("str is ::::::" + str);
         if (str != null && !str.equals("") && str.equals("check")) {
            csrfTokenNum = str1;
            this.logger.info("csrfTokenNum " + str1);
         }
         if (str != null && !str.equals("") && str.equals("actionFlag")) {
        	 actionFlagg = str1;
             this.logger.info("actionFlag " + actionFlagg);
          }
         if (str != null && !str.equals("") && str.equals("viewName")) {
        	 viewName = str1;
             this.logger.info("viewName " + viewName);
          }
      }
	
      this.logger.info("csrf_Token" + sess.getAttribute("csrf_Token"));
      
      
     // if(actionFlagg !=null && actionFlagg !=""){
      if (request.getParameter("actionFlag") != null && request.getParameter("actionFlag").equals("InsertDeptCompMpg")) {
         if (sess.getAttribute("csrf_Token").toString().equals(csrfTokenNum)) {
            this.logger.info("CSRF Token present DEPT COMP MAG ======");
         } else {
            this.logger.info("NO CSRF Token present for DEPT COMP MAG ======");
            RequestDispatcher localRequestDispatcher = request.getRequestDispatcher(this.errorPage);
            request = null;
            localRequestDispatcher.forward(request, response);
         }
      }
      
      if (request.getParameter("actionFlag") != null && request.getParameter("actionFlag").equals("changePassword")) {
          if (sess.getAttribute("csrf_Token").toString().equals(csrfTokenNum)) {
             this.logger.info("CSRF Token present change pass ======");
          } else {
             this.logger.info("NO CSRF Token present for change pass ======");
             RequestDispatcher localRequestDispatcher = request.getRequestDispatcher(this.errorPage);
             request = null;
             localRequestDispatcher.forward(request, response);
          }
       }
      
      RequestDispatcher localRequestDispatcher;
      String check;
      if (request.getParameter("actionFlag") != null && request.getParameter("actionFlag").equals("updateEmpChangedGISData")) {
         check = request.getParameter("check").toString();
         if (sess.getAttribute("csrf_token_gis").toString().equals(check)) {
            this.logger.info("CSRF Token present ======");
         } else {
            this.logger.info("NO CSRF Token present for GIS ======");
            localRequestDispatcher = request.getRequestDispatcher(this.errorPage);
            request = null;
            localRequestDispatcher.forward(request, response);
         }
      }

      if (request.getParameter("actionFlag") != null && request.getParameter("actionFlag").equals("addDDOInfo")) {
         check = request.getParameter("check").toString();
         if (sess.getAttribute("csrf_token_gis").toString().equals(check)) {
            this.logger.info("CSRF Token present ======");
         } else {
            this.logger.info("NO CSRF Token present for ddo information ======");
            localRequestDispatcher = request.getRequestDispatcher(this.errorPage);
            request = null;
            localRequestDispatcher.forward(request, response);
         }
      }

      if (request.getParameter("actionFlag") != null && request.getParameter("actionFlag").equals("updateUidEidDtls")) {
         check = request.getParameter("check").toString();
         if (sess.getAttribute("csrf_token_gis").toString().equals(check)) {
            this.logger.info("CSRF Token present ======");
         } else {
            this.logger.info("NO CSRF Token present for updateUID ======");
            localRequestDispatcher = request.getRequestDispatcher(this.errorPage);
            request = null;
            localRequestDispatcher.forward(request, response);
         }
      }

      if (request.getParameter("actionFlag") != null && request.getParameter("actionFlag").equals("saveOfficeData")) {
         check = request.getParameter("check").toString();
         if (sess.getAttribute("csrf_token_ddo").toString().equals(check)) {
            this.logger.info("CSRF Token present ======");
         } else {
            this.logger.info("NO CSRF Token present for updateUID ======");
            localRequestDispatcher = request.getRequestDispatcher(this.errorPage);
            request = null;
            localRequestDispatcher.forward(request, response);
         }
      }

      if (request.getParameter("actionFlag") != null && request.getParameter("actionFlag").equals("changePayDtlsOfEmp")) {
         check = request.getParameter("check").toString();
         if (sess.getAttribute("csrf_token_gis").toString().equals(check)) {
            this.logger.info("CSRF Token present ======");
         } else {
            this.logger.info("NO CSRF Token present for changePayDtlsOfEmp ======");
            localRequestDispatcher = request.getRequestDispatcher(this.errorPage);
            request = null;
            localRequestDispatcher.forward(request, response);
         }
      }
      if (!csrfTokenNum.equals("") && request.getParameter("actionFlag") != null) {
          if (sess.getAttribute("csrf_Token").toString().equals(csrfTokenNum)) {
             this.logger.info("CSRF Token present>>>>> ======");
          } else {
             this.logger.info("NO CCSRF Token present>>>>> ======");
             localRequestDispatcher = request.getRequestDispatcher(this.errorPage);
             request = null;
             localRequestDispatcher.forward(request, response);
          }
       }
      long csrfTok;
      Integer flag= 0;
      if (request.getParameter("actionFlag") != null && request.getParameter("actionFlag").equals("fillChangeGISComponents")) {
         csrfTok = this.getCsrfToken();
         sess.setAttribute("csrf_token_gis", csrfTok);
         flag=1;
      }

      if (request.getParameter("actionFlag") != null && request.getParameter("actionFlag").equals("updateUidEidDtls")) {
         csrfTok = this.getCsrfToken();
         sess.setAttribute("csrf_token_gis", csrfTok);
         flag=1;
      }

      if (request.getParameter("actionFlag") != null && request.getParameter("actionFlag").equals("getDcpsPayDetails")) {
         csrfTok = this.getCsrfToken();
         sess.setAttribute("csrf_token_gis", csrfTok);
         flag=1;
      }

      if (request.getParameter("actionFlag") != null && request.getParameter("actionFlag").equals("loadDDOInfo")) {
         csrfTok = this.getCsrfToken();
         sess.setAttribute("csrf_token_ddo", csrfTok);
         flag=1;
      }

      if (request.getParameter("actionFlag") != null && request.getParameter("actionFlag").equals("DeptCompMpg")) {
         csrfTok = this.getCsrfToken();
         sess.setAttribute("csrf_Token", csrfTok);
         flag=1;
      }

      if (request.getParameter("actionFlag") != null && request.getParameter("actionFlag").equals("InsertDeptCompMpg")) {
         csrfTok = this.getCsrfToken();
         sess.setAttribute("csrf_Token", csrfTok);
         flag=1;
      }
      if (request.getParameter("actionFlag") != null && request.getParameter("actionFlag").equals("changePassword")) {
          csrfTok = this.getCsrfToken();
          sess.setAttribute("csrf_Token", csrfTok);
          flag=1;
       }
      if (request.getParameter("actionFlag") != null && request.getParameter("actionFlag").equals("validateLogin")) {
          csrfTok = this.getCsrfToken();
          sess.setAttribute("csrf_Token", csrfTok);
          flag=1;
       }
      if (viewName.equals("acl-changePassword")) {
          csrfTok = this.getCsrfToken();
          sess.setAttribute("csrf_Token", csrfTok);
          flag=1;
       }
      
      
      /*if (securityContext != null && flag==0)
      {
    	  csrfTok = this.getCsrfToken();
          sess.setAttribute("csrf_Token", csrfTok);
      }*/
      

      chain.doFilter(request, response);
    }
   //}
   public long getCsrfToken() {
      Random rand = new Random();
      return (long)rand.nextInt();
   }
}
   


/*package com.tcs.sgv.filter.service;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Random;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.tcs.sgv.acl.acegilogin.filter.MetaCharFilter;

public class CSRFProcessingFilter extends MetaCharFilter implements Filter{
	Logger logger = Logger.getLogger(CSRFProcessingFilter.class);
	private final String loginPageName = "/login.jsp";
	private final String projectName = "/";
	private final String springCheck = "j_spring_security_check";
	private String errorPage;

	
	 * Created by : Hardik J Sheth (318097)This filter will intercept each and every request received at the web application, and check if the CSRF token is present or not.If the request is for the first page (login page), the comparision logic is
	 * skipped and new token is generated and added into the session.
	 * 
	 * Modified by Vaibhav Tyagi(547295).
	 

	public void doFilterHttp(HttpServletRequest request, HttpServletResponse response, FilterChain chain) 
	throws IOException, ServletException
	{
		//response.addHeader("csrfToken", "unique");]]
		
		
		HttpSession sess = request.getSession();
		//this.logger.info("UserName is *****"+userName);
		//this.logger.info("Role Id is *****"+roleId);
		String username=(String) sess.getAttribute("userName");
		String str=null;
		String str1=null;
		String csrfTokenNum=null;
		Enumeration localEnumeration = request.getParameterNames();
		while (localEnumeration.hasMoreElements())
		{
			str=(String)localEnumeration.nextElement();
			str1 = request.getParameter(str);
			logger.info("str1 is ::::::"+str1);
			logger.info("str is ::::::"+str);
	
			if(str!= null && !str.equals("") && str.equals("csrfToken")){
				csrfTokenNum = str1;
				logger.info("csrfTokenNum "+csrfTokenNum);
			}	
		}
		
		Enumeration e = (Enumeration) (sess.getAttributeNames());

        while ( e.hasMoreElements())
        {
            Object tring;
            if((tring = e.nextElement())!=null)
            {
            	this.logger.info(sess.getValue((String) tring));
            	this.logger.info(sess.getAttributeNames());
                
                
            }

        }
		
		if(username!=null  &&   request.getParameter("actionFlag")!=null  &&   request.getParameter("csrf_token")==null){
			RequestDispatcher localRequestDispatcher = request.getRequestDispatcher(this.errorPage);
			request=null;
			localRequestDispatcher.forward(request, response);
		}
		
		
		
		//response.addHeader("csrfToken",csrfToken);
		//response.addHeader("csrf_token",csrfToken);
		
		
	
		
		
		//String url = request.getRequestURI();
		//logger.info("url======" + url);
		// Retrive the csrf token from the hidden variable.
		Object obj = request.getParameter("csrf_token");
		logger.info("obj======" + obj);
		String csrfPara=null;
		if(obj!=null){
			logger.info("Login Page Request");
			csrfPara=obj.toString();
		}

	

		Object val = sess.getAttribute("csrf_token");
		
		
     	if(val==null){
			sess.setAttribute("csrfToken", csrfToken);
			sess.setAttribute("csrf_token", csrfToken);
		}
		logger.info("val======" + val);

		// /To retrive the csrf token from the Cookie.
		Cookie[] cok = request.getCookies();
		logger.info("cookies object is======" + cok);

		if (cok != null)
		{
			for (int i = 0; i < cok.length; i++)
			{
				logger.info("Cook Name======" + cok[i].getName());
				if (cok[i].getName().equals("csrf_token"))
				{
					obj = cok[i].getValue();
					break;

				}
				//cok[i].setValue(null);
			}
		}
		
		if(obj!=null){
			logger.info("cookies csrf value======" + obj.toString());
		}
		logger.info("Req Type====" + request.getMethod());
		// Compare the csrf token received from request object with the token present in session. If comparison is successful,
		// generate new token.
		
		logger.info("csrf_Token"+sess.getAttribute("csrf_Token"));
		
		if(csrfTokenNum != null){
			//String checkCsrf= request.getParameter("check").toString();
			if(sess.getAttribute("csrf_Token").toString().equals(csrfTokenNum)){
				logger.info("CSRF Token present ======");
			}
			
			else{
				logger.info("NO CSRF Token present  ======");
				RequestDispatcher localRequestDispatcher = request.getRequestDispatcher(this.errorPage);
				request=null;
				localRequestDispatcher.forward(request, response);
			}
		
	     }
		
		if(request.getParameter("actionFlag")!=null && request.getParameter("actionFlag").equals("updateEmpChangedGISData")){
			String check= request.getParameter("check").toString();
			if(sess.getAttribute("csrf_token_gis").toString().equals(check)){
				logger.info("CSRF Token present ======");
			}
			
			else{
				logger.info("NO CSRF Token present for GIS ======");
				RequestDispatcher localRequestDispatcher = request.getRequestDispatcher(this.errorPage);
				request=null;
				localRequestDispatcher.forward(request, response);
			}
		}
		//CSRF for payroll
		if(request.getParameter("actionFlag")!=null && request.getParameter("actionFlag").equals("addDDOInfo")){
			String check= request.getParameter("check").toString();
			if(sess.getAttribute("csrf_token_gis").toString().equals(check)){
				logger.info("CSRF Token present ======");
			}
			
			else{
				logger.info("NO CSRF Token present for ddo information ======");
				RequestDispatcher localRequestDispatcher = request.getRequestDispatcher(this.errorPage);
				request=null;
				localRequestDispatcher.forward(request, response);
			}
		}
		if(request.getParameter("actionFlag")!=null && request.getParameter("actionFlag").equals("updateUidEidDtls")){
			String check= request.getParameter("check").toString();
			if(sess.getAttribute("csrf_token_gis").toString().equals(check)){
				logger.info("CSRF Token present ======");
			}
			
			else{
				logger.info("NO CSRF Token present for updateUID ======");
				RequestDispatcher localRequestDispatcher = request.getRequestDispatcher(this.errorPage);
				request=null;
				localRequestDispatcher.forward(request, response);
			}
		}
		if(request.getParameter("actionFlag")!=null && request.getParameter("actionFlag").equals("saveOfficeData")){
			String check= request.getParameter("check").toString();
			if(sess.getAttribute("csrf_token_ddo").toString().equals(check)){
				logger.info("CSRF Token present ======");
			}
			
			else{
				logger.info("NO CSRF Token present for updateUID ======");
				RequestDispatcher localRequestDispatcher = request.getRequestDispatcher(this.errorPage);
				request=null;
				localRequestDispatcher.forward(request, response);
			}
		}
		if(request.getParameter("actionFlag")!=null && request.getParameter("actionFlag").equals("changePayDtlsOfEmp")){
			String check= request.getParameter("check").toString();
			if(sess.getAttribute("csrf_token_gis").toString().equals(check)){
				logger.info("CSRF Token present ======");
			}
			
			else{
				logger.info("NO CSRF Token present for changePayDtlsOfEmp ======");
				RequestDispatcher localRequestDispatcher = request.getRequestDispatcher(this.errorPage);
				request=null;
				localRequestDispatcher.forward(request, response);
			}
		}
		
		if ((obj != null && val != null) && Long.parseLong(obj.toString()) == Long.parseLong(val.toString()))
		{
			logger.info("CSRF Token present ======");
			long csrfTok = getCsrfToken();
			logger.info("CSRF Token generated ======"+csrfTok);
			sess.setAttribute("csrf_token", csrfTok);

			response.addHeader("csrf_token", "" + csrfTok);
			Cookie cok1 = new Cookie("csrf_token", "" + csrfTok);
			cok1.setSecure(true);
			//httpResponse.setHttpOnly(true);
			response.addCookie(cok1);

			response.setHeader("SET-COOKIE", "csrf_token=" + csrfTok + ";secureHttpOnly;path=" + request.getContextPath());
		}
		
		else if(csrfPara!=null && springCheck.equals(springCheck) && val==null){
			logger.info("Login after Logout ======");
			logger.info("Add CSRF Token present ======");
			long csrfTok = getCsrfToken();
			logger.info("CSRF Token generated ======"+csrfTok);
			sess.setAttribute("csrf_token", csrfTok);

			response.addHeader("csrf_token", "" + csrfTok);
			Cookie cok1 = new Cookie("csrf_token", "" + csrfTok);
			cok1.setSecure(true);
			//httpResponse.setHttpOnly(true);
			response.addCookie(cok1);

			response.setHeader("SET-COOKIE", "csrf_token=" + csrfTok + ";secureHttpOnly;path=" + request.getContextPath());
		}
		else if ((url.toString().endsWith(projectName)|| url.toString().endsWith(loginPageName)) || (request.getParameter("actionFlag")!=null 
				&& (request.getParameter("actionFlag").equals("getMonths")
						|| request.getParameter("actionFlag").equals("AllowDeductIDFromEmpCompMpg") 
						|| request.getParameter("actionFlag").equals("getHomePage")
						|| request.getParameter("actionFlag").equals("getEmpNameForAutoCompletePayrollSearch"))))
		{
			// If the request for the first page or the login page. Generate a new token.
			logger.info("ADD CSRF Token present ======");

			long csrfTok = getCsrfToken();
			sess.setAttribute("csrf_token", csrfTok);

			response.addHeader("csrf_token", "" + csrfTok);
			
			 * Cookie cok1 = new Cookie("csrf_token", "" + csrfTok); cok1.setSecure(true); cok1.setHttpOnly(true);
			 
			// httpResponse.addCookie(cok1);
			response.setHeader("SET-COOKIE", "csrf_token=" + csrfTok + ";HttpOnly;path=" + request.getContextPath());
		}
		else if (!request.getMethod().equals("GET") && !(url.toString().endsWith(".js") || url.toString().endsWith(".jpg") || url.toString().endsWith(".gif") || url.toString().endsWith(".png") || url.toString().endsWith(".css")))
		{
			// In case no CSRF token is found redirect to access denied page.
			// if (!req.getMethod().equals("GET") && !(url.toString().endsWith(".js") || url.toString().endsWith(".jpg") || url.toString().endsWith(".gif") || url.toString().endsWith(".png")))

			logger.info("NO CSRF Token present ======");
			RequestDispatcher localRequestDispatcher = request.getRequestDispatcher(this.errorPage);
			request=null;
			localRequestDispatcher.forward(request, response);
		}
		
		if(request.getParameter("actionFlag")!=null && request.getParameter("actionFlag").equals("fillChangeGISComponents")){
			long csrfTok = getCsrfToken();
			sess.setAttribute("csrf_token_gis", csrfTok);
		}
		if(request.getParameter("actionFlag")!=null && request.getParameter("actionFlag").equals("updateUidEidDtls")){
			long csrfTok = getCsrfToken();
			sess.setAttribute("csrf_token_gis", csrfTok);
		}
		if(request.getParameter("actionFlag")!=null && request.getParameter("actionFlag").equals("getDcpsPayDetails")){
			long csrfTok = getCsrfToken();
			sess.setAttribute("csrf_token_gis", csrfTok);
		}
		if(request.getParameter("actionFlag")!=null && request.getParameter("actionFlag").equals("loadDDOInfo")){
			long csrfTok = getCsrfToken();
			sess.setAttribute("csrf_token_ddo", csrfTok);
		}
		
		String csrfToken=String.valueOf(getCsrfToken());
		sess.setAttribute("csrf_Token", csrfToken);
		//sess.setAttribute("csrf_Token", "55555555");
		
		
		
		
		chain.doFilter(request, response);

	}

	public long getCsrfToken()
	{
		Random rand = new Random();
		return rand.nextInt();
	}

	
	 * @Override public int getOrder() { // TODO Auto-generated method stub return 0; }
	 
}
*/