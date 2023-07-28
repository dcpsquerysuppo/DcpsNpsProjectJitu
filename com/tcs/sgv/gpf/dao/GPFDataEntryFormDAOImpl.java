/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Dec 2, 2011		Jayraj Chudasama								
 *******************************************************************************
 */
package com.tcs.sgv.gpf.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

/**
 * Class Description -
 * 
 * 
 * @author Jayraj Chudasama
 * @version 0.1
 * @since JDK 5.0 Dec 2, 2011
 */
public class GPFDataEntryFormDAOImpl extends GenericDaoHibernateImpl implements GPFDataEntryFormDAO {

	Session ghibSession = null;

	public GPFDataEntryFormDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	public List getEmployeeNameAndPay(String lStrEmpCode, String lStrLocCode) throws Exception {
		List lLstEmpDtls = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT ME.name,ME.basicPay ");
			lSBQuery.append(" FROM MstEmp ME, OrgDdoMst ODM ");
			lSBQuery.append(" WHERE ODM.locationCode =:locationCode ");
			lSBQuery.append(" AND ME.ddoCode = ODM.ddoCode AND ME.sevarthId = UPPER(:sevarthId) ");
			lSBQuery.append(" AND ME.dcpsOrGpf = 'N' AND ME.group = 'D' ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("sevarthId", lStrEmpCode);
			lQuery.setParameter("locationCode", lStrLocCode);
			lLstEmpDtls = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return lLstEmpDtls;
	}

	public String getGpfAccNo(String lStrSevaarthId) throws Exception {
		String lStrGpfAccNo = "";

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT gpfAccNo");
			lSBQuery.append(" FROM MstEmpGpfAcc");
			lSBQuery.append(" WHERE sevaarthId = :sevaarthId");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("sevaarthId", lStrSevaarthId);
			if( lQuery.list()!=null &&  lQuery.list().size()> 0){
			lStrGpfAccNo = lQuery.list().get(0).toString();
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			//throw e;
		}
		return lStrGpfAccNo;
	}

	public String getEmpGpfAccID(String lStrSevaarthId) throws Exception {
		String lStrEmpGpfAccID = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT mstEmpGpfAccId");
			lSBQuery.append(" FROM MstEmpGpfAcc");
			lSBQuery.append(" WHERE sevaarthId = :sevaarthId");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("sevaarthId", lStrSevaarthId);
			if(lQuery.list()!=null && lQuery.list().size() > 0){
			lStrEmpGpfAccID = lQuery.list().get(0).toString();
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw e;
		}

		return lStrEmpGpfAccID;
	}

	public List getNRADetails(String lStrGpfAccNo, String lStrReqType) throws Exception {
		List lLstNRADetails = null;
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery
					.append("SELECT CLM.lookupName,MGA.amountSanctioned,MGA.sanctionedDate,MGA.voucherNo,MGA.voucherDate,MGA.noOfInstallments,MGA.installmentAmount,MGA.mstGpfAdvanceId,MGA.purposeCategory, ");
			lSBQuery
					.append("MGA.voucherNo2, MGA.voucherDate2, MGA.voucherNo3, MGA.voucherDate3, MGA.voucherNo4, MGA.voucherDate4, MGA.otherPurpose, MGA.installmentsLeft ");
			lSBQuery.append("FROM MstGpfAdvance MGA,CmnLookupMst CLM ");
			lSBQuery
					.append("WHERE MGA.advanceType='NRA' AND CLM.lookupId=MGA.purposeCategory AND MGA.gpfAccNo = :gpfAccNo ");
			/*if (lStrReqType.equals("Draft")) {
				lSBQuery.append("AND MGA.statusFlag='D' ");
			} else if (lStrReqType.equals("Forward")) {
				lSBQuery.append("AND MGA.statusFlag='F' ");
			}*/
			//lSBQuery.append("AND MGA.statusFlag='A' ");
			if (lStrReqType.equals("Draft")) {
				lSBQuery.append(" AND MGA.statusFlag in ('D','X') ");
			} else if (lStrReqType.equals("Forward")) {
				//lSBQuery.append(" AND MGA.statusFlag='A' ");
				lSBQuery.append(" AND MGA.statusFlag='F' ");
			}
			else if (lStrReqType.equals("Reject")) {
				lSBQuery.append(" AND MGA.statusFlag='X' ");
			}
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
			lLstNRADetails = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw e;
		}

		return lLstNRADetails;
	}

	public List getRADetailsHis(String lStrGpfAccNo, String lStrReqType) throws Exception {
		List lLstRADetails = null;
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery
					.append("SELECT CLM.lookupName,MGA.amountSanctioned,MGA.sanctionedDate,MGA.voucherNo,MGA.voucherDate,MGA.noOfInstallments, ");
			lSBQuery
					.append("MGA.installmentAmount,MGA.oddInstallment,MGA.installmentsLeft,MGA.mstGpfAdvanceId,MGA.purposeCategory,MGA.otherPurpose ");
			lSBQuery.append("FROM MstGpfAdvance MGA,CmnLookupMst CLM ");
			lSBQuery
					.append("WHERE MGA.advanceType='RA' AND CLM.lookupId = MGA.purposeCategory AND MGA.gpfAccNo = :gpfAccNo ");
			/*if (lStrReqType.equals("Draft")) {
				lSBQuery.append("AND MGA.statusFlag='D' ");
			} else if (lStrReqType.equals("Forward")) {
				lSBQuery.append("AND MGA.statusFlag='F' ");
			}*/
			//lSBQuery.append("AND MGA.statusFlag='A' ");
			if (lStrReqType.equals("Draft")) {
				lSBQuery.append(" AND MGA.statusFlag in ('DC','X') ");
			} else if (lStrReqType.equals("Forward")) {
				lSBQuery.append(" AND MGA.statusFlag='AC' ");
			}
			else if (lStrReqType.equals("Reject")) {
				lSBQuery.append(" AND MGA.statusFlag='X' ");
			}
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
			lLstRADetails = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw e;
		}

		return lLstRADetails;
	}

	public List getRADetailsCur(String lStrGpfAccNo, String lStrReqType) throws Exception {
		List lLstRADetails = null;
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery
					.append("SELECT CLM.lookupName,MGA.amountSanctioned,MGA.noOfInstallments,MGA.installmentAmount,MGA.installmentsLeft,MGA.oddInstallment, ");
			lSBQuery
					.append("MGA.sanctionedDate,MGA.voucherNo,MGA.voucherDate,MGA.purposeCategory,MGA.mstGpfAdvanceId,MGA.purposeCategory,MGA.otherPurpose,MGA.outstandingInstallmentPrevFinYear ");
			lSBQuery.append("FROM MstGpfAdvance MGA, CmnLookupMst CLM ");
			lSBQuery
					.append("WHERE MGA.advanceType='RA' AND CLM.lookupId = MGA.purposeCategory AND MGA.gpfAccNo = :gpfAccNo ");
			/*if (lStrReqType.equals("Draft")) {
				lSBQuery.append("AND MGA.statusFlag='DC' ");
			} else if (lStrReqType.equals("Forward")) {
				lSBQuery.append("AND MGA.statusFlag='FC' ");
			}*/
			if (lStrReqType.equals("Draft")) {
				lSBQuery.append(" AND MGA.statusFlag in ('DC','X') ");
			} else if (lStrReqType.equals("Forward")) {
				lSBQuery.append(" AND MGA.statusFlag='AC' ");
			}
			else if (lStrReqType.equals("Reject")) {
				lSBQuery.append(" AND MGA.statusFlag='X' ");
			}
		//	lSBQuery.append("AND MGA.statusFlag='DC' ");
			
			
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
			lLstRADetails = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw e;
		}

		return lLstRADetails;
	}

	public List getSubscriptionDetails(String lStrGpfAccNo, String lStrReqType) throws Exception {
		List lLstSubDetails = null;
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery
					.append("SELECT MGE.monthlySubscription,SMM.monthName,SFY.finYearCode,MGE.gpfEmpSubscriptionId,MGE.effectFromMonth,MGE.finYearId ");
			lSBQuery.append("FROM MstGpfEmpSubscription MGE,SgvcFinYearMst SFY,SgvaMonthMst SMM ");
			lSBQuery
					.append("WHERE MGE.effectFromMonth = SMM.monthId AND MGE.finYearId = SFY.finYearId AND MGE.gpfAccNo = :gpfAccNo ");
			lSBQuery.append("AND MGE.statusFlag='A' ");
			lSBQuery.append("ORDER BY SMM.monthId");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
			lLstSubDetails = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw e;
		}

		return lLstSubDetails;
	}

	public List getScheduleDetails(String lStrGpfAccNo, String lStrReqType) throws Exception {
		List lLstVoucherDetails = null;
		List lLstChallanDetails = null;
		try {
			StringBuilder lSBQueryVchr = new StringBuilder();
			/*
			  lSBQueryVchr
					.append("SELECT 'Voucher',SMM.monthName,SFY.finYearCode,MGM.voucherNo,MGM.voucherDate,MGM.advanceRecovery,MGM.instNo,MGM.mstGpfMonthlyId,MGM.monthId,MGM.finYearId ");
			lSBQueryVchr.append("FROM MstGpfMonthly MGM,SgvcFinYearMst SFY,SgvaMonthMst SMM ");
			lSBQueryVchr
					.append(" WHERE  MGM.monthId = SMM.monthId AND MGM.finYearId = SFY.finYearId AND MGM.gpfAccNo = :gpfAccNo AND MGM.dataEntry = 1");
			if (lStrReqType.equals("Draft")) {
				lSBQueryVchr.append(" AND MGM.status in ('D','X')");
			} else if (lStrReqType.equals("Forward")) {
				lSBQueryChallan.append("AND statusFlag = 'F'");
			}
			else if (lStrReqType.equals("Reject")) {
				lSBQueryVchr.append(" AND MGM.status='X'");
			}
			else{
			lSBQueryVchr.append(" AND MGM.status = 'F'");
			}
			lSBQueryVchr.append("ORDER BY MGM.finYearId, SMM.monthId");
			*/
			lSBQueryVchr.append(" SELECT 'Voucher' ,SMM.month_Name,SFY.FIN_YEAR_CODE,MGM.VOUCHER_NO,MGM.voucher_Date,MGM.ADVANCE_RECOVERY,MGM.INST_NO,MGM.MST_GPF_MONTHLY_ID,MGM.month_Id, ");
			lSBQueryVchr.append(" case when MGM.month_Id between 4 and 12 then cast( SFY.FIN_YEAR_CODE as bigint) else (cast( SFY.FIN_YEAR_CODE as bigint) + 1 ) end  ");
			lSBQueryVchr.append(" FROM MST_GPF_MONTHLY MGM,SGVC_FIN_YEAR_MST SFY,SGVA_MONTH_MST SMM  ");
			lSBQueryVchr.append(" WHERE  MGM.month_Id = SMM.month_Id AND MGM.fin_Year_Id = SFY.FIN_YEAR_ID AND MGM.GPF_ACC_NO = :gpfAccNo AND MGM.data_Entry = 1 ");
			if (lStrReqType.equals("Draft")) {
				lSBQueryVchr.append(" AND MGM.status in ('D','X')");
			} 
			else if (lStrReqType.equals("Reject")) {
				lSBQueryVchr.append(" AND MGM.status='X'");
			}
			else{
			lSBQueryVchr.append(" AND MGM.status = 'F'");
			}
			lSBQueryVchr.append("ORDER BY MGM.fin_Year_Id, SMM.month_Id");
			
			Query lQueryVchr = ghibSession.createSQLQuery(lSBQueryVchr.toString());
			lQueryVchr.setParameter("gpfAccNo", lStrGpfAccNo);
			lLstVoucherDetails = lQueryVchr.list();

			StringBuilder lSBQueryChallan = new StringBuilder();
			lSBQueryChallan
					.append("SELECT 'Challan',challanNo,challanDate,amount,instFrom,instTo,mstGpfChallanDtlsId ");
			lSBQueryChallan.append("FROM MstGpfChallanDtls ");
			lSBQueryChallan.append("WHERE gpfAccNo = :gpfAccNo ");
			if (lStrReqType.equals("Draft")) {
				lSBQueryChallan.append(" AND statusFlag in ('D','X') ");
			} /*else if (lStrReqType.equals("Forward")) {
				lSBQueryChallan.append("AND statusFlag = 'F'");
			}*/
			else if (lStrReqType.equals("Reject")) {
				lSBQueryChallan.append(" AND statusFlag = 'X'");
			}
			else{
			lSBQueryChallan.append(" AND statusFlag = 'F'");
			}
			logger.error(" lSBQueryVchr: " +lSBQueryVchr);
			Query lQueryChln = ghibSession.createQuery(lSBQueryChallan.toString());
			lQueryChln.setParameter("gpfAccNo", lStrGpfAccNo);
			lLstChallanDetails = lQueryChln.list();

			lLstVoucherDetails.addAll(lLstChallanDetails);
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			
			throw e;
		}

		return lLstVoucherDetails;
	}

	public List getEmpListForVerification(String lStrLocationCode) throws Exception {
		List lLstEmpDtls = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT TEG.name, TEG.sevaarthId, MEG.currentBalance, MEG.monthlySubscription, TEG.deoRemarks, TEG.trnEmpGpfAccId ");
			lSBQuery.append("FROM TrnEmpGpfAcc TEG, MstEmpGpfAcc MEG, OrgDdoMst ODM ");
			lSBQuery.append("WHERE TEG.statusFlag = 'F' AND TEG.sevaarthId = MEG.sevaarthId AND MEG.ddoCode = ODM.ddoCode ");
			lSBQuery.append("AND ODM.locationCode = :LocationCode");
			
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("LocationCode", lStrLocationCode);
			lLstEmpDtls = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw e;
		}

		return lLstEmpDtls;
	}

	public String getBillGroupId(String lStrSevaarthId) throws Exception {
		String lStrBillGroupId = "";

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT billGroupId ");
			lSBQuery.append("FROM MstEmp ");
			lSBQuery.append("WHERE sevarthId = :sevarthId");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("sevarthId", lStrSevaarthId);
			if(lQuery.list()!=null && lQuery.list().size() > 0){
				if(lQuery.list().get(0) != null)
			lStrBillGroupId = lQuery.list().get(0).toString();
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw e;
		}

		return lStrBillGroupId;
	}

	public String getTranIdForRAAdvance(String lStrGpfAccNo, String lStrReqType) throws Exception {
		String lStrTranId = "";

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT transactionId ");
			lSBQuery.append("FROM MstGpfAdvance ");
			/*if (lStrReqType.equals("Draft")) {
				lSBQuery.append("WHERE statusFlag ='DC' AND gpfAccNo = :gpfAccNo");
			} else if (lStrReqType.equals("Forward")) {
				lSBQuery.append("WHERE statusFlag ='FC' AND gpfAccNo = :gpfAccNo");
			}*/
			lSBQuery.append("WHERE statusFlag ='AC' AND gpfAccNo = :gpfAccNo");
			
			
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
			if (lQuery.list().size() > 0) {
				lStrTranId = lQuery.list().get(0).toString();
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw e;
		}

		return lStrTranId;
	}

	public String getDdoCodeForDDO(Long lLngPostId) throws Exception {

		String lStrDdoCode = null;
		List lLstDdoDtls = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT OD.ddoCode");
			lSBQuery.append(" FROM  OrgDdoMst OD");
			lSBQuery.append(" WHERE OD.postId = :postId ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("postId", lLngPostId);

			lLstDdoDtls = lQuery.list();
			if(lLstDdoDtls != null && lLstDdoDtls.size() > 0)
			lStrDdoCode = lLstDdoDtls.get(0).toString();

		} catch (Exception e) {
			logger.error("Error is :" + e, e);

		}
		return lStrDdoCode;
	}

	public List getEmpListForDraftReq(String lStrLocationCode) throws Exception {

		List lLstEmpDtls = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT TEG.name, TEG.sevaarthId, MEG.currentBalance, MEG.monthlySubscription, TEG.ddoRemarks ");
			lSBQuery.append("FROM TrnEmpGpfAcc TEG, MstEmpGpfAcc MEG, OrgDdoMst ODM ");
			lSBQuery.append("WHERE TEG.statusFlag in ('D','X') AND TEG.sevaarthId = MEG.sevaarthId AND MEG.ddoCode = ODM.ddoCode ");
			lSBQuery.append("AND ODM.locationCode = :LocCode");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("LocCode", lStrLocationCode);
			
			lLstEmpDtls = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			
			throw e;
		}

		return lLstEmpDtls;
	}

	public String getTrnEmpGpfAccID(String lStrSevaarthId, String lStrReqType) throws Exception {
		String lStrEmpGpfAccID = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT trnEmpGpfAccId");
			lSBQuery.append(" FROM TrnEmpGpfAcc");
			lSBQuery.append(" WHERE sevaarthId = :sevaarthId");
			if (lStrReqType.equals("Draft")) {
				lSBQuery.append(" AND statusFlag in ('D','X')");
			/*} else if (lStrReqType.equals("Forward")) {
				lSBQuery.append(" AND statusFlag = 'F'");
			}*/
			}
			else if (lStrReqType.equals("Reject")) {
				lSBQuery.append(" AND statusFlag = 'X'");
			}
			else{
				lSBQuery.append(" AND statusFlag = 'F'");
			}
			
			
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("sevaarthId", lStrSevaarthId);
			lStrEmpGpfAccID = lQuery.list().get(0).toString();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			
			throw e;
		}

		return lStrEmpGpfAccID;
	}

	public List getPrevDetailsVoucher(String lStrGpfAccNo) throws Exception {
		List lLstPrvVoucher = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("FROM MstGpfMonthly ");
			lSBQuery.append("WHERE status='A' AND gpfAccNo = :gpfAccNo ");
			Query lQueryVchr = ghibSession.createQuery(lSBQuery.toString());
			lQueryVchr.setParameter("gpfAccNo", lStrGpfAccNo);
			lLstPrvVoucher = lQueryVchr.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw e;
		}

		return lLstPrvVoucher;
	}

	public List getPrevDetailsChallan(String lStrGpfAccNo) throws Exception {
		List lLstPrvVoucher = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("FROM MstGpfChallanDtls ");
			lSBQuery.append("WHERE gpfAccNo = :gpfAccNo AND statusFlag = 'A'");
			Query lQueryVchr = ghibSession.createQuery(lSBQuery.toString());
			lQueryVchr.setParameter("gpfAccNo", lStrGpfAccNo);
			lLstPrvVoucher = lQueryVchr.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw e;
		}

		return lLstPrvVoucher;
	}

	public List chkIfEmpExist(String lStrGpfAccNo) throws Exception {
		List lLstEmpDtls = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("FROM TrnEmpGpfAcc ");
			lSBQuery.append("WHERE statusFlag IN ('F','A') AND gpfAccNo = :gpfAccNo");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
			lLstEmpDtls = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw e;
		}

		return lLstEmpDtls;
	}

	public List getAdvanceDetails(String lStrGpfAccNo) throws Exception {
		List lLstAdvanceDetails = null;
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("FROM MstGpfAdvance ");
			lSBQuery.append("WHERE statusFlag IN('A','AC') AND gpfAccNo = :gpfAccNo ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
			lLstAdvanceDetails = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw e;
		}

		return lLstAdvanceDetails;
	}

	public List getAdvanceDetailsNew(String lStrGpfAccNo) throws Exception {
		List lLstAdvanceDetails = null;
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("FROM MstGpfAdvance ");
			lSBQuery.append("WHERE statusFlag IN('F','AC') AND gpfAccNo = :gpfAccNo ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
			lLstAdvanceDetails = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw e;
		}

		return lLstAdvanceDetails;
	}
	
	public void updateTrnEmpGpfAcc(String lStrPk, String lStrDeoRemark, String lStrDdoRemark, String lStrReqType)
			throws Exception {
		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;

		try {
			lSBQuery.append("update TrnEmpGpfAcc ");
			if (lStrReqType.equals("Save")) {
				lSBQuery.append(" set deoRemarks = :deoRemarks, statusFlag='D'");
				lSBQuery.append(" WHERE trnEmpGpfAccId = :lStrPk");
				lQuery = ghibSession.createQuery(lSBQuery.toString());
				lQuery.setParameter("deoRemarks", lStrDeoRemark);
				lQuery.setParameter("lStrPk", Long.parseLong(lStrPk));
			} else if (lStrReqType.equals("Forward")) {
				lSBQuery.append(" set deoRemarks = :deoRemarks, statusFlag='F'");
				lSBQuery.append(" WHERE trnEmpGpfAccId = :lStrPk");
				lQuery = ghibSession.createQuery(lSBQuery.toString());
				lQuery.setParameter("deoRemarks", lStrDeoRemark);
				lQuery.setParameter("lStrPk", Long.parseLong(lStrPk));
			} else if (lStrReqType.equals("Approve")) {
				lSBQuery.append(" set ddoRemarks = :ddoRemarks, statusFlag='A'");
				//lSBQuery.append(" set statusFlag='A' ");
				lSBQuery.append(" WHERE trnEmpGpfAccId = :lStrPk ");
				lQuery = ghibSession.createQuery(lSBQuery.toString());
				if(lStrDdoRemark!=""){
				lQuery.setParameter("ddoRemarks", lStrDdoRemark);
				}
				else{
					lQuery.setParameter("ddoRemarks", null);
				}
				lQuery.setParameter("lStrPk", Long.parseLong(lStrPk));
			} else if (lStrReqType.equals("Reject")) {
				lSBQuery.append(" set ddoRemarks = :ddoRemarks, statusFlag='X'");
				lSBQuery.append(" WHERE trnEmpGpfAccId = :lStrPk");
				lQuery = ghibSession.createQuery(lSBQuery.toString());
				lQuery.setParameter("ddoRemarks", lStrDdoRemark);
				lQuery.setParameter("lStrPk", Long.parseLong(lStrPk));
			}

			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			
			throw e;
		}
	}

	public void updateMstEmpGpfAcc(String lStrSevaarthID, String lStrCurBalance, String lStrMonthlySubscription)
			throws Exception {
		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;

		try {
			lSBQuery.append("update MstEmpGpfAcc");
			lSBQuery.append(" set currentBalance = :currentBalance, monthlySubscription = :monthlySubscription");
			lSBQuery.append(" WHERE sevaarthId = :sevaarthId");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("currentBalance", Double.parseDouble(lStrCurBalance));
			lQuery.setParameter("monthlySubscription", Double.parseDouble(lStrMonthlySubscription));
			lQuery.setParameter("sevaarthId", lStrSevaarthID);
			
			lQuery.executeUpdate();
		
		} catch (Exception e) {
			
			logger.error(" Error is : " + e, e);
			throw e;
		}
	}

	public void updateEmpSubscription(String lStrSubscriptionID, String lStrReqType) throws Exception {
		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;

		try {
			lSBQuery.append("update MstGpfEmpSubscription ");
			if (lStrReqType.equals("Approve")) {
				lSBQuery.append("set statusFlag = 'A' ");
			} else if (lStrReqType.equals("Discard")) {
				lSBQuery.append("set statusFlag = 'X' ");
			}
			lSBQuery.append("WHERE gpfEmpSubscriptionId = :gpfEmpSubscriptionId");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfEmpSubscriptionId", Long.parseLong(lStrSubscriptionID));
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw e;
		}
	}

	public void updateGpfMonthly(Long lLngMonthlyID, Long lLngMonth, Long lLngFinYear, String lStrGpfAccNo)
			throws Exception {
		Double lDblRegularSub = null;
		try {
			lDblRegularSub = getMonthlySubscription(lLngMonth, lLngFinYear, lStrGpfAccNo);

			// Add Regular Subscription in MstGpfMonthly table
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("update MstGpfMonthly ");
			lSBQuery.append("set status = 'A' , regularSubscription =:regularSubscription ");
			lSBQuery.append("WHERE mstGpfMonthlyId = :mstGpfMonthlyId");
			Query lQuery = null;
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("mstGpfMonthlyId", lLngMonthlyID);
			lQuery.setParameter("regularSubscription", lDblRegularSub);
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw e;
		}
	}

	public void discardGPFMonthly(String lStrMonthlyID) throws Exception {
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("update MstGpfMonthly ");
			lSBQuery.append("set status = 'X' WHERE mstGpfMonthlyId = :mstGpfMonthlyId ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("mstGpfMonthlyId", Long.parseLong(lStrMonthlyID));
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw e;
		}
	}

	public void updateChallanDetails(String lStrChallanID, String lStrReqType) throws Exception {
		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;

		try {
			lSBQuery.append("update MstGpfChallanDtls ");
			if (lStrReqType.equals("Approve")) {
				lSBQuery.append("set statusFlag = 'A' ");
			} else if (lStrReqType.equals("Reject")) {
				lSBQuery.append("set statusFlag = 'X' ");
			}
			lSBQuery.append("WHERE mstGpfChallanDtlsId = :mstGpfChallanDtlsId");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("mstGpfChallanDtlsId", Long.parseLong(lStrChallanID));
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw e;
		}
	}

	public void updateAdvanceDetails(String lStrAdvanceID) throws Exception {
		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;

		try {
			lSBQuery.append("update MstGpfAdvance ");
			lSBQuery.append("set statusFlag = 'X' WHERE mstGpfAdvanceId = :mstGpfAdvanceId");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("mstGpfAdvanceId", Long.parseLong(lStrAdvanceID));
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw e;
		}
	}

	public Long getMonthlyIDForMonth(String lStrGpfAccNo, Integer lIntMonthId, Long lLngYearId) throws Exception {
		List lLstScheduleData = new ArrayList();
		Long lLngMonthlyID = null;
		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append("SELECT mstGpfMonthlyId from MstGpfMonthly ");
			lSBQuery.append("WHERE gpfAccNo= :gpfAccNo AND finYearId = :finYearId AND monthId =:monthId ");
			lSBQuery.append("AND status in ( 'D','F') ");
			
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			logger.info("Exception in isDataExistInMonthly of GPFDataEntryFormDAOImpl  : "+lSBQuery);
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
			lQuery.setParameter("finYearId", lLngYearId);
			lQuery.setParameter("monthId", lIntMonthId.longValue());

			lLstScheduleData = lQuery.list();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception in isDataExistInMonthly of GPFDataEntryFormDAOImpl  : ", e);
		}
		if (lLstScheduleData != null && lLstScheduleData.size() > 0) {
			lLngMonthlyID = (Long) lLstScheduleData.get(0);
		}
		return lLngMonthlyID;
	}

	public Double getMonthlySubscription(Long lLngMonth, Long lLngFinYear, String lStrGpfAccNo) throws Exception {

		Double lDblRegularSub = null;
		List lLstRegularSubDtls = null;
		Query lQuerySub = null;
		Integer lIntMonth = Integer.parseInt(lLngMonth.toString());
		try {
			StringBuilder lSBQuerySub = new StringBuilder();
			if (lIntMonth >= 1 && lIntMonth <= 3) {
				lSBQuerySub
						.append("SELECT monthlySubscription FROM MstGpfEmpSubscription WHERE effectFromMonth <= :effectFromMonth"
								+ " AND finYearId = :finYearId AND gpfAccNo = :gpfAccNo AND statusFlag != 'X' ORDER BY effectFromMonth DESC");
				lQuerySub = ghibSession.createQuery(lSBQuerySub.toString());
				lQuerySub.setParameter("finYearId", lLngFinYear);
				lQuerySub.setParameter("gpfAccNo", lStrGpfAccNo);
				lQuerySub.setParameter("effectFromMonth", lIntMonth);
				lLstRegularSubDtls = lQuerySub.list();

				if (lLstRegularSubDtls.size() == 0) {
					lSBQuerySub = new StringBuilder();
					lSBQuerySub
							.append("SELECT monthlySubscription FROM MstGpfEmpSubscription WHERE "
									+ "finYearId = :prevFinYearId AND gpfAccNo = :gpfAccNo AND statusFlag != 'X' ORDER BY effectFromMonth DESC");
					lQuerySub = ghibSession.createQuery(lSBQuerySub.toString());
					lQuerySub.setParameter("prevFinYearId", lLngFinYear - 1);
					lQuerySub.setParameter("gpfAccNo", lStrGpfAccNo);
					lLstRegularSubDtls = lQuerySub.list();
				}
			} else {
				lSBQuerySub
						.append("SELECT monthlySubscription FROM MstGpfEmpSubscription WHERE effectFromMonth <= :effectFromMonth "
								+ "AND finYearId = :finYearId AND gpfAccNo = :gpfAccNo AND statusFlag != 'X' ORDER BY effectFromMonth DESC");
				lQuerySub = ghibSession.createQuery(lSBQuerySub.toString());
				lQuerySub.setParameter("effectFromMonth", lIntMonth);
				lQuerySub.setParameter("finYearId", lLngFinYear);
				lQuerySub.setParameter("gpfAccNo", lStrGpfAccNo);
				lLstRegularSubDtls = lQuerySub.list();
			}

			if (lLstRegularSubDtls != null && !lLstRegularSubDtls.isEmpty()) {
				lDblRegularSub = (Double) lLstRegularSubDtls.get(0);
			} else {
				lSBQuerySub = new StringBuilder();
				lSBQuerySub.append("SELECT monthlySubscription FROM MstEmpGpfAcc WHERE gpfAccNo = :gpfAccNo");
				Query lQueryEmpAcc = null;
				lQueryEmpAcc = ghibSession.createQuery(lSBQuerySub.toString());
				lQueryEmpAcc.setParameter("gpfAccNo", lStrGpfAccNo);
				lDblRegularSub = (Double) lQueryEmpAcc.list().get(0);
			}
		} catch (Exception e) {
			logger.error("Exception in getMonthlySubscription of GPFDataEntryFormDAOImpl  : ", e);
		}

		return lDblRegularSub;
	}

	public Double getChallanPaidForMonth(String lStrGpfAccNo, Integer lLngMonthId, String lStrFinYear,
			Long lLngChallanType) throws Exception {

		List lLstChallan = null;
		Double lDblChallanAmount = 0d;
		Integer lIntYearCode = Integer.parseInt(lStrFinYear.substring(0, 4));
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT SUM(amount)");
			lSBQuery.append(" FROM MstGpfChallanDtls");
			lSBQuery
					.append(" WHERE gpfAccNo=:gpfAccNo AND MONTH(challanDate) =:monthId AND YEAR(challanDate)=:yearCode AND challanType=:challanType ");
			lSBQuery.append(" AND statusFlag != 'X' ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
			lQuery.setParameter("monthId", lLngMonthId);
			lQuery.setParameter("yearCode", lIntYearCode);
			lQuery.setParameter("challanType", lLngChallanType);

			lLstChallan = lQuery.list();
			if (lLstChallan != null && lLstChallan.size() > 0 && lLstChallan.get(0) != null) {
				lDblChallanAmount = (Double) lLstChallan.get(0);
			}
		} catch (Exception e) {
			logger.error("Exception in getChallanPaidForMonth : ", e);
		}
		return lDblChallanAmount;
	}

	public String chkEntryForSevaarthId(String lStrSevaarthId) throws Exception {
		List lLstResData = null;
		String lStrStatus = "Pending";
		String lStrflag="";
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT statusFlag FROM TrnEmpGpfAcc ");
			lSBQuery.append("WHERE sevaarthId = :sevaarthId ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("sevaarthId", lStrSevaarthId);
			lLstResData = lQuery.list();
			if(lLstResData!=null && lLstResData.size() > 0){
			lStrflag=lLstResData.get(0).toString();
			if(lStrflag.equalsIgnoreCase("D"))
			{
				 lStrStatus=lLstResData.get(0).toString();;
			}
			else if(lStrflag.equalsIgnoreCase("A"))
			{
				 lStrStatus=lLstResData.get(0).toString();;
			}
			else if(lStrflag.equalsIgnoreCase("F"))
			{
				 lStrStatus=lLstResData.get(0).toString();;
			}
			else
			{
				lStrStatus="Pending";
				return lStrStatus; 
			}
			}
			/*if (lLstResData != null && lLstResData.size() > 0) {
				lStrStatus = lLstResData.get(0).toString();
			} else {
				lStrStatus = "Pending";
			}*/
		} catch (Exception e) {
			logger.error("Exception in chkEntryForSevaarthId  : ", e);
		}

		return lStrStatus;
	}

	public List getSubscriptionDataFromPayroll(String lStrFinYearCode, Integer lIntCurMonth, String lStrEmpCode) {
		List lLstResData = null;
		String lStrStartYearCode = lStrFinYearCode.substring(0, 4);
		String lStrEndYearCode = lStrFinYearCode.substring(5, 9);
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT pb.EMP_ID, pb.GPF_GRP_D, pb.GPF_ADV_GRP_D, pb.GPF_ADV_GRP_D_INT,hd.PAYBILL_MONTH,hd.VOUCHER_NO,hd.VOUCHER_DATE,hd.paybill_year");
			lSBQuery.append(" FROM HR_PAY_PAYBILL pb, PAYBILL_HEAD_MPG hd, HR_EIS_EMP_MST eis");
			lSBQuery
					.append(" where pb.GPF_ADV_GRP_D <> 0 and pb.PAYBILL_GRP_ID =hd.PAYBILL_ID and eis.EMP_ID = pb.EMP_ID and hd.APPROVE_FLAG=1 and eis.SEVARTH_EMP_CD=:sevaarthId"
							+ " and ((hd.PAYBILL_MONTH>=4 and hd.PAYBILL_YEAR= :startYearCode) OR (hd.PAYBILL_MONTH<4 and hd.PAYBILL_YEAR= :endYearCode))");
							//+ " and hd.PAYBILL_MONTH>=4 and hd.PAYBILL_YEAR >= 2012"); ///to fetch data from 01/04/2012 to till date
			lSBQuery.append(" ORDER BY hd.PAYBILL_YEAR, hd.PAYBILL_MONTH");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("sevaarthId", lStrEmpCode);
			lQuery.setParameter("startYearCode", lStrStartYearCode);
			lQuery.setParameter("endYearCode", lStrEndYearCode);
			lLstResData = lQuery.list();

		} catch (Exception e) {
			logger.error("Exception in chkEntryForSevaarthId  : ", e);
		}

		return lLstResData;
	}

	
	public List getPreviousSubscriptionDataFromPayroll(String lStrFinYearCode, Integer lIntCurMonth, String lStrEmpCode) {
		List lLstResData = null;
		String lStrStartYearCode = lStrFinYearCode.substring(0, 4);
		String lStrEndYearCode = lStrFinYearCode.substring(5, 9);
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT pb.EMP_ID, pb.GPF_GRP_D, pb.GPF_ADV_GRP_D, pb.GPF_ADV_GRP_D_INT,hd.PAYBILL_MONTH,hd.VOUCHER_NO,hd.VOUCHER_DATE ");
			lSBQuery.append(" FROM HR_PAY_PAYBILL pb, PAYBILL_HEAD_MPG hd, HR_EIS_EMP_MST eis");
			lSBQuery
					.append(" where  pb.PAYBILL_GRP_ID =hd.PAYBILL_ID and eis.EMP_ID = pb.EMP_ID and hd.APPROVE_FLAG=1 and eis.SEVARTH_EMP_CD=:sevaarthId"
							+ " and ((hd.PAYBILL_MONTH<=3 and hd.PAYBILL_YEAR= :startYearCode) OR (hd.PAYBILL_MONTH>=4 and hd.PAYBILL_YEAR>= 2012))");
			lSBQuery.append(" ORDER BY hd.PAYBILL_YEAR, hd.PAYBILL_MONTH");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("sevaarthId", lStrEmpCode);
			lQuery.setParameter("startYearCode", lStrStartYearCode);
			//lQuery.setParameter("endYearCode", Long.parseLong(lStrStartYearCode)-1);
			lLstResData = lQuery.list();

		} catch (Exception e) {
			logger.error("Exception in chkEntryForSevaarthId  : ", e);
		}

		return lLstResData;
	}
	
	
	public Object[] getAdvanceDataFromPayroll(String lStrEmpCode) {
		List lLstResData = null;
		Object[] lObjAdvance = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery
					.append("SELECT '',case when HD.LOAN_PRIN_AMT<>0 then HD.LOAN_PRIN_AMT else HD.LOAN_INTEREST_AMT end ,"
							+ "case when HD.LOAN_PRIN_INST_NO<>0 then HD.LOAN_PRIN_INST_NO else HD.LOAN_INT_INST_NO end ,"
							+ "case when HD.LOAN_PRIN_EMI_AMT<>0 then HD.LOAN_PRIN_EMI_AMT else HD.LOAN_INT_EMI_AMT end ,"
							+ "case when HD.LOAN_PRIN_INST_NO<>0 then (select PRD.TOTAL_RECOVERED_INST from HR_LOAN_EMP_PRIN_RECOVER_DTLS PRD where PRD.EMP_LOAN_ID=HD.EMP_LOAN_ID)"
							+ " else (select IR.TOTAL_RECOVERED_INT_INST from HR_LOAN_EMP_INT_RECOVER_DTLS IR where IR.EMP_LOAN_ID=HD.EMP_LOAN_ID) end,"
							+ "ODD_INST_AMT,LOAN_SANC_ORDER_DATE,VOUCHER_NO,VOUCHER_DATE"
							+ " FROM HR_LOAN_EMP_DTLS  HD where HD.LOAN_TYPE_ID= 55 and HD.EMP_ID =(select emp_id from HR_EIS_EMP_MST where SEVARTH_EMP_CD =:sevaarthId)"
							+" order by hd.LOAN_DATE desc fetch first row only");
			///shailesh : edited to overcome the issue of deactive loan details in current financial year 
							//+ " and HD.LOAN_ACTIVATE_FLAG=1");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("sevaarthId", lStrEmpCode);
			lLstResData = lQuery.list();
			if (lLstResData != null && lLstResData.size() > 0) {
				lObjAdvance = (Object[]) lLstResData.get(0);
			}
		} catch (Exception e) {
			logger.error("Exception in chkEntryForSevaarthId  : ", e);
		}

		return lObjAdvance;
	}
	
	public String chkEntryForGpfYearly(String lStrGpfAccNo) throws Exception {
		List lLstResData = null;
		String lStrStatus = "";

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT mstGpfYearlyId FROM MstGpfYearly ");
			lSBQuery.append("WHERE gpfAccNo = :GpfAccNo ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("GpfAccNo", lStrGpfAccNo);
			lLstResData = lQuery.list();

			if (lLstResData != null && lLstResData.size() > 0) {
				lStrStatus = lLstResData.get(0).toString();
			} else {
				lStrStatus = "N";
			}
		} catch (Exception e) {
			logger.error("Exception in chkEntryForGpfYearly  : ", e);
		}

		return lStrStatus;
	}
}
