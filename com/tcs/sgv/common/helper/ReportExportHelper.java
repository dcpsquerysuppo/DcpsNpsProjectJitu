/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Nov 30, 2011		Vrajesh Raval								
 *******************************************************************************
 */
package com.tcs.sgv.common.helper;

/**
 * Class Description - 
 *
 *
 * @author Vrajesh Raval
 * @version 0.1
 * @since JDK 5.0
 * Nov 30, 2011
 */
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import com.tcs.sgv.common.constant.DBConstants;

import com.tcs.sgv.common.constant.DBConstants;
import com.tcs.sgv.core.valueobject.ResultObject;


public class ReportExportHelper {

	private static final Log logger = LogFactory.getLog(ReportExportHelper.class);
	static NumberFormat nf = NumberFormat.getInstance();
	static SimpleDateFormat oDateFormat;
	static SimpleDateFormat oSimpleFormat = new SimpleDateFormat("dd/MM/yyyy");
	static {
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);
		nf.setGroupingUsed(false);
		oDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	}
	ResourceBundle localStringsBundle = null;
	SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	Map objFormatter = new HashMap();
	static int NUM_ROW = 55;
	static int NUM_COLUMNS = 80;
	public int startPageNumber = 1;
	public int totalPages = 1;
	public boolean showPageNumber = true;
	public int noOfLines = 0;
	public static String gStrAlign = null;
	public final static String SEL_BOLD = new String(new char[]{27, 69});
	public final static String CAN_BOLD = new String(new char[]{27, 70});

	@SuppressWarnings({"unchecked", "unchecked", "unchecked", "unchecked"})
	public String getReportFile(final List rMainData, ColumnVo[] columnHeading, final String Header, String Footer, final String strParameterString, int nMode, boolean pageBreakonGrouping,
			int noOfLinesPrintedOnPage, boolean displayHeading, String lStrSeperator)

	{

		try {
			int nColumns = 0;
			nColumns = columnHeading.length;
			int nColumnsSize[] = null;
			// int nColumnsActualSize[] =null;
			String LINE_SEP = "\r\n";
			String SEPARATOR = lStrSeperator;

			String ReportGenerationTime = ReportHelper.oDateTimeFormat.format((new Date()));
			char c = 12;
			StringBuffer ff = new StringBuffer();
			ff.append(c);
			NUM_COLUMNS = nMode;
			LinkedList<String> objBody = new LinkedList<String>();
			LinkedList<String> objParameters = new LinkedList<String>();
			List al = new ArrayList();
			StringBuilder sbLine = new StringBuilder(NUM_COLUMNS);
			for (int i = 0; i < NUM_COLUMNS; i++) {
				sbLine.append('-');
			}

			StringBuffer fos = new StringBuffer();

			/* Heading and Parameter String Display */

			List<String> alReportTitle = new ArrayList<String>();
			String sTokens[] = Header.split("\r\n");
			for (String sTitle : sTokens) {
				if (noOfLinesPrintedOnPage == 1) {
					if(gStrAlign != null && "L".equals(gStrAlign)) {
						objBody.add(sTitle);
					}
					else {
						objBody.add(getCenterAlign(sTitle.trim(), NUM_COLUMNS));
					}
				}
				if(gStrAlign != null && "L".equals(gStrAlign)) {
					alReportTitle.add(sTitle);
				}
				else {
					alReportTitle.add(getCenterAlign(sTitle.trim(), NUM_COLUMNS));
				}
			}

			if (strParameterString != null) {
				StringBuilder sParamter = new StringBuilder();
				String tokens[] = strParameterString.split(",");
				for (String sToken : tokens) {
					/*
					 * if( (sToken.length() + sParamter.length()) < NUM_COLUMNS)
					 * sParamter.append(sToken + SEPARATOR); else
					 * objParameters.add(sToken + SEPARATOR );
					 */
					if (sToken != null && sToken.indexOf('\n') > 0) {
						objParameters.add(sToken.toString().trim());
					} else {
						objParameters.add(getCenterAlign(sToken.toString().trim(), nMode));
					}
				}

			}
			alReportTitle.addAll(objParameters);
			if (displayHeading) {
				alReportTitle.add(sbLine.toString());
			}

			objBody.addAll(objParameters);
			if (displayHeading) {
				objBody.add(sbLine.toString());
			}
			List reportData = rMainData;

			nColumnsSize = new int[columnHeading.length];
			int cntrl = 0;
			for (Object key : columnHeading) {
				ColumnVo columnvo = (ColumnVo) key;
				nColumnsSize[cntrl] = columnvo.getWidth();
				cntrl++;
			}
			if (displayHeading) {
				al = getColumnHeaderReportFile(columnHeading);
				al.add(sbLine.toString());
			}
			objBody.addAll(al);

			/* Report Detail Printing */
			boolean isGrouping = false;
			boolean isTotal = false;
			List<ColumnVo> alDisplay = new ArrayList<ColumnVo>();
			List l1 = new ArrayList();

			for (int i = 0; i < columnHeading.length; i++) {
				ColumnVo key = columnHeading[i];
				l1.add(key);
				if (key.isGroupingOrder()) {
					isGrouping = true;
				} else {
					alDisplay.add(key);
				}
				if (key.getDisplayTotal() == 1) {
					isTotal = true;
				}
			}

			nColumns = alDisplay.size();
			if (isGrouping) {
				reportData = getGroupingHelper(reportData, l1);
			}

			BigDecimal[] bgTotalAmt = new BigDecimal[nColumns];
			for (int i = 0; i < nColumns; i++) {
				bgTotalAmt[i] = BigDecimal.ZERO;
			}

			for (Object row : reportData) {
				StringBuffer sb = new StringBuffer();
				List[] arrangedFields = new ArrayList[nColumns];
				int maxRow = 1;
				List cols = (ArrayList) row;

				for (int j = 0; j < cols.size(); j++) {

					String sColumnValue = (cols.get(j) != null ? cols.get(j).toString() : "");

					if (isGrouping == true && "GTOTAL".equals(sColumnValue)) {
						objBody.add(sb.toString());
						sColumnValue = "Total";
					}
					if (isGrouping && "2GRP".equals(sColumnValue)) {
						objBody.add(sb.toString());
						sColumnValue = " ";
					}
					// ColumnVo columnVo = columnHeading[j];
					ColumnVo columnVo = alDisplay.get(j);
					if (columnVo.getDisplayTotal() == 1 && sColumnValue != null && !sColumnValue.equals("")) {
						bgTotalAmt[j] = bgTotalAmt[j].add(new BigDecimal(sColumnValue));
					}
					if (columnVo.isAllowWrap()) {
						if (columnVo.getAlignMent() == 3) {
							arrangedFields[j] = arrangeStringCenter(sColumnValue.trim(), columnVo.getRealWidth());
						} else {
							arrangedFields[j] = arrangeString(sColumnValue.trim(), columnVo.getRealWidth());
						}

						if (arrangedFields[j].size() > maxRow) {
							maxRow = arrangedFields[j].size();
						}
					} else {
						try {
							if (columnVo.isCurrancyFormated()) {
								sColumnValue = nf.format(new BigDecimal(sColumnValue));
							}
						} catch (Exception e) {
						}

						if (columnVo.getAlignMent() == 1) {

							arrangedFields[j] = new ArrayList();
							arrangedFields[j].add(String.format("%-" + columnVo.getWidth() + "s", sColumnValue));
						} else if (columnVo.getAlignMent() == 2) {
							arrangedFields[j] = new ArrayList();
							arrangedFields[j].add(String.format("%" + columnVo.getWidth() + "s", sColumnValue));
						} else {
							arrangedFields[j] = new ArrayList();
							arrangedFields[j].add(getCenterAlign(sColumnValue, columnVo.getWidth()));
						}

					}
				}
				for (int rCnt = 0; rCnt < maxRow; rCnt++) {
					for (int nCnt = 0; nCnt < arrangedFields.length; nCnt++) {
						ColumnVo columnVo = alDisplay.get(nCnt);
						if (arrangedFields[nCnt].size() > rCnt) {
							sb.append(arrangedFields[nCnt].get(rCnt).toString() + SEPARATOR);
						} else {
							sb.append(String.format("%" + columnVo.getWidth() + "s", " ") + SEPARATOR);
							// sb.append(String.format("%230s"," ").substring(0,nColumnsActualSize[nCnt])
							// + SEPARATOR );
						}
					}
					objBody.add(sb.toString());
					sb = new StringBuffer();
				}
			} // end of for row formatttion

			/* Display Total */
			if (isTotal) {
				objBody.add(sbLine.toString());
				StringBuffer sb = new StringBuffer();
				boolean isDisplayTotal = false;
				for (int t = 0; t < nColumns; t++) {
					ColumnVo columnVo = columnHeading[t];
					if (columnVo.getDisplayTotal() == 1) {
						if (columnVo.isCurrancyFormated()) {
							sb.append(String.format("%" + (columnVo.getWidth() + 1) + "s", nf.format(bgTotalAmt[t])));
						} else {
							sb.append(String.format("%" + (columnVo.getWidth() + 1) + "s", bgTotalAmt[t]));
						}
					} else {
						if (columnVo.getTotalLabel() != null) {
							sb.append(String.format("%-" + (columnVo.getWidth()) + "s", columnVo.getTotalLabel()));
						} else {
							if (t == 0) {
								isDisplayTotal = true;
								sb.append(String.format("%-" + columnVo.getWidth() + "s", ""));
							} else {
								sb.append(String.format("%" + (columnVo.getWidth() + 1) + "s", " "));
							}
						}
					}

				}
				if (isDisplayTotal == true) {
					objBody.add(sb != null ? sb.replace(0, 5, "Total").toString() : "");
				} else {
					objBody.add(sb != null ? sb.toString() : "");
				}
			}
			/* Display Total Over */
			if (displayHeading) {
				objBody.add(sbLine.toString());
			}
			int nPageNumber = startPageNumber;
			// int nLinesinPage = 1;
			int nLinesinPage = noOfLinesPrintedOnPage; // For sub reports if two
														// reports are coming on
														// same page then
														// header,page,time etc
														// should be displayed
														// only once.So
														// assigning no. of
														// lines
														// that are already
														// printed on the
														// current page due to
														// sub report.

			for (String strLine : objBody) {
				if (nLinesinPage % NUM_ROW == 0) {
					fos.append(ff.toString());
					nLinesinPage = 1;
					noOfLines = 1;
					if (showPageNumber) {
						fos.append(LINE_SEP);
						fos.append((ReportGenerationTime + String.format("%" + (NUM_COLUMNS - 20) + "s", "Page: " + (nPageNumber++))));
						fos.append(LINE_SEP);
					}
					if (nPageNumber > 1) {
						for (Object subHeading : alReportTitle) {

							fos.append((subHeading.toString() + LINE_SEP));
							nLinesinPage++;
							noOfLines++;

						}
						for (Object colHead : al) {
							fos.append((colHead + LINE_SEP));
							nLinesinPage++;
							noOfLines++;
						}
					}
				} else if (nLinesinPage == 1 && showPageNumber) {
					fos.append(LINE_SEP);
					fos.append((ReportGenerationTime + String.format("%" + (NUM_COLUMNS - 20) + "s", "Page: " + (nPageNumber++))));
					fos.append(LINE_SEP);

				}
				if (strLine.equals(ff.toString())) {
					nLinesinPage = 1;
					noOfLines = 1;
				} else {
					nLinesinPage++;
					noOfLines++;
				}

				fos.append((strLine + LINE_SEP));
			}
			totalPages = nPageNumber;
			fos.append("\r\n" + Footer);
			return fos.toString();
		} catch (Exception e) {
			logger.error("Exception in ReportExportHelper.getReportFile:: " + e, e);
		}
		return null;
	}

	public String getReportFileForCvpBill(final List rMainData, ColumnVo[] columnHeading, final String Header, String Footer, final String strParameterString, int nMode, boolean pageBreakonGrouping,
			int noOfLinesPrintedOnPage)

	{

		try {
			int nColumns = 0;
			nColumns = columnHeading.length;
			int nColumnsSize[] = null;
			// int nColumnsActualSize[] =null;
			String LINE_SEP = "\r\n";
			String SEPARATOR = "|";

			String ReportGenerationTime = ReportHelper.oDateTimeFormat.format((new Date()));
			char c = 12;
			StringBuffer ff = new StringBuffer();
			ff.append(c);
			NUM_COLUMNS = nMode;
			LinkedList<String> objBody = new LinkedList<String>();
			LinkedList<String> objParameters = new LinkedList<String>();
			List al = null;
			StringBuilder sbLine = new StringBuilder(NUM_COLUMNS);
			if (noOfLinesPrintedOnPage == 1) {
				for (int i = 0; i < NUM_COLUMNS; i++) {
					sbLine.append('-');
				}
			}
			StringBuffer fos = new StringBuffer();

			/* Heading and Parameter String Display */

			List<String> alReportTitle = new ArrayList<String>();
			String sTokens[] = Header.split("\r\n");
			for (String sTitle : sTokens) {
				if (noOfLinesPrintedOnPage == 1) {
					objBody.add(getCenterAlign(sTitle.trim(), NUM_COLUMNS));
				}
				alReportTitle.add(getCenterAlign(sTitle.trim(), NUM_COLUMNS));
			}

			if (strParameterString != null) {
				StringBuilder sParamter = new StringBuilder();
				String tokens[] = strParameterString.split(",");
				for (String sToken : tokens) {
					/*
					 * if( (sToken.length() + sParamter.length()) < NUM_COLUMNS)
					 * sParamter.append(sToken + SEPARATOR); else
					 * objParameters.add(sToken + SEPARATOR );
					 */
					if (sToken != null && sToken.indexOf('\n') > 0) {
						objParameters.add(sToken.toString().trim());
					} else {
						objParameters.add(getCenterAlign(sToken.toString().trim(), nMode));
					}
				}

			}
			alReportTitle.addAll(objParameters);
			alReportTitle.add(sbLine.toString());

			objBody.addAll(objParameters);
			objBody.add(sbLine.toString());
			List reportData = rMainData;

			nColumnsSize = new int[columnHeading.length];
			int cntrl = 0;
			for (Object key : columnHeading) {
				ColumnVo columnvo = (ColumnVo) key;
				nColumnsSize[cntrl] = columnvo.getWidth();
				cntrl++;
			}
			if (noOfLinesPrintedOnPage == 1) {
				al = getColumnHeaderReportFileForCvpBill(columnHeading);
			} else {
				al = new ArrayList();
			}
			al.add(sbLine.toString());
			objBody.addAll(al);

			/* Report Detail Printing */
			boolean isGrouping = false;
			boolean isTotal = false;
			List<ColumnVo> alDisplay = new ArrayList<ColumnVo>();
			List l1 = new ArrayList();

			for (int i = 0; i < columnHeading.length; i++) {
				ColumnVo key = columnHeading[i];
				l1.add(key);
				if (key.isGroupingOrder()) {
					isGrouping = true;
				} else {
					alDisplay.add(key);
				}
				if (key.getDisplayTotal() == 1) {
					isTotal = true;
				}
			}

			nColumns = alDisplay.size();
			if (isGrouping) {
				reportData = getGroupingHelper(reportData, l1);
			}

			BigDecimal[] bgTotalAmt = new BigDecimal[nColumns];
			for (int i = 0; i < nColumns; i++) {
				bgTotalAmt[i] = BigDecimal.ZERO;
			}

			for (Object row : reportData) {
				StringBuffer sb = new StringBuffer();
				List[] arrangedFields = new ArrayList[nColumns];
				int maxRow = 1;
				List cols = (ArrayList) row;

				for (int j = 0; j < cols.size(); j++) {

					String sColumnValue = (cols.get(j) != null ? cols.get(j).toString() : "");

					if (isGrouping == true && "GTOTAL".equals(sColumnValue)) {
						objBody.add(sb.toString());
						sColumnValue = "Total";
					}
					if (isGrouping && "2GRP".equals(sColumnValue)) {
						objBody.add(sb.toString());
						sColumnValue = " ";
					}
					// ColumnVo columnVo = columnHeading[j];
					ColumnVo columnVo = alDisplay.get(j);
					if (columnVo.getDisplayTotal() == 1 && sColumnValue != null && !sColumnValue.equals("")) {
						bgTotalAmt[j] = bgTotalAmt[j].add(new BigDecimal(sColumnValue));
					}
					if (columnVo.isAllowWrap()) {
						if (columnVo.getAlignMent() == 3) {
							arrangedFields[j] = arrangeStringCenter(sColumnValue.trim(), columnVo.getRealWidth());
						} else {
							arrangedFields[j] = arrangeString(sColumnValue.trim(), columnVo.getRealWidth());
						}

						if (arrangedFields[j].size() > maxRow) {
							maxRow = arrangedFields[j].size();
						}
					} else {
						try {
							if (columnVo.isCurrancyFormated()) {
								sColumnValue = nf.format(new BigDecimal(sColumnValue));
							}
						} catch (Exception e) {
						}

						if (columnVo.getAlignMent() == 1) {

							arrangedFields[j] = new ArrayList();
							arrangedFields[j].add(String.format("%-" + columnVo.getWidth() + "s", sColumnValue));
						} else if (columnVo.getAlignMent() == 2) {
							arrangedFields[j] = new ArrayList();
							arrangedFields[j].add(String.format("%" + columnVo.getWidth() + "s", sColumnValue));
						} else {
							arrangedFields[j] = new ArrayList();
							arrangedFields[j].add(getCenterAlign(sColumnValue, columnVo.getWidth()));
						}

					}
				}
				for (int rCnt = 0; rCnt < maxRow; rCnt++) {
					for (int nCnt = 0; nCnt < arrangedFields.length; nCnt++) {
						ColumnVo columnVo = alDisplay.get(nCnt);
						if (arrangedFields[nCnt].size() > rCnt) {
							sb.append(arrangedFields[nCnt].get(rCnt).toString() + SEPARATOR);
						} else {
							sb.append(String.format("%" + columnVo.getWidth() + "s", " ") + SEPARATOR);
							// sb.append(String.format("%230s"," ").substring(0,nColumnsActualSize[nCnt])
							// + SEPARATOR );
						}
					}
					objBody.add(sb.toString());
					sb = new StringBuffer();
				}
			} // end of for row formatttion

			/* Display Total */
			if (isTotal) {
				objBody.add(sbLine.toString());
				StringBuffer sb = new StringBuffer();
				boolean isDisplayTotal = false;
				for (int t = 0; t < nColumns; t++) {
					ColumnVo columnVo = columnHeading[t];
					if (columnVo.getDisplayTotal() == 1) {
						if (columnVo.isCurrancyFormated()) {
							sb.append(String.format("%" + (columnVo.getWidth() + 1) + "s", nf.format(bgTotalAmt[t])));
						} else {
							sb.append(String.format("%" + (columnVo.getWidth() + 1) + "s", bgTotalAmt[t]));
						}
					} else {
						if (columnVo.getTotalLabel() != null) {
							sb.append(String.format("%-" + (columnVo.getWidth()) + "s", columnVo.getTotalLabel()));
						} else {
							if (t == 0) {
								isDisplayTotal = true;
								sb.append(String.format("%-" + columnVo.getWidth() + "s", ""));
							} else {
								sb.append(String.format("%" + (columnVo.getWidth() + 1) + "s", " "));
							}
						}
					}

				}
				if (isDisplayTotal == true) {
					objBody.add(sb != null ? sb.replace(0, 5, "Total").toString() : "");
				} else {
					objBody.add(sb != null ? sb.toString() : "");
				}
			}
			/* Display Total Over */

			objBody.add(sbLine.toString());
			int nPageNumber = startPageNumber;
			// int nLinesinPage = 1;
			int nLinesinPage = noOfLinesPrintedOnPage; // For sub reports if two
														// reports are coming on
														// same page then
														// header,page,time etc
														// should be displayed
														// only once.So
														// assigning no. of
														// lines
														// that are already
														// printed on the
														// current page due to
														// sub report.

			for (String strLine : objBody) {
				if (nLinesinPage % NUM_ROW == 0) {
					fos.append(ff.toString());
					nLinesinPage = 1;
					noOfLines = 1;
					if (showPageNumber) {
						fos.append(LINE_SEP);
						fos.append((ReportGenerationTime + String.format("%" + (NUM_COLUMNS - 20) + "s", "Page: " + (nPageNumber++))));
						fos.append(LINE_SEP);
					}
					if (nPageNumber > 1) {
						for (Object subHeading : alReportTitle) {

							fos.append((subHeading.toString() + LINE_SEP));
							nLinesinPage++;
							noOfLines++;

						}
						for (Object colHead : al) {
							fos.append((colHead + LINE_SEP));
							nLinesinPage++;
							noOfLines++;
						}
					}
				} else if (nLinesinPage == 1 && showPageNumber) {
					fos.append(LINE_SEP);
					fos.append((ReportGenerationTime + String.format("%" + (NUM_COLUMNS - 20) + "s", "Page: " + (nPageNumber++))));
					fos.append(LINE_SEP);

				}
				if (strLine.equals(ff.toString())) {
					nLinesinPage = 1;
					noOfLines = 1;
				} else {
					nLinesinPage++;
					noOfLines++;
				}

				fos.append((strLine + LINE_SEP));
			}
			totalPages = nPageNumber;
			fos.append("\r\n" + Footer);
			return fos.toString();
		} catch (Exception e) {
			logger.error("Exception in ReportExportHelper.getReportFile:: " + e, e);
		}
		return null;
	}

	private static List getColumnHeaderReportFileForCvpBill(ColumnVo[] rData) {

		List objHeading = new ArrayList();
		int nColumns = rData.length;
		int maxRow = 1;

		List[] arrangedHeads = new ArrayList[nColumns];
		int width[] = new int[nColumns];

		for (int j = 0; j < nColumns; j++) {
			ColumnVo key = rData[j];
			String sColumnValue = key.getColumnName();
			width[j] = key.getWidth();
			arrangedHeads[j] = arrangeString(sColumnValue, key.getWidth());
			if (arrangedHeads[j].size() > maxRow) {
				maxRow = arrangedHeads[j].size();
			}
		}
		StringBuffer sbHead = new StringBuffer();
		for (int rCnt = 0; rCnt < maxRow; rCnt++) {
			for (int nCnt = 0; nCnt < arrangedHeads.length; nCnt++) {
				if (arrangedHeads[nCnt].size() > rCnt) {

					sbHead.append(arrangedHeads[nCnt].get(rCnt).toString().replaceAll("$", "") + "|");
				} else {
					sbHead.append(String.format("%" + width[nCnt] + "s", "") + "|");
				}
			}
			objHeading.add(sbHead.toString());
			sbHead = new StringBuffer();
		}
		return objHeading;
	}

	public String getReportFileWithPageBreakBefore7610(final List rMainData, ColumnVo[] columnHeading, final String Header, String Footer, final String strParameterString, int nMode,
			boolean pageBreakonGrouping)

	{

		try {
			int nColumns = 0;
			nColumns = columnHeading.length;
			int nColumnsSize[] = null;
			// int nColumnsActualSize[] =null;
			String LINE_SEP = "\r\n";
			String SEPARATOR = " ";

			String ReportGenerationTime = ReportHelper.oDateTimeFormat.format((new Date()));
			char c = 12;
			StringBuffer ff = new StringBuffer();
			ff.append(c);
			NUM_COLUMNS = nMode;
			LinkedList<String> objBody = new LinkedList<String>();
			LinkedList<String> objParameters = new LinkedList<String>();
			List al = null;
			StringBuilder sbLine = new StringBuilder(NUM_COLUMNS);
			for (int i = 0; i < NUM_COLUMNS; i++) {
				sbLine.append('-');
			}

			StringBuffer fos = new StringBuffer();

			/* Heading and Parameter String Display */

			List<String> alReportTitle = new ArrayList<String>();
			String sTokens[] = Header.split("\r\n");
			for (String sTitle : sTokens) {
				objBody.add(getCenterAlign(sTitle.trim(), NUM_COLUMNS));
				alReportTitle.add(getCenterAlign(sTitle.trim(), NUM_COLUMNS));
			}

			if (strParameterString != null) {
				StringBuilder sParamter = new StringBuilder();
				String tokens[] = strParameterString.split(",");
				for (String sToken : tokens) {
					/*
					 * if( (sToken.length() + sParamter.length()) < NUM_COLUMNS)
					 * sParamter.append(sToken + SEPARATOR); else
					 * objParameters.add(sToken + SEPARATOR );
					 */
					if (sToken != null && sToken.indexOf('\n') > 0) {
						objParameters.add(sToken.toString().trim());
					} else {
						objParameters.add(getCenterAlign(sToken.toString().trim(), nMode));
					}
				}

			}
			alReportTitle.addAll(objParameters);
			alReportTitle.add(sbLine.toString());

			objBody.addAll(objParameters);
			objBody.add(sbLine.toString());
			List reportData = rMainData;

			nColumnsSize = new int[columnHeading.length];
			int cntrl = 0;
			for (Object key : columnHeading) {
				ColumnVo columnvo = (ColumnVo) key;
				nColumnsSize[cntrl] = columnvo.getWidth();
				cntrl++;
			}

			al = getColumnHeaderReportFile(columnHeading);
			al.add(sbLine.toString());
			objBody.addAll(al);

			/* Report Detail Printing */
			boolean isGrouping = false;
			boolean isTotal = false;
			List<ColumnVo> alDisplay = new ArrayList<ColumnVo>();
			List l1 = new ArrayList();

			for (int i = 0; i < columnHeading.length; i++) {
				ColumnVo key = columnHeading[i];
				l1.add(key);
				if (key.isGroupingOrder()) {
					isGrouping = true;
				} else {
					alDisplay.add(key);
				}
				if (key.getDisplayTotal() == 1) {
					isTotal = true;
				}
			}

			nColumns = alDisplay.size();
			if (isGrouping) {
				reportData = getGroupingHelper(reportData, l1);
			}

			BigDecimal[] bgTotalAmt = new BigDecimal[nColumns];
			for (int i = 0; i < nColumns; i++) {
				bgTotalAmt[i] = BigDecimal.ZERO;
			}

			for (Object row : reportData) {
				StringBuffer sb = new StringBuffer();
				List[] arrangedFields = new ArrayList[nColumns];
				int maxRow = 1;
				List cols = (ArrayList) row;

				for (int j = 0; j < cols.size(); j++) {

					String sColumnValue = (cols.get(j) != null ? cols.get(j).toString() : "");

					if (isGrouping == true && "GTOTAL".equals(sColumnValue)) {
						objBody.add(sb.toString());
						sColumnValue = "Total";
					}
					if (isGrouping && "2GRP".equals(sColumnValue)) {
						objBody.add(sb.toString());
						sColumnValue = " ";
					}
					// ColumnVo columnVo = columnHeading[j];
					ColumnVo columnVo = alDisplay.get(j);
					if (columnVo.getDisplayTotal() == 1 && sColumnValue != null && !sColumnValue.equals("")) {
						bgTotalAmt[j] = bgTotalAmt[j].add(new BigDecimal(sColumnValue));
					}
					if (columnVo.isAllowWrap()) {
						if (columnVo.getAlignMent() == 3) {
							arrangedFields[j] = arrangeStringCenter(sColumnValue.trim(), columnVo.getRealWidth());
						} else {
							arrangedFields[j] = arrangeString(sColumnValue.trim(), columnVo.getRealWidth());
						}

						if (arrangedFields[j].size() > maxRow) {
							maxRow = arrangedFields[j].size();
						}
					} else {
						try {
							if (columnVo.isCurrancyFormated()) {
								sColumnValue = nf.format(new BigDecimal(sColumnValue));
							}
						} catch (Exception e) {
						}

						if (columnVo.getAlignMent() == 1) {

							arrangedFields[j] = new ArrayList();
							arrangedFields[j].add(String.format("%-" + columnVo.getWidth() + "s", sColumnValue));
						} else if (columnVo.getAlignMent() == 2) {
							arrangedFields[j] = new ArrayList();
							arrangedFields[j].add(String.format("%" + columnVo.getWidth() + "s", sColumnValue));
						} else {
							arrangedFields[j] = new ArrayList();
							arrangedFields[j].add(getCenterAlign(sColumnValue, columnVo.getWidth()));
						}

					}
				}
				for (int rCnt = 0; rCnt < maxRow; rCnt++) {
					for (int nCnt = 0; nCnt < arrangedFields.length; nCnt++) {
						ColumnVo columnVo = alDisplay.get(nCnt);
						if (arrangedFields[nCnt].size() > rCnt) {
							sb.append(arrangedFields[nCnt].get(rCnt).toString() + SEPARATOR);
						} else {
							sb.append(String.format("%" + columnVo.getWidth() + "s", " ") + SEPARATOR);
							// sb.append(String.format("%230s"," ").substring(0,nColumnsActualSize[nCnt])
							// + SEPARATOR );
						}
					}
					objBody.add(sb.toString());
					sb = new StringBuffer();
				}
			} // end of for row formatttion

			/* Display Total */
			if (isTotal) {
				objBody.add(sbLine.toString());
				StringBuffer sb = new StringBuffer();
				boolean isDisplayTotal = false;
				for (int t = 0; t < nColumns; t++) {
					ColumnVo columnVo = columnHeading[t];
					if (columnVo.getDisplayTotal() == 1) {
						if (columnVo.isCurrancyFormated()) {
							sb.append(String.format("%" + (columnVo.getWidth() + 1) + "s", nf.format(bgTotalAmt[t])));
						} else {
							sb.append(String.format("%" + (columnVo.getWidth() + 1) + "s", bgTotalAmt[t]));
						}
					} else {
						if (columnVo.getTotalLabel() != null) {
							sb.append(String.format("%-" + (columnVo.getWidth()) + "s", columnVo.getTotalLabel()));
						} else {
							if (t == 0) {
								isDisplayTotal = true;
								sb.append(String.format("%-" + columnVo.getWidth() + "s", ""));
							} else {
								sb.append(String.format("%" + (columnVo.getWidth() + 1) + "s", " "));
							}
						}
					}

				}
				if (isDisplayTotal == true) {
					objBody.add(sb != null ? sb.replace(0, 5, "Total").toString() : "");
				} else {
					objBody.add(sb != null ? sb.toString() : "");
				}
			}
			/* Display Total Over */

			objBody.add(sbLine.toString());
			int nPageNumber = startPageNumber;
			int nLinesinPage = 1;

			for (String strLine : objBody) {
				if ((nLinesinPage % NUM_ROW == 0) || (strLine.startsWith("7610 - "))) {
					fos.append(ff.toString());
					nLinesinPage = 1;
					noOfLines = 1;
					if (showPageNumber) {
						fos.append(LINE_SEP);
						fos.append((ReportGenerationTime + String.format("%" + (NUM_COLUMNS - 20) + "s", "Page: " + (nPageNumber++))));
						fos.append(LINE_SEP);
					}
					if (nPageNumber > 1) {
						for (Object subHeading : alReportTitle) {

							fos.append((subHeading.toString() + LINE_SEP));
							nLinesinPage++;
							noOfLines++;

						}
						for (Object colHead : al) {
							fos.append((colHead + LINE_SEP));
							nLinesinPage++;
							noOfLines++;
						}
					}
				} else if (nLinesinPage == 1 && showPageNumber) {
					fos.append(LINE_SEP);
					fos.append((ReportGenerationTime + String.format("%" + (NUM_COLUMNS - 20) + "s", "Page: " + (nPageNumber++))));
					fos.append(LINE_SEP);

				}
				if (strLine.equals(ff.toString())) {
					nLinesinPage = 1;
					noOfLines = 1;
				} else {
					nLinesinPage++;
					noOfLines++;
				}

				fos.append((strLine + LINE_SEP));
			}
			totalPages = nPageNumber;
			fos.append("\r\n" + Footer);
			return fos.toString();
		} catch (Exception e) {
			logger.error("Exception in ReportExportHelper.getReportFile:: " + e, e);
		}
		return null;
	}

	public String getMulipalePage(List mainData, ColumnVo[] cvo, String Header, String Footer, String strParameterString, int mode) {

		int staticWidth = 0;
		int numPages = 0;
		int totalWidth = 0;

		String LINE_SEP = "\r\n";
		String SEPARATOR = " ";
		StringBuilder sbLine = new StringBuilder(mode);
		for (int i = 0; i < mode; i++) {
			sbLine.append('-');
		}
		String ReportGenerationTime = ReportHelper.oDateTimeFormat.format((new Date()));

		// Report Tittle Starts
		List<String> alReportTitle = new ArrayList<String>();
		List<String> objParameters = new ArrayList<String>();

		String sTokens[] = Header.split("\r\n");
		for (String sTitle : sTokens) {
			alReportTitle.add(getCenterAlign(sTitle.trim(), mode));
		}
		if (strParameterString != null) {
			StringBuilder sParamter = new StringBuilder();
			String tokens[] = strParameterString.split(",");
			for (String sToken : tokens) {
				if ((sToken.length() + sParamter.length()) < mode) {
					sParamter.append(sToken + SEPARATOR);
				} else {
					objParameters.add(sToken + SEPARATOR);
				}
			}
			objParameters.add(getCenterAlign(sParamter.toString().trim(), mode));
		}
		alReportTitle.addAll(objParameters);
		// alReportTitle.add(sbLine.toString());
		// Report Tittle Over

		char c = 12;
		StringBuffer ff = new StringBuffer();
		ff.append(c);
		NUM_COLUMNS = mode;

		List noOfColumn = new ArrayList();
		int colCount = 0;
		boolean isTotal = false;
		for (ColumnVo cv : cvo) {
			if (cv.isStatic) {
				staticWidth += cv.getWidth();
			}

			if ((totalWidth + cv.getWidth()) >= mode) {
				numPages++;
				noOfColumn.add(colCount);
				colCount = 1;
				totalWidth = staticWidth;
			}
			totalWidth += cv.getWidth();
			colCount++;

			if (cv.getDisplayTotal() == 1) {
				isTotal = true;
			}

		}
		if (staticWidth <= totalWidth) {
			noOfColumn.add(colCount);
			numPages++;
		}
		List oColHeadOfPage = new ArrayList();

		/* Report Heading Array */
		totalWidth = 0;
		List<ColumnVo> temp = new ArrayList<ColumnVo>();
		List<ColumnVo> staticColList = new ArrayList<ColumnVo>();
		for (ColumnVo key : cvo) {

			if ((totalWidth + key.getWidth()) > mode) {
				List<ColumnVo> lst = new ArrayList<ColumnVo>();
				lst.addAll(staticColList);
				lst.addAll(temp);

				List<String> colHead = getColumnHeader(lst.toArray(new ColumnVo[lst.size()]));

				colHead.add(0, sbLine.toString());
				colHead.add(sbLine.toString());
				oColHeadOfPage.add(colHead);

				temp = new ArrayList<ColumnVo>();
				colHead = new ArrayList();
				totalWidth = staticWidth;
			}

			totalWidth += key.getWidth();
			if (key.isStatic) {
				staticColList.add(key);
			} else {
				temp.add(key);
			}
		}

		if (temp.isEmpty() == false) {
			List<ColumnVo> lst = new ArrayList<ColumnVo>();
			lst.addAll(staticColList);
			lst.addAll(temp);
			List<String> colHead = getColumnHeader(lst.toArray(new ColumnVo[lst.size()]));
			colHead.add(0, sbLine.toString());
			colHead.add(sbLine.toString());
			oColHeadOfPage.add(colHead);
		}

		/* Report Heading Array */

		StringBuffer fos = new StringBuffer();
		List newBody = new ArrayList();
		List oFormattedBody = new ArrayList();
		totalWidth = 0;
		for (Object row : mainData) {
			List cols = (ArrayList) row;

			List rowPage = new ArrayList();
			List staticData = new ArrayList();
			List tempPages = new ArrayList();
			totalWidth = 0;
			for (int i = 0; i < cvo.length; i++) {

				if ((totalWidth + cvo[i].getWidth()) >= mode) {
					List alt = new ArrayList();
					alt.addAll(staticData);
					alt.addAll(rowPage);
					tempPages.add(alt);
					rowPage = new ArrayList();
					totalWidth = staticWidth;
				}
				totalWidth += cvo[i].getWidth();
				if (cvo[i].isStatic) {
					staticData.add(cols.get(i));
				} else {
					if (cols.size() > i) {
						rowPage.add(cols.get(i));
					}
				}

			}
			if (totalWidth > staticWidth) {
				List alt = new ArrayList();
				alt.addAll(staticData);
				alt.addAll(rowPage);
				tempPages.add(alt);
				rowPage = new ArrayList();
				totalWidth = staticWidth;
			}
			newBody.add(tempPages);
		}
		BigDecimal[][] bgTotalAmt = new BigDecimal[numPages][cvo.length];
		BigDecimal[] bgAmtTotal = new BigDecimal[cvo.length];
		List[] total = new ArrayList[numPages];
		List[] lineAftTotal = new ArrayList[numPages];
		for (int t = 0; t < numPages; t++) {
			for (int i = 0; i < cvo.length; i++) {
				bgTotalAmt[t][i] = BigDecimal.ZERO;
				bgAmtTotal[i] = BigDecimal.ZERO;
			}
		}
		for (Object row : newBody) {
			StringBuffer sb = new StringBuffer();

			List colsPages = (ArrayList) row;
			List[] outPages = new ArrayList[colsPages.size()];

			// BigDecimal[] bgArrTotalAmt = new BigDecimal[cvo.length];

			for (int i = 0; i < outPages.length; i++) {
				outPages[i] = new ArrayList();
			}
			for (int i = 0; i < total.length; i++) {
				total[i] = new ArrayList();
			}
			for (int i = 0; i < lineAftTotal.length; i++) {
				lineAftTotal[i] = new ArrayList();
			}

			int maxRow = 1;

			for (int i = 0; i < colsPages.size(); i++) {
				List cols = (ArrayList) colsPages.get(i);
				List[] arrangedFields = new ArrayList[cols.size()];

				for (int j = 0; j < cols.size(); j++) {
					String sColumnValue = cols.get(j) != null ? cols.get(j).toString() : "";
					ColumnVo columnVo = cvo[j];
					if (columnVo.getDisplayTotal() == 1 && sColumnValue != null && !sColumnValue.trim().equals("")) {
						bgTotalAmt[i][j] = bgTotalAmt[i][j].add(new BigDecimal(sColumnValue));
					}
					if (columnVo.isAllowWrap()) {
						if (columnVo.getAlignMent() == 3) {
							arrangedFields[j] = arrangeStringCenter(sColumnValue.trim(), columnVo.getRealWidth());
						} else {
							arrangedFields[j] = arrangeString(sColumnValue.trim(), columnVo.getRealWidth());
						}

						if (arrangedFields[j].size() > maxRow) {
							maxRow = arrangedFields[j].size();
						}
					} else {
						try {
							if (columnVo.isCurrancyFormated()) {
								sColumnValue = nf.format(Double.valueOf(sColumnValue));
							}
						} catch (Exception e) {
						}

						if (columnVo.getAlignMent() == 1) {

							arrangedFields[j] = new ArrayList();
							arrangedFields[j].add(String.format("%-" + columnVo.getRealWidth() + "s", sColumnValue));
						} else if (columnVo.getAlignMent() == 2) {
							arrangedFields[j] = new ArrayList();
							arrangedFields[j].add(String.format("%" + columnVo.getRealWidth() + "s", sColumnValue));
						} else {
							arrangedFields[j] = new ArrayList();
							arrangedFields[j].add(getCenterAlign(sColumnValue, columnVo.getRealWidth()));
						}
					}
				}
				for (int rCnt = 0; rCnt < maxRow; rCnt++) {
					for (int nCnt = 0; nCnt < arrangedFields.length; nCnt++) {
						ColumnVo columnVo = cvo[nCnt];
						if (arrangedFields[nCnt].size() > rCnt) {
							sb.append(arrangedFields[nCnt].get(rCnt).toString() + SEPARATOR);
						} else {
							sb.append(String.format("%" + columnVo.getRealWidth() + "s", " ") + SEPARATOR);
						}
					}
					outPages[i].add(sb.toString());
					sb = new StringBuffer();
				}
			}
			oFormattedBody.add(outPages);
		} // end of for row formatttion
		int staticCount = -1;
		if (isTotal) {
			if (newBody != null && !newBody.isEmpty()) {
				for (int k = 0; k < noOfColumn.size(); k++) {
					StringBuffer sb = new StringBuffer();
					BigDecimal bgAmtArr[] = bgTotalAmt[k];
					if (k == 0) {
						for (int nCnt = 0; nCnt < Integer.parseInt(noOfColumn.get(k).toString()); nCnt++) {
							ColumnVo columnVo = cvo[nCnt];
							if (columnVo.isStatic) {
								staticCount++;
							}
							if (nCnt == 0) {
								sb.append(String.format("%-" + columnVo.getRealWidth() + "s", ""));
							} else {
								if (columnVo.getDisplayTotal() == 1) {
									sb.append(String.format("%" + (columnVo.getWidth()) + "s", nf.format(bgAmtArr[nCnt])));
								} else {
									sb.append(String.format("%" + (columnVo.getWidth()) + "s", " "));
								}
							}
						}
					} else {
						for (int nCnt = 0; nCnt < staticCount + Integer.parseInt(noOfColumn.get(k).toString()); nCnt++) {
							ColumnVo columnVo = cvo[nCnt];
							if (nCnt == 0) {
								sb.append(String.format("%-" + columnVo.getRealWidth() + "s", ""));
							} else {
								if (columnVo.getDisplayTotal() == 1) {
									sb.append(String.format("%" + (columnVo.getWidth()) + "s", nf.format(bgAmtArr[nCnt])));
								} else {
									sb.append(String.format("%" + (columnVo.getWidth()) + "s", " "));
								}
							}
						}
					}

					total[k].add((sb != null ? sb.replace(0, 5, "Total").toString() : ""));
					lineAftTotal[k].add(sbLine.toString());
				}
			}
			oFormattedBody.add(lineAftTotal);
			oFormattedBody.add(total);
		}
		oFormattedBody.add(lineAftTotal);

		int nLinesinPage = 1, nPageNumber = startPageNumber;
		noOfLines = 1;

		fos.append((ReportGenerationTime + String.format("%" + (NUM_COLUMNS - 20) + "s", "Page: " + (nPageNumber))));
		fos.append(LINE_SEP);

		boolean firstRecord = true;
		StringBuffer sbOutput = new StringBuffer();
		StringBuffer[] sbPages = null;
		int pages = 0;
		for (Object key : oFormattedBody) {

			List[] formattedRow = (ArrayList[]) key;

			if (firstRecord) {
				// List lst = (ArrayList)oColHeadOfPage.get(0);
				// nLinesinPage = lst.size();
				if (formattedRow != null && formattedRow.length > 0) {
					pages = formattedRow.length;
				}
				sbPages = new StringBuffer[pages];
				for (int i = 0; i < sbPages.length; i++) {
					sbPages[i] = new StringBuffer();
				}

				firstRecord = false;
			}

			for (int i = 0; i < pages; i++) {
				if (formattedRow[i] != null) {
					for (Object fRow : formattedRow[i]) {
						sbPages[i].append(fRow.toString() + LINE_SEP);
						if (i == 0) {
							nLinesinPage++;
							noOfLines++;
						}
					}
				}
			}

			if (nLinesinPage > NUM_ROW) {

				nLinesinPage = 1;
				noOfLines = 1;

				for (int i = 0; i < pages; i++) {
					sbOutput.append(LINE_SEP);
					sbOutput.append((ReportGenerationTime + String.format("%" + (mode - 20) + "s", "Page: " + (nPageNumber++))));
					sbOutput.append(LINE_SEP);
					for (Object oColHead : alReportTitle) {
						sbOutput.append(oColHead.toString() + LINE_SEP);
					}
					List colHead = (ArrayList) oColHeadOfPage.get(i);
					for (Object oColHead : colHead) {
						sbOutput.append(oColHead.toString() + LINE_SEP);
					}

					sbOutput.append(sbPages[i].toString() + "\f");

					/*
					 * if(nPageNumber > 1) { for(Object subHeading :
					 * alReportTitle) {
					 * fos.append((subHeading+LINE_SEP).getBytes());
					 * nLinesinPage++; } for(Object colHead : al) {
					 * fos.append((colHead+LINE_SEP).getBytes());
					 * nLinesinPage++; } }
					 */
					firstRecord = true;
				}
			}
		}// main loop

		for (int i = 0; i < pages; i++) {
			sbOutput.append(LINE_SEP);
			sbOutput.append((ReportGenerationTime + String.format("%" + (mode - 20) + "s", "Page: " + (nPageNumber++))));
			sbOutput.append(LINE_SEP);

			for (Object oColHead : alReportTitle) {
				sbOutput.append(oColHead.toString() + LINE_SEP);
			}
			try {
				List colHead = (ArrayList) oColHeadOfPage.get(i);
				for (Object oColHead : colHead) {
					sbOutput.append(oColHead.toString() + LINE_SEP);
				}
			} catch (Exception e) {
				logger.error("Error Occured in:" + e, e);
			}

			sbOutput.append(sbPages[i].toString() + '\f');
		}
		if (!"".equals(Footer) && Footer != null) {
			// sbOutput.append(LINE_SEP);
			// sbOutput.append( (ReportGenerationTime +
			// String.format("%"+(mode-20)+ "s","Page: "+(nPageNumber++))));
			// sbOutput.append(LINE_SEP);
			sbOutput.delete(sbOutput.length() - 1, sbOutput.length());
			sbOutput.append(Footer + '\f');
		}
		totalPages = nPageNumber;
		return sbOutput.toString();
	}

	// added by vishwadeep for Receipt subsidiary register to solve printing
	// problem.

	public String getMulipalePageForReceiptSubReg(List mainData, ColumnVo[] cvo, String Header, String Footer, String strParameterString, int mode) {

		int staticWidth = 0;
		int numPages = 0;
		int totalWidth = 0;

		String LINE_SEP = "\r\n";
		String SEPARATOR = " ";
		StringBuilder sbLine = new StringBuilder(mode);
		for (int i = 0; i < mode; i++) {
			sbLine.append('-');
		}
		String ReportGenerationTime = ReportHelper.oDateTimeFormat.format((new Date()));

		// Report Tittle Starts
		List<String> alReportTitle = new ArrayList<String>();
		List<String> objParameters = new ArrayList<String>();

		String sTokens[] = Header.split("\r\n");
		for (String sTitle : sTokens) {
			alReportTitle.add(getCenterAlign(sTitle.trim(), mode));
		}
		if (strParameterString != null) {
			StringBuilder sParamter = new StringBuilder();
			String tokens[] = strParameterString.split(",");
			for (String sToken : tokens) {
				if ((sToken.length() + sParamter.length()) < mode) {
					sParamter.append(sToken + SEPARATOR);
				} else {
					objParameters.add(sToken + SEPARATOR);
				}
			}
			objParameters.add(getCenterAlign(sParamter.toString().trim(), mode));
		}
		alReportTitle.addAll(objParameters);
		// alReportTitle.add(sbLine.toString());
		// Report Tittle Over

		char c = 12;
		StringBuffer ff = new StringBuffer();
		ff.append(c);
		NUM_COLUMNS = mode;

		List noOfColumn = new ArrayList();
		int colCount = 0;
		boolean isTotal = false;
		for (ColumnVo cv : cvo) {
			if (cv.isStatic) {
				staticWidth += cv.getWidth();
			}

			if ((totalWidth + cv.getWidth()) >= mode) {
				numPages++;
				noOfColumn.add(colCount);
				colCount = 1;
				totalWidth = staticWidth;
			}
			totalWidth += cv.getWidth();
			colCount++;

			if (cv.getDisplayTotal() == 1) {
				isTotal = true;
			}

		}
		if (staticWidth <= totalWidth) {
			noOfColumn.add(colCount);
			numPages++;
		}
		List oColHeadOfPage = new ArrayList();

		/* Report Heading Array */
		totalWidth = 0;
		List<ColumnVo> temp = new ArrayList<ColumnVo>();
		List<ColumnVo> staticColList = new ArrayList<ColumnVo>();
		for (ColumnVo key : cvo) {

			if ((totalWidth + key.getWidth()) > mode) {
				List<ColumnVo> lst = new ArrayList<ColumnVo>();
				lst.addAll(staticColList);
				lst.addAll(temp);

				List<String> colHead = getColumnHeader(lst.toArray(new ColumnVo[lst.size()]));

				colHead.add(0, sbLine.toString());
				colHead.add(sbLine.toString());
				oColHeadOfPage.add(colHead);

				temp = new ArrayList<ColumnVo>();
				colHead = new ArrayList();
				totalWidth = staticWidth;
			}

			totalWidth += key.getWidth();
			if (key.isStatic) {
				staticColList.add(key);
			} else {
				temp.add(key);
			}
		}

		if (temp.isEmpty() == false) {
			List<ColumnVo> lst = new ArrayList<ColumnVo>();
			lst.addAll(staticColList);
			lst.addAll(temp);
			List<String> colHead = getColumnHeader(lst.toArray(new ColumnVo[lst.size()]));
			colHead.add(0, sbLine.toString());
			colHead.add(sbLine.toString());
			oColHeadOfPage.add(colHead);
		}

		/* Report Heading Array */

		StringBuffer fos = new StringBuffer();
		List newBody = new ArrayList();
		List oFormattedBody = new ArrayList();
		totalWidth = 0;
		for (Object row : mainData) {
			List cols = (ArrayList) row;

			List rowPage = new ArrayList();
			List staticData = new ArrayList();
			List tempPages = new ArrayList();
			totalWidth = 0;
			for (int i = 0; i < cvo.length; i++) {

				if ((totalWidth + cvo[i].getWidth()) >= mode) {
					List alt = new ArrayList();
					alt.addAll(staticData);
					alt.addAll(rowPage);
					tempPages.add(alt);
					rowPage = new ArrayList();
					totalWidth = staticWidth;
				}
				totalWidth += cvo[i].getWidth();
				if (cvo[i].isStatic) {
					staticData.add(cols.get(i));
				} else {
					if (cols.size() > i) {
						rowPage.add(cols.get(i));
					}
				}

			}
			if (totalWidth > staticWidth) {
				List alt = new ArrayList();
				alt.addAll(staticData);
				alt.addAll(rowPage);
				tempPages.add(alt);
				rowPage = new ArrayList();
				totalWidth = staticWidth;
			}
			newBody.add(tempPages);
		}
		BigDecimal[][] bgTotalAmt = new BigDecimal[numPages][cvo.length];
		BigDecimal[] bgAmtTotal = new BigDecimal[cvo.length];
		List[] total = new ArrayList[numPages];
		List[] lineAftTotal = new ArrayList[numPages];
		for (int t = 0; t < numPages; t++) {
			for (int i = 0; i < cvo.length; i++) {
				bgTotalAmt[t][i] = BigDecimal.ZERO;
				bgAmtTotal[i] = BigDecimal.ZERO;
			}
		}
		for (Object row : newBody) {
			StringBuffer sb = new StringBuffer();

			List colsPages = (ArrayList) row;
			List[] outPages = new ArrayList[colsPages.size()];

			// BigDecimal[] bgArrTotalAmt = new BigDecimal[cvo.length];

			for (int i = 0; i < outPages.length; i++) {
				outPages[i] = new ArrayList();
			}
			for (int i = 0; i < total.length; i++) {
				total[i] = new ArrayList();
			}
			for (int i = 0; i < lineAftTotal.length; i++) {
				lineAftTotal[i] = new ArrayList();
			}

			int maxRow = 1;

			for (int i = 0; i < colsPages.size(); i++) {
				List cols = (ArrayList) colsPages.get(i);
				List[] arrangedFields = new ArrayList[cols.size()];

				for (int j = 0; j < cols.size(); j++) {
					String sColumnValue = cols.get(j) != null ? cols.get(j).toString() : "";
					ColumnVo columnVo = cvo[j];
					if (columnVo.getDisplayTotal() == 1 && sColumnValue != null && !sColumnValue.trim().equals("")) {
						bgTotalAmt[i][j] = bgTotalAmt[i][j].add(new BigDecimal(sColumnValue));
					}
					if (columnVo.isAllowWrap()) {
						if (columnVo.getAlignMent() == 3) {
							arrangedFields[j] = arrangeStringCenter(sColumnValue.trim(), columnVo.getRealWidth());
						} else {
							arrangedFields[j] = arrangeString(sColumnValue.trim(), columnVo.getRealWidth());
						}

						if (arrangedFields[j].size() > maxRow) {
							maxRow = arrangedFields[j].size();
						}
					} else {
						try {
							if (columnVo.isCurrancyFormated()) {
								sColumnValue = nf.format(Double.valueOf(sColumnValue));
							}
						} catch (Exception e) {
						}

						if (columnVo.getAlignMent() == 1) {

							arrangedFields[j] = new ArrayList();
							arrangedFields[j].add(String.format("%-" + columnVo.getRealWidth() + "s", sColumnValue));
						} else if (columnVo.getAlignMent() == 2) {
							arrangedFields[j] = new ArrayList();
							arrangedFields[j].add(String.format("%" + columnVo.getRealWidth() + "s", sColumnValue));
						} else {
							arrangedFields[j] = new ArrayList();
							arrangedFields[j].add(getCenterAlign(sColumnValue, columnVo.getRealWidth()));
						}
					}
				}
				for (int rCnt = 0; rCnt < maxRow; rCnt++) {
					for (int nCnt = 0; nCnt < arrangedFields.length; nCnt++) {
						ColumnVo columnVo = cvo[nCnt];
						if (arrangedFields[nCnt].size() > rCnt) {
							sb.append(arrangedFields[nCnt].get(rCnt).toString() + SEPARATOR);
						} else {
							sb.append(String.format("%" + columnVo.getRealWidth() + "s", " ") + SEPARATOR);
						}
					}
					outPages[i].add(sb.toString());
					sb = new StringBuffer();
				}
			}
			oFormattedBody.add(outPages);
		} // end of for row formatttion
		int staticCount = -1;
		if (isTotal) {
			if (newBody != null && !newBody.isEmpty()) {
				for (int k = 0; k < noOfColumn.size(); k++) {
					StringBuffer sb = new StringBuffer();
					BigDecimal bgAmtArr[] = bgTotalAmt[k];
					if (k == 0) {
						for (int nCnt = 0; nCnt < Integer.parseInt(noOfColumn.get(k).toString()); nCnt++) {
							ColumnVo columnVo = cvo[nCnt];
							if (columnVo.isStatic) {
								staticCount++;
							}
							if (nCnt == 0) {
								sb.append(String.format("%-" + columnVo.getRealWidth() + "s", ""));
							} else {
								if (columnVo.getDisplayTotal() == 1) {
									sb.append(String.format("%" + (columnVo.getWidth()) + "s", nf.format(bgAmtArr[nCnt])));
								} else {
									sb.append(String.format("%" + (columnVo.getWidth()) + "s", " "));
								}
							}
						}
					} else {
						for (int nCnt = 0; nCnt < staticCount + Integer.parseInt(noOfColumn.get(k).toString()); nCnt++) {
							ColumnVo columnVo = cvo[nCnt];
							if (nCnt == 0) {
								sb.append(String.format("%-" + columnVo.getRealWidth() + "s", ""));
							} else {
								if (columnVo.getDisplayTotal() == 1) {
									sb.append(String.format("%" + (columnVo.getWidth()) + "s", nf.format(bgAmtArr[nCnt])));
								} else {
									sb.append(String.format("%" + (columnVo.getWidth()) + "s", " "));
								}
							}
						}
					}

					total[k].add((sb != null ? sb.replace(0, 5, "Total").toString() : ""));
					lineAftTotal[k].add(sbLine.toString());
				}
			}
			oFormattedBody.add(lineAftTotal);
			oFormattedBody.add(total);
		}
		oFormattedBody.add(lineAftTotal);

		int nLinesinPage = 1, nPageNumber = startPageNumber;
		noOfLines = 1;

		fos.append((ReportGenerationTime + String.format("%" + (NUM_COLUMNS - 20) + "s", "Page: " + (nPageNumber))));
		fos.append(LINE_SEP);

		boolean firstRecord = true;
		StringBuffer sbOutput = new StringBuffer();
		StringBuffer[] sbPages = null;
		int pages = 0;
		for (Object key : oFormattedBody) {

			List[] formattedRow = (ArrayList[]) key;

			if (firstRecord) {
				List lst = (ArrayList) oColHeadOfPage.get(0);
				nLinesinPage = lst.size();
				if (formattedRow != null && formattedRow.length > 0) {
					pages = formattedRow.length;
				}
				sbPages = new StringBuffer[pages];
				for (int i = 0; i < sbPages.length; i++) {
					sbPages[i] = new StringBuffer();
				}

				firstRecord = false;
			}

			for (int i = 0; i < pages; i++) {
				if (formattedRow[i] != null) {
					for (Object fRow : formattedRow[i]) {
						sbPages[i].append(fRow.toString() + LINE_SEP);
						if (i == 0) {
							nLinesinPage++;
							noOfLines++;
						}
					}
				}
			}

			if (nLinesinPage > NUM_ROW) {

				nLinesinPage = 1;
				noOfLines = 1;

				for (int i = 0; i < pages; i++) {
					sbOutput.append(LINE_SEP);
					sbOutput.append((ReportGenerationTime + String.format("%" + (mode - 20) + "s", "Page: " + (nPageNumber++))));
					sbOutput.append(LINE_SEP);
					for (Object oColHead : alReportTitle) {
						sbOutput.append(oColHead.toString() + LINE_SEP);
					}
					List colHead = (ArrayList) oColHeadOfPage.get(i);
					for (Object oColHead : colHead) {
						sbOutput.append(oColHead.toString() + LINE_SEP);
					}

					sbOutput.append(sbPages[i].toString() + "\f");

					/*
					 * if(nPageNumber > 1) { for(Object subHeading :
					 * alReportTitle) {
					 * fos.append((subHeading+LINE_SEP).getBytes());
					 * nLinesinPage++; } for(Object colHead : al) {
					 * fos.append((colHead+LINE_SEP).getBytes());
					 * nLinesinPage++; } }
					 */
					firstRecord = true;
				}
			}
		}// main loop

		for (int i = 0; i < pages; i++) {
			sbOutput.append(LINE_SEP);
			sbOutput.append((ReportGenerationTime + String.format("%" + (mode - 20) + "s", "Page: " + (nPageNumber++))));
			sbOutput.append(LINE_SEP);

			for (Object oColHead : alReportTitle) {
				sbOutput.append(oColHead.toString() + LINE_SEP);
			}
			try {
				List colHead = (ArrayList) oColHeadOfPage.get(i);
				for (Object oColHead : colHead) {
					sbOutput.append(oColHead.toString() + LINE_SEP);
				}
			} catch (Exception e) {
				logger.error("Error Occured in:" + e, e);
			}

			sbOutput.append(sbPages[i].toString() + '\f');
		}
		if (!"".equals(Footer) && Footer != null) {
			// sbOutput.append(LINE_SEP);
			// sbOutput.append( (ReportGenerationTime +
			// String.format("%"+(mode-20)+ "s","Page: "+(nPageNumber++))));
			// sbOutput.append(LINE_SEP);
			sbOutput.delete(sbOutput.length() - 1, sbOutput.length());
			sbOutput.append(Footer + '\f');
		}
		totalPages = nPageNumber;
		return sbOutput.toString();
	}

	/* Arrange Head for Center Alignment */
	private static List arrangeStringCenter(String strValue, int Width) {

		List al = new ArrayList();
		StringTokenizer oTokenizer = new StringTokenizer(strValue, " ");
		String LastLine = "";
		while (oTokenizer.hasMoreElements()) {
			String strToken = oTokenizer.nextToken();
			if (strToken.length() >= Width) {
				List oSpl = null;
				if (LastLine.equals("")) {
					oSpl = getSplitedString(strToken, Width);
				} else {
					oSpl = getSplitedString(LastLine + " " + strToken, Width);
				}
				for (int i = 0; i < oSpl.size() - 1; i++) {
					al.add(oSpl.get(i));
				}

				LastLine = oSpl.get(oSpl.size() - 1).toString().trim();
			} else {
				if ((LastLine.length() + strToken.length()) < Width) {
					if (!"".equals(LastLine)) {
						LastLine = LastLine + " " + strToken;
					} else {
						LastLine = strToken;
					}

				} else {
					al.add(getCenterAlign(LastLine.trim(), Width));
					LastLine = strToken;
				}
			}
		}
		if (!LastLine.equals("")) {
			al.add(getCenterAlign(LastLine, Width));
		}
		return al;
	}

	private static List arrangeString(String strValue, int Width) {

		List al = new ArrayList();
		StringTokenizer oTokenizer = null;
		if (strValue.indexOf("$") == -1) {
			oTokenizer = new StringTokenizer(strValue, " ");
		} else {
			oTokenizer = new StringTokenizer(strValue, "$");
		}

		String LastLine = "";
		while (oTokenizer.hasMoreElements()) {
			String strToken = oTokenizer.nextToken();
			if (strToken.length() >= Width) {
				List oSpl = null;
				if (LastLine.equals("")) {
					oSpl = getSplitedString(strToken, Width);
				} else {
					oSpl = getSplitedString(LastLine + " " + strToken, Width);
				}
				for (int i = 0; i < oSpl.size() - 1; i++) {
					al.add(oSpl.get(i));
				}

				LastLine = oSpl.get(oSpl.size() - 1).toString().trim();
			} else {
				if ((LastLine.length() + strToken.length()) < Width) {
					if (!"".equals(LastLine)) {
						LastLine = LastLine + " " + strToken;
					} else {
						LastLine = strToken;
					}

				} else {
					al.add(String.format("%-" + Width + "s", LastLine.trim()));
					LastLine = strToken;
				}
			}
		}
		if (!LastLine.equals("")) {
			al.add(String.format("%-" + Width + "s", LastLine));
		}
		return al;
	}

	private static List arrangeStringRight(String strValue, int Width) {

		List al = new ArrayList();
		StringTokenizer oTokenizer = new StringTokenizer(strValue, " ");
		String LastLine = "";
		while (oTokenizer.hasMoreElements()) {
			String strToken = oTokenizer.nextToken();
			if (strToken.length() >= Width) {
				List oSpl = null;
				if (LastLine.equals("")) {
					oSpl = getSplitedString(strToken, Width);
				} else {
					oSpl = getSplitedString(LastLine + " " + strToken, Width);
				}
				for (int i = 0; i < oSpl.size() - 1; i++) {
					al.add(oSpl.get(i));
				}

				LastLine = oSpl.get(oSpl.size() - 1).toString().trim();
			} else {
				if ((LastLine.length() + strToken.length()) < Width) {
					if (!"".equals(LastLine)) {
						LastLine = LastLine + " " + strToken;
					} else {
						LastLine = strToken;
					}

				} else {
					al.add(String.format("%" + Width + "s", LastLine.trim()));
					LastLine = strToken;
				}
			}
		}
		if (!LastLine.equals("")) {
			al.add(String.format("%" + Width + "s", LastLine));
		}
		return al;
	}

	public static List getGroupingHelper(List rData, List<ColumnVo> columns) {

		List<List> oReportBody = new ArrayList<List>();
		Map<Object, Object> hm = new TreeMap<Object, Object>();
		List<Integer> listGroupColumn = new ArrayList<Integer>();
		List<Integer> listGroupColumnDisplayValue = new ArrayList<Integer>();
		boolean disTotal = false;
		Map oFirstGroupValue = new HashMap();
		Map oSecondGroupValue = new HashMap();
		BigDecimal[] oTotals = new BigDecimal[columns.size()];
		int ind = 0;
		for (ColumnVo cvo : columns) {
			if (cvo.groupingOrder) {
				listGroupColumn.add(cvo.groupIndex);
				listGroupColumnDisplayValue.add(cvo.getGroupValueColumn());
			}
			if (cvo.displayTotal == 1) {
				disTotal = true;
			}

			oTotals[ind++] = BigDecimal.ZERO;
		}

		if (!listGroupColumn.isEmpty()) {
			Integer index = listGroupColumn.get(0);
			Integer disIndex = listGroupColumnDisplayValue.get(0);
			for (Object row : rData) {
				List values = (ArrayList) row;

				Object keyCol = values.get(index.intValue());
				String keyVal = values.get(disIndex.intValue()).toString();
				if (hm.containsKey(keyCol)) {
					List listValues = (ArrayList) hm.get(keyCol);
					listValues.add(row);
					hm.put(keyCol, listValues);
				} else {
					List listValues = new ArrayList();
					listValues.add(row);
					hm.put(keyCol, listValues);
					oFirstGroupValue.put(keyCol, keyVal);
				}
			}
		}
		if (listGroupColumn.size() > 1) {
			Integer index = listGroupColumn.get(1);
			Integer disIndex = listGroupColumnDisplayValue.get(1);
			for (Object key : hm.keySet()) {
				List subList = (ArrayList) hm.get(key);
				Map subMap = new HashMap();

				for (Object row : subList) {
					List values = (ArrayList) row;

					String keyCol = values.get(index.intValue()).toString();
					String keyVal = values.get(disIndex.intValue()).toString();

					if (subMap.containsKey(keyCol)) {
						List listValues = (ArrayList) subMap.get(keyCol);
						listValues.add(row);
						subMap.put(keyCol, listValues);
					} else {
						List listValues = new ArrayList();
						listValues.add(row);
						subMap.put(keyCol, listValues);
						oSecondGroupValue.put(keyCol, keyVal);
					}
				}
				hm.put(key, subMap);
			}
		}

		if (listGroupColumn.size() > 1) {
			for (Object key : hm.keySet()) {
				Map subMap = (HashMap) hm.get(key);
				List al = new ArrayList();

				al.add(oFirstGroupValue.get(key));
				oReportBody.add(al);

				for (Object subkey : subMap.keySet()) {
					List subList = new ArrayList();
					subList.add("2GRP");
					subList.add(oSecondGroupValue.get(subkey));
					oReportBody.add(subList);

					List list = (ArrayList) subMap.get(subkey);
					for (Object row : list) {
						List cols = (ArrayList) row;

						for (int i = 0; i < cols.size(); i++) {
							if (columns.get(i).displayTotal == 1) {
								oTotals[i] = oTotals[i].add(new BigDecimal(cols.get(i).toString()));
							}
						}
					}
					if (disTotal) {
						List groupTotal = new ArrayList();
						groupTotal.add("GTOTAL");
						for (BigDecimal total : oTotals) {
							groupTotal.add(total);
						}
						list.add(groupTotal);
					}
					oReportBody.addAll(list);
				}

			}

			for (List row : oReportBody) {
				for (int i = listGroupColumn.size() - 1; i >= 0; i--) {
					if (row.size() > 2) {
						row.remove(listGroupColumn.get(i).intValue());
					}
				}
			}
		}
		if (listGroupColumn.size() == 1) {
			for (Object key : hm.keySet()) {
				List al = new ArrayList();
				al.add(key);
				oReportBody.add(al);

				List list = (ArrayList) hm.get(key);
				for (Object row : list) {
					List cols = (ArrayList) row;
					for (int i = 0; i < cols.size(); i++) {
						if (columns.get(i).displayTotal == 1) {
							oTotals[i] = oTotals[i].add(new BigDecimal(cols.get(i).toString()));
						}
					}
				}
				// if(disTotal)
				// {
				List groupTotal = new ArrayList();
				groupTotal.add("GTOTAL");
				for (BigDecimal total : oTotals) {
					groupTotal.add(total);
				}
				list.add(groupTotal);
				// }
				oReportBody.addAll(list);

			}
			for (List row : oReportBody) {
				for (int i = listGroupColumn.size() - 1; i >= 0; i--) {
					if (row.size() > 2) {
						row.remove(listGroupColumn.get(i).intValue());
					}
				}
			}
		}
		return oReportBody;
	}

	private static List getColumnHeader(ColumnVo[] rData) {

		List objHeading = new ArrayList();
		int nColumns = rData.length;
		int maxRow = 1;

		List[] arrangedHeads = new ArrayList[nColumns];
		int width[] = new int[nColumns];

		for (int j = 0; j < nColumns; j++) {
			ColumnVo key = rData[j];
			String sColumnValue = key.getColumnName();
			width[j] = key.getRealWidth();
			if (key.alignMent == 2) {
				arrangedHeads[j] = arrangeStringRight(sColumnValue.trim(), key.getRealWidth());
			} else if (key.alignMent == 1) {
				arrangedHeads[j] = arrangeString(sColumnValue.trim(), key.getRealWidth());
			} else {
				arrangedHeads[j] = arrangeStringCenter(sColumnValue.trim(), key.getRealWidth());
			}
			if (arrangedHeads[j].size() > maxRow) {
				maxRow = arrangedHeads[j].size();
			}
		}
		StringBuffer sbHead = new StringBuffer();
		for (int rCnt = 0; rCnt < maxRow; rCnt++) {
			for (int nCnt = 0; nCnt < arrangedHeads.length; nCnt++) {
				if (arrangedHeads[nCnt].size() > rCnt) {
					sbHead.append(getCenterAlign(arrangedHeads[nCnt].get(rCnt).toString(), width[nCnt]) + " ");
				} else {
					sbHead.append(String.format("%" + width[nCnt] + "s", "") + " ");
				}
			}
			objHeading.add(sbHead.toString());
			sbHead = new StringBuffer();
		}
		return objHeading;
	}

	private static List getColumnHeaderReportFile(ColumnVo[] rData) {

		List objHeading = new ArrayList();
		int nColumns = rData.length;
		int maxRow = 1;

		List[] arrangedHeads = new ArrayList[nColumns];
		int width[] = new int[nColumns];

		for (int j = 0; j < nColumns; j++) {
			ColumnVo key = rData[j];
			String sColumnValue = key.getColumnName();
			width[j] = key.getWidth();
			arrangedHeads[j] = arrangeStringCenter(sColumnValue.trim(), key.getWidth());
			if (arrangedHeads[j].size() > maxRow) {
				maxRow = arrangedHeads[j].size();
			}
		}
		StringBuffer sbHead = new StringBuffer();
		for (int rCnt = 0; rCnt < maxRow; rCnt++) {
			for (int nCnt = 0; nCnt < arrangedHeads.length; nCnt++) {
				if (arrangedHeads[nCnt].size() > rCnt) {
					sbHead.append(getCenterAlign(arrangedHeads[nCnt].get(rCnt).toString(), width[nCnt]) + "|");
				} else {
					sbHead.append(String.format("%" + width[nCnt] + "s", "") + "|");
				}
			}
			objHeading.add(sbHead.toString());
			sbHead = new StringBuffer();
		}
		return objHeading;
	}

	private static List getSplitedString(String strValue, int Width) {

		List al = new ArrayList();
		int beginIndex = 0, endIndex = Width;
		strValue = strValue.trim();
		int sLength = strValue.length();
		String sFormat = "%-" + Width + "s";

		if (sLength <= Width) {
			al.add(String.format(sFormat, strValue));
			return al;
		}
		while (endIndex < sLength) {
			String token = strValue.substring(beginIndex, endIndex);
			al.add(String.format(sFormat, token));
			beginIndex = endIndex;
			endIndex = beginIndex + Width;
		}
		if (beginIndex < sLength) {
			al.add(String.format(sFormat, strValue.substring(beginIndex)));
		}

		return al;
	}

	public static String getSplitedStringinString(String strValue, int Width) {

		List al = new ArrayList();
		int beginIndex = 0, endIndex = Width;
		strValue = strValue.trim();
		int sLength = strValue.length();
		String sFormat = "%-" + Width + "s";
		String retStr = "";
		if (sLength <= Width) {
			retStr = String.format(sFormat, strValue);
			return retStr;
		}
		while (endIndex < sLength) {
			String token = strValue.substring(beginIndex, endIndex);
			al.add(String.format(sFormat, token));
			beginIndex = endIndex;
			endIndex = beginIndex + Width;
		}
		if (beginIndex < sLength) {
			al.add(String.format(sFormat, strValue.substring(beginIndex)));
		}
		int count = 1;
		for (Object obj : al) {
			if (count == 1) {
				retStr = obj.toString();
			} else {
				retStr = retStr + "\r\n" + obj.toString();
			}
			count++;
		}
		return retStr;

	}

	private static String getCenterAlign(String sValue, int length) {

		StringBuffer sb = new StringBuffer(length);
		int nStartPos = (length - sValue.length()) / 2;
		for (int i = 0; i < nStartPos; i++) {
			sb.append(" ");
		}
		sb.append(sValue);
		for (int i = sb.length(); i < length; i++) {
			sb.append(" ");
		}
		return sb.toString();
	}
	
	public void exportToTextFile(HttpServletRequest request, HttpServletResponse response, String output, String lStrFileName) throws UnsupportedEncodingException {

		String fileName = "reportFile.txt";
		if (lStrFileName != null && lStrFileName.length() > 0) {
			fileName = lStrFileName + ".txt";
		}
		response.setContentType("text/plain;charset=UTF-8");
		request.setAttribute("ExportedReportBytesArray", output.getBytes("UTF-8"));
		response.addHeader("Content-disposition", "attachment; filename=" + fileName);
		response.setCharacterEncoding("UTF-8");
	}

	public void getExportData(ResultObject resObj, String exportTo, Map objectArgs, Map output, boolean isCondence) throws Exception {

		HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
		HttpServletResponse response = (HttpServletResponse) objectArgs.get("responseObj");
		String lStrFileName = (String) objectArgs.get("FileName");
		if (exportTo.equals(DBConstants.DIS_TEXTFILE)) {
			String strOut = (String) output.get(DBConstants.DIS_TEXTFILE);
			new ReportExportHelper().exportToTextFile(request, response, strOut, lStrFileName);
			resObj.setViewName("ExportReportPage");
		}
		if (exportTo.equals(DBConstants.DIS_ONSCREEN)) {
			String strOut = (String) output.get(DBConstants.DIS_ONSCREEN);
			if (isCondence) {
				objectArgs.put("sOutput4Print", "<condence>" + strOut + "</condence>");
			} else {
				objectArgs.put("sOutput4Print", strOut);
			}
			resObj.setViewName("TextReport");
		}
		if (exportTo.equals(DBConstants.DIS_EXCELFILE)) {
			byte[] aryOut = (byte[]) output.get(DBConstants.DIS_EXCELFILE);
			ExcelExportHelper.exportToExcelFile(request, response, aryOut, lStrFileName);
			resObj.setViewName("ExportReportPage");
		}
		if (exportTo.equals(DBConstants.DIS_PDFFILE)) {
			byte[] aryOut = (byte[]) output.get(DBConstants.DIS_PDFFILE);
			// PDFExportHelper.exportToPdfFile(request, response, aryOut);
			resObj.setViewName("ExportReportPage");
		}
	}
}
