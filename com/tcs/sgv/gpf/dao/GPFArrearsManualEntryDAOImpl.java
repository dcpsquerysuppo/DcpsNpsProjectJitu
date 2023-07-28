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
 * @author 397138
 * 
 */
public class GPFArrearsManualEntryDAOImpl extends GenericDaoHibernateImpl implements GPFArrearsManualEntryDAO {
	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	public GPFArrearsManualEntryDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.gpf.dao.GPFArrearsManualEntryDAO#getMonths()
	 */
	public List getMonths() {
		StringBuilder lSBQuery = new StringBuilder();
		List lMonthList = new ArrayList();
		List<Object> lLstReturnList = null;
		try {
			lSBQuery.append("select monthId,monthName FROM SgvaMonthMst WHERE monthId < 13");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lMonthList = lQuery.list();
			ComboValuesVO lObjComboValuesVO = null;

			if (lMonthList != null && lMonthList.size() != 0) {
				lLstReturnList = new ArrayList<Object>();
				Object obj[];
				for (int liCtr = 0; liCtr < lMonthList.size(); liCtr++) {
					obj = (Object[]) lMonthList.get(liCtr);
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
			logger.error("Exception in getMonths of GPFArrearsManualEntryDAOImpl  : ", e);			
		}
		return lLstReturnList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.gpf.dao.GPFArrearsManualEntryDAO#getYears()
	 */
	public List getYears() {
		StringBuilder lSBQuery = new StringBuilder();
		List lYearList = new ArrayList();
		List<Object> lLstReturnList = null;
		try {
			lSBQuery
					.append("select finYearId,finYearCode from SgvcFinYearMst where finYearCode between '2008' and '2015'");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lYearList = lQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			if (lYearList != null && lYearList.size() != 0) {
				lLstReturnList = new ArrayList<Object>();
				Object obj[];
				for (int liCtr = 0; liCtr < lYearList.size(); liCtr++) {
					obj = (Object[]) lYearList.get(liCtr);
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
			logger.error("Exception in getYears of GPFArrearsManualEntryDAOImpl  : ", e);			
		}
		return lLstReturnList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFArrearsManualEntryDAO#getEmpArrearsDtls(java.lang
	 * .String[], java.lang.String)
	 */
	public List getEmpArrearsDtls(String[] lStrArrEmpGroup, String lStrDdoCode) {
		StringBuilder lSBQuery = new StringBuilder();
		List lEmpArrearsList = new ArrayList();
		try {
			// query to get the employee arrear details (group wise)
			lSBQuery.append("select MEA.sevaarth_Id,MGE.emp_name,MEA.gpf_Acc_No,");
			lSBQuery
					.append(" coalesce((select max(MGA.INSTALLMENT_NO) from mst_gPF_arrears_yearly MGA where MGA.GPF_ACC_NO=MEA.GPF_ACC_NO),0)+1 ");
			lSBQuery.append(" FROM Mst_Emp_Gpf_Acc MEA,Mst_Dcps_Emp MGE");
			lSBQuery.append(" WHERE MEA.mst_Gpf_Emp_Id=MGE.dcps_Emp_Id AND MGE.dcps_or_gpf = 'N' ");
			lSBQuery.append(" AND MGE.emp_group IN(:EmpGroup) AND MEA.ddo_Code=:ddoCode ");
			// lSBQuery.append(" AND MGY.installmentNo = (SELECT MAX(installmentNo) FROM MstGpfArrearsYearly WHERE gpfAccNo=MEA.gpfAccNo)");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameterList("EmpGroup", lStrArrEmpGroup);
			lQuery.setParameter("ddoCode", lStrDdoCode);

			lEmpArrearsList = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception in getEmpArrearsDtls of GPFArrearsManualEntryDAOImpl  : ", e);			
		}
		return lEmpArrearsList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFArrearsManualEntryDAO#getNextInstallmentNo(java
	 * .lang.String)
	 */
	public List getNextInstallmentNo(String lStrGpfAccNo) {
		StringBuilder lSBQuery = new StringBuilder();
		List lEmpArrearsList = new ArrayList();
		try {
			// query to get the next arrear installment number for single
			// employee
			lSBQuery.append("SELECT MAX(installmentNo),yearId ");
			lSBQuery.append(" FROM MstGpfArrearsYearly");
			lSBQuery.append(" WHERE gpfAccNo=:gpfAccNo GROUP BY yearId ORDER BY 2 DESC LIMIT 1");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);

			lEmpArrearsList = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception in getNextInstallmentNo of GPFArrearsManualEntryDAOImpl  : ", e);			
		}
		return lEmpArrearsList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFArrearsManualEntryDAO#getEmpDtls(java.lang.String,
	 * java.lang.String)
	 */
	public List getEmpDtls(String lStrSevaarthID, String lStrDdoCode) {
		StringBuilder lSBQuery = new StringBuilder();
		List lEmpAccountList = new ArrayList();
		try {
			// get the details of selected employee(For single employee)
			lSBQuery.append("select MGE.name,MEA.gpfAccNo,DO.dcpsDdoOfficeName");
			lSBQuery.append(" FROM MstEmp MGE,DdoOffice DO,MstEmpGpfAcc MEA");
			lSBQuery
					.append(" WHERE MEA.sevaarthId=:SevaarthID AND MGE.dcpsEmpId=MEA.mstGpfEmpId AND MGE.dcpsOrGpf='N' AND MGE.currOff = DO.dcpsDdoOfficeIdPk AND MEA.ddoCode=:ddoCode AND MGE.group ='D'");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("SevaarthID", lStrSevaarthID);
			lQuery.setParameter("ddoCode", lStrDdoCode);
			lEmpAccountList = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception in getEmpDtls of GPFArrearsManualEntryDAOImpl  : ", e);			
		}
		return lEmpAccountList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFArrearsManualEntryDAO#getPkValue(java.lang.String,
	 * java.lang.Long)
	 */
	public Long getPkValue(String lStrGpfAccNo, Long lLngYearId) {
		StringBuilder lSBQuery = new StringBuilder();
		List lLstArrearsYearly = new ArrayList();
		Long lLngPkValue = null;
		try {
			lSBQuery.append("select gpfArrearsYearlyId");
			lSBQuery.append(" FROM MstGpfArrearsYearly");
			lSBQuery.append(" WHERE gpfAccNo=:GpfAccNo AND yearId = :YearId");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("GpfAccNo", lStrGpfAccNo);
			lQuery.setParameter("YearId", lLngYearId);
			lLstArrearsYearly = lQuery.list();
			if (lLstArrearsYearly != null && lLstArrearsYearly.size() > 0) {
				lLngPkValue = (Long) lLstArrearsYearly.get(0);
			}
		} catch (Exception e) {
			logger.error("Exception in getPkValue of GPFArrearsManualEntryDAOImpl  : ", e);			
		}
		return lLngPkValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFArrearsManualEntryDAO#getPreviousCloseBal(java
	 * .lang.String, java.lang.Integer)
	 */
	public Double getPreviousCloseBal(String lStrGpfAccNo, Integer lIntInstNo) {
		StringBuilder lSBQuery = new StringBuilder();
		List lLstArrearsYearly = new ArrayList();
		Double lLngCloseBal = 0d;
		try {
			lSBQuery.append("SELECT closeBalance ");
			lSBQuery.append(" FROM MstGpfArrearsYearly");
			lSBQuery.append(" WHERE gpfAccNo=:gpfAccNo AND installmentNo = :instNo");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
			lQuery.setParameter("instNo", lIntInstNo);
			lLstArrearsYearly = lQuery.list();
			if (lLstArrearsYearly != null && lLstArrearsYearly.size() > 0) {
				lLngCloseBal = (Double) lLstArrearsYearly.get(0);
			}
		} catch (Exception e) {
			logger.error("Exception in getPreviousCloseBal of GPFArrearsManualEntryDAOImpl  : ", e);			
		}
		return lLngCloseBal;
	}
}
