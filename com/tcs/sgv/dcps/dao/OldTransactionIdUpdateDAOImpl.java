package com.tcs.sgv.dcps.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.text.SimpleDateFormat;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.util.DBConnection;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class OldTransactionIdUpdateDAOImpl  extends GenericDaoHibernateImpl implements OldTransactionIdUpdateDAO {

	private Session ghibSession = null;
	private final Logger gLogger = Logger.getLogger(getClass());

	public OldTransactionIdUpdateDAOImpl(Class type, SessionFactory sessionFactory) {
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
			//lsb = new StringBuffer(
			//"select CM.loc_Id , CM.loc_Name from Cmn_Location_Mst CM where CM.department_Id in (100006,100003)  and CM.LANG_ID = 1 and CM.LOC_ID not in(9991,1111,1111111)  order by CM.loc_id ");
			
			lsb = new StringBuffer(
			"select CM.loc_Id , CM.loc_Name from Cmn_Location_Mst CM where CM.department_Id in (100006,100003)  and CM.LANG_ID = 1   order by CM.loc_id ");

			
			
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
			logger.info("Sql Exception:" + e, e);
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
				logger.info("Sql Exception:" + e, e);
			}
		}
		return arrTreasury;

	}
	
	

	public List getFinyears() {
		
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
//		Date date = new Date();
		Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        		
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

	
//	SELECT bh_date,FILE_NAME,BH_EMP_AMOUNT,BH_EMPLR_AMOUNT,TRANSACTION_ID FROM nsdl_bh_dtls
	//where substr(bh_date,3,8)=092015 and substr(file_name,1,4)=1111

	@Override
	public List getDetails( String string, String string2,String treasurynos) {
	
		List temp=null;
		String date=string2+string;
		StringBuilder  Strbld = new StringBuilder();
		//String treasury=treasurynos.substring(0,2);
		if(Integer.parseInt(string2) < 4){
		Strbld.append(" SELECT FILE_NAME,BH_EMP_AMOUNT,BH_EMPLR_AMOUNT,TRANSACTION_ID FROM nsdl_bh_dtls ");
		Strbld.append(" where year= "+string+"  and month="+string2+" and file_name like '"+treasurynos+"%' and file_status <> -1 and status <> -1 and transaction_id is not null" );
		}else{
			Strbld.append(" SELECT FILE_NAME,BH_EMP_AMOUNT,BH_EMPLR_AMOUNT,TRANSACTION_ID FROM nsdl_bh_dtls ");
			Strbld.append(" where year= "+string+" and month="+string2+" and file_name like '"+treasurynos+"%' and file_status <> -1 and status <> -1 and transaction_id is not null" );
		}
		
	

		
		
		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());

		temp=lQuery.list();
		
		return temp;
	}

	
	@Override
	public int updateDetails(String fid, String tid, String res, String rem) {
		int temp=0;
		StringBuilder  Strbld = new StringBuilder();
		String ress=null;
		if(Integer.parseInt(res)==7){
			ress="Wrong Transaction Id";
		}
		else if(Integer.parseInt(res)==8){
			ress="Lapsed Transaction Id";
		}
		
		Strbld.append(" update nsdl_bh_dtls set FILE_STATUS='"+res+"',REASON_FOR_TRAN_ID_UPDATE='"+ress+"',REMARK_FOR_TRAN_ID_UPDATE='"+rem+"' where FILE_NAME='"+fid+"' ");
		
	
		
		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());

		temp=lQuery.executeUpdate();
		
		return temp;
		
	}
	

	
}
