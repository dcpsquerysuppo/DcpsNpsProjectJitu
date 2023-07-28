package com.tcs.sgv.dcps.service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import com.tcs.sgv.dcps.dao.BankBranchMstDaoImpl;
import com.tcs.sgv.dcps.dao.NewRegDdoDAO;
import com.tcs.sgv.dcps.dao.NewRegDdoDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import java.util.List;

public class TestService extends ServiceImpl  {
	
	private final Log gLogger = LogFactory.getLog(getClass());

	private String gStrPostId = null; /* STRING POST ID */

	private String gStrUserId = null; /* STRING USER ID */

	//private String gStrLocale = null; /* STRING LOCALE */

//	private Locale gLclLocale = null; /* LOCALE */

	private Long gLngLangId = null; /* LANG ID */

	private Long gLngDBId = null; /* DB ID */

	private Date gDtCurDate = null; /* CURRENT DATE */

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	private HttpSession session = null; /* SESSION */

	private static String userName = null;
	private ResourceBundle integrationBundleConst = ResourceBundle
	.getBundle("resources/common/IFMSIntegration");
	
	public static String getUserName() {
		return userName;
	}


	public static void setUserName(String userName) {
		TestService.userName = userName;
	}


	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for Current Date */
	Date gDtCurrDt = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;

	/* Global Variable for User Loc Map */
	static HashMap sMapUserLoc = new HashMap();

	/* Global Variable for User Location */
	String gStrUserLocation = null;

	/* Resource bundle for the constants */
	private ResourceBundle gObjRsrcBndle = ResourceBundle
	.getBundle("resources/dcps/DCPSConstants");

	private void setSessionInfo(Map inputMap) {
		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			session = request.getSession();
			serv = (ServiceLocator) inputMap.get("serviceLocator");
		//	gLclLocale = new Locale(SessionHelper.getLocale(request));
		//	gStrLocale = SessionHelper.getLocale(request);
			gLngLangId = SessionHelper.getLangId(inputMap);
			gLngPostId = SessionHelper.getPostId(inputMap);
			gStrPostId = gLngPostId.toString();
			gLngUserId = SessionHelper.getUserId(inputMap);
			gStrUserId = gLngUserId.toString();
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
			gLngDBId = SessionHelper.getDbId(inputMap);
			gDtCurDate = SessionHelper.getCurDate();
			gDtCurrDt = SessionHelper.getCurDate();
		} catch (Exception e) {

		}

	}
	
	
	public ResultObject getDdoOfficeName(Map<String, Object> inputMap) throws Exception{
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletResponse response = (HttpServletResponse) inputMap.get("responseObj");
		OutputStream lOutStream = null;  
		try{
			setSessionInfo(inputMap);
			TestDaoImpl testDao = new TestDaoImpl(null, serv.getSessionFactory());
			String ddoCode = "";
			testDao.updateEmp("1211");
			
			TestService.setUserName(gStrUserId);
			session.setAttribute("userName", gStrUserId);
			gLogger.info("userName "+session.getAttribute("userName")+""+integrationBundleConst.getString("Status00"));
			/*if(StringUtility.getParameter("ddoCode", request) != null && StringUtility.getParameter("ddoCode", request) != "")
				{*/	
				/*ddoCode = StringUtility.getParameter("ddoCode", request);
				String officeName = testDao.getTargetDDo(ddoCode);	
				gLogger.info("officeName "+officeName);
				if(officeName == null || officeName == "")
					officeName = "No office found";
				StringBuilder lStrBldXML = new StringBuilder();
				lStrBldXML.append("<XMLDOC>");
				lStrBldXML.append("<officeName>");
				//lStrBldXML.append("<postName>");
				lStrBldXML.append("<![CDATA[");
				lStrBldXML.append(officeName);
				lStrBldXML.append("]]>");						
				lStrBldXML.append("</officeName>");
				lStrBldXML.append("</XMLDOC>");
				//String lStrTempResult = null;				
				gLogger.info("ifscCode "+officeName);
				String lStrTempResult = new AjaxXmlBuilder().addItem("ajax_Key",lStrBldXML.toString()).toString();
				inputMap.put("ajaxKey", lStrTempResult);
				gLogger.info("ifscCode .."+lStrTempResult);*/
				
				/*StringBuilder lStrBldXML = new StringBuilder();
				lStrBldXML.append("<XMLDOC>");
				lStrBldXML.append("<officeName>");
				//lStrBldXML.append("<postName>");
				lStrBldXML.append("<![CDATA[");
				
				lStrBldXML.append("]]>");						
				lStrBldXML.append("</officeName>");
				lStrBldXML.append("</XMLDOC>");
				
				String lStrFileName = "Test";
				String fileName = "";
				if (lStrFileName != null && lStrFileName.length() > 0) {
					fileName = lStrFileName + ".txt";
				}
				int i = 6/0;*/
				/*response.setContentType("text/plain;charset=UTF-8");
				request.setAttribute("ExportedReportBytesArray", "werewrw".toString().getBytes("UTF-8"));
				response.addHeader("Content-disposition", "attachment; filename=" + fileName);
				response.setCharacterEncoding("UTF-8");*/
				//lOutStream = response.getOutputStream(); */ 
				/* try
				    {
				       // lLogger.info("In ExportReport.jsp"+request.getAttribute( "ExportedReportBytesArray" ));
						//lLogger.info(response.toString());
					 //	PrintWriter write = response.getWriter();
					 	//write.print("<script>hideProgressbar();</script>");
				       // OutputStream lOutStream = response.getOutputStream();      
				       // byte[] allBytesInBlob = (byte[])request.getAttribute( "ExportedReportBytesArray" ); 
				       // lLogger.info("In ExportReport.jsp");
				       // byte[] allBytesInBlob  = {2,34,45};
				         //   lOutStream.write(allBytesInBlob);// (byte[])request.getAttribute( "ExportedReportBytesArray" ) );
				         //   lOutStream.flush();
				        }
				        catch( IOException ex )
				        {
				        	ex.printStackTrace();
				           // lLogger.error( ex.getMessage(), ex );
				        }
				resObj.setViewName("ExportReportPage");
*/	
				//resObj.setResultValue(inputMap);
				
				//byte[] bytes = DatatypeConverter.parseHexBinary(str);
				/* byte[] allBytesInBlob = "qwerer".getBytes(); 
				lOutStream.write(allBytesInBlob);// (byte[])request.getAttribute( "ExportedReportBytesArray" ) );
	            lOutStream.flush();	       */     
				//resObj.setViewName("ExportReportPage");
				//resObj.setViewName("ajaxData");
				//return resObj;
			//}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		finally{
			//lOutStream.close();
		}
		resObj.setResultValue(inputMap);
		resObj.setViewName("testService");
		return resObj;
	}
	
	
}
