/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jun 16, 2011		Vihan Khatri								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.InterestCalculationDAO;
import com.tcs.sgv.dcps.dao.InterestCalculationDAOImpl;
import com.tcs.sgv.dcps.dao.NewRegDdoDAO;
import com.tcs.sgv.dcps.dao.NewRegDdoDAOImpl;
import com.tcs.sgv.dcps.dao.OfflineContriDAO;
import com.tcs.sgv.dcps.dao.OfflineContriDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstDcpsContribTerminationMonthly;
import com.tcs.sgv.dcps.valueobject.MstDcpsContribTerminationYearly;
import com.tcs.sgv.dcps.valueobject.MstDcpsContributionMonthly;
import com.tcs.sgv.dcps.valueobject.MstDcpsContributionYearly;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.TrnDcpsContribution;
import com.tcs.sgv.eis.service.IdGenerator;

/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 Jun 16, 2011
 */
public class InterestCalculationServiceImpl extends ServiceImpl implements
InterestCalculationService {

	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for LangId */
	Long gLngLangId = null;

	/* Global Variable for EmpId */
	Long gLngEmpId = null;

	/* Global Variable for Location Id */
	String gStrLocId = null;

	/* Global Variable for DB Id */
	Long gLngDBId = null;

	/* Global Variable for Current Date */
	Date gCurDate = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;

	private Locale gLclLocale = null; /* LOCALE */
	private String gStrPostId = null; /* STRING POST ID */
	private String gStrUserId = null; /* STRING USER ID */
	private HttpServletRequest request = null; /* REQUEST OBJECT */
	private ServiceLocator serv = null; /* SERVICE LOCATOR */
	private HttpSession session = null; /* SESSION */
	private String gStrLocale = null; /* STRING LOCALE */
	private Date gDtCurDate = null; /* CURRENT DATE */

	/* Resource bundle for the constants */
	private ResourceBundle gObjRsrcBndle = ResourceBundle
	.getBundle("resources/dcps/DCPSConstants");

	private final static Logger gLogger = Logger
	.getLogger(InterestCalculationServiceImpl.class);

	private static final Log logger = LogFactory
	.getLog(InterestCalculationServiceImpl.class); /* LOGGER */

	private void setSessionInfo(Map inputMap) throws Exception {

		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			session = request.getSession();
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gLclLocale = new Locale(SessionHelper.getLocale(request));
			gStrLocale = SessionHelper.getLocale(request);
			gLngLangId = SessionHelper.getLangId(inputMap);
			gLngPostId = SessionHelper.getPostId(inputMap);
			gStrPostId = gLngPostId.toString();
			gLngUserId = SessionHelper.getUserId(inputMap);
			gStrUserId = gLngUserId.toString();
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
			gLngDBId = SessionHelper.getDbId(inputMap);
			gDtCurDate = SessionHelper.getCurDate();
			gStrLocId = SessionHelper.getLocationId(inputMap).toString();

		} catch (Exception e) {
			throw e;
		}
	}
	public ResultObject loadInterestCalculation(Map<String, Object> inputMap) {
        
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
 		List lLstYears = null;
		List lLstTreasuries = null;
		List lLstTreasuriesAndSub=null;
		String selectedTreasury = null;
		String selectedDDO = null;
		List lListDdoList = null;
		List lListEmpList = null;

		try {

			setSessionInfo(inputMap);

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());
			InterestCalculationDAO lObjInterestCalculationDAO = new InterestCalculationDAOImpl(
					null, serv.getSessionFactory());
			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
					TrnDcpsContribution.class, serv.getSessionFactory());
			String lStrViewPageFor = StringUtility.getParameter("viewPageFor", request).trim();
			String lStrYear = StringUtility.getParameter("yearId", request).trim();
			String lStrGetEmps = StringUtility.getParameter("getEmpsForDDO",request).trim();

			lLstYears = lObjDcpsCommonDAO.getFinyearsAfterCurrYear();
			inputMap.put("YEARS", lLstYears);

			inputMap.put("selectedYear", lStrYear);

			String lStrFromDate = StringUtility.getParameter("fromDate", request).trim();
			String lStrToDate = StringUtility.getParameter("toDate", request).trim();
			inputMap.put("fromDate", lStrFromDate);
			inputMap.put("toDate", lStrToDate);

			if(!"".equals(lStrViewPageFor))
			{
				lLstTreasuries = lObjDcpsCommonDAO.getAllTreasuriesForInterest();
				inputMap.put("TREASURIES", lLstTreasuries);

			}

			if(lStrViewPageFor.equals("Treasury"))
			{
				inputMap.put("TreasuryChecked", "Y");
				inputMap.put("DDOChecked", "N");
				inputMap.put("EMPChecked", "N");

			}

			if(lStrViewPageFor.equals("DDO"))
			{
				inputMap.put("TreasuryChecked", "N");
				inputMap.put("DDOChecked", "Y");
				inputMap.put("EMPChecked", "N");
				lLstTreasuriesAndSub=lObjDcpsCommonDAO.getAllTreasuriesAndSubTreasuriesForInterest();
				inputMap.put("TREASURIES", lLstTreasuriesAndSub);
			}

			if(lStrViewPageFor.equals("Emp"))
			{
				inputMap.put("TreasuryChecked", "N");
				inputMap.put("DDOChecked", "N");
				inputMap.put("EMPChecked", "Y");
				lLstTreasuriesAndSub=lObjDcpsCommonDAO.getAllTreasuriesAndSubTreasuriesForInterest();
				inputMap.put("TREASURIES", lLstTreasuriesAndSub);
				if(lStrGetEmps.equals("Yes"))
				{
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
					SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");


					selectedTreasury = StringUtility.getParameter("treasuryCode", request);
					inputMap.put("selectedTreasury", selectedTreasury);
					lListDdoList = lObjOfflineContriDAO.getAllDDOCodeASC(selectedTreasury);
					//lListDdoList = lObjDcpsCommonDAO.getAllDDOForTreasury(selectedTreasury);
					selectedDDO = StringUtility.getParameter("ddoCode", request).trim();
					inputMap.put("DDOList", lListDdoList);
					inputMap.put("selectedDDO", selectedDDO);
					//lListEmpList = lObjInterestCalculationDAO.getAllEmpsUnderDDO(selectedDDO);

					String lStrFromDatePassed = StringUtility.getParameter("fromDate", request).trim();
					String lStrToDatePassed = StringUtility.getParameter("toDate", request).trim();

					if(!"".equals(lStrFromDatePassed))
					{
						lStrFromDatePassed = sdf2.format(sdf1.parse(lStrFromDatePassed));
					}
					if(!"".equals(lStrToDatePassed))
					{
						lStrToDatePassed = sdf2.format(sdf1.parse(lStrToDatePassed));
					}
					lListEmpList=lObjInterestCalculationDAO.getAllEmpsUnderDDO(selectedTreasury,selectedDDO,lStrFromDatePassed, lStrToDatePassed);
					inputMap.put("EmpList", lListEmpList);
				}
			}

			resObj.setResultValue(inputMap);
			resObj.setViewName("InterestCalculation");

		} catch (Exception e) {
			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error in loadInterestCalculation " + e, e);
			return resObj;
		}

		return resObj;

	}

	/*public ResultObject calculateDCPSYearlyInterest(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		Boolean lBlFlag = false;
		List listAllEmpsForIntrstCalc = new ArrayList();
		List<Long> listAllEmpsForIntrstCalcLongValue = new ArrayList();
		//List listAllEmpsForIntrstCalcSixPC = new ArrayList();
		List listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc =  new ArrayList();

		// Added by ashish to display only eligible employee list
		List listAllEmpsEmpIdForIntrstCalc=new ArrayList();

		List listAllEmpsEmpIdsForIntrstCalcForMissingCredits = new ArrayList();
		List listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits = new ArrayList();
		List listAllEmpsDCPSIdsForIntrstCalc = new ArrayList();
		List<MstDcpsContributionYearly> lLstMstDcpsContributionYearly =  new ArrayList();
		List<MstDcpsContributionMonthly> lLstMstDcpsContributionMonthly =  new ArrayList();
		List lListAllEmpContriAndIntrstDtls = new ArrayList();
		List lListAllEmpContriAndIntrstDtlsForMissingCredits = new ArrayList();
		List lListAllEmpSixPCDtls = new ArrayList();
		List lListEmpDetailsForGivenEmp = null;
		List lListSixPCDtlsForGivenEmp = null;
		Long dcpsEmpId = null;
		String dcpsId = null;
		Long yearId = null;
		Long previousFinYearId = null;
		Long YearIdForVoucherDate = null;
		Long treasuryCode = null;
		List listContriDtlsForGivenEmp = null;
		String lStrVoucherDate = "";
		Long lLongVoucherNo = null;
		String lStrVoucherNo = "";
		Double lDoubleVoucherContriForMonth = 0d;
		Double lDoubleVoucherContriForMonthEmplr = 0d;
		Date lDateContriToDate = null;
		Date lDateContriFromDate = null;
		Date lDateVoucherDate = null;
		Date lDateFinYearEndDate = null;
		Integer DaysLeftTillYearEnd = null;
		Integer DaysLeft = null;
		Double InterestRate = 0d;
		Double InterestRateMonthly = 0d;
		Double InterestRateEffectiveForOpenInt = 0d;
		List lIntRateDetailsForGivenYear = null;
		//Double interestForCurrentYear = 0d;
		//Double interestForCurrentYearEmplr = 0d;
		Double interestForCurrentMonth = 0d;
		Double interestForCurrentMonthEmplr = 0d;
		Double totalOfAllMonthlyContributionsForYear = 0d;
		Double totalOfAllMonthlyContributionsForYearEmplr = 0d;
		String lStrFinYearStartDate = null;
		String lStrFinYearEndDate = null;
		SgvcFinYearMst lObjSgvcFinYearMst = null;
		SgvcFinYearMst lObjSgvcFinYearMstForVoucher = null;
		MstEmp lObjMstEmp  = null;

		Date lDateContriIntWiseStartDate = null;
		Date lDateContriIntWiseEndDate = null;

		String lStrArrearsLimitDate = null;
		Date lDateArrearsLimitDate = null;

		Long dcpsContributionYearlyId = null;
		MstDcpsContributionYearly lObjMstDcpsContributionYearly = null;
		MstDcpsContributionYearly MstDcpsContributionYearlyForInsertion = null;
		MstDcpsContributionYearly lObjMstDcpsContributionYearlyForCurrentYear = null;
		Integer NumberOfDaysInGivenYear = null;
		String lStrYearCodeForVoucher = null;

		Double openEmployee = 0d;
		Double openEmployer = 0d;
		Double openNet = 0d;
		Double openInt = 0d;
		Double openIntForYear = 0d;
		Double closeEmployee = 0d;
		Double closeEmployer = 0d;
		Double openIntPreviousYear = 0d;
		Double interestForCurrentYearEmployee = 0d;
		Double interestForCurrentYearEmployer = 0d;
		Double contributionForCurrentYearEmployee = 0d;
		Double contributionForCurrentYearEmployer = 0d;
		Double closeNet = 0d;

		String lStrPostEmpContriDoneOrNot = null;

		//variables added for tier 2
		List listArrearsDtlsForGivenEmp = null;
		Integer lIntDaysLeftForSixPCIntrstCalc = 0;
		Double lDoubleOpenBalanceTier2 = 0d;
		Double lDoubleCloseBalanceTier2 = 0d ;
		Double lArrearAmountTier2 = 0d ;
		Double lDoubleInterestTier2 = 0d ;
		Object[] lObjArrearsDtls = null;

		Long lLongDcpsContriId = null;

		MstDcpsContributionMonthly MstDcpsContributionMonthlyForInsertion = null;
		Long dcpsContributionMonthlyId = null;

		Integer lIntVMonth = null;
		Long vMonth = null;
		Integer lIntVYear = null;
		Long vYear = null;
		Calendar lObjCalendar = null;
		Long lLongPayMonth = null;
		Long lLongPayYear = null;
		Long payMonth = null;
		Long payYear = null;
		Integer NumberOfDaysInGivenYearForSixPC = null;
		Long payYearTier2 = null;

		String lStrVoucherNoTier2 = "";
		String lStrVoucherDateTier2 = "";
		Date lDateVoucherDateTier2 = null;
		Calendar lObjCalendarTier2 = null;
		Integer lIntVMonthTier2 = null;
		Integer lIntVYearTier2 = null;
		Long vMonthTier2 = null;
		Long vYearTier2 = null;

		String lStrTypeOfPayment = null;

		List lListDcpsContriIdsPk = null;

		lListDcpsContriIdsPk = new ArrayList();

		// No of samples decided here so as to restrict the no of values pass into IN clause of HQL query
		Double lDoubleNoOfSamples = 1000d;
		Integer lIntNoOfSamples = lDoubleNoOfSamples.intValue();
		String val = "";
		try {

			setSessionInfo(inputMap);

			HibernateTemplate hitStg = new HibernateTemplate(serv.getSessionFactory());

			InterestCalculationDAO lObjInterestCalculationDAO = new InterestCalculationDAOImpl(MstDcpsContributionYearly.class, serv.getSessionFactory());
			InterestCalculationDAO lObjInterestCalculationMonthlyDAO = new InterestCalculationDAOImpl(MstDcpsContributionMonthly.class, serv.getSessionFactory());

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(
					SgvcFinYearMst.class, serv.getSessionFactory());
			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class, serv.getSessionFactory());

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

			if (!StringUtility.getParameter("yearId", request).trim().equalsIgnoreCase("")) {
				yearId = Long.valueOf(StringUtility.getParameter("yearId",request).trim());
			}
			if (!StringUtility.getParameter("treasuryCode", request).trim().equalsIgnoreCase("")) {
				treasuryCode = Long.valueOf(StringUtility.getParameter("treasuryCode", request).trim());
			}

			String ddoCode = StringUtility.getParameter("ddoCode", request).trim();

			lObjSgvcFinYearMst = (SgvcFinYearMst) lObjDcpsCommonDAO.read(yearId);

			lStrFinYearStartDate = lObjSgvcFinYearMst.getFromDate().toString().trim();
			lStrFinYearEndDate = lObjSgvcFinYearMst.getToDate().toString().trim();

			String lStrFromDatePassed = StringUtility.getParameter("fromDate", request).trim();
			String lStrToDatePassed = StringUtility.getParameter("toDate", request).trim();

			if(!"".equals(lStrFromDatePassed))
			{
				lStrFromDatePassed = sdf2.format(sdf1.parse(lStrFromDatePassed));
			}
			if(!"".equals(lStrToDatePassed))
			{
				lStrToDatePassed = sdf2.format(sdf1.parse(lStrToDatePassed));
			}

			// Never ever change below line as gives financial year end date which is required to calculate the days remaining form the voucher date
			lDateFinYearEndDate = lObjSgvcFinYearMst.getToDate();
			lStrArrearsLimitDate =  gObjRsrcBndle.getString("DCPS.ArrearsLimitDateForInterestCalc");
			lStrArrearsLimitDate = lStrArrearsLimitDate.trim().concat(lObjSgvcFinYearMst.getFinYearCode()).trim();
			lDateArrearsLimitDate = sdf1.parse(lStrArrearsLimitDate);

			previousFinYearId = lObjSgvcFinYearMst.getPrevFinYearId();

			GregorianCalendar grgcal = new GregorianCalendar();

			if (grgcal.isLeapYear(Integer.parseInt(lObjSgvcFinYearMst.getFinYearCode()) + 1)) {
				NumberOfDaysInGivenYear = 366;
			} else {
				NumberOfDaysInGivenYear = 365;
			}

			Double lDoubleInterestDays = 0d; 

			lIntRateDetailsForGivenYear = lObjInterestCalculationDAO.getAllIntRateDtlsForGivenYear(yearId);

			Object[] lObjArrIntDtls = null;

			Integer lIntDays = 0;

			for(Integer lInt=0;lInt<lIntRateDetailsForGivenYear.size();lInt++)
			{
				lObjArrIntDtls = (Object[]) lIntRateDetailsForGivenYear.get(lInt);
				lIntDays = ((sdf.parse(lObjArrIntDtls[1].toString().trim())),(sdf.parse(lObjArrIntDtls[2].toString().trim()))) + 1;;
				lDoubleInterestDays = lDoubleInterestDays + (Double.parseDouble(lObjArrIntDtls[0].toString()) * lIntDays) ;
			}

			InterestRateEffectiveForOpenInt = lDoubleInterestDays / (NumberOfDaysInGivenYear);

			InterestRateMonthly = InterestRateEffectiveForOpenInt;

			String lStrInterestFor = StringUtility.getParameter("interestFor",request).trim();


			//listAllEmpsDCPSIdsForIntrstCalc = lObjInterestCalculationDAO.getDCPSIdsForDcpsEmpIds(listAllEmpsForIntrstCalcLongValue);

			if(lStrInterestFor.equals("Treasury") || lStrInterestFor.equals("DDO"))
			{
				//listAllEmpsForIntrstCalc = lObjInterestCalculationDAO.getAllDCPSEmployeesForIntrstCalc(treasuryCode,ddoCode,lStrFromDatePassed, lStrToDatePassed);
				listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc = lObjInterestCalculationDAO.getAllDCPSEmployeesForIntrstCalc(treasuryCode,ddoCode,lStrFromDatePassed, lStrToDatePassed);

				Object []lObjDCPSEmpIdAndDCPSId = null;
				if(listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc != null && listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc.size() > 0){

					for(Integer lCount=0;lCount < listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc.size();lCount++)
					{
						lObjDCPSEmpIdAndDCPSId = (Object[]) listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc.get(lCount);
						listAllEmpsForIntrstCalc.add((BigInteger)lObjDCPSEmpIdAndDCPSId[0]);
						listAllEmpsDCPSIdsForIntrstCalc.add(lObjDCPSEmpIdAndDCPSId[1].toString());
					}
					for(Integer lCount=0;lCount<listAllEmpsForIntrstCalc.size();lCount++)
					{
						listAllEmpsForIntrstCalcLongValue.add(((BigInteger)listAllEmpsForIntrstCalc.get(lCount)).longValue());
					}
				}

				// Gets all DCPS_EMP_IDs which are eligible for Missing Credits
				listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits = lObjInterestCalculationDAO.getAllDCPSEmployeesForIntrstCalcForMissingCredits(treasuryCode,ddoCode,lStrFromDatePassed, lStrToDatePassed);

				// Below for loop adds DCPS_EMP_Ids related to Missing Credits to the main list of DCPS_EMP_Ids if it is not there in the main list.
				for(Integer lInt=0;lInt<listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits.size();lInt++)
				{
					lObjDCPSEmpIdAndDCPSId = (Object[]) listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits.get(lInt);
					listAllEmpsEmpIdsForIntrstCalcForMissingCredits.add(((BigInteger)lObjDCPSEmpIdAndDCPSId[0]).longValue());
					if(!(listAllEmpsForIntrstCalc.contains(lObjDCPSEmpIdAndDCPSId[0])))
						{
							listAllEmpsForIntrstCalc.add((BigInteger)lObjDCPSEmpIdAndDCPSId[0]);
							listAllEmpsForIntrstCalcLongValue.add(((BigInteger)lObjDCPSEmpIdAndDCPSId[0]).longValue());
							listAllEmpsDCPSIdsForIntrstCalc.add(lObjDCPSEmpIdAndDCPSId[1].toString());
						}
				}
			}
			else if(lStrInterestFor.equals("Emp"))
			{

				// Added by ashish to display only eligible employee list


				String lStrEmpIds = StringUtility.getParameter("empIds", request).trim();
				String[] lStrArrdcpsEmpIds = lStrEmpIds.split("~");
				Long[] lLongArrdcpsEmpIds = new Long[lStrArrdcpsEmpIds.length];
				for (Integer lInt = 0; lInt < lStrArrdcpsEmpIds.length; lInt++) {
					lLongArrdcpsEmpIds[lInt] = Long.valueOf(lStrArrdcpsEmpIds[lInt]);



					if(lObjInterestCalculationDAO.checkEmployeeEligibleForIntrstCalc(lLongArrdcpsEmpIds[lInt], lStrFromDatePassed, lStrToDatePassed,yearId))
					{
						listAllEmpsForIntrstCalc.add(lLongArrdcpsEmpIds[lInt]);
						listAllEmpsEmpIdsForIntrstCalcForMissingCredits.add(lLongArrdcpsEmpIds[lInt]);

					}


				}
				for(Integer lCount=0;lCount<listAllEmpsForIntrstCalc.size();lCount++)
				{
					listAllEmpsForIntrstCalcLongValue.add((Long) listAllEmpsForIntrstCalc.get(lCount));
				}
				listAllEmpsDCPSIdsForIntrstCalc = lObjInterestCalculationDAO.getDCPSIdsForDcpsEmpIds(listAllEmpsForIntrstCalcLongValue);
			}

			Integer lLongTotalInsertsInYearly = listAllEmpsForIntrstCalc.size();

			// Below code generates long values list of all DCPS_EMP_IDs fetched which are BigInteger 



			// Method that gets all DCPS_IDs for dcps_emp_ids


			// Code that deletes all records of mst_dcps_contribution_yearly and mst_dcps_contribution_monthly table for all above dcps_emp_ids.


			// Code that gets all contribution and interest details in single query for all the employees in the emp list
			// Only start_date,end_date,year_id,prvs_year_id and listOfEmps are used in below method

			// Map prepared for storing contributions details for all employees


			lListAllEmpContriAndIntrstDtls = lObjInterestCalculationDAO.getContriDtlsForGivenEmployeeListFinal(
					lStrFromDatePassed, lStrToDatePassed,previousFinYearId,listAllEmpsForIntrstCalcLongValue);


			// Code added to sample the main list into batches and then pass into IN clause of HQL queries and finally consolidate all lists

			Integer lIntCountWithinMainListEmpIdForContri = (int) Math.ceil(listAllEmpsForIntrstCalcLongValue.size() / lDoubleNoOfSamples);
			Integer lIntIncrementalCountEmpIdForContri = 0; 
			Integer lIntNextIncrementalCountEmpIdForContri = 0;

			List lListDcpsEmpIdsPkSampled = null;
			List lListTempContriAndIntrstDtls = null;
			List lListTempSixPCDtls = null;

			for(Integer lIntOutCount = 1; lIntOutCount <=  lIntCountWithinMainListEmpIdForContri; lIntOutCount++ )
			{
				lListDcpsEmpIdsPkSampled = new ArrayList();
				lListTempContriAndIntrstDtls = new ArrayList();

				if(lIntOutCount == lIntCountWithinMainListEmpIdForContri)
				{
					lIntNextIncrementalCountEmpIdForContri =  lIntIncrementalCountEmpIdForContri + (listAllEmpsForIntrstCalcLongValue.size() % lIntNoOfSamples);
				}
				else
				{
					lIntNextIncrementalCountEmpIdForContri =  lIntIncrementalCountEmpIdForContri + lIntNoOfSamples;
				}

				for(Integer lIntCount = lIntIncrementalCountEmpIdForContri ; lIntCount < lIntNextIncrementalCountEmpIdForContri ; lIntCount++ )
				{
					lListDcpsEmpIdsPkSampled.add(listAllEmpsForIntrstCalcLongValue.get(lIntCount));
					lIntIncrementalCountEmpIdForContri = lIntCount + 1;
				}


				lListTempContriAndIntrstDtls = lObjInterestCalculationDAO.getContriDtlsForGivenEmployeeListFinal(
						lStrFromDatePassed, lStrToDatePassed,previousFinYearId,lListDcpsEmpIdsPkSampled);
				lListAllEmpContriAndIntrstDtls.addAll(lListTempContriAndIntrstDtls);

				lListTempSixPCDtls = lObjInterestCalculationDAO.getArrearsDtlsForAllEmps(lListDcpsEmpIdsPkSampled,yearId);
				if(lListTempSixPCDtls != null)
				{
					if(lListTempSixPCDtls.size() != 0)
					{
						lListAllEmpSixPCDtls.addAll(lListTempSixPCDtls);
					}
				}
			}

			// Code of sampling ends

			Map lMapEmpDetailsForEmp = new HashMap();
			Long lLongEmpId = null;
			Object[] tuple = null;

			if (lListAllEmpContriAndIntrstDtls != null && !lListAllEmpContriAndIntrstDtls.isEmpty())
			{
				Iterator it = lListAllEmpContriAndIntrstDtls.iterator();

				while (it.hasNext())
				{
					tuple = (Object[]) it.next();
					lLongEmpId = Long.valueOf(tuple[10].toString());
					lListEmpDetailsForGivenEmp = (List) lMapEmpDetailsForEmp.get(lLongEmpId);
					if(lListEmpDetailsForGivenEmp != null)
					{
						lListEmpDetailsForGivenEmp.add(tuple);
					}
					else
					{
						lListEmpDetailsForGivenEmp = new ArrayList();
						lListEmpDetailsForGivenEmp.add(tuple);
					}
					lMapEmpDetailsForEmp.put(lLongEmpId, lListEmpDetailsForGivenEmp);
				}
			}

			// Above Map creation overs

			// Map prepared for storing Six PC arrears of employees
//			lListAllEmpSixPCDtls = lObjInterestCalculationDAO.getArrearsDtlsForAllEmps(listAllEmpsForIntrstCalcLongValue,yearId);
			Map lMapEmpDetailsForEmpSixPC = new HashMap();
			Long lLongEmpIdSixPC = null;
			Object[] tupleSixPC = null;

			if(lListAllEmpSixPCDtls != null && !lListAllEmpSixPCDtls.isEmpty())
			{
				Iterator itSixPC = lListAllEmpSixPCDtls.iterator();
				while(itSixPC.hasNext())
				{
					tupleSixPC = (Object[]) itSixPC.next();
					lLongEmpIdSixPC = Long.valueOf(tupleSixPC[1].toString());
					lListSixPCDtlsForGivenEmp = (List) lMapEmpDetailsForEmpSixPC.get(lLongEmpIdSixPC);
					if(lListSixPCDtlsForGivenEmp != null)
					{
						lListSixPCDtlsForGivenEmp.add(tupleSixPC);
					}
					else
					{
						lListSixPCDtlsForGivenEmp = new ArrayList();
						lListSixPCDtlsForGivenEmp.add(tupleSixPC);
					}
					lMapEmpDetailsForEmpSixPC.put(lLongEmpIdSixPC, lListSixPCDtlsForGivenEmp);
				}
			}

			// Code for Calculating Interests of Missing Credits starts

			if (listAllEmpsEmpIdsForIntrstCalcForMissingCredits != null && !listAllEmpsEmpIdsForIntrstCalcForMissingCredits.isEmpty())
			{
				lListAllEmpContriAndIntrstDtlsForMissingCredits =  lObjInterestCalculationDAO.getContriDtlsForGivenEmployeeListFinalForMissingCredits(
						lStrFromDatePassed,lStrToDatePassed,yearId,listAllEmpsEmpIdsForIntrstCalcForMissingCredits);
			}

			// Below Iterator generates map of DCPS_CONTRIBUTION_ID versus all year's data till current financial year

			Map lMapContriDtlsForDcpsContriIdMissingCredits = new HashMap();
			Long lLongDcpsContriIdForMissingCredit = null;
			Object[] tupleForDcpsContriIdForMissingCredit = null;
			List lListDcpsContriDtlsForDcpsContriIdForMissingCredit = new ArrayList(); 

			if (lListAllEmpContriAndIntrstDtlsForMissingCredits != null && !lListAllEmpContriAndIntrstDtlsForMissingCredits.isEmpty())
			{
				Iterator it = lListAllEmpContriAndIntrstDtlsForMissingCredits.iterator();

				while (it.hasNext())
				{
					tupleForDcpsContriIdForMissingCredit = (Object[]) it.next();
					lLongDcpsContriIdForMissingCredit = Long.valueOf(tupleForDcpsContriIdForMissingCredit[10].toString());
					lListDcpsContriDtlsForDcpsContriIdForMissingCredit = (List) lMapContriDtlsForDcpsContriIdMissingCredits.get(lLongDcpsContriIdForMissingCredit);
					if(lListDcpsContriDtlsForDcpsContriIdForMissingCredit != null)
					{
						lListDcpsContriDtlsForDcpsContriIdForMissingCredit.add(tupleForDcpsContriIdForMissingCredit);
					}
					else
					{
						lListDcpsContriDtlsForDcpsContriIdForMissingCredit = new ArrayList();
						lListDcpsContriDtlsForDcpsContriIdForMissingCredit.add(tupleForDcpsContriIdForMissingCredit);
					}
					lMapContriDtlsForDcpsContriIdMissingCredits.put(lLongDcpsContriIdForMissingCredit, lListDcpsContriDtlsForDcpsContriIdForMissingCredit);
				}
			}

			// Below Iterator generates map of DCPS_ID versus all DCPS_CONTRIBUTION_IDs

			Map lMapDcpsContriIdsListForDcpsIdForMissingCredits = new HashMap();
			String lStrDcpsIdForMissingCredit = null;
			List lListDcpsContriIdsListForDcpsIdForMissingCredit = new ArrayList();

			Integer lIntTotalMissingCreditsForAllEmps = 0;

			if (lListAllEmpContriAndIntrstDtlsForMissingCredits != null && !lListAllEmpContriAndIntrstDtlsForMissingCredits.isEmpty())
			{
				Iterator it = lListAllEmpContriAndIntrstDtlsForMissingCredits.iterator();

				while (it.hasNext())
				{
					tupleForDcpsContriIdForMissingCredit = (Object[]) it.next();
					lStrDcpsIdForMissingCredit = tupleForDcpsContriIdForMissingCredit[16].toString().trim();
					lListDcpsContriIdsListForDcpsIdForMissingCredit = (List) lMapDcpsContriIdsListForDcpsIdForMissingCredits.get(lStrDcpsIdForMissingCredit);
					if(lListDcpsContriIdsListForDcpsIdForMissingCredit != null)
					{
						if(!(lListDcpsContriIdsListForDcpsIdForMissingCredit.contains(tupleForDcpsContriIdForMissingCredit[10])))
						{
							lListDcpsContriIdsListForDcpsIdForMissingCredit.add(tupleForDcpsContriIdForMissingCredit[10]);
							lListDcpsContriIdsPk.add(Long.valueOf(tupleForDcpsContriIdForMissingCredit[10].toString()));
							lIntTotalMissingCreditsForAllEmps = lIntTotalMissingCreditsForAllEmps + 1;
						}
					}
					else
					{
						lListDcpsContriIdsListForDcpsIdForMissingCredit = new ArrayList();
						if(!(lListDcpsContriIdsListForDcpsIdForMissingCredit.contains(tupleForDcpsContriIdForMissingCredit[10])))
						{
							lListDcpsContriIdsListForDcpsIdForMissingCredit.add(tupleForDcpsContriIdForMissingCredit[10]);
							lListDcpsContriIdsPk.add(Long.valueOf(tupleForDcpsContriIdForMissingCredit[10].toString()));
							lIntTotalMissingCreditsForAllEmps = lIntTotalMissingCreditsForAllEmps + 1;
						}
					}
					lMapDcpsContriIdsListForDcpsIdForMissingCredits.put(lStrDcpsIdForMissingCredit, lListDcpsContriIdsListForDcpsIdForMissingCredit);
				}
			}

			// Below Code calculates Missing Credit's Interest for each DCPS_CONTRIBUTION_ID and generates a Map which has key as DCPS_CONTRIBUTION_ID and value as the Interest Calculated 

			Double lDoubleInterestForDcpsContriIdForMissingCredit = 0d;
			Double lDoubleContriAndInterestForDcpsContriIdForMissingCredit = 0d;
			Double lDoubleTotalContriAndInterestForDcpsIdForMissingCredit = 0d;
			Double lDoubleTotalContriForDcpsIdForMissingCredit = 0d;
			Double lDoubleInterestRateForDcpsContriMissingCredit = 0d;

			Double lDoubleDcpsContributionForDcpsContriIdForMsngCrdt = 0d;
			Integer lIntDaysForDcpsContriForMissingCredit = 0;
			Integer lIntTotalDaysInFinYearMsngCrdt = 0;
			Date lDateMissingCreditStartDateMsngCrdt = null;
			Date lDateMissingCreditEndDateMsngCrdt = null;
			Date lDateFinYearStartDateMsngCrdt = null;
			Date lDateFinYearEndDateMsngCrdt = null;

			Map lMapDcpsInterestAmountForDcpsContriIdForMissingCredits = new HashMap();

			Map lMapDcpsContriAmountForDcpsContriIdForMissingCredits = new HashMap();

			LinkedHashMap linkedHashMapContriDtlsYearWiseForMissingCreditForDcpsContriId = new LinkedHashMap();

			Iterator itDcpsContriDtlsMissingCredits = lMapContriDtlsForDcpsContriIdMissingCredits.keySet().iterator();

			Iterator itForLinkedHashMapYearWise = null;

			Long lLongYearIdForMissingCredit = null;

			List lListYearWiseMissingCreditDtlsForEmp = null;

			Integer lIntCountYearWiseForMissingCredit = 0;

			while(itDcpsContriDtlsMissingCredits.hasNext())
			{
				lLongDcpsContriIdForMissingCredit = Long.valueOf(itDcpsContriDtlsMissingCredits.next().toString());
				lListDcpsContriDtlsForDcpsContriIdForMissingCredit = (List) lMapContriDtlsForDcpsContriIdMissingCredits.get(lLongDcpsContriIdForMissingCredit);
				lDoubleContriAndInterestForDcpsContriIdForMissingCredit = 0d;
				lDoubleDcpsContributionForDcpsContriIdForMsngCrdt = 0d;

				// Linked HashMap prepared for preserving the same order

				linkedHashMapContriDtlsYearWiseForMissingCreditForDcpsContriId = new LinkedHashMap();

				if (lListDcpsContriDtlsForDcpsContriIdForMissingCredit != null && !lListDcpsContriDtlsForDcpsContriIdForMissingCredit.isEmpty())
				{
					itForLinkedHashMapYearWise = lListDcpsContriDtlsForDcpsContriIdForMissingCredit.iterator();

					while (itForLinkedHashMapYearWise.hasNext())
					{
						tuple = (Object[]) itForLinkedHashMapYearWise.next();
						lLongYearIdForMissingCredit = Long.valueOf(tuple[4].toString());
						lListYearWiseMissingCreditDtlsForEmp = (List) linkedHashMapContriDtlsYearWiseForMissingCreditForDcpsContriId.get(lLongYearIdForMissingCredit);
						if(lListYearWiseMissingCreditDtlsForEmp != null)
						{
							lListYearWiseMissingCreditDtlsForEmp.add(tuple);
						}
						else
						{
							lListYearWiseMissingCreditDtlsForEmp = new ArrayList();
							lListYearWiseMissingCreditDtlsForEmp.add(tuple);
						}
						linkedHashMapContriDtlsYearWiseForMissingCreditForDcpsContriId.put(lLongYearIdForMissingCredit, lListYearWiseMissingCreditDtlsForEmp);
					}
				}

				// Linked HashMap preparation overs

				// Calculation of Missing credits interest for missing credit by iterating the Linked HashMap

				Iterator itYearWiseLinkedHashMapForMissingCredit = linkedHashMapContriDtlsYearWiseForMissingCreditForDcpsContriId.keySet().iterator();
				Object[] tupleForDcpsContriIdForMissingCreditInsert = null;
				Date lDateVoucherDateForMissingCredit = null;

				lIntCountYearWiseForMissingCredit = 0;

				while(itYearWiseLinkedHashMapForMissingCredit.hasNext())
				{
					lLongYearIdForMissingCredit = Long.valueOf(itYearWiseLinkedHashMapForMissingCredit.next().toString());
					lListYearWiseMissingCreditDtlsForEmp = (List) linkedHashMapContriDtlsYearWiseForMissingCreditForDcpsContriId.get(lLongYearIdForMissingCredit);

					lDoubleInterestForDcpsContriIdForMissingCredit = 0d;

					for(Integer lInt=0;lInt<lListYearWiseMissingCreditDtlsForEmp.size();lInt++)
					{
						tupleForDcpsContriIdForMissingCredit = (Object[]) lListYearWiseMissingCreditDtlsForEmp.get(lInt);
						if(lInt == 0 && lIntCountYearWiseForMissingCredit == 0)
						{
							lDoubleDcpsContributionForDcpsContriIdForMsngCrdt = Double.valueOf(tupleForDcpsContriIdForMissingCredit[6].toString());
							lDoubleDcpsContributionForDcpsContriIdForMsngCrdt = lDoubleDcpsContributionForDcpsContriIdForMsngCrdt * 2;
							lDoubleContriAndInterestForDcpsContriIdForMissingCredit = lDoubleDcpsContributionForDcpsContriIdForMsngCrdt;
							// Above amount multiplied by two as Government's same contribution has also to be added.
						}

						lDateMissingCreditStartDateMsngCrdt = sdf.parse(tupleForDcpsContriIdForMissingCredit[0].toString().trim());
						lDateMissingCreditEndDateMsngCrdt = sdf.parse(tupleForDcpsContriIdForMissingCredit[1].toString().trim());
						lDateFinYearStartDateMsngCrdt = sdf.parse(tupleForDcpsContriIdForMissingCredit[2].toString().trim());
						lDateFinYearEndDateMsngCrdt = sdf.parse(tupleForDcpsContriIdForMissingCredit[3].toString().trim());
						lDoubleInterestRateForDcpsContriMissingCredit = Double.valueOf(tupleForDcpsContriIdForMissingCredit[14].toString().trim());

						lIntDaysForDcpsContriForMissingCredit = daysBetween(lDateMissingCreditStartDateMsngCrdt, lDateMissingCreditEndDateMsngCrdt) + 1;
						lIntTotalDaysInFinYearMsngCrdt = daysBetween(lDateFinYearStartDateMsngCrdt, lDateFinYearEndDateMsngCrdt) + 1;

						lDoubleInterestForDcpsContriIdForMissingCredit = lDoubleInterestForDcpsContriIdForMissingCredit + ((lDoubleContriAndInterestForDcpsContriIdForMissingCredit * lDoubleInterestRateForDcpsContriMissingCredit * lIntDaysForDcpsContriForMissingCredit * 0.01) / lIntTotalDaysInFinYearMsngCrdt);

					}

					lDoubleContriAndInterestForDcpsContriIdForMissingCredit = lDoubleContriAndInterestForDcpsContriIdForMissingCredit + lDoubleInterestForDcpsContriIdForMissingCredit;

					lIntCountYearWiseForMissingCredit++;

					lListYearWiseMissingCreditDtlsForEmp.clear();

				}

				// Overs the calculation of Missing credits interest for missing credit by iterating the Linked HashMap


				for(Integer lInt=0;lInt<lListDcpsContriDtlsForDcpsContriIdForMissingCredit.size();lInt++)
				{
					tupleForDcpsContriIdForMissingCredit = (Object[]) lListDcpsContriDtlsForDcpsContriIdForMissingCredit.get(lInt);
					if(lInt == 0)
					{
						lDoubleDcpsContributionForDcpsContriIdForMsngCrdt = Double.valueOf(tupleForDcpsContriIdForMissingCredit[6].toString());
						lDoubleDcpsContributionForDcpsContriIdForMsngCrdt = lDoubleDcpsContributionForDcpsContriIdForMsngCrdt * 2;
						lDoubleContriAndInterestForDcpsContriIdForMissingCredit = lDoubleDcpsContributionForDcpsContriIdForMsngCrdt;
						// Above amount multiplied by two as Government's same contribution has also to be added.
					}

					lDateMissingCreditStartDateMsngCrdt = sdf.parse(tupleForDcpsContriIdForMissingCredit[0].toString().trim());
					lDateMissingCreditEndDateMsngCrdt = sdf.parse(tupleForDcpsContriIdForMissingCredit[1].toString().trim());
					lDateFinYearStartDateMsngCrdt = sdf.parse(tupleForDcpsContriIdForMissingCredit[2].toString().trim());
					lDateFinYearEndDateMsngCrdt = sdf.parse(tupleForDcpsContriIdForMissingCredit[3].toString().trim());
					lDoubleInterestRateForDcpsContriMissingCredit = Double.valueOf(tupleForDcpsContriIdForMissingCredit[14].toString().trim());

					lIntDaysForDcpsContriForMissingCredit = daysBetween(lDateMissingCreditStartDateMsngCrdt, lDateMissingCreditEndDateMsngCrdt) + 1;
					lIntTotalDaysInFinYearMsngCrdt = daysBetween(lDateFinYearStartDateMsngCrdt, lDateFinYearEndDateMsngCrdt) + 1;

					lDoubleInterestForDcpsContriIdForMissingCredit = (lDoubleContriAndInterestForDcpsContriIdForMissingCredit * lDoubleInterestRateForDcpsContriMissingCredit * lIntDaysForDcpsContriForMissingCredit * 0.01) / lIntTotalDaysInFinYearMsngCrdt;
					lDoubleContriAndInterestForDcpsContriIdForMissingCredit = lDoubleContriAndInterestForDcpsContriIdForMissingCredit + lDoubleInterestForDcpsContriIdForMissingCredit;
				}


				lMapDcpsInterestAmountForDcpsContriIdForMissingCredits.put(lLongDcpsContriIdForMissingCredit, lDoubleContriAndInterestForDcpsContriIdForMissingCredit);
				lMapDcpsContriAmountForDcpsContriIdForMissingCredits.put(lLongDcpsContriIdForMissingCredit, lDoubleDcpsContributionForDcpsContriIdForMsngCrdt);

				linkedHashMapContriDtlsYearWiseForMissingCreditForDcpsContriId.clear();

				// Above map must be cleared, never delete the above line
			}

			// Below code iterates two Maps and calculates total Missing Credits' Interests for all missing credit contributions for all employees

			Map lMapTotalContriAndInterestForGivenDcpsIdForMissingCredit = new HashMap();
			Map lMapTotalContriForGivenDcpsIdForMissingCredit = new HashMap();

			Iterator itForAllDcpsContriIdsForGivenDcpsIdForMsngCrdt = lMapDcpsContriIdsListForDcpsIdForMissingCredits.keySet().iterator();
			while(itForAllDcpsContriIdsForGivenDcpsIdForMsngCrdt.hasNext())
			{
				lDoubleTotalContriAndInterestForDcpsIdForMissingCredit = 0d;
				lDoubleTotalContriForDcpsIdForMissingCredit = 0d;
				lStrDcpsIdForMissingCredit = itForAllDcpsContriIdsForGivenDcpsIdForMsngCrdt.next().toString();
				lListDcpsContriIdsListForDcpsIdForMissingCredit = (List) lMapDcpsContriIdsListForDcpsIdForMissingCredits.get(lStrDcpsIdForMissingCredit);
				for(Integer lInt=0;lInt<lListDcpsContriIdsListForDcpsIdForMissingCredit.size();lInt++)
				{
					lLongDcpsContriIdForMissingCredit = Long.valueOf(lListDcpsContriIdsListForDcpsIdForMissingCredit.get(lInt).toString());
					lDoubleContriAndInterestForDcpsContriIdForMissingCredit = Double.valueOf(lMapDcpsInterestAmountForDcpsContriIdForMissingCredits.get(lLongDcpsContriIdForMissingCredit).toString());
					lDoubleTotalContriAndInterestForDcpsIdForMissingCredit = lDoubleTotalContriAndInterestForDcpsIdForMissingCredit + lDoubleContriAndInterestForDcpsContriIdForMissingCredit;
					lDoubleTotalContriForDcpsIdForMissingCredit = lDoubleTotalContriForDcpsIdForMissingCredit + Double.valueOf(lMapDcpsContriAmountForDcpsContriIdForMissingCredits.get(lLongDcpsContriIdForMissingCredit).toString());
				}
				lMapTotalContriAndInterestForGivenDcpsIdForMissingCredit.put(lStrDcpsIdForMissingCredit,lDoubleTotalContriAndInterestForDcpsIdForMissingCredit);
				lMapTotalContriForGivenDcpsIdForMissingCredit.put(lStrDcpsIdForMissingCredit,lDoubleTotalContriForDcpsIdForMissingCredit);
			}

			// Code for Calculating Interests of Missing Credits overs

			Integer lLongTotalInsertsInMonthly = lListAllEmpContriAndIntrstDtls.size() + lListAllEmpSixPCDtls.size() + lIntTotalMissingCreditsForAllEmps;

			// Above Map creation overs

			// Iterator for all employee Ids of map
			//Iterator ItForMapEmps = lMapEmpDetailsForEmp.keySet().iterator() ;

			Long lLongLastCountInYearly = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("mst_dcps_contribution_yearly", inputMap, lLongTotalInsertsInYearly);
			Long lLongLastCountInMonthly = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("mst_dcps_contribution_monthly", inputMap, lLongTotalInsertsInMonthly);
			Iterator ItrEmps = listAllEmpsForIntrstCalc.iterator();

			Object[] tupleForEmp = null;
			Long lLongPrvsYearIntId =0l;

			Long lLongTreasuryCodeVoucherWiseTable = null;
			String lStrDdoCodeVoucherWiseTable = null;

			List lListEmpsNoRegularOnlyMissingOrChallan = new ArrayList();

			Map lMapEmpContriDtlsForGivenDcpsContriId = new HashMap();
			List lListContriDtlsForDcpsContriId = new ArrayList();

			while (ItrEmps.hasNext()) {

				dcpsEmpId = Long.valueOf(ItrEmps.next().toString());
				lListEmpDetailsForGivenEmp = (List) lMapEmpDetailsForEmp.get(dcpsEmpId);

				// Done Till here as on 19/03/2013 Vihan

				// gets DCPS Id of employee and close employee,employer,open interest and close tier 2 from the first record of the list for given emp


				dcpsId = lObjInterestCalculationDAO.getDcpsIdForEmpId(dcpsEmpId);
				lObjMstEmp = (MstEmp) lObjNewRegDdoDAO.read(dcpsEmpId) ;

				if(lObjMstEmp == null)
				{
					continue;
				}


				// Below list has the employees which do not have any regular contributions but only missing credits or challan
				if(lListEmpDetailsForGivenEmp == null)
				{
					lListEmpsNoRegularOnlyMissingOrChallan.add(dcpsEmpId);
					continue;
				}
				tupleForEmp = (Object[]) lListEmpDetailsForGivenEmp.get(0);

				dcpsId = tupleForEmp[11].toString();
				lStrDdoCodeVoucherWiseTable = tupleForEmp[12].toString();

				if("".equals(ddoCode))
				{
					if(tupleForEmp[19] != null)
					{
						ddoCode = tupleForEmp[19].toString();
					}
				}

				lLongTreasuryCodeVoucherWiseTable = Long.valueOf(tupleForEmp[18].toString());

				//lStrDdoCodeForEmp = lObjMstEmp.getDdoCode();


				lObjMstDcpsContributionYearly = lObjInterestCalculationDAO.getContriYearlyVOForYear(dcpsEmpId,previousFinYearId);

				if (lObjMstDcpsContributionYearly != null) {

					openEmployee = lObjMstDcpsContributionYearly.getCloseEmp();
					openEmployer = lObjMstDcpsContributionYearly.getCloseEmplr();
					openIntPreviousYear = lObjMstDcpsContributionYearly.getOpenInt();
					lDoubleOpenBalanceTier2 = lObjMstDcpsContributionYearly.getCloseTier2();

				} else {

					openEmployee = 0d;
					openEmployer = 0d;
					openIntPreviousYear = 0d;
					lDoubleOpenBalanceTier2 = 0d;
				}


				// Above code commented as it will hit queries in loop for every employee, instead used the Ids from first single bulk data fetching query

				lLongPrvsYearIntId = Long.valueOf(tupleForEmp[13].toString());
				if(lLongPrvsYearIntId == 0l)
				{
					openEmployee = 0d;
					openEmployer = 0d;
					openIntPreviousYear = 0d;
					lDoubleOpenBalanceTier2 = 0d;
				}
				else
				{
					openEmployee = Double.valueOf(tupleForEmp[14].toString());
					openEmployer = Double.valueOf(tupleForEmp[15].toString());
					openIntPreviousYear = Double.valueOf(tupleForEmp[16].toString());
					lDoubleOpenBalanceTier2 = Double.valueOf(tupleForEmp[17].toString());
				}

				openNet = openEmployee + openEmployer ;
				//openIntForYear = (openNet + openIntPreviousYear) * InterestRate ;
				//openInt = openIntForYear + openIntPreviousYear  ;

				// Above formula changed as below are correct as communicated by Sevaarth Team - Abhijeet
				openInt = openNet * InterestRateEffectiveForOpenInt * 0.01;

				//lObjMstDcpsContributionYearlyForCurrentYear = lObjInterestCalculationDAO.getContriYearlyVOForYear(dcpsEmpId, yearId);

				// deleted from here

					//listContriDtlsForGivenEmp = lObjInterestCalculationDAO.getContriDtlsForGivenEmployee(treasuryCode,lStrFromDatePassed, lStrToDatePassed,dcpsEmpId);

					totalOfAllMonthlyContributionsForYear = 0d;
					totalOfAllMonthlyContributionsForYearEmplr = 0d;
					interestForCurrentYearEmployee = 0d;
					interestForCurrentYearEmployer = 0d;

					// Code added to generate a Map of contribution details if interest rate is changed within year for given dcps_contribution_id for the employee

					lMapEmpContriDtlsForGivenDcpsContriId = new HashMap();
					lListContriDtlsForDcpsContriId = new ArrayList();

					if (lListEmpDetailsForGivenEmp != null && !lListEmpDetailsForGivenEmp.isEmpty())
					{
						Iterator itDtlsDcpsContriWise = lListEmpDetailsForGivenEmp.iterator();

						while (itDtlsDcpsContriWise.hasNext())
						{
							tuple = (Object[]) itDtlsDcpsContriWise.next();
							lLongDcpsContriId = Long.valueOf(tuple[5].toString());
							lListContriDtlsForDcpsContriId = (List) lMapEmpContriDtlsForGivenDcpsContriId.get(lLongDcpsContriId);
							if(lListContriDtlsForDcpsContriId != null)
							{
								lListContriDtlsForDcpsContriId.add(tuple);
							}
							else
							{
								lListContriDtlsForDcpsContriId = new ArrayList();
								lListContriDtlsForDcpsContriId.add(tuple);
							}
							lMapEmpContriDtlsForGivenDcpsContriId.put(lLongDcpsContriId, lListContriDtlsForDcpsContriId);
						}
					}

					//Iterator ItrContriDtlsForEmp = lListEmpDetailsForGivenEmp.iterator();

					//lObjInterestCalculationDAO.deleteMonthlyInterestForDCPSIdAndYear(dcpsId, yearId);

					Iterator itDcpsContriDtls = lMapEmpContriDtlsForGivenDcpsContriId.keySet().iterator();

					while (itDcpsContriDtls.hasNext()) {

						lLongDcpsContriId = Long.valueOf(itDcpsContriDtls.next().toString());
						lListContriDtlsForDcpsContriId = (List) lMapEmpContriDtlsForGivenDcpsContriId.get(lLongDcpsContriId);

						// gets only first element in the list.
						Object[] lObjVoucherDtls = (Object[]) lListContriDtlsForDcpsContriId.get(0);

						if(lObjVoucherDtls[0] != null)
						{
							lStrVoucherDate = lObjVoucherDtls[0].toString().trim();
						}

						if("".equals(lStrVoucherDate))
						{
							continue;
						}
						//lLongVoucherNo =  Long.valueOf(lObjVoucherDtls[6].toString().trim());
						if(lObjVoucherDtls[6] != null)
						{
							lStrVoucherNo = lObjVoucherDtls[6].toString().trim();
						}
						if("".equals(lStrVoucherNo))
						{
							lStrVoucherNo = "NOT AVAILABLE";
						}

						lDoubleVoucherContriForMonth = Double.valueOf(lObjVoucherDtls[1].toString());

						if(lObjVoucherDtls[4] != null)
						{
							lStrPostEmpContriDoneOrNot = lObjVoucherDtls[4].toString();
						}
						if(null != lStrPostEmpContriDoneOrNot)
						{
							if(lStrPostEmpContriDoneOrNot.equals("Y"))
							{
								lDoubleVoucherContriForMonthEmplr = lDoubleVoucherContriForMonth;
							}
						}

						// Below two statements updates the status flag of trn_dcps_contribution stating that the interest has been calculated.
						lLongDcpsContriId = Long.valueOf(lObjVoucherDtls[5].toString());
						//lObjInterestCalculationDAO.updateTrnDcpsContriIntCalculated(lLongDcpsContriId);

						lListDcpsContriIdsPk.add(lLongDcpsContriId);


						lStrYearCodeForVoucher = sdf1.format(lDateContriFromDate).substring(6, 10);
						YearIdForVoucherDate = lObjInterestCalculationDAO.getYearIdForYearCode(lStrYearCodeForVoucher);
						lObjSgvcFinYearMstForVoucher = (SgvcFinYearMst) lObjDcpsCommonDAO.read(YearIdForVoucherDate);
						lDateContriToDate = sdf2.parse(lObjSgvcFinYearMstForVoucher.getToDate().toString().trim());


						lDateVoucherDate = sdf.parse(lStrVoucherDate.trim());
						DaysLeftTillYearEnd = daysBetween(lDateVoucherDate,lDateFinYearEndDate) + 1;

						interestForCurrentMonth = 0d;
						interestForCurrentMonthEmplr = 0d;

						for(Integer lIntContri = 0; lIntContri < lListContriDtlsForDcpsContriId.size(); lIntContri++)
						{

							Object[] lObjContriDtlsForDcpsContriId = (Object[]) lListContriDtlsForDcpsContriId.get(lIntContri);

							logger.info("0th column is ::::"+lObjContriDtlsForDcpsContriId[0]);
							logger.info("1th column is ::::"+lObjContriDtlsForDcpsContriId[1]);
							logger.info("2th column is ::::"+lObjContriDtlsForDcpsContriId[2]);
							logger.info("3th column is ::::"+lObjContriDtlsForDcpsContriId[3]);
							logger.info("4th column is ::::"+lObjContriDtlsForDcpsContriId[4]);
							logger.info("5th column is ::::"+lObjContriDtlsForDcpsContriId[5]);
							logger.info("6th column is ::::"+lObjContriDtlsForDcpsContriId[6]);
							logger.info("7th column is ::::"+lObjContriDtlsForDcpsContriId[7]);
							logger.info("8th column is ::::"+lObjContriDtlsForDcpsContriId[8]);
							logger.info("9th column is ::::"+lObjContriDtlsForDcpsContriId[9]);
							logger.info("10th column is ::::"+lObjContriDtlsForDcpsContriId[10]);
							logger.info("20th column is ::::"+lObjContriDtlsForDcpsContriId[20]);
							logger.info("21th column is ::::"+lObjContriDtlsForDcpsContriId[21]);
							val = val +"~"+ lObjContriDtlsForDcpsContriId[9];
							InterestRateMonthly = Double.parseDouble(lObjContriDtlsForDcpsContriId[9].toString());

							lDateContriIntWiseStartDate = sdf.parse(lObjContriDtlsForDcpsContriId[20].toString().trim());
							lDateContriIntWiseEndDate = sdf.parse(lObjContriDtlsForDcpsContriId[21].toString().trim());

							DaysLeft = daysBetween(lDateContriIntWiseStartDate, lDateContriIntWiseEndDate) + 1;

							interestForCurrentMonth = interestForCurrentMonth + DaysLeft
	 * InterestRateMonthly * lDoubleVoucherContriForMonth
	 * 0.01 / NumberOfDaysInGivenYear;

							interestForCurrentMonthEmplr = interestForCurrentMonthEmplr + DaysLeft
	 * InterestRateMonthly * lDoubleVoucherContriForMonthEmplr
	 * 0.01 / NumberOfDaysInGivenYear;

						}

						logger.info("Days left are ::::"+DaysLeft);
						//interestForCurrentYear = interestForCurrentYear + interestForCurrentMonth;

						interestForCurrentYearEmployee = interestForCurrentYearEmployee + interestForCurrentMonth ;
						interestForCurrentYearEmployer = interestForCurrentYearEmployer + interestForCurrentMonthEmplr ;

						totalOfAllMonthlyContributionsForYear = totalOfAllMonthlyContributionsForYear + lDoubleVoucherContriForMonth;
						totalOfAllMonthlyContributionsForYearEmplr = totalOfAllMonthlyContributionsForYearEmplr + lDoubleVoucherContriForMonthEmplr;

						// Inserts entry mst_dcps_contribution_monthly table

						MstDcpsContributionMonthlyForInsertion = new MstDcpsContributionMonthly();
						//dcpsContributionMonthlyId = IFMSCommonServiceImpl.getNextSeqNum("mst_dcps_contribution_monthly",inputMap);

						dcpsContributionMonthlyId = ++lLongLastCountInMonthly;
						dcpsContributionMonthlyId = IFMSCommonServiceImpl.getFormattedPrimaryKey(dcpsContributionMonthlyId, inputMap);

						MstDcpsContributionMonthlyForInsertion.setDcpsContributionMonthlyId(dcpsContributionMonthlyId);

						MstDcpsContributionMonthlyForInsertion.setDcpsId(dcpsId);
						MstDcpsContributionMonthlyForInsertion.setYearId(yearId);
						MstDcpsContributionMonthlyForInsertion.setCurTreasuryCD(lLongTreasuryCodeVoucherWiseTable);
						MstDcpsContributionMonthlyForInsertion.setCurDdoCD(lStrDdoCodeVoucherWiseTable);

						MstDcpsContributionMonthlyForInsertion.setContribEmp(Round(lDoubleVoucherContriForMonth,2));
						MstDcpsContributionMonthlyForInsertion.setContribEmplr(Round(lDoubleVoucherContriForMonthEmplr,2));

						MstDcpsContributionMonthlyForInsertion.setIntContribEmp(Round(interestForCurrentMonth,2));
						MstDcpsContributionMonthlyForInsertion.setIntContribEmplr(Round(interestForCurrentMonthEmplr,2));

						MstDcpsContributionMonthlyForInsertion.setContribTier2(0d);
						MstDcpsContributionMonthlyForInsertion.setIntContribTier2(0d);

						MstDcpsContributionMonthlyForInsertion.setVorcNo(lStrVoucherNo);
						MstDcpsContributionMonthlyForInsertion.setVorcDate(lDateVoucherDate);

						if(lObjVoucherDtls[8] != null)
						{
							lStrTypeOfPayment = lObjVoucherDtls[8].toString();
						}

						MstDcpsContributionMonthlyForInsertion.setTypeOfPayment(lStrTypeOfPayment);

						lObjCalendar = Calendar.getInstance();
						lObjCalendar.setTime(lDateVoucherDate);

						//lIntVMonth = lObjCalendar.MONTH + 1;
						lIntVMonth = lObjCalendar.get(Calendar.MONTH) + 1;
						vMonth = Long.parseLong(lIntVMonth.toString());

						//lIntVYear = lObjCalendar.YEAR ;
						lIntVYear = lObjCalendar.get(Calendar.YEAR) ;
						vYear = Long.parseLong(lIntVYear.toString());

						MstDcpsContributionMonthlyForInsertion.setvYear(vYear);
						MstDcpsContributionMonthlyForInsertion.setvMonth(vMonth);

						lLongPayMonth = Long.valueOf(lObjVoucherDtls[2].toString().trim());
						lLongPayYear = Long.valueOf(lObjVoucherDtls[7].toString().trim());

						payMonth = lLongPayMonth ;
						if(payMonth == 1 || payMonth == 2 || payMonth == 3)
						{
							payYear = lLongPayYear + 1;
						}
						else
						{
							payYear = lLongPayYear ;
						}

						MstDcpsContributionMonthlyForInsertion.setPayMonth(payMonth);
						MstDcpsContributionMonthlyForInsertion.setPayYear(payYear);

						MstDcpsContributionMonthlyForInsertion.setCreatedDate(gDtCurDate);
						MstDcpsContributionMonthlyForInsertion.setCreatedPostId(gLngPostId);
						MstDcpsContributionMonthlyForInsertion.setCreatedUserId(gLngUserId);

						lLstMstDcpsContributionMonthly.add(MstDcpsContributionMonthlyForInsertion);
						//lObjInterestCalculationMonthlyDAO.create(MstDcpsContributionMonthlyForInsertion);
					}

					//interestForCurrentYearEmployee = interestForCurrentYear/2 ;
					//interestForCurrentYearEmployer = interestForCurrentYear/2 ;
					//contributionForCurrentYearEmployee = totalOfAllMonthlyContributionsForYear/2 ;
					//contributionForCurrentYearEmployer = totalOfAllMonthlyContributionsForYear/2 ;

					contributionForCurrentYearEmployee = totalOfAllMonthlyContributionsForYear;
					contributionForCurrentYearEmployer = totalOfAllMonthlyContributionsForYearEmplr;

					//closeEmployee = openEmployee + interestForCurrentYearEmployee + contributionForCurrentYearEmployee ;
					//closeEmployer = openEmployer + interestForCurrentYearEmployer + contributionForCurrentYearEmployer ;
					//closeNet = closeEmployee + closeEmployer + openIntForYear;
					// Above three formula was wrong. Below is correct as per Sevaarth Team Abhijeet


					if (lObjMstDcpsContributionYearlyForCurrentYear == null) {

						MstDcpsContributionYearlyForInsertion = new MstDcpsContributionYearly();
						dcpsContributionYearlyId = IFMSCommonServiceImpl.getNextSeqNum("mst_dcps_contribution_yearly",inputMap);
						MstDcpsContributionYearlyForInsertion.setDcpsContributionYearlyId(dcpsContributionYearlyId);
					}
					else
					{
						//dcpsContributionYearlyId = lObjMstDcpsContributionYearlyForCurrentYear.getDcpsContributionYearlyId();
						//MstDcpsContributionYearlyForInsertion = (MstDcpsContributionYearly) lObjInterestCalculationDAO.read(dcpsContributionYearlyId);

						MstDcpsContributionYearlyForInsertion = lObjMstDcpsContributionYearlyForCurrentYear;
					}


					// Above query commented as every time a new value inserted

					MstDcpsContributionYearlyForInsertion = new MstDcpsContributionYearly();
					//dcpsContributionYearlyId = IFMSCommonServiceImpl.getNextSeqNum("mst_dcps_contribution_yearly",inputMap);
					dcpsContributionYearlyId = ++lLongLastCountInYearly;
					dcpsContributionYearlyId = IFMSCommonServiceImpl.getFormattedPrimaryKey(dcpsContributionYearlyId, inputMap);

					MstDcpsContributionYearlyForInsertion.setDcpsContributionYearlyId(dcpsContributionYearlyId);

					MstDcpsContributionYearlyForInsertion.setDcpsId(dcpsId);
					MstDcpsContributionYearlyForInsertion.setOpenNet(Round(openNet,2));
					MstDcpsContributionYearlyForInsertion.setOpenEmp(Round(openEmployee,2));
					MstDcpsContributionYearlyForInsertion.setOpenEmplr(Round(openEmployer,2));
					MstDcpsContributionYearlyForInsertion.setIntContribEmp(Round(interestForCurrentYearEmployee,2));
					MstDcpsContributionYearlyForInsertion.setIntContribEmplr(Round(interestForCurrentYearEmployer,2));
					MstDcpsContributionYearlyForInsertion.setContribEmp(Round(contributionForCurrentYearEmployee,2));
					MstDcpsContributionYearlyForInsertion.setContribEmplr(Round(contributionForCurrentYearEmployer,2));

					MstDcpsContributionYearlyForInsertion.setOpenInt(Round(openInt,2));

					MstDcpsContributionYearlyForInsertion.setYearId(yearId);
					MstDcpsContributionYearlyForInsertion.setCurTreasuryCD(treasuryCode);
					MstDcpsContributionYearlyForInsertion.setCurDdoCD(ddoCode);

					// Code for Six PC Starts

					//listArrearsDtlsForGivenEmp = lObjInterestCalculationDAO.getArrearsDtlsForGivenEmployee(dcpsEmpId,yearId);

				    lArrearAmountTier2 = 0d; 
				    lDoubleInterestTier2 = 0d;

					lListSixPCDtlsForGivenEmp = (List) lMapEmpDetailsForEmpSixPC.get(dcpsEmpId);
					if(lListSixPCDtlsForGivenEmp != null && lListSixPCDtlsForGivenEmp.size()!= 0)
					{
						lObjArrearsDtls = (Object[]) lListSixPCDtlsForGivenEmp.get(0);
						lArrearAmountTier2 = Double.parseDouble(lObjArrearsDtls[0].toString());
						lIntDaysLeftForSixPCIntrstCalc = daysBetween(lDateArrearsLimitDate,lDateFinYearEndDate) + 1;
						lDoubleInterestTier2 = lIntDaysLeftForSixPCIntrstCalc * InterestRateEffectiveForOpenInt * lArrearAmountTier2 * 0.01 / NumberOfDaysInGivenYear;

						// code of Six PC for entry in Mst_dcps_contribution_monthly table starts

						MstDcpsContributionMonthlyForInsertion = new MstDcpsContributionMonthly();

						//dcpsContributionMonthlyId = IFMSCommonServiceImpl.getNextSeqNum("mst_dcps_contribution_monthly",inputMap);

						dcpsContributionMonthlyId = ++lLongLastCountInMonthly;
						dcpsContributionMonthlyId = IFMSCommonServiceImpl.getFormattedPrimaryKey(dcpsContributionMonthlyId, inputMap);

						MstDcpsContributionMonthlyForInsertion.setDcpsContributionMonthlyId(dcpsContributionMonthlyId);
						MstDcpsContributionMonthlyForInsertion.setDcpsId(dcpsId);
						MstDcpsContributionMonthlyForInsertion.setYearId(yearId);
						MstDcpsContributionMonthlyForInsertion.setCurTreasuryCD(lLongTreasuryCodeVoucherWiseTable);
						MstDcpsContributionMonthlyForInsertion.setCurDdoCD(lStrDdoCodeVoucherWiseTable);
						MstDcpsContributionMonthlyForInsertion.setContribTier2(Round(lArrearAmountTier2,2));
						MstDcpsContributionMonthlyForInsertion.setIntContribTier2(Round(lDoubleInterestTier2,2));

						MstDcpsContributionMonthlyForInsertion.setContribEmp(0d);
						MstDcpsContributionMonthlyForInsertion.setContribEmplr(0d);
						MstDcpsContributionMonthlyForInsertion.setIntContribEmp(0d);
						MstDcpsContributionMonthlyForInsertion.setIntContribEmplr(0d);

						// Pay month hard-coded to June as said by Sevaarth Team Abhijeet
						String lStrPaycommission = "";
						if(lObjArrearsDtls[0] != null)
						{
							lStrPaycommission = lObjArrearsDtls[5].toString().trim();
						}

						// Pay months and pay year are hard-coded as per Sevaarth Team Abhijeet's communication
						if(lStrPaycommission.equals("700339"))
						{
							MstDcpsContributionMonthlyForInsertion.setPayMonth(5l);
							MstDcpsContributionMonthlyForInsertion.setPayYear(2010l);
						}
						else
						{
							MstDcpsContributionMonthlyForInsertion.setPayMonth(6l);
							MstDcpsContributionMonthlyForInsertion.setPayYear(2009l);
						}
						//payYearTier2 = Long.valueOf(lObjArrearsDtls[4].toString().trim());
						//MstDcpsContributionMonthlyForInsertion.setPayYear(payYearTier2);

						if(lObjArrearsDtls[2] != null)
						{
							lStrVoucherNoTier2 = lObjArrearsDtls[2].toString().trim();
						}
						// Voucher no if not there then 0 entered as for some yearly installments in the beginning it was not captured.
						if(!"".equals(lStrVoucherNoTier2))
						{
							MstDcpsContributionMonthlyForInsertion.setVorcNo(lStrVoucherNoTier2);
						}
						else
						{
							MstDcpsContributionMonthlyForInsertion.setVorcNo("NOT AVAILABLE");
						}

						if(lObjArrearsDtls[3] != null)
						{
							lStrVoucherDateTier2 = lObjArrearsDtls[3].toString().trim();
							lDateVoucherDateTier2 = sdf.parse(lStrVoucherDateTier2);

							lObjCalendarTier2 = Calendar.getInstance();
							lObjCalendarTier2.setTime(lDateVoucherDateTier2);

							lIntVMonthTier2 = lObjCalendarTier2.get(Calendar.MONTH) + 1;
							vMonthTier2 = Long.parseLong(lIntVMonthTier2.toString());

							lIntVYearTier2 = lObjCalendarTier2.get(Calendar.YEAR) ;
							vYearTier2 = Long.parseLong(lIntVYearTier2.toString());

						}
						if("".equals(lStrVoucherDateTier2))
						{
							lStrVoucherDateTier2 = "01/01/9999";
							lDateVoucherDateTier2 = sdf1.parse(lStrVoucherDateTier2);
							vMonthTier2 = 0l;
							vYearTier2 = 0l;
						}

						MstDcpsContributionMonthlyForInsertion.setVorcDate(lDateVoucherDateTier2);

						MstDcpsContributionMonthlyForInsertion.setvYear(vYearTier2);
						MstDcpsContributionMonthlyForInsertion.setvMonth(vMonthTier2);

						MstDcpsContributionMonthlyForInsertion.setCreatedDate(gDtCurDate);
						MstDcpsContributionMonthlyForInsertion.setCreatedPostId(gLngPostId);
						MstDcpsContributionMonthlyForInsertion.setCreatedUserId(gLngUserId);

						//lObjInterestCalculationMonthlyDAO.create(MstDcpsContributionMonthlyForInsertion);
						lLstMstDcpsContributionMonthly.add(MstDcpsContributionMonthlyForInsertion);

						//code of Six PC for entry in Mst_dcps_contribution_monthly table ends

					}

					lDoubleCloseBalanceTier2 = lDoubleOpenBalanceTier2 + lArrearAmountTier2 + lDoubleInterestTier2 ;

					MstDcpsContributionYearlyForInsertion.setOpenTier2(Round(lDoubleOpenBalanceTier2,2));
					MstDcpsContributionYearlyForInsertion.setContribTier2(Round(lArrearAmountTier2,2));
					MstDcpsContributionYearlyForInsertion.setIntContribTier2(Round(lDoubleInterestTier2,2));
					MstDcpsContributionYearlyForInsertion.setCloseTier2(Round(lDoubleCloseBalanceTier2,2));

					closeEmployee = openEmployee + contributionForCurrentYearEmployee + interestForCurrentYearEmployee + (openInt/2) + lArrearAmountTier2 + lDoubleInterestTier2 ;
					closeEmployer = openEmployer + contributionForCurrentYearEmployer + interestForCurrentYearEmployer + (openInt/2) ;
					closeNet = closeEmployee + closeEmployer ;

					MstDcpsContributionYearlyForInsertion.setCloseEmp(Round(closeEmployee,2));
					MstDcpsContributionYearlyForInsertion.setCloseEmplr(Round(closeEmployer,2));
					MstDcpsContributionYearlyForInsertion.setCloseNet(Round(closeNet,2));


					if (lObjMstDcpsContributionYearlyForCurrentYear == null) {

						MstDcpsContributionYearlyForInsertion.setCreatedDate(gDtCurDate);
						MstDcpsContributionYearlyForInsertion.setCreatedPostId(gLngPostId);
						MstDcpsContributionYearlyForInsertion.setCreatedUserId(gLngUserId);
						lObjInterestCalculationDAO.create(MstDcpsContributionYearlyForInsertion);
					}
					else
					{
						MstDcpsContributionYearlyForInsertion.setUpdatedDate(gDtCurDate);
						MstDcpsContributionYearlyForInsertion.setUpdatedPostId(gLngPostId);
						MstDcpsContributionYearlyForInsertion.setUpdatedUserId(gLngUserId);
						lObjInterestCalculationDAO.update(MstDcpsContributionYearlyForInsertion);
					}

					// Above lines commented as every time a new value inserted

					MstDcpsContributionYearlyForInsertion.setCreatedDate(gDtCurDate);
					MstDcpsContributionYearlyForInsertion.setCreatedPostId(gLngPostId);
					MstDcpsContributionYearlyForInsertion.setCreatedUserId(gLngUserId);
					lLstMstDcpsContributionYearly.add(MstDcpsContributionYearlyForInsertion);
					//lObjInterestCalculationDAO.create(MstDcpsContributionYearlyForInsertion);

					// Below code clears lists of contribution and arrears details of given employee

					if(lListEmpDetailsForGivenEmp != null)
					{
						lListEmpDetailsForGivenEmp.clear();
					}

					if(lListSixPCDtlsForGivenEmp != null)
					{
						lListSixPCDtlsForGivenEmp.clear();
					}

					if(lMapEmpContriDtlsForGivenDcpsContriId != null)
					{
						lMapEmpContriDtlsForGivenDcpsContriId.clear();
					}

					if(lListContriDtlsForDcpsContriId != null)
					{
						lListContriDtlsForDcpsContriId.clear();
					}

			}

			// Code to insert missing credits in mst_dcps_contribution_monthly table

			Iterator itDcpsContriDtlsMissingCreditsInserts = lMapContriDtlsForDcpsContriIdMissingCredits.keySet().iterator();
			Object[] tupleForDcpsContriIdForMissingCreditInsert = null;
			Date lDateVoucherDateForMissingCredit = null;

			while(itDcpsContriDtlsMissingCreditsInserts.hasNext())
			{
				lLongDcpsContriIdForMissingCredit = Long.valueOf(itDcpsContriDtlsMissingCreditsInserts.next().toString());
				lListDcpsContriDtlsForDcpsContriIdForMissingCredit = (List) lMapContriDtlsForDcpsContriIdMissingCredits.get(lLongDcpsContriIdForMissingCredit);

				tupleForDcpsContriIdForMissingCreditInsert = (Object[]) lListDcpsContriDtlsForDcpsContriIdForMissingCredit.get(0);

				MstDcpsContributionMonthlyForInsertion = new MstDcpsContributionMonthly();

				dcpsContributionMonthlyId = ++lLongLastCountInMonthly;
				dcpsContributionMonthlyId = IFMSCommonServiceImpl.getFormattedPrimaryKey(dcpsContributionMonthlyId, inputMap);

				MstDcpsContributionMonthlyForInsertion.setDcpsContributionMonthlyId(dcpsContributionMonthlyId);

				lStrDcpsIdForMissingCredit = tupleForDcpsContriIdForMissingCreditInsert[16].toString().trim();

				lLongTreasuryCodeVoucherWiseTable = Long.valueOf(tupleForDcpsContriIdForMissingCreditInsert[18].toString().trim());
				lStrDdoCodeVoucherWiseTable = null;
				lStrDdoCodeVoucherWiseTable = tupleForDcpsContriIdForMissingCreditInsert[17].toString().trim();

				MstDcpsContributionMonthlyForInsertion.setDcpsId(lStrDcpsIdForMissingCredit);
				MstDcpsContributionMonthlyForInsertion.setYearId(yearId);
				MstDcpsContributionMonthlyForInsertion.setCurTreasuryCD(lLongTreasuryCodeVoucherWiseTable);

				MstDcpsContributionMonthlyForInsertion.setCurDdoCD(lStrDdoCodeVoucherWiseTable);

				lDoubleVoucherContriForMonth = 0d;
				lDoubleVoucherContriForMonthEmplr = 0d;

				lDoubleVoucherContriForMonth = Round(Double.valueOf(tupleForDcpsContriIdForMissingCreditInsert[6].toString().trim()),2);
				lDoubleVoucherContriForMonthEmplr = Round(Double.valueOf(tupleForDcpsContriIdForMissingCreditInsert[6].toString().trim()),2);

				MstDcpsContributionMonthlyForInsertion.setContribEmp(Round(lDoubleVoucherContriForMonth,2));
				MstDcpsContributionMonthlyForInsertion.setContribEmplr(Round(lDoubleVoucherContriForMonthEmplr,2));

				interestForCurrentMonth = 0d;
				interestForCurrentMonthEmplr = 0d;

				interestForCurrentMonth = (Double.valueOf(lMapDcpsInterestAmountForDcpsContriIdForMissingCredits.get(lLongDcpsContriIdForMissingCredit).toString())/2) - lDoubleVoucherContriForMonth;
				interestForCurrentMonthEmplr = (Double.valueOf(lMapDcpsInterestAmountForDcpsContriIdForMissingCredits.get(lLongDcpsContriIdForMissingCredit).toString())/2) - lDoubleVoucherContriForMonthEmplr;

				MstDcpsContributionMonthlyForInsertion.setIntContribEmp(Round(interestForCurrentMonth,2));
				MstDcpsContributionMonthlyForInsertion.setIntContribEmplr(Round(interestForCurrentMonthEmplr,2));

				lStrTypeOfPayment = null;
				lStrTypeOfPayment = tupleForDcpsContriIdForMissingCreditInsert[13].toString().trim();

				MstDcpsContributionMonthlyForInsertion.setTypeOfPayment(lStrTypeOfPayment);

				lLongPayMonth = Long.valueOf(tupleForDcpsContriIdForMissingCreditInsert[7].toString().trim());
				lLongPayYear = Long.valueOf(tupleForDcpsContriIdForMissingCreditInsert[12].toString().trim());

				payMonth = lLongPayMonth ;
				if(payMonth == 1 || payMonth == 2 || payMonth == 3)
				{
					payYear = lLongPayYear + 1;
				}
				else
				{
					payYear = lLongPayYear ;
				}

				MstDcpsContributionMonthlyForInsertion.setPayMonth(payMonth);
				MstDcpsContributionMonthlyForInsertion.setPayYear(payYear);

				lObjCalendar = Calendar.getInstance();
				lObjCalendar.setTime(sdf.parse(tupleForDcpsContriIdForMissingCreditInsert[5].toString().trim()));

				//lIntVMonth = lObjCalendar.MONTH + 1;
				lIntVMonth = lObjCalendar.get(Calendar.MONTH) + 1;
				vMonth = Long.parseLong(lIntVMonth.toString());

				//lIntVYear = lObjCalendar.YEAR ;
				lIntVYear = lObjCalendar.get(Calendar.YEAR) ;
				vYear = Long.parseLong(lIntVYear.toString());

				MstDcpsContributionMonthlyForInsertion.setvYear(vYear);
				MstDcpsContributionMonthlyForInsertion.setvMonth(vMonth);

				// Voucher No and Voucher Date are to be added.
				lStrVoucherNo = tupleForDcpsContriIdForMissingCreditInsert[11].toString().trim();
				MstDcpsContributionMonthlyForInsertion.setVorcNo(lStrVoucherNo);

				lDateVoucherDateForMissingCredit = sdf.parse(tupleForDcpsContriIdForMissingCreditInsert[5].toString().trim());
				MstDcpsContributionMonthlyForInsertion.setVorcDate(lDateVoucherDateForMissingCredit);

				MstDcpsContributionMonthlyForInsertion.setCreatedDate(gDtCurDate);
				MstDcpsContributionMonthlyForInsertion.setCreatedPostId(gLngPostId);
				MstDcpsContributionMonthlyForInsertion.setCreatedUserId(gLngUserId);

				MstDcpsContributionMonthlyForInsertion.setContribTier2(0d);
				MstDcpsContributionMonthlyForInsertion.setIntContribTier2(0d);

				if(tupleForDcpsContriIdForMissingCreditInsert[20] != null)
				{
					if(tupleForDcpsContriIdForMissingCreditInsert[20].toString().equals("Y"))
					{
						MstDcpsContributionMonthlyForInsertion.setIsMissingCredit('Y');
					}
				}

				if(tupleForDcpsContriIdForMissingCreditInsert[21] != null)
				{
					if(tupleForDcpsContriIdForMissingCreditInsert[21].toString().equals("Y"))
					{
						MstDcpsContributionMonthlyForInsertion.setIsChallan('Y');
					}
				}

				lLstMstDcpsContributionMonthly.add(MstDcpsContributionMonthlyForInsertion);
			}


			//Code Added for SixPC Interest Calculation Previously added
	 *  MstDcpsSixPCInterestYearly lObjMstDcpsSixPCInterestYearlyForCurrentYear = null;
				MstDcpsSixPCInterestYearly lObjMstDcpsSixPCInterestYearlyforInsertion = null;
				Long dcpsSixPCInterestYearlyId = null;
	 * 
	 * 
			if(lStrInterestFor.equals("Treasury") || lStrInterestFor.equals("DDO"))
			{
				listAllEmpsForIntrstCalcSixPC = lObjInterestCalculationDAO.getAllDCPSEmployeesForIntrstCalcSixPC(treasuryCode,ddoCode);
			}
			else if(lStrInterestFor.equals("Emp"))
			{
				String lStrEmpIds = StringUtility.getParameter("empIds", request);
				String[] lStrArrdcpsEmpIds = lStrEmpIds.split("~");
				Long[] lLongArrdcpsEmpIds = new Long[lStrArrdcpsEmpIds.length];
				for (Integer lInt = 0; lInt < lStrArrdcpsEmpIds.length; lInt++) {
					lLongArrdcpsEmpIds[lInt] = Long.valueOf(lStrArrdcpsEmpIds[lInt]);
					listAllEmpsForIntrstCalcSixPC.add(lLongArrdcpsEmpIds[lInt]);
				}
			}
			Iterator ItrEmpsSixPC = listAllEmpsForIntrstCalcSixPC.iterator();
			MstDcpsSixPCInterestYearly lObjMstDcpsSixPCInterestYearly = null;
			Double lDoubleOpenInterest = 0d ;

			while (ItrEmpsSixPC.hasNext()) {

				dcpsEmpId = Long.valueOf(ItrEmpsSixPC.next().toString());
				dcpsId = lObjInterestCalculationDAO
						.getDcpsIdForEmpId(dcpsEmpId);
				lObjMstEmp = (MstEmp) lObjNewRegDdoDAO.read(dcpsEmpId) ;
				lStrDdoCodeForEmp = lObjMstEmp.getDdoCode();

				lObjMstDcpsSixPCInterestYearly = lObjInterestCalculationDAO
						.getSixPCYearlyInterestVOForYear(dcpsEmpId,
								previousFinYearId);

				if (lObjMstDcpsSixPCInterestYearly != null) {

					lDoubleOpenBalanceTier2 = lObjMstDcpsSixPCInterestYearly.getCloseBalance();

				} else {

					lDoubleOpenBalanceTier2 = 0d;
				}

				lObjMstDcpsSixPCInterestYearlyForCurrentYear = lObjInterestCalculationDAO.getSixPCYearlyInterestVOForYear(dcpsEmpId,yearId);

				if(lObjMstDcpsSixPCInterestYearlyForCurrentYear == null)
				{
						listArrearsDtlsForGivenEmp = lObjInterestCalculationDAO.getArrearsDtlsForGivenEmployee(dcpsEmpId,yearId);

						if(listArrearsDtlsForGivenEmp != null && listArrearsDtlsForGivenEmp.size()!=0)
						{
							lObjArrearsDtls = (Object[]) listArrearsDtlsForGivenEmp.get(0);
							lArrearAmountTier2 = Double.parseDouble(lObjArrearsDtls[0].toString());
							lDoubleOpenInterest = InterestRate * lDoubleOpenBalanceTier2  / 100;

							lIntDaysLeftForSixPCIntrstCalc = daysBetween(lDateArrearsLimitDate,lDateFinYearEndDate);

							lDoubleInterestTier2 = lIntDaysLeftForSixPCIntrstCalc
	 * InterestRate * lArrearAmountTier2
	 * 0.01 / NumberOfDaysInGivenYear;

							// lDoubleOpenInterest wont be there.

							lDoubleCloseBalanceTier2 = lDoubleOpenBalanceTier2 + lArrearAmountTier2 + lDoubleInterestTier2 + lDoubleOpenInterest ;

							dcpsSixPCInterestYearlyId = IFMSCommonServiceImpl.getNextSeqNum("mst_dcps_sixpc_interest_yearly",inputMap);
							lObjMstDcpsSixPCInterestYearlyforInsertion = new  MstDcpsSixPCInterestYearly();
							lObjMstDcpsSixPCInterestYearlyforInsertion.setDcpsSixPCInterestYearlyId(dcpsSixPCInterestYearlyId);
							lObjMstDcpsSixPCInterestYearlyforInsertion.setDcpsId(dcpsId);
							lObjMstDcpsSixPCInterestYearlyforInsertion.setYearId(yearId);
							lObjMstDcpsSixPCInterestYearlyforInsertion.setCurTreasuryCD(treasuryCode);
							lObjMstDcpsSixPCInterestYearlyforInsertion.setCurDdoCD(lStrDdoCodeForEmp);
							lObjMstDcpsSixPCInterestYearlyforInsertion.setOpenBalance(Round(lDoubleOpenBalanceTier2,2));
							lObjMstDcpsSixPCInterestYearlyforInsertion.setArrearAmount(Round(lArrearAmountTier2,2));
							lObjMstDcpsSixPCInterestYearlyforInsertion.setInterest(Round(lDoubleInterestTier2,2));
							lObjMstDcpsSixPCInterestYearlyforInsertion.setOpenInterest(Round(lDoubleOpenInterest,2));
							lObjMstDcpsSixPCInterestYearlyforInsertion.setCloseBalance(Round(lDoubleCloseBalanceTier2,2));

							lObjMstDcpsSixPCInterestYearlyforInsertion.setCreatedDate(gDtCurDate);
							lObjMstDcpsSixPCInterestYearlyforInsertion.setCreatedPostId(gLngPostId);
							lObjMstDcpsSixPCInterestYearlyforInsertion.setCreatedUserId(gLngUserId);

							lObjInterestCalculationDAO.create(lObjMstDcpsSixPCInterestYearlyforInsertion);
						}
				}

			}

			// Code for SixPC Interest Calculation Ends Previously added.




			// Below code deletes all records of the employee from both yearly and monthly interest tables

			lObjInterestCalculationDAO.deleteMonthlyIntrstsForGivenEmpList(listAllEmpsDCPSIdsForIntrstCalc, yearId);
			lObjInterestCalculationDAO.deleteYearlyIntrstsForGivenEmpList(listAllEmpsDCPSIdsForIntrstCalc, yearId);


			// Below code deletes for batches of Ids

			Integer lIntCountWithinMainListDCPSIdsForDelete = (int) Math.ceil(listAllEmpsDCPSIdsForIntrstCalc.size() / lDoubleNoOfSamples);
			Integer lIntIncrementalCountDCPSIdsForDelete = 0; 
			Integer lIntNextIncrementalCountDCPSIdsForDelete = 0;

			List lListDcpsIdsSampled = null;

			for(Integer lIntOutCount = 1; lIntOutCount <=  lIntCountWithinMainListDCPSIdsForDelete; lIntOutCount++ )
			{
				lListDcpsIdsSampled = new ArrayList();

				if(lIntOutCount == lIntCountWithinMainListDCPSIdsForDelete)
				{
					lIntNextIncrementalCountDCPSIdsForDelete =  lIntIncrementalCountDCPSIdsForDelete + (listAllEmpsDCPSIdsForIntrstCalc.size() % lIntNoOfSamples);
				}
				else
				{
					lIntNextIncrementalCountDCPSIdsForDelete =  lIntIncrementalCountDCPSIdsForDelete + lIntNoOfSamples;
				}

				for(Integer lIntCount = lIntIncrementalCountDCPSIdsForDelete ; lIntCount < lIntNextIncrementalCountDCPSIdsForDelete ; lIntCount++ )
				{
					lListDcpsIdsSampled.add(listAllEmpsDCPSIdsForIntrstCalc.get(lIntCount));
					lIntIncrementalCountDCPSIdsForDelete = lIntCount + 1;
				}

				lObjInterestCalculationDAO.deleteMonthlyIntrstsForGivenEmpList(lListDcpsIdsSampled, yearId);
				lObjInterestCalculationDAO.deleteYearlyIntrstsForGivenEmpList(lListDcpsIdsSampled, yearId);
			}

			// Sampling code ends
			String lStrDcpsId = null;
			Double lDoubleCloseNetForRegular = 0d;
			Double lDoubleTotalClosingNetForRegularAndMissingCredits = 0d;

			Double lDoubleClosingEmpWithMisssingAdded = 0d;
			Double lDoubleClosingEmplrWithMisssingAdded = 0d;

			// Bulk insert in mst_dcps_contribution_yearly table
			for (MstDcpsContributionYearly lObjMstDcpsContributionYearlyTemp : lLstMstDcpsContributionYearly) {
				// Code added to also consider missing credit's interest
				lStrDcpsId = lObjMstDcpsContributionYearlyTemp.getDcpsId().trim();

				if(lMapTotalContriAndInterestForGivenDcpsIdForMissingCredit.containsKey(lStrDcpsId))
				{
					lDoubleTotalClosingNetForRegularAndMissingCredits = 0d;
					lDoubleTotalContriAndInterestForDcpsIdForMissingCredit = 0d;
					lDoubleCloseNetForRegular = lObjMstDcpsContributionYearlyTemp.getCloseNet();

					lDoubleTotalContriForDcpsIdForMissingCredit = Double.valueOf(lMapTotalContriForGivenDcpsIdForMissingCredit.get(lStrDcpsId).toString());
					lDoubleTotalContriAndInterestForDcpsIdForMissingCredit = Double.valueOf(lMapTotalContriAndInterestForGivenDcpsIdForMissingCredit.get(lStrDcpsId).toString());
					lDoubleTotalClosingNetForRegularAndMissingCredits = lDoubleCloseNetForRegular + lDoubleTotalContriAndInterestForDcpsIdForMissingCredit;

					lDoubleClosingEmpWithMisssingAdded = lObjMstDcpsContributionYearlyTemp.getCloseEmp() + lDoubleTotalContriAndInterestForDcpsIdForMissingCredit / 2;
					lDoubleClosingEmplrWithMisssingAdded = lObjMstDcpsContributionYearlyTemp.getCloseEmplr() + lDoubleTotalContriAndInterestForDcpsIdForMissingCredit / 2;

					lObjMstDcpsContributionYearlyTemp.setContribTotalMissingCredit(Round(lDoubleTotalContriForDcpsIdForMissingCredit,2));
					lObjMstDcpsContributionYearlyTemp.setCloseMissingCredit(Round(lDoubleTotalContriAndInterestForDcpsIdForMissingCredit,2));
					lObjMstDcpsContributionYearlyTemp.setCloseEmp(Round(lDoubleClosingEmpWithMisssingAdded,2));
					lObjMstDcpsContributionYearlyTemp.setCloseEmplr(Round(lDoubleClosingEmplrWithMisssingAdded,2));
					lObjMstDcpsContributionYearlyTemp.setCloseNet(Round(lDoubleTotalClosingNetForRegularAndMissingCredits,2));
				}

				hitStg.save(lObjMstDcpsContributionYearlyTemp);
			}

			// Bulk insert in mst_dcps_contribution_monthly table

			for (MstDcpsContributionMonthly lObjMstDcpsContributionMonthlyTemp : lLstMstDcpsContributionMonthly) {
				hitStg.save(lObjMstDcpsContributionMonthlyTemp);
			}

			// Below code updates status of a contribution to 'H' showing interest on it is calculated.
			//lObjInterestCalculationDAO.updateTrnDcpsContriIntCalculated(lListDcpsContriIdsPk);

			// Above line is temporarily commented.
			// Test code to divide the main list into sublists with 5000 Ids and executing HQL update query individually for all


			Integer lIntCountWithinMainListContriForUpdate = (int) Math.ceil(lListDcpsContriIdsPk.size() / lDoubleNoOfSamples);
			Integer lIntIncrementalCountContriForUpdate = 0; 
			Integer lIntNextIncrementalCountContriForUpdate = 0;

			List lListDcpsContriIdsPkSampled = null;

			for(Integer lIntOutCount = 1; lIntOutCount <=  lIntCountWithinMainListContriForUpdate; lIntOutCount++ )
			{
				lListDcpsContriIdsPkSampled = new ArrayList();

				if(lIntOutCount == lIntCountWithinMainListContriForUpdate)
				{
					lIntNextIncrementalCountContriForUpdate =  lIntIncrementalCountContriForUpdate + (lListDcpsContriIdsPk.size() % lIntNoOfSamples);
				}
				else
				{
					lIntNextIncrementalCountContriForUpdate =  lIntIncrementalCountContriForUpdate + lIntNoOfSamples;
				}

				for(Integer lIntCount = lIntIncrementalCountContriForUpdate ; lIntCount < lIntNextIncrementalCountContriForUpdate ; lIntCount++ )
				{
					lListDcpsContriIdsPkSampled.add(lListDcpsContriIdsPk.get(lIntCount));
					lIntIncrementalCountContriForUpdate = lIntCount + 1;
				}

				lObjInterestCalculationDAO.updateTrnDcpsContriIntCalculated(lListDcpsContriIdsPkSampled);
			}

			// Clears all array lists to release and free memory
			if(listAllEmpsForIntrstCalc != null)
			{
				listAllEmpsForIntrstCalc.clear();
			}
			if(listAllEmpsForIntrstCalcLongValue != null)
			{
				listAllEmpsForIntrstCalcLongValue.clear();
			}
			if(listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc != null)
			{
				listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc.clear();
			}
			if(listAllEmpsDCPSIdsForIntrstCalc != null)
			{
				listAllEmpsDCPSIdsForIntrstCalc.clear();
			}
			if(lLstMstDcpsContributionYearly != null)
			{
				lLstMstDcpsContributionYearly.clear();
			}
			if(lLstMstDcpsContributionMonthly != null)
			{
				lLstMstDcpsContributionMonthly.clear();
			}
			if(lListAllEmpContriAndIntrstDtls != null)
			{
				lListAllEmpContriAndIntrstDtls.clear();
			}
			if(lListAllEmpSixPCDtls != null)
			{
				lListAllEmpSixPCDtls.clear();	
			}
			if(lListDcpsContriIdsPk != null)
			{
				lListDcpsContriIdsPk.clear();
			}
			if(lListDcpsContriIdsPkSampled != null)
			{
				lListDcpsContriIdsPkSampled.clear();
			}
			if(lListTempContriAndIntrstDtls != null)
			{
				lListTempContriAndIntrstDtls.clear();
			}
			if(lListTempSixPCDtls != null)
			{
				lListTempSixPCDtls.clear();
			}
			if(lListDcpsEmpIdsPkSampled != null)
			{
				lListDcpsEmpIdsPkSampled.clear();
			}
			if(lListTempContriAndIntrstDtls != null)
			{
				lListTempContriAndIntrstDtls.clear();
			}
			if(lListTempSixPCDtls != null)
			{
				lListTempSixPCDtls.clear();
			}
			if(lListDcpsIdsSampled != null)
			{
				lListDcpsIdsSampled.clear();
			}
			if(lListDcpsContriDtlsForDcpsContriIdForMissingCredit != null)
			{
				lListDcpsContriDtlsForDcpsContriIdForMissingCredit.clear();
			}
			if(lListDcpsContriIdsListForDcpsIdForMissingCredit != null)
			{
				lListDcpsContriIdsListForDcpsIdForMissingCredit.clear();
			}
			if(listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits != null)
			{
				listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits.clear();
//			}

			// Clears all maps to release and free memory
			if(!(lMapEmpDetailsForEmp.isEmpty()))
			{
				lMapEmpDetailsForEmp.clear();
			}
			if(!(lMapEmpDetailsForEmpSixPC.isEmpty()))
			{
				lMapEmpDetailsForEmpSixPC.clear();
			}
			if(!(lMapContriDtlsForDcpsContriIdMissingCredits.isEmpty()))
			{
				lMapContriDtlsForDcpsContriIdMissingCredits.clear();
			}
			if(!(lMapDcpsContriIdsListForDcpsIdForMissingCredits.isEmpty()))
			{
				lMapDcpsContriIdsListForDcpsIdForMissingCredits.clear();
			}
			if(!(lMapDcpsInterestAmountForDcpsContriIdForMissingCredits.isEmpty()))
			{
				lMapDcpsInterestAmountForDcpsContriIdForMissingCredits.clear();
			}
			if(!(lMapTotalContriAndInterestForGivenDcpsIdForMissingCredit.isEmpty()))
			{
				lMapTotalContriAndInterestForGivenDcpsIdForMissingCredit.clear();
			}

			lBlFlag = true;

			String lSBStatus = getResponseXMLDoc(lBlFlag,lStrInterestFor).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
					lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");

			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.info("Final String is ::"+ val);
			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error in Interest Calculation " + e, e);
			return resObj;
		}
		return resObj;
	}*/
	public ResultObject calculateDCPSYearlyInterestTmp(Map<String, Object> inputMap) 
	{
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		try
		{
			setSessionInfo(inputMap);

			HibernateTemplate hitStg = new HibernateTemplate(serv.getSessionFactory());
			InterestCalculationDAO lObjInterestCalculationDAO = new InterestCalculationDAOImpl(MstDcpsContributionYearly.class, serv.getSessionFactory());
			String s="17101000599SMAM8802I, 17101000599SMBM8901J, 17101020316UDDM8701F, 17101010316TTAM8501M, 17101050316SGMM8301E, 17101050316SGMM8401D, 17101040316SBGM8601D, 17101040316SSTM8601Z, 17101010316SBSM8601I, 17101040316SPBM8201I, 17101000454RVPM8901D, 17101020316RDPM8801P, 17101000599RSSM8303H, 17101050316RAVM8501O, 17101020316RAMM8701C, 17101020316RSJM8401W, 17101040316RSBM8601I, 17101000599RRNM8601E, 17101000599SKBM8801M, 17101000599SVKM8401E, 17101000599SPGF8901A, 17101000599SSBM8202F, 17101000599SMPM7002E, 17101000599SJMM8301L, 17101000599SMPM8801U, 17101000599SVTM8601B, 17101000599SSZM8701L, 17101000599UVLM8201P, 17101000599SWPM8201Q, 17101000454SSPF8901I, 17101030316SSCM8501E, 17101050316SSSM8501Y, 17101010316STHM8601X, 17101030316SRBM8401J, 17101000599MRKF8601F, 17101000599MPMM8701R, 17101000599MSSM8801V, 17101000599MSCM8401V, 17101000599MRPM8701G, 17101000599SRBM8001N, 17101000599SSDM8602V, 17101000599SSSF8804Z, 17101000599SNBM9101J, 17101000599SPWM8602T, 17101000599SLBM8901K, 17101000599SLSM8601O, 17101000599SMPM8601W, 17101000599SPRM8601N, 17101000599SPRM8501O, 17101000599SNSM7001Z, 17101000599SVKM8602X, 17101000599SSSM8301K, 17101000599SSKM8702Z, 17101000599SSDM8802T, 17101000599USKM8501S, 17101000599VHKF8701D, 17101050316SNNM8601R, 17101010316STPF8901F, 17101040316SSJM8601D, 17101020316SSRM8901M, 17101000599SSSF8802J, 17101010316SNKM8101Z, 17101000599MMAM8501G, 17101050316MMRM8701V, 17101050316SSSM8401Z, 17101040316SSGM8401O, 17101000599SNPM8501W, 17101000599RSSF8703M, 17101020316SKLM8801N, 17101000599SLUF8801P, 17101050316SRMF8901W, 17101030316MNGM8701L, 17101030316MYMM9001I, 17101010316SSMM8601J, 17101000599SSGF8601A, 17101010316SSJM8201W, 17101000599SSKF9001N, 17101010316STCM8901J, 17101040316SSZM8601H, 17101010316SVGF8901E, 17101040316SCGM8901Z, 17101040316SVKM8702R, 17101000599MPTM8501Y, 17101000599NPSM8501U, 17101010316NLGM8601R, 17101020316NLDM8201Z, 17101010316NGGM8401Y, 17101010316NGCM8801G, 17101040316NGCM8501U, 17101000599NGNM8901O, 17101040316NDBM8601Z, 17101000599NDGM8801N, 17101020316NCTM8901F, 17101040316NBKM8501B, 17101040316NAPM8701L, 17101000599NAPM8701Q, 17101000454NMBF8801O, 17101000599NANF8701F, 17101000599SKMM7901L, 17101000599SSRM8301N, 17101000599SRLM8001J, 17101000599SNKM8201O, 17101000599SPSM8301N, 17101000599SNSM8801K, 17101000599SMMM8502B, 17101000599SJKM8102O, 17101000599SSGF9201X, 17101000599SSWF8901B, 17101000599SSPM9003F, 17101000599SPTM8801F, 17101000599SSKF9002I, 17101000599SSJF9101P, 17101000599SRPF9101Y, 17101000599STPF8901V, 17101000599SAIM8601D, 17101000457SPGF8801Y, 17101000599SSKF8801M, 17101000455SSPF8701F, 17101010316SDVM8601X, 17101000599SEGF8801M, 17101000455SASF8601P, 17101000599SNMF8401P, 17101000599MSRM8301D, 17101010316SSVM8401K, 17101000599SSKF8601O, 17101000599SASF8703X, 17101040316SSKM8901X, 17101000599SRTF8801M, 17101020316STMM8401F, 17101000599SYRM8501F, 17101030316TNMM8601X, 17101000455SSPF8601G, 17101000599SRMM8001G, 17101050316SNKF8901G, 17101050316SRSF8901E, 17101050316SNZM8301K, 17101050316NAIF8601L, 17101000599NAJM8501K, 17101000599SRMF8801H, 17101010316SPTM8301U, 17101000599SLKF8701U, 17101050316SPMM8801Q, 17101000599STPF8602T, 17101000599SGPF8302J, 17101000457SPSF8601Q, 17101000599SSKM8101K, 17101000599STJM7901L, 17101000599SLKM8701L, 17101000599VGPM8401J, 17101000599SSCM8501E, 17101000599RGDM8501U, 17101000599SNPM8201Z, 17101000599PBLM8501P, 17101000599VKKM8001Y, 17101000599MSMM8501Q, 17101000599RATM8602Y, 17101000599SATM8401Y, 17101000599SRSM8302G, 17101000599SDKM8701T, 17101000599SLTM8501M, 17101000599PBSM8003P, 17101000599SGSM8401V, 17101000599SSGF9002U, 17101000599SSMF9001H, 17101000599SMTF9001S, 17101000599STKF9101L, 17101000599SSKF9005T, 17101000599SSGF8902S, 17101000599SSCF8901J, 17101000599SRKF8801N, 17101000599SRGF8001H, 17101000599SMBF9002P, 17101000599SLSF8601X, 17101000599SYMM8801R, 17101000599SSKM9103T, 17101000599SSSM8505O, 17101000599SSKM8801D, 17101000599SSCM8401F, 17101000599NSTF9001V, 17101000599PVKM8701W, 17101000599PSZM8801F, 17101000599PSSM8701B, 17101000599PSNM8701Q, 17101000599PSBM9001A, 17101000599PNPM8301T, 17101000599RNRM9101U, 17101000599RMLM8701O, 17101000599MMSF9101K, 17101000599RSRM8901O, 17101000599RNBM8801Q, 17101000599NAMF8601J, 17101000599MMPM8501N, 17101000599RJMM9001O, 17101000599PHPM8401Y, 17101000599SCPF8301S, 17101000599RVPM8901R, 17101000599RAKM8901B, 17101000599RJTM8601U, 17101000599RTSM8701M, 17101000599RJKM8401X, 17101000599RBKM8201H, 17101000599RBSM8701E, 17101000599RCKM8801A, 17101000599RCJM8501G, 17101000599RAHM8601N, 17101000599RRRF8701A, 17101000599RRPM9001X, 17101000599RBGF9101W, 17101000599RRKM8901K, 17101000599RACM9101A, 17101000599RBBM8501F, 17101000599UBDF8901J, 17101000454NVTM8601W, 17101040316NSSM8201P, 17101000599NSSM8701P, 17101010316NSPM8701I, 17101030316NPKM8301U, 17101000599NHWM8401R, 17101000457NAPM8001U, 17101010316NLBM8601G, 17101000599SPRM8601N, 17101000599VASM9101C, 17101000599SPRM8301Q, 17101000599VASF9101L, 17101000599VJSM8601V, 17101000599SSSM8603X, 17101000599TBPM8801Y, 17101010316SSJM8201W, 17101000599MRWF9101T, 17101000599MRWM8401O, 17101000599SSMM8001F, 17101000599RTCM8501K, 17101000599VAJM8201J, 17101000599SSDM8601A, 17101000454SANF8701I, 17101000599SASM7901D, 17101000599NMDM8601P, 17101000599NMJM8801V, 17101000599MMGM8901K, 17101000599MMBM8202B, 17101000599MRPF8601Q, 17101000455SNTM8001W, 17101000599SYRM8501F, 17101000599SPVM8602W, 17101000599SMBM8601M, 17101000599SJPM8702T, 17101000599SJJM8901O, 17101000599SSPF8901W, 17101000599SUGM8901M, 17101000599SJPM8101E, 17101000599SSHM8701N, 17101000599SMLF9001Q, 17101000599SVPM8502J, 17101000599SRSM8101N, 17101000599SWLM8401A, 17101000599SSSM8602C, 17101000599SVPM8801L, 17101000599SVJM8901C, 17101000599SUBM8701D, 17101040316URNM8901B, 17101000599VKCM8601Q, 17101000599VBGM8601N, 17101000599RNCM8401R, 17101000599MPSM8501B, 17101000599RTTM8501L, 17101000457PRPF9001R, 17101000454PPTF8901U, 17101000599PPPF8801V, 17101000599PNPF8901W, 17101000599PSMF8501E, 17101000599PVMM8701Q, 17101000599PPWM8201X, 17101020316PVDM8601X, 17101040316PUKM8701S, 17101000599PSVM8201X, 17101000599SBVF8901V, 17101010454NSGF8001W, 17101000599SNSM8301P, 17101000599SPRM8501O, 17101000599TKPF8901X, 17101000599SRPM9101P, 17101000599STPF8902Q, 17101000599URPM8801B, 17101000599VASM8401G, 17101000599SKRM8701R, 17101000599TDPF8701G, 17101000599SKPM8601Y, 17101000599UTLM8501O, 17101000599TRSM8301E, 17101000599SAPM8701H, 17101000599SAKM8201B, 17101000599SSGM8803F, 17101000599SSPM8802J, 17101000599SVPF8701V, 17101000599SVKM8201G, 17101000599TASM9001R, 17101000599TSRF8701L, 17101000599TDRF8701A, 17101000599VDPF8901Q, 17101000599VDPM8301N, 17101000599USKM6901C, 17101000599SPPM8701S, 17101000599SZCM8601W, 17101000599SSDM8801Y, 17101000599STPM8301S, 17101000599SGSM8501U, 17101000599SADM8401U, 17101030316VDGM8001R, 17101050316TSSM8901N, 17101050316VGTF8601U, 17101010316VBDM8901D, 17101000599MTDF8901V, 17101000599MMBM8901Z, 17101000454PRRF8801Z, 17101000599PRMF8801C, 17101000457PPLF8901D, 17101030316PVGF8801Q, 17101000455PSVF8901G, 17101000599PVDM8701R, 17101010316PVPM8201W, 17101050316PSSM8501T, 17101000599PSMM7901Y, 17101020316PSJM8801G, 17101000599SMKF8702O, 17101000599STKM8601E, 17101000599STMM8201C, 17101000599SRLM8701C, 17101000599SVMM8201A, 17101000599SSGF8802T, 17101000599SSPF8801X, 17101000599SSKF8901L, 17101000599SVKF8901I, 17101000599SNMM8302C, 17101000599SSGF9001Z, 17101000599SSSF9101O, 17101000599SMLF9101P, 17101000599SPKF8701Q, 17101000599SRKM8602B, 17101000599SSSM8504T, 17101000455PSGF8401E, 17101050316PSWF9001O, 17101020316PVPM8401P, 17101030316PNPM8501R, 17101050316PSMM8401M, 17101000599PPJM8801E, 17101010316PVVM8401C, 17101000599PVGM7201U, 17101000454PRBF8501Y, 17101000599SRRM8102Q, 17101000599RLPM8201I, 17101000599PMKF8801N, 17101000599PDTM8301R, 17101000599PMJM8201N, 17101000599NSAM8001Y, 17101000599NASM8201M, 17101000599SSHM9001N, 17101000599SMKM8203F, 17101000599SSSM8002I, 17101000599SKSM6801B, 17101000599SLTF8901R, 17101000599SRGM8901P, 17101000599SRGM8801Q, 17101000599SRPM8801P, 17101000599SSMM8502V, 17101000599SSKM8804O, 17101000599SMSM8801L, 17101000599SMRM8302O, 17101000599SNSM8502I, 17101000599SPNM9101X, 17101000599SPDM9001C, 17101000599SSJF8802K, 17101000599SSKF9006O, 17101000599SRKF8703E, 17101000599SLTF8901R, 17101000599SKSM6801B, 17101000599SMKM8203F, 17101000599SSKM8804O, 17101000599SSMM8502V, 17101000599SRGM8901P, 17101000599SPDM9001C, 17101000599SPNM9101X, 17101000599SNSM8502I, 17101000599SMRM8302O, 17101000599SMSM8801L, 17101000599SKPM8601Y, 17101000599SSGF8903N, 17101000599SSKF9003D, 17101020316RBSM8901H, 17101000599MSKM8601V, 17101000599MSKM8301Y, 17101000599MMBM8201G, 17101000599PNJF9001Q, 17101000599SVMM8601W, 17101010316SRSM8701R, 17101000599SSGM8202Q, 17101000599NPPM8202B, 17101050316SSCM8201X, 17101000599SMIM8201V, 17101000599SONM8201E, 17101000599SNAM7501W, 17101040316SMTM8401H, 17101040316SSSM8802V, 17101020316SRBM8201Q, 17101000599SJPM8701Y, 17101000599SYPM9001J, 17101000599SRKM8603W, 17101000599SMPM8701V, 17101000599SMKM8404Y, 17101000599SVSF8901K, 17101000599SMWF8801I, 17101000599SMKF8701T, 17101000599SSMF8601I, 17101000599SSTF9001M, 17101000599SRNF9001F, 17101000599SRWF8601F, 17101000599SNNF8601K, 17101000599SSSM8002I, 17101000599SSHM9001N, 17101000599SRPM8801P, 17101000599SVKM8201G, 17101000599SNGM8602R, 17101000599NMMF8901U, 17101000599SMBM9001L, 17101000599SNBM8101Q, 17101000454SDKF8901M, 17101000599NSAF9001A, 17101050316SDGF8801D, 17101000454SASF8401W, 17101040316SCGM8901Z, 17101000599NRTF8901U, 17101000599SACM8901S, 17101010316SRTM8401R, 17101000456SRKM8601I, 17101000599SMSM8401P, 17101050316SNWM7901U, 17101010316RSSM8101D, 17101000599RNDM8801K, 17101000599PSSM8503T, 17101000599SMMM8701E, 17101000599SKKM8501O, 17101000599SNKM8401M, 17101000599SASF8801G, 17101000599PSMM8601U, 17101000599PEKM8501P, 17101000599PMJF8901P, 17101000599RCCM9001Z, 17101000599PBJM9201R, 17101000599NNGF9001N, 17101000599NMMM8801M, 17101040316SZGM8501G, 17101010316SSKM8001V, 17101000599MVKM8701R, 17101000599MRPM8901E, 17101000599MSKM8801T, 17101000599MNPM8801J, 17101000599MRYM8701F, 17101000599MNTM8801X, 17101000599MNKM9001Z, 17101000599MMGM8701M, 17101000599MMHM9101I, 17101000599MMKM8601B, 17101000599MMBM8901Z, 17101000599MSBM8501X, 17101000599MMGM8901K, 17101000599MSBM8301Z, 17101000599MSPM8401I, 17101000599MRPM7301R, 17101000599RCSM8301H, 17101000599PPNF8301G, 17101000599PBDF8701U, 17101000599NPMM8301O, 17101000599NVKM8201P, 17101000599NHBM8201E, 17101000599MRHF8701N, 17101000599PDJF8401D, 17101030316ORJM8801J, 17101030316NVSM8801L, 17101040316NUSM8301M, 17101040316NSWM8401B, 17101040316NRGM8601W, 17101000599NNKM8001Z, 17101000599NLGM7901L, 17101040316NKPM8401E, 17101000599SPGF8901A, 17101000599SMMF8501P, 17101000599SVKM8401E, 17101000599SJCM8601M, 17101000599SKGM8301C, 17101000599SMKM8502H, 17101000599SSBM8202F, 17101000599SSMM8101E, 17101000599SVGM8701N, 17101000599SYAM8701C, 17101000599SMPM7002E, 17101000599SMUM8601H, 17101000599SKMM8702B, 17101000599SMPM8101B, 17101000599SJMM8301L, 17101000599SKPM8902Q, 17101000599SRPM8603H, 17101000599SSPM8901N, 17101000599SSDM8701Z, 17101000599SRJF8801Q, 17101000599SSPF9001Y, 17101000599SPHM9001Q, 17101000599SPSM8701J, 17101000599SNSM8301P, 17101000599SPSM8402H, 17101000599SKKM9001M, 17101000599SMRM8701P, 17101000599SJRM8601T, 17101000599SKSM8703E, 17101000599SYPF8901Q, 17101000599SHBM9001Q, 17101000599SNCF8601R, 17101040316RMPM8501Z, 17101000599PBMM9001K, 17101000599RGDM8801R, 17101000599PDKM8501Q, 17101000599RRGM9101X, 17101000599RNKM8701Q, 17101000599PRSM9002X, 17101000599PRSF8501N, 17101000599RPBM8502M, 17101000599PSPF9101S, 17101000599RRJM8201U, 17101000599UNKF8701E, 17101000599SRDF7201V, 17101000599RDSM7801I, 17101000599SJHF8501H, 17101000599RSPM8504J, 17101000599SRCF8401P, 17101000599SVDM8101C, 17101000457SVHF8801P, 17101020316SRSM8802G, 17101050316SSGM8601H, 17101000457MRRF8901E, 17101000599MSLM8901P, 17101050316SRBM8901U, 17101010316TNKM8801L, 17101020316SSMM8901B, 17101050316SNSM8301F, 17101000599SMGF8801E, 17101000454SPPM8901C, 17101000599SPSM8501L, 17101050316SVPM8201H, 17101010316SRJM8301W, 17101000599MSZM8301F, 17101000599SVJM9001E, 17101000599SSSM8604S, 17101000599SKCM8601L, 17101000599SKGM8701Y, 17101000599SPPF9001B, 17101000599SJDF8801Q, 17101000599SULM8801Y, 17101000599SLBM8702H, 17101000599SMGM9001W, 17101000599SSGM8601R, 17101000599SYPM9001J, 17101000599SNKF9002N, 17101000599SJPM8701Y, 17101000599SPGM9002O, 17101000599SSSM8901E, 17101000599SMKM8404Y, 17101000599SVKF8801J, 17101020316SVPM8801Q, 17101050316SRCM8301X, 17101050316SSJM8701X, 17101000599RNBF8701A, 17101000599SNLM8301K, 17101050316SRRM8701A, 17101000454SPGM8601G, 17101050316SNPF8901R, 17101010316SSDM8301N, 17101040316SSPF8801S, 17101000454TMTF8601Y, 17101050316SBRM8501S, 17101000599MSSF8701F, 17101000599SPGM8601U, 17101050316MMTM8601Q, 17101050316SGGM8701S, 17101000599SSTM8602Z, 17101000599SVSF9001M, 17101000599SHBF8601A, 17101000599SLPM8301A, 17101000599NSVM8301K, 17101000599PVDM8101X, 17101000599SMJM7802O, 17101000599SDLM8602M, 17101000599RRAM8001X, 17101000599RHSM8401B, 17101000599SRPM8301U, 17101000599SAPM8301L, 17101000599SJMM8601I, 17101000454MYBM8301F, 17101000454SASF8401W, 17101000457NDVF8901Z, 17101000599NHSM8901Y, 17101020316NSSM8501W, 17101010316NKFM8701U, 17101000599NPSF8801A, 17101050316NVAF8201S, 17101000599NVVF8401P, 17101000457NMPF8801J, 17101010316NYKM8601S, 17101000599NRKM8301S, 17101000599NJMM8201V, 17101000599NSKM8601O, 17101040316NSDM8501F, 17101000599NJBM8301B, 17101000599NJLM7601B, 17101000599NNKM8501U, 17101000599STRF8702M, 17101000599RMNM8601J, 17101000599RKMF8401Z, 17101000599PSCM8802R, 17101000599RPBM8601Q, 17101000599PSJF8802F, 17101000599PSGF9001U, 17101000599PBJM8601U, 17101000599RKKM8503L, 17101000599PAPM8901A, 17101000599PBPM8701B, 17101000599SSBF8201T, 17101000599VBKM6701O, 17101000599SVKM6601Q, 17101000599SSCF7101Y, 17101000599PGTM7601S, 17101050316STAM8701X, 17101000599SNDF8301R, 17101000455SNTM8001W, 17101050316SDGF8801D, 17101000599SSSM8501I, 17101000599SKKM8601N, 17101000599SKBF8601X, 17101000454SNKF8401H, 17101010316SRBM8601R, 17101000599SPRM7901R, 17101000599SPKM8801G, 17101030316SSBM8501H, 17101000599SSPM8401S, 17101010454STSF9001V, 17101000457STAF8901L, 17101040316SSSM8801A, 17101000599SSSF8901N, 17101000599SRWF8901C, 17101040316SPPM9001N, 17101050316SSNM8701L, 17101010316SSHF9001G, 17101020316SRSM8801L, 17101010316SDKM8301H, 17101000454SANF8701I, 17101010316SKRM8801A, 17101010316SLPM8501I, 17101000599SRSM8201M, 17101000599TKPM8701Q, 17101000599PBKM8601R, 17101000599MRMM8501R, 17101000599PMKM8101L, 17101000599SBJM8301C, 17101000599SBJM8501A, 17101000599SGPM8302A, 17101000599MVPM7901H, 17101000599SSPM8702K, 17101000599SLBM8701M, 17101000599SVSM8201I, 17101000599STKM8101J, 17101000599TALM8901K, 17101040316TMTM8501Z, 17101000454TMTF8601Y, 17101010316TSPM8701S, 17101020316TSGM8201T, 17101030316TNMM8101C, 17101000599TRKM8401B, 17101040316TPJM8201D, 17101030316TNMM8601X, 17101050316TDKM8101I, 17101000599SPKM9001H, 17101000599SBCM8601U, 17101000599SBCM8701T, 17101000599SBJF9101G, 17101000599SBKF9101D, 17101000599SBLM8101Y, 17101000599SBMF9102S, 17101000599SBMM8602L, 17101000599RTSF9101U, 17101000599RRVF8901M, 17101000599RGKF9101F, 17101000599SDSF9001E, 17101000599SDWM8901H, 17101000599SGAM8501W, 17101000599SGJM9001T, 17101000599SGSF8901Z, 17101000599RKPM8701E, 17101000599RTPM8501X, 17101000599SUPM8901L, 17101000599SKSM8602K, 17101000599SVKM9101A, 17101000599SYOM8701M, 17101000599SSAM8602E, 17101000599SSBM8902Y, 17101000599SSTM9001D, 17101000599SSMM9101X, 17101000599SSAM8701I, 17101000599SSPM8706Q, 17101000599SSMM8702T, 17101000599SSPM9002K, 17101000599SSTM8101J, 17101000599SSWM8601V, 17101000599RPSM8801P, 17101000599RPSM8901O, 17101000599STHM9101L, 17101000599SVDM8601X, 17101000599SSDM9001Z, 17101000599SSSM9101F, 17101000599SPVM8901Y, 17101000599SRHM8701O, 17101000599SBPF9001P, 17101000599SBPF8901N, 17101000599SACM9101T, 17101000599SBJM8502V, 17101000599SBWF9101T, 17101000599SCSF9101E, 17101000599SDSM8701V, 17101000599SDBM8801T, 17101000599SPPM8802M, 17101000599SRPM9002L, 17101000599SKPM8902Q, 17101000599SSPF9001Y, 17101000599SKSM8703E, 17101000599VBPM9001L, 17101000599SSPM8602L, 17101000599SKSM8702J, 17101000599UMSM8401B, 17101000599SVRM8701G, 17101000599SKRM8801Q, 17101000599SSSM8604S, 17101000599SNRM8601P, 17101000599SVRF9101O, 17101000599SSRF8901Q, 17101000599STPF8901V, 17101050316STAM8701X, 17101000599MVBM9001S, 17101000599MVMF8801T, 17101000599MYPM8701Z, 17101000599NLMM8801N, 17101000599NBSF9102J, 17101000599NDSM8901C, 17101000599NGPM9001K, 17101010316SDKM8301H, 17101000599NGSM9101A, 17101000455SASF8601P, 17101030316SSBM8501H, 17101010316SRBM8601R, 17101000599NVBM8601M, 17101000599SPRM7901R, 17101000454SNKF8401H, 17101000599SRSM8505P, 17101000599SBPM8901E, 17101000599SDJM8601X, 17101000599SDKF8802W, 17101000599SGUM8501O, 17101000599RRNF8701M, 17101000599RBNM9101S, 17101000599RBPF9001W, 17101000599SDDM8403H, 17101000599SDJM8501Y, 17101000599SDPM8901C, 17101000599NBLF8701K, 17101000599MMBM8502Y, 17101000599PRPF9102O, 17101000599MNDF7101Q, 17101000599MRMM8701P, 17101010316PSHM8901Q, 17101050316PPMM8701M, 17101000599PPRM8401K, 17101050316PVMF8901N, 17101000599PSVM8101Y, 17101050316PTSM8401T, 17101030316PSPM8701K, 17101010316PRSM8701M, 17101040316PPGM8701J, 17101000454PNCM8601P, 17101000599PSMM8401W, 17101000599PSBF8401M, 17101000599PPTM8301F, 17101000599PTMM8701S, 17101030316PSDM8501W, 17101010316PRPM7901A, 17101000599NSSF8801X, 17101000599NSCM8602H, 17101040316SVKM8701W, 17101000599NSHM8801V, 17101040316SVAM8401D, 17101000457STAF8901L, 17101040316SSSM8801A, 17101000599SSPM8401S, 17101010454STSF9001V, 17101000599SPKM8801G, 17101000599NSCM8601M, 17101000599VASF8603D, 17101000599SSMM8602U, 17101000599RKMF9101V, 17101000599NJJF8801H, 17101000599NNSM8802O, 17101000599VGKM8401Y, 17101000599SPDM8101I, 17101000599RATM8001J, 17101000599RESM6401S, 17101000599RVGM8501W, 17101000599RSSM8701N, 17101000599RKBM8501W, 17101000599PRGM8601N, 17101000599PSSM8401E, 17101000599NGRM8301I, 17101000599MKVM8701V, 17101000599SDDF9101W, 17101030316SRHM8801N, 17101000599RJKF7801J, 17101000599RPSM8301U, 17101000599NSGM9001Z, 17101000599VKDM7701T, 17101010316PRGM8001D, 17101030316PNPM8502M, 17101050316PSWF8701O, 17101030316PNGM8801P, 17101010316PSSM8801K, 17101040316PSPM8401I, 17101000454PRSM8601P, 17101040316PPJM8801Z, 17101010316PPPM8701X, 17101000599PVKM7901B, 17101000599PSGM8001S, 17101000599PSJM8101I, 17101000599PTPM7001X, 17101000599PSDM6501K, 17101000599PSFM8201T, 17101010316PRPM8201A, 17101000599SRBM8602C, 17101000599PBAM8801T, 17101000599PBMM8701K, 17101000599PCNF9101O, 17101000599PPBM8603U, 17101000599PRJM8701D, 17101000599PPBM8501F, 17101000599PPBM8701D, 17101000599PPPM9001N, 17101000599PSKF8101O, 17101000599PSPM8502H, 17101000599PSPM8701K, 17101000599PSPM9001K, 17101000599PTJM8801A, 17101000599PTSM8501C, 17101000599PUKF8601H, 17101000599NASF8601R, 17101000599MMSM8301G, 17101010316MMPF9001E, 17101030316OBJM8401D, 17101000454NSTF8601I, 17101000599NUGM8201C, 17101040316NRGM8602R, 17101000599NNAM8201B, 17101040316NGBM8501X, 17101000599NBGM8101W, 17101000599NSIM9001T, 17101000454NSDF8801C, 17101000599NPMF8301X, 17101030316NMSM8601W, 17101000455NVCF8901W, 17101000599NDPM8201S, 17101000599SSYF8801W, 17101000599SSKF9003D, 17101000599SSGF8903N, 17101000599SKPM8601Y, 17101000599SMPF8801D, 17101000599SMDF8801N, 17101000599SMKF9001T, 17101000599SPMF8501M, 17101000599SRTF9101M, 17101000599SSKM8805J, 17101000599STRM9001I, 17101000599SUPM8501P, 17101000599SVRM8701G, 17101000599SVUM8201C, 17101000599SNBM8801J, 17101000599SNCM8701H, 17101000599SSYF8801W, 17101000599PSWF9101X, 17101000599PVLF9001C, 17101000599PAMM9001L, 17101000599PAMM9002G, 17101000599PASM8801S, 17101000599PVTM8801U, 17101000599PBBM8701R, 17101000599PBPM8402Z, 17101000599PBSM8901Q, 17101000599PDJF9001A, 17101000599PSHM8701I, 17101000599PSPF8801S, 17101000599PSPM8401N, 17101000599MMKM8601B, 17101000599MMRF8901M, 17101010316SRTM8501Q, 17101000454SSBF8601B, 17101000599SSKM8401H, 17101020316SRSM8701M, 17101000599PSIF9101N, 17101050316SRTF8701D, 17101000599SKBM8601O, 17101000454SKGF8901R, 17101000599PTMF9201Z, 17101000454SGIF8101X, 17101010316SDDM9001Y, 17101000599RDRM8802Z, 17101000599SSSM8201L, 17101000599SSPM8502M, 17101030316SRGM8301V, 17101010316SSBM8801O, 17101000599SRGM8801Q, 17101000599PDJM8301V, 17101000599PDLM8801K, 17101000599PMMF9101H, 17101000599PNKM8901C, 17101000599PMPM8901O, 17101000599MNGM8701L, 17101000599MNGM8901J, 17101000599MNMM8501V, 17101000599MNTM8801X, 17101000599MPKM8901V, 17101000599NDTM8601C, 17101000599NGPM8701K, 17101000599NKCM8901R, 17101000599NKPM8401J, 17101000599MMJM9101C, 17101000599MPSM8501B, 17101000599MTDF8901V, 17101000599PSTM8201D, 17101000599SVIM8601I, 17101050316SSRF8801H, 17101000599SPYM8401U, 17101000599NDJM8101L, 17101000599RMMM8901J, 17101000599NKKM8901T, 17101000599NHSF9101I, 17101000599NRCF8601W, 17101000599NLBM8501X, 17101000599MMGM8701M, 17101000599NRPF9101H, 17101000599MNPM8801J, 17101000599MNJF8801K, 17101050316SSKM8301Y, 17101000599NNSM8401X, 17101000599MKSF8701N, 17101000599RTLM8201M, 17101040316STWM8601P, 17101000599RTNF9001K, 17101000599MVJM8701U, 17101000599PRVM9101S, 17101000599PRPF8701U, 17101000599PVPM8801G, 17101000599PVKM8501Y, 17101000599MWKM8901O, 17101000599PJWM8601Z, 17101000599NAJM8701I, 17101000599NBSM8801F, 17101000599NRSF8801Y, 17101000599MSBM8101B, 17101000599SDDM9101N, 17101000599SACF8701D, 17101000599SBCM8701T, 17101000599SAJM9101Y, 17101000599SDPF8802H, 17101000599SBGF9002L, 17101000599SCPF9001O, 17101000599SDSF9001E, 17101000599SBNF8901T, 17101000599SBSF9101F, 17101000599SATF8901C, 17101000599SAPF9001Q, 17101000599SAMF9001Z, 17101000599SASF9001H, 17101000599SBJM8702T, 17101000599NDSM8501G, 17101050316NAPM8401J, 17101050316NRGM8601R, 17101050316NVBF8501M, 17101000454NPDF8901E, 17101000599NNJF8201J, 17101000599NSNF8601O, 17101000599NVJF8401Z, 17101000599NRKF8801W, 17101000454NNPF8801X, 17101050316NVDF8701E, 17101050316NVBM8801A, 17101040316NSPM8401W, 17101030316NSSM8301T, 17101030316NRSM8401T, 17101000599NRNM7901K, 17101000599SRJM8602E, 17101000599SSVF8801F, 17101000599SSYF7801D, 17101000599SMPM8801U, 17101000599SVKM8602X, 17101000599SKJF9001Y, 17101000599SVVM8701U, 17101000599SVTM8601B, 17101000599SSZM8701L, 17101000599STSM8901D, 17101000599SSTM8603U, 17101000599SSSM8301K, 17101000599SSKM8702Z, 17101000599SSAM8601J, 17101000599SSMM8801X, 17101000599SSDM8802T, 17101000599SSKF8803C, 17101000599SHMM8601K, 17101000599SKSM8801N, 17101000599SSKM8803T, 17101000599SSPM8705V, 17101000599SPPM8401V, 17101000599SRJM8201N, 17101000599SNCM8201M, 17101000599SPBM8502F, 17101000599SMKM8701K, 17101000599SMKM8801J, 17101000599SKBM8301R, 17101000599SMGM8901U, 17101000599SJKM8901L, 17101000599SKRM8801Q, 17101000599SSSF8501R, 17101000599RKCM9201P, 17101000599RVPM8501V, 17101000599RNPM8702W, 17101000599MRKF8601F, 17101000599MVKM9001R, 17101000599RSFM8001H, 17101000599RTRF8101E, 17101000599SRSF8703G, 17101000599SJWM7501M, 17101000599PYGM6001A, 17101000599RRSM7301Z, 17101000599SAIM6701Q, 17101000599VJRM7401H, 17101000599RHWM7301X, 17101000599PBWM7401Q, 17101000599SDSM8802P, 17101050316SSWM8701K, 17101000457SSDF8601G, 17101000599PNTM8501F, 17101000599PPJF9101N, 17101000599PRMF8901B, 17101000599PRPM8402J, 17101000599PRTM8601A, 17101000599PGJM8201T, 17101000599RRNF8401P, 17101000599MLDF8401I, 17101000599NABM9001G, 17101000599NBCM8801B, 17101000599NMDM8601P, 17101000599NMJM8801V, 17101000599MMBM8202B, 17101000599NBSF9102J, 17101000599SMPM8701V, 17101000599SPGM8701T, 17101000599SRKM8603W, 17101000599SBNM8902F, 17101000599SDTM8601T, 17101000599SABM8201C, 17101000599SASM8901W, 17101000599SBBF8602B, 17101000599SGHF9101H, 17101000599SAKF9002A, 17101000599SBMF9102S, 17101000599SBYM8801E, 17101000599SADF8801Z, 17101000599PTSF9101I, 17101000599SBJM8801X, 17101000599PSPM9001K, 17101000599SASM7901D, 17101000599RAKM8701D, 17101000599RBGM8901M, 17101000599RBPM8901L, 17101000599RBVM8601W, 17101000599RGGF9101R, 17101000599SSNM8202V, 17101000599PPPF8601X, 17101000599STCF8601L, 17101000599SAPF8601R, 17101000599PSNF8601A, 17101000599VBVM8701T, 17101000599PCSM8801Q, 17101000599PESM8201U, 17101000599PJWM8601Z, 17101000599TRKM8401B, 17101000454MYBF8901I, 17101000599MMSF8701L, 17101000599MNGF8801T, 17101000599MPTM8301A, 17101000599MRKM8601W, 17101000599RPBF9001Y, 17101000599MYPM8701Z, 17101000599NVBM8601M, 17101000599NGPM9001K, 17101000599NGSM9101A, 17101000599NLMM8801N, 17101000599SSYM8401R, 17101000599NRTM8601O, 17101000599RDGM8401P, 17101000599RSPM8401Z, 17101000599RMJM8401X, 17101040316NTKM8401K, 17101000455NMPM8001S, 17101000599NAPM8401T, 17101000599NTGM7801E, 17101050316NSKM8401G, 17101040316NAMM8901S, 17101010454NUJM8201A, 17101050316NRGM8101W, 17101040316NDMM8501T, 17101000599NEKF8501M, 17101000454NVHF8501Q, 17101000454NMIF8801T, 17101000599NVPM8001C, 17101000599NBGF8601A, 17101000454NATF8601A, 17101000454NNGF9001Z, 17101000599RRPF6201Z, 17101000599NSCF6901G, 17101000599SMRF6901K, 17101000599NMZF8701J, 17101000599RBSM6501U, 17101000599SVBM8502Z, 17101000599SSNF6701S, 17101000599NKSF8001N, 17101000599SDSF6401V, 17101000599RJSM7801C, 17101000599SBSF7701N, 17101000599SSCM6801P, 17101000599RVSM6501A, 17101000599SSKF6301F, 17101000599TRSF6501Z, 17101000599SLYF8801D, 17101000454SDKF8901M, 17101000599PDJM8701R, 17101000599PDJM8301V, 17101000599PEJM8901O, 17101000599PESM8201U, 17101000599PJWM8601Z, 17101000599PAKM8601S, 17101000599PBJM8801S, 17101000599PBPM8402Z, 17101000599PBPM8201G, 17101000599PCSM8801Q, 17101000599PGCM8801I, 17101000599PPSM8701E, 17101000599PMJF8901P, 17101000599PSMF9001C, 17101000599PDSM8201V, 17101000599RVNM8101F, 17101000599SVWM8401U, 17101000599SPBM8201N, 17101000599PAMM9002G, 17101000599PAPM8901A, 17101000599PASM8701T, 17101000599PAAM9001V, 17101000599PATM8601R, 17101000599PVPM8801G, 17101000599PPGM9001O, 17101000599PDVF9001Q, 17101000599PSKF8101O, 17101000599PUKF8901E, 17101000599PJDM9101C, 17101000599PKPM8601T, 17101000599PKNM8801X, 17101010316TDGM8401L, 17101000454TGSM8801W, 17101010316TNKM8801L, 17101000599TAKM8301T, 17101000457SSGF8901U, 17101000457SVHF8801P, 17101000455SRAF9001Z, 17101000455SSDF8901N, 17101000599SUPF8601X, 17101000457SPGF8801Y, 17101000599SVDM8101C, 17101000599SSSM8503Y, 17101040316SSJM8601D, 17101020316SSDM8701E, 17101050316SNPM8201P, 17101020316SSGM8601W, 17101000599SABF8701G, 17101000599SABM8602T, 17101000599SACM8702P, 17101000599SAPM8802B, 17101000599SATF8901C, 17101000599SDPM8204U, 17101000599SDPM9001E, 17101000599SECM8301U, 17101000599SAPF9001Q, 17101000599SASF9001H, 17101000599SATM8702Q, 17101000599SBCF9101B, 17101000599SBDM8601R, 17101000599SBJM6601N, 17101000599SBPF9101O, 17101000599MSKM8801T, 17101000599SDKF8802W, 17101000599SDVF8801U, 17101000599SDDM8702J, 17101000599SABF9001G, 17101000599SCRM9002U, 17101000599SCRM8701Z, 17101000599SDLF9101Y, 17101000599SDSF8901C, 17101000599SASF9002C, 17101000599SAPF8602M, 17101000599SDBM8701U, 17101000599SAGF9102L, 17101000599SABM9101W, 17101000599SDPM8901C, 17101000599SBSM9101W, 17101000599SDPM8203Z, 17101000599SCJM9001X, 17101000599SCBM8701V, 17101000599SDKM9001T, 17101000599SDJM8901U, 17101000599SDPF8901L, 17101000599SDBF8202D, 17101000599SGNM8801G, 17101000599SGKM8901O, 17101000599SBPM8901E, 17101000599SBVM8401R, 17101000599SASF9101G, 17101000599SAPM8801G, 17101000599SBGF8801P, 17101000599SANM9101M, 17101000599SBEM8701N, 17101000599SDDM8403H, 17101000599ULKM8301B, 17101000599TRPM8401M, 17101000599SRTM8601F, 17101000599SABM8101D, 17101000599SBPM8401J, 17101000599SHPM8201F, 17101000599SPHM8401T, 17101000599SCPM8601G, 17101000599RLSM8701U, 17101000599RRKM7101Z, 17101000599RITM8301Y, 17101000599RSSM8101T, 17101000599RDDM8501X, 17101000599PPTM8701B, 17101000599NSVM8501I, 17101000599NMKM8201Y, 17101000599NVNM8801A, 17101000599SKKM8601N, 17101000599SKBF8601X, 17101000599NYKM8301L, 17101020316SKLM8801N, 17101000599PPBM9101C, 17101000599PPBM8602Z, 17101000599PPBM8603U, 17101000599PSKM8101F, 17101000599PSVM8501U, 17101000599PDMM8401L, 17101000599PMRM9001K, 17101000599PRVM9101S, 17101000599PAGM9101C, 17101000599PBJM9201R, 17101000599PBMM9001K, 17101000599MRPF8701P, 17101000599MRYM8701F, 17101000599MSGF9101O, 17101000599SASF9002C, 17101000599SDPM8203Z, 17101000599MMSM8201H, 17101000599MRHF9001N, 17101000599MRKM8701V, 17101000599MRTF9001D, 17101000599MSBM8101B, 17101000599MSSF8801E, 17101000599MSSM8601X, 17101000599MSSM8802Q, 17101000599MUNF9001S, 17101000599MVKM8701R, 17101000599PDSM8201V, 17101000454PNPF8801J, 17101000599PDPM8301D, 17101000599PDBM8701P, 17101000599PEKM8501P, 17101000599PGVM8101K, 17101000599PHMM8301I, 17101000599PBKM8502N, 17101000599PBMM8701K, 17101000599PBJM8601U, 17101000599PCNM8601H, 17101000599PDPM8501B, 17101000599PRPF8701U, 17101000599PAMM9001L, 17101000599PBAM8801T, 17101000599PBKM8901O, 17101000599PBPM8701B, 17101040316VDDM8301S, 17101000599VAMM8601W, 17101050316VAHM8601B, 17101000599VGSM7901C, 17101000599MSMM8301S, 17101000599MMSM8501E, 17101000599MRSM7501G, 17101000599MMMM8301Y, 17101000599MRGM8501J, 17101000599MMKM8501C, 17101000454MSBF9001Q, 17101000599MVRM8801V, 17101000599MTGM8201K, 17101050316MPMM8401K, 17101010454MMHF8301D, 17101000599MKVM8701V, 17101000599VDKM8701Y, 17101000599VBBM8701B, 17101030316VBMM8901S, 17101020316VAMM8501C, 17101030316VBKM8601B, 17101000599VFRM8201G, 17101030316MNGM8701L, 17101000456MSLM8501V, 17101000599MMKM8401D, 17101040316MTJM8601S, 17101020316MSBM8901Y, 17101000599MRMM8501R, 17101040316MZCM8301K, 17101000599MVWF8401T, 17101000457MTGF9001L, 17101000457MRRF8901E, 17101010316PPRF8601B, 17101000599NBSM8801F, 17101000599NBPM8801O, 17101000599NCKM8101J, 17101000599NDGM8901M, 17101000599NDGM8802I, 17101000599NDAM8901E, 17101000599NRSM8001X, 17101000599NAKM8701F, 17101000599NBSM8201L, 17101000599NBBM8701F, 17101000599NSTF9001V, 17101000599NASM7301S, 17101000599NSJF8901X, 17101000599NSYF8501I, 17101000599NUSF9101V, 17101000599MRSM8501Z, 17101000599MSKF9102X, 17101000599RCCM9002U, 17101000599PGGM8401A, 17101000599PJSM9001K, 17101000599RVSM8701K, 17101000599RNMM8703A, 17101000599RPBM8802J, 17101000599RDCF9101G, 17101000599RKKM8801S, 17101000599MTPM8701E, 17101000599MUKF9001B, 17101000599NBSF9101O, 17101000599NCPF8301B, 17101000599NDGM8901M, 17101000599PPBM8602Z, 17101050316NSSM8701F, 17101000599NBJM8701H, 17101000599NAKF8801N, 17101000599NDGF9101W, 17101000599NRSF8801Y, 17101000599NRTF8901U, 17101000599NTGM8401B, 17101000599NSTF9101U, 17101000599NSHM8701W, 17101000599NRRF9101B, 17101000599NMJM8801V, 17101000599NSYM8701X, 17101000599NSKM8801M, 17101000599NSGM8901X, 17101000599NSCM8602H, 17101000599NSPM8601Z, 17101000599SPSM8502G, 17101000599SPRM8301Q, 17101000599SRGM8701R, 17101000599PPBM8701D, 17101000599PPSM8501G, 17101000599PLJM8601K, 17101000599PMDM9101Z, 17101000599PJWM8601Z, 17101000599PKBM8401L, 17101000599PEJM8901O, 17101000599PESM8201U, 17101000599PDJM8701R, 17101000599PDJM8301V, 17101000599PBPM8402Z, 17101000599PBPM8201G, 17101000599PAKM8601S, 17101000599NBKF8801M, 17101000599NBSM8201L, 17101000599MPCM8601W, 17101000599RSWF8801J, 17101000599RTKM8301O, 17101000599NSYM8701X, 17101000599NVGF8601G, 17101000599PAAM9001V, 17101000599PBSF9001B, 17101000599PBSM8602O, 17101000599PGVM8101K, 17101000599PHDM8701F, 17101000599PMLM8901A, 17101000599PMTF8901L, 17101000599PNWM8501W, 17101000599PVPM8401K, 17101000599RAJM8601H, 17101000599PBVF9001S, 17101040316SMKM8701F, 17101040316SVDM8401U, 17101000599SVPM8301Q, 17101010316SSKM8701O, 17101000599NRPM9201X, 17101000599SGSF8401E, 17101000599MMBM8701B, 17101000599SSVM8602T, 17101050316SSKM8901S, 17101000599MMBM8901Z, 17101000599SRSM8401K, 17101000599PJDM9101C, 17101000599NASM9001H, 17101000599MVKF8701A, 17101000599RSGF9001G, 17101000599NPPM8801A, 17101000599NRGM8501C, 17101000599NSJM8201V, 17101000599NSKM8501P, 17101000599NSKM9101M, 17101000599PVTM8901T, 17101000599RBGM9001O, 17101000599PDCM8701M, 17101000599PDJM8701R, 17101000599PGMF8901M, 17101000599NSSM8501R, 17101000599RKAM8201C, 17101000599RMBM9001S, 17101000599RRRM8401U, 17101000599PPMM8701W, 17101000599NPRM8301Z, 17101000599MRGF9001Q, 17101000599PMGM8401U, 17101000599PMDM9101Z, 17101000599PNMF8901F, 17101000599PNJM9001H, 17101000599PPDM8801W, 17101000599PNPM8401S, 17101000599PRCM9101X, 17101000599PPSM8701E, 17101000599NSMM9101G, 17101000599PSGM8901J, 17101000599RBBM8401G, 17101000599PTTM8701X, 17101000599PKKM8701H, 17101000599PKJF9101S, 17101000599PMCF8901K, 17101000599NTGM8401B, 17101000599NSRM8601T, 17101000599PBJM8801S, 17101000599PAKM8402P, 17101000599PBPF8801J, 17101000599PBKM8901O, 17101000599PGCM8801I, 17101000599PDDF9001S, 17101000599PKGM8601U, 17101000599NBSM8301K, 17101000599NRPM9001Z, 17101000599NMKM8801S, 17101000599NSSM8601Q, 17101000599NSKM9001N, 17101000599MNKM9001Z, 17101000599MMWM9101P, 17101000599SGRM9001V, 17101000599SAMM9001Q, 17101000599SGUM8501O, 17101000599SBCM8401W, 17101000599SASM8902R, 17101000599SABF8701G, 17101000599SAGF9101Q, 17101000599SAPF8801P, 17101000599SBMF9103N, 17101000599SACM8702P, 17101000599SDKM9002O, 17101000599SDPM8204U, 17101000599SDWM8901H, 17101000599SGNM8201M, 17101000599SAVM8701P, 17101000599SBPM8001N, 17101000599SDPM8401H, 17101000599NPSM8301W, 17101000599RSGF8801F, 17101000599RSSF9001W, 17101000599RRNF8701M, 17101000599RSSF8601X, 17101000599RNPF9001K, 17101000599RRVF8901M, 17101000599RMPF9001L, 17101000599RMTF8401C, 17101000599RBPF9001W, 17101000599RGKF9101F, 17101000599RATF9001L, 17101000599RANF9101C, 17101000599RVTF8501S, 17101000599RASF8901M, 17101000599RVKF8901P, 17101000599RPKF8901V, 17101000599SRPM9001Q, 17101000599SRRM8601L, 17101000599PDPM8501B, 17101000599PDPM8301D, 17101000599PBJM8601U, 17101000599PCNM8601H, 17101000599PBJM9201R, 17101000599PBKM8502N, 17101000599PRVM9101S, 17101000599PAGM9101C, 17101000599PDMM8401L, 17101000599PMRM9001K, 17101000599TSMF9001A, 17101000599TDKF9201T, 17101000599TASM9001R, 17101000599TSDM8801R, 17101000599SRDM8601B, 17101000599SSKM8901C, 17101000599SSGM8803F, 17101000599SRSM8202H, 17101000599SSPM8201U, 17101000599SRMM8603Q, 17101000599SRSM8703X, 17101000599SMMM8703U, 17101000599SPBM9002D, 17101000599SRMM8201E, 17101000599SRVM9001Y, 17101000599SHPM8302Z, 17101000599SKBM8901L, 17101000599SLAM9101O, 17101000599SVBM8401F, 17101000599SLSM8701N, 17101000599SEWM8701I, 17101000599SGHF9101H, 17101000599SGMM8701K, 17101000599SAPM8402F, 17101000599SGSM9101R, 17101000599SAPF8602M, 17101000599SAPF8901O, 17101000599SBKF8901C, 17101000599SBPF8901N, 17101000599RKKF9001C, 17101000599RNMF9001T, 17101000599RPBF8801X, 17101000599RASF8901M, 17101000599SDKM8803I, 17101000599SDLM8502N, 17101000599SDLM8501S, 17101000599NDSM8901C, 17101000599RSGM8601Y, 17101000599NVNM8801A, 17101000599NYKM8301L, 17101000599MVBM9001S, 17101000599MVMF8801T, 17101000599NSGM9001Z, 17101000599SCWM8501M, 17101000599PMPM8301U, 17101000599VDDM8801S, 17101000599SACM8501W, 17101000599RSSF8903K, 17101000599RSKM8501N, 17101000599SFCM7901U, 17101000599SBNM8601N, 17101000599PSNM8401T, 17101000599PCNF9101O, 17101000599PSGF9001U, 17101000599PSGM8901J, 17101000599PRKF8701J, 17101000599PRGM8701M, 17101000599PSWF9101X, 17101000599PTMF9201Z, 17101000599PRMF8901B, 17101000599PSGF9002P, 17101000599PMKF8901M, 17101000599PMPF9101Y, 17101000599PJMF9001L, 17101000599PMMF9101H, 17101000599PDBF9001Y, 17101000599PDPF9001I, 17101000599PLKF9102J, 17101000599RPSM8801P, 17101000599RAKM8901B, 17101000599RBMM8901U, 17101000599RPPM8503R, 17101000599RHKF8801E, 17101000599RJTM8601U, 17101000599RJKM8401X, 17101000599RMKM8801Q, 17101000599RNAM8602Q, 17101000599RPSM8901O, 17101000599RNBM8402P, 17101000599RRRF8701A, 17101000599RBSM8701E, 17101000599RACM9101A, 17101000599RAKM8501F, 17101000599PSGF9002P, 17101000599SPKM8202H, 17101030316SRHM8801N, 17101000599SMNM9001B, 17101000599RJKF7801J, 17101000599SSMM8602U, 17101000599RSDM8801F, 17101000599RTPM8501X, 17101000599RVPM8901R, 17101000599RVSM9001K, 17101000599RYWM9101U, 17101000599RCRM8501I, 17101000599RCKM8801A, 17101000599RDPM8802F, 17101000599RFDM8701T, 17101000599RGSM8601A, 17101000599PTSF9101I, 17101050316NTAM8601H, 17101000599RSMF9001O, 17101000599RSSF8602S, 17101000599RBMF8501H, 17101000599RDDF9001E, 17101000599RAJM8801F, 17101000599RRRM8801Q, 17101000599RPPM8901X, 17101000599RRNM7201P, 17101000599RPBM8801O, 17101000599RPPM8701Z, 17101000599RCSM9001D, 17101000599RHPM8401K, 17101000599RSMM8601G, 17101000599RAPM8401R, 17101000599RSBM8502J, 17101000599RDCM8501A, 17101020316PRPM8601R, 17101000599SSLM8701B, 17101000599NMVM8601N, 17101000599SADM8601S, 17101000599SJMM8701H, 17101000599SBHM8601F, 17101000599SRPM8501S, 17101000599SCYM8501G, 17101000599SKPM8401A, 17101000599SGMM8801J, 17101000599SBSM8802R, 17101000599SCBM8701V, 17101000599SCRM8701Z, 17101000599SABM8901V, 17101000599SAMM8801P, 17101000599SANF9001W, 17101000599PSBM9001A, 17101000599PSNM8701Q, 17101000599PHDM8701F, 17101000599PPPM9001N, 17101000599PTJM8801A, 17101000599PBPM8401E, 17101000599PBSM8101Y, 17101000599PDTM8601O, 17101000599PDLM8801K, 17101000599PGGM8901V, 17101000599PBBM8701R, 17101000599PDCM8701M, 17101000599PDKM8501Q, 17101000599PDGM8901Y, 17101000599PHPM8401Y, 17101000599PBMM8801J, 17101000599PNPM8301T, 17101000599PNJM9001H, 17101000599PMSM8601I, 17101000599PRTM8601A, 17101000599PDDF9001S, 17101000599PAMM8901J, 17101000599PAKM8402P, 17101000599PLAM8501M, 17101000599PSPM8601L, 17101000599PSLM8702R, 17101000599PSZM8801F, 17101000599PVTM8601W, 17101000599PSCM8801W, 17101000599PSSM9001B, 17101000599PTSM8501C, 17101000599PMLM8901A, 17101000599SRWF8901C, 17101000599SSWF8801C, 17101050316SRSF8601H, 17101000454SRHF8501L, 17101050316SSKM7901Z, 17101010316SRSM8501T, 17101000599SRSM8303B, 17101050316SSWM8701K, 17101010454SSKM7901Q, 17101040316SSPM8301O, 17101000599SVMF8401H, 17101000599SNPM7901Z, 17101000457SRDF7901L, 17101000454SRIF8701G, 17101000454SPGM8601G, 17101000457SNSF8801Q, 17101000599MSPF8601P, 17101000599NAPF8801Y, 17101000599NBBM8601G, 17101000599NBJM8701H, 17101000599MVLM8401R, 17101000599NBBM8701F, 17101000599NDCM8301E, 17101000599NDSF8701N, 17101000599PAMM8901J, 17101000599PAWM8601I, 17101000599PBBF9001A, 17101000599MSJF9001G, 17101000599MSKF9101C, 17101000599MSPM8501H, 17101000599NNBF8801B, 17101000599NSKM8801M, 17101000599SBAF9101H, 17101000599SAKM8701W, 17101000599SAMF8901X, 17101000599SATM8701V, 17101000599SAKF8901D, 17101000599SBDF8901X, 17101000599SGAM8301Y, 17101000599SANF9001W, 17101000599SDGM8301J, 17101000599SDJM8501Y, 17101000599SDSM8901T, 17101000599SDBM8702P, 17101000599SBPM8604S, 17101000599SBSM8803M, 17101000599SBJM9101X, 17101000599SDJF8801E, 17101000599SDAM8802R, 17101000599SABM8602T, 17101000599SANM8801M, 17101000599SDLM8502N, 17101000599SBMM8602L, 17101000599SDWM8502G, 17101000599SGBM8501T, 17101000599SBMF9104I, 17101000599SBKM8505D, 17101000599SGSF9101A, 17101000599SBRM8801Z, 17101000599SBSF9201E, 17101000599SDJF9001F, 17101000599SBSM8901V, 17101000599SBKF8901C, 17101000599SDPF8801M, 17101000599SBPM9001G, 17101000599SMNM9001B, 17101000599RNPM8702W, 17101000599RRBM8601O, 17101000599RKCM9201P, 17101000599RMMM8901J, 17101000599RBPM8703D, 17101000599RBKM9001C, 17101000599RSGM8602T, 17101000599RHVM9001P, 17101000599RNGM8601D, 17101000599RSBM8601N, 17101000599RKPM8501G, 17101000599RMPM8601D, 17101000599RVPM8702O, 17101000599RRPF8601H, 17101000599RUBM8501M, 17101000599PBMM8301O, 17101000599PNBM8701F, 17101000599PNRF9101R, 17101000599PPJF9101N, 17101000599PSDF8701D, 17101000599PVLF9001C, 17101000599PAWM8601I, 17101000599PAKM8501T, 17101000599PAPM8601D, 17101000599PRPM8402J, 17101000599PVTM8901T, 17101000599PAHM8901Y, 17101000599PPKM8301G, 17101000599PAPM8602Y, 17101000599PNJF9001Q, 17101000599PNMF8901F, 17101000599NDAM8901E, 17101000599NDGF9101W, 17101000599NGMM8701T, 17101000599NLPM8701F, 17101000599PATM8601R, 17101000599PDBF8601Z, 17101000599PDBF9001Y, 17101000599PDPF9001I, 17101000599PDPM8301D, 17101000599NSAM8801Q, 17101000599NSPM8501A, 17101000599OAJM8601C, 17101000599PAKM8501T, 17101000599PASM8701T, 17101000599PRSM8901A, 17101000599NMPM8701E, 17101000599PSNF8801Y, 17101000599PSKF8301M, 17101000599PSTM8601Z, 17101000599PVMF9101Y, 17101000599PDBF8601Z, 17101000599PSJF9001L, 17101000599PTJF8801J, 17101000599PGGM8401A, 17101000599PVGM8801H, 17101000599PMPM8901O, 17101000599PRSF8501N, 17101000599PSPF9101S, 17101000599PSPF8801S, 17101000599PAKF9001A, 17101000599PAPF9101K, 17101000599PBJM8701T, 17101000599MKTM8601C, 17101000599RPSM8801P, 17101000599RBMM8901U, 17101000599RVSM9001K, 17101000599RYWM9101U, 17101000599RSKM8801K, 17101000599RSDM8801F, 17101000599RRDM8901F, 17101000599RRRM8601S, 17101000599RMKM8801Q, 17101000599RNAM8602Q, 17101000599RCKM8801A, 17101000599RDPM8802F, 17101000599RATM9201A, 17101000599RBSM8802Y, 17101050316PJGF8701T, 17101050316MMUF8601W, 17101000599RTPM8501X, 17101000599RVPM8901R, 17101000599RPSM8901O, 17101000599RRPM9001X, 17101000599RJTM8601U, 17101000599RJKM8401X, 17101000599RFDM8701T, 17101000599RGSM8601A, 17101000599RBBM8501F, 17101000599RCRM8501I, 17101000599RAKM8501F, 17101000599RAHM8601N, 17101000599RNBM8402P, 17101000599RRRF8701A, 17101000599PBBM8302Q, 17101000599NGSM9101A, 17101000599NDJM8101L, 17101000599NNMF8701V, 17101000599NRCF8601W, 17101000599NSSF8901W, 17101000599VDJM8402Z, 17101000599NLPM8701F, 17101000599NPPM8801A, 17101000599NPJM8801S, 17101000599NRPM9201X, 17101000599RBSM8502B, 17101000599SAPM8901F, 17101000599SAJM8802T, 17101000599SAGM8801H, 17101000599SJHF8501H, 17101000599RSHF9001D, 17101000599PPKM8301G, 17101000599PRNF8901Y, 17101000599PRPF8801T, 17101000599NMRM8502V, 17101000599NSYF8501I, 17101000599PAKF9001A, 17101000599MRKF9001E, 17101000599PVMF9101Y, 17101000599PVTM8601W, 17101000599PPSM8501G, 17101000599PRKF8701J, 17101000599PRSM9001C, 17101000599PSMM8402R, 17101000599PSSM8901Z, 17101000599NNGM8901C, 17101000599NNMF8701V, 17101000599RVBF9101R, 17101000599RAMM8901V, 17101000599RDMM8601V, 17101000599RDKM8301E, 17101000599RDPM8701L, 17101000599RKKF9001C, 17101000599RNMF8801S, 17101000599RHMF9001Z, 17101000599RJDF8701Y, 17101000599RAKF8501O, 17101000599RBMF9201D, 17101000599RPBF8801X, 17101000599RNMF9001T, 17101000599RVSM8901I, 17101000599RDCM9001Y, 17101000599RSGF8201L, 17101000599PBJM8801S, 17101000599PAAM9001V, 17101000599PATM8601R, 17101000599PAPM8901A, 17101000599PASM8701T, 17101000599PRGM8601N, 17101000599PAMM9002G, 17101000599PMJF8901P, 17101000599PSMF9001C, 17101000599PGCM8801I, 17101000599PPSM8701E, 17101000599PSKF8101O, 17101000599PUKF8901E, 17101000599PPGM9001O, 17101000599PDVF9001Q, 17101000599PTJM8801A, 17101000599NSJF8901X, 17101000599NSKF8601X, 17101000599NSPM8601Z, 17101000599PNCM8901A, 17101000599NDLM9101Y, 17101000599NDMM8901U, 17101000599NLAM8701Y, 17101000599NLBM9101U, 17101000599MLVM8901S, 17101000599MMPM8101R, 17101000599MMPM8901J, 17101000599MRNF8801U, 17101000599MRPM7301R, 17101000599PDPF8901G, 17101000599PDTM8601O, 17101000599PGGM8901V, 17101000599NDGM8802I, 17101000599RNGM8601D, 17101000599NCKM8101J, 17101000599RNSM8601T, 17101000599NGKM8801Y, 17101000599RMGM7301O, 17101000599RBSM8701E, 17101000599RACM9101A, 17101000599PBDM8201Q, 17101000599PBKF8301D, 17101050316PKKF9001G, 17101000457PDBF8201A, 17101050316PGPM8801L, 17101050316PGSM9001D, 17101000599SHFF9101M, 17101000599STPF8902Q, 17101000599PSSF8801J, 17101000599PHRM8201U, 17101000599PKBM8401L, 17101000599PMRM9001K, 17101000599MTJF8901D, 17101000599NISM8601A, 17101000599NKTM8801T, 17101000599NNGF9102H, 17101000599NPBM8901P, 17101000599PVMM8601R, 17101000599PAPM8601D, 17101000599PBKM8502N, 17101000599PBPM8401E, 17101000599PBTF8901W, 17101000599PSDF8701D, 17101000599PSSM9001B, 17101000599PLKF9102J, 17101000599NBMF9001H, 17101000599PMKF8901M, 17101000599NJKM8801V, 17101000599RSGM9001X, 17101000599RKPM8701E, 17101000599PGBM6901Y, 17101010316PABM8601D, 17101000599PMAM8401M, 17101050316PHRF9001O, 17101000599PKSM8301N, 17101000599PLNM8801W, 17101000599PDKM8801N, 17101050316PDGM8701Q, 17101000457PBMM8001O, 17101000599PBSM7601A, 17101000599MNSM8201G, 17101000599MNMF9101B, 17101000599MRKF8901C, 17101000599NSVM8501I, 17101000599NSJM8301U, 17101000599NSDM8201N, 17101040316NSWM8401B, 17101000599NRPM8602V, 17101000599NRMM8701I, 17101040316NRGM8602R, 17101040316NRGM8601W, 17101000599NNAM8201B, 17101000599NNCM8801P, 17101000599NLGM7901L, 17101000599NMSM8801U, 17101000599NHTM8501Z, 17101000599SAGM8601J, 17101000599SBSM8804H, 17101000599SCWF9101S, 17101000599SGSF8901Z, 17101000599SBKF9101D, 17101000599SBMF9001Y, 17101000599SAPF8901O, 17101000599SADF8802U, 17101000599SBKM8504I, 17101000599SDSM8802P, 17101000599SDNM8401N, 17101000599SGSF9001B, 17101000599SDGM8701F, 17101000599SDDF8601Y, 17101000599SAVM9101O, 17101000599SBTM8501W, 17101000599RRPM8601Y, 17101000599RHTM8201A, 17101000599RSGM8701X, 17101000599RRPM8602T, 17101000599RNAM8601V, 17101000599RPKM8101U, 17101000599RKDM8201T, 17101000599RMTM8501S, 17101000599RADM8502V, 17101000599RBWM8501U, 17101000599RPSM8601R, 17101000599REGM8801K, 17101000599RPPM8501B, 17101000599RRGM9101X, 17101000599RMLM8701O, 17101000599RPRM8901R, 17101000599TKPF8901X, 17101000599TRTM8001E, 17101000599TLKM8601F, 17101000599TSSM8501B, 17101000599TBPM8801Y, 17101000599TGSM8701L, 17101000599TDCF8801S, 17101000599TRGF8001A, 17101000599SYJF8801J, 17101000599TRBF9001I, 17101000599STPF8501Z, 17101000599SUJF8901M, 17101000599SSGF8901X, 17101000599SSDF9101H, 17101000599SSNF9101D, 17101000599SSRF8901Q, 17101000599STSM8902Y, 17101000599SPCM7701M, 17101000599SJPF9002C, 17101000599SVMF8701E, 17101000599SSWF8701D, 17101000457SVHF8801P, 17101000599SSSF9102J, 17101000457SSGF8901U, 17101000599STMF8801F, 17101000455SRAF9001Z, 17101000455SSDF8901N, 17101000599SUPF8601X, 17101000457SPGF8801Y, 17101000599SVDM8101C, 17101000599SSSM8503Y, 17101000599SSKM8502B, 17101000599SDPF8802H, 17101000599SDSF9103T, 17101000599SDSM8503N, 17101000599NNKM8001Z, 17101000599NNBM7901Y, 17101000599NMNM8401N, 17101000599NNKM8301W, 17101000599NKMM8301T, 17101040316NKPM8401E, 17101000599NDSM8501G, 17101040316NGBM8501X, 17101000599NBGM8101W, 17101000599NBPM8402N, 17101000599NSIM9001T, 17101050316NAPM8401J, 17101000454NPDF8901E, 17101000599RRKM8901K, 17101000599RRPM9001X, 17101000599RRDM8901F, 17101000599RRRM8601S, 17101000599RSKM8801K, 17101000599RAHM8601N, 17101000599RATM9201A, 17101000599RBSM8802Y, 17101000599RBBM8501F, 17101000599RBKM8201H, 17101000599PRGM8701M, 17101000599PSGF9001U, 17101000599PSGM8901J, 17101000599PSPM9001K, 17101000599PCNF9101O, 17101000599PJMF9001L, 17101000599PRNF8901Y, 17101000599PDPF8901G, 17101000599PBSF9001B, 17101000599PKKM8701H, 17101000599PMSF9001Q, 17101000599PSSF8801J, 17101000599PBTF8901W, 17101000599PVKM8701W, 17101000599PJKM9201G, 17101000599PVAM9201Y, 17101000599PVMM8601R, 17101000599PSPM8502H, 17101000599PTTM8701X, 17101000599PSCM8802R, 17101000599PSMM8402R, 17101000599PSTM8201D, 17101000599PSSM8701B, 17101000599PSWF9101X, 17101000599PTMF9201Z, 17101000599PRPF8801T, 17101000599PRKF8701J, 17101000599PLKF9102J, 17101000599PRNF8901Y, 17101000599PBVF9001S, 17101000599PDBF9001Y, 17101000599PDPF9001I, 17101000599PSCM8802R, 17101000599PSMM8402R, 17101000599PSPM8502H, 17101000599PTTM8701X, 17101000599PVAM9201Y, 17101000599PPBM8501F, 17101000599PRCM9101X, 17101000599PYCM8901P, 17101000599RSGM9001X, 17101000599RTSM8701M, 17101000599RKPM8701E, 17101000599PBTF8901W, 17101000599PKKM8701H, 17101000599PMSF9001Q, 17101000599PDPF8901G, 17101000599PBSF9001B, 17101000599PVMM8601R, 17101000599PVTM8801U, 17101000599PVKM8701W, 17101000599PJKM9201G, 17101000599PSSF8801J, 17101000599PKBM8401L, 17101000599PLJM8601K, 17101000599RMPM8801B, 17101000599RBPM8901L, 17101000599RTTM8501L, 17101000599RBGM8901M, 17101000599RCCM9002U, 17101000599RPBM8802J, 17101000599RTNF9001K, 17101000599RKPF9101M, 17101000599RSWF8801J, 17101000599RBGF8901V, 17101000599RDRM8802Z, 17101000599RNDM8801K, 17101000599RTKM8301O, 17101000599RTLM8201M, 17101000599RMBM9001S, 17101000599RNCM8401R, 17101000599SANM8601O, 17101000599RSHF9001D, 17101000599RPPF8501K, 17101000599NVVF8401P, 17101050316NVAF8201S, 17101000599NSMF8701Q, 17101000599NRBF8201D, 17101000599NBPF8501A, 17101000457NMPF8801J, 17101000599NJBF8001N, 17101010316NYKM8601S, 17101000599NRKM8301S, 17101000599NMLM8501S, 17101000599NRKM8302N, 17101000599NSRM8401V, 17101000599NSBM8301S, 17101000599PPMM8701W, 17101000599PRSM9002X, 17101000599PRJM8701D, 17101000599PSPM8701K, 17101000599PKTF8801O, 17101000599PLJF9001S, 17101000599PLKF9101O, 17101000599PMPF9102T, 17101000599PMCF8901K, 17101000599PPDM8801W, 17101000599PSSM8901Z, 17101000599PVRM8601C, 17101000599PUKF8601H, 17101000599PAKM8801Q, 17101000599PJSM9001K, 17101000599PKGM8601U, 17101000599PMGM8401U, 17101000599PBBF9001A, 17101000599PBLF9001W, 17101000599PCJF9001B, 17101000599PCWF8601P, 17101000599PDJF9001A, 17101000599PDBF8802S, 17101000599PIAF8901U, 17101000599PNCM8901A, 17101000599PVPM8401K, 17101000599PHRM8201U, 17101000599PADF9001V, 17101000599PGMF8901M, 17101000599PVKM8501Y, 17101000599PASM8602P, 17101000599PRSM9001C, 17101000599PASM8801S, 17101000599SSKF8302M, 17101000599SSGF8601A, 17101000457SPSF8601Q, 17101050316SPGF8801R, 17101010316STHM8601X, 17101000599SVKM8601C, 17101040316SSHM8701I, 17101000599SSPM8601Q, 17101000454SSPM7901G, 17101050316SSGM8601H, 17101000599SRMM8001G, 17101010316SSVM8501J, 17101000454SPTM8501U, 17101050316SPMM8101X, 17101000599SSMM8701Y, 17101050316SPGM8801I, 17101000599NSRF8701B, 17101000599NTSF8801W, 17101000599PADF9001V, 17101000599NSCF8701U, 17101000599NMLM8502N, 17101000599NJMM8201V, 17101000599NSKM8601O, 17101040316NSDM8501F, 17101000599NJBM8301B, 17101000599NGKM8501B, 17101000599NSCM8601M, 17101000599NMKM8201Y, 17101000599NBPM8302O, 17101000599NRCM8001T, 17101040316NTKM8401K, 17101000599SRSM8502E, 17101000599SBPF8801O, 17101000599SSBF8801N, 17101000599SSSF8602L, 17101000599STRM8801H, 17101000599SRSF8501S, 17101000599SVTM7701H, 17101000457SPSF8601Q, 17101010316STHM8601X, 17101000599SVKM8601C, 17101000599SSPM8601Q, 17101000599STCM8601C, 17101000454SSPM7901G, 17101050316SSGM8601H, 17101040316SRBM8701B, 17101020316SSRM8901M, 17101050316SPGM8801I, 17101000599SDJF8602B, 17101000599SCPF9002J, 17101050316SPGF8801R, 17101000599SSKF8302M, 17101040316SSHM8701I, 17101000599SSPM8001W, 17101010316SSVM8501J, 17101000599SSGM8201V, 17101050316SPMM8101X, 17101000599SPRM8701M, 17101000599SPPM8701S, 17101000599SPSM8302I, 17101000599SPPM8702N, 17101000599SSMM8701Y, 17101050316SPBF8901F, 17101000599SRCF8402K, 17101000599RSRM8901O, 17101000599RSGF9001G, 17101000599RSPM8701W, 17101000599RPPM8502W, 17101000599RVGM8502R, 17101000599RNSM8601T, 17101000599RPBM8502M, 17101000599RKKM8901R, 17101000599RKKM8503L, 17101000599RDKM8701A, 17101000599RGRM8501E, 17101000599RCAM9001F, 17101000599RCYM8801K, 17101000599RSMM8901D, 17101000599RASM8401I, 17101000599RSKM8402J, 17101000599PPAF9101O, 17101000599PSJF8802F, 17101000599PSIF9101N, 17101000599PBSM8602O, 17101000599PNTM8501F, 17101000599PPBM8901B, 17101000599PSHM8701I, 17101000599PSSM8503T, 17101000599PMTF8901L, 17101000599PRPF9102O, 17101000599PSNF9001Z, 17101000599PSCF8701G, 17101000599PANF8601S, 17101000599NMKM8801S, 17101000599NMRM8502V, 17101000599NMMM8801M, 17101000599NNGF9101M, 17101000599NNSM8502R, 17101000599NRRF9101B, 17101000599VKKM8001Y, 17101000599VCNM8601R, 17101000599VBVM8501V, 17101000599VKDM7701T, 17101000599VGKM8401Y, 17101000599VDKM8701Y, 17101000599VBBM8701B, 17101020316VAMM8501C, 17101000599SSJM8001O, 17101050316VDJM8601S, 17101030316VDGM8001R, 17101000599VBGM8401P, 17101000599SRSM8501J, 17101000599PBSM8901Q, 17101000599PNPM8401S, 17101000599PNWM8501W, 17101000599PNKM8901C, 17101000599OAJM8601C, 17101000599PDSM8301U, 17101000599PBKF8801Y, 17101000599PBPF8801J, 17101000599PKJF9101S, 17101000599NPPM8202B, 17101000599NRBM8801O, 17101000599NSCM8301P, 17101000599NSPM8501A, 17101000599NSRM8601T, 17101000599NDMM8901U, 17101000599NGMM8701T, 17101000599PYCM8901P, 17101000599PMPM8301U, 17101050316PMGM7901M, 17101000599PHSM7801S, 17101040316PJMM8401A, 17101000454PBMM8801V, 17101010316PBPM8501N, 17101000599PBMM8001R, 17101000457PAJF8601B, 17101000599PGWM8401E, 17101000599PGGM8101D, 17101000599PAAM8501X, 17101000599PAPM8701C, 17101000599PBSM8601T, 17101000599PDKM8601P, 17101000599PBPM8501D, 17101050316PBWM8201B, 17101050316PMPM8601H, 17101050316PMAM8601A, 17101050316PJMM8301W, 17101000599PKSM8501L, 17101000599PGGM7901C, 17101000599PGJM8201T, 17101050316PBKM8301K, 17101020316PCSM8701W, 17101000599PDKM8402M, 17101000599PAPM8501E, 17101030316PHSM8301Q, 17101000599PJVM8101H, 17101000599PBRM8301Z, 17101050316PBBM8501J, 17101050316PDKM7801K, 17101000599RSKF9001U, 17101000599RSNF8301P, 17101000599RSJF8802R, 17101000599RVMF8901J, 17101000599RRTF9101T, 17101000599RSSF8902P, 17101000599RNIF8601G, 17101000599RPPF8501K, 17101000599RDBF9001K, 17101000599RGLF8801C, 17101000599RAPF9001X, 17101000599RACF9101J, 17101000599RMAM9001V, 17101000599RRBF9101V, 17101000599RRPM8901V, 17101000599RVMM8601D, 17101000599NNVM8701L, 17101000599NRCM8601N, 17101000599NSSF8901W, 17101040316VDDM8301S, 17101030316VBMM8901S, 17101000599VAMM8601W, 17101050316VAHM8601B, 17101030316VBKM8601B, 17101000599VFRM8201G, 17101050316VKBM8301M, 17101000599VGMM8201U, 17101010316VGWM8001C, 17101020316VDJM8701G, 17101000599VDSM8701A, 17101000599VBJM8701D, 17101000599VADM8601X, 17101000599RSPF8801E, 17101000599RSJF8801W, 17101000599RSDF9101O, 17101000599RJAF8801G, 17101000599RPPF8701I, 17101000599RASF8801N, 17101000599RDPF8901S, 17101000599RRPM8401A, 17101000599RKPF9102H, 17101000599RGBM8701Y, 17101000599RNPM8901Z, 17101000599RSBM8702H, 17101000599RMTM8901O, 17101000599RDJF8601N, 17101000599RKBM8901S, 17101000599RSDF8801O, 17101000599PVPM8801G, 17101000599PSNM8701Q, 17101000599PHDM8701F, 17101000599PSVM8501U, 17101000599PSBM9001A, 17101000599PPBM8603U, 17101000599PSKM8101F, 17101000599PPBM9101C, 17101000599PPBM8602Z, 17101000599PNPM8301T, 17101000599PNJM9001H, 17101000599PJDM9101C, 17101000599PKPM8601T, 17101000599PGVM8101K, 17101000599PHMM8301I, 17101000599PDBM8701P, 17101000599PMPF9102T, 17101000599MSTF8701C, 17101000599VGSM7901C, 17101000599VKCM8101V, 17101000599VGLM8501U, 17101000599VDBM8401C, 17101000599VDCM8201B, 17101000599VDDM8101Z, 17101000599VBKM8301E, 17101000599VAWM8801Q, 17101010454VCDF8901I, 17101000599VKDM8101S, 17101010316VGPF8901X, 17101000599VBMF8501F, 17101050316VBMM7801Q, 17101000599ULKM8301B, 17101000599PKGM8801S, 17101000599PMHM8401R, 17101050316PBJM8901H, 17101000599PBBM8301V, 17101000599PBLM8501P, 17101000599PDMM8102J, 17101050316PBKF8401S, 17101050316PBGF8401E, 17101000599PDDM8301N, 17101000599PDJM8401U, 17101010316PBLM8601Y, 17101000599PBNM8601I, 17101000599PDRM8201Y, 17101050316PGKM8301F, 17101010316PDRM8701D, 17101030316PLPM8801Q, 17101000599PSTM8601Z, 17101000599PVAM9201Y, 17101000599PVGM8801H, 17101010316VBDM8901D, 17101000599VAPM8201R, 17101000599VJTF8501C, 17101000599VGPM8401J, 17101000454VCBF8501X, 17101050316VCHF8801G, 17101050316VGTF8601U, 17101000599VBJF8601N, 17101050316VDJM8901P, 17101000599UPTM8101Y, 17101000599UMBF8601H, 17101000599UUUM8501M, 17101040316URNM8901B, 17101000599PDKM8701O, 17101000599PDSF8701Z, 17101000599PGSM8101T, 17101000599PGSM8001U, 17101000599PBCM8101U, 17101000599PBGM8701C, 17101000599PAKM8201W, 17101040316PAJM8601Q, 17101030316PKGM8201Y, 17101050316PMOM9001J, 17101000457PMPM7201Z, 17101000599PHNM7001P, 17101000599PBSM8003P, 17101000599PBSM8002U, 17101000454PATM8101I, 17101010316PARM8501I, 17101000599NJSM8301C, 17101000599NBKM8401H, 17101000599NDFM8601S, 17101000599NASM8201M, 17101000599NATM8601F, 17101050316NVBF8501M, 17101050316NRGM8601R, 17101000599NNJF8201J, 17101000599NPMF8301X, 17101000599NSGF8801H, 17101000599NJMM7801W, 17101000599NVJF8401Z, 17101000599NMTM7901X, 17101000599NVBM8501N, 17101050316NVDF8701E, 17101050316NSSM8701F, 17101000599SAKM8402U, 17101000599SANM8601O, 17101000454SRHF8801I, 17101000455SPGF8801I, 17101000599SNDF8301R, 17101000599SSBF8701O, 17101040316SPPM9001N, 17101020316SSPM8401X, 17101000599SPKM8201M, 17101000599SVCM8401C, 17101040316SSKM8901X, 17101000599SRKM8001M, 17101000599SSSM8702B, 17101000599SSJF8601R, 17101000599SRTM8602A, 17101000599SSSM7901L, 17101000599RGDM8801R, 17101000599RGGF9101R, 17101000599RDCF9101G, 17101000599RHSM8201D, 17101000599RSBM8503E, 17101000599RVSM8701K, 17101000599RCCM9001Z, 17101000599RNRM9101U, 17101000599RKMF8401Z, 17101000599RPMF8501T, 17101000599RNMM8703A, 17101000599RPGF8901H, 17101000599RVCM8801F, 17101000599RBGM9001O, 17101000599RPBM8601Q, 17101000599RRRM8401U, 17101000599SRKF8901M, 17101000599SSPF9101X, 17101000599SRSF8402O, 17101000599SRKF9001O, 17101000599SPDF9001L, 17101000599SPBF8501T, 17101000599SJKF8501Y, 17101000599SMJF8901U, 17101000599SVGM9001N, 17101000599SVSM8901B, 17101000599SRPM9101P, 17101000599SSBM8901D, 17101000599SRSM8601I, 17101000599SRWM8801U, 17101000599SRBM8601H, 17101000599SRPM9002L, 17101020316SSDM8701E, 17101020316SSGM8601W, 17101040316SSJM8601D, 17101050316SNPM8201P, 17101000599SRTM8601F, 17101000599SSWF8801C, 17101000599SSMF8501J, 17101000454SRHF8501L, 17101000599SRWF8901C, 17101050316SRSF8601H, 17101000599SRJF8101X, 17101000599SSGM8802K, 17101000599SRSM8302G, 17101010316SRSM8501T, 17101000599SRSM8201M, 17101050316SSKM7901Z, 17101000454NSDF8801C, 17101000599NBKM8201J, 17101000599NBKF8301R, 17101030316NMSM8601W, 17101000599NSNF8601O, 17101000599NRKF8801W, 17101000455NVCF8901W, 17101000599NDPM8201S, 17101000454NNPF8801X, 17101050316NVBM8801A, 17101000599NVPM8501X, 17101000599NSVM8301K, 17101040316NSPM8401W, 17101000599NRKM8401R, 17101030316NSSM8301T, 17101000599NNLM8301T, 17101000599PMMF9101H, 17101000599PMKF8901M, 17101000599PMPF9101Y, 17101000599PRMF8901B, 17101000599SVMM8502S, 17101000599SSNM8901T, 17101000599SRSM8402F, 17101000599SPYM8401U, 17101010454STSF9001V, 17101000599SNMF8401P, 17101010316SSMM8601J, 17101000599SVNM7901X, 17101010316STCM8901J, 17101000599SSCM8301G, 17101000599SSKM8001L, 17101040316SRNM8001Y, 17101000599PRCM9101X, 17101000599PSMM8601U, 17101040316SVDM8401U, 17101000599SSJM8501J, 17101000599SSHM8101T, 17101000599SRPM7601Y, 17101000599SRCM8601E, 17101050316SPSM8501B, 17101000599SSWF8802X, 17101050316SNPF8901R, 17101000599SSLM8701B, 17101020316SVPM8801Q, 17101000599SSKM8202E, 17101000599SSKM8403X, 17101010316SSNM8201K, 17101050316SRCM8301X, 17101000599PSMM8601U, 17101000599PSTM8201D, 17101000599PSSM8701B, 17101010316SPWM8701H, 17101000599SPDF8301P, 17101000455SNSF8001I, 17101000599SNBM8101Q, 17101010316SSKM8701O, 17101050316SSCM8201X, 17101010316SRSM8701R, 17101000599SRPM8301U, 17101000599SONM8201E, 17101000599SPKM8801G, 17101000599SVAM8001M, 17101000599SSDM8801Y, 17101000599SSNM8203Q, 17101000599PMDM9101Z, 17101000599PPBM8701D, 17101000599PPSM8501G, 17101020316SVPM8801Q, 17101010316STCM8901J, 17101010316SSKM8701O, 17101010316SSVM8401K, 17101010316SSNM8201K, 17101040316SRNM8001Y, 17101020316SRSM8701M, 17101050316SNSM8301F, 17101000599SPKM8801G, 17101000599SVAM8001M, 17101050316STAM8701X, 17101040316SSGM8401O, 17101000599SSKM8201J, 17101000599RBBM8401G, 17101000599RBVM8601W, 17101000599RSRM8801P, 17101000599RAKM8701D, 17101000599RNBM8801Q, 17101000599RRBM8201S, 17101000599RSAM8201U, 17101000599RKKM8502Q, 17101000599RDMM8901S, 17101000599RNMM8702F, 17101000599RKAM8701X, 17101000599RSPM8504J, 17101000599RSPM8802Q, 17101000599RDNM8801Q, 17101000599RDKM8901Y, 17101000599RMSM8701T, 17101000599NJLM7601B, 17101000599NRPM8601A, 17101000599NMSF8201J, 17101000599NRPM8401C, 17101000599NNKM8501U, 17101000599NSGM8501B, 17101000599NSMM7001V, 17101000599NRTM8602J, 17101000455NMPM8001S, 17101010316VCKM8601K, 17101000599VDPM8301N, 17101040316VBGM8401K, 17101000599VDRM7901I, 17101000599VGDM8901O, 17101000599VBSM8201H, 17101000599VKDM8701M, 17101000599PRSM8901A, 17101000599PSPM8401N, 17101050316SSCM8201X, 17101010316SRSM8701R, 17101050316SRCM8301X, 17101000599SONM8201E, 17101000599SVCM8301D, 17101000599SSNM8203Q, 17101000599SSAM8001P, 17101050316SRBM8901U, 17101050316SNWM7901U, 17101000457STAF8901L, 17101000457SPKF8901L, 17101000599SVPM8301Q, 17101000599SSVM8101D, 17101010316SSJM8201W, 17101000599NSSM8401S, 17101000599NAMF8601J, 17101000599NBJF8101W, 17101000599NBMF8601I, 17101000599NVGF8601G, 17101040316SMTM8401H, 17101010316SPTM8301U, 17101040316STWM8601P, 17101040316SSAM7901I, 17101050316SSSM8401Z, 17101010316SRBM8601R, 17101000599SSGF8501B, 17101050316SRMF8901W, 17101000454SSBF8601B, 17101040316SVAM8401D, 17101010316SSDM8801I, 17101000599SNPM8501W, 17101050316SPBF8901F, 17101040316SNGM8801P, 17101020316STDM8201I, 17101000599SPSM8501L, 17101000599SSTM8602Z, 17101050316SSBF8902X, 17101000454SSPF8901I, 17101000599SRCF8401P, 17101000455SSSF8901U, 17101030316SNKM8901H, 17101020316SRSM8801L, 17101030316SSCM8501E, 17101000599STKM8501F, 17101050316SSSM8501Y, 17101050316SSPM8601G, 17101000599SRMM8001G, 17101000599NLPM8601G, 17101000599NHGM8301O, 17101000599VHKF8701D, 17101000599VBPM8501N, 17101000599VJKM6801F, 17101000599VADM8301A, 17101030316VBJM8701D, 17101000599VCMM7901Y, 17101000597VBHM8601U, 17101000599VAMM7801B, 17101000599VISM8501X, 17101000599VGNM8301Q, 17101000599VGBM8701W, 17101000599VDKM8101E, 17101000599VDKM8502V, 17101000599VBSM8701C, 17101000454SPTM8501U, 17101000599SYKF8701H, 17101000599SNLM8301K, 17101000599SNPM8501W, 17101000599SVHF8601U, 17101000599SPSM8501L, 17101000599SSTM8602Z, 17101000455SSSF8901U, 17101000599SSSF7101C, 17101000599SVPM8102N, 17101020316SRSM8801L, 17101030316SSCM8501E, 17101050316SSSM8501Y, 17101000599SNCM8401K, 17101020316SSRM8901M, 17101010316SNKM8101Z, 17101040316SNGM8801P, 17101020316STDM8201I, 17101050316SSBF8902X, 17101000454SSPF8901I, 17101000599SRCF8401P, 17101000599SPJF8401W, 17101000599SRTM8302D, 17101000599STKM8501F, 17101050316SSPM8601G, 17101050316SSSM8901U, 17101000454SSKM8501S, 17101040316SRBM8701B, 17101000599SVSM8201I, 17101010316SSKF8601Y, 17101000599SSVF8401J, 17101050316SRSF8902Z, 17101000599RSJM8901M, 17101000599RPIM8701U, 17101000599RSPM9001W, 17101000599RMKM8701R, 17101000599RNSM8501U, 17101000599RJMM9001O, 17101000599RMGM7301O, 17101000599RDSM8001J, 17101000599RJRM9101Y, 17101000599RBVM8701V, 17101000599RCJM8501G, 17101000599RBGF9101W, 17101000599RAKM8601E, 17101000599RSSF8903K, 17101000599RBPF8802Q, 17101000599RPPM8503R, 17101000599NNSM8502R, 17101000599NPBM8901P, 17101000599SCSF9101E, 17101000599SBWF9101T, 17101000599SAPF8602M, 17101000599SASF9002C, 17101000599SAGF9102L, 17101000599SDBM8701U, 17101000599SDSM8701V, 17101000599SDPM8901C, 17101000599SDPM8203Z, 17101000599SCBM8701V, 17101000599SAKM8701W, 17101000599SANF9001W, 17101000599SDBM8702P, 17101000599SANM9101M, 17101000454VJAF8001Y, 17101010454VGBF8901K, 17101000599STJM7901L, 17101010316ULPM8701S, 17101000454UHPF8901F, 17101000599USPM8601C, 17101000599UKKM8301C, 17101000599UGDF8901E, 17101000599UMKF8601G, 17101000599SNJM8201R, 17101010316SSDM8301N, 17101000599SNGM8701V, 17101000454SVMM8201M, 17101040316SRSM8201H, 17101000599SRSF8202Q, 17101010316SSKF8601Y, 17101000599NHPM8601K, 17101000599NKCM8901R, 17101000599NLMM8801N, 17101000599SABM9101W, 17101000599SBSM9101W, 17101000599SBAF9101H, 17101000599SAMF8901X, 17101000599SAKF8901D, 17101000599SGKM8901O, 17101000599SAPM8801G, 17101000599SBPF8801O, 17101000599SANM8801M, 17101000599SBKM8505D, 17101000599SBKF8901C, 17101000599SDJF8602B, 17101000599SAJM9101Y, 17101000599PAGM8501F, 17101040316PBKF9001U, 17101000454PJKF9001D, 17101030316PGSM8501P, 17101000599PASM8601U, 17101020316PANM8701N, 17101020316PBBM8301A, 17101000454PBSM8801D, 17101040316PBAM8601Q, 17101030316NVSM8801L, 17101000599NASM8301L, 17101000454NSTF8601I, 17101000599NNKM8601T, 17101000599NSBM8501Q, 17101000599NUGM8201C, 17101000599NSGM8101F, 17101000599PAAM8301Z, 17101000599PBPM8801A, 17101000599PBWM8501I, 17101000599PKAM8301P, 17101000454PLNM8501L, 17101000599PBSM8501U, 17101000599PBKM8501S, 17101040316PMGM8201R, 17101000599PBMF7701A, 17101040316PABM8601O, 17101010316PBKM8101G, 17101000599PKNM8201D, 17101000599PMNM6701K, 17101000599PCVF8801Q, 17101000457PLPF8901V, 17101000599PMPM8701Q, 17101000599RTSF9101U, 17101000599RBNM9101S, 17101000599SCJM9001X, 17101000599SATM8701V, 17101000599SDPF8901L, 17101000599SGNM8801G, 17101000599SDSM8901T, 17101000599SBPM8604S, 17101000599SBGF8801P, 17101000599SBRM8801Z, 17101000599SBPM9001G, 17101000599SDDM9101N, 17101000599SDSF9001E, 17101000599SATF8901C, 17101000599SBJM8702T, 17101000599SAGF9101Q, 17101000599VAJM7701L, 17101050316VDAM8601T, 17101000454VBIF8701B, 17101050316VGPF9001F, 17101000454VBGF8801G, 17101000599VKNM8101O, 17101000599USSM8401V, 17101000599UBSM8401M, 17101000599USCM8201T, 17101050316USSM8601J, 17101000599URPM8601D, 17101000599ULKM8501Z, 17101000454UEBM8401U, 17101000599UMBM8601Y, 17101050316UANF9001Y, 17101000599USKM6901C, 17101000599RTPF9101D, 17101000599SDJM8901U, 17101000599SBDF8901X, 17101000599SDGM8301J, 17101000599SBPM8901E, 17101000599SASF9101G, 17101000599SBEM8701N, 17101000599SDLM8502N, 17101000599SDJF9001F, 17101000599SACF8701D, 17101000599SDPF8802H, 17101000599SASF9001H, 17101000599SABF8701G, 17101000599SAGM8601J, 17101000599SAPF8901O, 17101000599SAVM9101O, 17101000599PEKM8501P, 17101000599SDBF8202D, 17101000599SGAM8301Y, 17101000599SBVM8401R, 17101000599SBSM8803M, 17101000599SDJF8801E, 17101000599SDAM8802R, 17101000599SABM8602T, 17101000599SDWM8502G, 17101000599SGSF9101A, 17101000599SBSM8901V, 17101000599SBGF9002L, 17101000599SAPF9001Q, 17101000599SGRM9001V, 17101000599SGUM8501O, 17101000599SBMF9103N, 17101000597UDPF8601K, 17101000599UTLM8501O, 17101020316URKM9001W, 17101000599UBGM7801Z, 17101000599UMNF9001W, 17101010316USPM8801K, 17101000599TVBM8701V, 17101000599STPM8601P, 17101050316SNNM8601R, 17101000599SPKF8402O, 17101000599SSSM8402E, 17101000599SSCM8501E, 17101050316STJF8801E, 17101000455SSPF8601G, 17101000599SSPF8701Y, 17101050316SRSF9001G, 17101000599PBSM7901X, 17101050316PBSM8201N, 17101000599PAPM6501S, 17101000457PDGF8701G, 17101000599PBKM8602M, 17101000599PCKM8501R, 17101000599PKKM8101N, 17101020316PKWM8201H, 17101000457PBWM7801J, 17101040316PDSM8501N, 17101000457PASF8701Z, 17101000457PBKF8801V, 17101000599NUTM8201P, 17101030316OBJM8401D, 17101000599NUCF8701S, 17101000599NEHF8501V, 17101000599UDAM8501L, 17101030316URPM8201H, 17101010454UABF8401C, 17101020316UHPM8601S, 17101010316SUCM8701K, 17101020316SSSM8501N, 17101000599SNPM8301Y, 17101000599SVSF9001M, 17101000599SVPM8501O, 17101000599SSBM8101L, 17101000599SSKF9001N, 17101000599SRGF8701A, 17101000599SSSF7901U, 17101000599SRMF7501R, 17101000599SSSF8802J, 17101030316SRBM8401J, 17101000599PBKM8601R, 17101030316PDFM8501F, 17101050316PDMM8801X, 17101000599PANM8401L, 17101050316PAYF8801Z, 17101050316PBGF9001B, 17101000599PASM8401W, 17101000599PBDM8501N, 17101000599PCVM8301M, 17101000599PDSM8601R, 17101000599PMGM8701R, 17101030316PAAM8601W, 17101000599PDKM8401R, 17101000599PJKM8501K, 17101000599PDBF8801X, 17101000599PLVM7801F, 17101030316ORJM8801J, 17101000599NTMM8601H, 17101000599NRNM7901K, 17101030316NRSM8401T, 17101000599NPSM8301W, 17101000599NRKM8701O, 17101000599NMVM8601N, 17101000599NNPM8001K, 17101020316NLDM8201Z, 17101010316NLGM8601R, 17101000599NGRM8301I, 17101000599NGNM8901O, 17101020316NCTM8901F, 17101000599NDKM8501E, 17101000599NBSM8101M, 17101040316NBKM8501B, 17101000599NAPM8701Q, 17101040316NAPM8701L, 17101000599STKM8101J, 17101010316SSVM8401K, 17101000599SSGM8505Y, 17101020316SRSM8701M, 17101050316SNSM8301F, 17101000599SVDM8701W, 17101000599SVCM8301D, 17101040316SSAM7901I, 17101000599SSPM8405Y, 17101000599SRSM8301L, 17101050316SNWM7901U, 17101010316SRBM8602M, 17101050316SSNM8701L, 17101000599SSSF8901N, 17101010316SSDM8301N, 17101040316SRSM8201H, 17101000599RKAM8201C, 17101000599RMNM8601J, 17101000599RRPM8901V, 17101000599RSPF8801E, 17101000599RSDF9101O, 17101000599RDPF8901S, 17101000599RASF8801N, 17101000599RRPM8401A, 17101000599RGBM8701Y, 17101000599RSBM8702H, 17101000599RTPF9101D, 17101000599RSDF8801O, 17101000599RSGF8801F, 17101000599RRNF8701M, 17101000599RNPF9001K, 17101000599RGKF9101F, 17101000599SNKM8701J, 17101000599SPKM8702C, 17101000599RVMM8601D, 17101000599RSKM8901J, 17101000599RBNM9101S, 17101000599RTSF9101U, 17101000599RSGF8201L, 17101000599RSJF8801W, 17101000599RJAF8801G, 17101000599RATF8501N, 17101000599RKPF9102H, 17101000599RNPM8901Z, 17101000599RMTM8901O, 17101000599RDJF8601N, 17101000599RSSF9002R, 17101000599RSSF9001W, 17101000599SVWM8401U, 17101050316SSWM8701K, 17101000599SSYM8401R, 17101000599SRSM8303B, 17101000599SPTM8501I, 17101000599SSCM8601D, 17101000599SVGM8501P, 17101040316SSPM8301O, 17101000599SSLM7701I, 17101000599SNPM7901Z, 17101010454SSKM7901Q, 17101000454SRIF8701G, 17101000599SVMF8401H, 17101000457SNSF8801Q, 17101000457SRDF7901L, 17101000599SSGF8601A, 17101000599NPSM8501U, 17101010316NGGM8401Y, 17101000599NKYM8201K, 17101040316NGCM8501U, 17101010316NGCM8801G, 17101000599NDGM8801N, 17101040316NDBM8601Z, 17101000599NBNM8301Z, 17101000599NCKM8501F, 17101000599NBRM8301N, 17101000599NBNM8601W, 17101000454NMBF8801O, 17101000599NVSF8701V, 17101020316NSSM8501W, 17101000599NHSM8901Y, 17101000599NPSF8801A, 17101000599SRPM8302P, 17101000599SNPM8201Z, 17101040316SMTM8401H, 17101010316SPTM8301U, 17101040316STWM8601P, 17101000599SSNM8301Z, 17101040316SSGM8401O, 17101000599SSKM8201J, 17101010316SRBM8601R, 17101000454SRVM8801J, 17101040316SSZM8601H, 17101030316SNGM8601W, 17101000454SRKF8801Z, 17101050316SSBF8901C, 17101000599SVBM8501E, 17101000599SSAF8001Y, 17101000599SPGM7701A, 17101000599SSVM8601Y, 17101000599SRCM8401G, 17101050316STAM8701X, 17101000599SSMM7901D, 17101050316SSSM8401Z, 17101000599SSAM8001P, 17101000599SRWM8401Y, 17101050316SRSF8902Z, 17101000599SRTF8701N, 17101000454SPGM8501H, 17101010316STMM8401K, 17101050316SSGF8701P, 17101020316SSMM8901B, 17101020316SSSM8501N, 17101000599SVSF9001M, 17101000599SRRM8102Q, 17101050316SRBM8901U, 17101000599SNKM7201V, 17101010316SSKM8801N, 17101050316SNKF8901G, 17101040316SPGM8101U, 17101030316SVBF8701L, 17101000454SSPF8801J, 17101000599SSKM8402C, 17101040316SVKM8702R, 17101050316SNNM8601R, 17101000454SVMM8201M, 17101000455SSPF8601G, 17101050316SRSF8901E, 17101010316STPF8901F, 17101000454SPVM8501O, 17101000599SNKM7201V, 17101000599SNAM7501W, 17101050316SPDF8801A, 17101050316SRTF8701D, 17101000455SSPF8101L, 17101050316SSKM8301Y, 17101000599SNNM8501C, 17101040316SSSM8802V, 17101000599SVRM8602C, 17101010316SSKM8001V, 17101000456SRKM8601I, 17101050316SNZM8301K, 17101010316SRTM8501Q, 17101000454SNKF8401H, 17101020316STMM8401F, 17101050316SSKM8901S, 17101000599RRKM8801L, 17101000599RVPM8501V, 17101000599VHMM8801N, 17101000599VDDM8102U, 17101000599VDGM8601L, 17101000599VCBM8701A, 17101050316VBSM8601T, 17101000599VBJM8801C, 17101000599VBMM8401X, 17101000599VJMM8501O, 17101000599VDHM8301L, 17101000599VDJM8402Z, 17101000599VDDM8801S, 17101000599VABM8801B, 17101000599VAGM8202N, 17101000599VKCM8601Q, 17101020316VDPM8801N, 17101000599VBCM7901D, 17101000599SPBM8301M, 17101000599SPBM9001I, 17101000599SVRM8601H, 17101010316SPGM8501F, 17101000599SSPM8502M, 17101040316SVKM8701W, 17101000599SRSM8401K, 17101030316SVJM8801D, 17101050316SPMM8801Q, 17101010316SRTM8401R, 17101040316SZGM8501G, 17101000599SVMM8601W, 17101000599SSSM8201L, 17101000599SSRM8501L, 17101000599SSNM8202V, 17101000599SSGM8202Q, 17101050316SSPF8801N, 17101000599SVSM8501F, 17101000599STJM8401J, 17101030316SRGM8301V, 17101000599SPPM8801R, 17101050316SPTM8901U, 17101000455SNTM8001W, 17101050316SVPM8201H, 17101000599SVIM8601I, 17101010316SSBM8801O, 17101040316SRKM8501C, 17101050316SSRF8801H, 17101050316SSJM8701X, 17101000599SYRM8501F, 17101050316SVPM7901H, 17101000599SSKM8401H, 17101000454SSKM8501S, 17101050316SSSM8901U, 17101020316VBSM8801G, 17101050316VCCF8601X, 17101000599VGTM8401X, 17101030316SNKM8901H, 17101050316VGDM8501I, 17101000599VGMM8401S, 17101000599VGTM8801T, 17101000599VDPM8401M, 17101000599VDDM8401W, 17101050316VBKM8701Q, 17101040316VAMM8301U, 17101000599VBAM8101K, 17101000599VHMM8201T, 17101000599VDAM8101I, 17101000599VBSM7801I, 17101000599VBMM8701U, 17101000599SVMM8501X, 17101010454STSM8201R, 17101000599SRGM8501T, 17101000457SSDF8601G, 17101000599SRTF8701N, 17101050316SNKF8901G, 17101000599SYSM8701A, 17101040316SSZM8601H, 17101000599SRKM8401I, 17101000599SPKM8202H, 17101010316STMM8401K, 17101010316SRBM8602M, 17101000599TSPF8801Q, 17101050316SSBF8901C, 17101000599SSKM8402C, 17101000599SVBM8501E, 17101010316SSKM8801N, 17101000454SRVM8801J, 17101000454SPGM8501H, 17101040316SPGM8101U, 17101030316SNGM8601W, 17101030316SVBF8701L, 17101000454SRKF8801Z, 17101050316SSGF8701P, 17101000454SSPF8801J, 17101020316SSMM8901B, 17101000599SSJM8301L, 17101000599SVMM8501X, 17101010316SNKM8101Z, 17101000599STPF8602T, 17101000599SSSF8901N, 17101000599SRMF8801H, 17101000599RHKF8801E, 17101000599SDDM8403H, 17101000599SBMM8602L, 17101000599SBMF9104I, 17101000599SBSF9201E, 17101000599SCPF9002J, 17101000599SBCM8701T, 17101000599SBNF8901T, 17101000599SAMF9001Z, 17101000599SAMM9001Q, 17101000599SAPF8801P, 17101000599SDWM8901H, 17101000599SAVM8701P, 17101000599SGSF8901Z, 17101000599SDDF8601Y, 17101000599SAKM8402U, 17101000599SABM8201C, 17101010454STSM8201R, 17101030316SVHM8701K, 17101000599SPCM6801S, 17101000599SSPM8302O, 17101000599SRCM8602Z, 17101000599SNPM8401X, 17101000599SUTM8301F, 17101010316SRJM8301W, 17101000457SSCF8501K, 17101000599SSJF8801P, 17101000454SNFM8201P, 17101040316STSF8601K, 17101000599SSMF6801U, 17101000599SVKF8801J, 17101000599SSPF8702T, 17101010316SSHF9001G, 17101000599SCPF9001O, 17101000599SASM8902R, 17101000599SDKM9002O, 17101000599SDPM8204U, 17101000599SBSM8804H, 17101000599SBKF9101D, 17101000599SGSF9001B, 17101000599SDGM8701F, 17101000599SBNM8902F, 17101000599SAKF9002A, 17101000599SDWM8702E, 17101000599SBRM8501C, 17101000599SGRF8601F, 17101000599SAKF9101E, 17101000599SGMM8502H, 17101000599SBGM9101G, 17101000599NSKM8001U, 17101000599PMKM8101L, 17101000599SACM8702P, 17101000599SGNM8201M, 17101000599SCWF9101S, 17101000599SADF8802U, 17101000599SDNM8401N, 17101000599SANM8601O, 17101000599SBBF8602B, 17101000599SBJM8801X, 17101000599SAAM9001A, 17101000599SDSF9103T, 17101000599SAKF9001F, 17101000599SDSM8801U, 17101000599SBPM9101F, 17101000599SBBM8602S, 17101000599SDPM8502B, 17101000599SCGM8501I, 17101000599SRGM8202R, 17101000599STPF8601Y, 17101000599SSPM8702K, 17101000599SNMM8401G, 17101050316SSBF8701E, 17101040316SSPF8801S, 17101050316SRSF8901E, 17101010316SVGF8901E, 17101010316STPF8901F, 17101000599SPKM8401K, 17101000454SPVM8501O, 17101050316SVBM8601T, 17101030316SSGM8501S, 17101000599SSVF8401J, 17101000457SSDF8601G, 17101000599SNSM8401O, 17101000599SBMF9102S, 17101000599SBKM8701V, 17101000599SAMM8801P, 17101000599SDLF9102T, 17101000599SEPM8301H, 17101000599SAMM8302P, 17101000599SDPM8701E, 17101000599SGJM9001T, 17101000599SBDM8601R, 17101000599SDAM8801W, 17101000599SGSM8601T, 17101000599SATM8702Q, 17101000599SDZM8601B, 17101000599SBMM8902I, 17101000599SGPM8601C, 17101000599SBSM8701X, 17101000599SAPM8802B, 17101000599SBPM8001N, 17101000599SBMF9001Y, 17101000599SDSM8802P, 17101000599SBTM8501W, 17101000599SDTM8601T, 17101000599SGHF9101H, 17101000599SBYM8801E, 17101000599SBAM9001Z, 17101000599SAPM8402F, 17101000599SBJF9101G, 17101000599SGJM8101Z, 17101000599SDWM8601K, 17101000599SBLM8101Y, 17101000599SESM8201Z, 17101000599SDJM8601X, 17101000599SGSM9101R, 17101000599SVDF8401I, 17101000599SRTF8801M, 17101000454SMVF8801X, 17101020316SSRM8101U, 17101000599SSNF7601M, 17101000599SZCM8601W, 17101040316SSBM8701A, 17101000599SPGM8601U, 17101000599STCF8601L, 17101000599SRPF8701Z, 17101030316SSBM8601G, 17101040316SSKM8101F, 17101000599SMSM8501O, 17101000599SUJM8001M, 17101000454SRRM8801V, 17101000599SSSM8501I, 17101040316NUSM8301M, 17101000599NKKM8301Z, 17101000599SUKF8401O, 17101050316STSM8301Z, 17101020316SRSM8802G, 17101000599SPKF8801P, 17101050316SUAF8801E, 17101000456SSAF8901R, 17101000599SSKM8506H, 17101050316SRPM8701G, 17101040316SPIM8601J, 17101050316SSRM8901X, 17101030316SSDM8401C, 17101000599SSHM8801M, 17101000599SSJF8401T, 17101000457SRCM8001H, 17101000599SSKF8601O, 17101020316SRGM8701W, 17101000599SPAF8501W, 17101000599PDJF8401D, 17101000457NDVF8901Z, 17101000599NANF8701F, 17101000454SMVF8801X, 17101030316SVHM8701K, 17101000599SPKF8801P, 17101040316SSKM8101F, 17101000599SSKF8601O, 17101000599SRSF8502N, 17101000599SSKF8801M, 17101020316SSPM8401X, 17101000599SSJM8501J, 17101000599SRSM8402F, 17101000599SPYM8401U, 17101010454STSF9001V, 17101000599SNMF8401P, 17101000455SNSF8001I, 17101050316SNPF8901R, 17101010316SSMM8601J, 17101000599RBPF9001W, 17101000599RASF8901M, 17101000599RVKF8901P, 17101000599RBRF9001Q, 17101000599RSSF8601X, 17101000599RRVF8901M, 17101000599RMPF9001L, 17101000599RCPF9001V, 17101000599RANF9101C, 17101000599RATF9001L, 17101000599RVTF8501S, 17101000599RSSF8602S, 17101000599RSMF9001O, 17101000599RDDF9001E, 17101000599RBMF8501H, 17101000454SPGM8601G, 17101010316NKFM8701U, 17101010316SUCM8701K, 17101000599STPF8601Y, 17101050316SSBF8701E, 17101000599SSKF9001N, 17101050316SRSF9001G, 17101000599SSSF7901U, 17101030316SRBM8401J, 17101050316SVBM8601T, 17101040316SSBM8701A, 17101000599SPGM8601U, 17101000599SRPF8701Z, 17101030316SSBM8601G, 17101050316SSRM8901X, 17101010316SRJM8301W, 17101000457SRCM8001H, 17101020316SRGM8701W, 17101050316STJF8801E, 17101040316SSPF8801S, 17101010316SVGF8901E, 17101000599SRTF8801M, 17101020316SSRM8101U, 17101000599SSNF7601M, 17101030316SSGM8501S, 17101020316SRSM8802G, 17101050316SUAF8801E, 17101000599STNM8601V, 17101040316SPIM8601J, 17101000599SSSM8501I, 17101000455SSPF8701F, 17101010316SSHF9001G, 17101000455SPGF8801I, 17101040316SVDM8401U, 17101050316STSM8301Z, 17101000456SSAF8901R, 17101050316SRPM8701G, 17101030316SSDM8401C, 17101000454SRRM8801V, 17101000457SSCF8501K, 17101000599SSJF8801P, 17101000454SNFM8201P, 17101040316STSF8601K, 17101000599SVKF8801J, 17101000454SRHF8801I, 17101040316SPPM9001N, 17101000599SVMM8502S, 17101040316SSKM8901X, 17101000599SRKM8001M, 17101010316SPWM8701H, 17101040316SSSM8801A, 17101000599SVNM8201X, 17101000599SVPM8601N, 17101000599SSKM8101K, 17101030316SSBM8501H, 17101000599SSTM8201I, 17101000599SSSM8504T, 17101000599SSSF8804Z, 17101000599SKSM8801N, 17101020316URKM9001W, 17101000599UDAM8501L, 17101000454UEBM8401U, 17101030316URPM8201H, 17101000599UMKF8601G, 17101020316UHPM8601S, 17101030316TUBM8901U, 17101000599VAGM8601O, 17101000599SPSF8601T, 17101000599SSSF8501R, 17101040316URNM8901B, 17101000599UKKM8301C, 17101000599ULKM8501Z, 17101000599UGDF8901E, 17101000599UMBM8601Y, 17101010454UABF8401C, 17101010316USPM8801K, 17101000599TVBM8701V, 17101000599TZKM8401T, 17101000599SDKF8802W, 17101000599SABF9001G, 17101000599SCRM8701Z, 17101000599SACM9101T, 17101000599SDSF8901C, 17101000599SSPM8401S, 17101000599SSVM8602T, 17101000599SPSM8503B, 17101000599SNKM8401M, 17101000599SSTM8502A, 17101000599RRJM8201U, 17101000599URPM8601D, 17101000599UBGM7801Z, 17101000599UMNF9001W, 17101050316UANF9001Y, 17101000599USKM6901C, 17101000599VBMF8201I, 17101000599SDVF8801U, 17101000599SBPF8901N, 17101000599SBPF9001P, 17101000599SDDM8702J, 17101000599VBGM8601N, 17101000599SSAF8001Y, 17101040316SVKM8702R, 17101000599SADF8801Z, 17101000599SCDF8501A, 17101000599SBJM6601N, 17101000599SBBF9101E, 17101000599SBCF9101B, 17101000599SGMM8802E, 17101000599SBMM9001P, 17101000599SBPF9101O, 17101000599SEWM8701I, 17101000599SCRM9001Z, 17101000599SDWM8801I, 17101000599SBSM9001X, 17101000599SBGM8201M, 17101000599SAAF8501L, 17101000599SBNM8901K, 17101000599SDKM8803I, 17101000599SSKF8801M, 17101000599SDLM8501S, 17101000599SDPM9001E, 17101000599SDCM9001R, 17101000599SADM8802L, 17101000599SGSM8503K, 17101000599SBGM8801G, 17101000599SABM8901V, 17101000599SAWM8301Q, 17101000599SGGM8401F, 17101000599SDSM8503N, 17101000599SBTM8801T, 17101000599SAPM8403A, 17101000599SAJM8802T, 17101000599RDKM8301E, 17101000599RVBF9101R, 17101000599RSNF8301P, 17101000599SDWM8701J, 17101000599SAAM8501C, 17101000599SDBM8503M, 17101000599SBAM8701Z, 17101000599SBPM8703W, 17101000599SDSF9101D, 17101000599SDSM8502S, 17101000599SBBM8802Q, 17101000599SBGM9201F, 17101000599SAGM8801H, 17101000599RRKM8202M, 17101000599RAMM8901V, 17101000599RSJF8802R, 17101000599RSHF9001D, 17101000599RSRF8601A, 17101000599RPPF8501K, 17101000599STNM8601V, 17101000599RDPM8701L, 17101000599RRTF9101T, 17101000599RNMF8801S, 17101000599RKKF9001C, 17101000599RJDF8701Y, 17101000599RHMF9001Z, 17101000599RDBF9001K, 17101000599RBMF9201D, 17101000599RBPF8801V, 17101000599RAKF8501O, 17101000599RACF9101J, 17101000599RAPF9001X, 17101000599RNMF9001T, 17101000599RPBF8801X, 17101000599RRBF9101V, 17101000599RMAM9001V, 17101000599SECM8301U, 17101000599SGMM8701K, 17101000599SBSM8403Q, 17101000599SASM8602U, 17101000599SBJM8703O, 17101000599SASM8402W, 17101000599SDRM8901W, 17101000599SCSM8501Y, 17101000599SBMM9101O, 17101000599SASM8801X, 17101000599SEPM8401G, 17101000599SDHM8801B, 17101000599SBCM8601U, 17101000599SAKM8601X, 17101000599RBSM8502B, 17101000599RDMM8601V, 17101000455SSPF8701F, 17101000599SRSF8502N, 17101000599SNBM8101Q, 17101000599SPAF8501W, 17101000599SSNM8901T, 17101050316SPSM8501B, 17101050316TSSM8901N, 17101000599SDLF9101Y, 17101000599SBJM8502V, 17101000599SBBM8603N, 17101000599RSSF8902P, 17101000599RNIF8601G, 17101000599RVSM8901I, 17101000599RVMF8901J, 17101003264YSPM8601Q, 17101004710HJLM8601X, 17101001949AARM7501K, 17101001949PSYM7901S, 17101002419AKSF8101K, 17101002419AKSF8101K, 17101003264SSBF8501G, 17101005318OBTM6601H, 17101005318SRCM7001N, 17101005318SJKM8301N, 17101005318SVGM8501L, 17101001450SMSM8301P, 17101005318SVSM8301D, 17101005318VSTM8801D, 17101005318NTKM7701P, 17101005318PLPM6601C, 17101001865PBDM7701L, 17101001561AKBM7301K, 17101001811PGKF8701W, 17101004571SBCF7501D, 17101005318SDSF8501C, 17101000622RPPM7201O, 17101000255SMKF8001J, 17101001595DDBM7701P, 17101001595SBDF8201R, 17101005761MALF7301P, 17101004413NBSF7501Z, 17101004413AHKM8201R, 17101004413SSWF7601M, 17101004413GGSM7801F, 17101004413KSAM7901S, 17101004413SSTM7601M, 17101001825PNWM8501B, 17101001825MMAF8401V, 17101001825DBLM8601Z, 12201004142AVRM7901R, 17101001825NGGF8101F, 17101004431DYSM8501I, 17101003264SSCF8201G, 17101005600SDCM8401E, 17101004720SRNM8801F, 17101004720RKGM8801O, 17101004720SIFM8601O, 17101003452SSTF7501W, 17101004720VBKM7401U, 17101004720VPRM8501D, 17101004720VRSM8401Z, 17101004720YSBM8801Y, 17101002026VDRM8001L, 17101002026VDRM8001L, 17101002182PGPF8101N, 17101000599VSMM8101J, 17101003220AMKM7901H, 17101001863BRTM7801C, 17101001865SGSM6901X, 17101001859SUKM6901Q, 17101002141ASKF8201H, 17101002141CDLM6301J, 17101002141SDYM6701K, 17101002141KSSF7201Y, 17101000814DKGM7901Y, 17101000814VBRM8401C, 17101004600VRRF8601Q, 17101000599YSBM8101V, 17101002556SBUM7901J, 17101002556SVGM8101G, 17101002556SVGM8101G, 17101002556SBUM7901J, 17101002556JMHF7701H, 17101002556RCMF7801X, 17101002556JMHF7701H, 17101002556SNNF8001D, 17101002556MDSF7201T, 17101002556NBSM8101Z, 17101004821SVRM8201P, 17101004821VSHF8801E, 17101004821NAWM8201E, 17101002556JVDF8301H, 17101002556PMDF7501F, 15201002426SVGF8201Z, 17101002047GNMM7701G, 17101002047GNMM7701G, 17101002047GNMM7701G, 17101002047GNMM7701G, 17101002047GNMM7701G, 15101000921PVPM7501B, 17101002418NRWM8301Y, 17101001435NUAM8601W, 17101001715SBTM8301H, 17101001715MMPM8101A, 17101001715NAAF7901G, 17101001715SABF7002Y, 17101001712YESM7401M, 13501002658PMBF8201O, 17101002047GNMM7701G, 17101002047GNMM7701G, 17101001825AAWM8601O, 17101002196SRIM7501Z, 17101002141TVHM6801J, 17101004821BBKM8001V, 17101001874SPSF8001U, 17101004720DNJF8901E, 17101004720MDJM8701W, 17101004720RAKM8701N, 17101004720SASM8901G, 17101004720ABSM8501F, 17101004720HMSM8601W, 17101004720KSPM8301H, 17101002047VEKM7901S, 17101003220LVVF7401G, 17101003220UUDF7501X, 17101002213KSHM7901N, 17101003457YMDF8801Z, 17101001616SNGF7401T, 17101001616SPDF8201V, 17101001616VNVF7801B, 12311001517NDGM7801T, 17101000599VLMF8801S, 17101003698GSNM7801Q, 17101005643HLKF8101I, 17101005643NBPF8101N, 17101005643AKTF8401C, 17101000773MVPM8101Q, 15101000402SBPF8201L, 15101000402SSHM8101K, 17101010316SSPF7601Q, 17101001616NNPM8001P, 17101001616RPTM8501U, 17101004431KJPM8001O, 17101001570VBSM8001B, 17101002026SAJF8701J, 17101004710AARM8701K, 17101002379SSPM7301U, 17101002379BSVF8801O, 17101002379DRGM7901R, 17101002379SBSM8601S, 17101004571VKKM8301N, 11206002333RROF7801H, 17101003457YMDF8801Z, 17101004828AMZM7401S, 17101002553ANRF7701C, 17101004863SBRM8401L, 17101030316VLKM8801P, 17101050316VSKM8901X, 15201002426RBDF8601F, 17101002419SVKF8401Y, 13401002426SHBF8001A, 17101002551ABKM7801J, 17101002556JVDF8301H, 17101002556NBSM8101Z, 17101002556RCMF7801X, 17101002556PMDF7501F, 17101002556MDSF7201T, 17101002556SNNF8001D, 17101002379VSTF7901Q, 17101002379YRKF7901X, 17101002379YVPM8301U, 17101004600GPBM6901R, 17101004600SGPM7201E, 17101004600SSKM7801B, 17101000599PCPM8701A, 17101000599SBSM8801W, 17101003264PJSF7801P, 17101003264YDHM8501E, 17101004791CMMM6901I, 12201005511MDTM7701C, 17101035373TSDM7601O, 17101005701PNMM8201H, 17101005733MMPF7101S, 17101005810BKRM8501N, 17101005810PSJM7601P, 17101010036VSSM7701Z, 17101000599AHSM8701N, 17101020316DRNM8801B, 17101000599LVNM8601Q, 17101004863VVPF7401S, 17101001448MVSF7401A, 17101001479ARGF8501Y, 17101001479AYTF7701J, 15201002426SVNF8201E, 15201002426SVNF8201E, 17101002419SVKF8401Y, 17101002419SVKF8401Y, 15201002426SVGF8201Z, 17101003480MATF7701D, 17101003490MSKM7901Y, 17101003490SSSF8001V, 17101001050RBDM7801G, 17101001050RBPM7301B, 17101014453ACSF8301P, 17101005512VSIF8101C, 17101005512AADM8001S, 17101001435VRBF8101G, 17101001433MSRF8301C, 17101004821VJCM7801A, 17101001435VRBF8101G, 17101001433MSRF8301C, 17101001435VRBF8101G, 17101001433MSRF8301C, 17101004571AHVM8501Y, 17101000599VSDM8401H, 17101000599YPSM8701T, 11305022016VRKF7501A, 17101002058SSDF8101W, 17101003482SVCM7501A, 15101003860SVSF8401Z, 17101003490SLPM7601D, 17101003490USBF8601A, 17101002397SSKF8701L, 17101002656SSPF8201P, 17101002397UBHF7301I, 17101002397VNHM8701V, 17101002397UDPF7701E, 14601002657YBBM8201C, 12601002398MJDF8101W, 17101005712JBGM8101U, 17101005733MMPF7101S, 17101005733SRMF6901F, 17101005733JSSF6601A, 17101005733KASF5801Q, 17101002397PPTF7701P, 17101002397PTBM7801D, 17101002397RCNM8801P, 17101002397SDDF8201A, 17101003457KDTM7901D, 17101001620AGSF8201I, 17101001620NBMM7101R, 17101001620PPWF8101N, 17101001620SSMM7801K, 17101001620UNNM8401V, 12201002416SDGF7901D, 17101002418AVBF7401H, 17101003662RSBM8001P, 17101003662SAGM7801K, 17101003662YRSM8101T, 17101003662VMGM7501G, 17101002012ASGF8601U, 17101002012KKLM8401O, 17101003457AVJM7801I, 17101003457K.SM7701L, 13401002426SSBF8401L, 12406012398CBTF7802A, 13101002441MRTF8101L, 17101002377SKPF8201P, 17101002213KSHM7901N, 17101002301SGPM8401H, 17101002301GJJM8601A, 11401002554SSYM7501X, 17101002301GJJM8601A, 17101014453SATF7801U, 17101002377ARWF8401H, 12201002389MAKF7701D, 12401002426NNGF7101X, 12601002422SPMF7802X, 17101002012KKLM8401O, 17101002026MMRF8501R, 17101001825PBLM7201E, 17101055774GGPF8001S, 17101005774JPRF6801N, 17101025774SNBF6901P, 17101005774SVRF8501L, 17101004791ASPM6301N, 17101004791SKKM8001D, 17101004791KRPM8001L, 17101004791GBGM7601F, 17101004791SVRM7601Y, 17101004791MTSM7801L, 17101004791VKGF7901B, 17101001621NMSM8101C, 17101001621NMSM8101C, 17101001621NMSM8101C, 17101001621NMSM8101C, 17101002551ABKM7801J, 17101002551ABKM7801J, 17101002551ABKM7801J, 17101002551ABKM7801J, 11312000771SSLF7001Q, 11303000771ASCF8001M, 11305000771MAKM7401S, 11312000771MDSF7101Z, 17101003220SGPM8201C, 17101001889AVKF7701G, 17101001889MSKM7701U, 17101004431SSKM8001Q, 17101001078UGKF8001B, 17101002551ABKM7801J, 17101010316RSAM7801F, 17101010316NSKF7101T, 17101001434HSDM7601R, 17101001467PVPM8901S, 17101001467SKKM8101F, 17101002553NDMM8701Y, 17101004571PAMF7601U, 17101004571PAMM8501F, 17101004571PBGM8201Z, 17101004571MJMM7601X, 17101004571MMDM7401X, 17101002418AHYM8401O, 17101002418UFWM8001Q, 17101002418GPGM9101O, 17101002418GGMM8601H, 17101000599VSSM8802F, 17101004890PGWM7801V, 17101003292YNTF7801Y, 17101000599VSGM8701V, 17101040316YBGM8601N, 17101010316VVJM8901R, 17101000599VSTM8901G, 17101040316VPPM8401V, 17101001825PBLM7201E, 17101002418SPLF8601E, 17101002418STDM7601W, 11401002554NSVM7801M, 11401002554MRMM8301T, 17101002553SNBF8501X, 17101002553NDMM8701Y, 11310002554SVBF7701R, 17101002553HAYF7801W, 17101002553KYCF8401O, 15101000402PSLM8001U, 17101002141RDWM7401T, 17101002141ANUM8401X, 17101002141GSGM6601E, 17101002141DBKM8901N, 17101002141PSRF7301R, 17101002141SBTF8101C, 17101045318RSTF8401Y, 17101005315SRTM7501Y, 17101005315SSGM7301M, 17101005315VNGM7701S, 17101000796JSRF8201V, 12201002652JVPM7001B, 17101002047RVBF8801H, 17101002012STKM8501D, 17101002047AUWM8301I, 17101002047LWYM8001E, 17101002026VDRM8001L, 17101002026VVKM8801G, 17101003269SSGF7901V, 17101001616AVNM8101Z, 17101005645PUBF8301K, 17101005645SSDF8601I, 17101004821BBKM8001V, 17101003264SLSF8701M, 17101003264SMWF8001G, 17101003264SPVM8401T, 17101003264SSKF8701D, 17101003264SVKF8001H, 12601002970KBPF8001X, 17101004571UVKM7801L, 17101002026VBBM8401F, 17101002026APCM6501S, 17101002378JGSF7701S, 17101010316SSVF9001Q, 17101050316VSMF8801B, 17101000599VRSM7802N, 17101000599YPTM8801P, 17101000599VVSM8602E, 17101000599VTMM8101I, 17101050316YMKM8301O, 17101000599VSDM8901C, 17101000599VSMF8601N, 17101000599VVKM8902Z, 17101003540ASKM8701N, 17101003540SMKM8401A, 17101003540PDGF8701W, 17101000599VRPM8702Q, 17101040316VNKM8501L, 17101006005SSGM7801G, 17101006005VRPM7901K, 17101006005VVRM8101B, 17101006005SEBM8001K, 17101004791ASPM6301N, 17101004828HDPM7601G, 17101005341NWZM7501V, 17101005342SYGF7901P, 17101005342VVMF7501J, 17101035373TSDM7601O, 17101005373MPKM8001H, 17101005810DMKM7901V, 17101006005DDPM7001D, 17101006005MVNM8601T, 17101004730STCF8701R, 17101004730MASM7801B, 17101002343KSPM7301L, 17101002343PNSF8001D, 17101000622RPPM7201O, 17101004821APGM8101Z, 11201000450SBGM8701A, 17101002419MMKF8001B, 15201002426GMMF8201W, 15201002426GMMF8201W, 17101002419SVKF8401Y, 17101003452VTMF7601U, 17101003457RDAM8401J, 17101001715SBTM8301H, 16101004542SSPF6501N, 17101000599WASM8801V, 17101000599RJAM8101E, 17101000599YRDM8701K, 17101002419UVDF7101P, 17101002419SPCF7301K, 17101002419SSBF7801F, 17101002419VGCF7901S, 17101003452GPSM7901V, 17101003452K.EF7901H, 17101004821NCWM8101D, 15301004821PRBF8501M, 17101004821SGPM7001T, 17101004714NGKM7201E, 17101004714GRGF8201E, 17101004714MKPM7601O, 17101004714SNSM8401H, 17101002418AVBF7401H, 11304003482JBBM8201J, 17101002378PBMF8301W, 17101005315ASKM8601M, 17101005315ASKM8601M, 17101005315RBTM8101S, 17101005318CSPM8301X, 17101005318NTKM7701P, 17101005342SYGF7901P, 17101005342SASF7501H, 17101005342SYGF7901P, 17101005342VGGM8301C, 17101004720YSBM8801Y, 17101004724KSGM8501M, 17101004821APGM8101Z, 11501004836MRNM7901R, 17101004821SGPM7901K, 17101004821VDYM7901R, 17101004821VHPM8301N, 17101003457KDTM7901D, 17101003457AVAM7501M, 11201003556JSPM7201F, 17101001835AVDM8401X, 17101002553SGKF7601J, 17101002650RYMM8101V, 17101002650BNMM7701P, 16201002554AVKM8301W, 17101003457KDTM7901D, 17101001595RVCF8401F, 17101001591KVMF7501Y, 17101005751RGRF8401D, 17101002553JBCF8201U, 11501002554SPWM8001Z, 11301000622VJJF7401Z, 17101014453AAJM8201K, 12401003437SSMF7302A, 17101003603MDMF8701V, 17101002551ASRF7601I, 17101000599YDJM8801F, 17101005318SDSF8501C, 17101000599VSRM8201T, 17101021450SKGF8501Y, 17101005645PCRF8701C, 17101002396BVNM7201W, 12406012398SSJM8401T, 17101002396SSSM8101P, 17101002396SWGM7901U, 17101002396UEFF7801L, 17101002396VYYM7801W, 17101002396VDAF8101U, 17101000599VRPM8801U, 17101001625AASM7901G, 17101002182ARWM8101Z, 17101002182SKKM8001V, 17101002188KSWM8601X, 17101002182SSKF8801O, 17101002419AKSF8101K, 17101002026SATF7501O, 11402003421PBJF8501V, 17101003488RBCF7701E, 17101003488VYKM7601Z, 17101003488BRGF8201I, 17101003488MNMM7001V, 17101003488RNSF7901U, 17101003488RRCM7601G, 17101003488SMSM7101N, 17101002725MSWM8101P, 17101003457MKJM7601P, 17101003457MMLM8501B, 17101003457RDAM8401J, 17101003457KDTM7901D, 17101003457KDTM7901D, 17101003269ODSM8701O, 17101002971NPPM8101J, 17101002971STAM8101P, 17101002971TKBM8101O, 17101005810HRBM8501M, 17101005810MNRM7701Q, 17101000622RPPM7201O, 17101005751ARGM8701C, 17101005600SDCM8401E, 17101003662YAKF8301P, 17101003662AABF8101E, 14601005373SPKM7501D, 17101004830RSGM8101J, 17101005613ABJM7401W, 17101002418RDSM7401C, 17101002418RGSM7801V, 17101002418NRWM8301Y, 17101002418TSAM7301C, 17101004431ARDM8401E, 17101000599VSSF8501W, 17101000599VVMM8201F, 17101000599VMPM8201F, 17101000599VSSF8502R, 17101002418NMWF7601Q, 17101002553JBCF8201U, 17101002553SGKF7601J, 17101006005BSSM7801L, 17101010316YVWM8701L, 17101000599YLPM7001U, 17101000599PSPM8001R, 17101000599SPNF8601I, 17101030316VRPM8601W, 17101000599VNJF8801Z, 17101000599VTRF8901U, 17101000599VVAM8501M, 17101000454VVKM8401V, 17101050316VKVM8601B, 17101050316VPBM8701D, 17101000599VVSM8901G, 17101050316VLGM8601T, 17101030316YBJM8701I, 17101010316YSPM8401M, 15101000362SSGF8301D, 17101002397SDDM8301Q, 17101002397SGMM8301M, 17101002397SIRF7201M, 17101002397SKHF7901H, 17101002397SNSM7401T, 17101002397SRWF8001J, 17101002397SRMM7901C, 17101002397SSAF8001W, 17101002397SSAM8001N, 17101002397SRTM9201A, 17101002397SSJM8601G, 12301002426JBGF8401G, 17101002397ALCM8401G, 17101002397ANWM8301X, 17101002397LNSM7601O, 17101002397GASM7901H, 17101002047GNMM7701G, 17101001835URPF7701U, 17101002553HAYF7801W, 17101002553KYCF8401O, 15101000402PSLM8001U, 17101002553ANRF7701C, 11401002554NSVM7801M, 11401002554MRMM8301T, 17101002553SNBF8501X, 17101002553NDMM8701Y, 11310002554SVBF7701R, 17101002551NKPF7801H, 17101002551PDRF7801U, 17101002551PSSF8001D, 17101002551SKDF8001J, 17101002551SSBF7601I, 11214001443SSKM8701U, 17101000413AINM8801D, 17101001616BHRM8201T, 11201001492PDGF8501B, 17101001616PNNF8301N, 17101004863SBSM7701M, 17101004890SNUM8601U, 17101004863ASVF8601L, 17101004863HPKF8301B, 17101004863NBDM8501J, 17101004863PNGM8301C, 17101004863RMKF7801O, 17101004863SBDF8501J, 17101004863SPNF7801V, 15101000402AJDM8201A, 17101004453TPBM8001X, 17101002026DVJF8501R, 17101003264VKDF7601T, 17101003264MMKF7901E, 17101003264MVSF8201X, 17101003264NBCF8501D, 17101003264NMMF7701T, 17101003264NPRF8201Z, 17101003264SCPF8301I, 17101003264SCSF8201A, 16501002186SKGM7702W, 17101003264SKSM8301I, 17101003264RVCM7701D, 17101003264SAMM7301R, 17101003264SMBM7601J, 17101003264SRAF8901G, 17101003264SRKF7701L, 17101003452MRDM8401U, 17101003452NTGM8401C, 17101003452NVBM7501V, 17101003452SCBM8001D, 17101003452SSRF8601U, 17101003452AASM8801U, 17101003452KDMM8101Y, 17101003452NBYM7401Z, 17101003452VTMF7601U, 17101004821RSBF8001G, 17101000599RAIF8601T, 17101000599SPNF8601I, 17101000599PSPM8001R, 17101000599BGPM8201V, 17101000599YTAM8301V, 17101000599VPJM8301T, 17101002047RVBF8801H, 17101002058SNHF8901H, 17101002141GSGM6601E, 17101002141CDLM6301J, 17101002144AVMM6701J, 17101002144CMMF7301K, 17101002182NLPF7801W, 17101002196GSPF8401M, 17101001625KLPM7601P, 17101001625SMGM6801Q, 17101001625KLPM7601P, 17101001824VVPM7101O, 17101001825DBLM8601Z, 17101001835AVDM8401X, 13101002147RSSM7901T, 17101001824RNRM8001M, 17101005361SAMM7901L, 17101005361SMHM6801W, 17101005361VSBM7101N, 17101005361SASM7101B, 11201004821APWF8501V, 17101004821PSSF7301Z, 17101001835AVDM8401X, 17101005373YBMM7701J, 16201002554AVKM8301W, 17101005631MAJM8201Q, 17101004882FIKM7401S, 17101000599VVSM8601J, 15301000948RDLF8001Z, 17101004901KRJF8401H, 17101004901RHJF8501T, 17101004882GVDM7001X, 17101002419RDPF7601N, 17101002419SVKF8401Y, 17101002419KKPF8201A, 11201000450SBGM8701A, 17101001622PIJM6401Z, 17101002551ABKM7801J, 17101003264SSCF8201G, 17101003264SSCF8201G, 17101002419SVPF8401J, 17101004791SSDF8601T, 17101004791VPBM8801W, 17101004791APKM6801A, 17101004791PSDM8401H, 17101002419SSGF8201P, 17101004791NHWM7501H, 17101004791SAKF7501Y, 11304003482JBBM8201J, 17101003490NJSM8001E, 17101000773MVPM8101Q, 17101002012APPF7901A, 12501002590DDSM8201B, 15201002426GMMF8201W, 17101002419SVKF8401Y, 17101002419TNGF8101O, 15201002426RBDF8601F, 17101002371PRHM7501A, 17101000672MGAM7401I, 17101000672NDPM8401E, 17101000672SMCM7001K, 11301000362BDJF8401Z, 17101000672DDGM7601C, 17101000672MDBM7001M, 17101004615SDBM7501S, 17101000599VSDF8401Q, 17101004710VTTM8601V, 11201003456VAPM8401J, 17101004710YVSM8201F, 17101004710TPKF8302A, 11501002554SPWM8001Z, 17101004710RRAF8801L, 17101004710SBWF8501J, 17101004710SSSM7001H, 17101004710AARM8701K, 17101004710RBPM8101G, 17101004710DRMM8801M, 17101004710DSAM8701W, 17101004710HJLM8601X, 17101004710JSPM8601O, 14601000450NNJM8201Q, 17101010316AJRF7301S, 17101010316MMUF6901B, 17101010316SSDF6801F, 17101000599ABJM8602Q, 17101000599JHMM8401X, 17101000599PISM8101R, 17101020316RDGM8001Y, 17101000599BKTM8001H, 17101000599SSJF8501S, 17101000599AABM7901Y, 17101000599GDSM8001I, 17101002379VRKF8401Q, 17101002378PBMF8301W, 17101002419UVDF7101P, 17101002343SBJF7901T, 17101005318PSPM7001U, 17101005318ABBM7001C, 17101005318ASGM7701P, 17101005318JAGM8101V, 17101005318AAGM8501C, 17101005318CSPM8301X, 13302012398ULGF7801N, 17101002377VPPF8601L, 17101005318KFRM7501F, 17101005318NAKM8701B, 17101005318KSVF7201S, 13401002426VSRF7501A, 12406012398SSSF8501A, 13401002426STHF8501R, 12406012398SVSF8101B, 17101002377UKBF7601U, 17101004781AVCM7401S, 17101004781CKKM7201T, 17101004781MDNM8001U, 17101004781MSJF7501C, 17101002026SATF7501O, 17101003173BDMF7901J, 17101005315ASKM8601M, 11201005318MNRM6501D, 17101005315NJGM7901Y, 17101005315RBTM8101S, 17101002015DSKM7801U, 17101002015SOPF8401O, 17101002015SSPM8601Z, 17101005701PNMM8201H, 17101005701VARF7801Z, 17101005701RMRM8101G, 17101014453SATF7801U, 17101003698SSKF8201B, 17101003698VPMM7101C, 17101003698GRCM7401C, 17101003698KSTM7901V, 17101903696MSAM7101D, 17101003698PSSM7701R, 17101003698RANF7101Z, 12401003482RMAF8101C, 17101003698RVMM7301W, 11505003421SDRM8301L, 11401000584SHSM8201B, 17101001435VRBF8101G, 17101001433MSRF8301C, 17101002679SSMF8401B, 17101002679SSNF8101B, 17101005512SGDF8101Y, 17101005512SSSF8701N, 11407004476TSBF8101E, 17101004571GMPM8401W, 17101014453RNLM6401O, 17101002016SAKM7601I, 17101002016SPBM7801S, 17101002016BLPM7801V, 17101002016PHPM8001G, 17101002418BBSM8001J, 17101001917JSDF8101W, 11309002191SLPM8001H, 17101004571VSKM7301M, 17101004571VSNM8701S, 17101004571VVKM7601G, 17101004571VVNF7201K, 17101002725ARDM8401Y, 17101002725DNKM6701X, 17101002725KRHM7101E, 17101002725VUDM8401E, 17101001837RAGF8301U, 17101001837SGGM8201Z, 17101001837VMBM8701I, 17101004453GRNM8701R, 17101002378MVPF7701R, 17101002378MVPF7701R, 17101002378MVPF7701R, 17101002378MVPF7701R, 17101002377NDSF8301V, 17101002012SSPM8201S, 17101002419SPGF7801T, 17101002419PIRF7801O, 17101004791SAKF8101V, 17101004791SKPM7401R, 17101004791SPVM7801Q, 17101004791SRYF6901U, 17101004791TGPF8501P, 17101004791URRF7501Y, 17101004791BGLM7501V, 17101004791AVMF8301O, 17101004791MBTF7901I, 17101004791MCPF7801U, 17101004791MGJM7701A, 17101004791NRBM8301D, 17101004791RRBM7701E, 17101004791JAMM9001L, 17101004791JCBF8401C, 17101004791KBPM8201Z, 17101002182ADYM7501K, 17101002182ADYM7501K, 17101004431BUPM7801N, 17101003292DAFM8701D, 17101020316DAKF7901Q, 17101000599VSRM8201T, 17101000599VMPM7901F, 17101000599YSSM8501S, 17101002656NSKF6701W, 17101002397SVBF7801P, 17101004791RVBM6301L, 17101004791VTPM8001K, 17101004791CMMM6901I, 17101004791MPKM8101N, 17101004791SSMF6401I, 12604004476STKF8201Y, 17101002196GSPF8401M, 17101001561SGRM8001S, 17101001865KNHF8001F, 12501000622SNPM7901X, 17101000621SPCF8101C, 17101000620NGAM8601R, 11201000622SDPM8301Y, 11201000622NBSM8301A, 17101000621VSSM7501C, 17101002343SGGM8401M, 17101002343PSPF7601I, 16308000771GSDM8401M, 17101002343AAAF8201R, 17101002343KNKF7101Q, 17101002343SAKM9201B, 17101002343RNPF8201W, 17101000599VSRM8401R, 17101002556MHRM7601F, 17101002556MVKF8101T, 17101002556SBDM8501F, 17101025774SNBF6901P, 17101055774GGPF8001S, 17101005774JPRF6801N, 17101005774SVRF8501L, 17101002058SNHF8901H, 17101004828AMZM7401S, 17101002419MTJF8401T, 17101002378VDSF7301T, 17101004431RSPF7801Q, 17101003269SSGF7901V, 11504000956ARPF7701U, 17101000599YSBM8501R, 17101004453PVGM6101R, 17101004453SBWM7801G, 17101004453VASF7401L, 17101004453JDMF7301H, 17101004453MYBM7901J, 17101004453JDMF7301H, 17101004453RSVM7001H, 17101004453GMSM7501Q, 17101004453MCPM7501T, 17101004453PRRM8101A, 17101004453SJSM8301I, 17101004453SJTM8001I, 11201004523VVSF8101H, 17101004431VBTM7501N, 17101004431YDSM8401N, 17101004431MMSM7301S, 17101001949AARM7501K, 17101002012KKLM8401O, 17101001540PDVF6601G, 17101001561NSWM8601U, 17101001587BDSF7301K, 17101001616NNPM8001P, 17101001620SSMM7801K, 17101001620SSMM7801K, 17101001621MVPF7901R, 17101001621MVPF7901R, 11310003502DMBM8001K, 17101001622PIJM6401Z, 15301002970YRPF8101S, 17101000814VBRM8401C, 17101004600VRRF8601Q, 11314000771JSBF8301T, 17101004781SBCM8301K, 17101004781ASJF6601O, 17101004781NAKM8801R, 17101004781JCMM8601N, 17101004781SGDM8501A, 17101004791ARGM8001E, 17101004791DDPM8101V, 17101004791JCBF8401C, 17101004710JSPM8601O, 17101004710PKKM8901S, 17101004710RBPM8101G, 17101004710SSSM7001H, 11201003456VAPM8401J, 17101004710VTTM8601V, 17101004710YVSM8201F, 17101004710TPKF8302A, 17101004791KEMM8801Z, 17101004791KYTF8301Y, 17101004791APDM8701I, 17101004791ARGM8001E, 17101004791BSGF7701F, 17101004791DDPM8101V, 17101004791GCDM8801E, 17101004791VPSF8001O, 17101004791DPRM8301B, 17101004791SMBM8601W, 17101004791GDHM7701Z, 17101004791ADPM7801Q, 17101004791KBKM8501L, 17101004791KSMM8101S, 17101004791MBAM7701G, 17101004791SNPM7401O, 17101004710PKKM8901S, 17101004791TGPF8501P, 17101004791URRF7501Y, 17101004791VPBM8801W, 17101004791APKM6801A, 17101004791PSDM8401H, 17101004791KEMM8801Z, 17101004791NHWM7501H, 17101004791SAKF7501Y, 17101004791SAKF8101V, 17101004791SRYF6901U, 17101004791AVMF8301O, 17101004791BGLM7501V, 17101004791BSGF7701F, 17101004791DBJM8101P, 17101004791DDPM8101V, 17101004791SPVM7801Q, 17101004791SSDF8601T, 17101004791ADPM7801Q, 17101004791APDM8701I, 17101004791ARGM8001E, 17101004791MCPF7801U, 17101004791MGJM7701A, 17101004791NRBM8301D, 17101004791RRBM7701E, 17101004791SKPM7401R, 17101004791JAMM9001L, 17101004791JCBF8401C, 17101004791KBPM8201Z, 17101004791KYTF8301Y, 17101004791MBTF7901I, 17101004791VPPF6301I, 17101000599VYPM8401R, 17101003456ABDM8001G, 17101003452ABPM7601L, 17101003452GPSM7901V, 17101000599AABM7901Y, 17101000599PLKM7901L, 17101000599STRF8701R, 17101000599SBSM8003U, 17101000599NSGM8001G, 17101000599AGIF8501D, 17101000599RYMM7901E, 17101000599LBGF8101T, 17101000599ARYF8601V, 17101000599GDSM8001I, 17101000599DJMM7901N, 17101000599BRSM8001D, 17101002397MGMM6901K, 17101002397NJTM7601B, 17101002397MSSF7201P, 17101002397MUPF7301V, 13401002655NDSF8201G, 17101002397RRSF8001C, 17101002397APRF8501R, 17101002397GAMF7901I, 17101002397NMTF8101F, 17101002397NNKF8401C, 17101002397PAJM7701Z, 17101002397PSMM8301V, 17101002397SRLM8201F, 17101002397SSMF7501O, 17101002397UDBF8001U, 13401002655VHPF7801I, 15101000402SKMM8301B, 17101004714MKPM7601O, 17101003272SRHM8001S, 17101003272SBGM7801K, 17101003272SSDM8301A, 17101001889RBKF8201J, 17101001835SPVF7401V, 17101004710DRMM8801M, 17101004710DSAM8701W, 17101004710HJLM8601X, 17101004710RRAF8801L, 17101004710SBWF8501J, 17101000599VVPF9002V, 17101000599VSPF9001D, 17101000599VSPM8302T, 17101000599VRPF8801D, 17101000599SLRM8101W, 17101000599PBSM8001Z, 17101000599PBPF8601L, 17101000599PNRM8001Q, 17101000599SBNF8501X, 17101000599RSGM8402A, 17101000599SASF8702H, 17101000599SSJF8501S, 17101000599CBCM7901G, 17101000599SSTF8501O, 17101000599MBGM8101D, 17101000599BKNF8102H, 17101000599BBJM8001U, 17101000599JBDM8101H, 11401000362SVKF8401K, 17101000599LGGM8101F, 17101004791VVMM7701R, 17101004791SMBM8601W, 17101004791GDHM7701Z, 17101004791GCDM8801E, 17101004791RPSM7301L, 17101004791SHSF7101X, 17101004791AEGM8301O, 17101004791SNPM7401O, 17101004791UADM7301Y, 17101004791JCRF7801J, 17101004791KASM8801L, 17101004791KSMM8101S, 17101004791MBAM7701G, 17101004791PPGF8901F, 17101004791SRMM7601R, 17101004791VPSF8001O, 17101004791UADM7301Y, 17101004791SHSF7101X, 17101004791AEGM8301O, 17101004791SRMM7601R, 17101004791VPPF6301I, 17101004791VVMM7701R, 17101004791PVSF6601G, 17101004791RAHM8201B, 17101004791RPSM7301L, 17101004791SAKF6701D, 17101004791SDKM8701D, 17101004791NGBF8801S, 17101004791NKKM8001M, 17101004791NKSM8201M, 17101004791PKHM8201F, 17101004791PPGF8901F, 17101000599VPPM8001E, 12201002652USCM7701L, 17101005810BPSM7501M, 17101002343GSSM8001A, 17101001930AAGM8401H, 17101001622PIJM6401Z, 17101002419PIRF7801O, 17101002419SPGF7801T, 17101002419SPGF7801T, 17101003457AVAM7501M, 17101003457MMLM8501B, 17101003457AVJM7801I, 17101003457K.SM7701L, 17101003457MKJM7601P, 11201003919DBJM7101Y, 17101001621ACPM8201H, 17101004791DPRM8301B, 17101004791ATGM7401F, 17101004791CYBM7901W, 17101004791PTDM7701K, 17101004791PVSF6601G, 17101004791RAHM8201B, 17101004791SAKF6701D, 17101004791SDKM8701D, 17101004791KBKM8501L, 17101004791NGBF8801S, 17101004791NKKM8001M, 17101004791NKSM8201M, 17101004791PKHM8201F, 17101004791AATM8201G, 17101004791BNTM8501J, 17101004791DBLM8401G, 17101004791DLIM7401M, 17101004791DRPM8001I, 17101005342VVMF7501J, 17101005342SVSF7801J, 17101004791PDGM7801Q, 17101004791TSAM8401O, 17101004791SSMM8801H, 17101005342SYGF7901P, 17101005342MIPM7401Q, 17101005342PRMF7301F, 17101005342SASF7501H, 17101005342VGGM8301C, 17101004863SBRM8401L, 17101005763SMSM7801U, 17101004791VTPM8001K, 17101005342ABPM7601B, 17101003960VSHM7701C, 17101003480JVRF8401F, 17101003490MPKF7701M, 11401000584NPTM7801A, 17101003490NVBM7601S, 17101003490SGHM7501H, 17101014453SATF7801U, 17101004446SSPF7801H, 17101004714BJGM7901M, 17101003490ATBM7801F, 17101003480HBNF8601X, 17101002144AZGM7201V, 17101002144VVDF7101B, 15101000402GMMM8101H, 14801001350PBSF8101H, 11301001350SLKF7801I, 17101001917JSDF8101W, 11309002191SLPM8001H, 17101000599VRPF8401H, 17101000599VVDF8401N, 17101000599RJMF8601Y, 17101000599VVSF7401B, 17101000599VSCF7701X, 15101004542MSJF6401Z, 17101000621PSKM8501J, 17101000621MPDF7401T, 17101000621MRGM8201U, 17101000621MSDM8601Y, 17101000621VAKF7401C, 17101001837RAGF8301U, 17101001837SGGM8201Z, 17101001837SJSM8401K, 17101004453UVAF7801V, 17101004453VCGF8001Q, 17101004453VCKM6801B, 17101000599VMAM8201Y, 17101000599SSPM8403I, 17101001622HPPM8701N, 17101003490MSSF8501G, 17101004863AMSM8401T, 17101004863GKPM7401V, 17101004453SNKM8201D, 17101004453SPGF7701Y, 17101004453SPPM7901M, 17101004453SSGF8201T, 17101004453SSPM6801R, 17101004453SSSM7901A, 17101004453SSTF7301M, 17101003662MDJM8001P, 17101003662PVKM8501U, 17101014453SVRF7601H, 17101004830NPWM7501V, 17101004830RCGM8001A, 17101004830SSYF8101J, 17101004830VVKM8101S, 17101004090MRMF7101H, 17101002971AMAM7501V, 17101002971HRWM8201B, 17101002971HDNM8101R, 17101002971JUNM8401J, 17101002971KPPM7001M, 17101002971SKSF8001G, 17101002971SCKM8501Y, 17101002419ABSF8301R, 17101003264SSHF7501V, 17101003264SSNF8001B, 17101003264STCM8501T, 17101003264VASM8301X, 17101003264VDPF8901G, 17101003264CSGM8301S, 17101003264DJPM8401S, 17101003264DLPF7501F, 17101003264GRTF8601K, 17101003264IVKM8701J, 17101003264JPGM7701B, 17101003264KNGF7401I, 17101003264RSAM8101L, 17101003264RSNF7501K, 17101003264RSNF8501D, 17101003264MPZF7801J, 17101000599VPPM8502Z, 17101000599VMTM8601P, 13401002426SVSF8201L, 17101002377VPPF8601L, 17101001825DBLM8601Z, 17101001825AAWM8601O, 17101002196SRIM7501Z, 17101001825PNWM8501B, 17101001825MMAF8401V, 17101004431BUPM7801N, 17101002058RMSF8201P, 11401002016RRKF7901H, 12406012398SSSF8501A, 13401002426STHF8501R, 12406012398SVSF8101B, 17101002377UKBF7601U, 17101002397VNWM7401M, 17101002397YVKM8601K, 17101002397PLSF8501R, 17101000599VSBM8601L, 17101000599VMPM8601B, 17101000599YSGM8501C, 17101001353VBPM8001D, 17101001353DVGM8001G, 17101005553ARPM7401D, 17101005553MASM8201A, 17101002395ABTM8601Z, 17101002397ASBF8501K, 17101002397CSSF7301G, 17101002397MSKF8501D, 17101002397NAZM8201P, 17101002397ADAF8501C, 17101000672RDWM7901J, 17101000672SCBF8701S, 17101000672VNGM7601W, 17101002379DLSM7601Q, 17101002379DRSM6701Q, 17101002379JBVM8201Y, 17101002379KRWM7401D, 17101002379KTWF7701H, 17101002379PJSM8101K, 17101002379SBSF7101N, 17101002379THCM7801G, 17101000599VSDM8401H, 17101000599YPSM8701T, 17101000796BDNF8201A, 17101000783VAKM8301K, 17101000796VBRM8101Y, 17101003488PATF8501P, 17101000599VYJF8101V, 17101010316DSTM7901T, 17101001712VRKM8201N, 12401003905DBSM8001B, 11501003270VCGM7701X, 17101004590NAKM8301A, 17101000599VVLF8701M, 17101001837AAMF8301R, 17101001837AWDM7601R, 17101001837SJSM8401K, 17101001837SSSF8001O, 17101001837SSSM7901D, 17101001837KRMM8201A, 17101002378SDSF7601L, 17101002378SRSM7901L, 17101000599VSHM8501U, 17101000599VRKM8701K, 17101002396RRMM7601R, 17101002396RSPM7301K, 17101002396RYPF8001J, 17101002396PPGM7701Y, 17101002396RPGM7201P, 17101002396RSJF7701H, 17101002396ABSM7901B, 17101002396SKBF6801M, 17101002396SKTF8301B, 17101002396SPBM7501U, 17101002396KAJM7901L, 17101002396GYPM7201E, 17101002396JRRM7801E, 17101002396KRWF7601T, 17101000454VVKM8401V, 17101050316VMVM8501A, 17101002418CNNF8401K, 17101002418MRPM7401G, 17101002418PPMF7401F, 17101002418PSSF7601I, 17101002418RJWF8001Q, 17101002418RSRF6901B, 17101002418SJMM7701E, 17101002418SDCM7701O, 17101040316VRJM8801H, 17101050316VKVM8601B, 17101000599VLBM7901W, 17101000599VRKM8301O, 17101005645SLDF7601W, 17101040316VVBM8001J, 17101005701SCPF7501B, 17101004781ASKM8401Q, 17101004781PTKF6601J, 17101004781SBCM8301K, 17101000599VVGM8002U, 17101000599VSPM8301Y, 17101000599VPPM8502U, 17101000599VRJM8201S, 17101000599VRBM8402J, 17101000599YSKM8201T, 17101001561SJPM7901T, 17101002015BSKM7901H, 17101002015DHGM8101R, 17101014453SBKM7901K, 12201003947KKKF7501S, 17101004415PAPM7301E, 17101004863VVPF7401S, 12201005511MDTM7701C, 17101005361MDPM8101Q, 17101005361BDDF7301N, 17101005361SMMF7801J, 17101005361JSSM7601P, 17101005361RKNM8201F, 17101004440SMSM7901Y, 17101004440SPJF6801N, 17101004440SRKF7201H, 17101004440SSMF7701V, 17101004440VDUF7801Q, 17101004440VMRM8001I, 17101004431VSAF7501K, 17101004440RPPM7001U, 17101000454VMGM8801M, 17101001621JBWM7901C, 17101001621MVPF7901R, 17101001621RNSM7701A, 17101001621SDRM8001G, 17101005733JSSF6601A, 17101005733KASF5801Q, 17101005733MMPF7101S, 17101005733SRMF6901F, 17101000599YTAM8301V, 17101000599VPJM8301T, 17101004571SBCF7501D, 17101005631MAJM8201Q, 11312000771SSLF7001Q, 17101004830RSGM8101J, 11401003465MASM8001H, 17101001621RSGM7901D, 17101002551SSGF8201Q, 17101001859RRIF7801J, 14601001853MRTM7801Q, 17101001859ARCM7601J, 17101005763SMSM7801U, 15301002554KDPF8101Q, 17101002551ABWM8301X, 17101002551SSPM8501D, 17101002551PAPF7801D, 17101002551PBPM8301R, 17101002551RCTF8001C, 17101002551SNJF6401Y, 17101002551SNKF7601M, 17101003490VVKF8601P, 17101002551MMPF6601V, 17101000965MBHM8001A, 17101004821NCWM8101D, 17101004828BBIM6801Y, 17101014453SVRF7601H, 17101004571UKPM8601C, 17101004571VSKM7301M, 17101004571VVNF7401I, 17101004591AACM8201H, 17101004600GPBM6901R, 17101004600SSKM7801B, 17101004615SDBM7501S, 17101004710DSAM8701W, 17101004710VTTM8601V, 17101004720MDJM8701W, 17101004571PPHF7501V, 17101004571RBMM7201A, 17101004571RSSF8401R, 17101004791BNTM8501J, 17101004791CYBM7901W, 17101004791DBLM8401G, 17101004791DLIM7401M, 17101004791KASM8801L";
			String[] arrS=s.split(",");
			List dcpsIdForDeletion=null;
			dcpsIdForDeletion= new ArrayList();
			for (int i=0;i<arrS.length;i++)
			{
			
				dcpsIdForDeletion.add(arrS[i]);
				
			}
			
			
			if(dcpsIdForDeletion!=null && dcpsIdForDeletion.size()>0){
				
				gLogger.info("size dcpsIdForDeletion"+dcpsIdForDeletion.size());	
				List tempDcpsEmpId = new ArrayList();
				for(int i = 0; i<dcpsIdForDeletion.size(); i++){
					if(i==0 || i%5000!=0){
						tempDcpsEmpId.add(dcpsIdForDeletion.get(i));
					}

					else{
						tempDcpsEmpId.add(dcpsIdForDeletion.get(i));
						lObjInterestCalculationDAO.deleteMonthlyIntrstsForGivenEmpList(tempDcpsEmpId, 25l);
						tempDcpsEmpId = null;
						tempDcpsEmpId = new ArrayList();
					}
				}
				lObjInterestCalculationDAO.deleteMonthlyIntrstsForGivenEmpList(tempDcpsEmpId, 25l);
			}
			
		}
		catch (Exception e) {
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error in Interest Calculation " + e, e);
			return resObj;
		}

		return resObj;
	}
	public ResultObject calculateDCPSYearlyInterestBkp2018(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		Boolean lBlFlag = false;
		String listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc = null;
		String listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits = null;
		List dcpsEmpIdForSixPc = null;
		Long yearId = null;
		Long treasuryCode = null;
		String lStrFinYearStartDate = null;
		String lStrFinYearEndDate = null;
		SgvcFinYearMst lObjSgvcFinYearMst = null;
		SgvcFinYearMst lObjSgvcFinYearMstForVoucher = null;


		String lStrArrearsLimitDate = null;

		Integer NumberOfDaysInGivenYear = null;


		MstDcpsContributionMonthly mstDcpsContributionMonthlyForInsertion = null;
		List<MstDcpsContributionMonthly> lstMstContriMonth = new ArrayList<MstDcpsContributionMonthly>();

		// No of samples decided here so as to restrict the no of values pass into IN clause of HQL query
		try {

			
			Object[] obj;
		      Object[] objRates;
		      Double intRate;
		      String effectiveFrom;
		      String applicableTo;
		      Integer tempDaysBetween;
		      Integer daysBetween;
		      setSessionInfo(inputMap);

			HibernateTemplate hitStg = new HibernateTemplate(serv.getSessionFactory());
          
			InterestCalculationDAO lObjInterestCalculationDAO = new InterestCalculationDAOImpl(MstDcpsContributionYearly.class, serv.getSessionFactory());
			InterestCalculationDAO lObjInterestCalculationMonthlyDAO = new InterestCalculationDAOImpl(MstDcpsContributionMonthly.class, serv.getSessionFactory());

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(
					SgvcFinYearMst.class, serv.getSessionFactory());
			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class, serv.getSessionFactory());

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

			if (!StringUtility.getParameter("yearId", request).trim().equalsIgnoreCase("")) {
				yearId = Long.valueOf(StringUtility.getParameter("yearId",request).trim());
			
			}
			if (!StringUtility.getParameter("treasuryCode", request).trim().equalsIgnoreCase("")) {
				treasuryCode = Long.valueOf(StringUtility.getParameter("treasuryCode", request).trim());
				
			}

			String ddoCode = StringUtility.getParameter("ddoCode", request).trim();
			lObjSgvcFinYearMst = (SgvcFinYearMst) lObjDcpsCommonDAO.read(yearId);
			lStrFinYearStartDate = lObjSgvcFinYearMst.getFromDate().toString().trim();
			lStrFinYearEndDate = lObjSgvcFinYearMst.getToDate().toString().trim();

			String lStrFromDatePassed = StringUtility.getParameter("fromDate", request).trim();
			String lStrToDatePassed = StringUtility.getParameter("toDate", request).trim();

			if(!"".equals(lStrFromDatePassed))
			{
				lStrFromDatePassed = sdf2.format(sdf1.parse(lStrFromDatePassed));
			}
			if(!"".equals(lStrToDatePassed))
			{
				lStrToDatePassed = sdf2.format(sdf1.parse(lStrToDatePassed));
			}

			lStrArrearsLimitDate =  gObjRsrcBndle.getString("DCPS.ArrearsLimitDateForInterestCalc");
			lStrArrearsLimitDate = lStrArrearsLimitDate.trim().concat(lObjSgvcFinYearMst.getFinYearCode()).trim();
			Date lDateArrearsLimitDate = sdf1.parse(lStrArrearsLimitDate);

			Long previousFinYearId = lObjSgvcFinYearMst.getPrevFinYearId();

			GregorianCalendar grgcal = new GregorianCalendar();

			if (grgcal.isLeapYear(Integer.parseInt(lObjSgvcFinYearMst.getFinYearCode()) + 1)) {
				NumberOfDaysInGivenYear = 366;
			} else {
				NumberOfDaysInGivenYear = 365;
			}

			String lStrInterestFor = StringUtility.getParameter("interestFor",request).trim();

			String totalDcpsEmpIds = null;

			if(lStrInterestFor.equals("Treasury") || lStrInterestFor.equals("DDO"))
			{
				// Gets all DCPS_EMP_IDs which are eligible for Regular Contri
	          gLogger.info("Inside First if of treasury and ddo**********");		
				listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc = lObjInterestCalculationDAO.getAllDCPSEmployeesForIntrstCalc(treasuryCode,ddoCode,lStrFromDatePassed, lStrToDatePassed);
				//listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc = lObjInterestCalculationDAO.getEmpListForRegularConti(treasuryCode,ddoCode,lStrFromDatePassed, lStrToDatePassed);

				// Gets all DCPS_EMP_IDs which are eligible for Missing Credits
				listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits = lObjInterestCalculationDAO.getAllDCPSEmployeesForIntrstCalcForMissingCredits(treasuryCode,ddoCode,lStrFromDatePassed, lStrToDatePassed,listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc);

				if(listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits!=null && !listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits.equals("") && listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc!=null && !listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc.equals("")){
					totalDcpsEmpIds = listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc +","+listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits;
				}
				else if (listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits!=null && !listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits.equals("") && (listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc==null || listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc.equals("") ))
				{
					totalDcpsEmpIds = listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits;
				}

				else{
					totalDcpsEmpIds = listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc;
				}

			}
			
			else if(lStrInterestFor.equals("Emp"))
			{
				// Added by ashish to display only eligible employee list
				String lStrEmpIds = StringUtility.getParameter("empIds", request).trim().replaceAll("~", ",");

				totalDcpsEmpIds = lObjInterestCalculationDAO.checkEmployeeEligibleForIntrstCalc(lStrEmpIds, lStrFromDatePassed, lStrToDatePassed,yearId);

				lStrEmpIds = null;
			}
			 gLogger.info("Total Employee count is ****"+totalDcpsEmpIds);
			List empDataForInterestCalcRegular = null;

			List empDataForInterestCalcMissingCredit = null;

			List dcpsIdForDeletion = new ArrayList();

			if(totalDcpsEmpIds!=null && !totalDcpsEmpIds.equals("")){
				if(lStrInterestFor.equals("Treasury") || lStrInterestFor.equals("DDO")){
					  
					//empDataForInterestCalcRegular = lObjInterestCalculationDAO.getEmpListForRegularConti(listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc,lStrFromDatePassed, lStrToDatePassed,yearId.toString());
					empDataForInterestCalcRegular = lObjInterestCalculationDAO.getEmpListForRegularConti(totalDcpsEmpIds,lStrFromDatePassed, lStrToDatePassed,yearId.toString());
					  gLogger.info("got employees for regular"+empDataForInterestCalcRegular);		
					//if(listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits!=null && !listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits.equals("")){
						//empDataForInterestCalcMissingCredit = lObjInterestCalculationDAO.getEmpListForMissingCredit(listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits,lStrFromDatePassed, lStrToDatePassed,yearId.toString());
						empDataForInterestCalcMissingCredit = lObjInterestCalculationDAO.getEmpListForMissingCredit(totalDcpsEmpIds,lStrFromDatePassed, lStrToDatePassed,yearId.toString());
						 gLogger.info("got employees for missing credit"+empDataForInterestCalcMissingCredit);	
					//}
				}

				else if(lStrInterestFor.equals("Emp"))
				{

					empDataForInterestCalcRegular = lObjInterestCalculationDAO.getEmpListForRegularConti(totalDcpsEmpIds,lStrFromDatePassed, lStrToDatePassed,yearId.toString());
					empDataForInterestCalcMissingCredit = lObjInterestCalculationDAO.getEmpListForMissingCredit(totalDcpsEmpIds,lStrFromDatePassed, lStrToDatePassed,yearId.toString());
				}
			}

			if(totalDcpsEmpIds!=null && !totalDcpsEmpIds.equals("")){
				dcpsEmpIdForSixPc = lObjInterestCalculationDAO.getArrearsDtlsForAllEmps(totalDcpsEmpIds,yearId);
			}

			//Long dcpsContributionMonthlyId = IFMSCommonServiceImpl.getNextSeqNum("mst_dcps_contribution_monthly",inputMap);

			Long dcpsContributionMonthlyId = lObjInterestCalculationDAO.getNextSeqNum("MST_DCPS_CONTRIBUTION_MONTHLY");
			Long regularSize = 0l;
			Long missingCreditSize = 0l;
			Long sixPcSize = 0l;

			if(empDataForInterestCalcRegular != null){
				regularSize = Long.valueOf(empDataForInterestCalcRegular.size());
			}

			if(empDataForInterestCalcMissingCredit != null){
				missingCreditSize = Long.valueOf(empDataForInterestCalcMissingCredit.size());
			}

			if(dcpsEmpIdForSixPc != null){
				sixPcSize = Long.valueOf(dcpsEmpIdForSixPc.size());
			}

			
			Long dcpsContributionMonthlyIdForUpdate = dcpsContributionMonthlyId + regularSize+missingCreditSize+sixPcSize+1;

			//lObjInterestCalculationDAO.updateGeneratedId(Long.valueOf(dcpsContributionMonthlyIdForUpdate.toString().substring(6, dcpsContributionMonthlyIdForUpdate.toString().length())),"MST_DCPS_CONTRIBUTION_MONTHLY");
			lObjInterestCalculationDAO.updateGeneratedId(Long.valueOf(dcpsContributionMonthlyIdForUpdate.toString()),"MST_DCPS_CONTRIBUTION_MONTHLY");
			listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc = null;
			listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits = null;

			String voucherDate = null;
			int countMonth=0;
			Double contribution = null;
			String typeOfPayment = null;
			Long monthId = 0l;
			Long finYearCode = 0l;
			Long dcpsContriId = 0l;
			String voucherNo = null;
			Long voucherMonth = 0l;
			Long voucherYear = 0l;
			Long dcpsEmpId = null;
			String dcpsId = null;
			String contriDDOCode = null;
			String contriTresuryCode = null;
			String currDDOCode = null;
			String currTreasuryCode = null;
			//Long mstContriYearlyId = null;
			Double yearlyAmtSixpc=0d;
			Character isMissingCredit = null;
			Character isChalan = null;
			List interestRatesForVoucherNo = null;
			List interestRatesForMissingCredit=null;
			Iterator it = null;
			Iterator itr = null;
			Iterator iter = null;
			Double interestForMonthlyContri = 0d;
			String payCommision=null;
			Double openEmployeeInt=0d; 
			Double  openEmployerInt=0d;
			Double closeEmpPrv=0d; 
			Double  closeEmplrPrv=0d;
			Double  closeTier2Prv=0d;
			List lstDcpsContriId = new ArrayList();
			// Calculation of interest for Regular Month Contributions
			if(empDataForInterestCalcRegular!=null && empDataForInterestCalcRegular.size()>0){
				it = empDataForInterestCalcRegular.iterator();
				interestRatesForVoucherNo =  lObjInterestCalculationDAO.getInterestRatesForVoucherNo(yearId);

				while(it.hasNext()){
					obj = (Object[]) it.next();
					voucherDate = obj[0]!=null ? obj[0].toString():"";
					contribution = obj[1]!=null ? Double.parseDouble(obj[1].toString()):0d;
					typeOfPayment = obj[2]!=null ? obj[2].toString():"-";
					monthId = obj[3]!=null ? Long.parseLong(obj[3].toString()):0;
					finYearCode = obj[4]!=null ? Long.parseLong(obj[4].toString()):0;
					dcpsContriId = obj[5]!=null ? Long.parseLong(obj[5].toString()):0l;
					voucherNo = obj[6]!=null ? obj[6].toString():"-";
					voucherMonth = obj[7]!=null ? Long.parseLong(obj[7].toString()):0;
					voucherYear = obj[8]!=null ? Long.parseLong(obj[8].toString()):0;
					dcpsEmpId = obj[9]!=null ? Long.parseLong(obj[9].toString()):0l;
					dcpsId = obj[10]!=null ? obj[10].toString():"-";
					contriDDOCode = obj[11]!=null ? obj[11].toString():"-";
					contriTresuryCode = obj[12]!=null ? obj[12].toString():"-";
					currDDOCode = obj[13]!=null && !obj[13].equals("")? obj[13].toString():"-";
					closeEmpPrv = obj[16]!=null && !obj[16].equals("")? Double.parseDouble(obj[16].toString()):0d;
					closeEmplrPrv = obj[17]!=null && !obj[17].equals("")? Double.parseDouble(obj[17].toString()):0d;
					closeTier2Prv = obj[19]!=null && !obj[19].equals("")? Double.parseDouble(obj[19].toString()):0d;
					lstDcpsContriId.add(dcpsContriId);
					currTreasuryCode = obj[14]!=null && !obj[14].equals("")? obj[14].toString():"0";
					//	mstContriYearlyId = obj[15]!=null ? Long.parseLong(obj[15].toString()):0l;

					itr = interestRatesForVoucherNo.iterator();
					while(itr.hasNext()){
						objRates = (Object[])itr.next();
						intRate = objRates[0]!=null ? Double.parseDouble(objRates[0].toString()) : 0d;
						effectiveFrom = objRates[1]!=null ? objRates[1].toString() : "";
						applicableTo  = objRates[2]!=null ? objRates[2].toString() : "";

						tempDaysBetween = daysBetween(sdf2.parse(voucherDate), sdf2.parse(effectiveFrom));

						if(tempDaysBetween!=0){
							voucherDate = effectiveFrom;
						}

						if(applicableTo!=null && !applicableTo.equals("")){
							daysBetween = daysBetween(sdf2.parse(voucherDate), sdf2.parse(applicableTo)); 
							interestForMonthlyContri = interestForMonthlyContri + ((contribution*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);
							daysBetween = null;
						}

						else{
							daysBetween = daysBetween(sdf2.parse(voucherDate), sdf2.parse(lStrToDatePassed)); 

							interestForMonthlyContri = interestForMonthlyContri + ((contribution*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);

							daysBetween = null;
						}
						
						objRates = null;
						intRate = null;
						effectiveFrom = null;
						applicableTo = null;
						tempDaysBetween = null;
					}

					mstDcpsContributionMonthlyForInsertion = new MstDcpsContributionMonthly();
					mstDcpsContributionMonthlyForInsertion.setDcpsContributionMonthlyId(dcpsContributionMonthlyId);
					mstDcpsContributionMonthlyForInsertion.setDcpsId(dcpsId);
					
					mstDcpsContributionMonthlyForInsertion.setYearId(yearId);
				
					mstDcpsContributionMonthlyForInsertion.setCurTreasuryCD(Long.parseLong(currTreasuryCode));
					
					mstDcpsContributionMonthlyForInsertion.setCurDdoCD(currDDOCode);
				
					mstDcpsContributionMonthlyForInsertion.setvMonth(voucherMonth);
					
					mstDcpsContributionMonthlyForInsertion.setvYear(voucherYear);
				
					mstDcpsContributionMonthlyForInsertion.setPayYear(finYearCode);
					
					mstDcpsContributionMonthlyForInsertion.setPayMonth(monthId);
				
					mstDcpsContributionMonthlyForInsertion.setVorcNo(voucherNo);
				
					mstDcpsContributionMonthlyForInsertion.setVorcDate(sdf.parse(voucherDate));
				
					mstDcpsContributionMonthlyForInsertion.setContribEmp(Round(contribution,2));
				
					mstDcpsContributionMonthlyForInsertion.setContribEmplr(Round(contribution,2));
					mstDcpsContributionMonthlyForInsertion.setContribTier2(0d);
					mstDcpsContributionMonthlyForInsertion.setIntContribEmp(Round(interestForMonthlyContri,2));
					mstDcpsContributionMonthlyForInsertion.setIntContribEmplr(Round(interestForMonthlyContri,2));
					mstDcpsContributionMonthlyForInsertion.setIntContribTier2(0d);
					mstDcpsContributionMonthlyForInsertion.setCreatedPostId(1l);
					mstDcpsContributionMonthlyForInsertion.setCreatedUserId(1l);
					mstDcpsContributionMonthlyForInsertion.setCreatedDate(DBUtility.getCurrentDateFromDB());
					mstDcpsContributionMonthlyForInsertion.setUpdatedPostId(null);
					mstDcpsContributionMonthlyForInsertion.setUpdatedUserId(null);
					mstDcpsContributionMonthlyForInsertion.setUpdatedDate(null);
					mstDcpsContributionMonthlyForInsertion.setTypeOfPayment(typeOfPayment);
					mstDcpsContributionMonthlyForInsertion.setIsMissingCredit(null);
					mstDcpsContributionMonthlyForInsertion.setIsChallan(null);
					mstDcpsContributionMonthlyForInsertion.setCloseEmpPrv(closeEmpPrv);
					mstDcpsContributionMonthlyForInsertion.setCloseEmplrPrv(closeEmplrPrv);
					mstDcpsContributionMonthlyForInsertion.setCloseTier2Prv(closeTier2Prv);
					lstMstContriMonth.add(mstDcpsContributionMonthlyForInsertion);

					mstDcpsContributionMonthlyForInsertion = null;

					++dcpsContributionMonthlyId;
					++countMonth;

					dcpsIdForDeletion.add(dcpsId);

					interestForMonthlyContri = 0d;
					obj = null;
					
					
				}
			}
			
			gLogger.info("VO created");	
			it = null;
			itr = null;
			empDataForInterestCalcRegular = null;

			// Calculation of interest for Regular Month Contributions
			if(empDataForInterestCalcMissingCredit!=null){
				it = empDataForInterestCalcMissingCredit.iterator();

				while(it.hasNext()){
					obj = (Object[]) it.next();
					voucherDate = obj[0]!=null ? obj[0].toString():"";
					contribution = obj[1]!=null ? Double.parseDouble(obj[1].toString()):0d;
					typeOfPayment = obj[2]!=null ? obj[2].toString():"-";
					monthId = obj[3]!=null ? Long.parseLong(obj[3].toString()):0;
					finYearCode = obj[4]!=null ? Long.parseLong(obj[4].toString()):0;
					dcpsContriId = obj[5]!=null ? Long.parseLong(obj[5].toString()):0l;
					voucherNo = obj[6]!=null ? obj[6].toString():"-";
					voucherMonth = obj[7]!=null ? Long.parseLong(obj[7].toString()):0;
					voucherYear = obj[8]!=null ? Long.parseLong(obj[8].toString()):0;
					dcpsEmpId = obj[9]!=null ? Long.parseLong(obj[9].toString()):0l;
					dcpsId = obj[10]!=null ? obj[10].toString():"-";
					contriDDOCode = obj[11]!=null ? obj[11].toString():"-";
					contriTresuryCode = obj[12]!=null ? obj[12].toString():"-";
					currDDOCode = obj[13]!=null && !obj[13].equals("")? obj[13].toString():"-";
					currTreasuryCode = obj[14]!=null && !obj[14].equals("")? obj[14].toString():"0";
					//mstContriYearlyId = obj[15]!=null ? Long.parseLong(obj[15].toString()):0l;
					isMissingCredit = obj[15]!=null && obj[15].toString().equals("Y") ? 'Y':null;
					
					isChalan = obj[16]!=null && obj[16].toString().equals("Y") ? 'Y':null;
					closeEmpPrv = obj[18]!=null && !obj[18].equals("")? Double.parseDouble(obj[18].toString()):0d;
					closeEmplrPrv = obj[19]!=null && !obj[18].equals("")? Double.parseDouble(obj[19].toString()):0d;
					closeTier2Prv = obj[21]!=null && !obj[21].equals("")? Double.parseDouble(obj[21].toString()):0d;

					interestRatesForMissingCredit =  lObjInterestCalculationDAO.getInterestRatesForMissingCredit(lStrToDatePassed,voucherDate,yearId);

					itr = interestRatesForMissingCredit.iterator();

					lstDcpsContriId.add(dcpsContriId);

					while(itr.hasNext()){
					
						objRates = (Object[])itr.next();
						intRate = objRates[0]!=null ? Double.parseDouble(objRates[0].toString()) : 0d;
						effectiveFrom = objRates[1]!=null ? objRates[1].toString() : "";
						applicableTo  = objRates[2]!=null ? objRates[2].toString() : "";
						Long finYearId = objRates[3]!=null ? Long.parseLong(objRates[3].toString()) : 0l;
						lObjSgvcFinYearMst = (SgvcFinYearMst) lObjDcpsCommonDAO.read(finYearId);

						lStrFinYearStartDate = lObjSgvcFinYearMst.getFromDate().toString().trim();
						lStrFinYearEndDate = lObjSgvcFinYearMst.getToDate().toString().trim();
						grgcal = new GregorianCalendar();

						if (grgcal.isLeapYear(Integer.parseInt(lObjSgvcFinYearMst.getFinYearCode()) + 1)) {
							NumberOfDaysInGivenYear = 366;
						} else {
							NumberOfDaysInGivenYear = 365;
						}

						 tempDaysBetween = daysBetween(sdf2.parse(voucherDate), sdf2.parse(effectiveFrom));

						if(tempDaysBetween!=0){
							voucherDate = effectiveFrom;
						}

						if(applicableTo!=null && !applicableTo.equals("")){
							 daysBetween = daysBetween(sdf2.parse(voucherDate), sdf2.parse(applicableTo)); 

							interestForMonthlyContri = interestForMonthlyContri + ((contribution*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);

							daysBetween = null;
						}

						else{
							 daysBetween = daysBetween(sdf2.parse(voucherDate), sdf2.parse(lStrToDatePassed)); 

							interestForMonthlyContri = interestForMonthlyContri + ((contribution*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);

							daysBetween = null;
						}

						intRate = null;
						effectiveFrom = null;
						applicableTo = null;
						finYearId = null;
						lObjSgvcFinYearMst = null;
						lStrFinYearStartDate = null;
						lStrFinYearEndDate = null;
						grgcal = null;
						tempDaysBetween = null;
						objRates = null;
					}


					interestRatesForMissingCredit = null;
					itr = null;

					mstDcpsContributionMonthlyForInsertion = new MstDcpsContributionMonthly();
					mstDcpsContributionMonthlyForInsertion.setDcpsContributionMonthlyId(dcpsContributionMonthlyId);
					mstDcpsContributionMonthlyForInsertion.setDcpsId(dcpsId);
					mstDcpsContributionMonthlyForInsertion.setYearId(yearId);
					mstDcpsContributionMonthlyForInsertion.setCurTreasuryCD(Long.parseLong(currTreasuryCode));
					mstDcpsContributionMonthlyForInsertion.setCurDdoCD(currDDOCode);
					mstDcpsContributionMonthlyForInsertion.setvMonth(voucherMonth);
					mstDcpsContributionMonthlyForInsertion.setvYear(voucherYear);
					mstDcpsContributionMonthlyForInsertion.setPayYear(finYearCode);
					mstDcpsContributionMonthlyForInsertion.setPayMonth(monthId);
					mstDcpsContributionMonthlyForInsertion.setVorcNo(voucherNo);
					mstDcpsContributionMonthlyForInsertion.setVorcDate(sdf.parse(voucherDate));
					mstDcpsContributionMonthlyForInsertion.setContribEmp(Round(contribution,2));
					mstDcpsContributionMonthlyForInsertion.setContribEmplr(Round(contribution,2));
					mstDcpsContributionMonthlyForInsertion.setContribTier2(0d);
					mstDcpsContributionMonthlyForInsertion.setIntContribEmp(Round(interestForMonthlyContri,2));
					mstDcpsContributionMonthlyForInsertion.setIntContribEmplr(Round(interestForMonthlyContri,2));
					mstDcpsContributionMonthlyForInsertion.setIntContribTier2(0d);
					mstDcpsContributionMonthlyForInsertion.setCreatedPostId(1l);
					mstDcpsContributionMonthlyForInsertion.setCreatedUserId(1l);
					mstDcpsContributionMonthlyForInsertion.setCreatedDate(DBUtility.getCurrentDateFromDB());
					mstDcpsContributionMonthlyForInsertion.setUpdatedPostId(gLngPostId);
					mstDcpsContributionMonthlyForInsertion.setUpdatedUserId(gLngUserId);
					mstDcpsContributionMonthlyForInsertion.setUpdatedDate(gDtCurDate);
					mstDcpsContributionMonthlyForInsertion.setTypeOfPayment(typeOfPayment);
					
					mstDcpsContributionMonthlyForInsertion.setIsMissingCredit(isMissingCredit);
				
					mstDcpsContributionMonthlyForInsertion.setIsChallan(isChalan);
				
					mstDcpsContributionMonthlyForInsertion.setCloseEmpPrv(closeEmpPrv);
					
					mstDcpsContributionMonthlyForInsertion.setCloseEmplrPrv(closeEmplrPrv);
					
					mstDcpsContributionMonthlyForInsertion.setCloseTier2Prv(closeTier2Prv);
				
					lstMstContriMonth.add(mstDcpsContributionMonthlyForInsertion);

					mstDcpsContributionMonthlyForInsertion = null;
					obj = null;

					interestForMonthlyContri = 0d;

					++dcpsContributionMonthlyId;
					++countMonth;
					dcpsIdForDeletion.add(dcpsId);
				}
			}
			
			gLogger.info("VO created for missing credit");	

			it = null;
			empDataForInterestCalcMissingCredit = null;
			

			// Gets all DCPS_EMP_IDs which are eligible for Six PC Arrrears
			if(totalDcpsEmpIds!=null && !totalDcpsEmpIds.equals("")){

				it = dcpsEmpIdForSixPc.iterator();
				interestRatesForVoucherNo =  lObjInterestCalculationDAO.getInterestRatesForVoucherNo(yearId);

				while(it.hasNext()){
					Object[] objSixpc = (Object[])it.next();
					yearlyAmtSixpc = objSixpc[0]!=null ? Double.parseDouble(objSixpc[0].toString()):0d;
					dcpsEmpId = objSixpc[1]!=null ? Long.parseLong(objSixpc[1].toString()):0l;
					voucherNo = objSixpc[2]!=null ? objSixpc[2].toString():"-";
					voucherDate = objSixpc[3]!=null ? objSixpc[3].toString():"";
					finYearCode = objSixpc[4]!=null ? Long.parseLong(objSixpc[4].toString()):0l;
					payCommision = objSixpc[5]!=null ?  objSixpc[5].toString():"-";
					dcpsId = objSixpc[6]!=null ?  objSixpc[6].toString():"-";

					itr = interestRatesForVoucherNo.iterator();
					while(itr.hasNext()){
					    objRates = (Object[])itr.next();
						 intRate = objRates[0]!=null ? Double.parseDouble(objRates[0].toString()) : 0d;
						 effectiveFrom = objRates[1]!=null ? objRates[1].toString() : "";
						 applicableTo  = objRates[2]!=null ? objRates[2].toString() : "";

						 tempDaysBetween = daysBetween(sdf1.parse(lStrArrearsLimitDate), sdf2.parse(effectiveFrom));

						if(tempDaysBetween!=0){
							lStrArrearsLimitDate = effectiveFrom;
							
						}

						if(applicableTo!=null && !applicableTo.equals("")){
							
							daysBetween = daysBetween(sdf1.parse(lStrArrearsLimitDate), sdf2.parse(applicableTo)); 
							

							interestForMonthlyContri = interestForMonthlyContri + ((yearlyAmtSixpc*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);

							daysBetween = null;
						}

						else{
							
							daysBetween = daysBetween(sdf1.parse(lStrArrearsLimitDate), sdf2.parse(lStrToDatePassed)); 
							
							interestForMonthlyContri = interestForMonthlyContri + ((yearlyAmtSixpc*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);

							daysBetween = null;
						}
						objRates = null;
						intRate = null;
						effectiveFrom = null;
						applicableTo = null;
						tempDaysBetween = null;
					}


					mstDcpsContributionMonthlyForInsertion = new MstDcpsContributionMonthly();
					mstDcpsContributionMonthlyForInsertion.setDcpsContributionMonthlyId(dcpsContributionMonthlyId);
					mstDcpsContributionMonthlyForInsertion.setDcpsId(dcpsId);
					mstDcpsContributionMonthlyForInsertion.setYearId(yearId);
					mstDcpsContributionMonthlyForInsertion.setCurTreasuryCD(Long.parseLong(currTreasuryCode));
					mstDcpsContributionMonthlyForInsertion.setCurDdoCD(currDDOCode);
					mstDcpsContributionMonthlyForInsertion.setvMonth(voucherMonth);
					mstDcpsContributionMonthlyForInsertion.setvYear(voucherYear);
					if(payCommision.equals("700339"))
					{
						mstDcpsContributionMonthlyForInsertion.setPayMonth(5l);
						mstDcpsContributionMonthlyForInsertion.setPayYear(2010l);
					}
					else
					{
						mstDcpsContributionMonthlyForInsertion.setPayMonth(6l);
						mstDcpsContributionMonthlyForInsertion.setPayYear(2009l);
					}



					// Voucher no if not there then 0 entered as for some yearly installments in the beginning it was not captured.
					if(!"".equals(voucherNo))
					{
						mstDcpsContributionMonthlyForInsertion.setVorcNo(voucherNo);
					}
					else
					{
						mstDcpsContributionMonthlyForInsertion.setVorcNo("NOT AVAILABLE");
					}
					Date lDateVoucherDateTier2 = null;
					if(voucherDate != null && !voucherDate.equals(""))
					{
						lDateVoucherDateTier2 = sdf.parse(voucherDate.toString().trim());

						Calendar lObjCalendarTier2 = Calendar.getInstance();
						lObjCalendarTier2.setTime(lDateVoucherDateTier2);

						Integer lIntVMonthTier2 = lObjCalendarTier2.get(Calendar.MONTH) + 1;
						monthId = Long.parseLong(lIntVMonthTier2.toString());

						Integer lIntVYearTier2 = lObjCalendarTier2.get(Calendar.YEAR) ;
						finYearCode = Long.parseLong(lIntVYearTier2.toString());

					}
					if("".equals(voucherDate))
					{
						voucherNo="9999";
						voucherDate = "9999-01-01";
						lDateVoucherDateTier2 = sdf2.parse(voucherDate);
						monthId = 0l;
						finYearCode = 0l;
					}

					mstDcpsContributionMonthlyForInsertion.setVorcDate(lDateVoucherDateTier2);

					mstDcpsContributionMonthlyForInsertion.setvYear(finYearCode);
					mstDcpsContributionMonthlyForInsertion.setvMonth(monthId);

					mstDcpsContributionMonthlyForInsertion.setVorcNo(voucherNo);
					mstDcpsContributionMonthlyForInsertion.setVorcDate(sdf2.parse(voucherDate));
					mstDcpsContributionMonthlyForInsertion.setContribEmp(0d);
					mstDcpsContributionMonthlyForInsertion.setContribEmplr(0d);
					mstDcpsContributionMonthlyForInsertion.setContribTier2(Round(yearlyAmtSixpc,2));
					mstDcpsContributionMonthlyForInsertion.setIntContribEmp(0d);
					mstDcpsContributionMonthlyForInsertion.setIntContribEmplr(0d);
					mstDcpsContributionMonthlyForInsertion.setIntContribTier2(Round(interestForMonthlyContri,2));
					mstDcpsContributionMonthlyForInsertion.setCreatedPostId(1l);
					mstDcpsContributionMonthlyForInsertion.setCreatedUserId(1l);
					mstDcpsContributionMonthlyForInsertion.setCreatedDate(DBUtility.getCurrentDateFromDB());
					mstDcpsContributionMonthlyForInsertion.setUpdatedPostId(gLngPostId);
					mstDcpsContributionMonthlyForInsertion.setUpdatedUserId(gLngUserId);
					mstDcpsContributionMonthlyForInsertion.setUpdatedDate(gDtCurDate);
					mstDcpsContributionMonthlyForInsertion.setTypeOfPayment(null);
					mstDcpsContributionMonthlyForInsertion.setIsMissingCredit(null);
					mstDcpsContributionMonthlyForInsertion.setIsChallan(null);
					mstDcpsContributionMonthlyForInsertion.setCloseEmpPrv(0d);
					mstDcpsContributionMonthlyForInsertion.setCloseEmplrPrv(0d);
					mstDcpsContributionMonthlyForInsertion.setCloseTier2Prv(0d);
					lstMstContriMonth.add(mstDcpsContributionMonthlyForInsertion);

					mstDcpsContributionMonthlyForInsertion = null;
					objSixpc = null;

					interestForMonthlyContri = 0d;

					++dcpsContributionMonthlyId;
					++countMonth;
					dcpsIdForDeletion.add(dcpsId);
				}
			}
			it = null;
			itr = null;
			dcpsEmpIdForSixPc = null;
			totalDcpsEmpIds = null;

			//	Long dcpsContributionMonthlyIdForUpdate = dcpsContributionMonthlyId+countMonth + 1;

			//lObjInterestCalculationDAO.updateGeneratedId(Long.valueOf(dcpsContributionMonthlyIdForUpdate.toString()),"MST_DCPS_CONTRIBUTION_MONTHLY");
			//Delete Records From Monthly Contribution Table
			
			if(dcpsIdForDeletion!=null && dcpsIdForDeletion.size()>0){
				
				gLogger.info("size dcpsIdForDeletion"+dcpsIdForDeletion.size());	
				List tempDcpsEmpId = new ArrayList();
				for(int i = 0; i<dcpsIdForDeletion.size(); i++){
					if(i==0 || i%5000!=0){
						tempDcpsEmpId.add(dcpsIdForDeletion.get(i));
					}

					else{
						tempDcpsEmpId.add(dcpsIdForDeletion.get(i));
						lObjInterestCalculationDAO.deleteMonthlyIntrstsForGivenEmpList(tempDcpsEmpId, yearId);
						tempDcpsEmpId = null;
						tempDcpsEmpId = new ArrayList();
					}
				}
				lObjInterestCalculationDAO.deleteMonthlyIntrstsForGivenEmpList(tempDcpsEmpId, yearId);
			}

			gLogger.info("Monthly deleted");	

			Double openEmp = 0d;
			Double openEmplr = 0d;
			Double openBalanceTier2 = 0d;
			Double openNetBalance = 0d;
			Double openIntBalance = 0d;
			Double closeEmp = null;
			Double closeEmplr = null;
			Double openIntPrv = null;
			Double closeTier2 = null;

			Map<String,MstDcpsContributionYearly> yearlyDetails = new HashMap<String, MstDcpsContributionYearly>();

			for(MstDcpsContributionMonthly mstDcpsContributionMonthly : lstMstContriMonth){
				MstDcpsContributionYearly mstDcpsContributionYearlyOld = new MstDcpsContributionYearly();
				MstDcpsContributionYearly mstDcpsContributionYearlyOldHistory = new MstDcpsContributionYearly();

				if(yearlyDetails.containsKey(mstDcpsContributionMonthly.getDcpsId())){
					mstDcpsContributionYearlyOldHistory = yearlyDetails.get(mstDcpsContributionMonthly.getDcpsId());

					mstDcpsContributionYearlyOldHistory.setCurTreasuryCD(mstDcpsContributionMonthly.getCurTreasuryCD());
					mstDcpsContributionYearlyOldHistory.setCurDdoCD(mstDcpsContributionMonthly.getCurDdoCD());
					mstDcpsContributionYearlyOldHistory.setOpenEmp(mstDcpsContributionYearlyOldHistory.getOpenEmp());
					mstDcpsContributionYearlyOldHistory.setOpenEmplr(mstDcpsContributionYearlyOldHistory.getOpenEmplr());
					mstDcpsContributionYearlyOldHistory.setOpenNet(mstDcpsContributionYearlyOldHistory.getOpenNet());
					mstDcpsContributionYearlyOldHistory.setOpenTier2(mstDcpsContributionYearlyOldHistory.getCloseTier2());
					mstDcpsContributionYearlyOldHistory.setContribEmp(mstDcpsContributionYearlyOldHistory.getContribEmp()+mstDcpsContributionMonthly.getContribEmp());
					mstDcpsContributionYearlyOldHistory.setContribEmplr(mstDcpsContributionYearlyOldHistory.getContribEmplr()+mstDcpsContributionMonthly.getContribEmplr());
					mstDcpsContributionYearlyOldHistory.setContribTier2(mstDcpsContributionYearlyOldHistory.getContribTier2()+mstDcpsContributionMonthly.getContribTier2());
					mstDcpsContributionYearlyOldHistory.setIntContribEmp(mstDcpsContributionYearlyOldHistory.getIntContribEmp()+mstDcpsContributionMonthly.getIntContribEmp());
					mstDcpsContributionYearlyOldHistory.setIntContribEmplr(mstDcpsContributionYearlyOldHistory.getIntContribEmplr()+mstDcpsContributionMonthly.getIntContribEmplr());
					mstDcpsContributionYearlyOldHistory.setIntContribTier2(mstDcpsContributionYearlyOldHistory.getIntContribTier2()+mstDcpsContributionMonthly.getIntContribTier2());

					yearlyDetails.put(mstDcpsContributionMonthly.getDcpsId(), mstDcpsContributionYearlyOldHistory);
					mstDcpsContributionYearlyOldHistory = null;
				}
				else{
					//Long dcpsContributionYearlyId = IFMSCommonServiceImpl.getNextSeqNum("mst_dcps_contribution_yearly",inputMap);
					//mstDcpsContributionYearlyOld = lObjInterestCalculationDAO.getContriYearlyVOForYear(mstDcpsContributionMonthly.getDcpsId(),previousFinYearId);
					/*if(mstDcpsContributionYearlyOld==null){
						mstDcpsContributionYearlyOldHistory.setDcpsId(mstDcpsContributionMonthly.getDcpsId());
						mstDcpsContributionYearlyOldHistory.setYearId(yearId);
						mstDcpsContributionYearlyOldHistory.setCurTreasuryCD(mstDcpsContributionMonthly.getCurTreasuryCD());
						mstDcpsContributionYearlyOldHistory.setCurDdoCD(mstDcpsContributionMonthly.getCurDdoCD());
						mstDcpsContributionYearlyOldHistory.setOpenEmp(0d);
						mstDcpsContributionYearlyOldHistory.setOpenEmplr(0d);
						mstDcpsContributionYearlyOldHistory.setOpenNet(0d);
						mstDcpsContributionYearlyOldHistory.setOpenTier2(0d);
						mstDcpsContributionYearlyOldHistory.setContribEmp(mstDcpsContributionMonthly.getContribEmp());
						mstDcpsContributionYearlyOldHistory.setContribEmplr(mstDcpsContributionMonthly.getContribEmplr());
						mstDcpsContributionYearlyOldHistory.setContribTier2(mstDcpsContributionMonthly.getContribTier2());
						mstDcpsContributionYearlyOldHistory.setIntContribEmp(mstDcpsContributionMonthly.getIntContribEmp());
						mstDcpsContributionYearlyOldHistory.setIntContribEmplr(mstDcpsContributionMonthly.getIntContribEmplr());
						mstDcpsContributionYearlyOldHistory.setIntContribTier2(mstDcpsContributionMonthly.getIntContribTier2());
						mstDcpsContributionYearlyOldHistory.setCloseEmp(0d);
						mstDcpsContributionYearlyOldHistory.setCloseEmplr(0d);
						mstDcpsContributionYearlyOldHistory.setCloseNet(0d);
						mstDcpsContributionYearlyOldHistory.setCloseTier2(0d);

					}
					else{
						mstDcpsContributionYearlyOldHistory.setDcpsId(mstDcpsContributionMonthly.getDcpsId());
						mstDcpsContributionYearlyOldHistory.setYearId(yearId);
						mstDcpsContributionYearlyOldHistory.setCurTreasuryCD(mstDcpsContributionMonthly.getCurTreasuryCD());
						mstDcpsContributionYearlyOldHistory.setCurDdoCD(mstDcpsContributionMonthly.getCurDdoCD());
						mstDcpsContributionYearlyOldHistory.setOpenEmp(mstDcpsContributionYearlyOld.getCloseEmp());
						mstDcpsContributionYearlyOldHistory.setOpenEmplr(mstDcpsContributionYearlyOld.getCloseEmplr());
						mstDcpsContributionYearlyOldHistory.setOpenNet(mstDcpsContributionYearlyOld.getCloseNet());
						mstDcpsContributionYearlyOldHistory.setOpenTier2(mstDcpsContributionYearlyOld.getCloseTier2());
						mstDcpsContributionYearlyOldHistory.setContribEmp(mstDcpsContributionMonthly.getContribEmp());
						mstDcpsContributionYearlyOldHistory.setContribEmplr(mstDcpsContributionMonthly.getContribEmplr());
						mstDcpsContributionYearlyOldHistory.setContribTier2(mstDcpsContributionMonthly.getContribTier2());
						mstDcpsContributionYearlyOldHistory.setIntContribEmp(mstDcpsContributionMonthly.getIntContribEmp());
						mstDcpsContributionYearlyOldHistory.setIntContribEmplr(mstDcpsContributionMonthly.getIntContribEmplr());
						mstDcpsContributionYearlyOldHistory.setIntContribTier2(mstDcpsContributionMonthly.getIntContribTier2());
						mstDcpsContributionYearlyOldHistory.setCloseEmp(mstDcpsContributionYearlyOld.getCloseEmp());
						mstDcpsContributionYearlyOldHistory.setCloseEmplr(mstDcpsContributionYearlyOld.getCloseEmplr());
						mstDcpsContributionYearlyOldHistory.setCloseNet(mstDcpsContributionYearlyOld.getCloseNet());
						mstDcpsContributionYearlyOldHistory.setCloseTier2(mstDcpsContributionYearlyOld.getCloseTier2());
					}*/

					mstDcpsContributionYearlyOldHistory.setDcpsId(mstDcpsContributionMonthly.getDcpsId());
					mstDcpsContributionYearlyOldHistory.setYearId(yearId);
					mstDcpsContributionYearlyOldHistory.setCurTreasuryCD(mstDcpsContributionMonthly.getCurTreasuryCD());
					mstDcpsContributionYearlyOldHistory.setCurDdoCD(mstDcpsContributionMonthly.getCurDdoCD());
					mstDcpsContributionYearlyOldHistory.setOpenEmp(mstDcpsContributionMonthly.getCloseEmpPrv());
					mstDcpsContributionYearlyOldHistory.setOpenEmplr(mstDcpsContributionMonthly.getCloseEmplrPrv());
					mstDcpsContributionYearlyOldHistory.setOpenNet(mstDcpsContributionMonthly.getCloseEmpPrv() + mstDcpsContributionMonthly.getCloseEmpPrv());
					mstDcpsContributionYearlyOldHistory.setOpenTier2(mstDcpsContributionMonthly.getCloseTier2Prv());
					
					mstDcpsContributionYearlyOldHistory.setContribEmp(mstDcpsContributionMonthly.getContribEmp());
					mstDcpsContributionYearlyOldHistory.setContribEmplr(mstDcpsContributionMonthly.getContribEmplr());
					mstDcpsContributionYearlyOldHistory.setContribTier2(mstDcpsContributionMonthly.getContribTier2());
					mstDcpsContributionYearlyOldHistory.setIntContribEmp(mstDcpsContributionMonthly.getIntContribEmp());
					mstDcpsContributionYearlyOldHistory.setIntContribEmplr(mstDcpsContributionMonthly.getIntContribEmplr());
					mstDcpsContributionYearlyOldHistory.setIntContribTier2(mstDcpsContributionMonthly.getIntContribTier2());
					mstDcpsContributionYearlyOldHistory.setCloseEmp(0d);
					mstDcpsContributionYearlyOldHistory.setCloseEmplr(0d);
					mstDcpsContributionYearlyOldHistory.setCloseNet(0d);
					mstDcpsContributionYearlyOldHistory.setCloseTier2(0d);
					yearlyDetails.put(mstDcpsContributionMonthly.getDcpsId(), mstDcpsContributionYearlyOldHistory);

					mstDcpsContributionYearlyOldHistory = null;
					mstDcpsContributionYearlyOld = null;
				}
				if(mstDcpsContributionMonthly.getVorcNo()==null){
					mstDcpsContributionMonthly.setVorcNo("9999");
				}
				hitStg.save(mstDcpsContributionMonthly);
			}

			lstMstContriMonth = null;

			InterestCalculationDAO interestCalculationDAO = new InterestCalculationDAOImpl(MstDcpsContributionYearly.class, serv.getSessionFactory());
			//Delete Records From Yearly Contribution Table

			if(dcpsIdForDeletion!=null && dcpsIdForDeletion.size()>0){
				gLogger.info("size dcpsIdForDeletion"+dcpsIdForDeletion.size());

				if(dcpsIdForDeletion!=null && dcpsIdForDeletion.size()>0){
					List tempDcpsEmpId = new ArrayList();
					for(int i = 0; i<dcpsIdForDeletion.size(); i++){
						if(i==0 || i%5000!=0){
							tempDcpsEmpId.add(dcpsIdForDeletion.get(i));
						}

						else{
							tempDcpsEmpId.add(dcpsIdForDeletion.get(i));
                         
							interestCalculationDAO.deleteYearlyIntrstsForGivenEmpList(tempDcpsEmpId, yearId);
							tempDcpsEmpId = null;
							tempDcpsEmpId = new ArrayList();
						}
					}
					interestCalculationDAO.deleteYearlyIntrstsForGivenEmpList(tempDcpsEmpId, yearId);
				}

				//interestCalculationDAO.deleteYearlyIntrstsForGivenEmpList(dcpsIdForDeletion, yearId);
			}
			
			gLogger.info("yearly deleted");	

			dcpsIdForDeletion = null;

			//Inserting Yearly Contributions In MstDcpsContributionYearly

			IdGenerator idGen = new IdGenerator();
			//Long dcpsContributionYearlyId = idGen.PKGenerator("mst_dcps_contribution_yearly", inputMap);
			Long dcpsContributionYearlyId = lObjInterestCalculationDAO.getNextSeqNum("MST_DCPS_CONTRIBUTION_YEARLY");
			Long dcpsContributionYearlyIdForUpdate = 0l;
			if(yearlyDetails!=null && yearlyDetails.size()>0){
				dcpsContributionYearlyIdForUpdate = dcpsContributionYearlyId + yearlyDetails.size() + 1;
			}


			if(dcpsContributionYearlyIdForUpdate!=0){
				lObjInterestCalculationDAO.updateGeneratedId(Long.valueOf(dcpsContributionYearlyIdForUpdate.toString()),"MST_DCPS_CONTRIBUTION_YEARLY");
			}

			Iterator itYearltContri = yearlyDetails.keySet().iterator();
			List<MstDcpsContributionYearly> lstMstContriYear = new ArrayList<MstDcpsContributionYearly>();
			interestRatesForVoucherNo =  lObjInterestCalculationDAO.getInterestRatesForVoucherNo(yearId);
			while(itYearltContri.hasNext()){
				MstDcpsContributionYearly mstDcpsContributionYearly = new MstDcpsContributionYearly();
				String dcpsIdEmp = (String)itYearltContri.next();
				MstDcpsContributionYearly mstDcpsContributionYearlyOld= new MstDcpsContributionYearly();

				mstDcpsContributionYearlyOld=  (MstDcpsContributionYearly)(yearlyDetails!=null ? yearlyDetails.get(dcpsIdEmp):null);

				dcpsIdEmp = null;

				itr = interestRatesForVoucherNo.iterator();
				
				
					while(itr.hasNext()){
						objRates = (Object[])itr.next();
				        intRate = Double.valueOf((objRates[0] != null) ? Double.parseDouble(objRates[0].toString()) : 0d);
				        effectiveFrom = objRates[1]!=null ? objRates[1].toString() : "";
					    applicableTo  = objRates[2]!=null ? objRates[2].toString() : "";
						

						if(applicableTo!=null && !applicableTo.equals("")){
							daysBetween = daysBetween(sdf2.parse(effectiveFrom), sdf2.parse(applicableTo)); 
							daysBetween=daysBetween+1;
							openEmployeeInt = openEmployeeInt + ((mstDcpsContributionYearlyOld.getOpenEmp()*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);
							openEmployerInt = openEmployerInt + ((mstDcpsContributionYearlyOld.getOpenEmplr()*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);
							daysBetween = null;
						}
						else
						{
							daysBetween = daysBetween(sdf2.parse(effectiveFrom), sdf2.parse(lStrToDatePassed));
							daysBetween=daysBetween+1;
							openEmployeeInt = openEmployeeInt + ((mstDcpsContributionYearlyOld.getOpenEmp()*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);
							openEmployerInt = openEmployerInt + ((mstDcpsContributionYearlyOld.getOpenEmplr()*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);
							daysBetween = null;
						}

					//	openEmployeeInt= openEmployeeInt+(intRate * mstDcpsContributionYearlyOld.getOpenEmp()  / 100);
					//	openEmployerInt= openEmployerInt+(intRate * mstDcpsContributionYearlyOld.getOpenEmplr() / 100);
						
						intRate = null;
						objRates = null;
						effectiveFrom = null;
						applicableTo = null;
					}
				
		
				
				
				
				openIntBalance=openEmployeeInt+openEmployerInt;
			
				closeEmp=mstDcpsContributionYearlyOld.getOpenEmp() + mstDcpsContributionYearlyOld.getContribEmp() + mstDcpsContributionYearlyOld.getIntContribEmp() + openEmployeeInt + mstDcpsContributionYearlyOld.getContribTier2() + mstDcpsContributionYearlyOld.getIntContribTier2() ;
				closeEmplr=mstDcpsContributionYearlyOld.getOpenEmplr() + mstDcpsContributionYearlyOld.getContribEmplr() + mstDcpsContributionYearlyOld.getIntContribEmplr() + openEmployerInt ;
				closeTier2=mstDcpsContributionYearlyOld.getContribTier2() + mstDcpsContributionYearlyOld.getIntContribTier2() ;
				mstDcpsContributionYearly.setDcpsContributionYearlyId(dcpsContributionYearlyId);
				mstDcpsContributionYearly.setDcpsId(mstDcpsContributionYearlyOld.getDcpsId());
				mstDcpsContributionYearly.setYearId(mstDcpsContributionYearlyOld.getYearId());
				mstDcpsContributionYearly.setCurTreasuryCD(mstDcpsContributionYearlyOld.getCurTreasuryCD());
				mstDcpsContributionYearly.setCurDdoCD(mstDcpsContributionYearlyOld.getCurDdoCD());
				mstDcpsContributionYearly.setOpenNet(mstDcpsContributionYearlyOld.getOpenNet());
				mstDcpsContributionYearly.setOpenEmp(mstDcpsContributionYearlyOld.getOpenEmp());
				mstDcpsContributionYearly.setOpenEmplr(mstDcpsContributionYearlyOld.getOpenEmplr());
				mstDcpsContributionYearly.setOpenTier2(mstDcpsContributionYearlyOld.getOpenTier2());
				mstDcpsContributionYearly.setContribEmp(mstDcpsContributionYearlyOld.getContribEmp());
				mstDcpsContributionYearly.setContribEmplr(mstDcpsContributionYearlyOld.getContribEmplr());
				mstDcpsContributionYearly.setContribTier2(mstDcpsContributionYearlyOld.getContribTier2());
				mstDcpsContributionYearly.setIntContribEmp(mstDcpsContributionYearlyOld.getIntContribEmp());
				mstDcpsContributionYearly.setIntContribEmplr(mstDcpsContributionYearlyOld.getIntContribEmplr());
				mstDcpsContributionYearly.setIntContribTier2(mstDcpsContributionYearlyOld.getIntContribTier2());
				mstDcpsContributionYearly.setOpenInt(Round(openIntBalance,2));
				mstDcpsContributionYearly.setCloseEmp(Round(closeEmp,2));
				mstDcpsContributionYearly.setCloseEmplr(Round(closeEmplr,2));
				mstDcpsContributionYearly.setCloseTier2(Round(closeTier2,2));
				mstDcpsContributionYearly.setCloseNet(Round((closeEmp+closeEmplr),2));
				mstDcpsContributionYearly.setCreatedPostId(1l);
				mstDcpsContributionYearly.setCreatedUserId(1l);
				mstDcpsContributionYearly.setCreatedDate(DBUtility.getCurrentDateFromDB());
				mstDcpsContributionYearly.setUpdatedPostId(null);
				mstDcpsContributionYearly.setUpdatedUserId(null);
				mstDcpsContributionYearly.setUpdatedDate(null);
				mstDcpsContributionYearly.setOpenIntEmp(Round(openEmployeeInt,2));
				mstDcpsContributionYearly.setOpenIntEmplr(Round(openEmployerInt,2));
				lstMstContriYear.add(mstDcpsContributionYearly);

				mstDcpsContributionYearly = null;
				mstDcpsContributionYearlyOld = null;
				dcpsContributionYearlyId=dcpsContributionYearlyId+1;
				openEmployeeInt = 0d;
				openEmployerInt = 0d;
				openIntBalance = 0d;
				//interestCalculationDAO.create(mstDcpsContributionYearly);
			}
			idGen = null;
			itYearltContri = null;
			itr = null;


			/*Long dcpsContributionYearlyIdForUpdate = 0l;
			if(yearlyDetails!=null && yearlyDetails.size()>0){
				dcpsContributionYearlyIdForUpdate = dcpsContributionYearlyId + countYear + 1;
			}


			if(dcpsContributionYearlyIdForUpdate!=0){
				lObjInterestCalculationDAO.updateGeneratedId(Long.valueOf(dcpsContributionYearlyIdForUpdate.toString()),"MST_DCPS_CONTRIBUTION_YEARLY");
			}
			 */

			for(MstDcpsContributionYearly objMstDcpsContributionYearly : lstMstContriYear){

				hitStg.save(objMstDcpsContributionYearly);
			}

			List tempDcpsContriId = new ArrayList();
			
			if(lstDcpsContriId!=null && lstDcpsContriId.size()>0){
				gLogger.info("size lstDcpsContriId"+lstDcpsContriId.size());	
				for(int i=0 ; i<lstDcpsContriId.size() ; i++){
					if(i==0 || i%5000!=0){
						tempDcpsContriId.add(lstDcpsContriId.get(i));
					}
					else{
						tempDcpsContriId.add(lstDcpsContriId.get(i));
					
						lObjInterestCalculationDAO.updateTrnDcpsContriIntCalculated(tempDcpsContriId);
						tempDcpsContriId = null;
						tempDcpsContriId = new ArrayList();
					}
				}
				lObjInterestCalculationDAO.updateTrnDcpsContriIntCalculated(tempDcpsContriId);
			}
			
			gLogger.info("yearly updated");	

			tempDcpsContriId = null;
			lstMstContriYear = null;
			lObjInterestCalculationDAO = null;
			lstDcpsContriId = null;

			lBlFlag = true;

			String lSBStatus = getResponseXMLDoc(lBlFlag,lStrInterestFor).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
					lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");

			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error in Interest Calculation " + e, e);
			return resObj;
		}

		return resObj;

	
		
	}

	public ResultObject calculateDCPSYearlyInterest(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		Boolean lBlFlag = false;
		String listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc = null;
		String listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits = null;
		List dcpsEmpIdForSixPc = null;
		Long yearId = null;
		Long treasuryCode = null;
		String lStrFinYearStartDate = null;
		String lStrFinYearEndDate = null;
		SgvcFinYearMst lObjSgvcFinYearMst = null;
		SgvcFinYearMst lObjSgvcFinYearMstForVoucher = null;


		String lStrArrearsLimitDate = null;

		Integer NumberOfDaysInGivenYear = null;


		MstDcpsContribTerminationMonthly mstDcpsContributionMonthlyForInsertion = null;
		List<MstDcpsContribTerminationMonthly> lstMstContriMonth = new ArrayList<MstDcpsContribTerminationMonthly>();

		// No of samples decided here so as to restrict the no of values pass into IN clause of HQL query
		try {

			
		     Object[] obj;
		      Object[] objRates;
		      Double intRate;
		      String effectiveFrom;
		      String applicableTo;
		      Integer tempDaysBetween;
		      Integer daysBetween;
		      setSessionInfo(inputMap);

			HibernateTemplate hitStg = new HibernateTemplate(serv.getSessionFactory());
          
			InterestCalculationDAO lObjInterestCalculationDAO = new InterestCalculationDAOImpl(MstDcpsContribTerminationYearly.class, serv.getSessionFactory());
			InterestCalculationDAO lObjInterestCalculationMonthlyDAO = new InterestCalculationDAOImpl(MstDcpsContribTerminationMonthly.class, serv.getSessionFactory());

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(
					SgvcFinYearMst.class, serv.getSessionFactory());
			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class, serv.getSessionFactory());

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

			if (!StringUtility.getParameter("yearId", request).trim().equalsIgnoreCase("")) {
				yearId = Long.valueOf(StringUtility.getParameter("yearId",request).trim());
			
			}
			if (!StringUtility.getParameter("treasuryCode", request).trim().equalsIgnoreCase("")) {
				treasuryCode = Long.valueOf(StringUtility.getParameter("treasuryCode", request).trim());
				
			}

			String ddoCode = StringUtility.getParameter("ddoCode", request).trim();

			lObjSgvcFinYearMst = (SgvcFinYearMst) lObjDcpsCommonDAO.read(yearId);

			lStrFinYearStartDate = lObjSgvcFinYearMst.getFromDate().toString().trim();
			lStrFinYearEndDate = lObjSgvcFinYearMst.getToDate().toString().trim();

			String lStrFromDatePassed = StringUtility.getParameter("fromDate", request).trim();
			String lStrToDatePassed = StringUtility.getParameter("toDate", request).trim();

			if(!"".equals(lStrFromDatePassed))
			{
				lStrFromDatePassed = sdf2.format(sdf1.parse(lStrFromDatePassed));
			}
			if(!"".equals(lStrToDatePassed))
			{
				lStrToDatePassed = sdf2.format(sdf1.parse(lStrToDatePassed));
			}

			lStrArrearsLimitDate =  gObjRsrcBndle.getString("DCPS.ArrearsLimitDateForInterestCalc");
			lStrArrearsLimitDate = lStrArrearsLimitDate.trim().concat(lObjSgvcFinYearMst.getFinYearCode()).trim();
			Date lDateArrearsLimitDate = sdf1.parse(lStrArrearsLimitDate);

			Long previousFinYearId = lObjSgvcFinYearMst.getPrevFinYearId();

			GregorianCalendar grgcal = new GregorianCalendar();

			if (grgcal.isLeapYear(Integer.parseInt(lObjSgvcFinYearMst.getFinYearCode()) + 1)) {
				NumberOfDaysInGivenYear = 366;
			} else {
				NumberOfDaysInGivenYear = 365;
			}

			String lStrInterestFor = StringUtility.getParameter("interestFor",request).trim();

			String totalDcpsEmpIds = null;

			if(lStrInterestFor.equals("Treasury") || lStrInterestFor.equals("DDO"))
			{
				// Gets all DCPS_EMP_IDs which are eligible for Regular Contri
	          gLogger.info("Inside First if of treasury and ddo**********");		
				listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc = lObjInterestCalculationDAO.getAllDCPSEmployeesForIntrstCalc(treasuryCode,ddoCode,lStrFromDatePassed, lStrToDatePassed);
				//listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc = lObjInterestCalculationDAO.getEmpListForRegularConti(treasuryCode,ddoCode,lStrFromDatePassed, lStrToDatePassed);


				// Gets all DCPS_EMP_IDs which are eligible for Missing Credits
				listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits = lObjInterestCalculationDAO.getAllDCPSEmployeesForIntrstCalcForMissingCredits(treasuryCode,ddoCode,lStrFromDatePassed, lStrToDatePassed,listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc);

				if(listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits!=null && !listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits.equals("") && listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc!=null && !listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc.equals("")){
					totalDcpsEmpIds = listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc +","+listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits;
				}
				else if (listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits!=null && !listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits.equals("") && (listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc==null || listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc.equals("") ))
				{
					totalDcpsEmpIds = listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits;
				}

				else{
					totalDcpsEmpIds = listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc;
				}

			}
			
			else if(lStrInterestFor.equals("Emp"))
			{
				// Added by ashish to display only eligible employee list
				String lStrEmpIds = StringUtility.getParameter("empIds", request).trim().replaceAll("~", ",");


				totalDcpsEmpIds = lObjInterestCalculationDAO.checkEmployeeEligibleForIntrstCalc(lStrEmpIds, lStrFromDatePassed, lStrToDatePassed,yearId);

				lStrEmpIds = null;
			}
			 gLogger.info("Total Employee count is ****"+totalDcpsEmpIds);
			List empDataForInterestCalcRegular = null;

			List empDataForInterestCalcMissingCredit = null;

			List dcpsIdForDeletion = new ArrayList();

			if(totalDcpsEmpIds!=null && !totalDcpsEmpIds.equals("")){
				if(lStrInterestFor.equals("Treasury") || lStrInterestFor.equals("DDO")){
					  
					//empDataForInterestCalcRegular = lObjInterestCalculationDAO.getEmpListForRegularConti(listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc,lStrFromDatePassed, lStrToDatePassed,yearId.toString());
					empDataForInterestCalcRegular = lObjInterestCalculationDAO.getEmpListForRegularConti(totalDcpsEmpIds,lStrFromDatePassed, lStrToDatePassed,yearId.toString());
					  gLogger.info("got employees for regular"+empDataForInterestCalcRegular);		
					//if(listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits!=null && !listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits.equals("")){
						//empDataForInterestCalcMissingCredit = lObjInterestCalculationDAO.getEmpListForMissingCredit(listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits,lStrFromDatePassed, lStrToDatePassed,yearId.toString());
						empDataForInterestCalcMissingCredit = lObjInterestCalculationDAO.getEmpListForMissingCredit(totalDcpsEmpIds,lStrFromDatePassed, lStrToDatePassed,yearId.toString());
						 gLogger.info("got employees for missing credit"+empDataForInterestCalcMissingCredit);	
					//}
				}

				else if(lStrInterestFor.equals("Emp"))
				{

					empDataForInterestCalcRegular = lObjInterestCalculationDAO.getEmpListForRegularConti(totalDcpsEmpIds,lStrFromDatePassed, lStrToDatePassed,yearId.toString());
					empDataForInterestCalcMissingCredit = lObjInterestCalculationDAO.getEmpListForMissingCredit(totalDcpsEmpIds,lStrFromDatePassed, lStrToDatePassed,yearId.toString());
				}
			}

			if(totalDcpsEmpIds!=null && !totalDcpsEmpIds.equals("")){
				dcpsEmpIdForSixPc = lObjInterestCalculationDAO.getArrearsDtlsForAllEmps(totalDcpsEmpIds,yearId);
			}



			//Long dcpsContributionMonthlyId = IFMSCommonServiceImpl.getNextSeqNum("mst_dcps_contribution_monthly",inputMap);

			Long dcpsContributionMonthlyId = lObjInterestCalculationDAO.getNextSeqNum("MST_DCPS_CONTRIB_TERMINATION_MONTHLY");
			Long regularSize = 0l;
			Long missingCreditSize = 0l;
			Long sixPcSize = 0l;

			if(empDataForInterestCalcRegular != null){
				regularSize = Long.valueOf(empDataForInterestCalcRegular.size());
			}

			if(empDataForInterestCalcMissingCredit != null){
				missingCreditSize = Long.valueOf(empDataForInterestCalcMissingCredit.size());
			}

			if(dcpsEmpIdForSixPc != null){
				sixPcSize = Long.valueOf(dcpsEmpIdForSixPc.size());
			}

			
			Long dcpsContributionMonthlyIdForUpdate = dcpsContributionMonthlyId + regularSize+missingCreditSize+sixPcSize+1;

			//lObjInterestCalculationDAO.updateGeneratedId(Long.valueOf(dcpsContributionMonthlyIdForUpdate.toString().substring(6, dcpsContributionMonthlyIdForUpdate.toString().length())),"MST_DCPS_CONTRIBUTION_MONTHLY");
			lObjInterestCalculationDAO.updateGeneratedId(Long.valueOf(dcpsContributionMonthlyIdForUpdate.toString()),"MST_DCPS_CONTRIB_TERMINATION_MONTHLY");
			listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc = null;
			listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits = null;

			String voucherDate = null;
			int countMonth=0;
			Double contribution = null;
			String typeOfPayment = null;
			Long monthId = 0l;
			Long finYearCode = 0l;
			Long dcpsContriId = 0l;
			String voucherNo = null;
			Long voucherMonth = 0l;
			Long voucherYear = 0l;
			Long dcpsEmpId = null;
			String dcpsId = null;
			String contriDDOCode = null;
			String contriTresuryCode = null;
			String currDDOCode = null;
			String currTreasuryCode = null;
			//Long mstContriYearlyId = null;
			Double yearlyAmtSixpc=0d;
			Character isMissingCredit = null;
			Character isChalan = null;
			List interestRatesForVoucherNo = null;
			List interestRatesForMissingCredit=null;
			Iterator it = null;
			Iterator itr = null;
			Iterator iter = null;
			Double interestForMonthlyContri = 0d;
			String payCommision=null;
			Double openEmployeeInt=0d; 
			Double  openEmployerInt=0d;
			Double closeEmpPrv=0d; 
			Double  closeEmplrPrv=0d;
			Double  closeTier2Prv=0d;
			String endDate=null;
			String superAnnDate=null;
			String termDate="";
			int termYear=0;
			int termMonth=0;
			List lstDcpsContriId = new ArrayList();
			HashMap endDateMap= new HashMap();
			// Calculation of interest for Regular Month Contributions
			if(empDataForInterestCalcRegular!=null && empDataForInterestCalcRegular.size()>0){
				it = empDataForInterestCalcRegular.iterator();
				interestRatesForVoucherNo =  lObjInterestCalculationDAO.getInterestRatesForVoucherNo(yearId);

				while(it.hasNext()){
					obj = (Object[]) it.next();
					voucherDate = obj[0]!=null ? obj[0].toString():"";
					contribution = obj[1]!=null ? Double.parseDouble(obj[1].toString()):0d;
					typeOfPayment = obj[2]!=null ? obj[2].toString():"-";
					monthId = obj[3]!=null ? Long.parseLong(obj[3].toString()):0;
					finYearCode = obj[4]!=null ? Long.parseLong(obj[4].toString()):0;
					dcpsContriId = obj[5]!=null ? Long.parseLong(obj[5].toString()):0l;
					voucherNo = obj[6]!=null ? obj[6].toString():"-";
					voucherMonth = obj[7]!=null ? Long.parseLong(obj[7].toString()):0;
					voucherYear = obj[8]!=null ? Long.parseLong(obj[8].toString()):0;
					dcpsEmpId = obj[9]!=null ? Long.parseLong(obj[9].toString()):0l;
					dcpsId = obj[10]!=null ? obj[10].toString():"-";
					contriDDOCode = obj[11]!=null ? obj[11].toString():"-";
					contriTresuryCode = obj[12]!=null ? obj[12].toString():"-";
					currDDOCode = obj[13]!=null && !obj[13].equals("")? obj[13].toString():"-";
					closeEmpPrv = obj[16]!=null && !obj[16].equals("")? Double.parseDouble(obj[16].toString()):0d;
					closeEmplrPrv = obj[17]!=null && !obj[17].equals("")? Double.parseDouble(obj[17].toString()):0d;
					closeTier2Prv = obj[19]!=null && !obj[19].equals("")? Double.parseDouble(obj[19].toString()):0d;
					superAnnDate=obj[20]!=null ? obj[20].toString():"";
					currTreasuryCode = obj[14]!=null && !obj[14].equals("")? obj[14].toString():"0";
					endDate=obj[21]!=null ? obj[21].toString():"";
				
					/*if(endDateMap!=null && endDateMap.containsKey(dcpsId))
					{
						if(endDateMap.get(dcpsId)!=null)
						endDate=endDateMap.get(dcpsId).toString();
					}
					else
					{
						endDate=lObjInterestCalculationDAO.getEndDate(dcpsId);
						endDateMap.put(dcpsId, endDate);
					}*/
					
					
					if(endDate!=null && !"".equalsIgnoreCase(endDate))
					{
						if(superAnnDate!=null && !"".equalsIgnoreCase(superAnnDate))
						{
							 if(sdf2.parse(endDate).before(sdf2.parse(superAnnDate))){
								 termDate=endDate;
					            }
							 else
							 {
								 termDate=superAnnDate;
							 }
						}
						else
						{
							termDate=endDate;
						}
						
					}
					else if(superAnnDate!=null && !"".equalsIgnoreCase(superAnnDate))
					{
						termDate=superAnnDate;
					}
					if(termDate!=null && !"".equalsIgnoreCase(termDate))
					{
						if(sdf2.parse(termDate).after(sdf2.parse(lStrToDatePassed)))
						{
							
						}
						
						else
						{
							
							Calendar cal = Calendar.getInstance();
							cal.setTime(sdf2.parse(termDate));
							termYear = cal.get(Calendar.YEAR);
							termMonth = cal.get(Calendar.MONTH)+1;
						}
					}
					if(endDateMap!=null && !endDateMap.containsKey(dcpsId))
					{
						endDateMap.put(dcpsId, termDate);
					}
					if((termMonth == 0 && termYear==0) || (termYear > finYearCode) || ( termYear == finYearCode && termMonth >= monthId))
					{
						lstDcpsContriId.add(dcpsContriId);
						
						//	mstContriYearlyId = obj[15]!=null ? Long.parseLong(obj[15].toString()):0l;

						itr = interestRatesForVoucherNo.iterator();
						while(itr.hasNext()){
							objRates = (Object[])itr.next();
							intRate = objRates[0]!=null ? Double.parseDouble(objRates[0].toString()) : 0d;
							effectiveFrom = objRates[1]!=null ? objRates[1].toString() : "";
							applicableTo  = objRates[2]!=null ? objRates[2].toString() : "";
							if(termYear >0 && termMonth>0)
							{
								if(applicableTo!=null && !applicableTo.equals(""))
								{
									if(sdf2.parse(applicableTo).after(sdf2.parse(termDate)))
									{
										applicableTo=termDate;
									}
								}
								else
								{
									if(sdf2.parse(lStrToDatePassed).after(sdf2.parse(termDate)))
									{
										applicableTo=termDate;
									}
								}
							}

							tempDaysBetween = daysBetween(sdf2.parse(voucherDate), sdf2.parse(effectiveFrom));

							if(tempDaysBetween!=0){
								voucherDate = effectiveFrom;
							}

							if(applicableTo!=null && !applicableTo.equals("")){
								daysBetween = daysBetween(sdf2.parse(voucherDate), sdf2.parse(applicableTo)); 
								if(daysBetween <0)
								{
									daysBetween=0;
								}
								interestForMonthlyContri = interestForMonthlyContri + ((contribution*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);
								daysBetween = null;
							}

							else{
								
								daysBetween = daysBetween(sdf2.parse(voucherDate), sdf2.parse(lStrToDatePassed)); 
								if(daysBetween <0)
								{
									daysBetween=0;
								}

								interestForMonthlyContri = interestForMonthlyContri + ((contribution*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);

								daysBetween = null;
							}
							
							
							

							objRates = null;
							intRate = null;
							effectiveFrom = null;
							applicableTo = null;
							tempDaysBetween = null;
						}

						mstDcpsContributionMonthlyForInsertion = new MstDcpsContribTerminationMonthly();
						mstDcpsContributionMonthlyForInsertion.setDcpsContributionMonthlyId(dcpsContributionMonthlyId);
						mstDcpsContributionMonthlyForInsertion.setDcpsId(dcpsId);
						
						mstDcpsContributionMonthlyForInsertion.setYearId(yearId);
					
						mstDcpsContributionMonthlyForInsertion.setCurTreasuryCD(Long.parseLong(currTreasuryCode));
						
						mstDcpsContributionMonthlyForInsertion.setCurDdoCD(currDDOCode);
					
						mstDcpsContributionMonthlyForInsertion.setvMonth(voucherMonth);
						
						mstDcpsContributionMonthlyForInsertion.setvYear(voucherYear);
					
						mstDcpsContributionMonthlyForInsertion.setPayYear(finYearCode);
						
						mstDcpsContributionMonthlyForInsertion.setPayMonth(monthId);
					
						mstDcpsContributionMonthlyForInsertion.setVorcNo(voucherNo);
					
						mstDcpsContributionMonthlyForInsertion.setVorcDate(sdf.parse(voucherDate));
					
						mstDcpsContributionMonthlyForInsertion.setContribEmp(Round(contribution,2));
					
						mstDcpsContributionMonthlyForInsertion.setContribEmplr(Round(contribution,2));
						mstDcpsContributionMonthlyForInsertion.setContribTier2(0d);
						mstDcpsContributionMonthlyForInsertion.setIntContribEmp(Round(interestForMonthlyContri,2));
						mstDcpsContributionMonthlyForInsertion.setIntContribEmplr(Round(interestForMonthlyContri,2));
						mstDcpsContributionMonthlyForInsertion.setIntContribTier2(0d);
						mstDcpsContributionMonthlyForInsertion.setCreatedPostId(1l);
						mstDcpsContributionMonthlyForInsertion.setCreatedUserId(1l);
						mstDcpsContributionMonthlyForInsertion.setCreatedDate(DBUtility.getCurrentDateFromDB());
						mstDcpsContributionMonthlyForInsertion.setUpdatedPostId(null);
						mstDcpsContributionMonthlyForInsertion.setUpdatedUserId(null);
						mstDcpsContributionMonthlyForInsertion.setUpdatedDate(null);
						mstDcpsContributionMonthlyForInsertion.setTypeOfPayment(typeOfPayment);
						mstDcpsContributionMonthlyForInsertion.setIsMissingCredit(null);
						mstDcpsContributionMonthlyForInsertion.setIsChallan(null);
						mstDcpsContributionMonthlyForInsertion.setCloseEmpPrv(closeEmpPrv);
						mstDcpsContributionMonthlyForInsertion.setCloseEmplrPrv(closeEmplrPrv);
						mstDcpsContributionMonthlyForInsertion.setCloseTier2Prv(closeTier2Prv);
						lstMstContriMonth.add(mstDcpsContributionMonthlyForInsertion);

						mstDcpsContributionMonthlyForInsertion = null;

						++dcpsContributionMonthlyId;
						++countMonth;

						dcpsIdForDeletion.add(dcpsId);

						interestForMonthlyContri = 0d;
						obj = null;
					}
					mstDcpsContributionMonthlyForInsertion = null;
					interestForMonthlyContri = 0d;
					obj = null;
					termDate=null;
					termMonth=0;
					termYear=0;
					
				}
			}
			
			gLogger.info("VO created");	
			it = null;
			itr = null;
			empDataForInterestCalcRegular = null;
			termDate=null;
			termMonth=0;
			termYear=0;
			// Calculation of interest for Regular Month Contributions
			if(empDataForInterestCalcMissingCredit!=null){
				it = empDataForInterestCalcMissingCredit.iterator();

				while(it.hasNext()){
					obj = (Object[]) it.next();
					voucherDate = obj[0]!=null ? obj[0].toString():"";
					contribution = obj[1]!=null ? Double.parseDouble(obj[1].toString()):0d;
					typeOfPayment = obj[2]!=null ? obj[2].toString():"-";
					monthId = obj[3]!=null ? Long.parseLong(obj[3].toString()):0;
					finYearCode = obj[4]!=null ? Long.parseLong(obj[4].toString()):0;
					dcpsContriId = obj[5]!=null ? Long.parseLong(obj[5].toString()):0l;
					voucherNo = obj[6]!=null ? obj[6].toString():"-";
					voucherMonth = obj[7]!=null ? Long.parseLong(obj[7].toString()):0;
					voucherYear = obj[8]!=null ? Long.parseLong(obj[8].toString()):0;
					dcpsEmpId = obj[9]!=null ? Long.parseLong(obj[9].toString()):0l;
					dcpsId = obj[10]!=null ? obj[10].toString():"-";
					contriDDOCode = obj[11]!=null ? obj[11].toString():"-";
					contriTresuryCode = obj[12]!=null ? obj[12].toString():"-";
					currDDOCode = obj[13]!=null && !obj[13].equals("")? obj[13].toString():"-";
					currTreasuryCode = obj[14]!=null && !obj[14].equals("")? obj[14].toString():"0";
					//mstContriYearlyId = obj[15]!=null ? Long.parseLong(obj[15].toString()):0l;
					isMissingCredit = obj[15]!=null && obj[15].toString().equals("Y") ? 'Y':null;
					
					isChalan = obj[16]!=null && obj[16].toString().equals("Y") ? 'Y':null;
					closeEmpPrv = obj[18]!=null && !obj[18].equals("")? Double.parseDouble(obj[18].toString()):0d;
					closeEmplrPrv = obj[19]!=null && !obj[18].equals("")? Double.parseDouble(obj[19].toString()):0d;
					closeTier2Prv = obj[21]!=null && !obj[21].equals("")? Double.parseDouble(obj[21].toString()):0d;
					
					
					superAnnDate=obj[22]!=null ? obj[22].toString():"";
					endDate=obj[23]!=null ? obj[23].toString():"";
					
					/*if(endDateMap!=null && endDateMap.containsKey(dcpsId))
					{
						if(endDateMap.get(dcpsId)!=null)
						endDate=endDateMap.get(dcpsId).toString();
					}
					else
					{
						endDate=lObjInterestCalculationDAO.getEndDate(dcpsId);
						endDateMap.put(dcpsId, endDate);
					}*/
					if(endDate!=null && !"".equalsIgnoreCase(endDate))
					{
						if(superAnnDate!=null && !"".equalsIgnoreCase(superAnnDate))
						{
							 if(sdf2.parse(endDate).before(sdf2.parse(superAnnDate))){
								 termDate=endDate;
					            }
							 else
							 {
								 termDate=superAnnDate;
							 }
						}
						else
						{
							termDate=endDate;
						}
						
					}
					else if(superAnnDate!=null && !"".equalsIgnoreCase(superAnnDate))
					{
						termDate=superAnnDate;
					}
					if(termDate!=null && !"".equalsIgnoreCase(termDate))
					{
						if(sdf2.parse(termDate).after(sdf2.parse(lStrToDatePassed)))
						{
							
						}
						
						else
						{
							
							Calendar cal = Calendar.getInstance();
							cal.setTime(sdf2.parse(termDate));
							termYear = cal.get(Calendar.YEAR);
							termMonth = cal.get(Calendar.MONTH)+1;
						}
					}
					if(endDateMap!=null && !endDateMap.containsKey(dcpsId))
					{
						endDateMap.put(dcpsId, termDate);
					}
					
					if((termMonth == 0 && termYear==0) || (termYear > finYearCode) || ( termYear == finYearCode && termMonth >= monthId))
					{
						interestRatesForMissingCredit =  lObjInterestCalculationDAO.getInterestRatesForMissingCredit(lStrToDatePassed,voucherDate,yearId);

						itr = interestRatesForMissingCredit.iterator();

						lstDcpsContriId.add(dcpsContriId);

						while(itr.hasNext()){
						
							objRates = (Object[])itr.next();
							intRate = objRates[0]!=null ? Double.parseDouble(objRates[0].toString()) : 0d;
							effectiveFrom = objRates[1]!=null ? objRates[1].toString() : "";
							applicableTo  = objRates[2]!=null ? objRates[2].toString() : "";
							Long finYearId = objRates[3]!=null ? Long.parseLong(objRates[3].toString()) : 0l;
							lObjSgvcFinYearMst = (SgvcFinYearMst) lObjDcpsCommonDAO.read(finYearId);
							
							if(termYear >0 && termMonth>0)
							{
								if(applicableTo!=null && !applicableTo.equals(""))
								{
									if(sdf2.parse(applicableTo).after(sdf2.parse(termDate)))
									{
										applicableTo=termDate;
									}
								}
								else
								{
									if(sdf2.parse(lStrToDatePassed).after(sdf2.parse(termDate)))
									{
										applicableTo=termDate;
									}
								}
								
							}

							lStrFinYearStartDate = lObjSgvcFinYearMst.getFromDate().toString().trim();
							lStrFinYearEndDate = lObjSgvcFinYearMst.getToDate().toString().trim();
							grgcal = new GregorianCalendar();

							if (grgcal.isLeapYear(Integer.parseInt(lObjSgvcFinYearMst.getFinYearCode()) + 1)) {
								NumberOfDaysInGivenYear = 366;
							} else {
								NumberOfDaysInGivenYear = 365;
							}

							 tempDaysBetween = daysBetween(sdf2.parse(voucherDate), sdf2.parse(effectiveFrom));

							if(tempDaysBetween!=0){
								voucherDate = effectiveFrom;
							}

							if(applicableTo!=null && !applicableTo.equals("")){
								 daysBetween = daysBetween(sdf2.parse(voucherDate), sdf2.parse(applicableTo));
								 if(daysBetween <0)
									{
										daysBetween=0;
									}
								 
								 gLogger.info("$t intRate-->"+intRate);
								 gLogger.info("$t voucherDate-->"+voucherDate);
								 gLogger.info("$t applicableTo-->"+applicableTo);
								 gLogger.info("$t daysBetween-->"+daysBetween);
								 gLogger.info("$t contribution-->"+contribution);
								 gLogger.info("$t intRate-->"+intRate);
								 gLogger.info("$t daysBetween-->"+daysBetween);
								 gLogger.info("$t NumberOfDaysInGivenYear-->"+NumberOfDaysInGivenYear);
								 gLogger.info("$t interestForMonthlyContri-->"+interestForMonthlyContri);
								 gLogger.info("$t ((contribution*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear)-->"+((contribution*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear));

								interestForMonthlyContri = interestForMonthlyContri + ((contribution*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);

								daysBetween = null;
							}

							else{
								 daysBetween = daysBetween(sdf2.parse(voucherDate), sdf2.parse(lStrToDatePassed)); 
								 if(daysBetween <0)
									{
										daysBetween=0;
									}

								 gLogger.info("$t contribution-->"+contribution);
								 gLogger.info("$t intRate-->"+intRate);
								 gLogger.info("$t daysBetween-->"+daysBetween);
								 gLogger.info("$t NumberOfDaysInGivenYear-->"+NumberOfDaysInGivenYear);
								 gLogger.info("$t interestForMonthlyContri-->"+interestForMonthlyContri);
								 gLogger.info("$t ((contribution*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear)-->"+((contribution*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear));
								 
								interestForMonthlyContri = interestForMonthlyContri + ((contribution*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);
								
								daysBetween = null;
							}

							intRate = null;
							effectiveFrom = null;
							applicableTo = null;
							finYearId = null;
							lObjSgvcFinYearMst = null;
							lStrFinYearStartDate = null;
							lStrFinYearEndDate = null;
							grgcal = null;
							tempDaysBetween = null;
							objRates = null;
						}


						interestRatesForMissingCredit = null;
						itr = null;

						mstDcpsContributionMonthlyForInsertion = new MstDcpsContribTerminationMonthly();
						mstDcpsContributionMonthlyForInsertion.setDcpsContributionMonthlyId(dcpsContributionMonthlyId);
						mstDcpsContributionMonthlyForInsertion.setDcpsId(dcpsId);
						mstDcpsContributionMonthlyForInsertion.setYearId(yearId);
						mstDcpsContributionMonthlyForInsertion.setCurTreasuryCD(Long.parseLong(currTreasuryCode));
						mstDcpsContributionMonthlyForInsertion.setCurDdoCD(currDDOCode);
						mstDcpsContributionMonthlyForInsertion.setvMonth(voucherMonth);
						mstDcpsContributionMonthlyForInsertion.setvYear(voucherYear);
						mstDcpsContributionMonthlyForInsertion.setPayYear(finYearCode);
						mstDcpsContributionMonthlyForInsertion.setPayMonth(monthId);
						mstDcpsContributionMonthlyForInsertion.setVorcNo(voucherNo);
						mstDcpsContributionMonthlyForInsertion.setVorcDate(sdf.parse(voucherDate));
						mstDcpsContributionMonthlyForInsertion.setContribEmp(Round(contribution,2));
						mstDcpsContributionMonthlyForInsertion.setContribEmplr(Round(contribution,2));
						mstDcpsContributionMonthlyForInsertion.setContribTier2(0d);
						mstDcpsContributionMonthlyForInsertion.setIntContribEmp(Round(interestForMonthlyContri,2));
						mstDcpsContributionMonthlyForInsertion.setIntContribEmplr(Round(interestForMonthlyContri,2));
						mstDcpsContributionMonthlyForInsertion.setIntContribTier2(0d);
						mstDcpsContributionMonthlyForInsertion.setCreatedPostId(1l);
						mstDcpsContributionMonthlyForInsertion.setCreatedUserId(1l);
						mstDcpsContributionMonthlyForInsertion.setCreatedDate(DBUtility.getCurrentDateFromDB());
						mstDcpsContributionMonthlyForInsertion.setUpdatedPostId(gLngPostId);
						mstDcpsContributionMonthlyForInsertion.setUpdatedUserId(gLngUserId);
						mstDcpsContributionMonthlyForInsertion.setUpdatedDate(gDtCurDate);
						mstDcpsContributionMonthlyForInsertion.setTypeOfPayment(typeOfPayment);
						
						mstDcpsContributionMonthlyForInsertion.setIsMissingCredit(isMissingCredit);
					
						mstDcpsContributionMonthlyForInsertion.setIsChallan(isChalan);
					
						mstDcpsContributionMonthlyForInsertion.setCloseEmpPrv(closeEmpPrv);
						
						mstDcpsContributionMonthlyForInsertion.setCloseEmplrPrv(closeEmplrPrv);
						
						mstDcpsContributionMonthlyForInsertion.setCloseTier2Prv(closeTier2Prv);
					
						lstMstContriMonth.add(mstDcpsContributionMonthlyForInsertion);

						mstDcpsContributionMonthlyForInsertion = null;
						obj = null;

						interestForMonthlyContri = 0d;

						++dcpsContributionMonthlyId;
						++countMonth;
						dcpsIdForDeletion.add(dcpsId);
					}
					mstDcpsContributionMonthlyForInsertion = null;
					obj = null;

					interestForMonthlyContri = 0d;
					termDate=null;
					termMonth=0;
					termYear=0;

					
				}
			}
			
			gLogger.info("VO created for missing credit");	

			it = null;
			empDataForInterestCalcMissingCredit = null;
			

			// Gets all DCPS_EMP_IDs which are eligible for Six PC Arrrears
			if(totalDcpsEmpIds!=null && !totalDcpsEmpIds.equals("")){

				it = dcpsEmpIdForSixPc.iterator();
				interestRatesForVoucherNo =  lObjInterestCalculationDAO.getInterestRatesForVoucherNo(yearId);

				while(it.hasNext()){
					Object[] objSixpc = (Object[])it.next();
					yearlyAmtSixpc = objSixpc[0]!=null ? Double.parseDouble(objSixpc[0].toString()):0d;
					dcpsEmpId = objSixpc[1]!=null ? Long.parseLong(objSixpc[1].toString()):0l;
					voucherNo = objSixpc[2]!=null ? objSixpc[2].toString():"-";
					voucherDate = objSixpc[3]!=null ? objSixpc[3].toString():"";
					finYearCode = objSixpc[4]!=null ? Long.parseLong(objSixpc[4].toString()):0l;
					payCommision = objSixpc[5]!=null ?  objSixpc[5].toString():"-";
					dcpsId = objSixpc[6]!=null ?  objSixpc[6].toString():"-";
					String termDateSixPC=null;
					int termSixPCYear=0;
					int termSixpcMonth=0;
					int payMonth=0;
					int payYear=0;
					if(endDateMap!=null && endDateMap.containsKey(dcpsId) && endDateMap.get(dcpsId)!=null)
					{
						termDateSixPC=endDateMap.get(dcpsId).toString();
						
						Calendar cal = Calendar.getInstance();
						cal.setTime(sdf2.parse(termDateSixPC));
						termSixPCYear = cal.get(Calendar.YEAR);
						termSixpcMonth = cal.get(Calendar.MONTH)+1;
					}
					if(payCommision.equals("700339"))
					{
						payMonth=5;
						payYear=2010;
					}
					else
					{
						payMonth=6;
						payYear=2009;
					}
					
					if((termSixpcMonth == 0 && termSixPCYear==0) || (termSixPCYear > payYear) || ( termSixPCYear == payYear && termSixpcMonth >= payMonth))
					{
						itr = interestRatesForVoucherNo.iterator();
						while(itr.hasNext()){
						    objRates = (Object[])itr.next();
							 intRate = objRates[0]!=null ? Double.parseDouble(objRates[0].toString()) : 0d;
							 effectiveFrom = objRates[1]!=null ? objRates[1].toString() : "";
							 applicableTo  = objRates[2]!=null ? objRates[2].toString() : "";

							 tempDaysBetween = daysBetween(sdf1.parse(lStrArrearsLimitDate), sdf2.parse(effectiveFrom));

							if(tempDaysBetween!=0){
								lStrArrearsLimitDate = effectiveFrom;
								
							}
							
						    if(termDateSixPC!=null && !"".equalsIgnoreCase(termDateSixPC))
						    {
						    	
						    		if(applicableTo!=null && !applicableTo.equals(""))
						    		{
						    			if(sdf2.parse(termDateSixPC).before(sdf2.parse(applicableTo)))
						    			{
						    				applicableTo=termDateSixPC;
						    			}
						    		}
						    		else
						    		{
						    			
						    			if(sdf2.parse(termDateSixPC).before(sdf2.parse(lStrToDatePassed)))
						    			{
						    				applicableTo=termDateSixPC;
						    			}
						    		}
						    		
						    	
						    }

							if(applicableTo!=null && !applicableTo.equals("")){
								
								daysBetween = daysBetween(sdf1.parse(lStrArrearsLimitDate), sdf2.parse(applicableTo)); 
								

								interestForMonthlyContri = interestForMonthlyContri + ((yearlyAmtSixpc*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);

								daysBetween = null;
							}

							else{
								
								daysBetween = daysBetween(sdf1.parse(lStrArrearsLimitDate), sdf2.parse(lStrToDatePassed)); 
								
								interestForMonthlyContri = interestForMonthlyContri + ((yearlyAmtSixpc*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);

								daysBetween = null;
							}
							objRates = null;
							intRate = null;
							effectiveFrom = null;
							applicableTo = null;
							tempDaysBetween = null;
						}


						mstDcpsContributionMonthlyForInsertion = new MstDcpsContribTerminationMonthly();
						mstDcpsContributionMonthlyForInsertion.setDcpsContributionMonthlyId(dcpsContributionMonthlyId);
						mstDcpsContributionMonthlyForInsertion.setDcpsId(dcpsId);
						mstDcpsContributionMonthlyForInsertion.setYearId(yearId);
						mstDcpsContributionMonthlyForInsertion.setCurTreasuryCD(Long.parseLong(currTreasuryCode));
						mstDcpsContributionMonthlyForInsertion.setCurDdoCD(currDDOCode);
						mstDcpsContributionMonthlyForInsertion.setvMonth(voucherMonth);
						mstDcpsContributionMonthlyForInsertion.setvYear(voucherYear);
						if(payCommision.equals("700339"))
						{
							mstDcpsContributionMonthlyForInsertion.setPayMonth(5l);
							mstDcpsContributionMonthlyForInsertion.setPayYear(2010l);
						}
						else
						{
							mstDcpsContributionMonthlyForInsertion.setPayMonth(6l);
							mstDcpsContributionMonthlyForInsertion.setPayYear(2009l);
						}



						// Voucher no if not there then 0 entered as for some yearly installments in the beginning it was not captured.
						if(!"".equals(voucherNo))
						{
							mstDcpsContributionMonthlyForInsertion.setVorcNo(voucherNo);
						}
						else
						{
							mstDcpsContributionMonthlyForInsertion.setVorcNo("NOT AVAILABLE");
						}
						Date lDateVoucherDateTier2 = null;
						if(voucherDate != null && !voucherDate.equals(""))
						{
							lDateVoucherDateTier2 = sdf.parse(voucherDate.toString().trim());

							Calendar lObjCalendarTier2 = Calendar.getInstance();
							lObjCalendarTier2.setTime(lDateVoucherDateTier2);

							Integer lIntVMonthTier2 = lObjCalendarTier2.get(Calendar.MONTH) + 1;
							monthId = Long.parseLong(lIntVMonthTier2.toString());

							Integer lIntVYearTier2 = lObjCalendarTier2.get(Calendar.YEAR) ;
							finYearCode = Long.parseLong(lIntVYearTier2.toString());

						}
						if("".equals(voucherDate))
						{
							voucherNo="9999";
							voucherDate = "9999-01-01";
							lDateVoucherDateTier2 = sdf2.parse(voucherDate);
							monthId = 0l;
							finYearCode = 0l;
						}

						mstDcpsContributionMonthlyForInsertion.setVorcDate(lDateVoucherDateTier2);

						mstDcpsContributionMonthlyForInsertion.setvYear(finYearCode);
						mstDcpsContributionMonthlyForInsertion.setvMonth(monthId);

						mstDcpsContributionMonthlyForInsertion.setVorcNo(voucherNo);
						mstDcpsContributionMonthlyForInsertion.setVorcDate(sdf2.parse(voucherDate));
						mstDcpsContributionMonthlyForInsertion.setContribEmp(0d);
						mstDcpsContributionMonthlyForInsertion.setContribEmplr(0d);
						mstDcpsContributionMonthlyForInsertion.setContribTier2(Round(yearlyAmtSixpc,2));
						mstDcpsContributionMonthlyForInsertion.setIntContribEmp(0d);
						mstDcpsContributionMonthlyForInsertion.setIntContribEmplr(0d);
						mstDcpsContributionMonthlyForInsertion.setIntContribTier2(Round(interestForMonthlyContri,2));
						mstDcpsContributionMonthlyForInsertion.setCreatedPostId(1l);
						mstDcpsContributionMonthlyForInsertion.setCreatedUserId(1l);
						mstDcpsContributionMonthlyForInsertion.setCreatedDate(DBUtility.getCurrentDateFromDB());
						mstDcpsContributionMonthlyForInsertion.setUpdatedPostId(gLngPostId);
						mstDcpsContributionMonthlyForInsertion.setUpdatedUserId(gLngUserId);
						mstDcpsContributionMonthlyForInsertion.setUpdatedDate(gDtCurDate);
						mstDcpsContributionMonthlyForInsertion.setTypeOfPayment(null);
						mstDcpsContributionMonthlyForInsertion.setIsMissingCredit(null);
						mstDcpsContributionMonthlyForInsertion.setIsChallan(null);
						mstDcpsContributionMonthlyForInsertion.setCloseEmpPrv(0d);
						mstDcpsContributionMonthlyForInsertion.setCloseEmplrPrv(0d);
						mstDcpsContributionMonthlyForInsertion.setCloseTier2Prv(0d);
						lstMstContriMonth.add(mstDcpsContributionMonthlyForInsertion);

						mstDcpsContributionMonthlyForInsertion = null;
						objSixpc = null;

						interestForMonthlyContri = 0d;

						++dcpsContributionMonthlyId;
						++countMonth;
						dcpsIdForDeletion.add(dcpsId);
					}

					mstDcpsContributionMonthlyForInsertion = null;
					objSixpc = null;

					interestForMonthlyContri = 0d;
				}
			}
			it = null;
			itr = null;
			dcpsEmpIdForSixPc = null;
			totalDcpsEmpIds = null;

			//	Long dcpsContributionMonthlyIdForUpdate = dcpsContributionMonthlyId+countMonth + 1;

			//lObjInterestCalculationDAO.updateGeneratedId(Long.valueOf(dcpsContributionMonthlyIdForUpdate.toString()),"MST_DCPS_CONTRIBUTION_MONTHLY");
			//Delete Records From Monthly Contribution Table
			
			if(dcpsIdForDeletion!=null && dcpsIdForDeletion.size()>0){
				
				gLogger.info("size dcpsIdForDeletion"+dcpsIdForDeletion.size());	
				List tempDcpsEmpId = new ArrayList();
				for(int i = 0; i<dcpsIdForDeletion.size(); i++){
					if(i==0 || i%5000!=0){
						tempDcpsEmpId.add(dcpsIdForDeletion.get(i));
					}

					else{
						tempDcpsEmpId.add(dcpsIdForDeletion.get(i));
						lObjInterestCalculationDAO.deleteMonthlyIntrstsForGivenEmpList(tempDcpsEmpId, yearId);
						tempDcpsEmpId = null;
						tempDcpsEmpId = new ArrayList();
					}
				}
				lObjInterestCalculationDAO.deleteMonthlyIntrstsForGivenEmpList(tempDcpsEmpId, yearId);
			}

			gLogger.info("Monthly deleted");	

			Double openEmp = 0d;
			Double openEmplr = 0d;
			Double openBalanceTier2 = 0d;
			Double openNetBalance = 0d;
			Double openIntBalance = 0d;
			Double closeEmp = null;
			Double closeEmplr = null;
			Double openIntPrv = null;
			Double closeTier2 = null;

			Map<String,MstDcpsContribTerminationYearly> yearlyDetails = new HashMap<String, MstDcpsContribTerminationYearly>();

			for(MstDcpsContribTerminationMonthly mstDcpsContributionMonthly : lstMstContriMonth){
				MstDcpsContribTerminationYearly mstDcpsContributionYearlyOld = new MstDcpsContribTerminationYearly();
				MstDcpsContribTerminationYearly mstDcpsContributionYearlyOldHistory = new MstDcpsContribTerminationYearly();

				if(yearlyDetails.containsKey(mstDcpsContributionMonthly.getDcpsId())){
					mstDcpsContributionYearlyOldHistory = yearlyDetails.get(mstDcpsContributionMonthly.getDcpsId());

					mstDcpsContributionYearlyOldHistory.setCurTreasuryCD(mstDcpsContributionMonthly.getCurTreasuryCD());
					mstDcpsContributionYearlyOldHistory.setCurDdoCD(mstDcpsContributionMonthly.getCurDdoCD());
					mstDcpsContributionYearlyOldHistory.setOpenEmp(mstDcpsContributionYearlyOldHistory.getOpenEmp());
					mstDcpsContributionYearlyOldHistory.setOpenEmplr(mstDcpsContributionYearlyOldHistory.getOpenEmplr());
					mstDcpsContributionYearlyOldHistory.setOpenNet(mstDcpsContributionYearlyOldHistory.getOpenNet());
					mstDcpsContributionYearlyOldHistory.setOpenTier2(mstDcpsContributionYearlyOldHistory.getCloseTier2());
					mstDcpsContributionYearlyOldHistory.setContribEmp(mstDcpsContributionYearlyOldHistory.getContribEmp()+mstDcpsContributionMonthly.getContribEmp());
					mstDcpsContributionYearlyOldHistory.setContribEmplr(mstDcpsContributionYearlyOldHistory.getContribEmplr()+mstDcpsContributionMonthly.getContribEmplr());
					mstDcpsContributionYearlyOldHistory.setContribTier2(mstDcpsContributionYearlyOldHistory.getContribTier2()+mstDcpsContributionMonthly.getContribTier2());
					mstDcpsContributionYearlyOldHistory.setIntContribEmp(mstDcpsContributionYearlyOldHistory.getIntContribEmp()+mstDcpsContributionMonthly.getIntContribEmp());
					mstDcpsContributionYearlyOldHistory.setIntContribEmplr(mstDcpsContributionYearlyOldHistory.getIntContribEmplr()+mstDcpsContributionMonthly.getIntContribEmplr());
					mstDcpsContributionYearlyOldHistory.setIntContribTier2(mstDcpsContributionYearlyOldHistory.getIntContribTier2()+mstDcpsContributionMonthly.getIntContribTier2());

					yearlyDetails.put(mstDcpsContributionMonthly.getDcpsId(), mstDcpsContributionYearlyOldHistory);
					mstDcpsContributionYearlyOldHistory = null;
				}
				else{
					//Long dcpsContributionYearlyId = IFMSCommonServiceImpl.getNextSeqNum("mst_dcps_contribution_yearly",inputMap);
					//mstDcpsContributionYearlyOld = lObjInterestCalculationDAO.getContriYearlyVOForYear(mstDcpsContributionMonthly.getDcpsId(),previousFinYearId);
					/*if(mstDcpsContributionYearlyOld==null){
						mstDcpsContributionYearlyOldHistory.setDcpsId(mstDcpsContributionMonthly.getDcpsId());
						mstDcpsContributionYearlyOldHistory.setYearId(yearId);
						mstDcpsContributionYearlyOldHistory.setCurTreasuryCD(mstDcpsContributionMonthly.getCurTreasuryCD());
						mstDcpsContributionYearlyOldHistory.setCurDdoCD(mstDcpsContributionMonthly.getCurDdoCD());
						mstDcpsContributionYearlyOldHistory.setOpenEmp(0d);
						mstDcpsContributionYearlyOldHistory.setOpenEmplr(0d);
						mstDcpsContributionYearlyOldHistory.setOpenNet(0d);
						mstDcpsContributionYearlyOldHistory.setOpenTier2(0d);
						mstDcpsContributionYearlyOldHistory.setContribEmp(mstDcpsContributionMonthly.getContribEmp());
						mstDcpsContributionYearlyOldHistory.setContribEmplr(mstDcpsContributionMonthly.getContribEmplr());
						mstDcpsContributionYearlyOldHistory.setContribTier2(mstDcpsContributionMonthly.getContribTier2());
						mstDcpsContributionYearlyOldHistory.setIntContribEmp(mstDcpsContributionMonthly.getIntContribEmp());
						mstDcpsContributionYearlyOldHistory.setIntContribEmplr(mstDcpsContributionMonthly.getIntContribEmplr());
						mstDcpsContributionYearlyOldHistory.setIntContribTier2(mstDcpsContributionMonthly.getIntContribTier2());
						mstDcpsContributionYearlyOldHistory.setCloseEmp(0d);
						mstDcpsContributionYearlyOldHistory.setCloseEmplr(0d);
						mstDcpsContributionYearlyOldHistory.setCloseNet(0d);
						mstDcpsContributionYearlyOldHistory.setCloseTier2(0d);

					}
					else{
						mstDcpsContributionYearlyOldHistory.setDcpsId(mstDcpsContributionMonthly.getDcpsId());
						mstDcpsContributionYearlyOldHistory.setYearId(yearId);
						mstDcpsContributionYearlyOldHistory.setCurTreasuryCD(mstDcpsContributionMonthly.getCurTreasuryCD());
						mstDcpsContributionYearlyOldHistory.setCurDdoCD(mstDcpsContributionMonthly.getCurDdoCD());
						mstDcpsContributionYearlyOldHistory.setOpenEmp(mstDcpsContributionYearlyOld.getCloseEmp());
						mstDcpsContributionYearlyOldHistory.setOpenEmplr(mstDcpsContributionYearlyOld.getCloseEmplr());
						mstDcpsContributionYearlyOldHistory.setOpenNet(mstDcpsContributionYearlyOld.getCloseNet());
						mstDcpsContributionYearlyOldHistory.setOpenTier2(mstDcpsContributionYearlyOld.getCloseTier2());
						mstDcpsContributionYearlyOldHistory.setContribEmp(mstDcpsContributionMonthly.getContribEmp());
						mstDcpsContributionYearlyOldHistory.setContribEmplr(mstDcpsContributionMonthly.getContribEmplr());
						mstDcpsContributionYearlyOldHistory.setContribTier2(mstDcpsContributionMonthly.getContribTier2());
						mstDcpsContributionYearlyOldHistory.setIntContribEmp(mstDcpsContributionMonthly.getIntContribEmp());
						mstDcpsContributionYearlyOldHistory.setIntContribEmplr(mstDcpsContributionMonthly.getIntContribEmplr());
						mstDcpsContributionYearlyOldHistory.setIntContribTier2(mstDcpsContributionMonthly.getIntContribTier2());
						mstDcpsContributionYearlyOldHistory.setCloseEmp(mstDcpsContributionYearlyOld.getCloseEmp());
						mstDcpsContributionYearlyOldHistory.setCloseEmplr(mstDcpsContributionYearlyOld.getCloseEmplr());
						mstDcpsContributionYearlyOldHistory.setCloseNet(mstDcpsContributionYearlyOld.getCloseNet());
						mstDcpsContributionYearlyOldHistory.setCloseTier2(mstDcpsContributionYearlyOld.getCloseTier2());
					}*/

					mstDcpsContributionYearlyOldHistory.setDcpsId(mstDcpsContributionMonthly.getDcpsId());
					mstDcpsContributionYearlyOldHistory.setYearId(yearId);
					mstDcpsContributionYearlyOldHistory.setCurTreasuryCD(mstDcpsContributionMonthly.getCurTreasuryCD());
					mstDcpsContributionYearlyOldHistory.setCurDdoCD(mstDcpsContributionMonthly.getCurDdoCD());
					mstDcpsContributionYearlyOldHistory.setOpenEmp(mstDcpsContributionMonthly.getCloseEmpPrv());
					mstDcpsContributionYearlyOldHistory.setOpenEmplr(mstDcpsContributionMonthly.getCloseEmplrPrv());
					mstDcpsContributionYearlyOldHistory.setOpenNet(mstDcpsContributionMonthly.getCloseEmpPrv() + mstDcpsContributionMonthly.getCloseEmpPrv());
					mstDcpsContributionYearlyOldHistory.setOpenTier2(mstDcpsContributionMonthly.getCloseTier2Prv());
					
					mstDcpsContributionYearlyOldHistory.setContribEmp(mstDcpsContributionMonthly.getContribEmp());
					mstDcpsContributionYearlyOldHistory.setContribEmplr(mstDcpsContributionMonthly.getContribEmplr());
					mstDcpsContributionYearlyOldHistory.setContribTier2(mstDcpsContributionMonthly.getContribTier2());
					mstDcpsContributionYearlyOldHistory.setIntContribEmp(mstDcpsContributionMonthly.getIntContribEmp());
					mstDcpsContributionYearlyOldHistory.setIntContribEmplr(mstDcpsContributionMonthly.getIntContribEmplr());
					mstDcpsContributionYearlyOldHistory.setIntContribTier2(mstDcpsContributionMonthly.getIntContribTier2());
					mstDcpsContributionYearlyOldHistory.setCloseEmp(0d);
					mstDcpsContributionYearlyOldHistory.setCloseEmplr(0d);
					mstDcpsContributionYearlyOldHistory.setCloseNet(0d);
					mstDcpsContributionYearlyOldHistory.setCloseTier2(0d);
					yearlyDetails.put(mstDcpsContributionMonthly.getDcpsId(), mstDcpsContributionYearlyOldHistory);

					mstDcpsContributionYearlyOldHistory = null;
					mstDcpsContributionYearlyOld = null;
				}
				if(mstDcpsContributionMonthly.getVorcNo()==null){
					mstDcpsContributionMonthly.setVorcNo("9999");
				}
				hitStg.save(mstDcpsContributionMonthly);
			}

			lstMstContriMonth = null;

			InterestCalculationDAO interestCalculationDAO = new InterestCalculationDAOImpl(MstDcpsContribTerminationYearly.class, serv.getSessionFactory());
			//Delete Records From Yearly Contribution Table

			if(dcpsIdForDeletion!=null && dcpsIdForDeletion.size()>0){
				gLogger.info("size dcpsIdForDeletion"+dcpsIdForDeletion.size());

				if(dcpsIdForDeletion!=null && dcpsIdForDeletion.size()>0){
					List tempDcpsEmpId = new ArrayList();
					for(int i = 0; i<dcpsIdForDeletion.size(); i++){
						if(i==0 || i%5000!=0){
							tempDcpsEmpId.add(dcpsIdForDeletion.get(i));
						}

						else{
							tempDcpsEmpId.add(dcpsIdForDeletion.get(i));
                         
							interestCalculationDAO.deleteYearlyIntrstsForGivenEmpList(tempDcpsEmpId, yearId);
							tempDcpsEmpId = null;
							tempDcpsEmpId = new ArrayList();
						}
					}
					interestCalculationDAO.deleteYearlyIntrstsForGivenEmpList(tempDcpsEmpId, yearId);
				}

				//interestCalculationDAO.deleteYearlyIntrstsForGivenEmpList(dcpsIdForDeletion, yearId);
			}
			
			gLogger.info("yearly deleted");	

			dcpsIdForDeletion = null;

			//Inserting Yearly Contributions In MstDcpsContributionYearly

			IdGenerator idGen = new IdGenerator();
			//Long dcpsContributionYearlyId = idGen.PKGenerator("mst_dcps_contribution_yearly", inputMap);
			Long dcpsContributionYearlyId = lObjInterestCalculationDAO.getNextSeqNum("MST_DCPS_CONTRIB_TERMINATION_YEARLY");
			Long dcpsContributionYearlyIdForUpdate = 0l;
			if(yearlyDetails!=null && yearlyDetails.size()>0){
				dcpsContributionYearlyIdForUpdate = dcpsContributionYearlyId + yearlyDetails.size() + 1;
			}


			if(dcpsContributionYearlyIdForUpdate!=0){
				lObjInterestCalculationDAO.updateGeneratedId(Long.valueOf(dcpsContributionYearlyIdForUpdate.toString()),"MST_DCPS_CONTRIB_TERMINATION_YEARLY");
			}

			Iterator itYearltContri = yearlyDetails.keySet().iterator();
			List<MstDcpsContribTerminationYearly> lstMstContriYear = new ArrayList<MstDcpsContribTerminationYearly>();
			interestRatesForVoucherNo =  lObjInterestCalculationDAO.getInterestRatesForVoucherNo(yearId);
			while(itYearltContri.hasNext()){
				MstDcpsContribTerminationYearly mstDcpsContributionYearly = new MstDcpsContribTerminationYearly();
				String dcpsIdEmp = (String)itYearltContri.next();
				MstDcpsContribTerminationYearly mstDcpsContributionYearlyOld= new MstDcpsContribTerminationYearly();

				mstDcpsContributionYearlyOld=  (MstDcpsContribTerminationYearly)(yearlyDetails!=null ? yearlyDetails.get(dcpsIdEmp):null);

				dcpsIdEmp = null;
				String terminationDate=null;
				
				itr = interestRatesForVoucherNo.iterator();
				if(endDateMap!=null && endDateMap.containsKey(mstDcpsContributionYearlyOld.getDcpsId()) && endDateMap.get(mstDcpsContributionYearlyOld.getDcpsId())!=null)
				{
					 terminationDate=endDateMap.get(mstDcpsContributionYearlyOld.getDcpsId()).toString();
				}
				
				
				
					while(itr.hasNext()){
						objRates = (Object[])itr.next();
				        intRate = Double.valueOf((objRates[0] != null) ? Double.parseDouble(objRates[0].toString()) : 0d);
				        effectiveFrom = objRates[1]!=null ? objRates[1].toString() : "";
					    applicableTo  = objRates[2]!=null ? objRates[2].toString() : "";
					    if(terminationDate!=null && !"".equalsIgnoreCase(terminationDate))
					    {
					    	//if(sdf2.parse(terminationDate).after(sdf2.parse(effectiveFrom)))
					    	//{
					    		if(applicableTo!=null && !applicableTo.equals(""))
					    		{
					    			if(sdf2.parse(terminationDate).before(sdf2.parse(applicableTo)))
					    			{
					    				applicableTo=terminationDate;
					    			}
					    		}
					    		else
					    		{
					    			
					    			if(sdf2.parse(terminationDate).before(sdf2.parse(lStrToDatePassed)))
					    			{
					    				applicableTo=terminationDate;
					    			}
					    		}
					    		
					    	//}
					    }
						

						if(applicableTo!=null && !applicableTo.equals("")){
							daysBetween = daysBetween(sdf2.parse(effectiveFrom), sdf2.parse(applicableTo)); 
							if(daysBetween < 0)
							{
								daysBetween=0;
							}
							else if(daysBetween >0)
							{
								daysBetween=daysBetween+1;
							}
						
							openEmployeeInt = openEmployeeInt + ((mstDcpsContributionYearlyOld.getOpenEmp()*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);
							openEmployerInt = openEmployerInt + ((mstDcpsContributionYearlyOld.getOpenEmplr()*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);
							daysBetween = null;
						}
						else
						{
							daysBetween = daysBetween(sdf2.parse(effectiveFrom), sdf2.parse(lStrToDatePassed));
							if(daysBetween < 0)
							{
								daysBetween=0;
							}
							else if(daysBetween >0)
							{
								daysBetween=daysBetween+1;
							}
							openEmployeeInt = openEmployeeInt + ((mstDcpsContributionYearlyOld.getOpenEmp()*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);
							openEmployerInt = openEmployerInt + ((mstDcpsContributionYearlyOld.getOpenEmplr()*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);
							daysBetween = null;
						}

					//	openEmployeeInt= openEmployeeInt+(intRate * mstDcpsContributionYearlyOld.getOpenEmp()  / 100);
					//	openEmployerInt= openEmployerInt+(intRate * mstDcpsContributionYearlyOld.getOpenEmplr() / 100);
						
						intRate = null;
						objRates = null;
						effectiveFrom = null;
						applicableTo = null;
					}
				
		
				
				
				
				openIntBalance=openEmployeeInt+openEmployerInt;
			
				closeEmp=mstDcpsContributionYearlyOld.getOpenEmp() + mstDcpsContributionYearlyOld.getContribEmp() + mstDcpsContributionYearlyOld.getIntContribEmp() + openEmployeeInt + mstDcpsContributionYearlyOld.getContribTier2() + mstDcpsContributionYearlyOld.getIntContribTier2() ;
				closeEmplr=mstDcpsContributionYearlyOld.getOpenEmplr() + mstDcpsContributionYearlyOld.getContribEmplr() + mstDcpsContributionYearlyOld.getIntContribEmplr() + openEmployerInt ;
				closeTier2=mstDcpsContributionYearlyOld.getContribTier2() + mstDcpsContributionYearlyOld.getIntContribTier2() ;
				mstDcpsContributionYearly.setDcpsContributionYearlyId(dcpsContributionYearlyId);
				mstDcpsContributionYearly.setDcpsId(mstDcpsContributionYearlyOld.getDcpsId());
				mstDcpsContributionYearly.setYearId(mstDcpsContributionYearlyOld.getYearId());
				mstDcpsContributionYearly.setCurTreasuryCD(mstDcpsContributionYearlyOld.getCurTreasuryCD());
				mstDcpsContributionYearly.setCurDdoCD(mstDcpsContributionYearlyOld.getCurDdoCD());
				mstDcpsContributionYearly.setOpenNet(mstDcpsContributionYearlyOld.getOpenNet());
				mstDcpsContributionYearly.setOpenEmp(mstDcpsContributionYearlyOld.getOpenEmp());
				mstDcpsContributionYearly.setOpenEmplr(mstDcpsContributionYearlyOld.getOpenEmplr());
				mstDcpsContributionYearly.setOpenTier2(mstDcpsContributionYearlyOld.getOpenTier2());
				mstDcpsContributionYearly.setContribEmp(mstDcpsContributionYearlyOld.getContribEmp());
				mstDcpsContributionYearly.setContribEmplr(mstDcpsContributionYearlyOld.getContribEmplr());
				mstDcpsContributionYearly.setContribTier2(mstDcpsContributionYearlyOld.getContribTier2());
				mstDcpsContributionYearly.setIntContribEmp(mstDcpsContributionYearlyOld.getIntContribEmp());
				mstDcpsContributionYearly.setIntContribEmplr(mstDcpsContributionYearlyOld.getIntContribEmplr());
				mstDcpsContributionYearly.setIntContribTier2(mstDcpsContributionYearlyOld.getIntContribTier2());
				mstDcpsContributionYearly.setOpenInt(Round(openIntBalance,2));
				mstDcpsContributionYearly.setCloseEmp(Round(closeEmp,2));
				mstDcpsContributionYearly.setCloseEmplr(Round(closeEmplr,2));
				mstDcpsContributionYearly.setCloseTier2(Round(closeTier2,2));
				mstDcpsContributionYearly.setCloseNet(Round((closeEmp+closeEmplr),2));
				mstDcpsContributionYearly.setCreatedPostId(1l);
				mstDcpsContributionYearly.setCreatedUserId(1l);
				mstDcpsContributionYearly.setCreatedDate(DBUtility.getCurrentDateFromDB());
				mstDcpsContributionYearly.setUpdatedPostId(null);
				mstDcpsContributionYearly.setUpdatedUserId(null);
				mstDcpsContributionYearly.setUpdatedDate(null);
				mstDcpsContributionYearly.setOpenIntEmp(Round(openEmployeeInt,2));
				mstDcpsContributionYearly.setOpenIntEmplr(Round(openEmployerInt,2));
				lstMstContriYear.add(mstDcpsContributionYearly);

				mstDcpsContributionYearly = null;
				mstDcpsContributionYearlyOld = null;
				dcpsContributionYearlyId=dcpsContributionYearlyId+1;
				openEmployeeInt = 0d;
				openEmployerInt = 0d;
				openIntBalance = 0d;
				//interestCalculationDAO.create(mstDcpsContributionYearly);
			}
			idGen = null;
			itYearltContri = null;
			itr = null;


			/*Long dcpsContributionYearlyIdForUpdate = 0l;
			if(yearlyDetails!=null && yearlyDetails.size()>0){
				dcpsContributionYearlyIdForUpdate = dcpsContributionYearlyId + countYear + 1;
			}


			if(dcpsContributionYearlyIdForUpdate!=0){
				lObjInterestCalculationDAO.updateGeneratedId(Long.valueOf(dcpsContributionYearlyIdForUpdate.toString()),"MST_DCPS_CONTRIBUTION_YEARLY");
			}
			 */

			for(MstDcpsContribTerminationYearly objMstDcpsContributionYearly : lstMstContriYear){

				hitStg.save(objMstDcpsContributionYearly);
			}

			List tempDcpsContriId = new ArrayList();
			
			if(lstDcpsContriId!=null && lstDcpsContriId.size()>0){    
				gLogger.info("size lstDcpsContriId"+lstDcpsContriId.size());	
				for(int i=0 ; i<lstDcpsContriId.size() ; i++){
					if(i==0 || i%5000!=0){
						tempDcpsContriId.add(lstDcpsContriId.get(i));
					}
					else{
						tempDcpsContriId.add(lstDcpsContriId.get(i));
					
						lObjInterestCalculationDAO.updateTrnDcpsContriIntCalculated(tempDcpsContriId);
						tempDcpsContriId = null;
						tempDcpsContriId = new ArrayList();
					}
				}
				lObjInterestCalculationDAO.updateTrnDcpsContriIntCalculated(tempDcpsContriId);
			}
			
			gLogger.info("yearly updated");	

			tempDcpsContriId = null;
			lstMstContriYear = null;
			lObjInterestCalculationDAO = null;
			lstDcpsContriId = null;

			lBlFlag = true;

			String lSBStatus = getResponseXMLDoc(lBlFlag,lStrInterestFor).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
					lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");

			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error in Interest Calculation " + e, e);
			return resObj;
		}

		return resObj;

	}



	public static Integer daysBetween(Date sPassedDate, Date ePassedDate) {

		Calendar sDate = Calendar.getInstance();
		sDate.setTime(sPassedDate);
		Calendar eDate = Calendar.getInstance();
		eDate.setTime(ePassedDate);

		Calendar d = (Calendar) sDate.clone();
		Integer dBetween = 0;
		while (d.before(eDate)) {
			d.add(Calendar.DAY_OF_MONTH, 1);
			dBetween++;
		}
		if(dBetween==0 && d.equals(eDate)){
			dBetween = 1;
		}

		else if(dBetween==0 && d.after(eDate)){
			dBetween = 0;
		}
		return dBetween;
	}

	private StringBuilder getResponseXMLDoc(Boolean flag,String lStrInterestFor) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("<InterestFor>");
		lStrBldXML.append(lStrInterestFor);
		lStrBldXML.append("</InterestFor>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	public static double Round(double Rval, int Rpl) {
		double p = (double)Math.pow(10,Rpl);
		Rval = Rval * p;
		double tmp = Math.round(Rval);
		return (double)tmp/p;
	}

}
