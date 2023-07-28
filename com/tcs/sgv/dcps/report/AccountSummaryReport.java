/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Sep 29, 2011		Vrajesh Raval								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.report;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.URLData;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;
import com.tcs.sgv.pensionpay.dao.CommonPensionDAOImpl;
import com.tcs.sgv.pensionpay.report.PensionpayQueryDAO;
import com.tcs.sgv.pensionpay.report.PensionpayQueryDAOImpl;


/**
 * Class Description -
 * 
 * 
 * @author Vrajesh Raval
 * @version 0.1
 * @since JDK 5.0 Sep 29, 2011
 */
public class AccountSummaryReport extends DefaultReportDataFinder {

	private static final Logger gLogger = Logger.getLogger("AccountSummaryReport");
	private PensionpayQueryDAO gObjRptQueryDAO = null;
	ServiceLocator serviceLocator = null;
	SessionFactory gObjSessionFactory = null;
	String gStrLocCode = null;
	Long gLngLangId = null;
	Map lMapSeriesHeadCode = null;
	Session ghibSession = null;
	Long locationId = null;
	List lArrReportData = null;
	Long gLngPostId = null;
	/**
	 * Global Variable for Resource Bundle
	 */
	public Collection findReportData(ReportVO lObjReport, Object criteria) throws ReportException {

		List lLstDataList = new ArrayList();
		Map lMapRequestAttributes = null;
		Map lMapSessionAttributes = null;
		LoginDetails lObjLoginVO = null;
		try {
			lMapRequestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
			lMapSessionAttributes = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
			lObjLoginVO = (LoginDetails) lMapSessionAttributes.get("loginDetails");
			serviceLocator = (ServiceLocator) lMapRequestAttributes.get("serviceLocator");
			gObjSessionFactory = serviceLocator.getSessionFactorySlave();
			gStrLocCode = lObjLoginVO.getLocation().getLocationCode();
			gLngLangId = lObjLoginVO.getLangId();
			ghibSession = gObjSessionFactory.getCurrentSession();
			gObjRptQueryDAO = new PensionpayQueryDAOImpl(null, gObjSessionFactory);

			Map sessionKeys = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
			Map loginDetail = (HashMap) sessionKeys.get("loginDetailsMap");

			locationId = (Long) loginDetail.get("locationId");

			lMapRequestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
			lMapSessionAttributes = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
			lObjLoginVO = (LoginDetails) lMapSessionAttributes.get("loginDetails");
			gStrLocCode = lObjLoginVO.getLocation().getLocationCode();
			gLngLangId = lObjLoginVO.getLangId();
			gLngPostId = lObjLoginVO.getLoggedInPost().getPostId();

			// gObjRptQueryDAO.setReportHeaderAndFooter(lObjReport, lObjLoginVO,
			// gStrLocCode);

			if (lObjReport.getReportCode().equals("700061")) {
				lLstDataList = getAccountSummary(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("700062")) {
				lLstDataList = getAccountSummaryForTreasury(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("700063")) {
				lLstDataList = getAccountSummaryForDDO(lObjReport, gStrLocCode);
			}
		} catch (Exception e) {
			gLogger.info("findReportData(): Exception is" + e, e);
			e.printStackTrace();
		}
	
		return lLstDataList;
	}

	public List getAccountSummary(ReportVO lObjReport, String lStrLocCode) {

		List dataList = new ArrayList();
		List rowList = null;
		new HashMap<BigDecimal, String>();
		new CommonPensionDAOImpl(gObjSessionFactory);
		List lLstResult = new ArrayList();
		int lIntSerialNo = 0;
		String lStrTreasuryCode = "";
		String lStrYearId = "";
		new SimpleDateFormat("dd/MM/yyyy");
		try {
			lStrYearId = (String) lObjReport.getParameterValue("yearId");
			lStrTreasuryCode = (String) lObjReport.getParameterValue("tresuryList");
			lLstResult = getAllEmpData(lStrYearId, lStrTreasuryCode);
			
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(SgvcFinYearMst.class,
						serviceLocator.getSessionFactory());
			
			String urlPrefix = "ifms.htm?actionFlag=reportService&reportCode=700097&action=generateReport&DirectReport=TRUE&displayOK=TRUE";

			int flag = 0;
			Iterator lObjIterator = lLstResult.iterator();
			String lStrTempDdoCode = "";
			Double lLngEmpAmnt = 0.0;
			while (lObjIterator.hasNext()) {

				Object[] lObj = (Object[]) lObjIterator.next();
				if (lObj[0] != null) {
					rowList = new ArrayList();
					String lStrDdocode = lObj[10].toString();
					String lStrDcpsEmpId = lObj[11].toString();

					if (flag == 0) {
						lStrTempDdoCode = lStrDdocode;
						flag = 1;
					} else {
						if (!(lStrTempDdoCode.equals(lStrDdocode))) {
							lIntSerialNo = 0;
							lStrTempDdoCode = lStrDdocode;
						}
					}

					++lIntSerialNo;
					rowList.add(lIntSerialNo);
					rowList.add(lObj[0].toString());
					//rowList.add(lObj[1].toString());
					
					//For employee name clickable
					rowList.add(new URLData(lObj[1].toString(), urlPrefix
							+ "&treasuryCode=" + lStrTreasuryCode
							+ "&ddoCode=" + lStrDdocode + "&dcpsEmpId=" + lStrDcpsEmpId
							+ "&yearId=" + lStrYearId ));
					
					if (lObj[2] != null) {
						lLngEmpAmnt = (Double) lObj[2];
						rowList.add(lLngEmpAmnt.longValue());
					} else {
						rowList.add("");
					}
					if (lObj[3] != null) {
						lLngEmpAmnt = (Double) lObj[3];
						rowList.add(lLngEmpAmnt.longValue());
					} else {
						rowList.add("");
					}
					if (lObj[4] != null) {
						lLngEmpAmnt = (Double) lObj[4];
						rowList.add(lLngEmpAmnt.longValue());
					} else {
						rowList.add("");
					}
					if (lObj[5] != null) {
						lLngEmpAmnt = (Double) lObj[5];
						rowList.add(lLngEmpAmnt.longValue());
					} else {
						rowList.add("");
					}
					if (lObj[6] != null) {
						lLngEmpAmnt = (Double) lObj[6];
						rowList.add(lLngEmpAmnt.longValue());
					} else {
						rowList.add("");
					}
					if (lObj[7] != null) {
						lLngEmpAmnt = (Double) lObj[7];
						rowList.add(lLngEmpAmnt.longValue());
					} else {
						rowList.add("");
					}
					if (lObj[8] != null) {
						lLngEmpAmnt = (Double) lObj[8];
						rowList.add(lLngEmpAmnt.longValue());
					} else {
						rowList.add("");
					}
					if (lObj[9] != null) {
						lLngEmpAmnt = (Double) lObj[9];
						rowList.add(lLngEmpAmnt.longValue());
					} else {
						rowList.add("");
					}
					
					if (lObj[10] != null) {
						
						String lStrDDOName = lObjDcpsCommonDAO.getDdoNameForCode(lStrDdocode);
						rowList.add("(" + lStrDdocode + ")" + space(2) + lStrDDOName);
						
					} else {
						rowList.add("");
					}
					
					dataList.add(rowList);
				}
			}
		} catch (Exception e) {
			gLogger.info("findReportData(): Exception is" + e, e);
			e.printStackTrace();
		}
		return dataList;
	}

	public List getAccountSummaryForTreasury(ReportVO lObjReport, String lStrLocCode) {

		List dataList = new ArrayList();
		List rowList = null;
		new HashMap<BigDecimal, String>();
		new CommonPensionDAOImpl(gObjSessionFactory);
		List lLstResult = new ArrayList();
		int lIntSerialNo = 0;
		String lStrTreasuryCode = "";
		String lStrYearId = "";
		
		try {
			
			lStrYearId = (String) lObjReport.getParameterValue("yearId");
			lStrTreasuryCode = locationId.toString();
			lLstResult = getAllEmpData(lStrYearId, lStrTreasuryCode);
			
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serviceLocator.getSessionFactory());

			int flag = 0;
			Iterator lObjIterator = lLstResult.iterator();
			String lStrTempDdoCode = "";
			Double lLngEmpAmnt = 0.0;
			while (lObjIterator.hasNext()) {

				Object[] lObj = (Object[]) lObjIterator.next();
				
				if (lObj[0] != null) {
					
					rowList = new ArrayList();
					String lStrDdocode = null;
					
					if(lObj[10] != null && !"".equals(lObj[10]))
					{
						lStrDdocode = lObj[10].toString();
					}

					if (flag == 0) {
						lStrTempDdoCode = lStrDdocode;
						flag = 1;
					} else {
						if (!(lStrTempDdoCode.equals(lStrDdocode))) {
							lIntSerialNo = 0;
							lStrTempDdoCode = lStrDdocode;
						}
					}

					++lIntSerialNo;
					rowList.add(lIntSerialNo);
					rowList.add(lObj[0].toString());
					rowList.add(lObj[1].toString());
					if (lObj[2] != null) {
						lLngEmpAmnt = (Double) lObj[2];
						rowList.add(lLngEmpAmnt.longValue());
					} else {
						rowList.add("");
					}
					if (lObj[3] != null) {
						lLngEmpAmnt = (Double) lObj[3];
						rowList.add(lLngEmpAmnt.longValue());
					} else {
						rowList.add("");
					}
					if (lObj[4] != null) {
						lLngEmpAmnt = (Double) lObj[4];
						rowList.add(lLngEmpAmnt.longValue());
					} else {
						rowList.add("");
					}
					if (lObj[5] != null) {
						lLngEmpAmnt = (Double) lObj[5];
						rowList.add(lLngEmpAmnt.longValue());
					} else {
						rowList.add("");
					}
					if (lObj[6] != null) {
						lLngEmpAmnt = (Double) lObj[6];
						rowList.add(lLngEmpAmnt.longValue());
					} else {
						rowList.add("");
					}
					if (lObj[7] != null) {
						lLngEmpAmnt = (Double) lObj[7];
						rowList.add(lLngEmpAmnt.longValue());
					} else {
						rowList.add("");
					}
					if (lObj[8] != null) {
						lLngEmpAmnt = (Double) lObj[8];
						rowList.add(lLngEmpAmnt.longValue());
					} else {
						rowList.add("");
					}
					if (lObj[9] != null) {
						lLngEmpAmnt = (Double) lObj[9];
						rowList.add(lLngEmpAmnt.longValue());
					} else {
						rowList.add("");
					}
					
					if(lStrDdocode != null)
					{
						String lStrDDOName = lObjDcpsCommonDAO.getDdoNameForCode(lStrDdocode);
						rowList.add("(" + lStrDdocode + ")" + space(2) + lStrDDOName);
					}
				
					dataList.add(rowList);
				}
			}
		} catch (Exception e) {
			gLogger.info("findReportData(): Exception is" + e, e);
			e.printStackTrace();
		}
		return dataList;
	}

	public List getAccountSummaryForDDO(ReportVO lObjReport, String lStrLocCode) {

		List dataList = new ArrayList();
		List rowList = null;
		new HashMap<BigDecimal, String>();
		new CommonPensionDAOImpl(gObjSessionFactory);
		List lLstResult = new ArrayList();
		int lIntSerialNo = 0;
		String lStrYearId = "";
		new SimpleDateFormat("dd/MM/yyyy");

		DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serviceLocator.getSessionFactory());

		String lStrDDOCode = lObjDcpsCommonDAO.getDdoCodeForDDO(gLngPostId);
		try {
			lStrYearId = (String) lObjReport.getParameterValue("yearId");

			lLstResult = getAllEmpDataForDDO(lStrYearId, lStrDDOCode);

			int flag = 0;
			Iterator lObjIterator = lLstResult.iterator();
			String lStrTempDdoCode = "";
			Double lLngEmpAmnt = 0.0;
			while (lObjIterator.hasNext()) {

				Object[] lObj = (Object[]) lObjIterator.next();
				if (lObj[0] != null) {
					rowList = new ArrayList();
					String lStrDdocode = lObj[10].toString();

					if (flag == 0) {
						lStrTempDdoCode = lStrDdocode;
						flag = 1;
					} else {
						if (!(lStrTempDdoCode.equals(lStrDdocode))) {
							lIntSerialNo = 0;
							lStrTempDdoCode = lStrDdocode;
						}
					}

					++lIntSerialNo;
					rowList.add(lIntSerialNo);
					rowList.add(lObj[0].toString());
					rowList.add(lObj[1].toString());
					if (lObj[2] != null) {
						lLngEmpAmnt = (Double) lObj[2];
						rowList.add(lLngEmpAmnt.longValue());
					} else {
						rowList.add("");
					}
					if (lObj[3] != null) {
						lLngEmpAmnt = (Double) lObj[3];
						rowList.add(lLngEmpAmnt.longValue());
					} else {
						rowList.add("");
					}
					if (lObj[4] != null) {
						lLngEmpAmnt = (Double) lObj[4];
						rowList.add(lLngEmpAmnt.longValue());
					} else {
						rowList.add("");
					}
					if (lObj[5] != null) {
						lLngEmpAmnt = (Double) lObj[5];
						rowList.add(lLngEmpAmnt.longValue());
					} else {
						rowList.add("");
					}
					if (lObj[6] != null) {
						lLngEmpAmnt = (Double) lObj[6];
						rowList.add(lLngEmpAmnt.longValue());
					} else {
						rowList.add("");
					}
					if (lObj[7] != null) {
						lLngEmpAmnt = (Double) lObj[7];
						rowList.add(lLngEmpAmnt.longValue());
					} else {
						rowList.add("");
					}
					if (lObj[8] != null) {
						lLngEmpAmnt = (Double) lObj[8];
						rowList.add(lLngEmpAmnt.longValue());
					} else {
						rowList.add("");
					}
					if (lObj[9] != null) {
						lLngEmpAmnt = (Double) lObj[9];
						rowList.add(lLngEmpAmnt.longValue());
					} else {
						rowList.add("");
					}

					dataList.add(rowList);
				}
			}
		} catch (Exception e) {
			gLogger.info("findReportData(): Exception is" + e, e);
			e.printStackTrace();
		}
		return dataList;
	}

	public List getAllEmpData(String lStrYearId, String lStrTreasuryCode) throws Exception {

		List lLstResult = null;
		StringBuffer lSBQuery = null;

		try {
			lSBQuery = new StringBuffer("SELECT EM.dcps_id, EM.emp_name, YR.Open_employee, YR.open_employer, YR.contrib_employee, "
					+ "YR.contrib_employer, YR.Int_Contrb_employee, YR.Int_Contrb_employer, YR.close_employee, YR.close_employer,EM.ddo_code,EM.dcps_emp_id \n");
			lSBQuery.append("FROM mst_dcps_emp EM,rlt_ddo_org RL, mst_dcps_contribution_yearly YR \n");
			lSBQuery.append("WHERE EM.ddo_code = RL.ddo_code \n");
			lSBQuery.append("AND EM.dcps_id = Yr.dcps_id \n");
			lSBQuery.append("AND RL.Location_code = :treasuryCode \n");
			lSBQuery.append("AND YR.year_id = :yearId \n");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setString("treasuryCode", lStrTreasuryCode);
			lQuery.setParameter("yearId", lStrYearId);
			lLstResult = lQuery.list();

		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
			throw e;
		}
		return lLstResult;
	}

	public List getAllEmpDataForDDO(String lStrYearId, String lStrDDOCode) throws Exception {

		List lLstResult = null;
		StringBuffer lSBQuery = null;

		try {
			lSBQuery = new StringBuffer("SELECT EM.dcps_id, EM.emp_name, YR.Open_employee, YR.open_employer, YR.contrib_employee, "
					+ "YR.contrib_employer, YR.Int_Contrb_employee, YR.Int_Contrb_employer, YR.close_employee, YR.close_employer,EM.ddo_code \n");
			lSBQuery.append("FROM mst_dcps_emp EM, mst_dcps_contribution_yearly YR \n");
			lSBQuery.append("WHERE EM.ddo_code =:ddoCode  \n");
			lSBQuery.append("AND EM.dcps_id = Yr.dcps_id \n");
			lSBQuery.append("AND YR.year_id = :yearId \n");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setString("ddoCode", lStrDDOCode);
			lQuery.setParameter("yearId", lStrYearId);
			lLstResult = lQuery.list();

		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
			throw e;
		}
		return lLstResult;
	}

	public List getYear(String lStrLangId, String lStrLocId) {

		List<Object> lArrYears = new ArrayList<Object>();
		try {
			Session lObjSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();

			String lStrBufLang = "SELECT finYearId, finYearDesc FROM SgvcFinYearMst WHERE langId = :langId and finYearCode BETWEEN '2007' AND '2015' ORDER BY finYearCode";

			Query lObjQuery = lObjSession.createQuery(lStrBufLang);
			lObjQuery.setString("langId", lStrLangId);

			List lLstResult = lObjQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			Object[] lArrData = null;

			if (lLstResult != null && !lLstResult.isEmpty()) {
				for (int lIntCtr = 0; lIntCtr < lLstResult.size(); lIntCtr++) {
					lObjComboValuesVO = new ComboValuesVO();
					lArrData = (Object[]) lLstResult.get(lIntCtr);
					lObjComboValuesVO.setId(lArrData[0].toString());
					lObjComboValuesVO.setDesc(lArrData[1].toString());
					lArrYears.add(lObjComboValuesVO);
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
		}

		return lArrYears;
	}

	public List getAllTreasuries(String lStrLangId, String lStrLocId) {

		List<Object> lArrTreasuries = new ArrayList<Object>();
		try {

			Session lObjSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			String lStrBufLang = "SELECT locId, locName FROM CmnLocationMst WHERE departmentId = 100003 order by locName ";
			Query lObjQuery = lObjSession.createQuery(lStrBufLang);

			List lLstResult = lObjQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			Object[] lArrData = null;

			if (lLstResult != null && !lLstResult.isEmpty()) {
				for (int lIntCtr = 0; lIntCtr < lLstResult.size(); lIntCtr++) {
					lObjComboValuesVO = new ComboValuesVO();
					lArrData = (Object[]) lLstResult.get(lIntCtr);
					lObjComboValuesVO.setId(lArrData[0].toString());
					lObjComboValuesVO.setDesc(lArrData[1].toString());
					lArrTreasuries.add(lObjComboValuesVO);
				}
			}

		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
		}
		return lArrTreasuries;
	}
	
	public String space(int noOfSpace) {
		String blank = "";
		for (int i = 0; i < noOfSpace; i++) {
			blank += "\u00a0";
		}
		return blank;
	}
}
