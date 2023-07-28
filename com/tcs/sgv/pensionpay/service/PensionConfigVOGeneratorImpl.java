/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 16, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.icu.text.DateFormat;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.dao.PensionConfigDAO;
import com.tcs.sgv.pensionpay.dao.PensionConfigDAOImpl;
import com.tcs.sgv.pensionpay.valueobject.HstPnsnPmntPpoNo;
import com.tcs.sgv.pensionpay.valueobject.MstChangeStmntCtgry;
import com.tcs.sgv.pensionpay.valueobject.MstPensionHeadcode;
import com.tcs.sgv.pensionpay.valueobject.MstPensionMainCategory;
import com.tcs.sgv.pensionpay.valueobject.RltAuditorBank;
import com.tcs.sgv.pensionpay.valueobject.RltPensionHeadcodeChargable;


/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 May 16, 2011
 */
public class PensionConfigVOGeneratorImpl extends ServiceImpl implements PensionConfigVOGenerator {

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for LangId */
	Long gLngLangId = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;

	Log gLogger = LogFactory.getLog(getClass());

	Date gDateDBDate = null;

	private final ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	// Sets session information in the global variables
	private void setSessionInfo(Map inputMap) {

		gLngLangId = SessionHelper.getLangId(inputMap);
		gLngPostId = SessionHelper.getPostId(inputMap);
		gLngUserId = SessionHelper.getUserId(inputMap);
		gStrLocationCode = SessionHelper.getLocationCode(inputMap);
		gDateDBDate = DBUtility.getCurrentDateFromDB();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.PensionPaymentAdminVOGenerator#generateMap
	 * (java.util.Map)
	 */
	public ResultObject generateMap(Map inputMap) {

		gLogger.info("In generateMap of PensionConfigVOGeneratorImpl........");

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		PensionConfigDAO lObjPensionConfigDAO = null;
		String lStrSeries = null;
		String lStrDescrptn = null;
		String lStrPnsnSchemeCode = null;
		String lStrCvpSchemeCode = null;
		String lStrDcrgSchemeCode = null;

		try {
			setSessionInfo(inputMap);
			List<RltAuditorBank> lLstRltAuditorBankVO = new ArrayList<RltAuditorBank>();
			lLstRltAuditorBankVO = generateRltAuditorBankVO(inputMap);
			MstPensionHeadcode lObjMstPensionHeadcodeVO = new MstPensionHeadcode();
			List lLstMstPensionHeadcode = null;
			List<RltPensionHeadcodeChargable> lLstRltPensionHeadcodeChargableVO = new ArrayList<RltPensionHeadcodeChargable>();
			RltPensionHeadcodeChargable lObjRltPensionHeadcodeChargableVO = new RltPensionHeadcodeChargable();
			lObjPensionConfigDAO = new PensionConfigDAOImpl(MstPensionHeadcode.class, serv.getSessionFactory());
			if (StringUtility.getParameter("gString", request).length() > 0) {

				lStrSeries = StringUtility.getParameter("headCode", request).trim();
				if (lStrSeries.length() > 0) {
					lLstMstPensionHeadcode = lObjPensionConfigDAO.getListByColumnAndValue("series", lStrSeries.trim());
				}
				if (lLstMstPensionHeadcode != null && lLstMstPensionHeadcode.size() > 0) {
					lObjMstPensionHeadcodeVO = (MstPensionHeadcode) lLstMstPensionHeadcode.get(0);
				}

				lStrDescrptn = StringUtility.getParameter("txtDescription", request).trim();
				if (lStrDescrptn.length() > 0) {
					lObjMstPensionHeadcodeVO.setDescription(lStrDescrptn);
				}

				lObjMstPensionHeadcodeVO.setUpdatedDate(gDateDBDate);
				lObjMstPensionHeadcodeVO.setUpdatedPostId(BigDecimal.valueOf(gLngPostId));
				lObjMstPensionHeadcodeVO.setUpdatedUserId(BigDecimal.valueOf(gLngUserId));

				lStrPnsnSchemeCode = StringUtility.getParameter("txtPensionSchemeCode", request).trim();
				lStrCvpSchemeCode = StringUtility.getParameter("txtCvpSchemeCode", request).trim();
				lStrDcrgSchemeCode = StringUtility.getParameter("txtDcrgSchemeCode", request).trim();

				lLstRltPensionHeadcodeChargableVO = lObjPensionConfigDAO.getRltPensionHeadcodeChargableVO(String.valueOf(lObjMstPensionHeadcodeVO.getHeadCode().longValue()));
				if (lLstRltPensionHeadcodeChargableVO != null && !lLstRltPensionHeadcodeChargableVO.isEmpty()) {
					for (int lIntCnt = 0; lIntCnt < 3; lIntCnt++) {
						if (lStrPnsnSchemeCode.length() > 0 || lStrCvpSchemeCode.length() > 0 || lStrDcrgSchemeCode.length() > 0) {
							lObjRltPensionHeadcodeChargableVO = lLstRltPensionHeadcodeChargableVO.get(lIntCnt);
						}

						if (lIntCnt == 0) {
							lObjRltPensionHeadcodeChargableVO.setSchemeCode(lStrPnsnSchemeCode);
						}
						if (lIntCnt == 1) {
							lObjRltPensionHeadcodeChargableVO.setSchemeCode(lStrCvpSchemeCode);
						}
						if (lIntCnt == 2) {
							lObjRltPensionHeadcodeChargableVO.setSchemeCode(lStrDcrgSchemeCode);
						}

						lObjRltPensionHeadcodeChargableVO.setUpdatedDate(gDateDBDate);
						lObjRltPensionHeadcodeChargableVO.setUpdatedPostId(BigDecimal.valueOf(gLngPostId));
						lObjRltPensionHeadcodeChargableVO.setUpdatedUserId(BigDecimal.valueOf(gLngUserId));
					}
				}
				inputMap.put("Mode", "Update");
			} else if (StringUtility.getParameter("PnsnHeadCodeId", request).length() > 1) {
				inputMap.put("Mode", "Delete");
			} else {

				lObjMstPensionHeadcodeVO = generateMstPensionHeadcodeVO(inputMap);
				lLstRltPensionHeadcodeChargableVO = generateRltPensionHeadcodeChargableVO(inputMap);
				inputMap.put("Mode", "Add");

			}
			inputMap.put("lLstRltAuditorBankVO", lLstRltAuditorBankVO);
			inputMap.put("lObjMstPensionHeadcodeVO", lObjMstPensionHeadcodeVO);
			inputMap.put("lLstRltPensionHeadcodeChargableVO", lLstRltPensionHeadcodeChargableVO);

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

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.pensionpay.service.PensionPaymentAdminVOGenerator#
	 * generateRltAuditorBankVO(java.util.Map)
	 */
	public List<RltAuditorBank> generateRltAuditorBankVO(Map inputMap) {

		gLogger.info("In generateRltAuditorBankVO of PensionPaymentAdminVOGeneratorImpl........");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		List<RltAuditorBank> lLstRltAuditorBankVO = new ArrayList<RltAuditorBank>();
		RltAuditorBank lObjRltAuditorBankVO = null;
		PensionConfigDAO lObjPensionConfigDAO = null;
		try {
			setSessionInfo(inputMap);
			String lStrPostId = null;
			String lStrBankCode = null;
			String lStrBranchCode = null;
			String lStrAction = null;
			// String lStrRemoveValue = null;
			String[] lStrArrBranchCode = null;
			String[] lStrArrRemoveValue = null;
			
			lStrAction =  StringUtility.getParameter("action", request);
			
			lStrPostId = (StringUtility.getParameter("cmbAuditorName", request).trim().length() > 0) ? StringUtility.getParameter("cmbAuditorName", request).trim() : null;

			lStrBankCode = (StringUtility.getParameter("cmbBankName", request).trim().length() > 0) ? StringUtility.getParameter("cmbBankName", request).trim() : null;

			lStrBranchCode = StringUtility.getParameter("CmbDisplayBranchName", request);

			// lStrRemoveValue = StringUtility.getParameter("removeArrValue",
			// request);

			lObjPensionConfigDAO = new PensionConfigDAOImpl(RltAuditorBank.class, serv.getSessionFactory());

			lStrArrBranchCode = lStrBranchCode.split("~");
			if(lStrAction.length() > 0 && lStrAction.equals("Add"))
			{
				
				if (lStrArrBranchCode.length > 0) {
					for (int lIntCnt = 0; lIntCnt < lStrArrBranchCode.length; lIntCnt++) {
						lObjRltAuditorBankVO = new RltAuditorBank();
						// false means this auditor already mapped with some other
						// bank-branch
						if (lObjPensionConfigDAO.validateMapping(lStrPostId, lStrBankCode, lStrArrBranchCode[lIntCnt].trim(), gStrLocationCode) == false) {
							// update this entry with new auditor post id
							continue;
						}
						lObjRltAuditorBankVO.setBankCode(lStrBankCode);
						lObjRltAuditorBankVO.setBranchCode(lStrArrBranchCode[lIntCnt].trim());
						lObjRltAuditorBankVO.setPostId(Long.parseLong(lStrPostId));
						lObjRltAuditorBankVO.setCreatedUserId(gLngUserId);
						lObjRltAuditorBankVO.setCreatedPostId(gLngPostId);
						lObjRltAuditorBankVO.setCreatedDate(gDateDBDate);
						lObjRltAuditorBankVO.setUpdatedDate(null);
						lObjRltAuditorBankVO.setUpdatedPostId(null);
						lObjRltAuditorBankVO.setUpdatedUserId(null);
						lObjRltAuditorBankVO.setPensionScheme("IRLA");
						lObjRltAuditorBankVO.setBillType(null);
						lObjRltAuditorBankVO.setLocationCode(gStrLocationCode);
						lObjRltAuditorBankVO.setTrnCounter(1);

						lLstRltAuditorBankVO.add(lObjRltAuditorBankVO);

					}
				}
			}
			
			/*
			 * if (lStrRemoveValue.length() > 1) { lStrArrRemoveValue =
			 * lStrRemoveValue.split("~"); for (int lIntCnt = 0; lIntCnt <
			 * lStrArrRemoveValue.length; lIntCnt++) {
			 * lObjPensionConfigDAO.removeMapping(lStrPostId, lStrBankCode,
			 * lStrArrRemoveValue[lIntCnt], gStrLocationCode);
			 * 
			 * } }
			 */
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error in generateRltAuditorBankVO method is :" + e, e);

		}

		return lLstRltAuditorBankVO;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.pensionpay.service.PensionConfigVOGenerator#
	 * generateMstPensionHeadcodeVO(java.util.Map)
	 */
	public MstPensionHeadcode generateMstPensionHeadcodeVO(Map inputMap) {

		gLogger.info("In generateMstPensionHeadcodeVO method........");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		MstPensionHeadcode lObjMstPensionHeadcodeVO = new MstPensionHeadcode();
		PensionConfigDAO lObjPensionConfigDAO = null;
		try {
			setSessionInfo(inputMap);

			lObjPensionConfigDAO = new PensionConfigDAOImpl(null, serv.getSessionFactory());

			BigDecimal lBDheadCode = null;
			String lStrSeries = null;
			String lStrdescription = null;
			String lStrMainCatId = StringUtility.getParameter("cmbMainCategoryId", request);
			String lStrMainCatDesc = StringUtility.getParameter("cmbMainCategoryDesc", request);
			lStrSeries = StringUtility.getParameter("txtPnsnrCtgry", request);

			lStrdescription = StringUtility.getParameter("txtDescription", request);

			lBDheadCode = lObjPensionConfigDAO.getMaxHeadCode();

			if (lStrSeries != "" && lStrSeries.length() > 0) {
				lObjMstPensionHeadcodeVO.setSeries(lStrSeries);
			}
			if (lStrdescription != "" && lStrdescription.length() > 0) {
				lObjMstPensionHeadcodeVO.setDescription(lStrdescription);
			}

			Long lLngHeadCode = lBDheadCode.longValue() + 1L;

			lObjMstPensionHeadcodeVO.setHeadCode(BigDecimal.valueOf(lLngHeadCode));

			lObjMstPensionHeadcodeVO.setDescription(lStrdescription);
			lObjMstPensionHeadcodeVO.setLangId(BigDecimal.valueOf(gLngLangId));
			lObjMstPensionHeadcodeVO.setCreatedUserId(BigDecimal.valueOf(gLngUserId));
			lObjMstPensionHeadcodeVO.setCreatedPostId(BigDecimal.valueOf(gLngPostId));
			lObjMstPensionHeadcodeVO.setCreatedDate(gDateDBDate);
			lObjMstPensionHeadcodeVO.setUpdatedDate(gDateDBDate);
			lObjMstPensionHeadcodeVO.setUpdatedPostId(BigDecimal.valueOf(gLngPostId));
			lObjMstPensionHeadcodeVO.setUpdatedUserId(BigDecimal.valueOf(gLngUserId));
			lObjMstPensionHeadcodeVO.setMainCatCode(Integer.valueOf(lStrMainCatId.trim()));
			lObjMstPensionHeadcodeVO.setMainCatDesc(lStrMainCatDesc.trim());

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error in generateMstPensionHeadcodeVO method is :" + e, e);
		}
		return lObjMstPensionHeadcodeVO;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.pensionpay.service.PensionConfigVOGenerator#
	 * generateRltPensionHeadcodeChargableVO(java.util.Map)
	 */
	public List<RltPensionHeadcodeChargable> generateRltPensionHeadcodeChargableVO(Map inputMap) {

		gLogger.info("In generateRltPensionHeadcodeChargableVO method........");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		List<RltPensionHeadcodeChargable> lLstRltPensionHeadcodeChargableVO = new ArrayList<RltPensionHeadcodeChargable>();
		RltPensionHeadcodeChargable lObjRltPensionHeadcodeChargableVO = null;
		PensionConfigDAO lObjPensionConfigDAO = null;
		try {
			setSessionInfo(inputMap);

			String lStrPnsnSchemeCode = null;
			String lStrCvpSchemeCode = null;
			String lStrDcrgSchemeCode = null;
			Long lLngFinYearId = null;
			BigDecimal lBDHeadCode = null;

			DateFormat df = DateFormat.getDateInstance();
			String lStrCurDate = df.format(gDateDBDate);
			String lStrCurYear = lStrCurDate.substring(lStrCurDate.length() - 4, lStrCurDate.length());

			lObjPensionConfigDAO = new PensionConfigDAOImpl(null, serv.getSessionFactory());
			lBDHeadCode = lObjPensionConfigDAO.getMaxHeadCode();

			Long lLngHeadCode = lBDHeadCode.longValue() + 1L;

			lStrPnsnSchemeCode = (StringUtility.getParameter("txtPensionSchemeCode", request).trim().length() > 0) ? StringUtility.getParameter("txtPensionSchemeCode", request).trim() : null;

			lStrCvpSchemeCode = (StringUtility.getParameter("txtCvpSchemeCode", request).trim().length() > 0) ? StringUtility.getParameter("txtCvpSchemeCode", request).trim() : null;

			lStrDcrgSchemeCode = (StringUtility.getParameter("txtDcrgSchemeCode", request).trim().length() > 0) ? StringUtility.getParameter("txtDcrgSchemeCode", request).trim() : null;

			lLngFinYearId = lObjPensionConfigDAO.getFinYearIdFromCurrDt(lStrCurYear);

			for (int lIntCnt = 0; lIntCnt < 3; lIntCnt++) {
				lObjRltPensionHeadcodeChargableVO = new RltPensionHeadcodeChargable();

				lObjRltPensionHeadcodeChargableVO.setheadCode(lLngHeadCode);
				if (lIntCnt == 0) {
					lObjRltPensionHeadcodeChargableVO.setBillType(gObjRsrcBndle.getString("PPMT.PENSION1"));
					lObjRltPensionHeadcodeChargableVO.setSchemeCode(lStrPnsnSchemeCode);
				}
				if (lIntCnt == 1) {
					lObjRltPensionHeadcodeChargableVO.setBillType(gObjRsrcBndle.getString("PPMT.CVP"));
					lObjRltPensionHeadcodeChargableVO.setSchemeCode(lStrCvpSchemeCode);
				}
				if (lIntCnt == 2) {
					lObjRltPensionHeadcodeChargableVO.setBillType(gObjRsrcBndle.getString("PPMT.DCRG"));
					lObjRltPensionHeadcodeChargableVO.setSchemeCode(lStrDcrgSchemeCode);
				}

				lObjRltPensionHeadcodeChargableVO.setFinYearId(lLngFinYearId);
				lObjRltPensionHeadcodeChargableVO.setDemandNo(null);
				lObjRltPensionHeadcodeChargableVO.setMjrhdCode(null);
				lObjRltPensionHeadcodeChargableVO.setSubmjrhdCode(null);
				lObjRltPensionHeadcodeChargableVO.setMinhdCode(null);
				lObjRltPensionHeadcodeChargableVO.setSubhdCode(null);
				lObjRltPensionHeadcodeChargableVO.setDtlhdCode(null);
				lObjRltPensionHeadcodeChargableVO.setEdpCode(null);
				lObjRltPensionHeadcodeChargableVO.setCreatedUserId(BigDecimal.valueOf(gLngUserId));
				lObjRltPensionHeadcodeChargableVO.setCreatedPostId(BigDecimal.valueOf(gLngPostId));
				lObjRltPensionHeadcodeChargableVO.setCreatedDate(gDateDBDate);
				lObjRltPensionHeadcodeChargableVO.setUpdatedDate(gDateDBDate);
				lObjRltPensionHeadcodeChargableVO.setUpdatedPostId(BigDecimal.valueOf(gLngPostId));
				lObjRltPensionHeadcodeChargableVO.setUpdatedUserId(BigDecimal.valueOf(gLngUserId));
				lObjRltPensionHeadcodeChargableVO.setActiveFlag("Y");

				lLstRltPensionHeadcodeChargableVO.add(lObjRltPensionHeadcodeChargableVO);

			}

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error in generateRltAuditorBankVO method is :" + e, e);

		}

		return lLstRltPensionHeadcodeChargableVO;

	}

	/**
	 * Generate MstChangeStmntCtgryVO.
	 * 
	 * @param lMapInput
	 * @return MstChangeStmntCtgry List
	 */
	public ResultObject generateMapForMstChangeStmntCtgry(Map<String, Object> lMapInput) {

		gLogger.info("In generateRltPensionHeadcodeChargableVO method........");
		HttpServletRequest request = (HttpServletRequest) lMapInput.get("requestObj");
		ServiceLocator serv = (ServiceLocator) lMapInput.get("serviceLocator");
		List<MstChangeStmntCtgry> lLstMstChangeStmntCtgry = new ArrayList<MstChangeStmntCtgry>();
		MstChangeStmntCtgry lObjMstChangeStmntCtgry = null;
		ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionConstants");
		PensionConfigDAO lObjPensionConfigDAO = null;
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		try {

			setSessionInfo(lMapInput);
			lObjPensionConfigDAO = new PensionConfigDAOImpl(MstChangeStmntCtgry.class, serv.getSessionFactory());
			String lStrCategoryId = StringUtility.getParameter("ctgryId", request);
			String[] lStrArrCategoryId = lStrCategoryId.split("~");
			List<Integer> lLstCategoryId = new ArrayList<Integer>();
			for (int lIntCnt = 0; lIntCnt < lStrArrCategoryId.length; lIntCnt++) {
				lLstCategoryId.add(Integer.valueOf(lStrArrCategoryId[lIntCnt].trim()));
			}
			lObjPensionConfigDAO.removeCtgryIfExists(gStrLocationCode);
			for (int lIntCnt = 0; lIntCnt < lStrArrCategoryId.length; lIntCnt++) {
				lObjMstChangeStmntCtgry = new MstChangeStmntCtgry();

				lObjMstChangeStmntCtgry.setCategoryId(Integer.valueOf(lStrArrCategoryId[lIntCnt].trim()));
				lObjMstChangeStmntCtgry.setCategoryDesc(bundleConst.getString("CHANGSTMNT.CATEGORY" + lStrArrCategoryId[lIntCnt].trim()));
				lObjMstChangeStmntCtgry.setLocationCode(Long.valueOf(gStrLocationCode));
				lObjMstChangeStmntCtgry.setDbId(BigDecimal.valueOf(SessionHelper.getDbId(lMapInput)));
				lObjMstChangeStmntCtgry.setCreatedUserId(BigDecimal.valueOf(gLngUserId));
				lObjMstChangeStmntCtgry.setCreatedPostId(BigDecimal.valueOf(gLngPostId));
				lObjMstChangeStmntCtgry.setCreatedDate(gDateDBDate);
				lObjMstChangeStmntCtgry.setUpdatedDate(gDateDBDate);
				lObjMstChangeStmntCtgry.setUpdatedPostId(BigDecimal.valueOf(gLngPostId));
				lObjMstChangeStmntCtgry.setUpdatedUserId(BigDecimal.valueOf(gLngUserId));
				lLstMstChangeStmntCtgry.add(lObjMstChangeStmntCtgry);

			}
			lMapInput.put("lLstMstChangeStmntCtgry", lLstMstChangeStmntCtgry);
			lMapInput.put("Mode", "Add");
			objRes.setResultCode(ErrorConstants.SUCCESS);
			objRes.setResultValue(lMapInput);
		} catch (Exception e) {
			gLogger.error("Error is  " + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.pensionpay.service.PensionConfigVOGenerator#
	 * generateHstPnsnPmntPpoNoVO(java.util.Map)
	 */

	public ResultObject generateHstPnsnPmntPpoNoVO(Map inputMap) {

		gLogger.info("In generateHstPnsnPmntPpoNoVO method........");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		HstPnsnPmntPpoNo lObjHstPnsnPmntPpoNo = null;
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		try {

			setSessionInfo(inputMap);
			lObjHstPnsnPmntPpoNo = new HstPnsnPmntPpoNo();
			String lStrPensionerCode = StringUtility.getParameter("pnsnrCode", request);

			String lStrOldPpoNo = StringUtility.getParameter("oldPpoNo", request);
			String lStrNewPpoNo = StringUtility.getParameter("newPpoNo", request);

			lObjHstPnsnPmntPpoNo.setDbId(BigDecimal.valueOf(SessionHelper.getDbId(inputMap)));
			lObjHstPnsnPmntPpoNo.setLocationCode(Long.valueOf(gStrLocationCode));
			lObjHstPnsnPmntPpoNo.setPensionerCode(lStrPensionerCode);
			lObjHstPnsnPmntPpoNo.setOldppoNo(lStrOldPpoNo);
			lObjHstPnsnPmntPpoNo.setNewppoNo(lStrNewPpoNo);
			lObjHstPnsnPmntPpoNo.setRemarks(null);
			lObjHstPnsnPmntPpoNo.setCreatedUserId(BigDecimal.valueOf(gLngUserId));
			lObjHstPnsnPmntPpoNo.setCreatedPostId(BigDecimal.valueOf(gLngPostId));
			lObjHstPnsnPmntPpoNo.setCreatedDate(gDateDBDate);
			lObjHstPnsnPmntPpoNo.setUpdatedDate(gDateDBDate);
			lObjHstPnsnPmntPpoNo.setUpdatedPostId(BigDecimal.valueOf(gLngPostId));
			lObjHstPnsnPmntPpoNo.setUpdatedUserId(BigDecimal.valueOf(gLngUserId));

			inputMap.put("lObjHstPnsnPmntPpoNo", lObjHstPnsnPmntPpoNo);
			inputMap.put("Mode", "Add");
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.pensionpay.service.PensionConfigVOGenerator#
	 * generateMstPensionMainCategoryVO(java.util.Map)
	 */
	public ResultObject generateMstPensionMainCategoryVO(Map inputMap) {

		gLogger.info("In generateMstPensionMainCategoryVO method........");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		MstPensionMainCategory lObjMstPensionMainCategory = null;
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		PensionConfigDAO lObjPensionConfigDAO = null;
		try {

			setSessionInfo(inputMap);
			lObjMstPensionMainCategory = new MstPensionMainCategory();
			lObjPensionConfigDAO = new PensionConfigDAOImpl(MstPensionMainCategory.class, serv.getSessionFactory());
			String lStrMainCatId = null;
			String lStrVal = StringUtility.getParameter("hidString", request);
			String lStrMainCategoryDesc = StringUtility.getParameter("txtMainCtgryDesc", request);
			String lStrPnsnSchemeCode = StringUtility.getParameter("txtPensionSchemeCode", request).trim();
			String lStrCvpSchemeCode = StringUtility.getParameter("txtCvpSchemeCode", request).trim();
			String lStrDcrgSchemeCode = StringUtility.getParameter("txtDcrgSchemeCode", request).trim();

			if (!"".equals(lStrVal) && lStrVal.length() > 0) {
				if (lStrVal.equals("Update")) {
					lStrMainCatId = StringUtility.getParameter("hidMainCatId", request);
					if (!"".equals(lStrMainCatId) && lStrMainCatId.length() > 0) {
						lObjMstPensionMainCategory = (MstPensionMainCategory) lObjPensionConfigDAO.read(Long.valueOf(lStrMainCatId.trim()));
					}
					lObjMstPensionMainCategory.setUpdatedDate(gDateDBDate);
					lObjMstPensionMainCategory.setUpdatedPostId(BigDecimal.valueOf(gLngPostId));
					lObjMstPensionMainCategory.setUpdatedUserId(BigDecimal.valueOf(gLngUserId));

					inputMap.put("lObjMstPensionMainCategory", lObjMstPensionMainCategory);
					inputMap.put("Mode", "Update");

				}
				if (lStrVal.equals("Add")) {
					lObjMstPensionMainCategory.setCreatedUserId(BigDecimal.valueOf(gLngUserId));
					lObjMstPensionMainCategory.setCreatedPostId(BigDecimal.valueOf(gLngPostId));
					lObjMstPensionMainCategory.setCreatedDate(gDateDBDate);
					lObjMstPensionMainCategory.setUpdatedDate(gDateDBDate);
					lObjMstPensionMainCategory.setUpdatedPostId(BigDecimal.valueOf(gLngPostId));
					lObjMstPensionMainCategory.setUpdatedUserId(BigDecimal.valueOf(gLngUserId));
					lObjMstPensionMainCategory.setLangId(BigDecimal.valueOf(gLngLangId));

					if (!"".equals(lStrMainCategoryDesc) && lStrMainCategoryDesc.length() > 0)
						lObjMstPensionMainCategory.setMainCatDesc(lStrMainCategoryDesc);
					if (!"".equals(lStrPnsnSchemeCode) && lStrPnsnSchemeCode.length() > 0)
						lObjMstPensionMainCategory.setSchemeCodePension(lStrPnsnSchemeCode);
					if (!"".equals(lStrCvpSchemeCode) && lStrCvpSchemeCode.length() > 0)
						lObjMstPensionMainCategory.setSchemeCodeCVP(lStrCvpSchemeCode);
					if (!"".equals(lStrDcrgSchemeCode) && lStrDcrgSchemeCode.length() > 0)
						lObjMstPensionMainCategory.setSchemeCodeDCRG(lStrDcrgSchemeCode);

					inputMap.put("lObjMstPensionMainCategory", lObjMstPensionMainCategory);
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
