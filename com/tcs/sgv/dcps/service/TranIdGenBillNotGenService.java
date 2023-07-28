package com.tcs.sgv.dcps.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.MatchContriEntryDAO;
import com.tcs.sgv.dcps.dao.MatchContriEntryDAOImpl;
import com.tcs.sgv.dcps.dao.MatchContriEntryDAOTO;
import com.tcs.sgv.dcps.dao.MatchContriEntryDAOTOImpl;
import com.tcs.sgv.dcps.dao.NsdlReportsDAO;
import com.tcs.sgv.dcps.dao.NsdlReportsDAOImpl;
import com.tcs.sgv.dcps.dao.NsdlSrkaPranFileGenerationDAO;
import com.tcs.sgv.dcps.dao.NsdlSrkaPranFileGenerationDAOImpl;
import com.tcs.sgv.dcps.dao.OldTransactionIdUpdateDAO;
import com.tcs.sgv.dcps.dao.OldTransactionIdUpdateDAOImpl;
import com.tcs.sgv.dcps.dao.TranIdGenBillNotGenDAO;
import com.tcs.sgv.dcps.dao.TranIdGenBillNotGenDAOImpl;

public class TranIdGenBillNotGenService extends ServiceImpl 
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
/*
	public ResultObject updateOldTransactionId(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrTempFromDate = null;
		String lStrTempToDate = null;
		String lStrFromDate = null;
		String lStrToDate = null;
		Date lDateFromDate = null;
		Date lDateToDate = null;
		List lListTreasuriesMatchEntries = null;
		Long yearId = null;
		Long totalSAmount=0L;
		Long totalTAmount=0L;
		Long AcMain=null;
		try {

			 Sets the Session Information 
			setSessionInfo(inputMap);

			DummyOfficeDAO lObjDummyOfficeDAO = new DummyOfficeDAOImpl(
					null, serv.getSessionFactory());
			MatchContriEntryDAO lObjMatchEntryDAO = new MatchContriEntryDAOImpl(
					null, serv.getSessionFactory());
			MatchContriEntryDAOTO lObjMatchEntryDAOTO = new MatchContriEntryDAOTOImpl(
					null, serv.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());
			//OldTransactionIdUpdateDAO lObjNsdlReportsDAO = new OldTransactionIdUpdateDAOImpl(null, serv
				//	.getSessionFactory());
			
			TranIdGenBillNotGenDAO lObjOldTransactionIdUpdateDAO = new TranIdGenBillNotGenDAOImpl(null,serv.getSessionFactory()); 
			
			  Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");

	            //      lstAisType  = lObjAlIndSer.getAISlist();
	           
	     //       List treasury=lObjOldTransactionIdUpdateDAO.getAllTreasuries();

	            //      inputMap.put("lstAisType",lstAisType);  
	         //   inputMap.put("treasury",treasury);   
public List getFinyear() {

	String query = "select finYearId,finYearDesc from SgvcFinYearMst where finYearCode between '2007' and '2014' order by finYearCode ASC";
	List<Object> lLstReturnList = null;
	StringBuilder sb = new StringBuilder();
	sb.append(query);
	Query selectQuery = ghibSession.createQuery(sb.toString());
	List lLstResult = selectQuery.list();
	ComboValuesVO lObjComboValuesVO = null;

	if (lLstResult != null && lLstResult.size() != 0) {
		lLstReturnList = new ArrayList<Object>();
		Object obj[];
		for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
			obj = (Object[]) lLstResult.get(liCtr);
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId(obj[0].toString());
			lObjComboValuesVO.setDesc(obj[1].toString());
			lLstReturnList.add(lObjComboValuesVO);
		}
	} else {
		lLstReturnList = new ArrayList<Object>();
		lObjComboValuesVO = new ComboValuesVO();
		lObjComboValuesVO.setId("-1");
		lObjComboValuesVO.setDesc("--Select--");
		lLstReturnList.add(lObjComboValuesVO);
	}
	return lLstReturnList;
}

			
			//String allowedOrNot="1";//(String) lObjNsdlReportsDAO.getStatusOfDist(gStrLocationCode);
   		// gLogger.info("allowedOrNot for paybill " + allowedOrNot);
   		
			 Get Years 
	        	//List lLstYears = lObjDcpsCommonDAO.getFinyears();
			List lLstYears = lObjOldTransactionIdUpdateDAO.getFinyears();
		List lLstMonths= lObjDcpsCommonDAO.getMonths();
			List lLstDummyOffice= lObjDummyOfficeDAO.getDummyOffices();
		
		if(!StringUtility.getParameter("finYear",request).equalsIgnoreCase("") && StringUtility.getParameter("finYear",request) != null
				&& !StringUtility.getParameter("month",request).equalsIgnoreCase("") && StringUtility.getParameter("month",request) != null
				&& !StringUtility.getParameter("treasno",request).equalsIgnoreCase("") && StringUtility.getParameter("treasno",request) != null
				&& !StringUtility.getParameter("tid",request).equalsIgnoreCase("") && StringUtility.getParameter("tid",request) != null
				&& !StringUtility.getParameter("res",request).equalsIgnoreCase("") && StringUtility.getParameter("res",request) != null
				&& !StringUtility.getParameter("rem",request).equalsIgnoreCase("") && StringUtility.getParameter("rem",request) != null
				&& !StringUtility.getParameter("fid",request).equalsIgnoreCase("") && StringUtility.getParameter("fid",request) != null)
			{
			System.out.println("in serv");
				String tid=StringUtility.getParameter("tid", request);
				String res=StringUtility.getParameter("res", request);
				String rem=StringUtility.getParameter("rem", request);
				String fid=StringUtility.getParameter("fid", request);
			
				System.out.println("tid************"+tid);
				System.out.println("res************"+res);
				System.out.println("rem************"+rem);
				System.out.println("fid************"+fid);
				
				String lstrFinYear=StringUtility.getParameter("finYear", request);
				String lstrMonth=StringUtility.getParameter("month", request);
				String treasurynos=StringUtility.getParameter("treasno", request);
			
				System.out.println("treasurynos************"+treasurynos);
				
				String fids[]=fid.split("~");
				String tids[]=tid.split("~");
				String ress[]=res.split("~");
				String rems[]=rem.split("~");
				int j=0;
				for(int i=0;i<fids.length;i++){
				  j=lObjOldTransactionIdUpdateDAO.updateDetails(fids[i],tids[i],ress[i],rems[i]);
				}
				//gLogger.info("lLstDetails########################"+lLstDetails);
				inputMap.put("j", j);
				inputMap.put("lstrFinYear", lstrFinYear);
				inputMap.put("lstrMonth", lstrMonth);
				//inputMap.put("lLstDetails", lLstDetails);
				  inputMap.put("treasuryno",treasurynos);
				  
				
			}else if(!StringUtility.getParameter("finYear",request).equalsIgnoreCase("") && StringUtility.getParameter("finYear",request) != null
				&& !StringUtility.getParameter("month",request).equalsIgnoreCase("") && StringUtility.getParameter("month",request) != null
				&& !StringUtility.getParameter("treasno",request).equalsIgnoreCase("") && StringUtility.getParameter("treasno",request) != null)
			{
				System.out.println("in serv1");
				String lstrFinYear=StringUtility.getParameter("finYear", request);
				String lstrMonth=StringUtility.getParameter("month", request);
				String treasurynos=StringUtility.getParameter("treasno", request);
			
				System.out.println("treasurynos************"+treasurynos);
				
				List lLstDetails= lObjOldTransactionIdUpdateDAO.getDetails(lstrFinYear,lstrMonth,treasurynos);
				//gLogger.info("lLstDetails########################"+lLstDetails);
				
				inputMap.put("lstrFinYear", lstrFinYear);
				inputMap.put("lstrMonth", lstrMonth);
				inputMap.put("lLstDetails", lLstDetails);
				  inputMap.put("treasuryno",treasurynos);
				  
				  
				
			}
		System.out.println("after serv");
		
	
			
				//   gLogger.info("totalSAmount is ***"+totalSAmount);
				inputMap.put("lLstDummyOffice", lLstDummyOffice);
				inputMap.put("lLstYears", lLstYears);
				inputMap.put("lLstMonths", lLstMonths);
				
				
			System.out.println("Year list is "+lLstYears.size());



			resObj.setResultValue(inputMap);
			resObj.setViewName("OldTransactionIdUpdate");

			
	
		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}*/

	public ResultObject TIDGenBillNotGenReport(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrTempFromDate = null;
		String lStrTempToDate = null;
		String lStrFromDate = null;
		String lStrToDate = null;
		Date lDateFromDate = null;
		Date lDateToDate = null;
		List lListTreasuriesMatchEntries = null;
		Long yearId = null;
		Long totalSAmount=0L;
		Long totalTAmount=0L;
		Long AcMain=null;
		try {

			/* Sets the Session Information */
			setSessionInfo(inputMap);

		/*	DummyOfficeDAO lObjDummyOfficeDAO = new DummyOfficeDAOImpl(
					null, serv.getSessionFactory());
			MatchContriEntryDAO lObjMatchEntryDAO = new MatchContriEntryDAOImpl(
					null, serv.getSessionFactory());
			MatchContriEntryDAOTO lObjMatchEntryDAOTO = new MatchContriEntryDAOTOImpl(
					null, serv.getSessionFactory());*/
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());
			//OldTransactionIdUpdateDAO lObjNsdlReportsDAO = new OldTransactionIdUpdateDAOImpl(null, serv
				//	.getSessionFactory());
			
			TranIdGenBillNotGenDAO lObjTranIdGenBillNotGenDAO = new TranIdGenBillNotGenDAOImpl(null,serv.getSessionFactory()); 
			
			  Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");

	            //      lstAisType  = lObjAlIndSer.getAISlist();
	           
	           // List treasury=lObjOldTransactionIdUpdateDAO.getAllTreasuries();

	            //      inputMap.put("lstAisType",lstAisType);  
	          //  inputMap.put("treasury",treasury);   
/*public List getFinyear() {

	String query = "select finYearId,finYearDesc from SgvcFinYearMst where finYearCode between '2007' and '2014' order by finYearCode ASC";
	List<Object> lLstReturnList = null;
	StringBuilder sb = new StringBuilder();
	sb.append(query);
	Query selectQuery = ghibSession.createQuery(sb.toString());
	List lLstResult = selectQuery.list();
	ComboValuesVO lObjComboValuesVO = null;

	if (lLstResult != null && lLstResult.size() != 0) {
		lLstReturnList = new ArrayList<Object>();
		Object obj[];
		for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
			obj = (Object[]) lLstResult.get(liCtr);
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId(obj[0].toString());
			lObjComboValuesVO.setDesc(obj[1].toString());
			lLstReturnList.add(lObjComboValuesVO);
		}
	} else {
		lLstReturnList = new ArrayList<Object>();
		lObjComboValuesVO = new ComboValuesVO();
		lObjComboValuesVO.setId("-1");
		lObjComboValuesVO.setDesc("--Select--");
		lLstReturnList.add(lObjComboValuesVO);
	}
	return lLstReturnList;
}
*/
			
			//String allowedOrNot="1";//(String) lObjNsdlReportsDAO.getStatusOfDist(gStrLocationCode);
   		// gLogger.info("allowedOrNot for paybill " + allowedOrNot);
   		
			/* Get Years */
	        	//List lLstYears = lObjDcpsCommonDAO.getFinyears();
			List lLstYears = lObjTranIdGenBillNotGenDAO .getFinyears();
		List lLstMonths= lObjDcpsCommonDAO.getMonths();
		/*	List lLstDummyOffice= lObjDummyOfficeDAO.getDummyOffices();*/
		
		
				inputMap.put("lLstYears", lLstYears);
				inputMap.put("lLstMonths", lLstMonths);
				
				
		



			resObj.setResultValue(inputMap);
			resObj.setViewName("TranIdGenBillNotGen");

			
	
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