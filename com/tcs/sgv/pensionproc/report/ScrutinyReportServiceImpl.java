package com.tcs.sgv.pensionproc.report;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAO;
import com.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAOImpl;
import com.tcs.sgv.pensionproc.dao.TrnPnsnprocFamilydtlsDAO;
import com.tcs.sgv.pensionproc.dao.TrnPnsnprocFamilydtlsDAOImpl;
import com.tcs.sgv.pensionproc.dao.TrnPnsnprocNomineedtlsDAO;
import com.tcs.sgv.pensionproc.dao.TrnPnsnprocNomineedtlsDAOImpl;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcAdvnceBal;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcInwardPension;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcPnsnCalc;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcPnsnrDtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocAvgPayCalc;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocEventdtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocFamilydtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocNomineedtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocPnsnrpay;

public class ScrutinyReportServiceImpl extends ServiceImpl 
{
	private final static Logger gLogger = Logger.getLogger(ScrutinyReportServiceImpl.class);

	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	Long gLngLangId = null;
	
	public ResultObject generateScrutinyReport(Map inputMap)
	{
		StringBuilder sb= new StringBuilder();
		ScrutinyReportPrintUtil sr = null;
		
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request=(HttpServletRequest)inputMap.get("requestObj");
		ServiceLocator serv=(ServiceLocator)inputMap.get("serviceLocator");
		
		Long lLngInwardId = null,pensionerId = null;
	
		gLngLangId = SessionHelper.getLangId(inputMap);
		try 
		{
			String[] inwardNo=request.getParameter("InwardNo").split(",");
			Integer len = inwardNo.length;			
			Long locId=SessionHelper.getLocationId(inputMap);
			
			TrnPnsnProcInwardPensionDAO lObjTrnPnsnProcInwardPensionDAO = null;
			
			for(Integer i=0;i<len;i++)
			{				
				lLngInwardId = Long.valueOf(inwardNo[i]);
				
				lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcInwardPension.class, serv.getSessionFactory());
				TrnPnsnProcInwardPension lObjTrnPnsnProcInwardPensionVO = (TrnPnsnProcInwardPension) lObjTrnPnsnProcInwardPensionDAO.read(lLngInwardId);
				
				lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcPnsnrDtls.class, serv.getSessionFactory());
				TrnPnsnProcPnsnrDtls lObjTrnPnsnProcPnsnrDtlsVO = lObjTrnPnsnProcInwardPensionDAO.getPnsnrDtlsVO(lLngInwardId);
				
				List lLstEmpGpfOrDcpsAccNo = lObjTrnPnsnProcInwardPensionDAO.getEmpGpfOrDcpsAccNo(lObjTrnPnsnProcInwardPensionVO.getSevaarthId());
				
				TrnPnsnprocPnsnrpay lObjTrnPnsnprocPnsnrpayVO = lObjTrnPnsnProcInwardPensionDAO.getPnsnrPayVO(lLngInwardId);

				List<TrnPnsnprocAvgPayCalc> lLstTrnPnsnprocAvgPayCalcVO = lObjTrnPnsnProcInwardPensionDAO.getPensionCaseAvgPayCalcDtls(lLngInwardId);
				
				List<TrnPnsnprocEventdtls> lLstTrnPnsnprocEventdtls = lObjTrnPnsnProcInwardPensionDAO.getPensionCaseEventDtls(lLngInwardId);
				
				TrnPnsnProcPnsnCalc lObjTrnPnsnProcPnsnCalcVO = lObjTrnPnsnProcInwardPensionDAO.getPnsnCalcVO(lLngInwardId);
				
				TrnPnsnprocFamilydtlsDAO lObjTrnPnsnprocFamilydtlsDAO = new TrnPnsnprocFamilydtlsDAOImpl(TrnPnsnprocFamilydtls.class, serv.getSessionFactory());
				List<TrnPnsnprocFamilydtls> lLstPnsnrFamilyDtls = lObjTrnPnsnprocFamilydtlsDAO.getListOfPnsnrFamilyDtls(lLngInwardId);
				
				TrnPnsnprocNomineedtlsDAO lObjTrnPnsnprocNomineedtlsDAO = new TrnPnsnprocNomineedtlsDAOImpl(TrnPnsnprocNomineedtls.class, serv.getSessionFactory());
				List<TrnPnsnprocNomineedtls> lLstPnsnrNomineeDtls = lObjTrnPnsnprocNomineedtlsDAO.getPnsnrNomineeDtls(lLngInwardId);
			 
				List<TrnPnsnProcAdvnceBal> lLstTrnPnsnProcAdvnceBal = lObjTrnPnsnProcInwardPensionDAO.getPensionCaseAdvnceBalDtls(lLngInwardId);
				
				sr = new ScrutinyReportPrintUtil(lObjTrnPnsnProcInwardPensionVO,lObjTrnPnsnProcPnsnrDtlsVO,
												 lObjTrnPnsnprocPnsnrpayVO,lLstEmpGpfOrDcpsAccNo,lLstTrnPnsnprocAvgPayCalcVO,
												 lLstTrnPnsnprocEventdtls,lObjTrnPnsnProcPnsnCalcVO,lLstPnsnrFamilyDtls,
												 lLstPnsnrNomineeDtls,lLstTrnPnsnProcAdvnceBal,serv,gLngLangId);
				
				sb.append(sr.generateScrutinyTemplate()).append("^");
			}
			
			if(sb != null)
			{
				inputMap.put("reportData",sb);				
			}
			
			resObj.setResultValue(inputMap);
			resObj.setViewName("scrutinyReport");	
		} 
		catch(Exception e) 
		{
			gLogger.error(" Error in generateScrutinyReport :" + e, e);
		}
		
		return resObj;
	}
	
}
