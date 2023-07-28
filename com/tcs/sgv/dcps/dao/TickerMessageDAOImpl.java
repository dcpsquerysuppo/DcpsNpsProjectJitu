/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 25, 2011		Meeta Thacker								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

/**
 * Class Description - 
 *
 *
 * @author Meeta Thacker
 * @version 0.1
 * @since JDK 5.0
 * May 25, 2011
 */
public class TickerMessageDAOImpl extends GenericDaoHibernateImpl implements TickerMessageDAO{

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	public TickerMessageDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);

	}
	
	public List getTickerMessageList(){
		String strTickerMessage = null;
		List lstMessages = null;
		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" FROM TickerMessage  order by dcpsTickerMessageIdPk desc");
		
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		
		if(lQuery != null){
			lstMessages = lQuery.list();		
		}
		return lstMessages;
	}
	
	public List getTickerMessage(){
		String strTickerMessage = null;
		List lstMessages = null;
		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery
				.append(" SELECT message1,message2,message3 ");
		lSBQuery.append(" FROM TickerMessage where START_DATE <= sysdate and END_DATE >= sysdate ORDER BY dcpsTickerMessageIdPk DESC ");
		
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		
		if(lQuery != null){
			lstMessages = lQuery.list();
			/*if(lstMessages.size() > 0){
				Object obj[] = (Object[])lstMessages.get(0);
				if(obj[0] != null)
				strTickerMessage = obj[0].toString();
				else
					strTickerMessage = "";
				if(obj[1] != null)
					if(!strTickerMessage.equals(""))
						strTickerMessage = strTickerMessage + " --**-- " + obj[1].toString();
					else 
						strTickerMessage =  obj[1].toString();
				
				if(obj[2] != null)
					if(!strTickerMessage.equals(""))
						strTickerMessage = strTickerMessage + " --**-- " + obj[2].toString();
					else 
						strTickerMessage =  obj[2].toString();				
			}*/
		}
		return lstMessages;
	}
	
	public int deleteTickerMessage(String tickerMessagePk){
		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;
		lSBQuery.append(" delete from MST_DCPS_TICKER_MSG where DCPS_TICKER_MESSAGE_ID = "+tickerMessagePk);
		int rows = ghibSession.createSQLQuery(lSBQuery.toString()).executeUpdate();
		gLogger.info("rows..."+rows);
		return rows;
	}
}
