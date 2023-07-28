package com.tcs.sgv.mdc.report;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.business.reports.ReportDataFinder;
import com.tcs.sgv.common.dao.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.DBConnection;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.PageBreak;
import com.tcs.sgv.common.valuebeans.reports.ReportAttributeVO;
import com.tcs.sgv.common.valuebeans.reports.ReportTemplate;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.core.service.ServiceLocator;


 public class DDOWiseReport  extends DefaultReportDataFinder implements ReportDataFinder {

	 private static Logger logger = Logger.getLogger(PostEmpCorrectionDtlsDAOImpl.class);

		private StyleVO[] selfCloseVO = null;
		private ResultSet lRs1 = null;
		Session ghibSession = null;
		public Collection findReportData(ReportVO report, Object criteria)throws ReportException {

			Connection lCon = null;
			Statement lStmt = null;

			Map requestKeys = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
			Map serviceMap = (Map) requestKeys.get("serviceMap");
			Map baseLoginMap = (Map) serviceMap.get("baseLoginMap");
			CmnLocationMst locationVO = (CmnLocationMst) baseLoginMap.get("locationVO");
			String locationName = locationVO.getLocName();
			long locationId = locationVO.getLocId();
			String locationNameincaps = locationName.toUpperCase();
			ServiceLocator serv1 = (ServiceLocator) requestKeys.get("serviceLocator");

		List DataList = new ArrayList();

			StyleVO[] baseFont = new StyleVO[1];
			baseFont[0] = new StyleVO();
			baseFont[0].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			baseFont[0].setStyleValue("13");

			ReportTemplate rt = new ReportTemplate();
			rt.put(IReportConstants.TMPLT_COLUMN_HEADER, baseFont);
			rt.put(IReportConstants.TMPLT_BASE_FONT, baseFont);
			report.setReportTemplate(rt);

			ReportAttributeVO ravo1 = new ReportAttributeVO();
			ravo1.setAttributeType(IReportConstants.ADDL_HEADER_LOCATION);
			ravo1.setAttributeType(IReportConstants.ADDL_HEADER_ON_EACH_PAGE);
			ravo1.setAlignment(IReportConstants.HEADER_ALIGN_CENTER);
			report.addReportAttributeItem(ravo1);
			report.setAdditionalHeader("");
			StyleVO[] styleVOPgBrk = null;
			styleVOPgBrk = new StyleVO[2];

			styleVOPgBrk[0] = new StyleVO();
			styleVOPgBrk[0].setStyleId(IReportConstants.PAGE_BREAK_BRFORE_SUBREPORT);
			styleVOPgBrk[0].setStyleValue("yes");
			styleVOPgBrk[0] = new StyleVO();

			styleVOPgBrk[1] = new StyleVO();
			styleVOPgBrk[1].setStyleId(IReportConstants.SHOW_REPORT_WHEN_NO_DATA);
			styleVOPgBrk[1].setStyleValue("yes");

			report.addReportStyles(styleVOPgBrk);
			logger.info("EmployeeLoanHistroyDAO111");
			
			
			StyleVO[] CenterAlignVO = new StyleVO[2];
			CenterAlignVO[0] = new StyleVO();
			CenterAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			CenterAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
			CenterAlignVO[1] = new StyleVO();
			CenterAlignVO[1].setStyleId(IReportConstants.BORDER);
			CenterAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			
			
			try {

				Map sessionKeys = (Map) ((Map) criteria)
						.get(IReportConstants.SESSION_KEYS);
				Map requestAttributes = (Map) ((Map) criteria)
						.get(IReportConstants.REQUEST_ATTRIBUTES);
				ServiceLocator serviceLocator = (ServiceLocator) requestAttributes
						.get("serviceLocator");
				SessionFactory sessionFactory = serviceLocator.getSessionFactory();
				Session session = sessionFactory.getCurrentSession();
				Session hibSession = sessionFactory.getCurrentSession();
				ServiceLocator serv = (ServiceLocator) requestKeys.get("serviceLocator");

				StyleVO[] boldStyleVO = new StyleVO[1];
				boldStyleVO[0] = new StyleVO();
				boldStyleVO[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
				boldStyleVO[0].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLDER);
				StyledData dataStyle = null;

				StyleVO[] colorStyleVO = new StyleVO[1];
				colorStyleVO[0] = new StyleVO();
				colorStyleVO[0].setStyleId(IReportConstants.STYLE_FONT_COLOR);
				colorStyleVO[0].setStyleValue("blue");
				selfCloseVO = new StyleVO[1];
				selfCloseVO[0] = new StyleVO();
				selfCloseVO[0].setStyleId(IReportConstants.REPORT_PAGE_OK_BTN_URL);
				selfCloseVO[0].setStyleValue("javascript:self.close()");

				lCon = (Connection) DBConnection.getConnection();
				lStmt = (Statement) lCon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,	ResultSet.CONCUR_UPDATABLE);

				StyleVO[] leftHeader = new StyleVO[3];
				leftHeader[0] = new StyleVO();
				leftHeader[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
				leftHeader[0].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
				leftHeader[1] = new StyleVO();
				leftHeader[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
				leftHeader[1].setStyleValue("10");
				leftHeader[2] = new StyleVO();
				leftHeader[2].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
				leftHeader[2]
						.setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_LEFT);

				StyleVO[] rightHead = new StyleVO[3];
				rightHead[0] = new StyleVO();
				rightHead[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
				rightHead[0].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
				rightHead[1] = new StyleVO();
				rightHead[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
				rightHead[1].setStyleValue("10");
				rightHead[2] = new StyleVO();
				rightHead[2].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
				rightHead[2]
						.setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);

				StyleVO[] headerStyleVo = new StyleVO[4];
				headerStyleVo[0] = new StyleVO();
				headerStyleVo[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
				headerStyleVo[0]
						.setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLDER);
				headerStyleVo[1] = new StyleVO();
				headerStyleVo[1].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
				headerStyleVo[1]
						.setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
				headerStyleVo[2] = new StyleVO();
				headerStyleVo[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
				headerStyleVo[2].setStyleValue("14");
				headerStyleVo[3] = new StyleVO();
				headerStyleVo[3].setStyleId(IReportConstants.STYLE_FONT_FAMILY);
				headerStyleVo[3]
						.setStyleValue(IReportConstants.VALUE_FONT_FAMILY_ARIAL);

				StyleVO[] centerboldStyleVO1 = new StyleVO[3];
				centerboldStyleVO1[0] = new StyleVO();
				centerboldStyleVO1[0]
						.setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
				centerboldStyleVO1[0]
						.setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLDER);
				centerboldStyleVO1[1] = new StyleVO();
				centerboldStyleVO1[1]
						.setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
				centerboldStyleVO1[1].setStyleValue("Center");
				centerboldStyleVO1[2] = new StyleVO();
				centerboldStyleVO1[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
				centerboldStyleVO1[2].setStyleValue("10");
	
	if (report.getReportCode().equals("700101")) {
		
		logger.info("ENTER in 700101 report is -----");
		
String Admin_ID;
String Field_name;
	Admin_ID = (String) report.getParameterValue("AdminName");

	Field_name = (String) report.getParameterValue("FieldName");


		logger.info("Location code is  ***********" + Admin_ID);

		logger.info("Location code is  ***********" + Field_name);
		
		
		 DemoReportDAO ObjeDDOWISE = new DemoReportDAOImpl(CmnLocationMst.class, serv.getSessionFactory());
		
		 List TOTAL_DDO_COUNT = new ArrayList();
		 
		 List FinalList = new ArrayList();
		 
		 String DDOCOUNT= null;
		 String DDO_Code= null;
		 String admin = null;
		 String Field = null;
		 String DDO_Office = null;
		  Integer ExEMP =0;
		  Integer ExEMP1 =0;
		  Integer TOTAL_ExEMP = 0;
		  Integer Final_TOTAL_ExEMP=0;
		  BigInteger Temp1 = null;
		  BigInteger Temp2 = null;
		  BigInteger TOTAL_Temp = null;
		 Long FInal_TOTAL_Temp = 0L; 
		 Integer i=0 ;
		 Integer j =0;
		 
		 Integer  TOtal_Positive =0;
		 Integer  TOtal_negative =0;
		 
		 Integer absolute_Negative = 0;
		 
		Integer Remaing_Emp = 0;
		 Integer Entered_Temp_data =0;
		 
		 Integer Total_of_diffrence= 0;
		 Integer grant_diffrence = 0;
		 Integer Total_of_absolute= 0;
			Integer abso_Total_of_diffrence = 0;		 
		 
		
		  Long ddocount =null;
		  ddocount =ObjeDDOWISE.getTotalNoOFDDOCount(Admin_ID, Field_name);
		  logger.info("DDO Count is  ***********" + 	ddocount);
		  
		  
		  Long Enterddocount = null;
		  Enterddocount =ObjeDDOWISE.getCountTotalNoOFDDOEnter(Admin_ID, Field_name);
		  
		  logger.info(" Enter DDO Count is  ***********" + 	Enterddocount+0);
		  
		  //List difference 		 ;
		 List ListCol = new ArrayList();
		 int cnt= 0;
		    ListCol = ObjeDDOWISE.getDDOInformation(Admin_ID,Field_name);
		    for (Object resKey : ListCol) {
		    	
		    	cnt++;
				 FinalList = new ArrayList();
				Object[] lObj = (Object[]) resKey;
					
				
					
				admin= (lObj[1]!=null)?lObj[1].toString():"NA";
				logger.info("admin name is  ***********" + 	admin); // admin name
				Field = (lObj[2]!=null)?lObj[2].toString():"NA";
				logger.info("Field name is  ***********" + 	Field); // Field Name
				
				DDO_Code= (lObj[4]!=null)?lObj[4].toString():"NA";
				logger.info("DDO code is  ***********" + 	DDO_Code); // DDo code

				DDO_Office= (lObj[6]!=null)?lObj[6].toString():"NA";
				logger.info("DDO Office is  ***********" + 	DDO_Office); // Dooc Office

				ExEMP= (lObj[7]!=null)?Integer.parseInt(lObj[7].toString()):new Integer(0);
				
				logger.info(" Existing employee is  ***********" + 	ExEMP); // existing emp
				
				ExEMP1= (lObj[8]!=null)?Integer.parseInt(lObj[8].toString()):new Integer(0);
				logger.info("Existing employee1 is  ***********" + 	ExEMP1+0); // existing emp 1
				
				TOTAL_ExEMP = ExEMP + ExEMP1;
				 
				// employee total
				
				logger.info("Existing employee1  TOTAL is  ***********" + 	TOTAL_ExEMP);
				
					Temp1 = (lObj[9]!=null)?new BigInteger(lObj[9].toString()):new BigInteger("0");
					logger.info("Entered employee1s is  ***********" + 	Temp1);
					
					// entered emp
					
					Temp2 = (lObj[10]!=null)?new BigInteger(lObj[10].toString()):new BigInteger("0");
					logger.info("Entered employee1s2 is  ***********" + 	Temp2);
					
					// entered emp 1
					
					TOTAL_Temp=Temp1.add(Temp2);	
					
					Entered_Temp_data =TOTAL_Temp.intValue();
					logger.info("ENTER EMPLYOEE TOTAL  is -----"+ TOTAL_Temp);
					
					//Entered employee total
					 
					 BigInteger bigIntValue = new BigInteger(Integer.toString(TOTAL_ExEMP));
					 
					 Integer Ex_EMP_Data = bigIntValue.intValue();
					 
					 Remaing_Emp=   Ex_EMP_Data - Entered_Temp_data;
					 logger.info("Remaing EMPLYOEE TOTAL  is -----"+ Remaing_Emp);
					
					 //Long mark = Remaing_Emp.longValue();
					
					
				/*	 if (Remaing_Emp<0)
					 {
						 i = Remaing_Emp;
						 
						 logger.info("REMAING VALUE IS " + i);
						   
						 }
					 else
					 {
					  	i= -1*Remaing_Emp;
					  	logger.info("REMAING VALUE IS " + i);
					 }
					 
					*/

				Final_TOTAL_ExEMP += (TOTAL_ExEMP!=null)?Integer.parseInt(TOTAL_ExEMP.toString()):new Integer(0);
						
						logger.info(" Final Existing employee1  TOTAL is  ***********" + 	Final_TOTAL_ExEMP);
					
												/// GrantTotal employee
	 
						
						FInal_TOTAL_Temp += (Entered_Temp_data!=null)?Integer.parseInt(Entered_Temp_data.toString()):new Integer(0);
					
						//logger.info("ENTER EMPLYOEE TOTAL  is -----"+ FInal_TOTAL_Temp);
						
						// GrantTotal of entered Employee
						
					Total_of_diffrence = (Remaing_Emp!=null)?Integer.parseInt(Remaing_Emp.toString()):new Integer(0);
						
				
					
					
					
					
				Integer absolute = Math.abs(Remaing_Emp);
					logger.info("Absolute value  is -----"+ absolute);
					
					
					 if (Remaing_Emp>0){
							i = Remaing_Emp;
							j = 0;
						}
						else{
							i= 0; 
							j = Remaing_Emp;
						}
					
					 absolute_Negative = Math.abs(i);
					 TOtal_Positive = TOtal_Positive + absolute_Negative;
					
					 abso_Total_of_diffrence =Math.abs(j);						
						grant_diffrence = grant_diffrence +abso_Total_of_diffrence; // negative
				
				// TOtal_Positive = TOtal_Positive +i;
					
				 Total_of_absolute =  Total_of_absolute + absolute;
					
				Long diffAmt= ddocount-Enterddocount;
				if(diffAmt<0)
					diffAmt=-1L*diffAmt;
				if (cnt>1){
				  FinalList.add(0, "");
				  FinalList.add(1, "");
				  FinalList.add(2,"");
				  FinalList.add(3,"");
				  FinalList.add(4,"");
				  FinalList.add(5,"");
				}
				else
				{
					FinalList.add(0,  cnt);
					  FinalList.add(1, admin);
					  FinalList.add(2, Field);
					  FinalList.add(3, ddocount);
					  FinalList.add(4,Enterddocount );
					  FinalList.add(5, diffAmt);
					
				}
				  
				  
				  
				  FinalList.add(6, DDO_Code);
				  FinalList.add(7, DDO_Office);
				  FinalList.add(8, TOTAL_ExEMP);
				  FinalList.add(9, TOTAL_Temp);
				  
				/*  if (Remaing_Emp>0){
						FinalList.add(10,"0");
						FinalList.add(11, Remaing_Emp);
					}
					else{
						FinalList.add(10, Remaing_Emp);
					FinalList.add(11, "0");
					
					}*/
				 
						FinalList.add(10,j);
					 FinalList.add(11,absolute);
					 FinalList.add(12,i);
				
					  
				  
		   			DataList.add(FinalList);
				  
		    }
		 

			ArrayList row2 = new ArrayList();

			StyledData dataStyle1 = new StyledData();
			dataStyle1 = new StyledData();
			dataStyle1.setStyles(boldStyleVO);
			dataStyle1.setData("TOTAL");
			row2.add("");
			row2.add("");
			row2.add("");
			row2.add("");
			row2.add("");
			row2.add("");
			row2.add("");
			row2.add(dataStyle1);

			dataStyle1 = new StyledData();
			dataStyle1.setStyles(boldStyleVO);
			dataStyle1.setData(Final_TOTAL_ExEMP); 
			row2.add(dataStyle1);
		
			StyledData dataStyle22 = new StyledData();
			dataStyle22 = new StyledData();
			dataStyle22.setStyles(boldStyleVO);
			dataStyle22.setData(FInal_TOTAL_Temp); 
			row2.add(dataStyle22);
			
			StyledData dataStyle3 = new StyledData();
			dataStyle3 = new StyledData();
			dataStyle3.setStyles(boldStyleVO);
			dataStyle3.setData(-1*grant_diffrence);  
			row2.add(dataStyle3);
			
			StyledData dataStyle5 = new StyledData();
			dataStyle5 = new StyledData();
			dataStyle5.setStyles(boldStyleVO);
			dataStyle5.setData(Total_of_absolute);  
			row2.add(dataStyle5);
			

			StyledData dataStyle51 = new StyledData();
			dataStyle51 = new StyledData();
			dataStyle51.setStyles(boldStyleVO);
			dataStyle51.setData(TOtal_Positive);  
			row2.add(dataStyle51);	

			
			
			

			DataList.add(row2);
	
		 
		 
		
		}
		} catch (Exception e) {
			logger.error("Error in Field DEPT Report " + e.getMessage());
			logger.error("Printing StackTrace");
			e.printStackTrace();
		}

		return DataList;

	}

	

	public List<ComboValuesVO> getAdminName(Hashtable otherArgs, String lStrLangId, String lStrLocId) {

		Hashtable sessionKeys = (Hashtable) (otherArgs).get(IReportConstants.SESSION_KEYS);
		Map loginMap = (Map) sessionKeys.get("loginDetailsMap");
		Long lLngLangId = null;
		if (loginMap.containsKey("langId")) {
			lLngLangId = (Long) loginMap.get("langId");
		}
		List<Object[]> lLstResult = new ArrayList<Object[]>();
		List<ComboValuesVO> lLstadmin = new ArrayList<ComboValuesVO>();
		ComboValuesVO lObjComboValueVO = null;
  
		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append(" SELECT locId,locName FROM CmnLocationMst where cmnLanguageMst.langId =:langId and departmentId = 100001 ");

		try {
			ghibSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("langId", lLngLangId);

			lLstResult = lQuery.list();

			if (lLstResult != null && lLstResult.size() > 0) {
				for (Object[] lArrObj : lLstResult) {
					lObjComboValueVO = new ComboValuesVO();
					lObjComboValueVO.setId(lArrObj[0].toString());
					lObjComboValueVO.setDesc(lArrObj[1].toString());
					lLstadmin.add(lObjComboValueVO);
				}
			}

		} catch (Exception e) {
			logger.error(" Error is in getAdmin methods###################### : " + e, e);
		}

		return lLstadmin;
	}
	
		public List<ComboValuesVO> getFieldName(String parentParamValue, String lStrLangId, String lStrLocId) {

		logger.error(" ENTER in getFieldName methods -------------------" );
	
		List<Object[]> lLstResult1 = new ArrayList<Object[]>();
		List<ComboValuesVO> lLstadmin12 = new ArrayList<ComboValuesVO>();
		ComboValuesVO lObjComboValueVO = null;
  
		String p_id =parentParamValue;
		StringBuilder lSBQuery = new StringBuilder();
		
		lSBQuery.append("  SELECT fie.loc_Id,fie.loc_Name FROM Cmn_Location_Mst fie ,cmn_Language_Mst lan where lan.lang_Id=1 and fie.parent_Loc_Id =:parentLocId1");

		
		try {	
			
			ghibSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			/*lQuery.setLong("langId", 1);*/
			lQuery.setString("parentLocId1",p_id);

			lLstResult1 = lQuery.list();
			if (lLstResult1 != null && lLstResult1.size() > 0) {
				for (Object[] lArrObj : lLstResult1) {
					lObjComboValueVO = new ComboValuesVO();
					lObjComboValueVO.setId(lArrObj[0].toString());
					lObjComboValueVO.setDesc(lArrObj[1].toString());
					lLstadmin12.add(lObjComboValueVO);
				}
			}
		
		else{		
			ComboValuesVO lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("-- Select --");
			lLstadmin12.add(lObjComboValuesVO);
		}
			} catch (Exception e) {
			logger.error(" Error is in getFieldName methods###################### : " + e, e);
		}

		return lLstadmin12;
	}
}
