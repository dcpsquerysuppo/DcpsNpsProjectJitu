package com.tcs.sgv.gpf.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.dao.CmnLanguageMstDao;
import com.tcs.sgv.common.dao.CmnLanguageMstDaoImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.valueobject.CmnLanguageMst;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class GPFPurposeConfigDAOImpl extends GenericDaoHibernateImpl implements GPFPurposeConfigDAO
{
	Session ghibSession = null;
	public GPFPurposeConfigDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}
	
	public List getAllRAPurpose()throws Exception
	{
		List lLstRAPurpose = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT lookupId, lookupName FROM CmnLookupMst WHERE parentLookupId = 800001");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lLstRAPurpose = lQuery.list();
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return lLstRAPurpose;
	}
	
	public List getAllNRAPurpose()throws Exception
	{
		List lLstNRAPurpose = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT lookupId, lookupName FROM CmnLookupMst WHERE parentLookupId = 800020");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lLstNRAPurpose = lQuery.list();
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return lLstNRAPurpose;
	}
	
	public void insertCmnLookupMst(String lStrLookupName, Long lLngParentLookUpId, Long lLngCrtdUsrId, Long lLngCrtdPostId)throws Exception
	{
		CmnLookupMst lObjCmnLookupMst = null;
		Long lLngLookUpId = null;
		Long lLngOrderNo = null;
		try{
			lLngLookUpId = getMaxLookupId();
			lLngOrderNo = getMaxOrderNO(lLngParentLookUpId);
			
			CmnLanguageMst lObjCmnLanguageMst = new CmnLanguageMst();
			CmnLanguageMstDao lObjCmnLanguageMstDAO = new CmnLanguageMstDaoImpl(CmnLanguageMst.class,this.getSessionFactory());
			lObjCmnLanguageMst = lObjCmnLanguageMstDAO.read(Long.valueOf(1));
			
			
			lObjCmnLookupMst = new CmnLookupMst();
			lObjCmnLookupMst.setLookupId(lLngLookUpId);
			lObjCmnLookupMst.setParentLookupId(lLngParentLookUpId);
			lObjCmnLookupMst.setLookupName(lStrLookupName);
			lObjCmnLookupMst.setLookupDesc(lStrLookupName);
			if(lStrLookupName.length() > 15){
				lObjCmnLookupMst.setLookupShortName(lStrLookupName.substring(0,15));
			}else{
				lObjCmnLookupMst.setLookupShortName(lStrLookupName);
			}
			lObjCmnLookupMst.setCmnLanguageMst(lObjCmnLanguageMst);
			lObjCmnLookupMst.setCreatedBy(lLngCrtdUsrId);
			lObjCmnLookupMst.setCreatedByPost(lLngCrtdPostId);
			lObjCmnLookupMst.setCreatedDate(DBUtility.getCurrentDateFromDB());
			lObjCmnLookupMst.setOrderNo(lLngOrderNo);
			ghibSession.save(lObjCmnLookupMst);
			ghibSession.flush();
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
	}
	
	public Long getMaxLookupId()throws Exception
	{
		Long lLngMaxId = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT MAX(lookupId) FROM CmnLookupMst WHERE lookupId LIKE '80%'");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lLngMaxId = (Long) lQuery.list().get(0);
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return (lLngMaxId + 1);
	}
	
	public Long getMaxOrderNO(Long lLngParentLookUpId)throws Exception
	{
		Long lLngMaxId = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT MAX(orderNo) FROM CmnLookupMst WHERE parentLookupId =:parentLookUp");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("parentLookUp", lLngParentLookUpId);
			lLngMaxId = (Long) lQuery.list().get(0);
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return (lLngMaxId + 1);
	}
	
	public void updatePurposeCategory(String[] lArrLookUpName, String[] lArrLookupId)throws Exception
	{
		StringBuilder lSBQuery = null;
		Query lQuery = null;
		try{
			for(Integer lIntCnt =0; lIntCnt < lArrLookupId.length; lIntCnt++)
			{
				lSBQuery = new StringBuilder();
				lSBQuery.append("UPDATE CmnLookupMst SET lookupName =:lookUpName ");
				lSBQuery.append("WHERE lookupId =:lookUpId");
				lQuery = ghibSession.createQuery(lSBQuery.toString());
				lQuery.setParameter("lookUpName", lArrLookUpName[lIntCnt]);
				lQuery.setLong("lookUpId",Long.parseLong(lArrLookupId[lIntCnt]));
				lQuery.executeUpdate();
			}
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
	}

}