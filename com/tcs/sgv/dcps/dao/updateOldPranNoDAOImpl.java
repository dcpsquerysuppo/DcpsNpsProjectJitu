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

public class updateOldPranNoDAOImpl extends GenericDaoHibernateImpl{
	private Session ghibSession = null;
	private final Logger gLogger = Logger.getLogger(getClass());
	public updateOldPranNoDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = sessionFactory.getCurrentSession();
		// TODO Auto-generated constructor stub
	}
	public List testPranNO(String pranno)
	{

		StringBuffer stbuff = new StringBuffer();
		List<Long>  lstpn = new ArrayList();
		//Boolean flag  = false;
		stbuff.append("select emp_name,dcps_id,dcps_Emp_Id from Mst_DCPS_EMP where PRAN_NO =:pranNO");
		logger.info("Inside testPran"+stbuff.toString());

		Query lstQuery = ghibSession.createSQLQuery(stbuff.toString());
		lstQuery.setString("pranNO",pranno);

		lstpn = lstQuery.list();
		//String dtls=null;
		//dtls=lstpn.get(0).toString();
		/*if(lstpn != null)
		{
			if(lstpn.size()!=0)
			{
				dtls=lstpn.get(0).toString();
			}
		}*/
		return lstpn;

	}


	public List getAllEmp(String lStrSevaarthId ,String lStrEmployeeName, String lStrDcpsId, String lStrPpanNo, String locId){
		logger.info(" in getallemp########### "+locId);
		List lLstEmpDeselect = null;
		StringBuilder  Strbld = new StringBuilder();
		try {//DESIG_DESC
			Strbld.append(" select DISTINCT dcps.EMP_NAME, dcps.DCPS_ID, dcps.SEVARTH_ID,desig.DSGN_NAME,to_char(dcps.DOJ,'dd-MM-yyyy'), to_char(dcps.DOB,'dd-MM-yyyy'),nvl(dcps.PPAN,' '),nvl(dcps.PRAN_NO,' '),dcps.ddo_code,u.pran_no,nvl(u.PRAN_UPLOAD_ID,0) from mst_dcps_emp dcps ");
			Strbld.append(" inner join org_emp_mst emp on dcps.ORG_EMP_MST_ID=emp.EMP_ID ");
			Strbld.append(" inner join ORG_USERPOST_RLT user on emp.USER_ID=user.USER_ID  and user.ACTIVATE_FLAG = 1 ");
			Strbld.append(" inner join ORG_POST_MST post on user.POST_ID=post.POST_ID  "); //and (post.end_date > sysdate or post.end_date is null) removed post active flag condition
			Strbld.append(" inner join org_designation_mst desig on post.DSGN_CODE=desig.DSGN_ID ");
			Strbld.append(" inner join HST_DCPS_EMP_DEPUTATION dep on dep.DCPS_EMP_ID = dcps.DCPS_EMP_ID  and dep.REASON_DETACH is null "); 
			Strbld.append(" inner join MST_DCPS_DUMMY_OFFICE off on off.DUMMYOFFICE_ID = dep.OFF_CODE ");
			Strbld.append("left outer  join TRN_PRAN_UPLOAD_DTLS u on u.PPAN=dcps.PPAN and u.STATUS=1 ");
			Strbld.append(" where dcps.pran_no is null and dcps.ddo_code is null and dcps.PAY_COMMISSION not in ('700337') and (dcps.EMP_SERVEND_DT > sysdate or dcps.EMP_SERVEND_DT is null) and dcps.reg_status=1 and dcps.AC_DCPS_MAINTAINED_BY in (700174,700240,700241,700242) and dcps.DCPS_OR_GPF='Y' and off.TREASURY= '"+locId+"'");

			logger.info("query lstgetAllEmp ---------"+ Strbld.toString());	


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

			logger.info("query lstgetAllEmp ---------"+ Strbld.toString());
			lLstEmpDeselect = lQuery.list();
			logger.info("size is ########################################################"+lLstEmpDeselect.size());
		}
		catch(Exception e)
		{
			logger.info("Error occured in lstgetAllEmp ---------"+ e);
		}
		return lLstEmpDeselect;
	}


	public List getAllEmpNew(String lStrSevaarthId ,String lStrEmployeeName, String lStrDcpsId, String lStrPpanNo, String locId){
		logger.info(" in getallemp########### "+locId);
		List lLstEmpDeselect = null;
		StringBuilder  Strbld = new StringBuilder();
		try {//DESIG_DESC
			Strbld.append(" select DISTINCT dcps.EMP_NAME, dcps.DCPS_ID, dcps.SEVARTH_ID,desig.DSGN_NAME,to_char(dcps.DOJ,'dd-MM-yyyy'), to_char(dcps.DOB,'dd-MM-yyyy'),nvl(dcps.PPAN,' '),nvl(dcps.PRAN_NO,' '),dcps.ddo_code,u.pran_no,nvl(u.PRAN_UPLOAD_ID,0) from mst_dcps_emp dcps ");
			Strbld.append(" inner join org_emp_mst emp on dcps.ORG_EMP_MST_ID=emp.EMP_ID ");
			Strbld.append(" inner join ORG_USERPOST_RLT user on emp.USER_ID=user.USER_ID and user.ACTIVATE_FLAG = 0 ");
			Strbld.append(" inner join ORG_POST_MST post on user.POST_ID=post.POST_ID "); //and (post.end_date > sysdate or post.end_date is null) removed post active flag condition
			Strbld.append(" inner join org_designation_mst desig on post.DSGN_CODE=desig.DSGN_ID ");
			Strbld.append(" inner join HST_DCPS_EMP_DEPUTATION dep on dep.DCPS_EMP_ID = dcps.DCPS_EMP_ID  and dep.REASON_DETACH is null "); 
			Strbld.append(" inner join MST_DCPS_DUMMY_OFFICE off on off.DUMMYOFFICE_ID = dep.OFF_CODE ");
			Strbld.append("left outer  join TRN_PRAN_UPLOAD_DTLS u on u.PPAN=dcps.PPAN and u.STATUS=1 ");
			Strbld.append(" where dcps.pran_no is null and dcps.ddo_code is null and dcps.PAY_COMMISSION not in ('700337') and (dcps.EMP_SERVEND_DT > sysdate or dcps.EMP_SERVEND_DT is null) and dcps.reg_status=1 and dcps.AC_DCPS_MAINTAINED_BY in (700174,700240,700241,700242) and dcps.DCPS_OR_GPF='Y' and off.TREASURY= '"+locId+"'");

			logger.info("query lstgetAllEmp ---------"+ Strbld.toString());	


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

			logger.info("query lstgetAllEmp ---------"+ Strbld.toString());
			lLstEmpDeselect = lQuery.list();
			logger.info("size is ########################################################"+lLstEmpDeselect.size());
		}
		catch(Exception e)
		{
			logger.info("Error occured in lstgetAllEmp ---------"+ e);
		}
		return lLstEmpDeselect;
	}

	
	
	
	public List checkSevaarthIdExist(String lStrSevaarthId,String lStrDDOCode, String strEmpname, String strDcpsId, String strPpanNo,String trCode) {



		StringBuilder sb = new StringBuilder();
		Query selectQuery = null;
		Date lDtCurrDate = SessionHelper.getCurDate();

		sb.append(" SELECT dcps.reg_status, nvl(to_char(dcps.EMP_SERVEND_DT,'dd-MM-yyyy'),'01-01-1900'), nvl(dcps.AC_DCPS_MAINTAINED_BY,'NA'),nvl(to_char(dcps.DOJ,'dd-MM-yyyy'),'01-01-1900'),nvl(dcps.PRAN_NO,'#'),nvl(dcps.ddo_code,'NA'),off.TREASURY FROM mst_dcps_emp dcps   ");


		sb.append(" inner join HST_DCPS_EMP_DEPUTATION dep on dep.DCPS_EMP_ID = dcps.DCPS_EMP_ID ");
		sb.append(" inner join  MST_DCPS_DUMMY_OFFICE off on off.DUMMYOFFICE_ID = dep.OFF_CODE and substr(off.TREASURY,1,2)='"+trCode+"'"); 
		sb.append(" where form_status=1 ");


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

		sb.append(" and ( EMP_SERVEND_DT is null or EMP_SERVEND_DT  >= :currentDate ) and pran_no is null ");
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

	public String updatePranNo(String sevarthId, String pranNo)
	{
		StringBuilder strBuld = new StringBuilder();  
		Query updateQuery = null;
		String flag = "NA";
		try
		{

			strBuld.append("update mst_dcps_emp set pran_no =:pranNo where sevarth_id =:sevarthId" );
			logger.info("Inside updatePranNo Query************" +strBuld.toString());
			SQLQuery lQuery = ghibSession.createSQLQuery(strBuld.toString());

			lQuery.setParameter("sevarthId", sevarthId);
			System.out.println("sevarthId"+sevarthId);
			System.out.println("pranNo"+pranNo);
			lQuery.setParameter("pranNo", pranNo);

			int result=lQuery.executeUpdate();
			System.out.println("result:"+result);
			if (result!= 0 && result > 0) {
				flag="Updated";

			}
			else {

				flag= "NotUpdated";
			}}
		catch (Exception e) {
			e.printStackTrace();
			//e.printStackTrace();
			//return objRes;
			// TODO: handle exception
		}
		return flag;
	}










}


