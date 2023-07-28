/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Aug 16, 2011		Shivani Rana								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.report;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.business.reports.ReportDataFinder;
import com.tcs.sgv.common.constant.DBConstants;
import com.tcs.sgv.common.dao.CommonDAO;
import com.tcs.sgv.common.dao.CommonDAOImpl;
import com.tcs.sgv.common.dao.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.URLData;
import com.tcs.sgv.common.valueobject.MstBank;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionTransferDtls;
import com.tcs.sgv.pensionpay.dao.CommonPensionDAO;
import com.tcs.sgv.pensionpay.dao.CommonPensionDAOImpl;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAOImpl;
import com.tcs.sgv.pensionpay.valueobject.HstCommutationDtls;
import com.tcs.sgv.pensionpay.valueobject.HstPnsnPmntDcrgDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionerRivisionDtls;

/**
 * Class Description -
 * 
 * 
 * @author 365450
 * @version 0.1
 * @since JDK 5.0 Aug 16, 2011
 */
public class PensionpayReportDAOImpl extends DefaultReportDataFinder implements ReportDataFinder {

	private static final Logger gLogger = Logger.getLogger("PensionpayReports");
	private PensionpayQueryDAO gObjRptQueryDAO = null;
	ServiceLocator serviceLocator = null;
	SessionFactory gObjSessionFactory = null;
	String gStrLocCode = null;
	Long gLngLangId = null;
	Map lMapSeriesHeadCode = null;
	Session ghibSession = null;

	/**
	 * Global Variable for Resource Bundle
	 */
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");
	
	private ResourceBundle gObjRsrcBndleMarathi = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants_ma");

	private ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionConstants");
	
	private ResourceBundle gObjLblRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels");

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

			gObjRptQueryDAO.setReportHeaderAndFooter(lObjReport, lObjLoginVO);

			if (lObjReport.getReportCode().equals("365455")) {
				lLstDataList = getPensionCaseTrackingReport(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365458")) {
				lLstDataList = getPensionAllocationReportData(lObjReport, gLngLangId, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365456")) {
				lLstDataList = getBillTrackingReport(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365462")) {
				lLstDataList = getBankBranchWisePensionerCount(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365463")) {
				lLstDataList = getPensionerCountMonthWise(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365464")) {
				lLstDataList = getArrearPaymentDtls(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365466")) {
				lLstDataList = getSixPayArrearDtls(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365465")) {
				lLstDataList = getRecoveryReport(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365467")) {
				lLstDataList = getFirstPmntCases(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365469")) {
				lLstDataList = getArchivedCases(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365470")) {
				lLstDataList = getSchemeWisePaymentDtls(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365471")) {
				lLstDataList = getBankWisePaymentDtls(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365472")) {
				lLstDataList = getBranchWisePaymentDtls(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365473")) {
				lLstDataList = getAGFirstPayStatement(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365475")) {
				lLstDataList = getCVPPaymentDtls(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365476")) {
				lLstDataList = getDCRGPaymentDtls(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365477")) {
				lLstDataList = getConsolidatedAuditorwiseRpt(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365478")) {
				lLstDataList = getChangeStatementRpt(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365479")) {
				lLstDataList = getRevisedAgCasesRpt(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365480")) {
				lLstDataList = getRegisterOfPpoReceivedRpt(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365481")) {
				lLstDataList = getRegisterOfGratuityOrderReceivedRpt(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365482")) {
				lLstDataList = getRegisterOfCommutationOrderReceivedRpt(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365483")) {
				lLstDataList = getChangeInTrsryRpt(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365484")) {
				lLstDataList = getOverPaymentRecoveryRpt(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365485")) {
				lLstDataList = getStatePensionStatistics(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365486")) {
				lLstDataList = getTrsryWiseStatePensionStatistics(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365487")) {
				lLstDataList = getPnsnTypeWisePensionStatistics(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365488")) {
				lLstDataList = getDeptWiseAgingAnalysisRpt(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365489")) {
				lLstDataList = getTrsryDeptWiseAgingAnalysisRpt(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365490")) {
				lLstDataList = getPnsnrTrsryDeptWiseAgingAnalysisRpt(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365491")) {
				lLstDataList = getTrsryWiseAgingAnalysisRpt(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365492")) {
				lLstDataList = getTrsryPnsnrWiseAgingAnalysisRpt(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365493")) {
				lLstDataList = getCasesUploadedByAGRpt(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365494")) {
				lLstDataList = getTrsrywiseCasesUploadedByAGRpt(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365495")) {
				lLstDataList = getTotalNonIdentifiedPnsnrRpt(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365496")) {
				lLstDataList = getTrsryWiseNonIdentifiedPnsnrRpt(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365497")) {
				lLstDataList = getPnsnrWiseNonIdentifiedPnsnrRpt(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365498")) {
				lLstDataList = getTrsryWiseRevisedPpoByAGRpt(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365499")) {
				lLstDataList = getPnsnrWiseRevisedPpoByAGRpt(lObjReport, gStrLocCode);
			}
			if (lObjReport.getReportCode().equals("365500")) {
				lLstDataList = getPayComWisePensionerDtls(lObjReport, gStrLocCode);
			}
		} catch (Exception e) {
			gLogger.info("findReportData(): Exception is" + e, e);
		}
		return lLstDataList;
	}

	/**
	 * 
	 * 
	 * 
	 * <H3>Method to get allocation report data -</H3>
	 * 
	 * @author 365450
	 * @param lObjReport
	 * @param lLngLangId
	 * @param lStrLocCode
	 * @return
	 */
	public List getPensionAllocationReportData(ReportVO lObjReport, Long lLngLangId, String lStrLocCode) {

		String lStrBranchCode = null;
		String lStrHeadCode = null;
		String lStrForMonth = null;
		String lStrForYear = null;
		Integer lIntMonthYear = null;
		List<TrnPensionBillDtls> lLstRptData = null;
		List dataList = new ArrayList();
		List rowList = null;
		Double lDblTotalAllocAmount = 0.0;
		Map<BigDecimal, String> lMapHeadCodeSeries = new HashMap<BigDecimal, String>();
		CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(gObjSessionFactory);
		try {
			lStrForMonth = (String) lObjReport.getParameterValue("forMonth");
			lStrForYear = (String) lObjReport.getParameterValue("forYear");
			lStrBranchCode = (String) lObjReport.getParameterValue("branchCode");
			lStrHeadCode = (String) lObjReport.getParameterValue("headCode");
			lMapHeadCodeSeries = lObjCommonPensionDAO.getAllHeadCodeSeriesMap();
			String lStrMonthYear = null;
			if (Integer.parseInt(lStrForMonth) < 10) {
				lStrMonthYear = lStrForYear + "0" + lStrForMonth;
			} else {
				lStrMonthYear = lStrForYear + lStrForMonth;
			}
			if (lStrMonthYear != null) {
				lIntMonthYear = Integer.valueOf(lStrMonthYear);
			}

			gObjRptQueryDAO = new PensionpayQueryDAOImpl(TrnPensionBillDtls.class, gObjSessionFactory);
			lLstRptData = gObjRptQueryDAO.getPensionAllocationDetails(lIntMonthYear, lStrBranchCode, lStrHeadCode);
			if (lLstRptData != null && !lLstRptData.isEmpty()) {
				for (TrnPensionBillDtls lObjTrnPensionBillDtls : lLstRptData) {
					rowList = new ArrayList();
					rowList.add(((lObjTrnPensionBillDtls.getLedgerNo() != null) ? lObjTrnPensionBillDtls.getLedgerNo() : "") + " / "
							+ ((lObjTrnPensionBillDtls.getPageNo() != null) ? lObjTrnPensionBillDtls.getPageNo() : ""));
					rowList.add(lObjTrnPensionBillDtls.getPpoNo());
					rowList.add(lObjTrnPensionBillDtls.getPensionerName());
					rowList.add(lObjTrnPensionBillDtls.getAllcationBf1436().intValue());
					rowList.add(lObjTrnPensionBillDtls.getAllcationBf11156().intValue());
					rowList.add(lObjTrnPensionBillDtls.getAllcationAf11156().intValue());
					rowList.add(lObjTrnPensionBillDtls.getAllcationAf10560().intValue());
					rowList.add(lObjTrnPensionBillDtls.getAllcationAfZp().intValue());
					lDblTotalAllocAmount = lObjTrnPensionBillDtls.getAllcationBf1436().doubleValue() + lObjTrnPensionBillDtls.getAllcationBf11156().doubleValue()
							+ lObjTrnPensionBillDtls.getAllcationAf11156().doubleValue() + lObjTrnPensionBillDtls.getAllcationAf10560().doubleValue()
							+ lObjTrnPensionBillDtls.getAllcationAfZp().doubleValue();
					rowList.add(lDblTotalAllocAmount.intValue());
					rowList.add(lMapHeadCodeSeries.get(lObjTrnPensionBillDtls.getHeadCode()));
					dataList.add(rowList);
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is :" + e);
		}
		return dataList;
	}

	public List<ComboValuesVO> getBankList(Hashtable otherArgs, String lStrLangId, String lStrLocCode) {

		Hashtable sessionKeys = (Hashtable) (otherArgs).get(IReportConstants.SESSION_KEYS);
		Map loginMap = (Map) sessionKeys.get("loginDetailsMap");
		Long lLngLangId = null;
		if (loginMap.containsKey("langId")) {
			lLngLangId = (Long) loginMap.get("langId");
		}
		List<Object[]> lLstResult = new ArrayList<Object[]>();
		List<ComboValuesVO> lLstBanks = new ArrayList<ComboValuesVO>();
		ComboValuesVO lObjComboValueVO = null;

		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append("select mb.bankCode, mb.bankName ");
		lSBQuery.append("from MstBank mb where ");
		lSBQuery.append("mb.langId=:langId and mb.activateFlag=:activeFlag order by mb.bankName");

		try {
			ghibSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("langId", lLngLangId);
			lQuery.setParameter("activeFlag", Long.valueOf(1));

			lLstResult = lQuery.list();

			if (lLstResult != null && lLstResult.size() > 0) {
				for (Object[] lArrObj : lLstResult) {
					lObjComboValueVO = new ComboValuesVO();
					lObjComboValueVO.setId(lArrObj[0].toString());
					lObjComboValueVO.setDesc(lArrObj[1].toString());
					lLstBanks.add(lObjComboValueVO);
				}
			}

		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
		}

		return lLstBanks;
	}

	public List<ComboValuesVO> getBranchListFromBankCode(String lStrBankCode, Hashtable otherArgs, String lStrLangId, String lStrLocId) {

		Hashtable sessionKeys = (Hashtable) (otherArgs).get(IReportConstants.SESSION_KEYS);
		Map loginMap = (Map) sessionKeys.get("loginDetailsMap");
		Long lLngLangId = null;
		String lStrLocCode = "";
		if (loginMap.containsKey("langId")) {
			lLngLangId = (Long) loginMap.get("langId");
		}
		if (loginMap.containsKey("locationId")) {
			lStrLocCode = loginMap.get("locationId").toString();
		}
		List<ComboValuesVO> lLstBankBrank = new ArrayList<ComboValuesVO>();
		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = new ArrayList<Object[]>();
		ComboValuesVO lObjComboValueVO = null;
		try {
			ghibSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			lSBQuery.append("SELECT branchCode,branchName ");
			lSBQuery.append("FROM RltBankBranch  WHERE ");
			lSBQuery.append("bankCode =:bankCode AND langId =:langId AND locationCode= :locationCode order by branchName");

			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setLong("langId", lLngLangId);
			hqlQuery.setLong("bankCode", Long.valueOf(lStrBankCode));
			hqlQuery.setString("locationCode", lStrLocCode);
			hqlQuery.setCacheable(true).setCacheRegion("ecache_lookup");
			lLstResult = hqlQuery.list();
			if (lLstResult != null && lLstResult.size() > 0) {
				for (Object[] lArrObj : lLstResult) {
					lObjComboValueVO = new ComboValuesVO();
					lObjComboValueVO.setId(lArrObj[0].toString());
					lObjComboValueVO.setDesc(lArrObj[1].toString());
					lLstBankBrank.add(lObjComboValueVO);
				}
			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
		}
		return lLstBankBrank;
	}

	public List<ComboValuesVO> getAuditorList(Hashtable otherArgs, String lStrLangId, String lStrLocCode) {

		Hashtable sessionKeys = (Hashtable) (otherArgs).get(IReportConstants.SESSION_KEYS);
		Map loginMap = (Map) sessionKeys.get("loginDetailsMap");
		List<ComboValuesVO> lLstAuditors = new ArrayList<ComboValuesVO>();
		List<Object[]> lLstResult = new ArrayList<Object[]>();
		ComboValuesVO lObjComboValueVO = null;

		try {
			if (loginMap.containsKey("locationId")) {
				lStrLocCode = loginMap.get("locationId").toString();
			}

			ghibSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			StringBuffer lSBBillQry = new StringBuffer();
			lSBBillQry.append("SELECT oup.orgPostMstByPostId.postId,concat(concat(concat(concat(oem.empFname,' '),oem.empMname),' '),oem.empLname) \n");
			lSBBillQry.append(" FROM AclRoleMst arm,AclPostroleRlt apr,OrgUserpostRlt oup,OrgEmpMst oem, OrgPostMst opm \n");
			lSBBillQry.append(" WHERE arm.roleId=apr.aclRoleMst.roleId \n");

			lSBBillQry.append("AND oup.orgPostMstByPostId.postId=apr.orgPostMst.postId \n");
			lSBBillQry.append("AND oup.orgUserMst.userId = oem.orgUserMst.userId \n");
			lSBBillQry.append("AND oup.orgPostMstByPostId.postId=opm.postId\n");
			lSBBillQry.append("AND arm.roleId=:roleId \n");
			lSBBillQry.append("AND opm.locationCode=:locationCode \n");
			lSBBillQry.append("order by 1 \n");

			Query hqlQuery = ghibSession.createQuery(lSBBillQry.toString());
			hqlQuery.setLong("locationCode", Long.parseLong(lStrLocCode));
			hqlQuery.setLong("roleId", Long.parseLong(bundleConst.getString("AUDITOR.ROLEID")));
			hqlQuery.setCacheable(true).setCacheRegion("ecache_lookup");
			lLstResult = hqlQuery.list();
			if (lLstResult != null && lLstResult.size() > 0) {
				for (Object[] lArrObj : lLstResult) {
					lObjComboValueVO = new ComboValuesVO();
					lObjComboValueVO.setId(lArrObj[0].toString());
					lObjComboValueVO.setDesc(lArrObj[1].toString());
					lLstAuditors.add(lObjComboValueVO);
				}
			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
		}
		return lLstAuditors;

	}

	public List<ComboValuesVO> getPensionCategoryList(String lStrLangId, String lStrLocCode) {

		StringBuffer lSBQuery = new StringBuffer();
		List<Object[]> lLstResult = new ArrayList<Object[]>();
		List<ComboValuesVO> lLstPnsnCategory = new ArrayList<ComboValuesVO>();
		ComboValuesVO lObjComboValusVO = null;
		try {
			ghibSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			lSBQuery.append("Select \n");
			lSBQuery.append("headCode,series \n");
			lSBQuery.append("from \n");
			lSBQuery.append("MstPensionHeadcode  \n");

			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			lLstResult = hqlQuery.list();

			for (Object[] lArrObj : lLstResult) {
				lObjComboValusVO = new ComboValuesVO();
				if (lArrObj[0] != null && lArrObj[1] != null) {
					lObjComboValusVO.setId(lArrObj[0].toString());
					lObjComboValusVO.setDesc(lArrObj[1].toString());
					lLstPnsnCategory.add(lObjComboValusVO);
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
		}
		return lLstPnsnCategory;
	}

	public List<ComboValuesVO> getMonths(String lStrLangId, String lStrLocCode) throws Exception {

		List<Object[]> lLstResult = null;
		ComboValuesVO lObjComboValueVO = null;
		List<ComboValuesVO> lLstMonths = new ArrayList<ComboValuesVO>();
		try {
			ghibSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			StringBuffer lSBQuery = new StringBuffer("select monthNo,monthName from SgvaMonthMst where langId = :langId order by monthId");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("langId", lStrLangId);
			lLstResult = lQuery.list();

			if (lLstResult != null && !lLstResult.isEmpty()) {
				for (Object[] lArrObj : lLstResult) {
					if (lArrObj[0] != null && lArrObj[1] != null) {
						lObjComboValueVO = new ComboValuesVO();
						lObjComboValueVO.setId(lArrObj[0].toString());
						lObjComboValueVO.setDesc(lArrObj[1].toString());
						lLstMonths.add(lObjComboValueVO);
					}
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;
		}
		return lLstMonths;
	}

	public List<ComboValuesVO> getYears(String lStrLangId, String lStrLocCode) throws Exception {

		List<String> lLstResult = null;
		ComboValuesVO lObjComboValueVO = null;
		List<ComboValuesVO> lLstYears = new ArrayList<ComboValuesVO>();

		try {
			ghibSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			StringBuffer lSBQuery = new StringBuffer("select finYearCode from SgvcFinYearMst where langId = :lLangId order by finYearCode");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("lLangId", lStrLangId);
			lLstResult = lQuery.list();
			if (lLstResult != null && !lLstResult.isEmpty()) {
				for (String lStrYearCode : lLstResult) {
					if (lStrYearCode != null) {
						lObjComboValueVO = new ComboValuesVO();
						lObjComboValueVO.setId(lStrYearCode);
						lObjComboValueVO.setDesc(lStrYearCode);
						lLstYears.add(lObjComboValueVO);
					}
				}
			}

		} catch (Exception e) {
			gLogger.error("Exception::" + e.getMessage(), e);
			throw (e);
		}
		return lLstYears;
	}

	public List<ComboValuesVO> getFinYearDesc(String lStrLangId, String lStrLocCode) throws Exception {

		List<Object[]> lLstResult = null;
		ComboValuesVO lObjComboValueVO = null;
		List<ComboValuesVO> lLstYears = new ArrayList<ComboValuesVO>();

		try {
			ghibSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			StringBuffer lSBQuery = new StringBuffer("select finYearCode,finYearDesc from SgvcFinYearMst where langId = :lLangId");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("lLangId", lStrLangId);
			lLstResult = lQuery.list();
			
			if (lLstResult != null && !lLstResult.isEmpty()) {
				for (Object[] lArrObj : lLstResult) {
					if (lArrObj[0] != null && lArrObj[1] != null) {
						lObjComboValueVO = new ComboValuesVO();
						lObjComboValueVO.setId(lArrObj[0].toString());
						lObjComboValueVO.setDesc(lArrObj[1].toString());
						lLstYears.add(lObjComboValueVO);
					}
				}
			}

		} catch (Exception e) {
			gLogger.error("Exception::" + e.getMessage(), e);
			throw (e);
		}
		return lLstYears;
	}
	
	public List getPensionCaseTrackingReport(ReportVO lObjReport, String lStrLocationCode) {

		List lArrReturn = new ArrayList();
		gObjRptQueryDAO = new PensionpayQueryDAOImpl(TrnPensionRqstHdr.class, gObjSessionFactory);
		String lStrFromDate = null;
		String lStrToDate = null;
		String lStrTreasuryName = null;
		String lStrBankName = null;
		String lStrBranchName = null;
		String lStrPensionerName = null;
		String lStrAccountNumber = null;
		String lStrPpoNo = null;
		try {

			lStrFromDate = lObjReport.getParameterValue("InwardDatefrom").toString();
			lStrToDate = lObjReport.getParameterValue("InwardDateto").toString();

			if (!lObjReport.getParameterValue("treasuryCode").equals("-1")) {
				lStrTreasuryName = lObjReport.getParameterValue("treasuryCode").toString();
			}
			if (!lObjReport.getParameterValue("bankCode").equals("-1")) {
				lStrBankName = lObjReport.getParameterValue("bankCode").toString();
			}
			if (!lObjReport.getParameterValue("branchCode").equals("-1")) {
				lStrBranchName = lObjReport.getParameterValue("branchCode").toString();
			}
			if (!lObjReport.getParameterValue("pensionerName").equals("")) {
				lStrPensionerName = lObjReport.getParameterValue("pensionerName").toString().toUpperCase().trim();
			}
			if (!lObjReport.getParameterValue("accountNumber").equals("")) {
				lStrAccountNumber = lObjReport.getParameterValue("accountNumber").toString();
			}
			if (!lObjReport.getParameterValue("PPONo").equals("")) {
				lStrPpoNo = lObjReport.getParameterValue("PPONo").toString().trim();
			}

			lArrReturn = gObjRptQueryDAO.getPensionCaseTrackingReport(lObjReport, lStrTreasuryName, lStrFromDate, lStrToDate, lStrTreasuryName, lStrBankName, lStrBranchName,
					lStrPensionerName, lStrAccountNumber, lStrPpoNo);
		} catch (Exception e) {
			gLogger.error(e.getMessage(), e);

		}
		return lArrReturn;
	}

	/*
	 * List getBillTypeList(String lStrLangId, String lStrLocId) { List
	 * lLstBillType = new ArrayList();
	 * 
	 * try { ComboValuesVO lObjCombo1 = new ComboValuesVO(); ComboValuesVO
	 * lObjCombo2 = new ComboValuesVO(); ComboValuesVO lObjCombo3 = new
	 * ComboValuesVO(); ComboValuesVO lObjCombo4 = new ComboValuesVO();
	 * 
	 * lObjCombo1.setId("9");
	 * lObjCombo1.setDesc(gObjRsrcBndle.getString("PPMT.PENSION"));
	 * lLstBillType.add(lObjCombo1);
	 * 
	 * lObjCombo2.setId("10");
	 * lObjCombo2.setDesc(gObjRsrcBndle.getString("PPMT.CVP"));
	 * lLstBillType.add(lObjCombo2);
	 * 
	 * lObjCombo3.setId("11");
	 * lObjCombo3.setDesc(gObjRsrcBndle.getString("PPMT.DCRG"));
	 * lLstBillType.add(lObjCombo3);
	 * 
	 * lObjCombo4.setId("44");
	 * lObjCombo4.setDesc(gObjRsrcBndle.getString("PPMT.MONTHLY"));
	 * lLstBillType.add(lObjCombo4);
	 * 
	 * System.out.println("lLstBillType.size() is" + lLstBillType.size());
	 * 
	 * } catch (Exception e) {
	 * gLogger.error("In  class :::: getBillTypeList method :::: Error is  : " +
	 * e, e); }
	 * 
	 * return lLstBillType;
	 * 
	 * }
	 */
	public List getBillTrackingReport(ReportVO lObjReport, String lStrLocationCode) {

		List lArrList = new ArrayList();
		gObjRptQueryDAO = new PensionpayQueryDAOImpl(null, gObjSessionFactory);
		String lStrFromDate = null;
		String lStrToDate = null;
		String lStrBillNo = null;
		String lStrBillType = null;
		try {
			if (lObjReport.getParameterValue("BillNo") != null) {
				lStrBillNo = lObjReport.getParameterValue("BillNo").toString();
			}

			lStrFromDate = lObjReport.getParameterValue("BillDatefrom").toString();
			lStrToDate = lObjReport.getParameterValue("BillDateto").toString();
			lStrBillType = lObjReport.getParameterValue("BillType").toString();
			if (lStrFromDate.length() > 0 && lStrToDate.length() > 0 && lStrBillType.length() > 0) {
				lArrList = gObjRptQueryDAO.getBillTrackingReport(lObjReport, lStrLocationCode, lStrFromDate, lStrToDate, lStrBillNo, lStrBillType);
			}
		} catch (Exception e) {
			gLogger.error(e.getMessage(), e);

		}
		return lArrList;
	}

	public List getBillTypeList(String lStrLangId, String lStrLocId) {

		List lLstBillType = new ArrayList();
		try {
			ComboValuesVO lObjCombo1 = new ComboValuesVO();
			lObjCombo1.setId(gObjRsrcBndle.getString("PPMT.PENSION"));
			lObjCombo1.setDesc(gObjRsrcBndle.getString("PPMT.PENSION"));
			lLstBillType.add(lObjCombo1);

			ComboValuesVO lObjCombo2 = new ComboValuesVO();
			lObjCombo2.setId(gObjRsrcBndle.getString("PPMT.CVP"));
			lObjCombo2.setDesc(gObjRsrcBndle.getString("PPMT.CVP"));
			lLstBillType.add(lObjCombo2);

			ComboValuesVO lObjCombo3 = new ComboValuesVO();
			lObjCombo3.setId(gObjRsrcBndle.getString("PPMT.DCRG"));
			lObjCombo3.setDesc(gObjRsrcBndle.getString("PPMT.DCRG"));
			lLstBillType.add(lObjCombo3);

			ComboValuesVO lObjCombo4 = new ComboValuesVO();
			lObjCombo4.setId(gObjRsrcBndle.getString("PPMT.MONTHLY"));
			lObjCombo4.setDesc(gObjRsrcBndle.getString("PPMT.MONTHLY"));
			lLstBillType.add(lObjCombo4);
		} catch (Exception e) {
			gLogger.error("In  class BillTrackingReportDAOImpl :::: getBillTypeList method :::: Error is  : " + e, e);
		}
		return lLstBillType;
	}

	public List getBankBranchWisePensionerCount(ReportVO lObjReport, String lStrLocationCode) {

		List lArrList = new ArrayList();
		gObjRptQueryDAO = new PensionpayQueryDAOImpl(null, gObjSessionFactory);
		String lStrBankCode = null;
		String lStrBranchCode = null;
		List dataList = new ArrayList();
		List rowList = null;
		int lIntSerialNo = 0;

		// for Right Alignment format
		StyleVO[] RightAlignVO = new StyleVO[3];
		RightAlignVO[0] = new StyleVO();
		RightAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		RightAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
		RightAlignVO[1] = new StyleVO();
		RightAlignVO[1].setStyleId(IReportConstants.BORDER);
		RightAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
		RightAlignVO[2] = new StyleVO();
		RightAlignVO[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		RightAlignVO[2].setStyleValue("14");

		try {

			lStrBankCode = lObjReport.getParameterValue("bankCode").toString();
			lStrBranchCode = lObjReport.getParameterValue("branchCode").toString();

			if (lStrBankCode.length() > 0 && lStrBranchCode.length() > 0) {
				lArrList = gObjRptQueryDAO.getBankBranchWisePensionerCount(lStrBankCode, lStrBranchCode, lStrLocationCode);

				if (lArrList != null && !lArrList.isEmpty()) {
					Iterator it = lArrList.iterator();
					while (it.hasNext()) {
						lIntSerialNo++;
						Object[] tuple = (Object[]) it.next();
						rowList = new ArrayList();
						rowList.add(lIntSerialNo);
						rowList.add(tuple[0].toString());
						rowList.add(tuple[1].toString());
						rowList.add(tuple[2].toString());
						rowList.add(tuple[3].toString());
						rowList.add(new StyledData(tuple[4].toString(), RightAlignVO));

						dataList.add(rowList);
					}
				}
			}
		} catch (Exception e) {
			gLogger.error(e.getMessage(), e);

		}
		return dataList;
	}

	public List getPensionerCountMonthWise(ReportVO lObjReport, String lStrLocationCode) {

		List lArrList = new ArrayList();
		gObjRptQueryDAO = new PensionpayQueryDAOImpl(null, gObjSessionFactory);
		String lStrBankCode = null;
		String lStrBranchCode = null;
		String lStrForMonth = null;
		String lStrForYear = null;
		String lStrPayForMonthYear = null;
		List dataList = new ArrayList();

		List rowList = null;
		int lIntSerialNo = 0;
		Double lLngGrossAmt = null;
		Double lLngNetAmt = null;
		Double lLngRecovryAmt = null;

		// for Right Alignment format
		StyleVO[] RightAlignVO = new StyleVO[3];
		RightAlignVO[0] = new StyleVO();
		RightAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		RightAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
		RightAlignVO[1] = new StyleVO();
		RightAlignVO[1].setStyleId(IReportConstants.BORDER);
		RightAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
		RightAlignVO[2] = new StyleVO();
		RightAlignVO[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		RightAlignVO[2].setStyleValue("14");

		try {

			lStrBankCode = lObjReport.getParameterValue("bankCode").toString();
			lStrBranchCode = lObjReport.getParameterValue("branchCode").toString();
			lStrForYear = lObjReport.getParameterValue("forYear").toString();
			lStrForMonth = lObjReport.getParameterValue("forMonth").toString();
			if (lStrForMonth != null || !("").equals(lStrForMonth)) {
				if (Integer.parseInt(lStrForMonth) < 10) {
					lStrPayForMonthYear = lStrForYear + "0" + lStrForMonth;
				} else {
					lStrPayForMonthYear = lStrForYear + lStrForMonth;
				}
			}

			if (lStrBankCode.length() > 0 && lStrBranchCode.length() > 0) {
				lArrList = gObjRptQueryDAO.getPensionerCountForMonth(lStrBankCode, lStrBranchCode, lStrPayForMonthYear, lStrLocationCode);

				if (lArrList != null && !lArrList.isEmpty()) {
					Iterator it = lArrList.iterator();
					while (it.hasNext()) {
						lIntSerialNo++;
						Object[] tuple = (Object[]) it.next();

						rowList = new ArrayList();
						rowList.add(lIntSerialNo);
						rowList.add(tuple[0].toString());
						rowList.add(tuple[1].toString());
						rowList.add(tuple[2].toString());
						rowList.add(tuple[3].toString());
						rowList.add(new StyledData(tuple[4].toString(), RightAlignVO));
						if (tuple[5] != null) {

							lLngGrossAmt = Double.parseDouble(tuple[5].toString());
							// rowList.add(lLngGrossAmt);

						}
						rowList.add(new StyledData(lLngGrossAmt, RightAlignVO));
						if (tuple[6] != null) {
							lLngRecovryAmt = Double.parseDouble(tuple[6].toString());
							// rowList.add(lLngRecovryAmt);

						}
						rowList.add(new StyledData(lLngRecovryAmt, RightAlignVO));
						if (tuple[7] != null) {
							lLngNetAmt = Double.parseDouble(tuple[7].toString());
							// rowList.add(lLngNetAmt);

						}
						rowList.add(new StyledData(lLngNetAmt, RightAlignVO));
						dataList.add(rowList);
					}
				}
			}
		} catch (Exception e) {
			gLogger.error(e.getMessage(), e);

		}
		return dataList;
	}

	public List getConsolidatedAuditorwiseRpt(ReportVO lObjReport, String lStrLocationCode) {

		List lArrList = new ArrayList();
		gObjRptQueryDAO = new PensionpayQueryDAOImpl(null, gObjSessionFactory);
		String lStrAudPostId = null;
		String lStrForMonth = null;
		String lStrForYear = null;
		String lStrPayForMonthYear = null;
		List dataList = new ArrayList();

		List rowList = null;
		int lIntSerialNo = 0;
		Double lLngGrossAmt = null;
		Double lLngNetAmt = null;
		Double lLngRecovryAmt = null;

		// for Right Alignment format
		StyleVO[] RightAlignVO = new StyleVO[3];
		RightAlignVO[0] = new StyleVO();
		RightAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		RightAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
		RightAlignVO[1] = new StyleVO();
		RightAlignVO[1].setStyleId(IReportConstants.BORDER);
		RightAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
		RightAlignVO[2] = new StyleVO();
		RightAlignVO[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		RightAlignVO[2].setStyleValue("14");

		try {

			lStrAudPostId = lObjReport.getParameterValue("postId").toString();
			lStrForYear = lObjReport.getParameterValue("forYear").toString();
			lStrForMonth = lObjReport.getParameterValue("forMonth").toString();

			if (lStrForMonth != null || !("").equals(lStrForMonth)) {
				if (Integer.parseInt(lStrForMonth) < 10) {
					lStrPayForMonthYear = lStrForYear + "0" + lStrForMonth;
				} else {
					lStrPayForMonthYear = lStrForYear + lStrForMonth;
				}
			}

			if (lStrAudPostId.length() > 0 && lStrPayForMonthYear.length() > 0) {
				lArrList = gObjRptQueryDAO.getAuditorwisePensionerCountForMonth(lStrAudPostId, lStrPayForMonthYear, lStrLocationCode);

				if (lArrList != null && !lArrList.isEmpty()) {
					Iterator it = lArrList.iterator();
					while (it.hasNext()) {
						lIntSerialNo++;
						Object[] tuple = (Object[]) it.next();

						rowList = new ArrayList();
						rowList.add(lIntSerialNo);
						rowList.add(tuple[0].toString());
						rowList.add(tuple[1].toString());
						rowList.add(tuple[2].toString());
						rowList.add(tuple[3].toString());
						rowList.add(new StyledData(tuple[4].toString(), RightAlignVO));
						if (tuple[5] != null) {

							lLngGrossAmt = Double.parseDouble(tuple[5].toString());
							// rowList.add(lLngGrossAmt);

						}
						rowList.add(new StyledData(lLngGrossAmt, RightAlignVO));
						if (tuple[6] != null) {
							lLngRecovryAmt = Double.parseDouble(tuple[6].toString());
							// rowList.add(lLngRecovryAmt);

						}
						rowList.add(new StyledData(lLngRecovryAmt, RightAlignVO));
						if (tuple[7] != null) {
							lLngNetAmt = Double.parseDouble(tuple[7].toString());
							// rowList.add(lLngNetAmt);

						}
						rowList.add(new StyledData(lLngNetAmt, RightAlignVO));
						dataList.add(rowList);
					}
				}
			}
		} catch (Exception e) {
			gLogger.error(e.getMessage(), e);

		}
		return dataList;
	}

	public List getArrearPaymentDtls(ReportVO lObjReport, String lStrLocationCode) {

		List lArrList = new ArrayList();
		gObjRptQueryDAO = new PensionpayQueryDAOImpl(null, gObjSessionFactory);
		String lStrBankCode = null;
		String lStrBranchCode = null;
		String lStrForMonth = null;
		String lStrForYear = null;
		String lStrPayForMonthYear = null;
		List dataList = new ArrayList();

		List rowList = null;
		int lIntSerialNo = 0;
		Double lDbArrearAmnt = 0.0;
		Double lDbTiArrearAmnt = 0.0;
		Double lDbTotalArrearAmnt = 0.0;

		// for Right Alignment format
		StyleVO[] RightAlignVO = new StyleVO[3];
		RightAlignVO[0] = new StyleVO();
		RightAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		RightAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
		RightAlignVO[1] = new StyleVO();
		RightAlignVO[1].setStyleId(IReportConstants.BORDER);
		RightAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
		RightAlignVO[2] = new StyleVO();
		RightAlignVO[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		RightAlignVO[2].setStyleValue("14");

		try {

			lStrBankCode = lObjReport.getParameterValue("bankCode").toString();
			lStrBranchCode = lObjReport.getParameterValue("branchCode").toString();
			lStrForYear = lObjReport.getParameterValue("forYear").toString();
			lStrForMonth = lObjReport.getParameterValue("forMonth").toString();
			if (lStrForMonth != null || !("").equals(lStrForMonth)) {
				if (Integer.parseInt(lStrForMonth) < 10) {
					lStrPayForMonthYear = lStrForYear + "0" + lStrForMonth;
				} else {
					lStrPayForMonthYear = lStrForYear + lStrForMonth;
				}
			}

			if (lStrBankCode.length() > 0 && lStrBranchCode.length() > 0) {
				lArrList = gObjRptQueryDAO.getArrearDtlsBankBranchWise(lStrBankCode, lStrBranchCode, lStrPayForMonthYear, lStrLocationCode);

				if (lArrList != null && !lArrList.isEmpty()) {
					Iterator it = lArrList.iterator();
					while (it.hasNext()) {
						lIntSerialNo++;
						Object[] tuple = (Object[]) it.next();

						if (tuple[4] != null) {

							lDbArrearAmnt = Double.parseDouble(tuple[4].toString());
						}
						if (tuple[5] != null) {

							lDbTiArrearAmnt = Double.parseDouble(tuple[5].toString());
						}

						lDbTotalArrearAmnt = lDbArrearAmnt + lDbTiArrearAmnt;

						if (lDbTotalArrearAmnt > 0.0) {
							rowList = new ArrayList();
							rowList.add(lIntSerialNo);
							rowList.add(tuple[0].toString());
							rowList.add(tuple[1].toString());
							rowList.add(tuple[2].toString());
							rowList.add(tuple[3].toString());
							rowList.add(new StyledData(lDbTotalArrearAmnt, RightAlignVO));

							dataList.add(rowList);
						}
					}
				}
			}
		} catch (Exception e) {
			gLogger.error(e.getMessage(), e);

		}
		return dataList;
	}

	public List getRecoveryReport(ReportVO lObjReport, String lStrLocationCode) {

		List lArrList = new ArrayList();
		gObjRptQueryDAO = new PensionpayQueryDAOImpl(null, gObjSessionFactory);
		String lStrBankCode = null;
		String lStrBranchCode = null;
		String lStrForMonth = null;
		String lStrForYear = null;
		String lStrPayForMonthYear = null;
		String lStrSchemeCode = null;
		List dataList = new ArrayList();

		List rowList = null;
		int lIntSerialNo = 0;
		Double lDbRecovAmt = 0.0;
		// for Right Alignment format
		StyleVO[] RightAlignVO = new StyleVO[3];
		RightAlignVO[0] = new StyleVO();
		RightAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		RightAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
		RightAlignVO[1] = new StyleVO();
		RightAlignVO[1].setStyleId(IReportConstants.BORDER);
		RightAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
		RightAlignVO[2] = new StyleVO();
		RightAlignVO[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		RightAlignVO[2].setStyleValue("14");

		try {

			lStrBankCode = lObjReport.getParameterValue("bankCode").toString();
			lStrBranchCode = lObjReport.getParameterValue("branchCode").toString();
			lStrForYear = lObjReport.getParameterValue("forYear").toString();
			lStrForMonth = lObjReport.getParameterValue("forMonth").toString();
			if (lStrForMonth != null || !("").equals(lStrForMonth)) {
				if (Integer.parseInt(lStrForMonth) < 10) {
					lStrPayForMonthYear = lStrForYear + "0" + lStrForMonth;
				} else {
					lStrPayForMonthYear = lStrForYear + lStrForMonth;
				}
			}

			lStrSchemeCode = lObjReport.getParameterValue("schemeCode").toString();
			if (lStrBankCode.length() > 0 && lStrBranchCode.length() > 0) {
				lArrList = gObjRptQueryDAO.getRecoveryReport(lStrBankCode, lStrBranchCode, lStrPayForMonthYear, lStrSchemeCode, lStrLocationCode);

				if (lArrList != null && !lArrList.isEmpty()) {
					Iterator it = lArrList.iterator();
					while (it.hasNext()) {
						lIntSerialNo++;
						Object[] tuple = (Object[]) it.next();

						// if (lDbTotalArrearAmnt > 0.0) {
						rowList = new ArrayList();
						rowList.add(lIntSerialNo);
						rowList.add(tuple[0].toString());
						rowList.add(tuple[1].toString());
						rowList.add(tuple[2].toString());
						rowList.add(tuple[3].toString());
						if (tuple[4] != null) {
							lDbRecovAmt = Double.parseDouble(tuple[4].toString());
						}
						rowList.add(new StyledData(lDbRecovAmt, RightAlignVO));
						rowList.add(tuple[5].toString());
						dataList.add(rowList);
						// }
					}
				}
			}
		} catch (Exception e) {
			gLogger.error(e.getMessage(), e);

		}
		return dataList;
	}

	public List getSixPayArrearDtls(ReportVO lObjReport, String lStrLocationCode) {

		List lArrReturn = new ArrayList();
		gObjRptQueryDAO = new PensionpayQueryDAOImpl(null, gObjSessionFactory);
		String lStrBankCode = null;
		String lStrBranchCode = null;
		String lStrPpoNo = null;
		try {
			lStrBankCode = lObjReport.getParameterValue("bankCode").toString();
			lStrBranchCode = lObjReport.getParameterValue("branchCode").toString();
			if (lObjReport.getParameterValue("PpoNo") != "") {
				lStrPpoNo = lObjReport.getParameterValue("PpoNo").toString();
			}

			lArrReturn = gObjRptQueryDAO.getSixPayArrearDtls(lStrBankCode, lStrBranchCode, lStrPpoNo, lStrLocationCode);
		} catch (Exception e) {
			gLogger.error(e.getMessage(), e);

		}
		return lArrReturn;
	}

	public List getFirstPmntCases(ReportVO lObjReport, String lStrLocationCode) {

		List lArrReturn = new ArrayList();
		gObjRptQueryDAO = new PensionpayQueryDAOImpl(null, gObjSessionFactory);
		String lStrTreasuryCode = null;
		String lStrPPONo = null;
		try {
			lStrTreasuryCode = lObjReport.getParameterValue("treasuryCode").toString();
			if (lObjReport.getParameterValue("PpoNo") != "") {
				lStrPPONo = lObjReport.getParameterValue("PpoNo").toString();
			}
			if (lStrTreasuryCode.length() > 0) {
				lArrReturn = gObjRptQueryDAO.getFirstPmntCases(lStrTreasuryCode, lStrPPONo);
			}
		} catch (Exception e) {
			gLogger.error(e.getMessage(), e);

		}
		return lArrReturn;
	}

	public List getArchivedCases(ReportVO lObjReport, String lStrLocationCode) {

		List lArrReturn = new ArrayList();
		gObjRptQueryDAO = new PensionpayQueryDAOImpl(null, gObjSessionFactory);
		String lStrTreasuryCode = null;
		String lStrPPONo = null;
		try {
			lStrTreasuryCode = lObjReport.getParameterValue("treasuryCode").toString();
			if (lObjReport.getParameterValue("PpoNo") != "") {
				lStrPPONo = lObjReport.getParameterValue("PpoNo").toString();
			}
			if (lStrTreasuryCode.length() > 0) {
				lArrReturn = gObjRptQueryDAO.getArchivedCases(lStrTreasuryCode, lStrPPONo);
			}
		} catch (Exception e) {
			gLogger.error(e.getMessage(), e);

		}
		return lArrReturn;
	}

	public List<ComboValuesVO> getBankNameList(Hashtable otherArgs, String lStrLangId, String lStrLocCode) {

		Hashtable sessionKeys = (Hashtable) (otherArgs).get(IReportConstants.SESSION_KEYS);
		Map loginMap = (Map) sessionKeys.get("loginDetailsMap");
		Long lLngLangId = null;
		if (loginMap.containsKey("langId")) {
			lLngLangId = (Long) loginMap.get("langId");
		}
		List<Object[]> lLstResult = new ArrayList<Object[]>();
		List<ComboValuesVO> lLstBanks = new ArrayList<ComboValuesVO>();
		ComboValuesVO lObjComboValueVO = null;

		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append("select mb.bankCode, mb.bankName ");
		lSBQuery.append("from MstBank mb where ");
		lSBQuery.append("mb.langId=:langId and mb.activateFlag=:activeFlag order by mb.bankName");

		try {
			ghibSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("langId", lLngLangId);
			lQuery.setParameter("activeFlag", Long.valueOf(1));

			lLstResult = lQuery.list();

			if (lLstResult != null && lLstResult.size() > 0) {
				for (Object[] lArrObj : lLstResult) {
					lObjComboValueVO = new ComboValuesVO();
					lObjComboValueVO.setId(lArrObj[0].toString());
					lObjComboValueVO.setDesc(lArrObj[1].toString());
					lLstBanks.add(lObjComboValueVO);
				}

				lObjComboValueVO = new ComboValuesVO();
				lObjComboValueVO.setId("0");
				lObjComboValueVO.setDesc("All");
				lLstBanks.add(lObjComboValueVO);
			}

		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
		}

		return lLstBanks;
	}

	public List<ComboValuesVO> getBranchNameListFromBankCode(String lStrBankCode, Hashtable otherArgs, String lStrLangId, String lStrLocId) {

		Hashtable sessionKeys = (Hashtable) (otherArgs).get(IReportConstants.SESSION_KEYS);
		Map loginMap = (Map) sessionKeys.get("loginDetailsMap");
		Long lLngLangId = null;
		String lStrLocCode = "";
		if (loginMap.containsKey("langId")) {
			lLngLangId = (Long) loginMap.get("langId");
		}
		if (loginMap.containsKey("locationId")) {
			lStrLocCode = loginMap.get("locationId").toString();
		}
		List<ComboValuesVO> lLstBankBrank = new ArrayList<ComboValuesVO>();
		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = new ArrayList<Object[]>();
		ComboValuesVO lObjComboValueVO = null;
		try {
			ghibSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			if (Long.valueOf(lStrBankCode) != 0L) {
				lSBQuery.append("SELECT branchCode,branchName ");
				lSBQuery.append("FROM RltBankBranch  WHERE ");
				lSBQuery.append("bankCode =:bankCode AND langId =:langId AND locationCode= :locationCode order by branchName");

				Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
				hqlQuery.setLong("langId", lLngLangId);
				hqlQuery.setLong("bankCode", Long.valueOf(lStrBankCode));
				hqlQuery.setString("locationCode", lStrLocCode);
				hqlQuery.setCacheable(true).setCacheRegion("ecache_lookup");
				lLstResult = hqlQuery.list();
				if (lLstResult != null && lLstResult.size() > 0) {
					for (Object[] lArrObj : lLstResult) {
						lObjComboValueVO = new ComboValuesVO();
						lObjComboValueVO.setId(lArrObj[0].toString());
						lObjComboValueVO.setDesc(lArrObj[1].toString());
						lLstBankBrank.add(lObjComboValueVO);
					}
					lObjComboValueVO = new ComboValuesVO();
					lObjComboValueVO.setId("0");
					lObjComboValueVO.setDesc("All");
					lLstBankBrank.add(lObjComboValueVO);
				}
			} else {
				lObjComboValueVO = new ComboValuesVO();
				lObjComboValueVO.setId("0");
				lObjComboValueVO.setDesc("All");
				lLstBankBrank.add(lObjComboValueVO);
			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
		}
		return lLstBankBrank;
	}

	public List<ComboValuesVO> getSchemeCodeList(Hashtable otherArgs, String lStrLangId, String lStrLocCode) {

		Hashtable sessionKeys = (Hashtable) (otherArgs).get(IReportConstants.SESSION_KEYS);
		Map loginMap = (Map) sessionKeys.get("loginDetailsMap");
		Long lLngLangId = null;
		if (loginMap.containsKey("langId")) {
			lLngLangId = (Long) loginMap.get("langId");
		}
		List<Object[]> lLstResult = new ArrayList<Object[]>();
		List<ComboValuesVO> lLstSchemes = new ArrayList<ComboValuesVO>();
		ComboValuesVO lObjComboValueVO = null;

		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append("select ms.schemeCode, ms.schemeId ");
		lSBQuery.append("from MstScheme ms where ");
		lSBQuery.append("ms.langId=:langId order by ms.schemeCode");

		try {
			ghibSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("langId", lLngLangId);

			lLstResult = lQuery.list();

			if (lLstResult != null && lLstResult.size() > 0) {
				for (Object[] lArrObj : lLstResult) {
					lObjComboValueVO = new ComboValuesVO();
					lObjComboValueVO.setId(lArrObj[1].toString());
					lObjComboValueVO.setDesc(lArrObj[0].toString());
					lLstSchemes.add(lObjComboValueVO);
				}

				lObjComboValueVO = new ComboValuesVO();
				lObjComboValueVO.setId("0");
				lObjComboValueVO.setDesc("All");
				lLstSchemes.add(lObjComboValueVO);
			}

		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
		}

		return lLstSchemes;
	}

	public List<ComboValuesVO> getAllTreasury(Hashtable otherArgs, String lStrLangId, String lStrLocId) {

		Hashtable sessionKeys = (Hashtable) (otherArgs).get(IReportConstants.SESSION_KEYS);
		Map loginMap = (Map) sessionKeys.get("loginDetailsMap");
		Long lLngLangId = null;
		if (loginMap.containsKey("langId")) {
			lLngLangId = (Long) loginMap.get("langId");
		}
		List<Object[]> lLstResult = new ArrayList<Object[]>();
		List<ComboValuesVO> lLstTreasury = new ArrayList<ComboValuesVO>();
		ComboValuesVO lObjComboValueVO = null;

		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append(" SELECT locationCode,locName   ");
		lSBQuery.append(" FROM CmnLocationMst ");
		lSBQuery.append(" WHERE cmnLanguageMst.langId =:langId AND departmentId IN (100003) ");
		lSBQuery.append(" order by locName ");

		try {
			ghibSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("langId", lLngLangId);

			lLstResult = lQuery.list();

			if (lLstResult != null && lLstResult.size() > 0) {
				for (Object[] lArrObj : lLstResult) {
					lObjComboValueVO = new ComboValuesVO();
					lObjComboValueVO.setId(lArrObj[0].toString());
					lObjComboValueVO.setDesc(lArrObj[1].toString());
					lLstTreasury.add(lObjComboValueVO);
				}
			}

		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
		}

		return lLstTreasury;
	}
	
	
	public List<ComboValuesVO> getSchemeCodeForBill(Hashtable otherArgs, String lStrLangId, String lStrLocCode) {

		List<String> lLstResult = new ArrayList<String>();
		List<ComboValuesVO> lLstSchemes = new ArrayList<ComboValuesVO>();
		ComboValuesVO lObjComboValueVO = null;

		try {

			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append("select distinct schemeCode ");
			lSBQuery.append("from RltPensionHeadcodeChargable where ");
			lSBQuery.append("billType = :billType");
			ghibSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setString("billType", "PENSION");

			lLstResult = lQuery.list();

			if (lLstResult != null && lLstResult.size() > 0) {
				for (String lStrSchemeCode : lLstResult) {
					lObjComboValueVO = new ComboValuesVO();
					lObjComboValueVO.setId(lStrSchemeCode);
					lObjComboValueVO.setDesc(lStrSchemeCode);
					lLstSchemes.add(lObjComboValueVO);
				}

			}

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error(" Error is : " + e, e);
		}

		return lLstSchemes;
	}

	public List<String> getSchemeCodeListForPensionBill() {

		List<String> lLstResult = new ArrayList<String>();

		try {

			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append("select distinct schemeCode ");
			lSBQuery.append("from RltPensionHeadcodeChargable where ");
			lSBQuery.append("billType = :billType");
			ghibSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setString("billType", "PENSION");

			lLstResult = lQuery.list();

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error(" Error is : " + e, e);
		}

		return lLstResult;
	}

	public List getSchemeWisePaymentDtls(ReportVO lObjReport, String lStrLocCode) {

		String lStrSchemeCode = null;
		String lStrForMonth = null;
		String lStrForYear = null;
		List lLstRptData = null;
		List dataList = new ArrayList();
		List rowList = null;

		Double lDbGrossAmount = 0.0;
		Double lDbDeductionAmount = 0.0;
		Double lDbNetAmount = 0.0;
		List<String> lLstSchemeCode = new ArrayList<String>();
		Map<String, String> lMapSchemeCode = new HashMap<String, String>();
		try {
			lStrForMonth = (String) lObjReport.getParameterValue("forMonth");
			lStrForYear = (String) lObjReport.getParameterValue("forYear");
			lStrSchemeCode = (String) lObjReport.getParameterValue("SchemeCode");

			if (!"-1".equals(lStrSchemeCode)) {
				lLstSchemeCode.add(lStrSchemeCode);
			} else {
				lLstSchemeCode = getSchemeCodeListForPensionBill();
			}
			CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(gObjSessionFactory);
			lMapSchemeCode = lObjCommonPensionDAO.getPaymentSchemeCodeMap(lLstSchemeCode);

			String lStrMonthYear = null;
			if (Integer.parseInt(lStrForMonth) < 10) {
				lStrMonthYear = lStrForYear + "0" + lStrForMonth;
			} else {
				lStrMonthYear = lStrForYear + lStrForMonth;
			}
			StyleVO[] RightAlignVO = new StyleVO[3];
			RightAlignVO[0] = new StyleVO();
			RightAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			RightAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
			RightAlignVO[1] = new StyleVO();
			RightAlignVO[1].setStyleId(IReportConstants.BORDER);
			RightAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			RightAlignVO[2] = new StyleVO();
			RightAlignVO[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			RightAlignVO[2].setStyleValue("14");

			String urlPrefix = "ifms.htm?actionFlag=reportService&reportCode=365471&action=generateReport&DirectReport=TRUE&displayOK=TRUE";

			gObjRptQueryDAO = new PensionpayQueryDAOImpl(TrnPensionBillDtls.class, gObjSessionFactory);
			lLstRptData = gObjRptQueryDAO.getSchemeWisePaymentDtls(lStrMonthYear, lStrSchemeCode, lStrLocCode);
			if (lLstRptData != null && !lLstRptData.isEmpty()) {

				Iterator it = lLstRptData.iterator();
				while (it.hasNext()) {

					Object[] tuple = (Object[]) it.next();
					rowList = new ArrayList();
					rowList.add(new URLData(tuple[0].toString(), urlPrefix + "&forMonth=" + lStrForMonth + "&forYear=" + lStrForYear + "&schemeCode=" + tuple[0].toString()
							+ "&flag=Y"));
					rowList.add(lMapSchemeCode.get(tuple[0].toString()));
					if (tuple[1] != null) {
						lDbGrossAmount = Double.parseDouble(tuple[1].toString());
					}
					rowList.add(new StyledData(lDbGrossAmount.longValue(), RightAlignVO));
					if (tuple[2] != null) {
						lDbDeductionAmount = Double.parseDouble(tuple[2].toString());
					}
					rowList.add(new StyledData(lDbDeductionAmount.longValue(), RightAlignVO));
					if (tuple[3] != null) {
						lDbNetAmount = Double.parseDouble(tuple[3].toString());
					}
					rowList.add(new StyledData(lDbNetAmount.longValue(), RightAlignVO));

					dataList.add(rowList);

				}
			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e);
		}
		return dataList;
	}

	public List getBankWisePaymentDtls(ReportVO lObjReport, String lStrLocCode) {

		String lStrSchemeCode = null;
		String lStrMonthYear = null;
		List lLstRptData = null;
		List dataList = new ArrayList();
		List rowList = null;
		String lStrForMonth = null;
		String lStrForYear = null;
		String lStrBankCode = null;
		String lStrFlag = "";
		Double lDbGrossAmount = 0.0;
		Double lDbDeductionAmount = 0.0;
		Double lDbNetAmount = 0.0;

		try {
			lStrForMonth = (String) lObjReport.getParameterValue("forMonth");
			lStrForYear = (String) lObjReport.getParameterValue("forYear");
			lStrBankCode = (String) lObjReport.getParameterValue("bankCode");
			lStrFlag = (String) lObjReport.getParameterValue("flag");
			if (lStrFlag == null || "".equals(lStrFlag)) {
				StyleVO[] lArrStyleVO = lObjReport.getStyleList();
				lArrStyleVO[0].setStyleValue("ifms.htm?actionFlag=reportService&reportCode=365471&action=parameterPage");
			}
			// lStrMonthYear = (String)
			// lObjReport.getParameterValue("forMonthYear");
			if (Integer.parseInt(lStrForMonth) < 10) {
				lStrMonthYear = lStrForYear + "0" + lStrForMonth;
			} else {
				lStrMonthYear = lStrForYear + lStrForMonth;
			}
			lStrSchemeCode = (String) lObjReport.getParameterValue("schemeCode");

			StyleVO[] RightAlignVO = new StyleVO[3];
			RightAlignVO[0] = new StyleVO();
			RightAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			RightAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
			RightAlignVO[1] = new StyleVO();
			RightAlignVO[1].setStyleId(IReportConstants.BORDER);
			RightAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			RightAlignVO[2] = new StyleVO();
			RightAlignVO[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			RightAlignVO[2].setStyleValue("14");

			String urlPrefix = "ifms.htm?actionFlag=reportService&reportCode=365472&action=generateReport&DirectReport=TRUE&displayOK=TRUE";

			gObjRptQueryDAO = new PensionpayQueryDAOImpl(TrnPensionBillDtls.class, gObjSessionFactory);
			lLstRptData = gObjRptQueryDAO.getBankWisePaymentDtls(lStrMonthYear, lStrSchemeCode, lStrBankCode, lStrLocCode);
			if (lLstRptData != null && !lLstRptData.isEmpty()) {

				Iterator it = lLstRptData.iterator();
				while (it.hasNext()) {

					Object[] tuple = (Object[]) it.next();
					rowList = new ArrayList();
					if (lStrFlag != null && !"".equals(lStrFlag)) {
						if ("Y".equals(lStrFlag)) {
							rowList.add(new URLData(tuple[0].toString(), urlPrefix + "&forMonth=" + lStrForMonth + "&forYear=" + lStrForYear + "&schemeCode=" + lStrSchemeCode
									+ "&bankCode=" + tuple[0].toString() + "&flag=Y"));
						}
					} else {
						rowList.add(tuple[0]);
					}
					rowList.add(tuple[1]);
					if (tuple[2] != null) {
						lDbGrossAmount = Double.parseDouble(tuple[2].toString());
					}
					rowList.add(new StyledData(lDbGrossAmount.longValue(), RightAlignVO));
					if (tuple[3] != null) {
						lDbDeductionAmount = Double.parseDouble(tuple[3].toString());
					}
					rowList.add(new StyledData(lDbDeductionAmount.longValue(), RightAlignVO));
					if (tuple[4] != null) {
						lDbNetAmount = Double.parseDouble(tuple[4].toString());
					}
					rowList.add(new StyledData(lDbNetAmount.longValue(), RightAlignVO));

					dataList.add(rowList);

				}
			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e);
		}
		return dataList;
	}

	public List getBranchWisePaymentDtls(ReportVO lObjReport, String lStrLocCode) {

		String lStrSchemeCode = null;
		String lStrForMonth = null;
		String lStrForYear = null;
		String lStrMonthYear = null;
		String lStrBankCode = null;
		String lStrBranchCode = null;
		String lStrFlag = "";
		List lLstRptData = null;
		List dataList = new ArrayList();
		List rowList = null;
		Double lDbGrossAmount = 0.0;
		Double lDbDeductionAmount = 0.0;
		Double lDbNetAmount = 0.0;

		try {
			// lStrMonthYear = (String)
			// lObjReport.getParameterValue("forMonthYear");
			lStrForMonth = (String) lObjReport.getParameterValue("forMonth");
			lStrForYear = (String) lObjReport.getParameterValue("forYear");
			lStrSchemeCode = (String) lObjReport.getParameterValue("schemeCode");
			lStrBankCode = (String) lObjReport.getParameterValue("bankCode");
			lStrBranchCode = (String) lObjReport.getParameterValue("branchCode");
			lStrFlag = (String) lObjReport.getParameterValue("flag");
			if (lStrFlag == null || "".equals(lStrFlag)) {
				StyleVO[] lArrStyleVO = lObjReport.getStyleList();
				lArrStyleVO[0].setStyleValue("ifms.htm?actionFlag=reportService&reportCode=365472&action=parameterPage");
			}

			if (Integer.parseInt(lStrForMonth) < 10) {
				lStrMonthYear = lStrForYear + "0" + lStrForMonth;
			} else {
				lStrMonthYear = lStrForYear + lStrForMonth;
			}
			StyleVO[] RightAlignVO = new StyleVO[3];
			RightAlignVO[0] = new StyleVO();
			RightAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			RightAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
			RightAlignVO[1] = new StyleVO();
			RightAlignVO[1].setStyleId(IReportConstants.BORDER);
			RightAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			RightAlignVO[2] = new StyleVO();
			RightAlignVO[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			RightAlignVO[2].setStyleValue("14");

			gObjRptQueryDAO = new PensionpayQueryDAOImpl(TrnPensionBillDtls.class, gObjSessionFactory);
			lLstRptData = gObjRptQueryDAO.getBranchWisePaymentDtls(lStrMonthYear, lStrSchemeCode, lStrBankCode, lStrBranchCode, lStrLocCode);
			if (lLstRptData != null && !lLstRptData.isEmpty()) {

				Iterator it = lLstRptData.iterator();
				while (it.hasNext()) {

					Object[] tuple = (Object[]) it.next();
					rowList = new ArrayList();
					rowList.add(tuple[0]);

					rowList.add(tuple[1]);
					if (tuple[2] != null) {
						lDbGrossAmount = Double.parseDouble(tuple[2].toString());
					}
					rowList.add(new StyledData(lDbGrossAmount.longValue(), RightAlignVO));
					if (tuple[3] != null) {
						lDbDeductionAmount = Double.parseDouble(tuple[3].toString());
					}
					rowList.add(new StyledData(lDbDeductionAmount.longValue(), RightAlignVO));
					if (tuple[4] != null) {
						lDbNetAmount = Double.parseDouble(tuple[4].toString());
					}
					rowList.add(new StyledData(lDbNetAmount.longValue(), RightAlignVO));

					dataList.add(rowList);

				}
			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e);
		}
		return dataList;
	}

	public List getAGFirstPayStatement(ReportVO lObjReport, String lStrLocCode) {

		String lStrFormVoucherDate = null;
		String lStrToVoucherDate = null;
		String lStrTreasuryCode = null;
		String lStrPPONo = null;
		Date lDtFromVoucherDate = null;
		Date lDtToVoucherDate = null;
		Long lLngTreasuryCode = null;
		List lLstRptData = new ArrayList();
		List dataList = new ArrayList();
		List rowList = null;
		SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Double lDbGrossBeforeDiff = 0D;
		String lStrPensionerCode = null;
		Map<String, List<Object[]>> lMapDCRGDtls = new HashMap<String, List<Object[]>>();
		List<Object[]> lLstDtls = new ArrayList<Object[]>();
		List<Object[]> lLstCVPFromToDateAmount = null;
		String lStrCVPFromToDateAmount = "";
		Object[] lArrObj = null;
		Object[] tuple = null;
		Integer lIntRowNum = 1;
		try {

			lStrFormVoucherDate = (String) lObjReport.getParameterValue("formVoucherDate");
			lStrToVoucherDate = (String) lObjReport.getParameterValue("toVoucherDate");
			lStrTreasuryCode = (String) lObjReport.getParameterValue("treasuryCode");
			lStrPPONo = (String) lObjReport.getParameterValue("ppoNo");
			if (!"".equals(lStrFormVoucherDate)) {
				lDtFromVoucherDate = StringUtility.convertStringToDate(lStrFormVoucherDate);
			}
			if (!"".equals(lStrToVoucherDate)) {
				lDtToVoucherDate = StringUtility.convertStringToDate(lStrToVoucherDate);
				lDtToVoucherDate.setDate(lDtToVoucherDate.getDate() + 1);
			}
			if (!"".equals(lStrTreasuryCode)) {
				lLngTreasuryCode = Long.parseLong(lStrTreasuryCode);
			}

			StyleVO[] centerVo = new StyleVO[1];
			centerVo[0] = new StyleVO();
			centerVo[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			centerVo[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);		
			
			CommonDAO lObjCommonDAO = new CommonDAOImpl(gObjSessionFactory);
			lObjReport.setAdditionalHeader(new StyledData("Treasury Name: "+lObjCommonDAO.getTreasuryName(gLngLangId, lLngTreasuryCode),centerVo));
			gObjRptQueryDAO = new PensionpayQueryDAOImpl(TrnPensionBillDtls.class, gObjSessionFactory);
			lLstRptData = gObjRptQueryDAO.getAGFirstPayStatement(lDtFromVoucherDate, lDtToVoucherDate, lLngTreasuryCode, lStrPPONo.trim());

			if (lLstRptData != null && !lLstRptData.isEmpty()) {

				Iterator it = lLstRptData.iterator();
				while (it.hasNext()) {
					tuple = (Object[]) it.next();

					lStrPensionerCode = tuple[20].toString();

					lLstDtls = lMapDCRGDtls.get(lStrPensionerCode);

					lArrObj = new Object[3];

					if (tuple[21] != null) {
						lArrObj[0] = lObjDateFormat.format(tuple[21]);
					} else {
						lArrObj[0] = "";
					}
					if (tuple[22] != null) {
						lArrObj[1] = lObjDateFormat.format(tuple[22]);
					} else {
						lArrObj[1] = "";
					}
					if (tuple[23] != null) {
						Double lLngCVPAmount = Double.parseDouble(tuple[23].toString());
						lArrObj[2] = lLngCVPAmount.longValue();
					} else {
						lArrObj[2] = "";
					}

					if (lLstDtls != null) {
						lLstDtls.add(lArrObj);
					} else {
						lLstDtls = new ArrayList<Object[]>();
						lLstDtls.add(lArrObj);
					}

					lMapDCRGDtls.put(lStrPensionerCode, lLstDtls);
				}
				
				it = lLstRptData.iterator();
				while (it.hasNext()) {

					tuple = (Object[]) it.next();
					rowList = new ArrayList();
					lStrCVPFromToDateAmount = "";
					
					rowList.add(lIntRowNum);
					
					if (tuple[1] != null) {
						rowList.add(tuple[1]);
					} else {
						rowList.add("");
					}
					if (tuple[2] != null) {
						rowList.add(tuple[2]);
					} else {
						rowList.add("");
					}
					if (tuple[3] != null) {
						rowList.add(lObjDateFormat.format(tuple[3]));
					} else {
						rowList.add("");
					}
					if (tuple[4] != null) {
						rowList.add(tuple[4]);
					} else {
						rowList.add("");
					}
					if (tuple[5] != null) {
						rowList.add(lObjDateFormat.format(tuple[5]));
					} else {
						rowList.add("");
					}
					if (tuple[6] != null) {
						rowList.add(tuple[6]);
					} else {
						rowList.add("");
					}
					if (tuple[7] != null) {
						rowList.add(lObjDateFormat.format(tuple[7]));
					} else {
						rowList.add("");
					}
					lLstCVPFromToDateAmount = lMapDCRGDtls.get(tuple[20].toString());
					if (lLstCVPFromToDateAmount != null) {
						for (Object[] lArrObjBeneficiaryDtls : lLstCVPFromToDateAmount) {

							lStrCVPFromToDateAmount += lArrObjBeneficiaryDtls[0].toString() + " - " + lArrObjBeneficiaryDtls[1].toString() + " - "
									+ lArrObjBeneficiaryDtls[2].toString() + "\n";
						}
					}
					rowList.add(lStrCVPFromToDateAmount);

					if (tuple[8] != null) {
						rowList.add(tuple[8]);
					} else {
						rowList.add("");
					}
					if (tuple[9] != null) {
						rowList.add(tuple[9]);
					} else {
						rowList.add("");
					}
					if (tuple[10] != null) {
						rowList.add(tuple[10]);
					} else {
						rowList.add("");
					}
					if (tuple[11] != null) {
						rowList.add(tuple[11]);
					} else {
						rowList.add("");
					}
					if (tuple[12] != null) {
						rowList.add(tuple[12]);
					} else {
						rowList.add("");
					}
					if (tuple[13] != null) {
						rowList.add(tuple[13]);
					} else {
						rowList.add("");
					}
					if (tuple[14] != null) {
						rowList.add(tuple[14]);
					} else {
						rowList.add("");
					}
					if (tuple[15] != null) {
						rowList.add(tuple[15]);
					} else {
						rowList.add("");
					}
					if (tuple[17] != null && tuple[16] != null) {
						lDbGrossBeforeDiff = Double.parseDouble(tuple[17].toString()) - Double.parseDouble(tuple[16].toString());
					}
					rowList.add(lDbGrossBeforeDiff);

					if (tuple[16] != null) {
						rowList.add(tuple[16]);
					} else {
						rowList.add("");
					}
					if (tuple[17] != null) {
						rowList.add(tuple[17]);
					} else {
						rowList.add("");
					}
					if (tuple[18] != null) {
						rowList.add(tuple[18]);
					} else {
						rowList.add("");
					}
					if (tuple[19] != null) {
						rowList.add(tuple[19]);
					} else {
						rowList.add("");
					}
					dataList.add(rowList);
					lIntRowNum++;
				}

			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e);
		}
		return dataList;
	}

	public List getCVPPaymentDtls(ReportVO lObjReport, String lStrLocCode) {

		String lStrFormVoucherDate = null;
		String lStrToVoucherDate = null;
		String lStrTreasuryCode = null;
		String lStrPPONo = null;
		Date lDtFromVoucherDate = null;
		Date lDtToVoucherDate = null;
		Long lLngTreasuryCode = null;
		List lLstRptData = new ArrayList();
		List dataList = new ArrayList();
		List rowList = null;
		Integer lIntRowNum = 1;
		SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		try {

			lStrFormVoucherDate = (String) lObjReport.getParameterValue("formVoucherDate");
			lStrToVoucherDate = (String) lObjReport.getParameterValue("toVoucherDate");
			lStrTreasuryCode = (String) lObjReport.getParameterValue("treasuryCode");
			lStrPPONo = (String) lObjReport.getParameterValue("ppoNo");
			if (!"".equals(lStrFormVoucherDate)) {
				lDtFromVoucherDate = StringUtility.convertStringToDate(lStrFormVoucherDate);
			}
			if (!"".equals(lStrToVoucherDate)) {
				lDtToVoucherDate = StringUtility.convertStringToDate(lStrToVoucherDate);
				lDtToVoucherDate.setDate(lDtToVoucherDate.getDate() + 1);
			}
			if (!"".equals(lStrTreasuryCode)) {
				lLngTreasuryCode = Long.parseLong(lStrTreasuryCode);
			}

			StyleVO[] RightAlignVO = new StyleVO[3];
			RightAlignVO[0] = new StyleVO();
			RightAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			RightAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
			RightAlignVO[1] = new StyleVO();
			RightAlignVO[1].setStyleId(IReportConstants.BORDER);
			RightAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			RightAlignVO[2] = new StyleVO();
			RightAlignVO[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			RightAlignVO[2].setStyleValue("14");

			StyleVO[] CenterAlignVO = new StyleVO[3];
			CenterAlignVO[0] = new StyleVO();
			CenterAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			CenterAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
			CenterAlignVO[1] = new StyleVO();
			CenterAlignVO[1].setStyleId(IReportConstants.BORDER);
			CenterAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			CenterAlignVO[2] = new StyleVO();
			CenterAlignVO[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			CenterAlignVO[2].setStyleValue("14");

			CommonDAO lObjCommonDAO = new CommonDAOImpl(gObjSessionFactory);
			lObjReport.setAdditionalHeader(new StyledData("Treasury Name: "+lObjCommonDAO.getTreasuryName(gLngLangId, lLngTreasuryCode),CenterAlignVO));
			

			gObjRptQueryDAO = new PensionpayQueryDAOImpl(TrnPensionBillDtls.class, gObjSessionFactory);
			lLstRptData = gObjRptQueryDAO.getCVPPaymentDtls(lDtFromVoucherDate, lDtToVoucherDate, lLngTreasuryCode, lStrPPONo.trim());

			if (lLstRptData != null && !lLstRptData.isEmpty()) {

				Iterator it = lLstRptData.iterator();
				while (it.hasNext()) {

					Object[] tuple = (Object[]) it.next();
					rowList = new ArrayList();

					
					rowList.add(lIntRowNum);
					
					if (tuple[1] != null) {
						rowList.add(tuple[1]);
					} else {
						rowList.add("");
					}

					if (tuple[2] != null) {
						rowList.add(tuple[2]);
					} else {
						rowList.add("");
					}
					if (tuple[3] != null) {
						rowList.add(new StyledData(lObjDateFormat.format(tuple[3]), CenterAlignVO));
					} else {
						rowList.add("");
					}
					if (tuple[4] != null) {
						rowList.add(tuple[4]);
					} else {
						rowList.add("");
					}
					if (tuple[5] != null) {
						rowList.add(new StyledData(lObjDateFormat.format(tuple[5]), CenterAlignVO));
					} else {
						rowList.add("");
					}
					if (tuple[6] != null) {
						rowList.add(new StyledData(tuple[6], RightAlignVO));
					} else {
						rowList.add("");
					}
					if (tuple[7] != null) {
						rowList.add(tuple[7].toString());
					} else {
						rowList.add("");
					}
					if (tuple[8] != null) {
						rowList.add(new StyledData(lObjDateFormat.format(tuple[8]), CenterAlignVO));
					} else {
						rowList.add("");
					}
					if (tuple[9] != null) {
						rowList.add(tuple[9]);
					} else {
						rowList.add("");
					}
					dataList.add(rowList);
					lIntRowNum++;
				}
			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e);
		}
		return dataList;
	}

	public List getDCRGPaymentDtls(ReportVO lObjReport, String lStrLocCode) {

		String lStrFormVoucherDate = null;
		String lStrToVoucherDate = null;
		String lStrTreasuryCode = null;
		String lStrPPONo = null;
		Date lDtFromVoucherDate = null;
		Date lDtToVoucherDate = null;
		Long lLngTreasuryCode = null;
		List lLstRptData = new ArrayList();
		List dataList = new ArrayList();
		List rowList = null;
		Integer lIntRowNum = 1;
		SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Map<BigInteger, List<Object[]>> lMapDCRGDtls = new HashMap<BigInteger, List<Object[]>>();
		List<Object[]> lLstDtls = new ArrayList<Object[]>();
		BigInteger lBigIntBillNo = null;
		List<Object[]> lLstBeneficiaryDtls = null;
		String lStrBeneficiaryDtls;
		Object[] lArrObj = null;
		try {

			lStrFormVoucherDate = (String) lObjReport.getParameterValue("formVoucherDate");
			lStrToVoucherDate = (String) lObjReport.getParameterValue("toVoucherDate");
			lStrTreasuryCode = (String) lObjReport.getParameterValue("treasuryCode");
			lStrPPONo = (String) lObjReport.getParameterValue("ppoNo");

			if (!"".equals(lStrFormVoucherDate)) {
				lDtFromVoucherDate = StringUtility.convertStringToDate(lStrFormVoucherDate);
			}
			if (!"".equals(lStrToVoucherDate)) {
				lDtToVoucherDate = StringUtility.convertStringToDate(lStrToVoucherDate);
				lDtToVoucherDate.setDate(lDtToVoucherDate.getDate() + 1);
			}
			if (!"".equals(lStrTreasuryCode)) {
				lLngTreasuryCode = Long.parseLong(lStrTreasuryCode);
			}

			StyleVO[] RightAlignVO = new StyleVO[3];
			RightAlignVO[0] = new StyleVO();
			RightAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			RightAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
			RightAlignVO[1] = new StyleVO();
			RightAlignVO[1].setStyleId(IReportConstants.BORDER);
			RightAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			RightAlignVO[2] = new StyleVO();
			RightAlignVO[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			RightAlignVO[2].setStyleValue("14");

			StyleVO[] CenterAlignVO = new StyleVO[3];
			CenterAlignVO[0] = new StyleVO();
			CenterAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			CenterAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
			CenterAlignVO[1] = new StyleVO();
			CenterAlignVO[1].setStyleId(IReportConstants.BORDER);
			CenterAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			CenterAlignVO[2] = new StyleVO();
			CenterAlignVO[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			CenterAlignVO[2].setStyleValue("14");

			CommonDAO lObjCommonDAO = new CommonDAOImpl(gObjSessionFactory);
			lObjReport.setAdditionalHeader(new StyledData("Treasury Name: "+lObjCommonDAO.getTreasuryName(gLngLangId, lLngTreasuryCode),CenterAlignVO));
			
			gObjRptQueryDAO = new PensionpayQueryDAOImpl(TrnPensionBillDtls.class, gObjSessionFactory);
			lLstRptData = gObjRptQueryDAO.getDCRGPaymentDtls(lDtFromVoucherDate, lDtToVoucherDate, lLngTreasuryCode, lStrPPONo.trim());

			if (lLstRptData != null && !lLstRptData.isEmpty()) {
				Iterator it = lLstRptData.iterator();
				while (it.hasNext()) {
					Object[] tuple = (Object[]) it.next();

					lBigIntBillNo = new BigInteger(tuple[1].toString());

					lLstDtls = lMapDCRGDtls.get(lBigIntBillNo);

					lArrObj = new Object[2];
					lArrObj[0] = tuple[12];
					lArrObj[1] = tuple[13];

					if (lLstDtls != null) {
						lLstDtls.add(lArrObj);
					} else {
						lLstDtls = new ArrayList<Object[]>();
						lLstDtls.add(lArrObj);
					}

					lMapDCRGDtls.put(lBigIntBillNo, lLstDtls);
				}
				it = lLstRptData.iterator();
				while (it.hasNext()) {
					Object[] tuple = (Object[]) it.next();

					rowList = new ArrayList();

					
					rowList.add(lIntRowNum);
					
					if (tuple[2] != null) {
						rowList.add(tuple[2]);
					} else {
						rowList.add("");
					}
					if (tuple[3] != null) {
						rowList.add(tuple[3]);
					} else {
						rowList.add("");
					}

					if (tuple[4] != null) {
						rowList.add(new StyledData(lObjDateFormat.format(tuple[4]), CenterAlignVO));
					} else {
						rowList.add("");
					}
					if (tuple[5] != null) {
						rowList.add(tuple[5]);
					} else {
						rowList.add("");
					}

					if (tuple[6] != null) {
						rowList.add(new StyledData(lObjDateFormat.format(tuple[6]), CenterAlignVO));
					} else {
						rowList.add("");
					}
					if (tuple[7] != null) {
						rowList.add(new StyledData(tuple[7], RightAlignVO));
					} else {
						rowList.add("");
					}
					if (tuple[8] != null) {
						rowList.add(new StyledData(tuple[8], RightAlignVO));
					} else {
						rowList.add("");
					}
					if (tuple[9] != null) {
						rowList.add(new StyledData(tuple[9], RightAlignVO));
					} else {
						rowList.add("");
					}
					if (tuple[10] != null) {
						rowList.add(tuple[10].toString());
					} else {
						rowList.add("");
					}

					if (tuple[11] != null) {
						rowList.add(new StyledData(lObjDateFormat.format(tuple[11]), CenterAlignVO));
					} else {
						rowList.add("");
					}

					lLstBeneficiaryDtls = lMapDCRGDtls.get(tuple[1]);
					lStrBeneficiaryDtls = "";
					Double lDbAmt;

					for (Object[] lArrObjBeneficiaryDtls : lLstBeneficiaryDtls) {
						lDbAmt = Double.parseDouble(lArrObjBeneficiaryDtls[1].toString());
						lStrBeneficiaryDtls += lArrObjBeneficiaryDtls[0].toString() + " - " + lDbAmt.longValue() + "\n";
					}
					rowList.add(lStrBeneficiaryDtls);
					dataList.add(rowList);
					lIntRowNum++;
				}

			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e);
		}
		return dataList;
	}

	public List getChangeStatementRpt(ReportVO lObjReport, String lStrLocCode) {
		String lStrYear = null;
		String lStrMonth = null;
		String lStrAuditorPostId = null;
		String lStrMonthYear = null;
		Integer lIntMonthYear = 0;
		Long lLngAuditorPostId = null;
		List lLstRptData = new ArrayList();
		List dataList = new ArrayList();
		List rowList = null;
		Integer lIntSrNoCnt = 1;
		try {

			lStrYear = (String) lObjReport.getParameterValue("year");
			lStrMonth = (String) lObjReport.getParameterValue("month");
			lStrAuditorPostId = (String) lObjReport.getParameterValue("auditorPostId");

			if (Integer.parseInt(lStrMonth) < 10) {
				lStrMonthYear = lStrYear + "0" + lStrMonth;
			} else {
				lStrMonthYear = lStrYear + lStrMonth;
			}
			if (lStrMonthYear != null) {
				lIntMonthYear = Integer.valueOf(lStrMonthYear);
			}
			if (!"-1".equals(lStrAuditorPostId)) {
				lLngAuditorPostId = Long.parseLong(lStrAuditorPostId);
			}

			gObjRptQueryDAO = new PensionpayQueryDAOImpl(MstBank.class, gObjSessionFactory);
			lLstRptData = gObjRptQueryDAO.getChangeStatementData(lLngAuditorPostId, lIntMonthYear, lStrLocCode, gLngLangId.toString());

			if (lLstRptData != null && !lLstRptData.isEmpty()) {
				Iterator it = lLstRptData.iterator();
				while (it.hasNext()) {
					Object[] tuple = (Object[]) it.next();

					rowList = new ArrayList();

					rowList.add(lIntSrNoCnt);

					if (tuple[0] != null) {
						rowList.add(tuple[0]);
					} else {
						rowList.add("");
					}
					if (tuple[1] != null) {
						rowList.add(tuple[1]);
					} else {
						rowList.add("");
					}
					if (tuple[2] != null) {
						if (tuple[2].equals("WithATO Approval")) {
							rowList.add("With ATO for Approval");
						} else {
							rowList.add(tuple[2]);
						}
					} else {
						rowList.add("");
					}
					if (tuple[3] != null) {
						if (!"".equals(tuple[3].toString().trim())) {
							rowList.add(tuple[3]);
						} else {
							rowList.add("Not Mapped with Auditor");
						}
					} else {
						rowList.add("Not Mapped with Auditor");
					}
					dataList.add(rowList);
					lIntSrNoCnt++;
				}
			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e);
		}
		return dataList;
	}
	
	public List getRevisedAgCasesRpt(ReportVO lObjReport, String lStrLocCode) {
		String lStrAcceptFromDate = null;
		String lStrAcceptToDate = null;
		String lStrAuditorPostId = null;
		String lStrMonthYear = null;
		Integer lIntMonthYear = 0;
		Long lLngAuditorPostId = null;
		List lLstRptData = new ArrayList();
		List dataList = new ArrayList();
		List rowList = null;
		Integer lIntSrNoCnt = 1;
		SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		try {
			
			lStrAcceptFromDate = lObjReport.getParameterValue("PpoAcceptDatefrom").toString();
			lStrAcceptToDate = lObjReport.getParameterValue("PpoAcceptDateto").toString();
			
			StyleVO[] RightAlignVO = new StyleVO[3];
			RightAlignVO[0] = new StyleVO();
			RightAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			RightAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
			RightAlignVO[1] = new StyleVO();
			RightAlignVO[1].setStyleId(IReportConstants.BORDER);
			RightAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			RightAlignVO[2] = new StyleVO();
			RightAlignVO[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			RightAlignVO[2].setStyleValue("14");

			gObjRptQueryDAO = new PensionpayQueryDAOImpl(TrnPensionerRivisionDtls.class, gObjSessionFactory);
			lLstRptData = gObjRptQueryDAO.getRevisedAgCases(lObjDateFormat.parse(lStrAcceptFromDate), lObjDateFormat.parse(lStrAcceptToDate), lStrLocCode);

			if (lLstRptData != null && !lLstRptData.isEmpty()) {
				Iterator it = lLstRptData.iterator();
				while (it.hasNext()) {
					Object[] tuple = (Object[]) it.next();

					rowList = new ArrayList();
					if (tuple[0] != null) {
						rowList.add(tuple[0]);
					} else {
						rowList.add("");
					}
					if (tuple[1] != null) {
						rowList.add(tuple[1]);
					} else {
						rowList.add("");
					}
					if (tuple[2] != null) {
						rowList.add(tuple[2]);
					} else {
						rowList.add("");
					}
					if (tuple[3] != null) {
						rowList.add(tuple[3]);
					} else {
						rowList.add("");
					}
					if (tuple[4] != null) {
						rowList.add(tuple[4]);
					} else {
						rowList.add("");
					}
					if (tuple[5] != null) {
						rowList.add(tuple[5]);
					} else {
						rowList.add("");
					}
					if (tuple[6] != null) {
						rowList.add(new StyledData(tuple[6], RightAlignVO));
					} else {
						rowList.add("");
					}
					if (tuple[7] != null) {
						rowList.add(tuple[7]);
					} else {
						rowList.add("");
					}
					if (tuple[8] != null) {
						rowList.add(new StyledData(tuple[8], RightAlignVO));
						
					} else {
						rowList.add("");
					}
					if (tuple[9] != null) {
						rowList.add(tuple[9]);
					} else {
						rowList.add("");
					}
					dataList.add(rowList);
					
				}
			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e);
		}
		return dataList;
	}
	
	public List getRegisterOfGratuityOrderReceivedRpt(ReportVO lObjReport, String lStrLocCode) {
				
	    String lStrFinYear = null;
	    String lStrFinYearRange = null;
	    Integer lIntYear = 0;
	    String lStrOrderFromDate = null;
	    String lStrOrderToDate = null;
		List lLstRptData = new ArrayList();
		List dataList = new ArrayList();
		List rowList = null;
		Integer lIntSrNoCnt = 1;
		StringBuilder lSbHeaderVal = new StringBuilder();
		String lStrLocationCode = "";
		String lStrLocationName = "";
		List<Short> lLstBillStatus = new ArrayList<Short>();
		SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		DecimalFormat lDecFormat = new DecimalFormat("#0.##"); 
		try {
			Date lDtCurrDate = SessionHelper.getCurDate();			
			Calendar lObjCalendar = null;
			lObjCalendar = Calendar.getInstance();
			lObjCalendar.setTime(lDtCurrDate);
			
			CommonDAO lObjCommonDAO = new CommonDAOImpl(gObjSessionFactory);
			lStrLocationName = lObjCommonDAO.getTreasuryName(gLngLangId, Long.parseLong(lStrLocCode));
			lStrLocationCode = lObjReport.getParameterValue("locationCode").toString();
			lStrOrderFromDate = lObjReport.getParameterValue("gpoOrderDatefrom").toString();
			lStrOrderToDate = lObjReport.getParameterValue("gpoOrderDateto").toString();
			
			//lStrFinYear =  lObjReport.getParameterValue("finYear").toString();
			lLstBillStatus.add(DBConstants.ST_BILL_CREATED);
			lLstBillStatus.add(DBConstants.ST_BILL_FORW_TO_ATO);
			lLstBillStatus.add(DBConstants.ST_BILL_REJECTED);
			lLstBillStatus.add(DBConstants.ST_BILL_DISCARD);
			lLstBillStatus.add(DBConstants.ST_BILL_ARCHEIVED);
						
//			if(!"".equals(lStrFinYear))
//			{
//				lStrOrderFromDate = "01/01/"+lStrFinYear;
//				lStrOrderToDate = "31/12/"+lStrFinYear;
//				lIntYear = Integer.parseInt(lStrFinYear);
//				lStrFinYearRange = lIntYear + " - " + (lIntYear +1);
//			}
			StyleVO[] RightAlignVO = new StyleVO[3];
			RightAlignVO[0] = new StyleVO();
			RightAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			RightAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
			RightAlignVO[1] = new StyleVO();
			RightAlignVO[1].setStyleId(IReportConstants.BORDER);
			RightAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			RightAlignVO[2] = new StyleVO();
			RightAlignVO[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			RightAlignVO[2].setStyleValue("14");
					
			StyleVO[] boldRedFontVO = new StyleVO[2];
			boldRedFontVO[0] = new StyleVO();
			boldRedFontVO[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			boldRedFontVO[0].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			boldRedFontVO[1] = new StyleVO();
			boldRedFontVO[1].setStyleId(IReportConstants.BACKGROUNDCOLOR);
			boldRedFontVO[1].setStyleValue("#E34234");
			
			lSbHeaderVal.append("MTR-36 \r\n");
			lSbHeaderVal.append("(See Rule 355(2)) \r\n\r\n");
			lSbHeaderVal.append("Register of Gratuity Orders Received in "+lStrLocationName+"\r\n");
			//lSbHeaderVal.append("For the Year "+lStrFinYearRange);
			lObjReport.setReportName(lSbHeaderVal.toString());
			
			gObjRptQueryDAO = new PensionpayQueryDAOImpl(HstPnsnPmntDcrgDtls.class, gObjSessionFactory);
			lLstRptData = gObjRptQueryDAO.getRegisterOfGratuityOrderReceived(lObjDateFormat.parse(lStrOrderFromDate), lObjDateFormat.parse(lStrOrderToDate),lLstBillStatus, lStrLocationCode);
			
			if (lLstRptData != null && !lLstRptData.isEmpty()) {
				
				Iterator it = lLstRptData.iterator();
				while (it.hasNext()) {
					Object[] tuple = (Object[]) it.next();

					rowList = new ArrayList();
					rowList.add(lIntSrNoCnt);
					
					if (tuple[0] != null) {
						rowList.add(tuple[0]);
					} else {
						rowList.add("");
					}
					if (tuple[1] != null && tuple[2] != null) {
						rowList.add(tuple[1]+"-"+lObjDateFormat.format(tuple[2]));
					} else {
						rowList.add("");
					}
					if (tuple[3] != null) {
						rowList.add(tuple[3]);
					} else {
						rowList.add("");
					}
					if (tuple[4] != null) {
						rowList.add(tuple[4]);
					} else {
						rowList.add("");
					}
					if (tuple[5] != null) {
						rowList.add(new StyledData(tuple[5], RightAlignVO));
					} else {
						rowList.add("");
					}
					if (tuple[6] != null) {
						rowList.add(new StyledData(tuple[6], RightAlignVO));
					} else {
						rowList.add("");
					}
					if(tuple[5] != null && tuple[6] != null) {
						BigDecimal lBgDcmlTotalDcrgAmt = new BigDecimal(tuple[5].toString());
						BigDecimal lBgDcmlRecoveryAmt = new BigDecimal(tuple[6].toString());
						BigDecimal lBgDcmlDcrgPaid = lBgDcmlTotalDcrgAmt.subtract(lBgDcmlRecoveryAmt);
						rowList.add(new StyledData(lBgDcmlDcrgPaid, RightAlignVO));
					} else {
						rowList.add("");
					}
					if (tuple[7] != null && tuple[8] != null) {
						rowList.add(tuple[7] + "-" +lObjDateFormat.format(tuple[8]));
					} else {
						rowList.add("");
					}
					rowList.add("");
					int lIntYearDiff = 0;
					BigDecimal lBgDcmlYearDiff = BigDecimal.ZERO;
					double lDbYearDiff = 0D;
					BigDecimal lIntDaysDiff = BigDecimal.ZERO;
					int lIntMonthDiff = 0;
					if (tuple[2] != null) {
						
						Calendar lObjCalendar1 = Calendar.getInstance();
						lObjCalendar1.setTime(lObjDateFormat.parse(lObjDateFormat.format(tuple[2])));
						
						long milliseconds1 = lObjCalendar1.getTimeInMillis();
						long milliseconds2 = lObjCalendar.getTimeInMillis();
						
						long diff = milliseconds2 - milliseconds1;

						lIntDaysDiff = new BigDecimal(diff / (24 * 60 * 60 * 1000));
						
						lBgDcmlYearDiff = new BigDecimal(lDecFormat.format(lIntDaysDiff.doubleValue() / 365));
				
					}
					if (tuple[7] == null && tuple[8] == null && lBgDcmlYearDiff.compareTo(BigDecimal.ONE) == 1) {
						rowList.add(new StyledData("Limit Exceeded", boldRedFontVO));
						//rowList.add("Limit Exceeded");
					} else {
						rowList.add("");	
					}
					dataList.add(rowList);
					++lIntSrNoCnt;
				}
			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e);
		}
		return dataList;
	}
	
	public List getRegisterOfPpoReceivedRpt(ReportVO lObjReport, String lStrLocCode) {
		
		String lStrPpoInwardFromDate = null;
		String lStrPpoInwardToDate = null;
	    String lStrPpoNo = null;
	    String lStrLocationCode = null;
	    String lStrCaseStatus = null;
	    List<String> lLstCaseStatus = new ArrayList<String>();
		List lLstRptData = new ArrayList();
		List dataList = new ArrayList();
		List rowList = null;
		Integer lIntSrNoCnt = 1;
		StringBuilder lSbHeaderVal = new StringBuilder();
		String lStrLocationName = "";
		SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		try {
			lLstCaseStatus.add(gObjRsrcBndle.getString("STATFLG.NEW"));
			lLstCaseStatus.add(gObjRsrcBndle.getString("STATFLG.REGISTERED"));
			lLstCaseStatus.add(gObjRsrcBndle.getString("STATFLG.INWARDNEW"));
						
			CommonDAO lObjCommonDAO = new CommonDAOImpl(gObjSessionFactory);
			lStrLocationName = lObjCommonDAO.getTreasuryName(gLngLangId, Long.parseLong(lStrLocCode));
			lStrPpoInwardFromDate = lObjReport.getParameterValue("PpoInwardDatefrom").toString();
			lStrPpoInwardToDate = lObjReport.getParameterValue("PpoInwardDateto").toString();
			lStrLocationCode = lObjReport.getParameterValue("locationCode").toString();
			lStrCaseStatus = lObjReport.getParameterValue("caseStatus").toString();
			lStrPpoNo =  lObjReport.getParameterValue("PPONo").toString();
			
			StyleVO[] RightAlignVO = new StyleVO[3];
			RightAlignVO[0] = new StyleVO();
			RightAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			RightAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
			RightAlignVO[1] = new StyleVO();
			RightAlignVO[1].setStyleId(IReportConstants.BORDER);
			RightAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			RightAlignVO[2] = new StyleVO();
			RightAlignVO[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			RightAlignVO[2].setStyleValue("14");
			
			StyleVO[] CenterAlignVO = new StyleVO[3];
			CenterAlignVO[0] = new StyleVO();
			CenterAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			CenterAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
			CenterAlignVO[1] = new StyleVO();
			CenterAlignVO[1].setStyleId(IReportConstants.BORDER);
			CenterAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			CenterAlignVO[2] = new StyleVO();
			CenterAlignVO[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			CenterAlignVO[2].setStyleValue("14");
			
			StyleVO[] boldBlackFontVO = new StyleVO[4];
			boldBlackFontVO[0] = new StyleVO();
			boldBlackFontVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			boldBlackFontVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
			boldBlackFontVO[1] = new StyleVO();
			boldBlackFontVO[1].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			boldBlackFontVO[1].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			boldBlackFontVO[2] = new StyleVO();
			boldBlackFontVO[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			boldBlackFontVO[2].setStyleValue("14");
			boldBlackFontVO[3] = new StyleVO();
			boldBlackFontVO[3].setStyleId(IReportConstants.BACKGROUNDCOLOR);
			boldBlackFontVO[3].setStyleValue("#D3D3D3");

			lSbHeaderVal.append(gObjRsrcBndleMarathi.getString("PPMT.REGOFPPOHEADER1")+" MTR-34 \r\n");
			lSbHeaderVal.append("("+gObjRsrcBndleMarathi.getString("PPMT.REGOFPPOHEADER2")+") (See Rule 325) \r\n\r\n");
			lSbHeaderVal.append(lStrLocationName +" "+gObjRsrcBndleMarathi.getString("PPMT.REGOFPPOHEADER3")+" \r\n");
			lSbHeaderVal.append("Register of PPO received "+lStrLocationName);
			lObjReport.setReportName(lSbHeaderVal.toString());
			
			gObjRptQueryDAO = new PensionpayQueryDAOImpl(TrnPensionerRivisionDtls.class, gObjSessionFactory);
			lLstRptData = gObjRptQueryDAO.getRegisterOfPpoReceived(lObjDateFormat.parse(lStrPpoInwardFromDate), lObjDateFormat.parse(lStrPpoInwardToDate),lStrPpoNo, lStrLocationCode,lLstCaseStatus,lStrCaseStatus);
			
			if (lLstRptData != null && !lLstRptData.isEmpty()) {
				rowList = new ArrayList();
				rowList.add(new StyledData("PPO No.", boldBlackFontVO));
				rowList.add(new StyledData("Name of Pensioner", boldBlackFontVO));
				rowList.add(new StyledData("Basic Pension Amount", boldBlackFontVO));
				rowList.add(new StyledData("PPO Inward Date", boldBlackFontVO));
				rowList.add(new StyledData("Pension Start Date", boldBlackFontVO));
				rowList.add(new StyledData("Status", boldBlackFontVO));
				
				dataList.add(rowList);
				Iterator it = lLstRptData.iterator();
				while (it.hasNext()) {
					Object[] tuple = (Object[]) it.next();

					rowList = new ArrayList();
					if (tuple[0] != null) {
						rowList.add(tuple[0]);
					} else {
						rowList.add("");
					}
					if (tuple[1] != null) {
						rowList.add(tuple[1]);
					} else {
						rowList.add("");
					}
					if (tuple[2] != null) {
						rowList.add(new StyledData(tuple[2], RightAlignVO));
					} else {
						rowList.add("");
					}
					if (tuple[3] != null) {
						rowList.add(new StyledData(lObjDateFormat.format(tuple[3]), CenterAlignVO));
				    } else {
				    	rowList.add("");
				    }
					if (tuple[4] != null) {
						rowList.add(new StyledData(lObjDateFormat.format(tuple[4]), CenterAlignVO));
					} else {
						rowList.add("");
					}
					
					if (tuple[5] != null) {
						if(gObjRsrcBndle.getString("STATFLG.NEW").equals(tuple[5]) || gObjRsrcBndle.getString("STATFLG.REGISTERED").equals(tuple[5])
								|| gObjRsrcBndle.getString("STATFLG.INWARDNEW").equals(tuple[5]))
						{
							rowList.add("Non Identified");
						}
						else
						{
							rowList.add(gObjRsrcBndle.getString("STATFLG.IDENTIFIED"));
						}
					} else {
						rowList.add("");
					}
								
					dataList.add(rowList);
					
				}
			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e);
		}
		return dataList;
	}
	
	public List getRegisterOfCommutationOrderReceivedRpt(ReportVO lObjReport, String lStrLocCode) {
		
	    String lStrFinYear = null;
	    String lStrFinYearRange = null;
	    Integer lIntYear = 0;
	    String lStrOrderFromDate = null;
	    String lStrOrderToDate = null;
		List lLstRptData = new ArrayList();
		List dataList = new ArrayList();
		List rowList = null;
		Integer lIntSrNoCnt = 1;
		StringBuilder lSbHeaderVal = new StringBuilder();
		String lStrLocationName = "";
		String lStrLocationCode = "";
		List<Short> lLstBillStatus = new ArrayList<Short>();
		SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		try {
			CommonDAO lObjCommonDAO = new CommonDAOImpl(gObjSessionFactory);
			lStrLocationName = lObjCommonDAO.getTreasuryName(gLngLangId, Long.parseLong(lStrLocCode));
			lStrOrderFromDate = lObjReport.getParameterValue("cpoOrderDatefrom").toString();
			lStrOrderToDate = lObjReport.getParameterValue("cpoOrderDateto").toString();
			lStrLocationCode = lObjReport.getParameterValue("locationCode").toString();
			
			//lStrFinYear =  lObjReport.getParameterValue("finYear").toString();
			lLstBillStatus.add(DBConstants.ST_BILL_CREATED);
			lLstBillStatus.add(DBConstants.ST_BILL_FORW_TO_ATO);
			lLstBillStatus.add(DBConstants.ST_BILL_REJECTED);
			lLstBillStatus.add(DBConstants.ST_BILL_DISCARD);
			lLstBillStatus.add(DBConstants.ST_BILL_ARCHEIVED);
						
//			if(!"".equals(lStrFinYear))
//			{
//				lStrOrderFromDate = "01/01/"+lStrFinYear;
//				lStrOrderToDate = "31/12/"+lStrFinYear;
//				lIntYear = Integer.parseInt(lStrFinYear);
//				lStrFinYearRange = lIntYear + " - " + (lIntYear +1);
//			}
			StyleVO[] RightAlignVO = new StyleVO[3];
			RightAlignVO[0] = new StyleVO();
			RightAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			RightAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
			RightAlignVO[1] = new StyleVO();
			RightAlignVO[1].setStyleId(IReportConstants.BORDER);
			RightAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			RightAlignVO[2] = new StyleVO();
			RightAlignVO[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			RightAlignVO[2].setStyleValue("14");
			
			StyleVO[] boldBlackFontVO = new StyleVO[4];
			boldBlackFontVO[0] = new StyleVO();
			boldBlackFontVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			boldBlackFontVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
			boldBlackFontVO[1] = new StyleVO();
			boldBlackFontVO[1].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			boldBlackFontVO[1].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			boldBlackFontVO[2] = new StyleVO();
			boldBlackFontVO[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			boldBlackFontVO[2].setStyleValue("14");
			boldBlackFontVO[3] = new StyleVO();
			boldBlackFontVO[3].setStyleId(IReportConstants.BACKGROUNDCOLOR);
			boldBlackFontVO[3].setStyleValue("#D3D3D3");
					
			lSbHeaderVal.append("MTR-36 \r\n");
			lSbHeaderVal.append("(See Rule 355(2)) \r\n\r\n");
			lSbHeaderVal.append(lStrLocationName+" " +gObjRsrcBndleMarathi.getString("PPMT.REGOFCVPHEADER1") +"\r\n");
			//lSbHeaderVal.append(gObjRsrcBndleMarathi.getString("PPMT.REGOFCVPHEADER2") +" "+lStrFinYearRange +"\r\n"); 
			lSbHeaderVal.append("Register of Commutation Value Payment Orders received in "+lStrLocationName + "\r\n");
			//lSbHeaderVal.append("Year "+lStrFinYearRange);
			lObjReport.setReportName(lSbHeaderVal.toString());
			
			gObjRptQueryDAO = new PensionpayQueryDAOImpl(HstCommutationDtls.class, gObjSessionFactory);
			lLstRptData = gObjRptQueryDAO.getRegisterOfCvpOrderReceived(lObjDateFormat.parse(lStrOrderFromDate), lObjDateFormat.parse(lStrOrderToDate),lLstBillStatus, lStrLocationCode);
			
			if (lLstRptData != null && !lLstRptData.isEmpty()) {
				rowList = new ArrayList();
				rowList.add(new StyledData("Sr. No.", boldBlackFontVO));
				rowList.add(new StyledData("Name of Pensioner", boldBlackFontVO));
				rowList.add(new StyledData("CPO No. & Date", boldBlackFontVO));
				//rowList.add(new StyledData("Name of Payee", boldBlackFontVO));
				rowList.add(new StyledData("Treasury for Payment", boldBlackFontVO));
				rowList.add(new StyledData("Amount of CVP", boldBlackFontVO));
				rowList.add(new StyledData("Recovery (if any)", boldBlackFontVO));
				rowList.add(new StyledData("Payment Voucher No. & Date", boldBlackFontVO));
				rowList.add(new StyledData("Signature of TO", boldBlackFontVO));
				rowList.add(new StyledData("Remarks", boldBlackFontVO));
				dataList.add(rowList);
				
				Iterator it = lLstRptData.iterator();
				while (it.hasNext()) {
					Object[] tuple = (Object[]) it.next();

					rowList = new ArrayList();
					rowList.add(lIntSrNoCnt);
					
					if (tuple[0] != null) {
						rowList.add(tuple[0]);
					} else {
						rowList.add("");
					}
					if (tuple[1] != null && tuple[2] != null) {
						rowList.add(tuple[1]+"-"+lObjDateFormat.format(tuple[2]));
					} else {
						rowList.add("");
					}
//					if (tuple[3] != null) {
//						rowList.add(tuple[3]);
//					} else {
//						rowList.add("");
//					}
					if (tuple[4] != null) {
						rowList.add(tuple[4]);
					} else {
						rowList.add("");
					}
					if (tuple[5] != null) {
						rowList.add(new StyledData(tuple[5], RightAlignVO));
					} else {
						rowList.add("");
					}
					rowList.add("");
				
					if (tuple[6] != null && tuple[7] != null) {
						rowList.add(tuple[6] + "-" +lObjDateFormat.format(tuple[7]));
					} else {
						rowList.add("");
					}
					rowList.add("");
					rowList.add("");
					dataList.add(rowList);
					++lIntSrNoCnt;
				}
			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e);
		}
		return dataList;
	}
	
	public List getChangeInTrsryRpt(ReportVO lObjReport, String lStrLocCode) {
		
	    String lStrRequestGenFromDate = null;
	    String lStrRequestGenToDate = null;
	    List lLstRptData = new ArrayList();
		List dataList = new ArrayList();
		List rowList = null;
		Integer lIntSrNoCnt = 1;
		StringBuilder lSbHeaderVal = new StringBuilder();
				
		SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat lObjSmplDateFormat = new SimpleDateFormat("MMM-yyyy");
		try {
						
			lStrRequestGenFromDate =  lObjReport.getParameterValue("requestGenerationDatefrom").toString();
			lStrRequestGenToDate =  lObjReport.getParameterValue("requestGenerationDateto").toString();
			
			StyleVO[] RightAlignVO = new StyleVO[3];
			RightAlignVO[0] = new StyleVO();
			RightAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			RightAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
			RightAlignVO[1] = new StyleVO();
			RightAlignVO[1].setStyleId(IReportConstants.BORDER);
			RightAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			RightAlignVO[2] = new StyleVO();
			RightAlignVO[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			RightAlignVO[2].setStyleValue("14");
			
			StyleVO[] boldBlackFontVO = new StyleVO[4];
			boldBlackFontVO[0] = new StyleVO();
			boldBlackFontVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			boldBlackFontVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
			boldBlackFontVO[1] = new StyleVO();
			boldBlackFontVO[1].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			boldBlackFontVO[1].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			boldBlackFontVO[2] = new StyleVO();
			boldBlackFontVO[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			boldBlackFontVO[2].setStyleValue("14");
			boldBlackFontVO[3] = new StyleVO();
			boldBlackFontVO[3].setStyleId(IReportConstants.BACKGROUNDCOLOR);
			boldBlackFontVO[3].setStyleValue("#D3D3D3");
					
			lSbHeaderVal.append(gObjRsrcBndleMarathi.getString("PPMT.CHANGEINTRSRY") +"\r\n");
			lSbHeaderVal.append("CHANGE IN TREASURY (PPO Transfer)");
			
			lObjReport.setReportName(lSbHeaderVal.toString());
			
			gObjRptQueryDAO = new PensionpayQueryDAOImpl(TrnPensionTransferDtls.class, gObjSessionFactory);
			lLstRptData = gObjRptQueryDAO.getChangeInTrsryData(lObjDateFormat.parse(lStrRequestGenFromDate), lObjDateFormat.parse(lStrRequestGenToDate),lStrLocCode,gLngLangId);
			
			if (lLstRptData != null && !lLstRptData.isEmpty()) {
				rowList = new ArrayList();
				rowList.add(new StyledData("Sr. No.", boldBlackFontVO));
				rowList.add(new StyledData("Received Application Date", boldBlackFontVO));
				rowList.add(new StyledData("Pensioner's Name and Address", boldBlackFontVO));
				rowList.add(new StyledData("PPO Order No.", boldBlackFontVO));
				rowList.add(new StyledData("Pensioner Bank Name and Account", boldBlackFontVO));
				rowList.add(new StyledData("Last Payment upto Month & Year", boldBlackFontVO));
				rowList.add(new StyledData("Transfer from which Treasury Name", boldBlackFontVO));
				rowList.add(new StyledData("Transfer to which Treasury Name", boldBlackFontVO));
				rowList.add(new StyledData("Last Pay Certificate No. & Date", boldBlackFontVO));
				rowList.add(new StyledData("ATO Signature", boldBlackFontVO));
				dataList.add(rowList);
				
				Iterator it = lLstRptData.iterator();
				while (it.hasNext()) {
					Object[] tuple = (Object[]) it.next();
					String lStrPnsnrName = "";
					String lStrPnsnrAddr = "";
					String lStrMonth = "";
					String lStrYear = "";
					rowList = new ArrayList();
					rowList.add(lIntSrNoCnt);
					
					if (tuple[0] != null) {
						rowList.add(lObjDateFormat.format(tuple[0]));
					} else {
						rowList.add("");
					}
					if (tuple[15] != null && tuple[16] != null) {
						lStrPnsnrName = tuple[16].toString();
					}
					else
					{
						if(tuple[1] != null)
						{
							lStrPnsnrName = tuple[1].toString();
						}
					}
					lStrPnsnrAddr = ((tuple[2] != null) ? tuple[2] +" ":" ")+
									((tuple[3] != null) ? tuple[3] +" ":" ")+
									((tuple[4] != null) ? tuple[4] +" ":" ")+
									((tuple[5] != null) ? tuple[5] +" ":" ")+
									((tuple[6] != null) ? tuple[6] +" ":" ")+
									((tuple[7] != null) ? tuple[7] +" ":"");
									
					rowList.add(lStrPnsnrName + " - " + (lStrPnsnrAddr != null ? lStrPnsnrAddr : ""));
					
					if (tuple[8] != null) {
						rowList.add(tuple[8]);
					} else {
						rowList.add("");
					}
					if (tuple[9] != null && tuple[10] != null) {
						rowList.add(tuple[9] + "-" + tuple[10]);
					} else {
						rowList.add("");
					}
					if (tuple[11] != null && tuple[12] != null) {
						String lStrMonthYear = tuple[12].toString();
						lStrYear = tuple[12].toString().substring(0, 4);
						lStrMonth = tuple[12].toString().substring(4);
						String lStrLastPayDate = "01/" + lStrMonth +"/"+ lStrYear;
						Date lDtLastPayDate = lObjDateFormat.parse(lStrLastPayDate);
						rowList.add(tuple[11] + "-" + lObjSmplDateFormat.format(lDtLastPayDate));
					} else {
						rowList.add("");
					}
					if (tuple[13] != null) {
						rowList.add(tuple[13]);
					} else {
						rowList.add("");
					}
					if (tuple[14] != null) {
						rowList.add(tuple[14]);
					} else {
						rowList.add("");
					}
					rowList.add("");
					rowList.add("");
					dataList.add(rowList);
					++lIntSrNoCnt;
				}
			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e);
		}
		return dataList;
	}
	
	public List getOverPaymentRecoveryRpt(ReportVO lObjReport, String lStrLocCode) {
		
	    String lStrRecoveryFromDate = null;
	    String lStrRecoveryToDate = null;
	    Date lDtRecoveryFromDate = null;
	    Date lDtRecoveryToDate = null;
	    Integer lIntFromMonthYear;
	    Integer lIntToMonthYear;
	    List lLstRptData = new ArrayList();
		List dataList = new ArrayList();
		List rowList = null;
		Integer lIntSrNoCnt = 1;
		StringBuilder lSbHeaderVal = new StringBuilder();
		String lStrTreasuryName = "";
		SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat lObjSmplDateFormat = new SimpleDateFormat("MMM-yyyy");
		List<Short> lLstBillStatus = new ArrayList<Short>();
		List<String> lLstRecoveryType = new ArrayList<String>();
		List lLstPnsnrDtls = null;
		List lLstMonthlyRecovery = null;
		Map<String,Object[]> lMapPnsnrDtls = new TreeMap<String,Object[]>();
		Map<String,String> lMapMonthlyRecDtls = new TreeMap<String,String>();
		try {
						
			lStrRecoveryFromDate =  lObjReport.getParameterValue("recoveryDatefrom").toString();
			lStrRecoveryToDate =  lObjReport.getParameterValue("recoveryDateto").toString();
			lDtRecoveryFromDate = lObjDateFormat.parse(lStrRecoveryFromDate);
			lDtRecoveryToDate = lObjDateFormat.parse(lStrRecoveryToDate);
			
			Calendar lObjCalendar = Calendar.getInstance();
			lObjCalendar.setTime(lDtRecoveryFromDate);
			Integer lIntMonth = lObjCalendar.get(lObjCalendar.MONTH) + 1;
			String lStrYYYYMM = lObjCalendar.get(lObjCalendar.YEAR) + "" + (lIntMonth < 10 ? "0" + lIntMonth : lIntMonth);
			lIntFromMonthYear = Integer.parseInt(lStrYYYYMM);
			
			lObjCalendar = Calendar.getInstance();
			lObjCalendar.setTime(lDtRecoveryToDate);
			lIntMonth = lObjCalendar.get(lObjCalendar.MONTH) + 1;
			lStrYYYYMM = lObjCalendar.get(lObjCalendar.YEAR) + "" + (lIntMonth < 10 ? "0" + lIntMonth : lIntMonth);
			lIntToMonthYear = Integer.parseInt(lStrYYYYMM);
			
			CommonDAO lObjCommonDAO = new CommonDAOImpl(gObjSessionFactory);
			lStrTreasuryName = lObjCommonDAO.getTreasuryName(gLngLangId, Long.parseLong(lStrLocCode));
			
			StyleVO[] RightAlignVO = new StyleVO[3];
			RightAlignVO[0] = new StyleVO();
			RightAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			RightAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
			RightAlignVO[1] = new StyleVO();
			RightAlignVO[1].setStyleId(IReportConstants.BORDER);
			RightAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			RightAlignVO[2] = new StyleVO();
			RightAlignVO[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			RightAlignVO[2].setStyleValue("14");
			
			StyleVO[] boldBlackFontVO = new StyleVO[4];
			boldBlackFontVO[0] = new StyleVO();
			boldBlackFontVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			boldBlackFontVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
			boldBlackFontVO[1] = new StyleVO();
			boldBlackFontVO[1].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			boldBlackFontVO[1].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			boldBlackFontVO[2] = new StyleVO();
			boldBlackFontVO[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			boldBlackFontVO[2].setStyleValue("14");
			boldBlackFontVO[3] = new StyleVO();
			boldBlackFontVO[3].setStyleId(IReportConstants.BACKGROUNDCOLOR);
			boldBlackFontVO[3].setStyleValue("#D3D3D3");
					
			lSbHeaderVal.append(gObjRsrcBndleMarathi.getString("PPMT.OVERPAYRECOVERY1") +"\r\n");
			lSbHeaderVal.append(lStrTreasuryName+" "+gObjRsrcBndleMarathi.getString("PPMT.OVERPAYRECOVERY2") +"\r\n");
			lSbHeaderVal.append(gObjRsrcBndleMarathi.getString("PPMT.OVERPAYRECOVERY3") + " " +lObjDateFormat.format(SessionHelper.getCurDate()) + "\r\n");
			lSbHeaderVal.append("Register of Over Payment being Recovered" + "\r\n");
			lSbHeaderVal.append("Treasury "+lStrTreasuryName+"\r\n");
			lSbHeaderVal.append("Date "+lObjDateFormat.format(SessionHelper.getCurDate()));
			
			lObjReport.setReportName(lSbHeaderVal.toString());
			
			lLstBillStatus.add(DBConstants.ST_BILL_CREATED);
			lLstBillStatus.add(DBConstants.ST_BILL_FORW_TO_ATO);
			lLstBillStatus.add(DBConstants.ST_BILL_REJECTED);
			lLstBillStatus.add(DBConstants.ST_BILL_DISCARD);
			lLstBillStatus.add(DBConstants.ST_BILL_ARCHEIVED);
						
			lLstRecoveryType.add(gObjRsrcBndle.getString("PPMT.ADVANCE"));
			lLstRecoveryType.add(gObjRsrcBndle.getString("PPMT.ASSESSEDDUES"));
			lLstRecoveryType.add(gObjRsrcBndle.getString("PPMT.HBA"));
			lLstRecoveryType.add(gObjRsrcBndle.getString("PPMT.MCA"));
			lLstRecoveryType.add(gObjRsrcBndle.getString("PPMT.OTHER"));
			lLstRecoveryType.add(gObjRsrcBndle.getString("PPMT.OVERPAY"));
			lLstRecoveryType.add(gObjRsrcBndle.getString("PPMT.LICENSEFEE"));
			lLstRecoveryType.add(gObjRsrcBndle.getString("PPMT.ARREAR"));
						
			gObjRptQueryDAO = new PensionpayQueryDAOImpl(TrnPensionTransferDtls.class, gObjSessionFactory);
			lLstPnsnrDtls = gObjRptQueryDAO.getOverPaymentRecoveryDtls(lIntFromMonthYear, lIntToMonthYear,lLstBillStatus,lLstRecoveryType,lStrLocCode,gLngLangId);
			lLstMonthlyRecovery = gObjRptQueryDAO.getMonthlyRecoveryDtls(lIntFromMonthYear, lIntToMonthYear, lLstRecoveryType,lLstBillStatus, lStrLocCode);
			
			if(lLstPnsnrDtls != null && !lLstPnsnrDtls.isEmpty())
			{
				for(int lIntCnt=0;lIntCnt<lLstPnsnrDtls.size();lIntCnt++)
				{
					Object[] obj=(Object[])lLstPnsnrDtls.get(lIntCnt);
					String lStrKey = obj[0].toString() + "~" + obj[1].toString();
					lMapPnsnrDtls.put(lStrKey,obj);
				}
			}
			if(lLstMonthlyRecovery != null && !lLstMonthlyRecovery.isEmpty())
			{
				for(int lIntCnt=0;lIntCnt<lLstMonthlyRecovery.size();lIntCnt++)
				{
					Object[] obj= (Object[]) lLstMonthlyRecovery.get(lIntCnt);
					String lStrKey = obj[0].toString() + "~" + obj[1].toString();
					lMapMonthlyRecDtls.put(lStrKey,obj[2].toString());
 				}
			}
			
			//if (lMapPnsnrDtls != null) {
				
				rowList = new ArrayList();
				rowList.add(new StyledData("Sr. No.", boldBlackFontVO));
				rowList.add(new StyledData("Name of Pensioner and PPO No.", boldBlackFontVO));
				rowList.add(new StyledData("Volume No. / Page No.", boldBlackFontVO));
				rowList.add(new StyledData("Name of Bank Branch and A/C No.", boldBlackFontVO));
				rowList.add(new StyledData("Total Amount of Recovery", boldBlackFontVO));
				rowList.add(new StyledData("Reason for Over Payment", boldBlackFontVO));
				rowList.add(new StyledData("Total installments for recovery", boldBlackFontVO));
				rowList.add(new StyledData("Recovery start from date", boldBlackFontVO));
				rowList.add(new StyledData("Recovery effected till Date", boldBlackFontVO));
				
				dataList.add(rowList);
				
				//Iterator it = lLstRptData.iterator();
				for(String lStrKey : lMapPnsnrDtls.keySet())
				{
					Object[] tuple = (Object[]) lMapPnsnrDtls.get(lStrKey);
					
					String lStrPnsnrName = "";
					String lStrPnsnrAddr = "";
					String lStrMonth = "";
					String lStrYear = "";
					rowList = new ArrayList();
					rowList.add(lIntSrNoCnt);
					
					if (tuple[3] != null && tuple[4] != null) {
						rowList.add(tuple[3] + ","+ tuple[4]);
					} else {
						rowList.add("");
					}
					if (tuple[5] != null && tuple[6] != null) {
						rowList.add(tuple[5]+ " / " + tuple[6]);
					}
					else
					{
						rowList.add("");
					}
					if (tuple[7] != null && tuple[8] != null && tuple[9] != null) {
						rowList.add(tuple[7]+ " / " + tuple[8]+" / "+tuple[9]);
					}
					else
					{
						rowList.add("");
					}
					if (tuple[10] != null) {
						rowList.add(new StyledData(tuple[10], RightAlignVO));
						
					} else if(tuple[11] != null && (gObjRsrcBndle.getString("PPMT.OVERPAY").equals(tuple[15])
							|| gObjRsrcBndle.getString("PPMT.LICENSEFEE").equals(tuple[15]) || gObjRsrcBndle.getString("PPMT.ARREAR").equals(tuple[15]))){
						rowList.add(new StyledData(tuple[11], RightAlignVO));
					}
					else
					{
						rowList.add("");
					}
					if (tuple[2] != null) {
						rowList.add(tuple[2]);
					} else {
						rowList.add("");
					}
					if("SupplyPension".equals(tuple[14])){
						rowList.add("1-" +  tuple[11]);
					}
					else if (tuple[11] != null && (tuple[12] != null || (gObjRsrcBndle.getString("PPMT.OVERPAY").equals(tuple[15])
							|| gObjRsrcBndle.getString("PPMT.LICENSEFEE").equals(tuple[15]) || gObjRsrcBndle.getString("PPMT.ARREAR").equals(tuple[15])))) {
						if(tuple[12] == null)
							rowList.add("1-" +  tuple[11]);
						else
							rowList.add(tuple[12] + "-" +  tuple[11]);
					} else {
						rowList.add("");
					}
					
					if (tuple[13] != null) {
						String lStrMonthYear = tuple[13].toString();
						lStrYear = tuple[13].toString().substring(0, 4);
						lStrMonth = tuple[13].toString().substring(4);
						String lStrFromDate = "01/" + lStrMonth +"/"+ lStrYear;
						Date lDtFromDate = lObjDateFormat.parse(lStrFromDate);
						rowList.add(lObjDateFormat.format(lDtFromDate));
					} else {
						rowList.add("");
					}
					if (lMapMonthlyRecDtls != null && !"SupplyPension".equals(tuple[14])) {
						if(lMapMonthlyRecDtls.get(lStrKey) != null)
						{
							rowList.add(new StyledData(lMapMonthlyRecDtls.get(lStrKey), RightAlignVO));
						}
						else
						{
							rowList.add("");
						}
					} else if("SupplyPension".equals(tuple[14])){
						rowList.add(new StyledData(tuple[10], RightAlignVO));
					} else {
						rowList.add("");
					}
					
					dataList.add(rowList);
					++lIntSrNoCnt;
				}
			//}

		} catch (Exception e) {
			gLogger.error("Error is :" + e);
		}
		return dataList;
	}
	
	public List getStatePensionStatistics(ReportVO lObjReport, String lStrLocCode) {
		
	    List lLstRptData = new ArrayList();
		List dataList = new ArrayList();
		List rowList = null;
		List<Short> lLstBillStatus = new ArrayList<Short>();
		List<Long> lLstSubjectId = new ArrayList<Long>();
		try {
			Date lDtCurrDate = SessionHelper.getCurDate();			
			Calendar lObjCalendar = Calendar.getInstance();
			lObjCalendar.setTime(lDtCurrDate);
			Integer lIntMonth = lObjCalendar.get(lObjCalendar.MONTH);
			String lStrYYYYMM = lObjCalendar.get(lObjCalendar.YEAR) + "" + (lIntMonth < 10 ? "0" + lIntMonth : lIntMonth);
			Integer lIntForMonthYear = Integer.parseInt(lStrYYYYMM);
			
			lObjReport.setReportName("State Pension Statistics");
			
			StyleVO[] RightAlignVO = new StyleVO[3];
			RightAlignVO[0] = new StyleVO();
			RightAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			RightAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
			RightAlignVO[1] = new StyleVO();
			RightAlignVO[1].setStyleId(IReportConstants.BORDER);
			RightAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			RightAlignVO[2] = new StyleVO();
			RightAlignVO[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			RightAlignVO[2].setStyleValue("14");
			
			StyleVO[] boldBlackFontVO = new StyleVO[4];
			boldBlackFontVO[0] = new StyleVO();
			boldBlackFontVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			boldBlackFontVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
			boldBlackFontVO[1] = new StyleVO();
			boldBlackFontVO[1].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			boldBlackFontVO[1].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			boldBlackFontVO[2] = new StyleVO();
			boldBlackFontVO[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			boldBlackFontVO[2].setStyleValue("14");
			boldBlackFontVO[3] = new StyleVO();
			boldBlackFontVO[3].setStyleId(IReportConstants.BACKGROUNDCOLOR);
			boldBlackFontVO[3].setStyleValue("#D3D3D3");
			
			lLstBillStatus.add(DBConstants.ST_BILL_CREATED);
			lLstBillStatus.add(DBConstants.ST_BILL_FORW_TO_ATO);
			lLstBillStatus.add(DBConstants.ST_BILL_REJECTED);
			lLstBillStatus.add(DBConstants.ST_BILL_DISCARD);
			lLstBillStatus.add(DBConstants.ST_BILL_ARCHEIVED);
			
			lLstSubjectId.add(9L);
			lLstSubjectId.add(44L);
			
			String urlPrefix = "ifms.htm?actionFlag=reportService&reportCode=365486&action=generateReport&DirectReport=TRUE&displayOK=TRUE";
			
			gObjRptQueryDAO = new PensionpayQueryDAOImpl(TrnPensionTransferDtls.class, gObjSessionFactory);
			lLstRptData = gObjRptQueryDAO.getStatePensionStatisticsDtls(lIntForMonthYear, lLstBillStatus,lLstSubjectId);
			
			if (lLstRptData != null && !lLstRptData.isEmpty()) {
				rowList = new ArrayList();
							
				Iterator it = lLstRptData.iterator();
				while (it.hasNext()) {
					Object[] tuple = (Object[]) it.next();
					
					rowList = new ArrayList();
										
					if (tuple[0] != null) {
						rowList.add(new URLData(tuple[0].toString(), urlPrefix));
					} else {
						rowList.add("");
					}
					if (tuple[1] != null) {
						rowList.add(new URLData(new StyledData(tuple[1], RightAlignVO), urlPrefix));
					}
					else
					{
						rowList.add("");
					}
										
					dataList.add(rowList);
					
				}
			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e);
		}
		return dataList;
	}
	
	public List getTrsryWiseStatePensionStatistics(ReportVO lObjReport, String lStrLocCode) {

		  List lLstRptData = new ArrayList();
		  List dataList = new ArrayList();
		  List rowList = null;
		  List<Short> lLstBillStatus = new ArrayList<Short>();
		  List<Long> lLstSubjectId = new ArrayList<Long>();
		  try {
				Date lDtCurrDate = SessionHelper.getCurDate();			
				Calendar lObjCalendar = Calendar.getInstance();
				lObjCalendar.setTime(lDtCurrDate);
				Integer lIntMonth = lObjCalendar.get(lObjCalendar.MONTH);
				String lStrYYYYMM = lObjCalendar.get(lObjCalendar.YEAR) + "" + (lIntMonth < 10 ? "0" + lIntMonth : lIntMonth);
				Integer lIntForMonthYear = Integer.parseInt(lStrYYYYMM);
			
				lObjReport.setReportName("Treasury wise State Pension Statistics");
				
				StyleVO[] RightAlignVO = new StyleVO[3];
				RightAlignVO[0] = new StyleVO();
				RightAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
				RightAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
				RightAlignVO[1] = new StyleVO();
				RightAlignVO[1].setStyleId(IReportConstants.BORDER);
				RightAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
				RightAlignVO[2] = new StyleVO();
				RightAlignVO[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
				RightAlignVO[2].setStyleValue("14");
	
				String urlPrefix = "ifms.htm?actionFlag=reportService&reportCode=365487&action=generateReport&DirectReport=TRUE&displayOK=TRUE";
	
				lLstBillStatus.add(DBConstants.ST_BILL_CREATED);
				lLstBillStatus.add(DBConstants.ST_BILL_FORW_TO_ATO);
				lLstBillStatus.add(DBConstants.ST_BILL_REJECTED);
				lLstBillStatus.add(DBConstants.ST_BILL_DISCARD);
				lLstBillStatus.add(DBConstants.ST_BILL_ARCHEIVED);
				
				lLstSubjectId.add(9L);
				lLstSubjectId.add(44L);
				
				gObjRptQueryDAO = new PensionpayQueryDAOImpl(TrnPensionBillDtls.class, gObjSessionFactory);
				lLstRptData = gObjRptQueryDAO.getTrsyWiseStatePensionStatisticsDtls(lIntForMonthYear, lLstBillStatus,lLstSubjectId);
				if (lLstRptData != null && !lLstRptData.isEmpty()) {
	
					Iterator it = lLstRptData.iterator();
					while (it.hasNext()) {
	
						Object[] tuple = (Object[]) it.next();
						rowList = new ArrayList();
						if (tuple[0] != null) {
							rowList.add(tuple[0].toString());
						} else {
							rowList.add("");
						}
						if (tuple[1] != null) {
							rowList.add(tuple[1].toString());
						}
						else
						{
							rowList.add("");
						}
						if (tuple[2] != null) {
							rowList.add(new URLData(tuple[2].toString(), urlPrefix + "&locationCode=" + tuple[3]));
						}
						else {
							rowList.add("");
						}
						
						dataList.add(rowList);
	
					}
				}

		} catch (Exception e) {
			gLogger.error("Error is :" + e);
		}
		return dataList;
	}
	
	public List getPnsnTypeWisePensionStatistics(ReportVO lObjReport, String lStrLocCode) {

		  List lLstRptData = new ArrayList();
		  List dataList = new ArrayList();
		  List rowList = null;
		  List<Short> lLstBillStatus = new ArrayList<Short>();
		  List<Long> lLstSubjectId = new ArrayList<Long>();
		  StringBuilder lSbHeaderVal = new StringBuilder();
		  String lStrTreasuryName = "";
		  try {
			    String lStrLocationCode = (String) lObjReport.getParameterValue("locationCode");
			    			  
			    CommonDAO lObjCommonDAO = new CommonDAOImpl(gObjSessionFactory);
			    lStrTreasuryName = lObjCommonDAO.getTreasuryName(gLngLangId, Long.parseLong(lStrLocationCode));
			    
			    lSbHeaderVal.append("Pension Type wise State Pension Statistics \r\n\r\n");
			    lSbHeaderVal.append("Treasury Name : "+lStrTreasuryName);
			    lObjReport.setReportName(lSbHeaderVal.toString());
			    
				Date lDtCurrDate = SessionHelper.getCurDate();			
				Calendar lObjCalendar = Calendar.getInstance();
				lObjCalendar.setTime(lDtCurrDate);
				Integer lIntMonth = lObjCalendar.get(lObjCalendar.MONTH);
				String lStrYYYYMM = lObjCalendar.get(lObjCalendar.YEAR) + "" + (lIntMonth < 10 ? "0" + lIntMonth : lIntMonth);
				Integer lIntForMonthYear = Integer.parseInt(lStrYYYYMM);
			
				StyleVO[] RightAlignVO = new StyleVO[3];
				RightAlignVO[0] = new StyleVO();
				RightAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
				RightAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
				RightAlignVO[1] = new StyleVO();
				RightAlignVO[1].setStyleId(IReportConstants.BORDER);
				RightAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
				RightAlignVO[2] = new StyleVO();
				RightAlignVO[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
				RightAlignVO[2].setStyleValue("14");
	
				lLstBillStatus.add(DBConstants.ST_BILL_CREATED);
				lLstBillStatus.add(DBConstants.ST_BILL_FORW_TO_ATO);
				lLstBillStatus.add(DBConstants.ST_BILL_REJECTED);
				lLstBillStatus.add(DBConstants.ST_BILL_DISCARD);
				lLstBillStatus.add(DBConstants.ST_BILL_ARCHEIVED);
				
				lLstSubjectId.add(9L);
				lLstSubjectId.add(44L);
				
				gObjRptQueryDAO = new PensionpayQueryDAOImpl(TrnPensionBillDtls.class, gObjSessionFactory);
				lLstRptData = gObjRptQueryDAO.getPnsnTypeWisePensionStatisticsDtls(lIntForMonthYear, lLstBillStatus,lLstSubjectId,lStrLocationCode);
				if (lLstRptData != null && !lLstRptData.isEmpty()) {
	
					Iterator it = lLstRptData.iterator();
					while (it.hasNext()) {
	
						Object[] tuple = (Object[]) it.next();
						rowList = new ArrayList();
						if (tuple[0] != null) {
							rowList.add(tuple[0].toString());
						} else {
							rowList.add("");
						}
						if (tuple[1] != null) {
							rowList.add(new StyledData(tuple[1], RightAlignVO));
						}
						else
						{
							rowList.add("");
						}
						if (tuple[3] != null && !"".equals(tuple[3])) {
							rowList.add(gObjLblRsrcBndle.getString("PNSNTYPE." + tuple[3]));
						}
						else {
							rowList.add("Pension type not entered");
						}
																	
						dataList.add(rowList);
	
					}
				}

		} catch (Exception e) {
			gLogger.error("Error is :" + e);
		}
		return dataList;
	}
	
	public List getDeptWiseAgingAnalysisRpt(ReportVO lObjReport, String lStrLocCode) {

		  List lLstRptData = new ArrayList();
		  List dataList = new ArrayList();
		  List rowList = null;
		  List<Short> lLstBillStatus = new ArrayList<Short>();
		  List<Long> lLstSubjectId = new ArrayList<Long>();
		  StringBuilder lSbHeaderVal = new StringBuilder();
		  try {
			    String lStrRetirementYear = (String) lObjReport.getParameterValue("retirementYear");
			    	
			    lSbHeaderVal.append("Departmentwise Aging Analysis \r\n");
			    
			    lObjReport.setReportName(lSbHeaderVal.toString());
			    
				String urlPrefix = "ifms.htm?actionFlag=reportService&reportCode=365489&action=generateReport&DirectReport=TRUE&displayOK=TRUE";
				
				Long lLngDepartmentId = Long.valueOf(gObjRsrcBndle.getString("PPMT.DEPARTMENTID"));
				
				gObjRptQueryDAO = new PensionpayQueryDAOImpl(TrnPensionBillDtls.class, gObjSessionFactory);
				lLstRptData = gObjRptQueryDAO.getDeptWiseAgingAnalysisDtls(lStrRetirementYear,lLngDepartmentId,gLngLangId);
				if (lLstRptData != null && !lLstRptData.isEmpty()) {
	
					Iterator it = lLstRptData.iterator();
					while (it.hasNext()) {
	
						Object[] tuple = (Object[]) it.next();
						rowList = new ArrayList();
						if (tuple[0] != null) {
							rowList.add(tuple[0].toString());
						} else {
							rowList.add("Department not entered");
						}
						
						if (tuple[1] != null && !"".equals(tuple[1])) {
							rowList.add(gObjLblRsrcBndle.getString("PNSNTYPE." + tuple[1]));
						}
						else {
							rowList.add("Pension type not entered");
						}
						
						if (tuple[2] != null) {
							rowList.add(new URLData(tuple[2].toString(), urlPrefix + "&departmentId=" + (tuple[3]==null ?"":tuple[3]) + "&pensionType=" + (tuple[1]==null?"":tuple[1]) + "&retirementYear=" +lStrRetirementYear));
						}
						else
						{
							rowList.add("");
						}
																	
						dataList.add(rowList);
	
					}
				}

		} catch (Exception e) {
			gLogger.error("Error is :" + e);
		}
		return dataList;
	}
	
	public List getTrsryDeptWiseAgingAnalysisRpt(ReportVO lObjReport, String lStrLocCode) {

		  List lLstRptData = new ArrayList();
		  List dataList = new ArrayList();
		  List rowList = null;
		  List<Short> lLstBillStatus = new ArrayList<Short>();
		  List<Long> lLstSubjectId = new ArrayList<Long>();
		  StringBuilder lSbHeaderVal = new StringBuilder();
		  String lStrDepartmentName = "";
		  try {
			    String lStrRetirementYear = (String) lObjReport.getParameterValue("retirementYear");
			    String lStrDepartmentId = (String) lObjReport.getParameterValue("departmentId");
			    String lStrPensionType = (String) lObjReport.getParameterValue("pensionType");
			    			  
			    PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnPensionBillDtls.class, gObjSessionFactory);
			    
			    Long lLngDepartmentId = Long.valueOf(gObjRsrcBndle.getString("PPMT.DEPARTMENTID"));
			    if(lStrDepartmentId != null && !"".equals(lStrDepartmentId))
			    	lStrDepartmentName = lObjPhysicalCaseInwardDAO.getDepartmentName(lLngDepartmentId, Long.parseLong(lStrDepartmentId), gLngLangId);
			    
			    lSbHeaderVal.append("Treasury Departmentwise Aging Analysis \r\n\r\n");
			    lSbHeaderVal.append("Retirement Year : " +lStrRetirementYear + "      ");
			    if(lStrPensionType != null && !"".equals(lStrPensionType))
			    	lSbHeaderVal.append("Pension Type : " + gObjLblRsrcBndle.getString("PNSNTYPE." + lStrPensionType) + "\r\n");
			    else
			    	lSbHeaderVal.append("Pension Type : Pension type not entered \r\n");
			    lSbHeaderVal.append("Department Name : " +lStrDepartmentName +"\r\n");
			    
			    lObjReport.setReportName(lSbHeaderVal.toString());
			    
				String urlPrefix = "ifms.htm?actionFlag=reportService&reportCode=365490&action=generateReport&DirectReport=TRUE&displayOK=TRUE";
				
				gObjRptQueryDAO = new PensionpayQueryDAOImpl(TrnPensionBillDtls.class, gObjSessionFactory);
				lLstRptData = gObjRptQueryDAO.getTrsryDeptWiseAgingAnalysisDtls(lStrRetirementYear,lStrDepartmentId,lStrPensionType);
				if (lLstRptData != null && !lLstRptData.isEmpty()) {
	
					Iterator it = lLstRptData.iterator();
					while (it.hasNext()) {
	
						Object[] tuple = (Object[]) it.next();
						rowList = new ArrayList();
						if (tuple[0] != null) {
							rowList.add(tuple[0].toString());
						} else {
							rowList.add("");
						}
						if (tuple[1] != null) {
							rowList.add(new URLData(tuple[1].toString(), urlPrefix + "&departmentId=" + lStrDepartmentId + "&pensionType=" + lStrPensionType + "&retirementYear=" +lStrRetirementYear + "&locationCode=" + tuple[2]));
						}
						else
						{
							rowList.add("");
						}
																	
						dataList.add(rowList);
	
					}
				}

		} catch (Exception e) {
			gLogger.error("Error is :" + e);
		}
		return dataList;
	}
	
	public List getPnsnrTrsryDeptWiseAgingAnalysisRpt(ReportVO lObjReport, String lStrLocCode) {

		  List lLstRptData = new ArrayList();
		  List dataList = new ArrayList();
		  List rowList = null;
		  List<Short> lLstBillStatus = new ArrayList<Short>();
		  List<Long> lLstSubjectId = new ArrayList<Long>();
		  StringBuilder lSbHeaderVal = new StringBuilder();
		  String lStrTreasuryName = "";
		  String lStrDepartmentName = "";
		  try {
			    String lStrRetirementYear = (String) lObjReport.getParameterValue("retirementYear");
			    String lStrDepartmentId = (String) lObjReport.getParameterValue("departmentId");
			    String lStrPensionType = (String) lObjReport.getParameterValue("pensionType");
			    String lStrLocationCode = (String) lObjReport.getParameterValue("locationCode");
			    			   
			    CommonDAO lObjCommonDAO = new CommonDAOImpl(gObjSessionFactory);
			    lStrTreasuryName = lObjCommonDAO.getTreasuryName(gLngLangId, Long.parseLong(lStrLocationCode));
			    PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnPensionBillDtls.class, gObjSessionFactory);
			    
			    Long lLngDepartmentId = Long.valueOf(gObjRsrcBndle.getString("PPMT.DEPARTMENTID"));
			    if(lStrDepartmentId != null && !"".equals(lStrDepartmentId))
			    	lStrDepartmentName = lObjPhysicalCaseInwardDAO.getDepartmentName(lLngDepartmentId, Long.parseLong(lStrDepartmentId), gLngLangId);
			    
			    lSbHeaderVal.append("Pensionerwise Aging Analysis \r\n\r\n");
			    lSbHeaderVal.append("Retirement Year : " +lStrRetirementYear + "\r\n" + "Department Name : "+lStrDepartmentName+ "\r\n");
			    if(lStrPensionType != null && !"".equals(lStrPensionType))
			    	lSbHeaderVal.append("Pension Type : " +gObjLblRsrcBndle.getString("PNSNTYPE." + lStrPensionType) + "       "+"Treasury Name : "+lStrTreasuryName);
			    else
			    	lSbHeaderVal.append("Pension Type :        "+"Treasury Name : "+lStrTreasuryName);
			    
			    lObjReport.setReportName(lSbHeaderVal.toString());
			    
				gObjRptQueryDAO = new PensionpayQueryDAOImpl(TrnPensionBillDtls.class, gObjSessionFactory);
				lLstRptData = gObjRptQueryDAO.getPnsnrTrsryDeptWiseAgingAnalysisDtls(lStrRetirementYear,lStrDepartmentId,lStrPensionType,lStrLocationCode);
				if (lLstRptData != null && !lLstRptData.isEmpty()) {
	
					Iterator it = lLstRptData.iterator();
					while (it.hasNext()) {
	
						Object[] tuple = (Object[]) it.next();
						rowList = new ArrayList();
						if (tuple[0] != null) {
							rowList.add(tuple[0].toString());
						} else {
							rowList.add("");
						}
						if (tuple[1] != null) {
							rowList.add(tuple[1]);
						}
						else
						{
							rowList.add("");
						}
																	
						dataList.add(rowList);
	
					}
				}

		} catch (Exception e) {
			gLogger.error("Error is :" + e);
		}
		return dataList;
	}
	
	public List getTrsryWiseAgingAnalysisRpt(ReportVO lObjReport, String lStrLocCode) {

		  List lLstRptData = new ArrayList();
		  List dataList = new ArrayList();
		  List rowList = null;
		  List<Short> lLstBillStatus = new ArrayList<Short>();
		  List<Long> lLstSubjectId = new ArrayList<Long>();
		 
		  try {
			    String lStrRetirementYear = (String) lObjReport.getParameterValue("retirementYear");
			   
				String urlPrefix = "ifms.htm?actionFlag=reportService&reportCode=365492&action=generateReport&DirectReport=TRUE&displayOK=TRUE";
				
				lObjReport.setReportName("Treasurywise Aging Analysis \r\n");
				
				gObjRptQueryDAO = new PensionpayQueryDAOImpl(TrnPensionBillDtls.class, gObjSessionFactory);
				lLstRptData = gObjRptQueryDAO.getTrsryWiseAgingAnalysisDtls(lStrRetirementYear);
				if (lLstRptData != null && !lLstRptData.isEmpty()) {
	
					Iterator it = lLstRptData.iterator();
					while (it.hasNext()) {
	
						Object[] tuple = (Object[]) it.next();
						rowList = new ArrayList();
						if (tuple[1] != null) {
							rowList.add(tuple[1].toString());
						} else {
							rowList.add("");
						}
						
						if (tuple[2] != null && !"".equals(tuple[2])) {
							rowList.add(gObjLblRsrcBndle.getString("PNSNTYPE." + tuple[2]));
						}
						else {
							rowList.add("Pension type not entered");
						}
						
						if (tuple[3] != null) {
							rowList.add(new URLData(tuple[3].toString(), urlPrefix + "&retirementYear=" +lStrRetirementYear + "&locationCode="+ tuple[0] + "&pensionType=" + (tuple[2] == null? "":tuple[2])));
						}
						else
						{
							rowList.add("");
						}
																	
						dataList.add(rowList);
	
					}
				}

		} catch (Exception e) {
			gLogger.error("Error is :" + e);
		}
		return dataList;
	}
	
	public List getTrsryPnsnrWiseAgingAnalysisRpt(ReportVO lObjReport, String lStrLocCode) {

		  List lLstRptData = new ArrayList();
		  List dataList = new ArrayList();
		  List rowList = null;
		  StringBuilder lSbHeaderVal = new StringBuilder();
		  String lStrTreasuryName = "";
		  try {
			    String lStrRetirementYear = (String) lObjReport.getParameterValue("retirementYear");
			    String lStrPensionType = (String) lObjReport.getParameterValue("pensionType");
			    String lStrLocationCode = (String) lObjReport.getParameterValue("locationCode");
			   	
			    CommonDAO lObjCommonDAO = new CommonDAOImpl(gObjSessionFactory);
			    lStrTreasuryName = lObjCommonDAO.getTreasuryName(gLngLangId, Long.parseLong(lStrLocationCode));
			    
			    lSbHeaderVal.append("Pensionerwise Aging Analysis \r\n\r\n");
			    lSbHeaderVal.append("Retirement Year : " +lStrRetirementYear + "\r\n");
			    if(lStrPensionType != null && !"".equals(lStrPensionType))
			    	lSbHeaderVal.append("Pension Type : " +gObjLblRsrcBndle.getString("PNSNTYPE." + lStrPensionType) + "\r\n"+"Treasury Name : "+lStrTreasuryName);
			    else
			    	lSbHeaderVal.append("Pension Type :     \r\n"+"Treasury Name : "+lStrTreasuryName);
			    
			    lObjReport.setReportName(lSbHeaderVal.toString());
			    
				gObjRptQueryDAO = new PensionpayQueryDAOImpl(TrnPensionBillDtls.class, gObjSessionFactory);
				lLstRptData = gObjRptQueryDAO.getTrsryPnsnrWiseAgingAnalysisDtls(lStrRetirementYear,lStrLocationCode,lStrPensionType);
				if (lLstRptData != null && !lLstRptData.isEmpty()) {
	
					Iterator it = lLstRptData.iterator();
					while (it.hasNext()) {
	
						Object[] tuple = (Object[]) it.next();
						rowList = new ArrayList();
						if (tuple[0] != null) {
							rowList.add(tuple[0].toString());
						} else {
							rowList.add("");
						}
						if (tuple[1] != null) {
							rowList.add(tuple[1]);
						}
						else
						{
							rowList.add("");
						}
																	
						dataList.add(rowList);
	
					}
				}

		} catch (Exception e) {
			gLogger.error("Error is :" + e);
		}
		return dataList;
	}
	
	public List getCasesUploadedByAGRpt(ReportVO lObjReport, String lStrLocCode) {

		  List lLstRptData = new ArrayList();
		  List dataList = new ArrayList();
		  List rowList = null;
		  try {
			  	String urlPrefix = "ifms.htm?actionFlag=reportService&reportCode=365494&action=generateReport&DirectReport=TRUE&displayOK=TRUE";
			  
			  	lObjReport.setReportName("Cases Uploaded by AG");
				gObjRptQueryDAO = new PensionpayQueryDAOImpl(TrnPensionBillDtls.class, gObjSessionFactory);
				lLstRptData = gObjRptQueryDAO.getCasesUploadedByAGDtls();
				if (lLstRptData != null && !lLstRptData.isEmpty()) {
	
					Iterator it = lLstRptData.iterator();
					while (it.hasNext()) {
	
						Object[] tuple = (Object[]) it.next();
						rowList = new ArrayList();
						if (tuple[1] != null) {
							rowList.add(tuple[1].toString());
						} else {
							rowList.add("");
						}
						if (tuple[2] != null) {
							rowList.add(new URLData(tuple[2].toString(), urlPrefix + "&locationCode="+ tuple[0]));
							
						}
						else
						{
							rowList.add("");
						}
																	
						dataList.add(rowList);
	
					}
				}

		} catch (Exception e) {
			gLogger.error("Error is :" + e);
		}
		return dataList;
	}
	
	public List getTrsrywiseCasesUploadedByAGRpt(ReportVO lObjReport, String lStrLocCode) {

		  List lLstRptData = new ArrayList();
		  List dataList = new ArrayList();
		  List rowList = null;
		  String lStrTreasuryName = "";
		  StringBuilder lSbHeaderVal = new StringBuilder();
		  try {
			    String lStrLocationCode = (String) lObjReport.getParameterValue("locationCode");	
			    
			    CommonDAO lObjCommonDAO = new CommonDAOImpl(gObjSessionFactory);
			    lStrTreasuryName = lObjCommonDAO.getTreasuryName(gLngLangId, Long.parseLong(lStrLocationCode));
			    
			    lSbHeaderVal.append("Fin Year wise Cases Uploaded by AG \r\n\r\n");
			    lSbHeaderVal.append("Treasury Name  :  " + lStrTreasuryName);
			    lObjReport.setReportName(lSbHeaderVal.toString());
			    
				gObjRptQueryDAO = new PensionpayQueryDAOImpl(TrnPensionBillDtls.class, gObjSessionFactory);
				lLstRptData = gObjRptQueryDAO.getTrsrywiseCasesUploadedByAGDtls(lStrLocationCode);
				if (lLstRptData != null && !lLstRptData.isEmpty()) {
	
					Iterator it = lLstRptData.iterator();
					while (it.hasNext()) {
	
						Object[] tuple = (Object[]) it.next();
						rowList = new ArrayList();
						if (tuple[0] != null) {
							rowList.add(tuple[0].toString());
						} else {
							rowList.add("");
						}
						if (tuple[1] != null) {
							rowList.add(tuple[1]);
							
						}
						else
						{
							rowList.add("");
						}
																	
						dataList.add(rowList);
	
					}
				}

		} catch (Exception e) {
			gLogger.error("Error is :" + e);
		}
		return dataList;
	}
	
	public List getTotalNonIdentifiedPnsnrRpt(ReportVO lObjReport, String lStrLocCode) {

		  Integer lIntNoOfPnsnr = 0;
		  List dataList = new ArrayList();
		  List rowList = new ArrayList();
		  List lLstCaseStatus = new ArrayList<String>();
		  String lStrStatus = "";
		  try {
			  	lObjReport.setReportName("Treasurywise Pensioner Identification");
			  	
			  	lStrStatus = gObjRsrcBndle.getString("STATUS.CONTINUE");   
			  	
			  	lLstCaseStatus.add(gObjRsrcBndle.getString("STATFLG.NEW"));
			  	lLstCaseStatus.add(gObjRsrcBndle.getString("STATFLG.REGISTERED"));
			  	lLstCaseStatus.add(gObjRsrcBndle.getString("STATFLG.INWARDNEW"));
			  	
			  	String urlPrefix = "ifms.htm?actionFlag=reportService&reportCode=365496&action=generateReport&DirectReport=TRUE&displayOK=TRUE";
			  	
				gObjRptQueryDAO = new PensionpayQueryDAOImpl(TrnPensionBillDtls.class, gObjSessionFactory);
				lIntNoOfPnsnr = gObjRptQueryDAO.getTotalNonIdentifiedPnsnr(lStrStatus,lLstCaseStatus);
				
	            rowList.add(new URLData(lIntNoOfPnsnr, urlPrefix));
																									
				dataList.add(rowList);
	
		} catch (Exception e) {
			gLogger.error("Error is :" + e);
		}
		return dataList;
	}
	
	public List getTrsryWiseNonIdentifiedPnsnrRpt(ReportVO lObjReport, String lStrLocCode) {

		  List lLstRptData = new ArrayList();
		  List dataList = new ArrayList();
		  List rowList = new ArrayList();
		  List lLstCaseStatus = new ArrayList<String>();
		  String lStrStatus = "";
		  try {
			  	lObjReport.setReportName("Treasurywise Pensioner Identification");
			  	lStrStatus = gObjRsrcBndle.getString("STATUS.CONTINUE");   
			  	
			  	lLstCaseStatus.add(gObjRsrcBndle.getString("STATFLG.NEW"));
			  	lLstCaseStatus.add(gObjRsrcBndle.getString("STATFLG.REGISTERED"));
			  	lLstCaseStatus.add(gObjRsrcBndle.getString("STATFLG.INWARDNEW"));
			  	
			  	String urlPrefix = "ifms.htm?actionFlag=reportService&reportCode=365497&action=generateReport&DirectReport=TRUE&displayOK=TRUE";
			  	
				gObjRptQueryDAO = new PensionpayQueryDAOImpl(TrnPensionBillDtls.class, gObjSessionFactory);
				lLstRptData = gObjRptQueryDAO.getTrsryWiseNonIdentifiedPnsnr(lStrStatus,lLstCaseStatus);
				
				if (lLstRptData != null && !lLstRptData.isEmpty()) {
					
					Iterator it = lLstRptData.iterator();
					while (it.hasNext()) {
	
						Object[] tuple = (Object[]) it.next();
						rowList = new ArrayList();
						if (tuple[0] != null) {
							rowList.add(tuple[0].toString());
						} else {
							rowList.add("");
						}
						if (tuple[1] != null) {
							rowList.add(new URLData(tuple[1].toString(), urlPrefix + "&locationCode="+ tuple[2]));
													
						}
						else
						{
							rowList.add("");
						}
																	
						dataList.add(rowList);
	
					}
				}
	
		} catch (Exception e) {
			gLogger.error("Error is :" + e);
		}
		return dataList;
	}
	
	public List getPnsnrWiseNonIdentifiedPnsnrRpt(ReportVO lObjReport, String lStrLocCode) {

		  List lLstRptData = new ArrayList();
		  List dataList = new ArrayList();
		  List rowList = new ArrayList();
		  List lLstCaseStatus = new ArrayList<String>();
		  String lStrStatus = "";
		  String lStrTreasuryName = "";
		  try {
			  
			    String lStrLocationCode = (String) lObjReport.getParameterValue("locationCode");
			    
			    CommonDAO lObjCommonDAO = new CommonDAOImpl(gObjSessionFactory);
			    lStrTreasuryName = lObjCommonDAO.getTreasuryName(gLngLangId, Long.parseLong(lStrLocationCode));
			    
			    lObjReport.setReportName("Pensioner wise Identification \r\n\r\n" + "Treasury Name  :  " + lStrTreasuryName);
			    
			  	lStrStatus = gObjRsrcBndle.getString("STATUS.CONTINUE");   
			  	
			  	lLstCaseStatus.add(gObjRsrcBndle.getString("STATFLG.NEW"));
			  	lLstCaseStatus.add(gObjRsrcBndle.getString("STATFLG.REGISTERED"));
			  	lLstCaseStatus.add(gObjRsrcBndle.getString("STATFLG.INWARDNEW"));
			  	
				gObjRptQueryDAO = new PensionpayQueryDAOImpl(TrnPensionBillDtls.class, gObjSessionFactory);
				lLstRptData = gObjRptQueryDAO.getPnsnrWiseNonIdentifiedPnsnr(lStrStatus,lLstCaseStatus,lStrLocationCode);
				
				if (lLstRptData != null && !lLstRptData.isEmpty()) {
					
					Iterator it = lLstRptData.iterator();
					while (it.hasNext()) {
	
						Object[] tuple = (Object[]) it.next();
						rowList = new ArrayList();
						if (tuple[0] != null) {
							rowList.add(tuple[0].toString());
						} else {
							rowList.add("");
						}
						if (tuple[1] != null) {
							rowList.add(tuple[1]);
							
						}
						else
						{
							rowList.add("No Designation enetered");
						}
																	
						dataList.add(rowList);
	
					}
				}
	
		} catch (Exception e) {
			gLogger.error("Error is :" + e);
		}
		return dataList;
	}
	
	public List getTrsryWiseRevisedPpoByAGRpt(ReportVO lObjReport, String lStrLocCode) {

		  List lLstRevisedData = new ArrayList();
		  List lLstNonRevisedData = new ArrayList();
		  List dataList = new ArrayList();
		  List rowList = new ArrayList();
		  Map lMapRevised = new HashMap();
		  Map lMapNonRevised = new HashMap();
		  List<ComboValuesVO> lLstTreasury = new ArrayList<ComboValuesVO>();
		  List<Long> lLstDepartmentId = new ArrayList<Long>();
		  Map<String,String> lMapLocation = new HashMap<String,String>();
		  Map<String,String> lMapRevisedData = new HashMap<String,String>();
		  Map<String,String> lMapNonRevisedData = new HashMap<String,String>();
		  Set lSetLocCode = new HashSet();
		  try {
			  	lObjReport.setReportName("Treasury wise Revised Cases");
			  	
			    lLstDepartmentId.add(Long.valueOf(gObjRsrcBndle.getString("PPMT.TREASURYID1")));
			    lLstDepartmentId.add(Long.valueOf(gObjRsrcBndle.getString("PPMT.TREASIRYID2")));
				
			  	String urlPrefix = "ifms.htm?actionFlag=reportService&reportCode=365499&action=generateReport&DirectReport=TRUE&displayOK=TRUE";
			  	CommonDAO lObjCommonDAO = new CommonDAOImpl(gObjSessionFactory);
			  	lLstTreasury = lObjCommonDAO.getAllTreasury(gLngLangId, lLstDepartmentId);
			  	if(lLstTreasury != null && !lLstTreasury.isEmpty())
			  	{
			  		for(ComboValuesVO lObjComboValueVO : lLstTreasury)
			  		{
			  			lMapLocation.put(lObjComboValueVO.getId(), lObjComboValueVO.getDesc());
			  		}
			  	}
			  	
				gObjRptQueryDAO = new PensionpayQueryDAOImpl(TrnPensionBillDtls.class, gObjSessionFactory);
				lLstRevisedData = gObjRptQueryDAO.getTrsryWiseRevisedPpoByAGDtls();
				lLstNonRevisedData = gObjRptQueryDAO.getTrsryWiseNonRevisedPpoByAGDtls();
				
				if (lLstRevisedData != null && !lLstRevisedData.isEmpty()) {
					Iterator it = lLstRevisedData.iterator();
					while (it.hasNext()) {
	
						Object[] obj = (Object[]) it.next();
						if(obj[0] != null && obj[1] != null)
						{
							lMapRevisedData.put(obj[0].toString(), obj[1].toString());
							lSetLocCode.add(obj[0].toString());
						}
						
					}
				}
				
				if (lLstNonRevisedData != null && !lLstNonRevisedData.isEmpty()) {
					Iterator it = lLstNonRevisedData.iterator();
					while (it.hasNext()) {
	
						Object[] obj = (Object[]) it.next();
						if(obj[0] != null && obj[1] != null)
						{
							lMapNonRevisedData.put(obj[0].toString(), obj[1].toString());
							lSetLocCode.add(obj[0].toString());
						}
					}
				}
				Object[] lArrLocCode=  lSetLocCode.toArray();
				for(int lIntCnt = 0;lIntCnt < lArrLocCode.length; lIntCnt++)
				{
					rowList = new ArrayList();
					rowList.add(lMapLocation.get(lArrLocCode[lIntCnt]));
					if(lMapRevisedData.containsKey(lArrLocCode[lIntCnt]))
						rowList.add(new URLData(lMapRevisedData.get(lArrLocCode[lIntCnt]), urlPrefix + "&locationCode="+ lArrLocCode[lIntCnt] + "&revisionFlag=Y"));
					else
						rowList.add(0);
					if(lMapNonRevisedData.containsKey(lArrLocCode[lIntCnt]))
						rowList.add(new URLData(lMapNonRevisedData.get(lArrLocCode[lIntCnt]), urlPrefix + "&locationCode="+ lArrLocCode[lIntCnt] + "&revisionFlag=N"));
					else
						rowList.add(0);
					dataList.add(rowList);
				}
							
	
		} catch (Exception e) {
			gLogger.error("Error is :" + e);
		}
		return dataList;
	}
	
	public List getPnsnrWiseRevisedPpoByAGRpt(ReportVO lObjReport, String lStrLocCode) {

		  List lLstRptData = new ArrayList();
		  List dataList = new ArrayList();
		  List rowList = new ArrayList();
		  List lLstCaseStatus = new ArrayList<String>();
		  String lStrStatus = "";
		  String lStrTreasuryName = "";
		  try {
			  
			    String lStrLocationCode = (String) lObjReport.getParameterValue("locationCode");
			    String lStrRevisionFlag = (String) lObjReport.getParameterValue("revisionFlag");
			    
			    CommonDAO lObjCommonDAO = new CommonDAOImpl(gObjSessionFactory);
			    lStrTreasuryName = lObjCommonDAO.getTreasuryName(gLngLangId, Long.parseLong(lStrLocationCode));
			    
			    if(lStrRevisionFlag != null && !"".equals(lStrRevisionFlag))
			    {
			    	if("Y".equals(lStrRevisionFlag))
			    		lObjReport.setReportName("Pensioner wise Revised Cases \r\n\r\n" + "Treasury Name  :  "+lStrTreasuryName);
			    	else
			    		lObjReport.setReportName("Pensioner wise Non Revised Cases \r\n\r\n" + "Treasury Name  :  "+lStrTreasuryName);
			    }
			    			    
			  	gObjRptQueryDAO = new PensionpayQueryDAOImpl(TrnPensionBillDtls.class, gObjSessionFactory);
				lLstRptData = gObjRptQueryDAO.getPnsnrWiseRevisedPpoByAGDtls(lStrLocationCode,lStrRevisionFlag);
				
				if (lLstRptData != null && !lLstRptData.isEmpty()) {
					
					Iterator it = lLstRptData.iterator();
					while (it.hasNext()) {
	
						Object[] tuple = (Object[]) it.next();
						rowList = new ArrayList();
						if (tuple[0] != null) {
							rowList.add(tuple[0].toString());
						} else {
							rowList.add("");
						}
						if (tuple[1] != null) {
							rowList.add(tuple[1]);
							
						}
						else
						{
							rowList.add("No Designation enetered");
						}
																	
						dataList.add(rowList);
	
					}
				}
	
		} catch (Exception e) {
			gLogger.error("Error is :" + e);
		}
		return dataList;
	}
	public List getPayComWisePensionerDtls(ReportVO lObjReport, String lStrLocCode) {

		  List lLstRptData = new ArrayList();
		  
		  try {
			  
			    String lStrTreasuryCode = (String) lObjReport.getParameterValue("treasuryCode");
			    String lStrRopType = (String) lObjReport.getParameterValue("ropType");
			    			    
			  	gObjRptQueryDAO = new PensionpayQueryDAOImpl(TrnPensionBillDtls.class, gObjSessionFactory);
				lLstRptData = gObjRptQueryDAO.getPayComWisePensionerDtls(lStrTreasuryCode,lStrRopType);
	
		} catch (Exception e) {
			gLogger.error("Error is :" + e);
		}
		return lLstRptData;
	}
	public List<ComboValuesVO> getRopType(Hashtable otherArgs, String lStrLangId, String lStrLocId) {

		List<ComboValuesVO> lLstTreasury = new ArrayList<ComboValuesVO>();
		ComboValuesVO lObjComboValueVO = null;

		try {
					lObjComboValueVO = new ComboValuesVO();
					lObjComboValueVO.setId(gObjRsrcBndle.getString("PPMT.ROPTYPE1986"));
					lObjComboValueVO.setDesc(gObjLblRsrcBndle.getString("PPMT.1986"));
					lLstTreasury.add(lObjComboValueVO);
					
					lObjComboValueVO = new ComboValuesVO();
					lObjComboValueVO.setId(gObjRsrcBndle.getString("PPMT.ROPTYPE1996"));
					lObjComboValueVO.setDesc(gObjLblRsrcBndle.getString("PPMT.1996"));
					lLstTreasury.add(lObjComboValueVO);
					
					lObjComboValueVO = new ComboValuesVO();
					lObjComboValueVO.setId(gObjRsrcBndle.getString("PPMT.ROPTYPE2006"));
					lObjComboValueVO.setDesc(gObjLblRsrcBndle.getString("PPMT.2006"));
					lLstTreasury.add(lObjComboValueVO);
		
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
		}

		return lLstTreasury;
	}
	
	public List<ComboValuesVO> getTreasuryList(Hashtable otherArgs, String lStrLangId, String lStrLocId) {

		Hashtable sessionKeys = (Hashtable) (otherArgs).get(IReportConstants.SESSION_KEYS);
		Map loginMap = (Map) sessionKeys.get("loginDetailsMap");
		Long lLngLangId = null;
		Long lLngPostId = null;
		if (loginMap.containsKey("langId")) {
			lLngLangId = (Long) loginMap.get("langId");
		}
		if(loginMap.containsKey("loggedInPost"))
		{
			lLngPostId = (Long) loginMap.get("loggedInPost");
		}
		CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(ServiceLocator.getServiceLocator().getSessionFactory());
		
		Long lLngLocationId = null;
		if(loginMap.containsKey("locationId"))
		{
			lLngLocationId = (Long) loginMap.get("locationId");
		}
		List<Object[]> lLstResult = new ArrayList<Object[]>();
		List<ComboValuesVO> lLstTreasury = new ArrayList<ComboValuesVO>();
		ComboValuesVO lObjComboValueVO = null;

		try {
			String lStrRoleId = lObjCommonPensionDAO.getRoleByPost(lLngPostId);
			
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT locationCode,locName   ");
			lSBQuery.append(" FROM CmnLocationMst ");
			lSBQuery.append(" WHERE cmnLanguageMst.langId =:langId AND departmentId IN (100003) ");
			
			if(lStrRoleId != null && !"".equals(lStrRoleId) && !gObjRsrcBndle.getString("PPMT.STATEADMIN").equals(lStrRoleId))
			{
				lSBQuery.append(" and locationCode = :locationCode ");
			}
			lSBQuery.append(" order by locName ");
			
			
			ghibSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("langId", lLngLangId);
			if(lStrRoleId != null && !"".equals(lStrRoleId) && !gObjRsrcBndle.getString("PPMT.STATEADMIN").equals(lStrRoleId))
			{
				lQuery.setLong("locationCode", lLngLocationId);
			}

			lLstResult = lQuery.list();

			if (lLstResult != null && lLstResult.size() > 0) {
				for (Object[] lArrObj : lLstResult) {
					lObjComboValueVO = new ComboValuesVO();
					lObjComboValueVO.setId(lArrObj[0].toString());
					lObjComboValueVO.setDesc(lArrObj[1].toString());
					lLstTreasury.add(lObjComboValueVO);
				}
			}

		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
		}

		return lLstTreasury;
	}

	public List<ComboValuesVO> getCaseStatusList(Hashtable otherArgs, String lStrLangId, String lStrLocId) {

		Hashtable sessionKeys = (Hashtable) (otherArgs).get(IReportConstants.SESSION_KEYS);
		List<ComboValuesVO> lLstCaseStatus = new ArrayList<ComboValuesVO>();
		try {
			
			    ComboValuesVO lObjComboValueVO = new ComboValuesVO();
				lObjComboValueVO.setId(gObjRsrcBndle.getString("STATFLG.IDENTIFIED"));
				lObjComboValueVO.setDesc(gObjRsrcBndle.getString("STATFLG.IDENTIFIED"));
				lLstCaseStatus.add(lObjComboValueVO);
					
				lObjComboValueVO = new ComboValuesVO();
				lObjComboValueVO.setId(gObjRsrcBndle.getString("STATFLG.NONIDENTIFIED"));
				lObjComboValueVO.setDesc("Non Identified");
				lLstCaseStatus.add(lObjComboValueVO);
									
		
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
		}

		return lLstCaseStatus;
	}

	
}
