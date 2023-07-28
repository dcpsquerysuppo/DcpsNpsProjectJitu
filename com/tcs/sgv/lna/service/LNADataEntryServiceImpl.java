package com.tcs.sgv.lna.service;

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
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;
import com.tcs.sgv.lna.dao.LNAComputerAdvanceDAO;
import com.tcs.sgv.lna.dao.LNAComputerAdvanceDAOImpl;
import com.tcs.sgv.lna.dao.LNADataEntryFormDAO;
import com.tcs.sgv.lna.dao.LNADataEntryFormDAOImpl;
import com.tcs.sgv.lna.dao.LNAHouseAdvanceDAO;
import com.tcs.sgv.lna.dao.LNAHouseAdvanceDAOImpl;
import com.tcs.sgv.lna.dao.LNAMotorAdvanceDAO;
import com.tcs.sgv.lna.dao.LNAMotorAdvanceDAOImpl;
import com.tcs.sgv.lna.dao.LNARequestProcessDAO;
import com.tcs.sgv.lna.dao.LNARequestProcessDAOImpl;
import com.tcs.sgv.lna.valueobject.MstLnaChallanDtls;
import com.tcs.sgv.lna.valueobject.MstLnaCompAdvance;
import com.tcs.sgv.lna.valueobject.MstLnaHouseAdvance;
import com.tcs.sgv.lna.valueobject.MstLnaMonthly;
import com.tcs.sgv.lna.valueobject.MstLnaMotorAdvance;
import com.tcs.sgv.lna.valueobject.MstLnaRequest;
import com.tcs.sgv.lna.valueobject.TrnEmpLoanDtls;

public class LNADataEntryServiceImpl extends ServiceImpl implements LNADataEntryService {
	Log gLogger = LogFactory.getLog(getClass());

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for Current Date */
	Date gDtCurDate = null;

	String gStrLocationCode = null;
	
	Long gLngLangId = null;

	private void setSessionInfo(Map inputMap) {

		request = (HttpServletRequest) inputMap.get("requestObj");
		serv = (ServiceLocator) inputMap.get("serviceLocator");
		gLngPostId = SessionHelper.getPostId(inputMap);
		gLngUserId = SessionHelper.getUserId(inputMap);
		gLngUserId.toString();
		gDtCurDate = SessionHelper.getCurDate();
		gStrLocationCode = SessionHelper.getLocationCode(inputMap);
		gLngLangId = SessionHelper.getLangId(inputMap);

	}

	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/lna/LNAConstants");

	public ResultObject loadDataEntryForm(Map<String, Object> inputMap) {
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		try {
			LNADataEntryFormDAO lObjDataEntryFormDAO = new LNADataEntryFormDAOImpl(MstLnaCompAdvance.class, serv.getSessionFactory());
			
			String lStrUserType = StringUtility.getParameter("userType", request);
			Long lLngDepartmentId = 100003L;
			
			List<ComboValuesVO> lLstFinYear = lObjDataEntryFormDAO.getFinYear();
			List<CmnLookupMst> lstComputerSubType = IFMSCommonServiceImpl.getLookupValues("Computer Sub Type", SessionHelper.getLangId(inputMap), inputMap);
			List<CmnLookupMst> lstHouseSubType = IFMSCommonServiceImpl.getLookupValues("House Sub Type", SessionHelper.getLangId(inputMap), inputMap);
			List<CmnLookupMst> lstVehicleSubType = IFMSCommonServiceImpl.getLookupValues("Vehicle Sub Type", SessionHelper.getLangId(inputMap), inputMap);
			List<ComboValuesVO> lstTreasury =  lObjDataEntryFormDAO.getAllTreasury(lLngDepartmentId, gLngLangId);
			DcpsCommonDAO lObjCommomDao = new DcpsCommonDAOImpl(SgvcFinYearMst.class, serv.getSessionFactory());
			List lLstMonths = lObjCommomDao.getMonths();

			inputMap.put("lstVehicleSubType", lstVehicleSubType);
			inputMap.put("lstHouseSubType", lstHouseSubType);
			inputMap.put("lstComputerSubType", lstComputerSubType);
			inputMap.put("lLstMonths", lLstMonths);
			inputMap.put("lLstYears", lLstFinYear);
			inputMap.put("UserType", lStrUserType);
			inputMap.put("lstTreasury", lstTreasury);

			resObj.setResultValue(inputMap);
			resObj.setViewName("LNADataEntryForm");
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		return resObj;
	}

	public ResultObject saveLNADataEntryForm(Map<String, Object> inputMap) {
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Map<String, List> lScheduleVOMap = new HashMap<String, List>();
		Map<String, List> lScheduleVOMapHBA = new HashMap<String, List>();
		Map<String, List> lScheduleVOMapMCA = new HashMap<String, List>();
		List<MstLnaMonthly> lLstVoucherDetails = null;
		List<MstLnaChallanDtls> lLstChallanDetails = null;
		List<MstLnaMonthly> lLstVoucherDetailsHBA = null;
		List<MstLnaChallanDtls> lLstChallanDetailsHBA = null;
		List<MstLnaMonthly> lLstVoucherDetailsMCA = null;
		List<MstLnaChallanDtls> lLstChallanDetailsMCA = null;
		MstLnaCompAdvance lObjCompAdvance = null;
		MstLnaHouseAdvance lObjHouseAdvance = null;
		MstLnaMotorAdvance lObjMotorAdvance = null;
		TrnEmpLoanDtls lObjEmpLoanDtls = null;
		MstLnaChallanDtls lObjChallanDtls = null;
		Integer lIntSaveOrUpdate = null;
		Integer lIntSaveOrUpdateHBA = null;
		Integer lIntSaveOrUpdateMCA = null;
		Integer iSaveOrUpdateFlagEmpLoan = null;
		String lStrSaveOrUpdateVchr = "";
		String lStrSaveOrUpdateChlln = "";
		String[] iSaveOrUpdate = null;
		MstLnaMonthly lObjLnaMonthly = null;
		String lStrTrnsId = null;
		Long lLngMonthlyId = null;
		Long lLngBillGrpId = null;
		Boolean lBlFlag = false;
		Long lLngRequestId = null;
		MstLnaRequest lObjLnaRequest = new MstLnaRequest();
		MstLnaRequest lObjLnaRequestHBA = new MstLnaRequest();
		MstLnaRequest lObjLnaRequestMCA = new MstLnaRequest();
		try {

			LNARequestProcessDAO lObjRequestProcessDAO = new LNARequestProcessDAOImpl(MstLnaCompAdvance.class, serv.getSessionFactory());
			LNADataEntryFormDAO lObjDataEntryFormDAO = new LNADataEntryFormDAOImpl(MstLnaCompAdvance.class, serv.getSessionFactory());
			LNADataEntryFormDAO lObjDataEntryFormDAOForChallan = new LNADataEntryFormDAOImpl(MstLnaChallanDtls.class, serv.getSessionFactory());
			LNAComputerAdvanceDAO lObjComputerAdvanceDAO = new LNAComputerAdvanceDAOImpl(MstLnaCompAdvance.class, serv.getSessionFactory());
			LNAHouseAdvanceDAO lObjHouseAdvanceDAO = new LNAHouseAdvanceDAOImpl(MstLnaHouseAdvance.class, serv.getSessionFactory());
			LNAMotorAdvanceDAO lObjMotorAdvanceDAO = new LNAMotorAdvanceDAOImpl(MstLnaMotorAdvance.class, serv.getSessionFactory());
			LNADataEntryFormDAO lObjEntryFormDAO = new LNADataEntryFormDAOImpl(TrnEmpLoanDtls.class, serv.getSessionFactory());
			String lStrSevaarthId = StringUtility.getParameter("txtEmployeeCode", request);
           
            
			lScheduleVOMap = (Map<String, List>) inputMap.get("ScheduleDtls");
			lLstChallanDetails = lScheduleVOMap.get("lLstChallanDtls");
			lLstVoucherDetails = lScheduleVOMap.get("lLstVoucherDtls");

			lScheduleVOMapHBA = (Map<String, List>) inputMap.get("ScheduleDtlsHBA");
			lLstChallanDetailsHBA = lScheduleVOMapHBA.get("lLstChallanDtlsHBA");
			lLstVoucherDetailsHBA = lScheduleVOMapHBA.get("lLstVoucherDtlsHBA");

			lScheduleVOMapMCA = (Map<String, List>) inputMap.get("ScheduleDtlsMCA");
			lLstChallanDetailsMCA = lScheduleVOMapMCA.get("lLstChallanDtlsMCA");
			lLstVoucherDetailsMCA = lScheduleVOMapMCA.get("lLstVoucherDtlsMCA");

			lObjCompAdvance = (MstLnaCompAdvance) inputMap.get("CompAdvance");
			lIntSaveOrUpdate = (Integer) inputMap.get("iSaveOrUpdateFlag");

			lObjEmpLoanDtls = (TrnEmpLoanDtls) inputMap.get("LoanDtls");
			System.out.println("map:"+inputMap.toString());
			System.out.println("map comp:"+lObjEmpLoanDtls.getCompAdvance());
			System.out.println("map house:"+lObjEmpLoanDtls.getHouseAdvance());
			System.out.println("map  motor:"+lObjEmpLoanDtls.getMotorAdvance());
			System.out.println("map status:"+lObjEmpLoanDtls.getStatus());
			iSaveOrUpdateFlagEmpLoan = (Integer) inputMap.get("iSaveOrUpdateFlagEmpLoan");

			String lStrCompAdvance = lObjEmpLoanDtls.getCompAdvance();
			String lStrHouseAdvance = lObjEmpLoanDtls.getHouseAdvance();
			String lStrMotorAdvance = lObjEmpLoanDtls.getMotorAdvance();

			lObjHouseAdvance = (MstLnaHouseAdvance) inputMap.get("HouseAdvance");
			lIntSaveOrUpdateHBA = (Integer) inputMap.get("iSaveOrUpdateFlagHBA");

			lObjMotorAdvance = (MstLnaMotorAdvance) inputMap.get("MotorAdvance");
			lIntSaveOrUpdateMCA = (Integer) inputMap.get("iSaveOrUpdateFlagMCA");

			lObjEmpLoanDtls.setStatus("D");
			if (iSaveOrUpdateFlagEmpLoan == 1) {
				Long lLngEmpLoanDtls = IFMSCommonServiceImpl.getNextSeqNum("trn_emp_loan_dtls", inputMap);
				lObjEmpLoanDtls.setEmpLoanDtlsId(lLngEmpLoanDtls);
				lObjEntryFormDAO.create(lObjEmpLoanDtls);
			} else {
				lObjEntryFormDAO.update(lObjEmpLoanDtls);
			}
			
			
			if ("Y".equals(lStrCompAdvance)) {
				lObjCompAdvance.setStatusFlag("DD");
				if (lIntSaveOrUpdate == 1) {
					Long lLngCompAdvanceId = IFMSCommonServiceImpl.getNextSeqNum("mst_lna_comp_advance", inputMap);
					lStrTrnsId = lObjRequestProcessDAO.getNewTransactionId(lStrSevaarthId, null);
					lObjCompAdvance.setComputerAdvanceId(lLngCompAdvanceId);
					lObjCompAdvance.setTransactionId(lStrTrnsId);
					lObjComputerAdvanceDAO.create(lObjCompAdvance);
					lLngRequestId = IFMSCommonServiceImpl.getNextSeqNum("mst_lna_request", inputMap);
					lObjLnaRequest.setRequestId(lLngRequestId);
					lObjLnaRequest.setTransactionId(lStrTrnsId);
					lObjLnaRequest.setLoanAdvanceId(lLngCompAdvanceId);
					lObjLnaRequest.setAdvanceType(lObjCompAdvance.getAdvanceType());
					lObjLnaRequest.setCreatedPostId(gLngPostId);
					lObjLnaRequest.setCreatedUserId(gLngUserId);
					lObjLnaRequest.setCreatedDate(gDtCurDate);
					lObjComputerAdvanceDAO.create(lObjLnaRequest);

				} else {
					lStrTrnsId = lObjCompAdvance.getTransactionId();
					lObjComputerAdvanceDAO.update(lObjCompAdvance);
				}

				if (lLstVoucherDetails != null && !lLstVoucherDetails.isEmpty()) {
					Long lLngPkCntMstLnaMonthly = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("mst_lna_monthly", inputMap, lLstVoucherDetails.size());
					for (int lIntCnt = 0; lIntCnt < lLstVoucherDetails.size(); lIntCnt++) {
						lStrSaveOrUpdateVchr = (String) inputMap.get("isSaveOrUpdateVchr");
						iSaveOrUpdate = lStrSaveOrUpdateVchr.split(",");
						lObjLnaMonthly = new MstLnaMonthly();
						lObjLnaMonthly = lLstVoucherDetails.get(lIntCnt);
						lObjLnaMonthly.setStatus("D");
						if (iSaveOrUpdate[lIntCnt].equals("1")) {
							lLngMonthlyId = ++lLngPkCntMstLnaMonthly;
							lLngMonthlyId = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngMonthlyId, inputMap);
							lObjLnaMonthly.setLnaMonthlyId(lLngMonthlyId);
							lLngBillGrpId = lObjDataEntryFormDAO.getBillGroupId(lStrSevaarthId);
							lObjLnaMonthly.setBillgroupId(lLngBillGrpId);
							if (!"".equals(lStrTrnsId)) {
								lObjLnaMonthly.setTransactionId(lStrTrnsId);
							}
							lObjDataEntryFormDAO.create(lObjLnaMonthly);
						} else if (iSaveOrUpdate[lIntCnt].equals("2")) {
							lObjDataEntryFormDAO.update(lObjLnaMonthly);
						}
					}
				}
				System.out.println("before challan");
				
				if (lLstChallanDetails != null && !lLstChallanDetails.isEmpty()) {
					System.out.println("inside challan ");
					Long lLngPkCntMstLnaMonthly = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("mst_lna_challan_dtls", inputMap, lLstChallanDetails.size());
					for (int lIntCnt = 0; lIntCnt < lLstChallanDetails.size(); lIntCnt++) {
						lStrSaveOrUpdateChlln = (String) inputMap.get("isSaveOrUpdateChln");
						System.out.println("date:" +lLstChallanDetails.get(0).getCreatedDate());
						System.out.println("date:" +lLstChallanDetails.get(0).getChallanDate());
						iSaveOrUpdate = lStrSaveOrUpdateChlln.split(",");
						lObjChallanDtls = new MstLnaChallanDtls();
						lObjChallanDtls = lLstChallanDetails.get(lIntCnt);
						lObjChallanDtls.setStatusFlag("D");
						if (iSaveOrUpdate[lIntCnt].equals("1")) {
							lLngMonthlyId = ++lLngPkCntMstLnaMonthly;
							lLngMonthlyId = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngMonthlyId, inputMap);
							lObjChallanDtls.setLnaChallanDtlsId(lLngMonthlyId);
							if (!"".equals(lStrTrnsId)) {
								lObjChallanDtls.setTransactionId(lStrTrnsId);
							}
							lObjDataEntryFormDAOForChallan.create(lObjChallanDtls);
						} else if (iSaveOrUpdate[lIntCnt].equals("2")) {
							lObjDataEntryFormDAOForChallan.update(lObjChallanDtls);
						}
					}
				}
			}
			
			System.out.println("after vouvher");
			if ("Y".equals(lStrHouseAdvance)) {
				lObjHouseAdvance.setStatusFlag("DD");
				if (lIntSaveOrUpdateHBA == 1) {
					Long lLngHouseAdvanceId = IFMSCommonServiceImpl.getNextSeqNum("mst_lna_house_advance", inputMap);
					lStrTrnsId = lObjRequestProcessDAO.getNewTransactionId(lStrSevaarthId, null);
					lObjHouseAdvance.setHouseAdvanceId(lLngHouseAdvanceId);
					lObjHouseAdvance.setTransactionId(lStrTrnsId);
					lObjHouseAdvanceDAO.create(lObjHouseAdvance);
					lLngRequestId = IFMSCommonServiceImpl.getNextSeqNum("mst_lna_request", inputMap);
					lObjLnaRequestHBA.setRequestId(lLngRequestId);
					lObjLnaRequestHBA.setTransactionId(lStrTrnsId);
					lObjLnaRequestHBA.setLoanAdvanceId(lLngHouseAdvanceId);
					lObjLnaRequestHBA.setAdvanceType(lObjHouseAdvance.getAdvanceType());
					lObjLnaRequestHBA.setCreatedPostId(gLngPostId);
					lObjLnaRequestHBA.setCreatedUserId(gLngUserId);
					lObjLnaRequestHBA.setCreatedDate(gDtCurDate);
					lObjHouseAdvanceDAO.create(lObjLnaRequestHBA);

				} else {
					lStrTrnsId = lObjHouseAdvance.getTransactionId();
					lObjHouseAdvanceDAO.update(lObjHouseAdvance);
				}

				if (lLstVoucherDetailsHBA != null && !lLstVoucherDetailsHBA.isEmpty()) {
					Long lLngPkCntMstLnaMonthly = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("mst_lna_monthly", inputMap, lLstVoucherDetailsHBA.size());
					for (int lIntCnt = 0; lIntCnt < lLstVoucherDetailsHBA.size(); lIntCnt++) {
						lStrSaveOrUpdateVchr = (String) inputMap.get("isSaveOrUpdateVchrHBA");
						iSaveOrUpdate = lStrSaveOrUpdateVchr.split(",");
						lObjLnaMonthly = new MstLnaMonthly();
						lObjLnaMonthly = lLstVoucherDetailsHBA.get(lIntCnt);
						lObjLnaMonthly.setStatus("D");
						if (iSaveOrUpdate[lIntCnt].equals("1")) {
							lLngMonthlyId = ++lLngPkCntMstLnaMonthly;
							lLngMonthlyId = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngMonthlyId, inputMap);
							lObjLnaMonthly.setLnaMonthlyId(lLngMonthlyId);
							lLngBillGrpId = lObjDataEntryFormDAO.getBillGroupId(lStrSevaarthId);
							lObjLnaMonthly.setBillgroupId(lLngBillGrpId);
							if (!"".equals(lStrTrnsId)) {
								lObjLnaMonthly.setTransactionId(lStrTrnsId);
							}
							lObjDataEntryFormDAO.create(lObjLnaMonthly);
						} else if (iSaveOrUpdate[lIntCnt].equals("2")) {
							lObjDataEntryFormDAO.update(lObjLnaMonthly);
						}
					}
				}
				if (lLstChallanDetailsHBA != null && !lLstChallanDetailsHBA.isEmpty()) {
					Long lLngPkCntMstLnaMonthly = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("mst_lna_challan_dtls", inputMap, lLstChallanDetailsHBA.size());
					for (int lIntCnt = 0; lIntCnt < lLstChallanDetailsHBA.size(); lIntCnt++) {
						lStrSaveOrUpdateChlln = (String) inputMap.get("isSaveOrUpdateChlnHBA");
						iSaveOrUpdate = lStrSaveOrUpdateChlln.split(",");
						lObjChallanDtls = new MstLnaChallanDtls();
						lObjChallanDtls = lLstChallanDetailsHBA.get(lIntCnt);
						lObjChallanDtls.setStatusFlag("D");
						if (iSaveOrUpdate[lIntCnt].equals("1")) {
							lLngMonthlyId = ++lLngPkCntMstLnaMonthly;
							lLngMonthlyId = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngMonthlyId, inputMap);
							lObjChallanDtls.setLnaChallanDtlsId(lLngMonthlyId);
							if (!"".equals(lStrTrnsId)) {
								lObjChallanDtls.setTransactionId(lStrTrnsId);
							}
							lObjDataEntryFormDAOForChallan.create(lObjChallanDtls);
						} else if (iSaveOrUpdate[lIntCnt].equals("2")) {
							lObjDataEntryFormDAOForChallan.update(lObjChallanDtls);
						}
					}
				}
			}
			
			
			if ("Y".equals(lStrMotorAdvance)) {
				lObjMotorAdvance.setStatusFlag("DD");
				if (lIntSaveOrUpdateMCA == 1) {
					Long lLngMotorAdvanceId = IFMSCommonServiceImpl.getNextSeqNum("mst_lna_motor_advance", inputMap);
					lStrTrnsId = lObjRequestProcessDAO.getNewTransactionId(lStrSevaarthId, null);
					lObjMotorAdvance.setMotorAdvanceId(lLngMotorAdvanceId);
					lObjMotorAdvance.setTransactionId(lStrTrnsId);
					lObjMotorAdvanceDAO.create(lObjMotorAdvance);
					lLngRequestId = IFMSCommonServiceImpl.getNextSeqNum("mst_lna_request", inputMap);
					lObjLnaRequestMCA.setRequestId(lLngRequestId);
					lObjLnaRequestMCA.setTransactionId(lStrTrnsId);
					lObjLnaRequestMCA.setLoanAdvanceId(lLngMotorAdvanceId);
					lObjLnaRequestMCA.setAdvanceType(lObjHouseAdvance.getAdvanceType());
					lObjLnaRequestMCA.setCreatedPostId(gLngPostId);
					lObjLnaRequestMCA.setCreatedUserId(gLngUserId);
					lObjLnaRequestMCA.setCreatedDate(gDtCurDate);
					lObjMotorAdvanceDAO.create(lObjLnaRequestMCA);

				} else {
					lStrTrnsId = lObjMotorAdvance.getTransactionId();
					lObjMotorAdvanceDAO.update(lObjMotorAdvance);
				}

				if (lLstVoucherDetailsMCA != null && !lLstVoucherDetailsMCA.isEmpty()) {
					Long lLngPkCntMstLnaMonthly = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("mst_lna_monthly", inputMap, lLstVoucherDetailsMCA.size());
					for (int lIntCnt = 0; lIntCnt < lLstVoucherDetailsMCA.size(); lIntCnt++) {
						lStrSaveOrUpdateVchr = (String) inputMap.get("isSaveOrUpdateVchrMCA");
						iSaveOrUpdate = lStrSaveOrUpdateVchr.split(",");
						lObjLnaMonthly = new MstLnaMonthly();
						lObjLnaMonthly = lLstVoucherDetailsMCA.get(lIntCnt);
						lObjLnaMonthly.setStatus("D");
						if (iSaveOrUpdate[lIntCnt].equals("1")) {
							lLngMonthlyId = ++lLngPkCntMstLnaMonthly;
							lLngMonthlyId = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngMonthlyId, inputMap);
							lObjLnaMonthly.setLnaMonthlyId(lLngMonthlyId);
							lLngBillGrpId = lObjDataEntryFormDAO.getBillGroupId(lStrSevaarthId);
							lObjLnaMonthly.setBillgroupId(lLngBillGrpId);
							if (!"".equals(lStrTrnsId)) {
								lObjLnaMonthly.setTransactionId(lStrTrnsId);
							}
							lObjDataEntryFormDAO.create(lObjLnaMonthly);
						} else if (iSaveOrUpdate[lIntCnt].equals("2")) {
							lObjDataEntryFormDAO.update(lObjLnaMonthly);
						}
					}
				}
				if (lLstChallanDetailsMCA != null && !lLstChallanDetailsMCA.isEmpty()) {
					Long lLngPkCntMstLnaMonthly = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("mst_lna_challan_dtls", inputMap, lLstChallanDetailsMCA.size());
					for (int lIntCnt = 0; lIntCnt < lLstChallanDetailsMCA.size(); lIntCnt++) {
						lStrSaveOrUpdateChlln = (String) inputMap.get("isSaveOrUpdateChlnMCA");
						iSaveOrUpdate = lStrSaveOrUpdateChlln.split(",");
						lObjChallanDtls = new MstLnaChallanDtls();
						lObjChallanDtls = lLstChallanDetailsMCA.get(lIntCnt);
						lObjChallanDtls.setStatusFlag("D");
						if (iSaveOrUpdate[lIntCnt].equals("1")) {
							lLngMonthlyId = ++lLngPkCntMstLnaMonthly;
							lLngMonthlyId = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngMonthlyId, inputMap);
							lObjChallanDtls.setLnaChallanDtlsId(lLngMonthlyId);
							if (!"".equals(lStrTrnsId)) {
								lObjChallanDtls.setTransactionId(lStrTrnsId);
							}
							lObjDataEntryFormDAOForChallan.create(lObjChallanDtls);
						} else if (iSaveOrUpdate[lIntCnt].equals("2")) {
							lObjDataEntryFormDAOForChallan.update(lObjChallanDtls);
						}
					}
				}
			}
			
			lBlFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		String lSBStatus = getSaveResponseXMLDocForDataEntry(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	public ResultObject popUpEmpDtlsDataEntryForm(Map<String, Object> inputMap) {
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrEmpName = null;
		String lStrDsgnName = null;
		String lStrOfficeName = null;
		Boolean lBlFlag = false;
		String lSBStatus = "";
		List lLstEmpDtls=null;
		List lLstEmpLoanCompDtls=null;
		List lLstEmpLoanVehicleDtls=null; 
		List lLstEmpLoanHouseDtls=null;
		try {
			LNADataEntryFormDAO lObjDataEntryFormDAO = new LNADataEntryFormDAOImpl(MstLnaCompAdvance.class, serv.getSessionFactory());
			DcpsCommonDAO lObjCommomDao = new DcpsCommonDAOImpl(SgvcFinYearMst.class, serv.getSessionFactory());
			
			String lStrEmpCode = StringUtility.getParameter("EmpCode", request);
			String lStrRequestPending = lObjDataEntryFormDAO.requestPendingStatus(lStrEmpCode.toUpperCase().trim());
			
			inputMap.put("SevaarthId",lStrEmpCode);
			Long lLngCompAdvance = 58L;
			Long lLngVehicleAdvance = 56L;
			Long lLngHouseAdvance = 67L;
			Long lLngDepartmentId = 100003L;
			
			Integer lIntTotalRecordCA = 0;
			Integer lIntTotalRecordHBA = 0;
			Integer lIntTotalRecordMCA = 0;
			
			if ("No".equals(lStrRequestPending)) {
				
				//lLstEmpDtls = lObjDataEntryFormDAO.getEmpDtls(lStrEmpCode.toUpperCase().trim(), gStrLocationCode);
				lLstEmpDtls = lObjDataEntryFormDAO.getNewEmpDtls(lStrEmpCode.toUpperCase().trim());
				if (!lLstEmpDtls.isEmpty()) {
					
					
					lLstEmpLoanCompDtls = lObjDataEntryFormDAO.getEmpLoanDtlsFromPayroll(lStrEmpCode, lLngCompAdvance);
					
					lLstEmpLoanVehicleDtls = lObjDataEntryFormDAO.getEmpLoanDtlsFromPayroll(lStrEmpCode, lLngVehicleAdvance);
					lLstEmpLoanHouseDtls = lObjDataEntryFormDAO.getEmpLoanDtlsFromPayroll(lStrEmpCode, lLngHouseAdvance);
					List<ComboValuesVO> lLstFinYear = lObjDataEntryFormDAO.getFinYear();
					List lLstMonths = lObjCommomDao.getMonths();
					
					List<CmnLookupMst> lstComputerSubType = IFMSCommonServiceImpl.getLookupValues("Computer Sub Type", SessionHelper.getLangId(inputMap), inputMap);
					List<CmnLookupMst> lstHouseSubType = IFMSCommonServiceImpl.getLookupValues("House Sub Type", SessionHelper.getLangId(inputMap), inputMap);
					List<CmnLookupMst> lstVehicleSubType = IFMSCommonServiceImpl.getLookupValues("Vehicle Sub Type", SessionHelper.getLangId(inputMap), inputMap);
					List<ComboValuesVO> lstTreasury =  lObjDataEntryFormDAO.getAllTreasury(lLngDepartmentId, gLngLangId);
					
					List lLstVocherDtlsCA = lObjDataEntryFormDAO.getEmpLoanRecoveryDtlsFromPayroll(lStrEmpCode.toUpperCase().trim(), lLngCompAdvance);
					if (lLstVocherDtlsCA != null && !lLstVocherDtlsCA.isEmpty()) {
						lIntTotalRecordCA = lIntTotalRecordCA + lLstVocherDtlsCA.size();
					}
					List lLstVocherDtlsHBA = lObjDataEntryFormDAO.getEmpLoanRecoveryDtlsFromPayroll(lStrEmpCode.toUpperCase().trim(), lLngHouseAdvance);
					if (lLstVocherDtlsHBA != null && !lLstVocherDtlsHBA.isEmpty()) {
						lIntTotalRecordHBA = lIntTotalRecordHBA + lLstVocherDtlsHBA.size();
					}
					
					List lLstVocherDtlsMCA = lObjDataEntryFormDAO.getEmpLoanRecoveryDtlsFromPayroll(lStrEmpCode.toUpperCase().trim(), lLngVehicleAdvance);
					if (lLstVocherDtlsMCA != null && !lLstVocherDtlsMCA.isEmpty()) {
						lIntTotalRecordMCA = lIntTotalRecordMCA + lLstVocherDtlsMCA.size();
					}
					
					inputMap.put("lLstVocherDtlsCA", lLstVocherDtlsCA);
					inputMap.put("lLstVocherDtlsHBA", lLstVocherDtlsHBA);
					inputMap.put("lLstVocherDtlsMCA", lLstVocherDtlsMCA);
					
					inputMap.put("EmpDtls", lLstEmpDtls.get(0));
									
					if(lLstEmpLoanCompDtls != null &&!lLstEmpLoanCompDtls.isEmpty()){
						inputMap.put("lLstCompDtls", lLstEmpLoanCompDtls.get(0));
					}
					if (lLstEmpLoanHouseDtls != null && !lLstEmpLoanHouseDtls.isEmpty()) {						
						inputMap.put("lLstHouseDtls", lLstEmpLoanHouseDtls.get(0));
					}
					if (lLstEmpLoanVehicleDtls != null && !lLstEmpLoanVehicleDtls.isEmpty()) {
						inputMap.put("lLstMotorDtls", lLstEmpLoanVehicleDtls.get(0));
					}
					
					inputMap.put("TotalRecordCA", lIntTotalRecordCA);
					inputMap.put("TotalRecordHBA", lIntTotalRecordHBA);
					inputMap.put("TotalRecordMCA", lIntTotalRecordMCA);
					inputMap.put("lstVehicleSubType", lstVehicleSubType);
					inputMap.put("lstHouseSubType", lstHouseSubType);
					inputMap.put("lstComputerSubType", lstComputerSubType);
					inputMap.put("lLstMonths", lLstMonths);
					inputMap.put("lLstYears", lLstFinYear);					
					inputMap.put("lstTreasury", lstTreasury);
					inputMap.put("Payroll", "Yes");
				}else{
					inputMap.put("msg", "InvalidEmp");
				}
			
			} else {
				inputMap.put("msg", "Pending");				
			}
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}

		resObj.setResultValue(inputMap);
		resObj.setViewName("LNADataEntryForm");
		return resObj;
	}

	public ResultObject loadDraftDataEntry(Map<String, Object> inputMap) {
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List lLstDraftReqComp = null;
		try {
			String lStrUserType = StringUtility.getParameter("userType", request);
			LNADataEntryFormDAO lObjDataEntryFormDAO = new LNADataEntryFormDAOImpl(TrnEmpLoanDtls.class, serv.getSessionFactory());
			lLstDraftReqComp = lObjDataEntryFormDAO.getDraftReq(lStrUserType, gStrLocationCode);
			inputMap.put("DraftReq", lLstDraftReqComp);
			inputMap.put("UserType", lStrUserType);
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		resObj.setResultValue(inputMap);
		resObj.setViewName("LNADataEntryWorklist");
		return resObj;
	}

	public ResultObject loadLNADataEntryCase(Map<String, Object> inputMap) {
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List lLstCompDtls = null;
		List lLstHouseDtls = null;
		List lLstMotorDtls = null;
		List lLstVocherDtlsCA = null;
		List lLstVocherDtlsHBA = null;
		List lLstVocherDtlsMCA = null;
		List lLstChallanDtlsCA = null;
		List lLstChallanDtlsHBA = null;
		List lLstChallanDtlsMCA = null;
		Object[] lObj = null;
		Integer lIntTotalRecordCA = 0;
		Integer lIntTotalRecordHBA = 0;
		Integer lIntTotalRecordMCA = 0;
		try {
			LNADataEntryFormDAO lObjDataEntryFormDAO = new LNADataEntryFormDAOImpl(MstLnaCompAdvance.class, serv.getSessionFactory());
			DcpsCommonDAO lObjCommomDao = new DcpsCommonDAOImpl(SgvcFinYearMst.class, serv.getSessionFactory());
			List lLstMonths = lObjCommomDao.getMonths();

			String lStrSevaarthId = StringUtility.getParameter("sevaarthId", request);
			String lStrCompAdvc = StringUtility.getParameter("compAdvance", request);
			String lStrHouseAdvc = StringUtility.getParameter("houseAdvance", request);
			String lStrMotorAdvc = StringUtility.getParameter("motorAdvance", request);
			String lStrTrnEmpLoanPk = StringUtility.getParameter("pkValue", request);
			String lStrUserType = StringUtility.getParameter("userType", request);
			Long lLngDepartmentId = 100003L;
			
			List<ComboValuesVO> lLstFinYear = lObjDataEntryFormDAO.getFinYear();
			List<CmnLookupMst> lstComputerSubType = IFMSCommonServiceImpl.getLookupValues("Computer Sub Type", SessionHelper.getLangId(inputMap), inputMap);
			List<CmnLookupMst> lstHouseSubType = IFMSCommonServiceImpl.getLookupValues("House Sub Type", SessionHelper.getLangId(inputMap), inputMap);
			List<CmnLookupMst> lstVehicleSubType = IFMSCommonServiceImpl.getLookupValues("Vehicle Sub Type", SessionHelper.getLangId(inputMap), inputMap);
			List<ComboValuesVO> lstTreasury =  lObjDataEntryFormDAO.getAllTreasury(lLngDepartmentId, gLngLangId);
			
			if ("Y".equals(lStrCompAdvc)) {
				lLstCompDtls = lObjDataEntryFormDAO.getCompAdvance(lStrSevaarthId, lStrUserType);
				if (lLstCompDtls != null && !lLstCompDtls.isEmpty()) {
					lObj = (Object[]) lLstCompDtls.get(0);
					inputMap.put("lLstCompDtls", lLstCompDtls.get(0));
				}
				lLstVocherDtlsCA = lObjDataEntryFormDAO.getVoucherDtls(lStrSevaarthId, Long.parseLong(gObjRsrcBndle.getString("LNA.COMPUTERADVANCE")), lStrUserType);
				if (lLstVocherDtlsCA != null && !lLstVocherDtlsCA.isEmpty()) {
					lIntTotalRecordCA = lIntTotalRecordCA + lLstVocherDtlsCA.size();
				}
				lLstChallanDtlsCA = lObjDataEntryFormDAO.getChallanDtls(lStrSevaarthId, lObj[8].toString(), lStrUserType);
				if (lLstChallanDtlsCA != null && !lLstChallanDtlsCA.isEmpty()) {
					lIntTotalRecordCA = lIntTotalRecordCA + lLstChallanDtlsCA.size();
					lLstVocherDtlsCA.addAll(lLstChallanDtlsCA);
				}

			}
			if ("Y".equals(lStrHouseAdvc)) {
				lLstHouseDtls = lObjDataEntryFormDAO.getHouseAdvance(lStrSevaarthId, lStrUserType);
				if (lLstHouseDtls != null && !lLstHouseDtls.isEmpty()) {
					lObj = (Object[]) lLstHouseDtls.get(0);
					inputMap.put("lLstHouseDtls", lLstHouseDtls.get(0));
				}
				lLstVocherDtlsHBA = lObjDataEntryFormDAO.getVoucherDtls(lStrSevaarthId, Long.parseLong(gObjRsrcBndle.getString("LNA.HOUSEADVANCE")), lStrUserType);
				if (lLstVocherDtlsHBA != null && !lLstVocherDtlsHBA.isEmpty()) {
					lIntTotalRecordHBA = lIntTotalRecordHBA + lLstVocherDtlsHBA.size();
				}
				lLstChallanDtlsHBA = lObjDataEntryFormDAO.getChallanDtls(lStrSevaarthId, lObj[11].toString(), lStrUserType);
				if (lLstChallanDtlsHBA != null && !lLstChallanDtlsHBA.isEmpty()) {
					lIntTotalRecordHBA = lIntTotalRecordHBA + lLstChallanDtlsHBA.size();
					lLstVocherDtlsHBA.addAll(lLstChallanDtlsHBA);
				}

			}
			if ("Y".equals(lStrMotorAdvc)) {
				lLstMotorDtls = lObjDataEntryFormDAO.getMotorAdvance(lStrSevaarthId, lStrUserType);
				if (lLstMotorDtls != null && !lLstMotorDtls.isEmpty()) {
					lObj = (Object[]) lLstMotorDtls.get(0);
					inputMap.put("lLstMotorDtls", lLstMotorDtls.get(0));
				}
				lLstVocherDtlsMCA = lObjDataEntryFormDAO.getVoucherDtls(lStrSevaarthId, Long.parseLong(gObjRsrcBndle.getString("LNA.MOTORADVANCE")), lStrUserType);
				if (lLstVocherDtlsMCA != null && !lLstVocherDtlsMCA.isEmpty()) {
					lIntTotalRecordMCA = lIntTotalRecordMCA + lLstVocherDtlsMCA.size();
				}
				lLstChallanDtlsMCA = lObjDataEntryFormDAO.getChallanDtls(lStrSevaarthId, lObj[11].toString(), lStrUserType);
				if (lLstChallanDtlsMCA != null && !lLstChallanDtlsMCA.isEmpty()) {
					lIntTotalRecordMCA = lIntTotalRecordMCA + lLstChallanDtlsMCA.size();
					lLstVocherDtlsMCA.addAll(lLstChallanDtlsMCA);
				}

			}

			//List lLstEmpDtls = lObjDataEntryFormDAO.getEmpDtls(lStrSevaarthId, gStrLocationCode);
			List lLstEmpDtls = lObjDataEntryFormDAO.getNewEmpDtls(lStrSevaarthId);
			if (lLstEmpDtls != null && !lLstEmpDtls.isEmpty()) {
				inputMap.put("EmpDtls", lLstEmpDtls.get(0));
			}
			inputMap.put("SevaarthId", lStrSevaarthId);
			inputMap.put("UserType", lStrUserType);
			inputMap.put("lLstVocherDtlsCA", lLstVocherDtlsCA);
			inputMap.put("lLstVocherDtlsHBA", lLstVocherDtlsHBA);
			inputMap.put("lLstVocherDtlsMCA", lLstVocherDtlsMCA);
			inputMap.put("lstVehicleSubType", lstVehicleSubType);
			inputMap.put("lstHouseSubType", lstHouseSubType);
			inputMap.put("lstComputerSubType", lstComputerSubType);
			inputMap.put("lLstMonths", lLstMonths);
			inputMap.put("lLstYears", lLstFinYear);
			inputMap.put("TotalRecordCA", lIntTotalRecordCA);
			inputMap.put("TotalRecordHBA", lIntTotalRecordHBA);
			inputMap.put("TotalRecordMCA", lIntTotalRecordMCA);
			inputMap.put("TrnEmpLoanPk", lStrTrnEmpLoanPk);
			inputMap.put("lstTreasury", lstTreasury);

		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		resObj.setResultValue(inputMap);
		resObj.setViewName("LNADataEntryForm");
		return resObj;
	}

	public ResultObject forwardLNADataEntryForm(Map<String, Object> inputMap) {
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Map<String, List> lScheduleVOMap = new HashMap<String, List>();
		Map<String, List> lScheduleVOMapHBA = new HashMap<String, List>();
		Map<String, List> lScheduleVOMapMCA = new HashMap<String, List>();
		List<MstLnaMonthly> lLstVoucherDetails = null;
		List<MstLnaChallanDtls> lLstChallanDetails = null;
		List<MstLnaMonthly> lLstVoucherDetailsHBA = null;
		List<MstLnaChallanDtls> lLstChallanDetailsHBA = null;
		List<MstLnaMonthly> lLstVoucherDetailsMCA = null;
		List<MstLnaChallanDtls> lLstChallanDetailsMCA = null;
		MstLnaCompAdvance lObjCompAdvance = null;
		MstLnaHouseAdvance lObjHouseAdvance = null;
		MstLnaMotorAdvance lObjMotorAdvance = null;
		TrnEmpLoanDtls lObjEmpLoanDtls = null;
		MstLnaChallanDtls lObjChallanDtls = null;
		Integer lIntSaveOrUpdate = null;
		Integer lIntSaveOrUpdateHBA = null;
		Integer lIntSaveOrUpdateMCA = null;
		Integer iSaveOrUpdateFlagEmpLoan = null;
		String lStrSaveOrUpdateVchr = "";
		String lStrSaveOrUpdateChlln = "";
		String[] iSaveOrUpdate = null;
		MstLnaMonthly lObjLnaMonthly = null;
		String lStrTrnsId = null;
		Long lLngMonthlyId = null;
		Long lLngBillGrpId = null;
		Boolean lBlFlag = false;
		Long lLngRequestId = null;
		MstLnaRequest lObjLnaRequest = new MstLnaRequest();
		MstLnaRequest lObjLnaRequestHBA = new MstLnaRequest();
		MstLnaRequest lObjLnaRequestMCA = new MstLnaRequest();
		try {

			LNARequestProcessDAO lObjRequestProcessDAO = new LNARequestProcessDAOImpl(MstLnaCompAdvance.class, serv.getSessionFactory());
			LNADataEntryFormDAO lObjDataEntryFormDAO = new LNADataEntryFormDAOImpl(MstLnaCompAdvance.class, serv.getSessionFactory());
			LNADataEntryFormDAO lObjDataEntryFormDAOForChallan = new LNADataEntryFormDAOImpl(MstLnaChallanDtls.class, serv.getSessionFactory());
			LNAComputerAdvanceDAO lObjComputerAdvanceDAO = new LNAComputerAdvanceDAOImpl(MstLnaCompAdvance.class, serv.getSessionFactory());
			LNAHouseAdvanceDAO lObjHouseAdvanceDAO = new LNAHouseAdvanceDAOImpl(MstLnaHouseAdvance.class, serv.getSessionFactory());
			LNAMotorAdvanceDAO lObjMotorAdvanceDAO = new LNAMotorAdvanceDAOImpl(MstLnaMotorAdvance.class, serv.getSessionFactory());
			LNADataEntryFormDAO lObjEntryFormDAO = new LNADataEntryFormDAOImpl(TrnEmpLoanDtls.class, serv.getSessionFactory());
			String lStrSevaarthId = StringUtility.getParameter("txtEmployeeCode", request);

			lScheduleVOMap = (Map<String, List>) inputMap.get("ScheduleDtls");
			lLstChallanDetails = lScheduleVOMap.get("lLstChallanDtls");
			lLstVoucherDetails = lScheduleVOMap.get("lLstVoucherDtls");

			lScheduleVOMapHBA = (Map<String, List>) inputMap.get("ScheduleDtlsHBA");
			lLstChallanDetailsHBA = lScheduleVOMapHBA.get("lLstChallanDtlsHBA");
			lLstVoucherDetailsHBA = lScheduleVOMapHBA.get("lLstVoucherDtlsHBA");

			lScheduleVOMapMCA = (Map<String, List>) inputMap.get("ScheduleDtlsMCA");
			lLstChallanDetailsMCA = lScheduleVOMapMCA.get("lLstChallanDtlsMCA");
			lLstVoucherDetailsMCA = lScheduleVOMapMCA.get("lLstVoucherDtlsMCA");

			lObjCompAdvance = (MstLnaCompAdvance) inputMap.get("CompAdvance");
			lIntSaveOrUpdate = (Integer) inputMap.get("iSaveOrUpdateFlag");

			lObjEmpLoanDtls = (TrnEmpLoanDtls) inputMap.get("LoanDtls");
			iSaveOrUpdateFlagEmpLoan = (Integer) inputMap.get("iSaveOrUpdateFlagEmpLoan");

			String lStrCompAdvance = lObjEmpLoanDtls.getCompAdvance();
			String lStrHouseAdvance = lObjEmpLoanDtls.getHouseAdvance();
			String lStrMotorAdvance = lObjEmpLoanDtls.getMotorAdvance();

			lObjHouseAdvance = (MstLnaHouseAdvance) inputMap.get("HouseAdvance");
			lIntSaveOrUpdateHBA = (Integer) inputMap.get("iSaveOrUpdateFlagHBA");

			lObjMotorAdvance = (MstLnaMotorAdvance) inputMap.get("MotorAdvance");
			lIntSaveOrUpdateMCA = (Integer) inputMap.get("iSaveOrUpdateFlagMCA");

			lObjEmpLoanDtls.setStatus("F");
			if (iSaveOrUpdateFlagEmpLoan == 1) {
				Long lLngEmpLoanDtls = IFMSCommonServiceImpl.getNextSeqNum("trn_emp_loan_dtls", inputMap);
				lObjEmpLoanDtls.setEmpLoanDtlsId(lLngEmpLoanDtls);
				lObjEntryFormDAO.create(lObjEmpLoanDtls);
			} else {
				lObjEntryFormDAO.update(lObjEmpLoanDtls);
			}

			if ("Y".equals(lStrCompAdvance)) {
				lObjCompAdvance.setStatusFlag("F");
				if (lIntSaveOrUpdate == 1) {
					Long lLngCompAdvanceId = IFMSCommonServiceImpl.getNextSeqNum("mst_lna_comp_advance", inputMap);
					lStrTrnsId = lObjRequestProcessDAO.getNewTransactionId(lStrSevaarthId, null);
					lObjCompAdvance.setComputerAdvanceId(lLngCompAdvanceId);
					lObjCompAdvance.setTransactionId(lStrTrnsId);
					lObjComputerAdvanceDAO.create(lObjCompAdvance);
					lLngRequestId = IFMSCommonServiceImpl.getNextSeqNum("mst_lna_request", inputMap);
					lObjLnaRequest.setRequestId(lLngRequestId);
					lObjLnaRequest.setTransactionId(lStrTrnsId);
					lObjLnaRequest.setLoanAdvanceId(lLngCompAdvanceId);
					lObjLnaRequest.setAdvanceType(lObjCompAdvance.getAdvanceType());
					lObjLnaRequest.setCreatedPostId(gLngPostId);
					lObjLnaRequest.setCreatedUserId(gLngUserId);
					lObjLnaRequest.setCreatedDate(gDtCurDate);
					lObjComputerAdvanceDAO.create(lObjLnaRequest);

				} else {
					lStrTrnsId = lObjCompAdvance.getTransactionId();
					lObjComputerAdvanceDAO.update(lObjCompAdvance);
				}

				if (lLstVoucherDetails != null && !lLstVoucherDetails.isEmpty()) {
					Long lLngPkCntMstLnaMonthly = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("mst_lna_monthly", inputMap, lLstVoucherDetails.size());
					for (int lIntCnt = 0; lIntCnt < lLstVoucherDetails.size(); lIntCnt++) {
						lStrSaveOrUpdateVchr = (String) inputMap.get("isSaveOrUpdateVchr");
						iSaveOrUpdate = lStrSaveOrUpdateVchr.split(",");
						lObjLnaMonthly = new MstLnaMonthly();
						lObjLnaMonthly = lLstVoucherDetails.get(lIntCnt);
						lObjLnaMonthly.setStatus("F");
						if (iSaveOrUpdate[lIntCnt].equals("1")) {
							lLngMonthlyId = ++lLngPkCntMstLnaMonthly;
							lLngMonthlyId = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngMonthlyId, inputMap);
							lObjLnaMonthly.setLnaMonthlyId(lLngMonthlyId);
							lLngBillGrpId = lObjDataEntryFormDAO.getBillGroupId(lStrSevaarthId);
							lObjLnaMonthly.setBillgroupId(lLngBillGrpId);
							if (!"".equals(lStrTrnsId)) {
								lObjLnaMonthly.setTransactionId(lStrTrnsId);
							}
							lObjDataEntryFormDAO.create(lObjLnaMonthly);
						} else if (iSaveOrUpdate[lIntCnt].equals("2")) {
							lObjDataEntryFormDAO.update(lObjLnaMonthly);
						}
					}
				}
				if (lLstChallanDetails != null && !lLstChallanDetails.isEmpty()) {
					Long lLngPkCntMstLnaMonthly = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("mst_lna_challan_dtls", inputMap, lLstChallanDetails.size());
					for (int lIntCnt = 0; lIntCnt < lLstChallanDetails.size(); lIntCnt++) {
						lStrSaveOrUpdateChlln = (String) inputMap.get("isSaveOrUpdateChln");
						iSaveOrUpdate = lStrSaveOrUpdateChlln.split(",");
						lObjChallanDtls = new MstLnaChallanDtls();
						lObjChallanDtls = lLstChallanDetails.get(lIntCnt);
						lObjChallanDtls.setStatusFlag("F");
						if (iSaveOrUpdate[lIntCnt].equals("1")) {
							lLngMonthlyId = ++lLngPkCntMstLnaMonthly;
							lLngMonthlyId = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngMonthlyId, inputMap);
							lObjChallanDtls.setLnaChallanDtlsId(lLngMonthlyId);
							if (!"".equals(lStrTrnsId)) {
								lObjChallanDtls.setTransactionId(lStrTrnsId);
							}
							lObjDataEntryFormDAOForChallan.create(lObjChallanDtls);
						} else if (iSaveOrUpdate[lIntCnt].equals("2")) {
							lObjDataEntryFormDAOForChallan.update(lObjChallanDtls);
						}
					}
				}
			}
			if ("Y".equals(lStrHouseAdvance)) {
				lObjHouseAdvance.setStatusFlag("F");
				if (lIntSaveOrUpdateHBA == 1) {
					Long lLngHouseAdvanceId = IFMSCommonServiceImpl.getNextSeqNum("mst_lna_house_advance", inputMap);
					lStrTrnsId = lObjRequestProcessDAO.getNewTransactionId(lStrSevaarthId, null);
					lObjHouseAdvance.setHouseAdvanceId(lLngHouseAdvanceId);
					lObjHouseAdvance.setTransactionId(lStrTrnsId);
					lObjHouseAdvanceDAO.create(lObjHouseAdvance);
					lLngRequestId = IFMSCommonServiceImpl.getNextSeqNum("mst_lna_request", inputMap);
					lObjLnaRequestHBA.setRequestId(lLngRequestId);
					lObjLnaRequestHBA.setTransactionId(lStrTrnsId);
					lObjLnaRequestHBA.setLoanAdvanceId(lLngHouseAdvanceId);
					lObjLnaRequestHBA.setAdvanceType(lObjHouseAdvance.getAdvanceType());
					lObjLnaRequestHBA.setCreatedPostId(gLngPostId);
					lObjLnaRequestHBA.setCreatedUserId(gLngUserId);
					lObjLnaRequestHBA.setCreatedDate(gDtCurDate);
					lObjHouseAdvanceDAO.create(lObjLnaRequestHBA);

				} else {
					lStrTrnsId = lObjHouseAdvance.getTransactionId();
					lObjHouseAdvanceDAO.update(lObjHouseAdvance);
				}

				if (lLstVoucherDetailsHBA != null && !lLstVoucherDetailsHBA.isEmpty()) {
					Long lLngPkCntMstLnaMonthly = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("mst_lna_monthly", inputMap, lLstVoucherDetailsHBA.size());
					for (int lIntCnt = 0; lIntCnt < lLstVoucherDetailsHBA.size(); lIntCnt++) {
						lStrSaveOrUpdateVchr = (String) inputMap.get("isSaveOrUpdateVchrHBA");
						iSaveOrUpdate = lStrSaveOrUpdateVchr.split(",");
						lObjLnaMonthly = new MstLnaMonthly();
						lObjLnaMonthly = lLstVoucherDetailsHBA.get(lIntCnt);
						lObjLnaMonthly.setStatus("F");
						if (iSaveOrUpdate[lIntCnt].equals("1")) {
							lLngMonthlyId = ++lLngPkCntMstLnaMonthly;
							lLngMonthlyId = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngMonthlyId, inputMap);
							lObjLnaMonthly.setLnaMonthlyId(lLngMonthlyId);
							lLngBillGrpId = lObjDataEntryFormDAO.getBillGroupId(lStrSevaarthId);
							lObjLnaMonthly.setBillgroupId(lLngBillGrpId);
							if (!"".equals(lStrTrnsId)) {
								lObjLnaMonthly.setTransactionId(lStrTrnsId);
							}
							lObjDataEntryFormDAO.create(lObjLnaMonthly);
						} else if (iSaveOrUpdate[lIntCnt].equals("2")) {
							lObjDataEntryFormDAO.update(lObjLnaMonthly);
						}
					}
				}
				if (lLstChallanDetailsHBA != null && !lLstChallanDetailsHBA.isEmpty()) {
					Long lLngPkCntMstLnaMonthly = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("mst_lna_challan_dtls", inputMap, lLstChallanDetailsHBA.size());
					for (int lIntCnt = 0; lIntCnt < lLstChallanDetailsHBA.size(); lIntCnt++) {
						lStrSaveOrUpdateChlln = (String) inputMap.get("isSaveOrUpdateChlnHBA");
						iSaveOrUpdate = lStrSaveOrUpdateChlln.split(",");
						lObjChallanDtls = new MstLnaChallanDtls();
						lObjChallanDtls = lLstChallanDetailsHBA.get(lIntCnt);
						lObjChallanDtls.setStatusFlag("F");
						if (iSaveOrUpdate[lIntCnt].equals("1")) {
							lLngMonthlyId = ++lLngPkCntMstLnaMonthly;
							lLngMonthlyId = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngMonthlyId, inputMap);
							lObjChallanDtls.setLnaChallanDtlsId(lLngMonthlyId);
							if (!"".equals(lStrTrnsId)) {
								lObjChallanDtls.setTransactionId(lStrTrnsId);
							}
							lObjDataEntryFormDAOForChallan.create(lObjChallanDtls);
						} else if (iSaveOrUpdate[lIntCnt].equals("2")) {
							lObjDataEntryFormDAOForChallan.update(lObjChallanDtls);
						}
					}
				}
			}
			if ("Y".equals(lStrMotorAdvance)) {
				lObjMotorAdvance.setStatusFlag("F");
				if (lIntSaveOrUpdateMCA == 1) {
					Long lLngMotorAdvanceId = IFMSCommonServiceImpl.getNextSeqNum("mst_lna_motor_advance", inputMap);
					lStrTrnsId = lObjRequestProcessDAO.getNewTransactionId(lStrSevaarthId, null);
					lObjMotorAdvance.setMotorAdvanceId(lLngMotorAdvanceId);
					lObjMotorAdvance.setTransactionId(lStrTrnsId);
					lObjMotorAdvanceDAO.create(lObjMotorAdvance);
					lLngRequestId = IFMSCommonServiceImpl.getNextSeqNum("mst_lna_request", inputMap);
					lObjLnaRequestMCA.setRequestId(lLngRequestId);
					lObjLnaRequestMCA.setTransactionId(lStrTrnsId);
					lObjLnaRequestMCA.setLoanAdvanceId(lLngMotorAdvanceId);
					lObjLnaRequestMCA.setAdvanceType(lObjHouseAdvance.getAdvanceType());
					lObjLnaRequestMCA.setCreatedPostId(gLngPostId);
					lObjLnaRequestMCA.setCreatedUserId(gLngUserId);
					lObjLnaRequestMCA.setCreatedDate(gDtCurDate);
					lObjMotorAdvanceDAO.create(lObjLnaRequestMCA);

				} else {
					lStrTrnsId = lObjMotorAdvance.getTransactionId();
					lObjMotorAdvanceDAO.update(lObjMotorAdvance);
				}

				if (lLstVoucherDetailsMCA != null && !lLstVoucherDetailsMCA.isEmpty()) {
					Long lLngPkCntMstLnaMonthly = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("mst_lna_monthly", inputMap, lLstVoucherDetailsMCA.size());
					for (int lIntCnt = 0; lIntCnt < lLstVoucherDetailsMCA.size(); lIntCnt++) {
						lStrSaveOrUpdateVchr = (String) inputMap.get("isSaveOrUpdateVchrMCA");
						iSaveOrUpdate = lStrSaveOrUpdateVchr.split(",");
						lObjLnaMonthly = new MstLnaMonthly();
						lObjLnaMonthly = lLstVoucherDetailsMCA.get(lIntCnt);
						lObjLnaMonthly.setStatus("F");
						if (iSaveOrUpdate[lIntCnt].equals("1")) {
							lLngMonthlyId = ++lLngPkCntMstLnaMonthly;
							lLngMonthlyId = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngMonthlyId, inputMap);
							lObjLnaMonthly.setLnaMonthlyId(lLngMonthlyId);
							lLngBillGrpId = lObjDataEntryFormDAO.getBillGroupId(lStrSevaarthId);
							lObjLnaMonthly.setBillgroupId(lLngBillGrpId);
							if (!"".equals(lStrTrnsId)) {
								lObjLnaMonthly.setTransactionId(lStrTrnsId);
							}
							lObjDataEntryFormDAO.create(lObjLnaMonthly);
						} else if (iSaveOrUpdate[lIntCnt].equals("2")) {
							lObjDataEntryFormDAO.update(lObjLnaMonthly);
						}
					}
				}
				if (lLstChallanDetailsMCA != null && !lLstChallanDetailsMCA.isEmpty()) {
					Long lLngPkCntMstLnaMonthly = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("mst_lna_challan_dtls", inputMap, lLstChallanDetailsMCA.size());
					for (int lIntCnt = 0; lIntCnt < lLstChallanDetailsMCA.size(); lIntCnt++) {
						lStrSaveOrUpdateChlln = (String) inputMap.get("isSaveOrUpdateChlnMCA");
						iSaveOrUpdate = lStrSaveOrUpdateChlln.split(",");
						lObjChallanDtls = new MstLnaChallanDtls();
						lObjChallanDtls = lLstChallanDetailsMCA.get(lIntCnt);
						lObjChallanDtls.setStatusFlag("F");
						if (iSaveOrUpdate[lIntCnt].equals("1")) {
							lLngMonthlyId = ++lLngPkCntMstLnaMonthly;
							lLngMonthlyId = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngMonthlyId, inputMap);
							lObjChallanDtls.setLnaChallanDtlsId(lLngMonthlyId);
							if (!"".equals(lStrTrnsId)) {
								lObjChallanDtls.setTransactionId(lStrTrnsId);
							}
							lObjDataEntryFormDAOForChallan.create(lObjChallanDtls);
						} else if (iSaveOrUpdate[lIntCnt].equals("2")) {
							lObjDataEntryFormDAOForChallan.update(lObjChallanDtls);
						}
					}
				}
			}
			lBlFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		String lSBStatus = getSaveResponseXMLDocForDataEntry(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	public ResultObject approveLNADataEntryForm(Map<String, Object> inputMap) {
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = false;
		try {
			String lStrTrnEmpLoanPk = StringUtility.getParameter("TrnEmpLoanPk", request);
			String lStrCompAdvanceId = StringUtility.getParameter("hidCompId", request);
			String lStrHouseAdvanceId = StringUtility.getParameter("hidHouseId", request);
			String lStrMotorAdvanceId = StringUtility.getParameter("hidMotorId", request);
			String lStrSevaarthId = StringUtility.getParameter("txtEmployeeCode", request);
			String lStrHODRemarksCA = StringUtility.getParameter("txtHODRemarksCA", request);
			String lStrHODRemarksHBA = StringUtility.getParameter("txtHODRemarksHBA", request);
			String lStrHODRemarksMCA = StringUtility.getParameter("txtHODRemarksMCA", request);

			LNADataEntryFormDAO lObjDataEntryFormDAO = new LNADataEntryFormDAOImpl(MstLnaMonthly.class, serv.getSessionFactory());

			if (!"".equals(lStrTrnEmpLoanPk.trim())) {
				lObjDataEntryFormDAO.updateEmpLoanDtls(lStrTrnEmpLoanPk);
			}
			if (!"".equals(lStrCompAdvanceId.trim())) {
				lObjDataEntryFormDAO.updateCompAdvance(lStrCompAdvanceId, lStrHODRemarksCA);
			}
			if (!"".equals(lStrHouseAdvanceId.trim())) {
				lObjDataEntryFormDAO.updateHouseAdvance(lStrHouseAdvanceId, lStrHODRemarksHBA);
			}
			if (!"".equals(lStrMotorAdvanceId.trim())) {
				lObjDataEntryFormDAO.updateMotorAdvance(lStrMotorAdvanceId, lStrHODRemarksMCA);
			}
			if (!"".equals(lStrSevaarthId.trim())) {
				lObjDataEntryFormDAO.updateChallanDtls(lStrSevaarthId);
				lObjDataEntryFormDAO.updateVoucherDtls(lStrSevaarthId);

			}

			lBlFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		String lSBStatus = getSaveResponseXMLDocForDataEntry(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	private StringBuilder getResponseXMLDocForEmpDtls(Boolean flag, String lStrEmpName, String lStrDsgnName, String lStrOfficeName) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</FLAG>");
		lStrBldXML.append("<lEmpName>");
		lStrBldXML.append(lStrEmpName);
		lStrBldXML.append("</lEmpName>");
		lStrBldXML.append("<lDsgnName>");
		lStrBldXML.append(lStrDsgnName);
		lStrBldXML.append("</lDsgnName>");
		lStrBldXML.append("<lOfficeName>");
		lStrBldXML.append(lStrOfficeName);
		lStrBldXML.append("</lOfficeName>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	private StringBuilder getSaveResponseXMLDocForDataEntry(Boolean flag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</FLAG>");
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}

	private StringBuilder getResponseXMLDocForPendingReq(String lStrReqPending) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<lStrReqPending>");
		lStrBldXML.append(lStrReqPending);
		lStrBldXML.append("</lStrReqPending>");
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}
}
