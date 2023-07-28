/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 30, 2011		Meeta Thacker								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.rowset.serial.SerialBlob;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lowagie.text.pdf.codec.Base64;
import com.lowagie.text.pdf.codec.Base64.InputStream;
import com.mysql.jdbc.Blob;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.DARateDAO;
import com.tcs.sgv.dcps.dao.DARateDAOImpl;
import com.tcs.sgv.dcps.valueobject.CityMaster;
import com.tcs.sgv.dcps.valueobject.DARate;

import java.awt.image.BufferedImage; 
import java.io.ByteArrayOutputStream; 
import java.io.File; 
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import javax.imageio.ImageIO;



/**
 * Class Description -
 * 
 * 
 * @author Meeta Thacker
 * @version 0.1
 * @since JDK 5.0 Apr 30, 2011
 */
public class CityMasterServiceImpl extends ServiceImpl implements DARateService {
		
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
	private ResourceBundle gObjRsrcBndle = ResourceBundle
			.getBundle("resources/dcps/DCPSConstants");
	
	private void setSessionInfo(Map inputMap) {

		try {
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
			
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			e.printStackTrace();
		}

	}
		
	/* To save City Master Date*/
	public ResultObject saveDARateEntry(Map<String, Object> inputMap)
			throws Exception {
		setSessionInfo(inputMap);
		Log logger = LogFactory.getLog(getClass());
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String strToDate = StringUtility.getParameter("txtCity",request);
		Boolean lBFlag = false;
		//gLogger.error("here saveDARateEntry *****");
		if(!strToDate.equals(null))////if(!strToDate.equalsIgnoreCase(null) )
		{
			CityMaster objCity=new CityMaster();
			DARateDAO daRateDAO = new DARateDAOImpl(CityMaster.class, serv.getSessionFactory());//?????
			String CityAddress = StringUtility.getParameter("txtAddress",request);
			String CityEmail = StringUtility.getParameter("txtEmail",request);
			//String ImagePath = StringUtility.getParameter("photo",request);
			//File file = new File(StringUtility.getParameter("photo",request));
//			FileInputStream fis =new FileInputStream(file);
////			   byte[] fileContent = new byte[(int) file.length()];
//			   byte[] fileContent = Files.readAllBytes(file.toPath());
			objCity.setcName(strToDate);
			objCity.setcAddress(CityAddress);
			objCity.setcEmailId(CityEmail);
			//objCity.setImg(new SerialBlob(fileContent));
//			objCity.setcId(2L);
			System.out.println("TEst::"+objCity);
			daRateDAO.create(objCity);//set method
			lBFlag = true;
		}
		String lSBStatus = getResponseXMLDoc(lBFlag).toString();//<XMLDOC><Flag>true</Flag></XMLDOC>
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();//?
		System.out.println("lStrResult::"+lStrResult);//<name>ajax_key</name><value><XMLDOC><Flag>true</Flag></XMLDOC></value>
		inputMap.put("ajaxKey", lStrResult);//where we get("ajaxKey")
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");//??????ajaxData
		//gLogger.error("here after saveDARateEntry *****");
		return resObj;//? where we called this saveDARateEntry() & resObj return 
	}

	public ResultObject loadCityMasterEntry(Map<String, Object> inputMap)
			throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		//gLogger.error("here serviceimpl *****");
		try {
			DARateDAO lObjcmnDCPSDARateDAO = new DARateDAOImpl(null, serv.getSessionFactory());
			
			List lLstPayCommission = IFMSCommonServiceImpl.getLookupValues("PayCommissionDCPS", SessionHelper.getLangId(inputMap),inputMap);
			inputMap.put("lLstPayCommission", lLstPayCommission);

			List IlstCity = lObjcmnDCPSDARateDAO.getAllCityList();
			System.out.println("City List Is-->"+IlstCity.size());
			inputMap.put("ViewCityList", IlstCity);
			 //byte[] fileContent ;
			if(!IlstCity.isEmpty() && IlstCity.size() > 0){
				Object []lObj = (Object[]) IlstCity.get(0);
				System.out.println("-->"+lObj[0]);
				System.out.println("-->"+lObj[1]);
				System.out.println("-->"+lObj[2]);
				System.out.println("-->"+lObj[3]);
			}
//				 byte[] imgData = (byte[]) (lObj[3]); 
//				SerialBlob b= new SerialBlob(lObj[3]);//2 means 2nd column data  
//				byte barr[]=b.getBytes(1,(int)b.length());
//				System.out.println("---"+barr);
				
//				 byte[] imgData = (byte[]) (lObj[3]); working
//				 String encode = Base64.getEncoder().encodeToString(imgData);
//				 byte[] encode1=com.sun.jersey.core.util.Base64.encode(imgData);
//				 String encode11=Base64.encodeBytes(imgData);
//					inputMap.put("imgBase",b.getBytes(1,(int)b.length()));
//					inputMap.put("imgDan",b);
			
			
//			 byte[] imgData = rs.getBytes("img"); // blob field 
//	            String encode = Base64.getEncoder().encodeToString(imgData);
//				inputMap.put("imgBase", encode);

			
			
//			Connection con = new DBConnection().getConnection();
//	        String sql = " SELECT * FROM tea ";
//	        PreparedStatement ps = con.prepareStatement(sql);
//	        ResultSet rs = ps.executeQuery();
//
//	        while (rs.next()) {
//	            byte[] imgData = rs.getBytes("img"); // blob field 
//	            request.setAttribute("rvi", "Ravinath");
//	            rs.getString("teatitle");
//
//	            String encode = Base64.getEncoder().encodeToString(imgData);
//	            request.setAttribute("imgBase", encode);
//	        }
			
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			e.printStackTrace();
		}

		resObj.setResultValue(inputMap);
		resObj.setViewName("Test");
		return resObj;
	}

	private StringBuilder getResponseXMLDoc(boolean flag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("</XMLDOC>");
System.out.println("lStrBldXML : "+lStrBldXML);
		return lStrBldXML;
	}
	
	@Override
	public ResultObject loadDARateEntry(Map<String, Object> inputMap)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
//	private StringBuilder getResponseXMLDoc(boolean flag) {
//
//		StringBuilder lStrBldXML = new StringBuilder();
//
//		lStrBldXML.append("<XMLDOC>");
//		lStrBldXML.append("<Flag>");
//		lStrBldXML.append(flag);
//		lStrBldXML.append("</Flag>");
//		lStrBldXML.append("</XMLDOC>");
//
//		return lStrBldXML;
//	}
}
