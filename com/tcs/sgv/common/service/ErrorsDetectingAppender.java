package com.tcs.sgv.common.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;


public class ErrorsDetectingAppender extends AppenderSkeleton {

	private final Log gLogger = LogFactory.getLog(getClass());
	private static boolean errorsOccured = false;     
	public static boolean errorsOccured() {       
		return errorsOccured;  
	}      
	public ErrorsDetectingAppender() {        
		super();     
	}      
	public void close() {             
		System.out.println("------VD - Closed Called");
	}     
	public boolean requiresLayout() {         
		System.out.println("------VD - requiresLayout Called");
		return false;     
	}     
	protected void append(LoggingEvent event) {        
		if (event.getLevel().toString().toLowerCase().equals("error")) {       
			System.out.println("------VD - Errors detected");
			this.errorsOccured = true;        
			
			String categoryName = event.categoryName; //Deprecated
			String level = String.valueOf(event.level); //Deprecated
			
			String fullInfo = event.getLocationInformation().fullInfo;
			String methodName = event.getLocationInformation().getMethodName();
			String className = event.getLocationInformation().getClassName();
			String fileName = event.getLocationInformation().getFileName();
			String lineNumber = event.getLocationInformation().getLineNumber();
			
			String message = event.getRenderedMessage();
			String startTime = String.valueOf(event.getStartTime());
			String timeStamp = String.valueOf(event.timeStamp);
			
			String threadInvoker = event.getThreadName();
			
			
			gLogger.info("-----------categoryName " + categoryName);
			gLogger.info("-----------level " + level);
			gLogger.info("-----------fullInfo " + fullInfo);
			gLogger.info("-----------methodName " + methodName);
			gLogger.info("-----------className " + className);
			gLogger.info("-----------fileName " + fileName);
			gLogger.info("-----------lineNumber " + lineNumber);
			gLogger.info("-----------message " + message);
			gLogger.info("-----------startTime " + startTime);
			gLogger.info("-----------timeStamp " + timeStamp);
			gLogger.info("-----------threadInvoker " + threadInvoker);
			
			
			
			
			
		}    
	} 
}
