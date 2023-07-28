package com.tcs.sgv.dcps.report;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.business.reports.ReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAO;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valuebeans.reports.PageBreak;
import com.tcs.sgv.common.valuebeans.reports.ReportColumnVO;
import com.tcs.sgv.common.valuebeans.reports.ReportTemplate;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.common.valueobject.OrgDdoMst;
import com.tcs.sgv.core.service.ServiceLocator;

import com.tcs.sgv.dcps.dao.EmpGPFDetailsDAOImpl;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;
import com.tcs.sgv.dcps.valueobject.DdoOffice;
import com.tcs.sgv.eis.dao.PayBillDAOImpl;

public class EmpGPFSeriesReport extends DefaultReportDataFinder implements
ReportDataFinder {
	private static final Logger logger = Logger
	.getLogger(EmpGPFSeriesReport.class);
	String Lang_Id = "en_US";
	String Loc_Id = "LC1";
	public static String newline = System.getProperty("line.separator");
	private ResourceBundle gObjRsrcBndle = ResourceBundle
	.getBundle("resources/dcps/BankCertificateLabels");
	Map requestAttributes = null;
	ServiceLocator serviceLocator = null;
	SessionFactory lObjSessionFactory = null;
	private  StyleVO[] selfCloseVO=null;          
	public Collection findReportData(ReportVO report, Object criteria)
	throws ReportException {



		Connection con = null;

		criteria.getClass();

		Statement smt = null;
		ResultSet rs = null;

		ArrayList dataList = new ArrayList();
		ArrayList tr = null;
		ArrayList td = null;		


		try {
			requestAttributes = (Map) ((Map) criteria)
			.get(IReportConstants.REQUEST_ATTRIBUTES);
			serviceLocator = (ServiceLocator) requestAttributes
			.get("serviceLocator");
			lObjSessionFactory = serviceLocator.getSessionFactorySlave();
			final Map serviceMap = (Map) requestAttributes.get("serviceMap");
			HttpServletRequest request = (HttpServletRequest) serviceMap.get("requestObj");
			/*Map requestAttributes = (Map) ((Map) criteria)
			.get(IReportConstants.REQUEST_ATTRIBUTES);
			ServiceLocator serviceLocator = (ServiceLocator) requestAttributes
			.get("serviceLocator");*/
			SessionFactory lObjSessionFactory = serviceLocator
			.getSessionFactorySlave();
			con = lObjSessionFactory.getCurrentSession().connection();
			smt = con.createStatement();

			Map sessionKeys = (Map) ((Map) criteria)
			.get(IReportConstants.SESSION_KEYS);
			Map loginDetail = (HashMap) sessionKeys.get("loginDetailsMap");

			Long locationId = (Long) loginDetail.get("locationId");


			Map lMapSessionAttributes = null;			
			LoginDetails lObjLoginVO = null;			
			Long gLngPostId = null;

			lMapSessionAttributes = (Map) ((Map) criteria)
			.get(IReportConstants.SESSION_KEYS);
			lObjLoginVO = (LoginDetails) lMapSessionAttributes
			.get("loginDetails");			
			gLngPostId = lObjLoginVO.getLoggedInPost().getPostId();

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null,
					serviceLocator.getSessionFactory());			

			String lStrDDOCode = lObjDcpsCommonDAO.getDdoCodeForDDO(gLngPostId);
			logger.info("gLngPostId "+gLngPostId);
			logger.info("lStrDDOCode "+lStrDDOCode);
			String lStrDdocode = null;
			OrgDdoMst lObjDdoMst = null;
			PayBillDAOImpl lObjBillDAO = new PayBillDAOImpl(null,serviceLocator.getSessionFactory());
			List<OrgDdoMst> lLstDDOList = lObjBillDAO.getDDOCodeByLoggedInlocId(locationId);
			if(!lLstDDOList.isEmpty() && lLstDDOList != null){
				lObjDdoMst = lLstDDOList.get(0);
				lStrDdocode = lObjDdoMst.getDdoCode();
			}		

			String reportCode = report.getReportCode(); 
			logger.info("lStrDdocode "+lStrDdocode);
			DdoOffice lObjDdoOffice = null;
			lObjDdoOffice = lObjDcpsCommonDAO.getDdoMainOffice(lStrDDOCode);

			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

			List list =new ArrayList();


			StyleVO[] leftHeader = new StyleVO[3];
			leftHeader[0] = new StyleVO();
			leftHeader[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			leftHeader[0].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD); 
			leftHeader[1] = new StyleVO();
			leftHeader[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			leftHeader[1].setStyleValue("11"); 
			leftHeader[2] = new StyleVO();
			leftHeader[2].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			leftHeader[2].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_LEFT);

			StyleVO[] rightHead = new StyleVO[3];
			rightHead[0] = new StyleVO();
			rightHead[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			rightHead[0].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD); 
			rightHead[1] = new StyleVO();
			rightHead[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			rightHead[1].setStyleValue("11"); 
			rightHead[2] = new StyleVO();
			rightHead[2].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			rightHead[2].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);


			StyleVO[] headerStyleVo = new StyleVO[4];
			headerStyleVo[0] = new StyleVO();
			headerStyleVo[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			headerStyleVo[0].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLDER);
			headerStyleVo[1] = new StyleVO();
			headerStyleVo[1].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			headerStyleVo[1].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
			headerStyleVo[2] = new StyleVO();
			headerStyleVo[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			headerStyleVo[2].setStyleValue("14");
			headerStyleVo[3] = new StyleVO();
			headerStyleVo[3].setStyleId(IReportConstants.STYLE_FONT_FAMILY);
			headerStyleVo[3].setStyleValue(IReportConstants.VALUE_FONT_FAMILY_ARIAL);

			StyleVO[] reportName = new StyleVO[4];
			reportName[0] = new StyleVO();
			reportName[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			reportName[0].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLDER);
			reportName[1] = new StyleVO();
			reportName[1].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			reportName[1].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
			reportName[2] = new StyleVO();
			reportName[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			reportName[2].setStyleValue("20");
			reportName[3] = new StyleVO();
			reportName[3].setStyleId(IReportConstants.STYLE_FONT_FAMILY);
			reportName[3].setStyleValue(IReportConstants.VALUE_FONT_FAMILY_ARIAL);
			EmpGPFDetailsDAOImpl lObjEmpGPFDetailsDAOImpl = new EmpGPFDetailsDAOImpl(null ,serviceLocator.getSessionFactory());    	


			String ddoName = "";
			String ddoOfcname = "";
			String user = request.getParameter("User");
			if(user!= null && user != "" && user.equals("MDC")){
				logger.info("in mdc "+user);
				lStrDdocode = request.getParameter("DDOCode");
				logger.info("in mdc lStrDdocode "+lStrDdocode);
				if(lStrDdocode != null && lStrDdocode != ""){
					lLstDDOList = lObjEmpGPFDetailsDAOImpl.getDDODetails(lStrDdocode);
					if(!lLstDDOList.isEmpty() && lLstDDOList != null){
						lObjDdoMst = lLstDDOList.get(0);
						lStrDdocode = lObjDdoMst.getDdoCode();
						ddoName = lObjDdoMst.getDdoName();
						ddoOfcname = lObjDdoMst.getDdoOffice();
					}
				}
			}
			else {
				lLstDDOList = lObjBillDAO.getDDOCodeByLoggedInlocId(locationId);
				if(!lLstDDOList.isEmpty() && lLstDDOList != null){
					lObjDdoMst = lLstDDOList.get(0);
					lStrDdocode = lObjDdoMst.getDdoCode();
					ddoName = lObjDdoMst.getDdoName();
					ddoOfcname = lObjDdoMst.getDdoOffice();
				}
			}

			ArrayList r3;
			ArrayList stData = new ArrayList();
			ArrayList styleList = new ArrayList();
			StyledData styledHeader = null;

			ArrayList headerRow= null;

			StyledData dataStyle =null;
			String name1 = "";
			if(reportCode.equals("8000068"))
				name1 = "GPF Series Report";
			if(reportCode.equals("8000069")) 
				name1 = "Bank Branch Report";
			if(reportCode.equals("8000071")) 
				name1 = "Prapatra 1";

			headerRow = new ArrayList();
			dataStyle = new StyledData();
			dataStyle.setStyles(reportName);
			dataStyle.setColspan(10);
			dataStyle.setData(name1);
			headerRow.add(dataStyle);
			styleList.add(headerRow);
			TabularData tData = null;
			if(!reportCode.equals("8000071")) {
				headerRow = new ArrayList();
				dataStyle = new StyledData();
				dataStyle.setStyles(headerStyleVo);
				dataStyle.setColspan(10);
				dataStyle.setData("DDO Code : "+lStrDdocode+" DDO :"+ddoName);
				headerRow.add(dataStyle);
				styleList.add(headerRow);

				headerRow = new ArrayList();
				dataStyle = new StyledData();
				dataStyle.setStyles(headerStyleVo);
				dataStyle.setColspan(10);
				dataStyle.setData("Office : "+ddoOfcname);
				
				headerRow.add(dataStyle);
				styleList.add(headerRow);

				tData = new TabularData(styleList);				
				tData.addStyle(IReportConstants.STYLE_FONT_ALIGNMENT, IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
				tData.addStyle(IReportConstants.BORDER, "No");
			}
			
			StyleVO[] style = new StyleVO[2];
			style[0] = new StyleVO();
			style[0].setStyleId(IReportConstants.ROWS_PER_PAGE);
			style[0].setStyleValue(10 + "");
			style[1] = new StyleVO();
			style[1].setStyleId(IReportConstants.REPORT_PAGE_OK_BTN_URL);
			style[1].setStyleValue("ifms.htm?actionFlag=getHomePage");
			
			report.setStyleList(style);
			report.setReportName("");

			if(reportCode.equals("8000068")){

				StringBuffer strBfr = new StringBuffer();
				strBfr.append(" select eis.SEVARTH_EMP_CD,org.EMP_FNAME||' '||org.emp_mname||' '||org.emp_lname, ");
				strBfr.append(" gpf.PF_SERIES,gpf.GPF_ACC_NO,");
				strBfr.append(" org.EMP_DOB, look.LOOKUP_NAME,gbu.NEW_ACCOUNT_NO,gbu.remarks from hr_Eis_emp_mst eis inner join org_emp_mst org on eis.EMP_MPG_ID = org.EMP_ID ");
				strBfr.append(" inner join ORG_USERPOST_RLT up on up.USER_ID = org.USER_ID and up.ACTIVATE_FLAG = 1 ");
				strBfr.append(" inner join ORG_POST_MST post on post.POST_ID = up.POST_ID and post.ACTIVATE_FLAG = 1 ");
				strBfr.append(" inner join ORG_POST_DETAILS_RLT rlt on post.POST_ID = rlt.POST_ID and rlt.LOC_ID = post.LOCATION_CODE ");
				strBfr.append(" inner join HR_PAY_POST_PSR_MPG psr on psr.POST_ID = post.POST_ID and psr.LOC_ID = post.LOCATION_CODE ");
				strBfr.append(" inner join HR_PAY_GPF_DETAILS gpf on gpf.USER_ID = up.USER_ID ");
				strBfr.append(" inner join org_ddo_mst ddo on ddo.LOCATION_CODE = post.LOCATION_CODE ");
				strBfr.append(" inner join HR_PAY_GPF_BIRTH_UPDATION gbu on org.EMP_ID = gbu.EMP_ID and gbu.DDO_CODE = :Ddocode");
				strBfr.append(" left outer join CMN_LOOKUP_MST look on look.LOOKUP_ID = gbu.NEW_GPF_SERIES ");
				strBfr.append(" where  gpf.PF_SERIES <> 'DCPS' " );
				strBfr.append(" and gbu.NEW_ACCOUNT_NO is not null or( gbu.NEW_GPF_SERIES <> '-1' and gbu.NEW_GPF_SERIES is not null ) ");
				strBfr.append(" order by  org.EMP_FNAME ");
				logger.info("strbfr ... "+strBfr);
				Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
				SQLQuery Query = ghibSession.createSQLQuery(strBfr.toString());
				Query.setParameter("Ddocode", lStrDdocode);

				list =Query.list();
				Long lLongSRNo = 1l;			

				if (list != null && !list.isEmpty()) {
					Iterator it = list.iterator();
					int size = 0;
					while (it.hasNext()) {				

						if(size%10 == 0)
							report.setAdditionalHeader(tData);
						size = size +1;
						Object[] tuple = (Object[]) it.next();

						td = new ArrayList();
						td.add(lLongSRNo);  // sr no.

						if(tuple[0]!= null){
							td.add(tuple[0]);
						} 
						else td.add("");      /// sevaarth id

						if(tuple[1]!= null){
							String name[] = tuple[1].toString().split(" ");
							String printName= "";
							int i =0;
							for( i =0; i < name.length -1; i++){
								printName = printName + name[i]+"\r\n";
							}
							printName =printName+ name[i];

							td.add(printName);
						}
						else td.add("");               ///emp name


						if(tuple[4]!= null){
							td.add(dateFormat.format((Date)tuple[4]));
						}
						else td.add("");          ///dob


						/*if(tuple[7]!= null){
							td.add(dateFormat.format((Date)tuple[7]));
						}
						else td.add("");       //new dob
						//td.add("");
						 */

						if(tuple[2]!= null){
							td.add(tuple[2]);
						}
						else td.add("");     //gpf series


						if(tuple[5]!= null){
							td.add(tuple[5]);
						}
						else td.add("");      //new gpf series
						//td.add("");


						if(tuple[3]!= null){
							td.add(tuple[3]);
						}
						else td.add("");         //gpf acct no.


						if(tuple[6]!= null){
							td.add(tuple[6]);
						}
						else td.add("");        //new gpf accnt no.

						if(tuple[7]!= null && !tuple[7].toString().equals("-1")){
							td.add(tuple[7]);
						}
						td.add("");        //emp sign

						td.add("");
						td.add("");


						lLongSRNo++;


						dataList.add(td);
						if(size%10 == 0)
						{
							ArrayList rowList = new ArrayList();
							rowList.add(new PageBreak());						
							dataList.add(rowList);
						}
					}
				}	
			}
			if(reportCode.equals("8000069")){


				StringBuffer strBfr = new StringBuffer();
				strBfr.append(" SELECT emp.EMP_NAME, emp.SEVARTH_ID,bank.BANK_NAME,brn.BRANCH_NAME,emp.IFSC_CODE,emp.BANK_ACNT_NO FROM ");
				strBfr.append(" MST_DCPS_EMP emp left outer join RLT_BANK_BRANCH_PAY brn on emp.BANK_NAME = brn.BANK_CODE ");
				strBfr.append("  and emp.BRANCH_NAME = brn.BRANCH_CODE ");
				strBfr.append(" inner outer join MST_BANK_PAY bank on bank.BANK_CODE = brn.BANK_CODE ");
				strBfr.append(" where emp.FORM_STATUS = 1 and emp.REG_STATUS in (1,2) ");
				strBfr.append(" and emp.ddo_code = :Ddocode  ");
				strBfr.append(" order by emp.EMP_NAME ");
				logger.info("strbfr ... "+strBfr);
				Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
				SQLQuery Query = ghibSession.createSQLQuery(strBfr.toString());
				Query.setParameter("Ddocode", lStrDdocode);

				list =Query.list();
				Long lLongSRNo = 1l;

				if (list != null && !list.isEmpty()) {
					Iterator it = list.iterator();
					int size = 0;
					while (it.hasNext()) {
						Object[] tuple = (Object[]) it.next();
						if(size%10 == 0)
							report.setAdditionalHeader(tData);
						size = size +1;
						td = new ArrayList();
						td.add(lLongSRNo); // SR No


						if(tuple[1]!= null){
							td.add(tuple[1]); // sevaarthId
						}
						else td.add("");

						if(tuple[0]!= null){
							String name[] = tuple[0].toString().split(" ");
							String printName= "";
							int i =0;
							for( i =0; i < name.length -1; i++){
								printName = printName + name[i]+"\r\n";
							}
							printName =printName+ name[i];
							logger.info("printName "+printName);
							td.add(printName);
						}
						else td.add("");  //name


						if(tuple[2]!= null){
							td.add(tuple[2]); // bank name
						}
						else td.add("");


						//td.add(""); // correct bank name


						if(tuple[3]!= null){
							td.add(tuple[3]); // branch name
						}
						else td.add("");

						//td.add("");  //correct branch name

						if(tuple[4]!= null){
							td.add(tuple[4]); // ifsc code
						}
						else td.add(""); 

						//td.add("");  //correct ifsc code

						if(tuple[5]!= null){
							td.add(tuple[5]); // accnt no
						}
						else td.add(""); 

						//td.add(""); // correct accnt no

						td.add(""); // emp sign

						td.add(""); // remarks

						lLongSRNo++;
						dataList.add(td);
						if(size%10 == 0)
						{
							ArrayList rowList = new ArrayList();
							rowList.add(new PageBreak());						
							dataList.add(rowList);
						}
					}
				}	
			}
			if(reportCode.equals("8000071")){

				String bankCode = "";
				String branchCode = "";
				bankCode =  request.getParameter("bankCode").trim();
				branchCode =  request.getParameter("branchCode").trim();
				StringBuffer strBfr = new StringBuffer();
				strBfr.append(" SELECT bank.BANK_NAME,branch.BRANCH_NAME,branch.ifsc_code FROM  ");
				strBfr.append(" mst_bank_PAy bank inner join RLT_BANK_BRANCH_PAY branch on cast(bank.BANK_CODE as bigint)= cast(branch.BANK_CODE as bigint) ");
				strBfr.append(" where cast(bank.BAnk_CODE as bigint) = cast("+bankCode+" as bigint) and cast(branch.BRANCH_id  as bigint)= cast("+branchCode +" as bigint)");
			
				logger.info("strbfr ... "+strBfr);
				Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
				SQLQuery Query = ghibSession.createSQLQuery(strBfr.toString());
				list =Query.list();
				
				if(list != null){
					Object[] obj = (Object[])list.get(0);
				headerRow = new ArrayList();
				
				dataStyle = new StyledData();
				dataStyle.setStyles(headerStyleVo);
				dataStyle.setColspan(10);
				dataStyle.setData("Bank Name : "+obj[0]);
				headerRow.add(dataStyle);
				styleList.add(headerRow);

				headerRow = new ArrayList();
				dataStyle = new StyledData();
				dataStyle.setStyles(headerStyleVo);
				dataStyle.setColspan(10);
				dataStyle.setData("Branch Name : "+obj[1]);
				headerRow.add(dataStyle);
				styleList.add(headerRow);
				
				headerRow = new ArrayList();
				dataStyle = new StyledData();
				dataStyle.setStyles(headerStyleVo);
				dataStyle.setColspan(10);
				dataStyle.setData("IFSC Code : "+obj[2]);
				headerRow.add(dataStyle);
				styleList.add(headerRow);
				
				headerRow = new ArrayList();
				dataStyle = new StyledData();
				dataStyle.setStyles(headerStyleVo);
				dataStyle.setColspan(10);
				dataStyle.setData("Correct IFSC Code(if the above code is incorrect) : _____________________");
				headerRow.add(dataStyle);
				styleList.add(headerRow);
				
				

				//TabularData tData ;

				tData = new TabularData(styleList);				
				tData.addStyle(IReportConstants.STYLE_FONT_ALIGNMENT, IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
				tData.addStyle(IReportConstants.BORDER, "No");
				report.setAdditionalHeader(tData);
				
				
				}
				strBfr = new StringBuffer();
				
				strBfr.append(" SELECT em.EMP_NAME,em.SEVARTH_ID,bank.BANK_NAME,branch.BRANCH_NAME ,em.IFSC_CODE,em.BANK_ACNT_NO,branch.bsr_code ");
				strBfr.append(" FROM mst_dcps_emp em left join MST_BANK_PAY bank on cast(bank.BANK_CODE as bigint) = cast(em.BANK_NAME as bigint)");
				strBfr.append("  left join RLT_BANK_BRANCH_PAY branch on cast(branch.BRANCH_ID as bigint) = cast(em.BRANCH_NAME as bigint) ");
				strBfr.append(" inner join ORG_EMP_MST emp on em.ORG_EMP_MST_ID = emp.emp_id ");
				strBfr.append(" inner join ORG_USERPOST_RLT usr on usr.user_id = emp.user_id and usr.activate_flag = 1 ");
				strBfr.append(" where em.ddo_code = :Ddocode and em.SERV_END_FLAG is null and em.FORM_STATUS = 1 and em.REG_STATUS in (1,2) ");
				strBfr.append(" and (em.SUPER_ANN_DATE > sysdate or em.SUPER_ANN_DATE is null) and cast(em.BANK_NAME as bigint) = cast("+bankCode +" as bigint) and cast (em.BRANCH_NAME as bigint) = cast("+branchCode+" as bigint)");
				strBfr.append(" order by em.EMP_NAME ");
				logger.info("strbfr ... "+strBfr);
				 ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
				 Query = ghibSession.createSQLQuery(strBfr.toString());
				Query.setParameter("Ddocode", lStrDdocode);

				list =Query.list();
				Long lLongSRNo = 1l;
				Object[] tuple = null;
				if (list != null && !list.isEmpty()) {
					Iterator it = list.iterator();
					int size = 0;
					while (it.hasNext()) {
						tuple = (Object[]) it.next();
						
						size = size +1;
						td = new ArrayList();
						td.add(lLongSRNo); // SR No


						if(tuple[1]!= null){
							td.add(tuple[1]); // sevaarthId
						}
						else td.add("");

						if(tuple[0]!= null){
							String name[] = tuple[0].toString().split(" ");
							String printName= "";
							int i =0;
							for( i =0; i < name.length -1; i++){
								printName = printName + name[i]+"\r\n";
							}
							printName =printName+ name[i];
							logger.info("printName "+printName);
							td.add(printName);
						}
						else td.add("");  //name
						//td.add("");  //correct ifsc code

						if(tuple[5]!= null){
							td.add(tuple[5]); // accnt no
						}
						else td.add(""); 
						
						td.add(""); //A/c no. corrected by empl
						
						td.add(""); // emp sign

						td.add(""); // remarks

						lLongSRNo++;
						dataList.add(td);
						if(size%10 == 0)
						{
							ArrayList rowList = new ArrayList();
							rowList.add(new PageBreak());						
							dataList.add(rowList);
						}
					}					
				}	

			}

		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception :" + e, e);
		} finally {
			try {
				if (smt != null) {
					smt.close();
				}

				if (rs != null) {
					rs.close();
				}

				if (con != null) {
					con.close();
				}

				smt = null;
				rs = null;
				con = null;

			} catch (Exception e1) {
				e1.printStackTrace();
				logger.error("Exception :" + e1, e1);

			}
		}
		return dataList;
	}
}
