package com.tcs.sgv.dcps.report;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
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
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.common.business.reports.ReportDataFinder;
import com.tcs.sgv.common.dao.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAO;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.EnglishDecimalFormat;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportColumnVO;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.SubReportVO;
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.ess.valueobject.OrgPostMst;


public class FormEReport extends DefaultReportDataFinder implements ReportDataFinder 
{
	private static final Logger gLogger = Logger.getLogger(FormEReport.class);
	public static String newline = System.getProperty("line.separator");
	String Lang_Id = "en_US";
	String Loc_Id = "LC1";
	TabularData rptTd = null;
	ReportVO RptVo = null;
	ReportsDAO reportsDao = new ReportsDAOImpl();
	String gStrLocCode = null;
	Map requestAttributes = null;
	ServiceLocator serviceLocator = null;
	SessionFactory lObjSessionFactory = null;
	Session ghibSession = null;
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/dcps/terminalFormE");

	public Collection findReportData(ReportVO report, Object criteria) throws ReportException 
	{
		requestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
		serviceLocator = (ServiceLocator) requestAttributes.get("serviceLocator");
		lObjSessionFactory = serviceLocator.getSessionFactorySlave();		

		Map sessionKeys = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
		Map loginDetail = (HashMap) sessionKeys.get("loginDetailsMap");

		ArrayList twoRows = null;
		ArrayList td = null;


		Map lMapRequestAttributes = null;
		Map lMapSessionAttributes = null;
		LoginDetails lObjLoginVO = null;
		lMapRequestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
		lMapSessionAttributes = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
		lObjLoginVO = (LoginDetails) lMapSessionAttributes.get("loginDetails");
		gStrLocCode = lObjLoginVO.getLocation().getLocationCode();
		gLogger.info("hiii the location Code is ******&&&&&&&#########******"+gStrLocCode);

		List<OrgPostMst> lLngPostIdList = (List) loginDetail.get("postIdList");
		OrgPostMst lObjPostMst = lLngPostIdList.get(0);
		Long lLngPostId = lObjPostMst.getPostId();

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
		
		StyleVO[] RightAlignVO = new StyleVO[3];
		RightAlignVO[0] = new StyleVO();
		RightAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		RightAlignVO[0]
		             .setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
		//RightAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
		RightAlignVO[1] = new StyleVO();
		RightAlignVO[1].setStyleId(IReportConstants.BORDER);
		RightAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

		RightAlignVO[2] = new StyleVO();
		RightAlignVO[2].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
		RightAlignVO[2].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);

		StyleVO[] rightAlign = new StyleVO[2];
		rightAlign[0] = new StyleVO();
		rightAlign[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		rightAlign[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
		rightAlign[1] = new StyleVO();
		rightAlign[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		rightAlign[1].setStyleValue("13");

		StyleVO[] leftAlign = new StyleVO[2];
		leftAlign[0] = new StyleVO();
		leftAlign[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		leftAlign[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_LEFT);
		leftAlign[1] = new StyleVO();
		leftAlign[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		leftAlign[1].setStyleValue("13");

		StyleVO[] centerAlign = new StyleVO[2];
		centerAlign[0] = new StyleVO();
		centerAlign[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		centerAlign[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
		centerAlign[1] = new StyleVO();
		centerAlign[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		centerAlign[1].setStyleValue("13");


		//		StyleVO[] noBorder = new StyleVO[1];
		//		noBorder[0] = new StyleVO();
		//		noBorder[0].setStyleId(IReportConstants.BORDER);
		//		noBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

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

		ArrayList<Object> dataList = new ArrayList<Object>();
		ArrayList<Object> dataList1 = new ArrayList<Object>();

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
		String lastMonth = "";
		String lastYear = "";
		String voucherNo ="";
		String voucherDate ="";      
		String ddoOffice ="";   
		String ddoOfficeAndAddress ="";    
		String ddoOfficeAddress ="";       
		String formS1Id ="";       
		String deathId ="";       
		String formAGenDate ="";       
		
		Date today = new Date();

		Integer currDate = today.getDate();
		Integer currMonth = today.getMonth() + 1;
		Integer currYear = today.getYear() + 1900;
		String lStrDDOLocName="";
		String lStrSevaarthId ="";
		//      try{
		ghibSession = lObjSessionFactory.getCurrentSession();

		if(report.getReportCode().equals("80000858"))
		{
			report.setStyleList(rowsFontsVO);
			lStrSevaarthId = ((String) report.getParameterValue("sevarthId")).trim();
			gLogger.info("lStrSevaarthId*************" + lStrSevaarthId);
			SimpleDateFormat lObjSimpleDate = new SimpleDateFormat("dd/MM/yyyy");
			DateFormat dateFormat5 = new SimpleDateFormat("dd/MM/yy :hh:mm:ss");
			lLstEmpDetails = getEmployeeDetails(lStrSevaarthId);

			if(lLstEmpDetails!= null && lLstEmpDetails.size()>0)
			{			
				Object[] tuple = (Object[]) lLstEmpDetails.get(0);
				empName= tuple[0].toString();
				empName1= tuple[0].toString();
				dcpsId = tuple[2].toString();
				doj = tuple[4].toString();
				dateOfTermination = tuple[13].toString();				
				reasonOfTerm = tuple[17].toString();
				designation = tuple[5].toString();
				treasuryName = tuple[8].toString();
				ddoOffice = tuple[14].toString();
				formS1Id = tuple[10].toString();
				deathId = tuple[15].toString();
				formAGenDate =  tuple[16].toString();
				if(dateOfTermination.equalsIgnoreCase("0"))
				{
					dateOfTermination = tuple[6].toString();

				}
				if(reasonOfTerm.equalsIgnoreCase("0"))
				{
					reasonOfTerm = tuple[7].toString();
				}
			}

			ArrayList<Object> rowList = new ArrayList<Object>();

			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.FormE")+newline+newline,centerAlign));
			dataList.add(rowList);

			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.LINE1")+newline,centerAlign));
			dataList.add(rowList);	

			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.LINE2"),rightAlign));
			dataList.add(rowList);

			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.LINE3"),rightAlign));
			dataList.add(rowList);

			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.LINE4"),rightAlign));
			dataList.add(rowList);

			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.LINE5"),rightAlign));
			dataList.add(rowList);

			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.LINE6"),rightAlign));
			dataList.add(rowList);

			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.LINE7"),rightAlign));
			dataList.add(rowList);

			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.LINE8")+dateFormat5.format(today)+newline,rightAlign));
			dataList.add(rowList);

			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.FORM1")+newline+newline,leftAlign));
			dataList.add(rowList);
			
			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.FORM33")+"__"+ddoOffice+"__"+gObjRsrcBndle.getString("FORM.FORM2")+"__"+dateFormat5.format(today)+"__"+gObjRsrcBndle.getString("FORM.FORM3")+newline+newline
					,leftAlign));
			dataList.add(rowList);

			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.FORM4")+"__"+empName+"__"+gObjRsrcBndle.getString("FORM.FORM5")+"__"+formAGenDate+"__"+
					gObjRsrcBndle.getString("FORM.FORM3")+newline+newline
					,leftAlign));
			dataList.add(rowList);               

			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.FORM6")+"__"+treasuryName+"__"
					+gObjRsrcBndle.getString("FORM.FORM7")+newline+newline
					,leftAlign));
			dataList.add(rowList);

			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.FORM8")+newline+newline,centerAlign));
			dataList.add(rowList);

			gLogger.info("dateOfTermination"+dateOfTermination);

			DateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
			DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date = null;
			try {
				date = originalFormat.parse(dateOfTermination);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String dateOfTermination1 = targetFormat.format(date);  
			gLogger.info("dateOfTermination1"+dateOfTermination1);

			int finYear = Integer.parseInt(getFinYear(dateOfTermination1));
			gLogger.info("finYear********"+finYear);
			long amountR3 = 0L;
			if(finYear<25)
			{		
				amountR3 = Math.round(getContributionDetails(lStrSevaarthId,finYear));

			}
			else
			{
				amountR3=Math.round(getContributionDetailsNew(dcpsId,finYear));

			}
			gLogger.info("amountR3**********"+amountR3);

			String lStrTotAmtInWords = 
				EnglishDecimalFormat.convertWithSpace(new BigDecimal(Math.round(amountR3)));

			gLogger.info("lStrTotAmtInWords**********"+lStrTotAmtInWords);

			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.FORM9")+"__"+ddoOffice+"__"+gObjRsrcBndle.getString("FORM.FORM10")+"__"+empName+"__"+gObjRsrcBndle.getString("FORM.FORM11")+"__"+treasuryName+"__"
					+gObjRsrcBndle.getString("FORM.FORM12")+"__"+empName+"__"+gObjRsrcBndle.getString("FORM.FORM15")+"__"+dateOfTermination+"__"+gObjRsrcBndle.getString("FORM.FORM16")+"__"+amountR3+"__"
					+gObjRsrcBndle.getString("FORM.FORM17")+"__"+lStrTotAmtInWords+"__"+gObjRsrcBndle.getString("FORM.FORM18")+"__"+empName1+"__"+gObjRsrcBndle.getString("FORM.FORM19")+newline
					,leftAlign));
			dataList.add(rowList);

			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.FORM20")+"__"+reasonOfTerm+"__"+gObjRsrcBndle.getString("FORM.FORM21")+dateOfTermination+
					gObjRsrcBndle.getString("FORM.FORM22")+newline
					,leftAlign));
			dataList.add(rowList);

			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.FORM23")+newline,rightAlign));
			dataList.add(rowList);

			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.FORM24")+newline,rightAlign));
			dataList.add(rowList);

			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.FORM25")+newline,rightAlign));
			dataList.add(rowList);

			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.FORM26")+newline,leftAlign));
			dataList.add(rowList);

			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.FORM27")+"__"+treasuryName+"__"
					+gObjRsrcBndle.getString("FORM.FORM28")+newline
					,leftAlign));
			dataList.add(rowList);

			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.FORM29")+ddoOffice+"__"+gObjRsrcBndle.getString("FORM.FORM30")+newline
					,leftAlign));
			dataList.add(rowList);

			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.FORM31")+"__"+empName+"__"
					+gObjRsrcBndle.getString("FORM.FORM32")+newline
					,leftAlign));
			dataList.add(rowList);               

			ArrayList styleList = new ArrayList();
			TabularData tData  = new TabularData(dataList);				
			tData.addStyle(IReportConstants.STYLE_FONT_ALIGNMENT, IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
			tData.addStyle(IReportConstants.BORDER, "No");
			tData.addStyle(IReportConstants.SHOW_REPORT_NAME, IReportConstants.VALUE_NO);
			report.setAdditionalHeader(tData);

			ArrayList td1 = new ArrayList();
			td1.add("");
			dataList1.add(td1);                      	

		}          
		return dataList1;
	}

	public String space(int noOfSpace) {
		String blank = "";
		for (int i = 0; i < noOfSpace; i++) {
			blank += "\u00a0";
		}
		return blank;
	}

	public List getEmployeeDetails(String lStrSevaarthId){
		List lLstEmpDeselect = null;  	
		StringBuilder  Strbld = new StringBuilder();
		try {
			Strbld.append(" SELECT dcps.EMP_NAME,dcps.SEVARTH_ID,dcps.DCPS_ID,dcps.PRAN_NO,to_char(dcps.DOJ,'dd/MM/yyyy'),d.DSGN_NAME,to_char(emp.END_DATE,'dd/MM/yyyy'),l.LOOKUP_NAME,loc.LOC_NAME, ");
			Strbld.append(" dcps.ADDRESS_BUILDING ||','|| dcps.ADDRESS_STREET ||','|| dcps.LANDMARK || ',' || dcps.DISTRICT || ',' || dcps.PINCODE as emp_add,nvl(s1.FORM_S1_ID,0), ");
			Strbld.append(" s1.PRESENT_ADD_FLAT_UNIT_NO_BLOCK_NO ||','|| s1.PRESENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE ||','|| s1.PRESENT_ADDRESS_AREA_LOCALITY_TALUKA || ',' || s1.PRESENT_ADDRESS_DISTRICT_TOWN_CITY || ',' || s1.PRESENT_ADDRESS_STATE_UNION_TERRITORY || ',' || s1.PRESENT_ADDRESS_PIN_CODE as s1_add,tern.TERMINATION_DTLS,nvl(to_char(tern.TERMINATION_DATE,'dd/MM/yyyy'),0),(ddo.off_name||','||ddo.address1),l.LOOKUP_ID,nvl(to_char(tern.FORMA_GEN_DATE,'dd/MM/yyyy'),0) ,nvl(l3.LOOKUP_NAME,'0')   from ");
			Strbld.append(" MST_DCPS_EMP dcps  inner join ORG_DESIGNATION_MST d on d.DSGN_ID=dcps.DESIGNATION ");
			Strbld.append(" inner join HR_EIS_EMP_MST mst on mst.emp_mpg_id=dcps.ORG_EMP_MST_ID");
			Strbld.append(" inner join HR_EIS_EMP_END_DATE emp on emp.EMP_ID=mst.EMP_ID ");
			Strbld.append(" inner join CMN_LOOKUP_MST l on l.LOOKUP_ID=emp.REASON ");
			Strbld.append(" left outer join FRM_FORM_S1_DTLS s1 on s1.SEVARTH_ID=dcps.SEVARTH_ID ");
			Strbld.append(" left outer join TERN_TERMINATION_DTLS tern on tern.SEVARTH_ID=dcps.SEVARTH_ID ");
			 Strbld.append(" left outer  join CMN_LOOKUP_MST l3 on l3.LOOKUP_ID= tern.REASON_OF_TERMINATION  ");
			Strbld.append(" left outer join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)= substr(dcps.DDO_CODE,1,2) and loc.DEPARTMENT_ID=100003 ");
			Strbld.append(" inner join MST_DCPS_DDO_OFFICE ddo on dcps.CURR_OFF = ddo.DCPS_DDO_OFFICE_MST_ID where dcps.SEVARTH_ID ='"+lStrSevaarthId+"' and tern.STATUS not in (60009) ");	
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


	public List getEmpPayDetails(String sevarthID) throws Exception
	{
		List resultList=null;
		try
		{
			StringBuilder sb = new StringBuilder();
			SQLQuery selectQuery = null;
			//         Date lDtCurrDate = SessionHelper.getCurDate();
			sb.append(" SELECT paybill.PAYBILL_MONTH,mon.MONTH_NAME,paybill.PAYBILL_YEAR ,mpg.VOUCHER_NO,to_char(mpg.VOUCHER_DATE,'dd/MM/yyyy') FROM HR_PAY_PAYBILL paybill ");
			sb.append(" inner join PAYBILL_HEAD_MPG mpg on mpg.paybill_id =paybill.paybill_grp_id  and mpg.approve_flag in (1,5) inner join SGVA_MONTH_MST mon on mon.MONTH_ID=paybill.PAYBILL_MONTH and mon.LANG_ID='en_US' inner join  ");
			sb.append(" (select temp.emp_id as emp1,temp.paybill_year as year1,max(paybill1.paybill_month) as month1  from HR_PAY_PAYBILL paybill1 inner join ");
			sb.append(" (select paybill.EMP_ID as EMP_ID,max(paybill.paybill_year) as PAYBILL_YEAR from paybill_head_mpg mpg ");
			sb.append(" inner join HR_PAY_PAYBILL paybill on mpg.paybill_id =paybill.paybill_grp_id  inner join hr_eis_emp_mst eis on eis.EMP_ID=paybill.EMP_ID ");
			sb.append(" inner join mst_dcps_emp emp on emp.ORG_EMP_MST_ID =eis.EMP_MPG_ID where mpg.approve_flag in (1,5) and emp.SEVARTH_ID='"+sevarthID+"' ");
			sb.append(" group by paybill.EMP_ID) temp on temp.emp_id=paybill1.EMP_ID and temp.paybill_year=paybill1.paybill_year  ");
			sb.append(" group by temp.emp_id,temp.paybill_year) temp1 on temp1.emp1=paybill.EMP_ID and temp1.year1=paybill.PAYBILL_YEAR and temp1.month1=paybill.PAYBILL_MONTH   ");

			selectQuery = this.ghibSession.createSQLQuery(sb.toString());


			resultList = selectQuery.list();
		}
		catch (Exception e) {
			this.gLogger.error((Object)("Error is :" + e), (Throwable)e);
			throw e;
		}
		return resultList;
	}

	public Double getContributionDetails(String sevarthID ,int finYear) 
	{
		List resultList=null;
		Double lAmount = 0d;
		try
		{
			StringBuilder sb = new StringBuilder();
			SQLQuery selectQuery = null;
			sb.append(" SELECT  cast((a.closeEmplrBal+a.CloseBal)as  double) as final_amount ");
			sb.append(" from MST_DCPS_EMP dcps    ");
			sb.append(" inner join (SELECT tr3.fin_year as fin_year ,  ");
			sb.append(" cast((tr3.OPEN_EMPLR_CONTRIB+sum(temp.CUR_EMPLR_CONTRIB)+tr3.INT_EMPL_CONTRIB) as DECIMAL(16,2))  as closeEmplrBal,  ");
			sb.append(" cast((tr3.INT_TIER2_CONTRIB+tr3.INT_EMPL_CONTRIB+tr3.OPEN_EMPL_CONTRIB+tr3.tier2 +sum(temp.CUR_EMPL_CONTRIB))as DECIMAL(16,2)) as  CloseBal ,temp.emp_id_no as dcps_id  ");
			sb.append(" FROM TEMPR3 temp inner join TEMPEMPR3 tr3 on temp.emp_id_no=tr3.emp_id_no      ");
			sb.append(" inner join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_DESC=temp.fin_year and fin.FIN_YEAR_DESC=tr3.fin_year    ");
			sb.append(" inner join mst_dcps_emp emp on emp.DCPS_ID = temp.emp_id_no   ");
			sb.append(" where  emp.SEVARTH_ID ='"+sevarthID+"' and fin.FIN_YEAR_ID  ="+finYear);
			sb.append(" group by tr3.INT_EMPL_CONTRIB,tr3.INT_EMPL_CONTRIB,tr3.TIER2,tr3.INT_TIER2_CONTRIB,tr3.OPEN_EMPLR_CONTRIB,tr3.OPEN_EMPL_CONTRIB,tr3.OPEN_INT,tr3.fin_year,temp.emp_id_no ) a   ");
			sb.append(" on a.dcps_id = dcps.DCPS_ID where  dcps.SEVARTH_ID = '"+sevarthID+"' ");
			selectQuery = this.ghibSession.createSQLQuery(sb.toString());
			resultList = selectQuery.list();
			if(resultList.size() > 0){
				lAmount = (Double) resultList.get(0);
			}
		}
		catch(Exception e)
		{
			gLogger.info("Error occured in getContributionDetails ---------"+ e);
			e.printStackTrace();
		}
		return lAmount;
	}

	public Double getContributionDetailsNew(String dcpsId,int finYear)
	{
		List resultList=null;
		Double lAmount = 0d;
		try
		{
			StringBuilder sb = new StringBuilder();
			SQLQuery selectQuery = null;
			sb.append("SELECT yr.CLOSE_NET  as double ");
			sb.append("from MST_DCPS_CONTRIBUTION_YEARLY yr  ");
			sb.append(" inner join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID = yr.YEAR_ID ");
			sb.append("where  yr.DCPS_ID = '"+dcpsId+"' and yr.YEAR_ID ="+finYear);
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

	public String getFinYear(String dateOftermination)
	{
		List lLstData = null;
		String lStrfinYear = "";

		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT FIN_YEAR_ID FROM SGVC_FIN_YEAR_MST where '"+dateOftermination+"' between FROM_DATE and TO_DATE ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lLstData = lQuery.list();

			if(lLstData.size() > 0){
				lStrfinYear = lLstData.get(0).toString();
			}
		}catch(Exception e){
			gLogger.error("Exception in getFinYear:" + e, e);
		}
		return lStrfinYear;
	}



}


