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

public class UpdateNSDLTranDetailsDAOImpl extends GenericDaoHibernateImpl implements UpdateNSDLTranDetailsDAO {
	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	private final ResourceBundle gObjRsrcBndle = ResourceBundle
	.getBundle("resources/pensionproc/PensionCaseConstants");

	public UpdateNSDLTranDetailsDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);

	}


/*
	public List getDdoWiseTotalAmt(Long yearId,
			Long monthId, String strLocationCode) {

		List lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		sb.append(" SELECT ddo.DDO_CODE,sum(paybill.GROSS_AMT),sum(paybill.NET_TOTAL),sum(paybill.DCPS)+sum(paybill.DCPS_PAY)+sum(paybill.DCPS_DELAY)+sum(paybill.DCPS_DA) as employye,   ");
		sb.append(" sum(paybill.DED_ADJUST),reg.ddo_reg_no FROM PAYBILL_HEAD_MPG head   inner join NSDL_PAYBILL_DATA paybill on paybill.PAYBILL_GRP_ID =head.PAYBILL_ID  and paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA =paybill.DED_ADJUST ");
		sb.append(" inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID inner join HR_EIS_EMP_MST hreis on hreis.EMP_ID=paybill.EMP_ID  ");
		sb.append(" inner join MST_DCPS_EMP mstemp on mstemp.ORG_EMP_MST_ID=hreis.EMP_MPG_ID ");
		sb.append(" inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
		sb.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2) ");
		sb.append(" inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  ");
		sb.append(" where head.PAYBILL_YEAR="+yearId+" and head.PAYBILL_MONTH="+monthId+"  and substr(ddo.ddo_code,1,4)='"+strLocationCode+"' and head.APPROVE_FLAG=1 ");
		sb.append(" and mstemp.PRAN_NO is not null and mstemp.DCPS_OR_GPF='Y'  and mstemp.AC_DCPS_MAINTAINED_BY=700174 ");
		sb.append(" and mstemp.PRAN_NO not in (SELECT sd.SD_PRAN_NO FROM NSDL_SD_DTLS sd inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.STATUS <>-1 and bh.YEAR="+yearId+" and bh.MONTH="+monthId+" and bh.file_name like '"+strLocationCode+"%')");
		sb.append("  group by ddo.DDO_CODE,reg.ddo_reg_no having sum(paybill.DCPS)+sum(paybill.DCPS_PAY)+sum(paybill.DCPS_DELAY)+sum(paybill.DCPS_DA) >0 ");
		sb.append(" order by reg.ddo_reg_no  ");
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		logger.info(" selectQuery  selectQuery  selectQuery  ---------"+selectQuery.toString() );
		lLstReturnList= selectQuery.list();
		
		StringBuilder sb1 = new StringBuilder();
		sb1.append(" SELECT 'A',sum(amt),sum(net),sum(EMPLOYYE),sum(emlr) FROM ( ");
		sb1.append(" SELECT 'A',sum(paybill.GROSS_AMT) as amt,sum(paybill.NET_TOTAL) as net,sum(paybill.DCPS)+sum(paybill.DCPS_PAY)+sum(paybill.DCPS_DELAY)+sum(paybill.DCPS_DA) as employye,");   
		sb1.append(" sum(paybill.DED_ADJUST) as emlr,'' FROM PAYBILL_HEAD_MPG head   inner join NSDL_PAYBILL_DATA paybill on paybill.PAYBILL_GRP_ID =head.PAYBILL_ID  ");
	    sb1.append(" and paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA =paybill.DED_ADJUST  ");
		sb1.append(" inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID inner join HR_EIS_EMP_MST hreis on hreis.EMP_ID=paybill.EMP_ID  ");
		sb1.append(" inner join MST_DCPS_EMP mstemp on mstemp.ORG_EMP_MST_ID=hreis.EMP_MPG_ID ");
		sb1.append(" inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
		sb1.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2) ");
	    sb1.append("inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4) "); 
	    sb1.append(" where head.PAYBILL_YEAR="+yearId+" and head.PAYBILL_MONTH="+monthId+"  and substr(ddo.ddo_code,1,4)='"+strLocationCode+"' and head.APPROVE_FLAG=1 "); 
	    sb1.append("and mstemp.PRAN_NO is not null and mstemp.DCPS_OR_GPF='Y'  and mstemp.AC_DCPS_MAINTAINED_BY=700174 ");
	    sb1.append(" and mstemp.PRAN_NO not in (SELECT sd.SD_PRAN_NO FROM NSDL_SD_DTLS sd inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.STATUS <>-1 and bh.YEAR="+yearId+" and bh.MONTH="+monthId+" and bh.file_name like '"+strLocationCode+"%') ");
	    sb1.append(" group by ddo.DDO_CODE,reg.ddo_reg_no  having sum(paybill.DCPS)+sum(paybill.DCPS_PAY)+sum(paybill.DCPS_DELAY)+sum(paybill.DCPS_DA) >0)  ");

		  
	    Query selectQuery1 = ghibSession.createSQLQuery(sb1.toString());
		List lLstLastRow= selectQuery1.list();


		if(lLstReturnList!=null && lLstReturnList.size()>0){
			lLstReturnList.add(lLstLastRow.get(0));
		}
		
		
		return lLstReturnList;
	}*/


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




	/*public List getEmployeeListNsdl(String yrCode, String month,
			String treasuryyno) {
		List empLst = null;



		StringBuilder  Strbld = new StringBuilder();

		try{

			Strbld.append(" SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,sum(paybill.DCPS)+sum(paybill.DCPS_PAY)+sum(paybill.DCPS_DELAY)+sum(paybill.DCPS_DA) as emp_amt, ");   
			Strbld.append(" sum(paybill.DED_ADJUST),loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no ");
			Strbld.append(" FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");
			Strbld.append(" inner join NSDL_PAYBILL_DATA paybill on paybill.EMP_ID=eis.EMP_ID ");
			Strbld.append(" inner join PAYBILL_HEAD_MPG head on head.PAYBILL_ID=paybill.PAYBILL_GRP_ID ");
			Strbld.append(" and paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA =paybill.DED_ADJUST  ");
			Strbld.append(" inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID ");
			Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
			Strbld.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2)  ");
			Strbld.append(" inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  ");
			Strbld.append(" where head.PAYBILL_YEAR='"+yrCode+"' and head.PAYBILL_MONTH='"+month+"'  and substr(ddo.ddo_code,1,4)='"+treasuryyno+"'  and  ");
			Strbld.append(" emp.PRAN_NO is not null and emp.AC_DCPS_MAINTAINED_BY=700174 and emp.REG_STATUS=1 and head.APPROVE_FLAG=1  ");
			Strbld.append(" group by  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no  having sum(paybill.DCPS)+sum(paybill.DCPS_PAY)+sum(paybill.DCPS_DELAY)+sum(paybill.DCPS_DA) >0  ");
			//Strbld.append("  and sum(paybill.DCPS)+sum(paybill.DCPS_PAY)+sum(paybill.DCPS_DELAY)+sum(paybill.DCPS_DA) =sum(paybill.DED_ADJUST) ");
			Strbld.append(" order by  reg.DDO_REG_NO ");
//end
			logger.info("   ---------"+Strbld.toString() );
			SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());

			empLst = lQuery.list();


		}
		catch(Exception e)
		{
			logger.info("Error occer in  getEmployeeList ---------"+ e);
		}
		return empLst;


	}*/



/*	public Long getDDoRegCount(String yrCode, String month,
			String treasuryyno) {
		// TODO Auto-generated method stub

		List temp=null;
		Long regCount=null;
		StringBuilder  Strbld = new StringBuilder();

		Strbld.append(" SELECT  count( distinct head.LOC_ID) ");   
		Strbld.append(" FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");
		Strbld.append(" inner join NSDL_PAYBILL_DATA paybill on paybill.EMP_ID=eis.EMP_ID ");
		Strbld.append(" inner join PAYBILL_HEAD_MPG head on head.PAYBILL_ID=paybill.PAYBILL_GRP_ID ");
		Strbld.append(" inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID ");
		Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
		Strbld.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2) ");
		Strbld.append(" inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  ");
		Strbld.append(" where head.PAYBILL_YEAR='"+yrCode+"' and head.PAYBILL_MONTH='"+month+"'  and substr(ddo.ddo_code,1,4)='"+treasuryyno+"'  and  ");
		Strbld.append(" emp.PRAN_NO is not null and emp.AC_DCPS_MAINTAINED_BY=700174 and emp.REG_STATUS=1 and head.APPROVE_FLAG=1  ");
		Strbld.append(" and paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA = paybill.DED_ADJUST and paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA >0 ");

		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());


		temp=lQuery.list();
		logger.info("temp size"+temp.size());
		if(temp!=null && temp.size()>0){
			regCount=Long.parseLong(temp.get(0).toString());
		}

		return regCount;
	}*/



	/*public List getEmployeeContriTotalList(String yrCode, String month,
			String treasuryyno) {
		// TODO Auto-generated method stub

		List temp=null;
		Long regCount=null;
		StringBuilder  Strbld = new StringBuilder();

		Strbld.append(" SELECT CAST(sum(paybill.DCPS)+sum(paybill.DCPS_PAY)+sum(paybill.DCPS_DELAY)+sum(paybill.DCPS_DA) AS VARCHAR(20))||'#'||CAST(sum(paybill.DED_ADJUST) AS VARCHAR(20))");   
		Strbld.append(" FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");
		Strbld.append(" inner join NSDL_PAYBILL_DATA paybill on paybill.EMP_ID=eis.EMP_ID ");
		Strbld.append(" inner join PAYBILL_HEAD_MPG head on head.PAYBILL_ID=paybill.PAYBILL_GRP_ID ");
		Strbld.append(" inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID ");
		Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
		Strbld.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2) ");
		Strbld.append(" inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  ");
		Strbld.append(" where  head.PAYBILL_YEAR='"+yrCode+"' and head.PAYBILL_MONTH='"+month+"'  and substr(ddo.ddo_code,1,4)='"+treasuryyno+"'  and  ");
		Strbld.append(" emp.PRAN_NO is not null and emp.AC_DCPS_MAINTAINED_BY=700174 and emp.REG_STATUS=1 and head.APPROVE_FLAG=1  ");
		Strbld.append(" and paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA = paybill.DED_ADJUST ");
		gLogger.info("Query is ***********getEmployeeContriTotalList***"+Strbld.toString());

		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());


		temp=lQuery.list();
		gLogger.info("Query is ***********getEmployeeContriTotalList* temp size is **"+temp.size());
		return temp;
	}*/


/*
	public String[] getEmployeeCountDdoregNsdl(String yrCode, String month,
			String treasuryyno) {
		// TODO Auto-generated method stub

		List empLst = null;

		String [] empCountLst=null;

		StringBuilder  Strbld = new StringBuilder();
		try{
			Strbld.append(" SELECT  count( distinct emp.PRAN_NO) ");   
			Strbld.append(" FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");
			Strbld.append(" inner join NSDL_PAYBILL_DATA paybill on paybill.EMP_ID=eis.EMP_ID ");
			Strbld.append(" inner join PAYBILL_HEAD_MPG head on head.PAYBILL_ID=paybill.PAYBILL_GRP_ID ");
			Strbld.append(" inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID ");
			Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
			Strbld.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2) ");
			Strbld.append(" inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  ");
			Strbld.append(" where paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA >0 and paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA =paybill.DED_ADJUST and head.PAYBILL_YEAR='"+yrCode+"' and head.PAYBILL_MONTH='"+month+"'  and substr(ddo.ddo_code,1,4)='"+treasuryyno+"'  and  ");
			Strbld.append(" emp.PRAN_NO is not null and emp.AC_DCPS_MAINTAINED_BY=700174 and emp.REG_STATUS=1 and head.APPROVE_FLAG=1 group by reg.DDO_REG_NO order by   reg.DDO_REG_NO");

			logger.info("query for count"+Strbld.toString());
			SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());


			empLst = lQuery.list();
			empCountLst = new String[empLst.size()];
			for(int i=0;i<empLst.size();i++)
				empCountLst[i]=empLst.get(i).toString();
		}
		catch(Exception e)
		{
			logger.info("Error occer in  getEmployeeList ---------"+ e);
		}
		return empCountLst;

	}


	//Query to be modified.
	public String[] getEmployeeListDdoregNsdl(String yrCode, String month,
			String treasuryyno) {
		// TODO Auto-generated method stub

		List empLst = null;

		

		StringBuilder  Strbld = new StringBuilder();


		Strbld.append(" SELECT CAST(sum(paybill.DCPS)+sum(paybill.DCPS_PAY)+sum(paybill.DCPS_DELAY)+sum(paybill.DCPS_DA) AS VARCHAR(20))||'#'||CAST(sum(paybill.DED_ADJUST) AS VARCHAR(20))");   
		Strbld.append(" FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");
		Strbld.append(" inner join NSDL_PAYBILL_DATA paybill on paybill.EMP_ID=eis.EMP_ID ");
		Strbld.append(" inner join PAYBILL_HEAD_MPG head on head.PAYBILL_ID=paybill.PAYBILL_GRP_ID and ");
		Strbld.append(" paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA =paybill.DED_ADJUST");
		Strbld.append(" inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID ");
		Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
		Strbld.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2) ");
		Strbld.append(" inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  ");
		Strbld.append(" where  head.PAYBILL_YEAR='"+yrCode+"' and head.PAYBILL_MONTH='"+month+"'  and substr(ddo.ddo_code,1,4)='"+treasuryyno+"'  and  ");
		Strbld.append(" emp.PRAN_NO is not null and emp.AC_DCPS_MAINTAINED_BY=700174 and emp.REG_STATUS=1 and head.APPROVE_FLAG=1   group by reg.DDO_REG_NO having sum(paybill.DCPS)+sum(paybill.DCPS_PAY)+sum(paybill.DCPS_DELAY)+sum(paybill.DCPS_DA) > 0  order by reg.DDO_REG_NO");


		logger.info("   ---------"+Strbld.toString() );
		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
		logger.info("script for all employee ---------"+lQuery.toString() );
		lQuery.setString("acMain",acMain);
			lQuery.setString("billno",billno);
			lQuery.setString("finType",finType);

		empLst = lQuery.list();
		String [] empDdoLst = new String[lQuery.list().size()];
		for(int i=0;i<empLst.size();i++)
			empDdoLst[i]=empLst.get(i).toString();

		return empDdoLst;
	}



	public List selectTrnPk(String yrCode, String month, String treasuryyno) throws Exception {


		List contrList = null;
		Query lQuery = null;
		StringBuilder lSBQuery = null;
		try{
			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT distinct emp.DCPS_ID   FROM TEMPEMPR3 temp inner join tempr3 r3 on r3.EMP_ID_NO=temp.EMP_ID_NO and temp.FIN_YEAR=r3.FIN_YEAR    "); 
			lSBQuery.append("  inner join SGVA_MONTH_MST mo on mo.MONTH_ID = r3.PAY_MONTH  and mo.LANG_ID = 'en_US'    ");
			lSBQuery.append(" inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO      ");
			lSBQuery.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2)  ");
			lSBQuery.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003  ");
			lSBQuery.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE    ");
			lSBQuery.append(" where temp.FIN_YEAR='"+yrCode+"' and loc.loc_id ='"+treasuryyno+"' and emp.AC_DCPS_MAINTAINED_BY=700174  and emp.pran_no is not null and temp.INT_EMPL_CONTRIB<>0 and  temp.BATCH_ID is null ");
			lSBQuery.append(" group by emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no,loc.loc_id order by reg.DDO_REG_NO ");

			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

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



	public void updateRepStatus(String dcpsContriIdPks, String batchId,
			String yrCode, String month, String treasuryyno) {
			StringBuffer sb= new StringBuffer();
			//ghibSession = getSession();
			try{
				sb.append("update TEMPEMPR3 set batch_id="+BatchId+"  ");

				sb.append(" where  emp_id_no='"+dcpsId+"' and  fin_year='"+finyr+"' and batch_id is null ");
				int i = ghibSession.createSQLQuery(sb.toString()).executeUpdate();

			}catch(Exception e){
				e.printStackTrace();
			}
			 }



	public void insertBatchHeader(String bhHeader1, String bhHeader,
			String bhHeader2, String string4, String string5,
			String currentdate, String string6, long ddoCount, int count,
			String govContri, String subContri, String total,String fileName, String yrCode, String month) {

		final Session session = this.getSession();
		final StringBuffer str = new StringBuffer();
		str.append("insert into NSDL_BH_dtls values ('"+bhHeader1+"','"+bhHeader+"','"+bhHeader2+"','"+string4+"','"+string5+"','"+currentdate+"',");
		str.append("'"+string6+"','"+ddoCount+"','"+count+"','"+govContri+"','"+subContri+"','"+total+"','"+fileName+"','"+yrCode+"','"+month+"','"+0+"',null,0,null,null)");
		final Query updateQuery = session.createSQLQuery(str.toString());
		logger.info("Query to insert in batch heaqder**********"+str.toString());

		updateQuery.executeUpdate();

	}



	public String getbatchIdCount(String batchIdPrefix) {
		String lLstReturnList = "";
		StringBuilder sb = new StringBuilder();


		sb.append(" select count(1)+1 from NSDL_BH_dtls where FILE_NAME like '"+batchIdPrefix+"%'");
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		lLstReturnList=  selectQuery.uniqueResult().toString();


		return lLstReturnList;
	}



	public void insertDHDetails(int i, String string, String string2,
			int j, String ddoRegNo, Long empCount,
			String totalEmplyDHContri, String totalEmplyerDHContri, String batchId) {

		final Session session = this.getSession();
		final StringBuffer str = new StringBuffer();
		str.append("insert into NSDL_DH_dtls values ('"+i+"','"+string+"','"+string2+"','"+j+"','"+ddoRegNo+"','"+empCount+"',");
		str.append("'"+totalEmplyDHContri+"','"+totalEmplyerDHContri+"','"+batchId+"',0)");
		final Query updateQuery = session.createSQLQuery(str.toString());
		logger.info("Query to insert in insertDHDetails heaqder**********"+str.toString());

		updateQuery.executeUpdate();

	}



	public void insertSDDetails(int i, String string, String string2,
			int j, int empCount, String pranno, String govEmpContri,
			String subempContri, String string3, String string4,
			String batchId, String string5, String ddoRegNo) {

		final Session session = this.getSession();
		final StringBuffer str = new StringBuffer();
		str.append("insert into NSDL_SD_dtls values ('"+i+"','"+string+"','"+string2+"','"+j+"','"+empCount+"','"+pranno+"',");
		str.append("'"+govEmpContri+"','"+subempContri+"','"+string3+"','"+string4+"','"+batchId+"','"+string5+"','"+ddoRegNo+"',0)");
		final Query updateQuery = session.createSQLQuery(str.toString());
		logger.info("Query to insert in insertSDDetails heaqder**********"+str.toString());

		updateQuery.executeUpdate();

	}*/



	 public List getAllData(String yrCode, String month, String strLocationCode)
	    throws Exception
	  {
	    List contrList = null;
	    Query lQuery = null;
	    StringBuilder lSBQuery = null;
	    try {
	      lSBQuery = new StringBuilder();
	      //lSBQuery.append("SELECT distinct bh.FILE_NAME,bh.BH_EMP_AMOUNT,bh.BH_EMPLR_AMOUNT,bh.TRANSACTION_ID,bh.file_status,bh.frn_no FROM NSDL_BH_DTLS bh where bh.YEAR='" + yrCode + "' and bh.MONTH='" + month + "' and((bh.FILE_STATUS=5 AND bh.FRN_NO <>0 AND bh.FRN_NO IS NOT NULL AND bh.TRANSACTION_ID is null) or ( bh.FILE_STATUS in (7,8) AND bh.FRN_NO <> 0 AND bh.FRN_NO IS NOT NULL AND bh.TRANSACTION_ID IS NOT NULL)) and bh.STATUS<>-1 and substr(bh.file_name,1,2)=substr('" + strLocationCode + "',1,2)   ");
	      lSBQuery.append("SELECT distinct bh.FILE_NAME,bh.BH_EMP_AMOUNT,bh.BH_EMPLR_AMOUNT,bh.TRANSACTION_ID,bh.file_status  FROM NSDL_BH_DTLS bh where bh.YEAR='" + yrCode + "' and bh.MONTH='" + month + "' and (( bh.FILE_STATUS=1 and bh.TRANSACTION_ID is null) or ( bh.FILE_STATUS in (7,8) and bh.TRANSACTION_ID is not null) ) and bh.STATUS<>-1 and bh.file_name like '" + strLocationCode + "%'   ");
	      lQuery = this.ghibSession.createSQLQuery(lSBQuery.toString());
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




	/*public List getBHDeatils(String fileNumber) {
		// TODO Auto-generated method stub
		return null;
	}



	public String getBatchData(String fileNumber) {

		String lLstReturnList = "";
		StringBuilder sb = new StringBuilder();


		sb.append(" select SR_NO||'^'||HEADER_NAME||'^'||BH_NO||'^'||BH_COL2||'^'||BH_FIX_NO||'^'||BH_DATE||'^'||BH_BATCH_FIX_ID||'^^'||BH_DDO_COUNT||'^'||BH_PRAN_COUNT||'^'||BH_EMP_AMOUNT||'^'||BH_EMPLR_AMOUNT||'^^'||BH_TOTAL_AMT||'^' from NSDL_BH_dtls ");
		sb.append(" where FILE_NAME='"+fileNumber+"' ");

		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		lLstReturnList=  selectQuery.uniqueResult().toString();


		return lLstReturnList;

	}



	public List getDHData(String fileNumber) {
		List lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT SR_NO||'^'||HEADER_NAME||'^'||DH_NO||'^'||DH_COL2||'^'||DH_DDO_REG_NO||'^'||BH_SD_COUNT||'^'||DH_EMP_AMOUNT||'^'||DH_EMPLR_AMOUNT||'^^',DH_DDO_REG_NO FROM NSDL_DH_dtls ");
		sb.append(" where FILE_NAME='"+fileNumber+"' order by SR_NO asc");

		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		lLstReturnList=  selectQuery.list();


		return lLstReturnList;


	}



	public List getSDDtls(String fileNumber, String ddoRegNo) {
		
		List lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT SR_NO||'^'||HEADER_NAME||'^'||SD_NO||'^'||SD_NO_2||'^'||SD_NO_3||'^'||SD_PRAN_NO||'^'||SD_EMP_AMOUNT||'^'||SD_EMPLR_AMOUNT||'^'||'^'||SD_TOTAL_AMT||'^'||SD_STATUS||'^'||SD_REMARK||'^' FROM NSDL_SD_DTLS  ");
		sb.append(" where   FILE_NAME='"+fileNumber+"'and DDO_REG_NO='"+ddoRegNo+"' order by SR_NO asc ");

		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		lLstReturnList=  selectQuery.list();


		return lLstReturnList;
	}



	public void deleteNsdlFile(String fileNumber) {
		
		StringBuilder sb = new StringBuilder();
		sb.append("  update NSDL_BH_DTLS set status=-1 where FILE_NAME='"+fileNumber+"' ");
		final Query updateQuery = ghibSession.createSQLQuery(sb.toString());
		
		logger.info("Query to delete in deleteNsdlFile heaqder**********"+sb.toString());

		updateQuery.executeUpdate();
		
		
		StringBuilder sb1 = new StringBuilder();
		sb1.append("  update NSDL_DH_DTLS set DH_status=-1 where FILE_NAME='"+fileNumber+"' ");
		final Query updateQuery1 = ghibSession.createSQLQuery(sb1.toString());
		
		logger.info("Query to delete in deleteNsdlFile heaqder**********"+sb1.toString());

		updateQuery1.executeUpdate();
		
		
		StringBuilder sb2 = new StringBuilder();
		sb2.append("  update NSDL_SD_DTLS set status=-1 where FILE_NAME='"+fileNumber+"' ");
		final Query updateQuery2 = ghibSession.createSQLQuery(sb2.toString());
		
		logger.info("Query to delete in deleteNsdlFile heaqder**********"+sb2.toString());

		updateQuery2.executeUpdate();
		
		
	}



	public void updateVoucherEntry(String month, String year, String fileNumber, String voucherNo, String vouchedate) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append("  update ifms.NSDL_BILL_dtls  set BILL_STATUS=1,VOUCHER_NO="+voucherNo+",VOUCHER_DATE='"+vouchedate+"' where FILE_NAME='"+fileNumber+"' ");
		final Query updateQuery = ghibSession.createSQLQuery(sb.toString());
		
		logger.info("Query to updateVoucherEntry**********"+sb.toString());

		updateQuery.executeUpdate();
		
	}



	public void createNSDLBillGenration(Long nsdl_paybill_pk, String year,
			String month, double employeeContribution, double employerContribution,
			double totalContribution, String fileNumber) {
		StringBuilder sb = new StringBuilder();
		sb.append("  INSERT INTO NSDL_BILL_dtls  VALUES ("+nsdl_paybill_pk+","+year+","+month+","+employeeContribution+","+employerContribution+","+totalContribution+",null,null,'0',sysdate,'"+fileNumber+"',null) ");
		final Query updateQuery = ghibSession.createSQLQuery(sb.toString());
		
		logger.info("Query to createNSDLBillGenration**********"+sb.toString());

		updateQuery.executeUpdate();
		
	}



	public String getBillStatus(String fileNumber) {
		List temp=null;
		String billStatus="";
		StringBuilder  Strbld = new StringBuilder();

		Strbld.append(" SELECT count(1) FROM NSDL_BILL_dtls where  FILE_NAME='"+fileNumber+"' ");   
		


		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());


	
			billStatus=lQuery.list().get(0).toString();
		

		return billStatus;
	}



	public List getBillNoDate(String fileNumber) {
		List billDetails=null;
		Long billNo=0l;
		StringBuilder  Strbld = new StringBuilder();

		Strbld.append(" SELECT BILL_ID,to_char(BILL_GENERATION_DATE,'dd/mm/yyyy'),month(BILL_GENERATION_DATE) FROM ifms.NSDL_BILL_dtls where  FILE_NAME='"+fileNumber+"' ");   
		


		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());


		billDetails=lQuery.list();
		//if(billDetails!=null && billDetails.size()>0){
			//billNo=Long.parseLong(temp.get(0).toString());
		//}

		return billDetails;
	}



	public void updateMD5hash(String crypt, String fileNumber) {
		StringBuilder sb = new StringBuilder();
		sb.append("  update NSDL_BH_DTLS set NSDL_FILE_HASH='"+crypt+"' where FILE_NAME='"+fileNumber+"' ");
		final Query updateQuery = ghibSession.createSQLQuery(sb.toString());
		
		logger.info("Query to delete in deleteNsdlFile heaqder**********"+sb.toString());

		updateQuery.executeUpdate();
		
	}



	public int checkForFileDtls(String crypt, String fileno) {
		List temp=null;
		int billStatus=3;
		StringBuilder  Strbld = new StringBuilder();
		Strbld.append(" SELECT FILE_NAME FROM NSDL_BH_DTLS where  FILE_NAME ='"+fileno+"' and NSDL_FILE_HASH='"+crypt+"'");   
		logger.info("Strbld is ***************"+Strbld.toString());
		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
		temp=lQuery.list();
		return temp.size();
	}



	public void updateFileStatus(int fileStatus, String fileno, String errorData) {
		StringBuilder sb = new StringBuilder();
		errorData=errorData.replace("'", "");
		sb.append("  update NSDL_BH_DTLS set file_status='"+fileStatus+"'  ");
		
		if(errorData!=null && !errorData.equals(""))
		sb.append(" , error_data='"+errorData+"' ");
		
		
		sb.append("   where FILE_NAME='"+fileno+"' ");
		final Query updateQuery = ghibSession.createSQLQuery(sb.toString());
		
		logger.info("Query to delete in deleteNsdlFile heaqder**********"+sb.toString());

		updateQuery.executeUpdate();
		
	}

	public String getTransactionId(String fileNumber) {
		List temp=null;
		String transactionId="";
			StringBuilder  Strbld = new StringBuilder();

			Strbld.append(" SELECT TRANSACTION_ID FROM NSDL_BH_DTLS where FILE_NAME='"+fileNumber+"' ");   
			


			SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());


			temp=lQuery.list();
			logger.info("temp size"+temp.size());
			if(temp!=null && temp.size()>0){
				if(temp.get(0)!=null){
					transactionId=temp.get(0).toString();
			}
			}

			return transactionId;
}
	public String getTreasuryName(String billNo) {
		List temp=null;
	String treasuryName="";
		StringBuilder  Strbld = new StringBuilder();

		Strbld.append(" SELECT LOC_NAME FROM CMN_LOCATION_MST where LOC_ID ='"+billNo+"' ");   
		


		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());


		temp=lQuery.list();
		logger.info("temp size"+temp.size());
		if(temp!=null && temp.size()>0){
			if(temp.get(0)!=null){
				treasuryName=temp.get(0).toString();
		}
		}

		return treasuryName;
	}



	public String getErrorData(String fileNumber) {

		List temp=null;
		String data="";
			StringBuilder  Strbld = new StringBuilder();

			Strbld.append(" SELECT cast(error_data as varchar(7000)) FROM NSDL_BH_DTLS where FILE_NAME='"+fileNumber+"' ");   
			


			SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
			logger.info("Query to getErrorData in  heaqder**********"+Strbld.toString());

			temp=lQuery.list();
			logger.info("temp size"+temp.size());
			if(temp!=null && temp.size()>0){
				if(temp.get(0)!=null){
					data=temp.get(0).toString();
			}
			}

			return data;

	}

*/

	public List getAllSubTreasury() {
		List<ComboValuesVO> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		//sb.append(" SELECT c.loc_id, c.loc_name FROM CMN_LOCATION_MST c inner join MST_DIGI_SIGN_ACTIVATION_DTLS m on c.LOC_ID = m.LOC_ID where c.department_id=100003 and m.ACTIVE_FLAG = 1 ");
		sb.append(" SELECT c.loc_id,c.loc_name FROM CMN_LOCATION_MST c where c.department_id=100003 and c.loc_id not in(9991,99991,2028915) ");////$t28July2022
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
	/*public String getDtoRegNo(String treasuryCode) {

		List temp=null;
		String data="";
			StringBuilder  Strbld = new StringBuilder();

			Strbld.append(" SELECT dto_reg_no FROM  MST_DTO_REG where substr(LOC_ID,1,2)="+treasuryCode );   
			


			SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
			logger.info("Query to getErrorData in  heaqder**********"+Strbld.toString());

			temp=lQuery.list();
			logger.info("temp size"+temp.size());
			if(temp!=null && temp.size()>0){
				if(temp.get(0)!=null){
					data=temp.get(0).toString();
			}
			}

			return data;

	}
*/


	  	  public void updateTransactionDetails(String fileName, String tranIds)

	  {
	    List temp = null;
	    String data = "";

	    StringBuilder Strbld = new StringBuilder();
	    StringBuilder Strblds = new StringBuilder();
	    StringBuilder Strbldx = new StringBuilder();

	    Strbldx.append(" select file_Status from nsdl_bh_dtls where file_name='" + fileName+"'");
	    SQLQuery lQueryx = this.ghibSession.createSQLQuery(Strbldx.toString());
	    int count = Integer.parseInt(lQueryx.uniqueResult().toString());
	    this.logger.info("Query to getErrorData in  heaqder**********" + Strbldx.toString());

	    if ((count == 7) || (count == 8)) {
	      Strbld.append(" INSERT INTO NSDL_TRANSACTION_ID_HISTORY (FILE_NAME,TRANSACTION_ID,CREATED_DATE,file_status,remark) VALUES ('" + fileName + "',(SELECT TRANSACTION_ID FROM NSDL_BH_DTLS where file_name='" + fileName + "'),sysdate,(SELECT FILE_STATUS FROM NSDL_BH_DTLS where file_name='" + fileName + "'),(SELECT REMARK_FOR_TRAN_ID_UPDATE FROM NSDL_BH_DTLS where file_name='" + fileName + "')) ");
	      this.logger.info("Query to getErrorData in  heaqder**********" + Strbld.toString());
	      SQLQuery lQuery = this.ghibSession.createSQLQuery(Strbld.toString());
	      lQuery.executeUpdate();

	      Strblds.append(" update NSDL_BH_DTLS set TRANSACTION_ID='" + tranIds + "', FILE_STATUS=11, STATUS=1 , REMARK_FOR_TRAN_ID_UPDATE=null WHERE FILE_NAME='" + fileName + "'");
	      this.logger.info("Query to getErrorData in  heaqder**********" + Strblds.toString());
	      SQLQuery lQuerys = this.ghibSession.createSQLQuery(Strblds.toString());

	      lQuerys.executeUpdate();

	      this.logger.info("Query to updateTransactionDetails in  heaqder**********" + Strbld.toString());
	    }
	    else
	    {
	      Strblds.append(" update NSDL_BH_DTLS set TRANSACTION_ID='" + tranIds + "', FILE_STATUS=11, STATUS=1 WHERE FILE_NAME='" + fileName + "'");
	      //Strblds.append(" update NSDL_BH_DTLS set TRANSACTION_ID='" + tranIds + "', FILE_STATUS=11, STATUS=1 WHERE FILE_NAME='" + fileName + "' and year='" + year + "' and month='" + month + "' ");
	      this.logger.info("Query to getErrorData in  heaqder**********" + Strblds.toString());
	      SQLQuery lQuerys = this.ghibSession.createSQLQuery(Strblds.toString());

	      lQuerys.executeUpdate();

	      this.logger.info("Query to updateTransactionDetails in  heaqder**********" + Strbld.toString());
	    }
	  }


	  public int getDigiActivationDtls(String trCode)
		{
			List temp=null;
			int activeFlag=0;
			
				StringBuilder  Strbld = new StringBuilder();
				try
				{
					Strbld.append(" select ACTIVE_FLAG from MST_DIGI_SIGN_ACTIVATION_DTLS where LOC_ID ="+trCode+" ");   
					


					SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
				

				


				temp=lQuery.list();
				if(temp!=null && temp.size()>0 && temp.get(0)!=null)
				{
					activeFlag=Integer.parseInt(temp.get(0).toString());
				}
				
				}
				catch(Exception e){
					logger.error("Exception in getDigiActivationDtls: " , e);
					e.printStackTrace();
				}	
				

				return activeFlag;
		}

	
	
}

