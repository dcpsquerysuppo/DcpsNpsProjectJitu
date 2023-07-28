package com.tcs.sgv.dcps.TestWebService;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
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
import com.tcs.sgv.dcps._TDSWebService.TDSWebService;
import com.thoughtworks.xstream.XStream;

public class EmpDetailsWs {
	private final static Logger gLogger = Logger.getLogger(TDSWebService.class);
	public String getEmpDetails(String lStrXML) throws ParserConfigurationException, SAXException, IOException 
	{
		gLogger.info("billportal xml in getBillPoratlData is "+lStrXML);
String empDetails = "";
		ServiceLocator servLoc = ServiceLocator.getServiceLocator();
		Map ObjectArgs = new HashedMap();
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		BaseControllerServiceExecuter baseController = servLoc.getServiceExecuter();
		String sevaarthId = "";
		
			if(lStrXML != null && lStrXML != "")
			{
				DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				InputSource is = new InputSource();
				is.setCharacterStream(new StringReader(lStrXML));

				Document doc = db.parse(is);
				NodeList nodes = doc.getElementsByTagName("emp");

				Element element = (Element) nodes.item(0);

				NodeList NLUserName= element.getElementsByTagName("sevarthId");
				Element line = (Element) NLUserName.item(0);
				gLogger.info("userName: " + getCharacterDataFromElement(line));
				sevaarthId = getCharacterDataFromElement(line);
			}
				ObjectArgs.put("sevaarthId", sevaarthId);
				ObjectArgs.put("serviceLocator", servLoc);
				try {
					resultObject = servLoc.executeService("empDetailsWSTest", ObjectArgs);
					
				} catch (ServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				EmpData objEmpdetails =new EmpData();
				objEmpdetails = (EmpData) ObjectArgs.get("emp");
				XStream xstream = new XStream();
				xstream.alias("empInfo", EmpData.class);
				gLogger.info("xstream.toXML()"+xstream.toXML(objEmpdetails));
				return("<?xml version='1.0' encoding='UTF-8' ?>\n"+xstream.toXML(objEmpdetails).toString());
				
				
				
			
		
		//return empDetails;

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
	
	public ResultObject empDetailsWSTest(Map inputMap)
	throws Exception {

ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

String sevarthId = inputMap.get("sevaarthId").toString();

EmpDetailsWsDAOImpl dao = new EmpDetailsWsDAOImpl(null, null);
List empData = dao.getEmpDetails(sevarthId);
Object obj[] =null;
String empName ="";
String dob="";
String doj="";
if(empData!=null){
	obj = (Object[]) empData.get(0); 
	empName= obj[0].toString();
	dob=obj[1].toString();
	doj=obj[2].toString();
}
EmpData emp = new EmpData();
emp.setEmpName(empName);
emp.setDob(dob);
emp.setDoj(doj);
inputMap.put("emp", emp);
return resObj;
}
	
}
	
