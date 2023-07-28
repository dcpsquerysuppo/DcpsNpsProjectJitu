package com.tcs.sgv.pensionpay.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.dao.CommonPensionDAO;
import com.tcs.sgv.pensionpay.dao.CommonPensionDAOImpl;
import com.tcs.sgv.pensionpay.dao.MonthlyPensionBillDAO;
import com.tcs.sgv.pensionpay.dao.MonthlyPensionBillDAOImpl;
import com.tcs.sgv.pensionpay.dao.MstPensionerHdrDAO;
import com.tcs.sgv.pensionpay.dao.MstPensionerHdrDAOImpl;
import com.tcs.sgv.pensionpay.dao.NewPensionBillDAO;
import com.tcs.sgv.pensionpay.dao.NewPensionBillDAOImpl;
import com.tcs.sgv.pensionpay.dao.PensionBillDAO;
import com.tcs.sgv.pensionpay.dao.PensionBillDAOImpl;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAOImpl;
import com.tcs.sgv.pensionpay.dao.TrnPensionBillDtlsDAO;
import com.tcs.sgv.pensionpay.dao.TrnPensionBillDtlsDAOImpl;
import com.tcs.sgv.pensionpay.dao.TrnPensionBillHdrDAO;
import com.tcs.sgv.pensionpay.dao.TrnPensionBillHdrDAOImpl;
import com.tcs.sgv.pensionpay.valueobject.MonthlyPensionBillVO;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerDtls;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerFamilyDtls;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerHdr;
import com.tcs.sgv.pensionpay.valueobject.RltPensionHeadcodeRate;
import com.tcs.sgv.pensionpay.valueobject.TrnCvpRestorationDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnMonthlyPensionRecoveryDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionArrearDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionCutDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRecoveryDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;
import com.tcs.sgv.pensionpay.valueobject.ValidPcodeView;


/**
 * Pension Bill Specific Srvice Impl.
 * 
 * @author
 * @version 1.0
 */
public class NewPensionBillServiceImpl extends ServiceImpl implements NewPensionBillService {

	/* Global Variable for PostId */
	private Long gLngPostId = null;

	/* Global Variable for UserId */
	private Long gLngUserId = null;

	/* Glonal Variable for Location Code */
	private String gStrLocCode = null;
	private Long glLocId = null;

	/* Global Variable for DB Id */
	private Long gLngDBId = null;

	/* Global Variable for LangId */
	private Long gLngLangId = null;

	/* Global Variable for EmpId */
	private Long gLngEmpId = null;

	/* Global Variable for Logger Class */
	private final Log gLogger = LogFactory.getLog(getClass());

	/* Global Variable for Current Date */
	private Date gDate = null;

	private ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionConstants");

	private ResourceBundle bundleCaseConst = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	/**
	 * Get Pension Bill Data From Pension Case
	 * 
	 * @param inputMap
	 * @return objRes ResultObject
	 */
	public ResultObject getNewPensionBillData(Map<String, Object> inputMap) {

		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		TrnPensionRqstHdr lObjTrnPensionRqstHdrVO = null;
		MstPensionerHdr lObjMstPensionerHdrVO = null;
		ValidPcodeView lObjValidPcodeVO = null;

		String lStrStatus = bundleConst.getString("STATUS.CONTINUE");
		String lStrPensionFlag = bundleConst.getString("RECOVERY.PENSION");
		String lStrPnsrBasic = bundleConst.getString("NETPNSN.BASIC");
		String lStrCaseStatus = null;
		// lStrCaseStatus = bundleConst.getString("STATUS.APPROVED");

		String lStrPPONo = null;
		String lStrPnsrCode = null;
		String lStrPnsnerName = null;
		String lStrBasicPnsnFlage = null;
		Double lBasicPensionAmt = 0D;
		long lNetPensionAmt = 0;
		Double lDPPerAmt = 0D;
		Double lTIPerAmt = 0D;
		Double lIRAmt = 0D;
		Double lCvpMonthlyAmt = 0D;
		Double lADPAmt = 0d;

		Double lPnsnRecoveryAmt = 0D;
		Double lCrntMonthRecoveryAmt = 0D;
		Double lTotalCrntRecoveryAmt = 0D;
		Date lDtPpoEnd = null;
		Date lDtCommencement = null;
		Integer lCurrentyyyyMM = 0;
		Date lCurrentDate = null;
		Integer lIntEndDate = 0;
		Integer lIntCommDate = 0;
		SimpleDateFormat lSdf = new SimpleDateFormat("yyyyMM");
		String lStrPpoEndFlag = "N";
		Date lDeathDate = null;
		MstPensionerFamilyDtls lMstPensionerFamilyDtlsVO = null;
		String lStrPnsrName = "";
		MstPensionerDtls lObjMstPensionerDtls = null;
		PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = null;
		// String lStrCalcType = null;

		try {
			setSessionInfo(inputMap);

			lStrPPONo = inputMap.get("PPONo").toString();
			inputMap.put("BillType", lStrPensionFlag);
			lStrBasicPnsnFlage = lStrPnsrBasic;

			CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(srvcLoc.getSessionFactory());
			lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(MstPensionerDtls.class, srvcLoc.getSessionFactory());
			new NewPensionBillDAOImpl(srvcLoc.getSessionFactory());
			MstPensionerHdrDAO lObjMstPensionerHdrDAO = new MstPensionerHdrDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());
			new PensionBillProcessServiceImpl();
			MonthlyPensionBillDAO lObjMonthlyPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());
			MonthlyPensionBillVO lMonthlyPensionBillVO = null;
			new ArrayList<TrnPensionArrearDtls>();

			if (lStrPPONo != null && lStrPPONo.length() > 0) {
				// Getting the ObjectVo of TrnPensionRqstHdr
				lObjTrnPensionRqstHdrVO = lObjCommonPensionDAO.getTrnPensionRqstHdrDtls(lStrPPONo, lStrStatus, gStrLocCode);

				if (lObjTrnPensionRqstHdrVO != null) {
					inputMap.put("TrnPensionRqstHdrVO", lObjTrnPensionRqstHdrVO);
					inputMap.put("TrnPensionRqstID", lObjTrnPensionRqstHdrVO.getPensionRequestId().toString());
					lStrPnsrCode = lObjTrnPensionRqstHdrVO.getPensionerCode();
					// Getting the ObjectVo of MstPensionerHdr... Start...
					lObjMstPensionerHdrVO = lObjMstPensionerHdrDAO.getMstPnsnrHdrWithStatus(lStrPnsrCode, lStrCaseStatus);
					inputMap.put("MstPensionerHdrVO", lObjMstPensionerHdrVO);
					lObjMstPensionerDtls = lObjPhysicalCaseInwardDAO.getMstPensionerDtlsVO(lStrPnsrCode);
					inputMap.put("MstPensionerDtlsVO", lObjMstPensionerDtls);

					// Set VO of View.
					lObjValidPcodeVO = setViewVo(inputMap);
					inputMap.put("lObjValidPcode", lObjValidPcodeVO);

					/** Get Basic Amount for Pensioner **/

					// ------Check for ppo end date and commencement starts
					// <<<<<<<<
					// ------If ppo end date is null or ppo end date is in or
					// after current month then only calculate monthly pension
					// -------If commencement month-year is after current
					// month-year
					// than not calculating pension.
					SimpleDateFormat lObjSmplDateFrm = new SimpleDateFormat("dd/MM/yyyy");
					if (inputMap.get("Date") != null) {
						lCurrentyyyyMM = Integer.valueOf(inputMap.get("Date").toString());
						lCurrentDate = lObjSmplDateFrm.parse("01/" + (lCurrentyyyyMM.toString()).substring(4, 6) + "/" + (lCurrentyyyyMM.toString()).substring(0, 4));
					} else {
						// --For first pension bill, checking for which month
						// bill is to be generated.lStrGenPnsnBillForFlag is set
						// in pensionBillPopup.jsp
						Calendar c = Calendar.getInstance();
						String lStrGenPnsnBillForFlag = (String) inputMap.get("lStrGenPnsnBillForFlag");
						if (lStrGenPnsnBillForFlag != null && "prevMonth".equals(lStrGenPnsnBillForFlag)) {
							/*
							 * int currMonth = c.get(Calendar.MONTH); int
							 * prevMonth = 0; if (currMonth == 0) { prevMonth =
							 * 11; } else { prevMonth = currMonth - 1; }
							 * c.set(Calendar.MONTH, prevMonth);
							 */
							c.add(Calendar.MONTH, -1);
						}
						lCurrentDate = c.getTime();
						lCurrentyyyyMM = Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lCurrentDate));
						inputMap.put("Date", lCurrentyyyyMM);
					}
					lDtPpoEnd = lObjValidPcodeVO.getEndDate();
					lDtCommencement = lObjValidPcodeVO.getCommensionDate();
					if (lDtCommencement != null) {
						lIntCommDate = Integer.parseInt(lSdf.format(lDtCommencement));
						if (lIntCommDate > lCurrentyyyyMM) {
							lStrPpoEndFlag = "Y";
						}
					}

					if (lDtPpoEnd != null) {
						lIntEndDate = Integer.parseInt(lSdf.format(lDtPpoEnd));
						if ((lIntEndDate < lCurrentyyyyMM)) {
							lStrPpoEndFlag = "Y";
						}
					}

					// ------Check for ppo end date ends >>>>>>>>>>>

					if (("N".equals(lStrPpoEndFlag)) && (lStrStatus.equals(lObjValidPcodeVO.getStatus()))) {
						getMonthlyPensionBillVO(inputMap);
						lMonthlyPensionBillVO = (MonthlyPensionBillVO) inputMap.get("lMonthlyPensionBillVO");
					} else {
						// --If ppo end date is already gone or commencement
						// month-year is after current month-year then
						// generating
						// bill with zero amounts only.and not calculating any
						// pension component.
						lMonthlyPensionBillVO = new MonthlyPensionBillVO();
						lDeathDate = lObjValidPcodeVO.getDateOfDeath();
						if (lDeathDate != null) {

							lMstPensionerFamilyDtlsVO = lObjMonthlyPensionBillDAO.getMstPensionerFamilyDtls(lObjValidPcodeVO.getPensionerCode());
							// lMstPensionerFamilyDtlsVO =
							// (MstPensionerFamilyDtls)
							// inputMap.get("lMstPensionerFamilyDtlsVO");
							if (lMstPensionerFamilyDtlsVO != null) {
								lStrPnsrName = lMstPensionerFamilyDtlsVO.getName();
							} else {
								lStrPnsrName = ((lObjValidPcodeVO.getFirstName() != null) ? lObjValidPcodeVO.getFirstName() : "") + " ";
								// lStrPnsrName +=
								// ((lObjValidPcodeVO.getMiddleName() != null) ?
								// lObjValidPcodeVO.getMiddleName() : "") + " ";
								// lStrPnsrName +=
								// (lObjValidPcodeVO.getLastName() != null) ?
								// lObjValidPcodeVO.getLastName() : "";
							}
						} else {
							lStrPnsrName = ((lObjValidPcodeVO.getFirstName() != null) ? lObjValidPcodeVO.getFirstName() : "") + " ";
							// lStrPnsrName +=
							// ((lObjValidPcodeVO.getMiddleName() != null) ?
							// lObjValidPcodeVO.getMiddleName() : "") + " ";
							// lStrPnsrName += (lObjValidPcodeVO.getLastName()
							// != null) ? lObjValidPcodeVO.getLastName() : "";
						}

						lMonthlyPensionBillVO.setPpoNo(lObjValidPcodeVO.getPpoNo());
						lMonthlyPensionBillVO.setPensionerCode(lObjValidPcodeVO.getPensionerCode());
						lMonthlyPensionBillVO.setPensionerName(lStrPnsrName);
						inputMap.put("lMonthlyPensionBillVO", lMonthlyPensionBillVO);
						inputMap.put("PnsrCrntDate", gDate);
						inputMap.put("CrntMonth", lCurrentyyyyMM);
					}

					lStrPnsnerName = (lObjMstPensionerHdrVO.getFirstName() != null) ? lObjMstPensionerHdrVO.getFirstName() + " " : "";
					/*
					 * lStrLastName = (lObjMstPensionerHdrVO.getLastName() !=
					 * null) ? lObjMstPensionerHdrVO.getLastName() + " " : "";
					 * lStrMiddleName = (lObjMstPensionerHdrVO.getMiddleName()
					 * != null) ? lObjMstPensionerHdrVO.getMiddleName() + " " :
					 * ""; lStrPnsnerName = lStrFirstName + lStrMiddleName +
					 * lStrLastName;
					 */

				}
			}
			/*
			 * Date lseenDate = new
			 * NewPensionBillDAOImpl(srvcLoc.getSessionFactory
			 * ()).getPensionerSeenDate(lStrPnsrCode, "Y");
			 * inputMap.put("PPOSeenDate", lseenDate);
			 */

			lTotalCrntRecoveryAmt = lPnsnRecoveryAmt + lCrntMonthRecoveryAmt;

			lNetPensionAmt = Math.round(lBasicPensionAmt);
			lNetPensionAmt = lNetPensionAmt + Math.round(lDPPerAmt) + Math.round(lADPAmt) + Math.round(lTIPerAmt);
			lNetPensionAmt = lNetPensionAmt - Math.round(lCvpMonthlyAmt);
			lNetPensionAmt = lNetPensionAmt + Math.round(lIRAmt);
			lNetPensionAmt = (lNetPensionAmt - Math.round(lTotalCrntRecoveryAmt));

			inputMap.put("AliveFlg", lObjMstPensionerHdrVO.getAliveFlag());
			inputMap.put("PensionerCode", lObjValidPcodeVO.getPensionerCode());
			inputMap.put("PnsnerName", lStrPnsnerName);
			inputMap.put("BasicPnsnFlage", lStrBasicPnsnFlage);
			inputMap.put("BasicPensionAmt", lMonthlyPensionBillVO.getBasicPensionAmount());
			inputMap.put("DPPercentAmt", lMonthlyPensionBillVO.getDpPercent());
			inputMap.put("TIPercentAmt", lMonthlyPensionBillVO.getTiPercent());
			inputMap.put("CvpMonthlyAmt", lMonthlyPensionBillVO.getCvpMonthlyAmount());
			inputMap.put("IRAmt", lMonthlyPensionBillVO.getIr1Amt() + lMonthlyPensionBillVO.getIr2Amt() + lMonthlyPensionBillVO.getIr3Amt());
			inputMap.put("RecoveryAmt", lMonthlyPensionBillVO.getRecoveryAmount());
			inputMap.put("PensionBillAmt", lMonthlyPensionBillVO.getNetPensionAmount());
			inputMap.put("lNetPensionAmt", lMonthlyPensionBillVO.getNetPensionAmount());
			inputMap.put("TotalArrearRecovery", lMonthlyPensionBillVO.getRecoveryAmount());

			inputMap.put("Headcode", lObjValidPcodeVO.getHeadCode());
			inputMap.put("Scheme", lObjValidPcodeVO.getSchemeType());
			inputMap.put("PPONo", lStrPPONo);
			inputMap.put("Name", lStrPnsnerName);

			inputMap.put("PnsnrCode", lStrPnsrCode);

			inputMap.put("ADPAmt", Math.round(lADPAmt));
			inputMap.put("bankCode", lObjValidPcodeVO.getBankCode());
			inputMap.put("branchCode", lObjValidPcodeVO.getBranchCode());

			/*
			 * String lStrPrintPensionBill =
			 * getPrintPensionBillString(inputMap);
			 * inputMap.put("BillArrPrintString", lStrPrintPensionBill);
			 */

			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is : ", e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	/*
	 * private String checkFromToMoth(TrnPensionItCutDtls lObjPensionItCutDtls)
	 * { Integer lFromYYYYMM = 0; Integer lToYYYYMM = 0;
	 * 
	 * Integer lCurYYYYMM = Integer.parseInt(new
	 * SimpleDateFormat("yyyyMM").format(gDate));
	 * 
	 * lFromYYYYMM = lObjPensionItCutDtls.getFromMonth(); lToYYYYMM =
	 * lObjPensionItCutDtls.getToMonth();
	 * 
	 * if( lCurYYYYMM >= lFromYYYYMM && lCurYYYYMM <= lToYYYYMM ) { return
	 * "true"; }
	 * 
	 * return "false"; }
	 */

	/**
	 * Save Pension Bill Dtls
	 * 
	 * @param lMapInput
	 * @return
	 */
	public ResultObject saveNewPensionBillDtls(Map<String, Object> lMapInput) {

		ServiceLocator srvcLoc = (ServiceLocator) lMapInput.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) lMapInput.get("requestObj");
		ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionConstants");

		TrnPensionBillHdrDAO lObjTrnPensionBillHdrDAO = new TrnPensionBillHdrDAOImpl(TrnPensionBillHdr.class, srvcLoc.getSessionFactory());
		TrnPensionBillHdr lObjTrnPensionBillHdr = null;

		TrnPensionBillDtlsDAO lObjTrnPensionBillDtlsDAO = new TrnPensionBillDtlsDAOImpl(TrnPensionBillDtls.class, srvcLoc.getSessionFactory());
		TrnPensionBillDtls lObjTrnPensionBillDtls = null;

		String lStrPension = bundleConst.getString("RECOVERY.PENSION");
		Long lOBPMBillNo = Long.parseLong(lMapInput.get("billNo").toString());

		SimpleDateFormat lObjSmplDateFrm = new SimpleDateFormat("dd/MM/yyyy");
		String lStrPnsrCode = null;
		TrnPensionRqstHdr lObjTrnPensionRqstHdr = null;
		String lStrStatus = bundleConst.getString("STATUS.CONTINUE");
		Integer lIntForMonth = null;
		PensionBillDAO lObjPensionBillDAO = null;
		List<String> lLstPensionerCode = new ArrayList<String>();
		MonthlyPensionBillDAO lObjMonthlyPensionBillDAO = null;
		Map<String, List<TrnPensionRecoveryDtls>> lMapRcvryDtl = new HashMap<String, List<TrnPensionRecoveryDtls>>();
		List<TrnPensionRecoveryDtls> lLstTrnPensionRecoveryDtlsVO = null;
		TrnMonthlyPensionRecoveryDtls lObjTrnMonthlyPensionRecoveryDtls = null;
		HibernateTemplate lHibTemplate = null;
		List<TrnMonthlyPensionRecoveryDtls> lLstTrnMonthlyPensionRecoveryDtls = null;
		try {
			setSessionInfo(lMapInput);
			lObjPensionBillDAO = new PensionBillDAOImpl(srvcLoc.getSessionFactory());
			lObjMonthlyPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());
			String hidFromPayDate = StringUtility.getParameter("hidFromPayDate", request).trim();
			Date lFromPayDate = lObjSmplDateFrm.parse(hidFromPayDate);

			String hidUptoBillCrtDate = StringUtility.getParameter("hidUptoBillCrtDate", request).trim();
			Date lLstPaidDate = lObjSmplDateFrm.parse(hidUptoBillCrtDate);
			// String lLstPaidAmt =
			// StringUtility.getParameter("txtNetPensionAmt", request).trim();

			// ---- 1 ----- insert Date TrnPensionBillHdr Table.

			lObjTrnPensionBillHdr = (TrnPensionBillHdr) lMapInput.get("TrnPensionBillHdrVO");
			lIntForMonth = lObjTrnPensionBillHdr.getForMonth();
			lObjTrnPensionBillHdr.setBillNo(lOBPMBillNo);
			lObjTrnPensionBillHdr.setBillType(lStrPension);
			lObjTrnPensionBillHdr.setTrnPensionBillHdrId(IFMSCommonServiceImpl.getNextSeqNum("trn_pension_bill_hdr", lMapInput));
			lObjTrnPensionBillHdrDAO.create(lObjTrnPensionBillHdr);

			// --- 2 --- insert Date TrnPensionBillDtls Table.
			NewPensionBillDAO lObjNewPensionBillDAO = new NewPensionBillDAOImpl(srvcLoc.getSessionFactory());
			lObjTrnPensionBillDtls = (TrnPensionBillDtls) lMapInput.get("TrnPensionBillDtlsVO");
			lStrPnsrCode = lObjTrnPensionBillDtls.getPensionerCode();
			if (lStrPnsrCode != null && lStrPnsrCode.length() > 0) {
				// Getting the ObjectVo of TrnPensionRqstHdr... Start...
				lObjTrnPensionRqstHdr = lObjNewPensionBillDAO.getPensionRqstHdrDtls(lStrPnsrCode, lStrStatus, gStrLocCode);
				// lObjTrnPensionBillDtls.setAllcationBf1436(lObjTrnPensionRqstHdr.getOrgBf11136());
				// lObjTrnPensionBillDtls.setAllcationBf11156(lObjTrnPensionRqstHdr.getOrgAf11136());
				// lObjTrnPensionBillDtls.setAllcationAf11156(lObjTrnPensionRqstHdr.getOrgAf11156());
				// lObjTrnPensionBillDtls.setAllcationAf10560(lObjTrnPensionRqstHdr.getOrgAf10560());
				// lObjTrnPensionBillDtls.setAllcationAfZp(lObjTrnPensionRqstHdr.getOrgAfZp());
			}
			lObjTrnPensionBillDtls.setTrnPensionBillHdrId(lObjTrnPensionBillHdr.getTrnPensionBillHdrId());
			lObjTrnPensionBillDtls.setFromDate(lFromPayDate);
			lObjTrnPensionBillDtls.setToDate(lLstPaidDate);

			lObjTrnPensionBillDtls.setTrnPensionBillDtlsId(IFMSCommonServiceImpl.getNextSeqNum("trn_pension_bill_dtls", lMapInput));

			lObjTrnPensionBillDtlsDAO.create(lObjTrnPensionBillDtls);
			/*
			 * --Setting bill number in arrear details of pensioner code in
			 * bill.
			 */
			if (lIntForMonth != null && lStrPnsrCode != null) {
				lLstPensionerCode.add(lStrPnsrCode);
				lObjPensionBillDAO.updateBillNoInArrearDtls(lLstPensionerCode, lOBPMBillNo, lIntForMonth, gLngUserId, gLngPostId, gDate);

				/*
				 * --Insertion into Trn_Monthly_Pension_Recovery_Dtls for
				 * maintaining recovery history starts <<<
				 */
				lMapRcvryDtl = lObjMonthlyPensionBillDAO.getRecoveryDtlsForChngStmnt(lLstPensionerCode, lIntForMonth.toString());
				if (lMapRcvryDtl != null) {
					lLstTrnPensionRecoveryDtlsVO = lMapRcvryDtl.get(lStrPnsrCode);
					if (lLstTrnPensionRecoveryDtlsVO != null) {
						Long lLngPkRcvryDtls = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("trn_monthly_pension_recovery_dtls", lMapInput, lLstTrnPensionRecoveryDtlsVO.size());
						lHibTemplate = new HibernateTemplate(srvcLoc.getSessionFactory());
						lLstTrnMonthlyPensionRecoveryDtls = new ArrayList<TrnMonthlyPensionRecoveryDtls>();
						for (TrnPensionRecoveryDtls lObjTrnPensionRecoveryDtls : lLstTrnPensionRecoveryDtlsVO) {
							lObjTrnMonthlyPensionRecoveryDtls = new TrnMonthlyPensionRecoveryDtls();
							lObjTrnMonthlyPensionRecoveryDtls.setTrnMonthlyPensionRecoveryDtls(IFMSCommonServiceImpl.getFormattedPrimaryKey(++lLngPkRcvryDtls, lMapInput));
							lObjTrnMonthlyPensionRecoveryDtls.setPensionerCode(lStrPnsrCode);
							lObjTrnMonthlyPensionRecoveryDtls.setRecoveryFromFlag(bundleConst.getString("RECOVERY.PENSION"));
							lObjTrnMonthlyPensionRecoveryDtls.setRecoveryType(lObjTrnPensionRecoveryDtls.getRecoveryType());
							lObjTrnMonthlyPensionRecoveryDtls.setAccountNumber(lObjTrnPensionRecoveryDtls.getAccountNumber());
							lObjTrnMonthlyPensionRecoveryDtls.setAmount(lObjTrnPensionRecoveryDtls.getAmount());
							lObjTrnMonthlyPensionRecoveryDtls.setForMonth(lIntForMonth);
							lObjTrnMonthlyPensionRecoveryDtls.setBankCode(lObjTrnPensionBillHdr.getBankCode());
							lObjTrnMonthlyPensionRecoveryDtls.setBranchCode(lObjTrnPensionBillHdr.getBranchCode());
							lObjTrnMonthlyPensionRecoveryDtls.setTrnCounter(1);
							lObjTrnMonthlyPensionRecoveryDtls.setNoOfInstallments(lObjTrnPensionRecoveryDtls.getNoOfInstallments());
							lObjTrnMonthlyPensionRecoveryDtls.setNature(lObjTrnPensionRecoveryDtls.getNature());
							lObjTrnMonthlyPensionRecoveryDtls.setSchemeCode(lObjTrnPensionRecoveryDtls.getSchemeCode());
							lObjTrnMonthlyPensionRecoveryDtls.setPpoNo(lObjTrnPensionBillDtls.getPpoNo());
							lObjTrnMonthlyPensionRecoveryDtls.setLocationCode(Integer.valueOf(gStrLocCode));
							lObjTrnMonthlyPensionRecoveryDtls.setCreatedUserId(gLngUserId);
							lObjTrnMonthlyPensionRecoveryDtls.setCreatedPostId(gLngPostId);
							lObjTrnMonthlyPensionRecoveryDtls.setCreatedDate(gDate);
							lObjTrnMonthlyPensionRecoveryDtls.setDbId(gLngDBId.intValue());
							lObjTrnMonthlyPensionRecoveryDtls.setBillNo(lOBPMBillNo);
							lLstTrnMonthlyPensionRecoveryDtls.add(lObjTrnMonthlyPensionRecoveryDtls);
						}
						lHibTemplate.saveOrUpdateAll(lLstTrnMonthlyPensionRecoveryDtls);
					}
				}
				/*
				 * --Insertion into Trn_Monthly_Pension_Recovery_Dtls for
				 * maintaining recovery history ends >>>
				 */
			}
			resObj.setResultValue(lMapInput);
		} catch (Exception e) {
			gLogger.error("Error is : ", e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	/**
	 * Get Saved Pension Bill Data For View the Pension Bill.
	 * 
	 * @param inputMap
	 * @return objRes ResultObject
	 */
	public ResultObject getSavedNewPensionBillData(Map<String, Object> inputMap) {

		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionConstants");

		TrnPensionBillHdr lObjTrnPensionBillHdrVO = null;
		TrnPensionBillDtls lObjTrnPensionBillDtls = null;
		MstPensionerHdr lObjMstPensionerHdrVO = null;
		List<TrnPensionArrearDtls> lObjArrearDtlsVOLst = null;
		List<MonthlyPensionBillVO> lMonthlyArrearVoLst = null;
		List<TrnPensionBillDtls> lPnsnBillArrDtlsLst = null;
		MonthlyPensionBillVO lPensionBillVO = null;

		String lStrStatus = bundleConst.getString("STATUS.CONTINUE");
		String lStrPnsrBasic = bundleConst.getString("NETPNSN.BASIC");
		String lStrPnsrFP1 = bundleConst.getString("NETPNSN.FP1");
		String lStrPnsrFP2 = bundleConst.getString("NETPNSN.FP2");
		String lStrPPONo = null;
		Long lLngBillNo = null;
		String lStrBillType = null;
		String lStrPnsnerName = null;
		String lStrPnsrCode = null;
		String lStrBasicPnsnFlage = null;
		String lStrFirstName = null;
		String lStrMiddleName = "";
		String lStrLastName = null;
		String lStrTokenNo = null;
		String lStrArrearDtls = null;
		String lStrEffFromDate = "";
		String lStrEffToDate = "";
		Double lBasicPensionAmt = 0D;
		Double lPensionCutAmt = 0D;
		Double lNetPensionAmt = 0D;
		Double lDPPerAmt = 0D;
		Double lADPAmt = 0D;
		Double lTIPerAmt = 0D;
		Double lMAAmt = 0D;
		Double lPersonalPnsnAmt = 0D;
		Double lIRAmt = 0D;
		Double lITCutAmt = 0D;
		Double lSpecialCutAmt = 0D;
		Double lRecoveryAmt = 0D;
		Double lOthrArrAmt = 0D;
		Double lOMRAmt = 0D;
		Double lArrearAmt = 0D;
		Double lATIAmt = 0D;
		Double lOtherBenefitAmt = 0D;
		Double lPeonAllowance = 0D;
		Double lGallantryAmt = 0D;

		Double lCVPMonthlyAmt = 0D;
		Long lPensionBillAmt = 0L;
		Double lTotalArrearRecoveryAmt = 0D;

		Double lIR1Amt = 0D;
		Double lIR2Amt = 0D;
		Double lIR3Amt = 0D;
		Double lDblGrossAmount = 0D;
		Double lDblPensionNetAmount = 0D;
		Long lTrnPensionBillHdrPK = null;

		Date lDateOfDeath = null;
		Date lFP1Date = null;
		Date lFP2Date = null;
		Date lStrBillCrtDate = null;

		Integer lNoOfYear = 0;
		Integer lBillPaidMnt = 0;

		Double lPvrTempArr = 0d;

		TrnPensionRqstHdr lObjTrnPensionRqstHdr = null;

		BigDecimal lBgDcmlBasicPnsnAmount = BigDecimal.ZERO;
		BigDecimal lBgDcmlDPAmount = BigDecimal.ZERO;
		BigDecimal lBgDcmlDAAmount = BigDecimal.ZERO;
		BigDecimal lBgDcmlADPAmount = BigDecimal.ZERO;
		BigDecimal lBgDcmlRecoveryAmount = BigDecimal.ZERO;
		BigDecimal lBgDcmlCvpMonthlyAmount = BigDecimal.ZERO;
		BigDecimal lBgDcmlDaArrearAmount = BigDecimal.ZERO;
		BigDecimal lBgDcmlOthrArrearAmount = BigDecimal.ZERO;
		BigDecimal lBgDcmlGrossAmount = BigDecimal.ZERO;
		BigDecimal lBgDcmlNetPensionAmount = BigDecimal.ZERO;
		BigDecimal lBgDcmlIr1Amount = BigDecimal.ZERO;
		BigDecimal lBgDcmlIr2Amount = BigDecimal.ZERO;
		BigDecimal lBgDcmlIr3Amount = BigDecimal.ZERO;
		BigDecimal lBgDcmlPeonAllowance = BigDecimal.ZERO;
		BigDecimal lBgDcmlMAAmount = BigDecimal.ZERO;
		BigDecimal lBgDcmlGallantryAmount = BigDecimal.ZERO;
		BigDecimal lBgDcmlOtherBenefitAmount = BigDecimal.ZERO;
		BigDecimal lBgDcmlPensionCutAmount = BigDecimal.ZERO;
		SimpleDateFormat lSdf = new SimpleDateFormat("dd/MM/yyyy");
		String lStrSchemeCode = null;
		Double lDbl6PCArrAmt = 0d;
		Double lDblOthrDiffArrAmt = 0d;
		Double lDblCommutationArrAmt = 0d;
		BigDecimal lBgDcml6PCArrAmt = BigDecimal.ZERO;
		BigDecimal lBgDcmlOthrDiffArrAmt = BigDecimal.ZERO;
		BigDecimal lBgDcmlCommutationArrAmt = BigDecimal.ZERO;
		try {
			setSessionInfo(inputMap);

			lLngBillNo = Long.parseLong(inputMap.get("billNo").toString());

			lStrBillType = bundleConst.getString("RECOVERY.PENSION");

			lStrBasicPnsnFlage = lStrPnsrBasic;

			NewPensionBillDAO lObjNewPensionBillDAO = new NewPensionBillDAOImpl(srvcLoc.getSessionFactory());
			TrnPensionBillHdrDAO lObjTrnPensionBillHdrDAO = new TrnPensionBillHdrDAOImpl(TrnPensionBillHdr.class, srvcLoc.getSessionFactory());
			MstPensionerHdrDAO lObjMstPensionerHdrDAO = new MstPensionerHdrDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());

			if (lLngBillNo != null) {
				// Getting the ObjectVo of TrnPensionBillHdr
				lObjTrnPensionBillHdrVO = lObjTrnPensionBillHdrDAO.getTrnPensionBillHdr(lLngBillNo, lStrBillType);
				lStrTokenNo = lObjTrnPensionBillHdrVO.getTokenNo();
				lStrBillCrtDate = lObjTrnPensionBillHdrVO.getBillDate();
				lBillPaidMnt = lObjTrnPensionBillHdrVO.getForMonth();
				lStrSchemeCode = (lObjTrnPensionBillHdrVO.getSchemeCode() != null) ? lObjTrnPensionBillHdrVO.getSchemeCode() : "";
				// Getting the ObjectVo of TrnPensionBillDtls
				lTrnPensionBillHdrPK = lObjTrnPensionBillHdrVO.getTrnPensionBillHdrId();
				lObjTrnPensionBillDtls = lObjNewPensionBillDAO.getTrnPensionBillDtls(lTrnPensionBillHdrPK);
			}

			if (lObjTrnPensionBillDtls != null) {
				// Getting the Value from TrnPensionRqstHdr... Start...
				lStrPnsrCode = lObjTrnPensionBillDtls.getPensionerCode();
				lBasicPensionAmt = lObjTrnPensionBillDtls.getPensionAmount() != null ? lObjTrnPensionBillDtls.getPensionAmount().doubleValue() : 0D;
				lDPPerAmt = lObjTrnPensionBillDtls.getDpAmount() != null ? lObjTrnPensionBillDtls.getDpAmount().doubleValue() : 0D;
				if (lObjTrnPensionBillDtls.getAdpAmount() != null) {
					lADPAmt = lObjTrnPensionBillDtls.getAdpAmount().doubleValue();
				} else {
					lADPAmt = 0D;
				}
				lTIPerAmt = lObjTrnPensionBillDtls.getTiAmount() != null ? lObjTrnPensionBillDtls.getTiAmount().doubleValue() : 0D;
				lCVPMonthlyAmt = lObjTrnPensionBillDtls.getCvpMonthAmount() != null ? lObjTrnPensionBillDtls.getCvpMonthAmount().doubleValue() : 0D;
				lMAAmt = lObjTrnPensionBillDtls.getMedicalAllowenceAmount() != null ? lObjTrnPensionBillDtls.getMedicalAllowenceAmount().doubleValue() : 0D;
				lPersonalPnsnAmt = lObjTrnPensionBillDtls.getPersonalPensionAmount() != null ? lObjTrnPensionBillDtls.getPersonalPensionAmount().doubleValue() : 0d;
				lIRAmt = lObjTrnPensionBillDtls.getIrAmount() != null ? lObjTrnPensionBillDtls.getIrAmount().doubleValue() : 0D;
				lPensionCutAmt = lObjTrnPensionBillDtls.getPensnCutAmount() != null ? lObjTrnPensionBillDtls.getPensnCutAmount().doubleValue() : 0D;
				lITCutAmt = lObjTrnPensionBillDtls.getIncomeTaxCutAmount() != null ? lObjTrnPensionBillDtls.getIncomeTaxCutAmount().doubleValue() : 0D;
				lSpecialCutAmt = lObjTrnPensionBillDtls.getSpecialCutAmount() != null ? lObjTrnPensionBillDtls.getSpecialCutAmount().doubleValue() : 0D;
				lOtherBenefitAmt = lObjTrnPensionBillDtls.getOtherBenefits() != null ? lObjTrnPensionBillDtls.getOtherBenefits().doubleValue() : 0D;
				lATIAmt = lObjTrnPensionBillDtls.getTiArrearAmount() != null ? lObjTrnPensionBillDtls.getTiArrearAmount().doubleValue() : 0D;
				lArrearAmt = lObjTrnPensionBillDtls.getArrearAmount() != null ? lObjTrnPensionBillDtls.getArrearAmount().doubleValue() : 0D;
				lRecoveryAmt = lObjTrnPensionBillDtls.getRecoveryAmount() != null ? lObjTrnPensionBillDtls.getRecoveryAmount().doubleValue() : 0D;
				lOMRAmt = lObjTrnPensionBillDtls.getOmr() != null ? lObjTrnPensionBillDtls.getOmr().doubleValue() : 0D;
				lIR1Amt = lObjTrnPensionBillDtls.getIr1Amount() != null ? lObjTrnPensionBillDtls.getIr1Amount().doubleValue() : 0D;
				lIR2Amt = lObjTrnPensionBillDtls.getIr2Amount() != null ? lObjTrnPensionBillDtls.getIr2Amount().doubleValue() : 0D;
				lIR3Amt = lObjTrnPensionBillDtls.getIr3Amount() != null ? lObjTrnPensionBillDtls.getIr3Amount().doubleValue() : 0D;
				lPeonAllowance = lObjTrnPensionBillDtls.getPeonAllowance() != null ? lObjTrnPensionBillDtls.getPeonAllowance().doubleValue() : 0D;
				lGallantryAmt = lObjTrnPensionBillDtls.getOther1() != null ? lObjTrnPensionBillDtls.getOther1().doubleValue() : 0D;
				lDbl6PCArrAmt = lObjTrnPensionBillDtls.getArrear6PC() != null ? lObjTrnPensionBillDtls.getArrear6PC().doubleValue() : 0D;
				lDblOthrDiffArrAmt = lObjTrnPensionBillDtls.getArrearOthrDiff() != null ? lObjTrnPensionBillDtls.getArrearOthrDiff().doubleValue() : 0D;
				lDblCommutationArrAmt = lObjTrnPensionBillDtls.getArrearCommutation() != null ? lObjTrnPensionBillDtls.getArrearCommutation().doubleValue() : 0D;

				lNetPensionAmt = lObjTrnPensionBillDtls.getReducedPension() != null ? lObjTrnPensionBillDtls.getReducedPension().doubleValue() : 0D;
				lStrArrearDtls = lObjTrnPensionBillDtls.getArrearDtls();
				lStrEffFromDate = (lObjTrnPensionBillDtls.getFromDate() != null) ? lSdf.format(lObjTrnPensionBillDtls.getFromDate()) : "";
				lStrEffToDate = (lObjTrnPensionBillDtls.getToDate() != null) ? lSdf.format(lObjTrnPensionBillDtls.getToDate()) : "";
				// Getting the Value from TrnPensionRqstHdr... End...
			}

			if (lStrPnsrCode != null && lStrPnsrCode.length() > 0) {
				// Getting the ObjectVo of TrnPensionRqstHdr... Start...
				lObjTrnPensionRqstHdr = lObjNewPensionBillDAO.getPensionRqstHdrDtls(lStrPnsrCode, lStrStatus, gStrLocCode);

				if (lObjTrnPensionRqstHdr != null) {
					lStrPPONo = lObjTrnPensionRqstHdr.getPpoNo();
					lFP1Date = lObjTrnPensionRqstHdr.getFp1Date();
					lFP2Date = lObjTrnPensionRqstHdr.getFp2Date();
				}
				// Getting the ObjectVo of TrnPensionRqstHdr... Start...

				// Getting the ObjectVo of MstPensionerHdr... Start...
				lObjMstPensionerHdrVO = lObjMstPensionerHdrDAO.getMstPensionerHdrDtls(lStrPnsrCode);
				lStrPnsnerName = (lObjMstPensionerHdrVO.getFirstName() != null) ? lObjMstPensionerHdrVO.getFirstName() : " ";
				// lStrLastName = (lObjMstPensionerHdrVO.getLastName() != null)
				// ? lObjMstPensionerHdrVO.getLastName() : " ";
				// lStrMiddleName = (lObjMstPensionerHdrVO.getMiddleName() !=
				// null) ? lObjMstPensionerHdrVO.getMiddleName() + " " : "";
				// lStrPnsnerName = lStrFirstName + ' ' + lStrMiddleName +
				// lStrLastName;

				// Check for Basic Pension / FP1 Pension / FP2 Pension...
				// Start....
				lDateOfDeath = lObjMstPensionerHdrVO.getDateOfDeath();

				if (lDateOfDeath != null && lDateOfDeath.toString().length() > 0) {
					if (lDateOfDeath.before(lFP1Date) || lDateOfDeath.equals(lFP1Date)) {
						lStrBasicPnsnFlage = lStrPnsrFP1;
					} else if (lDateOfDeath.after(lFP2Date) || lDateOfDeath.equals(lFP2Date)) {
						lStrBasicPnsnFlage = lStrPnsrFP2;
					}
				}
				// Check for Basic Pension / FP1 Pension / FP2 Pension...
				// End....

				// Getting the ObjectVo of MstPensionerHdr... End...

			}

			/*
			 * lPensionBillAmt = (Math.round(lBasicPensionAmt) -
			 * Math.round(lPensionCutAmt)); lPensionBillAmt = lPensionBillAmt +
			 * Math.round(lDPPerAmt) + Math.round(lTIPerAmt) +
			 * Math.round(lMAAmt); lPensionBillAmt = lPensionBillAmt -
			 * Math.round(lCVPMonthlyAmt); lPensionBillAmt = lPensionBillAmt +
			 * Math.round(lPersonalPnsnAmt) + Math.round(lIRAmt) +
			 * Math.round(lOtherBenefitAmt); lPensionBillAmt = (lPensionBillAmt
			 * - (Math.round(lSpecialCutAmt) + Math.round(lITCutAmt) +
			 * (Math.round(lRecoveryAmt) -
			 * Math.round(lTotalArrearRecoveryAmt)))); lPensionBillAmt =
			 * lPensionBillAmt + Math.round(lOthrArrAmt);
			 */

			lBgDcmlBasicPnsnAmount = new BigDecimal(lBasicPensionAmt);
			lBgDcmlDAAmount = new BigDecimal(lTIPerAmt);
			lBgDcmlDPAmount = new BigDecimal(lDPPerAmt);
			lBgDcmlCvpMonthlyAmount = new BigDecimal(lCVPMonthlyAmt);
			lBgDcmlRecoveryAmount = new BigDecimal(lRecoveryAmt);
			lBgDcmlDaArrearAmount = new BigDecimal(lATIAmt);
			lBgDcmlOthrArrearAmount = new BigDecimal(lArrearAmt);
			lBgDcmlIr1Amount = new BigDecimal(lIR1Amt);
			lBgDcmlIr2Amount = new BigDecimal(lIR2Amt);
			lBgDcmlIr3Amount = new BigDecimal(lIR3Amt);
			lBgDcmlADPAmount = new BigDecimal(lADPAmt);
			lBgDcmlPeonAllowance = new BigDecimal(lPeonAllowance);
			lBgDcmlMAAmount = new BigDecimal(lMAAmt);
			lBgDcmlGallantryAmount = new BigDecimal(lGallantryAmt);
			lBgDcmlOtherBenefitAmount = new BigDecimal(lOtherBenefitAmt);
			lBgDcmlPensionCutAmount = new BigDecimal(lPensionCutAmt);
			lBgDcml6PCArrAmt = new BigDecimal(lDbl6PCArrAmt);
			lBgDcmlOthrDiffArrAmt = new BigDecimal(lDblOthrDiffArrAmt);
			lBgDcmlCommutationArrAmt = new BigDecimal(lDblCommutationArrAmt);
			lDblGrossAmount = lBgDcmlBasicPnsnAmount.doubleValue() + lBgDcmlDAAmount.doubleValue() + lBgDcmlDPAmount.doubleValue() + lBgDcmlADPAmount.doubleValue() + lBgDcmlIr1Amount.doubleValue()
					+ lBgDcmlIr2Amount.doubleValue() + lBgDcmlIr3Amount.doubleValue() + lBgDcmlDaArrearAmount.doubleValue() + lBgDcmlOthrArrearAmount.doubleValue()
					+ lBgDcmlPeonAllowance.doubleValue() + lBgDcmlMAAmount.doubleValue() + lBgDcmlGallantryAmount.doubleValue() + lBgDcmlOtherBenefitAmount.doubleValue()
					+ lBgDcml6PCArrAmt.doubleValue() + lBgDcmlOthrDiffArrAmt.doubleValue() + lBgDcmlCommutationArrAmt.doubleValue() - lBgDcmlCvpMonthlyAmount.doubleValue()
					- lBgDcmlPensionCutAmount.doubleValue();

			lDblPensionNetAmount = lDblGrossAmount - lBgDcmlRecoveryAmount.doubleValue();
			lBgDcmlGrossAmount = new BigDecimal(lDblGrossAmount);
			lBgDcmlNetPensionAmount = new BigDecimal(lDblPensionNetAmount);

			MonthlyPensionBillVO lMonthlyPensionBillVO = new MonthlyPensionBillVO();
			lMonthlyPensionBillVO.setPpoNo(lStrPPONo);
			lMonthlyPensionBillVO.setPensionerCode(lObjMstPensionerHdrVO.getPensionerCode());
			lMonthlyPensionBillVO.setPensionerName(lStrPnsnerName);
			// lMonthlyPensionBillVO.setAccountNo(lObjValidPcodeVO.getAcountNo());
			// lMonthlyPensionBillVO.setForMonth(lCurrentyyyyMM);
			// lMonthlyPensionBillVO.setHeadCode(new
			// BigDecimal(lObjValidPcodeVO.getHeadCode()));

			lMonthlyPensionBillVO.setBasicPensionAmount(lBgDcmlBasicPnsnAmount);
			lMonthlyPensionBillVO.setDpPercent(lBgDcmlDPAmount);
			lMonthlyPensionBillVO.setDpPercentAmount(lBgDcmlDPAmount);
			lMonthlyPensionBillVO.setAdpAmount(lBgDcmlADPAmount);
			lMonthlyPensionBillVO.setTiPercentAmount(lBgDcmlDAAmount);
			lMonthlyPensionBillVO.setCvpMonthlyAmount(lBgDcmlCvpMonthlyAmount);
			lMonthlyPensionBillVO.setTIArrearsAmount(lBgDcmlDaArrearAmount);
			lMonthlyPensionBillVO.setOtherArrearsAmount(lBgDcmlOthrArrearAmount);
			lMonthlyPensionBillVO.setIr1Amt(lBgDcmlIr1Amount);
			lMonthlyPensionBillVO.setIr2Amt(lBgDcmlIr2Amount);
			lMonthlyPensionBillVO.setIr3Amt(lBgDcmlIr3Amount);
			lMonthlyPensionBillVO.setPeonAllowanceAmt(lBgDcmlPeonAllowance);
			lMonthlyPensionBillVO.setMedicalAllowenceAmount(lBgDcmlMAAmount);
			lMonthlyPensionBillVO.setGallantryAmt(lBgDcmlGallantryAmount);
			lMonthlyPensionBillVO.setArrear6PC(lBgDcml6PCArrAmt);
			lMonthlyPensionBillVO.setArrearCommutation(lBgDcmlCommutationArrAmt);
			lMonthlyPensionBillVO.setArrearOthrDiff(lBgDcmlOthrDiffArrAmt);
			lMonthlyPensionBillVO.setOtherBenefit(lBgDcmlOtherBenefitAmount);
			lMonthlyPensionBillVO.setPensionCutAmount(lBgDcmlPensionCutAmount);
			lMonthlyPensionBillVO.setGrossAmount(lBgDcmlGrossAmount);
			lMonthlyPensionBillVO.setRecoveryAmount(lBgDcmlRecoveryAmount);
			lMonthlyPensionBillVO.setNetPensionAmount(lBgDcmlNetPensionAmount);
			// lMonthlyPensionBillVO.setFromDate(lPayStartDate);
			// lMonthlyPensionBillVO.setToDate(lPayEndDate);

			/*
			 * inputMap.put("PPONo", lStrPPONo); inputMap.put("CrntMonth",
			 * lBillPaidMnt);
			 * 
			 * inputMap.put("PnsnBillTokenNo", lStrTokenNo);
			 * inputMap.put("PnsrCrntDate", lStrBillCrtDate);
			 * 
			 * inputMap.put("PnsnerName", lStrPnsnerName);
			 * inputMap.put("lNoOfYear", lNoOfYear);
			 * inputMap.put("BasicPnsnFlage", lStrBasicPnsnFlage);
			 * inputMap.put("BasicPensionAmt", Math.round(lBasicPensionAmt));
			 * inputMap.put("PensionCutAmt", Math.round(lPensionCutAmt));
			 * inputMap.put("DPPercentAmt", Math.round(lDPPerAmt));
			 * inputMap.put("TIPercentAmt", Math.round(lTIPerAmt));
			 * inputMap.put("MAAmt", Math.round(lMAAmt));
			 * inputMap.put("PersonalPnsnAmt", Math.round(lPersonalPnsnAmt));
			 * inputMap.put("IRAmt", Math.round(lIRAmt));
			 * inputMap.put("ITCutAmt", Math.round(lITCutAmt));
			 * inputMap.put("SpecialCutAmt", Math.round(lSpecialCutAmt));
			 * inputMap.put("RecoveryAmt", Math.round(lRecoveryAmt));
			 * inputMap.put("lOthrArrears", Math.round(lOthrArrAmt));
			 * inputMap.put("CvpMonthlyAmt", Math.round(lCVPMonthlyAmt));
			 * inputMap.put("PensionBillAmt", lPensionBillAmt);
			 * inputMap.put("lNetPensionAmt", Math.round(lNetPensionAmt));
			 * inputMap.put("OtherBenefitAmt", Math.round(lOtherBenefitAmt));
			 * inputMap.put("TIArrearAmt", Math.round(lATIAmt));
			 * inputMap.put("OMRAmt", Math.round(lOMRAmt));
			 * inputMap.put("ArrearAmt", Math.round(lArrearAmt));
			 * inputMap.put("ArrearDtlVoLst", lObjArrearDtlsVOLst);
			 * inputMap.put("MonthlyArrearVoLst", lMonthlyArrearVoLst);
			 * inputMap.put("TotalArrearRecovery", lTotalArrearRecoveryAmt);
			 * inputMap.put("ADPAmt", Math.round(lADPAmt));
			 */

			/*
			 * String lStrPrintPensionBill =
			 * getPrintPensionBillString(inputMap);
			 * inputMap.put("BillArrPrintString", lStrPrintPensionBill);
			 */
			inputMap.put("CrntMonth", lBillPaidMnt);
			inputMap.put("PnsrCrntDate", lStrBillCrtDate);
			inputMap.put("PnsnerName", lStrPnsnerName);
			inputMap.put("lMonthlyPensionBillVO", lMonthlyPensionBillVO);
			inputMap.put("TrnPensionRqstHdrVO", lObjTrnPensionRqstHdr);
			inputMap.put("MstPensionerHdrVO", lObjMstPensionerHdrVO);
			inputMap.put("arrearDtls", lStrArrearDtls);
			inputMap.put("FromPayDate", lStrEffFromDate);
			inputMap.put("UptoBillCrtDate", lStrEffToDate);
			inputMap.put("schemeNo", lStrSchemeCode);
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is : ", e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	private void setSessionInfo(Map inputMap) {

		gLngPostId = SessionHelper.getPostId(inputMap);
		gLngUserId = SessionHelper.getUserId(inputMap);
		gStrLocCode = SessionHelper.getLocationCode(inputMap);
		gLngLangId = SessionHelper.getLangId(inputMap);
		gDate = DBUtility.getCurrentDateFromDB();
		gLngEmpId = SessionHelper.getEmpId(inputMap);
		glLocId = SessionHelper.getLocationId(inputMap);
		gLngDBId = SessionHelper.getDbId(inputMap);
	}

	private Integer getDaysOfMonth(Integer lYYYYMM) {

		Integer YYYY = Integer.parseInt(lYYYYMM.toString().substring(0, 4));
		Integer MM = Integer.parseInt(lYYYYMM.toString().substring(4, 6));
		Calendar cal = new GregorianCalendar(YYYY, (MM - 1), 1);
		Integer days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

		return days;
	}

	private Integer getDaysDifference(Date lDtFrom, Date lDtTo) {

		Calendar calFrom = Calendar.getInstance();
		calFrom.setTime(lDtFrom);

		Calendar calTo = Calendar.getInstance();
		calTo.setTime(lDtTo);
		Integer lIntDaysDiff = (calTo.get(Calendar.DATE) - calFrom.get(Calendar.DATE) + 1);
		return (lIntDaysDiff > 0) ? lIntDaysDiff : 0;
	}

	private static Integer getYearsDifference(Date lDtFrom, Date lDtTo) {

		int lYearDiff = 0;
		if (lDtFrom != null && lDtTo != null) {
			Calendar calFrom = Calendar.getInstance();
			calFrom.setTime(lDtFrom);

			Calendar calTo = Calendar.getInstance();
			calTo.setTime(lDtTo);

			lYearDiff = calTo.get(Calendar.YEAR) - calFrom.get(Calendar.YEAR);

			calFrom.add(Calendar.YEAR, lYearDiff);
			if (lDtTo.before(calFrom.getTime())) {
				lYearDiff--;
			}
		}
		return lYearDiff;
	}

	public ValidPcodeView setViewVo(Map inputMap) throws Exception {

		ValidPcodeView lValidPcodeView = null;
		TrnPensionRqstHdr lPnsnRqstHdrVo = null;
		MstPensionerHdr lPensionerHdr = null;
		MstPensionerDtls lObjMstPensionerDtls = null;
		try {
			if (inputMap.containsKey("TrnPensionRqstHdrVO") && inputMap.get("TrnPensionRqstHdrVO") != null) {
				lValidPcodeView = new ValidPcodeView();
				lPnsnRqstHdrVo = (TrnPensionRqstHdr) inputMap.get("TrnPensionRqstHdrVO");
				lPensionerHdr = (MstPensionerHdr) inputMap.get("MstPensionerHdrVO");
				lObjMstPensionerDtls = (MstPensionerDtls) inputMap.get("MstPensionerDtlsVO");

				lValidPcodeView.setPensionerCode(lPnsnRqstHdrVo.getPensionerCode());
				// lValidPcodeView.setbranchCode = branchCode;
				lValidPcodeView.setPpoNo(lPnsnRqstHdrVo.getPpoNo());
				lValidPcodeView.setSeenFlag(lPnsnRqstHdrVo.getSeenFlag());
				lValidPcodeView.setHeadCode(lPnsnRqstHdrVo.getHeadCode().longValue());
				lValidPcodeView.setStatus(lPnsnRqstHdrVo.getStatus());
				lValidPcodeView.setSchemeType(lPnsnRqstHdrVo.getSchemeType());
				lValidPcodeView.setIsRop(lPnsnRqstHdrVo.getIsRop());
				lValidPcodeView.setBasicPensionAmount(lPnsnRqstHdrVo.getBasicPensionAmount());
				lValidPcodeView.setFp1Date(lPnsnRqstHdrVo.getFp1Date());
				lValidPcodeView.setFp1Amount(lPnsnRqstHdrVo.getFp1Amount());
				lValidPcodeView.setFp2Date(lPnsnRqstHdrVo.getFp2Date());
				lValidPcodeView.setFp2Amount(lPnsnRqstHdrVo.getFp2Amount());
				lValidPcodeView.setEndDate(lPnsnRqstHdrVo.getEndDate());
				lValidPcodeView.setPensionRequestId(lPnsnRqstHdrVo.getPensionRequestId());
				lValidPcodeView.setCalcType(lPnsnRqstHdrVo.getCalcType());
				lValidPcodeView.setDpPercent(lPnsnRqstHdrVo.getDpPercent());
				lValidPcodeView.setTiPercent(lPnsnRqstHdrVo.getTiPercent());
				lValidPcodeView.setMedicalAllowenceAmount(lPnsnRqstHdrVo.getMedicalAllowenceAmount());
				lValidPcodeView.setOrgBf11156(lPnsnRqstHdrVo.getOrgBf11156());
				lValidPcodeView.setOrgAf11156(lPnsnRqstHdrVo.getOrgAf11156());
				lValidPcodeView.setOrgAf10560(lPnsnRqstHdrVo.getOrgAf10560());
				lValidPcodeView.setPersonalPension(lPnsnRqstHdrVo.getPersonalPension());
				lValidPcodeView.setIr(lPnsnRqstHdrVo.getIr());
				lValidPcodeView.setCvpMonthlyAmount(lPnsnRqstHdrVo.getCvpMonthlyAmount());
				lValidPcodeView.setPaidDate(lPnsnRqstHdrVo.getPaidDate());
				lValidPcodeView.setPnsnrPrefix(lPensionerHdr.getPnsnrPrefix());
				lValidPcodeView.setDateOfDeath(lPensionerHdr.getDateOfDeath());
				lValidPcodeView.setDateOfRetirement(lPensionerHdr.getDateOfRetirement());
				lValidPcodeView.setFirstName(lPensionerHdr.getFirstName());
				lValidPcodeView.setMiddleName(lPensionerHdr.getMiddleName());
				lValidPcodeView.setLastName(lPensionerHdr.getLastName());
				lValidPcodeView.setRedBf11156(lPnsnRqstHdrVo.getRedBf11156());
				lValidPcodeView.setRedAf11156(lPnsnRqstHdrVo.getRedAf11156());
				lValidPcodeView.setCvpDate(lPnsnRqstHdrVo.getCvpDate());
				lValidPcodeView.setPpoDate(lPnsnRqstHdrVo.getPpoDate());
				lValidPcodeView.setAppliedDate(lPnsnRqstHdrVo.getAppliedDate());
				lValidPcodeView.setCommensionDate(lPnsnRqstHdrVo.getCommensionDate());
				lValidPcodeView.setPensionType(lPnsnRqstHdrVo.getPensionType());
				lValidPcodeView.setOmrType(lPnsnRqstHdrVo.getOmrType());
				lValidPcodeView.setDpFlag(lPnsnRqstHdrVo.getDpFlag());
				lValidPcodeView.setCvpEffectiveMonth(lPnsnRqstHdrVo.getCvpEffectiveMonth());
				lValidPcodeView.setDateOfBirth(lPensionerHdr.getDateOfBirth());
				lValidPcodeView.setArrearAmount(lPnsnRqstHdrVo.getArrearAmount());
				lValidPcodeView.setRopType(lPnsnRqstHdrVo.getRopType());
				lValidPcodeView.setOrgBf11136(lPnsnRqstHdrVo.getOrgBf11136());
				lValidPcodeView.setOrgAfZp(lPnsnRqstHdrVo.getOrgAfZp());
				lValidPcodeView.setReEmploymentFlag(lPnsnRqstHdrVo.getReEmploymentFlag());
				lValidPcodeView.setDaInPensionSalary(lPnsnRqstHdrVo.getDaInPensionSalary());
				lValidPcodeView.setReEmploymentFromDate(lPnsnRqstHdrVo.getReEmploymentFromDate());
				lValidPcodeView.setReEmploymentToDate(lPnsnRqstHdrVo.getReEmploymentToDate());
				lValidPcodeView.setPeonAllowanceAmount(lPnsnRqstHdrVo.getPeonAllowanceAmount());
				lValidPcodeView.setOther1(lPnsnRqstHdrVo.getOther1());
				lValidPcodeView.setOther2(lPnsnRqstHdrVo.getOther2());
				lValidPcodeView.setDaRateForState(lPnsnRqstHdrVo.getDaRateForState().longValue());
				lValidPcodeView.setPensionCutAmount(lPnsnRqstHdrVo.getPensionCut());
				lValidPcodeView.setOrgBf10436Percent(lPnsnRqstHdrVo.getOrgBf10436Percent());
				lValidPcodeView.setOrgAf10436Percent(lPnsnRqstHdrVo.getOrgAf10436Percent());
				lValidPcodeView.setOrgAf11156Percent(lPnsnRqstHdrVo.getOrgAf11156Percent());
				lValidPcodeView.setOrgAf10560Percent(lPnsnRqstHdrVo.getOrgAf10560Percent());
				lValidPcodeView.setOrgZpAfPercent(lPnsnRqstHdrVo.getOrgZpAfPercent());
				lValidPcodeView.setPensionCutMonthyear(lPnsnRqstHdrVo.getPensionCutMonthYear());
				if (lObjMstPensionerDtls != null) {
					lValidPcodeView.setBankCode(lObjMstPensionerDtls.getBankCode());
					lValidPcodeView.setBranchCode(lObjMstPensionerDtls.getBranchCode());
				}
			}

		} catch (Exception e) {
			gLogger.error("Error is : ", e);
			throw (e);
		}
		return lValidPcodeView;
	}

	/**
	 * Dp Amt, TI Amt, ATI Amt, IR Amt according to HeadCode and for duration .
	 * 
	 * @param inputMap
	 * @throws Exception
	 */
	public void getMasterRatesForMonth(Map<String, Object> inputMap) throws Exception {

		new MonthlyPensionBillVO();

		new SimpleDateFormat("dd/MM/yyyy");
		try {

		} catch (Exception e) {
			gLogger.error("Error is :: " + e, e);
			throw (e);
		}
	}

	/*
	 * public static void main(String[] args) { Calendar c =
	 * Calendar.getInstance();
	 * 
	 * Date lCurrentDate = c.getTime(); c.set(2011,04,01,0,0,0); Date
	 * lMonthStartDt = c.getTime();
	 * 
	 * c.set(2011,04,31,0,0,0); Date lMonthEndDate = c.getTime();
	 * 
	 * c.set(2011,04,10,0,0,0); Date lPPOEndDate = c.getTime();
	 * 
	 * c.set(2011,04,07,0,0,0); Date lCommDate = c.getTime();
	 * 
	 * c.set(2011,11,31,0,0,0); Date lFP1Date = c.getTime();
	 * 
	 * c.set(2012,00,01,0,0,0); Date lFP2Date = c.getTime();
	 * 
	 * c.set(2011,04,06,0,0,0); Date lDeathDate = c.getTime(); Date
	 * lPayStartDate = lMonthStartDt; //First Date of month Date lPayEndDate =
	 * lMonthEndDate; // Last Date of month
	 * 
	 * int lCurrentyyyyMM = Integer.valueOf(new
	 * SimpleDateFormat("yyyyMM").format(lCurrentDate)); int lCommyyyyMM =
	 * Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lCommDate)); int
	 * lFP1yyyyMM = Integer.valueOf(new
	 * SimpleDateFormat("yyyyMM").format(lFP1Date)); int lFP2yyyyMM =
	 * Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lFP2Date)); int
	 * lPPOEndYYYYMM = lPPOEndDate !=null ?Integer.valueOf(new
	 * SimpleDateFormat("yyyyMM").format(lPPOEndDate)) :0; int lDeathYYYYMM =
	 * lDeathDate != null ? Integer.valueOf(new
	 * SimpleDateFormat("yyyyMM").format(lDeathDate)) :0; int
	 * lPayStartDateYYYYMM = Integer.valueOf(new
	 * SimpleDateFormat("yyyyMM").format(lMonthStartDt)); int lPayEndDateYYYYMM
	 * = Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lMonthEndDate));
	 * double lBasicPayDays = 0; double lFP1PayDays =0; double lFP2PayDays =0;
	 * double lTotalDaysofMonth = getDaysOfMonth(lCurrentyyyyMM);
	 * 
	 * lFP1yyyyMM = Integer.valueOf(new
	 * SimpleDateFormat("yyyyMM").format(lFP1Date));
	 * 
	 * try {
	 * 
	 * if(lCommyyyyMM == lCurrentyyyyMM ) { lPayStartDate =
	 * lCommDate.after(lMonthStartDt)? lCommDate : lMonthStartDt; // which ever
	 * is later } else { lPayStartDate = lMonthStartDt; }
	 * 
	 * 
	 * if(lPPOEndDate != null) { if(lPPOEndYYYYMM == lCurrentyyyyMM ) {
	 * lPayEndDate = lPPOEndDate.after(lMonthEndDate) ? lMonthEndDate :
	 * lPPOEndDate ; // which ever is earlier } else { lPayEndDate =
	 * lMonthEndDate; } } else { lPayEndDate = lMonthEndDate; }
	 * 
	 * if(lDeathDate == null) { lBasicPayDays =
	 * getDaysDifference(lPayStartDate,lPayEndDate);
	 * 
	 * } else if(lDeathDate != null) { //MonthlyPensionBillDAO
	 * lObjMonthlyPensionBillDAO = new
	 * MonthlyPensionBillDAOImpl(MstPensionerHdr.
	 * class,srvcLoc.getSessionFactory()); //MstPensionerFamilyDtls
	 * lMstPensionerFamilyDtlsVO =
	 * lObjMonthlyPensionBillDAO.getMstPensionerFamilyDtls(lStrPnsrCode);
	 * 
	 * List lLstFPMember = new ArrayList(); //lLstFPMember.add("1");
	 * if(lLstFPMember != null) { PensionBillProcessServiceImpl
	 * lObjProcessServiceImpl = new PensionBillProcessServiceImpl();
	 * //lLstFPMember =
	 * lObjProcessServiceImpl.getFpMemberList(lMstPensionerFamilyDtlsVO);
	 * c.set(2011,04,15,0,0,0); Date lFPDeathDate = c.getTime();
	 * //lMstPensionerFamilyDtlsVO.getDateOfDeath();
	 * 
	 * if(lLstFPMember.isEmpty()) // No valid family member found { //Exit; }
	 * else if(!lLstFPMember.isEmpty() && lFPDeathDate != null) // Pensioner and
	 * family member both are dead { int lFPDeathYYYYMM = lFPDeathDate != null ?
	 * Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lFPDeathDate)) :0;
	 * 
	 * if(lFPDeathYYYYMM == lCurrentyyyyMM) { if(lFPDeathDate.before(lDeathDate)
	 * || lFPDeathDate.equals(lDeathDate)) { //Exit lBasicPayDays =
	 * getDaysDifference(lPayStartDate,lDeathDate); } else { lBasicPayDays =
	 * getDaysDifference(lPayStartDate,lDeathDate);
	 * 
	 * 
	 * if(lFP2yyyyMM == lCurrentyyyyMM) { if(lFP1Date.before(lDeathDate)) {
	 * lFP2PayDays =
	 * getDaysDifference(addDaysInDate(lDeathDate,1),lFPDeathDate); } else {
	 * lFP1PayDays = getDaysDifference(addDaysInDate(lDeathDate1,),lFP1Date); }
	 * 
	 * if(lFP2Date.before(lFPDeathDate)) { lFP2PayDays =
	 * getDaysDifference(lFP2Date,lFPDeathDate); } } else {
	 * 
	 * if(lFP1Date.before(lDeathDate)) { lFP2PayDays =
	 * getDaysDifference(addDaysInDate(lDeathDate,1),lFPDeathDate); } else {
	 * lFP1PayDays =
	 * getDaysDifference(addDaysInDate(lDeathDate,1),lFPDeathDate); }
	 * 
	 * 
	 * if(lFP2Date.before(lPayStartDate)) { lFP1PayDays = 0; lFP2PayDays =
	 * getDaysDifference(lPayStartDate,lFPDeathDate); } else {
	 * if(lFP1Date.equals(lPayEndDate)) { lFP1PayDays =
	 * getDaysDifference(addDaysInDate(lDeathDate,1),lFPDeathDate); }
	 * 
	 * lFP2PayDays = 0; } }
	 * 
	 * }
	 * 
	 * }
	 * 
	 * } else // Pensioner is expired and Family member is alive {
	 * if(lDeathYYYYMM == lCurrentyyyyMM) {
	 * if(lCommDate.equals(addDaysInDate(lDeathDate,1))) { lFP1PayDays =
	 * getDaysDifference(lPayStartDate,lPayEndDate); } else { lBasicPayDays =
	 * getDaysDifference(lPayStartDate,lDeathDate);
	 * 
	 * if(lFP2yyyyMM == lCurrentyyyyMM) { lFP1PayDays =
	 * getDaysDifference(addDaysInDate(lDeathDate,1),lFP1Date); lFP2PayDays =
	 * getDaysDifference(lFP2Date,lPayEndDate); } else {
	 * if(lFP2Date.before(lPayStartDate)) { lFP1PayDays = 0; lFP2PayDays =
	 * getDaysDifference(lPayStartDate,lPayEndDate); } else {
	 * if(lFP1Date.equals(lPayEndDate)) { lFP1PayDays =
	 * getDaysDifference(addDaysInDate(lDeathDate,1),lPayEndDate); } lFP2PayDays
	 * = 0; } } } } else // Death already done earlier { if(lFP2yyyyMM ==
	 * lCurrentyyyyMM) { if(lFP2Date.equals(lPayStartDate)) { lFP2PayDays =
	 * getDaysDifference(lFP2Date,lPayEndDate); lFP1PayDays =0; } else {
	 * lFP1PayDays = getDaysDifference(lPayStartDate,lFP1Date); lFP2PayDays =
	 * getDaysDifference(lFP2Date,lPayEndDate); } } else { lFP1PayDays =
	 * getDaysDifference(lPayStartDate,lPayEndDate); lFP2PayDays = 0; } } } } }
	 * 
	 * Double lBasicPensionAmt = 10000D; Double lFP1Amount = 1000D; Double
	 * lFP2Amount = 100D;
	 * 
	 * System.out.println("lBasicPayDays =" + lBasicPayDays);
	 * System.out.println("lFP1PayDays =" + lFP1PayDays);
	 * System.out.println("lFP2PayDays =" + lFP2PayDays);
	 * 
	 * System.out.println("total Days =" + (lBasicPayDays + lFP1PayDays +
	 * lFP2PayDays)); lBasicPensionAmt = (lBasicPensionAmt
	 * (lBasicPayDays/lTotalDaysofMonth)) + (lFP1Amount
	 * (lFP1PayDays/lTotalDaysofMonth)) + (lFP2Amount
	 * (lFP2PayDays/lTotalDaysofMonth)); System.out.println("Basic Amount =" +
	 * (lBasicPensionAmt));
	 * 
	 * 
	 * 
	 * 
	 * 
	 * System.out.println("" + lFP1Date +"" + addDaysInDate(lFP1Date,1));
	 * System.out.println("" + lFP2Date +"" + addDaysInDate(lFP2Date,1));
	 * System.out.println("" + lDeathDate +"" + addDaysInDate(lDeathDate,1)); }
	 * catch(Exception e) { e.printStackTrace(); } }
	 */
	public static int getDaysOfMonth1(Integer lYYYYMM) {

		Integer YYYY = Integer.parseInt(lYYYYMM.toString().substring(0, 4));
		Integer MM = Integer.parseInt(lYYYYMM.toString().substring(4, 6));
		Calendar cal = new GregorianCalendar(YYYY, (MM - 1), 1);
		Integer days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

		return days;
	}

	public static int getDaysDifference1(Date lDtFrom, Date lDtTo) {

		Calendar calFrom = Calendar.getInstance();
		calFrom.setTime(lDtFrom);

		Calendar calTo = Calendar.getInstance();
		calTo.setTime(lDtTo);

		return calTo.get(Calendar.DATE) - calFrom.get(Calendar.DATE) + 1;
	}

	public static Date addDaysInDate1(Date lDate, int Days) throws Exception {

		Date lNewDate = null;
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(lDate);
			cal.add(Calendar.DATE, Days);
			lNewDate = cal.getTime();
		} catch (Exception e) {
			throw (e);
		}
		return lNewDate;
	}

	public Date addDaysInDate(Date lDate, int Days) throws Exception {

		Date lNewDate = null;
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(lDate);
			cal.add(Calendar.DATE, Days);
			lNewDate = cal.getTime();
		} catch (Exception e) {
			throw (e);
		}
		return lNewDate;
	}

	public Map getMonthlyPensionBillVO(Map<String, Object> inputMap) throws Exception {

		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		SimpleDateFormat lObjSmplDateFrm = new SimpleDateFormat("dd/MM/yyyy");
		ValidPcodeView lObjValidPcodeVO = (ValidPcodeView) inputMap.get("lObjValidPcode");
		if (inputMap.containsKey("lStrSubjectId") && (inputMap.get("lStrSubjectId") != null)) {
			inputMap.get("lStrSubjectId").toString();
		}
		String lStrPnsrCode = lObjValidPcodeVO.getPensionerCode();
		Integer lCurrentyyyyMM = 0;
		Date lCurrentDate = null;
		NewPensionBillDAOImpl lPensionBillDAO = new NewPensionBillDAOImpl(srvcLoc.getSessionFactory());

		MstPensionerFamilyDtls lMstPensionerFamilyDtlsVO = null;

		if (inputMap.containsKey("Date") && (inputMap.get("Date") != null)) {
			lCurrentyyyyMM = Integer.valueOf(inputMap.get("Date").toString());
			lCurrentDate = lObjSmplDateFrm.parse("01/" + (lCurrentyyyyMM.toString()).substring(4, 6) + "/" + (lCurrentyyyyMM.toString()).substring(0, 4));
		} else {
			Calendar c = Calendar.getInstance();
			lCurrentDate = c.getTime();
			lCurrentyyyyMM = Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lCurrentDate));
		}
		inputMap.put("lCurrentDate", lCurrentDate);

		Date lPPOEndDate = lObjValidPcodeVO.getEndDate();
		Date lCommDate = lObjValidPcodeVO.getCommensionDate();
		Date lFP1Date = lObjValidPcodeVO.getFp1Date();
		Date lFP2Date = lObjValidPcodeVO.getFp2Date();
		Date lDeathDate = lObjValidPcodeVO.getDateOfDeath();
		String lStrPnsnType = lObjValidPcodeVO.getPensionType();

		/*
		 * c.set(Calendar.DATE, 4); c.set(Calendar.MONTH, 4);
		 * c.set(Calendar.YEAR, 2011); Date lPPOEndDate = c.getTime();
		 * c.set(Calendar.DATE, 4); c.set(Calendar.MONTH, 3);
		 * c.set(Calendar.YEAR, 2011); Date lCommDate = c.getTime();
		 * c.set(Calendar.DATE, 14); c.set(Calendar.MONTH, 4);
		 * c.set(Calendar.YEAR, 2011); Date lFP1Date = c.getTime();
		 * c.set(Calendar.DATE, 15); c.set(Calendar.MONTH, 4);
		 * c.set(Calendar.YEAR, 2011); Date lFP2Date = c.getTime();
		 * c.set(Calendar.DATE, 5); c.set(Calendar.MONTH, 4);
		 * c.set(Calendar.YEAR, 2011); Date lDeathDate = c.getTime();
		 */

		int lCommyyyyMM = Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lCommDate));
		int lPPOEndYYYYMM = lPPOEndDate != null ? Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lPPOEndDate)) : 0;
		int lDeathYYYYMM = lDeathDate != null ? Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lDeathDate)) : 0;
		double lTotalDaysofMonth = getDaysOfMonth(lCurrentyyyyMM);

		double lBasicPayDays = 0;
		double lFP1PayDays = 0;
		double lFP2PayDays = 0;
		double lTotalPayableDays = 0;

		double lDPPercent = 0.0;
		double lDAPercent = 0.0;
		double lDPAmount = 0.0;
		double lADPamount = 0.0;
		double lDAAmount = 0.0;
		Double lDblRecoveryAmt = 0.0;
		Double lDblDaArrearAmt = 0.0;
		Double lDblPnsnArrearAmt = 0.0;
		Double lDblSixPayArrearAmt = 0.0;
		Double lDblPeonAllowanceAmt = lObjValidPcodeVO.getPeonAllowanceAmount() != null ? lObjValidPcodeVO.getPeonAllowanceAmount().doubleValue() : 0D;
		Double lDblGallantryAmt = lObjValidPcodeVO.getOther1() != null ? lObjValidPcodeVO.getOther1().doubleValue() : 0D;
		Double lDblOthrBenefitAmt = lObjValidPcodeVO.getOther2() != null ? lObjValidPcodeVO.getOther2().doubleValue() : 0D;
		// Double lDblPensionCutAmt = lObjValidPcodeVO.getPensionCutAmount() !=
		// null ? lObjValidPcodeVO.getPensionCutAmount().doubleValue() : 0D;
		Double lDblPensionCutAmt = 0D;
		Double lDblMAAmt = lObjValidPcodeVO.getMedicalAllowenceAmount() != null ? lObjValidPcodeVO.getMedicalAllowenceAmount().doubleValue() : 0D;

		Double lDblMonthlyCommutationAmt = 0.0;
		Double lDblIR1Amt = 0.0;
		Double lDblIR2Amt = 0.0;
		Double lDblIR3Amt = 0.0;
		Double lDblGrossAmount = 0.0;
		Double lDblPensionNetAmount = 0.0;
		Double lDblObj = null;
		BigDecimal lBgDcmlBasicPnsnAmount = BigDecimal.ZERO;
		BigDecimal lBgDcmlDPAmount = BigDecimal.ZERO;
		BigDecimal lBgDcmlDAAmount = BigDecimal.ZERO;
		BigDecimal lBgDcmlADPAmount = BigDecimal.ZERO;
		BigDecimal lBgDcmlRecoveryAmount = BigDecimal.ZERO;
		BigDecimal lBgDcmlCvpMonthlyAmount = BigDecimal.ZERO;
		BigDecimal lBgDcmlDaArrearAmount = BigDecimal.ZERO;
		BigDecimal lBgPnsnArrearAmount = BigDecimal.ZERO;
		BigDecimal lBgDcmlArrearAmount = (lObjValidPcodeVO.getArrearAmount() != null ? lObjValidPcodeVO.getArrearAmount() : BigDecimal.ZERO).setScale(0, BigDecimal.ROUND_HALF_UP);
		BigDecimal lBgDcmlGrossAmount = BigDecimal.ZERO;
		BigDecimal lBgDcmlNetPensionAmount = BigDecimal.ZERO;
		BigDecimal lBgDcmlIr1Amount = BigDecimal.ZERO;
		BigDecimal lBgDcmlIr2Amount = BigDecimal.ZERO;
		BigDecimal lBgDcmlIr3Amount = BigDecimal.ZERO;
		BigDecimal lBgDcmlDARate = BigDecimal.ZERO;
		BigDecimal lBgDcmlPeonAllowanceAmt = BigDecimal.ZERO;
		BigDecimal lBgDcmlGallantryAmt = BigDecimal.ZERO;
		BigDecimal lBgDcmlMAAmt = BigDecimal.ZERO;
		BigDecimal lBgDcmlOthrBenefit = BigDecimal.ZERO;
		BigDecimal lBgDcmlTotalArrearAmt = BigDecimal.ZERO;
		BigDecimal lBgDcmlPensionCutAmt = BigDecimal.ZERO;
		Double lDblTotalAllocationAmt = 0.0;
		Double lDblAllcBf36Amt = 0.0;
		Double lDblAllcAf36Amt = 0.0;
		Double lDblAllcAf56Amt = 0.0;
		Double lDblAllcAf60Amt = 0.0;
		Double lDblAllcAfZPAmt = 0.0;

		String lStrPnsrName = null;

		lBgDcmlRecoveryAmount = new BigDecimal(lDblRecoveryAmt).setScale(0, BigDecimal.ROUND_HALF_UP);
		Map<String, List<String>> lMapProbPPOBranch = null;
		List<String> lLstProbPpoNos = null;
		Map<String, List<String>> lMapNoFPDatesPpoBranch = null;
		List<String> lLstNoFPDatesPpoNos = null;
		SimpleDateFormat lSdfMonthYear = new SimpleDateFormat("yyyyMM");

		List<TrnCvpRestorationDtls> lLstTrnCvpRestorationDtlsObj = null;
		Integer lIntCVPFrmDt = 0;
		Integer lIntCVPToDt = 0;
		Date lDtCVPFrom = null;
		Date lDtCVPTo = null;
		Date lDtCVPEndDt = null;
		String lStrRestorationRcvFlg = null;
		int lCvpDays = 0;

		List<TrnPensionCutDtls> lLstTrnPensionCutDtlsObj = null;
		Integer lIntPnsnCutFrmDt = 0;
		Integer lIntPnsnCutToDt = 0;
		Date lDtPnsnCutFrom = null;
		Date lDtPnsnCutTo = null;
		Date lDtPnsnCutEnd = null;
		int lPnsnCutDays = 0;
		Double lDblMonthlyPunishmentCutAmt = 0.0;
		Double lDblLCArrAmt = 0d;
		Double lDbl6PCArrAmt = 0d;
		Double lDblOthrDiffArrAmt = 0d;
		Double lDblCommutationArrAmt = 0d;
		Double lDblDAOthrArrAmt = 0d;
		BigDecimal lBgDcmlLCArrAmt = BigDecimal.ZERO;
		BigDecimal lBgDcml6PCArrAmt = BigDecimal.ZERO;
		BigDecimal lBgDcmlOthrDiffArrAmt = BigDecimal.ZERO;
		BigDecimal lBgDcmlCommutationArrAmt = BigDecimal.ZERO;
		BigDecimal lBgDcmlMonthlyPunishmentCutAmt = BigDecimal.ZERO;
		try {

			/** Basic Amount Calculation Start ****************************************************************************/
			SimpleDateFormat lFormatDDYYYYMM = new SimpleDateFormat("ddyyyyMM");
			Date lMonthStartDt = lFormatDDYYYYMM.parse("01" + lCurrentyyyyMM);
			Date lMonthEndDt = lFormatDDYYYYMM.parse(((int) lTotalDaysofMonth + "" + lCurrentyyyyMM));

			inputMap.put("lMonthStartDt", lMonthStartDt);
			inputMap.put("lMonthEndDt", lMonthEndDt);
			lMapProbPPOBranch = (Map<String, List<String>>) inputMap.get("lMapProbPPOBranch");
			lMapNoFPDatesPpoBranch = (Map<String, List<String>>) inputMap.get("lMapNoFPDatesPpoBranch");
			Date lPayStartDate = lMonthStartDt; // First Date of month
			Date lPayEndDate = lMonthEndDt; // Last Date of month

			if (lCommyyyyMM == lCurrentyyyyMM) {
				lPayStartDate = lCommDate.after(lMonthStartDt) ? lCommDate : lMonthStartDt; // which
				// ever
				// is
				// later
			} else {
				lPayStartDate = lMonthStartDt;
			}

			if (lPPOEndDate != null) {
				if (lPPOEndYYYYMM == lCurrentyyyyMM) {
					lPayEndDate = lPPOEndDate.after(lMonthEndDt) ? lMonthEndDt : lPPOEndDate; // which
					// ever
					// is
					// earlier
				} else {
					lPayEndDate = lMonthEndDt;
				}
			} else {
				lPayEndDate = lMonthEndDt;
			}

			if (lDeathDate == null) {
				lBasicPayDays = getDaysDifference(lPayStartDate, lPayEndDate);
			} else if (lDeathDate != null) {
				// Integer.valueOf(new
				// SimpleDateFormat("yyyyMM").format(lFP1Date));
				Map lMapFamilyDtls = (Map) inputMap.get("lMapFamilyDtls");
				if (lMapFamilyDtls != null) {
					lMstPensionerFamilyDtlsVO = (MstPensionerFamilyDtls) lMapFamilyDtls.get(lStrPnsrCode);
				} else {
					MonthlyPensionBillDAO lObjMonthlyPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());
					lMstPensionerFamilyDtlsVO = lObjMonthlyPensionBillDAO.getMstPensionerFamilyDtls(lObjValidPcodeVO.getPensionerCode());
				}

				inputMap.put("lMstPensionerFamilyDtlsVO", lMstPensionerFamilyDtlsVO);
				List lLstFPMember = new ArrayList(); // lLstFPMember.add("1");
				if (lMstPensionerFamilyDtlsVO != null) {
					PensionBillProcessServiceImpl lObjProcessServiceImpl = new PensionBillProcessServiceImpl();
					lLstFPMember = lObjProcessServiceImpl.getFpMemberList(lMstPensionerFamilyDtlsVO);

					Date lFPDeathDate = lMstPensionerFamilyDtlsVO.getDateOfDeath();
					inputMap.put("lLstFPMember", lLstFPMember);
					if (lLstFPMember.isEmpty()) // No valid family member found
					{
						// Exit;
						if (lDeathYYYYMM == lCurrentyyyyMM) {
							lBasicPayDays = getDaysDifference(lPayStartDate, (lDeathDate.before(lPayEndDate)) ? lDeathDate : lPayEndDate); // change
						}
					} else if (!lLstFPMember.isEmpty()) {
						if (lFP1Date != null && lFP2Date != null) {
							int lFP2yyyyMM = Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lFP2Date));
							// Pensioner
							// and
							// family
							// member
							// both
							// are
							// dead
							if (lFPDeathDate != null) {
								int lFPDeathYYYYMM = lFPDeathDate != null ? Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lFPDeathDate)) : 0;

								if (lFPDeathYYYYMM == lCurrentyyyyMM) {
									if (lFPDeathDate.before(lDeathDate) || lFPDeathDate.equals(lDeathDate)) {
										// Exit
										lBasicPayDays = getDaysDifference(lPayStartDate, (lDeathDate.before(lPayEndDate)) ? lDeathDate : lPayEndDate); // change
									} else {
										lBasicPayDays = getDaysDifference(lPayStartDate, (lDeathDate.before(lPayEndDate)) ? lDeathDate : lPayEndDate); // change

										if (lFP2yyyyMM == lCurrentyyyyMM) {
											if (lFP1Date.before(lDeathDate)) {
												lFP2PayDays = getDaysDifference(addDaysInDate(lDeathDate, 1), (lFPDeathDate.before(lPayEndDate)) ? lFPDeathDate : lPayEndDate); // change
											} else {
												lFP1PayDays = getDaysDifference(addDaysInDate(lDeathDate, 1), (lFP1Date.before(lPayEndDate)) ? lFP1Date : lPayEndDate); // change
											}

											if (lFP2Date.before(lFPDeathDate)) {
												// lFP2PayDays =
												// getDaysDifference(lFP2Date,
												// lFPDeathDate);
												lFP2PayDays = getDaysDifference(lFP2Date, (lFPDeathDate.before(lPayEndDate)) ? lFPDeathDate : lPayEndDate); // changed
											}
										} else {

											if (lFP1Date.before(lDeathDate)) {
												lFP2PayDays = getDaysDifference(addDaysInDate(lDeathDate, 1), (lFPDeathDate.before(lPayEndDate)) ? lFPDeathDate : lPayEndDate); // changed
											} else {
												lFP1PayDays = getDaysDifference(addDaysInDate(lDeathDate, 1), (lFPDeathDate.before(lPayEndDate)) ? lFPDeathDate : lPayEndDate); // changed
											}

											/*
											 * if(lFP2Date.before(lPayStartDate))
											 * { lFP1PayDays = 0; lFP2PayDays =
											 * getDaysDifference
											 * (lPayStartDate,lFPDeathDate); }
											 * else {
											 * if(lFP1Date.equals(lPayEndDate))
											 * { lFP1PayDays =
											 * getDaysDifference(addDaysInDate
											 * (lDeathDate,1),lFPDeathDate); }
											 * 
											 * lFP2PayDays = 0; }
											 */
										}

									}

								}
							} else // Pensioner is expired and Family member is
									// alive
							{
								if (lDeathYYYYMM == lCurrentyyyyMM) {
									// ----For family pension case commencement
									// date
									// is
									// next to death date
									if (lCommDate.equals(addDaysInDate(lDeathDate, 1))) {
										lFP1PayDays = getDaysDifference(lPayStartDate, lPayEndDate);
									} else {
										lBasicPayDays = getDaysDifference(lPayStartDate, (lDeathDate.before(lPayEndDate)) ? lDeathDate : lPayEndDate);

										if (lFP2yyyyMM == lCurrentyyyyMM) {
											lFP1PayDays = getDaysDifference(addDaysInDate(lDeathDate, 1), lFP1Date);
											lFP2PayDays = getDaysDifference((lDeathDate.after(lFP2Date)) ? lDeathDate : lFP2Date, lPayEndDate);// whichever
											// is
											// later.
										} else {
											if (lFP2Date.before(lPayStartDate)) {
												lFP1PayDays = 0;
												// lFP2PayDays =
												// getDaysDifference(lPayStartDate,
												// lPayEndDate);
												lFP2PayDays = getDaysDifference(addDaysInDate(lDeathDate, 1), lPayEndDate); // changed
											} else {
												/*
												 * If fp2 date is after current
												 * month then check if fp1 date
												 * is last day of current month
												 * or not.
												 */
												if (lFP1Date.equals(lPayEndDate)) {
													lFP1PayDays = getDaysDifference(addDaysInDate(lDeathDate, 1), lPayEndDate);
												} /*
												 * else { lFP1PayDays =
												 * getDaysDifference
												 * (addDaysInDate(lDeathDate,
												 * 1), lPayEndDate); // changed
												 * }
												 */
												lFP2PayDays = 0;
											}
										}
									}
								} else // Pensioner's Death already done earlier
										// not
										// in
								// current month (before current month)
								{
									if (lFP2yyyyMM == lCurrentyyyyMM) {
										if (lFP2Date.equals(lPayStartDate)) {
											lFP2PayDays = getDaysDifference(lFP2Date, lPayEndDate);
											lFP1PayDays = 0;
										} else {
											lFP1PayDays = getDaysDifference(lPayStartDate, lFP1Date);
											lFP2PayDays = getDaysDifference(lFP2Date, lPayEndDate);
										}
									} else {
										// change in this block
										if (lFP2Date.before(lPayStartDate)) {
											lFP1PayDays = 0;
											// lFP2PayDays =
											// getDaysDifference(lPayStartDate,
											// lPayEndDate);
											lFP2PayDays = getDaysDifference(lPayStartDate, lPayEndDate); // changed
											lFP1PayDays = 0;
										} else {
											lFP1PayDays = getDaysDifference(lPayStartDate, lPayEndDate);
											lFP2PayDays = 0;
										}
									}
								}
							}
						} else {
							// --Creating branch-wise list of ppo , of which fp1
							// date or fp2 date is null and death date is not
							// null.and have valid family pensioner.
							if (lMapNoFPDatesPpoBranch != null) {
								lLstNoFPDatesPpoNos = lMapNoFPDatesPpoBranch.get(lObjValidPcodeVO.getBranchCode());
								if (lLstNoFPDatesPpoNos != null) {
									lLstNoFPDatesPpoNos.add(lObjValidPcodeVO.getPpoNo());
								} else {
									lLstNoFPDatesPpoNos = new ArrayList<String>();
									lLstNoFPDatesPpoNos.add(lObjValidPcodeVO.getPpoNo());
								}
								lMapNoFPDatesPpoBranch.put(lObjValidPcodeVO.getBranchCode(), lLstNoFPDatesPpoNos);
							}
						}
					}
				} else {
					if (lDeathYYYYMM == lCurrentyyyyMM) {
						lBasicPayDays = getDaysDifference(lPayStartDate, (lDeathDate.before(lPayEndDate)) ? lDeathDate : lPayEndDate); // change
					}
				}
			}

			/*
			 * Double lBasicPensionAmt = 10000D; Double lFP1Amount = 1000D;
			 * Double lFP2Amount = 100D;
			 */

			Double lBasicPensionAmt = lObjValidPcodeVO.getBasicPensionAmount().doubleValue();
			Double lFP1Amount = lObjValidPcodeVO.getFp1Amount().doubleValue();
			Double lFP2Amount = lObjValidPcodeVO.getFp2Amount().doubleValue();

			lTotalPayableDays = lBasicPayDays + lFP1PayDays + lFP2PayDays;
			// System.out.println("lBasicPayDays =" + lBasicPayDays);
			// System.out.println("lFP1PayDays =" + lFP1PayDays);
			// System.out.println("lFP2PayDays =" + lFP2PayDays);

			// System.out.println("total Days =" + (lBasicPayDays + lFP1PayDays
			// + lFP2PayDays));

			Double lDblCalculatedBasicAmtForPnsr = lBasicPensionAmt * (lBasicPayDays / lTotalDaysofMonth);
			Double lDblCalculatedBasicAmtForFp = (lFP1Amount * (lFP1PayDays / lTotalDaysofMonth)) + (lFP2Amount * (lFP2PayDays / lTotalDaysofMonth));

			inputMap.put("lDblCalculatedBasicAmtForPnsr", lDblCalculatedBasicAmtForPnsr);
			inputMap.put("lDblCalculatedBasicAmtForFp", lDblCalculatedBasicAmtForFp);

			Double lCalculatedBasicPensionAmt = (lBasicPensionAmt * (lBasicPayDays / lTotalDaysofMonth)) + (lFP1Amount * (lFP1PayDays / lTotalDaysofMonth))
					+ (lFP2Amount * (lFP2PayDays / lTotalDaysofMonth));
			// System.out.println("Basic Amount =" +
			// (lCalculatedBasicPensionAmt));
			inputMap.put("lCalculatedBasicPensionAmt", lCalculatedBasicPensionAmt);
			/** Basic Amount Calculation Over ****************************************************************************/

			MonthlyPensionBillDAO lObjMnthlyBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());
			String lStrPnsnrCode = lObjValidPcodeVO.getPensionerCode();

			// ----If pension type is "Injury Pension" or "Gallantry Pension"
			// then net pension = calculated basic amount + arrear amount only.
			// ----For Political pensin type net pension = calculated basic
			// amount + arrear amount - recovery amount
			if ((!bundleCaseConst.getString("PPMT.INJURY").equals(lStrPnsnType) && !bundleCaseConst.getString("PPMT.GALLANTRY").equals(lStrPnsnType))
					&& !bundleCaseConst.getString("PPMT.POLITICAL").equals(lStrPnsnType)) {

				/** Punishment cut calculation starts ***************************************************************************/
				Map<String, List<TrnPensionCutDtls>> lPensionCutDtlMap = (Map<String, List<TrnPensionCutDtls>>) inputMap.get("lPensionCutDtlMap");
				if (lPensionCutDtlMap != null) {
					// ---For monthly pension
					lLstTrnPensionCutDtlsObj = lPensionCutDtlMap.get(lStrPnsnrCode);
				} else {
					// ---For first pension
					lLstTrnPensionCutDtlsObj = lObjMnthlyBillDAO.getMonthlyPensionCutAmount(lStrPnsnrCode, lCurrentyyyyMM);
				}
				if (lLstTrnPensionCutDtlsObj != null) {
					for (TrnPensionCutDtls lObjTrnPensionCutDtls : lLstTrnPensionCutDtlsObj) {
						lDtPnsnCutFrom = lObjTrnPensionCutDtls.getFromDate();
						lDtPnsnCutTo = lObjTrnPensionCutDtls.getToDate();
						if (lDtPnsnCutFrom != null) {
							lIntPnsnCutFrmDt = Integer.valueOf(lSdfMonthYear.format(lDtPnsnCutFrom));
							if (lDtPnsnCutTo != null) {
								lIntPnsnCutToDt = Integer.valueOf(lSdfMonthYear.format(lDtPnsnCutTo));
							} else {
								// --If pension cut to date is null then
								// assigning last day of current month as
								// pension cut to date.
								lIntPnsnCutToDt = lCurrentyyyyMM;
								lDtPnsnCutTo = lPayEndDate;
							}

							if (lDeathDate != null) {
								if (lDeathYYYYMM == lCurrentyyyyMM) {
									// --In current month:pensioncut from
									// date,pensioncut to
									// date,death
									// date,pay end date(may be)
									if (lIntPnsnCutFrmDt.equals(lCurrentyyyyMM) && lIntPnsnCutToDt.equals(lCurrentyyyyMM)) {
										lDtPnsnCutEnd = lDeathDate.before(lPayEndDate) ? lDeathDate : lPayEndDate;
										lDtPnsnCutEnd = lDtPnsnCutEnd.before(lDtPnsnCutTo) ? lDtPnsnCutEnd : lDtPnsnCutTo;
										lPnsnCutDays = getDaysDifference(lDtPnsnCutFrom, lDtPnsnCutEnd);
									}
									// --In current month:pensioncut from
									// date,death
									// date,pay end date(may be)
									else if (lIntPnsnCutFrmDt.equals(lCurrentyyyyMM)) {
										lDtPnsnCutEnd = lDeathDate.before(lPayEndDate) ? lDeathDate : lPayEndDate;
										lPnsnCutDays = getDaysDifference(lDtPnsnCutFrom, lDtPnsnCutEnd);
									}
									// --In current month:pensioncut to
									// date,death
									// date,pay end date(may be)
									else if (lIntPnsnCutToDt.equals(lCurrentyyyyMM)) {
										lDtPnsnCutEnd = lDeathDate.before(lPayEndDate) ? lDeathDate : lPayEndDate;
										lDtPnsnCutEnd = lDtPnsnCutEnd.before(lDtPnsnCutTo) ? lDtPnsnCutEnd : lDtPnsnCutTo;
										lPnsnCutDays = getDaysDifference(lPayStartDate, lDtPnsnCutEnd);
									}
									// ---In current month:death date,pay end
									// date(may
									// be)
									else if ((lCurrentyyyyMM > lIntPnsnCutFrmDt) && (lCurrentyyyyMM < lIntPnsnCutToDt)) {
										lDtPnsnCutEnd = lDeathDate.before(lPayEndDate) ? lDeathDate : lPayEndDate;
										lPnsnCutDays = getDaysDifference(lPayStartDate, lDtPnsnCutEnd);
									}
								} else {
									// --If death date is passed then not
									// cutting pension cut.
									if (lDeathYYYYMM < lCurrentyyyyMM) {
										lPnsnCutDays = 0;
									}
								}
							} else {
								// --In current month:pensioncut from date,
								// pensioncut to date,pay
								// end
								// date(may be)
								if (lIntPnsnCutFrmDt.equals(lCurrentyyyyMM) && lIntPnsnCutToDt.equals(lCurrentyyyyMM)) {
									lDtPnsnCutEnd = lPayEndDate.before(lDtPnsnCutTo) ? lPayEndDate : lDtPnsnCutTo;
									lPnsnCutDays = getDaysDifference(lDtPnsnCutFrom, lDtPnsnCutEnd);
								}
								// --In current month:pensioncut from date,pay
								// end
								// date(may be)
								else if (lIntPnsnCutFrmDt.equals(lCurrentyyyyMM)) {
									lPnsnCutDays = getDaysDifference(lDtPnsnCutFrom, lPayEndDate);
								}
								// --In current month:pensioncut to date,pay end
								// date(may be)
								else if (lIntPnsnCutToDt.equals(lCurrentyyyyMM)) {
									lDtPnsnCutEnd = lDtPnsnCutTo.before(lPayEndDate) ? lDtPnsnCutTo : lPayEndDate;
									lPnsnCutDays = getDaysDifference(lPayStartDate, lDtPnsnCutEnd);
								}
								// ---In current month:pay end date(may be)
								else if ((lCurrentyyyyMM > lIntPnsnCutFrmDt) && (lCurrentyyyyMM < lIntPnsnCutToDt)) {
									lPnsnCutDays = getDaysDifference(lPayStartDate, lPayEndDate);
								}
							}
						}
						lDblMonthlyPunishmentCutAmt = lDblMonthlyPunishmentCutAmt + ((lPnsnCutDays / lTotalDaysofMonth) * lObjTrnPensionCutDtls.getAmount().doubleValue());
					}
				}
				lCalculatedBasicPensionAmt = lCalculatedBasicPensionAmt - lDblMonthlyPunishmentCutAmt;
				/** Punishment cut calculation ends **********************************************************************/

				/** ROP Wise Amount Calculation ****************************************************************************/
				// Double lDblGrossAmount = lCalculatedBasicPensionAmt;
				lBgDcmlBasicPnsnAmount = new BigDecimal(lCalculatedBasicPensionAmt).setScale(0, BigDecimal.ROUND_HALF_UP);
				lCalculatedBasicPensionAmt = lBgDcmlBasicPnsnAmount.doubleValue();
				String lStrROPPaid = lObjValidPcodeVO.getRopType();
				String lStrDPFlag = lObjValidPcodeVO.getDpFlag();
				RltPensionHeadcodeRate lPensionHeadcodeRateVO = null;
				if (lStrROPPaid != null && "1986".equals(lStrROPPaid)) {
					/* Get DA -TI Rate & Amount */// Flag is = _DA1986_
					lPensionHeadcodeRateVO = (RltPensionHeadcodeRate) inputMap.get(lObjValidPcodeVO.getDaRateForState() + "_DA_1986_" + lCalculatedBasicPensionAmt + "_" + lPayStartDate);
					if (lPensionHeadcodeRateVO == null) {
						lPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lObjValidPcodeVO.getDaRateForState(), "DA_1986", lCalculatedBasicPensionAmt, lPayStartDate);
						inputMap.put(lObjValidPcodeVO.getDaRateForState() + "_DA_1986_" + lCalculatedBasicPensionAmt + "_" + lPayStartDate, lPensionHeadcodeRateVO);
					}
					if (lPensionHeadcodeRateVO != null) {
						lDAPercent = (lPensionHeadcodeRateVO.getRate() != null) ? lPensionHeadcodeRateVO.getRate().doubleValue() : 0;

						lDAAmount = (lCalculatedBasicPensionAmt) * (lDAPercent / 100);

						/*
						 * If lDAAmount is less than minimum amount then
						 * DAAmount = minAmount
						 */
						if (lPensionHeadcodeRateVO.getMinAmount() != null && !lPensionHeadcodeRateVO.equals("")) {
							Double lMinAmt = (Double.valueOf(lPensionHeadcodeRateVO.getMinAmount().toString()) * lTotalPayableDays) / lTotalDaysofMonth;
							if (lMinAmt > lDAAmount) {
								lDAAmount = lMinAmt;
							}
						}
					}
					/** Get DA Rate & Amount Over **/

					/** Getting IR Details For ROP 1986 Starts *****************************************************************************************/
					/*
					 * List<String> lLstIRApplicable = new ArrayList<String>();
					 * List<RltPensionHeadcodeRate> lLstRltPensionHeadcodeRateVO
					 * = new ArrayList<RltPensionHeadcodeRate>();
					 * 
					 * if (lCurrentDate != null) { if (lCurrentDate.after(new
					 * SimpleDateFormat("dd/mm/yyyy").parse("31/03/1995")) &&
					 * lCurrentDate.before(new
					 * SimpleDateFormat("dd/mm/yyyy").parse("01/04/1996"))) {
					 * lLstIRApplicable.add("IR2"); } else if
					 * (lCurrentDate.after(new
					 * SimpleDateFormat("dd/mm/yyyy").parse("31/03/1996"))) {
					 * lLstIRApplicable.add("IR2"); lLstIRApplicable.add("IR3");
					 * } lLstRltPensionHeadcodeRateVO =
					 * lObjMnthlyBillDAO.getIRRateFromHeadcodeRateRlt
					 * (lObjValidPcodeVO.getHeadCode(), lLstIRApplicable);
					 * lIR1Amt = (50 lTotalPayableDays) / lTotalDaysofMonth; //
					 * IR1 // is // fixed // 50 // . if
					 * (lLstRltPensionHeadcodeRateVO != null &&
					 * lLstRltPensionHeadcodeRateVO.size() > 0) { Double
					 * lCalculatedIRAmt = 0.0; Double lMinIRAmt = 0.0; for
					 * (Object lObjVO : lLstRltPensionHeadcodeRateVO) {
					 * RltPensionHeadcodeRate lObjPensionHeadcodeRate =
					 * (RltPensionHeadcodeRate) lObjVO; lCalculatedIRAmt =
					 * (lObjPensionHeadcodeRate.getRate().doubleValue()
					 * lCalculatedBasicPensionAmt) / 100; lMinIRAmt =
					 * (lObjPensionHeadcodeRate.getMinAmount().doubleValue()
					 * lTotalPayableDays) / lTotalDaysofMonth; if
					 * ("IR2".equals(lObjPensionHeadcodeRate.getFieldType())) {
					 * lIR2Amt = (lCalculatedIRAmt > lMinIRAmt) ?
					 * lCalculatedIRAmt : lMinIRAmt; } if
					 * ("IR3".equals(lObjPensionHeadcodeRate.getFieldType())) {
					 * lIR3Amt = (lCalculatedIRAmt > lMinIRAmt) ?
					 * lCalculatedIRAmt : lMinIRAmt; } } }
					 * 
					 * }
					 */

					if (lCurrentDate != null && lCurrentDate.after(lObjSmplDateFrm.parse("31/03/1995"))) {
						lDblIR1Amt = (50 * lTotalPayableDays) / lTotalDaysofMonth; // IR1
						List<String> lLstIRApplicable = new ArrayList<String>();
						lLstIRApplicable.add("IR2");
						lLstIRApplicable.add("IR3");
						List<RltPensionHeadcodeRate> lLstRltPensionHeadcodeRateVO = null;

						lLstRltPensionHeadcodeRateVO = (List<RltPensionHeadcodeRate>) inputMap.get(lObjValidPcodeVO.getDaRateForState() + "_IR_");
						if (lLstRltPensionHeadcodeRateVO == null) {
							lLstRltPensionHeadcodeRateVO = lPensionBillDAO.getRateLstFromHeadcode(lObjValidPcodeVO.getDaRateForState(), lLstIRApplicable);
							inputMap.put(lObjValidPcodeVO.getDaRateForState() + "_IR_", lLstRltPensionHeadcodeRateVO);
						}
						if (lLstRltPensionHeadcodeRateVO != null && lLstRltPensionHeadcodeRateVO.size() > 0) {
							Double lCalculatedIRAmt = 0.0;
							Double lMinIRAmt = 0.0;

							for (Object lObjVO : lLstRltPensionHeadcodeRateVO) {
								RltPensionHeadcodeRate lObjPensionHeadcodeRate = (RltPensionHeadcodeRate) lObjVO;
								lCalculatedIRAmt = (((lObjPensionHeadcodeRate.getRate() != null) ? lObjPensionHeadcodeRate.getRate().doubleValue() : 0) * lCalculatedBasicPensionAmt) / 100;
								lMinIRAmt = (((lObjPensionHeadcodeRate.getMinAmount() != null) ? lObjPensionHeadcodeRate.getMinAmount().doubleValue() : 0) * lTotalPayableDays) / lTotalDaysofMonth;
								if ("IR2".equals(lObjPensionHeadcodeRate.getFieldType())) {
									lDblIR2Amt = (lCalculatedIRAmt > lMinIRAmt) ? lCalculatedIRAmt : lMinIRAmt;
								}
								if ("IR3".equals(lObjPensionHeadcodeRate.getFieldType())) {
									lDblIR3Amt = (lCalculatedIRAmt > lMinIRAmt) ? lCalculatedIRAmt : lMinIRAmt;
								}
							}
						}
					}
					/** Getting IR Details For ROP 1986 Ends *****************************************************************************************/

				} else if (lStrROPPaid != null && "1996".equals(lStrROPPaid)) {
					if (lStrDPFlag != null && "Y".equals(lStrDPFlag)) {
						/* Get DP Rate & Amount */

						lPensionHeadcodeRateVO = (RltPensionHeadcodeRate) inputMap.get(lObjValidPcodeVO.getDaRateForState() + "_DP_" + lCalculatedBasicPensionAmt + "_" + lPayStartDate);
						if (lPensionHeadcodeRateVO == null) {
							lPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lObjValidPcodeVO.getDaRateForState(), "DP", lCalculatedBasicPensionAmt, lPayStartDate);
							inputMap.put(lObjValidPcodeVO.getDaRateForState() + "_DP_" + lCalculatedBasicPensionAmt + "_" + lPayStartDate, lPensionHeadcodeRateVO);
						}
						if (lPensionHeadcodeRateVO != null) {
							lDPPercent = (lPensionHeadcodeRateVO.getRate() != null) ? lPensionHeadcodeRateVO.getRate().doubleValue() : 0;
							lDPAmount = (lCalculatedBasicPensionAmt) * (lDPPercent / 100);
							lBgDcmlDPAmount = new BigDecimal(lDPAmount).setScale(0, BigDecimal.ROUND_HALF_UP);
							lDPAmount = lBgDcmlDPAmount.doubleValue();
							/*
							 * } else { throw new
							 * Exception("DP Rate is not configured for headcode "
							 * + lObjValidPcodeVO.getHeadCode()); }
							 */
							// lCalculatedBasicPensionAmt =
							// lCalculatedBasicPensionAmt + lDPAmount;
						}

						/* Get DA -TI Rate & Amount */// With DP Flag is =
						// _DA_1996_DP_
						// lPensionHeadcodeRateVO = (RltPensionHeadcodeRate)
						// inputMap.get(lObjValidPcodeVO.getHeadCode() +
						// "_DA_1996_DP_" + lCalculatedBasicPensionAmt + "_" +
						// lPayStartDate);
						// if (lPensionHeadcodeRateVO == null) {
						// lPensionHeadcodeRateVO =
						// lPensionBillDAO.getRateFromHeadcodeRateRlt(lObjValidPcodeVO.getHeadCode(),
						// "DA_1996_DP", lCalculatedBasicPensionAmt,
						// lPayStartDate);
						// inputMap.put(lObjValidPcodeVO.getHeadCode() +
						// "_DA_1996_DP_" + lCalculatedBasicPensionAmt + "_" +
						// lPayStartDate, lPensionHeadcodeRateVO);
						// }
						// if (lPensionHeadcodeRateVO != null) {
						// lDAPercent = (lPensionHeadcodeRateVO.getRate() !=
						// null) ?
						// lPensionHeadcodeRateVO.getRate().doubleValue() : 0;
						// lDAAmount = (lCalculatedBasicPensionAmt + lDPAmount)
						// *
						// (lDAPercent / 100);
						// }
						/** Get DA Rate & Amount Over **/
					}
					// else {
					// /* Get DA -TI Rate & Amount */// Without DP Flag is =
					// // _DA_1996_
					// lPensionHeadcodeRateVO = (RltPensionHeadcodeRate)
					// inputMap.get(lObjValidPcodeVO.getHeadCode() + "_DA_1996_"
					// +
					// lCalculatedBasicPensionAmt + "_" + lPayStartDate);
					// if (lPensionHeadcodeRateVO == null) {
					// lPensionHeadcodeRateVO =
					// lPensionBillDAO.getRateFromHeadcodeRateRlt(lObjValidPcodeVO.getHeadCode(),
					// "DA_1996", lCalculatedBasicPensionAmt, lPayStartDate);
					// inputMap.put(lObjValidPcodeVO.getHeadCode() + "_DA_1996_"
					// +
					// lCalculatedBasicPensionAmt + "_" + lPayStartDate,
					// lPensionHeadcodeRateVO);
					// }
					// if (lPensionHeadcodeRateVO != null) {
					// lDAPercent = (lPensionHeadcodeRateVO.getRate() != null) ?
					// lPensionHeadcodeRateVO.getRate().doubleValue() : 0;
					// lDAAmount = (lCalculatedBasicPensionAmt) * (lDAPercent /
					// 100);
					// // lDblGrossAmount = lDblGrossAmount + lDAAmount;
					// }
					// /** Get DA Rate & Amount Over **/
					// }
					/* Get DA -TI Rate & Amount */// With DP Flag is =
					// _DA_1996_DP_
					lPensionHeadcodeRateVO = (RltPensionHeadcodeRate) inputMap.get(lObjValidPcodeVO.getDaRateForState() + "_DA_1996_DP_" + lCalculatedBasicPensionAmt + "_" + lPayStartDate);
					if (lPensionHeadcodeRateVO == null) {
						lPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lObjValidPcodeVO.getDaRateForState(), "DA_1996_DP", lCalculatedBasicPensionAmt, lPayStartDate);
						inputMap.put(lObjValidPcodeVO.getDaRateForState() + "_DA_1996_DP_" + lCalculatedBasicPensionAmt + "_" + lPayStartDate, lPensionHeadcodeRateVO);
					}
					if (lPensionHeadcodeRateVO != null) {
						lDAPercent = (lPensionHeadcodeRateVO.getRate() != null) ? lPensionHeadcodeRateVO.getRate().doubleValue() : 0;
						lDAAmount = (lCalculatedBasicPensionAmt + lDPAmount) * (lDAPercent / 100);
					}
				} else if (lStrROPPaid != null && "2006".equals(lStrROPPaid)) {
					// ADP Calculation
					/* Get ADA Rate & Amount */
					lPensionHeadcodeRateVO = (RltPensionHeadcodeRate) inputMap.get(lObjValidPcodeVO.getDaRateForState() + "_DA_2006_" + lCalculatedBasicPensionAmt + "_" + lPayStartDate);
					if (lPensionHeadcodeRateVO == null) {
						lPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lObjValidPcodeVO.getDaRateForState(), "DA_2006", lCalculatedBasicPensionAmt, lPayStartDate);
						inputMap.put(lObjValidPcodeVO.getDaRateForState() + "_DA_2006_" + lCalculatedBasicPensionAmt + "_" + lPayStartDate, lPensionHeadcodeRateVO);
					}
					if (lPensionHeadcodeRateVO != null) {
						lDAPercent = (lPensionHeadcodeRateVO.getRate() != null) ? lPensionHeadcodeRateVO.getRate().doubleValue() : 0;
						// lADPamount = getADPAmount(inputMap);
						lBgDcmlADPAmount = new BigDecimal(lADPamount).setScale(BigDecimal.ROUND_UP, 0);
						lADPamount = lBgDcmlADPAmount.doubleValue();
						lDAAmount = (lCalculatedBasicPensionAmt + lADPamount) * (lDAPercent / 100);
						// lDblGrossAmount = lDblGrossAmount + lDAAmount;
					}

					/** Get ADA Rate & Amount Over **/
				}

				/** ROP Wise Amount Calculation Over ****************************************************************************/

				/** Get CVP Monthly for current month starts ********************************************************************/
				/*
				 * Map lCVPDtlMap = (Map) inputMap.get("lCVPDtlMap"); if
				 * (lCVPDtlMap != null) { lDblObj = (Double)
				 * lCVPDtlMap.get(lStrPnsnrCode); if (lDblObj != null) {
				 * lDblMonthlyCommutationAmt = lDblObj; } } else {
				 * lDblMonthlyCommutationAmt =
				 * lObjMnthlyBillDAO.getMonthlyCommutationAmount(lStrPnsnrCode,
				 * lCurrentyyyyMM); } lDblMonthlyCommutationAmt =
				 * lDblMonthlyCommutationAmt (lBasicPayDays /
				 * lTotalDaysofMonth); lDblMonthlyCommutationAmt =
				 * lDblMonthlyCommutationAmt (lBasicPayDays /
				 * lTotalDaysofMonth);
				 */

				Map<String, List<TrnCvpRestorationDtls>> lCVPDtlMap = (Map<String, List<TrnCvpRestorationDtls>>) inputMap.get("lCVPDtlMap");
				if (lCVPDtlMap != null) {
					// ---For monthly pension
					lLstTrnCvpRestorationDtlsObj = lCVPDtlMap.get(lStrPnsnrCode);
				} else {
					// ---For first pension
					lLstTrnCvpRestorationDtlsObj = lObjMnthlyBillDAO.getMonthlyCommutationAmount(lStrPnsnrCode, lCurrentyyyyMM);
				}

				if (lLstTrnCvpRestorationDtlsObj != null) {
					for (TrnCvpRestorationDtls lObjTrnCvpRestorationDtls : lLstTrnCvpRestorationDtlsObj) {
						lDtCVPFrom = lObjTrnCvpRestorationDtls.getFromDate();
						lDtCVPTo = lObjTrnCvpRestorationDtls.getToDate();
						lStrRestorationRcvFlg = lObjTrnCvpRestorationDtls.getRestnAplnReceivedFlag();
						if (lDtCVPFrom != null && lDtCVPTo != null) {
							lIntCVPFrmDt = Integer.valueOf(lSdfMonthYear.format(lDtCVPFrom));
							lIntCVPToDt = Integer.valueOf(lSdfMonthYear.format(lDtCVPTo));

							if (lDeathDate != null) {
								if (lDeathYYYYMM == lCurrentyyyyMM) {
									// --In current month:cvp from date,cvp to
									// date,death
									// date,pay end date(may be)
									if (lIntCVPFrmDt.equals(lCurrentyyyyMM) && lIntCVPToDt.equals(lCurrentyyyyMM)) {
										lDtCVPEndDt = lDeathDate.before(lPayEndDate) ? lDeathDate : lPayEndDate;
										// --If restoration flag is Y then
										// restoring commutation upto cvpToDate.
										if (lStrRestorationRcvFlg != null && "Y".equals(lStrRestorationRcvFlg)) {
											lDtCVPEndDt = lDtCVPEndDt.before(lDtCVPTo) ? lDtCVPEndDt : lDtCVPTo;
										}
										lCvpDays = getDaysDifference(lDtCVPFrom, lDtCVPEndDt);
									}
									// --In current month:cvp from date,death
									// date,pay end date(may be)
									else if (lIntCVPFrmDt.equals(lCurrentyyyyMM)) {
										lDtCVPEndDt = lDeathDate.before(lPayEndDate) ? lDeathDate : lPayEndDate;
										lCvpDays = getDaysDifference(lDtCVPFrom, lDtCVPEndDt);
									}
									// --In current month:cvp to date,death
									// date,pay end date(may be)
									else if (lIntCVPToDt.equals(lCurrentyyyyMM)) {
										lDtCVPEndDt = lDeathDate.before(lPayEndDate) ? lDeathDate : lPayEndDate;
										// --If restoration flag is Y then
										// restoring commutation upto cvpToDate.
										if (lStrRestorationRcvFlg != null && "Y".equals(lStrRestorationRcvFlg)) {
											lDtCVPEndDt = lDtCVPEndDt.before(lDtCVPTo) ? lDtCVPEndDt : lDtCVPTo;
										}
										lCvpDays = getDaysDifference(lPayStartDate, lDtCVPEndDt);
									}
									// ---In current month:death date,pay end
									// date(may
									// be)
									else if ((lCurrentyyyyMM > lIntCVPFrmDt) && (lCurrentyyyyMM < lIntCVPToDt)) {
										lDtCVPEndDt = lDeathDate.before(lPayEndDate) ? lDeathDate : lPayEndDate;
										lCvpDays = getDaysDifference(lPayStartDate, lDtCVPEndDt);
									}
								} else {
									// --If death date is passed then not
									// cutting any cvp
									// amount.
									if (lDeathYYYYMM < lCurrentyyyyMM) {
										lCvpDays = 0;
									}
								}
							} else {
								// --In current month:cvp from date, cvp to
								// date,pay
								// end
								// date(may be)
								if (lIntCVPFrmDt.equals(lCurrentyyyyMM) && lIntCVPToDt.equals(lCurrentyyyyMM)) {
									// lDtCVPEndDt =
									// lPayEndDate.before(lDtCVPTo) ?
									// lPayEndDate : lDtCVPTo;
									lDtCVPEndDt = lPayEndDate; // considering
																// cvp end date
																// as pay end
																// date in all
																// cases.(suggession
																// during UAT.)
									// --If restoration flag is Y then restoring
									// commutation upto cvpToDate.
									if (lStrRestorationRcvFlg != null && "Y".equals(lStrRestorationRcvFlg)) {
										lDtCVPEndDt = lDtCVPEndDt.before(lDtCVPTo) ? lDtCVPEndDt : lDtCVPTo;
									}
									lCvpDays = getDaysDifference(lDtCVPFrom, lDtCVPEndDt);
								}
								// --In current month:cvp from date,pay end
								// date(may be)
								else if (lIntCVPFrmDt.equals(lCurrentyyyyMM)) {
									lCvpDays = getDaysDifference(lDtCVPFrom, lPayEndDate);
								}
								// --In current month:cvp to date,pay end
								// date(may be)
								else if (lIntCVPToDt.equals(lCurrentyyyyMM)) {
									// lDtCVPEndDt =
									// lDtCVPTo.before(lPayEndDate) ? lDtCVPTo :
									// lPayEndDate;
									lDtCVPEndDt = lPayEndDate;// considering
									// cvp end date
									// as pay end
									// date in all
									// cases.(suggession during UAT.)
									// --If restoration flag is Y then restoring
									// commutation upto cvpToDate.
									if (lStrRestorationRcvFlg != null && "Y".equals(lStrRestorationRcvFlg)) {
										lDtCVPEndDt = lDtCVPEndDt.before(lDtCVPTo) ? lDtCVPEndDt : lDtCVPTo;
									}
									lCvpDays = getDaysDifference(lPayStartDate, lDtCVPEndDt);
								}
								// ---In current month:pay end date(may be)
								else if ((lCurrentyyyyMM > lIntCVPFrmDt) && (lCurrentyyyyMM < lIntCVPToDt)) {
									lCvpDays = getDaysDifference(lPayStartDate, lPayEndDate);
								}
								// ---Cvp Restoration To date is less than
								// current
								// month.
								else if ((lCurrentyyyyMM > lIntCVPFrmDt) && (lCurrentyyyyMM > lIntCVPToDt)) {

									// Mubeen Ahmed 26 Nov 2012 - Start
									/*  This is added if Restoration Received Flag is "Yes"  
									and period of CVP deduction has already gone 
									i.e. CVP to Date (End Date) is less than Today date */
									if (lStrRestorationRcvFlg != null && "Y".equals(lStrRestorationRcvFlg)) {
										lCvpDays = 0;
									} else {
										lCvpDays = getDaysDifference(lPayStartDate, lPayEndDate);
									}
									// Mubeen Ahmed 26 Nov 2012 - End

								}
							}
						} else {
							/**
							 * For Migration Data :: If from date and to date is
							 * null and amount is more than 0. then also cut CVP
							 * amount from pension.
							 **/
							lCvpDays = (int) lTotalDaysofMonth;
							if (lDeathDate != null) {
								if (lDeathYYYYMM <= lCurrentyyyyMM) {
									lCvpDays = 0;
								}
							}
						}
						lDblMonthlyCommutationAmt = lDblMonthlyCommutationAmt + ((lCvpDays / lTotalDaysofMonth) * lObjTrnCvpRestorationDtls.getAmount().doubleValue());
					}
				}

				/** Get CVP Monthly for current month ends **********************************************************************/

				/** --Calculation for re-employment starts *********************************************************************/
				// ---If current month is between reemployment from and to date,
				// and
				// daInSalaryPension is Salary
				// then not
				// giving da component for re-employment period
				Date lDtReEmpFrom = lObjValidPcodeVO.getReEmploymentFromDate();
				Date lDtReEmpTo = lObjValidPcodeVO.getReEmploymentToDate();
				if ("Y".equals(lObjValidPcodeVO.getReEmploymentFlag())) {
					if (lDtReEmpFrom != null) {
						int lDAPayDays = 0;
						Integer lIntReEmpFrom = Integer.valueOf(lSdfMonthYear.format(lDtReEmpFrom));
						Integer lIntReEmpTo = 0;
						if (lDtReEmpTo != null) {
							lIntReEmpTo = Integer.valueOf(lSdfMonthYear.format(lDtReEmpTo));
						} else {
							lDtReEmpTo = lMonthEndDt;
							lIntReEmpTo = lCurrentyyyyMM;

						}
						if ((lCurrentyyyyMM >= lIntReEmpFrom) && (lCurrentyyyyMM <= lIntReEmpTo)) {
							if (bundleCaseConst.getString("REEMP.DAINSALARY").equals(lObjValidPcodeVO.getDaInPensionSalary())) {
								if (lCurrentyyyyMM.equals(lIntReEmpFrom) && lCurrentyyyyMM.equals(lIntReEmpTo)) {
									lDAPayDays = (int) lTotalDaysofMonth - getDaysDifference(lDtReEmpFrom, lDtReEmpTo);
								} else if (lCurrentyyyyMM.equals(lIntReEmpFrom)) {
									lDAPayDays = (int) lTotalDaysofMonth - getDaysDifference(lDtReEmpFrom, lMonthEndDt);
								} else if (lCurrentyyyyMM.equals(lIntReEmpTo)) {
									lDAPayDays = (int) lTotalDaysofMonth - getDaysDifference(lMonthStartDt, lDtReEmpTo);
								}
								lDAAmount = lDAAmount * (lDAPayDays / lTotalDaysofMonth);
							}
						}
					} else {
						lDAAmount = 0; // --For migrated cases re-employment
						// from
						// and to date is not available.So if
						// re-employment flag is Y then not giving
						// da amount.
					}

				}
				/** --Calculation for re-employment ends *********************************************************************/
			}

			/** Get Recovery for current month starts ***********************************************************************/
			// ---Not deducting recovery amount for injury and gallantry pension
			// type.
			if (!bundleCaseConst.getString("PPMT.INJURY").equals(lStrPnsnType) && !bundleCaseConst.getString("PPMT.GALLANTRY").equals(lStrPnsnType)) {
				String lStrRcvryFlag = "Monthly";
				Map lRcvDtlMap = (Map) inputMap.get("lRcvDtlMap");
				if (lRcvDtlMap != null) {
					// ---For monthly pension
					lDblObj = (Double) lRcvDtlMap.get(lStrPnsnrCode);
					if (lDblObj != null) {
						lDblRecoveryAmt = lDblObj;
					}
				} else {
					// ---For first pension
					lDblRecoveryAmt = lObjMnthlyBillDAO.getRecoveryDtls(lStrPnsnrCode, lStrRcvryFlag, Integer.valueOf(lCurrentyyyyMM).toString());
				}

				// ---Do not remove this code .Commented for migration.(as per
				// client data recovery will be cut from pension for family
				// pension
				// case also.).
				// --There will be no recovery after pensioner death.
				// lDblRecoveryAmt = lDblRecoveryAmt * (lBasicPayDays /
				// lTotalDaysofMonth);
			}
			/** Get Recovery for current month ends *************************************************************************/

			/** Get Arrear amount for the month starts **********************************************************************/
			Map lArrDtlMap = (Map) inputMap.get("lArrDtlMap");
			if (lArrDtlMap != null) {
				// ---For monthly pension
				lDblObj = (Double) lArrDtlMap.get(lStrPnsnrCode + "~TI");
				if (lDblObj != null) {
					lDblDaArrearAmt = lDblObj;
				}
				lDblObj = (Double) lArrDtlMap.get(lStrPnsnrCode + "~LC");
				if (lDblObj != null) {
					lDblLCArrAmt = lDblObj;
				}
				lDblObj = (Double) lArrDtlMap.get(lStrPnsnrCode + "~DA");
				if (lDblObj != null) {
					lDblDAOthrArrAmt = lDblObj;
				}
				lDblObj = (Double) lArrDtlMap.get(lStrPnsnrCode + "~6PC");
				if (lDblObj != null) {
					lDbl6PCArrAmt = lDblObj;
				}
				lDblObj = (Double) lArrDtlMap.get(lStrPnsnrCode + "~COMM");
				if (lDblObj != null) {
					lDblCommutationArrAmt = lDblObj;
				}
				lDblObj = (Double) lArrDtlMap.get(lStrPnsnrCode + "~OTHRDIFF");
				if (lDblObj != null) {
					lDblOthrDiffArrAmt = lDblObj;
				}
				lDblObj = (Double) lArrDtlMap.get(lStrPnsnrCode + "~PENSION");
				if (lDblObj != null) {
					lDblPnsnArrearAmt = lDblObj;
				}
				// lDblObj = (Double) lArrDtlMap.get(lStrPnsnrCode + "~Othr");
				// if (lDblObj != null) {
				// lDblOthrArrearAmt = lDblObj;
				// }
			} else {
				// ---For first pension
				List lLstArrearDtl = lObjMnthlyBillDAO.getArrearDtlsByPnsrCode(lStrPnsnrCode, lCurrentyyyyMM);
				if (lLstArrearDtl != null && lLstArrearDtl.size() > 0) {
					for (Object lObj : lLstArrearDtl) {
						Object[] lArrObj = (Object[]) lObj;
						if (lArrObj[0] != null && lArrObj[1] != null) {
							if ("TI".equals(lArrObj[0])) {
								lDblDaArrearAmt = Double.valueOf(lArrObj[1].toString());
							} else if (bundleCaseConst.getString("ARREARTYPE.LC").equals(lArrObj[0])) {
								lDblLCArrAmt = Double.valueOf(lArrObj[1].toString());
							} else if (bundleCaseConst.getString("ARREARTYPE.DA").equals(lArrObj[0])) {
								lDblDAOthrArrAmt = Double.valueOf(lArrObj[1].toString());
							} else if (bundleCaseConst.getString("ARREARTYPE.6PC").equals(lArrObj[0])) {
								lDbl6PCArrAmt = Double.valueOf(lArrObj[1].toString());
							} else if (bundleCaseConst.getString("ARREARTYPE.COMPNSN").equals(lArrObj[0])) {
								lDblCommutationArrAmt = Double.valueOf(lArrObj[1].toString());
							} else if (bundleCaseConst.getString("ARREARTYPE.OTHERDIFF").equals(lArrObj[0])) {
								lDblOthrDiffArrAmt = Double.valueOf(lArrObj[1].toString());
							} else if (bundleCaseConst.getString("ARREARTYPE.PENSION").equals(lArrObj[0])) {
								lDblPnsnArrearAmt = Double.valueOf(lArrObj[1].toString());
							}
							// else {
							// lDblOthrArrearAmt =
							// Double.valueOf(lArrObj[1].toString());
							// }
						}
					}
				}
			}
			// --Total DA Arrear amount = da arrear amount set in pension case +
			// da arrear amount arised due to da rate configuration.
			lDblDaArrearAmt = lDblDaArrearAmt + lDblDAOthrArrAmt;
			// ----Six pay arrear starts <<<<<<
			Map lSixPayArrearDtlMap = (Map) inputMap.get("lSixPayArrearDtlMap");
			if (lSixPayArrearDtlMap != null) {
				// --For monthly pension
				lDblObj = (Double) lSixPayArrearDtlMap.get(lStrPnsnrCode);
				if (lDblObj != null) {
					lDblSixPayArrearAmt = lDblObj;
				}
			}
			// ----Six pay arrear ends >>>>>>>>>
			// lDblOthrArrearAmt = lDblOthrArrearAmt + lDblSixPayArrearAmt +
			// lBgDcmlArrearAmount.doubleValue();
			// lDblPnsnArrearAmt = lDblPnsnArrearAmt +
			// lBgDcmlArrearAmount.doubleValue();
			lDbl6PCArrAmt = lDbl6PCArrAmt + lDblSixPayArrearAmt;
			/** Get Arrear amount for the month ends ************************************************************************/

			// ---Setting pension cut amount if it is in bill generation month
			// year
			if (lObjValidPcodeVO.getPensionCutMonthyear() != null && lObjValidPcodeVO.getPensionCutAmount() != null) {
				if (lObjValidPcodeVO.getPensionCutMonthyear().equals(lCurrentyyyyMM)) {
					lDblPensionCutAmt = lObjValidPcodeVO.getPensionCutAmount().doubleValue();
				}
			}
			lBgDcmlBasicPnsnAmount = new BigDecimal(lCalculatedBasicPensionAmt).setScale(0, BigDecimal.ROUND_HALF_UP);
			lBgDcmlDAAmount = new BigDecimal(lDAAmount).setScale(0, BigDecimal.ROUND_UP);
			lBgDcmlCvpMonthlyAmount = new BigDecimal(lDblMonthlyCommutationAmt).setScale(0, BigDecimal.ROUND_HALF_UP);
			lBgDcmlRecoveryAmount = new BigDecimal(lDblRecoveryAmt).setScale(0, BigDecimal.ROUND_HALF_UP);
			lBgDcmlDaArrearAmount = new BigDecimal(lDblDaArrearAmt).setScale(0, BigDecimal.ROUND_HALF_UP);
			lBgPnsnArrearAmount = new BigDecimal(lDblPnsnArrearAmt).setScale(0, BigDecimal.ROUND_HALF_UP);
			lBgDcmlIr1Amount = new BigDecimal(lDblIR1Amt).setScale(0, BigDecimal.ROUND_HALF_UP);
			lBgDcmlIr2Amount = new BigDecimal(lDblIR2Amt).setScale(0, BigDecimal.ROUND_HALF_UP);
			lBgDcmlIr3Amount = new BigDecimal(lDblIR3Amt).setScale(0, BigDecimal.ROUND_HALF_UP);
			lBgDcmlDARate = new BigDecimal(lDAPercent);
			lBgDcmlPeonAllowanceAmt = new BigDecimal(lDblPeonAllowanceAmt).setScale(0, BigDecimal.ROUND_HALF_UP);
			lBgDcmlMAAmt = new BigDecimal(lDblMAAmt).setScale(0, BigDecimal.ROUND_HALF_UP);
			lBgDcmlGallantryAmt = new BigDecimal(lDblGallantryAmt).setScale(0, BigDecimal.ROUND_HALF_UP);
			lBgDcmlOthrBenefit = new BigDecimal(lDblOthrBenefitAmt).setScale(0, BigDecimal.ROUND_HALF_UP);
			lBgDcmlPensionCutAmt = new BigDecimal(lDblPensionCutAmt).setScale(0, BigDecimal.ROUND_HALF_UP);
			lBgDcmlLCArrAmt = new BigDecimal(lDblLCArrAmt).setScale(0, BigDecimal.ROUND_HALF_UP);
			lBgDcml6PCArrAmt = new BigDecimal(lDbl6PCArrAmt).setScale(0, BigDecimal.ROUND_HALF_UP);
			lBgDcmlOthrDiffArrAmt = new BigDecimal(lDblOthrDiffArrAmt).setScale(0, BigDecimal.ROUND_HALF_UP);
			lBgDcmlCommutationArrAmt = new BigDecimal(lDblCommutationArrAmt).setScale(0, BigDecimal.ROUND_HALF_UP);
			lBgDcmlMonthlyPunishmentCutAmt = new BigDecimal(lDblMonthlyPunishmentCutAmt).setScale(0, BigDecimal.ROUND_HALF_UP);
			// lBgDcmlDAOthrArrAmt = new
			// BigDecimal(lDblDAOthrArrAmt).setScale(0,
			// BigDecimal.ROUND_HALF_UP);
			lBgDcmlTotalArrearAmt = new BigDecimal(lBgPnsnArrearAmount.doubleValue() + lBgDcmlDaArrearAmount.doubleValue() + lBgDcmlPeonAllowanceAmt.doubleValue() + lBgDcmlMAAmt.doubleValue()
					+ lBgDcmlGallantryAmt.doubleValue() + lBgDcmlOthrBenefit.doubleValue() + lBgDcmlLCArrAmt.doubleValue() + lBgDcml6PCArrAmt.doubleValue() + lBgDcmlOthrDiffArrAmt.doubleValue()
					+ lBgDcmlCommutationArrAmt.doubleValue());

			lDblGrossAmount = lBgDcmlBasicPnsnAmount.doubleValue() + lBgDcmlDAAmount.doubleValue() + lBgDcmlDPAmount.doubleValue() + lBgDcmlADPAmount.doubleValue() + lBgDcmlIr1Amount.doubleValue()
					+ lBgDcmlIr2Amount.doubleValue() + lBgDcmlIr3Amount.doubleValue() + lBgDcmlTotalArrearAmt.doubleValue() - lBgDcmlCvpMonthlyAmount.doubleValue()
					- lBgDcmlPensionCutAmt.doubleValue();

			lDblPensionNetAmount = lDblGrossAmount - lBgDcmlRecoveryAmount.doubleValue();
			lBgDcmlGrossAmount = new BigDecimal(lDblGrossAmount);
			lBgDcmlNetPensionAmount = new BigDecimal(lDblPensionNetAmount);

			Double lDblReducedPnsnAmount = lBgDcmlBasicPnsnAmount.doubleValue() + lBgDcmlDPAmount.doubleValue() - lBgDcmlCvpMonthlyAmount.doubleValue();

			lDblTotalAllocationAmt = lBgDcmlBasicPnsnAmount.doubleValue() - lBgDcmlCvpMonthlyAmount.doubleValue();
			lDblAllcBf36Amt = lDblTotalAllocationAmt * (lObjValidPcodeVO.getOrgBf10436Percent().doubleValue() / 100);
			lDblAllcAf36Amt = lDblTotalAllocationAmt * (lObjValidPcodeVO.getOrgAf10436Percent().doubleValue() / 100);
			lDblAllcAf56Amt = lDblTotalAllocationAmt * (lObjValidPcodeVO.getOrgAf11156Percent().doubleValue() / 100);
			lDblAllcAf60Amt = lDblTotalAllocationAmt * (lObjValidPcodeVO.getOrgAf10560Percent().doubleValue() / 100);
			lDblAllcAfZPAmt = lDblTotalAllocationAmt * (lObjValidPcodeVO.getOrgZpAfPercent().doubleValue() / 100);
			if (lDeathDate != null) {
				lMstPensionerFamilyDtlsVO = (MstPensionerFamilyDtls) inputMap.get("lMstPensionerFamilyDtlsVO");
				if (lMstPensionerFamilyDtlsVO != null) {
					lStrPnsrName = lMstPensionerFamilyDtlsVO.getName();
				} else {
					lStrPnsrName = ((lObjValidPcodeVO.getFirstName() != null) ? lObjValidPcodeVO.getFirstName() : "") + " ";
					// lStrPnsrName += ((lObjValidPcodeVO.getMiddleName() !=
					// null) ? lObjValidPcodeVO.getMiddleName() : "") + " ";
					// lStrPnsrName += (lObjValidPcodeVO.getLastName() != null)
					// ? lObjValidPcodeVO.getLastName() : "";
				}
			} else {
				lStrPnsrName = ((lObjValidPcodeVO.getFirstName() != null) ? lObjValidPcodeVO.getFirstName() : "") + " ";
				// lStrPnsrName += ((lObjValidPcodeVO.getMiddleName() != null) ?
				// lObjValidPcodeVO.getMiddleName() : "") + " ";
				// lStrPnsrName += (lObjValidPcodeVO.getLastName() != null) ?
				// lObjValidPcodeVO.getLastName() : "";
			}

			// -----Preparing list of pensioners having net amount less than
			// zero.
			if (lDblPensionNetAmount < 0) {
				if (lMapProbPPOBranch != null) {
					lLstProbPpoNos = lMapProbPPOBranch.get(lObjValidPcodeVO.getBranchCode());
					if (lLstProbPpoNos != null) {
						lLstProbPpoNos.add(lObjValidPcodeVO.getPpoNo());
					} else {
						lLstProbPpoNos = new ArrayList<String>();
						lLstProbPpoNos.add(lObjValidPcodeVO.getPpoNo());
					}
					lMapProbPPOBranch.put(lObjValidPcodeVO.getBranchCode(), lLstProbPpoNos);
				}
			}

			MonthlyPensionBillVO lMonthlyPensionBillVO = new MonthlyPensionBillVO();
			lMonthlyPensionBillVO.setPpoNo(lObjValidPcodeVO.getPpoNo());
			lMonthlyPensionBillVO.setPensionerCode(lObjValidPcodeVO.getPensionerCode());
			lMonthlyPensionBillVO.setPensionerName(lStrPnsrName);
			lMonthlyPensionBillVO.setAccountNo(lObjValidPcodeVO.getAcountNo());
			lMonthlyPensionBillVO.setForMonth(lCurrentyyyyMM);
			lMonthlyPensionBillVO.setHeadCode(new BigDecimal(lObjValidPcodeVO.getHeadCode()));

			lMonthlyPensionBillVO.setBasicPensionAmount(lBgDcmlBasicPnsnAmount);
			lMonthlyPensionBillVO.setDpPercent(new BigDecimal(lDPPercent));
			lMonthlyPensionBillVO.setDpPercentAmount(lBgDcmlDPAmount);
			lMonthlyPensionBillVO.setTiPercent(lBgDcmlDARate);
			lMonthlyPensionBillVO.setAdpAmount(lBgDcmlADPAmount);
			lMonthlyPensionBillVO.setTiPercentAmount(lBgDcmlDAAmount);
			lMonthlyPensionBillVO.setCvpMonthlyAmount(lBgDcmlCvpMonthlyAmount);
			lMonthlyPensionBillVO.setTIArrearsAmount(lBgDcmlDaArrearAmount);
			lMonthlyPensionBillVO.setOtherArrearsAmount(lBgPnsnArrearAmount);
			lMonthlyPensionBillVO.setIr1Amt(lBgDcmlIr1Amount);
			lMonthlyPensionBillVO.setIr2Amt(lBgDcmlIr2Amount);
			lMonthlyPensionBillVO.setIr3Amt(lBgDcmlIr3Amount);
			lMonthlyPensionBillVO.setGrossAmount(lBgDcmlGrossAmount);
			lMonthlyPensionBillVO.setRecoveryAmount(lBgDcmlRecoveryAmount);
			lMonthlyPensionBillVO.setNetPensionAmount(lBgDcmlNetPensionAmount);
			lMonthlyPensionBillVO.setReducedPension(new BigDecimal(lDblReducedPnsnAmount));
			lMonthlyPensionBillVO.setAllnBf11136(new BigDecimal(lDblAllcBf36Amt).setScale(0, BigDecimal.ROUND_HALF_UP));
			lMonthlyPensionBillVO.setAllnBf11156(new BigDecimal(lDblAllcAf36Amt).setScale(0, BigDecimal.ROUND_HALF_UP));
			lMonthlyPensionBillVO.setAllnAf11156(new BigDecimal(lDblAllcAf56Amt).setScale(0, BigDecimal.ROUND_HALF_UP));
			lMonthlyPensionBillVO.setAllnAf10560(new BigDecimal(lDblAllcAf60Amt).setScale(0, BigDecimal.ROUND_HALF_UP));
			lMonthlyPensionBillVO.setAllnAfZp(new BigDecimal(lDblAllcAfZPAmt).setScale(0, BigDecimal.ROUND_HALF_UP));
			lMonthlyPensionBillVO.setFromDate(lPayStartDate);
			lMonthlyPensionBillVO.setToDate(lPayEndDate);
			lMonthlyPensionBillVO.setLedgerNo(lObjValidPcodeVO.getLedgerNo());
			lMonthlyPensionBillVO.setPageNo(lObjValidPcodeVO.getPageNo());
			lMonthlyPensionBillVO.setPeonAllowanceAmt(lObjValidPcodeVO.getPeonAllowanceAmount());
			lMonthlyPensionBillVO.setMedicalAllowenceAmount(lObjValidPcodeVO.getMedicalAllowenceAmount());
			lMonthlyPensionBillVO.setGallantryAmt(lObjValidPcodeVO.getOther1());
			lMonthlyPensionBillVO.setOtherBenefit(lObjValidPcodeVO.getOther2());
			lMonthlyPensionBillVO.setPensionCutAmount(lBgDcmlPensionCutAmt);
			lMonthlyPensionBillVO.setTotalArrearAmt(lBgDcmlTotalArrearAmt);
			lMonthlyPensionBillVO.setRopType(lObjValidPcodeVO.getRopType());
			lMonthlyPensionBillVO.setArrear6PC(lBgDcml6PCArrAmt);
			lMonthlyPensionBillVO.setArrearCommutation(lBgDcmlCommutationArrAmt);
			lMonthlyPensionBillVO.setArrearLC(lBgDcmlLCArrAmt);
			lMonthlyPensionBillVO.setArrearOthrDiff(lBgDcmlOthrDiffArrAmt);
			lMonthlyPensionBillVO.setPunishmentCutAmt(lBgDcmlMonthlyPunishmentCutAmt);
			// lMonthlyPensionBillVO.setIr(new BigDecimal(lIRAmount).setScale(0,
			// BigDecimal.ROUND_HALF_UP));

			// lMonthlyPensionBillVO.setMedicalAllowenceAmount(lObj.getMedicalAllowenceAmount());
			// lMonthlyPensionBillVO.setItCutAmount(lObj.getIncomeTaxCutAmount());
			// lMonthlyPensionBillVO.setSpecialCutAmount(lObj.getSpecialCutAmount());
			// lMonthlyPensionBillVO.setOtherBenefit(lObj.getOtherBenefits());
			// lMonthlyPensionBillVO.setOMR(lObj.getOmr());
			// lMonthlyPensionBillVO.setPensionCutAmount(lObj.getPensnCutAmount());
			// lMonthlyPensionBillVO.setPersonalPension();
			// lMonthlyPensionBillVO.setMOComm(lObj.getMoCommission());

			inputMap.put("lMapProbPPOBranch", lMapProbPPOBranch);
			inputMap.put("lMapNoFPDatesPpoBranch", lMapNoFPDatesPpoBranch);
			inputMap.put("lMonthlyPensionBillVO", lMonthlyPensionBillVO);
			inputMap.put("PnsrCrntDate", gDate);
			inputMap.put("CrntMonth", lCurrentyyyyMM);
			inputMap.put("FromPayDate", new SimpleDateFormat("dd/MM/yyyy").format(lPayStartDate));
			inputMap.put("UptoBillCrtDate", new SimpleDateFormat("dd/MM/yyyy").format(lPayEndDate));

		} catch (Exception e) {
			// e.printStackTrace();
			throw e;
		}
		return inputMap;
	}

	public double getADPAmount(Map inputMap) throws Exception {

		ValidPcodeView lObjValidPcodeVO = (ValidPcodeView) inputMap.get("lObjValidPcode");
		Date lCurrentDate = (Date) inputMap.get("lCurrentDate");
		Date lMonthStartDt = (Date) inputMap.get("lMonthStartDt");
		Date lMonthEndDate = (Date) inputMap.get("lMonthEndDt");
		Date lPayStartDate = lMonthStartDt; // First Date of month
		Date lPayEndDate = lMonthEndDate; // Last Date of month

		Date lPPOEndDate = lObjValidPcodeVO.getEndDate();
		Date lCommDate = lObjValidPcodeVO.getCommensionDate();
		Date lBirthDate = lObjValidPcodeVO.getDateOfBirth();
		Date lDeathDate = lObjValidPcodeVO.getDateOfDeath();

		int lCurrentyyyyMM = Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lCurrentDate));
		int lCommyyyyMM = Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lCommDate));
		int lPPOEndYYYYMM = lPPOEndDate != null ? Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lPPOEndDate)) : 0;
		int lDeathYYYYMM = lDeathDate != null ? Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lDeathDate)) : 0;

		Double ldBasicAmount = (Double) inputMap.get("lDblCalculatedBasicAmtForPnsr");
		Double ldFP1andFP2Amount = (Double) inputMap.get("lDblCalculatedBasicAmtForFp");

		// Double ldBasicAmount = (Double)
		// inputMap.get("lCalculatedBasicPensionAmt");
		// double ldFP1andFP2Amount = 100d;
		double ldADPAmount = 0;

		try {
			if (lCommyyyyMM == lCurrentyyyyMM) {
				lPayStartDate = lCommDate.after(lMonthStartDt) ? lCommDate : lMonthStartDt; // which
				// ever
				// is
				// later
			} else {
				lPayStartDate = lMonthStartDt;
			}

			if (lPPOEndDate != null) {
				if (lPPOEndYYYYMM == lCurrentyyyyMM) {
					lPayEndDate = lPPOEndDate.after(lMonthEndDate) ? lMonthEndDate : lPPOEndDate; // which
					// ever
					// is
					// earlier
				} else {
					lPayEndDate = lMonthEndDate;
				}
			} else {
				lPayEndDate = lMonthEndDate;
			}

			int lPayDays = getDaysDifference(lPayStartDate, lPayEndDate);

			if (lDeathDate == null) {
				Map inputArgMap = new HashMap();
				inputArgMap.put("lBirthDate", lBirthDate);
				inputArgMap.put("lPayStartDate", lPayStartDate);
				inputArgMap.put("lPayEndDate", lPayEndDate);
				inputArgMap.put("lCurrentyyyyMM", lCurrentyyyyMM);
				inputArgMap.put("lPayDays", lPayDays);
				inputArgMap.put("lCalculatedBasicAmount", ldBasicAmount);

				ldADPAmount = calculateADPAmount(inputArgMap);
			} else if (lDeathDate != null) {

				MstPensionerFamilyDtls lMstPensionerFamilyDtlsVO = (MstPensionerFamilyDtls) inputMap.get("lMstPensionerFamilyDtlsVO");
				List lLstFPMember = (ArrayList) inputMap.get("lLstFPMember");
				if (lLstFPMember != null) {
					Date lFPDeathDate = lMstPensionerFamilyDtlsVO.getDateOfDeath();
					Date lFPBirthDate = lMstPensionerFamilyDtlsVO.getDateOfBirth();

					if (lLstFPMember.isEmpty()) // No valid family member found
					{
						// Exit;
					} else if (!lLstFPMember.isEmpty() && lFPDeathDate != null) // Pensioner
					// and
					// family
					// member
					// both
					// are
					// dead
					{
						int lFPDeathYYYYMM = lFPDeathDate != null ? Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lFPDeathDate)) : 0;

						if (lFPDeathYYYYMM == lCurrentyyyyMM) {
							if (lFPDeathDate.before(lDeathDate) || lFPDeathDate.equals(lDeathDate)) {
								Map inputArgMap = new HashMap();
								inputArgMap.put("lBirthDate", lBirthDate);
								inputArgMap.put("lPayStartDate", lPayStartDate);
								inputArgMap.put("lPayEndDate", lDeathDate); // lPayEndDate
								// =
								// lDeathDate
								// (as
								// lDeathDate
								// is
								// earlier
								// than
								// lPayEndDate
								inputArgMap.put("lCurrentyyyyMM", lCurrentyyyyMM);
								inputArgMap.put("lCalculatedBasicAmount", ldBasicAmount);

								ldADPAmount = calculateADPAmount(inputArgMap);
							} else {
								Map inputArgMap = new HashMap();
								if (lDeathYYYYMM == lCurrentyyyyMM) // Pensioner's
								// Death in
								// current
								// month
								{

									inputArgMap.put("lBirthDate", lBirthDate); // Birth
									// Date
									// of
									// Pensioner
									inputArgMap.put("lPayStartDate", lPayStartDate);
									inputArgMap.put("lPayEndDate", lDeathDate); // lPayEndDate
									// =
									// lDeathDate
									// (as
									// lDeathDate
									// is
									// earlier
									// than
									// lPayEndDate
									inputArgMap.put("lCurrentyyyyMM", lCurrentyyyyMM);
									inputArgMap.put("lCalculatedBasicAmount", ldBasicAmount);
									ldADPAmount = calculateADPAmount(inputArgMap); // ADP
									// Calculation
									// based
									// on
									// Basic
									// Amount

									inputArgMap = new HashMap();
									inputArgMap.put("lBirthDate", lFPBirthDate); // Birth
									// date
									// of
									// Family
									// member
									inputArgMap.put("lPayStartDate", addDaysInDate1(lDeathDate, 1)); // Pensioner's
									// Death
									// Date
									// +
									// 1
									inputArgMap.put("lPayEndDate", lFPDeathDate); // lPayEndDate
									// =
									// FM's
									// lDeathDate
									// (as
									// FM's
									// lDeathDate
									// is
									// earlier
									// than
									// lPayEndDate
									inputArgMap.put("lCurrentyyyyMM", lCurrentyyyyMM);
									inputArgMap.put("lCalculatedBasicAmount", ldFP1andFP2Amount);
									ldADPAmount += calculateADPAmount(inputArgMap);
								} else {
									inputArgMap = new HashMap();
									inputArgMap.put("lBirthDate", lFPBirthDate); // Birth
									// date
									// of
									// Family
									// member
									inputArgMap.put("lPayStartDate", lPayStartDate); // Pensioner's
									// Death
									// Date
									// +
									// 1
									inputArgMap.put("lPayEndDate", lFPDeathDate); // lPayEndDate
									// =
									// FM's
									// lDeathDate
									// (as
									// FM's
									// lDeathDate
									// is
									// earlier
									// than
									// lPayEndDate
									inputArgMap.put("lCurrentyyyyMM", lCurrentyyyyMM);
									inputArgMap.put("lCalculatedBasicAmount", ldFP1andFP2Amount);
									ldADPAmount = calculateADPAmount(inputArgMap);
								}
							}
						}
					} else // Pensioner is expired and Family member is alive
					{
						if (lDeathYYYYMM == lCurrentyyyyMM) // Pensioner's Death
						// in current month
						{
							Map inputArgMap = new HashMap();
							inputArgMap.put("lBirthDate", lBirthDate); // Birth
							// Date
							// of
							// Pensioner
							inputArgMap.put("lPayStartDate", lPayStartDate);
							inputArgMap.put("lPayEndDate", lDeathDate); // lPayEndDate
							// =
							// lDeathDate
							// (as
							// lDeathDate
							// is
							// earlier
							// than
							// lPayEndDate
							inputArgMap.put("lCurrentyyyyMM", lCurrentyyyyMM);
							inputArgMap.put("lCalculatedBasicAmount", ldBasicAmount);
							ldADPAmount = calculateADPAmount(inputArgMap); // ADP
							// Calculation
							// based
							// on
							// Basic
							// Amount

							inputArgMap = new HashMap();
							inputArgMap.put("lBirthDate", lFPBirthDate); // Birth
							// date
							// of
							// Family
							// member
							inputArgMap.put("lPayStartDate", addDaysInDate1(lDeathDate, 1)); // Pensioner's
							// Death
							// Date
							// +
							// 1
							inputArgMap.put("lPayEndDate", lPayEndDate);
							inputArgMap.put("lCurrentyyyyMM", lCurrentyyyyMM);
							inputArgMap.put("lCalculatedBasicAmount", ldFP1andFP2Amount);
							ldADPAmount += calculateADPAmount(inputArgMap);
						} else // Pensioner's Death NOT in current month
						{
							Map inputArgMap = new HashMap();
							inputArgMap.put("lBirthDate", lFPBirthDate); // Birth
							// date
							// of
							// Family
							// member
							inputArgMap.put("lPayStartDate", addDaysInDate1(lDeathDate, 1)); // Pensioner's
							// Death
							// Date
							// +
							// 1
							inputArgMap.put("lPayEndDate", lPayEndDate);
							inputArgMap.put("lCurrentyyyyMM", lCurrentyyyyMM);
							// inputArgMap.put("lCalculatedBasicAmount",
							// ldFP1andFP2Amount);
							inputArgMap.put("lCalculatedBasicAmount", ldFP1andFP2Amount);
							ldADPAmount = calculateADPAmount(inputArgMap);
						}
					}
				} else {
					// If pensioner's death is in current month and there is not
					// any valid family pensioner
					if (lDeathYYYYMM == lCurrentyyyyMM) // Pensioner's Death
					// in current month
					{
						Map inputArgMap = new HashMap();
						inputArgMap.put("lBirthDate", lBirthDate); // Birth
						// Date
						// of
						// Pensioner
						inputArgMap.put("lPayStartDate", lPayStartDate);
						inputArgMap.put("lPayEndDate", lDeathDate); // lPayEndDate
						// =
						// lDeathDate
						// (as
						// lDeathDate
						// is
						// earlier
						// than
						// lPayEndDate
						inputArgMap.put("lCurrentyyyyMM", lCurrentyyyyMM);
						inputArgMap.put("lCalculatedBasicAmount", ldBasicAmount);
						ldADPAmount = calculateADPAmount(inputArgMap); // ADP
						// Calculation
						// based
						// on
						// Basic
						// Amount
					}
				}
			}
			return ldADPAmount;
		} catch (Exception e) {
			throw e;
		}
	}

	public double calculateADPAmount(Map inputArgMap) throws Exception {

		Date lBirthDate = (Date) inputArgMap.get("lBirthDate");
		Date lPayStartDate = (Date) inputArgMap.get("lPayStartDate");
		Date lPayEndDate = (Date) inputArgMap.get("lPayEndDate");
		Integer lCurrentyyyyMM = (Integer) inputArgMap.get("lCurrentyyyyMM");
		int lPayDays = getDaysDifference(lPayStartDate, lPayEndDate);
		double ldCalculatedBasicAmount = (Double) inputArgMap.get("lCalculatedBasicAmount");
		double ldCalculatedADPAmount = 0;

		double lPayDay80 = 0;
		double lPayDay85 = 0;
		double lPayDay90 = 0;
		double lPayDay95 = 0;
		double lPayDay100 = 0;
		int li80_85 = 20;
		int li85_90 = 30;
		int li90_95 = 40;
		int li95_100 = 50;
		int li100 = 100;

		if (lBirthDate != null) {
			// double lTotalDaysofMonth = getDaysOfMonth1(lCurrentyyyyMM);
			int liYearsAge = getYearsDifference(lBirthDate, lPayStartDate);
			if (liYearsAge >= 79) {
				Calendar c = Calendar.getInstance();
				c.setTime(lBirthDate);

				c.add(Calendar.YEAR, 80);
				Date lBirthDate80 = c.getTime();
				c.add(Calendar.YEAR, 5);
				Date lBirthDate85 = c.getTime();
				c.add(Calendar.YEAR, 0);
				Date lBirthDate90 = c.getTime();
				c.add(Calendar.YEAR, 5);
				Date lBirthDate95 = c.getTime();
				c.add(Calendar.YEAR, 5);
				Date lBirthDate100 = c.getTime();

				int lBirthYYYYMM80 = lBirthDate80 != null ? Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lBirthDate80)) : 0;
				int lBirthYYYYMM85 = lBirthDate85 != null ? Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lBirthDate85)) : 0;
				int lBirthYYYYMM90 = lBirthDate90 != null ? Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lBirthDate90)) : 0;
				int lBirthYYYYMM95 = lBirthDate95 != null ? Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lBirthDate95)) : 0;
				int lBirthYYYYMM100 = lBirthDate100 != null ? Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lBirthDate100)) : 0;

				if (liYearsAge == 79) {
					if (lCurrentyyyyMM == lBirthYYYYMM80) {
						lPayDay80 = getDaysDifference(lBirthDate80, lPayEndDate);
					}
				} else if (liYearsAge == 84) {
					if (lCurrentyyyyMM == lBirthYYYYMM85) {
						if (lBirthDate85.before(lPayEndDate)) {
							lPayDay80 = getDaysDifference(lPayStartDate, addDaysInDate1(lBirthDate85, -1));
							lPayDay85 = getDaysDifference(lBirthDate85, lPayEndDate);
						} else {
							lPayDay80 = getDaysDifference(lPayStartDate, lPayEndDate);
							lPayDay85 = 0;
						}
					} else {
						lPayDay80 = lPayDays;
					}
				} else if (liYearsAge == 89) {
					if (lCurrentyyyyMM == lBirthYYYYMM90) {
						if (lBirthDate90.before(lPayEndDate)) {
							lPayDay85 = getDaysDifference(lPayStartDate, addDaysInDate1(lBirthDate90, -1));
							lPayDay90 = getDaysDifference(lBirthDate90, lPayEndDate);
						} else {
							lPayDay85 = getDaysDifference(lPayStartDate, lPayEndDate);
							lPayDay90 = 0;
						}
					} else {
						lPayDay85 = lPayDays;
					}
				} else if (liYearsAge == 94) {
					if (lCurrentyyyyMM == lBirthYYYYMM95) {
						if (lBirthDate95.before(lPayEndDate)) {
							lPayDay90 = getDaysDifference(lPayStartDate, addDaysInDate1(lBirthDate95, -1));
							lPayDay95 = getDaysDifference(lBirthDate95, lPayEndDate);
						} else {
							lPayDay90 = getDaysDifference(lPayStartDate, lPayEndDate);
							lPayDay95 = 0;
						}
					} else {
						lPayDay90 = lPayDays;
					}
				} else if (liYearsAge == 99) {
					if (lCurrentyyyyMM == lBirthYYYYMM100) {
						if (lBirthDate100.before(lPayEndDate)) {
							lPayDay95 = getDaysDifference(lPayStartDate, addDaysInDate1(lBirthDate100, -1));
							lPayDay100 = getDaysDifference(lBirthDate100, lPayEndDate);
						} else {
							lPayDay95 = getDaysDifference(lPayStartDate, lPayEndDate);
							lPayDay100 = 0;
						}
					} else {
						lPayDay95 = lPayDays;
					}
				} else if (liYearsAge >= 80 && liYearsAge < 85) {
					lPayDay80 = lPayDays;
				} else if (liYearsAge >= 85 && liYearsAge < 90) {
					lPayDay85 = lPayDays;
				} else if (liYearsAge >= 90 && liYearsAge < 95) {
					lPayDay90 = lPayDays;
				} else if (liYearsAge >= 95 && liYearsAge < 100) {
					lPayDay95 = lPayDays;
				} else if (liYearsAge >= 100) {
					lPayDay100 = lPayDays;
				}
			}
			if (lPayDays > 0) {
				// System.out.println("ldCalculatedBasicAmount=" +
				// ldCalculatedBasicAmount);
				// System.out.println("lPayDay80=" + lPayDay80);
				// System.out.println("lPayDay85=" + lPayDay85);
				// System.out.println("lPayDay90=" + lPayDay90);
				// System.out.println("lPayDay95=" + lPayDay95);
				// System.out.println("lPayDay100=" + lPayDay100);
				// System.out.println("lPayDay100=" + ldCalculatedBasicAmount *
				// ((lPayDay80 * li80_85) + (lPayDay85 * li85_90) + (lPayDay90 *
				// li90_95) + (lPayDay95 * li95_100) + (lPayDay100 * li100))
				// / (lPayDays * 100)); // changed from lTotalDaysofMonth to
				// // lPayDays
				ldCalculatedADPAmount = ldCalculatedBasicAmount * ((lPayDay80 * li80_85) + (lPayDay85 * li85_90) + (lPayDay90 * li90_95) + (lPayDay95 * li95_100) + (lPayDay100 * li100))
						/ (lPayDays * 100);
			}
		}
		return ldCalculatedADPAmount;
	}
}
