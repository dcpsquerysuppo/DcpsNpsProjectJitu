package com.tcs.sgv.dcps.report;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.RoundEnvironment;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.business.reports.ReportDataFinder;
import com.tcs.sgv.common.dao.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAO;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.DBConnection;
import com.tcs.sgv.common.util.EnglishDecimalFormat;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.PageBreak;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;


public class FormR3ReportDdowise extends DefaultReportDataFinder implements ReportDataFinder
{
	private static final Logger gLogger = Logger.getLogger(FormR3Report.class);
	public static String newline = System.getProperty("line.separator");
	ServiceLocator serviceLocator = null;
	SessionFactory gObjSessionFactory = null;
	String gStrLocCode = null;
	Long gLngLangId = null;
	Long gLngPostId = null;
	Session ghibSession = null;	

	

	private static String nosci(double d) {
		if (d < 0) {
			return "-" + nosci(-d);
		}
		String javaString = String.valueOf(d);
		int indexOfE = javaString.indexOf("E");
		if (indexOfE == -1) {
			return javaString;
		}
		StringBuffer sb = new StringBuffer();
		if (d > 1) {// big number
			int exp = Integer.parseInt(javaString.substring(indexOfE + 1));
			String sciDecimal = javaString.substring(2, indexOfE);
			int sciDecimalLength = sciDecimal.length();
			if (exp == sciDecimalLength) {
				sb.append(javaString.charAt(0));
				sb.append(sciDecimal);
			} else if (exp > sciDecimalLength) {
				sb.append(javaString.charAt(0));
				sb.append(sciDecimal);
				for (int i = 0; i < exp - sciDecimalLength; i++) {
					sb.append('0');
				}
			} else if (exp < sciDecimalLength) {
				sb.append(javaString.charAt(0));
				sb.append(sciDecimal.substring(0, exp));
				sb.append('.');
				for (int i = exp; i < sciDecimalLength; i++) {
					sb.append(sciDecimal.charAt(i));
				}
			}

			return sb.toString();
		} else {
			// for little numbers use the default or you will
			// loose accuracy

			return 	javaString;
		}

	}
	
	
	
	
	
	public Collection findReportData(ReportVO lObjReport, Object criteria)throws ReportException
	{
		List dataList = new ArrayList();
		Map lMapRequestAttributes = null;
		Map lMapSessionAttributes = null;
		LoginDetails lObjLoginVO = null;
		List lLstDataList = new ArrayList();
		lObjReport.getLangId();
		ReportsDAO reportsDao = new ReportsDAOImpl();
		lObjReport.getLocId();
		try{
			lMapRequestAttributes = (Map)((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
			lMapSessionAttributes = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
			lObjLoginVO = (LoginDetails) lMapSessionAttributes.get("loginDetails");
			serviceLocator = (ServiceLocator) lMapRequestAttributes.get("serviceLocator");
			gObjSessionFactory = serviceLocator.getSessionFactorySlave();
			ghibSession = gObjSessionFactory.getCurrentSession();
			gStrLocCode = lObjLoginVO.getLocation().getLocationCode();
			gLngLangId = lObjLoginVO.getLangId();
			Map lServiceMap = (Map) lMapRequestAttributes.get("serviceMap");
			Map lBaseLoginMap = (Map) lServiceMap.get("baseLoginMap");
			gLngPostId = (Long) lBaseLoginMap.get("loggedInPost");



			ArrayList rptList1 = null;
			TabularData rptTd = null;
			ReportVO RptVo = null;
			List lLstOpeningBalcTier1 = null;
			List lLstEmpTier1 = null;
			Double lTotalAmt=null;
			List lLstDtlsTier2 = null;
			List lLstDtlsFinalTier1=null;
			List lLstDtlsFinalMissingTier1=null;
			Object []lObj = null;
			Object []lObjTier1 = null;
			Object []lObjMissingTier1 = null;
			String lStrOpeningEmpTier1 = "";
			String lStrOpeningEmployrTier1 = "";
			String lStrInterestTier1 = "";			
			List lDblRegRecvryEmp = null;
			List TotalRegular = null;
			List lDblRegRecvryEmplyr = null;
			List lDblDelyRecvryEmp = null;
			List lDblDelyRecvryEmplyr = null;
			List lDblDAArrearEmp = null;
			List lDblDAArrearEmplyr = null;
			List   lDblPayArrearEmp = null;
			List lDbMissingCreditTier1=null;
			List lDblPayArrearEmplyr = null;
			Long lLngTotalEmpT1 = 0l;
			Long lLngEmpT1 = 0l;
			Long lLngEmplyrT1 = 0l;
			Double lDoubleTotalEmpT1 = 0d;
			Double lDoubleTotalEmplyrT1 = 0d;
			Long lLngTotalEmplyrT1 = 0l;
			Long lLngTotalIntrstT1 = 0l;
			Long lLngOpeningTier2 = 0l;
			Long lLngYearlyContriTier2 = 0l;
			Long lLngTotalEmpT2 = 0l;
			Long lLngTotalIntrstT2 = 0l;
			Double empMissingIntrstT2 = 0d;
			Double  emplrMissingIntrstT2 = 0d;
			Double empIntrstT2 = 0d;
			Double  emplrIntrstT2 = 0d;
			Double lOpeningEmpTier1 = 0d;
			Double lOpeningEmployrTier1 = 0d;
			Double lInterestTier1=0d;
			String missingamt =  null;
			Double empMisIntTier=0d;
			Double lTotalMissingamt=0d;

			ArrayList rowList=null;
			// Added by ashish for new header 			


			StyleVO[] rowsFontsVO = new StyleVO[6];
			rowsFontsVO[0] = new StyleVO();
			rowsFontsVO[0].setStyleId(IReportConstants.ROWS_PER_PAGE);
			rowsFontsVO[0].setStyleValue("26");
			rowsFontsVO[1] = new StyleVO();
			rowsFontsVO[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			rowsFontsVO[1].setStyleValue("14");
			rowsFontsVO[2] = new StyleVO();
			rowsFontsVO[2].setStyleId(IReportConstants.STYLE_FONT_FAMILY);
			rowsFontsVO[2].setStyleValue("Shruti");
			rowsFontsVO[3] = new StyleVO();
			rowsFontsVO[3].setStyleId(IReportConstants.BACKGROUNDCOLOR);
			rowsFontsVO[3].setStyleValue("white");
			rowsFontsVO[4] = new StyleVO();
			rowsFontsVO[4].setStyleId(IReportConstants.BORDER);
			rowsFontsVO[4]
			            .setStyleValue(IReportConstants.VALUE_STYLE_BORDER_MEDIUM);
			rowsFontsVO[5] = new StyleVO();
			rowsFontsVO[5].setStyleId(IReportConstants.REPORT_PAGINATION);
			rowsFontsVO[5].setStyleValue("NO");

			StyleVO[] RightAlignVO = new StyleVO[2];
			RightAlignVO[0] = new StyleVO();
			RightAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			RightAlignVO[0]
			             .setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
			RightAlignVO[1] = new StyleVO();
			RightAlignVO[1].setStyleId(IReportConstants.BORDER);
			RightAlignVO[1]
			             .setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

			StyleVO[] noBorder = new StyleVO[1];
			noBorder[0] = new StyleVO();
			noBorder[0].setStyleId(IReportConstants.BORDER);
			noBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

			StyleVO[] withBorder = new StyleVO[1];
			withBorder[0] = new StyleVO();
			withBorder[0].setStyleId(IReportConstants.BORDER);
			withBorder[0]
			           .setStyleValue(IReportConstants.VALUE_STYLE_BORDER_MEDIUM);

			StyleVO[] boldVO = new StyleVO[2];
			boldVO[0] = new StyleVO();
			boldVO[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			boldVO[0].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			boldVO[1] = new StyleVO();
			boldVO[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			boldVO[1].setStyleValue("10");

			StyleVO[] CenterAlignVO = new StyleVO[4];
			CenterAlignVO[0] = new StyleVO();
			CenterAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			CenterAlignVO[0]
			              .setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_NORMAL);
			CenterAlignVO[1] = new StyleVO();
			CenterAlignVO[1].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			CenterAlignVO[1].setStyleValue("Center");
			CenterAlignVO[2] = new StyleVO();
			CenterAlignVO[2]
			              .setStyleId(IReportConstants.BORDER);
			CenterAlignVO[2].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			CenterAlignVO[3] = new StyleVO();
			CenterAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			CenterAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);



			StyleVO[]  italic= new StyleVO[2];
			italic[0] = new StyleVO();
			italic[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			italic[0].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			italic[1] = new StyleVO();
			italic[1].setStyleId(IReportConstants.STYLE_FONT_STYLE);
			italic[1].setStyleValue(IReportConstants.VALUE_STYLE_TEXT_DECORATION_UNDERLINE);

			String StrSqlQuery = "";
			//Added by ashish for new header 
			if (lObjReport.getReportCode().equals("8000097"))
			{
				DateFormat dateFormat = new SimpleDateFormat("dd MMM,yyyy");
				DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
				DateFormat dateFormat3 = new SimpleDateFormat("dd MMM yyyy");
				DateFormat dateFormat4 = new SimpleDateFormat("dd/MM/yy");
				DateFormat dateFormat5 = new SimpleDateFormat(
				"dd/MM/yy :hh:mm:ss");
				DateFormat dateFormat6 = new SimpleDateFormat("dd/MM/yyyy");

				Date date = new Date();
				dateFormat.format(date);
				String gStrLocCode = null;

				lMapRequestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
				lMapSessionAttributes = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
				lObjLoginVO = (LoginDetails) lMapSessionAttributes.get("loginDetails");
				gStrLocCode = lObjLoginVO.getLocation().getLocationCode().trim();

				Long lLngTreasuryId = Long.valueOf(gStrLocCode);
				String lStrDdoCode = (String) lObjReport.getParameterValue("ddoCode");


				Long lLngYearId = Long.valueOf((String) lObjReport
						.getParameterValue("yearId"));

				DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(SgvcFinYearMst.class, this.serviceLocator.getSessionFactory());
				String lStrTreasuryName = getLocationName(lLngTreasuryId);
				String lStrYear = lObjDcpsCommonDAO.getFinYearForYearId(lLngYearId);
				String finYearId=lObjDcpsCommonDAO.getFinYearIdForYearDesc(lStrYear);

				String lStrDdoName = getDdoName(lStrDdoCode);
				List interestRates = getInterestRatesForGivenYear(lLngYearId);
				List lstYearDates = lObjDcpsCommonDAO
				.getDatesFromFinYearId(lLngYearId);
				Object[] objDates = (Object[]) lstYearDates.get(0);
				Date startDate = (Date) objDates[0];
				Date endDate = (Date) objDates[1];


				StringBuilder SBQueryy = new StringBuilder();
				SBQueryy.append(" SELECT  distinct emp.DCPS_ID,emp.DCPS_EMP_ID,emp.EMP_NAME,emp.DOB,emp.DOJ,emp.PAY_COMMISSION,nvl(cast(emp.BASIC_PAY as int),0) ");
				SBQueryy.append(" FROM mst_dcps_emp emp  inner join MST_DCPS_DDO_OFFICE off on emp.CURR_OFF=cast(off.DCPS_DDO_OFFICE_MST_ID as varchar) ");
				SBQueryy.append(" inner  join  TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=emp.DCPS_EMP_ID ");
				SBQueryy.append(" inner  join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID=trn.FIN_YEAR_ID ");
				SBQueryy.append(" where  fin.FIN_YEAR_ID= :yearId  and off.DDO_CODE= :ddoCode1   and trn.STATUS in ('G','H') ");
				SBQueryy.append(" and   trn.REG_STATUS=1 order by emp.EMP_NAME  ");  
				StrSqlQuery = SBQueryy.toString();
				Query lQuery = ghibSession.createSQLQuery(SBQueryy.toString());
				//	lQuery.setParameter("treasuryId", lLngTreasuryId);
				lQuery.setParameter("ddoCode1", lStrDdoCode);
				lQuery.setParameter("yearId", lLngYearId);
				List lEmpList = lQuery.list();
				gLogger.info("CD DDO code is "+lStrDdoCode);
				gLogger.info("Employee list size is "+lEmpList.size());
				Float DP = null;


				StyledData	dataStyle = new StyledData();
				ArrayList rowList1 = new ArrayList();
				ArrayList headerList = new ArrayList();
				dataStyle = new StyledData();
				dataStyle.setColspan(1);
				dataStyle.setStyles(CenterAlignVO);
				dataStyle.setData("Form R3");
				rowList1.add(dataStyle);
				dataStyle = null;
				// dataList.add(rowList1);
				headerList.add(rowList1);

				rowList1 = new ArrayList();
				dataStyle = new StyledData();
				dataStyle.setColspan(1);
				dataStyle.setStyles(CenterAlignVO);
				dataStyle.setData("(As referred to in para no.18 & 23 of Government Resolution, Finance Department 1007/18/SER- 4, dated 7 July, 2007 )");
				rowList1.add(dataStyle);
				dataStyle = null;
				//dataList.add(rowList1);
				headerList.add(rowList1);

				rowList1 = new ArrayList();
				dataStyle = new StyledData();
				dataStyle.setColspan(1);
				dataStyle.setStyles(CenterAlignVO);
				dataStyle.setData("OFFICE OF THE STATE RECORD KEEPING AGENCY, MUMBAI");
				rowList1.add(dataStyle);
				dataStyle = null;
				// dataList.add(rowList1);
				headerList.add(rowList1);

				rowList1 = new ArrayList();
				dataStyle = new StyledData();
				dataStyle.setColspan(1);
				dataStyle.setStyles(CenterAlignVO);
				dataStyle.setData("Statement of Account in respect of contributions under the D.C.P.Scheme For the Year");
				rowList1.add(dataStyle);
				dataStyle = null;
				//dataList.add(rowList1);
				headerList.add(rowList1);

				TabularData tData  = new TabularData(headerList);
				tData.addStyle(IReportConstants.STYLE_FONT_ALIGNMENT, IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
				tData.addStyle(IReportConstants.BORDER, "No");
				tData.addStyle(IReportConstants.SHOW_REPORT_NAME, IReportConstants.VALUE_NO);
				lObjReport.setAdditionalHeader(tData);	

				Object [] lObj1=null;
				List Date=null;
				String fromDate=null;
				String toDate=null;
				Date=getFromToDate(lLngYearId);
				lObj1 = (Object[]) Date.get(0);
				if(lObj1[0]!=null){
					fromDate=lObj1[0].toString();

				}

				if(lObj1[1]!=null){
					toDate=lObj1[1].toString();
				}




				HashMap map = getOpeningBalanceTier1("", lLngYearId,lStrDdoCode);
				HashMap map1 = getEmpAllDtlsTire1(lLngYearId,fromDate,toDate,lStrDdoCode);
				HashMap map2= getEmpAllDtlsDelTire1(lLngYearId,fromDate,toDate,lStrDdoCode);
				HashMap map3=getEmpAllMissingCreditTire1(lLngYearId,fromDate,toDate,lStrDdoCode);
				HashMap map4=getTotalAmountDetails(lLngYearId,fromDate,toDate,lStrDdoCode);
				HashMap map5=getTotalMissingAmountDetails(lLngYearId,fromDate,toDate,lStrDdoCode);
				HashMap map6=getTier1TotalDetails(lLngYearId,lStrDdoCode);
				HashMap map7=getTier2Details(lLngYearId,lStrDdoCode);
				HashMap map8=getYearlyArrAmountOfEmp(lLngYearId,lStrDdoCode);
				HashMap map9=getTier1FinalDetails(lLngYearId,lStrDdoCode);
				HashMap map10=getTier1FinalIntMissingDetails(lLngYearId,lStrDdoCode);


				gLogger.info("map value is "+map.entrySet());
				String dcpsIdMap=null;
				String monthIdMap=null;
				String paymentType=null;
				if (lEmpList != null && !lEmpList.isEmpty()) {
					for (Integer lInt = 0; lInt < lEmpList.size(); lInt++) {

						Object[] lArrObj = (Object[]) lEmpList.get(lInt);
						String dcpsId = (String) lArrObj[0];
						String empName = (String) lArrObj[2];
						BigInteger dcpsEmpId1 = (BigInteger) lArrObj[1];
						Date DOB = (Date) lArrObj[3];
						int basicPay = (Integer) lArrObj[6];
						String payComm = (String) lArrObj[5];
						Date DOJ=(Date) lArrObj[4];
						Long dcpsEmpId=dcpsEmpId1.longValue();
						Float daRate = lObjDcpsCommonDAO.getCurrentDARate(payComm);
						Double openEmployeeInt=0d; 
						Double  openEmployerInt=0d;

						if(basicPay==0  ){
							basicPay= 0;
						}


						if (payComm.equals("700015")) {
							DP = new Float((basicPay) * 50 / 100);
							daRate = new Float((basicPay) * daRate
									/ 100);
						} else {
							DP = new Float(0);
							daRate = new Float(((basicPay) + DP)* daRate / 100);
						}


						lObjReport.setStyleList(rowsFontsVO);

						String header1 = "<center><b>" + "<font size=\"1.2px\"> "
						+ "FORM - R- 3" + "</font></b></center>";
						String header2 = "<center><b>"
							+ "<font size=\"0.8px\"> "
							+ "(As referred to in para no.18 & 23 of Government Resolution, Finance Department 1007/18/SER- 4, dated 7 July, 2007 )"
							+ "</font></b></center>";
						String header3 = "<center><b>" + "<font size=\"0.8px\"> "
						+ "OFFICE OF THE STATE RECORD KEEPING AGENCY, MUMBAI"
						+ "</font></b></center>";
						String header4 = "<center><b>"
							+ "<font size=\"1px\"> "
							+ "Statement of Account in respect of contributions under the D.C.P.Scheme For the Year "
							+ lStrYear + "</font></b></center>";

						String additionalHeader = header1 + header2 + header3 + header4;
						//lObjReport.setAdditionalHeader(new StyledData(space(145)+"Form R3"+newline+space(50)+"(As referred to in para no.18 & 23 of Government Resolution, Finance Department 1007/18/SER- 4, dated 7 July, 2007 )"+newline+space(100)+"OFFICE OF THE STATE RECORD KEEPING AGENCY, MUMBAI"+newline+space(80)+"Statement of Account in respect of contributions under the D.C.P.Scheme For the Year"+lStrYear,boldVO));

						rowList = new ArrayList();
						rowList.add(new StyledData("Name of the Employee : " + empName ,boldVO));
						dataList.add(rowList);

						rowList = new ArrayList();
						rowList.add(new StyledData("Pension Account Number : " + dcpsId ,boldVO));
						dataList.add(rowList);

						rowList = new ArrayList();
						rowList.add(new StyledData("Birth Date : "
								+ dateFormat2.format(DOB).toString() + space(25)
								+ "Treasury : (" + lLngTreasuryId.toString() + ") "
								+ lStrTreasuryName ,
								//+ space(20) + "Rate of Interest : "+ interestRate
								boldVO));
						dataList.add(rowList);

						rowList = new ArrayList();
						rowList.add(new StyledData("DDO :("+lStrDdoCode+")"+ lStrDdoName, boldVO));
						dataList.add(rowList);




						rowList = new ArrayList();
						rowList.add(new StyledData("Date Of Joining : " + dateFormat2.format(DOJ).toString() ,boldVO));
						dataList.add(rowList);


						rowList = new ArrayList();
						rowList.add(new StyledData("Interest Rate : ",boldVO));
						dataList.add(rowList);

						String lStrFromDateInt = "";
						String lStrToDateInt = "";
						Double lDoubleIntRate = null;
						Object[] lObjIntDtls = null;

						for(Integer lInt1=0;lInt1<interestRates.size();lInt1++)
						{
							rowList1 = new ArrayList();
							lObjIntDtls = (Object[]) interestRates.get(lInt1);
							lDoubleIntRate = Double.valueOf(lObjIntDtls[0].toString().trim());
							lDoubleIntRate = Round(lDoubleIntRate,2);
							lStrFromDateInt = dateFormat6.format(dateFormat2.parse(lObjIntDtls[1].toString().trim()));
							lStrToDateInt = dateFormat6.format(dateFormat2.parse(lObjIntDtls[2].toString().trim()));
							rowList1.add(lDoubleIntRate + " ( " + lStrFromDateInt + " to " + lStrToDateInt + " )");
							dataList.add(rowList1);
						}

						rowList = new ArrayList();
						rowList.add(new StyledData("From (Month) : "
								+ dateFormat3.format(startDate).toString() + space(15)
								+ "To (Month) : "
								+ dateFormat3.format(endDate).toString() ,boldVO));
						dataList.add(rowList);

						rowList = new ArrayList();
						rowList.add(new StyledData("Basic Pay :" + basicPay + space(15) + "D.P. :"
								+ DP + space(15) + "D.A. :" + Math.round(daRate)
								,boldVO));
						dataList.add(rowList);



						/*
						lLstOpeningBalcTier1 = getOpeningBalanceTier1(dcpsId, lLngYearId);

						if ((lLstOpeningBalcTier1 != null) && (lLstOpeningBalcTier1.size() > 0))
						{
							lObj = (Object[])lLstOpeningBalcTier1.get(0);
							if (lObj[0] != null)
								lStrOpeningEmpTier1 = lObj[0].toString();

							if (lObj[1] != null)
								lStrOpeningEmployrTier1 = lObj[1].toString();

							if (lObj[2] != null)
								lStrInterestTier1 = lObj[2].toString();

						}
						 */

						String IntrValue=(String) map.get(dcpsId);
						gLogger.info("dcpsId is ***********"+dcpsId);

						gLogger.info("IntrValue is ***********"+IntrValue);
						if(IntrValue!=null && !IntrValue.equals("")){
							String[] IntrvalrArr=IntrValue.split("#");
							lStrOpeningEmpTier1=IntrvalrArr[0];
							lStrOpeningEmployrTier1=IntrvalrArr[1];
							lStrInterestTier1=IntrvalrArr[2];
						}
						else{
							lStrOpeningEmpTier1="0";
							lStrOpeningEmployrTier1="0";
							lStrInterestTier1="0";
						}

						lLstDataList = new ArrayList();
						rowList = new ArrayList();

						rowList.add(newline);
						rowList.add(newline);
						rowList.add(newline);
						rowList.add(newline);
						rowList.add(newline);
						rowList.add(newline);

						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("");
						rowList.add(new StyledData("Tier 1",boldVO));
						rowList.add("");
						rowList.add("");
						rowList.add("");
						rowList.add("");

						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("1");
						rowList.add("Opening Balance");
						rowList.add("");
						rowList.add(nosci(Double.parseDouble(lStrOpeningEmpTier1)).toString());
						rowList.add(nosci(Double.parseDouble(lStrOpeningEmployrTier1)).toString());
						if ((lLstOpeningBalcTier1 != null) && (lLstOpeningBalcTier1.size() > 0))
							rowList.add(Round(Double.valueOf(lStrInterestTier1),2));
						else
							rowList.add(nosci(Double.parseDouble(lStrInterestTier1)).toString());
						lLstDataList.add(rowList);


						//----------- Ashish Code starts Added to display April contri added with march-----------



						String EmpAmount =  null;
						String[] arrYear = lStrYear.split("-");
						String[] arrMonths = new String[12];

						arrMonths[0] = "JAN-" + arrYear[1];
						arrMonths[1] = "FEB-" + arrYear[1];
						arrMonths[2] = "MAR-" + arrYear[0];
						arrMonths[3] = "APR-" + arrYear[0];
						arrMonths[4] = "MAY-" + arrYear[0];
						arrMonths[5] = "JUN-" + arrYear[0];
						arrMonths[6] = "JUL-" + arrYear[0];
						arrMonths[7] = "AUG-" + arrYear[0];
						arrMonths[8] = "SEP-" + arrYear[0];
						arrMonths[9] = "OCT-" + arrYear[0];
						arrMonths[10] = "NOV-" + arrYear[0];
						arrMonths[11] = "DEC-" + arrYear[0];


						rowList = new ArrayList();
						rowList.add("2");
						rowList.add("Regular Recovery");
						rowList.add("");
						rowList.add("");
						rowList.add("");
						rowList.add("");
						lLstDataList.add(rowList);

						//lDblRegRecvryEmp = getEmpAllDtlsTire1(dcpsEmpId, lLngYearId,fromDate,toDate);

						for(int monID=3;monID<=14;monID++)
						{




							int newIndex = monID;
							if(monID > 12)
								newIndex = monID - 12;


							rowList = new ArrayList();
							boolean prnitOrNot = true;


							/*	for (int i =0; i < lDblRegRecvryEmp.size(); i++) {*/




							//	Object[]obj = (Object[]) lDblRegRecvryEmp.get(i);	

							String Key1=dcpsId+"#"+newIndex+"#700046";
							/*	String Key2=dcpsId+"#"+newIndex+"#700047";
							String Key3=dcpsId+"#"+newIndex+"#700048";
							String Key4=dcpsId+"#"+newIndex+"#700049";
							 */
							String monYr=null;
							int monId=0;
							String tier1Value=(String) map1.get(Key1);
							if(tier1Value!=null && !tier1Value.equals("")){
								String[] tier1Valuearr=tier1Value.split("#");
								monYr=tier1Valuearr[0];
								EmpAmount=tier1Valuearr[2];
								paymentType=tier1Valuearr[4];
								monId=Integer.parseInt(tier1Valuearr[3].toString());
							}
							else{
								monYr="";
								EmpAmount="0**";
								monId=0;
							}

							if(newIndex == monId){

								rowList.add("");
								rowList.add(" ");
								rowList.add(new StyledData(monYr,boldVO));
								rowList.add(nosci(Double.parseDouble(EmpAmount)).toString());
								rowList.add(nosci(Double.parseDouble(EmpAmount)).toString());
								rowList.add("");
								prnitOrNot = false;

							}
							/*}*/

							else if (prnitOrNot) 
							{
								rowList.add("");
								rowList.add("");
								rowList.add(new StyledData(arrMonths[newIndex-1],boldVO));
								rowList.add("0**");
								rowList.add('0');
								rowList.add("");
							}
							lLstDataList.add(rowList);
						}



						rowList = new ArrayList();
						rowList.add("3");
						rowList.add("Delayed Recovery");
						rowList.add("");
						rowList.add("");
						rowList.add("");
						rowList.add("");
						lLstDataList.add(rowList);

						String  month12 = null;
						String  month1 = null;
						String year1 = null;
						String EmpAmount1 =  null;



						//	lDblDelyRecvryEmp = getEmpAllDtlsDelTire1(dcpsEmpId, lLngYearId,fromDate,toDate);
						for(int monID=3;monID<=14;monID++)
						{
							int newIndex = monID;
							if(monID > 12)
								newIndex = monID - 12;

							rowList = new ArrayList();
							/*for (int i =0; i < lDblDelyRecvryEmp.size(); i++) {
								Object[]obj = (Object[]) lDblDelyRecvryEmp.get(i);	*/

							String Key2=dcpsId+"#"+newIndex+"#700047";
							String monYr1=null;
							int monId1=0;
							String tier1DelValue=(String) map2.get(Key2);

							if(tier1DelValue!=null && !tier1DelValue.equals("")){
								String[] tier1ValueDelarr=tier1DelValue.split("#");
								monYr1=tier1ValueDelarr[0];
								EmpAmount1=tier1ValueDelarr[2];
								paymentType=tier1ValueDelarr[4];
								monId1=Integer.parseInt(tier1ValueDelarr[3].toString());
							}
							else{
								monYr1="";
								EmpAmount1="0**";
								monId1=0;
							}

							if(newIndex == monId1){

								rowList.add("");
								rowList.add(" ");
								rowList.add(new StyledData(monYr1,boldVO));
								rowList.add(nosci(Double.parseDouble(EmpAmount1)).toString());
								rowList.add(nosci(Double.parseDouble(EmpAmount1)).toString());
								rowList.add("");

								lLstDataList.add(rowList);

							}
							//}

						}



						rowList = new ArrayList();
						rowList.add("4");
						rowList.add("DA Arrears");
						rowList.add("");
						rowList.add("");
						rowList.add("");
						rowList.add("");
						lLstDataList.add(rowList);


						String monYr2=null;
						int monId2=0;
						String EmpAmount2=null;



						//lDblDAArrearEmp = getEmpAllDtlsTire1(dcpsEmpId, lLngYearId, "700048");
						for(int monID=3;monID<=14;monID++)
						{
							int newIndex = monID;
							if(monID > 12)
								newIndex = monID - 12;

							rowList = new ArrayList();
							/*for (int i =0; i < lDblRegRecvryEmp.size(); i++) {*/
							/*								Object[]obj = (Object[]) lDblRegRecvryEmp.get(i);*/	

							String Key2=dcpsId+"#"+newIndex+"#700048";




							String tier1Value=(String) map1.get(Key2);
							if(tier1Value!=null && !tier1Value.equals("")){
								String[] tier1Valuearr=tier1Value.split("#");
								monYr2=tier1Valuearr[0];
								EmpAmount2=tier1Valuearr[2];
								paymentType=tier1Valuearr[4];
								monId2=Integer.parseInt(tier1Valuearr[3].toString());
							}
							else{
								monYr2="";
								EmpAmount2="0**";
								monId2=0;
							}

							if(newIndex == monId2){

								rowList.add("");
								rowList.add(" ");
								rowList.add(new StyledData(monYr2,boldVO));
								rowList.add(nosci(Double.parseDouble(EmpAmount2)).toString());
								rowList.add(nosci(Double.parseDouble(EmpAmount2)).toString());
								rowList.add("");
								lLstDataList.add(rowList);

							}
						}
						/*}*/

						/*	}*/



						rowList = new ArrayList();
						rowList.add("5");
						rowList.add("Pay Arrears");
						rowList.add("");
						rowList.add("");
						rowList.add("");
						rowList.add("");
						lLstDataList.add(rowList);



						String monYr3=null;
						int monId3=0;
						String EmpAmount3=null;

						//lDblPayArrearEmp = getEmpAllDtlsTire1(dcpsEmpId, lLngYearId, "700049");
						for(int monID=3;monID<=14;monID++)
						{
							int newIndex = monID;
							if(monID > 12)
								newIndex = monID - 12;

							rowList = new ArrayList();

							String Key3=dcpsId+"#"+newIndex+"#700049";


							//	for (int i =0; i < lDblRegRecvryEmp.size(); i++) {
							//Object[]obj = (Object[]) lDblRegRecvryEmp.get(i);	




							String tier1Value=(String) map1.get(Key3);
							if(tier1Value!=null && !tier1Value.equals("")){
								String[] tier1Valuearr=tier1Value.split("#");
								monYr3=tier1Valuearr[0];
								EmpAmount3=tier1Valuearr[2];
								paymentType=tier1Valuearr[4];
								monId3=Integer.parseInt(tier1Valuearr[3].toString());
							}
							else{
								monYr3="";
								EmpAmount3="0**";
								monId3=0;
							}

							if(newIndex == monId3){

								rowList.add("");
								rowList.add(" ");
								rowList.add(new StyledData(monYr3,boldVO));
								rowList.add(nosci(Double.parseDouble(EmpAmount3)).toString());
								rowList.add(nosci(Double.parseDouble(EmpAmount3)).toString());
								rowList.add("");

								lLstDataList.add(rowList);
							}
						}
						//	}






						rowList = new ArrayList();
						rowList.add("6");
						rowList.add("Mising Credits");
						rowList.add("");
						rowList.add("");
						rowList.add("");
						rowList.add("");
						lLstDataList.add(rowList);

						String monYr4=null;
						int monId4=0;
						String EmpAmount4=null;
						String payType=null;
						String[] arrYearSplit;

						//lDbMissingCreditTier1 = getEmpAllMissingCreditTire1(dcpsEmpId, fromDate,toDate,lLngYearId);
						// empMisIntTier=getTier1IntMissingDetails(dcpsId, lLngYearId);
						for(int monID=3;monID<=14;monID++)
						{
							int newIndex = monID;
							if(monID > 12)
								newIndex = monID - 12;
							//gLogger.info("Size of Missing reocrds  is------------" + lDbMissingCreditTier1.size());

							//for (int i =0; i < lDbMissingCreditTier1.size(); i++) {
							rowList = new ArrayList();
							//	Object[]obj = (Object[]) lDbMissingCreditTier1.get(i);	


							String Key4=dcpsId+"#"+newIndex;
							String tier1MissingValue=(String) map3.get(Key4);
							if(tier1MissingValue!=null && !tier1MissingValue.equals("")){
								String[] tier1MissingValuearr=tier1MissingValue.split("#");
								monYr4=tier1MissingValuearr[0];
								EmpAmount4=tier1MissingValuearr[2];
								payType=tier1MissingValuearr[4];
								monId4=Integer.parseInt(tier1MissingValuearr[3].toString());
							}
							else{
								monYr4="";
								EmpAmount4="0**";
								monId4=0;
							}

							if(newIndex == monId4){
								EmpAmount4	=((EmpAmount4!=null) ? EmpAmount4.toString(): "");

								rowList.add("");
								rowList.add(" ");

								rowList.add(new StyledData(monYr4+"-"+payType,boldVO));
								rowList.add(nosci(Double.parseDouble(EmpAmount4)).toString());	
								rowList.add(nosci(Double.parseDouble(EmpAmount4)).toString());
								rowList.add("");

								lLstDataList.add(rowList);

							}
						}

						rowList = new ArrayList();
						rowList.add(newline);
						rowList.add(newline);
						rowList.add(newline);
						rowList.add(newline);
						rowList.add(newline);
						rowList.add(newline);

						lLstDataList.add(rowList);


						//lTotalAmt=getTotalAmountDetails(dcpsEmpId, lLngYearId, fromDate, toDate);

						String totalAmt=(String) map4.get(dcpsId);

						if(totalAmt!=null && !totalAmt.equals("")){

							lTotalAmt=Double.parseDouble(totalAmt);
							gLogger.info("Total Amount For regular is **********"+lTotalAmt);

						}
						else{
							lTotalAmt=0.0;

						}


						//lTotalMissingamt=getTotalMissingAmountDetails(dcpsEmpId, lLngYearId, fromDate, toDate);


						String totalMissAmt=(String) map5.get(dcpsId);

						if(totalMissAmt!=null && !totalMissAmt.equals("")){

							lTotalMissingamt=Double.parseDouble(totalMissAmt);

							gLogger.info("Total Amount For Missing is **********"+lTotalMissingamt);
						}
						else{
							lTotalMissingamt=0.0;

						}



						rowList = new ArrayList();
						rowList.add("");
						rowList.add(new StyledData("Total Missing Credit Amount",boldVO));
						rowList.add("");


						if(lTotalMissingamt!=null)
						{
							rowList.add(nosci(lTotalMissingamt).toString());
							rowList.add(nosci(lTotalMissingamt).toString());
						}
						else{
							rowList.add(0);
							rowList.add(0);
						}
						rowList.add(newline);
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add(newline);
						rowList.add(newline);
						rowList.add(newline);
						rowList.add(newline);
						rowList.add(newline);
						rowList.add(newline);

						lLstDataList.add(rowList);


						//Double empTier = getTier1TotalDetails(dcpsId, lLngYearId);
						Double empTier=null;

						String totalemptierIntAmt=(String) map6.get(dcpsId);

						if(totalemptierIntAmt!=null && !totalemptierIntAmt.equals("")){

							empTier=Double.parseDouble(totalemptierIntAmt);


						}
						else{
							empTier=0.0;

						}



						//lTotalAmt=getTotalAmountDetails(dcpsEmpId, lLngYearId, fromDate, toDate);

						rowList = new ArrayList();
						rowList.add("");
						rowList.add(new StyledData("TIER -1 Total For F.Y. "+lStrYear,boldVO));



						rowList.add("");


						if(lTotalAmt != null || lTotalMissingamt!=null)
						{

							rowList.add(lTotalAmt+lTotalMissingamt);
							rowList.add(lTotalAmt+lTotalMissingamt);
						}
						else{
							rowList.add(0);
							rowList.add(0);
						}
						if(empTier != null)
						{
							//rowList.add(empTier+empMisIntTier);
							rowList.add(nosci(empTier).toString());
						}
						else
						{
							rowList.add("0");
						}
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add(newline);
						rowList.add(newline);
						rowList.add(newline);
						rowList.add(newline);
						rowList.add(newline);
						rowList.add(newline);

						lLstDataList.add(rowList);



						rowList = new ArrayList();
						rowList.add("");
						rowList.add(new StyledData("Tier 2",boldVO));
						rowList.add("");
						rowList.add("");
						rowList.add("");
						rowList.add("");
						lLstDataList.add(rowList);


						String [] arr=null;
						String [] arr1=null;
						String test=null;
						//lLstDtlsTier2 = getTier2Details(dcpsId, lLngYearId);



						/*lLstDtlsFinalTier1=getTier1FinalDetails(dcpsId, lLngYearId);
						lLstDtlsFinalMissingTier1=getTier1FinalIntMissingDetails(dcpsId, lLngYearId);
*/
						//Details For Tier1 Missing interest for employee and employer
						
						String tier1IntValue=(String) map9.get(dcpsId);
						if(tier1IntValue!=null && !tier1IntValue.equals("")){
							String[] tier1IntValuearr=tier1IntValue.split("#");
							empIntrstT2=Double.parseDouble(tier1IntValuearr[0]);
							emplrIntrstT2=Double.parseDouble(tier1IntValuearr[1]);
							
						}
						else{
							empIntrstT2=0d;
							emplrIntrstT2=0d;
							
						}
						
						String tier1MissIntValue=(String) map10.get(dcpsId);
						if(tier1MissIntValue!=null && !tier1MissIntValue.equals("")){
							String[] tier1MissIntValuearr=tier1MissIntValue.split("#");
							empMissingIntrstT2=Double.parseDouble(tier1MissIntValuearr[0]);
							emplrMissingIntrstT2=Double.parseDouble(tier1MissIntValuearr[1]);
							
						}
						else{
							empMissingIntrstT2=0d;
							emplrMissingIntrstT2=0d;
							
						}
						//end
					
						//Tier 2 Records
						String tier2Rec=(String) map7.get(dcpsId);
						if(tier2Rec!=null && !tier2Rec.equals("")){
							String[] tier2Recarr=tier2Rec.split("#");
							lLngOpeningTier2 = Math.round(Double.parseDouble(tier2Recarr[0]));
							lLngTotalEmpT2 = Math.round(Double.parseDouble(tier2Recarr[2]));
							lLngTotalIntrstT2 = Math.round(Double.parseDouble(tier2Recarr[3]));
							lOpeningEmpTier1  =Double.parseDouble(tier2Recarr[6].toString());
							lOpeningEmployrTier1 =Double.parseDouble(tier2Recarr[7].toString());
							lInterestTier1 = Double.parseDouble(tier2Recarr[8].toString());
						}
						else{
							lLngOpeningTier2 = 0l;
							lLngTotalEmpT2 = 0l;
							lLngTotalIntrstT2 = 0l;
							lOpeningEmpTier1  =0d;
							lOpeningEmployrTier1 =0d;
							lInterestTier1 = 0d;
						}
						//end

						String totalSixApprove=(String) map8.get(dcpsId);

						if(totalSixApprove!=null && !totalSixApprove.equals("")){

							lLngYearlyContriTier2=Math.round(Double.parseDouble(totalSixApprove));


						}
						else{
							lLngYearlyContriTier2=0l;

						}


						

						rowList = new ArrayList();
						rowList.add("7");
						rowList.add("Opening Balance *");
						rowList.add("");
						rowList.add(nosci(lLngOpeningTier2).toString());
						rowList.add("");
						rowList.add("");
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("8");
						rowList.add("Yearly Contributions");
						rowList.add("");
						rowList.add(nosci(lLngYearlyContriTier2).toString());
						rowList.add("");
						rowList.add("");
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add(newline);
						rowList.add(newline);
						rowList.add(newline);
						rowList.add(newline);
						rowList.add(newline);
						rowList.add(newline);
						lLstDataList.add(rowList);



						rowList = new ArrayList();
						rowList.add("");
						rowList.add(new StyledData("TIER -2 Total For F.Y. "+lStrYear,boldVO));
						//rowList.add(lLngTotalEmpT2);
						rowList.add("");
						Long TotalTierTwo=lLngYearlyContriTier2+lLngOpeningTier2;
						rowList.add(nosci(TotalTierTwo).toString());
						rowList.add("");
						rowList.add(nosci(lLngTotalIntrstT2).toString());

						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add(newline);
						rowList.add(newline);
						rowList.add(newline);
						rowList.add(newline);
						rowList.add(newline);
						rowList.add(newline);
						lLstDataList.add(rowList);

						Double totalContr=Double.valueOf(lTotalAmt.doubleValue() + lTotalMissingamt.doubleValue()+ lLngYearlyContriTier2.doubleValue());
						Double totalInt=Double.valueOf(lLngTotalIntrstT2.doubleValue() + empIntrstT2.doubleValue() +  empMissingIntrstT2.doubleValue());
						Double totalEmplrInt=Double.valueOf(emplrIntrstT2.doubleValue() + emplrMissingIntrstT2.doubleValue() );

						rowList = new ArrayList();
						rowList.add("");
						rowList.add("Total Contribution from Employer and Employee");
						rowList.add("");
						rowList.add(nosci(totalContr).toString());
						rowList.add(Double.valueOf(lTotalAmt.doubleValue()+lTotalMissingamt.doubleValue()));
						rowList.add("");
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("");
						rowList.add("Total Interest");
						rowList.add("");
						//rowList.add(arr1[0] +" ."+ arr1[1]); empIntrstT2
						rowList.add(nosci(totalInt).toString()); 
						rowList.add(nosci(totalEmplrInt).toString());
						rowList.add(""); 
						lLstDataList.add(rowList);

						Double totalEmpAmt=Double.valueOf( lOpeningEmpTier1.doubleValue() + totalInt.doubleValue() + totalContr.doubleValue() );
						Double totalEmplyrAmt=Double.valueOf(lTotalAmt.doubleValue() +  lTotalMissingamt.doubleValue() + lOpeningEmployrTier1.doubleValue() + totalEmplrInt.doubleValue() ); 
						gLogger.info("Interest rate size is"+ interestRates.size());
						for(Integer lInt1=0;lInt1<interestRates.size();lInt1++)
						{

							lObjIntDtls = (Object[]) interestRates.get(lInt1);
							lDoubleIntRate = Double.valueOf(lObjIntDtls[0].toString().trim());
							lDoubleIntRate = Round(lDoubleIntRate,2);

							openEmployeeInt=openEmployeeInt+lDoubleIntRate * lOpeningEmpTier1  / 100;
							openEmployerInt=openEmployerInt+lDoubleIntRate * lOpeningEmployrTier1  / 100;
						}

						rowList = new ArrayList();
						rowList.add("");
						rowList.add("Total Amount Standing to the Credit of the Employee");
						rowList.add("");
						rowList.add(nosci(totalEmpAmt).toString());
						rowList.add(nosci(totalEmplyrAmt).toString());
						rowList.add("");
						lLstDataList.add(rowList);		

						rowList = new ArrayList();
						rowList.add("");
						rowList.add("Open Int");
						rowList.add("");
						rowList.add(Round((openEmployeeInt),2));
						rowList.add(Round(openEmployerInt,2));
						rowList.add("");
						lLstDataList.add(rowList);	

						rowList = new ArrayList();
						rowList.add("");
						rowList.add("Closing Balance with open Int");
						rowList.add("");
						rowList.add(Round((totalEmpAmt),2)+Round((openEmployeeInt),2));
						rowList.add(Round((totalEmplyrAmt),2)+Round(openEmployerInt,2));
						rowList.add("");
						lLstDataList.add(rowList);	

						rptTd = new TabularData(lLstDataList);
						RptVo = reportsDao.getReport("8000089", lObjReport.getLangId(), 
								lObjReport.getLocId());
						rptTd.setRelatedReport(RptVo);

						rptList1 = new ArrayList();
						rptList1.add(rptTd);
						dataList.add(rptList1);

						Long totalAmtCredit=Math.round(Double.valueOf(totalEmpAmt.doubleValue() + totalEmplyrAmt.doubleValue() + lInterestTier1.doubleValue() )) ;

						String lStrTotAmtInWords = 
							EnglishDecimalFormat.convertWithSpace(new BigDecimal(Math.round(totalAmtCredit)));
						rowList1 = new ArrayList();
						rowList1.add(new StyledData("Total Amount standing to credit : Rs. " + 
								Math.round(totalAmtCredit ) +
								space(5) + 
								"( Rupees in Words : " + 
								lStrTotAmtInWords+")" 
								,boldVO));
						dataList.add(rowList1);

						rowList = new ArrayList();
						rowList.add("Certified that the details shown above are correct as per the information received in this office. However, the closing balance shown above will be subject to final adjustments on account of excess credit or excess interest, if any, which may come to the notice of this Office at a later date."+ newline+ newline);
						dataList.add(rowList);

						rowList = new ArrayList();	
						rowList.add(new StyledData("Place: Mumbai"+ newline,boldVO));
						dataList.add(rowList);

						rowList = new ArrayList();
						rowList.add(new StyledData("Date:   "
								+ dateFormat4.format(date) + space(50) +   "**Shows Missing Credits"
								,boldVO));
						dataList.add(rowList);

						rowList = new ArrayList();
						rowList.add(new StyledData("Office-in-Charge - Treasury Officer" + newline
								+ "(" + lLngTreasuryId + ") " + lStrTreasuryName
								,RightAlignVO));
						dataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("");
						dataList.add(rowList);

						rowList = new ArrayList();
						rowList
						.add("Please verify the details of Shri/Smt/Kum "
								+ empName
								+ ". If there is any discrepancy; please inform the Treasury Officer concerned with Vr.No.,Date, Amount of the bill alongwith a copy of the schedule.");
						dataList.add(rowList);

						rowList = new ArrayList();
						rowList
						.add("This is a Computer Generated Statement, Needs No Signature"
								+newline 
								+newline);
						dataList.add(rowList);

						rowList = new ArrayList();
						rowList
						.add(new StyledData("* Tier - 2 Opening Balance has been added to the Employees contribution Opening Balance in Sr. No. 1",boldVO));
						dataList.add(rowList);


						rowList = new ArrayList();
						rowList.add(new StyledData(dateFormat5.format(date),
								RightAlignVO));
						dataList.add(rowList);

						if (lInt != lEmpList.size() - 1) {
							rowList = new ArrayList();
							rowList.add(new PageBreak());
							dataList.add(rowList);
						}
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			gLogger.info("findReportData(): Exception is" + e, e);
		} 
		return dataList;
	}

	public List getAllDDO(Hashtable otherArgs, String lStrLangId, String lStrLocId) {

		Hashtable sessionKeys = (Hashtable) (otherArgs).get(IReportConstants.SESSION_KEYS);
		Map loginMap = (Map) sessionKeys.get("loginDetailsMap");
		String lStrLocationId = null;
		if(loginMap.containsKey("locationId"))
		{
			lStrLocationId = loginMap.get("locationId").toString();
		}
		List<Object> lLstReturnList = null;

		try
		{
			StringBuilder sb = new StringBuilder();
			Session lObjSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			sb.append("SELECT DM.ddoCode, DM.ddoName FROM RltDdoOrg RO, OrgDdoMst DM,CmnLocationMst LM ");
			sb.append("WHERE RO.locationCode = :locationCode AND RO.ddoCode = DM.ddoCode AND LM.locationCode = RO.locationCode AND LM.cmnLanguageMst.langId = 1");
			sb.append("ORDER BY DM.ddoCode ASC ");
			Query selectQuery = lObjSession.createQuery(sb.toString());
			selectQuery.setParameter("locationCode", lStrLocationId);
			List lLstResult = selectQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			if (lLstResult != null && lLstResult.size() != 0) {
				lLstReturnList = new ArrayList<Object>();
				Object obj[];
				for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
					obj = (Object[]) lLstResult.get(liCtr);
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId(obj[0].toString());
					lObjComboValuesVO.setDesc("(" + obj[0].toString() + ") " +obj[1].toString());
					lLstReturnList.add(lObjComboValuesVO);
				}
			}
		}
		catch (Exception e)
		{
			gLogger.error("Error is : " + e, e);
		}
		return lLstReturnList;
	}


	public List getYear(String lStrLangId, String lStrLocId) {
		ArrayList<ComboValuesVO> arrYear = new ArrayList<ComboValuesVO>();
		Connection lCon = null;
		PreparedStatement lStmt = null;
		ResultSet lRs = null;

		String fin_year_id = null;
		String fin_name = null;
		try {

			lCon = DBConnection.getConnection();
			StringBuffer lsb = new StringBuffer();
			lsb = new StringBuffer(
					"select * from sgvc_fin_year_mst where lang_id ='"
					+ lStrLangId
					+ "'  and fin_year_code between '2012' and '2015' order by fin_year_code ");

			lStmt = lCon.prepareStatement(lsb.toString());
			lRs = lStmt.executeQuery();
			while (lRs.next()) {
				ComboValuesVO vo = new ComboValuesVO();
				fin_year_id = lRs.getString("fin_year_id");
				fin_name = lRs.getString("fin_year_desc");
				vo.setId(fin_year_id);
				vo.setDesc(fin_name);
				arrYear.add(vo);
			}

		} catch (Exception e) {
			gLogger.error("Exception is : " + e, e);
		} finally {
			try {
				if (lStmt != null) {
					lStmt.close();
				}
				if (lRs != null) {
					lRs.close();
				}
				if (lCon != null) {
					lCon.close();
				}

				lStmt = null;
				lRs = null;
				lCon = null;
			} catch (Exception e) {				
				gLogger.info("Sql Exception:" + e, e);
			}
		}
		return arrYear;
	}

	// To get Tier 1 Opninig balance	
	public HashMap getOpeningBalanceTier1(String dcpsId, Long lLngYearId, String strDdoCode)
	{
		List lLstData = null;
		Object [] obj=null;
		String dcps_id="";
		String intrValue="";
		HashMap map= new HashMap();
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT dcps_id,OPEN_EMPLOYEE ||'#' ||OPEN_EMPLOYER||'#'||OPEN_INT ");
			lSBQuery.append("FROM MST_DCPS_CONTRIBUTION_YEARLY ");
			lSBQuery.append("WHERE dcps_id in  (SELECT distinct emp.DCPS_ID FROM mst_dcps_emp emp  inner join MST_DCPS_DDO_OFFICE off on emp.CURR_OFF=off.DCPS_DDO_OFFICE_MST_ID inner join  TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=emp.DCPS_EMP_ID  inner  join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID=trn.FIN_YEAR_ID where  fin.FIN_YEAR_ID= '"+lLngYearId+"'  and off.DDO_CODE= '"+strDdoCode+"' and trn.STATUS in ('G','H') and  trn.REG_STATUS=1 )");
			lSBQuery.append("AND YEAR_ID = '"+lLngYearId+"' ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			//lQuery.setParameter("dcpsId", dcpsId);
			gLogger.info("Sql query for interest is *************:" + lSBQuery.toString());
			lLstData = lQuery.list();

			if(lLstData!=null && !lLstData.isEmpty()){
				Iterator it =  lLstData.iterator();
				while(it.hasNext()){
					obj = (Object[]) it.next();
					dcps_id = obj[0].toString();
					intrValue=obj[1].toString();
					map.put(dcps_id, intrValue);
				}

			}



		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}

		return map;
	}




	// To get Regular,DA and PA Tier 1 records
	public HashMap getEmpAllDtlsTire1(Long lLngYearId,String fromDate,String toDate,String strDdoCode)
	{
		List lLstData = null;
		List lDblRecovery = new ArrayList();
		Object [] obj=null;
		String dcps_id="";
		String tier1val="";
		HashMap map= new HashMap();
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("  SELECT emp.dcps_id||'#'||mst.month_id||'#'||trn.type_of_payment,substr(upper(mst.MONTH_NAME),1,3)||'-'||case when mst.month_id >3 then cast(fin.FIN_YEAR_CODE as bigint) else cast((fin.FIN_YEAR_CODE)+1 as bigint) end|| '#'||fin.FIN_YEAR_DESC||'#'||cast(sum(trn.contribution) as dec(15,2))||'#'||mst.month_id||'#'||trn.type_of_payment  from TRN_DCPS_CONTRIBUTION trn     ");
			lSBQuery.append(" inner join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID = trn.FIN_YEAR_ID ");
			lSBQuery.append(" inner join SGVA_MONTH_MST mst on mst.MONTH_ID =trn.MONTH_ID ");
			lSBQuery.append(" inner join mst_dcps_emp emp on emp.dcps_emp_id=trn.dcps_emp_id ");
			lSBQuery.append("  WHERE  ((trn.fin_year_id="+lLngYearId+"  and trn.MONTH_ID <> 3 ) or (trn.fin_year_id="+lLngYearId+"-1  AND trn.MONTH_ID = 3 )) and trn.DCPS_EMP_ID in   (SELECT distinct emp.dcps_emp_id FROM mst_dcps_emp emp  inner join MST_DCPS_DDO_OFFICE off on emp.CURR_OFF=off.DCPS_DDO_OFFICE_MST_ID inner join  TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=emp.DCPS_EMP_ID  inner  join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID=trn.FIN_YEAR_ID where  fin.FIN_YEAR_ID= '"+lLngYearId+"'  and off.DDO_CODE= '"+strDdoCode+"' and trn.STATUS in ('G','H') and  trn.REG_STATUS=1 )  ");
			lSBQuery.append("  AND trn.REG_STATUS = 1  and  trn.EMPLOYER_CONTRI_FLAG = 'Y' and (trn.is_missing_credit is null and trn.IS_CHALLAN is null) and trn.TYPE_OF_PAYMENT<>700047  and trn.voucher_date between '"+fromDate+"' and '"+toDate+"' ");
			lSBQuery.append(" 	group by mst.MONTH_NAME,fin.FIN_YEAR_DESC,mst.month_id,trn.TYPE_OF_PAYMENT,fin.FIN_YEAR_CODE,emp.dcps_id    ");


			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			gLogger.info("Sql query for tier 1 is *************:" + lSBQuery.toString());
			lLstData = lQuery.list();

			if(lLstData!=null && !lLstData.isEmpty()){
				Iterator it =  lLstData.iterator();
				while(it.hasNext()){
					obj = (Object[]) it.next();
					dcps_id = obj[0].toString();
					tier1val=obj[1].toString();
					map.put(dcps_id, tier1val);
				}

			}



		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}

		return map;
	}
	// To get Delayed Tier 1 records
	public HashMap getEmpAllDtlsDelTire1(Long lLngYearId,String fromDate,String toDate,String strDdoCode)
	{
		List lLstData = null;
		List lDblRecovery = new ArrayList();
		Object [] obj=null;
		String dcps_id="";
		String tier1Delval="";
		HashMap map= new HashMap();

		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("  SELECT  emp.dcps_id||'#'||mst.month_id||'#'||trn.type_of_payment,substr(upper(mst.MONTH_NAME),1,3)||'-'||case when mst.month_id >3 then cast(fin.FIN_YEAR_CODE as bigint) else cast((fin.FIN_YEAR_CODE)+1 as bigint) end|| '#'||fin.FIN_YEAR_DESC||'#'||cast(sum(trn.contribution) as dec(15,2))||'#'||mst.month_id||'#'||trn.type_of_payment  from TRN_DCPS_CONTRIBUTION trn  ");
			lSBQuery.append(" inner join SGVC_FIN_YEAR_MST fin on  trn.STARTDATE>=fin.from_date and fin.to_date>=trn.STARTDATE "); 
			lSBQuery.append(" inner join SGVA_MONTH_MST mst on mst.MONTH_ID = month(trn.STARTDATE)  ");
			lSBQuery.append(" inner join mst_dcps_emp emp on emp.dcps_emp_id=trn.dcps_emp_id ");
			lSBQuery.append("  WHERE  trn.fin_year_id="+lLngYearId+" and trn.DCPS_EMP_ID in   (SELECT distinct emp.dcps_emp_id FROM mst_dcps_emp emp  inner join MST_DCPS_DDO_OFFICE off on emp.CURR_OFF=off.DCPS_DDO_OFFICE_MST_ID inner join  TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=emp.DCPS_EMP_ID  inner  join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID=trn.FIN_YEAR_ID where  fin.FIN_YEAR_ID= '"+lLngYearId+"'  and off.DDO_CODE= '"+strDdoCode+"' and trn.STATUS in ('G','H') and  trn.REG_STATUS=1 )  ");
			lSBQuery.append("  AND trn.REG_STATUS = 1  and  trn.EMPLOYER_CONTRI_FLAG = 'Y' and (trn.is_missing_credit is null and trn.IS_CHALLAN is null) and trn.TYPE_OF_PAYMENT=700047  and trn.voucher_date between '"+fromDate+"' and '"+toDate+"'");
			lSBQuery.append(" 	group by emp.dcps_id,mst.MONTH_NAME,fin.FIN_YEAR_DESC,mst.month_id,trn.TYPE_OF_PAYMENT,fin.FIN_YEAR_CODE  ");


			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			gLogger.info("Sql query for tier 1 for delayed is *************:" + lSBQuery.toString());
			lLstData = lQuery.list();

			if(lLstData!=null && !lLstData.isEmpty()){
				Iterator it =  lLstData.iterator();
				while(it.hasNext()){
					obj = (Object[]) it.next();
					dcps_id = obj[0].toString();
					tier1Delval=obj[1].toString();
					map.put(dcps_id, tier1Delval);
				}

			}

		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}

		return map;
	}

	// To get Missing credit Tier 1 records

	public HashMap getEmpAllMissingCreditTire1(Long lLngYearId,String fromDate,String toDate,String strDdoCode)
	{
		List lLstData = null;
		List lDblRecovery = new ArrayList();
		Object [] obj=null;
		String dcps_id="";
		String tier1Misval="";
		HashMap map= new HashMap();

		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT emp.dcps_id||'#'||mst.month_id,substr(upper(mst.MONTH_NAME),1,3)||'-'||case when mst.month_id >3 then cast(fin.FIN_YEAR_CODE as bigint) else cast((fin.FIN_YEAR_CODE)+1 as bigint) end|| '#'||fin.FIN_YEAR_DESC||'#'||cast(sum(trn.contribution) as dec(15,2))||'#'||mst.month_id||'#'||look.LOOKUP_NAME   from TRN_DCPS_CONTRIBUTION trn ");  
			lSBQuery.append(" inner join SGVC_FIN_YEAR_MST fin on  trn.STARTDATE>=fin.from_date and fin.to_date>=trn.STARTDATE "); 
			lSBQuery.append(" inner join SGVA_MONTH_MST mst on mst.MONTH_ID = month(trn.STARTDATE)  ");
			lSBQuery.append(" inner join CMN_LOOKUP_MST look on look.LOOKUP_ID=trn.TYPE_OF_PAYMENT ");
			lSBQuery.append(" inner join mst_dcps_emp emp on emp.dcps_emp_id=trn.dcps_emp_id ");
			lSBQuery.append(" WHERE  trn.MISSING_CREDIT_APPROVAL_DATE between '"+fromDate+"' and '"+toDate+"' and  trn.DCPS_EMP_ID in   (SELECT distinct emp.dcps_emp_id FROM mst_dcps_emp emp  inner join MST_DCPS_DDO_OFFICE off on emp.CURR_OFF=off.DCPS_DDO_OFFICE_MST_ID inner join  TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=emp.DCPS_EMP_ID  inner  join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID=trn.FIN_YEAR_ID where  fin.FIN_YEAR_ID= '"+lLngYearId+"'  and off.DDO_CODE= '"+strDdoCode+"' and trn.STATUS in ('G','H') and  trn.REG_STATUS=1 )  ");  
			lSBQuery.append(" AND trn.REG_STATUS = 1  and  trn.EMPLOYER_CONTRI_FLAG = 'Y'  and (trn.is_missing_credit ='Y' or trn.IS_CHALLAN ='Y')  "); 
			lSBQuery.append(" group by emp.dcps_id,mst.MONTH_NAME,fin.FIN_YEAR_DESC,mst.MONTH_NAME,look.LOOKUP_NAME,mst.MONTH_ID,fin.FIN_YEAR_CODE ");

			lSBQuery.append(" union all ");

			lSBQuery.append(" SELECT emp.dcps_id||'#'||mst.month_id,substr(upper(mst.MONTH_NAME),1,3)||'-'||case when mst.month_id >3 then cast(fin.FIN_YEAR_CODE as bigint) else cast((fin.FIN_YEAR_CODE)+1 as bigint) end|| '#'||fin.FIN_YEAR_DESC||'#'||cast(sum(trn.contribution) as dec(15,2))||'#'||mst.month_id||'#'||look.LOOKUP_NAME   from TRN_DCPS_CONTRIBUTION trn ");   
			lSBQuery.append(" inner join SGVC_FIN_YEAR_MST fin on  trn.STARTDATE>=fin.from_date and fin.to_date>=trn.STARTDATE ");  
			lSBQuery.append(" inner join SGVA_MONTH_MST mst on mst.MONTH_ID = month(trn.STARTDATE)  ");
			lSBQuery.append(" inner join CMN_LOOKUP_MST look on look.LOOKUP_ID=trn.TYPE_OF_PAYMENT ");
			lSBQuery.append(" inner join mst_dcps_emp emp on emp.dcps_emp_id=trn.dcps_emp_id ");
			lSBQuery.append(" WHERE  trn.voucher_date between '"+fromDate+"' and '"+toDate+"' and trn.DCPS_EMP_ID in   (SELECT distinct emp.dcps_emp_id FROM mst_dcps_emp emp  inner join MST_DCPS_DDO_OFFICE off on emp.CURR_OFF=off.DCPS_DDO_OFFICE_MST_ID inner join  TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=emp.DCPS_EMP_ID  inner  join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID=trn.FIN_YEAR_ID where  fin.FIN_YEAR_ID= '"+lLngYearId+"'  and off.DDO_CODE= '"+strDdoCode+"' and trn.STATUS in ('G','H') and  trn.REG_STATUS=1 )  and ((trn.MONTH_ID <> 3 and trn.FIN_YEAR_ID<>"+lLngYearId+") or (trn.MONTH_ID=3 and trn.FIN_YEAR_ID<>"+lLngYearId+"-1)) ");
			lSBQuery.append(" AND trn.REG_STATUS = 1  and  trn.EMPLOYER_CONTRI_FLAG = 'Y'  and (trn.is_missing_credit is null and trn.IS_CHALLAN is null) ");   
			lSBQuery.append(" group by emp.dcps_id,mst.MONTH_NAME,fin.FIN_YEAR_DESC,mst.MONTH_NAME,look.LOOKUP_NAME,mst.MONTH_ID,fin.FIN_YEAR_CODE ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			gLogger.info("Sql query for tier 1 for missing credit  is *************:" + lSBQuery.toString());
			lLstData = lQuery.list();

			if(lLstData!=null && !lLstData.isEmpty()){
				Iterator it =  lLstData.iterator();
				while(it.hasNext()){
					obj = (Object[]) it.next();
					dcps_id = obj[0].toString();
					tier1Misval=obj[1].toString();
					map.put(dcps_id, tier1Misval);
				}
			}	
		}catch (Exception e) {			

			gLogger.error("Error is : " + e, e);
		}

		return map;
	}
	//Commented For missing credit 
	/*public Double getTier1IntMissingDetails(String dcpsId, Long lLngYearId)
	{
		List lLstData = null;
		Double totalInt=null;
		try{
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT NVL(sum(totalInt),0) FROM ");
			lSBQuery.append(" (SELECT NVL((INT_CONTRB_EMPLOYEE + INT_CONTRB_EMPLOYER),0)  as totalInt ");
			lSBQuery.append(" FROM MST_DCPS_CONTRIBUTION_MONTHLY where dcps_id = :dcpsId ");			
			lSBQuery.append(" AND YEAR_ID = "+lLngYearId+" AND (IS_MISSING_CREDIT='Y' or IS_CHALLAN='Y')) ");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("dcpsId", dcpsId);



			lLstData = lQuery.list();
			if(lLstData.size()>0 && lLstData!=null)
				totalInt=Double.parseDouble(lLstData.get(0).toString());
		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}

		return totalInt;
	}*/
	// To get Total amount Missing+Regular amount	
	public HashMap getTier1TotalDetails(Long lLngYearId,String strDdoCode)
	{
		List lLstData = null;
		Object [] obj=null;
		String dcps_id="";
		String intrTotalInterstValue="";
		HashMap map= new HashMap();
		try{
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT emp.dcps_id,sum(NVL((INT_CONTRB_EMPLOYEE + INT_CONTRB_EMPLOYER),0))  as totalInt FROM MST_DCPS_CONTRIBUTION_MONTHLY mon ");
			lSBQuery.append(" inner join mst_dcps_emp emp on emp.DCPS_ID=mon.DCPS_ID where emp.dcps_id in  (SELECT distinct emp.DCPS_ID FROM mst_dcps_emp emp  inner join MST_DCPS_DDO_OFFICE off on emp.CURR_OFF=off.DCPS_DDO_OFFICE_MST_ID inner join  TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=emp.DCPS_EMP_ID  inner  join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID=trn.FIN_YEAR_ID where  fin.FIN_YEAR_ID= '"+lLngYearId+"'  and off.DDO_CODE= '"+strDdoCode+"' and trn.STATUS in ('G','H') and  trn.REG_STATUS=1 ) ");
			//lSBQuery.append(" FROM MST_DCPS_CONTRIBUTION_MONTHLY where dcps_id = :dcpsId  ");			
			//lSBQuery.append(" AND YEAR_ID = "+lLngYearId+" And (IS_MISSING_CREDIT is null and IS_CHALLAN is null)) ");
			lSBQuery.append(" AND YEAR_ID = "+lLngYearId+" group by emp.dcps_id ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			//	lQuery.setParameter("dcpsId", dcpsId);

			gLogger.info("Sql query for Missing+Regular amount  is *************:" + lSBQuery.toString());

			lLstData = lQuery.list();
			if(lLstData!=null && !lLstData.isEmpty()){
				Iterator it =  lLstData.iterator();
				while(it.hasNext()){
					obj = (Object[]) it.next();
					dcps_id = obj[0].toString();
					intrTotalInterstValue=obj[1].toString();
					map.put(dcps_id, intrTotalInterstValue);
				}
			}
		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}


		return map;
	}
	// To get total  amount employee wise
	public HashMap getTotalAmountDetails(Long lLngYearId,String fromDate,String toDate,String strDdoCode)
	{
		List lLstData = null;
		Object [] obj=null;
		String dcps_id="";
		String intrTotalValue="";
		HashMap map= new HashMap();
		try{
			StringBuilder lSBQuery = new StringBuilder();

			//lSBQuery.append(" SELECT emp.dcps_id,nvl(SUM(trn.CONTRIBUTION),0) FROM TRN_DCPS_CONTRIBUTION WHERE DCPS_EMP_ID=:dcpsEmpId  AND (is_missing_credit is null and IS_CHALLAN is null) AND REG_STATUS=1 AND  EMPLOYER_CONTRI_FLAG='Y' and    voucher_date between '"+fromDate+"' and '"+toDate+"' and ((MONTH_ID = 3 and FIN_YEAR_ID="+lLngYearId+"-1) or (MONTH_ID<>3 and FIN_YEAR_ID="+lLngYearId+")) ");

			lSBQuery.append(" SELECT tmp.dcpsid,cast(sum(tmp.con) as double) FROM 	( SELECT emp.dcps_id as dcpsid,cast(sum(trn.contribution) as dec(15,2)) as con from TRN_DCPS_CONTRIBUTION trn    ");
			lSBQuery.append(" inner join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID = trn.FIN_YEAR_ID   ");
			lSBQuery.append(" inner join SGVA_MONTH_MST mst on mst.MONTH_ID =trn.MONTH_ID  inner join mst_dcps_emp emp on emp.dcps_emp_id=trn.dcps_emp_id ");
			lSBQuery.append("  WHERE  ((trn.fin_year_id='"+lLngYearId+"'   and trn.MONTH_ID <> 3 ) or (trn.fin_year_id='"+lLngYearId+"'-1 AND trn.MONTH_ID = 3 )) and trn.DCPS_EMP_ID in    (SELECT distinct emp.dcps_emp_id FROM mst_dcps_emp emp  inner join MST_DCPS_DDO_OFFICE off on emp.CURR_OFF=off.DCPS_DDO_OFFICE_MST_ID inner join  TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=emp.DCPS_EMP_ID  inner  join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID=trn.FIN_YEAR_ID where  fin.FIN_YEAR_ID= '"+lLngYearId+"'   and off.DDO_CODE= '"+strDdoCode+"' and trn.STATUS in ('G','H') and  trn.REG_STATUS=1 )  ");
			lSBQuery.append("  AND trn.REG_STATUS = 1  and  trn.EMPLOYER_CONTRI_FLAG = 'Y' and (trn.is_missing_credit is null and trn.IS_CHALLAN is null) and trn.TYPE_OF_PAYMENT<>700047  and trn.voucher_date between '"+fromDate+"' and '"+toDate+"' 	group by emp.dcps_id   ");



			lSBQuery.append(" union   ");
			lSBQuery.append(" SELECT  emp.dcps_id as dcpsid,cast(sum(trn.contribution) as dec(15,2))  as con  from TRN_DCPS_CONTRIBUTION trn  ");
			lSBQuery.append(" inner join SGVC_FIN_YEAR_MST fin on  trn.STARTDATE>=fin.from_date and fin.to_date>=trn.STARTDATE  ");
			lSBQuery.append(" inner join SGVA_MONTH_MST mst on mst.MONTH_ID = month(trn.STARTDATE)  ");
			lSBQuery.append(" inner join mst_dcps_emp emp on emp.dcps_emp_id=trn.dcps_emp_id ");
			lSBQuery.append(" WHERE  trn.fin_year_id='"+lLngYearId+"'  and trn.DCPS_EMP_ID in   (SELECT distinct emp.dcps_emp_id FROM mst_dcps_emp emp  inner join MST_DCPS_DDO_OFFICE off on emp.CURR_OFF=off.DCPS_DDO_OFFICE_MST_ID inner join  TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=emp.DCPS_EMP_ID  inner  join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID=trn.FIN_YEAR_ID where  fin.FIN_YEAR_ID= '"+lLngYearId+"'   and off.DDO_CODE= '"+strDdoCode+"' and trn.STATUS in ('G','H') and  trn.REG_STATUS=1 )"); 
			lSBQuery.append(" AND trn.REG_STATUS = 1  and  trn.EMPLOYER_CONTRI_FLAG = 'Y' and (trn.is_missing_credit is null and trn.IS_CHALLAN is null) and trn.TYPE_OF_PAYMENT=700047  and trn.voucher_date between  '"+fromDate+"' and '"+toDate+"' ");
			lSBQuery.append(" group by emp.dcps_id )tmp group by tmp.dcpsid ");



			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			gLogger.info("Sql query for Regular  employee wise amount  is *************:" + lSBQuery.toString());


			lLstData = lQuery.list();
			if(lLstData!=null && !lLstData.isEmpty()){
				Iterator it =  lLstData.iterator();
				while(it.hasNext()){
					obj = (Object[]) it.next();
					dcps_id = obj[0].toString();
					intrTotalValue=obj[1].toString();
					map.put(dcps_id, intrTotalValue);
				}
			}
		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}


		return map;
	}
	// To get total  missing credit amount employee wise
	public HashMap getTotalMissingAmountDetails(Long lLngYearId,String fromDate,String toDate,String strDdoCode)
	{
		List lLstData = null;
		Object [] obj=null;
		String dcps_id="";
		String intrTotalMissValue="";
		HashMap map= new HashMap();
		try{
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT tmp.dcpsId,sum(tmp.contri) FROM  "); 
			lSBQuery.append(" (  ");
			lSBQuery.append(" SELECT emp.DCPS_ID as dcpsId,nvl(sum(CONTRIBUTION),0) as contri FROM  TRN_DCPS_CONTRIBUTION trn inner join mst_dcps_emp emp on emp.DCPS_EMP_ID=trn.DCPS_EMP_ID ");
			lSBQuery.append(" where trn.REG_STATUS=1 and MISSING_CREDIT_APPROVAL_DATE between '"+fromDate+"' and '"+toDate+"'  and EMPLOYER_CONTRI_FLAG='Y'  and trn.DCPS_EMP_ID in   (SELECT distinct emp.dcps_emp_id FROM mst_dcps_emp emp  inner join MST_DCPS_DDO_OFFICE off on emp.CURR_OFF=off.DCPS_DDO_OFFICE_MST_ID inner join  TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=emp.DCPS_EMP_ID  inner  join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID=trn.FIN_YEAR_ID where  fin.FIN_YEAR_ID= '"+lLngYearId+"'   and off.DDO_CODE= '"+strDdoCode+"' and trn.STATUS in ('G','H') and  trn.REG_STATUS=1 )  and (is_missing_credit='Y' or IS_CHALLAN='Y') group by emp.DCPS_ID ");

			lSBQuery.append(" UNION ");

			lSBQuery.append(" SELECT emp.DCPS_ID as dcpsId,nvl(sum(CONTRIBUTION),0)  as contri  FROM  TRN_DCPS_CONTRIBUTION trn inner join mst_dcps_emp emp on emp.DCPS_EMP_ID=trn.DCPS_EMP_ID  WHERE trn.REG_STATUS=1 AND VOUCHER_DATE BETWEEN '"+fromDate+"' and '"+toDate+"'  ");
			lSBQuery.append(" and EMPLOYER_CONTRI_FLAG='Y'   and trn.DCPS_EMP_ID in  (SELECT distinct emp.dcps_emp_id FROM mst_dcps_emp emp  inner join MST_DCPS_DDO_OFFICE off on emp.CURR_OFF=off.DCPS_DDO_OFFICE_MST_ID inner join  TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=emp.DCPS_EMP_ID  inner  join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID=trn.FIN_YEAR_ID where  fin.FIN_YEAR_ID= '"+lLngYearId+"'   and off.DDO_CODE= '"+strDdoCode+"' and trn.STATUS in ('G','H') and  trn.REG_STATUS=1 ) AND (is_missing_credit IS NULL and  IS_CHALLAN IS NULL) and  ((MONTH_ID <> 3 and FIN_YEAR_ID<>"+lLngYearId+") or (MONTH_ID=3 and FIN_YEAR_ID<>"+lLngYearId+"-1)) ");
			lSBQuery.append(" group by emp.DCPS_ID ) tmp group by tmp.dcpsId ");



			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			//lQuery.setLong("dcpsEmpId", lLngDcpsEmpId);

			gLogger.info("Sql query for missing credit amount employee wise amount  is *************:" + lSBQuery.toString());

			lLstData = lQuery.list();
			if(lLstData!=null && !lLstData.isEmpty()){
				Iterator it =  lLstData.iterator();
				while(it.hasNext()){
					obj = (Object[]) it.next();
					dcps_id = obj[0].toString();
					intrTotalMissValue=obj[1].toString();
					map.put(dcps_id, intrTotalMissValue);
				}
			}
		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}


		return map;
	}
	// To get Tier 2 details employee wise called
	public HashMap getTier2Details(Long lLngYearId,String strDdoCode)
	{
		List lLstData = null;
		Object [] obj=null;
		String dcps_id="";
		String tier2Records="";
		HashMap map= new HashMap();
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("select emp.DCPS_ID,cast(OPEN_TIER2 as dec(25,2))||'#'||cast(CONTRIB_TIER2 as dec(25,2))||'#'||cast((OPEN_TIER2+CONTRIB_TIER2) as dec(25,2))||'#'||cast(INT_CONTRB_TIER2 as  dec(25,2))||'#'||cast(INT_CONTRB_EMPLOYEE as dec(25,2))||'#'||cast(INT_CONTRB_EMPLOYER as decimal(25,2))||'#'||cast(OPEN_EMPLOYEE as dec(25,2))||'#'||cast(OPEN_EMPLOYER as dec(25,2))||'#'||cast(OPEN_INT as dec(25,2))  ");
			lSBQuery.append(" FROM MST_DCPS_CONTRIBUTION_YEARLY yr inner join mst_dcps_emp emp on emp.DCPS_ID=yr.DCPS_ID WHERE emp.dcps_id in  (SELECT distinct emp.DCPS_ID FROM mst_dcps_emp emp  inner join MST_DCPS_DDO_OFFICE off on emp.CURR_OFF=off.DCPS_DDO_OFFICE_MST_ID inner join  TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=emp.DCPS_EMP_ID  inner  join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID=trn.FIN_YEAR_ID where  fin.FIN_YEAR_ID= '"+lLngYearId+"'  and off.DDO_CODE= '"+strDdoCode+"' and trn.STATUS in ('G','H') and  trn.REG_STATUS=1 )");			
			lSBQuery.append("AND YEAR_ID = :year ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			//	lQuery.setParameter("dcpsId", dcpsId);
			lQuery.setLong("year", lLngYearId);

			lLstData = lQuery.list();

			gLogger.info("Sql query for ier 2 details employee wise is *************:" + lSBQuery.toString());
			lLstData = lQuery.list();
			if(lLstData!=null && !lLstData.isEmpty()){
				Iterator it =  lLstData.iterator();
				while(it.hasNext()){
					obj = (Object[]) it.next();
					dcps_id = obj[0].toString();
					tier2Records=obj[1].toString();
					map.put(dcps_id, tier2Records);
				}
			}
		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}

		return map;
	}
	//To get treasury Name called onlyy once
	public String getLocationName(Long lLngLocId)
	{   
		List lLstDept = null;
		String lStrLocName = "";

		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT LOC_NAME FROM CMN_LOCATION_MST WHERE LOC_ID = :locId");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("locId", lLngLocId);
			lLstDept = lQuery.list();

			if(lLstDept.size() > 0){
				lStrLocName = lLstDept.get(0).toString();
			}
		}catch(Exception e){
			gLogger.error("Exception in getLocationName:" + e, e);
		}
		return lStrLocName;
	}
	//To get DDO name called only once
	public String getDdoName(String lStrDdoCode)
	{
		List lLstData = null;
		String lStrDdoName = "";

		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT DDO_NAME FROM ORG_DDO_MST WHERE DDO_CODE = :ddoCode");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("ddoCode", lStrDdoCode);
			lLstData = lQuery.list();

			if(lLstData.size() > 0){
				lStrDdoName = lLstData.get(0).toString();
			}
		}catch(Exception e){
			gLogger.error("Exception in getDdoName:" + e, e);
		}
		return lStrDdoName;
	}	


	//To get SixPC approved Installment amount employeewsie
	public HashMap getYearlyArrAmountOfEmp(Long lLngYearId,String strDdoCode)
	{
		List lLstData = null;
		Object [] obj=null;
		String dcps_id="";
		String tier2Records="";
		HashMap map= new HashMap();
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT emp.dcps_id,rlt.YEARLY_AMOUNT FROM rlt_dcps_sixpc_yearly rlt inner join mst_dcps_emp emp on emp.DCPS_EMP_ID=rlt.DCPS_EMP_ID ");			
			lSBQuery.append(" where rlt.DCPS_EMP_ID in   (SELECT distinct emp.dcps_emp_id FROM mst_dcps_emp emp  inner join MST_DCPS_DDO_OFFICE off on emp.CURR_OFF=off.DCPS_DDO_OFFICE_MST_ID inner join  TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=emp.DCPS_EMP_ID  inner  join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID=trn.FIN_YEAR_ID where  fin.FIN_YEAR_ID= '"+lLngYearId+"'   and off.DDO_CODE= '"+strDdoCode+"' and trn.STATUS in ('G','H') and  trn.REG_STATUS=1 )  and FIN_YEAR_ID = :year and STATUS_FLAG = 'A' ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			//lQuery.setLong("dcpsEmpId", lLngDcpsEmpId);
			lQuery.setLong("year", lLngYearId);

			lLstData = lQuery.list();
			gLogger.info("Sql query for yearly amount details employee wise is *************:" + lSBQuery.toString());
			if(lLstData!=null && !lLstData.isEmpty()){
				Iterator it =  lLstData.iterator();
				while(it.hasNext()){
					obj = (Object[]) it.next();
					dcps_id = obj[0].toString();
					tier2Records=obj[1].toString();
					map.put(dcps_id, tier2Records);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is : " + e, e);
		}

		return map;
	}

	//To get Interest Rate called only once	
	public List getInterestRatesForGivenYear(Long finYearId) {

		List<Object> lLstReturnList = null;
		StringBuilder lSBQuery = null;
		Query sqlQuery = null;

		try {

			lSBQuery = new StringBuilder();
			Session lObjSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();

			lSBQuery.append(" SELECT IR.INTEREST,IR.EFFECTIVE_FROM,nvl(IR.APPLICABLE_TO,FY.TO_DATE) FROM MST_DCPS_INTEREST_RATE IR LEFT JOIN SGVC_FIN_YEAR_MST FY ON IR.FIN_YEAR_ID = FY.FIN_YEAR_ID WHERE IR.FIN_YEAR_ID =" + finYearId + " ORDER BY IR.EFFECTIVE_FROM ASC");
			sqlQuery = lObjSession.createSQLQuery(lSBQuery.toString());

			lLstReturnList = sqlQuery.list();

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.info("Error " + e);

		}
		return lLstReturnList;
	}


	//For From and To date called once
	public List getFromToDate(Long lLngYearId)
	{
		List lLstData=null;


		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT from_date,TO_DATE  FROM  SGVC_FIN_YEAR_MST  where FIN_year_id=:lLngYearId");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("lLngYearId", lLngYearId);
			lLstData = lQuery.list();


		}catch(Exception e){
			gLogger.error("Exception in getDdoName:" + e, e);
		}
		return lLstData;
	}	
	//Employee wise Interest	
	public HashMap getTier1FinalDetails(Long lLngYearId,String strDdoCode)
	{
		List lLstData = null;
		Object [] obj=null;
		String dcps_id="";
		String empIntRecords="";
		HashMap map= new HashMap();

		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT tmp.dcpsId,cast(sum(tmp.totalEmpInt) as dec(25,2))||'#'||cast(sum(tmp.totalEmplrInt) as dec(25,2)) FROM  ");
			lSBQuery.append(" (SELECT emp.dcps_id as dcpsId,NVL(INT_CONTRB_EMPLOYEE,0) as totalEmpInt,nvl(INT_CONTRB_EMPLOYER,0)  as totalEmplrInt  FROM MST_DCPS_CONTRIBUTION_MONTHLY mon inner join mst_dcps_emp emp on emp.dcps_id=mon.dcps_id where emp.dcps_id in  (SELECT distinct emp.DCPS_ID FROM mst_dcps_emp emp  inner join MST_DCPS_DDO_OFFICE off on emp.CURR_OFF=off.DCPS_DDO_OFFICE_MST_ID inner join  TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=emp.DCPS_EMP_ID  inner  join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID=trn.FIN_YEAR_ID where  fin.FIN_YEAR_ID= '"+lLngYearId+"'  and off.DDO_CODE= '"+strDdoCode+"' and trn.STATUS in ('G','H') and  trn.REG_STATUS=1 )  ");
			lSBQuery.append(" AND YEAR_ID = "+lLngYearId+" and (is_missing_credit is null and IS_CHALLAN is null)) tmp  group by tmp.dcpsId ");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			gLogger.info("Sql query for Employee wise Interest is *************:" + lSBQuery.toString());
			//lQuery.setParameter("dcpsId", dcpsId);
			lLstData = lQuery.list();

			if(lLstData!=null && !lLstData.isEmpty()){
				Iterator it =  lLstData.iterator();
				while(it.hasNext()){
					obj = (Object[]) it.next();
					dcps_id = obj[0].toString();
					empIntRecords=obj[1].toString();
					map.put(dcps_id, empIntRecords);
				}
			}


		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}

		return map;
	}
	//Employee wise Missing credit Interest
	public HashMap getTier1FinalIntMissingDetails(Long lLngYearId,String strDdoCode)
	{
		List lLstData = null;
		Object [] obj=null;
		String dcps_id="";
		String empMissIntRecords="";
		HashMap map= new HashMap();

		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT tmp.dcpsId,cast(sum(tmp.totalEmpInt) as dec(25,2))||'#'||cast(sum(tmp.totalEmplrInt) as dec(25,2)) FROM  ");
			lSBQuery.append(" (SELECT emp.dcps_id as dcpsId,NVL(INT_CONTRB_EMPLOYEE,0) as totalEmpInt,nvl(INT_CONTRB_EMPLOYER,0)  as totalEmplrInt  FROM MST_DCPS_CONTRIBUTION_MONTHLY mon inner join mst_dcps_emp emp on emp.dcps_id=mon.dcps_id where emp.dcps_id in  (SELECT distinct emp.DCPS_ID FROM mst_dcps_emp emp  inner join MST_DCPS_DDO_OFFICE off on emp.CURR_OFF=off.DCPS_DDO_OFFICE_MST_ID inner join  TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=emp.DCPS_EMP_ID  inner  join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID=trn.FIN_YEAR_ID where  fin.FIN_YEAR_ID= '"+lLngYearId+"'  and off.DDO_CODE= '"+strDdoCode+"' and trn.STATUS in ('G','H') and  trn.REG_STATUS=1 ) ");
			lSBQuery.append(" AND YEAR_ID = "+lLngYearId+" AND (IS_MISSING_CREDIT='Y' or IS_CHALLAN='Y')) tmp  group by tmp.dcpsId ");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			gLogger.info("Sql query for Employee wise Missing credit Interest is *************:" + lSBQuery.toString());


			lLstData = lQuery.list();

			if(lLstData!=null && !lLstData.isEmpty()){
				Iterator it =  lLstData.iterator();
				while(it.hasNext()){
					obj = (Object[]) it.next();
					dcps_id = obj[0].toString();
					empMissIntRecords=obj[1].toString();
					map.put(dcps_id, empMissIntRecords);
				}
			}


		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}

		return map;
	}

	public String space(int noOfSpace) {
		String blank = "";
		for (int i = 0; i < noOfSpace; i++) {
			blank += "\u00a0";
		}
		return blank;
	}
	public static double Round(double Rval, int Rpl) {
		double p = (double)Math.pow(10,Rpl);
		Rval = Rval * p;
		double tmp = Math.round(Rval);
		return (double)tmp/p;
	}

}
