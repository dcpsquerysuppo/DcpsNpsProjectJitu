package com.tcs.sgv.dcps._TDSWebService;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.exception.ServiceException;
import com.tcs.sgv.core.helper.BaseControllerServiceExecuter;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

public class TDSWebServiceUserInfoUpdate 
{

	private final static Logger gLogger = Logger.getLogger(TDSWebServiceUserInfoUpdate.class);
	/*
	 1.validate employee login
	 2.update email and mobile no
	 3.employee profile details 
	 */
	public String updateUserInfo(String lStrXML) throws ParserConfigurationException, SAXException, IOException
	{
		gLogger.info("TDS update user info xml in updateUserInfo is "+lStrXML);

		ServiceLocator servLoc = ServiceLocator.getServiceLocator();
		Map ObjectArgs = new HashedMap();
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		BaseControllerServiceExecuter baseController = servLoc.getServiceExecuter();
		//START parsing xml to get sevarth id
		String userName="";
		String userType="";
		String mobileNo="";
		String emailId="";
		
		if(lStrXML != null && lStrXML != "")
		{
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(lStrXML));

			Document doc = db.parse(is);
			NodeList nodes = doc.getElementsByTagName("emp");

			Element element = (Element) nodes.item(0);

			NodeList NLSevarthId = element.getElementsByTagName("userName");
			Element line = (Element) NLSevarthId.item(0);
			gLogger.info("userName: " + getCharacterDataFromElement(line));
			userName = getCharacterDataFromElement(line);
			
			NodeList NLUserType = element.getElementsByTagName("userType");
			line = (Element) NLUserType.item(0);
			gLogger.info("userType: " + getCharacterDataFromElement(line));
			userType = getCharacterDataFromElement(line);
			
			NodeList NLMobileNo = element.getElementsByTagName("mobileNo");
			line = (Element) NLMobileNo.item(0);
			gLogger.info("mobileNo: " + getCharacterDataFromElement(line));
			mobileNo = getCharacterDataFromElement(line);
			
			NodeList NLEmailId = element.getElementsByTagName("emailId");
			line = (Element) NLEmailId.item(0);
			gLogger.info("emailId: " + getCharacterDataFromElement(line));
			emailId = getCharacterDataFromElement(line);

			
		}
		//END parsing xml to get sevarth id
		ObjectArgs.put("userName", userName.trim());
		ObjectArgs.put("userType", userType.trim());
		ObjectArgs.put("mobileNo", mobileNo.trim());
		ObjectArgs.put("emailId", emailId.trim());
		ObjectArgs.put("serviceLocator", servLoc);
		gLogger.info("calling method to get user info");
		try 
		{
			resultObject = servLoc.executeService("updateUserInfoForTDS", ObjectArgs);
		} 
		catch (ServiceException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TDSEmpInfoUpdateValueObject tdsEmpInfoUpdate = new TDSEmpInfoUpdateValueObject();
		
		tdsEmpInfoUpdate=(TDSEmpInfoUpdateValueObject) ObjectArgs.get("tdsEmpInfoUpdate");
		//XStream xstream = new XStream();
		//xstream.alias("empInfo", TDSEmpInfoValueObject.class);
		//gLogger.info("xstream.toXML()"+xstream.toXML(tdsEmpInfo));
		//return("<?xml version='1.0' encoding='UTF-8' ?>\n"+xstream.toXML(tdsEmpInfo).toString());
		
		//json
		XStream xstream = new XStream(new JettisonMappedXmlDriver());
        xstream.setMode(XStream.NO_REFERENCES);
        xstream.alias("empInfo", TDSEmpInfoUpdateValueObject.class);
        gLogger.info("xstream.toXML()"+xstream.toXML(tdsEmpInfoUpdate));
        return (xstream.toXML(tdsEmpInfoUpdate));
	}

	public static String getCharacterDataFromElement(Element e) 
	{
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) 
		{
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "";
	}

}
