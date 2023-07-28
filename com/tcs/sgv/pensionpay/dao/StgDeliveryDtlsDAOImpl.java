/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 14, 2011		Shripal Soni								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.constant.Constants;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionpay.valueobject.StgDeliveryDtls;


/**
 * Class Description -
 * 
 * 
 * @author Shripal Soni
 * @version 0.1
 * @since JDK 5.0 Feb 14, 2011
 */
public class StgDeliveryDtlsDAOImpl extends GenericDaoHibernateImpl implements StgDeliveryDtlsDAO {

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	SessionFactory sessionFactory = null;

	public StgDeliveryDtlsDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	public int getStgDeliveryDtlsCount(String lStrLocationCode) throws Exception {

		StringBuffer strQuery = null;
		List lLstDeliveryDtl = null;
		int lIntCount = 0;
		try {
			strQuery = new StringBuffer();
			lLstDeliveryDtl = new ArrayList<StgDeliveryDtls>();
			strQuery.append("select count (distinct delvId) from StgDeliveryDtls where locationCode = :locationCode");
//			strQuery.append("select count(delv.fileId) ");
//			strQuery.append("From ");
//			strQuery.append("StgDeliveryDtls delv");

			Query hqlQuery = ghibSession.createQuery(strQuery.toString());
			hqlQuery.setString("locationCode", lStrLocationCode);
			
			lLstDeliveryDtl = hqlQuery.list();
			lIntCount = ((Long) lLstDeliveryDtl.get(0)).intValue();
		} catch (Exception e) {
			gLogger.error("Error occured during fetching of stg delivery details");
			throw (e);
		}
		return lIntCount;

	}

	public List<StgDeliveryDtls> getStgDeliveryDtls(Map displayTag,String lStrLocationCode) throws Exception {

		StringBuffer strQuery = null;
		List<StgDeliveryDtls> lLstDeliveryDtl = null;
		try {
			String[] columnValues = new String[]{"", "uploadDate", "delvStatus"};
			strQuery = new StringBuffer();
			lLstDeliveryDtl = new ArrayList<StgDeliveryDtls>();

			strQuery.append("select delvId,max(uploadDate),delvStatus from StgDeliveryDtls where locationCode = :locationCode group by delvId,delvStatus order by ");
			
//			strQuery.append("select clm.lookupName,delv.uploadDate,delv.delvStatus ");
//			strQuery.append("From ");
//			strQuery.append("StgDeliveryDtls delv, CmnLookupMst clm  ");
//			strQuery.append("WHERE  clm.lookupId = delv.delvType");
//			strQuery.append(" ORDER BY ");

			String orderString = (displayTag.containsKey(Constants.KEY_SORT_ORDER) ? (String) displayTag.get(Constants.KEY_SORT_ORDER) : "desc");

			Integer orderbypara = null;

			if (displayTag.containsKey(Constants.KEY_SORT_PARA)) {
				orderbypara = (Integer) displayTag.get(Constants.KEY_SORT_PARA);
				strQuery.append(columnValues[orderbypara.intValue()] + " " + orderString);
			} else {
				strQuery.append(" delvId,delvStatus DESC");
			}
			Query hqlQuery = ghibSession.createQuery(strQuery.toString());
			hqlQuery.setString("locationCode", lStrLocationCode);
			Integer pageNo = (displayTag.containsKey(Constants.KEY_PAGE_NO) ? (Integer) displayTag.get(Constants.KEY_PAGE_NO) : 1);
			hqlQuery.setFirstResult((pageNo.intValue() - 1) * Constants.DELV_PAGE_SIZE);
			hqlQuery.setMaxResults(Constants.DELV_PAGE_SIZE);
			lLstDeliveryDtl = hqlQuery.list();
		} catch (Exception e) {
			gLogger.error("Error occured during fetching of stg delivery details");
			throw (e);
		}
		return lLstDeliveryDtl;

	}
}
