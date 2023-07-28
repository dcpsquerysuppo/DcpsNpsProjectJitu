package com.tcs.sgv.filter.service;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.Session;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.SpringSecurityMessageSource;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.tcs.sgv.acl.acegilogin.event.RequestFailureCrossSiteEvent;
import com.tcs.sgv.acl.acegilogin.event.RequestSecuritySuccessEvent;
import com.tcs.sgv.acl.acegilogin.exception.RequestSecurityFaliureException;
import com.tcs.sgv.acl.acegilogin.filter.MetaCharFilter;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.ess.valueobject.OrgUserMst;
import com.tcs.sgv.filter.dao.PriveledgeFilterDAOImpl;




public class XSSFilterImpl  extends MetaCharFilter implements Filter{


	protected ApplicationEventPublisher eventPublisher;
	protected MessageSourceAccessor messages;
	private CommonsMultipartResolver multipartResolver;
	private static ResourceBundle skipParamResourceBundle = null;
	private String errorPage;
	static final String FILTER_APPLIED = "_meta_char_filter_applied";

	public XSSFilterImpl()
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

		/*logger.info("Inside XSSFilter");
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Meta character filter checking request for cross site script");
		}*/

		/*if (paramHttpServletRequest.getAttribute("_meta_char_filter_applied") != null)
		{
			paramFilterChain.doFilter(paramHttpServletRequest, paramHttpServletResponse);

			return;
		}*/

		String str = null;
		try {
			if (this.multipartResolver.isMultipart(paramHttpServletRequest)) {
				MultipartHttpServletRequest localMultipartHttpServletRequest = this.multipartResolver.resolveMultipart(paramHttpServletRequest);
				str = checkForSecurity(localMultipartHttpServletRequest);
				paramHttpServletRequest = localMultipartHttpServletRequest;
			} else {
				str = checkForSecurity(paramHttpServletRequest);
			}

		}
		catch (RequestSecurityFaliureException localRequestSecurityFaliureException)
		{
			/*logger.info("Inside catch in filter");
			if (this.logger.isDebugEnabled())
				this.logger.debug("Meta character filter found cross site script in " + localRequestSecurityFaliureException.getExtraInformation() + " Parameter");
*/
			if (this.eventPublisher != null)
			{
				this.eventPublisher.publishEvent(new RequestFailureCrossSiteEvent(paramHttpServletRequest, localRequestSecurityFaliureException));
			}
			
			RequestDispatcher localRequestDispatcher = paramHttpServletRequest.getRequestDispatcher(this.errorPage);
			paramHttpServletRequest = null;
			localRequestDispatcher.forward(paramHttpServletRequest, paramHttpServletResponse);
		}
		/*if (this.logger.isDebugEnabled())
			this.logger.debug("Meta character filter not found cross site script in http request");
*/
		//added for priveledge escalation:start
		if(paramHttpServletRequest.getParameter("j_username") != null && !paramHttpServletRequest.getParameter("j_username").toString().equals("")){
			ServiceLocator serviceLocator = ServiceLocator.getServiceLocator();
			String userName= paramHttpServletRequest.getParameter("j_username");

			PriveledgeFilterDAOImpl dao = new PriveledgeFilterDAOImpl(OrgUserMst.class,serviceLocator.getSessionFactory());
			List roleForUser= dao.getRoleListForUser(userName);
			String roleId= null;
			for(int i=0 ; i<roleForUser.size();i++){
				if(roleId!=null){
					roleId=roleId+","+roleForUser.get(i).toString();
				}
				else{
					roleId=roleForUser.get(i).toString();
				}
			}
			HttpSession session = paramHttpServletRequest.getSession();
			//this.logger.info("UserName is *****"+userName);
			//this.logger.info("Role Id is *****"+roleId);
			session.setAttribute("userName", userName);
			session.setAttribute("roleId", roleId);
		}
		//added for priveledge escalation:end

		this.eventPublisher.publishEvent(new RequestSecuritySuccessEvent(paramHttpServletRequest));

		//paramHttpServletRequest.setAttribute("_meta_char_filter_applied", Boolean.TRUE);

		
		paramHttpServletResponse.addHeader("Strict-Transport-Security","max-age=31536000 ; includeSubDomains");
		paramHttpServletResponse.addHeader("X-Content-Type-Options", "nosniff");
		paramHttpServletRequest.setAttribute("_meta_char_filter_applied", Boolean.TRUE);
		paramHttpServletResponse.addHeader("X-Frame-Options", "DENY");
		paramHttpServletResponse.addHeader("X-XSS-Protection", "1; mode=block");
	    paramHttpServletResponse.addHeader("Referrer-Policy", "no-referrer");
	    paramHttpServletResponse.addHeader("Permissions-Policy", "FEATURE ORIGIN");
	    paramHttpServletResponse.addHeader("Cache-Control", "no-cache, no-store");
	    paramHttpServletResponse.addHeader("Pragma", "no-cache");
	
	        
	        //https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css
	        
	   // paramHttpServletResponse.setHeader("Content-Security-Policy", " connect-src 'self' font-src 'self'  img-src 'self'  default-src 'self' style-src 'self' *.https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css  'self' *.https://unpkg.com/sweetalert/dist/sweetalert.min.js 'unsafe-inline' *.http://code.jquery.com/ui/1.10.4/jquery-ui.js 'unsafe-inline' *.http://100.70.120.236:8080/ifmsmaha/script/NewLogin/js/jquery.js 'unsafe-inline' *.http://100.70.120.236:8080/ifmsmaha/script/NewLogin/js/jquery.js 'unsafe-inline' *.http://code.jquery.com/jquery-1.10.2.js 'unsafe-inline' *.http://100.70.120.236:8080/ifmsmaha/script/NewLogin/js/jquery-1.5.min.js 'unsafe-inline' *.http://100.70.120.236:8080/ifmsmaha/script/common/prototype.js 'unsafe-inline' *.http://100.70.120.236:8080/ifmsmaha/script/common/prototype.js");
	  //  paramHttpServletResponse.setHeader("Content-Security-Policy", " connect-src 'self' font-src 'self'  img-src 'self'  default-src 'self' style-src 'none' script-src 'none' object-src 'none' frame-src 'none' base-uri 'none' *.https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css  'unsafe-inline' *.https://unpkg.com/sweetalert/dist/sweetalert.min.js ");
	    paramHttpServletResponse.setHeader("Content-Security-Policy", " connect-src 'self' font-src 'self'  img-src 'self'  default-src 'self' style-src 'self' script-src 'self' object-src 'none' frame-src 'none' base-uri 'none' *.https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css  'unsafe-inline' *.https://unpkg.com/sweetalert/dist/sweetalert.min.js 'unsafe-inline' *.http://code.jquery.com/ui/1.10.4/jquery-ui.js");
		                                                                                                                  
		paramFilterChain.doFilter(paramHttpServletRequest, paramHttpServletResponse);
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
	

	private String checkForSecurity(HttpServletRequest paramHttpServletRequest)
	throws RequestSecurityFaliureException	  {

		logger.info("Inside our filter");
		String actionFlag=null;
		String actionName=null;
		String action=null;
		Enumeration localEnumeration = paramHttpServletRequest.getParameterNames();
		String str1 = null;
		while (localEnumeration.hasMoreElements())
		{
			String str2 = (String)localEnumeration.nextElement();
			str1 = paramHttpServletRequest.getParameter(str2);
			//logger.info("str1 is ::::::"+str1);
			//logger.info("str2 is ::::::"+str2);

			if(str2!=null && (str2.equals("actionFlag"))){
				actionFlag=str1;
			}

			if(str2!=null && (str2.equals("action"))){
				action=str1;
			}

			/*if(str2!=null && str2.substring(0, 2).equals("d-")){
				actionName=str2;
			}*/
			logger.info("Inside our filter str2"+str2+">>>>>>str1>>>"+str1);
			if(str1!=null && (str1.contains("<")|| str1.contains(">") || str1.contains("alert"))){
				///logger.info("paramHttpServletRequest1 "+paramHttpServletRequest.getParameter(str2));
				throw new RequestSecurityFaliureException("Cross site script has been found", str1);
			}

			else if(str2!=null && (str2.contains("<")|| str2.contains(">") || str1.contains("alert"))){
				paramHttpServletRequest.setAttribute(str2, null);

				//logger.info("paramHttpServletRequest2 "+paramHttpServletRequest.getParameter(str2));

				throw new RequestSecurityFaliureException("Cross site script has been found", str2);
			}

		}
		
		  
/*
		//added for CSRF:start
		logger.info("Trojan Horse Value is*******"+paramHttpServletRequest.getParameter("trojanHorse"));
		logger.info("Ajax Call Value is*******"+paramHttpServletRequest.getHeader("X-Requested-With"));
		String trojanHorse=paramHttpServletRequest.getParameter("trojanHorse");
		String ssasmap= paramHttpServletRequest.getParameter("ssasmap");
		String contentType=null;

		if(paramHttpServletRequest.getHeader("Content-Type")!=null)
			contentType=paramHttpServletRequest.getHeader("Content-Type");

		else if(paramHttpServletRequest.getHeader("Content-type")!=null)
			contentType=paramHttpServletRequest.getHeader("Content-type");
		
		logger.info("Content Type*******"+paramHttpServletRequest.getContentType());


	if(ssasmap!=null && !ssasmap.equals("")){
		trojanHorse=null;
	}
	String ajaxCall=paramHttpServletRequest.getHeader("X-Requested-With");

	logger.info("ajaxCall "+ajaxCall+" request is "+paramHttpServletRequest);
	String prvtrojanHorse=null;
	String prvtrojanHorse1=null;
	String prvtrojanHorse2=null;
	String prvtrojanHorse3=null;
	String prvtrojanHorse4=null;
	List<String> tokenNumbers =null;


	if(paramHttpServletRequest.getSession().getAttribute("trojanHorse")!=null){
		prvtrojanHorse= paramHttpServletRequest.getSession().getAttribute("trojanHorse").toString();			
	}

	if(paramHttpServletRequest.getSession().getAttribute("prvtrojanHorse")!=null){
		prvtrojanHorse1=paramHttpServletRequest.getSession().getAttribute("prvtrojanHorse").toString();
	}

	if(paramHttpServletRequest.getSession().getAttribute("prvtrojanHorse1")!=null){
		prvtrojanHorse2=paramHttpServletRequest.getSession().getAttribute("prvtrojanHorse1").toString();
	}

	if(paramHttpServletRequest.getSession().getAttribute("prvtrojanHorse2")!=null){
		prvtrojanHorse3=paramHttpServletRequest.getSession().getAttribute("prvtrojanHorse2").toString();
	}

	if(paramHttpServletRequest.getSession().getAttribute("prvtrojanHorse3")!=null){
		prvtrojanHorse4=paramHttpServletRequest.getSession().getAttribute("prvtrojanHorse3").toString();
	}

	if(paramHttpServletRequest.getSession().getAttribute("trojanHorse")!=null){
		prvtrojanHorse= paramHttpServletRequest.getSession().getAttribute("trojanHorse").toString();
	}

	if(trojanHorse==null &&	actionFlag!=null && !actionFlag.equals("validateLogin") 
			&& !actionFlag.equals("getHomePage")
			&& (actionFlag.equals("insertAllwDdct") || actionFlag.equals("AllowDeductIDFromEmpCompMpg")
					|| actionFlag.equals("insertBulkAllw") || actionFlag.equals("getIncPrintReport")
					|| actionFlag.equals("getNonGovDeductionMaster") || actionFlag.equals("popBrnchNms")
					|| actionFlag.equals("showTokenNumber") || actionFlag.equals("reportService")
					|| actionFlag.equals("getOuterData") || actionFlag.equals("getGisData")
					|| actionFlag.equals("generatePayslip"))){
		if(ssasmap!=null){
			trojanHorse=ssasmap;
		}
	}

	if(trojanHorse==null &&	actionFlag!=null && !actionFlag.equals("validateLogin") 
			&& !actionFlag.equals("getHomePage") && !actionFlag.equals("popBrnchNms") && !actionFlag.equals("popTalukaNms") && !actionFlag.equals("popDistNms") && (paramHttpServletRequest.getParameter("elementId")!=null 
					|| contentType.equals("application/x-www-form-urlencoded")
					|| contentType.equals("text/html; charset=iso-8859-1")
					||(ajaxCall!=null && !ajaxCall.equals("") && ajaxCall.equals("XMLHttpRequest")))){
		if(ssasmap!=null){
			trojanHorse=ssasmap;
		}
	}

	logger.info("Action Flag is*******"+actionFlag);

	logger.info("trojanHorse final is*******"+trojanHorse);

	if(paramHttpServletRequest.getParameter("j_username")!=null && !paramHttpServletRequest.getParameter("j_username").equals("")){
		logger.info("Trying to Login");
		return null;
	}

	else if(ajaxCall!=null && !ajaxCall.equals("") && ajaxCall.equals("XMLHttpRequest")){
			logger.info("Ajax Call");
			paramHttpServletRequest.getSession().setAttribute("trojanHorse", "");
			return null;
		}

	else if(actionName!= null && actionName.substring(0,2).equals("d-") && actionName.substring(actionName.length()-2).equals("-p")){
		logger.info("Pagination");
		paramHttpServletRequest.getSession().setAttribute("trojanHorse", "");
		return null;
	}

	else if(action!= null && action.equals("gotoPage")){
		logger.info("Report Pagination");
		paramHttpServletRequest.getSession().setAttribute("trojanHorse", "");
		return null;
	}

	else{
		if(actionFlag!=null && !actionFlag.equals("validateLogin") && !actionFlag.equals("getHomePage")
				&& !actionFlag.equals("insertUpdateQtrDtls") && !actionFlag.equals("getEmpDataForMapping") 
				&& !actionFlag.equals("payrollSearchEmployeeByAll") && !actionFlag.equals("getEmpDataForEndDate")
				&& !actionFlag.equals("fillLoanCombo") && !actionFlag.equals("multipleLoanData")
				&& !actionFlag.equals("multipleAddLoan") && !actionFlag.equals("getLoanValue")
				&& !actionFlag.equals("insertEmpLoanDtls") && !actionFlag.equals("getQuarterDtls")
				&& !actionFlag.equals("getEmpNameForAutoCompletePayrollSearch")
				&& !actionFlag.equals("getEmpNameForAutoCompleteDCPS") && !actionFlag.equals("popBrnchNms")
				&& !actionFlag.equals("popTalukaNms") && !actionFlag.equals("popDistNms")){

			if(prvtrojanHorse!=null){
				logger.info("Ajax Call 1");
				if(trojanHorse.equals(prvtrojanHorse)){
					logger.info("Ajax Call 2");
					throw new RequestSecurityFaliureException("CSRF has been found", "Same Token Number as before");
				}
			}

			if(prvtrojanHorse1!=null){
				logger.info("Ajax Call 1.1");
				if(trojanHorse.equals(prvtrojanHorse1)){
					logger.info("Ajax Call 2.1");
					throw new RequestSecurityFaliureException("CSRF has been found", "Same Token Number as before");
				}
			}

			if(prvtrojanHorse2!=null){
				logger.info("Ajax Call 1.2");
				if(trojanHorse.equals(prvtrojanHorse2)){
					logger.info("Ajax Call 2.2");
					throw new RequestSecurityFaliureException("CSRF has been found", "Same Token Number as before");
				}
			}

			if(prvtrojanHorse3!=null){
				logger.info("Ajax Call 1.3");
				if(trojanHorse.equals(prvtrojanHorse3)){
					logger.info("Ajax Call 2.3");
					throw new RequestSecurityFaliureException("CSRF has been found", "Same Token Number as before");
				}
			}

			if(prvtrojanHorse4!=null){
				logger.info("Ajax Call 1.4");
				if(trojanHorse.equals(prvtrojanHorse4)){
					logger.info("Ajax Call 2.4");
					throw new RequestSecurityFaliureException("CSRF has been found", "Same Token Number as before");
				}
			}


			tokenNumbers = TokenNumberList.tokenNumberList;

			if(tokenNumbers!=null && tokenNumbers.size()>0){
				if(tokenNumbers.size()>=100){
					TokenNumberList.tokenNumberList=null;
				}

				else{
					for(int i=0; i<tokenNumbers.size();i++){
						if(trojanHorse.equals(tokenNumbers.get(i).toString())){
							logger.info("Static List 1.5");
							logger.info("Static List contains token number");
							throw new RequestSecurityFaliureException("CSRF has been found", "Same Token Number as before");
						}
					}
				}
			}

			String specialChar="((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{2,65})";

			logger.info("Ajax Call 3");
			Pattern pattern=Pattern.compile(specialChar);
			Matcher matcher=pattern.matcher(trojanHorse);

			logger.info("Ajax Call 4");
			if(!matcher.find()){
				logger.info("Ajax Call 5");
				throw new RequestSecurityFaliureException("CSRF has been found", "Change Token Number");
			}

			logger.info("Last Three Digits are:"+trojanHorse.substring(trojanHorse.length()-3));
			if(checkOnlyNumber(trojanHorse.substring(trojanHorse.length()-3))){
				int lastDigits= Integer.parseInt(trojanHorse.substring(trojanHorse.length()-3));

				if(lastDigits%8!=0){
					throw new RequestSecurityFaliureException("CSRF has been found", "Change Last Three Digits Number");
				}
			}

			else{
				throw new RequestSecurityFaliureException("CSRF has been found", "Change Last Three Digits Number");
			}
		}

		paramHttpServletRequest.getSession().setAttribute("trojanHorse", trojanHorse);

		paramHttpServletRequest.getSession().setAttribute("prvtrojanHorse", prvtrojanHorse);

		paramHttpServletRequest.getSession().setAttribute("prvtrojanHorse1", prvtrojanHorse1);

		paramHttpServletRequest.getSession().setAttribute("prvtrojanHorse2", prvtrojanHorse2);

		paramHttpServletRequest.getSession().setAttribute("prvtrojanHorse3", prvtrojanHorse3);

		if(trojanHorse!=null && !trojanHorse.equals("")){
			TokenNumberList.tokenNumberList.add(trojanHorse);
			logger.info("Token List Size is::"+TokenNumberList.tokenNumberList.size());
		}

	}*/
	return null;
}
	
/**/
private boolean checkOnlyNumber(String lastThreeDigits){
	boolean isNo=false;
	try{
		int check= Integer.parseInt(lastThreeDigits);
		isNo=true;
	}
	catch(Exception e){
		//logger.info("Error is :::"+e.getMessage());
		isNo=false;
	}
	return isNo;
}

	
public void setErrorPage(String paramString)
{
	if ((paramString != null) && (!(paramString.startsWith("/")))) {
		throw new IllegalArgumentException("errorPage must begin with '/'");
	}

	this.errorPage = paramString;
}


}
