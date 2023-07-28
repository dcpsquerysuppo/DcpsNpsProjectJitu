package com.tcs.sgv.pensionpay.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.util.ExcelParser;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttachmentMstDAOImpl;
import com.tcs.sgv.common.valueobject.CmnAttachmentMpg;
import com.tcs.sgv.common.valueobject.CmnAttachmentMst;
import com.tcs.sgv.common.valueobject.CmnAttdocMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.dao.StgDeliveryDtlsDAO;
import com.tcs.sgv.pensionpay.dao.StgDeliveryDtlsDAOImpl;
import com.tcs.sgv.pensionpay.dao.UploadPensionBankACNoExcelDAOImpl;
import com.tcs.sgv.pensionpay.valueobject.StgDeliveryDtls;

public class UploadPensionBankACNoExcel extends ServiceImpl {
	private ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	Log gLogger = LogFactory.getLog(getClass());

	private String gStrPostId = null; /* STRING POST ID */

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for Current Date */
	Date gDtCurDate = null;
	
	/* Global Variable for LocCode*/
	String gStrLocCode = null;

	private void setSessionInfo(Map inputMap) {

		request = (HttpServletRequest) inputMap.get("requestObj");
		serv = (ServiceLocator) inputMap.get("serviceLocator");
		gLngPostId = SessionHelper.getPostId(inputMap);
		gStrPostId = gLngPostId.toString();
		gLngUserId = SessionHelper.getUserId(inputMap);
		gLngUserId.toString();
		gDtCurDate = SessionHelper.getCurDate();
		gStrLocCode = SessionHelper.getLocationCode(inputMap);
	}
	
	public ResultObject uploadPensionBankACNo(Map<String, Object> inputMap) {
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Object[][] xlsData = null;
		StgDeliveryDtls lObjStgDeliveryDtls = null;
		String lStrFileName = null;
		Long lLngDeliveryType = 140039L;
		Long lLngDelvId = null;
		Long lLngFileId = null;
		
		try{
		
			StgDeliveryDtlsDAO lObjStgDeliveryDtlsDAO = new StgDeliveryDtlsDAOImpl(StgDeliveryDtls.class, serv.getSessionFactory());
			UploadPensionBankACNoExcelDAOImpl lObjBankACNoExcelDAOImpl = new UploadPensionBankACNoExcelDAOImpl(serv.getSessionFactory());
			CmnAttdocMst cmnAttDocMst = null;
			lStrFileName = StringUtility.getParameter("attachedFileName", request);
			int lIntDotPos = lStrFileName.lastIndexOf(".");
			String lStrExtension = lStrFileName.substring(lIntDotPos);
			Integer lIntRowSize = 0;
			
			if(".xls".equals(lStrExtension)){
				Long lObjAttachmentId = null;
				Map attachMap = new HashMap();

				resObj = serv.executeService("FILE_UPLOAD_VOGEN", inputMap);
				resObj = serv.executeService("FILE_UPLOAD_SRVC", inputMap);

				attachMap = (Map) resObj.getResultValue();

				lObjAttachmentId = null;
				if (attachMap != null) {
					if (attachMap.get("AttachmentId_UploadBankACNoExcel") != null) {
						lObjAttachmentId = Long.parseLong(String.valueOf(attachMap.get("AttachmentId_UploadBankACNoExcel")));

					}
					if (lObjAttachmentId != null) {
						CmnAttachmentMst attachmentMst = new CmnAttachmentMst();
						attachmentMst.setAttachmentId(lObjAttachmentId);
					}					
				}

				if (lObjAttachmentId != null) {
					CmnAttachmentMstDAOImpl mnAttachmentMstDAO = new CmnAttachmentMstDAOImpl(CmnAttachmentMst.class, serv.getSessionFactory());
					CmnAttachmentMst cmnAttachmentMst = mnAttachmentMstDAO.findByAttachmentId(lObjAttachmentId);
					Iterator lObjIterator = cmnAttachmentMst.getCmnAttachmentMpgs().iterator();
					
					while (lObjIterator != null && lObjIterator.hasNext()) {
						CmnAttachmentMpg cmnAttachmentMpg = (CmnAttachmentMpg) lObjIterator.next();
						cmnAttDocMst = (CmnAttdocMst) cmnAttachmentMpg.getCmnAttdocMsts().iterator().next();
					}
					if (cmnAttDocMst != null) {
								
							Workbook lWorkBook = Workbook.getWorkbook(new ByteArrayInputStream(cmnAttDocMst.getFinalAttachment()));
							Sheet sheet = lWorkBook.getSheet(0);
				
							Integer lIntRow = sheet.getRows();
							Integer lIntCol = sheet.getColumns();
							
							if(lIntCol == 7 && lIntRow > 1){
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
								lLngDelvId = IFMSCommonServiceImpl.getNextSeqNum("stg_delivery_dtls", inputMap);				
								
								lObjStgDeliveryDtls = new StgDeliveryDtls();
								lLngFileId = IFMSCommonServiceImpl.getNextSeqNum("stg_file_dtls", inputMap);
								lObjStgDeliveryDtls.setFileId(lLngFileId);
								lObjStgDeliveryDtls.setDelvId(lLngDelvId);
								lObjStgDeliveryDtls.setDelvType(lLngDeliveryType);
								lObjStgDeliveryDtls.setUploadBy(gLngPostId);
								lObjStgDeliveryDtls.setUploadDate(DBUtility.getCurrentDateFromDB());
								lObjStgDeliveryDtls.setDelvStatus(bundleConst.getString("DELVSTATUS.SUCCESS"));
								lObjStgDeliveryDtls.setFileAttachmentId(lObjAttachmentId);
								lObjStgDeliveryDtls.setLocationCode(gStrLocCode);
								lObjStgDeliveryDtlsDAO.create(lObjStgDeliveryDtls);
			
								inputMap.put("LocCode", gStrLocCode);
								inputMap.put("PostId", gLngPostId);
								inputMap.put("UserId", gLngUserId);
								inputMap.put("DelvId", lLngDelvId);
								// -----------Inserting entry in stg_delivery_dtls
								lObjBankACNoExcelDAOImpl.setStgPnsnBankAcNo(xlsData, lIntRowSize, lLngDelvId, gLngPostId,gLngUserId,inputMap);
								
								List lLstPPONoAndPensionerCode = lObjBankACNoExcelDAOImpl.getPPONoAndPensionerCode(gStrLocCode, lLngDelvId);
								
								Map<Object,Object> lMapPensionerCode = new HashMap<Object,Object>();
								Map<Object,Object> lMapNewBankAcNo = new HashMap<Object,Object>();
								
								Object[] lArrObjPPO = null;
								Integer lIntValidExcelFlag = 1;
								List<String> lLstExcelPPO = new ArrayList<String>();
								for(int i=0;i<lLstPPONoAndPensionerCode.size();i++){
									lArrObjPPO = (Object[]) lLstPPONoAndPensionerCode.get(i);
									if(lArrObjPPO[0] != null && lArrObjPPO[0] != ""){
										lMapPensionerCode.put(lArrObjPPO[0], lArrObjPPO[1]);
										lMapNewBankAcNo.put(lArrObjPPO[0], lArrObjPPO[2]);
										lLstExcelPPO.add(lArrObjPPO[0].toString());
									}else{
										lIntValidExcelFlag = 0;
										break;	
									}																		
								}					
								if(lIntValidExcelFlag == 1){		
									Object[] lArrObjInvalidPPO = null;
									List lLstInvalidPPO = lObjBankACNoExcelDAOImpl.getInvalidPPOList(gStrLocCode, lLngDelvId);
									List<String> lLstPPO = new ArrayList<String>();
									if(lLstInvalidPPO != null && !lLstInvalidPPO.isEmpty()){
										for(int i=0;i<lLstInvalidPPO.size();i++){
											lArrObjInvalidPPO = (Object[]) lLstInvalidPPO.get(i);
											lLstPPO.add(lArrObjInvalidPPO[0].toString());
										}	
										lObjBankACNoExcelDAOImpl.insertErrorPPOData(lLstPPO, gStrLocCode, lLngDelvId,inputMap);
										lLstExcelPPO.removeAll(lLstPPO);
									}
									lObjBankACNoExcelDAOImpl.updateBankAcNo(lLstExcelPPO, lMapPensionerCode, lMapNewBankAcNo, inputMap);
									
									List lLstErroredPPONoList = lObjBankACNoExcelDAOImpl.getErroredPPONoList(gStrLocCode, lLngDelvId);						
									inputMap.put("Msg", "Accept");
									inputMap.put("ErrorPPO", lLstErroredPPONoList);
									lMapNewBankAcNo = null;
									lMapPensionerCode = null;
								}else{
									inputMap.put("Msg", "PPONoBlank");
								}
							}else{
								//not proper excel
								inputMap.put("Msg", "NotValidExcel");
							}
						}else{
							//not an excel
							inputMap.put("Msg", "NotExcel");
						}
				}else{
					//not an excel
					inputMap.put("Msg", "NotExcel");
				}				
			}else{
				//not an excel
				inputMap.put("Msg", "NotExcel");
			}
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		resObj.setResultValue(inputMap);
		resObj.setViewName("UploadBankACNo");
		return resObj;
	}
}
