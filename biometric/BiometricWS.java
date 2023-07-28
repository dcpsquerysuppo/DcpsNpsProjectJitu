package biometric;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;

import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.helper.BaseControllerServiceExecuter;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;

public class BiometricWS {
	private Logger logger = Logger.getLogger(BiometricWS.class);

	public String executeProcess(String xmlString) {
		String result = "";
		ResourceBundle.getBundle("resources/Constants");
		try {
			// File directory = new
			// File(constantBundle.getString("FILE_STORAGE_PATH_MAHAIFMS"));
			//
			// if (directory.exists()) {
			// if (directory.isDirectory()) {
			// if (directory.list().length != 0) {
			// String files[] = directory.list();
			// for (String temp : files) {
			// File fileDelete = new File(directory, temp);
			// fileDelete.delete();
			// logger.info("File is deleted : " + fileDelete.getAbsolutePath());
			// }
			// }
			// }
			// }

			ServiceLocator servLoc = ServiceLocator.getServiceLocator();

			System.out.println("I am here " + servLoc.getDataSource());
			System.out.println("ServLoc::" + servLoc);
			if (servLoc == null) {
				System.out.println("Locator not found");
			}

			Map ObjectArgs = new HashedMap();

			XMLConverter xmlConverterObj = new XMLConverter();
			System.out.println("The XML is : " + xmlString);
			logger.info("The XML is : " + xmlString);

			ObjectArgs = (HashMap) xmlConverterObj.XMLToObject(xmlString);
			ObjectArgs.put("serviceLocator", servLoc);
			ObjectArgs.put("xmlString", xmlString);
			// ObjectArgs.get("sevaarthId");

			ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);

			BaseControllerServiceExecuter baseController = servLoc.getServiceExecuter();

			System.out.println("baseController" + baseController);

			ObjectArgs.put("serviceNameForThr", "getEmpDataForBiometrics");

			resultObject = baseController.offlineServiceExecuter(ObjectArgs);
			System.out.println("resultObject==" + resultObject);

			Map resultMap = null;
			resultMap = (Map) resultObject.getResultValue();
			if (resultMap != null) {
				logger.info("resultMap:::::" + resultMap);
				resultMap.remove("serviceLocator");
				resultMap.remove("xmlString");
				resultMap.remove("serviceNameForThr");
			}

			result = xmlConverterObj.objectToXML(resultMap);
			logger.info("End===" + result);
			System.out.println("End===" + result);

		} catch (Exception e) {
			logger.info("Exception While Calling the Remote Method >>" + e.toString(), e);
			result = "failure";
		}

		return result;
	}

}