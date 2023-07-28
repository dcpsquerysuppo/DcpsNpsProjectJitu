/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 11, 2011		Shivani Rana								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.service;

//com.tcs.sgv.pensionpay.service.ReadExcel

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.util.ExcelParser;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttachmentMstDAO;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttachmentMstDAOImpl;
import com.tcs.sgv.common.valueobject.CmnAttachmentMpg;
import com.tcs.sgv.common.valueobject.CmnAttachmentMst;
import com.tcs.sgv.common.valueobject.CmnAttdocMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAOImpl;
import com.tcs.sgv.pensionpay.dao.ReadExcelDAO;
import com.tcs.sgv.pensionpay.dao.ReadExcelDAOImpl;
import com.tcs.sgv.pensionpay.dao.StgDeliveryDtlsDAO;
import com.tcs.sgv.pensionpay.dao.StgDeliveryDtlsDAOImpl;
import com.tcs.sgv.pensionpay.dao.StgTablesDAO;
import com.tcs.sgv.pensionpay.dao.StgTablesDAOImpl;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerHdr;
import com.tcs.sgv.pensionpay.valueobject.StgDeliveryDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionerRivisionDtls;


/**
 * Class Description -
 * 
 * 
 * @author Shivani Rana
 * @version 0.1
 * @since JDK 5.0 Mar 11, 2011
 */

public class ReadExcel extends ServiceImpl {

	private ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	private Log gLogger = LogFactory.getLog(getClass());

	/* Global Variable for PostId */
	private Long gLngPostId = null;
	
	private Long gLngUserId = null;
	
	private Date gCurrDate = null;

	/* Global Variable for User Loc Map */
	static HashMap sMapUserLoc = new HashMap();

	private String gStrLocCode = null;

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	/*
	 * Function to save the session specific details
	 */

	private Thread gObjThread = null;

	private Long gDelvId = null;

	public ReadExcel() {

		gLogger.info("============In ReadExcel Constructor===========");
	}

	private void setSessionInfo(Map inputMap) throws Exception {

		try {

			gLngPostId = SessionHelper.getPostId(inputMap);
			serv = new ServiceLocator();
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gStrLocCode = SessionHelper.getLocationCode(inputMap);
			gLngUserId = SessionHelper.getUserId(inputMap);
			gCurrDate = DBUtility.getCurrentDateFromDB();
		} catch (Exception e) {
			throw (e);
		}

	}

	public ResultObject loadAttachAgData(Map<Object, Object> inputMap) {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List<StgDeliveryDtls> lLstStgDeliveryDtls = null;
		int totalRecords = 0;
		String lStrDelvStatus = null;
		ReadExcelDAO lObjReadExcelDAO = null;
		StgDeliveryDtlsDAO lObjStgDeliveryDtlsDAOImpl = null;
		StgTablesDAO lObjStgTablesDAO = new StgTablesDAOImpl();
		try {
			setSessionInfo(inputMap);
			lObjReadExcelDAO = new ReadExcelDAOImpl(serv.getSessionFactory());
			lObjStgDeliveryDtlsDAOImpl = new StgDeliveryDtlsDAOImpl(StgDeliveryDtls.class, serv.getSessionFactory());
			lLstStgDeliveryDtls = new ArrayList<StgDeliveryDtls>();
			lStrDelvStatus = (String) inputMap.get("delvStatus");
			// -----Setting all delivered file status to Failed.
			if ((bundleConst.getString("DELVSTATUS.FAILED")).equals(lStrDelvStatus)) {
				if (gDelvId != null) {
					lObjStgTablesDAO.updateDelvStatusToFailed(gDelvId, serv.getSessionFactory().getCurrentSession());
				}
			}
			Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);

			totalRecords = lObjStgDeliveryDtlsDAOImpl.getStgDeliveryDtlsCount(gStrLocCode);
			if (totalRecords > 0) {
				lLstStgDeliveryDtls = lObjStgDeliveryDtlsDAOImpl.getStgDeliveryDtls(displayTag, gStrLocCode);
			}
			if (lLstStgDeliveryDtls != null && lLstStgDeliveryDtls.size() > 0) {
				inputMap.put("upldFlag", "Y");
				inputMap.put("upldedFiles", lLstStgDeliveryDtls);
			} else {
				inputMap.put("upldFlag", "N");
			}
			inputMap.put("totalRecords", totalRecords);
			objRes.setViewName("uploadExcel");
			objRes.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}

		return objRes;

	}

	public ResultObject getDataFromXLS(Map<Object, Object> inputMap) {

		Object[][] xlsData = null;
		StgDeliveryDtls lObjStgDeliveryDtls = null;
		String lStrTableName = null;
		Long lLngDeliveryType = null;
		Long lLngDelvId = null;
		Long lLngFileId = null;
		StringBuilder lStrBldXML = null;
		List<StgDeliveryDtls> lLstStgDeliveryDtls = null;
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		try {
			setSessionInfo(inputMap);
			HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
			ReadExcelDAO lObjReadExcelDAO = new ReadExcelDAOImpl(serv.getSessionFactory());
			StgDeliveryDtlsDAO lObjStgDeliveryDtlsDAO = new StgDeliveryDtlsDAOImpl(StgDeliveryDtls.class, serv.getSessionFactory());

			String lStrAttachedFileNames = StringUtility.getParameter("attachedFileNames", request);
			String[] lArrStrFileNames = lStrAttachedFileNames.split(",");
			inputMap.put("lLngPostId", gLngPostId);

			// Code For Attachement starts
			Long lObjAttachmentId = null;
			Map attachMap = new HashMap();

			objRes = serv.executeService("FILE_UPLOAD_VOGEN", inputMap);
			objRes = serv.executeService("FILE_UPLOAD_SRVC", inputMap);

			attachMap = (Map) objRes.getResultValue();

			lObjAttachmentId = null;
			if (attachMap != null) {
				if (attachMap.get("AttachmentId_scan") != null) {
					lObjAttachmentId = Long.parseLong(String.valueOf(attachMap.get("AttachmentId_scan")));

				}
				if (lObjAttachmentId != null) {
					CmnAttachmentMst attachmentMst = new CmnAttachmentMst();
					attachmentMst.setAttachmentId(lObjAttachmentId);
				}
				attachMap.get("attachmentNames");
			}

			// Code For Attachement ends

			if (lObjAttachmentId != null) {
				CmnAttachmentMstDAOImpl mnAttachmentMstDAO = new CmnAttachmentMstDAOImpl(CmnAttachmentMst.class, serv.getSessionFactory());
				CmnAttachmentMst cmnAttachmentMst = mnAttachmentMstDAO.findByAttachmentId(lObjAttachmentId);
				Iterator lObjIterator = cmnAttachmentMst.getCmnAttachmentMpgs().iterator();

				// --------Generating one delivery id for all files uploaded at
				// a time
				lLngDelvId = IFMSCommonServiceImpl.getNextSeqNum("stg_delivery_dtls", inputMap);
				// System.out.println("delievery id = "+lLngDelvId);
				inputMap.put("lLngDelvId", lLngDelvId);
				gDelvId = lLngDelvId;
				Iterator itr;
				Integer lIntCnt = 0;
				while (lObjIterator != null && lObjIterator.hasNext()) {
					CmnAttachmentMpg cmnAttachmentMpg = (CmnAttachmentMpg) lObjIterator.next();
					CmnAttdocMst cmnAttDocMst = (CmnAttdocMst) cmnAttachmentMpg.getCmnAttdocMsts().iterator().next();

					String lStrFileName = cmnAttachmentMpg.getOrgFileName().trim();
					int lIntDotPos = lStrFileName.lastIndexOf(".");
					String lStrExtension = lStrFileName.substring(lIntDotPos);
					Integer lIntRowSize = 0;
					if (cmnAttDocMst != null) {

						if (".txt".equals(lStrExtension)) {
							String lStrFilePath = "";
							for (int lIntFileCnt = 0; lIntFileCnt < lArrStrFileNames.length; lIntFileCnt++) {
								Integer result = lArrStrFileNames[lIntFileCnt].lastIndexOf("\\");
								String lStrFile = lArrStrFileNames[lIntFileCnt].substring(result + 1);
								if (lStrFile.equalsIgnoreCase(lStrFileName)) {
									lStrFilePath = lArrStrFileNames[lIntFileCnt];
									break;
								}
							}

							File file = new File(lStrFilePath);
							FileInputStream fstream = new FileInputStream(file);
							DataInputStream in = new DataInputStream(fstream);
							BufferedReader br = new BufferedReader(new InputStreamReader(in));
							String strLine;
							ArrayList list = new ArrayList();

							while ((strLine = br.readLine()) != null) {
								list.add(strLine);
							}
							xlsData = new Object[list.size()][];
							lIntRowSize = list.size();
							for (int i = 0; i < list.size(); ++i) {

								String str = list.get(i).toString();
								Integer lIntRow = str.split("\\|", -1).length;
								String[] splitSt = new String[lIntRow];
								splitSt = str.split("\\|", -1);

								xlsData[i] = splitSt;

							}
							inputMap.put("FileType", bundleConst.getString("FILETYPE.TEXT"));
						} else {
							List lObjSheetLst = ExcelParser.parseExcel(new ByteArrayInputStream(cmnAttDocMst.getFinalAttachment()));

							if (lObjSheetLst != null && !lObjSheetLst.isEmpty()) {
								List lObjRowLst = (List) lObjSheetLst.get(0);
								if (lObjRowLst != null && !lObjRowLst.isEmpty()) {
									lIntRowSize = lObjRowLst.size();
									xlsData = new Object[lObjRowLst.size()][];
									for (int i = 0; i < lObjRowLst.size(); ++i) {
										xlsData[i] = ((List) lObjRowLst.get(i)).toArray();
									}
								}
							}
							inputMap.put("FileType", bundleConst.getString("FILETYPE.EXCEL"));
						}
						// String lStrFileName =
						// cmnAttachmentMpg.getOrgFileName().trim();
						getDelvTypeFromFileName(lStrFileName, inputMap);
						lLngDeliveryType = (Long) inputMap.get("lDeliveryType");

						// -----------Insertion of entry in
						// stg_delivery_dtls table starts <<<<<<<<<<<<<<<<

						lObjStgDeliveryDtls = new StgDeliveryDtls();
						lLngFileId = IFMSCommonServiceImpl.getNextSeqNum("stg_file_dtls", inputMap);
						lObjStgDeliveryDtls.setFileId(lLngFileId);
						lObjStgDeliveryDtls.setDelvId(lLngDelvId);
						lObjStgDeliveryDtls.setDelvType(lLngDeliveryType);
						lObjStgDeliveryDtls.setUploadBy(gLngPostId);
						lObjStgDeliveryDtls.setUploadDate(DBUtility.getCurrentDateFromDB());
						lObjStgDeliveryDtls.setDelvStatus(bundleConst.getString("DELVSTATUS.INPROGRESS"));
						lObjStgDeliveryDtls.setFileAttachmentId(lObjAttachmentId);
						lObjStgDeliveryDtls.setLocationCode(gStrLocCode);
						lObjStgDeliveryDtlsDAO.create(lObjStgDeliveryDtls);

						// -----------Inserting entry in stg_delivery_dtls
						// table ends >>>>>>>>>>>>>>>>>>>>>>>
						lObjReadExcelDAO.insertIntoTable(xlsData, lIntRowSize, lStrFileName, inputMap);
					}
				}

				Runnable runnable = new StgToMasterServiceImpl(serv.getSessionFactory(), lLngDelvId, inputMap);
				gObjThread = new Thread(runnable, "PnsnPmnt Thread");
				gObjThread.start();
			}
			// using ajax
			/*
			 * lStrBldXML = getResponseXMLDoc(inputMap); String lStrResult = new
			 * AjaxXmlBuilder().addItem("ajax_key",
			 * lStrBldXML.toString()).toString(); inputMap.put("ajaxKey",
			 * lStrResult);
			 */

			inputMap.put("delvStatus", bundleConst.getString("DELVSTATUS.SUCCESS"));
			/*
			 * StgDeliveryDtlsDAO lObjStgDeliveryDtlsDAOImpl = new
			 * StgDeliveryDtlsDAOImpl( StgDeliveryDtls.class,
			 * serv.getSessionFactory()); lLstStgDeliveryDtls = new
			 * ArrayList<StgDeliveryDtls>(); lLstStgDeliveryDtls =
			 * lObjStgDeliveryDtlsDAOImpl .getStgDeliveryDtls(); if
			 * (lLstStgDeliveryDtls != null && lLstStgDeliveryDtls.size() > 0) {
			 * inputMap.put("upldFlag", "Y"); inputMap.put("upldedFiles",
			 * lLstStgDeliveryDtls); } else { inputMap.put("upldFlag", "N"); }
			 * objRes.setViewName("uploadExcel");
			 * objRes.setResultValue(inputMap);
			 */
		} catch (Exception e) {
			inputMap.put("delvStatus", bundleConst.getString("DELVSTATUS.FAILED"));
			gLogger.error(" Error is : " + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		objRes = loadAttachAgData(inputMap);
		return objRes;
	}

	private static List getDocumentForXLS(CmnAttdocMst cmnAttDocMst, HttpServletRequest request) throws Exception {

		List lLstReturn = null;
		File lObjFile = null;

		try {

			/** Getting path of temp directory in server. **/
			String serverPathStr = request.getSession().getServletContext().getRealPath("UPLOADED-FILES");

			lObjFile = new File(serverPathStr);
			if (!lObjFile.exists()) {
				lObjFile.mkdir();
			}

			String tempFilePath = serverPathStr + (lObjFile.separator.equals("\\") ? ("\\tempFile_") : "tempFile_") + System.currentTimeMillis();
			/** Made one file with name tempFile_currentTimeMillis(). **/
			lObjFile = new File(tempFilePath);
			/** Getting file as a output stream. **/
			FileOutputStream lobjFileOutputStream = new FileOutputStream(tempFilePath);
			/** write the attachment blob data into temp file. **/
			lobjFileOutputStream.write(cmnAttDocMst.getFinalAttachment(), 0, cmnAttDocMst.getFinalAttachment().length);
			lobjFileOutputStream.flush();
			/** Close the output stream. **/
			lobjFileOutputStream.close();
			/** Parse your Excel file, it will return list of list. **/
			lLstReturn = ExcelParser.parseExcel(lObjFile);

		} catch (Exception e) {
			throw (e);
		} finally {
			if (lObjFile != null) {
				/** Delete the temp file from temp directory in server. **/
				lObjFile.delete();
			}
		}

		return lLstReturn;
	}

	private void getDelvTypeFromFileName(String lStrFileName, Map inputMap) throws Exception {

		Long lDeliveryType = null;
		if (lStrFileName.startsWith(bundleConst.getString("FILE_CPO_ALLOC"))) {
			lDeliveryType = 140001L;
			inputMap.put("lDeliveryType", lDeliveryType);
		} else if (lStrFileName.startsWith(bundleConst.getString("FILE_CPO"))) {
			lDeliveryType = 140002L;
			inputMap.put("lDeliveryType", lDeliveryType);
		} else if (lStrFileName.startsWith(bundleConst.getString("FILE_GPO_ALLOC"))) {
			lDeliveryType = 140004L;
			inputMap.put("lDeliveryType", lDeliveryType);
		} else if (lStrFileName.startsWith(bundleConst.getString("FILE_PPO_ALLOC"))) {
			lDeliveryType = 140006L;
			inputMap.put("lDeliveryType", lDeliveryType);
		} else if (lStrFileName.startsWith(bundleConst.getString("FILE_FAMILY"))) {
			lDeliveryType = 140003L;
			inputMap.put("lDeliveryType", lDeliveryType);
		} else if (lStrFileName.startsWith(bundleConst.getString("FILE_PENSIONER"))) {
			lDeliveryType = 140005L;
			inputMap.put("lDeliveryType", lDeliveryType);
		} else if (lStrFileName.startsWith(bundleConst.getString("FILE_RECOVERY"))) {
			lDeliveryType = 140007L;
			inputMap.put("lDeliveryType", lDeliveryType);
		}

	}

	public ResultObject viewStgFileErrorDtls(Map<Object, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		List lLstResult = new ArrayList();

		try {
			setSessionInfo(inputMap);
			ReadExcelDAO lObjReadExcelDAO = new ReadExcelDAOImpl(serv.getSessionFactory());
			Map<String, Map<String, List<String>>> lMapFileErrorDtls = new HashMap<String, Map<String, List<String>>>();
			Map<String, List<String>> lMapErrorCodeApplnNo = new HashMap<String, List<String>>();
			List<String> lLstApplnNo = new ArrayList<String>();
			String lStrMainKey = null;

			String lStrDelvId = StringUtility.getParameter("DelvId", request);
			if (!"".equals(lStrDelvId)) {
				lLstResult = lObjReadExcelDAO.getStgFileErrorDtls(Long.parseLong(lStrDelvId.trim()));
			}
			if (!lLstResult.isEmpty()) {

				for (Integer lIntCnt = 0; lIntCnt < lLstResult.size(); lIntCnt++) {
					Object[] obj = (Object[]) lLstResult.get(lIntCnt);
					String lStrFileName = obj[1].toString();
					String lStrErrorCode = obj[2].toString();
					String lStrApplnNo = obj[3].toString();

					if (obj[1] != null) {
						lStrMainKey = obj[1].toString();
					}
					lMapErrorCodeApplnNo = lMapFileErrorDtls.get(lStrMainKey);
					if (lMapErrorCodeApplnNo != null) {
						lLstApplnNo = lMapErrorCodeApplnNo.get(lStrErrorCode);
						if (lLstApplnNo != null) {
							lLstApplnNo.add(lStrApplnNo);
						} else {
							lLstApplnNo = new ArrayList<String>();
							lLstApplnNo.add(lStrApplnNo);
							lMapErrorCodeApplnNo.put(lStrErrorCode, lLstApplnNo);

						}
					} else {
						lMapErrorCodeApplnNo = new HashMap<String, List<String>>();
						lLstApplnNo = new ArrayList<String>();
						lLstApplnNo.add(lStrApplnNo);
						lMapErrorCodeApplnNo.put(lStrErrorCode, lLstApplnNo);
						lMapFileErrorDtls.put(lStrMainKey, lMapErrorCodeApplnNo);

					}
				}
			}
			StringBuffer lSbDisplayString = new StringBuffer();
			for (String mainKey : lMapFileErrorDtls.keySet()) {
				Map<String, List<String>> lMapErrorApplnNo = lMapFileErrorDtls.get(mainKey);
				lSbDisplayString.append("File Name : " + mainKey);
				lSbDisplayString.append("\r\n");
				lSbDisplayString.append("\r\n");
				for (String key : lMapErrorApplnNo.keySet()) {
					boolean lStrWarngFlag = false;
					if (key.equals(bundleConst.getString("WARNGCODE.BASICAMT")) || key.equals(bundleConst.getString("WARNGCODE.FAMILYPNSNR"))
							|| key.equals(bundleConst.getString("WARNGCODE.FP1FP2DATE")) || key.equals(bundleConst.getString("WARNGCODE.SAMEAPPLNOPPONO"))) {
						lStrWarngFlag = true;
					}
					lSbDisplayString.append("\r\n");
					if (lStrWarngFlag) {
						lSbDisplayString.append("Warning : " + getErrorDescFromErrorCode(key));
					} else {
						lSbDisplayString.append("Error : " + getErrorDescFromErrorCode(key));
					}
					lSbDisplayString.append("\r\n");
					lSbDisplayString.append("-------------------------------");
					lSbDisplayString.append("\r\n");
					lSbDisplayString.append("| Sr No. |  Application No.   |");
					lSbDisplayString.append("\r\n");
					lSbDisplayString.append("-------------------------------");
					lSbDisplayString.append("\r\n");
					Integer lIntSrNo = 1;
					List<String> lLstApplicationNo = lMapErrorApplnNo.get(key);
					for (String lStrApplnNo : lLstApplicationNo) {
						lSbDisplayString.append(String.format("%1s", "|"));
						lSbDisplayString.append(String.format("%8s", lIntSrNo));
						lSbDisplayString.append(String.format("%1s", "|"));
						lSbDisplayString.append(String.format("%20s", lStrApplnNo));
						lSbDisplayString.append(String.format("%1s", "|"));
						lIntSrNo = lIntSrNo + 1;
						lSbDisplayString.append("\r\n");
					}

					lSbDisplayString.append("-------------------------------");
					lSbDisplayString.append("\r\n");
				}
				lSbDisplayString.append("\r\n");
			}

			inputMap.put("DisplayString", lSbDisplayString);
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		resObj.setViewName("StgFileErrorDtls");
		return resObj;

	}

	private String getErrorDescFromErrorCode(String lStrErrorCode) throws Exception {

		String lStrErrorDesc = "";
		try {
			if (lStrErrorCode != null && !"".equals(lStrErrorCode)) {
				if (lStrErrorCode.equals(bundleConst.getString("ERRORCODE.NULLPPO"))) {
					lStrErrorDesc = bundleConst.getString("ERRORDESC.NULLPPO");
				} else if (lStrErrorCode.equals(bundleConst.getString("ERRORCODE.DUPLPPOINFILE"))) {
					lStrErrorDesc = bundleConst.getString("ERRORDESC.DUPLPPOINFILE");
				} else if (lStrErrorCode.equals(bundleConst.getString("ERRORCODE.DUPLPPOINDB"))) {
					lStrErrorDesc = bundleConst.getString("ERRORDESC.DUPLPPOINDB");
				} else if (lStrErrorCode.equals(bundleConst.getString("ERRORCODE.LOCCODENULL"))) {
					lStrErrorDesc = bundleConst.getString("ERRORDESC.LOCCODENULL");
				} else if (lStrErrorCode.equals(bundleConst.getString("ERRORCODE.PNSNSTARTDTNULL"))) {
					lStrErrorDesc = bundleConst.getString("ERRORDESC.PNSNSTARTDTNULL");
				} else if (lStrErrorCode.equals(bundleConst.getString("ERRORCODE.LOCCODEAGCIRCLE"))) {
					lStrErrorDesc = bundleConst.getString("ERRORDESC.LOCCODEAGCIRCLE");
				} else if (lStrErrorCode.equals(bundleConst.getString("WARNGCODE.BASICAMT"))) {
					lStrErrorDesc = bundleConst.getString("WARNGDESC.BASICAMT");
				} else if (lStrErrorCode.equals(bundleConst.getString("WARNGCODE.FAMILYPNSNR"))) {
					lStrErrorDesc = bundleConst.getString("WARNGDESC.FAMILYPNSNR");
				} else if (lStrErrorCode.equals(bundleConst.getString("WARNGCODE.FP1FP2DATE"))) {
					lStrErrorDesc = bundleConst.getString("WARNGDESC.FP1FP2DATE");
				} else if (lStrErrorCode.equals(bundleConst.getString("WARNGCODE.SAMEAPPLNOPPONO"))) {
					lStrErrorDesc = bundleConst.getString("WARNGDESC.SAMEAPPLNOPPONO");
				}
			}
		} catch (Exception e) {

			gLogger.error("Error is :" + e, e);
			throw e;
		}
		return lStrErrorDesc;
	}

	public ResultObject viewAttachedFiles(Map<Object, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		List lLstResult = new ArrayList();

		try {
			setSessionInfo(inputMap);
			ReadExcelDAO lObjReadExcelDAO = new ReadExcelDAOImpl(serv.getSessionFactory());

			String lStrFileAttachId = null;
			String lStrDelvId = StringUtility.getParameter("DelvId", request);
			if (!"".equals(lStrDelvId)) {
				lStrFileAttachId = lObjReadExcelDAO.getAttachmentIdFromDelvId(Long.parseLong(lStrDelvId.trim()));
			}

			CmnAttachmentMst lObjCmnAttachmentMst = null;
			CmnAttachmentMstDAO lObjCmnAttachmentMstDAO = new CmnAttachmentMstDAOImpl(CmnAttachmentMst.class, serv.getSessionFactory());
			if (lStrFileAttachId != null) {
				lObjCmnAttachmentMst = new CmnAttachmentMst();
				lObjCmnAttachmentMst = lObjCmnAttachmentMstDAO.findByAttachmentId(Long.parseLong(lStrFileAttachId));
				inputMap.put("scan", lObjCmnAttachmentMst);
			}

			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		resObj.setViewName("viewAgAttachedFiles");
		return resObj;
	}
	
	public ResultObject getAGRevisedPPOList(Map<Object, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		List<TrnPensionerRivisionDtls> lLstTrnPensionerRivisionDtls = new ArrayList<TrnPensionerRivisionDtls>();
		Integer lIntCount = 0;
		try {
			setSessionInfo(inputMap);
			ReadExcelDAO lObjReadExcelDAO = new ReadExcelDAOImpl(serv.getSessionFactory());
			
			Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);
			lIntCount = lObjReadExcelDAO.getAGRevisedPPOCount(gStrLocCode);
			if(lIntCount > 0)
			{
				lLstTrnPensionerRivisionDtls = lObjReadExcelDAO.getAGRevisedPPOList(displayTag,gStrLocCode);
			}
			inputMap.put("totalRecords", lIntCount);
			inputMap.put("AGRevisedPPOList", lLstTrnPensionerRivisionDtls);
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		resObj.setViewName("MapAGRevisedPpo");
		return resObj;
	}
	
	public ResultObject validatePPOBeforeMap(Map<String, Object> inputMap) {

		// TODO Auto-generated method stub
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String lStrPPONo = null;
		List lLstResult = new ArrayList();
		String lStrPpoNumber = "";
		String lStrPensionerCode = "";
		String lStrPensionerName = "";
		StringBuilder lStrBldXML = new StringBuilder();
		try {
			setSessionInfo(inputMap);
			ReadExcelDAO lObjReadExcelDAO = new ReadExcelDAOImpl(serv.getSessionFactory());
			lStrPPONo = StringUtility.getParameter("PpoNo", request);
			if (!"".equals(lStrPPONo)) {
				lLstResult = lObjReadExcelDAO.validatePPOBeforeMap(lStrPPONo.toUpperCase(), gStrLocCode);
				if(!lLstResult.isEmpty())
				{
					Object[] obj=(Object[])lLstResult.get(0);
					lStrPensionerCode = obj[0].toString();
					lStrPensionerName = obj[2].toString();
				}
			}
			
			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<ISEXISTS>");
			if(lLstResult.isEmpty()) {
				lStrBldXML.append("N");
			} else {
				lStrBldXML.append("Y");
			}
			lStrBldXML.append("</ISEXISTS>");
			if(!lLstResult.isEmpty()) {
				lStrBldXML.append("<PNSNRCODE>"+lStrPensionerCode);
				lStrBldXML.append("</PNSNRCODE>");
				lStrBldXML.append("<PNSNRNAME><![CDATA[" + lStrPensionerName);
				lStrBldXML.append("]]></PNSNRNAME>");
			}
			lStrBldXML.append("</XMLDOC>");

			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}
		return resObj;

	}

	public ResultObject mapAgRevisedPpoWithIfmsPpo(Map<String, Object> inputMap) {

		// TODO Auto-generated method stub
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String lStrRevisionIdPpo = null;
		String[] lArrStrRevisionIdPpo = null;
		String[] lArrStrRevIdIfmsPpo = null;
		String lStrRevisionId = "";
		String lStrIfmsPpoNo = "";
		String lStrIfmsPnsnrName = "";
		String lStrPensionerCode = "";
		TrnPensionerRivisionDtls lObjTrnPensionerRivisionDtls = new TrnPensionerRivisionDtls();
		StringBuilder lStrBldXML = new StringBuilder();
		try {
			setSessionInfo(inputMap);
			StgDeliveryDtlsDAO lObjStgDeliveryDtlsDAO = new StgDeliveryDtlsDAOImpl(TrnPensionerRivisionDtls.class, serv.getSessionFactory());
			lStrRevisionIdPpo = StringUtility.getParameter("revisionIdPpo", request);
			lArrStrRevisionIdPpo = lStrRevisionIdPpo.split("-");
			
			for(int lIntCnt=0;lIntCnt<lArrStrRevisionIdPpo.length;lIntCnt++)
			{
				lArrStrRevIdIfmsPpo = new String[4];
				lArrStrRevIdIfmsPpo = lArrStrRevisionIdPpo[lIntCnt].split("~");
				lStrRevisionId = lArrStrRevIdIfmsPpo[0];
				lStrIfmsPpoNo = lArrStrRevIdIfmsPpo[1];
				lStrIfmsPnsnrName = lArrStrRevIdIfmsPpo[2];
				lStrPensionerCode = lArrStrRevIdIfmsPpo[3];
				lObjTrnPensionerRivisionDtls = (TrnPensionerRivisionDtls) lObjStgDeliveryDtlsDAO.read(Long.parseLong(lStrRevisionId));
				lObjTrnPensionerRivisionDtls.setIfmsPPONo(lStrIfmsPpoNo.toUpperCase());
				lObjTrnPensionerRivisionDtls.setIfmsPensionerName(lStrIfmsPnsnrName);
				lObjTrnPensionerRivisionDtls.setPensionerCode(lStrPensionerCode);
				lObjTrnPensionerRivisionDtls.setPpoAcceptDate(gCurrDate);
				lObjTrnPensionerRivisionDtls.setUpdatedPostId(new BigDecimal(gLngPostId));
				lObjTrnPensionerRivisionDtls.setUpdatedUserId(new BigDecimal(gLngUserId));
				lObjTrnPensionerRivisionDtls.setUpdatedDate(gCurrDate);
				lObjStgDeliveryDtlsDAO.update(lObjTrnPensionerRivisionDtls);
			}
			
			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<MESSAGE>");
			lStrBldXML.append("Accepted Successfully");
			lStrBldXML.append("</MESSAGE>");
			lStrBldXML.append("</XMLDOC>");

			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}
		return resObj;

	}

}
