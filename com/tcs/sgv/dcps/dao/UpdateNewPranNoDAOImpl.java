package com.tcs.sgv.dcps.dao;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class UpdateNewPranNoDAOImpl extends GenericDaoHibernateImpl{
	private Session ghibSession = null;
	private final Logger gLogger = Logger.getLogger(getClass());
	public UpdateNewPranNoDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = sessionFactory.getCurrentSession();
		// TODO Auto-generated constructor stub
	}
	public List testPranNO(String pranno)
	{
		StringBuffer stbuff = new StringBuffer();
		List<Long>  lstpn = new ArrayList();
		try
		{
			stbuff.append("select emp_name,dcps_id,dcps_Emp_Id from Mst_DCPS_EMP where PRAN_NO =:pranNO");
		    logger.error("Inside testPran"+stbuff.toString());
		
			Query lstQuery = ghibSession.createSQLQuery(stbuff.toString());
			lstQuery.setString("pranNO",pranno);
			
			lstpn = lstQuery.list();
			
			logger.error("lstpn"+lstpn.get(0));
		}
		catch(Exception e)
	  	{
	  		logger.error("Error occured in testPranNO ---------"+ e);
	  		e.printStackTrace();
	  	}

		return lstpn;
		
	}


public List getAllEmp(String lStrSevaarthId ,String lStrEmployeeName, String lStrDcpsId, String lStrPpanNo,String lStrPranNo){
	 
	List lLstEmpDeselect = null;
	StringBuilder  Strbld = new StringBuilder();
	try {//DESIG_DESC
		Strbld.append("select DISTINCT dcps.EMP_NAME, dcps.DCPS_ID, dcps.SEVARTH_ID,desig.DSGN_NAME,to_char(dcps.DOJ,'dd-MM-yyyy'), to_char(dcps.DOB,'dd-MM-yyyy'),nvl(dcps.PPAN,' '),nvl(dcps.PRAN_NO,' '),dcps.ddo_code,case when (post.end_date > sysdate or post.end_date is null)  then 1 else 0 end,nvl(to_char(post.end_date,'dd-MM-YYYY'),'01-01-1111'), user.END_DATE  from mst_dcps_emp dcps ");
		Strbld.append("inner join org_emp_mst emp on dcps.ORG_EMP_MST_ID=emp.EMP_ID ");
		Strbld.append("inner join ORG_USERPOST_RLT user on emp.USER_ID=user.USER_ID  and user.ACTIVATE_FLAG =1 ");
		Strbld.append("inner join ORG_POST_MST post on user.POST_ID=post.POST_ID AND post.activate_flag=1 "); 
		Strbld.append("inner join org_designation_mst desig on post.DSGN_CODE=desig.DSGN_ID ");
		Strbld.append("where dcps.pran_no is not null and dcps.ddo_code is not null and (dcps.EMP_SERVEND_DT > sysdate or dcps.EMP_SERVEND_DT is null) and dcps.reg_status=1 and dcps.AC_DCPS_MAINTAINED_BY in (700174,700240,700241,700242) and dcps.DCPS_OR_GPF='Y' ");
		
		if (lStrEmployeeName != null && !"".equals(lStrEmployeeName)) {
			Strbld.append(" AND UPPER(dcps.EMP_NAME) = :lStrEmpName");
		}
		if (lStrSevaarthId != null && !"".equals(lStrSevaarthId)) {
			Strbld.append(" AND UPPER(dcps.SEVARTH_ID) = :sevarthId");
		}
		if (lStrDcpsId != null && !"".equals(lStrDcpsId)) {
			Strbld.append(" AND UPPER(dcps.DCPS_ID) = :dcpsId");
		}
		if (lStrPpanNo != null && !"".equals(lStrPpanNo)) {
			Strbld.append(" AND UPPER(dcps.PPAN) = :ppanNo");
		}
		if (lStrPranNo != null && !"".equals(lStrPranNo)) {
			Strbld.append(" AND UPPER(dcps.PRAN_NO) = :pranNo");
		}
		
		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
		if (lStrEmployeeName != null && !"".equals(lStrEmployeeName)) {
		lQuery.setString("lStrEmpName", lStrEmployeeName);
		}
		if (lStrSevaarthId != null && !"".equals(lStrSevaarthId)) {
		lQuery.setString("sevarthId", lStrSevaarthId);
		}
		if (lStrDcpsId != null && !"".equals(lStrDcpsId)) {
			lQuery.setString("dcpsId", lStrDcpsId);
			}
		if (lStrPpanNo != null && !"".equals(lStrPpanNo)) {
			lQuery.setString("ppanNo", lStrPpanNo);
		}
		if (lStrPranNo != null && !"".equals(lStrPranNo)) {
			lQuery.setString("pranNo", lStrPranNo);
		}
//		lQuery.setString("locId", locId);
		
		logger.info("query lstgetAllEmp ---------"+ Strbld.toString());
		lLstEmpDeselect = lQuery.list();
	}
	catch(Exception e)
  	{
  		logger.info("Error occured in lstgetAllEmp ---------"+ e);
  	}
	return lLstEmpDeselect;
}


public List checkSevaarthIdExist(String lStrSevaarthId,String lStrDDOCode, String strEmpname, String strDcpsId, String strPpanNo,String lStrPranNo) {

	StringBuilder sb = new StringBuilder();
	Query selectQuery = null;
	Date lDtCurrDate = SessionHelper.getCurDate();

	sb.append(" SELECT reg_status, nvl(to_char(EMP_SERVEND_DT,'dd-MM-yyyy'),'01-01-1900'), nvl(AC_DCPS_MAINTAINED_BY,'NA'),nvl(to_char(DOJ,'dd-MM-yyyy'),'01-01-1900'),nvl(PRAN_NO,'#'),ddo_code FROM mst_dcps_emp where form_status=1  ");

	
	if (!"".equals(lStrSevaarthId) && lStrSevaarthId != null) {
		sb.append(" AND UPPER(SEVARTH_ID) = :lStrSevaarthId");
	}
	if (!"".equals(strEmpname) && strEmpname != null) {
		sb.append(" AND UPPER(emp_name) = :strEmpname");
	}
	if (!"".equals(strDcpsId) && strDcpsId != null) {
		sb.append(" AND UPPER(Dcps_Id) = :strDcpsId");
	}
	if (!"".equals(strPpanNo) && strPpanNo != null) {
		sb.append(" AND UPPER(ppan) = :strPpanNo");
	}
	if (!"".equals(lStrPranNo) && lStrPranNo != null) {
		sb.append(" AND UPPER(PRAN_NO) = :lStrPranNo");
	}
	logger.info("Inside checkSevaarthIdExist query is *********** "+sb.toString());
	
	selectQuery = ghibSession.createSQLQuery(sb.toString());

	
	if (!"".equals(lStrSevaarthId) && lStrSevaarthId != null) {
		selectQuery.setParameter("lStrSevaarthId", lStrSevaarthId.trim().toUpperCase());
		
	}
	if (!"".equals(strEmpname) && strEmpname != null) {
		selectQuery.setParameter("strEmpname", strEmpname.trim().toUpperCase());
		
	}
	if (!"".equals(strDcpsId) && strDcpsId != null) {
		selectQuery.setParameter("strDcpsId", strDcpsId.trim().toUpperCase());
		
	}
	if (!"".equals(strPpanNo) && strPpanNo != null) {
		selectQuery.setParameter("strPpanNo", strPpanNo.trim().toUpperCase());
		
	}	if (!"".equals(lStrPranNo) && lStrPranNo != null) {
		selectQuery.setParameter("lStrPranNo", lStrPranNo.trim().toUpperCase());
		
	}
	//selectQuery.setParameter("lStrDDOCode", lStrDDOCode.trim());

	List exist = selectQuery.list();
	System.out.println("resultList"+exist.size());

	
	return exist;

}
public List getEmpNameAutoComplete(String searchKey,
		 String lStrDDOCode,String lStrSearchBy) {

	ArrayList<ComboValuesVO> finalList = new ArrayList<ComboValuesVO>();
	ComboValuesVO cmbVO;
	Object[] obj;

	StringBuilder sb = new StringBuilder();
	Query selectQuery = null;
	Date lDtCurrDate = SessionHelper.getCurDate();

	sb.append("select emp_name,emp_name from mst_dcps_emp where UPPER(emp_name) LIKE :searchKey and reg_status = 1 and form_status=1 and (EMP_SERVEND_DT > sysdate or EMP_SERVEND_DT is null) and AC_DCPS_MAINTAINED_BY in (700174,700240,700241,700242) ");
	
	if (lStrDDOCode != null) {
	
			sb.append(" and ddo_Code = :ddoCode");
		
	}
	
	sb.append(" and ( EMP_SERVEND_DT is null or EMP_SERVEND_DT  >= :currentDate ) and pran_no is not null ");
    logger.info("Inside getEmpNameAutoComplete Query is********* :" +sb.toString());
	selectQuery = ghibSession.createSQLQuery(sb.toString());
	selectQuery.setParameter("searchKey", '%' + searchKey + '%');
	selectQuery.setDate("currentDate", lDtCurrDate);

	if (lStrDDOCode != null) {
		if (!"".equals(lStrDDOCode)) {
			selectQuery.setParameter("ddoCode", lStrDDOCode.trim());
		}
	}

	List resultList = selectQuery.list();

	cmbVO = new ComboValuesVO();

	if (resultList != null && resultList.size() > 0) {
		Iterator it = resultList.iterator();
		while (it.hasNext()) {
			cmbVO = new ComboValuesVO();
			obj = (Object[]) it.next();
			cmbVO.setId(obj[1].toString());
			cmbVO.setDesc(obj[1].toString());
			finalList.add(cmbVO);
		}
	}

	return finalList;

  }

public String updatePranNo(String pranNoOld, String pranNoNew)
{
	logger.info("Inside updatePranNo *********");

	StringBuilder strBuld = new StringBuilder();  
	StringBuilder strBuld1 = new StringBuilder();  

	Query updateQuery = null;
	String flag = "NA";
	  try
	  {
	
	strBuld.append("update mst_dcps_emp set pran_no =:pranNoNew ,pran_remark='Pran Updated From "+pranNoOld+" on Date : '||sysdate where pran_no =:pranNoOld" );
	logger.info("Inside updatePranNo Query************" +strBuld.toString());
	SQLQuery lQuery = ghibSession.createSQLQuery(strBuld.toString());
	
	if (!"".equals(pranNoNew) && pranNoNew != null) {
		lQuery.setParameter("pranNoNew", pranNoNew.trim().toUpperCase());	
	}
	if (!"".equals(pranNoOld) && pranNoOld != null) {
		lQuery.setParameter("pranNoOld", pranNoOld.trim().toUpperCase());	
	}
	strBuld1.append("update nsdl_Sd_dtls set sd_pran_no=:pranNoNew where sd_pran_no=:pranNoOld" );
	logger.info("Inside updatePranNo Query************" +strBuld1.toString());
	SQLQuery lQuery1 = ghibSession.createSQLQuery(strBuld1.toString());
	
//	lQuery.setParameter("pranNoOld", pranNoOld);
//	lQuery.setParameter("pranNoNew", pranNoNew);
	if (!"".equals(pranNoNew) && pranNoNew != null) {
		lQuery1.setParameter("pranNoNew", pranNoNew.trim().toUpperCase());	
	}
	if (!"".equals(pranNoOld) && pranNoOld != null) {
		lQuery1.setParameter("pranNoOld", pranNoOld.trim().toUpperCase());	
	}
	int result=lQuery.executeUpdate();
	int result1=lQuery1.executeUpdate();

	logger.info("result:"+result);
	logger.info("result1:"+result1);
	
	if ((result!= 0 && result > 0)&&(result1!= 0 && result1 > 0)) {
		flag="Updated";	
	}
	else {
		
		flag= "NotUpdated";
	}}
	 catch (Exception e) {
		 e.printStackTrace();
	}
	return flag;
}


public String updatePran(String pranNoOld, String pranNoNew)
{
	logger.info("Inside updatePran *********");

	StringBuilder strBuld = new StringBuilder();  
	Query updateQuery = null;
	String flag = "NA";
	  try
	  {
	
	strBuld.append("update mst_dcps_emp set pran_no =:pranNoNew ,pran_remark='Pran Updated From "+pranNoOld+" on Date : '||sysdate where pran_no =:pranNoOld" );
	logger.info("Inside updatePranNo Query************" +strBuld.toString());
	SQLQuery lQuery = ghibSession.createSQLQuery(strBuld.toString());
	
//	lQuery.setParameter("pranNoOld", pranNoOld);
//	lQuery.setParameter("pranNoNew", pranNoNew);
	if (!"".equals(pranNoNew) && pranNoNew != null) {
		lQuery.setParameter("pranNoNew", pranNoNew.trim().toUpperCase());	
	}
	if (!"".equals(pranNoOld) && pranNoOld != null) {
		lQuery.setParameter("pranNoOld", pranNoOld.trim().toUpperCase());	
	}
	logger.info("pranNoNew"+pranNoNew);
	logger.info("pranNoOld"+pranNoOld);
	
	int result=lQuery.executeUpdate();

	logger.info("result:"+result);

	if (result!= 0 && result > 0) {
		flag="Updated";
	}
	else {
		flag= "NotUpdated";
	}}
	 catch (Exception e) {
		 e.printStackTrace();
	}
	return flag;
}


public List getPranNoDetails(String pranNoOld)
{
	logger.info("Inside getPranNoDetails *********");

	StringBuilder strBuld = new StringBuilder();  
	List lLstEmpDeselect = null;

	  try
	  {

	strBuld.append("SELECT FILE_NAME FROM NSDL_SD_DTLS where SD_PRAN_NO =:pranNoOld" );
	logger.info("Inside updatePranNo Query************" +strBuld.toString());
	SQLQuery lQuery = ghibSession.createSQLQuery(strBuld.toString());

	if (!"".equals(pranNoOld) && pranNoOld != null) {
		lQuery.setParameter("pranNoOld", pranNoOld.trim().toUpperCase());	
	}
	lLstEmpDeselect = lQuery.list();

	  }catch (Exception e) {
		  	e.printStackTrace();	}
	return lLstEmpDeselect;
}


	}
	
	