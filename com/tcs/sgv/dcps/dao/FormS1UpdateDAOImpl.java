package com.tcs.sgv.dcps.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.valueobject.FrmFormS1Dtls;

public class FormS1UpdateDAOImpl extends GenericDaoHibernateImpl implements
		FormS1UpdateDAO {
	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	public FormS1UpdateDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);

	}

	@SuppressWarnings("unchecked")
	@Override
	public List getEmpListForFrmS1Edit(String strDDOCode, String flag,
			String txtSearch, String isDeputation) {
		Session hibSession = getSession();
		StringBuffer strQuery = new StringBuffer();
		strQuery.append(" select empmst.SEVARTH_ID,empmst.EMP_NAME ,VARCHAR_FORMAT(org.EMP_DOJ, 'dd/MM/yyyy'), ");
		strQuery.append(" desig.DSGN_NAME, empmst.DDO_CODE, empmst.DCPS_ID,empmst.PAN_NO,bkp.IS_PRINT_CSRF ");// //$t27Jun2022/////$t19Dec2022
		strQuery.append(" from MST_DCPS_EMP empmst ");
		strQuery.append(" inner join org_emp_mst org on org.EMP_ID = empmst.ORG_EMP_MST_ID ");
		strQuery.append(" inner join ORG_USERPOST_RLT userpost on userpost.USER_ID=org.USER_ID  ");
		strQuery.append(" inner join ORG_POST_MST post on post.POST_ID=userpost.POST_ID ");
		strQuery.append(" inner join ORG_DESIGNATION_MST desig on empmst.DESIGNATION=desig.DSGN_ID ");
		strQuery.append(" left outer join FRM_FORM_S1_DTLS_bkp bkp on empmst.sevarth_id=bkp.sevarth_id ");// ///$t19Dec2022
		strQuery.append(" where empmst.EMP_SERVEND_DT > sysdate ");
		if (isDeputation.equals("Y")) {

		} else {
			strQuery.append(" and empmst.DDO_CODE='" + strDDOCode + "' ");
		}
		strQuery.append(" and empmst.DCPS_OR_GPF='Y' and  empmst.AC_DCPS_MAINTAINED_BY in (700174,700240,700241,700242) ");
		if (isDeputation.equals("Y")) {
			strQuery.append(" and not exists (select null FROM FRM_FORM_S1_DTLS frms1 where empmst.SEVARTH_ID=frms1.sevarth_id ) ");
		} else {
			strQuery.append(" and not exists (select null FROM FRM_FORM_S1_DTLS frms1 where empmst.SEVARTH_ID=frms1.sevarth_id ) and userpost.ACTIVATE_FLAG = 1 and post.ACTIVATE_FLAG = 1 and empmst.ddo_code is not null ");
			// //$t OPGM 9-9-2020
			// strQuery.append(" and empmst.PRAN_NO is null ");

		}
		if (isDeputation.equals("Y")) {
			strQuery.append(" and empmst.ddo_code is null ");
			strQuery.append(" and ((empmst.DEPT_DDO_CODE ='" + strDDOCode
					+ "'  and '" + strDDOCode + "'  ");
			strQuery.append(" in (SELECT hodddo.ddo_code FROM mst_dcps_Emp emp  ");
			strQuery.append(" inner join cmn_location_mst loc on loc.loc_id=emp.PARENT_DEPT  ");
			strQuery.append(" inner join ACL_HODDDO_RLT hodddo on hodddo.HOD_LOC_ID=loc.PARENT_LOC_ID  where emp.sevarth_id='"
					+ txtSearch.trim().toUpperCase()
					+ "') ) or ( empmst.DEPT_DDO_CODE is null )) ");
		}

		if (flag.equals("sevarthId")) {
			strQuery.append(" and empmst.SEVARTH_ID='"
					+ txtSearch.trim().toUpperCase() + "' ");
		}
		if (flag.equals("dsgn")) {
			strQuery.append(" and desig.DSGN_ID  = '"
					+ txtSearch.trim().toUpperCase() + "' ");
		}
		strQuery.append(" group by empmst.SEVARTH_ID,empmst.EMP_NAME,org.EMP_DOJ,desig.DSGN_NAME ,");
		strQuery.append(" empmst.DDO_CODE, ");

		strQuery.append(" empmst.DCPS_ID,empmst.PAN_NO,bkp.IS_PRINT_CSRF ");// //$t27Jun2022/////$t19Dec2022

		logger.info("Query to get Emp List For Frm S1 Edit is "
				+ strQuery.toString());
		Query query = hibSession.createSQLQuery(strQuery.toString());
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List getRelationList() {
		Session hibSession = getSession();
		StringBuffer strQuery = new StringBuffer();
		strQuery.append(" SELECT  LOOKUP_ID,LOOKUP_SHORT_NAME FROM CMN_LOOKUP_MST where PARENT_LOOKUP_ID = 230045 and LOOKUP_ID <>230052 ");

		logger.info("Query to get relations List For Frm S1 Edit is "
				+ strQuery.toString());
		Query query = hibSession.createSQLQuery(strQuery.toString());
		return query.list();
	}

	@Override
	public void insertRecordToS1(FrmFormS1Dtls ffs, String doj,
			String nominee1DOB, String nominee2DOB, String nominee3DOB,
			String strDDOCode, String OrphanPerson, Long attachment_Id_order) {
		Session hibSession = getSession();
		StringBuffer strQuery = new StringBuffer();
		String ddoc = strDDOCode.substring(0, 2);
		gLogger.info(" ddoc " + ddoc);
		gLogger.info(" doj " + doj);
		gLogger.info("nominee1DOB " + nominee1DOB);
		gLogger.info("nominee2DOB " + nominee2DOB);
		gLogger.info("nominee3DOB " + nominee3DOB);

		// //$t OPGM 17-9-20 check pran is already generated
		List sev = null;
		String present = "N";
		StringBuffer strQuery1 = new StringBuffer();
		strQuery1
				.append(" SELECT  pran_no FROM mst_dcps_emp where sevarth_id = '"
						+ ffs.getSevarthId()
						+ "' and dcps_id= '"
						+ ffs.getDcpsId() + "' ");
		logger.info("Query to get check pran is already generated is "
				+ strQuery1.toString());
		Query query1 = hibSession.createSQLQuery(strQuery1.toString());
		sev = query1.list();
		if ((!sev.isEmpty()) && (sev.size() > 0) && (sev.get(0) != null)) {
			present = "Y";
		}
		// //$t OPGM

/*		strQuery.append(" insert into FRM_FORM_S1_DTLS ");
		strQuery.append(" (SEVARTH_ID,EMP_NAME,DCPS_ID,DESIGNATION, ");
		strQuery.append(" DOJ,DDO_CODE,FATHER_NAME, ");
		strQuery.append(" PRESENT_ADD_FLAT_UNIT_NO_BLOCK_NO,PRESENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE, ");
		strQuery.append(" PRESENT_ADDRESS_AREA_LOCALITY_TALUKA,PRESENT_ADDRESS_DISTRICT_TOWN_CITY, ");
		strQuery.append(" PRESENT_ADDRESS_STATE_UNION_TERRITORY,PRESENT_ADDRESS_COUNTRY, ");
		strQuery.append(" PRESENT_ADDRESS_PIN_CODE, ");
		strQuery.append(" PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO,PERMANENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE, ");
		strQuery.append(" PERMANENT_ADDRESS_AREA_LOCALITY_TALUKA,PERMANENT_ADDRESS_DISTRICT_TOWN_CITY, ");
		strQuery.append(" PERMANENT_ADDRESS_STATE_UNION_TERRITORY,PERMANENT_ADDRESS_COUNTRY, ");
		strQuery.append(" PERMANENT_ADDRESS_PIN_CODE, ");
		strQuery.append(" PHONE_NO_STD_CODE,PHONE_NO_PHONE_NO, ");
		strQuery.append(" MOBILE_NO,EMAIL_ID, ");
		strQuery.append(" NOMINEE_1_NAME,NOMINEE_1_DOB,NOMINEE_1_RELATIONSHIP, ");
		strQuery.append(" NOMINEE_1_PERCENT_SHARE,NOMINEE_1_GUARDIAN_NAME,NOMINEE_1_NOMINATION_INVALID_CONDITION, ");
		strQuery.append(" NOMINEE_2_NAME,NOMINEE_2_DOB,NOMINEE_2_RELATIONSHIP, ");
		strQuery.append(" NOMINEE_2_PERCENT_SHARE,NOMINEE_2_GUARDIAN_NAME,NOMINEE_2_NOMINATION_INVALID_CONDITION, ");
		strQuery.append(" NOMINEE_3_NAME,NOMINEE_3_DOB,NOMINEE_3_RELATIONSHIP, ");
		strQuery.append(" NOMINEE_3_PERCENT_SHARE,NOMINEE_3_GUARDIAN_NAME,NOMINEE_3_NOMINATION_INVALID_CONDITION, ");
		strQuery.append(" CREATED_DATE,TREASURY_EMP_COUNT,NSDL_STATUS, ");// added
																			// by
																			// akshay
		strQuery.append(" MOTHER_NAME,CITY_OF_BIRTH,COUNTRY_OF_BIRTH,MARTIAL_STATUS, ");
		strQuery.append(" SPOUSE_NAME,US_PERSON,COUNTRY_OF_TAX, ");
		strQuery.append(" ADDRESS_OF_TAX,CITY_OF_TAX, ");
		strQuery.append(" STATE_OF_TAX,POST_CODE_OF_TAX,TIN_OR_PAN,TIN_COUNTRY,STAGE) ");
		strQuery.append(" values(:SEVARTH_ID,:EMP_NAME,:DCPS_ID,:DESIGNATION,");
		strQuery.append(" :DOJ,:DDO_CODE,:FATHER_NAME, ");
		strQuery.append(" :PRESENT_ADD_FLAT_UNIT_NO_BLOCK_NO,:PRESENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE, ");
		strQuery.append(" :PRESENT_ADDRESS_AREA_LOCALITY_TALUKA,:PRESENT_ADDRESS_DISTRICT_TOWN_CITY, ");
		strQuery.append(" :PRESENT_ADDRESS_STATE_UNION_TERRITORY,:PRESENT_ADDRESS_COUNTRY, ");
		strQuery.append(" :PRESENT_ADDRESS_PIN_CODE,  ");
		strQuery.append(" :PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO,:PERMANENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE, ");
		strQuery.append(" :PERMANENT_ADDRESS_AREA_LOCALITY_TALUKA,:PERMANENT_ADDRESS_DISTRICT_TOWN_CITY,   ");
		strQuery.append(" :PERMANENT_ADDRESS_STATE_UNION_TERRITORY,:PERMANENT_ADDRESS_COUNTRY, ");
		strQuery.append(" :PERMANENT_ADDRESS_PIN_CODE, ");
		strQuery.append(" :PHONE_NO_STD_CODE,:PHONE_NO_PHONE_NO, ");
		strQuery.append(" :MOBILE_NO,:EMAIL_ID, ");
		strQuery.append(" :NOMINEE_1_NAME,:NOMINEE_1_DOB,:NOMINEE_1_RELATIONSHIP, ");
		strQuery.append(" :NOMINEE_1_PERCENT_SHARE,:NOMINEE_1_GUARDIAN_NAME,:NOMINEE_1_NOMINATION_INVALID_CONDITION, ");
		strQuery.append(" :NOMINEE_2_NAME,:NOMINEE_2_DOB,:NOMINEE_2_RELATIONSHIP, ");
		strQuery.append(" :NOMINEE_2_PERCENT_SHARE,:NOMINEE_2_GUARDIAN_NAME,:NOMINEE_2_NOMINATION_INVALID_CONDITION, ");
		strQuery.append(" :NOMINEE_3_NAME,:NOMINEE_3_DOB,:NOMINEE_3_RELATIONSHIP, ");
		strQuery.append(" :NOMINEE_3_PERCENT_SHARE,:NOMINEE_3_GUARDIAN_NAME,:NOMINEE_3_NOMINATION_INVALID_CONDITION, ");
		strQuery.append(" sysdate,(SELECT max(treasury_emp_count)+1 FROM frm_form_s1_dtls where substr(ddo_code,1,4) like '"
				+ ddoc + "%'),null, "); // added by akshay
		strQuery.append(" :MOTHER_NAME,:CITY_OF_BIRTH,:COUNTRY_OF_BIRTH,:MARTIAL_STATUS, ");
		strQuery.append(" :SPOUSE_NAME,:US_PERSON,:COUNTRY_OF_TAX,:ADDRESS_OF_TAX, ");
		strQuery.append(" :CITY_OF_TAX,:STATE_OF_TAX,:POST_OF_TAX,:TIN_OR_PIN,:TIN_COUNTRY,:STAGE) ");
		// strQuery.setParameter("ORPHAN",OrphanPerson); //jitu OrphanPerson
		logger.info("Query to save Frm S1 is: " + strQuery.toString());
		Query query = hibSession.createSQLQuery(strQuery.toString());

		gLogger.info("SEVARTH_ID: " + ffs.getSevarthId());
		gLogger.info("EMP_NAME: " + ffs.getEmpName());
		gLogger.info("DCPS_ID: " + ffs.getDcpsId());
		gLogger.info("DESIGNATION: " + ffs.getDcpsId());
		gLogger.info("DOJ: " + doj);
		gLogger.info("DDO_CODE: " + strDDOCode);// get ddo loacaly
		gLogger.info("FATHER_NAME: " + ffs.getEmpFatherName());
		gLogger.info("PRESENT_ADD_FLAT_UNIT_NO_BLOCK_NO: "
				+ ffs.getPresentAddFlatNo());
		gLogger.info("PRESENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE: "
				+ ffs.getPresentAddBuilding());
		gLogger.info("PRESENT_ADDRESS_AREA_LOCALITY_TALUKA: "
				+ ffs.getPresentAddTaluka());
		gLogger.info("PRESENT_ADDRESS_DISTRICT_TOWN_CITY: "
				+ ffs.getPresentAddDist());
		gLogger.info("PRESENT_ADDRESS_STATE_UNION_TERRITORY: "
				+ ffs.getPresentAddState());
		gLogger.info("PRESENT_ADDRESS_COUNTRY: " + "India");
		gLogger.info("PRESENT_ADDRESS_PIN_CODE: " + ffs.getPresentAddPin());
		gLogger.info("PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO: "
				+ ffs.getPermanentAddFlatNo());
		gLogger.info("PERMANENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE: "
				+ ffs.getPermanentAddBuilding());
		gLogger.info("PERMANENT_ADDRESS_AREA_LOCALITY_TALUKA: "
				+ ffs.getPermanentAddTaluka());
		gLogger.info("PERMANENT_ADDRESS_DISTRICT_TOWN_CITY: "
				+ ffs.getPermanentAddDist());
		gLogger.info("PERMANENT_ADDRESS_STATE_UNION_TERRITORY: "
				+ ffs.getPermanentAddState());
		gLogger.info("PERMANENT_ADDRESS_COUNTRY: " + "India");
		gLogger.info("PERMANENT_ADDRESS_PIN_CODE: " + ffs.getPermanentAddPin());
		gLogger.info("PHONE_NO_STD_CODE: " + ffs.getPhoneSTDCode());
		gLogger.info("PHONE_NO_PHONE_NO: " + ffs.getPhoneNo());
		gLogger.info("MOBILE_NO: " + "+91" + ffs.getMobileNo());
		gLogger.info("EMAIL_ID: " + ffs.getEmailId());
		gLogger.info("NOMINEE_1_NAME: " + ffs.getNominee1Name());
		gLogger.info("NOMINEE_1_DOB: " + nominee1DOB);
		gLogger.info("NOMINEE_1_RELATIONSHIP: " + ffs.getNominee1Relation());
		gLogger.info("NOMINEE_1_PERCENT_SHARE: " + ffs.getNominee1Percent());
		gLogger.info("NOMINEE_1_GUARDIAN_NAME: " + ffs.getNominee1Guardian());
		gLogger.info("NOMINEE_1_NOMINATION_INVALID_CONDITION: "
				+ ffs.getNominee1InvalidCondition());
		gLogger.info("NOMINEE_2_NAME: " + ffs.getNominee2Name());
		gLogger.info("NOMINEE_2_DOB: " + nominee2DOB);
		gLogger.info("NOMINEE_2_RELATIONSHIP: " + ffs.getNominee2Relation());
		gLogger.info("NOMINEE_2_PERCENT_SHARE: " + ffs.getNominee2Percent());
		gLogger.info("NOMINEE_2_GUARDIAN_NAME: " + ffs.getNominee2Guardian());
		gLogger.info("NOMINEE_2_NOMINATION_INVALID_CONDITION: "
				+ ffs.getNominee2InvalidCondition());
		gLogger.info("NOMINEE_3_NAME: " + ffs.getNominee3Name());
		gLogger.info("NOMINEE_3_DOB: " + nominee3DOB);
		gLogger.info("NOMINEE_3_RELATIONSHIP: " + ffs.getNominee3Relation());
		gLogger.info("NOMINEE_3_PERCENT_SHARE: " + ffs.getNominee3Percent());
		gLogger.info("NOMINEE_3_GUARDIAN_NAME: " + ffs.getNominee3Guardian());
		gLogger.info("NOMINEE_3_NOMINATION_INVALID_CONDITION: "
				+ ffs.getNominee3InvalidCondition());
		// -----------------------------------------------------------------------------------------
		gLogger.info("MOTHER_NAME" + ffs.getEmpMotherName());
		gLogger.info("city of birth" + ffs.getEmpCityOfBirth());
		gLogger.info("country of birth" + ffs.getEmpCountryOfBirth());
		gLogger.info("emp spouse" + ffs.getEmpSpouseName());
		gLogger.info("married " + ffs.getMarriedOrNot());
		gLogger.info("usperson" + ffs.getUsPerson());
		gLogger.info("country of tax" + ffs.getEmpCountryOfTax());
		gLogger.info("tax address" + ffs.getTaxResAddress());
		gLogger.info("tax city" + ffs.getTaxResCity());
		gLogger.info("tax state" + ffs.getTaxResState());
		gLogger.info("tax pin" + ffs.getTaxResPostCode());
		gLogger.info("tin or pan" + ffs.getEmpTINOrPAN());
		gLogger.info("tin country" + ffs.getEmpTIN());

		query.setParameter("SEVARTH_ID", ffs.getSevarthId());
		query.setParameter("EMP_NAME", ffs.getEmpName().toUpperCase());// $t
																		// OPGM
																		// 26-8
		query.setParameter("DCPS_ID", ffs.getDcpsId());
		query.setParameter("DESIGNATION", ffs.getDcpsId());
		query.setParameter("DOJ", doj);
		query.setParameter("DDO_CODE", strDDOCode);// get ddo loacaly
		query.setParameter("FATHER_NAME", ffs.getEmpFatherName().toUpperCase());// $t
																				// OPGM
																				// 26-8
		query.setParameter("PRESENT_ADD_FLAT_UNIT_NO_BLOCK_NO", ffs
				.getPresentAddFlatNo().replaceAll("[^a-zA-Z0-9\\s+]", ""));
		query.setParameter("PRESENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE",
				ffs.getPresentAddBuilding().replaceAll("[^a-zA-Z0-9\\s+]", ""));
		query.setParameter("PRESENT_ADDRESS_AREA_LOCALITY_TALUKA", ffs
				.getPresentAddTaluka().replaceAll("[^a-zA-Z0-9\\s+]", ""));
		query.setParameter("PRESENT_ADDRESS_DISTRICT_TOWN_CITY", ffs
				.getPresentAddDist().replaceAll("[^a-zA-Z0-9\\s+]", ""));
		query.setParameter("PRESENT_ADDRESS_STATE_UNION_TERRITORY", ffs
				.getPresentAddState().replaceAll("[^a-zA-Z0-9\\s+]", ""));
		query.setParameter("PRESENT_ADDRESS_COUNTRY", "India");
		query.setParameter("PRESENT_ADDRESS_PIN_CODE", ffs.getPresentAddPin()
				.replaceAll("[^a-zA-Z0-9\\s+]", ""));
		query.setParameter("PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO", ffs
				.getPermanentAddFlatNo().replaceAll("[^a-zA-Z0-9\\s+]", ""));
		query.setParameter(
				"PERMANENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE",
				ffs.getPermanentAddBuilding()
						.replaceAll("[^a-zA-Z0-9\\s+]", ""));
		query.setParameter("PERMANENT_ADDRESS_AREA_LOCALITY_TALUKA", ffs
				.getPermanentAddTaluka().replaceAll("[^a-zA-Z0-9\\s+]", ""));
		query.setParameter("PERMANENT_ADDRESS_DISTRICT_TOWN_CITY", ffs
				.getPermanentAddDist().replaceAll("[^a-zA-Z0-9\\s+]", ""));
		query.setParameter("PERMANENT_ADDRESS_STATE_UNION_TERRITORY", ffs
				.getPermanentAddState().replaceAll("[^a-zA-Z0-9\\s+]", ""));
		query.setParameter("PERMANENT_ADDRESS_COUNTRY", "India");
		query.setParameter("PERMANENT_ADDRESS_PIN_CODE", ffs
				.getPermanentAddPin().replaceAll("[^a-zA-Z0-9\\s+]", ""));
		query.setParameter("PHONE_NO_STD_CODE", ffs.getPhoneSTDCode()
				.replaceAll("[^a-zA-Z0-9\\s+]", ""));
		query.setParameter("PHONE_NO_PHONE_NO",
				ffs.getPhoneNo().replaceAll("[^a-zA-Z0-9\\s+]", ""));
		query.setParameter("MOBILE_NO",
				"+91" + ffs.getMobileNo().replaceAll("[^a-zA-Z0-9\\s+]", ""));
		query.setParameter("EMAIL_ID", ffs.getEmailId());// //$t OPGM remove
															// spcial character
															// .replaceAll()
															// method
		query.setParameter("NOMINEE_1_NAME",
				ffs.getNominee1Name().replaceAll("[^a-zA-Z0-9\\s+]", "")
						.toUpperCase());// $t OPGM 26-8
		query.setParameter("NOMINEE_1_DOB", nominee1DOB);
		query.setParameter("NOMINEE_1_RELATIONSHIP", ffs.getNominee1Relation());
		query.setParameter("NOMINEE_1_PERCENT_SHARE", ffs.getNominee1Percent()
				.replaceAll("[^a-zA-Z0-9\\s+]", ""));
		query.setParameter("NOMINEE_1_GUARDIAN_NAME", ffs.getNominee1Guardian()
				.replaceAll("[^a-zA-Z0-9\\s+]", ""));
		query.setParameter(
				"NOMINEE_1_NOMINATION_INVALID_CONDITION",
				ffs.getNominee1InvalidCondition().replaceAll(
						"[^a-zA-Z0-9\\s+]", ""));
		query.setParameter("NOMINEE_2_NAME",
				ffs.getNominee2Name().replaceAll("[^a-zA-Z0-9\\s+]", ""));
		query.setParameter("NOMINEE_2_DOB", nominee2DOB);
		query.setParameter("NOMINEE_2_RELATIONSHIP", ffs.getNominee2Relation());
		query.setParameter("NOMINEE_2_PERCENT_SHARE", ffs.getNominee2Percent()
				.replaceAll("[^a-zA-Z0-9\\s+]", ""));
		query.setParameter("NOMINEE_2_GUARDIAN_NAME", ffs.getNominee2Guardian()
				.replaceAll("[^a-zA-Z0-9\\s+]", ""));
		query.setParameter(
				"NOMINEE_2_NOMINATION_INVALID_CONDITION",
				ffs.getNominee2InvalidCondition().replaceAll(
						"[^a-zA-Z0-9\\s+]", ""));
		query.setParameter("NOMINEE_3_NAME",
				ffs.getNominee3Name().replaceAll("[^a-zA-Z0-9\\s+]", ""));
		query.setParameter("NOMINEE_3_DOB", nominee3DOB);
		query.setParameter("NOMINEE_3_RELATIONSHIP", ffs.getNominee3Relation());
		query.setParameter("NOMINEE_3_PERCENT_SHARE", ffs.getNominee3Percent()
				.replaceAll("[^a-zA-Z0-9\\s+]", ""));
		query.setParameter("NOMINEE_3_GUARDIAN_NAME", ffs.getNominee3Guardian()
				.replaceAll("[^a-zA-Z0-9\\s+]", ""));
		query.setParameter(
				"NOMINEE_3_NOMINATION_INVALID_CONDITION",
				ffs.getNominee3InvalidCondition().replaceAll(
						"[^a-zA-Z0-9\\s+]", ""));
		// -------------------------------------------------------------------------------------------------
		query.setParameter("MOTHER_NAME", ffs.getEmpMotherName());
		query.setParameter("CITY_OF_BIRTH",
				ffs.getEmpCityOfBirth().replaceAll("[^a-zA-Z0-9\\s+]", ""));
		query.setParameter("COUNTRY_OF_BIRTH", ffs.getEmpCountryOfBirth()
				.replaceAll("[^a-zA-Z0-9\\s+]", ""));
		query.setParameter("MARTIAL_STATUS", ffs.getMarriedOrNot());
		query.setParameter("SPOUSE_NAME",
				ffs.getEmpSpouseName().replaceAll("[^a-zA-Z0-9\\s+]", ""));
		query.setParameter("US_PERSON", ffs.getUsPerson());
		query.setParameter("COUNTRY_OF_TAX", ffs.getEmpCountryOfTax()
				.replaceAll("[^a-zA-Z0-9\\s+]", ""));
		query.setParameter("ADDRESS_OF_TAX",
				ffs.getTaxResAddress().replaceAll("[^a-zA-Z0-9\\s+]", ""));
		query.setParameter("CITY_OF_TAX",
				ffs.getTaxResCity().replaceAll("[^a-zA-Z0-9\\s+]", ""));
		query.setParameter("STATE_OF_TAX",
				ffs.getTaxResState().replaceAll("[^a-zA-Z0-9\\s+]", ""));
		query.setParameter("POST_OF_TAX",
				ffs.getTaxResPostCode().replaceAll("[^a-zA-Z0-9\\s+]", ""));
		query.setParameter("TIN_OR_PIN",
				ffs.getEmpTINOrPAN().replaceAll("[^a-zA-Z0-9\\s+]", "")
						.toUpperCase());// $t OPGM 26-8
		query.setParameter("TIN_COUNTRY",
				ffs.getEmpTIN().replaceAll("[^a-zA-Z0-9\\s+]", ""));
*/		// //$t OPGM
		
		//jitu 
		  strQuery.append(" insert into FRM_FORM_S1_DTLS ");
	      strQuery.append(" (SEVARTH_ID,EMP_NAME,DCPS_ID,DESIGNATION, ");
	      strQuery.append(" DOJ,DDO_CODE,FATHER_NAME, ");
	      strQuery.append(" PRESENT_ADD_FLAT_UNIT_NO_BLOCK_NO,PRESENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE, ");
	      strQuery.append(" PRESENT_ADDRESS_AREA_LOCALITY_TALUKA,PRESENT_ADDRESS_DISTRICT_TOWN_CITY, ");
	      strQuery.append(" PRESENT_ADDRESS_STATE_UNION_TERRITORY,PRESENT_ADDRESS_COUNTRY, ");
	      strQuery.append(" PRESENT_ADDRESS_PIN_CODE, ");
	      strQuery.append(" PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO,PERMANENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE, ");
	      strQuery.append(" PERMANENT_ADDRESS_AREA_LOCALITY_TALUKA,PERMANENT_ADDRESS_DISTRICT_TOWN_CITY, ");
	      strQuery.append(" PERMANENT_ADDRESS_STATE_UNION_TERRITORY,PERMANENT_ADDRESS_COUNTRY, ");
	      strQuery.append(" PERMANENT_ADDRESS_PIN_CODE, ");
	      strQuery.append(" PHONE_NO_STD_CODE,PHONE_NO_PHONE_NO, ");
	      strQuery.append(" MOBILE_NO,EMAIL_ID, ");
	      strQuery.append(" NOMINEE_1_NAME,NOMINEE_1_DOB,NOMINEE_1_RELATIONSHIP, ");
	      strQuery.append(" NOMINEE_1_PERCENT_SHARE,NOMINEE_1_GUARDIAN_NAME,NOMINEE_1_NOMINATION_INVALID_CONDITION, ");
	      strQuery.append(" NOMINEE_2_NAME,NOMINEE_2_DOB,NOMINEE_2_RELATIONSHIP, ");
	      strQuery.append(" NOMINEE_2_PERCENT_SHARE,NOMINEE_2_GUARDIAN_NAME,NOMINEE_2_NOMINATION_INVALID_CONDITION, ");
	      strQuery.append(" NOMINEE_3_NAME,NOMINEE_3_DOB,NOMINEE_3_RELATIONSHIP, ");
	      strQuery.append(" NOMINEE_3_PERCENT_SHARE,NOMINEE_3_GUARDIAN_NAME,NOMINEE_3_NOMINATION_INVALID_CONDITION, ");
	      strQuery.append(" CREATED_DATE,TREASURY_EMP_COUNT,NSDL_STATUS, ");
	      strQuery.append(" MOTHER_NAME,CITY_OF_BIRTH,COUNTRY_OF_BIRTH,MARTIAL_STATUS, ");
	      strQuery.append(" SPOUSE_NAME,US_PERSON,COUNTRY_OF_TAX, ");
	      strQuery.append(" ADDRESS_OF_TAX,CITY_OF_TAX, ");
	      strQuery.append(" STATE_OF_TAX,POST_CODE_OF_TAX,TIN_OR_PAN,TIN_COUNTRY,STAGE,ORPHAN) ");
	      strQuery.append(" values(:SEVARTH_ID,:EMP_NAME,:DCPS_ID,:DESIGNATION,");
	      strQuery.append(" :DOJ,:DDO_CODE,:FATHER_NAME, ");
	      strQuery.append(" :PRESENT_ADD_FLAT_UNIT_NO_BLOCK_NO,:PRESENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE, ");
	      strQuery.append(" :PRESENT_ADDRESS_AREA_LOCALITY_TALUKA,:PRESENT_ADDRESS_DISTRICT_TOWN_CITY, ");
	      strQuery.append(" :PRESENT_ADDRESS_STATE_UNION_TERRITORY,:PRESENT_ADDRESS_COUNTRY, ");
	      strQuery.append(" :PRESENT_ADDRESS_PIN_CODE,  ");
	      strQuery.append(" :PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO,:PERMANENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE, ");
	      strQuery.append(" :PERMANENT_ADDRESS_AREA_LOCALITY_TALUKA,:PERMANENT_ADDRESS_DISTRICT_TOWN_CITY,   ");
	      strQuery.append(" :PERMANENT_ADDRESS_STATE_UNION_TERRITORY,:PERMANENT_ADDRESS_COUNTRY, ");
	      strQuery.append(" :PERMANENT_ADDRESS_PIN_CODE, ");
	      strQuery.append(" :PHONE_NO_STD_CODE,:PHONE_NO_PHONE_NO, ");
	      strQuery.append(" :MOBILE_NO,:EMAIL_ID, ");
	      strQuery.append(" :NOMINEE_1_NAME,:NOMINEE_1_DOB,:NOMINEE_1_RELATIONSHIP, ");
	      strQuery.append(" :NOMINEE_1_PERCENT_SHARE,:NOMINEE_1_GUARDIAN_NAME,:NOMINEE_1_NOMINATION_INVALID_CONDITION, ");
	      strQuery.append(" :NOMINEE_2_NAME,:NOMINEE_2_DOB,:NOMINEE_2_RELATIONSHIP, ");
	      strQuery.append(" :NOMINEE_2_PERCENT_SHARE,:NOMINEE_2_GUARDIAN_NAME,:NOMINEE_2_NOMINATION_INVALID_CONDITION, ");
	      strQuery.append(" :NOMINEE_3_NAME,:NOMINEE_3_DOB,:NOMINEE_3_RELATIONSHIP, ");
	      strQuery.append(" :NOMINEE_3_PERCENT_SHARE,:NOMINEE_3_GUARDIAN_NAME,:NOMINEE_3_NOMINATION_INVALID_CONDITION, ");
	      strQuery.append(" sysdate,(SELECT max(treasury_emp_count)+1 FROM frm_form_s1_dtls where substr(ddo_code,1,4) like '" + ddoc + "%'),null, ");
	      strQuery.append(" :MOTHER_NAME,:CITY_OF_BIRTH,:COUNTRY_OF_BIRTH,:MARTIAL_STATUS, ");
	      strQuery.append(" :SPOUSE_NAME,:US_PERSON,:COUNTRY_OF_TAX,:ADDRESS_OF_TAX, ");
	      strQuery.append(" :CITY_OF_TAX,:STATE_OF_TAX,:POST_OF_TAX,:TIN_OR_PIN,:TIN_COUNTRY,:STAGE,:ORPHAN) ");
	      this.logger.info("Query to save Frm S1 is: " + strQuery.toString());
	      Query query = hibSession.createSQLQuery(strQuery.toString());
	      this.gLogger.info("SEVARTH_ID: " + ffs.getSevarthId());
	      this.gLogger.info("EMP_NAME: " + ffs.getEmpName());
	      this.gLogger.info("DCPS_ID: " + ffs.getDcpsId());
	      this.gLogger.info("DESIGNATION: " + ffs.getDcpsId());
	      this.gLogger.info("DOJ: " + doj);
	      this.gLogger.info("DDO_CODE: " + strDDOCode);
	      this.gLogger.info("FATHER_NAME: " + ffs.getEmpFatherName());
	      this.gLogger.info("PRESENT_ADD_FLAT_UNIT_NO_BLOCK_NO: " + ffs.getPresentAddFlatNo());
	      this.gLogger.info("PRESENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE: " + ffs.getPresentAddBuilding());
	      this.gLogger.info("PRESENT_ADDRESS_AREA_LOCALITY_TALUKA: " + ffs.getPresentAddTaluka());
	      this.gLogger.info("PRESENT_ADDRESS_DISTRICT_TOWN_CITY: " + ffs.getPresentAddDist());
	      this.gLogger.info("PRESENT_ADDRESS_STATE_UNION_TERRITORY: " + ffs.getPresentAddState());
	      this.gLogger.info("PRESENT_ADDRESS_COUNTRY: India");
	      this.gLogger.info("PRESENT_ADDRESS_PIN_CODE: " + ffs.getPresentAddPin());
	      this.gLogger.info("PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO: " + ffs.getPermanentAddFlatNo());
	      this.gLogger.info("PERMANENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE: " + ffs.getPermanentAddBuilding());
	      this.gLogger.info("PERMANENT_ADDRESS_AREA_LOCALITY_TALUKA: " + ffs.getPermanentAddTaluka());
	      this.gLogger.info("PERMANENT_ADDRESS_DISTRICT_TOWN_CITY: " + ffs.getPermanentAddDist());
	      this.gLogger.info("PERMANENT_ADDRESS_STATE_UNION_TERRITORY: " + ffs.getPermanentAddState());
	      this.gLogger.info("PERMANENT_ADDRESS_COUNTRY: India");
	      this.gLogger.info("PERMANENT_ADDRESS_PIN_CODE: " + ffs.getPermanentAddPin());
	      this.gLogger.info("PHONE_NO_STD_CODE: " + ffs.getPhoneSTDCode());
	      this.gLogger.info("PHONE_NO_PHONE_NO: " + ffs.getPhoneNo());
	      this.gLogger.info("MOBILE_NO: +91" + ffs.getMobileNo());
	      this.gLogger.info("EMAIL_ID: " + ffs.getEmailId());
	      this.gLogger.info("NOMINEE_1_NAME: " + ffs.getNominee1Name());
	      this.gLogger.info("NOMINEE_1_DOB: " + nominee1DOB);
	      this.gLogger.info("NOMINEE_1_RELATIONSHIP: " + ffs.getNominee1Relation());
	      this.gLogger.info("NOMINEE_1_PERCENT_SHARE: " + ffs.getNominee1Percent());
	      this.gLogger.info("NOMINEE_1_GUARDIAN_NAME: " + ffs.getNominee1Guardian());
	      this.gLogger.info("NOMINEE_1_NOMINATION_INVALID_CONDITION: " + ffs.getNominee1InvalidCondition());
	      this.gLogger.info("NOMINEE_2_NAME: " + ffs.getNominee2Name());
	      this.gLogger.info("NOMINEE_2_DOB: " + nominee2DOB);
	      this.gLogger.info("NOMINEE_2_RELATIONSHIP: " + ffs.getNominee2Relation());
	      this.gLogger.info("NOMINEE_2_PERCENT_SHARE: " + ffs.getNominee2Percent());
	      this.gLogger.info("NOMINEE_2_GUARDIAN_NAME: " + ffs.getNominee2Guardian());
	      this.gLogger.info("NOMINEE_2_NOMINATION_INVALID_CONDITION: " + ffs.getNominee2InvalidCondition());
	      this.gLogger.info("NOMINEE_3_NAME: " + ffs.getNominee3Name());
	      this.gLogger.info("NOMINEE_3_DOB: " + nominee3DOB);
	      this.gLogger.info("NOMINEE_3_RELATIONSHIP: " + ffs.getNominee3Relation());
	      this.gLogger.info("NOMINEE_3_PERCENT_SHARE: " + ffs.getNominee3Percent());
	      this.gLogger.info("NOMINEE_3_GUARDIAN_NAME: " + ffs.getNominee3Guardian());
	      this.gLogger.info("NOMINEE_3_NOMINATION_INVALID_CONDITION: " + ffs.getNominee3InvalidCondition());
	      this.gLogger.info("MOTHER_NAME" + ffs.getEmpMotherName());
	      this.gLogger.info("city of birth" + ffs.getEmpCityOfBirth());
	      this.gLogger.info("country of birth" + ffs.getEmpCountryOfBirth());
	      this.gLogger.info("emp spouse" + ffs.getEmpSpouseName());
	      this.gLogger.info("married " + ffs.getMarriedOrNot());
	      this.gLogger.info("usperson" + ffs.getUsPerson());
	      this.gLogger.info("country of tax" + ffs.getEmpCountryOfTax());
	      this.gLogger.info("tax address" + ffs.getTaxResAddress());
	      this.gLogger.info("tax city" + ffs.getTaxResCity());
	      this.gLogger.info("tax state" + ffs.getTaxResState());
	      this.gLogger.info("tax pin" + ffs.getTaxResPostCode());
	      this.gLogger.info("tin or pan" + ffs.getEmpTINOrPAN());
	      this.gLogger.info("tin country" + ffs.getEmpTIN());
	      query.setParameter("SEVARTH_ID", ffs.getSevarthId());
	      query.setParameter("EMP_NAME", ffs.getEmpName().toUpperCase());
	      query.setParameter("DCPS_ID", ffs.getDcpsId());
	      query.setParameter("DESIGNATION", ffs.getDcpsId());
	      query.setParameter("DOJ", doj);
	      query.setParameter("DDO_CODE", strDDOCode);
	      query.setParameter("FATHER_NAME", ffs.getEmpFatherName().toUpperCase());
	      query.setParameter("PRESENT_ADD_FLAT_UNIT_NO_BLOCK_NO", ffs.getPresentAddFlatNo().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("PRESENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE", ffs.getPresentAddBuilding().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("PRESENT_ADDRESS_AREA_LOCALITY_TALUKA", ffs.getPresentAddTaluka().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("PRESENT_ADDRESS_DISTRICT_TOWN_CITY", ffs.getPresentAddDist().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("PRESENT_ADDRESS_STATE_UNION_TERRITORY", ffs.getPresentAddState().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("PRESENT_ADDRESS_COUNTRY", "India");
	      query.setParameter("PRESENT_ADDRESS_PIN_CODE", ffs.getPresentAddPin().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("PERMANENT_ADD_FLAT_UNIT_NO_BLOCK_NO", ffs.getPermanentAddFlatNo().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("PERMANENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE", ffs.getPermanentAddBuilding().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("PERMANENT_ADDRESS_AREA_LOCALITY_TALUKA", ffs.getPermanentAddTaluka().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("PERMANENT_ADDRESS_DISTRICT_TOWN_CITY", ffs.getPermanentAddDist().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("PERMANENT_ADDRESS_STATE_UNION_TERRITORY", ffs.getPermanentAddState().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("PERMANENT_ADDRESS_COUNTRY", "India");
	      query.setParameter("PERMANENT_ADDRESS_PIN_CODE", ffs.getPermanentAddPin().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("PHONE_NO_STD_CODE", ffs.getPhoneSTDCode().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("PHONE_NO_PHONE_NO", ffs.getPhoneNo().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("MOBILE_NO", "+91" + ffs.getMobileNo().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("EMAIL_ID", ffs.getEmailId());
	      query.setParameter("NOMINEE_1_NAME", ffs.getNominee1Name().replaceAll("[^a-zA-Z0-9\\s+]", "").toUpperCase());
	      query.setParameter("NOMINEE_1_DOB", nominee1DOB);
	      query.setParameter("NOMINEE_1_RELATIONSHIP", ffs.getNominee1Relation());
	      query.setParameter("NOMINEE_1_PERCENT_SHARE", ffs.getNominee1Percent().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("NOMINEE_1_GUARDIAN_NAME", ffs.getNominee1Guardian().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("NOMINEE_1_NOMINATION_INVALID_CONDITION", ffs.getNominee1InvalidCondition().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("NOMINEE_2_NAME", ffs.getNominee2Name().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("NOMINEE_2_DOB", nominee2DOB);
	      query.setParameter("NOMINEE_2_RELATIONSHIP", ffs.getNominee2Relation());
	      query.setParameter("NOMINEE_2_PERCENT_SHARE", ffs.getNominee2Percent().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("NOMINEE_2_GUARDIAN_NAME", ffs.getNominee2Guardian().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("NOMINEE_2_NOMINATION_INVALID_CONDITION", ffs.getNominee2InvalidCondition().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("NOMINEE_3_NAME", ffs.getNominee3Name().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("NOMINEE_3_DOB", nominee3DOB);
	      query.setParameter("NOMINEE_3_RELATIONSHIP", ffs.getNominee3Relation());
	      query.setParameter("NOMINEE_3_PERCENT_SHARE", ffs.getNominee3Percent().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("NOMINEE_3_GUARDIAN_NAME", ffs.getNominee3Guardian().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("NOMINEE_3_NOMINATION_INVALID_CONDITION", ffs.getNominee3InvalidCondition().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("MOTHER_NAME", ffs.getEmpMotherName());
	      query.setParameter("CITY_OF_BIRTH", ffs.getEmpCityOfBirth().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("COUNTRY_OF_BIRTH", ffs.getEmpCountryOfBirth().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("MARTIAL_STATUS", ffs.getMarriedOrNot());
	      query.setParameter("SPOUSE_NAME", ffs.getEmpSpouseName().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("US_PERSON", ffs.getUsPerson());
	      query.setParameter("COUNTRY_OF_TAX", ffs.getEmpCountryOfTax().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("ADDRESS_OF_TAX", ffs.getTaxResAddress().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("CITY_OF_TAX", ffs.getTaxResCity().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("STATE_OF_TAX", ffs.getTaxResState().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("POST_OF_TAX", ffs.getTaxResPostCode().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("TIN_OR_PIN", ffs.getEmpTINOrPAN().replaceAll("[^a-zA-Z0-9\\s+]", "").toUpperCase());
	      query.setParameter("TIN_COUNTRY", ffs.getEmpTIN().replaceAll("[^a-zA-Z0-9\\s+]", ""));
	      query.setParameter("ORPHAN",OrphanPerson); //jitu OrphanPerson

		if (present.equals("Y")) {
			query.setParameter("STAGE", 5);
		} else {
			query.setParameter("STAGE", 1);
		}

		query.executeUpdate();
	}

	@Override
	public Long checkFormS1(String strSevarthId) {

		Session hibSession = getSession();

		Long finalCheckFlag = null;
		StringBuffer sb = new StringBuffer();
		gLogger.info("strSevarthId : " + strSevarthId);
		sb.append("select count (1) from FRM_FORM_S1_DTLS where upper(SEVARTH_ID) = '"
				+ strSevarthId.toUpperCase() + "'");

		gLogger.info("Query to sevarth id in form s1:  " + sb.toString());
		Query sqlQuery1 = hibSession.createSQLQuery(sb.toString());
		finalCheckFlag = Long.parseLong(sqlQuery1.uniqueResult().toString());
		gLogger.info("finalCheckFlag : " + finalCheckFlag);
		return finalCheckFlag;
	}

	@Override
	public List getEmpDesigList(String strDDOCode) {
		Session hibSession = getSession();
		StringBuffer strQuery = new StringBuffer();
		strQuery.append(" select desig.DSGN_NAME,desig.DSGN_ID,mstddo.DDO_CODE ");
		strQuery.append(" from MST_DCPS_EMP empmst ");
		strQuery.append(" inner join org_emp_mst org on org.EMP_ID = empmst.ORG_EMP_MST_ID ");
		strQuery.append(" inner join ORG_USERPOST_RLT userpost on userpost.USER_ID=org.USER_ID ");
		strQuery.append(" inner join ORG_POST_MST post on post.POST_ID=userpost.POST_ID   ");
		strQuery.append(" inner join MST_DCPS_DDO_OFFICE mstddo on  empmst.CURR_OFF=mstddo.DCPS_DDO_OFFICE_MST_ID ");
		strQuery.append(" inner join ORG_DESIGNATION_MST desig on empmst.DESIGNATION=desig.DSGN_ID ");
		strQuery.append(" where mstddo.DDO_CODE='" + strDDOCode
				+ "' and  empmst.EMP_SERVEND_DT > sysdate ");
		strQuery.append(" and empmst.DCPS_OR_GPF='Y' and  empmst.AC_DCPS_MAINTAINED_BY in (700174,700240,700241,700242) ");
		strQuery.append(" and not exists (select null FROM FRM_FORM_S1_DTLS frms1 where empmst.SEVARTH_ID=frms1.sevarth_id ) and userpost.ACTIVATE_FLAG = 1 and post.ACTIVATE_FLAG = 1  ");
		strQuery.append(" group by desig.DSGN_NAME,desig.DSGN_ID,mstddo.DDO_CODE ");
		strQuery.append(" order by desig.DSGN_NAME ");

		logger.info("Query to get Emp desig List For Frm S1 Edit is "
				+ strQuery.toString());
		Query query = hibSession.createSQLQuery(strQuery.toString());
		return query.list();
	}

	public void insertDepFlagDdo(String strDDOCode, String strEmpSevarthId) {
		Session hibSession = getSession();
		StringBuffer strQuery = new StringBuffer();
		// String ddoc=strDDOCode.substring(0,2);
		gLogger.info(" strDDOCode " + strDDOCode);

		gLogger.info("strEmpSevarthId " + strEmpSevarthId);

		strQuery.append(" update FRM_FORM_S1_DTLS set dep_updated_ddo_cd='"
				+ strDDOCode + "' where sevarth_id='" + strEmpSevarthId + "' ");
		logger.info("Query to ######updateDepFlagDdo: " + strQuery.toString());
		Query query = hibSession.createSQLQuery(strQuery.toString());
		query.executeUpdate();
	}

	public String chkFrmUpdatedByLgnDdo(String sevarthId) {
		org.hibernate.Session hibSession = this.getSession();
		StringBuilder newQuery = new StringBuilder();
		String ddoCodee = "Z";
		List empLst = null;
		String[] empCountLst = null;
		newQuery.append(" SELECT nvl(DEP_UPDATED_DDO_CD,0) FROM frm_form_s1_dtls where sevarth_id='"
				+ sevarthId + "' ");
		SQLQuery query = hibSession.createSQLQuery(newQuery.toString());
		// gLogger.info("########Result is :"+query.uniqueResult().toString());

		empLst = query.list();
		// empLst.get(0);
		// empCountLst = new String[empLst.size()];
		// for(int i=0;i<empLst.size();i++)
		// empCountLst[i]=empLst.get(i).toString();

		if (empLst.size() > 0) {
			if (!empLst.get(0).equals("0")) {
				ddoCodee = query.uniqueResult().toString();
				gLogger.info("########Result is not 0 :"
						+ query.uniqueResult().toString());
			}
			if (empLst.get(0).equals("0")) {
				ddoCodee = "S";
				gLogger.info("########Result is 0 :"
						+ query.uniqueResult().toString());
			}
		}
		return ddoCodee;
	}

	public void updateOrphanDoc(Long attachment_Id_order, String strEmpSevarthId) {
		Session hibSession1 = getSession();
		StringBuffer strQuery1 = new StringBuffer();
		// String ddoc=strDDOCode.substring(0,2);
		gLogger.info(" attachment_Id_order-- " + attachment_Id_order);

		gLogger.info("strEmpSevarthId --" + strEmpSevarthId);

		strQuery1.append(" UPDATE FRM_FORM_S1_DTLS SET ORPHAN_ATTACHMENTID="
				+ attachment_Id_order + " where sevarth_id='" + strEmpSevarthId
				+ "' ");
		logger.info("Query to ######updateDepFlagDdo: " + strQuery1.toString());
		Query query = hibSession1.createSQLQuery(strQuery1.toString());
		query.executeUpdate();

		gLogger.info(" updated ---" + attachment_Id_order);

	}

}
