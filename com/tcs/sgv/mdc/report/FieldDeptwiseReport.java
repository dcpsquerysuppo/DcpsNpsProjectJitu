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

public class FieldDeptwiseReport extends DefaultReportDataFinder implements
ReportDataFinder {

	private static Logger logger = Logger
	.getLogger(PostEmpCorrectionDtlsDAOImpl.class);

	private StyleVO[] selfCloseVO = null;
	private ResultSet lRs1 = null;
	Session ghibSession = null;

	public Collection findReportData(ReportVO report, Object criteria)
	throws ReportException {

		Connection lCon = null;
		Statement lStmt = null;

		Map requestKeys = (Map) ((Map) criteria)
		.get(IReportConstants.REQUEST_ATTRIBUTES);
		Map serviceMap = (Map) requestKeys.get("serviceMap");
		Map baseLoginMap = (Map) serviceMap.get("baseLoginMap");
		CmnLocationMst locationVO = (CmnLocationMst) baseLoginMap
		.get("locationVO");
		String locationName = locationVO.getLocName();
		long locationId = locationVO.getLocId();
		String locationNameincaps = locationName.toUpperCase();
		ServiceLocator serv1 = (ServiceLocator) requestKeys
		.get("serviceLocator");

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
		styleVOPgBrk[0]
		             .setStyleId(IReportConstants.PAGE_BREAK_BRFORE_SUBREPORT);
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
		CenterAlignVO[0]
		              .setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
		CenterAlignVO[1] = new StyleVO();
		CenterAlignVO[1].setStyleId(IReportConstants.BORDER);
		CenterAlignVO[1]
		              .setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

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
			ServiceLocator serv = (ServiceLocator) requestKeys
			.get("serviceLocator");

			StyleVO[] boldStyleVO = new StyleVO[1];
			boldStyleVO[0] = new StyleVO();
			boldStyleVO[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			boldStyleVO[0]
			            .setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLDER);
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
			lStmt = (Statement) lCon.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			StyleVO[] leftHeader = new StyleVO[3];
			leftHeader[0] = new StyleVO();
			leftHeader[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			leftHeader[0]
			           .setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
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

			if (report.getReportCode().equals("700099")) {
				StringBuffer lsb = new StringBuffer();
				String Admin_id;
				Admin_id = (String) report.getParameterValue("AdminName");
				logger.info("admin id " + Admin_id);
				logger.info("Location code is  ***********" + Admin_id);

				if (Admin_id != null && Admin_id.length() > 0)

				{
					FieldDeptReportDAOImpl Objpostempcorredtl = new FieldDeptReportDAOImpl(
							CmnLocationMst.class, serv.getSessionFactory());

					Long Fieldcount = 0L;
					Long TotalEnterFieldcount = 0L;
					String Fieldid =null;
					Objpostempcorredtl.setSessionFactory(serv
							.getSessionFactory());


					String 	 admin_name  = Objpostempcorredtl.getAdminID(Admin_id);

					Fieldcount = Objpostempcorredtl.getFieldCount(Admin_id);

					logger.info("Field Count is  ***********" + Fieldcount);

					TotalEnterFieldcount = (Long) Objpostempcorredtl
					.getEnteredFieldCount(Admin_id);

					logger.info("Enter Field  Count is  ***********"
							+ TotalEnterFieldcount);


					String Field_name = null;

					Long DDO_COUNT = 0l;
					Long EMPLOYEE_COUNT =0l;
					Long ENTERED_EMPLOYEE_COUNT= 0l;
					Long Entered_DDO_COUNT= 0l;
					Long DDO_Diffrence =0l; 
					Long Grant_DDO_Diffrence =0l; 
					Long Employee_Diffrence =0l; 
					Long Grant_DDO =0l;
					Long Grant_Employee =0l;	
					Long	Grant_Employee_Enter =0l;
					Long	Grant_DDO_Enter= 0l;
				  
				   Long TOtal_negative = 0l;
				   Long TOtal_positive = 0l;
				   Long absul_negative =0l;
				   Long absul_positive =0l;
				   Long grant_absolute =0l;
				  
					Long absolute =0l;
					Long xyz =0l;
					Long abc =0l;
					
					
					Long fll_id =0l;

					List<Object> DDO_FIELD_ID =null;

					List outerMap = new ArrayList();

					outerMap = Objpostempcorredtl.getSumOfDDOCount(Admin_id);

				//	List Field_ID = Objpostempcorredtl.getField_ID(Admin_id);

						List outerList = new ArrayList();				
					outerList = Objpostempcorredtl.getSumOfDDOCountEnter(Admin_id);

					if(outerList != null){
						int cnt=0;
						for(int i=0; i<outerMap.size();i++)
						{   
							

							List FinalList = new ArrayList();

						long inner_field_id =0l;
							for( int j =0; j<outerList.size();j++)
							{
								
								Object Obj[] = (Object[]) outerList.get(j);

								
								
								Entered_DDO_COUNT = (Long) ((Obj[0] != null) ? Long.parseLong(Obj[0].toString()) : "NA");

								ENTERED_EMPLOYEE_COUNT = (Long) ((Obj[1] != null) ? Long.parseLong(Obj[1].toString()) : "NA");

								inner_field_id = (Long) ((Obj[4] != null) ? Long.parseLong(Obj[4].toString()) : "NA");	


								//for outer list
								Object lObj[] = (Object[]) outerMap.get(i);

								Long admin_id =0l;
								admin_id = (Long) ((lObj[0] != null) ? Long.parseLong(lObj[0].toString()) : "NA");

								Field_name = (lObj[1] != null) ? lObj[1].toString() : "NA";

								DDO_COUNT = (Long) ((lObj[2] != null) ? Long
										.parseLong(lObj[2].toString()) : "NA");

								long outer_field_id =0l;
								outer_field_id = (Long) ((lObj[3] != null) ? Long.parseLong(lObj[3].toString()) : "NA");
								logger.info(" field id1 --------------------"+outer_field_id);

								EMPLOYEE_COUNT = (Long) ((lObj[4] != null) ? Long
										.parseLong(lObj[4].toString()) : "NA");


								Long admin_id1 =0l;
								String admin_name1;
								admin_name1 = (Obj[2] != null) ? lObj[2].toString() : "NA";
								String Field_name1;
								Field_name1 = (Obj[3] != null) ? lObj[3].toString() : "NA";



								logger.info("secound ffiel is =------------------"+inner_field_id);

								if(outer_field_id == inner_field_id)
								{
									cnt++;
									logger.info("outer "+outer_field_id);
									logger.info("innere  "+inner_field_id);

									
									if(i>0)
									{
										FinalList.add(0,"");															
										FinalList.add(1,"");							
										FinalList.add(2,"");
										FinalList.add(3,"");
									}
									else
									{
										FinalList.add(0,cnt);
										FinalList.add(1,admin_name);							
										FinalList.add(2, Fieldcount);
										FinalList.add(3, TotalEnterFieldcount);
									}
									
									
									
									/*FinalList.add(0, cnt);							
									FinalList.add(1,admin_name);							
									FinalList.add(2, Fieldcount);
									FinalList.add(3, TotalEnterFieldcount);		*/				
									FinalList.add(4, Field_name);
									FinalList.add(5, DDO_COUNT);

									Grant_DDO = Grant_DDO + DDO_COUNT;	
									FinalList.add(6, Entered_DDO_COUNT);
									
									Grant_DDO_Enter = Grant_DDO_Enter+ Entered_DDO_COUNT;
									
									DDO_Diffrence	 = DDO_COUNT - Entered_DDO_COUNT;
									
									FinalList.add(7, DDO_Diffrence);
									
									Grant_DDO_Diffrence =Grant_DDO_Diffrence+ DDO_Diffrence;

									FinalList.add(8, EMPLOYEE_COUNT);
									
									Grant_Employee	 =Grant_Employee + EMPLOYEE_COUNT;
									
									FinalList.add(9, ENTERED_EMPLOYEE_COUNT);
									
									Grant_Employee_Enter = Grant_Employee_Enter + Grant_Employee;
									
									Employee_Diffrence = 	EMPLOYEE_COUNT - ENTERED_EMPLOYEE_COUNT;
							
							
							 absolute = Math.abs(Employee_Diffrence);
							
							 grant_absolute = grant_absolute + absolute;
						
							 if (Employee_Diffrence>0){
								 xyz= Employee_Diffrence;
								 abc= 0L;
								}
								else{
									xyz= 0L; 
									abc = Employee_Diffrence;
								}
							 
							 absul_negative = Math.abs(abc);
							 absul_positive = Math.abs(xyz);
							
							 TOtal_negative =TOtal_negative + absul_negative;
							
							 TOtal_positive = TOtal_positive + absul_positive;
							 
									FinalList.add(10,abc);

									FinalList.add(11, absolute);
									FinalList.add(12,xyz);

									DataList.add(FinalList);;

								}

							}
						}

	/*String deptHeader = "This is DEMO REPORT";

					List styleList = new ArrayList();
					List stData = new ArrayList();
					StyledData styledHeader = new StyledData(deptHeader,
							headerStyleVo);
					styledHeader.setColspan(2);
					stData.add(styledHeader);
					styleList.add(stData);

					TabularData tData = new TabularData(styleList);
					tData.addStyle(IReportConstants.STYLE_FONT_ALIGNMENT,
							IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
					tData.addStyle(IReportConstants.BORDER, "No");
					tData.addStyle(IReportConstants.SHOW_REPORT_NAME,
							IReportConstants.VALUE_NO);
					report.setAdditionalHeader(tData);
				 */

							 ArrayList row2 = new ArrayList();

					StyledData dataStyle1 = new StyledData(); dataStyle1 =
					 new StyledData(); dataStyle1.setStyles(boldStyleVO);
					 dataStyle1.setData("TOTAL");
					 row2.add(""); 
					 row2.add(dataStyle1);

					 dataStyle1 = new StyledData();
					 dataStyle1.setStyles(boldStyleVO); 
					 dataStyle1.setData(Fieldcount);
					 row2.add(dataStyle1);

					 dataStyle1 = new StyledData();
					 dataStyle1.setStyles(boldStyleVO); 
					 dataStyle1.setData(TotalEnterFieldcount);
					 row2.add(dataStyle1);
					 row2.add(""); 

					 dataStyle1 = new StyledData();
					 dataStyle1.setStyles(boldStyleVO); 
					 dataStyle1.setData(Grant_DDO);
					 row2.add(dataStyle1);


					 dataStyle1 = new StyledData();
					 dataStyle1.setStyles(boldStyleVO); 
					 dataStyle1.setData(Grant_DDO_Enter);
					 row2.add(dataStyle1);


					 dataStyle1 = new StyledData();
					 dataStyle1.setStyles(boldStyleVO); 
					 dataStyle1.setData(Grant_DDO_Diffrence);
					 row2.add(dataStyle1);


					 dataStyle1 = new StyledData();
					 dataStyle1.setStyles(boldStyleVO); 
					 dataStyle1.setData(Grant_Employee);
					 row2.add(dataStyle1);



					 dataStyle1 = new StyledData();
					 dataStyle1.setStyles(boldStyleVO); 
					 dataStyle1.setData(Grant_Employee_Enter);
					 row2.add(dataStyle1);

					 dataStyle1 = new StyledData();
					 dataStyle1.setStyles(boldStyleVO); 
					 dataStyle1.setData(TOtal_negative);
					 row2.add(dataStyle1);

					 dataStyle1 = new StyledData();
					 dataStyle1.setStyles(boldStyleVO); 
					 dataStyle1.setData(grant_absolute);
					 row2.add(dataStyle1);

					 dataStyle1 = new StyledData();
					 dataStyle1.setStyles(boldStyleVO); 
					 dataStyle1.setData(TOtal_positive);
					 row2.add(dataStyle1);

					 DataList.add(row2);
				 

			}
		}
	}
		}
	catch (Exception e) {
		logger.error("Error in Field DEPT Report " + e.getMessage());
		logger.error("Printing StackTrace");
		e.printStackTrace();
	}

	return DataList;

}

public List<ComboValuesVO> getAdminName(Hashtable otherArgs,
		String lStrLangId, String lStrLocId) {

	Hashtable sessionKeys = (Hashtable) (otherArgs)
	.get(IReportConstants.SESSION_KEYS);
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
		ghibSession = ServiceLocator.getServiceLocator()
		.getSessionFactory().getCurrentSession();
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
		logger.error(
				" Error is in getDetails methods of field wise report ###################### : "
				+ e, e);
	}

	return lLstadmin;
}

}