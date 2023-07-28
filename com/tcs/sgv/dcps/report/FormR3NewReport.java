package com.tcs.sgv.dcps.report;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;


public class FormR3NewReport extends DefaultReportDataFinder implements ReportDataFinder
{
	private static final Logger gLogger = Logger.getLogger(FormR3NewReport.class);
	public static String newline = System.getProperty("line.separator");
	ServiceLocator serviceLocator = null;
	SessionFactory gObjSessionFactory = null;
	String gStrLocCode = null;
	Long gLngLangId = null;
	Long gLngPostId = null;
	Session ghibSession = null;	


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
			List lLstOpeningBalcTier1New = null;
			List lLstEmpTier1 = null;
			Double lTotalAmt=null;
			Double lTotalAmtNew=null;
			Double lTotalMissingamt=0d;
			Double lTotalMissingamtNew=0d;
			List lLstDtlsTier2 = null;
			List lLstDtlsFinalTier1=null;
			List lLstDtlsFinalMissingTier1=null;
			List lLstDtlsTier2New = null;
			List lLstDtlsFinalTier1New=null;
			List lLstDtlsFinalMissingTier1New=null;
			Object []lObj = null;
			Object []lObjTier1 = null;
			Object []lObjTier1New = null;
			Object []lObjMissingTier1 = null;
			Object []lObjMissingTier1New = null;
			String lStrOpeningEmpTier1 = "";
			String lStrOpeningEmployrTier1 = "";
			String lStrOpeningEmpTier1New = "";
			String lStrOpeningEmployrTier1New = "";
			String lStrInterestTier1 = "";	
			String lStrInterestTier1New = "";			
			List lDblRegRecvryEmp = null;
			List lDblRegRecvryEmpNew = null;

			List TotalRegular = null;
			List lDblRegRecvryEmplyr = null;
			List lDblDelyRecvryEmp = null;
			List lDblDelyRecvryEmpNew = null;
			List lDblDelyRecvryEmplyr = null;
			List lDblDAArrearEmp = null;
			List lDblDAArrearEmpNew = null;
			List lDblDAArrearEmplyr = null;
			List lDblPayArrearEmp = null;
			List lDblPayArrearEmpNew = null;
			List lDbMissingCreditTier1=null;
			List lDbMissingCreditTier1New=null;
			Double empMisIntTier=0d;
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
			Long lLngOpeningTier2New = 0l;
			Long lLngYearlyContriTier2New = 0l;
			Long lLngTotalEmpT2New = 0l;
			Long lLngTotalIntrstT2New = 0l;
			Double empIntrstT2 = 0d;
			Double empIntrstT2New = 0d;
			Double empMissingIntrstT2 = 0d;
			Double empMissingIntrstT2New = 0d;
			Double  emplrIntrstT2 = 0d;
			Double  emplrIntrstT2New = 0d;
			Double  emplrMissingIntrstT2 = 0d;
			Double  emplrMissingIntrstT2New = 0d;
			Double lOpeningEmpTier1 = 0d;
			Double lOpeningEmployrTier1 = 0d;
			Double lInterestTier1=0d;
			Double lOpeningEmpTier1New = 0d;
			Double lOpeningEmployrTier1New = 0d;
			Double lInterestTier1New=0d;
			Double openEmployeeInt=0d; 
			Double  openEmployerInt=0d;			
			Double openEmployeeIntNew=0d; 
			Double  openEmployerIntNew=0d;

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

			if (lObjReport.getReportCode().equals("80000830")) 
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
				
				Long lLngTreasuryId=0l;
				if (lObjReport.getParameterValue("treasuryCode")!=null && !"".equalsIgnoreCase(lObjReport.getParameterValue("treasuryCode").toString()))
				{
					lLngTreasuryId = Long.parseLong((lObjReport.getParameterValue("treasuryCode").toString().trim()));

					
				}

				
				String lStrDdoCode = (String)lObjReport.getParameterValue("ddoCode");
				String lStrDcpsEmpId = (String)lObjReport.getParameterValue("dcpsEmpId");
				Long lLngYearId = Long.valueOf((String)lObjReport.getParameterValue("yearId"));
				System.out.println("yearId is **** "+lLngYearId);
				DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(SgvcFinYearMst.class, this.serviceLocator.getSessionFactory());
				String lStrTreasuryName = getLocationName(lLngTreasuryId);
				String lStrYear = lObjDcpsCommonDAO.getFinYearForYearId(lLngYearId);
				String lStrDdoName="";
				if (lStrDdoCode!=null && !"".equalsIgnoreCase(lStrDdoCode))
				{
					 lStrDdoName = getDdoName(lStrDdoCode);
				}
				
				String lStrEmpName = getEmpName(Long.valueOf(Long.parseLong(lStrDcpsEmpId)));
				MstEmp objEmpData = lObjDcpsCommonDAO.getEmpVOForEmpId(Long
						.valueOf(lStrDcpsEmpId));
				String dcpsId = objEmpData.getDcpsId().trim();
				String empName = objEmpData.getName();
				Long empId = objEmpData.getDcpsEmpId();
				Date DOB = objEmpData.getDob();
				Date DOJ = objEmpData.getDoj();
				String basicPay = objEmpData.getBasicPay().toString();
				String ddoName="";
				if (lStrDdoCode!=null && !"".equalsIgnoreCase(lStrDdoCode))
				{
					 ddoName = lObjDcpsCommonDAO.getDdoNameForCode(lStrDdoCode);
				}
				
				String payComm = objEmpData.getPayCommission();
				List lstYearDates = lObjDcpsCommonDAO
				.getDatesFromFinYearId(lLngYearId);
				String missingamt =  null;
				String missingamtNew =  null;
				String missingTotalAmt =  null;
				String missingTotalAmtNew =  null;			
				Object[] objDates = (Object[]) lstYearDates.get(0);
				Date startDate = (Date) objDates[0];
				Date endDate = (Date) objDates[1];
				List interestRates = getInterestRatesForGivenYear(lLngYearId);
				Float daRate = lObjDcpsCommonDAO.getCurrentDARate(payComm);
				Float DP = null;
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
				gLogger.info("############ location code is and post id is  : #### " +gLngPostId+" # "+gStrLocCode);
				gLogger.info("From date is" +fromDate);
				gLogger.info("To date is" +toDate);
				if (payComm.equals("700015")) {
					DP = new Float(Double.parseDouble(basicPay) * 50 / 100);
					daRate = new Float(Double.parseDouble(basicPay) * daRate
							/ 100);
				} else {
					DP = new Float(0);
					daRate = new Float((Double.parseDouble(basicPay) + DP)* daRate / 100);
				}

				String lStrPostEmpContriDoneOrNot = null;

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
				//lObjReport.setAdditionalHeader(new StyledData(space(145)+" Form R3"+newline+space(50)+"(As referred to in para no.18 & 23 of Government Resolution, Finance Department 1007/18/SER- 4, dated 7 July, 2007 )"+newline+space(100)+"OFFICE OF THE STATE RECORD KEEPING AGENCY, MUMBAI"+newline+space(80)+"Statement of Account in respect of contributions under the D.C.P.Scheme For the Year"+lStrYear,boldVO));
				StyledData	dataStyle = new StyledData();
				ArrayList rowList1 = new ArrayList();
				ArrayList headerList = new ArrayList();
				dataStyle = new StyledData();
				dataStyle.setColspan(1);
				dataStyle.setStyles(CenterAlignVO);
				dataStyle.setData("Form R3");
				rowList1.add(dataStyle);
	//			rowList1.add("Form R3");

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
				
				rowList1 = new ArrayList();
				dataStyle = new StyledData();
				dataStyle.setColspan(1);
				dataStyle.setStyles(CenterAlignVO);
				dataStyle.setData("Fin Year : "+ lStrYear);
				rowList1.add(dataStyle);
				dataStyle = null;
				//dataList.add(rowList1);
				headerList.add(rowList1);

				TabularData tData  = new TabularData(headerList);
				tData.addStyle(IReportConstants.STYLE_FONT_ALIGNMENT, IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
				tData.addStyle(IReportConstants.BORDER, "No");
				tData.addStyle(IReportConstants.SHOW_REPORT_NAME, IReportConstants.VALUE_NO);
				lObjReport.setAdditionalHeader(tData);	

				rowList1 = new ArrayList();

				rowList1.add(new StyledData("Name of the Employee : " + empName ,boldVO));
				dataList.add(rowList1);

				rowList1 = new ArrayList();
				rowList1.add(new StyledData("Pension Account Number : " + dcpsId ,boldVO));
				dataList.add(rowList1);

				rowList1 = new ArrayList();
				rowList1.add(new StyledData("Birth Date : "+ dateFormat2.format(DOB).toString() + space(70)+ "Date Of Joining : " + dateFormat2.format(DOJ).toString() ,boldVO));
				dataList.add(rowList1);


				rowList1 = new ArrayList();
				rowList1.add(new StyledData("Treasury : (" + lLngTreasuryId.toString() + ") "+ lStrTreasuryName ,boldVO));
				dataList.add(rowList1);




				String lStrFromDateInt = "";
				String lStrToDateInt = "";
				Double lDoubleIntRate = null;
				Object[] lObjIntDtls = null;

				for(Integer lInt=0;lInt<interestRates.size();lInt++)
				{
					rowList1 = new ArrayList();
					lObjIntDtls = (Object[]) interestRates.get(lInt);
					lDoubleIntRate = Double.valueOf(lObjIntDtls[0].toString().trim());
					lDoubleIntRate = Round(lDoubleIntRate,2);
					lStrFromDateInt = dateFormat6.format(dateFormat2.parse(lObjIntDtls[1].toString().trim()));
					lStrToDateInt = dateFormat6.format(dateFormat2.parse(lObjIntDtls[2].toString().trim()));
		//			rowList1.add(lDoubleIntRate + " ( " + lStrFromDateInt + " to " + lStrToDateInt + " )");
		//			dataList.add(rowList1);
				}

				
				rowList1 = new ArrayList();
				rowList1.add(new StyledData("Interest Rate "+ lDoubleIntRate + " ( " + lStrFromDateInt + " to " + lStrToDateInt + " )  ",boldVO));
				dataList.add(rowList1);

				rowList1 = new ArrayList();
				rowList1.add(new StyledData("DDO :("+lStrDdoCode+")"+ ddoName, boldVO));
				dataList.add(rowList1);

				rowList1 = new ArrayList();
				rowList1.add(new StyledData("From (Month) : "
						+ dateFormat3.format(startDate).toString() + space(50)
						+ "To (Month) : "
						+ dateFormat3.format(endDate).toString() ,boldVO));
				dataList.add(rowList1);

				rowList1 = new ArrayList();
				rowList1.add(new StyledData("Basic Pay :" + basicPay + space(70) + "D.P. :"
						+ DP + space(70) + "D.A. :" + Math.round(daRate)
						,boldVO));
				dataList.add(rowList1);


				/*	
				lObjReport.setAdditionalHeader("Treasury Name :" + lStrTreasuryName + "\r\nDDO Name :" + lStrDdoName + "\r\nEmployee Name :" + lStrEmpName + 
						"\r\nFinancial Year :" + lStrYear);*/
				lLstOpeningBalcTier1 = getOpeningBalanceTier1(Long.valueOf(Long.parseLong(lStrDcpsEmpId)), lLngYearId);

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
				
				lLstOpeningBalcTier1New = getOpeningBalanceTier1New(Long.valueOf(Long.parseLong(lStrDcpsEmpId)), lLngYearId);

				if ((lLstOpeningBalcTier1New != null) && (lLstOpeningBalcTier1New.size() > 0))
				{
					lObj = (Object[])lLstOpeningBalcTier1New.get(0);
					if (lObj[0] != null)
						lStrOpeningEmpTier1New = lObj[0].toString();

					if (lObj[1] != null)
						lStrOpeningEmployrTier1New = lObj[1].toString();

					if (lObj[2] != null)
						lStrInterestTier1New = lObj[2].toString();

				}


				ArrayList rowList = new ArrayList();

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
				rowList.add("Opening Balance (A)");
				rowList.add("");
				rowList.add("<p>OLD:- "+lStrOpeningEmpTier1+"</p>"+"<p> NEW:-  "+lStrOpeningEmpTier1New+"</p>");
				rowList.add("<p>OLD:- "+lStrOpeningEmployrTier1+"</p>"+"<p> NEW:-  "+lStrOpeningEmployrTier1New+"</p>");
				if ((lLstOpeningBalcTier1 != null) && (lLstOpeningBalcTier1.size() > 0))
				rowList.add("<p>OLD:- "+Round(Double.valueOf(lStrInterestTier1),2)+"</p>"+"<p> NEW:-  "+Round(Double.valueOf(lStrInterestTier1New),2)+"</p>");
				else
				rowList.add("<p>OLD:- "+lStrInterestTier1+"</p>"+"<p> NEW:-  "+lStrInterestTier1New+"</p>");	
				
				lLstDataList.add(rowList);

				String  month11 = null;
				String  month = null;
				String year = null;
				String EmpAmount =  null;
				String EmpAmountNew =  null;

				String total =  null;
				String totalNew =  null;

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
				rowList.add("Regular Contribution (B)");
				rowList.add("");
				rowList.add("");
				rowList.add("");
				rowList.add("");
				lLstDataList.add(rowList);

				lDblRegRecvryEmp = getEmpAllDtlsTire1(Long.parseLong(lStrDcpsEmpId), lLngYearId, "700046",fromDate,toDate);
				lDblRegRecvryEmpNew = getEmpAllDtlsTire1New(Long.parseLong(lStrDcpsEmpId), lLngYearId, "700046",fromDate,toDate);

				for(int monID=3;monID<=14;monID++)
				{
					int newIndex = monID;
					if(monID > 12)
						newIndex = monID - 12;
					
					int newIndexNew = monID;
					if(monID > 12)
						newIndexNew = monID - 12;

					rowList = new ArrayList();
					boolean prnitOrNot = true;


					for (int i =0, j=0; i < lDblRegRecvryEmp.size() &&  j < lDblRegRecvryEmpNew.size(); i++,j++) {

						Object[]obj = (Object[]) lDblRegRecvryEmp.get(i);	
						Object[]objNew = (Object[]) lDblRegRecvryEmpNew.get(j);	

						if((newIndex == Integer.parseInt(obj[3].toString()))&&(newIndexNew == Integer.parseInt(objNew[3].toString()))){


							EmpAmount	=((obj[2]!=null) ? obj[2].toString(): "0**");
							gLogger.info("Old empAmount  is------------"+EmpAmount );

							EmpAmountNew	=((objNew[2]!=null) ? objNew[2].toString(): "0**");
							gLogger.info("New EmpAmountNew  is------------"+EmpAmountNew );

							total	=((obj[4]!=null) ? obj[4].toString(): "0**");
							gLogger.info("Old total  is------------"+total );

							totalNew	=((objNew[4]!=null) ? objNew[4].toString(): "0**");
							gLogger.info("New totalNew  is------------"+totalNew );

							

							rowList.add("");
							rowList.add(" ");
							rowList.add(new StyledData(obj[0],boldVO));
							rowList.add("<p>OLD:- "+EmpAmount+"<p>"+"<p> NEW:-  "+EmpAmountNew+"</p>");
							rowList.add("<p>OLD:- "+EmpAmount+"</p>"+"<p> NEW:-  "+EmpAmountNew+"</p>");
							rowList.add("<p>OLD:- "+total+"<p>"+"<p> NEW:-  "+totalNew+"</p>");
							prnitOrNot = false;
							break;

						}
					}

					if(prnitOrNot) 
					{
						rowList.add("");
						rowList.add("");
						rowList.add(new StyledData(arrMonths[newIndex-1],boldVO));
						rowList.add("<p>OLD:-  0**</p><p>NEW:-  0**</p>");
						rowList.add("<p>OLD:-  0</p><p>NEW:-  0</p>");
						rowList.add("<p>OLD:-  0</p><p>NEW:-  0</p>");
					}
					lLstDataList.add(rowList);
				}
			
				rowList = new ArrayList();
				rowList.add("3");
				rowList.add("Delayed Recovery (C)");
				rowList.add("");
				rowList.add("");
				rowList.add("");
				rowList.add("");
				lLstDataList.add(rowList);

				String  month12 = null;
				String  month1 = null;
				String year1 = null;
				String EmpAmount1 =  null;



				lDblDelyRecvryEmp = getEmpAllDtlsTire1(Long.parseLong(lStrDcpsEmpId), lLngYearId, "700047",fromDate,toDate);
				lDblDelyRecvryEmpNew = getEmpAllDtlsTire1New(Long.parseLong(lStrDcpsEmpId), lLngYearId, "700047",fromDate,toDate);
				
				for(int monID=3;monID<=14;monID++)
				{
					int newIndex = monID;
					if(monID > 12)
						newIndex = monID - 12;
					
					int newIndexNew = monID;
					if(monID > 12)
						newIndexNew = monID - 12;

					for (int i =0, j=0; i < lDblRegRecvryEmp.size() &&  j < lDblDelyRecvryEmpNew.size(); i++,j++) {

						Object[]obj = (Object[]) lDblRegRecvryEmp.get(i);	
						Object[]objNew = (Object[]) lDblDelyRecvryEmpNew.get(j);	

						if((newIndex == Integer.parseInt(obj[3].toString()))&&(newIndexNew == Integer.parseInt(objNew[3].toString()))){

							EmpAmount	=((obj[2]!=null) ? obj[2].toString(): "");
							gLogger.info("Old empAmount1  is------------"+EmpAmount );

							EmpAmountNew	=((objNew[2]!=null) ? objNew[2].toString(): "");
							gLogger.info("New EmpAmountNew1  is------------"+EmpAmountNew );
							total	=((obj[4]!=null) ? obj[4].toString(): "0**");
							gLogger.info("Old total  is------------"+total );

							totalNew	=((objNew[4]!=null) ? objNew[4].toString(): "0**");
							gLogger.info("New totalNew  is------------"+totalNew );

							rowList.add("");
							rowList.add(" ");
							rowList.add(new StyledData(obj[0],boldVO));
							rowList.add("<p>OLD:- "+EmpAmount+"<p>"+"<p> NEW:-  "+EmpAmountNew+"</p>");
							rowList.add("<p>OLD:- "+EmpAmount+"</p>"+"<p> NEW:-  "+EmpAmountNew+"</p>");
							rowList.add("<p>OLD:- "+total+"<p>"+"<p> NEW:-  "+totalNew+"</p>");
							lLstDataList.add(rowList);
							break;
						}
					}
				}

			
				rowList = new ArrayList();
				rowList.add("4");
				rowList.add("DA Arrears (D)");
				rowList.add("");
				rowList.add("");
				rowList.add("");
				rowList.add("");
				lLstDataList.add(rowList);

				String month13=null;
				String  month2 = null;
				String year2 = null;
				String EmpAmount2 =  null;


				lDblDAArrearEmp = getEmpAllDtlsTire1(Long.parseLong(lStrDcpsEmpId), lLngYearId, "700048",fromDate,toDate);

				lDblDAArrearEmpNew = getEmpAllDtlsTire1New(Long.parseLong(lStrDcpsEmpId), lLngYearId, "700048",fromDate,toDate);
				
				for(int monID=3;monID<=14;monID++)
				{
					int newIndex = monID;
					if(monID > 12)
						newIndex = monID - 12;
					
					int newIndexNew = monID;
					if(monID > 12)
						newIndexNew = monID - 12;

					for (int i =0, j=0; i < lDblRegRecvryEmp.size() &&  j < lDblDAArrearEmpNew.size(); i++,j++) {

						Object[]obj = (Object[]) lDblRegRecvryEmp.get(i);	
						Object[]objNew = (Object[]) lDblDAArrearEmpNew.get(j);	

						if((newIndex == Integer.parseInt(obj[3].toString()))&&(newIndexNew == Integer.parseInt(objNew[3].toString()))){

							EmpAmount	=((obj[2]!=null) ? obj[2].toString(): "");
							gLogger.info("Old empAmount1  is------------"+EmpAmount );

							EmpAmountNew	=((objNew[2]!=null) ? objNew[2].toString(): "");
							gLogger.info("New EmpAmountNew1  is------------"+EmpAmountNew );

							total	=((obj[4]!=null) ? obj[4].toString(): "0**");
							gLogger.info("Old total  is------------"+total );

							totalNew	=((objNew[4]!=null) ? objNew[4].toString(): "0**");
							gLogger.info("New totalNew  is------------"+totalNew );

							rowList.add("");
							rowList.add(" ");
							rowList.add(new StyledData(obj[0],boldVO));
							rowList.add("<p>OLD:- "+EmpAmount+"<p>"+"<p> NEW:-  "+EmpAmountNew+"</p>");
							rowList.add("<p>OLD:- "+EmpAmount+"</p>"+"<p> NEW:-  "+EmpAmountNew+"</p>");
							rowList.add("<p>OLD:- "+total+"<p>"+"<p> NEW:-  "+totalNew+"</p>");
							lLstDataList.add(rowList);
							break;
						}
					}
				}
				
				
				
				
				
				

				rowList = new ArrayList();
				rowList.add("5");
				rowList.add("Pay Arrears (E)");
				rowList.add("");
				rowList.add("");
				rowList.add("");
				rowList.add("");
				lLstDataList.add(rowList);

				String  month14 = null;
				String  month3 = null;
				String year3 = null;
				String EmpAmount3 =  null;

				lDblPayArrearEmp = getEmpAllDtlsTire1(Long.parseLong(lStrDcpsEmpId), lLngYearId, "700049",fromDate,toDate);
				
				lDblPayArrearEmpNew = getEmpAllDtlsTire1New(Long.parseLong(lStrDcpsEmpId), lLngYearId, "700049",fromDate,toDate);
				
				for(int monID=3;monID<=14;monID++)
				{
					int newIndex = monID;
					if(monID > 12)
						newIndex = monID - 12;
					
					int newIndexNew = monID;
					if(monID > 12)
						newIndexNew = monID - 12;

					for (int i =0, j=0; i < lDblRegRecvryEmp.size() &&  j < lDblPayArrearEmpNew.size(); i++,j++) {

						Object[]obj = (Object[]) lDblRegRecvryEmp.get(i);	
						Object[]objNew = (Object[]) lDblPayArrearEmpNew.get(j);	

						if((newIndex == Integer.parseInt(obj[3].toString()))&&(newIndexNew == Integer.parseInt(objNew[3].toString()))){

							EmpAmount	=((obj[2]!=null) ? obj[2].toString(): "");
							gLogger.info("Old empAmount1  is------------"+EmpAmount );

							EmpAmountNew	=((objNew[2]!=null) ? objNew[2].toString(): "");
							gLogger.info("New EmpAmountNew1  is------------"+EmpAmountNew );

							total	=((obj[4]!=null) ? obj[4].toString(): "0**");
							gLogger.info("Old total  is------------"+total );

							totalNew	=((objNew[4]!=null) ? objNew[4].toString(): "0**");
							gLogger.info("New totalNew  is------------"+totalNew );

							rowList.add("");
							rowList.add(" ");
							rowList.add(new StyledData(obj[0],boldVO));
							rowList.add("<p>OLD:- "+EmpAmount+"<p>"+"<p> NEW:-  "+EmpAmountNew+"</p>");
							rowList.add("<p>OLD:- "+EmpAmount+"</p>"+"<p> NEW:-  "+EmpAmountNew+"</p>");
							rowList.add("<p>OLD:- "+total+"<p>"+"<p> NEW:-  "+totalNew+"</p>");
							lLstDataList.add(rowList);
							break;
						}
					}
				}		

				rowList = new ArrayList();
				rowList.add("6");
				rowList.add("Mising Credit");
				rowList.add("");
				rowList.add("");
				rowList.add("");
				rowList.add("");
				lLstDataList.add(rowList);

				String  missingMon = null;
				String  missingMonth = null;
				String missingYearDesc = null;

				String[] arrYearSplit;
				
				lDbMissingCreditTier1 = getEmpAllMissingCreditTire1(Long.parseLong(lStrDcpsEmpId), fromDate,toDate,lLngYearId);
				lDbMissingCreditTier1New = getEmpAllMissingCreditTire1New(Long.parseLong(lStrDcpsEmpId), fromDate,toDate,lLngYearId);

				empMisIntTier=getTier1IntMissingDetails(Long.parseLong(lStrDcpsEmpId), lLngYearId);////////////pending
				for(int monID=3;monID<=14;monID++)
				{
					int newIndex = monID;
					if(monID > 12)
						newIndex = monID - 12;
					
					int newIndexNew = monID;
					if(monID > 12)
						newIndexNew = monID - 12;
					gLogger.info("Size of Missing records  is------------" + lDbMissingCreditTier1.size());

						for (int i =0, j=0; i < lDbMissingCreditTier1.size() &&  j < lDbMissingCreditTier1New.size(); i++,j++) {

						rowList = new ArrayList();
						Object[]obj = (Object[]) lDbMissingCreditTier1.get(i);	
						Object[]objNew = (Object[]) lDblPayArrearEmpNew.get(j);	

						if((newIndex == Integer.parseInt(obj[3].toString()))&&(newIndexNew == Integer.parseInt(objNew[3].toString()))){
							missingamt	=((obj[2]!=null) ? obj[2].toString(): "");
							gLogger.info("missingamt  is------------"+missingamt );
							gLogger.info("empMisIntTier  is------------"+empMisIntTier );
							
							missingamtNew	=((objNew[2]!=null) ? objNew[2].toString(): "");
							gLogger.info("missingamtNew  is------------"+missingamtNew );
							
							missingTotalAmt	=((obj[5]!=null) ? obj[5].toString(): "");
							gLogger.info("missingTotalAmt  is------------"+missingTotalAmt );
							
							missingTotalAmtNew	=((objNew[5]!=null) ? objNew[5].toString(): "");
							gLogger.info("missingTotalAmtNew  is------------"+missingTotalAmtNew );
							
							rowList.add("");
							rowList.add(" ");

								rowList.add(new StyledData(obj[0]+"-"+obj[4],boldVO));

								rowList.add("<p>OLD:- "+missingamt+"<p>"+"<p> NEW:-  "+missingamtNew+"</p>");	
								rowList.add("<p>OLD:- "+missingamt+"<p>"+"<p> NEW:-  "+missingamtNew+"</p>");	
								rowList.add("<p>OLD:- "+missingTotalAmt+"<p>"+"<p> NEW:-  "+missingTotalAmtNew+"</p>");	

							lLstDataList.add(rowList);

						}
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
				

				lTotalAmt=getTotalAmountDetails(Long.parseLong(lStrDcpsEmpId), lLngYearId, fromDate, toDate);
				lTotalMissingamt=getTotalMissingAmountDetails(Long.parseLong(lStrDcpsEmpId), lLngYearId, fromDate, toDate);
				
				lTotalAmtNew =getTotalAmountDetailsNew(Long.parseLong(lStrDcpsEmpId), lLngYearId, fromDate, toDate);
				lTotalMissingamtNew=getTotalMissingAmountDetailsNew(Long.parseLong(lStrDcpsEmpId), lLngYearId, fromDate, toDate);
				
				gLogger.info("lTotalMissingamt  is------------"+lTotalMissingamt );
				gLogger.info("lTotalAmt  is------------"+lTotalAmt );

				gLogger.info("lTotalMissingamtNew  is------------"+lTotalMissingamtNew );
				gLogger.info("lTotalAmtNew  is------------"+lTotalAmtNew );
				
				rowList = new ArrayList();
				rowList.add("");
				rowList.add(new StyledData("Total Missing Credit Amount (F)",boldVO));
				rowList.add("");


				if(lTotalMissingamt!=null)
				{
					rowList.add("<p>OLD:- "+lTotalMissingamt+"<p>"+"<p> NEW:-  "+lTotalMissingamtNew+"</p>");	
					rowList.add("<p>OLD:- "+lTotalMissingamt+"<p>"+"<p> NEW:-  "+lTotalMissingamtNew+"</p>");	
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


				Double empTier = getTier1TotalDetails(Long.parseLong(lStrDcpsEmpId), lLngYearId);
				lTotalAmt=getTotalAmountDetails(Long.parseLong(lStrDcpsEmpId), lLngYearId, fromDate, toDate);
				
				Double empTierNew = getTier1TotalDetailsNew(Long.parseLong(lStrDcpsEmpId), lLngYearId);
				lTotalAmtNew=getTotalAmountDetailsNew(Long.parseLong(lStrDcpsEmpId), lLngYearId, fromDate, toDate);

				gLogger.info("lTotalMissingamt  is------------"+lTotalMissingamt );
				gLogger.info("lTotalAmt  is------------"+lTotalAmt );
				
				Double finalTotal = lTotalAmt+lTotalMissingamt+lTotalAmt+lTotalMissingamt;
				Double finalTotalNew = lTotalAmtNew+lTotalMissingamtNew+lTotalAmtNew+lTotalMissingamtNew;

				rowList = new ArrayList();
				rowList.add("");
//				rowList.add(new StyledData("TIER -1 Total For F.Y. "+lStrYear,boldVO));
				rowList.add(new StyledData("TIER -1 Total For F.Y. "+lStrYear+"\n (G = A+B+C+D+E+F)",boldVO));
				rowList.add("");


				if(lTotalAmt != null || lTotalMissingamt!=null)
				{
					rowList.add("<p>OLD:- "+lTotalAmt+lTotalMissingamt+"<p>"+"<p> NEW:-  "+lTotalAmtNew+lTotalMissingamtNew+"</p>");	
					rowList.add("<p>OLD:- "+lTotalAmt+lTotalMissingamt+"<p>"+"<p> NEW:-  "+lTotalAmtNew+lTotalMissingamtNew+"</p>");	
				}
				else{
					rowList.add(0);
					rowList.add(0);
				}
				if(empTier==null)
				{
					empTier=0d;
				}
				if(empTier != null || empMisIntTier!=null)
				{
					rowList.add("<p>OLD:- "+finalTotal+"<p>"+"<p> NEW:-  "+finalTotalNew+"</p>");

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
				lLstDtlsTier2 = getTier2Details(Long.parseLong(lStrDcpsEmpId), lLngYearId);
				lLstDtlsFinalTier1=getTier1FinalDetails(Long.parseLong(lStrDcpsEmpId), lLngYearId);
				lLstDtlsFinalMissingTier1=getTier1FinalIntMissingDetails(Long.parseLong(lStrDcpsEmpId), lLngYearId);

				lLstDtlsTier2New = getTier2DetailsNew(Long.parseLong(lStrDcpsEmpId), lLngYearId);
				lLstDtlsFinalTier1New=getTier1FinalDetailsNew(Long.parseLong(lStrDcpsEmpId), lLngYearId);
				lLstDtlsFinalMissingTier1New=getTier1FinalIntMissingDetailsNew(Long.parseLong(lStrDcpsEmpId), lLngYearId);
				
				//Details For Tier1 interest for employee and employer
				if(lLstDtlsFinalTier1 != null && lLstDtlsFinalTier1.size() > 0)
				{
					lObjTier1 = (Object[]) lLstDtlsFinalTier1.get(0);
					if(lObjTier1[0] != null){
						empIntrstT2 = Double.parseDouble(lObjTier1[0].toString());
					}

					gLogger.info("empInt is "+empIntrstT2);
					if(lObjTier1[1] != null){
						emplrIntrstT2 =Double.parseDouble(lObjTier1[1].toString());
					}

				}
				if(lLstDtlsFinalTier1New != null && lLstDtlsFinalTier1New.size() > 0)
				{
					lObjTier1New = (Object[]) lLstDtlsFinalTier1New.get(0);
					if(lObjTier1New[0] != null){
						empIntrstT2New = Double.parseDouble(lObjTier1New[0].toString());
					}

					gLogger.info("empInt is "+empIntrstT2);
					if(lObjTier1New[1] != null){
						emplrIntrstT2New =Double.parseDouble(lObjTier1New[1].toString());
					}

				}

				//Details For Tier1 Missing interest for employee and employer

				if(lLstDtlsFinalMissingTier1 != null && lLstDtlsFinalMissingTier1.size() > 0)
				{
					lObjMissingTier1 = (Object[]) lLstDtlsFinalMissingTier1.get(0);
					if(lObjMissingTier1[0] != null){
						empMissingIntrstT2 = Double.parseDouble(lObjMissingTier1[0].toString());
					}


					if(lObjMissingTier1[1] != null){
						emplrMissingIntrstT2 =Double.parseDouble(lObjMissingTier1[1].toString());
					}

				}
				if(lLstDtlsFinalMissingTier1New != null && lLstDtlsFinalMissingTier1New.size() > 0)
				{
					lObjMissingTier1New = (Object[]) lLstDtlsFinalMissingTier1New.get(0);
					if(lObjMissingTier1New[0] != null){
						empMissingIntrstT2New = Double.parseDouble(lObjMissingTier1New[0].toString());
					}


					if(lObjMissingTier1New[1] != null){
						emplrMissingIntrstT2New =Double.parseDouble(lObjMissingTier1New[1].toString());
					}

				}

				if(lLstDtlsTier2 != null && lLstDtlsTier2.size() > 0)
				{

					lObj = (Object[]) lLstDtlsTier2.get(0);
					if(lObj[0] != null){
						lLngOpeningTier2 = Math.round(Double.parseDouble(lObj[0].toString()));
					}
					if(lObj[1] != null){
						//lLngYearlyContriTier2 = Math.round(Double.parseDouble(lObj[1].toString()));
						lLngYearlyContriTier2 = Math.round(getYearlyArrAmountOfEmp(Long.parseLong(lStrDcpsEmpId),lLngYearId));
					}
					if(lObj[2] != null){
						lLngTotalEmpT2 = Math.round(Double.parseDouble(lObj[2].toString()));
					}
					if(lObj[3] != null){
						lLngTotalIntrstT2 = Math.round(Double.parseDouble(lObj[3].toString()));
					}
					gLogger.info("empInt is obj4 "+lObj[4].toString());


					if(lObj[6] != null){
						lOpeningEmpTier1  =Double.parseDouble(lObj[6].toString());
					}
					if(lObj[7] != null){
						lOpeningEmployrTier1 =Double.parseDouble( lObj[7].toString());
					}

					if(lObj[8] != null){
						lInterestTier1 = Double.parseDouble(lObj[8].toString());
					}

				}
				if(lLstDtlsTier2New != null && lLstDtlsTier2New.size() > 0)
				{

					lObj = (Object[]) lLstDtlsTier2New.get(0);
					if(lObj[0] != null){
						lLngOpeningTier2New = Math.round(Double.parseDouble(lObj[0].toString()));
					}
					if(lObj[1] != null){
						//lLngYearlyContriTier2 = Math.round(Double.parseDouble(lObj[1].toString()));
						lLngYearlyContriTier2New = Math.round(getYearlyArrAmountOfEmp(Long.parseLong(lStrDcpsEmpId),lLngYearId));
					}
					if(lObj[2] != null){
						lLngTotalEmpT2New = Math.round(Double.parseDouble(lObj[2].toString()));
					}
					if(lObj[3] != null){
						lLngTotalIntrstT2New = Math.round(Double.parseDouble(lObj[3].toString()));
					}
					gLogger.info("empIntNew is obj4 "+lObj[4].toString());


					if(lObj[6] != null){
						lOpeningEmpTier1New  =Double.parseDouble(lObj[6].toString());
					}
					if(lObj[7] != null){
						lOpeningEmployrTier1New =Double.parseDouble( lObj[7].toString());
					}

					if(lObj[8] != null){
						lInterestTier1New = Double.parseDouble(lObj[8].toString());
					}

				}
				rowList = new ArrayList();
				rowList.add("7");
				rowList.add("Opening Balance (H)");
				rowList.add("");
				rowList.add("<p>OLD:- "+lLngOpeningTier2+"<p>"+"<p> NEW:-  "+lLngOpeningTier2New+"</p>");
				rowList.add("");
				rowList.add("<p>OLD:- "+lLngOpeningTier2+"<p>"+"<p> NEW:-  "+lLngOpeningTier2New+"</p>");
				lLstDataList.add(rowList);

				rowList = new ArrayList();
				rowList.add("8");
				rowList.add("Yearly Contributions (I) ");
				rowList.add("");
				rowList.add("<p>OLD:- "+lLngYearlyContriTier2+"<p>"+"<p> NEW:-  "+lLngYearlyContriTier2New+"</p>");
				rowList.add("");
				rowList.add("<p>OLD:- "+lLngYearlyContriTier2+"<p>"+"<p> NEW:-  "+lLngYearlyContriTier2New+"</p>");
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
				rowList.add(new StyledData("Tier 2 Total For Fin Year "+lStrYear+" \n (J= H+I)" ,boldVO));
				//rowList.add(lLngTotalEmpT2);
				rowList.add("");
//				rowList.add(lLngYearlyContriTier2+lLngOpeningTier2);
				rowList.add("<p>OLD:- "+lLngYearlyContriTier2+lLngOpeningTier2+"<p>"+"<p> NEW:-  "+lLngYearlyContriTier2New+lLngOpeningTier2New+"</p>");
				
				rowList.add("");
//				rowList.add(lLngTotalIntrstT2);
				rowList.add("<p>OLD:- "+lLngYearlyContriTier2+lLngOpeningTier2+"<p>"+"<p> NEW:-  "+lLngYearlyContriTier2New+lLngOpeningTier2New+"</p>");

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

				Double totalContrNew=Double.valueOf(lTotalAmtNew.doubleValue() + lTotalMissingamtNew.doubleValue()+ lLngYearlyContriTier2New.doubleValue());
				Double totalIntNew=Double.valueOf(lLngTotalIntrstT2New.doubleValue() + empIntrstT2New.doubleValue() +  empMissingIntrstT2New.doubleValue());
				Double totalEmplrIntNew=Double.valueOf(emplrIntrstT2New.doubleValue() + emplrMissingIntrstT2New.doubleValue() );
				
				Double finalAmount = totalContr+lTotalAmt+lTotalMissingamt;
				Double finalAmountNew = totalContrNew+lTotalAmtNew+lTotalMissingamtNew;
				
				rowList = new ArrayList();
				rowList.add("");
				rowList.add("Employee and Employer Contribution for Fin Year "+lStrYear+"\n (K= G+J)");
//				rowList.add("Employee and Employer Contribution for Fin Year"+lStrYear+);
				rowList.add("");
				rowList.add("<p>OLD:- "+totalContr+"<p>"+"<p> NEW:-  "+totalContrNew+"</p>");
				rowList.add("<p>OLD:- "+Double.valueOf(lTotalAmt.doubleValue()+lTotalMissingamt.doubleValue())+"<p>"+"<p> NEW:-  "+Double.valueOf(lTotalAmtNew.doubleValue()+lTotalMissingamtNew.doubleValue())+"</p>");
				rowList.add("<p>OLD:- "+finalAmount+"<p>"+"<p> NEW:-  "+finalAmountNew+"</p>");
				lLstDataList.add(rowList);

				rowList = new ArrayList();
				rowList.add("");
				rowList.add("Interest on Employee and Employer Contribution for Fin Year "+lStrYear+" \n (L)");	
				rowList.add("");
				//rowList.add(arr1[0] +" ."+ arr1[1]); empIntrstT2
				rowList.add("<p>OLD:- "+totalInt.doubleValue()+"<p>"+"<p> NEW:-  "+totalIntNew.doubleValue()+"</p>");
				rowList.add("<p>OLD:- "+totalEmplrInt+"<p>"+"<p> NEW:-  "+totalEmplrIntNew.doubleValue()+"</p>");
				rowList.add("<p>OLD:- "+(totalInt+totalEmplrInt)+"<p>"+"<p> NEW:-  "+(totalIntNew+totalEmplrIntNew)+"</p>");
				lLstDataList.add(rowList);

				Double totalEmpAmt=Double.valueOf( lOpeningEmpTier1.doubleValue() + totalInt.doubleValue() + totalContr.doubleValue() );
				Double totalEmplyrAmt=Double.valueOf(lTotalAmt.doubleValue() +  lTotalMissingamt.doubleValue() + lOpeningEmployrTier1.doubleValue() + totalEmplrInt.doubleValue() ); 

				for(Integer lInt=0;lInt<interestRates.size();lInt++)
				{
					
					lObjIntDtls = (Object[]) interestRates.get(lInt);
					lDoubleIntRate = Double.valueOf(lObjIntDtls[0].toString().trim());
					lDoubleIntRate = Round(lDoubleIntRate,2);
			
					openEmployeeInt=openEmployeeInt+lDoubleIntRate * lOpeningEmpTier1  / 100;
					openEmployerInt=openEmployerInt+lDoubleIntRate * lOpeningEmployrTier1  / 100;
					
					openEmployeeIntNew=openEmployeeIntNew+lDoubleIntRate * lOpeningEmpTier1New / 100;
					openEmployerIntNew=openEmployerIntNew+lDoubleIntRate * lOpeningEmployrTier1New  / 100;
					
				}
				
				gLogger.info("Closing balance is "+totalEmpAmt+"********"+openEmployeeInt);
				
//				rowList = new ArrayList();
//				rowList.add("");
//				rowList.add("Total Amount Standing to the Credit of the Employee");
//				rowList.add("");
//				rowList.add(totalEmpAmt);
//				rowList.add(totalEmplyrAmt);
//				rowList.add("");
//				lLstDataList.add(rowList);	
//				
						
				rowList = new ArrayList();
				rowList.add("");
				rowList.add("Interest on openeing balance for fin year"+lStrYear+" \n (M)");
				rowList.add("");
				rowList.add("<p>OLD:- "+Round((openEmployeeInt),2)+"<p>"+"<p> NEW:-  "+Round((openEmployeeIntNew),2)+"</p>");
				rowList.add("<p>OLD:- "+Round(openEmployerInt,2)+"<p>"+"<p> NEW:-  "+Round(openEmployerIntNew,2)+"</p>");
				rowList.add("<p>OLD:- "+(Round((openEmployeeInt),2)+Round(openEmployerInt,2))+"<p>"+"<p> NEW:-  "+(Round((openEmployeeIntNew),2)+Round(openEmployerIntNew,2))+"</p>");
				lLstDataList.add(rowList);
				
				Double totalAmountStanding = totalContr.doubleValue()+totalInt.doubleValue()+openEmployeeInt.doubleValue();
				Double totalAmountStandingNew = totalContrNew.doubleValue()+totalIntNew.doubleValue()+openEmployeeIntNew.doubleValue();
				
				Double totalAmountStandingEmplyr = Double.valueOf(lTotalAmt.doubleValue()+lTotalMissingamt.doubleValue())+totalEmplrInt+openEmployerInt;
				Double totalAmountStandingEmplyrNew = Double.valueOf(lTotalAmtNew.doubleValue()+lTotalMissingamtNew.doubleValue())+totalEmplrIntNew+openEmployerIntNew;
				
				Double finalTotalStandingAmount = totalAmountStanding.doubleValue()+totalAmountStandingEmplyr.doubleValue();
				Double finalTotalStandingAmountNew = totalAmountStandingNew.doubleValue()+totalAmountStandingEmplyrNew.doubleValue();

				
				rowList = new ArrayList();
				rowList.add("");
				rowList.add(new StyledData("Total Amount Standing to the Credit of the Employee for fin year"+lStrYear+" \n (N=K+L+M)" ,boldVO));
				rowList.add("");
				rowList.add("<p>OLD:- "+totalAmountStanding+"<p>"+"<p> NEW:-  "+totalAmountStandingNew+"</p>");
				rowList.add("<p>OLD:- "+totalAmountStandingEmplyr+"<p>"+"<p> NEW:-  "+totalAmountStandingEmplyrNew+"</p>");
//				rowList.add(Round((totalEmpAmt),2)+Round((openEmployeeInt),2));
//				rowList.add(Round((totalEmplyrAmt),2)+Round(openEmployerInt,2));
				rowList.add("<p>OLD:- "+finalTotalStandingAmount+"<p>"+"<p> NEW:-  "+finalTotalStandingAmountNew+"</p>");
				lLstDataList.add(rowList);	
				
				
	
				
//				rowList = new ArrayList();
//				rowList.add("");
//				rowList.add("Closing Balance with open Int");
//				rowList.add("");
//				rowList.add(Round((totalEmpAmt),2)+Round((openEmployeeInt),2));
//				rowList.add(Round((totalEmplyrAmt),2)+Round(openEmployerInt,2));
//				rowList.add("");
//				lLstDataList.add(rowList);	



				rptTd = new TabularData(lLstDataList);
				RptVo = reportsDao.getReport("80000820", lObjReport.getLangId(), 
						lObjReport.getLocId());
				rptTd.setRelatedReport(RptVo);

				rptList1 = new ArrayList();
				rptList1.add(rptTd);
				dataList.add(rptList1);

	//			Long totalAmtCredit=Math.round(Double.valueOf(totalEmpAmt.doubleValue() + totalEmplyrAmt.doubleValue() + lInterestTier1.doubleValue() )) ;
				Long totalAmtCredit=Math.round(Double.valueOf(finalTotalStandingAmountNew)) ;

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


				rowList1 = new ArrayList();
				rowList1.add("Certified that the details shown above are correct as per the information received in this office. However, the closing balance shown above will be subject to final adjustments on account of excess credit or excess interest, if any, which may come to the notice of this Office at a later date."+ newline+ newline);
				dataList.add(rowList1);

				rowList1 = new ArrayList();	
//				rowList1.add(new StyledData("Place: Mumbai"+ newline,boldVO));
				rowList1.add(new StyledData("Place: Mumbai"+ space(70)+ "Date:   "+ dateFormat4.format(date) + space(70) +   "**Shows Missing Credits",boldVO));
				dataList.add(rowList1);

//				rowList1 = new ArrayList();
//				rowList1.add(new StyledData("Date:   "+ dateFormat4.format(date) + space(50) +   "**Shows Missing Credits"
//						,boldVO));
//				dataList.add(rowList1);

				rowList1 = new ArrayList();
				rowList1.add("Office-in-Charge - Treasury Officer" + newline+ "(" + lLngTreasuryId + ") " + lStrTreasuryName);
				dataList.add(rowList1);

				rowList1 = new ArrayList();
				rowList1.add("");
				dataList.add(rowList1);

				rowList1 = new ArrayList();
				rowList1
				.add("Please verify the details of Shri/Smt/Kum "
						+ empName
						+ ". If there is any discrepancy; please inform the Treasury Officer concerned with Vr.No.,Date, Amount of the bill alongwith a copy of the schedule.");
				dataList.add(rowList1);

				rowList1 = new ArrayList();
			//	rowList1.add("This is a Computer Generated Statement, Needs No Signature"+newline+newline);
				rowList1.add(new StyledData("This is a Computer Generated Statement, Needs No Signature"+newline+newline,CenterAlignVO));
				dataList.add(rowList1);

				rowList1 = new ArrayList();
				rowList1.add(new StyledData("* Tier - 2 Opening Balance has been added to the Employees contribution Opening Balance in Sr. No. 1",boldVO));
				dataList.add(rowList1);


				rowList1 = new ArrayList();
				rowList1.add("Date and Time of generation :          "+dateFormat5.format(date));
				dataList.add(rowList1);


			}
		}

		catch (Exception e) {
			e.printStackTrace();
			gLogger.info("findReportData(): Exception is" + e, e);
		} 
		return dataList;
	}

	public List getAllTreasuries( String lStrLangId, String lStrLocId) {

		ArrayList<ComboValuesVO> arrTreasury = new ArrayList<ComboValuesVO>();
		Connection lCon = null;
		PreparedStatement lStmt = null;
		ResultSet lRs = null;
		String treasury_id = null;
		String treasury_name = null;

		try {			
			StringBuffer lsb = new StringBuffer();
			lsb = new StringBuffer(
			"select CM.loc_Id , CM.loc_Name from Cmn_Location_Mst CM where department_Id in (100006,100003)  and CM.LANG_ID = 1 and CM.LOC_ID not in(9991,1111)  order by CM.loc_id  ");

			lCon = DBConnection.getConnection();
			lStmt = lCon.prepareStatement(lsb.toString());
			lRs = lStmt.executeQuery();
			while (lRs.next()) {
				ComboValuesVO vo = new ComboValuesVO();
				treasury_id = lRs.getString("loc_Id");
				treasury_name = lRs.getString("loc_Name");
				vo.setId(treasury_id);
				vo.setDesc(treasury_id+"-"+treasury_name);
				arrTreasury.add(vo);
			}

		} catch (Exception e) {
			gLogger.info("Sql Exception:" + e, e);
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
		return arrTreasury;

	}

	public List getAllDDO(String treasuryId, String lStrLangId, String lStrLocId) {

		ArrayList<ComboValuesVO> arrDDO = new ArrayList<ComboValuesVO>();
		Connection lCon = null;
		PreparedStatement lStmt = null;
		ResultSet lRs = null;
		String ddo_code = null;
		String ddo_name = null;
		try {
			lCon = DBConnection.getConnection();
			StringBuffer lsb = new StringBuffer();
			lsb = new StringBuffer(
					"SELECT DM.ddo_code as ddo_code, DM.ddo_name as ddo_name FROM Rlt_Ddo_Org RO, Org_Ddo_Mst DM,Cmn_Location_Mst LM "
					+ "WHERE RO.location_Code = '"
					+ treasuryId
					+ "' AND RO.ddo_Code = DM.ddo_Code AND LM.location_Code = RO.location_Code AND LM.LANG_ID = 1 order by DM.ddo_code");

			lStmt = lCon.prepareStatement(lsb.toString());
			lRs = lStmt.executeQuery();
			if(lRs!=null)
			{	
				while (lRs.next()) {
					ComboValuesVO vo = new ComboValuesVO();
					ddo_code = lRs.getString("ddo_code");
					ddo_name = lRs.getString("ddo_name");
					vo.setId(ddo_code);
					//vo.setDesc(ddo_name);
					vo.setDesc("<![CDATA["+"("+ddo_code+") "+ddo_name+"]]>");
					arrDDO.add(vo);
				}
			}
			else
			{
				ComboValuesVO vo = new ComboValuesVO();
				vo.setId("-1");
				vo.setDesc("--Select--");
				arrDDO.add(vo);
			}
		} catch (Exception e) {			
			gLogger.info("Sql Exception:" + e, e);

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
		return arrDDO;
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

	public List getAllEmpsUnderDDO(String lStrddoCode, String lStrLangId,
			String lStrLocId) {

		List<Object> lLstReturnList = null;
		try {
			if (!lStrddoCode.equals("-1")) {
				StringBuilder sb = new StringBuilder();
				Session lObjSession = ServiceLocator.getServiceLocator()
				.getSessionFactory().getCurrentSession();
				sb.append(" SELECT EM.dcpsEmpId,EM.name FROM MstEmp EM ");
				sb
				.append(" WHERE EM.ddoCode = :ddoCode and EM.dcpsOrGpf = 'Y' and regStatus = 1  order by EM.name");
				Query selectQuery = lObjSession.createQuery(sb.toString());
				selectQuery.setParameter("ddoCode", lStrddoCode);
				List lLstResult = selectQuery.list();
				ComboValuesVO lObjComboValuesVO = null;
				if (lLstResult != null && lLstResult.size() != 0) {
					lLstReturnList = new ArrayList<Object>();
					Object obj[];
					for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
						obj = (Object[]) lLstResult.get(liCtr);
						lObjComboValuesVO = new ComboValuesVO();
						lObjComboValuesVO.setId(obj[0].toString());
						lObjComboValuesVO.setDesc(obj[1].toString());
						lLstReturnList.add(lObjComboValuesVO);
					}
				}
				else {
					lLstReturnList = new ArrayList<Object>();
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId("-1");
					lObjComboValuesVO.setDesc("-- Select --");
					lLstReturnList.add(lObjComboValuesVO);
				}
			} else {
				lLstReturnList = new ArrayList<Object>();
				ComboValuesVO lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("-- Select --");
				lLstReturnList.add(lObjComboValuesVO);
			}
		} catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}
		return lLstReturnList; 
	}

	public List getOpeningBalanceTier1(Long lLngDcpsEmpId, Long lLngYearId)
	{
		List lLstData = null;
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT OPEN_EMPLOYEE,OPEN_EMPLOYER,(OPEN_EMPLOYEE+OPEN_EMPLOYER) as total ");
			lSBQuery.append("FROM MST_DCPS_CONTRIBUTION_YEARLY ");
			lSBQuery.append("WHERE dcps_id = (select dcps_id from mst_dcps_emp where dcps_Emp_id = :dcpsEmpId ) ");
			lSBQuery.append("AND YEAR_ID = :year ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("dcpsEmpId", lLngDcpsEmpId);
			lQuery.setLong("year", lLngYearId);

			lLstData = lQuery.list();
		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}

		return lLstData;
	}



	public List getEmpAllDtlsTire1(Long lLngDcpsEmpId, Long lLngYearId, String lStrType,String fromDate,String toDate)
	{
		List lLstData = null;
		List lDblRecovery = new ArrayList();


		try{
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT substr(upper(mst.MONTH_NAME),1,3)||'-'||case when mst.month_id >3 then cast(fin.FIN_YEAR_CODE as bigint) else cast((fin.FIN_YEAR_CODE)+1 as bigint) end as year,fin.FIN_YEAR_DESC,sum(trn.contribution),mst.month_id ,sum(trn.contribution+trn.contribution)as total  from TRN_DCPS_CONTRIBUTION trn ");  
			if (lStrType.equals("700047"))
			{
				lSBQuery.append(" inner join SGVC_FIN_YEAR_MST fin on  trn.STARTDATE>=fin.from_date and fin.to_date>=trn.STARTDATE "); 
				lSBQuery.append(" inner join SGVA_MONTH_MST mst on mst.MONTH_ID = month(trn.STARTDATE)  ");
				lSBQuery.append(" WHERE   trn.fin_year_id="+lLngYearId+"  and  trn.TYPE_OF_PAYMENT="+lStrType+" and trn.DCPS_EMP_ID ="+lLngDcpsEmpId+"  ");
			}
			else
			{
				lSBQuery.append(" inner join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID = trn.FIN_YEAR_ID ");
				lSBQuery.append(" inner join SGVA_MONTH_MST mst on mst.MONTH_ID =trn.MONTH_ID ");
				lSBQuery.append(" WHERE  ((trn.MONTH_ID <> 3 and trn.fin_year_id="+lLngYearId+")  or (trn.MONTH_ID = 3 and trn.fin_year_id=:lLngYearId )) and trn.DCPS_EMP_ID ="+lLngDcpsEmpId+"  and  trn.TYPE_OF_PAYMENT="+lStrType+"  ");
			}

			lSBQuery.append("  and trn.voucher_date between '"+fromDate+"' and '"+toDate+"' AND trn.REG_STATUS = 1  and  trn.EMPLOYER_CONTRI_FLAG = 'Y'  and (trn.is_missing_credit is null and trn.IS_CHALLAN is null)  ");
			lSBQuery.append(" group by mst.MONTH_NAME,fin.FIN_YEAR_DESC,mst.month_id,fin.FIN_YEAR_CODE  ");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			if (!lStrType.equals("700047")){
				lQuery.setParameter("lLngYearId", lLngYearId-1);
			}

			lLstData = lQuery.list();
			gLogger.info("Query isd **************"+lQuery.toString());
			gLogger.info("List of data 000000000000000000"+lLstData.toString());

		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}

		return lLstData;
	}


	public List getEmpAllMissingCreditTire1(Long lLngDcpsEmpId,String fromDate,String toDate,Long lLngYearId)
	{
		List lLstData = null;
		List lDblRecovery = new ArrayList();


		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT substr(upper(mst.MONTH_NAME),1,3)||'-'||case when mst.month_id >3 then cast(fin.FIN_YEAR_CODE as bigint) else cast((fin.FIN_YEAR_CODE)+1 as bigint) end as year,fin.FIN_YEAR_DESC,sum(trn.contribution) ,mst.month_id,look.LOOKUP_NAME   ,sum(trn.contribution+trn.contribution)as total  from TRN_DCPS_CONTRIBUTION trn ");  
			lSBQuery.append(" inner join SGVC_FIN_YEAR_MST fin on  trn.STARTDATE>=fin.from_date and fin.to_date>=trn.STARTDATE "); 
			lSBQuery.append(" inner join SGVA_MONTH_MST mst on mst.MONTH_ID = month(trn.STARTDATE)  ");
			lSBQuery.append(" inner join CMN_LOOKUP_MST look on look.LOOKUP_ID=trn.TYPE_OF_PAYMENT ");
			lSBQuery.append(" WHERE  trn.MISSING_CREDIT_APPROVAL_DATE between '"+fromDate+"' and '"+toDate+"' and trn.DCPS_EMP_ID ="+lLngDcpsEmpId+"  ");  
			lSBQuery.append(" AND trn.REG_STATUS = 1  and  trn.EMPLOYER_CONTRI_FLAG = 'Y'  and (trn.is_missing_credit ='Y' or trn.IS_CHALLAN ='Y')  "); 
			lSBQuery.append(" group by mst.MONTH_NAME,fin.FIN_YEAR_DESC,mst.MONTH_NAME,look.LOOKUP_NAME,mst.MONTH_ID,fin.FIN_YEAR_CODE ");

			lSBQuery.append(" union all ");

			lSBQuery.append(" SELECT substr(upper(mst.MONTH_NAME),1,3)||'-'||case when mst.month_id >3 then cast(fin.FIN_YEAR_CODE as bigint) else cast((fin.FIN_YEAR_CODE)+1 as bigint) end as year,fin.FIN_YEAR_DESC,sum(trn.contribution) ,mst.month_id,look.LOOKUP_NAME  ,sum(trn.contribution+trn.contribution)as total   from TRN_DCPS_CONTRIBUTION trn ");   
			lSBQuery.append(" inner join SGVC_FIN_YEAR_MST fin on  trn.STARTDATE>=fin.from_date and fin.to_date>=trn.STARTDATE ");  
			lSBQuery.append(" inner join SGVA_MONTH_MST mst on mst.MONTH_ID = month(trn.STARTDATE)  ");
			lSBQuery.append(" inner join CMN_LOOKUP_MST look on look.LOOKUP_ID=trn.TYPE_OF_PAYMENT ");
			lSBQuery.append(" WHERE  trn.voucher_date between '"+fromDate+"' and '"+toDate+"' and trn.DCPS_EMP_ID ="+lLngDcpsEmpId+" and ((trn.MONTH_ID <> 3 and trn.FIN_YEAR_ID<>"+lLngYearId+") or (trn.MONTH_ID=3 and trn.FIN_YEAR_ID<>"+lLngYearId+"-1)) ");
			lSBQuery.append(" AND trn.REG_STATUS = 1  and  trn.EMPLOYER_CONTRI_FLAG = 'Y'  and (trn.is_missing_credit is null and trn.IS_CHALLAN is null) ");   
			lSBQuery.append(" group by mst.MONTH_NAME,fin.FIN_YEAR_DESC,mst.MONTH_NAME,look.LOOKUP_NAME,mst.MONTH_ID,fin.FIN_YEAR_CODE ");


			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lLstData = lQuery.list();
			gLogger.info("Query isd **************"+lQuery.toString());
			gLogger.info("List of data 000000000000000000"+lLstData.toString());

		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}

		return lLstData;
	}


	public Double getTier1TotalDetails(Long lLngDcpsEmpId, Long lLngYearId)
	{
		List lLstData = null;
		Double totalInt=null;
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT NVL(sum(totalInt),0) FROM ");
			lSBQuery.append(" ((SELECT NVL((INT_CONTRB_EMPLOYEE + INT_CONTRB_EMPLOYER),0)  as totalInt ");
			lSBQuery.append(" FROM MST_DCPS_CONTRIBUTION_MONTHLY where dcps_id = (select dcps_id from mst_dcps_emp where dcps_Emp_id = :dcpsEmpId ) ");			
			//lSBQuery.append(" AND YEAR_ID = "+lLngYearId+" and PAY_MONTH <> 3) ");
			lSBQuery.append(" AND YEAR_ID = "+lLngYearId+" And (IS_MISSING_CREDIT is null and IS_CHALLAN is null) )) ");
			/*	lSBQuery.append(" union ");

			lSBQuery.append(" (SELECT NVL((INT_CONTRB_EMPLOYEE + INT_CONTRB_EMPLOYER),0)  as totalInt ");
			lSBQuery.append(" FROM MST_DCPS_CONTRIBUTION_MONTHLY where dcps_id = (select dcps_id from mst_dcps_emp where dcps_Emp_id = :dcpsEmpId ) ");			
			lSBQuery.append(" AND YEAR_ID = :year and PAY_MONTH = 3)) ");*/

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("dcpsEmpId", lLngDcpsEmpId);
			//	lQuery.setLong("year", lLngYearId-1);


			lLstData = lQuery.list();
			if(lLstData.size()>0 && lLstData!=null)
				totalInt=Double.parseDouble(lLstData.get(0).toString());
		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}

		return totalInt;
	}


	public Double getTier1IntMissingDetails(Long lLngDcpsEmpId, Long lLngYearId)
	{
		List lLstData = null;
		Double totalInt=null;
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT NVL(sum(totalInt),0) FROM ");
			lSBQuery.append(" ((SELECT NVL((INT_CONTRB_EMPLOYEE + INT_CONTRB_EMPLOYER),0)  as totalInt ");
			lSBQuery.append(" FROM MST_DCPS_CONTRIBUTION_MONTHLY where dcps_id = (select dcps_id from mst_dcps_emp where dcps_Emp_id = :dcpsEmpId ) ");			
			//lSBQuery.append(" AND YEAR_ID = "+lLngYearId+" AND IS_MISSING_CREDIT='Y' and PAY_MONTH <> 3 ) ");
			lSBQuery.append(" AND YEAR_ID = "+lLngYearId+" AND (IS_MISSING_CREDIT='Y' or IS_CHALLAN='Y'))) ");
			/*	lSBQuery.append(" union ");


			lSBQuery.append(" (SELECT NVL((INT_CONTRB_EMPLOYEE + INT_CONTRB_EMPLOYER),0)  as totalInt ");
			lSBQuery.append(" FROM MST_DCPS_CONTRIBUTION_MONTHLY where dcps_id = (select dcps_id from mst_dcps_emp where dcps_Emp_id = :dcpsEmpId ) ");			
			lSBQuery.append(" AND YEAR_ID = :year AND IS_MISSING_CREDIT='Y' and PAY_MONTH = 3 )) ");*/

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("dcpsEmpId", lLngDcpsEmpId);
			//	lQuery.setLong("year", lLngYearId-1);


			lLstData = lQuery.list();
			if(lLstData.size()>0 && lLstData!=null)
				gLogger.info("Tier 1 Missing credit:"+lLstData.get(0).toString());
			totalInt=Double.parseDouble(lLstData.get(0).toString());
		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}

		return totalInt;
	}

	public Double getTotalAmountDetails(Long lLngDcpsEmpId, Long lLngYearId,String fromDate,String toDate)
	{
		List lLstData = null;
		Double amount=0d;
		try{
			StringBuilder lSBQuery = new StringBuilder();

			/*	lSBQuery.append(" select NVL(sum(totalAmt),0) from ");
			lSBQuery.append(" (SELECT nvl(sum(CONTRIBUTION),0) as totalAmt FROM  TRN_DCPS_CONTRIBUTION where REG_STATUS=1 and fin_year_id="+lLngYearId+" and EMPLOYER_CONTRI_FLAG='Y'  and DCPS_EMP_ID=:dcpsEmpId and is_missing_credit is null and month(STARTDATE) <> 3");

			lSBQuery.append(" union "); 

			lSBQuery.append(" SELECT nvl(sum(CONTRIBUTION),0) as totalAmt FROM  TRN_DCPS_CONTRIBUTION where REG_STATUS=1 and fin_year_id=:lLngYearId and EMPLOYER_CONTRI_FLAG='Y'  and DCPS_EMP_ID=:dcpsEmpId and is_missing_credit is null and month(STARTDATE)= 3) ");
			 */
			lSBQuery.append(" SELECT nvl(SUM(CONTRIBUTION),0) FROM TRN_DCPS_CONTRIBUTION WHERE DCPS_EMP_ID=:dcpsEmpId  AND (is_missing_credit is null and IS_CHALLAN is null) AND REG_STATUS=1 AND  EMPLOYER_CONTRI_FLAG='Y' and    voucher_date between '"+fromDate+"' and '"+toDate+"' and ((MONTH_ID = 3 and FIN_YEAR_ID="+lLngYearId+"-1) or (MONTH_ID<>3 and FIN_YEAR_ID="+lLngYearId+")) ");


			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("dcpsEmpId", lLngDcpsEmpId);

			//lQuery.setParameter("lLngYearId", lLngYearId-1);


			lLstData = lQuery.list();
			if(lLstData!=null)
			{
				amount=Double.parseDouble(lLstData.get(0).toString());
			}
		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}


		return amount;
	}

	public Double getTotalMissingAmountDetails(Long lLngDcpsEmpId, Long lLngYearId,String fromDate,String toDate)
	{
		List lLstData = null;
		Double amount=0d;
		try{
			StringBuilder lSBQuery = new StringBuilder();
		//	lSBQuery.append(" SELECT nvl(sum(CONTRIBUTION),0) FROM  TRN_DCPS_CONTRIBUTION where REG_STATUS=1 and MISSING_CREDIT_APPROVAL_DATE between '"+fromDate+"' and '"+toDate+"'  and EMPLOYER_CONTRI_FLAG='Y'  and DCPS_EMP_ID=:dcpsEmpId  and (is_missing_credit='Y' or IS_CHALLAN='Y')  ");

			lSBQuery.append(" SELECT sum(contri) FROM "); 
			lSBQuery.append(" (  ");
			lSBQuery.append(" SELECT nvl(sum(CONTRIBUTION),0) as contri FROM  TRN_DCPS_CONTRIBUTION where REG_STATUS=1 ");
			lSBQuery.append(" and MISSING_CREDIT_APPROVAL_DATE between '"+fromDate+"' and '"+toDate+"'  and EMPLOYER_CONTRI_FLAG='Y'  and DCPS_EMP_ID=:dcpsEmpId  and (is_missing_credit='Y' or IS_CHALLAN='Y') ");

			lSBQuery.append(" UNION ");

			lSBQuery.append(" SELECT nvl(sum(CONTRIBUTION),0)  as contri FROM TRN_DCPS_CONTRIBUTION WHERE REG_STATUS=1 AND VOUCHER_DATE BETWEEN '"+fromDate+"' and '"+toDate+"'   and EMPLOYER_CONTRI_FLAG='Y'  and DCPS_EMP_ID=:dcpsEmpId  and ");
			lSBQuery.append(" (is_missing_credit IS NULL and  IS_CHALLAN IS NULL) and  ((MONTH_ID <> 3 and FIN_YEAR_ID<>"+lLngYearId+") or (MONTH_ID=3 and FIN_YEAR_ID<>"+lLngYearId+"-1)) ");
			lSBQuery.append(" ) ");
			
			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("dcpsEmpId", lLngDcpsEmpId);
			//lQuery.setParameter("lLngYearId", lLngYearId);


			lLstData = lQuery.list();
			if(lLstData!=null)
			{
				amount=Double.parseDouble(lLstData.get(0).toString());
			}
		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}


		return amount;
	}

	public List getTier2Details(Long lLngDcpsEmpId, Long lLngYearId)
	{
		List lLstData = null;
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" select OPEN_TIER2,CONTRIB_TIER2,OPEN_TIER2+CONTRIB_TIER2,INT_CONTRB_TIER2 ,cast(INT_CONTRB_EMPLOYEE as decimal(16,2)) as INT_CONTRB_EMP,cast(INT_CONTRB_EMPLOYER as decimal(16,2)) as INT_CONTRB_EMPlr,OPEN_EMPLOYEE,OPEN_EMPLOYER,OPEN_INT FROM MST_DCPS_CONTRIBUTION_YEARLY ");
			lSBQuery.append(" WHERE dcps_id = (select dcps_id from mst_dcps_emp where dcps_Emp_id = :dcpsEmpId ) ");			
			lSBQuery.append(" AND YEAR_ID = :year ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("dcpsEmpId", lLngDcpsEmpId);
			lQuery.setLong("year", lLngYearId);

			lLstData = lQuery.list();		}catch (Exception e) {			
				gLogger.error("Error is : " + e, e);
			}

			return lLstData;
	}

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

	public String getEmpName(Long lLngDcpsEmpId)
	{
		List lLstData = null;
		String lStrEmpName = "";

		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT EMP_NAME FROM MST_DCPS_EMP WHERE DCPS_EMP_ID = :dcpsEmpId");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("dcpsEmpId", lLngDcpsEmpId);
			lLstData = lQuery.list();

			if(lLstData.size() > 0){
				lStrEmpName = lLstData.get(0).toString();
			}
		}catch(Exception e){
			gLogger.error("Exception in getDdoName:" + e, e);
		}
		return lStrEmpName;
	}	

	public Double getYearlyArrAmountOfEmp(Long lLngDcpsEmpId, Long lLngYearId)
	{
		List lLstData = null;
		Double lDblArrAmount = 0d;
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT YEARLY_AMOUNT FROM rlt_dcps_sixpc_yearly ");			
			lSBQuery.append(" where DCPS_EMP_ID = :dcpsEmpId and FIN_YEAR_ID = :year and STATUS_FLAG = 'A' ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("dcpsEmpId", lLngDcpsEmpId);
			lQuery.setLong("year", lLngYearId);

			lLstData = lQuery.list();

			if(lLstData != null)
			{
				if( lLstData.size() > 0 && lLstData.get(0) != null ){
					lDblArrAmount = Double.parseDouble(lLstData.get(0).toString());
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is : " + e, e);
		}

		return lDblArrAmount;
	}
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


	public List getTier1FinalDetails(Long lLngDcpsEmpId, Long lLngYearId)
	{
		List lLstTier1FinalData = null;

		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT NVL(sum(totalEmpInt),0),NVL(sum(totalEmplrInt),0) FROM ");
			lSBQuery.append(" 	((SELECT NVL(INT_CONTRB_EMPLOYEE,0) as totalEmpInt,nvl(INT_CONTRB_EMPLOYER,0)  as totalEmplrInt  ");
			lSBQuery.append(" FROM MST_DCPS_CONTRIBUTION_MONTHLY where dcps_id = (select dcps_id from mst_dcps_emp where dcps_Emp_id = :dcpsEmpId ) ");			
			//	lSBQuery.append(" AND YEAR_ID = "+lLngYearId+" and PAY_MONTH <> 3) ");
			lSBQuery.append(" AND YEAR_ID = "+lLngYearId+" and (is_missing_credit is null and IS_CHALLAN is null) )) ");
			/*	lSBQuery.append(" union ");

			lSBQuery.append(" (SELECT NVL(INT_CONTRB_EMPLOYEE,0) as totalEmpInt,nvl(INT_CONTRB_EMPLOYER,0)  as totalEmplrInt ");
			lSBQuery.append(" FROM MST_DCPS_CONTRIBUTION_MONTHLY where dcps_id = (select dcps_id from mst_dcps_emp where dcps_Emp_id = :dcpsEmpId ) ");			
			lSBQuery.append(" AND YEAR_ID = :year and PAY_MONTH = 3)) ");*/

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("dcpsEmpId", lLngDcpsEmpId);
			//lQuery.setLong("year", lLngYearId-1);


			lLstTier1FinalData = lQuery.list();

		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}

		return lLstTier1FinalData;
	}

	public List getTier1FinalIntMissingDetails(Long lLngDcpsEmpId, Long lLngYearId)
	{
		List lLstTier1MissingData = null;

		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT NVL(sum(totalEmpInt),0),NVL(sum(totalEmplrInt),0) FROM ");
			lSBQuery.append(" ((SELECT NVL(INT_CONTRB_EMPLOYEE,0) as totalEmpInt,nvl(INT_CONTRB_EMPLOYER,0)  as totalEmplrInt  ");
			lSBQuery.append(" FROM MST_DCPS_CONTRIBUTION_MONTHLY where dcps_id = (select dcps_id from mst_dcps_emp where dcps_Emp_id = :dcpsEmpId ) ");			
			//lSBQuery.append(" AND YEAR_ID = "+lLngYearId+" AND IS_MISSING_CREDIT='Y' and PAY_MONTH <> 3 ) ");
			lSBQuery.append(" AND YEAR_ID = "+lLngYearId+" AND (IS_MISSING_CREDIT='Y' or IS_CHALLAN='Y')  )) ");
			/*	lSBQuery.append(" union ");


			lSBQuery.append(" (SELECT NVL(INT_CONTRB_EMPLOYEE,0) as totalEmpInt,nvl(INT_CONTRB_EMPLOYER,0)  as totalEmplrInt ");
			lSBQuery.append(" FROM MST_DCPS_CONTRIBUTION_MONTHLY where dcps_id = (select dcps_id from mst_dcps_emp where dcps_Emp_id = :dcpsEmpId ) ");			
			lSBQuery.append(" AND YEAR_ID = :year AND IS_MISSING_CREDIT='Y' and PAY_MONTH = 3 )) ");*/

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("dcpsEmpId", lLngDcpsEmpId);
			//lQuery.setLong("year", lLngYearId-1);


			lLstTier1MissingData = lQuery.list();

		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}

		return lLstTier1MissingData;
	}


	
	
	

	public List getOpeningBalanceTier1New(Long lLngDcpsEmpId, Long lLngYearId) 
	{
		List lLstData = null;
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT OPEN_EMPLOYEE,OPEN_EMPLOYER,(OPEN_EMPLOYEE+OPEN_EMPLOYER) as total ");
			lSBQuery.append("FROM MST_DCPS_CONTRIB_YEARLY_NEW ");
			lSBQuery.append("WHERE dcps_id = (select dcps_id from mst_dcps_emp where dcps_Emp_id = :dcpsEmpId ) ");
			lSBQuery.append("AND YEAR_ID = :year ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("dcpsEmpId", lLngDcpsEmpId);
			lQuery.setLong("year", lLngYearId);

			lLstData = lQuery.list();
		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}

		return lLstData;
	}

	public List getEmpAllDtlsTire1New(Long lLngDcpsEmpId, Long lLngYearId, String lStrType,String fromDate,String toDate)
	{
		List lLstData = null;
		List lDblRecovery = new ArrayList();


		try{
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT substr(upper(mst.MONTH_NAME),1,3)||'-'||case when mst.month_id >3 then cast(fin.FIN_YEAR_CODE as bigint) else cast((fin.FIN_YEAR_CODE)+1 as bigint) end as year,fin.FIN_YEAR_DESC,sum(trn.contribution),mst.month_id  ,sum(trn.contribution+trn.contribution)as total  from TRN_DCPS_CONTRIB_FOR_INT_CALC trn ");  
			if (lStrType.equals("700047"))
			{
				lSBQuery.append(" inner join SGVC_FIN_YEAR_MST fin on  trn.STARTDATE>=fin.from_date and fin.to_date>=trn.STARTDATE "); 
				lSBQuery.append(" inner join SGVA_MONTH_MST mst on mst.MONTH_ID = month(trn.STARTDATE)  ");
				lSBQuery.append(" WHERE   trn.fin_year_id="+lLngYearId+"  and  trn.TYPE_OF_PAYMENT="+lStrType+" and trn.DCPS_EMP_ID ="+lLngDcpsEmpId+"  ");
			}
			else
			{
				lSBQuery.append(" inner join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID = trn.FIN_YEAR_ID ");
				lSBQuery.append(" inner join SGVA_MONTH_MST mst on mst.MONTH_ID =trn.MONTH_ID ");
				lSBQuery.append(" WHERE  ((trn.MONTH_ID <> 3 and trn.fin_year_id="+lLngYearId+")  or (trn.MONTH_ID = 3 and trn.fin_year_id=:lLngYearId )) and trn.DCPS_EMP_ID ="+lLngDcpsEmpId+"  and  trn.TYPE_OF_PAYMENT="+lStrType+"  ");
			}

			lSBQuery.append("  and trn.voucher_date between '"+fromDate+"' and '"+toDate+"' AND trn.REG_STATUS = 1  and  trn.EMPLOYER_CONTRI_FLAG = 'Y'  and (trn.is_missing_credit is null and trn.IS_CHALLAN is null)  ");
			lSBQuery.append(" group by mst.MONTH_NAME,fin.FIN_YEAR_DESC,mst.month_id,fin.FIN_YEAR_CODE  ");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			if (!lStrType.equals("700047")){
				lQuery.setParameter("lLngYearId", lLngYearId-1);
			}

			lLstData = lQuery.list();
			gLogger.info("Query isd **************"+lQuery.toString());
			gLogger.info("List of data 000000000000000000"+lLstData.toString());

		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}

		return lLstData;
	}


	public List getEmpAllMissingCreditTire1New(Long lLngDcpsEmpId,String fromDate,String toDate,Long lLngYearId)
	{
		List lLstData = null;
		List lDblRecovery = new ArrayList();


		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT substr(upper(mst.MONTH_NAME),1,3)||'-'||case when mst.month_id >3 then cast(fin.FIN_YEAR_CODE as bigint) else cast((fin.FIN_YEAR_CODE)+1 as bigint) end as year,fin.FIN_YEAR_DESC,sum(trn.contribution) ,mst.month_id,look.LOOKUP_NAME  ,sum(trn.contribution+trn.contribution)as total   from TRN_DCPS_CONTRIB_FOR_INT_CALC trn ");  
			lSBQuery.append(" inner join SGVC_FIN_YEAR_MST fin on  trn.STARTDATE>=fin.from_date and fin.to_date>=trn.STARTDATE "); 
			lSBQuery.append(" inner join SGVA_MONTH_MST mst on mst.MONTH_ID = month(trn.STARTDATE)  ");
			lSBQuery.append(" inner join CMN_LOOKUP_MST look on look.LOOKUP_ID=trn.TYPE_OF_PAYMENT ");
			lSBQuery.append(" WHERE  trn.MISSING_CREDIT_APPROVAL_DATE between '"+fromDate+"' and '"+toDate+"' and trn.DCPS_EMP_ID ="+lLngDcpsEmpId+"  ");  
			lSBQuery.append(" AND trn.REG_STATUS = 1  and  trn.EMPLOYER_CONTRI_FLAG = 'Y'  and (trn.is_missing_credit ='Y' or trn.IS_CHALLAN ='Y')  "); 
			lSBQuery.append(" group by mst.MONTH_NAME,fin.FIN_YEAR_DESC,mst.MONTH_NAME,look.LOOKUP_NAME,mst.MONTH_ID,fin.FIN_YEAR_CODE ");

			lSBQuery.append(" union all ");

			lSBQuery.append(" SELECT substr(upper(mst.MONTH_NAME),1,3)||'-'||case when mst.month_id >3 then cast(fin.FIN_YEAR_CODE as bigint) else cast((fin.FIN_YEAR_CODE)+1 as bigint) end as year,fin.FIN_YEAR_DESC,sum(trn.contribution) ,mst.month_id,look.LOOKUP_NAME  ,sum(trn.contribution+trn.contribution)as total   from TRN_DCPS_CONTRIBUTION trn ");   
			lSBQuery.append(" inner join SGVC_FIN_YEAR_MST fin on  trn.STARTDATE>=fin.from_date and fin.to_date>=trn.STARTDATE ");  
			lSBQuery.append(" inner join SGVA_MONTH_MST mst on mst.MONTH_ID = month(trn.STARTDATE)  ");
			lSBQuery.append(" inner join CMN_LOOKUP_MST look on look.LOOKUP_ID=trn.TYPE_OF_PAYMENT ");
			lSBQuery.append(" WHERE  trn.voucher_date between '"+fromDate+"' and '"+toDate+"' and trn.DCPS_EMP_ID ="+lLngDcpsEmpId+" and ((trn.MONTH_ID <> 3 and trn.FIN_YEAR_ID<>"+lLngYearId+") or (trn.MONTH_ID=3 and trn.FIN_YEAR_ID<>"+lLngYearId+"-1)) ");
			lSBQuery.append(" AND trn.REG_STATUS = 1  and  trn.EMPLOYER_CONTRI_FLAG = 'Y'  and (trn.is_missing_credit is null and trn.IS_CHALLAN is null) ");   
			lSBQuery.append(" group by mst.MONTH_NAME,fin.FIN_YEAR_DESC,mst.MONTH_NAME,look.LOOKUP_NAME,mst.MONTH_ID,fin.FIN_YEAR_CODE ");


			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lLstData = lQuery.list();
			gLogger.info("Query isd **************"+lQuery.toString());
			gLogger.info("List of data 000000000000000000"+lLstData.toString());

		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}

		return lLstData;
	}

	public Double getTier1TotalDetailsNew(Long lLngDcpsEmpId, Long lLngYearId)
	{
		List lLstData = null;
		Double totalInt=null;
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT NVL(sum(totalInt),0) FROM ");
			lSBQuery.append(" ((SELECT NVL((INT_CONTRB_EMPLOYEE + INT_CONTRB_EMPLOYER),0)  as totalInt ");
			lSBQuery.append(" FROM MST_DCPS_CONTRIB_MONTHLY_NEW where dcps_id = (select dcps_id from mst_dcps_emp where dcps_Emp_id = :dcpsEmpId ) ");			
			//lSBQuery.append(" AND YEAR_ID = "+lLngYearId+" and PAY_MONTH <> 3) ");
			lSBQuery.append(" AND YEAR_ID = "+lLngYearId+" And (IS_MISSING_CREDIT is null and IS_CHALLAN is null) )) ");
			/*	lSBQuery.append(" union ");

			lSBQuery.append(" (SELECT NVL((INT_CONTRB_EMPLOYEE + INT_CONTRB_EMPLOYER),0)  as totalInt ");
			lSBQuery.append(" FROM MST_DCPS_CONTRIBUTION_MONTHLY where dcps_id = (select dcps_id from mst_dcps_emp where dcps_Emp_id = :dcpsEmpId ) ");			
			lSBQuery.append(" AND YEAR_ID = :year and PAY_MONTH = 3)) ");*/

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("dcpsEmpId", lLngDcpsEmpId);
			//	lQuery.setLong("year", lLngYearId-1);


			lLstData = lQuery.list();
			if(lLstData.size()>0 && lLstData!=null)
				totalInt=Double.parseDouble(lLstData.get(0).toString());
		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}

		return totalInt;
	}


	public Double getTier1IntMissingDetailsNew(Long lLngDcpsEmpId, Long lLngYearId)
	{
		List lLstData = null;
		Double totalInt=null;
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT NVL(sum(totalInt),0) FROM ");
			lSBQuery.append(" ((SELECT NVL((INT_CONTRB_EMPLOYEE + INT_CONTRB_EMPLOYER),0)  as totalInt ");
			lSBQuery.append(" FROM MST_DCPS_CONTRIB_MONTHLY_NEW where dcps_id = (select dcps_id from mst_dcps_emp where dcps_Emp_id = :dcpsEmpId ) ");			
			//lSBQuery.append(" AND YEAR_ID = "+lLngYearId+" AND IS_MISSING_CREDIT='Y' and PAY_MONTH <> 3 ) ");
			lSBQuery.append(" AND YEAR_ID = "+lLngYearId+" AND (IS_MISSING_CREDIT='Y' or IS_CHALLAN='Y'))) ");
			/*	lSBQuery.append(" union ");


			lSBQuery.append(" (SELECT NVL((INT_CONTRB_EMPLOYEE + INT_CONTRB_EMPLOYER),0)  as totalInt ");
			lSBQuery.append(" FROM MST_DCPS_CONTRIBUTION_MONTHLY where dcps_id = (select dcps_id from mst_dcps_emp where dcps_Emp_id = :dcpsEmpId ) ");			
			lSBQuery.append(" AND YEAR_ID = :year AND IS_MISSING_CREDIT='Y' and PAY_MONTH = 3 )) ");*/

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("dcpsEmpId", lLngDcpsEmpId);
			//	lQuery.setLong("year", lLngYearId-1);


			lLstData = lQuery.list();
			if(lLstData.size()>0 && lLstData!=null)
				gLogger.info("Tier 1 Missing credit:"+lLstData.get(0).toString());
			totalInt=Double.parseDouble(lLstData.get(0).toString());
		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}

		return totalInt;
	}

	public Double getTotalAmountDetailsNew(Long lLngDcpsEmpId, Long lLngYearId,String fromDate,String toDate)
	{
		List lLstData = null;
		Double amount=0d;
		try{
			StringBuilder lSBQuery = new StringBuilder();

			/*	lSBQuery.append(" select NVL(sum(totalAmt),0) from ");
			lSBQuery.append(" (SELECT nvl(sum(CONTRIBUTION),0) as totalAmt FROM  TRN_DCPS_CONTRIBUTION where REG_STATUS=1 and fin_year_id="+lLngYearId+" and EMPLOYER_CONTRI_FLAG='Y'  and DCPS_EMP_ID=:dcpsEmpId and is_missing_credit is null and month(STARTDATE) <> 3");

			lSBQuery.append(" union "); 

			lSBQuery.append(" SELECT nvl(sum(CONTRIBUTION),0) as totalAmt FROM  TRN_DCPS_CONTRIBUTION where REG_STATUS=1 and fin_year_id=:lLngYearId and EMPLOYER_CONTRI_FLAG='Y'  and DCPS_EMP_ID=:dcpsEmpId and is_missing_credit is null and month(STARTDATE)= 3) ");
			 */
			lSBQuery.append(" SELECT nvl(SUM(CONTRIBUTION),0) FROM TRN_DCPS_CONTRIB_FOR_INT_CALC WHERE DCPS_EMP_ID=:dcpsEmpId  AND (is_missing_credit is null and IS_CHALLAN is null) AND REG_STATUS=1 AND  EMPLOYER_CONTRI_FLAG='Y' and    voucher_date between '"+fromDate+"' and '"+toDate+"' and ((MONTH_ID = 3 and FIN_YEAR_ID="+lLngYearId+"-1) or (MONTH_ID<>3 and FIN_YEAR_ID="+lLngYearId+")) ");


			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("dcpsEmpId", lLngDcpsEmpId);

			//lQuery.setParameter("lLngYearId", lLngYearId-1);


			lLstData = lQuery.list();
			if(lLstData!=null)
			{
				amount=Double.parseDouble(lLstData.get(0).toString());
			}
		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}


		return amount;
	}

	public Double getTotalMissingAmountDetailsNew(Long lLngDcpsEmpId, Long lLngYearId,String fromDate,String toDate)
	{
		List lLstData = null;
		Double amount=0d;
		try{
			StringBuilder lSBQuery = new StringBuilder();
		//	lSBQuery.append(" SELECT nvl(sum(CONTRIBUTION),0) FROM  TRN_DCPS_CONTRIBUTION where REG_STATUS=1 and MISSING_CREDIT_APPROVAL_DATE between '"+fromDate+"' and '"+toDate+"'  and EMPLOYER_CONTRI_FLAG='Y'  and DCPS_EMP_ID=:dcpsEmpId  and (is_missing_credit='Y' or IS_CHALLAN='Y')  ");

			lSBQuery.append(" SELECT sum(contri) FROM "); 
			lSBQuery.append(" (  ");
			lSBQuery.append(" SELECT nvl(sum(CONTRIBUTION),0) as contri FROM  TRN_DCPS_CONTRIB_FOR_INT_CALC where REG_STATUS=1 ");
			lSBQuery.append(" and MISSING_CREDIT_APPROVAL_DATE between '"+fromDate+"' and '"+toDate+"'  and EMPLOYER_CONTRI_FLAG='Y'  and DCPS_EMP_ID=:dcpsEmpId  and (is_missing_credit='Y' or IS_CHALLAN='Y') ");

			lSBQuery.append(" UNION ");

			lSBQuery.append(" SELECT nvl(sum(CONTRIBUTION),0)  as contri FROM TRN_DCPS_CONTRIB_FOR_INT_CALC WHERE REG_STATUS=1 AND VOUCHER_DATE BETWEEN '"+fromDate+"' and '"+toDate+"'   and EMPLOYER_CONTRI_FLAG='Y'  and DCPS_EMP_ID=:dcpsEmpId  and ");
			lSBQuery.append(" (is_missing_credit IS NULL and  IS_CHALLAN IS NULL) and  ((MONTH_ID <> 3 and FIN_YEAR_ID<>"+lLngYearId+") or (MONTH_ID=3 and FIN_YEAR_ID<>"+lLngYearId+"-1)) ");
			lSBQuery.append(" ) ");
			
			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("dcpsEmpId", lLngDcpsEmpId);
			//lQuery.setParameter("lLngYearId", lLngYearId);


			lLstData = lQuery.list();
			if(lLstData!=null)
			{
				amount=Double.parseDouble(lLstData.get(0).toString());
			}
		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}


		return amount;
	}

	public List getTier2DetailsNew(Long lLngDcpsEmpId, Long lLngYearId)
	{
		List lLstData = null;
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" select OPEN_TIER2,CONTRIB_TIER2,OPEN_TIER2+CONTRIB_TIER2,INT_CONTRB_TIER2 ,cast(INT_CONTRB_EMPLOYEE as decimal(16,2)) as INT_CONTRB_EMP,cast(INT_CONTRB_EMPLOYER as decimal(16,2)) as INT_CONTRB_EMPlr,OPEN_EMPLOYEE,OPEN_EMPLOYER,OPEN_INT FROM MST_DCPS_CONTRIB_YEARLY_NEW  ");
			lSBQuery.append(" WHERE dcps_id = (select dcps_id from mst_dcps_emp where dcps_Emp_id = :dcpsEmpId ) ");			
			lSBQuery.append(" AND YEAR_ID = :year ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("dcpsEmpId", lLngDcpsEmpId);
			lQuery.setLong("year", lLngYearId);

			lLstData = lQuery.list();		}catch (Exception e) {			
				gLogger.error("Error is : " + e, e);
			}

			return lLstData;
	}

	public List getTier1FinalDetailsNew(Long lLngDcpsEmpId, Long lLngYearId)
	{
		List lLstTier1FinalData = null;

		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT NVL(sum(totalEmpInt),0),NVL(sum(totalEmplrInt),0) FROM ");
			lSBQuery.append(" 	((SELECT NVL(INT_CONTRB_EMPLOYEE,0) as totalEmpInt,nvl(INT_CONTRB_EMPLOYER,0)  as totalEmplrInt  ");
			lSBQuery.append(" FROM MST_DCPS_CONTRIB_MONTHLY_NEW where dcps_id = (select dcps_id from mst_dcps_emp where dcps_Emp_id = :dcpsEmpId ) ");			
			//	lSBQuery.append(" AND YEAR_ID = "+lLngYearId+" and PAY_MONTH <> 3) ");
			lSBQuery.append(" AND YEAR_ID = "+lLngYearId+" and (is_missing_credit is null and IS_CHALLAN is null) )) ");
			/*	lSBQuery.append(" union ");

			lSBQuery.append(" (SELECT NVL(INT_CONTRB_EMPLOYEE,0) as totalEmpInt,nvl(INT_CONTRB_EMPLOYER,0)  as totalEmplrInt ");
			lSBQuery.append(" FROM MST_DCPS_CONTRIBUTION_MONTHLY where dcps_id = (select dcps_id from mst_dcps_emp where dcps_Emp_id = :dcpsEmpId ) ");			
			lSBQuery.append(" AND YEAR_ID = :year and PAY_MONTH = 3)) ");*/

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("dcpsEmpId", lLngDcpsEmpId);
			//lQuery.setLong("year", lLngYearId-1);


			lLstTier1FinalData = lQuery.list();

		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}

		return lLstTier1FinalData;
	}
	
	public List getTier1FinalIntMissingDetailsNew(Long lLngDcpsEmpId, Long lLngYearId)
	{
		List lLstTier1MissingData = null;

		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT NVL(sum(totalEmpInt),0),NVL(sum(totalEmplrInt),0) FROM ");
			lSBQuery.append(" ((SELECT NVL(INT_CONTRB_EMPLOYEE,0) as totalEmpInt,nvl(INT_CONTRB_EMPLOYER,0)  as totalEmplrInt  ");
			lSBQuery.append(" FROM MST_DCPS_CONTRIB_MONTHLY_NEW where dcps_id = (select dcps_id from mst_dcps_emp where dcps_Emp_id = :dcpsEmpId ) ");			
			//lSBQuery.append(" AND YEAR_ID = "+lLngYearId+" AND IS_MISSING_CREDIT='Y' and PAY_MONTH <> 3 ) ");
			lSBQuery.append(" AND YEAR_ID = "+lLngYearId+" AND (IS_MISSING_CREDIT='Y' or IS_CHALLAN='Y')  )) ");
			/*	lSBQuery.append(" union ");


			lSBQuery.append(" (SELECT NVL(INT_CONTRB_EMPLOYEE,0) as totalEmpInt,nvl(INT_CONTRB_EMPLOYER,0)  as totalEmplrInt ");
			lSBQuery.append(" FROM MST_DCPS_CONTRIBUTION_MONTHLY where dcps_id = (select dcps_id from mst_dcps_emp where dcps_Emp_id = :dcpsEmpId ) ");			
			lSBQuery.append(" AND YEAR_ID = :year AND IS_MISSING_CREDIT='Y' and PAY_MONTH = 3 )) ");*/

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("dcpsEmpId", lLngDcpsEmpId);
			//lQuery.setLong("year", lLngYearId-1);


			lLstTier1MissingData = lQuery.list();

		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}

		return lLstTier1MissingData;
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
