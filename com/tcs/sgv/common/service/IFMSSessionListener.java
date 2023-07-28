package com.tcs.sgv.common.service;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IFMSSessionListener implements HttpSessionListener{
	
	private Log logger = LogFactory.getLog(getClass()); /* LOGGER */
	
	public void sessionCreated(HttpSessionEvent sessionEvent) {}
	
	
	public void sessionDestroyed(HttpSessionEvent sessionEvent) {
		
		try{
			
			
		}catch (Exception ex) {
			logger.error("Error while executing sessionDestroyed...");
			logger.error(ex);
		}
		
	}

}
