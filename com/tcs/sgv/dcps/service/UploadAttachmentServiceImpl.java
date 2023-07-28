package com.tcs.sgv.dcps.service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.helper.WorkFlowHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.util.ExcelParser;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttachmentMstDAOImpl;
import com.tcs.sgv.common.valueobject.CmnAttachmentMpg;
import com.tcs.sgv.common.valueobject.CmnAttachmentMst;
import com.tcs.sgv.common.valueobject.CmnAttdocMst;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;


public class UploadAttachmentServiceImpl extends ServiceImpl {

	private ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	private Log gLogger = LogFactory.getLog(getClass());

	/* Global Variable for PostId */
	private String gStrPostId = null; /* STRING POST ID */

	/* Global Variable for User Loc Map */
	static HashMap sMapUserLoc = new HashMap();

	/* Global Variable for User Location */
	String gStrUserLocation = null;

	private String gStrLocationCode = null;

	private Long gLngPostId = null;

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	private HttpSession session = null; /* SESSION */

	private Date gDtCurDate = null; /* CURRENT DATE */

	private String gStrUserId = null; /* STRING USER ID */

	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/dcps/DCPSConstants");

	Long gLngUserId = null;

	private Long gLngDBId = null; /* DB ID */

	/*
	 * Function to save the session specific details
	 */

	private void setSessionInfo(Map inputMap) throws Exception {

		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			session = request.getSession();
			gLngPostId = SessionHelper.getPostId(inputMap);
			gStrPostId = SessionHelper.getPostId(inputMap).toString();
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
			gLngUserId = SessionHelper.getUserId(inputMap);
			gStrUserId = gLngUserId.toString();
			gLngDBId = SessionHelper.getDbId(inputMap);
			gDtCurDate = SessionHelper.getCurDate();
		} catch (Exception e) {
			throw (e);
		}

	}

	public ResultObject getDataFromXLS(Map<Object, Object> inputMap) {

		Object[][] xlsData = null;
		Object[][] empList = null;
		Object[][] xlsHeaders = null;

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try {
			setSessionInfo(inputMap);
			inputMap.put("lLngPostId", gLngPostId);

			// Code For Attachement starts
			Long lObjAttachmentId = null;
			Map attachMap = new HashMap();
			objRes = serv.executeService("FILE_UPLOAD_VOGEN", inputMap);
			objRes = serv.executeService("FILE_UPLOAD_SRVC", inputMap);

			attachMap = (Map) objRes.getResultValue();

			if (attachMap != null) {
				if (attachMap.get("AttachmentId_scan") != null) {
					lObjAttachmentId = Long.parseLong(String.valueOf(attachMap.get("AttachmentId_scan")));
				}
				if (lObjAttachmentId != null) {
					CmnAttachmentMst attachmentMst = new CmnAttachmentMst();
					attachmentMst.setAttachmentId(lObjAttachmentId);
				}
			}

			// Code For Attachement ends

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			Long lLongbillGroupId = null;
			Long monthId = null;
			Long yearId = null;

			String schemename = null;
			/*
			 * List lLstMonths = lObjDcpsCommonDAO.getMonths();
			 * inputMap.put("MONTHS", lLstMonths);
			 * 
			 * List lLstYears = lObjDcpsCommonDAO.getYears();
			 * inputMap.put("YEARS", lLstYears);
			 */
			List listPayCommission = IFMSCommonServiceImpl.getLookupValues("PayCommissionDCPS", SessionHelper.getLangId(inputMap), inputMap);
			inputMap.put("listPayCommission", listPayCommission);
			List listTypeOfPayment = IFMSCommonServiceImpl.getLookupValues("TypeOfPaymentDCPS", SessionHelper.getLangId(inputMap), inputMap);
			inputMap.put("listTypeOfPayment", listTypeOfPayment);
			/*
			 * List lLstBillGroups = lObjDcpsCommonDAO.getBillGroups();
			 * inputMap.put("BILLGROUPS", lLstBillGroups);
			 */

			if (lObjAttachmentId != null) {
				CmnAttachmentMstDAOImpl mnAttachmentMstDAO = new CmnAttachmentMstDAOImpl(CmnAttachmentMst.class, serv.getSessionFactory());
				CmnAttachmentMst cmnAttachmentMst = mnAttachmentMstDAO.findByAttachmentId(lObjAttachmentId);
				Iterator lObjIterator = cmnAttachmentMst.getCmnAttachmentMpgs().iterator();

				while (lObjIterator != null && lObjIterator.hasNext()) {
					CmnAttachmentMpg cmnAttachmentMpg = (CmnAttachmentMpg) lObjIterator.next();
					CmnAttdocMst cmnAttDocMst = (CmnAttdocMst) cmnAttachmentMpg.getCmnAttdocMsts().iterator().next();

					if (cmnAttDocMst != null) {
						List lObjSheetLst = getDocumentForXLS(cmnAttDocMst, request);

						if (lObjSheetLst != null && !lObjSheetLst.isEmpty()) {
							List lObjRowLst = (List) lObjSheetLst.get(0);
							if (lObjRowLst != null && !lObjRowLst.isEmpty()) {
								int rowNo = lObjRowLst.size() - 11;
								xlsData = new Object[rowNo][];
								empList = new Object[rowNo][15];
								xlsHeaders = new Object[10][];
								for (int i = 11; i < lObjRowLst.size(); ++i) {
									int row = i - 11;
									xlsData[row] = ((List) lObjRowLst.get(i)).toArray();
									for (int j = 0; j < listPayCommission.size(); j++) {
										new ArrayList();
										CmnLookupMst tempList = (CmnLookupMst) listPayCommission.get(j);

										if (tempList.getLookupDesc().equals(xlsData[row][2])) {
											empList[row][3] = new Long(tempList.getLookupId());
										}
									}

									for (int j = 0; j < listTypeOfPayment.size(); j++) {
										new ArrayList();
										CmnLookupMst tempList = (CmnLookupMst) listTypeOfPayment.get(j);

										if (tempList.getLookupDesc().equals(xlsData[row][4])) {
											empList[row][6] = new Long(tempList.getLookupId());
										}

									}
									empList[row][0] = xlsData[row][8];
									empList[row][1] = xlsData[row][1];
									empList[row][2] = xlsData[row][0];
									empList[row][4] = xlsData[row][3];
									empList[row][11] = xlsData[row][5];
									empList[row][12] = xlsData[row][6];
									empList[row][13] = xlsData[row][7];
									empList[row][14] = xlsData[row][15];
								}

								for (int i = 0; i < 10; ++i) {
									xlsHeaders[i] = ((List) lObjRowLst.get(i)).toArray();
								}
								monthId = Long.parseLong((String) xlsHeaders[5][1]);
								schemename = (String) xlsHeaders[3][1];
								/*
								 * for(int i=0;i<lLstYears.size();i++){ List
								 * list = new ArrayList(); ComboValuesVO
								 * lObjComboValuesVO= (ComboValuesVO)
								 * lLstYears.get(i);
								 * if(lObjComboValuesVO.getDesc
								 * ().equals(xlsHeaders[6][1]) ){ yearId =
								 * Long.parseLong(lObjComboValuesVO.getId()); }
								 * } for(int i=0;i<lLstBillGroups.size();i++){
								 * List list = new ArrayList(); ComboValuesVO
								 * lObjComboValuesVO= (ComboValuesVO)
								 * lLstBillGroups.get(i);
								 * if(lObjComboValuesVO.getDesc
								 * ().equals(xlsHeaders[2][1]) ){
								 * lLongbillGroupId =
								 * Long.parseLong(lObjComboValuesVO.getId()); }
								 * }
								 */
							}
						}
					}
				}
				/*
				 * Runnable runnable = new
				 * StgToMasterServiceImpl(serv.getSessionFactory(), lLngDelvId,
				 * inputMap); gObjThread = new Thread(runnable,
				 * "PnsnPmnt Thread"); gObjThread.start();
				 */
			}
			Integer lIntMonth = Integer.parseInt(monthId.toString());
			Integer lIntYear = Integer.parseInt(xlsHeaders[6][1].toString());

			Date lDtLastDate = lObjDcpsCommonDAO.getLastDate(lIntMonth - 1, lIntYear);
			Date lDtFirstDate = lObjDcpsCommonDAO.getFirstDate(lIntMonth - 1, lIntYear);

			if (lIntMonth == 1) {
				lIntYear--;
			}
			Date lDtDelFirstDate = lObjDcpsCommonDAO.getFirstDate(lIntMonth - 2, lIntYear);
			Date lDtDelLastDate = lObjDcpsCommonDAO.getLastDate(lIntMonth - 2, lIntYear);
			char cEditForm = 'N';
			inputMap.put("EditForm", cEditForm);
			inputMap.put("lLongbillGroupId", lLongbillGroupId);
			inputMap.put("schemename", schemename);
			inputMap.put("FirstDate", lDtFirstDate);
			inputMap.put("LastDate", lDtLastDate);
			inputMap.put("DelFirstDate", lDtDelFirstDate);
			inputMap.put("DelLastDate", lDtDelLastDate);
			inputMap.put("monthId", monthId);
			inputMap.put("yearId", yearId);
			inputMap.put("empList", empList);
			inputMap.put("totalRecords", empList.length);
			/* Data for Headers */
			String treasuryName = xlsHeaders[0][1].toString();
			String DdoName = xlsHeaders[1][1].toString();
			String BillGroup = xlsHeaders[2][1].toString();
			String Month = xlsHeaders[4][1].toString();
			String Year = xlsHeaders[6][1].toString();
			yearId = Long.parseLong(xlsHeaders[6][2].toString());
			String treasuryCode = xlsHeaders[0][2].toString();
			String DDoCode = xlsHeaders[1][2].toString();
			String BillGroupId = xlsHeaders[2][2].toString();
			String SchemeCode = xlsHeaders[3][2].toString();

			List UserList = null;

			UserList = getHierarchyUsers(inputMap);

			inputMap.put("UserList", UserList);

			inputMap.put("treasuryName", treasuryName);
			inputMap.put("treasuryCode", treasuryCode);
			inputMap.put("DdoName", DdoName);
			inputMap.put("DDoCode", DDoCode);
			inputMap.put("BillGroup", BillGroup);
			inputMap.put("BillGroupId", BillGroupId);
			inputMap.put("Month", Month);
			inputMap.put("Year", Year);
			inputMap.put("yearId", yearId);
			inputMap.put("SchemeCode", SchemeCode);

			String TypeOfContribution = "Offline";
			inputMap.put("TypeOfContribution", TypeOfContribution);
			/* End */

			inputMap.put("lStrUser", "ATO");
			inputMap.put("lStrUse", "ViewAll");
			objRes.setViewName("DCPSOfflineEntryForm");
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

	private List getHierarchyUsers(Map inputMap) {

		List UserList = null;
		String subjectName = null;

		try {
			setSessionInfo(inputMap);

			Integer llFromLevelId = 0;
			UserList = new ArrayList<String>();

			subjectName = gObjRsrcBndle.getString("DCPS.Contribution");

			Long lLngHierRefId = WorkFlowHelper.getHierarchyByPostIDAndDescription(gStrPostId, subjectName, inputMap);

			llFromLevelId = WorkFlowHelper.getLevelFromPostMpg(gStrPostId, lLngHierRefId, inputMap);

			List rsltList = WorkFlowHelper.getUpperPost(gStrPostId, lLngHierRefId, llFromLevelId, inputMap);

			Object[] lObjNextPost = null;

			for (Integer lInt = 0; lInt < rsltList.size(); lInt++) {

				lObjNextPost = (Object[]) rsltList.get(lInt);

				if (!(lObjNextPost.equals(null))) {
					UserList.add(lObjNextPost[0].toString());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error(" Error is : " + e, e);
		}
		return UserList;
	}
}
