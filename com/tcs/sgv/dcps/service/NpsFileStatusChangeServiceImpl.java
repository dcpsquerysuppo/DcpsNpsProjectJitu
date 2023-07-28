
package com.tcs.sgv.dcps.service; 

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.NpsFileStatusChangeDAO;
import com.tcs.sgv.dcps.dao.NpsFileStatusChangeDAOImpl;

public class NpsFileStatusChangeServiceImpl  extends ServiceImpl 
{

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

	private HttpServletResponse response= null;/* RESPONSE OBJECT*/

	/* Global Variable for UserId */
	Long gLngUserId = null;

	private Long gLngDBId = null; /* DB ID */

	/* Resource bundle for the constants */

	//private ResourceBundle integrationBundleConst = ResourceBundle
	//.getBundle("resources/dcps/DCPSConstants");
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
			gLogger.info(" gLngUserId ssssss : " + gLngUserId);
			gStrUserId = gLngUserId.toString();
			gLngDBId = SessionHelper.getDbId(inputMap);
			gDtCurDate = SessionHelper.getCurDate();
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
		}
	}

	public ResultObject loadNPSNSDLGenFiles(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String cmbDep="2";
		List nsdlDeatils=null;
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
			gLogger.info("currentmonth is *******************"+currentmonth);
			gLogger.info("currentyear is *******************"+currentyear);

			String trCode=null;
			if(StringUtility.getParameter("cmbMonth", request)!=null && !(StringUtility.getParameter("cmbMonth", request).equals("")) && Long.parseLong(StringUtility.getParameter("cmbMonth", request))!=-1 
					&& StringUtility.getParameter("cmbYear", request)!=null && !(StringUtility.getParameter("cmbYear", request).equals("")) && Long.parseLong(StringUtility.getParameter("cmbYear", request))!=-1
					&& StringUtility.getParameter("trCode", request)!=null && !(StringUtility.getParameter("trCode", request).equals("")) && Long.parseLong(StringUtility.getParameter("trCode", request))!=-1
			){

				currentmonth= Long.parseLong(StringUtility.getParameter("cmbMonth", request));
				currentyear=Long.parseLong(StringUtility.getParameter("cmbYear", request));
				trCode=StringUtility.getParameter("trCode", request);
				if(StringUtility.getParameter("cmbDep", request)!=null && !(StringUtility.getParameter("cmbDep", request).equals("")) && Long.parseLong(StringUtility.getParameter("cmbDep", request))!=-1)
				{
					cmbDep=StringUtility.getParameter("cmbDep", request);
				}

				gLogger.info("currentmonth is *******************"+currentmonth);
				gLogger.info("currentyear is *******************"+currentyear);
				gLogger.info("trCode is *******************"+trCode);
				gLogger.info("cmbDep is *******************"+cmbDep);

			}

			NpsFileStatusChangeDAOImpl lObjNsdlDAO = new NpsFileStatusChangeDAOImpl(null, serv.getSessionFactory());
			gLogger.info("trCode is *******************"+trCode);

			//	List subTr=lObjNsdlDAO.getAllSubTreasury(gStrLocationCode);
			if(trCode!=null){
			if(cmbDep!=null && !"".equalsIgnoreCase(cmbDep) && "2".equalsIgnoreCase(cmbDep))
			{
				nsdlDeatils=lObjNsdlDAO.getAllData(currentyear.toString(), currentmonth.toString(), trCode);
			}
			else
			{
				nsdlDeatils=lObjNsdlDAO.getAllDataDeputation(currentyear.toString(), currentmonth.toString(), trCode);
			}
			}
			gLogger.info("nsdlDeatils is *******************"+nsdlDeatils);

			List lLstYears = lObjNsdlDAO.getFinyear();
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());
			inputMap.put("selMonth", currentmonth);
			//	inputMap.put("subTr", subTr);
			inputMap.put("selYear", currentyear);

			List lLstMonths=lObjDcpsCommonDAO.getMonths();
			if(trCode!=null){
			inputMap.put("size", nsdlDeatils.size());
			}
			inputMap.put("nsdlDeatils", nsdlDeatils);
			
			//List lLstTreasuries = lObjDcpsCommonDAO.getAllTreasuriesForInterest();
			
			////$t 28-7-2021
		//	List lLstTreasuries = lObjNsdlDAO.getAllTreasuries(this.gStrLocationCode);////$t 28-7-2021
		//	inputMap.put("TREASURIES", lLstTreasuries);

			gLogger.info("Month and year is "+lLstMonths.size());
			inputMap.put("lLstYears", lLstYears);
			inputMap.put("lLstMonths", lLstMonths);
			inputMap.put("idDeputation",cmbDep);
			resObj.setResultValue(inputMap);
			resObj.setViewName("NpsFileStatusChange");


			/*	}
		else{
			inputMap.put("allowedOrNot", allowedOrNot);	
			inputMap.put("restrictmsg", "You are not allowed to use this functionality as on today. Kindly contact MDC for further action.");
			resObj.setResultCode(ErrorConstants.SUCCESS);
			resObj.setResultValue(inputMap);
			resObj.setViewName("NPSVALIDATE");
		}*/
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

	public ResultObject updateFileStatus(Map inputMap)
	{
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String txtReason = null;
		String txtFileName = null;
		String txtRemarks = null;
		String fileIds = null;
		Long lLngPkIdForFile = null;
		String flag= null;

		gLogger.info("inside service updateFileStatus");
		try
		{
			setSessionInfo(inputMap);
			txtReason = StringUtility.getParameter("txtReason", request).trim();
			txtFileName = StringUtility.getParameter("txtFileName",request).trim();
			txtRemarks = StringUtility.getParameter("txtRemarks",request).trim();
			fileIds = StringUtility.getParameter("fileIds",request).trim();

			String txtMonth = StringUtility.getParameter("txtMonth",request).trim();
			String txtYear = StringUtility.getParameter("txtYear",request).trim();
			String empContri = StringUtility.getParameter("empContri",request).trim();
			String emplyrContri = StringUtility.getParameter("emplyrContri",request).trim();
			String totalAmount = StringUtility.getParameter("totalAmount", request).trim();

			NpsFileStatusChangeDAO lObjNsdlDAO = new NpsFileStatusChangeDAOImpl(null, serv.getSessionFactory());
//			gLogger.info("FLAG:"+flag+" txtFileName"+txtFileName+" txtReason"+txtReason+" txtRemarks"+txtRemarks);
			gLogger.info("txtFileName********** "+txtFileName);
			gLogger.info("txtRemarks********** "+txtRemarks);
			gLogger.info("fileIds********** "+fileIds);
			gLogger.info("txtReason********** "+txtReason);
			gLogger.info("txtMonth********** "+txtMonth);
			gLogger.info("txtYear********** "+txtYear);
			gLogger.info("empContri********** "+empContri);
			gLogger.info("emplyrContri********** "+emplyrContri);
			gLogger.info("totalAmount********** "+totalAmount);
			if((StringUtility.getParameter("fileIds", request)!=null)&&(StringUtility.getParameter("txtReason", request)!="")&&(StringUtility.getParameter("txtRemarks", request)!="")){
				fileIds= StringUtility.getParameter("fileIds", request);
				txtReason= StringUtility.getParameter("txtReason", request);	
				txtRemarks= StringUtility.getParameter("txtRemarks", request);	
				
				empContri= StringUtility.getParameter("empContri", request);
				emplyrContri= StringUtility.getParameter("emplyrContri", request);	
				totalAmount= StringUtility.getParameter("totalAmount", request);	
				
				String[] fileids = fileIds.split("~");
				String[] Reason = txtReason.split("~");
				String[] Remarks = txtRemarks.split("~");
				
				String[] empContribution = empContri.split("~");
				String[] emplyrContribution = emplyrContri.split("~");
				String[] totalFinalAmount = totalAmount.split("~");
				
				String[] fileArray = new String[fileids.length];
				String[] reasonArray = new String[Reason.length];
				String[] remarksArray = new String[Remarks.length];
				
				String[] empContributionArray = new String[empContribution.length];
				String[] emplyrContributionArray = new String[emplyrContribution.length];
				String[] totalFinalAmountArray = new String[totalFinalAmount.length];
				
				for (Integer lInt = 0; lInt < fileids.length; lInt++)
				{
					if (fileids[lInt] != null && !"".equals(fileids[lInt]))
					{
						fileArray[lInt] = fileids[lInt];
						reasonArray[lInt] = Reason[lInt];
						remarksArray[lInt] = Remarks[lInt];

						empContributionArray[lInt] = empContribution[lInt];
						emplyrContributionArray[lInt] = emplyrContribution[lInt];
						totalFinalAmountArray[lInt] = totalFinalAmount[lInt];
						
						gLogger.info("hii********** "+fileArray[lInt]);
						gLogger.info("hii********** "+reasonArray[lInt]);
						gLogger.info("hii********** "+remarksArray[lInt]);
						
						gLogger.info("hii********** "+empContributionArray[lInt]);
						gLogger.info("hii********** "+emplyrContributionArray[lInt] );
						gLogger.info("hii********** "+totalFinalAmountArray[lInt] );
					}
				}

				for (Integer lInt = 0; lInt < fileids.length; lInt++)
				{
					lLngPkIdForFile = IFMSCommonServiceImpl.getNextSeqNum("NSDL_NPS_FILE", inputMap);
					gLogger.info("lLngPkIdForPran++++++++++++++"+lLngPkIdForFile);
					lObjNsdlDAO.insertFileDetails(lLngPkIdForFile,fileArray[lInt],reasonArray[lInt],gLngPostId,remarksArray[lInt],empContributionArray[lInt],emplyrContributionArray[lInt],totalFinalAmountArray[lInt],txtMonth,txtYear);

				}
				flag = lObjNsdlDAO.updateFileStatus(txtFileName);
			}
			
			gLogger.info("flag++++++++++"+flag);
			StringBuffer strbuflag = new StringBuffer();

			if(flag !=null){
				strbuflag.append("<XMLDOC>");
				strbuflag.append("<Flag>");
				strbuflag.append(flag);
				strbuflag.append("</Flag>");
				strbuflag.append("</XMLDOC>");
			}
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",strbuflag.toString()).toString();
			if(lStrResult !=null){
				inputMap.put("ajaxKey", lStrResult);
			}
			objRes.setResultValue(inputMap);
			objRes.setViewName("ajaxData");
		}

		catch (Exception e) {
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
	}


	private String getFinYear(int month, int year) {
		gLogger.info("month:"+month+"year:"+year);
		if (month >= 4 && month <= 12) {

			long nxtYear = year + 1;
			gLogger.info("year:"+year+"nxtYear:"+nxtYear);
			return year + "," + nxtYear;
		} else if (month >= 1 && month < 4) {
			long prevYear = year - 1;
			return prevYear + "," + year;
		}
		return "";
	}

}
