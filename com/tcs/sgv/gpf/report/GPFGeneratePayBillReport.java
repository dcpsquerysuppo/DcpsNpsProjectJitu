package com.tcs.sgv.gpf.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.business.reports.ReportDataFinder;
import com.tcs.sgv.common.dao.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAO;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.EnglishDecimalFormat;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.report.SixPayArrearAmountDepositionReportSchedule;
import com.tcs.sgv.ess.valueobject.OrgPostMst;

public class GPFGeneratePayBillReport extends DefaultReportDataFinder implements ReportDataFinder 
{
	private static final Logger gLogger = Logger.getLogger(GPFGeneratePayBillReport.class);
	public static String newline = System.getProperty("line.separator");
	String Lang_Id = "en_US";
	String Loc_Id = "LC1";
	TabularData rptTd = null;
	ReportVO RptVo = null;
	ReportsDAO reportsDao = new ReportsDAOImpl();

	Map requestAttributes = null;
	ServiceLocator serviceLocator = null;
	SessionFactory lObjSessionFactory = null;
	Session ghibSession = null;
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/gpf/GeneratePayBill");
	
	public Collection findReportData(ReportVO report, Object criteria) throws ReportException 
	{
		requestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
		serviceLocator = (ServiceLocator) requestAttributes.get("serviceLocator");
		lObjSessionFactory = serviceLocator.getSessionFactorySlave();		
	
		Map sessionKeys = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
		Map loginDetail = (HashMap) sessionKeys.get("loginDetailsMap");
		
		ArrayList twoRows = null;
		ArrayList td = null;
	
		List<OrgPostMst> lLngPostIdList = (List) loginDetail.get("postIdList");
		OrgPostMst lObjPostMst = lLngPostIdList.get(0);
		Long lLngPostId = lObjPostMst.getPostId();
		
		StyleVO[] rowsFontsVO = new StyleVO[5];		
		rowsFontsVO[0] = new StyleVO();
		rowsFontsVO[0].setStyleId(IReportConstants.BACKGROUNDCOLOR);
		rowsFontsVO[0].setStyleValue("white");
		rowsFontsVO[1] = new StyleVO();
		rowsFontsVO[1].setStyleId(IReportConstants.BORDER);
		rowsFontsVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
		rowsFontsVO[2] = new StyleVO();
		rowsFontsVO[2].setStyleId(26);
		rowsFontsVO[2].setStyleValue("JavaScript:self.close()");
		rowsFontsVO[3] = new StyleVO();
		rowsFontsVO[3].setStyleId(IReportConstants.REPORT_PAGINATION);
		rowsFontsVO[3].setStyleValue("NO");
		rowsFontsVO[4] = new StyleVO();
		rowsFontsVO[4].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		rowsFontsVO[4].setStyleValue(IReportConstants.VALUE_FONT_SIZE_MEDIUM);
		
		StyleVO[] rightAlign = new StyleVO[2];
		rightAlign[0] = new StyleVO();
		rightAlign[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		rightAlign[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
		rightAlign[1] = new StyleVO();
		rightAlign[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		rightAlign[1].setStyleValue(IReportConstants.VALUE_FONT_SIZE_MEDIUM);
		
		StyleVO[] centerAlign = new StyleVO[2];
		centerAlign[0] = new StyleVO();
		centerAlign[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		centerAlign[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
		centerAlign[1] = new StyleVO();
		centerAlign[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		centerAlign[1].setStyleValue(IReportConstants.VALUE_FONT_SIZE_LARGE);
		
		ArrayList<Object> dataList = new ArrayList<Object>();
		
		String lStrNameOfOffice = "";
		List lLstEmpDetails = null;		
		String lStrEmpDtls = "";
		Object []lObj = null;
		
		Date today = new Date();

		Integer currDate = today.getDate();
		Integer currMonth = today.getMonth() + 1;
		Integer currYear = today.getYear() + 1900;
		
		try{
			ghibSession = lObjSessionFactory.getCurrentSession();
			
			if(report.getReportCode().equals("800010"))
			{
				report.setStyleList(rowsFontsVO);
				String lStrGpfAccNo = (String) report.getParameterValue("gpfAccNo");
				String lStrMonth = (String) report.getParameterValue("month");
				String lStrYear = (String) report.getParameterValue("year");
				String lStrTransactionId = (String) report.getParameterValue("transactionId");
				
				SimpleDateFormat lObjSimpleDate = new SimpleDateFormat("dd/MM/yyyy");
				
				lStrNameOfOffice = getNameOfOffice(lStrGpfAccNo);
				
				ArrayList<Object> rowList = new ArrayList<Object>();

				lLstEmpDetails = getEmployeeDetails(lStrGpfAccNo, lStrTransactionId);
				List lstOrderNo = getOrderNoFromGPFActNoAndTrsnID(lStrTransactionId);
				String lStrOrderNo = "";
				String ddoDesign = "";
				String tresName = "";
				String bilNo = "";
				Object lObj1[] = null;
				if(lLstEmpDetails != null && lLstEmpDetails.size() > 0){
					
					if(lstOrderNo != null && lstOrderNo.size() > 0){
						
						if(lstOrderNo.get(0) != null ){
							Object obj2[] = (Object[])lstOrderNo.get(0); 
						lStrOrderNo = obj2[0].toString();
						bilNo = obj2[1].toString();
						}
					}
					
					lObj = (Object[]) lLstEmpDetails.get(0);
					lStrEmpDtls = lObj[0].toString() + ", " + lObj[1].toString() + ", "+gObjRsrcBndle.getString("GPF.PAYBILLGENLINE29") + lObj[2].toString()+", "+lStrOrderNo;
					
					Date lDtSancDate = (Date) lObj[3];
					lStrEmpDtls += ", " + lObjSimpleDate.format(lDtSancDate);
					ddoDesign = lObj[5].toString();
					tresName = lObj[6].toString();
					
				}
				
				rowList.add(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE1") + space(200) + gObjRsrcBndle.getString("GPF.PAYBILLGENLINE2"));
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add("G.R.F.D., No.DAT/2269/5626/70/12, dt. 4-2-70;" + space(190) + "Gen.45 m. e.");
				dataList.add(rowList);				
				
				rowList = new ArrayList<Object>();
				rowList.add(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE3"));
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add("Bill No. : GPF Grp-D/"+bilNo);
				dataList.add(rowList);

				
				rowList = new ArrayList<Object>();
				rowList.add(new StyledData(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE4"), centerAlign));
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add(new StyledData(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE5"), centerAlign));
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add(new StyledData("FORM M.T.R.52", centerAlign));
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add(new StyledData("(See Rule 480)", centerAlign));
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add(new StyledData(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE6"), rightAlign));
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add(lStrNameOfOffice+" " + gObjRsrcBndle.getString("GPF.PAYBILLGENLINE7") +" "+currMonth+" "+gObjRsrcBndle.getString("GPF.PAYBILLGENLINE27"));
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add("Bill For G.P.F. Advances * of the establishment of the ________________________________ for the month of " + currMonth + "/" + currYear + " Month/year.");
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add(newline);
				dataList.add(rowList);				
				
				twoRows = new ArrayList();
				td = new ArrayList();

				td.add("1");
				td.add(lStrEmpDtls);
				td.add(lStrGpfAccNo);
				td.add(lObj[4]);
				td.add("");				
				twoRows.add(td);
				
				td = new ArrayList();				
				td.add("");
				td.add("");
				td.add("");
				td.add("");
				td.add("");				
				twoRows.add(td);
				
				td = new ArrayList();				
				td.add("");
				td.add("");
				td.add(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE28")+" Total");
				td.add(lObj[4]);
				td.add("");				
				twoRows.add(td);

				rptTd = new TabularData(twoRows);
				RptVo = reportsDao.getReport("800011", report.getLangId(), report.getLocId());
				(rptTd).setRelatedReport(RptVo);				
				
				rowList = new ArrayList();
				rowList.add(rptTd);
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add(newline);
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE8"));
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add("__________________________________________________________________");
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
							
				rowList.add("Net amount required for payment ( in words ) Rupees "+EnglishDecimalFormat.convert(((Double)Double.parseDouble(lObj[4].toString())).longValue()));
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add("__________________________________________________________________");
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add(space(30) + gObjRsrcBndle.getString("GPF.PAYBILLGENLINE9"));
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add(space(30) + "Space for Classification :-");
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add(space(30) + "B. Unfunded Debt :-");
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add(space(30) + "State Provident Fund :-");
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add(space(30) + "General Provident Fund :-" + space(250) + gObjRsrcBndle.getString("GPF.PAYBILLGENLINE10") 
									  + space(10) + "Signature");
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add(space(30) + "Rupee Branch :-");
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add(space(30) + "Account of Class IV* / other than Class IV*	Government Servants");
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add(space(30) + gObjRsrcBndle.getString("GPF.PAYBILLGENLINE11") + " "+ddoDesign);
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add(new StyledData("Designation of the Drawing Officer",rightAlign));
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add(new StyledData(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE13") + "Station"+" "+tresName,rightAlign));
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add("State for use in Treasury/Pay and Accounts Officer");
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add("Pay Rs. (                                               ) Rupees.");
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add(newline);
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add(new StyledData(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE14") + "_______/20",rightAlign));
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add(new StyledData("Dated __________/20",rightAlign));
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add(new StyledData(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE15"),rightAlign));
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add(new StyledData("Examined and entered",rightAlign));
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add(new StyledData(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE16") + "Accountant",rightAlign));
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add(new StyledData("Treasury Officer,_______________________________________" 
							+ gObjRsrcBndle.getString("GPF.PAYBILLGENLINE17") ,rightAlign));
				
				rowList = new ArrayList<Object>();
				rowList.add(newline);
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add(newline);
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add(newline);
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add(new StyledData(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE18") + space(10) +"Certificate", centerAlign));
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add(newline);
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add("1. " + gObjRsrcBndle.getString("GPF.PAYBILLGENLINE19"));
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add("1. Payment Received");
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add("2. " + gObjRsrcBndle.getString("GPF.PAYBILLGENLINE20"));
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add("2. Certificate that I have satisfied myself that all sums included in bills in Standard Form (Gen.45e or old Try.413e) drawn 1 Month / 2 Month / 3 months previous to this date with the exception of those detailed below (of which the total has been refunded by deducting from this bill) have been disbursed to the proper person and that their acquaintances have been taken and field in my office with receipt stamp duty cancelled for every payment in excess of Rs.500.");
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add("3. " + gObjRsrcBndle.getString("GPF.PAYBILLGENLINE21"));
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add("3. Certifked that the balances at my credit/ at the credit of the subscriber on the date of withdrawal covers the sum drawn in the bill. Credited also that the amount asked for in this bill is required to meet the yearly  premium  due on___________________20______________________________in respect of Policy No. the ____________________________ Co.Ltd and that the policy in question has been assigned  to the Governor of to be taken have been communicated to and accepted by the Accounts Officer_________________________in his letter No.____________dated the _______20     )");
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add("4. " + gObjRsrcBndle.getString("GPF.PAYBILLGENLINE22"));
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add("4. Certified that the umber of policies financed the General Provident Fund  does not exceed four / the number of policies financed from General Provident Fund exceeds four as these were accepted prior of 22nd june 1953.");
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add("5. " + gObjRsrcBndle.getString("GPF.PAYBILLGENLINE23"));
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add("5. Certified that I have satisfied myself that the amount withdrawn previously on the same account has been utilized by the subscriber for the purpose for which it was intended and that the relevant premium receipt / Receipts has / have been duly enfaced by me.");
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add("6. " + gObjRsrcBndle.getString("GPF.PAYBILLGENLINE24"));
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add("6. Cerficate that the presentation of this  claim/ application for withdrawal of this amount has been was made within three months from the date of payment to the LIC.");
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add("_______________________________________" + gObjRsrcBndle.getString("GPF.PAYBILLGENLINE10") 
						  + space(10) + "Signature");
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add("_______________________________________" + gObjRsrcBndle.getString("GPF.PAYBILLGENLINE12") 
						  + space(10) + "Designation");
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add("*"+gObjRsrcBndle.getString("GPF.PAYBILLGENLINE25"));
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add("*Give details here if more than one policy has to be cited.");
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add("___________________________________________________________");
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add(new StyledData(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE26") + space(10) + "Four use in Audit Office", centerAlign));
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add("Admitted Rs. ____________________________");
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add("Objected Rs. ____________________________");
				dataList.add(rowList);
				
				rowList = new ArrayList<Object>();
				rowList.add("Auditor _______________________________");
				dataList.add(rowList);
			}
		}catch (Exception e) {
			gLogger.error("Exception :" + e, e);
		}
		
		return dataList;
	}
	
	public String space(int noOfSpace) {
		String blank = "";
		for (int i = 0; i < noOfSpace; i++) {
			blank += "\u00a0";
		}
		return blank;
	}
	
	public String getNameOfOffice(String lStrGpfAccNo)
	{
		String lStrOfficeName = "";
		List lLstData = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT ODM.ddoOffice FROM OrgDdoMst ODM, MstEmpGpfAcc MEGA ");
			lSBQuery.append("WHERE MEGA.gpfAccNo = :gpfAccNo AND MEGA.ddoCode = ODM.ddoCode");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
			lLstData = lQuery.list();
			
			if(lLstData != null && lLstData.size() > 0){
				lStrOfficeName = lLstData.get(0).toString();
			}
		}catch (Exception e) {
			gLogger.error("Exception :" + e, e);
		}
		
		return lStrOfficeName;
	}
	
	public List getEmployeeDetails(String lStrGpfAccNo, String lStrTransactionId)
	{
		List lLstData = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT ME.EMP_NAME, ODM.DSGN_NAME, ME.BASIC_PAY, MGA.SANCTIONED_DATE, MGA.AMOUNT_SANCTIONED , ddo.DSGN_NAME||' ',loc.LOC_NAME ");
			lSBQuery.append("FROM MST_DCPS_EMP ME JOIN MST_EMP_GPF_ACC MEGA ");
			lSBQuery.append("ON MEGA.MST_GPF_EMP_ID = ME.DCPS_EMP_ID AND MEGA.GPF_ACC_NO = :gpfAccNo ");
			lSBQuery.append("JOIN ORG_DESIGNATION_MST ODM ON ME.DESIGNATION = ODM.DSGN_ID ");
			lSBQuery.append("JOIN MST_GPF_ADVANCE MGA ON MEGA.GPF_ACC_NO = MGA.GPF_ACC_NO AND MGA.TRANSACTION_ID = :transactionId");
			lSBQuery.append(" join org_ddo_mst ddo on ddo.DDO_CODE = me.DDO_CODE  ");
			lSBQuery.append(" join CMN_LOCATION_MST loc on loc.LOC_ID = substr(me.ddo_Code,0,4) ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
			lQuery.setParameter("transactionId", lStrTransactionId);
			
			lLstData = lQuery.list();
		}catch (Exception e) {
			gLogger.error("Exception :" + e, e);
		}
	
		return lLstData;
	}
	
	public List getOrderNoFromGPFActNoAndTrsnID( String lStrTransactionId)
	{
		List lLstData = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT req.ORDER_No,bill.BILL_NO FROM mst_gpf_req req inner join MST_GPF_BILL_DTLS bill on req.TRANSACTION_ID = bill.TRANSACTION_ID where req.TRANSACTION_ID = :transactionId");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			gLogger.info("lQuery "+lStrTransactionId);
			//lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
			lQuery.setParameter("transactionId", lStrTransactionId);
			
			lLstData = lQuery.list();
		}catch (Exception e) {
			gLogger.error("Exception :" + e, e);
		}
	
		return lLstData;
	}
		
}
