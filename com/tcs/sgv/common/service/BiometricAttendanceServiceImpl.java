/**
 * 
 */
package com.tcs.sgv.common.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;

/**
 * @author Meeta Thacker
 * 
 */
public class BiometricAttendanceServiceImpl extends ServiceImpl {

	private Session ghibSession = null;
	private final static Logger gLogger = Logger.getLogger(BiometricAttendanceServiceImpl.class);

	public ResultObject getEmpDataForBiometrics(Map inputMap) throws Exception {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator servLoc = (ServiceLocator) inputMap.get("serviceLocator");
		SessionFactory sessionFactory = servLoc.getSessionFactory();
		ghibSession = sessionFactory.getCurrentSession();
		String lStrSevaarthId = (String) inputMap.get("sevaarthId");
		List lLstEmpDetails = getDetailsForSevaarthID(lStrSevaarthId);
		if (lLstEmpDetails != null && lLstEmpDetails.size() > 0) {
			Object lObjEmpDtls = lLstEmpDetails.get(0);
			gLogger.info("finalObject is :: " + lObjEmpDtls);
			inputMap.put("Result", "Successful");
			inputMap.put("EmpDetails", lObjEmpDtls);
		} else {
			inputMap.put("Result", "Invalid SevaarthId");
		}
		// XStream xStream = new XStream(new DomDriver("UTF-8"));
		// xStream.alias("collection", java.util.Map.class);
		// xStream.registerConverter(new biometric.MapConverter());

		objRes.setResultValue(inputMap);
		return objRes;
	}

	public List getDetailsForSevaarthID(String lStrSevaarthId) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		List resultList = new ArrayList();
		try {
			lSBQuery
					.append("SELECT COALESCE(EM.EMP_NAME,'Not Available'),COALESCE(EM.FATHER_OR_HUSBAND,'Not Available'),COALESCE(CLM.LOOKUP_NAME,'Not Available') ,"
							+ " COALESCE(EM.GENDER,'Not Available'),COALESCE(SUBSTR(EM.DOB,1,10),'Not Available'),COALESCE(SUBSTR(EM.DOJ,1,10),'Not Available'),"
							+ " COALESCE(ODM.DSGN_NAME,'Not Available'),COALESCE(CLM1.LOOKUP_NAME,'Not Available'), COALESCE(EM.ADDRESS_BUILDING,'Not Available'),"
							+ " COALESCE(EM.ADDRESS_STREET,'Not Available'),COALESCE(EM.LANDMARK,'Not Available'),COALESCE(EM.LOCALITY,'Not Available'),"
							+ " COALESCE(EM.DISTRICT,'Not Available'),COALESCE(CSM.STATE_NAME,'Not Available'),COALESCE(CAST(EM.PINCODE AS VARCHAR),'Not Available'),"
							+ " COALESCE(CAST(EM.CELL_NO AS VARCHAR),'Not Available'),COALESCE(CAST(EM.CNTCT_NO AS VARCHAR),'Not Available'),"
							+ " COALESCE(EM.EMAIL_ID,'Not Available'),COALESCE(CLM2.LOC_NAME,'Not Available'),COALESCE(MDC.CADRE_NAME,'Not Available'),"
							+ " COALESCE(EM.EMP_GROUP,'Not Available'),COALESCE(EM.FIRST_DESIGNATION,'Not Available'),COALESCE(CAST(EM.APPOINTMENT_DATE AS VARCHAR),'Not Available'),"
							+ " COALESCE(DO.OFF_NAME,'Not Available'),COALESCE(EM.EID_NO,'Not Available'),COALESCE(EM.UID_NO,'Not Available'),COALESCE(EM.PAN_NO,'Not Available'),"
							+ " COALESCE(EM.DDO_CODE,'Not Available'),COALESCE(DDO.DDO_NAME,'Not Available'),COALESCE(MDB.DESCRIPTION,'Not Available')"
							+ " FROM MST_DCPS_EMP EM "
							+ " join CMN_LOOKUP_MST CLM on CLM.LOOKUP_ID=EM.SALUTATION"
							+ " join ORG_DESIGNATION_MST ODM on ODM.DSGN_ID=EM.DESIGNATION"
							+ " join CMN_LOOKUP_MST CLM1 on CLM1.LOOKUP_ID=EM.PAY_COMMISSION"
							+ " join CMN_STATE_MST CSM on CSM.STATE_ID=EM.STATE"
							+ " join CMN_LOCATION_MST CLM2 on CLM2.LOC_ID = EM.PARENT_DEPT"
							+ " left outer join MST_DCPS_CADRE MDC on MDC.CADRE_ID=EM.CADRE"
							+ " join MST_DCPS_DDO_OFFICE DO on DO.DCPS_DDO_OFFICE_MST_ID=EM.CURR_OFF"
							+ " join MST_DCPS_BILL_GROUP MDB on MDB.BILL_GROUP_ID=EM.BILLGROUP_ID"
							+ " join ORG_DDO_MST DDO on DDO.DDO_CODE=EM.DDO_CODE"
							+ " where EM.sevarth_id='"
							+ lStrSevaarthId + "'");

			Query lObjQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			resultList = lObjQuery.list();

		} catch (Exception e) {
			gLogger.error("Error in getDetailsForSevaarthID is :" + e, e);
			throw e;
		}
		return resultList;
	}
}
