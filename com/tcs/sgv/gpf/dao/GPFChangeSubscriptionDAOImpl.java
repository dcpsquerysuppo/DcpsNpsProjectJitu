package com.tcs.sgv.gpf.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class GPFChangeSubscriptionDAOImpl extends GenericDaoHibernateImpl implements GPFChangeSubscriptionDAO {
	Session ghibSession = null;

	public GPFChangeSubscriptionDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFChangeSubscriptionDAO#getChangeSubscription(java
	 * .lang.String)
	 */
	public List getChangeSubscription(String lStrGpfAccNo) {
		List changeSubAmountList = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery
					.append("select transactionId,monthlySubscription,effectFromMonth,gpfEmpSubscriptionId,applicationDate");
			lSBQuery.append(" FROM MstGpfEmpSubscription");
			lSBQuery.append(" WHERE gpfAccNo = :gpfAccNo AND statusFlag = 'D'");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
			changeSubAmountList = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception in getChangeSubscription of GPFChangeSubscriptionDAOImpl  : ", e);			
		}
		return changeSubAmountList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.gpf.dao.GPFChangeSubscriptionDAO#
	 * getChangeSubscriptionToDEOApprover(java.lang.String, java.lang.Long,
	 * java.lang.Long)
	 */
	public List getChangeSubscriptionToDEOApprover(String lStrGpfAccNo, Long lLngChangeSubId, Long lLngPostId) {
		List gpfEmpApproverlist = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();

		try {
			lSBQuery
					.append("select transactionId,monthlySubscription,effectFromMonth,gpfEmpSubscriptionId,applicationDate,approverRemarks");
			lSBQuery.append(" FROM MstGpfEmpSubscription");
			lSBQuery.append(" WHERE gpfAccNo = :gpfAccNo AND gpfEmpSubscriptionId = :changeSubId ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
			lQuery.setParameter("changeSubId", lLngChangeSubId);
			// lQuery.setParameter("postId", lLngPostId);

			gpfEmpApproverlist = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception in getChangeSubscriptionToDEOApprover of GPFChangeSubscriptionDAOImpl  : ", e);			
		}
		return gpfEmpApproverlist;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFChangeSubscriptionDAO#getFinancialYearCount(java
	 * .lang.String, java.lang.Integer)
	 */
	public Long getFinancialYearCount(String lStrGpfAccNo, Integer lIntFinYearId) {
		Long lLngYearCount = 0L;
		List<Long> tempList = new ArrayList<Long>();
		StringBuilder lSBQuery = new StringBuilder();
		Integer lIntNxtYearId = lIntFinYearId + 1;

		try {
			lSBQuery.append("select count(*)");
			lSBQuery.append(" FROM MstGpfEmpSubscription");
			lSBQuery
					.append(" WHERE gpfAccNo = :gpfAccNo AND ((finYearId = :finYearId AND effectFromMonth>3) OR (finYearId = :nxtFinYearId AND effectFromMonth<=3)) AND statusFlag = 'A' AND activeFlag=1");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
			lQuery.setParameter("finYearId", Long.parseLong(lIntFinYearId.toString()));
			lQuery.setParameter("nxtFinYearId", lIntNxtYearId.longValue());

			tempList = lQuery.list();
			lLngYearCount = tempList.get(0);
		} catch (Exception e) {
			logger.error("Exception in getFinancialYearCount of GPFChangeSubscriptionDAOImpl  : ", e);			
		}
		return lLngYearCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFChangeSubscriptionDAO#getFinancialYearDtlDecrement
	 * (java.lang.String, java.lang.Integer)
	 */
	public Long getFinancialYearDtlDecrement(String lStrGpfAccNo, Integer lIntFinYearId) {
		Long lLngDecrement = 0L;
		List<Long> tempList = new ArrayList<Long>();
		StringBuilder lSBQuery = new StringBuilder();
		Integer lIntNxtYearId = lIntFinYearId + 1;
		try {
			lSBQuery.append("select count(*)");
			lSBQuery.append(" FROM MstGpfEmpSubscription");
			lSBQuery
					.append(" WHERE gpfAccNo = :gpfAccNo AND ((finYearId = :finYearId AND effectFromMonth>3) OR (finYearId = :nxtFinYearId AND effectFromMonth<=3)) AND changeType='D' AND activeFlag=1");
			lSBQuery.append(" AND statusFlag = 'A'");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
			lQuery.setParameter("finYearId", lIntFinYearId.longValue());
			lQuery.setParameter("nxtFinYearId", lIntNxtYearId.longValue());

			tempList = lQuery.list();
			lLngDecrement = tempList.get(0);
		} catch (Exception e) {
			logger.error("Exception in getFinancialYearDtlDecrement of GPFChangeSubscriptionDAOImpl  : ", e);			
		}
		return lLngDecrement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFChangeSubscriptionDAO#getFinancialYearDtlIncrement
	 * (java.lang.String, java.lang.Integer)
	 */
	public Long getFinancialYearDtlIncrement(String lStrGpfAccNo, Integer lIntFinYearId) {
		Long lLngIncrement = 0L;
		List<Long> tempList = new ArrayList<Long>();
		StringBuilder lSBQuery = new StringBuilder();
		Integer lIntNxtYearId = lIntFinYearId + 1;
		try {
			lSBQuery.append("select count(*)");
			lSBQuery.append(" FROM MstGpfEmpSubscription");
			lSBQuery
					.append(" WHERE gpfAccNo = :gpfAccNo AND ((finYearId = :finYearId AND effectFromMonth>3) OR (finYearId = :nxtFinYearId AND effectFromMonth<=3)) AND changeType='I' AND activeFlag=1");
			lSBQuery.append(" AND statusFlag = 'A'");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
			lQuery.setParameter("finYearId", Long.parseLong(lIntFinYearId.toString()));
			lQuery.setParameter("nxtFinYearId", lIntNxtYearId.longValue());
			tempList = lQuery.list();
			lLngIncrement = tempList.get(0);
		} catch (Exception e) {
			logger.error("Exception in getFinancialYearDtlDecrement of GPFChangeSubscriptionDAOImpl  : ", e);			
		}
		return lLngIncrement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFChangeSubscriptionDAO#getFutureMonthDtlForNullify
	 * (java.lang.String, java.lang.Long, java.lang.Integer)
	 */
	public List getFutureMonthDtlForNullify(String lStrGpfAccNo, Long lLngFinYearId, Integer lIntMonthId) {

		List lFutureMonthList = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		Long lLngNxtYearId = lLngFinYearId + 1;
		try {
			lSBQuery.append("select gpf.gpfEmpSubscriptionId");
			lSBQuery.append(" FROM MstGpfEmpSubscription gpf");
			lSBQuery.append(" WHERE gpfAccNo = :gpfAccNo");
			if (lIntMonthId > 3) {
				lSBQuery
						.append(" AND ((finYearId = :finYearId AND effectFromMonth >= :MonthId) OR finYearId=:nxtFinYearId)");
			} else {
				lSBQuery.append(" AND finYearId = :finYearId AND effectFromMonth >= :MonthId ");
			}
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
			lQuery.setParameter("finYearId", lLngFinYearId);
			lQuery.setParameter("MonthId", lIntMonthId);
			if (lIntMonthId > 3) {
				lQuery.setParameter("nxtFinYearId", lLngNxtYearId);
			}
			lFutureMonthList = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception in getFutureMonthDtlForNullify of GPFChangeSubscriptionDAOImpl  : ", e);			
		}

		return lFutureMonthList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFChangeSubscriptionDAO#getMonths(java.lang.Long)
	 */
	public List getMonths(Long lLngYearId) {

		StringBuilder lSBQuery = new StringBuilder();
		Long lLngNxtYear = lLngYearId + 1;
		List<Object> lLstReturnList = null;
		try {
			lSBQuery
					.append("Select * from (SELECT mm.month_id,SUBSTR(mm.month_name,1,3) CONCAT '-' CONCAT fy.fin_year_code , fy.fin_year_id");
			lSBQuery
					.append(" FROM sgva_month_mst mm, sgvc_fin_year_mst fy WHERE mm.month_id < 13 AND mm.month_id >3 AND fy.fin_year_id = "
							+ lLngYearId);
			lSBQuery.append(" UNION ");
			lSBQuery
					.append("SELECT mm.month_id,SUBSTR(mm.month_name,1,3) CONCAT '-' CONCAT fy.fin_year_code,fy.fin_year_id");
			lSBQuery
					.append(" FROM sgva_month_mst mm, sgvc_fin_year_mst fy WHERE mm.month_id IN (1,2,3) AND fy.fin_year_id = "
							+ lLngNxtYear + ") order by 3,1");

			Query selectQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			List lLstResult = selectQuery.list();
			ComboValuesVO lObjComboValuesVO = null;

			if (lLstResult != null && lLstResult.size() != 0) {
				lLstReturnList = new ArrayList<Object>();
				Object obj[];
				for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
					obj = (Object[]) lLstResult.get(liCtr);
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId(obj[0].toString());
					lObjComboValuesVO.setDesc(obj[1].toString());
					lLstReturnList.add(lObjComboValuesVO);
				}
			} else {
				lLstReturnList = new ArrayList<Object>();
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("-- Select --");
				lLstReturnList.add(lObjComboValuesVO);
			}
		} catch (Exception e) {
			logger.error("Exception in getMonths of GPFChangeSubscriptionDAOImpl  : ", e);			
		}
		return lLstReturnList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFChangeSubscriptionDAO#requestDataAlreadyExists
	 * (java.lang.String)
	 */
	public Boolean requestDataAlreadyExists(String strGpfAccNo) {
		List CSRequest = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append("select MGES.transactionId");
			lSBQuery.append(" FROM MstGpfEmpSubscription MGES");
			lSBQuery.append(" WHERE MGES.gpfAccNo = :gpfAccNo AND MGES.statusFlag LIKE 'F%'");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", strGpfAccNo);
			CSRequest = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception in requestDataAlreadyExists of GPFChangeSubscriptionDAOImpl  : ", e);			
		}
		if (CSRequest.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFChangeSubscriptionDAO#getLastScheduleData(java
	 * .lang.String, java.lang.Long)
	 */
	public List getLastScheduleData(String lStrGpfAccNo, Long lLngCurrFinYearId) throws Exception {
		List lLstLastSchedule = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		Long lLngNxtYearId = lLngCurrFinYearId + 1l;
		try {
			lSBQuery.append("SELECT monthId,finYearId");
			lSBQuery.append(" FROM MstGpfMonthly");
			lSBQuery
					.append(" WHERE gpfAccNo = :gpfAccNo AND status != 'X' AND ((finYearId = :finYearId AND monthId>3) OR (finYearId = :nxtFinYearId AND monthId<=3)) ORDER BY finYearId DESC,monthId DESC LIMIT 1");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
			lQuery.setParameter("finYearId", lLngCurrFinYearId);
			lQuery.setParameter("nxtFinYearId", lLngNxtYearId);
			lLstLastSchedule = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception in getLastScheduleData of GPFChangeSubscriptionDAOImpl  : ", e);			
		}
		return lLstLastSchedule;
	}
}
