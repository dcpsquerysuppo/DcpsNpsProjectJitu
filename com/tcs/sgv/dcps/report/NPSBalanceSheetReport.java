package com.tcs.sgv.dcps.report;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.URLData;
import com.tcs.sgv.core.service.ServiceLocator;

public class NPSBalanceSheetReport  extends DefaultReportDataFinder 

{
	private static final Logger gLogger = Logger.getLogger(NPSBalanceSheetReport.class);
	Map requestAttributes = null;
	ServiceLocator serviceLocator = null;
	SessionFactory gObjSessionFactory = null;
	Session ghibSession = null;	
	
	public Collection findReportData(ReportVO report, Object criteria)
	throws ReportException 
	{

		ArrayList tabledata = new ArrayList();
		String langId = report.getLangId();
		String locId = report.getLocId();
		String lStrSevaarthId="";
		String lStrEmpName="";
		String lStrDcpsEmpId="";
		String lStrDcpsId="";
		String pranNo="";
		String lSevaarthId="";
		String lEmpName="";
		String lPranNo="";
		ArrayList td;
		ArrayList td1;
		ArrayList td2;
		List llisData = null;
		SessionFactory sessionFactory1=new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        //Session neardrSession = sessionFactory1.openSession();
		try
		{
			Map sessionKeys = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
			Map loginDetail = (HashMap) sessionKeys.get("loginDetailsMap");
			requestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
			serviceLocator = (ServiceLocator) requestAttributes.get("serviceLocator");
			gObjSessionFactory = serviceLocator.getSessionFactorySlave();
			ghibSession = gObjSessionFactory.getCurrentSession();
			Long locationId=0l;
			Long userId=0l;
			
			StyleVO[] rowsFontsVO = new StyleVO[4];
			rowsFontsVO[0] = new StyleVO();
			rowsFontsVO[0].setStyleId(IReportConstants.ROWS_PER_PAGE);
			rowsFontsVO[0].setStyleValue("26");
			rowsFontsVO[1] = new StyleVO();
			rowsFontsVO[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			rowsFontsVO[1].setStyleValue("10");
			rowsFontsVO[2] = new StyleVO();
			rowsFontsVO[2].setStyleId(IReportConstants.STYLE_FONT_FAMILY);
			rowsFontsVO[2].setStyleValue("Shruti");
			rowsFontsVO[3] = new StyleVO();
			rowsFontsVO[3].setStyleId(IReportConstants.BACKGROUNDCOLOR);
			rowsFontsVO[3].setStyleValue("white");
			
			StyleVO[] boldVO = new StyleVO[2];
			boldVO[0] = new StyleVO();
			boldVO[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			boldVO[0].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			boldVO[1] = new StyleVO();
			boldVO[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			boldVO[1].setStyleValue("10");
			
			if (report.getReportCode().equals("80000832")) 
			{
			if(report.getParameterValue("lStrSevaarthId")!=null && !"".equalsIgnoreCase(report.getParameterValue("lStrSevaarthId").toString()))
			{
				lSevaarthId = ((String) report.getParameterValue("lStrSevaarthId")).trim();
				gLogger.info("lStrSevaarthId +++++++++++++++++++++++++" + lSevaarthId);
			}
		
			if(report.getParameterValue("empName")!=null && !"".equalsIgnoreCase(report.getParameterValue("empName").toString()))
			{
				lEmpName = ((String) report.getParameterValue("empName"));
				gLogger.info("lStrEmpName" + lEmpName);

			}
			if(report.getParameterValue("pranNo")!=null && !"".equalsIgnoreCase(report.getParameterValue("pranNo").toString()))
			{
				lPranNo = (String) report.getParameterValue("pranNo");
				gLogger.info("pranNo" + lPranNo);
			}
			
			 llisData =getDcpsEmpId(lEmpName, lPranNo, lSevaarthId);
			
			 if(llisData != null && llisData.size() > 0)
				{
					Object[] lObjTier1 = (Object[]) llisData.get(0);
					if(lObjTier1[0] != null){
						lStrDcpsEmpId = lObjTier1[0].toString();
					}
					if(lObjTier1[1] != null){
						lStrDcpsId =lObjTier1[1].toString();
					}

				}
			 
			 String lStrDdoCode=getDdoCode(lEmpName, lPranNo, lSevaarthId); 

			List lLstOpeningBalcTier1 = null;
			List lFinList = null;
		
			if (loginDetail.get("locationId")!=null && !"".equalsIgnoreCase(loginDetail.get("locationId").toString()))
			{
				locationId=Long.parseLong(loginDetail.get("locationId").toString());
				
			}
			
		if (loginDetail.get("userId")!=null && !"".equalsIgnoreCase(loginDetail.get("userId").toString()))
			{
				userId =Long.parseLong(loginDetail.get("userId").toString());

			}  
			gLogger.info("user id is ###########################"+userId);  


			report.setStyleList(rowsFontsVO);
			report.initializeDynamicTreeModel();
			report.initializeTreeModel();
			String ddoCode="";
			String dcpsEmpId="";
			String treasuryCode="";

			
				Date d= new Date();
		        int currYearCode=0;
		        int currYear=d.getYear()+1900;
		        int currMonth=d.getMonth()+1;
		        if(currMonth >3)
		     	{
		        	currYearCode=currYear;
		     	}
		        else
		        {
		        	currYearCode=currYear-1;
		        }
		        String currStrFinYearId=getFinYearId(String.valueOf(currYearCode));

				int finYearId = Integer.parseInt(currStrFinYearId); 

					for (int i=20;i<=finYearId;i++)
					{
						String lYearId = null;
						String lFromDate = null;
						String lToDate = null;
						String lFinYearDesc = null;
						Double lTotalAmt=null;
						Double lTotalMissingamt=0d;
						Double lTier1Amount =0d;
						Double lTier2Amount =0d;
						Long lLngYearlyContriTier2 = 0l;
						List lLstDtlsTier2 = null;
						List lLstDtlsFinalTier1=null;
						List lLstDtlsFinalMissingTier1=null;
						List lLstOldContribution=null;
						List lLstoldContributionAndInterest=null;
						Object []lObj1 = null;
						Object []tupleSub1 = null;
						Long lLngOpeningTier2 = 0l;
						Object []lObj = null;
						Object []lObjTier1 = null;
						Double empIntrstT2 = 0d;
						Double empMissingIntrstT2 = 0d;
						Double  emplrIntrstT2 = 0d;
						Object []lObjMissingTier1 = null;
						Double  emplrMissingIntrstT2 = 0d;
						Long lLngTotalIntrstT2 = 0l;
						Double lOpeningEmpTier1 = 0d;
						Double lOpeningEmployrTier1 = 0d;
						List interestRates = null;
						Double lDoubleIntRate = null;
						Object[] lObjIntDtls = null;
						Double openEmployeeInt=0d; 
						Double  openEmployerInt=0d;
						Double openingEmpBal=0d; 
						Double openingEmpInt=0d;
						Double openingEmplyrBal=0d; 
						Double openingEmplyrInt=0d;
						Double totalEmployeeInt=0d;
						Double totalEmployerInt = 0d;
						Double closingBalEmp = 0d;
						Double closingBalEmpyr = 0d;
						Double totalEmpInt=0d;

						
						lFinList = lFinData(i);
						if(lFinList!=null && lFinList.size() >0)
						{
							
						Object[] tupleSub = (Object[]) lFinList.get(0);
						if (tupleSub[0] != null) 
						{
							lYearId=tupleSub[0].toString();
						}

						if (tupleSub[1] != null) 
						{
							lFromDate=tupleSub[1].toString();
						}

						if (tupleSub[2] != null) 
						{
							lToDate=tupleSub[2].toString();
						}	
						if (tupleSub[3] != null) 
						{
							lFinYearDesc=tupleSub[3].toString();
						}
						}
						
						if(i <25)
						{
							lLstOldContribution =oldOpeningContribution(lStrDcpsId, lYearId);
							
							
							if(lLstOldContribution!=null && lLstOldContribution.size() >0)
							{
								for (int j=0;j<lLstOldContribution.size();j++)
								{
									tupleSub1 = (Object[]) lLstOldContribution.get(j);

									if (tupleSub1[0] != null) 
									{
										openingEmpBal = Double.parseDouble( tupleSub1[0].toString());
									}
									if (tupleSub1[1] != null) 
									{
										openingEmplyrBal = Double.parseDouble( tupleSub1[1].toString());
									}
									if (tupleSub1[2] != null) 
									{
										totalEmpInt = Double.parseDouble( tupleSub1[2].toString());

									}
								}	
							}
							
							lLstoldContributionAndInterest =oldContributionAndInterest(lStrDcpsId, lYearId);

							
							if(lLstoldContributionAndInterest!=null && lLstoldContributionAndInterest.size() >0)
							{
								for (int j=0;j<lLstoldContributionAndInterest.size();j++)
								{
									tupleSub1 = (Object[]) lLstoldContributionAndInterest.get(j);

									if (tupleSub1[0] != null) 
									{
										lTier1Amount = Double.parseDouble( tupleSub1[0].toString());
									}

									if (tupleSub1[6] != null) 
									{
										totalEmployeeInt = Double.parseDouble( tupleSub1[6].toString());
									}
									if (tupleSub1[9] != null) 
									{
										totalEmployerInt = Double.parseDouble( tupleSub1[9].toString());
									}
									if (tupleSub1[5] != null) 
									{
										lTier2Amount =  Double.parseDouble(tupleSub1[5].toString());
									}
									if (tupleSub1[4] != null) 
									{
										closingBalEmp =  Double.parseDouble(tupleSub1[4].toString());
									}
									if (tupleSub1[1] != null) 
									{
										closingBalEmpyr =  Double.parseDouble(tupleSub1[1].toString());
									}
								}	
							}
				
						}
						if(i >=25)
						{
							lLstOpeningBalcTier1 = getOpeningBalanceTier1(lStrDcpsId, lYearId);
							lTotalMissingamt=getTotalMissingAmountDetails(Long.parseLong(lStrDcpsEmpId), lYearId, lFromDate, lToDate);
							lTotalAmt=getTotalAmountDetails(Long.parseLong(lStrDcpsEmpId), lYearId, lFromDate, lToDate);
							lTier1Amount = lTotalAmt+lTotalMissingamt;
							
							//for tier 2
							lLstDtlsTier2 = getTier2Details(lStrDcpsId, lYearId);
							lLstDtlsFinalTier1=getTier1FinalDetails(lStrDcpsId, lYearId);
							lLstDtlsFinalMissingTier1=getTier1FinalIntMissingDetails(lStrDcpsId, lYearId);

							//Details For Tier1 interest for employee and employer
							if(lLstDtlsFinalTier1 != null && lLstDtlsFinalTier1.size() > 0)
							{
								lObjTier1 = (Object[]) lLstDtlsFinalTier1.get(0);
								if(lObjTier1[0] != null){
									empIntrstT2 = Double.parseDouble(lObjTier1[0].toString());
								}

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

								lObj1 = (Object[]) lLstDtlsTier2.get(0);
								if(lObj1[0] != null){
									lLngOpeningTier2 = Math.round(Double.parseDouble(lObj1[0].toString()));
								}
								if(lObj1[1] != null){
									//lLngYearlyContriTier2 = Math.round(Double.parseDouble(lObj[1].toString()));
									lLngYearlyContriTier2 = Math.round(getYearlyArrAmountOfEmp(Long.parseLong(lStrDcpsEmpId),lYearId));
								}
								if(lObj1[3] != null){
									lLngTotalIntrstT2 = Math.round(Double.parseDouble(lObj1[3].toString()));
								}
								if(lObj1[6] != null){
									lOpeningEmpTier1  =Double.parseDouble(lObj1[6].toString());
								}
								if(lObj1[7] != null){
									lOpeningEmployrTier1 =Double.parseDouble( lObj1[7].toString());
								}
							}
							
							
							lTier2Amount = Double.parseDouble(lLngOpeningTier2.toString())+lLngYearlyContriTier2;
							totalEmployeeInt=Double.valueOf(lLngTotalIntrstT2.doubleValue() + empIntrstT2.doubleValue() +  empMissingIntrstT2.doubleValue());
							totalEmployerInt=Double.valueOf(emplrIntrstT2.doubleValue() + emplrMissingIntrstT2.doubleValue() );
							
							interestRates = getInterestRatesForGivenYear(lYearId);
						
							for(Integer lInt=0;lInt<interestRates.size();lInt++)
							{
								
								lObjIntDtls = (Object[]) interestRates.get(lInt);
								lDoubleIntRate = Double.valueOf(lObjIntDtls[0].toString().trim());
								lDoubleIntRate = Round(lDoubleIntRate,2);
						
								openEmployeeInt=openEmployeeInt+lDoubleIntRate * lOpeningEmpTier1  / 100;
								openEmployerInt=openEmployerInt+lDoubleIntRate * lOpeningEmployrTier1  / 100;
							}
							
							
							//closing balance
							Double totalContr=Double.valueOf(lTotalAmt.doubleValue() + lTotalMissingamt.doubleValue()+ lLngYearlyContriTier2.doubleValue());
							Double totalInt=Double.valueOf(lLngTotalIntrstT2.doubleValue() + empIntrstT2.doubleValue() +  empMissingIntrstT2.doubleValue());
							Double totalEmplrInt=Double.valueOf(emplrIntrstT2.doubleValue() + emplrMissingIntrstT2.doubleValue() );

							Double totalEmpAmt=Double.valueOf( lOpeningEmpTier1.doubleValue() + totalInt.doubleValue() + totalContr.doubleValue() );
							Double totalEmplyrAmt=Double.valueOf(lTotalAmt.doubleValue() +  lTotalMissingamt.doubleValue() + lOpeningEmployrTier1.doubleValue() + totalEmplrInt.doubleValue() ); 
							
							closingBalEmp = Round((totalEmpAmt),2)+Round((openEmployeeInt),2);
							closingBalEmpyr = Round((totalEmplyrAmt),2)+Round(openEmployerInt,2);

							openingEmpInt = Round((openEmployeeInt),2);
							openingEmplyrInt = Round(openEmployerInt,2);
							totalEmpInt = openingEmpInt+openingEmplyrInt;
		
							if(lLstOpeningBalcTier1!=null && lLstOpeningBalcTier1.size() >0)
							{
								for (int j=0;j<lLstOpeningBalcTier1.size();j++)
								{
									tupleSub1 = (Object[]) lLstOpeningBalcTier1.get(j);

									if (tupleSub1[0] != null) 
									{
										openingEmpBal = Double.parseDouble( tupleSub1[0].toString());
									}
									if (tupleSub1[1] != null) 
									{
										openingEmplyrBal = Double.parseDouble( tupleSub1[1].toString());
									}
								}	
							}
							
						}
						
								td = new ArrayList();
								td.add(lFinYearDesc);
								td.add("Employee Contribution:- ");
								td.add(openingEmpBal);
								if(openingEmpInt != null && openingEmpInt != 0 )
								{
									td.add(openingEmpInt);						
								}
								else
								{
									td.add("-");						

								}
								td.add(lTier1Amount);
								td.add(lTier2Amount);
								td.add(totalEmployeeInt);
								td.add(closingBalEmp);
						
								tabledata.add(td);
								td1 = new ArrayList();				
								td1.add(lFinYearDesc);
								td1.add("Employer Contribution:- ");
								td1.add(openingEmplyrBal);
								if(openingEmplyrInt != null && openingEmplyrInt != 0 )
								{
									td1.add(openingEmplyrInt);						
								}
								else
								{
									td1.add("-");						
								}
								td1.add(lTier1Amount);
								td1.add(lTier2Amount);
								td1.add(totalEmployerInt);
								td1.add(closingBalEmpyr);								
								tabledata.add(td1);
								
								td2 = new ArrayList();		
								td2.add(new StyledData(lFinYearDesc,boldVO));
								td2.add(new StyledData("Total :- ",boldVO));
								td2.add(new StyledData((openingEmpBal+openingEmplyrBal),boldVO));
								td2.add(new StyledData(totalEmpInt,boldVO));
								td2.add(new StyledData((lTier1Amount+lTier1Amount),boldVO));
								td2.add(new StyledData((lTier2Amount+lTier2Amount),boldVO));						
								td2.add(new StyledData((totalEmployeeInt+totalEmployerInt),boldVO));
								td2.add(new StyledData((closingBalEmp+closingBalEmpyr),boldVO));
								tabledata.add(td2);
		
					}		
			}
			if (report.getReportCode().equals("80000834")) 
			{
				gLogger.info("=====Innside child report latest 80000834=====:");
				if(report.getParameterValue("lStrSevaarthId")!=null && !"".equalsIgnoreCase(report.getParameterValue("lStrSevaarthId").toString()))
				{
					lStrSevaarthId = ((String) report
							.getParameterValue("lStrSevaarthId")).trim();
					gLogger.info("lStrSevaarthId" + lStrSevaarthId);
				}				
				

				List TableDataSub = null;
				StringBuilder SBQuerySub = new StringBuilder();

				SBQuerySub
				.append(" SELECT fin_year, cast(sum(EMP_CONTRI)as double ) , cast(sum(EMPLR_CONTRI)as double),cast(sum(EMP_INT)as double), cast(sum(EMPLR_INT)as double), cast(sum(cast(EMP_CONTRI as double)+cast(EMPlr_CONTRI as double)+cast(EMP_int as double)+cast(EMPLR_INT as double)) as double) FROM DCPS_LEGACY_DATA where ");
				
				if(lStrSevaarthId!=null && !"".equalsIgnoreCase(lStrSevaarthId))
				{
					SBQuerySub
					.append(" dcps_id = (select dcps_id from mst_dcps_emp where sevarth_id ='"); 
					SBQuerySub
					.append(lStrSevaarthId); 
					SBQuerySub
					.append("') "); 
							
				}
				
				if(lStrEmpName!=null && !"".equalsIgnoreCase(lStrEmpName))
				{

					SBQuerySub
					.append(" dcps_id = (select dcps_id from mst_dcps_emp where  upper(emp_name) like upper('%"); 
					SBQuerySub
					.append(lStrEmpName); 
					SBQuerySub
					.append("%')) "); 
							
				}
				if(pranNo!=null && !"".equalsIgnoreCase(pranNo))
				{
					SBQuerySub
					.append("  dcps_id = (select dcps_id from mst_dcps_emp where PRAN_NO='"); 
					SBQuerySub
					.append(pranNo); 
					SBQuerySub
					.append("') "); 
							
				}
				SBQuerySub
				.append("  group by fin_year "); 

				Session ghibSession = ServiceLocator.getServiceLocator()
						.getSessionFactorySlave().getCurrentSession();
				Query lQuerySub = ghibSession.createSQLQuery(SBQuerySub
						.toString());
				TableDataSub= lQuerySub.list();
				if(TableDataSub!=null && TableDataSub.size() >0)
				{
					for (int i=0;i<TableDataSub.size();i++)
					{
						Object[] tupleSub = (Object[]) TableDataSub.get(i);
						td = new ArrayList();
						if (tupleSub[0] != null) 
						{
								td.add(tupleSub[0]);
						}
						else
						{
								td.add("-");
						}
						if (tupleSub[1] != null) 
						{
								td.add(tupleSub[1]);
						}
						else
						{
								td.add("-");
						}
						if (tupleSub[2] != null) 
						{
								td.add(tupleSub[2]);
						}
						else
						{
								td.add("-");
						}
						
						if (tupleSub[3] != null) 
						{
								td.add(tupleSub[3]);
						}
						else
						{
								td.add("-");
						}
						if (tupleSub[4] != null) 
						{
								td.add(tupleSub[4]);
						}
						else
						{
								td.add("-");
						}
						if (tupleSub[5] != null) 
						{
								td.add(tupleSub[5]);
						}
						else
						{
								td.add("-");
						}
						
						tabledata.add(td);
					}
					
				
				}
				
			}
			
		}
		catch (Exception e) 
		{
			gLogger.info("=====findReportData(): Exception is=====" + e, e);
		}
		finally{
			gLogger.info("bf neardrSession.close();");
			//neardrSession.close();
			gLogger.info("af neardrSession.close();");
		}
		return tabledata;
	}
	
	
	public String getSevarthId(Long userId) {

		
		StringBuilder lSBQuery = new StringBuilder();
		String SevarthId=null;
		/////////////////
		
			lSBQuery.append("SELECT dcps.SEVARTH_ID FROM mst_dcps_emp dcps INNER JOIN org_emp_mst org ON dcps.ORG_EMP_MST_ID = org.EMP_ID where org.USER_ID ="+userId); 
		
			Session ghibSession = ServiceLocator.getServiceLocator()
			.getSessionFactorySlave().getCurrentSession();
			Query lQuery = ghibSession.createSQLQuery(lSBQuery
			.toString());
			SevarthId= lQuery.list().get(0).toString();
			
			return SevarthId;
	
		}
		
		
	public String getDdoCode(String lStrEmpName, String pranNo,String lStrSevaarthId) throws Exception {

		String ddoCode=null;

		List<Object> lLstAMBList = new ArrayList<Object>();
		try
		{
			Session lObjSession = serviceLocator.getServiceLocator().getSessionFactory().getCurrentSession();

			StringBuilder sblist = new StringBuilder();
			sblist.append("SELECT emp.DDO_CODE from MST_DCPS_EMP emp where 1=1  ");
			if(lStrEmpName!=null && !"".equalsIgnoreCase(lStrEmpName))
			{
				sblist
				.append(" and upper(emp.emp_name) like upper('%"); 
				sblist
				.append(lStrEmpName); 
				sblist
				.append("%') "); 
						
			}
			if(pranNo!=null && !"".equalsIgnoreCase(pranNo))
			{
				sblist
				.append(" and emp.PRAN_NO='"); 
				sblist
				.append(pranNo); 
				sblist
				.append("' "); 
							
			}
			if(lStrSevaarthId!=null && !"".equalsIgnoreCase(lStrSevaarthId))
			{
				sblist
				.append(" and emp.sevarth_id='"); 
				sblist
				.append(lStrSevaarthId); 
				sblist
				.append("' "); 
						
			}		 
			Query lQuery = lObjSession.createSQLQuery(sblist.toString());

			
		
			List lLstList = lQuery.list();
			

			if(lLstList!= null && !lLstList.isEmpty() && lLstList.size()!=0 && lLstList.get(0)!=null)
			{				 
				ddoCode=lLstList.get(0).toString();
			}
		}			 			 

		catch(Exception e){
			gLogger.error(" Error in getDdoCode method :////////////////////// " + e, e);
			throw e;
		}
		return ddoCode;

	}

	public List getDcpsEmpId(String lStrEmpName, String pranNo,String lStrSevaarthId) throws Exception {

		List lLstData=null;

		List<Object> lLstAMBList = new ArrayList<Object>();
		try
		{
			Session lObjSession = serviceLocator.getServiceLocator().getSessionFactory().getCurrentSession();

			StringBuilder sblist = new StringBuilder();
			sblist.append("SELECT emp.DCPS_EMP_ID , emp.dcps_id from MST_DCPS_EMP emp where 1=1  ");
			if(lStrEmpName!=null && !"".equalsIgnoreCase(lStrEmpName))
			{
				sblist
				.append(" and upper(emp.emp_name) like upper('%"); 
				sblist
				.append(lStrEmpName); 
				sblist
				.append("%') "); 
						
			}
			if(pranNo!=null && !"".equalsIgnoreCase(pranNo))
			{
				sblist
				.append(" and emp.PRAN_NO='"); 
				sblist
				.append(pranNo); 
				sblist
				.append("' "); 
						
			}
			gLogger.info(" Error in lStrSevaarthId /////////////////// " +lStrSevaarthId);

			if(lStrSevaarthId!=null && !"".equalsIgnoreCase(lStrSevaarthId))
			{
				sblist
				.append(" and emp.sevarth_id='"); 
				sblist
				.append(lStrSevaarthId); 
				sblist
				.append("' "); 
						
			}		 
			Query lQuery = lObjSession.createSQLQuery(sblist.toString());
			lLstData = lQuery.list();
		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}

		return lLstData;

	}
	
	
	
	
	public List getOpeningBalanceTier1(String lStrDcpsId, String lLngYearId)
	{
		List lLstData = null;
		SessionFactory sessionFactory1=new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        Session neardrSession = sessionFactory1.openSession();
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT OPEN_EMPLOYEE,OPEN_EMPLOYER,INT_CONTRB_EMPLOYEE ,INT_CONTRB_EMPLOYER ");
			lSBQuery.append("FROM MST_DCPS_CONTRIBUTION_YEARLY ");
			lSBQuery.append("WHERE YEAR_ID = :year  ");
			lSBQuery.append("AND dcps_id = :dcpsId  ");
			Query lQuery = neardrSession.createSQLQuery(lSBQuery.toString());
			lQuery.setString("dcpsId", lStrDcpsId);
			lQuery.setString("year", lLngYearId);

			lLstData = lQuery.list();
		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}finally{
			gLogger.info("bf neardrSession.close();");
			neardrSession.close();
			gLogger.info("af neardrSession.close();");
		}
		return lLstData;
	}
	
	
	public Double getTotalAmountDetails(Long lLngDcpsEmpId, String lLngYearId,String fromDate,String toDate)
	{
		List lLstData = null;
		Double amount=0d;
		SessionFactory sessionFactory1=new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        Session neardrSession = sessionFactory1.openSession();
		try{
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT nvl(SUM(CONTRIBUTION),0) FROM TRN_DCPS_CONTRIBUTION WHERE  REG_STATUS=1  AND (is_missing_credit is null and IS_CHALLAN is null)  AND  EMPLOYER_CONTRI_FLAG='Y' and    voucher_date between '"+fromDate+"' and '"+toDate+"' and ((MONTH_ID = 3 and FIN_YEAR_ID="+lLngYearId+"-1) or (MONTH_ID<>3 and FIN_YEAR_ID="+lLngYearId+")) AND DCPS_EMP_ID=:dcpsEmpId ");

			Query lQuery = neardrSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("dcpsEmpId", lLngDcpsEmpId);
			lLstData = lQuery.list();
			if(lLstData!=null)
			{
				amount=Double.parseDouble(lLstData.get(0).toString());
			}
		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}
		finally{
			gLogger.info("bf neardrSession.close();");
			neardrSession.close();
			gLogger.info("af neardrSession.close();");
		}
		return amount;
	}
	public Double getTotalMissingAmountDetails(Long lLngDcpsEmpId, String lLngYearId,String fromDate,String toDate)
	{
		List lLstData = null;
		Double amount=0d;
		SessionFactory sessionFactory1=new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        Session neardrSession = sessionFactory1.openSession();
		try{
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT sum(contri) FROM "); 
			lSBQuery.append(" (  ");
			lSBQuery.append(" SELECT nvl(sum(CONTRIBUTION),0) as contri FROM  TRN_DCPS_CONTRIBUTION where REG_STATUS=1 ");
			lSBQuery.append(" and MISSING_CREDIT_APPROVAL_DATE between '"+fromDate+"' and '"+toDate+"'  and EMPLOYER_CONTRI_FLAG='Y' and (is_missing_credit='Y' or IS_CHALLAN='Y')   and DCPS_EMP_ID=:dcpsEmpId ");

			lSBQuery.append(" UNION ");

			lSBQuery.append(" SELECT nvl(sum(CONTRIBUTION),0)  as contri FROM TRN_DCPS_CONTRIBUTION WHERE REG_STATUS=1 AND VOUCHER_DATE BETWEEN '"+fromDate+"' and '"+toDate+"'   and EMPLOYER_CONTRI_FLAG='Y'  and ");
			lSBQuery.append(" (is_missing_credit IS NULL and  IS_CHALLAN IS NULL) and  ((MONTH_ID <> 3 and FIN_YEAR_ID<>"+lLngYearId+") or (MONTH_ID=3 and FIN_YEAR_ID<>"+lLngYearId+"-1))  and DCPS_EMP_ID=:dcpsEmpId ");
			lSBQuery.append(" ) ");
			
			
			Query lQuery = neardrSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("dcpsEmpId", lLngDcpsEmpId);

			lLstData = lQuery.list();
			if(lLstData!=null)
			{
				amount=Double.parseDouble(lLstData.get(0).toString());
			}
		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}finally{
			gLogger.info("bf neardrSession.close();");
			neardrSession.close();
			gLogger.info("af neardrSession.close();");
		}


		return amount;
	}
	public List getTier1FinalDetails(String lStrDcpsId, String lLngYearId)
	{
		List lLstTier1FinalData = null;
		SessionFactory sessionFactory1=new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        Session neardrSession = sessionFactory1.openSession();
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT NVL(sum(totalEmpInt),0),NVL(sum(totalEmplrInt),0) FROM ");
			lSBQuery.append(" 	((SELECT NVL(INT_CONTRB_EMPLOYEE,0) as totalEmpInt,nvl(INT_CONTRB_EMPLOYER,0)  as totalEmplrInt  ");
			lSBQuery.append(" FROM MST_DCPS_CONTRIBUTION_MONTHLY where 1=1 ");			
			//	lSBQuery.append(" AND YEAR_ID = "+lLngYearId+" and PAY_MONTH <> 3) ");
			lSBQuery.append(" AND YEAR_ID = "+lLngYearId+" and (is_missing_credit is null and IS_CHALLAN is null)  and dcps_id =  :dcpsId ))");
			/*	lSBQuery.append(" union ");

			lSBQuery.append(" (SELECT NVL(INT_CONTRB_EMPLOYEE,0) as totalEmpInt,nvl(INT_CONTRB_EMPLOYER,0)  as totalEmplrInt ");
			lSBQuery.append(" FROM MST_DCPS_CONTRIBUTION_MONTHLY where dcps_id = (select dcps_id from mst_dcps_emp where dcps_Emp_id = :dcpsEmpId ) ");			
			lSBQuery.append(" AND YEAR_ID = :year and PAY_MONTH = 3)) ");*/

			Query lQuery = neardrSession.createSQLQuery(lSBQuery.toString());
			lQuery.setString("dcpsId", lStrDcpsId);
			//lQuery.setLong("year", lLngYearId-1);


			lLstTier1FinalData = lQuery.list();

		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}finally{
			gLogger.info("bf neardrSession.close();");
			neardrSession.close();
			gLogger.info("af neardrSession.close();");
		}


		return lLstTier1FinalData;
	}
	public Double getYearlyArrAmountOfEmp(Long lLngDcpsEmpId, String lLngYearId)/////////// for tier 2 to add  in Z
	{
		List lLstData = null;
		Double lDblArrAmount = 0d;
		SessionFactory sessionFactory1=new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        Session neardrSession = sessionFactory1.openSession();
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT YEARLY_AMOUNT FROM rlt_dcps_sixpc_yearly ");			
			lSBQuery.append(" where FIN_YEAR_ID = :year and STATUS_FLAG = 'A' and DCPS_EMP_ID = :dcpsEmpId ");
			Query lQuery = neardrSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("dcpsEmpId", lLngDcpsEmpId);
			lQuery.setString("year", lLngYearId);

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
		}finally{
			gLogger.info("bf neardrSession.close();");
			neardrSession.close();
			gLogger.info("af neardrSession.close();");
		}
		return lDblArrAmount;
	}

	public List getTier2Details(String lStrDcpsId, String lLngYearId)
	{
		List lLstData = null;
		SessionFactory sessionFactory1=new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        Session neardrSession = sessionFactory1.openSession();
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" select OPEN_TIER2,CONTRIB_TIER2,OPEN_TIER2+CONTRIB_TIER2,INT_CONTRB_TIER2 ,cast(INT_CONTRB_EMPLOYEE as decimal(16,2)) as INT_CONTRB_EMP,cast(INT_CONTRB_EMPLOYER as decimal(16,2)) as INT_CONTRB_EMPlr,OPEN_EMPLOYEE,OPEN_EMPLOYER,OPEN_INT FROM MST_DCPS_CONTRIBUTION_YEARLY ");
			lSBQuery.append(" WHERE YEAR_ID = :year ");			
			lSBQuery.append(" AND  dcps_id =  :dcpsId ");
			Query lQuery = neardrSession.createSQLQuery(lSBQuery.toString());
			lQuery.setString("dcpsId", lStrDcpsId);
			lQuery.setString("year", lLngYearId);

			lLstData = lQuery.list();		}catch (Exception e) {			
				gLogger.error("Error is : " + e, e);
			}finally{
				gLogger.info("bf neardrSession.close();");
				neardrSession.close();
				gLogger.info("af neardrSession.close();");
			}
			return lLstData;
	}
	public List getTier1FinalIntMissingDetails(String lStrDcpsId, String lLngYearId)
	{
		List lLstTier1MissingData = null;
		SessionFactory sessionFactory1=new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        Session neardrSession = sessionFactory1.openSession();
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT NVL(sum(totalEmpInt),0),NVL(sum(totalEmplrInt),0) FROM ");
			lSBQuery.append(" ((SELECT NVL(INT_CONTRB_EMPLOYEE,0) as totalEmpInt,nvl(INT_CONTRB_EMPLOYER,0)  as totalEmplrInt  ");
			lSBQuery.append(" FROM MST_DCPS_CONTRIBUTION_MONTHLY where 1=1 ");			
			lSBQuery.append(" AND YEAR_ID = "+lLngYearId+" AND (IS_MISSING_CREDIT='Y' or IS_CHALLAN='Y')  and dcps_id =  :dcpsId  ))");

			Query lQuery = neardrSession.createSQLQuery(lSBQuery.toString());
			lQuery.setString("dcpsId", lStrDcpsId);


			lLstTier1MissingData = lQuery.list();

		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}finally{
			gLogger.info("bf neardrSession.close();");
			neardrSession.close();
			gLogger.info("af neardrSession.close();");
		}
		return lLstTier1MissingData;
	}
	public List getInterestRatesForGivenYear(String finYearId) {

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

	public List lFinData(Integer currStrFinYearId)
	{
		List lLstData = null;
		SessionFactory sessionFactory1=new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        Session neardrSession = sessionFactory1.openSession();
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT FIN_YEAR_ID , FROM_DATE , TO_DATE , FIN_YEAR_DESC FROM SGVC_FIN_YEAR_MST fin where fin.FIN_YEAR_ID =  "+currStrFinYearId);
			Query lQuery = neardrSession.createSQLQuery(lSBQuery.toString());
			
			lLstData = lQuery.list();		
			}catch (Exception e) {			
				gLogger.error("Error is : " + e, e);
			}finally{
				gLogger.info("bf neardrSession.close();");
				neardrSession.close();
				gLogger.info("af neardrSession.close();");
			}
			 
			return lLstData;
	}
	public String getFinYearId(String finYearCode){

		List sev = null;
		String FinYearId = "";
		SessionFactory sessionFactory1=new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        Session neardrSession = sessionFactory1.openSession();
		try{
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append( " SELECT SFYM.FIN_YEAR_ID FROM SGVC_FIN_YEAR_MST SFYM " ); 
		lSBQuery.append( " WHERE SFYM.FIN_YEAR_CODE =:finYearCode " );

		Query lQuery = neardrSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("finYearCode",finYearCode);		


		sev = lQuery.list();

		if(!sev.isEmpty())
			FinYearId = (sev.get(0).toString());
		}
		catch(Exception e){
			gLogger.error("Exception in getFinYearId of LNALedgerQueryDAOImpl: " , e);
		}finally{
			gLogger.info("bf neardrSession.close();");
			neardrSession.close();
			gLogger.info("af neardrSession.close();");
		}

		return FinYearId;

	} 
	
	
	public List oldOpeningContribution(String lStrDcpsId, String lLngYearId)
	{
		List lLstData = null;
		SessionFactory sessionFactory1=new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        Session neardrSession = sessionFactory1.openSession();
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT temp.OPEN_EMPL_CONTRIB  as open_employee,temp.OPEN_EMPLR_CONTRIB as open_employer,temp.OPEN_INT as open_int , temp.TIER2 FROM TEMPEMPR3 temp");
			lSBQuery.append(" inner join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_DESC=temp.FIN_YEAR ");
			lSBQuery.append(" WHERE fin.FIN_YEAR_ID="+lLngYearId+" AND  temp.EMP_ID_NO ='"+lStrDcpsId+"' ");

			Query lQuery = neardrSession.createSQLQuery(lSBQuery.toString());
			
			lLstData = lQuery.list();		
			}catch (Exception e) {			
				gLogger.error("Error is : " + e, e);
			}finally{
				gLogger.info("bf neardrSession.close();");
				neardrSession.close();
				gLogger.info("af neardrSession.close();");
			}			 
			return lLstData;
	}
	
	public List oldContributionAndInterest(String lStrDcpsId, String lLngYearId)
	{
		List lLstData = null;
		SessionFactory sessionFactory1=new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        Session neardrSession = sessionFactory1.openSession();
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT cast(sum(temp.CUR_EMPL_CONTRIB) as DECIMAL(16,2)) as contrib_employee ,cast((tr3.OPEN_EMPLR_CONTRIB+sum(temp.CUR_EMPLR_CONTRIB)+tr3.INT_EMPL_CONTRIB) as DECIMAL(16,2))  as closeEmplrBal," +
					"cast((tr3.OPEN_EMPLR_CONTRIB+sum(temp.CUR_EMPLR_CONTRIB)+tr3.INT_EMPL_CONTRIB+tr3.INT_TIER2_CONTRIB+tr3.INT_EMPL_CONTRIB+tr3.OPEN_EMPL_CONTRIB+tr3.tier2 +tr3.OPEN_INT +sum(temp.CUR_EMPL_CONTRIB)) as DECIMAL(16,2)) as Closenet," +
					"cast(sum(temp.CUR_EMPLR_CONTRIB) as DECIMAL(16,2)) as contrib_employer, cast((tr3.INT_TIER2_CONTRIB+tr3.INT_EMPL_CONTRIB+tr3.OPEN_EMPL_CONTRIB+tr3.tier2 +sum(temp.CUR_EMPL_CONTRIB))as DECIMAL(16,2)) as  CloseBal," +
					" tr3.tier2 as tier2,cast((tr3.INT_TIER2_CONTRIB+tr3.INT_EMPL_CONTRIB) as DECIMAL(16,2)) as TotalEmpInt ,tr3.INT_TIER2_CONTRIB as INT_TIER2_CONTRIB,tr3.INT_EMPL_CONTRIB as int_contrb_employee,tr3.INT_EMPL_CONTRIB as int_contrb_employer,tr3.OPEN_INT as open_net  FROM TEMPR3 temp inner join TEMPEMPR3 tr3 on temp.emp_id_no=tr3.emp_id_no ");
			lSBQuery.append("    inner join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_DESC=temp.fin_year and fin.FIN_YEAR_DESC=tr3.fin_year   ");
			lSBQuery.append("  where fin.FIN_YEAR_ID="+lLngYearId+" and temp.EMP_ID_NO='"+lStrDcpsId+"' ");
			lSBQuery.append("  group by tr3.INT_EMPL_CONTRIB,tr3.INT_EMPL_CONTRIB,tr3.TIER2,tr3.INT_TIER2_CONTRIB,tr3.OPEN_EMPLR_CONTRIB,tr3.OPEN_EMPL_CONTRIB,tr3.OPEN_INT ");

			Query lQuery = neardrSession.createSQLQuery(lSBQuery.toString());
			
			lLstData = lQuery.list();		
			}catch (Exception e) {			
				gLogger.error("Error is : " + e, e);
			}finally{
				gLogger.info("bf neardrSession.close();");
				neardrSession.close();
				gLogger.info("af neardrSession.close();");
			}			 
			return lLstData;
	}
	
	
	public static double Round(double Rval, int Rpl) {
		double p = (double)Math.pow(10,Rpl);
		Rval = Rval * p;
		double tmp = Math.round(Rval);
		return (double)tmp/p;
	}
	
}
