package com.tcs.sgv.dcps.service;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.icu.text.DecimalFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.FormS1DAO;
import com.tcs.sgv.dcps.dao.FormS1DAOImpl;
import com.tcs.sgv.dcps.dao.NsdlNpsDAOImpl;
import com.tcs.sgv.dcps.valueobject.FrmFormS1Dtls;
import com.tcs.sgv.dcps.valueobject.MstEmp;
public class FormS1ServiceImpl extends ServiceImpl {

	private final Log gLogger = LogFactory.getLog(getClass());

	// private String gStrPostId = null; /* STRING POST ID */

	private String gStrUserId = null; /* STRING USER ID */

	private String gStrLocale = null; /* STRING LOCALE */

	// private Locale gLclLocale = null; /* LOCALE */

	private Long gLngLangId = null; /* LANG ID */

	private Long gLngDBId = null; /* DB ID */

	private Date gDtCurDate = null; /* CURRENT DATE */

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	private HttpSession session = null; /* SESSION */

	private HttpServletResponse response = null;

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
			// gLclLocale = new Locale(SessionHelper.getLocale(request));
			gStrLocale = SessionHelper.getLocale(request);
			gLngLangId = SessionHelper.getLangId(inputMap);
			gLngPostId = SessionHelper.getPostId(inputMap);
			// gStrPostId = gLngPostId.toString();
			gLngUserId = SessionHelper.getUserId(inputMap);
			gStrUserId = gLngUserId.toString();
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
			gLngDBId = SessionHelper.getDbId(inputMap);
			gDtCurDate = SessionHelper.getCurDate();
			gDtCurrDt = SessionHelper.getCurDate();
			response = (HttpServletResponse) inputMap.get("responseObj");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ResultObject loadFormS1WithDetails(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

		String sevaarthId = null;
		String empName = null;
		List lSecAFormS1 = null;
		List lSecBFormS1 = null;
		List lSecCFormS1 = null;
		String[] SecCtuple = null;
		String[] SecCtuple1 = null;
		List lNmnCount = null;
		List DDTORegNo = null;
		String office = null;
		String department = null;
		String ministry = null;
		String Off1 = null;
		String Off2 = null;
		String dept1 = null;
		String dept2 = null;
		String min1 = null;
		String min2 = null;
		String emailId = null;
		String bankAdd = null;
		String emailId1 = null;
		String emailId2 = null;
		String bankAdd1 = null;
		String bankAdd2 = null;
		String nomineeName1 = null;
		String nomineeDOB1 = null;
		// $t 2019 20-12
		String nomineeDOB2 = null;
		String nomineeDOB3 = null;
		//
		String nomineeName2 = null;
		String nomineeName3 = null;
		String nomineeRel1 = null;
		String nomineeRel2 = null;
		String nomineeRel3 = null;
		String nomineeGuar1 = null;
		String nomineeGuar2 = null;
		String nomineeGuar3 = null;
		String nmn11 = null;
		String nmn111 = null;
		// $t 2019 20-12
		String nmn112 = null;
		String nmn113 = null;
		//
		String nmn12 = null;
		String nmnRel11 = null;
		String nmnRel12 = null;
		String nmnGuar11 = null;
		String nmnGuar12 = null;
		String nmn21 = null;
		String nmn22 = null;
		String nmnRel21 = null;
		String nmnRel22 = null;
		String nmnGuar21 = null;
		String nmnGuar22 = null;
		String nmn31 = null;
		String nmn32 = null;
		String nmnRel31 = null;
		String nmnRel32 = null;
		String nmnGuar31 = null;
		String nmnGuar32 = null;
		// String ddoCode = "";
		String Pnmn1 = null;
		String Pnmn2 = null;
		String Pnmn3 = null;
		String Pnmnage1 = null, Pnmnage2 = null, Pnmnage3 = null, pranNo = null, preFlatno = null;
		String subFullName = null;
		String IsDeputation = null;

		try {
			setSessionInfo(inputMap);
			FormS1DAO lObjSearchEmployeeDAO = new FormS1DAOImpl(MstEmp.class,
					serv.getSessionFactory());
			IsDeputation = StringUtility.getParameter("IsDeputation", request)
					.toString();
			gLogger.info("IsDeputation**********+++++:" + IsDeputation);
			if (StringUtility.getParameter("txtSevaarthId", request).toString() != null
					|| !StringUtility.getParameter("txtSevaarthId", request)
							.toString().equals(""))
				sevaarthId = StringUtility.getParameter("txtSevaarthId",
						request).toString();
			// for new form S1
			// ddoCode = lObjSearchEmployeeDAO.getDDOCode(gStrLocationCode);
			gLogger.info("sevaarthId**********+++++:" + sevaarthId);
			// gLogger.info("ddoCode:"+ddoCode);
			// end for new form S1

			Connection con = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			PreparedStatement ps1 = null;
			ResultSet rs1 = null;

			if (sevaarthId != null) {
				lObjSearchEmployeeDAO.deleteMultipleRecords(sevaarthId);
				lSecAFormS1 = lObjSearchEmployeeDAO
						.getSectionADetails(sevaarthId);
				lSecBFormS1 = lObjSearchEmployeeDAO.getSectionBDetails(
						sevaarthId, IsDeputation);
				DDTORegNo = lObjSearchEmployeeDAO.getDTORegNo(sevaarthId);
				lSecCFormS1 = lObjSearchEmployeeDAO
						.getSectionCDetails(sevaarthId);
				lNmnCount = lObjSearchEmployeeDAO.checkNmnCount(sevaarthId);

				/* Click on sevaarth Id Photo display */
				try {
					String PhotoId = "", PhotosrNo = "";
					Class.forName("com.ibm.db2.jcc.DB2Driver");
//					 con = DriverManager.getConnection(
//					 "jdbc:db2://100.70.201.168:50000/IFMSMIGR", "ifms",
//					 "Mahait@99");// where
					// con =
					// DriverManager.getConnection("jdbc:db2://10.34.82.226:60000/IFMSMIGR","db2mahait",
					// "Sevaarth@99");// where
					con = DriverManager.getConnection(
							"jdbc:db2://10.34.82.235:50015/IFMSMIGR", "ifms",
							"IFMS@dat#@!");// where

					ps = con.prepareStatement("select emp.PHOTO_ATTACHMENTID,doc.SR_NO,doc.final_attachment from CMN_ATTACHMENT_MPG mpg  join MST_DCPS_EMP emp on emp.SEVARTH_ID = '"
							+ sevaarthId
							+ "' "
							+ " and emp.PHOTO_ATTACHMENTID=mpg.ATTACHMENT_ID join CMN_ATTDOC_MST doc on doc.SR_NO=mpg.SR_NO "
							+ " and mpg.ACTIVATE_FLAG='Y' order by mpg.CREATED_DATE desc FETCH first ROW only ");
					rs = ps.executeQuery();
					if (rs.next()) {
						PhotoId = rs.getString(1);
						PhotosrNo = rs.getString(2);
						Blob b = rs.getBlob(3);
						if (b == null)
							inputMap.put("SFTPflag", false);
						else
							inputMap.put("SFTPflag", true);

						inputMap.put("PhotoId", PhotoId);
						inputMap.put("PhotosrNo", PhotosrNo);
					}
					String SignsrNo = "", SignId = "";
					ps1 = con
							.prepareStatement(" select emp.SIGNATURE_ATTACHMENTID,doc.SR_NO,doc.final_attachment from CMN_ATTACHMENT_MPG mpg  join MST_DCPS_EMP emp on emp.SEVARTH_ID = '"
									+ sevaarthId
									+ "' "
									+ " and emp.SIGNATURE_ATTACHMENTID=mpg.ATTACHMENT_ID join CMN_ATTDOC_MST doc on doc.SR_NO=mpg.SR_NO "
									+ " and mpg.ACTIVATE_FLAG='Y' order by mpg.CREATED_DATE desc FETCH first ROW only ");
					// +
					// "on emp.SEVARTH_ID = 'DATPTSF9201' and emp.SIGNATURE_ATTACHMENTID=mpg.ATTACHMENT_ID  join CMN_ATTDOC_MST doc on doc.SR_NO=mpg.SR_NO ");
					rs1 = ps1.executeQuery();

					if (rs1.next()) {

						SignId = rs1.getString(1);
						SignsrNo = rs1.getString(2);
						Blob b = rs.getBlob(3);
						if (b == null)
							inputMap.put("SFTPflag", false);
						else
							inputMap.put("SFTPflag", true);

						inputMap.put("SignId", SignId);
						inputMap.put("SignsrNo", SignsrNo);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					rs.close();
					ps.close();
					rs1.close();
					ps1.close();
					con.close();
				}
			}
			/* End Click on sevaarth Id Photo display */

			Object[] tuple = null;
			Iterator it = lSecBFormS1.iterator();

			while (it.hasNext()) {
				tuple = (Object[]) it.next();
				office = tuple[5].toString();
				ministry = tuple[6].toString();
				department = tuple[7].toString();
			}
			gLogger.info("Office details" + office);

			int offSize = office.length();
			int deptSize = department.length();
			int minSize = ministry.length();

			if (offSize > 30) {
				Off1 = office.substring(0, 30);
				gLogger.info("#Off1" + Off1);
				Off2 = office.substring(30, offSize);
				gLogger.info("#Off1" + Off2);
			} else {
				Off1 = office.substring(0, offSize);
				Off2 = " ";
				gLogger.info("#Off1" + Off2);
			}

			if (deptSize > 30) {
				dept1 = department.substring(0, 30);
				gLogger.info("#dept1" + dept1);
				dept2 = department.substring(30, deptSize);
				gLogger.info("#dept2" + dept2);
			} else {
				dept1 = department.substring(0, deptSize);
				dept2 = " ";
				gLogger.info("#dept1" + dept1);
			}

			if (minSize > 30) {
				min1 = ministry.substring(0, 30);
				gLogger.info("#min1" + min1);
				min2 = ministry.substring(30, minSize);
				gLogger.info("#min2" + min2);
			} else {
				min1 = ministry.substring(0, minSize);
				min2 = " ";
				gLogger.info("#min1" + min1);
			}

			Object[] tupleA = null;
			Iterator itA = lSecAFormS1.iterator();
			String taxcountry = "";
			String usPerson = "";
			String taxAddress = "";
			String taxCity = "";
			String taxState = "";
			String taxPin = "";
			String tinPan = "";
			String tinCountry = "";
			String watermark = "N";

			while (itA.hasNext()) {
				tupleA = (Object[]) itA.next();
				emailId = tupleA[13].toString();
				gLogger.info("#emailId" + emailId);
				bankAdd = tupleA[17].toString();
				gLogger.info("#bankAdd" + bankAdd);
				pranNo = tupleA[30].toString();
				gLogger.info("#pranNo" + pranNo);
				preFlatno = tupleA[4].toString();
				gLogger.info("#preFlatno" + preFlatno);
				subFullName = tupleA[34].toString();
				gLogger.info("#subFullName" + subFullName);
				if (tupleA[41] != null) {
					taxcountry = tupleA[41].toString();
				}
				gLogger.info("#taxcountry" + taxcountry);
				if (tupleA[40] != null) {
					usPerson = tupleA[40].toString();
				}
				gLogger.info("#usPerson" + usPerson);
				if (tupleA[43] != null) {
					taxCity = tupleA[43].toString();
				}
				gLogger.info("#taxCity" + taxCity);
				if (tupleA[44] != null) {
					taxState = tupleA[44].toString();
				}
				gLogger.info("#taxState" + taxState);
				if (tupleA[45] != null) {
					taxPin = tupleA[45].toString();
				}
				gLogger.info("#taxPin" + taxPin);
				if (tupleA[46] != null) {
					tinPan = tupleA[46].toString();
				}
				gLogger.info("#tinCountry" + tinPan);
				if (tupleA[47] != null) {
					tinCountry = tupleA[47].toString();
				}
				gLogger.info("#tinCountry" + tinCountry);
				if (tupleA[42] != null) {
					taxAddress = tupleA[42].toString();
				}
				gLogger.info("#taxAddress" + taxAddress);
				// /$t opgm watermark
				if (tupleA[49] != null) {
					watermark = tupleA[49].toString();
				}
				gLogger.info("#watermark" + watermark);

			}
			// /$t opgm watermark
			inputMap.put("watermark", watermark);
			inputMap.put("taxcountry", taxcountry);
			inputMap.put("usPerson", usPerson);
			inputMap.put("taxCity", taxCity);
			inputMap.put("taxState", taxState);
			inputMap.put("taxPin", taxPin);
			inputMap.put("tinPan", tinPan);
			inputMap.put("tinCountry", tinCountry);
			inputMap.put("taxAddress", taxAddress);

			int emailIdSize = emailId.length();
			int bankAddSize = bankAdd.length();

			if (emailIdSize > 30) {
				emailId1 = emailId.substring(0, 30);
				emailId2 = emailId.substring(30, emailIdSize);
			} else {
				emailId1 = emailId.substring(0, emailIdSize);
				emailId2 = " ";
			}

			if (bankAddSize > 30) {
				bankAdd1 = bankAdd.substring(0, 30);
				bankAdd2 = bankAdd.substring(30, bankAddSize);
			} else {
				bankAdd1 = bankAdd.substring(0, bankAddSize);
				bankAdd2 = " ";
			}

			Object[] tupleC = null;
			Iterator itC = lSecCFormS1.iterator();
			String TaxCountry = null;
			while (itC.hasNext()) {
				tupleC = (Object[]) itC.next();
				nomineeName1 = tupleC[0].toString();

				nomineeName2 = tupleC[6].toString();
				nomineeName3 = tupleC[12].toString();
				nomineeRel1 = tupleC[2].toString();
				nomineeRel2 = tupleC[8].toString();
				nomineeRel3 = tupleC[14].toString();
				nomineeGuar1 = tupleC[4].toString();
				nomineeGuar2 = tupleC[10].toString();
				nomineeGuar3 = tupleC[16].toString();
				nomineeDOB1 = tupleC[1].toString();
				// $t 2019 20-12
				nomineeDOB2 = tupleC[7].toString();
				nomineeDOB3 = tupleC[13].toString();

			}
			if ((nomineeGuar1.equalsIgnoreCase(" "))
					|| (nomineeGuar1.equalsIgnoreCase(""))) {
				nomineeDOB1 = "";

			}
			// $t 2019 20-12
			if ((nomineeGuar2.equalsIgnoreCase(" "))
					|| (nomineeGuar2.equalsIgnoreCase(""))) {
				nomineeDOB2 = "";
			}
			if ((nomineeGuar3.equalsIgnoreCase(" "))
					|| (nomineeGuar3.equalsIgnoreCase(""))) {
				nomineeDOB3 = "";
			}
			// $t 2019 20-12

			gLogger.info("#nomineeGuar1" + nomineeGuar1 + "/");
			gLogger.info("#nomineeDOB1" + nomineeDOB1 + "/");
			gLogger.info("#nomineeGuar2" + nomineeGuar2 + "/");
			gLogger.info("#nomineeDOB2" + nomineeDOB2 + "/");
			gLogger.info("#nomineeGuar3" + nomineeGuar3 + "/");
			gLogger.info("#nomineeDOB3" + nomineeDOB3 + "/");
			int nmn1Size = nomineeName1.length();
			int nmn2Size = nomineeName2.length();
			int nmn3Size = nomineeName3.length();
			int nmn1Size1 = nomineeName1.length();
			// $t 2019 20-12
			int nmn2Size2 = nomineeName2.length();
			int nmn3Size3 = nomineeName3.length();
			// $t 2019 20-12
			int nmnRel1 = nomineeRel1.length();
			int nmnRel2 = nomineeRel2.length();
			int nmnRel3 = nomineeRel3.length();

			int nmnGuarSize1 = nomineeGuar1.length();
			int nmnGuarSize2 = nomineeGuar2.length();
			int nmnGuarSize3 = nomineeGuar3.length();

			if (nmn1Size > 15) {
				nmn11 = nomineeName1.substring(0, 15);
				nmn12 = nomineeName1.substring(15, nmn1Size);
			} else {
				nmn11 = nomineeName1.substring(0, nmn1Size);
				nmn12 = " ";
			}

			if (nmn1Size1 > 30) {
				nmn111 = nomineeName1.substring(0, 30);
			} else {
				nmn111 = nomineeName1.substring(0, nmn1Size);
			}

			if (nmn2Size > 15) {
				nmn21 = nomineeName2.substring(0, 15);
				nmn22 = nomineeName2.substring(15, nmn2Size);
			} else {
				nmn21 = nomineeName2.substring(0, nmn2Size);
				nmn22 = " ";
			}
			// $t 2019 20-12
			if (nmn2Size2 > 30) {
				nmn112 = nomineeName2.substring(0, 30);
			} else {
				nmn112 = nomineeName2.substring(0, nmn2Size);
			}
			//
			if (nmn3Size > 15) {
				nmn31 = nomineeName3.substring(0, 15);
				nmn32 = nomineeName3.substring(15, nmn3Size);
			} else {
				nmn31 = nomineeName3.substring(0, nmn3Size);
				nmn32 = " ";
			}
			// $t 2019 20-12
			if (nmn3Size3 > 30) {
				nmn113 = nomineeName3.substring(0, 30);
			} else {
				nmn113 = nomineeName3.substring(0, nmn3Size);
			}
			//
			if (nmnRel1 > 15) {
				nmnRel11 = nomineeRel1.substring(0, 15);
				nmnRel12 = nomineeRel1.substring(15, nmnRel1);
			} else {
				nmnRel11 = nomineeRel1.substring(0, nmnRel1);
				nmnRel12 = " ";
			}
			if (nmnRel2 > 15) {
				nmnRel21 = nomineeRel2.substring(0, 15);
				nmnRel22 = nomineeRel2.substring(15, nmnRel2);
			} else {
				nmnRel21 = nomineeRel2.substring(0, nmnRel2);
				nmnRel22 = " ";
			}

			if (nmnRel3 > 15) {
				nmnRel31 = nomineeRel3.substring(0, 15);
				nmnRel32 = nomineeRel3.substring(15, nmnRel3);
			} else {
				nmnRel31 = nomineeRel3.substring(0, nmnRel3);
				nmnRel32 = " ";
			}

			if (nmnGuarSize1 > 15) {
				nmnGuar11 = nomineeGuar1.substring(0, 15);
				nmnGuar12 = nomineeGuar1.substring(15, nmnGuarSize1);
			} else {
				nmnGuar11 = nomineeGuar1.substring(0, nmnGuarSize1);
				nmnGuar12 = " ";
			}
			if (nmnGuarSize2 > 15) {
				nmnGuar21 = nomineeGuar2.substring(0, 15);
				nmnGuar22 = nomineeGuar2.substring(15, nmnGuarSize2);
			} else {
				nmnGuar21 = nomineeGuar2.substring(0, nmnGuarSize2);
				nmnGuar22 = " ";
			}

			if (nmnGuarSize3 > 15) {
				nmnGuar31 = nomineeGuar3.substring(0, 15);
				nmnGuar32 = nomineeGuar3.substring(15, nmnGuarSize3);
			} else {
				nmnGuar31 = nomineeGuar3.substring(0, nmnGuarSize3);
				nmnGuar32 = " ";
			}

			gLogger.info("Third relation is *******" + nmnRel31);

			Object[] check = null;
			Iterator count = lNmnCount.iterator();

			while (count.hasNext()) {
				check = (Object[]) count.next();
				Pnmn1 = check[0].toString();
				Pnmn2 = check[1].toString();
				Pnmn3 = check[2].toString();
				Pnmnage1 = check[3].toString();
				Pnmnage2 = check[4].toString();
				Pnmnage3 = check[5].toString();
			}

			inputMap.put("Pnmn1", Pnmn1);
			inputMap.put("Pnmn2", Pnmn2);
			inputMap.put("Pnmn3", Pnmn3);
			inputMap.put("Pnmnage1", Pnmnage1);
			inputMap.put("Pnmnage2", Pnmnage2);
			inputMap.put("Pnmnage3", Pnmnage3);

			FrmFormS1Dtls ffs = new FrmFormS1Dtls();

			String preflatNo = ffs.getPresentAddFlatNo();

			if (lSecAFormS1 != null && lSecAFormS1.size() > 0) {
				inputMap.put("lSecAFormS1", lSecAFormS1);

			}
			gLogger.info("Present flat no is*****" + preFlatno);
			inputMap.put("preFlatno", preFlatno);
			if (lSecBFormS1 != null && lSecBFormS1.size() > 0) {
				inputMap.put("lSecBFormS1", lSecBFormS1);

			}
			if (lSecCFormS1 != null && lSecCFormS1.size() > 0) {
				inputMap.put("lSecCFormS1", lSecCFormS1);
			}

			Object[] tupleddto = null;
			Iterator ddto = DDTORegNo.iterator();
			String ddoRegNo = null;
			String dtoRegNo = null;
			while (ddto.hasNext()) {
				tupleddto = (Object[]) ddto.next();
				dtoRegNo = tupleddto[0].toString();
				ddoRegNo = tupleddto[1].toString();

			}

			if (DDTORegNo != null) {
				inputMap.put("dtoRegNo", dtoRegNo);
				inputMap.put("ddoRegNo", ddoRegNo);
			}

			inputMap.put("nomineeDOB1", nomineeDOB1);
			// $t 2019 20-12
			inputMap.put("nomineeDOB2", nomineeDOB2);
			inputMap.put("nomineeDOB3", nomineeDOB3);
			//
			inputMap.put("Off1", Off1);
			inputMap.put("Off2", Off2);
			inputMap.put("dept1", dept1);
			inputMap.put("dept2", dept2);
			inputMap.put("min1", min1);
			inputMap.put("min2", min2);
			inputMap.put("emailId1", emailId1);
			inputMap.put("emailId2", emailId2);
			inputMap.put("bankAdd1", bankAdd1);
			inputMap.put("bankAdd2", bankAdd2);

			inputMap.put("nmn111", nmn111);
			// $t 2019 21-12
			inputMap.put("nmn112", nmn112);
			inputMap.put("nmn113", nmn113);
			//
			inputMap.put("nmn11", nmn11);
			inputMap.put("nmn12", nmn12);
			inputMap.put("nmn21", nmn21);
			inputMap.put("nmn22", nmn22);
			inputMap.put("nmn31", nmn31);
			inputMap.put("nmn32", nmn32);
			inputMap.put("nmnRel11", nmnRel11);
			inputMap.put("nmnRel12", nmnRel12);
			inputMap.put("nmnRel21", nmnRel21);
			inputMap.put("nmnRel22", nmnRel22);
			inputMap.put("nmnRel31", nmnRel31);
			inputMap.put("nmnRel32", nmnRel32);
			inputMap.put("nmnGuar11", nmnGuar11);
			inputMap.put("nmnGuar12", nmnGuar12);
			inputMap.put("nmnGuar21", nmnGuar21);
			inputMap.put("nmnGuar22", nmnGuar22);
			inputMap.put("nmnGuar31", nmnGuar31);
			inputMap.put("nmnGuar32", nmnGuar32);
			inputMap.put("sevaarthId", sevaarthId);
			inputMap.put("pranNo", pranNo);

			inputMap.put("subFullName", subFullName);
			// gLogger.info("ddoCode for form S1:"+ddoCode);
			gLogger.info("subFullName" + subFullName);
			// Started for new formS1
			// if(ddoCode.equals("1111222222")){
			resObj.setViewName("formS1PranFormSectionWise");
			// }
			// else

			// resObj.setViewName("NPSPRANFORMS1");
			resObj.setResultValue(inputMap);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return resObj;
	}

	public ResultObject getEmpNameForS1AutoCompleteDCPS(
			Map<String, Object> inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		List finalList = null;
		String lStrEmpName = null;
		String lStrSearchBy = null;
		String lStrDDOCode = null;
		String lStrSearchType = null;

		try {
			gLogger.info("Inside getEmpNameForS1AutoCompleteDCPS service");
			setSessionInfo(inputMap);
			FormS1DAO lObjSearchEmployeeDAO = new FormS1DAOImpl(MstEmp.class,
					serv.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null,
					serv.getSessionFactory());

			lStrEmpName = StringUtility.getParameter("searchKey", request)
					.trim();

			lStrSearchBy = StringUtility.getParameter("searchBy", request)
					.trim();

			lStrSearchType = StringUtility.getParameter("searchType", request);

			if (lStrSearchBy.equals("searchFromDDODeSelection")
					|| lStrSearchBy.equals("searchByDDO")) {
				lStrDDOCode = lObjDcpsCommonDAO.getDdoCodeForDDO(gLngPostId);
			}

			gLogger.info("DDO code is ********" + lStrDDOCode);
			finalList = lObjSearchEmployeeDAO.getEmpNameForS1AutoComplete(
					lStrEmpName.toUpperCase(), lStrDDOCode);

			String lStrTempResult = null;
			if (finalList != null) {
				lStrTempResult = new AjaxXmlBuilder().addItems(finalList,
						"desc", "id", true).toString();

			}
			inputMap.put("ajaxKey", lStrTempResult);
			objRes.setResultValue(inputMap);
			objRes.setViewName("ajaxData");

		} catch (Exception ex) {
			objRes.setResultValue(null);
			objRes.setThrowable(ex);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			// ex.printStackTrace();
			return objRes;
		}

		return objRes;

	}

	public ResultObject checkSevaarthExistInDDO(Map<String, Object> inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		List finalList = null;
		String txtSevaarthId = null, ddo_Code = null;
		String lStrDDOCode = null;
		String exist = "NA";
		String empName = null;
		String updation = "blank";
		String lSBStatus = null;
		String ddoRegStatus = "NO";
		// String lddoOffice="";
		// String ddoStatus="";

		try {
			setSessionInfo(inputMap);
			FormS1DAO lObjSearchEmployeeDAO = new FormS1DAOImpl(MstEmp.class,
					serv.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null,
					serv.getSessionFactory());

			txtSevaarthId = StringUtility.getParameter("empSevarthId", request)
					.trim();

			// empName = StringUtility.getParameter("txtEmployeeName",
			// request).trim();
			gLogger.info("Sevaarth in service  is ********" + txtSevaarthId);
			ddo_Code = StringUtility.getParameter("empDdoCode", request).trim();
			if (ddo_Code == null)
				lStrDDOCode = lObjDcpsCommonDAO.getDdoCodeForDDO(gLngPostId);
			else
				lStrDDOCode = ddo_Code;

			exist = lObjSearchEmployeeDAO.checkSevaarthIdExist(txtSevaarthId,
					lStrDDOCode);
			gLogger.info("Flag is  ********" + exist);
			// ddoStatus=lObjSearchEmployeeDAO.chkFrmUpdatedByLgnDdo(txtSevaarthId);
			// if((ddoStatus!=lStrDDOCode)&&(!ddoStatus.equalsIgnoreCase("Z"))){
			// lddoOffice=ddoStatus;
			// lSBStatus =
			// getResponsecheckSevaarthIdExist(exist,lddoOffice).toString();
			// }

			// if(exist.equals("AVAIL")){
			updation = lObjSearchEmployeeDAO.checkUpdationDone(txtSevaarthId);

			// }
			gLogger.info("Flag is  ********" + updation);

			ddoRegStatus = lObjSearchEmployeeDAO
					.checkDDORegPresent(lStrDDOCode);
			gLogger.info("ddoRegStatus******" + ddoRegStatus);
			String checkForPpan = lObjSearchEmployeeDAO
					.checkForPpan(txtSevaarthId);
			gLogger.info("checkForPpan status is ******" + checkForPpan);
			if (checkForPpan.equals("NoPpan")) {
				gLogger.info("i m inside checking for ppan***********");
				lSBStatus = getResponsecheckSevaarthIdExist("NoPpan")
						.toString();

			} else if (updation.equals("blank")) {
				lSBStatus = getResponsecheckSevaarthIdExist(updation)
						.toString();
				gLogger.info("in blank*********");

			} else if (ddoRegStatus.equals("NO")) {
				lSBStatus = getResponsecheckSevaarthIdExist(ddoRegStatus)
						.toString();
				gLogger.info("in NO*********");

			}

			// Added BY roshan
			else if (lObjSearchEmployeeDAO.checkBranchAddress(txtSevaarthId)) {
				gLogger.info("hi i m insside lObjSearchEmployeeDAO.checkBranchAddress***********");
				lSBStatus = getResponsecheckSevaarthIdExist("BANKNA")
						.toString();
				gLogger.info("in BANKNA*********");

			} else if (exist.equals("NA")) {
				// lddoOffice=lObjSearchEmployeeDAO.getDdo(txtSevaarthId);
				lSBStatus = getResponsecheckSevaarthIdExist(exist).toString();
				gLogger.info("in NA*********");

			}
			// ended by roshan

			else {
				lSBStatus = getResponsecheckSevaarthIdExist("eligible")
						.toString();
			}

			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
					lSBStatus).toString();
			inputMap.put("ajaxKey", lStrResult);
			objRes.setViewName("ajaxData");

			objRes.setResultValue(inputMap);

		} catch (Exception ex) {
			objRes.setResultValue(null);
			objRes.setThrowable(ex);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			// ex.printStackTrace();
			return objRes;
		}

		return objRes;

	}

	private StringBuilder getResponsecheckSevaarthIdExist(String flag) {
		gLogger.info("Flag is  in AJAX********" + flag);
		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");
		// lStrBldXML.append("<DdoFlag>");
		// lStrBldXML.append(ddoFlag);
		// lStrBldXML.append("</DdoFlag>");

		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	public ResultObject getEmpListForFormS(Map inputMap) throws Exception {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List lstEmpForFrmS1Edit = null;
		List empDesigList = null;
		String strDDOCode = null;
		String txtSearch = null;
		String flag = null;
		String sevarthId = null;
		String IsDeputation = null;
		int DepSize = 100;
		String depPrintDdoFlag = "N";
		// String depStatus = null;
		// /$t OPGM 30-9-2020
		String showBtn = "showbtn"; // /null;
		String flagDDO = "allowDDO"; // "PAO";
		/*
		 * String showBtn =null; ///null; String flagDDO ="PAO"; //"PAO";
		 */
		// /$t
		try {
			setSessionInfo(inputMap);
			DcpsCommonDAO lObjDcpsCommonDao = new DcpsCommonDAOImpl(null,
					serv.getSessionFactory());
			FormS1DAO lObjSearchEmployeeDAO = new FormS1DAOImpl(MstEmp.class,
					serv.getSessionFactory());
			NsdlNpsDAOImpl lObjNsdlDAO = new NsdlNpsDAOImpl(null,
					this.serv.getSessionFactory());
			depPrintDdoFlag = StringUtility.getParameter("depPrintDdoFlag",
					request);
			strDDOCode = lObjDcpsCommonDao.getDdoCodeForDDO(gLngPostId);
			sevarthId = StringUtility.getParameter("sevarthId", request);
			gLogger.info("logged in ddo code: " + strDDOCode);
			IsDeputation = StringUtility.getParameter((String) "IsDeputation",
					(HttpServletRequest) this.request);
			txtSearch = StringUtility.getParameter("searchTxt", request);
			flag = StringUtility.getParameter("flag", request);
			List subTr = lObjNsdlDAO.getAllSubTreasury(this.gStrLocationCode);
			// $t OPGM 3-7-2020 after search to hide button
			// added else stmt because button show to all conditions
			// after search emp button enabled to all employees but checkbox
			// disabled
			/*
			 * if(flag==null || flag.equals("")){ showBtn="showbtn"; }else{
			 * showBtn="showbtn"; } //$t OPGM 13-7-2020 show buttons to
			 * particular DDO if((strDDOCode==null ||
			 * strDDOCode.equals(""))&&(this
			 * .gStrLocationCode.equals("7101")||this
			 * .gStrLocationCode.equals("1111"))){ flagDDO="allowDDO"; }else if(
			 * strDDOCode
			 * .equals("7101003269")||strDDOCode.equals("7101003272")||
			 * strDDOCode
			 * .equals("7101003264")||strDDOCode.equals("7101000393")||
			 * strDDOCode
			 * .equals("7101014453")||strDDOCode.equals("7101050599")||
			 * strDDOCode
			 * .equals("7101040316")||strDDOCode.equals("7101030599")||
			 * strDDOCode
			 * .equals("7101040599")||strDDOCode.equals("7101010597")||
			 * strDDOCode
			 * .equals("7101002314")||strDDOCode.equals("7101000455")||
			 * strDDOCode.equals("7101000421")||strDDOCode.equals("1111222222")
			 * ){ flagDDO="allowDDO"; }
			 */
			if (sevarthId != null && !sevarthId.equals("")) {
				txtSearch = sevarthId;
			}
			this.gLogger
					.info((Object) ("IsDeputation is Y#########" + IsDeputation));

			// depStatus=lObjSearchEmployeeDAO.checkEmpListForFrmS1Dep(strDDOCode,flag,txtSearch,IsDeputation);

			if (IsDeputation.equals("Y")) {
				this.gLogger
						.info((Object) ("txtSearch is Y in if#########" + txtSearch));
				this.gLogger
						.info((Object) ("IsDeputation is Y#########" + IsDeputation));
				// if (txtSearch != null && !txtSearch.equals("")) {///$t
				// deptopgm
				lstEmpForFrmS1Edit = new ArrayList();
				lstEmpForFrmS1Edit = lObjSearchEmployeeDAO
						.getEmpListForFrmS1Edit(strDDOCode, flag, txtSearch,
								IsDeputation, gStrLocationCode);
				empDesigList = lObjSearchEmployeeDAO
						.getEmpDesigList(strDDOCode);
				if (lstEmpForFrmS1Edit != null)
					DepSize = lstEmpForFrmS1Edit.size();
				// }
			} else {

				lstEmpForFrmS1Edit = lObjSearchEmployeeDAO
						.getEmpListForFrmS1Edit(strDDOCode, flag, txtSearch,
								IsDeputation, gStrLocationCode);
				empDesigList = lObjSearchEmployeeDAO
						.getEmpDesigList(strDDOCode);

			}
			gLogger.info("##################DepSize" + DepSize);
			// //$T OPGM 3-7-2020
			inputMap.put("searchFlag", showBtn);
			// $t
			// //$T OPGM 13-7-2020
			inputMap.put("flagDDO", flagDDO);
			// $t
			inputMap.put("depPrintDdoFlag", depPrintDdoFlag);
			// inputMap.put("depPrintDdoFlag", depPrintDdoFlag);
			inputMap.put("subTr", subTr);
			inputMap.put("DepSize", DepSize);
			inputMap.put("IsDeputation", IsDeputation);
			inputMap.put("empList", lstEmpForFrmS1Edit);

			inputMap.put("DDOCode", strDDOCode);
			inputMap.put("empDesigList", empDesigList);
			resObj.setResultValue(inputMap);
			resObj.setViewName("empListForFormS1");
		} catch (Exception e) {
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;

	}

	// to Update Method
	/*************************************************************
	 * **********Main Method*********** Delete Configured PPO in Seventh Pay
	 * **********************************************************/
	public ResultObject csrfFormforwardFormTO(Map<String, Object> inputMap) {
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String pensionerCode = "0", bank_code = "0", BRANCH_CODE = "0";
		FormS1DAOImpl FormS1DAO = new FormS1DAOImpl(null,
				serv.getSessionFactory());

		int checkRecovery = 0;
		int Delete = 0;
		setSessionInfo(inputMap);

		try {
			String status = "N";

			String sevarthId = StringUtility.getParameter("sevarthId", request);
			String sevarthid[] = sevarthId.split("~");
			System.out.println("SevarthId-->" + sevarthId);

			if (sevarthid != null && sevarthid.length > 0) {

				for (Integer lInt = 0; lInt < sevarthid.length; lInt++) {

					FormS1DAO.csrfFormforwardFormTO(sevarthid[lInt]);
					status = "Y";
				}

				String lSBStatus = getResponseUpdatedDdoCode(status).toString();
				String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
						lSBStatus.toString()).toString();
				inputMap.put("ajaxKey", lStrResult);
				resObj.setViewName("ajaxData");
				resObj.setResultValue(inputMap);
			}
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
		}
		return resObj;
	}

	public static byte[] compress(byte[] in) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DeflaterOutputStream defl = new DeflaterOutputStream(out);
			defl.write(in);
			defl.flush();
			defl.close();

			return out.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(150);
			return null;
		}
	}

	// for csrf file generation code
	// Folder created on local machine
	public ResultObject createTextFilesForCSRF(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String sevarthId1 = "";
		int fcount = 1;
		// int dcount = 1;
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		try {
			setSessionInfo(inputMap);
			{
				// //$t OPGM for only two treasury and demo treasury
				// 10-10-20(allow for all treasuries)
				// if(this.gStrLocationCode.equals("7101")||this.gStrLocationCode.equals("5101")||this.gStrLocationCode.equals("1111")){
				FormS1DAOImpl lObjFormS1 = new FormS1DAOImpl(null,
						serv.getSessionFactory());
				String dtoRegNo = "";
				StringBuffer sb = new StringBuffer();
				int count = 1;
				gLogger.info("All fine 1 is *********");
				// for multiple sevarth id
				String sevarthId = StringUtility.getParameter("sevarthId",
						request);
				String sevarthid[] = sevarthId.split("~");
				System.out.println("SevarthId-->" + sevarthid.length);
				// //$t 16-05-2020 OPGM
				// OpgmFileViewDao lObjOpgmFile = new
				// OpgmFileViewDaoImpl(null,serv.getSessionFactory());

				// Long fileSeq =
				// IFMSCommonServiceImpl.getNextSeqNum("OPGM_FILE_DTLS",inputMap);////$t
				// 3-6-2021 fileSeq

				// String
				// FileNumber=lObjFormS1.generateOpgmFile(this.gStrLocationCode,sevarthid.length,fileSeq);////$t
				// 3-6-2021 fileSeq
				String FileNumber = lObjFormS1.generateOpgmFile(
						this.gStrLocationCode, sevarthid.length);
				// //$t
				if (sevarthid != null && sevarthid.length > 0) {
					// main directory creation
					int directoryYear = Calendar.getInstance().get(
							Calendar.YEAR);
					int directoryMonth = Calendar.getInstance().get(
							Calendar.MONTH) + 1;
					String directoryFolder = Integer.toString(directoryMonth)
							+ Integer.toString(directoryYear);

					String parentDirectoryPath = request.getSession()
							.getServletContext().getRealPath("/")
							+ "/" + directoryFolder;// $there
					// String directoryPath =
					// request.getSession().getServletContext().getRealPath("/")
					// + "/" + FileNumber;
					String directoryPath = parentDirectoryPath + "/"
							+ FileNumber;// $there
					gLogger.info("realtime diretory path:" + directoryPath);
					gLogger.info("realtime diretory path:"
							+ parentDirectoryPath);
					// System.out.println("realtime diretory path:"+
					// directoryPath);
					// System.out.println("realtime diretory path2:"+
					// request.getSession().getServletContext().getRealPath("/"));
					// //$there
					File parentMainDirectory = new File(parentDirectoryPath);
					// parentMainDirectory.delete();
					// parentMainDirectory.mkdirs();
					if (!parentMainDirectory.exists()) {
						parentMainDirectory.mkdirs();
					}

					File fMainDirectory = new File(directoryPath);
					// fMainDirectory.delete();
					fMainDirectory.mkdirs();

					// main Photo creation
					// String directoryPathPhoto =
					// request.getSession().getServletContext().getRealPath("/"+FileNumber)+
					// "/" + FileNumber+"_photo";
					String directoryPathPhoto = parentDirectoryPath + "/"
							+ FileNumber + "/" + FileNumber + "_photo";// $there
					// System.out.println("realtime directoryPathPhoto path:"+
					// directoryPathPhoto);
					gLogger.info("realtime directoryPathPhoto path:"
							+ directoryPathPhoto);
					File fphoto = new File(directoryPathPhoto);
					// fphoto.delete();
					fphoto.mkdirs();

					// Jitu 29-Dec-2022
					// Start new directory creation for Citizen and Orphan photo
					String directoryPathcitizendocphoto = parentDirectoryPath
							+ "/" + FileNumber + "/" + FileNumber
							+ "_citizendocphoto";// $there
					gLogger.info("realtime directoryPath Citizen Doc Photo path:"
							+ directoryPathcitizendocphoto);
					File fCphoto = new File(directoryPathcitizendocphoto);
					fCphoto.mkdirs();

					String directoryPathorphandocphoto = parentDirectoryPath
							+ "/" + FileNumber + "/" + FileNumber
							+ "_orphandocphoto";// $there
					gLogger.info("realtime directoryPath Orphan Doc Photo path:"
							+ directoryPathorphandocphoto);
					File forphanphoto = new File(directoryPathorphandocphoto);
					forphanphoto.mkdirs();

					// End new directory creation for Citizen and Orphan photo

					// main signature creation
					// String directoryPathSign =
					// request.getSession().getServletContext().getRealPath("/"+FileNumber)+
					// "/" + FileNumber+"_sig";
					String directoryPathSign = parentDirectoryPath + "/"
							+ FileNumber + "/" + FileNumber + "_sig";// $there
					// System.out.println("realtime directoryPathSign path:"+
					// directoryPathSign);
					gLogger.info("realtime directoryPathSign path:"
							+ directoryPathSign);
					File fsign = new File(directoryPathSign);
					// fsign.delete();
					fsign.mkdirs();

					Date date = new Date();
					SimpleDateFormat formatter = new SimpleDateFormat(
							"MMddyyyy");
					Object strDate = formatter.format(date);

					DecimalFormat df = new DecimalFormat("000000");
					DecimalFormat dfHeader = new DecimalFormat("000");
					String c = df.format(9);
					// ////$t opgm
					// DecimalFormat df4 = new DecimalFormat("000");
					// if(strDate)

					// dfHeader////$t OPGM
					Long Header = 0L;

					Header = lObjFormS1.getTodayGeneratedFileCount();

					if (Header < 1) {
						Header = 1L;
					}

					sb.append("000001^FH^PRAN^R^" + strDate + "^"
							+ dfHeader.format(Header) + "^"
							+ df.format(sevarthid.length) + "^NCRA"); // header
					sb.append("\r\n");

					DecimalFormat df1 = new DecimalFormat("000000");

					// fro SFTP Session Code

					String SFTPHOST = "10.34.82.225";

					int SFTPPORT = 8888;

					String SFTPUSER = "tcsadmin";

					String SFTPPASS = "Tcsadmin@123";

					String SFTPWORKINGDIR = "/home/EmployeeConfigurationForm";

//					 String SFTPHOST= "100.70.201.169";
//					 int SFTPPORT = 22;
//					 String SFTPUSER = "mahait";
//					
//					 String SFTPPASS = "Mahait@9999";
//					 String SFTPWORKINGDIR = "/OPGM" + "/" + directoryMonth +
//					 directoryYear;

					Session session = null;

					Channel channel = null;

					ChannelSftp channelSftp = null;
					byte[] data = (byte[]) null;
					byte[] lBytes1 = (byte[]) null;
					JSch jsch = new JSch();

					// //$t OPGM
					Long dcount = lObjFormS1
							.getGeneratedEmpCount(this.gStrLocationCode);
					// System.out.println("dcount-->"+dcount);
					dcount = dcount + sevarthid.length;
					// System.out.println("dcount1-->"+dcount);

					for (Integer lInt = 0; lInt < sevarthid.length; lInt++) {
						sevarthId1 = sevarthid[lInt];
						// ///$t 17-5-2020 OPGM
						// lObjFormS1.updateOpgmIdFrm(sevarthId1,FileNumber);
						// $t
						List fdDtls = lObjFormS1.getFDDtls(sevarthId1);
						List ndDtls1 = lObjFormS1.getNDDtls1(sevarthId1);
						List ndDtls2 = lObjFormS1.getNDDtls2(sevarthId1);
						List ndDtls3 = lObjFormS1.getNDDtls3(sevarthId1);
						List ddDtls = lObjFormS1.getDDdtls(sevarthId1);

						List AssoDtoRegNo = lObjFormS1
								.getAssoDtoRegNo(sevarthId1);

						DecimalFormat df2 = new DecimalFormat("000000");
						DecimalFormat df3 = new DecimalFormat("0000000");
						Iterator iterator = fdDtls.iterator();

						while (iterator.hasNext()) {
							sb.append(df1.format(++count) + "^FD^N^"
									+ df2.format(fcount) + "^^"
									+ AssoDtoRegNo.get(0) + "100"
									+ df3.format(dcount) + iterator.next()); // header
							sb.append("\r\n");
						}
						// for first nominee
						if (ndDtls1.size() > 0) {
							Iterator iterator1 = ndDtls1.iterator();
							while (iterator1.hasNext()) {
								sb.append(df1.format(++count) + "^ND^N^"
										+ iterator1.next()); // header String
								sb.append("\r\n");
							}
						}
						// for second nominee
						if (ndDtls2.size() > 0) {
							Iterator iterator1 = ndDtls2.iterator();
							while (iterator1.hasNext()) {
								sb.append(df1.format(++count) + "^ND^N^"
										+ iterator1.next()); // header String
								sb.append("\r\n");
							}
						}
						// for third nominee
						if (ndDtls3.size() > 0) {
							Iterator iterator1 = ndDtls3.iterator();
							while (iterator1.hasNext()) {
								sb.append(df1.format(++count) + "^ND^N^"
										+ iterator1.next()); // header String
								sb.append("\r\n");
							}
						}

						// for dd details
						Iterator iterator2 = ddDtls.iterator();
						while (iterator2.hasNext()) {
							sb.append(df1.format(++count) + "^DD^N^"
									+ iterator2.next()); // header String
							sb.append("\r\n");
						}

						// for image code
						try {
							try{
							Class.forName("com.ibm.db2.jcc.DB2Driver");
//							 con = DriverManager.getConnection(
//							 "jdbc:db2://100.70.201.168:50000/IFMSMIGR",
//							 "ifms", "Mahait@99");

							// con =
							// DriverManager.getConnection("jdbc:db2://10.34.82.226:60000/IFMSMIGR","db2mahait",
							// "Mahasevaarth@123");// where
							con = DriverManager.getConnection(
									"jdbc:db2://10.34.82.235:50015/IFMSMIGR",
									"ifms", "IFMS@dat#@!");// where

							// ps =
							// con.prepareStatement("select mpg.SR_NO,SEVARTH_ID from CMN_ATTACHMENT_MPG mpg  join MST_DCPS_EMP emp on emp.SEVARTH_ID = '"+
							// sevarthId1+"'   and emp.PHOTO_ATTACHMENTID=mpg.ATTACHMENT_ID  ");
							ps = con.prepareStatement(" select mpg.SR_NO,SEVARTH_ID,doc.FINAL_ATTACHMENT from CMN_ATTACHMENT_MPG mpg  join MST_DCPS_EMP emp on emp.SEVARTH_ID = '"
									+ sevarthId1
									+ "' "
									+ " and emp.PHOTO_ATTACHMENTID=mpg.ATTACHMENT_ID  join CMN_ATTDOC_MST doc on doc.sr_no=mpg.SR_NO "
									+ " and mpg.ACTIVATE_FLAG='Y' order by mpg.CREATED_DATE desc FETCH first ROW only "); // $t
																															// opgm
																															// 6-9-20
							rs = ps.executeQuery();
							if (rs.next()) {
								String jpg = "_photo.jpg";

								// byte arrayOfByte[];
								String srno = rs.getString(1);
								// for getting files from VM
								Blob b = rs.getBlob(3);// 1 means 1nd column
								if (b != null) {
									byte arrayOfByte[] = b.getBytes(1,
											(int) b.length());
									FileOutputStream foutImage = new FileOutputStream(
											directoryPathPhoto + "/"
													+ AssoDtoRegNo.get(0)
													+ "100"
													+ df3.format(dcount) + jpg);
									foutImage.write(arrayOfByte);
									foutImage.close();
								}
								// End VM CODE

								// for upload from SFTP
								if (b == null) {

									session = jsch.getSession(SFTPUSER,
											SFTPHOST, SFTPPORT);

									session.setPassword(SFTPPASS);

									Properties config = new Properties();

									config.put("StrictHostKeyChecking", "no");

									session.setConfig(config);

									session.connect();

									byte[] buffer = new byte['?'];

									channel = session.openChannel("sftp");
									channelSftp = (ChannelSftp) channel;
									channel.connect();
									channelSftp.cd(SFTPWORKINGDIR);

									byte[] lBytes = (byte[]) null;

									String fileName = srno;
									BufferedInputStream bis = new BufferedInputStream(
											channelSftp.get(fileName));

									File newFile = new File(fileName);

									channelSftp.get(
											"/home/EmployeeConfigurationForm/"
													+ fileName,
											parentDirectoryPath);
									byte[] arrayOfByte = Files
											.readAllBytes(new File(
													parentDirectoryPath + "/"
															+ fileName)
													.toPath());
									FileOutputStream foutImage = new FileOutputStream(
											directoryPathPhoto + "/"
													+ AssoDtoRegNo.get(0)
													+ "100"
													+ df3.format(dcount) + jpg);
									foutImage.write(arrayOfByte);
									foutImage.close();
									// End SFTP CODE
								}
								// to upload image on real path
								// FileOutputStream foutImage = new
								// FileOutputStream(directoryPathPhoto+ "/" +
								// AssoDtoRegNo.get(0) + "100"+
								// df3.format(dcount) + jpg);
								// foutImage.write(arrayOfByte);
								// foutImage.close();
								// System.out.println("foutImage--"+directoryPathPhoto+
								// "/" + AssoDtoRegNo.get(0) + "100"+
								// df3.format(dcount) + jpg);

								// to compress code
								File input = new File(directoryPathPhoto + "/"
										+ AssoDtoRegNo.get(0) + "100"
										+ df3.format(dcount) + jpg);
								BufferedImage image = ImageIO.read(input);
								BufferedImage resized = resize(image, 150, 150);

								// System.out.println("compress code--"+directoryPathPhoto
								// + "/"+ AssoDtoRegNo.get(0) + "100"+
								// df3.format(dcount) + jpg);

								File output = new File(directoryPathPhoto + "/"
										+ AssoDtoRegNo.get(0) + "100"
										+ df3.format(dcount) + jpg);
								ImageIO.write(resized, "jpg", output);
								// System.out.println("compress code1--"+directoryPathPhoto+
								// "/"+ AssoDtoRegNo.get(0) + "100"+
								// df3.format(dcount) + jpg);

							}
							
							/* End for Photo */

							/* for signature */
							// for getting sign from db
							ps1 = con
									.prepareStatement("select mpg.SR_NO,SEVARTH_ID,doc.FINAL_ATTACHMENT from CMN_ATTACHMENT_MPG mpg join MST_DCPS_EMP emp on emp.SEVARTH_ID = '"
											+ sevarthId1
											+ "' "
											+ " and emp.SIGNATURE_ATTACHMENTID=mpg.ATTACHMENT_ID join CMN_ATTDOC_MST doc on doc.sr_no=mpg.SR_NO "
											+ " and mpg.ACTIVATE_FLAG='Y' order by mpg.CREATED_DATE desc FETCH first ROW only ");// $t
																																	// opgm
																																	// 6-9-20

							// ps1 =
							// con.prepareStatement("select mpg.SR_NO,SEVARTH_ID from CMN_ATTACHMENT_MPG mpg join MST_DCPS_EMP emp on emp.SEVARTH_ID = '"+
							// sevarthId1+"' "
							// +" and emp.SIGNATURE_ATTACHMENTID=mpg.ATTACHMENT_ID ");

							String jpg = "_sig.jpg";
							rs1 = ps1.executeQuery();
							if (rs1.next()) {

								String signature = rs1.getString(1);
								String fileName = signature;

								// for VM COde
								Blob b = rs1.getBlob(3);// 1 means 1nd column
								if (b != null) {
									byte arrayOfByte[] = b.getBytes(1,
											(int) b.length());
									FileOutputStream fuploadsign = new FileOutputStream(
											directoryPathSign + "/"
													+ AssoDtoRegNo.get(0)
													+ "100"
													+ df3.format(dcount) + jpg);
									// System.out.println("fuploadsign--"+directoryPathSign+
									// "/"+ AssoDtoRegNo.get(0) + "100"+
									// df3.format(dcount) + jpg);
									gLogger.info("fuploadsign--"
											+ directoryPathSign + "/"
											+ AssoDtoRegNo.get(0) + "100"
											+ df3.format(dcount) + jpg);
									fuploadsign.write(arrayOfByte);
									fuploadsign.close();
								}
								// End Vm Code

								// for upload from SFTP
								if (b == null) {
									session = jsch.getSession(SFTPUSER,
											SFTPHOST, SFTPPORT);

									session.setPassword(SFTPPASS);

									Properties config = new Properties();

									config.put("StrictHostKeyChecking", "no");

									session.setConfig(config);

									session.connect();

									byte[] buffer = new byte['?'];

									channel = session.openChannel("sftp");
									channelSftp = (ChannelSftp) channel;
									channel.connect();
									channelSftp.cd(SFTPWORKINGDIR);
									byte[] lBytes = (byte[]) null;

									// for sftp sign code
									BufferedInputStream bis = new BufferedInputStream(
											channelSftp.get(fileName));

									File newFile = new File(fileName);

									channelSftp.get(
											"/home/EmployeeConfigurationForm/"
													+ fileName,
											parentDirectoryPath);
									byte[] arrayOfByte = Files
											.readAllBytes(new File(
													parentDirectoryPath + "/"
															+ fileName)
													.toPath());
									// End for sftp sign code

									FileOutputStream fuploadsign = new FileOutputStream(
											directoryPathSign + "/"
													+ AssoDtoRegNo.get(0)
													+ "100"
													+ df3.format(dcount) + jpg);
									// System.out.println("fuploadsign--"+directoryPathSign+
									// "/"+ AssoDtoRegNo.get(0) + "100"+
									// df3.format(dcount) + jpg);
									gLogger.info("fuploadsign--"
											+ directoryPathSign + "/"
											+ AssoDtoRegNo.get(0) + "100"
											+ df3.format(dcount) + jpg);
									fuploadsign.write(arrayOfByte);
									fuploadsign.close();
								}

								// // for upload sign

								// to compress code
								File input = new File(directoryPathSign + "/"
										+ AssoDtoRegNo.get(0) + "100"
										+ df3.format(dcount) + jpg);
								// System.out.println("Sign compress code--"+directoryPathSign+
								// "/"+ AssoDtoRegNo.get(0) + "100"+
								// df3.format(dcount) + jpg);
								gLogger.info("Sign compress code--"
										+ directoryPathSign + "/"
										+ AssoDtoRegNo.get(0) + "100"
										+ df3.format(dcount) + jpg);
								BufferedImage image = ImageIO.read(input);
								BufferedImage resized = resize(image, 150, 150);

								File output = new File(directoryPathSign + "/"
										+ AssoDtoRegNo.get(0) + "100"
										+ df3.format(dcount) + jpg);
								// System.out.println("Sign compress 1 code--"+directoryPathSign+
								// "/"+ AssoDtoRegNo.get(0) + "100"+
								// df3.format(dcount) + jpg);
								gLogger.info("Sign compress 1 code--"
										+ directoryPathSign + "/"
										+ AssoDtoRegNo.get(0) + "100"
										+ df3.format(dcount) + jpg);
								ImageIO.write(resized, "jpg", output);
							}
						}
						catch (Exception e) {
							e.printStackTrace();
						}
							/* End Code for signature */

							/* Start Orphan document */

							ps2 = con
									.prepareStatement(" SELECT mpg.SR_NO,frm.SEVARTH_ID,doc.FINAL_ATTACHMENT FROM CMN_ATTACHMENT_MPG mpg join FRM_FORM_S1_DTLS frm on frm.SEVARTH_ID= '"
											+ sevarthId1
											+ "' "
											+ " and frm.ORPHAN_ATTACHMENTID=mpg.ATTACHMENT_ID join CMN_ATTDOC_MST doc on doc.sr_no=mpg.SR_NO "
											+ " and mpg.ACTIVATE_FLAG='Y' order by mpg.CREATED_DATE desc FETCH first ROW only "); // jitu
																																	// 2-feb-2023
							rs2 = ps2.executeQuery();
							if (rs2.next()) {
								String orphanjpg = "_orphandocphoto.jpg";

								// byte arrayOfByte[];
								String srno = rs2.getString(1);
								// for getting files from VM
								Blob b1 = rs2.getBlob(3);// 1 means 1nd column
								if (b1 != null) {
									byte arrayOfByte[] = b1.getBytes(1,
											(int) b1.length());
									FileOutputStream foutImage1 = new FileOutputStream(
											directoryPathorphandocphoto + "/"
													+ AssoDtoRegNo.get(0)
													+ "100"
													+ df3.format(dcount)
													+ orphanjpg);
									foutImage1.write(arrayOfByte);
									foutImage1.close();
								}
								// End VM CODE

								// for upload from SFTP
								if (b1 == null) {

									session = jsch.getSession(SFTPUSER,
											SFTPHOST, SFTPPORT);

									session.setPassword(SFTPPASS);

									Properties config = new Properties();

									config.put("StrictHostKeyChecking", "no");

									session.setConfig(config);

									session.connect();

									byte[] buffer = new byte['?'];

									channel = session.openChannel("sftp");
									channelSftp = (ChannelSftp) channel;
									channel.connect();
									channelSftp.cd(SFTPWORKINGDIR);

									byte[] lBytes = (byte[]) null;

									String fileName1 = srno;
									BufferedInputStream bis = new BufferedInputStream(
											channelSftp.get(fileName1));

									File newFile1 = new File(fileName1);

									channelSftp.get(
											"/home/EmployeeConfigurationForm/"
													+ fileName1,
											directoryPathorphandocphoto);
									byte[] arrayOfByte = Files
											.readAllBytes(new File(
													directoryPathorphandocphoto
															+ "/" + fileName1)
													.toPath());
									FileOutputStream foutImage2 = new FileOutputStream(
											directoryPathorphandocphoto + "/"
													+ AssoDtoRegNo.get(0)
													+ "100"
													+ df3.format(dcount) + orphanjpg);
									foutImage2.write(arrayOfByte);
									foutImage2.close();
									// End SFTP CODE
								}
								// to upload image on real path
								// FileOutputStream foutImage = new
								// FileOutputStream(directoryPathPhoto+ "/" +
								// AssoDtoRegNo.get(0) + "100"+
								// df3.format(dcount) + jpg);
								// foutImage.write(arrayOfByte);
								// foutImage.close();
								// System.out.println("foutImage--"+directoryPathPhoto+
								// "/" + AssoDtoRegNo.get(0) + "100"+
								// df3.format(dcount) + jpg);

								// to compress code
								File input = new File(
										directoryPathorphandocphoto + "/"
												+ AssoDtoRegNo.get(0) + "100"
												+ df3.format(dcount) + orphanjpg);
								BufferedImage image = ImageIO.read(input);
								BufferedImage resized = resize(image, 150, 150);

								// System.out.println("compress code--"+directoryPathPhoto
								// + "/"+ AssoDtoRegNo.get(0) + "100"+
								// df3.format(dcount) + jpg);

								File output = new File(
										directoryPathorphandocphoto + "/"
												+ AssoDtoRegNo.get(0) + "100"
												+ df3.format(dcount) + orphanjpg);
								ImageIO.write(resized, "jpg", output);
								// System.out.println("compress code1--"+directoryPathPhoto+
								// "/"+ AssoDtoRegNo.get(0) + "100"+
								// df3.format(dcount) + jpg);

							}

							/* end orphan documnet */

							/*
							 * Remove employee from TO screen after ZIP
							 * generated
							 */
							// /this method not used ,stage=3 set in another
							// method
							// lObjFormS1.removeEmpFromTo(sevarthId1);
							/*
							 * End Remove employee from TO screen after ZIP
							 * generated
							 */
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (rs != null)
								rs.close();
							if (ps != null)
								ps.close();
							if (rs1 != null)
								rs1.close();
							if (ps1 != null)
								ps1.close();
							if (rs2 != null)
								rs2.close();
							if (ps2 != null)
								ps2.close();
							if (con != null)
								con.close();
						}

						// for count increment purpose
						fcount++;
						dcount--;
					}
					PrintWriter fout = new PrintWriter(directoryPath + "/"
							+ FileNumber + ".txt");
					// System.out.println(" FileNumber.txt code--"+directoryPath+
					// "/");
					gLogger.info("FileNumber.txt code--" + directoryPath + "/");
					// appends the string to the file
					fout.write(sb.toString());
					fout.close();

					String ZIpPath = null;

					try {
						/* validate folder */
						// System.out.println(" before jar--");
						String[] inputParameters = { directoryPath };
						// System.out.println(" String[] inputParameters jar--"+inputParameters);
						// RunSubsRegFvu.main(inputParameters);

						// INSIDEJAR.main(inputParameters);

						CallJarFromOutside(inputParameters, request);

						// System.out.println(" RunSubsRegFvu.main(inputParameters) jar--");
						gLogger.info("RunSubsRegFvu.main(inputParameters) jar--");

						/* download error file */
						try {
							File chkFile = new File(fMainDirectory + "/"
									+ "Processed");// Processed
							if (chkFile.isDirectory()
									&& chkFile.list().length == 0) {

								/*
								 * start $t OPGM 1-9-20 due to error update
								 * file_status=-1 in opgm_file_dtls
								 */
								lObjFormS1.updateFileStatusOpgm(FileNumber);
								/*
								 * End due to error update file_status=-1 in
								 * opgm_file_dtls
								 */
								gLogger.info("Directory is  empty");
								File downloadErrFile = new File(fMainDirectory
										+ "/" + "Error" + "/" + FileNumber
										+ "_Resp.html");// Processed

								if (downloadErrFile.exists()) {
									// System.out.print("here");

									response.setContentType("APPLICATION/OCTET-STREAM");
									response.setHeader("Content-Disposition",
											"attachment;filename=ErrorFile"
													+ ".html");

									FileInputStream fileInputStream = new FileInputStream(
											downloadErrFile);
									OutputStream os = response
											.getOutputStream();
									byte[] bufferData = new byte[4096];
									int read = 0;
									while ((read = fileInputStream
											.read(bufferData)) > 0) {
										os.write(bufferData, 0, read);
									}
									os.close();
									fileInputStream.close();
									response.flushBuffer();

								} else {
									throw new ServletException(
											"File doesn't exists on server.");
								}

							} else {

								zipFolder(directoryPath, directoryPath + ".zip");
								// System.out.println(" zipFolder(directoryPath,directoryPath+--");
								ZIpPath = directoryPath + ".zip";
								// System.out.println(" String ZIpPath=directoryPath+--");

								/* Final Steps to download zip file */

								// for update status and opgm code
								{
									for (Integer lInt = 0; lInt < sevarthid.length; lInt++) {

										sevarthId1 = sevarthid[lInt];
										lObjFormS1.updateOpgmIdFrm(sevarthId1,
												FileNumber);
									}
								}

								try {
									if (ZIpPath == null || ZIpPath.equals("")) {
										throw new ServletException(
												"File Name can't be null or empty");
									}

									File file1 = new File(ZIpPath);
									if (!file1.exists()) {
										throw new ServletException(
												"File doesn't exists on server.");
									}

									response.setContentType("APPLICATION/OCTET-STREAM");
									response.setHeader("Content-Disposition",
											"attachment;filename=" + FileNumber
													+ ".zip");

									FileInputStream fileInputStream = new FileInputStream(
											file1);
									OutputStream os = response
											.getOutputStream();
									byte[] bufferData = new byte[4096];
									int read = 0;
									while ((read = fileInputStream
											.read(bufferData)) > 0) {
										os.write(bufferData, 0, read);
									}
									os.close();
									fileInputStream.close();
									response.flushBuffer();

									/* $t Start push ._verifed file on sftp */
									Session sessionPush = null;
									Channel channelPush = null;
									ChannelSftp channelSftpPush = null;
									FileInputStream instream = null;
									FileOutputStream outstream = null;
									try {
										int year = Calendar.getInstance().get(
												Calendar.YEAR);
										int month = Calendar.getInstance().get(
												Calendar.MONTH) + 1;
										File pushFile = new File(fMainDirectory
												+ "/" + "Upload" + "/"
												+ FileNumber + "_verified.txt");// Upload
										File finalPushFile = new File(
												FileNumber + "_verified.txt");
										instream = new FileInputStream(pushFile);
										outstream = new FileOutputStream(
												finalPushFile);
										byte[] buffer = new byte[1024];
										int length;
										while ((length = instream.read(buffer)) > 0) {
											outstream.write(buffer, 0, length);
										}

										// sftp push for vm jitu
//										 String SFTPHOSTPush =
//										 "100.70.201.169";
//										 int SFTPPORTPush = 22;
//										 String SFTPUSERPush = "mahait";
//										 String SFTPPASSPush = "Mahait@99";
//										 String SFTPWORKINGDIRPush = "/OPGM"
//										 + "/" + month + year;
										// //sftp for live

										String SFTPHOSTPush = "10.34.82.225";
										int SFTPPORTPush = 8888;
										String SFTPUSERPush = "tcsadmin";
										String SFTPPASSPush = "Tcsadmin@123";
										String SFTPWORKINGDIRPush = "/home/OPGM_TEMP"
												+ "/" + month + year;

										// //sftplive
										JSch jschPush = new JSch();
										sessionPush = jschPush.getSession(
												SFTPUSERPush, SFTPHOSTPush,
												SFTPPORTPush);
										sessionPush.setPassword(SFTPPASSPush);
										java.util.Properties config = new java.util.Properties();
										config.put("StrictHostKeyChecking",
												"no");
										sessionPush.setConfig(config);
										sessionPush.connect();
										channelPush = sessionPush
												.openChannel("sftp");
										channelPush.connect();
										channelSftpPush = (ChannelSftp) channelPush;

										String[] folders = SFTPWORKINGDIRPush
												.split("/");
										for (String folder : folders) {
											if (folder.length() > 0) {
												try {
													channelSftpPush.cd(folder);
												} catch (SftpException e) {
													channelSftpPush
															.mkdir(folder);
													channelSftpPush.cd(folder);
												}
											}
										}

										// channelSftpPush.cd(SFTPWORKINGDIRPush);

										channelSftpPush.put(
												new FileInputStream(
														finalPushFile),
												finalPushFile.getName());
										channelSftpPush.exit();

									} catch (Exception e) {
										e.printStackTrace();
									} finally {
										instream.close();
										outstream.close();
										channelSftpPush.disconnect();
										channelPush.disconnect();
										sessionPush.disconnect();
									}
									/* $t End push ._verifed file on sftp */

								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								/* End of Final Steps to download zip file */

								/* Delete ZIP */
								try {
									File DZfile = new File(
											"E:/st/.metadata/.plugins/org.eclipse.wst.server.core/tmp4/wtpwebapps/dcps/"
													+ FileNumber + ".zip");
									if (!DZfile.exists()) {
										throw new ServletException(
												"Zip  doesn't exists on server.");
									}
									DZfile.delete();
								} catch (Exception e) {
									e.printStackTrace();
								}
								/* end Delete ZIP */
								// System.out.print("Directory is not empty");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

						/* end download error file */

					} catch (Exception e) {
						e.printStackTrace();
						// System.out.println(" Error+--"+e);
						gLogger.info("Error+--" + e);
					}
					gLogger.info("All fine 8 is *********");
					resObj.setResultValue(inputMap);
					resObj.setViewName("ExportReportPage");
				}
				// }
			}
		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		} finally {
			con.close();
		}
		return resObj;
	}

	// ///this method we not using instead of we use this
	// rejectEmployeesMoveToDdo()
	public ResultObject rejectEmployeesMoveToDdo1(Map<String, Object> inputMap) {
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

		FormS1DAOImpl lObjFormS1 = new FormS1DAOImpl(null,
				serv.getSessionFactory());
		setSessionInfo(inputMap);

		try {
			String status = "N";

			String sevarthId = StringUtility.getParameter("sevarthId", request);
			String reason = StringUtility.getParameter("reason", request);
			// System.out.println("sevarthId-->" + sevarthId);

			lObjFormS1.rejectEmployeesFromTo(sevarthId);
			status = "Y";

			String lSBStatus = getResponseRejectEmpFromTo(status).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
					lSBStatus.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
		}
		return resObj;
	}

	// $t 2020-2-3
	// for Rejection Of employee
	public ResultObject rejectEmployeesMoveToDdo(Map<String, Object> inputMap) {
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

		FormS1DAOImpl lObjFormS1 = new FormS1DAOImpl(null,
				serv.getSessionFactory());
		setSessionInfo(inputMap);

		try {
			String status = "N";

			String sevarthId = StringUtility.getParameter("sevarthId", request);
			String sevarth_Id[] = sevarthId.split("~");
			String reason = StringUtility.getParameter("Reason", request);
			String reason_Id[] = reason.split("~");
			System.out.println("sevarth_Id-->" + sevarth_Id);
			System.out.println("reason_Id-->" + reason_Id);

			if (sevarth_Id != null && sevarth_Id.length > 0) {
				for (Integer lInt = 0; lInt < sevarth_Id.length; lInt++) {
					lObjFormS1.rejectEmployeesFromTo(sevarth_Id[lInt],
							reason_Id[lInt]);
					status = "Y";
				}
				String lSBStatus = getResponseRejectEmpFromTo(status)
						.toString();
				String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
						lSBStatus.toString()).toString();
				inputMap.put("ajaxKey", lStrResult);
				resObj.setViewName("ajaxData");
				resObj.setResultValue(inputMap);
			}
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
		}
		return resObj;
	}

	private static BufferedImage resize(BufferedImage img, int height, int width) {
		Image tmp = img.getScaledInstance(width, height, Image.SCALE_FAST);
		BufferedImage resized = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB); // TYPE_USHORT_555_RGB // for no
												// color TYPE_INT_ARGB //
												// working fine
												// TYPE_USHORT_565_RGB
		Graphics2D g2d = resized.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return resized;
	}
//	private static BufferedImage resizenew(File imageFile, int height, int width) {
//		File imageFile = new File(img);
//        File compressedImageFile = new File("myimage_compressed.jpg");
// 
//        InputStream is = new FileInputStream(imageFile);
//        OutputStream os = new FileOutputStream(compressedImageFile);
// 
//        float quality = 0.5f;
// 
//        // create a BufferedImage as the result of decoding the supplied InputStream
//        BufferedImage image = ImageIO.read(is);
// 
//        // get all image writers for JPG format
//        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
// 
//        if (!writers.hasNext())
//            throw new IllegalStateException("No writers found");
// 
//        ImageWriter writer = (ImageWriter) writers.next();
//        ImageOutputStream ios = ImageIO.createImageOutputStream(os);
//        writer.setOutput(ios);
// 
//        ImageWriteParam param = writer.getDefaultWriteParam();
// 
//        // compress to a given quality
//        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
//        param.setCompressionQuality(quality);
// 
//		return null;
//	}
	static public void zipFolder(String srcFolder, String destZipFile)
			throws Exception {
		ZipOutputStream zip = null;
		FileOutputStream fileWriter = null;
		fileWriter = new FileOutputStream(destZipFile);
		zip = new ZipOutputStream(fileWriter);
		addFolderToZip("", srcFolder, zip);
		zip.flush();
		zip.close();
	}

	static private void addFileToZip(String path, String srcFile,
			ZipOutputStream zip) throws Exception {
		File folder = new File(srcFile);
		if (folder.isDirectory()) {
			addFolderToZip(path, srcFile, zip);
		} else {
			byte[] buf = new byte[1024];
			int len;
			FileInputStream in = new FileInputStream(srcFile);
			zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
			while ((len = in.read(buf)) > 0) {
				zip.write(buf, 0, len);
			}
		}
	}

	static private void addFolderToZip(String path, String srcFolder,
			ZipOutputStream zip) throws Exception {
		File folder = new File(srcFolder);

		for (String fileName : folder.list()) {
			if (path.equals("")) {
				addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
			} else {
				addFileToZip(path + "/" + folder.getName(), srcFolder + "/"
						+ fileName, zip);
			}
		}
	}

	//
	// public ResultObject createTextFilesForCSRF(Map inputMap) throws
	// JSchException {
	//
	// ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
	// String lStrTempFromDate = null;
	// String lStrTempToDate = null;
	// String lStrFromDate = null;
	// String lStrToDate = null;
	// Date lDateFromDate = null;
	// Date lDateToDate = null;
	// List lListTotalDdowiseEntries = null;
	// Long yearId = null;
	// Long monthId = null;
	// Long lLongEmployeeAmt = 0L;
	// Long lLongEmployerAmt = 0L;
	// Long TotalAmt = 0L;
	// String BatchId = null;
	// String dhDtls = "";
	// // String AssociatedDtoRegNo ="";
	// String ddoRegNo = "";
	// String sevarthId1 = "";
	// String lStrFileName = "Test1";
	// String path = "";
	// String fileName = "";
	// int fcount = 1;
	// int dcount = 1;
	//
	// String SFTPHOST= "100.70.201.169";
	// int SFTPPORT = 22;
	// String SFTPUSER = "mahait";
	// String SFTPPASS = "Mahait@99";
	// String SFTPWORKINGDIR = "/upload/Testing";
	// Session session = null;
	// Channel channel = null;
	// ChannelSftp channelSftp = null;
	// JSch jsch = new JSch();
	// session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
	// session.setPassword(SFTPPASS);
	// java.util.Properties config = new java.util.Properties();
	// config.put("StrictHostKeyChecking", "no");
	// session.setConfig(config);
	// session.connect();
	// channel = session.openChannel("sftp");
	// channel.connect();
	// channelSftp = (ChannelSftp) channel;
	//
	//
	// try {
	// setSessionInfo(inputMap);
	// {
	// FormS1DAOImpl lObjFormS1 = new FormS1DAOImpl(null,
	// serv.getSessionFactory());
	// String dtoRegNo = "";
	// StringBuffer sb = new StringBuffer();
	// PrintWriter outputfile = response.getWriter();
	// int count = 1;
	// // sb.append("1^FH^P^"+dtoRegNo+"^1^^^^^^^");
	// gLogger.info("All fine 1 is *********");
	// // for multiple sevarth id
	//
	// String sevarthId = StringUtility.getParameter("sevarthId",
	// request);
	// String sevarthid[] = sevarthId.split("~");
	// System.out.println("SevarthId-->" + sevarthId);
	//
	// if (sevarthid != null && sevarthid.length > 0)
	//
	// {
	// // Date lDtCurrDate = SessionHelper.getCurDate();
	// Date date = new Date();
	// SimpleDateFormat formatter = new SimpleDateFormat(
	// "MMddyyyy");
	// Object strDate = formatter.format(date);
	//
	// DecimalFormat df = new DecimalFormat("000000");
	//
	// String c = df.format(9);
	//
	// sb.append("000001^FH^PRAN^R^" + strDate + "^001^"
	// + df.format(sevarthid.length) + "^NCRA"); // header
	// // String
	// sb.append("\r\n");
	// DecimalFormat df1 = new DecimalFormat("000000");
	//
	// for (Integer lInt = 0; lInt < sevarthid.length; lInt++) {
	//
	// sevarthId1 = sevarthid[lInt];
	// // $t
	// List fdDtls = lObjFormS1.getFDDtls(sevarthId1);
	// List ndDtls1 = lObjFormS1.getNDDtls1(sevarthId1);
	// List ndDtls2 = lObjFormS1.getNDDtls2(sevarthId1);
	// List ndDtls3 = lObjFormS1.getNDDtls3(sevarthId1);
	// List ddDtls = lObjFormS1.getDDdtls(sevarthId1);
	//
	// List AssoDtoRegNo = lObjFormS1
	// .getAssoDtoRegNo(sevarthId1);
	//
	// // if(!AssoDtoRegNo.get(0).equals("")){
	// // AssDtoRegNo= (Object[]) AssoDtoRegNo.get(0);
	//
	// // for family details
	// // '^^'||(reg.ASSOCIATED_DTO_REG_NO||'100'||'0000001')||
	// DecimalFormat df2 = new DecimalFormat("000000");
	// DecimalFormat df3 = new DecimalFormat("0000000");
	// Iterator iterator = fdDtls.iterator();
	// // int fcount=1;
	// while (iterator.hasNext()) {
	// sb.append(df1.format(++count) + "^FD^N^"
	// + df2.format(fcount) + "^^"
	// + AssoDtoRegNo.get(0) + "100"
	// + df3.format(dcount) + iterator.next()); // header
	// // String
	// sb.append("\r\n");
	// }
	// // }
	// // for first nominee
	// if (ndDtls1.size() > 0) {
	// Iterator iterator1 = ndDtls1.iterator();
	// while (iterator1.hasNext()) {
	// sb.append(df1.format(++count) + "^ND^N^"
	// + iterator1.next()); // header String
	// sb.append("\r\n");
	// }
	// }
	// // for second nominee
	//
	// if (ndDtls2.size() > 0) {
	// Iterator iterator1 = ndDtls2.iterator();
	// while (iterator1.hasNext()) {
	// sb.append(df1.format(++count) + "^ND^N^"
	// + iterator1.next()); // header String
	// sb.append("\r\n");
	// }
	// }
	// // for third nominee
	// if (ndDtls3.size() > 0) {
	// Iterator iterator1 = ndDtls3.iterator();
	// while (iterator1.hasNext()) {
	// sb.append(df1.format(++count) + "^ND^N^"
	// + iterator1.next()); // header String
	// sb.append("\r\n");
	// }
	// }
	//
	// // for dd details
	// Iterator iterator2 = ddDtls.iterator();
	// while (iterator2.hasNext()) {
	// sb.append(df1.format(++count) + "^DD^N^"
	// + iterator2.next()); // header String
	// sb.append("\r\n");
	// }
	//
	// // for image code
	// try {
	// fileName = "Downloaded" + ".txt";
	// response.setContentType("text/plain;charset=UTF-8");
	// response.addHeader("Content-disposition","attachment; filename=" +
	// fileName);
	// response.setCharacterEncoding("UTF-8");
	//
	// Class.forName("com.ibm.db2.jcc.DB2Driver");
	// Connection con =
	// DriverManager.getConnection("jdbc:db2://100.70.201.168:50000/IFMSMIGR","ifms",
	// "Mahait#99");
	// PreparedStatement ps =
	// con.prepareStatement("select doc.final_attachment,SEVARTH_ID from CMN_ATTACHMENT_MPG mpg  join MST_DCPS_EMP emp on emp.SEVARTH_ID ='"+
	// sevarthId1
	// + "' and emp.PHOTO_ATTACHMENTID=mpg.ATTACHMENT_ID "
	// + " join CMN_ATTDOC_MST doc on doc.SR_NO=mpg.SR_NO");
	// ResultSet rs = ps.executeQuery();
	// String createFolder = "sample";
	// String photo = "sample_photo";
	// path = "F:\\" + createFolder;
	// System.out.println("path done " + path);
	// if (rs.next()) {// now on 1st row
	// File f = new File(path);
	// System.out.println("f: " + f);
	// if (!f.exists())
	// f.mkdirs();
	// String path1 = path + "\\" + photo;
	// System.out.println("path1: " + path1);
	// File f1 = new File(path1);
	// System.out.println("f1: " + f1);
	// if (!f1.exists())
	// f1.mkdirs();
	// Blob b = rs.getBlob(1);// 1 means 1nd column
	// byte barr[] = b.getBytes(1, (int) b.length());// 1
	//
	// String jpg = "_photo.jpg";
	//
	// FileOutputStream fout1 = new FileOutputStream(
	// path1 + "\\" + AssoDtoRegNo.get(0)
	// + "100" + df3.format(dcount)
	// + jpg);
	// fout1.write(barr);
	// fout1.close();
	//
	// File input = new File(path1 + "\\"
	// + AssoDtoRegNo.get(0) + "100"
	// + df3.format(dcount) + jpg);
	// BufferedImage image = ImageIO.read(input);
	//
	// Iterator<ImageWriter> writers = ImageIO
	// .getImageWritersByFormatName("jpg");
	// ImageWriter writer = (ImageWriter) writers
	// .next();
	//
	// ImageWriteParam param = writer
	// .getDefaultWriteParam();
	//
	// param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
	// param.setCompressionQuality(0.05f);
	// // writer.write(null, new IIOImage(image, null,null), param);
	//
	// }
	// String[] folders = SFTPWORKINGDIR.split( "/" );
	// // for ( String folder : folders ) {
	// // if ( folder.length() > 0 ) {
	// // try {
	// // channelSftp.cd( folder );
	// // }
	// // catch ( SftpException e ) {
	// // channelSftp.mkdir( folder );
	// // channelSftp.cd( folder );
	// // }
	// // }
	// // }
	// //ended
	// channelSftp.cd(SFTPWORKINGDIR);
	// System.out.println("ok");
	// con.close();
	// }
	// catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// gLogger.info("All fine 7 is *********");
	// if (outputfile != null)
	// outputfile.close();
	// }
	// //for count increment purpose
	// fcount++;
	// dcount++;
	// }
	// // for path sample folder code is below
	// PrintWriter fout = new PrintWriter(path + "\\"
	// + "sample.txt");
	// fout.write(sb.toString());// appends the string to the file
	// fout.close();
	// // for download file
	// outputfile.write(sb.toString());
	// outputfile.flush();
	//
	//
	// // String authNo1 = "Dnyanesh";
	// // String fileName1=authNo1+".txt";
	// //
	// // String content="hi this is forst time i am doing this";
	// //
	//
	// final OutputStream os = new FileOutputStream(fileName);
	// final PrintStream printStream = new PrintStream(os);
	// printStream.print(sb.toString());
	// printStream.close();
	// File f1 = new File(fileName.toString());
	// channelSftp.put(new FileInputStream(f1), f1.getName());
	// channelSftp.exit();
	// channel.disconnect();
	// session.disconnect();
	// }
	// }
	// gLogger.info("All fine 8 is *********");
	// resObj.setResultValue(inputMap);
	// resObj.setViewName("ExportReportPage");
	// } catch (Exception e) {
	// e.printStackTrace();
	// gLogger.error(" Error is : " + e, e);
	// resObj.setResultValue(null);
	// resObj.setThrowable(e);
	// resObj.setResultCode(ErrorConstants.ERROR);
	// resObj.setViewName("errorPage");
	// }
	//
	// return resObj;
	// }
	//
	/* LoadDeleteFormOpgm */
	public ResultObject LoadDeleteFormOpgm(Map inputMap) throws Exception {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		try {
			setSessionInfo(inputMap);
			String sevarthId = StringUtility.getParameter("sevarthId", request);
			inputMap.put("sevarthId", sevarthId);
			resObj.setResultValue(inputMap);
			resObj.setViewName("RejectReasonOPGM");
		} catch (Exception e) {
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;

	}

	/* END */

	public ResultObject chkFrmUpdatedByLgnDdo(Map inputMap) throws Exception {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List lstEmpForFrmS1Edit = null;
		List empDesigList = null;
		String strDDOCode = null;
		String txtSearch = null;
		String flag = null;
		String sevarthId = null;
		String IsDeputation = null;
		int DepSize = 100;
		String CheckFormUpdatedByLgnDDO = null;
		String lSBStatus = null;

		try {
			setSessionInfo(inputMap);
			DcpsCommonDAO lObjDcpsCommonDao = new DcpsCommonDAOImpl(null,
					serv.getSessionFactory());
			FormS1DAO lObjSearchEmployeeDAO = new FormS1DAOImpl(MstEmp.class,
					serv.getSessionFactory());
			strDDOCode = lObjDcpsCommonDao.getDdoCodeForDDO(gLngPostId);
			gLogger.info("logged in ddo code: " + strDDOCode);
			txtSearch = StringUtility.getParameter("searchTxt", request);

			CheckFormUpdatedByLgnDDO = lObjSearchEmployeeDAO
					.chkFrmUpdatedByLgnDdo(txtSearch);
			if (CheckFormUpdatedByLgnDDO.equals("Z")) {
				lSBStatus = getResponseUpdatedDdoCode("Z").toString();
			} else if (CheckFormUpdatedByLgnDDO.equals("S")) {
				lSBStatus = getResponseUpdatedDdoCode("S").toString();
			} else {
				if (CheckFormUpdatedByLgnDDO.equals(strDDOCode)) {
					lSBStatus = getResponseUpdatedDdoCode("Y").toString();
				} else {
					lSBStatus = getResponseUpdatedDdoCode(
							CheckFormUpdatedByLgnDDO).toString();
				}
			}

			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
					lSBStatus).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");

			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;

	}

	private StringBuilder getResponseUpdatedDdoCode(String flag) {
		gLogger.info("Flag is  in AJAX********" + flag);
		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");

		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	public void CallJarFromOutside(String[] args, HttpServletRequest request2)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, ClassNotFoundException,
			NoSuchMethodException, SecurityException, IOException {
		/*
		 * String parentDirectoryPath =
		 * request.getSession().getServletContext().
		 * getRealPath("/images/2.61NoAADHAAR.jar"); //
		 * "jar:file:/E:/DNYANESH/JAR/2.61NoAADHAAR.jar!/"; final String JAR_URL
		 * = "jar:file:/"+parentDirectoryPath+"!/";
		 * System.out.println("-->"+JAR_URL); final String JAR_FILE_PATH =
		 * "file:/"+parentDirectoryPath; URLClassLoader urlClassLoader;
		 */

		// imp jar:http://www.abcd.com/networking.jar!/
//		String parentDirectoryPath = request.getSession().getServletContext()
//				.getRealPath("/images/SubscriberRegistrationFVU3.jar");
//		 String parentDirectoryPath =
//		 "http://100.70.201.169:8080/ifmsmaha/images/SubscriberRegistrationFVU3.jar";
		// // for
		// vm
		 String parentDirectoryPath =
		 "http://10.34.82.167:8580/IFMS-GPFLNA_live/images/SubscriberRegistrationFVU3.jar";
//		 for preprod

//		 String parentDirectoryPath =
//		 "https://sevaarth.mahakosh.gov.in/images/SubscriberRegistrationFVU3.jar"; //for
		// preprod(live)
		// String parentDirectoryPath =
		// request.getSession().getServletContext().getRealPath("/images/SubscriberRegistrationFVU3.jar");

		System.out.println("parent Path-->" + parentDirectoryPath);
		// "jar:file:/E:/DNYANESH/JAR/2.61NoAADHAAR.jar!/";
		final String JAR_URL = "jar:" + parentDirectoryPath + "!/";
		System.out.println("-->" + JAR_URL);
		final String JAR_FILE_PATH = parentDirectoryPath;
		System.out.println("JAR_FILE_PATH-->" + JAR_FILE_PATH);
		URLClassLoader urlClassLoader;

		try {

			// Create a URL that refers to a jar file in the file system
			URL FileSysUrl = new URL(JAR_URL);

			// Create a jar URL connection object
			JarURLConnection jarURLConnection = (JarURLConnection) FileSysUrl
					.openConnection();

			// Get the jar file
			JarFile jarFile = jarURLConnection.getJarFile();

			// Get jar file name
			System.out.println("Jar Name: " + jarFile.getName());

			// When no entry is specified on the URL, the entry name is null
			System.out
					.println("\nJar Entry: " + jarURLConnection.getJarEntry());

			// Get the manifest of the jar
			Manifest manifest = jarFile.getManifest();

			// Print the manifest attributes
			System.out.println("\nManifest file attributes: ");
			for (Entry entry : manifest.getMainAttributes().entrySet()) {
				System.out.println(entry.getKey() + ": " + entry.getValue());
			}
			System.out.println("\nExternal JAR Execution output: ");

			// Get the jar URL which contains target class
			URL[] classLoaderUrls = new URL[] { new URL(JAR_FILE_PATH) };

			// Create a classloader and load the entry point class
			urlClassLoader = new URLClassLoader(classLoaderUrls);

			// Get the main class name (the entry point class)
			String mainClassName = manifest.getMainAttributes().getValue(
					Attributes.Name.MAIN_CLASS);

			// Load the target class
			Class beanClass = urlClassLoader.loadClass(mainClassName);

			// Get the main method from the loaded class and invoke it
			Method method = beanClass.getMethod("main", String[].class);

			// init params accordingly
			String[] params = args;

			// static method doesn't have an instance
			method.invoke(null, (Object) params);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// //$t 2020-2-3
	private StringBuilder getResponseRejectEmpFromTo(String flag) {
		gLogger.info("Flag is  in AJAX********" + flag);
		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");

		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	public ResultObject updateIsPrintCsrf(Map<String, Object> inputMap) {
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

		FormS1DAOImpl lObjFormS1 = new FormS1DAOImpl(null,
				serv.getSessionFactory());
		setSessionInfo(inputMap);
		try {
			String status = "Y";
			String EmpFirstName = StringUtility.getParameter("EmpFirstName",
					request);
			String EmpFatherName = StringUtility.getParameter("EmpFatherName",
					request);
			String Nmn1FirstName = StringUtility.getParameter("Nmn1FirstName",
					request);
			String TinPan = StringUtility.getParameter("TinPan", request);
			// gLogger.info("EmpFirstName***"+EmpFirstName);
			// gLogger.info("EmpFatherName***"+EmpFatherName);
			// gLogger.info("Nmn1FirstName***"+Nmn1FirstName);
			// gLogger.info("Nmn1Relation***"+Nmn1Relation);

			lObjFormS1.updateIsPrintCsrfFlag(EmpFirstName, EmpFatherName,
					Nmn1FirstName, TinPan);

			String lSBStatus = getResponseRejectEmpFromTo(status).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
					lSBStatus.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			e.printStackTrace();
			resObj.setResultValue(null);
		}
		return resObj;
	}

}// end class

