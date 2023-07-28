package com.tcs.sgv.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.hibernate.stat.Statistics;

import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.utils.Utility;

public class CacheStatisticsWriter implements Runnable {
	
	private static Log logger = LogFactory.getLog(Utility.class);
	
    private static Logger statLogger = Logger.getLogger("cacheStatistics.log");

    private static FileHandler fh = null;
    
    private static ResourceBundle logResourceBundle = null;
    
    private static String newline = System.getProperty("line.separator");
    
    public CacheStatisticsWriter() {
    	Thread cacheStatisticsThread = new Thread(this, "cacheStatisticsThread");
    	cacheStatisticsThread.start();
    }
	
    static {
    	try {
	        logResourceBundle = ResourceBundle.getBundle("log4j");
	        String logPath = logResourceBundle.getString("logpath");
	        fh = new FileHandler(logPath + "cacheStatistics%g.log", 1000000, 20,
	                true);// it have max 20 files with 1MB size
	        fh.setFormatter(new CustomLoggerFormatter());
	        statLogger.addHandler(fh);
	        statLogger.setLevel(Level.ALL);
	        statLogger.setUseParentHandlers(false);
    	}
    	catch(Exception ex) {
    		logger.error("Error creating Statistics logger", ex);
    	}    	
    }    
    
    private void logSecondLevelCacheStatistics(String cacheName, SessionFactory sessionFactory) {
    	try {
	    	Statistics statistics = sessionFactory.getStatistics();	    	
	    	SecondLevelCacheStatistics secLevelCacheStatistics = statistics.getSecondLevelCacheStatistics(cacheName);
	    	StringBuilder strBuilder = new StringBuilder();
	    	strBuilder.append("----------- ## ").append(cacheName).append(" ## SecondLevelCacheStatistics Start ------------").append(newline);
	    	strBuilder.append("ElementCountInMemory = ").append(secLevelCacheStatistics.getElementCountInMemory()).append(newline);
	    	strBuilder.append("SizeInMemory         = ").append(secLevelCacheStatistics.getSizeInMemory()).append(newline);			
	    	strBuilder.append("ElementCountOnDisk   = ").append(secLevelCacheStatistics.getElementCountOnDisk()).append(newline);
	    	strBuilder.append("HitCount             = ").append(secLevelCacheStatistics.getHitCount()).append(newline);
	    	strBuilder.append("MissCount            = ").append(secLevelCacheStatistics.getMissCount()).append(newline);
	    	strBuilder.append("PutCount             = ").append(secLevelCacheStatistics.getPutCount()).append(newline);
			//System.out.println("Entries Map          = " +	secLevelCacheStatistics.getEntries());
	    	strBuilder.append("----------- ## ").append(cacheName).append(" ## SecondLevelCacheStatistics End ------------").append(newline);
	    	statLogger.info(strBuilder.toString());
    	}
    	catch(Exception ex) {
    		statLogger.info("Error writing the Second Level Cache statistics : " + cacheName + " : " + ex.getMessage());
    		//ex.printStackTrace();
    	}
    }
    
    private void logQueryCacheStatistics(SessionFactory sessionFactory) {    
    	try {
	    	Statistics statistics = sessionFactory.getStatistics();
	    	StringBuilder strBuilder = new StringBuilder();
	    	strBuilder.append("----------- QueryCacheStatistics Start ------------").append(newline);    	
	    	strBuilder.append("QueryCacheHitCount   = ").append(statistics.getQueryCacheHitCount()).append(newline);
	    	strBuilder.append("QueryCacheMissCount  = ").append(statistics.getQueryCacheMissCount()).append(newline);
	    	strBuilder.append("QueryCachePutCount   = ").append(statistics.getQueryCachePutCount()).append(newline);
	    	strBuilder.append("----------- QueryCacheStatistics End ------------").append(newline);
	    	statLogger.info(strBuilder.toString());
    	}
    	catch(Exception ex) {
    		statLogger.info("Error writing the query cache statistics : " + ex.getMessage());
    	}    	
    }
    

	@Override
	public void run() {
		ServiceLocator servLoc = ServiceLocator.getServiceLocator();
		SessionFactory sessionFactoryMaster = servLoc.getSessionFactory();
		SessionFactory sessionFactorySlave = servLoc.getSessionFactorySlave();
		
		while(true) {
			try {
				Thread.sleep(300000); // sleep for 5 mins
			}
			catch(InterruptedException ex) {
				
			}
			
			statLogger.info("============================= Master Start ========================");
			//logSecondLevelCacheStatistics("default", sessionFactoryMaster);
			//logSecondLevelCacheStatistics("ecache_ac", sessionFactoryMaster);
			logSecondLevelCacheStatistics("CacheUtility", sessionFactoryMaster);
			logSecondLevelCacheStatistics("ecache_lookup", sessionFactoryMaster);
			logSecondLevelCacheStatistics("ecache_object", sessionFactoryMaster);			
			logSecondLevelCacheStatistics("dssCache", sessionFactoryMaster);
			//logSecondLevelCacheStatistics("dssReportCache", sessionFactoryMaster);
			logSecondLevelCacheStatistics("ddoCache", sessionFactoryMaster);
			logSecondLevelCacheStatistics("budgetCache", sessionFactoryMaster);
			logSecondLevelCacheStatistics("wfCache", sessionFactoryMaster);	
			logSecondLevelCacheStatistics("ecache_query", sessionFactoryMaster);
			logSecondLevelCacheStatistics("org.hibernate.cache.StandardQueryCache", sessionFactoryMaster);
			logQueryCacheStatistics(sessionFactoryMaster);
			statLogger.info("============================= Master End ========================" + newline);			
			
			statLogger.info("============================= Slave Start ========================");			
			//logSecondLevelCacheStatistics("default", sessionFactorySlave);
			//logSecondLevelCacheStatistics("ecache_ac", sessionFactorySlave);
			logSecondLevelCacheStatistics("CacheUtility", sessionFactorySlave);
			logSecondLevelCacheStatistics("ecache_lookup", sessionFactorySlave);
			logSecondLevelCacheStatistics("ecache_object", sessionFactorySlave);
			logSecondLevelCacheStatistics("dssCache", sessionFactorySlave);
			//logSecondLevelCacheStatistics("dssReportCache", sessionFactorySlave);
			logSecondLevelCacheStatistics("ddoCache", sessionFactorySlave);
			logSecondLevelCacheStatistics("budgetCache", sessionFactorySlave);
			logSecondLevelCacheStatistics("wfCache", sessionFactorySlave);
			logSecondLevelCacheStatistics("ecache_query", sessionFactorySlave);
			logSecondLevelCacheStatistics("org.hibernate.cache.StandardQueryCache", sessionFactorySlave);			
			logQueryCacheStatistics(sessionFactorySlave);
			statLogger.info("============================= Slave End ========================" + newline);	
		}
	}
	
	static class CustomLoggerFormatter  extends Formatter  {
	    public String format(LogRecord rec) {
	    	SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	    	StringBuffer buf = new StringBuffer(300);
	    	buf.append(format.format(new Date()));
	    	buf.append(',');
	        buf.append(rec.getMessage());
	        buf.append(System.getProperty("line.separator"));
	        return buf.toString();
	     }
	}	
}
