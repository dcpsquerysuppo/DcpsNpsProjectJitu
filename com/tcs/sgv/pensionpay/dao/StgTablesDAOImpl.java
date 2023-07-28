/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 31, 2011		Shivani Rana								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.dao;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.pensionpay.valueobject.StgCpo;
import com.tcs.sgv.pensionpay.valueobject.StgCpoAlloc;
import com.tcs.sgv.pensionpay.valueobject.StgFamily;
import com.tcs.sgv.pensionpay.valueobject.StgFileErrorDtls;
import com.tcs.sgv.pensionpay.valueobject.StgGpoAlloc;
import com.tcs.sgv.pensionpay.valueobject.StgPensioner;
import com.tcs.sgv.pensionpay.valueobject.StgPpoAlloc;
import com.tcs.sgv.pensionpay.valueobject.StgRecovery;


/**
 * Class Description -
 * 
 * 
 * @author Shivani Rana
 * @version 0.1
 * @since JDK 5.0 Mar 31, 2011
 */
public class StgTablesDAOImpl implements StgTablesDAO {

	private final Log gLogger = LogFactory.getLog(getClass());

	SessionFactory gSessFactory = null;
	
	private ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");
	
	private ResourceBundle gObjLblRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels");
	
	List<StgFileErrorDtls> gLstStgFileErrorDtls = new ArrayList<StgFileErrorDtls>();
	
	List<StgFileErrorDtls> gLstStgFileWarningDtls = new ArrayList<StgFileErrorDtls>();
	
	List<String> gLstDuplPpoInSameLoc = new ArrayList<String>();

	public StgTablesDAOImpl() {

	}

	public Map getStgTableData(Session lHibSession, String[] lStrArrTableName, Long delvId, String lStrLocCode) throws Exception {

		String[] lStrArrStgTables = {"StgPensioner", "StgCpo", "StgCpoAlloc", "StgGpoAlloc", "StgPpoAlloc", "StgFamily", "StgRecovery"};
		// List<ComboValuesVO> arrBranchVO = new ArrayList<ComboValuesVO>();
		StringBuffer strQuery = null;
		StgPensioner lObjStgPensioner = null;
		StgCpo lObjStgCpo = null;
		StgCpoAlloc lObjStgCpoAlloc = null;
		StgGpoAlloc lObjStgGpoAlloc = null;
		StgPpoAlloc lObjStgPpoAlloc = null;
		StgFamily lObjStgFamily = null;
		StgRecovery lObjStgRecovery = null;
		StgFileErrorDtls lObjStgFileErrorDtls = null;

		List<StgPensioner> lLstStgPensioner = null;
		List<StgCpo> lLstStgCpo = null;
		List<StgCpoAlloc> lLstStgCpoAlloc = null;
		List<StgGpoAlloc> lLstStgGpoAlloc = null;
		List<StgPpoAlloc> lLstStgPpoAlloc = null;
		List<StgFamily> lLstStgFamily = null;
		List<StgRecovery> lLstStgRecovery = null;
		
		List<String[]> lLstDuplPpoNo = new ArrayList<String[]>();
		List<String> lLstLocationCode = new ArrayList<String>();
		List lLstDelvTypeFileId = new ArrayList();
		Map<String, String> lMapDelvTypeFileId = new HashMap<String, String>();
		Map<String, List<StgPensioner>> lMapStgPensioner = new HashMap<String, List<StgPensioner>>();
		Map<String, List<StgCpo>> lMapStgCpo = new HashMap<String, List<StgCpo>>();
		Map<String, List<StgCpoAlloc>> lMapStgCpoAlloc = new HashMap<String, List<StgCpoAlloc>>();
		Map<String, List<StgGpoAlloc>> lMapStgGpoAlloc = new HashMap<String, List<StgGpoAlloc>>();
		Map<String, List<StgPpoAlloc>> lMapStgPpoAlloc = new HashMap<String, List<StgPpoAlloc>>();
		Map<String, List<StgFamily>> lMapStgFamily = new HashMap<String, List<StgFamily>>();
		Map<String, List<StgRecovery>> lMapStgRecovery = new HashMap<String, List<StgRecovery>>();
		Map<String, List<StgFileErrorDtls>> lMapStgFileErrorDtls = new HashMap<String, List<StgFileErrorDtls>>();
		Map<String, String[]> lMapPpoNoApplNo = new HashMap<String, String[]>();
		String[] lArrStrApplNoTranId = new String[2];
		Set<String> lSetPpoNo = new TreeSet<String>();

		Map<String, Map> lMapResultRcds = new HashMap<String, Map>();
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		List lLstStgData = new ArrayList();
		Integer lIntFlag = 0;
		String lStrApplnNo = null;
		String lStrPpoNo = null;
		String lStrLocationCode = null;
		String lStrPensionType = null;
		String lStrDateOfExpiry = null;
		String lStrBasicPensionAmt = null;
		String lStrPnsnStartDt = null;
		String lStrUploadBy = null;
		Date lDtUploadDate = null;
				
		String lStrDelvType = null;
		String lStrFileId = null;
		try {
			strQuery = new StringBuffer();
			strQuery.append("SELECT delvType,fileId,uploadBy,uploadDate FROM StgDeliveryDtls\n");
			strQuery.append("WHERE delvId =:delvId");

			Query hqlQuery = lHibSession.createQuery(strQuery.toString());

			hqlQuery.setLong("delvId", delvId);
			lLstDelvTypeFileId = hqlQuery.list();
			
			if(!lLstDelvTypeFileId.isEmpty())
			{	
				for(Integer lIntCnt=0;lIntCnt<lLstDelvTypeFileId.size();lIntCnt++)
				{
					Object[] obj=(Object[])lLstDelvTypeFileId.get(lIntCnt);
					if(obj[0] != null)
						lStrDelvType = obj[0].toString();
					if(obj[1] != null)
						lStrFileId = obj[1].toString();
					if(obj[2] != null)
						lStrUploadBy = obj[2].toString();
					if(obj[3] != null)
						lDtUploadDate = lObjSimpleDateFormat.parse(lObjSimpleDateFormat.format(obj[3]));
					lMapDelvTypeFileId.put(lStrDelvType, lStrFileId);
				}
			}
			lLstLocationCode = getLocCodeListFromAgCircle(lStrLocCode, lHibSession);
			
			int lIntArrSize = lStrArrTableName.length;
			for (int index = 0; index < lIntArrSize; index++) {
				strQuery = new StringBuffer();
				strQuery.append("FROM \n");
				strQuery.append(lStrArrTableName[index] + "\n");
				strQuery.append("WHERE delvId =:delvId");

				hqlQuery = lHibSession.createQuery(strQuery.toString());

				hqlQuery.setLong("delvId", delvId);
				lLstStgData = hqlQuery.list();

				if (lStrArrTableName[index].equals("StgPensioner")) {
					for (Object lObjStgData : lLstStgData) {
						lObjStgPensioner = new StgPensioner();
						lObjStgPensioner = (StgPensioner) lObjStgData;
						lStrApplnNo = lObjStgPensioner.getApplnNo();
						lStrPpoNo = lObjStgPensioner.getPnsrPpoNo();
						lStrLocationCode = lObjStgPensioner.getTreasuryDataCd();
						lStrPensionType = lObjStgPensioner.getPenType();
						lStrDateOfExpiry = lObjStgPensioner.getDtExpiry();
						lStrBasicPensionAmt = lObjStgPensioner.getPensionAmt();
						lStrPnsnStartDt = lObjStgPensioner.getPenStartDate();
						boolean lErrorFlag = false;
						//Warning : For Superannuation pension type, basic pension amount is mandatory
						if(lStrPensionType != null)
						{
							if(gObjLblRsrcBndle.getString("PNSNTYPE.SUPERANNU").equalsIgnoreCase(lStrPensionType) && (lStrDateOfExpiry == null || "".equals(lStrDateOfExpiry)) 
									&& (lStrBasicPensionAmt == null || "".equals(lStrBasicPensionAmt)))
							{
								lObjStgFileErrorDtls = new StgFileErrorDtls();
								lObjStgFileErrorDtls.setApplnNo(lStrApplnNo);
								lObjStgFileErrorDtls.setDelvId(delvId);
								lObjStgFileErrorDtls.setFileId(Long.parseLong(lMapDelvTypeFileId.get("140005")));
								lObjStgFileErrorDtls.setErrorCode(Long.parseLong(bundleConst.getString("WARNGCODE.BASICAMT")));
								lObjStgFileErrorDtls.setTransactionId(lObjStgPensioner.getStgPensionerId());
								lObjStgFileErrorDtls.setUploadBy(Long.parseLong(lStrUploadBy));
								lObjStgFileErrorDtls.setUploadDate(lDtUploadDate);
								lObjStgFileErrorDtls.setErrorFlag("W");
								gLstStgFileWarningDtls.add(lObjStgFileErrorDtls);
							}
						}
						//Warning : For Family Pension type, if date of expiry is not null, family pensioner is mandatory 
						if (gObjLblRsrcBndle.getString("PNSNTYPE.FAMILYPNSN").equalsIgnoreCase(lStrPensionType) && lStrDateOfExpiry != null && !"".equals(lStrDateOfExpiry)) 
						{		
							if(lMapStgFamily.containsKey(lStrApplnNo)) {
							
								lLstStgFamily = lMapStgFamily.get(lStrApplnNo);
								boolean lFamilyError = false;
								String lStrFp1Date = null;
								String lStrFp2Date = null;
								for(StgFamily lObjStgFamly : lLstStgFamily)
								{
									lStrFp1Date = lObjStgFamly.getEfpFromDate();
									lStrFp2Date = lObjStgFamly.getFpFromDate();
									if((lStrFp1Date == null || "".equals(lStrFp1Date)) && (lStrFp2Date == null || "".equals(lStrFp2Date)))
									{
										lFamilyError = true;
									}
									else
									{
										lFamilyError = false;
										break;
									}
								}
								if(lFamilyError)
								{
									lObjStgFileErrorDtls = new StgFileErrorDtls();
									lObjStgFileErrorDtls.setApplnNo(lStrApplnNo);
									lObjStgFileErrorDtls.setDelvId(delvId);
									lObjStgFileErrorDtls.setFileId(Long.parseLong(lMapDelvTypeFileId.get("140003")));
									lObjStgFileErrorDtls.setErrorCode(Long.parseLong(bundleConst.getString("WARNGCODE.FP1FP2DATE")));
									lObjStgFileErrorDtls.setTransactionId(lObjStgPensioner.getStgPensionerId());
									lObjStgFileErrorDtls.setUploadBy(Long.parseLong(lStrUploadBy));
									lObjStgFileErrorDtls.setUploadDate(lDtUploadDate);
									lObjStgFileErrorDtls.setErrorFlag("W");
									gLstStgFileWarningDtls.add(lObjStgFileErrorDtls);
								}
							}
							else
							{
								lObjStgFileErrorDtls = new StgFileErrorDtls();
								lObjStgFileErrorDtls.setApplnNo(lStrApplnNo);
								lObjStgFileErrorDtls.setDelvId(delvId);
								lObjStgFileErrorDtls.setFileId(Long.parseLong(lMapDelvTypeFileId.get("140003")));
								lObjStgFileErrorDtls.setErrorCode(Long.parseLong(bundleConst.getString("WARNGCODE.FAMILYPNSNR")));
								lObjStgFileErrorDtls.setTransactionId(lObjStgPensioner.getStgPensionerId());
								lObjStgFileErrorDtls.setUploadBy(Long.parseLong(lStrUploadBy));
								lObjStgFileErrorDtls.setUploadDate(lDtUploadDate);
								lObjStgFileErrorDtls.setErrorFlag("W");
								gLstStgFileWarningDtls.add(lObjStgFileErrorDtls);
							}
						}
						//Error : PPO Number is null
						
						if(!lMapStgPensioner.containsKey(lStrApplnNo) && (lStrPpoNo == null || "".equals(lStrPpoNo)))
						{
							lObjStgFileErrorDtls = new StgFileErrorDtls();
							lObjStgFileErrorDtls.setApplnNo(lStrApplnNo);
							lObjStgFileErrorDtls.setDelvId(delvId);
							lObjStgFileErrorDtls.setFileId(Long.parseLong(lMapDelvTypeFileId.get("140005")));
							lObjStgFileErrorDtls.setErrorCode(Long.parseLong(bundleConst.getString("ERRORCODE.NULLPPO")));
							lObjStgFileErrorDtls.setTransactionId(lObjStgPensioner.getStgPensionerId());
							lObjStgFileErrorDtls.setUploadBy(Long.parseLong(lStrUploadBy));
							lObjStgFileErrorDtls.setUploadDate(lDtUploadDate);
							lObjStgFileErrorDtls.setErrorFlag("E");
							gLstStgFileErrorDtls.add(lObjStgFileErrorDtls);
							lErrorFlag = true;
						}
						//Error : Pension Start Date and date of expiry are null 
						if((lStrPnsnStartDt == null || "".equals(lStrPnsnStartDt)) && (lStrDateOfExpiry == null || "".equals(lStrDateOfExpiry)))
						{
							lObjStgFileErrorDtls = new StgFileErrorDtls();
							lObjStgFileErrorDtls.setApplnNo(lStrApplnNo);
							lObjStgFileErrorDtls.setDelvId(delvId);
							lObjStgFileErrorDtls.setFileId(Long.parseLong(lMapDelvTypeFileId.get("140005")));
							lObjStgFileErrorDtls.setErrorCode(Long.parseLong(bundleConst.getString("ERRORCODE.PNSNSTARTDTNULL")));
							lObjStgFileErrorDtls.setTransactionId(lObjStgPensioner.getStgPensionerId());
							lObjStgFileErrorDtls.setUploadBy(Long.parseLong(lStrUploadBy));
							lObjStgFileErrorDtls.setUploadDate(lDtUploadDate);
							lObjStgFileErrorDtls.setErrorFlag("E");
							gLstStgFileErrorDtls.add(lObjStgFileErrorDtls);
							lErrorFlag = true;
						}
						//Error : Location code is null
						if(lStrLocationCode == null || "".equals(lStrLocationCode))
						{
							lObjStgFileErrorDtls = new StgFileErrorDtls();
							lObjStgFileErrorDtls.setApplnNo(lStrApplnNo);
							lObjStgFileErrorDtls.setDelvId(delvId);
							lObjStgFileErrorDtls.setFileId(Long.parseLong(lMapDelvTypeFileId.get("140005")));
							lObjStgFileErrorDtls.setErrorCode(Long.parseLong(bundleConst.getString("ERRORCODE.LOCCODENULL")));
							lObjStgFileErrorDtls.setTransactionId(lObjStgPensioner.getStgPensionerId());
							lObjStgFileErrorDtls.setUploadBy(Long.parseLong(lStrUploadBy));
							lObjStgFileErrorDtls.setUploadDate(lDtUploadDate);
							lObjStgFileErrorDtls.setErrorFlag("E");
							gLstStgFileErrorDtls.add(lObjStgFileErrorDtls);
							lErrorFlag = true;
						}
						else //Error : Location code is not pertaining in current AG circle
						{
							if(!lLstLocationCode.contains(lStrLocationCode))
							{
								lObjStgFileErrorDtls = new StgFileErrorDtls();
								lObjStgFileErrorDtls.setApplnNo(lStrApplnNo);
								lObjStgFileErrorDtls.setDelvId(delvId);
								lObjStgFileErrorDtls.setFileId(Long.parseLong(lMapDelvTypeFileId.get("140005")));
								lObjStgFileErrorDtls.setErrorCode(Long.parseLong(bundleConst.getString("ERRORCODE.LOCCODEAGCIRCLE")));
								lObjStgFileErrorDtls.setTransactionId(lObjStgPensioner.getStgPensionerId());
								lObjStgFileErrorDtls.setUploadBy(Long.parseLong(lStrUploadBy));
								lObjStgFileErrorDtls.setUploadDate(lDtUploadDate);
								lObjStgFileErrorDtls.setErrorFlag("E");
								gLstStgFileErrorDtls.add(lObjStgFileErrorDtls);
								lErrorFlag = true;
							}
						}
						if((lStrPpoNo != null && !"".equals(lStrPpoNo)))
						{
							boolean lDuplPpoFlag = false;
							boolean lFlag = false;
							if(!lSetPpoNo.contains(lStrPpoNo))
							{
								lSetPpoNo.add(lStrPpoNo);
								lArrStrApplNoTranId = new String[3];
								lArrStrApplNoTranId[0] = lStrApplnNo;
								lArrStrApplNoTranId[1] = lObjStgPensioner.getStgPensionerId().toString();
								lArrStrApplNoTranId[2] = lObjStgPensioner.getTreasuryDataCd();
								lMapPpoNoApplNo.put(lStrPpoNo, lArrStrApplNoTranId);
								lFlag = true;
							}
							else 
							{
								lArrStrApplNoTranId = new String[3];
								lArrStrApplNoTranId = lMapPpoNoApplNo.get(lStrPpoNo);
								if(lArrStrApplNoTranId[0].equals(lStrApplnNo))
									lDuplPpoFlag = true;
							}
							//Warning : Duplicate PPO Number with same application Number in file
							if(lDuplPpoFlag)
							{
								lObjStgFileErrorDtls = new StgFileErrorDtls();
								lObjStgFileErrorDtls.setApplnNo(lStrApplnNo);
								lObjStgFileErrorDtls.setDelvId(delvId);
								lObjStgFileErrorDtls.setFileId(Long.parseLong(lMapDelvTypeFileId.get("140005")));
								lObjStgFileErrorDtls.setErrorCode(Long.parseLong(bundleConst.getString("WARNGCODE.SAMEAPPLNOPPONO")));
								lObjStgFileErrorDtls.setTransactionId(lObjStgPensioner.getStgPensionerId());
								lObjStgFileErrorDtls.setUploadBy(Long.parseLong(lStrUploadBy));
								lObjStgFileErrorDtls.setUploadDate(lDtUploadDate);
								lObjStgFileErrorDtls.setErrorFlag("W");
								gLstStgFileWarningDtls.add(lObjStgFileErrorDtls);
								lErrorFlag = true;
							}
							else if(lSetPpoNo.contains(lStrPpoNo) && !lFlag)
							{
								//Error : Duplicate PPO Number in file
								lObjStgFileErrorDtls = new StgFileErrorDtls();
								lObjStgFileErrorDtls.setApplnNo(lStrApplnNo);
								lObjStgFileErrorDtls.setDelvId(delvId);
								lObjStgFileErrorDtls.setFileId(Long.parseLong(lMapDelvTypeFileId.get("140005")));
								lObjStgFileErrorDtls.setErrorCode(Long.parseLong(bundleConst.getString("ERRORCODE.DUPLPPOINFILE")));
								lObjStgFileErrorDtls.setTransactionId(lObjStgPensioner.getStgPensionerId());
								lObjStgFileErrorDtls.setUploadBy(Long.parseLong(lStrUploadBy));
								lObjStgFileErrorDtls.setUploadDate(lDtUploadDate);
								lObjStgFileErrorDtls.setErrorFlag("E");
								gLstStgFileErrorDtls.add(lObjStgFileErrorDtls);
								lErrorFlag = true;
							}
						}
						if(!lErrorFlag)
						{
							if (!lMapStgPensioner.containsKey(lStrApplnNo)) {
									lLstStgPensioner = new ArrayList<StgPensioner>();
									lLstStgPensioner.add(lObjStgPensioner);
									lMapStgPensioner.put(lStrApplnNo, lLstStgPensioner);
							}
						}
						lIntFlag = 1;
						
					}
					lMapResultRcds.put("MapStgPensioner", lMapStgPensioner);
				} else if (lStrArrTableName[index].equals("StgCpo")) {
					for (Object lObjStgData : lLstStgData) {
						lObjStgCpo = new StgCpo();
						lObjStgCpo = (StgCpo) lObjStgData;
						lStrApplnNo = lObjStgCpo.getApplnNo();
						if (lMapStgCpo.containsKey(lStrApplnNo)) {
							lLstStgCpo = lMapStgCpo.get(lStrApplnNo);
							lLstStgCpo.add(lObjStgCpo);
							lMapStgCpo.put(lStrApplnNo, lLstStgCpo);
						} else {
							lLstStgCpo = new ArrayList<StgCpo>();
							lLstStgCpo.add(lObjStgCpo);
							lMapStgCpo.put(lStrApplnNo, lLstStgCpo);
						}
					}
					lMapResultRcds.put("MapStgCpo", lMapStgCpo);
				} else if (lStrArrTableName[index].equals("StgCpoAlloc")) {
					for (Object lObjStgData : lLstStgData) {
						lObjStgCpoAlloc = new StgCpoAlloc();
						lObjStgCpoAlloc = (StgCpoAlloc) lObjStgData;
						lStrApplnNo = lObjStgCpoAlloc.getApplnNo();
						if (lMapStgCpoAlloc.containsKey(lStrApplnNo)) {
							lLstStgCpoAlloc = lMapStgCpoAlloc.get(lStrApplnNo);
							lLstStgCpoAlloc.add(lObjStgCpoAlloc);
							lMapStgCpoAlloc.put(lStrApplnNo, lLstStgCpoAlloc);
						} else {
							lLstStgCpoAlloc = new ArrayList<StgCpoAlloc>();
							lLstStgCpoAlloc.add(lObjStgCpoAlloc);
							lMapStgCpoAlloc.put(lStrApplnNo, lLstStgCpoAlloc);
						}
					}
					lMapResultRcds.put("MapStgCpoAlloc", lMapStgCpoAlloc);
				} else if (lStrArrTableName[index].equals("StgGpoAlloc")) {
					for (Object lObjStgData : lLstStgData) {
						lObjStgGpoAlloc = new StgGpoAlloc();
						lObjStgGpoAlloc = (StgGpoAlloc) lObjStgData;
						lStrApplnNo = lObjStgGpoAlloc.getApplnNo();
						if (lMapStgGpoAlloc.containsKey(lStrApplnNo)) {
							lLstStgGpoAlloc = lMapStgGpoAlloc.get(lStrApplnNo);
							lLstStgGpoAlloc.add(lObjStgGpoAlloc);
							lMapStgGpoAlloc.put(lStrApplnNo, lLstStgGpoAlloc);
						} else {
							lLstStgGpoAlloc = new ArrayList<StgGpoAlloc>();
							lLstStgGpoAlloc.add(lObjStgGpoAlloc);
							lMapStgGpoAlloc.put(lStrApplnNo, lLstStgGpoAlloc);
						}
					}
					lMapResultRcds.put("MapStgGpoAlloc", lMapStgGpoAlloc);
				} else if (lStrArrTableName[index].equals("StgPpoAlloc")) {
					for (Object lObjStgData : lLstStgData) {
						lObjStgPpoAlloc = new StgPpoAlloc();
						lObjStgPpoAlloc = (StgPpoAlloc) lObjStgData;
						lStrApplnNo = lObjStgPpoAlloc.getApplnNo();
						if (lMapStgPpoAlloc.containsKey(lStrApplnNo)) {
							lLstStgPpoAlloc = lMapStgPpoAlloc.get(lStrApplnNo);
							lLstStgPpoAlloc.add(lObjStgPpoAlloc);
							lMapStgPpoAlloc.put(lStrApplnNo, lLstStgPpoAlloc);
						} else {
							lLstStgPpoAlloc = new ArrayList<StgPpoAlloc>();
							lLstStgPpoAlloc.add(lObjStgPpoAlloc);
							lMapStgPpoAlloc.put(lStrApplnNo, lLstStgPpoAlloc);
						}
					}
					lMapResultRcds.put("MapStgPpoAlloc", lMapStgPpoAlloc);
				} else if (lStrArrTableName[index].equals("StgFamily")) {
					for (Object lObjStgData : lLstStgData) {
						lObjStgFamily = new StgFamily();
						lObjStgFamily = (StgFamily) lObjStgData;
						lStrApplnNo = lObjStgFamily.getApplnNo();
						if (lMapStgFamily.containsKey(lStrApplnNo)) {
							lLstStgFamily = lMapStgFamily.get(lStrApplnNo);
							lLstStgFamily.add(lObjStgFamily);
							lMapStgFamily.put(lStrApplnNo, lLstStgFamily);
						} else {
							lLstStgFamily = new ArrayList<StgFamily>();
							lLstStgFamily.add(lObjStgFamily);
							lMapStgFamily.put(lStrApplnNo, lLstStgFamily);
						}
					}
					lMapResultRcds.put("MapStgFamily", lMapStgFamily);
				} else if (lStrArrTableName[index].equals("StgRecovery")) {
					for (Object lObjStgData : lLstStgData) {
						lObjStgRecovery = new StgRecovery();
						lObjStgRecovery = (StgRecovery) lObjStgData;
						lStrApplnNo = lObjStgRecovery.getApplnNo();
						if (lMapStgRecovery.containsKey(lStrApplnNo)) {
							lLstStgRecovery = lMapStgRecovery.get(lStrApplnNo);
							lLstStgRecovery.add(lObjStgRecovery);
							lMapStgRecovery.put(lStrApplnNo, lLstStgRecovery);
						} else {
							lLstStgRecovery = new ArrayList<StgRecovery>();
							lLstStgRecovery.add(lObjStgRecovery);
							lMapStgRecovery.put(lStrApplnNo, lLstStgRecovery);
						}
					}
					lMapResultRcds.put("MapStgRecovery", lMapStgRecovery);
				}
			}
			List lLstPpoNo = new ArrayList(lSetPpoNo);
			Map<String,List<String>> lMapDupPpoInSameLoc = new HashMap<String,List<String>>();
			lLstDuplPpoNo = getDuplicatePpoList(lHibSession, lLstPpoNo,lLstLocationCode);
			if(!lLstDuplPpoNo.isEmpty())
			{
				//Duplicate ppo no in same location
				for(int lIntCnt=0;lIntCnt<lLstDuplPpoNo.size();lIntCnt++)
				{
					String[] lStrPpoNoLocCode = new String[2];
					lStrPpoNoLocCode = lLstDuplPpoNo.get(lIntCnt);
					String[] lStrPpoNoApplLoc = lMapPpoNoApplNo.get(lStrPpoNoLocCode[0]);
					if(lStrPpoNoApplLoc[2].equals(lStrPpoNoLocCode[1]))
					{
						gLstDuplPpoInSameLoc.add(lStrPpoNoLocCode[0]);
					}
					else //Duplicate ppo no in different location
					{
						lObjStgFileErrorDtls = new StgFileErrorDtls();
						lArrStrApplNoTranId = new String[3];
						//String[] lStrPpoNoLocCode = new String[2];
						lStrPpoNoLocCode = lLstDuplPpoNo.get(lIntCnt);
						lArrStrApplNoTranId = lMapPpoNoApplNo.get(lStrPpoNoLocCode[0]);
						lObjStgFileErrorDtls.setApplnNo(lArrStrApplNoTranId[0]);
						lObjStgFileErrorDtls.setDelvId(delvId);
						lObjStgFileErrorDtls.setFileId(Long.parseLong(lMapDelvTypeFileId.get("140005")));
						lObjStgFileErrorDtls.setErrorCode(Long.parseLong(bundleConst.getString("ERRORCODE.DUPLPPOINDB")));
						lObjStgFileErrorDtls.setTransactionId(Long.parseLong(lArrStrApplNoTranId[1]));
						lObjStgFileErrorDtls.setUploadBy(Long.parseLong(lStrUploadBy));
						lObjStgFileErrorDtls.setUploadDate(lDtUploadDate);
						gLstStgFileErrorDtls.add(lObjStgFileErrorDtls);
					}
				}
				
//				for(int lIntCnt=0;lIntCnt<lLstDuplPpoNo.size();lIntCnt++)
//				{
//					lObjStgFileErrorDtls = new StgFileErrorDtls();
//					lArrStrApplNoTranId = new String[3];
//					String[] lStrPpoNoLocCode = new String[2];
//					lStrPpoNoLocCode = lLstDuplPpoNo.get(lIntCnt);
//					lArrStrApplNoTranId = lMapPpoNoApplNo.get(lStrPpoNoLocCode[0]);
//					lObjStgFileErrorDtls.setApplnNo(lArrStrApplNoTranId[0]);
//					lObjStgFileErrorDtls.setDelvId(delvId);
//					lObjStgFileErrorDtls.setFileId(Long.parseLong(lMapDelvTypeFileId.get("140005")));
//					lObjStgFileErrorDtls.setErrorCode(Long.parseLong(bundleConst.getString("ERRORCODE.DUPLPPOINDB")));
//					lObjStgFileErrorDtls.setTransactionId(Long.parseLong(lArrStrApplNoTranId[1]));
//					lObjStgFileErrorDtls.setUploadBy(Long.parseLong(lStrUploadBy));
//					lObjStgFileErrorDtls.setUploadDate(lDtUploadDate);
//					gLstStgFileErrorDtls.add(lObjStgFileErrorDtls);
//				}
			}
			lMapDupPpoInSameLoc.put("DupPpoInSameLocList", gLstDuplPpoInSameLoc);
			lMapStgFileErrorDtls.put("FileErrors", gLstStgFileErrorDtls);
			lMapStgFileErrorDtls.put("FileWarnings", gLstStgFileWarningDtls);
			lMapResultRcds.put("MapStgFileError", lMapStgFileErrorDtls);
			lMapResultRcds.put("MapDupPpoInSameLoc", lMapDupPpoInSameLoc);
			

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;
		}
		return lMapResultRcds;
	}

	public Map<String, BigDecimal> getAllHeadCodeMap(Session lHibSession) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		List<Object[]> lLstResult = new ArrayList<Object[]>();
		Map<String, BigDecimal> lMapHeadCode = new HashMap<String, BigDecimal>();
		try {
			lSBQuery.append("Select \n");
			lSBQuery.append("series,headCode \n");
			lSBQuery.append("from \n");
			lSBQuery.append("MstPensionHeadcode  \n");

			Query hqlQuery = lHibSession.createQuery(lSBQuery.toString());
			lLstResult = hqlQuery.list();

			for (Object[] lArrObj : lLstResult) {
				lMapHeadCode.put(lArrObj[0].toString(), (BigDecimal) lArrObj[1]);
			}
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;
		}
		return lMapHeadCode;
	}
	
	public List<String[]> getDuplicatePpoList(Session lHibSession,List lLstPpoNo,List<String> lLstLocationCode) throws Exception {
		
		StringBuffer lSBQuery = new StringBuffer();
		List<Object[]> lLstResult = new ArrayList<Object[]>();
		List<String[]> lLstDuplPpoNo = new ArrayList<String[]>();
		List<List<String>> lLstPpoNoBatch = new ArrayList<List<String>>();
		int maxBatchSize = 1000;
		List<String> lLstPpo = null;
		try {
			
			if (lLstPpoNo.size() > maxBatchSize) {
				int totalBatches = lLstPpoNo.size() / maxBatchSize;
				for (int cnt = 1; cnt <= totalBatches; cnt++) {
					lLstPpo = new ArrayList<String>();
					lLstPpo.addAll((lLstPpoNo.subList(maxBatchSize * (cnt - 1), (maxBatchSize * cnt))));
					lLstPpoNoBatch.add(lLstPpo);
				}
				int remainingPpos = lLstPpoNo.size() % maxBatchSize;
				if (remainingPpos > 0) {
					lLstPpo = new ArrayList<String>();
					lLstPpo.addAll(lLstPpoNo.subList(maxBatchSize * totalBatches, (maxBatchSize * totalBatches) + remainingPpos));
					lLstPpoNoBatch.add(lLstPpo);
				}
			}
			else {
				lLstPpoNoBatch.add(lLstPpoNo);
			}
				
			
			lSBQuery.append("select \n");
			lSBQuery.append("ppoNo,locationCode \n");
			lSBQuery.append("from \n");
			lSBQuery.append("TrnPensionRqstHdr where  \n");
			lSBQuery.append("(ppoNo in (:lLstPpoNo1) \n");
			for (int i = 2; i <= lLstPpoNoBatch.size(); i++) {
				lSBQuery.append(" or ppoNo in (:lLstPpoNo" + i + ") ");
			}
			lSBQuery.append(" ) \n");
			lSBQuery.append(" and locationCode in (:lLstLocCode) \n");
			Query hqlQuery = lHibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameterList("lLstLocCode", lLstLocationCode);
			for (int i = 1; i <= lLstPpoNoBatch.size(); i++) {
				hqlQuery.setParameterList("lLstPpoNo" + i, lLstPpoNoBatch.get(i - 1));
			}
			
			lLstResult = hqlQuery.list();

			if (lLstResult != null && !lLstResult.isEmpty()) {
				Iterator itr = lLstResult.iterator();
				Object[] obj = null;
				while (itr.hasNext()) {
					obj = (Object[]) itr.next();
					String[] lStrPpoNoLocCode = new String[2];
					lStrPpoNoLocCode[0] = obj[0].toString();
					lStrPpoNoLocCode[1] = obj[1].toString();
					lLstDuplPpoNo.add(lStrPpoNoLocCode);
				}
			}
			
//			if(!lLstResult.isEmpty())
//			{
//				Iterator it = lLstResult.iterator();
//				while (it.hasNext()) {
//					String lStrPpoNo = (String) it.next();
//					lLstDuplPpoNo.add(lStrPpoNo);  
//				}
//			}
						
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;
		}
		return lLstDuplPpoNo;
	}
	
	public void updateDelvStatusToFailed(Long lLngDelvId,Session lHibSession) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append("Update StgDeliveryDtls sdd \n");
			lSBQuery.append("Set sdd.delvStatus = :lStrFailedStatus \n");
			lSBQuery.append("WHERE \n");
			lSBQuery.append("sdd.delvId=:lLngDelvID \n");

			Query lQuery = lHibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("lLngDelvID", lLngDelvId);
			lQuery.setString("lStrFailedStatus", bundleConst.getString("DELVSTATUS.FAILED"));
			lQuery.executeUpdate();

		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.dao.StgTablesDAO#updateDelvStatusToSuccessful(java.lang.Long, org.hibernate.Session)
	 */
	public void updateDelvStatusToSuccessful(Long lLngDelvId, Session lHibSession) throws Exception {

		// TODO Auto-generated method stub
		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append("Update StgDeliveryDtls sdd \n");
			lSBQuery.append("Set sdd.delvStatus = :lStrSuccessStatus \n");
			lSBQuery.append("WHERE \n");
			lSBQuery.append("sdd.delvId=:lLngDelvID \n");

			Query lQuery = lHibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("lLngDelvID", lLngDelvId);
			lQuery.setString("lStrSuccessStatus", bundleConst.getString("DELVSTATUS.SUCCESS"));
			lQuery.executeUpdate();

		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
	}
	
	public String getPensionerCodeFromPpoNo(String ppoNo, String lStrLocCd,Session lHibSession) throws Exception {

		String lStrPensionerCode = "";
		StringBuffer strQuery = new StringBuffer();
		List resultList;
		try {
			strQuery.append(" SELECT pensionerCode  FROM TrnPensionRqstHdr WHERE ppoNo = :ppoNo and locationCode = :locationCode ");

			Query hqlQuery = lHibSession.createQuery(strQuery.toString());
			hqlQuery.setString("ppoNo", ppoNo.trim());
			hqlQuery.setString("locationCode", lStrLocCd);
			resultList = hqlQuery.list();
			if (!resultList.isEmpty()) {
				lStrPensionerCode = resultList.get(0).toString();
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw e;
		}
		return lStrPensionerCode;
	}
	
	public void deletePnsnrFamilyDetails(String lStrPensionerCode,Session lHibSession) throws Exception {

		StringBuffer strQuery = new StringBuffer();
		
		try {
			strQuery.append("delete from MstPensionerFamilyDtls where pensionerCode = :pensionerCode ");

			Query hqlQuery = lHibSession.createQuery(strQuery.toString());
			hqlQuery.setString("pensionerCode", lStrPensionerCode);
			
			hqlQuery.executeUpdate();
			
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw e;
		}
		
	}

	
	public List<String> getLocCodeListFromAgCircle(String lStrAgCircleCode,Session lHibSession)
			throws Exception {
		// TODO Auto-generated method stub
		StringBuffer lSBQuery = new StringBuffer();
		List<Object[]> lLstResult = new ArrayList<Object[]>();
		List<String> lLstLocationCode = new ArrayList<String>();
		try {
			lSBQuery.append("select locationCode from CmnLocationMst where departmentId = :departmentId and officeCode = :agCircleCode");

			Query hqlQuery = lHibSession.createQuery(lSBQuery.toString());
			hqlQuery.setString("departmentId", "100003");
			hqlQuery.setString("agCircleCode", lStrAgCircleCode);
			
			lLstResult = hqlQuery.list();
			if(!lLstResult.isEmpty())
			{
				Iterator itr = lLstResult.iterator();
				Object[] obj = null;
				while (itr.hasNext()) {
					lLstLocationCode.add((String) itr.next());
				}
			}
			
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw e;
		}
		return lLstLocationCode;
	}

	
	public List getPensionerNameForRevisedPpoList(List<String> lLstPpoNo,
			Session lHibSession) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer lStrQuery = new StringBuffer();
		List lLstResult = new ArrayList();
		try {
			lStrQuery.append("select tpr.ppoNo, mph.firstName,mph.pensionerCode FROM TrnPensionRqstHdr tpr, MstPensionerHdr mph \n");
			lStrQuery.append(" WHERE tpr.pensionerCode = mph.pensionerCode and tpr.ppoNo in (:ppoNoList)");

			Query hqlQuery = lHibSession.createQuery(lStrQuery.toString());
			hqlQuery.setParameterList("ppoNoList", lLstPpoNo);
			
			lLstResult = hqlQuery.list();
			
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw e;
		}
		return lLstResult;
	}

	
	public List<String> getRevisedPpoList(Session lHibSession) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer lStrQuery = new StringBuffer();
		List lLstResult = new ArrayList();
		List<String> lLstPpoNo = new ArrayList<String>();
		try {
			lStrQuery.append("select agPPONo FROM TrnPensionerRivisionDtls \n");
			lStrQuery.append(" WHERE revisionFlag = 'AG' and ppoAcceptDate is null");

			Query hqlQuery = lHibSession.createQuery(lStrQuery.toString());
						
			lLstResult = hqlQuery.list();
			
			lLstResult = hqlQuery.list();
			if(!lLstResult.isEmpty())
			{
				Iterator itr = lLstResult.iterator();
				Object[] obj = null;
				while (itr.hasNext()) {
					lLstPpoNo.add((String) itr.next());
				}
			}
			
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw e;
		}
		return lLstPpoNo;
	}
}
