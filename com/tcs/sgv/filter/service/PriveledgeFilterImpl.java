package com.tcs.sgv.filter.service;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.Authentication;
import org.springframework.security.SpringSecurityMessageSource;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.providers.dao.cache.EhCacheBasedUserCache;
import org.springframework.security.ui.SpringSecurityFilter;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.tcs.sgv.acl.acegilogin.event.RequestFailureCrossSiteEvent;
import com.tcs.sgv.acl.acegilogin.event.RequestSecuritySuccessEvent;
import com.tcs.sgv.acl.acegilogin.exception.RequestSecurityFaliureException;
import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.filter.dao.PriveledgeFilterDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.ess.valueobject.OrgUserMst;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class PriveledgeFilterImpl extends SpringSecurityFilter
implements ApplicationEventPublisherAware
{

	protected ApplicationEventPublisher eventPublisher;
	protected MessageSourceAccessor messages;
	private CommonsMultipartResolver multipartResolver;
	static final String FILTER_APPLIED = "_meta_char_filter_applied";
	private EhCacheBasedUserCache userCache;
	private String errorPage;
	

	public PriveledgeFilterImpl()
	{		this.messages = SpringSecurityMessageSource.getAccessor();
	}

	public void afterPropertiesSet()
	throws Exception
	{
		Assert.notNull(this.multipartResolver, "multipartResolver must be specified");
	}

	protected void doFilterHttp(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
	throws IOException, ServletException
	{
		HttpSession session = request.getSession();
		String userName= (String) session.getAttribute("userName");
		ServiceLocator serviceLocator = ServiceLocator.getServiceLocator();
		//LoginDetails user = (LoginDetails) session.getAttribute("user");
		
		logger.info("SessionId logout --- " + session.getId());
		long userId =0l;
        SecurityContext securityContext = (SecurityContext)session.getAttribute("SPRING_SECURITY_CONTEXT");
        if (securityContext != null)
        {
        	//Authentication authentication = securityContext.getAuthentication();
             userId = Long.parseLong(session.getAttribute("userId").toString());
           // String locale = session.getAttribute("locale").toString();
        }
		this.logger.info("Priveledge Filter userId**************** "+ userId);
		String str = null;
		PriveledgeFilterDAOImpl dao = new PriveledgeFilterDAOImpl(OrgUserMst.class,serviceLocator.getSessionFactory());
		List lstroleId=null;
		String roleId="";
		try {
			lstroleId = dao.getRoleIdList(userId);
			
			if (this.multipartResolver.isMultipart(request)) {
				MultipartHttpServletRequest localMultipartHttpServletRequest = this.multipartResolver.resolveMultipart(request);
				str = checkForSecurity(localMultipartHttpServletRequest,lstroleId);
				request = localMultipartHttpServletRequest;
			} else {
				str = checkForSecurity(request,lstroleId);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.eventPublisher.publishEvent(new RequestSecuritySuccessEvent(request));
			request.setAttribute("_priveledge_escalation_filter", Boolean.TRUE);
			RequestDispatcher localRequestDispatcher= request.getRequestDispatcher(this.errorPage);
			request = null;
			localRequestDispatcher.forward(request, response);
		}
		
		/*if(!lstroleId.isEmpty()){
		for (int i = 0; i < lstroleId.size(); i++) {*/
			
		
		/*if(userId!=0l){
			try{
				if (this.multipartResolver.isMultipart(request)) {
					MultipartHttpServletRequest localMultipartHttpServletRequest = this.multipartResolver.resolveMultipart(request);
					str = checkForSecurity(localMultipartHttpServletRequest,lstroleId);
					request = localMultipartHttpServletRequest;
				} else {
					str = checkForSecurity(request,lstroleId);
				}
			}

			catch (RequestSecurityFaliureException localRequestSecurityFaliureException)
			{
				logger.info("Inside catch in filter");
				RequestDispatcher localRequestDispatcher = request.getRequestDispatcher(this.errorPage);
				request = null;
				localRequestDispatcher.forward(request, response);
			}
		  }*/
		//}
		//}
		this.logger.info(" Priveledge matches......");

		this.eventPublisher.publishEvent(new RequestSecuritySuccessEvent(request));
		request.setAttribute("_priveledge_escalation_filter", Boolean.TRUE);

		chain.doFilter(request, response);

	}

	private String checkForSecurity(HttpServletRequest paramHttpServletRequest,List roleId)
	throws RequestSecurityFaliureException	  {
		this.logger.info(" Priveledge checkForSecurity......");
		Enumeration localEnumeration = paramHttpServletRequest.getParameterNames();
		ServiceLocator serviceLocator = ServiceLocator.getServiceLocator();
		PriveledgeFilterDAOImpl dao = null;
		String str1 = null;
		String str4=null;
		String str5=null;
		String elementID="";
		while (localEnumeration.hasMoreElements())
		{
			String str2 = (String)localEnumeration.nextElement();
			str1 = paramHttpServletRequest.getParameter(str2);
			this.logger.info(" Priveledge checkForSecurity......"+str1+">>>"+str2+">>>roleId"+roleId);
			if(str2!=null && !str2.equals("") && str2.equals("elementId")){
				elementID=str1;
				dao = new PriveledgeFilterDAOImpl(OrgUserMst.class,serviceLocator.getSessionFactory());
				boolean isValid= dao.checkPriveledgeForElementId(str1,roleId);
				if(isValid){
					while (localEnumeration.hasMoreElements())
					{
						String str3 = (String)localEnumeration.nextElement();
						str4 = paramHttpServletRequest.getParameter(str3);	
						this.logger.info(" Priveledge checkForSecurity......"+str3+">>>"+str4+">>>roleId"+roleId);
						if(str3!=null && !str3.equals("") && str3.equals("actionFlag") && !elementID.equals("")){
							boolean isValidAction= dao.checkPriveledgeForElementId(elementID,str4);
							if(isValidAction){
								
							}
							else{
								this.logger.info("Priveledge Escalation has been action found");
								throw new RequestSecurityFaliureException("Priveledge Escalation has been action found", str4);
							}
						
						}
					}
					return null;
				}
				else{
					
					while (localEnumeration.hasMoreElements())
					{
						String str3 = (String)localEnumeration.nextElement();
						str5 = paramHttpServletRequest.getParameter(str3);	
						this.logger.info(" Priveledge checkForSecurity......"+str3+">>>"+str5+">>>roleId"+roleId);
						if(str3!=null && !str3.equals("") && str3.equals("actionFlag") && !elementID.equals("")){
							boolean isValidAction= dao.checkPriveledgeForElementId(elementID,str5);
							if(isValidAction){
								
							}
							else{
								this.logger.info("Priveledge Escalation has been action found");
								throw new RequestSecurityFaliureException("Priveledge Escalation has been action found", str5);
							}
						
						}
					}
					//this.logger.info("Priveledge Escalation has been found");
					//throw new RequestSecurityFaliureException("Priveledge Escalation has been found", str1);
				}
			}
		}
		return null;
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

	public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher)
	{
		this.eventPublisher = eventPublisher;
	}

	public EhCacheBasedUserCache getUserCache() {
		return this.userCache;
	}

	public void setUserCache(EhCacheBasedUserCache userCache) {
		this.userCache = userCache;
	}

	public void setErrorPage(String paramString)
	{
		if ((paramString != null) && (!(paramString.startsWith("/")))) {
			throw new IllegalArgumentException("errorPage must begin with '/'");
		}

		this.errorPage = paramString;
	}

	public int getOrder() {
		return 0;
	}
}
