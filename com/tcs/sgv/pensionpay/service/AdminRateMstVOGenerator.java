package com.tcs.sgv.pensionpay.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.service.VOGeneratorService;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.dao.AdminRateMstDAO;
import com.tcs.sgv.pensionpay.dao.AdminRateMstDAOImpl;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAOImpl;
import com.tcs.sgv.pensionpay.dao.RltPensionHeadcodeRateDAO;
import com.tcs.sgv.pensionpay.dao.RltPensionHeadcodeRateDAOImpl;
import com.tcs.sgv.pensionpay.dao.RltPensionHeadcodeSpecialDAO;
import com.tcs.sgv.pensionpay.dao.RltPensionHeadcodeSpecialDAOImpl;
import com.tcs.sgv.pensionpay.valueobject.HstRltPensionHeadcodeRate;
import com.tcs.sgv.pensionpay.valueobject.MstPensionStateRate;
import com.tcs.sgv.pensionpay.valueobject.RltPensionHeadcodeRate;
import com.tcs.sgv.pensionpay.valueobject.RltPensionHeadcodeSpecial;
import com.tcs.sgv.pensionpay.valueobject.RltPensionRevisedPayment;


public class AdminRateMstVOGenerator extends ServiceImpl implements VOGeneratorService {

	/* Global Variable for PostId */
	private Long gLngPostId = null;

	/* Global Variable for UserId */
	private Long gLngUserId = null;

	/* Global Variable for Current Date */
	private Date gDate = null;

	/* Global Variable for Logger Class */
	private Log gLogger = LogFactory.getLog(getClass());

	public ResultObject generateMap(Map objArgs) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) objArgs.get("requestObj");

		try {
			setSessionInfo(objArgs);
			Map inputMap = new HashMap();

			String lTablePK = StringUtility.getParameter("hidPKTable", request).trim();
			String lTableNewPK = StringUtility.getParameter("hidTableNewPK", request).trim();

			String Fieldtype = StringUtility.getParameter("Fieldtype", request).trim();
			String lTIRateType = StringUtility.getParameter("TIRateType", request).trim();
			String lForPension = StringUtility.getParameter("ForPension", request).trim();
			String lOldRate = StringUtility.getParameter("OldRate", request).trim();
			String lNewRate = StringUtility.getParameter("txtNewRate", request).trim();
			String lOldMinAmt = StringUtility.getParameter("OldMinAmt", request).trim();
			String lNewMinAmt = StringUtility.getParameter("txtNewMinAmt", request).trim();
			String lGrNo = StringUtility.getParameter("txtGrNo", request).trim();
			String lGrDt = StringUtility.getParameter("txtGrDt", request).trim();

			String lHeadCode = StringUtility.getParameter("cmbStateCode", request).trim();
			String lEffDate = StringUtility.getParameter("txtEffectedDate", request).trim();
			Date effDate = new SimpleDateFormat("dd/MM/yyyy").parse(lEffDate);
			Date prevDate = new SimpleDateFormat("dd/MM/yyyy").parse(lEffDate);

			Date lDtGrDt = null;
			if (lGrDt != null && !"".equals(lGrDt)) {
				lDtGrDt = new SimpleDateFormat("dd/MM/yyyy").parse(lGrDt);
			}

			prevDate = getPrevDate(prevDate);

			if (lNewRate == null || lNewRate.length() <= 0) {
				lNewRate = "0";
			}
			if (lNewMinAmt == null || lNewMinAmt.length() <= 0) {
				lNewMinAmt = "0";
			}

			objArgs.put("HeadCode", lHeadCode);
			objArgs.put("effDate", effDate);
			objArgs.put("prevDate", prevDate);
			objArgs.put("TablePK", lTablePK);
			objArgs.put("TableNewPK", lTableNewPK);

			objArgs.put("Fieldtype", Fieldtype);
			objArgs.put("TIRateType", lTIRateType);
			objArgs.put("ForPension", lForPension);
			objArgs.put("OldRate", lOldRate);
			objArgs.put("NewRate", lNewRate);
			objArgs.put("OldMinAmt", lOldMinAmt);
			objArgs.put("NewMinAmt", lNewMinAmt);
			objArgs.put("lGrNo", lGrNo);
			objArgs.put("lDtGrDt", lDtGrDt);

			if (lHeadCode.equals("16") || lHeadCode.equals("17") || lHeadCode.equals("18") || lHeadCode.equals("19")) {
				inputMap = generateRltPensionHeadcodeSpecial(objArgs);
				objArgs.put("RltPensionHeadcodeSpecialOld", inputMap.get("RltPensionHeadcodeSpecialOld"));
				objArgs.put("RltPensionHeadcodeSpecialNew", inputMap.get("RltPensionHeadcodeSpecialNew"));
			} else {
				inputMap = generateRltPensionHeadcodeRate1(objArgs);
				objArgs.put("RltPensionHeadcodeRateOld", inputMap.get("RltPensionHeadcodeRateOld"));
				objArgs.put("RltPensionHeadcodeRateNew", inputMap.get("RltPensionHeadcodeRateNew"));
			}

			inputMap = generateRltPensionRevisedPayment(objArgs);
			objArgs.put("LstRevisedPayment", inputMap.get("LstRevisedPayment"));

			objRes.setResultCode(ErrorConstants.SUCCESS);
			objRes.setResultValue(objArgs);

			/* select from trn_pension_arrear_dtls */
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
	}

	private Date getPrevDate(Date lDate) throws Exception {

		try {
			long dateMillis = lDate.getTime();
			long dayInMillis = 1000 * 60 * 60 * 24;// subtract a day
			dateMillis = dateMillis - dayInMillis;
			lDate.setTime(dateMillis);// set myDate to new time
		} catch (Exception e) {
			gLogger.error("Error is :: " + e, e);
			throw (e);
		}
		return lDate;
	}

	private Map generateRltPensionHeadcodeSpecial(Map<String, Object> inputMap) throws Exception {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		String tablePK = null;

		try {
			setSessionInfo(inputMap);
			tablePK = inputMap.get("TablePK").toString();
			Date prevDate = (Date) inputMap.get("prevDate");
			Date effDate = (Date) inputMap.get("effDate");
			String lHeadcode = inputMap.get("HeadCode").toString();
			String lOldRate = inputMap.get("OldRate").toString();
			String lNewRate = inputMap.get("NewRate").toString();

			// old entry for update
			RltPensionHeadcodeSpecialDAO lObjHeadcodeSplDAO = new RltPensionHeadcodeSpecialDAOImpl(RltPensionHeadcodeSpecial.class, serv.getSessionFactory());
			RltPensionHeadcodeSpecial lObjHeadcodeSpl = lObjHeadcodeSplDAO.read(Long.parseLong(tablePK));
			lObjHeadcodeSpl.setToDate(prevDate);
			lObjHeadcodeSpl.setUpdatedDate(gDate);
			lObjHeadcodeSpl.setUpdatedPostId(new BigDecimal(gLngPostId));
			lObjHeadcodeSpl.setUpdatedUserId(new BigDecimal(gLngUserId));
			inputMap.put("RltPensionHeadcodeSpecialOld", lObjHeadcodeSpl);
			// old entry ready for update

			// new entry for insert
			lObjHeadcodeSpl = new RltPensionHeadcodeSpecial();

			lObjHeadcodeSpl.setCreatedDate(gDate);
			lObjHeadcodeSpl.setCreatedPostId(new BigDecimal(gLngPostId));
			lObjHeadcodeSpl.setCreatedUserId(new BigDecimal(gLngUserId));
			lObjHeadcodeSpl.setFromDate(effDate);
			lObjHeadcodeSpl.setHeadCode(new BigDecimal(lHeadcode));
			lObjHeadcodeSpl.setNewAmount(new BigDecimal(lNewRate));
			lObjHeadcodeSpl.setOldAmount(new BigDecimal(lOldRate));

			inputMap.put("RltPensionHeadcodeSpecialNew", lObjHeadcodeSpl);
			// new entry for insert
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			throw (e);
		}

		return inputMap;
	}

	private Map generateRltPensionHeadcodeRate1(Map<String, Object> inputMap) throws Exception {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		String tablePK = null;
		String lngNewPk = null;

		try {
			setSessionInfo(inputMap);
			tablePK = inputMap.get("TablePK").toString();
			lngNewPk = inputMap.get("TableNewPK").toString();

			Date prevDate = (Date) inputMap.get("prevDate");
			Date effDate = (Date) inputMap.get("effDate");
			String lHeadcode = inputMap.get("HeadCode").toString();
			String lFieldType = inputMap.get("Fieldtype").toString();
			String lTIRateType = inputMap.get("TIRateType").toString();
			String lForPension = inputMap.get("ForPension").toString();
			String lNewRate = inputMap.get("NewRate").toString();
			String lNewMinAmt = inputMap.get("NewMinAmt").toString();
			String lStrGrNo = (String) inputMap.get("lGrNo");
			Date lDtGrDate = (Date) inputMap.get("lDtGrDt");

			// old entry for update
			RltPensionHeadcodeRateDAO lObjHeadcodeRateDAO = new RltPensionHeadcodeRateDAOImpl(RltPensionHeadcodeRate.class, serv.getSessionFactory());
			RltPensionHeadcodeRate lObjHeadcodeRate = lObjHeadcodeRateDAO.read(Long.parseLong(tablePK));
			lObjHeadcodeRate.setEffectiveToDate(prevDate);
			lObjHeadcodeRate.setUpdatedDate(gDate);
			lObjHeadcodeRate.setUpdatedPostId(new BigDecimal(gLngPostId));
			lObjHeadcodeRate.setUpdatedUserId(new BigDecimal(gLngUserId));
			inputMap.put("RltPensionHeadcodeRateOld", lObjHeadcodeRate);
			// old entry ready for update

			// new entry for insert

			lObjHeadcodeRate = new RltPensionHeadcodeRate();
			lObjHeadcodeRate.setPensionHeadcodeRateId(Long.valueOf(lngNewPk));
			lObjHeadcodeRate.setCreatedDate(gDate);
			lObjHeadcodeRate.setCreatedPostId(new BigDecimal(gLngPostId));
			lObjHeadcodeRate.setCreatedUserId(new BigDecimal(gLngUserId));
			lObjHeadcodeRate.setEffectiveFromDate(effDate);
			lObjHeadcodeRate.setFieldType(lTIRateType);
			lObjHeadcodeRate.setHeadCode(new BigDecimal(lHeadcode));
			lObjHeadcodeRate.setGrNo(lStrGrNo);
			if (lDtGrDate != null) {
				lObjHeadcodeRate.setGrDate(lDtGrDate);
			}

			if (lFieldType.equals("MA")) {
				lObjHeadcodeRate.setMinAmount(new BigDecimal(lNewRate));
			} else if (lFieldType.equals("IR")) {
				lObjHeadcodeRate.setRate(new BigDecimal(lNewRate));
				lObjHeadcodeRate.setMinAmount(new BigDecimal(lNewMinAmt));
			} else if (lFieldType.equals("DP")) {
				lObjHeadcodeRate.setRate(new BigDecimal(lNewRate));
			} else if (lFieldType.equals("TI")) {
				lObjHeadcodeRate.setRate(new BigDecimal(lNewRate));

				if (lTIRateType.equals("DA_1986")) {
					if (lForPension.equals("UPTO 1750")) {
						lObjHeadcodeRate.setUptoBasic(new BigDecimal("1750"));
					} else if (lForPension.equals("UPTO 3000")) {
						lObjHeadcodeRate.setMinAmount(new BigDecimal(lNewMinAmt));
						lObjHeadcodeRate.setUptoBasic(new BigDecimal("3000"));
					} else if (lForPension.equals("ABOVE 3001")) {
						lObjHeadcodeRate.setMinAmount(new BigDecimal(lNewMinAmt));
						lObjHeadcodeRate.setUptoBasic(new BigDecimal("999999"));
					}
					inputMap.put("TIStyle", "DA_1986");
				} else if (lTIRateType.equals("DA_1996")) {
					lObjHeadcodeRate.setFieldType("DA_1996");
					inputMap.put("TIStyle", "DA_1996");
				} else if (lTIRateType.equals("DA_1996_DP")) {
					lObjHeadcodeRate.setFieldType("DA_1996_DP");
					inputMap.put("TIStyle", "DA_1996_DP");
				} else if (lTIRateType.equals("DA_2006")) {
					lObjHeadcodeRate.setFieldType("DA_2006");
					inputMap.put("TIStyle", "DA_2006");
				}
			}
			inputMap.put("RltPensionHeadcodeRateNew", lObjHeadcodeRate);
			// new entry for insert
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			throw (e);
		}

		return inputMap;
	}

	private Map generateRltPensionRevisedPayment(Map<String, Object> inputMap) throws Exception {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");

		String lStrFromYYYYMM = null;
		String lStrToYYYYMM = null;
		String lStrInYYYYMM = null;

		List<RltPensionRevisedPayment> lLstRevisedPayment = new ArrayList<RltPensionRevisedPayment>();
		RltPensionRevisedPayment lObjRevisedPayment = null;

		try {
			setSessionInfo(inputMap);

			String[] lStrPayFromMM = StringUtility.getParameterValues("PayFromMM", request);
			String[] lStrPayFromYYYY = StringUtility.getParameterValues("PayFromYYYY", request);
			String[] lStrPayToMM = StringUtility.getParameterValues("PayToMM", request);
			String[] lStrPayToYYYY = StringUtility.getParameterValues("PayToYYYY", request);
			String[] lStrPayInMM = StringUtility.getParameterValues("PayInMM", request);
			String[] lStrPayInYYYY = StringUtility.getParameterValues("PayInYYYY", request);

			String lFieldType = inputMap.get("Fieldtype").toString();
			String tablePK = inputMap.get("TablePK").toString();
			String tableNewPK = inputMap.get("TableNewPK").toString();
			inputMap.put("CalcArrear", "Y");
			for (int i = 0; i < lStrPayFromMM.length; i++) {
				if ("-1".equals(lStrPayFromMM[i])) {
					lStrPayFromMM[i] = "1";
				}
				if ("-1".equals(lStrPayToMM[i])) {
					lStrPayToMM[i] = "1";
				}
				if ("-1".equals(lStrPayInMM[i])) {
					lStrPayInMM[i] = "1";
					inputMap.put("CalcArrear", "N");
				}

				lStrFromYYYYMM = createYYYYMM(lStrPayFromMM[i], lStrPayFromYYYY[i]);
				lStrToYYYYMM = createYYYYMM(lStrPayToMM[i], lStrPayToYYYY[i]);
				lStrInYYYYMM = createYYYYMM(lStrPayInMM[i], lStrPayInYYYY[i]);

				lObjRevisedPayment = new RltPensionRevisedPayment();

				lObjRevisedPayment.setHeadcodeRatePk(Long.parseLong(tableNewPK));
				lObjRevisedPayment.setFieldType(lFieldType);
				lObjRevisedPayment.setForPayMonth(Integer.parseInt(lStrFromYYYYMM));
				lObjRevisedPayment.setToPayMonth(Integer.parseInt(lStrToYYYYMM));
				lObjRevisedPayment.setPayInMonth(Integer.parseInt(lStrInYYYYMM));
				lObjRevisedPayment.setCreatedDate(gDate);
				lObjRevisedPayment.setCreatedPostId(new BigDecimal(gLngPostId));
				lObjRevisedPayment.setCreatedUserId(new BigDecimal(gLngUserId));

				lLstRevisedPayment.add(lObjRevisedPayment);
			}
			inputMap.put("LstRevisedPayment", lLstRevisedPayment);
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			throw (e);
		}
		return inputMap;
	}

	private String createYYYYMM(String MM, String YYYY) throws Exception {

		String lStrYYYYMM = null;

		try {
			if (Integer.parseInt(MM) < 10) {
				lStrYYYYMM = YYYY + "0" + MM;
			} else {
				lStrYYYYMM = YYYY + MM;
			}
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			throw (e);
		}
		return lStrYYYYMM;
	}

	private void setSessionInfo(Map inputMap) throws Exception {

		try {
			gLngPostId = SessionHelper.getPostId(inputMap);
			gLngUserId = SessionHelper.getUserId(inputMap);
			gDate = DBUtility.getCurrentDateFromDB();
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw (e);
		}

	}

	public ResultObject generateRltPensionHeadcodeRate(Map inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		RltPensionHeadcodeRate lObjRltPensionHeadcodeRate = null;
		HstRltPensionHeadcodeRate lObjHstPensionHeadcodeRate = null;
		List<RltPensionHeadcodeRate> lLstRltPensionHeadcodeRate = new ArrayList<RltPensionHeadcodeRate>();

		try {

			setSessionInfo(inputMap);
			Date lDtcurDate = SessionHelper.getCurDate();
			Date lDtMaxDate = null;
			Date lDtEffctFromDate = null;
			String lStrDateFlag = "";
			Long lLngPensionHeadCodeRateId = null;

			String lStrFieldType = StringUtility.getParameter("TIRateType", request);
			String lStrHeadCode = StringUtility.getParameter("cmbStateCode", request);			
			String lStrForPension = StringUtility.getParameter("ForPension", request);
			if (!lStrFieldType.equals("DA_1986") && !lStrFieldType.equals("")) {
				lStrForPension = "";
			}
			String[] lArrStrFromDate = StringUtility.getParameterValues("txtFromDate", request);
			String[] lArrStrToDate = StringUtility.getParameterValues("txtToDate", request);
			String[] lArrStrRate = StringUtility.getParameterValues("txtDARate", request);
			String[] lArrStrMinAmnt = StringUtility.getParameterValues("txtMinAmnt", request);
			String[] lArrStrTablePK = StringUtility.getParameterValues("hidTablePk", request);
			String lStrMaxDate = StringUtility.getParameter("maxDate", request);
			if(!lStrMaxDate.equals("")){
				lDtMaxDate = simpleDateFormat.parse(lStrMaxDate);
			}
			Long lLngPk = null;
			Long lLngHeadcodeRateId = null;
			BigDecimal lLngRevision = null;
			Object []lObj = null;
			
			RltPensionHeadcodeRateDAO lObjRltPensionHeadcodeRateDAO = new RltPensionHeadcodeRateDAOImpl(RltPensionHeadcodeRate.class, serv.getSessionFactory());
			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(HstRltPensionHeadcodeRate.class, serv.getSessionFactory());
			AdminRateMstDAOImpl lObjAdminRateMstDAO = new AdminRateMstDAOImpl(serv.getSessionFactory());
			
			lDtEffctFromDate = lObjAdminRateMstDAO.getMaxDate(Long.parseLong(lStrHeadCode), lStrFieldType);
			
			if(lDtMaxDate != null && lDtEffctFromDate != null){
				if(lDtMaxDate.after(lDtEffctFromDate)){
					lStrDateFlag = "Y";
				}else{
					lStrDateFlag = "N";
				}
			}
			
			if(!lStrDateFlag.equals("Y"))
			{
				lLngRevision = lObjAdminRateMstDAO.getMaxRevision(Long.parseLong(lStrHeadCode), lStrFieldType);
				
				for(Integer lIntCnt = 0; lIntCnt < lArrStrTablePK.length; lIntCnt++)
				{
					lLngPk = Long.parseLong(lArrStrTablePK[lIntCnt]);
					lObjRltPensionHeadcodeRate = lObjRltPensionHeadcodeRateDAO.read(lLngPk);
					lObjHstPensionHeadcodeRate = new HstRltPensionHeadcodeRate();
					lLngPensionHeadCodeRateId = IFMSCommonServiceImpl.getNextSeqNum("HST_RLT_PENSION_HEADCODE_RATE", inputMap);
					lObjHstPensionHeadcodeRate.setPensionHeadcodeRateId(lLngPensionHeadCodeRateId);
					lObjHstPensionHeadcodeRate.setHeadCode(lObjRltPensionHeadcodeRate.getHeadCode());
					lObjHstPensionHeadcodeRate.setFieldType(lObjRltPensionHeadcodeRate.getFieldType());
					lObjHstPensionHeadcodeRate.setEffectiveFromDate(lObjRltPensionHeadcodeRate.getEffectiveFromDate());
					lObjHstPensionHeadcodeRate.setEffectiveToDate(lObjRltPensionHeadcodeRate.getEffectiveToDate());
					lObjHstPensionHeadcodeRate.setRate(lObjRltPensionHeadcodeRate.getRate());
					lObjHstPensionHeadcodeRate.setMinAmount(lObjRltPensionHeadcodeRate.getMinAmount());
					lObjHstPensionHeadcodeRate.setUptoBasic(lObjRltPensionHeadcodeRate.getUptoBasic());
					lObjHstPensionHeadcodeRate.setCreatedDate(DBUtility.getCurrentDateFromDB());
					lObjHstPensionHeadcodeRate.setCreatedPostId(BigDecimal.valueOf(gLngPostId));
					lObjHstPensionHeadcodeRate.setCreatedUserId(BigDecimal.valueOf(gLngUserId));				
					lObjHstPensionHeadcodeRate.setGrDate(lObjRltPensionHeadcodeRate.getGrDate());				
					lObjHstPensionHeadcodeRate.setRevision(lLngRevision);
					if(lObjRltPensionHeadcodeRate.getGrNo() != null){
						lObjHstPensionHeadcodeRate.setGrNo(lObjRltPensionHeadcodeRate.getGrNo());
					}
					lObjPhysicalCaseInwardDAO.create(lObjHstPensionHeadcodeRate);								
				}
				
				List lLstHeadCodeDtls = lObjAdminRateMstDAO.getDARateDetails(lStrFieldType, lStrHeadCode, "-1");
				for(Integer lIntCnt=0; lIntCnt < lLstHeadCodeDtls.size(); lIntCnt++)
				{
					lObj = (Object[]) lLstHeadCodeDtls.get(lIntCnt);
					lLngHeadcodeRateId = Long.parseLong(lObj[4].toString());
					lObjRltPensionHeadcodeRate = (RltPensionHeadcodeRate)lObjRltPensionHeadcodeRateDAO.read(lLngHeadcodeRateId);
					lObjRltPensionHeadcodeRateDAO.delete(lObjRltPensionHeadcodeRate);
				}
				
	
				if (lArrStrRate.length > 0) {
					for (int lIntCnt = 0; lIntCnt < lArrStrRate.length; lIntCnt++) {
						lObjRltPensionHeadcodeRate = new RltPensionHeadcodeRate();
	
						lObjRltPensionHeadcodeRate.setHeadCode(BigDecimal.valueOf(Long.valueOf(lStrHeadCode)));
						if (lStrFieldType.length() > 0) {
							lObjRltPensionHeadcodeRate.setFieldType(lStrFieldType);
						}
						if (lArrStrFromDate[lIntCnt] != null && lArrStrFromDate[lIntCnt].trim().length() > 0) {
							lObjRltPensionHeadcodeRate.setEffectiveFromDate(StringUtility.convertStringToDate(lArrStrFromDate[lIntCnt].trim()));
						}
						if (lArrStrToDate[lIntCnt] != null && lArrStrToDate[lIntCnt].trim().length() > 0) {
							lObjRltPensionHeadcodeRate.setEffectiveToDate(StringUtility.convertStringToDate(lArrStrToDate[lIntCnt].trim()));
						}
						if (lArrStrRate[lIntCnt] != null && lArrStrRate[lIntCnt].trim().length() > 0) {
							lObjRltPensionHeadcodeRate.setRate(new BigDecimal(lArrStrRate[lIntCnt].trim()));
						}
						if (lArrStrMinAmnt[lIntCnt] != null && !lArrStrMinAmnt[lIntCnt].equals("")) {
							lObjRltPensionHeadcodeRate.setMinAmount(BigDecimal.valueOf(Math.round(Double.parseDouble(lArrStrMinAmnt[lIntCnt].trim()))));
						}
						if (lStrForPension.length() > 0 && !lStrForPension.equals("")) {
							lObjRltPensionHeadcodeRate.setUptoBasic(BigDecimal.valueOf(Long.valueOf(lStrForPension.substring(lStrForPension.length() - 4, lStrForPension.length()))));
						}
	
						lObjRltPensionHeadcodeRate.setCreatedUserId(BigDecimal.valueOf(gLngUserId));
						lObjRltPensionHeadcodeRate.setCreatedPostId(BigDecimal.valueOf(gLngPostId));
						lObjRltPensionHeadcodeRate.setCreatedDate(lDtcurDate);
						lObjRltPensionHeadcodeRate.setGrDate(null);
						lObjRltPensionHeadcodeRate.setGrNo(null);
	
						lLstRltPensionHeadcodeRate.add(lObjRltPensionHeadcodeRate);
					}
				}
				inputMap.put("maxDate", "N");
			}else{
				inputMap.put("maxDate", "Y");
			}
			inputMap.put("lLstRltPensionHeadcodeRate", lLstRltPensionHeadcodeRate);
			inputMap.put("Mode", "Add");
			objRes.setResultCode(ErrorConstants.SUCCESS);
			objRes.setResultValue(inputMap);

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error in generateRltPensionHeadcodeRate method is :" + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);

		}
		return objRes;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.pensionpay.service.AdminRateMstVOGenerator#
	 * generateMstPensionStateRateVO(java.util.Map)
	 */
	public ResultObject generateMstPensionStateRateVO(Map inputMap) {

		gLogger.info("In generateMstPensionStateRateVO method........");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		MstPensionStateRate lObjMstPensionStateRate = null;
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = null;
		AdminRateMstDAO lObjAdminRateMstDAO = null;
		try {

			setSessionInfo(inputMap);
			lObjMstPensionStateRate = new MstPensionStateRate();
			lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(MstPensionStateRate.class, serv.getSessionFactory());
			lObjAdminRateMstDAO = new AdminRateMstDAOImpl(serv.getSessionFactory());
			String lStrMstPensionStateRateId = null;
			String lStrVal = StringUtility.getParameter("hidString", request);
			String lStrStateDesc = StringUtility.getParameter("txtStateDesc", request);
			Long lLngStateCode = null;

			if (!"".equals(lStrVal) && lStrVal.length() > 0) {
				if (lStrVal.equals("Update")) {
					lStrMstPensionStateRateId = StringUtility.getParameter("hidMstPensionStateRateId", request);
					if (!"".equals(lStrMstPensionStateRateId) && lStrMstPensionStateRateId.length() > 0) {
						lObjMstPensionStateRate = (MstPensionStateRate) lObjPhysicalCaseInwardDAO.read(Long.valueOf(lStrMstPensionStateRateId.trim()));
					}
					lObjMstPensionStateRate.setUpdatedDate(gDate);
					lObjMstPensionStateRate.setUpdatedPostId(BigDecimal.valueOf(gLngPostId));
					lObjMstPensionStateRate.setUpdatedUserId(BigDecimal.valueOf(gLngUserId));

					inputMap.put("lObjMstPensionStateRate", lObjMstPensionStateRate);
					inputMap.put("Mode", "Update");

				}
				if (lStrVal.equals("Add")) {
					lObjMstPensionStateRate.setCreatedUserId(BigDecimal.valueOf(gLngUserId));
					lObjMstPensionStateRate.setCreatedPostId(BigDecimal.valueOf(gLngPostId));
					lObjMstPensionStateRate.setCreatedDate(gDate);
					lObjMstPensionStateRate.setUpdatedDate(gDate);
					lObjMstPensionStateRate.setUpdatedPostId(BigDecimal.valueOf(gLngPostId));
					lObjMstPensionStateRate.setUpdatedUserId(BigDecimal.valueOf(gLngUserId));
					// lObjMstPensionStateRate.setLocationCode(Long.valueOf(SessionHelper.getLocationCode(inputMap).trim()));
					lObjMstPensionStateRate.setDbId(BigDecimal.valueOf(SessionHelper.getDbId(inputMap)));
					lObjMstPensionStateRate.setLangId(SessionHelper.getLangId(inputMap));

					lLngStateCode = lObjAdminRateMstDAO.getMaxStateCode(SessionHelper.getLangId(inputMap), Long.valueOf(SessionHelper.getLocationCode(inputMap).trim()));
					lObjMstPensionStateRate.setStateCode(new BigDecimal(lLngStateCode));

					if (!"".equals(lStrStateDesc) && lStrStateDesc.length() > 0) {
						lObjMstPensionStateRate.setStateDesc(lStrStateDesc.trim());
					}

					inputMap.put("lObjMstPensionStateRate", lObjMstPensionStateRate);
					inputMap.put("Mode", "Add");
				}
			} else {
				inputMap.put("Mode", "Delete");
			}
			objRes.setResultCode(ErrorConstants.SUCCESS);
			objRes.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is  " + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
	}

}
