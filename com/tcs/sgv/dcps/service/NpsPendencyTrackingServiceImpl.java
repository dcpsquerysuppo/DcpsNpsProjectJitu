/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date                                Initials             Version            Changes and additions
 ******************************************************************************
 *      April 3, 2015           Ashish Sharma                                                      
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.NpsPendencyTrackingDAOImpl;

//import cra.standalone.paosubcontr.PAOFvu;

/**
 * Class Description -
 * 
 * 
 * @author Ashish Sharma
 * @version 0.1
 * @since JDK 7.0 Arp 3, 2015
 */
public class NpsPendencyTrackingServiceImpl extends ServiceImpl {

	/* Global Variable for Logger Class */
	private final Log gLogger = LogFactory.getLog(getClass());

	private String gStrPostId = null; /* STRING POST ID */

	private String gStrLocationCode = null;

	private Long gLngPostId = null;

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	private HttpSession session = null; /* SESSION */

	private Date gDtCurDate = null; /* CURRENT DATE */

	private String gStrUserId = null; /* STRING USER ID */

	private HttpServletResponse response = null;/* RESPONSE OBJECT */

	/* Global Variable for UserId */
	Long gLngUserId = null;

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
			gDtCurDate = SessionHelper.getCurDate();
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
		}
	}

	public ResultObject loadNPSNSDLForm(Map inputMap) {

		ResultObject resObj = new ResultObject(0, "FAIL");
		String lStrTempFromDate = null;
		try {
			setSessionInfo(inputMap);
			NpsPendencyTrackingDAOImpl lObjNsdlDAO = new NpsPendencyTrackingDAOImpl(null,this.serv.getSessionFactory());
			String strRoleId=lObjNsdlDAO.getRoleId(gLngPostId);

			List lLstYears = lObjNsdlDAO.getFinyear();
			List lLstMonths = lObjNsdlDAO.getFinMonth();
			List subTr=null;
			if(strRoleId.equalsIgnoreCase("700003") || strRoleId.equalsIgnoreCase("451467895"))
			{
				subTr = lObjNsdlDAO.getSubTreasury(this.gStrLocationCode);
				//inputMap.put("strDDOCode", this.gStrLocationCode);

			}
			else if (strRoleId.equalsIgnoreCase("700004")||strRoleId.equalsIgnoreCase("700016"))
			{
				subTr = lObjNsdlDAO.getAllSubTreasury();
				//inputMap.put("strDDOCode", this.gStrLocationCode);

			}
			/*else if (strRoleId.equalsIgnoreCase("700001"))
			{
				String ddoPostId=lObjNsdlDAO.getDDOPostID(gLngPostId);

				String strDDOCode=lObjNsdlDAO.getDdoCodeForDDO(Long.parseLong(ddoPostId));
				subTr = lObjNsdlDAO.getAllDDo(strDDOCode);
				inputMap.put("strDDOCode", strDDOCode);

			}*/
			
			
			
			inputMap.put("YEARS", lLstYears);
			inputMap.put("MONTHS", lLstMonths);
			inputMap.put("subTr", subTr);
			inputMap.put("strRoleId", strRoleId);
			resObj.setResultValue(inputMap);
			resObj.setViewName("NpsPendencyTracking");
		} catch (Exception e) {
			e.printStackTrace();
			this.gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(-1);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	private String getFinYear(int month, int year) {
		gLogger.info("month:" + month + "year:" + year);
		if (month >= 4 && month <= 12) {

			long nxtYear = year + 1;
			gLogger.info("year:" + year + "nxtYear:" + nxtYear);
			return year + "," + nxtYear;
		} else if (month >= 1 && month < 4) {
			long prevYear = year - 1;
			return prevYear + "," + year;
		}
		return "";
	}

}
