package com.tcs.sgv.dcps.report;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
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
import com.tcs.sgv.common.valuebeans.reports.ReportColumnVO;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.SubReportVO;
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.ess.valueobject.OrgPostMst;


public class FormAReport extends DefaultReportDataFinder implements ReportDataFinder 
{
	private static final Logger gLogger = Logger.getLogger(FormAReport.class);
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
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/dcps/terminalFormA");

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
		
		

		StyleVO[] styleNoBorder = new StyleVO[3];
		styleNoBorder[0] = new StyleVO();
		styleNoBorder[0].setStyleId(IReportConstants.ROW_HIGH_LIGHT);
		styleNoBorder[0].setStyleValue("NO");
		styleNoBorder[1] = new StyleVO();
		styleNoBorder[1].setStyleId(IReportConstants.REPORT_PAGINATION);
		styleNoBorder[1].setStyleValue("NO");
		styleNoBorder[2] = new StyleVO();
		styleNoBorder[2].setStyleId(IReportConstants.REPORT_PAGE_OK_BTN_URL);
		styleNoBorder[2].setStyleValue("javaScript:self.close();");

		StyleVO[] rowsFontsVO = new StyleVO[4];		
		rowsFontsVO[0] = new StyleVO();
		rowsFontsVO[0].setStyleId(IReportConstants.BACKGROUNDCOLOR);
		rowsFontsVO[0].setStyleValue("white");
		rowsFontsVO[1] = new StyleVO();
		rowsFontsVO[1].setStyleId(IReportConstants.BORDER);
		rowsFontsVO[1].setStyleValue("YES");     
		//rowsFontsVO[2] = new StyleVO();
		//rowsFontsVO[2].setStyleId(26);
		//rowsFontsVO[2].setStyleValue("javaScript:self.close()");
		rowsFontsVO[2] = new StyleVO();
		rowsFontsVO[2].setStyleId(IReportConstants.REPORT_PAGINATION);
		rowsFontsVO[2].setStyleValue("NO");
		rowsFontsVO[3] = new StyleVO();
		rowsFontsVO[3].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		rowsFontsVO[3].setStyleValue(IReportConstants.VALUE_FONT_SIZE_MEDIUM);
		//        rowsFontsVO[5] = new StyleVO();
		//        rowsFontsVO[5].setStyleId(IReportConstants.ROW_HIGH_LIGHT);
		//        rowsFontsVO[5].setStyleValue("NO");

		StyleVO[] rightAlign = new StyleVO[2];
		rightAlign[0] = new StyleVO();
		rightAlign[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		rightAlign[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
		rightAlign[1] = new StyleVO();
		rightAlign[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		rightAlign[1].setStyleValue("12");              

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

		StyleVO[] centerAlign1 = new StyleVO[2];
		centerAlign1[0] = new StyleVO();
		centerAlign1[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		centerAlign1[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
		centerAlign1[1] = new StyleVO();
		centerAlign1[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		centerAlign1[1].setStyleValue("13");

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
		String formS1Id ="";       
		String deathId ="";       
		String nomineeName ="";       
		String nomineeRelationship ="";       

		Date today = new Date();

		Integer currDate = today.getDate();
		Integer currMonth = today.getMonth() + 1;
		Integer currYear = today.getYear() + 1900;
		String lStrDDOLocName="";
		String lStrSevaarthId ="";
		//      try{
		ghibSession = lObjSessionFactory.getCurrentSession();

		if(report.getReportCode().equals("80000850"))
		{
			report.setStyleList(styleNoBorder);
			//report.setStyleList(styleClose);
			//     String lStrSevaarthId = StringUtility.getParameter("sevarthId", request).trim().toUpperCase();

			lStrSevaarthId = ((String) report.getParameterValue("sevarthId")).trim();
			gLogger.info("lStrSevaarthId*************" + lStrSevaarthId);
			SimpleDateFormat lObjSimpleDate = new SimpleDateFormat("dd/MM/yyyy");
			gLogger.info("lObjSimpleDate*************" + lObjSimpleDate);
			gLogger.info("today date*************" + today);
			DateFormat dateFormat5 = new SimpleDateFormat("dd/MM/yy :hh:mm:ss");
			lLstEmpDetails = getEmployeeDetails(lStrSevaarthId);
			if(lLstEmpDetails!= null && lLstEmpDetails.size()>0)
			{			
				Object[] tuple = (Object[]) lLstEmpDetails.get(0);
				empName= tuple[0].toString();
				empName1= tuple[0].toString();
				doj = tuple[4].toString();
				dateOfTermination = tuple[13].toString();				
				reasonOfTerm = tuple[7].toString();
				treasuryName = tuple[8].toString();
				ddoOffice = tuple[14].toString();
				formS1Id = tuple[10].toString();
				deathId = tuple[15].toString();
				if(formS1Id.equalsIgnoreCase("0"))
				{
					address1 = tuple[9].toString();
					nomineeName = tuple[17].toString();
				}else
				{
					address1 = tuple[11].toString();
					nomineeName = tuple[16].toString();

				}	
				if(dateOfTermination.equalsIgnoreCase("0"))
				{
					dateOfTermination = tuple[6].toString();

				}
				if(deathId.equalsIgnoreCase("250007"))
				{
					empName1 = "";
					address1 = "";
				}

			}
			gLogger.info("deathId**********" + deathId);
			gLogger.info("formS1Id**********" + formS1Id);
			gLogger.info("empName**********" + empName);

			ArrayList<Object> rowList = new ArrayList<Object>();

			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.FormA")+newline+newline,centerAlign));
			dataList.add(rowList);

			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.LINE1")+newline,leftAlign));
			dataList.add(rowList);	

			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.LINE2"),leftAlign));
			dataList.add(rowList);

			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.EmployeeNameAndAddress")+empName1+newline+address1+newline,rightAlign));
			dataList.add(rowList);

			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.Date")+space(5)+dateFormat5.format(today)+newline+newline,rightAlign));
			dataList.add(rowList);

			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.SALUTATION1"),leftAlign));
			dataList.add(rowList);


			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.SALUTATION2"),leftAlign));
			dataList.add(rowList);


			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.SALUTATION3")+ddoOffice,leftAlign));
			dataList.add(rowList);


			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.SALUTATION4"),leftAlign));
			dataList.add(rowList);


			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.SALUTATION5"),leftAlign));
			dataList.add(rowList);

			ArrayList styleList = new ArrayList();
			TabularData tData  = new TabularData(dataList);				
			tData.addStyle(IReportConstants.STYLE_FONT_ALIGNMENT, IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
			tData.addStyle(IReportConstants.BORDER, "No");
			tData.addStyle(IReportConstants.SHOW_REPORT_NAME, IReportConstants.VALUE_NO);
			report.setAdditionalHeader(tData);

			ArrayList td2 = new ArrayList();
			td2.add("");
			dataList1.add(td2);                
		}
		if(report.getReportCode().equals("80000853"))
		{
			report.setStyleList(rowsFontsVO);
			lStrSevaarthId = ((String) report.getParameterValue("sevarthId")).trim();
			lLstEmpDetails = getEmployeeDetails(lStrSevaarthId);
			if(lLstEmpDetails!= null && lLstEmpDetails.size()>0)
			{			
				Object[] tuple = (Object[]) lLstEmpDetails.get(0);
				deathId = tuple[15].toString();
			}
			if(deathId.equalsIgnoreCase("250007"))
			{
				lStrSevaarthId = ((String) report.getParameterValue("sevarthId")).trim();
				gLogger.info("lStrSevaarthId*************" + lStrSevaarthId);
				SimpleDateFormat lObjSimpleDate = new SimpleDateFormat("dd/MM/yyyy");
				gLogger.info("lObjSimpleDate*************" + lObjSimpleDate);
				gLogger.info("today date*************" + today);
				DateFormat dateFormat5 = new SimpleDateFormat("dd/MM/yy :hh:mm:ss");
				lLstEmpDetails = getEmployeeDetails(lStrSevaarthId);
				if(lLstEmpDetails!= null && lLstEmpDetails.size()>0)
				{			
					Object[] tuple = (Object[]) lLstEmpDetails.get(0);
					empName= tuple[0].toString();
					empName1= tuple[0].toString();
					doj = tuple[4].toString();
					dateOfTermination = tuple[13].toString();				
					reasonOfTerm = tuple[7].toString();
					treasuryName = tuple[8].toString();
					ddoOffice = tuple[14].toString();
					formS1Id = tuple[10].toString();
					deathId = tuple[15].toString();
					if(formS1Id.equalsIgnoreCase("0"))
					{
						address1 = tuple[9].toString();
						nomineeName = tuple[17].toString();
						nomineeRelationship = tuple[19].toString();						
					}else
					{
						address1 = tuple[11].toString();
						nomineeName = tuple[16].toString();
						String nomineeId =  tuple[18].toString();
						nomineeRelationship = getNomineeDetials(nomineeId);
					}	
					if(dateOfTermination.equalsIgnoreCase("0"))
					{
						dateOfTermination = tuple[6].toString();

					}
					if(deathId.equalsIgnoreCase("250007"))
					{
						empName1 = "";
						address1 = "";
					}

				}

				ArrayList<Object> rowList = new ArrayList<Object>();

				rowList = new ArrayList<Object>();
				rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.FORM1")+"__"+empName+"__"+gObjRsrcBndle.getString("FORM.FORM2")+"__"+doj+"__"
						+gObjRsrcBndle.getString("FORM.FORM3")+"__"+dateOfTermination+"__"+gObjRsrcBndle.getString("FORM.FORM4")+"__"+dateOfTermination+"__"+
						gObjRsrcBndle.getString("FORM.FORM5")+"__"+dateOfTermination+"__"+gObjRsrcBndle.getString("FORM.FORM6")+newline
						,leftAlign));
				dataList.add(rowList);


				rowList = new ArrayList<Object>();
				rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.FORM7")+"__"+empName+"__"+gObjRsrcBndle.getString("FORM.FORM8")+newline,leftAlign));
				dataList.add(rowList);

				ArrayList styleList = new ArrayList();
				TabularData tData  = new TabularData(dataList);				
				tData.addStyle(IReportConstants.STYLE_FONT_ALIGNMENT, IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
				tData.addStyle(IReportConstants.BORDER, "No");
				tData.addStyle(IReportConstants.SHOW_REPORT_NAME, IReportConstants.VALUE_NO);
				report.setAdditionalHeader(tData);

				ArrayList td1 = new ArrayList();
				td1.add("1");
				td1.add(nomineeName);
				td1.add(nomineeRelationship);
				dataList1.add(td1); 
			}
			else
			{
				report.setStyleList(rowsFontsVO);
				lStrSevaarthId = ((String) report.getParameterValue("sevarthId")).trim();
				gLogger.info("lStrSevaarthId*************" + lStrSevaarthId);

				lLstEmpDetails = getEmployeeDetails(lStrSevaarthId);
				if(lLstEmpDetails!= null && lLstEmpDetails.size()>0)
				{			
					Object[] tuple1 = (Object[]) lLstEmpDetails.get(0);
					empName= tuple1[0].toString();
					dcpsId = tuple1[2].toString();
					doj = tuple1[4].toString();
					designation = tuple1[20].toString();
					dateOfTermination = tuple1[13].toString();				
					reasonOfTerm = tuple1[21].toString();
					treasuryName = tuple1[8].toString();
					formS1Id = tuple1[10].toString();
					deathId = tuple1[15].toString();
					address1 = tuple1[22].toString();

					if(address1.equalsIgnoreCase("0"))
					{
					if(formS1Id.equalsIgnoreCase("0"))
					{
						address1 = tuple1[9].toString();

					}else
					{
						address1 = tuple1[11].toString();
					}
					}
					if(dateOfTermination.equalsIgnoreCase("0"))
					{
						dateOfTermination = tuple1[6].toString();

					}
					if(designation.equalsIgnoreCase("0"))
					{
						designation = tuple1[5].toString();

					}
					if(reasonOfTerm.equalsIgnoreCase("0"))
					{
						reasonOfTerm = tuple1[7].toString();
					}
				}

				try {
					lLstEmpPayDetails = getEmpPayDetails(lStrSevaarthId);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(lLstEmpPayDetails!= null && lLstEmpPayDetails.size()>0)
				{			
					Object[] tuple2 = (Object[]) lLstEmpPayDetails.get(0);
//					if((tuple2[1].toString())!= null){
//						lastMonth = tuple2[1].toString();
//						}
//						else{
//							lastMonth = "";
//						}
//						if((tuple2[2].toString())!= null){
//							lastYear = tuple2[2].toString();
//							}
//							else{
//								lastYear = "";
//							}
//						if((tuple2[3].toString())!= null){
//							voucherNo = tuple2[3].toString();
//							}
//							else{
//								voucherNo = "";
//							}
//						if((tuple2[4].toString())!= null){
//							voucherDate = tuple2[4].toString();
//							}
//							else{
//								voucherDate = "";
//							}
					lastMonth = tuple2[1].toString();
					lastYear = tuple2[2].toString();				
					voucherNo = tuple2[3].toString();
					voucherDate = tuple2[4].toString();
				}

				ArrayList<Object> rowList = new ArrayList<Object>();

				rowList = new ArrayList<Object>();
				rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.FORM9")+"__"+empName+"__"+gObjRsrcBndle.getString("FORM.FORM10")+"__"+doj+"__"
						+gObjRsrcBndle.getString("FORM.FORM11")+"__"+dateOfTermination+"__"+gObjRsrcBndle.getString("FORM.FORM12")+"__"+reasonOfTerm+"__"+
						gObjRsrcBndle.getString("FORM.FORM13")+"__"+lastMonth+"__"+gObjRsrcBndle.getString("FORM.FORM14")+"__"+lastYear+"__"+newline
						,leftAlign));
				dataList.add(rowList);

				rowList = new ArrayList<Object>();
				rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.FORM15")+"__"+treasuryName+"__"+gObjRsrcBndle.getString("FORM.FORM16")+"__"+voucherNo+"__"+
						gObjRsrcBndle.getString("FORM.FORM17")+"__"+voucherDate+"__"+gObjRsrcBndle.getString("FORM.FORM18")+newline
						,leftAlign));
				dataList.add(rowList);

				rowList = new ArrayList<Object>();
				rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.FORM19")+newline,leftAlign));
				dataList.add(rowList);


				ReportColumnVO vo= new ReportColumnVO();
				vo.setAlignment(1);
				vo.setColumnHeader(gObjRsrcBndle.getString("FORM.TABLE1"));

				vo.setColumnId(1);

				vo.setColumnLevel(1);
				vo.setColumnName(gObjRsrcBndle.getString("FORM.TABLE1"));
				vo.setDataType(10);
				vo.setDisplayTotal(0); 

				ReportColumnVO vo1= new ReportColumnVO();
				vo1.setAlignment(1);
				vo1.setColumnHeader(gObjRsrcBndle.getString("FORM.TABLE2"));

				vo1.setColumnId(1);

				vo1.setColumnLevel(1);
				vo1.setColumnName(gObjRsrcBndle.getString("FORM.TABLE2"));
				vo1.setDataType(10);
				vo1.setDisplayTotal(0); 

				ReportColumnVO vo2= new ReportColumnVO();
				vo2.setAlignment(1);
				vo2.setColumnHeader(gObjRsrcBndle.getString("FORM.TABLE3"));

				vo2.setColumnId(1);

				vo2.setColumnLevel(1);
				vo2.setColumnName(gObjRsrcBndle.getString("FORM.TABLE3"));
				vo2.setDataType(10);
				vo2.setDisplayTotal(0); 

				ReportColumnVO vo3= new ReportColumnVO();
				vo3.setAlignment(1);
				vo3.setColumnHeader(gObjRsrcBndle.getString("FORM.TABLE4"));

				vo3.setColumnId(1);

				vo3.setColumnLevel(1);
				vo3.setColumnName(gObjRsrcBndle.getString("FORM.TABLE4"));
				vo3.setDataType(10);
				vo3.setDisplayTotal(0); 

				ReportColumnVO vo4= new ReportColumnVO();
				vo4.setAlignment(1);
				vo4.setColumnHeader(gObjRsrcBndle.getString("FORM.TABLE5"));

				vo4.setColumnId(1);

				vo4.setColumnLevel(1);
				vo4.setColumnName(gObjRsrcBndle.getString("FORM.TABLE5"));
				vo4.setDataType(10);
				vo4.setDisplayTotal(0); 



				ReportColumnVO[] b= new ReportColumnVO[5];
				b[0]=vo;
				b[1]=vo1;
				b[2]=vo2;
				b[3]=vo3;
				b[4]=vo4;

				gLogger.info("b[0]*************" + b[0]);
				gLogger.info("b*************" + b);
				report.setReportColumns(b);
				report.initializeDynamicTreeModel();
				report.initializeTreeModel();

				ArrayList styleList = new ArrayList();
				TabularData tData  = new TabularData(dataList);				
				tData.addStyle(IReportConstants.STYLE_FONT_ALIGNMENT, IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
				tData.addStyle(IReportConstants.BORDER, "No");
				tData.addStyle(IReportConstants.SHOW_REPORT_NAME, IReportConstants.VALUE_NO);
				report.setAdditionalHeader(tData);

				ArrayList td3 = new ArrayList();
				td3.add(empName);
				td3.add(dcpsId);
				td3.add(doj);
				td3.add(designation);
				td3.add(address1);
				dataList1.add(td3);

			}  
		}

		if(report.getReportCode().equals("80000852"))
		{
			report.setStyleList(rowsFontsVO);
			lStrSevaarthId = ((String) report.getParameterValue("sevarthId")).trim();
			gLogger.info("lStrSevaarthId*************" + lStrSevaarthId);
			SimpleDateFormat lObjSimpleDate = new SimpleDateFormat("dd/MM/yyyy");

			ArrayList<Object> rowList = new ArrayList<Object>();

			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.FORM20")+newline,rightAlign));
			dataList.add(rowList);

			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.FORM21"),leftAlign));
			dataList.add(rowList);

			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.FORM22"),leftAlign));
			dataList.add(rowList);

			rowList = new ArrayList<Object>();
			rowList.add(new StyledData(gObjRsrcBndle.getString("FORM.FORM23"),leftAlign));
			dataList.add(rowList);

			ArrayList styleList = new ArrayList();
			TabularData tData  = new TabularData(dataList);				
			tData.addStyle(IReportConstants.STYLE_FONT_ALIGNMENT, IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
			tData.addStyle(IReportConstants.BORDER, "No");
			tData.addStyle(IReportConstants.SHOW_REPORT_NAME, IReportConstants.VALUE_NO);
			report.setAdditionalHeader(tData);

			ArrayList td2 = new ArrayList();
			td2.add("");
			dataList1.add(td2);   

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
			 Strbld.append("  SELECT dcps.EMP_NAME,dcps.SEVARTH_ID,dcps.DCPS_ID,dcps.PRAN_NO,to_char(dcps.DOJ,'dd/MM/yyyy'),d.DSGN_NAME,to_char(emp.END_DATE,'dd/MM/yyyy'),l.LOOKUP_NAME,loc.LOC_NAME,  ");
			 Strbld.append(" dcps.ADDRESS_BUILDING ||','|| dcps.ADDRESS_STREET ||','|| dcps.LANDMARK || ',' || dcps.DISTRICT || ',' || dcps.PINCODE as emp_add,nvl(s1.FORM_S1_ID,0),  ");
			 Strbld.append(" s1.PRESENT_ADD_FLAT_UNIT_NO_BLOCK_NO ||','|| s1.PRESENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE ||','|| s1.PRESENT_ADDRESS_AREA_LOCALITY_TALUKA || ',' || s1.PRESENT_ADDRESS_DISTRICT_TOWN_CITY || ',' || s1.PRESENT_ADDRESS_STATE_UNION_TERRITORY || ',' || s1.PRESENT_ADDRESS_PIN_CODE as s1_add,  ");
			 Strbld.append(" tern.TERMINATION_DTLS,nvl(to_char(tern.TERMINATION_DATE,'dd/MM/yyyy'),0),(ddo.off_name||','||ddo.address1),l.LOOKUP_ID,nvl(s1.NOMINEE_1_NAME,' '),nvl(nmn.DCPS_NMN_NAME,' '),nvl(s1.NOMINEE_1_RELATIONSHIP,' '), UPPER(case when nmn.DCPS_NMN_RLT = '-1' then ' '  when nmn.DCPS_NMN_RLT  is null then ' '  when l2.LOOKUP_NAME  is null then nmn.DCPS_NMN_RLT else l2.LOOKUP_NAME end),nvl(d2.DSGN_NAME,'0'),nvl(l3.LOOKUP_NAME,'0'),nvl(tern.CUR_ADDRESS,'0')   from  ");		
			 Strbld.append(" MST_DCPS_EMP dcps  inner join ORG_DESIGNATION_MST d on d.DSGN_ID=dcps.DESIGNATION  ");
			 Strbld.append(" inner join HR_EIS_EMP_MST mst on mst.emp_mpg_id=dcps.ORG_EMP_MST_ID ");
			 Strbld.append(" inner join HR_EIS_EMP_END_DATE emp on emp.EMP_ID=mst.EMP_ID  ");
			 Strbld.append(" inner join CMN_LOOKUP_MST l on l.LOOKUP_ID=emp.REASON  ");
			 Strbld.append(" left outer join FRM_FORM_S1_DTLS s1 on s1.SEVARTH_ID=dcps.SEVARTH_ID  ");
			 Strbld.append(" left outer join TERN_TERMINATION_DTLS tern on tern.SEVARTH_ID=dcps.SEVARTH_ID  ");
			 Strbld.append(" left outer join ORG_DESIGNATION_MST d2 on d2.DSGN_ID= tern.DESIGNATION  ");
			 Strbld.append(" left outer  join CMN_LOOKUP_MST l3 on l3.LOOKUP_ID= tern.REASON_OF_TERMINATION  ");
			 Strbld.append(" left outer join MST_DCPS_EMP_NMN nmn on nmn.DCPS_EMP_ID = dcps.DCPS_EMP_ID  ");
			 Strbld.append(" left outer join CMN_LOOKUP_MST l2 on cast(l2.LOOKUP_ID as varchar)=nmn.DCPS_NMN_RLT  ");
			 Strbld.append(" left outer join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)= substr(dcps.DDO_CODE,1,2) and loc.DEPARTMENT_ID=100003  ");
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
			sb.append("  SELECT cast(nvl(paybill.PAYBILL_MONTH,' ')as bigint), cast(nvl(mon.MONTH_NAME,' ')as varchar),cast(nvl(paybill.PAYBILL_YEAR,' ')as bigint) ,cast(nvl(mpg.VOUCHER_NO,' ')as bigint),cast(nvl(to_char(mpg.VOUCHER_DATE,'dd/MM/yyyy'),' ')as varchar) FROM HR_PAY_PAYBILL paybill ");
			sb.append(" inner join PAYBILL_HEAD_MPG mpg on mpg.paybill_id =paybill.paybill_grp_id  and mpg.approve_flag in (1,5) inner join SGVA_MONTH_MST mon on mon.MONTH_ID=paybill.PAYBILL_MONTH and mon.LANG_ID='en_US' inner join  ");
			sb.append("  (select temp.emp_id as emp1,temp.paybill_year as year1,max(paybill1.paybill_month) as month1  from HR_PAY_PAYBILL paybill1 inner join ");
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

	public String getNomineeDetials(String lookupId)
	{
		List resultList=null;
		String nomineeRlt= "";
		try
		{
			StringBuilder sb = new StringBuilder();
			SQLQuery selectQuery = null;
			sb.append("SELECT LOOKUP_NAME FROM CMN_LOOKUP_MST where LOOKUP_ID = "+lookupId);
			selectQuery = this.ghibSession.createSQLQuery(sb.toString());
			resultList = selectQuery.list();

			if(resultList.size() > 0){
				nomineeRlt = (String) resultList.get(0);
			}
		}
		catch(Exception e)
		{
			gLogger.info("Error occured in getContributionDetailsNew ---------"+ e);
			e.printStackTrace();
		}
		return nomineeRlt;
	}
	
}


