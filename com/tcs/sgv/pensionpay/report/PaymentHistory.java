/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jul 4, 2011		Shivani Rana								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.report;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.pensionpay.dao.PensionBillDAOImpl;


/**
 * Class Description -
 * 
 * 
 * @author Shivani Rana
 * @version 0.1
 * @since JDK 5.0 Jul 4, 2011
 */
public class PaymentHistory extends DefaultReportDataFinder {

	private static final Logger gLogger = Logger.getLogger(GenerateMandateReport.class);
	String Lang_Id = "en_US";
	String Loc_Id = "LC1";

	Map requestAttributes = null;
	ServiceLocator serviceLocator = null;
	SessionFactory lObjSessionFactory = null;
	ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionLabels");
	private ResourceBundle bundleBillConst = ResourceBundle.getBundle("resources/pensionpay/PensionConstants");
	Long gLngLocCode = null;

	public Collection findReportData(ReportVO report, Object criteria) throws ReportException {

		gLogger.info("inside reports function");
		gLogger.info("***********************Outward Register****************************************");

		String langId = report.getLangId();
		gLogger.info("\n\n LangId is " + langId);

		String locId = report.getLocId();
		gLogger.info("\n\n LocationId is " + locId);

		Connection con = null;
		criteria.getClass();

		Statement smt = null;
		ResultSet rs = null;
		ArrayList dataList = new ArrayList();
		new ReportsDAOImpl();
		List<Object[]> lLstResult = new ArrayList();

		StyleVO[] selfClose = new StyleVO[1];
		selfClose[0] = new StyleVO();
		selfClose[0].setStyleId(26);
		selfClose[0].setStyleValue("JavaScript:self.close();");

		// for Right Alignment format
		StyleVO[] RightAlignVO = new StyleVO[2];
		RightAlignVO[0] = new StyleVO();
		RightAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		RightAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
		RightAlignVO[1] = new StyleVO();
		RightAlignVO[1].setStyleId(IReportConstants.BORDER);
		RightAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

		// for Center Alignment format
		StyleVO[] CenterAlignVO = new StyleVO[2];
		CenterAlignVO[0] = new StyleVO();
		CenterAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		CenterAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
		CenterAlignVO[1] = new StyleVO();
		CenterAlignVO[1].setStyleId(IReportConstants.BORDER);
		CenterAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

		try {
			requestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
			serviceLocator = (ServiceLocator) requestAttributes.get("serviceLocator");
			lObjSessionFactory = serviceLocator.getSessionFactorySlave();
			PensionBillDAOImpl lObjPensionBillDAOImpl = new PensionBillDAOImpl(serviceLocator.getSessionFactory());
			Map requestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
			ServiceLocator serviceLocator = (ServiceLocator) requestAttributes.get("serviceLocator");
			SessionFactory lObjSessionFactory = serviceLocator.getSessionFactorySlave();
			con = lObjSessionFactory.getCurrentSession().connection();
			smt = con.createStatement();
			gLogger.info("***********************11****************************************");
			Map sessionKeys = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
			Map loginDetail = (HashMap) sessionKeys.get("loginDetailsMap");

			gLogger.info("Logindetails contents of map :" + loginDetail);
			gLogger.info("***********************loginDetail****************************************" + loginDetail);
			// LoginDetails objLoginDetails =
			// (LoginDetails)loginDetail.get("baseLoginVO");
			SimpleDateFormat lObjSimpleFormat = new SimpleDateFormat("dd/MM/yyyy");
			Calendar lObjCal = new GregorianCalendar();
			SimpleDateFormat lObjSdf1 = new SimpleDateFormat("yyyyMM");
			Date lcurrDate = SessionHelper.getCurDate();
			lObjSimpleFormat.format(lcurrDate);

			gLngLocCode = (Long) loginDetail.get("locationId");
			gLogger.info("Location id from Map :" + gLngLocCode);

			ArrayList rowList = null;

			new StringBuffer();
			gLogger.info("***********************22****************************************");
			String pensionerCode = (String) report.getParameterValue("pensionerCode");

			Map<String, List<List<Object[]>>> lMpPensionerDtls = new LinkedHashMap<String, List<List<Object[]>>>();
			List lLstDetails = null;
			List<Object[]> lLstChequeDtls = new ArrayList();
			List lLstPensionerChequeDtls = new ArrayList();
			List<List<Object[]>> lLstBillWisePnsrDtls = new ArrayList<List<Object[]>>();
			List lLstCheqNoDate = new ArrayList();
			if (report.getReportCode().equals("365457")) {

				report.setStyleList(selfClose);
				if (!"".equals(pensionerCode) && pensionerCode != null) {
					lLstResult = lObjPensionBillDAOImpl.getPnsrDtlsForPaymentHistory(pensionerCode, gLngLocCode.toString());
					lLstChequeDtls = lObjPensionBillDAOImpl.getChequeDtls(pensionerCode, gLngLocCode.toString());
				}
				if (!lLstResult.isEmpty()) {

					// for (Object[] lArrObj : lLstResult) {
					// rowList = new ArrayList();
					// rowList.add(lIntSrNo++);
					// if (lArrObj[0] != null) {
					// rowList.add(lArrObj[0].toString());
					// } else {
					// rowList.add("");
					// }
					// if (lArrObj[1] != null) {
					// rowList.add(lArrObj[1].toString());
					// } else {
					// rowList.add("");
					// }
					// if (lArrObj[2] != null) {
					// rowList.add(lArrObj[2].toString());
					// } else {
					// rowList.add("");
					// }
					// rowList.add(lArrObj[3]);
					// rowList.add(lArrObj[4]);
					// if (lArrObj[5] != null) {
					// rowList.add(lArrObj[5].toString());
					// } else {
					// rowList.add("");
					// }
					// if (lArrObj[6] != null) {
					// rowList.add(lArrObj[6].toString());
					// } else {
					// rowList.add("");
					// }
					// if (lArrObj[7] != null) {
					// rowList.add(lArrObj[7].toString());
					// } else {
					// rowList.add("");
					// }
					// dataList.add(rowList);
					// }

					for (Object[] lArrObj : lLstResult) {
						lLstPensionerChequeDtls = new ArrayList();
						lLstBillWisePnsrDtls = lMpPensionerDtls.get(lArrObj[8].toString());
						if (lLstBillWisePnsrDtls != null) {
							lLstDetails = new ArrayList();
							lLstDetails.add(lArrObj[0]);
							lLstDetails.add(lArrObj[1]);
							lLstDetails.add(lArrObj[2]);
							lLstDetails.add(lArrObj[3]);
							lLstDetails.add(lArrObj[4]);
							lLstDetails.add(lArrObj[5]);
							lLstDetails.add(lArrObj[6]);
							lLstDetails.add(lArrObj[7]);
							lLstDetails.add(lLstPensionerChequeDtls);
							if ("N".equals(lArrObj[9])) {
								lLstDetails.add("Paid");
							} else {
								lLstDetails.add("Rejected");
							}
							lLstDetails.add(lArrObj[10]);
							lLstBillWisePnsrDtls.add(lLstDetails);
						} else {
							lLstBillWisePnsrDtls = new ArrayList();
							lLstDetails = new ArrayList();
							lLstDetails.add(lArrObj[0]);
							lLstDetails.add(lArrObj[1]);
							lLstDetails.add(lArrObj[2]);
							lLstDetails.add(lArrObj[3]);
							lLstDetails.add(lArrObj[4]);
							lLstDetails.add(lArrObj[5]);
							lLstDetails.add(lArrObj[6]);
							lLstDetails.add(lArrObj[7]);
							lLstDetails.add(lLstPensionerChequeDtls);
							if ("N".equals(lArrObj[9])) {
								lLstDetails.add("Paid");
							} else {
								lLstDetails.add("Rejected");
							}
							lLstDetails.add(lArrObj[10]);
							lLstBillWisePnsrDtls.add(lLstDetails);
							lMpPensionerDtls.put(lArrObj[8].toString(), lLstBillWisePnsrDtls);
						}
					}

				}

				if (!lLstChequeDtls.isEmpty()) {

					for (Object[] lArrObj : lLstChequeDtls) {
						if (lArrObj[0] != null && lArrObj[1] != null && lArrObj[2] != null) {
							String lStrBillNo = lArrObj[3].toString();
							lLstPensionerChequeDtls = new ArrayList();
							if (lMpPensionerDtls.containsKey(lStrBillNo)) {
								lLstBillWisePnsrDtls = lMpPensionerDtls.get(lStrBillNo);
								for (List lLstDtls : lLstBillWisePnsrDtls) {
									lLstPensionerChequeDtls = (List) lLstDtls.get(8);
									lLstCheqNoDate = new ArrayList();
									lLstCheqNoDate.add(lArrObj[0].toString());
									lLstCheqNoDate.add(lArrObj[1]);
									lLstCheqNoDate.add(lArrObj[2]);
									lLstPensionerChequeDtls.add(lLstCheqNoDate);
								}

							}
						}
					}
				}
				// for (Object[] lArrObj : lLstChequeDtls) {
				// if (lArrObj[2] != null) {
				// String lStrBillNo = lArrObj[2].toString();
				// lLstPensionerChequeDtls = new ArrayList();
				// if (lMpPensionerDtls.containsKey(lStrBillNo)) {
				// List lLstDtls = lMpPensionerDtls.get(lStrBillNo);
				// lLstPensionerChequeDtls = (List) lLstDtls.get(8);
				// lLstCheqNoDate = new ArrayList();
				// lLstCheqNoDate.add(lArrObj[0].toString());
				// lLstCheqNoDate.add(lArrObj[1]);
				// lLstPensionerChequeDtls.add(lLstCheqNoDate);
				// }
				// }
				// }
				Set<String> lSetBillNos = lMpPensionerDtls.keySet();
				Integer lIntSrNo = 1;
				List<List<Object[]>> lLstBillWise = new ArrayList<List<Object[]>>();
				for (String lStrBillNo : lSetBillNos) {
					lLstBillWise = lMpPensionerDtls.get(lStrBillNo);
					for (List lInnLstDetails : lLstBillWise) {
						rowList = new ArrayList();
						lLstPensionerChequeDtls = new ArrayList();
						rowList.add(lIntSrNo++);
						if (lInnLstDetails.get(0) == null) {
							rowList.add("");
						} else {
							rowList.add(lInnLstDetails.get(0));
						}
						rowList.add(lInnLstDetails.get(1));
						rowList.add(lInnLstDetails.get(2));
						if (bundleBillConst.getString("MNTH.MONTHLY").equals(lInnLstDetails.get(2))) {
							lObjCal.setTime(lObjSdf1.parse(lInnLstDetails.get(10).toString()));
							int maxDays = lObjCal.getActualMaximum(Calendar.DATE);
							lObjCal.set(Calendar.DATE, maxDays);
							rowList.add(new StyledData(lObjSimpleFormat.format(lObjCal.getTime()), CenterAlignVO));
						} else {
							rowList.add(new StyledData(lInnLstDetails.get(3), CenterAlignVO));
						}
						rowList.add(new StyledData(lInnLstDetails.get(4), RightAlignVO));
						if (lInnLstDetails.get(5) != null) {
							rowList.add(lInnLstDetails.get(5));
						} else {
							rowList.add("");
						}
						if (lInnLstDetails.get(6) != null) {
							rowList.add(lInnLstDetails.get(6));
						} else {
							rowList.add("");
						}
						rowList.add(lInnLstDetails.get(7));
						rowList.add(lInnLstDetails.get(9));
						lLstPensionerChequeDtls = (List) lInnLstDetails.get(8);

						// if (lLstPensionerChequeDtls.size() == 0) {
						// rowList.add("");
						// rowList.add("");
						// }
						if (lLstPensionerChequeDtls.size() > 1) {
							for (int i = 0; i < lLstPensionerChequeDtls.size(); i++) {
								if (i > 0) {
									rowList = new ArrayList();
									rowList.add("");
									rowList.add("");
									rowList.add("");
									rowList.add("");
									rowList.add("");
									rowList.add("");
									rowList.add("");
									rowList.add("");
									rowList.add("");
									rowList.add("");
								}
								lLstCheqNoDate = new ArrayList();
								lLstCheqNoDate = (List) lLstPensionerChequeDtls.get(i);
								rowList.add(lLstCheqNoDate.get(0).toString());
								rowList.add(new StyledData(lLstCheqNoDate.get(1), CenterAlignVO));
								rowList.add(lLstCheqNoDate.get(2).toString());
								dataList.add(rowList);
								// dataList.add(rowList);
							}
						} else if (lLstPensionerChequeDtls.size() == 1) {
							lLstCheqNoDate = new ArrayList();
							lLstCheqNoDate = (List) lLstPensionerChequeDtls.get(0);
							rowList.add(lLstCheqNoDate.get(0).toString());
							rowList.add(new StyledData(lLstCheqNoDate.get(1), CenterAlignVO));
							rowList.add(lLstCheqNoDate.get(2).toString());
							dataList.add(rowList);
						} else {
							rowList.add("");
							rowList.add("");
							rowList.add("");
							dataList.add(rowList);
						}
					}
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Exception :" + e, e);
		} finally {
			try {
				if (smt != null) {
					smt.close();
				}

				if (rs != null) {
					rs.close();
				}

				if (con != null) {
					con.close();
				}

				smt = null;
				rs = null;
				con = null;

			} catch (Exception e1) {
				//e1.printStackTrace();
				gLogger.error("Exception :" + e1, e1);

			}
		}
		gLogger.info("DATALIST SIZE IS" + dataList.size());

		return dataList;
	}
}