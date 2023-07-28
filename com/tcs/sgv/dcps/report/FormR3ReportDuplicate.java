package com.tcs.sgv.dcps.report;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.RoundEnvironment;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;

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


public class FormR3ReportDuplicate extends DefaultReportDataFinder implements ReportDataFinder
{
	private static final Logger gLogger = Logger.getLogger(FormR3ReportDuplicate.class);
	public static String newline = System.getProperty("line.separator");
	ServiceLocator serviceLocator = null;
	SessionFactory gObjSessionFactory = null;
	String gStrLocCode = null;
	Long gLngLangId = null;
	Long gLngPostId = null;
	Session ghibSession = null;	

	@SuppressWarnings("deprecation")
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
			Double lTotalMissingamt=0d;
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
			List lDblPayArrearEmp = null;
			List lDbMissingCreditTier1=null;
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
			Double empIntrstT2 = 0d;
			Double empMissingIntrstT2 = 0d;
			Double  emplrIntrstT2 = 0d;
			Double  emplrMissingIntrstT2 = 0d;
			Double lOpeningEmpTier1 = 0d;
			Double lOpeningEmployrTier1 = 0d;
			Double lInterestTier1=0d;
			Double openEmployeeInt=0d; 
			Double  openEmployerInt=0d;
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

			if (lObjReport.getReportCode().equals("80000920")) 
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
					lLngTreasuryId = Long.valueOf((String) lObjReport
							.getParameterValue("treasuryCode"));
					
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
				Date toDateNew = null;
				Date=getFromToDate(lLngYearId);
				lObj1 = (Object[]) Date.get(0);
				if(lObj1[0]!=null){
					fromDate=lObj1[0].toString();

				}

				if(lObj1[1]!=null){
					toDate=lObj1[1].toString();
					toDateNew=(Date) lObj1[1];
				}
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

				rowList1 = new ArrayList();

				rowList1.add(new StyledData("Name of the Employee : " + empName ,boldVO));
				dataList.add(rowList1);

				rowList1 = new ArrayList();
				rowList1.add(new StyledData("Pension Account Number : " + dcpsId ,boldVO));
				dataList.add(rowList1);

				rowList1 = new ArrayList();
				rowList1.add(new StyledData("Birth Date : "
						+ dateFormat2.format(DOB).toString() + space(25)
						+ "Treasury : (" + lLngTreasuryId.toString() + ") "
						+ lStrTreasuryName ,
						//+ space(20) + "Rate of Interest : "+ interestRate
						boldVO));
				dataList.add(rowList1);


				rowList1 = new ArrayList();
				rowList1.add(new StyledData("Date Of Joining : " + dateFormat2.format(DOJ).toString() ,boldVO));
				dataList.add(rowList1);


				rowList1 = new ArrayList();
				rowList1.add(new StyledData("Interest Rate",boldVO));
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
					rowList1.add(lDoubleIntRate + " ( " + lStrFromDateInt + " to " + lStrToDateInt + " )");
					dataList.add(rowList1);
				}


				rowList1 = new ArrayList();
				rowList1.add(new StyledData("DDO :("+lStrDdoCode+")"+ ddoName, boldVO));
				dataList.add(rowList1);

				rowList1 = new ArrayList();
				rowList1.add(new StyledData("From (Month) : "
						+ dateFormat3.format(startDate).toString() + space(15)
						+ "To (Month) : "
						+ dateFormat3.format(endDate).toString() ,boldVO));
				dataList.add(rowList1);

				rowList1 = new ArrayList();
				rowList1.add(new StyledData("Basic Pay :" + basicPay + space(15) + "D.P. :"
						+ DP + space(15) + "D.A. :" + Math.round(daRate)
						,boldVO));
				dataList.add(rowList1);


				/*	
				lObjReport.setAdditionalHeader("Treasury Name :" + lStrTreasuryName + "\r\nDDO Name :" + lStrDdoName + "\r\nEmployee Name :" + lStrEmpName + 
						"\r\nFinancial Year :" + lStrYear);*/
				
				
				List lLstServiceEnd = getServiceEndDates(Long.valueOf(Long.parseLong(lStrDcpsEmpId)));
				Date finalDate = null;
				Date superAnnDate = null;
				Date serviceEndDate = null;
				if(lLstServiceEnd!=null&&lLstServiceEnd.size()>0){
					lObj = (Object[])lLstServiceEnd.get(0);
					superAnnDate = (Date) lObj[0];
					serviceEndDate = (Date) lObj[1];
					gLogger.info("superAnnDate"+superAnnDate);
					gLogger.info("serviceEndDate"+serviceEndDate);
				}
				if(superAnnDate!=null&&serviceEndDate!=null){
					if(superAnnDate.after(serviceEndDate)){
						finalDate = serviceEndDate;
					}else{
						finalDate = superAnnDate;
					}
				}
				if(superAnnDate!=null&&serviceEndDate==null){
						finalDate = superAnnDate;
				}
				if(superAnnDate==null&&serviceEndDate!=null){
					finalDate = serviceEndDate;
				}
				if(superAnnDate==null&&serviceEndDate==null){
					Calendar cal = Calendar.getInstance();
					cal.set(2080, Calendar.JANUARY, 1); 
					finalDate = cal.getTime();				
				}
				gLogger.info("toDateNew"+toDateNew);

				if(finalDate.after(toDateNew)){
					finalDate = toDateNew;
				}

				Calendar c = Calendar.getInstance();
				c.setTime(finalDate);
				c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
				gLogger.info("date%%%%%%%%%%"+c.getTime());
				
				gLogger.info("finalDate *******"+finalDate);
				Integer finalMonth = finalDate.getMonth();
				String latestFinalDate = dateFormat2.format(c.getTime());
				gLogger.info("latestFinalDate *******"+latestFinalDate);

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
				rowList.add("Opening Balance");
				rowList.add("");
				rowList.add(lStrOpeningEmpTier1);
				rowList.add(lStrOpeningEmployrTier1);
				if ((lLstOpeningBalcTier1 != null) && (lLstOpeningBalcTier1.size() > 0))
				rowList.add(Round(Double.valueOf(lStrInterestTier1),2));
				else
				rowList.add(lStrInterestTier1);	
				
				lLstDataList.add(rowList);



				//----------- Ashish Code starts Added to display April contri added with march-----------

				String  month11 = null;
				String  month = null;
				String year = null;
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






				lDblRegRecvryEmp = getEmpAllDtlsTire1(Long.parseLong(lStrDcpsEmpId), lLngYearId, "700046",fromDate,latestFinalDate);
				for(int monID=3;monID<=14;monID++)
				{
					int newIndex = monID;
					if(monID > 12)
						newIndex = monID - 12;


					rowList = new ArrayList();
					boolean prnitOrNot = true;


					for (int i =0; i < lDblRegRecvryEmp.size(); i++) {

						Object[]obj = (Object[]) lDblRegRecvryEmp.get(i);	
						if(newIndex == Integer.parseInt(obj[3].toString())){


							EmpAmount	=((obj[2]!=null) ? obj[2].toString(): "0**");
							gLogger.info("empAmount  is------------"+EmpAmount );


							rowList.add("");
							rowList.add(" ");
							rowList.add(new StyledData(obj[0],boldVO));
							rowList.add(EmpAmount);
							rowList.add(EmpAmount);
							rowList.add("");
							prnitOrNot = false;
							break;

						}
					}

					if(prnitOrNot) 
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



				lDblDelyRecvryEmp = getEmpAllDtlsTire1(Long.parseLong(lStrDcpsEmpId), lLngYearId, "700047",fromDate,latestFinalDate);


				for(int monID=3;monID<=14;monID++)
				{
					int newIndex = monID;
					if(monID > 12)
						newIndex = monID - 12;

					rowList = new ArrayList();
					for (int i =0; i < lDblDelyRecvryEmp.size(); i++) {
						Object[]obj = (Object[]) lDblDelyRecvryEmp.get(i);	
						if(newIndex == Integer.parseInt(obj[3].toString())){
							EmpAmount1	=((obj[2]!=null) ? obj[2].toString(): "");
							gLogger.info("empAmount1  is------------"+EmpAmount1 );


							rowList.add("");
							rowList.add(" ");
							rowList.add(new StyledData(obj[0],boldVO));
							rowList.add(EmpAmount1);	
							rowList.add(EmpAmount1);
							rowList.add("");

							lLstDataList.add(rowList);
							break;

						}
					}

				}



				rowList = new ArrayList();
				rowList.add("4");
				rowList.add("DA Arrears");
				rowList.add("");
				rowList.add("");
				rowList.add("");
				rowList.add("");
				lLstDataList.add(rowList);

				String month13=null;
				String  month2 = null;
				String year2 = null;
				String EmpAmount2 =  null;

				lDblDAArrearEmp = getEmpAllDtlsTire1(Long.parseLong(lStrDcpsEmpId), lLngYearId, "700048",fromDate,latestFinalDate);
				for(int monID=3;monID<=14;monID++)
				{
					int newIndex = monID;
					if(monID > 12)
						newIndex = monID - 12;

					rowList = new ArrayList();
					for (int i =0; i < lDblDAArrearEmp.size(); i++) {
						Object[]obj = (Object[]) lDblDAArrearEmp.get(i);	
						if(newIndex == Integer.parseInt(obj[3].toString())){
							EmpAmount1	=((obj[2]!=null) ? obj[2].toString(): "");
							gLogger.info("empAmount1  is------------"+EmpAmount1 );


							rowList.add("");
							rowList.add(" ");
							rowList.add(new StyledData(obj[0],boldVO));
							rowList.add(EmpAmount1);	
							rowList.add(EmpAmount1);
							rowList.add("");

							lLstDataList.add(rowList);
							break;

						}
					}

				}


				rowList = new ArrayList();
				rowList.add("5");
				rowList.add("Pay Arrears");
				rowList.add("");
				rowList.add("");
				rowList.add("");
				rowList.add("");
				lLstDataList.add(rowList);

				String  month14 = null;
				String  month3 = null;
				String year3 = null;
				String EmpAmount3 =  null;

				lDblPayArrearEmp = getEmpAllDtlsTire1(Long.parseLong(lStrDcpsEmpId), lLngYearId, "700049",fromDate,latestFinalDate);
				for(int monID=3;monID<=14;monID++)
				{
					int newIndex = monID;
					if(monID > 12)
						newIndex = monID - 12;

					rowList = new ArrayList();
					for (int i =0; i < lDblPayArrearEmp.size(); i++) {
						Object[]obj = (Object[]) lDblPayArrearEmp.get(i);	
						if(newIndex == Integer.parseInt(obj[3].toString())){
							EmpAmount1	=((obj[2]!=null) ? obj[2].toString(): "");
							gLogger.info("empAmount1  is------------"+EmpAmount1 );


							rowList.add("");
							rowList.add(" ");
							rowList.add(new StyledData(obj[0],boldVO));
							rowList.add(EmpAmount1);	
							rowList.add(EmpAmount1);
							rowList.add("");

							lLstDataList.add(rowList);
							break;

						}
					}

				}


				rowList = new ArrayList();
				rowList.add("6");
				rowList.add("Mising Credits");
				rowList.add("");
				rowList.add("");
				rowList.add("");
				rowList.add("");
				lLstDataList.add(rowList);

				String  missingMon = null;
				String  missingMonth = null;
				String missingYearDesc = null;

				String[] arrYearSplit;

				lDbMissingCreditTier1 = getEmpAllMissingCreditTire1(Long.parseLong(lStrDcpsEmpId), fromDate,latestFinalDate,lLngYearId);
				empMisIntTier=getTier1IntMissingDetails(Long.parseLong(lStrDcpsEmpId), lLngYearId);
				for(int monID=3;monID<=14;monID++)
				{
					int newIndex = monID;
					if(monID > 12)
						newIndex = monID - 12;
					gLogger.info("Size of Missing reocrds  is------------" + lDbMissingCreditTier1.size());

					for (int i =0; i < lDbMissingCreditTier1.size(); i++) {
						rowList = new ArrayList();
						Object[]obj = (Object[]) lDbMissingCreditTier1.get(i);	

						/*missingMon = (obj[0] != null) ? obj[0].toString() : "";
						missingMonth = missingMon.substring(0, 3);


						missingYearDesc = (obj[1] != null) ? obj[1].toString() : "";
						arrYearSplit = missingYearDesc.split("-");
						gLogger.info("Year1  is------------" + arrYearSplit[0]);
						gLogger.info("Year2  is------------" + arrYearSplit[1]);*/
						if(newIndex == Integer.parseInt(obj[3].toString())){
							missingamt	=((obj[2]!=null) ? obj[2].toString(): "");
							gLogger.info("missingamt  is------------"+missingamt );
							gLogger.info("empMisIntTier  is------------"+empMisIntTier );

							rowList.add("");
							rowList.add(" ");
							//if(newIndex>3){
								rowList.add(new StyledData(obj[0]+"-"+obj[4],boldVO));
							//}
							/*else
							{
								rowList.add(new StyledData(missingMonth + "-" + arrYearSplit[1],boldVO));
							}*/
							rowList.add(missingamt);	
							rowList.add(missingamt);
							rowList.add("");

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


				lTotalAmt=getTotalAmountDetails(Long.parseLong(lStrDcpsEmpId), lLngYearId, fromDate, latestFinalDate);
				lTotalMissingamt=getTotalMissingAmountDetails(Long.parseLong(lStrDcpsEmpId), lLngYearId, fromDate, latestFinalDate);
				gLogger.info("lTotalMissingamt  is------------"+lTotalMissingamt );
				gLogger.info("lTotalAmt  is------------"+lTotalAmt );

				rowList = new ArrayList();
				rowList.add("");
				rowList.add(new StyledData("Total Missing Credit Amount",boldVO));
				rowList.add("");


				if(lTotalMissingamt!=null)
				{
					rowList.add(lTotalMissingamt);
					rowList.add(lTotalMissingamt);
				}
				else{
					rowList.add(0);
					rowList.add(0);
				}
				/*if(empMisIntTier==null)
				{
					empMisIntTier=0d;
				}
				if(empMisIntTier!=null)
				{
					rowList.add(empMisIntTier);

				}
				else
				{
					rowList.add("0");
				}*/
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
				lTotalAmt=getTotalAmountDetails(Long.parseLong(lStrDcpsEmpId), lLngYearId, fromDate, latestFinalDate);

				gLogger.info("lTotalMissingamt  is------------"+lTotalMissingamt );
				gLogger.info("lTotalAmt  is------------"+lTotalAmt );

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
				if(empTier==null)
				{
					empTier=0d;
				}
				if(empTier != null || empMisIntTier!=null)
				{
					rowList.add(empTier+empMisIntTier);

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

				rowList = new ArrayList();
				rowList.add("7");
				rowList.add("Opening Balance *");
				rowList.add("");
				rowList.add(lLngOpeningTier2);
				rowList.add("");
				rowList.add("");
				lLstDataList.add(rowList);

				rowList = new ArrayList();
				rowList.add("8");
				rowList.add("Yearly Contributions");
				rowList.add("");
				rowList.add(lLngYearlyContriTier2);
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
				rowList.add(lLngYearlyContriTier2+lLngOpeningTier2);
				rowList.add("");
				rowList.add(lLngTotalIntrstT2);

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
				rowList.add(totalContr);
				rowList.add(Double.valueOf(lTotalAmt.doubleValue()+lTotalMissingamt.doubleValue()));
				rowList.add("");
				lLstDataList.add(rowList);

				rowList = new ArrayList();
				rowList.add("");
				rowList.add("Total Interest");
				rowList.add("");
				//rowList.add(arr1[0] +" ."+ arr1[1]); empIntrstT2
				rowList.add(totalInt); 
				rowList.add(totalEmplrInt);
				rowList.add(""); 
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
				}
				
				gLogger.info("Closing balance is "+totalEmpAmt+"********"+openEmployeeInt);
				
				
				rowList = new ArrayList();
				rowList.add("");
				rowList.add("Total Amount Standing to the Credit of the Employee");
				rowList.add("");
				rowList.add(totalEmpAmt);
				rowList.add(totalEmplyrAmt);
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


				rowList1 = new ArrayList();
				rowList1.add("Certified that the details shown above are correct as per the information received in this office. However, the closing balance shown above will be subject to final adjustments on account of excess credit or excess interest, if any, which may come to the notice of this Office at a later date."+ newline+ newline);
				dataList.add(rowList1);

				rowList1 = new ArrayList();	
				rowList1.add(new StyledData("Place: Mumbai"+ newline,boldVO));
				dataList.add(rowList1);

				rowList1 = new ArrayList();
				rowList1.add(new StyledData("Date:   "
						+ dateFormat4.format(date) + space(50) +   "**Shows Missing Credits"
						,boldVO));
				dataList.add(rowList1);

				rowList1 = new ArrayList();
				rowList1.add(new StyledData("Office-in-Charge - Treasury Officer" + newline
						+ "(" + lLngTreasuryId + ") " + lStrTreasuryName
						,RightAlignVO));
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
				rowList1
				.add("This is a Computer Generated Statement, Needs No Signature"
						+newline 
						+newline);
				dataList.add(rowList1);

				rowList1 = new ArrayList();
				rowList1
				.add(new StyledData("* Tier - 2 Opening Balance has been added to the Employees contribution Opening Balance in Sr. No. 1",boldVO));
				dataList.add(rowList1);


				rowList1 = new ArrayList();
				rowList1.add(new StyledData(dateFormat5.format(date),
						RightAlignVO));
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
			lSBQuery.append("SELECT OPEN_EMPLOYEE,OPEN_EMPLOYER,OPEN_INT ");
			lSBQuery.append("FROM MST_DCPS_CONTRIB_TERMINATION_YEARLY ");
			lSBQuery.append("WHERE dcps_id in (select dcps_id from mst_dcps_emp where dcps_Emp_id = :dcpsEmpId ) ");
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



	public List getEmpAllDtlsTire1(Long lLngDcpsEmpId, Long lLngYearId, String lStrType,String fromDate,String latestFinalDate)
	{
		List lLstData = null;
		List lDblRecovery = new ArrayList();


		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT substr(upper(mst.MONTH_NAME),1,3)||'-'||case when mst.month_id >3 then cast(fin.FIN_YEAR_CODE as bigint) else cast((fin.FIN_YEAR_CODE)+1 as bigint) end as year,fin.FIN_YEAR_DESC,sum(trn.contribution),mst.month_id   from TRN_DCPS_CONTRIBUTION trn ");  
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

			lSBQuery.append("  and trn.voucher_date between '"+fromDate+"' and '"+latestFinalDate+"' AND trn.REG_STATUS = 1  and  trn.EMPLOYER_CONTRI_FLAG = 'Y'  and (trn.is_missing_credit is null and trn.IS_CHALLAN is null)  ");
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


	public List getEmpAllMissingCreditTire1(Long lLngDcpsEmpId,String fromDate,String latestFinalDate,Long lLngYearId)
	{
		List lLstData = null;
		List lDblRecovery = new ArrayList();


		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT substr(upper(mst.MONTH_NAME),1,3)||'-'||case when mst.month_id >3 then cast(fin.FIN_YEAR_CODE as bigint) else cast((fin.FIN_YEAR_CODE)+1 as bigint) end as year,fin.FIN_YEAR_DESC,sum(trn.contribution) ,mst.month_id,look.LOOKUP_NAME   from TRN_DCPS_CONTRIBUTION trn ");  
			lSBQuery.append(" inner join SGVC_FIN_YEAR_MST fin on  trn.STARTDATE>=fin.from_date and fin.to_date>=trn.STARTDATE "); 
			lSBQuery.append(" inner join SGVA_MONTH_MST mst on mst.MONTH_ID = month(trn.STARTDATE)  ");
			lSBQuery.append(" inner join CMN_LOOKUP_MST look on look.LOOKUP_ID=trn.TYPE_OF_PAYMENT ");
			lSBQuery.append(" WHERE  trn.MISSING_CREDIT_APPROVAL_DATE between '"+fromDate+"' and '"+latestFinalDate+"' and trn.DCPS_EMP_ID ="+lLngDcpsEmpId+"  ");  
			lSBQuery.append(" AND trn.REG_STATUS = 1  and  trn.EMPLOYER_CONTRI_FLAG = 'Y'  and (trn.is_missing_credit ='Y' or trn.IS_CHALLAN ='Y')  "); 
			lSBQuery.append(" group by mst.MONTH_NAME,fin.FIN_YEAR_DESC,mst.MONTH_NAME,look.LOOKUP_NAME,mst.MONTH_ID,fin.FIN_YEAR_CODE ");

			lSBQuery.append(" union all ");

			lSBQuery.append(" SELECT substr(upper(mst.MONTH_NAME),1,3)||'-'||case when mst.month_id >3 then cast(fin.FIN_YEAR_CODE as bigint) else cast((fin.FIN_YEAR_CODE)+1 as bigint) end as year,fin.FIN_YEAR_DESC,sum(trn.contribution) ,mst.month_id,look.LOOKUP_NAME   from TRN_DCPS_CONTRIBUTION trn ");   
			lSBQuery.append(" inner join SGVC_FIN_YEAR_MST fin on  trn.STARTDATE>=fin.from_date and fin.to_date>=trn.STARTDATE ");  
			lSBQuery.append(" inner join SGVA_MONTH_MST mst on mst.MONTH_ID = month(trn.STARTDATE)  ");
			lSBQuery.append(" inner join CMN_LOOKUP_MST look on look.LOOKUP_ID=trn.TYPE_OF_PAYMENT ");
			lSBQuery.append(" WHERE  trn.voucher_date between '"+fromDate+"' and '"+latestFinalDate+"' and trn.DCPS_EMP_ID ="+lLngDcpsEmpId+" and ((trn.MONTH_ID <> 3 and trn.FIN_YEAR_ID<>"+lLngYearId+") or (trn.MONTH_ID=3 and trn.FIN_YEAR_ID<>"+lLngYearId+"-1)) ");
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



	/*public List getEmployerAllDtlsTire1(Long lLngDcpsEmpId, Long lLngYearId, String lStrType, int monthId,String Fromdate,String latestFinalDate)
	{
		//List lLstData = null;
		List lDblRecovery = new ArrayList();
		try{
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append("SELECT sum(trn.contribution),mst.MONTH_NAME from TRN_DCPS_CONTRIBUTION trn ");			
			if (lStrType.equals("700047")){
				lSBQuery.append(" inner join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID = trn.DELAYED_FIN_YEAR_ID  ");
				lSBQuery.append(" inner join SGVA_MONTH_MST mst on mst.MONTH_ID =trn.DELAYED_MONTH_ID ");

				lSBQuery.append(" inner join SGVC_FIN_YEAR_MST fin on    trn.STARTDATE>=fin.from_date and fin.to_date>=trn.STARTDATE "); 
				lSBQuery.append(" inner join SGVA_MONTH_MST mst on mst.MONTH_ID = month(trn.STARTDATE)   ");



			}

			else
			{
				lSBQuery.append(" inner join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID = trn.FIN_YEAR_ID ");
				lSBQuery.append(" inner join SGVA_MONTH_MST mst on mst.MONTH_ID =trn.MONTH_ID ");
			}



		//	lSBQuery.append(" WHERE trn.dcps_emp_id =:dcpsEmpId AND trn.voucher_date between '" + Fromdate+ "' AND '" + latestFinalDate + "'   ");
			lSBQuery.append(" WHERE trn.dcps_emp_id =:dcpsEmpId AND trn.fin_year_id=:lLngYearId   ");
			lSBQuery.append(" AND trn.REG_STATUS = 1 and  trn.EMPLOYER_CONTRI_FLAG = 'Y'and trn.TYPE_OF_PAYMENT=:type  and trn.month_id='"+monthId+"'  ");
			lSBQuery.append(" 	group by mst.MONTH_NAME,fin.FIN_YEAR_DESC,mst.month_id  ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("dcpsEmpId", lLngDcpsEmpId);

			lQuery.setParameter("type", lStrType);
			lQuery.setParameter("lLngYearId", lLngYearId);

			lDblRecovery = lQuery.list();

			if(lLstData.get(0) != null && lLstData.size() > 0){
				lDblRecovery = (Double) lLstData.get(0);
			}



		}catch (Exception e) {			
			gLogger.error("Error is : " + e);
			e.printStackTrace();
		}

		return lDblRecovery;
	}
	 */
	public Double getTier1TotalDetails(Long lLngDcpsEmpId, Long lLngYearId)
	{
		List lLstData = null;
		Double totalInt=null;
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT NVL(sum(totalInt),0) FROM ");
			lSBQuery.append(" ((SELECT NVL((INT_CONTRB_EMPLOYEE + INT_CONTRB_EMPLOYER),0)  as totalInt ");
			lSBQuery.append(" FROM MST_DCPS_CONTRIB_TERMINATION_MONTHLY where dcps_id in (select dcps_id from mst_dcps_emp where dcps_Emp_id = :dcpsEmpId ) ");			
			//lSBQuery.append(" AND YEAR_ID = "+lLngYearId+" and PAY_MONTH <> 3) ");
			lSBQuery.append(" AND YEAR_ID = "+lLngYearId+" And (IS_MISSING_CREDIT is null and IS_CHALLAN is null) )) ");
			/*	lSBQuery.append(" union ");

			lSBQuery.append(" (SELECT NVL((INT_CONTRB_EMPLOYEE + INT_CONTRB_EMPLOYER),0)  as totalInt ");
			lSBQuery.append(" FROM MST_DCPS_CONTRIB_TERMINATION_MONTHLY where dcps_id = (select dcps_id from mst_dcps_emp where dcps_Emp_id = :dcpsEmpId ) ");			
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
			lSBQuery.append(" FROM MST_DCPS_CONTRIB_TERMINATION_MONTHLY where dcps_id in (select dcps_id from mst_dcps_emp where dcps_Emp_id = :dcpsEmpId ) ");			
			//lSBQuery.append(" AND YEAR_ID = "+lLngYearId+" AND IS_MISSING_CREDIT='Y' and PAY_MONTH <> 3 ) ");
			lSBQuery.append(" AND YEAR_ID = "+lLngYearId+" AND (IS_MISSING_CREDIT='Y' or IS_CHALLAN='Y'))) ");
			/*	lSBQuery.append(" union ");


			lSBQuery.append(" (SELECT NVL((INT_CONTRB_EMPLOYEE + INT_CONTRB_EMPLOYER),0)  as totalInt ");
			lSBQuery.append(" FROM MST_DCPS_CONTRIB_TERMINATION_MONTHLY where dcps_id = (select dcps_id from mst_dcps_emp where dcps_Emp_id = :dcpsEmpId ) ");			
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

	public Double getTotalAmountDetails(Long lLngDcpsEmpId, Long lLngYearId,String fromDate,String latestFinalDate)
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
			lSBQuery.append(" SELECT nvl(SUM(CONTRIBUTION),0) FROM TRN_DCPS_CONTRIBUTION WHERE DCPS_EMP_ID=:dcpsEmpId  AND (is_missing_credit is null and IS_CHALLAN is null) AND REG_STATUS=1 AND  EMPLOYER_CONTRI_FLAG='Y' and    voucher_date between '"+fromDate+"' and '"+latestFinalDate+"' and ((MONTH_ID = 3 and FIN_YEAR_ID="+lLngYearId+"-1) or (MONTH_ID<>3 and FIN_YEAR_ID="+lLngYearId+")) ");


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

	public Double getTotalMissingAmountDetails(Long lLngDcpsEmpId, Long lLngYearId,String fromDate,String latestFinalDate)
	{
		List lLstData = null;
		Double amount=0d;
		try{
			StringBuilder lSBQuery = new StringBuilder();
		//	lSBQuery.append(" SELECT nvl(sum(CONTRIBUTION),0) FROM  TRN_DCPS_CONTRIBUTION where REG_STATUS=1 and MISSING_CREDIT_APPROVAL_DATE between '"+fromDate+"' and '"+latestFinalDate+"'  and EMPLOYER_CONTRI_FLAG='Y'  and DCPS_EMP_ID=:dcpsEmpId  and (is_missing_credit='Y' or IS_CHALLAN='Y')  ");

			lSBQuery.append(" SELECT sum(contri) FROM "); 
			lSBQuery.append(" (  ");
			lSBQuery.append(" SELECT nvl(sum(CONTRIBUTION),0) as contri FROM  TRN_DCPS_CONTRIBUTION where REG_STATUS=1 ");
			lSBQuery.append(" and MISSING_CREDIT_APPROVAL_DATE between '"+fromDate+"' and '"+latestFinalDate+"'  and EMPLOYER_CONTRI_FLAG='Y'  and DCPS_EMP_ID=:dcpsEmpId  and (is_missing_credit='Y' or IS_CHALLAN='Y') ");

			lSBQuery.append(" UNION ");

			lSBQuery.append(" SELECT nvl(sum(CONTRIBUTION),0)  as contri FROM TRN_DCPS_CONTRIBUTION WHERE REG_STATUS=1 AND VOUCHER_DATE BETWEEN '"+fromDate+"' and '"+latestFinalDate+"'   and EMPLOYER_CONTRI_FLAG='Y'  and DCPS_EMP_ID=:dcpsEmpId  and ");
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
			lSBQuery.append(" select OPEN_TIER2,CONTRIB_TIER2,OPEN_TIER2+CONTRIB_TIER2,INT_CONTRB_TIER2 ,cast(INT_CONTRB_EMPLOYEE as decimal(16,2)) as INT_CONTRB_EMP,cast(INT_CONTRB_EMPLOYER as decimal(16,2)) as INT_CONTRB_EMPlr,OPEN_EMPLOYEE,OPEN_EMPLOYER,OPEN_INT FROM MST_DCPS_CONTRIB_TERMINATION_YEARLY ");
			lSBQuery.append(" WHERE dcps_id in (select dcps_id from mst_dcps_emp where dcps_Emp_id = :dcpsEmpId ) ");			
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
			lSBQuery.append(" FROM MST_DCPS_CONTRIB_TERMINATION_MONTHLY where dcps_id in (select dcps_id from mst_dcps_emp where dcps_Emp_id = :dcpsEmpId ) ");			
			//	lSBQuery.append(" AND YEAR_ID = "+lLngYearId+" and PAY_MONTH <> 3) ");
			lSBQuery.append(" AND YEAR_ID = "+lLngYearId+" and (is_missing_credit is null and IS_CHALLAN is null) )) ");
			/*	lSBQuery.append(" union ");

			lSBQuery.append(" (SELECT NVL(INT_CONTRB_EMPLOYEE,0) as totalEmpInt,nvl(INT_CONTRB_EMPLOYER,0)  as totalEmplrInt ");
			lSBQuery.append(" FROM MST_DCPS_CONTRIB_TERMINATION_MONTHLY where dcps_id = (select dcps_id from mst_dcps_emp where dcps_Emp_id = :dcpsEmpId ) ");			
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
			lSBQuery.append(" FROM MST_DCPS_CONTRIB_TERMINATION_MONTHLY where dcps_id in (select dcps_id from mst_dcps_emp where dcps_Emp_id = :dcpsEmpId ) ");			
			//lSBQuery.append(" AND YEAR_ID = "+lLngYearId+" AND IS_MISSING_CREDIT='Y' and PAY_MONTH <> 3 ) ");
			lSBQuery.append(" AND YEAR_ID = "+lLngYearId+" AND (IS_MISSING_CREDIT='Y' or IS_CHALLAN='Y')  )) ");
			/*	lSBQuery.append(" union ");


			lSBQuery.append(" (SELECT NVL(INT_CONTRB_EMPLOYEE,0) as totalEmpInt,nvl(INT_CONTRB_EMPLOYER,0)  as totalEmplrInt ");
			lSBQuery.append(" FROM MST_DCPS_CONTRIB_TERMINATION_MONTHLY where dcps_id = (select dcps_id from mst_dcps_emp where dcps_Emp_id = :dcpsEmpId ) ");			
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

	public List getServiceEndDates(Long lLngDcpsEmpId)
	{
		List lLstData = null;
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT m.SUPER_ANN_DATE ,b.end_date  FROM mst_Dcps_emp m inner join HR_EIS_EMP_MST h on h.EMP_MPG_ID = m.ORG_EMP_MST_ID ");
			lSBQuery.append(" left outer join (SELECT end.emp_id,end.created_date,end.end_date FROM HR_EIS_EMP_END_DATE end ");			
			lSBQuery.append(" inner join (select max(CREATED_DATE) created_dt,emp_id  as empid FROM HR_EIS_EMP_END_DATE group by emp_id ) a  on a.created_dt=end.created_date and a.empid=end.emp_id) b ");
			lSBQuery.append(" on h.EMP_ID = b.emp_id where m.DCPS_EMP_ID = :dcpsEmpId ");
			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("dcpsEmpId", lLngDcpsEmpId);

			lLstData = lQuery.list();		
			}catch (Exception e) {			
				gLogger.error("Error is : " + e, e);
			}

			return lLstData;
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
