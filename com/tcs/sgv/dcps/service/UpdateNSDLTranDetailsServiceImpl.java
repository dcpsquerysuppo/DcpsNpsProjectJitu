package com.tcs.sgv.dcps.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cra.common.util.CRAConfigReader;
import com.cra.common.util.encrypt.Hash;
import com.cra.common.util.file.CRABasicFileWriter;
import com.cra.pao.fvu.SubContrFileFormatValidator;
import com.cra.pao.vo.PAOContrErrorFileVO;
import com.ibm.icu.util.StringTokenizer;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.util.EnglishDecimalFormat;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.NsdlNpsDAOImpl;
import com.tcs.sgv.dcps.dao.NsdlSrkaFileGeneDAOImpl;
import com.tcs.sgv.dcps.dao.UpdateNSDLDetailsDAOImpl;
import com.tcs.sgv.dcps.dao.UpdateNSDLTranDetailsDAOImpl;
import com.tcs.sgv.eis.service.IdGenerator;

import cra.standalone.paosubcontr.PAOFvu;

public class UpdateNSDLTranDetailsServiceImpl extends ServiceImpl  {

	private final Log gLogger = LogFactory.getLog(getClass());

	private String gStrPostId = null; /* STRING POST ID */

	private String gStrLocationCode = null;

	private Long gLngPostId = null;

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	private HttpSession session = null; /* SESSION */

	private Date gDtCurDate = null; /* CURRENT DATE */

	private String gStrUserId = null; /* STRING USER ID */

	private HttpServletResponse response= null;/* RESPONSE OBJECT*/

	/* Global Variable for UserId */
	Long gLngUserId = null;

	private Long gLngDBId = null; /* DB ID */

	/* Resource bundle for the constants */
	private ResourceBundle gObjRsrcBndle = ResourceBundle
	.getBundle("resources/dcps/DCPSConstants");

	/*
	 * Function to save the session specific details
	 */
	private void setSessionInfo(Map inputMap) {
		
		

		try {
			response = (HttpServletResponse) inputMap.get("responseObj");
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

	public ResultObject loadNPSNSDLTranUpdate(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrTempFromDate = null;
		String lStrTempToDate = null;
		String lStrFromDate = null;
		String lStrToDate = null;
		Date lDateFromDate = null;
		Date lDateToDate = null;
		List lListTotalDdowiseEntries = null;
		Long yearId = null;
		Long monthId=null;
		Long lLongEmployeeAmt=0L;
		Long lLongEmployerAmt=0L;
		Long TotalAmt=0L;
		int digiActivate=0;

		try {
			setSessionInfo(inputMap);
			String month = StringUtility.getParameter("cmbMonth", request);
			String year = StringUtility.getParameter("cmbYear", request);
			Calendar cal = Calendar.getInstance();
			String currmonth = (new Integer((cal.get(Calendar.MONTH) + 1))).toString();
			String curryear = (new Integer(cal.get(Calendar.YEAR))).toString();
			Long currentyear=null;
			Long currentmonth=null;
			if(currmonth.equals("1")){
				currentmonth= 12L;
				currentyear=Long.parseLong(curryear)-1;

			}
			else{
				currentmonth= Long.parseLong(currmonth)-1;
				currentyear=Long.parseLong(curryear);

			}
			String trCode=gStrLocationCode;
			if(StringUtility.getParameter("cmbMonth", request)!=null && !(StringUtility.getParameter("cmbMonth", request).equals("")) && Long.parseLong(StringUtility.getParameter("cmbMonth", request))!=-1 
					&& StringUtility.getParameter("cmbYear", request)!=null && !(StringUtility.getParameter("cmbYear", request).equals("")) && Long.parseLong(StringUtility.getParameter("cmbYear", request))!=-1
					&& StringUtility.getParameter("trCode", request)!=null && !(StringUtility.getParameter("trCode", request).equals("")) && Long.parseLong(StringUtility.getParameter("trCode", request))!=-1){
				currentmonth= Long.parseLong(StringUtility.getParameter("cmbMonth", request));
				currentyear=Long.parseLong(StringUtility.getParameter("cmbYear", request));
				trCode=StringUtility.getParameter("trCode", request);
			}
			
			
			UpdateNSDLTranDetailsDAOImpl lObjNsdlDAO = new UpdateNSDLTranDetailsDAOImpl(null, serv.getSessionFactory());
			
			
			
			String fileIds=null;
			String tranIds=null;
		 
			

			if((StringUtility.getParameter("fileIds", request)!=null)&&(StringUtility.getParameter("fileIds", request)!="")){
				fileIds= StringUtility.getParameter("fileIds", request);
				tranIds= StringUtility.getParameter("tranIds", request);	
				 
				
				String[] fileids = fileIds.split("~");
				String[] tranids = tranIds.split("~");
			 
				
				String[] fileArray = new String[fileids.length];
				String[] tranArr = new String[tranids.length];
				 
				
				for (Integer lInt = 0; lInt < fileids.length; lInt++)
				{
					if (fileids[lInt] != null && !"".equals(fileids[lInt]))
					{
						fileArray[lInt] = fileids[lInt];
						tranArr[lInt] = tranids[lInt];
						 
							
						gLogger.info("hii********** "+fileArray[lInt]);
						gLogger.info("hii********** "+tranArr[lInt]);
						 
					}
				}

				for (Integer lInt = 0; lInt < fileids.length; lInt++)
				{
					lObjNsdlDAO.updateTransactionDetails(fileArray[lInt],tranArr[lInt]);
//					lObjNsdlDAO.updateTransactionDetails(fileArray[lInt],tranArr[lInt],year,month);


				}
			}
			
			
			
			digiActivate=lObjNsdlDAO.getDigiActivationDtls(gStrLocationCode);
			

		

			List subTr=lObjNsdlDAO.getAllSubTreasury();
			
			List nsdlDeatils=lObjNsdlDAO.getAllData(currentyear.toString(), currentmonth.toString(), trCode);
			List lLstYears = lObjNsdlDAO.getFinyear();
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());
			inputMap.put("selMonth", currentmonth);
			inputMap.put("trCode", trCode);
			inputMap.put("selYear", currentyear);
			inputMap.put("subTr", subTr);
			inputMap.put("digiActivate", digiActivate);
			List lLstMonths=lObjDcpsCommonDAO.getMonths();
			inputMap.put("size", nsdlDeatils.size());
			inputMap.put("nsdlDeatils", nsdlDeatils);

			gLogger.info("Month and year is "+lLstMonths.size());
			inputMap.put("lLstYears", lLstYears);
			inputMap.put("lLstMonths", lLstMonths);

			resObj.setResultValue(inputMap);
			resObj.setViewName("UpdateNPSTRANID");

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
}

