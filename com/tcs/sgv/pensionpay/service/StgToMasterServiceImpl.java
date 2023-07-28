/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 1, 2011		Shivani Rana								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.util.DateTimeUtil;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAOImpl;
import com.tcs.sgv.pensionpay.dao.StgTablesDAO;
import com.tcs.sgv.pensionpay.dao.StgTablesDAOImpl;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerDtls;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerFamilyDtls;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerHdr;
import com.tcs.sgv.pensionpay.valueobject.StgCpo;
import com.tcs.sgv.pensionpay.valueobject.StgFamily;
import com.tcs.sgv.pensionpay.valueobject.StgFileErrorDtls;
import com.tcs.sgv.pensionpay.valueobject.StgGpoAlloc;
import com.tcs.sgv.pensionpay.valueobject.StgPensioner;
import com.tcs.sgv.pensionpay.valueobject.StgPpoAlloc;
import com.tcs.sgv.pensionpay.valueobject.StgRecovery;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionerRivisionDtls;


/**
 * Class Description -
 * 
 * 
 * @author Shivani Rana
 * @version 0.1
 * @since JDK 5.0 Apr 1, 2011
 */
public class StgToMasterServiceImpl extends ServiceImpl implements Runnable, StgToMasterService {

	private Log gLogger = LogFactory.getLog(getClass());
	private SessionFactory gSessFactory = null;
	private Long gLngDelvId = null;
	private Map inputMap = null;

	private Long gLngPostId = null;
	private Long gLngUserId = null;
	private Long gLngDBId = null;
	private String gStrLocationCode = null;
	private Date gDateDBDate = null;
	private Session ghibSession = null;
	private ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");
	
	private ResourceBundle gObjLblRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels");

	public StgToMasterServiceImpl(SessionFactory sessFactory, Long lLngDelvId, Map lInputMap) {

		gSessFactory = sessFactory;
		gLngDelvId = lLngDelvId;
		inputMap = lInputMap;
	}

	private void setSessionInfo() {

		gLngPostId = SessionHelper.getPostId(inputMap);
		gLngUserId = SessionHelper.getUserId(inputMap);
		gLngDBId = SessionHelper.getDbId(inputMap);
		gStrLocationCode = SessionHelper.getLocationCode(inputMap);
		gDateDBDate = DBUtility.getCurrentDateFromDB();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */

	public void run() {

		String[] lStrArrStgTables = {"StgCpo", "StgCpoAlloc", "StgGpoAlloc", "StgPpoAlloc", "StgFamily", "StgRecovery","StgPensioner"};
		try {
			DateTimeUtil.waitFor(30);
			stagingToMasterTransfer(lStrArrStgTables);
		} catch (Exception e) {
			gLogger.error("Error occured in run method of Pnsnpmnt Thread :" + e);
			ghibSession.clear();
		} finally {
			ghibSession.flush();
			ghibSession.close();
					
		}

	}

	private void stagingToMasterTransfer(String[] lStrArrStgTables) throws Exception {

		setSessionInfo();
		StgTablesDAO lObjStgTablesDAO = null;

		Map lMapResultRcds = null;
		Map<String, List<StgPensioner>> lMapStgPensioner = null;
		Map<String, List<StgCpo>> lMapStgCpo = null;
		// Map<String, List<StgCpoAlloc>> lMapStgCpoAlloc = null;
		Map<String, List<StgPpoAlloc>> lMapStgPpoAlloc = null;
		Map<String, List<StgFamily>> lMapStgFamily = null;
		Map<String, List<StgRecovery>> lMapStgRecovery = null;
		Map<String, List<StgGpoAlloc>> lMapStgGpoAlloc = null;
		Map<String, List<StgFileErrorDtls>> lMapStgFileErrorDtls = null;
		Map<String, List<String>> lMapDupPpoInSameLoc = null;
		Map<String, String[]> lMapRevisedPpo = new HashMap<String, String[]>();
		
		List<StgPensioner> lLstStgPensioner = null;
		List<StgCpo> lLstStgCpo = null;
		// List<StgCpoAlloc> lLstStgCpoAlloc = null;
		List<StgGpoAlloc> lLstStgGpoAlloc = null;
		List<StgPpoAlloc> lLstStgPpoAlloc = null;
		List<StgFamily> lLstStgFamily = null;
		List<StgRecovery> lLstStgRecovery = null;
		List<StgFileErrorDtls> lLstStgFileErrorDtls = null;
		List<StgFileErrorDtls> lLstStgFileWarningDtls = null;
		List<String> lLstDupPpoInSameLoc = new ArrayList<String>();
		List<String> lLstRevisedPpoInSameLoc = new ArrayList<String>();

		StgPensioner lObjStgPensioner = null;
		StgCpo lObjStgCpo = null;

		StgGpoAlloc lObjStgGpoAlloc = null;

		MstPensionerHdr lObjMstPensionerHdr = null;
		TrnPensionRqstHdr lObjTrnPensionRqstHdr = null;
		MstPensionerFamilyDtls lObjMstPensionerFamilyDtls = null;
		MstPensionerDtls lObjMstPensionerDtls = null;

		Set<String> lSetApplnNo = null;
		Iterator<String> lItrApplnNo = null;

		Long lLngMstPensionerHdrId = null;
		Long lLngMstPensionerDtlsId = null;
		String lStrApplnNo = null;
		String lPenArAddr = null; // after retirement address of pensioner
		String lPenBrAddr = null; // before retirement address of pensioner
		Date lDtPnsrDOB = null;
		Date lDtPnsrDOJ = null;
		Date lDtPnsrDOR = null;
		Date lDtPnsrDOD = null;
		Double lDbPnsrLstPay = null;
		Double lDbFp1Amt = null;
		Double lLngFp2Amt = null;

		Long lLngpensionRequestId = null;
		Date lDtPenStrt = null;
		BigDecimal lBDCvpAmt = null;
		BigDecimal lBDCommAmt = null;

		Long lLngMstPnsrFamilyDtls = null;
		Date lDtFmlyMemDob = null;
		Date lDtFp1 = null;
		Date lDtFp2 = null;
		Date lDtAGPpoOut = null;
		Calendar lObjCal = new GregorianCalendar();
		Map<String, BigDecimal> lMapHeadCodeSeries = null;
		SimpleDateFormat lObjSdf1 = new SimpleDateFormat("dd/MM/yyyy");
		Boolean isValidFamilyPnsr = true;
		try {
			DateTimeUtil.waitFor(15);
			ghibSession = gSessFactory.openSession();
			
			lMapHeadCodeSeries = new HashMap<String, BigDecimal>();
			lObjStgTablesDAO = new StgTablesDAOImpl();
			lMapResultRcds = new HashMap();
			ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");

			// ---getting map of series->headcode
			lMapHeadCodeSeries = lObjStgTablesDAO.getAllHeadCodeMap(ghibSession);
			// getting pensioner data from staging table

			lMapResultRcds = lObjStgTablesDAO.getStgTableData(ghibSession, lStrArrStgTables, gLngDelvId,gStrLocationCode);

			lMapStgPensioner = (Map) lMapResultRcds.get("MapStgPensioner");
			lMapStgCpo = (Map) lMapResultRcds.get("MapStgCpo");
			// lMapStgCpoAlloc = (Map) lMapResultRcds.get("MapStgCpoAlloc");
			lMapStgPpoAlloc = (Map) lMapResultRcds.get("MapStgPpoAlloc");
			lMapStgFamily = (Map) lMapResultRcds.get("MapStgFamily");
			lMapStgRecovery = (Map) lMapResultRcds.get("MapStgRecovery");
			lMapStgGpoAlloc = (Map) lMapResultRcds.get("MapStgGpoAlloc");
			lMapStgFileErrorDtls = (Map) lMapResultRcds.get("MapStgFileError");
			lMapDupPpoInSameLoc = (Map) lMapResultRcds.get("MapDupPpoInSameLoc");
			
			lSetApplnNo = lMapStgPensioner.keySet();
			lItrApplnNo = lSetApplnNo.iterator();
			lLstStgPensioner = new ArrayList<StgPensioner>();
			lLstStgCpo = new ArrayList<StgCpo>();
			lLstStgGpoAlloc = new ArrayList<StgGpoAlloc>();
			lLstStgRecovery = new ArrayList<StgRecovery>();
			lLstStgPpoAlloc = new ArrayList<StgPpoAlloc>();
			lLstStgFamily = new ArrayList<StgFamily>();

			lObjStgPensioner = new StgPensioner();
			lObjStgCpo = new StgCpo();
			lObjStgGpoAlloc = new StgGpoAlloc();
			lLstStgFileErrorDtls = lMapStgFileErrorDtls.get("FileErrors");
			lLstStgFileWarningDtls = lMapStgFileErrorDtls.get("FileWarnings");
			lLstDupPpoInSameLoc =  lMapDupPpoInSameLoc.get("DupPpoInSameLocList");
			
		
			
			if(lLstStgFileErrorDtls.isEmpty())
			{
			  while (lItrApplnNo.hasNext()) {
				lStrApplnNo = lItrApplnNo.next();
				isValidFamilyPnsr = true;
				lLstStgPensioner = lMapStgPensioner.get(lStrApplnNo);
				lLstStgCpo = lMapStgCpo.get(lStrApplnNo);
				lLstStgGpoAlloc = lMapStgGpoAlloc.get(lStrApplnNo);
				lLstStgFamily = lMapStgFamily.get(lStrApplnNo);
				lLstStgRecovery = lMapStgRecovery.get(lStrApplnNo);
				lLstStgPpoAlloc = lMapStgPpoAlloc.get(lStrApplnNo);
				if (lLstStgPensioner != null && lLstStgPensioner.size() > 0) {
					lObjStgPensioner = lLstStgPensioner.get(0);
					boolean isUpdateFlag = false;
					if (lObjStgPensioner.getPnsrPpoNo() != null && !lObjStgPensioner.getPnsrPpoNo().equals("")) {
						// -------Data insertion into mst_pensioner_hdr table
						// starts
						// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
						String lStrPensionerCode = "";
						PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(MstPensionerHdr.class, ghibSession);
						if(lObjStgPensioner.getRevisionFlag() != null && !"".equals(lObjStgPensioner.getRevisionFlag()) && "Y".equals(lObjStgPensioner.getRevisionFlag()))
						{
							String[] lArrStrPnsnrNameLoc = new String[6];
							lArrStrPnsnrNameLoc[0] = lObjStgPensioner.getPenName();
							lArrStrPnsnrNameLoc[1] = lObjStgPensioner.getTreasuryDataCd();
							if (lLstStgCpo != null && lLstStgCpo.size() > 0) {
								lObjStgCpo = lLstStgCpo.get(0);
								lArrStrPnsnrNameLoc[2] = lObjStgCpo.getCpoNo();
								lArrStrPnsnrNameLoc[3] = lObjStgCpo.getCvpAmt();
							}
							if (lLstStgGpoAlloc != null && lLstStgGpoAlloc.size() > 0) {
								lObjStgGpoAlloc = lLstStgGpoAlloc.get(0);
								lArrStrPnsnrNameLoc[4] = lObjStgGpoAlloc.getGpoNo();
								Double lDbDCRGAmt = 0D;
								for (StgGpoAlloc lInnObjStgGpoAlloc : lLstStgGpoAlloc) {
									if (!lInnObjStgGpoAlloc.getAlocAmt().equals("")) {
										lDbDCRGAmt += getDoubleValue(lInnObjStgGpoAlloc.getAlocAmt());
									}
								}
								lArrStrPnsnrNameLoc[5] = lDbDCRGAmt.toString();
							}
							lMapRevisedPpo.put(lObjStgPensioner.getPnsrPpoNo(), lArrStrPnsnrNameLoc);
							if(lLstDupPpoInSameLoc.contains(lObjStgPensioner.getPnsrPpoNo()))
								lLstRevisedPpoInSameLoc.add(lObjStgPensioner.getPnsrPpoNo());
							continue;
						}
						if(lLstDupPpoInSameLoc.contains(lObjStgPensioner.getPnsrPpoNo()))
						{
							continue;
							/*** Code for update pensioner details already exists with same ppo no start ***/
							//isUpdateFlag = true;
							//lStrPensionerCode = lObjStgTablesDAO.getPensionerCodeFromPpoNo(lObjStgPensioner.getPnsrPpoNo(), lObjStgPensioner.getTreasuryDataCd(),ghibSession);
							/*** Code for update pensioner details already exists with same ppo no end ***/
						}
						if(isUpdateFlag)
						{
							lObjMstPensionerHdr = lObjPhysicalCaseInwardDAO.getMstPensionerHdrVO(lStrPensionerCode);
						}
						else
						{
							lObjMstPensionerHdr = new MstPensionerHdr();
							lLngMstPensionerHdrId = IFMSCommonServiceImpl.getNextSeqNum("mst_pensioner_hdr", inputMap);
							lObjMstPensionerHdr.setPensionerId(lLngMstPensionerHdrId);
							lObjMstPensionerHdr.setPensionerCode(lLngMstPensionerHdrId.toString());
						}
						lObjMstPensionerHdr.setPnsrFileNo(lObjStgPensioner.getPnsrFileNo());
						lObjMstPensionerHdr.setApplnNO(lObjStgPensioner.getApplnNo());
						lObjMstPensionerHdr.setFirstName(lObjStgPensioner.getPenName());
						//lObjMstPensionerHdr.setLastName(lObjStgPensioner.getPenSurname());
						//lObjMstPensionerHdr.setMiddleName(lObjStgPensioner.getPenMname());
						lObjMstPensionerHdr.setPnsnrFatherName(lObjStgPensioner.getPenSurname() + " " + lObjStgPensioner.getPenMname());
						lPenArAddr = ((lObjStgPensioner.getApenArAddr1() != null) ? lObjStgPensioner.getApenArAddr1() + "," : "")
								+ ((lObjStgPensioner.getApenArAddr2() != null) ? lObjStgPensioner.getApenArAddr2() + "," : "")
								+ ((lObjStgPensioner.getApenArAddr3() != null) ? lObjStgPensioner.getApenArAddr3() + "," : "")
								+ ((lObjStgPensioner.getArCity() != null) ? lObjStgPensioner.getArCity() + "," : "")
								+ ((lObjStgPensioner.getArState() != null) ? lObjStgPensioner.getArState() + "," : "")
								+ ((lObjStgPensioner.getApenArPin() != null) ? lObjStgPensioner.getApenArPin() : "");
						lObjMstPensionerHdr.setPensionerAddr(lPenArAddr);
						lObjMstPensionerHdr.setPnsnrAddr1(lObjStgPensioner.getApenArAddr1());
						lPenArAddr = ((lObjStgPensioner.getApenArAddr2() != null) ? lObjStgPensioner.getApenArAddr2() + "," : "")
										+ ((lObjStgPensioner.getApenArAddr3() != null) ? lObjStgPensioner.getApenArAddr3() : "");
						lObjMstPensionerHdr.setPnsnrAddr2(lPenArAddr);
						
						lPenBrAddr = ((lObjStgPensioner.getApenBrAddr1() != null) ? lObjStgPensioner.getApenBrAddr1() + "," : "")
								+ ((lObjStgPensioner.getApenBrAddr2() != null) ? lObjStgPensioner.getApenBrAddr2() + "," : "")
								+ ((lObjStgPensioner.getApenBrAddr3() != null) ? lObjStgPensioner.getApenBrAddr3() + "," : "")
								+ ((lObjStgPensioner.getBrCity() != null) ? lObjStgPensioner.getBrCity() + "," : "")
								+ ((lObjStgPensioner.getBrState() != null) ? lObjStgPensioner.getBrState() + "," : "")
								+ ((lObjStgPensioner.getApenBrPin() != null) ? lObjStgPensioner.getApenBrPin() + "," : "");
						lObjMstPensionerHdr.setApenBRAddr(lPenBrAddr);

						if (lObjStgPensioner.getDtBirth() != null && lObjStgPensioner.getDtBirth().length() > 0) {
							lDtPnsrDOB = lObjSdf1.parse(lObjStgPensioner.getDtBirth());
							lObjMstPensionerHdr.setDateOfBirth(lDtPnsrDOB);
						}
						if (lObjStgPensioner.getDtAppointment() != null && lObjStgPensioner.getDtAppointment().length() > 0) {
							lDtPnsrDOJ = lObjSdf1.parse(lObjStgPensioner.getDtAppointment());
							lObjMstPensionerHdr.setDateOfJoin(lDtPnsrDOJ);
							lObjMstPensionerHdr.setDojAvailable("Y");
						}
						else
						{
							lObjMstPensionerHdr.setDojAvailable("N");
						}
						if (lObjStgPensioner.getDtRetirement() != null && lObjStgPensioner.getDtRetirement().length() > 0) {
							lDtPnsrDOR = lObjSdf1.parse(lObjStgPensioner.getDtRetirement());
							lObjMstPensionerHdr.setDateOfRetirement(lDtPnsrDOR);
						}
						
						if (lObjStgPensioner.getDtExpiry() != null && lObjStgPensioner.getDtExpiry().length() > 0) {
							lDtPnsrDOD = lObjSdf1.parse(lObjStgPensioner.getDtExpiry());
							lObjMstPensionerHdr.setDateOfDeath(lDtPnsrDOD);
							lObjMstPensionerHdr.setAliveFlag("N");
						} else {
							lObjMstPensionerHdr.setAliveFlag("Y");
						}
						lObjMstPensionerHdr.setPayScale(lObjStgPensioner.getPayScale());

						if (lObjStgPensioner.getLastPay() != null && lObjStgPensioner.getLastPay().length() > 0) {
							lDbPnsrLstPay = getDoubleValue(lObjStgPensioner.getLastPay());
							lObjMstPensionerHdr.setLastPay(BigDecimal.valueOf(lDbPnsrLstPay));
						}

						if (!"".equals(lObjStgPensioner.getPenSex())) {
							if (bundleConst.getString("AGDATA.MGENDER").equals(lObjStgPensioner.getPenSex())) {
								lObjMstPensionerHdr.setGender("M");
							} else if (bundleConst.getString("AGDATA.FMGENDER").equals(lObjStgPensioner.getPenSex())) {
								lObjMstPensionerHdr.setGender("F");
							} else {
								lObjMstPensionerHdr.setGender("T");
							}
						}
						lObjMstPensionerHdr.setEmploymentOffice(lObjStgPensioner.getLastOff());
						lObjMstPensionerHdr.setDesignation(lObjStgPensioner.getDesignation());
						lObjMstPensionerHdr.setClassType(lObjStgPensioner.getPnsnClass());// Class
						// type
						// should
						// be
						// like
						// IAS,Judges,Group
						// A

						// lObjMstPensionerHdr.setDesignation(lObjStgPensioner.getDesignation());
						// // Designation
						// code
						// required

						lObjMstPensionerHdr.setLocationCode(lObjStgPensioner.getTreasuryDataCd());
						lObjMstPensionerHdr.setCreatedDate(gDateDBDate);
						lObjMstPensionerHdr.setCreatedPostId(BigDecimal.valueOf(gLngPostId));
						lObjMstPensionerHdr.setCreatedUserId(BigDecimal.valueOf(gLngUserId));
						if(isUpdateFlag)
						{
							ghibSession.update(lObjMstPensionerHdr);
						}
						else
						{
							ghibSession.save(lObjMstPensionerHdr);
						}
						// -------Data insertion into mst_pensioner_hdr table
						// starts
						// ends>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

						// -------Data insertion into trn_pension_rqst_hdr table
						// starts
						// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
						lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnPensionRqstHdr.class, ghibSession);
						if(isUpdateFlag)
						{
							lObjTrnPensionRqstHdr = lObjPhysicalCaseInwardDAO.getTrnPensionRqstHdrVO(lStrPensionerCode);
						}
						else
						{
							lObjTrnPensionRqstHdr = new TrnPensionRqstHdr();
							lLngpensionRequestId = IFMSCommonServiceImpl.getNextSeqNum("trn_pension_rqst_hdr", inputMap);
							lObjTrnPensionRqstHdr.setPensionRequestId(lLngpensionRequestId);
							lObjTrnPensionRqstHdr.setPensionerCode(lLngMstPensionerHdrId.toString());
							
						}
						lObjTrnPensionRqstHdr.setPpoNo(lObjStgPensioner.getPnsrPpoNo());
						lObjTrnPensionRqstHdr.setPpoInwardDate(gDateDBDate);
						lObjTrnPensionRqstHdr.setInwardMode(bundleConst.getString("INWDMODE.ONLINE"));
						lObjTrnPensionRqstHdr.setHeadCode(lMapHeadCodeSeries.get(lObjStgPensioner.getPnsnSeries()));
						if(lObjStgPensioner.getPenType() != null && !"".equals(lObjStgPensioner.getPenType()))
						{
							if((gObjLblRsrcBndle.getString("PNSNTYPE.ABSORPTION")).equals(lObjStgPensioner.getPenType().replace("&", "And")))
								lObjTrnPensionRqstHdr.setPensionType(bundleConst.getString("PPMT.ABSORPTION"));
							else if((gObjLblRsrcBndle.getString("PNSNTYPE.COMPASSIONATE")).equals(lObjStgPensioner.getPenType().replace("&", "And")))
								lObjTrnPensionRqstHdr.setPensionType(bundleConst.getString("PPMT.COMPASSIONATE"));
							else if((gObjLblRsrcBndle.getString("PNSNTYPE.COMPENSATION")).equals(lObjStgPensioner.getPenType().replace("&", "And")))
								lObjTrnPensionRqstHdr.setPensionType(bundleConst.getString("PPMT.COMPENSATION"));
							else if((gObjLblRsrcBndle.getString("PNSNTYPE.COMPULSORY")).equals(lObjStgPensioner.getPenType().replace("&", "And")))
								lObjTrnPensionRqstHdr.setPensionType(bundleConst.getString("PPMT.COMPULSORY"));
							else if((gObjLblRsrcBndle.getString("PNSNTYPE.EXTRAORDINARY")).equals(lObjStgPensioner.getPenType().replace("&", "And")))
								lObjTrnPensionRqstHdr.setPensionType(bundleConst.getString("PPMT.EXTRAORDINARY"));
							else if((gObjLblRsrcBndle.getString("PNSNTYPE.FAMILYPNSN")).equals(lObjStgPensioner.getPenType().replace("&", "And")))
								lObjTrnPensionRqstHdr.setPensionType(bundleConst.getString("PPMT.FAMILYPNSN"));
							else if((gObjLblRsrcBndle.getString("PNSNTYPE.INVALID")).equals(lObjStgPensioner.getPenType().replace("&", "And")))
								lObjTrnPensionRqstHdr.setPensionType(bundleConst.getString("PPMT.INVALID"));
							else if((gObjLblRsrcBndle.getString("PNSNTYPE.RETIRING105")).equals(lObjStgPensioner.getPenType().replace("&", "And")))
								lObjTrnPensionRqstHdr.setPensionType(bundleConst.getString("PPMT.RETIRING105"));
							else if((gObjLblRsrcBndle.getString("PNSNTYPE.RETIRING104")).equals(lObjStgPensioner.getPenType().replace("&", "And")))
								lObjTrnPensionRqstHdr.setPensionType(bundleConst.getString("PPMT.RETIRING104"));
							else if((gObjLblRsrcBndle.getString("PNSNTYPE.SUPERANNU")).equals(lObjStgPensioner.getPenType().replace("&", "And")))
								lObjTrnPensionRqstHdr.setPensionType(bundleConst.getString("PPMT.SUPERANNU"));
							else if((gObjLblRsrcBndle.getString("PNSNTYPE.VOLUNTARY64")).equals(lObjStgPensioner.getPenType().replace("&", "And")))
								lObjTrnPensionRqstHdr.setPensionType(bundleConst.getString("PPMT.VOLUNTARY64"));
							else if((gObjLblRsrcBndle.getString("PNSNTYPE.VOLUNTARY65")).equals(lObjStgPensioner.getPenType().replace("&", "And")))
								lObjTrnPensionRqstHdr.setPensionType(bundleConst.getString("PPMT.VOLUNTARY65"));
							else if((gObjLblRsrcBndle.getString("PNSNTYPE.INJURY")).equals(lObjStgPensioner.getPenType().replace("&", "And")))
								lObjTrnPensionRqstHdr.setPensionType(bundleConst.getString("PPMT.INJURY"));
							else if((gObjLblRsrcBndle.getString("PNSNTYPE.GALLANTRY")).equals(lObjStgPensioner.getPenType().replace("&", "And")))
								lObjTrnPensionRqstHdr.setPensionType(bundleConst.getString("PPMT.GALLANTRY"));
							
							
							//lObjTrnPensionRqstHdr.setPensionType(lObjStgPensioner.getPenType().replace("&", "And"));
						}
						lObjTrnPensionRqstHdr.setSchemeType(bundleConst.getString("PPMT.IRLA"));
						lObjTrnPensionRqstHdr.setStatus(bundleConst.getString("STATUS.CONTINUE"));
						if (lObjStgPensioner.getPenStartDate() != null && lObjStgPensioner.getPenStartDate().length() > 0) {
							lDtPenStrt = lObjSdf1.parse(lObjStgPensioner.getPenStartDate());
							lObjTrnPensionRqstHdr.setCommensionDate(lDtPenStrt);
						}
						else if(lObjStgPensioner.getDtExpiry() != null && lObjStgPensioner.getDtExpiry().length() > 0)
						{
							lDtPenStrt = lObjSdf1.parse(lObjStgPensioner.getDtExpiry());
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
							Calendar c = Calendar.getInstance(); 
							c.setTime(sdf.parse(sdf.format(lDtPenStrt))); 
							c.add(Calendar.DATE, 1);  
							lDtPenStrt = c.getTime();
							lObjTrnPensionRqstHdr.setCommensionDate(lDtPenStrt);
						}
						if (lObjStgPensioner.getPensionAmt() != null && !lObjStgPensioner.getPensionAmt().equals("")) {
							Double lDbPnsnAmt = getDoubleValue(lObjStgPensioner.getPensionAmt());
							lObjTrnPensionRqstHdr.setBasicPensionAmount(BigDecimal.valueOf(lDbPnsnAmt));
						}

						// ---------Setting data from stg_cpo
						// table starts <<<<<<<<<<<<<<<<<
						if (lLstStgCpo != null && lLstStgCpo.size() > 0) {
							lObjStgCpo = lLstStgCpo.get(0);
							lObjTrnPensionRqstHdr.setCvpOrderNo(lObjStgCpo.getCpoNo());
							if (lObjStgCpo.getCvpAmt() != null && !lObjStgCpo.getCvpAmt().equals("")) {
								Double lDbCvpAmt = getDoubleValue(lObjStgCpo.getCvpAmt());
								lBDCvpAmt = BigDecimal.valueOf(lDbCvpAmt);
								lObjTrnPensionRqstHdr.setCvpAmount(lBDCvpAmt);
							}
							if (lObjStgCpo.getCommAmt() != null && !lObjStgCpo.getCommAmt().equals("")) {
								Double lDbCommAmt = getDoubleValue(lObjStgCpo.getCommAmt());
								lBDCommAmt = BigDecimal.valueOf(lDbCommAmt);
								lObjTrnPensionRqstHdr.setCvpMonthlyAmount(lBDCommAmt);
							}
						}
						// ---------Setting data from stg_cpo
						// table ends >>>>>>>>>>>>>>>>>>>

						// ---------Setting data from stg_gpo_alloc
						// table starts <<<<<<<<<<<<<<<<<
						if (lLstStgGpoAlloc != null && lLstStgGpoAlloc.size() > 0) {
							lObjStgGpoAlloc = lLstStgGpoAlloc.get(0);
							lObjTrnPensionRqstHdr.setDcrgOrderNo(lObjStgGpoAlloc.getGpoNo());
							Double lDbDCRGAmt = 0D;
							for (StgGpoAlloc lInnObjStgGpoAlloc : lLstStgGpoAlloc) {
								if (!lInnObjStgGpoAlloc.getAlocAmt().equals("")) {
									lDbDCRGAmt += getDoubleValue(lInnObjStgGpoAlloc.getAlocAmt());
								}
							}
							lObjTrnPensionRqstHdr.setDcrgAmount(BigDecimal.valueOf(lDbDCRGAmt));
							lObjTrnPensionRqstHdr.setTotalDcrgAmount(BigDecimal.valueOf(lDbDCRGAmt));
							lObjTrnPensionRqstHdr.setDcrgWithHeldAmnt(BigDecimal.valueOf(lDbDCRGAmt));
						}
						// ---------Setting data from stg_gpo_alloc
						// table ends <<<<<<<<<<<<<<<<<

						if (lObjStgPensioner.getEfpAmt() != null && !lObjStgPensioner.getEfpAmt().equals("")) {
							lDbFp1Amt = getDoubleValue(lObjStgPensioner.getEfpAmt());
							lObjTrnPensionRqstHdr.setFp1Amount(BigDecimal.valueOf(lDbFp1Amt));
						}
						if (lObjStgPensioner.getFpAmt() != null && !lObjStgPensioner.getFpAmt().equals("")) {
							lLngFp2Amt = getDoubleValue(lObjStgPensioner.getFpAmt());
							lObjTrnPensionRqstHdr.setFp2Amount(BigDecimal.valueOf(lLngFp2Amt));
						}

						// -------Data insertion into mst_pensioner_family_dtls
						// table
						// starts
						// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
						if(isUpdateFlag)
						{
							lObjStgTablesDAO.deletePnsnrFamilyDetails(lStrPensionerCode, ghibSession);
							
						}
						
						if (lLstStgFamily != null && lLstStgFamily.size() > 0) {
							String lStrRel = "";
							for (StgFamily lObjStgFamily : lLstStgFamily) {
								lObjMstPensionerFamilyDtls = new MstPensionerFamilyDtls();
								lLngMstPnsrFamilyDtls = IFMSCommonServiceImpl.getNextSeqNum("mst_pensioner_family_dtls", inputMap);
								lObjMstPensionerFamilyDtls.setFamilyMemberId(lLngMstPnsrFamilyDtls);
								if(isUpdateFlag)
								{
									lObjMstPensionerFamilyDtls.setPensionerCode(lStrPensionerCode);
								}
								else
								{
									lObjMstPensionerFamilyDtls.setPensionerCode(lLngMstPensionerHdrId.toString());
								}
								lObjMstPensionerFamilyDtls.setName(lObjStgFamily.getApfName());
								
								if (lObjStgFamily.getRelationship() != null && !lObjStgFamily.getRelationship().equals("")) {
									lStrRel = lObjStgFamily.getRelationship();

									// ---Relationship that is being set, is the
									// look up short names in cmn_lookup_mst
									// where lookup id is between 10000031 and
									// 10000053

									// ---Using relationship code(to be used
									// when ag starts sending relationship code)
									// --Do not delete following code.

									// if ("WIFE".equals(lStrRel)) {
									// lObjMstPensionerFamilyDtls.setRelationship("Wife");
									// } else if ("HUSBAND".equals(lStrRel)) {
									// lObjMstPensionerFamilyDtls.setRelationship("Husband");
									// } else if ("FATHER".equals(lStrRel)) {
									// lObjMstPensionerFamilyDtls.setRelationship("Father");
									// } else if ("MOTHER".equals(lStrRel)) {
									// lObjMstPensionerFamilyDtls.setRelationship("Mother");
									// }
									// // ---DAUGHTER/M -> Major Daughter
									// // ---UDAU -> Unmarried Daughters
									// else if (("DAUGHTER/M".equals(lStrRel))
									// || ("UDAU".equals(lStrRel))) {
									// lObjMstPensionerFamilyDtls.setRelationship("Daughter");
									// }
									// // -- MDAU -> Married Daughters
									// else if ("MDAU".equals(lStrRel)) {
									// lObjMstPensionerFamilyDtls.setRelationship("Daughter");
									// lObjMstPensionerFamilyDtls.setMarriedFlag("Y");
									// }
									// // MINDAUGHTE -> Minor Daughter
									// else if ("MINDAUGHTE".equals(lStrRel)) {
									// lObjMstPensionerFamilyDtls.setRelationship("Daughter");
									// lObjMstPensionerFamilyDtls.setMajorFlag("N");
									// lObjMstPensionerFamilyDtls.setGuardianName(lObjStgFamily.getApfGrdnName());
									// }
									// // WDAU -> Widowed Daughters
									// else if ("WDAU".equals(lStrRel)) {
									// lObjMstPensionerFamilyDtls.setRelationship("WDAU");
									// }
									// // --SON/M SON -> Minor Son
									// else if ("SON/M SON".equals(lStrRel)) {
									// lObjMstPensionerFamilyDtls.setRelationship("Son");
									// lObjMstPensionerFamilyDtls.setMajorFlag("N");
									// lObjMstPensionerFamilyDtls.setGuardianName(lObjStgFamily.getApfGrdnName());
									// } else if ("MAJORSON".equals(lStrRel)) {
									// lObjMstPensionerFamilyDtls.setRelationship("Son");
									// } else if ("BROTHER".equals(lStrRel)) {
									// lObjMstPensionerFamilyDtls.setRelationship("Brother");
									// }
									// // --USIS -> Unmarried Sister
									// else if ("USIS".equals(lStrRel)) {
									// lObjMstPensionerFamilyDtls.setRelationship("Sister");
									// } else if ("SISTER".equals(lStrRel)) {
									// lObjMstPensionerFamilyDtls.setRelationship("Sister");
									// lObjMstPensionerFamilyDtls.setMarriedFlag("Y");
									// }
									// // WSIS -> Widow Sister
									// else if ("WSIS".equals(lStrRel)) {
									// lObjMstPensionerFamilyDtls.setRelationship("WSIS");
									// } else if ("OTHR".equals(lStrRel)) {
									// lObjMstPensionerFamilyDtls.setRelationship("Other");
									// }
									// // DG -> Defecto Guardian
									// else if ("DG".equals(lStrRel)) {
									// lObjMstPensionerFamilyDtls.setRelationship("DG");
									// }
									// // CPDS -> Children of Predeceased Son
									// else if ("CPDS".equals(lStrRel)) {
									// lObjMstPensionerFamilyDtls.setRelationship("CPDS");
									// }

									// ---Using relationship description
									// (temporary use)
									if ("Wife".equals(lStrRel)) {
										lObjMstPensionerFamilyDtls.setRelationship("Wife");
									} else if ("Husband".equals(lStrRel)) {
										lObjMstPensionerFamilyDtls.setRelationship("Husband");
									} else if ("Father".equals(lStrRel)) {
										lObjMstPensionerFamilyDtls.setRelationship("Father");
									} else if ("Mother".equals(lStrRel)) {
										lObjMstPensionerFamilyDtls.setRelationship("Mother");
									}
									// ---DAUGHTER/M -> Major Daughter
									// ---UDAU -> Unmarried Daughters
									else if (("Major Daughter".equals(lStrRel)) || ("Unmarried Daughters".equals(lStrRel))) {
										lObjMstPensionerFamilyDtls.setRelationship("Daughter");
									}
									// -- MDAU -> Married Daughters
									else if ("Married Daughters".equals(lStrRel)) {
										lObjMstPensionerFamilyDtls.setRelationship("Daughter");
										lObjMstPensionerFamilyDtls.setMarriedFlag("Y");
									}
									// MINDAUGHTE -> Minor Daughter
									else if ("Minor Daughter".equals(lStrRel)) {
										lObjMstPensionerFamilyDtls.setRelationship("Daughter");
										lObjMstPensionerFamilyDtls.setMajorFlag("N");
										lObjMstPensionerFamilyDtls.setGuardianName(lObjStgFamily.getApfGrdnName());
									}
									// WDAU -> Widowed Daughters
									else if ("Widowed Daughters".equals(lStrRel)) {
										lObjMstPensionerFamilyDtls.setRelationship("WDAU");
									}
									// --SON/M SON -> Minor Son
									else if ("Minor Son".equals(lStrRel)) {
										lObjMstPensionerFamilyDtls.setRelationship("Son");
										lObjMstPensionerFamilyDtls.setMajorFlag("N");
										lObjMstPensionerFamilyDtls.setGuardianName(lObjStgFamily.getApfGrdnName());
									} else if ("Major Son".equals(lStrRel)) {
										lObjMstPensionerFamilyDtls.setRelationship("Son");
									} else if ("Brother".equals(lStrRel)) {
										lObjMstPensionerFamilyDtls.setRelationship("Brother");
									}
									// --USIS -> Unmarried Sister
									else if ("Unmarried Sister".equals(lStrRel)) {
										lObjMstPensionerFamilyDtls.setRelationship("Sister");
									} else if ("Married Sister".equals(lStrRel)) {
										lObjMstPensionerFamilyDtls.setRelationship("Sister");
										lObjMstPensionerFamilyDtls.setMarriedFlag("Y");
									}
									// WSIS -> Widow Sister
									else if ("Widow Sister".equals(lStrRel)) {
										lObjMstPensionerFamilyDtls.setRelationship("WSIS");
									} else if ("Other Relation".equals(lStrRel)) {
										lObjMstPensionerFamilyDtls.setRelationship("Other");
									}
									// DG -> Defecto Guardian
									else if ("Defecto Guardian".equals(lStrRel)) {
										lObjMstPensionerFamilyDtls.setRelationship("DG");
									}
									// CPDS -> Children of Predeceased Son
									else if ("Children of Predeceased Son".equals(lStrRel)) {
										lObjMstPensionerFamilyDtls.setRelationship("CPDS");
									}
								}

								if (lObjStgFamily.getDob() != null && !lObjStgFamily.getDob().equals("")) {
									lDtFmlyMemDob = lObjSdf1.parse(lObjStgFamily.getDob());
									lObjMstPensionerFamilyDtls.setDateOfBirth(lDtFmlyMemDob);
								}
								lObjMstPensionerFamilyDtls.setHandicapeFlag(lObjStgFamily.getApfHandicap());
								lObjMstPensionerFamilyDtls.setCreatedDate(gDateDBDate);
								lObjMstPensionerFamilyDtls.setCreatedPostId(gLngPostId);
								lObjMstPensionerFamilyDtls.setCreatedUserId(gLngUserId);

								// ---In AG Data EFP(FP1) date is fp1 start
								// date.(instead of fp1 end date)
								// --So getting fp1 date from fp2 date : fp1 =
								// fp2 - 1
								if (lObjStgFamily.getFpFromDate() != null && !lObjStgFamily.getFpFromDate().equals("")) {
									lDtFp2 = lObjSdf1.parse(lObjStgFamily.getFpFromDate());
									lObjCal.setTime(lDtFp2);
									lObjCal.add(Calendar.DATE, -1);
									lObjTrnPensionRqstHdr.setFp2Date(lDtFp2);
									lObjTrnPensionRqstHdr.setFp1Date(lObjCal.getTime());
								}
								if(((lObjStgFamily.getFpFromDate() != null && !lObjStgFamily.getFpFromDate().equals(""))
										|| (lObjStgFamily.getEfpFromDate() != null && !lObjStgFamily.getEfpFromDate().equals("")))
										&& (isValidFamilyPnsr))
								{
									isValidFamilyPnsr = false;
									lObjMstPensionerFamilyDtls.setPercentage(100l);
								}
								
								ghibSession.save(lObjMstPensionerFamilyDtls);
							}
						}
						// -------Data insertion into mst_pensioner_family_dtls
						// table
						// ends
						// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

						lObjTrnPensionRqstHdr.setTypeFlag("R");
						lObjTrnPensionRqstHdr.setCaseStatus(bundleConst.getString("STATFLG.NEW"));

						// ---------Setting data from stg_recovery
						// table starts <<<<<<<<<<<<<<<<<
						if (lLstStgRecovery != null && lLstStgRecovery.size() > 0) {
							Double lDbRcvryAmt = 0D;
							for (StgRecovery lObjStgRecovery : lLstStgRecovery) {
								if (!lObjStgRecovery.getRecoveryAmt().equals("")) {
									lDbRcvryAmt += getDoubleValue(lObjStgRecovery.getRecoveryAmt());
								}
							}
							lObjTrnPensionRqstHdr.setTotalRecovery(BigDecimal.valueOf(lDbRcvryAmt));
						}
						// ---------Setting data from stg_recovery
						// table ends <<<<<<<<<<<<<<<<<

						lObjTrnPensionRqstHdr.setLocationCode(lObjStgPensioner.getTreasuryDataCd());
						lObjTrnPensionRqstHdr.setDbId(BigDecimal.valueOf(gLngDBId));
						lObjTrnPensionRqstHdr.setCreatedDate(gDateDBDate);
						lObjTrnPensionRqstHdr.setCreatedPostId(BigDecimal.valueOf(gLngPostId));
						lObjTrnPensionRqstHdr.setCreatedUserId(BigDecimal.valueOf(gLngUserId));

						if (lLstStgPpoAlloc != null && lLstStgPpoAlloc.size() > 0) {
							for (StgPpoAlloc lObjStgPpoAlloc : lLstStgPpoAlloc) {
								if (!lObjStgPpoAlloc.getAlocFor().equals("")) {
									Double lDbAlocAmt = 0D;
									if (lObjStgPpoAlloc.getAlocFor().equals(bundleConst.getString("AGDATA.BOMBF1156"))) {
										if (lObjStgPpoAlloc.getAlocAmt() != null && !lObjStgPpoAlloc.getAlocAmt().equals("")) {
											lDbAlocAmt = getDoubleValue(lObjStgPpoAlloc.getAlocAmt());
											lObjTrnPensionRqstHdr.setOrgAf11136(BigDecimal.valueOf(lDbAlocAmt));
										}
									} else if (lObjStgPpoAlloc.getAlocFor().equals(bundleConst.getString("AGDATA.BOMAF1156"))) {
										if (lObjStgPpoAlloc.getAlocAmt() != null && !lObjStgPpoAlloc.getAlocAmt().equals("")) {
											lDbAlocAmt = getDoubleValue(lObjStgPpoAlloc.getAlocAmt());
											lObjTrnPensionRqstHdr.setOrgAf11156((BigDecimal.valueOf(lDbAlocAmt)));
										}
									} else if (lObjStgPpoAlloc.getAlocFor().equals(bundleConst.getString("AGDATA.MAHAAF1560"))) {
										if (lObjStgPpoAlloc.getAlocAmt() != null && !lObjStgPpoAlloc.getAlocAmt().equals("")) {
											lDbAlocAmt = getDoubleValue(lObjStgPpoAlloc.getAlocAmt());
											lObjTrnPensionRqstHdr.setOrgAf10560(((BigDecimal.valueOf(lDbAlocAmt))));
										}
									} else if (lObjStgPpoAlloc.getAlocFor().equals(bundleConst.getString("AGDATA.AFZP"))) {
										if (lObjStgPpoAlloc.getAlocAmt() != null && !lObjStgPpoAlloc.getAlocAmt().equals("")) {
											lDbAlocAmt = getDoubleValue(lObjStgPpoAlloc.getAlocAmt());
											lObjTrnPensionRqstHdr.setOrgAfZp((BigDecimal.valueOf(lDbAlocAmt)));
										}
									}
								}
							}

						}
						lObjTrnPensionRqstHdr.setPpoAGOutwardNo(lObjStgPensioner.getPpoAgoutwardNo());
						if (lObjStgPensioner.getPpoAgOutwardDt() != null && !lObjStgPensioner.getPpoAgOutwardDt().equals("")) {
							lDtAGPpoOut = lObjSdf1.parse(lObjStgPensioner.getPpoAgOutwardDt());
							lObjTrnPensionRqstHdr.setPpoAGOutwardDate(lDtAGPpoOut);
						}
						if(isUpdateFlag)
						{
							ghibSession.update(lObjTrnPensionRqstHdr);
						}
						else
						{
							ghibSession.save(lObjTrnPensionRqstHdr);
						}

						// -------Data insertion into mst_pensioner_dtls
						// table
						// starts
						// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
						lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(MstPensionerDtls.class, ghibSession);
						if(isUpdateFlag)
						{
							lObjMstPensionerDtls = lObjPhysicalCaseInwardDAO.getMstPensionerDtlsVO(lStrPensionerCode);
						}
						else
						{
							lObjMstPensionerDtls = new MstPensionerDtls();
							lLngMstPensionerDtlsId = IFMSCommonServiceImpl.getNextSeqNum("mst_pensioner_dtls", inputMap);
							lObjMstPensionerDtls.setPensionerDtlsId(lLngMstPensionerDtlsId);
							lObjMstPensionerDtls.setPensionerCode(lLngMstPensionerHdrId.toString());
						}
						lObjMstPensionerDtls.setAccountNo(lObjStgPensioner.getApenBankAcno());
						lObjMstPensionerDtls.setLocationCode(lObjStgPensioner.getTreasuryDataCd());
						//lObjMstPensionerDtls.setActiveFlag("Y");
						lObjMstPensionerDtls.setCreatedUserId(BigDecimal.valueOf(gLngUserId));
						lObjMstPensionerDtls.setCreatedPostId(BigDecimal.valueOf(gLngPostId));
						lObjMstPensionerDtls.setCreatedDate(gDateDBDate);
						lObjMstPensionerDtls.setIdentificationFlag("N");
						if(isUpdateFlag)
						{
							ghibSession.update(lObjMstPensionerDtls);
						}
						else
						{
							ghibSession.save(lObjMstPensionerDtls);
						}
					}
				}

			}
			    List lLstPnsnrNameRevisedPpo = new ArrayList();
				Map<String, String[]> lMapPnsnrNameRevisedPpo = new HashMap<String, String[]>();
				List<TrnPensionerRivisionDtls> lLstTrnPensionerRivisionDtls = new ArrayList<TrnPensionerRivisionDtls>();
				TrnPensionerRivisionDtls lObjTrnPensionerRivisionDtls = null; 
				List<String> lLstRevisedPpo = lObjStgTablesDAO.getRevisedPpoList(ghibSession);
				if(!lLstRevisedPpoInSameLoc.isEmpty())
				{
					lLstPnsnrNameRevisedPpo = lObjStgTablesDAO.getPensionerNameForRevisedPpoList(lLstRevisedPpoInSameLoc,ghibSession);
					if(!lLstPnsnrNameRevisedPpo.isEmpty())
					{
						for(int lIntCnt=0;lIntCnt<lLstPnsnrNameRevisedPpo.size();lIntCnt++)
						{
							Object[] obj=(Object[])lLstPnsnrNameRevisedPpo.get(lIntCnt);
							String lStrPpoNo = obj[0].toString();
							String[] lArrStrPnsnrNameCode = new String[2];
							lArrStrPnsnrNameCode[0] = obj[1].toString();
							lArrStrPnsnrNameCode[1] = obj[2].toString();
							
							lMapPnsnrNameRevisedPpo.put(lStrPpoNo, lArrStrPnsnrNameCode);
						}
					}
				}
				
				Set lSet = lMapRevisedPpo.keySet();
				lSet.removeAll(lLstRevisedPpo);
				Iterator lItr = lSet.iterator();
				while(lItr.hasNext())
				{
					String lStrPpoNo = (String) lItr.next();
					String[] lArrStrPnsnrNameLoc = lMapRevisedPpo.get(lStrPpoNo);
					lObjTrnPensionerRivisionDtls = new TrnPensionerRivisionDtls();
					Long lLngRevisionDtlsId = IFMSCommonServiceImpl.getNextSeqNum("trn_pensioner_rivision_dtls", inputMap);
					lObjTrnPensionerRivisionDtls.setRivisionDtlsId(lLngRevisionDtlsId);
					lObjTrnPensionerRivisionDtls.setAgPPONo(lStrPpoNo);
					lObjTrnPensionerRivisionDtls.setAgPensionerName(lArrStrPnsnrNameLoc[0]);
					lObjTrnPensionerRivisionDtls.setLocationCode(lArrStrPnsnrNameLoc[1]);
					lObjTrnPensionerRivisionDtls.setCvpOrderNo(lArrStrPnsnrNameLoc[2]);
					if(lArrStrPnsnrNameLoc[3] != null && !"".equals(lArrStrPnsnrNameLoc[3]))
					{
						Double lDbCvpAmt = getDoubleValue(lArrStrPnsnrNameLoc[3].toString());
						lBDCvpAmt = BigDecimal.valueOf(lDbCvpAmt);
						lObjTrnPensionerRivisionDtls.setCvpAmount(lBDCvpAmt);
					}
					lObjTrnPensionerRivisionDtls.setGpoOrderNo(lArrStrPnsnrNameLoc[4]);
					if(lArrStrPnsnrNameLoc[5] != null && !"".equals(lArrStrPnsnrNameLoc[5]))
					{
						Double lDbDcrgAmt = getDoubleValue(lArrStrPnsnrNameLoc[5]);
						BigDecimal lBDDcrgAmt = BigDecimal.valueOf(lDbDcrgAmt);
						lObjTrnPensionerRivisionDtls.setDcrgAmount(lBDDcrgAmt);
					}
					if(lMapPnsnrNameRevisedPpo.get(lStrPpoNo) != null)
					{
						String[] lArrStrPnsnrNameCode = lMapPnsnrNameRevisedPpo.get(lStrPpoNo);
						lObjTrnPensionerRivisionDtls.setIfmsPPONo(lStrPpoNo);
						lObjTrnPensionerRivisionDtls.setIfmsPensionerName(lArrStrPnsnrNameCode[0]);
						lObjTrnPensionerRivisionDtls.setPensionerCode(lArrStrPnsnrNameCode[1]);
					}
					lObjTrnPensionerRivisionDtls.setRevisionFlag("AG");
					lObjTrnPensionerRivisionDtls.setRivisionDate(gDateDBDate);
					lObjTrnPensionerRivisionDtls.setCreatedPostId(new BigDecimal(gLngPostId));
					lObjTrnPensionerRivisionDtls.setCreatedUserId(new BigDecimal(gLngUserId));
					lObjTrnPensionerRivisionDtls.setCreatedDate(gDateDBDate);
					ghibSession.save(lObjTrnPensionerRivisionDtls);
					//lLstTrnPensionerRivisionDtls.add(lObjTrnPensionerRivisionDtls);
				}
			}
			
			//Long lLngDelvId = null;
			Long lLngStgFileErrorDtlsId = null;
			if(!lLstStgFileWarningDtls.isEmpty())
			{
				for(StgFileErrorDtls lObjStgFileErrorDtls : lLstStgFileWarningDtls)
				{
					lLngStgFileErrorDtlsId = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("STG_FILE_ERROR_DTLS", inputMap, lLstStgFileErrorDtls.size());
					lObjStgFileErrorDtls.setStgFileErrorDtlsId(lLngStgFileErrorDtlsId);
					ghibSession.save(lObjStgFileErrorDtls);
					//lLngDelvId = lObjStgFileErrorDtls.getDelvId();
				}
				
			}
			if(!lLstStgFileErrorDtls.isEmpty())
			{
				for(StgFileErrorDtls lObjStgFileErrorDtls : lLstStgFileErrorDtls)
				{
					lLngStgFileErrorDtlsId = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("STG_FILE_ERROR_DTLS", inputMap, lLstStgFileErrorDtls.size());
					lObjStgFileErrorDtls.setStgFileErrorDtlsId(lLngStgFileErrorDtlsId);
					ghibSession.save(lObjStgFileErrorDtls);
					//lLngDelvId = lObjStgFileErrorDtls.getDelvId();
				}
				lObjStgTablesDAO.updateDelvStatusToFailed(gLngDelvId,ghibSession);
			}
			else
			{
				lObjStgTablesDAO.updateDelvStatusToSuccessful(gLngDelvId,ghibSession);
			}
			
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error occured in stagingToMasterTransfer method:" + e);
			throw e;
		}
	}

	private Long getLongValue(String lStrNum) throws Exception {

		String[] lStrArrNum = lStrNum.split(",");
		String lStrJointNum = "";
		Long lLngNum = null;
		for (int index = 0; index < lStrArrNum.length; index++) {
			lStrJointNum = lStrJointNum + lStrArrNum[index];
		}
		lLngNum = Long.valueOf(lStrJointNum);
		return lLngNum;
	}
	
	private Double getDoubleValue(String lStrNum) throws Exception {

		String[] lStrArrNum = lStrNum.split(",");
		String lStrJointNum = "";
		Double lDbNum = null;
		for (int index = 0; index < lStrArrNum.length; index++) {
			lStrJointNum = lStrJointNum + lStrArrNum[index];
		}
		lDbNum = Double.parseDouble(lStrJointNum);
		return lDbNum;
	}
}
