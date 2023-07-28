package com.tcs.sgv.lna.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.helper.WorkFlowHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttachmentMstDAO;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttachmentMstDAOImpl;
import com.tcs.sgv.common.valueobject.CmnAttachmentMst;
import com.tcs.sgv.common.valueobject.RltLevelRole;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.eis.valueobject.HrEisScaleMst;
import com.tcs.sgv.lna.dao.LNABillDtlsDAO;
import com.tcs.sgv.lna.dao.LNABillDtlsDAOImpl;
import com.tcs.sgv.lna.dao.LNAComputerAdvanceDAO;
import com.tcs.sgv.lna.dao.LNAComputerAdvanceDAOImpl;
import com.tcs.sgv.lna.dao.LNAHouseAdvanceDAO;
import com.tcs.sgv.lna.dao.LNAHouseAdvanceDAOImpl;
import com.tcs.sgv.lna.dao.LNAMotorAdvanceDAO;
import com.tcs.sgv.lna.dao.LNAMotorAdvanceDAOImpl;
import com.tcs.sgv.lna.dao.LNARequestProcessDAO;
import com.tcs.sgv.lna.dao.LNARequestProcessDAOImpl;
import com.tcs.sgv.lna.dao.LNAWorkFlowConfigDAO;
import com.tcs.sgv.lna.dao.LNAWorkFlowConfigDAOImpl;
import com.tcs.sgv.lna.valueobject.MstLnaBillDtls;
import com.tcs.sgv.lna.valueobject.MstLnaCompAdvance;
import com.tcs.sgv.lna.valueobject.MstLnaHouseAdvance;
import com.tcs.sgv.lna.valueobject.MstLnaMotorAdvance;
import com.tcs.sgv.lna.valueobject.MstLnaPayrollLoanTypeMpg;

public class LNARequestProcessServiceImpl extends ServiceImpl implements LNARequestProcessService {
	Log gLogger = LogFactory.getLog(getClass());

	private String gStrPostId = null; /* STRING POST ID */

	private Date gDtCurDate = null; /* CURRENT DATE */
	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for Current Date */
	Date gDtCurrDt = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;
	
	/* Global Variable for LangId */
	Long gLngLangId = null;
	
	Long gLngLocationCode = null;
	
	Integer lIntSubtypeSelection = 0;
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/lna/LNAConstants");

	private void setSessionInfo(Map inputMap) {

		request = (HttpServletRequest) inputMap.get("requestObj");
		serv = (ServiceLocator) inputMap.get("serviceLocator");
		gLngPostId = SessionHelper.getPostId(inputMap);
		gStrPostId = gLngPostId.toString();
		gLngUserId = SessionHelper.getUserId(inputMap);
		gDtCurDate = SessionHelper.getCurDate();
		gStrLocationCode = SessionHelper.getLocationCode(inputMap);
		gLngLocationCode = Long.parseLong(gStrLocationCode);
		gLngLangId = SessionHelper.getLangId(inputMap);

	}

	public ResultObject loadLNASearchForm(Map<String, Object> inputMap) throws Exception {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		try {
			setSessionInfo(inputMap);
			String hoMapdOrNot = "Y";
			List lstLoanType = IFMSCommonServiceImpl.getLookupValues("Loan And Advance Request Type", SessionHelper.getLangId(inputMap), inputMap);
			String lStrUser = StringUtility.getParameter("userType", request);
			inputMap.put("lstLoanType", lstLoanType);
			inputMap.put("userType", lStrUser);
			
			//to check HO mapped or not
			LNAWorkFlowConfigDAO lObjLNAWorkFlowConfig = new LNAWorkFlowConfigDAOImpl(null, serv.getSessionFactory());
			hoMapdOrNot = lObjLNAWorkFlowConfig.chkRoleHoEntry(gStrLocationCode);
			
			inputMap.put("hoMapdOrNot", hoMapdOrNot);
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		resObj.setResultValue(inputMap);
		resObj.setViewName("LNAEmpSearch");
		return resObj;
	}

	public ResultObject loadLoanAdvanceRequest(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
      
		try {
			setSessionInfo(inputMap);
			List lnaEmpList = new ArrayList();
			String alertMessage = "";
			Boolean lBlOneRequestExists = false;
			Double lDblPayInPb = null;

			String lStrSevaarthId = StringUtility.getParameter("txtSevaarthId", request);
			String lStrName = StringUtility.getParameter("txtEmployeeName", request);
			String lStrCriteria = StringUtility.getParameter("criteria", request);
			String lStrRequestType = StringUtility.getParameter("requestType", request);
			String lStrElementId = StringUtility.getParameter("elementId", request);
			String lStrDisburse = StringUtility.getParameter("disburse", request);
			Long lLngRequestType = null;
			if (!StringUtility.getParameter("requestType", request).equals("")) {
				lLngRequestType = Long.parseLong(StringUtility.getParameter("requestType", request));
			}
			String lStrUser = StringUtility.getParameter("userType", request);

			inputMap.put("requestType", lStrRequestType);
			inputMap.put("userType", lStrUser);
			inputMap.put("ElementId", lStrElementId);

			LNARequestProcessDAO lObjLNARequestProcessDAO = new LNARequestProcessDAOImpl(MstLnaCompAdvance.class, serv.getSessionFactory());
			DcpsCommonDAO lObjCommonDAO = new DcpsCommonDAOImpl(MstEmp.class, serv.getSessionFactory());
			List lLstdcpsOrGpf = null;
			String lStrDdoCode = null;
			//if (lStrUser.equals("DEO")) {
			if (lStrUser.equals("DEO")) {
				lStrDdoCode = lObjCommonDAO.getDdoCode(gLngPostId);
				createLNAHierarchy(inputMap);
				lLstdcpsOrGpf = lObjLNARequestProcessDAO.getEmployeeDcpsOrGpf(lStrSevaarthId, lStrName, lStrCriteria, lStrDdoCode, gStrLocationCode,lStrUser);
			} else {
				lLstdcpsOrGpf = lObjLNARequestProcessDAO.getEmployeeDcpsOrGpf(lStrSevaarthId, lStrName, lStrCriteria, lStrDdoCode, gStrLocationCode,lStrUser);
			}

			if (lStrCriteria.equals("1")) {
				inputMap.put("SevaarthId", lStrSevaarthId);
			} else if (lStrCriteria.equals("2")) {
				inputMap.put("EmpName", lStrName);
			} else {
				inputMap.put("SevaarthId", lStrSevaarthId);
				inputMap.put("EmpName", lStrName);
			}
			
			Character dcpsOrGpf = null;
			Long lLngOrgEmpId = null;
			Boolean lBIsPriLoanMapped = false;
			Boolean lBIsIntLoanMapped = false;
			String lStrPriLoanName = "";
			String lStrIntLoanName = "";
			Long lLngPayrollPrinId = null;
			Long lLngPayrollIntId = null;
		
			//addded by Adi
			String sevarthID=null;
			Boolean EmpPriCheck=null;
			Boolean EmpIntCheck=null;
			sevarthID=lObjLNARequestProcessDAO.getSevarthIDfromEmpName(lStrName);
			System.out.println("sevarth id:" +sevarthID);
			System.out.println("request type:" +lLngRequestType);
			
			if(lStrRequestType.equals(gObjRsrcBndle.getString("LNA.COMPUTERADVANCE")))
			{
			 EmpPriCheck=lObjLNARequestProcessDAO.isPriLoanAlreadyTaken(sevarthID,lLngRequestType);
			 System.out.println("check:" +EmpPriCheck);
			}
			else if(lStrRequestType.equals(gObjRsrcBndle.getString("LNA.HOUSEADVANCE")))
			{
				
				EmpPriCheck=lObjLNARequestProcessDAO.isPriLoanAlreadyTaken(sevarthID,lLngRequestType);
				
				EmpIntCheck=lObjLNARequestProcessDAO.isIntLoanAlreadyTaken(sevarthID,lLngRequestType);
				
			}
			else if(lStrRequestType.equals(gObjRsrcBndle.getString("LNA.MOTORADVANCE"))){
				
				EmpPriCheck=lObjLNARequestProcessDAO.isPriLoanAlreadyTaken(sevarthID,lLngRequestType);
				
				EmpIntCheck=lObjLNARequestProcessDAO.isIntLoanAlreadyTaken(sevarthID,lLngRequestType);
			}
			//adi
			
				MstLnaPayrollLoanTypeMpg lObjTypeMpg = new MstLnaPayrollLoanTypeMpg();
			LNABillDtlsDAO lObjBillDtlsDAO = new LNABillDtlsDAOImpl(MstLnaBillDtls.class, serv.getSessionFactory());
			if (!lLstdcpsOrGpf.isEmpty()) {
				
				Object[] lArrObj = (Object[]) lLstdcpsOrGpf.get(0);
				dcpsOrGpf = (Character) lArrObj[0];
				lLngOrgEmpId = (Long) lArrObj[1];
				lStrSevaarthId = (String) lArrObj[2];
				inputMap.put("dcpsOrGpf", dcpsOrGpf);
				
				List<MstLnaPayrollLoanTypeMpg> lLstPayrollLoanId = lObjBillDtlsDAO.getPayrollLoanId(lLngRequestType);
				
				if(!lLstPayrollLoanId.isEmpty()){
					lObjTypeMpg = lLstPayrollLoanId.get(0);
					lLngPayrollPrinId = lObjTypeMpg.getPayrollPriLoanId();
					lLngPayrollIntId = lObjTypeMpg.getPayrollIntLoanId();
				}
		//added by adi	
          if(lStrRequestType.equals(gObjRsrcBndle.getString("LNA.COMPUTERADVANCE")) && EmpPriCheck){
					
        	  		alertMessage="Already computer loan taken";
        	  		inputMap.put("alertMessage", alertMessage);
        	  		resObj.setResultValue(inputMap);
        	  		resObj.setViewName("LNAEmpSearch");
        	  		List lstLoanType = IFMSCommonServiceImpl.getLookupValues("Loan And Advance Request Type", SessionHelper.getLangId(inputMap), inputMap);
					inputMap.put("lstLoanType", lstLoanType);
					resObj.setViewName("LNAEmpSearch");
					return resObj;
					
				}else if(lStrRequestType.equals(gObjRsrcBndle.getString("LNA.HOUSEADVANCE")) && (EmpPriCheck || EmpIntCheck)){
					
					alertMessage="Already house loan taken";
        	  		inputMap.put("alertMessage", alertMessage);
        	  		resObj.setResultValue(inputMap);
        	  		resObj.setViewName("LNAEmpSearch");
        	  		List lstLoanType = IFMSCommonServiceImpl.getLookupValues("Loan And Advance Request Type", SessionHelper.getLangId(inputMap), inputMap);
					inputMap.put("lstLoanType", lstLoanType);
					resObj.setViewName("LNAEmpSearch");
					return resObj;
				}
				else if(lStrRequestType.equals(gObjRsrcBndle.getString("LNA.MOTORADVANCE")) && (EmpPriCheck || EmpIntCheck)){
					alertMessage="Already motor loan taken";
        	  		inputMap.put("alertMessage", alertMessage);
        	  		resObj.setResultValue(inputMap);
        	  		resObj.setViewName("LNAEmpSearch");
        	  		List lstLoanType = IFMSCommonServiceImpl.getLookupValues("Loan And Advance Request Type", SessionHelper.getLangId(inputMap), inputMap);
					inputMap.put("lstLoanType", lstLoanType);
					resObj.setViewName("LNAEmpSearch");
					return resObj;
				}
          //adi
				if(lStrRequestType.equals(gObjRsrcBndle.getString("LNA.COMPUTERADVANCE"))){
					lBIsPriLoanMapped = lObjLNARequestProcessDAO.isPriLoanMappedWithDDO(lStrSevaarthId, lLngRequestType);
						if(!lBIsPriLoanMapped){
							lStrPriLoanName = lObjLNARequestProcessDAO.getAdvNameFromId(lLngPayrollPrinId);							
						}
				}else if(lStrRequestType.equals(gObjRsrcBndle.getString("LNA.HOUSEADVANCE"))){
					lBIsPriLoanMapped = lObjLNARequestProcessDAO.isPriLoanMappedWithDDO(lStrSevaarthId, lLngRequestType);
					lBIsIntLoanMapped = lObjLNARequestProcessDAO.isIntLoanMappedWithDDO(lStrSevaarthId, lLngRequestType);
					if(!lBIsPriLoanMapped){
						lStrPriLoanName = lObjLNARequestProcessDAO.getAdvNameFromId(lLngPayrollPrinId);							
					}
					if(!lBIsIntLoanMapped){
						lStrIntLoanName = lObjLNARequestProcessDAO.getAdvNameFromId(lLngPayrollIntId);							
					}
					
				}else if(lStrRequestType.equals(gObjRsrcBndle.getString("LNA.MOTORADVANCE"))){
					lBIsPriLoanMapped = lObjLNARequestProcessDAO.isPriLoanMappedWithDDO(lStrSevaarthId, lLngRequestType);
					lBIsIntLoanMapped = lObjLNARequestProcessDAO.isIntLoanMappedWithDDO(lStrSevaarthId, lLngRequestType);
					if(!lBIsPriLoanMapped){
						lStrPriLoanName = lObjLNARequestProcessDAO.getAdvNameFromId(lLngPayrollPrinId);							
					}
					if(!lBIsIntLoanMapped){
						lStrIntLoanName = lObjLNARequestProcessDAO.getAdvNameFromId(lLngPayrollIntId);							
					}
				}
				if(lStrRequestType.equals(gObjRsrcBndle.getString("LNA.COMPUTERADVANCE")) && !lBIsPriLoanMapped){
					
					inputMap.put("lStrPriLoanName", lStrPriLoanName);
					alertMessage = "InvalidEmp";
					inputMap.put("alertMessage", alertMessage);
					resObj.setResultValue(inputMap);
					List lstLoanType = IFMSCommonServiceImpl.getLookupValues("Loan And Advance Request Type", SessionHelper.getLangId(inputMap), inputMap);
					inputMap.put("lstLoanType", lstLoanType);
					resObj.setViewName("LNAEmpSearch");
					
					return resObj;
				}else if(lStrRequestType.equals(gObjRsrcBndle.getString("LNA.HOUSEADVANCE")) && (!lBIsPriLoanMapped || !lBIsIntLoanMapped)){
					
						if(!lBIsIntLoanMapped)
							inputMap.put("lStrIntLoanName", lStrIntLoanName);
						if(!lBIsPriLoanMapped)						
						inputMap.put("lStrPriLoanName", lStrPriLoanName);
						
						alertMessage = "notmapped";
						inputMap.put("alertMessage", alertMessage);
						resObj.setResultValue(inputMap);
						List lstLoanType = IFMSCommonServiceImpl.getLookupValues("Loan And Advance Request Type", SessionHelper.getLangId(inputMap), inputMap);
						inputMap.put("lstLoanType", lstLoanType);
						resObj.setViewName("LNAEmpSearch");
						return resObj;
				}else if(lStrRequestType.equals(gObjRsrcBndle.getString("LNA.MOTORADVANCE")) && (!lBIsPriLoanMapped || !lBIsIntLoanMapped)){
					
					if(!lBIsIntLoanMapped)
						inputMap.put("lStrIntLoanName", lStrIntLoanName);
					if(!lBIsPriLoanMapped)						
						inputMap.put("lStrPriLoanName", lStrPriLoanName);
					
					alertMessage = "notmapped";
					inputMap.put("alertMessage", alertMessage);
					resObj.setResultValue(inputMap);
					List lstLoanType = IFMSCommonServiceImpl.getLookupValues("Loan And Advance Request Type", SessionHelper.getLangId(inputMap), inputMap);
					inputMap.put("lstLoanType", lstLoanType);
					resObj.setViewName("LNAEmpSearch");
					return resObj;
				}
				if (dcpsOrGpf.equals('Y')) {
					lnaEmpList = lObjLNARequestProcessDAO.getDCPSEmployeeDetail(lLngOrgEmpId);
				} else {
					lnaEmpList = lObjLNARequestProcessDAO.getGPFEmployeeDetail(lLngOrgEmpId);
				}

				List lLstEmpBankDtls = lObjLNARequestProcessDAO.getEmpBankDetails(lStrSevaarthId);

				
				if (!lLstEmpBankDtls.isEmpty()) {
					Object[] lObjEmpBankDtls = (Object[]) lLstEmpBankDtls.get(0);
					inputMap.put("EmpBankDtls", lObjEmpBankDtls);
				}

				if (!lnaEmpList.isEmpty()) {
					Object[] LNAEmpVO = (Object[]) lnaEmpList.get(0);
					String lStrEmpGroup = LNAEmpVO[5].toString();
					String lStrEmpPayComName = LNAEmpVO[1].toString();
					String lStrDesignation = LNAEmpVO[6].toString();
					Double lDblBasicPay = (Double) LNAEmpVO[3];
					inputMap.put("LNAEmpVO", LNAEmpVO);
					inputMap.put("EmpGroup", lStrEmpGroup);

					HrEisScaleMst lObjHrEisScaleMst = null;
					lObjHrEisScaleMst = lObjLNARequestProcessDAO.getPayScaleData(lLngOrgEmpId);

					if (lObjHrEisScaleMst != null) {

						StringBuffer scaleDisp = new StringBuffer("");
						if (lObjHrEisScaleMst.getHrPayCommissionMst().getId() == 2500341) {
							scaleDisp.append(lObjHrEisScaleMst.getScaleStartAmt());
							scaleDisp.append("-");
							scaleDisp.append(lObjHrEisScaleMst.getScaleEndAmt());
							scaleDisp.append(" (");
							scaleDisp.append(lObjHrEisScaleMst.getScaleGradePay());
							scaleDisp.append(")");

							Long lLngGradePay = lObjHrEisScaleMst.getScaleGradePay();
							inputMap.put("GradePay", lLngGradePay);
							lDblPayInPb = lDblBasicPay - lLngGradePay.doubleValue();
							inputMap.put("PayInPb", lDblPayInPb);
						} else {
							scaleDisp.append(lObjHrEisScaleMst.getScaleStartAmt());
							scaleDisp.append("-");
							scaleDisp.append(lObjHrEisScaleMst.getScaleIncrAmt());
							scaleDisp.append("-");
							scaleDisp.append(lObjHrEisScaleMst.getScaleEndAmt());
							if (lObjHrEisScaleMst.getScaleHigherIncrAmt() > 0 && lObjHrEisScaleMst.getScaleHigherEndAmt() > 0) {
								scaleDisp.append("-");
								scaleDisp.append(lObjHrEisScaleMst.getScaleHigherIncrAmt());
								scaleDisp.append("-");
								scaleDisp.append(lObjHrEisScaleMst.getScaleHigherEndAmt());
								if (lObjHrEisScaleMst.getScaleSecondHigherIncrAmt() > 0 && lObjHrEisScaleMst.getScaleSecondHigherEndAmt() > 0) {
									scaleDisp.append("-");
									scaleDisp.append(lObjHrEisScaleMst.getScaleSecondHigherIncrAmt());
									scaleDisp.append("-");
									scaleDisp.append(lObjHrEisScaleMst.getScaleSecondHigherEndAmt());

									if (lObjHrEisScaleMst.getScaleThirdHigherIncrAmt() > 0 && lObjHrEisScaleMst.getScaleThirdHigherEndAmt() > 0) {
										scaleDisp.append("-");
										scaleDisp.append(lObjHrEisScaleMst.getScaleThirdHigherIncrAmt());
										scaleDisp.append("-");
										scaleDisp.append(lObjHrEisScaleMst.getScaleThirdHigherEndAmt());
									}
								}
							}
						}
						inputMap.put("payScale", scaleDisp);
					}

					if (lStrRequestType.equals(gObjRsrcBndle.getString("LNA.COMPUTERADVANCE"))) {
						/*if (lStrEmpGroup.equals("D")) {
							alertMessage = "GradeDEmp";
							inputMap.put("alertMessage", alertMessage);
							lBlOneRequestExists = true;
						} else {*/
						lBlOneRequestExists = loadComputerAdvance(lStrSevaarthId, lStrUser, lLngRequestType, inputMap);
						//}
					} else if (lStrRequestType.equals(gObjRsrcBndle.getString("LNA.HOUSEADVANCE"))) {
						lBlOneRequestExists = loadHouseAdvance(lStrSevaarthId, lStrUser, lLngRequestType, lStrDisburse, inputMap);
					} else if (lStrRequestType.equals(gObjRsrcBndle.getString("LNA.MOTORADVANCE"))) {
						if (lStrDesignation.matches(".*driver.*") || lStrDesignation.matches(".*Driver.*")) {
							lBlOneRequestExists = loadMotorCarAdvance(lStrSevaarthId, lStrUser, lLngRequestType, inputMap);
						} else {
							if (lStrEmpPayComName.equals("5th Pay Commission")) {
								if (lStrEmpGroup.equals("D")) {
									if (lDblBasicPay > 5000) {
										alertMessage = "BasicPayGroupD";
										inputMap.put("alertMessage", alertMessage);
										lBlOneRequestExists = true;
									} else {
										lBlOneRequestExists = loadMotorCarAdvance(lStrSevaarthId, lStrUser, lLngRequestType, inputMap);
									}
								} else {
									if (lDblBasicPay < 4600) {
										alertMessage = "BasicPay";
										inputMap.put("alertMessage", alertMessage);
										lBlOneRequestExists = true;
									} else {
										lBlOneRequestExists = loadMotorCarAdvance(lStrSevaarthId, lStrUser, lLngRequestType, inputMap);
									}
								}
							} else {
								if (lStrEmpGroup.equals("A") || lStrEmpGroup.equals("B") || lStrEmpGroup.equals("BnGz")) {
									if (lDblPayInPb < 8650) {
										alertMessage = "PayInPBForGroupA";
										inputMap.put("alertMessage", alertMessage);
										lBlOneRequestExists = true;
									} else {
										lBlOneRequestExists = loadMotorCarAdvance(lStrSevaarthId, lStrUser, lLngRequestType, inputMap);
									}
								} else if (lStrEmpGroup.equals("C")) {
									lBlOneRequestExists = loadMotorCarAdvance(lStrSevaarthId, lStrUser, lLngRequestType, inputMap);
								} else if (lStrEmpGroup.equals("D")) {
									if (lDblPayInPb > 2800) {
										alertMessage = "PayInPBForGroupD";
										inputMap.put("alertMessage", alertMessage);
										lBlOneRequestExists = true;
									} else {
										lBlOneRequestExists = loadMotorCarAdvance(lStrSevaarthId, lStrUser, lLngRequestType, inputMap);
									}
								}

							}
						}
					}

					inputMap.put("lDtCurrDate", lObjDateFormat.format(gDtCurDate));

					List lLstPayCommissionGR = IFMSCommonServiceImpl.getLookupValues("Pay Commission LNA", SessionHelper.getLangId(inputMap), inputMap);
					inputMap.put("PayCommissionGR", lLstPayCommissionGR);
					List lstLoanType = IFMSCommonServiceImpl.getLookupValues("Loan And Advance Request Type", SessionHelper.getLangId(inputMap), inputMap);
					inputMap.put("lstLoanType", lstLoanType);

					String LNANextPostId = null;
					if (!lStrUser.equals("HOD")) {
						List UserList = getHierarchyUsers(inputMap,lStrRequestType);
						LNANextPostId = UserList.get(0).toString();
						inputMap.put("PostIdForDEO", LNANextPostId);
						inputMap.put("PostId", gStrPostId);
					}
					if (lBlOneRequestExists) {
						if (lStrCriteria.equals("1")) {
							inputMap.put("SevaarthId", lStrSevaarthId);
						} else if (lStrCriteria.equals("2")) {
							inputMap.put("EmpName", lStrName);
						} else {
							inputMap.put("SevaarthId", lStrSevaarthId);
							inputMap.put("EmpName", lStrName);
						}
						resObj.setResultValue(inputMap);
						resObj.setViewName("LNAEmpSearch");
					} else {
						if (lIntSubtypeSelection == 0) {
							inputMap.put("SevaarthId", lStrSevaarthId);
							List lstOddInstallNo = IFMSCommonServiceImpl.getLookupValues("Odd Installment No", SessionHelper.getLangId(inputMap), inputMap);
							inputMap.put("OddInstallNo", lstOddInstallNo);
							resObj.setResultValue(inputMap);
							resObj.setViewName("LNARequestForm");
						} else if (lIntSubtypeSelection == 1) {
							inputMap.put("SevaarthId", lStrSevaarthId);
							resObj.setResultValue(inputMap);
							resObj.setViewName("SubtypeSelection");
						} else if (lIntSubtypeSelection == 2) {
							resObj.setResultValue(inputMap);
							resObj.setViewName("LNARequestForm");
						} else if (lIntSubtypeSelection == 3) {
							inputMap.put("alertMessage", "Accept");
							resObj.setResultValue(inputMap);
							resObj.setViewName("LNAEmpSearch");
						}
					}
				} else {

					alertMessage = "InvalidEmp";
					inputMap.put("alertMessage", alertMessage);
					resObj.setResultValue(inputMap);
					List lstLoanType = IFMSCommonServiceImpl.getLookupValues("Loan And Advance Request Type", SessionHelper.getLangId(inputMap), inputMap);
					inputMap.put("lstLoanType", lstLoanType);
					resObj.setViewName("LNAEmpSearch");
				}
			} else {
				inputMap.put("SevaarthId", lStrSevaarthId);
				inputMap.put("EmpName", lStrName);
				alertMessage = "InvalidEmp";
				inputMap.put("alertMessage", alertMessage);
				resObj.setResultValue(inputMap);
				List lstLoanType = IFMSCommonServiceImpl.getLookupValues("Loan And Advance Request Type", SessionHelper.getLangId(inputMap), inputMap);
				inputMap.put("lstLoanType", lstLoanType);
				resObj.setViewName("LNAEmpSearch");
			}
		
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		return resObj;

	}

	private Boolean loadComputerAdvance(String lStrSevaarthId, String userType, Long lLngRequestType, Map inputMap) throws Exception {
		List computerAdvanceList = null;
		Long lLngComAdvnId = null;
		MstLnaCompAdvance lObjCompAdvance = null;
		String alertMessage = "";

		LNARequestProcessDAO lObjRequestProcessDAO = new LNARequestProcessDAOImpl(MstLnaCompAdvance.class, serv.getSessionFactory());
		LNAComputerAdvanceDAO lObjComputerAdvanceDAO = new LNAComputerAdvanceDAOImpl(MstLnaCompAdvance.class, serv.getSessionFactory());

		if (lObjComputerAdvanceDAO.requestDataAlreadyExists(lStrSevaarthId) && (userType.equals("DEO") || userType.equals("HODASST2"))) {
			alertMessage = "OneReqExists";
			inputMap.put("alertMessage", alertMessage);
			return true;
		}
		
		//added by shailesh
		//if (lObjComputerAdvanceDAO.requestPendingStatus(lStrSevaarthId) && (userType.equals("DEO") || userType.equals("HODASST2"))) {
		if (lObjComputerAdvanceDAO.requestPendingStatus(lStrSevaarthId) && userType.equals("DEO")) {
			alertMessage = "Pending";
			inputMap.put("alertMessage", alertMessage);
			return true;
		}
		if (lObjRequestProcessDAO.checkEligibilityForLNA(lStrSevaarthId)) {
			alertMessage = "Eligibility";
			inputMap.put("alertMessage", alertMessage);
			return true;
		}
		if (userType.equals("DEO") || userType.equals("HODASST2")) {
			computerAdvanceList = lObjComputerAdvanceDAO.getComputerAdvance(lStrSevaarthId, lLngRequestType);
		}
		if (userType.equals("DEOAPP") || userType.equals("HO") || userType.equals("HODASST") || userType.equals("HODASST2") || userType.equals("HOD")) {
			String lStrPkValue = StringUtility.getParameter("pkValue", request);
			if (!"".equals(lStrPkValue.trim())) {
				Long lLngComAdvanceId = Long.parseLong(lStrPkValue);
				computerAdvanceList = lObjComputerAdvanceDAO.getComAdvanceToDEOApprover(lLngComAdvanceId);
			}
		}

		if (!computerAdvanceList.isEmpty()) {
			lLngComAdvnId = (Long) computerAdvanceList.get(0);
			lObjCompAdvance = (MstLnaCompAdvance) lObjComputerAdvanceDAO.read(lLngComAdvnId);

			CmnAttachmentMstDAO lObjCmnAttachmentMstDAO = new CmnAttachmentMstDAOImpl(CmnAttachmentMst.class, serv.getSessionFactory());
			CmnAttachmentMst lObjCmnAttachmentMst = null;
			if (lObjCompAdvance.getAttachmentId() != null) {
				lObjCmnAttachmentMst = new CmnAttachmentMst();
				lObjCmnAttachmentMst = lObjCmnAttachmentMstDAO.findByAttachmentId(lObjCompAdvance.getAttachmentId());
				inputMap.put("ProofCA", lObjCmnAttachmentMst);
			}
			inputMap.put("savedCompAdvance", lObjCompAdvance);
			inputMap.put("frwrdToRHOPostId", lObjCompAdvance.getFrwrdToRHO());
			inputMap.put("fromPostId", lObjCompAdvance.getFromPostId());
			List documentList = lObjRequestProcessDAO.getCheckList(lStrSevaarthId, lLngRequestType, lObjCompAdvance.getAdvanceSubType());
			inputMap.put("Checklist", documentList);
		}
		List lstLoanSubType = IFMSCommonServiceImpl.getLookupValues("Computer Sub Type", SessionHelper.getLangId(inputMap), inputMap);
		inputMap.put("lstComputerSubType", lstLoanSubType);
		return false;
	}

	private Boolean loadHouseAdvance(String lStrSevaarthId, String userType, Long lLngRequestType, String lStrDisburse, Map inputMap) throws Exception {
		List houseAdvanceList = null;
		Long lLngHouseAdvnId = null;
		MstLnaHouseAdvance lObjHouseAdvance = null;
		String alertMessage = "";
		LNARequestProcessDAO lObjRequestProcessDAO = new LNARequestProcessDAOImpl(MstLnaHouseAdvance.class, serv.getSessionFactory());
		LNAHouseAdvanceDAO lObjHouseAdvanceDAO = new LNAHouseAdvanceDAOImpl(MstLnaHouseAdvance.class, serv.getSessionFactory());

		Date lDtReleaseDateOne = null;
		Date lDtReleaseDateTwo = null;
		Date lDtReleaseDateThree = null;
		Date lDtReleaseDateFour = null;
		List lListSubtypeDtls = null;
		Integer lIntReqCount = lObjHouseAdvanceDAO.requestDataAlreadyExists(lStrSevaarthId, lLngRequestType);
		if (lIntReqCount > 0 && (userType.equals("DEO") || userType.equals("HODASST2"))) {
			alertMessage = "OneReqExists";
			inputMap.put("alertMessage", alertMessage);
			lListSubtypeDtls = lObjHouseAdvanceDAO.getSubtypeDtlsforDisbursement(lStrSevaarthId);
			if (lListSubtypeDtls != null && !lListSubtypeDtls.isEmpty()) {
				Long lLngPkvalue = (Long) lListSubtypeDtls.get(0);
				lObjHouseAdvance = (MstLnaHouseAdvance) lObjHouseAdvanceDAO.read(lLngPkvalue);
				if ("".equals(lStrDisburse)) {
					lIntSubtypeSelection = 1;
					if (lObjHouseAdvance.getAdvanceSubType() == 800038) {
						lDtReleaseDateOne = lObjHouseAdvance.getReleaseDateOne();
						lDtReleaseDateTwo = lObjHouseAdvance.getReleaseDateTwo();
						lDtReleaseDateThree = lObjHouseAdvance.getReleaseDateThree();

						if (lDtReleaseDateThree == null) {
							if (lDtReleaseDateTwo != null) {
								inputMap.put("twoPloatPurchase", "twoPloatPurchase");
							} else if (lDtReleaseDateOne != null) {
								inputMap.put("onePloatPurchase", "onePloatPurchase");
							}
						} else {
							lIntSubtypeSelection = 2;
						}

					} else if (lObjHouseAdvance.getAdvanceSubType() == 800058) {
						lDtReleaseDateOne = lObjHouseAdvance.getReleaseDateOne();
						lDtReleaseDateTwo = lObjHouseAdvance.getReleaseDateTwo();
						lDtReleaseDateThree = lObjHouseAdvance.getReleaseDateThree();
						lDtReleaseDateFour = lObjHouseAdvance.getReleaseDateFour();
						if (lDtReleaseDateFour == null) {
							if (lDtReleaseDateThree != null) {
								inputMap.put("threeLaterCons", "threeLaterCons");
							} else if (lDtReleaseDateTwo != null) {
								inputMap.put("twoLaterCons", "twoLaterCons");
							} else if (lDtReleaseDateOne != null) {
								inputMap.put("oneLaterCons", "oneLaterCons");
							}
						} else {
							lIntSubtypeSelection = 2;
						}

					}
				} else if (lStrDisburse.equals("Stop2ndCF") || lStrDisburse.equals("Stop3rdCF") || lStrDisburse.equals("Stop2ndLC") || lStrDisburse.equals("Stop3rdLC")
						|| lStrDisburse.equals("Stop4thLC")) {
					lIntSubtypeSelection = 3;
					lObjHouseAdvance.setStatusFlag("A");
					lObjHouseAdvanceDAO.update(lObjHouseAdvance);
				} else if (lStrDisburse.equals("800059")) {

				} else if (lStrDisburse.equals("Club2nd3rdCF") || lStrDisburse.equals("Club3rd4thLC")) {
					CmnAttachmentMstDAO lObjCmnAttachmentMstDAO = new CmnAttachmentMstDAOImpl(CmnAttachmentMst.class, serv.getSessionFactory());
					CmnAttachmentMst lObjCmnAttachmentMst = null;
					if (lObjHouseAdvance.getAttachmentId() != null) {
						lObjCmnAttachmentMst = new CmnAttachmentMst();
						lObjCmnAttachmentMst = lObjCmnAttachmentMstDAO.findByAttachmentId(lObjHouseAdvance.getAttachmentId());
						inputMap.put("ProofHBA", lObjCmnAttachmentMst);
					}
					if (lObjHouseAdvance.getPayCommissionGR() != null) {
						Map voToServiceMap = new HashMap();
						String commissionId = null;
						if (lObjHouseAdvance.getPayCommissionGR().toString().equals(gObjRsrcBndle.getString("LNA.5THPCGR"))) {
							commissionId = gObjRsrcBndle.getString("LNA.5THPC");
						} else {
							commissionId = gObjRsrcBndle.getString("LNA.6THPC");
						}
						voToServiceMap.put("commissionId", commissionId);

						inputMap.put("voToServiceMap", voToServiceMap);

						serv.executeService("GetScalefromDesg", inputMap);

						List PayScaleList = (List) inputMap.get("PayScaleList");
						inputMap.put("PayScaleList", PayScaleList);
					}
					inputMap.put("HouseAdvance", lObjHouseAdvance);
					inputMap.put("frwrdToRHOPostId", lObjHouseAdvance.getFrwrdToRHO());
					inputMap.put("fromPostId", lObjHouseAdvance.getFromPostId());
				} else {
					CmnAttachmentMstDAO lObjCmnAttachmentMstDAO = new CmnAttachmentMstDAOImpl(CmnAttachmentMst.class, serv.getSessionFactory());
					CmnAttachmentMst lObjCmnAttachmentMst = null;
					if (lObjHouseAdvance.getAttachmentId() != null) {
						lObjCmnAttachmentMst = new CmnAttachmentMst();
						lObjCmnAttachmentMst = lObjCmnAttachmentMstDAO.findByAttachmentId(lObjHouseAdvance.getAttachmentId());
						inputMap.put("ProofHBA", lObjCmnAttachmentMst);
					}
					if (lObjHouseAdvance.getPayCommissionGR() != null) {
						Map voToServiceMap = new HashMap();
						String commissionId = null;
						if (lObjHouseAdvance.getPayCommissionGR().toString().equals(gObjRsrcBndle.getString("LNA.5THPCGR"))) {
							commissionId = gObjRsrcBndle.getString("LNA.5THPC");
						} else {
							commissionId = gObjRsrcBndle.getString("LNA.6THPC");
						}
						voToServiceMap.put("commissionId", commissionId);

						inputMap.put("voToServiceMap", voToServiceMap);

						serv.executeService("GetScalefromDesg", inputMap);

						List PayScaleList = (List) inputMap.get("PayScaleList");
						inputMap.put("PayScaleList", PayScaleList);
					}
					inputMap.put("HouseAdvance", lObjHouseAdvance);
					inputMap.put("frwrdToRHOPostId", lObjHouseAdvance.getFrwrdToRHO());
					inputMap.put("fromPostId", lObjHouseAdvance.getFromPostId());
				}

			}
			inputMap.put("Disburse", lStrDisburse);
		}
		//added by shailesh 
		//if (lObjHouseAdvanceDAO.requestPendingStatus(lStrSevaarthId) && (userType.equals("DEO") || userType.equals("HODASST2"))) {
		if (lObjHouseAdvanceDAO.requestPendingStatus(lStrSevaarthId) && userType.equals("DEO")) {
			alertMessage = "Pending";
			inputMap.put("alertMessage", alertMessage);
			return true;
		}
		if (lObjRequestProcessDAO.checkEligibilityForLNA(lStrSevaarthId)) {
			alertMessage = "Eligibility";
			inputMap.put("alertMessage", alertMessage);
			return true;
		}

		if ((userType.equals("DEO") || userType.equals("HODASST2"))) {
			houseAdvanceList = lObjHouseAdvanceDAO.getHouseAdvance(lStrSevaarthId, lLngRequestType);
		}

		if (userType.equals("DEOAPP") || userType.equals("HO") || userType.equals("HODASST") || userType.equals("HODASST2") || userType.equals("HOD")) {
			String lStrPkValue = StringUtility.getParameter("pkValue", request);
			if (!"".equals(lStrPkValue.trim())) {
				Long lLngHouseAdvanceId = Long.parseLong(lStrPkValue);
				houseAdvanceList = lObjHouseAdvanceDAO.getHouseAdvanceToDEOApprover(lLngHouseAdvanceId);
			}
		}

		if (houseAdvanceList != null && !houseAdvanceList.isEmpty()) {
			lLngHouseAdvnId = (Long) houseAdvanceList.get(0);
			lObjHouseAdvance = (MstLnaHouseAdvance) lObjHouseAdvanceDAO.read(lLngHouseAdvnId);

			CmnAttachmentMstDAO lObjCmnAttachmentMstDAO = new CmnAttachmentMstDAOImpl(CmnAttachmentMst.class, serv.getSessionFactory());
			CmnAttachmentMst lObjCmnAttachmentMst = null;
			if (lObjHouseAdvance.getAttachmentId() != null) {
				lObjCmnAttachmentMst = new CmnAttachmentMst();
				lObjCmnAttachmentMst = lObjCmnAttachmentMstDAO.findByAttachmentId(lObjHouseAdvance.getAttachmentId());
				inputMap.put("ProofHBA", lObjCmnAttachmentMst);
			}
			if (lObjHouseAdvance.getPayCommissionGR() != null) {
				Map voToServiceMap = new HashMap();
				String commissionId = null;
				if (lObjHouseAdvance.getPayCommissionGR().toString().equals(gObjRsrcBndle.getString("LNA.5THPCGR"))) {
					commissionId = gObjRsrcBndle.getString("LNA.5THPC");
				} else {
					commissionId = gObjRsrcBndle.getString("LNA.6THPC");
				}
				voToServiceMap.put("commissionId", commissionId);

				inputMap.put("voToServiceMap", voToServiceMap);

				serv.executeService("GetScalefromDesg", inputMap);

				List PayScaleList = (List) inputMap.get("PayScaleList");
				inputMap.put("PayScaleList", PayScaleList);

			}
			inputMap.put("HouseAdvance", lObjHouseAdvance);
			inputMap.put("frwrdToRHOPostId", lObjHouseAdvance.getFrwrdToRHO());
			inputMap.put("fromPostId", lObjHouseAdvance.getFromPostId());
			
			List lLstGuarantorDtls = null;
			String lStrFirstGuarantor = lObjHouseAdvance.getFirstGuarantor();
			String lStrSecondGuarantor = lObjHouseAdvance.getSecondGuarantor();
			String lStrEmpName = null;
			String lStrDdocode = null;
			String lStrDdoName = null;
			String lStrBasicPay = null;

			if (lStrFirstGuarantor != null) {
				lLstGuarantorDtls = lObjHouseAdvanceDAO.getGuarantorDtls(lStrFirstGuarantor);
				if (!lLstGuarantorDtls.isEmpty()) {
					Object[] lObjGuarantorDtls = (Object[]) lLstGuarantorDtls.get(0);
					lStrEmpName = lObjGuarantorDtls[0].toString();
					lStrDdocode = lObjGuarantorDtls[1].toString();
					lStrDdoName = lObjGuarantorDtls[2].toString();
					lStrBasicPay = lObjGuarantorDtls[3].toString();
				}
				inputMap.put("EmpName1", lStrEmpName);
				inputMap.put("Ddocode1", lStrDdocode);
				inputMap.put("DdoName1", lStrDdoName);
				inputMap.put("BasicPay1", lStrBasicPay);
			}
			if (lStrSecondGuarantor != null) {
				lLstGuarantorDtls = lObjHouseAdvanceDAO.getGuarantorDtls(lStrSecondGuarantor);
				if (!lLstGuarantorDtls.isEmpty()) {
					Object[] lObjGuarantorDtls = (Object[]) lLstGuarantorDtls.get(0);
					lStrEmpName = lObjGuarantorDtls[0].toString();
					lStrDdocode = lObjGuarantorDtls[1].toString();
					lStrDdoName = lObjGuarantorDtls[2].toString();
					lStrBasicPay = lObjGuarantorDtls[3].toString();
				}
				inputMap.put("EmpName2", lStrEmpName);
				inputMap.put("Ddocode2", lStrDdocode);
				inputMap.put("DdoName2", lStrDdoName);
				inputMap.put("BasicPay2", lStrBasicPay);
			}			
		}
		if(lObjHouseAdvance != null){
			List documentList = lObjRequestProcessDAO.getCheckList(lStrSevaarthId, lLngRequestType, lObjHouseAdvance.getAdvanceSubType());
			inputMap.put("Checklist", documentList);
		}
		
		List lstLoanSubType = IFMSCommonServiceImpl.getLookupValues("House Sub Type", SessionHelper.getLangId(inputMap), inputMap);
		inputMap.put("lstHouseSubType", lstLoanSubType);
		return false;
	}

	private Boolean loadMotorCarAdvance(String lStrSevaarthId, String userType, Long lLngRequestType, Map inputMap) throws Exception {
		List motorcarAdvanceList = null;
		Long lLngMotorcarAdvnId = null;
		MstLnaMotorAdvance lObjMotorAdvance = null;
		String alertMessage = "";
		LNARequestProcessDAO lObjRequestProcessDAO = new LNARequestProcessDAOImpl(MstLnaMotorAdvance.class, serv.getSessionFactory());
		LNAMotorAdvanceDAO lObjMotorAdvanceDAO = new LNAMotorAdvanceDAOImpl(MstLnaMotorAdvance.class, serv.getSessionFactory());
// added by adi
		Double subtype=lObjMotorAdvanceDAO.getMotorSubtype(lStrSevaarthId);
		if(subtype==800035|| subtype==800051)
			{
			if (lObjMotorAdvanceDAO.requestDataAlreadyExists(lStrSevaarthId, lLngRequestType) && (userType.equals("DEO") || userType.equals("HODASST2"))) {
			
			alertMessage = "OneReqExists";
			inputMap.put("alertMessage", alertMessage);
			return true;
		}
			}
		
	//added by adi
		if (lObjMotorAdvanceDAO.requestCheck(lStrSevaarthId, lLngRequestType)) 
				{
			if (lObjMotorAdvanceDAO.requestDataAlreadyExists(lStrSevaarthId, lLngRequestType) && (userType.equals("DEO") || userType.equals("HODASST2"))) {
				alertMessage = "OneReqExists";
				inputMap.put("alertMessage", alertMessage);
				return true;
				
		}
			}
		/*if (lObjMotorAdvanceDAO.requestDataAlreadyExists(lStrSevaarthId, lLngRequestType) && (userType.equals("DEO") || userType.equals("HODASST2"))) {
			alertMessage = "OneReqExists";
			inputMap.put("alertMessage", alertMessage);
			return true;
		}*/
		
		//added by shailesh
		//if (lObjMotorAdvanceDAO.requestPendingStatus(lStrSevaarthId) && (userType.equals("DEO") || userType.equals("HODASST2"))) {
		if (lObjMotorAdvanceDAO.requestPendingStatus(lStrSevaarthId) && userType.equals("DEO")) {
			alertMessage = "Pending";
			inputMap.put("alertMessage", alertMessage);
			return true;
		}
		if (lObjRequestProcessDAO.checkEligibilityForLNA(lStrSevaarthId)) {
			alertMessage = "Eligibility";
			inputMap.put("alertMessage", alertMessage);
			return true;
		}
		if (userType.equals("DEO") || userType.equals("HODASST2")) {
			motorcarAdvanceList = lObjMotorAdvanceDAO.getMotorAdvance(lStrSevaarthId, lLngRequestType);
		}
		if (userType.equals("DEOAPP") || userType.equals("HO") || userType.equals("HODASST") || userType.equals("HODASST2") || userType.equals("HOD")) {
			String lStrPkValue = StringUtility.getParameter("pkValue", request);
			if (!"".equals(lStrPkValue.trim())) {
				Long lLngComAdvanceId = Long.parseLong(lStrPkValue);
				motorcarAdvanceList = lObjMotorAdvanceDAO.getMotorAdvanceToDEOApprover(lLngComAdvanceId);
			}
		}

		if (motorcarAdvanceList != null && !motorcarAdvanceList.isEmpty()) {
			lLngMotorcarAdvnId = (Long) motorcarAdvanceList.get(0);
			lObjMotorAdvance = (MstLnaMotorAdvance) lObjMotorAdvanceDAO.read(lLngMotorcarAdvnId);

			CmnAttachmentMstDAO lObjCmnAttachmentMstDAO = new CmnAttachmentMstDAOImpl(CmnAttachmentMst.class, serv.getSessionFactory());
			CmnAttachmentMst lObjCmnAttachmentMst = null;
			if (lObjMotorAdvance.getAttachmentId() != null) {
				lObjCmnAttachmentMst = new CmnAttachmentMst();
				lObjCmnAttachmentMst = lObjCmnAttachmentMstDAO.findByAttachmentId(lObjMotorAdvance.getAttachmentId());
				inputMap.put("ProofMCA", lObjCmnAttachmentMst);
			}
			
			inputMap.put("MotorAdvance", lObjMotorAdvance);
			inputMap.put("frwrdToRHOPostId", lObjMotorAdvance.getFrwrdToRHO());
			inputMap.put("fromPostId", lObjMotorAdvance.getFromPostId());
			
			List documentList = lObjRequestProcessDAO.getCheckList(lStrSevaarthId, lLngRequestType, lObjMotorAdvance.getAdvanceSubType());
			inputMap.put("Checklist", documentList);
		}
		List lstLoanSubType = IFMSCommonServiceImpl.getLookupValues("Vehicle Sub Type", SessionHelper.getLangId(inputMap), inputMap);
		inputMap.put("lstVehicleSubType", lstLoanSubType);
		inputMap.put("SubType", subtype);
		return false;
	}

	public ResultObject loadLNARequestList(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try {
			setSessionInfo(inputMap);
			SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyy");			
			List<ComboValuesVO> lLstDepartment = null;
			List empListForDeoAppover = null;
			Long lLngOfficeId = null;
			Date lDtFromDate = null;
			Date lDtToDate = null;
			
			String lStrUser = StringUtility.getParameter("userType", request);
			
			String lStrSearchBy = StringUtility.getParameter("cmbSearchBy", request);
			String lStrOfficeId = StringUtility.getParameter("cmbFieldDept", request);
			String lStrFromDate = StringUtility.getParameter("txtFromDate", request);
			String lStrToDate = StringUtility.getParameter("txtToDate", request);
			
						
			inputMap.put("UserType", lStrUser);
			LNARequestProcessDAO lObjRequestProcessDAO = new LNARequestProcessDAOImpl(MstLnaCompAdvance.class, serv.getSessionFactory());
			
			if(!"-1".equals(lStrSearchBy)){
				if("-1".equals(lStrOfficeId) || "".equals(lStrOfficeId)){
					if(!"".equals(lStrFromDate) && !"".equals(lStrToDate)){					
						if(!"".equals(lStrFromDate))					
							lDtFromDate = lObjDateFormat.parse(lStrFromDate);
						if(!"".equals(lStrToDate)){
							lDtToDate = lObjDateFormat.parse(lStrToDate);
							lDtToDate.setDate(lDtToDate.getDate()+1);
						}
						empListForDeoAppover = lObjRequestProcessDAO.getEmployeeDetailForApprover(lLngOfficeId, lDtFromDate, lDtToDate, gStrPostId);
					}else{
						empListForDeoAppover = lObjRequestProcessDAO.getEmployeeDetailForApprover(gStrPostId);	
					}
				}else{
					lLngOfficeId = Long.parseLong(lStrOfficeId);				
					empListForDeoAppover = lObjRequestProcessDAO.getEmployeeDetailForApprover(lLngOfficeId, lDtFromDate, lDtToDate, gStrPostId);
				}
			}else{
				empListForDeoAppover = lObjRequestProcessDAO.getEmployeeDetailForApprover(gStrPostId);
			}
			lLstDepartment = lObjRequestProcessDAO.getAllDepartment(gLngLocationCode,gLngLangId);
			
			inputMap.put("CaseList", empListForDeoAppover);
			inputMap.put("lLstDepartment", lLstDepartment);
			inputMap.put("lStrSearchBy", lStrSearchBy);
			inputMap.put("lOfficeId", lStrOfficeId);
			inputMap.put("FromDate", lStrFromDate);
			inputMap.put("ToDate", lStrToDate);			
			
			resObj.setResultValue(inputMap);
			resObj.setViewName("LNAWorklist");

		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}

		return resObj;
	}

	 List getHierarchyUsers(Map inputMap,String lLngRequestType) {

		List UserList = null;

		try {

			setSessionInfo(inputMap);
			Integer llFromLevelId = 0;
			UserList = new ArrayList<String>();
			String subjectName = null; 
			// Get the Subject Name
			if(lLngRequestType.equals(gObjRsrcBndle.getString("LNA.COMPUTERADVANCE")))
				subjectName = gObjRsrcBndle.getString("LNA.CompAdvanceOffline");
			else if(lLngRequestType.equals(gObjRsrcBndle.getString("LNA.MOTORADVANCE")))
				subjectName = gObjRsrcBndle.getString("LNA.MotorAdvanceOffline");
			else if (lLngRequestType.equals(gObjRsrcBndle.getString("LNA.HOUSEADVANCE")))
				subjectName = gObjRsrcBndle.getString("LNA.HouseAdvanceOffline");

			// Get the Hierarchy Id
			//Long lLngHierRefId = WorkFlowHelper.getHierarchyByPostIDAndDescription(gStrPostId, subjectName, inputMap);
			String frmPostId = null;
			if(inputMap.get("fromPostId") != null)
				frmPostId =  (String)inputMap.get("fromPostId");
			else frmPostId = gStrPostId;
			
			
			LNAWorkFlowConfigDAO lObjLNAWorkFlowConfig = new LNAWorkFlowConfigDAOImpl(RltLevelRole.class, serv.getSessionFactory());
			
			String locID = lObjLNAWorkFlowConfig.getPostIdForLocId(frmPostId);
			//ArrayList<Long> refId = new ArrayList<Long>();
			Long lLngHierRefId  = Long.parseLong(lObjLNAWorkFlowConfig.getRefIdByDescriptionAndLocId(locID,subjectName));

			// Get the From level Id
			llFromLevelId = WorkFlowHelper.getLevelFromPostMpg(gStrPostId, lLngHierRefId, inputMap);

			// Get the List of Post ID of the users at the next Level
			List rsltList = WorkFlowHelper.getUpperPost(gStrPostId, lLngHierRefId, llFromLevelId, inputMap);

			Object[] lObjNextPost = null;

			for (Integer lInt = 0; lInt < rsltList.size(); lInt++) {

				lObjNextPost = (Object[]) rsltList.get(lInt);
				UserList.add(lObjNextPost[0].toString());
			}

		} catch (Exception e) {
			gLogger.error(" Error is " + e, e);
		}
		return UserList;
	}

	public ResultObject getEmpNameForAutoCompleteLNA(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		List finalList = null;
		String lStrEmpName = null;
		try {
			setSessionInfo(inputMap);

			lStrEmpName = StringUtility.getParameter("searchKey", request).trim();
			String lStrUser = StringUtility.getParameter("userType", request);
			LNARequestProcessDAO lObjRequestProcessDAO = new LNARequestProcessDAOImpl(MstLnaCompAdvance.class, serv.getSessionFactory());
			DcpsCommonDAO lObjCommonDAO = new DcpsCommonDAOImpl(MstEmp.class, serv.getSessionFactory());
			String lStrDdoCode = null;
			if (lStrUser.equals("DEO")) {
				lStrDdoCode = lObjCommonDAO.getDdoCode(gLngPostId);
				finalList = lObjRequestProcessDAO.getEmpNameForAutoComplete(lStrEmpName.toUpperCase(), lStrDdoCode, gStrLocationCode,lStrUser);
			} else {
				lStrDdoCode = lObjRequestProcessDAO.getDdoCodeFromLocCode(gStrLocationCode.toString());
				finalList = lObjRequestProcessDAO.getEmpNameForAutoComplete(lStrEmpName.toUpperCase(), lStrDdoCode, gStrLocationCode,lStrUser);
			}

			String lStrTempResult = null;
			if (!finalList.isEmpty()) {
				lStrTempResult = new AjaxXmlBuilder().addItems(finalList, "desc", "id", true).toString();
			}
			inputMap.put("ajaxKey", lStrTempResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");

		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}

		return resObj;

	}

	public ResultObject loadDraftRequestList(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			setSessionInfo(inputMap);
			LNARequestProcessDAO lObjLNAReqProcess = new LNARequestProcessDAOImpl(MstLnaCompAdvance.class, serv.getSessionFactory());

			String lStrCriteria = StringUtility.getParameter("criteria", request);
			String lStrName = StringUtility.getParameter("name", request);
			String lStrDate = StringUtility.getParameter("date", request);
			String userType = StringUtility.getParameter("userType", request);
			
			Date lDtSaveDate = null;
			if (!lStrDate.equals("")) {
				lDtSaveDate = sdf.parse(lStrDate);
			}

			List lLstDraftReq = lObjLNAReqProcess.getDraftRequestList(lStrCriteria, lStrName, lDtSaveDate, gStrLocationCode, gStrPostId);
			inputMap.put("userType", userType);
			inputMap.put("lLstDraftReq", lLstDraftReq);
			resObj.setResultValue(inputMap);
			resObj.setViewName("LNADraftRequests");
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}

		return resObj;
	}

	public ResultObject loadEmpLoanStatus(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		try {
			setSessionInfo(inputMap);
			LNARequestProcessDAO lObjLNAReqProcess = new LNARequestProcessDAOImpl(MstLnaCompAdvance.class, serv.getSessionFactory());
			List lLstEmpLoanStatus = lObjLNAReqProcess.getEmpLoanDetails(gLngUserId);
			inputMap.put("EmpLoanStatus", lLstEmpLoanStatus);
			resObj.setResultValue(inputMap);
			resObj.setViewName("EmpLoanStatus");
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}

		return resObj;
	}

	
	public ResultObject popupEmpNameFromSevaarthId(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		
		String lStrSevaarthId = null;
		String lStrEmpName = "";
		Boolean lBlFlag = false;
		try {
			setSessionInfo(inputMap);

			lStrSevaarthId = StringUtility.getParameter("sevaarthId", request).trim();		
			LNARequestProcessDAO lObjRequestProcessDAO = new LNARequestProcessDAOImpl(MstLnaCompAdvance.class, serv.getSessionFactory());
			
			if(!"".equals(lStrSevaarthId))
				lStrEmpName = lObjRequestProcessDAO.getEmpNameFromSevaarthId(lStrSevaarthId.toUpperCase());
			
			if(!"".equals(lStrEmpName))
				lBlFlag = true;
			
			String lSBStatus = getResponseXMLDocForEmpName(lBlFlag, lStrEmpName).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");

		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}

		return resObj;

	}

	public ResultObject getHRODtls(Map inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List lLstHroDtls = null;
		
		try {
			setSessionInfo(inputMap);;
			
			LNARequestProcessDAO lObjRequestProcessDAO = new LNARequestProcessDAOImpl(MstLnaCompAdvance.class, serv.getSessionFactory());
			
			if(gLngLocationCode != null){
				lLstHroDtls = lObjRequestProcessDAO.getHROList(gLngLocationCode);
			}
			String lSBStatus = "";
			if(lLstHroDtls != null && lLstHroDtls.size() > 0)
			lSBStatus = getResponseXMLDocForHRO(lLstHroDtls).toString();
			else lSBStatus = "<XMLDOC><FLAG>false</FLAG></XMLDOC>";
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			String userType = request.getParameter("userType");
			String pkValue = request.getParameter("pkValue");
			String requestType = request.getParameter("requestType");
			
			inputMap.put("userType", userType);
			inputMap.put("pkValue", pkValue);
			inputMap.put("requestType", requestType);			
			inputMap.put("ajaxKey", lStrResult);
			inputMap.put("lLstHroDtls", lLstHroDtls);
			resObj.setResultValue(inputMap);
			//resObj.setViewName("ajaxData");
			resObj.setViewName("HRODetails");

		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}

		return resObj;

	}
	
	public void createLNAHierarchy(Map inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		
		Long ddoPostId = null;
		Long hodPostId = null;
		
		try {
			setSessionInfo(inputMap);
			LNARequestProcessDAO lObjRequestProcessDAO = new LNARequestProcessDAOImpl(MstLnaCompAdvance.class, serv.getSessionFactory());
			LNAWorkFlowConfigDAO lObjLNAWorkFlowConfig = new LNAWorkFlowConfigDAOImpl(RltLevelRole.class, serv.getSessionFactory());
			
			DcpsCommonDAO lObjCommonDAO = new DcpsCommonDAOImpl(MstEmp.class, serv.getSessionFactory());
			if(gLngLocationCode != null){
				lObjLNAWorkFlowConfig.generateRefId(gLngLocationCode.toString(),inputMap,gLngLangId,gLngUserId);
				/*get  ref_id for insertion in WF_HIERACHY_POST_MPG*/
				ArrayList<Long> refId = new ArrayList<Long>();
				refId = lObjLNAWorkFlowConfig.getRefId(gLngLocationCode.toString());
				
				ArrayList<Long> asstLvl = new ArrayList<Long>();
				asstLvl.add(10l);
				ArrayList<Long> ddoLvl = new ArrayList<Long>();
				ddoLvl.add(15l);
				ArrayList<Long> hodLvl = new ArrayList<Long>();
				hodLvl.add(25l);
				
				
				ddoPostId = lObjCommonDAO.getDDOPostIdForDDOAsst(gLngPostId);
				hodPostId = lObjRequestProcessDAO.getHODPostId(gLngLocationCode);				
				
				///to add ddo assistant in hierarchy 
				lObjLNAWorkFlowConfig.addPostInHierarchy(gLngLocationCode.toString(), gLngPostId, gLngUserId, inputMap,
						asstLvl, refId);
				////to add ddo in hierarchy 
				lObjLNAWorkFlowConfig.addPostInHierarchy(gLngLocationCode.toString(), ddoPostId, gLngUserId, inputMap,
						ddoLvl, refId);
				////to add HOD in hierarchy 
				lObjLNAWorkFlowConfig.addPostInHierarchy(gLngLocationCode.toString(), hodPostId, gLngUserId, inputMap,
						hodLvl, refId);
				
				IFMSCommonServiceImpl lObjIFMSCommonServiceImpl = new IFMSCommonServiceImpl();
				lObjIFMSCommonServiceImpl.clearCacheForMapDDOAsst(serv.getSessionFactory());
				lObjIFMSCommonServiceImpl.clearCacheForMapDDOAsst(serv.getSessionFactorySlave());
				
			}
			
		}
		catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		
	}
	private StringBuilder getResponseXMLDocForEmpName(Boolean flag, String lStrEmpName) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</FLAG>");
		lStrBldXML.append("<lEmpName>");
		lStrBldXML.append(lStrEmpName);
		lStrBldXML.append("</lEmpName>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	private StringBuilder getResponseXMLDocForHRO(List hroList) {
		StringBuilder lStrBldXML = new StringBuilder();
		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append("true");
		lStrBldXML.append("</FLAG>");
		
		if(hroList != null){
			for(int i =0; i < hroList.size(); i++){
		Object obj[] =(Object[]) hroList.get(i);		
		
		lStrBldXML.append("<lEmpName>");
		lStrBldXML.append(obj[0].toString());
		lStrBldXML.append("</lEmpName>");

		lStrBldXML.append("<locId>");
		lStrBldXML.append(obj[1].toString());
		lStrBldXML.append("</locId>");
		
		lStrBldXML.append("<locName>");
		lStrBldXML.append(obj[2].toString());
		lStrBldXML.append("</locName>");
		
		lStrBldXML.append("<postId>");
		lStrBldXML.append(obj[3].toString());
		lStrBldXML.append("</postId>");
		}
		}
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}
}
