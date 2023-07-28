package com.tcs.sgv.dcps.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.loan.valueobject.HrLoanAdvMst;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class LedgerReportDAOImpl extends GenericDaoHibernateImpl{
	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;
	
	public LedgerReportDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);

		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);

	}
	public List searchEmpsForLedger(String lStrSevarthId, String lStrName) {

		StringBuilder lSBQuery = new StringBuilder();
		List EmployeeList = null;

		lStrSevarthId = lStrSevarthId.toUpperCase();
		lStrName = lStrName.toUpperCase();

		lSBQuery.append(" SELECT EM.ORG_EMP_MST_ID, EM.emp_name,EM.dcps_Id, EM.gender, EM.dob, nvl(DO.off_name,''),nvl(OD.DSGN_NAME,''),EM.sevarth_Id,EM.DDO_CODE,nvl(DO.off_name,''),EM.dcps_emp_id");
		lSBQuery.append(" from mst_dcps_Emp EM ");
		lSBQuery.append(" left join mst_dcps_ddo_office DO on DO.DCPS_DDO_OFFICE_MST_ID = EM.CURR_OFF ");
		lSBQuery.append(" left join org_designation_mst OD on OD.DSGN_ID = EM.DESIGNATION");
		lSBQuery.append(" WHERE ");

		if (lStrSevarthId != null && !"".equals(lStrSevarthId)) {
			lSBQuery.append(" UPPER(EM.SEVARTH_ID) = :sevarthId ");
		}
		if (lStrName != null && !"".equals(lStrName)) {
			if (lStrSevarthId != null && !"".equals(lStrSevarthId)) {
				lSBQuery.append(" AND ");
			}
			lSBQuery.append(" UPPER(EM.emp_name) = :empName ");
		}

		lSBQuery.append(" AND EM.REG_STATUS IN (1,2) ");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		if (lStrSevarthId != null && !"".equals(lStrSevarthId)) {
			lQuery.setParameter("sevarthId", lStrSevarthId.trim().toUpperCase());
		}
		if (lStrName != null && !"".equals(lStrName)) {
			lQuery.setParameter("empName", lStrName.trim().toUpperCase());
		}

		EmployeeList = lQuery.list();

		return EmployeeList;
	}
	
	public List getNameForAutoComplete(String searchKey,long locId)
	{
		
		logger.info("Inside getEMPNameForAutoComplete****** ");

		ArrayList<ComboValuesVO> finalList = new ArrayList<ComboValuesVO>();
		ComboValuesVO cmbVO;
		Object[] obj;
		StringBuilder sb = new StringBuilder();
		//StringBuilder lSBQuery = new StringBuilder();
		Query selectQuery = null;
		Date lDtCurrDate = SessionHelper.getCurDate();

		sb.append("select name,name from MstEmp where UPPER(name) LIKE :searchKey and regStatus in (1,2) ");
		sb.append(" and ( servEndDate is null or servEndDate  >= :currentDate ) ");
		/*lSBQuery.append("SELECT empMst.* FROM ORG_POST_MST postMst , ORG_USERPOST_RLT postRlt, ORG_EMP_MST empMst ");
		lSBQuery.append("where postMst.POST_ID = postRlt.POST_ID ");
		lSBQuery.append("and postMst.LOCATION_CODE = "+locId+" ");
		lSBQuery.append("and empMst.USER_ID = postRlt.USER_ID ");
		lSBQuery.append("and postMst.ACTIVATE_FLAG = 1 ");
		lSBQuery.append("and postRlt.ACTIVATE_FLAG = 1 ");
		lSBQuery.append("and UPPER(CONCAT(CONCAT(CONCAT(CONCAT(emp_fname,' '),emp_mname),' '),emp_lname)) LIKE '%" + searchKey + "%'");
		*/

		selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("searchKey", '%' + searchKey + '%');
		selectQuery.setDate("currentDate", lDtCurrDate);

		

		List resultList = selectQuery.list();

		cmbVO = new ComboValuesVO();

		if (resultList != null && resultList.size() > 0) {
			Iterator it = resultList.iterator();
			while (it.hasNext()) {
				cmbVO = new ComboValuesVO();
				obj = (Object[]) it.next();
				logger.info("Inside getEmpNameForAutoComplete List results are--->"+obj[0].toString());
				cmbVO.setId(obj[0].toString());
				
				cmbVO.setDesc(obj[1].toString());
				
				finalList.add(cmbVO);
			}
		}

		return finalList;
	}
	 public String getGPFAcNoDetails(long UserID){
			
			Session hibSession = getSession();
			
			StringBuilder newQuery = new StringBuilder();


	         String GPFNo="";
			
	         newQuery.append("SELECT concat(concat(PF_SERIES, '/'), GPF_ACC_NO) FROM HR_PAY_GPF_DETAILS where USER_ID ="+UserID);
	         Query query=hibSession.createSQLQuery(newQuery.toString());
	         GPFNo=query.uniqueResult().toString();
			
			return GPFNo;
	}
	
	public List getEmpLastMnthBillData(long empId,String lStrDdoCode,String month,int year)
	{
		logger.info("empId "+empId+" lStrDdoCode "+lStrDdoCode+" month "+month+" year "+year);
		Session hibSession = getSession();
		StringBuffer sb = new StringBuffer();
		//
		sb.append(" SELECT pb ,head.voucherNumber,head.voucherDate,bg.dcpsDdoSchemeCode "); 
		sb.append(" FROM PaybillHeadMpg head,HrPayPaybill pb,MstEmp dcps,HrEisEmpMst eis,OrgEmpMst emp,MstDcpsBillGroup bg "); 
		sb.append(" WHERE dcps.orgEmpMstId = emp.empId and emp.empId = eis.orgEmpMst.empId and eis.empId = pb.hrEisEmpMst.empId and pb.paybillGrpId = head.hrPayPaybill "); 
		sb.append(" and head.month in ("+month+") and head.year = :year and head.approveFlag = 1 and dcps.orgEmpMstId = :empId and bg.dcpsDdoBillGroupId = head.billNo.dcpsDdoBillGroupId "); 
		sb.append(" and bg.dcpsDdoCode = :ddoCode ");
		Query query = hibSession.createQuery(sb.toString());
		Query sql1query=hibSession.createSQLQuery(sb.toString());
		query.setDouble("year", new Double(year));
		query.setLong("empId", new Long(empId));
		query.setString("ddoCode", lStrDdoCode);
		logger.info("Query in getEmpLastMnthBillData is " + query.toString());
		logger.info("sql query for last month bill data is"+sql1query.toString());
		
		return query.list();
	}
	
	public List getBrokenPeriodfrYr(long empId,String month,long year)
	{
		logger.info("empId "+empId+" month "+month+" year "+year);
		Session hibSession = getSession();
		StringBuffer sb = new StringBuffer();
		//
		
		
		
		
		sb.append("SELECT distinct mbpp.broken_period_id as BrokenId,mde.EMP_NAME as Employee_Name,mde.SEVARTH_ID as Sevarth_Id,mbpp.MONTH_ID as Month,mbpp.YEAR_ID as Year,mbpp.FROM_DATE as From,mbpp.TO_DATE as To,");
		sb.append("mbpp.NO_OF_DAYS as NoOfDays,mbpp.BASIC_PAY as Basic_Pay,clm.LOOKUP_NAME as Reason,mbpp.REMARKS as Remarks,mbpp.NET_PAY as Net_Pay  FROM MST_DCPS_BROKEN_PERIOD_PAY mbpp ");
		sb.append("inner join hr_eis_emp_mst eis on eis.emp_id=mbpp.EIS_EMP_ID ");
		sb.append("inner join MST_DCPS_EMP mde on mde.ORG_EMP_MST_ID=eis.EMP_MPG_ID ");
		sb.append("join CMN_LOOKUP_MST clm on clm.LOOKUP_ID=mbpp.REASON ");
		sb.append("where mbpp.MONTH_ID in("+month+") and mbpp.YEAR_ID ="+year+" and eis.EMP_MPG_ID ="+empId);
		//Query query = hibSession.createQuery(sb.toString());
		Query sql1query=hibSession.createSQLQuery(sb.toString());
		//logger.info("Query in getEmpLastMnthBroken is " + query.toString());
		logger.info("sql query for last month broken data is"+sql1query.toString());
		
		return sql1query.list();
	}
	
	
	public List getEmpCompoFrmBill1 (long payPayBillId,long compoType)
	{
		Session hibSession = getSession();
		String lStrCom = null;
		List payCompColmMpgLst = null;
		StringBuffer sb = new StringBuffer();
		//sb.append(" select ecm.compoId from PaybillEmpCompMpg ecm where ecm.paybillHeadMpg = "+payBillHeadMpgId );
		//sb.append(" and ecm.paybillID = "+payPayBillId+" and ecm.compoType = "+compoType );
		sb.append(" select ecm.compoId from PaybillEmpCompMpg ecm where ecm.paybillID = "+payPayBillId );
		sb.append(" and ecm.compoType = "+compoType );
		Query query = hibSession.createQuery(sb.toString());
		Query sqlquery=hibSession.createSQLQuery(sb.toString());
		logger.info("Query in getEmpCompoFrmBill1 is " + query.toString());
		logger.info("sql query for empcompo-------"+sqlquery.toString());
		List compoLst = query.list();
		if(compoLst != null && !compoLst.isEmpty())
		{
			lStrCom = compoLst.get(0).toString();
			logger.info("lStrCom ::: "+lStrCom);
			StringBuffer hqlQuery = new StringBuffer();
			hqlQuery.append(" SELECT ccm FROM HrPayCompoColumnMpg ccm "); 
			hqlQuery.append(" where ccm.id.compoId in ( ");
			hqlQuery.append(lStrCom);
			hqlQuery.append(" ) and ccm.id.compoType = "+compoType);
			query = hibSession.createQuery(hqlQuery.toString());
		//	logger.info("query in getEmpCompoFrmBill is " + query.toString());
			
			payCompColmMpgLst = new ArrayList();
			payCompColmMpgLst = query.list();
		}
		return payCompColmMpgLst;

	}
	
	public List getEmpDeductionFrmBill1 (long payPayBillId,long compoType)
	{
		Session hibSession = getSession();
		String lStrCom = null;
		List payDedColmMpgLst = null;
		StringBuffer sb = new StringBuffer();
		//sb.append(" select ecm.compoId from PaybillEmpCompMpg ecm where ecm.paybillHeadMpg = "+payBillHeadMpgId );
		//sb.append(" and ecm.paybillID = "+payPayBillId+" and ecm.compoType = "+compoType );
		sb.append(" select ecm.compoId from PaybillEmpCompMpg ecm where ecm.paybillID = "+payPayBillId );
		sb.append(" and ecm.compoType = "+compoType );
		Query query = hibSession.createQuery(sb.toString());
		Query sqlquery=hibSession.createSQLQuery(sb.toString());
		logger.info("Query in getEmpCompoFrmBill1 is " + query.toString());
		logger.info("sql query for empcompo-------"+sqlquery.toString());
		List compoLst = query.list();
		if(compoLst != null && !compoLst.isEmpty())
		{
			lStrCom = compoLst.get(0).toString();
			logger.info("lStrCom ::: "+lStrCom);
			StringBuffer hqlQuery = new StringBuffer();
			hqlQuery.append(" SELECT ccm FROM HrPayCompoColumnMpg ccm "); 
			hqlQuery.append(" where ccm.id.compoId in ( ");
			hqlQuery.append(lStrCom);
			hqlQuery.append(" ) and ccm.id.compoType = "+compoType);
			query = hibSession.createQuery(hqlQuery.toString());
		//	logger.info("query in getEmpCompoFrmBill is " + query.toString());
			
			payDedColmMpgLst = new ArrayList();
			payDedColmMpgLst = query.list();
		}
		return payDedColmMpgLst;

	}
	
	
	public String getJoiningDate(Long empId){
		Session hibSession = getSession();
		String joinDate="";
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT START_DATE FROM HST_DCPS_EMP_DETAILS ");
		sb.append("where DCPS_EMP_ID="+empId+" order by START_DATE desc");
		Query query = hibSession.createSQLQuery(sb.toString());
		logger.info("joining date query " + query.toString());
		List joiningDates= query .list();
		joinDate=joiningDates.get(0).toString().substring(0, 10);
		
		return joinDate;
	}
	
	public List getHrLoanAdvMst(){
		List<HrLoanAdvMst> advMstList = null;
		Session hibSession = getSession();
		StringBuffer query = new StringBuffer();
		query.append(" from HrLoanAdvMst loan");
	    Query query1 = hibSession.createQuery(query.toString());
	    if(query1 != null){
	    	advMstList = query1.list();
	    }
	    return advMstList;
	}
	
	
	public List getEmpDetails(String sevarthId)
	{
		
		Session hibSession = getSession();
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT emp.EMP_NAME, to_char(emp.DOB, 'dd/MM/yyyy'),scale.SCALE_DESC,emp.PAY_IN_PAY_BAND,emp.GRADE_PAY,emp.BASIC_PAY,to_char(payfix.NXT_INCR_DATE, 'dd/MM/yyyy'),to_char(rlt.CURR_POST_JOINING_DATE, 'dd/MM/yyyy'),qtr.QUARTER_NAME,to_char(qtr.ALLOCATION_START_DATE, 'dd/MM/yyyy'),to_char(qtr.ALLOCATION_END_DATE, 'dd/MM/yyyy'),emp.ADDRESS_BUILDING||','||emp.ADDRESS_STREET||','||emp.LANDMARK||','||emp.LOCALITY||','||emp.DISTRICT||','||emp.PINCODE FROM mst_dcps_emp emp inner join org_emp_mst mst on mst.EMP_ID = emp.ORG_EMP_MST_ID ");
		sb.append("left	outer join  HR_PAYFIX_MST payfix on payfix.USER_ID = mst.USER_ID ");
		sb.append("inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID = emp.ORG_EMP_MST_ID ");
		sb.append("inner join RLT_DCPS_PAYROLL_EMP rlt on rlt.DCPS_EMP_ID = emp.DCPS_EMP_ID ");
		sb.append("inner join hr_eis_scale_mst scale on scale.scale_id = emp.payscale ");
		sb.append("left outer join HR_EIS_QTR_EMP_MPG qtr on qtr.ALLOCATED_TO = eis.EMP_ID where emp.SEVARTH_ID = '"+sevarthId+"'");
		
		//Query query = hibSession.createQuery(sb.toString());
		Query sql1query=hibSession.createSQLQuery(sb.toString());
		//logger.info("Query in getEmpLastMnthBroken is " + query.toString());
		logger.info("sql query for getEmpDetails broken data is"+sql1query.toString());
		
		return sql1query.list();
	}
	public List getLoanDetails(String sevarthId, long finYrId) {
		// TODO Auto-generated method stub
		Session hibSession = getSession();
		StringBuffer sb = new StringBuffer();		
		
		sb.append("SELECT distinct loan.LOAN_TYPE_ID,loan.LOAN_PRIN_AMT,loan.LOAN_PRIN_EMI_AMT,loan.VOUCHER_NO,to_char(loan.VOUCHER_DATE,'dd/MM/yyyy'),loan.LOAN_PRIN_INST_NO,adv.LOAN_ADV_NAME,trim(nvl(loan.LOAN_SANC_ORDER_NO,'NA')),to_char(loan.LOAN_SANC_ORDER_DATE,'dd/MM/yyyy') FROM HR_LOAN_EMP_DTLS loan inner join   ");
		sb.append("HR_EIS_EMP_MST eis on loan.EMP_ID  = eis.EMP_ID ");
		sb.append("inner join mst_dcps_emp emp on emp.ORG_EMP_MST_ID = eis.EMP_MPG_ID ");
		sb.append("inner join HR_PAY_PAYBILL pay on pay.EMP_ID = eis.EMP_ID ");
		sb.append("inner join PAYBILL_HEAD_MPG mpg on mpg.PAYBILL_ID = pay.PAYBILL_GRP_ID ");
		sb.append("inner join HR_LOAN_ADV_MST adv on adv.LOAN_ADV_ID = loan.LOAN_TYPE_ID ");
		sb.append("where emp.SEVARTH_ID = '"+sevarthId+"' and loan.LOAN_ACTIVATE_FLAG = 1 " );
		
		//Query query = hibSession.createQuery(sb.toString());
		Query sql1query=hibSession.createSQLQuery(sb.toString());
		//logger.info("Query in getEmpLastMnthBroken is " + query.toString());
		logger.info("sql query for getLoanDetails data is"+sql1query.toString());
		
		return sql1query.list();
	}
	public List getLoanDetailsMonthWise(String sevarthId, long finYrIdLower,long finYrIdUpper ) {
		// TODO Auto-generated method stub
		Session hibSession = getSession();
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT pay.PAYBILL_MONTH,'67~'|| pay.HBA_HOUSE,'51~'|| pay.HBA_LAND,'54~'|| pay.GPF_ADV_GRP_ABC,  ");
		sb.append("'55~'|| pay.GPF_ADV_GRP_D,'56~'|| pay.MCA_LAND,'57~'|| pay.OTHER_VEH_ADV,'58~'|| pay.COMPUTER_ADV, '59~'|| pay.FESTIVAL_ADVANCE, ");
		sb.append("'60~'|| pay.OTHER_ADV,'61~'|| pay.CO_HSG_SOC,'63~'|| pay.GPF_IAS_LOAN,'108~'||TOTAL_DED ");
		sb.append("FROM mst_dcps_emp emp inner join hr_eis_emp_mst eis on emp.ORG_EMP_MST_ID = eis.EMP_MPG_ID ");
		sb.append("inner join hr_pay_paybill pay on eis.EMP_ID = pay.EMP_ID ");
		sb.append("inner join PAYBILL_HEAD_MPG mpg on pay.PAYBILL_GRP_ID = mpg.PAYBILL_ID ");
		sb.append("where emp.SEVARTH_ID ='"+sevarthId+"' and mpg.APPROVE_FLAG = 1 and mpg.BILL_CATEGORY = 2 and ((pay.PAYBILL_YEAR ="+finYrIdUpper+" and pay.PAYBILL_MONTH<4) or (pay.PAYBILL_YEAR ="+finYrIdLower+" and pay.PAYBILL_MONTH>3)) " );
		
		//Query query = hibSession.createQuery(sb.toString());
		Query sql1query=hibSession.createSQLQuery(sb.toString());
		//logger.info("Query in getEmpLastMnthBroken is " + query.toString());
		logger.info("sql query for getLoanDetails data is"+sql1query.toString());
		
		return sql1query.list();
	}
	 
	public String getFinYear(long finYrId) {
		// TODO Auto-generated method stub
		Session hibSession = getSession();
		String finYear="";
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT FIN_YEAR_CODE FROM SGVC_FIN_YEAR_MST where FIN_YEAR_ID ="+finYrId);
		
		Query query = hibSession.createSQLQuery(sb.toString());
		logger.info("joining date query " + query.toString());
		List joiningDates= query .list();
		finYear=joiningDates.get(0).toString();
		
		return finYear;
	}
	 
	public List getGpfDetails(String sevarthId, long finYrId) {
		// TODO Auto-generated method stub
		Session hibSession = getSession();
		StringBuffer sb = new StringBuffer();	
		sb.append("SELECT gpfmst.GPF_ACC_NO,gpfyr.CLOSING_BALANCE,gpfyr.OPENING_BALANCE FROM MST_emp_GPF_ACC gpfmst inner join mst_gpf_yearly gpfyr on gpfmst.GPF_ACC_NO=gpfyr.GPF_ACC_NO ");
		sb.append("where gpfmst.SEVAARTH_ID ='"+sevarthId+"' and gpfyr.FIN_YEAR_ID ="+finYrId );
		
		//Query query = hibSession.createQuery(sb.toString());
		Query sql1query=hibSession.createSQLQuery(sb.toString());
		//logger.info("Query in getEmpLastMnthBroken is " + query.toString());
		logger.info("sql query for getLoanDetails data is"+sql1query.toString());
		
		return sql1query.list();
	}

	
	public List getGISDetails(String sevarthId, long finYrIdLower,long finYrIdUpper ) {
		// TODO Auto-generated method stub
		Session hibSession = getSession();
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT distinct to_char(gis.MEMBERSHIP_DATE, 'dd/MM/yyyy'),org.GRADE_NAME,pay.GIS,pay.PAYBILL_MONTH FROM HR_EIS_GIS_DTLS gis inner join HR_EIS_EMP_MST eis on eis.EMP_ID = gis.EMP_ID  ");
		sb.append("inner join mst_dcps_emp emp on emp.ORG_EMP_MST_ID = eis.EMP_MPG_ID ");
		sb.append("inner join ORG_GRADE_MST org on org.GRADE_ID = gis.GIS_GROUP_GRADE_ID ");
		sb.append("inner join HR_PAY_PAYBILL pay on pay.EMP_ID = eis.EMP_ID ");	
		//sb.append("inner join HST_DCPS_EMP_DETAILS hst on emp.DCPS_EMP_ID = hst.DCPS_EMP_ID ");	
		sb.append("inner join PAYBILL_HEAD_MPG mpg on pay.PAYBILL_GRP_ID = mpg.PAYBILL_ID ");
		sb.append("where emp.SEVARTH_ID = '"+sevarthId+"' and org.LANG_ID = 1 and mpg.APPROVE_FLAG =1 and  ((pay.PAYBILL_YEAR ="+finYrIdUpper+" and pay.PAYBILL_MONTH<4) or (pay.PAYBILL_YEAR ="+finYrIdLower+" and pay.PAYBILL_MONTH>3)) order by  pay.PAYBILL_MONTH desc ");
		//Query query = hibSession.createQuery(sb.toString());
		Query sql1query=hibSession.createSQLQuery(sb.toString());
		//logger.info("Query in getEmpLastMnthBroken is " + query.toString());
		logger.info("sql query for getLoanDetails data is"+sql1query.toString());
		
		return sql1query.list();
	}
	 
	public Long getYearlyContribution(String sevarthID,long finYrId) {
		// TODO Auto-generated method stub
		Session hibSession = getSession();
		Long dcpsYearlyCon=0l;
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT dcps.CONTRIB_EMPLOYEE FROM MST_DCPS_CONTRIBUTION_YEARLY dcps inner join MST_DCPS_EMP emp on emp.DCPS_ID = dcps.DCPS_ID where emp.SEVARTH_ID ='"+sevarthID+"' and dcps.YEAR_ID ="+finYrId);
		
		Query query = hibSession.createSQLQuery(sb.toString());
		logger.info("joining date query " + query.toString());
		List joiningDates= query .list();
		if(joiningDates!=null && !joiningDates.isEmpty()){
			if(joiningDates.get(0)!=null){			
			
			dcpsYearlyCon=Long.parseLong(joiningDates.get(0).toString());
			}
		}
		
		return dcpsYearlyCon;
	}
	
	
	
	
	
	public List getPostDetails(String sevarthId) {
		// TODO Auto-generated method stub
		Session hibSession = getSession();
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT distinct desg.DESIG_DESC,post.POST_NAME,postmst.POST_TYPE_LOOKUP_ID FROM ORG_USER_MST user inner join ORG_USERPOST_RLT userpost on  user.USER_ID= userpost.USER_ID ");
		sb.append("inner join ORG_POST_DETAILS_RLT post on post.POST_ID = userpost.POST_ID ");
		sb.append("inner join ORG_POST_MST postmst on postmst.POST_ID =  post.POST_ID ");
		sb.append("inner join MST_PAYROLL_DESIGNATION desg on desg.ORG_DESIGNATION_ID = post.DSGN_ID where user.USER_NAME = '"+sevarthId+"' and postmst.ACTIVATE_FLAG = 1 and userpost.ACTIVATE_FLAG = 1 ");		
		//Query query = hibSession.createQuery(sb.toString());
		Query sql1query=hibSession.createSQLQuery(sb.toString());
		//logger.info("Query in getEmpLastMnthBroken is " + query.toString());
		logger.info("sql query for getLoanDetails data is"+sql1query.toString());
		
		return sql1query.list();
	}
	
	 
	public List getTotalDedandGrass(String sevarthId, long finYrIdLower,long finYrIdUpper) {
		// TODO Auto-generated method stub
		Session hibSession = getSession();
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT pay.PAYBILL_MONTH,pay.PAYBILL_MONTH ||'~'|| pay.GROSS_AMT,pay.PAYBILL_MONTH ||'~'|| pay.TOTAL_DED,pay.PAYBILL_MONTH ||'~'||(pay.GROSS_AMT-pay.PO),pay.PAYBILL_MONTH ||'~'||mpg.BILL_NO, pay.PAYBILL_MONTH ||'~'||to_char(mpg.CREATED_DATE,'dd/mm/yyyy')  ");
		sb.append("FROM mst_dcps_emp emp inner join hr_eis_emp_mst eis on emp.ORG_EMP_MST_ID = eis.EMP_MPG_ID ");
		sb.append("inner join hr_pay_paybill pay on eis.EMP_ID = pay.EMP_ID ");
		sb.append("inner join PAYBILL_HEAD_MPG mpg on pay.PAYBILL_GRP_ID = mpg.PAYBILL_ID ");
		sb.append("where emp.SEVARTH_ID ='"+sevarthId+"' and mpg.APPROVE_FLAG = 1 and ((pay.PAYBILL_YEAR ="+finYrIdUpper+" and pay.PAYBILL_MONTH<4) or (pay.PAYBILL_YEAR ="+finYrIdLower+" and pay.PAYBILL_MONTH>3)) order by  pay.PAYBILL_MONTH ");		
		//Query query = hibSession.createQuery(sb.toString());
		Query sql1query=hibSession.createSQLQuery(sb.toString());
		//logger.info("Query in getEmpLastMnthBroken is " + query.toString());
		logger.info("sql query for getLoanDetails data is"+sql1query.toString());
		
		return sql1query.list();
	}
	public List getBrokenPeriodData(String sevarthId, long finYrId,String component) {
		// TODO Auto-generated method stub
		Session hibSession = getSession();
		StringBuffer sb = new StringBuffer();
		if(component.equals("Basic"))
			sb.append("SELECT mbpp.BASIC_PAY  ||'~'|| month(mbpp.FROM_DATE) ");
			if(component.equals("netPay"))
				sb.append("SELECT mbpp.NET_PAY ||'~'|| month(mbpp.FROM_DATE) ");
			if(component.equals("noOfDays"))
				sb.append("SELECT mbpp.NO_OF_DAYS  ||'~'|| month(mbpp.FROM_DATE) ");
			if(component.equals("reason"))
				sb.append("SELECT clm.LOOKUP_NAME ||'~'|| month(mbpp.FROM_DATE) ");
			if(component.equals("remarks"))
				sb.append("SELECT mbpp.REMARKS  ||'~'|| month(mbpp.FROM_DATE) ");
			if(component.equals("fromDate"))
				sb.append("SELECT to_char(mbpp.FROM_DATE,'dd/MM/yyyy') ||'~'|| month(mbpp.FROM_DATE) ");
			if(component.equals("toDate"))
				sb.append("SELECT to_char(mbpp.TO_DATE,'dd/MM/yyyy') ||'~'|| month(mbpp.FROM_DATE) ");
			if(component.equals("voucherNo"))
				sb.append("SELECT head.VOUCHER_NO  ||'~'|| month(mbpp.FROM_DATE) ");
			if(component.equals("voucherDate"))
				sb.append("SELECT  to_char(head.VOUCHER_DATE,'dd/MM/yyyy')  ||'~'|| month(mbpp.FROM_DATE) ");
			if(component.equals("billNo"))
				sb.append("SELECT head.BILL_NO  ||'~'|| month(mbpp.FROM_DATE) ");
			if(component.equals("BillDate"))
				sb.append("SELECT head.CREATED_DATE  ||'~'|| month(mbpp.FROM_DATE) ");
		sb.append(" FROM PAYBILL_HEAD_MPG   head ");
		sb.append(" inner join HR_PAY_PAYBILL hpp on hpp.PAYBILL_GRP_ID=head.PAYBILL_ID ");
		sb.append(" inner join MST_DCPS_BROKEN_PERIOD_PAY mbpp on mbpp.EIS_EMP_ID= hpp.EMP_ID ");
		sb.append(" inner join HR_EIS_EMP_MST eis on eis.emp_id=hpp.emp_id ");
		sb.append(" inner join MST_DCPS_EMP mde on mde.ORG_EMP_MST_ID=eis.emp_mpg_id");
		sb.append(" join CMN_LOOKUP_MST clm on clm.LOOKUP_ID=mbpp.REASON ");		
		sb.append(" and mbpp.YEAR_ID="+finYrId+" and head.approve_flag  = 1 and mde.SEVARTH_ID='"+sevarthId+"' ");
		sb.append(" and  head.BILL_CATEGORY=3 ");
		
		//Query query = hibSession.createQuery(sb.toString());
		Query sql1query=hibSession.createSQLQuery(sb.toString());
		//logger.info("Query in getEmpLastMnthBroken is " + query.toString());
		logger.info("sql query for getbrokenperioddata data is"+sql1query.toString());
		
		return sql1query.list();
	}
	public List getBrokenPeriodAllowData(Long brokenPeriodId) {
		Session hibSession = getSession();
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT patm.ALLOW_NAME as allowName,dbpa.RLT_BROKEN_PERIOD_ID as BrokenId FROM RLT_DCPS_BROKEN_PERIOD_ALLOW dbpa ");
		sb.append("join HR_PAY_ALLOW_TYPE_MST patm on dbpa.ALLOW_CODE=patm.ALLOW_CODE where dbpa.RLT_BROKEN_PERIOD_ID in ("+brokenPeriodId+")");
		
		
		//Query query = hibSession.createQuery(sb.toString());
		Query sql1query=hibSession.createSQLQuery(sb.toString());
		//logger.info("Query in getEmpLastMnthBroken is " + query.toString());
		logger.info("sql query for getbrokenperioddallowata data is"+sql1query.toString());
		
		return sql1query.list();
		
	}
	public List getBrokenPeriodDeducData(Long brokenPeriodId) {
		Session hibSession = getSession();
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT pdtm.DEDUC_NAME as deductName,dbpd.RLT_BROKEN_PERIOD_ID as BrokenId FROM RLT_DCPS_BROKEN_PERIOD_DEDUC dbpd ");
		sb.append("join HR_PAY_DEDUC_TYPE_MST pdtm on dbpd.DEDUC_CODE=pdtm.DEDUC_CODE where dbpd.RLT_BROKEN_PERIOD_ID in ("+brokenPeriodId+")");
		
		
		//Query query = hibSession.createQuery(sb.toString());
		Query sql1query=hibSession.createSQLQuery(sb.toString());
		//logger.info("Query in getEmpLastMnthBroken is " + query.toString());
		logger.info("sql query for getbrokenperioddallowata data is"+sql1query.toString());
		
		return sql1query.list();
		
		
	}
	public List getBroenPeriodId(String sevarthId, long finYrId) {
		Session hibSession = getSession();
		StringBuffer sb = new StringBuffer();
		
		sb.append(" SELECT mbpp.BROKEN_PERIOD_ID	 FROM PAYBILL_HEAD_MPG   head ");
		sb.append(" inner join HR_PAY_PAYBILL hpp on hpp.PAYBILL_GRP_ID=head.PAYBILL_ID ");
		sb.append(" inner join MST_DCPS_BROKEN_PERIOD_PAY mbpp on mbpp.EIS_EMP_ID= hpp.EMP_ID and mbpp.MONTH_ID=head.PAYBILL_MONTH  ");
		sb.append(" inner join HR_EIS_EMP_MST eis on eis.emp_id=hpp.emp_id ");
		sb.append(" inner join MST_DCPS_EMP mde on mde.ORG_EMP_MST_ID=eis.emp_mpg_id ");		
		sb.append(" and mbpp.YEAR_ID="+finYrId+" and head.approve_flag  = 1  and mde.SEVARTH_ID='"+sevarthId+"' ");
		sb.append(" and  head.BILL_CATEGORY=3 ");
		
		//Query query = hibSession.createQuery(sb.toString());
		Query sql1query=hibSession.createSQLQuery(sb.toString());
		//logger.info("Query in getEmpLastMnthBroken is " + query.toString());
		logger.info("sql query for getbrokenperiodID data is"+sql1query.toString());
		
		return sql1query.list();
	}
	public List getBrokenAllow( String brokenPeriodId,Long allowId) {
		Session hibSession = getSession();
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT allow.allow_name,bpa.ALLOW_VALUE || '~' || month(bpp.FROM_DATE) FROM RLT_DCPS_BROKEN_PERIOD_ALLOW bpa inner join MST_DCPS_BROKEN_PERIOD_PAY bpp on bpp.BROKEN_PERIOD_ID = bpa.RLT_BROKEN_PERIOD_ID ");
		sb.append(" inner join HR_EIS_EMP_MST eis on eis.EMP_ID = bpp.EIS_EMP_ID ");
		sb.append(" inner join MST_DCPS_EMP emp on emp.ORG_EMP_MST_ID= eis.EMP_MPG_ID ");
		sb.append(" inner join HR_PAY_ALLOW_TYPE_MST allow on allow.ALLOW_CODE=bpa.ALLOW_CODE ");
		sb.append(" where bpa.RLT_BROKEN_PERIOD_ID in ( "+brokenPeriodId+") and  bpa.ALLOW_CODE = "+allowId);
		
		//Query query = hibSession.createQuery(sb.toString());
		Query sql1query=hibSession.createSQLQuery(sb.toString());
		//logger.info("Query in getEmpLastMnthBroken is " + query.toString());
		logger.info("sql query for getbrokenperioddallowata data is"+sql1query.toString());
		
		return sql1query.list();
		
	}
	public List getMaxallowList(String sevarthId, Long finYrId) {
		Session hibSession = getSession();
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT distinct rlt.ALLOW_CODE FROM RLT_DCPS_BROKEN_PERIOD_ALLOW rlt inner join MST_DCPS_BROKEN_PERIOD_PAY  pay on rlt.RLT_BROKEN_PERIOD_ID=pay.BROKEN_PERIOD_ID  ");
		sb.append(" inner join HR_PAY_ALLOW_TYPE_MST allow on allow.ALLOW_CODE=rlt.ALLOW_CODE ");
		sb.append(" inner join hr_eis_emp_mst eis on eis.EMP_ID=pay.EIS_EMP_ID ");
		sb.append(" inner join mst_dcps_emp emp on emp.ORG_EMP_MST_ID=eis.EMP_MPG_ID ");
		sb.append(" where emp.SEVARTH_ID = '"+sevarthId+"' and pay.YEAR_ID  = "+finYrId);
		
		//Query query = hibSession.createQuery(sb.toString());
		Query sql1query=hibSession.createSQLQuery(sb.toString());
		//logger.info("Query in getEmpLastMnthBroken is " + query.toString());
		logger.info("sql query for getbrokenperioddallowata data is"+sql1query.toString());
		
		return sql1query.list();
		
	}
	public List getMaxDeducList(String sevarthId, Long finYrId) {
		Session hibSession = getSession();
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT distinct rlt.DEDUC_CODE FROM RLT_DCPS_BROKEN_PERIOD_DEDUC rlt inner join MST_DCPS_BROKEN_PERIOD_PAY  pay on rlt.RLT_BROKEN_PERIOD_ID=pay.BROKEN_PERIOD_ID  ");
		sb.append(" inner join HR_PAY_DEDUC_TYPE_MST dedc on dedc.DEDUC_CODE=rlt.DEDUC_CODE ");
		sb.append(" inner join hr_eis_emp_mst eis on eis.EMP_ID=pay.EIS_EMP_ID ");
		sb.append(" inner join mst_dcps_emp emp on emp.ORG_EMP_MST_ID=eis.EMP_MPG_ID ");
		sb.append(" where emp.SEVARTH_ID = '"+sevarthId+"' and pay.YEAR_ID  = "+finYrId);
		
		//Query query = hibSession.createQuery(sb.toString());
		Query sql1query=hibSession.createSQLQuery(sb.toString());
		//logger.info("Query in getEmpLastMnthBroken is " + query.toString());
		logger.info("sql query for getbrokenperioddallowata data is"+sql1query.toString());
		
		return sql1query.list();
		
	}
	public List getBrokenDeducValue(String strBrokenIdList, Long deducId) {
		Session hibSession = getSession();
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT deduc.DEDUC_NAME,bpa.DEDUC_VALUE || '~' || month(bpp.FROM_DATE) FROM RLT_DCPS_BROKEN_PERIOD_DEDUC bpa inner join MST_DCPS_BROKEN_PERIOD_PAY bpp on bpp.BROKEN_PERIOD_ID = bpa.RLT_BROKEN_PERIOD_ID  ");
		sb.append(" inner join HR_EIS_EMP_MST eis on eis.EMP_ID = bpp.EIS_EMP_ID  ");
		sb.append(" inner join MST_DCPS_EMP emp on emp.ORG_EMP_MST_ID= eis.EMP_MPG_ID ");
		sb.append(" inner join HR_PAY_DEDUC_TYPE_MST deduc on deduc.DEDUC_CODE=bpa.DEDUC_CODE ");
		sb.append(" where bpa.RLT_BROKEN_PERIOD_ID in ( "+strBrokenIdList+") and  bpa.DEDUC_CODE = "+deducId);
		
		//Query query = hibSession.createQuery(sb.toString());
		Query sql1query=hibSession.createSQLQuery(sb.toString());
		//logger.info("Query in getEmpLastMnthBroken is " + query.toString());
		logger.info("sql query for getBrokenDeducValue data is"+sql1query.toString());
		
		return sql1query.list();
		
	}
	public List getTotalAllowDeducValue(String strBrokenIdList, String component) {
		Session hibSession = getSession();
		StringBuffer sb = new StringBuffer();
		if(component.equals("totalAllowance")){
		sb.append("SELECT cast(sum(bpa.ALLOW_VALUE )as varchar(10)) ||'~'|| cast(month(mbpp.FROM_DATE )as varchar(2)) ");
		sb.append(" FROM RLT_DCPS_BROKEN_PERIOD_ALLOW bpa inner join MST_DCPS_BROKEN_PERIOD_PAY mbpp on bpa.RLT_BROKEN_PERIOD_ID=mbpp.BROKEN_PERIOD_ID  ");	
		
		sb.append(" where bpa.RLT_BROKEN_PERIOD_ID in ( "+strBrokenIdList+") group by month(mbpp.FROM_DATE )");
		}
		else if(component.equals("totalDeduction")){
			sb.append("SELECT cast(sum(bpa.DEDUC_VALUE )as varchar(10)) ||'~'|| cast(month(mbpp.FROM_DATE )as varchar(2)) ");
			sb.append(" FROM RLT_DCPS_BROKEN_PERIOD_DEDUC bpa inner join MST_DCPS_BROKEN_PERIOD_PAY mbpp on bpa.RLT_BROKEN_PERIOD_ID=mbpp.BROKEN_PERIOD_ID   ");
			sb.append(" where bpa.RLT_BROKEN_PERIOD_ID in ( "+strBrokenIdList+") group by month(mbpp.FROM_DATE )");
			
		}
		//Query query = hibSession.createQuery(sb.toString());
		Query sql1query=hibSession.createSQLQuery(sb.toString());
		//logger.info("Query in getEmpLastMnthBroken is " + query.toString());
		logger.info("sql query for getBrokenDeducValue data is"+sql1query.toString());
		
		return sql1query.list();
	}
	public List getallowListIdNormal(String sevarthId, Long finYearLower,Long finYearUpper,Long compoId) {
		Session hibSession = getSession();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT paycomp.compo_id FROM HR_PAY_PAYBILL paybill ");
		sb.append("  INNER JOIN HR_EIS_EMP_MST hremp on paybill.EMP_ID=hremp.EMP_ID ");
		sb.append(" inner JOIN MST_DCPS_EMP mstemp on hremp.EMP_MPG_ID=mstemp.ORG_EMP_MST_ID ");
		sb.append(" INNER JOIN  PAYBILL_HEAD_MPG payhead on payhead.PAYBILL_ID=paybill.PAYBILL_GRP_ID ");
		sb.append(" LEFT OUTER JOIN HR_PAY_PAYBILL_EMP_COMP_MPG paycomp on paycomp.HR_PAY_PAYBILL_ID = paybill.ID ");
		sb.append("  INNER JOIN MST_DCPS_BILL_GROUP billgrp on billgrp.BILL_GROUP_ID=payhead.BILL_NO ");
		sb.append(" INNER JOIN CMN_LOOKUP_MST lookup on paycomp.COMPO_TYPE=lookup.LOOKUP_ID ");
		sb.append("  where hremp.SEVARTH_EMP_CD='"+sevarthId+"'  ");
		sb.append("  and payhead.APPROVE_FLAG =1  and ((paybill.PAYBILL_YEAR ="+finYearUpper+" and paybill.PAYBILL_MONTH<4) or (paybill.PAYBILL_YEAR ="+finYearLower+" and paybill.PAYBILL_MONTH>3)) and paycomp.COMPO_TYPE= "+compoId);
		
		Query sql1query=hibSession.createSQLQuery(sb.toString());
		//logger.info("Query in getEmpLastMnthBroken is " + query.toString());
		logger.info("sql query for getBrokenDeducValue data is"+sql1query.toString());
		
		return sql1query.list();
		
	}
	public List getallowNames(String strAllowValues,Long compoType) {
		Session hibSession = getSession();
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT PAYBILL_COLUMN FROM HR_PAY_COMPO_COLUMN_MPG where COMPO_ID in ("+strAllowValues+") and COMPO_TYPE= "+compoType);
		
		
		Query sql1query=hibSession.createSQLQuery(sb.toString());
		//logger.info("Query in getEmpLastMnthBroken is " + query.toString());
		logger.info("sql query for getBrokenDeducValue data is"+sql1query.toString());
		
		return sql1query.list();
		
	}
	public List getAllowValues(String sevarthId, Long finYearLower,
			Long finYearUpper, String allowName) {
		Session hibSession = getSession();
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT "+allowName+" ||'~'|| pay.PAYBILL_MONTH from hr_pay_paybill pay inner join HR_EIS_EMP_MST eis on eis.EMP_ID = pay.EMP_ID ");
		sb.append(" inner join PAYBILL_HEAD_MPG mpg on mpg.PAYBILL_ID = pay.PAYBILL_GRP_ID ");
		sb.append("  inner join mst_dcps_emp emp on emp.ORG_EMP_MST_ID = eis.EMP_MPG_ID where emp.SEVARTH_ID='"+sevarthId+"' and   ");
		sb.append("  mpg.APPROVE_FLAG=1 and mpg.BILL_CATEGORY = 2 and  ((pay.PAYBILL_YEAR =2015 and pay.PAYBILL_MONTH<4) or (pay.PAYBILL_YEAR =2014 and pay.PAYBILL_MONTH>3)) ");		
		Query sql1query=hibSession.createSQLQuery(sb.toString());
		//logger.info("Query in getEmpLastMnthBroken is " + query.toString());
		logger.info("sql query for getAllowValues is"+sql1query.toString());
		
		return sql1query.list();
	}
	public List getOtherComponentValues(String sevarthId, Long finYearLower,Long finYearUpper, String compoName) {
		Session hibSession = getSession();
		StringBuffer sb = new StringBuffer();
		if(compoName.equals("TotalAllowance"))
			sb.append("SELECT pay.PAYBILL_MONTH ||'~'||(pay.GROSS_AMT-pay.PO) ");
		if(compoName.equals("TotalDeduction"))
			sb.append("SELECT pay.PAYBILL_MONTH ||'~'|| pay.TOTAL_DED ");
		if(compoName.equals("NetPay"))
			sb.append("SELECT pay.PAYBILL_MONTH ||'~'||pay.net_total ");
		if(compoName.equals("BillNo"))
			sb.append("SELECT pay.PAYBILL_MONTH ||'~'||mpg.BILL_NO ");
		if(compoName.equals("BillDate"))
			sb.append(" SELECT pay.PAYBILL_MONTH ||'~'||to_char(mpg.CREATED_DATE,'dd/mm/yyyy') ");
		if(compoName.equals("VoucherNo"))
			sb.append(" SELECT pay.PAYBILL_MONTH ||'~'|| mpg.VOUCHER_NO ");
		if(compoName.equals("VoucherDate"))
		sb.append(" SELECT pay.PAYBILL_MONTH ||'~'|| to_char(mpg.VOUCHER_DATE,'dd/mm/yyyy') ");		 
		sb.append(" FROM mst_dcps_emp emp inner join hr_eis_emp_mst eis on emp.ORG_EMP_MST_ID = eis.EMP_MPG_ID ");
		sb.append(" inner join hr_pay_paybill pay on eis.EMP_ID = pay.EMP_ID  ");
		sb.append(" inner join PAYBILL_HEAD_MPG mpg on pay.PAYBILL_GRP_ID = mpg.PAYBILL_ID  ");
		sb.append(" where emp.SEVARTH_ID ='"+sevarthId+"' and mpg.APPROVE_FLAG = 1 and mpg.BILL_CATEGORY = 2 and ((pay.PAYBILL_YEAR ="+finYearUpper+" and pay.PAYBILL_MONTH<4) or (pay.PAYBILL_YEAR ="+finYearLower+" and pay.PAYBILL_MONTH>3)) order by  pay.PAYBILL_MONTH ");
		
		Query sql1query=hibSession.createSQLQuery(sb.toString());
		//logger.info("Query in getEmpLastMnthBroken is " + query.toString());
		logger.info("sql query for getOtherComponentValues is"+sql1query.toString());
		
		return sql1query.list();
		
	}
	public List getInstallRecValues(String sevarthId, Long finYearLower,	Long finYearUpper, String allowName) {
		Session hibSession = getSession();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT distinct "+allowName+" || '~'|| loan.RECOVERED_INST || '~' || loan.TOTAL_INST || '~' || mpg.PAYBILL_MONTH FROM HR_PAY_PAYBILL_LOAN_DTLS loan inner join PAYBILL_HEAD_MPG mpg on loan.PAYBILL_ID = mpg.PAYBILL_ID ");
		sb.append(" inner join HR_LOAN_EMP_DTLS emploan on emploan.EMP_LOAN_ID=loan.EMP_LOAN_ID ");
		sb.append(" inner join HR_PAY_COMPO_COLUMN_MPG compo on compo.COMPO_Id=loan.LOAN_TYPE_ID ");
		sb.append(" inner join HR_EIS_EMP_MST eis on eis.EMP_ID = emploan.EMP_ID  ");
		sb.append(" inner join HR_PAY_PAYBILL pay on pay.EMP_ID = eis.EMP_ID and  pay.PAYBILL_GRP_ID = mpg.PAYBILL_ID ");
		sb.append(" inner join mst_dcps_emp emp on emp.ORG_EMP_MST_ID=eis.EMP_MPG_ID where emp.SEVARTH_ID = '"+sevarthId+"' ");
		sb.append(" and mpg.APPROVE_FLAG = 1 and mpg.BILL_CATEGORY=2 and compo.PAYBILL_COLUMN='"+allowName+"' and ((mpg.PAYBILL_YEAR ="+finYearUpper+" and mpg.PAYBILL_MONTH<4) or (mpg.PAYBILL_YEAR ="+finYearLower+" and mpg.PAYBILL_MONTH>3)) ");
		Query sql1query=hibSession.createSQLQuery(sb.toString());
		//logger.info("Query in getEmpLastMnthBroken is " + query.toString());
		logger.info("sql query for getAllowValues is"+sql1query.toString());
		
		return sql1query.list();
	}
	public List getLpcIdDate(String sevarthId, Long finYearLower,Long finYearUpper) {
		Session hibSession = getSession();
		StringBuffer sb = new StringBuffer();
		sb.append(" select hst.HST_DCPS_ID,to_char(hst.END_DATE, 'dd/MM/yyyy'),mpg.PAYBILL_MONTH from mst_dcps_emp emp inner join HST_DCPS_EMP_DETAILS hst on emp.DCPS_EMP_ID = hst.DCPS_EMP_ID ");
		sb.append(" inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID = emp.ORG_EMP_MST_ID ");
		sb.append(" inner join HR_PAY_PAYBILL pay on pay.EMP_ID = eis.EMP_ID ");
		sb.append(" inner join PAYBILL_HEAD_MPG mpg on mpg.PAYBILL_ID = pay.PAYBILL_GRP_ID ");
		sb.append(" where emp.SEVARTH_ID =  '"+sevarthId+"' and mpg.APPROVE_FLAG =1 and mpg.BILL_CATEGORY = 2 and  ((pay.PAYBILL_YEAR = "+finYearUpper+" and pay.PAYBILL_MONTH<4) or (pay.PAYBILL_YEAR = "+finYearLower+" and pay.PAYBILL_MONTH>3)) order by  pay.PAYBILL_MONTH desc ");
		Query sql1query=hibSession.createSQLQuery(sb.toString());
		//logger.info("Query in getEmpLastMnthBroken is " + query.toString());
		logger.info("sql query for getLpcIdDate is"+sql1query.toString());
		
		return sql1query.list();
		
	}
	public void updateGPFDuplEmpDetails(String empSevarthId, String pfAcNo,
			String pfSeriesDesc) {
		// TODO Auto-generated method stub
		
	}
	public List getGPFDuplEmpList(String empSevarthId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	 

}
