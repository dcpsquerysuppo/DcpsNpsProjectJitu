package com.tcs.sgv.pensionpay.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.tcs.sgv.common.constant.DBConstants;
import com.tcs.sgv.common.dao.BillMovementDAOImpl;
import com.tcs.sgv.common.dao.BptmCommonServicesDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.RltBillParty;
import com.tcs.sgv.common.valueobject.TrnBillMvmnt;
import com.tcs.sgv.common.valueobject.TrnBillRegister;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.valueobject.ConfigOnlineBill;


/**
 * Class for generating Common VOs Related to Online Bill.
 * 
 * @author Vipul Sanghani
 * @version 1.0
 */
public class OnlineBillVOGenerator extends ServiceImpl {

	/** Global Variable for PostId */
	private Long gLngPostId = null;

	/** Global Variable for UserId */
	private Long gLngUserId = null;

	/** Global Variable for LangId */
	private Long gLngLangId = null;

	/** Global Variable for Location Code */
	private String gStrLocationCode = null;

	/** Global Variable for DB Id */
	private Long gLngDBId = null;

	/** Global Variable for Current Date */
	private Date gDtCurrDt = null;

	/** Global Variable for Fin Year Id */
	private Integer gFinYrId = null;

	/** Global Variable for Logger Class */
	private Log gLogger = LogFactory.getLog(getClass());

	/** Global Variable for Resourse Bundle */
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/onlinebillprep/OnlineBillConstants");

	/**
	 * Generates all the common VOs related to Bill.
	 * 
	 * @param Map
	 *            InputMap
	 * @return ResultObject
	 */
	public ResultObject generateMap(Map lMapInput) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator srvcLoc = (ServiceLocator) lMapInput.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) lMapInput.get("requestObj");
		try {
			setSessionInfo(lMapInput);
			String lStrSubjectId = StringUtility.getParameter("hidBillTypeId", request);
			String lStrBillNo = StringUtility.getParameter("hidBillNo", request);
			TrnBillMvmnt lObjBillMvmnt = generateBillMvmntVO(lMapInput);
			lMapInput.put("BillMvmntVO", lObjBillMvmnt);

			if (!"44".equals(lStrSubjectId)) {
				TrnBillRegister lObjBillReg = generateBillRegVO(lMapInput);
				lMapInput.put("BillRegVO", lObjBillReg);
				if (lObjBillReg.getBillNo() == 0L || lObjBillReg.getSubjectId() == 45L) {
					List<RltBillParty> lLstBillParty = generateBillPartyVO(lMapInput);
					if (lLstBillParty != null && !lLstBillParty.isEmpty()) {
						lMapInput.put("BillPartyDtls", lLstBillParty);
					}
					ConfigOnlineBill lObjConfigBill = populateConfigOnlineBillVO(lObjBillReg.getSubjectId(), srvcLoc.getSessionFactory());
					ResultObject lRsBillResult = srvcLoc.executeService(lObjConfigBill.getVogen(), lMapInput);
					Map lMapBill = (Map) lRsBillResult.getResultValue();
					lMapInput.put("BillMap", lMapBill);
				}
			} else if (lStrBillNo != null && lStrBillNo.length() > 0) {
				TrnBillRegister lObjBillReg = generateBillRegVO(lMapInput);
				lMapInput.put("BillRegVO", lObjBillReg);
			}
			objRes.setResultCode(ErrorConstants.SUCCESS);
			objRes.setResultValue(lMapInput);

		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, objRes, e, "Error is : ");
		}
		return objRes;
	}

	/**
	 * Method for setting the Global Variables using Session Helper
	 * 
	 * @param Map
	 *            inputMap
	 * @throws Exception
	 */
	private void setSessionInfo(Map inputMap) throws Exception {

		try {
			gLngLangId = SessionHelper.getLangId(inputMap);
			gLngPostId = SessionHelper.getPostId(inputMap);
			gLngUserId = SessionHelper.getUserId(inputMap);
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
			gLngDBId = SessionHelper.getDbId(inputMap);
			gDtCurrDt = DBUtility.getCurrentDateFromDB();
			gFinYrId = SessionHelper.getFinYrId(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw (e);
		}
	}

	/**
	 * Method for populating TrnBillRegister VO.
	 * 
	 * @param Map
	 *            lMapInput
	 * @return TrnBillRegister.
	 */
	private TrnBillRegister generateBillRegVO(Map lMapInput) throws Exception {

		TrnBillRegister lObjBillRegister = null;
		HttpServletRequest request = (HttpServletRequest) lMapInput.get("requestObj");
		request.getSession();
		ServiceLocator srvcLoc = (ServiceLocator) lMapInput.get("serviceLocator");

		BigDecimal lBDExpAmt = BigDecimal.ZERO;
		BigDecimal lBDRecAmt = BigDecimal.ZERO;
		String lStrBillNo = StringUtility.getParameter("hidBillNo", request);
		String lStrSubjectId = StringUtility.getParameter("hidBillTypeId", request);
		String lStrBillCntrlNo = StringUtility.getParameter("hidBillCntrlNo", request);
		String lStrExpAmt = StringUtility.getParameter("txtExpenditure", request);
		String lStrRecAmt = StringUtility.getParameter("txtRecovery", request);
		String lStrAction = StringUtility.getParameter("userAction", request);
		SimpleDateFormat lSdf = new SimpleDateFormat("dd/MM/yyyy");
		BptmCommonServicesDAOImpl lObjPhyBillDAOImpl = new BptmCommonServicesDAOImpl(TrnBillRegister.class, srvcLoc.getSessionFactory());
		String lStrForMonth = StringUtility.getParameter("hidBillForMnth", request).trim();
		try {
			/**
			 * If Bill No Found - Then Upadte Bill Else New Bill Save
			 */
			if (lStrBillNo != null && !"".equalsIgnoreCase(lStrBillNo)) {
				lObjBillRegister = (TrnBillRegister) lObjPhyBillDAOImpl.read(Long.parseLong(lStrBillNo.trim()));

				lObjBillRegister.setUpdatedDate(gDtCurrDt);
				lObjBillRegister.setUpdatedPostId(gLngPostId);
				lObjBillRegister.setUpdatedUserId(gLngUserId);
			} else {
				lObjBillRegister = new TrnBillRegister();
				lObjBillRegister.setCreatedDate(gDtCurrDt);
				lObjBillRegister.setCreatedUserId(gLngUserId);
				lObjBillRegister.setCreatedPostId(gLngPostId);
				lObjBillRegister.setAuditStatus(DBConstants.BILL_AUD_FOR);
				lObjBillRegister.setTrnCounter(1);
				lObjBillRegister.setIsAudit(Short.parseShort("0"));
				lObjBillRegister.setBillDate(lSdf.parse(lSdf.format(gDtCurrDt)));
			}
			if (!"44".equals(lStrSubjectId)) {
				lObjBillRegister.setBillCntrlNo(lStrBillCntrlNo.trim());

				if (lStrSubjectId != null && !"".equalsIgnoreCase(lStrSubjectId)) {
					lObjBillRegister.setSubjectId(Long.parseLong(lStrSubjectId.trim()));
				}
				lObjBillRegister.setPhyBill(Short.parseShort(gObjRsrcBndle.getString("CMN.OnlineBillType")));
				lObjBillRegister.setDemandCode(StringUtility.getParameter("txtDmd", request));
				lObjBillRegister.setBudmjrHd(StringUtility.getParameter("txtMjrHd", request));
				if (lStrExpAmt != null && !"".equalsIgnoreCase(lStrExpAmt)) {
					lBDExpAmt = new BigDecimal(lStrExpAmt.trim());
				}
				if (lStrRecAmt != null && !"".equalsIgnoreCase(lStrRecAmt)) {
					lBDRecAmt = new BigDecimal(lStrRecAmt.trim());
				}
				lObjBillRegister.setBillGrossAmount(lBDExpAmt);
				lObjBillRegister.setBillNetAmount(lBDExpAmt.subtract(lBDRecAmt));
				lObjBillRegister.setExempted("Y");
				lObjBillRegister.setCurrBillStatusDate(gDtCurrDt);
				if (lStrAction != null && !"".equalsIgnoreCase(lStrAction) && lStrAction.trim().equalsIgnoreCase(gObjRsrcBndle.getString("CONST.Approve"))) {
					lObjBillRegister.setCurrBillStatus(DBConstants.ST_BAPRVD_DDO);
				} else if (lObjBillRegister.getCurrBillStatus() == null) {
					lObjBillRegister.setCurrBillStatus(DBConstants.ST_BCRTD);
				}
				lObjBillRegister.setFinYearId(gFinYrId.toString());
				lObjBillRegister.setLocationCode(gStrLocationCode);
				lObjBillRegister.setDbId(gLngDBId);
				lObjBillRegister.setSchemeNo(StringUtility.getParameter("txtSchemeNo", request).trim());
				lObjBillRegister.setDemandCode(StringUtility.getParameter("txtDmd", request).trim());
				lObjBillRegister.setBudmjrHd(StringUtility.getParameter("txtMjrHd", request).trim());
				lObjBillRegister.setBudSubmjrHd(StringUtility.getParameter("txtSbMjrHd", request).trim());
				lObjBillRegister.setBudMinHd(StringUtility.getParameter("txtMnrHd", request).trim());
				lObjBillRegister.setBudSubHd(StringUtility.getParameter("txtSbHd", request).trim());
				lObjBillRegister.setBudDtlHd(StringUtility.getParameter("txtDtldHd", request).trim());
				lObjBillRegister.setLocationCode(gStrLocationCode);
				if ("9".equals(lStrSubjectId) && lStrForMonth.length() > 0) {
					lObjBillRegister.setForMonth(Integer.valueOf(lStrForMonth));
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw (e);
		}
		return lObjBillRegister;
	}

	/**
	 * Method for TrnBillMvmnt VO
	 * 
	 * @param Map
	 *            lMapInput
	 * @return TrnBillMvmnt.
	 */
	private TrnBillMvmnt generateBillMvmntVO(Map lMapInput) throws Exception {

		TrnBillMvmnt lObjBillMvmnt = new TrnBillMvmnt();

		try {
			HttpServletRequest request = (HttpServletRequest) lMapInput.get("requestObj");
			ServiceLocator srvcLoc = (ServiceLocator) lMapInput.get("serviceLocator");

			String lStrBillMvmntId = StringUtility.getParameter("hidBillMvmntId", request);
			String lStrRole = StringUtility.getParameter("currRole", request);
			if (lStrBillMvmntId != null && !"".equalsIgnoreCase(lStrBillMvmntId)) {
				BillMovementDAOImpl lObjMvmntDAO = new BillMovementDAOImpl(TrnBillMvmnt.class, srvcLoc.getSessionFactory());

				lObjBillMvmnt = lObjMvmntDAO.read(Long.parseLong(lStrBillMvmntId.trim()));
				lObjBillMvmnt.setUpdatedDate(gDtCurrDt);
				lObjBillMvmnt.setUpdatedPostId(gLngPostId);
				lObjBillMvmnt.setUpdatedUserId(gLngUserId);
			} else {
				lObjBillMvmnt.setCreatedDate(gDtCurrDt);
				lObjBillMvmnt.setCreatedPostId(gLngPostId);
				lObjBillMvmnt.setCreatedUserId(gLngUserId);
				lObjBillMvmnt.setMovemntId(1L);
			}
			if (!"".equals(lStrRole) && lStrRole.trim().length() > 0) {
				lObjBillMvmnt.setRoleId(Long.valueOf(lStrRole.trim()));
			}

			lObjBillMvmnt.setBillRemarks(StringUtility.getParameter("txtareaRemarks", request).trim());
			// lObjBillMvmnt.setObjRemarks(StringUtility.getParameter("objRemarks",
			// request).trim());
			lObjBillMvmnt.setReceivingUserId(gLngUserId);
			lObjBillMvmnt.setReceivedDate(gDtCurrDt);
			lObjBillMvmnt.setStatusUpdtDate(gDtCurrDt);
			lObjBillMvmnt.setStatusUpdtPostid(gLngPostId); // next
			lObjBillMvmnt.setStatusUpdtUserid(gLngUserId);

			lObjBillMvmnt.setMvmntStatus(DBConstants.ST_BCRTD);

			lObjBillMvmnt.setDbId(gLngDBId);
			lObjBillMvmnt.setLocationCode(gStrLocationCode);
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw (e);
		}
		return lObjBillMvmnt;
	}

	/**
	 * Method for generating Bill Party VOs.
	 * 
	 * @param Map
	 *            lMapInput
	 * @return List
	 */
	public List<RltBillParty> generateBillPartyVO(Map lMapInput) throws Exception {

		List<RltBillParty> lLstBillParty = new ArrayList<RltBillParty>();
		HttpServletRequest request = (HttpServletRequest) lMapInput.get("requestObj");

		RltBillParty lObjBillParty = null;

		try {
			String lStrPaymentMode = StringUtility.getParameter("paymentMode", request);

			String[] lStrPartyName = StringUtility.getParameterValues("txtPartyName", request);
			String[] lStrPartyBankCode = StringUtility.getParameterValues("cmbBankCode", request);
			String[] lStrPartyAddr = StringUtility.getParameterValues("txtAddress", request);
			String[] lStrPartyAcNo = StringUtility.getParameterValues("txtAccountNo", request);
			String[] lStrPartyMICRCode = StringUtility.getParameterValues("txtMicrCode", request);
			String[] lStrPartyChkAmt = StringUtility.getParameterValues("txtChkAmt", request);
			String[] lStrPartyBranchCode = StringUtility.getParameterValues("txtBranchCode", request);
			// String[] lStrDDOParty =
			// StringUtility.getParameterValues("cmbDdoParty", request);
			String[] lStrBankName = StringUtility.getParameterValues("hidPartyBankName", request);

			// lStrPnsnrName = ((StringUtility.getParameter("txtPnsnrName",
			// request).trim()).length() > 0) ?
			// StringUtility.getParameter("txtPnsnrName", request).trim() :
			// null;

			if (lStrPartyName != null && lStrPartyName.length > 0) {
				for (int lIntCnt = 0; lIntCnt < lStrPartyName.length; lIntCnt++) {
					lObjBillParty = new RltBillParty();

					if (lStrPartyAddr[lIntCnt] != null) {
						lObjBillParty.setPartyAddr(lStrPartyAddr[lIntCnt].trim());
					}

					if (lStrPartyBankCode != null && lStrPartyBankCode.length > 0 && lStrPartyBankCode[lIntCnt] != null && !"-1".equalsIgnoreCase(lStrPartyBankCode[lIntCnt])) {
						lObjBillParty.setBankCode(lStrPartyBankCode[lIntCnt]);
					}

					if (lStrPartyAcNo[lIntCnt] != null) {
						lObjBillParty.setAccntNo(lStrPartyAcNo[lIntCnt].trim());
					}

					if (lStrPartyName[lIntCnt] != null && !"".equalsIgnoreCase(lStrPartyName[lIntCnt])) {
						lObjBillParty.setPartyName(lStrPartyName[lIntCnt].trim().toUpperCase());
					}
					if (lStrBankName != null && lStrBankName.length > 0 && lStrBankName[lIntCnt] != null && !"".equals(lStrBankName[lIntCnt]) && !"-1".equalsIgnoreCase(lStrBankName[lIntCnt])) {
						lObjBillParty.setPartyName(lStrBankName[lIntCnt].trim().toUpperCase());
					}

					// Added By Sagar
					if (lStrPartyMICRCode != null && lStrPartyMICRCode.length > 0 && lStrPartyMICRCode[lIntCnt] != null && lStrPartyMICRCode[lIntCnt].trim().length() > 0) {
						lObjBillParty.setMicrCode(Long.valueOf(lStrPartyMICRCode[lIntCnt].trim()));
					}

					if (lStrPartyChkAmt[lIntCnt] != null && lStrPartyChkAmt[lIntCnt].trim().length() > 0) {
						lObjBillParty.setPartyAmt(new BigDecimal(lStrPartyChkAmt[lIntCnt].trim()));
					} else {
						lObjBillParty.setPartyAmt(BigDecimal.valueOf(0));
					}

					if (lStrPaymentMode != null && lStrPaymentMode.trim().length() > 0) {
						lObjBillParty.setPaymentMode(lStrPaymentMode);
					}
					if (lStrPartyBranchCode != null && lStrPartyBranchCode.length > 0 && lStrPartyBranchCode[lIntCnt] != null && !"-1".equalsIgnoreCase(lStrPartyBranchCode[lIntCnt])) {
						// if (lStrPartyBranchCode[lIntCnt] != null &&
						// lStrPartyBranchCode.length > 0) {
						lObjBillParty.setBranchCode(lStrPartyBranchCode[lIntCnt]);
					}

					lObjBillParty.setCreatedDate(gDtCurrDt);
					lObjBillParty.setCreatedPostId(gLngPostId);
					lObjBillParty.setCreatedUserId(gLngUserId);

					lObjBillParty.setLocationCode(gStrLocationCode);
					lObjBillParty.setDbId(gLngDBId);

					lLstBillParty.add(lObjBillParty);
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstBillParty;
	}

	private ConfigOnlineBill populateConfigOnlineBillVO(Long lLngSubjectId, SessionFactory lObjSessionFactory) {

		Session lObjSession = lObjSessionFactory.getCurrentSession();
		List<ConfigOnlineBill> lLstData = null;
		Criteria lObjCriteria = null;
		ConfigOnlineBill lObjConfigOnlineBill = null;
		try {
			lObjCriteria = lObjSession.createCriteria(ConfigOnlineBill.class);
			lObjCriteria.add(Restrictions.eq("subjectId", lLngSubjectId));

			// lObjCriteria.setCacheable(true).setCacheRegion("ecache_lookup");
			lLstData = lObjCriteria.list();

			if (lLstData != null && !lLstData.isEmpty() && lLstData.get(0) != null) {
				lObjConfigOnlineBill = lLstData.get(0);
			}
		} catch (Exception e) {
			gLogger.error("Error in populateConfigOnlineBillVO. Error is : " + e, e);
		}
		return lObjConfigOnlineBill;
	}

	// New for Supplymentary bill

}