package com.tcs.sgv.dcps._TDSWebService;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class TDSWSDAOImpl extends GenericDaoHibernateImpl
{

	public TDSWSDAOImpl(Class type, SessionFactory sessionFactory) 
	{
		super(type);
		setSessionFactory(sessionFactory);
	}

	public List getCountOfValidatedUser(String userName, String md5Pwd, String userType) 
	{
		logger.info("inside getBillPortalDataLpcWS");
		List list =new ArrayList();
		Session hibSession = getSession();
		StringBuffer strBfr = new StringBuffer();
		if(userType.equalsIgnoreCase("E"))
		{
			strBfr.append(" SELECT user.USER_NAME,user.PASSWORD,mstemp.CELL_NO,mstemp.EMAIL_ID,mstemp.EMP_NAME FROM org_user_mst user ");
			strBfr.append(" inner join org_emp_mst emp on user.USER_ID=emp.USER_ID ");
			strBfr.append(" inner join MST_DCPS_EMP mstemp on emp.EMP_ID = mstemp.ORG_EMP_MST_ID ");
			strBfr.append(" where user.USER_NAME = '"+userName.trim()+"' and user.PASSWORD='"+md5Pwd.trim()+"' ");

			logger.info("Query to validate userName and pwd and get mobile no and email of employee ... "+strBfr.toString());
		}
		else if(userType.equalsIgnoreCase("D"))
		{

			strBfr.append(" SELECT user.USER_NAME,user.PASSWORD, ddooffice.TEL_NO2, ddooffice.EMAIL,ddo.DDO_PERSONAL_NAME FROM ORG_USER_MST user ");
			strBfr.append(" INNER JOIN ORG_USERPOST_RLT userpost on user.USER_ID = userpost.USER_ID and userpost.ACTIVATE_FLAG=1 ");
			strBfr.append(" INNER JOIN ORG_POST_DETAILS_RLT rltpost on rltpost.post_id = userpost.POST_ID ");
			strBfr.append(" INNER JOIN MST_DCPS_DDO_OFFICE ddooffice on rltpost.LOC_ID= ddooffice.LOC_ID ");
			strBfr.append(" INNER JOIN ORG_DDO_MST ddo on ddo.DDO_CODE = ddooffice.DDO_CODE and ddo.ACTIVATE_FLAG = 1 ");
			strBfr.append(" WHERE lower(ddooffice.DDO_OFFICE) = 'yes' and user.ACTIVATE_FLAG = 1 ");
			strBfr.append(" and user.USER_NAME = '"+userName.trim()+"' and user.PASSWORD='"+md5Pwd.trim()+"' ");

			logger.info("Query to validate userName and pwd and get mobile no and email of DDO ... "+strBfr.toString());

		}
		else if(userType.equalsIgnoreCase("T"))
		{

			strBfr.append("  ");
			strBfr.append("  ");
			strBfr.append("  ");
			strBfr.append("  ");
			strBfr.append("  ");
			strBfr.append(" where user.USER_NAME = '"+userName.trim()+"' and user.PASSWORD='"+md5Pwd.trim()+"' ");

			logger.info("Query to validate userName and pwd and get mobile no and email of TO ... "+strBfr.toString());

		}
		else
		{

		}

		Query lQuery = hibSession.createSQLQuery(strBfr.toString());
		list =lQuery.list();

		return (list);
	}

	public List getuserInfoForTCS(String sevarthId) 
	{
		logger.info("inside getuserInfoForTCS");
		List list =new ArrayList();
		Session hibSession = getSession();
		StringBuffer strBfr = new StringBuffer();

		strBfr.append(" select empmst.SEVARTH_ID,empmst.GENDER, VARCHAR_FORMAT(org.EMP_DOB , 'dd/MM/yyyy') as DOB, mstddo.EMAIL as office_emailID, cadre.cadre_name, ");
		strBfr.append(" look.LOOKUP_DESC as group, VARCHAR_FORMAT(org.EMP_DOJ, 'dd/MM/yyyy') as DOJ, ");
		strBfr.append(" VARCHAR_FORMAT(rltdcps.CURR_CADRE_JOINING_DATE, 'dd/MM/yyyy') as cur_cadre_joining, postrlt.post_name, ");
		strBfr.append(" mstddo.OFF_NAME as current_office, empmst.FIRST_DESIGNATION, locmst.LOC_NAME as field_department from MST_DCPS_EMP empmst ");
		strBfr.append(" inner join org_emp_mst org on org.EMP_ID = empmst.ORG_EMP_MST_ID ");
		strBfr.append(" inner join ORG_USERPOST_RLT up on up.USER_ID = org.USER_ID and up.ACTIVATE_FLAG = 1 ");
		strBfr.append(" inner join ORG_POST_DETAILS_RLT postrlt on postrlt.post_id=up.POST_ID ");
		strBfr.append(" inner join ORG_POST_MST post on post.POST_ID = up.POST_ID and post.ACTIVATE_FLAG = 1 ");
		strBfr.append(" inner join RLT_DCPS_PAYROLL_EMP rltdcps on rltdcps.DCPS_EMP_ID = empmst.DCPS_EMP_ID ");
		strBfr.append(" inner join MST_DCPS_DDO_OFFICE mstddo on  empmst.CURR_OFF=mstddo.DCPS_DDO_OFFICE_MST_ID ");
		strBfr.append(" inner join MST_DCPS_CADRE cadre on cadre.CADRE_ID = empmst.CADRE  ");
		strBfr.append(" inner join CMN_LOCATION_MST locmst on locmst.LOC_ID = empmst.PARENT_DEPT ");
		strBfr.append(" inner join CMN_LOOKUP_MST look on look.LOOKUP_ID=org.GRADE_ID ");
		strBfr.append(" LEFT OUTER join MST_PAYROLL_DESIGNATION mstpay on empmst.DESIGNATION=mstpay.ORG_DESIGNATION_ID and mstpay.FIELD_DEPT_ID=empmst.PARENT_DEPT ");
		strBfr.append(" where empmst.SEVARTH_ID = '"+sevarthId+"'  ");
		strBfr.append(" group by empmst.SEVARTH_ID,empmst.GENDER, emp_DOB,mstddo.EMAIL, cadre.cadre_name, look.LOOKUP_DESC,org.EMP_DOJ, postrlt.post_name, ");
		strBfr.append(" mstddo.OFF_NAME, rltdcps.CURR_CADRE_JOINING_DATE , ");
		strBfr.append(" empmst.FIRST_DESIGNATION, locmst.LOC_NAME ");

		logger.info("Query to get user info ... "+strBfr.toString());
		Query lQuery = hibSession.createSQLQuery(strBfr.toString());
		list =lQuery.list();

		return (list);
	}

	public int[] updateUserInfoForTDS(String userName, String userType,String mobileNo, String emailId) 
	{
		int arrRetrnFlg [] = new int [2];

		///////////////////////////////

		logger.info("inside updateUserInfoForTDS");
		//List list =new ArrayList();
		int rowUpdatedMobileNo = 0;
		int rowUpdatedEmailId = 0;
		Session hibSession = getSession();
		

		if((userType != null) && !(userType.equals("")) && (userName != null) && !(userName.equals("")))
		{
			
			// employee info update
			if(userType.equalsIgnoreCase("E"))
			{
				if((mobileNo != null) && !(mobileNo.equals("")))
				{
					StringBuffer strBfr = new StringBuffer();
					strBfr.append(" update MST_DCPS_EMP set CELL_NO ='"+mobileNo+"' ");
					strBfr.append(" where org_emp_mst_id = (select emp_id from ORG_EMP_MST where USER_ID = (SELECT USER_ID from ORG_USER_MST where USER_NAME = '"+userName+"')) ");
					logger.info("Query to update employee user info mobile no ... "+strBfr.toString());
					Query lQuery = hibSession.createSQLQuery(strBfr.toString());
					rowUpdatedMobileNo = lQuery.executeUpdate();
				}
				if((emailId != null) && !(emailId.equals("")))
				{
					StringBuffer strBfr = new StringBuffer();
					strBfr.append(" update MST_DCPS_EMP set email_id ='"+emailId+"' ");
					strBfr.append(" where org_emp_mst_id = (select emp_id from ORG_EMP_MST where USER_ID = (SELECT USER_ID from ORG_USER_MST where USER_NAME = '"+userName+"')) ");
					logger.info("Query to update employee user info email ... "+strBfr.toString());
					Query lQuery = hibSession.createSQLQuery(strBfr.toString());
					rowUpdatedEmailId = lQuery.executeUpdate();
				}
			}
			
			// DDO info update
			if(userType.equalsIgnoreCase("D"))
			{
				if((mobileNo != null) && !(mobileNo.equals("")))
				{
					StringBuffer strBfr = new StringBuffer();
					strBfr.append(" update MST_DCPS_DDO_OFFICE  set TEL_NO2 = '"+mobileNo+"'  ");
					strBfr.append(" where LOC_ID = (SELECT LOC_ID FROM ORG_POST_DETAILS_RLT where POST_ID = (SELECT POST_ID FROM ORG_USERPOST_RLT where USER_ID = (select USER_ID from ORG_USER_MST where USER_NAME = 'DEMO_DDO' and ACTIVATE_FLAG = '1') and ACTIVATE_FLAG = 1)) ");
					strBfr.append(" AND upper(ddo_office) = 'YES' ");
					strBfr.append(" and DDO_CODE in (select DDO_CODE from ORG_DDO_MST where ACTIVATE_FLAG = '1') ");
					logger.info("Query to update DDO user info mobile no ... "+strBfr.toString());
					Query lQuery = hibSession.createSQLQuery(strBfr.toString());
					rowUpdatedMobileNo = lQuery.executeUpdate();
				}
				if((emailId != null) && !(emailId.equals("")))
				{
					StringBuffer strBfr = new StringBuffer();
					strBfr.append(" update MST_DCPS_DDO_OFFICE  set EMAIL  = '"+emailId+"'  ");
					strBfr.append(" where LOC_ID = (SELECT LOC_ID FROM ORG_POST_DETAILS_RLT where POST_ID = (SELECT POST_ID FROM ORG_USERPOST_RLT where USER_ID = (select USER_ID from ORG_USER_MST where USER_NAME = 'DEMO_DDO' and ACTIVATE_FLAG = '1') and ACTIVATE_FLAG = 1)) ");
					strBfr.append(" AND upper(ddo_office) = 'YES' ");
					strBfr.append(" and DDO_CODE in (select DDO_CODE from ORG_DDO_MST where ACTIVATE_FLAG = '1') ");
					logger.info("Query to update DDO user info email ... "+strBfr.toString());
					Query lQuery = hibSession.createSQLQuery(strBfr.toString());
					rowUpdatedEmailId = lQuery.executeUpdate();
				}
			}

			// TO info update
			if(userType.equalsIgnoreCase("T"))
			{
				StringBuffer strBfr = new StringBuffer();
				strBfr.append("  ");
				logger.info("Query to update TO user info ... "+strBfr.toString());
				Query lQuery = hibSession.createSQLQuery(strBfr.toString());
				//list =lQuery.list();
			}
		}
		arrRetrnFlg[0] = rowUpdatedMobileNo;
		arrRetrnFlg[1] = rowUpdatedEmailId;
		
///////////////////////////////

		return arrRetrnFlg;
	}

}
