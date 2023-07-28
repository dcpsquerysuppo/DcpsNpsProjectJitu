/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Sep 26, 2011		Vrajesh Raval								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.report;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAO;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportColumnVO;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.eis.valueobject.HrEisScaleMst;
import com.tcs.sgv.pensionpay.dao.CommonPensionDAOImpl;
import com.tcs.sgv.pensionpay.report.PensionpayQueryDAO;
import com.tcs.sgv.pensionpay.report.PensionpayQueryDAOImpl;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillDtls;

/**
 * Class Description -
 * 
 * 
 * @author Vrajesh Raval
 * @version 0.1
 * @since JDK 5.0 Sep 26, 2011
 */
public class NewPensionAcListReport extends DefaultReportDataFinder {

	private static final Logger gLogger = Logger.getLogger("PensionpayReports");
	private PensionpayQueryDAO gObjRptQueryDAO = null;
	ServiceLocator serviceLocator = null;
	SessionFactory gObjSessionFactory = null;
	String gStrLocCode = null;
	Long gLngLangId = null;
	Map lMapSeriesHeadCode = null;
	Session ghibSession = null;
	Long locationId = null;

	/**
	 * Global Variable for Resource Bundle
	 */

	public Collection findReportData(ReportVO lObjReport, Object criteria)
			throws ReportException {

		List lLstDataList = new ArrayList();
		Map lMapRequestAttributes = null;
		Map lMapSessionAttributes = null;
		LoginDetails lObjLoginVO = null;
		try {
			lMapRequestAttributes = (Map) ((Map) criteria)
					.get(IReportConstants.REQUEST_ATTRIBUTES);
			lMapSessionAttributes = (Map) ((Map) criteria)
					.get(IReportConstants.SESSION_KEYS);
			lObjLoginVO = (LoginDetails) lMapSessionAttributes
					.get("loginDetails");
			serviceLocator = (ServiceLocator) lMapRequestAttributes
					.get("serviceLocator");
			gObjSessionFactory = serviceLocator.getSessionFactorySlave();
			gStrLocCode = lObjLoginVO.getLocation().getLocationCode();
			gLngLangId = lObjLoginVO.getLangId();
			ghibSession = gObjSessionFactory.getCurrentSession();
			gObjRptQueryDAO = new PensionpayQueryDAOImpl(null,
					gObjSessionFactory);

			Map sessionKeys = (Map) ((Map) criteria)
					.get(IReportConstants.SESSION_KEYS);
			Map loginDetail = (HashMap) sessionKeys.get("loginDetailsMap");

			locationId = (Long) loginDetail.get("locationId");

			// gObjRptQueryDAO.setReportHeaderAndFooter(lObjReport, lObjLoginVO,
			// gStrLocCode);

			if (lObjReport.getReportCode().equals("700055")) {
				lLstDataList = getNewPensionRep(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("700060")) {
				lLstDataList = getNewPensionRepForTreasury(lObjReport,
						gStrLocCode);
			}
		} catch (Exception e) {
			gLogger.info("findReportData(): Exception is" + e, e);
			e.printStackTrace();
		}
		return lLstDataList;
	}

	public List getNewPensionRep(ReportVO lObjReport, String lStrLocCode) {

		List dataList = new ArrayList();
		List rowList = null;
		new HashMap<BigDecimal, String>();
		new CommonPensionDAOImpl(gObjSessionFactory);
		List lLstResult = new ArrayList();
		int lIntSerialNo = 0;
		String lStrLetter = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		ReportVO RptVoForGroupBy = null;
		String lStrOption = null;
		try {

			lStrOption = (String) lObjReport
					.getParameterValue("optionSelected");

			// if ("A".equals(lStrOption) || "L".equals(lStrOption)) {
			lStrLetter = (String) lObjReport.getParameterValue("letter");
			String lStrTreasuryCode = (String) lObjReport
					.getParameterValue("treasuryCode");
			lLstResult = getAllEmpData(lStrLetter, lStrTreasuryCode);
			// }
			ReportsDAO reportsDao = new ReportsDAOImpl();
			RptVoForGroupBy = reportsDao.getReport("700055", lObjReport
					.getLangId(), lObjReport.getLocId());
			ReportColumnVO[] lArrReportColumnVOs = RptVoForGroupBy
					.getReportColumns();
			if ("A".equals(lStrOption) || "L".equals(lStrOption)) {

				for (int i = 0; i < lArrReportColumnVOs.length; i++) {
					if ("Treasury code".equals(lArrReportColumnVOs[i]
							.getColumnName())) {
						lArrReportColumnVOs[i].setGroupByOrder(0);
					}
				}

			} else {
				for (int i = 0; i < lArrReportColumnVOs.length; i++) {
					if ("Treasury code".equals(lArrReportColumnVOs[i]
							.getColumnName())) {
						lArrReportColumnVOs[i].setGroupByOrder(1);
					}
				}
			}
			RptVoForGroupBy.setReportColumns(lArrReportColumnVOs);
			int flag = 0;
			Iterator lObjIterator = lLstResult.iterator();
			String lStrTempDdoCode = "";
			while (lObjIterator.hasNext()) {
				Object[] lObj = (Object[]) lObjIterator.next();
				if (lObj[1] != null && lObj[8] != null) {
					rowList = new ArrayList();
					String lStrDdocode = lObj[8].toString();
					
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
					if(lObj[0] != null)
					{
						rowList.add(lObj[0].toString());
					}
					else
					{
						rowList.add("");
					}
					if(lObj[1] != null)
					{
						rowList.add(lObj[1].toString());
					}
					else
					{
						rowList.add("");
					}
					if(lObj[2] != null)
					{
						rowList.add(lObj[2].toString());
					}
					else
					{
						rowList.add("");
					}
					if(lObj[3] != null)
					{
						rowList.add(sdf.parse(sdf.format(lObj[3])));
					}
					else
					{
						rowList.add("");
					}
					if(lObj[4] != null)
					{
						rowList.add(sdf.parse(sdf.format(lObj[4])));
					}
					else
					{
						rowList.add("");
					}
					if(lObj[5] != null)
					{
						rowList.add(lObj[5].toString());
					}
					else
					{
						rowList.add("");
					}
					if(lObj[6] != null)
					{
						rowList.add(lObj[6].toString());
					}
					else
					{
						rowList.add("");
					}
					if (lObj[7] != null) {
						BigInteger lBgIntSevarthID = (BigInteger) lObj[7];
						Long lLngSevarthId = lBgIntSevarthID.longValue();
						rowList.add(getPayScale(lLngSevarthId));
					} else {
						rowList.add("");
					}
					if (lObj[8] != null) {
						rowList.add(lObj[8].toString());
					} else {
						rowList.add("");
					}
					dataList.add(rowList);
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is :" + e);
			e.printStackTrace();
		}
		return dataList;
	}

	public List getNewPensionRepForTreasury(ReportVO lObjReport,
			String lStrLocCode) {

		List dataList = new ArrayList();
		List rowList = null;
		new HashMap<BigDecimal, String>();
		new CommonPensionDAOImpl(gObjSessionFactory);
		List lLstResult = new ArrayList();
		int lIntSerialNo = 0;
		String lStrLetter = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		ReportVO RptVoForGroupBy = null;
		String lStrOption = null;

		try {

			lStrOption = (String) lObjReport
					.getParameterValue("optionSelected");

			// if ("A".equals(lStrOption) || "L".equals(lStrOption)) {
			lStrLetter = (String) lObjReport.getParameterValue("letter");
			String lStrTreasuryCode = locationId.toString();
			lLstResult = getAllEmpData(lStrLetter, lStrTreasuryCode);
			// }
			ReportsDAO reportsDao = new ReportsDAOImpl();
			RptVoForGroupBy = reportsDao.getReport("700060", lObjReport
					.getLangId(), lObjReport.getLocId());
			ReportColumnVO[] lArrReportColumnVOs = RptVoForGroupBy
					.getReportColumns();
			if ("A".equals(lStrOption) || "L".equals(lStrOption)) {

				for (int i = 0; i < lArrReportColumnVOs.length; i++) {
					if ("DDO code".equals(lArrReportColumnVOs[i]
							.getColumnName())) {
						lArrReportColumnVOs[i].setGroupByOrder(0);
					}
				}

			} else {
				for (int i = 0; i < lArrReportColumnVOs.length; i++) {
					if ("DDO code".equals(lArrReportColumnVOs[i]
							.getColumnName())) {
						lArrReportColumnVOs[i].setGroupByOrder(1);
					}
				}
			}
			RptVoForGroupBy.setReportColumns(lArrReportColumnVOs);
			int flag = 0;
			Iterator lObjIterator = lLstResult.iterator();
			String lStrTempDdoCode = "";
			while (lObjIterator.hasNext()) {
				Object[] lObj = (Object[]) lObjIterator.next();
				if (lObj[1] != null && lObj[8] != null) {
					rowList = new ArrayList();
					String lStrDdocode = lObj[8].toString();

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
					rowList.add(lObj[2].toString());
					rowList.add(sdf.parse(sdf.format(lObj[3])));
					rowList.add(sdf.parse(sdf.format(lObj[4])));
					rowList.add(lObj[5].toString());
					rowList.add(lObj[6].toString());
					if (lObj[7] != null) {
						BigInteger lBgIntSevarthID = (BigInteger) lObj[7];
						Long lLngSevarthId = lBgIntSevarthID.longValue();
						rowList.add(getPayScale(lLngSevarthId));
					} else {
						rowList.add("");
					}
					if (lObj[8] != null) {
						rowList.add(lObj[8].toString());
					} else {
						rowList.add("");
					}
					dataList.add(rowList);
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is :" + e);
			e.printStackTrace();
		}
		return dataList;
	}

	public List getAllEmpData(String lStrLetter, String lStrTreasuryCode)
			throws Exception {

		List<TrnPensionBillDtls> lLstResult = null;
		StringBuffer lSBQuery = null;

		try {
			lSBQuery = new StringBuffer(
					"SELECT em.emp_name,em.dcps_id,SUBSTR(gender,1,1),em.dob,em.doj,em.emp_group,dm.dsgn_name,em.org_emp_mst_id,em.ddo_code \n");
			lSBQuery.append("FROM mst_dcps_emp em, org_designation_mst dm, org_emp_mst oem \n");
			lSBQuery.append("WHERE dm.dsgn_id = em.designation AND em.ORG_EMP_MST_ID = oem.EMP_ID \n");
			if (!"".equals(lStrLetter)) {
				lSBQuery.append("AND UPPER(oem.EMP_FNAME) LIKE :empFirstLetter \n");
			}
			if (!"".equals(lStrTreasuryCode)) {
				lSBQuery
						.append("AND SUBSTR(em.ddo_code,1,4) = :treasuryCode \n");
			}
			lSBQuery.append("ORDER BY em.ddo_code \n");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			if (!"".equals(lStrLetter) && !(lStrLetter.length() == 0)) {

				lQuery.setString("empFirstLetter", lStrLetter.trim() + "%");
			}
			if (!"".equals(lStrTreasuryCode)) {
				lQuery.setString("treasuryCode", lStrTreasuryCode.trim());
			}

			lLstResult = lQuery.list();

		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
			throw e;
		}
		return lLstResult;
	}

	public ArrayList getRadioOptions(String lStrLangId, String lStrLocationcode)
			throws Exception {

		ArrayList arrTbillList = new ArrayList();
		try {
			// Rs in Thousand,Rs in Lacs,Rs in Crore,Absolute Amount(Rs.)
			ComboValuesVO vo = new ComboValuesVO();
			vo.setId("L");
			vo.setDesc("Last Name Starting with ");

			ComboValuesVO vo1 = new ComboValuesVO();
			vo1.setId("A");
			vo1.setDesc("All Employees");

			ComboValuesVO vo2 = new ComboValuesVO();
			vo2.setId("T");
			vo2.setDesc("Treasury Wise");

			arrTbillList.add(vo);
			arrTbillList.add(vo1);
			arrTbillList.add(vo2);

		} catch (Exception e) {
			throw e;
		}
		return arrTbillList;
	}

	public List getAllTreasuries(String optionSelected, String lStrLangId,
			String lStrLocId) {

		List<Object> lArrTreasuries = new ArrayList<Object>();
		try {

			Session lObjSession = ServiceLocator.getServiceLocator()
					.getSessionFactory().getCurrentSession();
			String lStrBufLang = "SELECT locId, locName FROM CmnLocationMst WHERE departmentId = 100003";
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

	public HrEisScaleMst getPayScaleData(Long empId) {

		HrEisScaleMst hrOtherInfo = new HrEisScaleMst();
		Session hibSession = ServiceLocator.getServiceLocator()
				.getSessionFactory().getCurrentSession();
		String query1 = "select  empLookup.hrEisSgdMpg.hrEisScaleMst  from HrEisOtherDtls as empLookup where empLookup.hrEisEmpMst.orgEmpMst.empId = "
				+ empId;
		Query sqlQuery1 = hibSession.createQuery(query1);

		if (sqlQuery1.list().size() < 1) {
			hrOtherInfo = null;
		} else {
			hrOtherInfo = (HrEisScaleMst) sqlQuery1.uniqueResult();
			gLogger.info("setting sqlQuery's uniqueResult");
		}

		return hrOtherInfo;
	}

	private String getPayScale(Long lLngSevarthId) {

		HrEisScaleMst lObjHrEisScaleMst = null;
		lObjHrEisScaleMst = getPayScaleData(lLngSevarthId);
		StringBuffer scaleDisp = new StringBuffer("");

		if (lObjHrEisScaleMst != null) {

			if (lObjHrEisScaleMst.getHrPayCommissionMst().getId() == 2500341) {
				scaleDisp.append(lObjHrEisScaleMst.getScaleStartAmt());
				scaleDisp.append("-");
				scaleDisp.append(lObjHrEisScaleMst.getScaleEndAmt());
				scaleDisp.append(" (");
				scaleDisp.append(lObjHrEisScaleMst.getScaleGradePay());
				scaleDisp.append(")");

			} else {
				scaleDisp.append(lObjHrEisScaleMst.getScaleStartAmt());
				scaleDisp.append("-");
				scaleDisp.append(lObjHrEisScaleMst.getScaleIncrAmt());
				scaleDisp.append("-");
				scaleDisp.append(lObjHrEisScaleMst.getScaleEndAmt());
				if (lObjHrEisScaleMst.getScaleHigherIncrAmt() > 0
						&& lObjHrEisScaleMst.getScaleHigherEndAmt() > 0) {
					scaleDisp.append("-");
					scaleDisp.append(lObjHrEisScaleMst.getScaleHigherIncrAmt());
					scaleDisp.append("-");
					scaleDisp.append(lObjHrEisScaleMst.getScaleHigherEndAmt());
					if (lObjHrEisScaleMst.getScaleSecondHigherIncrAmt() > 0
							&& lObjHrEisScaleMst.getScaleSecondHigherEndAmt() > 0) {
						scaleDisp.append("-");
						scaleDisp.append(lObjHrEisScaleMst
								.getScaleSecondHigherIncrAmt());
						scaleDisp.append("-");
						scaleDisp.append(lObjHrEisScaleMst
								.getScaleSecondHigherEndAmt());

						if (lObjHrEisScaleMst.getScaleThirdHigherIncrAmt() > 0
								&& lObjHrEisScaleMst
										.getScaleThirdHigherEndAmt() > 0) {
							scaleDisp.append("-");
							scaleDisp.append(lObjHrEisScaleMst
									.getScaleThirdHigherIncrAmt());
							scaleDisp.append("-");
							scaleDisp.append(lObjHrEisScaleMst
									.getScaleThirdHigherEndAmt());
						}
					}
				}
			}

		}
		return (scaleDisp.toString());
	}
}
