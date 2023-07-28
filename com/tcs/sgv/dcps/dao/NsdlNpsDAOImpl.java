package com.tcs.sgv.dcps.dao;
////KR revised Code till 31-12-2020
////$t 1/12/2020 2020 nov and dec 2020 month
//$t 2019 16/10 kavita sysout comments
import com.mysql.jdbc.PreparedStatement;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.valueobject.TrnNPSBeamsIntegration;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.report.Drconnectionclass;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.loader.custom.Return;

public class NsdlNpsDAOImpl extends GenericDaoHibernateImpl implements
		NsdlNpsDAO {
	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	public NsdlNpsDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		this.ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}
////$t 2019 10-10 before
	public List getDdoWiseTotalAmt(Long yearId, Long monthId,
			String strLocationCode, int flag) {
		
		List lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		//$t 2020 14-1 nearDear DGore 			
		//  SessionFactory sessionFactory1=new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        //  Session neardrSession = sessionFactory1.openSession();
        //
		try {
			sb.append(" SELECT ddo.DDO_CODE,mstemp.PRAN_NO,sum(paybill.GROSS_AMT) as GROSS_AMT ,sum(paybill.NET_TOTAL) as NET_TOTAL ,sum(paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA+paybill.DCPS_PAY_DIFF) as employye, ");////PayArrearDiff $t 23-2-2021
			// $t 2019
			sb.append("sum(paybill.DED_ADJUST+nvl(paybill.emplr_contri_arrears,0)+nvl(paybill.NPS_EMPLR_DIFFERENCE_ADJ,0)) as DED_ADJUST,reg.ddo_reg_no ");//$tC
			sb.append("FROM PAYBILL_HEAD_MPG head   ");
			sb.append("inner join NSDL_PAYBILL_DATA paybill on paybill.PAYBILL_GRP_ID =head.PAYBILL_ID ");
			sb.append("and paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA+paybill.DCPS_PAY_DIFF >paybill.DED_ADJUST ");////PayArrearDiff $t 23-2-2021
			sb.append("inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID inner join HR_EIS_EMP_MST hreis on hreis.EMP_ID=paybill.EMP_ID  ");
			sb.append("inner join MST_DCPS_EMP mstemp on mstemp.ORG_EMP_MST_ID=hreis.EMP_MPG_ID ");
			sb.append("inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
			sb.append("inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2) ");
			sb.append("inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  ");
			//if(strLocationCode.substring(0, 2).equals("12")||strLocationCode.substring(0, 2).equals("46")||strLocationCode.substring(0, 2).equals("23")||strLocationCode.substring(0, 2).equals("51")){////$t KR 5-1-2020
			sb.append("where head.PAYBILL_YEAR=" + yearId
					+ " and head.PAYBILL_MONTH=" + monthId
					+ "  and substr(ddo.ddo_code,1,2)='" + strLocationCode.substring(0, 2)
					+ "' and head.APPROVE_FLAG=1 ");/////$t KR
			/*}else{
				sb.append("where head.PAYBILL_YEAR=" + yearId
						+ " and head.PAYBILL_MONTH=" + monthId
						+ "  and substr(ddo.ddo_code,1,4)='" + strLocationCode
						+ "' and head.APPROVE_FLAG=1 ");
			}*/
			sb.append("and mstemp.PRAN_NO is not null and mstemp.PRAN_ACTIVE=1 and mstemp.DCPS_OR_GPF='Y' group by ddo.DDO_CODE,mstemp.PRAN_NO,reg.ddo_reg_no  ");////$t KR order by ddo.DDO_CODE order by ddo.DDO_CODE
			//old session 
			Query selectQuery = this.ghibSession.createSQLQuery(sb.toString());
			//new Slave session
            //Session ghiSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
            //Query selectQuery = ghiSession.createSQLQuery(sb.toString());
            //nearDR session
            //Query selectQuery = neardrSession.createSQLQuery(sb.toString());
			
			this.logger.info(" selectQuery  selectQuery  selectQuery  ---------"+ selectQuery.toString());
			lLstReturnList = selectQuery.list();

		} catch (Exception e) {
			e.printStackTrace();
			this.gLogger.error(" Error is : " + e, e);
		}finally{
			//gLogger.info("bf neardrSession.close();");
			//neardrSession.close();
			//gLogger.info("af neardrSession.close();");
		}
		return lLstReturnList;
	}
	
//////$t 2019 19-11
//	public List getDdoWiseTotalAmt(Long yearId, Long monthId,
//			String strLocationCode, int flag) {
//	List lLstReturnList = null;
//	
//	if (yearId.equals("2019")  && (monthId.equals("10")|| monthId.equals("11") || monthId.equals("9"))) {
//		try {
//			List TableData1 = null;
//			List TableData2 = null;
//			
//			StringBuilder sb = new StringBuilder();
//			sb.append(" SELECT ddo.DDO_CODE,mstemp.PRAN_NO,sum(paybill.GROSS_AMT) as GROSS_AMT ,sum(paybill.NET_TOTAL) as NET_TOTAL ,sum(paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA) as employye, ");
//			// $t 2019
//			sb.append("sum(paybill.DED_ADJUST) as DED_ADJUST,reg.ddo_reg_no ");
//			sb.append("FROM PAYBILL_HEAD_MPG head   ");
//			sb.append("inner join NSDL_PAYBILL_DATA paybill on paybill.PAYBILL_GRP_ID =head.PAYBILL_ID ");
//			sb.append("and paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA <=paybill.DED_ADJUST ");
//			sb.append("inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID inner join HR_EIS_EMP_MST hreis on hreis.EMP_ID=paybill.EMP_ID  ");
//			sb.append("inner join MST_DCPS_EMP mstemp on mstemp.ORG_EMP_MST_ID=hreis.EMP_MPG_ID ");
//			sb.append("inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
//			sb.append("inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2) ");
//			sb.append("inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  ");
//			sb.append("where head.PAYBILL_YEAR=" + yearId
//					+ " and head.PAYBILL_MONTH=" + monthId
//					+ "  and substr(ddo.ddo_code,1,4)='" + strLocationCode
//					+ "' and head.APPROVE_FLAG=1 ");
//			sb.append("and mstemp.PRAN_NO is not null and PRAN_ACTIVE=1 and mstemp.DCPS_OR_GPF='Y' group by ddo.DDO_CODE,mstemp.PRAN_NO,reg.ddo_reg_no ");
//			
//			Query selectQuery = this.ghibSession.createSQLQuery(sb.toString());
//			this.logger.info(" selectQuery  selectQuery  selectQuery  ---------"+ selectQuery.toString());
//			TableData1 = selectQuery.list();
//			
//			StringBuilder sb1 = new StringBuilder();
//			
//			 sb1.append(" SELECT ddo.DDO_CODE,mstemp.PRAN_NO,sum(paybill.GROSS_AMT) as GROSS_AMT ,sum(paybill.NET_TOTAL) as NET_TOTAL ,cast('0' as double) as employye, ");
//			 sb1.append("cast(SUM(nvl(paybill.emplr_contri_arrears,0))as double)as DED_ADJUST,reg.ddo_reg_no ");
//			 sb1.append("FROM PAYBILL_HEAD_MPG head   ");
//			 sb1.append("inner join NSDL_PAYBILL_DATA paybill on paybill.PAYBILL_GRP_ID =head.PAYBILL_ID ");
//			 sb1.append("inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID inner join HR_EIS_EMP_MST hreis on hreis.EMP_ID=paybill.EMP_ID  ");
//			 sb1.append("inner join MST_DCPS_EMP mstemp on mstemp.ORG_EMP_MST_ID=hreis.EMP_MPG_ID ");
//			 sb1.append("inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
//			 sb1.append("inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2) ");
//			 sb1.append("inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  ");
//			 sb1.append("where head.PAYBILL_YEAR=" + yearId +
//			 " and head.PAYBILL_MONTH=" + monthId +
//			 "  and substr(ddo.ddo_code,1,4)='" + strLocationCode +
//			 "' and head.APPROVE_FLAG=1 ");
//			 sb1.append("and mstemp.PRAN_NO is not null and PRAN_ACTIVE=1  and cast((nvl(paybill.emplr_contri_arrears,0)) as double) <> 0 and mstemp.DCPS_OR_GPF='Y' group by ddo.DDO_CODE,mstemp.PRAN_NO,reg.ddo_reg_no ");
//			
//
//			Query selectQuery1 = this.ghibSession.createSQLQuery(sb1.toString());
//			this.logger.info(" selectQuery  selectQuery  selectQuery  ---------"+ selectQuery1.toString());
//			TableData2 = selectQuery1.list();
//			
//			if ((TableData1 != null && TableData1.size() > 0)
//					|| (TableData2 != null && TableData2.size() > 0)) {
//				lLstReturnList = new ArrayList();
//				if (TableData1 != null && TableData1.size() > 0) {
//					lLstReturnList.addAll(TableData1);
//
//				}
//				if (TableData2 != null && TableData2.size() > 0) {
//					lLstReturnList.addAll(TableData2);
//				}
//
//			}
//		
//		} catch (Exception e) {
//			e.printStackTrace();
//			this.gLogger.error(" Error is : " + e, e);
//		}
//		
//	}else{
//	StringBuilder sb = new StringBuilder();
//	try {
//		sb.append(" SELECT ddo.DDO_CODE,mstemp.PRAN_NO,sum(paybill.GROSS_AMT) as GROSS_AMT ,sum(paybill.NET_TOTAL) as NET_TOTAL ,sum(paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA) as employye, ");
//		// $t 2019 remove arrear 
//		sb.append("sum(paybill.DED_ADJUST) as DED_ADJUST,reg.ddo_reg_no ");
//		sb.append("FROM PAYBILL_HEAD_MPG head   ");
//		sb.append("inner join NSDL_PAYBILL_DATA paybill on paybill.PAYBILL_GRP_ID =head.PAYBILL_ID ");
//		sb.append("and paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA <=paybill.DED_ADJUST ");
//		sb.append("inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID inner join HR_EIS_EMP_MST hreis on hreis.EMP_ID=paybill.EMP_ID  ");
//		sb.append("inner join MST_DCPS_EMP mstemp on mstemp.ORG_EMP_MST_ID=hreis.EMP_MPG_ID ");
//		sb.append("inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
//		sb.append("inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2) ");
//		sb.append("inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  ");
//		sb.append("where head.PAYBILL_YEAR=" + yearId
//				+ " and head.PAYBILL_MONTH=" + monthId
//				+ "  and substr(ddo.ddo_code,1,4)='" + strLocationCode
//				+ "' and head.APPROVE_FLAG=1 ");
//		sb.append("and mstemp.PRAN_NO is not null and PRAN_ACTIVE=1 and mstemp.DCPS_OR_GPF='Y' group by ddo.DDO_CODE,mstemp.PRAN_NO,reg.ddo_reg_no ");
//
//		Query selectQuery = this.ghibSession.createSQLQuery(sb.toString());
//		this.logger
//				.info(" selectQuery  selectQuery  selectQuery  ---------"
//						+ selectQuery.toString());
//		lLstReturnList = selectQuery.list();
//	} catch (Exception e) {
//		e.printStackTrace();
//		this.gLogger.error(" Error is : " + e, e);
//	}
//   }
//	return lLstReturnList;
//}
	

	public List getDdoWiseTotalAmtSentToNSDL(Long yearId, Long monthId,
			String strLocationCode, int flag) {

		List lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		try {
			
			/*sb.append(" select * from nps_mqt2 ");*/
			
			// cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt
			sb.append(" SELECT sd.SD_PRAN_NO,cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt ,bh.YEAR,bh.MONTH,sd.ddo_reg_no,cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt FROM NSDL_SD_DTLS sd ");//,mst.ddo_code
			sb.append(" inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.STATUS <>-1 ");//inner join MST_DDO_REG mst on sd.ddo_reg_no=mst.ddo_reg_no
			//if(strLocationCode.substring(0, 2).equals("12")||strLocationCode.substring(0, 2).equals("46")||strLocationCode.substring(0, 2).equals("23")||strLocationCode.substring(0, 2).equals("51")){////$t KR 5-1-2020
			sb.append(" and bh.YEAR=" + yearId + " and bh.MONTH=" + monthId+
					"  and bh.file_name like '" + strLocationCode.substring(0, 2) + "%' and bh.file_name not like '%D'");////$t KR //+ "  /////$t  25-3-2021 $t16Dec2021 AvoidDeputationEmp 
			/*}else{
			sb.append(" and bh.YEAR=" + yearId + " and bh.MONTH=" + monthId+
						"  and bh.file_name like '" + strLocationCode + "%' ");
			}*/
			
			sb.append(" group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH,sd.ddo_reg_no ");/////$t KR 2-2-2021 order by ddo code ,mst.ddo_code order by mst.ddo_code
			
			Query selectQuery = this.ghibSession.createSQLQuery(sb.toString());
			this.logger
					.info(" selectQuery  selectQuery  selectQuery  ---------"
							+ selectQuery.toString());
			lLstReturnList = selectQuery.list();
			// this.//ghibSession.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			this.gLogger.error(" Error is : " + e, e);
		}
		return lLstReturnList;
	}

	public List getDdoWiseTotalAmtForDeputation(Long yearId, Long monthId,
			String strLocationCode, String yearCode, String treasuryDDOCode) {// 2019,6,1111,2019,1111222222
		List lLstReturnList = null;
		List lLstReturnList1 = null;
		List lLstReturnList2 = null;
		List finalList = null;
		Collection l = null;
		List consolidatedList = null;
		StringBuilder sb = new StringBuilder();

		sb.append(" select case when substr(abc.DDO_CODE,1,2)='"+ strLocationCode.substring(0, 2)+ "' then abc.DDO_CODE else '"+treasuryDDOCode+"' end,'0','0',cast(abc.c-nvl(a.sd_amnt,0) as double),cast(abc.d-nvl(a.sd_emplr_amnt,0) as double),abc.DDO_CODE from ");
		sb.append("(SELECT   case when substr(ddo.DDO_CODE,1,2)='"+ strLocationCode.substring(0, 2)+ "' then ddo.DDO_CODE else '"+treasuryDDOCode+"' end,  mstemp.pran_no,'0','0', cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as c, cast(sum(nvl(trn.CONTRIBUTION_EMPLR,0) ) as double) as d,ddo.ddo_code FROM MST_DCPS_EMP mstemp ");
		sb.append("inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=mstemp.DCPS_EMP_ID ");
		sb.append("inner join ORG_DDO_MST ddo on ddo.DDO_CODE=mstemp.DDO_CODE ");
		sb.append("inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code ");
		sb.append("where trn.FIN_YEAR_ID="+ yearId+ " and trn.MONTH_ID="+ monthId+" and substr(trn.TREASURY_CODE,1,2)='"+ strLocationCode.substring(0, 2)+ "' " ////$t 8-4-2021 deputation KR one file
				+ "and (trn.IS_CHALLAN='Y' or trn.IS_ARREARS='Y')  and trn.STATUS='H'  and trn.IS_DEPUTATION='Y'  and mstemp.DDO_CODE is not null and mstemp.DEPT_DDO_CODE is null and PRAN_ACTIVE=1 and mstemp.PRAN_NO is not null and mstemp.DCPS_OR_GPF='Y' ");
		sb.append("group  by  ddo.DDO_CODE,mstemp.pran_no) abc ");
		sb.append(" left outer join ( SELECT sd.SD_PRAN_NO, cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt, cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt, bh.YEAR, bh.MONTH FROM NSDL_SD_DTLS sd ");
		sb.append(" inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME ");
		sb.append(" and bh.STATUS <>-1 ");
		sb.append(" and bh.YEAR=" + yearCode +" and bh.MONTH=" + monthId +" and bh.file_name like '" + strLocationCode.substring(0, 2) + "%D'" ////$t 8-4-2021 deputation KR one file
				+ " group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ) ");
		sb.append(" a on a.SD_PRAN_NO=abc.pran_no   where  cast(abc.c-nvl(a.sd_amnt,0) as double) >=0 and cast(abc.d-nvl(a.sd_emplr_amnt,0) as double) >0 ");

		Query selectQuery = this.ghibSession.createSQLQuery(sb.toString());
		this.logger.info(" selectQuery  selectQuery  selectQuery  ---------"
				+ selectQuery.toString());
		lLstReturnList = selectQuery.list();
		this.logger.info(" lLstReturnList ---------" + lLstReturnList.size());

		StringBuilder sb1 = new StringBuilder();

		sb1.append(" select abc.DDO_CODE,'0','0',cast(abc.c-nvl(a.sd_amnt,0) as double),cast(abc.d-nvl(a.sd_emplr_amnt,0) as double) from ");
		sb1.append("(SELECT   ddo.DDO_CODE,  mstemp.pran_no,'0','0', cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as c, cast(sum(nvl(trn.CONTRIBUTION_EMPLR,0) ) as double) as d FROM MST_DCPS_EMP mstemp ");
		sb1.append("inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=mstemp.DCPS_EMP_ID  ");
		sb1.append("inner join ORG_DDO_MST ddo on ddo.DDO_CODE='"+ treasuryDDOCode + "' ");
		sb1.append("inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code ");
		sb1.append("where trn.FIN_YEAR_ID="+ yearId+ " and trn.MONTH_ID="+ monthId+" and substr(trn.TREASURY_CODE,1,2)='"+ strLocationCode.substring(0, 2)+ "' " ////$t 8-4-2021 deputation KR one file
				+ "and (trn.IS_CHALLAN='Y' or trn.IS_ARREARS='Y')  and trn.STATUS='H'  and trn.IS_DEPUTATION='Y'  and mstemp.DDO_CODE is null and PRAN_ACTIVE=1 and mstemp.DEPT_DDO_CODE is null and mstemp.PRAN_NO is not null and mstemp.DCPS_OR_GPF='Y' ");
		sb1.append("group  by  ddo.DDO_CODE,mstemp.pran_no) abc ");
		sb1.append(" left outer join ( SELECT sd.SD_PRAN_NO, cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt, cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt, bh.YEAR, bh.MONTH FROM NSDL_SD_DTLS sd ");
		sb1.append(" inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME ");
		sb1.append(" and bh.STATUS <>-1 ");
		sb1.append(" and bh.YEAR=" + yearCode +" and bh.MONTH=" + monthId +" and bh.file_name like '" + strLocationCode.substring(0, 2) + "%D'" ////$t 8-4-2021 deputation KR one file
				+ " group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ) ");
		sb1.append(" a on a.SD_PRAN_NO=abc.pran_no   where  cast(abc.c-nvl(a.sd_amnt,0) as double) >=0 and cast(abc.d-nvl(a.sd_emplr_amnt,0) as double) >0 ");

		Query selectQuery1 = this.ghibSession.createSQLQuery(sb1.toString());
		lLstReturnList1 = selectQuery1.list();
		this.logger.info(" selectQuery  selectQuery  selectQuery  ---1------"
				+ selectQuery1.toString());
		this.logger.info(" lLstReturnList -----1----" + lLstReturnList1.size());
		StringBuilder sb2 = new StringBuilder();

		sb2.append(" select case when substr(abc.DDO_CODE,1,2)='"+ strLocationCode.substring(0, 2)+ "' then abc.DDO_CODE else '"+treasuryDDOCode+"' end,'0','0',cast(abc.c-nvl(a.sd_amnt,0) as double),cast(abc.d-nvl(a.sd_emplr_amnt,0) as double),abc.DDO_CODE from ");
		sb2.append("(SELECT case when substr(ddo.DDO_CODE,1,2)='"+ strLocationCode.substring(0, 2)+ "' then ddo.DDO_CODE else '"+treasuryDDOCode+"' end,  mstemp.pran_no,'0','0', cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as c, cast(sum(nvl(trn.CONTRIBUTION_EMPLR,0) ) as double) as d,ddo.DDO_CODE FROM MST_DCPS_EMP mstemp ");
		sb2.append("inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=mstemp.DCPS_EMP_ID  ");
		sb2.append("inner join ORG_DDO_MST ddo on ddo.DDO_CODE=mstemp.DEPT_DDO_CODE ");
		sb2.append("inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code ");
		sb2.append("where trn.FIN_YEAR_ID="+ yearId+ " and trn.MONTH_ID="+ monthId+" and substr(trn.TREASURY_CODE,1,2)='"+ strLocationCode.substring(0, 2)+ "' " ////$t 8-4-2021 deputation KR one file
				+ "and (trn.IS_CHALLAN='Y' or trn.IS_ARREARS='Y')  and trn.STATUS='H'  and trn.IS_DEPUTATION='Y'  and mstemp.DDO_CODE is null and PRAN_ACTIVE=1 and mstemp.DEPT_DDO_CODE is not null and mstemp.PRAN_NO is not null and mstemp.DCPS_OR_GPF='Y' ");
		sb2.append("group  by  ddo.DDO_CODE,mstemp.pran_no) abc ");
		sb2.append(" left outer join ( SELECT sd.SD_PRAN_NO, cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt, cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt, bh.YEAR, bh.MONTH FROM NSDL_SD_DTLS sd ");
		sb2.append(" inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME ");
		sb2.append(" and bh.STATUS <>-1 ");
		sb2.append(" and bh.YEAR=" + yearCode +" and bh.MONTH=" + monthId +" and bh.file_name like '" + strLocationCode.substring(0, 2) + "%D'" ////$t 8-4-2021 deputation KR one file
				+ " group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ) ");
		sb2.append(" a on a.SD_PRAN_NO=abc.pran_no   where  cast(abc.c-nvl(a.sd_amnt,0) as double) >=0 and cast(abc.d-nvl(a.sd_emplr_amnt,0) as double) >0 ");

		Query selectQuery2 = this.ghibSession.createSQLQuery(sb2.toString());
		lLstReturnList2 = selectQuery2.list();
		this.logger.info(" selectQuery2  selectQuery  selectQuery  ---------"
				+ selectQuery2.toString());
		this.logger.info(" lLstReturnList --2-------" + lLstReturnList2.size());
		if (((lLstReturnList != null) && (lLstReturnList.size() > 0))
				|| ((lLstReturnList1 != null) && (lLstReturnList1.size() > 0))
				|| ((lLstReturnList2 != null) && (lLstReturnList2.size() > 0))) {
			finalList = new ArrayList();
			if ((lLstReturnList != null) && (lLstReturnList.size() > 0)) {
				finalList.addAll(lLstReturnList);
			}
			if ((lLstReturnList1 != null) && (lLstReturnList1.size() > 0)) {
				finalList.addAll(lLstReturnList1);
			}
			if ((lLstReturnList2 != null) && (lLstReturnList2.size() > 0)) {
				finalList.addAll(lLstReturnList2);
			}
		}
		HashMap m = new HashMap();
		if ((finalList != null) && (finalList.size() > 0)) {
			for (int i = 0; i < finalList.size(); i++) {
				Object[] objI = (Object[]) finalList.get(i);
				if ((objI != null) && (m.containsKey(objI[0]))) {
					Object[] objNew = new Object[5];
					objNew[0] = objI[0];
					objNew[1] = objI[1];
					objNew[2] = objI[2];
					Object[] objNew1 = (Object[]) m.get(objI[0]);
					objNew[3] = Double.valueOf(Double.parseDouble(objI[3]
							.toString())
							+ Double.parseDouble(objNew1[3].toString()));
					objNew[4] = Double.valueOf(Double.parseDouble(objI[4]
							.toString())
							+ Double.parseDouble(objNew1[4].toString()));
					m.remove(objI[0]);
					m.put(objI[0], objNew);
				} else if (objI != null) {
					m.put(objI[0], objI);
				}
			}
		}
		if ((m != null) && (!m.isEmpty())) {
			l = m.values();
			Object[] obj1 = l.toArray();
			if ((obj1 != null) && (obj1.length > 0)) {
				consolidatedList = new ArrayList();
				for (int k = 0; k < obj1.length; k++) {
					consolidatedList.add(obj1[k]);
				}
			}
		}
		// this.//ghibSession.disconnect();
		return consolidatedList;
	}

	public Long getFinYearId(String finYearCode) {
		List sev = null;
		Long FinYearId = Long.valueOf(0L);
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT SFYM.FIN_YEAR_ID FROM SGVC_FIN_YEAR_MST SFYM ");
			lSBQuery.append(" WHERE SFYM.FIN_YEAR_CODE =:finYearCode ");

			Query lQuery = this.ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("finYearCode", finYearCode);

			sev = lQuery.list();
			if ((!sev.isEmpty()) && (sev.size() > 0) && (sev.get(0) != null)) {
				FinYearId = Long.valueOf(Long.parseLong(sev.get(0).toString()));
			}
			// this.//ghibSession.disconnect();
		} catch (Exception e) {
			this.logger.error(
					"Exception in getFinYearId of LNALedgerQueryDAOImpl: ", e);
		}
		return FinYearId;
	}
////$t 2020 17-1 year added 2020
////$t 31-12-2020 year added 2021
	public List getFinyear() {
		String query = "select finYearCode,finYearCode from SgvcFinYearMst where finYearCode between '2015' and '2022' order by finYearCode ASC";
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = this.ghibSession.createQuery(sb.toString());
		List lLstResult = selectQuery.list();
		// this.//ghibSession.disconnect();
		ComboValuesVO lObjComboValuesVO = null;
		if ((lLstResult != null) && (lLstResult.size() != 0)) {
			lLstReturnList = new ArrayList();
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				Object[] obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc(obj[1].toString());
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}
		return lLstReturnList;
	}

/*	
////$t 2019 union all deployed public List getEmployeeListNsdl(String
	 * yrCode, String month, String treasuryyno, int flag) { List empLst = null;
	 * 
	 * StringBuilder sb = new StringBuilder(); try { sb.append(
	 * " (select a.EMP_NAME,a.DCPS_ID,a.PRAN_NO,cast(a.emp_amt-nvl(b.sd_amnt,0) as double) as emp_amount ,cast(a.DED_ADJUST-nvl(b.sd_emplr_amnt,0) as double) as emplr_amount,a.loc_name,a.dto_reg_no,a.ddo_reg_no,b.sd_amnt from "
	 * ); sb.append(
	 * " (SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO, sum(paybill.DED_ADJUST) as DED_ADJUST , sum(paybill.DCPS)+sum(paybill.DCPS_PAY)+sum(paybill.DCPS_DELAY)+sum(paybill.DCPS_DA) as emp_amt, "
	 * ); sb.append(
	 * " loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID "
	 * ); sb.append(
	 * " inner join NSDL_PAYBILL_DATA paybill on paybill.EMP_ID=eis.EMP_ID ");
	 * sb.append(
	 * " inner join PAYBILL_HEAD_MPG head on head.PAYBILL_ID=paybill.PAYBILL_GRP_ID "
	 * ); sb.append(
	 * " and paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA <=paybill.DED_ADJUST "
	 * );
	 * sb.append(" inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID "
	 * );
	 * sb.append(" inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  "
	 * ); sb.append(
	 * " inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2) "
	 * ); sb.append(
	 * " inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  "
	 * ); sb.append(" where head.PAYBILL_YEAR=" + yrCode +
	 * " and head.PAYBILL_MONTH=" + month + " and substr(ddo.ddo_code,1,4)='" +
	 * treasuryyno + "' "); sb.append(
	 * " and emp.PRAN_NO is not null and PRAN_ACTIVE=1 and emp.REG_STATUS=1 and head.APPROVE_FLAG=1  "
	 * ); sb.append(
	 * " group by  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no ) a left outer join "
	 * ); sb.append(
	 * " (SELECT sd.SD_PRAN_NO,cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt,cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt ,bh.YEAR,bh.MONTH ,sd.ddo_reg_no FROM NSDL_SD_DTLS sd "
	 * ); sb.append(
	 * " inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.STATUS <>-1 "
	 * ); sb.append("  and bh.YEAR=" + yrCode + " and bh.MONTH=" + month +
	 * " and bh.file_name like '" + treasuryyno + "%' "); sb.append(
	 * "  group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ,sd.ddo_reg_no ) b on b.SD_PRAN_NO=a.PRAN_NO and b.ddo_reg_no=a.ddo_reg_no "
	 * ); sb.append(
	 * "  where cast(a.emp_amt-nvl(b.sd_amnt,0) as double) > 0 and cast(a.DED_ADJUST-nvl(b.sd_emplr_amnt,0) as double) > 0 order by  a.ddo_reg_no ) "
	 * ); sb.append("  UNION all "); sb.append(
	 * " (select a.EMP_NAME,a.DCPS_ID,a.PRAN_NO,cast(a.emp_amt-nvl(b.sd_amnt,0) as double) as emp_amount ,cast(a.DED_ADJUST-nvl(b.sd_emplr_amnt,0) as double) as emplr_amount,a.loc_name,a.dto_reg_no,a.ddo_reg_no,b.sd_amnt from "
	 * ); sb.append(" (SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO, ");
	 * sb.append("  '0' as emp_amt, ");
	 * sb.append("  SUM(nvl(paybill.emplr_contri_arrears,0))  as DED_ADJUST, ");
	 * sb.append("  loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no, ");
	 * sb.append("  nvl(paybill.emplr_contri_arrears,0) as emplr_contri_arrears "
	 * ); sb.append(
	 * "  FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID "
	 * ); sb.append(
	 * "  inner join NSDL_PAYBILL_DATA paybill on paybill.EMP_ID=eis.EMP_ID ");
	 * sb.append(
	 * "  inner join PAYBILL_HEAD_MPG head on head.PAYBILL_ID=paybill.PAYBILL_GRP_ID "
	 * );
	 * sb.append("  inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID "
	 * );
	 * sb.append("  inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  "
	 * ); sb.append(
	 * "  inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2) "
	 * ); sb.append(
	 * "  inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  "
	 * ); sb.append("  where head.PAYBILL_YEAR=" + yrCode +
	 * " and head.PAYBILL_MONTH=" + month + " and substr(ddo.ddo_code,1,4)='" +
	 * treasuryyno + "' "); sb.append(
	 * "  and emp.PRAN_NO is not null and PRAN_ACTIVE=1 and emp.REG_STATUS=1 and head.APPROVE_FLAG=1 and cast((nvl(paybill.emplr_contri_arrears,0)) as double) <> 0  "
	 * ); sb.append(
	 * "  group by  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no,paybill.emplr_contri_arrears ) a left outer join "
	 * ); sb.append(
	 * "  (SELECT sd.SD_PRAN_NO,cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt,cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt ,bh.YEAR,bh.MONTH ,sd.ddo_reg_no FROM NSDL_SD_DTLS sd "
	 * ); sb.append(
	 * "  inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.STATUS <>-1 "
	 * ); sb.append("  and bh.YEAR=" + yrCode + " and bh.MONTH=" + month +
	 * " and bh.file_name like '" + treasuryyno + "%' "); sb.append(
	 * "  group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ,sd.ddo_reg_no ) b on b.SD_PRAN_NO=a.PRAN_NO and b.ddo_reg_no=a.ddo_reg_no "
	 * ); sb.append(
	 * "  where cast(a.emp_amt-nvl(b.sd_amnt,0) as double) >= 0 and cast(a.DED_ADJUST-nvl(b.sd_emplr_amnt,0) as double) > 0 order by  a.ddo_reg_no ) "
	 * );
	 * 
	 * this.logger.info("   ---------" + sb.toString()); SQLQuery lQuery =
	 * this.ghibSession.createSQLQuery(sb.toString());
	 * 
	 * empLst = lQuery.list(); } catch (Exception e) {
	 * this.logger.info("Error occer in  getEmployeeList ---------" + e); }
	 * return empLst; }
*/	 

	public List getEmployeeListNsdl(String yrCode, String month,
			String treasuryyno, int flag) throws Exception {// $t 2019 union all
															// separated.

		List TableData = null;
////$t 2020 17-1 year 2020 n month jan added ||yrCode.equals("2020"))
		if ((yrCode.equals("2019") && (month.equals("10")|| month.equals("11") || month.equals("9")||month.equals("12")))
			||yrCode.equals("2020")
			//||(yrCode.equals("2021") && (month.equals("1") || month.equals("2")))) {
			||(yrCode.equals("2021"))) {//$tC
			List TableData1 = null;
			List TableData2 = null;

			StringBuilder sb = new StringBuilder();
			try {
				sb.append(" (select a.EMP_NAME,a.DCPS_ID,a.PRAN_NO,cast(a.emp_amt-nvl(b.sd_amnt,0) as double) as emp_amount ,cast(a.DED_ADJUST-nvl(b.sd_emplr_amnt,0) as double) as emplr_amount,a.loc_name,a.dto_reg_no,a.ddo_reg_no,b.sd_amnt from ");
				sb.append(" (SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO, sum(paybill.DED_ADJUST) as DED_ADJUST , sum(paybill.DCPS)+sum(paybill.DCPS_PAY)+sum(paybill.DCPS_DELAY)+sum(paybill.DCPS_DA)+sum(paybill.DCPS_PAY_DIFF) as emp_amt, ");////PayArrearDiff $t 23-2-2021
				sb.append(" loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");
				sb.append(" inner join NSDL_PAYBILL_DATA paybill on paybill.EMP_ID=eis.EMP_ID ");
				sb.append(" inner join PAYBILL_HEAD_MPG head on head.PAYBILL_ID=paybill.PAYBILL_GRP_ID ");
				sb.append(" and paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA+paybill.DCPS_PAY_DIFF >paybill.DED_ADJUST ");////PayArrearDiff $t 23-2-2021
				sb.append(" inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID ");
				sb.append(" inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
				sb.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2) ");
				sb.append(" inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  ");
				//if(treasuryyno.substring(0, 2).equals("12")||treasuryyno.substring(0, 2).equals("46")||treasuryyno.substring(0, 2).equals("23")||treasuryyno.substring(0, 2).equals("51")){////$t KR 5-1-2020
				sb.append(" where head.PAYBILL_YEAR=" + yrCode
						+ " and head.PAYBILL_MONTH=" + month
						+ " and substr(ddo.ddo_code,1,2)='" + treasuryyno.substring(0, 2)
						+ "' ");////$t KR 18-12-2020
				/*}else{
				sb.append(" where head.PAYBILL_YEAR=" + yrCode
						+ " and head.PAYBILL_MONTH=" + month
						+ " and substr(ddo.ddo_code,1,4)='" + treasuryyno
						+ "' ");
				}*/
				sb.append(" and emp.PRAN_NO is not null and emp.PRAN_ACTIVE=1 and emp.REG_STATUS=1 and head.APPROVE_FLAG=1  ");////$t emp.PRAN_ACTIVE=1
				sb.append(" group by  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no ) a left outer join ");
				sb.append(" (SELECT sd.SD_PRAN_NO,cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt,cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt ,bh.YEAR,bh.MONTH ,sd.ddo_reg_no FROM NSDL_SD_DTLS sd ");
				sb.append(" inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.STATUS <>-1 ");
				//if(treasuryyno.substring(0, 2).equals("12")||treasuryyno.substring(0, 2).equals("46")||treasuryyno.substring(0, 2).equals("23")||treasuryyno.substring(0, 2).equals("51")){////$t KR 5-1-2020
				sb.append("  and bh.YEAR=" + yrCode + " and bh.MONTH=" + month + 
						" and bh.file_name like '" + treasuryyno.substring(0, 2) + "%' and bh.file_name not like '%D' and sd.SD_REMARK like 'Contribution%' ");////$t KR //+ " and bh.file_name like '" + treasuryyno + "%'  "); /////$t 25-3-2021
				/*}else{
				sb.append("  and bh.YEAR=" + yrCode + " and bh.MONTH=" + month + 
						" and bh.file_name like '" + treasuryyno + "%'  ");
				}*/
				sb.append("  group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ,sd.ddo_reg_no ) b on b.SD_PRAN_NO=a.PRAN_NO and b.ddo_reg_no=a.ddo_reg_no ");
				sb.append("  where cast(a.emp_amt-nvl(b.sd_amnt,0) as double) > 0 and cast(a.DED_ADJUST-nvl(b.sd_emplr_amnt,0) as double) > 0 order by  a.ddo_reg_no ) ");
                //$t 4/11 >=
				// sb.append("  UNION all ");

				Query lQuery = this.ghibSession.createSQLQuery(sb.toString());
				gLogger.info("script for all Rregular employee ---------"
						+ sb.toString());

				TableData1 = lQuery.list();

				gLogger.info("script for all Rregular employee ---------"
						+ sb.toString());

				StringBuilder sb1 = new StringBuilder();

				sb1.append(" (select a.EMP_NAME,a.DCPS_ID,a.PRAN_NO,cast(a.emp_amt-nvl(b.sd_amnt,0) as double) as emp_amount ,cast(a.DED_ADJUST-nvl(b.sd_emplr_amnt,0) as double) as emplr_amount,a.loc_name,a.dto_reg_no,a.ddo_reg_no,b.sd_amnt from ");
				sb1.append(" (SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO, ");
				sb1.append("  '0' as emp_amt, ");
				sb1.append("  SUM(nvl(paybill.emplr_contri_arrears,0))+SUM(nvl(paybill.NPS_EMPLR_DIFFERENCE_ADJ,0)) as DED_ADJUST, ");////$tC
				sb1.append("  loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no ");
				//sb1.append("  nvl(paybill.emplr_contri_arrears,0) as emplr_contri_arrears ");
				sb1.append("  FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");
				sb1.append("  inner join NSDL_PAYBILL_DATA paybill on paybill.EMP_ID=eis.EMP_ID ");
				sb1.append("  inner join PAYBILL_HEAD_MPG head on head.PAYBILL_ID=paybill.PAYBILL_GRP_ID ");
				sb1.append("  inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID ");
				sb1.append("  inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
				sb1.append("  inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2) ");
				sb1.append("  inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  ");
				//if(treasuryyno.substring(0, 2).equals("12")||treasuryyno.substring(0, 2).equals("46")||treasuryyno.substring(0, 2).equals("23")||treasuryyno.substring(0, 2).equals("51")){////$t KR 5-1-2020
				sb1.append("  where head.PAYBILL_YEAR=" + yrCode
						+ " and head.PAYBILL_MONTH=" + month
						+ " and substr(ddo.ddo_code,1,2)=" + treasuryyno.substring(0,2) + " ");////$t KR 18-12-2020
				/*}else{
				sb1.append("  where head.PAYBILL_YEAR=" + yrCode
						+ " and head.PAYBILL_MONTH=" + month
						+ " and substr(ddo.ddo_code,1,4)=" + treasuryyno + " ");
				}*/
				sb1.append("  and emp.PRAN_NO is not null and emp.PRAN_ACTIVE=1 and emp.REG_STATUS=1 and head.APPROVE_FLAG=1 and (cast((nvl(paybill.emplr_contri_arrears,0)) as double) <> 0  or cast((nvl(paybill.NPS_EMPLR_DIFFERENCE_ADJ,0)) as double) <> 0)  ");////$tC
				sb1.append("  group by  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no ) a left outer join ");
				sb1.append("  (SELECT sd.SD_PRAN_NO,cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt,cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt ,bh.YEAR,bh.MONTH ,sd.ddo_reg_no FROM NSDL_SD_DTLS sd ");
				sb1.append("  inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.STATUS <>-1 ");
				//if(treasuryyno.substring(0, 2).equals("12")||treasuryyno.substring(0, 2).equals("46")||treasuryyno.substring(0, 2).equals("23")||treasuryyno.substring(0, 2).equals("51")){////$t KR 5-1-2020
				sb1.append("  and bh.YEAR=" + yrCode + " and bh.MONTH=" + month+ 
						" and bh.file_name like '" + treasuryyno.substring(0, 2) + "%' and sd.SD_REMARK like '8 Per Contribution%' ");////$t KR //+ " and bh.file_name like '" + treasuryyno + "%'  ");
				/*}else{
				sb1.append("  and bh.YEAR=" + yrCode + " and bh.MONTH=" + month+ 
						" and bh.file_name like '" + treasuryyno + "%'  ");
				}*/
				sb1.append("  group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ,sd.ddo_reg_no ) b on b.SD_PRAN_NO=a.PRAN_NO and b.ddo_reg_no=a.ddo_reg_no ");
				sb1.append("  where cast(a.emp_amt-nvl(b.sd_amnt,0) as double) > 0 and cast(a.DED_ADJUST-nvl(b.sd_emplr_amnt,0) as double) > 0 order by  a.ddo_reg_no ) ");

				Query lQuery1 = this.ghibSession.createSQLQuery(sb1.toString());

				gLogger.info("script for all Rregular employee ---------"
						+ sb1.toString());

				TableData2 = lQuery1.list();

				gLogger.info("script for all Rregular employee ---------"
						+ sb1.toString());

				if ((TableData1 != null && TableData1.size() > 0)
						|| (TableData2 != null && TableData2.size() > 0)) {
					TableData = new ArrayList();
					if (TableData1 != null && TableData1.size() > 0) {
						TableData.addAll(TableData1);

					}
					if (TableData2 != null && TableData2.size() > 0) {
						TableData.addAll(TableData2);
					}

				}

				// for (Iterator it = TableData.iterator(); it.hasNext();)
				// {
				//
				// Object[] lObj = (Object[])it.next();
				// System.out.println("emp name-->"+lObj[0].toString()+" "+"ddoRegno-->"+lObj[7].toString());
				// }
				// this.//ghibSession.disconnect();

			} catch (Exception e) {
				this.logger.info("Error occer in  getEmployeeList ---------"
						+ e);
			}

		} else {

			StringBuilder sb = new StringBuilder();
			try {
				sb.append(" (select a.EMP_NAME,a.DCPS_ID,a.PRAN_NO,cast(a.emp_amt-nvl(b.sd_amnt,0) as double) as emp_amount ,cast(a.DED_ADJUST-nvl(b.sd_emplr_amnt,0) as double) as emplr_amount,a.loc_name,a.dto_reg_no,a.ddo_reg_no,b.sd_amnt from ");
				sb.append(" (SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO, sum(paybill.DED_ADJUST) as DED_ADJUST , sum(paybill.DCPS)+sum(paybill.DCPS_PAY)+sum(paybill.DCPS_DELAY)+sum(paybill.DCPS_DA)+sum(paybill.DCPS_PAY_DIFF) as emp_amt, ");////PayArrearDiff $t 23-2-2021
				sb.append(" loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");
				sb.append(" inner join NSDL_PAYBILL_DATA paybill on paybill.EMP_ID=eis.EMP_ID ");
				sb.append(" inner join PAYBILL_HEAD_MPG head on head.PAYBILL_ID=paybill.PAYBILL_GRP_ID ");
				sb.append(" and paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA+paybill.DCPS_PAY_DIFF <=paybill.DED_ADJUST ");////PayArrearDiff $t 23-2-2021
				sb.append(" inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID ");
				sb.append(" inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
				sb.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2) ");
				sb.append(" inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  ");
				//if(treasuryyno.substring(0, 2).equals("12")||treasuryyno.substring(0, 2).equals("46")||treasuryyno.substring(0, 2).equals("23")||treasuryyno.substring(0, 2).equals("51")){////$t KR 5-1-2020
				sb.append(" where head.PAYBILL_YEAR=" + yrCode
						+ " and head.PAYBILL_MONTH=" + month
						+ " and substr(ddo.ddo_code,1,2)='" + treasuryyno.substring(0, 2)
						+ "' ");////$t KR 18-12-2020
				/*}else{
				sb.append(" where head.PAYBILL_YEAR=" + yrCode
						+ " and head.PAYBILL_MONTH=" + month
						+ " and substr(ddo.ddo_code,1,4)='" + treasuryyno
						+ "' ");					
				}*/
				sb.append(" and emp.PRAN_NO is not null and emp.PRAN_ACTIVE=1 and emp.REG_STATUS=1 and head.APPROVE_FLAG=1  ");
				sb.append(" group by  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no ) a left outer join ");
				sb.append(" (SELECT sd.SD_PRAN_NO,cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt,cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt ,bh.YEAR,bh.MONTH ,sd.ddo_reg_no FROM NSDL_SD_DTLS sd ");
				sb.append(" inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.STATUS <>-1 ");
				//if(treasuryyno.substring(0, 2).equals("12")||treasuryyno.substring(0, 2).equals("46")||treasuryyno.substring(0, 2).equals("23")||treasuryyno.substring(0, 2).equals("51")){////$t KR 5-1-2020
				sb.append("  and bh.YEAR=" + yrCode + " and bh.MONTH=" + month
						+ " and bh.file_name like '" + treasuryyno.substring(0, 2) + "%' and bh.file_name not like '%D' and sd.SD_REMARK like 'Contribution for%' and cast(sd.SD_EMP_AMOUNT as bigint) <= cast(sd.SD_EMPlr_AMOUNT as bigint) ");////$t KR /////$t  25-3-2021
				/*}else{
				sb.append("  and bh.YEAR=" + yrCode + " and bh.MONTH=" + month
						+ " and bh.file_name like '" + treasuryyno + "%' ");////$t KR
				}*/
				sb.append("  group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ,sd.ddo_reg_no ) b on b.SD_PRAN_NO=a.PRAN_NO and b.ddo_reg_no=a.ddo_reg_no ");
				sb.append("  where cast(a.emp_amt-nvl(b.sd_amnt,0) as double) > 0 and cast(a.DED_ADJUST-nvl(b.sd_emplr_amnt,0) as double) > 0 order by  a.ddo_reg_no ) ");

				this.logger.info("   ---------" + sb.toString());
				SQLQuery lQuery = this.ghibSession
						.createSQLQuery(sb.toString());

				TableData = lQuery.list();
			} catch (Exception e) {
				this.logger.info("Error occer in  getEmployeeList ---------"
						+ e);
			}

		}

		return TableData;
	}

	//
	// public List getEmployeeListNsdl(String yrCode, String month, String
	// treasuryyno, int flag) throws Exception
	// {//$t 2019 dr connection
	// List empLst = new ArrayList() ;
	// //$t 2019
	// //List finalList = null;
	// //List empLst = null ;
	// List lLstDept = null;
	// String USER= "";
	// String PASS = "";
	// String passward = "";
	// String passward1 = "";
	// String passwardStr = "@99";
	// lLstDept = getuser();
	// if ((lLstDept != null) && (lLstDept.size() > 0) && (lLstDept.get(0) !=
	// null))
	// {
	// Object[] obj = (Object[])lLstDept.get(0);
	// if ((obj[0] != null) && (obj[1] != null))
	// {
	// USER = obj[0].toString();
	// PASS = obj[1].toString();
	// }
	// System.out.println("USER" + USER + "PASS" + PASS);
	// }
	// passward1 = getHexStringConverterInstance(PASS);
	// passward = passward1 + passwardStr;
	// System.out.println("passward" + passward);
	//
	// Connection con = Drconnectionclass.getConnection(USER, passward);
	// //$t 2019 as sd_amnt
	//
	//
	// PreparedStatement stmt = null;
	//
	//
	// // lPStmt = con.prepareStatement(sb.toString());
	// // ResultSet lRs2 = null;
	// //     lRs2 = lPStmt.executeQuery();
	//
	//
	// StringBuilder sb = new StringBuilder();
	// stmt = con.prepareStatement(sb.toString());
	//
	// ResultSet rs = null;
	//
	//
	// try
	// {
	// sb.append(" (select a.EMP_NAME,a.DCPS_ID,a.PRAN_NO,cast(a.emp_amt-nvl(b.sd_amnt,0) as double) as emp_amount ,cast(a.DED_ADJUST-nvl(b.sd_emplr_amnt,0) as double) as emplr_amount,a.loc_name,a.dto_reg_no,a.ddo_reg_no,  cast(nvl(b.sd_amnt,0)as double) as sd_amnt from ");
	// sb.append(" (SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO, sum(paybill.DED_ADJUST) as DED_ADJUST , sum(paybill.DCPS)+sum(paybill.DCPS_PAY)+sum(paybill.DCPS_DELAY)+sum(paybill.DCPS_DA) as emp_amt, ");
	// sb.append(" loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");
	// sb.append(" inner join NSDL_PAYBILL_DATA paybill on paybill.EMP_ID=eis.EMP_ID ");
	// sb.append(" inner join PAYBILL_HEAD_MPG head on head.PAYBILL_ID=paybill.PAYBILL_GRP_ID ");
	// sb.append(" and paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA <=paybill.DED_ADJUST ");
	// sb.append(" inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID ");
	// sb.append(" inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
	// sb.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2) ");
	// sb.append(" inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  ");
	// sb.append(" where head.PAYBILL_YEAR=" + yrCode +
	// " and head.PAYBILL_MONTH=" + month + " and substr(ddo.ddo_code,1,4)=" +
	// treasuryyno + " ");
	// sb.append(" and emp.PRAN_NO is not null and PRAN_ACTIVE=1 and emp.REG_STATUS=1 and head.APPROVE_FLAG=1  ");
	// sb.append(" group by  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no ) a left outer join ");
	// sb.append(" (SELECT sd.SD_PRAN_NO,cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt,cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt ,bh.YEAR,bh.MONTH ,sd.ddo_reg_no FROM NSDL_SD_DTLS sd ");
	// sb.append(" inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.STATUS <>-1 ");
	// sb.append("  and bh.YEAR=2019 and bh.MONTH=9 and bh.file_name like '1111%' ");
	// sb.append("  group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ,sd.ddo_reg_no ) b on b.SD_PRAN_NO=a.PRAN_NO and b.ddo_reg_no=a.ddo_reg_no ");
	// sb.append("  where cast(a.emp_amt-nvl(b.sd_amnt,0) as double) > 0 and cast(a.DED_ADJUST-nvl(b.sd_emplr_amnt,0) as double) > 0 order by  a.ddo_reg_no ) ");
	// sb.append("  UNION all ");
	// sb.append(" (select a.EMP_NAME,a.DCPS_ID,a.PRAN_NO,cast(a.emp_amt-nvl(b.sd_amnt,0) as double) as emp_amount ,cast(a.DED_ADJUST-nvl(b.sd_emplr_amnt,0) as double) as emplr_amount,a.loc_name,a.dto_reg_no,a.ddo_reg_no, cast(nvl(b.sd_amnt,0)as double) as sd_amnt from ");
	// sb.append(" (SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO, ");
	// sb.append("  '0' as emp_amt, ");
	// sb.append("  SUM(nvl(paybill.emplr_contri_arrears,0))  as DED_ADJUST, ");
	// sb.append("  loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no, ");
	// sb.append("  nvl(paybill.emplr_contri_arrears,0) as emplr_contri_arrears ");
	// sb.append("  FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");
	// sb.append("  inner join NSDL_PAYBILL_DATA paybill on paybill.EMP_ID=eis.EMP_ID ");
	// sb.append("  inner join PAYBILL_HEAD_MPG head on head.PAYBILL_ID=paybill.PAYBILL_GRP_ID ");
	// sb.append("  inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID ");
	// sb.append("  inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
	// sb.append("  inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2) ");
	// sb.append("  inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  ");
	// sb.append("  where head.PAYBILL_YEAR=" + yrCode +
	// " and head.PAYBILL_MONTH=" + month + " and substr(ddo.ddo_code,1,4)=" +
	// treasuryyno + " ");
	// sb.append("  and emp.PRAN_NO is not null and PRAN_ACTIVE=1 and emp.REG_STATUS=1 and head.APPROVE_FLAG=1 and cast((nvl(paybill.emplr_contri_arrears,0)) as double) <> 0  ");
	// sb.append("  group by  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no,paybill.emplr_contri_arrears ) a left outer join ");
	// sb.append("  (SELECT sd.SD_PRAN_NO,cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt,cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt ,bh.YEAR,bh.MONTH ,sd.ddo_reg_no FROM NSDL_SD_DTLS sd ");
	// sb.append("  inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.STATUS <>-1 ");
	// sb.append("  and bh.YEAR=2019 and bh.MONTH=9 and bh.file_name like '1111%' ");
	// sb.append("  group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ,sd.ddo_reg_no ) b on b.SD_PRAN_NO=a.PRAN_NO and b.ddo_reg_no=a.ddo_reg_no ");
	// sb.append("  where cast(a.emp_amt-nvl(b.sd_amnt,0) as double) >= 0 and cast(a.DED_ADJUST-nvl(b.sd_emplr_amnt,0) as double) > 0 order by  a.ddo_reg_no ) ");
	//
	// //Drconnectionclass conn=
	//
	// this.logger.info("   ---------" + sb.toString());
	// // SQLQuery lQuery = this.ghibSession.createSQLQuery(sb.toString());
	// rs=stmt.executeQuery(sb.toString());
	//
	// empLst.add(rs);
	//
	//
	//
	// //finalList = new ArrayList();
	//
	// // while (rs.next()) {
	// // ArrayList emplst1=new ArrayList();
	// // //finalList = new ArrayList();
	// // emplst1.add(rs.getString(1));
	// // emplst1.add(rs.getString(2));
	// // emplst1.add(rs.getString(3));
	// // emplst1.add(rs.getString(4));
	// // emplst1.add(rs.getString(5));
	// // emplst1.add(rs.getString(6));
	// // emplst1.add(rs.getString(7));
	// // emplst1.add(rs.getString(8));
	// // emplst1.add(rs.getString(9));
	// // empLst.addAll(emplst1);
	// // }
	//
	// //empLst.addAll(finalList);
	// }
	// catch (Exception e)
	// {
	// this.logger.info("Error occer in  getEmployeeList ---------" + e);
	// }
	// return empLst;
	// }

	public List getEmployeeListNsdlForDeputation(String yrCode, String month,
			String treasuryyno, String year, String treasuryDDOCode) {
		List empLst = null;
		List lLstReturnList1 = null;
		List lLstReturnList2 = null;
		List finalList = null;

		StringBuilder sb = new StringBuilder();
		try {
			sb.append(" SELECT abc.EMP_NAME, abc.DCPS_ID, abc.PRAN_NO, cast(abc.c-nvl(a.sd_amnt,0) as double), cast(abc.d-nvl(a.sd_amnt,0) as double), abc.ddo_reg_no , a.sd_amnt from ");
			sb.append(" (select mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as c, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as d, reg.ddo_reg_no ");
			sb.append(" FROM MST_DCPS_EMP mstemp inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=mstemp.DCPS_EMP_ID inner join ORG_DDO_MST ddo on ddo.DDO_CODE=mstemp.DDO_CODE inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code ");
			sb.append(" where trn.FIN_YEAR_ID="
					+ yrCode
					+ " and trn.MONTH_ID="
					+ month
					+ "  and substr(trn.TREASURY_CODE,1,4)='"
					+ treasuryyno
					+ "' and trn.IS_CHALLAN='Y' and trn.STATUS='H' and trn.IS_DEPUTATION='Y' and mstemp.PRAN_NO is not null and PRAN_ACTIVE=1  ");
			sb.append(" and mstemp.DDO_CODE is not null and mstemp.DCPS_OR_GPF='Y' ");
			sb.append(" group by mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, reg.ddo_reg_no) abc ");
			sb.append(" left outer join (    SELECT    sd.SD_PRAN_NO,    cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt ,    bh.YEAR,    bh.MONTH    FROM NSDL_SD_DTLS sd    inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME ");
			sb.append(" and bh.STATUS <>-1    and bh.YEAR=" + year
					+ " and bh.MONTH=" + month + " and ");
			sb.append(" bh.file_name like '%"
					+ treasuryyno
					+ "%D'    group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ) a on a.sd_pran_no=abc.pran_no ");
			sb.append(" where  cast(abc.c-nvl(a.sd_amnt,0) as double) >0 order by abc.DDO_REG_NO ");
			this.logger
					.error("   -----EmployeeList getEmployeeListNsdlForDeputation----"
							+ sb.toString());
			this.logger.error("   ---------" + sb.toString());
			SQLQuery lQuery = this.ghibSession.createSQLQuery(sb.toString());

			empLst = lQuery.list();

			StringBuilder sb1 = new StringBuilder();
			sb1.append(" SELECT abc.EMP_NAME, abc.DCPS_ID, abc.PRAN_NO, cast(abc.c-nvl(a.sd_amnt,0) as double), cast(abc.d-nvl(a.sd_amnt,0) as double), abc.ddo_reg_no , a.sd_amnt from ");
			sb1.append(" (select mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as c, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as d, reg.ddo_reg_no ");
			sb1.append(" FROM MST_DCPS_EMP mstemp inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=mstemp.DCPS_EMP_ID inner join ORG_DDO_MST ddo on ddo.DDO_CODE='"
					+ treasuryDDOCode
					+ "' inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code ");
			sb1.append(" where trn.FIN_YEAR_ID="
					+ yrCode
					+ " and trn.MONTH_ID="
					+ month
					+ "  and substr(trn.TREASURY_CODE,1,4)='"
					+ treasuryyno
					+ "' and trn.IS_CHALLAN='Y' and trn.STATUS='H' and trn.IS_DEPUTATION='Y' and mstemp.PRAN_NO is not null and PRAN_ACTIVE=1  ");
			sb1.append(" and mstemp.DDO_CODE is null and mstemp.DEPT_DDO_CODE is null and mstemp.DCPS_OR_GPF='Y' ");
			sb1.append(" group by mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, reg.ddo_reg_no) abc ");
			sb1.append(" left outer join (    SELECT    sd.SD_PRAN_NO,    cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt ,    bh.YEAR,    bh.MONTH    FROM NSDL_SD_DTLS sd    inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME ");
			sb1.append(" and bh.STATUS <>-1    and bh.YEAR=" + year
					+ " and bh.MONTH=" + month + " and ");
			sb1.append(" bh.file_name like '%"
					+ treasuryyno
					+ "%D'    group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ) a on a.sd_pran_no=abc.pran_no ");
			sb1.append(" where  cast(abc.c-nvl(a.sd_amnt,0) as double) >0 order by abc.DDO_REG_NO ");

			this.logger.error("   ---------" + sb.toString());
			SQLQuery lQuery1 = this.ghibSession.createSQLQuery(sb1.toString());

			lLstReturnList1 = lQuery1.list();

			StringBuilder sb2 = new StringBuilder();
			sb2.append(" SELECT abc.EMP_NAME, abc.DCPS_ID, abc.PRAN_NO, cast(abc.c-nvl(a.sd_amnt,0) as double), cast(abc.d-nvl(a.sd_amnt,0) as double), abc.ddo_reg_no , a.sd_amnt from ");
			sb2.append(" (select mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as c, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as d, reg.ddo_reg_no ");
			sb2.append(" FROM MST_DCPS_EMP mstemp inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=mstemp.DCPS_EMP_ID inner join ORG_DDO_MST ddo on ddo.DDO_CODE= mstemp.DEPT_DDO_CODE inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code ");
			sb2.append(" where trn.FIN_YEAR_ID="
					+ yrCode
					+ " and trn.MONTH_ID="
					+ month
					+ "  and substr(trn.TREASURY_CODE,1,4)='"
					+ treasuryyno
					+ "' and trn.IS_CHALLAN='Y' and trn.STATUS='H' and trn.IS_DEPUTATION='Y' and mstemp.PRAN_NO is not null and PRAN_ACTIVE=1  ");
			sb2.append(" and mstemp.DDO_CODE is null and mstemp.DEPT_DDO_CODE is not null and mstemp.DCPS_OR_GPF='Y' ");
			sb2.append(" group by mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, reg.ddo_reg_no) abc ");
			sb2.append(" left outer join (    SELECT    sd.SD_PRAN_NO,    cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt ,    bh.YEAR,    bh.MONTH    FROM NSDL_SD_DTLS sd    inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME ");
			sb2.append(" and bh.STATUS <>-1    and bh.YEAR=" + year
					+ " and bh.MONTH=" + month + " and ");
			sb2.append(" bh.file_name like '%"
					+ treasuryyno
					+ "%D'    group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ) a on a.sd_pran_no=abc.pran_no ");
			sb2.append(" where  cast(abc.c-nvl(a.sd_amnt,0) as double) >0 order by abc.DDO_REG_NO ");

			this.logger.error("   ---------" + sb.toString());
			SQLQuery lQuery2 = this.ghibSession.createSQLQuery(sb2.toString());

			lLstReturnList2 = lQuery2.list();
			if (((empLst != null) && (empLst.size() > 0))
					|| ((lLstReturnList1 != null) && (lLstReturnList1.size() > 0))
					|| ((lLstReturnList2 != null) && (lLstReturnList2.size() > 0))) {
				finalList = new ArrayList();
				if ((empLst != null) && (empLst.size() > 0)) {
					finalList.addAll(empLst);
				}
				if ((lLstReturnList1 != null) && (lLstReturnList1.size() > 0)) {
					finalList.addAll(lLstReturnList1);
				}
				if ((lLstReturnList2 != null) && (lLstReturnList2.size() > 0)) {
					finalList.addAll(lLstReturnList2);
				}
				// this.//ghibSession.disconnect();
			}

		} catch (Exception e) {
			this.logger.info("Error occer in  getEmployeeList ---------" + e);
		}
		return finalList;
	}

/*	
	 * public List getEmployeeListNsdlForDeputation(String yrCode, String month,
	 * String treasuryyno, String year, String treasuryDDOCode, String trDdoReg)
	 * {//$t back up List empLst = null; List lLstReturnList1 = null; List
	 * lLstReturnList2 = null; List finalList = null; List empLstNew = null;
	 * List lLstReturnList2New = null; List lLstReturnList2New2 = null;
	 * 
	 * StringBuilder sb = new StringBuilder(); try { sb.append(
	 * " SELECT abc.EMP_NAME, abc.DCPS_ID, abc.PRAN_NO , cast(abc.c-nvl(a.sd_amnt,0) as double), cast(abc.d-nvl(a.sd_amnt,0) as double), abc.ddo_reg_no , a.sd_amnt from "
	 * ); sb.append(
	 * " (select mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as c, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as d, reg.ddo_reg_no "
	 * ); sb.append(
	 * " FROM MST_DCPS_EMP mstemp inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=mstemp.DCPS_EMP_ID inner join ORG_DDO_MST ddo on ddo.DDO_CODE=mstemp.DDO_CODE inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code "
	 * ); sb.append(" where trn.FIN_YEAR_ID=" + yrCode + " and trn.MONTH_ID=" +
	 * month + "  and substr(trn.TREASURY_CODE,1,4)='" + treasuryyno +
	 * "' and trn.IS_CHALLAN='Y' and trn.STATUS='H' and trn.IS_DEPUTATION='Y' and mstemp.PRAN_NO is not null and PRAN_ACTIVE=1  "
	 * );
	 * sb.append(" and mstemp.DDO_CODE is not null and mstemp.DCPS_OR_GPF='Y' "
	 * ); sb.append(
	 * " group by mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, reg.ddo_reg_no) abc "
	 * ); sb.append(
	 * " left outer join (    SELECT    sd.SD_PRAN_NO,    cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt ,    bh.YEAR,    bh.MONTH    FROM NSDL_SD_DTLS sd    inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME "
	 * ); sb.append(" and bh.STATUS <>-1    and bh.YEAR=" + year +
	 * " and bh.MONTH=" + month + " and "); sb.append(" bh.file_name like '%" +
	 * treasuryyno +
	 * "%D'    group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ) a on a.sd_pran_no=abc.pran_no "
	 * ); sb.append(
	 * " where  cast(abc.c-nvl(a.sd_amnt,0) as double) >0 order by abc.DDO_REG_NO "
	 * );
	 * 
	 * this.logger.error("   ---------" + sb.toString()); SQLQuery lQuery =
	 * this.ghibSession.createSQLQuery(sb.toString());
	 * 
	 * empLst = lQuery.list();
	 * 
	 * StringBuilder sb1 = new StringBuilder(); sb1.append(
	 * " SELECT abc.EMP_NAME, abc.DCPS_ID, abc.PRAN_NO, cast(abc.c-nvl(a.sd_amnt,0) as double), cast(abc.d-nvl(a.sd_amnt,0) as double), abc.ddo_reg_no , a.sd_amnt from "
	 * ); sb1.append(
	 * " (select mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as c, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as d, reg.ddo_reg_no "
	 * ); sb1.append(
	 * " FROM MST_DCPS_EMP mstemp inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=mstemp.DCPS_EMP_ID inner join ORG_DDO_MST ddo on ddo.DDO_CODE='"
	 * + treasuryDDOCode +
	 * "' inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code ");
	 * sb1.append(" where trn.FIN_YEAR_ID=" + yrCode + " and trn.MONTH_ID=" +
	 * month + "  and substr(trn.TREASURY_CODE,1,4)='" + treasuryyno +
	 * "' and trn.IS_CHALLAN='Y' and trn.STATUS='H' and trn.IS_DEPUTATION='Y' and mstemp.PRAN_NO is not null and PRAN_ACTIVE=1  "
	 * ); sb1.append(
	 * " and mstemp.DDO_CODE is null and mstemp.DEPT_DDO_CODE is null and mstemp.DCPS_OR_GPF='Y' "
	 * ); sb1.append(
	 * " group by mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, reg.ddo_reg_no) abc "
	 * ); sb1.append(
	 * " left outer join (    SELECT    sd.SD_PRAN_NO,    cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt ,    bh.YEAR,    bh.MONTH    FROM NSDL_SD_DTLS sd    inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME "
	 * ); sb1.append(" and bh.STATUS <>-1    and bh.YEAR=" + year +
	 * " and bh.MONTH=" + month + " and "); sb1.append(" bh.file_name like '%" +
	 * treasuryyno +
	 * "%D'    group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ) a on a.sd_pran_no=abc.pran_no "
	 * ); sb1.append(
	 * " where  cast(abc.c-nvl(a.sd_amnt,0) as double) >0 order by abc.DDO_REG_NO "
	 * );
	 * 
	 * this.logger.error("   ---------" + sb.toString()); SQLQuery lQuery1 =
	 * this.ghibSession.createSQLQuery(sb1.toString());
	 * 
	 * lLstReturnList1 = lQuery1.list();
	 * 
	 * StringBuilder sb2 = new StringBuilder(); sb2.append(
	 * " SELECT abc.EMP_NAME, abc.DCPS_ID, abc.PRAN_NO, cast(abc.c-nvl(a.sd_amnt,0) as double), cast(abc.d-nvl(a.sd_amnt,0) as double), abc.ddo_reg_no , a.sd_amnt from "
	 * ); sb2.append(
	 * " (select mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as c, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as d, reg.ddo_reg_no "
	 * ); sb2.append(
	 * " FROM MST_DCPS_EMP mstemp inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=mstemp.DCPS_EMP_ID inner join ORG_DDO_MST ddo on ddo.DDO_CODE= mstemp.DEPT_DDO_CODE inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code "
	 * ); sb2.append(" where trn.FIN_YEAR_ID=" + yrCode + " and trn.MONTH_ID=" +
	 * month + "  and substr(trn.TREASURY_CODE,1,4)='" + treasuryyno +
	 * "' and trn.IS_CHALLAN='Y' and trn.STATUS='H' and trn.IS_DEPUTATION='Y' and mstemp.PRAN_NO is not null and PRAN_ACTIVE=1  "
	 * ); sb2.append(
	 * " and mstemp.DDO_CODE is null and mstemp.DEPT_DDO_CODE is not null and mstemp.DCPS_OR_GPF='Y' "
	 * ); sb2.append(
	 * " group by mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, reg.ddo_reg_no) abc "
	 * ); sb2.append(
	 * " left outer join (    SELECT    sd.SD_PRAN_NO,    cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt ,    bh.YEAR,    bh.MONTH    FROM NSDL_SD_DTLS sd    inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME "
	 * ); sb2.append(" and bh.STATUS <>-1    and bh.YEAR=" + year +
	 * " and bh.MONTH=" + month + " and "); sb2.append(" bh.file_name like '%" +
	 * treasuryyno +
	 * "%D'    group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ) a on a.sd_pran_no=abc.pran_no "
	 * ); sb2.append(
	 * " where  cast(abc.c-nvl(a.sd_amnt,0) as double) >0 order by abc.DDO_REG_NO "
	 * );
	 * 
	 * this.logger.error("   ---------" + sb.toString()); SQLQuery lQuery2 =
	 * this.ghibSession.createSQLQuery(sb2.toString());
	 * 
	 * lLstReturnList2 = lQuery2.list(); if (((empLst != null) && (empLst.size()
	 * > 0)) || ((lLstReturnList1 != null) && (lLstReturnList1.size() > 0)) || (
	 * (lLstReturnList2 != null) && (lLstReturnList2.size() > 0))) { finalList =
	 * new ArrayList(); if ((lLstReturnList1 != null) && (lLstReturnList1.size()
	 * > 0)) { finalList.addAll(lLstReturnList1); } } if ((empLst != null) &&
	 * (empLst.size() > 0)) { empLstNew = new ArrayList(); for (int i = 0; i <
	 * empLst.size(); i++) { Object[] objTest1 = (Object[])empLst.get(i); if
	 * ((objTest1 != null) && (objTest1[5].equals(trDdoReg))) {
	 * finalList.add(objTest1); } else { empLstNew.add(objTest1); } } } if
	 * ((lLstReturnList2 != null) && (lLstReturnList2.size() > 0)) {
	 * lLstReturnList2New = new ArrayList(); for (int i = 0; i <
	 * lLstReturnList2.size(); i++) { Object[] objTest1 =
	 * (Object[])lLstReturnList2.get(i); if ((objTest1 != null) &&
	 * (objTest1[5].equals(trDdoReg))) { finalList.add(objTest1); } else {
	 * lLstReturnList2New.add(objTest1); } } } boolean ifListAdded = false; if
	 * ((empLstNew != null) && (empLstNew.size() > 0) && ((lLstReturnList2New ==
	 * null) || (lLstReturnList2New.size() == 0))) {
	 * finalList.addAll(empLstNew); } if ((lLstReturnList2New != null) &&
	 * (lLstReturnList2New.size() > 0) && ((empLstNew == null) ||
	 * (empLstNew.size() == 0))) { finalList.addAll(lLstReturnList2New);
	 * ifListAdded = true; } HashMap keyMap = new HashMap(); if ((empLstNew !=
	 * null) && (lLstReturnList2New != null) && (empLstNew.size() > 0) &&
	 * (lLstReturnList2New.size() > 0)) { for (int i = 0; i < empLstNew.size();
	 * i++) { int size = lLstReturnList2New.size(); Object[] objTest1 =
	 * (Object[])empLstNew.get(i); for (int j = 0; j < size; j++) { if
	 * ((keyMap.size() == 0) || ((keyMap.size() > 0) &&
	 * (!keyMap.containsKey(Integer.valueOf(j))))) { Object[] objTest2 =
	 * (Object[])lLstReturnList2New.get(j); if ((objTest1 != null) && (objTest2
	 * != null) && (objTest1[5].equals(objTest2[5]))) { finalList.add(objTest2);
	 * keyMap.put(Integer.valueOf(j), objTest2); } } } finalList.add(objTest1);
	 * } } if ((lLstReturnList2New != null) && (lLstReturnList2New.size() > 0)
	 * && (!ifListAdded)) { for (int y = 0; y < lLstReturnList2New.size(); y++)
	 * { if ((keyMap.size() == 0) || ((keyMap.size() > 0) &&
	 * (!keyMap.containsKey(Integer.valueOf(y))))) {
	 * finalList.add(lLstReturnList2New.get(y)); } } } } catch (Exception e) {
	 * this.logger.info("Error occer in  getEmployeeList ---------" + e); }
	 * return finalList; }
	 
*/
	// cast(abc.d-nvl(a.sd_emplr_amnt,0) as double)
	// cast(sum(nvl(dept.CONTRIBUTION_EMPLR,0) ) as double) as d
	// inner join TRN_DCPS_DEPUTATION_CONTRIBUTION dept on
	// dept.DCPS_EMP_ID=trn.DCPS_EMP_ID
	// cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt

	public List getEmployeeListNsdlForDeputation(String yrCode, String month,
			String treasuryyno, String year, String treasuryDDOCode,
			String trDdoReg) {// $t back up
		List empLst = null;
		List lLstReturnList1 = null;
		List lLstReturnList2 = null;
		List finalList = null;
		List empLstNew = null;
		List lLstReturnList2New = null;
		List lLstReturnList2New2 = null;

		StringBuilder sb = new StringBuilder();
		try {
			sb.append(" SELECT abc.EMP_NAME, abc.DCPS_ID, abc.PRAN_NO , cast(abc.c-nvl(a.sd_amnt,0) as double), cast(abc.d-nvl(a.sd_emplr_amnt,0) as double), abc.ddo_reg_no , a.sd_amnt from ");
			sb.append(" (select mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as c, cast(sum(nvl(trn.CONTRIBUTION_EMPLR,0) ) as double) as d, reg.ddo_reg_no ");
			sb.append(" FROM MST_DCPS_EMP mstemp inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=mstemp.DCPS_EMP_ID  inner join ORG_DDO_MST ddo on ddo.DDO_CODE=case when substr(mstemp.DDO_CODE,1,2)=substr('"+treasuryyno+"',1,2) then mstemp.DDO_CODE else '"+treasuryDDOCode+"' end inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code ");////$t 24-5-2021 DDO-DTO mapping
			sb.append(" where trn.FIN_YEAR_ID="+ yrCode+ " and trn.MONTH_ID="+ month+"  and substr(trn.TREASURY_CODE,1,2)='"+ treasuryyno.substring(0, 2)+ "' " ////$t 8-4-2021 deputation KR one file
					+ "and (trn.IS_CHALLAN='Y' or trn.IS_ARREARS='Y') and trn.STATUS='H' and trn.IS_DEPUTATION='Y' and mstemp.PRAN_NO is not null and PRAN_ACTIVE=1  ");
			sb.append(" and mstemp.DDO_CODE is not null and mstemp.DEPT_DDO_CODE is null and mstemp.DCPS_OR_GPF='Y' ");
			sb.append(" group by mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, reg.ddo_reg_no) abc ");
			sb.append(" left outer join (    SELECT    sd.SD_PRAN_NO,    cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt ,cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt,    bh.YEAR,    bh.MONTH    FROM NSDL_SD_DTLS sd    inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME ");
			sb.append(" and bh.STATUS <>-1    and bh.YEAR=" + year+ " and bh.MONTH=" + month + " and ");
			sb.append(" bh.file_name like '"+ treasuryyno.substring(0, 2)+ "%D' group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ) a on a.sd_pran_no=abc.pran_no ");////$t 8-4-2021 deputation KR one file
			sb.append(" where  cast(abc.c-nvl(a.sd_amnt,0) as double) >=0 and cast(abc.d-nvl(a.sd_emplr_amnt,0) as double) >0 order by abc.DDO_REG_NO ");

			this.logger.error(" here1  ---------" + sb.toString());
			SQLQuery lQuery = this.ghibSession.createSQLQuery(sb.toString());

			empLst = lQuery.list();

			StringBuilder sb1 = new StringBuilder();
			sb1.append(" SELECT abc.EMP_NAME, abc.DCPS_ID, abc.PRAN_NO, cast(abc.c-nvl(a.sd_amnt,0) as double), cast(abc.d-nvl(a.sd_emplr_amnt,0) as double), abc.ddo_reg_no , a.sd_amnt from ");
			sb1.append(" (select mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as c, cast(sum(nvl(trn.CONTRIBUTION_EMPLR,0) ) as double) as d, reg.ddo_reg_no ");
			sb1.append(" FROM MST_DCPS_EMP mstemp inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=mstemp.DCPS_EMP_ID  inner join ORG_DDO_MST ddo on ddo.DDO_CODE='"
					+ treasuryDDOCode+ "' inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code ");
			sb1.append(" where trn.FIN_YEAR_ID="+ yrCode+ " and trn.MONTH_ID="+ month+"  and substr(trn.TREASURY_CODE,1,2)='"+ treasuryyno.substring(0, 2)+ "' " ////$t 8-4-2021 deputation KR one file
					+ "and (trn.IS_CHALLAN='Y' or trn.IS_ARREARS='Y') and trn.STATUS='H' and trn.IS_DEPUTATION='Y' and mstemp.PRAN_NO is not null and PRAN_ACTIVE=1  ");
			sb1.append(" and mstemp.DDO_CODE is null and mstemp.DEPT_DDO_CODE is null and mstemp.DCPS_OR_GPF='Y' ");
			sb1.append(" group by mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, reg.ddo_reg_no) abc ");
			sb1.append(" left outer join (    SELECT    sd.SD_PRAN_NO,    cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt ,cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt,    bh.YEAR,    bh.MONTH    FROM NSDL_SD_DTLS sd    inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME ");
			sb1.append(" and bh.STATUS <>-1    and bh.YEAR=" + year+ " and bh.MONTH=" + month + " and ");
			sb1.append(" bh.file_name like '"+ treasuryyno.substring(0, 2)+ "%D'    group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ) a on a.sd_pran_no=abc.pran_no ");////$t 8-4-2021 deputation KR one file
			sb1.append(" where  cast(abc.c-nvl(a.sd_amnt,0) as double) >=0 and cast(abc.d-nvl(a.sd_emplr_amnt,0) as double) >0 order by abc.DDO_REG_NO ");

			this.logger.error("   ---------" + sb.toString());
			SQLQuery lQuery1 = this.ghibSession.createSQLQuery(sb1.toString());

			lLstReturnList1 = lQuery1.list();

			StringBuilder sb2 = new StringBuilder();
			sb2.append(" SELECT abc.EMP_NAME, abc.DCPS_ID, abc.PRAN_NO, cast(abc.c-nvl(a.sd_amnt,0) as double), cast(abc.d-nvl(a.sd_emplr_amnt,0) as double), abc.ddo_reg_no , a.sd_amnt from ");
			sb2.append(" (select mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as c, cast(sum(nvl(trn.CONTRIBUTION_EMPLR,0) ) as double) as d, reg.ddo_reg_no ");
			sb2.append(" FROM MST_DCPS_EMP mstemp inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=mstemp.DCPS_EMP_ID  inner join ORG_DDO_MST ddo on ddo.DDO_CODE= case when substr(mstemp.DEPT_DDO_CODE,1,2)=substr('"+treasuryyno+"',1,2) then mstemp.DEPT_DDO_CODE else '"+treasuryDDOCode+"' end inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code ");////$t 24/5/2021 DDO-DTO mapping
			sb2.append(" where trn.FIN_YEAR_ID="+ yrCode+ " and trn.MONTH_ID="+ month+"  and substr(trn.TREASURY_CODE,1,2)='"+ treasuryyno.substring(0, 2)+ "' " ////$t 8-4-2021 deputation KR one file
					+ "and (trn.IS_CHALLAN='Y' or trn.IS_ARREARS='Y') and trn.STATUS='H' and trn.IS_DEPUTATION='Y' and mstemp.PRAN_NO is not null and PRAN_ACTIVE=1  ");
			sb2.append(" and mstemp.DDO_CODE is null and mstemp.DEPT_DDO_CODE is not null and mstemp.DCPS_OR_GPF='Y' ");
			sb2.append(" group by mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, reg.ddo_reg_no) abc ");
			sb2.append(" left outer join (    SELECT    sd.SD_PRAN_NO,    cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt , cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt,   bh.YEAR,    bh.MONTH    FROM NSDL_SD_DTLS sd    inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME ");
			sb2.append(" and bh.STATUS <>-1    and bh.YEAR=" + year+ " and bh.MONTH=" + month + " and ");
			sb2.append(" bh.file_name like '"+ treasuryyno.substring(0, 2)+ "%D'    group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ) a on a.sd_pran_no=abc.pran_no "); ////$t 8-4-2021 deputation KR one file
			sb2.append(" where  cast(abc.c-nvl(a.sd_amnt,0) as double) >=0 and cast(abc.d-nvl(a.sd_emplr_amnt,0) as double) >0 order by abc.DDO_REG_NO ");

			this.logger.error("   ---------" + sb.toString());
			SQLQuery lQuery2 = this.ghibSession.createSQLQuery(sb2.toString());

			lLstReturnList2 = lQuery2.list();
			if (((empLst != null) && (empLst.size() > 0))
					|| ((lLstReturnList1 != null) && (lLstReturnList1.size() > 0))
					|| ((lLstReturnList2 != null) && (lLstReturnList2.size() > 0))) {
				finalList = new ArrayList();
				if ((lLstReturnList1 != null) && (lLstReturnList1.size() > 0)) {
					finalList.addAll(lLstReturnList1);
				}
			}
			if ((empLst != null) && (empLst.size() > 0)) {
				empLstNew = new ArrayList();
				for (int i = 0; i < empLst.size(); i++) {
					Object[] objTest1 = (Object[]) empLst.get(i);
					if ((objTest1 != null) && (objTest1[5].equals(trDdoReg))) {
						finalList.add(objTest1);
					} else {
						empLstNew.add(objTest1);
					}
				}
			}
			if ((lLstReturnList2 != null) && (lLstReturnList2.size() > 0)) {
				lLstReturnList2New = new ArrayList();
				for (int i = 0; i < lLstReturnList2.size(); i++) {
					Object[] objTest1 = (Object[]) lLstReturnList2.get(i);
					if ((objTest1 != null) && (objTest1[5].equals(trDdoReg))) {
						finalList.add(objTest1);
					} else {
						lLstReturnList2New.add(objTest1);
					}
				}
			}
			boolean ifListAdded = false;
			if ((empLstNew != null)
					&& (empLstNew.size() > 0)
					&& ((lLstReturnList2New == null) || (lLstReturnList2New
							.size() == 0))) {
				finalList.addAll(empLstNew);
			}
			if ((lLstReturnList2New != null) && (lLstReturnList2New.size() > 0)
					&& ((empLstNew == null) || (empLstNew.size() == 0))) {
				finalList.addAll(lLstReturnList2New);
				ifListAdded = true;
			}
			HashMap keyMap = new HashMap();
			if ((empLstNew != null) && (lLstReturnList2New != null)
					&& (empLstNew.size() > 0)
					&& (lLstReturnList2New.size() > 0)) {
				for (int i = 0; i < empLstNew.size(); i++) {
					int size = lLstReturnList2New.size();
					Object[] objTest1 = (Object[]) empLstNew.get(i);
					for (int j = 0; j < size; j++) {
						if ((keyMap.size() == 0)
								|| ((keyMap.size() > 0) && (!keyMap
										.containsKey(Integer.valueOf(j))))) {
							Object[] objTest2 = (Object[]) lLstReturnList2New
									.get(j);
							if ((objTest1 != null) && (objTest2 != null)
									&& (objTest1[5].equals(objTest2[5]))) {
								finalList.add(objTest2);
								keyMap.put(Integer.valueOf(j), objTest2);
							}
						}
					}
					finalList.add(objTest1);
				}
			}
			if ((lLstReturnList2New != null) && (lLstReturnList2New.size() > 0)
					&& (!ifListAdded)) {
				for (int y = 0; y < lLstReturnList2New.size(); y++) {
					if ((keyMap.size() == 0)
							|| ((keyMap.size() > 0) && (!keyMap
									.containsKey(Integer.valueOf(y))))) {
						finalList.add(lLstReturnList2New.get(y));
					}
				}
			}
			// this.//ghibSession.disconnect();
		} catch (Exception e) {
			this.logger.info("Error occer in  getEmployeeList ---------" + e);
		}
		return finalList;
	}

	public String getEmployeeContribType(String month, String pranNo,
			String year) {
		List empLst = null;
		String flag = "C";

		StringBuilder sb = new StringBuilder();
		try {
			sb.append(" SELECT    sd.SD_PRAN_NO,  bh.YEAR,    bh.MONTH    FROM NSDL_SD_DTLS sd    inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME ");
			sb.append(" and bh.STATUS <>-1    and bh.YEAR=" + year
					+ " and bh.MONTH=" + month + " and ");
			sb.append(" sd.SD_PRAN_NO = '" + pranNo + "'   ");

			this.logger.error("   ---------" + sb.toString());
			SQLQuery lQuery = this.ghibSession.createSQLQuery(sb.toString());

			empLst = lQuery.list();
			if ((empLst != null) && (empLst.size() > 0)) {
				flag = "A";
			}
			// this.//ghibSession.disconnect();
		} catch (Exception e) {
			this.logger.info("Error occer in  getEmployeeList ---------" + e);
		}
		return flag;
	}

	public Long getDDoRegCount(String yrCode, String month, String treasuryyno,
			int flag) {
		List temp = null;
		Long regCount = null;
	////$t 2020 17-1 year 2020 n month jan added
		if ((yrCode.equals("2019") && (month.equals("10")|| month.equals("11") || month.equals("9")||month.equals("12")))
				||yrCode.equals("2020")
				//||(yrCode.equals("2021") && (month.equals("1") || month.equals("2")))) {
				||(yrCode.equals("2021"))) {////$tC

			List temp1 = null;
			List temp2 = null;
			Long regCount1 = null;
			Long regCount2 = null;

			StringBuilder Strbld1 = new StringBuilder();
			Strbld1.append("select  count( distinct a.LOC_ID)  from ");
			Strbld1.append("\t(SELECT head.LOC_ID,ddo.DDO_CODE,mstemp.PRAN_NO,sum(paybill.GROSS_AMT) as GROSS_AMT ,sum(paybill.NET_TOTAL) as NET_TOTAL ,sum(paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA+paybill.DCPS_PAY_DIFF) as employye,  ");////PayArrearDiff $t 23-2-2021
			Strbld1.append("sum(paybill.DED_ADJUST) as DED_ADJUST ,reg.ddo_reg_no ");
			Strbld1.append("FROM PAYBILL_HEAD_MPG head   ");
			Strbld1.append("inner join NSDL_PAYBILL_DATA paybill on paybill.PAYBILL_GRP_ID =head.PAYBILL_ID ");
			Strbld1.append("and paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA+paybill.DCPS_PAY_DIFF >paybill.DED_ADJUST ");////PayArrearDiff $t 23-2-2021
			Strbld1.append("inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID inner join HR_EIS_EMP_MST hreis on hreis.EMP_ID=paybill.EMP_ID  ");
			Strbld1.append("inner join MST_DCPS_EMP mstemp on mstemp.ORG_EMP_MST_ID=hreis.EMP_MPG_ID ");
			Strbld1.append("inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
			Strbld1.append("inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2) ");
			Strbld1.append("inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  ");
			//if(treasuryyno.substring(0, 2).equals("12")||treasuryyno.substring(0, 2).equals("46")||treasuryyno.substring(0, 2).equals("23")||treasuryyno.substring(0, 2).equals("51")){////$t KR 5-1-2020
			Strbld1.append(" where head.PAYBILL_YEAR='" + yrCode
					+ "' and head.PAYBILL_MONTH='" + month
					+ "' and substr(ddo.ddo_code,1,2)='" + treasuryyno.substring(0, 2)
					+ "' and head.APPROVE_FLAG=1 ");////$t KR 18-12-2020
			/*}else{
			Strbld1.append(" where head.PAYBILL_YEAR='" + yrCode
					+ "' and head.PAYBILL_MONTH='" + month
					+ "' and substr(ddo.ddo_code,1,4)='" + treasuryyno
					+ "' and head.APPROVE_FLAG=1 ");
			}*/
			Strbld1.append(" and mstemp.PRAN_NO is not null and mstemp.PRAN_ACTIVE=1 and mstemp.DCPS_OR_GPF='Y' and mstemp.REG_STATUS=1 group by ddo.DDO_CODE,mstemp.PRAN_NO,reg.ddo_reg_no ,head.LOC_ID  ) a left outer join ");
			Strbld1.append(" (SELECT sd.SD_PRAN_NO,cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt,cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt ,bh.YEAR,bh.MONTH,sd.ddo_reg_no FROM NSDL_SD_DTLS sd  ");
			Strbld1.append(" inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.STATUS <>-1 ");
			//if(treasuryyno.substring(0, 2).equals("12")||treasuryyno.substring(0, 2).equals("46")||treasuryyno.substring(0, 2).equals("23")||treasuryyno.substring(0, 2).equals("51")){////$t KR 5-1-2020
			Strbld1.append("\tand bh.YEAR=" + yrCode + " and bh.MONTH=" + month+ 
					" and bh.file_name like '" + treasuryyno.substring(0, 2) + "%' and bh.file_name not like '%D' and sd.SD_REMARK like 'Contribution%'");////$t KR //+ " and bh.file_name like '" + treasuryyno + "%'  "); /////$t  25-3-2021
			/*}else{
			Strbld1.append("\tand bh.YEAR=" + yrCode + " and bh.MONTH=" + month+ 
					" and bh.file_name like '" + treasuryyno + "%'  ");	
			}*/
			Strbld1.append(" group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH,sd.ddo_reg_no ) b on b.SD_PRAN_NO=a.PRAN_NO and b.ddo_reg_no=a.ddo_reg_no where a.employye-cast(nvl(b.sd_amnt,0) as double) >0 and a.DED_ADJUST-cast(nvl(b.sd_emplr_amnt,0) as double) >0 ");
           //$t 2019 4/11
			SQLQuery lQuery1 = this.ghibSession.createSQLQuery(Strbld1
					.toString());

			temp1 = lQuery1.list();
			this.logger.info("temp1 size" + temp1.size());

			StringBuilder Strbld2 = new StringBuilder();
			Strbld2.append("select  count( distinct a.LOC_ID)  from ");
			Strbld2.append("\t(SELECT head.LOC_ID,ddo.DDO_CODE,mstemp.PRAN_NO,sum(paybill.GROSS_AMT) as GROSS_AMT ,sum(paybill.NET_TOTAL) as NET_TOTAL ,'0' as employye,  ");
			Strbld2.append("sum(nvl(paybill.emplr_contri_arrears,0))+SUM(nvl(paybill.NPS_EMPLR_DIFFERENCE_ADJ,0)) as DED_ADJUST  ,reg.ddo_reg_no ");////$tC
			Strbld2.append("FROM PAYBILL_HEAD_MPG head   ");
			Strbld2.append("inner join NSDL_PAYBILL_DATA paybill on paybill.PAYBILL_GRP_ID =head.PAYBILL_ID ");
			// Strbld2.append("and paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA <=paybill.DED_ADJUST ");
			Strbld2.append("inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID inner join HR_EIS_EMP_MST hreis on hreis.EMP_ID=paybill.EMP_ID  ");
			Strbld2.append("inner join MST_DCPS_EMP mstemp on mstemp.ORG_EMP_MST_ID=hreis.EMP_MPG_ID ");
			Strbld2.append("inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
			Strbld2.append("inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2) ");
			Strbld2.append("inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  ");
			//if(treasuryyno.substring(0, 2).equals("12")||treasuryyno.substring(0, 2).equals("46")||treasuryyno.substring(0, 2).equals("23")||treasuryyno.substring(0, 2).equals("51")){////$t KR 5-1-2020
			Strbld2.append(" where head.PAYBILL_YEAR='" + yrCode
					+ "' and head.PAYBILL_MONTH='" + month
					+ "' and substr(ddo.ddo_code,1,2)='" + treasuryyno.substring(0,2)
					+ "' and head.APPROVE_FLAG=1 ");////$t KR 18-12-2020
			/*}else{
			Strbld2.append(" where head.PAYBILL_YEAR='" + yrCode
					+ "' and head.PAYBILL_MONTH='" + month
					+ "' and substr(ddo.ddo_code,1,4)='" + treasuryyno
					+ "' and head.APPROVE_FLAG=1 ");	
			}*/
			Strbld2.append(" and mstemp.PRAN_NO is not null and mstemp.PRAN_ACTIVE=1 and mstemp.DCPS_OR_GPF='Y' and mstemp.REG_STATUS=1 and (cast((nvl(paybill.emplr_contri_arrears,0)) as double) <> 0  or cast((nvl(paybill.NPS_EMPLR_DIFFERENCE_ADJ,0)) as double) <> 0) group by ddo.DDO_CODE,mstemp.PRAN_NO,reg.ddo_reg_no ,head.LOC_ID  ) a left outer join ");////$tC
			Strbld2.append(" (SELECT sd.SD_PRAN_NO,cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt,cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt ,bh.YEAR,bh.MONTH,sd.ddo_reg_no FROM NSDL_SD_DTLS sd  ");
			Strbld2.append(" inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.STATUS <>-1 ");
			//if(treasuryyno.substring(0, 2).equals("12")||treasuryyno.substring(0, 2).equals("46")||treasuryyno.substring(0, 2).equals("23")||treasuryyno.substring(0, 2).equals("51")){////$t KR 5-1-2020
			Strbld2.append("\tand bh.YEAR=" + yrCode + " and bh.MONTH=" + month+ 
					" and bh.file_name like '" + treasuryyno.substring(0,2) + "%' and sd.SD_REMARK like '8 Per Contribution%' ");////$t KR //+ " and bh.file_name like '" + treasuryyno + "%'  ");
			/*}else{
			Strbld2.append("\tand bh.YEAR=" + yrCode + " and bh.MONTH=" + month+
					" and bh.file_name like '" + treasuryyno + "%'  ");	
			}*/
			Strbld2.append(" group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH,sd.ddo_reg_no ) b on b.SD_PRAN_NO=a.PRAN_NO and b.ddo_reg_no=a.ddo_reg_no where a.employye-cast(nvl(b.sd_amnt,0) as double) >0 and a.DED_ADJUST-cast(nvl(b.sd_emplr_amnt,0) as double) >0 ");

			SQLQuery lQuery2 = this.ghibSession.createSQLQuery(Strbld2
					.toString());

			temp2 = lQuery2.list();
			this.logger.info("temp2 size" + temp2.size());

			if ((temp1 != null) && (temp1.size() > 0)) {
				regCount1 = Long.valueOf(Long
						.parseLong(temp1.get(0).toString()));
			}
			if ((temp2 != null) && (temp2.size() > 0)) {
				regCount2 = Long.valueOf(Long
						.parseLong(temp2.get(0).toString()));
			}

			regCount = regCount1 + regCount2;

		} else {
			StringBuilder Strbld = new StringBuilder();
			Strbld.append("select  count( distinct a.LOC_ID)  from ");
			Strbld.append("\t(SELECT head.LOC_ID,ddo.DDO_CODE,mstemp.PRAN_NO,sum(paybill.GROSS_AMT) as GROSS_AMT ,sum(paybill.NET_TOTAL) as NET_TOTAL ,sum(paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA+paybill.DCPS_PAY_DIFF) as employye,  ");////PayArrearDiff $t 23-2-2021
			Strbld.append("sum(paybill.DED_ADJUST) as DED_ADJUST ,reg.ddo_reg_no ");
			Strbld.append("FROM PAYBILL_HEAD_MPG head   ");
			Strbld.append("inner join NSDL_PAYBILL_DATA paybill on paybill.PAYBILL_GRP_ID =head.PAYBILL_ID ");
			Strbld.append("and paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA+paybill.DCPS_PAY_DIFF <=paybill.DED_ADJUST ");////PayArrearDiff $t 23-2-2021
			Strbld.append("inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID inner join HR_EIS_EMP_MST hreis on hreis.EMP_ID=paybill.EMP_ID  ");
			Strbld.append("inner join MST_DCPS_EMP mstemp on mstemp.ORG_EMP_MST_ID=hreis.EMP_MPG_ID ");
			Strbld.append("inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
			Strbld.append("inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2) ");
			Strbld.append("inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  ");
			//if(treasuryyno.substring(0, 2).equals("12")||treasuryyno.substring(0, 2).equals("46")||treasuryyno.substring(0, 2).equals("23")||treasuryyno.substring(0, 2).equals("51")){////$t KR 5-1-2020
			Strbld.append(" where head.PAYBILL_YEAR='" + yrCode
					+ "' and head.PAYBILL_MONTH='" + month
					+ "' and substr(ddo.ddo_code,1,2)='" + treasuryyno.substring(0,2)
					+ "' and head.APPROVE_FLAG=1 ");////$t KR 18-12-2020
			/*}else{
			Strbld.append(" where head.PAYBILL_YEAR='" + yrCode
					+ "' and head.PAYBILL_MONTH='" + month
					+ "' and substr(ddo.ddo_code,1,4)='" + treasuryyno
					+ "' and head.APPROVE_FLAG=1 ");	
			}*/
			Strbld.append(" and mstemp.PRAN_NO is not null and mstemp.PRAN_ACTIVE=1 and mstemp.DCPS_OR_GPF='Y' and mstemp.REG_STATUS=1 group by ddo.DDO_CODE,mstemp.PRAN_NO,reg.ddo_reg_no ,head.LOC_ID  ) a left outer join ");
			Strbld.append(" (SELECT sd.SD_PRAN_NO,cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt,cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt ,bh.YEAR,bh.MONTH,sd.ddo_reg_no FROM NSDL_SD_DTLS sd  ");
			Strbld.append(" inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.STATUS <>-1 ");
			//if(treasuryyno.substring(0, 2).equals("12")||treasuryyno.substring(0, 2).equals("46")||treasuryyno.substring(0, 2).equals("23")||treasuryyno.substring(0, 2).equals("51")){////$t KR 5-1-2020
			Strbld.append("\tand bh.YEAR=" + yrCode + " and bh.MONTH=" + month
					+ " and bh.file_name like '" + treasuryyno.substring(0, 2) + "%' and bh.file_name not like '%D' and sd.SD_REMARK like 'Contribution for%' and cast(sd.SD_EMP_AMOUNT as bigint) <= cast(sd.SD_EMPlr_AMOUNT as bigint) ");////$t KR /////$t  25-3-2021
			/*}else{
			Strbld.append("\tand bh.YEAR=" + yrCode + " and bh.MONTH=" + month
					+ " and bh.file_name like '" + treasuryyno + "%'  ");////$t KR	
			}*/
			Strbld.append(" group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH,sd.ddo_reg_no ) b on b.SD_PRAN_NO=a.PRAN_NO and b.ddo_reg_no=a.ddo_reg_no where a.employye-cast(nvl(b.sd_amnt,0) as double) >0 and a.DED_ADJUST-cast(nvl(b.sd_emplr_amnt,0) as double) >0 ");

			SQLQuery lQuery = this.ghibSession
					.createSQLQuery(Strbld.toString());

			temp = lQuery.list();
			this.logger.info("temp size" + temp.size());
			if ((temp != null) && (temp.size() > 0)) {
				regCount = Long.valueOf(Long.parseLong(temp.get(0).toString()));
			}
		}
		// this.//ghibSession.disconnect();
		return regCount;
	}

	public Long getDDoRegCountDeputation(String yrCode, String month,
			String treasuryyno, String year, String treasuryDDOCode) {
		List empLst = null;
		List lLstReturnList1 = null;
		List lLstReturnList2 = null;

		Long regCount = Long.valueOf(0L);
		int regCount1 = 0;
		int regCount2 = 0;
		int regCount3 = 0;

		StringBuilder sb = new StringBuilder();
		try {
			sb.append("  select distinct (abc.ddo_reg_no)   from ");
			sb.append(" (select mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as c, cast(sum(nvl(trn.CONTRIBUTION_EMPLR,0) ) as double) as d, reg.ddo_reg_no ");
			sb.append(" FROM MST_DCPS_EMP mstemp inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=mstemp.DCPS_EMP_ID  inner join ORG_DDO_MST ddo on ddo.DDO_CODE=case when substr(mstemp.DDO_CODE,1,2)=substr('"+treasuryyno+"',1,2) then mstemp.DDO_CODE else '"+treasuryDDOCode+"' end inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code ");////$t 24/5/2021 DDO-DTO mapping
			sb.append(" where trn.FIN_YEAR_ID="+ yrCode+ " and trn.MONTH_ID="+ month+"  and substr(trn.TREASURY_CODE,1,2)='"+ treasuryyno.substring(0, 2)+ "' " ////$t 8-4-2021 deputation KR one file
					+ "and (trn.IS_CHALLAN='Y' or trn.IS_ARREARS='Y')  and trn.STATUS='H' and trn.IS_DEPUTATION='Y' and mstemp.PRAN_NO is not null and PRAN_ACTIVE=1  ");
			sb.append(" and mstemp.DDO_CODE is not null and mstemp.DEPT_DDO_CODE is null and mstemp.DCPS_OR_GPF='Y' ");
			sb.append(" group by mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, reg.ddo_reg_no) abc ");
			sb.append(" left outer join (    SELECT    sd.SD_PRAN_NO,    cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt , cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt,   bh.YEAR,    bh.MONTH    FROM NSDL_SD_DTLS sd    inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME ");
			sb.append(" and bh.STATUS <>-1    and bh.YEAR=" + year+ " and bh.MONTH=" + month + " and ");
			sb.append(" bh.file_name like '"+ treasuryyno.substring(0, 2)+ "%D'    group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ) a on a.sd_pran_no=abc.pran_no ");////$t 8-4-2021 deputation KR one file
			sb.append(" where  cast(abc.c-nvl(a.sd_amnt,0) as double) >=0 and cast(abc.d-nvl(a.sd_emplr_amnt,0) as double) >0 ");

			this.logger.error("   ---------" + sb.toString());
			SQLQuery lQuery = this.ghibSession.createSQLQuery(sb.toString());

			empLst = lQuery.list();

			StringBuilder sb1 = new StringBuilder();
			sb1.append("  select distinct (abc.ddo_reg_no)   from ");
			sb1.append(" (select mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as c, cast(sum(nvl(trn.CONTRIBUTION_EMPLR,0) ) as double) as d, reg.ddo_reg_no ");
			sb1.append(" FROM MST_DCPS_EMP mstemp inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=mstemp.DCPS_EMP_ID  inner join ORG_DDO_MST ddo on ddo.DDO_CODE='"+ treasuryDDOCode+ "' inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code ");
			sb1.append(" where trn.FIN_YEAR_ID="+ yrCode+ " and trn.MONTH_ID="+ month+"  and substr(trn.TREASURY_CODE,1,2)='"+ treasuryyno.substring(0, 2)+ "' "
					+ "and (trn.IS_CHALLAN='Y' or trn.IS_ARREARS='Y') and trn.STATUS='H' and trn.IS_DEPUTATION='Y' and mstemp.PRAN_NO is not null and PRAN_ACTIVE=1  ");
			sb1.append(" and mstemp.DDO_CODE is null and mstemp.DEPT_DDO_CODE is null and mstemp.DCPS_OR_GPF='Y' ");
			sb1.append(" group by mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, reg.ddo_reg_no) abc ");
			sb1.append(" left outer join (    SELECT    sd.SD_PRAN_NO,    cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt ,cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt,    bh.YEAR,    bh.MONTH    FROM NSDL_SD_DTLS sd    inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME ");
			sb1.append(" and bh.STATUS <>-1    and bh.YEAR=" + year+ " and bh.MONTH=" + month + " and ");
			sb1.append(" bh.file_name like '"+ treasuryyno.substring(0, 2)+ "%D'    group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ) a on a.sd_pran_no=abc.pran_no ");////$t 8-4-2021 deputation KR one file
			sb1.append(" where  cast(abc.c-nvl(a.sd_amnt,0) as double) >=0 and cast(abc.d-nvl(a.sd_emplr_amnt,0) as double) >0  ");

			this.logger.error("   ---------" + sb.toString());
			SQLQuery lQuery1 = this.ghibSession.createSQLQuery(sb1.toString());

			lLstReturnList1 = lQuery1.list();

			StringBuilder sb2 = new StringBuilder();
			sb2.append("  select distinct (abc.ddo_reg_no)   from ");
			sb2.append(" (select mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as c, cast(sum(nvl(trn.CONTRIBUTION_EMPLR,0) ) as double) as d, reg.ddo_reg_no ");
			sb2.append(" FROM MST_DCPS_EMP mstemp inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=mstemp.DCPS_EMP_ID  inner join ORG_DDO_MST ddo on ddo.DDO_CODE= case when substr(mstemp.DEPT_DDO_CODE,1,2)=substr('"+treasuryyno+"',1,2) then mstemp.DEPT_DDO_CODE else '"+treasuryDDOCode+"' end inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code ");////$t 24/5/2021 DDO-DTO mapping
			sb2.append(" where trn.FIN_YEAR_ID="+ yrCode+ " and trn.MONTH_ID="+ month+"  and substr(trn.TREASURY_CODE,1,2)='"+ treasuryyno.substring(0, 2)+ "' " ////$t 8-4-2021 deputation KR one file
					+ "and (trn.IS_CHALLAN='Y' or trn.IS_ARREARS='Y')  and trn.STATUS='H' and trn.IS_DEPUTATION='Y' and mstemp.PRAN_NO is not null and PRAN_ACTIVE=1  ");
			sb2.append(" and mstemp.DDO_CODE is null and mstemp.DEPT_DDO_CODE is not null and mstemp.DCPS_OR_GPF='Y' ");
			sb2.append(" group by mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, reg.ddo_reg_no) abc ");
			sb2.append(" left outer join (    SELECT    sd.SD_PRAN_NO,    cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt ,cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt,    bh.YEAR,    bh.MONTH    FROM NSDL_SD_DTLS sd    inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME ");
			sb2.append(" and bh.STATUS <>-1    and bh.YEAR=" + year+ " and bh.MONTH=" + month + " and ");
			sb2.append(" bh.file_name like '"+ treasuryyno.substring(0, 2)+ "%D'    group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ) a on a.sd_pran_no=abc.pran_no ");////$t 8-4-2021 deputation KR one file
			sb2.append(" where  cast(abc.c-nvl(a.sd_amnt,0) as double) >=0 and cast(abc.d-nvl(a.sd_emplr_amnt,0) as double) >0  ");

			this.logger.error("   ---------" + sb.toString());
			SQLQuery lQuery2 = this.ghibSession.createSQLQuery(sb2.toString());

			lLstReturnList2 = lQuery2.list();

			Set set = new HashSet();
			if ((empLst != null) && (empLst.size() > 0)) {
				regCount1 = empLst.size();
				for (int i = 0; i < regCount1; i++) {
					set.add(empLst.get(i).toString());
				}
			}
			if ((lLstReturnList1 != null) && (lLstReturnList1.size() > 0)) {
				regCount2 = lLstReturnList1.size();
				for (int i = 0; i < regCount2; i++) {
					set.add(lLstReturnList1.get(i).toString());
				}
			}
			if ((lLstReturnList2 != null) && (lLstReturnList2.size() > 0)) {
				regCount3 = lLstReturnList2.size();
				for (int i = 0; i < regCount3; i++) {
					set.add(lLstReturnList2.get(i).toString());
				}
			}
			Integer i = Integer.valueOf(set.size());
			regCount = Long.valueOf(i.longValue());
			// this.//ghibSession.disconnect();
		} catch (Exception e) {
			this.logger.info("Error occer in  getEmployeeList ---------" + e);
		}
		return regCount;
	}
//$t 2019 11-11
	public List getEmployeeContriTotalList(String yrCode, String month,
			String treasuryyno, int flag) {
		
		List temp = null;
	////$t 2020 17-1 year 2020 n month jan added
		if ((yrCode.equals("2019") && (month.equals("10")|| month.equals("11") || month.equals("9")||month.equals("12")))
			||yrCode.equals("2020")
			//||(yrCode.equals("2021") && (month.equals("1") || month.equals("2")))) {
			||(yrCode.equals("2021"))) {////$tC

			List TableData1 = null;
			List TableData2 = null;
		StringBuilder Strbld = new StringBuilder();
		Strbld.append("select cast(sum(a.emp_amt-nvl(b.sd_amnt,0)) as VARCHAR(20)) ||'#'|| cast(sum(a.DED_ADJUST-nvl(b.sd_emplr_amnt,0)) as VARCHAR(20)) from ");
		Strbld.append("(SELECT sum(paybill.DCPS)+sum(paybill.DCPS_PAY)+sum(paybill.DCPS_DELAY)+sum(paybill.DCPS_DA)+sum(paybill.DCPS_PAY_DIFF) as emp_amt,    ");////PayArrearDiff $t 23-2-2021
		Strbld.append("sum(paybill.DED_ADJUST) as DED_ADJUST ,emp.pran_no,reg.ddo_reg_no  FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");
		Strbld.append("inner join NSDL_PAYBILL_DATA paybill on paybill.EMP_ID=eis.EMP_ID ");
		Strbld.append(" inner join PAYBILL_HEAD_MPG head on head.PAYBILL_ID=paybill.PAYBILL_GRP_ID ");
		Strbld.append(" and paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA+paybill.DCPS_PAY_DIFF >paybill.DED_ADJUST  ");////PayArrearDiff $t 23-2-2021
		Strbld.append(" inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID ");
		Strbld.append("inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
		Strbld.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2)  ");
		Strbld.append(" inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  ");
		//if(treasuryyno.substring(0, 2).equals("12")||treasuryyno.substring(0, 2).equals("46")||treasuryyno.substring(0, 2).equals("23")||treasuryyno.substring(0, 2).equals("51")){////$t KR 5-1-2020
		Strbld.append(" where head.PAYBILL_YEAR=" + yrCode
				+ " and head.PAYBILL_MONTH=" + month
				+ "  and substr(ddo.ddo_code,1,2)='" + treasuryyno.substring(0,2) + "' ");////$t KR 18-12-2020
		/*}else{
		Strbld.append(" where head.PAYBILL_YEAR=" + yrCode
				+ " and head.PAYBILL_MONTH=" + month
				+ "  and substr(ddo.ddo_code,1,4)='" + treasuryyno + "' ");	
		}*/
		Strbld.append(" and emp.PRAN_NO is not null and emp.PRAN_ACTIVE=1 and emp.REG_STATUS=1 and head.APPROVE_FLAG=1 group by emp.pran_no,reg.ddo_reg_no ) a left outer join ");
		Strbld.append(" (SELECT sd.SD_PRAN_NO,cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt ,cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt,bh.YEAR,bh.MONTH,sd.ddo_reg_no FROM NSDL_SD_DTLS sd ");
		Strbld.append(" inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.STATUS <>-1 ");
		//if(treasuryyno.substring(0, 2).equals("12")||treasuryyno.substring(0, 2).equals("46")||treasuryyno.substring(0, 2).equals("23")||treasuryyno.substring(0, 2).equals("51")){////$t KR 5-1-2020
		Strbld.append(" and bh.YEAR=" + yrCode + " and bh.MONTH=" + month+ 
				" and bh.file_name like '" + treasuryyno.substring(0,2) + "%' and bh.file_name not like '%D' and sd.SD_REMARK like 'Contribution%'");////$t KR //+ " and bh.file_name like '" + treasuryyno + "%' ");/////$t  25-3-2021
		/*}else{
		Strbld.append(" and bh.YEAR=" + yrCode + " and bh.MONTH=" + month+ 
				" and bh.file_name like '" + treasuryyno + "%' ");
		}*/

		Strbld.append(" group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH,sd.ddo_reg_no ) b ");
		Strbld.append(" on b.SD_PRAN_NO=a.PRAN_NO and b.ddo_reg_no=a.ddo_reg_no  where a.emp_amt-cast(nvl(b.sd_amnt,0) as double) > 0 and a.DED_ADJUST-cast(nvl(b.sd_emplr_amnt,0) as double) >0 ");
		
		this.gLogger.info("If 1 part Query is ***********getEmployeeContriTotalList***"+ Strbld.toString());
		SQLQuery lQuery = this.ghibSession.createSQLQuery(Strbld.toString());
		TableData1 = lQuery.list();
		this.gLogger.info("If 1 part Query is ***********getEmployeeContriTotalList* temp size is **"+ TableData1.size());
		
		StringBuilder Strbld1 = new StringBuilder();
		 Strbld1.append("select cast(sum(a.emp_amt-nvl(b.sd_amnt,0)) as VARCHAR(20)) ||'#'|| cast(sum(a.DED_ADJUST-nvl(b.sd_emplr_amnt,0)) as VARCHAR(20)) from ");
		 Strbld1.append("(SELECT cast('0' as double) as emp_amt, ");
		 Strbld1.append("cast(SUM(nvl(paybill.emplr_contri_arrears,0))as double)+SUM(nvl(paybill.NPS_EMPLR_DIFFERENCE_ADJ,0))  as DED_ADJUST,emp.pran_no,reg.ddo_reg_no  FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");////$tC
		 Strbld1.append("inner join NSDL_PAYBILL_DATA paybill on paybill.EMP_ID=eis.EMP_ID ");
		 Strbld1.append(" inner join PAYBILL_HEAD_MPG head on head.PAYBILL_ID=paybill.PAYBILL_GRP_ID ");
		 Strbld1.append(" inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID ");
		 Strbld1.append("inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
		 Strbld1.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2)  ");
		 Strbld1.append(" inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  ");
		 //if(treasuryyno.substring(0, 2).equals("12")||treasuryyno.substring(0, 2).equals("46")||treasuryyno.substring(0, 2).equals("23")||treasuryyno.substring(0, 2).equals("51")){////$t KR 5-1-2020
		 Strbld1.append(" where head.PAYBILL_YEAR=" + yrCode +
		 " and head.PAYBILL_MONTH=" + month +
		 "  and substr(ddo.ddo_code,1,2)='" + treasuryyno.substring(0,2) + "' ");////$t KR 18-12-2020
		/* }else{
		Strbld1.append(" where head.PAYBILL_YEAR=" + yrCode +
		" and head.PAYBILL_MONTH=" + month +
		"  and substr(ddo.ddo_code,1,4)='" + treasuryyno + "' ");	 
		 }*/
		 Strbld1.append(" and emp.PRAN_NO is not null and emp.PRAN_ACTIVE=1 and emp.REG_STATUS=1 and (cast((nvl(paybill.emplr_contri_arrears,0)) as double) <> 0  or cast((nvl(paybill.NPS_EMPLR_DIFFERENCE_ADJ,0)) as double) <> 0) and head.APPROVE_FLAG=1 group by emp.pran_no,reg.ddo_reg_no ) a left outer join ");////$tC
		 Strbld1.append(" (SELECT sd.SD_PRAN_NO,cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt ,cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt,bh.YEAR,bh.MONTH,sd.ddo_reg_no FROM NSDL_SD_DTLS sd ");
		 Strbld1.append(" inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.STATUS <>-1 ");
		 //if(treasuryyno.substring(0, 2).equals("12")||treasuryyno.substring(0, 2).equals("46")||treasuryyno.substring(0, 2).equals("23")||treasuryyno.substring(0, 2).equals("51")){////$t KR 5-1-2020
		 Strbld1.append(" and bh.YEAR=" +
		 yrCode +
		 " and bh.MONTH=" +
		 month +
		 " and bh.file_name like '" + treasuryyno.substring(0,2) + "%' and sd.SD_REMARK like '8 Per Contribution%' " );////$t KR //" and bh.file_name like '" + treasuryyno + "%' " );
		/* }else{
		Strbld1.append(" and bh.YEAR=" +
		yrCode +
		" and bh.MONTH=" +
		month +
		" and bh.file_name like '" + treasuryyno + "%' " );
		 }*/
		
		 Strbld1.append(" group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH,sd.ddo_reg_no ) b ");
		 Strbld1.append(" on b.SD_PRAN_NO=a.PRAN_NO and b.ddo_reg_no=a.ddo_reg_no  where a.emp_amt-cast(nvl(b.sd_amnt,0) as double) >0 and a.DED_ADJUST-cast(nvl(b.sd_emplr_amnt,0) as double) >0 ");
		

		this.gLogger.info("If 2 part Query is ***********getEmployeeContriTotalList***"+ Strbld1.toString());
		SQLQuery lQuery1 = this.ghibSession.createSQLQuery(Strbld1.toString());
		TableData2 = lQuery1.list();
		this.gLogger.info("If 2 part Query is ***********getEmployeeContriTotalList* temp size is **"+ TableData2.size());
		
		Double EmpleeContriR = null;
		Double EmplrContriR = null;
		Double EmpleeContriA = null;
		Double EmplrContriA = null;
		String totalEmplyContri = null;
		String totalEmplyerContri = null;
		
		/*if ((TableData1 != null) && (TableData1.size() > 0))
        {
	     this.gLogger.info("In Loop  " + TableData1.get(0).toString());
          String[] totalAmtBH = TableData1.get(0).toString().split("#");
          this.gLogger.info("In Loop1  " + TableData1.get(0).toString());
          EmpleeContriR = Double.valueOf(Double.parseDouble(totalAmtBH[0]));
          EmplrContriR = Double.valueOf(Double.parseDouble(totalAmtBH[1]));
          //totalEmplyerContri = nosci(EmplrContri.doubleValue());
          //this.gLogger.info("In Loop3  " + EmplrContri);
          //totalEContri = Double.valueOf(Double.parseDouble(totalEmplyContri) + Double.parseDouble(totalEmplyerContri));
          //this.gLogger.info("In Loop 4 " + totalEContri);
        }*/
		if ((TableData1 != null) && (TableData1.size() > 0))
        {
	     this.gLogger.info("In Loop  " + TableData1.get(0).toString());
	     if(TableData1.get(0).toString().equals("#")){/////$t 11-11-2020 if condition added for if 1st query data is null
	    	 EmpleeContriR = 0d;
	    	 EmplrContriR = 0d;
	     }else{
          String[] totalAmtBH = TableData1.get(0).toString().split("#");
          this.gLogger.info("In Loop1  " + TableData1.get(0).toString());
          EmpleeContriR = Double.valueOf(Double.parseDouble(totalAmtBH[0]));
          EmplrContriR = Double.valueOf(Double.parseDouble(totalAmtBH[1]));
	     }
        }
		if ((TableData2 != null) && (TableData2.size() > 0))
        {
	     this.gLogger.info("In Loop  " + TableData2.get(0).toString());
	     if(TableData2.get(0).toString().equals("#")){
	    	 EmpleeContriA = 0d;
	         EmplrContriA = 0d;
	     }else{
	         String[] totalAmtBH = TableData2.get(0).toString().split("#");
	          this.gLogger.info("In Loop1  " + TableData2.get(0).toString());
	          EmpleeContriA = Double.valueOf(Double.parseDouble(totalAmtBH[0]));
	          EmplrContriA = Double.valueOf(Double.parseDouble(totalAmtBH[1]));
	     }
        }
		
		totalEmplyContri=String.valueOf(EmpleeContriR + EmpleeContriA);
		this.gLogger.info("In totalEmplyContri  " + totalEmplyContri);
		totalEmplyerContri=String.valueOf(EmplrContriR + EmplrContriA);
		this.gLogger.info("In totalEmplyerContri  " + totalEmplyerContri);
		String finalVal=totalEmplyContri+"#"+totalEmplyerContri;
		this.gLogger.info("In finalVal  " + finalVal);
		//$t 2019 check
		 temp =new ArrayList();
		 temp.add(finalVal);
		}else{
			StringBuilder Strbld = new StringBuilder();
			
					Strbld.append("select cast(sum(a.emp_amt-nvl(b.sd_amnt,0)) as VARCHAR(20)) ||'#'|| cast(sum(a.DED_ADJUST-nvl(b.sd_emplr_amnt,0)) as VARCHAR(20)) from ");
					Strbld.append("(SELECT sum(paybill.DCPS)+sum(paybill.DCPS_PAY)+sum(paybill.DCPS_DELAY)+sum(paybill.DCPS_DA)+sum(paybill.DCPS_PAY_DIFF) as emp_amt,    ");////PayArrearDiff $t 23-2-2021
					Strbld.append("sum(paybill.DED_ADJUST) as DED_ADJUST ,emp.pran_no,reg.ddo_reg_no  FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");
					Strbld.append("inner join NSDL_PAYBILL_DATA paybill on paybill.EMP_ID=eis.EMP_ID ");
					Strbld.append(" inner join PAYBILL_HEAD_MPG head on head.PAYBILL_ID=paybill.PAYBILL_GRP_ID ");
					Strbld.append(" and paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA+paybill.DCPS_PAY_DIFF <=paybill.DED_ADJUST  ");////PayArrearDiff $t 23-2-2021
					Strbld.append(" inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID ");
					Strbld.append("inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
					Strbld.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2)  ");
					Strbld.append(" inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  ");
					//if(treasuryyno.substring(0, 2).equals("12")||treasuryyno.substring(0, 2).equals("46")||treasuryyno.substring(0, 2).equals("23")||treasuryyno.substring(0, 2).equals("51")){////$t KR 5-1-2020
					Strbld.append(" where head.PAYBILL_YEAR=" + yrCode
							+ " and head.PAYBILL_MONTH=" + month
							+ "  and substr(ddo.ddo_code,1,2)='" + treasuryyno.substring(0,2) + "' ");////$t KR 18-12-2020
					/*}else{
					Strbld.append(" where head.PAYBILL_YEAR=" + yrCode
							+ " and head.PAYBILL_MONTH=" + month
							+ "  and substr(ddo.ddo_code,1,4)='" + treasuryyno + "' ");	
					}*/
					Strbld.append(" and emp.PRAN_NO is not null and emp.PRAN_ACTIVE=1 and emp.REG_STATUS=1 and head.APPROVE_FLAG=1 group by emp.pran_no,reg.ddo_reg_no ) a left outer join ");
					Strbld.append(" (SELECT sd.SD_PRAN_NO,cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt ,cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt,bh.YEAR,bh.MONTH,sd.ddo_reg_no FROM NSDL_SD_DTLS sd ");
					Strbld.append(" inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.STATUS <>-1 ");
					//if(treasuryyno.substring(0, 2).equals("12")||treasuryyno.substring(0, 2).equals("46")||treasuryyno.substring(0, 2).equals("23")||treasuryyno.substring(0, 2).equals("51")){////$t KR 5-1-2020
					Strbld.append(" and bh.YEAR=" + yrCode + " and bh.MONTH=" + month+ 
							" and bh.file_name like '" + treasuryyno.substring(0,2) + "%' and bh.file_name not like '%D' and sd.SD_REMARK like 'Contribution for%' and cast(sd.SD_EMP_AMOUNT as bigint) <= cast(sd.SD_EMPlr_AMOUNT as bigint) ");////$t KR //+ " and bh.file_name like '" + treasuryyno + "%' "); /////$t  25-3-2021
					/*}else{
					Strbld.append(" and bh.YEAR=" + yrCode + " and bh.MONTH=" + month+ 
							" and bh.file_name like '" + treasuryyno + "%' ");	
					}*/
			
					Strbld.append(" group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH,sd.ddo_reg_no ) b ");
					Strbld.append(" on b.SD_PRAN_NO=a.PRAN_NO and b.ddo_reg_no=a.ddo_reg_no  where a.emp_amt-cast(nvl(b.sd_amnt,0) as double) > 0 and a.DED_ADJUST-cast(nvl(b.sd_emplr_amnt,0) as double) >0 ");
					this.gLogger.info("Else Query is ***********getEmployeeContriTotalList***"
							+ Strbld.toString());
			
					SQLQuery lQuery = this.ghibSession.createSQLQuery(Strbld.toString());
			
					temp = lQuery.list();
					this.gLogger.info("Else Query is ***********getEmployeeContriTotalList* temp size is **"+ temp.size());
		}
		return temp;
	}

////$t 2019 7-11 
//	public List getEmployeeContriTotalList(String yrCode, String month,
//			String treasuryyno, int flag) {
//		List temp = null;
//		//Long regCount = null;
//		StringBuilder Strbld = new StringBuilder();
//
//		Strbld.append("select cast(sum(a.emp_amt-nvl(b.sd_amnt,0)) as VARCHAR(20)) ||'#'|| cast(sum(a.DED_ADJUST-nvl(b.sd_emplr_amnt,0)) as VARCHAR(20)) from ");
//		Strbld.append("(SELECT sum(paybill.DCPS)+sum(paybill.DCPS_PAY)+sum(paybill.DCPS_DELAY)+sum(paybill.DCPS_DA) as emp_amt,    ");
//		// Strbld.append("sum(paybill.DED_ADJUST) as DED_ADJUST,emp.pran_no,reg.ddo_reg_no  FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");
//		Strbld.append("sum(paybill.DED_ADJUST+nvl(paybill.emplr_contri_arrears,0)) as DED_ADJUST ,emp.pran_no,reg.ddo_reg_no  FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");
//		Strbld.append("inner join NSDL_PAYBILL_DATA paybill on paybill.EMP_ID=eis.EMP_ID ");
//		Strbld.append(" inner join PAYBILL_HEAD_MPG head on head.PAYBILL_ID=paybill.PAYBILL_GRP_ID ");
//		Strbld.append(" and paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA <=paybill.DED_ADJUST  ");
//		Strbld.append(" inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID ");
//		Strbld.append("inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
//		Strbld.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2)  ");
//		Strbld.append(" inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  ");
//		Strbld.append(" where head.PAYBILL_YEAR=" + yrCode
//				+ " and head.PAYBILL_MONTH=" + month
//				+ "  and substr(ddo.ddo_code,1,4)='" + treasuryyno + "' ");
//		Strbld.append(" and emp.PRAN_NO is not null and PRAN_ACTIVE=1 and emp.REG_STATUS=1 and head.APPROVE_FLAG=1 group by emp.pran_no,reg.ddo_reg_no ) a left outer join ");
//		Strbld.append(" (SELECT sd.SD_PRAN_NO,cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt ,cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt,bh.YEAR,bh.MONTH,sd.ddo_reg_no FROM NSDL_SD_DTLS sd ");
//		Strbld.append(" inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.STATUS <>-1 ");
//		Strbld.append(" and bh.YEAR=" + yrCode + " and bh.MONTH=" + month
//				+ " and bh.file_name like '" + treasuryyno + "%' ");
//
//		Strbld.append(" group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH,sd.ddo_reg_no ) b ");
//		Strbld.append(" on b.SD_PRAN_NO=a.PRAN_NO and b.ddo_reg_no=a.ddo_reg_no  where a.emp_amt-cast(nvl(b.sd_amnt,0) as double) > 0 and a.DED_ADJUST-cast(nvl(b.sd_emplr_amnt,0) as double) >0 ");
//		//$t 2019 4/11
//		// Strbld.append(" union all ");
//		// Strbld.append("select cast(sum(a.emp_amt-nvl(b.sd_amnt,0)) as VARCHAR(20)) ||'#'|| cast(sum(a.DED_ADJUST-nvl(b.sd_emplr_amnt,0)) as VARCHAR(20)) from ");
//		// Strbld.append("(SELECT cast('0' as double) as emp_amt, ");
//		// Strbld.append("cast(SUM(nvl(paybill.emplr_contri_arrears,0))as double)  as DED_ADJUST,emp.pran_no,reg.ddo_reg_no  FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");
//		// Strbld.append("inner join NSDL_PAYBILL_DATA paybill on paybill.EMP_ID=eis.EMP_ID ");
//		// Strbld.append(" inner join PAYBILL_HEAD_MPG head on head.PAYBILL_ID=paybill.PAYBILL_GRP_ID ");
//		// Strbld.append(" inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID ");
//		// Strbld.append("inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
//		// Strbld.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2)  ");
//		// Strbld.append(" inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  ");
//		// Strbld.append(" where head.PAYBILL_YEAR=" + yrCode +
//		// " and head.PAYBILL_MONTH=" + month +
//		// "  and substr(ddo.ddo_code,1,4)='" + treasuryyno + "' ");
//		// Strbld.append(" and emp.PRAN_NO is not null and PRAN_ACTIVE=1 and emp.REG_STATUS=1 and cast((nvl(paybill.emplr_contri_arrears,0)) as double) <> 0 and head.APPROVE_FLAG=1 group by emp.pran_no,reg.ddo_reg_no ) a left outer join ");
//		// Strbld.append(" (SELECT sd.SD_PRAN_NO,cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt ,cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt,bh.YEAR,bh.MONTH,sd.ddo_reg_no FROM NSDL_SD_DTLS sd ");
//		// Strbld.append(" inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.STATUS <>-1 ");
//		// Strbld.append(" and bh.YEAR=" +
//		// yrCode +
//		// " and bh.MONTH=" +
//		// month +
//		// " and bh.file_name like '" + treasuryyno + "%' " );
//		//
//		// Strbld.append(" group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH,sd.ddo_reg_no ) b ");
//		// Strbld.append(" on b.SD_PRAN_NO=a.PRAN_NO and b.ddo_reg_no=a.ddo_reg_no  where a.emp_amt-cast(nvl(b.sd_amnt,0) as double) >=0 and a.DED_ADJUST-cast(nvl(b.sd_emplr_amnt,0) as double) >0 ");
//		//
//
//		this.gLogger.info("Query is ***********getEmployeeContriTotalList***"
//				+ Strbld.toString());
//
//		SQLQuery lQuery = this.ghibSession.createSQLQuery(Strbld.toString());
//
//		temp = lQuery.list();
//		this.gLogger
//				.info("Query is ***********getEmployeeContriTotalList* temp size is **"
//						+ temp.size());
//		// this.//ghibSession.disconnect();
//		return temp;
//	}
	
	
	
	public List getEmployeeContriTotalListForDeputation(String yrCode,
			String month, String treasuryyno, String year,
			String treasuryDDOCode) {
		List temp = null;

		List lLstReturnList1 = null;
		List lLstReturnList2 = null;
		List finalList = null;
		StringBuilder sb = new StringBuilder();

		sb.append("  select sum(abc.c-nvl(a.sd_amnt,0)),sum(abc.d-nvl(a.sd_emplr_amnt,0))   from ");
		sb.append(" (select mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as c,cast(sum(nvl(trn.CONTRIBUTION_EMPLR,0) ) as double) as d, reg.ddo_reg_no ");
		sb.append(" FROM MST_DCPS_EMP mstemp inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=mstemp.DCPS_EMP_ID  inner join ORG_DDO_MST ddo on ddo.DDO_CODE=case when substr(mstemp.DDO_CODE,1,2)=substr('"+treasuryyno+"',1,2) then mstemp.DDO_CODE else '"+treasuryDDOCode+"' end inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code ");////$t 24/5/2021 DDO-DTO mapping
		sb.append(" where trn.FIN_YEAR_ID="+ yrCode+ " and trn.MONTH_ID="+ month+"  and substr(trn.TREASURY_CODE,1,2)='"+ treasuryyno.substring(0, 2)+ "' " ////$t 8-4-2021 deputation KR one file
				+ "and (trn.IS_CHALLAN='Y' or trn.IS_ARREARS='Y')  and trn.STATUS='H' and trn.IS_DEPUTATION='Y' and mstemp.PRAN_NO is not null and PRAN_ACTIVE=1  ");
		sb.append(" and mstemp.DDO_CODE is not null and mstemp.DEPT_DDO_CODE is null and mstemp.DCPS_OR_GPF='Y' ");
		sb.append(" group by mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, reg.ddo_reg_no) abc ");
		sb.append(" left outer join (    SELECT    sd.SD_PRAN_NO,    cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt , cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt,   bh.YEAR,    bh.MONTH    FROM NSDL_SD_DTLS sd    inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME ");
		sb.append(" and bh.STATUS <>-1    and bh.YEAR=" + year+ " and bh.MONTH=" + month + " and ");
		sb.append(" bh.file_name like '"+ treasuryyno.substring(0, 2)+ "%D'    group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ) a on a.sd_pran_no=abc.pran_no "); ////$t 8-4-2021 deputation KR one file
		sb.append(" where  cast(abc.c-nvl(a.sd_amnt,0) as double) >=0 and cast(abc.d-nvl(a.sd_emplr_amnt,0) as double) >0 ");

		this.gLogger.info("Query is ***********getEmployeeContriTotalList***"
				+ sb.toString());

		SQLQuery lQuery = this.ghibSession.createSQLQuery(sb.toString());

		temp = lQuery.list();

		StringBuilder sb1 = new StringBuilder();
		sb1.append("  select sum(abc.c-nvl(a.sd_amnt,0)) ,sum(abc.d-nvl(a.sd_emplr_amnt,0))  from ");
		sb1.append(" (select mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as c,cast(sum(nvl(trn.CONTRIBUTION_EMPLR,0) ) as double) as d, reg.ddo_reg_no ");
		sb1.append(" FROM MST_DCPS_EMP mstemp inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=mstemp.DCPS_EMP_ID  inner join ORG_DDO_MST ddo on ddo.DDO_CODE='"+ treasuryDDOCode+ "' inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code ");
		sb1.append(" where trn.FIN_YEAR_ID="+ yrCode+ " and trn.MONTH_ID="+ month+"  and substr(trn.TREASURY_CODE,1,2)='"+ treasuryyno.substring(0, 2)+ "' " ////$t 8-4-2021 deputation KR one file
				+ "and (trn.IS_CHALLAN='Y' or trn.IS_ARREARS='Y')  and trn.STATUS='H' and trn.IS_DEPUTATION='Y' and mstemp.PRAN_NO is not null and PRAN_ACTIVE=1  ");
		sb1.append(" and mstemp.DDO_CODE is null and mstemp.DEPT_DDO_CODE is null and mstemp.DCPS_OR_GPF='Y' ");
		sb1.append(" group by mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, reg.ddo_reg_no) abc ");
		sb1.append(" left outer join (    SELECT    sd.SD_PRAN_NO,    cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt , cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt,   bh.YEAR,    bh.MONTH    FROM NSDL_SD_DTLS sd    inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME ");
		sb1.append(" and bh.STATUS <>-1    and bh.YEAR=" + year+ " and bh.MONTH=" + month + " and ");
		sb1.append(" bh.file_name like '"+ treasuryyno.substring(0, 2)+ "%D'    group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ) a on a.sd_pran_no=abc.pran_no ");
		sb1.append(" where  cast(abc.c-nvl(a.sd_amnt,0) as double) >=0  and cast(abc.d-nvl(a.sd_emplr_amnt,0) as double) >0 ");

		this.gLogger.info("Query is ***********getEmployeeContriTotalList***"
				+ sb.toString());

		SQLQuery lQuery1 = this.ghibSession.createSQLQuery(sb1.toString());

		lLstReturnList1 = lQuery1.list();

		StringBuilder sb2 = new StringBuilder();
		sb2.append("  select sum(abc.c-nvl(a.sd_amnt,0)),sum(abc.d-nvl(a.sd_emplr_amnt,0))   from ");
		sb2.append(" (select mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as c, cast(sum(nvl(trn.CONTRIBUTION_EMPLR,0) ) as double) as d, reg.ddo_reg_no ");
		sb2.append(" FROM MST_DCPS_EMP mstemp inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=mstemp.DCPS_EMP_ID  inner join ORG_DDO_MST ddo on ddo.DDO_CODE= case when substr(mstemp.DEPT_DDO_CODE,1,2)=substr('"+treasuryyno+"',1,2) then mstemp.DEPT_DDO_CODE else '"+treasuryDDOCode+"' end inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code ");////$t 24/5/2021 DDO-DTO mapping
		sb2.append(" where trn.FIN_YEAR_ID="+ yrCode+ " and trn.MONTH_ID="+ month+"  and substr(trn.TREASURY_CODE,1,2)='"+ treasuryyno.substring(0, 2)+ "' " ////$t 8-4-2021 deputation KR one file
				+ "and (trn.IS_CHALLAN='Y' or trn.IS_ARREARS='Y')  and trn.STATUS='H' and trn.IS_DEPUTATION='Y' and mstemp.PRAN_NO is not null and PRAN_ACTIVE=1  ");
		sb2.append(" and mstemp.DDO_CODE is null and mstemp.DEPT_DDO_CODE is not null and mstemp.DCPS_OR_GPF='Y' ");
		sb2.append(" group by mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, reg.ddo_reg_no) abc ");
		sb2.append(" left outer join (    SELECT    sd.SD_PRAN_NO,    cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt , cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt,   bh.YEAR,    bh.MONTH    FROM NSDL_SD_DTLS sd    inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME ");
		sb2.append(" and bh.STATUS <>-1    and bh.YEAR=" + year+ " and bh.MONTH=" + month + " and ");
		sb2.append(" bh.file_name like '"+ treasuryyno.substring(0, 2)+ "%D'    group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ) a on a.sd_pran_no=abc.pran_no "); ////$t 8-4-2021 deputation KR one file
		sb2.append(" where  cast(abc.c-nvl(a.sd_amnt,0) as double) >=0  and cast(abc.d-nvl(a.sd_emplr_amnt,0) as double) >0 ");

		this.gLogger.info("Query is ***********getEmployeeContriTotalList***"
				+ sb.toString());

		SQLQuery lQuery2 = this.ghibSession.createSQLQuery(sb2.toString());

		lLstReturnList2 = lQuery2.list();
		if (((temp != null) && (temp.size() > 0))
				|| ((lLstReturnList1 != null) && (lLstReturnList1.size() > 0))
				|| ((lLstReturnList2 != null) && (lLstReturnList2.size() > 0))) {
			finalList = new ArrayList();
			if ((temp != null) && (temp.size() > 0)) {
				finalList.addAll(temp);
			}
			if ((lLstReturnList1 != null) && (lLstReturnList1.size() > 0)) {
				finalList.addAll(lLstReturnList1);
			}
			if ((lLstReturnList2 != null) && (lLstReturnList2.size() > 0)) {
				finalList.addAll(lLstReturnList2);
			}
		}
		// this.//ghibSession.disconnect();
		return finalList;
	}

	// $t 2019 9oct
	public String[] getEmployeeCountDdoregNsdl(String yrCode, String month,
			String treasuryyno, int flag) {
		String[] empCountLst = new String[1];

	////$t 2020 17-1 year 2020 n month jan added
		if ((yrCode.equals("2019") && (month.equals("10")|| month.equals("11") || month.equals("9")||month.equals("12")))
			||yrCode.equals("2020")
			//||(yrCode.equals("2021") && (month.equals("1") || month.equals("2")))) {
			||(yrCode.equals("2021"))) {////$tC
			List empLst = null;
			List TableData1 = null;
			List TableData2 = null;

			StringBuilder Strbld = new StringBuilder();
			try {
				Strbld.append(" (select cast(count( distinct a.PRAN_NO)as bigint)  from ");
				Strbld.append("(SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,sum(paybill.DCPS)+sum(paybill.DCPS_PAY)+sum(paybill.DCPS_DELAY)+sum(paybill.DCPS_DA)+sum(paybill.DCPS_PAY_DIFF) as emp_amt,    ");////PayArrearDiff $t 23-2-2021
				Strbld.append(" sum(paybill.DED_ADJUST) as DED_ADJUST ,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");
				Strbld.append("inner join NSDL_PAYBILL_DATA paybill on paybill.EMP_ID=eis.EMP_ID ");
				Strbld.append(" inner join PAYBILL_HEAD_MPG head on head.PAYBILL_ID=paybill.PAYBILL_GRP_ID ");
				Strbld.append("and paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA+paybill.DCPS_PAY_DIFF >paybill.DED_ADJUST  ");////PayArrearDiff $t 23-2-2021
				Strbld.append("inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID ");
				Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
				Strbld.append("inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2)  ");
				Strbld.append("inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  ");
				//if(treasuryyno.substring(0, 2).equals("12")||treasuryyno.substring(0, 2).equals("46")||treasuryyno.substring(0, 2).equals("23")||treasuryyno.substring(0, 2).equals("51")){////$t KR 5-1-2020
				Strbld.append("where head.PAYBILL_YEAR='" + yrCode
						+ "' and head.PAYBILL_MONTH='" + month
						+ "'  and substr(ddo.ddo_code,1,2)='" + treasuryyno.substring(0,2)
						+ "' ");////$t KR 18-12-2020
				/*}else{
				Strbld.append("where head.PAYBILL_YEAR='" + yrCode
						+ "' and head.PAYBILL_MONTH='" + month
						+ "'  and substr(ddo.ddo_code,1,4)='" + treasuryyno
						+ "' ");	
				}*/
				Strbld.append(" and emp.PRAN_NO is not null and emp.PRAN_ACTIVE=1 and emp.REG_STATUS=1 and head.APPROVE_FLAG=1  ");
				Strbld.append(" group by  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no ) a left outer join ");
				Strbld.append(" (SELECT sd.SD_PRAN_NO,cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt,cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt ,bh.YEAR,bh.MONTH,sd.ddo_reg_no FROM NSDL_SD_DTLS sd ");
				Strbld.append(" inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.STATUS <>-1 ");
				//if(treasuryyno.substring(0, 2).equals("12")||treasuryyno.substring(0, 2).equals("46")||treasuryyno.substring(0, 2).equals("23")||treasuryyno.substring(0, 2).equals("51")){////$t KR 5-1-2020
				Strbld.append(" and bh.YEAR=" + yrCode + " and bh.MONTH="
						+ month + " and bh.file_name like '" + treasuryyno.substring(0,2)
						+ "%' and sd.SD_REMARK like 'Contribution%' and bh.file_name not like '%D'");////$t KR /////$t  25-3-2021
				/*}else{
				Strbld.append(" and bh.YEAR=" + yrCode + " and bh.MONTH="
						+ month + " and bh.file_name like '" + treasuryyno
						+ "%'  ");////$t KR	
				}*/
				Strbld.append(" group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH,sd.ddo_reg_no ) b on b.SD_PRAN_NO=a.PRAN_NO and b.ddo_reg_no=a.ddo_reg_no ");
				Strbld.append(" where cast(a.emp_amt-nvl(b.sd_amnt,0) as double) > 0  and cast(a.DED_ADJUST-nvl(b.sd_emplr_amnt,0) as double) > 0 group by a.ddo_reg_no order by  a.ddo_reg_no) ");
                //$t 2019 4/11
				Query lQuery = this.ghibSession.createSQLQuery(Strbld
						.toString());

				gLogger.info("script for all Rregular employee ---------"
						+ Strbld.toString());

				TableData1 = lQuery.list();

				gLogger.info("script for all Rregular employee ---------"
						+ Strbld.toString());

				// Strbld.append(" union all ");

				StringBuilder SBQuery1 = new StringBuilder();

				SBQuery1.append(" (select cast(count( distinct a.PRAN_NO) as bigint)  from ");
				SBQuery1.append(" (SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,cast('0' as double) as emp_amt,     ");
				SBQuery1.append(" cast(SUM(nvl(paybill.emplr_contri_arrears,0))as double)+SUM(nvl(paybill.NPS_EMPLR_DIFFERENCE_ADJ,0))  as DED_ADJUST ,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");////$tC
				SBQuery1.append(" inner join NSDL_PAYBILL_DATA paybill on paybill.EMP_ID=eis.EMP_ID ");
				SBQuery1.append(" inner join PAYBILL_HEAD_MPG head on head.PAYBILL_ID=paybill.PAYBILL_GRP_ID ");
				SBQuery1.append(" inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID ");
				SBQuery1.append(" inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
				SBQuery1.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2)  ");
				SBQuery1.append(" inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  ");
				//if(treasuryyno.substring(0, 2).equals("12")||treasuryyno.substring(0, 2).equals("46")||treasuryyno.substring(0, 2).equals("23")||treasuryyno.substring(0, 2).equals("51")){////$t KR 5-1-2020
				SBQuery1.append(" where head.PAYBILL_YEAR='" + yrCode
						+ "' and head.PAYBILL_MONTH='" + month
						+ "'  and substr(ddo.ddo_code,1,2)='" + treasuryyno.substring(0,2)
						+ "' ");////$t KR 18-12-2020
				/*}else{
				SBQuery1.append(" where head.PAYBILL_YEAR='" + yrCode
						+ "' and head.PAYBILL_MONTH='" + month
						+ "'  and substr(ddo.ddo_code,1,4)='" + treasuryyno
						+ "' ");	
				}*/
				SBQuery1.append(" and emp.PRAN_NO is not null and emp.PRAN_ACTIVE=1 and emp.REG_STATUS=1 and head.APPROVE_FLAG=1 and (cast((nvl(paybill.emplr_contri_arrears,0)) as double) <> 0  or cast((nvl(paybill.NPS_EMPLR_DIFFERENCE_ADJ,0)) as double) <> 0) ");////$tC
				SBQuery1.append(" group by  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no ) a left outer join ");
				SBQuery1.append(" (SELECT sd.SD_PRAN_NO,cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt,cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt ,bh.YEAR,bh.MONTH,sd.ddo_reg_no FROM NSDL_SD_DTLS sd ");
				SBQuery1.append(" inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.STATUS <>-1 ");
				//if(treasuryyno.substring(0, 2).equals("12")||treasuryyno.substring(0, 2).equals("46")||treasuryyno.substring(0, 2).equals("23")||treasuryyno.substring(0, 2).equals("51")){////$t KR 5-1-2020
				SBQuery1.append(" and bh.YEAR=" + yrCode + " and bh.MONTH="
						+ month + " and bh.file_name like '" + treasuryyno.substring(0,2)
						+ "%' and sd.SD_REMARK like '8 Per Contribution%' ");////$t KR
				/*}else{
				SBQuery1.append(" and bh.YEAR=" + yrCode + " and bh.MONTH="
						+ month + " and bh.file_name like '" + treasuryyno
						+ "%'  ");////$t KR	
				}*/
				SBQuery1.append(" group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH,sd.ddo_reg_no ) b on b.SD_PRAN_NO=a.PRAN_NO and b.ddo_reg_no=a.ddo_reg_no ");
				SBQuery1.append(" where cast(a.emp_amt-nvl(b.sd_amnt,0) as double) > 0  and cast(a.DED_ADJUST-nvl(b.sd_emplr_amnt,0) as double) > 0 group by a.ddo_reg_no order by  a.ddo_reg_no) ");

				Query lQuery1 = this.ghibSession.createSQLQuery(SBQuery1
						.toString());

				gLogger.info("script for all Rregular employee ---------"
						+ SBQuery1.toString());

				TableData2 = lQuery1.list();

				gLogger.info("script for all Rregular employee ---------"
						+ SBQuery1.toString());

				if ((TableData1 != null && TableData1.size() > 0)
						|| (TableData2 != null && TableData2.size() > 0)) {
					empLst = new ArrayList();
					if (TableData1 != null && TableData1.size() > 0) {
						empLst.addAll(TableData1);

					}
					if (TableData2 != null && TableData2.size() > 0) {
						empLst.addAll(TableData2);
					}

				}

				// empLst = lQuery.list();
				empCountLst = new String[empLst.size()];
				for (int i = 0; i < empLst.size(); i++) {
					empCountLst[i] = empLst.get(i).toString();
					this.logger.info("empCountLst##################"
							+ empCountLst);
				}
				// this.//ghibSession.disconnect();

				// empLst = lQuery.list();
				// Long total = Long.valueOf(0L);
				// Iterator itr = TableData.iterator();
				// while (itr.hasNext()) {
				// total = Long.valueOf(total.longValue()
				// + Long.parseLong(itr.next().toString()));
				// }
				// String StrTotal = "A";
				// if (total.longValue() > 0L) {
				// StrTotal = total.toString();
				// }
				// System.out.println("--->" + StrTotal);
				//
				// empCountLst[0] = StrTotal;
			} catch (Exception e) {
				this.logger.info("Error occer in  getEmployeeList ---------"
						+ e);
			}
		} else {

			List empLst = null;

			StringBuilder Strbld = new StringBuilder();
			try {
				Strbld.append(" (select cast(count( distinct a.PRAN_NO)as bigint)  from ");
				Strbld.append("(SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,sum(paybill.DCPS)+sum(paybill.DCPS_PAY)+sum(paybill.DCPS_DELAY)+sum(paybill.DCPS_DA)+sum(paybill.DCPS_PAY_DIFF) as emp_amt,    ");////PayArrearDiff $t 23-2-2021
				Strbld.append(" sum(paybill.DED_ADJUST) as DED_ADJUST ,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");
				Strbld.append("inner join NSDL_PAYBILL_DATA paybill on paybill.EMP_ID=eis.EMP_ID ");
				Strbld.append(" inner join PAYBILL_HEAD_MPG head on head.PAYBILL_ID=paybill.PAYBILL_GRP_ID ");
				Strbld.append("and paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA+paybill.DCPS_PAY_DIFF <=paybill.DED_ADJUST  ");////PayArrearDiff $t 23-2-2021
				Strbld.append("inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID ");
				Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
				Strbld.append("inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2)  ");
				Strbld.append("inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  ");
				//if(treasuryyno.substring(0, 2).equals("12")||treasuryyno.substring(0, 2).equals("46")||treasuryyno.substring(0, 2).equals("23")||treasuryyno.substring(0, 2).equals("51")){////$t KR 5-1-2020
				Strbld.append("where head.PAYBILL_YEAR='" + yrCode
						+ "' and head.PAYBILL_MONTH='" + month
						+ "'  and substr(ddo.ddo_code,1,2)='" + treasuryyno.substring(0,2)
						+ "' ");////$t KR 18-12-2020
				/*}else{
				Strbld.append("where head.PAYBILL_YEAR='" + yrCode
						+ "' and head.PAYBILL_MONTH='" + month
						+ "'  and substr(ddo.ddo_code,1,4)='" + treasuryyno
						+ "' ");////$t KR 18-12-2020	
				}*/
				Strbld.append(" and emp.PRAN_NO is not null and emp.PRAN_ACTIVE=1 and emp.REG_STATUS=1 and head.APPROVE_FLAG=1  ");
				Strbld.append(" group by  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no ) a left outer join ");
				Strbld.append(" (SELECT sd.SD_PRAN_NO,cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt,cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt ,bh.YEAR,bh.MONTH,sd.ddo_reg_no FROM NSDL_SD_DTLS sd ");
				Strbld.append(" inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.STATUS <>-1 ");
				//if(treasuryyno.substring(0, 2).equals("12")||treasuryyno.substring(0, 2).equals("46")||treasuryyno.substring(0, 2).equals("23")||treasuryyno.substring(0, 2).equals("51")){////$t KR 5-1-2020
				Strbld.append(" and bh.YEAR=" + yrCode + " and bh.MONTH="
						+ month + " and bh.file_name like '" + treasuryyno.substring(0,2)
						+ "%' and sd.SD_REMARK like 'Contribution for%' and bh.file_name not like '%D'and cast(sd.SD_EMP_AMOUNT as bigint) <= cast(sd.SD_EMPlr_AMOUNT as bigint)");////$t KR /////$t  25-3-2021
				/*}else{
				Strbld.append(" and bh.YEAR=" + yrCode + " and bh.MONTH="
						+ month + " and bh.file_name like '" + treasuryyno
						+ "%' ");////$t KR	
				}*/
				Strbld.append(" group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH,sd.ddo_reg_no ) b on b.SD_PRAN_NO=a.PRAN_NO and b.ddo_reg_no=a.ddo_reg_no ");
				Strbld.append(" where cast(a.emp_amt-nvl(b.sd_amnt,0) as double) > 0  and cast(a.DED_ADJUST-nvl(b.sd_emplr_amnt,0) as double) > 0 group by a.ddo_reg_no order by  a.ddo_reg_no) ");

				// Strbld.append(" union all ");
				// Strbld.append(" (select cast(count( distinct a.PRAN_NO) as bigint)  from ");
				// Strbld.append(" (SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,cast('0' as double) as emp_amt,     ");
				// Strbld.append(" cast(SUM(nvl(paybill.emplr_contri_arrears,0))as double)  as DED_ADJUST ,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");
				// Strbld.append(" inner join NSDL_PAYBILL_DATA paybill on paybill.EMP_ID=eis.EMP_ID ");
				// Strbld.append(" inner join PAYBILL_HEAD_MPG head on head.PAYBILL_ID=paybill.PAYBILL_GRP_ID ");
				// Strbld.append(" inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID ");
				// Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
				// Strbld.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2)  ");
				// Strbld.append(" inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  ");
				// Strbld.append(" where head.PAYBILL_YEAR='" + yrCode
				// + "' and head.PAYBILL_MONTH='" + month
				// + "'  and substr(ddo.ddo_code,1,4)='" + treasuryyno
				// + "' ");
				//
				// Strbld.append(" and emp.PRAN_NO is not null and PRAN_ACTIVE=1 and emp.REG_STATUS=1 and head.APPROVE_FLAG=1 and cast((nvl(paybill.emplr_contri_arrears,0)) as double) <> 0 ");
				// Strbld.append(" group by  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no ) a left outer join ");
				// Strbld.append(" (SELECT sd.SD_PRAN_NO,cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt,cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt ,bh.YEAR,bh.MONTH,sd.ddo_reg_no FROM NSDL_SD_DTLS sd ");
				// Strbld.append(" inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.STATUS <>-1 ");
				// Strbld.append(" and bh.YEAR=" + yrCode + " and bh.MONTH="
				// + month + " and bh.file_name like '" + treasuryyno
				// + "%' ");
				//
				// Strbld.append(" group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH,sd.ddo_reg_no ) b on b.SD_PRAN_NO=a.PRAN_NO and b.ddo_reg_no=a.ddo_reg_no ");
				// Strbld.append(" where cast(a.emp_amt-nvl(b.sd_amnt,0) as double) >= 0  and cast(a.DED_ADJUST-nvl(b.sd_emplr_amnt,0) as double) > 0 group by a.ddo_reg_no order by  a.ddo_reg_no) ");

				/*
				 * this.logger.info("query for count" + Strbld.toString());
				 * SQLQuery lQuery = this.ghibSession.createSQLQuery(Strbld
				 * .toString());
				 * 
				 * empLst = lQuery.list(); Long total = Long.valueOf(0L);
				 * Iterator itr = empLst.iterator(); while (itr.hasNext()) {
				 * total = Long.valueOf(total.longValue() +
				 * Long.parseLong(itr.next().toString())); } String StrTotal =
				 * "A"; if (total.longValue() > 0L) { StrTotal =
				 * total.toString(); } System.out.println("--->" + StrTotal);
				 * 
				 * empCountLst[0] = StrTotal;
				 */

				this.logger.info("query for count" + Strbld.toString());
				SQLQuery lQuery = this.ghibSession.createSQLQuery(Strbld
						.toString());

				empLst = lQuery.list();
				empCountLst = new String[empLst.size()];
				for (int i = 0; i < empLst.size(); i++) {
					empCountLst[i] = empLst.get(i).toString();
					this.logger.info("empCountLst##################"
							+ empCountLst);
				}
				// this.//ghibSession.disconnect();
			} catch (Exception e) {
				this.logger.info("Error occer in  getEmployeeList ---------"
						+ e);
			}
		}

		return empCountLst;
	}

/*	
	// //$t 2019 union all deployed
	
	 * public String[] getEmployeeCountDdoregNsdl(String yrCode, String month,
	 * String treasuryyno, int flag) { List empLst = null;
	 * 
	 * String[] empCountLst = new String[1];
	 * 
	 * StringBuilder Strbld = new StringBuilder(); try {
	 * Strbld.append(" (select cast(count( distinct a.PRAN_NO)as bigint)  from "
	 * ); Strbld.append(
	 * "(SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,sum(paybill.DCPS)+sum(paybill.DCPS_PAY)+sum(paybill.DCPS_DELAY)+sum(paybill.DCPS_DA) as emp_amt,    "
	 * ); Strbld.append(
	 * " sum(paybill.DED_ADJUST+nvl(paybill.emplr_contri_arrears,0)) as DED_ADJUST ,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID "
	 * ); Strbld.append(
	 * "inner join NSDL_PAYBILL_DATA paybill on paybill.EMP_ID=eis.EMP_ID ");
	 * Strbld.append(
	 * " inner join PAYBILL_HEAD_MPG head on head.PAYBILL_ID=paybill.PAYBILL_GRP_ID "
	 * ); Strbld.append(
	 * "and paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA <=paybill.DED_ADJUST  "
	 * ); Strbld.append(
	 * "inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID ");
	 * Strbld
	 * .append(" inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
	 * Strbld.append(
	 * "inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2)  "
	 * ); Strbld.append(
	 * "inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  "
	 * ); Strbld.append("where head.PAYBILL_YEAR='" + yrCode +
	 * "' and head.PAYBILL_MONTH='" + month +
	 * "'  and substr(ddo.ddo_code,1,4)='" + treasuryyno + "' ");
	 * 
	 * Strbld.append(
	 * " and emp.PRAN_NO is not null and PRAN_ACTIVE=1 and emp.REG_STATUS=1 and head.APPROVE_FLAG=1  "
	 * ); Strbld.append(
	 * " group by  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no ) a left outer join "
	 * ); Strbld.append(
	 * " (SELECT sd.SD_PRAN_NO,cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt,cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt ,bh.YEAR,bh.MONTH,sd.ddo_reg_no FROM NSDL_SD_DTLS sd "
	 * ); Strbld.append(
	 * " inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.STATUS <>-1 "
	 * ); Strbld.append(" and bh.YEAR=" + yrCode + " and bh.MONTH=" + month +
	 * " and bh.file_name like '" + treasuryyno + "%' ");
	 * 
	 * Strbld.append(
	 * " group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH,sd.ddo_reg_no ) b on b.SD_PRAN_NO=a.PRAN_NO and b.ddo_reg_no=a.ddo_reg_no "
	 * ); Strbld.append(
	 * " where cast(a.emp_amt-nvl(b.sd_amnt,0) as double) > 0  and cast(a.DED_ADJUST-nvl(b.sd_emplr_amnt,0) as double) > 0 group by a.ddo_reg_no order by  a.ddo_reg_no) "
	 * ); Strbld.append(" union all ");
	 * Strbld.append(" (select cast(count( distinct a.PRAN_NO) as bigint)  from "
	 * ); Strbld.append(
	 * " (SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,cast('0' as double) as emp_amt,     "
	 * ); Strbld.append(
	 * " cast(SUM(nvl(paybill.emplr_contri_arrears,0))as double)  as DED_ADJUST ,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID "
	 * ); Strbld.append(
	 * " inner join NSDL_PAYBILL_DATA paybill on paybill.EMP_ID=eis.EMP_ID ");
	 * Strbld.append(
	 * " inner join PAYBILL_HEAD_MPG head on head.PAYBILL_ID=paybill.PAYBILL_GRP_ID "
	 * ); Strbld.append(
	 * " inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID ");
	 * Strbld
	 * .append(" inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
	 * Strbld.append(
	 * " inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2)  "
	 * ); Strbld.append(
	 * " inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  "
	 * ); Strbld.append(" where head.PAYBILL_YEAR='" + yrCode +
	 * "' and head.PAYBILL_MONTH='" + month +
	 * "'  and substr(ddo.ddo_code,1,4)='" + treasuryyno + "' ");
	 * 
	 * Strbld.append(
	 * " and emp.PRAN_NO is not null and PRAN_ACTIVE=1 and emp.REG_STATUS=1 and head.APPROVE_FLAG=1 and cast((nvl(paybill.emplr_contri_arrears,0)) as double) <> 0 "
	 * ); Strbld.append(
	 * " group by  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no ) a left outer join "
	 * ); Strbld.append(
	 * " (SELECT sd.SD_PRAN_NO,cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt,cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt ,bh.YEAR,bh.MONTH,sd.ddo_reg_no FROM NSDL_SD_DTLS sd "
	 * ); Strbld.append(
	 * " inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.STATUS <>-1 "
	 * ); Strbld.append(" and bh.YEAR=" + yrCode + " and bh.MONTH=" + month +
	 * " and bh.file_name like '" + treasuryyno + "%' ");
	 * 
	 * Strbld.append(
	 * " group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH,sd.ddo_reg_no ) b on b.SD_PRAN_NO=a.PRAN_NO and b.ddo_reg_no=a.ddo_reg_no "
	 * ); Strbld.append(
	 * " where cast(a.emp_amt-nvl(b.sd_amnt,0) as double) >= 0  and cast(a.DED_ADJUST-nvl(b.sd_emplr_amnt,0) as double) > 0 group by a.ddo_reg_no order by  a.ddo_reg_no) "
	 * );
	 * 
	 * this.logger.info("query for count" + Strbld.toString()); SQLQuery lQuery
	 * = this.ghibSession.createSQLQuery(Strbld.toString());
	 * 
	 * empLst = lQuery.list(); Long total = Long.valueOf(0L); Iterator itr =
	 * empLst.iterator(); while (itr.hasNext()) { total =
	 * Long.valueOf(total.longValue() + Long.parseLong(itr.next().toString()));
	 * } String StrTotal = "A"; if (total.longValue() > 0L) { StrTotal =
	 * total.toString(); } System.out.println("--->" + StrTotal);
	 * 
	 * empCountLst[0] = StrTotal; } catch (Exception e) {
	 * this.logger.info("Error occer in  getEmployeeList ---------" + e); }
	 * return empCountLst; }
*/	 

	// public String[] getEmployeeCountDdoregNsdl(String yrCode, String month,
	// String treasuryyno, int flag) throws Exception
	// {//$t 2019 DR connection
	// List empLst = null;
	// List lLstDept = null;
	// String USER= "";
	// String PASS = "";
	// String passward = "";
	// String passward1 = "";
	// String passwardStr = "@99";
	// lLstDept = getuser();
	// if ((lLstDept != null) && (lLstDept.size() > 0) && (lLstDept.get(0) !=
	// null))
	// {
	// Object[] obj = (Object[])lLstDept.get(0);
	// if ((obj[0] != null) && (obj[1] != null))
	// {
	// USER = obj[0].toString();
	// PASS = obj[1].toString();
	// }
	// System.out.println("USER" + USER + "PASS" + PASS);
	// }
	// passward1 = getHexStringConverterInstance(PASS);
	// passward = passward1 + passwardStr;
	// System.out.println("passward" + passward);
	//
	// Connection con = Drconnectionclass.getConnection(USER, passward);
	// Statement stmt = con.createStatement();
	// ResultSet rs = null;
	//
	// String [] empCountLst = new String[1];;
	//
	// //int[] empCountLst1 = null;
	//
	//
	//
	// StringBuilder Strbld = new StringBuilder();
	// try
	// {
	// Strbld.append(" (select cast(count( distinct a.PRAN_NO)as bigint)  from ");
	// Strbld.append("(SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,sum(paybill.DCPS)+sum(paybill.DCPS_PAY)+sum(paybill.DCPS_DELAY)+sum(paybill.DCPS_DA) as emp_amt,    ");
	// Strbld.append(" sum(paybill.DED_ADJUST+nvl(paybill.emplr_contri_arrears,0)) as DED_ADJUST ,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");
	// Strbld.append("inner join NSDL_PAYBILL_DATA paybill on paybill.EMP_ID=eis.EMP_ID ");
	// Strbld.append(" inner join PAYBILL_HEAD_MPG head on head.PAYBILL_ID=paybill.PAYBILL_GRP_ID ");
	// Strbld.append("and paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA <=paybill.DED_ADJUST  ");
	// Strbld.append("inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID ");
	// Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
	// Strbld.append("inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2)  ");
	// Strbld.append("inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  ");
	// Strbld.append("where head.PAYBILL_YEAR='" + yrCode +
	// "' and head.PAYBILL_MONTH='" + month +
	// "'  and substr(ddo.ddo_code,1,4)='" + treasuryyno +
	// "' ");
	//
	// Strbld.append(" and emp.PRAN_NO is not null and PRAN_ACTIVE=1 and emp.REG_STATUS=1 and head.APPROVE_FLAG=1  ");
	// Strbld.append(" group by  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no ) a left outer join ");
	// Strbld.append(" (SELECT sd.SD_PRAN_NO,cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt,cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt ,bh.YEAR,bh.MONTH,sd.ddo_reg_no FROM NSDL_SD_DTLS sd ");
	// Strbld.append(" inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.STATUS <>-1 ");
	// Strbld.append(" and bh.YEAR=" +
	// yrCode +
	// " and bh.MONTH=" +
	// month +
	// " and bh.file_name like '" +
	// treasuryyno +
	// "%' ");
	//
	// Strbld.append(" group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH,sd.ddo_reg_no ) b on b.SD_PRAN_NO=a.PRAN_NO and b.ddo_reg_no=a.ddo_reg_no ");
	// Strbld.append(" where cast(a.emp_amt-nvl(b.sd_amnt,0) as double) > 0  and cast(a.DED_ADJUST-nvl(b.sd_emplr_amnt,0) as double) > 0 group by a.ddo_reg_no order by  a.ddo_reg_no) ");
	// Strbld.append(" union all ");
	// Strbld.append(" (select cast(count( distinct a.PRAN_NO) as bigint)  from ");
	// Strbld.append(" (SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,cast('0' as double) as emp_amt,     ");
	// Strbld.append(" cast(SUM(nvl(paybill.emplr_contri_arrears,0))as double)  as DED_ADJUST ,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");
	// Strbld.append(" inner join NSDL_PAYBILL_DATA paybill on paybill.EMP_ID=eis.EMP_ID ");
	// Strbld.append(" inner join PAYBILL_HEAD_MPG head on head.PAYBILL_ID=paybill.PAYBILL_GRP_ID ");
	// Strbld.append(" inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID ");
	// Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
	// Strbld.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2)  ");
	// Strbld.append(" inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  ");
	// Strbld.append(" where head.PAYBILL_YEAR='" + yrCode +
	// "' and head.PAYBILL_MONTH='" + month +
	// "'  and substr(ddo.ddo_code,1,4)='" + treasuryyno +
	// "' ");
	//
	// Strbld.append(" and emp.PRAN_NO is not null and PRAN_ACTIVE=1 and emp.REG_STATUS=1 and head.APPROVE_FLAG=1 and cast((nvl(paybill.emplr_contri_arrears,0)) as double) <> 0 ");
	// Strbld.append(" group by  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no ) a left outer join ");
	// Strbld.append(" (SELECT sd.SD_PRAN_NO,cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt,cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt ,bh.YEAR,bh.MONTH,sd.ddo_reg_no FROM NSDL_SD_DTLS sd ");
	// Strbld.append(" inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.STATUS <>-1 ");
	// Strbld.append(" and bh.YEAR=" +
	// yrCode +
	// " and bh.MONTH=" +
	// month +
	// " and bh.file_name like '" +
	// treasuryyno +
	// "%' ");
	//
	// Strbld.append(" group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH,sd.ddo_reg_no ) b on b.SD_PRAN_NO=a.PRAN_NO and b.ddo_reg_no=a.ddo_reg_no ");
	// Strbld.append(" where cast(a.emp_amt-nvl(b.sd_amnt,0) as double) >= 0  and cast(a.DED_ADJUST-nvl(b.sd_emplr_amnt,0) as double) > 0 group by a.ddo_reg_no order by  a.ddo_reg_no) ");
	//
	//
	// this.logger.info("query for count" + Strbld.toString());
	// //SQLQuery lQuery = this.ghibSession.createSQLQuery(Strbld.toString());
	//
	// //empLst = lQuery.list();
	// // empCountLst = new String[empLst.size()];
	// // for (int i = 0; i < empLst.size(); i++) {
	// // empCountLst[i] = empLst.get(i).toString();
	// // }
	// // Double D = Double.parseDouble(empCountLst);
	// //
	// // Object [] objI;
	// // Object [] objI1;
	//
	// // empLst = lQuery.list();
	//
	// rs=stmt.executeQuery(Strbld.toString());
	//
	// while (rs.next()) {
	// ArrayList emplst1=new ArrayList();
	//
	// emplst1.add(rs.getString(1));
	// //$t 2019
	// //emplst1.add(rs.getString(2));
	// empLst.add(emplst1);
	//
	// }
	//
	// Long total=0l;
	// Iterator itr = empLst.iterator();
	//
	// while(itr.hasNext()) {
	//
	// total=total+ Long.parseLong(itr.next().toString());
	// }
	//
	// String StrTotal="A";
	// if(total>0)
	// StrTotal=(total.toString());
	// System.out.println("--->"+StrTotal);
	//
	// empCountLst[0] = StrTotal;
	// }
	// catch (Exception e)
	// {
	// this.logger.info("Error occer in  getEmployeeList ---------" + e);
	// }
	//
	//
	// return empCountLst;
	// }

	// public String getHexStringConverterInstance(String input)throws
	// UnsupportedEncodingException {
	//
	// byte [] txtInByte = new byte [input.length() / 2];
	// int j = 0;
	// for (int i = 0; i < input.length(); i += 2)
	// {
	// txtInByte[j++] = Byte.parseByte(input.substring(i, i + 2), 16);
	// }
	// return new String(txtInByte);
	// }

	public String[] getEmployeeCountDdoregNsdlForDeputation(String yrCode,
			String month, String treasuryyno, String year,
			String treasuryDDOCode) {
		List empLst = null;
		List lLstReturnList1 = null;
		List lLstReturnList2 = null;
		List finalList = null;
		String[] consolidatedList = null;

		String[] empCountLst = null;

		StringBuilder sb = new StringBuilder();
		try {
			sb.append("  select count( distinct abc.PRAN_NO),abc.DDO_REG_NO   from ");
			sb.append(" (select mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as c, cast(sum(nvl(trn.CONTRIBUTION_EMPLR,0) ) as double) as d, reg.ddo_reg_no ");
			sb.append(" FROM MST_DCPS_EMP mstemp inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=mstemp.DCPS_EMP_ID  inner join  ORG_DDO_MST ddo on ddo.DDO_CODE=case when substr(mstemp.DDO_CODE,1,2)=substr('"+treasuryyno+"',1,2) then mstemp.DDO_CODE else '"+treasuryDDOCode+"' end inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code ");////$t 24/5/2021 DDO-DTO mapping
			sb.append(" where trn.FIN_YEAR_ID="+ yrCode+ " and trn.MONTH_ID="+ month+" and substr(trn.TREASURY_CODE,1,2)='"+ treasuryyno.substring(0, 2)+ "' " ////$t 8-4-2021 deputation KR one file
					+ "and (trn.IS_CHALLAN='Y' or trn.IS_ARREARS='Y')  and trn.STATUS='H' and trn.IS_DEPUTATION='Y' and mstemp.PRAN_NO is not null and PRAN_ACTIVE=1  ");
			sb.append(" and mstemp.DDO_CODE is not null and mstemp.DEPT_DDO_CODE is null and mstemp.DCPS_OR_GPF='Y' ");
			sb.append(" group by mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, reg.ddo_reg_no) abc ");
			sb.append(" left outer join (    SELECT    sd.SD_PRAN_NO,    cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt , cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt,   bh.YEAR,    bh.MONTH    FROM NSDL_SD_DTLS sd    inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME ");
			sb.append(" and bh.STATUS <>-1    and bh.YEAR=" + year+ " and bh.MONTH=" + month + " and ");
			sb.append(" bh.file_name like '"+ treasuryyno.substring(0, 2)+ "%D'    group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ) a on a.sd_pran_no=abc.pran_no "); ////$t 8-4-2021 deputation KR one file
			sb.append(" where  cast(abc.c-nvl(a.sd_amnt,0) as double) >=0 and cast(abc.d-nvl(a.sd_emplr_amnt,0) as double) >0 group by abc.DDO_REG_NO order by   abc.DDO_REG_NO ");

			this.logger.info("query for count" + sb.toString());
			SQLQuery lQuery = this.ghibSession.createSQLQuery(sb.toString());

			empLst = lQuery.list();

			StringBuilder sb1 = new StringBuilder();
			sb1.append("  select count( distinct abc.PRAN_NO),abc.DDO_REG_NO   from ");
			sb1.append(" (select mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as c, cast(sum(nvl(trn.CONTRIBUTION_EMPLR,0) ) as double) as d, reg.ddo_reg_no ");
			sb1.append(" FROM MST_DCPS_EMP mstemp inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=mstemp.DCPS_EMP_ID  inner join ORG_DDO_MST ddo on ddo.DDO_CODE='"+ treasuryDDOCode+ "' inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code ");
			sb1.append(" where trn.FIN_YEAR_ID="+ yrCode+ " and trn.MONTH_ID="+ month+" and substr(trn.TREASURY_CODE,1,2)='"+ treasuryyno.substring(0, 2)+ "' " ////$t 8-4-2021 deputation KR one file
					+ "and (trn.IS_CHALLAN='Y' or trn.IS_ARREARS='Y')  and trn.STATUS='H' and trn.IS_DEPUTATION='Y' and mstemp.PRAN_NO is not null and PRAN_ACTIVE=1  ");
			sb1.append(" and mstemp.DDO_CODE is null and mstemp.DEPT_DDO_CODE is null and mstemp.DCPS_OR_GPF='Y' ");
			sb1.append(" group by mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, reg.ddo_reg_no) abc ");
			sb1.append(" left outer join (    SELECT    sd.SD_PRAN_NO,    cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt , cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt,   bh.YEAR,    bh.MONTH    FROM NSDL_SD_DTLS sd    inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME ");
			sb1.append(" and bh.STATUS <>-1    and bh.YEAR=" + year+ " and bh.MONTH=" + month + " and ");
			sb1.append(" bh.file_name like '"+ treasuryyno.substring(0, 2)+ "%D'    group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ) a on a.sd_pran_no=abc.pran_no "); ////$t 8-4-2021 deputation KR one file
			sb1.append(" where  cast(abc.c-nvl(a.sd_amnt,0) as double) >=0 and cast(abc.d-nvl(a.sd_emplr_amnt,0) as double) >0 group by abc.DDO_REG_NO order by   abc.DDO_REG_NO ");

			this.logger.info("query for count" + sb1.toString());
			SQLQuery lQuery1 = this.ghibSession.createSQLQuery(sb1.toString());

			lLstReturnList1 = lQuery1.list();

			StringBuilder sb2 = new StringBuilder();
			sb2.append("  select count( distinct abc.PRAN_NO),abc.DDO_REG_NO   from ");
			sb2.append(" (select mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as c, cast(sum(nvl(trn.CONTRIBUTION_EMPLR,0) ) as double) as d, reg.ddo_reg_no ");
			sb2.append(" FROM MST_DCPS_EMP mstemp inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=mstemp.DCPS_EMP_ID  inner join ORG_DDO_MST ddo on ddo.DDO_CODE= case when substr(mstemp.DEPT_DDO_CODE,1,2)=substr('"+treasuryyno+"',1,2) then mstemp.DEPT_DDO_CODE else '"+treasuryDDOCode+"' end inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code ");////$t 24/5/2021 DDO-DTO mapping
			sb2.append(" where trn.FIN_YEAR_ID="+ yrCode+ " and trn.MONTH_ID="+ month+" and substr(trn.TREASURY_CODE,1,2)='"+ treasuryyno.substring(0, 2)+ "' " ////$t 8-4-2021 deputation KR one file
					+ "and (trn.IS_CHALLAN='Y' or trn.IS_ARREARS='Y')  and trn.STATUS='H' and trn.IS_DEPUTATION='Y' and mstemp.PRAN_NO is not null and PRAN_ACTIVE=1  ");
			sb2.append(" and mstemp.DDO_CODE is null and mstemp.DEPT_DDO_CODE is not null and mstemp.DCPS_OR_GPF='Y' ");
			sb2.append(" group by mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, reg.ddo_reg_no) abc ");
			sb2.append(" left outer join (    SELECT    sd.SD_PRAN_NO,    cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt ,cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt,    bh.YEAR,    bh.MONTH    FROM NSDL_SD_DTLS sd    inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME ");
			sb2.append(" and bh.STATUS <>-1    and bh.YEAR=" + year+ " and bh.MONTH=" + month + " and ");
			sb2.append(" bh.file_name like '"+ treasuryyno.substring(0, 2)+ "%D'    group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ) a on a.sd_pran_no=abc.pran_no ");////$t 8-4-2021 deputation KR one file
			sb2.append(" where  cast(abc.c-nvl(a.sd_amnt,0) as double) >=0 and cast(abc.d-nvl(a.sd_emplr_amnt,0) as double) >0 group by abc.DDO_REG_NO order by   abc.DDO_REG_NO  ");

			this.logger.info("query for count" + sb1.toString());
			SQLQuery lQuery2 = this.ghibSession.createSQLQuery(sb2.toString());

			lLstReturnList2 = lQuery2.list();

			HashMap m = new HashMap();
			int icount = empLst.size() + lLstReturnList2.size()
					+ lLstReturnList1.size();
			empCountLst = new String[icount];

			int counter = 0;
			if ((empLst != null) && (empLst.size() > 0)) {
				for (int i = 0; i < empLst.size(); i++) {
					Object[] obj = (Object[]) empLst.get(i);
					if (m.containsKey(obj[1].toString())) {
						String s = m.get(obj[1]).toString();
						Long l = Long.valueOf(Long.parseLong(s)
								+ Long.parseLong(obj[0].toString()));
						empCountLst[counter] = l.toString();
						m.remove(obj[1].toString());
						m.put(obj[1].toString(), empCountLst[counter]);
						counter++;
					} else {
						empCountLst[counter] = obj[0].toString();
						m.put(obj[1].toString(), empCountLst[counter]);
						counter++;
					}
				}
			}
			if ((lLstReturnList1 != null) && (lLstReturnList1.size() > 0)) {
				for (int i = 0; i < lLstReturnList1.size(); i++) {
					Object[] obj = (Object[]) lLstReturnList1.get(i);
					if (m.containsKey(obj[1].toString())) {
						String s = m.get(obj[1]).toString();
						Long l = Long.valueOf(Long.parseLong(s)
								+ Long.parseLong(obj[0].toString()));
						empCountLst[counter] = l.toString();
						m.remove(obj[1].toString());
						m.put(obj[1].toString(), empCountLst[counter]);
						counter++;
					} else {
						empCountLst[counter] = obj[0].toString();
						m.put(obj[1].toString(), empCountLst[counter]);
						counter++;
					}
				}
			}
			if ((lLstReturnList2 != null) && (lLstReturnList2.size() > 0)) {
				for (int i = 0; i < lLstReturnList2.size(); i++) {
					Object[] obj = (Object[]) lLstReturnList2.get(i);
					if (m.containsKey(obj[1].toString())) {
						String s = m.get(obj[1].toString()).toString();
						Long l = Long.valueOf(Long.parseLong(s)
								+ Long.parseLong(obj[0].toString()));
						empCountLst[counter] = l.toString();
						m.remove(obj[1].toString());
						m.put(obj[1].toString(), empCountLst[counter]);
						counter++;
					} else {
						empCountLst[counter] = obj[0].toString();
						m.put(obj[1].toString(), empCountLst[counter]);
						counter++;
					}
				}
			}
			Collection l = null;
			if ((m != null) && (!m.isEmpty())) {
				l = m.values();
				Object[] obj1 = l.toArray();
				if ((obj1 != null) && (obj1.length > 0)) {
					consolidatedList = new String[obj1.length];
					for (int k = 0; k < obj1.length; k++) {
						consolidatedList[k] = obj1[k].toString();
					}
				}
			}
			// this.//ghibSession.disconnect();
		} catch (Exception e) {
			this.logger.info("Error occer in  getEmployeeList ---------" + e);
		}
		return consolidatedList;
	}

	public HashMap getEmployeeCountDdoregNsdlForDeputationMap(String yrCode,
			String month, String treasuryyno, String year,
			String treasuryDDOCode) {
		List empLst = null;
		List lLstReturnList1 = null;
		List lLstReturnList2 = null;
		List finalList = null;
		HashMap m = null;

		String[] empCountLst = null;

		StringBuilder sb = new StringBuilder();
		try {
			sb.append("  select count( distinct abc.PRAN_NO),abc.DDO_REG_NO   from ");
			sb.append(" (select mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as c, cast(sum(nvl(trn.CONTRIBUTION_EMPLR,0) ) as double) as d, reg.ddo_reg_no ");
			sb.append(" FROM MST_DCPS_EMP mstemp inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=mstemp.DCPS_EMP_ID  inner join ORG_DDO_MST ddo on ddo.DDO_CODE=case when substr(mstemp.DDO_CODE,1,2)=substr('"+treasuryyno+"',1,2) then mstemp.DDO_CODE else '"+treasuryDDOCode+"' end inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code ");////$t 24/5/2021 DDO-DTO mapping
			sb.append(" where trn.FIN_YEAR_ID="+ yrCode+ " and trn.MONTH_ID="+ month+" and substr(trn.TREASURY_CODE,1,2)='"+ treasuryyno.substring(0, 2)+ "' " ////$t 8-4-2021 deputation KR one file
					+ "and (trn.IS_CHALLAN='Y' or trn.IS_ARREARS='Y')  and trn.STATUS='H' and trn.IS_DEPUTATION='Y' and mstemp.PRAN_NO is not null and PRAN_ACTIVE=1  ");
			sb.append(" and mstemp.DDO_CODE is not null and mstemp.DEPT_DDO_CODE is null and mstemp.DCPS_OR_GPF='Y' ");
			sb.append(" group by mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, reg.ddo_reg_no) abc ");
			sb.append(" left outer join (    SELECT    sd.SD_PRAN_NO,    cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt , cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt,   bh.YEAR,    bh.MONTH    FROM NSDL_SD_DTLS sd    inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME ");
			sb.append(" and bh.STATUS <>-1    and bh.YEAR=" + year+ " and bh.MONTH=" + month + " and ");
			sb.append(" bh.file_name like '"+ treasuryyno.substring(0, 2)+ "%D'    group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ) a on a.sd_pran_no=abc.pran_no ");////$t 8-4-2021 deputation KR one file
			sb.append(" where  cast(abc.c-nvl(a.sd_amnt,0) as double) >=0 and cast(abc.d-nvl(a.sd_emplr_amnt,0) as double) >0 group by abc.DDO_REG_NO order by   abc.DDO_REG_NO ");

			this.logger.info("query for count" + sb.toString());
			SQLQuery lQuery = this.ghibSession.createSQLQuery(sb.toString());

			empLst = lQuery.list();

			StringBuilder sb1 = new StringBuilder();
			sb1.append("  select count( distinct abc.PRAN_NO),abc.DDO_REG_NO   from ");
			sb1.append(" (select mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as c, cast(sum(nvl(trn.CONTRIBUTION_EMPLR,0) ) as double) as d, reg.ddo_reg_no ");
			sb1.append(" FROM MST_DCPS_EMP mstemp inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=mstemp.DCPS_EMP_ID  inner join ORG_DDO_MST ddo on ddo.DDO_CODE='"+ treasuryDDOCode+ "' inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code ");
			sb1.append(" where trn.FIN_YEAR_ID="+ yrCode+ " and trn.MONTH_ID="+ month +" and substr(trn.TREASURY_CODE,1,2)='"+ treasuryyno.substring(0, 2)+ "' " ////$t 8-4-2021 deputation KR one file
					+ "and (trn.IS_CHALLAN='Y' or trn.IS_ARREARS='Y')  and trn.STATUS='H' and trn.IS_DEPUTATION='Y' and mstemp.PRAN_NO is not null and PRAN_ACTIVE=1  ");
			sb1.append(" and mstemp.DDO_CODE is null and mstemp.DEPT_DDO_CODE is null and mstemp.DCPS_OR_GPF='Y' ");
			sb1.append(" group by mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, reg.ddo_reg_no) abc ");
			sb1.append(" left outer join (    SELECT    sd.SD_PRAN_NO,    cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt , cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt,    bh.YEAR,    bh.MONTH    FROM NSDL_SD_DTLS sd    inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME ");
			sb1.append(" and bh.STATUS <>-1    and bh.YEAR=" + year+ " and bh.MONTH=" + month + " and ");
			sb1.append(" bh.file_name like '"+ treasuryyno.substring(0, 2)+ "%D'    group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ) a on a.sd_pran_no=abc.pran_no ");////$t 8-4-2021 deputation KR one file
			sb1.append(" where  cast(abc.c-nvl(a.sd_amnt,0) as double) >=0 and cast(abc.d-nvl(a.sd_emplr_amnt,0) as double) >0  group by abc.DDO_REG_NO order by   abc.DDO_REG_NO ");

			this.logger.info("query for count" + sb1.toString());
			SQLQuery lQuery1 = this.ghibSession.createSQLQuery(sb1.toString());

			lLstReturnList1 = lQuery1.list();

			StringBuilder sb2 = new StringBuilder();
			sb2.append("  select count( distinct abc.PRAN_NO),abc.DDO_REG_NO   from ");
			sb2.append(" (select mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as c, cast(sum(nvl(trn.CONTRIBUTION_EMPLR,0) ) as double) as d, reg.ddo_reg_no ");
			sb2.append(" FROM MST_DCPS_EMP mstemp inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=mstemp.DCPS_EMP_ID  inner join ORG_DDO_MST ddo on ddo.DDO_CODE= case when substr(mstemp.DEPT_DDO_CODE,1,2)=substr('"+treasuryyno+"',1,2) then mstemp.DEPT_DDO_CODE else '"+treasuryDDOCode+"' end inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code ");////$t 24/5/2021 DDO-DTO mapping
			sb2.append(" where trn.FIN_YEAR_ID="+ yrCode+ " and trn.MONTH_ID="+ month+" and substr(trn.TREASURY_CODE,1,2)='"+ treasuryyno.substring(0, 2)+ "' " ////$t 8-4-2021 deputation KR one file
					+ "and (trn.IS_CHALLAN='Y' or trn.IS_ARREARS='Y')  and trn.STATUS='H' and trn.IS_DEPUTATION='Y' and mstemp.PRAN_NO is not null and PRAN_ACTIVE=1  ");
			sb2.append(" and mstemp.DDO_CODE is null and mstemp.DEPT_DDO_CODE is not null and mstemp.DCPS_OR_GPF='Y' ");
			sb2.append(" group by mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, reg.ddo_reg_no) abc ");
			sb2.append(" left outer join (    SELECT    sd.SD_PRAN_NO,    cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt , cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt,   bh.YEAR,    bh.MONTH    FROM NSDL_SD_DTLS sd    inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME ");
			sb2.append(" and bh.STATUS <>-1    and bh.YEAR=" + year+ " and bh.MONTH=" + month + " and ");
			sb2.append(" bh.file_name like '"+ treasuryyno.substring(0, 2)+ "%D'    group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ) a on a.sd_pran_no=abc.pran_no ");////$t 8-4-2021 deputation KR one file
			sb2.append(" where  cast(abc.c-nvl(a.sd_amnt,0) as double) >=0 and cast(abc.d-nvl(a.sd_emplr_amnt,0) as double) >0 group by abc.DDO_REG_NO order by   abc.DDO_REG_NO  ");

			this.logger.info("query for count" + sb1.toString());
			SQLQuery lQuery2 = this.ghibSession.createSQLQuery(sb2.toString());

			lLstReturnList2 = lQuery2.list();

			m = new HashMap();
			int icount = empLst.size() + lLstReturnList2.size()
					+ lLstReturnList1.size();
			empCountLst = new String[icount];

			int counter = 0;
			if ((empLst != null) && (empLst.size() > 0)) {
				for (int i = 0; i < empLst.size(); i++) {
					Object[] obj = (Object[]) empLst.get(i);
					if (m.containsKey(obj[1].toString())) {
						String s = m.get(obj[1]).toString();
						Long l = Long.valueOf(Long.parseLong(s)
								+ Long.parseLong(obj[0].toString()));
						empCountLst[counter] = l.toString();
						m.remove(obj[1].toString());
						m.put(obj[1].toString(), empCountLst[counter]);
						counter++;
					} else {
						empCountLst[counter] = obj[0].toString();
						m.put(obj[1].toString(), empCountLst[counter]);
						counter++;
					}
				}
			}
			if ((lLstReturnList1 != null) && (lLstReturnList1.size() > 0)) {
				for (int i = 0; i < lLstReturnList1.size(); i++) {
					Object[] obj = (Object[]) lLstReturnList1.get(i);
					if (m.containsKey(obj[1].toString())) {
						String s = m.get(obj[1]).toString();
						Long l = Long.valueOf(Long.parseLong(s)
								+ Long.parseLong(obj[0].toString()));
						empCountLst[counter] = l.toString();
						m.remove(obj[1].toString());
						m.put(obj[1].toString(), empCountLst[counter]);
						counter++;
					} else {
						empCountLst[counter] = obj[0].toString();
						m.put(obj[1].toString(), empCountLst[counter]);
						counter++;
					}
				}
			}
			if ((lLstReturnList2 != null) && (lLstReturnList2.size() > 0)) {
				for (int i = 0; i < lLstReturnList2.size(); i++) {
					Object[] obj = (Object[]) lLstReturnList2.get(i);
					if (m.containsKey(obj[1].toString())) {
						String s = m.get(obj[1]).toString();
						Long l = Long.valueOf(Long.parseLong(s)
								+ Long.parseLong(obj[0].toString()));
						empCountLst[counter] = l.toString();
						m.remove(obj[1].toString());
						m.put(obj[1].toString(), empCountLst[counter]);
						counter++;
					} else {
						empCountLst[counter] = obj[0].toString();
						m.put(obj[1].toString(), empCountLst[counter]);
						counter++;
					}
				}
			}
			// this.//ghibSession.disconnect();
		} catch (Exception e) {
			this.logger.info("Error occer in  getEmployeeList ---------" + e);
		}
		return m;
	}

	public String[] getEmployeeListDdoregNsdl(String yrCode, String month,
			String treasuryyno, int flag) throws HibernateException,
			SQLException {
		List empLst = null;
		String[] empDdoLst = new String[1];

	     ////$t 2020 17-1 year 2020 n month jan added
		if ((yrCode.equals("2019") && (month.equals("10")|| month.equals("11") || month.equals("9")||month.equals("12")))
			||yrCode.equals("2020")
			//||(yrCode.equals("2021") && (month.equals("1") || month.equals("2")))) {
			||(yrCode.equals("2021"))) {////$tC

			List TableData1 = null;
			List TableData2 = null;

			StringBuilder Strbld = new StringBuilder();

			Strbld.append("(select cast(sum(abc.employee) as VARCHAR(20)) ||'#'|| cast(sum(abc.DED_ADJUST) as VARCHAR(20)) from ");
			Strbld.append("(select a.emp_amt-cast(nvl(b.sd_amnt,0) as double) as employee,a.DED_ADJUST-cast(nvl(b.sd_emplr_amnt,0) as double) as DED_ADJUST,a.ddo_reg_no ,a.pran_no from ");
			Strbld.append("(SELECT sum(paybill.DCPS)+sum(paybill.DCPS_PAY)+sum(paybill.DCPS_DELAY)+sum(paybill.DCPS_DA)+sum(paybill.DCPS_PAY_DIFF) as emp_amt,    ");////PayArrearDiff $t 23-2-2021
			// Strbld.append("sum(paybill.DED_ADJUST) as DED_ADJUST,emp.pran_no,reg.ddo_reg_no  FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");
			Strbld.append("sum(paybill.DED_ADJUST) as DED_ADJUST ,emp.pran_no,reg.ddo_reg_no  FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");
			Strbld.append("inner join NSDL_PAYBILL_DATA paybill on paybill.EMP_ID=eis.EMP_ID ");
			Strbld.append("inner join PAYBILL_HEAD_MPG head on head.PAYBILL_ID=paybill.PAYBILL_GRP_ID ");
			Strbld.append("and paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA+paybill.DCPS_PAY_DIFF >paybill.DED_ADJUST  ");////PayArrearDiff $t 23-2-2021
			Strbld.append("inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID ");
			Strbld.append("inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
			Strbld.append("inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2)  ");
			Strbld.append("inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  ");
			//if(treasuryyno.substring(0, 2).equals("12")||treasuryyno.substring(0, 2).equals("46")||treasuryyno.substring(0, 2).equals("23")||treasuryyno.substring(0, 2).equals("51")){////$t KR 5-1-2020
			Strbld.append("where head.PAYBILL_YEAR='" + yrCode
					+ "' and head.PAYBILL_MONTH='" + month
					+ "'  and substr(ddo.ddo_code,1,2)='" + treasuryyno.substring(0,2) + "' ");////$t KR 18-12-2020
			/*}else{
			Strbld.append("where head.PAYBILL_YEAR='" + yrCode
					+ "' and head.PAYBILL_MONTH='" + month
					+ "'  and substr(ddo.ddo_code,1,4)='" + treasuryyno + "' ");	
			}*/
			Strbld.append(" and emp.PRAN_NO is not null and emp.PRAN_ACTIVE=1 and emp.REG_STATUS=1 and head.APPROVE_FLAG=1 group by emp.pran_no,reg.ddo_reg_no  ) a left outer join ");
			Strbld.append("(SELECT sd.SD_PRAN_NO,cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt,cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt ,bh.YEAR,bh.MONTH,sd.ddo_reg_no FROM NSDL_SD_DTLS sd ");
			Strbld.append("inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.STATUS <>-1 ");
			//if(treasuryyno.substring(0, 2).equals("12")||treasuryyno.substring(0, 2).equals("46")||treasuryyno.substring(0, 2).equals("23")||treasuryyno.substring(0, 2).equals("51")){////$t KR 5-1-2020
			Strbld.append("and bh.YEAR='" + yrCode + "' and bh.MONTH=" + month
					+ "  and bh.file_name like '" + treasuryyno.substring(0,2) + "%' and bh.file_name not like '%D' and sd.SD_REMARK like 'Contribution%'");/////$t KR /////$t  25-3-2021
			/*}else{
			Strbld.append("and bh.YEAR='" + yrCode + "' and bh.MONTH=" + month
					+ "  and bh.file_name like '" + treasuryyno + "%'  ");	
			}*/
			Strbld.append(" group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ,sd.ddo_reg_no ) b ");
			Strbld.append("on b.SD_PRAN_NO=a.PRAN_NO and b.ddo_reg_no=a.ddo_reg_no  where a.emp_amt-cast(nvl(b.sd_amnt,0) as double) > 0 and a.DED_ADJUST-cast(nvl(b.sd_emplr_amnt,0) as double) >0) abc group by abc.ddo_reg_no order by abc.ddo_reg_no) ");
            //$t 2019 4/11 
			this.logger.info("   ---------" + Strbld.toString());
			SQLQuery lQuery = this.ghibSession
					.createSQLQuery(Strbld.toString());
			// this.ghibSession.connection().commit();
			// //ghibSession.disconnect();
			// //this.ghibSession.connection().commit();

			this.logger.info("script for all employee ---------"
					+ lQuery.toString());

			TableData1 = lQuery.list();

			// Strbld.append(" union all ");

			StringBuilder Strbld1 = new StringBuilder();

			Strbld1.append("(select cast(sum(abc.employee) as VARCHAR(20)) ||'#'|| cast(sum(abc.DED_ADJUST) as VARCHAR(20)) from ");
			Strbld1.append("(select a.emp_amt-cast(nvl(b.sd_amnt,0) as double) as employee,a.DED_ADJUST-cast(nvl(b.sd_emplr_amnt,0) as double) as DED_ADJUST,a.ddo_reg_no ,a.pran_no from ");
			Strbld1.append("(SELECT cast('0' as double) as emp_amt, ");
			Strbld1.append("cast(SUM(nvl(paybill.emplr_contri_arrears,0))as double)+SUM(nvl(paybill.NPS_EMPLR_DIFFERENCE_ADJ,0))  as DED_ADJUST,emp.pran_no,reg.ddo_reg_no  FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");////$tC
			Strbld1.append("inner join NSDL_PAYBILL_DATA paybill on paybill.EMP_ID=eis.EMP_ID ");
			Strbld1.append("inner join PAYBILL_HEAD_MPG head on head.PAYBILL_ID=paybill.PAYBILL_GRP_ID ");
			Strbld1.append("inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID ");
			Strbld1.append("inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
			Strbld1.append("inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2)  ");
			Strbld1.append("inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  ");
			//if(treasuryyno.substring(0, 2).equals("12")||treasuryyno.substring(0, 2).equals("46")||treasuryyno.substring(0, 2).equals("23")||treasuryyno.substring(0, 2).equals("51")){////$t KR 5-1-2020
			Strbld1.append("where head.PAYBILL_YEAR='" + yrCode
					+ "' and head.PAYBILL_MONTH='" + month
					+ "'  and substr(ddo.ddo_code,1,2)='" + treasuryyno.substring(0,2) + "' ");////$t KR 18-12-2020
			/*}else{
			Strbld1.append("where head.PAYBILL_YEAR='" + yrCode
					+ "' and head.PAYBILL_MONTH='" + month
					+ "'  and substr(ddo.ddo_code,1,4)='" + treasuryyno + "' ");	
			}*/
			Strbld1.append(" and emp.PRAN_NO is not null and emp.PRAN_ACTIVE=1 and emp.REG_STATUS=1 and head.APPROVE_FLAG=1 and (cast((nvl(paybill.emplr_contri_arrears,0)) as double) <> 0  or cast((nvl(paybill.NPS_EMPLR_DIFFERENCE_ADJ,0)) as double) <> 0) group by emp.pran_no,reg.ddo_reg_no  ) a left outer join ");////$tC
			Strbld1.append("(SELECT sd.SD_PRAN_NO,cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt,cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt ,bh.YEAR,bh.MONTH,sd.ddo_reg_no FROM NSDL_SD_DTLS sd ");
			Strbld1.append("inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.STATUS <>-1 ");
			//if(treasuryyno.substring(0, 2).equals("12")||treasuryyno.substring(0, 2).equals("46")||treasuryyno.substring(0, 2).equals("23")||treasuryyno.substring(0, 2).equals("51")){////$t KR 5-1-2020
			Strbld1.append("and bh.YEAR='" + yrCode + "' and bh.MONTH=" + month
					+ "  and bh.file_name like '" + treasuryyno.substring(0,2) + "%' and sd.SD_REMARK like '8 Per Contribution%' ");////$t KR
			/*}else{
			Strbld1.append("and bh.YEAR='" + yrCode + "' and bh.MONTH=" + month
					+ "  and bh.file_name like '" + treasuryyno + "%'  "); 
			}*/
			Strbld1.append(" group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ,sd.ddo_reg_no ) b ");
			Strbld1.append("on b.SD_PRAN_NO=a.PRAN_NO and b.ddo_reg_no=a.ddo_reg_no  where a.emp_amt-cast(nvl(b.sd_amnt,0) as double) >= 0 and a.DED_ADJUST-cast(nvl(b.sd_emplr_amnt,0) as double) >0) abc group by abc.ddo_reg_no order by abc.ddo_reg_no) ");

			this.logger.info("   ---------" + Strbld1.toString());
			SQLQuery lQuery1 = this.ghibSession.createSQLQuery(Strbld1
					.toString());
			this.logger.info("script for all employee ---------"
					+ lQuery1.toString());

			TableData2 = lQuery1.list();

			if ((TableData1 != null && TableData1.size() > 0)
					|| (TableData2 != null && TableData2.size() > 0)) {
				empLst = new ArrayList();
				if (TableData1 != null && TableData1.size() > 0) {
					empLst.addAll(TableData1);

				}
				if (TableData2 != null && TableData2.size() > 0) {
					empLst.addAll(TableData2);
				}

			}

			empDdoLst = new String[empLst.size()];
			for (int i = 0; i < empLst.size(); i++) {
				empDdoLst[i] = empLst.get(i).toString();
			}
		} else {

			StringBuilder Strbld = new StringBuilder();

			Strbld.append("(select cast(sum(abc.employee) as VARCHAR(20)) ||'#'|| cast(sum(abc.DED_ADJUST) as VARCHAR(20)) from ");
			Strbld.append("(select a.emp_amt-cast(nvl(b.sd_amnt,0) as double) as employee,a.DED_ADJUST-cast(nvl(b.sd_emplr_amnt,0) as double) as DED_ADJUST,a.ddo_reg_no ,a.pran_no from ");
			Strbld.append("(SELECT sum(paybill.DCPS)+sum(paybill.DCPS_PAY)+sum(paybill.DCPS_DELAY)+sum(paybill.DCPS_DA)+sum(paybill.DCPS_PAY_DIFF) as emp_amt,    ");////PayArrearDiff $t 23-2-2021
			// Strbld.append("sum(paybill.DED_ADJUST) as DED_ADJUST,emp.pran_no,reg.ddo_reg_no  FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");
			Strbld.append("sum(paybill.DED_ADJUST) as DED_ADJUST ,emp.pran_no,reg.ddo_reg_no  FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");
			Strbld.append("inner join NSDL_PAYBILL_DATA paybill on paybill.EMP_ID=eis.EMP_ID ");
			Strbld.append("inner join PAYBILL_HEAD_MPG head on head.PAYBILL_ID=paybill.PAYBILL_GRP_ID ");
			Strbld.append("and paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA+paybill.DCPS_PAY_DIFF <=paybill.DED_ADJUST  ");////PayArrearDiff $t 23-2-2021
			Strbld.append("inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID ");
			Strbld.append("inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
			Strbld.append("inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2)  ");
			Strbld.append("inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  ");
			//if(treasuryyno.substring(0, 2).equals("12")||treasuryyno.substring(0, 2).equals("46")||treasuryyno.substring(0, 2).equals("23")||treasuryyno.substring(0, 2).equals("51")){////$t KR 5-1-2020
			Strbld.append("where head.PAYBILL_YEAR='" + yrCode
					+ "' and head.PAYBILL_MONTH='" + month
					+ "'  and substr(ddo.ddo_code,1,2)='" + treasuryyno.substring(0,2) + "' ");////$t KR 18-12-2020
			/*}else{
			Strbld.append("where head.PAYBILL_YEAR='" + yrCode
					+ "' and head.PAYBILL_MONTH='" + month
					+ "'  and substr(ddo.ddo_code,1,4)='" + treasuryyno + "' ");	
			}*/
			Strbld.append(" and emp.PRAN_NO is not null and emp.PRAN_ACTIVE=1 and emp.REG_STATUS=1 and head.APPROVE_FLAG=1 group by emp.pran_no,reg.ddo_reg_no  ) a left outer join ");
			Strbld.append("(SELECT sd.SD_PRAN_NO,cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt,cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt ,bh.YEAR,bh.MONTH,sd.ddo_reg_no FROM NSDL_SD_DTLS sd ");
			Strbld.append("inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.STATUS <>-1 ");
			//if(treasuryyno.substring(0, 2).equals("12")||treasuryyno.substring(0, 2).equals("46")||treasuryyno.substring(0, 2).equals("23")||treasuryyno.substring(0, 2).equals("51")){////$t KR 5-1-2020
			Strbld.append("and bh.YEAR='" + yrCode + "' and bh.MONTH=" + month
					+ "  and bh.file_name like '" + treasuryyno.substring(0,2) + "%' and bh.file_name not like '%D' and sd.SD_REMARK like 'Contribution for%' and cast(sd.SD_EMP_AMOUNT as bigint) <= cast(sd.SD_EMPlr_AMOUNT as bigint) ");////$t KR /////$t  25-3-2021
			/*}else{
			Strbld.append("and bh.YEAR='" + yrCode + "' and bh.MONTH=" + month
					+ "  and bh.file_name like '" + treasuryyno + "%' ");	
			}*/
			Strbld.append(" group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ,sd.ddo_reg_no ) b ");
			Strbld.append("on b.SD_PRAN_NO=a.PRAN_NO and b.ddo_reg_no=a.ddo_reg_no  where a.emp_amt-cast(nvl(b.sd_amnt,0) as double) > 0 and a.DED_ADJUST-cast(nvl(b.sd_emplr_amnt,0) as double) >0) abc group by abc.ddo_reg_no order by abc.ddo_reg_no) ");

			this.logger.info(" not oct  ---------" + Strbld.toString());
			SQLQuery lQuery = this.ghibSession
					.createSQLQuery(Strbld.toString());
			this.logger.info("script for all employee not oct ---------"
					+ lQuery.toString());

			empLst = lQuery.list();
			empDdoLst = new String[empLst.size()];
			for (int i = 0; i < empLst.size(); i++) {
				empDdoLst[i] = empLst.get(i).toString();
			}

		}
		// this.//ghibSession.disconnect();
		return empDdoLst;
	}

	public String[] getEmployeeListDdoregNsdlDeputation(String yrCode,
			String month, String treasuryyno, String year,
			String treasuryDDOCode) {
		List empLst = null;
		List lLstReturnList1 = null;
		List lLstReturnList2 = null;

		StringBuilder sb = new StringBuilder();

		sb.append("  select sum(abc.c-nvl(a.sd_amnt,0)),abc.DDO_REG_NO,sum(abc.d-nvl(a.sd_emplr_amnt,0))   from ");
		sb.append(" (select mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as c, cast(sum(nvl(trn.CONTRIBUTION_EMPLR,0) ) as double) as d, reg.ddo_reg_no ");
		sb.append(" FROM MST_DCPS_EMP mstemp inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=mstemp.DCPS_EMP_ID  inner join ORG_DDO_MST ddo on ddo.DDO_CODE=case when substr(mstemp.DDO_CODE,1,2)=substr('"+treasuryyno+"',1,2) then mstemp.DDO_CODE else '"+treasuryDDOCode+"' end inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code ");////$t 24/5/2021 DDO-DTO mapping
		sb.append(" where trn.FIN_YEAR_ID="+ yrCode+ " and trn.MONTH_ID="+ month+" and substr(trn.TREASURY_CODE,1,2)='"+ treasuryyno.substring(0, 2)+ "' " ////$t 8-4-2021 deputation KR one file
				+ "and (trn.IS_CHALLAN='Y' or trn.IS_ARREARS='Y')  and trn.STATUS='H' and trn.IS_DEPUTATION='Y' and mstemp.PRAN_NO is not null and PRAN_ACTIVE=1  ");
		sb.append(" and mstemp.DDO_CODE is not null and mstemp.DEPT_DDO_CODE is null and mstemp.DCPS_OR_GPF='Y' ");
		sb.append(" group by mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, reg.ddo_reg_no) abc ");
		sb.append(" left outer join (    SELECT    sd.SD_PRAN_NO,    cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt , cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt,   bh.YEAR,    bh.MONTH    FROM NSDL_SD_DTLS sd    inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME ");
		sb.append(" and bh.STATUS <>-1    and bh.YEAR=" + year+ " and bh.MONTH=" + month + " and ");
		sb.append(" bh.file_name like '"+ treasuryyno.substring(0, 2)+ "%D'    group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ) a on a.sd_pran_no=abc.pran_no ");////$t 8-4-2021 deputation KR one file
		sb.append(" where  cast(abc.c-nvl(a.sd_amnt,0) as double) >=0  and cast(abc.d-nvl(a.sd_emplr_amnt,0) as double) >0 group by abc.DDO_REG_NO order by   abc.DDO_REG_NO ");

		this.gLogger.info("Query is ***********getEmployeeContriTotalList***"
				+ sb.toString());

		SQLQuery lQuery = this.ghibSession.createSQLQuery(sb.toString());

		empLst = lQuery.list();

		StringBuilder sb1 = new StringBuilder();
		sb1.append("  select sum(abc.c-nvl(a.sd_amnt,0)),abc.DDO_REG_NO,sum(abc.d-nvl(a.sd_emplr_amnt,0))   from ");
		sb1.append(" (select mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as c, cast(sum(nvl(trn.CONTRIBUTION_EMPLR,0) ) as double) as d, reg.ddo_reg_no ");
		sb1.append(" FROM MST_DCPS_EMP mstemp inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=mstemp.DCPS_EMP_ID  inner join ORG_DDO_MST ddo on ddo.DDO_CODE='"+ treasuryDDOCode+ "' inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code ");
		sb1.append(" where trn.FIN_YEAR_ID="+ yrCode+ " and trn.MONTH_ID="+ month+" and substr(trn.TREASURY_CODE,1,2)='"+ treasuryyno.substring(0, 2)+ "' " ////$t 8-4-2021 deputation KR one file
				+ "and (trn.IS_CHALLAN='Y' or trn.IS_ARREARS='Y')  and trn.STATUS='H' and trn.IS_DEPUTATION='Y' and mstemp.PRAN_NO is not null and PRAN_ACTIVE=1  ");
		sb1.append(" and mstemp.DDO_CODE is null and mstemp.DEPT_DDO_CODE is null and mstemp.DCPS_OR_GPF='Y' ");
		sb1.append(" group by mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, reg.ddo_reg_no) abc ");
		sb1.append(" left outer join (    SELECT    sd.SD_PRAN_NO,    cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt ,  cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt,  bh.YEAR,    bh.MONTH    FROM NSDL_SD_DTLS sd    inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME ");
		sb1.append(" and bh.STATUS <>-1    and bh.YEAR=" + year+ " and bh.MONTH=" + month + " and ");
		sb1.append(" bh.file_name like '"+ treasuryyno.substring(0, 2)+ "%D'    group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ) a on a.sd_pran_no=abc.pran_no ");////$t 8-4-2021 deputation KR one file
		sb1.append(" where  cast(abc.c-nvl(a.sd_amnt,0) as double) >=0 and cast(abc.d-nvl(a.sd_emplr_amnt,0) as double) >0 group by abc.DDO_REG_NO order by   abc.DDO_REG_NO  ");

		this.gLogger.info("Query is ***********getEmployeeContriTotalList***"
				+ sb.toString());

		SQLQuery lQuery1 = this.ghibSession.createSQLQuery(sb1.toString());

		lLstReturnList1 = lQuery1.list();

		StringBuilder sb2 = new StringBuilder();
		sb2.append("  select sum(abc.c-nvl(a.sd_amnt,0)),abc.DDO_REG_NO,sum(abc.d-nvl(a.sd_emplr_amnt,0))   from ");
		sb2.append(" (select mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as c, cast(sum(nvl(trn.CONTRIBUTION_EMPLR,0) ) as double) as d, reg.ddo_reg_no ");
		sb2.append(" FROM MST_DCPS_EMP mstemp inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=mstemp.DCPS_EMP_ID  inner join ORG_DDO_MST ddo on ddo.DDO_CODE= case when substr(mstemp.DEPT_DDO_CODE,1,2)=substr('"+treasuryyno+"',1,2) then mstemp.DEPT_DDO_CODE else '"+treasuryDDOCode+"' end inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code ");////$t 24/5/2021 DDO-DTO mapping
		sb2.append(" where trn.FIN_YEAR_ID="+ yrCode+ " and trn.MONTH_ID="+ month+" and substr(trn.TREASURY_CODE,1,2)='"+ treasuryyno.substring(0, 2)+ "' " ////$t 8-4-2021 deputation KR one file
				+ "and (trn.IS_CHALLAN='Y' or trn.IS_ARREARS='Y')  and trn.STATUS='H' and trn.IS_DEPUTATION='Y' and mstemp.PRAN_NO is not null and PRAN_ACTIVE=1  ");
		sb2.append(" and mstemp.DDO_CODE is null and mstemp.DEPT_DDO_CODE is not null and mstemp.DCPS_OR_GPF='Y' ");
		sb2.append(" group by mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, reg.ddo_reg_no) abc ");
		sb2.append(" left outer join (    SELECT    sd.SD_PRAN_NO,    cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt ,cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt,    bh.YEAR,    bh.MONTH    FROM NSDL_SD_DTLS sd    inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME ");
		sb2.append(" and bh.STATUS <>-1    and bh.YEAR=" + year+ " and bh.MONTH=" + month + " and ");
		sb2.append(" bh.file_name like '"+ treasuryyno.substring(0, 2)+ "%D'    group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ) a on a.sd_pran_no=abc.pran_no ");////$t 8-4-2021 deputation KR one file
		sb2.append(" where  cast(abc.c-nvl(a.sd_amnt,0) as double) >=0 and cast(abc.d-nvl(a.sd_emplr_amnt,0) as double) >0 group by abc.DDO_REG_NO order by   abc.DDO_REG_NO ");

		this.gLogger.info("Query is ***********getEmployeeContriTotalList***"
				+ sb.toString());

		SQLQuery lQuery2 = this.ghibSession.createSQLQuery(sb2.toString());

		lLstReturnList2 = lQuery2.list();

		int icount = empLst.size() + lLstReturnList2.size()
				+ lLstReturnList1.size();

		String[] empDdoLst = new String[icount];
		int counter = 0;
		HashMap m = new HashMap();
		if ((empLst != null) && (empLst.size() > 0)) {
			for (int i = 0; i < empLst.size(); i++) {
				Object[] obj = (Object[]) empLst.get(i);
				if (m.containsKey(obj[1].toString())) {
					String s = m.get(obj[1]).toString();
					Double d = Double.valueOf(Double.parseDouble(s)
							+ Double.parseDouble(obj[0].toString()));
					empDdoLst[counter] = d.toString();
					m.remove(obj[1].toString());
					m.put(obj[1].toString(), empDdoLst[counter]);
					counter++;
				} else {
					empDdoLst[counter] = obj[0].toString();
					m.put(obj[1].toString(), empDdoLst[counter]);
					counter++;
				}
			}
		}
		if ((lLstReturnList1 != null) && (lLstReturnList1.size() > 0)) {
			for (int i = 0; i < lLstReturnList1.size(); i++) {
				Object[] obj = (Object[]) lLstReturnList1.get(i);
				if (m.containsKey(obj[1].toString())) {
					String s = m.get(obj[1]).toString();
					Double d = Double.valueOf(Double.parseDouble(s)
							+ Double.parseDouble(obj[0].toString()));
					empDdoLst[counter] = d.toString();
					m.remove(obj[1].toString());
					m.put(obj[1].toString(), empDdoLst[counter]);
					counter++;
				} else {
					empDdoLst[counter] = obj[0].toString();
					m.put(obj[1].toString(), empDdoLst[counter]);
					counter++;
				}
			}
		}
		if ((lLstReturnList2 != null) && (lLstReturnList2.size() > 0)) {
			for (int i = 0; i < lLstReturnList2.size(); i++) {
				Object[] obj = (Object[]) lLstReturnList2.get(i);
				if (m.containsKey(obj[1].toString())) {
					String s = m.get(obj[1]).toString();
					Double d = Double.valueOf(Double.parseDouble(s)
							+ Double.parseDouble(obj[0].toString()));
					empDdoLst[counter] = d.toString();
					m.remove(obj[1].toString());
					m.put(obj[1].toString(), empDdoLst[counter]);
					counter++;
				} else {
					empDdoLst[counter] = obj[0].toString();
					m.put(obj[1].toString(), empDdoLst[counter]);
					counter++;
				}
			}
		}
		String[] consolidatedList = null;

		Collection l = null;
		if ((m != null) && (!m.isEmpty())) {
			l = m.values();
			Object[] obj1 = l.toArray();
			if ((obj1 != null) && (obj1.length > 0)) {
				consolidatedList = new String[obj1.length];
				for (int k = 0; k < obj1.length; k++) {
					consolidatedList[k] = obj1[k].toString();
				}
			}
		}
		// this.//ghibSession.disconnect();
		return consolidatedList;
	}

	public HashMap getEmployeeListDdoregNsdlDeputationMap(String yrCode,
			String month, String treasuryyno, String year,
			String treasuryDDOCode) {
		List empLst = null;
		List lLstReturnList1 = null;
		List lLstReturnList2 = null;

		StringBuilder sb = new StringBuilder();

		sb.append("  select sum(abc.c-nvl(a.sd_amnt,0)),abc.DDO_REG_NO,sum(abc.d-nvl(a.sd_emplr_amnt,0))   from ");
		sb.append(" (select mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as c, cast(sum(nvl(trn.CONTRIBUTION_EMPLR,0) ) as double) as d, reg.ddo_reg_no ");
		sb.append(" FROM MST_DCPS_EMP mstemp inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=mstemp.DCPS_EMP_ID  inner join ORG_DDO_MST ddo on ddo.DDO_CODE=case when substr(mstemp.DDO_CODE,1,2)=substr('"+treasuryyno+"',1,2) then mstemp.DDO_CODE else '"+treasuryDDOCode+"' end inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code ");////$t 24/5/2021 DDO-DTO mapping
		sb.append(" where trn.FIN_YEAR_ID="+ yrCode+ " and trn.MONTH_ID="+ month+" and substr(trn.TREASURY_CODE,1,2)='"+ treasuryyno.substring(0, 2)+ "' " ////$t 8-4-2021 deputation KR one file
				+ "and (trn.IS_CHALLAN='Y' or trn.IS_ARREARS='Y') and trn.STATUS='H' and trn.IS_DEPUTATION='Y' and mstemp.PRAN_NO is not null and PRAN_ACTIVE=1  ");
		sb.append(" and mstemp.DDO_CODE is not null and mstemp.DEPT_DDO_CODE is null and mstemp.DCPS_OR_GPF='Y' ");
		sb.append(" group by mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, reg.ddo_reg_no) abc ");
		sb.append(" left outer join (    SELECT    sd.SD_PRAN_NO,    cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt , cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt,   bh.YEAR,    bh.MONTH    FROM NSDL_SD_DTLS sd    inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME ");
		sb.append(" and bh.STATUS <>-1    and bh.YEAR=" + year+ " and bh.MONTH=" + month + " and ");
		sb.append(" bh.file_name like '"+ treasuryyno.substring(0, 2)+ "%D'    group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ) a on a.sd_pran_no=abc.pran_no "); ////$t 8-4-2021 deputation KR one file
		sb.append(" where  cast(abc.c-nvl(a.sd_amnt,0) as double) >=0 and cast(abc.d-nvl(a.sd_emplr_amnt,0) as double) >0  group by abc.DDO_REG_NO order by   abc.DDO_REG_NO ");

		this.gLogger.info("Query is ***********getEmployeeContriTotalList***"
				+ sb.toString());

		SQLQuery lQuery = this.ghibSession.createSQLQuery(sb.toString());

		empLst = lQuery.list();
		// $t 2019
		// sb1.append("  select cast(sum(abc.c-nvl(a.sd_amnt,0))as BIGINT) ,abc.DDO_REG_NO,cast(sum(abc.d-nvl(a.sd_emplr_amnt,0))as BIGINT)  from ");
		StringBuilder sb1 = new StringBuilder();
		sb1.append("  select sum(abc.c-nvl(a.sd_amnt,0)),abc.DDO_REG_NO,sum(abc.d-nvl(a.sd_emplr_amnt,0))   from ");
		sb1.append(" (select mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as c, cast(sum(nvl(trn.CONTRIBUTION_EMPLR,0) ) as double) as d, reg.ddo_reg_no ");
		sb1.append(" FROM MST_DCPS_EMP mstemp inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=mstemp.DCPS_EMP_ID  inner join ORG_DDO_MST ddo on ddo.DDO_CODE='"+ treasuryDDOCode+ "' inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code ");
		sb1.append(" where trn.FIN_YEAR_ID="+ yrCode+ " and trn.MONTH_ID="+ month+" and substr(trn.TREASURY_CODE,1,2)='"+ treasuryyno.substring(0, 2)+ "' " ////$t 8-4-2021 deputation KR one file
				+ "and (trn.IS_CHALLAN='Y' or trn.IS_ARREARS='Y') and trn.STATUS='H' and trn.IS_DEPUTATION='Y' and mstemp.PRAN_NO is not null and PRAN_ACTIVE=1  ");
		sb1.append(" and mstemp.DDO_CODE is null and mstemp.DEPT_DDO_CODE is null and mstemp.DCPS_OR_GPF='Y' ");
		sb1.append(" group by mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, reg.ddo_reg_no) abc ");
		sb1.append(" left outer join (    SELECT    sd.SD_PRAN_NO,    cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt , cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt,   bh.YEAR,    bh.MONTH    FROM NSDL_SD_DTLS sd    inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME ");
		sb1.append(" and bh.STATUS <>-1    and bh.YEAR=" + year+ " and bh.MONTH=" + month + " and ");
		sb1.append(" bh.file_name like '"+ treasuryyno.substring(0, 2)+ "%D'    group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ) a on a.sd_pran_no=abc.pran_no "); ////$t 8-4-2021 deputation KR one file
		sb1.append(" where  cast(abc.c-nvl(a.sd_amnt,0) as double) >=0  and cast(abc.d-nvl(a.sd_emplr_amnt,0) as double) >0 group by abc.DDO_REG_NO order by   abc.DDO_REG_NO  ");

		this.gLogger.info("Query is ***********getEmployeeContriTotalList***"
				+ sb.toString());

		SQLQuery lQuery1 = this.ghibSession.createSQLQuery(sb1.toString());

		lLstReturnList1 = lQuery1.list();

		StringBuilder sb2 = new StringBuilder();
		sb2.append("  select sum(abc.c-nvl(a.sd_amnt,0)),abc.DDO_REG_NO,sum(abc.d-nvl(a.sd_emplr_amnt,0))   from ");
		sb2.append(" (select mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as c, cast(sum(nvl(trn.CONTRIBUTION_EMPLR,0) ) as double) as d, reg.ddo_reg_no ");
		sb2.append(" FROM MST_DCPS_EMP mstemp inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=mstemp.DCPS_EMP_ID  inner join ORG_DDO_MST ddo on ddo.DDO_CODE= case when substr(mstemp.DEPT_DDO_CODE,1,2)=substr('"+treasuryyno+"',1,2) then mstemp.DEPT_DDO_CODE else '"+treasuryDDOCode+"' end inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code ");////$t 24/5/2021 DDO-DTO mapping
		sb2.append(" where trn.FIN_YEAR_ID="+ yrCode+ " and trn.MONTH_ID="+ month+" and substr(trn.TREASURY_CODE,1,2)='"+ treasuryyno.substring(0, 2)+ "' " ////$t 8-4-2021 deputation KR one file
				+ "and (trn.IS_CHALLAN='Y' or trn.IS_ARREARS='Y') and trn.STATUS='H' and trn.IS_DEPUTATION='Y' and mstemp.PRAN_NO is not null and PRAN_ACTIVE=1  ");
		sb2.append(" and mstemp.DDO_CODE is null and mstemp.DEPT_DDO_CODE is not null and mstemp.DCPS_OR_GPF='Y' ");
		sb2.append(" group by mstemp.EMP_NAME, mstemp.DCPS_ID, mstemp.PRAN_NO, reg.ddo_reg_no) abc ");
		sb2.append(" left outer join (    SELECT    sd.SD_PRAN_NO,    cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt ,cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt,    bh.YEAR,    bh.MONTH    FROM NSDL_SD_DTLS sd    inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME ");
		sb2.append(" and bh.STATUS <>-1    and bh.YEAR=" + year+ " and bh.MONTH=" + month + " and ");
		sb2.append(" bh.file_name like '"+ treasuryyno.substring(0, 2)+ "%D'    group by sd.SD_PRAN_NO,bh.YEAR,bh.MONTH ) a on a.sd_pran_no=abc.pran_no "); ////$t 8-4-2021 deputation KR one file
		sb2.append(" where  cast(abc.c-nvl(a.sd_amnt,0) as double) >=0 and cast(abc.d-nvl(a.sd_emplr_amnt,0) as double) >0 group by abc.DDO_REG_NO order by   abc.DDO_REG_NO ");

		this.gLogger.info("Query is ***********getEmployeeContriTotalList***"
				+ sb.toString());

		SQLQuery lQuery2 = this.ghibSession.createSQLQuery(sb2.toString());

		lLstReturnList2 = lQuery2.list();

		int icount = empLst.size() + lLstReturnList2.size()
				+ lLstReturnList1.size();

		String[] empDdoLst = new String[icount];
		int counter = 0;
		HashMap m = new HashMap();
		if ((empLst != null) && (empLst.size() > 0)) {
			for (int i = 0; i < empLst.size(); i++) {
				Object[] obj = (Object[]) empLst.get(i);
				if (m.containsKey(obj[1].toString())) {
					String s = m.get(obj[1]).toString();
					
					Double d = Double.valueOf(Double.parseDouble(s)
							+ Double.parseDouble(obj[0].toString()));
					
					empDdoLst[counter] = d.toString();
					
					m.remove(obj[1].toString());
					m.put(obj[1].toString(), empDdoLst[counter]);
					counter++;
				} else {
					
					empDdoLst[counter] = obj[0].toString() + "-"
							+ obj[2].toString();
					
					m.put(obj[1].toString(), empDdoLst[counter]);
					counter++;
				}
			}
		}
		
		if ((lLstReturnList1 != null) && (lLstReturnList1.size() > 0)) {
			for (int i = 0; i < lLstReturnList1.size(); i++) {
				Object[] obj = (Object[]) lLstReturnList1.get(i);
				if (m.containsKey(obj[1].toString())) {
					String s = m.get(obj[1]).toString();
					String[] values = s.split("-");
					
					Double d = Double.valueOf(Double.parseDouble(values[0])
							+ Double.parseDouble(obj[0].toString()));
					Double d1 = Double.valueOf(Double.parseDouble(values[1])
							+ Double.parseDouble(obj[2].toString()));
					
					empDdoLst[counter] = d.toString() + "-" + d1.toString();
					m.remove(obj[1].toString());
					m.put(obj[1].toString(), empDdoLst[counter]);
					counter++;
				} else {
					empDdoLst[counter] = obj[0].toString() + "-"
							+ obj[2].toString();
					m.put(obj[1].toString(), empDdoLst[counter]);
					counter++;
				}
			}
		}
		if ((lLstReturnList2 != null) && (lLstReturnList2.size() > 0)) {
			for (int i = 0; i < lLstReturnList2.size(); i++) {
				Object[] obj = (Object[]) lLstReturnList2.get(i);
				
				if (m.containsKey(obj[1].toString())) {
					String s = m.get(obj[1]).toString();
					String[] values = s.split("-");
				
					Double d = Double.valueOf(Double.parseDouble(values[0])
							+ Double.parseDouble(obj[0].toString()));
					Double d1 = Double.valueOf(Double.parseDouble(values[1])
							+ Double.parseDouble(obj[2].toString()));
				
					empDdoLst[counter] = d.toString() + "-" + d1.toString();

					m.remove(obj[1].toString());
					m.put(obj[1].toString(), empDdoLst[counter]);
					counter++;
				} else {
					empDdoLst[counter] = obj[0].toString() + "-"
							+ obj[2].toString();
					m.put(obj[1].toString(), empDdoLst[counter]);
					counter++;
				}
			}
		}
		// this.//ghibSession.disconnect();
		return m;
	}

	public List selectTrnPk(String yrCode, String month, String treasuryyno)
			throws Exception {
		List contrList = null;
		Query lQuery = null;
		StringBuilder lSBQuery = null;
		try {
			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT distinct emp.DCPS_ID   FROM TEMPEMPR3 temp inner join tempr3 r3 on r3.EMP_ID_NO=temp.EMP_ID_NO and temp.FIN_YEAR=r3.FIN_YEAR    ");
			lSBQuery.append("  inner join SGVA_MONTH_MST mo on mo.MONTH_ID = r3.PAY_MONTH  and mo.LANG_ID = 'en_US'    ");
			lSBQuery.append(" inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO      ");
			lSBQuery.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2)  ");
			lSBQuery.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003  ");
			lSBQuery.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE    ");
			lSBQuery.append(" where temp.FIN_YEAR='"
					+ yrCode
					+ "' and loc.loc_id ='"
					+ treasuryyno
					+ "' and emp.pran_no is not null and temp.INT_EMPL_CONTRIB<>0 and  temp.BATCH_ID is null ");
			lSBQuery.append(" AND emp.PRAN_NO not in (SELECT sd.SD_PRAN_NO FROM NSDL_SD_DTLS sd inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.STATUS <>-1 and bh.YEAR="
					+ yrCode
					+ " and bh.MONTH="
					+ month
					+ " and bh.file_name like '" + treasuryyno + "%') ");
			lSBQuery.append(" group by emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no,loc.loc_id order by reg.DDO_REG_NO ");

			lQuery = this.ghibSession.createSQLQuery(lSBQuery.toString());

			contrList = lQuery.list();
			// this.//ghibSession.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			this.gLogger.error("Error is :" + e, e);
			throw e;
		}
		return contrList;
	}

	public void updateRepStatus(String dcpsContriIdPks, String batchId,
			String yrCode, String month, String treasuryyno) {
	}

	public void insertBatchHeader(String bhHeader1, String bhHeader,
			String bhHeader2, String string4, String string5,
			String currentdate, String string6, long ddoCount, int count,
			String govContri, String subContri, String total, String fileName,
			String yrCode, String month) {
		Session session = getSession();
		StringBuffer str = new StringBuffer();
		str.append("insert into NSDL_BH_dtls values ('" + bhHeader1 + "','"
				+ bhHeader + "','" + bhHeader2 + "','" + string4 + "','"
				+ string5 + "','" + currentdate + "',");
		str.append("'" + string6 + "','" + ddoCount + "','" + count + "','"
				+ subContri + "','" + govContri + "','" + total + "','"
				+ fileName + "','" + yrCode + "','" + month + "','" + 0
				//+ "',null,0,null,null,null,null,null,'0',null,null,null,null,sysdate)");/////$t new column TRANSACTION_UPDATE_DATE
				+ "',null,0,null,null,null,null,null,'0',null,null,null,null)");
		Query updateQuery = session.createSQLQuery(str.toString());
		this.logger.info("Query to insert in batch heaqder**********"
				+ str.toString());

		updateQuery.executeUpdate();
	}

	public String getbatchIdCount(String batchIdPrefix) {
		String lLstReturnList = "";
		StringBuilder sb = new StringBuilder();
		this.logger.info("getbatchIdCount1");

		sb.append(" select count(1)+1 from NSDL_BH_dtls where FILE_NAME like '"
				+ batchIdPrefix + "%'");
		Query selectQuery = this.ghibSession.createSQLQuery(sb.toString());
		lLstReturnList = selectQuery.uniqueResult().toString();
		this.logger.info("getbatchIdCount1" + lLstReturnList);

		return lLstReturnList;
	}

	public void insertDHDetails(int i, String string, String string2, int j,
			String ddoRegNo, Long empCount, String totalEmplyDHContri,
			String totalEmplyerDHContri, String batchId) {
		Session session = getSession();
		StringBuffer str = new StringBuffer();
		str.append("insert into NSDL_DH_dtls values ('" + i + "','" + string
				+ "','" + string2 + "','" + j + "','" + ddoRegNo + "','"
				+ empCount + "',");
		str.append("'" + totalEmplyDHContri + "','" + totalEmplyerDHContri
				+ "','" + batchId + "',0)");
		Query updateQuery = session.createSQLQuery(str.toString());
		this.logger.info("Query to insert in insertDHDetails heaqder**********"
				+ str.toString());

		updateQuery.executeUpdate();
	}

	public void insertSDDetails(int i, String string, String string2, int j,
			int empCount, String pranno, String govEmpContri,
			String subempContri, String string3, String string4,
			String batchId, String string5, String ddoRegNo)
			throws HibernateException, SQLException {
		Session session = getSession();
		StringBuffer str = new StringBuffer();
		str.append("insert into NSDL_SD_dtls values ('" + i + "','" + string
				+ "','" + string2 + "','" + j + "','" + empCount + "','"
				+ pranno + "',");
		str.append("'" + govEmpContri + "','" + subempContri + "','" + string3
				+ "','" + string4 + "','" + batchId + "','" + string5 + "','"
				+ ddoRegNo + "',0)");
		Query updateQuery = session.createSQLQuery(str.toString());
		this.logger.info("Query to insert in insertSDDetails heaqder**********"
				+ str.toString());

		updateQuery.executeUpdate();
		// session.connection().commit();
		// session.disconnect();

	}

	public List getAllData(String yrCode, String month, String strLocationCode)
			throws Exception {
		List contrList = null;
		Query lQuery = null;
		StringBuilder lSBQuery = null;
		try {
			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT distinct bh.FILE_NAME,bh.BH_EMP_AMOUNT,bh.BH_EMPLR_AMOUNT,NVL(cast(nsdl.BILL_ID as varchar(20)),'NA'),NVL(cast(nsdl.AUTH_NUMBER as varchar(20)),'NA') , decode(nsdl.BILL_STATUS,0,'PayBill Generated',1,'Paybill Approved',2,'Bill Rejected by Beams',3,'Bill Authorized by BEAMS','Paybill Not Generated')  , NVL(bh.TRANSACTION_ID,'-') ,   decode(bh.file_status,0,'File not Validated',1,'File is validated',2,'File is rejected',3,'File has been modified by User',5,'File has been sent to NSDL',11,'Transaction Id Updated',8,'Transaction Id Lapsed') ,nsdl.BILL_STATUS,bh.file_status,decode(NSDL_RESPONSE_HTML,null,1,2)  FROM NSDL_BH_DTLS bh   ");

			lSBQuery.append("  left outer join ifms.NSDL_BILL_DTLS nsdl on nsdl.FILE_NAME=bh.FILE_NAME  and nsdl.BILL_STATUS <> -1  where bh.MONTH='"
					+ month
					+ "' and bh.YEAR= '"
					+ yrCode
					+ "' "
					+ "and bh.file_name like '"
					+ strLocationCode
					+ "%' and bh.STATUS <> -1   ");

			lQuery = this.ghibSession.createSQLQuery(lSBQuery.toString());
			this.logger.info("lQuery*******is to get the list" + lQuery);
			contrList = lQuery.list();

			// //this.ghibSession.connection().commit();
			// this.//ghibSession.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			this.gLogger.error("Error is :" + e, e);
			throw e;
		}
		return contrList;
	}

	public List getAllDataDeputation(String yrCode, String month,
			String strLocationCode) throws Exception {
		List contrList = null;
		Query lQuery = null;
		StringBuilder lSBQuery = null;
		try {
			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT distinct bh.FILE_NAME,bh.BH_EMP_AMOUNT,bh.BH_EMPLR_AMOUNT,NVL(cast(nsdl.BILL_ID as varchar(20)),'NA'),NVL(cast(nsdl.AUTH_NUMBER as varchar(20)),'NA') , decode(nsdl.BILL_STATUS,0,'PayBill Generated',1,'Paybill Approved',2,'Bill Rejected by Beams',3,'Bill Authorized by BEAMS','Paybill Not Generated')  , NVL(bh.TRANSACTION_ID,'-') ,   decode(bh.file_status,0,'File not Validated',1,'File is validated',2,'File is rejected',3,'File has been modified by User',5,'File has been sent to NSDL',11,'Transaction Id Updated') ,nsdl.BILL_STATUS,bh.file_status,decode(NSDL_RESPONSE_HTML,null,1,2)  FROM NSDL_BH_DTLS bh   ");

			lSBQuery.append("  left outer join ifms.NSDL_BILL_DTLS nsdl on nsdl.FILE_NAME=bh.FILE_NAME  and nsdl.BILL_STATUS <> -1  where bh.MONTH='"
					+ month
					+ "' and bh.YEAR= '"
					+ yrCode
					+ "' "
					+ "and bh.file_name like '"
					+ strLocationCode
					+ "%D' and bh.STATUS <> -1   ");

			lQuery = this.ghibSession.createSQLQuery(lSBQuery.toString());
			this.logger.info("lQuery*******is to get the list" + lQuery);
			contrList = lQuery.list();
			// this.//ghibSession.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			this.gLogger.error("Error is :" + e, e);
			throw e;
		}
		return contrList;
	}

	public List getBHDeatils(String fileNumber) {
		return null;
	}

	public String getBatchData(String fileNumber) {
		String lLstReturnList = "";
		StringBuilder sb = new StringBuilder();

		sb.append(" select SR_NO||'^'||HEADER_NAME||'^'||BH_NO||'^'||BH_COL2||'^'||BH_FIX_NO||'^'||BH_DATE||'^'||BH_BATCH_FIX_ID||'^^'||BH_DDO_COUNT||'^'||BH_PRAN_COUNT||'^'||BH_EMPLR_AMOUNT||'^'||BH_EMP_AMOUNT||'^^'||BH_TOTAL_AMT||'^' from NSDL_BH_dtls ");
		sb.append(" where FILE_NAME='" + fileNumber + "' ");

		Query selectQuery = this.ghibSession.createSQLQuery(sb.toString());
		lLstReturnList = selectQuery.uniqueResult().toString();
		// this.//ghibSession.disconnect();
		return lLstReturnList;

	}

	public List getDHData(String fileNumber) {
		List lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT SR_NO||'^'||HEADER_NAME||'^'||DH_NO||'^'||DH_COL2||'^'||DH_DDO_REG_NO||'^'||BH_SD_COUNT||'^'||DH_EMPLR_AMOUNT||'^'||DH_EMP_AMOUNT||'^^',DH_DDO_REG_NO FROM NSDL_DH_dtls ");
		sb.append(" where FILE_NAME='" + fileNumber
				+ "' and DH_EMP_AMOUNT > 0 order by SR_NO asc");

		Query selectQuery = this.ghibSession.createSQLQuery(sb.toString());
		lLstReturnList = selectQuery.list();
		// this.//ghibSession.disconnect();
		return lLstReturnList;
	}

	public List getDHData1(String fileNumber) {
		List lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT SR_NO||'^'||HEADER_NAME||'^'||DH_NO||'^'||DH_COL2||'^'||DH_DDO_REG_NO||'^'||BH_SD_COUNT||'^'||DH_EMPLR_AMOUNT||'^'||DH_EMP_AMOUNT||'^^',DH_DDO_REG_NO FROM NSDL_DH_dtls ");
		sb.append(" where FILE_NAME='" + fileNumber
				+ "' and DH_EMP_AMOUNT = 0 order by SR_NO asc");

		Query selectQuery = this.ghibSession.createSQLQuery(sb.toString());
		lLstReturnList = selectQuery.list();
		// this.//ghibSession.disconnect();
		return lLstReturnList;
	}
	//$t 6-11 2019 remove condition(DH_EMP_AMOUNT = 0)
	public List getDHDataDeputation(String fileNumber) {
		List lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT SR_NO||'^'||HEADER_NAME||'^'||DH_NO||'^'||DH_COL2||'^'||DH_DDO_REG_NO||'^'||BH_SD_COUNT||'^'||DH_EMPLR_AMOUNT||'^'||DH_EMP_AMOUNT||'^^',DH_DDO_REG_NO FROM NSDL_DH_dtls ");
		sb.append(" where FILE_NAME='" + fileNumber+ "'  order by SR_NO asc");
		Query selectQuery = this.ghibSession.createSQLQuery(sb.toString());
		lLstReturnList = selectQuery.list();
		// this.//ghibSession.disconnect();
		return lLstReturnList;
	}
	
	public List getSDDtls(String fileNumber, String ddoRegNo) {
		List lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT SR_NO||'^'||HEADER_NAME||'^'||SD_NO||'^'||SD_NO_2||'^'||SD_NO_3||'^'||SD_PRAN_NO||'^'||SD_EMPLR_AMOUNT||'^'||SD_EMP_AMOUNT||'^'||'^'||SD_TOTAL_AMT||'^'||SD_STATUS||'^'||SD_REMARK||'^' FROM NSDL_SD_DTLS  ");
		sb.append(" where   FILE_NAME='" + fileNumber + "'and DDO_REG_NO='"
				+ ddoRegNo + "' and SD_EMP_AMOUNT > 0 order by SR_NO asc ");

		Query selectQuery = this.ghibSession.createSQLQuery(sb.toString());
		lLstReturnList = selectQuery.list();
		// this.//ghibSession.disconnect();
		return lLstReturnList;
	}

	//$t 6-11 2019 remove condition and SD_EMP_AMOUNT > 0
	public List getSDDtlsDeputation(String fileNumber, String ddoRegNo) {
		List lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT SR_NO||'^'||HEADER_NAME||'^'||SD_NO||'^'||SD_NO_2||'^'||SD_NO_3||'^'||SD_PRAN_NO||'^'||SD_EMPLR_AMOUNT||'^'||SD_EMP_AMOUNT||'^'||'^'||SD_TOTAL_AMT||'^'||SD_STATUS||'^'||SD_REMARK||'^' FROM NSDL_SD_DTLS  ");
		sb.append(" where   FILE_NAME='" + fileNumber + "' and DDO_REG_NO='"
				+ ddoRegNo + "'  order by SR_NO asc ");

		Query selectQuery = this.ghibSession.createSQLQuery(sb.toString());
		lLstReturnList = selectQuery.list();
		// this.//ghibSession.disconnect();
		return lLstReturnList;
	}

	
	public void deleteNsdlFile(String fileNumber) throws HibernateException,
			SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("  update NSDL_BH_DTLS set status=-1 where FILE_NAME='"
				+ fileNumber + "' ");
		Query updateQuery = this.ghibSession.createSQLQuery(sb.toString());

		this.logger.info("Query to delete in deleteNsdlFile heaqder**********"
				+ sb.toString());

		updateQuery.executeUpdate();

		StringBuilder sb1 = new StringBuilder();
		sb1.append("  update NSDL_DH_DTLS set DH_status=-1 where FILE_NAME='"
				+ fileNumber + "' ");
		Query updateQuery1 = this.ghibSession.createSQLQuery(sb1.toString());

		this.logger.info("Query to delete in deleteNsdlFile heaqder**********"
				+ sb1.toString());

		updateQuery1.executeUpdate();

		StringBuilder sb2 = new StringBuilder();
		sb2.append("  update NSDL_SD_DTLS set status=-1 where FILE_NAME='"
				+ fileNumber + "' ");
		Query updateQuery2 = this.ghibSession.createSQLQuery(sb2.toString());

		this.logger.info("Query to delete in deleteNsdlFile heaqder**********"
				+ sb2.toString());

		updateQuery2.executeUpdate();

		// this.ghibSession.connection().commit();
		// this.//ghibSession.disconnect();
	}

	public void updateVoucherEntry(String month, String year,
			String fileNumber, String voucherNo, String vouchedate)
			throws HibernateException, SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("  update ifms.NSDL_BILL_dtls  set BILL_STATUS=1,VOUCHER_NO="
				+ voucherNo + ",VOUCHER_DATE='" + vouchedate
				+ "' where FILE_NAME='" + fileNumber + "' ");
		Query updateQuery = this.ghibSession.createSQLQuery(sb.toString());

		this.logger.info("Query to updateVoucherEntry**********"
				+ sb.toString());

		updateQuery.executeUpdate();
		// this.ghibSession.connection().commit();
		// this.//ghibSession.disconnect();
	}

	public void createNSDLBillGenration(Long nsdl_paybill_pk, String year,
			String month, double employeeContribution,
			double employerContribution, double totalContribution,
			String fileNumber) throws HibernateException, SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("  INSERT INTO NSDL_BILL_dtls  VALUES (" + nsdl_paybill_pk
				+ "," + year + "," + month + "," + employeeContribution + ","
				+ employerContribution + "," + totalContribution
				+ ",null,null,'0',sysdate,'" + fileNumber + "',null) ");
		Query updateQuery = this.ghibSession.createSQLQuery(sb.toString());

		this.logger.info("Query to createNSDLBillGenration**********"
				+ sb.toString());

		updateQuery.executeUpdate();
		// this.ghibSession.connection().commit();
		// this.//ghibSession.disconnect();
	}

	public String getBillStatus(String fileNumber) {
		List temp = null;
		String billStatus = "";
		StringBuilder Strbld = new StringBuilder();

		Strbld.append(" SELECT count(1) FROM NSDL_BILL_dtls where  FILE_NAME='"
				+ fileNumber + "' and bill_status<>-1 ");

		SQLQuery lQuery = this.ghibSession.createSQLQuery(Strbld.toString());

		billStatus = lQuery.list().get(0).toString();
		// this.//ghibSession.disconnect();
		return billStatus;
	}

	public List getBillNoDate(String fileNumber) {
		List billDetails = null;
		Long billNo = Long.valueOf(0L);
		StringBuilder Strbld = new StringBuilder();

		Strbld.append(" SELECT BILL_ID,to_char(BILL_GENERATION_DATE,'dd/mm/yyyy'),month(BILL_GENERATION_DATE),BILL_MONTH FROM ifms.NSDL_BILL_dtls where  FILE_NAME='"
				+ fileNumber + "' ");

		SQLQuery lQuery = this.ghibSession.createSQLQuery(Strbld.toString());

		billDetails = lQuery.list();
		// this.//ghibSession.disconnect();
		return billDetails;
	}

	public void updateMD5hash(String crypt, String fileNumber)
			throws HibernateException, SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("  update NSDL_BH_DTLS set NSDL_FILE_HASH='" + crypt
				+ "' where FILE_NAME='" + fileNumber + "' ");
		Query updateQuery = this.ghibSession.createSQLQuery(sb.toString());

		this.logger.info("Query to delete in deleteNsdlFile heaqder**********"
				+ sb.toString());

		updateQuery.executeUpdate();
		// this.ghibSession.connection().commit();
		// this.//ghibSession.disconnect();
	}

	public int checkForFileDtls(String crypt, String fileno) {
		List temp = null;
		int billStatus = 3;
		StringBuilder Strbld = new StringBuilder();
		Strbld.append(" SELECT FILE_NAME FROM NSDL_BH_DTLS where  FILE_NAME ='"
				+ fileno + "' and NSDL_FILE_HASH='" + crypt + "'");
		this.logger.info("Strbld is ***************" + Strbld.toString());
		SQLQuery lQuery = this.ghibSession.createSQLQuery(Strbld.toString());
		temp = lQuery.list();
		// this.//ghibSession.disconnect();
		return temp.size();
	}

	public void updateFileStatus(int fileStatus, String fileno, String errorData)
			throws HibernateException, SQLException {
		StringBuilder sb = new StringBuilder();
		errorData = errorData.replace("'", "");
		sb.append("  update NSDL_BH_DTLS set file_status='" + fileStatus
				+ "'  ");
		if ((errorData != null) && (!errorData.equals(""))) {
			sb.append(" , error_data='" + errorData + "' ");
		}
		sb.append("   where FILE_NAME='" + fileno + "' ");
		Query updateQuery = this.ghibSession.createSQLQuery(sb.toString());

		this.logger.info("Query to delete in deleteNsdlFile heaqder**********"
				+ sb.toString());

		updateQuery.executeUpdate();
		// this.ghibSession.connection().commit();
		// this.//ghibSession.disconnect();
	}

	public String getTransactionId(String fileNumber) {
		List temp = null;
		String transactionId = "";
		StringBuilder Strbld = new StringBuilder();

		Strbld.append(" SELECT TRANSACTION_ID FROM NSDL_BH_DTLS where FILE_NAME='"
				+ fileNumber + "' ");

		SQLQuery lQuery = this.ghibSession.createSQLQuery(Strbld.toString());

		temp = lQuery.list();
		this.logger.info("temp size" + temp.size());
		if ((temp != null) && (temp.size() > 0) && (temp.get(0) != null)) {
			transactionId = temp.get(0).toString();
		}
		// this.//ghibSession.disconnect();
		return transactionId;
	}

	public String getTreasuryName(String billNo) {
		List temp = null;
		String treasuryName = "";
		StringBuilder Strbld = new StringBuilder();

		Strbld.append(" SELECT LOC_NAME FROM CMN_LOCATION_MST where LOC_ID ='"
				+ billNo + "' ");

		SQLQuery lQuery = this.ghibSession.createSQLQuery(Strbld.toString());

		temp = lQuery.list();
		this.logger.info("temp size" + temp.size());
		if ((temp != null) && (temp.size() > 0) && (temp.get(0) != null)) {
			treasuryName = temp.get(0).toString();
		}
		// this.//ghibSession.disconnect();
		return treasuryName;
	}

	public String getErrorData(String fileNumber) {
		List temp = null;
		String data = "";
		StringBuilder Strbld = new StringBuilder();

		Strbld.append(" SELECT cast(error_data as varchar(7000)) FROM NSDL_BH_DTLS where FILE_NAME='"
				+ fileNumber + "' ");

		SQLQuery lQuery = this.ghibSession.createSQLQuery(Strbld.toString());
		this.logger.info("Query to getErrorData in  heaqder**********"
				+ Strbld.toString());

		temp = lQuery.list();
		this.logger.info("temp size" + temp.size());
		if ((temp != null) && (temp.size() > 0) && (temp.get(0) != null)) {
			data = temp.get(0).toString();
		}
		// this.//ghibSession.disconnect();
		return data;
	}

	public String getErrorDataOfNSDL(String fileNumber) {
		List temp = null;
		String data = "";
		StringBuilder Strbld = new StringBuilder();

		Strbld.append(" SELECT cast(NSDL_RESPONSE_HTML as varchar(7000)) FROM NSDL_BH_DTLS where FILE_NAME='"
				+ fileNumber + "' ");

		SQLQuery lQuery = this.ghibSession.createSQLQuery(Strbld.toString());
		this.logger.info("Query to getErrorData in  heaqder**********"
				+ Strbld.toString());
		// this.//ghibSession.disconnect();
		temp = lQuery.list();
		this.logger.info("temp size" + temp.size());
		if ((temp != null) && (temp.size() > 0) && (temp.get(0) != null)) {
			data = temp.get(0).toString();
		}
		return data;
	}

	public List getAllSubTreasuryPAO(String treasuryId) {
		List<ComboValuesVO> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		//if(treasuryId.equals("1201")||treasuryId.equals("2301")||treasuryId.equals("4601")||treasuryId.equals("5101"))////KR $t 5-1-2020
		//sb.append("SELECT loc_id, loc_name FROM CMN_LOCATION_MST where LOC_ID= :loc_id order by loc_name ");////(department_id=100006 and PARENT_LOC_ID=:loc_id ) or
		//else
		sb.append("SELECT loc_id, loc_name FROM CMN_LOCATION_MST where department_id=100003 and LOC_ID= :loc_id order by loc_name ");	
			
		this.gLogger.info("query to select sub treasury from treasury code:::"
				+ sb);
		Query selectQuery = this.ghibSession.createSQLQuery(sb.toString());
		this.gLogger.info("sql query created");
		selectQuery.setParameter("loc_id", treasuryId);

		lLstReturnList = new ArrayList();

		List lLstResult = selectQuery.list();
		this.gLogger.info("list size:" + lLstResult.size());
		// this.//ghibSession.disconnect();
		return lLstResult;
	}

	public List getAllSubTreasury(String treasuryId) {
		List<ComboValuesVO> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		//if(treasuryId.equals("1201")||treasuryId.equals("2301")||treasuryId.equals("4601")||treasuryId.equals("5101"))////KR $t 5-1-2020
		//sb.append("SELECT loc_id, loc_name FROM CMN_LOCATION_MST where LOC_ID= :loc_id order by loc_name ");
		//else
		//sb.append("SELECT loc_id, loc_name FROM CMN_LOCATION_MST where (department_id=100006 and PARENT_LOC_ID=:loc_id ) or LOC_ID= :loc_id order by loc_name ");
		sb.append("SELECT loc_id, loc_name FROM CMN_LOCATION_MST where department_id=100003 and LOC_ID= :loc_id order by loc_name ");
		
		this.gLogger.info("query to select sub treasury from treasury code:::"
				+ sb);
		Query selectQuery = this.ghibSession.createSQLQuery(sb.toString());
		this.gLogger.info("sql query created");
		selectQuery.setParameter("loc_id", treasuryId);

		lLstReturnList = new ArrayList();

		List lLstResult = selectQuery.list();
		this.gLogger.info("list size:" + lLstResult.size());

		ComboValuesVO lObjComboValuesVO = new ComboValuesVO();
		lObjComboValuesVO.setId("-1");
		lObjComboValuesVO.setDesc("--Select--");
		lLstReturnList.add(lObjComboValuesVO);
		if ((lLstResult != null) && (lLstResult.size() != 0)) {
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				Object[] obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				String desc = obj[1].toString();
				lObjComboValuesVO.setDesc(desc);
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}
		// this.//ghibSession.disconnect();
		return lLstReturnList;
	}

	public String getDtoRegNo(String treasuryCode) {
		List temp = null;
		String data = "";
		StringBuilder Strbld = new StringBuilder();

		Strbld.append(" SELECT dto_reg_no FROM  MST_DTO_REG where substr(LOC_ID,1,2)="
				+ treasuryCode);

		SQLQuery lQuery = this.ghibSession.createSQLQuery(Strbld.toString());
		this.logger.info("Query to getErrorData in  heaqder**********"
				+ Strbld.toString());

		temp = lQuery.list();
		this.logger.info("temp size" + temp.size());
		if ((temp != null) && (temp.size() > 0) && (temp.get(0) != null)) {
			data = temp.get(0).toString();
		}
		// this.//ghibSession.disconnect();
		return data;
	}

	public int getMonthId(String fileNumber) {
		List temp = null;
		int transactionId = 0;
		StringBuilder Strbld = new StringBuilder();

		Strbld.append(" SELECT month FROM NSDL_BH_DTLS where FILE_NAME='"
				+ fileNumber + "' ");

		SQLQuery lQuery = this.ghibSession.createSQLQuery(Strbld.toString());

		temp = lQuery.list();
		this.logger.info("temp size" + temp.size());
		if ((temp != null) && (temp.size() > 0) && (temp.get(0) != null)) {
			transactionId = Integer.parseInt(temp.get(0).toString());
		}
		// this.//ghibSession.disconnect();
		return transactionId;
	}

	public List getBillId(String fileNumber, long intMonth, long intYear) {
		List billDetails = null;
		Long billNo = Long.valueOf(0L);
		StringBuilder Strbld = new StringBuilder();

		Strbld.append(" SELECT BILL_ID,BILL_GENERATION_DATE FROM NSDL_BILL_dtls where FILE_NAME  = '"
				+ fileNumber
				+ "' and BILL_YEAR = "
				+ intYear
				+ " and BILL_MONTH = " + intMonth + "  and BILL_STATUS = 0 ");

		SQLQuery lQuery = this.ghibSession.createSQLQuery(Strbld.toString());

		billDetails = lQuery.list();
		// this.//ghibSession.disconnect();
		return billDetails;
	}

	public int getCount(long intMonth, long intYear, long paybillId) {
		Session hibSession = getSession();
		int count = 0;
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT count(sd.SR_NO) FROM NSDL_SD_DTLS sd inner join NSDL_BH_DTLS bh on bh.FILE_NAME = sd.FILE_NAME ");
		sb.append(" inner join NSDL_BILL_DTLS bill on bill.FILE_NAME = bh.FILE_NAME  ");
		sb.append(" where bh.FILE_STATUS in (11,5) and bh.STATUS = 0 and bill.BILL_STATUS = 0 and bill.BILL_MONTH = "
				+ intMonth
				+ " and bill.BILL_YEAR = "
				+ intYear
				+ " and bill.BILL_ID = " + paybillId + " ");

		Query qry = hibSession.createSQLQuery(sb.toString());
		count = ((Integer) qry.uniqueResult()).intValue();
		// this.//ghibSession.disconnect();
		return count;
	}

	public long getGrossAmount(long intMonth, long intYear, long paybillId) {
		List sev = null;
		Long grossAmount = Long.valueOf(0L);
		Double grossValue = Double.valueOf(0.0D);
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT bh.BH_TOTAL_AMT FROM NSDL_BH_DTLS bh inner join NSDL_BILL_DTLS bill on bill.FILE_NAME = bh.FILE_NAME ");
			lSBQuery.append("where bh.FILE_STATUS in (11,5) and bh.STATUS in (0,1) and bill.BILL_STATUS = 0 and bill.BILL_MONTH = "
					+ intMonth
					+ " and bill.BILL_YEAR = "
					+ intYear
					+ " and bill.BILL_ID = " + paybillId + " ");
			Query lQuery = this.ghibSession.createSQLQuery(lSBQuery.toString());

			this.logger.info("lQuery getGrossAmount:" + lQuery);
			sev = lQuery.list();
			if ((!sev.isEmpty()) && (sev.size() > 0) && (sev.get(0) != null)) {
				grossValue = Double.valueOf(Double.parseDouble(sev.get(0)
						.toString()));
			}
			grossAmount = Long.valueOf(grossValue.longValue());
			// this.//ghibSession.disconnect();
		} catch (Exception e) {
			this.logger.error(
					"Exception in getFinYearId of LNALedgerQueryDAOImpl: ", e);
		}
		return grossAmount.longValue();
	}

	public void updateNsdlBillDetails(String authNo, long paybillId, String flag) {
		try {
			Session hibSession = getSession();
			StringBuffer str = new StringBuffer();
			if (flag.equals("Y")) {
				str.append("update NSDL_BILL_DTLS set AUTH_NUMBER = '" + authNo
						+ "',BILL_STATUS = 3 where BILL_ID = " + paybillId
						+ " ");
			} else {
				str.append("update NSDL_BILL_DTLS set BILL_STATUS = 2 where BILL_ID = "
						+ paybillId + " ");
			}
			this.gLogger.error("str" + str);
			Query query1 = hibSession.createSQLQuery(str.toString());
			query1.executeUpdate();
			// this.ghibSession.connection().commit();
			// this.//ghibSession.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TrnNPSBeamsIntegration getPayBillAuthSlipDtlsForNPS(String authNo) {
		TrnNPSBeamsIntegration TrnNPSBeamsIntegration = null;
		Session hibSession = getSession();
		StringBuffer strQuery = new StringBuffer(100);
		strQuery.append("  from TrnNPSBeamsIntegration TrnNPSBeamsIntegration where TrnNPSBeamsIntegration.authNo=:authNo");
		Query query = hibSession.createQuery(strQuery.toString());
		query.setString("authNo", authNo);
		this.logger.info("Query For getPayBillAuthSlipDtls"
				+ strQuery.toString());
		if ((query.list() != null) && (query.list().size() > 0)
				&& (query.list().get(0) != null)) {
			TrnNPSBeamsIntegration = (TrnNPSBeamsIntegration) query.list().get(
					0);
		}
		// this.//ghibSession.disconnect();
		return TrnNPSBeamsIntegration;
	}

	public void updateBillStatus(String billId, String fileNameTODelete)
			throws HibernateException, SQLException {
		Session hibSession = getSession();
		StringBuffer str = new StringBuffer();

		str.append("update NSDL_BILL_DTLS set BILL_STATUS = -1,FILE_NAME = '"
				+ fileNameTODelete + "' where BILL_ID = " + billId
				+ " and BILL_STATUS = 2 ");

		Query query1 = hibSession.createSQLQuery(str.toString());
		query1.executeUpdate();
		// this.ghibSession.connection().commit();
		// this.//ghibSession.disconnect();
	}

	public List getTreasuryDdoCode(long longLoggedInLocation) {
		List temp = null;
		String ddoCode = "";
		StringBuilder Strbld = new StringBuilder();

		Strbld.append(" select DDO_CODE,PYAMENT_MODE from MST_TREASURY_DDOCODE_MPG where LOC_ID ="
				+ longLoggedInLocation + " and ACTIVE_FLAG = 1 ");

		SQLQuery lQuery = this.ghibSession.createSQLQuery(Strbld.toString());

		temp = lQuery.list();
		// this.//ghibSession.disconnect();
		return temp;
	}

	public String getContribType(String dcpsID) {
		List temp = null;
		String contribType = "";
		try {
			StringBuilder Strbld = new StringBuilder();

			Strbld.append(" select NSDL_CONTRIB_TYPE from mst_dcps_emp where DCPS_ID ='"
					+ dcpsID + "' ");

			SQLQuery lQuery = this.ghibSession
					.createSQLQuery(Strbld.toString());

			temp = lQuery.list();
			// this.//ghibSession.disconnect();
			if ((temp != null) && (temp.size() > 0)) {
				contribType = temp.get(0).toString();
			}
		} catch (Exception e) {
			this.logger.error("Exception in getContribType: ", e);
			e.printStackTrace();
		}
		return contribType;
	}

	public List cehckIfTreasuryDdoCode(long ddoCode) {
		List temp = null;

		StringBuilder Strbld = new StringBuilder();

		Strbld.append(" select DDO_CODE from MST_TREASURY_DDOCODE_MPG where DDO_CODE ="
				+ ddoCode + " and ACTIVE_FLAG = 1 ");

		SQLQuery lQuery = this.ghibSession.createSQLQuery(Strbld.toString());

		temp = lQuery.list();
		// this.//ghibSession.disconnect();
		return temp;
	}

	public void updateFileStatusandFrnNo(int fileStatus, String fileno,
			String errorData, String frnNumber) throws HibernateException,
			SQLException {
		StringBuilder sb = new StringBuilder();
		errorData = errorData.replace("'", "");
		sb.append("  update NSDL_BH_DTLS set file_status='" + fileStatus
				+ "' ,FRN_NO='" + frnNumber + "' ");
		if ((errorData != null) && (!errorData.equals(""))) {
			sb.append(" , error_data='" + errorData + "' ");
		}
		sb.append("   where FILE_NAME='" + fileno + "' ");
		Query updateQuery = this.ghibSession.createSQLQuery(sb.toString());

		this.logger.info("Query to delete in deleteNsdlFile heaqder**********"
				+ sb.toString());

		updateQuery.executeUpdate();

		// this.ghibSession.connection().commit();
		// this.//ghibSession.disconnect();
	}

	public void updateErrorCode(String fileno, String errorCode,
			String errorDesc) throws HibernateException, SQLException {
		StringBuilder sb = new StringBuilder();

		sb.append("  update NSDL_BH_DTLS set NSDL_ERROR_CODE='" + errorCode
				+ "' ,NSDL_ERROR_DESC='" + errorDesc + "' ");

		sb.append("   where FILE_NAME='" + fileno + "' ");
		Query updateQuery = this.ghibSession.createSQLQuery(sb.toString());

		this.logger.info("Query to delete in deleteNsdlFile heaqder**********"
				+ sb.toString());

		updateQuery.executeUpdate();

		// this.ghibSession.connection().commit();
		// this.//ghibSession.disconnect();
	}

	public void updateFileStatusandTranNo(int fileStatus, String fileno,
			String errorData, String tranNo) throws HibernateException,
			SQLException {
		StringBuilder sb = new StringBuilder();
		errorData = errorData.replace("'", "");
		sb.append("  update NSDL_BH_DTLS set file_status='" + fileStatus
				+ "'  ");
		if ((errorData != null) && (!errorData.equals(""))) {
			sb.append(" , error_data='" + errorData + "' ");
		}
		if ((tranNo != null) && (!tranNo.equals(""))) {
			sb.append(" ,TRANSACTION_ID='" + tranNo + "' ");
		}
		sb.append("   where FILE_NAME='" + fileno + "' ");
		Query updateQuery = this.ghibSession.createSQLQuery(sb.toString());

		this.logger.info("Query to delete in deleteNsdlFile heaqder**********"
				+ sb.toString());

		updateQuery.executeUpdate();
		// this.ghibSession.connection().commit();
		// this.//ghibSession.disconnect();
	}

	public String getFrnNo(String fileno) {
		List temp = null;
		String frnNo = "";

		StringBuilder Strbld = new StringBuilder();
		try {
			Strbld.append(" select FRN_NO from NSDL_BH_DTLS where FILE_NAME ='"
					+ fileno + "' ");

			SQLQuery lQuery = this.ghibSession
					.createSQLQuery(Strbld.toString());

			temp = lQuery.list();
			// this.//ghibSession.disconnect();
			if ((temp != null) && (temp.size() > 0) && (temp.get(0) != null)) {
				frnNo = temp.get(0).toString();
			}
		} catch (Exception e) {
			this.logger.error("Exception in getContribType: ", e);
			e.printStackTrace();
		}
		return frnNo;
	}

	public void updateNsdlStatus(int fileStatus, String fileno,
			String response, String form) throws HibernateException,
			SQLException {
		StringBuilder sb = new StringBuilder();
		response = response.replace("'", "");
		form = response.replace("'", "");
		sb.append("  update NSDL_BH_DTLS set  file_status='" + fileStatus
				+ "'  ");
		if ((response != null) && (!response.equals(""))) {
			sb.append(" , NSDL_RESPONSE_HTML='" + response + "' ");
		}
		if ((form != null) && (!form.equals(""))) {
			sb.append(" , SUBMISSION_FORM_HTML='" + form + "' ");
		}
		sb.append("   where FILE_NAME='" + fileno + "' ");
		Query updateQuery = this.ghibSession.createSQLQuery(sb.toString());

		this.logger.info("Query to delete in deleteNsdlFile heaqder**********"
				+ sb.toString());

		updateQuery.executeUpdate();
		// this.ghibSession.connection().commit();
		// this.//ghibSession.disconnect();
	}

	public String getDDORegNo(String DDOCode) {
		List temp = null;
		String DDORegNo = "";

		StringBuilder Strbld = new StringBuilder();
		try {
			Strbld.append(" select DDO_REG_NO from mst_ddo_reg where DDO_CODE ='"
					+ DDOCode + "' ");

			SQLQuery lQuery = this.ghibSession
					.createSQLQuery(Strbld.toString());

			temp = lQuery.list();
			// this.//ghibSession.disconnect();
			if ((temp != null) && (temp.size() > 0) && (temp.get(0) != null)) {
				DDORegNo = temp.get(0).toString();
			}
		} catch (Exception e) {
			this.logger.error("Exception in getContribType: ", e);
			e.printStackTrace();
		}
		return DDORegNo;
	}

	public int getDigiActivationDtls(String trCode) {
		List temp = null;
		int activeFlag = 0;

		StringBuilder Strbld = new StringBuilder();
		try {
			Strbld.append(" select ACTIVE_FLAG from MST_DIGI_SIGN_ACTIVATION_DTLS where LOC_ID ="
					+ trCode + " ");

			SQLQuery lQuery = this.ghibSession
					.createSQLQuery(Strbld.toString());

			temp = lQuery.list();
			// this.//ghibSession.disconnect();
			if ((temp != null) && (temp.size() > 0) && (temp.get(0) != null)) {
				activeFlag = Integer.parseInt(temp.get(0).toString());
			}
		} catch (Exception e) {
			this.logger.error("Exception in getDigiActivationDtls: ", e);
			e.printStackTrace();
		}
		return activeFlag;
	}

	/*
	 * SELECT bh_date,substr(bh_date,3,2),substr(bh_date,5,4), CASE when
	 * substr(bh_date,3,2) > 3 then substr(bh_date,5,4)||'-'||cast (cast(
	 * (substr(bh_date,7,2)) as INTEGER) +1 as char(4) ) else cast (cast(
	 * (substr(bh_date,7,2)) as INTEGER) -1 as
	 * char(4))||'-'||substr(bh_date,5,4) end from NSDL_BH_DTLS WHERE
	 * FILE_NAME='1509201904001'
	 */

	public String getFYear(String fileNumber) {
		String finyear = null;
		List temp;
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(" SELECT ");
			sb.append(" CASE when substr(bh_date,3,2) > 3 then  substr(bh_date,5,4)||'-'||cast (cast( (substr(bh_date,7,2)) as INTEGER) +1 as char(4) )  ");
			sb.append(" else cast (cast( (substr(bh_date,7,2)) as INTEGER) -1 as char(4))||'-'||substr(bh_date,5,4) ");
			sb.append(" end  from NSDL_BH_DTLS WHERE FILE_NAME='" + fileNumber
					+ "' ");
			Query selectQuery = this.ghibSession.createSQLQuery(sb.toString());
			this.logger.info("  getFYear ---------" + selectQuery.toString());
			temp = selectQuery.list();
			if ((temp != null) && (temp.size() > 0) && (temp.get(0) != null)) {
				finyear = temp.get(0).toString();
				
			}
			// this.//ghibSession.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			this.gLogger.error(" Error is : " + e, e);
		}
		return finyear;
	}

	public List getSDDtls1(String fileNumber, String ddoRegNo) {
		List lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT SR_NO||'^'||HEADER_NAME||'^'||SD_NO||'^'||SD_NO_2||'^'||SD_NO_3||'^'||SD_PRAN_NO||'^'||SD_EMPLR_AMOUNT||'^'||SD_EMP_AMOUNT||'^'||'^'||SD_TOTAL_AMT||'^'||SD_STATUS||'^'||SD_REMARK||'^' FROM NSDL_SD_DTLS  ");
		sb.append(" where   FILE_NAME='" + fileNumber + "'and DDO_REG_NO='"
				+ ddoRegNo + "' and SD_EMP_AMOUNT = 0 order by SR_NO asc ");

		Query selectQuery = this.ghibSession.createSQLQuery(sb.toString());
		lLstReturnList = selectQuery.list();
		// //////////this.ghibSession.disconnect();
		return lLstReturnList;
	}
	
	
	public List getAllSubTreasury1(String treasuryId) {
		List<ComboValuesVO> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT loc_id, loc_name FROM CMN_LOCATION_MST where (department_id=100006 and PARENT_LOC_ID=:loc_id ) or LOC_ID= :loc_id order by loc_name ");
		
		this.gLogger.info("query to select sub treasury from treasury code:::"
				+ sb);
		Query selectQuery = this.ghibSession.createSQLQuery(sb.toString());
		this.gLogger.info("sql query created");
		selectQuery.setParameter("loc_id", treasuryId);

		lLstReturnList = new ArrayList();

		List lLstResult = selectQuery.list();
		this.gLogger.info("list size:" + lLstResult.size());

		ComboValuesVO lObjComboValuesVO = new ComboValuesVO();
		lObjComboValuesVO.setId("-1");
		lObjComboValuesVO.setDesc("--Select--");
		lLstReturnList.add(lObjComboValuesVO);
		if ((lLstResult != null) && (lLstResult.size() != 0)) {
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				Object[] obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				String desc = obj[1].toString();
				lObjComboValuesVO.setDesc(desc);
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}
		// this.//ghibSession.disconnect();
		return lLstReturnList;
	}
	 public List getlstForLessEmployerContri(String yrCode, String month, String strLocationCode, String pranNo) throws Exception {
	      List contrList = null;
	      Query lQuery = null;
	      StringBuilder sb = null;

	      try {
	         sb = new StringBuilder();
	         sb.append("SELECT paybill.NPS_EMPLR_DIFFERENCE_ADJ ");
	         sb.append("FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");
	         sb.append("inner join NSDL_PAYBILL_DATA paybill on paybill.EMP_ID=eis.EMP_ID ");
	         sb.append("inner join PAYBILL_HEAD_MPG head on head.PAYBILL_ID=paybill.PAYBILL_GRP_ID ");
	         sb.append("inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID ");
	         sb.append("inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code ");
	         sb.append("inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2) ");
	         sb.append("inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4) ");
	         sb.append("where head.PAYBILL_YEAR='" + yrCode + "' and head.PAYBILL_MONTH=" + month + " and substr(ddo.ddo_code,1,2)=substr('" + strLocationCode + "',0,2) and emp.PRAN_NO is not null and ");
	         sb.append("emp.PRAN_ACTIVE=1 and emp.REG_STATUS=1 and emp.pran_no='" + pranNo + "' ");
	         sb.append("and  cast((nvl(paybill.NPS_EMPLR_DIFFERENCE_ADJ,0)) as double) <> 0 ");
	         sb.append("and head.APPROVE_FLAG=1 ");
	         lQuery = this.ghibSession.createSQLQuery(sb.toString());
	         this.logger.info("lQuery*******is to get the list" + lQuery);
	         contrList = lQuery.list();
	         return contrList;
	      } catch (Exception var9) {
	         var9.printStackTrace();
	         this.gLogger.error("Error is :" + var9, var9);
	         throw var9;
	      }
	   }
	// public List getuser() {
	//
	// List temp=null;
	// StringBuilder sb = new StringBuilder();
	// try
	// {
	// sb.append(" SELECT USER_NAME,PASSWORD FROM ifms.cmn_dbuser_mst WHERE TYPE_OF_USER='dr' AND ACTIVE_FLAG=1 ");
	// Query selectQuery = this.ghibSession.createSQLQuery(sb.toString());
	// this.logger
	// .info("  getFYear ---------" +
	// selectQuery.toString());
	// temp = selectQuery.list();
	// }
	// catch (Exception e)
	// {
	// e.printStackTrace();
	// this.gLogger.error(" Error is : " + e, e);
	// }
	// return temp;
	// }

	/*
	 * public List getuser(){
	 * 
	 * List lLstDept;
	 * 
	 *  try{ StringBuilder lSBQuery = new StringBuilder();   lSBQuery.append(
	 * " SELECT USER_NAME,PASSWORD FROM ifms.cmn_dbuser_mst WHERE TYPE_OF_USER='dr' AND ACTIVE_FLAG=1 "
	 * );      Session ghibSession =
	 * ServiceLocator.getServiceLocator().getSessionFactorySlave
	 * ().getCurrentSession(); Query lQuery =
	 * ghibSession.createSQLQuery(lSBQuery.toString()); lLstDept= lQuery.list();
	 *    }catch(Exception e){ System.out.println(" Error is : " + e);  throw e;
	 *  }  return lLstDept; } 
	 */
}