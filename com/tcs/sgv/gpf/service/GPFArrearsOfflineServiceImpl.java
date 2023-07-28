package com.tcs.sgv.gpf.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.util.ExcelParser;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttachmentMstDAOImpl;
import com.tcs.sgv.common.valueobject.CmnAttachmentMpg;
import com.tcs.sgv.common.valueobject.CmnAttachmentMst;
import com.tcs.sgv.common.valueobject.CmnAttdocMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.gpf.dao.GPFArrearsManualEntryDAO;
import com.tcs.sgv.gpf.dao.GPFArrearsManualEntryDAOImpl;
import com.tcs.sgv.gpf.dao.GPFRequestProcessDAO;
import com.tcs.sgv.gpf.dao.GPFRequestProcessDAOImpl;

public class GPFArrearsOfflineServiceImpl extends ServiceImpl {

	private Log gLogger = LogFactory.getLog(getClass());

	/* Global Variable for User Location */
	String gStrUserLocation = null;

	private Long gLngPostId = null;

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	Long gLngUserId = null;

	/*
	 * Function to save the session specific details
	 */

	private void setSessionInfo(Map inputMap) throws Exception {

		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gLngPostId = SessionHelper.getPostId(inputMap);
			gLngUserId = SessionHelper.getUserId(inputMap);
		} catch (Exception e) {
			throw (e);
		}

	}

	public ResultObject readExcelData(Map<Object, Object> inputMap) {

		Object[][] xlsData = null;
		Object[][] empArrearsList = null;
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
				if (attachMap.get("AttachmentId_File") != null) {
					lObjAttachmentId = Long.parseLong(String.valueOf(attachMap.get("AttachmentId_File")));
				}
				if (lObjAttachmentId != null) {
					CmnAttachmentMst attachmentMst = new CmnAttachmentMst();
					attachmentMst.setAttachmentId(lObjAttachmentId);
				}
			}

			// Code For Attachement ends

			GPFArrearsManualEntryDAO lObjArrearsManualEntryDAO = new GPFArrearsManualEntryDAOImpl(null, serv
					.getSessionFactory());
			List lLstMonth = lObjArrearsManualEntryDAO.getMonths();
			List lLstYear = lObjArrearsManualEntryDAO.getYears();
			Long lLngYearId = 0l;
			Long lLngMonthId = 0l;
			inputMap.put("monthList", lLstMonth);
			inputMap.put("yearList", lLstYear);

			if (lObjAttachmentId != null) {
				CmnAttachmentMstDAOImpl mnAttachmentMstDAO = new CmnAttachmentMstDAOImpl(CmnAttachmentMst.class, serv
						.getSessionFactory());
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
								int rowNo = lObjRowLst.size() - 1;
								xlsData = new Object[rowNo][0];
								empArrearsList = new Object[rowNo][9];

								for (int i = 0; i < lObjRowLst.size() - 1; ++i) {
									xlsData[i] = ((List) lObjRowLst.get(i + 1)).toArray();
									empArrearsList[i][0] = xlsData[i][1];
									empArrearsList[i][1] = xlsData[i][2];
									empArrearsList[i][2] = xlsData[i][3];
									empArrearsList[i][3] = xlsData[i][4];
									empArrearsList[i][5] = xlsData[i][5];
									empArrearsList[i][6] = xlsData[i][6];
									lLngYearId = 0l;
									for (int j = 0; j < lLstYear.size(); j++) {
										ComboValuesVO lObjComboValuesVO = (ComboValuesVO) lLstYear.get(j);
										if (lObjComboValuesVO.getDesc().equals(xlsData[i][7])) {
											lLngYearId = Long.parseLong(lObjComboValuesVO.getId());
										}

									}
									lLngMonthId = 0l;
									for (int k = 0; k < lLstMonth.size(); k++) {
										ComboValuesVO lObjComboValuesVO = (ComboValuesVO) lLstMonth.get(k);
										if (lObjComboValuesVO.getDesc().equals(xlsData[i][8])) {
											lLngMonthId = Long.parseLong(lObjComboValuesVO.getId());
										}

									}
									empArrearsList[i][7] = lLngYearId;
									empArrearsList[i][8] = lLngMonthId;
								}

							}
						}
					}
				}
			}
			// inputMap.put("EmpGroup", lStrArrEmpGroup);

			inputMap.put("EmpArrearsList", empArrearsList);
			inputMap.put("totalRecords", xlsData.length);
			inputMap.put("EntryType", "Offline");
			objRes.setViewName("GPFArrearsManualEntry");
			objRes.setResultValue(inputMap);

		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, objRes, e, "Error is: ");
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

			String tempFilePath = serverPathStr + (lObjFile.separator.equals("\\") ? ("\\tempFile_") : "tempFile_")
					+ System.currentTimeMillis();
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

	public ResultObject createOfflineArrearsFile(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		setSessionInfo(inputMap);
		String relativeWebPath = "/WEB-INF/jsp/gpf";
		String absoluteFilePath = request.getSession().getServletContext().getRealPath(relativeWebPath);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		HttpServletResponse response = (HttpServletResponse) inputMap.get("responseObj");
		try {
			GPFArrearsManualEntryDAO lObjArrearsManualEntryDAO = new GPFArrearsManualEntryDAOImpl(null, serv
					.getSessionFactory());
			List lLstMonth = lObjArrearsManualEntryDAO.getMonths();
			List lLstYear = lObjArrearsManualEntryDAO.getYears();
			GPFRequestProcessDAO lObjGPFRequestProcessDAO = new GPFRequestProcessDAOImpl(null, serv.getSessionFactory());
			String lStrDDOCode = lObjGPFRequestProcessDAO.getDdoCodeForDDO(gLngPostId);

			String lStrSevaarthId = StringUtility.getParameter("sevaarthId", request);
			String lStrEmpName = StringUtility.getParameter("empName", request);
			String lStrInstNo = StringUtility.getParameter("instNo", request);
			String lStrGpfAccNo = StringUtility.getParameter("gpfAccNo", request);
			String lStrYearId = StringUtility.getParameter("yearId", request);
			String lStrMonthId = StringUtility.getParameter("monthId", request);
			String lStrAmount = StringUtility.getParameter("amount", request);
			String lStrInstAmount = StringUtility.getParameter("instAmount", request);

			String[] lStrArrSevaarthId = lStrSevaarthId.split("~");
			String[] lStrArrEmpName = lStrEmpName.split("~");
			String[] lStrArrInstNo = lStrInstNo.split("~");
			String[] lStrArrYearId = lStrYearId.split("~");
			String[] lStrArrGpfAccNo = lStrGpfAccNo.split("~");
			String[] lStrArrMonthId = lStrMonthId.split("~");
			String[] lStrArrAmount = lStrAmount.split("~");
			String[] lStrArrInstAmount = lStrInstAmount.split("~");
			WorkbookSettings ws = new WorkbookSettings();

			String tempFilePath = absoluteFilePath + "\\ArrearOffLine\\ArrearsData_" + lStrDDOCode + ".xls";
			File xlsFile = new File(tempFilePath);

			WritableWorkbook workbook = Workbook.createWorkbook(xlsFile, ws);

			WritableSheet workSheet = null;
			workSheet = workbook.createSheet("GPF Arrears", 0);
			workSheet.getSettings();

			WritableFont normalFont = new WritableFont(WritableFont.createFont("MS Sans Serif"),
					WritableFont.DEFAULT_POINT_SIZE, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE);

			WritableFont boldFont = new WritableFont(WritableFont.createFont("MS Sans Serif"),
					WritableFont.DEFAULT_POINT_SIZE, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE);

			WritableCellFormat normalFormat = new WritableCellFormat(normalFont);
			normalFormat.setWrap(true);
			normalFormat.setAlignment(jxl.format.Alignment.CENTRE);
			normalFormat.setVerticalAlignment(VerticalAlignment.CENTRE);

			WritableCellFormat BoldFormat = new WritableCellFormat(boldFont);
			BoldFormat.setWrap(true);
			BoldFormat.setAlignment(jxl.format.Alignment.CENTRE);
			BoldFormat.setVerticalAlignment(VerticalAlignment.CENTRE);

			workSheet.addCell(new jxl.write.Label(0, 0, "Sr No", BoldFormat));
			workSheet.addCell(new jxl.write.Label(1, 0, "Sevaarth Id", BoldFormat));
			workSheet.addCell(new jxl.write.Label(2, 0, "Employee Name", BoldFormat));
			workSheet.addCell(new jxl.write.Label(3, 0, "GPF Account No", BoldFormat));
			workSheet.addCell(new jxl.write.Label(4, 0, "Installment No", BoldFormat));
			workSheet.addCell(new jxl.write.Label(5, 0, "Total Amount", BoldFormat));
			workSheet.addCell(new jxl.write.Label(6, 0, "Installment Amount", BoldFormat));
			workSheet.addCell(new jxl.write.Label(7, 0, "Year", BoldFormat));
			workSheet.addCell(new jxl.write.Label(8, 0, "Month", BoldFormat));

			String strYear = "";
			String strMonth = "";

			for (int index = 0; index < lStrArrYearId.length; index++) {

				Integer excelRow = index + 1;
				strYear = "";
				for (int j = 0; j < lLstYear.size(); j++) {
					ComboValuesVO lObjComboValuesVO = (ComboValuesVO) lLstYear.get(j);
					if (Long.parseLong(lObjComboValuesVO.getId()) == Long.parseLong(lStrArrYearId[index])) {
						strYear = lObjComboValuesVO.getDesc();
					}

				}
				strMonth = "";
				for (int k = 0; k < lLstMonth.size(); k++) {
					ComboValuesVO lObjComboValuesVO = (ComboValuesVO) lLstMonth.get(k);
					if (Long.parseLong(lObjComboValuesVO.getId()) == Long.parseLong(lStrArrMonthId[index])) {
						strMonth = lObjComboValuesVO.getDesc();
					}
				}
				workSheet.addCell(new jxl.write.Label(0, excelRow, excelRow.toString(), normalFormat));
				workSheet.addCell(new jxl.write.Label(1, excelRow, lStrArrSevaarthId[index], normalFormat));
				workSheet.addCell(new jxl.write.Label(2, excelRow, lStrArrEmpName[index], normalFormat));
				workSheet.addCell(new jxl.write.Label(3, excelRow, lStrArrGpfAccNo[index], normalFormat));
				workSheet.addCell(new jxl.write.Number(4, excelRow, Integer.parseInt(lStrArrInstNo[index]),
						normalFormat));
				workSheet.addCell(new jxl.write.Number(5, excelRow, Double.parseDouble(lStrArrAmount[index]),
						normalFormat));
				workSheet.addCell(new jxl.write.Number(6, excelRow, Double.parseDouble(lStrArrInstAmount[index]),
						normalFormat));
				workSheet.addCell(new jxl.write.Label(7, excelRow, strYear, normalFormat));
				workSheet.addCell(new jxl.write.Label(8, excelRow, strMonth, normalFormat));
			}

			CellView cell = new CellView();
			for (int x = 0; x < 10; x++) {
				cell = workSheet.getColumnView(x);
				cell.setAutosize(true);
				workSheet.setColumnView(x, cell);
			}

			workbook.write();
			workbook.close();
			
			
			CreateZip(absoluteFilePath, lStrDDOCode);

			File zipFile = new File(absoluteFilePath
							+ "\\OfflinePackages\\ArrearData_"
							+ lStrDDOCode + ".zip");
			FileInputStream fis = new FileInputStream(zipFile);
			BufferedInputStream bInputStr = new BufferedInputStream(fis);
			byte[] byteBuffer = new byte[(int) zipFile.length()];
			bInputStr.read(byteBuffer);

			bInputStr.close();
			zipFile.delete();
			request.setAttribute("ContentType", "application/zip");
			request.setAttribute("ExportedReportBytesArray",byteBuffer);
			response.addHeader("Content-disposition","attachment; filename=ArrearData_"
									+ lStrDDOCode + ".zip");
			response.setCharacterEncoding("UTF-8");

		} catch (Exception e) {			
			gLogger.error(" Error is : " + e, e);
		}
		resObj.setViewName("ExportReportPage");

		return resObj;
	}
	
	public void CreateZip(String inPath, String lStrDDOCode) {

		try {

			File inFolder = new File(inPath + "\\ArrearOffLine");
			File outFolder = new File(inPath
					+ "\\OfflinePackages\\ArrearData_" + lStrDDOCode
					+ ".zip");

			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
					new FileOutputStream(outFolder)));

			BufferedInputStream in = null;
			byte[] data = new byte[1000];
			String files[] = inFolder.list();
			for (int i = 0; i < files.length; i++) {
				if (files[i].equals("images")) {

					File subFolder = new File(inPath
							+ "\\ArrearOffLine\\images");

					String subFiles[] = subFolder.list();
					for (int j = 0; j < subFiles.length; j++) {
						in = new BufferedInputStream(new FileInputStream(
								subFolder.getPath() + "/" + subFiles[j]), 1000);
						out
								.putNextEntry(new ZipEntry("images\\"
										+ subFiles[j]));
						int count;
						while ((count = in.read(data, 0, 1000)) != -1) {
							out.write(data, 0, count);
						}
					}

				} else {
					in = new BufferedInputStream(new FileInputStream(inFolder
							.getPath()
							+ "/" + files[i]), 1000);
					if (files[i].endsWith("xls")) {
						if (files[i].endsWith(lStrDDOCode + ".xls")) {
							out.putNextEntry(new ZipEntry(
									"ArrearData.xls"));
						} else {
							continue;
						}
					} else {
						out.putNextEntry(new ZipEntry(files[i]));
					}
					int count;
					while ((count = in.read(data, 0, 1000)) != -1) {
						out.write(data, 0, count);
					}
					if (files[i].endsWith("xls")) {
						File file = new File(files[i]);
						file.delete();
					}
				}
				out.closeEntry();
			}
			out.flush();
			out.close();
		} catch (Exception e) {
			gLogger.error("Error is: " + e ,e);
		}
	}
}
