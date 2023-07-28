
package com.tcs.sgv.dcps.dao;

import java.util.ArrayList;
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


public class NpsFileStatusChangeDAOImpl extends GenericDaoHibernateImpl implements NpsFileStatusChangeDAO
{

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	private final ResourceBundle gObjRsrcBndle = ResourceBundle
	.getBundle("resources/pensionproc/PensionCaseConstants");

	public NpsFileStatusChangeDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);

	}

	public Long getFinYearId(String finYearCode){

		List sev = null;
		Long FinYearId =0l;

		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append( " SELECT SFYM.FIN_YEAR_ID FROM SGVC_FIN_YEAR_MST SFYM " ); 
			lSBQuery.append( " WHERE SFYM.FIN_YEAR_CODE =:finYearCode " );

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("finYearCode",finYearCode);		


			sev = lQuery.list();

			if(!sev.isEmpty() && sev.size()>0 && sev.get(0)!=null)
				FinYearId = Long.parseLong(sev.get(0).toString());
		}
		catch(Exception e){
			logger.error("Exception in getFinYearId of LNALedgerQueryDAOImpl: " , e);
		}
		return FinYearId;

	}
	public List getFinyear() {

		String query = "select finYearCode,finYearCode from SgvcFinYearMst where finYearCode between '2015' and '2023' order by finYearCode ASC";
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createQuery(sb.toString());
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


	public List getAllData(String yrCode, String month,
			String strLocationCode) throws Exception {

		List contrList = null;
		Query lQuery = null;
		String trCode = null;
		trCode = strLocationCode.substring(0,2);
		StringBuilder lSBQuery = null;
		try{
			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT distinct bh.FILE_NAME,bh.BH_EMP_AMOUNT,bh.BH_EMPLR_AMOUNT, decode(bh.file_status,1,'File is validated'),bh.MONTH, bh.YEAR ,BH_TOTAL_AMT  FROM NSDL_BH_DTLS bh   ");
			lSBQuery.append(" where bh.MONTH='" + 
					month + "' and bh.YEAR= '" + yrCode + "' " + 
					"and bh.file_name like '" + trCode + "%'  and bh.file_name not like '%D' and bh.STATUS <> -1 and bh.file_status = '1' ");

			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			logger.info("lQuery*******is to get the list"+lQuery);
			contrList = lQuery.list();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
			throw(e);
		}
		return contrList;
	}

	public List getAllDataDeputation(String yrCode, String month,
			String strLocationCode) throws Exception {


		List contrList = null;
		Query lQuery = null;
		String trCode = null;
		trCode = strLocationCode.substring(0,2);
		StringBuilder lSBQuery = null;
		try{
			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT distinct bh.FILE_NAME,bh.BH_EMP_AMOUNT,bh.BH_EMPLR_AMOUNT, decode(bh.file_status,1,'File is validated'),bh.MONTH, bh.YEAR,BH_TOTAL_AMT FROM NSDL_BH_DTLS bh   ");
			lSBQuery.append(" where bh.MONTH='" + 
					month + "' and bh.YEAR= '" + yrCode + "' " + 
					"and bh.file_name like '" + trCode + "%D' and bh.STATUS <> -1  and bh.file_status = '1' ");

			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			logger.info("lQuery*******is to get the list"+lQuery);
			contrList = lQuery.list();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
			throw(e);
		}
		return contrList;
	}

//
//	public void updateFileStatus(int fileStatus, String fileno, String errorData) {
//		StringBuilder sb = new StringBuilder();
//		sb.append(" update NSDL_BH_DTLS set STATUS = 0 , FILE_STATUS = 0 where FILE_NAME in ("+fileno+") ");
//
//		final Query updateQuery = ghibSession.createSQLQuery(sb.toString());
//
//		logger.info("Query to delete in deleteNsdlFile heaqder**********"+sb.toString());
//
//		updateQuery.executeUpdate();
//
//	}
//


	public String updateFileStatus(String fileName)
	{
		logger.info("Inside updateFileStatus Query************");
		StringBuilder strBuld = new StringBuilder();  
		Query updateQuery = null;
		String flag = "NA";
		try
		{
			strBuld.append(" update NSDL_BH_DTLS set status = 0 , FILE_STATUS = 0 where FILE_NAME in ("+fileName+") " );

			logger.info("Inside updateFileStatus Query************" +strBuld.toString());
			updateQuery = ghibSession.createSQLQuery(strBuld.toString());

			int result=updateQuery.executeUpdate();
			logger.info("result is of updateFileStatus query "+result);

			if (result!= 0 && result > 0) 
			{
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

	public List getAllSubTreasury(String treasuryId) {
		List<ComboValuesVO> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		sb.append("SELECT loc_id, loc_name FROM CMN_LOCATION_MST where (department_id=100006 and PARENT_LOC_ID=:loc_id ) or LOC_ID= :loc_id order by loc_name ");
		gLogger.info("query to select sub treasury from treasury code:::" + sb);
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		gLogger.info("sql query created");
		selectQuery.setParameter("loc_id", treasuryId);	


		lLstReturnList = new ArrayList<ComboValuesVO>();

		List lLstResult = selectQuery.list();
		gLogger.info("list size:" +lLstResult.size());

		ComboValuesVO lObjComboValuesVO = new ComboValuesVO();
		lObjComboValuesVO.setId("-1");
		lObjComboValuesVO.setDesc("--Select--");
		lLstReturnList.add(lObjComboValuesVO);
		if (lLstResult != null && lLstResult.size() != 0) {
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				String desc=obj[1].toString();
				lObjComboValuesVO.setDesc(desc);
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList<ComboValuesVO>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}

		return lLstReturnList;

	}

	public void insertFileDetails(Long lLngPkIdForFile,String fileArray,String reasonArray,Long gLngPostId,String remarksArray,String empContributionArray,String emplyrContributionArray,String totalFinalAmountArray,String txtMonth,String txtYear) {
		String reason = null;
		String trCode = null;
		if(reasonArray.equalsIgnoreCase("1")){
			reason = "Regeneration of FVU";
		}else{
			reason = "Deletion of file due to issue";
		}
		trCode = fileArray.substring(0,4);
		final Session session = this.getSession();
		final StringBuffer str = new StringBuffer();
		str.append(" insert into nsdl_nps_file values ("+lLngPkIdForFile+",'"+fileArray+"','"+trCode+"','"+empContributionArray+"','"+emplyrContributionArray+"','"+totalFinalAmountArray+"',");
		str.append("'"+reason+"','"+txtYear+"','"+txtMonth+"','"+gLngPostId+"',sysdate,null,null,'"+remarksArray+"')");
		//		str.append(" insert into nsdl_nps_file values ("+lLngPkIdForFile+",'"+fileArray+"','"+null+"','"+EMP_AMOUNT+"','"+EMPLR_AMOUNT+"','"+TOTAL_AMOUNT+"',");
		//		str.append("'"+REASON+"','"+YEAR+"','"+MONTH+"','"+CREATED_POST_ID+"',sysdate,null,null,'"+REMARKS+"')");

		final Query updateQuery = session.createSQLQuery(str.toString());
		logger.info("Query to insert in insertFileDetails**********"+str.toString());

		int update = updateQuery.executeUpdate();
		logger.info("value of update**********"+update);
	}
	
////$t 28-7-2021
	/*public List getAllTreasuries(String locId) throws Exception {
		
		String query;
        if(locId.length()<5)
		query = "select CM.locId , CM.locName from CmnLocationMst CM where departmentId = 100003 and CM.locId='"+locId+"' and CM.locId not in (9991,2028914,2028915)  order by CM.locId";
        else
		query = "select CM.locId , CM.locName from CmnLocationMst CM where departmentId = 100003 and CM.locId not in (9991,2028914,2028915)  order by CM.locId";
        
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createQuery(sb.toString());

		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;
		if (lLstResult != null && lLstResult.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc(obj[0].toString() +"-"+ obj[1].toString());
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
*/

}
