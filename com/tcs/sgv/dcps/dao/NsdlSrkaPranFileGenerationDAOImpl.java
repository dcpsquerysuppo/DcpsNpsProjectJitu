package com.tcs.sgv.dcps.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import sun.org.mozilla.javascript.internal.regexp.SubString;

import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.util.DBConnection;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;


public class NsdlSrkaPranFileGenerationDAOImpl extends GenericDaoHibernateImpl implements NsdlSrkaPranFileGenerationDAO{

	private Session ghibSession = null;
	private final Logger gLogger = Logger.getLogger(getClass());

	public NsdlSrkaPranFileGenerationDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = sessionFactory.getCurrentSession();
	}

	public List getAllTreasuries() {

		ArrayList<ComboValuesVO> arrTreasury = new ArrayList<ComboValuesVO>();
		Connection lCon = null;
		PreparedStatement lStmt = null;
		ResultSet lRs = null;
		String treasury_id = null;
		String treasury_name = null;

		try {			
			StringBuffer lsb = new StringBuffer();
			lsb = new StringBuffer(
			"select CM.loc_Id , CM.loc_Name from Cmn_Location_Mst CM where department_Id in (100003) and CM.LOC_ID not in(9991,1111) and CM.LANG_ID = 1 order by CM.loc_id  ");//,1111111
																															//and CM.LOC_ID not in(9991,1111)
			lCon = DBConnection.getConnection();
			lStmt = lCon.prepareStatement(lsb.toString());
			lRs = lStmt.executeQuery();
			while (lRs.next()) {
				ComboValuesVO vo = new ComboValuesVO();
				treasury_id = lRs.getString("loc_Id");
				treasury_name = lRs.getString("loc_Name");
				vo.setId(treasury_id);
				vo.setDesc(treasury_id+"-"+treasury_name);
				arrTreasury.add(vo);
			}

		} catch (Exception e) {
			gLogger.info("Sql Exception:" + e, e);
		} finally {
			try {
				if (lStmt != null) {
					lStmt.close();
				}
				if (lRs != null) {
					lRs.close();
				}
				if (lCon != null) {
					lCon.close();
				}

				lStmt = null;
				lRs = null;
				lCon = null;
			} catch (Exception e) {				
				gLogger.info("Sql Exception:" + e, e);
			}
		}
		return arrTreasury;

	}

	@Override
	public List getBatch(String treasuryno) {
		
		List lstAis=null;
		List temp=null;
		
		StringBuilder  Strbld = new StringBuilder();
		String treasury=treasuryno.substring(0,2);
		Float count=null;
		
		
		  
		Strbld.append(" SELECT count(1) ");
		Strbld.append(" FROM FRM_FORM_S1_DTLS form  ");
		Strbld.append(" inner join mst_dcps_emp emp on emp.SEVARTH_ID=form.SEVARTH_ID  "); 
		Strbld.append(" left outer join HR_EIS_SCALE_MST scale on scale.SCALE_ID=emp.PAYSCALE  ");   
		Strbld.append(" INNER JOIN ORG_EMP_MST oremp on oremp.EMP_ID=emp.ORG_EMP_MST_ID   ");
		Strbld.append(" inner JOIN ORG_GRADE_MST grade on oremp.GRADE_ID=grade.GRADE_ID   ");
		Strbld.append(" left outer join MST_BANK_PAY pay on pay.BANK_CODE=emp.BANK_NAME   ");
		Strbld.append(" left outer join RLT_BANK_BRANCH_PAY br on emp.BRANCH_NAME=br.BRANCH_ID    ");
		Strbld.append(" inner join nsdl_Subscriber_Ack_id sub on sub.DCPS_EMP_ID = emp.DCPS_EMP_ID and sub.ACK_ID is not null "); 
		Strbld.append(" inner join org_ddo_mst oddo on oddo.DDO_CODE =form.DDO_CODE    ");
		Strbld.append(" inner join CMN_LOCATION_MST cmn on cmn.LOC_ID=oddo.DEPT_LOC_CODE   "); 
		Strbld.append(" inner join CMN_LOCATION_MST cmnloc on cmnloc.LOC_ID=oddo.HOD_LOC_CODE  ");  
		Strbld.append(" inner join MST_DTO_REG dto on substr(dto.dto_code,1,2)=substr(form.ddo_code,1,2) ");     
		Strbld.append(" inner join MST_DDO_REG ddo on ddo.ddo_code=form.DDO_CODE     ");
		Strbld.append(" where emp.nsdl_ppan is not null and emp.PRAN_NO is not null and substr(form.ddo_code,1,2) like '"+treasury+"%' and form.NSDL_STATUS is null ");  
		Strbld.append(" and (emp.emp_servend_dt > sysdate and emp.super_ann_date > sysdate) ");
		Strbld.append(" and (emp.ac_dcps_maintained_by=700174 or emp.ac_dcps_maintained_by is null) ");   
	/*	Strbld.append(" and emp.dcps_emp_id not in (SELECT distinct hst.dcps_emp_id FROM TRN_DCPS_CHANGES hst inner join mst_dcps_emp emp on emp.dcps_emp_id = hst.dcps_emp_id and hst.ddo_code = emp.ddo_code "); 
		Strbld.append(" inner join FRM_FORM_S1_DTLS form on emp.SEVARTH_ID = form.SEVARTH_ID where hst.TYPE_OF_CHANGES = 'PersonalDetails' and hst.FIELD_NAME = 'Full Name' and substr(hst.ddo_code,1,2) like '"+treasury+"%' and emp.reg_status = 1 and emp.form_status = 1 and form.NSDL_STATUS is null   ) ");*/ 



		
/*----------------------------------------------------------------------------------------------------------------------*/			
		//Strbld.append(" SELECT nvl(max(TREASURY_EMP_COUNT),0) as count FROM FRM_FORM_S1_DTLS where substr(ddo_code,1,2) like '"+treasury+"%' ");   
	   
		/*Strbld.append(" SELECT max(form.TREASURY_EMP_COUNT) ");*/
		    //Strbld.append("  form.NOMINEE_1_NAME,to_char(form.NOMINEE_1_DOB,'MMddyyyy'), ");
		//Strbld.append(" (SELECT cmn1.lookup_name||' ' FROM cmn_lookup_mst cmn1 where cmn1.lookup_id=(form.NOMINEE_1_RELATIONSHIP)), ");
		    //Strbld.append("	form.NOMINEE_1_GUARDIAN_NAME,form.NOMINEE_1_NOMINATION_INVALID_CONDITION,form.NOMINEE_2_NAME,to_char(form.NOMINEE_2_DOB,'MMddyyyy'), ");
		//Strbld.append("	(SELECT cmn.lookup_name||'  ' FROM cmn_lookup_mst cmn where cmn.lookup_id=(form.NOMINEE_2_RELATIONSHIP)), ");
		//   Strbld.append("	form.NOMINEE_2_GUARDIAN_NAME,form.NOMINEE_2_NOMINATION_INVALID_CONDITION,form.NOMINEE_3_NAME,to_char(form.NOMINEE_3_DOB,'MMddyyyy'), ");
		//Strbld.append("	(SELECT cmn3.lookup_name||' ' FROM cmn_lookup_mst cmn3 where cmn3.lookup_id=(form.NOMINEE_3_RELATIONSHIP)),form.NOMINEE_3_GUARDIAN_NAME,form.NOMINEE_3_NOMINATION_INVALID_CONDITION, ");
		//Strbld.append("	(SELECT cmn4.lookup_name||' ' FROM cmn_lookup_mst cmn4 where cmn4.lookup_id=(emp.SALUTATION)), ");
		/*Strbld.append(" FROM FRM_FORM_S1_DTLS form ");
		Strbld.append(" inner   join mst_dcps_emp emp on emp.SEVARTH_ID=form.SEVARTH_ID  left outer join HR_EIS_SCALE_MST scale on scale.SCALE_ID=emp.PAYSCALE    ");
		Strbld.append(" INNER JOIN ORG_EMP_MST oremp on oremp.EMP_ID=emp.ORG_EMP_MST_ID  ");
		Strbld.append(" inner JOIN ORG_GRADE_MST grade on oremp.GRADE_ID=grade.GRADE_ID  ");
		Strbld.append(" left outer join MST_BANK_PAY pay on pay.BANK_CODE=emp.BANK_NAME  ");
		Strbld.append(" left outer join RLT_BANK_BRANCH_PAY br on emp.BRANCH_NAME=br.BRANCH_ID ");  
		Strbld.append(" inner join nsdl_Subscriber_Ack_id sub on sub.DCPS_EMP_ID = emp.DCPS_EMP_ID and sub.ACK_ID is not null ");
		Strbld.append(" inner join org_ddo_mst oddo on oddo.DDO_CODE =form.DDO_CODE ");  
		Strbld.append(" inner join CMN_LOCATION_MST cmn on cmn.LOC_ID=oddo.DEPT_LOC_CODE ");  
		Strbld.append(" inner join CMN_LOCATION_MST cmnloc on cmnloc.LOC_ID=oddo.HOD_LOC_CODE ");  
		Strbld.append(" inner join MST_DTO_REG dto on substr(dto.dto_code,1,2)=substr(form.ddo_code,1,2) ");    
		Strbld.append(" inner join MST_DDO_REG ddo on ddo.ddo_code=form.DDO_CODE ");   
		Strbld.append(" where emp.PRAN_NO is not null and substr(form.ddo_code,1,2) like '"+treasury+"%' and form.NSDL_STATUS is null ");  
		Strbld.append(" and (emp.ac_dcps_maintained_by=700174 or emp.ac_dcps_maintained_by is null)  "); *//* and form.TREASURY_EMP_COUNT between "+string+" and "+string2+" */
		/*Strbld.append(" and emp.dcps_emp_id not in (SELECT distinct hst.dcps_emp_id FROM TRN_DCPS_CHANGES hst inner join mst_dcps_emp emp on emp.dcps_emp_id = hst.dcps_emp_id and hst.ddo_code = emp.ddo_code ");
		Strbld.append("	inner join FRM_FORM_S1_DTLS form on emp.SEVARTH_ID = form.SEVARTH_ID where hst.TYPE_OF_CHANGES = 'PersonalDetails' and hst.FIELD_NAME = 'Full Name' and substr(hst.ddo_code,1,2) like '"+treasury+"%' and emp.reg_status = 1 and emp.form_status = 1 ) "); *//* and form.TREASURY_EMP_COUNT between "+string+" and "+string2+" */
	//Strbld.append(" and form.TREASURY_EMP_COUNT between "+string+" and "+string2+" ");
		
		/*//Strbld.append(" SELECT count(1) FROM FRM_FORM_S1_DTLS form "); 
		Strbld.append(" SELECT count(1) ");
		//Strbld.append(" SELECT ROW_NUMBER() OVER (order by substr(form.ddo_code,1,2)) as rn,nvl(form.EMP_NAME,' '),nvl(form.FATHER_NAME,' '),to_char(emp.dob,'MMddyyyy'),to_char(emp.doj,'MMddyyyy'),nvl(emp.nsdl_ppan,' '),nvl(emp.pran_no,' '),nvl(emp.sevarth_id,' '),dto.DTO_reg_no,nvl(ddo.ddo_reg_no,' '),emp.gender,nvl(emp.PAN_NO,' '),nvl((form.PRESENT_ADD_FLAT_UNIT_NO_BLOCK_NO ||' '|| form.PRESENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE ||' '|| form.PRESENT_ADDRESS_AREA_LOCALITY_TALUKA),' ') as emp_address,nvl(emp.LOCALITY,' '), nvl(form.PRESENT_ADDRESS_DISTRICT_TOWN_CITY,' '),nvl(emp.STATE,' '), cast(nvl(form.PRESENT_ADDRESS_PIN_CODE,'0')as varchar),nvl((form.PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO ||' '|| form.PERMANENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE ||' '|| form.PERMANENT_ADDRESS_AREA_LOCALITY_TALUKA),' ') as pemp_address,nvl(emp.pLOCALITY,' '), nvl(form.PERMANENT_ADDRESS_DISTRICT_TOWN_CITY,' '),nvl(emp.pSTATE,' '), cast(nvl(form.PERMANENT_ADDRESS_PIN_CODE,'0')as varchar),nvl((form.PHONE_NO_STD_CODE||form.phone_no_phone_no),' '), nvl(form.mobile_no,' '),nvl(form.EMAIL_ID,' '),nvl(scale.SCALE_DESC,' '),cast(nvl(emp.BASIC_PAY,' ') as varchar),nvl(emp.nsdl_ppan,' '),nvl(form.PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO,' '),nvl(form.PERMANENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE,' '),to_char(emp.EMP_SERVEND_DT,'MMddyyyy'),to_char(emp.SUPER_ANN_DATE,'MMddyyyy'),nvl(ddo.DDO_OFFICE_NAME,' '),nvl(emp.BANK_ACNT_NO,' '),nvl(pay.BANK_NAME,' '),nvl(br.BRANCH_NAME,' '),nvl(br.BRANCH_ADDRESS,' '),cast(nvl(br.PINCODE,'0')as varchar),nvl(br.MICR_CODE,' '),nvl(br.IFSC_CODE,' '),nvl(grade.GRADE_name,' '),nvl(cmn.LOC_NAME,' ') as Admin_Dept,nvl(cmnloc.LOC_NAME,' ') as Field_dept,(SELECT max(RECORD_SEQ_NO) FROM ifms.nsdl_frm_s1_file_no) as rec_seq,form.NOMINEE_1_PERCENT_SHARE,form.NOMINEE_2_PERCENT_SHARE,form.NOMINEE_3_PERCENT_SHARE,nvl(form.PRESENT_ADD_FLAT_UNIT_NO_BLOCK_NO,' '),nvl(form.PRESENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE,' '),nvl(form.PRESENT_ADDRESS_AREA_LOCALITY_TALUKA,' '),nvl(form.PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO,' '),nvl(form.PERMANENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE,' '),nvl(form.PERMANENT_ADDRESS_AREA_LOCALITY_TALUKA,' ')  FROM FRM_FORM_S1_DTLS form ");
		//Strbld.append(" SELECT ROW_NUMBER() OVER (order by substr(form.ddo_code,1,2)) as rn,form.EMP_NAME,to_char(emp.dob,'dd-MM-yyyy'),to_char(emp.doj,'dd-MM-yyyy'),emp.nsdl_ppan,emp.pran_no,emp.sevarth_id,dto.DTO_reg_no,ddo.ddo_reg_no ");  
		Strbld.append(" FROM FRM_FORM_S1_DTLS form  "); 
		Strbld.append(" inner join mst_dcps_emp emp on emp.SEVARTH_ID=form.SEVARTH_ID  left outer join HR_EIS_SCALE_MST scale on scale.SCALE_ID=emp.PAYSCALE  ");
		Strbld.append(" INNER JOIN ORG_EMP_MST oremp on oremp.EMP_ID=emp.ORG_EMP_MST_ID ");
		Strbld.append(" inner JOIN ORG_GRADE_MST grade on oremp.GRADE_ID=grade.GRADE_ID ");
		Strbld.append(" inner join MST_BANK_PAY pay on pay.BANK_CODE=emp.BANK_NAME ");
		Strbld.append("inner join RLT_BANK_BRANCH_PAY br on emp.BRANCH_NAME=br.BRANCH_ID ");
		Strbld.append(" inner join org_ddo_mst oddo on oddo.DDO_CODE =emp.DDO_CODE ");
		Strbld.append(" inner join CMN_LOCATION_MST cmn on cmn.LOC_ID=oddo.DEPT_LOC_CODE ");
		Strbld.append(" inner join CMN_LOCATION_MST cmnloc on cmnloc.LOC_ID=oddo.HOD_LOC_CODE ");
		Strbld.append(" inner join MST_DTO_REG dto on substr(dto.dto_code,1,2)=substr(form.ddo_code,1,2) "); 
		Strbld.append(" inner join MST_DDO_REG ddo on ddo.ddo_code=form.DDO_CODE  ");
		Strbld.append(" where emp.PRAN_NO is not null and substr(form.ddo_code,1,2) like '"+treasury+"%' and form.NSDL_STATUS is null "); //and form.TREASURY_EMP_COUNT between "+string+" and "+string2
		*/	
/*----------------------------------------------------------------------------------------------------------------------*/
		
		
		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());

		temp=lQuery.list();
		logger.info("temp "+temp);
		logger.info("temp size"+temp.size());
		if(temp!=null && !temp.get(0).toString().equals("0")){
			
			count=Float.parseFloat(temp.get(0).toString());
		}

		
			
		if(count!= null)
		{
			 lstAis = new ArrayList<Object>();
			Object obj;
			Object obj1;
			ComboValuesVO lObjComboValuesVO = null;	
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("1-4999-001");
			lObjComboValuesVO.setDesc("Batch 1");
			lstAis.add(lObjComboValuesVO);
			if(count>0){
			logger.info("Total count"+Math.ceil(count/4999f));
			if((long) Math.ceil(count/4999)>1){
			for (long liCtr = 1; liCtr < (long) Math.ceil(count/4999f); liCtr++)
			{		
				lObjComboValuesVO = new ComboValuesVO();
				logger.info(liCtr+1);
				String strt=Long.toString(((liCtr*5000)-(liCtr-1)));
				String end=Long.toString((((liCtr+1)*5000)-(liCtr+1)));
				String desc=strt+"-"+end+"-00"+(liCtr+1);
				logger.info(" desc ###"+desc);
				obj1 = (Object)("Batch "+(liCtr+1));
				obj = (Object)desc;
				lObjComboValuesVO.setId(desc);
				lObjComboValuesVO.setDesc("Batch "+(liCtr+1));
				lstAis.add(lObjComboValuesVO);
			}
			}
			}
		} 
		
		return lstAis;
	}

	

	@Override
	public List getEmpList(String treasurynos, String string, String string2) {
	
		List temp=null;
		
		StringBuilder  Strbld = new StringBuilder();
		String treasury=treasurynos.substring(0,2);
/*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/		
		/*Strbld.append(" SELECT form.EMP_NAME,to_char(emp.dob,'dd-MM-yyyy'),to_char(emp.doj,'dd-MM-yyyy'),emp.nsdl_ppan,emp.pran_no,emp.sevarth_id,dto.DTO_reg_no,ddo.ddo_reg_no ");
		Strbld.append(" FROM FRM_FORM_S1_DTLS form  " );
		Strbld.append(" inner   join mst_dcps_emp emp on emp.SEVARTH_ID=form.SEVARTH_ID  left outer join HR_EIS_SCALE_MST scale on scale.SCALE_ID=emp.PAYSCALE  ");
		Strbld.append(" INNER JOIN ORG_EMP_MST oremp on oremp.EMP_ID=emp.ORG_EMP_MST_ID ");
		Strbld.append(" inner JOIN ORG_GRADE_MST grade on oremp.GRADE_ID=grade.GRADE_ID ");
		Strbld.append(" inner join MST_BANK_PAY pay on pay.BANK_CODE=emp.BANK_NAME ");
		Strbld.append(" inner join RLT_BANK_BRANCH_PAY br on emp.BRANCH_NAME=br.BRANCH_ID ");
		Strbld.append(" inner join org_ddo_mst oddo on oddo.DDO_CODE =emp.DDO_CODE ");
		Strbld.append(" inner join CMN_LOCATION_MST cmn on cmn.LOC_ID=oddo.DEPT_LOC_CODE ");
		Strbld.append(" inner join CMN_LOCATION_MST cmnloc on cmnloc.LOC_ID=oddo.HOD_LOC_CODE ");
		Strbld.append(" inner join MST_DTO_REG dto on substr(dto.dto_code,1,2)=substr(form.ddo_code,1,2) ");  
		Strbld.append(" inner join MST_DDO_REG ddo on ddo.ddo_code=form.DDO_CODE  ");
		Strbld.append(" where emp.PRAN_NO is not null and substr(form.ddo_code,1,2) like '"+treasury+"%' and form.NSDL_STATUS is null and form.TREASURY_EMP_COUNT between "+string+" and "+string2);

		*/
/*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/		
		
		
		
/*
		Strbld.append("  SELECT nvl(form.EMP_NAME,' '),to_char(emp.dob,'dd-MM-yyyy'),to_char(emp.doj,'dd-MM-yyyy'),nvl(emp.nsdl_ppan,' '),nvl(emp.pran_no,' '),nvl(emp.sevarth_id,' '),dto.DTO_reg_no,nvl(ddo.ddo_reg_no,' '),ROW_NUMBER() OVER (order by form.form_S1_id) as rn  ");
		//Strbld.append(" SELECT ROW_NUMBER() OVER (order by substr(form.ddo_code,1,2)) as rn,nvl(form.EMP_NAME,' '),nvl(form.FATHER_NAME,' '),to_char(emp.dob,'MMddyyyy'),to_char(emp.doj,'MMddyyyy'),nvl(emp.nsdl_ppan,' '),nvl(emp.pran_no,' '),nvl(emp.sevarth_id,' '),dto.DTO_reg_no,nvl(ddo.ddo_reg_no,' '),emp.gender,nvl(emp.PAN_NO,' '),nvl((form.PRESENT_ADD_FLAT_UNIT_NO_BLOCK_NO ||' '|| form.PRESENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE ||' '|| form.PRESENT_ADDRESS_AREA_LOCALITY_TALUKA),' ') as emp_address,nvl(emp.LOCALITY,' '), nvl(form.PRESENT_ADDRESS_DISTRICT_TOWN_CITY,' '),nvl(emp.STATE,' '), cast(nvl(form.PRESENT_ADDRESS_PIN_CODE,'0')as varchar),nvl((form.PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO ||' '|| form.PERMANENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE ||' '|| form.PERMANENT_ADDRESS_AREA_LOCALITY_TALUKA),' ') as pemp_address,nvl(emp.pLOCALITY,' '), nvl(form.PERMANENT_ADDRESS_DISTRICT_TOWN_CITY,' '),nvl(emp.pSTATE,' '), cast(nvl(form.PERMANENT_ADDRESS_PIN_CODE,'0')as varchar),nvl((form.PHONE_NO_STD_CODE||form.phone_no_phone_no),' '), nvl(form.mobile_no,' '),nvl(form.EMAIL_ID,' '),nvl(scale.SCALE_DESC,' '),cast(nvl(emp.BASIC_PAY,' ') as varchar),nvl(emp.nsdl_ppan,' '),nvl(form.PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO,' '),nvl(form.PERMANENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE,' '),to_char(emp.EMP_SERVEND_DT,'MMddyyyy'),to_char(emp.SUPER_ANN_DATE,'MMddyyyy'),nvl(ddo.DDO_OFFICE_NAME,' '),nvl(emp.BANK_ACNT_NO,' '),nvl(pay.BANK_NAME,' '),nvl(br.BRANCH_NAME,' '),nvl(br.BRANCH_ADDRESS,' '),cast(nvl(br.PINCODE,'0')as varchar),nvl(br.MICR_CODE,' '),nvl(br.IFSC_CODE,' '),nvl(grade.GRADE_name,' '),nvl(cmn.LOC_NAME,' ') as Admin_Dept,nvl(cmnloc.LOC_NAME,' ') as Field_dept,(SELECT max(RECORD_SEQ_NO) FROM ifms.nsdl_frm_s1_file_no) as rec_seq,form.NOMINEE_1_PERCENT_SHARE,form.NOMINEE_2_PERCENT_SHARE,form.NOMINEE_3_PERCENT_SHARE,nvl(form.PRESENT_ADD_FLAT_UNIT_NO_BLOCK_NO,' '),nvl(form.PRESENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE,' '),nvl(form.PRESENT_ADDRESS_AREA_LOCALITY_TALUKA,' '),nvl(form.PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO,' '),nvl(form.PERMANENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE,' '),nvl(form.PERMANENT_ADDRESS_AREA_LOCALITY_TALUKA,' ')  FROM FRM_FORM_S1_DTLS form ");
		//Strbld.append(" SELECT ROW_NUMBER() OVER (order by substr(form.ddo_code,1,2)) as rn,form.EMP_NAME,to_char(emp.dob,'dd-MM-yyyy'),to_char(emp.doj,'dd-MM-yyyy'),emp.nsdl_ppan,emp.pran_no,emp.sevarth_id,dto.DTO_reg_no,ddo.ddo_reg_no ");  
		Strbld.append(" FROM FRM_FORM_S1_DTLS form  "); 
		Strbld.append(" inner   join mst_dcps_emp emp on emp.SEVARTH_ID=form.SEVARTH_ID  left outer join HR_EIS_SCALE_MST scale on scale.SCALE_ID=emp.PAYSCALE    ");
		Strbld.append(" INNER JOIN ORG_EMP_MST oremp on oremp.EMP_ID=emp.ORG_EMP_MST_ID  ");
		Strbld.append(" inner JOIN ORG_GRADE_MST grade on oremp.GRADE_ID=grade.GRADE_ID  ");
		Strbld.append(" left outer join MST_BANK_PAY pay on pay.BANK_CODE=emp.BANK_NAME  ");
		Strbld.append(" left outer join RLT_BANK_BRANCH_PAY br on emp.BRANCH_NAME=br.BRANCH_ID "); 
		Strbld.append(" inner join nsdl_Subscriber_Ack_id sub on sub.DCPS_EMP_ID = emp.DCPS_EMP_ID and sub.ACK_ID is not null ");
		Strbld.append(" inner join org_ddo_mst oddo on oddo.DDO_CODE =form.DDO_CODE ");  
		Strbld.append(" inner join CMN_LOCATION_MST cmn on cmn.LOC_ID=oddo.DEPT_LOC_CODE ");  
		Strbld.append(" inner join CMN_LOCATION_MST cmnloc on cmnloc.LOC_ID=oddo.HOD_LOC_CODE ");  
		Strbld.append(" inner join MST_DTO_REG dto on substr(dto.dto_code,1,2)=substr(form.ddo_code,1,2) ");    
		Strbld.append(" inner join MST_DDO_REG ddo on ddo.ddo_code=form.DDO_CODE ");   
		Strbld.append(" where emp.PRAN_NO is not null and substr(form.ddo_code,1,2) like '"+treasury+"%' and form.NSDL_STATUS is null ");  
		Strbld.append(" and (emp.ac_dcps_maintained_by=700174 or emp.ac_dcps_maintained_by is null) and form.TREASURY_EMP_COUNT between "+string+" and "+string2+" ");
		Strbld.append(" and emp.dcps_emp_id not in (SELECT distinct hst.dcps_emp_id FROM TRN_DCPS_CHANGES hst inner join mst_dcps_emp emp on emp.dcps_emp_id = hst.dcps_emp_id and hst.ddo_code = emp.ddo_code ");
		Strbld.append("	inner join FRM_FORM_S1_DTLS form on emp.SEVARTH_ID = form.SEVARTH_ID where hst.TYPE_OF_CHANGES = 'PersonalDetails' and hst.FIELD_NAME = 'Full Name' and substr(hst.ddo_code,1,2) like '"+treasury+"%' and emp.reg_status = 1 and emp.form_status = 1 and form.TREASURY_EMP_COUNT between "+string+" and "+string2+") ");
*/	
		/*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/		
		
		
		Strbld.append("	SELECT * FROM (SELECT nvl(form.EMP_NAME,' '),to_char(emp.dob,'dd-MM-yyyy'),to_char(emp.doj,'dd-MM-yyyy'),nvl(emp.nsdl_ppan,' '),nvl(emp.pran_no,' '),nvl(emp.sevarth_id,' '),dto.DTO_reg_no,nvl(ddo.ddo_reg_no,' '),ROW_NUMBER() OVER (order by form.form_S1_id) as rn ");  
		Strbld.append("	FROM FRM_FORM_S1_DTLS form    ");
		Strbld.append("	inner join mst_dcps_emp emp on emp.SEVARTH_ID=form.SEVARTH_ID  left outer join HR_EIS_SCALE_MST scale on scale.SCALE_ID=emp.PAYSCALE ");    
		Strbld.append("	INNER JOIN ORG_EMP_MST oremp on oremp.EMP_ID=emp.ORG_EMP_MST_ID ");  
		Strbld.append("	inner JOIN ORG_GRADE_MST grade on oremp.GRADE_ID=grade.GRADE_ID ");  
		Strbld.append("	left outer join MST_BANK_PAY pay on pay.BANK_CODE=emp.BANK_NAME ");  
		Strbld.append("	left outer join RLT_BANK_BRANCH_PAY br on emp.BRANCH_NAME=br.BRANCH_ID ");  
		Strbld.append("	inner join nsdl_Subscriber_Ack_id sub on sub.DCPS_EMP_ID = emp.DCPS_EMP_ID and sub.ACK_ID is not null "); 
		Strbld.append("	inner join org_ddo_mst oddo on oddo.DDO_CODE =form.DDO_CODE ");   
		Strbld.append("	inner join CMN_LOCATION_MST cmn on cmn.LOC_ID=oddo.DEPT_LOC_CODE ");   
		Strbld.append("	inner join CMN_LOCATION_MST cmnloc on cmnloc.LOC_ID=oddo.HOD_LOC_CODE ");   
		Strbld.append("	inner join MST_DTO_REG dto on substr(dto.dto_code,1,2)=substr(form.ddo_code,1,2) ");     
		Strbld.append("	inner join MST_DDO_REG ddo on ddo.ddo_code=form.DDO_CODE ");    
		Strbld.append("	where emp.nsdl_ppan is not null and emp.PRAN_NO is not null and substr(form.ddo_code,1,2) like '"+treasury+"%' and form.NSDL_STATUS is null ");   
		Strbld.append("	and (emp.emp_servend_dt > sysdate and emp.super_ann_date > sysdate) ");
		Strbld.append("	and (emp.ac_dcps_maintained_by=700174 or emp.ac_dcps_maintained_by is null) ");  
		/*Strbld.append("	and emp.dcps_emp_id not in "); 
		Strbld.append("	(SELECT chg_dcps_emp_id FROM (SELECT distinct hst.dcps_emp_id as chg_dcps_emp_id,ROW_NUMBER() OVER (order by form.form_S1_id) as rns FROM TRN_DCPS_CHANGES hst inner join mst_dcps_emp emp on emp.dcps_emp_id = hst.dcps_emp_id and hst.ddo_code = emp.ddo_code "); 
		Strbld.append("	inner join FRM_FORM_S1_DTLS form on emp.SEVARTH_ID = form.SEVARTH_ID where hst.TYPE_OF_CHANGES = 'PersonalDetails' and hst.FIELD_NAME = 'Full Name' and substr(hst.ddo_code,1,2) like '"+treasury+"%' and emp.reg_status = 1 and emp.form_status = 1) ");
		Strbld.append("	where rns between "+string+" and "+string2+")) ");*/
		Strbld.append(")	where rn between "+string+" and "+string2+" ");
		
		
		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());

		temp=lQuery.list();
		
		return temp;
	}

	@Override
	public List getEmpListPrint(String treasurynos, String string,
			String string2, String string3) {
		List temp=null;
		StringBuilder  Strblds = new StringBuilder();
		StringBuilder  Strbld = new StringBuilder();
		String treasury=treasurynos.substring(0,2);
		StringBuilder  Strbldx = new StringBuilder();
		int tempx=0;
		
		
		//Strbld.append(" SELECT nvl(form.EMP_NAME,'Q'),nvl(form.FATHER_NAME,'Q'),to_char(emp.dob,'MMddyyyy'),to_char(emp.doj,'MMddyyyy'),nvl(emp.nsdl_ppan,'Q'),nvl(emp.pran_no,'Q'),nvl(emp.sevarth_id,'Q'),dto.DTO_CODE,nvl(ddo.ddo_reg_no,'Q'),emp.gender,nvl(emp.PAN_NO,'Q'),nvl((emp.ADDRESS_BUILDING ||' '|| emp.ADDRESS_STREET ||' '|| emp.LANDMARK),'Q') as emp_address, nvl(emp.LOCALITY,'Q'), nvl(emp.DISTRICT,'Q'), nvl(emp.STATE,'Q'), cast(nvl(emp.PINCODE,'0')as varchar), nvl((emp.pADDRESS_BUILDING ||' '|| emp.pADDRESS_STREET ||' '|| emp.pLANDMARK),'Q') as pemp_address, nvl(emp.pLOCALITY,'Q'), nvl(emp.pDISTRICT,'Q'), nvl(emp.pSTATE,'Q'), cast(nvl(PPINCODE,'0')as varchar), nvl((form.PHONE_NO_STD_CODE||form.phone_no_phone_no),'Q'), nvl(form.mobile_no,'Q'),nvl(form.EMAIL_ID,'Q'),nvl(scale.SCALE_DESC,'Q'),cast(nvl(emp.BASIC_PAY,'Q') as varchar),nvl(emp.nsdl_ppan,'Q'),nvl(form.PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO,'Q'),nvl(form.PERMANENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE,'Q'),to_char(emp.EMP_SERVEND_DT,'MMddyyyy'),to_char(emp.SUPER_ANN_DATE,'MMddyyyy'),nvl(ddo.DDO_OFFICE_NAME,'Q'),nvl(emp.BANK_ACNT_NO,'Q'),nvl(pay.BANK_NAME,'Q'),nvl(br.BRANCH_NAME,'Q'),nvl(br.BRANCH_ADDRESS,'Q'),cast(nvl(br.PINCODE,'0')as varchar),nvl(br.MICR_CODE,'Q'),nvl(br.IFSC_CODE,'Q'),nvl(grade.GRADE_name,'Q'),nvl(cmn.LOC_NAME,'Q') as Admin_Dept,nvl(cmnloc.LOC_NAME,'Q') as Field_dept,(SELECT max(RECORD_SEQ_NO) FROM ifms.nsdl_frm_s1_file_no) as rec_seq  FROM FRM_FORM_S1_DTLS form  " );
	
	//	Strbld.append(" SELECT nvl(form.EMP_NAME,' '),nvl(form.FATHER_NAME,' '),to_char(emp.dob,'MMddyyyy'),to_char(emp.doj,'MMddyyyy'),nvl(emp.nsdl_ppan,' '),nvl(emp.pran_no,' '),nvl(emp.sevarth_id,' '),dto.DTO_reg_no,nvl(ddo.ddo_reg_no,' '),emp.gender,nvl(emp.PAN_NO,' '),nvl((form.PRESENT_ADD_FLAT_UNIT_NO_BLOCK_NO ||' '|| form.PRESENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE ||' '|| form.PRESENT_ADDRESS_AREA_LOCALITY_TALUKA),' ') as emp_address,nvl(emp.LOCALITY,' '), nvl(form.PRESENT_ADDRESS_DISTRICT_TOWN_CITY,' '),nvl(emp.STATE,' '), cast(nvl(form.PRESENT_ADDRESS_PIN_CODE,'0')as varchar),nvl((form.PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO ||' '|| form.PERMANENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE ||' '|| form.PERMANENT_ADDRESS_AREA_LOCALITY_TALUKA),' ') as pemp_address,nvl(emp.pLOCALITY,' '), nvl(form.PERMANENT_ADDRESS_DISTRICT_TOWN_CITY,' '),nvl(emp.pSTATE,' '), cast(nvl(form.PERMANENT_ADDRESS_PIN_CODE,'0')as varchar),nvl((form.PHONE_NO_STD_CODE||form.phone_no_phone_no),' '), nvl(form.mobile_no,' '),nvl(form.EMAIL_ID,' '),nvl(scale.SCALE_DESC,' '),cast(nvl(emp.BASIC_PAY,' ') as varchar),nvl(emp.nsdl_ppan,' '),nvl(form.PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO,' '),nvl(form.PERMANENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE,' '),to_char(emp.EMP_SERVEND_DT,'MMddyyyy'),to_char(emp.SUPER_ANN_DATE,'MMddyyyy'),nvl(ddo.DDO_OFFICE_NAME,' '),nvl(emp.BANK_ACNT_NO,' '),nvl(pay.BANK_NAME,' '),nvl(br.BRANCH_NAME,' '),nvl(br.BRANCH_ADDRESS,' '),cast(nvl(br.PINCODE,'0')as varchar),nvl(br.MICR_CODE,' '),nvl(br.IFSC_CODE,' '),nvl(grade.GRADE_name,' '),nvl(cmn.LOC_NAME,' ') as Admin_Dept,nvl(cmnloc.LOC_NAME,' ') as Field_dept,(SELECT max(RECORD_SEQ_NO) FROM ifms.nsdl_frm_s1_file_no) as rec_seq,form.NOMINEE_1_PERCENT_SHARE,form.NOMINEE_2_PERCENT_SHARE,form.NOMINEE_3_PERCENT_SHARE,nvl(form.PRESENT_ADD_FLAT_UNIT_NO_BLOCK_NO,' '),nvl(form.PRESENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE,' '),nvl(form.PRESENT_ADDRESS_AREA_LOCALITY_TALUKA,' '),nvl(form.PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO,' '),nvl(form.PERMANENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE,' '),nvl(form.PERMANENT_ADDRESS_AREA_LOCALITY_TALUKA,' ')  FROM FRM_FORM_S1_DTLS form "); 
		//Strbld.append(" SELECT nvl(form.EMP_NAME,' '),nvl(form.FATHER_NAME,' '),to_char(emp.dob,'MMddyyyy'),to_char(emp.doj,'MMddyyyy'),nvl(emp.nsdl_ppan,' '),nvl(emp.pran_no,' '),nvl(emp.sevarth_id,' '),dto.DTO_CODE,nvl(ddo.ddo_reg_no,' '),emp.gender,nvl(emp.PAN_NO,' '),nvl((form.PRESENT_ADD_FLAT_UNIT_NO_BLOCK_NO ||'$'|| form.PRESENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE ||'$'|| form.PRESENT_ADDRESS_AREA_LOCALITY_TALUKA),' ') as emp_address,nvl(emp.LOCALITY,' '), nvl(form.PRESENT_ADDRESS_DISTRICT_TOWN_CITY,' '),nvl(emp.STATE,' '), cast(nvl(form.PRESENT_ADDRESS_PIN_CODE,'0')as varchar),nvl((form.PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO ||'$'|| form.PERMANENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE ||'$'|| form.PERMANENT_ADDRESS_AREA_LOCALITY_TALUKA),' ') as pemp_address,nvl(emp.pLOCALITY,' '), nvl(form.PERMANENT_ADDRESS_DISTRICT_TOWN_CITY,' '),nvl(emp.pSTATE,' '), cast(nvl(form.PERMANENT_ADDRESS_PIN_CODE,'0')as varchar),nvl((form.PHONE_NO_STD_CODE||form.phone_no_phone_no),' '), nvl(form.mobile_no,' '),nvl(form.EMAIL_ID,' '),nvl(scale.SCALE_DESC,' '),cast(nvl(emp.BASIC_PAY,' ') as varchar),nvl(emp.nsdl_ppan,' '),nvl(form.PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO,' '),nvl(form.PERMANENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE,' '),to_char(emp.EMP_SERVEND_DT,'MMddyyyy'),to_char(emp.SUPER_ANN_DATE,'MMddyyyy'),nvl(ddo.DDO_OFFICE_NAME,' '),nvl(emp.BANK_ACNT_NO,' '),nvl(pay.BANK_NAME,' '),nvl(br.BRANCH_NAME,' '),nvl(br.BRANCH_ADDRESS,' '),cast(nvl(br.PINCODE,'0')as varchar),nvl(br.MICR_CODE,' '),nvl(br.IFSC_CODE,' '),nvl(grade.GRADE_name,' '),nvl(cmn.LOC_NAME,' ') as Admin_Dept,nvl(cmnloc.LOC_NAME,' ') as Field_dept,(SELECT max(RECORD_SEQ_NO) FROM ifms.nsdl_frm_s1_file_no) as rec_seq,form.NOMINEE_1_PERCENT_SHARE,form.NOMINEE_2_PERCENT_SHARE,form.NOMINEE_3_PERCENT_SHARE  FROM FRM_FORM_S1_DTLS form  ");
	/*	Strbld.append(" inner   join mst_dcps_emp emp on emp.SEVARTH_ID=form.SEVARTH_ID  left outer join HR_EIS_SCALE_MST scale on scale.SCALE_ID=emp.PAYSCALE  ");
		Strbld.append(" INNER JOIN ORG_EMP_MST oremp on oremp.EMP_ID=emp.ORG_EMP_MST_ID ");
		Strbld.append(" inner JOIN ORG_GRADE_MST grade on oremp.GRADE_ID=grade.GRADE_ID ");
		Strbld.append(" inner join MST_BANK_PAY pay on pay.BANK_CODE=emp.BANK_NAME ");
		Strbld.append("inner join RLT_BANK_BRANCH_PAY br on emp.BRANCH_NAME=br.BRANCH_ID ");
		Strbld.append(" inner join org_ddo_mst oddo on oddo.DDO_CODE =emp.DDO_CODE ");
		Strbld.append(" inner join CMN_LOCATION_MST cmn on cmn.LOC_ID=oddo.DEPT_LOC_CODE ");
		Strbld.append(" inner join CMN_LOCATION_MST cmnloc on cmnloc.LOC_ID=oddo.HOD_LOC_CODE ");
		Strbld.append(" inner join MST_DTO_REG dto on substr(dto.dto_code,1,2)=substr(form.ddo_code,1,2) "); 
		Strbld.append(" inner join MST_DDO_REG ddo on ddo.ddo_code=form.DDO_CODE  ");
		Strbld.append(" where emp.PRAN_NO is not null and substr(form.ddo_code,1,2) like '"+treasury+"%' and form.NSDL_STATUS is null and form.TREASURY_EMP_COUNT between "+string+" and "+string2);
*/
		
		
		
		//latest for final modification
		
	
		Strbld.append(" SELECT * FROM (SELECT nvl(form.EMP_NAME,' '),nvl(form.FATHER_NAME,' '),to_char(emp.dob,'MMddyyyy'),to_char(emp.doj,'MMddyyyy'),nvl(emp.nsdl_ppan,' '),nvl(emp.pran_no,' '),nvl(emp.sevarth_id,' '),dto.DTO_reg_no,nvl(ddo.ddo_reg_no,' '),emp.gender,nvl(emp.PAN_NO,' '),nvl((form.PRESENT_ADD_FLAT_UNIT_NO_BLOCK_NO ||' '|| form.PRESENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE ||' '|| form.PRESENT_ADDRESS_AREA_LOCALITY_TALUKA),' ') as emp_address,nvl(emp.LOCALITY,' '), nvl(form.PRESENT_ADDRESS_DISTRICT_TOWN_CITY,' '),nvl(emp.STATE,' '), cast(nvl(form.PRESENT_ADDRESS_PIN_CODE,'0')as varchar),nvl((form.PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO ||' '|| form.PERMANENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE ||' '|| form.PERMANENT_ADDRESS_AREA_LOCALITY_TALUKA),' ') as pemp_address,nvl(emp.pLOCALITY,' '), nvl(form.PERMANENT_ADDRESS_DISTRICT_TOWN_CITY,' '),nvl(emp.pSTATE,' '), cast(nvl(form.PERMANENT_ADDRESS_PIN_CODE,'0')as varchar),nvl((form.PHONE_NO_STD_CODE||form.phone_no_phone_no),' '), nvl(form.mobile_no,' '),nvl(form.EMAIL_ID,' '),nvl(scale.SCALE_DESC,'Not Available'),cast(nvl(emp.BASIC_PAY,'0') as varchar),nvl(emp.nsdl_ppan,' '),nvl(form.PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO,' '),nvl(form.PERMANENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE,' '),to_char(emp.EMP_SERVEND_DT,'MMddyyyy'),to_char(emp.SUPER_ANN_DATE,'MMddyyyy'),nvl(ddo.DDO_OFFICE_NAME,' '),nvl(emp.BANK_ACNT_NO,' '),nvl(pay.BANK_NAME,' '),nvl(br.BRANCH_NAME,' '),nvl(br.BRANCH_ADDRESS,' '),cast(nvl(br.PINCODE,'0')as varchar),nvl(br.MICR_CODE,' '),nvl(br.IFSC_CODE,' '),nvl(grade.GRADE_name,' '),nvl(cmn.LOC_NAME,' ') as Admin_Dept,nvl(cmnloc.LOC_NAME,' ') as Field_dept,(SELECT max(RECORD_SEQ_NO) FROM ifms.nsdl_frm_s1_file_no) as rec_seq,nvl(form.NOMINEE_1_PERCENT_SHARE,0),nvl(form.NOMINEE_2_PERCENT_SHARE,0),nvl(form.NOMINEE_3_PERCENT_SHARE,0),nvl(form.PRESENT_ADD_FLAT_UNIT_NO_BLOCK_NO,' '),nvl(form.PRESENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE,' '),nvl(form.PRESENT_ADDRESS_AREA_LOCALITY_TALUKA,' '),nvl(form.PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO,' '),nvl(form.PERMANENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE,' '),nvl(form.PERMANENT_ADDRESS_AREA_LOCALITY_TALUKA,' '),  ");
		Strbld.append(" form.NOMINEE_1_NAME,nvl(to_char(form.NOMINEE_1_DOB,'MMddyyyy'),' '),  ");
		Strbld.append(" nvl((SELECT cmn1.lookup_name||' ' FROM cmn_lookup_mst cmn1 where cast(cmn1.lookup_id as varchar(15))=(form.NOMINEE_1_RELATIONSHIP)),' '),  ");
		Strbld.append(" form.NOMINEE_1_GUARDIAN_NAME,form.NOMINEE_1_NOMINATION_INVALID_CONDITION,form.NOMINEE_2_NAME,nvl(to_char(form.NOMINEE_2_DOB,'MMddyyyy'),' '),  ");
		Strbld.append(" nvl((SELECT cmn.lookup_name||'  ' FROM cmn_lookup_mst cmn where cast(cmn.lookup_id as varchar(15))=(form.NOMINEE_2_RELATIONSHIP)),' '),  ");
		Strbld.append(" form.NOMINEE_2_GUARDIAN_NAME,form.NOMINEE_2_NOMINATION_INVALID_CONDITION,form.NOMINEE_3_NAME,nvl(to_char(form.NOMINEE_3_DOB,'MMddyyyy'),' '),  ");
		Strbld.append(" nvl((SELECT cmn3.lookup_name||' ' FROM cmn_lookup_mst cmn3 where cast(cmn3.lookup_id as varchar(15))=(form.NOMINEE_3_RELATIONSHIP)),' '),form.NOMINEE_3_GUARDIAN_NAME,form.NOMINEE_3_NOMINATION_INVALID_CONDITION,  ");
		Strbld.append(" nvl((SELECT cmn4.lookup_name||' ' FROM cmn_lookup_mst cmn4 where cast(cmn4.lookup_id as varchar(15))=(emp.SALUTATION)),' '),  ");
		Strbld.append(" sub.ACK_ID,form.form_s1_id,ROW_NUMBER() OVER (order by form.form_S1_id) as rn  FROM FRM_FORM_S1_DTLS form   ");
		Strbld.append(" inner   join mst_dcps_emp emp on emp.SEVARTH_ID=form.SEVARTH_ID  left outer join HR_EIS_SCALE_MST scale on scale.SCALE_ID=emp.PAYSCALE     ");
		Strbld.append(" INNER JOIN ORG_EMP_MST oremp on oremp.EMP_ID=emp.ORG_EMP_MST_ID   ");
		Strbld.append(" inner JOIN ORG_GRADE_MST grade on oremp.GRADE_ID=grade.GRADE_ID   ");
		Strbld.append(" left outer join MST_BANK_PAY pay on pay.BANK_CODE=emp.BANK_NAME   ");
		Strbld.append(" left outer join RLT_BANK_BRANCH_PAY br on emp.BRANCH_NAME=br.BRANCH_ID    ");
		Strbld.append(" inner join nsdl_Subscriber_Ack_id sub on sub.DCPS_EMP_ID = emp.DCPS_EMP_ID and sub.ACK_ID is not null  ");
		Strbld.append(" inner join org_ddo_mst oddo on oddo.DDO_CODE =form.DDO_CODE    ");
		Strbld.append(" inner join CMN_LOCATION_MST cmn on cmn.LOC_ID=oddo.DEPT_LOC_CODE    ");
		Strbld.append(" inner join CMN_LOCATION_MST cmnloc on cmnloc.LOC_ID=oddo.HOD_LOC_CODE    ");
		Strbld.append(" inner join MST_DTO_REG dto on substr(dto.dto_code,1,2)=substr(form.ddo_code,1,2)      ");
		Strbld.append(" inner join MST_DDO_REG ddo on ddo.ddo_code=form.DDO_CODE     ");
		Strbld.append(" where emp.nsdl_ppan is not null and emp.PRAN_NO is not null and substr(form.ddo_code,1,2) like '"+treasury+"%' and form.NSDL_STATUS is null    ");
		Strbld.append(" and (emp.emp_servend_dt > sysdate and emp.super_ann_date > sysdate)  ");
		Strbld.append(" and (emp.ac_dcps_maintained_by=700174 or emp.ac_dcps_maintained_by is null) ");
		/*Strbld.append(" and emp.dcps_emp_id not in  ");
		Strbld.append(" (SELECT chg_dcps_emp_id FROM (SELECT distinct hst.dcps_emp_id as chg_dcps_emp_id,ROW_NUMBER() OVER (order by form.form_S1_id) as rns FROM TRN_DCPS_CHANGES hst inner join mst_dcps_emp emp on emp.dcps_emp_id = hst.dcps_emp_id and hst.ddo_code = emp.ddo_code  ");  
		Strbld.append(" inner join FRM_FORM_S1_DTLS form on emp.SEVARTH_ID = form.SEVARTH_ID where hst.TYPE_OF_CHANGES = 'PersonalDetails' and hst.FIELD_NAME = 'Full Name' and substr(hst.ddo_code,1,2) like '"+treasury+"%' and emp.reg_status = 1 and emp.form_status = 1)  ");
		Strbld.append(" where rns between "+string+" and "+string2+")) ");*/
		Strbld.append(") where rn between "+string+" and "+string2+"  ");
		
/*----------------------------------------------------------------------------------------------------------------------------------------
		
		Strbld.append(" SELECT nvl(form.EMP_NAME,' '),nvl(form.FATHER_NAME,' '),to_char(emp.dob,'MMddyyyy'),to_char(emp.doj,'MMddyyyy'),nvl(emp.nsdl_ppan,' '),nvl(emp.pran_no,' '),nvl(emp.sevarth_id,' '),dto.DTO_reg_no,nvl(ddo.ddo_reg_no,' '),emp.gender,nvl(emp.PAN_NO,' '),nvl((form.PRESENT_ADD_FLAT_UNIT_NO_BLOCK_NO ||' '|| form.PRESENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE ||' '|| form.PRESENT_ADDRESS_AREA_LOCALITY_TALUKA),' ') as emp_address,nvl(emp.LOCALITY,' '), nvl(form.PRESENT_ADDRESS_DISTRICT_TOWN_CITY,' '),nvl(emp.STATE,' '), cast(nvl(form.PRESENT_ADDRESS_PIN_CODE,'0')as varchar),nvl((form.PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO ||' '|| form.PERMANENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE ||' '|| form.PERMANENT_ADDRESS_AREA_LOCALITY_TALUKA),' ') as pemp_address,nvl(emp.pLOCALITY,' '), nvl(form.PERMANENT_ADDRESS_DISTRICT_TOWN_CITY,' '),nvl(emp.pSTATE,' '), cast(nvl(form.PERMANENT_ADDRESS_PIN_CODE,'0')as varchar),nvl((form.PHONE_NO_STD_CODE||form.phone_no_phone_no),' '), nvl(form.mobile_no,' '),nvl(form.EMAIL_ID,' '),nvl(scale.SCALE_DESC,'Not Available'),cast(nvl(emp.BASIC_PAY,'0') as varchar),nvl(emp.nsdl_ppan,' '),nvl(form.PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO,' '),nvl(form.PERMANENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE,' '),to_char(emp.EMP_SERVEND_DT,'MMddyyyy'),to_char(emp.SUPER_ANN_DATE,'MMddyyyy'),nvl(ddo.DDO_OFFICE_NAME,' '),nvl(emp.BANK_ACNT_NO,' '),nvl(pay.BANK_NAME,' '),nvl(br.BRANCH_NAME,' '),nvl(br.BRANCH_ADDRESS,' '),cast(nvl(br.PINCODE,'0')as varchar),nvl(br.MICR_CODE,' '),nvl(br.IFSC_CODE,' '),nvl(grade.GRADE_name,' '),nvl(cmn.LOC_NAME,' ') as Admin_Dept,nvl(cmnloc.LOC_NAME,' ') as Field_dept,(SELECT max(RECORD_SEQ_NO) FROM ifms.nsdl_frm_s1_file_no) as rec_seq,nvl(form.NOMINEE_1_PERCENT_SHARE,0),nvl(form.NOMINEE_2_PERCENT_SHARE,0),nvl(form.NOMINEE_3_PERCENT_SHARE,0),nvl(form.PRESENT_ADD_FLAT_UNIT_NO_BLOCK_NO,' '),nvl(form.PRESENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE,' '),nvl(form.PRESENT_ADDRESS_AREA_LOCALITY_TALUKA,' '),nvl(form.PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO,' '),nvl(form.PERMANENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE,' '),nvl(form.PERMANENT_ADDRESS_AREA_LOCALITY_TALUKA,' '), ");
		Strbld.append("  form.NOMINEE_1_NAME,nvl(to_char(form.NOMINEE_1_DOB,'MMddyyyy'),' '), ");
		Strbld.append(" nvl((SELECT cmn1.lookup_name||' ' FROM cmn_lookup_mst cmn1 where cast(cmn1.lookup_id as varchar(15))=(form.NOMINEE_1_RELATIONSHIP)),' '), ");
		Strbld.append("	form.NOMINEE_1_GUARDIAN_NAME,form.NOMINEE_1_NOMINATION_INVALID_CONDITION,form.NOMINEE_2_NAME,nvl(to_char(form.NOMINEE_2_DOB,'MMddyyyy'),' '), ");
		Strbld.append("	nvl((SELECT cmn.lookup_name||'  ' FROM cmn_lookup_mst cmn where cast(cmn.lookup_id as varchar(15))=(form.NOMINEE_2_RELATIONSHIP)),' '), ");
		Strbld.append("	form.NOMINEE_2_GUARDIAN_NAME,form.NOMINEE_2_NOMINATION_INVALID_CONDITION,form.NOMINEE_3_NAME,nvl(to_char(form.NOMINEE_3_DOB,'MMddyyyy'),' '), ");
		Strbld.append("	nvl((SELECT cmn3.lookup_name||' ' FROM cmn_lookup_mst cmn3 where cast(cmn3.lookup_id as varchar(15))=(form.NOMINEE_3_RELATIONSHIP)),' '),form.NOMINEE_3_GUARDIAN_NAME,form.NOMINEE_3_NOMINATION_INVALID_CONDITION, ");
		Strbld.append("	nvl((SELECT cmn4.lookup_name||' ' FROM cmn_lookup_mst cmn4 where cast(cmn4.lookup_id as varchar(15))=(emp.SALUTATION)),' '), ");
		Strbld.append(" sub.ACK_ID,ROW_NUMBER() OVER (order by form.form_S1_id) as rn  FROM FRM_FORM_S1_DTLS form  ");
		Strbld.append(" inner   join mst_dcps_emp emp on emp.SEVARTH_ID=form.SEVARTH_ID  left outer join HR_EIS_SCALE_MST scale on scale.SCALE_ID=emp.PAYSCALE    ");
		Strbld.append(" INNER JOIN ORG_EMP_MST oremp on oremp.EMP_ID=emp.ORG_EMP_MST_ID  ");
		Strbld.append(" inner JOIN ORG_GRADE_MST grade on oremp.GRADE_ID=grade.GRADE_ID  ");
		Strbld.append(" left outer join MST_BANK_PAY pay on pay.BANK_CODE=emp.BANK_NAME  ");
		Strbld.append(" left outer join RLT_BANK_BRANCH_PAY br on emp.BRANCH_NAME=br.BRANCH_ID ");  
		Strbld.append(" inner join nsdl_Subscriber_Ack_id sub on sub.DCPS_EMP_ID = emp.DCPS_EMP_ID and sub.ACK_ID is not null ");
		Strbld.append(" inner join org_ddo_mst oddo on oddo.DDO_CODE =form.DDO_CODE ");  
		Strbld.append(" inner join CMN_LOCATION_MST cmn on cmn.LOC_ID=oddo.DEPT_LOC_CODE ");  
		Strbld.append(" inner join CMN_LOCATION_MST cmnloc on cmnloc.LOC_ID=oddo.HOD_LOC_CODE ");  
		Strbld.append(" inner join MST_DTO_REG dto on substr(dto.dto_code,1,2)=substr(form.ddo_code,1,2) ");    
		Strbld.append(" inner join MST_DDO_REG ddo on ddo.ddo_code=form.DDO_CODE ");   
		Strbld.append(" where emp.PRAN_NO is not null and substr(form.ddo_code,1,2) like '"+treasury+"%' and form.NSDL_STATUS is null ");  
		Strbld.append(" and (emp.ac_dcps_maintained_by=700174 or emp.ac_dcps_maintained_by is null) and form.TREASURY_EMP_COUNT between "+string+" and "+string2+" ");
		Strbld.append(" and emp.dcps_emp_id not in (SELECT distinct hst.dcps_emp_id FROM TRN_DCPS_CHANGES hst inner join mst_dcps_emp emp on emp.dcps_emp_id = hst.dcps_emp_id and hst.ddo_code = emp.ddo_code ");
		Strbld.append("	inner join FRM_FORM_S1_DTLS form on emp.SEVARTH_ID = form.SEVARTH_ID where hst.TYPE_OF_CHANGES = 'PersonalDetails' and hst.FIELD_NAME = 'Full Name' and substr(hst.ddo_code,1,2) like '"+treasury+"%' and emp.reg_status = 1 and emp.form_status = 1 and form.TREASURY_EMP_COUNT between "+string+" and "+string2+") ");

------------------------------------------------------------------------------------------------------------------------------------------*/		 
		
		
		//latest rowcount query here
		/*Strbld.append(" select * from ( "); 
		Strbld.append("SELECT nvl(form.EMP_NAME,' '),nvl(form.FATHER_NAME,' '),to_char(emp.dob,'MMddyyyy'),to_char(emp.doj,'MMddyyyy'),nvl(emp.nsdl_ppan,' '),nvl(emp.pran_no,' '),nvl(emp.sevarth_id,' '),dto.DTO_reg_no,nvl(ddo.ddo_reg_no,' '),emp.gender,nvl(emp.PAN_NO,' '),nvl((form.PRESENT_ADD_FLAT_UNIT_NO_BLOCK_NO ||' '|| form.PRESENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE ||' '|| form.PRESENT_ADDRESS_AREA_LOCALITY_TALUKA),' ') as emp_address,nvl(emp.LOCALITY,' '), nvl(form.PRESENT_ADDRESS_DISTRICT_TOWN_CITY,' '),nvl(emp.STATE,' '), cast(nvl(form.PRESENT_ADDRESS_PIN_CODE,'0')as varchar),nvl((form.PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO ||' '|| form.PERMANENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE ||' '|| form.PERMANENT_ADDRESS_AREA_LOCALITY_TALUKA),' ') as pemp_address,nvl(emp.pLOCALITY,' '), nvl(form.PERMANENT_ADDRESS_DISTRICT_TOWN_CITY,' '),nvl(emp.pSTATE,' '), cast(nvl(form.PERMANENT_ADDRESS_PIN_CODE,'0')as varchar),nvl((form.PHONE_NO_STD_CODE||form.phone_no_phone_no),' '), nvl(form.mobile_no,' '),nvl(form.EMAIL_ID,' '),nvl(scale.SCALE_DESC,' '),cast(nvl(emp.BASIC_PAY,' ') as varchar),nvl(emp.nsdl_ppan,' '),nvl(form.PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO,' '),nvl(form.PERMANENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE,' '),to_char(emp.EMP_SERVEND_DT,'MMddyyyy'),to_char(emp.SUPER_ANN_DATE,'MMddyyyy'),nvl(ddo.DDO_OFFICE_NAME,' '),nvl(emp.BANK_ACNT_NO,' '),nvl(pay.BANK_NAME,' '),nvl(br.BRANCH_NAME,' '),nvl(br.BRANCH_ADDRESS,' '),cast(nvl(br.PINCODE,'0')as varchar),br.MICR_CODE,br.IFSC_CODE,nvl(grade.GRADE_name,' '),nvl(cmn.LOC_NAME,' ') as Admin_Dept,nvl(cmnloc.LOC_NAME,' ') as Field_dept,(SELECT max(RECORD_SEQ_NO) FROM ifms.nsdl_frm_s1_file_no) as rec_seq,form.NOMINEE_1_PERCENT_SHARE,form.NOMINEE_2_PERCENT_SHARE,form.NOMINEE_3_PERCENT_SHARE,nvl(form.PRESENT_ADD_FLAT_UNIT_NO_BLOCK_NO,' '),nvl(form.PRESENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE,' '),nvl(form.PRESENT_ADDRESS_AREA_LOCALITY_TALUKA,' '),nvl(form.PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO,' '),nvl(form.PERMANENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE,' '),nvl(form.PERMANENT_ADDRESS_AREA_LOCALITY_TALUKA,' '),ROW_NUMBER() OVER (order by form.form_S1_id) as rn  FROM FRM_FORM_S1_DTLS form ");
		//Strbld.append(" SELECT ROW_NUMBER() OVER (order by substr(form.ddo_code,1,2)) as rn,form.EMP_NAME,to_char(emp.dob,'dd-MM-yyyy'),to_char(emp.doj,'dd-MM-yyyy'),emp.nsdl_ppan,emp.pran_no,emp.sevarth_id,dto.DTO_reg_no,ddo.ddo_reg_no ");  
		//Strbld.append(" FROM FRM_FORM_S1_DTLS form  "); 
		Strbld.append(" inner   join mst_dcps_emp emp on emp.SEVARTH_ID=form.SEVARTH_ID  left outer join HR_EIS_SCALE_MST scale on scale.SCALE_ID=emp.PAYSCALE ");   
		Strbld.append(" INNER JOIN ORG_EMP_MST oremp on oremp.EMP_ID=emp.ORG_EMP_MST_ID "); 
		Strbld.append(" inner JOIN ORG_GRADE_MST grade on oremp.GRADE_ID=grade.GRADE_ID "); 
		Strbld.append(" inner join MST_BANK_PAY pay on pay.BANK_CODE=emp.BANK_NAME "); 
		Strbld.append(" inner join RLT_BANK_BRANCH_PAY br on emp.BRANCH_NAME=br.BRANCH_ID "); 
		Strbld.append(" inner join org_ddo_mst oddo on oddo.DDO_CODE =emp.DDO_CODE "); 
		Strbld.append(" inner join CMN_LOCATION_MST cmn on cmn.LOC_ID=oddo.DEPT_LOC_CODE "); 
		Strbld.append(" inner join CMN_LOCATION_MST cmnloc on cmnloc.LOC_ID=oddo.HOD_LOC_CODE "); 
		Strbld.append(" inner join MST_DTO_REG dto on substr(dto.dto_code,1,2)=substr(form.ddo_code,1,2)   "); 
		Strbld.append(" inner join MST_DDO_REG ddo on ddo.ddo_code=form.DDO_CODE  "); 
		Strbld.append(" where emp.PRAN_NO is not null and substr(form.ddo_code,1,2) like '"+treasury+"%' and form.NSDL_STATUS is null "); 
		//--and form.TREASURY_EMP_COUNT between "+string+" and "+string2);"); 
		Strbld.append(" ) "); 
		Strbld.append(" a where  rn  between "+string+" and "+string2); */
	//code here ...s
		
		
		
		////latest for final modification update query here
		
		
	/*	Strblds.append(" update frm_Form_s1_dtls set nsdl_Status='"+string3+"' where form_S1_id in (select fid from ( select form.form_s1_id as fid FROM FRM_FORM_S1_DTLS form  ");
		Strblds.append(" inner   join mst_dcps_emp emp on emp.SEVARTH_ID=form.SEVARTH_ID  left outer join HR_EIS_SCALE_MST scale on scale.SCALE_ID=emp.PAYSCALE ");    
		Strblds.append(" INNER JOIN ORG_EMP_MST oremp on oremp.EMP_ID=emp.ORG_EMP_MST_ID  ");
		Strblds.append(" inner JOIN ORG_GRADE_MST grade on oremp.GRADE_ID=grade.GRADE_ID ");  
		Strblds.append(" inner join MST_BANK_PAY pay on pay.BANK_CODE=emp.BANK_NAME  ");
		Strblds.append(" inner join RLT_BANK_BRANCH_PAY br on emp.BRANCH_NAME=br.BRANCH_ID ");  
		Strblds.append(" inner join org_ddo_mst oddo on oddo.DDO_CODE =emp.DDO_CODE  ");
		Strblds.append(" inner join CMN_LOCATION_MST cmn on cmn.LOC_ID=oddo.DEPT_LOC_CODE  ");
		Strblds.append(" inner join CMN_LOCATION_MST cmnloc on cmnloc.LOC_ID=oddo.HOD_LOC_CODE  ");
		Strblds.append(" inner join MST_DTO_REG dto on substr(dto.dto_code,1,2)=substr(form.ddo_code,1,2) ");    
		Strblds.append(" inner join MST_DDO_REG ddo on ddo.ddo_code=form.DDO_CODE   ");
		Strblds.append(" where emp.PRAN_NO is not null and substr(form.ddo_code,1,2) like '"+treasury+"%' and form.NSDL_STATUS is null  ");
		Strblds.append(" and (emp.ac_dcps_maintained_by=700174 or emp.ac_dcps_maintained_by is null) and form.TREASURY_EMP_COUNT between "+string+" and "+string2+" )) ");
		
		*/ //commented by akshay for new update method
	/*	latest rowcount update query here
		
		Strblds.append(" update frm_Form_s1_dtls set nsdl_Status='Y' where form_S1_id in (select fid from (  ");
		Strblds.append(" SELECT ROW_NUMBER() OVER (order by form.form_S1_id) as rn,form.form_S1_id as fid FROM FRM_FORM_S1_DTLS form ");
		Strblds.append(" inner   join mst_dcps_emp emp on emp.SEVARTH_ID=form.SEVARTH_ID  left outer join HR_EIS_SCALE_MST scale on scale.SCALE_ID=emp.PAYSCALE ");   
		Strblds.append(" INNER JOIN ORG_EMP_MST oremp on oremp.EMP_ID=emp.ORG_EMP_MST_ID "); 
		Strblds.append(" inner JOIN ORG_GRADE_MST grade on oremp.GRADE_ID=grade.GRADE_ID "); 
		Strblds.append(" inner join MST_BANK_PAY pay on pay.BANK_CODE=emp.BANK_NAME "); 
		Strblds.append(" inner join RLT_BANK_BRANCH_PAY br on emp.BRANCH_NAME=br.BRANCH_ID "); 
		Strblds.append(" inner join org_ddo_mst oddo on oddo.DDO_CODE =emp.DDO_CODE "); 
		Strblds.append(" inner join CMN_LOCATION_MST cmn on cmn.LOC_ID=oddo.DEPT_LOC_CODE "); 
		Strblds.append(" inner join CMN_LOCATION_MST cmnloc on cmnloc.LOC_ID=oddo.HOD_LOC_CODE "); 
		Strblds.append(" inner join MST_DTO_REG dto on substr(dto.dto_code,1,2)=substr(form.ddo_code,1,2)   "); 
		Strblds.append(" inner join MST_DDO_REG ddo on ddo.ddo_code=form.DDO_CODE  "); 
		Strblds.append(" where emp.PRAN_NO is not null and substr(form.ddo_code,1,2) like '"+treasury+"%' and form.NSDL_STATUS is null "); 
					//--and form.TREASURY_EMP_COUNT between "+string+" and "+string2);"); 
		Strblds.append(" ) "); 
		Strblds.append(" a where  rn  between "+string+" and "+string2+")"); 
				 
				 
		*/		 
				 /* sample query running
				  * 
				  * 
update frm_Form_s1_dtls set NSDL_STATUS='Y' where form_S1_id in (select fid from (  
		 SELECT ROW_NUMBER() OVER (order by substr(form.ddo_code,1,2)) as rn,form.form_S1_id as fid FROM FRM_FORM_S1_DTLS form 
		-- inner   join mst_dcps_emp emp on emp.SEVARTH_ID=form.SEVARTH_ID  left outer join HR_EIS_SCALE_MST scale on scale.SCALE_ID=emp.PAYSCALE    
		-- INNER JOIN ORG_EMP_MST oremp on oremp.EMP_ID=emp.ORG_EMP_MST_ID  
		-- inner JOIN ORG_GRADE_MST grade on oremp.GRADE_ID=grade.GRADE_ID  
		-- inner join MST_BANK_PAY pay on pay.BANK_CODE=emp.BANK_NAME  
		-- inner join RLT_BANK_BRANCH_PAY br on emp.BRANCH_NAME=br.BRANCH_ID  
		-- inner join org_ddo_mst oddo on oddo.DDO_CODE =emp.DDO_CODE  
		-- inner join CMN_LOCATION_MST cmn on cmn.LOC_ID=oddo.DEPT_LOC_CODE  
		-- inner join CMN_LOCATION_MST cmnloc on cmnloc.LOC_ID=oddo.HOD_LOC_CODE  
		-- inner join MST_DTO_REG dto on substr(dto.dto_code,1,2)=substr(form.ddo_code,1,2)    
		-- inner join MST_DDO_REG ddo on ddo.ddo_code=form.DDO_CODE   
		 where 
		 --emp.PRAN_NO is not null 
		-- and 
		 substr(form.ddo_code,1,2) like '12%' and form.NSDL_STATUS='K'  
		 )  
		 a where  rn  between 1 and 6)
SELECT * FROM frm_form_s1_dtls where nsdl_status='L'

*/
				 

	//Strblds.append(" update frm_form_s1_dtls set NSDL_STATUS='Y' where TREASURY_EMP_COUNT between "+string+" and "+string2);
		/*
		Strbld.append(" SELECT nvl(form.EMP_NAME,' '),to_char(emp.dob,'MMddyyyy'),to_char(emp.doj,'MMddyyyy'),nvl(emp.nsdl_ppan,' '),nvl(emp.pran_no,' '),emp.sevarth_id,dto.DTO_CODE,nvl(ddo.ddo_reg_no,' ') ");
		Strbld.append(" FROM FRM_FORM_S1_DTLS form  inner   join mst_dcps_emp emp on emp.SEVARTH_ID=form.SEVARTH_ID ");
		Strbld.append(" left outer join HR_EIS_SCALE_MST scale on scale.SCALE_ID=emp.PAYSCALE ");
		Strbld.append(" inner join MST_DTO_REG dto on substr(dto.dto_reg_no,1,2)=substr(form.ddo_code,1,2) ");
		Strbld.append(" inner join MST_DDO_REG ddo on ddo.ddo_code=form.DDO_CODE ");
		Strbld.append(" where emp.PRAN_NO is not null and substr(form.ddo_code,1,2) like '"+treasury+"%' and form.TREASURY_EMP_COUNT between "+string+" and "+string2);
	*/	
		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
		
		temp=lQuery.list();
		int size=temp.size();
		Strbldx.append(" update ifms.nsdl_frm_s1_file_no set RECORD_SEQ_NO=(SELECT max(RECORD_SEQ_NO)+"+size+" FROM nsdl_frm_s1_file_no) where RECORD_SEQ_NO=(SELECT max(RECORD_SEQ_NO) FROM nsdl_frm_s1_file_no) ");
		SQLQuery lQueryx = ghibSession.createSQLQuery(Strbldx.toString());
		tempx=lQueryx.executeUpdate();
	//	SQLQuery lQuerys = ghibSession.createSQLQuery(Strblds.toString());  commented by akshay for new update method
		//int temps=lQuerys.executeUpdate();   commented by akshay for new update method
		
		return temp;
	}

	

	public String getFileId(String date) {
	
		
	
		
		
	int temp=0;
	String temps=null;
	String tempseq=null;
		
		StringBuilder  Strbld = new StringBuilder();
		StringBuilder  Strblds = new StringBuilder();
		StringBuilder  Strbldss = new StringBuilder();
		
		
		
		Strbld.append(" update ifms.nsdl_frm_s1_file_no set file_id=(SELECT max(file_id)+1 FROM nsdl_frm_s1_file_no),GENERATED_DATE=sysdate where file_id=(SELECT max(file_id) FROM nsdl_frm_s1_file_no) and GENERATED_DATE like '"+date+"%' ");
		Strbldss.append(" update ifms.nsdl_frm_s1_file_no set file_id=0,GENERATED_DATE=sysdate where file_id=(SELECT max(file_id) FROM nsdl_frm_s1_file_no) and GENERATED_DATE not like '"+date+"%' ");

		//Strbld.append(" insert into ifms.nsdl_frm_s1_file_no values (SELECT max(file_id)+1 FROM nsdl_frm_s1_file_no); ");
		
		Strblds.append(" SELECT max(file_id)||'#'||max(RECORD_SEQ_NO) FROM ifms.nsdl_frm_s1_file_no ");
		
		
		SQLQuery lQueryss = ghibSession.createSQLQuery(Strbldss.toString());
		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
		SQLQuery lQuerys = ghibSession.createSQLQuery(Strblds.toString());
		
	
		temp=lQueryss.executeUpdate();
		temp=lQuery.executeUpdate();
		
		
		temps=lQuerys.uniqueResult().toString();
		
		
		return temps;
	}

	@Override
	public void updateFlagEmpListPrint(String treasurynos, String string,
			String string2, String file_name,String form_s1_id) {
		List temp=null;
		StringBuilder  Strblds = new StringBuilder();
		StringBuilder  Strbld = new StringBuilder();
		String treasury=treasurynos.substring(0,2);
		StringBuilder  Strbldx = new StringBuilder();
		int tempx=0;
		
		/*Strblds.append(" update frm_Form_s1_dtls set nsdl_Status='Y"+file_name+"' where form_S1_id in (select fid from ( select form.form_s1_id as fid FROM FRM_FORM_S1_DTLS form  ");
		Strblds.append(" inner   join mst_dcps_emp emp on emp.SEVARTH_ID=form.SEVARTH_ID  left outer join HR_EIS_SCALE_MST scale on scale.SCALE_ID=emp.PAYSCALE ");    
		Strblds.append(" INNER JOIN ORG_EMP_MST oremp on oremp.EMP_ID=emp.ORG_EMP_MST_ID  ");
		Strblds.append(" inner JOIN ORG_GRADE_MST grade on oremp.GRADE_ID=grade.GRADE_ID ");  
		Strblds.append(" inner join MST_BANK_PAY pay on pay.BANK_CODE=emp.BANK_NAME  ");
		Strblds.append(" inner join RLT_BANK_BRANCH_PAY br on emp.BRANCH_NAME=br.BRANCH_ID ");  
		Strblds.append(" inner join org_ddo_mst oddo on oddo.DDO_CODE =emp.DDO_CODE  ");
		Strblds.append(" inner join CMN_LOCATION_MST cmn on cmn.LOC_ID=oddo.DEPT_LOC_CODE  ");
		Strblds.append(" inner join CMN_LOCATION_MST cmnloc on cmnloc.LOC_ID=oddo.HOD_LOC_CODE  ");
		Strblds.append(" inner join MST_DTO_REG dto on substr(dto.dto_code,1,2)=substr(form.ddo_code,1,2) ");    
		Strblds.append(" inner join MST_DDO_REG ddo on ddo.ddo_code=form.DDO_CODE   ");
		Strblds.append(" where emp.PRAN_NO is not null and substr(form.ddo_code,1,2) like '"+treasury+"%' and form.NSDL_STATUS is null  ");
		Strblds.append(" and (emp.ac_dcps_maintained_by=700174 or emp.ac_dcps_maintained_by is null) and form.TREASURY_EMP_COUNT between "+string+" and "+string2+" )) ");
	*/
		
		Strblds.append(" update frm_Form_s1_dtls set nsdl_Status='Y"+file_name+"v2' where form_S1_id in "+form_s1_id);
	/*---------------------------------------------------------------------------------------------------------------------------------	
		Strblds.append(" update frm_Form_s1_dtls set nsdl_Status='Y"+file_name+"' where form_S1_id in (select fid from ( select form.form_s1_id as fid FROM FRM_FORM_S1_DTLS form  ");
		Strblds.append(" inner   join mst_dcps_emp emp on emp.SEVARTH_ID=form.SEVARTH_ID  left outer join HR_EIS_SCALE_MST scale on scale.SCALE_ID=emp.PAYSCALE    ");
		Strblds.append(" INNER JOIN ORG_EMP_MST oremp on oremp.EMP_ID=emp.ORG_EMP_MST_ID  ");
		Strblds.append(" inner JOIN ORG_GRADE_MST grade on oremp.GRADE_ID=grade.GRADE_ID  ");
		Strblds.append(" left outer join MST_BANK_PAY pay on pay.BANK_CODE=emp.BANK_NAME  ");
		Strblds.append(" left outer join RLT_BANK_BRANCH_PAY br on emp.BRANCH_NAME=br.BRANCH_ID   ");
		Strblds.append(" inner join nsdl_Subscriber_Ack_id sub on sub.DCPS_EMP_ID = emp.DCPS_EMP_ID and sub.ACK_ID is not null ");
		Strblds.append(" inner join org_ddo_mst oddo on oddo.DDO_CODE =form.DDO_CODE   ");
		Strblds.append(" inner join CMN_LOCATION_MST cmn on cmn.LOC_ID=oddo.DEPT_LOC_CODE  "); 
		Strblds.append(" inner join CMN_LOCATION_MST cmnloc on cmnloc.LOC_ID=oddo.HOD_LOC_CODE   ");
		Strblds.append(" inner join MST_DTO_REG dto on substr(dto.dto_code,1,2)=substr(form.ddo_code,1,2)    "); 
		Strblds.append(" inner join MST_DDO_REG ddo on ddo.ddo_code=form.DDO_CODE    ");
		Strblds.append(" where emp.PRAN_NO is not null and substr(form.ddo_code,1,2) like '"+treasury+"%' and form.NSDL_STATUS is null   ");
		Strblds.append(" and (emp.ac_dcps_maintained_by=700174 or emp.ac_dcps_maintained_by is null) and form.TREASURY_EMP_COUNT between "+string+" and "+string2+" ");
		Strblds.append(" and emp.dcps_emp_id not in (SELECT distinct hst.dcps_emp_id FROM TRN_DCPS_CHANGES hst inner join mst_dcps_emp emp on emp.dcps_emp_id = hst.dcps_emp_id and hst.ddo_code = emp.ddo_code "); 
		Strblds.append(" inner join FRM_FORM_S1_DTLS form on emp.SEVARTH_ID = form.SEVARTH_ID where hst.TYPE_OF_CHANGES = 'PersonalDetails' and hst.FIELD_NAME = 'Full Name' and substr(hst.ddo_code,1,2) like '"+treasury+"%' and emp.reg_status = 1 and emp.form_status = 1 and form.TREASURY_EMP_COUNT between "+string+" and "+string2+"))) ");
--------------------------------------------------------------------------------------------------------------------------------------*/
		 
		 
		SQLQuery lQuerys = ghibSession.createSQLQuery(Strblds.toString());
		int temps=lQuerys.executeUpdate();
		
	}

	

}
