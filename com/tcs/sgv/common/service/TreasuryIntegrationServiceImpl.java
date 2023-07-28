/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Aug 21, 2012		Vrajesh Raval								
 *******************************************************************************
 */
package com.tcs.sgv.common.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialBlob;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.axis2.client.ServiceClient;
//import org.apache.axis2.client.ServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;

import bds.authorization.AuthorizationServiceStub;
import bds.payroll.MapConverter;
import bds.payroll.AuthorizationServiceStub;
import bds.payroll.PayrollBEAMSIntegrateWS;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.common.constant.Constants;
import com.tcs.sgv.common.constant.DBConstants;
import com.tcs.sgv.common.dao.CommonDAO;
import com.tcs.sgv.common.dao.CommonDAOImpl;
import com.tcs.sgv.common.dao.TreasuryIntegrationDAO;
import com.tcs.sgv.common.dao.TreasuryIntegrationDAOImpl;
import com.tcs.sgv.common.helper.ColumnVo;
import com.tcs.sgv.common.helper.ExcelExportHelper;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.common.valueobject.MstIntegrationBillTypes;
import com.tcs.sgv.common.valueobject.MstScheme;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.common.valueobject.TrnBillMvmnt;
import com.tcs.sgv.common.valueobject.TrnBillRegister;
import com.tcs.sgv.common.valueobject.TrnIfmsArthwahiniIntegErrorDtls;
import com.tcs.sgv.common.valueobject.TrnIfmsArthwahiniIntegration;
import com.tcs.sgv.common.valueobject.TrnIfmsBeamsIntegration;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.ess.valueobject.OrgPostMst;
import com.tcs.sgv.ess.valueobject.OrgUserMst;
import com.tcs.sgv.integration.service.ArthwahiniMapCoverter;
import com.tcs.sgv.pension.dao.TrnBillRegisterDAO;
import com.tcs.sgv.pension.dao.TrnBillRegisterDAOImpl;
import com.tcs.sgv.pensionpay.dao.MonthlyPensionBillDAO;
import com.tcs.sgv.pensionpay.dao.MonthlyPensionBillDAOImpl;
import com.tcs.sgv.pensionpay.dao.PensionBillDAO;
import com.tcs.sgv.pensionpay.dao.PensionBillDAOImpl;
import com.tcs.sgv.pensionpay.dao.TrnPensionBillHdrDAO;
import com.tcs.sgv.pensionpay.dao.TrnPensionBillHdrDAOImpl;
import com.tcs.sgv.pensionpay.dao.TrnPensionSupplyBillDtlsDAO;
import com.tcs.sgv.pensionpay.dao.TrnPensionSupplyBillDtlsDAOImpl;
import com.tcs.sgv.pensionpay.service.PensionBillServiceImpl;
import com.tcs.sgv.pensionpay.service.SupplementaryBillService;
import com.tcs.sgv.pensionpay.service.SupplementaryBillServiceImpl;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnMonthlyPensionRecoveryDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionSupplyBillDtls;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.tcs.sgv.pensionpay.service.MonthlyPensionBillService;
import com.tcs.sgv.pensionpay.service.MonthlyPensionBillServiceImpl;

;

/**
 * Class Description -
 * 
 * 
 * @author Vrajesh Raval
 * @version 0.1
 * @since JDK 5.0 Aug 21, 2012
 */
public class TreasuryIntegrationServiceImpl extends ServiceImpl {

	private Log gLogger = LogFactory.getLog(getClass());
	private ResourceBundle bundleConst = ResourceBundle
	.getBundle("resources/pensionpay/PensionConstants");

	// Commented by Mubeen on 18th October 2013 - Start (For Payroll)
	// private ResourceBundle integrationBundleConst =
	// ResourceBundle.getBundle("resources/common/IFMSIntegration");
	// Commented by Mubeen on 18th October 2013 - End

	// Added by Mubeen on 18th October 2013 - Start (For Pension)
	private ResourceBundle integrationBundleConst = ResourceBundle
	.getBundle("resources/common/IFMSIntegration_Pension");
	// Added by Mubeen on 18th October 2013 - End

	private static ResourceBundle pensionBundleConst = ResourceBundle
	.getBundle("resources/pensionpay/PensionConstants");
	private Long gLngPostId = null;

	private Long gLngUserId = null;

	private Long gLngLangId = null;

	private Date gDate = null;

	private String gStrLocCode = null;

	private Long gDbId = null;

	private String gStrLocShortName = null;

	public ResultObject forwardPensionBillDataToBEAMS(Map objectArgs) {

		HttpServletRequest request = (HttpServletRequest) objectArgs
		.get("requestObj");
		ServiceLocator serv = (ServiceLocator) objectArgs.get("serviceLocator");
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		HashMap lMapBillDetailsMap = new HashMap();
		TrnBillRegisterDAO lObjTrnBillRegisterDAO = null;
		TrnPensionBillHdrDAO lObjTrnPensionBillHdrDAO = null;
		String lStrBillNo = null;
		Long lLngBillNo = null;
		TrnBillRegister lObjTrnBillRegister = null;
		TreasuryIntegrationDAO lObjTreasuryIntegrationDAO = null;
		CommonDAO lObjCommonDAO = null;
		// FinancialYearDAO lObjFinancialYearDAO = null;
		Map<String, Long> lMapDeducBifurcatedMap = null;
		Map<String, Long> lMapBifurcatedPartyNameMap = null;
		MstScheme lObjMstScheme = null;
		Long lLngFinYearCode1 = null;
		SgvcFinYearMst lObjSgvcFinYearMst = null;
		Long lLngTrnIFMSBeamsIntegrationId = null;
		String lStrAuthNo = null;
		String lStrStatusCode = null;
		byte[] lArrBytePdfData = null;
		Integer lIntBeneficiaryCount = 0;
		long lSubjectId;
		List<String> lLstFinalMsg = new ArrayList();
		String lStrFinalMsg = null;
		List<TrnPensionBillHdr> lLstTrnPensionBillHdr = null;
		TrnPensionBillHdr lObjTrnPensionBillHdr = null;
		List<TrnIfmsBeamsIntegration> lLstTrnIfmsBeamsIntegration = null;
		long lTotalRecoveryAmt = 0;
		MstIntegrationBillTypes lObjMstIntegrationBillTypes = null;
		String lStrBillType = null;
		String lStrFormId = "";
		//edited by aditya
		int payeeCount=0;
		String bulkFlag="";
		String paymentMode="";
		String payMode="";
		String billYear="";
		String billMonth="";
		String CMPBeamsFlag="";
		//edited by aditya
		// Mubeen Added 05 Dec 2012
		List<BigInteger> lLstMonthlyBillsToReject = null;
		MonthlyPensionBillService lObjMonthlyPensionBillService = null;

		try {

			setSessionInfo(objectArgs);
			lStrBillNo = StringUtility.getParameter("billNo", request).trim();
			if (lStrBillNo.length() > 0) {
				lLngBillNo = Long.valueOf(lStrBillNo);
			}
			if (lLngBillNo != null) {

				lObjTrnBillRegisterDAO = new TrnBillRegisterDAOImpl(
						TrnBillRegister.class, serv.getSessionFactory());
				lObjTreasuryIntegrationDAO = new TreasuryIntegrationDAOImpl(
						TrnIfmsBeamsIntegration.class, serv.getSessionFactory());
				lObjCommonDAO = new CommonDAOImpl(serv.getSessionFactory());
				lObjTrnBillRegister = lObjTrnBillRegisterDAO.read(lLngBillNo);
				lLstTrnIfmsBeamsIntegration = lObjTreasuryIntegrationDAO
				.getListByColumnAndValue("billNo", lLngBillNo);

				// Mubeen Added 05 Dec 2012
				lObjMonthlyPensionBillService = new MonthlyPensionBillServiceImpl();

				// --Check if bill is already forwarded or not.
				if (lLstTrnIfmsBeamsIntegration.size() == 0) {
					if (DBConstants.ST_BILL_APPROVED.equals(lObjTrnBillRegister
							.getCurrBillStatus())) {
						lSubjectId = lObjTrnBillRegister.getSubjectId();
						lStrBillType = getBEAMSBillType(lObjTrnBillRegister,serv.getSessionFactory());
						lObjMstIntegrationBillTypes = lObjTreasuryIntegrationDAO
						.getBEAMSBillTypeDtls(lStrBillType, "04");
						if (lObjMstIntegrationBillTypes != null) {
							lStrFormId = lObjMstIntegrationBillTypes.getFormId();
						}
						lObjTrnPensionBillHdrDAO = new TrnPensionBillHdrDAOImpl(
								TrnPensionBillHdr.class,
								serv.getSessionFactory());
						// --If bill is of pension type or dcrg type then
						// getting
						// total
						// recovery amount from trn_pension_bill_hdr table.
						if ((lSubjectId == 9) || (lSubjectId == 10)) {
							lLstTrnPensionBillHdr = lObjTrnPensionBillHdrDAO.getListByColumnAndValue("billNo",lLngBillNo);
							if (lLstTrnPensionBillHdr != null
									&& lLstTrnPensionBillHdr.size() > 0) {
								lObjTrnPensionBillHdr = lLstTrnPensionBillHdr
								.get(0);
							}
						}
						lObjMstScheme = lObjCommonDAO
						.getHeadDtlsBySchemeCode(lObjTrnBillRegister
								.getSchemeNo());
						lIntBeneficiaryCount = lObjTreasuryIntegrationDAO.getBeneficiaryCountOfBill(lLngBillNo);
						//edited by aditya
						payeeCount= lObjTreasuryIntegrationDAO.getNoOfPensioner(lStrBillNo);
						lObjTreasuryIntegrationDAO = new TreasuryIntegrationDAOImpl(
								SgvcFinYearMst.class, serv.getSessionFactory());
						lObjSgvcFinYearMst = (SgvcFinYearMst) lObjTreasuryIntegrationDAO.read(Long.valueOf(lObjTrnBillRegister.getFinYearId()));
						lMapBillDetailsMap.put("PaybillId",lObjTrnBillRegister.getBillNo());
						if (lObjTrnPensionBillHdr != null) {
							lTotalRecoveryAmt = lObjTrnPensionBillHdr.getDeductionA().longValue();
						} else {
							lTotalRecoveryAmt = lObjTrnBillRegister.getDeductionA().longValue();
						}
						lMapBillDetailsMap.put("TotalDeduction",lTotalRecoveryAmt);
						if (lObjMstScheme != null) {
							lMapBillDetailsMap.put("DetailHead",Constants.PENSION_DTLHD_CODE);
						}
						lMapBillDetailsMap.put("SchemeCode",lObjTrnBillRegister.getSchemeNo());
						// lMapBillDetailsMap.put("SchemeCode", "20120014");
						// lMapBillDetailsMap.put("DDOCode", "1201000131");

						/**
						 * Commented by Mubeen 07th November 2013 for only one
						 * DDO - Start
						 * 
						 * // Commented by Mubeen 04 Feb 2013 for only one DDO -
						 * Start // lMapBillDetailsMap.put("DDOCode",
						 * bundleConst.getString("DDOCODE." + gStrLocCode)); //
						 * Commented by Mubeen 04 Feb 2013 for only one DDO -
						 * End
						 * 
						 * 
						 * // Added by Mubeen on 04 Feb 2013 - Start
						 * lMapBillDetailsMap.put("DDOCode", "9101005555"); //
						 * Added by Mubeen on 04 Feb 2013 - End
						 * 
						 * Commented by Mubeen 07th November 2013 for only one
						 * DDO - End
						 **/

						// Added by Mubeen 07th November 2013 for Ahmednager,
						// Kolhapur, Nagpur and Wardha Treasuries - Start
						lMapBillDetailsMap.put("DDOCode",bundleConst.getString("DDOCODE."+ gStrLocCode));

						System.out.println("PENSION_BEAMS DDO Code Value: "
								+ bundleConst.getString("DDOCODE."
										+ gStrLocCode));
						gLogger.info("PENSION_BEAMS DDO Code Value: "
								+ bundleConst.getString("DDOCODE."
										+ gStrLocCode));
						gLogger.error("PENSION_BEAMS DDO Code Value: "
								+ bundleConst.getString("DDOCODE."
										+ gStrLocCode));
						// Added by Mubeen 07th November 2013 for Ahmednager,
						// Kolhapur, Nagpur and Wardha Treasuries - End

						/**
						 * Commented by Mubeen 07th November 2013 (Testing) -
						 * Start
						 * 
						 * // 5401 AHMEDNAGAR DDO Code: 5401003270 if (
						 * (gStrLocCode.equals("5401")) ){
						 * lMapBillDetailsMap.put("DDOCode", "5401003270");
						 * System.out.println("AHMEDNAGAR"); }
						 * 
						 * // 2601 KOLHAPUR DDO Code: 2601003270 if (
						 * (gStrLocCode.equals("2601")) ){
						 * lMapBillDetailsMap.put("DDOCode", "2601003270");
						 * System.out.println("KOLHAPUR"); }
						 * 
						 * // 4601 NAGPUR DDO Code: 4601003268 if (
						 * (gStrLocCode.equals("4601")) ){
						 * lMapBillDetailsMap.put("DDOCode", "4601003268");
						 * System.out.println("NAGPUR"); }
						 * 
						 * // 4501 WARDHA DDO Code: 4501003270 if (
						 * (gStrLocCode.equals("4501")) ){
						 * lMapBillDetailsMap.put("DDOCode", "4501003270");
						 * System.out.println("WARDHA"); }
						 * 
						 * // 9991 DEMO Treasury DDO Code: 9101005555 if (
						 * (gStrLocCode.equals("9991")) ){
						 * lMapBillDetailsMap.put("DDOCode", "9101005555");
						 * System.out.println("DEMO Treasury"); }
						 * 
						 * // System.out.println(
						 * "******** 07th November 2013 ********"); //
						 * System.out.println("Location Code:" +gStrLocCode); //
						 * System.out.println("DDO Code:"
						 * +bundleConst.getString("DDOCODE." + gStrLocCode));
						 * 
						 * Commented by Mubeen 07th November 2013 (Testing) -
						 * End
						 **/

						lObjTreasuryIntegrationDAO = new TreasuryIntegrationDAOImpl(
								TrnMonthlyPensionRecoveryDtls.class,
								serv.getSessionFactory());

						// --For monthly and pension bill , getting recovery
						// from
						// monthly_pension_recovery_dtls table.For other bill
						// types
						// getting it from trn_pension_recovery_dtls table
						if ((lSubjectId == 9) || (lSubjectId == 44)) {
							lMapDeducBifurcatedMap = lObjTreasuryIntegrationDAO.getPensionBillSchemewiseRecoveryDtls(lLngBillNo,lSubjectId);
						} else {
							lMapDeducBifurcatedMap = lObjTreasuryIntegrationDAO.getDCRGBillSchemewiseRecoveryDtls(lLngBillNo);
						}

						if (lMapDeducBifurcatedMap != null) {
							/**
							 * Commented by Mubeen on 30th September 2013
							 * lMapBillDetailsMap.put("BifurcatedDeductions",
							 * lMapDeducBifurcatedMap);
							 **/

							// Added by Mubeen on 30th September 2013
							lMapBillDetailsMap.put("BifurcatedDedMapInnerMap",lMapDeducBifurcatedMap);
						}

						//added by aditya for March MOnthly Bill on 6th March 2014
						if(lSubjectId==44){
							String BillDate=lObjTrnBillRegister.getForMonth().toString();

							System.out.println("BillDate: " +BillDate);
							billYear=BillDate.substring(0,4);
							billMonth=BillDate.substring(4);
							System.out.println("billMonth: "+billMonth);
							if(billMonth.equals("03"))
							{
								lMapBillDetailsMap.put("BillCreationDate","2014-04-01");
								lMapBillDetailsMap.put("FinYear2",Calendar.getInstance().get(Calendar.YEAR)+1);
								lMapBillDetailsMap.put("FinYear1",Calendar.getInstance().get(Calendar.YEAR));
							}
							else{

								lMapBillDetailsMap.put("BillCreationDate",lObjTrnBillRegister.getBillDate());
								lMapBillDetailsMap.put("FinYear2",Long.valueOf(lObjSgvcFinYearMst.getFinYearCode()) + 1);
								lMapBillDetailsMap.put("FinYear1",lObjSgvcFinYearMst.getFinYearCode());
							}
						}
						else{

							/*lMapBillDetailsMap.put("BillCreationDate",lObjTrnBillRegister.getBillDate());
							lMapBillDetailsMap.put("FinYear2",Long.valueOf(lObjSgvcFinYearMst.getFinYearCode()) + 1);
							lMapBillDetailsMap.put("FinYear1",lObjSgvcFinYearMst.getFinYearCode());*/
							lMapBillDetailsMap.put("BillCreationDate","2014-04-01");
							lMapBillDetailsMap.put("FinYear2",Calendar.getInstance().get(Calendar.YEAR)+1);
							lMapBillDetailsMap.put("FinYear1",Calendar.getInstance().get(Calendar.YEAR));
							
						}

						//added by aditya for March MOnthly Bill on 6th March 2014

						//commented by aditya for March MOnthly Bill on 6th March 2014

						//lMapBillDetailsMap.put("BillCreationDate",lObjTrnBillRegister.getBillDate());
						lMapBillDetailsMap.put("GrossAmount",lObjTrnBillRegister.getBillGrossAmount().longValue());
						//lMapBillDetailsMap.put("FinYear2",Long.valueOf(lObjSgvcFinYearMst.getFinYearCode()) + 1);
						//lMapBillDetailsMap.put("FinYear1",lObjSgvcFinYearMst.getFinYearCode());

						//commented by aditya for March MOnthly Bill on 6th March 2014


						//commented by aditya on 13th FEB 2014

						//lMapBillDetailsMap.put("BeneficiaryCount",payeeCount);
						//lMapBillDetailsMap.put("BeneficiaryCount",lIntBeneficiaryCount.longValue());
						//lMapBillDetailsMap.put("PayeeCount",lIntBeneficiaryCount.longValue());

						//commented by aditya on 13th FEB 2014

						lMapBillDetailsMap.put("BillType", lStrBillType);
						lMapBillDetailsMap.put("FormId", lStrFormId);

						// Added by Mubeen on 30th September 2013
						// lMapBillDetailsMap.put("User", "PENSION");

						// Added by Mubeen on 4th October 2013
						lMapBillDetailsMap.put("BillPortalName", "PENSION");

						// Added by Aditya on 17th December 2013 --Start
						CMPBeamsFlag=lObjTreasuryIntegrationDAO.getCMPBeamsFlag(gStrLocCode);
						payMode=lObjTreasuryIntegrationDAO.getPayMode(lStrBillNo,gStrLocCode);
						
						if ((lSubjectId == 9 || lSubjectId == 10
								|| lSubjectId == 11 || lSubjectId == 45) && CMPBeamsFlag.equals("true")) {

							if(payMode.equalsIgnoreCase("ECS")){
							lMapBifurcatedPartyNameMap = lObjTreasuryIntegrationDAO.getPartyNameForCMP(lStrBillNo,gStrLocCode);
							if (lMapBifurcatedPartyNameMap != null) {
								lMapBillDetailsMap.put("BifurcatedPartyNameMap",lMapBifurcatedPartyNameMap);
							}
							}
						
							else{
								if (lSubjectId == 9 || lSubjectId == 10
										|| lSubjectId == 11 || lSubjectId == 45) {



									List<TrnPensionSupplyBillDtls> lLstTrnPensionSupplyBillDtls = null;
									TrnPensionSupplyBillDtls lObjTrnPensionSupplyBillDtls = null;
									TrnPensionSupplyBillDtlsDAO lObjTrnPensionSupplyBillDtlsDAO = new TrnPensionSupplyBillDtlsDAOImpl(TrnPensionSupplyBillDtls.class,serv.getSessionFactory());
									SupplementaryBillService lObjSupplementaryBillService = new SupplementaryBillServiceImpl();
									lLstTrnPensionSupplyBillDtls = lObjTrnPensionSupplyBillDtlsDAO.getListByColumnAndValue("billNo",lObjTrnBillRegister.getBillNo());
									if (lLstTrnPensionSupplyBillDtls != null && lLstTrnPensionSupplyBillDtls.size() > 0) {
										lObjTrnPensionSupplyBillDtls = lLstTrnPensionSupplyBillDtls.get(0);
										if (lSubjectId == 45 && "PENSION".equals(lObjTrnPensionSupplyBillDtls.getBillType())) {
											lMapBifurcatedPartyNameMap = lObjTreasuryIntegrationDAO.getSupplementaryPensionBillBankwiseDtls(lStrBillNo, lSubjectId);
											if (lMapBifurcatedPartyNameMap != null) {
												lMapBillDetailsMap.put("BifurcatedPartyNameMap",lMapBifurcatedPartyNameMap);
											}
										}

										else{
											MonthlyPensionBillDAO lObjPensionBillDAO = null;
											List<Object[]> lLstFirstBillDtls = null;
											lObjPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class,serv.getSessionFactory());
											lMapBifurcatedPartyNameMap = lObjTreasuryIntegrationDAO.getFirstPensionBillBankwiseDtls(lStrBillNo, lSubjectId);
											if (lMapBifurcatedPartyNameMap != null) {
												lMapBillDetailsMap.put("BifurcatedPartyNameMap",lMapBifurcatedPartyNameMap);
											}
										}
									}
									else{
										MonthlyPensionBillDAO lObjPensionBillDAO = null;
										List<Object[]> lLstFirstBillDtls = null;
										lObjPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class,serv.getSessionFactory());
										lMapBifurcatedPartyNameMap = lObjTreasuryIntegrationDAO.getFirstPensionBillBankwiseDtls(lStrBillNo, lSubjectId);
										if (lMapBifurcatedPartyNameMap != null) {
											lMapBillDetailsMap.put("BifurcatedPartyNameMap",lMapBifurcatedPartyNameMap);
										}
									}
							}
							}
						}
						else{
						if (lSubjectId == 9 || lSubjectId == 10
								|| lSubjectId == 11 || lSubjectId == 45) {



							List<TrnPensionSupplyBillDtls> lLstTrnPensionSupplyBillDtls = null;
							TrnPensionSupplyBillDtls lObjTrnPensionSupplyBillDtls = null;
							TrnPensionSupplyBillDtlsDAO lObjTrnPensionSupplyBillDtlsDAO = new TrnPensionSupplyBillDtlsDAOImpl(TrnPensionSupplyBillDtls.class,serv.getSessionFactory());
							SupplementaryBillService lObjSupplementaryBillService = new SupplementaryBillServiceImpl();
							lLstTrnPensionSupplyBillDtls = lObjTrnPensionSupplyBillDtlsDAO.getListByColumnAndValue("billNo",lObjTrnBillRegister.getBillNo());
							if (lLstTrnPensionSupplyBillDtls != null && lLstTrnPensionSupplyBillDtls.size() > 0) {
								lObjTrnPensionSupplyBillDtls = lLstTrnPensionSupplyBillDtls.get(0);
								if (lSubjectId == 45 && "PENSION".equals(lObjTrnPensionSupplyBillDtls.getBillType())) {
									lMapBifurcatedPartyNameMap = lObjTreasuryIntegrationDAO.getSupplementaryPensionBillBankwiseDtls(lStrBillNo, lSubjectId);
									if (lMapBifurcatedPartyNameMap != null) {
										lMapBillDetailsMap.put("BifurcatedPartyNameMap",lMapBifurcatedPartyNameMap);
									}
								}

								else{
									MonthlyPensionBillDAO lObjPensionBillDAO = null;
									List<Object[]> lLstFirstBillDtls = null;
									lObjPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class,serv.getSessionFactory());
									lMapBifurcatedPartyNameMap = lObjTreasuryIntegrationDAO.getFirstPensionBillBankwiseDtls(lStrBillNo, lSubjectId);
									if (lMapBifurcatedPartyNameMap != null) {
										lMapBillDetailsMap.put("BifurcatedPartyNameMap",lMapBifurcatedPartyNameMap);
									}
								}
							}
							else{
								MonthlyPensionBillDAO lObjPensionBillDAO = null;
								List<Object[]> lLstFirstBillDtls = null;
								lObjPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class,serv.getSessionFactory());
								lMapBifurcatedPartyNameMap = lObjTreasuryIntegrationDAO.getFirstPensionBillBankwiseDtls(lStrBillNo, lSubjectId);
								if (lMapBifurcatedPartyNameMap != null) {
									lMapBillDetailsMap.put("BifurcatedPartyNameMap",lMapBifurcatedPartyNameMap);
								}
							}






							/*
							 * Object partyName[]=lLstFirstBillDtls.get(0);
							 * lMapBillDetailsMap.put("BankName",partyName[0]);
							 * lMapBillDetailsMap
							 * .put("NetAmount",lObjTrnBillRegister
							 * .getBillNetAmount().longValue());
							 */
						}
						}
						
						
						
						MonthlyPensionBillDAO lObjPensionBillDAO = null;
						List<Object[]> lLstMonthlyBillOuterDtls = null;
						lObjPensionBillDAO = new MonthlyPensionBillDAOImpl(
								MstPensionerHdr.class, serv.getSessionFactory());

						
						
						//if(lSubjectId == 44 && (gStrLocCode.equals("5401") || gStrLocCode.equals("6401") || gStrLocCode.equals("6501") || gStrLocCode.equals("6301") || gStrLocCode.equals("6101") || gStrLocCode.equals("6201") || gStrLocCode.equals("9991")))
						if(lSubjectId == 44 && CMPBeamsFlag.equals("true"))
						{
							if(payMode!=null){
								if(payMode.equalsIgnoreCase("ECS"))
								{


									lMapBifurcatedPartyNameMap = lObjTreasuryIntegrationDAO.getPartyNameForCMP(lStrBillNo, gStrLocCode);
									if (lMapBifurcatedPartyNameMap != null) {
										lMapBillDetailsMap.put("BifurcatedPartyNameMap",lMapBifurcatedPartyNameMap);
									}

								}
								else{



									lMapBifurcatedPartyNameMap = lObjTreasuryIntegrationDAO.getMonthlyPensionBillBankwiseDtls(lStrBillNo, gStrLocCode);
									if (lMapBifurcatedPartyNameMap != null) {
										lMapBillDetailsMap.put("BifurcatedPartyNameMap",lMapBifurcatedPartyNameMap);
									}
								}

							}
						}	
						else if (lSubjectId == 44) {
							List<String> bankName = new ArrayList<String>();
							List<Long> netAmt = new ArrayList<Long>();

							lMapBifurcatedPartyNameMap = lObjTreasuryIntegrationDAO.getMonthlyPensionBillBankwiseDtls(lStrBillNo, gStrLocCode);
							if (lMapBifurcatedPartyNameMap != null) {
								lMapBillDetailsMap.put("BifurcatedPartyNameMap",lMapBifurcatedPartyNameMap);
							}
						}
						int noOfPayee=0;
						if(lMapBifurcatedPartyNameMap!=null){
							noOfPayee=lMapBifurcatedPartyNameMap.size();
						}

						//edited by aditya for extra parameters on 17th FEB 2014----start
						String month="";
						int curYear= Calendar.getInstance().get(Calendar.YEAR);
						int curMonth= Calendar.getInstance().get(Calendar.MONTH)+1;
						if(curMonth < 10)
							month="0" + curMonth;
						else
							month=String.valueOf(curMonth);

						if(lSubjectId==44){
							lMapBillDetailsMap.put("PayYear",billYear);
							lMapBillDetailsMap.put("PayMonth",billMonth);
						}
						else{
							lMapBillDetailsMap.put("PayYear",curYear);
							lMapBillDetailsMap.put("PayMonth",month);
						}

						if(payMode!=null)
						{
							//if(lSubjectId == 44 && (gStrLocCode.equals("5401") || gStrLocCode.equals("6401") || gStrLocCode.equals("6501") ||  gStrLocCode.equals("6301") || gStrLocCode.equals("6101") || gStrLocCode.equals("6201") || gStrLocCode.equals("9991")))
							if(CMPBeamsFlag.equals("true"))
							{

								if(payMode.equalsIgnoreCase("ECS"))
								{
									bulkFlag="Y";
									paymentMode="CMP";
									lMapBillDetailsMap.put("BeneficiaryCount",payeeCount);
									lMapBillDetailsMap.put("PayeeCount",1);
									lMapBillDetailsMap.put("BulkFlag",bulkFlag);
									lMapBillDetailsMap.put("PaymentMode",paymentMode);
								}
								else
								{
									bulkFlag="N";
									paymentMode="CHQ";
									lMapBillDetailsMap.put("BeneficiaryCount",payeeCount);
									//lMapBillDetailsMap.put("PayeeCount",lIntBeneficiaryCount.longValue());
									lMapBillDetailsMap.put("PayeeCount",noOfPayee);
									lMapBillDetailsMap.put("BulkFlag",bulkFlag);
									lMapBillDetailsMap.put("PaymentMode",paymentMode);
								}

							}
							else
							{
								bulkFlag="N";
								if(payMode.equalsIgnoreCase("ECS"))
								{
									paymentMode="ECS";
									lMapBillDetailsMap.put("PaymentMode",paymentMode);
								}
								else
								{
									paymentMode="CHQ";
									lMapBillDetailsMap.put("PaymentMode",paymentMode);
								}
								lMapBillDetailsMap.put("BeneficiaryCount",payeeCount);
								//lMapBillDetailsMap.put("PayeeCount",lIntBeneficiaryCount.longValue());
								lMapBillDetailsMap.put("PayeeCount",noOfPayee);
								lMapBillDetailsMap.put("BulkFlag",bulkFlag);
							}
						}
						//edited by aditya for extra parameters on 17th FEB 014 --End

						/*
						 * Iterator it=lLstMonthlyBillOuterDtls.iterator(); int
						 * i=1; while(it.hasNext()){ Object[] lObjArr =
						 * (Object[]) it.next();
						 * System.out.println("bank :"+lObjArr[4]);
						 * System.out.println("net amt :"+lObjArr[8]);
						 * lMapBillDetailsMap.put("BankName"+i, lObjArr[4]);
						 * lMapBillDetailsMap.put("NetAmount"+i, lObjArr[8]);
						 * i++; bankName.add(lObjArr[4].toString());
						 * netAmt.add(Long.valueOf(lObjArr[8].toString())); }
						 */
						/*
						 * lMapBillDetailsMap.put("BankName", bankName);
						 * lMapBillDetailsMap.put("NetAmount", netAmt);
						 */

						// Added by Aditya on 17th December 2013 --End

						// lMapBillDetailsMap.put("BifurcatedDeductions",
						// lMapDeducBifurcatedMap);

						// lMapBillDetailsMap.put("PaybillId", "123456789");
						// lMapBillDetailsMap.put("DetailHead", "01");
						// lMapBillDetailsMap.put("TotalDeduction", "50");
						// lMapBillDetailsMap.put("SchemeCode", "20120014");
						// lMapBillDetailsMap.put("DDOCode", "1201000131");
						// lMapBillDetailsMap.put("BillCreationDate",
						// "2012-06-28 13:15:09.0");
						// lMapBillDetailsMap.put("GrossAmount", "100");
						// lMapBillDetailsMap.put("FinYear1", "2012");
						// lMapBillDetailsMap.put("FinYear2", "2013");
						// Map lMapDeducBifurcatMap = new HashMap();
						// lMapDeducBifurcatMap.put("RC70077101", "25.0");
						// lMapDeducBifurcatMap.put("RC8658519101", "25.0");
						// lMapDeducBifurcatMap.put("RC33333333","1.0");
						// lMapBillDetailsMap.put("BifurcatedDedMapInnerMap",
						// lMapDeducBifurcatMap);

						XStream xStream = new XStream(new DomDriver("UTF-8"));
						xStream.alias("collection", java.util.Map.class);
						xStream.registerConverter(new bds.payroll.MapConverter());
						// System.out.println("Xml is :" +
						// xStream.toXML(lMapBillDetailsMap));
						gLogger.info("Xml is :"
								+ xStream.toXML(lMapBillDetailsMap));

						// Added by Mubeen on 01st November 2013
						gLogger.error("PENSION_BEAMS Xml is :"
								+ xStream.toXML(lMapBillDetailsMap));

						PayrollBEAMSIntegrateWS payrollBEAMSIntegrateWSObj = new PayrollBEAMSIntegrateWS();
						HashMap resultMap = payrollBEAMSIntegrateWSObj
						.getBillApprvFrmBEAMSWS(lMapBillDetailsMap, "");
						// HashMap resultMap =
						// getBillApprvFrmBEAMSWS(lMapBillDetailsMap, "");
						// System.out.println("BEAMS  result map is :" +
						// resultMap);

						if (resultMap != null && !resultMap.isEmpty()) {
							lStrAuthNo = resultMap.get("authNo") != null ? (String) resultMap
									.get("authNo") : null;
									// statusCode = resultMap.get("statusCode") != null
									// ?
									// (String)resultMap.get("statusCode") : "00";
									lStrStatusCode = resultMap.get("statusCode") != null ? (String) resultMap
											.get("statusCode") : null;
											lArrBytePdfData = resultMap.get("pdfData") != null ? (byte[]) resultMap
													.get("pdfData") : null;
													gLogger.info("authNo is ::: " + lStrAuthNo);
													gLogger.info("statusCode is ::: " + lStrStatusCode);
													gLogger.info("pdfData is ::: " + lArrBytePdfData);

													System.out.println("lStrStatusCode"
															+ lStrStatusCode);

													// Added by Mubeen on 01st November 2013
													gLogger.error("PENSION_BEAMS authNo is ::: "
															+ lStrAuthNo);
													gLogger.error("PENSION_BEAMS statusCode is ::: "
															+ lStrStatusCode);
													gLogger.error("PENSION_BEAMS pdfData is ::: "
															+ lArrBytePdfData);

													lObjTreasuryIntegrationDAO = new TreasuryIntegrationDAOImpl(
															TrnIfmsBeamsIntegration.class,
															serv.getSessionFactory());
													TrnIfmsBeamsIntegration lObjTrnIfmsBeamsIntegration = new TrnIfmsBeamsIntegration();
													lLngTrnIFMSBeamsIntegrationId = IFMSCommonServiceImpl
													.getNextSeqNum(
															"trn_ifms_beams_integration",
															objectArgs);
													lObjTrnIfmsBeamsIntegration
													.setIfmsBeamsIntegrationId(lLngTrnIFMSBeamsIntegrationId);
													lObjTrnIfmsBeamsIntegration
													.setBillNo(lObjTrnBillRegister.getBillNo());
													lObjTrnIfmsBeamsIntegration
													.setPaybillId(lObjTrnBillRegister
															.getBillNo());
													lObjTrnIfmsBeamsIntegration
													.setBillType(Constants.INTEGRATION_PENSION_BILL_TYPE);
													lObjTrnIfmsBeamsIntegration
													.setBillGrossAmt(lObjTrnBillRegister
															.getBillGrossAmount());
													lObjTrnIfmsBeamsIntegration
													.setTotalRecoveryAmt(new BigDecimal(
															lTotalRecoveryAmt));
													lObjTrnIfmsBeamsIntegration
													.setBillNetAmt(lObjTrnBillRegister
															.getBillNetAmount());
													lObjTrnIfmsBeamsIntegration
													.setSchemeCode(lObjTrnBillRegister
															.getSchemeNo());
													lObjTrnIfmsBeamsIntegration
													.setDtlheadCode(Constants.PENSION_DTLHD_CODE);
													lObjTrnIfmsBeamsIntegration
													.setBillCreationDate(lObjTrnBillRegister
															.getBillDate());
													lObjTrnIfmsBeamsIntegration
													.setFinYear1(lMapBillDetailsMap.get(
													"FinYear1").toString());
													lObjTrnIfmsBeamsIntegration
													.setFinYear2(lMapBillDetailsMap.get(
													"FinYear2").toString());
													lObjTrnIfmsBeamsIntegration
													.setYearMonth(lObjTrnBillRegister
															.getForMonth());
													lObjTrnIfmsBeamsIntegration
													.setNoOfBeneficiary(lIntBeneficiaryCount);
													lObjTrnIfmsBeamsIntegration.setDdoCode(bundleConst
															.getString("DDOCODE." + gStrLocCode));
													lObjTrnIfmsBeamsIntegration.setAuthNo(lStrAuthNo);
													lObjTrnIfmsBeamsIntegration
													.setStatusCode(lStrStatusCode);

													// Added by Mubeen on 10th October 2013 - Start
													lObjTrnIfmsBeamsIntegration
													.setBeamsBillStatus(null);
													// Added by Mubeen on 10th October 2013 - End

													if (lArrBytePdfData != null) {
														lObjTrnIfmsBeamsIntegration
														.setAuthSlip(new SerialBlob(
																lArrBytePdfData));
													}
													if (lStrStatusCode != null
															&& lStrStatusCode.length() > 0) {
														if ("00".equals(lStrStatusCode)) {
															lObjTrnIfmsBeamsIntegration
															.setBillValidSatus("Y");

															// Added by Mubeen on 10th October 2013 -
															// Start
															lObjTrnIfmsBeamsIntegration
															.setBeamsBillStatus("1");
															// Added by Mubeen on 10th October 2013 -
															// End

														} else {
															lObjTrnIfmsBeamsIntegration
															.setBillValidSatus("N");
														}
													} else {
														lObjTrnIfmsBeamsIntegration
														.setBillValidSatus("N");
													}

													// Commented by Mubeen on 10th October 2013
													// lObjTrnIfmsBeamsIntegration.setBeamsBillStatus(null);

													lObjTrnIfmsBeamsIntegration
													.setLocationCode(gStrLocCode);
													lObjTrnIfmsBeamsIntegration.setDbId(gDbId
															.intValue());
													lObjTrnIfmsBeamsIntegration.setCreatedDate(gDate);
													lObjTrnIfmsBeamsIntegration
													.setCreatedUserId(gLngUserId);
													lObjTrnIfmsBeamsIntegration
													.setCreatedPostId(gLngPostId);
													lObjTrnIfmsBeamsIntegration
													.setBeamsBillType(lStrBillType);
													lObjTreasuryIntegrationDAO
													.create(lObjTrnIfmsBeamsIntegration);
						}

						if (lStrStatusCode != null
								&& lStrStatusCode.length() > 0) {
							if ("00".equals(lStrStatusCode)) {
								lLstFinalMsg
								.add("Bill is authorized by BEAMS successfully.");
							} else {

								// Mubeen 05 Dec 2012
								// Add Code to Reject all

								System.out.println("Mubeen: Using lSubjectId: "
										+ lSubjectId);
								// System.out.println("Mubeen: Using lObjTrnBillRegister.getSubjectId: "+lObjTrnBillRegister.getSubjectId());

								if (44 == lSubjectId) {
									System.out
									.println("Mubeen1: Getting List of All monthly Bills to reject");

									Integer lIntForMonth = lObjTrnBillRegister
									.getForMonth();
									System.out.println("Mubeen2: "
											+ lIntForMonth);
									String lStrLocationCode = lObjTrnBillRegister
									.getLocationCode();
									System.out.println("Mubeen3: "
											+ lStrLocationCode);

									lLstMonthlyBillsToReject = lObjTreasuryIntegrationDAO
									.getListOfBillsToReject(
											lIntForMonth,
											lStrLocationCode);
									System.out
									.println("Mubeen4: List of Bills to Reject "
											+ lLstMonthlyBillsToReject);

									for (BigInteger lBigIntBillNo : lLstMonthlyBillsToReject) {
										System.out.println("Bill: "
												+ lBigIntBillNo);
										System.out.println("Bill: "
												+ lBigIntBillNo.intValue());
									}

									objectArgs.put("BillList",
											lLstMonthlyBillsToReject);

									lObjMonthlyPensionBillService
									.rejectAllMonthlyBillForBEAMS(objectArgs);

								}
								// Mubeen 05 Dec 2012 - End

								lLstFinalMsg
								.add("Bill is rejected by BEAMS.Reason of rejection,");
								String[] stCode = lStrStatusCode.split("\\|");
								for (int cnt = 0; cnt < stCode.length; cnt++) {
									String key = "Status" + stCode[cnt];
									String stMsg = String.valueOf(cnt + 1)
									+ ") "
									+ integrationBundleConst
									.getString(key);
									lLstFinalMsg.add(stMsg);
								}
								objectArgs.put("finalMsg", lLstFinalMsg);

								// --Seting bill status to rejected
								rejectPensionBillByBEAMS(objectArgs, lLngBillNo);
								// lObjTrnBillRegister.setCurrBillStatus(DBConstants.ST_BILL_REJECTED);
								// lObjTrnBillRegister.setUpdatedDate(gDate);
								// lObjTrnBillRegister.setUpdatedPostId(gLngPostId);
								// lObjTrnBillRegister.setUpdatedUserId(gLngUserId);
								// lObjTrnBillRegisterDAO.update(lObjTrnBillRegister);
							}
						}
					} else {
						lLstFinalMsg
						.add("Only approved bill can be forwarded to BEAMS.");
					}
				} else {
					lLstFinalMsg.add("Bill is already forwarded to BEAMS.");
				}
			} else {
				lLstFinalMsg
				.add("Some problem occurred during transaction.Please try again later.");
			}
			StringBuilder lSBStatus = new StringBuilder();
			lSBStatus.append("<XMLDOC>");
			lSBStatus.append("<STATUSMSG>");
			for (String lStrMsg : lLstFinalMsg) {
				lSBStatus.append(lStrMsg);
				lSBStatus.append("\n");
			}
			lSBStatus.append("</STATUSMSG>");
			lSBStatus.append("</XMLDOC>");
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
					lSBStatus.toString()).toString();
			objectArgs.put("ajaxKey", lStrResult);
			resultObject.setResultValue(objectArgs);
			resultObject.setViewName("ajaxData");
		} catch (Exception e) {
			gLogger.error("error is :" + e);
		}
		return resultObject;
	}

	public HashMap getBillApprvFrmBEAMSWS(HashMap billData, String BEAMSWSURL) {

		HashMap billAprvDtlMap = new HashMap();
		XStream xStream = new XStream(new DomDriver("UTF-8"));
		// xStream.alias("collection", java / util / Map);
		xStream.alias("collection", Map.class);
		xStream.registerConverter(new MapConverter());
		String mapToXML = xStream.toXML(billData);
		Pattern pattern = Pattern.compile(">\\s+");
		Matcher matcher = pattern.matcher(mapToXML);
		mapToXML = matcher.replaceAll(">");
		StringBuilder lSBMapToXML = new StringBuilder();
		// lSBMapToXML.append("<collection>");
		// lSBMapToXML.append("<PaybillId>99220146</PaybillId>");
		// lSBMapToXML.append("<TotalDeduction>2000</TotalDeduction>");
		// lSBMapToXML.append("<DetailHead>01</DetailHead>");
		// // lSBMapToXML.append("<SchemeCode>20710103</SchemeCode>");
		// lSBMapToXML.append("<SchemeCode>20150012</SchemeCode>");
		// lSBMapToXML.append("<DDOCode>1201000131</DDOCode>");
		// lSBMapToXML.append("<BifurcatedDeductions>");
		// // lSBMapToXML.append("<RC0020001200>500</RC0020001200>");
		// // lSBMapToXML.append("<RC0071008401>1500</RC0071008401>");
		// lSBMapToXML.append("<RC8658530601>300</RC8658530601>");
		// lSBMapToXML.append("<RC8011502300>0</RC8011502300>");
		// lSBMapToXML.append("</BifurcatedDeductions>");
		// lSBMapToXML.append("<BillCreationDate>2012-08-16 15:27:43.0</BillCreationDate>");
		// lSBMapToXML.append("<GrossAmount>3115</GrossAmount>");
		// lSBMapToXML.append("<FinYear2>2013</FinYear2>");
		// lSBMapToXML.append("<FinYear1>2012</FinYear1>");
		// lSBMapToXML.append("</collection>");

		// lSBMapToXML.append("<collection>");
		// lSBMapToXML.append("<PaybillId>9920000321</PaybillId>");
		// lSBMapToXML.append("<TotalDeduction>33179</TotalDeduction>");
		// lSBMapToXML.append("<DetailHead>01</DetailHead>");
		// lSBMapToXML.append("<SchemeCode>20710103</SchemeCode>");
		// lSBMapToXML.append("<DDOCode>7101000036</DDOCode>");
		// lSBMapToXML.append("<BifurcatedDeductions>");
		// lSBMapToXML.append("<RC8658530601>300</RC8658530601>");
		// lSBMapToXML.append("<RC8011502300>0</RC8011502300>");
		// lSBMapToXML.append("</BifurcatedDeductions>");
		// lSBMapToXML.append("<BillCreationDate>2012-08-16 15:27:43.0</BillCreationDate>");
		// lSBMapToXML.append("<GrossAmount>271589</GrossAmount>");
		// lSBMapToXML.append("<FinYear2>2013</FinYear2>");
		// lSBMapToXML.append("<FinYear1>2012</FinYear1>");
		// lSBMapToXML.append("</collection>");
		String dataXML = (new StringBuilder(
		"<?xml version='1.0' encoding='UTF-8' ?>")).append(mapToXML)
		.toString();
		// String dataXML = (new
		// StringBuilder("<?xml version='1.0' encoding='UTF-8' ?>")).append(lSBMapToXML.toString()).toString();
		try {
			AuthorizationServiceStub serviceStub = new	 AuthorizationServiceStub("http://103.23.150.106/PayRollIntegration/services/PayRollService");
			//AuthorizationServiceStub serviceStub = new AuthorizationServiceStub();
			AuthorizationServiceStub.GetAuthSlip authSlip = new AuthorizationServiceStub.GetAuthSlip();
			AuthorizationServiceStub.GetAuthSlipResponse authSlipResponse = new AuthorizationServiceStub.GetAuthSlipResponse();
			org.apache.axis2.transport.http.HttpTransportProperties.ProxyProperties proxyProperties = new org.apache.axis2.transport.http.HttpTransportProperties.ProxyProperties();
			proxyProperties.setProxyName("proxy.tcs.com");
			proxyProperties.setProxyPort(8080);
			proxyProperties.setUserName("365450");
			proxyProperties.setPassWord("$rip@l-08");
			ServiceClient client = serviceStub._getServiceClient();
			client.getOptions().setProperty("PROXY", proxyProperties);
			authSlip.setXml(dataXML);
			gLogger.info((new StringBuilder("Before Response ::: ")).append(
					(new Date()).getTime()).toString());
			authSlipResponse = serviceStub.getAuthSlip(authSlip);
			gLogger.info((new StringBuilder("After Response ::: ")).append(
					(new Date()).getTime()).toString());
			gLogger.info((new StringBuilder("authSlipResponse ::: ")).append(
					authSlipResponse).toString());
			AuthorizationServiceStub.AuthorizationSlip slips[] = authSlipResponse
			.get_return();
			if (slips != null && slips.length > 0 && slips[0] != null) {
				gLogger.info((new StringBuilder("slips[0].getAuthNO() ::: "))
						.append(slips[0].getAuthNO()).toString());
				String authNo = slips[0].getAuthNO();
				gLogger.info((new StringBuilder("slips[0].getAuthPdf() ::: "))
						.append(slips[0].getAuthPdf()).toString());
				DataHandler DH = slips[0].getAuthPdf();
				byte pdfData[] = null;
				if (DH != null) {
					DataSource DS = DH.getDataSource();
					pdfData = getBytesFromInputStream(DS.getInputStream());
				}
				gLogger.info((new StringBuilder("slips[0].getStatusCode() ::: "))
						.append(slips[0].getStatusCode()).toString());
				String statusCode = slips[0].getStatusCode();
				billAprvDtlMap.put("statusCode", statusCode);
				billAprvDtlMap.put("authNo", authNo);
				billAprvDtlMap.put("pdfData", pdfData);
			}
		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error((new StringBuilder(
			"Exception in getBillApprvFrmBEAMSWS is ::")).append(
					e.getMessage()).toString());
		}
		return billAprvDtlMap;
	}

	public ResultObject getBeamsAuthSlip(Map objectArgs) {

		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) objectArgs
		.get("requestObj");
		ServiceLocator serv = (ServiceLocator) objectArgs.get("serviceLocator");
		HttpServletResponse response = (HttpServletResponse) objectArgs
		.get("responseObj");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		TreasuryIntegrationDAO lObjTreasuryIntegrationDAO = null;
		List<TrnIfmsBeamsIntegration> lLstTrnIfmsBeamsIntegration = null;
		TrnIfmsBeamsIntegration lObjTrnIfmsBeamsIntegration = null;
		try {
			lObjTreasuryIntegrationDAO = new TreasuryIntegrationDAOImpl(
					TrnIfmsBeamsIntegration.class, serv.getSessionFactory());
			String authNo = !"".equals(StringUtility.getParameter("authNo",
					request)) ? StringUtility.getParameter("authNo", request)
							.toString() : "";
							gLogger.info("authNo is ::" + authNo);

							lLstTrnIfmsBeamsIntegration = lObjTreasuryIntegrationDAO
							.getListByColumnAndValue("authNo", authNo);
							byte[] lBytes = null;
							if (lLstTrnIfmsBeamsIntegration != null
									&& lLstTrnIfmsBeamsIntegration.size() > 0) {
								lObjTrnIfmsBeamsIntegration = lLstTrnIfmsBeamsIntegration
								.get(0);
								Blob blob = lObjTrnIfmsBeamsIntegration.getAuthSlip();
								gLogger.info("blob is ::; " + blob);
								int blobLength = (int) blob.length();
								gLogger.info("blobLength is ::; " + blobLength);
								lBytes = blob.getBytes(1, blobLength);
								gLogger.info("lBytes is ::; " + lBytes.length);
							}
							baos.write(lBytes);

							// // Start code for Display Download Pdf Option in your browser
							response.setContentLength(lBytes.length);
							response.setContentType("application/pdf");
							response.addHeader("Content-Disposition",
							"inline;filename=authSlip.pdf");
							response.getOutputStream().write(lBytes);
							response.getOutputStream().flush();
							response.getOutputStream().close();

							resultObject.setResultValue(objectArgs);
							resultObject.setResultCode(ErrorConstants.SUCCESS);
							resultObject.setViewName("authorizationSlip");

							// //End Code For Display AuthSlip Pdf in New Jsp
		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Exception Occurrs in getData Method of DisplayOuterServiceImpl..Exception is "
					+ e.getMessage());
		}
		return resultObject;
	}

	public ResultObject saveBEAMSVoucherDetails(Map objectArgs)
	throws Exception {

		gLogger.info("saveBEAMSVoucherDetails called");
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) objectArgs
		.get("requestObj");
		ServiceLocator serv = (ServiceLocator) objectArgs.get("serviceLocator");
		HttpServletResponse response = (HttpServletResponse) objectArgs
		.get("responseObj");
		TreasuryIntegrationDAO lObjTreasuryIntegrationDAO = null;
		TrnBillRegisterDAO lObjTrnBillRegisterDAO = null;
		String lStrAuthNo = null;
		String lStrBeamsBillType = null;
		Long lLngBillNo = null;
		String lStrDDOCode = null;
		String lStrDtlHead = null;
		Object[] lArrObjBEAMSDtls = null;
		boolean isError = false;
		// Integer lIntVoucherNo = null;
		// Date lDtVoucher = null;
		// String lStrBEAMSBillStatus = null;
		List<TrnIfmsBeamsIntegration> lLstTrnIfmsBeamsIntegration = null;
		// List<TrnBillRegister> lLstTrnBillRegister = null;
		String lStrBillType = null;
		TrnBillRegister lObjTrnBillRegister = null;
		PensionBillDAO lObjPensionDao = null;
		List<Long> lLstBillNo = new ArrayList<Long>();
		TrnBillMvmnt lObjBillMvmnt = null;
		Map lMapLogin = new HashMap();
		CmnLocationMst lObjCmnLocationMst = new CmnLocationMst();
		lObjCmnLocationMst.setLocationCode("10000");
		lMapLogin.put("locationVO", lObjCmnLocationMst);
		lMapLogin.put("userId", Constants.BEAMS_USER_ID);
		lMapLogin.put("langId", 1L);
		objectArgs.put("baseLoginMap", lMapLogin);
		boolean isValidBillType = false;
		try {
			lObjTreasuryIntegrationDAO = new TreasuryIntegrationDAOImpl(
					TrnIfmsBeamsIntegration.class, serv.getSessionFactory());
			lObjTrnBillRegisterDAO = new TrnBillRegisterDAOImpl(
					TrnBillRegister.class, serv.getSessionFactory());
			lObjPensionDao = new PensionBillDAOImpl(serv.getSessionFactory());
			lLngBillNo = (Long) objectArgs.get("billNo");
			lStrAuthNo = (String) objectArgs.get("authNo");
			lStrDDOCode = (String) objectArgs.get("ddoCode");
			lStrBeamsBillType = (String) objectArgs.get("billType");
			lStrDtlHead = (String) objectArgs.get("detailHead");
			// lIntVoucherNo = (Integer) objectArgs.get("voucherNo");
			// lDtVoucher = (Date) objectArgs.get("voucherDate");
			// lStrBEAMSBillStatus = (String) objectArgs.get("beamsBillStatus");
			gLogger.info("before dao method");
			lLstBillNo.add(lLngBillNo);

			// Eidted on 07th October 2013
			lArrObjBEAMSDtls = lObjTreasuryIntegrationDAO
			.validateBEAMSBillAuthNo(lLngBillNo, lStrAuthNo);

			if (lArrObjBEAMSDtls != null) {
				if (lArrObjBEAMSDtls[0] != null) {
					String lStrDBAuthNo = (String) lArrObjBEAMSDtls[1];
					String lStrBEAMSBillStatus = (String) lArrObjBEAMSDtls[2];
					if (lStrDBAuthNo != null) {
						if (!lStrAuthNo.equals(lStrDBAuthNo)) {
							objectArgs.put("isAuthNoInvalid", true);
							isError = true;
						}
					} else {
						objectArgs.put("isAuthNoInvalid", true);
						isError = true;
					}

					// Added by Mubeen on on 07th October 2013 - Start
					if ((lStrDtlHead != null) && (lStrDtlHead.equals("01"))) {
						if (lStrBEAMSBillStatus != null) {
							isError = true;
							objectArgs.put("BeamsBillStatus",
									lStrBEAMSBillStatus.trim());
						}
					}
					// Added by Mubeen on on 07th October 2013 - End

				} else {
					objectArgs.put("isBillNoInvalid", true);
					isError = true;
				}
			} else {
				objectArgs.put("isBillNoInvalid", true);
				isError = true;
			}

			//added by aditya for Check in Reverse Rejection for Voucher
			/*String voucherFlag=lObjTreasuryIntegrationDAO.getVoucherFlagForReject(lLngBillNo);
			if(voucherFlag.equalsIgnoreCase("true")){
				objectArgs.put("isVoucherNoInvalid", true);
				isError=true;
			}*/


			//added by aditya for Check in Reverse Rejection for Voucher

			// --Check if bill type exists with the given detail head or not.
			isValidBillType = lObjTreasuryIntegrationDAO.isValidBillType(
					lStrDtlHead, lStrBeamsBillType);
			if (!isValidBillType) {
				objectArgs.put("isBillTypeInvalid", true);
				isError = true;
			}

			// Added by Mubeen on 07th October 2013 - Start

			// Check if data available in Itegration Tables for the given
			// details

			String[] lArrCriteria = new String[5];
			Object[] lArrObj = new Object[5];

			if ((lStrDtlHead != null) && (lStrDtlHead.equals("04"))) {
				// For Pension DtlHead = 4
				lArrCriteria[0] = "billNo";
				lArrCriteria[1] = "authNo";
				lArrCriteria[2] = "dtlheadCode";
				lArrCriteria[3] = "ddoCode";
				lArrCriteria[4] = "beamsBillType";

				lArrObj[0] = lLngBillNo;
				lArrObj[1] = lStrAuthNo;
				lArrObj[2] = lStrDtlHead;
				lArrObj[3] = lStrDDOCode;
				lArrObj[4] = lStrBeamsBillType;
			} else if (lStrDtlHead.equals("01")) {
				// For Payroll DtlHead = 1
				lArrCriteria[0] = "paybillId";
				lArrCriteria[1] = "authNo";
				lArrCriteria[2] = "dtlheadCode";
				lArrCriteria[3] = "ddoCode";
				lArrCriteria[4] = "billType";

				lArrObj[0] = lLngBillNo;
				lArrObj[1] = lStrAuthNo;
				lArrObj[2] = lStrDtlHead;
				lArrObj[3] = lStrDDOCode;
				lArrObj[4] = lStrBeamsBillType;
			}

			// Added by Mubeen on 07th October 2013 - End

			// --If billno and authno are valid and exists in
			// trn_ifms_beams_integration then updating voucher details
			if (!isError) {
				lLstTrnIfmsBeamsIntegration = lObjTreasuryIntegrationDAO
				.getListByColumnAndValue(lArrCriteria, lArrObj);
				if (lLstTrnIfmsBeamsIntegration != null
						&& lLstTrnIfmsBeamsIntegration.size() == 1) {
					TrnIfmsBeamsIntegration lObjTrnIfmsBeamsIntegration = lLstTrnIfmsBeamsIntegration
					.get(0);
					lStrBillType = lObjTrnIfmsBeamsIntegration.getBillType();
					// lObjTrnIfmsBeamsIntegration.setVoucherNo(lIntVoucherNo);
					// lObjTrnIfmsBeamsIntegration.setVoucherDate(lDtVoucher);
					// lObjTrnIfmsBeamsIntegration.setBeamsBillStatus(lStrBEAMSBillStatus);
					lObjTrnIfmsBeamsIntegration.setBeamsBillStatus("0");
					lObjTrnIfmsBeamsIntegration.setUpdatedDate(DBUtility
							.getCurrentDateFromDB());
					lObjTrnIfmsBeamsIntegration
					.setUpdatedUserId(Constants.BEAMS_USER_ID);
					lObjTrnIfmsBeamsIntegration
					.setUpdatedPostId(Constants.BEAMS_POST_ID);
					lObjTreasuryIntegrationDAO
					.update(lObjTrnIfmsBeamsIntegration);
					gLogger.info("Voucher details updated in trn_ifms_beams_integration successfully ..");
				} else {
					objectArgs.put("isRecordFound", false);
					isError = true;
				}
				if (lStrBillType != null) {

					System.out
					.println("PENSION_BEAMS 08 Nov 2013: lStrDtlHead: "
							+ lStrDtlHead);

					// ---Pension Business Logic goes here..
					if (Constants.INTEGRATION_PENSION_BILL_TYPE
							.equals(lStrBillType) && lLngBillNo != null) {
						rejectPensionBillByBEAMS(objectArgs, lLngBillNo);
					}

					// Added by Mubeen on 08th November 2013 - Start
					// INTEGRATION_PENSION_BILL_TYPE = "Pension"
					// INTEGRATION_PAYROLL_BILL_TYPE = "02"
					// Note: Due to above difference when It executes the below
					// Code Number Format Exception comes
					// to Avoid this now it will run the below code only when
					// lStrDtlHead != 4 (PENSION)

					if ((lStrDtlHead != null) && (!lStrDtlHead.equals("04"))) {
						// ------Payroll Business Logic goes here..
						if (Integer
								.parseInt(Constants.INTEGRATION_PAYROLL_BILL_TYPE) == Integer
								.parseInt(lStrBillType)
								|| Integer.parseInt(lStrBillType) == 3
								|| Integer.parseInt(lStrBillType) == 16 || Integer.parseInt(lStrBillType) == 15)///15 - suspension bill type 
						{
							if (lObjTreasuryIntegrationDAO
									.rejectSevaarthPaybill(lLngBillNo,
											lStrAuthNo) <= 0)
								isError = true;
						}
					}
					// Added by Mubeen on 08th November 2013 - End

				}

			}
			objectArgs.put("isError", isError);

		} catch (Exception e) {
			gLogger.info("Exception is e :" + e);
			objectArgs.put("isError", true);
			throw e;
		}
		return resultObject;
	}

	public ResultObject generateSBICMPExcelFile(Map objectArgs)
	throws Exception {

		gLogger.info("generateSBICMPExcelFile called");
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) objectArgs
		.get("requestObj");
		ServiceLocator serv = (ServiceLocator) objectArgs.get("serviceLocator");
		HttpServletResponse response = (HttpServletResponse) objectArgs
		.get("responseObj");
		String lStrBillNo = null;
		String lStrBillType = null;
		TreasuryIntegrationDAO lObjTreasuryIntegrationDAO = null;
		Long lLngBillNo = null;
		List<Object[]> lLstBillSummaryDtls = null;
		List<Object[]> lLstBillBeneficiaryDtls = null;
		List lLstSheetName = null;
		List lLstSheetHeader = null;
		List<ColumnVo[]> lLstBillSummaryColumns = null;
		List lLstColumnData = null;
		List<List> lLstDataRows = new ArrayList<List>();
		List<List<List>> lLstSheets = new ArrayList<List<List>>();
		ExcelExportHelper lObjExcelExportHelper = new ExcelExportHelper();
		try {
			lStrBillNo = StringUtility.getParameter("billNo", request).trim();
			lStrBillType = StringUtility.getParameter("billType", request)
			.trim();
			if (lStrBillNo.length() > 0 && lStrBillType.length() > 0) {
				lLngBillNo = Long.valueOf(lStrBillNo);
				lObjTreasuryIntegrationDAO = new TreasuryIntegrationDAOImpl(
						TrnIfmsBeamsIntegration.class, serv.getSessionFactory());
				if (Constants.INTEGRATION_PENSION_BILL_TYPE
						.equals(lStrBillType)) {
					TrnBillRegisterDAO lObjTrnBillRegisterDAO = null;
					TrnPensionSupplyBillDtls lObjTrnPensionSupplyBillDtls = null;
					List<TrnPensionSupplyBillDtls> lLstTrnPensionSupplyBillDtls = null;
					TrnPensionSupplyBillDtlsDAO lObjTrnPensionSupplyBillDtlsDAO = null;
					lObjTrnBillRegisterDAO = new TrnBillRegisterDAOImpl(
							TrnBillRegister.class, serv.getSessionFactory());
					lObjTrnPensionSupplyBillDtlsDAO = new TrnPensionSupplyBillDtlsDAOImpl(
							TrnPensionSupplyBillDtls.class,
							serv.getSessionFactory());
					TrnBillRegister lObjTrnBillRegister = lObjTrnBillRegisterDAO
					.read(lLngBillNo);
					String lStrSuppBillType = null;
					if (lObjTrnBillRegister != null) {
						lLstBillSummaryDtls = lObjTreasuryIntegrationDAO
						.getPensionBillSummaryForCMP(lLngBillNo);
						if (lObjTrnBillRegister.getSubjectId() == 9
								|| lObjTrnBillRegister.getSubjectId() == 10
								|| lObjTrnBillRegister.getSubjectId() == 11) {
							lLstBillBeneficiaryDtls = lObjTreasuryIntegrationDAO
							.getFirstPayBillBeneficiaryDtls(lLngBillNo);
						} else if (lObjTrnBillRegister.getSubjectId() == 44) {
							lLstBillBeneficiaryDtls = lObjTreasuryIntegrationDAO
							.getMonthlySuppBillBeneficiaryDtls(lLngBillNo);
						} else if (lObjTrnBillRegister.getSubjectId() == 45) {
							lLstTrnPensionSupplyBillDtls = lObjTrnPensionSupplyBillDtlsDAO
							.getListByColumnAndValue("billNo",
									Long.valueOf(lStrBillNo));
							if (lLstTrnPensionSupplyBillDtls != null
									&& lLstTrnPensionSupplyBillDtls.size() > 0) {
								lObjTrnPensionSupplyBillDtls = lLstTrnPensionSupplyBillDtls
								.get(0);
								lStrSuppBillType = lObjTrnPensionSupplyBillDtls
								.getBillType();
								if ("PENSION".equals(lStrSuppBillType)) {
									lLstBillBeneficiaryDtls = lObjTreasuryIntegrationDAO
									.getMonthlySuppBillBeneficiaryDtls(lLngBillNo);
								} else {
									lLstBillBeneficiaryDtls = lObjTreasuryIntegrationDAO
									.getFirstPayBillBeneficiaryDtls(lLngBillNo);
								}
							}
						}
					}
				}
				if (Constants.INTEGRATION_PAYROLL_BILL_TYPE
						.equals(lStrBillType)) {

				}
				/**
				 * Excel Report Generation Starts
				 */
				lLstSheetName = new ArrayList();

				lLstSheetName.add("Bill Summary");
				lLstSheetName.add("Beneficiary Details");
				Short lShAlignLeft = 1;
				Short lShAlignCenter = 2;
				Short lShAlignRight = 3;

				lLstSheetHeader = new ArrayList();
				lLstSheetHeader.add("Bill Summary");
				lLstSheetHeader.add("Beneficiary Details");
				lLstBillSummaryColumns = new ArrayList<ColumnVo[]>();
				ColumnVo[] lBillSummaryColHdg = new ColumnVo[7];
				lBillSummaryColHdg[0] = new ColumnVo("Bill No", lShAlignLeft,
						10, DBConstants.DATA_TYPE_STRING);
				lBillSummaryColHdg[1] = new ColumnVo("Scheme Code",
						lShAlignLeft, 15, DBConstants.DATA_TYPE_STRING);
				lBillSummaryColHdg[2] = new ColumnVo("Scheme Description",
						lShAlignLeft, 40, DBConstants.DATA_TYPE_STRING);
				lBillSummaryColHdg[3] = new ColumnVo("Bill Month-Year",
						lShAlignLeft, 15, DBConstants.DATA_TYPE_STRING);
				lBillSummaryColHdg[4] = new ColumnVo("No. Of Beneficiary",
						lShAlignRight, 30, DBConstants.DATA_TYPE_INTEGER);
				lBillSummaryColHdg[5] = new ColumnVo("Gross Amount",
						lShAlignRight, 15, DBConstants.DATA_TYPE_DOUBLE);
				lBillSummaryColHdg[6] = new ColumnVo("Net Amount",
						lShAlignRight, 15, DBConstants.DATA_TYPE_DOUBLE);
				lLstBillSummaryColumns.add(lBillSummaryColHdg);

				for (Object[] lArrBillSummaryDtls : lLstBillSummaryDtls) {
					lLstColumnData = new ArrayList();
					// Bill No
					lLstColumnData
					.add((lArrBillSummaryDtls[0] != null) ? lArrBillSummaryDtls[0]
					                                                            : "");
					// Scheme Code
					lLstColumnData
					.add((lArrBillSummaryDtls[1] != null) ? lArrBillSummaryDtls[1]
					                                                            : "");
					// Scheme Description
					lLstColumnData
					.add((lArrBillSummaryDtls[2] != null) ? lArrBillSummaryDtls[2]
					                                                            : "");
					// Bill Month-Year
					lLstColumnData
					.add((lArrBillSummaryDtls[3] != null) ? lArrBillSummaryDtls[3]
					                                                            : "");
					// No. Of Beneficiary
					lLstColumnData
					.add((lArrBillSummaryDtls[4] != null) ? lArrBillSummaryDtls[4]
					                                                            : 0);
					// Gross Amount
					lLstColumnData
					.add((lArrBillSummaryDtls[5] != null) ? lArrBillSummaryDtls[5]
					                                                            : 0.00);
					// Net Amount
					lLstColumnData
					.add((lArrBillSummaryDtls[6] != null) ? lArrBillSummaryDtls[6]
					                                                            : 0.00);
					lLstDataRows.add(lLstColumnData);
				}
				lLstSheets.add(lLstDataRows);
				lObjExcelExportHelper.getPwdProtectedExcel(lLstSheets,
						lLstColumnData, null, lLstSheetHeader, null,
						lLstSheetName);
				/**
				 * Excel Report Generation Ends
				 */

			}

		} catch (Exception e) {
			gLogger.info("Exception is e :" + e);
			throw e;
		}
		return resultObject;

	}

	public byte[] getBytesFromInputStream(InputStream lObjInputStream) {

		byte fileContent[] = null;
		try {
			int counter = -1;
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			while ((counter = lObjInputStream.read()) != -1) {
				outStream.write(counter);
			}
			outStream.flush();
			fileContent = outStream.toByteArray();
		} catch (Exception e) {
			gLogger.error((new StringBuilder(
			"General Exception for main Try Block in getBytesFromInputStream method "))
			.append(e.getMessage()).toString());
		}
		return fileContent;
	}

	public String getBEAMSBillType(TrnBillRegister lObjTrnBillRegister,
			SessionFactory sessionFactory) throws Exception {

		String lStrBillType = "";
		try {
			TrnPensionSupplyBillDtlsDAO lObjTrnPensionSupplyBillDtlsDAO = new TrnPensionSupplyBillDtlsDAOImpl(
					TrnPensionSupplyBillDtls.class, sessionFactory);
			List<TrnPensionSupplyBillDtls> lLstTrnPensionSupplyBillDtls = null;
			TrnPensionSupplyBillDtls lObjTrnPensionSupplyBillDtls = null;
			long lSubjectId = lObjTrnBillRegister.getSubjectId();
			if (lSubjectId == 45) {
				lLstTrnPensionSupplyBillDtls = lObjTrnPensionSupplyBillDtlsDAO
				.getListByColumnAndValue("billNo",
						lObjTrnBillRegister.getBillNo());
				if (lLstTrnPensionSupplyBillDtls != null
						&& lLstTrnPensionSupplyBillDtls.size() > 0) {
					lObjTrnPensionSupplyBillDtls = lLstTrnPensionSupplyBillDtls
					.get(0);
				}
				if (lObjTrnPensionSupplyBillDtls != null) {
					if ("PENSION".equals(lObjTrnPensionSupplyBillDtls
							.getBillType())) {
						//commented by aditya on 25th March 14
						//lStrBillType = "03";
						lStrBillType = "04";
					}
					if ("CVP"
							.equals(lObjTrnPensionSupplyBillDtls.getBillType())) {
						lStrBillType = "02";
					}
					if ("DCRG".equals(lObjTrnPensionSupplyBillDtls
							.getBillType())) {
						lStrBillType = "11";
					}
				}
			}
			if (lSubjectId == 44) {
				lStrBillType = "03";
			}
			if (lSubjectId == 9) {
				lStrBillType = "04";
			}
			if (lSubjectId == 10) {
				lStrBillType = "11";
			}
			if (lSubjectId == 11) {
				lStrBillType = "02";
			}
		} catch (Exception e) {
			throw e;
		}
		return lStrBillType;
	}

	public void rejectPensionBillByBEAMS(Map objectArgs, Long lLngBillNo)
	throws Exception {

		TrnBillRegister lObjTrnBillRegister = null;
		TrnBillRegisterDAO lObjTrnBillRegisterDAO = null;
		ServiceLocator serv = (ServiceLocator) objectArgs.get("serviceLocator");
		TrnBillMvmnt lObjBillMvmnt = null;

		try {
			lObjTrnBillRegisterDAO = new TrnBillRegisterDAOImpl(
					TrnBillRegister.class, serv.getSessionFactory());
			PensionBillServiceImpl lObjPensionBillService = new PensionBillServiceImpl();
			List<TrnPensionSupplyBillDtls> lLstTrnPensionSupplyBillDtls = null;
			TrnPensionSupplyBillDtls lObjTrnPensionSupplyBillDtls = null;
			TrnPensionSupplyBillDtlsDAO lObjTrnPensionSupplyBillDtlsDAO = new TrnPensionSupplyBillDtlsDAOImpl(
					TrnPensionSupplyBillDtls.class, serv.getSessionFactory());
			SupplementaryBillService lObjSupplementaryBillService = new SupplementaryBillServiceImpl();
			lObjTrnBillRegister = lObjTrnBillRegisterDAO.read(lLngBillNo);

			if (lObjTrnBillRegister != null) {
				Long lLngSubId = lObjTrnBillRegister.getSubjectId();
				lObjTrnBillRegister
				.setCurrBillStatus(DBConstants.ST_BILL_REJECTED);
				objectArgs.put("BillRegVO", lObjTrnBillRegister);
				objectArgs.put("ReqFromBEAMS", "Y");
				if (lLngSubId.equals(45L)) {
					lLstTrnPensionSupplyBillDtls = lObjTrnPensionSupplyBillDtlsDAO
					.getListByColumnAndValue("billNo",
							lObjTrnBillRegister.getBillNo());
					if (lLstTrnPensionSupplyBillDtls != null
							&& lLstTrnPensionSupplyBillDtls.size() > 0) {
						lObjTrnPensionSupplyBillDtls = lLstTrnPensionSupplyBillDtls
						.get(0);
					}
				}

				if (lObjTrnPensionSupplyBillDtls != null
						&& "PENSION".equals(lObjTrnPensionSupplyBillDtls
								.getBillType())) {
					//commented by aditya for reverse supplemenatary
					/*if (lObjTrnPensionSupplyBillDtls != null) {*/
					objectArgs.put("IntegBilNo",
							lObjTrnBillRegister.getBillNo());
					objectArgs.put("IntegUserAction", "R");
					//objectArgs.put("NoSessions","N");
					lObjSupplementaryBillService
					.approveRejectSupplPensionBill(objectArgs);

				} else if (lLngSubId.equals(44L)) {

					System.out
					.println("Mubeen: Monthly Rejection Logic - Start");
					// Mubeen 06 Dec 2012 3rd Rejection Logic - Start
					List<BigInteger> lBigIntLst = null;
					List<Long> lLstBillNo = null;
					MonthlyPensionBillService lObjMonthlyPensionBillService = null;
					TreasuryIntegrationDAO lObjTreasuryIntegrationDAO = null;
					MonthlyPensionBillDAO lObjMonthlyPensionBillDAO = null;

					lObjMonthlyPensionBillService = new MonthlyPensionBillServiceImpl();
					lObjTreasuryIntegrationDAO = new TreasuryIntegrationDAOImpl(
							TrnIfmsBeamsIntegration.class,
							serv.getSessionFactory());
					lObjMonthlyPensionBillDAO = new MonthlyPensionBillDAOImpl(
							MstPensionerHdr.class, serv.getSessionFactory());

					Integer lIntForMonth = lObjTrnBillRegister.getForMonth();
					String lStrLocationCode = lObjTrnBillRegister
					.getLocationCode();

					lBigIntLst = lObjTreasuryIntegrationDAO
					.getListOfBillsForBEAMSReverseReject(lIntForMonth,
							lStrLocationCode);

					if ((lBigIntLst.size() == 1)) {
						if (lLngBillNo.equals(Long.valueOf(((lBigIntLst.get(0))
								.longValue())))) {
							lLstBillNo = lObjMonthlyPensionBillDAO
							.getAllBillNoToReject(
									lIntForMonth.toString(),
									lStrLocationCode);
							objectArgs.put("BillList", lLstBillNo);

							// Added by Mubeen on 07th October 2013
							objectArgs.put("LocationCode", lStrLocationCode);

							// Added by Mubeen
							System.out.println("Mubeen: objectArgs Map values "
									+ objectArgs);

							System.out
							.println("Mubeen: Before Calling rejectAllMonthlyBillForBEAMS ");
							lObjMonthlyPensionBillService
							.rejectAllMonthlyBillForBEAMS(objectArgs);
							// lObjMonthlyPensionBillService.rejectAllMonthlyBillForBEAMS(objectArgs);
							System.out
							.println("Mubeen: After Calling rejectAllMonthlyBillForBEAMS ");

						}
					}
					System.out.println("Mubeen: Monthly Rejection Logic - End");
					// Mubeen 06 Dec 2012 3rd Rejection Logic - End

				} else {
					lObjBillMvmnt = new TrnBillMvmnt();
					lObjBillMvmnt.setMovemntId(1L);
					lObjBillMvmnt.setBillNo(lObjTrnBillRegister.getBillNo());
					lObjBillMvmnt.setStatusUpdtUserid(Constants.BEAMS_USER_ID);
					lObjBillMvmnt.setStatusUpdtPostid(Constants.BEAMS_POST_ID);
					lObjBillMvmnt.setStatusUpdtDate(DBUtility
							.getCurrentDateFromDB());
					lObjBillMvmnt.setReceivingUserId(Constants.BEAMS_USER_ID);
					lObjBillMvmnt.setReceivedDate(DBUtility
							.getCurrentDateFromDB());
					lObjBillMvmnt.setCreatedUserId(Constants.BEAMS_USER_ID);
					lObjBillMvmnt.setCreatedPostId(Constants.BEAMS_POST_ID);
					lObjBillMvmnt.setCreatedDate(DBUtility
							.getCurrentDateFromDB());
					lObjBillMvmnt.setDbId(99L);
					lObjBillMvmnt.setLocationCode(lObjTrnBillRegister
							.getLocationCode());
					lObjBillMvmnt.setBillRemarks("Rejected By BEAMS");
					lObjBillMvmnt.setRoleId(Long.valueOf(pensionBundleConst
							.getString("PPMT.ATOROLE")));
					objectArgs.put("BillMvmntVO", lObjBillMvmnt);
					objectArgs.put("IntegCurrRole", "365451");
					objectArgs.put("IntegUserAction", "reject");
					lObjPensionBillService.updatePensionBill(objectArgs);
				}
				lObjTrnBillRegister.setUpdatedDate(DBUtility
						.getCurrentDateFromDB());
				lObjTrnBillRegister.setUpdatedUserId(Constants.BEAMS_USER_ID);
				lObjTrnBillRegister.setUpdatedPostId(Constants.BEAMS_POST_ID);
				lObjTrnBillRegisterDAO.update(lObjTrnBillRegister);

				System.out.println("Mubeen: rejectPensionBillByBEAMS - End");
			}
		} catch (Exception e) {
			gLogger.info("Exception is e :" + e);
			throw e;
		}
	}

	public ResultObject saveVoucherDetailsFromArthwahini(Map objectArgs) {

		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		String lStrDtlsHead = null;
		String lStrAuthNo = null;
		String lStrVoucherNo = null;
		String lStrVoucherDate = null;
		String lStrBillType = null;
		Long lLngVoucherNo = null;
		Date lDtVoucher = null;
		StringBuilder lSBResultXML = new StringBuilder();
		StringBuilder lSBStatusCode = new StringBuilder();
		boolean isError = false;
		Set lSetSrNoNAAuthNo = new HashSet();
		Set<String> lSetAuthNosInvalidDtlHead = new HashSet<String>();
		Set<String> lSetAuthNosInvalidBillType = new HashSet<String>();
		Set<String> lSetAuthNosInvalidVoucherNo = new HashSet<String>();
		Set<String> lSetAuthNosInvalidVoucherDate = new HashSet<String>();
		Set<String> lSetAuthNosDuplicate = new HashSet<String>();
		Set<String> lSetErrorCodes = new HashSet<String>();
		long totalRecordsCnt = 0;
		SimpleDateFormat lSdf = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, String> lMapAuthNoDuplicateCheck = new HashMap<String, String>();
		List<List<String>> lLstAuthNoBatch = new ArrayList<List<String>>();
		List<String> lLstAuthNo = new ArrayList<String>();
		TrnIfmsArthwahiniIntegration lObjTrnIfmsArthwahiniIntegration = null;
		StringBuilder lSbErrorCodes = null;
		Map<String, TrnIfmsArthwahiniIntegration> lMapArthwahiniIntegDtls = new HashMap<String, TrnIfmsArthwahiniIntegration>();
		Long lLngTrnIfmsArthwahiniIntegId = null;
		Long lLngTrnIfmsArthwahiniIntegErrorDtlsId = null;
		Long lLngArthwahiniIntegPk = null;
		Long lLngArthwahiniIntegDelvId = null;
		Map<String, Set<String>> lMapAuthNoErrorCodes = new HashMap<String, Set<String>>();
		TreasuryIntegrationDAO lObjTreasuryIntegrationDAO = null;
		ServiceLocator serv = (ServiceLocator) objectArgs.get("serviceLocator");
		List<String> lLstAllAuthNo = new ArrayList<String>();
		List<String> lLstAvailableAuthNo = new ArrayList<String>();
		List<String> lLstAllAvailableAuthNo = new ArrayList<String>();
		List<String> lLstNotAvailableAuthNo = new ArrayList<String>();
		List<String> lLstAvailVoucherDtlsAuthNo = new ArrayList<String>();
		List<String> lLstAllAvailVoucherDtlsAuthNo = new ArrayList<String>();
		Integer lIntTotalErrorRecords = 0;
		Long lLngArthwahiniIntegErrorDtlsPk = null;
		Long lLngArthwahiniIntegErrorDtlsId = null;
		TrnIfmsArthwahiniIntegErrorDtls lObjTrnIfmsArthwahiniIntegErrorDtls = null;
		List<Map> lLstVoucerDtls = null;
		Map lMapLogin = new HashMap();
		CmnLocationMst lObjCmnLocationMst = new CmnLocationMst();
		lObjCmnLocationMst.setLocationCode("10000");
		lMapLogin.put("locationVO", lObjCmnLocationMst);
		lMapLogin.put("userId", Constants.ARTHWAHINI_USER_ID);
		lMapLogin.put("langId", 1L);
		objectArgs.put("baseLoginMap", lMapLogin);

		OrgPostMst orgPostMst = new OrgPostMst();
		orgPostMst.setPostId(Constants.ARTHWAHINI_POST_ID);
		OrgUserMst orgUserMst = new OrgUserMst();
		orgUserMst.setUserId(Constants.ARTHWAHINI_USER_ID);
		CmnLocationMst cmnLocationMst = new CmnLocationMst();
		cmnLocationMst.setLocationCode("10000");
		cmnLocationMst.setLocId(10000);
		LoginDetails baseLoginVO = new LoginDetails();
		baseLoginVO.setDbId(99L);
		baseLoginVO.setLoggedInPost(orgPostMst);
		baseLoginVO.setLangId(1L);
		baseLoginVO.setUser(orgUserMst);
		baseLoginVO.setLocation(cmnLocationMst);
		objectArgs.put("baseLoginVO", baseLoginVO);

		List<String> lLstPensionBillTypes = new ArrayList<String>();
		lLstPensionBillTypes.add("02");
		lLstPensionBillTypes.add("03");
		lLstPensionBillTypes.add("04");
		lLstPensionBillTypes.add("11");

		StringBuilder lSBAllStatusCode = new StringBuilder();
		try {
			lLstVoucerDtls = (List<Map>) objectArgs.get("lLstVoucerDtls");
			lLngArthwahiniIntegPk = IFMSCommonServiceImpl
			.getCurrentSeqNumAndUpdateCount(
					"trn_ifms_arthwahini_integration", objectArgs,
					lLstVoucerDtls.size());
			lLngArthwahiniIntegDelvId = IFMSCommonServiceImpl.getNextSeqNum(
					"ifms_arthwahini_integration_delv_id", objectArgs);
			lObjTreasuryIntegrationDAO = new TreasuryIntegrationDAOImpl(
					TrnIfmsArthwahiniIntegration.class,
					serv.getSessionFactory());
			for (Map lMapVoucherDtls : lLstVoucerDtls) {
				lMapAuthNoErrorCodes = new HashMap<String, Set<String>>();
				lLngTrnIfmsArthwahiniIntegId = ++lLngArthwahiniIntegPk;
				lLngTrnIfmsArthwahiniIntegId = IFMSCommonServiceImpl
				.getFormattedPrimaryKey(lLngTrnIfmsArthwahiniIntegId,
						objectArgs);
				lSbErrorCodes = new StringBuilder();
				totalRecordsCnt++;
				isError = false;
				lStrAuthNo = (String) lMapVoucherDtls.get("AuthorizationNo");
				lStrDtlsHead = (String) lMapVoucherDtls.get("DetailHead");
				lStrBillType = (String) lMapVoucherDtls.get("BillType");
				lStrVoucherNo = (String) lMapVoucherDtls.get("VoucherNo");
				lStrVoucherDate = (String) lMapVoucherDtls.get("VoucherDate");

				lObjTrnIfmsArthwahiniIntegration = new TrnIfmsArthwahiniIntegration();
				lObjTrnIfmsArthwahiniIntegration
				.setIfmsArthwahiniIntegrationId(lLngTrnIfmsArthwahiniIntegId);
				lObjTrnIfmsArthwahiniIntegration
				.setDelvId(lLngArthwahiniIntegDelvId);
				lObjTrnIfmsArthwahiniIntegration.setAuthNo(lStrAuthNo);
				lObjTrnIfmsArthwahiniIntegration.setDtlheadCode(lStrDtlsHead);
				lObjTrnIfmsArthwahiniIntegration.setBeamsBillType(lStrBillType);
				lObjTrnIfmsArthwahiniIntegration.setVoucherNo(lStrVoucherNo);
				lObjTrnIfmsArthwahiniIntegration
				.setVoucherDate(lStrVoucherDate);
				lObjTrnIfmsArthwahiniIntegration
				.setCreatedUserId(Constants.ARTHWAHINI_USER_ID);
				lObjTrnIfmsArthwahiniIntegration
				.setCreatedPostId(Constants.ARTHWAHINI_POST_ID);
				lObjTrnIfmsArthwahiniIntegration.setCreatedDate(DBUtility
						.getCurrentDateFromDB());
				lObjTrnIfmsArthwahiniIntegration.setDbId(99);
				lObjTrnIfmsArthwahiniIntegration.setDelvSuccessStatus("Y");

				if (lLstPensionBillTypes.contains(lStrBillType)) {
					lObjTrnIfmsArthwahiniIntegration.setBillType("Pension");
				}
				if ((lStrAuthNo != null && lStrAuthNo.trim().length() > 0)) {
					lStrAuthNo = lStrAuthNo.trim();
				} else {
					lSbErrorCodes.append("01");
					lSetErrorCodes.add("01");
					isError = true;
					lSetSrNoNAAuthNo.add(totalRecordsCnt);
				}

				// --If authorization number is not available then skipping all
				// other validations.
				if (!isError) {
					if (lStrDtlsHead != null
							&& lStrDtlsHead.trim().length() > 0) {
						lStrDtlsHead = lStrDtlsHead.trim();
						if ((!"01".equals(lStrDtlsHead) && !"04"
								.equals(lStrDtlsHead))
								&& (!"50".equals(lStrDtlsHead) && !"55"
										.equals(lStrDtlsHead))) {
							// lSBStatusCode.append("02|");
							lSetErrorCodes.add("02");
							isError = true;
						}
					} else {
						// lSBStatusCode.append("02|");
						lSetErrorCodes.add("02");
						isError = true;
						lSetAuthNosInvalidDtlHead.add(lStrAuthNo);
					}

					if ((lStrBillType != null && lStrBillType.trim().length() > 0)) {
						lStrBillType = lStrBillType.trim();
					} else {
						// lSBStatusCode.append("03|");
						lSetErrorCodes.add("03");
						isError = true;
						lSetAuthNosInvalidBillType.add(lStrAuthNo);
					}

					if ((lStrVoucherNo != null && lStrVoucherNo.trim().length() > 0)) {
						lStrVoucherNo = lStrVoucherNo.trim();
						if (CommonServiceImpl.isValidLongNumber(lStrVoucherNo)) {
							lLngVoucherNo = Long.parseLong(lStrVoucherNo);
						} else {
							lSetErrorCodes.add("04");
							isError = true;
							lSetAuthNosInvalidVoucherNo.add(lStrAuthNo);
						}
					} else {
						lSetErrorCodes.add("04");
						isError = true;
						lSetAuthNosInvalidVoucherNo.add(lStrAuthNo);
					}

					if ((lStrVoucherDate != null && lStrVoucherDate.trim()
							.length() > 0)) {
						lStrVoucherDate = lStrVoucherDate.trim();
						if (CommonServiceImpl.isValidDate(lStrVoucherDate)) {
							lDtVoucher = lSdf.parse(lStrVoucherDate);
						} else {
							lSetErrorCodes.add("05");
							isError = true;
							lSetAuthNosInvalidVoucherDate.add(lStrAuthNo);
						}
					} else {
						lSetErrorCodes.add("05");
						isError = true;
						lSetAuthNosInvalidVoucherDate.add(lStrAuthNo);
					}

					if (lMapAuthNoDuplicateCheck.put(lStrAuthNo, lStrAuthNo) != null) {
						lSetErrorCodes.add("06");
						isError = true;
						lSetAuthNosDuplicate.add(lStrAuthNo);
					}

					if (lLstAuthNo.size() < 1000) {
						lLstAuthNo.add(lStrAuthNo);
					} else {
						lLstAuthNoBatch.add(lLstAuthNo);
						lLstAuthNo = new ArrayList<String>();
						lLstAuthNo.add(lStrAuthNo);
					}
				}

				if (isError) {
					lObjTrnIfmsArthwahiniIntegration.setDelvSuccessStatus("N");
					lMapAuthNoErrorCodes.put(lStrAuthNo, lSetErrorCodes);
				}
				lMapArthwahiniIntegDtls.put(lStrAuthNo,
						lObjTrnIfmsArthwahiniIntegration);
				lLstAllAuthNo.add(lStrAuthNo);
			}
			lLstAuthNoBatch.add(lLstAuthNo);// --Adding remaining authorization
			// number.

			// -----Getting list of auth number which are not available in
			// trn_ifms_beams_integration table.
			for (List<String> lLstInnAuthNo : lLstAuthNoBatch) {
				lLstAvailableAuthNo = lObjTreasuryIntegrationDAO
				.getListOfAuthNumberOfBEAMS(lLstInnAuthNo);
				if (lLstAvailableAuthNo != null
						&& lLstAvailableAuthNo.size() > 0) {
					lLstAllAvailableAuthNo.addAll(lLstAvailableAuthNo);
				}
			}
			lLstNotAvailableAuthNo.addAll(lLstAllAuthNo);
			lLstNotAvailableAuthNo.removeAll(lLstAllAvailableAuthNo);
			if (lLstNotAvailableAuthNo.size() > 0) {
				for (String lStrNAAuthNo : lLstNotAvailableAuthNo) {
					lSetErrorCodes = lMapAuthNoErrorCodes.get(lStrNAAuthNo);
					if (lSetErrorCodes != null) {
						lSetErrorCodes.add("07");
					} else {
						lSetErrorCodes = new HashSet<String>();
						lSetErrorCodes.add("07");
						lMapAuthNoErrorCodes.put(lStrNAAuthNo, lSetErrorCodes);
					}
					lObjTrnIfmsArthwahiniIntegration = lMapArthwahiniIntegDtls
					.get(lStrNAAuthNo);
					if (lObjTrnIfmsArthwahiniIntegration != null) {
						lObjTrnIfmsArthwahiniIntegration
						.setDelvSuccessStatus("N");
					}
				}
				isError = true;
			}

			// -----Getting list of auth number for which voucher details are
			// avaliable
			for (List<String> lLstInnAuthNo : lLstAuthNoBatch) {
				lLstAvailVoucherDtlsAuthNo = lObjTreasuryIntegrationDAO
				.getListOfAuthNoWithVouchDtlsAvailable(lLstInnAuthNo);
				if (lLstAvailVoucherDtlsAuthNo != null
						&& lLstAvailVoucherDtlsAuthNo.size() > 0) {
					lLstAllAvailVoucherDtlsAuthNo
					.addAll(lLstAvailVoucherDtlsAuthNo);
				}
			}
			if (lLstAllAvailVoucherDtlsAuthNo.size() > 0) {
				for (String lStrAvailVouchDtlsAuthNo : lLstAllAvailVoucherDtlsAuthNo) {
					lSetErrorCodes = lMapAuthNoErrorCodes
					.get(lStrAvailVouchDtlsAuthNo);
					if (lSetErrorCodes != null) {
						lSetErrorCodes.add("08");
					} else {
						lSetErrorCodes = new HashSet<String>();
						lSetErrorCodes.add("08");
						lMapAuthNoErrorCodes.put(lStrAvailVouchDtlsAuthNo,
								lSetErrorCodes);
					}
					lObjTrnIfmsArthwahiniIntegration = lMapArthwahiniIntegDtls
					.get(lStrAvailVouchDtlsAuthNo);
					if (lObjTrnIfmsArthwahiniIntegration != null) {
						lObjTrnIfmsArthwahiniIntegration
						.setDelvSuccessStatus("N");
					}
				}
				isError = true;
			}

			for (String lStrInnAuthNo : lLstAllAuthNo) {
				lObjTrnIfmsArthwahiniIntegration = lMapArthwahiniIntegDtls
				.get(lStrInnAuthNo);
				lObjTreasuryIntegrationDAO
				.create(lObjTrnIfmsArthwahiniIntegration);
			}

			Set<String> lSetErrorAuthNos = lMapAuthNoErrorCodes.keySet();
			Set<String> lSetTmpErrorCodes = null;
			for (String lStrInnAuthNo : lSetErrorAuthNos) {
				lSetTmpErrorCodes = lMapAuthNoErrorCodes.get(lStrInnAuthNo);
				if (lSetTmpErrorCodes != null) {
					lIntTotalErrorRecords = lIntTotalErrorRecords
					+ lSetTmpErrorCodes.size();
				}
			}
			lObjTreasuryIntegrationDAO = new TreasuryIntegrationDAOImpl(
					TrnIfmsArthwahiniIntegErrorDtls.class,
					serv.getSessionFactory());
			lLngArthwahiniIntegErrorDtlsPk = IFMSCommonServiceImpl
			.getCurrentSeqNumAndUpdateCount(
					"trn_ifms_arthwahini_integ_error_dtls", objectArgs,
					lIntTotalErrorRecords);
			for (String lStrInnAuthNo : lSetErrorAuthNos) {
				lSetErrorCodes = lMapAuthNoErrorCodes.get(lStrInnAuthNo);
				if (lSetErrorCodes != null) {
					for (String lStrErrorCode : lSetErrorCodes) {
						lLngTrnIfmsArthwahiniIntegErrorDtlsId = ++lLngArthwahiniIntegErrorDtlsPk;
						lLngTrnIfmsArthwahiniIntegErrorDtlsId = IFMSCommonServiceImpl
						.getFormattedPrimaryKey(
								lLngTrnIfmsArthwahiniIntegErrorDtlsId,
								objectArgs);
						lObjTrnIfmsArthwahiniIntegErrorDtls = new TrnIfmsArthwahiniIntegErrorDtls();
						lObjTrnIfmsArthwahiniIntegErrorDtls
						.setIfmsArthwahiniIntegErrorDtlsId(lLngTrnIfmsArthwahiniIntegErrorDtlsId);
						lObjTrnIfmsArthwahiniIntegErrorDtls
						.setAuthNo(lStrInnAuthNo);
						lObjTrnIfmsArthwahiniIntegErrorDtls
						.setDelvId(lLngArthwahiniIntegDelvId);
						lObjTrnIfmsArthwahiniIntegErrorDtls
						.setErrorCode(lStrErrorCode);
						lObjTrnIfmsArthwahiniIntegErrorDtls
						.setCreatedUserId(Constants.ARTHWAHINI_USER_ID);
						lObjTrnIfmsArthwahiniIntegErrorDtls
						.setCreatedPostId(Constants.ARTHWAHINI_POST_ID);
						lObjTrnIfmsArthwahiniIntegErrorDtls
						.setCreatedDate(DBUtility
								.getCurrentDateFromDB());
						lObjTreasuryIntegrationDAO
						.create(lObjTrnIfmsArthwahiniIntegErrorDtls);
					}
				}
			}

			StringBuilder lSBErrorCodes = new StringBuilder();
			lSBAllStatusCode.append("<Collection>");
			for (String lStrInnAuthNo : lLstAllAuthNo) {
				lSBAllStatusCode.append("<StatusDtls>");
				lSBAllStatusCode.append("<AuthorizationNo>");
				lSBAllStatusCode.append(lStrInnAuthNo);
				lSBAllStatusCode.append("</AuthorizationNo>");
				lSetErrorCodes = lMapAuthNoErrorCodes.get(lStrInnAuthNo);
				lSBAllStatusCode.append("<StatusCode>");
				if (lSetErrorCodes != null) {
					lSBErrorCodes = new StringBuilder();

					for (String lStrErrorCode : lSetErrorCodes) {
						lSBErrorCodes.append(lStrErrorCode);
						lSBErrorCodes.append("|");
					}
					lSBAllStatusCode.append(lSBErrorCodes);
					lSBErrorCodes = null;
				} else {
					lSBAllStatusCode.append("00");
				}
				lSBAllStatusCode.append("</StatusCode>");
				lSBAllStatusCode.append("</StatusDtls>");
			}
			lSBAllStatusCode.append("</Collection>");
			objectArgs.put("IntegStatusCodes", lSBAllStatusCode);
		} catch (Exception e) {
			gLogger.info("Exception is e :" + e);
		}
		return resultObject;
	}

	// Mubeen 22 Nov 2012 - Start
	public ResultObject checkBillForSchemePaymode(Map inputMap) {

		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);

		TreasuryIntegrationDAO lObjTreasuryIntegrationDAO = null;
		List<Object[]> lLstBillForSchemePaymode = null;
		StringBuilder lSBStatus = new StringBuilder();

		try {

			System.out.println(" Inside checkBillForSchemePaymode method ");
			System.out.println("1s");
			setSessionInfo(inputMap);
			System.out.println("2s");
			HttpServletRequest request = (HttpServletRequest) inputMap
			.get("requestObj");
			System.out.println("3s");
			ServiceLocator serv = (ServiceLocator) inputMap
			.get("serviceLocator");
			System.out.println("4s");
			lObjTreasuryIntegrationDAO = new TreasuryIntegrationDAOImpl(
					TrnIfmsBeamsIntegration.class, serv.getSessionFactory());
			System.out.println("5s");
			lLstBillForSchemePaymode = lObjTreasuryIntegrationDAO
			.getBillForSchemePaymode();
			System.out.println("6s");

			lSBStatus.append("<XMLDOC>");
			for (Object[] lObjArr : lLstBillForSchemePaymode) {
				String lStrSchemeCode = (String) lObjArr[0];
				String lStrPayMode = (String) lObjArr[1];
				System.out.println(lStrSchemeCode + "\t" + lStrPayMode + "\n");
				lSBStatus.append("<DETAIL>");
				lSBStatus.append("<SCHEMECODE>");
				lSBStatus.append("<![CDATA[");
				lSBStatus.append(lStrSchemeCode);
				lSBStatus.append("]]>");
				lSBStatus.append("</SCHEMECODE>");
				lSBStatus.append("<PAYMODE>");
				lSBStatus.append("<![CDATA[");
				lSBStatus.append(lStrPayMode);
				lSBStatus.append("]]>");
				lSBStatus.append("</PAYMODE>");
				lSBStatus.append("</DETAIL>");
			}
			lSBStatus.append("</XMLDOC>");
			System.out.println("7s");

			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
					lSBStatus.toString()).toString();
			System.out.println("8s");
			inputMap.put("ajaxKey", lStrResult);
			System.out.println("9s");
			resultObject.setViewName("ajaxData");
			System.out.println("10s");
			resultObject.setResultValue(inputMap);
			System.out.println("11s");

		} catch (Exception e) {
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
					lSBStatus.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resultObject.setViewName("ajaxData");
			resultObject.setResultValue(inputMap);
		}

		return resultObject;
	}

	// Mubeen 22 Nov 2012 - End

	private void setSessionInfo(Map<String, Object> inputMap) {

		gLngLangId = SessionHelper.getLangId(inputMap);
		gLngPostId = SessionHelper.getPostId(inputMap);
		gLngUserId = SessionHelper.getUserId(inputMap);
		gStrLocCode = SessionHelper.getLocationCode(inputMap);
		gDate = DBUtility.getCurrentDateFromDB();
		gDbId = SessionHelper.getDbId(inputMap);
		gStrLocShortName = SessionHelper.getLocationVO(inputMap)
		.getLocShortName();

	}
}
