package com.tcs.sgv.dcps.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.core.service.ServiceLocator;


public class UpdateFrnNoDAOImpl extends GenericDaoHibernateImpl {
	private Session ghibSession = null;
	private final Logger gLogger = Logger.getLogger(getClass());

	private ServiceLocator serv = null ;
	
	
	public UpdateFrnNoDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = sessionFactory.getCurrentSession();
		// TODO Auto-generated constructor stub
	}

	
	public List getFileDetails(String lStrFileId) {
		List lLstEmpDeselect = null;
		StringBuilder Strbld = new StringBuilder();
		try {// DESIG_DESC
			Strbld.append(" SELECT nbd.FILE_NAME,nvl(nbd.TRANSACTION_ID,''),decode(nbd.file_status,0,'File not Validated',1,'File is validated',2,'File is rejected',3,'File has been modified by User',5,'File has been sent to NSDL',11,'Transaction Id Updated',8,'Transaction Id Lapsed'),nbd.YEAR ,nbd.BH_EMP_AMOUNT,nbd.BH_EMPLR_AMOUNT,smt.MONTH_NAME,nvl(nbd.FRN_NO,'0') ");
			Strbld.append(" FROM NSDL_BH_DTLS nbd ");
			Strbld.append(" INNER JOIN SGVA_MONTH_MST smt on smt.MONTH_ID = nbd.MONTH ");
			Strbld.append(" WHERE nbd.FILE_NAME = '" + lStrFileId
					+ "'  and nbd.STATUS != '-1' ");
			Strbld.append(" and ((nbd.TRANSACTION_ID is not null and nbd.FILE_STATUS in(7,8)) or (nbd.TRANSACTION_ID is null and nbd.FILE_STATUS in (1,5))) ");

			SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());

			logger.info("query getFileDetails ---------" + Strbld.toString());
			lLstEmpDeselect = lQuery.list();
		} catch (Exception e) {
			logger.info("Error occured in getFileDetails ---------" + e);
		}
		return lLstEmpDeselect;
	}

/*	public List checkfileIdExist(String lStrFileId) {

		StringBuilder sb = new StringBuilder();
		Query selectQuery = null;
		Date lDtCurrDate = SessionHelper.getCurDate();

		sb.append(" SELECT * FROM NSDL_BH_DTLS WHERE FILE_NAME = '"
				+ lStrFileId + "' and STATUS != '-1' ");

		logger.info("Inside checkfileIdExist query is *********** "
				+ sb.toString());

		selectQuery = ghibSession.createSQLQuery(sb.toString());

		List exist = selectQuery.list();
		System.out.println("resultList" + exist.size());

		return exist;

	}
*/
	public String updateFrnNo(String fileId, String frnNo) {
		StringBuilder strBuld = new StringBuilder();
		Query updateQuery = null;	
		
		String flag = "NA";
		try {
			
			strBuld.append("UPDATE NSDL_BH_DTLS SET FRN_NO='" + frnNo+"', FILE_STATUS = '5' WHERE FILE_NAME='" + fileId + "' ");
			
			logger.info("Inside updateFrnNo Query************"+ strBuld.toString());
			SQLQuery lQuery = ghibSession.createSQLQuery(strBuld.toString());

			/*
			 * lQuery.setParameter("fileId", fileId);
			 * System.out.println("fileId"+fileId);
			 * System.out.println("frnNo"+frnNo); lQuery.setParameter("frnNo",
			 * frnNo);
			 */

			int result = lQuery.executeUpdate();
			System.out.println("result:" + result);
			if (result != 0 && result > 0) {
				flag = "Updated";

			} else {

				flag = "NotUpdated";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			// e.printStackTrace();
			// return objRes;
			// TODO: handle exception
		}
		return flag;
	}

/*	public List checkStatus(String lStrFileId) {

		StringBuilder sb = new StringBuilder();
		Query selectQuery = null;

		sb.append(" SELECT ACTIVE_FLAG FROM MST_DIGI_SIGN_ACTIVATION_DTLS where substr(LOC_ID,1,2)=substr('"
				+ lStrFileId + "',1,2) ");

		logger.info("Inside checkStatus query is *********** " + sb.toString());

		selectQuery = ghibSession.createSQLQuery(sb.toString());

		List exist = selectQuery.list();
		System.out.println("resultList" + exist.size());

		return exist;
	}
*/	
	public List getFinyear() {
		
		Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        if(4>month+1){
            year = year-1;
        }

		String query = "select finYearCode,finYearCode from SgvcFinYearMst where finYearCode between '2015' and '"+year+"' order by finYearCode ASC";
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
	public List getAllSubTreasury() {
		List<ComboValuesVO> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		sb.append(" SELECT c.loc_id, c.loc_name FROM CMN_LOCATION_MST c inner join MST_DIGI_SIGN_ACTIVATION_DTLS m on c.LOC_ID = m.LOC_ID where c.department_id=100003 and m.ACTIVE_FLAG = 1 ");
		gLogger.info("query to select sub treasury from treasury code:::" + sb);
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		gLogger.info("sql query created");
		//selectQuery.setParameter("loc_id", treasuryId);	
		

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
	
/*	public int getDigiActivationDtls(String trCode) {
		List temp = null;
		int activeFlag = 0;

		StringBuilder Strbld = new StringBuilder();
		try {
			Strbld.append(" select ACTIVE_FLAG from MST_DIGI_SIGN_ACTIVATION_DTLS where LOC_ID ="
					+ trCode + " ");

			SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());

			temp = lQuery.list();
			if (temp != null && temp.size() > 0 && temp.get(0) != null) {
				activeFlag = Integer.parseInt(temp.get(0).toString());
			}

		} catch (Exception e) {
			logger.error("Exception in getDigiActivationDtls: ", e);
			e.printStackTrace();
		}

		return activeFlag;
	}
*/
	public List getAllData(String yrCode, String month, String strLocationCode)
		    throws Exception
		  {
		    List contrList = null;
		    Query lQuery = null;
		    StringBuilder lSBQuery = null;
		    try {
		      lSBQuery = new StringBuilder();
		      
		      
		      /*lSBQuery.append("SELECT distinct bh.FILE_NAME,bh.BH_EMP_AMOUNT,bh.BH_EMPLR_AMOUNT,bh.TRANSACTION_ID,bh.file_status  FROM NSDL_BH_DTLS bh where bh.YEAR='" + yrCode + "' and bh.MONTH='" + month + "' and (( bh.FILE_STATUS=1 and bh.TRANSACTION_ID is null) or ( bh.FILE_STATUS in (7,8) and bh.TRANSACTION_ID is not null) ) and bh.STATUS<>-1 and bh.file_name like '" + strLocationCode + "%'   ");

		      lQuery = this.ghibSession.createSQLQuery(lSBQuery.toString());*/
		      
		      lSBQuery.append(" SELECT nbd.FILE_NAME,nvl(nbd.TRANSACTION_ID,''),decode(nbd.file_status,0,'File not Validated',1,'File is validated',2,'File is rejected',3,'File has been modified by User',5,'File has been sent to NSDL',11,'Transaction Id Updated',8,'Transaction Id Lapsed'),nbd.YEAR ,nbd.BH_EMP_AMOUNT,nbd.BH_EMPLR_AMOUNT,smt.MONTH_NAME,nvl(nbd.FRN_NO,'0') ");
		      lSBQuery.append(" FROM NSDL_BH_DTLS nbd ");
		      lSBQuery.append(" INNER JOIN SGVA_MONTH_MST smt on smt.MONTH_ID = nbd.MONTH ");
		      lSBQuery.append(" WHERE nbd.YEAR='" + yrCode + "' and nbd.MONTH='" + month + "' and nbd.STATUS != '-1' ");
		      lSBQuery.append(" and ((nbd.TRANSACTION_ID is not null and nbd.FILE_STATUS in(7,8)) or (nbd.TRANSACTION_ID is null and nbd.FILE_STATUS in (1,5))) and substr(nbd.file_name,1,2)=substr('" + strLocationCode + "',1,2)");

				 lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		      this.logger.info("lQuery*******is to get the list" + lQuery);
		      contrList = lQuery.list();
		    }
		    catch (Exception e)
		    {
		      e.printStackTrace();
		      this.gLogger.error("Error is :" + e, e);
		      throw e;
		    }
		    return contrList;
		  }

	public List testFrnNO(String frn_No) {
		// TODO Auto-generated method stub
		StringBuffer stbuff = new StringBuffer();
		List<Long>  lstpn = new ArrayList();
		//Boolean flag  = false;
		//stbuff.append("select emp_name,dcps_id,dcps_Emp_Id from Mst_DCPS_EMP where PRAN_NO =:pranNO");
		stbuff.append("SELECT FILE_NAME,FRN_NO FROM NSDL_BH_DTLS WHERE FRN_NO ='"+frn_No+"'");
		logger.info("Inside testfrn"+stbuff.toString());
	
		Query lstQuery = ghibSession.createSQLQuery(stbuff.toString());
		//lstQuery.setString("frn_NO",frn_No);
		
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

	public String oldFrn(String fileId) {
		StringBuffer stbuff = new StringBuffer();
		List<String>  lstpn = new ArrayList();
		stbuff.append("SELECT nvl(FRN_NO,'0') FROM NSDL_BH_DTLS WHERE FILE_NAME ='"+fileId+"'");
		logger.info("Inside testfrn"+stbuff.toString());
	
		Query lstQuery = ghibSession.createSQLQuery(stbuff.toString());
		
		lstpn = lstQuery.list();
		String dtls=null;
		dtls=lstpn.get(0).toString();
		if(lstpn != null)
		{
			if(lstpn.size()!=0)
			{
				dtls=lstpn.get(0).toString();
			}
		}
		return dtls;
	}

	





	
}
