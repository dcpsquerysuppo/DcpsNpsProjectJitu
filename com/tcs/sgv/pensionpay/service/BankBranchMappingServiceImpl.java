/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Sep 26, 2011		383385								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.dao.CommonDAO;
import com.tcs.sgv.common.dao.CommonDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.dao.BankBranchMappingDAO;
import com.tcs.sgv.pensionpay.dao.BankBranchMappingDAOImpl;
import com.tcs.sgv.pensionpay.valueobject.BankMpg;
import com.tcs.sgv.pensionpay.valueobject.PasBankMaster;
import com.tcs.sgv.pensionpay.valueobject.PasBranchMaster;
import com.tcs.sgv.pensionpay.valueobject.RltBankBranchTmp;



/**
 * Class Description - 
 *
 *
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0
 * Sep 26, 2011
 */
public class BankBranchMappingServiceImpl  extends ServiceImpl implements BankBranchMappingService{

	private Long gLngPostId = null;

	private Long gLngUserId = null;

	private Long gLngLangId = null;

	private Log gLogger = LogFactory.getLog(getClass());

	private Date gCurrDate = null;

	private String gStrLocCode = null;

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	private void setSessionInfo(Map<String, Object> inputMap) {

		gLngLangId = SessionHelper.getLangId(inputMap);
		gLngPostId = SessionHelper.getPostId(inputMap);
		gLngUserId = SessionHelper.getUserId(inputMap);
		gStrLocCode = SessionHelper.getLocationCode(inputMap);
		gLngLangId = SessionHelper.getLangId(inputMap);
		gCurrDate = DBUtility.getCurrentDateFromDB();
		serv = (ServiceLocator) inputMap.get("serviceLocator");
	}
	
	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.service.BankBranchMappingService#loadBankBranchMapping(java.util.Map)
	 */
	public ResultObject loadBankBranchMapping(Map<String, Object> inputMap) {

		// TODO Auto-generated method stub
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List<ComboValuesVO> lLstPasBank = new ArrayList<ComboValuesVO>();
		List<ComboValuesVO> lLstDistrict = new ArrayList<ComboValuesVO>();
		
		try
		{
			BankBranchMappingDAO lObjBankBranchMappingDAO = new BankBranchMappingDAOImpl(PasBankMaster.class, serv.getSessionFactory());
			lLstPasBank = lObjBankBranchMappingDAO.getPasBankList(gStrLocCode);
			lLstDistrict = lObjBankBranchMappingDAO.getDistrictList(gStrLocCode);
			
			inputMap.put("lLstPasBank", lLstPasBank);
			inputMap.put("lLstDistrict", lLstDistrict);
						
			resObj.setViewName("BankBranchMapping");
			resObj.setResultValue(inputMap);
		}
		catch (Exception e) {
				//e.printStackTrace();
				gLogger.error("Error is;" + e, e);
				resObj.setResultValue(null);
				resObj.setThrowable(e);
				resObj.setResultCode(ErrorConstants.ERROR);
				resObj.setViewName("errorPage");

		}
		return resObj;
	}
	
	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.service.BankBranchMappingService#getPasBranchListFromBankCode(java.util.Map)
	 */
	public ResultObject getPasBranchListFromBankCode(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		String lStrPasBankCode = null;
		String lStrAjaxResult = null;
		List<ComboValuesVO> lLstPasBank = new ArrayList<ComboValuesVO>();
		List lLstPasBranch = new ArrayList();
		List<ComboValuesVO> lLstDistrict = new ArrayList<ComboValuesVO>();
		setSessionInfo(inputMap);
		try {
			
			lStrPasBankCode = StringUtility.getParameter("bankCode", request);
					
			BankBranchMappingDAO lObjBankBranchMappingDAO = new BankBranchMappingDAOImpl(PasBranchMaster.class, serv.getSessionFactory());
			lLstPasBank = lObjBankBranchMappingDAO.getPasBankList(gStrLocCode);
			lLstDistrict = lObjBankBranchMappingDAO.getDistrictList(gStrLocCode);
			inputMap.put("lLstPasBank", lLstPasBank);
			inputMap.put("lLstDistrict", lLstDistrict);
//			if (!"".equals(lStrPasBankCode) && lStrPasBankCode.length() > 0) {
//				lLstPasBranch = lObjBankBranchMappingDAO.getPasBranchListFromBankCode(gStrLocCode, lStrPasBankCode);
//				
//				lStrAjaxResult = new AjaxXmlBuilder().addItems(lLstPasBranch,
//						"desc", "id").toString();
//				inputMap.put("ajaxKey", lStrAjaxResult);
//				resObj.setViewName("ajaxData");
//			} else 
			if (!"".equals(lStrPasBankCode)) {
				lLstPasBranch = lObjBankBranchMappingDAO.getPasBranchListFromBankCode(gStrLocCode, lStrPasBankCode);
				
//				inputMap.put("lLstPasBranch", lLstPasBranch);
			}
			inputMap.put("lLstPasBranch", lLstPasBranch);
			inputMap.put("PasBankCode", lStrPasBankCode);
			resObj.setViewName("BankBranchMapping");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	
	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.service.BankBranchMappingService#getBankListFromDistrict(java.util.Map)
	 */
	public ResultObject getBankListFromDistrict(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		String lStrDistrictName = null;
		String lStrAjaxResult = null;
		List<ComboValuesVO> lLstBank = new ArrayList<ComboValuesVO>();
		setSessionInfo(inputMap);
		try {
			
			lStrDistrictName = StringUtility.getParameter("districtName", request);
					
			BankBranchMappingDAO lObjBankBranchMappingDAO = new BankBranchMappingDAOImpl(RltBankBranchTmp.class, serv.getSessionFactory());
		
			if (!"".equals(lStrDistrictName) && lStrDistrictName.length() > 0) {
				lLstBank = lObjBankBranchMappingDAO.getBankListFromDistrict(gStrLocCode, lStrDistrictName);
				
				lStrAjaxResult = new AjaxXmlBuilder().addItems(lLstBank,
						"desc", "id").toString();
				inputMap.put("ajaxKey", lStrAjaxResult);
				resObj.setViewName("ajaxData");
			} else 
			if (!"".equals(lStrDistrictName)) {
				lLstBank = lObjBankBranchMappingDAO.getBankListFromDistrict(gStrLocCode, lStrDistrictName);
				
				inputMap.put("lLstBank", lLstBank);
			}
		
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	
	
	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.service.BankBranchMappingService#loadBankBranchDtls(java.util.Map)
	 */
	public ResultObject loadBankBranchDtls(Map<String, Object> inputMap) {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List<ComboValuesVO> lLstBanks = new ArrayList<ComboValuesVO>();
		List<ComboValuesVO> lLstCity = new ArrayList<ComboValuesVO>();
		try
		{
			CommonDAO lObjCommonDAO = new CommonDAOImpl(serv.getSessionFactory());
			lLstBanks = lObjCommonDAO.getBankList(inputMap, gLngLangId);
				
			inputMap.put("lLstBanks", lLstBanks);
				
			resObj.setViewName("BankBranchDetails");
			resObj.setResultValue(inputMap);
		}
		catch (Exception e) {
				//e.printStackTrace();
				gLogger.error("Error is;" + e, e);
				resObj.setResultValue(null);
				resObj.setThrowable(e);
				resObj.setResultCode(ErrorConstants.ERROR);
				resObj.setViewName("errorPage");

		}
		return resObj;
	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.service.BankBranchMappingService#getBranchsFromBankCode(java.util.Map)
	 */
	public ResultObject getBranchsFromBankCode(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		String lStrDistrictName = null;
		String lStrBankCode=null;
		String lStrAjaxResult = null;
		List<RltBankBranchTmp> lLstBranch = new ArrayList<RltBankBranchTmp>();
		setSessionInfo(inputMap);
		try {
			
			lStrDistrictName = StringUtility.getParameter("districtName", request);
			lStrBankCode = StringUtility.getParameter("bankCode", request);
			
			BankBranchMappingDAO lObjBankBranchMappingDAO = new BankBranchMappingDAOImpl(RltBankBranchTmp.class, serv.getSessionFactory());
		
			if (!"".equals(lStrDistrictName) && lStrDistrictName.length() > 0 && !"".equals(lStrBankCode))  {
				lLstBranch = lObjBankBranchMappingDAO.getBranchsFromBankCode(gStrLocCode, lStrBankCode, lStrDistrictName);
				
				String lStrBranchDtls = getResponseXMLDocForBranch(lLstBranch).toString();
								
				lStrAjaxResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBranchDtls).toString();
				inputMap.put("ajaxKey", lStrAjaxResult);
				resObj.setViewName("ajaxData");
			} else 
			if (!"".equals(lStrDistrictName)  && !"".equals(lStrBankCode)) {
				lLstBranch = lObjBankBranchMappingDAO.getBranchsFromBankCode(gStrLocCode, lStrBankCode, lStrDistrictName);
				
				inputMap.put("lLstBranch", lLstBranch);
			}
		
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	private StringBuilder getResponseXMLDocForBranch(List<RltBankBranchTmp> lLstBranches) {

		StringBuilder lStrBldXML = new StringBuilder();
		
		lStrBldXML.append("<XMLDOC>"); 
		for(RltBankBranchTmp lObjRltBankBranch : lLstBranches)
		{
			lStrBldXML.append("<BranchCode>");
			lStrBldXML.append(lObjRltBankBranch.getBranchCode());
			lStrBldXML.append("</BranchCode>");
			lStrBldXML.append("<BranchName>");
			lStrBldXML.append(lObjRltBankBranch.getBranchName());
			lStrBldXML.append("</BranchName>");
			lStrBldXML.append("<BranchAddrMicrCode>");
			if(lObjRltBankBranch.getMicrCode() != null)
				lStrBldXML.append(lObjRltBankBranch.getBranchAddress() + " - " + lObjRltBankBranch.getMicrCode()+ " - " + lObjRltBankBranch.getIfscCode());
			else
				lStrBldXML.append(lObjRltBankBranch.getBranchAddress() + " - " + lObjRltBankBranch.getIfscCode());
			lStrBldXML.append("</BranchAddrMicrCode>");
		}
		lStrBldXML.append("</XMLDOC>");
		
		return lStrBldXML;
	}
	
	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.service.BankBranchMappingService#mapPasBranch(java.util.Map)
	 */
	public ResultObject mapBankBranch(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		StringBuilder lStrBldXML = new StringBuilder();
		try {

			setSessionInfo(inputMap);
			String lStrIfmsBankCode = StringUtility.getParameter("ifmsBankCode", request);
			String lStrIfmsBranchCode = StringUtility.getParameter("ifmsBranchCode", request);
			String lStrPasBankCode = StringUtility.getParameter("pasBankCode", request);
			String lStrPasBranchCode = StringUtility.getParameter("pasBranchCode", request);
			String lStrBankMpgFlag = StringUtility.getParameter("bankMpgFlag", request);
			String lStrRowId = StringUtility.getParameter("rowId", request);
			
			BankMpg lObjBankMpgVO = new BankMpg();
						
			BankBranchMappingDAO lObjBankBranchMappingDAO = new BankBranchMappingDAOImpl(BankMpg.class, serv.getSessionFactory());
			if("Y".equals(lStrBankMpgFlag))
			{
				lObjBankBranchMappingDAO.deleteBankMpgDtls(gStrLocCode, lStrPasBankCode);
				if(!"".equals(lStrIfmsBankCode))
					lObjBankMpgVO.setIfmsBankCode(Long.parseLong(lStrIfmsBankCode));
				if(!"".equals(lStrPasBankCode))
					lObjBankMpgVO.setPasBankCode(Long.parseLong(lStrPasBankCode));
				lObjBankMpgVO.setLocationCode(gStrLocCode);
				lObjBankBranchMappingDAO.create(lObjBankMpgVO);
				
			}
			lObjBankBranchMappingDAO = new BankBranchMappingDAOImpl(PasBranchMaster.class, serv.getSessionFactory());
			if(!"".equals(lStrPasBranchCode) && !"".equals(lStrPasBankCode) && !"".equals(lStrIfmsBankCode) && !"".equals(lStrIfmsBranchCode))
			{
				lObjBankBranchMappingDAO.updateBankBranchMapping(gStrLocCode, lStrIfmsBankCode, lStrIfmsBranchCode, lStrPasBankCode, lStrPasBranchCode);
				
				lStrBldXML.append("<XMLDOC>");
				lStrBldXML.append("<ROWID>");
				lStrBldXML.append(lStrRowId);
				lStrBldXML.append("</ROWID>");
				lStrBldXML.append("<MESSAGECODE>");
				lStrBldXML.append("Branch Mapped Successfully.");
				lStrBldXML.append("</MESSAGECODE>");
				lStrBldXML.append("</XMLDOC>");
				String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
				inputMap.put("ajaxKey", lStrResult);
				resObj.setResultValue(inputMap);
				resObj.setViewName("ajaxData");
			}
					
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}
	
}
