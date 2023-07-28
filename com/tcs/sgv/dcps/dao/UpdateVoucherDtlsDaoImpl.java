package com.tcs.sgv.eis.dao;

import java.sql.Blob;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.Session;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;


public class UpdateVoucherDtlsDaoImpl extends GenericDaoHibernateImpl implements UpdateVoucherDtlsDao{

    public UpdateVoucherDtlsDaoImpl(Class type, SessionFactory sessionFactory) {
        super(type);
        Session hibSession = sessionFactory.getCurrentSession();
        setSessionFactory(sessionFactory);
        // TODO Auto-generated constructor stub
    }

    public List getVoucherData(String selBill, String selYear,
            String selMonth) {
        StringBuilder lSBQuery = new StringBuilder();
        Query lQuery = null;
        List voucherList=null;
        Session session = getSession();
        logger.info("hi i Finding Voucher Details.");
        lSBQuery.append(" SELECT mpg.Paybill_id,bill.bill_group_id,bill.DESCRIPTION,mpg.VOUCHER_NO,mpg.VOUCHER_DATE,bill.ddo_code FROM PAYBILL_HEAD_MPG ");
        lSBQuery.append("mpg , mst_dcps_bill_group bill  where bill.BILL_GROUP_ID=mpg.BILL_NO and ");
        lSBQuery.append("mpg.PAYBILL_MONTH=:month and mpg.PAYBILL_YEAR=:year and mpg.BILL_NO=:billNo and mpg.APPROVE_FLAG=1");
        lQuery = session.createSQLQuery(lSBQuery.toString());
        lQuery.setParameter("month", selMonth);
        lQuery.setParameter("year", selYear);
        lQuery.setParameter("billNo", selBill);
        voucherList = lQuery.list();
        logger.info("query is"+lQuery.toString());
        logger.info("size is"+voucherList.size());
        return voucherList;
    }

    @Override
    public void updateDetails(String payBillId, String voucherNo,
            String voucherDt,String finYear) {
        Session session= getSession();
        logger.info("---- update voucher ---");
        StringBuffer sb = new StringBuffer();
        sb.append("update PAYBILL_HEAD_MPG set VOUCHER_NO=:vNo,VOUCHER_DATE=:vDate where ");
        sb.append(" paybill_id=:id and approve_flag=1");
        logger.info("---- update voucher ---"+sb);
        Query query = session.createSQLQuery(sb.toString());
        query.setParameter("vNo", voucherNo);
        query.setParameter("vDate", voucherDt);
        query.setParameter("id", payBillId);
        logger.info("---- query---"+sb);
        query.executeUpdate();


        StringBuffer sb1 = new StringBuffer();
        sb1.append("UPDATE TRN_DCPS_CONTRIBUTION SET VOUCHER_NO=:vNo1, VOUCHER_DATE=:vDate1 WHERE RLT_CONTRI_VOUCHER_ID in ");
        sb1.append(" (SELECT RLT_CONTRI_VOUCHER_ID FROM TRN_DCPS_CONTRIBUTION " +
                "where BILL_GROUP_ID in (select bill_no from paybill_head_mpg where paybill_id=:id) " +
                "AND MONTH_ID in (select paybill_month from paybill_head_mpg where paybill_id=:id) and " +
        "FIN_YEAR_ID= (SELECT FIN_YEAR_ID FROM SGVC_FIN_YEAR_MST where FIN_YEAR_DESC=:finDesc)) ");
        Query query1 = session.createSQLQuery(sb1.toString());
        query1.setParameter("vNo1", voucherNo);
        query1.setParameter("vDate1", voucherDt);
        query1.setParameter("id", payBillId);
        query1.setParameter("finDesc", finYear);
        logger.info("---- query---"+sb1);
        query1.executeUpdate();

        StringBuffer sb2 = new StringBuffer();
        sb2.append("UPDATE MST_DCPS_CONTRI_VOUCHER_DTLS  SET VOUCHER_NO=:vNo, VOUCHER_DATE=:vDateF WHERE MST_DCPS_CONTRI_VOUCHER_DTLS in ");
        sb2.append(" (SELECT RLT_CONTRI_VOUCHER_ID FROM TRN_DCPS_CONTRIBUTION " +
                "where BILL_GROUP_ID in (select bill_no from paybill_head_mpg where paybill_id=:id) " +
                "AND MONTH_ID in (select paybill_month from paybill_head_mpg where paybill_id=:id) and " +
        "FIN_YEAR_ID= (SELECT FIN_YEAR_ID FROM SGVC_FIN_YEAR_MST where FIN_YEAR_DESC=:finDesc)) ");
        Query query2 = session.createSQLQuery(sb2.toString());
        query2.setParameter("vNo", voucherNo);
        query2.setParameter("vDateF", voucherDt);
        query2.setParameter("id", payBillId);
        query2.setParameter("finDesc", finYear);
        logger.info("---- query---"+sb2);
        query2.executeUpdate();

    }

    //added by roshan to view abstract report :start
    public List viewAbstractReports(String billid) {
        Session session =  getSession();
        StringBuffer sb = new StringBuffer();
        List temp=null;
        logger.info("---- Abstract reports---");

        sb.append(" SELECT billgroup.BILL_GROUP_ID,billgroup.DESCRIPTION, office.OFF_NAME,office.ddo_code, ");
        sb.append(" sum(pay.BILL_NET_AMOUNT) as TOTAL_SALARY,sum(paybill.FESTIVAL_ADVANCE) as FA,");
        sb.append(" sum(paybill.EXC_PAYRC) as EXC_PAY_REC,sum(paybill.GROSS_AMT) as Gross_Amt,");
        sb.append(" sum(paybill.GPF_IV)+sum(paybill.GPF_ADV)+sum(paybill.GPF_IV_ADV)+sum(paybill.GPF_IAS_OTHER)+sum(paybill.GPF_IAS)+");
        sb.append(" sum(paybill.GPF_IPS)+sum(paybill.GPF_IFS)+sum(paybill.GPF_GRP_ABC)+sum(paybill.GPF_GRP_D)+sum(paybill.GPF_ADV_GRP_ABC)+");
        sb.append(" sum(paybill.GPF_ADV_GRP_D)+sum(paybill.GPF_ADV_GRP_ABC_INT)+sum(paybill.GPF_ADV_GRP_D_INT)+sum(paybill.GPF_ABC_ARR_MR)+");
        sb.append(" sum(paybill.GPF_D_ARR_MR)+sum(paybill.GPF_IAS_ARR_MR)+sum(paybill.GPF_IFS_ARR_MR)+sum(paybill.GPF_IPS_ARR_MR)+");
        sb.append(" sum(paybill.GPF_OTHER_STATE)+sum(paybill.GPF_IAS_LOAN) as GPF,");
        sb.append(" sum(paybill.DCPS_DELAY) as DCPS_Delay,sum(paybill.DCPS) as dcps_reg,");
        sb.append(" sum(paybill.IT) as IT, sum(paybill.DCPS_DA) as DCPS_DA, sum(paybill.PT) as PT, sum(paybill.COMPUTER_ADV) as COMP_ADV,");
        sb.append(" sum(paybill.OTHER_DEDUCTION) as OTHER_DEDUCTION, SUM(PAYBILL.PLI) AS PLI, SUM(PAYBILL.GIS) AS GIS,sum(paybill.TOTAL_DED) as TOTAL_DED,");
        sb.append(" sum(paybill.NET_TOTAL)-sum(paybill.TOTAL_DED) as NET_PAY");
        sb.append(" FROM CONSOLIDATED_BILL_MPG bill, PAYBILL_HEAD_MPG pay, HR_PAY_PAYBILL paybill, ");
        sb.append(" MST_DCPS_BILL_GROUP billGroup, MST_DCPS_DDO_OFFICE office where bill.PAYBILL_ID=pay.PAYBILL_ID and  ");
        sb.append(" pay.PAYBILL_ID=paybill.PAYBILL_GRP_ID and pay.BILL_NO=billgroup.BILL_GROUP_ID and ");
        sb.append(" billGroup.LOC_ID=office.LOC_ID and paybill.emp_id is not null  ");
        sb.append("and bill.CONS_BILL_ID=:billid");
        sb.append(" group by billgroup.BILL_GROUP_ID,billgroup.DESCRIPTION, office.OFF_NAME,office.ddo_code");
        logger.info("---- - Abstract reports---"+sb.toString());
        Query query = session.createSQLQuery(sb.toString());
        query.setParameter("billid", billid);
        temp = query.list();
        logger.info("---- - Abstract reports---"+sb.toString());
        return temp;

    }

    public String getAmount(String rdCode, String billGroupID, String consBillid) {
        Session session = getSession();
        StringBuffer sb = new StringBuffer();
        String temp=null;
        logger.info("---- get Non gov Deduce Amount---");
        sb.append(" select cast(sum(NON_GOV_DEDUC_AMOUNT) as Varchar(20)) as nonGov from HR_PAY_PAYSLIP_NON_GOVT where");
        sb.append(" NON_GOV_DEDUC_CODE in ("+rdCode+") and  paybill_id in");
        sb.append(" (select id from HR_PAY_PAYBILL where PAYBILL_GRP_ID in ");
        sb.append(" (select paybill_id from PAYBILL_HEAD_MPG where bill_no= "+billGroupID+" and ");
        sb.append(" paybill_id in (select PAYBILL_ID from CONSOLIDATED_BILL_MPG where ");
        sb.append(" CONS_BILL_ID= "+consBillid+")))");
        logger.info("---- - get Non gov Deduce Amount---"+sb.toString());
        Query query = session.createSQLQuery(sb.toString());
        //query.setParameter("rdCode", rdCode);
        //query.setParameter("billGroupID", rdCode);
        //query.setParameter("consBillid", consBillid);
        temp=(String) query.uniqueResult();
        if (temp!=null){
            return temp.toString();
        }
        else {
            return null;
        }
    }

    public List getTotalSum(String billid) {
        Session session = getSession();
        StringBuffer sb = new StringBuffer();
        List temp=null;
        logger.info("---- Abstract reports---");

        sb.append(" SELECT ");
        sb.append(" sum(pay.BILL_NET_AMOUNT) as TOTAL_SALARY,sum(paybill.FESTIVAL_ADVANCE) as FA,");
        sb.append(" sum(paybill.EXC_PAYRC) as EXC_PAY_REC,sum(paybill.GROSS_AMT) as Gross_Amt,");
        sb.append(" sum(paybill.GPF_IV)+sum(paybill.GPF_ADV)+sum(paybill.GPF_IV_ADV)+sum(paybill.GPF_IAS_OTHER)+sum(paybill.GPF_IAS)+");
        sb.append(" sum(paybill.GPF_IPS)+sum(paybill.GPF_IFS)+sum(paybill.GPF_GRP_ABC)+sum(paybill.GPF_GRP_D)+sum(paybill.GPF_ADV_GRP_ABC)+");
        sb.append(" sum(paybill.GPF_ADV_GRP_D)+sum(paybill.GPF_ADV_GRP_ABC_INT)+sum(paybill.GPF_ADV_GRP_D_INT)+sum(paybill.GPF_ABC_ARR_MR)+");
        sb.append(" sum(paybill.GPF_D_ARR_MR)+sum(paybill.GPF_IAS_ARR_MR)+sum(paybill.GPF_IFS_ARR_MR)+sum(paybill.GPF_IPS_ARR_MR)+");
        sb.append(" sum(paybill.GPF_OTHER_STATE)+sum(paybill.GPF_IAS_LOAN) as GPF,");
        sb.append(" sum(paybill.DCPS_DELAY) as DCPS_Delay,sum(paybill.DCPS) as dcps_reg,");
        sb.append(" sum(paybill.IT) as IT, sum(paybill.DCPS_DA) as DCPS_DA, sum(paybill.PT) as PT, sum(paybill.COMPUTER_ADV) as COMP_ADV,");
        sb.append(" sum(paybill.OTHER_DEDUCTION) as OTHER_DEDUCTION, SUM(PAYBILL.PLI) AS PLI, SUM(PAYBILL.GIS) AS GIS,sum(paybill.TOTAL_DED) as TOTAL_DED,");
        sb.append(" sum(paybill.NET_TOTAL)-sum(paybill.TOTAL_DED) as NET_PAY");
        sb.append(" FROM CONSOLIDATED_BILL_MPG bill, PAYBILL_HEAD_MPG pay, HR_PAY_PAYBILL paybill, ");
        sb.append(" MST_DCPS_BILL_GROUP billGroup, MST_DCPS_DDO_OFFICE office where bill.PAYBILL_ID=pay.PAYBILL_ID and  ");
        sb.append(" pay.PAYBILL_ID=paybill.PAYBILL_GRP_ID and pay.BILL_NO=billgroup.BILL_GROUP_ID and ");
        sb.append(" billGroup.LOC_ID=office.LOC_ID and paybill.emp_id is not null  ");
        sb.append("and bill.CONS_BILL_ID=:billid");
        logger.info("---- - Abstract reports---"+sb.toString());
        Query query = session.createSQLQuery(sb.toString());
        query.setParameter("billid", billid);
        temp = query.list();
        logger.info("---- - Abstract reports---"+sb.toString());
        return temp;
    }

    public List viewConsBillDetailsSum(String billid) {
        Session session = getSession();
        StringBuffer sb = new StringBuffer();
        List temp=null;
        logger.info("---- viewConsBillDetails---");
        sb.append("SELECT ");
        sb.append("sum(paybill.GROSS_AMT) as Gross_Amt, sum(paybill.TOTAL_DED) as TOTAL_DED, sum(paybill.NET_TOTAL) as NET_AMT ");
        sb.append("FROM CONSOLIDATED_BILL_MPG bill, PAYBILL_HEAD_MPG pay, HR_PAY_PAYBILL paybill, ");
        sb.append("MST_DCPS_BILL_GROUP billGroup, MST_DCPS_DDO_OFFICE office ");
        sb.append("where bill.PAYBILL_ID=pay.PAYBILL_ID and pay.PAYBILL_ID=paybill.PAYBILL_GRP_ID and pay.BILL_NO=billgroup.BILL_GROUP_ID and billGroup.LOC_ID=office.LOC_ID ");
        sb.append("and paybill.emp_id is not null ");
        sb.append("and bill.CONS_BILL_ID="+billid);
        logger.info("---- viewConsBillDetails---"+sb.toString());
        Query query = session.createSQLQuery(sb.toString());
        temp = query.list();
        logger.info("---- viewConsBillDetails---"+sb.toString());
        return temp;
    }
    //added by vaibhav tyagi to view consolidated bill details : start
    public List viewConsBillDetails(String billid) {
        Session session = getSession();
        StringBuffer sb = new StringBuffer();
        List temp=null;
        logger.info("---- viewConsBillDetails---");
        sb.append("SELECT billgroup.BILL_GROUP_ID,billgroup.DESCRIPTION, office.OFF_NAME, ");
        sb.append("sum(paybill.GROSS_AMT) as Gross_Amt, sum(paybill.TOTAL_DED) as TOTAL_DED, sum(paybill.NET_TOTAL) as NET_AMT ");
        sb.append("FROM CONSOLIDATED_BILL_MPG bill, PAYBILL_HEAD_MPG pay, HR_PAY_PAYBILL paybill, ");
        sb.append("MST_DCPS_BILL_GROUP billGroup, MST_DCPS_DDO_OFFICE office ");
        sb.append("where bill.PAYBILL_ID=pay.PAYBILL_ID and pay.PAYBILL_ID=paybill.PAYBILL_GRP_ID and pay.BILL_NO=billgroup.BILL_GROUP_ID and billGroup.LOC_ID=office.LOC_ID ");
        sb.append("and paybill.emp_id is not null ");
        sb.append("and bill.CONS_BILL_ID="+billid);
        sb.append(" group by billgroup.BILL_GROUP_ID,billgroup.DESCRIPTION, office.OFF_NAME");
        logger.info("---- viewConsBillDetails---"+sb.toString());
        Query query = session.createSQLQuery(sb.toString());
        temp = query.list();
        logger.info("---- viewConsBillDetails---"+sb.toString());
        return temp;	

    }
    //added by vaibhav tyagi to view consolidated bill details : end
    public String getOfcName(Long postid){
        String ddoDtls = null;
        Session session = getSession();
        StringBuffer sb = new StringBuffer();
        logger.info("---- getDDofromOffc DAO---");

        sb.append(" select mst.dcpsDdoOfficeName from  DdoOffice mst,OrgDdoMst org where org.locationCode=mst.LocId " +
        "and lower(mst.dcpsDdoOfficeDdoFlag)='yes' and org.postId=:postID ");
        logger.info("---- getDDofromOffc DAo---"+sb.toString());
        Query query = session.createQuery(sb.toString());
        query.setParameter("postID", postid);
        ddoDtls = query.uniqueResult().toString();
        logger.info("Query Result is::"+ddoDtls);
        return ddoDtls;
    }
    public void allowDistrict(String allowedDis, String columnName)
	  {
		Session hibSession = getSession();
		try {
			logger.info("Inside updateDetails....");
			StringBuilder SBQuery = new StringBuilder();
			
			SBQuery.append("update CMN_DISTRICT_MST set " + columnName + "=1 where district_id in (" + allowedDis + ") ");
			Query lQuery = hibSession.createSQLQuery(SBQuery.toString());
			logger.info("the query is ********"+lQuery.toString());
			if(allowedDis!=null && !allowedDis.equals("")){
				lQuery.executeUpdate();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error is :" + e, e);
		}
		
		try {
			logger.info("Inside updateDetails....");
			StringBuilder SBQuery = new StringBuilder();
			SBQuery.append(" update CMN_DISTRICT_MST set " + columnName + "=0 where district_id in (select district_id from cmn_district_mst where lang_id=1 and state_id=15 ) ");
			if(allowedDis!=null && !allowedDis.equals("")){
				SBQuery.append(" and district_id not in ("+allowedDis+")");
			}
			Query lQuery = hibSession.createSQLQuery(SBQuery.toString());
			logger.info("the query is ********"+lQuery.toString());
			lQuery.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error is :" + e, e);
		}
	}


public List getAllDistrict(String columnName) {
		List distList = null;
		Session hibSession = getSession();
		StringBuffer strQuery = new StringBuffer();
		strQuery.append("SELECT District_id, district_name," + columnName + " FROM cmn_district_mst where lang_id=1 and STATE_ID=15 order by district_name");
		logger.info("District List Query: "+strQuery.toString());
		Query query = hibSession.createSQLQuery(strQuery.toString());
		distList = query.list();	
		return distList;
	}

    public List getApprovedFormsForDDO(String ddoCode) {
        StringBuilder lSBQuery = new StringBuilder();
        Query lQuery = null;
        List<MstEmp> EmpList = null;
        Session session = getSession();
        logger.info("hi i Finding Approved  Employee");
        lSBQuery.append(" Select EM.dcps_Emp_Id,EM.EMP_NAME,EM.sevarth_id,EM.DOB,EM.gender,EM.EMP_NAME ");
        lSBQuery.append(" FROM Mst_dcps_emp EM");
        lSBQuery.append(" WHERE EM.reg_status in (1,2)");
        lSBQuery.append(" order by em.emp_name");
        lQuery = session.createSQLQuery(lSBQuery.toString());
        logger.info("query is"+lQuery.toString());
        EmpList = lQuery.list();
        logger.info("query is"+lQuery.toString());
        return EmpList;
    }

    @Override
    public void createNewGR(long attachment_Id_order,Long postId)
    {

        try {Session session = getSession();
        logger.info("Inside createNewGR");
        StringBuilder SBQuery = new StringBuilder();
        SBQuery.append(" INSERT INTO JEEVAN_PRAMAN_PATRA (created_date,ATTACHEMENT_ID,PRAMAN_PATRA_DTLS_ID ,STATUS,UPDATED_DATE,treasury_code ) values (sysdate,"+attachment_Id_order+", null,0,null,(SELECT loc_id FROM ORG_POST_DETAILS_RLT where post_id = '"+postId+"')) ");
        Query lQuery = session.createSQLQuery(SBQuery.toString());
        logger.info("the query to insert gr is********"+lQuery.toString());
        lQuery.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error is :" + e, e);

        }




    }

    @Override
    public List getGRlList(Long postId)
    {
        List grList = null;
        Session hibSession = getSession();
        StringBuffer strQuery = new StringBuffer();
        strQuery.append("SELECT PRAMAN_PATRA_ID,CREATED_DATE,STATUS,ATTACHEMENT_ID FROM JEEVAN_PRAMAN_PATRA where treasury_code in (SELECT loc_id FROM ORG_POST_DETAILS_RLT where post_id = '"+postId+"') order by PRAMAN_PATRA_ID desc");
        logger.info("gr Type List Query: "+strQuery.toString());
        Query query = hibSession.createSQLQuery(strQuery.toString());
        grList = query.list();  
        return grList;
    }

    public Blob getAttachment(String attachment_Id_order)
    { Session hibSession = getSession();
    logger.info("inside getAttachment");
    Blob result= null;
    String strQuery=null;


    strQuery = "SELECT final_attachment FROM CMN_ATTDOC_MST where SR_NO=(select SR_NO from CMN_ATTACHMENT_MPG where ATTACHMENT_ID='"+attachment_Id_order+"')";
    logger.info(strQuery);
    Query query = hibSession.createSQLQuery(strQuery);
    result = (Blob) query.uniqueResult();

    return result;
    }

    @Override
    public void saveFileDtls(String[] fileDtls, String id,Long lngPostId)
    {

        
        
        
        try {Session session = getSession();
        logger.info("Inside createNewGR");
        StringBuilder SBQuery = new StringBuilder();
        SBQuery.append(" insert into JEEVAN_PRAMAN_PATRA_DTLS (PRAMAN_PATRA_ID,CREATED_DATE,STATUS,BANK_CODE,PPO_NUMBER,BANK_ACCOUNT,LIFE_CERTIFICATE,RE_MARRIAGE,RE_EMPLOYED,AUTHENTICATION_DATE,PRAMAAN_ID,AADHAAR,MOBILE,NAME,GENDER,DATE_OF_BIRTH,TREASYRY_CODE,UPDATED_DATE,PPO_AVAILABLE_IN_SEVARTH) values ("+id+",sysdate,0,'"+fileDtls[0]+"','"+fileDtls[1]+"', '"+fileDtls[2]+"', '"+fileDtls[3]+"', '"+fileDtls[4]+"', '"+fileDtls[5]+"', '"+fileDtls[6]+"', '"+fileDtls[7]+"', '"+fileDtls[8]+"', '"+fileDtls[9]+"', '"+fileDtls[10]+"', '"+fileDtls[11]+"', '"+fileDtls[12]+"',(SELECT loc_id FROM ORG_POST_DETAILS_RLT where post_id = '"+lngPostId+"'), null, (select count(1) from TRN_PENSION_RQST_HDR where PPO_NO='"+fileDtls[1]+"')) ");
        Query lQuery = session.createSQLQuery(SBQuery.toString());
        logger.info("the query to insert saveFileDtls is********"+lQuery.toString());
        lQuery.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error is :" + e, e);

        }
    }

    @Override
    public void updateStatus(String id)
    { 
        try 
        {
            Session session = getSession();
            logger.info("Inside createNewGR");
            StringBuilder SBQuery = new StringBuilder();
            SBQuery.append(" update JEEVAN_PRAMAN_PATRA set status=1 where PRAMAN_PATRA_ID ="+id+" ");
            Query lQuery = session.createSQLQuery(SBQuery.toString());
            logger.info("the query to insert saveFileDtls is********"+lQuery.toString());
            lQuery.executeUpdate();

        } 

        catch (Exception e) {
            e.printStackTrace();
            logger.error("Error is :" + e, e);

        }

    }

    @Override
    public List getFileDtls(String fileNumber)
    {   
        Session session = getSession();
        List lLstReturnList = null;
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT BANK_CODE ||'|'|| PPO_NUMBER ||'|'|| BANK_ACCOUNT ||'|'|| LIFE_CERTIFICATE ||'|'|| RE_MARRIAGE ||'|'|| RE_EMPLOYED ||'|'|| AUTHENTICATION_DATE ||'|'|| PRAMAAN_ID ||'|'|| AADHAAR ||'|'|| MOBILE ||'|'|| NAME ||'|'|| GENDER ||'|'|| DATE_OF_BIRTH   FROM JEEVAN_PRAMAN_PATRA_DTLS ");
        sb.append(" where PRAMAN_PATRA_ID = '"+fileNumber+"' order by SR_NO asc");
        logger.info("the query to insert getFileDtls is********"+sb.toString());
        Query selectQuery = session.createSQLQuery(sb.toString());
        lLstReturnList=  selectQuery.list();


        return lLstReturnList;


    }

    @Override
    public List getAllValidCase(Long postId,String id)
    {
        List ppoList = null;
        Session hibSession = getSession();
        StringBuffer strQuery = new StringBuffer();
        strQuery.append("  select jeevan.SR_NO,jeevan.PPO_NUMBER,jeevan.NAME,jeevan.aadhaar,nvl(trn.PPO_NO,'NA'), nvl(hdr.FIRST_NAME,'NA'),jeevan.MOBILE,jeevan.DATE_OF_BIRTH,jeevan.BANK_ACCOUNT,jeevan.gender,jeevan.RE_MARRIAGE,jeevan.RE_EMPLOYED,jeevan.AUTHENTICATION_DATE,jeevan.PRAMAAN_ID,jeevan.AADHAAR,hdr.FIRST_NAME, hdr.DATE_OF_BIRTH,hdr.GENDER,hdr.UID_NO,mst.ACOUNT_NO,trn.SEEN_FLAG,jeevan.LIFE_CERTIFICATE,hdr.MOBLILE_NO,jeevan.AADHAAR from JEEVAN_PRAMAN_PATRA_DTLS jeevan inner join TRN_PENSION_RQST_HDR trn on trn.PPO_NO=jeevan.PPO_NUMBER inner join MST_PENSIONER_HDR hdr on hdr.PENSIONER_CODE = trn.PENSIONER_CODE inner join MST_PENSIONER_DTLS mst on mst.PENSIONER_CODE = trn.PENSIONER_CODE where jeevan.status not in ( 1,-1) and jeevan.praman_patra_id="+id+" and jeevan.TREASYRY_CODE in  (SELECT loc_id FROM ORG_POST_DETAILS_RLT where post_id = '"+postId+"') and mst.BRANCH_CODE in (select BRANCH_CODE from RLT_AUDITOR_BANK where post_id= '"+postId+"') and jeevan.praman_patra_id="+id+" ");
        logger.info("gr Type List Query: "+strQuery.toString());
        Query query = hibSession.createSQLQuery(strQuery.toString());
        ppoList = query.list();  
        return ppoList;
    }

    @Override
    public void updateLCFlag(String ppoNumber,Long lngPostId,String fileId)
    { 
        try 
        {
            //To update The life certificate date....
            Session session = getSession();
            logger.info("Inside createNewGR");
            StringBuilder SBQuery = new StringBuilder();
            SBQuery.append(" update JEEVAN_PRAMAN_PATRA_DTLS set updated_date=sysdate,Updated_by ='"+lngPostId+"',remarks='NA',status=1 where PPO_NUMBER ='"+ppoNumber+"' ");
            Query lQuery = session.createSQLQuery(SBQuery.toString());
            logger.info("the query to insert updateLCFlag is********"+lQuery.toString());
            lQuery.executeUpdate();

        } 

        catch (Exception e) {
            e.printStackTrace();
            logger.error("Error is :" + e, e);

        }
        
        try 
        {
            Session session = getSession();
            logger.info("Inside createNewGR");
            StringBuilder SBQuery1 = new StringBuilder();
            SBQuery1.append(" update TRN_PENSIONER_SEEN_DTLS  set LIFE_CERT_STATUS ='Y',SEEN_DATE=sysdate, updated_date=sysdate where PENSIONER_CODE in  ( select PENSIONER_CODE from TRN_PENSION_RQST_HDR where ppo_no ='"+ppoNumber+"') ");
            Query lQuery1 = session.createSQLQuery(SBQuery1.toString());
            logger.info("the query to insert updateLCFlag is********"+lQuery1.toString());
            lQuery1.executeUpdate();

        } 

        catch (Exception e) {
            e.printStackTrace();
            logger.error("Error is :" + e, e);

        }
        
        

        try 
        {
            Session session = getSession();
            logger.info("Inside createNewGR");
            StringBuilder SBQuery2 = new StringBuilder();
            SBQuery2.append(" update JEEVAN_PRAMAN_PATRA set STATUS=2,updated_date=sysdate where PRAMAN_PATRA_ID in (select PRAMAN_PATRA_ID from JEEVAN_PRAMAN_PATRA_DTLS where PPO_NUMBER ='"+ppoNumber+"') ");
            Query lQuery2 = session.createSQLQuery(SBQuery2.toString());
            logger.info("the query to insert updateLCFlag is********"+lQuery2.toString());
            lQuery2.executeUpdate();

        } 

        catch (Exception e) {
            e.printStackTrace();
            logger.error("Error is :" + e, e);

        }
        
        
        try 
        {
            Session session = getSession();
            logger.info("Inside createNewGR");
            StringBuilder SBQuery3 = new StringBuilder();
            SBQuery3.append(" update TRN_PENSION_RQST_HDR set SEEN_FLAG= 'Y' where ppo_no ='"+ppoNumber+"' ");
            Query lQuery3 = session.createSQLQuery(SBQuery3.toString());
            logger.info("the query to insert updateLCFlag is********"+lQuery3.toString());
            lQuery3.executeUpdate();

        } 

        catch (Exception e) {
            e.printStackTrace();
            logger.error("Error is :" + e, e);

        }
        
        
        try 
        {
            Session session = getSession();
            logger.info("Inside createNewGR");
            StringBuilder SBQuery4 = new StringBuilder();
            SBQuery4.append(" update MST_PENSIONER_HDR  set updated_date=sysdate,UID_NO=(select aadhaar from JEEVAN_PRAMAN_PATRA_DTLS where praman_patra_id='"+fileId+"' and PPO_NUMBER='"+ppoNumber+"') ,MOBLILE_NO=(select MOBILE from JEEVAN_PRAMAN_PATRA_DTLS where praman_patra_id='"+fileId+"' and PPO_NUMBER='"+ppoNumber+"')  where PENSIONER_CODE in  ( select PENSIONER_CODE from TRN_PENSION_RQST_HDR where ppo_no ='"+ppoNumber+"')  ");
            Query lQuery4 = session.createSQLQuery(SBQuery4.toString());
            logger.info("the query to insert updateLCFlag is********"+lQuery4.toString());
            lQuery4.executeUpdate();

        } 

        catch (Exception e) {
            e.printStackTrace();
            logger.error("Error is :" + e, e);

        }
        
        
        
        
        
    }

    @Override
    public String getFileId(String attachId)
    {
        String fileId = null;
        Session hibSession = getSession();
        StringBuffer strQuery = new StringBuffer();
        strQuery.append(" select PRAMAN_PATRA_ID from JEEVAN_PRAMAN_PATRA  where ATTACHEMENT_ID='"+attachId+"'  ");
        logger.info("gr Type List Query: "+strQuery.toString());
        Query query = hibSession.createSQLQuery(strQuery.toString());
        fileId = query.list().get(0).toString();  
        return fileId;
    }

    @Override
    public List getResponseFileDtls(String fileNumber)
    {
        Session session = getSession();
        List lLstReturnList = null;
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT PRAMAAN_ID  ||'|'|| aadhaar ||'|'|| CASE WHEN status=1 THEN 'S' WHEN  status=-1 THEN 'F' END ||'|'|| REMARKS  FROM JEEVAN_PRAMAN_PATRA_DTLS");
        sb.append(" where PRAMAN_PATRA_ID = '"+fileNumber+"' and status in (1,-1) order by SR_NO asc");
        logger.info("the query to insert getFileDtls is********"+sb.toString());
        Query selectQuery = session.createSQLQuery(sb.toString());
        lLstReturnList=  selectQuery.list();


        return lLstReturnList;
    }

	@Override
	public List getAllValidCaseDtls(Long postId) {
        List ppoList = null;
        Session hibSession = getSession();
        StringBuffer strQuery = new StringBuffer();
 
        strQuery.append(" select praman.PRAMAN_PATRA_ID,COUNT(1) from JEEVAN_PRAMAN_PATRA_DTLS jeevan inner join TRN_PENSION_RQST_HDR trn on trn.PPO_NO=jeevan.PPO_NUMBER inner join MST_PENSIONER_HDR hdr on hdr.PENSIONER_CODE = trn.PENSIONER_CODE inner join MST_PENSIONER_DTLS mst on mst.PENSIONER_CODE = trn.PENSIONER_CODE inner join JEEVAN_PRAMAN_PATRA praman on praman.PRAMAN_PATRA_ID=jeevan.PRAMAN_PATRA_ID ");
        strQuery.append(" where jeevan.status not in (1,-1) and jeevan.TREASYRY_CODE in  (SELECT loc_id FROM ORG_POST_DETAILS_RLT where post_id = '"+postId+"') and mst.BRANCH_CODE in (select BRANCH_CODE from RLT_AUDITOR_BANK where post_id= '"+postId+"')  group by  praman.PRAMAN_PATRA_ID ");
       
        logger.info("ID  List Query: "+strQuery.toString());
        Query query = hibSession.createSQLQuery(strQuery.toString());
        ppoList = query.list();  
        return ppoList;
    }

	@Override
	public void rejectDetails(String ppoId, String remarks, Long lngPostId) {
		 try 
	        {
	            //To update The life certificate date....
	            Session session = getSession();
	            logger.info("Inside createNewGR");
	            StringBuilder SBQuery = new StringBuilder();
	            SBQuery.append(" update JEEVAN_PRAMAN_PATRA_DTLS set updated_date=sysdate,Updated_by ='"+lngPostId+"',remarks='"+remarks+"',status=-1 where PPO_NUMBER ='"+ppoId+"' ");
	            Query lQuery = session.createSQLQuery(SBQuery.toString());
	            logger.info("the query to insert rejectDetails is********"+lQuery.toString());
	            lQuery.executeUpdate();

	        } 

	        catch (Exception e) {
	            e.printStackTrace();
	            logger.error("Error is :" + e, e);

	        }
		
	}
}
