/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Sep 14, 2011		Kapil Devani								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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

import com.tcs.sgv.allowance.service.SalaryRules;
import com.tcs.sgv.allowance.service.SalaryRules_6thPay;
import com.tcs.sgv.allowance.valueobject.HrPayAllowTypeMst;
import com.tcs.sgv.common.dao.CmnDatabaseMstDaoImpl;
import com.tcs.sgv.common.dao.CmnLookupMstDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.CmnDatabaseMst;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.BrokenPeriodDAO;
import com.tcs.sgv.dcps.dao.BrokenPeriodDAOImpl;
import com.tcs.sgv.dcps.dao.DdoProfileDAO;
import com.tcs.sgv.dcps.dao.DdoProfileDAOImpl;
import com.tcs.sgv.dcps.valueobject.BrokenPeriodPayCustomVO;
import com.tcs.sgv.dcps.valueobject.MstBrokenPeriodPay;
import com.tcs.sgv.dcps.valueobject.RltBrokenPeriodAllow;
import com.tcs.sgv.dcps.valueobject.RltBrokenPeriodDeduc;
import com.tcs.sgv.deduction.valueobject.HrPayDeducTypeMst;
import com.tcs.sgv.eis.dao.DeductionDtlsDAOImpl;
import com.tcs.sgv.eis.dao.DeptCompMPGDAOImpl;
import com.tcs.sgv.eis.dao.EmpAllwMapDAOImpl;
import com.tcs.sgv.eis.dao.EmpCompMpgDAOImpl;
import com.tcs.sgv.eis.dao.HrPayOfficePostMpgDAOImpl;
import com.tcs.sgv.eis.dao.NonGovDeducDAO;
import com.tcs.sgv.eis.dao.NonGovDeducDAOImpl;
import com.tcs.sgv.eis.dao.OtherDetailDAOImpl;
import com.tcs.sgv.eis.dao.PayBillDAOImpl;
import com.tcs.sgv.eis.dao.PayComponentMasterDAOImpl;
import com.tcs.sgv.eis.dao.PayComponentRuleGroupMstDAOImpl;
import com.tcs.sgv.eis.dao.PayComponentRuleGroupParamRltDAOImpl;
import com.tcs.sgv.eis.service.PayrollCalculationServiceImpl;
import com.tcs.sgv.eis.util.GenerateBillLoanAndAdvanceHelper;
import com.tcs.sgv.eis.util.GenerateBillServiceHelper;
import com.tcs.sgv.eis.valueobject.AllwValCustomVO;
import com.tcs.sgv.eis.valueobject.DeductionCustomVO;
import com.tcs.sgv.eis.valueobject.EmpBrokenPeriodVO;
import com.tcs.sgv.eis.valueobject.EmpNonGovVO;
import com.tcs.sgv.eis.valueobject.EmpPaybillLoanVO;
import com.tcs.sgv.eis.valueobject.EmpPaybillVO;
import com.tcs.sgv.eis.valueobject.HrEisEmpCompMpg;
import com.tcs.sgv.eis.valueobject.HrEisEmpMst;
import com.tcs.sgv.eis.valueobject.HrEisOtherDtls;
import com.tcs.sgv.eis.valueobject.HrPayAllowDedMst;
import com.tcs.sgv.eis.valueobject.HrPayDeductionDtls;
import com.tcs.sgv.eis.valueobject.HrPayEmpSalaryTxn;
import com.tcs.sgv.eis.valueobject.HrPayEmpallowMpg;
import com.tcs.sgv.eis.valueobject.HrPayLocComMpg;
import com.tcs.sgv.eis.valueobject.HrPayNonGovDeduction;
import com.tcs.sgv.eis.valueobject.HrPayOfficepostMpg;
import com.tcs.sgv.eis.valueobject.HrPayPaybill;
import com.tcs.sgv.eis.valueobject.HrPayRuleGrpMst;
import com.tcs.sgv.eis.valueobject.HrPayRuleGrpParamRlt;
import com.tcs.sgv.ess.dao.OrgPostMstDaoImpl;
import com.tcs.sgv.ess.dao.OrgUserMstDaoImpl;
import com.tcs.sgv.ess.valueobject.OrgPostMst;
import com.tcs.sgv.ess.valueobject.OrgUserMst;
import com.tcs.sgv.payroll.util.PayrollConstants;


/**
 * Class Description -
 * 
 * 
 * @author Kapil Devani
 * @version 0.1
 * @since JDK 5.0 Sep 14, 2011
 */
@SuppressWarnings({"unchecked","unused"})
public class BrokenPeriodPayServiceImpl extends ServiceImpl
{

	/* Global Variable for Logger Class */
	private final Log logger = LogFactory.getLog(getClass());

	private String gStrPostId = null; /* STRING POST ID */

	private String gStrUserId = null; /* STRING USER ID */

	private String gStrLocale = null; /* STRING LOCALE */

	private Locale gLclLocale = null; /* LOCALE */

	private Long gLngLangId = null; /* LANG ID */

	private Long gLngDBId = null; /* DB ID */

	private Date gDtCurDate = null; /* CURRENT DATE */

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	private HttpSession session = null; /* SESSION */

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for Current Date */
	Date gDtCurrDt = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;

	/* Global Variable for User Loc Map */
	static HashMap sMapUserLoc = new HashMap();

	/* Global Variable for User Location */
	String gStrUserLocation = null;

	/* Resource bundle for the constants */
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/dcps/DCPSConstants");

	private void setSessionInfo(Map inputMap)
	{
		try
		{
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
		}
		catch (Exception e)
		{

		}
	}

	public ResultObject searchEmployee(Map inputMap) throws Exception
	{
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS);

		HrEisEmpMst lObjHrEisEmpVO = null;
		Long lLongYearId = null;
		Long lLongMonthId = null;
		int generated= 0;
		try
		{
			setSessionInfo(inputMap);
			String lStrSearchName = StringUtility.getParameter("txtSearchName", request).toUpperCase();
			
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
			long locId = Long.parseLong(loginDetailsMap.get("locationId").toString());
			BrokenPeriodDAO lObjBrokenPeriodDAO = new BrokenPeriodDAOImpl(null, serv.getSessionFactory());

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());

			/* Get Years */
			List lLstYears = lObjDcpsCommonDAO.getFinyears();
			/* Get Months */
			List lLstMonths = lObjDcpsCommonDAO.getMonths();

			inputMap.put("YEARS", lLstYears);
			inputMap.put("MONTHS", lLstMonths);
			
			

			String lStrYear = StringUtility.getParameter("yearId", request);
			String lStrMonth = StringUtility.getParameter("monthId", request);
			String lStrBillType = StringUtility.getParameter("billType", request);
			
			if (!lStrYear.equals(""))
			{
				lLongYearId = Long.valueOf(lStrYear);
			}
			if (!lStrMonth.equals(""))
			{
				lLongMonthId = Long.valueOf(lStrMonth);
			}

			inputMap.put("yearId", lStrYear);
			inputMap.put("monthId", lStrMonth);
			inputMap.put("billType", lStrBillType);
			inputMap.put("yearGiven", lObjBrokenPeriodDAO.getyearfromFinYear(lStrYear,Integer.parseInt(lStrMonth)));
			inputMap.put("monthGiven", lStrMonth);
			if (lStrSearchName != "")
			{

				List lListReasonsForBrokenPeriod = IFMSCommonServiceImpl.getLookupValues("ReasonsForBrokenPeriod", SessionHelper.getLangId(inputMap), inputMap);
				inputMap.put("lListReasonsForBrokenPeriod", lListReasonsForBrokenPeriod);

				List lLstSinglEmployee = lObjBrokenPeriodDAO.SearchEmployeeWithName(lStrSearchName,locId);

				if (lLstSinglEmployee == null)
				{
					inputMap.put("SearchStatus", "false");
				}
				else
				{
					inputMap.put("SearchStatus", "true");
					inputMap.put("lLstSinglEmployee", lLstSinglEmployee);
					String fName = null;
					if (lLstSinglEmployee.get(1) != null)
					{
						fName = lLstSinglEmployee.get(1).toString().concat(" ");
					}
					else
					{
						fName = "";
					}
					String mName = null;
					if (lLstSinglEmployee.get(2) != null)
					{
						mName = lLstSinglEmployee.get(2).toString().concat(" ");
					}
					else
					{
						mName = "";
					}
					String lName = null;
					if (lLstSinglEmployee.get(3) != null)
					{
						lName = lLstSinglEmployee.get(3).toString();
					}
					else
					{
						lName = "";
					}
					//added by vivek for gpf validation
					Date superAnnuationDate=null;
					if (lLstSinglEmployee.get(6) != null)
					{
						superAnnuationDate =(Date)lLstSinglEmployee.get(6);
					}
					int allowMonthGpf=0;
					int allowyearGpf=0;
					Calendar calc = Calendar.getInstance(); 
					calc.setTime(superAnnuationDate); 
					calc.add(Calendar.MONTH, -3);
					Date allowedGPFDate= calc.getTime();
					allowMonthGpf=superAnnuationDate.getMonth()+1;
					allowyearGpf=superAnnuationDate.getYear()+1900;
					inputMap.put("allowMonthGpf", allowMonthGpf);
					inputMap.put("allowyearGpf", allowyearGpf);
					inputMap.put("superAnnuationDate", superAnnuationDate);
					String EmpId = lLstSinglEmployee.get(0).toString();

					lObjHrEisEmpVO = lObjBrokenPeriodDAO.getHrEisEmpMstVOForEmpMpgId(Long.valueOf(EmpId));

					Long lLongHrEisEmpId = Long.valueOf(lObjHrEisEmpVO.getEmpId());

					//Added by Abhilash for displaying sevarthId

					long emloyeeId = Long.valueOf(lObjHrEisEmpVO.getEmpId());
					logger.info("Employeeid from hreisempmst is **********" + emloyeeId);

					String sevarthId = lObjBrokenPeriodDAO.getSevarthId(emloyeeId);
					logger.info("sevarthId from hreisempmst is **********" + sevarthId);
					inputMap.put("sevarthId", sevarthId);

					//Ended by Abhilash 

					String Designation = lObjBrokenPeriodDAO.getDesignationName(EmpId);

					String OfficeName = lObjBrokenPeriodDAO.getOfficeName(EmpId);
					String GPFOrDCPSNo = lObjBrokenPeriodDAO.getGPFOrDCPSNo(sevarthId);
					inputMap.put("orgEmpId", EmpId);
					inputMap.put("lLongHrEisEmpId",lLongHrEisEmpId);
					inputMap.put("EmpId", lLongHrEisEmpId);
					inputMap.put("EmpName", fName.concat(mName.concat(lName)));
					inputMap.put("Designation", Designation);
					inputMap.put("OfficeName", OfficeName);
					inputMap.put("GPFOrDCPSNo", GPFOrDCPSNo);

					List lListAllowancesForEmp = lObjBrokenPeriodDAO.getAllowancesListForGivenEmp(lLongHrEisEmpId);
					List lListDeductionsForEmp = lObjBrokenPeriodDAO.getDeductionsListForGivenEmp(lLongHrEisEmpId);
                    Iterator it=lListAllowancesForEmp.iterator();
                    while(it.hasNext()){
                    	Object row[]=(Object [])it.next();
                    	logger.info("broken allow"+row[0]+"   "+row[1]);
                    	
                    }
                    it=null;
                    it=lListDeductionsForEmp.iterator();
                    while(it.hasNext()){
                    	Object row[]=(Object [])it.next();
                    	logger.info("broken allo"+row[0]+"   "+row[1]);
                    	
                    }
					inputMap.put("AllowancesList", lListAllowancesForEmp);
					inputMap.put("DeductionsList", lListDeductionsForEmp);

					//modified by Kinjal
					if (!lObjBrokenPeriodDAO.checkBrokenPeriodPayExistsOrNot(lLongHrEisEmpId, lLongYearId, lLongMonthId, lStrBillType))
					{
						inputMap.put("PaysAddedBefore", "No");
					}
					else
					{
						List<MstBrokenPeriodPay> lListAddedBrokenPeriodPays = lObjBrokenPeriodDAO.getAddedBrokenPeriodPaysForEmp(lLongHrEisEmpId, lLongYearId, lLongMonthId, lStrBillType);
						List DataForDisplayList = new ArrayList();
						for (Integer lInt = 0; lInt < lListAddedBrokenPeriodPays.size(); lInt++)
						{
							List lListAddedAllowances = new ArrayList();
							List lListAddedAllowancesNew = new ArrayList();
							List lListTempAddedAllowances = new ArrayList();
							List lListAddedDeductions = new ArrayList();
							List lListAddedDeductionsNew = new ArrayList();
							List lListTempAddedDeductions = new ArrayList();
							BrokenPeriodPayCustomVO brokenPeriodPayCustomVO = new BrokenPeriodPayCustomVO();
							
							MstBrokenPeriodPay brokenPeriodPay = lListAddedBrokenPeriodPays.get(lInt);
							brokenPeriodPayCustomVO.setFromDate(brokenPeriodPay.getFromDate());
							brokenPeriodPayCustomVO.setToDate(brokenPeriodPay.getToDate());
							brokenPeriodPayCustomVO.setNoOfDays(brokenPeriodPay.getNoOfDays());
							brokenPeriodPayCustomVO.setBasicPay(brokenPeriodPay.getBasicPay());
							brokenPeriodPayCustomVO.setNetPay(brokenPeriodPay.getNetPay());
							brokenPeriodPayCustomVO.setReason(brokenPeriodPay.getReason());
							brokenPeriodPayCustomVO.setRemarks(brokenPeriodPay.getRemarks());
							
							lListTempAddedAllowances = lObjBrokenPeriodDAO.getAddedAllowancesForEmp(lListAddedBrokenPeriodPays.get(lInt).getBrokenPeriodId());
							lListAddedAllowances.addAll(lListTempAddedAllowances);
							
							for (int i = 0; i < (lListAllowancesForEmp != null ? lListAllowancesForEmp.size() : 0); i++)
							{
								Long allowCode = (Long) (((Object[]) lListAllowancesForEmp.get(i))[0]);
								boolean found = false;
								for (int j = 0; j < (lListAddedAllowances != null ? lListAddedAllowances.size() : 0); j++)
								{
									Object[] data = (Object[]) lListAddedAllowances.get(j);
									if (Arrays.asList(data).contains(allowCode))
									{
										lListAddedAllowancesNew.add(data);
										found = true;
										break;
									}
								}
								if (!found)
								{
									String allowDesc = (String) (((Object[]) lListAllowancesForEmp.get(i))[1]);
									Object newData[] = { 0, 0, allowCode, 0, allowDesc };
									lListAddedAllowancesNew.add(newData);
								}
							}
							brokenPeriodPayCustomVO.setAllowList(lListAddedAllowancesNew);
							
							
							
							lListTempAddedDeductions = lObjBrokenPeriodDAO.getAddedDeductionsForEmp(lListAddedBrokenPeriodPays.get(lInt).getBrokenPeriodId());
							lListAddedDeductions.addAll(lListTempAddedDeductions);
							
							for (int i = 0; i < (lListDeductionsForEmp != null ? lListDeductionsForEmp.size() : 0); i++)
							{
								Long deducCode = (Long) (((Object[]) lListDeductionsForEmp.get(i))[0]);
								boolean found = false;
								for (int j = 0; j < (lListAddedDeductions != null ? lListAddedDeductions.size() : 0); j++)
								{
									Object[] data = (Object[]) lListAddedDeductions.get(j);
									if (Arrays.asList(data).contains(deducCode))
									{
										lListAddedDeductionsNew.add(data);
										found = true;
										break;
									}
								}
								if (!found)
								{
									String deducDesc = (String) (((Object[]) lListDeductionsForEmp.get(i))[1]);
									Object newData[] = { 0, 0, deducCode, 0, deducDesc };
									lListAddedDeductionsNew.add(newData);
								}
							}

							//Long eisEmpId = lListAddedBrokenPeriodPays.get(lInt).getEisEmpId();  lLongYearId, lLongMonthId
							
							//generated = lObjBrokenPeriodDAO.isGenerated(lLongHrEisEmpId, lLongMonthId, lLongYearId);
							brokenPeriodPayCustomVO.setDeductList(lListAddedDeductionsNew);
							DataForDisplayList.add(brokenPeriodPayCustomVO);
							
						}
						inputMap.put("yearId", lStrYear);
						inputMap.put("monthId", lStrMonth);
						
						/*inputMap.put("AddedAllowances", lListAddedAllowancesNew);
						inputMap.put("AddedDeductions", lListAddedDeductionsNew);
						inputMap.put("BrokenPeriodPayList", lListAddedBrokenPeriodPays);*/
						inputMap.put("DataForDisplayList", DataForDisplayList);
						inputMap.put("BrokenPeriodPayListSize", lListAddedBrokenPeriodPays.size());
						inputMap.put("PaysAddedBefore", "Yes");
						inputMap.put("Generated", generated); // 0 = generated, 1(gen or apprvd)
					}
					Long lLongYrId = lObjBrokenPeriodDAO.payBilYr(lLongMonthId, lLongYearId);
					generated = lObjBrokenPeriodDAO.isGenerated(lLongHrEisEmpId, lLongMonthId, lLongYrId);

					//Map ruleValueMap=getRuleValues(Long.valueOf(emloyeeId).toString(),inputMap,Long.parseLong(lStrYear));
					

					inputMap.put("Generated", generated); // 0 = generated, 1(gen or apprvd)
					//String GpfNo = lObjBrokenPeriodDAO.getGPFNo(EmpId);
					//inputMap.put("GpfNo", GpfNo);
				}

			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			objRes.setResultValue(null);
			objRes.setThrowable(ex);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		objRes.setResultValue(inputMap);
		objRes.setViewName("BrokenPeriodPay");

		return objRes;
	}

	public ResultObject saveBrokenPeriodPay(Map inputMap)
	{

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = false;
		Boolean lBlFirstTimeSave = null;
		Long lLongBrknPrdIdForDelete = null;

		try
		{
			setSessionInfo(inputMap);
			BrokenPeriodDAO lObjBrokenPeriodDAO = new BrokenPeriodDAOImpl(MstBrokenPeriodPay.class, serv.getSessionFactory());

			MstBrokenPeriodPay[] lArrMstBrokenPeriodPay = (MstBrokenPeriodPay[]) inputMap.get("lArrMstBrokenPeriodPay");
			List<RltBrokenPeriodAllow> lListRltBrokenPeriodAllow = (List<RltBrokenPeriodAllow>) inputMap.get("lListBrokenPeriodAllows");
			List<RltBrokenPeriodDeduc> lListRltBrokenPeriodDeduc = (List<RltBrokenPeriodDeduc>) inputMap.get("lListBrokenPeriodDeducs");

			Long lLongYear = Long.valueOf(StringUtility.getParameter("year", request).trim());
			Long lLongMonth = Long.valueOf(StringUtility.getParameter("month", request).trim());
			Long lLongEisEmpId = Long.valueOf(StringUtility.getParameter("eisEmpId", request).trim());
			//added by Kinjal 
			String lStrBillType = StringUtility.getParameter("billType", request).trim();
			lBlFirstTimeSave = (!lObjBrokenPeriodDAO.checkBrokenPeriodPayExistsOrNot(lLongEisEmpId, lLongYear, lLongMonth, lStrBillType));
			//lBlFirstTimeSave = (!lObjBrokenPeriodDAO.checkBrokenPeriodPayExistsOrNot(lLongEisEmpId, lLongYear, lLongMonth));
			if (!lBlFirstTimeSave)
			{
				List<MstBrokenPeriodPay> lListBrokenPeriodPayList = lObjBrokenPeriodDAO.getAddedBrokenPeriodPaysForEmp(lLongEisEmpId, lLongYear, lLongMonth, lStrBillType);
				for (Integer lInt = 0; lInt < lListBrokenPeriodPayList.size(); lInt++)
				{
					lLongBrknPrdIdForDelete = lListBrokenPeriodPayList.get(lInt).getBrokenPeriodId();
					lObjBrokenPeriodDAO.deleteAllBrokenPeriodAllowancesForBrknPrdId(lLongBrknPrdIdForDelete);
					lObjBrokenPeriodDAO.deleteAllBrokenPeriodDeductionsForBrknPrdId(lLongBrknPrdIdForDelete);
					lObjBrokenPeriodDAO.deleteAllBrokenPeriodPaysForPk(lLongBrknPrdIdForDelete);
				}
			}

			for (Integer lInt = 0; lInt < lArrMstBrokenPeriodPay.length; lInt++)
			{
				lObjBrokenPeriodDAO.create(lArrMstBrokenPeriodPay[lInt]);
			}

			for (Integer lInt = 0; lInt < lListRltBrokenPeriodAllow.size(); lInt++)
			{
				lObjBrokenPeriodDAO.create(lListRltBrokenPeriodAllow.get(lInt));

			}

			for (Integer lInt = 0; lInt < lListRltBrokenPeriodDeduc.size(); lInt++)
			{
				lObjBrokenPeriodDAO.create(lListRltBrokenPeriodDeduc.get(lInt));

			}

			lBlFlag = true;

		}
		catch (Exception ex)
		{
			resObj.setResultValue(null);
			logger.error(" Error is : " + ex, ex);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			ex.printStackTrace();
			return resObj;
		}

		String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;

	}

	private StringBuilder getResponseXMLDoc(Boolean flag)
	{

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
	
	private Map getRuleValues(String strEmpId,Map inputMap,long finYearId){
		Map ruleMap=new HashMap();
		Map loginMap = (Map) inputMap.get("baseLoginMap");
		try{
		PayBillDAOImpl payDao = new PayBillDAOImpl(HrPayPaybill.class, serv.getSessionFactory());
		PayComponentMasterDAOImpl payComponentMasterDAOImpl = new PayComponentMasterDAOImpl(HrPayAllowDedMst.class, serv.getSessionFactory());
		PayComponentRuleGroupMstDAOImpl payComponentRuleGroupMstDAOImpl = new PayComponentRuleGroupMstDAOImpl(HrPayRuleGrpMst.class, serv.getSessionFactory());
		PayComponentRuleGroupParamRltDAOImpl payComponentRuleGroupParamRltDAOImpl = new PayComponentRuleGroupParamRltDAOImpl(HrPayRuleGrpParamRlt.class, serv.getSessionFactory());
		BrokenPeriodDAOImpl brokenDao=new BrokenPeriodDAOImpl(null, serv.getSessionFactory());
		//Setting Basic details of employee
		EmpPaybillVO hrEisOtherDtlsObj =new EmpPaybillVO();
		hrEisOtherDtlsObj.setEisEmpId(Long.parseLong(inputMap.get("lLongHrEisEmpId").toString()));
		List empObjectList = brokenDao.getEmpBasicDetails(inputMap.get("lLongHrEisEmpId").toString());
		long orgEmpId;
		long eisEmpId;
		long basicAmt;
		long incomeTax;
		long desigId;
		long gradeId;
		long scaleId;
		long scaleStartAmt;
		long scaleEndAmt;
		String dcpsOrGPF;
		long postId;
		long userId;
		long payCommissionId;
		long empType;
		long gradeCode;
		int isAvailedHRA;
		Date empDOB;
		Date empSrvcExp;
		Date empDOJ;
		String isPhyHandicapped;
		long gradePay;
		long empCity;
		long qtrRentAmt = 0;
		long postPSRNo;
		long otherDtlsId;
		String empLname;
		if (empObjectList != null && empObjectList.size() > 0)
			for (int i = 0; i < empObjectList.size(); i++)
			{
				Object[] row = (Object[]) empObjectList.get(i);
				if (row != null){
					orgEmpId = row[0] != null ? Long.valueOf(row[0].toString()) : 0;
					eisEmpId = row[1] != null ? Long.valueOf(row[1].toString()) : 0;
					basicAmt = row[2] != null ? Long.valueOf(row[2].toString()) : 0;
					incomeTax = row[3] != null ? Long.valueOf(row[3].toString()) : 0;
					gradeId = row[4] != null ? Long.valueOf(row[4].toString()) : 0;
					desigId = row[5] != null ? Long.valueOf(row[5].toString()) : 0;
					scaleId = row[6] != null ? Long.valueOf(row[6].toString()) : 0;
					scaleStartAmt = row[7] != null ? Long.valueOf(row[7].toString()) : 0;
					scaleEndAmt = row[8] != null ? Long.valueOf(row[8].toString()) : 0;
					dcpsOrGPF = row[9] != null ? row[9].toString() : "";
					postId = row[10] != null ? Long.valueOf(row[10].toString()) : 0;
					userId = row[11] != null ? Long.valueOf(row[11].toString()) : 0;
					payCommissionId = row[12] != null ? Long.valueOf(row[12].toString()) : 0;
					empType = row[13] != null ? Long.valueOf(row[13].toString()) : 0;
					gradeCode = row[14] != null ? Long.valueOf(row[14].toString()) : 0;
					isAvailedHRA = row[15] != null ? Integer.valueOf(row[15].toString()) : 0;
					empDOB = row[16] != null ? (Date) row[16] : null;
					empSrvcExp = row[17] != null ? (Date) row[17] : null;
					empDOJ = row[18] != null ? (Date) row[18] : null;
					isPhyHandicapped = row[19] != null ? row[19].toString() : "";
					gradePay = row[20] != null ? Long.valueOf(row[20].toString()) : 0;
					empCity = row[21] != null ? Long.valueOf(row[21].toString()) : 0;
					//qtrRentAmt =  row[22]!=null ? Long.valueOf(row[22].toString()):0;
					postPSRNo = row[22] != null ? Long.valueOf(row[22].toString()) : 0;
					otherDtlsId = row[23] != null ? Long.valueOf(row[23].toString()) : 0;
					empLname = row[24] != null ? String.valueOf(row[24]) : "";

					if (orgEmpId != 0 && eisEmpId != 0 && desigId != 0 && gradeId != 0 && scaleId != 0 && postId != 0 && userId != 0)
					{
						
						hrEisOtherDtlsObj.setOrgEmpId(orgEmpId);
						hrEisOtherDtlsObj.setEisEmpId(eisEmpId);
						hrEisOtherDtlsObj.setBasicAmt(basicAmt);
						hrEisOtherDtlsObj.setDesigId(desigId);
						hrEisOtherDtlsObj.setGradeId(gradeId);
						hrEisOtherDtlsObj.setScaleId(scaleId);
						hrEisOtherDtlsObj.setScaleStartAmt(scaleStartAmt);
						hrEisOtherDtlsObj.setScaleEndAmt(scaleEndAmt);
						hrEisOtherDtlsObj.setDcpsOrGPF(dcpsOrGPF);
						hrEisOtherDtlsObj.setPostId(postId);
						hrEisOtherDtlsObj.setIncomeTax(incomeTax);
						hrEisOtherDtlsObj.setUserId(userId);
						hrEisOtherDtlsObj.setPayCommissionId(payCommissionId);
						hrEisOtherDtlsObj.setEmpType(empType);
						hrEisOtherDtlsObj.setGradeCode(gradeCode);
						hrEisOtherDtlsObj.setIsAvailedHRA(isAvailedHRA);
						hrEisOtherDtlsObj.setEmpDOB(empDOB);
						hrEisOtherDtlsObj.setEmpDOJ(empDOJ);
						hrEisOtherDtlsObj.setEmpSrvcExp(empSrvcExp);
						hrEisOtherDtlsObj.setIsPhyHandicapped(isPhyHandicapped);
						hrEisOtherDtlsObj.setGradePay(gradePay);
						hrEisOtherDtlsObj.setEmpCity(empCity);
						hrEisOtherDtlsObj.setQtrRentAmt(qtrRentAmt);
						hrEisOtherDtlsObj.setPostPSRNo(postPSRNo);
						hrEisOtherDtlsObj.setOtherDtlsId(otherDtlsId);
						hrEisOtherDtlsObj.setEmpLname(empLname);
						inputMap.put("basicAmt",Math.round(basicAmt));
						inputMap.put("orgEmpId", orgEmpId);
						inputMap.put("lLongHrEisEmpId",eisEmpId);
						inputMap.put("EmpId", eisEmpId);
						inputMap.put("GPFOrDCPSNo", dcpsOrGPF);
					}
					else
						logger.error("OrgEmpId is zero, or hreisempid is 0 or desig or grade or scale or post id is 0");
				}
				row = null;
			}
		
		List<HrPayRuleGrpMst> activeAllowanceRuleList = (List<HrPayRuleGrpMst>) payComponentRuleGroupMstDAOImpl.getAllActiveRuleList(PayrollConstants.PAYROLL_ALLOWANCE_TYPE);
		inputMap.put("activeAllowanceRuleList", activeAllowanceRuleList);
		activeAllowanceRuleList = null;

		List<HrPayRuleGrpMst> activeDeductionRuleList = (List<HrPayRuleGrpMst>) payComponentRuleGroupMstDAOImpl.getAllActiveDeductionRuleList();
		inputMap.put("activeDeductionRuleList", activeDeductionRuleList);
		activeDeductionRuleList = null;

		List activeAllowRuleParamMpgList = (List) payComponentRuleGroupParamRltDAOImpl.getAllActiveRuleParamMpgList(PayrollConstants.PAYROLL_ALLOWANCE_TYPE);
		inputMap.put("activeAllowRuleParamMpgList", activeAllowRuleParamMpgList);
		activeAllowRuleParamMpgList = null;

		List activeDeducRuleParamMpgList = payComponentRuleGroupParamRltDAOImpl.getAllActiveDeductionRuleParamMpgList();
		inputMap.put("activeDeducRuleParamMpgList", activeDeducRuleParamMpgList);
		activeDeducRuleParamMpgList = null;

		List<HrPayRuleGrpMst> activeAllowanceUsedInFormulaRuleList = (List<HrPayRuleGrpMst>) payComponentRuleGroupMstDAOImpl.getAllActiveRuleList(PayrollConstants.PAYROLL_ALLOWANCE_TYPE, 1);
		inputMap.put("activeAllowanceUsedInFormulaRuleList", activeAllowanceUsedInFormulaRuleList);
		activeAllowanceUsedInFormulaRuleList = null;

		List<HrPayRuleGrpMst> activeDeductionUsedInFormulaRuleList = (List<HrPayRuleGrpMst>) payComponentRuleGroupMstDAOImpl.getAllActiveDeductionRuleList(1);
		inputMap.put("activeDeductionUsedInFormulaRuleList", activeDeductionUsedInFormulaRuleList);
		activeDeductionUsedInFormulaRuleList = null;

		List activeAllowUsedInFormulaRuleParamMpgList = payComponentRuleGroupParamRltDAOImpl.getAllActiveRuleParamMpgList(PayrollConstants.PAYROLL_ALLOWANCE_TYPE, 1);
		inputMap.put("activeAllowUsedInFormulaRuleParamMpgList", activeAllowUsedInFormulaRuleParamMpgList);
		activeAllowUsedInFormulaRuleParamMpgList = null;

		List activeDeducUsedInFormulaRuleParamMpgList = payComponentRuleGroupParamRltDAOImpl.getAllActiveDeductionRuleParamMpgList(1);
		inputMap.put("activeDeducUsedInFormulaRuleParamMpgList", activeDeducUsedInFormulaRuleParamMpgList);
		activeDeducUsedInFormulaRuleParamMpgList = null;

		List<HrPayAllowDedMst> ruleBasedAllowanceList = (List<HrPayAllowDedMst>) payComponentMasterDAOImpl.getPayActiveComponets(PayrollConstants.PAYROLL_ALLOWANCE_TYPE);
		inputMap.put("ruleBasedAllowanceList", ruleBasedAllowanceList);
		ruleBasedAllowanceList = null;

		List<HrPayAllowDedMst> ruleBasedDeductionList = (List<HrPayAllowDedMst>) payComponentMasterDAOImpl.getPayActiveComponets(PayrollConstants.PAYROLL_DEDUCTION_TYPE);
		inputMap.put("ruleBasedDeductionList", ruleBasedDeductionList);
		ruleBasedDeductionList = null;

		long langId = StringUtility.convertToLong(loginMap.get("langId").toString());
		List ruleBasedAllowDeducUsedInFormulaList = payComponentMasterDAOImpl.getPayCompUsedInFormula(0, 0, langId);
		//List<HrPayAllowDedMst> ruleBasedDeductionUsedInFormulaList = (List<HrPayAllowDedMst>) payComponentMasterDAOImpl.getPayCompUsedInFormula(0, 0, langId);

		/**
		 * 
		 * Logic to seperate Allow/Ded List based on the Commision Id Starts
		 * 
		 */
		if (ruleBasedAllowDeducUsedInFormulaList != null && !ruleBasedAllowDeducUsedInFormulaList.isEmpty())
		{
			int dataListSize = ruleBasedAllowDeducUsedInFormulaList.size();
			StringBuilder sbr = null;
			HrPayAllowDedMst allowDedMst = null;
			long commissionId = 0;
			Object[] data = null;
			String sbrString = null;
			for (int ctr = 0; ctr < dataListSize; ctr++)
			{
				data = (Object[]) ruleBasedAllowDeducUsedInFormulaList.get(ctr);
				allowDedMst = data != null && data.length > 0 && data[0] != null ? (HrPayAllowDedMst) data[0] : new HrPayAllowDedMst();
				commissionId = data != null && data.length > 1 && data[1] != null ? (Long) data[1] : 0;

				sbr = new StringBuilder(String.valueOf(commissionId)).append("_");
				switch (allowDedMst.getType())
				{
				case PayrollConstants.PAYROLL_ALLOWANCE_TYPE:
					sbr.append(PayrollConstants.PAYROLL_ALLOWANCE_TYPE);
					break;

				case PayrollConstants.PAYROLL_DEDUCTION_TYPE:
					sbr.append(PayrollConstants.PAYROLL_DEDUCTION_TYPE);
					break;

				default:
					sbr = null;
					break;

				}
				sbrString = String.valueOf(sbr);
				if (inputMap.containsKey(sbrString))
				{
					List<HrPayAllowDedMst> tempDataList = (List<HrPayAllowDedMst>) inputMap.get(sbrString);
					tempDataList.add(allowDedMst);
					inputMap.put(sbrString, tempDataList);
					tempDataList = null;
				}
				else
				{
					List<HrPayAllowDedMst> tempDataList = new ArrayList<HrPayAllowDedMst>();
					tempDataList.add(allowDedMst);
					inputMap.put(sbrString, tempDataList);
					tempDataList = null;
				}

			}
		}
		ruleBasedAllowDeducUsedInFormulaList = null;
		//Quarter dtls
		int monthGiven = Integer.parseInt(inputMap.get("monthGiven").toString());
		int yearGiven = Integer.parseInt(inputMap.get("yearGiven").toString());
		
		Calendar tempCalGiven = Calendar.getInstance();
		tempCalGiven.set(Calendar.YEAR, yearGiven);
		tempCalGiven.set(Calendar.MONTH, (monthGiven - 1));
		tempCalGiven.set(Calendar.DAY_OF_MONTH, 1);
		Date givenDate = tempCalGiven.getTime();
		List empQtrRentList = brokenDao.getQtrAmount(strEmpId,givenDate);
		logger.info("Quater Fetching Query End time" + System.currentTimeMillis());
		Map qtrMap=new HashMap();
		for (int qtrCounter = 0; qtrCounter < empQtrRentList.size(); qtrCounter++)
		{
			Object[] row = (Object[]) empQtrRentList.get(qtrCounter);
			long leisEmpId = row[0] != null ? Long.valueOf(row[0].toString()) : 0;
			int qtrRent = row[1] != null ? Integer.valueOf(row[1].toString()) : 0;

			if (!qtrMap.containsKey(leisEmpId))
			{
				qtrMap.put(leisEmpId, qtrRent);
				hrEisOtherDtlsObj.setQtrRentAmt(qtrRent);
			}
		}

		empQtrRentList = null;
		
		//	Eis dtls
		Map empGisMap = new HashMap();
		logger.info("GIS Fetching Query Start time" + System.currentTimeMillis());
		List empGisList = brokenDao.getEisGrade(strEmpId);
		logger.info("GIS Fetching Query End time" + System.currentTimeMillis());
		Object[] row = null;
		int empGisSize = empGisList.size();
		for (int gisCounter = 0; gisCounter < empGisSize; gisCounter++)
		{
			row = (Object[]) empGisList.get(gisCounter);
			long gisGradeId = row[0] != null ? Long.valueOf(row[0].toString()) : 0;
			long gisGradeCode = row[1] != null ? Long.valueOf(row[1].toString()) : 0;
			long empId = row[2] != null ? Long.valueOf(row[2].toString()) : 0;

			if (!empGisMap.containsKey(empId))
			{
				empGisMap.put(empId, gisGradeId + "~" + gisGradeCode);
				hrEisOtherDtlsObj.setGradeId(gisGradeId);
				hrEisOtherDtlsObj.setGisGradeCode(gisGradeCode);
			}
		}

		empGisList = null;
		
		
		//Emp Offc MPG
		inputMap.put("hrEisOtherDtls", hrEisOtherDtlsObj);
		Map empOfficeMap = new HashMap();
		HrPayOfficePostMpgDAOImpl hrPayOfficePostMpgDAOImpl = new HrPayOfficePostMpgDAOImpl(HrPayOfficepostMpg.class, serv.getSessionFactory());
		logger.info("Office Post Fetching Query Start time" + System.currentTimeMillis());
		List officePostList = brokenDao.getOfficeClass(hrEisOtherDtlsObj.getOrgEmpId());
		logger.info("Office Post Fetching Query End time" + System.currentTimeMillis());
		int officePostListSize = officePostList.size();
		row = null;
		for (int officeCnt = 0; officeCnt < officePostListSize; officeCnt++)
		{
			row = (Object[]) officePostList.get(officeCnt);
			long officePostId = row[0] != null ? Long.valueOf(row[0].toString()) : 0;
			String OfficeCityClass = row[1] != null ? String.valueOf(row[1].toString()) : "";

			if (!empOfficeMap.containsKey(officePostId))
			{
				empOfficeMap.put(officePostId, OfficeCityClass);
				hrEisOtherDtlsObj.setPostCityClass(OfficeCityClass);
			}
		}

		officePostList = null;
	//dcps not required
		
		Map empDCPSValMap = new HashMap();
		logger.info("Congtribution Fetching Query Start time" + System.currentTimeMillis());
		List empDCPSValList = brokenDao.getDCPSValues(strEmpId, monthGiven, finYearId);
		logger.info("Contribution Fetching Query End time" + System.currentTimeMillis());

		if (empDCPSValList != null && empDCPSValList.size() > 0)
		{
			empDCPSValMap = generateDCPSMap(empDCPSValList);
			logger.info("DCPS Map in GenerateBillCalculation is " + empDCPSValMap);
		}

		inputMap.put("empDCPSValMap", empDCPSValMap);
		empDCPSValList = null;
		//long empCurrentStatus=1; //Active components
		int isPTMappedList = brokenDao.isComponenetMapped(strEmpId, 2500135, 35);//for PT
		Boolean isPTMappedMap=false;
		inputMap.put("isPTMapped","0");
		if(isPTMappedList>0)
			inputMap.put("isPTMapped","1");
		
		
		
		
		logger.info("Allowance Fetching Query Start time" + System.currentTimeMillis());
		List<HrPayAllowTypeMst> allowTypeMstList = brokenDao.getEmpMappedAllownace(Long.parseLong(strEmpId),gStrLocationCode);
		logger.info("Allowance Fetching Query End time" + System.currentTimeMillis());
		logger.info("Emp compo mapping list for Allowance size is " + allowTypeMstList.size());
		Map<Long, List<AllwValCustomVO>> empAllowCompoMap = generateEmpAllowCompoMap(allowTypeMstList);
		inputMap.put("empAllowCompoMap", empAllowCompoMap);

		allowTypeMstList = null;
		

		//20 jan 2012 changes here 
		DeptCompMPGDAOImpl deptCompMPGDAOImpl = new DeptCompMPGDAOImpl(HrPayLocComMpg.class, serv.getSessionFactory());
		inputMap.put("mapEditableAllowance", generatemapForAllEditableAllowance(brokenDao.getAllActiveEditableAllowance(Long.parseLong(strEmpId),gStrLocationCode)));
		inputMap.put("mapEditableDeduction", generatemapForAllEditableDeduction(brokenDao.getAllActiveEditableDeduction(Long.parseLong(strEmpId),gStrLocationCode)));
		deptCompMPGDAOImpl = null;

		DeductionDtlsDAOImpl deductionDaoImpl = new DeductionDtlsDAOImpl(HrPayDeductionDtls.class, serv.getSessionFactory());
		logger.info("Deductions Fetching Query Start time" + System.currentTimeMillis());
		List<HrPayDeducTypeMst> deducTypeMstList = brokenDao.getEmpMappedDeduction(Long.parseLong(strEmpId),gStrLocationCode);
		logger.info("Deductions Fetching Query End time" + System.currentTimeMillis());
		logger.info("Emp compo mapping list for Deduction size is " + deducTypeMstList.size());
		Map<Long, List<DeductionCustomVO>> empDeducCompoMap = generateEmpDeducCompoMap(deducTypeMstList);
		inputMap.put("empDeducCompoMap", empDeducCompoMap);
		deductionDaoImpl = null;
		deducTypeMstList = null;
		
		/*NonGovDeducDAO nonGovDeducDAO = new NonGovDeducDAOImpl(HrPayNonGovDeduction.class, serv.getSessionFactory());
		logger.info("Non Gov Fetching Query Start time" + System.currentTimeMillis());
		List empNonGovList = nonGovDeducDAO.getNonGovDeducDataByEmps(empIdsStr, locId,billNo,givenDate);
		logger.info("Non Gov Fetching Query End time" + System.currentTimeMillis());
		Map<Long, Map<Integer, EmpNonGovVO>> empNonGovMap = generateNonGovMap(empNonGovList);
		inputMap.put("empNonGovMap", empNonGovMap);
		
		empNonGovList = null;
		empNonGovMap = null;

		List checkNonGovPayslipList = null; 
		//nonGovDeducDAO.checkNonGovtPayslipEntries(empIdsStr, monthGiven, yearGiven);
		Map<Long, Map<Integer, Long>> nonGovPayslipMap = generatePayslipNonGovMap(checkNonGovPayslipList);
		inputMap.put("nonGovPayslipMap", nonGovPayslipMap);

		checkNonGovPayslipList = null;
		nonGovPayslipMap = null;
		nonGovDeducDAO = null;

		List<EmpBrokenPeriodVO> empBrokenList = payDao.getBrokenPeriodData(empIdsStr, monthGiven, sgvcFinYearMst.getFinYearId(),locId,billNo,givenDate);
		logger.info("Broken Fetching Query Start time" + System.currentTimeMillis());
		Map<Long, List<EmpBrokenPeriodVO>> empBrokenMap = generateBrokenMap(empBrokenList);
		logger.info("Broken Fetching Query End time" + System.currentTimeMillis());
		inputMap.put("empBrokenMap", empBrokenMap);

		empBrokenList = null;
		empBrokenMap = null;*/

		//long empCurrentStatus=1; //Active components
		/*List isPTMappedList = empAllwMapDAOImpl.isComponenetMapped(empIdsStr, 2500135, 35,locId,billNo,givenDate);//for PT
		Map<Long, Long> isPTMappedMap = generatePTMap(isPTMappedList);
		inputMap.put("isPTMappedMap", isPTMappedMap);

		isPTMappedList = null;
		isPTMappedMap = null;

		OtherDetailDAOImpl detailDAOImpl = new OtherDetailDAOImpl(HrEisOtherDtls.class, serv.getSessionFactory());
		Map allowEdpMap = detailDAOImpl.getEdpAllwMap(0, locId);
		Map deducEdpMap = detailDAOImpl.getEdpDeducMap(0, locId);

		detailDAOImpl = null;

		inputMap.put("allowEdpMap", allowEdpMap);
		inputMap.put("deducEdpMap", deducEdpMap);

		allowEdpMap = null;
		deducEdpMap = null;

		int paybill_Month = monthGiven;
		int paybill_Year = yearGiven;
		
*/		//setting basic details of employee
		
		inputMap=executeCoreLogic(inputMap);
		}
		catch(Exception ex)
		{
			
			logger.error(" Error is : " + ex, ex);
			
			
		}
		

		//ended by manish 
		
		return inputMap;
	}
	
	public Map generateEmpAllowCompoMap(List empAllowCompoList)
	{
		Map empAllowCompoMap = new HashMap();
		List<AllwValCustomVO> empAllwCompoLst = new ArrayList();
		long prevEmpId = 0;
		if (empAllowCompoList != null && empAllowCompoList.size() > 0)
		{
			int size = empAllowCompoList.size();
			Object[] row = null;
			for (int i = 0; i < size; i++)
			{
				row = (Object[]) empAllowCompoList.get(i);
				if (row != null)
				{
					long empId = row[0] != null ? Long.valueOf(row[0].toString()) : 0;
					if (prevEmpId == 0)
						prevEmpId = empId;
					if (prevEmpId != empId)
					{
						logger.info("Going to put in Map");
						logger.info("Prev emp id is " + prevEmpId + " new Emp Id is " + empId);
						logger.info("empAllwCompoLst is " + empAllwCompoLst);

						empAllowCompoMap.put(prevEmpId, empAllwCompoLst);
						empAllwCompoLst = new ArrayList();
						prevEmpId = empId;
					}
					if (i == (empAllowCompoList.size() - 1))
					{
						empAllowCompoMap.put(empId, empAllwCompoLst);
					}
					long allowId = row[1] != null ? Long.valueOf(row[1].toString()) : 0;
					String allowDesc = row[2] != null ? String.valueOf(row[2].toString()) : "";
					long allwDedId = row[3] != null ? Long.valueOf(row[3].toString()) : 0;
					AllwValCustomVO allwCustomVO = new AllwValCustomVO();
					if (empId != 0 && allowId != 0)
					{
						allwCustomVO.setAllwID(allowId);
						allwCustomVO.setAllowDesc(allowDesc);
						allwCustomVO.setAllwDedId(allwDedId);
						empAllwCompoLst.add(allwCustomVO);
					}
					allwCustomVO = null;
				}
			}
		}
		return empAllowCompoMap;
	}
	public Map generateDCPSMap(List empDCPSValList)
	{

		Map<Long, Map> empDCPSMap = new HashMap<Long, Map>();
		long prevEmpId = 0;
		Map revisedEmpDCPSList = new HashMap();
		if (empDCPSValList != null && empDCPSValList.size() > 0)
		{
			Object[] row = null;
			for (int i = 0; i < empDCPSValList.size(); i++)
			{
				row = (Object[]) empDCPSValList.get(i);
				if (row != null)
				{
					long empId = row[2] != null ? Long.valueOf(row[2].toString()) : 0;
					double contributionAmt = row[0] != null ? Double.valueOf(row[0].toString()) : 0;
					long contriType = row[1] != null ? Long.valueOf(row[1].toString()) : 0;

					if (prevEmpId == 0)
						prevEmpId = empId;
					if (prevEmpId != empId)
					{
						logger.info("Going to put in Map");
						logger.info("Prev emp id is " + prevEmpId + " new Emp Id is " + empId);
						logger.info("revisedEmpDCPSList is " + revisedEmpDCPSList);

						empDCPSMap.put(prevEmpId, revisedEmpDCPSList);
						revisedEmpDCPSList = new HashMap();
						prevEmpId = empId;
					}
					if (i == (empDCPSValList.size() - 1))
					{
						empDCPSMap.put(empId, revisedEmpDCPSList);
					}
					if (!revisedEmpDCPSList.containsKey(contriType))
						revisedEmpDCPSList.put(contriType, contributionAmt);

				}
			}
		}
		return empDCPSMap;
	}

	//20 jan 2012
	public Map<Long, List<AllwValCustomVO>> generatemapForAllEditableAllowance(List inputlist)
	{
		Map<Long, List<AllwValCustomVO>> returnMap = new HashMap<Long, List<AllwValCustomVO>>();
		if (inputlist != null && inputlist.size() > 0)
		{
			int size = inputlist.size();
			Object[] row = null;
			for (int i = 0; i < size; i++)
			{
				row = (Object[]) inputlist.get(i);
				Long empId = row[0] != null ? Long.valueOf(row[1].toString()) : 0;
				if (row != null)
				{
					//long empId = row[0]!=null?Long.valueOf(row[0].toString()):0;
					if (returnMap.get(empId) == null)// if new employee enconutered
					{
						List<AllwValCustomVO> tempList = new ArrayList<AllwValCustomVO>();
						AllwValCustomVO allwValCustomVO = new AllwValCustomVO();
						allwValCustomVO.setAllwID(Long.valueOf(row[2].toString()));
						allwValCustomVO.setAllowanceVal(Long.valueOf(row[3].toString()));
						tempList.add(allwValCustomVO);
						allwValCustomVO = null;
						returnMap.put(empId, tempList);
						tempList = null;
					}
					else
					{
						List<AllwValCustomVO> tempList = returnMap.get(empId);
						AllwValCustomVO allwValCustomVO = new AllwValCustomVO();
						allwValCustomVO.setAllwID(Long.valueOf(row[2].toString()));
						allwValCustomVO.setAllowanceVal(Long.valueOf(row[3].toString()));
						tempList.add(allwValCustomVO);
						allwValCustomVO = null;
						returnMap.put(empId, tempList);
						tempList = null;
					}
				}
			}
		}
		return returnMap;
	}

	public Map<Long, List<DeductionCustomVO>> generatemapForAllEditableDeduction(List inputlist)
	{
		Map<Long, List<DeductionCustomVO>> returnMap = new HashMap<Long, List<DeductionCustomVO>>();
		if (inputlist != null && inputlist.size() > 0)
		{
			int size = inputlist.size();
			Object[] row = null;
			Long empId = 0L;
			for (int i = 0; i < size; i++)
			{
				row = (Object[]) inputlist.get(i);
				if (row != null)
				{
					empId = row[0] != null ? Long.valueOf(row[1].toString()) : 0;
					logger.info(" - r0 " + row[0].toString() + " r1 " + row[1].toString() + " r2 " + row[2].toString() + " r3 " + row[3].toString());
					/*//System.out.println("ITERATION "+i+" - r0 "+row[0].toString()+" r1 "+row[1].toString()+" r2 "+row[2].toString()+" r3 "+row[3].toString());
					//System.out.println(Long.parseLong(row[1].toString())+" hashkey "+Long.valueOf((row[1].toString()).hashCode()));
					*/
					if (returnMap.get(empId) == null)// if new employee enconutered
					{
						logger.info(" -- new list created for " + empId);
						////System.out.println(" -- new list created for "+empId);
						List<DeductionCustomVO> tempList = new ArrayList<DeductionCustomVO>();
						DeductionCustomVO deducValCustomVO = new DeductionCustomVO();
						deducValCustomVO.setDeducId(Long.valueOf(row[2].toString()));
						deducValCustomVO.setDeductionVal(Double.valueOf(row[3].toString()));
						tempList.add(deducValCustomVO);
						deducValCustomVO = null;
						/*//System.out.println(" --- new list created for "+deducValCustomVO.getDeductionVal());
						//System.out.println("r0 "+row[0].toString()+" r1 "+row[1].toString()+" r2 "+row[2].toString()+" r3 "+row[3].toString());
						//System.out.println("--------------------------------------------------entered into list");
						*/
						returnMap.put(empId, tempList);
						tempList = null;
					}
					else
					{
						logger.info(" -- existing list used for " + empId);
						logger.info(" -- existing list used for " + empId);
						List<DeductionCustomVO> tempList = returnMap.get(empId);
						DeductionCustomVO deducValCustomVO = new DeductionCustomVO();
						deducValCustomVO.setDeducId(Long.valueOf(row[2].toString()));
						deducValCustomVO.setDeductionVal(Double.valueOf(row[3].toString()));
						tempList.add(deducValCustomVO);
						deducValCustomVO = null;
						returnMap.put(empId, tempList);
						tempList = null;
					}
				}
			}
		}
		return returnMap;
	}



	public Map executeCoreLogic(Map inputMap)throws Exception
	{
		
		Map resultMap = inputMap;
		if(resultMap.containsKey("payBillVO"))
			resultMap.remove("payBillVO");
		
		ResourceBundle resourceBundle = ResourceBundle.getBundle("resources.Payroll");			
		long loanLookupId = Long.parseLong(resourceBundle.getString("loanLookupId"));
		long advLookupId = Long.parseLong(resourceBundle.getString("advLookupId"));
		
		long sixthPayCommId = Long.parseLong(resourceBundle.getString("commissionSixId")); 
		long commissionFiveId =  Long.parseLong(resourceBundle.getString("commissionFiveId"));
		long commissionFourId =  Long.parseLong(resourceBundle.getString("commissionFourId"));
		long commissionThreeId =  Long.parseLong(resourceBundle.getString("commissionThreeId"));
		long commissionTwoId =  Long.parseLong(resourceBundle.getString("commissionTwoId"));
		long commissionOneId =  Long.parseLong(resourceBundle.getString("commissionOneId"));
		long commissionSevenId =  Long.parseLong(resourceBundle.getString("commissionSevenId"));
		
		long hrrId = Long.parseLong(resourceBundle.getString("hrrId"));						
		BrokenPeriodDAO objBrokenPeriodDAO=new BrokenPeriodDAOImpl(null,serv.getSessionFactory());
		ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");									 		
		long advEMI=0;
		try
		{

			long psrNo = 0;			
			int grade=0;				
			long scale_start_amt=0;												
			EmpPaybillVO empPaybillVO = (EmpPaybillVO)inputMap.get("hrEisOtherDtls");
			long empId = empPaybillVO.getEisEmpId();
			Map loginMap = (Map) inputMap.get("baseLoginMap");
			long dbId=StringUtility.convertToLong(loginMap.get("dbId").toString());
	        CmnDatabaseMstDaoImpl cmnDatabaseMstDaoImpl=new CmnDatabaseMstDaoImpl(CmnDatabaseMst.class,serv.getSessionFactory());
			CmnDatabaseMst cmnDatabaseMst=cmnDatabaseMstDaoImpl.read(dbId);
			logger.info("cmnDatabaseMst in Core Logic is " + cmnDatabaseMst);
			logger.info("DB id is " + cmnDatabaseMst.getDbDescription());
			long userId = StringUtility.convertToLong(loginMap.get("userId").toString());
			OrgUserMstDaoImpl orgUserMstDaoImpl = new OrgUserMstDaoImpl(OrgUserMst.class, serv.getSessionFactory());
			OrgUserMst orgUserMst = orgUserMstDaoImpl.read(userId);				
			long postId = StringUtility.convertToLong(loginMap.get("primaryPostId").toString());
			OrgPostMstDaoImpl orgPostMstDaoImpl = new OrgPostMstDaoImpl(OrgPostMst.class, serv.getSessionFactory());
			OrgPostMst orgPostMst = orgPostMstDaoImpl.read(postId);
			HrPayPaybill payBillVO = new HrPayPaybill();
			long payBillId = inputMap.get("payBillId")!=null?Long.valueOf(inputMap.get("payBillId").toString()):0;
			long empid=empPaybillVO.getEisEmpId();
			long orgEmpId = empPaybillVO.getOrgEmpId();

			logger.info("**********************************************************the emp id is "+empid);
			logger.info("Core Logic PerEmployee Start Time" + System.currentTimeMillis());
			//long basic=0;							
			long CurrBasic=0;				
			int isAvailedHRA=0;
			
			String city="";
			boolean isHandicapped=true;
	    	logger.info(" isAvailedHRA in coreLogic is: "+isAvailedHRA);
			logger.info("the currBasic "+CurrBasic);
			logger.info("the city "+city);
			logger.info("the grade  "+grade);
			logger.info("the isHandicapped "+isHandicapped);
			logger.info("the scale_start_amt "+scale_start_amt);
			logger.info("isAvailedHRA : "+isAvailedHRA);
			long payCommissionId = empPaybillVO.getPayCommissionId();
			logger.info("Pay Commission is --->"+payCommissionId);
			Map input  = this.generatePassMap(inputMap);
			inputMap.put("hrEisOtherDtls",empPaybillVO);
			Map inputRuleEngine  = this.generatePassMapForRuleEngine(inputMap);
			payBillVO.setBasic0101(Math.round((Double)input.get("revBasic")));
			if(input.get("DP") != null && String.valueOf(input.get("DP")).length() > 0){
			logger.info("Dp value set in core logic is "+Math.round((Double)input.get("DP")) );
			payBillVO.setAllow0119(Math.round((Double)input.get("DP")));}
			double parResult=0;
			
			List<AllwValCustomVO> allowTypeMst = new ArrayList();
			Map empAllowCompoMap = (HashMap) (inputMap.get("empAllowCompoMap")!=null ? inputMap.get("empAllowCompoMap"):new HashMap());
			if(empAllowCompoMap.containsKey(empPaybillVO.getEisEmpId())) {
				logger.info("Allowance mapping map found for emp id " + empPaybillVO.getEisEmpId());
				logger.info(" " + empAllowCompoMap.get(empPaybillVO.getEisEmpId()));
			    allowTypeMst = (List) (empAllowCompoMap.get(empPaybillVO.getEisEmpId())!=null ? empAllowCompoMap.get(empPaybillVO.getEisEmpId()):new ArrayList());
			}
			empAllowCompoMap = null;
			SalaryRules salaryRules=new SalaryRules();
			SalaryRules_6thPay salaryRuls6thPay = new SalaryRules_6thPay();
			
			Map allowEdpMap = (HashMap) (inputMap.containsKey("allowEdpMap")?inputMap.get("allowEdpMap"):new HashMap());

			String edpCode=null;
			double totalCompAllw=0;
			logger.info("allowList whole size is "+allowTypeMst.size());
			BigDecimal amt = new BigDecimal(0);
			Map<Long, HrPayEmpSalaryTxn>  ruleMap= new HashMap<Long, HrPayEmpSalaryTxn>();
			
			if(empPaybillVO.getPayCommissionId()!= PayrollConstants.PAYROLL_COSOLIDATED_PAYCOMMISSION)
			{
				ruleMap = (new PayrollCalculationServiceImpl()).getAllRuleBasedPayCompValue(empId, inputRuleEngine, serv,Long.valueOf(inputRuleEngine.get(PayrollConstants.PAYROLL_PARAMID_PAYCOMSN).toString()),inputMap);
			}
			logger.info("ruleMap is "+ruleMap);
			if(empPaybillVO.getPayCommissionId()== PayrollConstants.PAYROLL_PADHAMANABHAN_PAYCOMMISSION
					|| empPaybillVO.getPayCommissionId()== PayrollConstants.PAYROLL_SHETTY_PAYCOMMISSION
					)
			{
				HrPayEmpSalaryTxn empSalaryTxn = new HrPayEmpSalaryTxn();
				if(empPaybillVO.getPayCommissionId()== PayrollConstants.PAYROLL_SHETTY_PAYCOMMISSION){
					empSalaryTxn.setAmount(new BigDecimal(0));
					empSalaryTxn.setAllwDedCode( PayrollConstants.PAYROLL_HRA_ID);
					ruleMap.put(PayrollConstants.PAYROLL_HRA_ID, empSalaryTxn);
				}
				 empSalaryTxn = new HrPayEmpSalaryTxn();
				 empSalaryTxn.setAmount(new BigDecimal(0));
				 empSalaryTxn.setAllwDedCode( PayrollConstants.PAYROLL_TA_ID);
					
				ruleMap.put( PayrollConstants.PAYROLL_TA_ID, empSalaryTxn);
			}
			int billMonth = Integer.parseInt(inputMap.get("monthGiven").toString());
			int billyear = Integer.parseInt(inputMap.get("yearGiven").toString());
			if(billMonth == 3 && billyear == 2012  && (empPaybillVO.getPayCommissionId() == PayrollConstants.PAYROLL_SIXTH_PAYCOMMISSION
					|| empPaybillVO.getPayCommissionId() == PayrollConstants.PAYROLL_NONGOVT_PAYCOMMISSION
					|| empPaybillVO.getPayCommissionId() == PayrollConstants.PAYROLL_FOURTH_PAYCOMMISSION
					|| empPaybillVO.getPayCommissionId() == PayrollConstants.PAYROLL_PADHAMANABHAN_PAYCOMMISSION))
			{
				HrPayEmpSalaryTxn empSalaryTxn = new HrPayEmpSalaryTxn();
				
				HrPayEmpSalaryTxn empSalaryTxnNew=ruleMap.get(PayrollConstants.PAYROLL_BASIC_PAY_CODE);

				if(empSalaryTxnNew != null)
				{
				BigDecimal tempBasic = empSalaryTxnNew.getAmount();
				BigDecimal tempDaValNew = tempBasic.multiply(BigDecimal.valueOf(PayrollConstants.PAYROLL_DA_RATE_OLD));
				BigDecimal finalVal = tempDaValNew.divide(BigDecimal.valueOf(100),0,BigDecimal.ROUND_HALF_UP);
				empSalaryTxn.setAmount(finalVal);
				
				empSalaryTxn.setAllwDedCode(PayrollConstants.PAYROLL_DA_ID);
				logger.info("DA rate is taking old ");
				ruleMap.put(PayrollConstants.PAYROLL_DA_ID, empSalaryTxn);
				}
			}
			//added by sunitha
			if(billMonth < 11 && billyear <= 2012 && (empPaybillVO.getPayCommissionId() == PayrollConstants.PAYROLL_SIXTH_PAYCOMMISSION	))
			{
				HrPayEmpSalaryTxn empSalaryTxn = new HrPayEmpSalaryTxn();
				
				HrPayEmpSalaryTxn empSalaryTxnNew=ruleMap.get(PayrollConstants.PAYROLL_BASIC_PAY_CODE);

				if(empSalaryTxnNew != null)
				{
				BigDecimal tempBasic = empSalaryTxnNew.getAmount();
				logger.info("Basic Before Adding"+tempBasic.doubleValue());
				BigDecimal tempDaValNew = tempBasic.multiply(BigDecimal.valueOf(PayrollConstants.PAYROLL_NEW_DA_SIXTH_PAY));
				BigDecimal finalVal = tempDaValNew.divide(BigDecimal.valueOf(100),0,BigDecimal.ROUND_HALF_UP);
				empSalaryTxn.setAmount(finalVal);
				logger.info(" gayathri tempDaValNew"+tempDaValNew);
				logger.info("sunitha finalVal"+finalVal);
				logger.info("DA rate is taking old for 6th Pay 65 gayathri");
				empSalaryTxn.setAllwDedCode(PayrollConstants.PAYROLL_DA_ID);
				logger.info("DA rate is taking old for 6th Pay 65 gayathri");
				ruleMap.put(PayrollConstants.PAYROLL_DA_ID, empSalaryTxn);
				}
			}
			//ended by sunitha
			if(billMonth == 3 && billyear == 2012 && (empPaybillVO.getPayCommissionId() == PayrollConstants.PAYROLL_FIFTH_PAYCOMMISSION	))
			{
				HrPayEmpSalaryTxn empSalaryTxn = new HrPayEmpSalaryTxn();
				
				HrPayEmpSalaryTxn empSalaryTxnNew=ruleMap.get(PayrollConstants.PAYROLL_BASIC_PAY_CODE);

				if(empSalaryTxnNew != null)
				{
				BigDecimal tempBasic = empSalaryTxnNew.getAmount();
				logger.info("Basic Before Adding"+tempBasic.doubleValue());
				BigDecimal tempDaValNew = tempBasic.multiply(BigDecimal.valueOf(PayrollConstants.PAYROLL_OLD_DA_FIFTH_PAY));
				BigDecimal finalVal = tempDaValNew.divide(BigDecimal.valueOf(100),0,BigDecimal.ROUND_HALF_UP);
				empSalaryTxn.setAmount(finalVal);
				
				empSalaryTxn.setAllwDedCode(PayrollConstants.PAYROLL_DA_ID);
				logger.info("DA rate is taking old for 5th Pay");
				ruleMap.put(PayrollConstants.PAYROLL_DA_ID, empSalaryTxn);
				}
			}
			//code by Mum dev
			
			else if(billMonth <= 11  && billyear == 2012 && (empPaybillVO.getPayCommissionId() == PayrollConstants.PAYROLL_FIFTH_PAYCOMMISSION	))
			{
				HrPayEmpSalaryTxn empSalaryTxn = new HrPayEmpSalaryTxn();
				
				HrPayEmpSalaryTxn empSalaryTxnNew=ruleMap.get(PayrollConstants.PAYROLL_BASIC_PAY_CODE);

				if(empSalaryTxnNew != null)
				{
				BigDecimal tempBasic = empSalaryTxnNew.getAmount();
				logger.info("Basic Before Adding"+tempBasic.doubleValue());
				BigDecimal tempDaValNew = tempBasic.multiply(BigDecimal.valueOf(139));//da old rate
				BigDecimal finalVal = tempDaValNew.divide(BigDecimal.valueOf(100),0,BigDecimal.ROUND_HALF_UP);
				empSalaryTxn.setAmount(finalVal);
				
				empSalaryTxn.setAllwDedCode(PayrollConstants.PAYROLL_DA_ID);
				logger.info("DA rate is taking old for 139 5th Pay");
				ruleMap.put(PayrollConstants.PAYROLL_DA_ID, empSalaryTxn);
				}
			}
			
			java.util.Set   allowDedSet = ruleMap.keySet();
			Iterator iterator = allowDedSet.iterator();
			Map empValMap=new HashMap();
			while(iterator.hasNext())
			{
				HrPayEmpSalaryTxn empSalaryTxn = (HrPayEmpSalaryTxn)ruleMap.get(iterator.next());
				empValMap.put(empSalaryTxn.getAllwDedCode(), empSalaryTxn.getAmount());

			}
			Object[] allowDedIdArray=allowDedSet.toArray();
			StringBuffer allowDedStr=new StringBuffer();
			for(int i=0;i<allowDedIdArray.length;i++){
				if(i==0)
					allowDedStr.append(allowDedIdArray[i]);
				else{
					allowDedStr.append(",");
					allowDedStr.append(allowDedIdArray[i]);
				}
					
				
			}
			List allowId=objBrokenPeriodDAO.getAllowIDfrmAllowdedCode(allowDedStr.toString());
			List dedId=objBrokenPeriodDAO. getDedIDfrmAllowdedCode(allowDedStr.toString());
			int noOfdays=Integer.parseInt(inputMap.get("totalNoOfDays").toString());
			int fractionOfDays=Integer.parseInt(inputMap.get("lIntNoofdays").toString());
			List allowRuleList=new ArrayList();
			List dedRuleList=new ArrayList();
			Iterator it=allowId.iterator();
			while(it.hasNext()){
				Object[] allowArr=new Object[2];
				Object[] key=(Object[])it.next();
					if(empValMap.containsKey(Long.parseLong(key[1].toString()))){
					allowArr[0]=Long.parseLong(key[0].toString());
					allowRuleList.add(allowArr[0]);
					allowArr[1]=empValMap.get(Long.parseLong(key[1].toString()));
					BigDecimal allowVal = new BigDecimal(allowArr[1].toString());
					allowVal=allowVal.multiply(BigDecimal.valueOf(fractionOfDays));
					allowVal=allowVal.divide(BigDecimal.valueOf(noOfdays), 0, BigDecimal.ROUND_HALF_UP);
					allowRuleList.add(allowVal);			
					logger.info("allowArr[0]="+allowArr[0]+"allowArr="+allowVal);
				}
			}
			it=null;
			it=dedId.iterator();
			while(it.hasNext()){
				Object[] dedArr=new Object[2];
				Object[] key=(Object[])it.next();
				if(empValMap.containsKey(Long.parseLong(key[1].toString()))){
					dedArr[0]=Long.parseLong(key[0].toString());
					dedRuleList.add(dedArr[0]);
					dedArr[1]=empValMap.get(Long.parseLong(key[1].toString()));
					BigDecimal dedVal = new BigDecimal(dedArr[1].toString());
					dedVal=dedVal.multiply(BigDecimal.valueOf(fractionOfDays));
					dedVal=dedVal.divide(BigDecimal.valueOf(noOfdays), 0, BigDecimal.ROUND_HALF_UP);
					dedRuleList.add(dedVal);
					logger.info("dedRuleList[0]="+dedArr[0]+"dedArr="+dedVal);
				}
			}
		
				
			double totalNonCompAllw=0;					
				 Map<Long,List<AllwValCustomVO>> mapEditableAllowance = (Map<Long, List<AllwValCustomVO>>) inputMap.get("mapEditableAllowance");
				
				List<AllwValCustomVO> customVo = mapEditableAllowance.get(empid);
				mapEditableAllowance = null;
				double Val=0;
				String edp="";
				if(customVo!=null)
				for(int i=0;i<customVo.size();i++)
				{
					Val=0;
					long compoId = customVo.get(i).getAllwID(); //empAllowVOList.get(i).getHrPayAllowTypeMst().getAllowCode();
					Val= customVo.get(i).getAllowanceVal();//(double)empAllowVOList.get(i).getEmpAllowAmount();
					Val= Math.round(Val);
					allowRuleList.add(compoId);
					BigDecimal allowVal = new BigDecimal(Val);
					allowVal=allowVal.multiply(BigDecimal.valueOf(fractionOfDays));
					allowVal=allowVal.divide(BigDecimal.valueOf(noOfdays), 0, BigDecimal.ROUND_HALF_UP);
					allowRuleList.add(allowVal);
					
					
				}
				customVo = null;
				//21 jan 2012 end
				logger.info("total of noncomputaional Allowances is "+totalNonCompAllw);
				//calculation of HRA and HRR
				
				
				
				//added by khushal for hrr calculation
				double hrr=0;
				 double gross = totalCompAllw+totalNonCompAllw+Math.round((Double)input.get("revBasic"));
 				List<DeductionCustomVO> deducTypeMst = new ArrayList();
				hrr= empPaybillVO.getQtrRentAmt();
				logger.info("hrr value from EmpDeducDtls is "+hrr);
				dedRuleList.add("28");
				double roundedHrr=((hrr*fractionOfDays)/ noOfdays);
				dedRuleList.add(Math.round(roundedHrr));
				long deducEmpId=empPaybillVO.getOrgEmpId();
				//Map<Long,Long> isPTMappedMap = (HashMap) (inputMap.get("isPTMappedMap")!=null?inputMap.get("isPTMappedMap"):new HashMap());
	
				Object isPtMapped=inputMap.get("isPTMapped");
				
				
				if(isPtMapped!=null && Integer.parseInt(isPtMapped.toString())>0)
			{
				logger.info("Inside PT condition If PT is given");
				inputRuleEngine.put(PayrollConstants.PAYROLL_PARAMID_GROSSPAY, BigDecimal.valueOf(gross).divide(BigDecimal.valueOf(1),0, BigDecimal.ROUND_HALF_UP));
				double pt = 0;
				BigDecimal ptVal= (new PayrollCalculationServiceImpl()).getRuleCompoValue(inputRuleEngine, serv, inputMap, PayrollConstants.PAYROLL_PT_ID);
				if(ptVal.compareTo(BigDecimal.ZERO)>=0)
				{
					pt= ptVal.doubleValue();
					logger.info("Pt i got from rule engine is:"+pt);
					
					
				}
				else
				{
					if(payCommissionId == commissionFiveId || payCommissionId == commissionSevenId)
						pt = salaryRules.calculatePT(input);
					else if(payCommissionId == sixthPayCommId || payCommissionId == commissionFourId || payCommissionId == commissionThreeId || payCommissionId == commissionTwoId || payCommissionId == commissionOneId)
						pt = salaryRuls6thPay.calculatePT(input);
					logger.info("Pt i got from excel is "+pt);
				}
				
				
				 if ((billMonth == 2) && 
				          (pt == 200)) {
				          pt = 300;
				        }

					
			logger.info("Core Logic PerEmployee End Time" + System.currentTimeMillis());
			dedRuleList.add("35");
			double roundedpt=((pt*fractionOfDays)/ noOfdays);
			dedRuleList.add(Math.round(roundedpt));
			
			
			//non computational Deductions
			Map<Long,List<DeductionCustomVO>> mapEditableDeduction = (Map<Long, List<DeductionCustomVO>>) inputMap.get("mapEditableDeduction");
			List<DeductionCustomVO> customVO = mapEditableDeduction.get(empid);
			mapEditableDeduction = null;
			if(customVO!=null)
				for(int i=0;i<customVO.size();i++)
				{
					Val=0;
					BigDecimal dedVal = new BigDecimal(Val);
					long compoId = customVO.get(i).getDeducId(); //deducEditableList.get(i).getHrPayDeducTypeMst().getDeducCode();
					logger.info("compoId:::"+compoId);
					if(compoId!=hrrId ){							
						Val=(double)customVO.get(i).getDeductionVal();//deducEditableList.get(i).getEmpDeducAmount();									

						Val= Math.round(Val);
						if(compoId==72l || compoId==36l || compoId==76l || compoId==75l || compoId==78l || compoId==77l){
							if(!isGPFApplicable(billMonth,billyear,empPaybillVO.getEmpSrvcExp())){
								Val=0l;
							}
						}
						
						dedVal=dedVal.multiply(BigDecimal.valueOf(fractionOfDays));
						dedVal=dedVal.divide(BigDecimal.valueOf(noOfdays), 0, BigDecimal.ROUND_HALF_UP);
						
						
						
					}
					dedRuleList.add(compoId);
					dedRuleList.add(dedVal);
				}
			double basicAmt=Double.valueOf(inputMap.get("basicAmt").toString());
			logger.info("basicAmt b4 round ="+basicAmt);
			basicAmt=((basicAmt*fractionOfDays)/ noOfdays);
			resultMap.put("basicAmt",Math.round(basicAmt));
			resultMap.put("dedRuleList",dedRuleList);
			resultMap.put("allowRuleList",allowRuleList);
			}
		}
		catch(Exception e)
		{
			logger.error("Error occured in generateBillservice corelogic: "+ e.getMessage());
			throw new Exception(e);
			
		}
			return resultMap;		
}


	@SuppressWarnings({ "unchecked", "deprecation" })
	public Map generatePassMap(Map inputMap)
	{
	
		Map passMap = new HashMap();
		
		EmpPaybillVO empPaybillVO = (EmpPaybillVO)inputMap.get("hrEisOtherDtls");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResourceBundle constant =  ResourceBundle.getBundle("resources.eis.eis_Constants");
		ResourceBundle resourceBundle = ResourceBundle.getBundle("resources.Payroll");
		long sixthPayCommId = Long.parseLong(resourceBundle.getString("commissionSixId")); 
		//long commissionFiveId =  Long.parseLong(resourceBundle.getString("commissionFiveId"));
		long commissionFourId =  Long.parseLong(resourceBundle.getString("commissionFourId"));
		long commissionThreeId =  Long.parseLong(resourceBundle.getString("commissionThreeId"));
		long commissionTwoId =  Long.parseLong(resourceBundle.getString("commissionTwoId"));
		long commissionOneId =  Long.parseLong(resourceBundle.getString("commissionOneId"));
		//long commissionSevenId =  Long.parseLong(resourceBundle.getString("commissionSevenId"));
		int contractEmpType = Integer.parseInt(resourceBundle.getString("contract"));
		int fixedEmpType = Integer.parseInt(resourceBundle.getString("fixed"));
		boolean fullmonthcal = Boolean.parseBoolean(constant.getString("fullMonthCalculation"));
		//logic for days of post
		//passMap.put("daysOfPost", 15);
		
		//PayBillDAOImpl payDao= new PayBillDAOImpl(HrPayPaybill.class,serv.getSessionFactory());
		//long postId= empPaybillVO.getPostId();
		int monthGiven = Integer.parseInt(inputMap.get("monthGiven").toString());
		int yearGiven = Integer.parseInt(inputMap.get("yearGiven").toString());
		
		passMap.put("serviceLocator",serv);
		passMap.put("month", monthGiven);
		passMap.put("year", yearGiven);
		passMap.put("fullMonthCal", fullmonthcal);
		
		Calendar calGiven = Calendar.getInstance(); 
		calGiven.set(Calendar.YEAR, yearGiven);
		 calGiven.set(Calendar.MONTH,(monthGiven-1));
		 calGiven.set(Calendar.DAY_OF_MONTH, 1);
			
		int  maxDay=calGiven.getActualMaximum(5);
		passMap.put("maxDaysMonth", maxDay);
		
		long empType = empPaybillVO.getEmpType();
		logger.info("empType is"+empType);
		passMap.put("empType", empType);
		long grade = empPaybillVO.getGradeId();
		passMap.put("grade",grade);
		long Designation =0;
		
		/*long empid=empPaybillVO.getEisEmpId();
		long orgEmpId = empPaybillVO.getOrgEmpId();*/
		
		long scale_start_amt =0;
		long scale_end_amt =0;
				
		
		String jdcpsId = resourceBundle.getString("judgeIds");
		String[] list=jdcpsId.split(",");
		 
		 
		boolean isJudge=false;
		
		if(empType!=contractEmpType && empType!=fixedEmpType)
		{
			Designation=empPaybillVO.getDesigId();
			logger.info("Designation   is **********"+Designation);
			for(int k=0;k<list.length;k++)
			{
				logger.info("first dsgnid is **********"+list[k]);
				//if(list[k]==Designation)
				if(Designation==(int)(Long.parseLong(list[k])))
				{
					isJudge=true;
					break;
				}
			}
			logger.info("isJudge   is **********"+isJudge);
			scale_start_amt=empPaybillVO.getScaleStartAmt();
			scale_end_amt = empPaybillVO.getScaleEndAmt();
		}
		else
		{
			Designation = 0;
			scale_start_amt=empPaybillVO.getScaleStartAmt();
			scale_end_amt = empPaybillVO.getScaleEndAmt();
		}
		
		logger.info("isJudge out if else  is **********"+isJudge);
		passMap.put("isJudge",isJudge);
		passMap.put("designation", Designation);
		passMap.put("scaleStartAmt", scale_start_amt);
		passMap.put("scaleEndAmt", scale_end_amt);
		
		
		long CurrBasic=empPaybillVO.getBasicAmt();

		passMap.put("basic", CurrBasic);
					
		  
			 int daysOfPost=maxDay;
		
		passMap.put("daysOfPost",daysOfPost);
		
		double revisedBasic = Math.round((CurrBasic*daysOfPost) / maxDay);
		passMap.put("revBasic",revisedBasic);
		
		/*SimpleDateFormat sdfWA = new SimpleDateFormat("dd-MMM-yyyy");
		String currentDt=sdfWA.format(new Date());			
		long userIdEmp = empPaybillVO.getUserId();*/
		
		if(empPaybillVO.getGradeId()!=0 && empPaybillVO.getEisEmpId()!=0)
		{
		
		
		if(empPaybillVO.getGisGradeCode()!= 0 && empPaybillVO.getGisGradeId()!=0)
		{
			passMap.put("groupCode",empPaybillVO.getGisGradeCode());
			passMap.put("groupId",empPaybillVO.getGisGradeId());
		}
		else
		{
			passMap.put("groupCode",empPaybillVO.getGradeCode());
			passMap.put("groupId",empPaybillVO.getGradeId());
			
		}
		}
		else
		{	
			passMap.put("groupCode",0);
			passMap.put("groupId",0);
		}
		int isAvailedHRA = empPaybillVO.getIsAvailedHRA();
		passMap.put("isAvailedHRA", isAvailedHRA);
		
		Date doj = empPaybillVO.getEmpDOJ();
		passMap.put("doj",doj);
		SalaryRules rules = new SalaryRules();
		long gradePay=0;
		boolean isHandicapped = Boolean.parseBoolean(empPaybillVO.getIsPhyHandicapped().toLowerCase());
		passMap.put("isHandicapped", isHandicapped);
		double DP=0;
		long payCommissionId = empPaybillVO.getPayCommissionId();					
		if(payCommissionId == sixthPayCommId || payCommissionId == commissionFourId || payCommissionId == commissionThreeId || payCommissionId == commissionTwoId || payCommissionId == commissionOneId)
		{	
			gradePay=empPaybillVO.getGradePay();
			passMap.put("basicAndDP", revisedBasic+DP);
			 
		}
		else if(payCommissionId!=0){			// For Contractual,Fix Pay SGD is null
			//Modified by manish becasuse in case of fifth Pay Commmission grade pay is not theere
			gradePay=0;
			Map DPMap = new HashMap();
			DPMap.put("empType", empType);
			DPMap.put("revBasic", revisedBasic);
			DP = Math.round(rules.calculateDP(DPMap));
			
			DPMap = null;
			//logger.info("in generate map DP value is "+ DP);
			passMap.put("DP",DP);
			passMap.put("basicAndDP", revisedBasic+DP);
		}
		else
		{
			gradePay=0;
		}
		passMap.put("gradePay", gradePay);
		
		String city = "";
		
		if(empPaybillVO.getPostCityClass()!=null && empPaybillVO.getPostCityClass().trim()!="")
		  city=empPaybillVO.getPostCityClass();
		passMap.put("city", city.substring(10,city.length())!=null?city.substring(10,city.length()):"A");
		
		passMap.put("isAvailForce100", 0);
		passMap.put("isAvailForce25", 0);
		passMap.put("isAvailATS30",0);
		passMap.put("isAvailATS50",0);
		passMap.put("quarterId",0); 
		passMap.put("isVehicleAvail", "FALSE");
		passMap.put("distance", 5);
		logger.info("From core logic passing in the map " + isHandicapped + " " + city + " " + gradePay + " " + daysOfPost + " " + maxDay + " " + fullmonthcal);
		return passMap;
		
	}

	
	public Map generatePassMapForRuleEngine(Map inputMap)
	{
		logger.info("inside generatePassMapForRuleEngine method of GenerateBillServiceCoreLogic");
		Map passMap = new HashMap();
		
		EmpPaybillVO empPaybillVO = (EmpPaybillVO)inputMap.get("hrEisOtherDtls");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		
		Map<Integer, Object> paramValueMap = new HashMap<Integer, Object>();
		paramValueMap.put(PayrollConstants.PAYROLL_PARAMID_GRADE, empPaybillVO.getGradeCode());
		if(empPaybillVO.getGisGradeCode()!= 0)
			paramValueMap.put(PayrollConstants.PAYROLL_PARAMID_GRADE, empPaybillVO.getGisGradeCode());
		paramValueMap.put(PayrollConstants.PAYROLL_PARAMID_DESGN, empPaybillVO.getDesigId());
		if(empPaybillVO.getIsPhyHandicapped() != null && empPaybillVO.getIsPhyHandicapped().equals("TRUE"))
			paramValueMap.put(PayrollConstants.PAYROLL_PARAMID_PHYCHALLENGED,1);
		else
			paramValueMap.put(PayrollConstants.PAYROLL_PARAMID_PHYCHALLENGED,0);
		String city= empPaybillVO.getPostCityClass();
		String cityLookup =city.substring(10,city.length())!=null?city.substring(10,city.length()):"A";
		cityLookup+=" City Ctgry";
		logger.info("city Look up is "+cityLookup);
		CmnLookupMstDAOImpl cmnLookupMstDAOImpl = new CmnLookupMstDAOImpl(CmnLookupMst.class,serv.getSessionFactory());
		List<CmnLookupMst> list = cmnLookupMstDAOImpl.getListByColumnAndValue("lookupShortName", cityLookup);
		
		paramValueMap.put(PayrollConstants.PAYROLL_PARAMID_CITY,list.get(0).getLookupId());

		logger.info("city Look up id is "+list.get(0).getLookupId());

		paramValueMap.put(PayrollConstants.PAYROLL_PARAMID_BASICPAY, empPaybillVO.getBasicAmt());
		paramValueMap.put(PayrollConstants.PAYROLL_PARAMID_GRADEPAY, empPaybillVO.getGradePay());
		paramValueMap.put(PayrollConstants.PAYROLL_PARAMID_PAYSCALE, empPaybillVO.getScaleId());			
		paramValueMap.put(PayrollConstants.PAYROLL_PARAMID_PAYCOMSN, empPaybillVO.getPayCommissionId());
		
		if(empPaybillVO.getPayCommissionId() == PayrollConstants.PAYROLL_NONGOVT_PAYCOMMISSION
				|| empPaybillVO.getPayCommissionId() == PayrollConstants.PAYROLL_PADHAMANABHAN_PAYCOMMISSION
				||empPaybillVO.getPayCommissionId() == PayrollConstants.PAYROLL_FOURTH_PAYCOMMISSION)
				paramValueMap.put(PayrollConstants.PAYROLL_PARAMID_PAYCOMSN, PayrollConstants.PAYROLL_SIXTH_PAYCOMMISSION);
		if(empPaybillVO.getPayCommissionId() == PayrollConstants.PAYROLL_SHETTY_PAYCOMMISSION)
				paramValueMap.put(PayrollConstants.PAYROLL_PARAMID_PAYCOMSN, PayrollConstants.PAYROLL_FIFTH_PAYCOMMISSION);
		
		String yearDoj = ""+empPaybillVO.getEmpDOJ();
		
		String monthDOJ=yearDoj.substring(5,7);
		String dayDOJ=yearDoj.substring(8,10);
		String yearDOJ= yearDoj.substring(0,4);
        
		logger.info(new Date().getYear()+1900);
		paramValueMap.put(PayrollConstants.PAYROLL_DOJ_YEAR_ID, yearDOJ);
		if(new Date().getYear()+1900 != Integer.parseInt(yearDOJ) || ((new Date().getYear()+1900 == Integer.parseInt(yearDOJ)) &&  Integer.parseInt(monthDOJ)==1 && Integer.parseInt(dayDOJ)==1))
		//if(new Date().getYear()+1900 != Integer.parseInt(yearDOJ))
			paramValueMap.put(PayrollConstants.PAYROLL_DOJ_YEAR_ID, 1);
		
		logger.info("required year is "+empPaybillVO.getEmpDOJ());
		int monthGiven = Integer.parseInt(inputMap.get("monthGiven").toString());
		int yearGiven = Integer.parseInt(inputMap.get("yearGiven").toString());
		
		if(yearGiven>2014){
			paramValueMap.put(62, 2014);
			paramValueMap.put(63, 4);
		}
		
		else if(yearGiven==2014 && monthGiven>=4){
			paramValueMap.put(62, 2014);
			paramValueMap.put(63, 4);
		}
		
		else if(yearGiven==2014 && monthGiven<4){
			paramValueMap.put(62, 2014);
			paramValueMap.put(63, 1);
		}
		
		else if(yearGiven<2014){
			paramValueMap.put(62, 2014);
			paramValueMap.put(63, 1);
		}
		
		
		
		
		//
		
		
		/*passMap.put("serviceLocator",serv);
		passMap.put("month", monthGiven);
		passMap.put("year", yearGiven);
		passMap.put("fullMonthCal", fullmonthcal);
		
		Calendar calGiven = Calendar.getInstance(); 
		calGiven.set(Calendar.YEAR, yearGiven);
		 calGiven.set(Calendar.MONTH,(monthGiven-1));
		 calGiven.set(Calendar.DAY_OF_MONTH, 1);
			
		int  maxDay=calGiven.getActualMaximum(5);
		passMap.put("maxDaysMonth", maxDay);
		
		long empType = empPaybillVO.getEmpType();
		logger.info("empType is"+empType);
		passMap.put("empType", empType);
		long grade = empPaybillVO.getGradeId();
		passMap.put("grade",grade);
		long Designation =0;
		
		long empid=empPaybillVO.getEisEmpId();
		long orgEmpId = empPaybillVO.getOrgEmpId();
		
		long scale_start_amt =0;
		long scale_end_amt =0;
				
		
		String jdcpsId = resourceBundle.getString("judgeIds");
		String[] list=jdcpsId.split(",");
		 
		 
		boolean isJudge=true;
		
		if(empType!=contractEmpType && empType!=fixedEmpType)
		{
			Designation=empPaybillVO.getDesigId();
			logger.info("Designation   is **********"+Designation);
			for(int k=0;k<list.length;k++)
			{
				logger.info("first dsgnid is **********"+list[k]);
				//if(list[k]==Designation)
				if(Designation==(int)(Long.parseLong(list[k])))
				{
					isJudge=false;
					break;
				}
			}
			logger.info("isJudge   is **********"+isJudge);
			scale_start_amt=empPaybillVO.getScaleStartAmt();
			scale_end_amt = empPaybillVO.getScaleEndAmt();
		}
		else
		{
			Designation = 0;
			scale_start_amt=empPaybillVO.getScaleStartAmt();
			scale_end_amt = empPaybillVO.getScaleEndAmt();
		}
		
		logger.info("isJudge out if else  is **********"+isJudge);
		passMap.put("isJudge",isJudge);
		passMap.put("designation", Designation);
		passMap.put("scaleStartAmt", scale_start_amt);
		passMap.put("scaleEndAmt", scale_end_amt);
		
		
		long CurrBasic=empPaybillVO.getBasicAmt();
		passMap.put("basic", CurrBasic);
					
		  
			 int daysOfPost=maxDay;
		
		passMap.put("daysOfPost",daysOfPost);
		
		double revisedBasic = Math.round((CurrBasic*daysOfPost) / maxDay);
		passMap.put("revBasic",revisedBasic);
		
		SimpleDateFormat sdfWA = new SimpleDateFormat("dd-MMM-yyyy");
		String currentDt=sdfWA.format(new Date());			
		long userIdEmp = empPaybillVO.getUserId();
		
		if(empPaybillVO.getGradeId()!=0 && empPaybillVO.getEisEmpId()!=0)
		{
		
		
		if(empPaybillVO.getGisGradeCode()!= 0 && empPaybillVO.getGisGradeId()!=0)
		{
			passMap.put("groupCode",empPaybillVO.getGisGradeCode());
			passMap.put("groupId",empPaybillVO.getGisGradeId());
		}
		else
		{
			passMap.put("groupCode",empPaybillVO.getGradeCode());
			passMap.put("groupId",empPaybillVO.getGradeId());
			
		}
		}
		else
		{	
			passMap.put("groupCode",0);
			passMap.put("groupId",0);

		
		}
		int isAvailedHRA = empPaybillVO.getIsAvailedHRA();
		passMap.put("isAvailedHRA", isAvailedHRA);
		
		Date doj = empPaybillVO.getEmpDOJ();
		Date expDate = empPaybillVO.getEmpSrvcExp();
					
		
		passMap.put("doj",doj);
	
		//vpf end
		SalaryRules rules = new SalaryRules();
		long gradePay=0;
		boolean isHandicapped = Boolean.parseBoolean(empPaybillVO.getIsPhyHandicapped().toLowerCase());
		passMap.put("isHandicapped", isHandicapped);
		//passMap.put("cityId",hrEisOtherDtls.getCity() );
		double DP=0;
		long payCommissionId = empPaybillVO.getPayCommissionId();					
		if(payCommissionId==sixthPayCommId){
			
			gradePay=empPaybillVO.getGradePay();
			passMap.put("basicAndDP", revisedBasic+DP);
			 
		}
		else if(payCommissionId!=0){			// For Contractual,Fix Pay SGD is null
			//Modified by manish becasuse in case of fifth Pay Commmission grade pay is not theere
			gradePay=0;
			Map DPMap = new HashMap();
			DPMap.put("empType", empType);
			DPMap.put("revBasic", revisedBasic);
			DP = rules.calculateDP(DPMap);
			//logger.info("in generate map DP value is "+ DP);
			passMap.put("DP",DP);
			passMap.put("basicAndDP", revisedBasic+DP);
		}
		else
		{
			gradePay=0;
		}
		passMap.put("gradePay", gradePay);
		
		
		String city = "";
		
		if(empPaybillVO.getPostCityClass()!=null && empPaybillVO.getPostCityClass().trim()!="")
		  city=empPaybillVO.getPostCityClass();
		passMap.put("city", city.substring(10,city.length())!=null?city.substring(10,city.length()):"A");
		passMap.put("quarterId",0); //not used, but fetched from Map in CalculateHRA in Rule Engine
		
		
		//temp values,needs to be removed
		passMap.put("isVehicleAvail", "FALSE");
		passMap.put("distance", 5);
		logger.info("From core logic passing in the map " + isHandicapped + " " + city + " " + gradePay + " " + daysOfPost + " " + maxDay + " " + fullmonthcal);
		*/
		//
		return paramValueMap;
		
	}
	

	private boolean isGPFApplicable(int givenMonth,int givenYear,Date serviceExpDate){
		Calendar calc = Calendar.getInstance(); 
		calc.setTime(serviceExpDate); 
		calc.add(Calendar.MONTH, -3);
		Date allowedGPFDate= calc.getTime();
		logger.info("givenYear="+givenYear+" givenMonth="+givenMonth+" allowedGPFDate.getMonth()"+allowedGPFDate.getMonth()+" <allowedGPFDate.getYear()"+allowedGPFDate.getYear());
	    if(givenYear==(allowedGPFDate.getYear()+ 1900)){
	    	if(givenMonth<=(allowedGPFDate.getMonth()+1))
	    		return true;
	    	else
	    		return false;
	    }
	    else if(givenYear<(allowedGPFDate.getYear()+ 1900)){
	    	return true;
	    }
	    else
	    	return false;
		
		
	}


	public Map generateEmpDeducCompoMap(List empAllowCompoList)
	{
		Map empAllowCompoMap = new HashMap();
		List<AllwValCustomVO> empAllwCompoLst = new ArrayList();
		long prevEmpId = 0;
		if (empAllowCompoList != null && empAllowCompoList.size() > 0)
		{
			int size = empAllowCompoList.size();
			Object[] row = null;
			for (int i = 0; i < size; i++)
			{
				row = (Object[]) empAllowCompoList.get(i);
				if (row != null)
				{
					long empId = row[0] != null ? Long.valueOf(row[0].toString()) : 0;
					if (prevEmpId == 0)
						prevEmpId = empId;
					if (prevEmpId != empId)
					{
						logger.info("Going to put in Map");
						logger.info("Prev emp id is " + prevEmpId + " new Emp Id is " + empId);
						logger.info("empAllwCompoLst is " + empAllwCompoLst);

						empAllowCompoMap.put(prevEmpId, empAllwCompoLst);
						empAllwCompoLst = new ArrayList();
						prevEmpId = empId;
					}
					if (i == (empAllowCompoList.size() - 1))
					{
						empAllowCompoMap.put(empId, empAllwCompoLst);
					}
					long allowId = row[1] != null ? Long.valueOf(row[1].toString()) : 0;
					String allowDesc = row[2] != null ? String.valueOf(row[2].toString()) : "";
					long allwDedId = row[3] != null ? Long.valueOf(row[3].toString()) : 0;
					AllwValCustomVO allwCustomVO = new AllwValCustomVO();
					if (empId != 0 && allowId != 0)
					{
						allwCustomVO.setAllwID(allowId);
						allwCustomVO.setAllowDesc(allowDesc);
						allwCustomVO.setAllwDedId(allwDedId);
						empAllwCompoLst.add(allwCustomVO);
					}
					allwCustomVO = null;
				}
			}
		}
		return empAllowCompoMap;
	}

	public ResultObject calculateEmployeeSalary(Map<String, Object> inputMap ){
		ServiceLocator lObjServLoctr = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		try{
			setSessionInfo(inputMap);
			logger.info("in calculateEmployeeSalary service  ");
			int lLongYear = Integer.valueOf(StringUtility.getParameter("year", request).trim());
			int lLongMonth = Integer.valueOf(StringUtility.getParameter("month", request).trim());
			Long lLongEisEmpId = Long.valueOf(StringUtility.getParameter("eisEmpId", request).trim());
			int lIntNoofdays=Integer.valueOf(StringUtility.getParameter("noOfDays", request).trim());
			PayBillDAOImpl paydao=new PayBillDAOImpl(null, serv.getSessionFactory());
			inputMap.put("lLongHrEisEmpId",lLongEisEmpId);
			inputMap.put("monthId", lLongMonth);
			inputMap.put("yearGiven", lLongYear);
			inputMap.put("monthGiven", lLongMonth);
			Calendar cal2 = Calendar.getInstance();
			cal2.set(Calendar.YEAR, lLongYear);
			cal2.set(Calendar.MONTH, lLongMonth - 1);
			java.util.Date finYrDate = cal2.getTime();
			int totalNoOfDays = cal2.getActualMaximum(Calendar.DAY_OF_MONTH); 
			cal2 = null;
			logger.info("lLongMonth="+lLongMonth+" lLongYear="+lLongYear+" totalNoOfDays="+totalNoOfDays);
			SgvcFinYearMst finYrMst = paydao.getFinYrInfo(finYrDate, 1L);
			inputMap.put("totalNoOfDays", totalNoOfDays);
			inputMap.put("lIntNoofdays", lIntNoofdays);
			inputMap.put("yearId", finYrMst.getFinYearId());
			inputMap.put("monthId", lLongMonth);
			Map ruleValueMap=getRuleValues(lLongEisEmpId.toString(),inputMap,finYrMst.getFinYearId());
			List dedRuleList=(ArrayList)ruleValueMap.get("dedRuleList");
			List allowRuleList=(ArrayList)ruleValueMap.get("allowRuleList");
			String basicAmt=ruleValueMap.get("basicAmt").toString();
			StringBuilder lStrBldXML = new StringBuilder();
			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<dedRuleList>");
			lStrBldXML.append("<![CDATA[");
			lStrBldXML.append(dedRuleList);
			lStrBldXML.append("]]>");					
			lStrBldXML.append("</dedRuleList>");
			lStrBldXML.append("<allowRuleList>");
			lStrBldXML.append("<![CDATA[");
			lStrBldXML.append(allowRuleList);
			lStrBldXML.append("]]>");					
			lStrBldXML.append("</allowRuleList>");
			lStrBldXML.append("<basicAmt>");
			lStrBldXML.append("<![CDATA[");
			lStrBldXML.append(basicAmt);
			lStrBldXML.append("]]>");					
			lStrBldXML.append("</basicAmt>");
			lStrBldXML.append("</XMLDOC>");
			String lStrTempResult = null;				
			logger.info("ajax res "+lStrBldXML.toString());
			lStrTempResult = new AjaxXmlBuilder().addItem("ajax_Key",lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrTempResult);
			resObj.setResultCode(ErrorConstants.SUCCESS);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		
	}
	catch (Exception e) {
		// e.printStackTrace();
		resObj.setResultValue(null);
		resObj.setThrowable(e);
		resObj.setResultCode(ErrorConstants.ERROR);
		resObj.setViewName("errorPage");
		logger.error(" Error is : " + e, e);
	}

	return resObj;
}
	
	
	//Added by kinjal for NSB
/*	public ResultObject checkPreviouslyGeneratedBill(Map inputMap)
	{

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = false;
		Boolean lBlIsBillGen = null;
		Long lLongBrknPrdIdForDelete = null;

		try
		{
			setSessionInfo(inputMap);
			BrokenPeriodDAO lObjBrokenPeriodDAO = new BrokenPeriodDAOImpl(MstBrokenPeriodPay.class, serv.getSessionFactory());
			DcpsCommonDAOImpl commonDao = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			
			Long lLongYear = Long.valueOf(StringUtility.getParameter("year", request).trim());
			Long lLongMonth = Long.valueOf(StringUtility.getParameter("month", request).trim());
			Long lLongEisEmpId = Long.valueOf(StringUtility.getParameter("eisEmpId", request).trim());
			String lStrFromDate = (StringUtility.getParameter("fromDate", request).trim());
			String lStrToDate = (StringUtility.getParameter("todate", request).trim());
			logger.info("lStrFromDate----"+lStrFromDate);
			logger.info("lStrToDate----"+lStrToDate);
			Long yearId = commonDao.getFinYearIdForYearCode(lLongYear.toString());
			
			lBlIsBillGen = lObjBrokenPeriodDAO.checkPreviousBillExistsOrNot(lLongEisEmpId, lLongYear, lLongMonth);
			List prevBrokenPeriodList = lObjBrokenPeriodDAO.checkBrokenPeriodExistsOrNot(lLongEisEmpId, lLongYear, lLongMonth);
			logger.info("lBlIsBillGen   "+lBlIsBillGen);
			logger.info("prevBrokenPeriodList   "+prevBrokenPeriodList.size());
			
			if((!lBlIsBillGen) && (prevBrokenPeriodList.size()==0) )
			{
				lBlFlag = true;
				logger.info("In if"+lBlFlag);
			}
			else if (prevBrokenPeriodList.size()==0)
			{
				lBlFlag = true;
				logger.info("In else if"+lBlFlag);
			}
			else if (prevBrokenPeriodList.size()>0)
			{
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				Date fromDate = sdf.parse(lStrFromDate);
				Date toDate = sdf.parse(lStrToDate);
				Calendar cal = Calendar.getInstance();
			    cal.setTime(fromDate);
			    int fromDateYear = cal.get(Calendar.YEAR);
			    int fromDateMonth = cal.get(Calendar.MONTH);
			    int fromDateDay = cal.get(Calendar.DAY_OF_MONTH);
			    logger.info("fromDateYear"+fromDateYear);
			    logger.info("fromDateMonth"+fromDateMonth);
			    logger.info("fromDateDay"+fromDateDay);
			    
			    Calendar cal1 = Calendar.getInstance();
			    cal.setTime(toDate);
			    int toDateYear = cal.get(Calendar.YEAR);
			    int toDateMonth = cal.get(Calendar.MONTH);
			    int toDateDay = cal.get(Calendar.DAY_OF_MONTH);
			    logger.info("toDateYear"+toDateYear);
			    logger.info("toDateMonth"+toDateMonth);
			    logger.info("toDateDay"+toDateDay);
			    
			    Long prevMonthId= 0l; 
			    Long prevYearId= 0l; 
			    String lStrPrevFromDate=""; 
			    String lStrPrevToDate= ""; 
			    Long diff1 = 0l;
			    Long diff2 = 0l;
			    Long diff3 = 0l;
			    Long diff4 = 0l;
			    Long one_day = 1000l*60l*60l*24l;
			    
			    Iterator it=prevBrokenPeriodList.iterator();
				while(it.hasNext()){
					
					Object row[]=(Object [])it.next();
					
					if(row[0].toString()!=null)
						prevMonthId = Long.parseLong(row[0].toString());
					
					if(row[1].toString()!=null)
						prevYearId = Long.parseLong(row[1].toString());
					
					if(row[2].toString()!=null)
						lStrPrevFromDate = (row[2].toString());
					logger.info("lStrPrevFromDate---"+lStrPrevFromDate);
					
					if(row[3].toString()!=null)
						lStrPrevToDate = (row[3].toString());
					logger.info("lStrPrevToDate---"+lStrPrevToDate);

				}
				Date prevFromDate = sdf.parse(lStrPrevFromDate);
				Date prevToDate = sdf.parse(lStrPrevToDate);
				logger.info("prevFromDate "+prevFromDate);
				logger.info("prevToDate "+prevToDate);
				diff1 = (fromDate.getTime()- prevFromDate.getTime())/(one_day);
				diff2 = (prevToDate.getTime()-fromDate.getTime())/(one_day);
				diff3 = (toDate.getTime()-prevFromDate.getTime())/(one_day);
				diff4 = (prevToDate.getTime()-toDate.getTime())/(one_day);
				if((diff1>=0 && diff2>=0) || (diff3>=0 && diff4>=0))
				{
					lBlFlag = false;
					logger.info("In next if"+lBlFlag);
				}
				else
				{
					lBlFlag = true;
					logger.info("In next else "+lBlFlag);
				}
				logger.info("In else "+lBlFlag);
			}
			

		}
		catch (Exception ex)
		{
			resObj.setResultValue(null);
			logger.error(" Error is : " + ex, ex);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			ex.printStackTrace();
			return resObj;
		}

		String lSBStatus = getResponseXML(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;

	}
*/
	private StringBuilder getResponseXML(Boolean flag)
	{

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

}

