package com.tcs.sgv.dcps.service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.derby.iapi.store.raw.Compensation;

import com.ibm.icu.text.SimpleDateFormat;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.dao.CmnLookupMstDAO;
import com.tcs.sgv.common.dao.CmnLookupMstDAOImpl;
import com.tcs.sgv.common.dao.FinancialYearDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.loan.valueobject.HrLoanAdvMst;
import com.tcs.sgv.common.service.EmployeeRegisterVO;
import com.tcs.sgv.common.service.SupplimentEmployeeReport;
import com.tcs.sgv.common.service.SupplimentstrVO;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.EmployeeCornerDAOImpl;
import com.tcs.sgv.dcps.dao.LedgerReportDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.eis.dao.HrEdpComponentMpgDAOImpl;
import com.tcs.sgv.eis.dao.PayBillDAOImpl;
import com.tcs.sgv.eis.valueobject.HrPayCompoColumnMpg;
import com.tcs.sgv.eis.valueobject.HrPayEdpCompoMpg;
import com.tcs.sgv.eis.valueobject.HrPayGpfBalanceDtls;
import com.tcs.sgv.eis.valueobject.HrPayPaybill;
import com.tcs.sgv.eis.valueobject.HrPayPaybillLoanDtls;

public class LedgerReportServiceImpl extends ServiceImpl {
	private final Log gLogger = LogFactory.getLog(getClass());

	private String gStrPostId = null; /* STRING POST ID */

	private String gStrLocationCode = null;

	private Long gLngPostId = null;

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	private HttpSession session = null; /* SESSION */

	private Date gDtCurDate = null; /* CURRENT DATE */

	private String gStrUserId = null; /* STRING USER ID */

	/* Global Variable for UserId */
	Long gLngUserId = null;

	private Long gLngDBId = null; /* DB ID */

	/* Resource bundle for the constants */
	private ResourceBundle gObjRsrcBndle = ResourceBundle
	.getBundle("resources/dcps/DCPSConstants");


	private void setSessionInfo(Map inputMap) {

		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			session = request.getSession();
			gStrPostId = SessionHelper.getPostId(inputMap).toString();
			gLngPostId = SessionHelper.getPostId(inputMap);
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
			gLngUserId = SessionHelper.getUserId(inputMap);
			gStrUserId = gLngUserId.toString();
			gLngDBId = SessionHelper.getDbId(inputMap);
			gDtCurDate = SessionHelper.getCurDate();
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
		}
	}
	public ResultObject getEmployeeLedgerReport(Map inputMap){

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try {
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
			setSessionInfo(inputMap);
			long langId=StringUtility.convertToLong(loginDetailsMap.get("langId").toString());

			/*CmnLookupMstDAO lookupDAO = new CmnLookupMstDAOImpl(CmnLookupMst.class,serv.getSessionFactory());


			List yearList = lookupDAO.getAllChildrenByLookUpNameAndLang("6PcYear",langId);
			Collections.reverse(yearList);

			inputMap.put("yearList", yearList);*/




			inputMap.put("totalRecords", 0);
			/* Sets the Session Information */


			//setSessionInfo(inputMap);
			resObj.setResultValue(inputMap);
			resObj.setViewName("EmployeeLedger");
			/*
			 * Checks if request is sent by click of GO button or the page is
			 * loaded for the first time.
			 */

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;	

	}
	public ResultObject searchEmployeforLedger(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		List listSavedRequests = null;
		Integer totalRecords = 0;

		try {
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
			setSessionInfo(inputMap);
			//long langId=StringUtility.convertToLong(loginDetailsMap.get("langId").toString());

			//CmnLookupMstDAO lookupDAO = new CmnLookupMstDAOImpl(CmnLookupMst.class,serv.getSessionFactory());



			/*List yearList = lookupDAO.getAllChildrenByLookUpNameAndLang("6PcYear",langId);
			Collections.reverse(yearList);

			inputMap.put("yearList", yearList);*/


			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());

			/* Get Years */
			List lLstYears = lObjDcpsCommonDAO.getFinyears();


			inputMap.put("YEARS", lLstYears);




			LedgerReportDAOImpl lObjEmpLedgerDao = new LedgerReportDAOImpl(
					MstEmp.class, serv.getSessionFactory());
			new DcpsCommonDAOImpl(null, serv.getSessionFactory());



			String lStrSevarthId = StringUtility.getParameter("sevarthId",
					request).trim();

			inputMap.put("lStrSevarthId", lStrSevarthId);

			String lStrEmpName = StringUtility.getParameter("employeeName",
					request).trim();


			/*String year = StringUtility.getParameter("year",
					request);

			inputMap.put("Year", year);*/

			inputMap.put("lStrEmpName", lStrEmpName);

			if (!"".equals(lStrSevarthId) || !"".equals(lStrEmpName)) {
				listSavedRequests = lObjEmpLedgerDao.searchEmpsForLedger(
						lStrSevarthId, lStrEmpName);
			}

			if (listSavedRequests != null) {
				if (listSavedRequests.size() != 0) {
					totalRecords = listSavedRequests.size();
				}
			}

			inputMap.put("totalRecords", totalRecords);
			inputMap.put("CaseList", listSavedRequests);

			resObj.setViewName("EmployeeLedger");
			resObj.setResultValue(inputMap);

		} catch (Exception ex) {
			//ex.printStackTrace();
			gLogger.error("Error is;" + ex, ex);
		}

		return resObj;
	}
	public ResultObject getNameForAutoComplete(Map<String, Object> inputMap) throws Exception {




		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		List finalList  = new ArrayList<ComboValuesVO>();

		List finalListFromname  = new ArrayList<ComboValuesVO>();

		String lStrEmpName = null;

		try 
		{
			setSessionInfo(inputMap);
			LedgerReportDAOImpl lObjEmpLedgerDao = new LedgerReportDAOImpl(
					MstEmp.class, serv.getSessionFactory());
			lStrEmpName = StringUtility.getParameter("searchKey", request)
			.trim();
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");

			long locId = Long.parseLong(loginDetailsMap.get("locationId").toString());
			finalList = lObjEmpLedgerDao.getNameForAutoComplete(lStrEmpName.toUpperCase(),locId);


			gLogger.info("finalList size is **********"+finalList.size());

			String lStrTempResult = null;
			if (finalList != null && finalList.size()>0 ) {
				lStrTempResult = new AjaxXmlBuilder().addItems(finalList, "desc", "id",true).toString();
			}
			gLogger.info("Result------------------------"+lStrTempResult);
			inputMap.put("ajaxKey", lStrTempResult);
			objRes.setResultValue(inputMap);
			objRes.setViewName("ajaxData");

		} catch (Exception ex) {
			objRes.setResultValue(null);
			objRes.setThrowable(ex);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			gLogger.error("Error is: "+ ex.getMessage());
			return objRes;
		}

		return objRes;

	}

	public ResultObject getLedgerReport(Map inputMap) throws Exception {
		setSessionInfo(inputMap);
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		try{

			Calendar cal = Calendar.getInstance();
			//Object objDateOfDeselect=StringUtility.getParameter("year", request);
			String lStrDateOfDeselect = StringUtility.getParameter("year", request);
			//String lStrDateOfDeselect="2012-13";
			gLogger.info("lStrDateOfDeselect in service is ::: "+lStrDateOfDeselect);
			int selMonth = cal.get(Calendar.MONTH)+1;
			gLogger.info("month is "+selMonth);
			int selYear = cal.get(Calendar.YEAR);
			gLogger.info("year is "+selYear);
			FinancialYearDAOImpl financialYearDAOImpl = new FinancialYearDAOImpl(SgvcFinYearMst.class,serv.getSessionFactory());
			long finYrId = Long.parseLong(lStrDateOfDeselect);
			//financialYearDAOImpl.getFinYearId(lStrDateOfDeselect);
			gLogger.info("finYrId"+finYrId);
			List allowValueList = new ArrayList();
			List deducValueList = new ArrayList();
			List loanValueList = new ArrayList();
			List advanceValueList = new ArrayList();
			SgvcFinYearMst sgvcFinYearMst = financialYearDAOImpl.read(finYrId);
			int finYrFrmMnth = 0,finYrFrmYr = 0,finYrToMnth = 0,finYrToYr = 0;
			if(sgvcFinYearMst != null)
			{
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(sgvcFinYearMst.getFromDate());
				finYrFrmMnth = cal1.get(Calendar.MONTH)+1;    //+1 here change
				gLogger.info("finYrFrmMnth ::: "+finYrFrmMnth);
				finYrFrmYr = cal1.get(Calendar.YEAR);
				gLogger.info("finYrFrmYr ::: "+finYrFrmYr);

				cal1.setTime(sgvcFinYearMst.getToDate());
				finYrToMnth = cal1.get(Calendar.MONTH)+1;               //+1 here change
				gLogger.info("finYrToMnth ::: "+finYrToMnth);
				finYrToYr = cal1.get(Calendar.YEAR);
				gLogger.info("finYrToYr ::: "+finYrToYr);
			}
			inputMap.put("finYear"," (April, "+finYrFrmYr+" to March, "+finYrToYr+")");
			String selYrMnths = "4,5,6,7,8,9,10,11,12",selYrPrvYrMnths = "1,2,3";

			/*gLogger.info("selMonth"+selMonth+"finYrFrmMnth"+finYrFrmMnth+"selYear"+selYear+"finYrFrmYr"+finYrFrmYr);
		if(selMonth > finYrFrmMnth && selYear == finYrFrmYr)
		{
			for(int cnt = finYrFrmMnth ; cnt <= selMonth ; cnt++)
			{
				if(selYrMnths != null)
					selYrMnths = selYrMnths + "," + String.valueOf(cnt);
				else
					selYrMnths = String.valueOf(cnt);
			}
		}
		else if(selMonth <= finYrFrmMnth && selYear >= finYrToYr)
		{
			for(int cnt = finYrFrmMnth ; cnt <= 12 ; cnt++)
			{
				if(selYrPrvYrMnths != null)
					selYrPrvYrMnths = selYrPrvYrMnths + "," + String.valueOf(cnt);
				else
					selYrPrvYrMnths = String.valueOf(cnt);
			}
			for(int cnt = 1 ; cnt < selMonth ; cnt++)
			{
				if(selYrMnths != null)
					selYrMnths = selYrMnths + "," + String.valueOf(cnt);
				else
					selYrMnths = String.valueOf(cnt);
			}
		}*/
			gLogger.info("selYrMnths"+selYrMnths);
			Object lStrOrgEmpId=StringUtility.getParameter("orgempId", request);
			Object lStrDcpsEmpId=StringUtility.getParameter("dcpsempId", request);
			String employeName=StringUtility.getParameter("empName", request).toString();
			String sevarthId=StringUtility.getParameter("sevarthId", request).toString();
			String sevId=sevarthId.substring(0, 3).toUpperCase();
			String ddoCode=StringUtility.getParameter("ddoCode", request).toString().trim();
			long OrgempId = (lStrOrgEmpId != null && !lStrOrgEmpId .equals("") )? Long.parseLong(lStrOrgEmpId.toString()) : 0;
			long empId = (lStrDcpsEmpId != null && !lStrDcpsEmpId .equals("") )? Long.parseLong(lStrDcpsEmpId.toString()) : 0;
			List allowChldLst = null;
			List lstSelYrPrvYrMnthsBillData = null,lstAllMnthBillData = new ArrayList();
			List lstAllMnthBrokenData=new ArrayList();
			List basicValueList=new ArrayList();
			List otherCompValueList=new ArrayList();
			List otherstrCompValueList=new ArrayList();
//			/List noOfDaysList=lObjEmpLedgerDao.getBrokenPeriodData(sevarthId,finYrId,noOfDays);			
			LedgerReportDAOImpl lObjEmpLedgerDao = new LedgerReportDAOImpl(
					MstEmp.class, serv.getSessionFactory());

			String yearArray[] = sgvcFinYearMst.getFinYearDesc().split("-");
			long finYrIdemp=Long.parseLong(lObjEmpLedgerDao.getFinYear(finYrId));
			List empDetails = lObjEmpLedgerDao.getEmpDetails(sevarthId);
			String address = "";
			String address1 = "";
			String address2 = "";
			String address3 = "";
			String address4 = "";
			Object Obj1[];
			gLogger.info("empDetails"+empDetails.size());
			if(empDetails!=null && !empDetails.isEmpty()){
				Iterator it =  empDetails.iterator();
				while (it.hasNext()) {
					Obj1 = (Object[]) it.next();
					address=(Obj1[11].toString());					
				}
			}
			gLogger.info("address"+address);
			address=address+insertExtraSpace(address.length(),180);
			address1=address.substring(0, 45);
			address2=address.substring(46, 90);
			address3=address.substring(91,135);
			address4=address.substring(136,180);
			address=address1+"<br>"+address2+"<br>"+address3+"<br>"+address4;
			/*if(address.length()>150){
				address=address.substring(0, 150);
			}*/
			gLogger.info("address with spaces"+address);
			Long finYearLower = Long.parseLong(yearArray[0]);
			Long  finYearUpper= Long.parseLong(yearArray[1]);
			gLogger.info("finYearUpper"+finYearUpper);
			gLogger.info("finYearLower"+finYearLower);
			//List lstSelYrMnthsBillData = lObjEmpLedgerDao.getEmpLastMnthBillData(OrgempId, ddoCode, selYrMnths, selYear); // for the selected date year's months data
			List lstSelYrMnthsBillData = lObjEmpLedgerDao.getEmpLastMnthBillData(OrgempId, ddoCode, selYrMnths, Integer.parseInt(yearArray[0])); // for the selected date year's months data
			//lstAllMnthBillData.addAll(lstSelYrMnthsBillData);

			lstAllMnthBillData.addAll(lstSelYrMnthsBillData);

			List brokenPeriodselYr = lObjEmpLedgerDao.getBrokenPeriodfrYr(OrgempId, selYrMnths, finYrId); // for the selected date year's months data
			List empLoanDetails = lObjEmpLedgerDao.getLoanDetails(sevarthId,finYrIdemp);
			List empLoanDetailsMonthWise= lObjEmpLedgerDao.getLoanDetailsMonthWise(sevarthId,finYearLower,finYearUpper);
			List empGPFDetails=lObjEmpLedgerDao.getGpfDetails(sevarthId, finYrId);
			List empGISDetails=lObjEmpLedgerDao.getGISDetails(sevarthId,finYearLower,finYearUpper);
			List postDetails = lObjEmpLedgerDao.getPostDetails(sevarthId);
			List empTotaldedGross=lObjEmpLedgerDao.getTotalDedandGrass(sevarthId,finYearLower,finYearUpper);
			List lpcIddate=lObjEmpLedgerDao.getLpcIdDate(sevarthId,finYearLower,finYearUpper);
			//Long dcpsContribution= lObjEmpLedgerDao.getYearlyContribution(sevarthId, finYrId);
			gLogger.info("empGPFDetails"+empGPFDetails.size());
			gLogger.info("empTotaldedGross"+empTotaldedGross.size());
			Object Obj[];
			long loanId;
			long advanceAmount;
			long installmentAmount;
			String hbaHouse = "HBAHOUSE";
			String hbaLand = "HBALAND";
			String mca = "MCA";
			String coHosSoc = "COHOUSOC";
			String gpfAdvABC = "GPFADVABC";
			String gpfAdvD = "GPFADVD";
			String gpfIAS = "GPFIAS";
			String computer = "COMPUTER";
			String othervehicle="OTHERVEHICLE";
			String festivalAdvance="FESTIVALADVANCE";
			String otherAdvance="OTHERADVANCE";
			String totalDeduction="TOTALDED";
			Long gisAmount=0l;
			Long totalgisAmount=0l;
			String postlookupId=null;
			String postType=null;
			if(empGISDetails!=null && !empGISDetails.isEmpty()){
				Iterator it =  empGISDetails.iterator();
				while (it.hasNext()) {
					Obj = (Object[]) it.next();
					gisAmount=Long.parseLong(Obj[2].toString());
					totalgisAmount=gisAmount+totalgisAmount;
				}
			}
			gLogger.info("totalgisAmount"+totalgisAmount);
			if(postDetails!=null && !postDetails.isEmpty()){
				Iterator it1 =  postDetails.iterator();
				while (it1.hasNext()) {
					Obj = (Object[]) it1.next();
					postlookupId=(Obj[2].toString());					
				}
			}
			if(postlookupId!=null && postlookupId.equals("10001198129")){
				postType="Permenant";
			}

			else if(postType!=null && postType.equals("10001198130")){
				postType="Temporary";
			}

			Map <Long,EmployeeRegisterVO> loanmap=new HashMap<Long,EmployeeRegisterVO>();
			if(empLoanDetails!=null && !empLoanDetails.isEmpty()){
				//for(Iterator it =  empLoanDetails.iterator();it.hasNext();)
				//{
				Iterator it =  empLoanDetails.iterator();

				while (it.hasNext()) {
					EmployeeRegisterVO loanDetails = new EmployeeRegisterVO();
					Obj = (Object[]) it.next();
					gLogger.info("loanid"+Long.parseLong(Obj[0].toString()));
					gLogger.info("loanid"+Obj[1]);
					gLogger.info("loanid"+Obj[2]);
					gLogger.info("loanid"+Obj[3]);
					gLogger.info("loanid"+Obj[4]);
					loanDetails.setLoanId(Long.parseLong(Obj[0].toString()));
					loanDetails.setAdvanceAmount(Long.parseLong(Obj[1].toString()));
					loanDetails.setInstallmentAmount(Long.parseLong(Obj[2].toString()));
					loanDetails.setInstallmentNo(Long.parseLong(Obj[5].toString()));
					loanDetails.setVoucherNo(Long.parseLong(Obj[3].toString()));
					gLogger.info("Date is "+Obj[4]);
					loanDetails.setVoucherDate(Obj[4].toString());
					if(Obj[7]!=null){
					loanDetails.setSancOrderNo(Obj[7].toString());
					}
					loanDetails.setSancOrderDate(Obj[4].toString());
					loanDetails.setJan(0l);
					loanDetails.setFeb(0l);
					loanDetails.setMarch(0l);
					loanDetails.setApril(0l);
					loanDetails.setMay(0l);
					loanDetails.setJune(0l);
					loanDetails.setJuly(0l);
					loanDetails.setAug(0l);
					loanDetails.setSept(0l);
					loanDetails.setOct(0l);
					loanDetails.setNov(0l);
					loanDetails.setDec(0l);
					loanmap.put(Long.parseLong(Obj[0].toString()), loanDetails);
					gLogger.info("inside loop");
				}

				//}

			}


			if(empLoanDetailsMonthWise!=null && !empLoanDetailsMonthWise.isEmpty()){
				Iterator it =  empLoanDetailsMonthWise.iterator();

				while (it.hasNext()) {
					Obj = (Object[]) it.next();
					int month= Integer.parseInt(Obj[0].toString());

					// HBA House
					String[] hbaHouseArray=Obj[1].toString().split("~");
					if(Integer.parseInt(hbaHouseArray[1])>0){
						Long amount = Long.parseLong(hbaHouseArray[1]);
						Long loanMapId = Long.parseLong((hbaHouseArray[0]));
						EmployeeRegisterVO vo= loanmap.get(loanMapId);
						if(vo!=null){
							switch(month){
							case 1:
								vo.setJan(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);
									gLogger.info("vo.getTotalInstallments()"+vo.getTotalInstallments());
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(hbaHouse,vo);
								break;
							case 2:
								vo.setFeb(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);
									gLogger.info("vo.getTotalInstallments()"+vo.getTotalInstallments());
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(hbaHouse,vo);
								break;
							case 3:
								vo.setMarch(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);
									gLogger.info("vo.getTotalInstallments()"+vo.getTotalInstallments());
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(hbaHouse,vo);
								break;
							case 4:
								vo.setApril(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);
									gLogger.info("vo.getTotalInstallments()"+vo.getTotalInstallments());
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(hbaHouse,vo);
								break;
							case 5:
								vo.setMay(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);
									gLogger.info("vo.getTotalInstallments()"+vo.getTotalInstallments());
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(hbaHouse,vo);
								break;
							case 6:
								vo.setJune(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);
									gLogger.info("vo.getTotalInstallments()"+vo.getTotalInstallments());
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(hbaHouse,vo);
								break;
							case 7:
								vo.setJuly(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);
									gLogger.info("vo.getTotalInstallments()"+vo.getTotalInstallments());
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(hbaHouse,vo);
								break;
							case 8:
								vo.setAug(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);
									gLogger.info("vo.getTotalInstallments()"+vo.getTotalInstallments());
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(hbaHouse,vo);
								break;
							case 9:
								vo.setSept(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);
									gLogger.info("vo.getTotalInstallments()"+vo.getTotalInstallments());
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(hbaHouse,vo);
								break;
							case 10:
								vo.setOct(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);
									gLogger.info("vo.getTotalInstallments()"+vo.getTotalInstallments());
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(hbaHouse,vo);
								break;
							case 11:
								vo.setNov(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);
									gLogger.info("vo.getTotalInstallments()"+vo.getTotalInstallments());
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(hbaHouse,vo);
								break;
							case 12:
								vo.setDec(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);
									gLogger.info("vo.getTotalInstallments()"+vo.getTotalInstallments());
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(hbaHouse,vo);
								break;
							default:
								gLogger.info("Incorrect Record");
							break;
							}
						}
					}

					// HBA Land
					String[] hbaLandArray=Obj[2].toString().split("~");
					if(Integer.parseInt(hbaLandArray[1])>0){
						Long amount = Long.parseLong(hbaLandArray[1]);
						Long loanMapId = Long.parseLong((hbaLandArray[0]));
						EmployeeRegisterVO vo= loanmap.get(loanMapId);
						if(vo!=null){
							switch(month){
							case 1:
								vo.setJan(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);
									gLogger.info("vo.getTotalInstallments()"+vo.getTotalInstallments());
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(hbaLand,vo);
								break;
							case 2:
								vo.setFeb(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);
									gLogger.info("vo.getTotalInstallments()"+vo.getTotalInstallments());
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(hbaLand,vo);
								break;
							case 3:
								vo.setMarch(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);
									gLogger.info("vo.getTotalInstallments()"+vo.getTotalInstallments());
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(hbaLand,vo);
								break;
							case 4:
								vo.setApril(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);
									gLogger.info("vo.getTotalInstallments()"+vo.getTotalInstallments());
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(hbaLand,vo);
								break;
							case 5:
								vo.setMay(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(hbaLand,vo);
								break;
							case 6:
								vo.setJune(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(hbaLand,vo);
								break;
							case 7:
								vo.setJuly(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(hbaLand,vo);
								break;
							case 8:
								vo.setAug(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(hbaLand,vo);
								break;
							case 9:
								vo.setSept(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(hbaLand,vo);
								break;
							case 10:
								vo.setOct(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(hbaLand,vo);
								break;
							case 11:
								vo.setNov(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(hbaLand,vo);
								break;
							case 12:
								vo.setDec(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(hbaLand,vo);
								break;
							default:
								gLogger.info("Incorrect Record");
							break;
							}
						}
					}

					// GPF Adv ABC
					String[] gpfAdvABCArray=Obj[3].toString().split("~");
					if(Integer.parseInt(gpfAdvABCArray[1])>0){
						Long amount = Long.parseLong(gpfAdvABCArray[1]);
						Long loanMapId = Long.parseLong((gpfAdvABCArray[0]));
						EmployeeRegisterVO vo= loanmap.get(loanMapId);
						if(vo!=null){
							switch(month){
							case 1:
								vo.setJan(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfAdvABC,vo);
								break;
							case 2:
								vo.setFeb(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfAdvABC,vo);
								break;
							case 3:
								vo.setMarch(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfAdvABC,vo);
								break;
							case 4:
								vo.setApril(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfAdvABC,vo);
								break;
							case 5:
								vo.setMay(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfAdvABC,vo);
								break;
							case 6:
								vo.setJune(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfAdvABC,vo);
								break;
							case 7:
								vo.setJuly(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfAdvABC,vo);
								break;
							case 8:
								vo.setAug(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfAdvABC,vo);
								break;
							case 9:
								vo.setSept(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfAdvABC,vo);
								break;
							case 10:
								vo.setOct(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfAdvABC,vo);
								break;
							case 11:
								vo.setNov(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfAdvABC,vo);
								break;
							case 12:
								vo.setDec(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfAdvABC,vo);
								break;
							default:
								gLogger.info("Incorrect Record");
							break;
							}
						}
					}

					// GPF Adv D
					String[] gpfAdvDArray=Obj[4].toString().split("~");
					if(Integer.parseInt(gpfAdvDArray[1])>0){
						Long amount = Long.parseLong(gpfAdvDArray[1]);
						Long loanMapId = Long.parseLong((gpfAdvDArray[0]));
						EmployeeRegisterVO vo= loanmap.get(loanMapId);
						if(vo!=null){
							switch(month){
							case 1:
								vo.setJan(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfAdvD,vo);
								break;
							case 2:
								vo.setFeb(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfAdvD,vo);
								break;
							case 3:
								vo.setMarch(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfAdvD,vo);
								break;
							case 4:
								vo.setApril(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfAdvD,vo);
								break;
							case 5:
								vo.setMay(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfAdvD,vo);
								break;
							case 6:
								vo.setJune(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfAdvD,vo);
								break;
							case 7:
								vo.setJuly(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfAdvD,vo);
								break;
							case 8:
								vo.setAug(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfAdvD,vo);
								break;
							case 9:
								vo.setSept(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfAdvD,vo);
								break;
							case 10:
								vo.setOct(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfAdvD,vo);
								break;
							case 11:
								vo.setNov(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfAdvD,vo);
								break;
							case 12:
								vo.setDec(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfAdvD,vo);
								break;
							default:
								gLogger.info("Incorrect Record");
							break;
							}
						}
					}

					// MCA
					String[] mcaArray=Obj[5].toString().split("~");
					gLogger.info("mcaArray"+mcaArray);
					if(Integer.parseInt(mcaArray[1])>0){
						Long amount = Long.parseLong(mcaArray[1]);
						gLogger.info("mcaArray"+amount);
						Long loanMapId = Long.parseLong((mcaArray[0]));
						EmployeeRegisterVO vo= loanmap.get(loanMapId);
						if(vo!=null){
							switch(month){
							case 1:
								vo.setJan(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(mca,vo);
								break;
							case 2:
								vo.setFeb(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(mca,vo);
								break;
							case 3:
								vo.setMarch(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(mca,vo);
								break;
							case 4:
								vo.setApril(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(mca,vo);
								break;
							case 5:
								vo.setMay(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(mca,vo);
								break;
							case 6:
								vo.setJune(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(mca,vo);
								break;
							case 7:
								vo.setJuly(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(mca,vo);
								break;
							case 8:
								vo.setAug(amount);
								if(amount>0){
									gLogger.info("*1***"+vo.getTotalInstallments());
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
									gLogger.info("2****"+vo.getTotalInstallments());
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(mca,vo);
								break;
							case 9:
								vo.setSept(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(mca,vo);
								break;
							case 10:
								vo.setOct(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(mca,vo);
								break;
							case 11:
								vo.setNov(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(mca,vo);
								break;
							case 12:
								vo.setDec(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(mca,vo);
								break;
							default:
								gLogger.info("Incorrect Record");
							break;
							}
						}
					}

					// Other Vehicle Advance
					String[] otherVehicleArray=Obj[6].toString().split("~");
					if(Integer.parseInt(otherVehicleArray[1])>0){
						Long amount = Long.parseLong(otherVehicleArray[1]);
						Long loanMapId = Long.parseLong((otherVehicleArray[0]));
						EmployeeRegisterVO vo= loanmap.get(loanMapId);
						if(vo!=null){
							switch(month){
							case 1:
								vo.setJan(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(othervehicle,vo);
								break;
							case 2:
								vo.setFeb(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(othervehicle,vo);
								break;
							case 3:
								vo.setMarch(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(othervehicle,vo);
								break;
							case 4:
								vo.setApril(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(othervehicle,vo);
								break;
							case 5:
								vo.setMay(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(othervehicle,vo);
								break;
							case 6:
								vo.setJune(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(othervehicle,vo);
								break;
							case 7:
								vo.setJuly(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(othervehicle,vo);
								break;
							case 8:
								vo.setAug(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(othervehicle,vo);
								break;
							case 9:
								vo.setSept(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(othervehicle,vo);
								break;
							case 10:
								vo.setOct(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(othervehicle,vo);
								break;
							case 11:
								vo.setNov(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(othervehicle,vo);
								break;
							case 12:
								vo.setDec(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(othervehicle,vo);
								break;
							default:
								gLogger.info("Incorrect Record");
							break;
							}
						}
					}

					// Computer Advance
					String[] computerArray=Obj[7].toString().split("~");
					if(Integer.parseInt(computerArray[1])>0){
						Long amount = Long.parseLong(computerArray[1]);
						Long loanMapId = Long.parseLong((computerArray[0]));
						EmployeeRegisterVO vo= loanmap.get(loanMapId);
						if(vo!=null){
							switch(month){
							case 1:
								vo.setJan(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(computer,vo);
								break;
							case 2:
								vo.setFeb(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(computer,vo);
								break;
							case 3:
								vo.setMarch(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(computer,vo);
								break;
							case 4:
								vo.setApril(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(computer,vo);
								break;
							case 5:
								vo.setMay(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(computer,vo);
								break;
							case 6:
								vo.setJune(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(computer,vo);
								break;
							case 7:
								vo.setJuly(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(computer,vo);
								break;
							case 8:
								vo.setAug(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(computer,vo);
								break;
							case 9:
								vo.setSept(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(computer,vo);
								break;
							case 10:
								vo.setOct(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(computer,vo);
								break;
							case 11:
								vo.setNov(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(computer,vo);
								break;
							case 12:
								vo.setDec(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(computer,vo);
								break;
							default:
								gLogger.info("Incorrect Record");
							break;
							}
						}
					}

					// Festival Advance
					String[] faArray=Obj[8].toString().split("~");
					if(Integer.parseInt(faArray[1])>0){
						Long amount = Long.parseLong(faArray[1]);
						Long loanMapId = Long.parseLong((faArray[0]));
						EmployeeRegisterVO vo= loanmap.get(loanMapId);
						if(vo!=null){
							switch(month){
							case 1:
								vo.setJan(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(festivalAdvance,vo);
								break;
							case 2:
								vo.setFeb(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(festivalAdvance,vo);
								break;
							case 3:
								vo.setMarch(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(festivalAdvance,vo);
								break;
							case 4:
								vo.setApril(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(festivalAdvance,vo);
								break;
							case 5:
								vo.setMay(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(festivalAdvance,vo);
								break;
							case 6:
								vo.setJune(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(festivalAdvance,vo);
								break;
							case 7:
								vo.setJuly(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(festivalAdvance,vo);
								break;
							case 8:
								vo.setAug(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(festivalAdvance,vo);
								break;
							case 9:
								vo.setSept(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(festivalAdvance,vo);
								break;
							case 10:
								vo.setOct(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(festivalAdvance,vo);
								break;
							case 11:
								vo.setNov(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(festivalAdvance,vo);
								break;
							case 12:
								vo.setDec(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(festivalAdvance,vo);
								break;
							default:
								gLogger.info("Incorrect Record");
							break;
							}
						}
					}

					// Other Advance
					String[] otherAdvArray=Obj[9].toString().split("~");
					if(Integer.parseInt(otherAdvArray[1])>0){
						Long amount = Long.parseLong(otherAdvArray[1]);
						Long loanMapId = Long.parseLong((otherAdvArray[0]));
						EmployeeRegisterVO vo= loanmap.get(loanMapId);
						if(vo!=null){
							switch(month){
							case 1:
								vo.setJan(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(otherAdvance,vo);
								break;
							case 2:
								vo.setFeb(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(otherAdvance,vo);
								break;
							case 3:
								vo.setMarch(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(otherAdvance,vo);
								break;
							case 4:
								vo.setApril(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(otherAdvance,vo);
								break;
							case 5:
								vo.setMay(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(otherAdvance,vo);
								break;
							case 6:
								vo.setJune(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(otherAdvance,vo);
								break;
							case 7:
								vo.setJuly(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(otherAdvance,vo);
								break;
							case 8:
								vo.setAug(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(otherAdvance,vo);
								break;
							case 9:
								vo.setSept(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(otherAdvance,vo);
								break;
							case 10:
								vo.setOct(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(otherAdvance,vo);
								break;
							case 11:
								vo.setNov(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(otherAdvance,vo);
								break;
							case 12:
								vo.setDec(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(otherAdvance,vo);
								break;
							default:
								gLogger.info("Incorrect Record");
							break;
							}
						}
					}

					// Co HOS SOC
					int coHouSocCoveredinstCount=0;
					String[] coHouSocArray=Obj[10].toString().split("~");
					if(Integer.parseInt(coHouSocArray[1])>0){
						Long amount = Long.parseLong(coHouSocArray[1]);						
						Long loanMapId = Long.parseLong((coHouSocArray[0]));
						EmployeeRegisterVO vo= loanmap.get(loanMapId);
						if(vo!=null){
							switch(month){
							case 1:
								vo.setJan(amount);
								if(amount>0){									
									vo.setTotalInstallments(vo.getTotalInstallments()+1);
									gLogger.info("vo.getTotalInstallments()"+vo.getTotalInstallments());
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(coHosSoc,vo);
								break;
							case 2:
								vo.setFeb(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);
									gLogger.info("vo.getTotalInstallments()"+vo.getTotalInstallments());
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(coHosSoc,vo);
								break;
							case 3:
								vo.setMarch(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);
									gLogger.info("vo.getTotalInstallments()"+vo.getTotalInstallments());
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(coHosSoc,vo);
								break;
							case 4:
								vo.setApril(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);
									gLogger.info("vo.getTotalInstallments()"+vo.getTotalInstallments());
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(coHosSoc,vo);
								break;
							case 5:
								vo.setMay(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);
									gLogger.info("vo.getTotalInstallments()"+vo.getTotalInstallments());
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(coHosSoc,vo);
								break;
							case 6:
								vo.setJune(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);
									gLogger.info("vo.getTotalInstallments()"+vo.getTotalInstallments());
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(coHosSoc,vo);
								break;
							case 7:
								vo.setJuly(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);
									gLogger.info("vo.getTotalInstallments()"+vo.getTotalInstallments());
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(coHosSoc,vo);
								break;
							case 8:
								vo.setAug(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);
									gLogger.info("vo.getTotalInstallments()"+vo.getTotalInstallments());
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(coHosSoc,vo);
								break;
							case 9:
								vo.setSept(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);
									gLogger.info("vo.getTotalInstallments()"+vo.getTotalInstallments());
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(coHosSoc,vo);
								break;
							case 10:
								vo.setOct(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);
									gLogger.info("vo.getTotalInstallments()"+vo.getTotalInstallments());
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(coHosSoc,vo);
								break;
							case 11:
								vo.setNov(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);
									gLogger.info("vo.getTotalInstallments()"+vo.getTotalInstallments());
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(coHosSoc,vo);
								break;
							case 12:
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);
									gLogger.info("vo.getTotalInstallments()"+vo.getTotalInstallments());
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(coHosSoc,vo);
								break;
							default:
								gLogger.info("Incorrect Record");
							break;
							}
						}
					}
					gLogger.info("coHouSocCoveredinstCount after loop"+coHouSocCoveredinstCount);

					// GPF IAS
					String[] gpfIASArray=Obj[11].toString().split("~");
					if(Integer.parseInt(gpfIASArray[1])>0){
						Long amount = Long.parseLong(gpfIASArray[1]);
						Long loanMapId = Long.parseLong((gpfIASArray[0]));
						EmployeeRegisterVO vo= loanmap.get(loanMapId);
						if(vo!=null){
							switch(month){
							case 1:
								vo.setJan(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfIAS,vo);
								break;
							case 2:
								vo.setFeb(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfIAS,vo);
								break;
							case 3:
								vo.setMarch(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfIAS,vo);
								break;
							case 4:
								vo.setApril(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfIAS,vo);
								break;
							case 5:
								vo.setMay(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfIAS,vo);
								break;
							case 6:
								vo.setJune(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfIAS,vo);
								break;
							case 7:
								vo.setJuly(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfIAS,vo);
								break;
							case 8:
								vo.setAug(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfIAS,vo);
								break;
							case 9:
								vo.setSept(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfIAS,vo);
								break;
							case 10:
								vo.setOct(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfIAS,vo);
								break;
							case 11:
								vo.setNov(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfIAS,vo);
								break;
							case 12:
								vo.setDec(amount);
								if(amount>0){
									vo.setTotalInstallments(vo.getTotalInstallments()+1);	
								}
								loanmap.put(loanMapId, vo);
								inputMap.put(gpfIAS,vo);
								break;
							default:
								gLogger.info("Incorrect Record");
							break;
							}
						}
					}
					//total Deductions

					String[] totalDedArray=Obj[12].toString().split("~");
					if(Integer.parseInt(totalDedArray[1])>0){
						Long amount = Long.parseLong(totalDedArray[1]);
						gLogger.info("total ded amount"+amount);
						Long loanMapId = Long.parseLong((totalDedArray[0]));

						EmployeeRegisterVO vo= loanmap.get(loanMapId);
						if(vo!=null){
							switch(month){
							case 1:
								vo.setJan(amount);								
								loanmap.put(loanMapId, vo);
								inputMap.put(totalDeduction,vo);
								break;
							case 2:
								vo.setFeb(amount);								
								loanmap.put(loanMapId, vo);
								inputMap.put(totalDeduction,vo);
								break;
							case 3:
								vo.setMarch(amount);								
								loanmap.put(loanMapId, vo);
								inputMap.put(totalDeduction,vo);
								break;
							case 4:
								vo.setApril(amount);								
								loanmap.put(loanMapId, vo);
								inputMap.put(totalDeduction,vo);
								break;
							case 5:
								vo.setMay(amount);								
								loanmap.put(loanMapId, vo);
								inputMap.put(totalDeduction,vo);
								break;
							case 6:
								vo.setJune(amount);								
								loanmap.put(loanMapId, vo);
								inputMap.put(totalDeduction,vo);
								break;
							case 7:
								vo.setJuly(amount);								
								loanmap.put(loanMapId, vo);
								inputMap.put(totalDeduction,vo);
								break;
							case 8:
								vo.setAug(amount);								
								loanmap.put(loanMapId, vo);
								inputMap.put(totalDeduction,vo);
								break;
							case 9:
								vo.setSept(amount);								
								loanmap.put(loanMapId, vo);
								inputMap.put(totalDeduction,vo);
								break;
							case 10:
								vo.setOct(amount);								
								loanmap.put(loanMapId, vo);
								inputMap.put(totalDeduction,vo);
								break;
							case 11:
								vo.setNov(amount);								
								loanmap.put(loanMapId, vo);
								inputMap.put(totalDeduction,vo);
								break;
							case 12:
								vo.setDec(amount);								
								loanmap.put(loanMapId, vo);
								inputMap.put(totalDeduction,vo);
								break;
							default:
								gLogger.info("Incorrect Record");
							break;
							}
						}
					}

				}

			}

			gLogger.info("empLoanDetails"+empLoanDetails.size());



			if(lstAllMnthBillData != null && !lstAllMnthBillData.isEmpty())
			{
				Long allowCompoId=2500134l;
				Map<String,String> allowNormalMap=new HashMap();
				String strAllowValues="";

				//allowance for normal
				List allowIdList=lObjEmpLedgerDao.getallowListIdNormal(sevarthId,finYearLower,finYearUpper,allowCompoId);
				gLogger.info("allowIdList normal"+allowIdList.size());				
				if(allowIdList != null && !allowIdList.isEmpty())
				{				

					for(int i=0;i<allowIdList.size();i++){
						String arrAllowIdList[]=allowIdList.get(i).toString().split(",");
						gLogger.info("strAllowIdList "+arrAllowIdList.length);
						for(int j=0;j<arrAllowIdList.length;j++){
							gLogger.info("strAllowIdList[j] values "+arrAllowIdList[j].toString());
							if(!allowNormalMap.containsKey(arrAllowIdList[j].toString())){
								allowNormalMap.put(arrAllowIdList[j].toString(), arrAllowIdList[j].toString());
							}

						}

					}
				
				for (Map.Entry<String, String> entry : allowNormalMap.entrySet()) {
					gLogger.info(entry.getKey()+" : "+entry.getValue());
					strAllowValues=strAllowValues+entry.getKey()+",";
				}
				strAllowValues=strAllowValues.substring(0, strAllowValues.length()-1);
				gLogger.info("noraml allow string"+strAllowValues);

				List allowNameList=lObjEmpLedgerDao.getallowNames(strAllowValues,allowCompoId);

				for(int i= 0;i<allowNameList.size();i++){
					String allowName=allowNameList.get(i).toString();
					List allowValues=lObjEmpLedgerDao.getAllowValues(sevarthId,finYearLower,finYearUpper,allowName);
					gLogger.info("noraml allowvalue size"+allowValues.size());
					SupplimentEmployeeReport allowValue=new SupplimentEmployeeReport();
					allowValue.setMonth(allowName);
					for(int j= 0;j<allowValues.size();j++){

						String[] allowAmount=allowValues.get(j).toString().split("~");					
						Long amount = Long.parseLong(allowAmount[0].toString());
						gLogger.info("total ded amount"+amount);
						int monthValue =Integer.parseInt((allowAmount[1]));
						switch(monthValue){
						case 1:
							if(amount>0){
								allowValue.setJan(amount);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							else{
								allowValue.setJan(0l);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							break;
						case 2:
							if(amount>0){
								allowValue.setFeb(amount);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							else{
								allowValue.setFeb(0l);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							break;
						case 3:
							if(amount>0){
								allowValue.setMarch(amount);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							else{
								allowValue.setMarch(0l);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							break;
						case 4:
							if(amount>0){
								allowValue.setApril(amount);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							else
								allowValue.setApril(0l);
							//viewMap.put("Total Deduction", monthValueMap);
							break;
						case 5:
							if(amount>0)
								allowValue.setMay(amount);
							else
								allowValue.setMay(0l);
							break;
						case 6:
							if(amount>0)
								allowValue.setJune(amount);
							else
								allowValue.setJune(0l);
							break;
						case 7:
							if(amount>0)
								allowValue.setJuly(amount);
							else
								allowValue.setJuly(0l);
							break;
						case 8:
							if(amount>0)
								allowValue.setAug(amount);
							else
								allowValue.setAug(0l);
							break;
						case 9:
							if(amount>0)
								allowValue.setSept(amount);
							else
								allowValue.setSept(0l);
							break;
						case 10:
							if(amount>0)
								allowValue.setOct(amount);
							else
								allowValue.setOct(0l);
							break;
						case 11:
							if(amount>0)
								allowValue.setNov(amount);
							else
								allowValue.setNov(0l);
							break;
						case 12:
							if(amount>0)
								allowValue.setDec(amount);
							else
								allowValue.setDec(0l);
							break;
						default:
							gLogger.info("Incorrect Record");
						break;							




						}					

					}
					allowValueList.add(allowValue);
				}
				}
				
				
				//deductions Regular
				
				
				
				Long deducCompoId=2500135l;
				Map<String,String> deducRegularMap=new HashMap();
				String strDeducValues="";

				//allowance for normal
				List deducIdList=lObjEmpLedgerDao.getallowListIdNormal(sevarthId,finYearLower,finYearUpper,deducCompoId);
				gLogger.info("deducIdList normal"+deducIdList.size());

				if(deducIdList != null && !deducIdList.isEmpty())
				{				

					for(int i=0;i<deducIdList.size();i++){
						String arrdeducIdList[]=deducIdList.get(i).toString().split(",");
						gLogger.info("arrdeducIdList "+arrdeducIdList.length);
						for(int j=0;j<arrdeducIdList.length;j++){
							gLogger.info("arrdeducIdList[j] values "+arrdeducIdList[j].toString());
							if(!deducRegularMap.containsKey(arrdeducIdList[j].toString())){
								deducRegularMap.put(arrdeducIdList[j].toString(), arrdeducIdList[j].toString());
							}

						}

					}
				}	
				
				for (Map.Entry<String, String> entry : deducRegularMap.entrySet()) {
					gLogger.info(entry.getKey()+" : "+entry.getValue());
					strDeducValues=strDeducValues+entry.getKey()+",";
				}
				strDeducValues=strDeducValues.substring(0, strDeducValues.length()-1);
				gLogger.info("regular deduction string"+strDeducValues);

				List deducNameList=lObjEmpLedgerDao.getallowNames(strDeducValues,deducCompoId);
				gLogger.info("regular deduction deducNameList"+deducNameList.size());
				for(int i= 0;i<deducNameList.size();i++){
					String deducName=deducNameList.get(i).toString();
					List deducValues=lObjEmpLedgerDao.getAllowValues(sevarthId,finYearLower,finYearUpper,deducName);
					gLogger.info("noraml deducValues size"+deducValues.size());
					SupplimentEmployeeReport deducValue=new SupplimentEmployeeReport();
					deducValue.setMonth(deducName);
					for(int j= 0;j<deducValues.size();j++){

						String[] deducAmount=deducValues.get(j).toString().split("~");					
						Long amount = Long.parseLong(deducAmount[0].toString());
						gLogger.info("total ded amount"+amount);
						int monthValue =Integer.parseInt((deducAmount[1]));
						switch(monthValue){
						case 1:
							if(amount>0){
								deducValue.setJan(amount);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							else{
								deducValue.setJan(0l);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							break;
						case 2:
							if(amount>0){
								deducValue.setFeb(amount);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							else{
								deducValue.setFeb(0l);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							break;
						case 3:
							if(amount>0){
								deducValue.setMarch(amount);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							else{
								deducValue.setMarch(0l);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							break;
						case 4:
							if(amount>0){
								deducValue.setApril(amount);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							else
								deducValue.setApril(0l);
							//viewMap.put("Total Deduction", monthValueMap);
							break;
						case 5:
							if(amount>0)
								deducValue.setMay(amount);
							else
								deducValue.setMay(0l);
							break;
						case 6:
							if(amount>0)
								deducValue.setJune(amount);
							else
								deducValue.setJune(0l);
							break;
						case 7:
							if(amount>0)
								deducValue.setJuly(amount);
							else
								deducValue.setJuly(0l);
							break;
						case 8:
							if(amount>0)
								deducValue.setAug(amount);
							else
								deducValue.setAug(0l);
							break;
						case 9:
							if(amount>0)
								deducValue.setSept(amount);
							else
								deducValue.setSept(0l);
							break;
						case 10:
							if(amount>0)
								deducValue.setOct(amount);
							else
								deducValue.setOct(0l);
							break;
						case 11:
							if(amount>0)
								deducValue.setNov(amount);
							else
								deducValue.setNov(0l);
							break;
						case 12:
							if(amount>0)
								deducValue.setDec(amount);
							else
								deducValue.setDec(0l);
							break;
						default:
							gLogger.info("Incorrect Record");
						break;							




						}					

					}
					deducValueList.add(deducValue);
				}
			
				
				//loans for regular			
			
				
				Long loanCompoId=2500137l;
		
				Map<String,String> loanRegularMap=new HashMap();
				String strloanValues="";

				//allowance for normal
				List loanIdList=lObjEmpLedgerDao.getallowListIdNormal(sevarthId,finYearLower,finYearUpper,loanCompoId);
				gLogger.info("deducIdList normal"+loanIdList.size());

				if(loanIdList != null && !loanIdList.isEmpty())
				{				

					for(int i=0;i<loanIdList.size();i++){
						String arrloanIdList[]=loanIdList.get(i).toString().split(",");
						gLogger.info("arrdeducIdList "+arrloanIdList.length);
						for(int j=0;j<arrloanIdList.length;j++){
							gLogger.info("arrdeducIdList[j] values "+arrloanIdList[j].toString());
							if(!loanRegularMap.containsKey(arrloanIdList[j].toString())){
								loanRegularMap.put(arrloanIdList[j].toString(), arrloanIdList[j].toString());
							}

						}

					}
				
				for (Map.Entry<String, String> entry : loanRegularMap.entrySet()) {
					gLogger.info(entry.getKey()+" : "+entry.getValue());
					strloanValues=strloanValues+entry.getKey()+",";
				}
				strloanValues=strloanValues.substring(0, strloanValues.length()-1);
				gLogger.info("regular strloanValues string"+strloanValues);

				List loanNameList=lObjEmpLedgerDao.getallowNames(strloanValues,loanCompoId);
				gLogger.info("regular deduction deducNameList"+loanNameList.size());
				for(int i= 0;i<loanNameList.size();i++){
					String loanName=loanNameList.get(i).toString();
					//List loanValues=lObjEmpLedgerDao.getAllowValues(sevarthId,finYearLower,finYearUpper,loanName);
					List instalrecList=lObjEmpLedgerDao.getInstallRecValues(sevarthId,finYearLower,finYearUpper,loanName);
					gLogger.info("noraml deducValues size"+instalrecList.size());
					SupplimentstrVO loanValue=new SupplimentstrVO();
					loanValue.setMonth(loanName);					
					for(int j= 0;j<instalrecList.size();j++){

						String[] loanAmount=instalrecList.get(j).toString().split("~");					
						Long amount = Long.parseLong(loanAmount[0].toString());
						gLogger.info("total ded amount"+amount);
						int recoverdInst =Integer.parseInt((loanAmount[1]));
						int totalInst =Integer.parseInt((loanAmount[2]));
						int monthValue=Integer.parseInt((loanAmount[3]));
					String	value=recoverdInst +"/"+ totalInst + " " +amount;
						switch(monthValue){
						case 1:
							if(value!=null && value!=""){
								loanValue.setJan(value);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							else{
								loanValue.setJan("-");
								//viewMap.put("Total Deduction", monthValueMap);
							}
							break;
						case 2:
							if(value!=null && value!=""){
								loanValue.setFeb(value);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							else{
								loanValue.setFeb("-");
								//viewMap.put("Total Deduction", monthValueMap);
							}
							break;
						case 3:
							if(value!=null && value!=""){
								loanValue.setMarch(value);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							else{
								loanValue.setMarch("-");
								//viewMap.put("Total Deduction", monthValueMap);
							}
							break;
						case 4:
							if(value!=null && value!=""){
								loanValue.setApril(value);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							else
								loanValue.setApril("-");
							//viewMap.put("Total Deduction", monthValueMap);
							break;
						case 5:
							if(value!=null && value!="")
								loanValue.setMay(value);
							else
								loanValue.setMay("-");
							break;
						case 6:
							if(value!=null && value!="")
								loanValue.setJune(value);
							else
								loanValue.setJune("-");
							break;
						case 7:
							if(value!=null && value!="")
								loanValue.setJuly(value);
							else
								loanValue.setJuly("-");
							break;
						case 8:
							if(value!=null && value!="")
								loanValue.setAug(value);
							else
								loanValue.setAug("-");
							break;
						case 9:
							if(value!=null && value!="")
								loanValue.setSept(value);
							else
								loanValue.setSept("-");
							break;
						case 10:
							if(value!=null && value!="")
								loanValue.setOct(value);
							else
								loanValue.setOct("-");
							break;
						case 11:
							if(value!=null && value!="")
								loanValue.setNov(value);
							else
								loanValue.setNov("-");
							break;
						case 12:
							if(value!=null && value!="")
								loanValue.setDec(value);
							else
								loanValue.setDec("-");
							break;
						default:
							gLogger.info("Incorrect Record");
						break;							




						}					

					}
				
					loanValueList.add(loanValue);
				}
				}		

				//advances for regular			
			
				
				Long advCompoId=2500136l;
		
				Map<String,String> advRegularMap=new HashMap();
				String strAdvValues="";

				//allowance for normal
				List advIdList=lObjEmpLedgerDao.getallowListIdNormal(sevarthId,finYearLower,finYearUpper,advCompoId);
				gLogger.info("advIdList normal"+advIdList.size());

				if(advIdList != null && !advIdList.isEmpty())
				{				

					for(int i=0;i<advIdList.size();i++){
						String arradvIdList[]=advIdList.get(i).toString().split(",");
						gLogger.info("arradvIdList "+arradvIdList.length);
						for(int j=0;j<arradvIdList.length;j++){
							gLogger.info("arradvIdList[j] values "+arradvIdList[j].toString());
							if(!advRegularMap.containsKey(arradvIdList[j].toString())){
								advRegularMap.put(arradvIdList[j].toString(), arradvIdList[j].toString());
							}

						}

					}
				
				for (Map.Entry<String, String> entry : advRegularMap.entrySet()) {
					gLogger.info(entry.getKey()+" : "+entry.getValue());
					strAdvValues=strAdvValues+entry.getKey()+",";
				}
				strAdvValues=strAdvValues.substring(0, strAdvValues.length()-1);
				gLogger.info("regular strAdvValues string"+strAdvValues);

				List advNameList=lObjEmpLedgerDao.getallowNames(strAdvValues,advCompoId);
				gLogger.info("regular deduction deducNameList"+advNameList.size());
				for(int i= 0;i<advNameList.size();i++){
					String advName=advNameList.get(i).toString();
					List advValues=lObjEmpLedgerDao.getAllowValues(sevarthId,finYearLower,finYearUpper,advName);
					gLogger.info("noraml advValues size"+advValues.size());
					SupplimentEmployeeReport advValue=new SupplimentEmployeeReport();
					advValue.setMonth(advName);
					for(int j= 0;j<advValues.size();j++){
						
						String[] advAmount=advValues.get(j).toString().split("~");					
						Long amount = Long.parseLong(advAmount[0].toString());
						gLogger.info("total ded amount"+amount);
						int monthValue =Integer.parseInt((advAmount[1]));
						switch(monthValue){
						case 1:
							if(amount>0){
								advValue.setJan(amount);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							else{
								advValue.setJan(0l);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							break;
						case 2:
							if(amount>0){
								advValue.setFeb(amount);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							else{
								advValue.setFeb(0l);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							break;
						case 3:
							if(amount>0){
								advValue.setMarch(amount);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							else{
								advValue.setMarch(0l);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							break;
						case 4:
							if(amount>0){
								advValue.setApril(amount);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							else
								advValue.setApril(0l);
							//viewMap.put("Total Deduction", monthValueMap);
							break;
						case 5:
							if(amount>0)
								advValue.setMay(amount);
							else
								advValue.setMay(0l);
							break;
						case 6:
							if(amount>0)
								advValue.setJune(amount);
							else
								advValue.setJune(0l);
							break;
						case 7:
							if(amount>0)
								advValue.setJuly(amount);
							else
								advValue.setJuly(0l);
							break;
						case 8:
							if(amount>0)
								advValue.setAug(amount);
							else
								advValue.setAug(0l);
							break;
						case 9:
							if(amount>0)
								advValue.setSept(amount);
							else
								advValue.setSept(0l);
							break;
						case 10:
							if(amount>0)
								advValue.setOct(amount);
							else
								advValue.setOct(0l);
							break;
						case 11:
							if(amount>0)
								advValue.setNov(amount);
							else
								advValue.setNov(0l);
							break;
						case 12:
							if(amount>0)
								advValue.setDec(amount);
							else
								advValue.setDec(0l);
							break;
						default:
							gLogger.info("Incorrect Record");
						break;							




						}					

					}
					advanceValueList.add(advValue);
				}
				}		

		//basic
				String basic="PO";
				List basicValues=lObjEmpLedgerDao.getAllowValues(sevarthId,finYearLower,finYearUpper,basic);
				gLogger.info("noraml advValues size"+basicValues.size());
				SupplimentEmployeeReport basicValue=new SupplimentEmployeeReport();
				basicValue.setMonth("Basic");
				for(int j= 0;j<basicValues.size();j++){

						String[] basicAmount=basicValues.get(j).toString().split("~");					
						Long amount = Long.parseLong(basicAmount[0].toString());
						gLogger.info("total ded amount"+amount);
						int monthValue =Integer.parseInt((basicAmount[1]));
						switch(monthValue){
						case 1:
							if(amount>0){
								basicValue.setJan(amount);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							else{
								basicValue.setJan(0l);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							break;
						case 2:
							if(amount>0){
								basicValue.setFeb(amount);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							else{
								basicValue.setFeb(0l);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							break;
						case 3:
							if(amount>0){
								basicValue.setMarch(amount);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							else{
								basicValue.setMarch(0l);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							break;
						case 4:
							if(amount>0){
								basicValue.setApril(amount);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							else
								basicValue.setApril(0l);
							//viewMap.put("Total Deduction", monthValueMap);
							break;
						case 5:
							if(amount>0)
								basicValue.setMay(amount);
							else
								basicValue.setMay(0l);
							break;
						case 6:
							if(amount>0)
								basicValue.setJune(amount);
							else
								basicValue.setJune(0l);
							break;
						case 7:
							if(amount>0)
								basicValue.setJuly(amount);
							else
								basicValue.setJuly(0l);
							break;
						case 8:
							if(amount>0)
								basicValue.setAug(amount);
							else
								basicValue.setAug(0l);
							break;
						case 9:
							if(amount>0)
								basicValue.setSept(amount);
							else
								basicValue.setSept(0l);
							break;
						case 10:
							if(amount>0)
								basicValue.setOct(amount);
							else
								basicValue.setOct(0l);
							break;
						case 11:
							if(amount>0)
								basicValue.setNov(amount);
							else
								basicValue.setNov(0l);
							break;
						case 12:
							if(amount>0)
								basicValue.setDec(amount);
							else
								basicValue.setDec(0l);
							break;
						default:
							gLogger.info("Incorrect Record");
						break;							




						}					

					}
					basicValueList.add(basicValue);		
//for regular remaining components
					List<String> components = new ArrayList<String>();
					String compoName="";
					components.add("TotalAllowance");
					components.add("TotalDeduction");			
					components.add("NetPay");
					//components.add("BillNo");				
					//components.add("BillDate");
					components.add("VoucherNo");
					//components.add("VoucherDate");
					for(int i=0;i<components.size();i++){
						compoName=components.get(i).toString();
						List componentsValueList=lObjEmpLedgerDao.getOtherComponentValues(sevarthId,finYearLower,finYearUpper,compoName);
						gLogger.info("noraml advValues size"+componentsValueList.size());
						SupplimentEmployeeReport otherCompValue=new SupplimentEmployeeReport();
						otherCompValue.setMonth(compoName);
						for(int j= 0;j<componentsValueList.size();j++){

							String[] otherCompAmount=componentsValueList.get(j).toString().split("~");					
							Long amount = Long.parseLong(otherCompAmount[1].toString());
							gLogger.info("total ded amount"+amount);
							int monthValue =Integer.parseInt((otherCompAmount[0]));
							switch(monthValue){
							case 1:
								if(amount>0){
									otherCompValue.setJan(amount);
									//viewMap.put("Total Deduction", monthValueMap);
								}
								else{
									otherCompValue.setJan(0l);
									//viewMap.put("Total Deduction", monthValueMap);
								}
								break;
							case 2:
								if(amount>0){
									otherCompValue.setFeb(amount);
									//viewMap.put("Total Deduction", monthValueMap);
								}
								else{
									otherCompValue.setFeb(0l);
									//viewMap.put("Total Deduction", monthValueMap);
								}
								break;
							case 3:
								if(amount>0){
									otherCompValue.setMarch(amount);
									//viewMap.put("Total Deduction", monthValueMap);
								}
								else{
									otherCompValue.setMarch(0l);
									//viewMap.put("Total Deduction", monthValueMap);
								}
								break;
							case 4:
								if(amount>0){
									otherCompValue.setApril(amount);
									//viewMap.put("Total Deduction", monthValueMap);
								}
								else
									otherCompValue.setApril(0l);
								//viewMap.put("Total Deduction", monthValueMap);
								break;
							case 5:
								if(amount>0)
									otherCompValue.setMay(amount);
								else
									otherCompValue.setMay(0l);
								break;
							case 6:
								if(amount>0)
									otherCompValue.setJune(amount);
								else
									otherCompValue.setJune(0l);
								break;
							case 7:
								if(amount>0)
									otherCompValue.setJuly(amount);
								else
									otherCompValue.setJuly(0l);
								break;
							case 8:
								if(amount>0)
									otherCompValue.setAug(amount);
								else
									otherCompValue.setAug(0l);
								break;
							case 9:
								if(amount>0)
									otherCompValue.setSept(amount);
								else
									otherCompValue.setSept(0l);
								break;
							case 10:
								if(amount>0)
									otherCompValue.setOct(amount);
								else
									otherCompValue.setOct(0l);
								break;
							case 11:
								if(amount>0)
									otherCompValue.setNov(amount);
								else
									otherCompValue.setNov(0l);
								break;
							case 12:
								if(amount>0)
									otherCompValue.setDec(amount);
								else
									otherCompValue.setDec(0l);
								break;
							default:
								gLogger.info("Incorrect Record");
							break;							




							}					

						}
						otherCompValueList.add(otherCompValue);	
					}
					
					//for regulat Strcomponents
					
					//for regular remaining components
					List<String> strComponents = new ArrayList<String>();
					String strcompoName="";
					
					strComponents.add("BillNo");				
					strComponents.add("BillDate");					
					strComponents.add("VoucherDate");
					for(int i=0;i<strComponents.size();i++){
						strcompoName=strComponents.get(i).toString();
						List componentsValueList=lObjEmpLedgerDao.getOtherComponentValues(sevarthId,finYearLower,finYearUpper,strcompoName);
						gLogger.info("noraml advValues size"+componentsValueList.size());
						SupplimentstrVO otherstrCompValue=new SupplimentstrVO();
						otherstrCompValue.setMonth(strcompoName);
						for(int j= 0;j<componentsValueList.size();j++){

							String[] otherCompAmount=componentsValueList.get(j).toString().split("~");					
							String value = otherCompAmount[1].toString();
							gLogger.info("total ded value"+value);
							int monthValue =Integer.parseInt((otherCompAmount[0]));
							switch(monthValue){
							case 1:
								if(value!=null && value!="")									
								otherstrCompValue.setJan(value);
									//viewMap.put("Total Deduction", monthValueMap);
								else				
									otherstrCompValue.setJan("-");
									//viewMap.put("Total Deduction", monthValueMap);
								
								break;
							case 2:
								if(value!=null && value!="")	
								otherstrCompValue.setFeb(value);
								else								
							otherstrCompValue.setFeb("-");
									
							
								break;
							case 3:
								if(value!=null && value!="")
									
									otherstrCompValue.setMarch(value);
									//viewMap.put("Total Deduction", monthValueMap);
								
								else{
									otherstrCompValue.setMarch("-");
									//viewMap.put("Total Deduction", monthValueMap);
								}
								break;
							case 4:
								if(value!=null && value!="")
									
									otherstrCompValue.setApril(value);
									//viewMap.put("Total Deduction", monthValueMap
								
								else
									otherstrCompValue.setApril("-");
								//viewMap.put("Total Deduction", monthValueMap);
								break;
							case 5:
								if(value!=null && value!="")
									
									otherstrCompValue.setMay(value);
								else
									otherstrCompValue.setMay("-");
								break;
							case 6:
								if(value!=null && value!="")
									
									otherstrCompValue.setJune(value);
								else
									otherstrCompValue.setJune("-");
								break;
							case 7:
								if(value!=null && value!="")
									
									otherstrCompValue.setJuly(value);
								else
									otherstrCompValue.setJuly("-");
								break;
							case 8:
								if(value!=null && value!="")									
									otherstrCompValue.setAug(value);
								else
									otherstrCompValue.setAug("-");
								break;
							case 9:
								if(value!=null && value!="")
									
									otherstrCompValue.setSept(value);
								else
									otherstrCompValue.setSept("-");
								break;
							case 10:
								if(value!=null && value!="")
									
								
									otherstrCompValue.setOct(value);
								else
									otherstrCompValue.setOct("-");
								break;
							case 11:
								if(value!=null && value!="")
									
									otherstrCompValue.setNov(value);
								else
									otherstrCompValue.setNov("-");
								break;
							case 12:
								if(value!=null && value!="")
									
									otherstrCompValue.setDec(value);
								else
									otherstrCompValue.setDec("-");
								break;
							default:
								gLogger.info("Incorrect Record");
							break;							




							}					

						}
						otherstrCompValueList.add(otherstrCompValue);	
					}
			}
	
			else{
				inputMap.put("Status","Failed");
				resultObject.setResultValue(inputMap);
				resultObject.setViewName("LedgerRep");
				return resultObject;
			}


			if(lstAllMnthBrokenData != null && !lstAllMnthBrokenData.isEmpty()){

			}
			//HrPayPaybill hrPayPaybill = null;
			HrPayGpfBalanceDtls hrPayGpfBalanceDtls = null;
			String GpfNo= "";

			//For GPF Account Details
			String newGpfNo="";
			if(GpfNo != null && GpfNo!= "")
				newGpfNo = GpfNo;

			String joinDate=lObjEmpLedgerDao.getJoiningDate(empId);
			gLogger.info("joining date-------"+joinDate);






			Map finalMap = new HashMap();
			List compoNameLst = new ArrayList();
			gLogger.info("compoNameLst ::: "+compoNameLst.size());
			gLogger.info("finalMap ::: "+finalMap.size());
			inputMap.put("joinDate", joinDate);
			//for all months data ends
			inputMap.put("empName", employeName); 
			inputMap.put("GpfNo", newGpfNo);
			inputMap.put("sevarthId", sevarthId);
			inputMap.put("sevId", sevId);
			inputMap.put("ddoCode", ddoCode);		
			inputMap.put("Status","Success");
			inputMap.put("loanmap", loanmap);
			inputMap.put("address1", address1);
			inputMap.put("address2", address2);
			inputMap.put("address3", address3);
			inputMap.put("address4", address4);
			inputMap.put("empDetails", empDetails);
			inputMap.put("empGPFDetails", empGPFDetails); 
			inputMap.put("empLoanDetails", empLoanDetails); 
			inputMap.put("totalgisAmount", totalgisAmount);
			inputMap.put("postDetails", postDetails);
			inputMap.put("empGISDetails", empGISDetails);
			inputMap.put("finYearLower", finYearLower);
			inputMap.put("finYearUpper", finYearUpper);
			inputMap.put("lpcIddate", lpcIddate);
			inputMap.put("postType", postType);
			inputMap.put("sevarthId", sevarthId);
			//inputMap.put("dcpsContribution", dcpsContribution);
			inputMap.put("allowValueList", allowValueList);
			inputMap.put("deducValueList", deducValueList);
			inputMap.put("loanValueList", loanValueList);
			inputMap.put("advanceValueList", advanceValueList);
			inputMap.put("basicValueList", basicValueList);
			inputMap.put("otherCompValueList", otherCompValueList);
			inputMap.put("otherstrCompValueList", otherstrCompValueList);
			resultObject.setResultValue(inputMap);
			resultObject.setViewName("LedgerRep");
		}
		catch(Exception e){
			gLogger.info("excepiton.."+e);
		}
		return resultObject;

	}
	public ResultObject getSuplimentLedgerReport(Map inputMap) throws Exception {

		setSessionInfo(inputMap);
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		try{

			Calendar cal = Calendar.getInstance();
			//Object objDateOfDeselect=StringUtility.getParameter("year", request);
			String lStrDateOfDeselect = StringUtility.getParameter("year", request);
			//String lStrDateOfDeselect="2012-13";
			gLogger.info("lStrDateOfDeselect in service is ::: "+lStrDateOfDeselect);
			int selMonth = cal.get(Calendar.MONTH)+1;
			gLogger.info("month is "+selMonth);
			int selYear = cal.get(Calendar.YEAR);
			gLogger.info("year is "+selYear);
			FinancialYearDAOImpl financialYearDAOImpl = new FinancialYearDAOImpl(SgvcFinYearMst.class,serv.getSessionFactory());
			long finYrId = Long.parseLong(lStrDateOfDeselect);
			//financialYearDAOImpl.getFinYearId(lStrDateOfDeselect);
			gLogger.info("finYrId"+finYrId);

			SgvcFinYearMst sgvcFinYearMst = financialYearDAOImpl.read(finYrId);
			int finYrFrmMnth = 0,finYrFrmYr = 0,finYrToMnth = 0,finYrToYr = 0;
			if(sgvcFinYearMst != null)
			{
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(sgvcFinYearMst.getFromDate());
				finYrFrmMnth = cal1.get(Calendar.MONTH)+1;    //+1 here change
				gLogger.info("finYrFrmMnth ::: "+finYrFrmMnth);
				finYrFrmYr = cal1.get(Calendar.YEAR);
				gLogger.info("finYrFrmYr ::: "+finYrFrmYr);

				cal1.setTime(sgvcFinYearMst.getToDate());
				finYrToMnth = cal1.get(Calendar.MONTH)+1;               //+1 here change
				gLogger.info("finYrToMnth ::: "+finYrToMnth);
				finYrToYr = cal1.get(Calendar.YEAR);
				gLogger.info("finYrToYr ::: "+finYrToYr);
			}
			inputMap.put("finYear"," (April, "+finYrFrmYr+" to March, "+finYrToYr+")");
			String selYrMnths = "4,5,6,7,8,9,10,11,12",selYrPrvYrMnths = "1,2,3";

			/*gLogger.info("selMonth"+selMonth+"finYrFrmMnth"+finYrFrmMnth+"selYear"+selYear+"finYrFrmYr"+finYrFrmYr);
		if(selMonth > finYrFrmMnth && selYear == finYrFrmYr)
		{
			for(int cnt = finYrFrmMnth ; cnt <= selMonth ; cnt++)
			{
				if(selYrMnths != null)
					selYrMnths = selYrMnths + "," + String.valueOf(cnt);
				else
					selYrMnths = String.valueOf(cnt);
			}
		}
		else if(selMonth <= finYrFrmMnth && selYear >= finYrToYr)
		{
			for(int cnt = finYrFrmMnth ; cnt <= 12 ; cnt++)
			{
				if(selYrPrvYrMnths != null)
					selYrPrvYrMnths = selYrPrvYrMnths + "," + String.valueOf(cnt);
				else
					selYrPrvYrMnths = String.valueOf(cnt);
			}
			for(int cnt = 1 ; cnt < selMonth ; cnt++)
			{
				if(selYrMnths != null)
					selYrMnths = selYrMnths + "," + String.valueOf(cnt);
				else
					selYrMnths = String.valueOf(cnt);
			}
		}*/
			gLogger.info("selYrMnths"+selYrMnths);
			Object lStrOrgEmpId=StringUtility.getParameter("orgempId", request);
			gLogger.info("lStrOrgEmpId suppliment"+lStrOrgEmpId);
			Object lStrDcpsEmpId=StringUtility.getParameter("dcpsempId", request);
			String employeName=StringUtility.getParameter("empName", request).toString();
			String sevarthId=StringUtility.getParameter("sevarthId", request).toString();
			String sevId=sevarthId.substring(0, 3).toUpperCase();
			String ddoCode=StringUtility.getParameter("ddoCode", request).toString().trim();
			long OrgempId = (lStrOrgEmpId != null && !lStrOrgEmpId .equals("") )? Long.parseLong(lStrOrgEmpId.toString()) : 0;
			long empId = (lStrDcpsEmpId != null && !lStrDcpsEmpId .equals("") )? Long.parseLong(lStrDcpsEmpId.toString()) : 0;

			LedgerReportDAOImpl lObjEmpLedgerDao = new LedgerReportDAOImpl(
					MstEmp.class, serv.getSessionFactory());

			String yearArray[] = sgvcFinYearMst.getFinYearDesc().split("-");
			long finYrIdemp=Long.parseLong(lObjEmpLedgerDao.getFinYear(finYrId));

			Object Obj1[];


			Long finYearLower = Long.parseLong(yearArray[0]);
			Long  finYearUpper= Long.parseLong(yearArray[1]);
			gLogger.info("finYearUpper"+finYearUpper);
			gLogger.info("finYearLower"+finYearLower);
			//List lstSelYrMnthsBillData = lObjEmpLedgerDao.getEmpLastMnthBillData(OrgempId, ddoCode, selYrMnths, selYear); // for the selected date year's months data		



			List empTotaldedGross=lObjEmpLedgerDao.getTotalDedandGrass(sevarthId,finYearLower,finYearUpper);
			//Long dcpsContribution= lObjEmpLedgerDao.getYearlyContribution(sevarthId, finYrId);

			Object Obj[];
			String totalDeduction="TOTALDED";
			Long gisAmount=0l;
			Long totalgisAmount=0l;
			String postlookupId=null;
			String postType=null;
			Long brokenPeriodId=0l;			
			List brokenPeriodAllow=null;			
			List brokenData=null;
			List brokenPeriodIdList=lObjEmpLedgerDao.getBroenPeriodId(sevarthId,finYrId);
			if(brokenPeriodIdList != null && !brokenPeriodIdList.isEmpty())
			{


				String strBrokenIdList="";
				for(int i=0;i<brokenPeriodIdList.size();i++){
					strBrokenIdList=strBrokenIdList.concat(brokenPeriodIdList.get(i).toString()).concat(",");
				}
				strBrokenIdList=strBrokenIdList.substring(0,strBrokenIdList.length()-1);
				gLogger.info("brokenIdList*****"+strBrokenIdList);
				//List brokenBasic=lObjEmpLedgerDao.getBrokenPeriodData(sevarthId,finYrId,basic);
				///List netPayAmount=lObjEmpLedgerDao.getBrokenPeriodData(sevarthId,finYrId,netPay);
				//	/List noOfDaysList=lObjEmpLedgerDao.getBrokenPeriodData(sevarthId,finYrId,noOfDays);
				List<String> components = new ArrayList<String>();
				components.add("Basic");
				components.add("netPay");			
				components.add("noOfDays");
				/*components.add("reason");
			components.add("remarks");
			components.add("fromDate");
			components.add("toDate");
			components.add("voucherNo");
			components.add("voucherDate");*/
				components.add("billNo");
				//components.add("BillDate");
				List brokenTotalMonthData = new ArrayList();
				List allowBrokenData=new ArrayList();
				List deducBrokenData=new ArrayList();
				List strbrokenTotalMonthData = new ArrayList();
				List totalDedcAllowValueData = new ArrayList();
				for(int i=0;i<components.size();i++){
					gLogger.info("components.size()*****"+components.size());
					String component=components.get(i).toString();
					gLogger.info("components*****"+component);

					brokenData=lObjEmpLedgerDao.getBrokenPeriodData(sevarthId,finYrId,component);
					gLogger.info("brokenData size*****"+brokenData.size());


					SupplimentEmployeeReport VO = new SupplimentEmployeeReport(); 
					VO.setMonth(component);
					for(int j=0;j<brokenData.size();j++)
					{
						String[] brokenPeriodamount=brokenData.get(j).toString().split("~");
						gLogger.info("brokenPeriodamount*****"+brokenData.get(j));
						Long amount = Long.parseLong(brokenPeriodamount[0].toString());
						gLogger.info("total ded amount"+amount);
						int monthValue =Integer.parseInt((brokenPeriodamount[1]));
						switch(monthValue){
						case 1:
							if(amount>0){
								VO.setJan(amount);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							else{
								VO.setJan(0l);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							break;
						case 2:
							if(amount>0){
								VO.setFeb(amount);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							else{
								VO.setFeb(0l);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							break;
						case 3:
							if(amount>0){
								VO.setMarch(amount);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							else{
								VO.setMarch(0l);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							break;
						case 4:
							if(amount>0){
								VO.setApril(amount);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							else
								VO.setApril(0l);
							//viewMap.put("Total Deduction", monthValueMap);
							break;
						case 5:
							if(amount>0)
								VO.setMay(amount);
							else
								VO.setMay(0l);
							break;
						case 6:
							if(amount>0)
								VO.setJune(amount);
							else
								VO.setJune(0l);
							break;
						case 7:
							if(amount>0)
								VO.setJuly(amount);
							else
								VO.setJuly(0l);
							break;
						case 8:
							if(amount>0)
								VO.setAug(amount);
							else
								VO.setAug(0l);
							break;
						case 9:
							if(amount>0)
								VO.setSept(amount);
							else
								VO.setSept(0l);
							break;
						case 10:
							if(amount>0)
								VO.setOct(amount);
							else
								VO.setOct(0l);
							break;
						case 11:
							if(amount>0)
								VO.setNov(amount);
							else
								VO.setNov(0l);
							break;
						case 12:
							if(amount>0)
								VO.setDec(amount);
							else
								VO.setDec(0l);
							break;
						default:
							gLogger.info("Incorrect Record");
						break;							




						}	

					}
					brokenTotalMonthData.add(VO);

				}
				//allowances
				String allowComponent="";

				List maxAllowComponent=lObjEmpLedgerDao.getMaxallowList(sevarthId,finYrId);

				for(int i=0;i<maxAllowComponent.size();i++){
					Long allowId=Long.parseLong(maxAllowComponent.get(i).toString());
					List allowComponentsList=lObjEmpLedgerDao.getBrokenAllow(strBrokenIdList,allowId); 
					gLogger.info("allowComponentsList size"+allowComponentsList.size());
					if(allowComponentsList!=null && !allowComponentsList.isEmpty()){
						Iterator it =  allowComponentsList.iterator();
						SupplimentEmployeeReport brokenAllow = new SupplimentEmployeeReport(); 
						while (it.hasNext()) {
							Obj1 = (Object[]) it.next();
							allowComponent=(Obj1[0].toString());

							brokenAllow.setMonth(allowComponent);
							String[] allowAmountAray=Obj1[1].toString().split("~");
							Long amount=Long.parseLong(allowAmountAray[0].toString());
							gLogger.info("amount allow"+amount);
							int monthValue =Integer.parseInt((allowAmountAray[1]));
							gLogger.info("monthValue allow"+monthValue);
							switch(monthValue){

							case 1:
								if(amount>0){
									brokenAllow.setJan(amount);
									//viewMap.put("Total Deduction", monthValueMap);
								}
								else{
									brokenAllow.setJan(0l);
									//viewMap.put("Total Deduction", monthValueMap);
								}
								break;
							case 2:
								if(amount>0){
									brokenAllow.setFeb(amount);
									//viewMap.put("Total Deduction", monthValueMap);
								}
								else{
									brokenAllow.setFeb(0l);
									//viewMap.put("Total Deduction", monthValueMap);
								}
								break;
							case 3:
								if(amount>0){
									brokenAllow.setMarch(amount);
									//viewMap.put("Total Deduction", monthValueMap);
								}
								else{
									brokenAllow.setMarch(0l);
									//viewMap.put("Total Deduction", monthValueMap);
								}
								break;
							case 4:
								if(amount>0){
									brokenAllow.setApril(amount);
									//viewMap.put("Total Deduction", monthValueMap);
								}
								else
									brokenAllow.setApril(0l);
								//viewMap.put("Total Deduction", monthValueMap);
								break;
							case 5:
								if(amount>0)
									brokenAllow.setMay(amount);
								else
									brokenAllow.setMay(0l);
								break;
							case 6:
								if(amount>0)
									brokenAllow.setJune(amount);
								else
									brokenAllow.setJune(0l);
								break;
							case 7:
								if(amount>0)
									brokenAllow.setJuly(amount);
								else
									brokenAllow.setJuly(0l);
								break;
							case 8:
								if(amount>0)
									brokenAllow.setAug(amount);
								else
									brokenAllow.setAug(0l);
								break;
							case 9:
								if(amount>0)
									brokenAllow.setSept(amount);
								else
									brokenAllow.setSept(0l);
								break;
							case 10:
								if(amount>0)
									brokenAllow.setOct(amount);
								else
									brokenAllow.setOct(0l);
								break;
							case 11:
								if(amount>0)
									brokenAllow.setNov(amount);
								else
									brokenAllow.setNov(0l);
								break;
							case 12:
								if(amount>0)
									brokenAllow.setDec(amount);
								else
									brokenAllow.setDec(0l);
								break;
							default:
								gLogger.info("Incorrect Record");
							break;							





							}

						}
						allowBrokenData.add(brokenAllow);

					}


				}

				//deductions

				String deducComponent="";
				List maxDeducComponent=lObjEmpLedgerDao.getMaxDeducList(sevarthId,finYrId);

				for(int i=0;i<maxDeducComponent.size();i++){
					Long deducId=Long.parseLong(maxDeducComponent.get(i).toString());
					List deducComponentsList=lObjEmpLedgerDao.getBrokenDeducValue(strBrokenIdList,deducId); 
					gLogger.info("deducComponentsList size"+deducComponentsList.size());
					if(deducComponentsList!=null && !deducComponentsList.isEmpty()){
						Iterator it =  deducComponentsList.iterator();
						SupplimentEmployeeReport brokenDeduc = new SupplimentEmployeeReport(); 
						while (it.hasNext()) {
							Obj1 = (Object[]) it.next();
							deducComponent=(Obj1[0].toString());

							brokenDeduc.setMonth(deducComponent);
							String[] allowAmountAray=Obj1[1].toString().split("~");
							Long amount=Long.parseLong(allowAmountAray[0].toString());
							gLogger.info("amount allow"+amount);
							int monthValue =Integer.parseInt((allowAmountAray[1]));
							gLogger.info("monthValue allow"+monthValue);
							switch(monthValue){

							case 1:
								if(amount>0){
									brokenDeduc.setJan(amount);
									//viewMap.put("Total Deduction", monthValueMap);
								}
								else{
									brokenDeduc.setJan(0l);
									//viewMap.put("Total Deduction", monthValueMap);
								}
								break;
							case 2:
								if(amount>0){
									brokenDeduc.setFeb(amount);
									//viewMap.put("Total Deduction", monthValueMap);
								}
								else{
									brokenDeduc.setFeb(0l);
									//viewMap.put("Total Deduction", monthValueMap);
								}
								break;
							case 3:
								if(amount>0){
									brokenDeduc.setMarch(amount);
									//viewMap.put("Total Deduction", monthValueMap);
								}
								else{
									brokenDeduc.setMarch(0l);
									//viewMap.put("Total Deduction", monthValueMap);
								}
								break;
							case 4:
								if(amount>0){
									brokenDeduc.setApril(amount);
									//viewMap.put("Total Deduction", monthValueMap);
								}
								else
									brokenDeduc.setApril(0l);
								//viewMap.put("Total Deduction", monthValueMap);
								break;
							case 5:
								if(amount>0)
									brokenDeduc.setMay(amount);
								else
									brokenDeduc.setMay(0l);
								break;
							case 6:
								if(amount>0)
									brokenDeduc.setJune(amount);
								else
									brokenDeduc.setJune(0l);
								break;
							case 7:
								if(amount>0)
									brokenDeduc.setJuly(amount);
								else
									brokenDeduc.setJuly(0l);
								break;
							case 8:
								if(amount>0)
									brokenDeduc.setAug(amount);
								else
									brokenDeduc.setAug(0l);
								break;
							case 9:
								if(amount>0)
									brokenDeduc.setSept(amount);
								else
									brokenDeduc.setSept(0l);
								break;
							case 10:
								if(amount>0)
									brokenDeduc.setOct(amount);
								else
									brokenDeduc.setOct(0l);
								break;
							case 11:
								if(amount>0)
									brokenDeduc.setNov(amount);
								else
									brokenDeduc.setNov(0l);
								break;
							case 12:
								if(amount>0)
									brokenDeduc.setDec(amount);
								else
									brokenDeduc.setDec(0l);
								break;
							default:
								gLogger.info("Incorrect Record");
							break;							





							}

						}
						deducBrokenData.add(brokenDeduc);

					}


				}

				//string values
				List<String> strComponents = new ArrayList<String>();
				strComponents.add("reason");
				strComponents.add("remarks");
				strComponents.add("fromDate");
				strComponents.add("toDate");
				strComponents.add("voucherNo");
				strComponents.add("voucherDate");
				for(int i=0;i<strComponents.size();i++){
					gLogger.info("strComponents.size()*****"+strComponents.size());
					String component=strComponents.get(i).toString();
					gLogger.info("components*****"+component);

					brokenData=lObjEmpLedgerDao.getBrokenPeriodData(sevarthId,finYrId,component);
					gLogger.info("brokenData size*****"+brokenData.size());


					SupplimentstrVO strVO = new SupplimentstrVO(); 
					strVO.setMonth(component);
					for(int j=0;j<brokenData.size();j++)
					{
						String[] brokenPeriodamount=brokenData.get(j).toString().split("~");
						gLogger.info("brokenPeriodamount*****"+brokenData.get(j));
						String value= brokenPeriodamount[0].toString();
						gLogger.info("total ded amount"+value);
						int monthValue =Integer.parseInt((brokenPeriodamount[1]));
						switch(monthValue){
						case 1:
							if(value!=null && value!=""){
								strVO.setJan(value);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							else{
								strVO.setJan("-");
								//viewMap.put("Total Deduction", monthValueMap);
							}
							break;
						case 2:
							if(value!=null && value!=""){
								strVO.setFeb(value);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							else{
								strVO.setFeb("-");
								//viewMap.put("Total Deduction", monthValueMap);
							}
							break;
						case 3:
							if(value!=null && value!="")
								strVO.setMarch(value);
							//viewMap.put("Total Deduction", monthValueMap);

							else
								strVO.setMarch("-");
							//viewMap.put("Total Deduction", monthValueMap);

							break;
						case 4:
							if(value!=null && value!="")
								strVO.setApril(value);
							//viewMap.put("Total Deduction", monthValueMap);

							else
								strVO.setApril("-");
							//viewMap.put("Total Deduction", monthValueMap);
							break;
						case 5:
							if(value!=null && value!="")
								strVO.setMay(value);
							else
								strVO.setMay("-");
							break;
						case 6:
							if(value!=null && value!="")
								strVO.setJune(value);
							else
								strVO.setJune("-");
							break;
						case 7:
							if(value!=null && value!="")
								strVO.setJuly(value);
							else
								strVO.setJuly("-");
							break;
						case 8:
							if(value!=null && value!="")
								strVO.setAug(value);
							else
								strVO.setAug("-");
							break;
						case 9:
							if(value!=null && value!="")
								strVO.setSept(value);
							else
								strVO.setSept("-");
							break;
						case 10:
							if(value!=null && value!="")
								strVO.setOct(value);
							else
								strVO.setOct("-");
							break;
						case 11:
							if(value!=null && value!="")
								strVO.setNov(value);
							else
								strVO.setNov("-");
							break;
						case 12:
							if(value!=null && value!="")
								strVO.setDec(value);
							else
								strVO.setDec("-");
							break;
						default:
							gLogger.info("Incorrect Record");
						break;							




						}	

					}
					strbrokenTotalMonthData.add(strVO);

				}
				//total dedec and allowance

				List<String> totalDedcAllow = new ArrayList<String>();
				totalDedcAllow.add("totalAllowance");
				totalDedcAllow.add("totalDeduction");
				for(int i=0;i<totalDedcAllow.size();i++){
					String component=totalDedcAllow.get(i).toString();
					List totalAllowDeducList=lObjEmpLedgerDao.getTotalAllowDeducValue(strBrokenIdList,component);
					SupplimentEmployeeReport totalAllowDeduc = new SupplimentEmployeeReport(); 
					totalAllowDeduc.setMonth(component);
					for(int j=0;j<totalAllowDeducList.size();j++)
					{
						String[] totalAllowDeducamount=totalAllowDeducList.get(j).toString().split("~");
						gLogger.info("totalAllowDeducamount*****"+totalAllowDeducList.get(j));
						Long amount = Long.parseLong(totalAllowDeducamount[0].toString());
						gLogger.info("total ded amount"+amount);
						int monthValue =Integer.parseInt((totalAllowDeducamount[1]));
						switch(monthValue){
						case 1:
							if(amount>0){
								totalAllowDeduc.setJan(amount);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							else{
								totalAllowDeduc.setJan(0l);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							break;
						case 2:
							if(amount>0){
								totalAllowDeduc.setFeb(amount);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							else{
								totalAllowDeduc.setFeb(0l);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							break;
						case 3:
							if(amount>0){
								totalAllowDeduc.setMarch(amount);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							else{
								totalAllowDeduc.setMarch(0l);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							break;
						case 4:
							if(amount>0){
								totalAllowDeduc.setApril(amount);
								//viewMap.put("Total Deduction", monthValueMap);
							}
							else
								totalAllowDeduc.setApril(0l);
							//viewMap.put("Total Deduction", monthValueMap);
							break;
						case 5:
							if(amount>0)
								totalAllowDeduc.setMay(amount);
							else
								totalAllowDeduc.setMay(0l);
							break;
						case 6:
							if(amount>0)
								totalAllowDeduc.setJune(amount);
							else
								totalAllowDeduc.setJune(0l);
							break;
						case 7:
							if(amount>0)
								totalAllowDeduc.setJuly(amount);
							else
								totalAllowDeduc.setJuly(0l);
							break;
						case 8:
							if(amount>0)
								totalAllowDeduc.setAug(amount);
							else
								totalAllowDeduc.setAug(0l);
							break;
						case 9:
							if(amount>0)
								totalAllowDeduc.setSept(amount);
							else
								totalAllowDeduc.setSept(0l);
							break;
						case 10:
							if(amount>0)
								totalAllowDeduc.setOct(amount);
							else
								totalAllowDeduc.setOct(0l);
							break;
						case 11:
							if(amount>0)
								totalAllowDeduc.setNov(amount);
							else
								totalAllowDeduc.setNov(0l);
							break;
						case 12:
							if(amount>0)
								totalAllowDeduc.setDec(amount);
							else
								totalAllowDeduc.setDec(0l);
							break;
						default:
							gLogger.info("Incorrect Record");
						break;							




						}	

					}
					totalDedcAllowValueData.add(totalAllowDeduc);

				}
				inputMap.put("empName", employeName); 
				//inputMap.put("GpfNo", newGpfNo);
				inputMap.put("sevarthId", sevarthId);
				inputMap.put("sevId", sevId);
				inputMap.put("brokenTotalMonthData",brokenTotalMonthData);
				inputMap.put("allowBrokenData",allowBrokenData);
				inputMap.put("deducBrokenData",deducBrokenData);
				inputMap.put("strbrokenTotalMonthData",strbrokenTotalMonthData);
				inputMap.put("totalDedcAllowValueData",totalDedcAllowValueData);

			}	


			else{
				inputMap.put("Status","Failed");
				resultObject.setResultValue(inputMap);
				resultObject.setViewName("suplimentLedgerReport");
				return resultObject;
			}


			inputMap.put("Status","Success");


			resultObject.setResultValue(inputMap);
			resultObject.setViewName("suplimentLedgerReport");

		}
		catch(Exception e){
			gLogger.info("excepiton.."+e);
		}
		return resultObject;



	}

	private String insertExtraSpace(int startlenth,int endLength){
		String whiteSpace="";
		for(int i=startlenth;i<endLength;i++){
			whiteSpace+=" ";
		}
		return whiteSpace;
	}

	private LinkedHashMap initializeValueMap(LinkedHashMap map,int month,Object obj){


		for(int i=4;i<13;i++){
			map.put(i,null);
		}
		for(Integer i=1;i<4;i++){
			map.put(i,null);
		}
		map.put(month, obj);

		return map;

	}
	private LinkedHashMap updateTotal(LinkedHashMap map,Long[] data){

		//Integer i=1;
		for(int i=4;i<13;i++){
			map.put(i,data[i]);
		}
		for(Integer i=1;i<4;i++){
			map.put(i,data[i]);
		}

		return map;	
	}


	private LinkedHashMap updateTotalDed(LinkedHashMap map,Long[] data){

		//Integer i=1;
		for(int i=4;i<13;i++){
			map.put(i,data[i]);
		}
		for(Integer i=1;i<4;i++){
			map.put(i,data[i]);
		}

		return map;	
	}

	private LinkedHashMap removeErrorRow(LinkedHashMap viewMap,Map allowDedMap){

		Iterator entries = allowDedMap.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry entry = (Map.Entry) entries.next();
			Object key = (Object)entry.getKey();
			Integer value = (Integer)entry.getValue();
			if(value==0){
				viewMap.remove(key);
			}
		}
		return viewMap;
	}
}
