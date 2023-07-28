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
package com.tcs.sgv.pensionproc.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.business.reports.ReportDataFinder;
import com.tcs.sgv.common.dao.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.ess.valueobject.OrgPostMst;
import com.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAO;
import com.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAOImpl;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcInwardPension;

/**
 * Class Description -
 * 
 * 
 * @author 365450
 * @version 0.1
 * @since JDK 5.0 Aug 16, 2011
 */
public class PensionprocReportDAOImpl extends DefaultReportDataFinder implements ReportDataFinder {

	private static final Logger gLogger = Logger.getLogger("PensionpayReports");
	private PensionprocQueryDAO gObjRptQueryDAO = null;
	ServiceLocator serviceLocator = null;
	SessionFactory gObjSessionFactory = null;
	String gStrLocCode = null;
	Long gLngLangId = null;
	Long gLngPostId = null;
	OrgPostMst gObjOrgPostMst = null;
	Map lMapSeriesHeadCode = null;
	Session ghibSession = null;

	/**
	 * Global Variable for Resource Bundle
	 */
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionproc/PensionCaseLabels");
	
	private final ResourceBundle gObjBundleConst  = ResourceBundle.getBundle("resources/pensionproc/PensionCaseConstants");

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
			gObjOrgPostMst = lObjLoginVO.getLoggedInPost();
			gLngPostId = gObjOrgPostMst.getPostId();
			ghibSession = gObjSessionFactory.getCurrentSession();
			gObjRptQueryDAO = new PensionprocQueryDAOImpl(null, gObjSessionFactory);

			if (lObjReport.getReportCode().equals("1000002")) {
				lLstDataList = getPensionCaseTrackingReport(lObjReport, gStrLocCode);
			}
			
			} catch (Exception e) {
			gLogger.info("findReportData(): Exception is" + e, e);
		}
		return lLstDataList;
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

	public List<ComboValuesVO> getPensionType(Hashtable otherArgs, String lStrLangId, String lStrLocCode) {

		List<ComboValuesVO> lLstPensionType = new ArrayList<ComboValuesVO>();
		ComboValuesVO lObjComboValueVO = null;
		try {
			String[] lStrArrConst = new String[] {gObjBundleConst.getString("PPROC.EXTRAORDINARY")
												,gObjBundleConst.getString("PPROC.SUPERANNU")
												,gObjBundleConst.getString("PPROC.VOLUNTARY64")
												,gObjBundleConst.getString("PPROC.VOLUNTARY65")
												,gObjBundleConst.getString("PPROC.RETIRING104")
												,gObjBundleConst.getString("PPROC.RETIRING105")
												,gObjBundleConst.getString("PPROC.ABSORPTION")
												,gObjBundleConst.getString("PPROC.INVALID")
												,gObjBundleConst.getString("PPROC.INJURY")
												,gObjBundleConst.getString("PPROC.COMPULSORY")
												,gObjBundleConst.getString("PPROC.COMPASSIONATE")
												,gObjBundleConst.getString("PPROC.COMPENSATION")
												,gObjBundleConst.getString("PPROC.FAMILYPNSN")
												,gObjBundleConst.getString("PPROC.GALLANTRY")};
			
			String[] lStrArrValue = new String[] {gObjRsrcBndle.getString("PNSNTYPE.EXTRAORDINARY")
												,gObjRsrcBndle.getString("PNSNTYPE.SUPERANNU")
												,gObjRsrcBndle.getString("PNSNTYPE.VOLUNTARY64")
												,gObjRsrcBndle.getString("PNSNTYPE.VOLUNTARY65")
												,gObjRsrcBndle.getString("PNSNTYPE.RETIRING104")
												,gObjRsrcBndle.getString("PNSNTYPE.RETIRING105")
												,gObjRsrcBndle.getString("PNSNTYPE.ABSORPTION")
												,gObjRsrcBndle.getString("PNSNTYPE.INVALID")
												,gObjRsrcBndle.getString("PNSNTYPE.INJURY")
												,gObjRsrcBndle.getString("PNSNTYPE.COMPULSORY")
												,gObjRsrcBndle.getString("PNSNTYPE.COMPASSIONATE")
												,gObjRsrcBndle.getString("PNSNTYPE.COMPENSATION")
												,gObjRsrcBndle.getString("PNSNTYPE.FAMILYPNSN")
												,gObjRsrcBndle.getString("PNSNTYPE.GALLANTRY")};
			

			
				for (Integer lInt=0;lInt<lStrArrValue.length;lInt++) {
					lObjComboValueVO = new ComboValuesVO();
					lObjComboValueVO.setId(lStrArrConst[lInt].toString());
					lObjComboValueVO.setDesc(lStrArrValue[lInt].toString());
					lLstPensionType.add(lObjComboValueVO);
				}
			

		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
		}

		return lLstPensionType;
	}
	
	public List getPensionCaseTrackingReport(ReportVO lObjReport, String lStrLocationCode) {

		List lArrReturn = new ArrayList();
		gObjRptQueryDAO = new PensionprocQueryDAOImpl(TrnPnsnProcInwardPension.class, gObjSessionFactory);
		String lStrFromDate = "";
		String lStrToDate = "";
		String lStrPensionType = "";
		String lStrRetirementDate = "";		
		String lStrPensionerName = "";
		String lStrSevaarthId = "";
		String lStrPpoNo = "";
		try {

			lStrFromDate = lObjReport.getParameterValue("InwardDatefrom").toString();
			lStrToDate = lObjReport.getParameterValue("InwardDateto").toString();
			
			if (!lObjReport.getParameterValue("pensionType").equals("-1")) {
				lStrPensionType = lObjReport.getParameterValue("pensionType").toString();
			}
			if (!lObjReport.getParameterValue("retirementDate").equals("")) {
				lStrRetirementDate = lObjReport.getParameterValue("retirementDate").toString();
			}
			if (!lObjReport.getParameterValue("pensionerName").equals("")) {
				lStrPensionerName = lObjReport.getParameterValue("pensionerName").toString().toUpperCase().trim();
			}
			if (!lObjReport.getParameterValue("sevaarthId").equals("")) {
				lStrSevaarthId = lObjReport.getParameterValue("sevaarthId").toString().toUpperCase().trim();
			}
			if (!lObjReport.getParameterValue("PPONo").equals("")) {
				lStrPpoNo = lObjReport.getParameterValue("PPONo").toString().trim().toUpperCase();
			}

			TrnPnsnProcInwardPensionDAO lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcInwardPension.class, gObjSessionFactory);
			
			Long isDdoOrDdoAsst = lObjTrnPnsnProcInwardPensionDAO.isDdoOrDdoAsst(gLngPostId);
			String lStrDdoCode = "";
			if(isDdoOrDdoAsst == 1L){
				lStrDdoCode = lObjTrnPnsnProcInwardPensionDAO.getDdoCodeForDDO(gLngPostId);
			}else if(isDdoOrDdoAsst == 2L){
				lStrDdoCode = lObjTrnPnsnProcInwardPensionDAO.getDdoCodeForDDOAsst(gLngPostId);
			}
			Long lLngDdoCode = 0L;
			if(!"".equals(lStrDdoCode))
				lLngDdoCode = Long.parseLong(lStrDdoCode);
			
			lArrReturn = gObjRptQueryDAO.getPensionCaseTrackingReport(lObjReport, lStrFromDate, lStrToDate,
					lStrPensionType, lStrRetirementDate, lStrPensionerName, lStrSevaarthId, lStrPpoNo,gStrLocCode,lLngDdoCode);
		} catch (Exception e) {
			gLogger.error(e.getMessage(), e);

		}
		return lArrReturn;
	}

}

