package com.tcs.sgv.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.SpringSecurityMessageSource;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;



import com.ibm.db2.jcc.am.ne;
import com.tcs.sgv.acl.acegilogin.event.RequestFailureCrossSiteEvent;
import com.tcs.sgv.acl.acegilogin.event.RequestSecuritySuccessEvent;
import com.tcs.sgv.acl.acegilogin.exception.RequestSecurityFaliureException;
import com.tcs.sgv.acl.acegilogin.filter.MetaCharFilter;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.ess.valueobject.OrgUserMst;
import com.tcs.sgv.filter.dao.PriveledgeFilterDAOImpl;




public class EOfficeFilterImpl  extends MetaCharFilter implements Filter{


	protected ApplicationEventPublisher eventPublisher;
	protected MessageSourceAccessor messages;
	private CommonsMultipartResolver multipartResolver;
	private static ResourceBundle skipParamResourceBundle = null;
	private String errorPage;
	private String errorPageEoffice;
	private String loginFilter;
	static final String FILTER_APPLIED = "_xssfilter";

	public EOfficeFilterImpl()
	{
		this.messages = SpringSecurityMessageSource.getAccessor();
	}

	public void afterPropertiesSet()
	throws Exception
	{
		Assert.notNull(this.multipartResolver, "multipartResolver must be specified");
	}

	protected void doFilterHttp(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse, FilterChain paramFilterChain)
	throws IOException, ServletException
	{

		Map paramMap = null;
		Map attriMap = null;
		logger.info("Inside XSSFilter");
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Meta character filter checking request for cross site script");
		}
		Enumeration paramEnum = paramHttpServletRequest.getParameterNames();
		while(paramEnum.hasMoreElements()){
			paramMap = new HashMap();
			Object obj = paramEnum.nextElement();
			logger.info("Inside catch in filter"+obj.toString());
			logger.info("Inside catch in filter"+paramHttpServletRequest.getParameter(obj.toString()));
			paramMap.put(obj, paramHttpServletRequest.getParameter(obj.toString()));
		}
		
		//org.springframework.web.context.request.RequestContextListener = null;
		String str = null;
		/*try {
			logger.info("username is "+paramHttpServletRequest.getParameter("userName"));
			logger.info("username is "+paramHttpServletRequest.getParameter("j_username"));
			logger.info("password is "+paramHttpServletRequest.getParameter("j_password"));

			if(paramHttpServletRequest.getParameter("j_username") != null && !paramHttpServletRequest.getParameter("j_username").toString().equals("")){
				ServiceLocator serviceLocator = ServiceLocator.getServiceLocator();
				String userName= paramHttpServletRequest.getParameter("j_username");

				PriveledgeFilterDAOImpl dao = new PriveledgeFilterDAOImpl(OrgUserMst.class,serviceLocator.getSessionFactory());
				String password= dao.getPwdForUser(userName);
				HttpSession session = paramHttpServletRequest.getSession();
				this.logger.info("UserName is *****"+userName);
				this.logger.info("password is *****"+password);
				//session.setAttribute("userName", userName);
				paramHttpServletRequest.setAttribute("Password", password);

				paramFilterChain.doFilter(paramHttpServletRequest, paramHttpServletResponse);
			}

		}
		catch (RequestSecurityFaliureException localRequestSecurityFaliureException)
		{
			logger.info("Inside catch in filter");
			if (this.logger.isDebugEnabled())
				this.logger.debug("Meta character filter found cross site script in " + localRequestSecurityFaliureException.getExtraInformation() + " Parameter");

			if (this.eventPublisher != null)
			{
				this.eventPublisher.publishEvent(new RequestFailureCrossSiteEvent(paramHttpServletRequest, localRequestSecurityFaliureException));
			}
			paramHttpServletRequest = null;
			RequestDispatcher localRequestDispatcher = paramHttpServletRequest.getRequestDispatcher(this.errorPage);
			localRequestDispatcher.forward(paramHttpServletRequest, paramHttpServletResponse);
		}*/


		BASE64Decoder decoder = new BASE64Decoder();
		BASE64Encoder encoder = new BASE64Encoder();
		String statusofLogin=null;
		String userName=null;
		String userId=null;
		String salt=null;
		String date=null;
		String password=null;
		ServiceLocator serviceLocator = ServiceLocator.getServiceLocator();
		try {
			
			if(paramHttpServletRequest.getParameter("userName")!=null && paramHttpServletRequest.getParameter("userName")!=null && !paramHttpServletRequest.getParameter("userName").equals("") ){
				logger.info("username is "+paramHttpServletRequest.getParameter("userName"));
				String encodeUserName=paramHttpServletRequest.getParameter("userName");
				//String encodeSalt=paramHttpServletRequest.getParameter("salt");
				//String encodedBytes = encoder.encodeBuffer("gayathri#sunitha#DATMHSF8201".getBytes());
				//logger.info("encodedBytes " + encodedBytes);
				byte[] decodedBytes = decoder.decodeBuffer(encodeUserName);
				logger.info("decodedBytes " + new String(decodedBytes));
				logger.info("username is "+new String(decodedBytes));
				String decodeUserName=new String(decodedBytes);
				String[] SplitdecodeUserName=decodeUserName.split("#");
				userName=SplitdecodeUserName[2];
				userId=SplitdecodeUserName[0];
				date=SplitdecodeUserName[1];
				logger.info("userName after split " + userName);
				logger.info("userId after split " + userId);
				//byte[] saltdecodedBytes = decoder.decodeBuffer(encodeSalt);
				//logger.info("saltdecodedBytes " + new String(saltdecodedBytes));
				//logger.info("salt is "+new String(saltdecodedBytes));
				salt=SplitdecodeUserName[3];
				
				
				/*RequestWrapper wrappedRequest  = new RequestWrapper(paramHttpServletRequest,"ESTBVNM6701","ifms123",paramMap);
				
				wrappedRequest.setParameter("j_username", "ESTBVNM6701", paramHttpServletRequest.getParameterMap());
				wrappedRequest.setParameter("j_password", "ifms123", paramHttpServletRequest.getParameterMap());
				paramHttpServletRequest =wrappedRequest;
				Enumeration attributeNames = paramHttpServletRequest.getAttributeNames();*/
				
				
				RequestWrapper wrappedRequest  = new RequestWrapper(paramHttpServletRequest,"ESTBVNM6701","ifms123",paramMap);
				wrappedRequest.setParameter("j_username", "ESTBVNM6701");
				wrappedRequest.setParameter("j_password", "ifms123");
				wrappedRequest.setParameter("locale", "en_US");
				//paramHttpServletRequest =wrappedRequest;
				Enumeration attributeNames = paramHttpServletRequest.getParameterNames();
				
				Enumeration paramEnum1 = wrappedRequest.getParameterNames();
				while(paramEnum1.hasMoreElements()){
					
					Object obj = paramEnum1.nextElement();
					logger.info("Inside catch in filter"+obj.toString());
					logger.info("Inside catch in filter"+paramHttpServletRequest.getParameter(obj.toString()));
					
				}
				
				/*while(attributeNames.hasMoreElements()){
					
					Object obj = attributeNames.nextElement();
					logger.info("Inside catch in filter attributeNames "+obj.toString());
					logger.info("Inside catch in filter attributeNames "+paramHttpServletRequest.getAttribute(obj.toString()));
					wrappedRequest.setAttribute(obj.toString(), paramHttpServletRequest.getAttribute(obj.toString()));
				}*/
				
				/*RequestDispatcher localRequestDispatcher =  wrappedRequest.getRequestDispatcher(this.loginFilter);
				localRequestDispatcher.forward(wrappedRequest, paramHttpServletResponse);*/
				
				paramHttpServletRequest.setAttribute("_xssfilter", Boolean.TRUE);
				paramHttpServletRequest.setAttribute("_robo_request_filter_applied", Boolean.TRUE);
				//paramHttpServletResponse.addHeader("X-FRAME-OPTIONS", "DENY" );

				paramFilterChain.doFilter(wrappedRequest, paramHttpServletResponse);
				
				/*String url = "http://103.23.150.19/user_status_service?rquest=getUserTypeList";
				String charset = "UTF-8";
				String param1 = userId;
				String param2 = salt;
				// ...
				String query = null;
				try {
					query = String.format("userid=%s&salt=%s", 
							URLEncoder.encode(param1, charset), 
							URLEncoder.encode(param2, charset));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				URLConnection connection = null;
				try {
					System.getProperties().put("http.proxyHost", "proxy.tcs.com");
					System.getProperties().put("http.proxyPort", "8080"); 
					System.getProperties().put("http.proxyUser", "474792");
					System.getProperties().put("http.proxyPassword", "Gayathri@28"); 
					connection = new URL(url).openConnection();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				connection.setDoOutput(true); // Triggers POST.
				connection.setRequestProperty("Accept-Charset", charset);
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
				//connection.setConnectTimeout(1000);
				OutputStream output = null;
				try {
					output = connection.getOutputStream();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					System.out.println(" query "+query+" output  "+output.toString());
					output.write(query.getBytes(charset));
				} finally {
					try { 
						output.close(); 
					} 
					catch (IOException logOrIgnore) {}
				}
				StringBuilder sb = null;   
				String line = null; 
				BufferedReader  rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));   
				sb = new StringBuilder();   

				while ((line = rd.readLine()) != null)   
				{   
					sb.append(line + '\n');   
				}   

				System.out.println("response "+sb.toString()); 
				String status=sb.toString();
				logger.info("status filter"+status);
				String[] splitStatus=status.split(":");

				statusofLogin=splitStatus[2];
				logger.info("statusofLogin...."+statusofLogin.charAt(1));
				logger.info("statusofLogin filter"+statusofLogin);*/
				//paramHttpServletRequest.setAttribute("password", password);
				//paramHttpServletRequest.setAttribute("userName", userName.trim());

			}
			else {
				
				this.eventPublisher.publishEvent(new RequestSecuritySuccessEvent(paramHttpServletRequest));

				paramHttpServletRequest.setAttribute("_xssfilter", Boolean.TRUE);
				paramHttpServletResponse.addHeader("X-FRAME-OPTIONS", "DENY" );

				paramFilterChain.doFilter(paramHttpServletRequest, paramHttpServletResponse);
				
			}
		} catch (IOException e) {
			logger.info("Inside catch in filter");
			String status="502 ERROR";
			PriveledgeFilterDAOImpl dao = new PriveledgeFilterDAOImpl(OrgUserMst.class,serviceLocator.getSessionFactory());
			//dao.inserteofficeDetails(userName,userId,salt,status,date);
			//paramHttpServletRequest = null;
			//paramHttpServletRequest.setAttribute("j_password", "ifms123");
			//paramHttpServletRequest.setAttribute("j_username", "ESTBVNM5701_AST");		
		}
		/*if(userName!=null){
		if(statusofLogin !=null  && statusofLogin.length()!=0 ){
			if (statusofLogin.charAt(1)=='T'){
				logger.info("salt after split " + salt);
				PriveledgeFilterDAOImpl dao = new PriveledgeFilterDAOImpl(OrgUserMst.class,serviceLocator.getSessionFactory());
				password= dao.getPwdForUser(userName.trim());
				String status="true";
				//dao.inserteofficeDetails(userName,userId,salt,status,date);
				logger.info("password--- " + password);
				HttpSession session = paramHttpServletRequest.getSession();
				session.setAttribute("password", password);
				session.setAttribute("userName", userName.trim());
				session.setAttribute("salt", salt.trim());
				this.eventPublisher.publishEvent(new RequestSecuritySuccessEvent(paramHttpServletRequest));

				paramHttpServletRequest.setAttribute("_xssfilter", Boolean.TRUE);
				paramHttpServletResponse.addHeader("X-FRAME-OPTIONS", "DENY" );

				paramFilterChain.doFilter(paramHttpServletRequest, paramHttpServletResponse);
			}
			else {
				logger.info("status is false");
				String status="false";
				PriveledgeFilterDAOImpl dao = new PriveledgeFilterDAOImpl(OrgUserMst.class,serviceLocator.getSessionFactory());
				//dao.inserteofficeDetails(userName,userId,salt,status,date);
				RequestDispatcher localRequestDispatcher =  paramHttpServletRequest.getRequestDispatcher(this.errorPage);
				localRequestDispatcher.forward(paramHttpServletRequest, paramHttpServletResponse);
			}
		}
		}*/
		
	}


	public CommonsMultipartResolver getMultipartResolver()
	{
		return this.multipartResolver;
	}

	public void setMultipartResolver(CommonsMultipartResolver paramCommonsMultipartResolver)
	{
		this.multipartResolver = paramCommonsMultipartResolver; }

	public void setMessageSource(MessageSource paramMessageSource) {
		this.messages = new MessageSourceAccessor(paramMessageSource);
	}

	public void setApplicationEventPublisher(ApplicationEventPublisher paramApplicationEventPublisher)
	{
		this.eventPublisher = paramApplicationEventPublisher;
	}

	public void setErrorPage(String paramString)
	{
		if ((paramString != null) && (!(paramString.startsWith("/")))) {
			throw new IllegalArgumentException("errorPage must begin with '/'");
		}

		this.errorPage = paramString;
	}

	public void setErrorPageEoffice(String errorPageEoffice) {
		if ((errorPageEoffice != null) && (!(errorPageEoffice.startsWith("/")))) {
			throw new IllegalArgumentException("errorPage must begin with '/'");
		}

		this.errorPageEoffice = errorPageEoffice;
	}

	public String getErrorPageEoffice() {
		return errorPageEoffice;
	}

	public String getLoginFilter() {
		return loginFilter;
	}

	public void setLoginFilter(String loginFilter) {
		this.loginFilter = loginFilter;
	}

	/*class XSSFoundException extends Exception{
		  XSSFoundException()
			{
				super();
				logger.error("XSS Found");

			}
	  }*/
}
