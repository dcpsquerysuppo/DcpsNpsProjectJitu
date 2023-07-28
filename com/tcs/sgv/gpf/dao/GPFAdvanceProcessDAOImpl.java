/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Aug 06, 2011		Meeta Thacker								
 *******************************************************************************
 */
package com.tcs.sgv.gpf.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

/**
 * Class Description -
 * 
 * 
 * @author Meeta Thacker
 * @version 0.1
 * @since JDK 5.0 Aug 06, 2011
 */

public class GPFAdvanceProcessDAOImpl extends GenericDaoHibernateImpl implements GPFAdvanceProcessDAO {
	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	public GPFAdvanceProcessDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFAdvanceProcessDAO#getSavedAdvance(java.lang.String
	 * , java.lang.String, java.lang.String)
	 */
	public List getSavedAdvance(String strGpfAccNo, String requestType, String lStrPostId) {
		List savedAdvance = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append("select MGA.mstGpfAdvanceId");
			lSBQuery.append(" FROM MstGpfAdvance MGA");

			lSBQuery.append(" WHERE MGA.statusFlag = 'D'");

			lSBQuery.append(" AND MGA.gpfAccNo = :gpfAccNo AND MGA.advanceType = :requestType");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", strGpfAccNo);
			lQuery.setParameter("requestType", requestType);
			savedAdvance = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception in getSavedAdvance of GPFAdvanceProcessDAOImpl  : ", e);
		}
		return savedAdvance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFAdvanceProcessDAO#getAdvanceRequestForAdvanceId
	 * (java.lang.String, java.lang.Long, java.lang.String, java.lang.String)
	 */
	public List getAdvanceRequestForAdvanceId(String strGpfAccNo, Long lLngAdvanceId, String requestType,
			String lStrPostId) {
		List lLstGPFAdvance = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append("select MGA.mstGpfAdvanceId");
			lSBQuery.append(" FROM MstGpfAdvance MGA");
			// lSBQuery.append(", WfJobMst WJ");
			lSBQuery.append(" WHERE MGA.mstGpfAdvanceId =:advanceId ");
			lSBQuery.append(" AND MGA.gpfAccNo = :gpfAccNo AND MGA.advanceType = :requestType ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("advanceId", lLngAdvanceId);
			// lQuery.setParameter("postId", lStrPostId);
			// lQuery.setParameter("docId", 800002L);
			lQuery.setParameter("gpfAccNo", strGpfAccNo);
			lQuery.setParameter("requestType", requestType);
			lLstGPFAdvance = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception in getAdvanceRequestForAdvanceId of GPFAdvanceProcessDAOImpl  : ", e);
		}
		return lLstGPFAdvance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFAdvanceProcessDAO#requestDataAlreadyExists(java
	 * .lang.String)
	 */
	public Boolean requestDataAlreadyExists(String strGpfAccNo) {
		List savedRA = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append("select MGA.transactionId");
			lSBQuery.append(" FROM MstGpfAdvance MGA");
			lSBQuery.append(" WHERE MGA.gpfAccNo = :gpfAccNo AND MGA.statusFlag LIKE 'F%'");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", strGpfAccNo);
			savedRA = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception in requestDataAlreadyExists of GPFAdvanceProcessDAOImpl  : ", e);
		}
		if (savedRA.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFAdvanceProcessDAO#getMstAdvanceIdForTransactionId
	 * (java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Long getMstAdvanceIdForTransactionId(String lStrTransactionId) {
		StringBuilder lSBQuery = new StringBuilder();
		List<Long> tempList = new ArrayList();
		Long lLngAdvanceId = null;

		try {
			lSBQuery.append(" select mstGpfAdvanceId FROM MstGpfAdvance WHERE transactionId = :transactionId");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("transactionId", lStrTransactionId);
			
			if(lQuery != null)
			tempList = (List<Long>)lQuery.list();
			if (tempList != null && tempList.size() > 0) {
				lLngAdvanceId = tempList.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception in getMstAdvanceIdForTransactionId of GPFAdvanceProcessDAOImpl  : ", e);
		}
		return lLngAdvanceId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFAdvanceProcessDAO#getGPFAccountObjectForAccountNo
	 * (java.lang.String)
	 */
	public Object getGPFAccountObjectForAccountNo(String gpfAccNo) {
		StringBuilder lSBQuery = new StringBuilder();
		List gpfAccList = new ArrayList();
		try {
			lSBQuery.append(" FROM MstEmpGpfAcc WHERE gpfAccNo = :gpfAccNo");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", gpfAccNo);

			gpfAccList = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception in getGPFAccountObjectForAccountNo of GPFAdvanceProcessDAOImpl  : ", e);
		}
		return gpfAccList.get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFAdvanceProcessDAO#getAdvanceNotRecovered(java.
	 * lang.String)
	 */
	public List getAdvanceNotRecovered(String lStrGpfAccNo) {
		List lstAdvanceNotRecovered = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();

		try {
			lSBQuery.append("SELECT transactionId,outstandingAmount,voucherNo,voucherDate");
			lSBQuery.append(" FROM MstGpfAdvance");
			lSBQuery.append(" WHERE gpfAccNo=:gpfAccNo AND installmentsLeft>0 AND statusFlag IN ('A','AC')");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);

			lstAdvanceNotRecovered = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception in getAdvanceNotRecovered of GPFAdvanceProcessDAOImpl  : ", e);
		}
		return lstAdvanceNotRecovered;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFAdvanceProcessDAO#getSubTypesForPurpose(java.lang
	 * .Long)
	 */
	public List getSubTypesForPurpose(Long lLongPurpose) {
		List lstSubType = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		ComboValuesVO lObjComboValuesVO = null;
		List lLstReturnList = new ArrayList<Object>();

		try {
			lSBQuery.append("SELECT lookupId,lookupDesc");
			lSBQuery.append(" FROM CmnLookupMst");
			lSBQuery.append(" WHERE parentLookupId=:purposeId");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("purposeId", lLongPurpose);

			lstSubType = lQuery.list();

			if (lstSubType != null && lstSubType.size() != 0) {
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("--Select--");
				lLstReturnList.add(lObjComboValuesVO);
				Object obj[];
				for (int liCtr = 0; liCtr < lstSubType.size(); liCtr++) {
					obj = (Object[]) lstSubType.get(liCtr);
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId(obj[0].toString());
					lObjComboValuesVO.setDesc(obj[1].toString());
					lLstReturnList.add(lObjComboValuesVO);
				}
			} else {

				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("--Select--");
				lLstReturnList.add(lObjComboValuesVO);
			}
		} catch (Exception e) {
			logger.error("Exception in getSubTypesForPurpose of GPFAdvanceProcessDAOImpl  : ", e);
		}

		return lLstReturnList;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.gpf.dao.GPFAdvanceProcessDAO#getSubType(java.lang.Long)
	 */
	public List getSubType(Long lLngNonRefundType) {
		List<Object> lLstReturnList = null;
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT lookupId, lookupDesc from CmnLookupMst ");
			lSBQuery.append("WHERE parentLookupId = :NonRefundType");

			Query lObjQuery = ghibSession.createQuery(lSBQuery.toString());

			lObjQuery.setParameter("NonRefundType", lLngNonRefundType);

			List lLstResult = lObjQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			lLstReturnList = new ArrayList<Object>();

			if (lLstResult != null && lLstResult.size() != 0) {
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("--Select--");
				lLstReturnList.add(lObjComboValuesVO);
				Object obj[];
				for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
					obj = (Object[]) lLstResult.get(liCtr);
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId(obj[0].toString());
					lObjComboValuesVO.setDesc(obj[1].toString());
					lLstReturnList.add(lObjComboValuesVO);
				}
			} else {

				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("--Select--");
				lLstReturnList.add(lObjComboValuesVO);
			}
		} catch (Exception e) {
			logger.error("Exception in getSubType of GPFAdvanceProcessDAOImpl  : ", e);			
		}
		return lLstReturnList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.gpf.dao.GPFAdvanceProcessDAO#getUserName(java.lang.Long)
	 */
	public String getUserName(Long lLngUserId) {
		List lLstUser = new ArrayList();
		String lStrUserName = "";
		StringBuilder lSBQuery = new StringBuilder();

		try {
			lSBQuery.append("SELECT emp_fname CONCAT ' ' CONCAT emp_mname CONCAT ' ' CONCAT emp_lname");
			lSBQuery.append(" FROM Org_Emp_Mst");
			lSBQuery.append(" WHERE user_id = :userId");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("userId", lLngUserId);

			lLstUser = lQuery.list();
			if (lLstUser != null && lLstUser.size() > 0) {
				lStrUserName = lLstUser.get(0).toString();
			}
		} catch (Exception e) {
			logger.error("Exception in getUserName of GPFAdvanceProcessDAOImpl  : ", e);			
		}
		return lStrUserName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFAdvanceProcessDAO#getOrgEmpIdForGpfAccNo(java.
	 * lang.String)
	 */
	public Long getOrgEmpIdForGpfAccNo(String lStrGpfAccNo) {
		List lLstEmp = new ArrayList();
		Long lLstOrgEmpId = null;
		StringBuilder lSBQuery = new StringBuilder();

		try {
			lSBQuery.append("SELECT MGE.orgEmpMstId ");
			lSBQuery.append(" FROM MstEmp MGE,MstEmpGpfAcc MGA");
			lSBQuery.append(" WHERE MGA.gpfAccNo = :gpfAccNo AND MGE.dcpsEmpId=MGA.mstGpfEmpId AND MGE.dcpsOrGpf='N'");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);

			lLstEmp = lQuery.list();
			if (lLstEmp != null && lLstEmp.size() > 0) {
				lLstOrgEmpId = (Long) lLstEmp.get(0);
			}
		} catch (Exception e) {
			logger.error("Exception in getOrgEmpIdForGpfAccNo of GPFAdvanceProcessDAOImpl  : ", e);			
		}
		return lLstOrgEmpId;
	}
	
	public void updateOrderNo(String lStrGpfReqId, String lStrOrderNo) throws Exception
	{
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("UPDATE MstGpfReq SET orderNo = :orderNo WHERE mstGpfReqId = :mstGpfReqId");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("orderNo", lStrOrderNo);
			lQuery.setParameter("mstGpfReqId", Long.parseLong(lStrGpfReqId));
			lQuery.executeUpdate();
		} catch(Exception e){
			logger.error("Exception in updateOrderNo : ", e);
		}
	}
	
	public String getDesignationAndOfficeName(Long lLngPostId, String lStrLocCode)throws Exception
	{
		List lLstResData = null;
		String lStrFinalData = "";
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT ODM.dsgnName, CLM.locName ");
			lSBQuery.append("FROM OrgDesignationMst ODM, CmnLocationMst CLM, OrgPostMst OPM ");
			lSBQuery.append("WHERE OPM.postId = :postId AND OPM.dsgnCode = ODM.dsgnCode ");
			lSBQuery.append("AND CLM.locationCode = :locCode ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("postId", lLngPostId);
			lQuery.setParameter("locCode", lStrLocCode);
			lLstResData = lQuery.list();
			
			if(lLstResData != null && lLstResData.size() > 0){
				Object []lObj = (Object[]) lLstResData.get(0);
				lStrFinalData = lObj[0].toString() + " ,";
				lStrFinalData = lStrFinalData + lObj[1].toString();
			}
		}catch(Exception e){
			logger.error("Exception in getDesignationAndOfficeName : ", e);
		}
		
		return lStrFinalData;
	}
}
