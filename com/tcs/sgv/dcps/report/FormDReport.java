package com.tcs.sgv.dcps.report;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.common.business.reports.ReportDataFinder;
import com.tcs.sgv.common.dao.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAO;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.core.service.ServiceLocator;


public class FormDReport extends DefaultReportDataFinder implements ReportDataFinder
{
	private static final Logger gLogger = Logger.getLogger(FormDReport.class);
	public static String newline = System.getProperty("line.separator");
	ServiceLocator serviceLocator = null;
	SessionFactory gObjSessionFactory = null;
	String gStrLocCode = null;
	Long gLngLangId = null;
	Long gLngPostId = null;
	Session ghibSession = null;	
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/dcps/terminalFormD");

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
			String lStrNameOfOffice = "";
			List lLstEmpDetails = null;		 
			List lLstEmpPayDetails = null;		        
			String empName = "";
			String empName1 = "";        
			String dcpsId = "";        
			Object []lObj = null;
			String doj = "";
			String dateOfTermination = "";
			String reasonOfTerm = "";
			String treasuryName ="";
			String address1 ="";
			String designation ="";           
			String ddoOffice ="";       
			String formS1Id ="";       
			String deathId ="";    
			String lStrSevaarthId ="";
			String lStrtransactionId ="";
			String lStrFinal ="";
			Date serviceEndDate =null; 
			List contributionList = null;		        
			List lstManualContribution=null;
			Double totalManualContri = 0D;
			StyleVO[] rowsFontsVO = new StyleVO[7];
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
			rowsFontsVO[6] = new StyleVO();
			rowsFontsVO[6].setStyleId(IReportConstants.REPORT_PAGE_OK_BTN_URL);
			rowsFontsVO[6].setStyleValue("javaScript:self.close();");
			
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

			if (lObjReport.getReportCode().equals("80000859")) 
			{
				DateFormat dateFormat = new SimpleDateFormat("dd MMM,yyyy");
				DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
				DateFormat dateFormat3 = new SimpleDateFormat("dd MMM yyyy");
				DateFormat dateFormat4 = new SimpleDateFormat("dd/MM/yy");
				DateFormat dateFormat5 = new SimpleDateFormat("dd/MM/yy :hh:mm:ss");
				DateFormat dateFormat6 = new SimpleDateFormat("dd/MM/yyyy");

				Date date = new Date();
				dateFormat.format(date);

				lObjReport.setStyleList(rowsFontsVO);
				lStrSevaarthId = ((String) lObjReport.getParameterValue("sevarthId")).trim();
				gLogger.info("lStrSevaarthId*************" + lStrSevaarthId);
				lStrtransactionId = ((String) lObjReport.getParameterValue("transactionId")).trim();
				gLogger.info("transactionId*************" + lStrtransactionId);
				lStrFinal = ((String) lObjReport.getParameterValue("finalRep")).trim();
				gLogger.info("lStrFinal*************" + lStrFinal);
				lLstEmpDetails = getEmployeeDetails(lStrSevaarthId);

				if(lLstEmpDetails!= null && lLstEmpDetails.size()>0)
				{			
					Object[] tuple = (Object[]) lLstEmpDetails.get(0);
					empName= tuple[0].toString();
					empName1= tuple[0].toString();
					dcpsId = tuple[2].toString();
					doj = tuple[4].toString();
					dateOfTermination = tuple[13].toString();				
					reasonOfTerm = tuple[7].toString();
					designation = tuple[16].toString();
					treasuryName = tuple[8].toString();
					ddoOffice = tuple[14].toString();
					formS1Id = tuple[10].toString();
					deathId = tuple[15].toString();
					serviceEndDate =(Date)( tuple[17]);
					gLogger.info("serviceEndDate*************" + serviceEndDate);
					if(formS1Id.equalsIgnoreCase("0"))
					{
						address1 = tuple[9].toString();

					}else
					{
						address1 = tuple[11].toString();

					}	
					if(dateOfTermination.equalsIgnoreCase("0"))
					{
						dateOfTermination = tuple[6].toString();

					}
					if(designation.equalsIgnoreCase("0"))
					{
						designation = tuple[5].toString();

					}
				}
				
				int yearIdOfEnd = getFinyearId(serviceEndDate);
				gLogger.info("yearIdOfEnd"+yearIdOfEnd);
				Calendar cal = Calendar.getInstance();
				cal.setTime(serviceEndDate);
				int year1 = cal.get(Calendar.YEAR);
				int month1 = (cal.get(Calendar.MONTH))+1;
				int day1 = cal.get(Calendar.DAY_OF_MONTH);
				gLogger.info("month1   "+month1);
				gLogger.info("lStrtransactionId"+lStrtransactionId);
				StyledData	dataStyle = new StyledData();
				ArrayList rowList1 = new ArrayList();
				ArrayList headerList = new ArrayList();
				
				if(!lStrtransactionId.equalsIgnoreCase("Y")){
			
				String header1 = "<center>" + "<font size=\"1.2px\"> "  +gObjRsrcBndle.getString("FORM.FormD") + "</font></center>";
				String header2 = "<center>"	+ "<font size=\"0.8px\"> "	+gObjRsrcBndle.getString("FORM.LINE1")	+ "</font></center>";
				String header3 = "<center>"	+ "<font size=\"0.8px\"> "	+gObjRsrcBndle.getString("FORM.LINE2")	+ "</font></center>";
				String header4 = "<center>"	+ "<font size=\"0.8px\"> "	+gObjRsrcBndle.getString("FORM.LINE3")	+ "</font></center>";
				String header5 = "<center>"	+ "<font size=\"0.8px\"> "	+gObjRsrcBndle.getString("FORM.LINE4")	+ "</font></center>";
				

				String additionalHeader = header1 + header2 + header3 + header4;
				
				//lObjReport.setAdditionalHeader(new StyledData(space(145)+" Form R3"+newline+space(50)+"(As referred to in para no.18 & 23 of Government Resolution, Finance Department 1007/18/SER- 4, dated 7 July, 2007 )"+newline+space(100)+"OFFICE OF THE STATE RECORD KEEPING AGENCY, MUMBAI"+newline+space(80)+"Statement of Account in respect of contributions under the D.C.P.Scheme For the Year"+lStrYear,boldVO));

				
				dataStyle = new StyledData();
				dataStyle.setColspan(1);
				dataStyle.setStyles(CenterAlignVO);
				dataStyle.setData(gObjRsrcBndle.getString("FORM.FormD"));
				rowList1.add(dataStyle);
				dataStyle = null;
				// dataList.add(rowList1);
				headerList.add(rowList1);

				rowList1 = new ArrayList();
				dataStyle = new StyledData();
				dataStyle.setColspan(1);
				dataStyle.setStyles(CenterAlignVO);
				dataStyle.setData(gObjRsrcBndle.getString("FORM.LINE1"));
				rowList1.add(dataStyle);
				dataStyle = null;
				//dataList.add(rowList1);
				headerList.add(rowList1);

				rowList1 = new ArrayList();
				dataStyle = new StyledData();
				dataStyle.setColspan(1);
				dataStyle.setStyles(CenterAlignVO);
				dataStyle.setData(gObjRsrcBndle.getString("FORM.LINE2"));
				rowList1.add(dataStyle);
				dataStyle = null;
				// dataList.add(rowList1);
				headerList.add(rowList1);

				rowList1 = new ArrayList();
				dataStyle = new StyledData();
				dataStyle.setColspan(1);
				dataStyle.setStyles(CenterAlignVO);
				dataStyle.setData(gObjRsrcBndle.getString("FORM.LINE3"));
				rowList1.add(dataStyle);
				dataStyle = null;
				//dataList.add(rowList1);
				headerList.add(rowList1);

				rowList1 = new ArrayList();
				dataStyle = new StyledData();
				dataStyle.setColspan(1);
				dataStyle.setStyles(CenterAlignVO);
				dataStyle.setData(gObjRsrcBndle.getString("FORM.LINE4"));
				rowList1.add(dataStyle);
				dataStyle = null;
				//dataList.add(rowList1);
				headerList.add(rowList1);

				}
				String lStrFromDateInt = "";
				String lStrToDateInt = "";
				Double lDoubleIntRate = null;
				Object[] lObjIntDtls = null;
				List finList=null;
				int finYearId=0;
				String finYear="";

				gLogger.info("dateOfTermination"+dateOfTermination);

				DateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
				DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date date1 = originalFormat.parse(dateOfTermination);
				String dateOfTermination1 = targetFormat.format(date1);  
				gLogger.info("dateOfTermination1"+dateOfTermination1);

				finList = getFinYear(dateOfTermination1);
				if(finList!= null && finList.size()>0)
				{			
					Object[] tuple = (Object[]) finList.get(0);
					finYearId= Integer.parseInt(tuple[0].toString());
					finYear= tuple[1].toString();
				}
				
				gLogger.info("finYearId********"+finYearId);

				double empAmount = 0d;
				double emplyrAmount = 0d;
				double interst = 0d;
				double totalAmount = 0d;
				double openAmount = 0d;
				double finalTotal = 0d;

				if(finYearId<25)
				{		
					contributionList=getContributionDetails(dcpsId,finYearId, yearIdOfEnd ,month1);

					List lListtotalAmount=getContributionDetailsTotal(dcpsId,finYearId ,yearIdOfEnd ,month1);

					if(lListtotalAmount!= null && lListtotalAmount.size()>0)
					{			
						Object[] tuple = (Object[]) lListtotalAmount.get(0);
						empAmount= Double.parseDouble(tuple[0].toString());
						emplyrAmount= Double.parseDouble(tuple[1].toString());
						interst = Double.parseDouble(tuple[2].toString());
						totalAmount = Double.parseDouble(tuple[3].toString());
					}
					openAmount=getOpenAmount(dcpsId,finYear, yearIdOfEnd ,month1);
				}
				else
				{
					contributionList=getContributionDetailsNew(dcpsId,finYearId ,yearIdOfEnd ,month1);

					List lListtotalAmount=getContributionDetailsNewTotal(dcpsId,finYearId ,yearIdOfEnd ,month1);

					if(lListtotalAmount!= null && lListtotalAmount.size()>0)
					{			
						Object[] tuple = (Object[]) lListtotalAmount.get(0);
						empAmount= Double.parseDouble(tuple[0].toString());
						emplyrAmount= Double.parseDouble(tuple[1].toString());
						interst = Double.parseDouble(tuple[2].toString());
						totalAmount = Double.parseDouble(tuple[3].toString());

					}
					openAmount=getOpenAmountNew(dcpsId,finYearId , yearIdOfEnd ,month1);

				}
				finalTotal = openAmount+totalAmount;
				
				TabularData tData  = new TabularData(headerList);
				tData.addStyle(IReportConstants.STYLE_FONT_ALIGNMENT, IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
				tData.addStyle(IReportConstants.BORDER, "No");
				tData.addStyle(IReportConstants.SHOW_REPORT_NAME, IReportConstants.VALUE_NO);
				lObjReport.setAdditionalHeader(tData);	

				rowList1 = new ArrayList();				
				rowList1.add(new StyledData(gObjRsrcBndle.getString("FORM.FORM1")+(space(10))+" : " + empName ,boldVO));
				dataList.add(rowList1);

				rowList1 = new ArrayList();
				rowList1.add(new StyledData(gObjRsrcBndle.getString("FORM.FORM2")+(space(10))+" : "  + dcpsId ,boldVO));
				dataList.add(rowList1);

				rowList1 = new ArrayList();				
				rowList1.add(new StyledData(gObjRsrcBndle.getString("FORM.FORM3")+(space(10))+" : "  + ddoOffice ,boldVO));
				dataList.add(rowList1);

				rowList1 = new ArrayList();
				rowList1.add(new StyledData(gObjRsrcBndle.getString("FORM.FORM4")+(space(10))+" : "  + dateOfTermination ,boldVO));
				dataList.add(rowList1);

				rowList1 = new ArrayList();				
				rowList1.add(new StyledData(gObjRsrcBndle.getString("FORM.FORM5")+(space(10))+" : "  + designation ,boldVO));
				dataList.add(rowList1);

				rowList1 = new ArrayList();
				rowList1.add(new StyledData(gObjRsrcBndle.getString("FORM.FORM6")+(space(10))+" : "  + treasuryName ,boldVO));
				dataList.add(rowList1);

				rowList1 = new ArrayList();				
				rowList1.add(new StyledData(gObjRsrcBndle.getString("FORM.FORM7")+(space(10))+" : "  + finYear+newline ,boldVO));
				dataList.add(rowList1);

				rowList1 = new ArrayList();				
				rowList1.add(new StyledData(gObjRsrcBndle.getString("FORM.FORM8") ,boldVO));
				dataList.add(rowList1);

				rowList1 = new ArrayList();
				rowList1.add(new StyledData(gObjRsrcBndle.getString("FORM.FORM10")+(space(10))+" : "  +openAmount,boldVO));
				dataList.add(rowList1);

				rowList1 = new ArrayList();				
				rowList1.add(new StyledData(gObjRsrcBndle.getString("FORM.FORM11"),boldVO));
				dataList.add(rowList1);

				ArrayList rowList = new ArrayList();

								String[] arrMonths = new String[12];
								arrMonths[0] = gObjRsrcBndle.getString("FORM.FORM22");
								arrMonths[1] = gObjRsrcBndle.getString("FORM.FORM23");
								arrMonths[2] = gObjRsrcBndle.getString("FORM.FORM12");
								arrMonths[3] = gObjRsrcBndle.getString("FORM.FORM13");
								arrMonths[4] = gObjRsrcBndle.getString("FORM.FORM14");
								arrMonths[5] = gObjRsrcBndle.getString("FORM.FORM15");
								arrMonths[6] = gObjRsrcBndle.getString("FORM.FORM16");
								arrMonths[7] = gObjRsrcBndle.getString("FORM.FORM17");
								arrMonths[8] = gObjRsrcBndle.getString("FORM.FORM18");
								arrMonths[9] = gObjRsrcBndle.getString("FORM.FORM19");
								arrMonths[10] = gObjRsrcBndle.getString("FORM.FORM20");
								arrMonths[11] = gObjRsrcBndle.getString("FORM.FORM21");
								

				String  month11 = null;
				String  month = null;
				String year = null;
//				String EmpAmount =  null;

				//			String[] arrYear = lStrYear.split("-");

//				String[] arrMonths = new String[12];
//
//				arrMonths[0] = "JAN";
//				arrMonths[1] = "FEB";
//				arrMonths[2] = "MAR";
//				arrMonths[3] = "APR";
//				arrMonths[4] = "MAY";
//				arrMonths[5] = "JUN";
//				arrMonths[6] = "JUL";
//				arrMonths[7] = "AUG";
//				arrMonths[8] = "SEP";
//				arrMonths[9] = "OCT";
//				arrMonths[10] = "NOV";
//				arrMonths[11] = "DEC";				

				for(int monID=3;monID<=14;monID++)
				{
					int newIndex = monID;
					if(monID > 12)
						newIndex = monID - 12;


					rowList = new ArrayList();
					boolean prnitOrNot = true;


					for (int i =0; i < contributionList.size(); i++) {

						Object[]obj = (Object[]) contributionList.get(i);	
						if(newIndex == Integer.parseInt(obj[1].toString())){

							rowList.add(arrMonths[newIndex-1]);
							rowList.add(obj[2]);
							rowList.add(obj[3]);
							rowList.add(obj[4]);
							rowList.add(obj[5]);
							prnitOrNot = false;
							break;

						}
					}

					if(prnitOrNot) 
					{
						rowList.add(arrMonths[newIndex-1]);
						rowList.add('0');
						rowList.add('0');
						rowList.add('0');
						rowList.add('0');
					}
					lLstDataList.add(rowList);
				}
				if(lStrtransactionId.equalsIgnoreCase("Y")){
				rowList = new ArrayList();
				rowList.add("Manual Contributions");
				rowList.add(' ');
				rowList.add(' ');
				rowList.add(' ');
				rowList.add(' ');
				lLstDataList.add(rowList);
				
				lstManualContribution = manualBillEntryData(lStrSevaarthId);
				if(lstManualContribution.size()>0)
				{
					for(int i=0; i<lstManualContribution.size();i++){
					Object[] tuple = (Object[]) lstManualContribution.get(i);
					totalManualContri += Double.parseDouble(tuple[1].toString()); 
					gLogger.info("totalManualContri " +totalManualContri);
				rowList = new ArrayList();
				rowList.add(tuple[0].toString());
				rowList.add(tuple[1].toString());
				rowList.add(tuple[1].toString());
				rowList.add('0');
				rowList.add('0');
				lLstDataList.add(rowList);
					}
				}
				}
				rowList = new ArrayList();
				rowList.add(gObjRsrcBndle.getString("FORM.FORM24"));
				rowList.add(empAmount+totalManualContri);
				rowList.add(emplyrAmount+totalManualContri);
				rowList.add(interst);
				rowList.add(finalTotal);
				lLstDataList.add(rowList);

				
					
				rptTd = new TabularData(lLstDataList);
				RptVo = reportsDao.getReport("80000860", lObjReport.getLangId(), 
						lObjReport.getLocId());
				rptTd.setRelatedReport(RptVo);

				rptList1 = new ArrayList();
				rptList1.add(rptTd);
				dataList.add(rptList1);
				/*
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
				 */

				if(!lStrtransactionId.equalsIgnoreCase("Y")){
				rowList1 = new ArrayList();				
				rowList1.add(gObjRsrcBndle.getString("FORM.FORM25"));
				dataList.add(rowList1);

				rowList1 = new ArrayList();
				rowList1.add(new StyledData(gObjRsrcBndle.getString("FORM.FORM26"),CenterAlignVO));
				dataList.add(rowList1);

				}


			}
		}

		catch (Exception e) {
			e.printStackTrace();
			gLogger.info("findReportData(): Exception is" + e, e);
		} 
		return dataList;
	}



	public List getEmployeeDetails(String lStrSevaarthId){
		List lLstEmpDeselect = null;  	
		StringBuilder  Strbld = new StringBuilder();
		try {
			Strbld.append(" SELECT dcps.EMP_NAME,dcps.SEVARTH_ID,dcps.DCPS_ID,dcps.PRAN_NO,to_char(dcps.DOJ,'dd/MM/yyyy'),d.DSGN_NAME,to_char(emp.END_DATE,'dd/MM/yyyy'),l.LOOKUP_NAME,loc.LOC_NAME, ");
			Strbld.append(" dcps.ADDRESS_BUILDING ||','|| dcps.ADDRESS_STREET ||','|| dcps.LANDMARK || ',' || dcps.DISTRICT || ',' || dcps.PINCODE as emp_add,nvl(s1.FORM_S1_ID,0), ");
			Strbld.append(" s1.PRESENT_ADD_FLAT_UNIT_NO_BLOCK_NO ||','|| s1.PRESENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE ||','|| s1.PRESENT_ADDRESS_AREA_LOCALITY_TALUKA || ',' || s1.PRESENT_ADDRESS_DISTRICT_TOWN_CITY || ',' || s1.PRESENT_ADDRESS_STATE_UNION_TERRITORY || ',' || s1.PRESENT_ADDRESS_PIN_CODE as s1_add,tern.TERMINATION_DTLS,nvl(to_char(tern.TERMINATION_DATE,'dd/MM/yyyy'),0),(ddo.off_name||','||ddo.address1),l.LOOKUP_ID ,nvl(d2.DSGN_NAME,'0'),TERMINATION_DATE  from ");
			Strbld.append(" MST_DCPS_EMP dcps  inner join ORG_DESIGNATION_MST d on d.DSGN_ID=dcps.DESIGNATION ");
			Strbld.append(" inner join HR_EIS_EMP_MST mst on mst.emp_mpg_id=dcps.ORG_EMP_MST_ID");
			Strbld.append(" inner join HR_EIS_EMP_END_DATE emp on emp.EMP_ID=mst.EMP_ID ");
			Strbld.append(" inner join CMN_LOOKUP_MST l on l.LOOKUP_ID=emp.REASON ");
			Strbld.append(" left outer join FRM_FORM_S1_DTLS s1 on s1.SEVARTH_ID=dcps.SEVARTH_ID ");
			Strbld.append(" left outer join TERN_TERMINATION_DTLS tern on tern.SEVARTH_ID=dcps.SEVARTH_ID ");
			Strbld.append(" left outer join ORG_DESIGNATION_MST d2 on d2.DSGN_ID= tern.DESIGNATION  ");
			Strbld.append(" left outer join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)= substr(dcps.DDO_CODE,1,2) and loc.DEPARTMENT_ID=100003 ");
			Strbld.append(" inner join MST_DCPS_DDO_OFFICE ddo on dcps.CURR_OFF = ddo.DCPS_DDO_OFFICE_MST_ID where dcps.SEVARTH_ID ='"+lStrSevaarthId+"' and tern.STATUS not in (60009) ");

			//    		if (lStrSevaarthId != null && !"".equals(lStrSevaarthId)) {
			//    			Strbld.append(" dcps.SEVARTH_ID = :sevarthId");
			//   		}  		
			SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());

			//    		if (lStrSevaarthId != null && !"".equals(lStrSevaarthId)) {
			//    		lQuery.setString("sevarthId", lStrSevaarthId);
			//    		}  		
			gLogger.info("query getEmployeeDetails ---------"+ Strbld.toString());
			lLstEmpDeselect = lQuery.list();		
		}
		catch(Exception e)
		{
			gLogger.info("Error occured in getEmployeeDetails ---------"+ e);
			e.printStackTrace();
		}
		return lLstEmpDeselect;
	}

	public List getFinYear(String dateOftermination)
	{
		List lLstData = null;
		String lStrfinYear = "";

		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT FIN_YEAR_ID,FIN_YEAR_DESC FROM SGVC_FIN_YEAR_MST where '"+dateOftermination+"' between FROM_DATE and TO_DATE ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lLstData = lQuery.list();

//			if(lLstData.size() > 0){
//				lStrfinYear = lLstData.get(0).toString();
//			}
		}catch(Exception e){
			gLogger.error("Exception in getFinYear:" + e, e);
		}
		return lLstData;
	}	

	public List getContributionDetails(String dcpsId, int finYear ,int yearIdOfEnd ,int month1){
		List lLstEmpDeselect = null;  	
		StringBuilder  Strbld = new StringBuilder();
		try {
			Strbld.append(" SELECT substr(upper(mst.MONTH_NAME),1,3),t.PAY_MONTH, t.CUR_EMPL_CONTRIB,t.CUR_EMPLR_CONTRIB,t.CUR_TIER2_CONTRIB,cast(t.CUR_EMPL_CONTRIB+t.CUR_EMPLR_CONTRIB+t.CUR_TIER2_CONTRIB as double)  FROM TEMPR3 t ");
			Strbld.append(" inner join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_DESC = t.FIN_YEAR ");
			Strbld.append(" inner join SGVA_MONTH_MST mst on mst.MONTH_ID =t.PAY_MONTH ");
			Strbld.append(" where fin.FIN_YEAR_ID = "+finYear+" and t.EMP_ID_NO = '"+dcpsId+"' and fin.FIN_YEAR_ID <= "+yearIdOfEnd );    	
			
			if (month1 > 3){
			Strbld.append(" and  ((t.PAY_MONTH >3) and (t.PAY_MONTH <= "+month1+")) ");    	
			}
			else if(month1 ==3){
				Strbld.append(" and  t.PAY_MONTH in (1,2,3,4,5,6,7,8,9,10,11,12) ");    	
			}
			else if(month1 ==2){
				Strbld.append(" and  t.PAY_MONTH in (1,2,4,5,6,7,8,9,10,11,12) ");    	
			}
			else {
				Strbld.append(" and  t.PAY_MONTH in (1,4,5,6,7,8,9,10,11,12) ");    	
			}
			
			Strbld.append(" order by t.FIN_YEAR,t.PAY_MONTH ");    		  

			SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());

			gLogger.info("query getContributionDetails ---------"+ Strbld.toString());
			lLstEmpDeselect = lQuery.list();		
		}
		catch(Exception e)
		{
			gLogger.info("Error occured in getContributionDetails ---------"+ e);
			e.printStackTrace();
		}
		return lLstEmpDeselect;
	}

	public List getContributionDetailsNew(String dcpsId, int finYear ,int yearIdOfEnd ,int month1){
		List lLstEmpDeselect = null;  	
		StringBuilder  Strbld = new StringBuilder();
		try {
			Strbld.append(" SELECT substr(upper(mst.MONTH_NAME),1,3),con.PAY_MONTH,con.CONTRIB_EMPLOYEE,con.CONTRIB_EMPLOYER,cast(con.INT_CONTRB_EMPLOYEE+con.INT_CONTRB_EMPLOYER as double),cast(con.CONTRIB_EMPLOYEE+con.CONTRIB_EMPLOYER+con.INT_CONTRB_EMPLOYEE+con.INT_CONTRB_EMPLOYER as double) FROM MST_DCPS_CONTRIBUTION_MONTHLY con  ");
			Strbld.append(" inner join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID = con.YEAR_ID ");
			Strbld.append(" inner join SGVA_MONTH_MST mst on mst.MONTH_ID =con.PAY_MONTH ");
			Strbld.append(" where fin.FIN_YEAR_ID = "+finYear+" and con.DCPS_ID = '"+dcpsId+"'  and fin.FIN_YEAR_ID <= "+yearIdOfEnd );    		  

			if (month1 > 3){
			Strbld.append(" and  ((con.PAY_MONTH >3) and (con.PAY_MONTH <= "+month1+")) ");    	
			}
			else if(month1 ==3){
				Strbld.append(" and  con.PAY_MONTH in (1,2,3,4,5,6,7,8,9,10,11,12) ");    	
			}
			else if(month1 ==2){
				Strbld.append(" and  con.PAY_MONTH in (1,2,4,5,6,7,8,9,10,11,12) ");    	
			}
			else {
				Strbld.append(" and  con.PAY_MONTH in (1,4,5,6,7,8,9,10,11,12) ");    	
			}
			
			Strbld.append("  order by con.PAY_YEAR,con.PAY_MONTH "); 			
			
			
			SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());

			gLogger.info("query getContributionDetailsNew ---------"+ Strbld.toString());
			lLstEmpDeselect = lQuery.list();		
		}
		catch(Exception e)
		{
			gLogger.info("Error occured in getContributionDetailsNew ---------"+ e);
			e.printStackTrace();
		}
		return lLstEmpDeselect;
	}

	public List getContributionDetailsTotal(String dcpsId, int finYear ,int yearIdOfEnd ,int month1){
		List lLstEmpDeselect = null;  	
		StringBuilder  Strbld = new StringBuilder();
		try {
			Strbld.append(" SELECT nvl(cast(sum(t.CUR_EMPL_CONTRIB) as double),0),nvl(cast(sum(t.CUR_EMPLR_CONTRIB)as double),0),nvl(cast(sum(t.CUR_TIER2_CONTRIB) as double),0) ,nvl(sum(cast(t.CUR_EMPL_CONTRIB+t.CUR_EMPLR_CONTRIB+t.CUR_TIER2_CONTRIB as double)),0)  FROM TEMPR3 t ");
			Strbld.append(" inner join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_DESC = t.FIN_YEAR ");
			Strbld.append(" inner join SGVA_MONTH_MST mst on mst.MONTH_ID =t.PAY_MONTH ");
			Strbld.append(" where fin.FIN_YEAR_ID = "+finYear+" and t.EMP_ID_NO = '"+dcpsId+"'  and fin.FIN_YEAR_ID <= "+yearIdOfEnd );    		  

			if (month1 > 3){
			Strbld.append(" and  ((t.PAY_MONTH >3) and (t.PAY_MONTH <= "+month1+")) ");    	
			}
			else if(month1 ==3){
				Strbld.append(" and  t.PAY_MONTH in (1,2,3,4,5,6,7,8,9,10,11,12) ");    	
			}
			else if(month1 ==2){
				Strbld.append(" and  t.PAY_MONTH in (1,2,4,5,6,7,8,9,10,11,12) ");    	
			}
			else {
				Strbld.append(" and  t.PAY_MONTH in (1,4,5,6,7,8,9,10,11,12) ");    	
			}
			
			SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());

			gLogger.info("query getEmployeeDetails ---------"+ Strbld.toString());
			lLstEmpDeselect = lQuery.list();		
		}
		catch(Exception e)
		{
			gLogger.info("Error occured in getEmployeeDetails ---------"+ e);
			e.printStackTrace();
		}
		return lLstEmpDeselect;
	}


	public List getContributionDetailsNewTotal(String dcpsId, int finYear ,int yearIdOfEnd ,int month1){
		List lLstEmpDeselect = null;  	
		StringBuilder  Strbld = new StringBuilder();
		try {
			Strbld.append(" SELECT  nvl(cast(sum(con.CONTRIB_EMPLOYEE) as double),0), nvl(cast(sum(con.CONTRIB_EMPLOYER) as double),0),nvl(sum(cast(con.INT_CONTRB_EMPLOYEE+con.INT_CONTRB_EMPLOYER as double)),0),nvl(sum(cast(con.CONTRIB_EMPLOYEE+con.CONTRIB_EMPLOYER+con.INT_CONTRB_EMPLOYEE+con.INT_CONTRB_EMPLOYER as double)), 0) FROM MST_DCPS_CONTRIBUTION_MONTHLY con ");
			Strbld.append(" inner join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID = con.YEAR_ID ");
			Strbld.append(" inner join SGVA_MONTH_MST mst on mst.MONTH_ID =con.PAY_MONTH ");
			Strbld.append(" where fin.FIN_YEAR_ID = "+finYear+" and  con.DCPS_ID = '"+dcpsId+"'  and fin.FIN_YEAR_ID <= "+yearIdOfEnd );    		  

			if (month1 > 3){
			Strbld.append(" and  ((con.PAY_MONTH >3) and (con.PAY_MONTH <= "+month1+")) ");    	
			}
			else if(month1 ==3){
				Strbld.append(" and  con.PAY_MONTH in (1,2,3,4,5,6,7,8,9,10,11,12) ");    	
			}
			else if(month1 ==2){
				Strbld.append(" and  con.PAY_MONTH in (1,2,4,5,6,7,8,9,10,11,12) ");    	
			}
			else {
				Strbld.append(" and  con.PAY_MONTH in (1,4,5,6,7,8,9,10,11,12) ");    	
			}

			SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());

			gLogger.info("query getEmployeeDetails ---------"+ Strbld.toString());
			lLstEmpDeselect = lQuery.list();		
		}
		catch(Exception e)
		{
			gLogger.info("Error occured in getEmployeeDetails ---------"+ e);
			e.printStackTrace();
		}
		return lLstEmpDeselect;
	}

	
	public Double getOpenAmountNew(String dcpsId,int finYearId  ,int yearIdOfEnd ,int month1)
	{
		List resultList=null;
		Double lAmount = 0d;
		try
		{
			StringBuilder sb = new StringBuilder();
			SQLQuery selectQuery = null;
			sb.append("SELECT cast(OPEN_NET as double ) as double FROM MST_DCPS_CONTRIBUTION_YEARLY where DCPS_ID = '"+dcpsId+"' and YEAR_ID ="+finYearId +" and YEAR_ID <= "+yearIdOfEnd );

			selectQuery = this.ghibSession.createSQLQuery(sb.toString());
			resultList = selectQuery.list();

			if(resultList.size() > 0){
				lAmount = (Double) resultList.get(0);
			}
		}
		catch(Exception e)
		{
			gLogger.info("Error occured in getContributionDetailsNew ---------"+ e);
			e.printStackTrace();
		}
		return lAmount;
	}
	
	
	public Double getOpenAmount(String dcpsId,String finYear ,int yearIdOfEnd ,int month1)
	{
		List resultList=null;
		Double lAmount = 0d;
		try
		{
			StringBuilder sb = new StringBuilder();
			SQLQuery selectQuery = null;
			sb.append("SELECT cast(OPEN_NET as double ) as double FROM TEMPEMPR3 where EMP_ID_NO = '"+dcpsId+"' and FIN_YEAR ='"+finYear+"' and FIN_YEAR <= "+yearIdOfEnd );
			
			selectQuery = this.ghibSession.createSQLQuery(sb.toString());
			resultList = selectQuery.list();

			if(resultList.size() > 0){
				lAmount = (Double) resultList.get(0);
			}
		}
		catch(Exception e)
		{
			gLogger.info("Error occured in getContributionDetailsNew ---------"+ e);
			e.printStackTrace();
		}
		return lAmount;
	}

	public int getFinyearId(Date serviceEndDate)
	{
		List resultList=null;
		int yearId = 0;
		try
		{
			StringBuilder sb = new StringBuilder();
			SQLQuery selectQuery = null;
			sb.append(" SELECT FIN_YEAR_ID FROM SGVC_FIN_YEAR_MST where '"+ serviceEndDate+"' > FROM_DATE and '"+serviceEndDate+"' < TO_DATE");
			selectQuery = this.ghibSession.createSQLQuery(sb.toString());
			resultList = selectQuery.list();

			if(resultList.size() > 0){
				yearId = Integer.parseInt(resultList.get(0).toString());
			}
		}
		catch(Exception e)
		{
			gLogger.info("Error occured in getContributionDetailsNew ---------"+ e);
			e.printStackTrace();
		}
		return yearId;
	}
	
	
/*	public Date getServiceEndDate(String sevarthId)
	{
		List resultList=null;
		Date yearId = null;
		try
		{
			StringBuilder sb = new StringBuilder();
			SQLQuery selectQuery = null;
			sb.append(" SELECT EMP_SERVEND_DT FROM mst_dcps_emp where SEVARTH_ID = "+sevarthId);
			selectQuery = this.ghibSession.createSQLQuery(sb.toString());
			resultList = selectQuery.list();

			if(resultList.size() > 0){
				yearId = (Date) resultList.get(0);
			}
		}
		catch(Exception e)
		{
			gLogger.info("Error occured in getContributionDetailsNew ---------"+ e);
			e.printStackTrace();
		}
		return yearId;
	}
*/
	public List manualBillEntryData(String sevarthId){
		List lLstEmpDeselect = null;  	
		StringBuilder  Strbld = new StringBuilder();
		try {
			Strbld.append(" SELECT substr(upper(m.MONTH_NAME),1,3)||'-'||substr(s.FIN_YEAR_CODE,3,4), t.CONTRIBUTION FROM MST_DCPS_EMP e ");
			Strbld.append(" inner join TRN_DCPS_CONTRIBUTION t on e.DCPS_EMP_ID = t.DCPS_EMP_ID inner join SGVC_FIN_YEAR_MST s on s.FIN_YEAR_ID = t.FIN_YEAR_ID inner join SGVA_MONTH_MST m  on m.MONTH_ID = t.MONTH_ID ");
			Strbld.append(" where e.SEVARTH_ID = '"+sevarthId+"' and t.MANUAL_BILL_ENTRY is not null order by s.FIN_YEAR_CODE , t.MONTH_ID");

			SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());

			gLogger.info("query manualBillEntryData ---------"+ Strbld.toString());
			lLstEmpDeselect = lQuery.list();		
		}
		catch(Exception e)
		{
			gLogger.info("Error occured in manualBillEntryData ---------"+ e);
			e.printStackTrace();
		}
		return lLstEmpDeselect;
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
