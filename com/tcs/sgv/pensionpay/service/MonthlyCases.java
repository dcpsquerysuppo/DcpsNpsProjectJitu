package com.tcs.sgv.pensionpay.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.pensionpay.dao.MonthlyPensionBillDAO;
import com.tcs.sgv.pensionpay.dao.MonthlyPensionBillDAOImpl;
import com.tcs.sgv.pensionpay.dao.NewPensionBillDAOImpl;
import com.tcs.sgv.pensionpay.valueobject.MonthlyPensionBillVO;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerFamilyDtls;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionArrearDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionHeldReasonDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPnsncaseRopRlt;
import com.tcs.sgv.pensionpay.valueobject.ValidPcodeView;

public class MonthlyCases extends ServiceImpl{

	/* Global Variable for PostId */
    Long gLngPostId = null;

    /* Global Variable for UserId */
    Long gLngUserId = null;

    /* Global Variable for LangId */
    Long gLngLangId = null;

    /* Global Variable for DB Id */
    Long gLngDBId = null;
    
    /* Global Variable for Logger Class */
    Log gLogger = LogFactory.getLog(getClass());
    
    /* Global Variable for Current Date */
    Date gDate = null;
	
    /**
	 * Method for the computing monthly pension for a pensioner for current/computing month
	 * @param Map, String
	 * @return Map
	 */  
	public Map computeMonthlyPension(Map<String,Object> argsMap, String PensionerCode, String PPONo, Long PenReqId, String AccNo) throws Exception
	{
		Map<String,Object> inputMap = argsMap;
        ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
        ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionConstants");
        NumberFormat lDblFrmt = new DecimalFormat("0.00");       
        
        TrnPensionRqstHdr lObjTrnPensionRqstHdrVO = null;
        ValidPcodeView lObjValidPcodeVO = null;
        //MstPensionerHdr lObjMstPensionerHdrVO = null;
        List<MonthlyPensionBillVO> lLstMonthlyPensionBillVO = new ArrayList<MonthlyPensionBillVO>();
        List<MonthlyPensionBillVO> lLstMonthlyPensionArrearVO = new ArrayList<MonthlyPensionBillVO>();
        MonthlyPensionBillVO lObjMonthlyPensionVO = null;
        List<TrnPensionArrearDtls> lLstInsertArrears = new ArrayList<TrnPensionArrearDtls>();
                
        String lStrStatus = bundleConst.getString("STATUS.CONTINUE");
        //String lStrCaseStatus = bundleConst.getString("STATUS.APPROVED");
        String lStrRcvryFlag = bundleConst.getString("RECOVERY.MONTHLY");        
        String lStrMoneyOrder = "MONEY ORDER";
        String lStrArrTI = bundleConst.getString("ARREAR.TI");
        String lStrArrMA = bundleConst.getString("ARREAR.MA");
        String lStrArrPension = bundleConst.getString("ARREAR.PENSION");
        String lStrArrFP1 = bundleConst.getString("ARREAR.FP1");
        String lStrArrFP2 = bundleConst.getString("ARREAR.FP2");
        String lStrCutPP = bundleConst.getString("CUT.PP");
        String lStrCutPT = bundleConst.getString("CUT.PT");
        String lStrCutIT = bundleConst.getString("CUT.IT");
        String lStrCutST = bundleConst.getString("CUT.ST");
        String lStrCutPerBen = bundleConst.getString("CMN.PBENEFIT");
        String lStrCutTmpBen = bundleConst.getString("CMN.TBENEFIT");        
		//List lLstAccNo = null;
        /*String lStrBank = null;
        String lStrBranch = null;*/
        String lStrHeadCode = null;
        String lStrDate = null;
        
        String lStrScheme = null;
        String lStrPnsnrCode = null;
        String lStrPnsnerName = null;
        String lStrPPONo = null;
        Long lLngPenRqstId = null;
        String lStrAccNo = null;
        Double lDPPer = 0D;
        Double lTIPer = 0D;
        Double lCVPMonthlyAmt = 0D;
        Double lMAAmt = 0D;
        Double lBasicPensionAmt = 0D;
        Double lPensionCutAmt = 0D;
        Double lSpecialCutAmt = 0D;
        Double lITCutAmt = 0D;
        Double lOtherBenefits = 0D;
        Double lRecoveryAmt = 0D;
        Double lArrearAmt = 0D;
        Double lNetPensionAmt = 0D;
        Date lDateOfDeath = null;
		Date lEndDate = null;
		Integer EDate = 0;
        Double lOMR = 0D;
        Double lPendingArrear = 0D;
        Integer lLastPaidMonth = 0;
        List lItCutDtls = new ArrayList();
        List lArrearDtls = new ArrayList();
        Integer lPrevMonth = 0;
        Integer lIntDate = 0;
        String lArrComputeFlag = null;
        MstPensionerFamilyDtls lLstFPmembers = null;
        String lStrFPFlag = "N";
        List fpMemberList = null;        
        String endDate = null;
        Double lAllnBf56 = 0D;
        Double lAllnAf56 = 0D;
		Double lPerPension = 0D;
		Double lIR = 0D;
		Double lMOComm = 0D;
		Double lPensionArr = 0D;
        Double lFP1Arr = 0D;
        Double lFP2Arr = 0D;
        Double lTIArr = 0D;
        Double lMAArr = 0D;
        Double lOtherArr = 0D;
        Integer commMonth = 0;
        Integer lDODYYYYMM = 0;
        Integer finalCompMonth = 0;
		
		PensionBillProcessServiceImpl lObjPensionBill = new PensionBillProcessServiceImpl();
		NewPensionBillDAOImpl lObjNewPensionBillDAO = new NewPensionBillDAOImpl(srvcLoc.getSessionFactory());
		MonthlyPensionBillDAO lObjMnthlyBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class,srvcLoc.getSessionFactory());
    	//MstPensionerHdrDAO lObjMstPensionerHdrDAO = new MstPensionerHdrDAOImpl(MstPensionerHdr.class,srvcLoc.getSessionFactory());
    	
    	List<MonthlyPensionBillVO> lLstPendingPensionBillVO = null;
		List<TrnPensionArrearDtls> lLstPendingArrearVO = null;
		List lLstROPDtls = null;
		List<TrnPnsncaseRopRlt> lPnsnerROPLst = null;
		TrnPnsncaseRopRlt lPnsncaseRopRltVO = null;
		
		String lStrIsROPApplied = null;
		String lStrROP_1986 = null;
		String lStrROP86_AdptedType = null;
		String lStrROP_1996 = null;
		String lStrROP_2006 = null;
		String lStrROP_1986_PAY = null;
		String lStrROP_1996_PAY = null;
		String lStrROP_2006_PAY = null;
		String lStrPayROP_1986 = null;
		String lStrPayROP_1996 = null;
		String lStrPayROP_2006 = null;
		
		Date lTillPaidDate = null; 
		Date lPayStartDate = null;		
		
		BigDecimal lPnsnrBasicAmt = BigDecimal.ZERO;
		BigDecimal lPnsnrFP1Amt = BigDecimal.ZERO;
		BigDecimal lPnsnrFP2Amt = BigDecimal.ZERO;
		
		Double lOldSixPayBasicAmt = 0D;
		Double lOldSixPayFP1Amt = 0D;
		Double lOldSixPayFP2Amt = 0D;
		
		Map lMapROPPCode = (Map)inputMap.get("lMapROPPCode");
		
        try
        {        	
        	setSessionInfo(inputMap);

        	//getting parameters for computation
        	lArrComputeFlag = inputMap.get("ArrComputeFlag").toString();
        	        	 
        	lStrPnsnrCode = PensionerCode;
        	lStrPPONo = PPONo;
        	lLngPenRqstId = PenReqId;
        	if(AccNo != null)
        	{	lStrAccNo = AccNo;	}
        	else
        	{	lStrAccNo = "";		}
        	/*lStrBank = inputMap.get("Bank").toString();
        	lStrBranch = inputMap.get("Branch").toString();
        	lStrHeadCode = inputMap.get("HeadCode").toString();*/
        	lStrDate = inputMap.get("Date").toString();
        	lStrScheme = inputMap.get("Scheme").toString();
        	
        	inputMap.put("BillType","Monthly");
        	inputMap.put("forMonth",lStrDate);
        	
        	// Monthly Pension Paid upto this date. default its last date of current month.
        	lTillPaidDate = new SimpleDateFormat("dd/MM/yyyy").parse(getDaysOfMonth(Integer.valueOf(lStrDate))+"/"+lStrDate.substring(4,6)+"/"+lStrDate.substring(0,4));
        	
        	if(inputMap.containsKey("PensionerName") && inputMap.get("PensionerName") != null)
        	{
        		lStrPnsnerName = inputMap.get("PensionerName").toString();
        	}
        	lDateOfDeath = (Date) inputMap.get("DateOfDeath");
        	
        	//list of held pensioners with reason
        	List<TrnPensionHeldReasonDtls> lLstHeldReasonDtls = (List<TrnPensionHeldReasonDtls>)inputMap.get("lLstHeldReasonDtls");
        	
        	//checking if scheme is PSB...coz for that it should be NEW else it should be approved
        	/*if(lStrScheme != null && "PSB".equals(lStrScheme))
        	{
        		lStrCaseStatus = "NEW";
        	}*/
        	        	
        	if(lStrPnsnrCode != null && lStrPnsnrCode.length() > 0 ) 
        	{
        		//getting data for pensioner from TrnPensionRqstHdr
        		//Read TrnPensionRqstHdrVO from inputMap
        		
        		if(inputMap.containsKey("lObjValidPcode") && inputMap.get("lObjValidPcode") != null)
        		{
        			lObjValidPcodeVO = (ValidPcodeView) inputMap.get("lObjValidPcode");		
        		}
        		
        		if(lObjValidPcodeVO != null)
	            {
        			if(lObjValidPcodeVO.getStatus().equals(lStrStatus))
        			{
        				//status of pensioner is continue...hence we cmpute pension for him
        				
	        			//check for scheme to be same as same with parameter
	        			if(lStrScheme.equals(lObjValidPcodeVO.getSchemeType()))
	        			{
	        				lStrHeadCode = lObjValidPcodeVO.getHeadCode().toString();
	        				
	        				lPnsnrBasicAmt = lObjValidPcodeVO.getBasicPensionAmount();
	        				lPnsnrFP1Amt = lObjValidPcodeVO.getFp1Amount();
	        				lPnsnrFP2Amt = lObjValidPcodeVO.getFp2Amount();
	        				
	        				//pensioner is a valid pensioner
	        				
	        				//get pensioner data from MstPensionerHdr
	        				/*lObjMstPensionerHdrVO = lObjMstPensionerHdrDAO.getMstPensionerHdrDtls(lStrPnsnrCode);
	        				inputMap.put("MstPensionerHdrVO", lObjMstPensionerHdrVO);*/
	        				
	        				// finding if rop applied
	        				lStrIsROPApplied = lObjValidPcodeVO.getIsRop();
	        				// Checking ROP's Applied to Pensioner.		Start.        		
	    		        	inputMap.put("IsROPApplied", lStrIsROPApplied);
	    		        	
	    		        	if(lStrIsROPApplied != null && lStrIsROPApplied.equalsIgnoreCase("Y"))
	    		        	{
	    		        		//lPnsnerROPLst = lObjNewPensionBillDAO.getROPAppliedToPensner(lStrPPONo);  
	    		        		// Fetch ROP(s) Details.	    		        		
	    		        		if(inputMap.containsKey("MapTrnPnsnRopRlt"))
	    		        		{
	    		        			lPnsnerROPLst = (List<TrnPnsncaseRopRlt>)((Map)inputMap.get("MapTrnPnsnRopRlt")).get("ROP_"+lStrPPONo);	
	    		        		}
	    		        		else
	    		        		{
	    		        			lPnsnerROPLst = lObjNewPensionBillDAO.getROPAppliedToPensner(lStrPPONo);
	    		        		}
	    		        		
	    		        		if(lPnsnerROPLst!=null && !lPnsnerROPLst.isEmpty())
	    			    		{
	    			    			for(int i=0;i<lPnsnerROPLst.size();i++)
	    			    			{
	    			    				lPnsncaseRopRltVO = null;
	    			    				lPnsncaseRopRltVO = lPnsnerROPLst.get(i);
	    			    				
	    			    				if(lPnsncaseRopRltVO.getRop().equalsIgnoreCase("1986"))
	    			    				{
	    			    					if(lPnsncaseRopRltVO.getRopPaid() != null) 
	    			    					{
	    			    						lStrROP_1986 = lPnsncaseRopRltVO.getRopPaid();
	    			    					}
	    			    					else  {
	    			    						lStrROP_1986 = "Y";
	    			    					}		    					
	    			    					lStrROP86_AdptedType = lPnsncaseRopRltVO.getAdaptedType();
	    			    				}
	    			    				else if(lPnsncaseRopRltVO.getRop().equalsIgnoreCase("1996"))
	    			    				{
	    			    					if(lPnsncaseRopRltVO.getRopPaid() != null) 
	    			    					{
	    			    						lStrROP_1996 = lPnsncaseRopRltVO.getRopPaid();
	    			    					}
	    			    					else {
	    			    						lStrROP_1996 = "Y";
	    			    					}
	    			    				}
	    			    				else if(lPnsncaseRopRltVO.getRop().equalsIgnoreCase("2006"))
	    			    				{
	    			    					if(lPnsncaseRopRltVO.getRopPaid() != null)	
	    			    					{
	    			    						lStrROP_2006 = lPnsncaseRopRltVO.getRopPaid();
	    			    					}
	    			    					else {
	    			    						lStrROP_2006 = "Y";
	    			    					}
	    			    				}
	    			    			}
	    			    			inputMap.put("ROP_1986", lStrROP_1986);
	    			    			inputMap.put("ROP86_AdptedType", lStrROP86_AdptedType);
	    			    			inputMap.put("ROP_1996", lStrROP_1996);
	    			    			inputMap.put("ROP_2006", lStrROP_2006);
	    			    			
	    			    			inputMap.put("OldBasicAmt", Double.valueOf(lObjValidPcodeVO.getBasicPensionAmount().toString()));
	    			    			
	    			    			if( (lStrROP_1986 != null && lStrROP_1986.equalsIgnoreCase("Y")) ||
			    					    (lStrROP_1996 != null && lStrROP_1996.equalsIgnoreCase("Y")) || 
			    					    (lStrROP_2006 != null && lStrROP_2006.equalsIgnoreCase("Y")) )
			    					{
	    			    				// For Pension Basic Amt ROP Amt. 
		    			    			Map lRopResMap = new HashMap();
		    			    			inputMap.put("OldBasicAmt", Double.valueOf(lObjValidPcodeVO.getBasicPensionAmount().toString()));
		    			    			lRopResMap = lObjPensionBill.getNewROPBasicAmt(inputMap);
		    			    			
		    			    			inputMap.put("Basic1986", lRopResMap.get("NewBasic1986"));
		    			    			inputMap.put("Basic1996", lRopResMap.get("NewBasic1996"));
		    			    			inputMap.put("Basic2006", lRopResMap.get("NewBasic2006"));
		    			    			
		    			    			lStrPayROP_1986 = (String)lRopResMap.get("PayROP1986");
		    			    			lStrPayROP_1996 = (String)lRopResMap.get("PayROP1996");
		    			    			lStrPayROP_2006 = (String)lRopResMap.get("PayROP2006");
		    			    			
		    			    			//	 For Pension FP1 Amt ROP Amt.		    			
		    			    			if(lObjValidPcodeVO.getFp1Amount() != null)
		    			    			{
		    			    				inputMap.put("OldBasicAmt", Double.valueOf(lObjValidPcodeVO.getFp1Amount().toString()));
		    				    			lRopResMap = lObjPensionBill.getNewROPBasicAmt(inputMap);
		    				    			
		    				    			inputMap.put("FP11986", lRopResMap.get("NewBasic1986"));
		    				    			inputMap.put("FP11996", lRopResMap.get("NewBasic1996"));
		    				    			inputMap.put("FP12006", lRopResMap.get("NewBasic2006"));
		    			    			}
		    			    			
		    			    			//	 For Pension FP2 Amt ROP Amt. 
		    			    			if(lObjValidPcodeVO.getFp2Amount() != null)
		    			    			{
		    				    			inputMap.put("OldBasicAmt", Double.valueOf(lObjValidPcodeVO.getFp2Amount().toString()));
		    				    			lRopResMap = lObjPensionBill.getNewROPBasicAmt(inputMap);
		    				    			
		    				    			inputMap.put("FP21986", lRopResMap.get("NewBasic1986"));
		    				    			inputMap.put("FP21996", lRopResMap.get("NewBasic1996"));
		    				    			inputMap.put("FP22006", lRopResMap.get("NewBasic2006"));
		    			    			}			    			
		    			    			
		    			    			//  set ROP Pay Flage
		    			    			inputMap.put("PayROP1986", lStrPayROP_1986);
		    			    			inputMap.put("PayROP1996", lStrPayROP_1996);
		    			    			inputMap.put("PayROP2006", lStrPayROP_2006);
	
		    			    			//generating rop data to be updated at the time of save
		    			    			//has details of rop to be paid and data to be updated in rqst hdr 
		    			    			if("Y".equals(inputMap.get("PayROP2006").toString()))
		    			    			{
		    			    				lLstROPDtls = new ArrayList();
		    			    				lLstROPDtls.add(lLngPenRqstId);
		    			    				lLstROPDtls.add(lStrPPONo);
		    			    				lLstROPDtls.add(inputMap.get("PayROP1986").toString());
		    			    				lLstROPDtls.add(inputMap.get("PayROP1996").toString());
		    			    				lLstROPDtls.add(inputMap.get("PayROP2006").toString());
		    			    				lLstROPDtls.add(inputMap.get("Basic2006"));
		    			    				lLstROPDtls.add(inputMap.get("FP12006"));
		    			    				lLstROPDtls.add(inputMap.get("FP22006"));
		    			    				
		    			    			}
		    			    			else if("Y".equals(inputMap.get("PayROP1996").toString()))
		    			    			{
		    			    				lLstROPDtls = new ArrayList();
		    			    				lLstROPDtls.add(lLngPenRqstId);
		    			    				lLstROPDtls.add(lStrPPONo);
		    			    				lLstROPDtls.add(inputMap.get("PayROP1986").toString());
		    			    				lLstROPDtls.add(inputMap.get("PayROP1996").toString());
		    			    				lLstROPDtls.add(inputMap.get("PayROP2006").toString());
		    			    				lLstROPDtls.add(inputMap.get("Basic1996"));
		    			    				lLstROPDtls.add(inputMap.get("FP11996"));
		    			    				lLstROPDtls.add(inputMap.get("FP21996"));
		    			    				
		    			    			}
		    			    			else if("Y".equals(inputMap.get("PayROP1986").toString()))
		    			    			{
		    			    				lLstROPDtls = new ArrayList();
		    			    				lLstROPDtls.add(lLngPenRqstId);
		    			    				lLstROPDtls.add(lStrPPONo);
		    			    				lLstROPDtls.add(inputMap.get("PayROP1986").toString());
		    			    				lLstROPDtls.add(inputMap.get("PayROP1996").toString());
		    			    				lLstROPDtls.add(inputMap.get("PayROP2006").toString());
		    			    				lLstROPDtls.add(inputMap.get("Basic1986"));
		    			    				lLstROPDtls.add(inputMap.get("FP11986"));
		    			    				lLstROPDtls.add(inputMap.get("FP21986"));
		    			    			}
		    			    			inputMap.put("lLstROPDtls", lLstROPDtls);
	    			    			}
	    			    			else
	    			    			{
	    			    				inputMap.put("PayROP1986", "N");
		    			    			inputMap.put("PayROP1996", "N");
		    			    			inputMap.put("PayROP2006", "N");
	    			    			}
	    			    		}
	    		        	}
	    	 	        	// If Revision is already Paid then we will see which ROP's are paid.
	    	 	        	else if(lStrIsROPApplied != null && lStrIsROPApplied.equalsIgnoreCase("P"))
	    		        	{
	    	 	        		//lPnsnerROPLst = lObjNewPensionBillDAO.getROPAppliedToPensner(lStrPPONo);  
	    	 	        		// Fetch ROP(s) Details.
	    	 	        		if(inputMap.containsKey("MapTrnPnsnRopRlt"))
	    		        		{
	    		        			lPnsnerROPLst = (List<TrnPnsncaseRopRlt>)((Map)inputMap.get("MapTrnPnsnRopRlt")).get("ROP_"+lStrPPONo);	
	    		        		}
	    		        		else 
	    		        		{
	    		        			lPnsnerROPLst = lObjNewPensionBillDAO.getROPAppliedToPensner(lStrPPONo);
	    		        		}
	    	 	        		
	    	 	        		if(lPnsnerROPLst!=null && !lPnsnerROPLst.isEmpty())
	    			    		{
	    			    			for(int i=0;i<lPnsnerROPLst.size();i++)
	    			    			{
	    			    				lPnsncaseRopRltVO = null;
	    			    				lPnsncaseRopRltVO = lPnsnerROPLst.get(i);
	    			    				
	    			    				if(lPnsncaseRopRltVO.getRop().equalsIgnoreCase("1986"))
	    			    				{
	    			    					lStrROP_1986 = "P";
	    			    					//lStrROP_1986 = lPnsncaseRopRltVO.getRopPaid();
	    			    				}
	    			    				else if(lPnsncaseRopRltVO.getRop().equalsIgnoreCase("1996"))
	    			    				{
	    			    					lStrROP_1996 = "P";
	    		    						//lStrROP_1996 = lPnsncaseRopRltVO.getRopPaid();
	    			    				}
	    			    				else if(lPnsncaseRopRltVO.getRop().equalsIgnoreCase("2006"))
	    			    				{
	    			    					lStrROP_2006 = "P";
	    		    						//lStrROP_2006 = lPnsncaseRopRltVO.getRopPaid();
	    			    				}
	    			    			}
	    			    			
	    			    			inputMap.put("ROP_1986", lStrROP_1986);
	    			    			inputMap.put("ROP_1996", lStrROP_1996);
	    			    			inputMap.put("ROP_2006", lStrROP_2006);	
	    			    			
	    			    			if("P".equals(lStrROP_2006))
	    			    			{
	    			    				lMapROPPCode.put("ROP06"+lObjValidPcodeVO.getPensionerCode(), lStrROP_2006) ;
	    			    			}
	    			    		
	    			    		}
	    		        	}
	    		        	//	Checking ROP's Applied to Pensioner.		End.
	    					
	        				//checking for end date
	        				lEndDate = lObjValidPcodeVO.getEndDate();
	        				if(lEndDate != null){
	    	        			endDate = new SimpleDateFormat("yyyyMM").format(lEndDate);
	    	        			
	    	        			// set end date as pension last paid date.
	    	        			if(Integer.valueOf(endDate) <= Integer.valueOf(lStrDate ))
	    	        			{
	    	        				lTillPaidDate = lEndDate;
	    	        			}
	    	        		}
	        				else{
	        					endDate = "000000";
	        				}
	        				EDate = Integer.valueOf(endDate);
	        				//check the last month for which monthly pension or pension paid....if 0 implies no bill generated for this pensioner...not even pension bill....
	        				//hence should not compute monthly bill for tht pensioner
	        	    		
	        				/*lLastPaidMonth = lObjMnthlyBillDAO.getLastMonth(lStrPnsnrCode, lStrScheme);*/
	        				//now we have to consider rejected bills too...hence first check if bill for this month was rejected
	        				/*boolean lRejectFlag = lObjMnthlyBillDAO.isRejectedBill(lStrDate, lStrBank, lStrBranch, lStrHeadCode, lStrScheme);
	        				if(lRejectFlag == false)
	        				{
	        					//bill for this month was rejected...so generate it again
	        					//therefore get the last paid month before this month...
	        					//i.e last month before this month for which bill was computed
	        					lLastPaidMonth = lObjMnthlyBillDAO.getLastMonthForRejected(lStrPnsnrCode, lStrScheme, lStrDate);
	        				}
	        				else
	        				{
	        					//normal bill...being made for the first time
	        					lLastPaidMonth = lObjMnthlyBillDAO.getLastMonth(lStrPnsnrCode, lStrScheme);
	        				}*/
	        				
	        				if(lDateOfDeath != null)
	    	        		{
	        					lDODYYYYMM =  Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lDateOfDeath));
	        					Date lFPDODdate = null;
        						// indicates that the pensioner has expired and family details to be found compute details for family pensioner's...
    	    					if(inputMap.containsKey("lMapFamilyDtls"))
    	    					{
    	    						lLstFPmembers =  (MstPensionerFamilyDtls)((Map)inputMap.get("lMapFamilyDtls")).get("Family"+lStrPnsnrCode);	
    	    					}
    	    					//lLstFPmembers = lObjMnthlyBillDAO.getMstPensionerFamilyDtls(lStrPnsnrCode);
    	    					lStrFPFlag = "Y";
    	    				
    	    					fpMemberList = new ArrayList();
    	    					
    	    					if(lLstFPmembers != null)
    	    					{
    	    						fpMemberList = lObjPensionBill.getFpMemberList(lLstFPmembers);
    	    						lFPDODdate = lLstFPmembers.getDateOfDeath();
    	    						lStrPnsnerName = lLstFPmembers.getName();
    	    					}
    	    					
    	    					if(!fpMemberList.isEmpty() && lFPDODdate != null)
    	    					{
    	    						if(lFPDODdate.after(lDateOfDeath))
    	    						{
	    	    						if(lEndDate == null)
	    	    						{
	    	    							lEndDate = lFPDODdate;    	    							
	    	    						}
	    	    						else if(lEndDate != null && (lFPDODdate.before(lEndDate) || lFPDODdate.equals(lEndDate)) )
	        							{
	    	    							lEndDate = lFPDODdate;
	        							}	    	    						
    	    						}
    	    						else
    	    						{
    	    							if(lEndDate == null)
        	    						{
        	    							lEndDate = lDateOfDeath;
        	    						}
        	    						else if(lEndDate != null && (lDateOfDeath.before(lEndDate) || lDateOfDeath.equals(lEndDate)) )
            							{
        	    							lEndDate = lDateOfDeath;
            							}
    	    						}
    	    						lTillPaidDate = lEndDate;
    	    						endDate = new SimpleDateFormat("yyyyMM").format(lEndDate);
	    							EDate = Integer.valueOf(endDate);
    	    					}
    	    					else if(fpMemberList.isEmpty())
    	    					{
    	    						if(lEndDate == null)
    	    						{
    	    							lEndDate = lDateOfDeath;
    	    						}
    	    						else if(lEndDate != null && (lDateOfDeath.before(lEndDate) || lDateOfDeath.equals(lEndDate)) )
        							{
    	    							lEndDate = lDateOfDeath;
        							}
    	    						lTillPaidDate = lEndDate;
    	    						endDate = new SimpleDateFormat("yyyyMM").format(lEndDate);
	    							EDate = Integer.valueOf(endDate);
    	    					}
    	    					
    	    					inputMap.put("EndDate", lEndDate);
    	    					//inputMap.put("FPDOB", lLstFPmembers.getDateOfBirth());
    	    					inputMap.put("FPmembersVo",lLstFPmembers);
    	    					inputMap.put("fpMemberList",fpMemberList);
    	    					
    	    					/*if(fpMemberList != null && fpMemberList.size() <= 0)
    	    					{
    	    						//this pensioner has expired and has no family details/all family pensioners have expired
    	    						//hence monthly bill will not be generated for this pensioner for this month
    	    						//family details not valid for this expired pensioner....hence add this pensioner in held list
    	            				String lTempPcode = lObjTrnPensionRqstHdrVO.getPensionerCode();
    	                			String lTempPPOno = lObjTrnPensionRqstHdrVO.getPpoNo();
    	                			String lReason = "Status is "+lObjTrnPensionRqstHdrVO.getStatus();
    	                			
    	                			TrnPensionHeldReasonDtls lObjHeldReasonDtls = insertHeldReasonDtls(lTempPcode,lTempPPOno,lReason,lPrevMonth.toString());
    	                			lLstHeldReasonDtls.add(lObjHeldReasonDtls);
    	    					}*/
	    	        		}
	        				
	        				lLastPaidMonth = Integer.parseInt(inputMap.get("LastPaidMonth").toString());
	        	    		
	        	    		lIntDate = Integer.parseInt(lStrDate);
	        	    		
	        	    		
        	    			//find previous month
	        	    		if((lIntDate%100) == 01){
	        	    			lPrevMonth = lIntDate - 89;
	        	    		}
	        	    		else{
	        	    			lPrevMonth = lIntDate - 1;
	        	    		}
	        	    		
	        	    		//Required for Transfer Case and Transfer Scheme
	        	    		if(lLastPaidMonth == 0)
	        	    			lLastPaidMonth = lPrevMonth;
	        	    		
	        	    		
	        	    		if((lLastPaidMonth%100) == 12){
    							commMonth = lLastPaidMonth + 89;
	        	    		}
	        	    		else{
	        	    			commMonth = lLastPaidMonth + 1;
	        	    		}	        	    		
	        	    		
	        	    		// Starting date of the monthly pension will set as pension from date in bill details
	        	        	if(inputMap.containsKey("PayStartDate") && inputMap.get("PayStartDate") != null)
	        				{
	        					lPayStartDate = (Date) inputMap.get("PayStartDate");
	        					lPayStartDate = lObjPensionBill.getNextDate(lPayStartDate);
	        				}
	        				else
	        				{
	        					lPayStartDate = new SimpleDateFormat("dd/MM/yyyy").parse("01/"+commMonth.toString().substring(4,6)+"/"
	        										+commMonth.toString().substring(0,4));
	        				}
	        	    		
	        	        	
							/* ROP 2006 is applied and if calculate month is before 200904
			        		 * then for those month pensioner pension calculate according to 
			        		 * old basic amount.
			        		 */	        		
			        		if(lStrIsROPApplied != null && lStrIsROPApplied.equalsIgnoreCase("P") &&
			        			lStrROP_2006 != null && lStrROP_2006.equals("P") &&
			        			commMonth.intValue() >= 200601 && commMonth.intValue() < 200904)
			        		{
			        			List lObjOldBasicLst = lObjMnthlyBillDAO.getOldBasicSixPayNewBasic(lStrPnsnrCode);
			        		
			        			if(lObjOldBasicLst != null && !lObjOldBasicLst.isEmpty())
			        			{
			        				for(int t=0;t<lObjOldBasicLst.size();t++)
			        				{
			        					Object[] lObjTemp = (Object[]) lObjOldBasicLst.get(t);
			        					
			        					if(lObjTemp[0] != null && lObjTemp[0].equals("Basic Pension 2006"))
			        					{
			        						lOldSixPayBasicAmt = lObjTemp[1] != null ? Double.valueOf(lObjTemp[1].toString()) : 0d; 
			        					} else if(lObjTemp[0] != null && lObjTemp[0].equals("FP1 Amount 2006"))
			        					{
			        						lOldSixPayFP1Amt = lObjTemp[1] != null ? Double.valueOf(lObjTemp[1].toString()) : 0d; 
			        					} else if(lObjTemp[0] != null && lObjTemp[0].equals("FP2 Amount 2006"))
			        					{
			        						lOldSixPayFP2Amt = lObjTemp[1] != null ? Double.valueOf(lObjTemp[1].toString()) : 0d; 
			        					}
			        					
			        				}
			        			}
			        		}
	        	        	
			        		
			        		
	        	    		//monthly pension to be computed for pensioner only for the following condn 
	        	    		if(lLastPaidMonth < lIntDate)
		        	    	{
		        				if(EDate < lIntDate && EDate != 0)
		        				{
		        					/*					//end date was before this month
		        					//therefore do not calculate for this month 
		        					//we need to make an entry in held list for this month for this pensioner
		        					//end date is before this month....hence add this pensioner in held list
		            				String lTempPcode = lStrPnsnrCode;
		                			String lTempPPOno = lStrPPONo;
		                			String lReason = "End date before this month";
		                			
		                			TrnPensionHeldReasonDtls lObjHeldReasonDtls = insertHeldReasonDtls(lTempPcode,lTempPPOno,lReason,lStrDate);
		                			lLstHeldReasonDtls.add(lObjHeldReasonDtls);*/
		        							        					
		        					//but check if some previous bills were not paid
		        					if(!"M".equals(lObjValidPcodeVO.getCalcType()))
		        					{
			        					if(lLastPaidMonth != lPrevMonth && lLastPaidMonth < lPrevMonth && lLastPaidMonth != 0)
			            	    		{
			            	    			/*MonthlyPensionBillVO lObjPending = new MonthlyPensionBillVO();
			            	    			
			            	    			//monthly pension pending...so compute that
			            	    			//if not paid for any month....call recursive function and get net pension in arrears for this month
			            	    			inputMap.put("Date", lPrevMonth);
			            	    			inputMap.put("FPFlag", "N");
			                    			inputMap.put("ArrComputeFlag", "Y");
			            	    			Map lMapTemp = computeMonthlyPension(inputMap, lStrPnsnrCode, lStrPPONo, lLngPenRqstId, lStrAccNo);
			            	    			lLstPendingPensionBillVO = (List<MonthlyPensionBillVO>) lMapTemp.get("lLstMonthlyPensionBillVO");
			            	    	        lLstPendingArrearVO = (List<TrnPensionArrearDtls>) lMapTemp.get("lLstInsertArrears");
			            	    			
			            	    	        if(lLstPendingArrearVO != null && !lLstPendingArrearVO.isEmpty())
			            	    	        {
			            	    	        	for(int m = 0; m < lLstPendingArrearVO.size(); m++){
			            	    	        		lLstInsertArrears.add(lLstPendingArrearVO.get(m));
			            	    	        	}
			            	    			}
			            	    	        
			            	    	        if(lLstPendingPensionBillVO != null && !lLstPendingPensionBillVO.isEmpty())
			            	    	        {
			            	    	        	for(int m = 0; m < lLstPendingPensionBillVO.size(); m++){
			            	    	        		lObjPending = lLstPendingPensionBillVO.get(0);
			                    	    			lPendingArrear = lPendingArrear + Double.parseDouble(lObjPending.getNetPensionAmount().toString());
			                    	    			lLstMonthlyPensionArrearVO.add(lObjPending);
			            	    	        	}
			            	    			}*/
			        						
			        						/*finalCompMonth = EDate;
			        						if (lDateOfDeath != null && lDateOfDeath.before(lEndDate) && fpMemberList.isEmpty()) {
												finalCompMonth = lDODYYYYMM;
											}*/
			        									        						
			        						for(Integer compMonth = commMonth; compMonth <= EDate;)
			        						{
			        							MonthlyPensionBillVO lObjPending = new MonthlyPensionBillVO();
			        							
			        							//monthly pension pending...so compute that
			        							//if not paid for any month....call recursive function and get net pension in arrears for this month
			        							inputMap.put("Date", compMonth);
			        							inputMap.put("FPFlag", "N");
			        							inputMap.put("ArrComputeFlag", "Y");
			        							inputMap.put("TrnPensionRqstHdrVO", lObjTrnPensionRqstHdrVO);
			        							inputMap.put("AccNo", lStrAccNo);
			        							inputMap.put("lPendingArrear", 0);
			        							
			        							/* ROP 2006 is applied and calculate month is between 200601 and 200904
			        			        		 * then current month calculate according to 2005 ROP and 
			        			        		 * 200904 onwards current month calculate according to 2006 ROP.
			        			        		 */	        		
			        			        		if(lStrIsROPApplied != null && lStrIsROPApplied.equalsIgnoreCase("P") &&
			        			        			lStrROP_2006 != null && lStrROP_2006.equals("P") &&
			        			        			compMonth.intValue() >= 200601 && compMonth.intValue() < 200904)
			        			        		{
			        			        			inputMap.put("ROP_2006", null);
			        			        			
			        			        			ValidPcodeView lTemPcodeView = (ValidPcodeView) lObjValidPcodeVO.clone();
			        			        			lTemPcodeView.setBasicPensionAmount(new BigDecimal(lOldSixPayBasicAmt));
			        			        			lTemPcodeView.setFp1Amount(new BigDecimal(lOldSixPayFP1Amt));
			        			        			lTemPcodeView.setFp2Amount(new BigDecimal(lOldSixPayFP2Amt));
			        			        			inputMap.put("lObjValidPcode",lTemPcodeView);	
			        			        		}
			        			        		else if(lStrIsROPApplied != null && lStrIsROPApplied.equalsIgnoreCase("P") &&
			        			        				lStrROP_2006 != null && lStrROP_2006.equals("P") && 
			        			        				compMonth.intValue() >= 200904)
			        			        		{
			        			        			inputMap.put("ROP_2006", "P");
			        			        			ValidPcodeView lTemPcodeView = (ValidPcodeView) lObjValidPcodeVO.clone();
			        			        			lTemPcodeView.setBasicPensionAmount(lPnsnrBasicAmt);
			        			        			lTemPcodeView.setFp1Amount(lPnsnrFP1Amt);
			        			        			lTemPcodeView.setFp2Amount(lPnsnrFP2Amt);
			        			        			inputMap.put("lObjValidPcode",lTemPcodeView);
			        			        		}
			        							
			    	        					inputMap = lObjPensionBill.getCurrMonthData(inputMap);
			        							lLstPendingPensionBillVO = (List<MonthlyPensionBillVO>) inputMap.get("lLstMonthlyPensionBillVO");
			        							lLstPendingArrearVO = (List<TrnPensionArrearDtls>) inputMap.get("lLstInsertArrears");
			        							
			        					        if(lLstPendingArrearVO != null && !lLstPendingArrearVO.isEmpty())
			        					        {
			        					        	for(int m = 0; m < lLstPendingArrearVO.size(); m++)
			        					        	{
			        					        		lLstInsertArrears.add(lLstPendingArrearVO.get(m));
			        					        	}
			        							}
			        					        
			        					        if(lLstPendingPensionBillVO != null && !lLstPendingPensionBillVO.isEmpty())
			        					        {
			        					        	for(int m = 0; m < lLstPendingPensionBillVO.size(); m++)
			        					        	{
			        					        		lObjPending = lLstPendingPensionBillVO.get(0);
			        					    			lPendingArrear = lPendingArrear + Double.parseDouble(lObjPending.getNetPensionAmount().toString());
			        					    			lLstMonthlyPensionArrearVO.add(lObjPending);
			        					        	}
			        							}
			        							
			        							compMonth = (compMonth%100 == 12)?(compMonth + 89):(compMonth + 1);
			        						}
			        							
			            	    			if(lPendingArrear != 0D && lPendingArrear > 0D)
			            	    			{
			            	    				//get details like ppo no, pnsnr name and acc no
			            	    				//arrear amt = pending amt
			            	    				//other amts = 0
			            	    				//net pension = arrear
			            	    				//lObjMstPensionerHdrVO = lObjMstPensionerHdrDAO.getMstPensionerHdrDtls(lStrPnsnrCode);
			        	    	        		/*lLstAccNo = lObjMnthlyBillDAO.getAccountNo(lStrPnsnrCode, lStrBank, lStrBranch);
			        	    	        		if(lLstAccNo.get(0) != null){
			        	    	        			lStrAccNo = lLstAccNo.get(0).toString();
			        	    	        		}
			        	    	        		else{
			        	    	        			lStrAccNo = "";
			        	    	        		}*/
			        	    	        		/*if(lObjMstPensionerHdrVO != null)
			        	        	        	{
			        	        	        		// Getting the Value from MstPensionerHdr... Start...
			        	        					if(lObjMstPensionerHdrVO.getMiddleName() != null){
			        	        						lStrPnsnerName = lObjMstPensionerHdrVO.getFirstName() + ' ' + 
			        	        		 				  				 lObjMstPensionerHdrVO.getMiddleName() + ' ' +
			        	        		 				  				 lObjMstPensionerHdrVO.getLastName();
			        	        					}
			        	        					else{
			        	        						lStrPnsnerName = lObjMstPensionerHdrVO.getFirstName() + ' ' + 
			        	        										 lObjMstPensionerHdrVO.getLastName();
			        	        					}
			        	        		        	lDateOfDeath = lObjMstPensionerHdrVO.getDateOfDeath();
			        	        	        	}*/
			            	    				
			            	           	    	        		
			        	    	        		if(lStrScheme.equals(lStrMoneyOrder))
			        	    	        		{
			        	    	    				lMOComm = lNetPensionAmt * 5 / 100;
			        	    	    				lMOComm = Math.ceil(lMOComm);
			        	    	    			}
			        	    	        		
			        	    	        		if("Y".equals(lStrFPFlag) && !fpMemberList.isEmpty())
			        	    	    	    	{
			        	    	    	    		for(int y=0; y < (fpMemberList.size()); y+=3)
			        	    	    	    		{
			        	    	    	    			lStrPnsnerName = fpMemberList.get(y+0).toString();
			        	    	    	    			Double prcnt = Double.parseDouble(fpMemberList.get(y+1).toString())/100;
			        	    	    	    			
			        	    	    	    			/*if(fpMemberList.get(y+2) != null){
			        	    	    	    				lStrAccNo = fpMemberList.get(y+2).toString();
			        	    	    	    			}
			        	    	    	    			else{
			        	    	    	    				lStrAccNo = "";
			        	    	    	    			}*/
		
			        	    	    	    			lObjMonthlyPensionVO = new MonthlyPensionBillVO();
					    	            	        	inputMap.put("MnthPpoNo", lStrPPONo);
					    	            	        	inputMap.put("MnthPnsnrCode", lStrPnsnrCode);
					    	            	        	inputMap.put("MnthPensionerName", lStrPnsnerName);
					    	            	        	inputMap.put("MnthAccountNo", lStrAccNo);
					    	            	        	inputMap.put("MnthBasicPensionAmount", 0);
					    	            	        	inputMap.put("MnthDpPercent", 0);
					    	            	        	inputMap.put("MnthTiPercent", 0);
					    	            	        	inputMap.put("MnthDpPercentAmount", 0);
					    	            	        	inputMap.put("MnthTiPercentAmount", 0);
					    	            	        	inputMap.put("MnthMedicalAllowenceAmount", 0);
					    	            	        	inputMap.put("MnthCvpMonthlyAmount", 0);
					    	            	        	inputMap.put("MnthItCutAmount", 0);
					    	            	        	inputMap.put("MnthSpecialCutAmount", 0);
					    	            	        	inputMap.put("MnthPensionCutAmount", 0);
					    	            	        	inputMap.put("MnthRecoveryAmount", 0);
					    	            	        	//long lLngAmount = Math.round(lPendingArrear*prcnt);
					    	            	        	inputMap.put("MnthNetPensionAmount", Math.round(lPendingArrear*prcnt));
					    	            	        	inputMap.put("MnthAllnBf56", 0);
					    	            	        	inputMap.put("MnthAllnAf56", 0);
					    	            	        	inputMap.put("MnthAllnAf60", 0);
					    	            	        	inputMap.put("MnthPersonalPension", 0);
			        	    	    		        	inputMap.put("MnthIRAmount", 0);
			        	    	    		        	inputMap.put("MnthMOComm", Math.round(lMOComm*prcnt));
			        	    	    		        	inputMap.put("MnthTIArrearsAmount", 0);
			        	    	    		        	inputMap.put("MnthOtherArrearsAmount", Math.round(lPendingArrear*prcnt));
			        	    	    		        	inputMap.put("MnthOtherBenefit", 0);
			        	    	    		        	inputMap.put("MnthOMR", 0);
			        	    	    		        	inputMap.put("MnthYYYYMM", lIntDate);
			        	    	    		        	inputMap.put("MnthHeadcode", lStrHeadCode);
			        	    	    		        	inputMap.put("MnthFromPayDate", lPayStartDate);
			        	    	    		        	inputMap.put("MnthToPayDate", lTillPaidDate);
					    	            	        	
					        			        		lObjMonthlyPensionVO = lObjPensionBill.insertMonthlyDtls(inputMap);
					        			        		lLstMonthlyPensionBillVO.add(lObjMonthlyPensionVO);
			        	    	    	    		}
			        	    	    	    	}
			        	    	    	    	else
			        	    	    	    	{
			        	    	    	        	lObjMonthlyPensionVO = new MonthlyPensionBillVO();
				    	            	        	inputMap.put("MnthPpoNo", lStrPPONo);
				    	            	        	inputMap.put("MnthPnsnrCode", lStrPnsnrCode);
				    	            	        	inputMap.put("MnthPensionerName", lStrPnsnerName);
				    	            	        	inputMap.put("MnthAccountNo", lStrAccNo);
				    	            	        	inputMap.put("MnthBasicPensionAmount", 0);
				    	            	        	inputMap.put("MnthDpPercent", 0);
				    	            	        	inputMap.put("MnthTiPercent", 0);
				    	            	        	inputMap.put("MnthDpPercentAmount", 0);
				    	            	        	inputMap.put("MnthTiPercentAmount", 0);
				    	            	        	inputMap.put("MnthMedicalAllowenceAmount", 0);
				    	            	        	inputMap.put("MnthCvpMonthlyAmount", 0);
				    	            	        	inputMap.put("MnthItCutAmount", 0);
				    	            	        	inputMap.put("MnthSpecialCutAmount", 0);
				    	            	        	inputMap.put("MnthPensionCutAmount", 0);
				    	            	        	inputMap.put("MnthRecoveryAmount", 0);
				    	            	        	//long lLngAmount = Math.round(lPendingArrear);
				    	            	        	inputMap.put("MnthNetPensionAmount", Math.round(lPendingArrear));
				    	            	        	inputMap.put("MnthAllnBf56", 0);
				    	            	        	inputMap.put("MnthAllnAf56", 0);
				    	            	        	inputMap.put("MnthAllnAf60", 0);
				    	            	        	inputMap.put("MnthPersonalPension", 0);
		        	    	    		        	inputMap.put("MnthIRAmount", 0);
		        	    	    		        	inputMap.put("MnthMOComm", Math.round(lMOComm));
		        	    	    		        	inputMap.put("MnthTIArrearsAmount", 0);
		        	    	    		        	inputMap.put("MnthOtherArrearsAmount", Math.round(lPendingArrear));
		        	    	    		        	inputMap.put("MnthOtherBenefit", 0);
		        	    	    		        	inputMap.put("MnthOMR", 0);
		        	    	    		        	inputMap.put("MnthYYYYMM", lIntDate);
		        	    	    		        	inputMap.put("MnthHeadcode", lStrHeadCode);
		        	    	    		        	inputMap.put("MnthFromPayDate", lPayStartDate);
		        	    	    		        	inputMap.put("MnthToPayDate", lTillPaidDate);
				    	            	        					    	            	        	
				        			        		lObjMonthlyPensionVO = lObjPensionBill.insertMonthlyDtls(inputMap);
				        			        		lLstMonthlyPensionBillVO.add(lObjMonthlyPensionVO);
			        	    	    	    	}
			            	    			}
			            	       		}
			            	    		inputMap.put("FPFlag", "Y");
			            	    		inputMap.put("Date", lStrDate);
		        					}
		        				}
		        				else
		        				{
		        					//end date not before this month...hence compute monthly pension for current month
		        					if(!"M".equals(lObjValidPcodeVO.getCalcType()))
		        					{
		        						//mode of calculation is not manual....auto mode
		        						
		        						if(lDateOfDeath != null && fpMemberList.isEmpty() && lDODYYYYMM.intValue() < lIntDate.intValue())
		        						{
		        							lIntDate = lDODYYYYMM;
		        						}
		        						
		        						for(Integer compMonth = commMonth; compMonth < lIntDate; )
		        						{
		        							if(lLastPaidMonth != compMonth && lLastPaidMonth < compMonth && lLastPaidMonth != 0)
			        						{
			        							MonthlyPensionBillVO lObjPending = new MonthlyPensionBillVO();
			        							
			        							//monthly pension pending...so compute that
			        							//if not paid for any month....call recursive function and get net pension in arrears for this month
			        							inputMap.put("Date", compMonth);
			        							inputMap.put("FPFlag", "N");
			        							inputMap.put("ArrComputeFlag", "Y");
			        							inputMap.put("TrnPensionRqstHdrVO", lObjTrnPensionRqstHdrVO);
			        							inputMap.put("AccNo", lStrAccNo);
			        							inputMap.put("lPendingArrear", 0);
			        							
			        							/* ROP 2006 is applied and calculate month is between 200601 and 200904
			        			        		 * then current month calculate according to 2005 ROP and 
			        			        		 * 200904 onwards current month calculate according to 2006 ROP.
			        			        		 */	        		
			        			        		if(lStrIsROPApplied != null && lStrIsROPApplied.equalsIgnoreCase("P") &&
			        			        			lStrROP_2006 != null && lStrROP_2006.equals("P") &&
			        			        			compMonth.intValue() >= 200601 && compMonth.intValue() < 200904)
			        			        		{
			        			        			inputMap.put("ROP_2006", null);
			        			        			
			        			        			ValidPcodeView lTemPcodeView = (ValidPcodeView) lObjValidPcodeVO.clone();
			        			        			lTemPcodeView.setBasicPensionAmount(new BigDecimal(lOldSixPayBasicAmt));
			        			        			lTemPcodeView.setFp1Amount(new BigDecimal(lOldSixPayFP1Amt));
			        			        			lTemPcodeView.setFp2Amount(new BigDecimal(lOldSixPayFP2Amt));
			        			        			inputMap.put("lObjValidPcode",lTemPcodeView);
			        			        		}
			        			        		else if(lStrIsROPApplied != null && lStrIsROPApplied.equalsIgnoreCase("P") &&
			        			        				lStrROP_2006 != null && lStrROP_2006.equals("P") && 
			        			        				compMonth.intValue() >= 200904)
			        			        		{
			        			        			inputMap.put("ROP_2006", "P");
			        			        			
			        			        			ValidPcodeView lTemPcodeView = (ValidPcodeView) lObjValidPcodeVO.clone();			        			        			
			        			        			lTemPcodeView.setBasicPensionAmount(lPnsnrBasicAmt);
			        			        			lTemPcodeView.setFp1Amount(lPnsnrFP1Amt);
			        			        			lTemPcodeView.setFp2Amount(lPnsnrFP2Amt);
			        			        			inputMap.put("lObjValidPcode",lTemPcodeView);			        			        			
			        			        		}
			        							
			        							
			    	        					inputMap = lObjPensionBill.getCurrMonthData(inputMap);
			        							lLstPendingPensionBillVO = (List<MonthlyPensionBillVO>) inputMap.get("lLstMonthlyPensionBillVO");
			        							lLstPendingArrearVO = (List<TrnPensionArrearDtls>) inputMap.get("lLstInsertArrears");
			        							
			        					        if(lLstPendingArrearVO != null && !lLstPendingArrearVO.isEmpty())
			        					        {
			        					        	for(int m = 0; m < lLstPendingArrearVO.size(); m++)
			        					        	{
			        					        		lLstInsertArrears.add(lLstPendingArrearVO.get(m));
			        					        	}
			        							}
			        					        
			        					        if(lLstPendingPensionBillVO != null && !lLstPendingPensionBillVO.isEmpty())
			        					        {
			        					        	for(int m = 0; m < lLstPendingPensionBillVO.size(); m++)
			        					        	{
			        					        		lObjPending = lLstPendingPensionBillVO.get(0);
			        					    			lPendingArrear = lPendingArrear + Double.parseDouble(lObjPending.getNetPensionAmount().toString());
			        					    			lLstMonthlyPensionArrearVO.add(lObjPending);
			        					        	}
			        							}
			        				   		}
			        						inputMap.put("FPFlag", "Y");
			        						inputMap.put("Date", lStrDate);
			        						
			        						compMonth = (compMonth%100 == 12)?(compMonth + 89):(compMonth + 1);
		        						}
		        					
			        					Long pending = Math.round(lPendingArrear);
			        					lPendingArrear = Double.parseDouble(pending.toString());
	
			        					if(lDateOfDeath != null && fpMemberList.isEmpty() && lDODYYYYMM.intValue() < lIntDate.intValue())
		        						{
				        					if(lPendingArrear > 0)
				        					{
				        						lObjMonthlyPensionVO = new MonthlyPensionBillVO();
			    	            	        	inputMap.put("MnthPpoNo", lStrPPONo);
			    	            	        	inputMap.put("MnthPnsnrCode", lStrPnsnrCode);
			    	            	        	inputMap.put("MnthPensionerName", lStrPnsnerName);
			    	            	        	inputMap.put("MnthAccountNo", lStrAccNo);
			    	            	        	inputMap.put("MnthBasicPensionAmount", 0);
			    	            	        	inputMap.put("MnthDpPercent", 0);
			    	            	        	inputMap.put("MnthTiPercent", 0);
			    	            	        	inputMap.put("MnthDpPercentAmount", 0);
			    	            	        	inputMap.put("MnthTiPercentAmount", 0);
			    	            	        	inputMap.put("MnthMedicalAllowenceAmount", 0);
			    	            	        	inputMap.put("MnthCvpMonthlyAmount", 0);
			    	            	        	inputMap.put("MnthItCutAmount", 0);
			    	            	        	inputMap.put("MnthSpecialCutAmount", 0);
			    	            	        	inputMap.put("MnthPensionCutAmount", 0);
			    	            	        	inputMap.put("MnthRecoveryAmount", 0);
			    	            	        	//long lLngAmount = Math.round(lPendingArrear);
			    	            	        	inputMap.put("MnthNetPensionAmount", Math.round(lPendingArrear));
			    	            	        	inputMap.put("MnthAllnBf56", 0);
			    	            	        	inputMap.put("MnthAllnAf56", 0);
			    	            	        	inputMap.put("MnthAllnAf60", 0);
			    	            	        	inputMap.put("MnthPersonalPension", 0);
	        	    	    		        	inputMap.put("MnthIRAmount", 0);
	        	    	    		        	inputMap.put("MnthMOComm", Math.round(lMOComm));
	        	    	    		        	inputMap.put("MnthTIArrearsAmount", 0);
	        	    	    		        	inputMap.put("MnthOtherArrearsAmount", Math.round(lPendingArrear));
	        	    	    		        	inputMap.put("MnthOtherBenefit", 0);
	        	    	    		        	inputMap.put("MnthOMR", 0);
	        	    	    		        	inputMap.put("MnthYYYYMM", lIntDate);
	        	    	    		        	inputMap.put("MnthHeadcode", lStrHeadCode);
	        	    	    		        	inputMap.put("MnthFromPayDate", lPayStartDate);
	        	    	    		        	inputMap.put("MnthToPayDate", lTillPaidDate);
			    	            	        				    	            	        	
			        			        		lObjMonthlyPensionVO = lObjPensionBill.insertMonthlyDtls(inputMap);
			        			        		lLstMonthlyPensionBillVO.add(lObjMonthlyPensionVO);
				        					}
		        						}
			        					else
			        					{
			        						//monthly pension has to be computed for pensioner for this month....
				        					inputMap.put("FPFlag", "Y");
				                			inputMap.put("ArrComputeFlag", "N");
				        					inputMap.put("TrnPensionRqstHdrVO", lObjTrnPensionRqstHdrVO);
				        					inputMap.put("AccNo", lStrAccNo);
				        					inputMap.put("lPendingArrear", lPendingArrear);
				        					inputMap.put("Date", lStrDate);
				        					
				        					/* ROP 2006 is applied and calculate month is between 200601 and 200904 then current month calculate according to 2005 ROP and 
		        			        		 * 200904 onwards current month calculate according to 2006 ROP.  		 */	 
		        			             		
		        			        		if(lStrIsROPApplied != null && lStrIsROPApplied.equalsIgnoreCase("P") &&
		        			        			lStrROP_2006 != null && lStrROP_2006.equals("P") &&
		        			        			lIntDate.intValue() >= 200601 && lIntDate.intValue() < 200904)
		        			        		{
		        			        			inputMap.put("ROP_2006", null);
		        			        			
		        			        			ValidPcodeView lTemPcodeView = (ValidPcodeView) lObjValidPcodeVO.clone();
		        			        			lTemPcodeView.setBasicPensionAmount(new BigDecimal(lOldSixPayBasicAmt));
		        			        			lTemPcodeView.setFp1Amount(new BigDecimal(lOldSixPayFP1Amt));
		        			        			lTemPcodeView.setFp2Amount(new BigDecimal(lOldSixPayFP2Amt));
		        			        			inputMap.put("lObjValidPcode",lTemPcodeView);	
		        			        		}
		        			        		else if(lStrIsROPApplied != null && lStrIsROPApplied.equalsIgnoreCase("P") &&
		        			        				lStrROP_2006 != null && lStrROP_2006.equals("P") && 
		        			        				lIntDate.intValue() >= 200904)
		        			        		{
		        			        			inputMap.put("ROP_2006", "P");
		        			        			ValidPcodeView lTemPcodeView = (ValidPcodeView) lObjValidPcodeVO.clone();
		        			        			lTemPcodeView.setBasicPensionAmount(lPnsnrBasicAmt);
		        			        			lTemPcodeView.setFp1Amount(lPnsnrFP1Amt);
		        			        			lTemPcodeView.setFp2Amount(lPnsnrFP2Amt);
		        			        			inputMap.put("lObjValidPcode",lTemPcodeView);
		        			        		}
				        					
				        					inputMap = lObjPensionBill.getCurrMonthData(inputMap);
				        					
				        					lLstMonthlyPensionBillVO = (List<MonthlyPensionBillVO>) inputMap.get("lLstMonthlyPensionBillVO");
				        					
				        					if(lLstMonthlyPensionBillVO.size() > 0){
					        					lLstMonthlyPensionBillVO.get(lLstMonthlyPensionBillVO.size()-1).setFromDate(lPayStartDate);
					        					lLstMonthlyPensionBillVO.get(lLstMonthlyPensionBillVO.size()-1).setToDate(lTillPaidDate);
				        					}
				        					
				        					lLstPendingArrearVO = (List<TrnPensionArrearDtls>) inputMap.get("lLstInsertArrears");
				        					if(lLstPendingArrearVO != null && !lLstPendingArrearVO.isEmpty())
			    					        {
			    					        	for(int m = 0; m < lLstPendingArrearVO.size(); m++)
			    					        	{
			    					        		lLstInsertArrears.add(lLstPendingArrearVO.get(m));
			    					        	}
			    							}
			        					}
			        					
			        					List<TrnPensionHeldReasonDtls> lLstCurrHeldReasonDtls = (List<TrnPensionHeldReasonDtls>) inputMap.get("lLstCurrHeldReasonDtls");
			        					if(lLstCurrHeldReasonDtls!= null && !lLstCurrHeldReasonDtls.isEmpty())
			        					{
			        						//family pensioner details wasn't found for this pensioner....
			        						//hence add this list record sent in the list of held pensioners
			        						lLstHeldReasonDtls.add(lLstCurrHeldReasonDtls.get(0));
			        					}
		        					}
		        					else if("M".equals(lObjValidPcodeVO.getCalcType()))
		        					{
		        						//mode of calculation is manual thus take all basic values from tables and compute monthly pension
		        						//getting all required values from various tables...mst_pensioner_hdr, recovery, arrears, cuts
		        						//lObjMstPensionerHdrVO = lObjMstPensionerHdrDAO.getMstPensionerHdrDtls(lStrPnsnrCode);
		        						
	        							/*lLstAccNo = lObjMnthlyBillDAO.getAccountNo(lStrPnsnrCode, lStrBank, lStrBranch);
	        							if(!lLstAccNo.isEmpty()  && lLstAccNo.get(0) != null){
	        								lStrAccNo = lLstAccNo.get(0).toString();
	        							}
	        							else{
	        								lStrAccNo = "";
	        							}*/
		        						
		        						// Deep
		        						/*
		        						lRecoveryAmt =  lObjMnthlyBillDAO.getRecoveryDtls(lStrPnsnrCode, lStrRcvryFlag, lStrDate);
		        						lItCutDtls = lObjMnthlyBillDAO.getItCutDtls(lStrPnsnrCode, lStrDate);
		        						lArrearDtls = lObjMnthlyBillDAO.getArrearDtls(lStrPnsnrCode, lStrDate);
		        						*/
		        						if (inputMap.containsKey("lCutDtlMap") && inputMap.get("lCutDtlMap") != null) 
		        						{
		        							if (((Map) inputMap.get("lCutDtlMap")).containsKey(lStrPnsnrCode +"PT"+"~" + lStrDate))
		        								lPensionCutAmt += (Double)((Map) inputMap.get("lCutDtlMap")).get(lStrPnsnrCode +"PT"+"~" + lStrDate);
		        							if (((Map) inputMap.get("lCutDtlMap")).containsKey(lStrPnsnrCode +"IT"+"~" + lStrDate))
		        								lITCutAmt = (Double)((Map) inputMap.get("lCutDtlMap")).get(lStrPnsnrCode +"IT"+"~" + lStrDate);
		        							if (((Map) inputMap.get("lCutDtlMap")).containsKey(lStrPnsnrCode +"ST"+"~" + lStrDate))
		        								lSpecialCutAmt = (Double)((Map) inputMap.get("lCutDtlMap")).get(lStrPnsnrCode +"ST"+"~" + lStrDate);
		        							if (((Map) inputMap.get("lCutDtlMap")).containsKey(lStrPnsnrCode +"TempBenefit"+"~" + lStrDate))
		        								lOtherBenefits += (Double)((Map) inputMap.get("lCutDtlMap")).get(lStrPnsnrCode +"TempBenefit"+"~" + lStrDate);
		        							
		        							if (((Map) inputMap.get("lCutDtlMap")).containsKey(lStrPnsnrCode +"PP"))
		        								lPensionCutAmt += (Double)((Map) inputMap.get("lCutDtlMap")).get(lStrPnsnrCode +"PP");
		        							if (((Map) inputMap.get("lCutDtlMap")).containsKey(lStrPnsnrCode +"PermanentBenefit"))
		        								lOtherBenefits += (Double)((Map) inputMap.get("lCutDtlMap")).get(lStrPnsnrCode +"PermanentBenefit");
		        							if (((Map) inputMap.get("lCutDtlMap")).containsKey(lStrPnsnrCode +"PM"))
		        								lSpecialCutAmt += (Double)((Map) inputMap.get("lCutDtlMap")).get(lStrPnsnrCode +"PM");
		        						}
		        						if (inputMap.containsKey("lRcvDtlMap") && inputMap.get("lRcvDtlMap") != null) 
		        						{
		        							if (((Map) inputMap.get("lRcvDtlMap")).containsKey(lStrPnsnrCode + "~" + lStrDate))
		        								lRecoveryAmt = (Double)((Map) inputMap.get("lRcvDtlMap")).get(lStrPnsnrCode +"~" + lStrDate);
		        						}
		        						if (inputMap.containsKey("lArrDtlMap") && inputMap.get("lArrDtlMap") != null) 
		        						{
		        							if (((Map) inputMap.get("lArrDtlMap")).containsKey(lStrPnsnrCode + "~" + lStrDate))
		        								lOtherArr = (Double)((Map) inputMap.get("lArrDtlMap")).get(lStrPnsnrCode +"~" + lStrDate);
		        							if (((Map) inputMap.get("lArrDtlMap")).containsKey(lStrPnsnrCode +"TI"+"~" + lStrDate))
		        								lTIArr = (Double)((Map) inputMap.get("lArrDtlMap")).get(lStrPnsnrCode +"TI"+"~" + lStrDate);
		        						}
		        						
		        						// Deep
		        						
		        						
		        						//making a set of variables that we need...first get normal velues....for normal case for other conditions we will change later in computation
		        						// Getting the Value from TrnPensionRqstHdr... Start...
		        						
		        						/*if(lObjTrnPensionRqstHdrVO.getPpoNo()!= null){
		        							lStrPPONo = lObjTrnPensionRqstHdrVO.getPpoNo();
		        						}*/
		        						if(lObjValidPcodeVO.getBasicPensionAmount() != null)
		        						{
		        							lBasicPensionAmt = Double.parseDouble(lObjValidPcodeVO.getBasicPensionAmount().toString());
		        						}
		        						if(lObjValidPcodeVO.getDpPercent() != null)
		        						{
		        							lDPPer = Double.parseDouble(lObjValidPcodeVO.getDpPercent().toString());
		        						}
		        						if(lObjValidPcodeVO.getTiPercent() != null)
		        						{
		        							lTIPer = Double.parseDouble(lObjValidPcodeVO.getTiPercent().toString());
		        						}
		        						if(lObjValidPcodeVO.getMedicalAllowenceAmount() != null)
		        						{
		        							lMAAmt = Double.parseDouble(lObjValidPcodeVO.getMedicalAllowenceAmount().toString());
		        						}
		        						if(lObjValidPcodeVO.getRedBf11156() != null)
		        						{
		        							lAllnBf56 = Double.parseDouble(lObjValidPcodeVO.getOrgBf11156().toString());
		        						}
		        						if(lObjValidPcodeVO.getRedAf11156() != null)
		        						{
		        							lAllnAf56 = Double.parseDouble(lObjValidPcodeVO.getOrgAf11156().toString());
		        						}
		        						if(lObjValidPcodeVO.getPersonalPension() != null)
		        						{
		        							lPerPension = Double.parseDouble(lObjValidPcodeVO.getPersonalPension().toString());
		        						}
		        						if(lObjValidPcodeVO.getIr() != null)
		        						{
		        							lIR = Double.parseDouble(lObjValidPcodeVO.getIr().toString());
		        						}
		        						if(lObjValidPcodeVO.getDateOfDeath() == null )
		        						{
			        						if(lObjValidPcodeVO.getCvpMonthlyAmount() != null)
			        						{
			        							lCVPMonthlyAmt = Double.valueOf(lObjValidPcodeVO.getCvpMonthlyAmount().toString());
			        						}
		        						}
		        						
		        						//getting values from MstPensionerHdr
		        						/*if(lObjMstPensionerHdrVO != null)
		        				    	{
		        							//inputMap.put("MstPensionerHdrVO", lObjMstPensionerHdrVO);
		        				    		// Getting the Value from MstPensionerHdr... Start...
		        							if(lObjMstPensionerHdrVO.getMiddleName() != null){
		        								lStrPnsnerName = lObjMstPensionerHdrVO.getFirstName() + ' ' + 
		        				 				  				 lObjMstPensionerHdrVO.getMiddleName() + ' ' +
		        				 				  				 lObjMstPensionerHdrVO.getLastName();
		        							}
		        							else{
		        								lStrPnsnerName = lObjMstPensionerHdrVO.getFirstName() + ' ' + 
		        												 lObjMstPensionerHdrVO.getLastName();
		        							}
		        				    	}*/
		        						
		        						/*if(lItCutDtls != null && !lItCutDtls.isEmpty()) --
		        						{
		        							lITCutAmt = 0D;
		        							lPensionCutAmt = 0D;
		        							
		        							for(int i=0;i<lItCutDtls.size();i+=2)
		        							{
		        								if(lStrCutIT.equals(lItCutDtls.get(i)))
		        								{
		        									lITCutAmt = lITCutAmt + Double.parseDouble(lItCutDtls.get(i+1).toString());
		        								}
		        								if(lStrCutPT.equals(lItCutDtls.get(i))){
		        									lPensionCutAmt = lPensionCutAmt + Double.parseDouble(lItCutDtls.get(i+1).toString());
		        								}
		        								if(lStrCutPP.equals(lItCutDtls.get(i))){
		        									lPensionCutAmt = lPensionCutAmt + Double.parseDouble(lItCutDtls.get(i+1).toString());
		        								}
		        								if(lStrCutST.equals(lItCutDtls.get(i))){
		        									lSpecialCutAmt = lSpecialCutAmt + Double.parseDouble(lItCutDtls.get(i+1).toString());
		        								}
		        								if(lStrCutPerBen.equals(lItCutDtls.get(i))){
		        									lOtherBenefits = lOtherBenefits + Double.parseDouble(lItCutDtls.get(i+1).toString());
		        								}
		        								if(lStrCutTmpBen.equals(lItCutDtls.get(i))){
		        									lOtherBenefits = lOtherBenefits + Double.parseDouble(lItCutDtls.get(i+1).toString());
		        								}
		        							}
		        						}*/
		        						
		        						/*if(lArrearDtls != null && !lArrearDtls.isEmpty())
		        						{
		        							for(int i=0;i<lArrearDtls.size();i+=2)
		        							{
		        								if(lStrArrTI.equals(lArrearDtls.get(i)))
		        								{
		        									lTIArr = lTIArr + Double.parseDouble(lArrearDtls.get(i+1).toString());
		        								}
		        								if(lStrArrMA.equals(lArrearDtls.get(i)))
		        								{
		        									lMAArr = lMAArr + Double.parseDouble(lArrearDtls.get(i+1).toString());
		        								}
		        								if(lStrArrPension.equals(lArrearDtls.get(i)))
		        								{
		        									lPensionArr = lPensionArr + Double.parseDouble(lArrearDtls.get(i+1).toString());
		        								}
		        								if(lStrArrFP1.equals(lArrearDtls.get(i)))
		        								{
		        									lFP1Arr = lFP1Arr + Double.parseDouble(lArrearDtls.get(i+1).toString());
		        								}
		        								if(lStrArrFP2.equals(lArrearDtls.get(i)))
		        								{
		        									lFP2Arr = lFP2Arr + Double.parseDouble(lArrearDtls.get(i+1).toString());
		        								}
		        							}
		        							lOtherArr = lMAArr + lPensionArr + lFP1Arr + lFP2Arr;
		        							lArrearAmt = lTIArr + lOtherArr;
		        						}
*/		        						lArrearAmt = lTIArr + lOtherArr;
		        						Long BasicPensionAmt = Math.round(lBasicPensionAmt);
		        						Long PensionCutAmt = Math.round(lPensionCutAmt);	  
		        						Long MAAmt = Math.round(lMAAmt);
		        						Long CVPMonthlyAmt = Math.round(lCVPMonthlyAmt);	 
		        						Long ITCutAmt = Math.round(lITCutAmt);
		        						Long SpecialCutAmt = Math.round(lSpecialCutAmt);	 
		        						Long OtherBenefits = Math.round(lOtherBenefits);
		        						Long OMR = Math.round(lOMR);	 
		        						Long RecoveryAmt = Math.round(lRecoveryAmt);
		        						Long ArrearAmt = Math.round(lArrearAmt);	 
		        						Long PerPension = Math.round(lPerPension);
		        						Long IR = Math.round(lIR);	 
		        						Double NetPensionAmt = 0D;
		        						Double DPPerAmt = 0D;
		        						Double TIPerAmt = 0D;
		        						Long MOComm = 0L;
		        						
		        						// Net Pension Amount Calculation.
		        				    	NetPensionAmt = Math.rint(BasicPensionAmt - PensionCutAmt);	//	 NetPension = Basic - PensionCut;
		        				    	DPPerAmt += Math.round(NetPensionAmt * lDPPer / 100);
		        				    	DPPerAmt = Double.valueOf(lDblFrmt.format(DPPerAmt));  // Rounding a Double at 2 decimal point
		        				    	NetPensionAmt = NetPensionAmt + DPPerAmt;			// 	 NetPension = NetPension + DPAmt;
		        				    	//TIPerAmt += Math.round(NetPensionAmt * lTIPer / 100);
		        				    	//TIPerAmt = Double.valueOf(lDblFrmt.format(TIPerAmt)); // Rounding a Double at 2 decimal point
		        				    	// Upper Rounding done according to 6th pay.
		        				    	if("P".equals(lStrROP_2006))
		        				    	{
		        				    		TIPerAmt += Math.ceil(NetPensionAmt * lTIPer / 100);
		        				    	}
		        				    	else //// Normal Rounding done according to 5th & 4th pay.
		        				    	{
		        				    		TIPerAmt += Math.round(NetPensionAmt * lTIPer / 100);
		        				    	}
		        				    	TIPerAmt = Double.valueOf(lDblFrmt.format(TIPerAmt)); // Rounding a Double at 2 decimal point
		        				    	NetPensionAmt = NetPensionAmt + TIPerAmt;			// 	 NetPension = NetPension + TIAmt;
		        				    	NetPensionAmt = NetPensionAmt + MAAmt;				// 	 NetPension = NetPension + Medical Allowance;
		        				    	NetPensionAmt = NetPensionAmt - CVPMonthlyAmt;		// 	 NetPension = NetPension - CVPMonthlyAmt ;
		        				    	NetPensionAmt = NetPensionAmt - ITCutAmt;			// 	 NetPension = NetPension - IT Cut;
		        				    	NetPensionAmt = NetPensionAmt - SpecialCutAmt;		//   NetPension = NetPension - Special Cut;
		        				    	NetPensionAmt = NetPensionAmt + OtherBenefits;		//   NetPension = NetPension + OtherBenefits;
		        				    	NetPensionAmt = NetPensionAmt + OMR;				//   NetPension = NetPension + OMR;
		        				    	NetPensionAmt = NetPensionAmt - RecoveryAmt;		// 	 NetPension = NetPension - Recovery / Deduction;
		        				    	NetPensionAmt = NetPensionAmt + ArrearAmt;			//   NetPension = NetPension + ArrearAmt ;
		        				    	NetPensionAmt = NetPensionAmt + PerPension;			//   NetPension = NetPension + PersonalPension ;
		        				    	NetPensionAmt = NetPensionAmt + IR;					//   NetPension = NetPension + IRAmt ;
		        						
		        						// 
		        				    	
		        				    	
		        				    	
		        				    	if(lStrScheme.equals(lStrMoneyOrder))
		        				    	{
		        							MOComm = Math.round(NetPensionAmt * 5 / 100);
		        						
			        				    	if("P".equals(lStrROP_2006))
			        				    	{
			        				    		if(BasicPensionAmt <= 3500 )
				        				    	{
				        				    		NetPensionAmt = NetPensionAmt + MOComm;
				        				    	}
			        				    	}
			        				    	else if(BasicPensionAmt <= 2500 )
			        				    	{
			        				    		NetPensionAmt = NetPensionAmt + MOComm;
			        				    	}
		        				    	}
	        						
		        						Long ReducedPen = Math.round(BasicPensionAmt - PensionCutAmt + DPPerAmt - CVPMonthlyAmt);
		        						Long allnBf56 = Math.round(lAllnBf56);
		        						Long allnAf56 = Math.round(lAllnAf56);
		        						Long allnAf60 = ReducedPen - allnBf56 - allnAf56;
		        						
		        						lObjMonthlyPensionVO = new MonthlyPensionBillVO();
		        						Long TIArr = Math.round(lTIArr);
		        			        	Long TmpOthArr = ArrearAmt - TIArr;
		        			    		
		        			        	if(BasicPensionAmt > 0){
			        			        	inputMap.put("MnthPpoNo", lStrPPONo);
			        			        	inputMap.put("MnthPnsnrCode", lStrPnsnrCode);
			        			        	inputMap.put("MnthPensionerName", lStrPnsnerName);
			        			        	inputMap.put("MnthAccountNo", lStrAccNo);
			        			        	inputMap.put("MnthBasicPensionAmount", BasicPensionAmt);
			        			        	inputMap.put("MnthDpPercent", lDPPer.toString().length() != 0 ? lDPPer.toString() : "0");
			        			        	inputMap.put("MnthTiPercent", lTIPer.toString().length() != 0 ? lTIPer.toString() : "0");
			        			        	inputMap.put("MnthDpPercentAmount", DPPerAmt);
			        			        	inputMap.put("MnthTiPercentAmount", TIPerAmt);
			        			        	inputMap.put("MnthMedicalAllowenceAmount", MAAmt);
			        			        	inputMap.put("MnthCvpMonthlyAmount", CVPMonthlyAmt);
			        			        	inputMap.put("MnthTIArrearsAmount", TIArr);
			        			        	inputMap.put("MnthOtherArrearsAmount", TmpOthArr);
			        			        	inputMap.put("MnthItCutAmount", ITCutAmt);
			        			        	inputMap.put("MnthSpecialCutAmount", SpecialCutAmt);
			        			        	inputMap.put("MnthOtherBenefit", OtherBenefits);
			        			        	inputMap.put("MnthOMR", OMR);
			        			        	inputMap.put("MnthPensionCutAmount", PensionCutAmt);
			        			        	inputMap.put("MnthRecoveryAmount", RecoveryAmt);
			        			        	//long lLngAmount = Math.round(NetPensionAmt);
			        			        	inputMap.put("MnthNetPensionAmount", NetPensionAmt);
			        			        	inputMap.put("MnthAllnBf56", allnBf56);
			        			        	inputMap.put("MnthAllnAf56", allnAf56);
			        			        	inputMap.put("MnthAllnAf60", allnAf60);
			        			        	inputMap.put("MnthPersonalPension", PerPension);
			        			        	inputMap.put("MnthIRAmount", IR);
			        			        	inputMap.put("MnthMOComm", MOComm);
			        			        	inputMap.put("MnthYYYYMM", lIntDate);
	    	    	    		        	inputMap.put("MnthHeadcode", lStrHeadCode);
	    	    	    		        	inputMap.put("MnthFromPayDate", lPayStartDate);
	    	    	    		        	inputMap.put("MnthToPayDate", lTillPaidDate);		        			        	
	    	    	    		        	inputMap.put("MnthADPAmount", 0);
	    	    	    		        			        			        	
		        			        		lObjMonthlyPensionVO = lObjPensionBill.insertMonthlyDtls(inputMap);
		        			        		lLstMonthlyPensionBillVO.add(lObjMonthlyPensionVO);
		        			        	}
		        					}
		        				}
		        			}
	        			}
        			}
        		/*	else
        			{
        				//status is not continue....hence add this pensioner in held list
        				String lTempPcode = lStrPnsnrCode;
            			String lTempPPOno = lStrPPONo;
            			String lReason = "Status is "+lObjValidPcodeVO.getStatus();
            			
            			TrnPensionHeldReasonDtls lObjHeldReasonDtls = insertHeldReasonDtls(lTempPcode,lTempPPOno,lReason,lStrDate);
            			lLstHeldReasonDtls.add(lObjHeldReasonDtls);
        			}*/
	        	}
        	
        	}
        	inputMap.put("lMapROPPCode",lMapROPPCode);
        	if(lLstMonthlyPensionBillVO.size() > 0)
        	{
        		List lTillPaidDateLst = (List) inputMap.get("TillPaidDateLst");
            	Map<String,Object> lMapTillPaidDate = new HashMap<String, Object>();
            	lMapTillPaidDate.put("PensionRqstID",lObjValidPcodeVO.getPensionRequestId());
            	lMapTillPaidDate.put("LstPaidDate",lTillPaidDate);
        		lMapTillPaidDate.put("LstPaidAmt",lLstMonthlyPensionBillVO.get(lLstMonthlyPensionBillVO.size()-1).getNetPensionAmount());
        		lTillPaidDateLst.add(lMapTillPaidDate);
        		inputMap.put("TillPaidDateLst", lTillPaidDateLst);
        	}			
        	
        	lArrComputeFlag = "N";
        	inputMap.put("ArrComputeFlag", lArrComputeFlag);
        	inputMap.put("lLstMonthlyPensionBillVO", lLstMonthlyPensionBillVO);
        	inputMap.put("lLstInsertArrears", lLstInsertArrears);
        	inputMap.put("lLstMonthlyPensionArrearVO", lLstMonthlyPensionArrearVO);
        	inputMap.put("lLstHeldReasonDtls", lLstHeldReasonDtls);
        }
        catch(Exception e){
			throw(e);
		}
        return inputMap;
	}

	
	/**
	 * Method to generate vo for held reason 
	 * @param inputMap
	 * @return {@link TrnPensionHeldReasonDtls}
	 * @throws Exception
	 */
	/*public TrnPensionHeldReasonDtls insertHeldReasonDtls(String penCode, String ppoNo, String reason, String ForMonth) throws Exception
    {
		TrnPensionHeldReasonDtls lObjHeldReasonDtlsVo = new TrnPensionHeldReasonDtls();
      
        try{
        	Date lReasonDate = new SimpleDateFormat("dd/MM/yyyy").parse("01/"+ForMonth.substring(4,6)+"/"+ForMonth.substring(0,4));
        	
        	lObjHeldReasonDtlsVo.setPensionerCode(penCode);
        	lObjHeldReasonDtlsVo.setPpoNo(ppoNo);
        	lObjHeldReasonDtlsVo.setReason(reason);
        	lObjHeldReasonDtlsVo.setReasonFlag("M");
        	lObjHeldReasonDtlsVo.setReasonDate(lReasonDate);
        }
        catch (Exception e){
            gLogger.error("Error is :: " + e,e);
            throw(e);
        }        
        return lObjHeldReasonDtlsVo;
    }*/
	
	 /**
     * Method to set Session variables
     * @param inputMap
     */
    private void setSessionInfo(Map inputMap)
    {
        gLngLangId = SessionHelper.getLangId(inputMap);
        gLngPostId = SessionHelper.getPostId(inputMap);
        gLngUserId = SessionHelper.getUserId(inputMap);
        gLngDBId = SessionHelper.getDbId(inputMap);
        gDate = DBUtility.getCurrentDateFromDB();
    }
    
    /**
     * Return No of Day for the given month.
     * @param lYYYYMM
     * @return {@link Integer}
     */
    public Integer getDaysOfMonth(Integer lYYYYMM)
    {
        Integer YYYY = Integer.parseInt(lYYYYMM.toString().substring(0,4));
        Integer MM = Integer.parseInt(lYYYYMM.toString().substring(4,6));
        Calendar cal = new GregorianCalendar(YYYY, (MM-1), 1);
        Integer days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        return days;
    }  
}
