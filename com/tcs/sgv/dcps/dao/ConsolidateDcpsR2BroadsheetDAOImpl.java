/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jun 9, 2011		Vihan Khatri								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;


public class ConsolidateDcpsR2BroadsheetDAOImpl extends GenericDaoHibernateImpl implements
ConsolidateDcpsR2BroadSheetDAO {

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	private final ResourceBundle gObjRsrcBndle = ResourceBundle
	.getBundle("resources/pensionproc/PensionCaseConstants");

	public ConsolidateDcpsR2BroadsheetDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);

	}


	public List getAcMainDetails() throws Exception{

		String query = " SELECT distinct emp.AC_DCPS_MAINTAINED_BY,look.LOOKUP_NAME FROM mst_dcps_emp  emp inner join CMN_LOOKUP_MST look   on look.LOOKUP_ID=emp.AC_DCPS_MAINTAINED_BY   order by look.LOOKUP_NAME ";
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;

		if (lLstResult != null && lLstResult.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc(obj[1].toString());
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}
		return lLstReturnList;
	}
	

	
	public List getAllTreasuriesForConsolidateEnteries(Long finYear,Long acMain) {

		List lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		sb.append("	select cmnloc.loc_id,cmnloc.loc_name,temp2.ssss,temp2.ssss from (select temp.loc_district_id as district,  cast(sum(temp.sss) as bigint) as ssss from (select cmn.LOC_ID,cmn.parent_loc_id,cmn.loc_district_id,cast(SUM(trn.CONTRIBUTION) as DECIMAL(20,0)) as sss,cast(SUM(trn.CONTRIBUTION) as DECIMAL(20,0)) from mst_dcps_emp emp "); 
		sb.append("	inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=emp.DCPS_EMP_ID ");
		sb.append("	inner join CMN_LOCATION_MST cmn on trn.TREASURY_CODE=cmn.LOC_ID  ");
		sb.append("	where cmn.DEPARTMENT_ID in (100003,100006) and ");
		sb.append("	trn.REG_STATUS=1 and emp.REG_STATUS=1 and emp.DCPS_ID is not null  ");
		sb.append("	and emp.AC_DCPS_MAINTAINED_BY=:acMain and trn.FIN_YEAR_ID= :finYear group by cmn.LOC_ID,cmn.parent_loc_id,cmn.loc_district_id) temp ");
		sb.append("	group by temp.loc_district_id) temp2 ");
		sb.append("	inner join CMN_LOCATION_MST cmnloc on cmnloc.loc_district_id=temp2.district and cmnloc.DEPARTMENT_ID=100003  order by cmnloc.loc_id");
		
		
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		selectQuery.setParameter("acMain", acMain);
		selectQuery.setParameter("finYear", finYear);
		lLstReturnList = selectQuery.list();
		return lLstReturnList;
}
	
	
	
}