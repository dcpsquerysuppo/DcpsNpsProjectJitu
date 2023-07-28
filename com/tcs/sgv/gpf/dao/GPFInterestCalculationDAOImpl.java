/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 					Meeta Thacker								
 *******************************************************************************
 */
package com.tcs.sgv.gpf.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.gpf.valueobject.MstGpfArrearsYearly;

/**
 * Class Description -
 * 
 * 
 * @author Meeta Thacker
 * @version 0.1
 * @since JDK 5.0
 */
public class GPFInterestCalculationDAOImpl extends GenericDaoHibernateImpl implements GPFInterestCalculationDAO {
	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;
	Date gDtCurrDt = SessionHelper.getCurDate();

	public GPFInterestCalculationDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFInterestCalculationDAO#getEmpDtls(java.lang.String
	 * )
	 */
	public List getEmpDtls(String lStrSevaarthID, String lStrDdoCode) {
		StringBuilder lSBQuery = new StringBuilder();
		List lEmpDtlsList = new ArrayList();
		try {
			lSBQuery.append("select MGE.name,MEA.gpfAccNo,DO.dcpsDdoOfficeName,MEA.currentBalance");
			lSBQuery.append(" FROM MstEmp MGE,DdoOffice DO,MstEmpGpfAcc MEA");
			lSBQuery.append(" WHERE MEA.sevaarthId=:SevaarthID AND MGE.dcpsEmpId=MEA.mstGpfEmpId AND MGE.dcpsOrGpf='N' AND MGE.currOff = DO.dcpsDdoOfficeIdPk AND MGE.group ='D' ");
			lSBQuery.append(" AND MEA.ddoCode = :ddoCode ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("SevaarthID", lStrSevaarthID);
			lQuery.setParameter("ddoCode", lStrDdoCode);
			lEmpDtlsList = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception in getEmpDtls of GPFInterestCalculationDAOImpl  : ", e);
		}
		return lEmpDtlsList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFInterestCalculationDAO#getOfficeDtls(java.lang
	 * .String)
	 */
	public List getOfficeDtls(String lStrDdoCode) {
		StringBuilder lSBQuery = new StringBuilder();
		List lOfficeList = new ArrayList();
		List<Object> lLstReturnList = null;
		try {
			lSBQuery.append("SELECT dcpsDdoOfficeIdPk,dcpsDdoOfficeName");
			lSBQuery.append(" FROM DdoOffice WHERE dcpsDdoCode=:DdoCode");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("DdoCode", lStrDdoCode);
			lOfficeList = lQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			if (lOfficeList != null && lOfficeList.size() != 0) {
				lLstReturnList = new ArrayList<Object>();
				Object obj[];
				for (int liCtr = 0; liCtr < lOfficeList.size(); liCtr++) {
					obj = (Object[]) lOfficeList.get(liCtr);
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
			logger.error("Exception in getOfficeDtls of GPFInterestCalculationDAOImpl  : ", e);
		}
		return lLstReturnList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFInterestCalculationDAO#getEmpDtlsUsingOfficeId
	 * (java.lang.String, java.lang.Long, java.lang.Long, java.lang.Float)
	 */
	public List getEmpDtlsUsingOfficeId(String lStrOfficeId, Long lLngGroupId, Long lLngYearId, Float lFltInterestRate) {
		StringBuilder lSBQuery = new StringBuilder();
		List lEmpDtlsList = new ArrayList();
		Long lLngNxtYearId = lLngYearId + 1l;
		try {
			lSBQuery
					.append("select MGY.mstGpfYearlyId,MEA.sevaarthId,MEA.gpfAccNo,MGE.name,MEA.currentBalance,(SUM(MGM.closingBalance)*:interestRate/1200),MGY.flatInterest");
			lSBQuery.append(" FROM MstEmp MGE,MstEmpGpfAcc MEA,CmnLookupMst CLM,MstGpfYearly MGY,MstGpfMonthly MGM");
			lSBQuery
					.append(" WHERE MGE.currOff = :OfficeId AND CLM.lookupId = :GroupId AND MGE.group = CLM.lookupName AND MGE.dcpsEmpId=MEA.mstGpfEmpId AND MGE.dcpsOrGpf='N' AND MGE.group ='D'");
			lSBQuery
					.append(" AND MGY.gpfAccNo=MEA.gpfAccNo AND MGY.finYearId = :YearId AND ((MGM.finYearId = :finYearId AND MGM.monthId>3) OR (MGM.finYearId = :nxtFinYearId AND MGM.monthId<=3)) AND MGM.gpfAccNo=MEA.gpfAccNo");
			lSBQuery
					.append(" AND CLM.lookupName=MGE.group AND MGM.status != 'X' GROUP BY MEA.gpfAccNo,MGY.mstGpfYearlyId,MEA.sevaarthId,MGE.name,MEA.currentBalance,MGY.flatInterest");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("interestRate", lFltInterestRate.doubleValue());
			lQuery.setParameter("OfficeId", lStrOfficeId);
			lQuery.setParameter("GroupId", lLngGroupId);
			lQuery.setParameter("YearId", lLngYearId);
			lQuery.setParameter("finYearId", lLngYearId);
			lQuery.setParameter("nxtFinYearId", lLngNxtYearId);
			lEmpDtlsList = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception in getEmpDtlsUsingOfficeId of GPFInterestCalculationDAOImpl  : ", e);
		}
		return lEmpDtlsList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFInterestCalculationDAO#getInterestRateForGroup
	 * (java.lang.Long, java.lang.String)
	 */
	public Float getInterestRateForGroup(Long lLngGroupId, String lStrFinYear) {

		StringBuilder lSBQuery = new StringBuilder();
		Float lFltInterestRate = 0f;
		Query lQuery = null;
		String lStrFromDate = "01/04/" + lStrFinYear;
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

		try {
			lSBQuery
					.append("SELECT INTEREST FROM mst_gpf_interest_rate WHERE :fromDate >= effective_from_date AND (:fromDate < applicable_to_date OR applicable_to_date IS NULL ) AND emp_group =:groupId");

			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("fromDate", lObjSimpleDateFormat.parse(lStrFromDate));
			lQuery.setParameter("groupId", lLngGroupId);
			lFltInterestRate = Float.parseFloat(lQuery.uniqueResult().toString());

		} catch (Exception e) {
			logger.error("Exception in getInterestRateForGroup of GPFInterestCalculationDAOImpl  : ", e);
		}
		return lFltInterestRate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFInterestCalculationDAO#getInterestRateForSingleEmp
	 * (java.lang.Long, java.lang.String)
	 */
	public Float getInterestRateForSingleEmp(Long lLngGroupId, String lStrCurrDate) {

		StringBuilder lSBQuery = new StringBuilder();
		Float lFltInterestRate = 0f;
		Query lQuery = null;

		try {
			lSBQuery
					.append("SELECT interest FROM mst_gpf_interest_rate WHERE :currDate >= effective_from_date AND (:currDate < applicable_to_date OR applicable_to_date IS NULL ) AND emp_group =:groupId");

			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("currDate", lStrCurrDate);
			lQuery.setParameter("groupId", lLngGroupId);
			lFltInterestRate = Float.parseFloat(lQuery.uniqueResult().toString());

		} catch (Exception e) {
			logger.error("Exception in getInterestRateForSingleEmp of GPFInterestCalculationDAOImpl  : ", e);
		}
		return lFltInterestRate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFInterestCalculationDAO#getPkValue(java.lang.String
	 * , java.lang.Long)
	 */
	public Long getPkValue(String lStrGpfAccNo, Long lLngYearId) {
		StringBuilder lSBQuery = new StringBuilder();
		List lLstGpfYearly = new ArrayList();
		Long lLngPkValue = null;
		try {
			lSBQuery.append("select mstGpfYearlyId");
			lSBQuery.append(" FROM MstGpfYearly WHERE gpfAccNo = :GpfAccNo");
			lSBQuery.append(" AND finYearId = :YearId");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("GpfAccNo", lStrGpfAccNo);
			lQuery.setParameter("YearId", lLngYearId);
			lLstGpfYearly = lQuery.list();
			if (lLstGpfYearly != null && lLstGpfYearly.size() > 0) {
				lLngPkValue = (Long) lLstGpfYearly.get(0);
			}
		} catch (Exception e) {
			logger.error("Exception in getPkValue of GPFInterestCalculationDAOImpl  : ", e);
		}
		return lLngPkValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFInterestCalculationDAO#getCurrentInterestRateForEmp
	 * (java.lang.String, java.lang.String)
	 */
	public Float getCurrentInterestRateForEmp(String lStrSevaarthId, String lStrCurrDate) {
		StringBuilder lSBQuery = new StringBuilder();
		Float lFltInterestRate = 0f;
		Query lQuery = null;
		try {
			lSBQuery
					.append("SELECT interest FROM mst_gpf_interest_rate MI,mst_dcps_emp MGE,mst_emp_gpf_acc MGA,CMN_LOOKUP_MST CLM");
			lSBQuery
					.append(" WHERE MGE.emp_group = CLM.LOOKUP_NAME AND CLM.LOOKUP_ID=MI.EMP_GROUP AND MGA.mst_gpf_emp_id = MGE.dcps_emp_id");
			lSBQuery
					.append(" AND MGE.dcps_or_gpf = 'N' AND MGE.emp_group = 'D' AND MGA.sevaarth_id = :sevaarthId AND :currDate >= MI.effective_from_date");
			lSBQuery.append(" AND (:currDate < MI.applicable_to_date OR MI.applicable_to_date IS NULL )");

			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("currDate", lStrCurrDate);
			lQuery.setParameter("sevaarthId", lStrSevaarthId);
			lFltInterestRate = Float.parseFloat(lQuery.uniqueResult().toString());

		} catch (Exception e) {
			logger.error("Exception in getCurrentInterestRateForEmp of GPFInterestCalculationDAOImpl  : ", e);
		}
		return lFltInterestRate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFInterestCalculationDAO#getArrearYearlyIdForFinYear
	 * (java.lang.String, java.lang.Long)
	 */
	public MstGpfArrearsYearly getArrearYearlyIdForFinYear(String lStrGpfAccNo, Long lLngYearId) {
		StringBuilder lSBQuery = new StringBuilder();
		List lLstGpfArrears = new ArrayList();
		MstGpfArrearsYearly lObjMstGpfArrearsYearly = null;
		try {
			lSBQuery.append("FROM MstGpfArrearsYearly");
			lSBQuery.append(" WHERE gpfAccNo = :GpfAccNo AND yearId=:YearId");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("GpfAccNo", lStrGpfAccNo);
			lQuery.setParameter("YearId", lLngYearId);
			lLstGpfArrears = lQuery.list();
			if (lLstGpfArrears != null && lLstGpfArrears.size() > 0) {
				lObjMstGpfArrearsYearly = (MstGpfArrearsYearly) lLstGpfArrears.get(0);
			}
		} catch (Exception e) {
			logger.error("Exception in getArrearYearlyIdForFinYear of GPFInterestCalculationDAOImpl  : ", e);
		}
		return lObjMstGpfArrearsYearly;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFInterestCalculationDAO#getArrearBalance(java.lang
	 * .String, java.lang.Long)
	 */
	public Double getArrearBalance(String lStrGpfAccNo, Long lLngYearId) {
		StringBuilder lSBQuery = new StringBuilder();
		List lLstGpfArrears = new ArrayList();
		Double lDblArrBalance = 0d;
		try {
			lSBQuery.append("SELECT closeBalance FROM MstGpfArrearsYearly");
			lSBQuery.append(" WHERE gpfAccNo = :GpfAccNo AND yearId=:YearId");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("GpfAccNo", lStrGpfAccNo);
			lQuery.setParameter("YearId", lLngYearId);
			lLstGpfArrears = lQuery.list();
			if (lLstGpfArrears != null && lLstGpfArrears.size() > 0) {
				lDblArrBalance = (Double) lLstGpfArrears.get(0);
			}
		} catch (Exception e) {
			logger.error("Exception in getArrearBalance of GPFInterestCalculationDAOImpl  : ", e);
		}
		return lDblArrBalance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFInterestCalculationDAO#getMonthlyTotalForInterestCalc
	 * (java.lang.String, java.lang.Long)
	 */
	public Double getMonthlyTotalForInterestCalc(String lStrGpfAccNo, Long lLngYearId) {
		StringBuilder lSBQuery = new StringBuilder();
		List lLstMonthly = new ArrayList();
		Double lDblMonthlyTotal = null;
		Long lLngNxtFinYearId = lLngYearId + 1l;
		try {
			lSBQuery.append("SELECT AVG(MGM.closingBalance)");
			lSBQuery.append(" FROM MstGpfMonthly MGM");
			lSBQuery
					.append(" WHERE MGM.gpfAccNo = :gpfAccNo AND((MGM.finYearId = :finYearId AND MGM.monthId>3) OR (MGM.finYearId = :nxtFinYearId AND MGM.monthId<=3))");
			lSBQuery.append(" AND status != 'X' ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
			lQuery.setParameter("finYearId", lLngYearId);
			lQuery.setParameter("nxtFinYearId", lLngNxtFinYearId);
			lLstMonthly = lQuery.list();
			if (lLstMonthly != null && lLstMonthly.size() > 0) {
				lDblMonthlyTotal = (Double) lLstMonthly.get(0);
			}
		} catch (Exception e) {
			logger.error("Exception in getMonthlyTotalForInterestCalc of GPFInterestCalculationDAOImpl  : ", e);
		}
		return lDblMonthlyTotal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFInterestCalculationDAO#getYearlyIdForFinYear(java
	 * .lang.String, java.lang.Long)
	 */
	public Long getYearlyIdForFinYear(String lStrGpfAccNo, Long lLngYearId) {
		StringBuilder lSBQuery = new StringBuilder();
		List lLstGpfYearly = new ArrayList();
		Long lLngYearlyId = null;

		try {
			lSBQuery.append("Select mstGpfYearlyId ");
			lSBQuery.append("FROM MstGpfYearly ");
			lSBQuery.append("WHERE gpfAccNo = :GpfAccNo AND finYearId=:YearId");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("GpfAccNo", lStrGpfAccNo);
			lQuery.setParameter("YearId", lLngYearId);
			lLstGpfYearly = lQuery.list();
			if (lLstGpfYearly != null && lLstGpfYearly.size() > 0) {
				lLngYearlyId = (Long) lLstGpfYearly.get(0);
			}
		} catch (Exception e) {
			logger.error("Exception in getYearlyIdForFinYear of GPFInterestCalculationDAOImpl  : ", e);
		}
		return lLngYearlyId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFInterestCalculationDAO#getFinYearIdByFinCode(java
	 * .lang.String)
	 */
	public Long getFinYearIdByFinCode(String lStrFinCode) {
		Long lLngFinId = null;
		StringBuilder lSBQuery = new StringBuilder();

		try {
			lSBQuery.append("Select finYearId from SgvcFinYearMst ");
			lSBQuery.append(" WHERE finYearCode = :finCode");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("finCode", lStrFinCode);
			lLngFinId = (Long) lQuery.list().get(0);
		} catch (Exception e) {
			logger.error("Exception in getFinYearIdByFinCode of GPFInterestCalculationDAOImpl  : ", e);
		}

		return lLngFinId;
	}

	public Double getMissingCreditsPreviousYears(String lStrGpfAccNo, Long lLngYearId) {
		StringBuilder lSBQuery = new StringBuilder();
		List lLstMissingCredit = new ArrayList();
		Double lDblMissingCredits = 0d;
		try {
			lSBQuery
					.append("SELECT MGM.REGULAR_SUBSCRIPTION+MGM.ADVANCE_RECOVERY,MGM.FIN_YEAR_ID,MGM.MONTH_ID,INTR.INTEREST"
							+ " FROM MST_GPF_MISSING_CREDIT MGM, MST_GPF_INTEREST_RATE INTR where MGM.GPF_ACC_NO=:GpfAccNo and MGM.STATUS='S' "
							+ " and MGM.FIN_YEAR_ID <= :finYearId and INTR.EMP_GROUP=700061");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setParameter("GpfAccNo", lStrGpfAccNo);
			lQuery.setParameter("finYearId", lLngYearId);

			lLstMissingCredit = lQuery.list();

			if (lLstMissingCredit != null && lLstMissingCredit.size() > 0) {
				for (Integer index = 0; index < lLstMissingCredit.size(); index++) {
					// calculate interest for missing credits
				}
			}
		} catch (Exception e) {
			logger.error("Exception in getMissingCreditsPreviousYears of GPFInterestCalculationDAOImpl  : ", e);
		}

		return lDblMissingCredits;
	}
	
	public List getMissingCreditsToApprove(String lStrGpfAccNo, Long lLngYearId) {
		StringBuilder lSBQuery = new StringBuilder();
		List lLstMissingCredit = new ArrayList();
		Double lDblMissingCredits = 0d;
		try {
			lSBQuery
					.append("SELECT MST_GPF_MISSING_CREDIT_ID FROM MST_GPF_MISSING_CREDIT where GPF_ACC_NO=:GpfAccNo and STATUS='S' "
							+ " and FIN_YEAR_ID <= :finYearId");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setParameter("GpfAccNo", lStrGpfAccNo);
			lQuery.setParameter("finYearId", lLngYearId);

			lLstMissingCredit = lQuery.list();			
		} catch (Exception e) {
			logger.error("Exception in getMissingCreditsToApprove of GPFInterestCalculationDAOImpl  : ", e);
		}

		return lLstMissingCredit;
	}
	
	public String isRateExistForPeriod(Long lLngGroupId, String lStrEffectFromDate) 
	{
		StringBuilder lSBQuery = new StringBuilder();		
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		List lLstData = null;
		String lStrIsExist = "N";

		try {
			lSBQuery
					.append("SELECT INTEREST FROM mst_gpf_interest_rate WHERE :effctFromDate >= effective_from_date AND (:effctFromDate < applicable_to_date OR applicable_to_date IS NULL ) AND emp_group =:groupId");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("effctFromDate", lObjSimpleDateFormat.parse(lStrEffectFromDate));
			lQuery.setParameter("groupId", lLngGroupId);
			lLstData = lQuery.list();
			
			if(lLstData != null && lLstData.size() > 0){
				lStrIsExist = "Y";
			}

		} catch (Exception e) {
			logger.error("Exception in getInterestRateForGroup of GPFInterestCalculationDAOImpl  : ", e);
		}
		return lStrIsExist;
	}
}
