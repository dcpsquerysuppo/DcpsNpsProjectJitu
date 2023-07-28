package com.tcs.sgv.common.dao;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.constant.DBConstants;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.valueobject.TrnBillMvmnt;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class BillMovementDAOImpl extends GenericDaoHibernateImpl<TrnBillMvmnt, Long> implements BillMovementDAO 
{
	Log logger = LogFactory.getLog(getClass());
	HttpServletRequest request = null;
	String gStrLocale = null; /* Global Variable for locale */
	Locale gLclLocale = null; /* Global Variable for Locale */
	Long gLngLocId = null; /* Global Variable for PostId */
	Long gLngPostId = null; /* Global Variable for PostId */
	Long gLngUserId = null; /* Global Variable for UserId */
	Long gLngLangId = null; /* Global Variable for LangId */
	Long gLngEmpId = null; /* Global Variable for EmpId */
	String gStrLocationCode = null; /* Global Variable for Location Code */
	Long gLngDBId = null; /* Global Variable for DB Id */
	Date gDtCurDate = null; /* Global Variable for Current Date */

//	private static final String UPDATE_LAST_MVMNT_REMARKS = "UPDATE Trn_Bill_Mvmnt SET bill_remarks=:BILL_REMARKS, obj_remarks=:OBJ_REMARKS WHERE bill_no=:BILLNO AND movemnt_id IN (SELECT MAX(m2.movemnt_id) FROM (SELECt * FROM trn_bill_mvmnt) m2 WHERE m2.bill_no=:BILLNO)";
	private static final String UPDATE_LAST_MVMNT_REMARKS = "UPDATE TRN_BILL_MVMNT SET BILL_REMARKS = :BILL_REMARKS, OBJ_REMARKS = :OBJ_REMARKS WHERE BILL_NO = :BILLNO AND MOVEMNT_ID IN (select * from (SELECT MAX(MOVEMNT_ID) FROM TRN_BILL_MVMNT WHERE  BILL_NO = :BILLNO) X)";
	public BillMovementDAOImpl(Class<TrnBillMvmnt> type, SessionFactory sessionFactory) 
	{
		super(type);
		setSessionFactory(sessionFactory);
	}

	public long getmaxMovementId(long billNo) 
	{
		Session hibSession = getSession();
		Query sqlQuery = hibSession.createQuery(" SELECT MAX(T.movemntId)  FROM TrnBillMvmnt T  WHERE T.billNo=:billNo");
		sqlQuery.setParameter("billNo", billNo);
		Iterator iterator = sqlQuery.list().iterator();
		long maxMvmntId = 0;
		while (iterator.hasNext()) 
		{
			Object obj = iterator.next();
			if (obj != null)
				maxMvmntId = Long.parseLong(obj.toString());
		}
		maxMvmntId = maxMvmntId + 1;
		return maxMvmntId;
	}

	public int updateObjectionFlag(Long billNo) 
	{
		Session hibSession = getSession();
		Query query = hibSession.createSQLQuery("UPDATE RLT_OBJECTION SET OBJ_FLAG = 'PO' WHERE "
				  		+" MASTER_ID = :billNo AND MODULE_NAME=:moduleName");
		query.setParameter("billNo", billNo);
		query.setString("moduleName", DBConstants.BILL_PROCESSING);
		logger.info("Query to update Objection Flag in Bill Objection : " + query);
		int updateStatus = query.executeUpdate();
		return updateStatus;
	}

	public TrnBillMvmnt getMvmntVOByStatus(String lStrBillNo) 
	{
		TrnBillMvmnt trnBillMvmnt = null;
		Session hibSession = getSession();
		Query query = hibSession.createQuery("from TrnBillMvmnt where billNo=:billNo" + " and movemntId in (select max(movemntId) from TrnBillMvmnt where billNo =:billNo" + ") ");
		query.setParameter("billNo", Long.parseLong(lStrBillNo));
		List resultList = query.list();
		if (resultList != null && resultList.size() > 0) 
		{
			trnBillMvmnt = (TrnBillMvmnt) resultList.get(0);
		}
		return trnBillMvmnt;
	}

	/**
	 * Method to update Remarks
	 * @param TrnBillRemarks :  lObjRemarksVO
	 * 
	 * @return int
	 */

	public void updateRemarks(String lStrRemarks, String lStrObjRemarks, Long lLngBillNo) throws Exception 
	{
		Session hibSession = getSession();
		Query lQuery = hibSession.createSQLQuery(UPDATE_LAST_MVMNT_REMARKS);
		lQuery.setString("BILL_REMARKS", lStrRemarks);
		lQuery.setString("OBJ_REMARKS", lStrObjRemarks);
		lQuery.setLong("BILLNO", lLngBillNo);
		lQuery.executeUpdate();
	}

	/**
	 * Method to set all basic information obtained from SessionHelper
	 * 
	 * @param inputMap
	 */
	private void setSessionInfo(Map inputMap) 
	{
		request = (HttpServletRequest) inputMap.get("requestObj");
		gLclLocale = new Locale(SessionHelper.getLocale(request));
		gLngLangId = SessionHelper.getLangId(inputMap);
		gLngEmpId = SessionHelper.getEmpId(inputMap);
		gLngPostId = SessionHelper.getPostId(inputMap);
		gLngUserId = SessionHelper.getUserId(inputMap);
		gStrLocationCode = SessionHelper.getLocationCode(inputMap);
		gLngDBId = SessionHelper.getDbId(inputMap);
		gDtCurDate = SessionHelper.getCurDate();
		gLngLocId = SessionHelper.getLocationId(inputMap);
	}

	public TrnBillMvmnt getMvmntInstance(Long lLngUserId, Long lLngBillNo) 
	{
		TrnBillMvmnt lObjTrnBillMvmnt = new TrnBillMvmnt();
		Session hibSession = getSession();
		StringBuilder lSBQuery = new StringBuilder();
		List lListResult = null;

		lSBQuery.append(" FROM TrnBillMvmnt WHERE "  );
			//	"statusUpdtUserid =:userId");
		lSBQuery.append("  billNo=:billNo");
		//lSBQuery.append(" AND movemntId=:maxMvmntId");
		lSBQuery.append(" ORDER BY movemntId DESC ");

		Query query = hibSession.createQuery(lSBQuery.toString());
		//query.setParameter("userId", lLngUserId);
		query.setParameter("billNo", lLngBillNo);
		//query.setParameter("maxMvmntId", (getmaxMovementId(lLngBillNo) - 1));

		lListResult = query.list();
		if (lListResult != null && lListResult.size() > 0) 
		{
			lObjTrnBillMvmnt = (TrnBillMvmnt) lListResult.get(0);
		}
		return lObjTrnBillMvmnt;
	}

	public TrnBillMvmnt getRmrksForCurrUser(Long lLngBillNo, Long lLngUserId) throws Exception
    {
        logger.info("Start");
        StringBuilder lsb = new StringBuilder();
        TrnBillMvmnt lTrnBillMvmnt = null;
        List lLstRsltSet = null;
        try
        {
        	
            lsb.append(" FROM TrnBillMvmnt tbr");
            lsb.append(" Where tbr.billNo = :billNo and tbr.statusUpdtUserid = :userId ORDER BY tbr.movemntId DESC ");

            Session hibSession = getSession();
            Query lQuery = hibSession.createQuery(lsb.toString());
           
            lQuery.setParameter("billNo", lLngBillNo);
            lQuery.setParameter("userId", lLngUserId);

            lLstRsltSet = lQuery.list();
            if (lLstRsltSet != null && !lLstRsltSet.isEmpty())
            {
            	lTrnBillMvmnt = (TrnBillMvmnt) lLstRsltSet.get(0);
            }
        }
        catch (Exception e)
        {
            logger.error("Error is : " + e, e);
            throw (e);
        }
        logger.info("End");
        return lTrnBillMvmnt;
    }
	
}



