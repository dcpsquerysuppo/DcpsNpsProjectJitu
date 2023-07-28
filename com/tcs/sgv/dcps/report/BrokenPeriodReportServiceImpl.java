package com.tcs.sgv.dcps.report;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.ParseConversionEvent;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.business.reports.ReportDataFinder;
import com.tcs.sgv.common.dao.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAO;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.util.DBConnection;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valuebeans.reports.ReportAttributeVO;
import com.tcs.sgv.common.valuebeans.reports.ReportColumnVO;
import com.tcs.sgv.common.valuebeans.reports.ReportTemplate;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.BrokenPeriodDAOImpl;
import com.tcs.sgv.dcps.dao.NewRegDdoDAO;
import com.tcs.sgv.dcps.dao.NewRegDdoDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.MstEmpNmn;
import com.tcs.sgv.dcps.valueobject.TrnDcpsContribution;
import com.tcs.sgv.eis.dao.PayBillDAOImpl;
import com.tcs.sgv.eis.valueobject.HrPayPaybill;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jfree.util.StringUtils;


public class BrokenPeriodReportServiceImpl extends ServiceImpl  {
	private final Log logger = LogFactory.getLog(getClass());

	private int page=0;
	private final int pageSize=5;
	Map requestAttributes = null;
	ServiceLocator serviceLocator = null;
	SessionFactory lObjSessionFactory = null;
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for LangId */
	Long gLngLangId = null;

	/* Global Variable for Location Code */
	String gStrLocCode = null;

	/* Global Variable for DB Id */
	Long gLngDBId = null;

	/* Global Variable for Current Date */
	Date gDtCurrDt = null;
	private void setSessionInfo(Map inputMap) {

		gLngLangId = SessionHelper.getLangId(inputMap);
		gLngPostId = SessionHelper.getPostId(inputMap);
		gLngUserId = SessionHelper.getUserId(inputMap);
		gStrLocCode = SessionHelper.getLocationCode(inputMap);
		gLngDBId = SessionHelper.getDbId(inputMap);
		gDtCurrDt = DBUtility.getCurrentDateFromDB();
	}

	public ResultObject brokenPeriodReport(Map objectArgs) throws ReportException {


		setSessionInfo(objectArgs);
		Statement smt = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;

		Connection con = null;
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		LinkedHashMap viewMap=new LinkedHashMap();
		LinkedHashMap netPayMap=new LinkedHashMap();

		try {

			requestAttributes = (Map)  objectArgs.get(IReportConstants.REQUEST_ATTRIBUTES);
			Map loginMap = (Map) objectArgs.get("baseLoginMap");
			Map requestAttributes = (Map) objectArgs.get(IReportConstants.REQUEST_ATTRIBUTES);
			ServiceLocator serviceLocator = (ServiceLocator) objectArgs.get("serviceLocator");
			SessionFactory lObjSessionFactory = serviceLocator.getSessionFactorySlave();
			con = lObjSessionFactory.getCurrentSession().connection();
			Session hibSession=lObjSessionFactory.getCurrentSession();
			smt = con.createStatement();
			/*Map sessionKeys = (Map)  objectArgs.get(IReportConstants.SESSION_KEYS);
			Map loginDetail = (HashMap) sessionKeys.get("loginDetailsMap");*/
			HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
			PayBillDAOImpl payDao = new PayBillDAOImpl(HrPayPaybill.class, serviceLocator.getSessionFactory());
			BrokenPeriodDAOImpl borkenDao= new BrokenPeriodDAOImpl(null, serviceLocator.getSessionFactory());
			
			//PayBillDAOImpl payDaoimpl = new PayBillDAOImpl(null, serviceLocator.getSessionFactory());
			//String empId = StringUtility.getParameter("empId", request);
			int month = Integer.parseInt(StringUtility.getParameter("Month", request).toString());
			int year = Integer.parseInt(StringUtility.getParameter("Year", request).toString());
			String billNo=StringUtility.getParameter("billNo", request);
			logger.info("billNo----"+billNo);
			String billType = borkenDao.getBillTypeOfbillGroup(billNo);
			logger.info("billType---"+billType);
			long langId = StringUtility.convertToLong(loginMap.get("langId").toString());
			logger.info("login map++++++++"+loginMap);
			StringBuffer sql = new StringBuffer();
			String StrSqlQuery = "";
			String StrSqlQuery1 = "";
			String StrSqlQuery2 = "";
			LinkedHashMap netMap=new LinkedHashMap();
			
			Calendar cal2 = Calendar.getInstance();
			cal2.set(Calendar.YEAR, year);
			cal2.set(Calendar.MONTH, month - 1);

			java.util.Date finYrDate = cal2.getTime();
			cal2 = null;

			Calendar calSupplBill = Calendar.getInstance();
			SgvcFinYearMst finYrMst = payDao.getFinYrInfo(finYrDate, langId);
			long yearId=finYrMst.getFinYearId();
			//long monthId=finYrMst.get;

			finYrDate = null;
			
			
			StringBuilder sb = new StringBuilder();
			sb.append(" SELECT mde.DDO_CODE as ddoCode,mbpp.broken_period_id as BrokenId,mde.EMP_NAME as Employee_Name,mde.SEVARTH_ID as Sevarth_Id,mbpp.MONTH_ID as Month,mbpp.YEAR_ID as Year,mbpp.FROM_DATE as From,mbpp.TO_DATE as To, ");
			sb.append(" mbpp.NO_OF_DAYS as NoOfDays,mbpp.BASIC_PAY as Basic_Pay,clm.LOOKUP_NAME as Reason,mbpp.REMARKS as Remarks,mbpp.NET_PAY as Net_Pay  FROM PAYBILL_HEAD_MPG   head ");
			sb.append(" inner join HR_PAY_PAYBILL hpp on hpp.PAYBILL_GRP_ID=head.PAYBILL_ID ");
			sb.append(" inner join MST_DCPS_BROKEN_PERIOD_PAY mbpp on mbpp.EIS_EMP_ID= hpp.EMP_ID ");
			sb.append(" inner join HR_EIS_EMP_MST eis on eis.emp_id=hpp.emp_id ");
			sb.append(" inner join MST_DCPS_EMP mde on mde.ORG_EMP_MST_ID=eis.emp_mpg_id");
			sb.append(" join CMN_LOOKUP_MST clm on clm.LOOKUP_ID=mbpp.REASON ");
			sb.append(" where head.PAYBILL_MONTH="+month+" and head.PAYBILL_YEAR="+year+" and head.BILL_NO="+billNo );
			sb.append(" and mbpp.MONTH_ID="+month+" and mbpp.YEAR_ID="+yearId+" and mbpp.TYPE_OF_BILL = '"+billType+"' and head.approve_flag in (0,1,5) and ");
			sb.append(" mbpp.created_date <= hpp.created_date");
			sb.append(" order by mde.DESIGNATION, hpp.EMP_LNAME ");
			
			
			/*sb.append("SELECT mbpp.broken_period_id as BrokenId,mde.EMP_NAME as Employee_Name,mde.SEVARTH_ID as Sevarth_Id,mbpp.MONTH_ID as Month,mbpp.YEAR_ID as Year,mbpp.FROM_DATE as From,mbpp.TO_DATE as To,");
			sb.append("mbpp.NO_OF_DAYS as NoOfDays,mbpp.BASIC_PAY as Basic_Pay,clm.LOOKUP_NAME as Reason,mbpp.REMARKS as Remarks,mbpp.NET_PAY as Net_Pay FROM MST_DCPS_BROKEN_PERIOD_PAY mbpp ");
			sb.append("join MST_DCPS_EMP mde on mde.ORG_EMP_MST_ID=mbpp.EIS_EMP_ID ");
			sb.append("join CMN_LOOKUP_MST clm on clm.LOOKUP_ID=mbpp.REASON ");
			sb.append("where mbpp.MONTH_ID="+month+" and mbpp.YEAR_ID="+year+" and mbpp.EIS_EMP_ID in ("+empId+")");
			sb.append("772000043344,772000037390,772000037262,772000043167,77200004730003,772000047229,772000047193,77200004250,");
			sb.append("772000048206,772000048208,77200004898,1355,77200004140,772000049160,772000049251,1359,77101992528,772000046417,");
			sb.append("1408,772000043301,77200004396,1409,1501,1500)");*/
			StrSqlQuery = sb.toString();

			logger.info("query******"+StrSqlQuery);
			rs = smt.executeQuery(StrSqlQuery);
			int totalCount=0;
			String brokenQuery=null;
			String ddoCode=null;
			while(rs.next()){
				
				
				if(viewMap.containsKey("Broken-Id")){
					LinkedHashMap bId=(LinkedHashMap) viewMap.get("Broken-Id");
					bId.put(rs.getString("BrokenId"),0);
					totalCount=bId.size();
					brokenQuery=brokenQuery+","+rs.getString("BrokenId");
					viewMap.put("Broken-Id",bId);
				}

				else
				{
					LinkedHashMap bId=new LinkedHashMap();
					bId.put(rs.getString("BrokenId"),0);
					brokenQuery=rs.getString("BrokenId");
					viewMap.put("Broken-Id",bId);

				}
				if(netPayMap.size()<1)
					netPayMap=initialize(viewMap);

				if(viewMap.containsKey("Sevarth-Id")){
					LinkedHashMap sId=(LinkedHashMap) viewMap.get("Sevarth-Id");
					sId.put(rs.getString("BrokenId"),rs.getString("Sevarth_Id"));
					viewMap.put("Sevarth-Id",sId);
				}

				else
				{
					LinkedHashMap sId=new LinkedHashMap();
					sId.put(rs.getString("BrokenId"),rs.getString("Sevarth_Id"));
					viewMap.put("Sevarth-Id",sId);

				}

				if(viewMap.containsKey("Employee-Name")){
					LinkedHashMap EmpName=(LinkedHashMap) viewMap.get("Employee-Name");
					EmpName.put(rs.getString("BrokenId"),rs.getString("Employee_Name"));
					viewMap.put("Employee-Name",EmpName);
				}

				else
				{
					LinkedHashMap EmpName=new LinkedHashMap();
					EmpName.put(rs.getString("BrokenId"),rs.getString("Employee_Name"));
					viewMap.put("Employee-Name",EmpName);

				}


				if(viewMap.containsKey("From-Date")){
					LinkedHashMap from=(LinkedHashMap) viewMap.get("From-Date");
					from.put(rs.getString("BrokenId"),rs.getString("From").substring(0, 10));
					viewMap.put("From-Date",from);
				}

				else
				{
					LinkedHashMap from=new LinkedHashMap();
					from.put(rs.getString("BrokenId"),rs.getString("From").substring(0, 10));
					viewMap.put("From-Date",from);

				}


				if(viewMap.containsKey("To-Date")){
					LinkedHashMap to=(LinkedHashMap) viewMap.get("To-Date");
					to.put(rs.getString("BrokenId"),rs.getString("To").substring(0, 10));
					viewMap.put("To-Date",to);
				}

				else
				{
					LinkedHashMap from=new LinkedHashMap();
					from.put(rs.getString("BrokenId"),rs.getString("To").substring(0, 10));
					viewMap.put("To-Date",from);

				}


				if(viewMap.containsKey("No.Of Days")){
					LinkedHashMap Number=(LinkedHashMap) viewMap.get("No.Of Days");
					Number.put(rs.getString("BrokenId"),rs.getString("NoOfDays"));
					viewMap.put("No.Of Days",Number);
				}

				else
				{
					LinkedHashMap Number=new LinkedHashMap();
					Number.put(rs.getString("BrokenId"),rs.getString("NoOfDays"));
					viewMap.put("No.Of Days",Number);

				}


				if(viewMap.containsKey("Basic-Pay")){
					LinkedHashMap basic=(LinkedHashMap) viewMap.get("Basic-Pay");
					basic.put(rs.getString("BrokenId"),rs.getString("Basic_Pay"));
					viewMap.put("Basic-Pay",basic);
				}

				else
				{
					LinkedHashMap basic=new LinkedHashMap();
					basic.put(rs.getString("BrokenId"),rs.getString("Basic_Pay"));
					viewMap.put("Basic-Pay",basic);

				}

				if(viewMap.containsKey("Reason")){
					LinkedHashMap reason=(LinkedHashMap) viewMap.get("Reason");
					reason.put(rs.getString("BrokenId"),rs.getString("Reason"));
					viewMap.put("Reason",reason);
				}

				else
				{
					LinkedHashMap reason=new LinkedHashMap();
					reason.put(rs.getString("BrokenId"),rs.getString("Reason"));
					viewMap.put("Reason",reason);

				}

				if(viewMap.containsKey("Remarks")){
					LinkedHashMap remarks=(LinkedHashMap) viewMap.get("Remarks");
					remarks.put(rs.getString("BrokenId"),rs.getString("Remarks"));
					viewMap.put("Remarks",remarks);
				}

				else
				{
					LinkedHashMap remarks=new LinkedHashMap();
					remarks.put(rs.getString("BrokenId"),rs.getString("Remarks"));
					viewMap.put("Remarks",remarks);

				}


				//	LinkedHashMap netPay=(LinkedHashMap) netMap.get("Net-Pay");
				netPayMap.put(rs.getString("BrokenId"),rs.getString("Net_Pay"));
				//.put("Net-Pay",netPay);

			}

			StringBuilder sb1 = new StringBuilder();
			sb1.append("SELECT patm.ALLOW_NAME as allowName,dbpa.ALLOW_VALUE as allowValue,dbpa.RLT_BROKEN_PERIOD_ID as BrokenId FROM RLT_DCPS_BROKEN_PERIOD_ALLOW dbpa ");
			sb1.append("join HR_PAY_ALLOW_TYPE_MST patm on dbpa.ALLOW_CODE=patm.ALLOW_CODE where dbpa.RLT_BROKEN_PERIOD_ID in ("+brokenQuery+")");

			StrSqlQuery1 = sb1.toString();

			logger.info("query******"+StrSqlQuery1);
			rs1 = smt.executeQuery(StrSqlQuery1);

			while(rs1.next()){

				//for blank row
				if(viewMap.containsKey("Allowances")){
					LinkedHashMap blank=(LinkedHashMap) viewMap.get("Allowances");
					blank.put(rs1.getString("BrokenId"),0);
					viewMap.put("Allowances",blank);
				}

				else
				{
					LinkedHashMap blank=new LinkedHashMap();
					blank.put(rs1.getString("BrokenId"),0);
					viewMap.put("Allowances",blank);

				}


				if(viewMap.containsKey(rs1.getString("allowName"))){
					LinkedHashMap allow=(LinkedHashMap) viewMap.get(rs1.getString("allowName"));
					allow.put(rs1.getString("BrokenId"),rs1.getString("allowValue"));
					viewMap.put(rs1.getString("allowName"),allow);

				}
				else
				{
					LinkedHashMap allow=initialize(viewMap);
					allow.put(rs1.getString("BrokenId"),rs1.getString("allowValue"));
					viewMap.put(rs1.getString("allowName"),allow);
				}



			}

			StringBuilder sb2 = new StringBuilder();
			sb2.append("SELECT pdtm.DEDUC_NAME as deductName,dbpd.DEDUC_VALUE as deductValue,dbpd.RLT_BROKEN_PERIOD_ID as BrokenId FROM RLT_DCPS_BROKEN_PERIOD_DEDUC dbpd ");
			sb2.append("join HR_PAY_DEDUC_TYPE_MST pdtm on dbpd.DEDUC_CODE=pdtm.DEDUC_CODE where dbpd.RLT_BROKEN_PERIOD_ID in ("+brokenQuery+")");

			StrSqlQuery2 = sb2.toString();

			logger.info("query******"+StrSqlQuery2);
			rs2 = smt.executeQuery(StrSqlQuery2);

			while(rs2.next()){
				//for blank row
				if(viewMap.containsKey("Deductions")){
					LinkedHashMap blank=(LinkedHashMap) viewMap.get("Deductions");
					blank.put(rs2.getString("BrokenId"),0);
					viewMap.put("Deductions",blank);
				}

				else
				{
					LinkedHashMap blank=new LinkedHashMap();
					blank.put(rs2.getString("BrokenId"),0);
					viewMap.put("Deductions",blank);

				}


				if(viewMap.containsKey(rs2.getString("deductName"))){
					LinkedHashMap allow=(LinkedHashMap) viewMap.get(rs2.getString("deductName"));
					allow.put(rs2.getString("BrokenId"),rs2.getString("deductValue"));
					viewMap.put(rs2.getString("deductName"),allow);

				}
				else
				{
					LinkedHashMap allow=initialize(viewMap);
					allow.put(rs2.getString("BrokenId"),rs2.getString("deductValue"));
					viewMap.put(rs2.getString("deductName"),allow);
				}


			}
			if(viewMap.containsKey("Blank")){
				LinkedHashMap blank=(LinkedHashMap) viewMap.get("Blank");
				blank.put("space",0);
				viewMap.put("Blank",blank);
			}

			else
			{
				LinkedHashMap blank=new LinkedHashMap();
				blank.put("space",0);
				viewMap.put("Blank",blank);

			}

			viewMap.put("Net-Pay",netPayMap);

			if(totalCount>5){
			page=totalCount/5;
			}else
				page=1;

			logger.info("result*********"+viewMap+"/n size----"+totalCount+"pagecount-----"+page);
			objectArgs.put("viewMap",viewMap);
			objectArgs.put("pageSize",pageSize);
			objectArgs.put("page",page);
			objectArgs.put("yearFtr",year);
			
			String[] monthName = { "January", "February", "March", "April", "May",
					"June", "July", "August", "September", "October", "November",
					"December" };

			int MONTH = (int) month;
			String MONTHNAME = monthName[MONTH - 1];
			
			objectArgs.put("monthFtr",MONTHNAME);
			
			
			String locId=loginMap.get("locationId").toString();
			List list = new ArrayList();
			Object[] listObj=null;
			String offcName=null;
			StringBuffer query = new StringBuffer();
			query.append("SELECT off_name,ddo_code FROM mst_dcps_ddo_office where LOC_ID=" + locId + " and DDO_OFFICE='Yes'  ");

			Query hsqlQuery = hibSession.createSQLQuery(query.toString());
			list = hsqlQuery.list();

			if (list != null && list.size() > 0){
				listObj = (Object[]) list.get(0);
			offcName=listObj[0].toString();
			ddoCode=listObj[1].toString();
			}
			logger.info("ddocode--------"+ddoCode+"officeName-------"+offcName);
			objectArgs.put("officeName",offcName);
			objectArgs.put("ddoCode",ddoCode);
			resObj.setResultValue(objectArgs);
			resObj.setViewName("BrokenPeriodReport");
		} catch (Exception e) {
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
		return resObj;
	}
	public ResultObject getBrokenEmployeeCount(Map objectArgs) throws Exception
	{
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		
		setSessionInfo(objectArgs);
//		Statement smt = null;
	

//		Connection con = null;
		


			requestAttributes = (Map)  objectArgs.get(IReportConstants.REQUEST_ATTRIBUTES);
			Map loginMap = (Map) objectArgs.get("baseLoginMap");
			Map requestAttributes = (Map) objectArgs.get(IReportConstants.REQUEST_ATTRIBUTES);
			ServiceLocator serviceLocator = (ServiceLocator) objectArgs.get("serviceLocator");
			SessionFactory lObjSessionFactory = serviceLocator.getSessionFactorySlave();
			Session hiSession = lObjSessionFactory.getCurrentSession();
			//con = lObjSessionFactory.getCurrentSession().connection();
			//smt = con.createStatement();
	
			HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
			PayBillDAOImpl payDao = new PayBillDAOImpl(HrPayPaybill.class, serviceLocator.getSessionFactory());
		
			int month = Integer.parseInt(StringUtility.getParameter("Month", request).toString());
			int year = Integer.parseInt(StringUtility.getParameter("Year", request).toString());
			String billNo=StringUtility.getParameter("billNo", request);
			long langId = StringUtility.convertToLong(loginMap.get("langId").toString());
		
		
			
			Calendar cal2 = Calendar.getInstance();
			cal2.set(Calendar.YEAR, year);
			cal2.set(Calendar.MONTH, month - 1);

			java.util.Date finYrDate = cal2.getTime();
			cal2 = null;

			Calendar calSupplBill = Calendar.getInstance();
			SgvcFinYearMst finYrMst = payDao.getFinYrInfo(finYrDate, langId);
			long yearId=finYrMst.getFinYearId();
			//long monthId=finYrMst.get;

			finYrDate = null;
		
		
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT count(*) FROM PAYBILL_HEAD_MPG head,HR_PAY_PAYBILL pay,MST_DCPS_BROKEN_PERIOD_PAY broken ");
		sb.append("where pay.PAYBILL_GRP_ID=head.PAYBILL_ID ");
		sb.append("and broken.EIS_EMP_ID=pay.emp_ID and head.APPROVE_FLAG in (0,1,5) ");
		sb.append("and broken.MONTH_ID="+month+" and broken.YEAR_ID="+yearId+" and head.PAYBILL_YEAR="+year+" and head.paybill_MONTH="+month+" and head.BILL_NO="+billNo+" ");
	
		
	
		Query StrSqlQuery = hiSession.createSQLQuery(sb.toString());
		List resultList = StrSqlQuery.list();
		logger.info("query******"+StrSqlQuery);
		logger.info("result******"+resultList+"---------"+resultList.size());
		StringBuilder lStrBldXML = new StringBuilder();
		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<isGenerated>");
		int count=Integer.parseInt(resultList.get(0).toString());
		if(count==0){
			
			lStrBldXML.append("no");
			
		}
		else{
			lStrBldXML.append("yes");
		}
		lStrBldXML.append("</isGenerated>");
		lStrBldXML.append("</XMLDOC>");
		String lSBStatus = lStrBldXML.toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
		logger.info("********************************************" + lStrResult);
		objectArgs.put("ajaxKey", lStrResult);
		logger.info("^^^^^^^^^^^^^^^^^^^^^^^^^^" + lStrResult);
		resObj.setResultValue(objectArgs);
		resObj.setResultCode(ErrorConstants.SUCCESS);
		resObj.setViewName("ajaxData");
		return resObj;
	
	
	}

	private LinkedHashMap initialize(LinkedHashMap viewMap) throws SQLException {

		LinkedHashMap allow=new LinkedHashMap();
		allow.putAll(((LinkedHashMap)viewMap.get("Broken-Id")));

		return allow;
	}
	

}
