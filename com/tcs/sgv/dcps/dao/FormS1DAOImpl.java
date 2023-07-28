package com.tcs.sgv.dcps.dao;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.dao.FormS1DAO;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;

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

public class FormS1DAOImpl 
extends GenericDaoHibernateImpl
implements FormS1DAO {
	private final Log gLogger;
	org.hibernate.Session ghibSession;

	public FormS1DAOImpl(Class type, SessionFactory sessionFactory) {
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
		/*if (!"".equals(lStrDDOCode) && lStrDDOCode != null) 
		sb.append(" SELECT * FROM mst_dcps_emp where  DDO_CODE=:lStrDDOCode and reg_status=1 and form_status=1 and  (EMP_SERVEND_DT > sysdate OR EMP_SERVEND_DT is null)  ");
		else
		sb.append(" SELECT * FROM mst_dcps_emp where  dept_DDO_CODE=:lStrDDOCode and reg_status=1 and form_status=1 and  (EMP_SERVEND_DT > sysdate OR EMP_SERVEND_DT is null)  ");////$t9Feb22		
*/		
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
			lStrQuery.append(" SELECT upper(emp.emp_name),emp.gender,to_char(emp.dob,'dd/MM/yyyy'),upper(form.FATHER_NAME),substr(upper(form.PRESENT_ADD_FLAT_UNIT_NO_BLOCK_NO)||' '||upper(form.PRESENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE),1,30) , ");
			lStrQuery.append(" upper(form.PRESENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE) ,upper(form.PRESENT_ADDRESS_AREA_LOCALITY_TALUKA) ,  upper(form.PRESENT_ADDRESS_DISTRICT_TOWN_CITY),upper(form.PRESENT_ADDRESS_STATE_UNION_TERRITORY)||'', ");
			lStrQuery.append(" upper(form.PRESENT_ADDRESS_COUNTRY),form.PRESENT_ADDRESS_PIN_CODE,form.PHONE_NO_STD_CODE||form.PHONE_NO_PHONE_NO,form.MOBILE_NO,nvl(form.EMAIL_ID,' '),nvl(emp.bank_acnt_no,''),upper(nvl(bank.BANK_NAME,'')),upper(nvl(branch.BRANCH_NAME,'')), ");
			lStrQuery.append(" nvl(upper(branch.NEW_ADDRESS),' '),emp.IFSC_CODE,branch.MICR_CODE,emp.salutation,nvl(upper(emp.PAN_NO),' '),upper(form.PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO),upper(form.PERMANENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE),  ");
			lStrQuery.append(" upper(form.PERMANENT_ADDRESS_AREA_LOCALITY_TALUKA),upper(form.PERMANENT_ADDRESS_DISTRICT_TOWN_CITY),upper(form.PERMANENT_ADDRESS_STATE_UNION_TERRITORY),upper(form.PERMANENT_ADDRESS_COUNTRY),form.PERMANENT_ADDRESS_PIN_CODE,branch.PINCODE,nvl(emp.PRAN_NO,' '),upper(org.EMP_FNAME),upper(org.EMP_MNAME),upper(org.EMP_LNAME),org.EMP_FNAME||' '||org.EMP_MNAME||' '||org.EMP_LNAME,upper(form.mother_NAME),upper(form.city_of_birth),upper(form.country_of_birth),upper(form.martial_status),upper(form.spouse_NAME),upper(form.US_PERSON),upper(form.COUNTRY_OF_TAX),upper(form.ADDRESS_OF_TAX),upper(form.CITY_OF_TAX),upper(form.STATE_OF_TAX),upper(form.POST_CODE_OF_TAX),upper(form.TIN_OR_PAN),upper(form.TIN_COUNTRY), nvl(upper(substr(uid_no,9,4)),' '),CASE WHEN form.stage in(1,2,3) THEN 'Y' ELSE 'N' END,emp.emp_name_marathi, ");
			lStrQuery.append(" SUBSTR (emp.emp_name_marathi,1,INSTR( emp.emp_name_marathi,' ',1,1)-1),SUBSTR(emp.emp_name_marathi, INSTR(emp.emp_name_marathi,' ',1)+1,INSTR(emp.emp_name_marathi,' ',-1,1) - INSTR(emp.emp_name_marathi,' ',1,1)-1),SUBSTR(emp.emp_name_marathi,INSTR(emp.emp_name_marathi,' ',-1)+1), ");
			lStrQuery.append(" SUBSTR(form.FATHER_NAME, 1, INSTR(form.FATHER_NAME, ' ', 1, 1)-1),SUBSTR(form.FATHER_NAME,INSTR(form.FATHER_NAME, ' ', 1)+1,INSTR(form.FATHER_NAME, ' ', -1,1) - INSTR(form.FATHER_NAME, ' ', 1,1)-1),SUBSTR(form.FATHER_NAME, INSTR(form.FATHER_NAME, ' ', -1)+1), ");
			lStrQuery.append(" SUBSTR(form.mother_NAME, 1, INSTR(form.mother_NAME, ' ', 1, 1)-1),SUBSTR(form.mother_NAME,INSTR(form.mother_NAME, ' ', 1)+1,INSTR(form.mother_NAME, ' ', -1,1) - INSTR(form.mother_NAME, ' ', 1,1)-1),SUBSTR(form.mother_NAME, INSTR(form.mother_NAME, ' ', -1)+1) ");
			lStrQuery.append(" FROM mst_dcps_emp emp  inner join org_emp_mst org on org.EMP_Id = emp.ORG_EMP_MST_ID  ");//$t opgm watermark 9-10-20
			lStrQuery.append(" left join mst_bank_pay bank on bank.BANK_code=emp.BANK_NAME    ");
			lStrQuery.append(" left join RLT_BANK_BRANCH_PAY branch on branch.BRANCH_ID=emp.BRANCH_NAME   ");
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
				lStrQuery.append(" grd.GRADE_ID,month(emp.doj),year(emp.doj),reg.DDO_REG_NO,dto.DTO_REG_NO,nvl(emp.PRAN_NO,' '),emp.sevarth_id ");
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
				lStrQuery.append(" grd.GRADE_ID,month(doj),year(doj),reg.DDO_REG_NO,dto.DTO_REG_NO,nvl(emp.PRAN_NO,' '),emp.sevarth_id ");
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
	
 /* public List getFDDtls(String sevarthId1)
	  {
	    List lLstReturnList = null;
	    StringBuilder sb = new StringBuilder();
	    sb.append(" SELECT(reg.ASSOCIATED_DTO_REG_NO||'100'||'0000001')||'^^'||mst.Salutation||'^'||SUBSTR(mst.emp_name, 1, INSTR(mst.emp_name, ' ', 1, 1)-1)||'^'||SUBSTR(mst.emp_name,INSTR(mst.emp_name, ' ', 1)+1,INSTR(mst.emp_name, ' ', -1,1) - INSTR(mst.emp_name, ' ', 1,1)-1)||'^'||SUBSTR(mst.emp_name, INSTR(mst.emp_name, ' ', -1)+1)||'^'||SUBSTR(frm.father_name, 1, INSTR(frm.father_name, ' ', 1, 1)-1)||'^'||SUBSTR(frm.father_name,INSTR(frm.father_name, ' ', 1)+1,INSTR(frm.father_name, ' ', -1,1) - INSTR(frm.father_name, ' ', 1,1)-1)||'^'||SUBSTR(frm.father_name, INSTR(frm.father_name, ' ', -1)+1)||'^'||");
	    sb.append(" reg.ASSOCIATED_DTO_REG_NO||'^'||reg.DDO_REG_NO||'^'||mst.GENDER||'^'||TO_CHAR(mst.DOB,'MMDDYYYY')||'^'||mst.PAN_NO||'^'||frm.PRESENT_ADD_FLAT_UNIT_NO_BLOCK_NO||'^'||frm.PRESENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE||'^'||frm.PRESENT_ADDRESS_AREA_LOCALITY_TALUKA||'^'||frm.PRESENT_ADDRESS_DISTRICT_TOWN_CITY||'^^^'||'19'||'^'||'IN'||'^'||FRM.PRESENT_ADDRESS_PIN_CODE||'^'||FRM.PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO||'^'||frm.PERMANENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE||'^'||frm.PERMANENT_ADDRESS_AREA_LOCALITY_TALUKA||'^'||");
        sb.append(" frm.PERMANENT_ADDRESS_DISTRICT_TOWN_CITY||'^^^'||'19'||'^'||'IN'||'^'||frm.PERMANENT_ADDRESS_PIN_CODE||'^'||(frm.PHONE_NO_STD_CODE||frm.PHONE_NO_PHONE_NO)||'^'||substr(frm.MOBILE_NO,4)||'^^'||frm.EMAIL_ID||'^'||");
        sb.append(" CASE WHEN MOBILE_NO IS NOT NULL THEN 'Y' WHEN MOBILE_NO IS NULL THEN 'N' END ||'^'||'Y'||'^'||TO_CHAR(frm.DOJ,'MMDDYYYY')||'^'||TO_CHAR(mst.EMP_SERVEND_DT,'MMDDYYYY')||'^'||grd.GRADE_NAME||'^'||cmn.LOC_NAME||'^'||loc.LOC_NAME||'^'||reg.DDO_OFFICE_NAME||'^'||mst.PAYSCALE||'^'||");
        sb.append(" CASE WHEN mst.PAY_COMMISSION=700349 THEN mst.SEVEN_PC_BASIC ELSE mst.BASIC_PAY END ||'^'||mst.PPAN||'^'||'N'||'^'||mst.BANK_ACNT_NO||'^'||mst.BANK_NAME||'^'||mst.BRANCH_NAME||'^'||SUBSTR(mst.mother_name, 1, INSTR(mst.mother_name, ' ', 1, 1)-1)||'^^^'||'N'||'^^^^^^^'||mst.IFSC_CODE||'^'||'N'||'^^^^^^^^^'||'O'||'^'||'S'||'^^^'||'N'||'^'||'4'||'^'||'F'||'^'||'00' FROM MST_dCPS_EMP mst");
	    sb.append(" inner join FRM_FORM_S1_DTLS frm on mst.SEVARTH_ID=frm.SEVARTH_ID");
	    sb.append("	INNER join MST_DDO_REG reg on reg.ddo_code = mst.ddo_code");
	    sb.append("	INNER JOIN ORG_DDO_MST org on mst.DDO_CODE=org.DDO_CODE");
	    sb.append(" INNER JOIN CMN_LOCATION_MST cmn ON org.DEPT_LOC_CODE=cmn.LOC_ID");
	    sb.append(" INNER JOIN CMN_LOCATION_MST loc ON org.HOD_LOC_CODE=loc.LOC_ID");
	    sb.append(" inner join org_emp_mst oemp on oemp.EMP_ID = mst.ORG_EMP_MST_ID");
	    sb.append(" inner join org_grade_mst grd on grd.GRADE_ID=oemp.GRADE_ID where mst.SEVARTH_ID='"+sevarthId1+"' ");
	    Query selectQuery = this.ghibSession.createSQLQuery(sb.toString());
	    lLstReturnList = selectQuery.list();
	    
	    return lLstReturnList;
	  }*/
	
	//SUBSTR(frm.mother_name, 1, INSTR(frm.mother_name, ' ', 1, 1)-1)
	public List getFDDtls(String sevarthId1)
	  {
	    List lLstReturnList = null;
	    StringBuilder sb = new StringBuilder();
	    /*start OLD OPGM*/
	    
//	    sb.append(" SELECT '^'||CASE WHEN mst.SALUTATION=700075 THEN 'Shri.' WHEN mst.SALUTATION=700076 THEN 'Smt.' WHEN mst.SALUTATION=700077 THEN 'Kum.' END||'^'||SUBSTR(mst.emp_name, 1, INSTR(mst.emp_name, ' ', 1, 1)-1)||'^'||  SUBSTR(mst.emp_name, INSTR(mst.emp_name, ' ', -1)+1) ||'^'||");
//	    ////$t OPGM 15-7-2020 ^SURESH^^^
//	    sb.append(" SUBSTR(mst.emp_name,INSTR(mst.emp_name, ' ', 1)+1,INSTR(mst.emp_name, ' ', -1,1) - INSTR(mst.emp_name, ' ', 1,1)-1)||'^'||SUBSTR(frm.father_name, 1, INSTR(frm.father_name, ' ', 1, 1)-1)||'^'||SUBSTR(frm.father_name, INSTR(frm.father_name, ' ', -1)+1)||'^'||SUBSTR(frm.father_name,INSTR(frm.father_name, ' ', 1)+1,INSTR(frm.father_name, ' ', -1,1) - INSTR(frm.father_name, ' ', 1,1)-1)||'^'||");
//	    ////sb.append(" SUBSTR(mst.emp_name,INSTR(mst.emp_name, ' ', 1)+1,INSTR(mst.emp_name, ' ', -1,1) - INSTR(mst.emp_name, ' ', 1,1)-1)||'^'||case when(SUBSTR(frm.father_name,1,INSTR(frm.father_name,' ',1,1)-1)) IS NULL then frm.father_name else SUBSTR(frm.father_name,1,INSTR(frm.father_name,' ',1,1)-1) end||'^^^'||");
//	    sb.append(" reg.ASSOCIATED_DTO_REG_NO||'^'||reg.DDO_REG_NO||'^'||mst.GENDER||'^'||TO_CHAR(mst.DOB,'MMDDYYYY')||'^'||frm.TIN_OR_PAN||'^'||frm.PRESENT_ADD_FLAT_UNIT_NO_BLOCK_NO||'^'||frm.PRESENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE||'^'||frm.PRESENT_ADDRESS_AREA_LOCALITY_TALUKA||'^'||frm.PRESENT_ADDRESS_DISTRICT_TOWN_CITY||'^^^'||'19'||'^'||'IN'||'^'||FRM.PRESENT_ADDRESS_PIN_CODE||'^'||");
//	    sb.append(" FRM.PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO||'^'||frm.PERMANENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE||'^'||frm.PERMANENT_ADDRESS_AREA_LOCALITY_TALUKA||'^'||frm.PERMANENT_ADDRESS_DISTRICT_TOWN_CITY||'^^^'||'19'||'^'||'IN'||'^'||frm.PERMANENT_ADDRESS_PIN_CODE||'^'||(frm.PHONE_NO_STD_CODE||frm.PHONE_NO_PHONE_NO)||'^'||");
//	    sb.append(" substr(frm.MOBILE_NO,4)||'^^'||frm.EMAIL_ID||'^'||CASE WHEN MOBILE_NO IS NOT NULL THEN 'Y' WHEN MOBILE_NO IS NULL THEN 'N' END||'^'||'Y'||'^'||TO_CHAR(frm.DOJ,'MMDDYYYY')||'^'||TO_CHAR(mst.EMP_SERVEND_DT,'MMDDYYYY')||'^'||");
//	    sb.append(" case when grd.GRADE_NAME='BnGz' then 'B' else grd.GRADE_NAME end||'^'||substr(cmn.LOC_NAME,1,40)||'^'||substr(loc.LOC_NAME,1,40)||'^'||substr(reg.DDO_OFFICE_NAME,1,70)||'^'||mst.PAYSCALE||'^'||CASE WHEN mst.PAY_COMMISSION=700349 THEN mst.SEVEN_PC_BASIC ELSE mst.BASIC_PAY END ||'^'||mst.PPAN||'^'||'N'||'^'||'Savings'||'^'||");////$t 16-2-2021 substr(reg.DDO_OFFICE_NAME,1,70) substr(reg.DDO_OFFICE_NAME,1,70)
//	    sb.append(" mst.BANK_ACNT_NO||'^'||mst.BANK_NAME||'^'||substr(rlt.BRANCH_NAME,1,29)||'^'||rlt.NEW_ADDRESS||'^'||'19'||'^'||'IN'||'^'||rlt.PINCODE||'^^'||");
//	    sb.append(" CASE WHEN (frm.NOMINEE_1_NAME<>'NA' and frm.NOMINEE_2_NAME='NA' and frm.NOMINEE_3_NAME='NA') THEN 1 WHEN (frm.NOMINEE_1_NAME<>'NA' and frm.NOMINEE_2_NAME<>'NA' and frm.NOMINEE_3_NAME='NA') THEN 2");
//	    sb.append(" WHEN (frm.NOMINEE_1_NAME<>'NA' and frm.NOMINEE_2_NAME<>'NA' and frm.NOMINEE_3_NAME<>'NA') THEN 3 END||'^'||'000'||'^^^^^^'||SUBSTR(frm.mother_name, 1, INSTR(frm.mother_name, ' ', 1, 1)-1)||'^'||SUBSTR(frm.mother_name, INSTR(frm.mother_name, ' ', -1)+1)||'^'||SUBSTR(frm.mother_name,INSTR(frm.mother_name, ' ', 1)+1,INSTR(frm.mother_name, ' ', -1,1) - INSTR(frm.mother_name, ' ', 1,1)-1)||'^'||'N'||'^^^^^^^'||mst.IFSC_CODE||'^'||'N'||'^^^^^^^^^'||'O'||'^'||");
//	    sb.append("	'S'||'^^^'||'N'||'^'||'4'||'^'||'F'||'^'||'00'||'^^^^^^^'||'150'||'^^^^^^^^^^^^^^'||'RI'||'^^'||CASE WHEN frm.MARTIAL_STATUS='1' THEN 'M' WHEN frm.MARTIAL_STATUS='2' THEN 'U' WHEN frm.MARTIAL_STATUS='3' THEN 'O' END||'^^'||'Y'||'^'||'N'||'^^^'||frm.CITY_OF_BIRTH||'^'||'IN'||'^'||'C'||'^'||'C'||'^^'||'Y'||'^'||'RI'||'^'||'1'||'^'||'Y'||'^'||'N'||'^'||");		
//	    sb.append(" 'N' FROM MST_dCPS_EMP mst");
//	    sb.append(" inner join FRM_FORM_S1_DTLS frm on mst.SEVARTH_ID=frm.SEVARTH_ID");
//	    /*sb.append(" INNER join MST_DDO_REG reg on reg.ddo_code = mst.dept_ddo_code");///$t deptopgm
//	    sb.append(" INNER JOIN ORG_DDO_MST org on mst.dept_DDO_CODE=org.DDO_CODE");///$t deptopgm */
//        sb.append(" INNER join MST_DDO_REG reg on reg.ddo_code =(case when  frm.dep_updated_ddo_cd is not null then frm.dep_updated_ddo_cd else mst.DDO_CODE end) ");///$t deptopgm
//	    sb.append(" INNER JOIN ORG_DDO_MST org on  org.DDO_CODE=(case when  frm.dep_updated_ddo_cd is not null then frm.dep_updated_ddo_cd else mst.DDO_CODE end) ");////$t deptopgm
//	    sb.append(" INNER JOIN CMN_LOCATION_MST cmn ON org.DEPT_LOC_CODE=cmn.LOC_ID");
//	    sb.append(" INNER JOIN CMN_LOCATION_MST loc ON org.HOD_LOC_CODE=loc.LOC_ID");
//	    sb.append(" inner join org_emp_mst oemp on oemp.EMP_ID = mst.ORG_EMP_MST_ID");
//	    sb.append(" inner join org_grade_mst grd on grd.GRADE_ID=oemp.GRADE_ID");
//	    sb.append(" INNER JOIN RLT_BANK_BRANCH_PAY rlt ON mst.BRANCH_NAME=rlt.BRANCH_ID");
//	    sb.append(" INNER JOIN MST_BANK_PAY pay ON RLT.BANK_CODE=PAY.BANK_CODE where mst.SEVARTH_ID='"+sevarthId1+"' and mst.pran_no is null ");////OPGM 1-3-2021 mst.pran_no is null
//	    
	    /* End OLD OPGM*/
	    /* JG 04-jan-2022 */
	    sb.append("SELECT "); 
	    sb.append("'^'||CASE WHEN mst.SALUTATION=700075 THEN 'Shri.' WHEN mst.SALUTATION=700076 THEN 'Smt.' WHEN mst.SALUTATION=700077 THEN 'Kum.' END||'^'||SUBSTR "); 
	    sb.append("(mst.emp_name,1,INSTR(mst.emp_name,' ',1,1)-1)||'^'|| SUBSTR(mst.emp_name,INSTR(mst.emp_name,' ',-1)+1) "); 
	    sb.append("||'^'|| SUBSTR(mst.emp_name,INSTR(mst.emp_name,' ',1)+1,INSTR(mst.emp_name,' ',-1,1) - INSTR(mst.emp_name,' ',1,1)-1) "); 
	    sb.append("||'^'||CASE WHEN frm.father_name='NA' THEN '^^' ELSE SUBSTR(frm.father_name,1,INSTR(frm.father_name,' ',1,1)-1)||'^'||SUBSTR(frm.father_name,INSTR(frm.father_name,' ',-1)+1) "); 
	    sb.append("||'^'||SUBSTR(frm.father_name,INSTR(frm.father_name,' ',1)+1,INSTR(frm.father_name,' ',-1,1)- INSTR(frm.father_name,' ',1,1)-1) end"); //jituadd
	    sb.append("||'^'|| reg.ASSOCIATED_DTO_REG_NO||'^'||reg.DDO_REG_NO||'^'||mst.GENDER||'^'||TO_CHAR(mst.DOB,'MMDDYYYY') "); 
	    sb.append("||'^'||frm.TIN_OR_PAN||'^^^^^^^^^^'||FRM.PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO||'^'||frm.PERMANENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE "); 
	    sb.append("||'^'||frm.PERMANENT_ADDRESS_AREA_LOCALITY_TALUKA||'^'||frm.PERMANENT_ADDRESS_DISTRICT_TOWN_CITY||'^^^'||'19'||'^'||'IN' "); 
	    sb.append("||'^'||frm.PERMANENT_ADDRESS_PIN_CODE||'^'||(frm.PHONE_NO_STD_CODE||frm.PHONE_NO_PHONE_NO) "); 
	    sb.append("||'^'||substr(frm.MOBILE_NO,4)||'^^'||frm.EMAIL_ID||'^'||CASE WHEN MOBILE_NO IS NOT NULL THEN 'Y' WHEN MOBILE_NO IS NULL THEN 'N' END "); 
	    sb.append("||'^'||'Y'||'^'||TO_CHAR(frm.DOJ,'MMDDYYYY')||'^'||TO_CHAR(mst.EMP_SERVEND_DT,'MMDDYYYY') "); 
	    sb.append("||'^^'||substr(cmn.LOC_NAME,1,40) "); 
	    sb.append("||'^'||substr(loc.LOC_NAME,1,40)||'^'||substr(reg.DDO_OFFICE_NAME,1,70) "); 
	    sb.append("||'^^^'||mst.PPAN||'^'||'N'||'^'||'Savings'||'^'|| mst.BANK_ACNT_NO||'^'||mst.BANK_NAME||'^^^^^^^'|| "); 
	    sb.append("CASE WHEN(frm.NOMINEE_1_NAME<>'NA' and frm.NOMINEE_2_NAME='NA' and frm.NOMINEE_3_NAME='NA') "); 
	    sb.append("THEN 1 WHEN(frm.NOMINEE_1_NAME<>'NA' and frm.NOMINEE_2_NAME<>'NA' and frm.NOMINEE_3_NAME='NA') "); 
	    sb.append("THEN 2 WHEN(frm.NOMINEE_1_NAME<>'NA' and frm.NOMINEE_2_NAME<>'NA' and frm.NOMINEE_3_NAME<>'NA') "); 
	    sb.append("THEN 3 END||'^'||'000'||'^^^^^^'||CASE WHEN frm.mother_name='NA' THEN '^^' ELSE SUBSTR( frm.mother_name,1,INSTR(frm.mother_name,' ',1,1)-1) "); 
	    sb.append("||'^'||SUBSTR(frm.mother_name,INSTR(frm.mother_name,' ',-1)+1)||'^'|| "); 
	    sb.append("SUBSTR(frm.mother_name,INSTR(frm.mother_name,' ',1)+1,INSTR(frm.mother_name,' ',-1,1)- INSTR(frm.mother_name,' ',1,1)-1) end "); 
	    sb.append("||'^'||'N'||'^^^^^^^'||mst.IFSC_CODE||'^'||'N'||'^'|| "); 
	    sb.append("substr(mst.UID_NO,9,4) "); 
	    sb.append("||'^^^^^^^^^'||'S'||'^^^'||'N'||'^'||'4'||'^'||CASE WHEN frm.ORPHAN='1' THEN 'N' ELSE 'F' END||'^^^^^^^^^^^^^^^^^^' "); 
	    sb.append("||'150'||'^^^^^^'|| "); 
	    sb.append("CASE WHEN frm.MARTIAL_STATUS='1' THEN 'M' WHEN frm.MARTIAL_STATUS='2' THEN 'U' WHEN frm.MARTIAL_STATUS='3' THEN 'W' WHEN frm.MARTIAL_STATUS='4' THEN 'D' END||'^^'||'Y'||'^'||'N'||'^^^'|| "); 
	    sb.append("frm.CITY_OF_BIRTH||'^'||'IN'||'^^^^'||'Y'||'^'||'IN'||'^'||'1'||'^'||'Y'||'^^'||'N' "); 
	    sb.append("||'^'||'N'||'^^^^^^^'||'FU'||'^^^'||'Y'||'^'||'Y'||'^^'||'Y'||'^^'||CASE WHEN frm.ORPHAN = 1 THEN 'Y' ELSE 'N' end||'^^^^'||'I'||'^^^^'|| "); //for orphan
	    sb.append("SUBSTR "); 
	    sb.append("(frm.SPOUSE_NAME,1,INSTR(frm.SPOUSE_NAME,' ',1,1)-1)||'^'|| SUBSTR(frm.SPOUSE_NAME,INSTR(frm.SPOUSE_NAME,' ',-1)+1) "); 
	    sb.append("||'^'|| SUBSTR(frm.SPOUSE_NAME,INSTR(frm.SPOUSE_NAME,' ',1)+1,INSTR(frm.SPOUSE_NAME,' ',-1,1) - INSTR(frm.SPOUSE_NAME,' ',1,1)-1) "); 
	    sb.append("||'^'||'CG'||'^^^^^' "); 
	    sb.append("FROM MST_dCPS_EMP mst "); 
	    sb.append("inner join FRM_FORM_S1_DTLS frm on mst.SEVARTH_ID=frm.SEVARTH_ID "); 
	    sb.append("INNER join MST_DDO_REG reg on reg.ddo_code = "); 
	    sb.append("( "); 
	    sb.append("   case when frm.dep_updated_ddo_cd is not null then frm.dep_updated_ddo_cd else mst.DDO_CODE end "); 
	    sb.append(") "); 
	    sb.append("INNER JOIN ORG_DDO_MST org on org.DDO_CODE= "); 
	    sb.append("( "); 
	    sb.append("   case when frm.dep_updated_ddo_cd is not null then frm.dep_updated_ddo_cd else mst.DDO_CODE end "); 
	    sb.append(") "); 
	    sb.append("INNER JOIN CMN_LOCATION_MST cmn ON org.DEPT_LOC_CODE=cmn.LOC_ID "); 
	    sb.append("INNER JOIN CMN_LOCATION_MST loc ON org.HOD_LOC_CODE=loc.LOC_ID "); 
	    sb.append("INNER JOIN org_emp_mst oemp on oemp.EMP_ID = mst.ORG_EMP_MST_ID "); 
	    sb.append("INNER JOIN org_grade_mst grd on grd.GRADE_ID=oemp.GRADE_ID "); 
	    sb.append("INNER JOIN RLT_BANK_BRANCH_PAY rlt ON mst.BRANCH_NAME=rlt.BRANCH_ID "); 
	    sb.append("INNER JOIN MST_BANK_PAY pay ON RLT.BANK_CODE=PAY.BANK_CODE ");
	    sb.append("where mst.SEVARTH_ID='"+sevarthId1+"' "); 
	    sb.append("and mst.pran_no is null "); 
	    /* End JG  04-jan-2022*/
	    Query selectQuery = this.ghibSession.createSQLQuery(sb.toString());
	    lLstReturnList = selectQuery.list();
	    
	    return lLstReturnList;
	  }
	
	public List getAssoDtoRegNo(String sevarthId1)
	  {
	    List lLstReturnList = null;
	    StringBuilder sb = new StringBuilder();
	    //sb.append("SELECT reg.ASSOCIATED_DTO_REG_NO FROM mst_Dcps_emp emp inner join  mst_ddo_reg reg  on emp.dept_ddo_code=reg.ddo_code where emp.SEVARTH_ID='"+sevarthId1+"' ");////$t deptopgm
	    sb.append(" SELECT reg.ASSOCIATED_DTO_REG_NO FROM mst_Dcps_emp emp inner join FRM_FORM_S1_DTLS frm on emp.SEVARTH_ID=frm.SEVARTH_ID "
	    		+ "INNER join MST_DDO_REG reg on reg.ddo_code =(case when  frm.dep_updated_ddo_cd is not null then frm.dep_updated_ddo_cd else emp.DDO_CODE end) where emp.SEVARTH_ID='"+sevarthId1+"' "); ////$t deptopgm
        Query selectQuery = this.ghibSession.createSQLQuery(sb.toString());
	    lLstReturnList = selectQuery.list();
	    
	    return lLstReturnList;
	  }
	
	public List getNDDtls1(String sevarthId1)
	  {
		List lLstReturnList = null;
	    StringBuilder sb = new StringBuilder();
	    
	    /*  start Naminee  details for  old OPGM file */
//	    sb.append(" SELECT '01'||'^'||'01'||'^'||case when(SUBSTR(frm.NOMINEE_1_NAME,1,INSTR(frm.NOMINEE_1_NAME,' ',1,1)-1)) IS NULL then frm.NOMINEE_1_NAME else SUBSTR(frm.NOMINEE_1_NAME,1,INSTR(frm.NOMINEE_1_NAME,' ',1,1)-1) end||'^^^'||");
//	    sb.append(" CASE WHEN frm.NOMINEE_1_GUARDIAN_NAME not in ('NA','Not Available') THEN TO_CHAR(frm.NOMINEE_1_DOB,'MMDDYYYY') END||'^'||cmn.lookup_name||'^'||");
//	    sb.append(" CASE WHEN frm.NOMINEE_1_GUARDIAN_NAME not in ('NA','Not Available') THEN 'Y' ELSE 'N' END||'^'||"); 
//	  //sb.append(" SUBSTR(frm.NOMINEE_1_GUARDIAN_NAME, 1, INSTR(frm.NOMINEE_1_GUARDIAN_NAME, ' ', 1, 1)-1)||'^^^'||");
//	    sb.append(" CASE WHEN frm.NOMINEE_1_GUARDIAN_NAME NOT in ('NA','Not Available') THEN SUBSTR(frm.NOMINEE_1_GUARDIAN_NAME, 1, INSTR(frm.NOMINEE_1_GUARDIAN_NAME, ' ', 1, 1)-1) ELSE NULL END||'^^^'||");
//	    sb.append(" FRM.NOMINEE_1_PERCENT_SHARE||'^^'||frm.PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO||'^^^^^^' FROM FRM_FORM_S1_DTLS frm");
//	    sb.append(" INNER JOIN CMN_LOOKUP_MST cmn on frm.NOMINEE_1_RELATIONSHIP=cmn.lookup_id WHERE frm.NOMINEE_1_NAME<>'NA' and SEVARTH_ID='"+sevarthId1+"' ");
	   
	    /* END  Naminee details for old OPGM file */
	    
	    /* start Naminee details for  new  OPGM file  JG 4-jan-2022*/
	    sb.append("SELECT "); 
	    sb.append("'01'||'^'||'01'||'^'|| "); 
	    sb.append("CASE WHEN(SUBSTR(frm.NOMINEE_1_NAME,1,INSTR(frm.NOMINEE_1_NAME,' ',1,1)-1)) "); 
	    sb.append("IS NULL THEN frm.NOMINEE_1_NAME ELSE SUBSTR(frm.NOMINEE_1_NAME,1,INSTR(frm.NOMINEE_1_NAME,' ',1,1)-1)end ||'^'|| "); 
	    sb.append("CASE WHEN(SUBSTR(frm.NOMINEE_1_NAME,INSTR(frm.NOMINEE_1_NAME,' ',-1)+1))is NULL then'' ELSE SUBSTR "); 
	    sb.append("(frm.NOMINEE_1_NAME,INSTR(frm.NOMINEE_1_NAME,' ',-1)+1)end ||'^'|| "); 
	    sb.append("CASE WHEN(SUBSTR(frm.NOMINEE_1_NAME,INSTR(frm.NOMINEE_1_NAME,' ',1)+1,INSTR(frm.NOMINEE_1_NAME,' ',-1,1)- INSTR(frm.NOMINEE_1_NAME,' ',1,1)-1)) "); 
	    sb.append("IS NULL THEN '' ELSE SUBSTR(frm.NOMINEE_1_NAME,INSTR(frm.NOMINEE_1_NAME,' ',1)+1,INSTR(frm.NOMINEE_1_NAME,' ',-1,1)- INSTR(frm.NOMINEE_1_NAME,' ',1,1)-1) "); 
	    sb.append("end ||'^'||TO_CHAR(frm.NOMINEE_1_DOB,'MMDDYYYY')||'^'||CASE WHEN  frm.NOMINEE_1_RELATIONSHIP=230051 THEN '01' "); 
	    sb.append(" WHEN  frm.NOMINEE_1_RELATIONSHIP=230050 THEN '02' "); 
	    sb.append(" WHEN frm.NOMINEE_1_RELATIONSHIP=230047 THEN '03' "); 
	    sb.append(" WHEN frm.NOMINEE_1_RELATIONSHIP=230048 THEN '04' "); 
	    sb.append(" WHEN frm.NOMINEE_1_RELATIONSHIP=230049 THEN '05' "); 
	    sb.append(" WHEN frm.NOMINEE_1_RELATIONSHIP=230232 THEN '06' "); 
	    sb.append(" WHEN frm.NOMINEE_1_RELATIONSHIP=10001198192 THEN '07' "); 
	    sb.append(" WHEN frm.NOMINEE_1_RELATIONSHIP=10001198464 THEN '08' "); 
	    sb.append(" WHEN frm.NOMINEE_1_RELATIONSHIP=10001198465 THEN '09' "); 
	    sb.append(" WHEN frm.NOMINEE_1_RELATIONSHIP=10001198466 THEN '10' ");
	    sb.append(" ELSE  '99' END||'^'||'NOR'||'^'|| "); 
	    sb.append("CASE WHEN frm.NOMINEE_1_GUARDIAN_NAME not in('NA','Not Available')THEN 'Y' ELSE 'N' END||'^'|| "); 
	    sb.append("CASE WHEN frm.NOMINEE_1_GUARDIAN_NAME='NA' THEN '^^' ELSE ( "); 
	    sb.append("CASE WHEN(SUBSTR( frm.NOMINEE_1_GUARDIAN_NAME,1,INSTR(frm.NOMINEE_1_GUARDIAN_NAME,' ',1,1)-1)) "); 
	    sb.append("IS NULL then frm.NOMINEE_1_GUARDIAN_NAME else SUBSTR(frm.NOMINEE_1_GUARDIAN_NAME,1,INSTR(frm.NOMINEE_1_GUARDIAN_NAME,' ',1,1)-1) "); 
	    sb.append("end ||'^'|| case when( SUBSTR(frm.NOMINEE_1_GUARDIAN_NAME,INSTR(frm.NOMINEE_1_GUARDIAN_NAME,' ',-1)+1)) "); 
	    sb.append("is NULL then'' ELSE SUBSTR(frm.NOMINEE_1_GUARDIAN_NAME,INSTR(frm.NOMINEE_1_GUARDIAN_NAME,' ',-1)+1) "); 
	    sb.append("end ||'^'|| case when(SUBSTR(frm.NOMINEE_1_GUARDIAN_NAME,INSTR(frm.NOMINEE_1_GUARDIAN_NAME,' ',1)+1, "); 
	    sb.append("INSTR ( frm.NOMINEE_1_GUARDIAN_NAME,' ',-1,1 )- INSTR ( frm.NOMINEE_1_GUARDIAN_NAME,' ',1,1)-1 )) "); 
	    sb.append("IS NULL then '' else SUBSTR( frm.NOMINEE_1_GUARDIAN_NAME,INSTR(frm.NOMINEE_1_GUARDIAN_NAME,' ',1)+1, "); 
	    sb.append("INSTR ( frm.NOMINEE_1_GUARDIAN_NAME,' ',-1,1 )- INSTR( frm.NOMINEE_1_GUARDIAN_NAME,' ',1,1)-1) "); 
	    sb.append("end) end ||'^'||FRM.NOMINEE_1_PERCENT_SHARE||'^^^^^^^^^'||Timestampdiff(256, Char(Timestamp(sysdate) - Timestamp(NOMINEE_1_DOB))) "); 
	    sb.append("FROM FRM_FORM_S1_DTLS frm ");
	    sb.append("INNER JOIN CMN_LOOKUP_MST cmn on frm.NOMINEE_1_RELATIONSHIP=cmn.lookup_id "); 
	    sb.append("WHERE frm.NOMINEE_1_NAME<>'NA' "); 
	    sb.append("and SEVARTH_ID='"+sevarthId1+"' ");
	    /* END Naminee details for  new  OPGM file  JG 4-jan-2022*/
	    
	    Query selectQuery = this.ghibSession.createSQLQuery(sb.toString());
	    lLstReturnList = selectQuery.list();
	    
	    return lLstReturnList;
	  }
	
	public List getNDDtls2(String sevarthId1)
	  {
		List lLstReturnList = null;
	    StringBuilder sb = new StringBuilder();
	    /*  start Naminee 2  details for  old OPGM file */
//	    sb.append(" SELECT '02'||'^'||'02'||'^'||case when(SUBSTR(frm.NOMINEE_2_NAME,1,INSTR(frm.NOMINEE_2_NAME,' ',1,1)-1)) IS NULL then frm.NOMINEE_2_NAME else SUBSTR(frm.NOMINEE_2_NAME,1,INSTR(frm.NOMINEE_2_NAME,' ',1,1)-1) end||'^^^'||");
//	    sb.append(" CASE WHEN frm.NOMINEE_2_GUARDIAN_NAME NOT in ('NA','Not Available') THEN TO_CHAR(frm.NOMINEE_2_DOB,'MMDDYYYY') END||'^'||cmn.lookup_name||'^'||");
//	    sb.append(" CASE WHEN frm.NOMINEE_2_GUARDIAN_NAME not in ('NA','Not Available') THEN 'Y' ELSE 'N' END||'^'||");
//	    //sb.append(" SUBSTR(frm.NOMINEE_2_GUARDIAN_NAME, 1, INSTR(frm.NOMINEE_2_GUARDIAN_NAME, ' ', 1, 1)-1)||'^^^'||"); 
//	    sb.append(" CASE WHEN frm.NOMINEE_2_GUARDIAN_NAME NOT in ('NA','Not Available') THEN SUBSTR(frm.NOMINEE_2_GUARDIAN_NAME, 1, INSTR(frm.NOMINEE_2_GUARDIAN_NAME, ' ', 1, 1)-1) ELSE NULL END||'^^^'||");
//	    sb.append(" FRM.NOMINEE_2_PERCENT_SHARE||'^^'||frm.PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO||'^^^^^^' FROM FRM_FORM_S1_DTLS frm");
//	    sb.append(" INNER JOIN CMN_LOOKUP_MST cmn on frm.NOMINEE_2_RELATIONSHIP=cmn.lookup_id WHERE frm.NOMINEE_2_NAME<>'NA' and SEVARTH_ID='"+sevarthId1+"' ");
	    
	    /*  END Naminee 2  details for  old OPGM file */
	    
	    /* Start Naminee details for  new  OPGM file  JG 4-jan-2022*/
	    sb.append("SELECT "); 
	    sb.append("'02'||'^'||'02'||'^'|| "); 
	    sb.append("CASE WHEN(SUBSTR(frm.NOMINEE_2_NAME,1,INSTR(frm.NOMINEE_2_NAME,' ',1,1)-1)) "); 
	    sb.append("IS NULL THEN frm.NOMINEE_2_NAME ELSE SUBSTR(frm.NOMINEE_2_NAME,1,INSTR(frm.NOMINEE_2_NAME,' ',1,1)-1)end ||'^'|| "); 
	    sb.append("CASE WHEN(SUBSTR(frm.NOMINEE_2_NAME,INSTR(frm.NOMINEE_2_NAME,' ',-1)+1))is NULL then'' ELSE SUBSTR "); 
	    sb.append("(frm.NOMINEE_2_NAME,INSTR(frm.NOMINEE_2_NAME,' ',-1)+1)end ||'^'|| "); 
	    sb.append("CASE WHEN(SUBSTR(frm.NOMINEE_2_NAME,INSTR(frm.NOMINEE_2_NAME,' ',1)+1,INSTR(frm.NOMINEE_2_NAME,' ',-1,1)- INSTR(frm.NOMINEE_2_NAME,' ',1,1)-1)) "); 
	    sb.append("IS NULL THEN '' ELSE SUBSTR(frm.NOMINEE_2_NAME,INSTR(frm.NOMINEE_2_NAME,' ',1)+1,INSTR(frm.NOMINEE_2_NAME,' ',-1,1)- INSTR(frm.NOMINEE_2_NAME,' ',1,1)-1) "); 
	    sb.append("end ||'^'||TO_CHAR(frm.NOMINEE_2_DOB,'MMDDYYYY')||'^'||CASE WHEN  frm.NOMINEE_2_RELATIONSHIP=230051 THEN '01' "); 
	    sb.append(" WHEN  frm.NOMINEE_2_RELATIONSHIP=230050 THEN '02' "); 
	    sb.append(" WHEN frm.NOMINEE_2_RELATIONSHIP=230047 THEN '03' "); 
	    sb.append(" WHEN frm.NOMINEE_2_RELATIONSHIP=230048 THEN '04' "); 
	    sb.append(" WHEN frm.NOMINEE_2_RELATIONSHIP=230049 THEN '05' "); 
	    sb.append(" WHEN frm.NOMINEE_2_RELATIONSHIP=230232 THEN '06' "); 
	    sb.append(" WHEN frm.NOMINEE_2_RELATIONSHIP=10001198192 THEN '07' ");
	    sb.append(" WHEN frm.NOMINEE_2_RELATIONSHIP=10001198464 THEN '08' "); 
	    sb.append(" WHEN frm.NOMINEE_2_RELATIONSHIP=10001198465 THEN '09' "); 
	    sb.append(" WHEN frm.NOMINEE_2_RELATIONSHIP=10001198466 THEN '10' ");
	    sb.append(" ELSE  '99' END||'^'||'NOR'||'^'|| "); 
	    sb.append("CASE WHEN frm.NOMINEE_2_GUARDIAN_NAME not in('NA','Not Available')THEN 'Y' ELSE 'N' END||'^'|| "); 
	    sb.append("CASE WHEN frm.NOMINEE_2_GUARDIAN_NAME='NA' THEN '^^' ELSE ( "); 
	    sb.append("CASE WHEN(SUBSTR( frm.NOMINEE_2_GUARDIAN_NAME,1,INSTR(frm.NOMINEE_2_GUARDIAN_NAME,' ',1,1)-1)) "); 
	    sb.append("IS NULL then frm.NOMINEE_2_GUARDIAN_NAME else SUBSTR(frm.NOMINEE_2_GUARDIAN_NAME,1,INSTR(frm.NOMINEE_2_GUARDIAN_NAME,' ',1,1)-1) "); 
	    sb.append("end ||'^'|| case when( SUBSTR(frm.NOMINEE_2_GUARDIAN_NAME,INSTR(frm.NOMINEE_2_GUARDIAN_NAME,' ',-1)+1)) "); 
	    sb.append("is NULL then'' ELSE SUBSTR(frm.NOMINEE_2_GUARDIAN_NAME,INSTR(frm.NOMINEE_2_GUARDIAN_NAME,' ',-1)+1) "); 
	    sb.append("end ||'^'|| case when(SUBSTR(frm.NOMINEE_2_GUARDIAN_NAME,INSTR(frm.NOMINEE_2_GUARDIAN_NAME,' ',1)+1, "); 
	    sb.append("INSTR ( frm.NOMINEE_2_GUARDIAN_NAME,' ',-1,1 )- INSTR ( frm.NOMINEE_2_GUARDIAN_NAME,' ',1,1)-1 )) "); 
	    sb.append("IS NULL then '' else SUBSTR( frm.NOMINEE_2_GUARDIAN_NAME,INSTR(frm.NOMINEE_2_GUARDIAN_NAME,' ',1)+1, "); 
	    sb.append("INSTR ( frm.NOMINEE_2_GUARDIAN_NAME,' ',-1,1 )- INSTR( frm.NOMINEE_2_GUARDIAN_NAME,' ',1,1)-1) "); 
	    sb.append("end) end ||'^'||FRM.NOMINEE_2_PERCENT_SHARE||'^^^^^^^^^'||Timestampdiff(256, Char(Timestamp(sysdate) - Timestamp(NOMINEE_2_DOB))) "); 
	    sb.append("FROM FRM_FORM_S1_DTLS frm "); 
	    sb.append("INNER JOIN CMN_LOOKUP_MST cmn on frm.NOMINEE_2_RELATIONSHIP=cmn.lookup_id "); 
	    sb.append("WHERE frm.NOMINEE_2_NAME<>'NA' "); 
	    sb.append("and SEVARTH_ID='"+sevarthId1+"' ");
	    
	    /* END Naminee details for  new  OPGM file  JG 4-jan-2022*/
	    
	    Query selectQuery = this.ghibSession.createSQLQuery(sb.toString());
	    lLstReturnList = selectQuery.list();
	 
	    return lLstReturnList;
	  }
	
	public List getNDDtls3(String sevarthId1)
	  {
		List lLstReturnList = null;
	    StringBuilder sb = new StringBuilder();
	    /*  Start Naminee 3  details for  old OPGM file */
//	    sb.append(" SELECT '03'||'^'||'03'||'^'||case when(SUBSTR(frm.NOMINEE_3_NAME,1,INSTR(frm.NOMINEE_3_NAME,' ',1,1)-1)) IS NULL then frm.NOMINEE_3_NAME else SUBSTR(frm.NOMINEE_3_NAME,1,INSTR(frm.NOMINEE_3_NAME,' ',1,1)-1) end||'^^^'||");
//	    sb.append(" CASE WHEN frm.NOMINEE_3_GUARDIAN_NAME NOT in ('NA','Not Available') THEN TO_CHAR(frm.NOMINEE_3_DOB,'MMDDYYYY') END||'^'||cmn.lookup_name||'^'||");
//	    sb.append(" CASE WHEN frm.NOMINEE_3_GUARDIAN_NAME not in ('NA','Not Available') THEN 'Y' ELSE 'N' END||'^'||"); 
//	  //sb.append(" SUBSTR(frm.NOMINEE_3_GUARDIAN_NAME, 1, INSTR(frm.NOMINEE_3_GUARDIAN_NAME, ' ', 1, 1)-1)||'^^^'||");
//	    sb.append(" CASE WHEN frm.NOMINEE_3_GUARDIAN_NAME NOT in ('NA','Not Available') THEN SUBSTR(frm.NOMINEE_3_GUARDIAN_NAME, 1, INSTR(frm.NOMINEE_3_GUARDIAN_NAME, ' ', 1, 1)-1) ELSE NULL END||'^^^'||");
//	    sb.append(" FRM.NOMINEE_3_PERCENT_SHARE||'^^'||frm.PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO||'^^^^^^' FROM FRM_FORM_S1_DTLS frm");
//	    sb.append(" INNER JOIN CMN_LOOKUP_MST cmn on frm.NOMINEE_3_RELATIONSHIP=cmn.lookup_id WHERE frm.NOMINEE_3_NAME<>'NA' and SEVARTH_ID='"+sevarthId1+"' ");
	    
	    /*  END Naminee 3  details for  old OPGM file */
	    
	    /* Start Naminee 3 details for  new  OPGM file  JG 4-jan-2022*/
	    
	    sb.append("SELECT "); 
	    sb.append("'03'||'^'||'03'||'^'|| "); 
	    sb.append("CASE WHEN(SUBSTR(frm.NOMINEE_3_NAME,1,INSTR(frm.NOMINEE_3_NAME,' ',1,1)-1)) "); 
	    sb.append("IS NULL THEN frm.NOMINEE_3_NAME ELSE SUBSTR(frm.NOMINEE_3_NAME,1,INSTR(frm.NOMINEE_3_NAME,' ',1,1)-1)end ||'^'|| "); 
	    sb.append("CASE WHEN(SUBSTR(frm.NOMINEE_3_NAME,INSTR(frm.NOMINEE_3_NAME,' ',-1)+1))is NULL then'' ELSE SUBSTR "); 
	    sb.append("(frm.NOMINEE_3_NAME,INSTR(frm.NOMINEE_3_NAME,' ',-1)+1)end ||'^'|| "); 
	    sb.append("CASE WHEN(SUBSTR(frm.NOMINEE_3_NAME,INSTR(frm.NOMINEE_3_NAME,' ',1)+1,INSTR(frm.NOMINEE_3_NAME,' ',-1,1)- INSTR(frm.NOMINEE_3_NAME,' ',1,1)-1)) "); 
	    sb.append("IS NULL THEN '' ELSE SUBSTR(frm.NOMINEE_3_NAME,INSTR(frm.NOMINEE_3_NAME,' ',1)+1,INSTR(frm.NOMINEE_3_NAME,' ',-1,1)- INSTR(frm.NOMINEE_3_NAME,' ',1,1)-1) "); 
	    sb.append("end ||'^'||TO_CHAR(frm.NOMINEE_3_DOB,'MMDDYYYY')||'^'||CASE WHEN  frm.NOMINEE_3_RELATIONSHIP=230051 THEN '01' "); 
	    sb.append(" WHEN  frm.NOMINEE_3_RELATIONSHIP=230050 THEN '02' "); 
	    sb.append(" WHEN frm.NOMINEE_3_RELATIONSHIP=230047 THEN '03' "); 
	    sb.append(" WHEN frm.NOMINEE_3_RELATIONSHIP=230048 THEN '04' "); 
	    sb.append(" WHEN frm.NOMINEE_3_RELATIONSHIP=230049 THEN '05' "); 
	    sb.append(" WHEN frm.NOMINEE_3_RELATIONSHIP=230232 THEN '06' "); 
	    sb.append(" WHEN frm.NOMINEE_3_RELATIONSHIP=10001198192 THEN '07' "); 
	    sb.append(" WHEN frm.NOMINEE_3_RELATIONSHIP=10001198464 THEN '08' "); 
	    sb.append(" WHEN frm.NOMINEE_3_RELATIONSHIP=10001198465 THEN '09' "); 
	    sb.append(" WHEN frm.NOMINEE_3_RELATIONSHIP=10001198466 THEN '10' ");
	    sb.append(" ELSE  '99' END||'^'||'NOR'||'^'|| "); 
	    sb.append("CASE WHEN frm.NOMINEE_3_GUARDIAN_NAME not in('NA','Not Available')THEN 'Y' ELSE 'N' END||'^'|| "); 
	    sb.append("CASE WHEN frm.NOMINEE_3_GUARDIAN_NAME='NA' THEN '^^' ELSE ( "); 
	    sb.append("CASE WHEN(SUBSTR( frm.NOMINEE_3_GUARDIAN_NAME,1,INSTR(frm.NOMINEE_3_GUARDIAN_NAME,' ',1,1)-1)) "); 
	    sb.append("IS NULL then frm.NOMINEE_3_GUARDIAN_NAME else SUBSTR(frm.NOMINEE_3_GUARDIAN_NAME,1,INSTR(frm.NOMINEE_3_GUARDIAN_NAME,' ',1,1)-1) "); 
	    sb.append("end ||'^'|| case when( SUBSTR(frm.NOMINEE_3_GUARDIAN_NAME,INSTR(frm.NOMINEE_3_GUARDIAN_NAME,' ',-1)+1)) "); 
	    sb.append("is NULL then'' ELSE SUBSTR(frm.NOMINEE_3_GUARDIAN_NAME,INSTR(frm.NOMINEE_3_GUARDIAN_NAME,' ',-1)+1) "); 
	    sb.append("end ||'^'|| case when(SUBSTR(frm.NOMINEE_3_GUARDIAN_NAME,INSTR(frm.NOMINEE_3_GUARDIAN_NAME,' ',1)+1, "); 
	    sb.append("INSTR ( frm.NOMINEE_3_GUARDIAN_NAME,' ',-1,1 )- INSTR ( frm.NOMINEE_3_GUARDIAN_NAME,' ',1,1)-1 )) "); 
	    sb.append("IS NULL then '' else SUBSTR( frm.NOMINEE_3_GUARDIAN_NAME,INSTR(frm.NOMINEE_3_GUARDIAN_NAME,' ',1)+1, "); 
	    sb.append("INSTR ( frm.NOMINEE_3_GUARDIAN_NAME,' ',-1,1 )- INSTR( frm.NOMINEE_3_GUARDIAN_NAME,' ',1,1)-1) "); 
	    sb.append("end) end ||'^'||FRM.NOMINEE_3_PERCENT_SHARE||'^^^^^^^^^'||Timestampdiff(256, Char(Timestamp(sysdate) - Timestamp(NOMINEE_3_DOB))) "); 
	    sb.append("FROM FRM_FORM_S1_DTLS frm "); 
	    sb.append("INNER JOIN CMN_LOOKUP_MST cmn on frm.NOMINEE_3_RELATIONSHIP=cmn.lookup_id "); 
	    sb.append("WHERE frm.NOMINEE_3_NAME<>'NA' "); 
	    sb.append("and SEVARTH_ID='"+sevarthId1+"' ");
	    
	    /* END Naminee3  details for  new  OPGM file  JG 4-jan-2022*/
	    Query selectQuery = this.ghibSession.createSQLQuery(sb.toString());
	    lLstReturnList = selectQuery.list();
	 
	    return lLstReturnList;
	  }
      
	public List getDDdtls(String sevarthId1)
	  {
		List lLstReturnList = null;
	    StringBuilder sb = new StringBuilder();
	    sb.append(" select '01'||'^'||'IN'||'^'||frm.PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO||'^'||frm.TIN_OR_PAN||'^'||'IN'||'^^'||frm.PERMANENT_ADDRESS_STATE_UNION_TERRITORY||'^'||frm.PERMANENT_ADDRESS_DISTRICT_TOWN_CITY||'^'||frm.POST_CODE_OF_TAX");///$t OPGM 30/7/20 mst.PINCODE ,mst.pan_no
	    sb.append(" from mst_dcps_emp mst join FRM_FORM_S1_DTLS frm on mst.SEVARTH_ID=frm.SEVARTH_ID WHERE mst.SEVARTH_ID='"+sevarthId1+"' ");
	    Query selectQuery = this.ghibSession.createSQLQuery(sb.toString());
	    lLstReturnList = selectQuery.list();
	 
	    return lLstReturnList;
	  }
	
	
	public List getSectionCDetails(String lStrSevaarthId) throws Exception {
		List lLstEmpNmn = null;
		SQLQuery hqlQuery = null;
		StringBuilder lStrQuery = new StringBuilder();
		org.hibernate.Session session = this.getSession();
		Long lLngDesigAll = -2L;
		Date lDtCurrDate = SessionHelper.getCurDate();
		try {
			lStrQuery.append("  SELECT upper(decode(form.NOMINEE_1_NAME,'NA',' ','',' ',form.NOMINEE_1_NAME)),nvl(to_char(form.NOMINEE_1_DOB,'dd/MM/yyyy'),' '), upper(decode(form.NOMINEE_1_NAME,'NA',' ','',' ',look1.LOOKUP_NAME)), (case when form.NOMINEE_1_PERCENT_SHARE !=' ' then form.NOMINEE_1_PERCENT_SHARE||'%' else ' ' end),upper(decode(form.NOMINEE_1_GUARDIAN_NAME,'NA',' ','',' ',form.NOMINEE_1_GUARDIAN_NAME))");
			lStrQuery.append("  ,nvl(form.NOMINEE_1_NOMINATION_INVALID_CONDITION,' '), upper(decode(form.NOMINEE_2_NAME,'NA',' ','',' ',form.NOMINEE_2_NAME)),nvl(to_char(form.NOMINEE_2_DOB,'dd/MM/yyyy'),' '), upper(decode(form.NOMINEE_2_NAME,'NA',' ','',' ',look2.LOOKUP_NAME)),(case when form.NOMINEE_2_PERCENT_SHARE !=' ' then form.NOMINEE_2_PERCENT_SHARE||'%' else ' ' end), ");
			lStrQuery.append(" upper(decode(form.NOMINEE_2_GUARDIAN_NAME,'NA',' ','',' ',form.NOMINEE_2_GUARDIAN_NAME)),nvl(form.NOMINEE_2_NOMINATION_INVALID_CONDITION,' '),upper(decode(form.NOMINEE_3_NAME,'NA',' ','',' ',form.NOMINEE_3_NAME)), nvl(to_char(form.NOMINEE_3_DOB,'dd/MM/yyyy'),' '),upper(decode(form.NOMINEE_3_NAME,'NA',' ','',' ',look3.LOOKUP_NAME)),(case when form.NOMINEE_3_PERCENT_SHARE !=' ' then form.NOMINEE_3_PERCENT_SHARE||'%' else ' ' end), upper(decode(form.NOMINEE_3_GUARDIAN_NAME,'NA',' ','',' ',form.NOMINEE_3_GUARDIAN_NAME)),nvl(form.NOMINEE_3_NOMINATION_INVALID_CONDITION,' ')  ");
			lStrQuery.append(" FROM mst_dcps_emp emp left outer join FRM_FORM_S1_DTLS form on form.DCPS_ID=emp.DCPS_ID  ");
			lStrQuery.append(" left outer join CMN_LOOKUP_MST look1 on cast(look1.LOOKUP_ID as varchar)=form.NOMINEE_1_RELATIONSHIP   ");
			lStrQuery.append(" left outer join CMN_LOOKUP_MST look2 on cast(look2.LOOKUP_ID as varchar)=form.NOMINEE_2_RELATIONSHIP   ");
			lStrQuery.append(" left outer join CMN_LOOKUP_MST look3 on cast(look3.LOOKUP_ID as varchar)=form.NOMINEE_3_RELATIONSHIP   ");
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

	@SuppressWarnings("null")
	public List getEmpListForFrmS1Edit(String strDDOCode, String flag, String txtSearch, String isDeputation,String locationId) {

		List empData = null;
		List empData1 = null;
		List empData2 = null;
		
		this.logger.info((Object)("in dao txtSearch ####" + txtSearch));
		org.hibernate.Session hibSession = this.getSession();
		if(strDDOCode==null || (isDeputation == null || isDeputation.equals(""))){////allow to regular and Treasury
		StringBuffer strQuery = new StringBuffer();
		strQuery.append(" select empmst.SEVARTH_ID,empmst.EMP_NAME ,VARCHAR_FORMAT(org.EMP_DOJ, 'dd/MM/yyyy') , ");
		strQuery.append(" desig.DSGN_NAME ,empmst.DDO_CODE,empmst.DCPS_ID,Case when frm.stage=-1 then 'REJECT' else 'ACTIVE' end,Case when frm.CREATED_DATE >= '2017-12-09' then 'D' else 'ND' end ,empmst.DDO_CODE,frm.IS_PRINT_CSRF,nvl(empmst.pran_no,'Y'),nvl(frm.opgm_id,'Y') ,empmst.pran_no,decode(frm.stage,'1','PRAN is not generated','2','PRAN is not generated','3','PRAN is generated through OPGM','','Manually generated PRAN','5','Manually generated PRAN'),frm.stage ");////$t OPGM
		strQuery.append(" from MST_DCPS_EMP empmst ");
		strQuery.append(" inner join org_emp_mst org on org.EMP_ID = empmst.ORG_EMP_MST_ID ");
		strQuery.append(" inner join ORG_USERPOST_RLT userpost on userpost.USER_ID=org.USER_ID ");
		strQuery.append(" inner join ORG_POST_MST post on post.POST_ID=userpost.POST_ID   ");
		strQuery.append(" inner join ORG_DESIGNATION_MST desig on empmst.DESIGNATION=desig.DSGN_ID ");
		strQuery.append(" inner join frm_form_s1_dtls frm on empmst.sevarth_id=frm.sevarth_id ");
		strQuery.append(" where (empmst.EMP_SERVEND_DT > sysdate OR empmst.EMP_SERVEND_DT is null) ");
		
		if(strDDOCode==null)//for Treasury Login $t OPGM
		{
			strQuery.append(" and frm.stage=2 and  substr(empmst.DDO_CODE, 1,2) = substr('"+locationId+"', 1,2)   and frm.DEP_UPDATED_DDO_CD is null ");//$t deptopgm and frm.DEP_UPDATED_DDO_CD is not null
		}else{
            strQuery.append(" and (frm.stage=1 or frm.stage=-1 or frm.stage=5 or frm.stage is null)  and empmst.DDO_CODE='"+strDDOCode+"'  and frm.DEP_UPDATED_DDO_CD is null ");
		}
		strQuery.append(" and empmst.DCPS_OR_GPF='Y' and empmst.AC_DCPS_MAINTAINED_BY in (700174,700240,700241,700242) and userpost.ACTIVATE_FLAG = 1 and post.ACTIVATE_FLAG = 1 ");
		
		if (flag.equals("sevarthId")) {
			strQuery.append(" and empmst.SEVARTH_ID='" + txtSearch.trim().toUpperCase() + "' ");
		}
		if (flag.equals("dsgn")) {
			strQuery.append(" and desig.DSGN_ID  = '" + txtSearch.trim().toUpperCase() + "' ");
		}
		if (flag.equals("T")) {
			strQuery.append(" and  substr(empmst.DDO_CODE, 1,4) ='"+txtSearch.trim()+"' " );
		}
		strQuery.append(" group by empmst.SEVARTH_ID,empmst.EMP_NAME,org.EMP_DOJ,desig.DSGN_NAME, empmst.DDO_CODE,empmst.DCPS_ID,frm.CREATED_DATE,frm.stage,frm.IS_PRINT_CSRF,empmst.pran_no,frm.OPGM_ID order by frm.stage ");

		this.logger.info((Object)("Query to get Emp List Regular For Frm S1 Edit is " + strQuery.toString()));
		SQLQuery query = hibSession.createSQLQuery(strQuery.toString());
		empData1 = query.list();
	}
		
		if(strDDOCode==null || isDeputation.equals("Y")){////allow to dept and Treasury
		StringBuffer strQuery1 = new StringBuffer();
		strQuery1.append(" select empmst.SEVARTH_ID,empmst.EMP_NAME ,VARCHAR_FORMAT(org.EMP_DOJ, 'dd/MM/yyyy') , ");
		strQuery1.append(" desig.DSGN_NAME ,empmst.DDO_CODE,empmst.DCPS_ID,Case when frm.stage=-1 then 'REJECT' else 'ACTIVE' end,Case when frm.CREATED_DATE >= '2017-12-09' then 'D' else 'ND' end ,empmst.DDO_CODE,frm.IS_PRINT_CSRF,nvl(empmst.pran_no,'Y'),nvl(frm.opgm_id,'Y') ,empmst.pran_no,decode(frm.stage,'1','PRAN is not generated','2','PRAN is not generated','3','PRAN is generated through OPGM','','Manually generated PRAN','5','Manually generated PRAN'),frm.stage ");////$t OPGM
		strQuery1.append(" from MST_DCPS_EMP empmst ");
		strQuery1.append(" inner join org_emp_mst org on org.EMP_ID = empmst.ORG_EMP_MST_ID ");
		strQuery1.append(" inner join ORG_USERPOST_RLT userpost on userpost.USER_ID=org.USER_ID ");
		strQuery1.append(" inner join ORG_POST_MST post on post.POST_ID=userpost.POST_ID   ");
		strQuery1.append(" inner join ORG_DESIGNATION_MST desig on empmst.DESIGNATION=desig.DSGN_ID ");
		strQuery1.append(" inner join frm_form_s1_dtls frm on empmst.sevarth_id=frm.sevarth_id ");
		strQuery1.append(" where (empmst.EMP_SERVEND_DT > sysdate OR empmst.EMP_SERVEND_DT is null) ");
		
			if(strDDOCode==null)//for Treasury Login $t OPGM
			{		
				strQuery1.append("  and frm.DEP_UPDATED_DDO_CD is not null  and empmst.DCPS_OR_GPF='Y' and empmst.AC_DCPS_MAINTAINED_BY in (700174,700240,700241,700242)  ");
				strQuery1.append("  and empmst.ddo_code is null ");
				strQuery1.append("  and frm.stage=2 ");
			}else{
				if(flag !=null && !flag.equals("")){
					strQuery1.append("  and frm.DEP_UPDATED_DDO_CD is not null  and empmst.DCPS_OR_GPF='Y' and empmst.AC_DCPS_MAINTAINED_BY in (700174,700240,700241,700242)  ");
					strQuery1.append(" and empmst.ddo_code is null ");
					strQuery1.append(" and ((empmst.DEPT_DDO_CODE ='"+strDDOCode+"'  and '"+strDDOCode+"'  ");
					strQuery1.append(" in (SELECT hodddo.ddo_code FROM mst_dcps_Emp emp  ");
					strQuery1.append(" inner join cmn_location_mst loc on loc.loc_id=emp.PARENT_DEPT  ");
					strQuery1.append(" inner join ACL_HODDDO_RLT hodddo on hodddo.HOD_LOC_ID=loc.PARENT_LOC_ID  where emp.sevarth_id='"+txtSearch.trim().toUpperCase()+"') ) or ( empmst.DEPT_DDO_CODE is null )) ");
					strQuery1.append(" and (frm.stage=1 or frm.stage=-1 or frm.stage=5 or frm.stage is null) ");
				}else{
					strQuery1.append("  and frm.DEP_UPDATED_DDO_CD is not null  and empmst.DCPS_OR_GPF='Y' and empmst.AC_DCPS_MAINTAINED_BY in (700174,700240,700241,700242)  ");
					strQuery1.append(" and empmst.ddo_code is null ");
					strQuery1.append(" and ((empmst.DEPT_DDO_CODE ='"+strDDOCode+"'  and '"+strDDOCode+"'  ");
					strQuery1.append(" in (SELECT hodddo.ddo_code FROM mst_dcps_Emp emp  ");
					strQuery1.append(" inner join cmn_location_mst loc on loc.loc_id=emp.PARENT_DEPT  ");
					strQuery1.append(" inner join ACL_HODDDO_RLT hodddo on hodddo.HOD_LOC_ID=loc.PARENT_LOC_ID )) or ( empmst.DEPT_DDO_CODE is null )) ");
				    strQuery1.append(" and (frm.stage=1 or frm.stage=-1 or frm.stage=5 or frm.stage is null) ");
				}
			}
			
		if (flag.equals("sevarthId")) {
			strQuery1.append(" and empmst.SEVARTH_ID='" + txtSearch.trim().toUpperCase() + "' ");
		}
		if (flag.equals("dsgn")) {
			strQuery1.append(" and desig.DSGN_ID  = '" + txtSearch.trim().toUpperCase() + "' ");
		}
		if (flag.equals("T")) {
			strQuery1.append(" and  substr(empmst.DDO_CODE, 1,4) ='"+txtSearch.trim()+"' " );
		}
		strQuery1.append(" group by empmst.SEVARTH_ID,empmst.EMP_NAME,org.EMP_DOJ,desig.DSGN_NAME, empmst.DDO_CODE,empmst.DCPS_ID,frm.CREATED_DATE,frm.stage,frm.IS_PRINT_CSRF,empmst.pran_no,frm.OPGM_ID  order by frm.stage ");
		
		this.logger.info((Object)("Query to get Emp List deputation For Frm S1 Edit is " + strQuery1.toString()));
		SQLQuery query1 = hibSession.createSQLQuery(strQuery1.toString());
		empData2 = query1.list();
	}
		if ((empData1 != null && empData1.size() > 0)
				|| (empData2 != null && empData2.size() > 0)) {
			empData = new ArrayList();
			if (empData1 != null && empData1.size() > 0) {
				empData.addAll(empData1);

			}
			if (empData2 != null && empData2.size() > 0) {
				empData.addAll(empData2);
			}
		}
		return  empData;
	}
	

	
	public void csrfFormforwardFormTO(String seva_id) {
		org.hibernate.Session session = this.getSession();
		StringBuilder lSBQuery = new StringBuilder();////$t OPGM
		lSBQuery.append("update FRM_FORM_S1_DTLS set STAGE =2 where SEVARTH_ID ='"+seva_id+"' and STAGE=1 ");////$t OPGM
		
		SQLQuery lQuery = session.createSQLQuery(lSBQuery.toString());
		int status = lQuery.executeUpdate();
		this.logger.info((Object)("Query for updation **** " + lSBQuery.toString()));
		this.logger.info((Object)("No of rows updated for multiple entries " + status));
		//return null;
	}

	
	  public void createTextFilesForCSRF (Class type, SessionFactory sessionFactory)
	  {
	    //super(type);
	    this.ghibSession = sessionFactory.getCurrentSession();
	    setSessionFactory(sessionFactory);
	  }

	  public String getDtoRegNo(String treasuryCode)
	  {
	    List temp = null;
	    String data = "";
	    StringBuilder Strbld = new StringBuilder();
	    
	    Strbld.append(" SELECT dto_reg_no FROM  MST_DTO_REG where substr(LOC_ID,1,2)=" + 
	      treasuryCode);
	    
	    SQLQuery lQuery = this.ghibSession.createSQLQuery(Strbld.toString());
	    this.logger.info("Query to getErrorData in  heaqder**********" + 
	      Strbld.toString());
	    
	    temp = lQuery.list();
	    this.logger.info("temp size" + temp.size());
	    if ((temp != null) && (temp.size() > 0) && 
	      (temp.get(0) != null)) {
	      data = temp.get(0).toString();
	    }
	    return data;
	  }
	  
	  /*public String getBatchData(String fileNumber)
	  {
	    String lLstReturnList = "";
	    StringBuilder sb = new StringBuilder();
	    
	    sb.append(" select SR_NO||'^'||HEADER_NAME||'^'||BH_NO||'^'||BH_COL2||'^'||BH_FIX_NO||'^'||BH_DATE||'^'||BH_BATCH_FIX_ID||'^^'||BH_DDO_COUNT||'^'||BH_PRAN_COUNT||'^'||BH_EMP_AMOUNT||'^'||BH_EMPLR_AMOUNT||'^^'||BH_TOTAL_AMT||'^' from NSDL_BH_dtls ");
	    sb.append(" where FILE_NAME='" + fileNumber + "' ");
	    
	    Query selectQuery = this.ghibSession.createSQLQuery(sb.toString());
	    lLstReturnList = selectQuery.uniqueResult().toString();
	    
	    return lLstReturnList;
	  }
	  
	  public List getDHData(String fileNumber)
	  {
	    List lLstReturnList = null;
	    StringBuilder sb = new StringBuilder();
	    sb.append(" SELECT SR_NO||'^'||HEADER_NAME||'^'||DH_NO||'^'||DH_COL2||'^'||DH_DDO_REG_NO||'^'||BH_SD_COUNT||'^'||DH_EMP_AMOUNT||'^'||DH_EMPLR_AMOUNT||'^^',DH_DDO_REG_NO FROM NSDL_DH_dtls ");
	    sb.append(" where FILE_NAME='" + fileNumber + "' order by SR_NO asc");
	    
	    Query selectQuery = this.ghibSession.createSQLQuery(sb.toString());
	    lLstReturnList = selectQuery.list();
	    
	    return lLstReturnList;
	  }
	  
	  public List getSDDtls(String fileNumber, String ddoRegNo)
	  {
	    List lLstReturnList = null;
	    StringBuilder sb = new StringBuilder();
	    sb.append(" SELECT SR_NO||'^'||HEADER_NAME||'^'||SD_NO||'^'||SD_NO_2||'^'||SD_NO_3||'^'||SD_PRAN_NO||'^'||SD_EMP_AMOUNT||'^'||SD_EMPLR_AMOUNT||'^'||'^'||SD_TOTAL_AMT||'^'||SD_STATUS||'^'||SD_REMARK||'^' FROM NSDL_SD_DTLS  ");
	    sb.append(" where   FILE_NAME='" + fileNumber + "'and DDO_REG_NO='" + 
	      ddoRegNo + "' order by SR_NO asc ");
	    
	    Query selectQuery = this.ghibSession.createSQLQuery(sb.toString());
	    lLstReturnList = selectQuery.list();
	    
	    return lLstReturnList;
	  }*/
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

	@Override
	public String createTextFilesForCSRF(String sevaarthId) {
		// TODO Auto-generated method stub
		return null;
	}

	

	
	/*@Override
	public List csrfFormforwardFormTO(String sevaarthid, String Dcpsid) {
		// TODO Auto-generated method stub
		return null;
	}*/
	
//	public String  chkFrmFormS1(String sevarthId) {
//		org.hibernate.Session hibSession = this.getSession();
//		StringBuilder newQuery = new StringBuilder();
//		String ddoCodee = "Z";
//		List empLst = null;
//		String [] empCountLst=null;
//		newQuery.append(" SELECT DEP_UPDATED_DDO_CD FROM frm_form_s1_dtls where sevarth_id='" + sevarthId + "' ");
//		SQLQuery query = hibSession.createSQLQuery(newQuery.toString());
//		//  gLogger.info("########Result is :"+query.uniqueResult().toString());
//
//		empLst = query.list();
//		//	empLst.get(0);
//		//	empCountLst = new String[empLst.size()];
//		//	for(int i=0;i<empLst.size();i++)
//		//		empCountLst[i]=empLst.get(i).toString();
//
//
//		if(empLst.size()>0) 
//		{	if(empLst.get(0) !=null){
//			ddoCodee = query.uniqueResult().toString();
//			gLogger.info("########Result is :"+query.uniqueResult().toString());
//		}
//		}
//		return ddoCodee;
//	}
	
	
	
	////$t 2020-2-2 emp file generated successfully OPGM 
	///this method not used ,stage=3 set in another method 
	public void removeEmpFromTo(String sevarthId1) {
		org.hibernate.Session session = this.getSession();
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("update FRM_FORM_S1_DTLS set STAGE='3' where SEVARTH_ID ='"+ sevarthId1 + "' and STAGE='2' "); 

		SQLQuery lQuery = session.createSQLQuery(lSBQuery.toString());
		lQuery.executeUpdate();
		this.logger.info((Object) ("Query for OPGM removeEmpFromTo **** " + lSBQuery.toString()));
	}
	//$t 2020-2-3 employee move to ddo OPGM
	public void rejectEmployeesFromTo(String sevarthId1,String reason) {
		org.hibernate.Session session = this.getSession();
		StringBuilder lSBQuery = new StringBuilder();

		 lSBQuery.append("insert into FRM_FORM_S1_DTLS_bkp ");
		 lSBQuery.append(" SELECT ");
		 lSBQuery.append(" * ");
		 lSBQuery.append(" FROM FRM_FORM_S1_DTLS ");
		 lSBQuery.append(" WHERE sevarth_id='"+sevarthId1+"' ");
		 SQLQuery lQuery = session.createSQLQuery(lSBQuery.toString());
		 int result=lQuery.executeUpdate();
		 
		 if ((result != 0) && (result > 0)) {
		 lSBQuery = new StringBuilder();
		 lSBQuery.append("update FRM_FORM_S1_DTLS_bkp set IS_PRINT_CSRF='"+reason+"' where SEVARTH_ID ='"+ sevarthId1 + "'  "); 
		 lQuery = session.createSQLQuery(lSBQuery.toString());
		 lQuery.executeUpdate();
		 this.logger.info((Object) ("update Query for OPGM rejectEmployeesFromTo **** " + lSBQuery.toString()));
		 
		lSBQuery = new StringBuilder();
		lSBQuery.append("delete from FRM_FORM_S1_DTLS  where SEVARTH_ID ='"+ sevarthId1 + "'  "); 
		lQuery = session.createSQLQuery(lSBQuery.toString());
		lQuery.executeUpdate();
		this.logger.info((Object) ("delete Query for OPGM rejectEmployeesFromTo **** " + lSBQuery.toString()));
		 }
	}

	/////$t OPGM 17-05-2020
	public void updateOpgmIdFrm(String sevarthId1, String fileNumber) {
		StringBuilder lSBQuery=new StringBuilder();
		lSBQuery.append(" UPDATE FRM_FORM_S1_DTLS  SET OPGM_ID ='"+ fileNumber + "',stage=3 where SEVARTH_ID ='"+ sevarthId1 + "' and STAGE='2' ");///$t opgm 8-10-20
		logger.info("lSBQuery-->"+lSBQuery);
		Query updateQuery = this.ghibSession.createSQLQuery(lSBQuery.toString());
		updateQuery.executeUpdate();
		this.logger.info("Query to updateOpgmIdFrm in FRM_FORM_S1_DTLS heaqder**********" + lSBQuery.toString());
	}
/////$t OPGM 16-7-2020
	public Long getGeneratedEmpCount(String gStrLocationCode) {
		String count =null;
		try {
			StringBuilder sb = new StringBuilder();
			SQLQuery selectQuery = null;
			//sb.append(" SELECT sum(EMPLOYEE_count)+1 FROM OPGM_FILE_DTLS where TREASURY='"+gStrLocationCode+"'  ");
			
			//sb.append(" select  (select GENERATED_ID from CMN_TABLE_SEQ_MST where TABLE_NAME='OPGM_FILE_DTLS' and LOCATION_CODE='"+gStrLocationCode+"'  )- nvl(sum( EMPLOYEE_count)+1,0) from OPGM_FILE_DTLS where TREASURY ='"+gStrLocationCode+"'  ");
			
			sb.append(" select  (select GENERATED_ID from CMN_TABLE_SEQ_MST where TABLE_NAME='OPGM_FILE_DTLS' "
					+ "and substr(LOCATION_CODE,1,2)='" + gStrLocationCode.substring(0, 2) + "' )- nvl(sum( EMPLOYEE_count),0) from OPGM_FILE_DTLS where substr(TREASURY,1,2)='" + gStrLocationCode.substring(0, 2) + "' ");
			
			selectQuery = this.ghibSession.createSQLQuery(sb.toString());
			List resultList = selectQuery.list();
			count= resultList.get(0).toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Long.parseLong(count);
	}

	public long getTodayGeneratedFileCount() {
		long val=0L;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
		StringBuilder sb = new StringBuilder();
		SQLQuery selectQuery = null;
		sb.append(" SELECT * FROM OPGM_FILE_DTLS where FILE_GENERATION_DATE like '"+dateFormat.format(date)+"%'  ");
		selectQuery = this.ghibSession.createSQLQuery(sb.toString());
		List resultList = selectQuery.list();
		//val=resultList.size();
		return resultList.size();
	}

	////$t OPGM 
	public void updateIsPrintCsrfFlag(String empFirstName,
			String empFatherName, String nmn1FirstName,String TinPan) {
		StringBuilder lSBQuery=new StringBuilder();
		lSBQuery.append(" UPDATE FRM_FORM_S1_DTLS  SET IS_Print_CSRF ='Y' where EMP_NAME like '"+ empFirstName + "%'"
						+ " and TIN_OR_PAN like '"+ TinPan + "%' and stage='1' ");
		logger.info("updateIsPrintCsrfFlag lSBQuery-->"+lSBQuery);
		Query updateQuery = this.ghibSession.createSQLQuery(lSBQuery.toString());
		updateQuery.executeUpdate();
		this.logger.info("Query to updateIsPrintCsrfFlag in FRM_FORM_S1_DTLS heaqder**********" + lSBQuery.toString());
	}

////$t OPGM 1-9-20
	public void updateFileStatusOpgm(String fileNumber) {
		StringBuilder lSBQuery=new StringBuilder();
		lSBQuery.append(" UPDATE OPGM_FILE_DTLS  SET FILE_STATUS ='-1' where FILE_ID='"+ fileNumber + "' ");
		logger.info(" updateFILE_STATUSFlag OPGM_FILE_DTLS lSBQuery-->"+lSBQuery);
		Query updateQuery = this.ghibSession.createSQLQuery(lSBQuery.toString());
		updateQuery.executeUpdate();
		this.logger.info("Query to updateFILE_STATUSFlag in OPGM_FILE_DTLS heaqder**********" + lSBQuery.toString());
	}

////$t OPGM 1-9-20 file transfer from OpgmFileViewDaoImpl	
	public String generateOpgmFile(String Treasury,int FileEmpCount) {
		
		/*
		////new code generated
		String Seq="";
		try{
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int month = Calendar.getInstance().get(Calendar.MONTH)+1;
		DecimalFormat df1 = new DecimalFormat("00");
	    DecimalFormat df2 = new DecimalFormat("0000");
	    String fileSeqCount=Long.toString(fileSeq);
	    
	    fileSeqCount=fileSeqCount.substring(fileSeqCount.length()-4, fileSeqCount.length());
	    
		Seq=Treasury+year+df1.format(month)+"00"+df2.format(Integer.parseInt(fileSeqCount));
	    ////$t OPGM
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append(" insert into OPGM_FILE_DTLS values( '"+Seq+"', '"+year+"', '"+month+"', '"+FileEmpCount+"',0,sysdate,'"+Treasury+"') ");
		
		SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lSBQuery = new StringBuilder();
		lQuery.executeUpdate();		
		}catch(Exception e){
			System.out.println("Exception e"+e);
            e.printStackTrace();
		}
		return Seq;*/
	
		String Seq="";
		try{
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int month = Calendar.getInstance().get(Calendar.MONTH)+1;
		DecimalFormat df1 = new DecimalFormat("00");
	    DecimalFormat df2 = new DecimalFormat("000");
	    
        //code for generating sequence
		String Count = "";
		StringBuilder Strbld = new StringBuilder();
		Strbld.append(" SELECT count(1)+1 FROM OPGM_FILE_DTLS where Treasury ='"+Treasury+"' and FILE_YEAR='"+year+"' "
				+ "and FILE_MONTH = '"+month+"'");
		
		SQLQuery lSEQQuery = this.ghibSession.createSQLQuery(Strbld.toString());
		Count = lSEQQuery.list().get(0).toString();
		Seq = Treasury + year + month + FileEmpCount + "00" + Count;
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append(" insert into OPGM_FILE_DTLS values( '"+Seq+"', '"+year+"', '"+month+"', '"+FileEmpCount+"',0,sysdate,'"+Treasury+"') ");
		
		SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lSBQuery = new StringBuilder();
		lQuery.executeUpdate();		
		}catch(Exception e){
			System.out.println("Exception e"+e);
            e.printStackTrace();
		}
		return Seq;
	}

	public void rejectEmployeesFromTo(String sevarthId) {
		org.hibernate.Session session = this.getSession();
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("update FRM_FORM_S1_DTLS set STAGE='3' where SEVARTH_ID ='"+ sevarthId + "' and STAGE='2' "); 

		SQLQuery lQuery = session.createSQLQuery(lSBQuery.toString());
		lQuery.executeUpdate();
		this.logger.info((Object) ("Query for OPGM removeEmpFromTo **** " + lSBQuery.toString()));
	}
	
}//end class
