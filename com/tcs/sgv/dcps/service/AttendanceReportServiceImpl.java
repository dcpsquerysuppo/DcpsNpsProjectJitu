

package com.tcs.sgv.dcps.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.AttendanceReportDAO;
import com.tcs.sgv.dcps.dao.AttendanceReportDAOImpl;
import com.tcs.sgv.dcps.service.attendanceReportHelper.AttendanceReportDOMParserVO;
import com.tcs.sgv.dcps.service.attendanceReportHelper.BasDataShareReq;
import com.tcs.sgv.dcps.service.attendanceReportHelper.BasDataShareResp;
import com.tcs.sgv.dcps.service.attendanceReportHelper.HttpClientHelper;
public class AttendanceReportServiceImpl extends ServiceImpl{




	/* Global Variable for Logger Class */
	private final Log gLogger = LogFactory.getLog(getClass());

	private String gStrPostId = null; /* STRING POST ID */

	private String gStrUserId = null; /* STRING USER ID */

	private String gStrLocale = null; /* STRING LOCALE */

	private Locale gLclLocale = null; /* LOCALE */

	private Long gLngLangId = null; /* LANG ID */

	private Long gLngDBId = null; /* DB ID */

	private Date gDtCurDate = null; /* CURRENT DATE */

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	private HttpSession session = null; /* SESSION */

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
	//private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/eis/zp/zpDDOOffice/DCPSConstantsZP");

	/*
	 * Function to save the session specific details
	 */
	private void setSessionInfo(Map inputMap)
	{

		try
		{
			request = (HttpServletRequest) inputMap.get("requestObj");
			session = request.getSession();
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gLclLocale = new Locale(SessionHelper.getLocale(request));
			gStrLocale = SessionHelper.getLocale(request);
			gLngLangId = SessionHelper.getLangId(inputMap);
			gLngPostId = SessionHelper.getPostId(inputMap);
			gStrPostId = gLngPostId.toString();
			gLngUserId = SessionHelper.getUserId(inputMap);
			gStrUserId = gLngUserId.toString();
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
			gLngDBId = SessionHelper.getDbId(inputMap);
			gDtCurDate = SessionHelper.getCurDate();
			gDtCurrDt = SessionHelper.getCurDate();
		}
		catch (Exception e)
		{

		}
	}

	@SuppressWarnings("unchecked")
	public ResultObject getAttendanceReport(Map inputMap) throws Exception
	{
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List lstDept=null;
		String flag=null;
		try
		{
			setSessionInfo(inputMap);
			//AttendanceReportDAO lObjAttendanceReportDAO = new AttendanceReportDAOImpl(null, serv.getSessionFactory());
			//lstDept = lObjAttendanceReportDAO.getDeptList();
			//gLogger.info("dept list values: "+Arrays.toString(lstDept.toArray()));
			//inputMap.put("lstDept", lstDept);
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			Date date = new Date();
			gLogger.info("Current date: "+dateFormat.format(date)); 
			
			inputMap.put("curretDate", dateFormat.format(date).toString());
			resObj.setResultValue(inputMap);
			resObj.setViewName("attendanceReport");
		}
		catch (Exception e)
		{
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;

	}


	public ResultObject getEmpAttendanceReport(Map inputMap) throws Exception
	{
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		gLogger.info("inside getEmpAttendanceReport");
		//List lstDept=null;
		String cardId="";
		String sevarthId="";
		String frmDate="";
		String toDate="";
		try
		{
			setSessionInfo(inputMap);
			//AttendanceReportDAO lObjAttendanceReportDAO = new AttendanceReportDAOImpl(null, serv.getSessionFactory());

			cardId= StringUtility.getParameter("cardId", request);
			sevarthId= StringUtility.getParameter("sevarthId", request);
			frmDate= StringUtility.getParameter("frmDate", request);
			toDate= StringUtility.getParameter("toDate", request);

			gLogger.info("cardId: "+ cardId +" sevarthId: "+ sevarthId +" frmDate: "+ frmDate +" toDate: "+ toDate);
			AttendanceReportServiceImpl attend = new AttendanceReportServiceImpl();
			frmDate=attend.getFormattedDate(frmDate);
			toDate=attend.getFormattedDate(toDate);
			//START code for records list
			StringWriter smsReqxml = new StringWriter();
			BasDataShareReq basDataShareReq =new BasDataShareReq();
			BasDataShareResp appRes = new  BasDataShareResp();

			basDataShareReq.setCardId(cardId);
			basDataShareReq.setSevarthId(sevarthId);
			basDataShareReq.setFromDate(frmDate);
			basDataShareReq.setToDate(toDate);
			basDataShareReq.setUserId("sevarth");
			basDataShareReq.setPassWord("sevarth@123");


			String ServerURL = "http://103.23.150.116:8080/BAS-SEVA/rest/getEmpAttendance";
			//START added by samadhan
			System.getProperties().put("http.proxyHost", "proxy.tcs.com");
			System.getProperties().put("http.proxyPort", "8080"); 
			System.getProperties().put("http.proxyUser", "665570");
			System.getProperties().put("http.proxyPassword", "Tata@1114");
			//END
			JAXBContext.newInstance(BasDataShareReq.class).createMarshaller().marshal(basDataShareReq, smsReqxml);
			gLogger.info("Auth request XML :: --> " + smsReqxml.toString());
			URI serverURI = new URI(ServerURL.toString());
			gLogger.info("ServerURL :: --> " + ServerURL.toString());
			Client client = Client.create(HttpClientHelper.getClientConfig(serverURI.getScheme()));
			WebResource webResource = client.resource(ServerURL);
			appRes = webResource.header("REMOTE_ADDR", InetAddress.getLocalHost().getHostAddress()).post(BasDataShareResp.class, basDataShareReq);
			StringWriter resXML = new StringWriter();
			JAXBContext.newInstance(BasDataShareResp.class).createMarshaller().marshal(appRes, resXML);
			gLogger.info("--Auth Response " + resXML);	
			webResource = null;
			client.destroy();
			client = null;
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			InputStream inputStream = new ByteArrayInputStream(resXML.toString().getBytes());
			Document doc = dBuilder.parse(inputStream);
			doc.getDocumentElement().normalize();
			gLogger.info("Root element :" + doc.getDocumentElement().getNodeName());
			
			
			
			//check for successfull result
			String successFlag="";
			NodeList nListSuccess = doc.getElementsByTagName("absenceDataResp");
			List resultListSuccess=new ArrayList();
			for (int temp = 0; temp < nListSuccess.getLength(); temp++) 
			{


				Node nNode = nListSuccess.item(temp);

				System.out.println("\nCurrent Element :" + nNode.getNodeName());

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

					gLogger.info("success message : " + eElement.getElementsByTagName("message").item(0).getTextContent());
					successFlag=eElement.getElementsByTagName("message").item(0).getTextContent().toString();
				}
			
			}
			//success check end
			
			List lstEmpAttendanceRecords=new ArrayList();//attendance records
			List lstEmpAttendancePersonalRecords=new ArrayList();//personal records
			if(!successFlag.equals("No Data Found"))
			{
				NodeList nList = doc.getElementsByTagName("dataEmpAttendanceList");
				
				for (int temp = 0; temp < nList.getLength(); temp++) 
				{

					Node nNode = nList.item(temp);

					gLogger.info("\nCurrent Element :" + nNode.getNodeName());

					if (nNode.getNodeType() == Node.ELEMENT_NODE) {

						Element eElement = (Element) nNode;

						/*gLogger.info("date : " + eElement.getElementsByTagName("date").item(0).getTextContent());
						gLogger.info("inTime : " + eElement.getElementsByTagName("inTime").item(0).getTextContent());
						gLogger.info("outTime : " + eElement.getElementsByTagName("outTime").item(0).getTextContent());
						gLogger.info("status : " + eElement.getElementsByTagName("status").item(0).getTextContent());
						gLogger.info("workHours : " + eElement.getElementsByTagName("workHours").item(0).getTextContent());*/

						/*resultList.add(eElement.getElementsByTagName("date").item(0).getTextContent().toString()+
								"##"+eElement.getElementsByTagName("inTime").item(0).getTextContent().toString()+
								"##"+eElement.getElementsByTagName("outTime").item(0).getTextContent().toString()+
								"##"+eElement.getElementsByTagName("status").item(0).getTextContent().toString()+
								"##"+eElement.getElementsByTagName("workHours").item(0).getTextContent().toString());*/
						AttendanceReportDOMParserVO attendanceDomVO=new AttendanceReportDOMParserVO();
						attendanceDomVO.setDate(attend.getFormattedDatesForDisplay(eElement.getElementsByTagName("date").item(0).getTextContent().toString()).toString() );
						attendanceDomVO.setInTime(eElement.getElementsByTagName("inTime").item(0).getTextContent().toString());
						attendanceDomVO.setOutTime(eElement.getElementsByTagName("outTime").item(0).getTextContent().toString());
						attendanceDomVO.setStatus(eElement.getElementsByTagName("status").item(0).getTextContent().toString());
						attendanceDomVO.setWorkHrs(eElement.getElementsByTagName("workHours").item(0).getTextContent().toString());
						lstEmpAttendanceRecords.add(attendanceDomVO);
					}
				}
				gLogger.info(Arrays.toString(lstEmpAttendanceRecords.toArray()));
				//get all values of time 
				for (Object temp : lstEmpAttendanceRecords) {
					AttendanceReportDOMParserVO tmp=(AttendanceReportDOMParserVO) temp;
					gLogger.info(tmp.getDate()+" "+tmp.getInTime()+" "+tmp.getOutTime()+" "+tmp.getStatus()+" "+tmp.getWorkHrs());
				}
				
				
				NodeList nList1 = doc.getElementsByTagName("absenceDataResp");
				
				for (int temp = 0; temp < nList1.getLength(); temp++) 
				{

					Node nNode = nList1.item(temp);

					gLogger.info("\nCurrent Element :" + nNode.getNodeName());

					if (nNode.getNodeType() == Node.ELEMENT_NODE) {

						Element eElement = (Element) nNode;

						gLogger.info("cardId : " + eElement.getElementsByTagName("cardId").item(0).getTextContent());
						gLogger.info("deptName : " + eElement.getElementsByTagName("deptName").item(0).getTextContent());
						gLogger.info("desigName : " + eElement.getElementsByTagName("desigName").item(0).getTextContent());
						gLogger.info("fullName : " + eElement.getElementsByTagName("fullName").item(0).getTextContent());
						gLogger.info("message : " + eElement.getElementsByTagName("message").item(0).getTextContent());
						gLogger.info("mobileNo : " + eElement.getElementsByTagName("mobileNo").item(0).getTextContent());
						gLogger.info("sevarthId : " + eElement.getElementsByTagName("sevarthId").item(0).getTextContent());
						gLogger.info("uid : " + eElement.getElementsByTagName("uid").item(0).getTextContent());
						gLogger.info("no. of records: "+lstEmpAttendanceRecords.size());

						lstEmpAttendancePersonalRecords.add(eElement.getElementsByTagName("cardId").item(0).getTextContent().toString()+
								"##"+eElement.getElementsByTagName("deptName").item(0).getTextContent().toString()+
								"##"+eElement.getElementsByTagName("desigName").item(0).getTextContent().toString()+
								"##"+eElement.getElementsByTagName("fullName").item(0).getTextContent().toString()+
								"##"+eElement.getElementsByTagName("message").item(0).getTextContent().toString()+
								"##"+eElement.getElementsByTagName("mobileNo").item(0).getTextContent().toString()+
								"##"+eElement.getElementsByTagName("sevarthId").item(0).getTextContent().toString()+
								"##"+eElement.getElementsByTagName("uid").item(0).getTextContent().toString()+
								"##"+lstEmpAttendanceRecords.size());
						
					}
				}
				gLogger.info("personal records: "+Arrays.toString(lstEmpAttendancePersonalRecords.toArray()));
				
				
				
			}
			else
			{
				lstEmpAttendanceRecords=null;
				lstEmpAttendancePersonalRecords=null;
			}
			
			
			
			
			
			
			
			
			
			
			
			//END code for records list
			
			
			String flag="empWiseRecords";//or deptWiseRecords
			//lstDept = lObjAttendanceReportDAO.getDeptList();
			//gLogger.info("dept list values: "+Arrays.toString(lstDept.toArray()));
			//inputMap.put("lstDept", lstDept);
			
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			Date date = new Date();
			gLogger.info("Current date: "+dateFormat.format(date)); 
			
			inputMap.put("currentDate", dateFormat.format(date).toString());
			inputMap.put("lstEmpAttendanceRecords", lstEmpAttendanceRecords);
			inputMap.put("lstEmpAttendancePersonalRecords", lstEmpAttendancePersonalRecords);
			inputMap.put("flag", flag);
			inputMap.put("cardId", cardId);
			inputMap.put("sevarthId", sevarthId);
			inputMap.put("frmDate", attend.getFormattedDatesForDisplay(frmDate));
			inputMap.put("toDate", attend.getFormattedDatesForDisplay(toDate));
			
			
			
			resObj.setResultValue(inputMap);
			resObj.setViewName("attendanceReport");
		}
		catch (Exception e)
		{
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	private String getFormattedDatesForDisplay(String date) {
		String formattedDate="";
		String tmp[]=date.split("-");
		formattedDate=tmp[2]+"/"+tmp[1]+"/"+tmp[0];
		gLogger.info("formatted to display date: "+formattedDate);
		return formattedDate;
	}

	private String getFormattedDate(String date) {
		String formattedDate="";
		String tmp[]=date.split("/");
		formattedDate=tmp[2]+"-"+tmp[1]+"-"+tmp[0];
		gLogger.info("formatted date: "+formattedDate);
		return formattedDate;
	}

}
