/*
 * Samadhan Jadhav
 * 09 Apr 2015
 */
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
//import com.tcs.sgv.dcps.billPortalWebService.BillPortalDataValueObject;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

public class TDSWebService 
{
	private final static Logger gLogger = Logger.getLogger(TDSWebService.class);
	/*
	 1.validate employee login
	 2.update email and mobile no
	 3.employee profile details 
	 */
	public String authenthicateEmplyee(String lStrXML) throws ParserConfigurationException, SAXException, IOException
	{
		gLogger.info("billportal xml in getBillPoratlData is "+lStrXML);

		ServiceLocator servLoc = ServiceLocator.getServiceLocator();
		Map ObjectArgs = new HashedMap();
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		BaseControllerServiceExecuter baseController = servLoc.getServiceExecuter();
		//START parsing xml to get user name and md5 encrypted password and user type
		String userName="";
		String md5Pwd="";
		String userType="E";//maybe E:employee, D:ddo, T:treasuryOffice
		if(lStrXML != null && lStrXML != "")
		{
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(lStrXML));

			Document doc = db.parse(is);
			NodeList nodes = doc.getElementsByTagName("emp");

			Element element = (Element) nodes.item(0);

			NodeList NLUserName= element.getElementsByTagName("userName");
			Element line = (Element) NLUserName.item(0);
			gLogger.info("userName: " + getCharacterDataFromElement(line));
			userName = getCharacterDataFromElement(line);

			NodeList NLPwd = element.getElementsByTagName("md5Pwd");
			line = (Element) NLPwd.item(0);
			gLogger.info("md5Pwd: " + getCharacterDataFromElement(line));
			md5Pwd = getCharacterDataFromElement(line);
			
			
			NodeList NLUsertYPE = element.getElementsByTagName("userType");
			line = (Element) NLUsertYPE.item(0);
			gLogger.info("userType: " + getCharacterDataFromElement(line));
			userType = getCharacterDataFromElement(line);
		}
		//END parsing xml to get user name and md5 encrypted password and user type
		ObjectArgs.put("userName", userName.trim());
		ObjectArgs.put("md5Pwd", md5Pwd.trim());
		ObjectArgs.put("userType", userType.trim());
		ObjectArgs.put("serviceLocator", servLoc);
		gLogger.info("calling method to authenticate user");
		try 
		{
			resultObject = servLoc.executeService("authenticateEmpForTDS", ObjectArgs);
		} 
		catch (ServiceException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TDSEmpAuthValueObjet tdsEmpAuth = new TDSEmpAuthValueObjet();
		
		tdsEmpAuth=(TDSEmpAuthValueObjet) ObjectArgs.get("tdsEmpAuth");
		//XStream xstream = new XStream();
		//xstream.alias("empInfo", TDSEmpAuthValueObjet.class);
		//gLogger.info("xstream.toXML()"+xstream.toXML(tdsEmpAuth));
		//return("<?xml version='1.0' encoding='UTF-8' ?>\n"+xstream.toXML(tdsEmpAuth).toString());
		XStream xstream = new XStream(new JettisonMappedXmlDriver());
        xstream.setMode(XStream.NO_REFERENCES);
        xstream.alias("empInfo", TDSEmpAuthValueObjet.class);
        gLogger.info("xstream.toXML()"+xstream.toXML(tdsEmpAuth));
        return (xstream.toXML(tdsEmpAuth));
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
