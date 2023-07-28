package com.tcs.sgv.lna.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.loan.valueobject.HrLoanAdvMst;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.CmnDatabaseMst;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.eis.dao.LoanEmpIntRecvDAOImpl;
import com.tcs.sgv.eis.dao.LoanEmpPrinRecvDAOImpl;
import com.tcs.sgv.eis.util.CommomnDataObjectFatch;
import com.tcs.sgv.eis.valueobject.HrEisEmpMst;
import com.tcs.sgv.eis.valueobject.HrLoanEmpDtls;
import com.tcs.sgv.eis.valueobject.HrLoanEmpIntRecoverDtls;
import com.tcs.sgv.eis.valueobject.HrLoanEmpPrinRecoverDtls;
import com.tcs.sgv.ess.dao.EmpDAOImpl;
import com.tcs.sgv.ess.valueobject.OrgEmpMst;
import com.tcs.sgv.ess.valueobject.OrgPostMst;
import com.tcs.sgv.ess.valueobject.OrgUserMst;
import com.tcs.sgv.lna.dao.LNABillDtlsDAO;
import com.tcs.sgv.lna.dao.LNABillDtlsDAOImpl;
import com.tcs.sgv.lna.valueobject.MstLnaBillDtls;
import com.tcs.sgv.lna.valueobject.MstLnaPayrollLoanTypeMpg;

public class LNABillServiceImpl extends ServiceImpl implements LNABillService{

	Log gLogger = LogFactory.getLog(getClass());
	
	private String gStrPostId = null; /* STRING POST ID */

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for Current Date */
	Date gDtCurrDt = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;
	
	/* Global Variable for LangId */
	Long gLngLangId = null;
	
	Long gLngLocationCode = null;
	
	Long gLngDbId = null;
	
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/lna/LNAConstants");

	private void setSessionInfo(Map inputMap) {

		request = (HttpServletRequest) inputMap.get("requestObj");
		serv = (ServiceLocator) inputMap.get("serviceLocator");
		gLngPostId = SessionHelper.getPostId(inputMap);
		gStrPostId = gLngPostId.toString();
		gLngUserId = SessionHelper.getUserId(inputMap);
		gDtCurrDt = SessionHelper.getCurDate();
		gStrLocationCode = SessionHelper.getLocationCode(inputMap);
		gLngLocationCode = Long.parseLong(gStrLocationCode);
		gLngLangId = SessionHelper.getLangId(inputMap);
		gLngDbId = SessionHelper.getDbId(inputMap);
	}

	
	public ResultObject generateLoanBill(Map<String, Object> inputMap) {
		
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		LNABillDtlsDAO lObjBillDtlsDAO = new LNABillDtlsDAOImpl(null, serv.getSessionFactory());
		MstLnaBillDtls lObjLnaBillDtlsVO = new MstLnaBillDtls();
		
		try {
								
			LoginDetails lObjLoginVO = (LoginDetails) inputMap.get("lObjLoginVO");
			List<String> lLstEmpSevaarthId = (List<String>) inputMap.get("lLstEmpSevaarthId");
			List<String> lLstTransId = (List<String>) inputMap.get("lLstTransId");			
			Long lLngBillAmt = (Long) inputMap.get("billAmt");
			Long lLngAdvanceType = (Long) inputMap.get("advanceType");
			String lStrOrderNo = (String) inputMap.get("orderNo");
			
			Map baseLoginMap = new HashMap();
			
			baseLoginMap.put("userId", lObjLoginVO.getUser().getUserId());
			baseLoginMap.put("langId", lObjLoginVO.getLangId());;
			inputMap.put("baseLoginMap", baseLoginMap);
			
			String gStrLocCode = lObjLoginVO.getLocation().getLocationCode();
			Date lDtCurrDate = DBUtility.getCurrentDateFromDB();
			
			Long lLngBillDtlsId = IFMSCommonServiceImpl.getNextSeqNum("MST_LNA_BILL_DTLS", inputMap);
			
			lObjLnaBillDtlsVO.setBillDtlsId(lLngBillDtlsId);
			lObjLnaBillDtlsVO.setLocationCode(gStrLocCode);
			lObjLnaBillDtlsVO.setBillAmount(lLngBillAmt);
			lObjLnaBillDtlsVO.setBillGeneratedDate(lDtCurrDate);
			lObjLnaBillDtlsVO.setAdvacnceType(lLngAdvanceType);
			lObjLnaBillDtlsVO.setStatusFlag(1L);
			lObjLnaBillDtlsVO.setOrderNo(lStrOrderNo);
			lObjLnaBillDtlsVO.setOrderDate(lDtCurrDate);
			lObjLnaBillDtlsVO.setCreatedPostId(lObjLoginVO.getPrimaryPost().getPostId());
			lObjLnaBillDtlsVO.setCreatedUserId(lObjLoginVO.getUser().getUserId());
			lObjLnaBillDtlsVO.setCreatedDate(lDtCurrDate);
			
			lObjBillDtlsDAO.create(lObjLnaBillDtlsVO);
			
			if(lLngAdvanceType == 800028){
				lObjBillDtlsDAO.updateBillNoForCompAdv(lLstEmpSevaarthId, lLstTransId, lLngBillDtlsId);
			}else if(lLngAdvanceType == 800030){
				lObjBillDtlsDAO.updateBillNoForMotorAdv(lLstEmpSevaarthId, lLstTransId, lLngBillDtlsId);
			}else if(lLngAdvanceType  == 800029){
				lObjBillDtlsDAO.updateBillNoForHouseAdv(lLstEmpSevaarthId, lLstTransId, lLngBillDtlsId);
			}
			
			
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		
		inputMap.put("ajaxKey", "Success");
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;		
	}

	public ResultObject loadLoanBillDtls(Map<String, Object> inputMap) {
		
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);		
		try {
			setSessionInfo(inputMap);
			LNABillDtlsDAO lObjBillDtlsDAO = new LNABillDtlsDAOImpl(MstLnaBillDtls.class, serv.getSessionFactory());
			
			List<MstLnaBillDtls> lLstBillDtls = lObjBillDtlsDAO.getLoanBillDtls(gStrLocationCode);			
			inputMap.put("lLstBillDtls", lLstBillDtls);
			
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		resObj.setResultValue(inputMap);
		resObj.setViewName("LoanBillForm");
		return resObj;
	}
	
	public ResultObject viewLoanBill(Map<String, Object> inputMap) {
		
		
		return null;
	}
	
	public ResultObject approveLoanBill(Map<String, Object> inputMap) {
		
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = false;		
		SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		
		LNABillDtlsDAO lObjBillDtlsDAO = new LNABillDtlsDAOImpl(MstLnaBillDtls.class, serv.getSessionFactory());
		MstLnaBillDtls lObjLnaBillDtlsVO = new MstLnaBillDtls();
		HrLoanEmpDtls lObjLoanEmpDtlsVO = null;
		HrLoanEmpPrinRecoverDtls lObjEmpPrinRecoverDtlsVO = null;
		HrLoanEmpIntRecoverDtls lObjEmpIntRecoverDtlsVO = null;
		
		Long lLngPayrollPrinId = null;
		Long lLngPayrollIntId = null;
		List<MstLnaPayrollLoanTypeMpg> lLstPayrollLoanId = null;
		List<Object> lLstEmpLoanDtls = null;
		Long lLngLoanBillId = null;
		
		MstLnaPayrollLoanTypeMpg lObjTypeMpg = new MstLnaPayrollLoanTypeMpg();
		try {
			String lStrVoucherNo = StringUtility.getParameter("voucherNo", request);
			String lStrVoucherDate = StringUtility.getParameter("voucherDate", request);
			String lStrLoanBillId = StringUtility.getParameter("loanBillId", request);
			String lStrAdvanceType = StringUtility.getParameter("loanType", request);
			Date lDtVoucherDate = null;
			if(!"".equals(lStrLoanBillId)){
				
				lLngLoanBillId = Long.parseLong(lStrLoanBillId);
				lObjLnaBillDtlsVO = (MstLnaBillDtls) lObjBillDtlsDAO.read(lLngLoanBillId);
				lObjLnaBillDtlsVO.setVoucherNo(lStrVoucherNo);
				if(!"".equals(lStrVoucherDate)){
					lDtVoucherDate = lObjDateFormat.parse(lStrVoucherDate);
				}
				lObjLnaBillDtlsVO.setVoucherDate(lDtVoucherDate);
				lObjLnaBillDtlsVO.setStatusFlag(2L);
				lObjLnaBillDtlsVO.setUpdatedDate(gDtCurrDt);
				lObjLnaBillDtlsVO.setUpdatedPostId(gLngPostId);
				lObjLnaBillDtlsVO.setUpdatedUserId(gLngUserId);
				
				lObjBillDtlsDAO.update(lObjLnaBillDtlsVO);
				
				lLstPayrollLoanId = lObjBillDtlsDAO.getPayrollLoanId(Long.parseLong(lStrAdvanceType));
				
				if(!lLstPayrollLoanId.isEmpty()){
					lObjTypeMpg = lLstPayrollLoanId.get(0);
					lLngPayrollPrinId = lObjTypeMpg.getPayrollPriLoanId();
					lLngPayrollIntId = lObjTypeMpg.getPayrollIntLoanId();
				}
				gLogger.info("lObjTypeMpg "+lObjTypeMpg+" lStrLoanBillId" +lStrLoanBillId);
				if(lStrAdvanceType.equals("800028")){
					lLstEmpLoanDtls = lObjBillDtlsDAO.getEmpCompLoanDtls(lLngLoanBillId);
					gLogger.info("1");					
					Object[] lObjLoanDtls = null;
					Double lDbLoanPriAmt = 0D;
					Double lDbLoanPriInsNo = 0D;
					Double lDbLoanPriEmiAmt = 0D;				
					Double lDbLoanOddEmiAmt = 0D;
					if(!lLstEmpLoanDtls.isEmpty()){
						for(Integer lInt=0;lInt<lLstEmpLoanDtls.size();lInt++){
							gLogger.info("size "+lLstEmpLoanDtls.size());
							lObjLoanDtls = (Object[]) lLstEmpLoanDtls.get(lInt);
							Long lLngLoanEmpDtlsId = IFMSCommonServiceImpl.getNextSeqNum("HR_LOAN_EMP_DTLS", inputMap);
							gLogger.info("lLngLoanEmpDtlsId "+lLngLoanEmpDtlsId);
							EmpDAOImpl empDao = new EmpDAOImpl(OrgEmpMst.class, serv.getSessionFactory());
							GenericDaoHibernateImpl gImpl = new GenericDaoHibernateImpl(HrEisEmpMst.class);							
							gImpl.setSessionFactory(this.serv.getSessionFactory());
							
						    OrgEmpMst orgEmpMst = (OrgEmpMst)empDao.read(Long.parseLong(lObjLoanDtls[0].toString()));
						    orgEmpMst = empDao.getEngGujEmployee(orgEmpMst, gLngLangId);
						    
						    List hrEmpList = gImpl.getListByColumnAndValue("orgEmpMst", orgEmpMst);
						    HrEisEmpMst hrEmp= (HrEisEmpMst)hrEmpList.get(0);
																		    
						    HrLoanAdvMst hrLoanObj = new HrLoanAdvMst();

						    gImpl = new GenericDaoHibernateImpl(HrLoanAdvMst.class);
						    gImpl.setSessionFactory(serv.getSessionFactory());
						    hrLoanObj = (HrLoanAdvMst)gImpl.read(lLngPayrollPrinId);
						    
						    CommomnDataObjectFatch cmn = new CommomnDataObjectFatch(this.serv);
						    CmnDatabaseMst cmnDatabaseMst = cmn.getCmnDatabaseMst(gLngDbId);
						    CmnLocationMst cmnLocationMst = cmn.getCmnLocationMst(Long.parseLong(lObjLoanDtls[4].toString()));
						    OrgPostMst orgPostMst = cmn.getOrgPostMst(gLngPostId);
						    OrgUserMst orgUserMst = cmn.getorgUserMst(gLngUserId);
						    
						    lObjLoanEmpDtlsVO = new HrLoanEmpDtls();
						    
							lObjLoanEmpDtlsVO.setEmpLoanId(lLngLoanEmpDtlsId);
							lObjLoanEmpDtlsVO.setHrEisEmpMst(hrEmp);
							lObjLoanEmpDtlsVO.setHrLoanAdvMst(hrLoanObj);
							
							if(lObjLoanDtls[1] != null && lObjLoanDtls[1] != "")
								lDbLoanPriAmt = Double.parseDouble(lObjLoanDtls[1].toString());
							if(lObjLoanDtls[2] != null && lObjLoanDtls[2] != "")
								lDbLoanPriInsNo = Double.parseDouble(lObjLoanDtls[2].toString());
							if(lObjLoanDtls[5] != null && lObjLoanDtls[5] != "")
								lDbLoanPriEmiAmt = Double.parseDouble(lObjLoanDtls[5].toString());
							if(lObjLoanDtls[8] != null && lObjLoanDtls[8] != "")
								lDbLoanOddEmiAmt = Double.parseDouble(lObjLoanDtls[8].toString());
							
							
							lObjLoanEmpDtlsVO.setLoanPrinAmt(lDbLoanPriAmt.longValue());
							lObjLoanEmpDtlsVO.setLoanInterestAmt(0L);
							lObjLoanEmpDtlsVO.setLoanPrinInstNo(lDbLoanPriInsNo.longValue());
							lObjLoanEmpDtlsVO.setLoanIntInstNo(0L);
							lObjLoanEmpDtlsVO.setLoanDate((Date)lObjLoanDtls[3]);
							lObjLoanEmpDtlsVO.setCmnDatabaseMst(cmnDatabaseMst);
							lObjLoanEmpDtlsVO.setCmnLocationMst(cmnLocationMst);							
							lObjLoanEmpDtlsVO.setOrgPostMstByCreatedByPost(orgPostMst);
							lObjLoanEmpDtlsVO.setOrgUserMstByCreatedBy(orgUserMst);
							lObjLoanEmpDtlsVO.setCreatedDate(gDtCurrDt);
							lObjLoanEmpDtlsVO.setTrnCounter(new Integer(1));
							lObjLoanEmpDtlsVO.setLoanIntEmiAmt(0L);
							lObjLoanEmpDtlsVO.setLoanPrinEmiAmt(lDbLoanPriEmiAmt.longValue());
							lObjLoanEmpDtlsVO.setLoanSancOrderNo(lObjLoanDtls[6].toString());
							lObjLoanEmpDtlsVO.setLoanActivateFlag(new Integer(1));
							if(lObjLoanDtls[7] != null && lObjLoanDtls[7] != ""){
								if(lObjLoanDtls[7].equals(800056D)){
									lObjLoanEmpDtlsVO.setLoanOddinstno(1L);
									lObjLoanEmpDtlsVO.setLoanOddinstAmt(lDbLoanOddEmiAmt.longValue());
								}else if(lObjLoanDtls[7].equals(800057D)){
									lObjLoanEmpDtlsVO.setLoanOddinstno(lDbLoanPriInsNo.longValue());
									lObjLoanEmpDtlsVO.setLoanOddinstAmt(lDbLoanOddEmiAmt.longValue());
								}
							}else{
								lObjLoanEmpDtlsVO.setLoanOddinstno(0L);
								lObjLoanEmpDtlsVO.setLoanOddinstAmt(0L);
							}
							lObjLoanEmpDtlsVO.setIsApproved(0);
							lObjLoanEmpDtlsVO.setVoucherNo(lStrVoucherNo);
							lObjLoanEmpDtlsVO.setVoucherDate(lDtVoucherDate);
							lObjLoanEmpDtlsVO.setLoanSancOrderdate((Date) lObjLoanDtls[11]);
							lObjLoanEmpDtlsVO.setMulLoanRecoveryMode(0);
							lObjLoanEmpDtlsVO.setMulLoanInstRecvd(1);
							lObjLoanEmpDtlsVO.setMulLoanAmtRecvd(Long.valueOf(0L));
							
							lObjBillDtlsDAO.create(lObjLoanEmpDtlsVO);
							
							lObjEmpPrinRecoverDtlsVO = new HrLoanEmpPrinRecoverDtls();
							Long lLngLoanEmpPriRecDtlsId = IFMSCommonServiceImpl.getNextSeqNum("HR_LOAN_EMP_PRIN_RECOVER_DTLS", inputMap);
							
						    LoanEmpPrinRecvDAOImpl loanEmpPrinRecvDAO = new LoanEmpPrinRecvDAOImpl(HrLoanEmpPrinRecoverDtls.class, this.serv.getSessionFactory());
						    lObjEmpPrinRecoverDtlsVO.setPrinRecoverId(lLngLoanEmpPriRecDtlsId);
						    lObjEmpPrinRecoverDtlsVO.setHrLoanEmpDtls(lObjLoanEmpDtlsVO);
						    lObjEmpPrinRecoverDtlsVO.setCmnDatabaseMst(cmnDatabaseMst);
						    lObjEmpPrinRecoverDtlsVO.setOrgPostMstByUpdatedByPost(orgPostMst);
						    lObjEmpPrinRecoverDtlsVO.setOrgPostMstByCreatedByPost(orgPostMst);
						    lObjEmpPrinRecoverDtlsVO.setCmnLocationMst(cmnLocationMst);
						    lObjEmpPrinRecoverDtlsVO.setOrgUserMstByUpdatedBy(orgUserMst);
						    lObjEmpPrinRecoverDtlsVO.setOrgUserMstByCreatedBy(orgUserMst);
						    lObjEmpPrinRecoverDtlsVO.setCreatedDate(gDtCurrDt);
						    lObjEmpPrinRecoverDtlsVO.setTrnCounter(new Integer(1));
						    
						    loanEmpPrinRecvDAO.create(lObjEmpPrinRecoverDtlsVO);
						    
						    lObjEmpIntRecoverDtlsVO = new HrLoanEmpIntRecoverDtls();
						    Long lLngLoanEmpIntRecDtlsId = IFMSCommonServiceImpl.getNextSeqNum("HR_LOAN_EMP_INT_RECOVER_DTLS", inputMap);
						    LoanEmpIntRecvDAOImpl loanEmpIntRecvDAO = new LoanEmpIntRecvDAOImpl(HrLoanEmpIntRecoverDtls.class, this.serv.getSessionFactory());
						    lObjEmpIntRecoverDtlsVO.setIntRecoverId(lLngLoanEmpIntRecDtlsId);
						    lObjEmpIntRecoverDtlsVO.setHrLoanEmpDtls(lObjLoanEmpDtlsVO);
						    lObjEmpIntRecoverDtlsVO.setCmnDatabaseMst(cmnDatabaseMst);
						    lObjEmpIntRecoverDtlsVO.setOrgPostMstByUpdatedByPost(orgPostMst);
						    lObjEmpIntRecoverDtlsVO.setOrgPostMstByCreatedByPost(orgPostMst);
						    lObjEmpIntRecoverDtlsVO.setCmnLocationMst(cmnLocationMst);
						    lObjEmpIntRecoverDtlsVO.setOrgUserMstByUpdatedBy(orgUserMst);
						    lObjEmpIntRecoverDtlsVO.setOrgUserMstByCreatedBy(orgUserMst);
						    lObjEmpIntRecoverDtlsVO.setCreatedDate(gDtCurrDt);
						    lObjEmpIntRecoverDtlsVO.setTrnCounter(new Integer(1));

						    loanEmpIntRecvDAO.create(lObjEmpIntRecoverDtlsVO);
						    gLogger.info("done..lets out..");
						}						
					}
				}else if(lStrAdvanceType.equals("800029")){
					lLstEmpLoanDtls = lObjBillDtlsDAO.getEmpHouseLoanDtls(lLngLoanBillId);
					
					Object[] lObjLoanDtls = null;
					Double lDbLoanPriAmt = 0D;
					Double lDbLoanPriInsNo = 0D;
					Double lDbLoanPriEmiAmt = 0D;				
					Double lDbLoanPriOddEmiAmt = 0D;
					Double lDbLoanIntAmt = 0D;
					Double lDbLoanIntInsNo = 0D;
					Double lDbLoanIntEmiAmt = 0D;				
					Double lDbLoanIntOddEmiAmt = 0D;
					Long lLngLoanEmpDtlsId = null;
					HrLoanAdvMst hrLoanObj = null;
					HrEisEmpMst hrEmp = null; 
					Long lLngLoanEmpPriRecDtlsId = null;
					Long lLngLoanEmpIntRecDtlsId = null;
					if(!lLstEmpLoanDtls.isEmpty()){
						for(Integer lInt=0;lInt<lLstEmpLoanDtls.size();lInt++){
							lObjLoanDtls = (Object[]) lLstEmpLoanDtls.get(lInt);							
							
							EmpDAOImpl empDao = new EmpDAOImpl(OrgEmpMst.class, serv.getSessionFactory());
							GenericDaoHibernateImpl gImpl = new GenericDaoHibernateImpl(HrEisEmpMst.class);							
							gImpl.setSessionFactory(this.serv.getSessionFactory());
							
							
						    OrgEmpMst orgEmpMst = (OrgEmpMst)empDao.read(Long.parseLong(lObjLoanDtls[0].toString()));
						    orgEmpMst = empDao.getEngGujEmployee(orgEmpMst, gLngLangId);
						    
						    List hrEmpList = gImpl.getListByColumnAndValue("orgEmpMst", orgEmpMst);
						    hrEmp= (HrEisEmpMst)hrEmpList.get(0);
																		    
						    hrLoanObj = new HrLoanAdvMst();

						    gImpl = new GenericDaoHibernateImpl(HrLoanAdvMst.class);
						    gImpl.setSessionFactory(serv.getSessionFactory());
						    hrLoanObj = (HrLoanAdvMst)gImpl.read(lLngPayrollPrinId);
						    
						    CommomnDataObjectFatch cmn = new CommomnDataObjectFatch(this.serv);
						    CmnDatabaseMst cmnDatabaseMst = cmn.getCmnDatabaseMst(gLngDbId);
						    CmnLocationMst cmnLocationMst = cmn.getCmnLocationMst(Long.parseLong(lObjLoanDtls[6].toString()));
						    OrgPostMst orgPostMst = cmn.getOrgPostMst(gLngPostId);
						    OrgUserMst orgUserMst = cmn.getorgUserMst(gLngUserId);
						    
						    lLngLoanEmpDtlsId = IFMSCommonServiceImpl.getNextSeqNum("HR_LOAN_EMP_DTLS", inputMap);
						    
						    lObjLoanEmpDtlsVO = new HrLoanEmpDtls();						    
						    
							lObjLoanEmpDtlsVO.setEmpLoanId(lLngLoanEmpDtlsId);
							lObjLoanEmpDtlsVO.setHrEisEmpMst(hrEmp);
							lObjLoanEmpDtlsVO.setHrLoanAdvMst(hrLoanObj);
							
							
							if(lObjLoanDtls[1] != null && lObjLoanDtls[1] != "" && (lObjLoanDtls[17].toString().equals("800058") 
												||  lObjLoanDtls[17].toString().equals("800038")))
								lDbLoanPriAmt = Double.parseDouble(lObjLoanDtls[18].toString());
							else if(lObjLoanDtls[1] != null && lObjLoanDtls[1] != "")
								lDbLoanPriAmt = Double.parseDouble(lObjLoanDtls[1].toString());
							if(lObjLoanDtls[3] != null && lObjLoanDtls[3] != "")
								lDbLoanPriInsNo = Double.parseDouble(lObjLoanDtls[3].toString());
							if(lObjLoanDtls[7] != null && lObjLoanDtls[7] != "")
								lDbLoanPriEmiAmt = Double.parseDouble(lObjLoanDtls[7].toString());
							if(lObjLoanDtls[11] != null && lObjLoanDtls[11] != "")
								lDbLoanPriOddEmiAmt = Double.parseDouble(lObjLoanDtls[11].toString());
							
							
							lObjLoanEmpDtlsVO.setLoanPrinAmt(lDbLoanPriAmt.longValue());
							lObjLoanEmpDtlsVO.setLoanInterestAmt(0L);
							lObjLoanEmpDtlsVO.setLoanPrinInstNo(lDbLoanPriInsNo.longValue());
							lObjLoanEmpDtlsVO.setLoanIntInstNo(0L);
							lObjLoanEmpDtlsVO.setLoanDate((Date)lObjLoanDtls[5]);
							lObjLoanEmpDtlsVO.setCmnDatabaseMst(cmnDatabaseMst);
							lObjLoanEmpDtlsVO.setCmnLocationMst(cmnLocationMst);							
							lObjLoanEmpDtlsVO.setOrgPostMstByCreatedByPost(orgPostMst);
							lObjLoanEmpDtlsVO.setOrgUserMstByCreatedBy(orgUserMst);
							lObjLoanEmpDtlsVO.setCreatedDate(gDtCurrDt);
							lObjLoanEmpDtlsVO.setTrnCounter(new Integer(1));
							lObjLoanEmpDtlsVO.setLoanIntEmiAmt(0L);
							lObjLoanEmpDtlsVO.setLoanPrinEmiAmt(lDbLoanPriEmiAmt.longValue());
							lObjLoanEmpDtlsVO.setLoanSancOrderNo(lObjLoanDtls[9].toString());
							lObjLoanEmpDtlsVO.setLoanActivateFlag(new Integer(1));
							if(lObjLoanDtls[10] != null && lObjLoanDtls[10] != ""){
								if(lObjLoanDtls[10].equals(800056D)){
									lObjLoanEmpDtlsVO.setLoanOddinstno(1L);
									lObjLoanEmpDtlsVO.setLoanOddinstAmt(lDbLoanPriOddEmiAmt.longValue());
								}else if(lObjLoanDtls[10].equals(800057D)){
									lObjLoanEmpDtlsVO.setLoanOddinstno(lDbLoanPriInsNo.longValue());
									lObjLoanEmpDtlsVO.setLoanOddinstAmt(lDbLoanPriOddEmiAmt.longValue());
								}
							}else{
								lObjLoanEmpDtlsVO.setLoanOddinstno(0L);
								lObjLoanEmpDtlsVO.setLoanOddinstAmt(0L);
							}
							lObjLoanEmpDtlsVO.setIsApproved(0);
							lObjLoanEmpDtlsVO.setVoucherNo(lStrVoucherNo);
							lObjLoanEmpDtlsVO.setVoucherDate(lDtVoucherDate);
							lObjLoanEmpDtlsVO.setLoanSancOrderdate((Date) lObjLoanDtls[16]);
							lObjLoanEmpDtlsVO.setMulLoanRecoveryMode(0);
							lObjLoanEmpDtlsVO.setMulLoanInstRecvd(1);
							lObjLoanEmpDtlsVO.setMulLoanAmtRecvd(Long.valueOf(0L));
							
							lObjBillDtlsDAO.create(lObjLoanEmpDtlsVO);
							
							lObjEmpPrinRecoverDtlsVO = new HrLoanEmpPrinRecoverDtls();
							lLngLoanEmpPriRecDtlsId = IFMSCommonServiceImpl.getNextSeqNum("HR_LOAN_EMP_PRIN_RECOVER_DTLS", inputMap);
							
						    LoanEmpPrinRecvDAOImpl loanEmpPrinRecvDAO = new LoanEmpPrinRecvDAOImpl(HrLoanEmpPrinRecoverDtls.class, this.serv.getSessionFactory());
						    lObjEmpPrinRecoverDtlsVO.setPrinRecoverId(lLngLoanEmpPriRecDtlsId);
						    lObjEmpPrinRecoverDtlsVO.setHrLoanEmpDtls(lObjLoanEmpDtlsVO);
						    lObjEmpPrinRecoverDtlsVO.setCmnDatabaseMst(cmnDatabaseMst);
						    lObjEmpPrinRecoverDtlsVO.setOrgPostMstByUpdatedByPost(orgPostMst);
						    lObjEmpPrinRecoverDtlsVO.setOrgPostMstByCreatedByPost(orgPostMst);
						    lObjEmpPrinRecoverDtlsVO.setCmnLocationMst(cmnLocationMst);
						    lObjEmpPrinRecoverDtlsVO.setOrgUserMstByUpdatedBy(orgUserMst);
						    lObjEmpPrinRecoverDtlsVO.setOrgUserMstByCreatedBy(orgUserMst);
						    lObjEmpPrinRecoverDtlsVO.setCreatedDate(gDtCurrDt);
						    lObjEmpPrinRecoverDtlsVO.setTrnCounter(new Integer(1));
						    
						    loanEmpPrinRecvDAO.create(lObjEmpPrinRecoverDtlsVO);
						    
						    lObjEmpIntRecoverDtlsVO = new HrLoanEmpIntRecoverDtls();
						    
						    lLngLoanEmpIntRecDtlsId = IFMSCommonServiceImpl.getNextSeqNum("HR_LOAN_EMP_INT_RECOVER_DTLS", inputMap);
						    
						    LoanEmpIntRecvDAOImpl loanEmpIntRecvDAO = new LoanEmpIntRecvDAOImpl(HrLoanEmpIntRecoverDtls.class, this.serv.getSessionFactory());
						    lObjEmpIntRecoverDtlsVO.setIntRecoverId(lLngLoanEmpIntRecDtlsId);
						    lObjEmpIntRecoverDtlsVO.setHrLoanEmpDtls(lObjLoanEmpDtlsVO);
						    lObjEmpIntRecoverDtlsVO.setCmnDatabaseMst(cmnDatabaseMst);
						    lObjEmpIntRecoverDtlsVO.setOrgPostMstByUpdatedByPost(orgPostMst);
						    lObjEmpIntRecoverDtlsVO.setOrgPostMstByCreatedByPost(orgPostMst);
						    lObjEmpIntRecoverDtlsVO.setCmnLocationMst(cmnLocationMst);
						    lObjEmpIntRecoverDtlsVO.setOrgUserMstByUpdatedBy(orgUserMst);
						    lObjEmpIntRecoverDtlsVO.setOrgUserMstByCreatedBy(orgUserMst);
						    lObjEmpIntRecoverDtlsVO.setCreatedDate(gDtCurrDt);
						    lObjEmpIntRecoverDtlsVO.setTrnCounter(new Integer(1));

						    loanEmpIntRecvDAO.create(lObjEmpIntRecoverDtlsVO);
						    
						    // Interest Loan Entry
						    hrLoanObj = new HrLoanAdvMst();

						    gImpl = new GenericDaoHibernateImpl(HrLoanAdvMst.class);
						    gImpl.setSessionFactory(serv.getSessionFactory());
						    hrLoanObj = (HrLoanAdvMst)gImpl.read(lLngPayrollIntId);						    
						    
						    lLngLoanEmpDtlsId = IFMSCommonServiceImpl.getNextSeqNum("HR_LOAN_EMP_DTLS", inputMap);
						    
						    lObjLoanEmpDtlsVO = new HrLoanEmpDtls();						    
						    
							lObjLoanEmpDtlsVO.setEmpLoanId(lLngLoanEmpDtlsId);
							lObjLoanEmpDtlsVO.setHrEisEmpMst(hrEmp);
							lObjLoanEmpDtlsVO.setHrLoanAdvMst(hrLoanObj);
							
							if(lObjLoanDtls[2] != null && lObjLoanDtls[2] != "")
								lDbLoanIntAmt = Double.parseDouble(lObjLoanDtls[2].toString());
							if(lObjLoanDtls[4] != null && lObjLoanDtls[4] != "")
								lDbLoanIntInsNo = Double.parseDouble(lObjLoanDtls[4].toString());
							if(lObjLoanDtls[8] != null && lObjLoanDtls[8] != "")
								lDbLoanIntEmiAmt = Double.parseDouble(lObjLoanDtls[8].toString());
							if(lObjLoanDtls[13] != null && lObjLoanDtls[13] != "")
								lDbLoanIntOddEmiAmt = Double.parseDouble(lObjLoanDtls[13].toString());
							
							
							lObjLoanEmpDtlsVO.setLoanPrinAmt(0L);
							lObjLoanEmpDtlsVO.setLoanInterestAmt(lDbLoanIntAmt.longValue());
							lObjLoanEmpDtlsVO.setLoanPrinInstNo(0L);
							lObjLoanEmpDtlsVO.setLoanIntInstNo(lDbLoanIntInsNo.longValue());
							lObjLoanEmpDtlsVO.setLoanDate((Date)lObjLoanDtls[5]);
							lObjLoanEmpDtlsVO.setCmnDatabaseMst(cmnDatabaseMst);
							lObjLoanEmpDtlsVO.setCmnLocationMst(cmnLocationMst);							
							lObjLoanEmpDtlsVO.setOrgPostMstByCreatedByPost(orgPostMst);
							lObjLoanEmpDtlsVO.setOrgUserMstByCreatedBy(orgUserMst);
							lObjLoanEmpDtlsVO.setCreatedDate(gDtCurrDt);
							lObjLoanEmpDtlsVO.setTrnCounter(new Integer(1));
							lObjLoanEmpDtlsVO.setLoanIntEmiAmt(lDbLoanIntEmiAmt.longValue());
							lObjLoanEmpDtlsVO.setLoanPrinEmiAmt(0L);
							lObjLoanEmpDtlsVO.setLoanSancOrderNo(lObjLoanDtls[9].toString());
							lObjLoanEmpDtlsVO.setLoanActivateFlag(new Integer(2));
							if(lObjLoanDtls[12] != null && lObjLoanDtls[12] != ""){
								if(lObjLoanDtls[12].equals(800056D)){
									lObjLoanEmpDtlsVO.setLoanOddinstno(1L);
									lObjLoanEmpDtlsVO.setLoanOddinstAmt(lDbLoanIntOddEmiAmt.longValue());
								}else if(lObjLoanDtls[12].equals(800057D)){
									lObjLoanEmpDtlsVO.setLoanOddinstno(lDbLoanPriInsNo.longValue());
									lObjLoanEmpDtlsVO.setLoanOddinstAmt(lDbLoanIntOddEmiAmt.longValue());
								}
							}else{
								lObjLoanEmpDtlsVO.setLoanOddinstno(0L);
								lObjLoanEmpDtlsVO.setLoanOddinstAmt(0L);
							}
							lObjLoanEmpDtlsVO.setIsApproved(0);
							lObjLoanEmpDtlsVO.setVoucherNo(lStrVoucherNo);
							lObjLoanEmpDtlsVO.setVoucherDate(lDtVoucherDate);
							lObjLoanEmpDtlsVO.setLoanSancOrderdate((Date) lObjLoanDtls[16]);
							lObjLoanEmpDtlsVO.setMulLoanRecoveryMode(0);
							lObjLoanEmpDtlsVO.setMulLoanInstRecvd(1);
							lObjLoanEmpDtlsVO.setMulLoanAmtRecvd(Long.valueOf(0L));
							
							lObjBillDtlsDAO.create(lObjLoanEmpDtlsVO);
						    
							lObjEmpPrinRecoverDtlsVO = new HrLoanEmpPrinRecoverDtls();
							lLngLoanEmpPriRecDtlsId = IFMSCommonServiceImpl.getNextSeqNum("HR_LOAN_EMP_PRIN_RECOVER_DTLS", inputMap);							
						  
						    lObjEmpPrinRecoverDtlsVO.setPrinRecoverId(lLngLoanEmpPriRecDtlsId);
						    lObjEmpPrinRecoverDtlsVO.setHrLoanEmpDtls(lObjLoanEmpDtlsVO);
						    lObjEmpPrinRecoverDtlsVO.setCmnDatabaseMst(cmnDatabaseMst);
						    lObjEmpPrinRecoverDtlsVO.setOrgPostMstByUpdatedByPost(orgPostMst);
						    lObjEmpPrinRecoverDtlsVO.setOrgPostMstByCreatedByPost(orgPostMst);
						    lObjEmpPrinRecoverDtlsVO.setCmnLocationMst(cmnLocationMst);
						    lObjEmpPrinRecoverDtlsVO.setOrgUserMstByUpdatedBy(orgUserMst);
						    lObjEmpPrinRecoverDtlsVO.setOrgUserMstByCreatedBy(orgUserMst);
						    lObjEmpPrinRecoverDtlsVO.setCreatedDate(gDtCurrDt);
						    lObjEmpPrinRecoverDtlsVO.setTrnCounter(new Integer(1));
						    
						    loanEmpPrinRecvDAO.create(lObjEmpPrinRecoverDtlsVO);
						    
						    lObjEmpIntRecoverDtlsVO = new HrLoanEmpIntRecoverDtls();
						    
						    lLngLoanEmpIntRecDtlsId = IFMSCommonServiceImpl.getNextSeqNum("HR_LOAN_EMP_INT_RECOVER_DTLS", inputMap);						    
						    
						    lObjEmpIntRecoverDtlsVO.setIntRecoverId(lLngLoanEmpIntRecDtlsId);
						    lObjEmpIntRecoverDtlsVO.setHrLoanEmpDtls(lObjLoanEmpDtlsVO);
						    lObjEmpIntRecoverDtlsVO.setCmnDatabaseMst(cmnDatabaseMst);
						    lObjEmpIntRecoverDtlsVO.setOrgPostMstByUpdatedByPost(orgPostMst);
						    lObjEmpIntRecoverDtlsVO.setOrgPostMstByCreatedByPost(orgPostMst);
						    lObjEmpIntRecoverDtlsVO.setCmnLocationMst(cmnLocationMst);
						    lObjEmpIntRecoverDtlsVO.setOrgUserMstByUpdatedBy(orgUserMst);
						    lObjEmpIntRecoverDtlsVO.setOrgUserMstByCreatedBy(orgUserMst);
						    lObjEmpIntRecoverDtlsVO.setCreatedDate(gDtCurrDt);
						    lObjEmpIntRecoverDtlsVO.setTrnCounter(new Integer(1));

						    loanEmpIntRecvDAO.create(lObjEmpIntRecoverDtlsVO);
						}
					}					
				}else if(lStrAdvanceType.equals("800030")){
					lLstEmpLoanDtls = lObjBillDtlsDAO.getEmpMotorLoanDtls(lLngLoanBillId);
					
					Object[] lObjLoanDtls = null;
					Double lDbLoanPriAmt = 0D;
					Double lDbLoanPriInsNo = 0D;
					Double lDbLoanPriEmiAmt = 0D;				
					Double lDbLoanPriOddEmiAmt = 0D;
					Double lDbLoanIntAmt = 0D;
					Double lDbLoanIntInsNo = 0D;
					Double lDbLoanIntEmiAmt = 0D;				
					Double lDbLoanIntOddEmiAmt = 0D;
					Long lLngLoanEmpDtlsId = null;
					HrLoanAdvMst hrLoanObj = null;
					HrEisEmpMst hrEmp = null; 
					Long lLngLoanEmpPriRecDtlsId = null;
					Long lLngLoanEmpIntRecDtlsId = null;
					if(!lLstEmpLoanDtls.isEmpty()){
						for(Integer lInt=0;lInt<lLstEmpLoanDtls.size();lInt++){
							lObjLoanDtls = (Object[]) lLstEmpLoanDtls.get(lInt);							
							
							EmpDAOImpl empDao = new EmpDAOImpl(OrgEmpMst.class, serv.getSessionFactory());
							GenericDaoHibernateImpl gImpl = new GenericDaoHibernateImpl(HrEisEmpMst.class);							
							gImpl.setSessionFactory(this.serv.getSessionFactory());
							
							
						    OrgEmpMst orgEmpMst = (OrgEmpMst)empDao.read(Long.parseLong(lObjLoanDtls[0].toString()));
						    orgEmpMst = empDao.getEngGujEmployee(orgEmpMst, gLngLangId);
						    
						    List hrEmpList = gImpl.getListByColumnAndValue("orgEmpMst", orgEmpMst);
						    hrEmp= (HrEisEmpMst)hrEmpList.get(0);
																		    
						    hrLoanObj = new HrLoanAdvMst();

						    gImpl = new GenericDaoHibernateImpl(HrLoanAdvMst.class);
						    gImpl.setSessionFactory(serv.getSessionFactory());
						    hrLoanObj = (HrLoanAdvMst)gImpl.read(lLngPayrollPrinId);
						    
						    CommomnDataObjectFatch cmn = new CommomnDataObjectFatch(this.serv);
						    CmnDatabaseMst cmnDatabaseMst = cmn.getCmnDatabaseMst(gLngDbId);
						    CmnLocationMst cmnLocationMst = cmn.getCmnLocationMst(Long.parseLong(lObjLoanDtls[6].toString()));
						    OrgPostMst orgPostMst = cmn.getOrgPostMst(gLngPostId);
						    OrgUserMst orgUserMst = cmn.getorgUserMst(gLngUserId);
						    
						    lLngLoanEmpDtlsId = IFMSCommonServiceImpl.getNextSeqNum("HR_LOAN_EMP_DTLS", inputMap);
						    
						    lObjLoanEmpDtlsVO = new HrLoanEmpDtls();						    
						    
							lObjLoanEmpDtlsVO.setEmpLoanId(lLngLoanEmpDtlsId);
							lObjLoanEmpDtlsVO.setHrEisEmpMst(hrEmp);
							lObjLoanEmpDtlsVO.setHrLoanAdvMst(hrLoanObj);
							
							if(lObjLoanDtls[1] != null && lObjLoanDtls[1] != "")
								lDbLoanPriAmt = Double.parseDouble(lObjLoanDtls[1].toString());
							if(lObjLoanDtls[3] != null && lObjLoanDtls[3] != "")
								lDbLoanPriInsNo = Double.parseDouble(lObjLoanDtls[3].toString());
							if(lObjLoanDtls[7] != null && lObjLoanDtls[7] != "")
								lDbLoanPriEmiAmt = Double.parseDouble(lObjLoanDtls[7].toString());
							if(lObjLoanDtls[11] != null && lObjLoanDtls[11] != "")
								lDbLoanPriOddEmiAmt = Double.parseDouble(lObjLoanDtls[11].toString());
							
							
							lObjLoanEmpDtlsVO.setLoanPrinAmt(lDbLoanPriAmt.longValue());
							lObjLoanEmpDtlsVO.setLoanInterestAmt(0L);
							lObjLoanEmpDtlsVO.setLoanPrinInstNo(lDbLoanPriInsNo.longValue());
							lObjLoanEmpDtlsVO.setLoanIntInstNo(0L);
							lObjLoanEmpDtlsVO.setLoanDate((Date)lObjLoanDtls[5]);
							lObjLoanEmpDtlsVO.setCmnDatabaseMst(cmnDatabaseMst);
							lObjLoanEmpDtlsVO.setCmnLocationMst(cmnLocationMst);							
							lObjLoanEmpDtlsVO.setOrgPostMstByCreatedByPost(orgPostMst);
							lObjLoanEmpDtlsVO.setOrgUserMstByCreatedBy(orgUserMst);
							lObjLoanEmpDtlsVO.setCreatedDate(gDtCurrDt);
							lObjLoanEmpDtlsVO.setTrnCounter(new Integer(1));
							lObjLoanEmpDtlsVO.setLoanIntEmiAmt(0L);
							lObjLoanEmpDtlsVO.setLoanPrinEmiAmt(lDbLoanPriEmiAmt.longValue());
							lObjLoanEmpDtlsVO.setLoanSancOrderNo(lObjLoanDtls[9].toString());
							lObjLoanEmpDtlsVO.setLoanActivateFlag(new Integer(1));
							if(lObjLoanDtls[10] != null && lObjLoanDtls[10] != ""){
								if(lObjLoanDtls[10].equals(800056D)){
									lObjLoanEmpDtlsVO.setLoanOddinstno(1L);
									lObjLoanEmpDtlsVO.setLoanOddinstAmt(lDbLoanPriOddEmiAmt.longValue());
								}else if(lObjLoanDtls[10].equals(800057D)){
									lObjLoanEmpDtlsVO.setLoanOddinstno(lDbLoanPriInsNo.longValue());
									lObjLoanEmpDtlsVO.setLoanOddinstAmt(lDbLoanPriOddEmiAmt.longValue());
								}
							}else{
								lObjLoanEmpDtlsVO.setLoanOddinstno(0L);
								lObjLoanEmpDtlsVO.setLoanOddinstAmt(0L);
							}
							lObjLoanEmpDtlsVO.setIsApproved(0);
							lObjLoanEmpDtlsVO.setVoucherNo(lStrVoucherNo);
							lObjLoanEmpDtlsVO.setVoucherDate(lDtVoucherDate);
							lObjLoanEmpDtlsVO.setLoanSancOrderdate((Date) lObjLoanDtls[16]);
							lObjLoanEmpDtlsVO.setMulLoanRecoveryMode(0);
							lObjLoanEmpDtlsVO.setMulLoanInstRecvd(1);
							lObjLoanEmpDtlsVO.setMulLoanAmtRecvd(Long.valueOf(0L));
							
							lObjBillDtlsDAO.create(lObjLoanEmpDtlsVO);
							
							lObjEmpPrinRecoverDtlsVO = new HrLoanEmpPrinRecoverDtls();
							lLngLoanEmpPriRecDtlsId = IFMSCommonServiceImpl.getNextSeqNum("HR_LOAN_EMP_PRIN_RECOVER_DTLS", inputMap);
							
						    LoanEmpPrinRecvDAOImpl loanEmpPrinRecvDAO = new LoanEmpPrinRecvDAOImpl(HrLoanEmpPrinRecoverDtls.class, this.serv.getSessionFactory());
						    lObjEmpPrinRecoverDtlsVO.setPrinRecoverId(lLngLoanEmpPriRecDtlsId);
						    lObjEmpPrinRecoverDtlsVO.setHrLoanEmpDtls(lObjLoanEmpDtlsVO);
						    lObjEmpPrinRecoverDtlsVO.setCmnDatabaseMst(cmnDatabaseMst);
						    lObjEmpPrinRecoverDtlsVO.setOrgPostMstByUpdatedByPost(orgPostMst);
						    lObjEmpPrinRecoverDtlsVO.setOrgPostMstByCreatedByPost(orgPostMst);
						    lObjEmpPrinRecoverDtlsVO.setCmnLocationMst(cmnLocationMst);
						    lObjEmpPrinRecoverDtlsVO.setOrgUserMstByUpdatedBy(orgUserMst);
						    lObjEmpPrinRecoverDtlsVO.setOrgUserMstByCreatedBy(orgUserMst);
						    lObjEmpPrinRecoverDtlsVO.setCreatedDate(gDtCurrDt);
						    lObjEmpPrinRecoverDtlsVO.setTrnCounter(new Integer(1));
						    
						    loanEmpPrinRecvDAO.create(lObjEmpPrinRecoverDtlsVO);
						    
						    lObjEmpIntRecoverDtlsVO = new HrLoanEmpIntRecoverDtls();
						    
						    lLngLoanEmpIntRecDtlsId = IFMSCommonServiceImpl.getNextSeqNum("HR_LOAN_EMP_INT_RECOVER_DTLS", inputMap);
						    
						    LoanEmpIntRecvDAOImpl loanEmpIntRecvDAO = new LoanEmpIntRecvDAOImpl(HrLoanEmpIntRecoverDtls.class, this.serv.getSessionFactory());
						    lObjEmpIntRecoverDtlsVO.setIntRecoverId(lLngLoanEmpIntRecDtlsId);
						    lObjEmpIntRecoverDtlsVO.setHrLoanEmpDtls(lObjLoanEmpDtlsVO);
						    lObjEmpIntRecoverDtlsVO.setCmnDatabaseMst(cmnDatabaseMst);
						    lObjEmpIntRecoverDtlsVO.setOrgPostMstByUpdatedByPost(orgPostMst);
						    lObjEmpIntRecoverDtlsVO.setOrgPostMstByCreatedByPost(orgPostMst);
						    lObjEmpIntRecoverDtlsVO.setCmnLocationMst(cmnLocationMst);
						    lObjEmpIntRecoverDtlsVO.setOrgUserMstByUpdatedBy(orgUserMst);
						    lObjEmpIntRecoverDtlsVO.setOrgUserMstByCreatedBy(orgUserMst);
						    lObjEmpIntRecoverDtlsVO.setCreatedDate(gDtCurrDt);
						    lObjEmpIntRecoverDtlsVO.setTrnCounter(new Integer(1));

						    loanEmpIntRecvDAO.create(lObjEmpIntRecoverDtlsVO);
						    
						    // Interest Loan Entry
						    hrLoanObj = new HrLoanAdvMst();

						    gImpl = new GenericDaoHibernateImpl(HrLoanAdvMst.class);
						    gImpl.setSessionFactory(serv.getSessionFactory());
						    hrLoanObj = (HrLoanAdvMst)gImpl.read(lLngPayrollIntId);						    
						    
						    lLngLoanEmpDtlsId = IFMSCommonServiceImpl.getNextSeqNum("HR_LOAN_EMP_DTLS", inputMap);
						    
						    lObjLoanEmpDtlsVO = new HrLoanEmpDtls();						    
						    
							lObjLoanEmpDtlsVO.setEmpLoanId(lLngLoanEmpDtlsId);
							lObjLoanEmpDtlsVO.setHrEisEmpMst(hrEmp);
							lObjLoanEmpDtlsVO.setHrLoanAdvMst(hrLoanObj);
							
							if(lObjLoanDtls[2] != null && lObjLoanDtls[2] != "")
								lDbLoanIntAmt = Double.parseDouble(lObjLoanDtls[2].toString());
							if(lObjLoanDtls[4] != null && lObjLoanDtls[4] != "")
								lDbLoanIntInsNo = Double.parseDouble(lObjLoanDtls[4].toString());
							if(lObjLoanDtls[8] != null && lObjLoanDtls[8] != "")
								lDbLoanIntEmiAmt = Double.parseDouble(lObjLoanDtls[8].toString());
							if(lObjLoanDtls[13] != null && lObjLoanDtls[13] != "")
								lDbLoanIntOddEmiAmt = Double.parseDouble(lObjLoanDtls[13].toString());
							
							
							lObjLoanEmpDtlsVO.setLoanPrinAmt(0L);
							lObjLoanEmpDtlsVO.setLoanInterestAmt(lDbLoanIntAmt.longValue());
							lObjLoanEmpDtlsVO.setLoanPrinInstNo(0L);
							lObjLoanEmpDtlsVO.setLoanIntInstNo(lDbLoanIntInsNo.longValue());
							lObjLoanEmpDtlsVO.setLoanDate((Date)lObjLoanDtls[5]);
							lObjLoanEmpDtlsVO.setCmnDatabaseMst(cmnDatabaseMst);
							lObjLoanEmpDtlsVO.setCmnLocationMst(cmnLocationMst);							
							lObjLoanEmpDtlsVO.setOrgPostMstByCreatedByPost(orgPostMst);
							lObjLoanEmpDtlsVO.setOrgUserMstByCreatedBy(orgUserMst);
							lObjLoanEmpDtlsVO.setCreatedDate(gDtCurrDt);
							lObjLoanEmpDtlsVO.setTrnCounter(new Integer(1));
							lObjLoanEmpDtlsVO.setLoanIntEmiAmt(lDbLoanIntEmiAmt.longValue());
							lObjLoanEmpDtlsVO.setLoanPrinEmiAmt(0L);
							lObjLoanEmpDtlsVO.setLoanSancOrderNo(lObjLoanDtls[9].toString());
							lObjLoanEmpDtlsVO.setLoanActivateFlag(new Integer(2));
							if(lObjLoanDtls[12] != null && lObjLoanDtls[12] != ""){
								if(lObjLoanDtls[12].equals(800056D)){
									lObjLoanEmpDtlsVO.setLoanOddinstno(1L);
									lObjLoanEmpDtlsVO.setLoanOddinstAmt(lDbLoanIntOddEmiAmt.longValue());
								}else if(lObjLoanDtls[12].equals(800057D)){
									lObjLoanEmpDtlsVO.setLoanOddinstno(lDbLoanPriInsNo.longValue());
									lObjLoanEmpDtlsVO.setLoanOddinstAmt(lDbLoanIntOddEmiAmt.longValue());
								}
							}else{
								lObjLoanEmpDtlsVO.setLoanOddinstno(0L);
								lObjLoanEmpDtlsVO.setLoanOddinstAmt(0L);
							}
							lObjLoanEmpDtlsVO.setIsApproved(0);
							lObjLoanEmpDtlsVO.setVoucherNo(lStrVoucherNo);
							lObjLoanEmpDtlsVO.setVoucherDate(lDtVoucherDate);
							lObjLoanEmpDtlsVO.setLoanSancOrderdate((Date) lObjLoanDtls[16]);
							lObjLoanEmpDtlsVO.setMulLoanRecoveryMode(0);
							lObjLoanEmpDtlsVO.setMulLoanInstRecvd(1);
							lObjLoanEmpDtlsVO.setMulLoanAmtRecvd(Long.valueOf(0L));
							
							lObjBillDtlsDAO.create(lObjLoanEmpDtlsVO);
						    
							lObjEmpPrinRecoverDtlsVO = new HrLoanEmpPrinRecoverDtls();
							lLngLoanEmpPriRecDtlsId = IFMSCommonServiceImpl.getNextSeqNum("HR_LOAN_EMP_PRIN_RECOVER_DTLS", inputMap);							
						  
						    lObjEmpPrinRecoverDtlsVO.setPrinRecoverId(lLngLoanEmpPriRecDtlsId);
						    lObjEmpPrinRecoverDtlsVO.setHrLoanEmpDtls(lObjLoanEmpDtlsVO);
						    lObjEmpPrinRecoverDtlsVO.setCmnDatabaseMst(cmnDatabaseMst);
						    lObjEmpPrinRecoverDtlsVO.setOrgPostMstByUpdatedByPost(orgPostMst);
						    lObjEmpPrinRecoverDtlsVO.setOrgPostMstByCreatedByPost(orgPostMst);
						    lObjEmpPrinRecoverDtlsVO.setCmnLocationMst(cmnLocationMst);
						    lObjEmpPrinRecoverDtlsVO.setOrgUserMstByUpdatedBy(orgUserMst);
						    lObjEmpPrinRecoverDtlsVO.setOrgUserMstByCreatedBy(orgUserMst);
						    lObjEmpPrinRecoverDtlsVO.setCreatedDate(gDtCurrDt);
						    lObjEmpPrinRecoverDtlsVO.setTrnCounter(new Integer(1));
						    
						    loanEmpPrinRecvDAO.create(lObjEmpPrinRecoverDtlsVO);
						    
						    lObjEmpIntRecoverDtlsVO = new HrLoanEmpIntRecoverDtls();
						    
						    lLngLoanEmpIntRecDtlsId = IFMSCommonServiceImpl.getNextSeqNum("HR_LOAN_EMP_INT_RECOVER_DTLS", inputMap);						    
						    
						    lObjEmpIntRecoverDtlsVO.setIntRecoverId(lLngLoanEmpIntRecDtlsId);
						    lObjEmpIntRecoverDtlsVO.setHrLoanEmpDtls(lObjLoanEmpDtlsVO);
						    lObjEmpIntRecoverDtlsVO.setCmnDatabaseMst(cmnDatabaseMst);
						    lObjEmpIntRecoverDtlsVO.setOrgPostMstByUpdatedByPost(orgPostMst);
						    lObjEmpIntRecoverDtlsVO.setOrgPostMstByCreatedByPost(orgPostMst);
						    lObjEmpIntRecoverDtlsVO.setCmnLocationMst(cmnLocationMst);
						    lObjEmpIntRecoverDtlsVO.setOrgUserMstByUpdatedBy(orgUserMst);
						    lObjEmpIntRecoverDtlsVO.setOrgUserMstByCreatedBy(orgUserMst);
						    lObjEmpIntRecoverDtlsVO.setCreatedDate(gDtCurrDt);
						    lObjEmpIntRecoverDtlsVO.setTrnCounter(new Integer(1));

						    loanEmpIntRecvDAO.create(lObjEmpIntRecoverDtlsVO);
						}						
					}
				}
			}
			
			lBlFlag = true;
			
		}catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
			e.printStackTrace();
		}
		String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;		
	}
	
	public ResultObject rejectLoanBill(Map<String, Object> inputMap) {
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = false;
		LNABillDtlsDAO lObjBillDtlsDAO = new LNABillDtlsDAOImpl(MstLnaBillDtls.class, serv.getSessionFactory());
		MstLnaBillDtls lObjLnaBillDtlsVO = new MstLnaBillDtls();	
		try {
			String lStrLoanBillId = StringUtility.getParameter("loanBillId", request);			
			Long lLngLoantype = null;
			Long lLngBillId = null;
			if(!"".equals(lStrLoanBillId)){
				lLngBillId = Long.parseLong(lStrLoanBillId);
				lObjLnaBillDtlsVO = (MstLnaBillDtls) lObjBillDtlsDAO.read(lLngBillId);
				lObjLnaBillDtlsVO.setStatusFlag(0L);
				lObjLnaBillDtlsVO.setUpdatedDate(gDtCurrDt);
				lObjLnaBillDtlsVO.setUpdatedPostId(gLngPostId);
				lObjLnaBillDtlsVO.setUpdatedUserId(gLngUserId);
				
				lLngLoantype = lObjLnaBillDtlsVO.getAdvacnceType();
				if(lLngLoantype == 800028){
					lObjBillDtlsDAO.rejectBillForAdv("MstLnaCompAdvance", lLngBillId);
				}else if(lLngLoantype == 800029){
					lObjBillDtlsDAO.rejectBillForAdv("MstLnaHouseAdvance", lLngBillId);
				}else if(lLngLoantype == 800030){
					lObjBillDtlsDAO.rejectBillForAdv("MstLnaMotorAdvance", lLngBillId);
				}
				
			}
			lObjBillDtlsDAO.update(lObjLnaBillDtlsVO);
			lBlFlag = true;
			
		}catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;		
	}		
	
	private StringBuilder getResponseXMLDoc(Boolean flag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</FLAG>");		
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}



	public ResultObject viewOrderReport(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);		
		try {
			setSessionInfo(inputMap);
			LNABillDtlsDAO lObjBillDtlsDAO = new LNABillDtlsDAOImpl(MstLnaBillDtls.class, serv.getSessionFactory());
			
			List<MstLnaBillDtls> lLstBillDtls = lObjBillDtlsDAO.getLoanOrderDtls(gStrLocationCode);			
			inputMap.put("lLstBillDtls", lLstBillDtls);
			
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		resObj.setResultValue(inputMap);
		resObj.setViewName("LoanOrderReportForm");
		return resObj;
	}

}
