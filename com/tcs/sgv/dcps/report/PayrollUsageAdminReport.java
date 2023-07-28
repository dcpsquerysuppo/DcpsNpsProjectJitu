package com.tcs.sgv.dcps.report;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.ibm.icu.text.SimpleDateFormat;
import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.business.reports.ReportDataFinder;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.DBConnection;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportAttributeVO;
import com.tcs.sgv.common.valuebeans.reports.ReportTemplate;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.service.AllIndiaServicesEmpServiceImpl;

public class PayrollUsageAdminReport extends DefaultReportDataFinder implements
		ReportDataFinder {
	private static Logger logger = Logger
			.getLogger(AllIndiaServicesEmpServiceImpl.class);

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

           SimpleDateFormat objsimpledatefrm = new SimpleDateFormat("dd/MM/yyyy");
           
           
			if (report.getReportCode().equals("700106")) {
				logger.info("in report 700106  ***********");
				
			/*String	adminid = report.getParameterValue("adminloc").toString();*/
				
				String	adminid = null;
				
				
				Date date = new Date();
				
				String strheader = report.getReportName();
				  
				strheader = strheader + "As On" + objsimpledatefrm.format(date) + "\r\n (Administrative Department Wise)"; 	
				
				report.setReportName(strheader);

				String srno = null;
				String budget = null;
				String adminname = null;
				
				int drawingddo = 0;
				int notdrowingddo = 0;
				int totalnoofddo = 0;

				StringBuilder lSBQuery = new StringBuilder();

				lSBQuery.append(" SELECT ocmn.LOC_ID,ocmn.LOC_NAME,(select count(DISTINCT ddo.DDO_CODE)  FROM  ORG_DDO_MST ddo ");
				lSBQuery.append("  inner join MST_DCPS_BILL_GROUP bgr on bgr.DDO_CODE = ddo.DDO_CODE ");
				lSBQuery.append(" inner join PAYBILL_HEAD_MPG phm on phm.LOC_ID = bgr.LOC_ID ");
				lSBQuery.append(" inner join CMN_LOCATION_MST cmn on  ddo.DEPT_LOC_CODE=cmn.LOC_ID ");
				lSBQuery.append(" where ddo.ACTIVATE_FLAG = 1  and ddo.DDO_CODE <> '1111222222' and cmn.LOC_ID =ocmn.LOC_ID  ");
				lSBQuery.append(" and phm.APPROVE_FLAG = 1) PAYBILL_DROWN, ");
				lSBQuery.append(" (select count(distinct(dd.ddo_code)) from PAYBILL_HEAD_MPG hm inner join ORG_DDO_MST dd on hm.loc_id=dd.location_code  ");
				lSBQuery.append("  where dd.DEPT_LOC_CODE=ocmn.LOC_ID) TOTAL_DDO_IN_IFMS  ");
				lSBQuery.append(" from  CMN_LOCATION_MST ocmn where ocmn.DEPARTMENT_ID =100001  group by ocmn.LOC_ID,ocmn.LOC_NAME order by ocmn.LOC_ID ");

				Session ghibSession = ServiceLocator.getServiceLocator()
						.getSessionFactorySlave().getCurrentSession();
				SQLQuery Query = ghibSession
						.createSQLQuery(lSBQuery.toString());

				logger.info("payrollusagescript  ***********" + lSBQuery.toString());
				List lLstFinal = Query.list();

				List dataListForTable = new ArrayList();

				if (lLstFinal != null && !lLstFinal.isEmpty()) {
					int count = 0;

					for (Iterator it = lLstFinal.iterator(); it.hasNext();)

					{
						count++;
						dataListForTable = new ArrayList();
						Object[] lObj = (Object[]) it.next();

						adminid = (lObj[0] != null) ? lObj[0].toString() : "NA";
						logger.info("Admin Id is  ***********" + adminname);

						adminname = (lObj[1] != null) ? lObj[1].toString()
								: "NA";
						logger.info("Admin name is  ***********" + adminname);

						drawingddo = (lObj[2] != null) ? Integer
								.parseInt(lObj[2].toString()) : new Integer(0);
						logger.info("Drawing DDO is  ***********" + drawingddo);

						notdrowingddo = (lObj[3] != null) ? Integer
								.parseInt(lObj[3].toString()) : new Integer(0);
						;
						logger.info(" Not Drawing DDO is  ***********"
								+ notdrowingddo);

						totalnoofddo = drawingddo - notdrowingddo;
						logger.info("total number of ddo  ***********"
								+ totalnoofddo);

					
						dataListForTable.add(count);
						
						if (adminid.equals("10001"))
						{ 
						dataListForTable.add("A");
						
						}else
							if (adminid.equals("10002"))
							{ 
							dataListForTable.add("B");
							
							}else
								if (adminid.equals("10003"))
								{ 
								dataListForTable.add("C");
								
								}else
									if (adminid.equals("10004"))
									{ 
									dataListForTable.add("D");
									
									}else
										if (adminid.equals("10005"))
										{ 
										dataListForTable.add("E");
										
										}else
											if (adminid.equals("10006"))
											{ 
											dataListForTable.add("F");
											
											}else
												if (adminid.equals("10007"))
												{ 
												dataListForTable.add("G");
												
												}else
													if (adminid.equals("10008"))
													{ 
													dataListForTable.add("H");
													
													}else
														if (adminid.equals("10009"))
														{ 
														dataListForTable.add("I");
														
														}else
															if (adminid.equals("10010"))
															{ 
															dataListForTable.add("J");
															
															}else
																if (adminid.equals("10011"))
																{ 
																dataListForTable.add("K");
																
																}else
																	if (adminid.equals("10012"))
																	{ 
																	dataListForTable.add("L");
																	
																	}else
																		if (adminid.equals("10013"))
																		{ 
																		dataListForTable.add("M");
																		
																		}else
																			if (adminid.equals("10014"))
																			{ 
																			dataListForTable.add("N");
																			
																			}else
																				if (adminid.equals("10015"))
																				{ 
																				dataListForTable.add("O");
																				
																				}
																				else
																					if (adminid.equals("10016"))
																					{ 
																					dataListForTable.add("P");
																					
																					}
																					else
																						if (adminid.equals("10017"))
																						{ 
																						dataListForTable.add("Q");
																						
																						}
																						else
																							if (adminid.equals("10018"))
																							{ 
																							dataListForTable.add("R");
																							
																							}
																							else
																								if (adminid.equals("10019"))
																								{ 
																								dataListForTable.add("S");
																								
																								}
																								else
																									if (adminid.equals("10020"))
																									{ 
																									dataListForTable.add("T");
																									
																									}
																									else
																										if (adminid.equals("10021"))
																										{ 
																										dataListForTable.add("U");
																										
																										}else
																											if (adminid.equals("10022"))
																											{ 
																											dataListForTable.add("V");
																											
																											}else
																												if (adminid.equals("10023"))
																												{ 
																												dataListForTable.add("W");
																												
																												}else
																													if (adminid.equals("10024"))
																													{ 
																													dataListForTable.add("X");
																													
																													}else
																														if (adminid.equals("10025"))
																														{ 
																														dataListForTable.add("Y");
																														
																														}else
																															if (adminid.equals("10026"))
																															{ 
																															dataListForTable.add("ZA");
																															
																															}else
																																if (adminid.equals("10027"))
																																{ 
																																dataListForTable.add("ZC");
																																
																																}else 					
																																	if (adminid.equals("10029"))
																																	{ 
																																	dataListForTable.add("ZD");
																																	
																																	}else 					
																																		if (adminid.equals("10030"))
																																		{ 
																																		dataListForTable.add("ZE");
																																		
																																		}else if (adminid.equals("10032"))
																																			{ 
																																				dataListForTable.add("ZF");
																																				
																																				}else			
											{ 
											dataListForTable.add(" ");
											
											}/*else
												if (adminid.equals("10001"))
												{ 
												dataListForTable.add("A");
												
												}else
													if (adminid.equals("10001"))
													{ 
													dataListForTable.add("A");
													
													}else
														if (adminid.equals("10001"))
														{ 
														dataListForTable.add("A");
														
														}*/
						
						dataListForTable.add(adminname);
						dataListForTable.add(drawingddo);
						dataListForTable.add(notdrowingddo);
						dataListForTable.add(totalnoofddo);
						DataList.add(dataListForTable);

					}
				}
			}

		} catch (Exception e) {
			logger.error("Error in Payroll Usages Admin Report " + e.getMessage());
			logger.error("Printing StackTrace");
			e.printStackTrace();
		}

		finally {
			try {
				if (lStmt != null) {
					lStmt.close();
				}

				
				if (lCon != null) {
					lCon.close();
				}				

			} catch (Exception e1) {
				e1.printStackTrace();
				logger.error("Exception :" + e1);

			}
		}

		return DataList;

	}
}
