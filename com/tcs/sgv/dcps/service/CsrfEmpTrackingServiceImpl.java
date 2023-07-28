package com.tcs.sgv.dcps.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.ws.BindingProvider;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import bds.authorization.MapConverter;
import bds.authorization.PayrollBEAMSIntegrateWS;

import com.cra.pao.vo.PAOContrErrorFileVO;
import com.cra.stp.core.webservice.PerformFileUpload;
import com.cra.stp.core.webservice.STPWebServicePOJO;
import com.cra.stp.core.webservice.STPWebServicePOJOService;
import com.ibm.icu.util.StringTokenizer;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.ncode.ctrl.pki.PKICtrl;
import com.tcs.sgv.common.dao.FinancialYearDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.util.EnglishDecimalFormat;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.common.valueobject.TrnNPSBeamsIntegration;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.CsrfEmpTrackingDAOImpl;
import com.tcs.sgv.dcps.dao.NpsPendencyTrackingDAOImpl;
import com.tcs.sgv.dcps.dao.NsdlNpsDAOImpl;
import com.tcs.sgv.dcps.dao.NsdlSrkaFileGeneDAOImpl;
import com.tcs.sgv.eis.service.IdGenerator;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import cra.standalone.paosubcontr.PAOFvu;

public class CsrfEmpTrackingServiceImpl extends ServiceImpl {

	/* Global Variable for Logger Class */
	private final Log gLogger = LogFactory.getLog(getClass());

	private String gStrPostId = null; /* STRING POST ID */

	private String gStrLocationCode = null;

	private Long gLngPostId = null;

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	private HttpSession session = null; /* SESSION */

	private Date gDtCurDate = null; /* CURRENT DATE */

	private String gStrUserId = null; /* STRING USER ID */

	private HttpServletResponse response = null;/* RESPONSE OBJECT */

	/* Global Variable for UserId */
	Long gLngUserId = null;

	private void setSessionInfo(Map inputMap) {

		try {
			response = (HttpServletResponse) inputMap.get("responseObj");
			request = (HttpServletRequest) inputMap.get("requestObj");
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			session = request.getSession();
			gStrPostId = SessionHelper.getPostId(inputMap).toString();
			gLngPostId = SessionHelper.getPostId(inputMap);
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
			gLngUserId = SessionHelper.getUserId(inputMap);
			gStrUserId = gLngUserId.toString();
			gDtCurDate = SessionHelper.getCurDate();
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
		}
	}

	public ResultObject loadNPSNSDLForm(Map inputMap) {

		ResultObject resObj = new ResultObject(0, "FAIL");
		String lStrTempFromDate = null;
		try {
			setSessionInfo(inputMap);
		    CsrfEmpTrackingDAOImpl lObjNsdlDAO = new CsrfEmpTrackingDAOImpl(null,this.serv.getSessionFactory());
			
		    resObj.setResultValue(inputMap);
			resObj.setViewName("CsrfEmpTracking");
		} catch (Exception e) {
			e.printStackTrace();
			this.gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(-1);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}
}
