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

public class TDSWebServiceUpdateUserMobileEmail 
{


	private final static Logger gLogger = Logger.getLogger(TDSWebService.class);
	/*
	 1.validate employee login
	 2.update email and mobile no
	 3.employee profile details 
	 */
	public String getUserInfo(String lStrXML) throws ParserConfigurationException, SAXException, IOException
	{
		gLogger.info("TDS get user info xml in getUserInfo is "+lStrXML);

		ServiceLocator servLoc = ServiceLocator.getServiceLocator();
		Map ObjectArgs = new HashedMap();
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		BaseControllerServiceExecuter baseController = servLoc.getServiceExecuter();
		//START parsing xml to get sevarth id
		String sevarthId="";
		
		if(lStrXML != null && lStrXML != "")
		{
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(lStrXML));

			Document doc = db.parse(is);
			NodeList nodes = doc.getElementsByTagName("emp");

			Element element = (Element) nodes.item(0);

			NodeList name = element.getElementsByTagName("sevarthId");
			Element line = (Element) name.item(0);
			gLogger.info("sevarthId: " + getCharacterDataFromElement(line));
			sevarthId = getCharacterDataFromElement(line);

			
		}
		//END parsing xml to get sevarth id
		ObjectArgs.put("sevarthId", sevarthId.trim());
		ObjectArgs.put("serviceLocator", servLoc);
		gLogger.info("calling method to get user info");
		try 
		{
			resultObject = servLoc.executeService("getUserInfoForTDS", ObjectArgs);
		} 
		catch (ServiceException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TDSEmpInfoValueObject tdsEmpInfo = new TDSEmpInfoValueObject();
		
		tdsEmpInfo=(TDSEmpInfoValueObject) ObjectArgs.get("tdsEmpInfo");
		//XStream xstream = new XStream();
		//xstream.alias("empInfo", TDSEmpInfoValueObject.class);
		//gLogger.info("xstream.toXML()"+xstream.toXML(tdsEmpInfo));
		//return("<?xml version='1.0' encoding='UTF-8' ?>\n"+xstream.toXML(tdsEmpInfo).toString());
		
		//json
		XStream xstream = new XStream(new JettisonMappedXmlDriver());
        xstream.setMode(XStream.NO_REFERENCES);
        xstream.alias("empInfo", TDSEmpInfoValueObject.class);
        gLogger.info("xstream.toXML()"+xstream.toXML(tdsEmpInfo));
        return (xstream.toXML(tdsEmpInfo));
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
