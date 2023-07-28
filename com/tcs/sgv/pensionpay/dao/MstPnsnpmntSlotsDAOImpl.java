/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 15, 2011		Shripal Soni								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionpay.valueobject.MstPnsnpmntSlots;


/**
 * Class Description -
 * 
 * 
 * @author Shripal Soni
 * @version 0.1
 * @since JDK 5.0 Feb 15, 2011
 */
public class MstPnsnpmntSlotsDAOImpl extends GenericDaoHibernateImpl<MstPnsnpmntSlots, Long> implements MstPnsnpmntSlotsDAO {

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;
	SessionFactory sessionFactory = null;

	public MstPnsnpmntSlotsDAOImpl(Class<MstPnsnpmntSlots> type, SessionFactory sessionFactory) {

		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	public HashMap<Integer, MstPnsnpmntSlots> getMaxSlotNumber() throws Exception {

		Query lQuery = null;
		StringBuffer lSb = new StringBuffer();
		List resultList = new ArrayList();
		HashMap<Integer, MstPnsnpmntSlots> slotDtlMap = null;
		MstPnsnpmntSlots lObjMstPnsnpmntSlots = null;
		try {
			ghibSession = this.getSession();
			lSb.append("from MstPnsnpmntSlots \n");
			lQuery = ghibSession.createQuery(lSb.toString());
			resultList = lQuery.list();
			if (resultList != null && resultList.size() > 0) {
				slotDtlMap = new HashMap<Integer, MstPnsnpmntSlots>();
				for (Object lInnObjMstPnsnpmntSlots : resultList) {
					lObjMstPnsnpmntSlots = new MstPnsnpmntSlots();
					lObjMstPnsnpmntSlots = (MstPnsnpmntSlots) lInnObjMstPnsnpmntSlots;
					slotDtlMap.put(lObjMstPnsnpmntSlots.getSlotNo(), lObjMstPnsnpmntSlots);
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
			throw (e);
		}
		return slotDtlMap;

	}
}
