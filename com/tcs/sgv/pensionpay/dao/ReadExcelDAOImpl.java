/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 21, 2011		Shivani Rana								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.tcs.sgv.common.constant.Constants;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.pensionpay.valueobject.StgCpo;
import com.tcs.sgv.pensionpay.valueobject.StgCpoAlloc;
import com.tcs.sgv.pensionpay.valueobject.StgFamily;
import com.tcs.sgv.pensionpay.valueobject.StgGpoAlloc;
import com.tcs.sgv.pensionpay.valueobject.StgPensioner;
import com.tcs.sgv.pensionpay.valueobject.StgPpoAlloc;
import com.tcs.sgv.pensionpay.valueobject.StgRecovery;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionerRivisionDtls;


/**
 * Class Description -
 * 
 * 
 * @author Shivani Rana
 * @version 0.1
 * @since JDK 5.0 Mar 21, 2011
 */
public class ReadExcelDAOImpl implements ReadExcelDAO {

	/**
	 * @param type
	 */
	private Session ghibSession = null;
	private SessionFactory lSess = null;
	private Log logger = LogFactory.getLog(getClass());
	private ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");
	private Integer gIntRowCnt = 0;

	public ReadExcelDAOImpl(SessionFactory sesFactory) {

		ghibSession = sesFactory.getCurrentSession();
		lSess = sesFactory;
	}

	public void insertIntoTable(Object xlsData[][], int sizeOfList, String lStrFileName, Map inputMap) throws Exception {

		HibernateTemplate hitStg = new HibernateTemplate(lSess);
		List<StgCpoAlloc> lLstTmpCpoAlloc = null;
		List<StgCpo> lLstTmpCpo = null;
		List<StgFamily> lLstTmpFamily = null;
		List<StgGpoAlloc> lLstTmpGpoAlloc = null;
		List<StgPensioner> lLstTmpPensioner = null;
		List<StgPpoAlloc> lLstTmpPpoAlloc = null;
		List<StgRecovery> lLstTmpRecovery = null;

		Long lLngPostId = null;
		Long lLngDelvId = null;
		String lStrFileType = "";
		try {
			lLngPostId = (Long) inputMap.get("lLngPostId");
			lLngDelvId = (Long) inputMap.get("lLngDelvId");
			lStrFileType = (String) inputMap.get("FileType");
			if (bundleConst.getString("FILETYPE.TEXT").equals(lStrFileType)) {
				gIntRowCnt = 0;
			} else {
				gIntRowCnt = 1;
			}

			if (lStrFileName.startsWith(bundleConst.getString("FILE_CPO_ALLOC"))) {
				lLstTmpCpoAlloc = new ArrayList<StgCpoAlloc>();
				lLstTmpCpoAlloc = setStgCpoAlloc(xlsData, sizeOfList, lLngDelvId, lLngPostId);
				hitStg.saveOrUpdateAll(lLstTmpCpoAlloc);
			} else if (lStrFileName.startsWith(bundleConst.getString("FILE_CPO"))) {
				lLstTmpCpo = new ArrayList<StgCpo>();
				lLstTmpCpo = setStgCpo(xlsData, sizeOfList, lLngDelvId, lLngPostId);
				hitStg.saveOrUpdateAll(lLstTmpCpo);

			} else if (lStrFileName.startsWith(bundleConst.getString("FILE_FAMILY"))) {
				lLstTmpFamily = new ArrayList<StgFamily>();
				lLstTmpFamily = setStgFamily(xlsData, sizeOfList, lLngDelvId, lLngPostId);
				hitStg.saveOrUpdateAll(lLstTmpFamily);

			} else if (lStrFileName.startsWith(bundleConst.getString("FILE_GPO_ALLOC"))) {
				lLstTmpGpoAlloc = new ArrayList<StgGpoAlloc>();
				lLstTmpGpoAlloc = setStgGpoAlloc(xlsData, sizeOfList, lLngDelvId, lLngPostId);
				hitStg.saveOrUpdateAll(lLstTmpGpoAlloc);

			} else if (lStrFileName.startsWith(bundleConst.getString("FILE_PENSIONER"))) {
				lLstTmpPensioner = new ArrayList<StgPensioner>();
				lLstTmpPensioner = setStgPensioner(xlsData, sizeOfList, lLngDelvId, lLngPostId);
				hitStg.saveOrUpdateAll(lLstTmpPensioner);

			} else if (lStrFileName.startsWith(bundleConst.getString("FILE_PPO_ALLOC"))) {
				lLstTmpPpoAlloc = new ArrayList<StgPpoAlloc>();
				lLstTmpPpoAlloc = setStgPpoAlloc(xlsData, sizeOfList, lLngDelvId, lLngPostId);
				hitStg.saveOrUpdateAll(lLstTmpPpoAlloc);
			} else if (lStrFileName.startsWith(bundleConst.getString("FILE_RECOVERY"))) {
				lLstTmpRecovery = new ArrayList<StgRecovery>();
				lLstTmpRecovery = setStgRecovery(xlsData, sizeOfList, lLngDelvId, lLngPostId);
				hitStg.saveOrUpdateAll(lLstTmpRecovery);
			}

		} catch (Exception e) {
			logger.error("error occcured in insertIntoTable method ::" + e);
			throw e;
		}

	}

	private List<StgCpoAlloc> setStgCpoAlloc(Object paraXlsData[][], int paraSizeOfList, Long paraLngDelvId, Long paraLngPostId) throws Exception {

		StgCpoAlloc stgCpoAlloc = null;
		List<StgCpoAlloc> lLstTmpCpoAlloc = new ArrayList<StgCpoAlloc>();
		for (int i = gIntRowCnt; i < paraSizeOfList; i++) {
			stgCpoAlloc = new StgCpoAlloc();
			// stgCpoAlloc.setStgCpoAllocId(IFMSCommonServiceImpl.getNextSeqNum(
			// "stg_cpo_alloc", inputMap));
			// stgCpoAlloc.setStgCpoAllocId(Long.valueOf(i));
			stgCpoAlloc.setDelvId(paraLngDelvId);
			stgCpoAlloc.setCaseStatus("NEW");
			stgCpoAlloc.setPnsrFileNo(paraXlsData[i][0].toString());
			stgCpoAlloc.setApplnNo(paraXlsData[i][1].toString());
			stgCpoAlloc.setPnsrPpoNo(paraXlsData[i][2].toString());
			stgCpoAlloc.setCpoNo(paraXlsData[i][3].toString());
			stgCpoAlloc.setAlocFor(paraXlsData[i][4].toString());
			stgCpoAlloc.setAlocAmt(paraXlsData[i][5].toString());
			stgCpoAlloc.setCreatedBy(paraLngPostId);
			stgCpoAlloc.setCreatedDate(DBUtility.getCurrentDateFromDB());
			lLstTmpCpoAlloc.add(stgCpoAlloc);
		}
		return lLstTmpCpoAlloc;
	}

	private List<StgCpo> setStgCpo(Object paraXlsData[][], int paraSizeOfList, Long paraLngDelvId, Long paraLngPostId) throws Exception {

		StgCpo stgCpo = null;
		List<StgCpo> lLstTmpCpo = new ArrayList<StgCpo>();
		for (int i = gIntRowCnt; i < paraSizeOfList; i++) {
			stgCpo = new StgCpo();
			// stgCpo.setStgCpoAllocId(Long.valueOf(i));
			stgCpo.setDelvId(paraLngDelvId);
			stgCpo.setCaseStatus("NEW");
			stgCpo.setPnsrFileNo(paraXlsData[i][0].toString());
			stgCpo.setApplnNo(paraXlsData[i][1].toString());
			stgCpo.setPnsrPpoNo(paraXlsData[i][2].toString());
			stgCpo.setCpoNo(paraXlsData[i][3].toString());
			stgCpo.setCommAmt(paraXlsData[i][4].toString());
			stgCpo.setCvpAmt(paraXlsData[i][5].toString());
			stgCpo.setCpoSancAuNoCode(paraXlsData[i][6].toString());
			stgCpo.setCpoSancAuNo(paraXlsData[i][7].toString());
			stgCpo.setCpoAgoutNo(paraXlsData[i][8].toString());
			stgCpo.setCpoAgoutDate(paraXlsData[i][9].toString());
			stgCpo.setCreatedBy(paraLngPostId);
			stgCpo.setCreatedDate(DBUtility.getCurrentDateFromDB());
			lLstTmpCpo.add(stgCpo);
		}
		return lLstTmpCpo;
	}

	private List<StgFamily> setStgFamily(Object paraXlsData[][], int paraSizeOfList, Long paraLngDelvId, Long paraLngPostId) throws Exception {

		StgFamily stgFamily = null;
		List<StgFamily> lLstTmpFamily = new ArrayList<StgFamily>();
		for (int i = gIntRowCnt; i < paraSizeOfList; i++) {
			stgFamily = new StgFamily();
			// stgCpo.setStgCpoAllocId(Long.valueOf(i));
			stgFamily.setDelvId(paraLngDelvId);
			stgFamily.setCaseStatus("NEW");
			stgFamily.setPnsrFileNo(paraXlsData[i][0].toString());
			stgFamily.setApplnNo(paraXlsData[i][1].toString());
			stgFamily.setRelationship(paraXlsData[i][2].toString());
			stgFamily.setApfName(paraXlsData[i][3].toString());
			stgFamily.setDob(paraXlsData[i][4].toString());
			stgFamily.setApfHandicap(paraXlsData[i][5].toString());
			stgFamily.setEfpFromDate(paraXlsData[i][6].toString());
			stgFamily.setFpFromDate(paraXlsData[i][7].toString());
			stgFamily.setApaEfp(paraXlsData[i][8].toString());
			stgFamily.setApaFp(paraXlsData[i][9].toString());
			stgFamily.setApfGrdnName(paraXlsData[i][10].toString());
			stgFamily.setGdrnReltn(paraXlsData[i][11].toString());
			stgFamily.setCreatedBy(paraLngPostId);
			stgFamily.setCreatedDate(DBUtility.getCurrentDateFromDB());
			lLstTmpFamily.add(stgFamily);
		}
		return lLstTmpFamily;
	}

	private List<StgGpoAlloc> setStgGpoAlloc(Object paraXlsData[][], int paraSizeOfList, Long paraLngDelvId, Long paraLngPostId) throws Exception {

		StgGpoAlloc stgGpoAlloc = null;
		List<StgGpoAlloc> lLstTmpGpoAlloc = new ArrayList<StgGpoAlloc>();
		for (int i = gIntRowCnt; i < paraSizeOfList; i++) {
			stgGpoAlloc = new StgGpoAlloc();
			stgGpoAlloc.setDelvId(paraLngDelvId);
			stgGpoAlloc.setCaseStatus("NEW");
			stgGpoAlloc.setPnsrFileNo(paraXlsData[i][0].toString());
			stgGpoAlloc.setApplnNo(paraXlsData[i][1].toString());
			stgGpoAlloc.setPnsrPpoNo(paraXlsData[i][2].toString());
			stgGpoAlloc.setGpoNo(paraXlsData[i][3].toString());
			stgGpoAlloc.setAlocFor(paraXlsData[i][4].toString());
			stgGpoAlloc.setAlocAmt(paraXlsData[i][5].toString());
			stgGpoAlloc.setCreatedBy(paraLngPostId);
			stgGpoAlloc.setCreatedDate(DBUtility.getCurrentDateFromDB());
			lLstTmpGpoAlloc.add(stgGpoAlloc);
		}
		return lLstTmpGpoAlloc;
	}

	private List<StgPensioner> setStgPensioner(Object paraXlsData[][], int paraSizeOfList, Long paraLngDelvId, Long paraLngPostId) throws Exception {

		StgPensioner stgPensioner = null;
		List<StgPensioner> lLstTmpPensioner = new ArrayList<StgPensioner>();
		for (int i = gIntRowCnt; i < paraSizeOfList; i++) {
			// System.out.println("count = "+i);
			stgPensioner = new StgPensioner();
			stgPensioner.setDelvId(paraLngDelvId);
			stgPensioner.setCaseStatus("NEW");
			stgPensioner.setPnsrFileNo(paraXlsData[i][0].toString().trim());
			stgPensioner.setApplnNo(paraXlsData[i][1].toString().trim());
			stgPensioner.setPnsrPpoNo(paraXlsData[i][2].toString().trim());
			stgPensioner.setPenName(paraXlsData[i][3].toString().replace("'", ""));
			stgPensioner.setPenMname(paraXlsData[i][4].toString().trim().replace("'", ""));
			stgPensioner.setPenSurname(paraXlsData[i][5].toString().trim().replace("'", ""));
			stgPensioner.setApenBrAddr1(paraXlsData[i][6].toString().trim().replace("'", ""));
			stgPensioner.setApenBrAddr2(paraXlsData[i][7].toString().trim().replace("'", ""));
			stgPensioner.setApenBrAddr3(paraXlsData[i][8].toString().trim().replace("'", ""));
			stgPensioner.setBrCity(paraXlsData[i][9].toString().trim().replace("'", ""));
			stgPensioner.setBrState(paraXlsData[i][10].toString().trim().replace("'", ""));
			stgPensioner.setApenBrPin(paraXlsData[i][11].toString().trim().replace("'", ""));
			stgPensioner.setApenArAddr1(paraXlsData[i][12].toString().trim().replace("'", ""));
			stgPensioner.setApenArAddr2(paraXlsData[i][13].toString().trim().replace("'", ""));
			stgPensioner.setApenArAddr3(paraXlsData[i][14].toString().replace("'", ""));
			stgPensioner.setArCity(paraXlsData[i][15].toString().trim().replace("'", ""));
			stgPensioner.setArState(paraXlsData[i][16].toString().trim().replace("'", ""));
			stgPensioner.setApenArPin(paraXlsData[i][17].toString().trim().replace("'", ""));
			stgPensioner.setDtBirth(paraXlsData[i][18].toString().trim());
			stgPensioner.setDtExpiry(paraXlsData[i][19].toString().trim());
			stgPensioner.setDtAppointment(paraXlsData[i][20].toString().trim());
			stgPensioner.setDtRetirement(paraXlsData[i][21].toString().trim());
			stgPensioner.setPenSex(paraXlsData[i][22].toString().trim().replace("'", ""));
			stgPensioner.setPenStartDate(paraXlsData[i][23].toString());
			stgPensioner.setPpoAgoutwardNo(paraXlsData[i][24].toString().trim().replace("'", ""));
			stgPensioner.setGpoAgoutwardNo(paraXlsData[i][25].toString().trim().replace("'", ""));
			stgPensioner.setGpoAgoutwardDt(paraXlsData[i][26].toString().trim());
			stgPensioner.setPpoAgOutwardDt(paraXlsData[i][27].toString().trim());
			stgPensioner.setGpoNo(paraXlsData[i][28].toString().trim().replace("'", ""));
			stgPensioner.setSancAuthPenCode(paraXlsData[i][29].toString().trim().replace("'", ""));
			stgPensioner.setSancAuthPen(paraXlsData[i][30].toString().trim().replace("'", ""));
			stgPensioner.setLastOff(paraXlsData[i][31].toString().trim().replace("'", ""));
			stgPensioner.setApenBankName(paraXlsData[i][32].toString().trim().replace("'", ""));
			stgPensioner.setApenBankAcno(paraXlsData[i][33].toString().trim().replace("'", ""));
			stgPensioner.setApenBranch(paraXlsData[i][34].toString().trim().replace("'", ""));
			stgPensioner.setTreasuryDataCd(paraXlsData[i][35].toString().trim());
			stgPensioner.setPayScale(paraXlsData[i][36].toString().trim().replace("'", ""));
			stgPensioner.setDesignation(paraXlsData[i][37].toString().trim().replace("'", ""));
			stgPensioner.setLastPay(paraXlsData[i][38].toString().trim().replace("'", ""));
			stgPensioner.setProvGratuity(paraXlsData[i][39].toString().trim());
			stgPensioner.setProvPension(paraXlsData[i][40].toString().trim());
			stgPensioner.setGrossPension(paraXlsData[i][41].toString().trim());
			stgPensioner.setReducedPen(paraXlsData[i][42].toString().trim());
			stgPensioner.setPenType(paraXlsData[i][43].toString().trim());
			stgPensioner.setPensionAmt(paraXlsData[i][44].toString().trim());
			stgPensioner.setEfpAmt(paraXlsData[i][45].toString().trim());
			stgPensioner.setFpAmt(paraXlsData[i][46].toString().trim());
			stgPensioner.setPnsnClass(paraXlsData[i][47].toString().trim().replace("'", ""));
			stgPensioner.setPnsnSeries(paraXlsData[i][48].toString().trim().replace("'", ""));
			if(paraXlsData[i].length == 50)
				stgPensioner.setRevisionFlag(paraXlsData[i][49].toString().trim());
			stgPensioner.setCreatedBy(paraLngPostId);
			stgPensioner.setCreatedDate(DBUtility.getCurrentDateFromDB());
			lLstTmpPensioner.add(stgPensioner);
		}
		return lLstTmpPensioner;
	}

	private List<StgPpoAlloc> setStgPpoAlloc(Object paraXlsData[][], int paraSizeOfList, Long paraLngDelvId, Long paraLngPostId) throws Exception {

		StgPpoAlloc stgPpoAlloc = null;
		List<StgPpoAlloc> lLstTmpPpoAlloc = new ArrayList<StgPpoAlloc>();
		for (int i = gIntRowCnt; i < paraSizeOfList; i++) {
			stgPpoAlloc = new StgPpoAlloc();
			stgPpoAlloc.setDelvId(paraLngDelvId);
			stgPpoAlloc.setCaseStatus("NEW");
			stgPpoAlloc.setPnsrFileNo(paraXlsData[i][0].toString());
			stgPpoAlloc.setApplnNo(paraXlsData[i][1].toString());
			stgPpoAlloc.setPnsrPpoNo(paraXlsData[i][2].toString());
			stgPpoAlloc.setAlocFor(paraXlsData[i][3].toString());
			stgPpoAlloc.setAlocAmt(paraXlsData[i][4].toString());
			stgPpoAlloc.setCreatedBy(paraLngPostId);
			stgPpoAlloc.setCreatedDate(DBUtility.getCurrentDateFromDB());
			lLstTmpPpoAlloc.add(stgPpoAlloc);
		}
		return lLstTmpPpoAlloc;
	}

	private List<StgRecovery> setStgRecovery(Object paraXlsData[][], int paraSizeOfList, Long paraLngDelvId, Long paraLngPostId) throws Exception {

		StgRecovery stgRecovery = null;
		List<StgRecovery> lLstTmpRecovery = new ArrayList<StgRecovery>();
		for (int i = gIntRowCnt; i < paraSizeOfList; i++) {
			stgRecovery = new StgRecovery();
			stgRecovery.setDelvId(paraLngDelvId);
			stgRecovery.setCaseStatus("NEW");
			stgRecovery.setPnsrFileNo(paraXlsData[i][0].toString());
			stgRecovery.setApplnNo(paraXlsData[i][1].toString());
			stgRecovery.setPnsrPpoNo(paraXlsData[i][2].toString());
			stgRecovery.setRcvyHead(paraXlsData[i][3].toString());
			stgRecovery.setRecoveryAmt(paraXlsData[i][4].toString());
			stgRecovery.setCreatedBy(paraLngPostId);
			stgRecovery.setCreatedDate(DBUtility.getCurrentDateFromDB());
			lLstTmpRecovery.add(stgRecovery);
		}
		return lLstTmpRecovery;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.ReadExcelDAO#getStgFileErrorDtls(java.lang
	 * .Long)
	 */
	public List getStgFileErrorDtls(Long lLngDelvId) throws Exception {

		List lLstResult = new ArrayList();
		Query lHibQry = null;
		StringBuffer lSBQuery = null;

		try {
			lSBQuery = new StringBuffer();
			lSBQuery.append(" select sfe.delvId,clm.lookupName,sfe.errorCode,sfe.applnNo,sfe.errorFlag from \n");
			lSBQuery.append(" StgDeliveryDtls sd, StgFileErrorDtls sfe, CmnLookupMst clm  \n");
			lSBQuery.append(" where sd.delvId = sfe.delvId \n");
			lSBQuery.append(" and sd.delvType = clm.lookupId \n");
			lSBQuery.append(" and sfe.fileId = sd.fileId \n");
			lSBQuery.append(" and sd.delvId = :delvId \n");

			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setLong("delvId", lLngDelvId);
			lLstResult = lHibQry.list();

		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error is :" + e, e);
		}
		return lLstResult;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.ReadExcelDAO#getAttachmentIdFromDelvId(java
	 * .lang.Long)
	 */
	public String getAttachmentIdFromDelvId(Long lLngDelvId) throws Exception {

		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		String lStrAttachmentId = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;

		try {
			lSBQuery = new StringBuffer();
			lSBQuery.append(" select fileAttachmentId from \n");
			lSBQuery.append(" StgDeliveryDtls  \n");
			lSBQuery.append(" where delvId = :delvId \n");
			lSBQuery.append(" group by delvId,fileAttachmentId \n");

			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setLong("delvId", lLngDelvId);
			lLstResult = lHibQry.list();

			if (!lLstResult.isEmpty() && lLstResult.get(0) != null) {
				lStrAttachmentId = lLstResult.get(0).toString();
			}

		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error is :" + e, e);
		}
		return lStrAttachmentId;
	}

	public Integer getAGRevisedPPOCount(String lStrLocCode) throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		Query lHibQry = null;
		Integer lIntCount = 0;
		StringBuffer lSBQuery = null;

		try {
			lSBQuery = new StringBuffer();
			lSBQuery.append(" select count(rivisionDtlsId) from \n");
			lSBQuery.append(" TrnPensionerRivisionDtls  \n");
			lSBQuery.append(" where revisionFlag = 'AG' and ppoAcceptDate is null and locationCode = :locationCode \n");
			
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setString("locationCode", lStrLocCode);
			lLstResult = lHibQry.list();
		
			lIntCount = Integer.parseInt(lLstResult.get(0).toString());

		} catch (Exception e) {
			logger.error("Error is :" + e, e);
		}
		return lIntCount;
	}

	public List<TrnPensionerRivisionDtls> getAGRevisedPPOList(Map displayTag,String lStrLocCode) throws Exception {
		// TODO Auto-generated method stub
		List<TrnPensionerRivisionDtls> lLstResult = new ArrayList<TrnPensionerRivisionDtls>();
		Query lHibQry = null;
		StringBuffer lSBQuery = null;

		try {
			lSBQuery = new StringBuffer();
			lSBQuery.append(" select new com.tcs.sgv.pensionpay.valueobject.TrnPensionerRivisionDtls(rivisionDtlsId,agPPONo,agPensionerName,ifmsPPONo,ifmsPensionerName,pensionerCode) from \n");
			lSBQuery.append(" TrnPensionerRivisionDtls  \n");
			lSBQuery.append(" where revisionFlag = 'AG' and ppoAcceptDate is null and locationCode = :locationCode \n");
			
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			
			Integer pageNo = (displayTag.containsKey(Constants.KEY_PAGE_NO) ? (Integer) displayTag.get(Constants.KEY_PAGE_NO) : 1);
			lHibQry.setFirstResult((pageNo.intValue() - 1) * Constants.PAGE_SIZE);
			lHibQry.setMaxResults(Constants.PAGE_SIZE);
			
			lHibQry.setString("locationCode", lStrLocCode);
			lLstResult = lHibQry.list();
		

		} catch (Exception e) {
			logger.error("Error is :" + e, e);
		}
		return lLstResult;
	}

	
	public List validatePPOBeforeMap(String lStrPpoNo, String lStrLocCode)
			throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		Query lHibQry = null;
		String lStrPpoNumber = "";
		StringBuffer lSBQuery = null;

		try {
			lSBQuery = new StringBuffer();
			lSBQuery.append(" select mph.pensionerCode, tpr.ppoNo, mph.firstName from TrnPensionRqstHdr tpr, MstPensionerHdr mph \n");
			lSBQuery.append(" where tpr.pensionerCode = mph.pensionerCode and tpr.ppoNo = :ppoNo and tpr.locationCode = :locationCode \n");
			
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setString("ppoNo", lStrPpoNo);
			lHibQry.setString("locationCode", lStrLocCode);
			lLstResult = lHibQry.list();
		
			//lStrPpoNumber = lLstResult.get(0).toString();

		} catch (Exception e) {
			logger.error("Error is :" + e, e);
		}
		return lLstResult;
	}

	
	

}
