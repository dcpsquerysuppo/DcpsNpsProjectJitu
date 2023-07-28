package com.tcs.sgv.dcps.dao;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.dao.FormS1DAO;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;

public class FormSubscriber2DAOImpl
extends GenericDaoHibernateImpl
implements FormSubscriber2DAO {
	private final Log gLogger;
	org.hibernate.Session ghibSession;

	public FormSubscriber2DAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		this.gLogger = LogFactory.getLog(this.getClass());
		this.ghibSession = null;
		this.ghibSession = sessionFactory.getCurrentSession();
		this.setSessionFactory(sessionFactory);
	}

	public List getEmpNameForS1AutoComplete(String searchKey, String lStrDDOCode) {
		ArrayList<ComboValuesVO> finalList = new ArrayList<ComboValuesVO>();
		StringBuilder sb = new StringBuilder();
		Query selectQuery = null;
		Date lDtCurrDate = SessionHelper.getCurDate();
		this.gLogger.info((Object)("lStrDDOCode in DDO is **********" + lStrDDOCode));
		sb.append("select name,name from MstEmp where UPPER(name) LIKE :searchKey and regStatus=1 ");
		if (lStrDDOCode != null && !"".equals(lStrDDOCode)) {
			sb.append(" and ddoCode = :ddoCode");
		}
		sb.append(" and  servEndDate  >= :currentDate and  acDcpsMaintainedBy in (700174,700240,700241,700242) and formStatus=1 ");
		selectQuery = this.ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("searchKey", (Object)(String.valueOf('%') + searchKey + '%'));
		selectQuery.setDate("currentDate", lDtCurrDate);
		if (lStrDDOCode != null && !"".equals(lStrDDOCode)) {
			selectQuery.setParameter("ddoCode", (Object)lStrDDOCode.trim());
		}
		List resultList = selectQuery.list();
		ComboValuesVO cmbVO = new ComboValuesVO();
		if (resultList != null && resultList.size() > 0) {
			Iterator it = resultList.iterator();
			while (it.hasNext()) {
				cmbVO = new ComboValuesVO();
				Object[] obj = (Object[])it.next();
				cmbVO.setId(obj[1].toString());
				cmbVO.setDesc(obj[1].toString());
				finalList.add(cmbVO);
			}
		}
		return finalList;
	}

	public String checkSevaarthIdExist(String lStrSevaarthId, String lStrDDOCode) {
		String exist = "NA";
		StringBuilder sb = new StringBuilder();
		SQLQuery selectQuery = null;
		Date lDtCurrDate = SessionHelper.getCurDate();
		sb.append(" SELECT * FROM mst_dcps_emp where  DDO_CODE=:lStrDDOCode and reg_status=1 and form_status=1 and  (EMP_SERVEND_DT > sysdate OR EMP_SERVEND_DT is null)  ");
		if (!"".equals(lStrSevaarthId) && lStrSevaarthId != null) {
			sb.append(" AND UPPER(SEVARTH_ID) = :lStrSevaarthId");
		}
		selectQuery = this.ghibSession.createSQLQuery(sb.toString());
		if (!"".equals(lStrSevaarthId) && lStrSevaarthId != null) {
			selectQuery.setParameter("lStrSevaarthId", (Object)lStrSevaarthId.trim());
		}
		selectQuery.setParameter("lStrDDOCode", (Object)lStrDDOCode.trim());
		List resultList = selectQuery.list();
		if (resultList != null && resultList.size() > 0) {
			exist = "AVAIL";
		}
		return exist;
	}

//	public String getDdo(String lStrSevaarthId) {
//		String exist = "NA";
//		StringBuilder sb = new StringBuilder();
//		SQLQuery selectQuery = null;
//		Date lDtCurrDate = SessionHelper.getCurDate();
//		sb.append(" SELECT DDO_CODE FROM mst_dcps_emp where  ");
//		if (!"".equals(lStrSevaarthId) && lStrSevaarthId != null) {
//			sb.append(" AND UPPER(SEVARTH_ID) = :lStrSevaarthId");
//		}
//		selectQuery = this.ghibSession.createSQLQuery(sb.toString());
//		if (!"".equals(lStrSevaarthId) && lStrSevaarthId != null) {
//			selectQuery.setParameter("lStrSevaarthId", (Object)lStrSevaarthId.trim());
//		}
//		List resultList = selectQuery.list();
//		if (resultList != null && resultList.size() > 0) {
//			exist = "AVAIL";
//		}
//		return exist;
//	}
	
	public List getSectionADetails(String lStrSevaarthId) throws Exception {
		List lLstEmpPerDtls = null;
		SQLQuery hqlQuery = null;
		StringBuilder lStrQuery = new StringBuilder();
		org.hibernate.Session session = this.getSession();
		Long lLngDesigAll = -2L;
		Date lDtCurrDate = SessionHelper.getCurDate();
		try {
			lStrQuery.append(" SELECT upper(emp.emp_name),emp.gender,to_char(emp.dob,'dd/MM/yyyy'),upper(emp.FATHER_OR_HUSBAND),upper(emp.ADDRESS_BUILDING) , ");
			lStrQuery.append(" upper(emp.ADDRESS_STREET) ,upper(emp.LOCALITY) ,  upper(emp.DISTRICT),upper(c.state_name)||'', ");
			lStrQuery.append(" cast('INDIA' as varchar),cast(nvl(EMP.PINCODE,'') as varchar),NVL(EMP.CNTCT_NO,''),NVL(EMP.CELL_NO,''),nvl(EMP.EMAIL_ID,' '),nvl(emp.bank_acnt_no,''),upper(nvl(bank.BANK_NAME,'')),upper(nvl(branch.BRANCH_NAME,'')), ");
			lStrQuery.append(" nvl(upper(branch.NEW_ADDRESS),' '),emp.IFSC_CODE,branch.MICR_CODE,emp.salutation,nvl(upper(emp.PAN_NO),' '),nvl(upper(emp.PADDRESS_BUILDING),' '),nvl(upper(emp.PADDRESS_STREET),' '),  ");
			lStrQuery.append(" nvl(upper(emp.PLOCALITY),' '),nvl(upper(emp.PDISTRICT),' '),nvl(upper(c1.state_name),' '),cast('INDIA' as varchar),cast(nvl(EMP.PPINCODE,' ') as varchar),branch.PINCODE,nvl(emp.PRAN_NO,' '),upper(org.EMP_FNAME),upper(org.EMP_MNAME),upper(org.EMP_LNAME),org.EMP_FNAME||' '||org.EMP_MNAME||' '||org.EMP_LNAME,nvl(upper(emp.mother_NAME),' '),nvl(upper(form.city_of_birth),' '),nvl(upper(form.country_of_birth),' '),nvl(upper(form.martial_status),' '),nvl(upper(emp.spouse_NAME),' '),nvl(upper(form.US_PERSON),' '),nvl(upper(form.COUNTRY_OF_TAX),' '),nvl(upper(form.ADDRESS_OF_TAX),' '),nvl(upper(form.CITY_OF_TAX),' '),nvl(upper(form.STATE_OF_TAX),' '),nvl(upper(form.POST_CODE_OF_TAX),' '),nvl(upper(form.TIN_OR_PAN),' '),nvl(upper(form.TIN_COUNTRY),' '), nvl(upper(emp.UID_NO),' ') FROM  mst_dcps_emp emp ");
			lStrQuery.append(" inner join org_emp_mst org on org.EMP_Id = emp.ORG_EMP_MST_ID  ");
			lStrQuery.append(" left join mst_bank_pay bank on  cast(bank.BANK_code as varchar(20))=emp.BANK_NAME    ");
			lStrQuery.append(" left join RLT_BANK_BRANCH_PAY branch on cast(branch.BRANCH_ID as varchar(20))=emp.BRANCH_NAME   ");			
			lStrQuery.append(" left outer join CMN_STATE_MST c on cast(c.state_id as varchar(20)) = emp.STATE ");
			lStrQuery.append(" left outer join CMN_STATE_MST c1 on cast(c1.state_id as varchar(20)) = emp.PSTATE ");			
			lStrQuery.append(" left outer join FRM_FORM_S1_DTLS form on form.DCPS_ID=emp.DCPS_ID ");
			lStrQuery.append(" WHERE  emp.REG_STATUS=1  ");
			if (!"".equals(lStrSevaarthId) && lStrSevaarthId != null) {
				lStrQuery.append(" AND UPPER(emp.SEVARTH_ID) = :lStrSevaarthId");
			}
			lStrQuery.append(" AND  (emp.EMP_SERVEND_DT >= sysdate OR emp.EMP_SERVEND_DT is null) ");
			lStrQuery.append(" and emp.FORM_STATUS=1 and emp.AC_DCPS_MAINTAINED_BY in (700174,700240,700241,700242) ");
			hqlQuery = session.createSQLQuery(lStrQuery.toString());
			if (!"".equals(lStrSevaarthId) && lStrSevaarthId != null) {
				hqlQuery.setParameter("lStrSevaarthId", (Object)lStrSevaarthId.trim());
			}
			lLstEmpPerDtls = hqlQuery.list();
		}
		catch (Exception e) {
			this.gLogger.error((Object)("Error is :" + e), (Throwable)e);
			throw e;
		}
		return lLstEmpPerDtls;
	}

	public List getBankDetails(String lStrSevaarthId) throws Exception {
		List lLstEmpPerDtls = null;
		SQLQuery hqlQuery = null;
		StringBuilder lStrQuery = new StringBuilder();
		org.hibernate.Session session = this.getSession();
		Long lLngDesigAll = -2L;
		Date lDtCurrDate = SessionHelper.getCurDate();
		try {
			lStrQuery.append(" SELECT nvl(emp.bank_acnt_no,' '),upper(nvl(bank.BANK_NAME,' ')),upper(nvl(branch.BRANCH_NAME,' ')), ");
			lStrQuery.append(" nvl(upper(branch.NEW_ADDRESS),' '),emp.IFSC_CODE,nvl(branch.MICR_CODE,' '),branch.PINCODE,nvl(upper(c.state_name),' ')  FROM  mst_dcps_emp emp ");
			lStrQuery.append(" left join mst_bank_pay bank on  cast(bank.BANK_code as varchar(20))=emp.BANK_NAME    ");
			lStrQuery.append(" left join RLT_BANK_BRANCH_PAY branch on cast(branch.BRANCH_ID as varchar(20))=emp.BRANCH_NAME   ");			
			lStrQuery.append(" left outer join CMN_STATE_MST c on cast(c.state_id as varchar(20)) = emp.STATE ");
			lStrQuery.append(" WHERE  emp.REG_STATUS=1  ");
			if (!"".equals(lStrSevaarthId) && lStrSevaarthId != null) {
				lStrQuery.append(" AND UPPER(emp.SEVARTH_ID) = :lStrSevaarthId");
			}
			lStrQuery.append(" AND  (emp.EMP_SERVEND_DT >= sysdate OR emp.EMP_SERVEND_DT is null) ");
			lStrQuery.append(" and emp.FORM_STATUS=1 and emp.AC_DCPS_MAINTAINED_BY in (700174,700240,700241,700242) ");
			hqlQuery = session.createSQLQuery(lStrQuery.toString());
			if (!"".equals(lStrSevaarthId) && lStrSevaarthId != null) {
				hqlQuery.setParameter("lStrSevaarthId", (Object)lStrSevaarthId.trim());
			}
			lLstEmpPerDtls = hqlQuery.list();
		}
		catch (Exception e) {
			this.gLogger.error((Object)("Error is :" + e), (Throwable)e);
			throw e;
		}
		return lLstEmpPerDtls;
	}
	
	public List getSectionBDetails(String lStrSevaarthId,String IsDeputation) throws Exception {
		List lLstEmpEmpytDtls = null;
		SQLQuery hqlQuery = null;
		StringBuilder lStrQuery = new StringBuilder();
		org.hibernate.Session session = this.getSession();
		Long lLngDesigAll = -2L;
		Date lDtCurrDate = SessionHelper.getCurDate();
		try {

			if(IsDeputation.equals("Y")){
				lStrQuery.append(" SELECT to_char(emp.doj,'dd/MM/yyyy'),to_char(emp.super_ann_date,'dd/MM/yyyy'),emp.ppan,BASIC_PAY,scale.scale_desc,nvl(upper(ddo.DDO_NAME),' '),nvl(upper(loc.LOC_NAME),' '),nvl(upper(loc1.LOC_NAME),' '), ");
				lStrQuery.append(" grd.GRADE_ID,month(emp.doj),year(emp.doj),reg.DDO_REG_NO,dto.DTO_REG_NO ");
				lStrQuery.append(" FROM mst_dcps_emp emp ");
				lStrQuery.append(" inner join frm_form_s1_dtls frm on frm.sevarth_id=emp.sevarth_id ");
				lStrQuery.append(" inner join ORG_DDO_MST ddo on ddo.DDO_CODE=frm.DDO_CODE ");
				lStrQuery.append(" inner join CMN_LOCATION_MST loc on loc.LOC_ID=ddo.DEPT_LOC_CODE   ");
				lStrQuery.append(" inner join CMN_LOCATION_MST loc1 on loc1.LOC_ID=ddo.HOD_LOC_CODE ");
				lStrQuery.append(" inner join ORG_EMP_MST mst on mst.emp_id= emp.ORG_EMP_MST_ID ");
				lStrQuery.append(" inner join ORG_USER_MST user on user.USER_id=mst.USER_ID   ");
				lStrQuery.append(" inner join ORG_GRADE_MST grd on grd.GRADE_ID=mst.GRADE_ID ");
				lStrQuery.append(" left outer join HR_EIS_SCALE_MST scale on cast(scale.scale_id as varchar(20))=emp.PAYSCALE   ");
				lStrQuery.append(" inner join MST_DDO_REG reg on reg.ddo_code = frm.DDO_CODE ");
				lStrQuery.append("  inner join MST_DTO_REG dto on substr(dto.loc_id,1,2) = substr(frm.ddo_code,1,2) ");
				lStrQuery.append(" WHERE  emp.REG_STATUS=1  ");
			}else{
				lStrQuery.append(" SELECT to_char(doj,'dd/MM/yyyy'),to_char(super_ann_date,'dd/MM/yyyy'),emp.ppan,BASIC_PAY,scale.scale_desc,nvl(upper(ddo.DDO_NAME),' '),nvl(upper(loc.LOC_NAME),' '),nvl(upper(loc1.LOC_NAME),' '), ");
				lStrQuery.append(" grd.GRADE_ID,month(doj),year(doj),reg.DDO_REG_NO,dto.DTO_REG_NO ");
				lStrQuery.append(" FROM mst_dcps_emp emp inner join ORG_DDO_MST ddo on ddo.DDO_CODE=emp.DDO_CODE ");
				lStrQuery.append(" inner join CMN_LOCATION_MST loc on loc.LOC_ID=ddo.DEPT_LOC_CODE   ");
				lStrQuery.append(" inner join CMN_LOCATION_MST loc1 on loc1.LOC_ID=ddo.HOD_LOC_CODE ");
				lStrQuery.append(" inner join ORG_EMP_MST mst on mst.emp_id= emp.ORG_EMP_MST_ID ");
				lStrQuery.append(" inner join ORG_USER_MST user on user.USER_id=mst.USER_ID   ");
				lStrQuery.append(" inner join ORG_GRADE_MST grd on grd.GRADE_ID=mst.GRADE_ID ");
				lStrQuery.append(" left outer join HR_EIS_SCALE_MST scale on cast(scale.scale_id as varchar(20))=emp.PAYSCALE   ");
				lStrQuery.append(" inner join MST_DDO_REG reg on reg.ddo_code = emp.DDO_CODE ");
				lStrQuery.append("  inner join MST_DTO_REG dto on substr(dto.loc_id,1,2) = substr(emp.ddo_code,1,2) ");
				lStrQuery.append(" WHERE  emp.REG_STATUS=1  ");
			}
			if (!"".equals(lStrSevaarthId) && lStrSevaarthId != null) {
				lStrQuery.append(" AND UPPER(emp.SEVARTH_ID) = :lStrSevaarthId");
			}
			lStrQuery.append(" AND (emp.EMP_SERVEND_DT >= sysdate OR emp.EMP_SERVEND_DT is null) ");
			lStrQuery.append(" and emp.FORM_STATUS=1 and emp.AC_DCPS_MAINTAINED_BY in (700174,700240,700241,700242) and  grd.GRADE_ID in (100001,100064,100065,100066,100067) ");
			hqlQuery = session.createSQLQuery(lStrQuery.toString());
			if (!"".equals(lStrSevaarthId) && lStrSevaarthId != null) {
				hqlQuery.setParameter("lStrSevaarthId", (Object)lStrSevaarthId.trim());
			}
			lLstEmpEmpytDtls = hqlQuery.list();
		}
		catch (Exception e) {
			this.gLogger.error((Object)("Error is :" + e), (Throwable)e);
			throw e;
		}
		return lLstEmpEmpytDtls;
	}

	public List getSectionCDetails(String lStrSevaarthId) throws Exception {
		List lLstEmpNmn = null;
		SQLQuery hqlQuery = null;
		StringBuilder lStrQuery = new StringBuilder();
		org.hibernate.Session session = this.getSession();
		Long lLngDesigAll = -2L;
		Date lDtCurrDate = SessionHelper.getCurDate();
		try {
			lStrQuery.append(" SELECT upper(decode(form.DCPS_NMN_NAME,'NA',' ','',' ',form.DCPS_NMN_NAME)),nvl(to_char(form.DCPS_NMN_DOB,'dd/MM/yyyy'),' '), UPPER(case when form.DCPS_NMN_RLT = '-1' then ' ' when look1.LOOKUP_NAME  is null then form.DCPS_NMN_RLT  else look1.LOOKUP_NAME end),(case when form.DCPS_NMN_SHARE  is null then ' ' else form.DCPS_NMN_SHARE||'%' end),upper(decode(form.DCPS_NMN_GUARDIAN,'NA',' ','',' ',form.DCPS_NMN_GUARDIAN)) ");
			lStrQuery.append(" ,UPPER(nvl(form.DCPS_NMN_INVALID,' ')) , upper(decode(form.DCPS_NMN_NAME,'NA',' ','',' ',form.DCPS_NMN_NAME)),nvl(to_char(form.DCPS_NMN_DOB,'dd/MM/yyyy'),' '),UPPER(case when form.DCPS_NMN_RLT = '-1' then ' ' when look1.LOOKUP_NAME  is null then form.DCPS_NMN_RLT  else look1.LOOKUP_NAME end),(case when form.DCPS_NMN_SHARE  is null then ' ' else form.DCPS_NMN_SHARE||'%' end),  ");
			lStrQuery.append(" upper(decode(form.DCPS_NMN_GUARDIAN,'NA',' ','',' ',form.DCPS_NMN_GUARDIAN)),UPPER(nvl(form.DCPS_NMN_INVALID,' ')),upper(decode(form.DCPS_NMN_NAME,'NA',' ','',' ',form.DCPS_NMN_NAME)), nvl(to_char(form.DCPS_NMN_DOB,'dd/MM/yyyy'),' '),UPPER(case when form.DCPS_NMN_RLT = '-1' then ' ' when look1.LOOKUP_NAME  is null then form.DCPS_NMN_RLT  else look1.LOOKUP_NAME end),(case when form.DCPS_NMN_SHARE  is null then ' ' else form.DCPS_NMN_SHARE||'%' end),upper(decode(form.DCPS_NMN_GUARDIAN,'NA',' ','',' ',form.DCPS_NMN_GUARDIAN)),UPPER(nvl(form.DCPS_NMN_INVALID,' '))  ");
			lStrQuery.append(" FROM mst_dcps_emp emp left outer join MST_DCPS_EMP_NMN form on form.DCPS_EMP_ID=emp.DCPS_EMP_ID   ");
			lStrQuery.append(" left outer join CMN_LOOKUP_MST look1 on cast(look1.LOOKUP_ID as varchar)=form.DCPS_NMN_RLT  ");
			lStrQuery.append(" WHERE  emp.REG_STATUS=1  ");
			if (!"".equals(lStrSevaarthId) && lStrSevaarthId != null) {
				lStrQuery.append(" AND UPPER(emp.SEVARTH_ID) = :lStrSevaarthId");
			}
			lStrQuery.append(" AND (emp.EMP_SERVEND_DT >= sysdate OR emp.EMP_SERVEND_DT is null) ");
			lStrQuery.append(" and emp.FORM_STATUS=1 and emp.AC_DCPS_MAINTAINED_BY in (700174,700240,700241,700242)   ");
			hqlQuery = session.createSQLQuery(lStrQuery.toString());
			if (!"".equals(lStrSevaarthId) && lStrSevaarthId != null) {
				hqlQuery.setParameter("lStrSevaarthId", (Object)lStrSevaarthId.trim());
			}
			lLstEmpNmn = hqlQuery.list();
		}
		catch (Exception e) {
			this.gLogger.error((Object)("Error is :" + e), (Throwable)e);
			throw e;
		}
		return lLstEmpNmn;
	}

	public List getNomineeDetails(String lStrSevaarthId) throws Exception {
		List lLstEmpNmn = null;
		SQLQuery hqlQuery = null;
		StringBuilder lStrQuery = new StringBuilder();
		org.hibernate.Session session = this.getSession();
		Long lLngDesigAll = -2L;
		Date lDtCurrDate = SessionHelper.getCurDate();
		try {
			lStrQuery.append(" SELECT upper(decode(form.DCPS_NMN_NAME,'NA',' ','',' ',form.DCPS_NMN_NAME)),nvl(to_char(form.DCPS_NMN_DOB,'dd/MM/yyyy'),' '), UPPER(case when form.DCPS_NMN_RLT = '-1' then ' ' when look1.LOOKUP_NAME  is null then form.DCPS_NMN_RLT  else look1.LOOKUP_NAME end),(case when form.DCPS_NMN_SHARE  is null then ' ' else form.DCPS_NMN_SHARE||'%' end),upper(decode(form.DCPS_NMN_GUARDIAN,'NA',' ','',' ',form.DCPS_NMN_GUARDIAN)) ");
			lStrQuery.append(" ,UPPER(nvl(form.DCPS_NMN_INVALID,' ')) , upper(decode(form.DCPS_NMN_NAME,'NA',' ','',' ',form.DCPS_NMN_NAME)),nvl(to_char(form.DCPS_NMN_DOB,'dd/MM/yyyy'),' '),UPPER(case when form.DCPS_NMN_RLT = '-1' then ' ' when look1.LOOKUP_NAME  is null then form.DCPS_NMN_RLT  else look1.LOOKUP_NAME end),(case when form.DCPS_NMN_SHARE  is null then ' ' else form.DCPS_NMN_SHARE||'%' end),  ");
			lStrQuery.append(" upper(decode(form.DCPS_NMN_GUARDIAN,'NA',' ','',' ',form.DCPS_NMN_GUARDIAN)),UPPER(nvl(form.DCPS_NMN_INVALID,' ')),upper(decode(form.DCPS_NMN_NAME,'NA',' ','',' ',form.DCPS_NMN_NAME)), nvl(to_char(form.DCPS_NMN_DOB,'dd/MM/yyyy'),' '),UPPER(case when form.DCPS_NMN_RLT = '-1' then ' ' when look1.LOOKUP_NAME  is null then form.DCPS_NMN_RLT  else look1.LOOKUP_NAME end),(case when form.DCPS_NMN_SHARE  is null then ' ' else form.DCPS_NMN_SHARE||'%' end),upper(decode(form.DCPS_NMN_GUARDIAN,'NA',' ','',' ',form.DCPS_NMN_GUARDIAN)),UPPER(nvl(form.DCPS_NMN_INVALID,' '))  ");
			lStrQuery.append(" FROM mst_dcps_emp emp left outer join MST_DCPS_EMP_NMN form on form.DCPS_EMP_ID=emp.DCPS_EMP_ID   ");
			lStrQuery.append(" left outer join CMN_LOOKUP_MST look1 on cast(look1.LOOKUP_ID as varchar)=form.DCPS_NMN_RLT  ");
			lStrQuery.append(" WHERE  emp.REG_STATUS=1  ");
			if (!"".equals(lStrSevaarthId) && lStrSevaarthId != null) {
				lStrQuery.append(" AND UPPER(emp.SEVARTH_ID) = :lStrSevaarthId");
			}
			lStrQuery.append(" AND (emp.EMP_SERVEND_DT >= sysdate OR emp.EMP_SERVEND_DT is null) ");
			lStrQuery.append(" and emp.FORM_STATUS=1 and emp.AC_DCPS_MAINTAINED_BY in (700174,700240,700241,700242)   ");
			hqlQuery = session.createSQLQuery(lStrQuery.toString());
			if (!"".equals(lStrSevaarthId) && lStrSevaarthId != null) {
				hqlQuery.setParameter("lStrSevaarthId", (Object)lStrSevaarthId.trim());
			}
			lLstEmpNmn = hqlQuery.list();
		}
		catch (Exception e) {
			this.gLogger.error((Object)("Error is :" + e), (Throwable)e);
			throw e;
		}
		return lLstEmpNmn;
	}
	
	
	
	public List getDTORegNo(String lStrSevaarthId) throws Exception {
		SQLQuery hqlQuery = null;
		StringBuilder lStrQuery = new StringBuilder();
		org.hibernate.Session session = this.getSession();
		List dtdoRegNo = null;
		Date lDtCurrDate = SessionHelper.getCurDate();
		try {
			lStrQuery.append("  SELECT distinct  nvl(cast(dto.DTO_REG_NO as varchar),' '),nvl(cast( reg.ddo_reg_no as varchar),' ') FROM CMN_LOCATION_MST loc inner join MST_DTO_REG dto on dto.LOC_ID=loc.LOC_ID  ");
			lStrQuery.append(" inner join CMN_LOCATION_MST loc1 on (loc1.PARENT_LOC_ID=loc.LOC_ID or loc1.loc_id=dto.LOC_ID) ");
			lStrQuery.append(" inner join mst_dcps_emp emp on substr(emp.DDO_CODE,1,4)=loc1.LOC_ID ");
			lStrQuery.append("    left outer join MST_DDO_REG REG on REG.ASSOCIATED_DTO_REG_NO=dto.DTO_REG_NO  and reg.ddo_code=emp.ddo_code ");
			lStrQuery.append(" WHERE  emp.REG_STATUS=1  ");
			if (!"".equals(lStrSevaarthId) && lStrSevaarthId != null) {
				lStrQuery.append(" AND UPPER(emp.SEVARTH_ID) = :lStrSevaarthId");
			}
			lStrQuery.append(" AND  (emp.EMP_SERVEND_DT >= sysdate OR emp.EMP_SERVEND_DT is null) ");
			lStrQuery.append(" and emp.FORM_STATUS=1 and emp.AC_DCPS_MAINTAINED_BY in (700174,700240,700241,700242) and  emp.DDO_CODE is not null   ");
			hqlQuery = session.createSQLQuery(lStrQuery.toString());
			if (!"".equals(lStrSevaarthId) && lStrSevaarthId != null) {
				hqlQuery.setParameter("lStrSevaarthId", (Object)lStrSevaarthId.trim());
			}
			dtdoRegNo = hqlQuery.list();
		}
		catch (Exception e) {
			this.gLogger.error((Object)("Error is :" + e), (Throwable)e);
			throw e;
		}
		return dtdoRegNo;
	}

	public List checkNmnCount(String lStrSevaarthId) throws Exception {
		List lLstEmpNmn = null;
		SQLQuery hqlQuery = null;
		StringBuilder lStrQuery = new StringBuilder();
		org.hibernate.Session session = this.getSession();
		Long lLngDesigAll = -2L;
		Date lDtCurrDate = SessionHelper.getCurDate();
		try {
			lStrQuery.append(" SELECT nvl(NOMINEE_1_PERCENT_SHARE,'NA'),nvl(NOMINEE_2_PERCENT_SHARE,'NA'),nvl(NOMINEE_3_PERCENT_SHARE,'NA'),nvl(cast(year(sysdate)-year(NOMINEE_1_DOB) as varchar),' '),nvl(cast(year(sysdate)-year(NOMINEE_2_DOB) as varchar),' '),nvl(cast(year(sysdate)-year(NOMINEE_3_DOB) as varchar),' ') FROM FRM_FORM_S1_DTLS ");
			lStrQuery.append(" WHERE   ");
			if (!"".equals(lStrSevaarthId) && lStrSevaarthId != null) {
				lStrQuery.append("  UPPER(SEVARTH_ID) = :lStrSevaarthId");
			}
			hqlQuery = session.createSQLQuery(lStrQuery.toString());
			if (!"".equals(lStrSevaarthId) && lStrSevaarthId != null) {
				hqlQuery.setParameter("lStrSevaarthId", (Object)lStrSevaarthId.trim());
			}
			lLstEmpNmn = hqlQuery.list();
		}
		catch (Exception e) {
			this.gLogger.error((Object)("Error is :" + e), (Throwable)e);
			throw e;
		}
		return lLstEmpNmn;
	}

	public String checkUpdationDone(String lStrSevaarthId) {
		List resultList;
		String exist = "blank";
		StringBuilder sb = new StringBuilder();
		SQLQuery selectQuery = null;
		Date lDtCurrDate = SessionHelper.getCurDate();
		sb.append(" SELECT * FROM FRM_FORM_S1_DTLS where 1=1 and CREATED_DATE >= '2017-12-09' and  ");
		if (!"".equals(lStrSevaarthId) && lStrSevaarthId != null) {
			sb.append("  UPPER(SEVARTH_ID) = :lStrSevaarthId");
		}
		selectQuery = this.ghibSession.createSQLQuery(sb.toString());
		if (!"".equals(lStrSevaarthId) && lStrSevaarthId != null) {
			selectQuery.setParameter("lStrSevaarthId", (Object)lStrSevaarthId.trim());
		}
		if ((resultList = selectQuery.list()) != null && resultList.size() > 0) {
			this.gLogger.info((Object)("Size is ***********" + resultList.size()));
			exist = "exist";
		}
		return exist;
	}

	public List getEmpListForFrmS1Edit(String strDDOCode, String flag, String txtSearch, String isDeputation) {

		this.logger.info((Object)("in dao txtSearch ####" + txtSearch));
		org.hibernate.Session hibSession = this.getSession();
		StringBuffer strQuery = new StringBuffer();
		strQuery.append(" select empmst.SEVARTH_ID,empmst.EMP_NAME ,VARCHAR_FORMAT(org.EMP_DOJ, 'dd/MM/yyyy') , ");
		strQuery.append(" desig.DSGN_NAME ,empmst.DDO_CODE,empmst.DCPS_ID  ");
		strQuery.append(" from MST_DCPS_EMP empmst ");
		strQuery.append(" inner join org_emp_mst org on org.EMP_ID = empmst.ORG_EMP_MST_ID ");
		strQuery.append(" inner join ORG_USERPOST_RLT userpost on userpost.USER_ID=org.USER_ID ");
		strQuery.append(" inner join ORG_POST_MST post on post.POST_ID=userpost.POST_ID   ");
		strQuery.append(" inner join ORG_DESIGNATION_MST desig on empmst.DESIGNATION=desig.DSGN_ID ");
		strQuery.append(" inner join frm_form_s1_dtls frm on empmst.sevarth_id=frm.sevarth_id ");
		strQuery.append(" where (empmst.EMP_SERVEND_DT > sysdate OR empmst.EMP_SERVEND_DT is null) "); 
		if(isDeputation.equals("Y"))
		{
			/*if(depPrintDdoFlag.equals("Y")){
        			strQuery.append(" and frm.DEP_UPDATED_DDO_CD='"+strDDOCode+"' ");
        			}*/
		}
		else
		{
			strQuery.append(" and empmst.DDO_CODE='"+strDDOCode+"'  and frm.DEP_UPDATED_DDO_CD is null ");
		}

		if(isDeputation.equals("Y"))
		{
			strQuery.append("  and frm.DEP_UPDATED_DDO_CD is not null  and empmst.DCPS_OR_GPF='Y' and empmst.AC_DCPS_MAINTAINED_BY in (700174,700240,700241,700242)  ");

		}
		else
		{
			strQuery.append(" and empmst.DCPS_OR_GPF='Y' and empmst.AC_DCPS_MAINTAINED_BY in (700174,700240,700241,700242) and userpost.ACTIVATE_FLAG = 1 and post.ACTIVATE_FLAG = 1 ");
		} 
		if(isDeputation.equals("Y"))
		{
			strQuery.append(" and empmst.ddo_code is null ");
			strQuery.append(" and ((empmst.DEPT_DDO_CODE ='"+strDDOCode+"'  and '"+strDDOCode+"'  ");
			strQuery.append(" in (SELECT hodddo.ddo_code FROM mst_dcps_Emp emp  ");
			strQuery.append(" inner join cmn_location_mst loc on loc.loc_id=emp.PARENT_DEPT  ");
			strQuery.append(" inner join ACL_HODDDO_RLT hodddo on hodddo.HOD_LOC_ID=loc.PARENT_LOC_ID  where emp.sevarth_id='"+txtSearch.trim().toUpperCase()+"') ) or ( empmst.DEPT_DDO_CODE is null )) ");
		}

		if (flag.equals("sevarthId")) {
			strQuery.append(" and empmst.SEVARTH_ID='" + txtSearch.trim().toUpperCase() + "' ");
		}
		if (flag.equals("dsgn")) {
			strQuery.append(" and desig.DSGN_ID  = '" + txtSearch.trim().toUpperCase() + "' ");
		}
		strQuery.append(" group by empmst.SEVARTH_ID,empmst.EMP_NAME,org.EMP_DOJ,desig.DSGN_NAME, empmst.DDO_CODE,empmst.DCPS_ID  ");

		this.logger.info((Object)("Query to get Emp List For Frm S1 Edit is " + strQuery.toString()));
		SQLQuery query = hibSession.createSQLQuery(strQuery.toString());
		return query.list();
	}

	/*    public String checkEmpListForFrmS1Dep(String strDDOCode, String flag, String txtSearch, String isDeputation) {

    	this.logger.info((Object)("in checkEmpListForFrmS1Dep" + txtSearch));

        org.hibernate.Session hibSession = this.getSession();
		List empLst = null;

		String empCountLst=null;

        StringBuffer strQuery = new StringBuffer();
        strQuery.append(" select  nvl(frm.DEP_UPDATED_DDO_CD,0)  ");
        strQuery.append(" from MST_DCPS_EMP empmst ");
        strQuery.append(" inner join org_emp_mst org on org.EMP_ID = empmst.ORG_EMP_MST_ID ");
        strQuery.append(" inner join ORG_USERPOST_RLT userpost on userpost.USER_ID=org.USER_ID ");
        strQuery.append(" inner join ORG_POST_MST post on post.POST_ID=userpost.POST_ID   ");
        strQuery.append(" inner join MST_DCPS_DDO_OFFICE mstddo on  empmst.CURR_OFF=mstddo.DCPS_DDO_OFFICE_MST_ID ");
        strQuery.append(" inner join ORG_DESIGNATION_MST desig on empmst.DESIGNATION=desig.DSGN_ID ");
        strQuery.append(" inner join frm_form_s1_dtls frm on empmst.sevarth_id=frm.sevarth_id ");
        strQuery.append(" where (empmst.EMP_SERVEND_DT > sysdate OR empmst.EMP_SERVEND_DT is null) "); 
        		if(isDeputation.equals("Y"))
        		{
        			if(depPrintDdoFlag.equals("Y")){
        			strQuery.append(" and frm.DEP_UPDATED_DDO_CD='"+strDDOCode+"' ");
        			}
        		}
        		else
        		{
        			strQuery.append(" and mstddo.DDO_CODE='"+strDDOCode+"' ");
        		}

        strQuery.append(" and empmst.DCPS_OR_GPF='Y' and empmst.AC_DCPS_MAINTAINED_BY in (700174,700240,700241,700242) and userpost.ACTIVATE_FLAG = 1 and post.ACTIVATE_FLAG = 1 ");
		if(isDeputation.equals("Y"))
		{
			strQuery.append(" and empmst.ddo_code is null ");
			strQuery.append(" and (empmst.DEPT_DDO_CODE='"+strDDOCode+"' OR empmst.DEPT_DDO_CODE is null) ");					

					strQuery.append(" and '"+strDDOCode+"' in (SELECT hodddo.ddo_code FROM mst_dcps_Emp emp  ");
					strQuery.append(" inner join cmn_location_mst loc on loc.loc_id=emp.PARENT_DEPT ");
					strQuery.append(" inner join ACL_HODDDO_RLT hodddo on hodddo.HOD_LOC_ID=loc.PARENT_LOC_ID ");
					strQuery.append(" where emp.sevarth_id='"+txtSearch.trim().toUpperCase()+"') ");

		}

        if (flag.equals("sevarthId")) {
            strQuery.append(" and empmst.SEVARTH_ID='" + txtSearch.trim().toUpperCase() + "' ");
        }
        if (flag.equals("dsgn")) {
            strQuery.append(" and desig.DSGN_ID  = '" + txtSearch.trim().toUpperCase() + "' ");
        }

        this.logger.info((Object)("Query to get checkEmpListForFrmS1Dep " + strQuery.toString()));
        SQLQuery query = hibSession.createSQLQuery(strQuery.toString());

		empLst = query.list();
		for(int i=0;i<empLst.size();i++)
			empCountLst=empLst.get(0).toString();

        return empCountLst;     

    }
	 */



	public List getEmpDesigList(String strDDOCode) {
		org.hibernate.Session hibSession = this.getSession();
		StringBuffer strQuery = new StringBuffer();
		strQuery.append(" select desig.DSGN_NAME,desig.DSGN_ID,mstddo.DDO_CODE ");
		strQuery.append(" from MST_DCPS_EMP empmst ");
		strQuery.append(" inner join org_emp_mst org on org.EMP_ID = empmst.ORG_EMP_MST_ID ");
		strQuery.append(" inner join ORG_USERPOST_RLT userpost on userpost.USER_ID=org.USER_ID ");
		strQuery.append(" inner join ORG_POST_MST post on post.POST_ID=userpost.POST_ID   ");
		strQuery.append(" inner join MST_DCPS_DDO_OFFICE mstddo on  empmst.CURR_OFF=mstddo.DCPS_DDO_OFFICE_MST_ID ");
		strQuery.append(" inner join ORG_DESIGNATION_MST desig on empmst.DESIGNATION=desig.DSGN_ID ");
		strQuery.append(" where mstddo.DDO_CODE='" + strDDOCode + "' and  (empmst.EMP_SERVEND_DT > sysdate OR empmst.EMP_SERVEND_DT is null) ");
		strQuery.append(" and empmst.DCPS_OR_GPF='Y' and empmst.AC_DCPS_MAINTAINED_BY in (700174,700240,700241,700242)  and userpost.ACTIVATE_FLAG = 1 and post.ACTIVATE_FLAG = 1 ");
		strQuery.append(" group by desig.DSGN_NAME,desig.DSGN_ID,mstddo.DDO_CODE ");
		strQuery.append(" order by desig.DSGN_NAME ");
		this.logger.info((Object)("Query to get Emp desig List For Frm S1 Edit is " + strQuery.toString()));
		SQLQuery query = hibSession.createSQLQuery(strQuery.toString());
		return query.list();
	}

	public String checkDDORegPresent(String ddo) {
		String ddoreg = "NO";
		StringBuilder sb = new StringBuilder();
		SQLQuery selectQuery = null;
		Date lDtCurrDate = SessionHelper.getCurDate();
		sb.append(" SELECT * FROM MST_DDO_REG where ddo_code=:ddo  ");
		selectQuery = this.ghibSession.createSQLQuery(sb.toString());
		selectQuery.setParameter("ddo", (Object)ddo.trim());
		List resultList = selectQuery.list();
		if (resultList != null && resultList.size() > 0) {
			ddoreg = "YES";
		}
		return ddoreg;
	}

	public boolean checkBranchAddress(String txtSevaarthId) {
		Boolean checkFlag = false;
		StringBuilder sb = new StringBuilder();
		SQLQuery selectQuery = null;
		Date lDtCurrDate = SessionHelper.getCurDate();
		sb.append(" SELECT count(1) FROM mst_dcps_emp emp inner join rlt_bank_branch_pay branch on branch.branch_id=emp.branch_name where emp.sevarth_id='" + txtSevaarthId + "' and (branch.NEW_ADDRESS is null or branch.PINCODE is null) ");
		selectQuery = this.ghibSession.createSQLQuery(sb.toString());
		this.logger.info((Object)("selectQuery is " + selectQuery.toString()));
		int resultListSize = selectQuery.list().indexOf(0);
		this.logger.info((Object)("resultListSize is " + resultListSize));
		if (resultListSize != 0) {
			checkFlag = true;
		}
		this.logger.info((Object)("checkFlag is " + checkFlag));
		return checkFlag;
	}

	public void deleteMultipleRecords(String sevaarthId) {
		org.hibernate.Session session = this.getSession();
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append(" DELETE FROM ");
		lSBQuery.append("(SELECT ROWNUMBER() OVER (PARTITION BY sevarth_id) AS RN,SEVARTH_ID ");
		lSBQuery.append(" FROM FRM_FORM_S1_DTLS where SEVARTH_ID = '" + sevaarthId + "' ");
		lSBQuery.append(" ) AS A WHERE RN > 1 ");
		SQLQuery lQuery = session.createSQLQuery(lSBQuery.toString());
		int status = lQuery.executeUpdate();
		this.logger.info((Object)("Query for deletion **** " + lSBQuery.toString()));
		this.logger.info((Object)("No of rows deleted for multiple entries " + status));
	}

	public String getDDOCode(String strLocationCode) {
		org.hibernate.Session hibSession = this.getSession();
		StringBuilder newQuery = new StringBuilder();
		String ddoCode = "";
		newQuery.append("SELECT DDO_CODE FROM org_ddo_mst where LOCATION_CODE = '" + strLocationCode + "' ");
		SQLQuery query = hibSession.createSQLQuery(newQuery.toString());
		ddoCode = query.uniqueResult().toString();
		return ddoCode;
	}


	public String  chkFrmUpdatedByLgnDdo(String sevarthId) {
		org.hibernate.Session hibSession = this.getSession();
		StringBuilder newQuery = new StringBuilder();
		String ddoCodee = "Z";
		List empLst = null;
		String [] empCountLst=null;
		newQuery.append(" SELECT nvl(DEP_UPDATED_DDO_CD,0) FROM frm_form_s1_dtls where sevarth_id='" + sevarthId + "' ");
		SQLQuery query = hibSession.createSQLQuery(newQuery.toString());
		//  gLogger.info("########Result is :"+query.uniqueResult().toString());

		empLst = query.list();
		//	empLst.get(0);
		//	empCountLst = new String[empLst.size()];
		//	for(int i=0;i<empLst.size();i++)
		//		empCountLst[i]=empLst.get(i).toString();


		if(empLst.size()>0) 
		{	if(!empLst.get(0).equals("0")){
			ddoCodee = query.uniqueResult().toString();
			gLogger.info("########Result is not 0 :"+query.uniqueResult().toString());
		}
		if(empLst.get(0).equals("0")){
			ddoCodee = "S";
			gLogger.info("########Result is 0 :"+query.uniqueResult().toString());
		}
		}
		return ddoCodee;
	}

    public String checkForPpan(String lStrSevaarthId) {
        String exist = "NoPpan";
        StringBuilder sb = new StringBuilder();
        SQLQuery selectQuery = null;
        sb.append(" SELECT * FROM mst_dcps_emp where ppan is not null  ");
        if (!"".equals(lStrSevaarthId) && lStrSevaarthId != null) {
            sb.append(" AND UPPER(SEVARTH_ID) = :lStrSevaarthId");
        }
        selectQuery = this.ghibSession.createSQLQuery(sb.toString());
        if (!"".equals(lStrSevaarthId) && lStrSevaarthId != null) {
            selectQuery.setParameter("lStrSevaarthId", (Object)lStrSevaarthId.trim());
        }
        List resultList = selectQuery.list();
        if (resultList != null && resultList.size() > 0) {
            exist = "PpanAvailable";
        }
        return exist;
    }
	
	
}