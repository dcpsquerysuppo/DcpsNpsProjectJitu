package biometric;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class XMLConverter {

	public String objectToXML(Object voObj) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		XMLEncoder xmlEncoder = null;
		try {
			xmlEncoder = new XMLEncoder(new BufferedOutputStream(stream));
			xmlEncoder.writeObject(voObj);
			xmlEncoder.close();
			return stream.toString("UTF-8");
		} catch (Exception e) {
			// logger.error("Inside exception from pymtHistToXML : " +
			// e.getMessage());
			System.out.println("Exception while converting VO to XML : " + e.getMessage());
		}
		return null;
	}

	public Object XMLToObject(String dataXML) {
		XMLDecoder d = null;
		try {
			d = new XMLDecoder(new ByteArrayInputStream(dataXML.getBytes("UTF-8")));
			Object voObj = d.readObject();
			d.close();
			return voObj;
		} catch (Exception e) {
			System.out.println("Error while Converting XML to VO : " + e);
		}
		return null;
	}

}