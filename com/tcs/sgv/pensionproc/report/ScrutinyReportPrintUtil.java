package com.tcs.sgv.pensionproc.report;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.pensionproc.dao.CommonPensionDAO;
import com.tcs.sgv.pensionproc.dao.CommonPensionDAOImpl;
import com.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAO;
import com.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAOImpl;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcAdvnceBal;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcInwardPension;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcPnsnCalc;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcPnsnrDtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocAvgPayCalc;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocEventdtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocFamilydtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocNomineedtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocPnsnrpay;

public class ScrutinyReportPrintUtil 
{
	private final static Logger gLogger = Logger.getLogger(ScrutinyReportPrintUtil.class);
	
	public static ResourceBundle labelsBundle = null;
	
	public static ResourceBundle scrutinylabelsBundle = null;

	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	String sysdate = simpleDateFormat.format(new Date());
	
	Date date = null;

	private StringBuilder sb = null;
	
	
	TrnPnsnProcInwardPension lObjTrnPnsnProcInwardPensionVO = null;
	TrnPnsnProcPnsnrDtls lObjTrnPnsnProcPnsnrDtlsVO = null;
	TrnPnsnprocPnsnrpay lObjTrnPnsnprocPnsnrpayVO = null;
	List lLstEmpGpfOrDcpsAccNo = null;
	List<TrnPnsnprocAvgPayCalc> lLstTrnPnsnprocAvgPayCalcVO = null;
	List<TrnPnsnprocEventdtls> lLstTrnPnsnprocEventdtls = null;
	TrnPnsnProcPnsnCalc lObjTrnPnsnProcPnsnCalcVO = null;
	List<TrnPnsnprocFamilydtls> lLstPnsnrFamilyDtls = null;
	List<TrnPnsnprocNomineedtls> lLstPnsnrNomineeDtls = null;
	List<TrnPnsnProcAdvnceBal> lLstTrnPnsnProcAdvnceBal = null;
	Long gLngLangId = null;
	
	
	ServiceLocator servLoc = null;
	
	ScrutinyReportPrintUtil(
							
							TrnPnsnProcInwardPension lObjTrnPnsnProcInwardPensionVO,
							TrnPnsnProcPnsnrDtls lObjTrnPnsnProcPnsnrDtlsVO,
							TrnPnsnprocPnsnrpay lObjTrnPnsnprocPnsnrpayVO,
							List lLstEmpGpfOrDcpsAccNo,		
							List<TrnPnsnprocAvgPayCalc> lLstTrnPnsnprocAvgPayCalcVO,
							List<TrnPnsnprocEventdtls> lLstTrnPnsnprocEventdtls,
							TrnPnsnProcPnsnCalc lObjTrnPnsnProcPnsnCalcVO,
							List<TrnPnsnprocFamilydtls> lLstPnsnrFamilyDtls,
							List<TrnPnsnprocNomineedtls> lLstPnsnrNomineeDtls,
							List<TrnPnsnProcAdvnceBal> lLstTrnPnsnProcAdvnceBal,
							ServiceLocator servLoc,Long gLngLangId
							)
	{	
		this.lObjTrnPnsnProcInwardPensionVO = lObjTrnPnsnProcInwardPensionVO;
		this.lObjTrnPnsnProcPnsnrDtlsVO = lObjTrnPnsnProcPnsnrDtlsVO;
		this.lObjTrnPnsnprocPnsnrpayVO = lObjTrnPnsnprocPnsnrpayVO;
		this.lLstEmpGpfOrDcpsAccNo = lLstEmpGpfOrDcpsAccNo;	
		this.lLstTrnPnsnprocAvgPayCalcVO = lLstTrnPnsnprocAvgPayCalcVO;
		this.lLstTrnPnsnprocEventdtls = lLstTrnPnsnprocEventdtls;
		this.lObjTrnPnsnProcPnsnCalcVO = lObjTrnPnsnProcPnsnCalcVO;
		this.lLstPnsnrFamilyDtls = lLstPnsnrFamilyDtls;
		this.lLstPnsnrNomineeDtls = lLstPnsnrNomineeDtls;
		this.lLstTrnPnsnProcAdvnceBal = lLstTrnPnsnProcAdvnceBal;
		this.servLoc = servLoc;
		this.gLngLangId = gLngLangId;
	}
		
	public StringBuilder generateScrutinyTemplate()
	{
		
		String lStrInwardNo = null;
		String lStrInwardDate = null;
		String lStrSevaarthId = null;
		String lStrPPONo = null;
		String lStrPensionType = null;
		String lStrPensionerName = null;
		String lStrGpfOrDcpsAccNo = null;
		Character lChGpfOrDcps = null;
		String lStrDesignation = null;
		String lStrPensionerType = null;
		Object lObjArrAccNo[] = null;
		String lStrFieldDepartment  = null;
		String lStrHeadOfOffice  = null;
		String lStrOfficeAdd  = null;
		String lStrResiAdd  = null;
		
		List lLstPayScale = null;
		String lStrPayScale = "";
		Long lLngPayCom = null;
		
		String lStrDOB = null;
		String lStrDOJ = null;
		String lStrDOR = "";
		
		
	    
		sb = new StringBuilder();
		
		labelsBundle = ResourceBundle.getBundle("resources/pensionproc/PensionCaseLabels");
		scrutinylabelsBundle = ResourceBundle.getBundle("resources/pensionproc/ScrutinyReportLabels");
		try
		{
			TrnPnsnProcInwardPensionDAO lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcInwardPension.class, servLoc.getSessionFactory());
			CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(servLoc.getSessionFactory());
			
			lStrInwardNo = lObjTrnPnsnProcInwardPensionVO.getInwardNo();
			lStrInwardDate = simpleDateFormat.format(lObjTrnPnsnProcInwardPensionVO.getInwardDate());
			lStrPPONo = lObjTrnPnsnProcInwardPensionVO.getPpoNo();			
			lStrPensionType = labelsBundle.getString("PNSNTYPE."+lObjTrnPnsnProcInwardPensionVO.getPensionType());
			lStrPensionerName = lObjTrnPnsnProcPnsnrDtlsVO.getPnsnrName();
			lStrSevaarthId = lObjTrnPnsnProcInwardPensionVO.getSevaarthId();
			
			if(!lLstEmpGpfOrDcpsAccNo.isEmpty()){
				lObjArrAccNo = (Object[]) lLstEmpGpfOrDcpsAccNo.get(0);
				lStrGpfOrDcpsAccNo = (String) lObjArrAccNo[0];
				lChGpfOrDcps = (Character) lObjArrAccNo[1];
			}
			lStrDesignation = lObjTrnPnsnProcPnsnrDtlsVO.getDesignation();
			lStrPensionerType = lObjTrnPnsnProcInwardPensionVO.getPensionerType();
			
			String lStrDisCode = lObjTrnPnsnProcPnsnrDtlsVO.getPnsnrAddrDistCode();
			Long lLngDisCode = 0L;
			if(lStrDisCode != null && lStrDisCode != "")
				lLngDisCode = Long.parseLong(lStrDisCode);
			
			String lStrOfficeDisCode = lObjTrnPnsnProcPnsnrDtlsVO.getOfficeDistCode();
			Long lLngOfficeDisCode = 0L;
			if(lStrOfficeDisCode != null && lStrOfficeDisCode != "")
				lLngOfficeDisCode = Long.parseLong(lStrOfficeDisCode);
			
			lStrFieldDepartment = lObjTrnPnsnProcInwardPensionDAO.getDeptName(lObjTrnPnsnProcPnsnrDtlsVO.getDepartmentId());
			lStrHeadOfOffice = lObjTrnPnsnProcInwardPensionDAO.getDeptName(lObjTrnPnsnProcPnsnrDtlsVO.getHooId());
			
			lStrResiAdd = (lObjTrnPnsnProcPnsnrDtlsVO.getPnsnrAddrFlat() == null)?"":lObjTrnPnsnProcPnsnrDtlsVO.getPnsnrAddrFlat() + " ";
			lStrResiAdd = lStrResiAdd +	((lObjTrnPnsnProcPnsnrDtlsVO.getPnsnrAddrRoad() == null)?"":lObjTrnPnsnProcPnsnrDtlsVO.getPnsnrAddrRoad()) + " ";			
			lStrResiAdd = lStrResiAdd +	((lObjTrnPnsnProcPnsnrDtlsVO.getPnsnrAddrArea() == null)?"":lObjTrnPnsnProcPnsnrDtlsVO.getPnsnrAddrArea()) + " ";
			lStrResiAdd = lStrResiAdd + ((lObjTrnPnsnProcPnsnrDtlsVO.getPnsnrAddrPincode() == null)?"":lObjTrnPnsnProcPnsnrDtlsVO.getPnsnrAddrPincode()) + " ";
			
			lStrResiAdd = lStrResiAdd +	lObjCommonPensionDAO.getDistrictNameFromDistrictCode(lLngDisCode, gLngLangId)+ " "
							+ lObjCommonPensionDAO.getStateNameFromStateCode(lObjTrnPnsnProcPnsnrDtlsVO.getPnsnrAddrStateCode(), gLngLangId);
			
			lStrOfficeAdd = (lObjTrnPnsnProcPnsnrDtlsVO.getOfficeFlat() == null)?"":lObjTrnPnsnProcPnsnrDtlsVO.getOfficeFlat() + " ";
			lStrOfficeAdd =  lStrOfficeAdd + ((lObjTrnPnsnProcPnsnrDtlsVO.getOfficeRoad() == null)?"":lObjTrnPnsnProcPnsnrDtlsVO.getOfficeRoad())+ " ";
			lStrOfficeAdd =  lStrOfficeAdd + ((lObjTrnPnsnProcPnsnrDtlsVO.getOfficeArea()  == null)?"": lObjTrnPnsnProcPnsnrDtlsVO.getOfficeArea()) + " ";
			lStrOfficeAdd =  lStrOfficeAdd + ((lObjTrnPnsnProcPnsnrDtlsVO.getOfficePincode() == null)?"":lObjTrnPnsnProcPnsnrDtlsVO.getOfficePincode())+ " "; 
			lStrOfficeAdd =  lStrOfficeAdd + lObjCommonPensionDAO.getDistrictNameFromDistrictCode(lLngOfficeDisCode, gLngLangId)+ " "
							+ lObjCommonPensionDAO.getStateNameFromStateCode(lObjTrnPnsnProcPnsnrDtlsVO.getOfficeStateCode(), gLngLangId);

			lLstPayScale = lObjTrnPnsnProcInwardPensionDAO.getPayScaleDescFromScaleId(lObjTrnPnsnprocPnsnrpayVO.getLastPayScale());
			
			if(!lLstPayScale.isEmpty()){
				Object lObjArrPayScale[] = (Object[]) lLstPayScale.get(0);
				lStrPayScale = (String) lObjArrPayScale[0];
				lLngPayCom = (Long) lObjArrPayScale[1];
			}
			
			lStrDOB = simpleDateFormat.format(lObjTrnPnsnProcPnsnrDtlsVO.getBirthDate());
			lStrDOJ = simpleDateFormat.format(lObjTrnPnsnProcPnsnrDtlsVO.getJoiningDate());
			lStrDOR = simpleDateFormat.format(lObjTrnPnsnProcPnsnrDtlsVO.getRetirementDate());
			
			BigInteger lBigIntProvisionalAmt = lObjTrnPnsnProcInwardPensionDAO.getProvisionalPensionSum(lObjTrnPnsnProcInwardPensionVO.getInwardPensionId());
			String lStrLocationName = lObjTrnPnsnProcInwardPensionDAO.getTresuryNameFormDDOCode(lObjTrnPnsnProcInwardPensionVO.getDdoCode()+"");
			
			sb.append("<table  width='100%'><tr><td>");
				sb.append("<table  width='100%'>");			
				sb.append("<tr>");
					sb.append("<td  width='25%'>");
						sb.append("<b>"+scrutinylabelsBundle.getString("label1")+"</b>" );
					sb.append("</td>");
					sb.append("<td  width='30%'>");									
						sb.append(lStrInwardNo);
					sb.append("</td>");					
					sb.append("<td  width='15%'>");
						sb.append("<b>Inward Date</b>");										
					sb.append("</td>");
					sb.append("<td  width='30%'>");
						sb.append(lStrInwardDate);						
					sb.append("</td>");
				sb.append("</tr>");
				
				sb.append("<tr>");
					sb.append("<td>");
						sb.append("<b>"+scrutinylabelsBundle.getString("label1.2")+"</b>");				
					sb.append("</td>");
					sb.append("<td>");						
						sb.append((lStrSevaarthId== null)?"":lStrSevaarthId);
					sb.append("</td>");
					sb.append("<td>");
						sb.append("<b>"+scrutinylabelsBundle.getString("label3")+"</b>");				
					sb.append("</td>");
					sb.append("<td>");
						sb.append((lStrPensionerName == null)?"":lStrPensionerName);									
					sb.append("</td>");
				sb.append("</tr>");
				
				sb.append("<tr>");
					sb.append("<td>");
						sb.append("<b>"+scrutinylabelsBundle.getString("label5")+"</b>" );
					sb.append("</td>");
					sb.append("<td>");						
						sb.append((lStrPensionerType == null)?"":lStrPensionerType);
					sb.append("</td>");
					sb.append("<td>");
						sb.append("<b>"+scrutinylabelsBundle.getString("label2.2")+"</b>");
					sb.append("</td>");
					sb.append("<td>");
						sb.append(sysdate);				
					sb.append("</td>");					
				sb.append("</tr>");
				
				sb.append("<tr>");
					sb.append("<td>");
						sb.append("<b>"+scrutinylabelsBundle.getString("label4")+"</b>");				
					sb.append("</td>");
					sb.append("<td>");
						sb.append((lStrDesignation == null)?"":lStrDesignation);						
					sb.append("</td>");
					sb.append("<td>");
						if(lChGpfOrDcps.equals("Y"))
							sb.append("<b>DCPS No</b>");
						else
							sb.append("<b>GPF No</b>");			
					sb.append("</td>");
					sb.append("<td>");
							sb.append((lStrGpfOrDcpsAccNo == null)?"":lStrGpfOrDcpsAccNo);
					sb.append("</td>");
				sb.append("</tr>");
				
				sb.append("<tr>");
					sb.append("<td>");
						sb.append("<b>"+scrutinylabelsBundle.getString("label2")+"</b>");				
					sb.append("</td>");
					sb.append("<td>");						
						sb.append(lStrPensionType);
					sb.append("</td>");
					sb.append("<td>");						
						sb.append("<b>"+scrutinylabelsBundle.getString("label1.1")+"</b>");
					sb.append("</td>");
					sb.append("<td>");						
						sb.append((lStrPPONo== null)?"":lStrPPONo);
					sb.append("</td>");
				sb.append("</tr>");
				
				sb.append("<tr>");
					sb.append("<td>");
						sb.append("<b>"+scrutinylabelsBundle.getString("label6")+"</b>");
					sb.append("</td>");
					sb.append("<td>");						
						sb.append(lStrFieldDepartment);
					sb.append("</td>");
					sb.append("<td>");						
						sb.append("<b>"+scrutinylabelsBundle.getString("label7")+"</b>");
					sb.append("</td>");
					sb.append("<td>");						
						sb.append(lStrHeadOfOffice);
					sb.append("</td>");
				sb.append("</tr>");	
				
				sb.append("</table>");
				
				sb.append("<table  width='100%'>");
				
					sb.append("<tr>");
						sb.append("<td  width='25%'>");
							sb.append("<b>"+scrutinylabelsBundle.getString("label8")+"</b>");
						sb.append("</td>");
						sb.append("<td>");
							sb.append(lStrOfficeAdd);
						sb.append("</td  width='25%'>");
					sb.append("</tr>");
					
					sb.append("<tr>");
						sb.append("<td>");
							sb.append("<b>"+scrutinylabelsBundle.getString("label9")+"</b>");
						sb.append("</td>");
						sb.append("<td>");
							sb.append(lStrResiAdd);
						sb.append("</td>");
					sb.append("</tr>");
					
					sb.append("<tr>");
						sb.append("<td>");
							sb.append("<b>"+scrutinylabelsBundle.getString("label10")+"</b>");
						sb.append("</td>");
						sb.append("<td>");
							sb.append(lStrPayScale);
						sb.append("</td>");
					sb.append("</tr>");
					
				sb.append("</table>");
								
				sb.append("<table width='100%'>");
				
					sb.append("<tr>");
						sb.append("<td  width='23%'>");
							sb.append("<b>"+scrutinylabelsBundle.getString("label11")+"</b>");
						sb.append("</td>");
						sb.append("<td  width='15%'>");
							sb.append(lStrDOB);
						sb.append("</td>");
						sb.append("<td  width='18%'>");
							sb.append("<b>"+scrutinylabelsBundle.getString("label29")+"</b>");
						sb.append("</td>");
						sb.append("<td  width='10%'>");
							sb.append(lBigIntProvisionalAmt);
						sb.append("</td>");
						sb.append("<td  width='15%'>");
							sb.append("<b>"+scrutinylabelsBundle.getString("label47")+"</b>");
						sb.append("</td>");
						sb.append("<td  width='10%'>");
							sb.append(lStrLocationName);
						sb.append("</td>");
					sb.append("</tr>");
					
					sb.append("<tr>");
						sb.append("<td>");
								sb.append("<b>"+scrutinylabelsBundle.getString("label12")+"</b>");
						sb.append("</td>");
						sb.append("<td>");
							sb.append(lStrDOJ);
						sb.append("</td>");
						sb.append("<td>");
							sb.append("<b>"+scrutinylabelsBundle.getString("label18")+"</b>");
						sb.append("</td>");
						sb.append("<td>");
							sb.append((lObjTrnPnsnprocPnsnrpayVO.getPrvsnlPnsnpaidFromDate()==null)?"":simpleDateFormat.format(lObjTrnPnsnprocPnsnrpayVO.getPrvsnlPnsnpaidFromDate()));
						sb.append("</td>");
						sb.append("<td>");
							sb.append("<b>"+scrutinylabelsBundle.getString("label19")+"</b>");
						sb.append("</td>");
						sb.append("<td>");
							sb.append((lObjTrnPnsnprocPnsnrpayVO.getPrvsnlPnsnpaidToDate()==null)?"":simpleDateFormat.format(lObjTrnPnsnprocPnsnrpayVO.getPrvsnlPnsnpaidToDate()));
						sb.append("</td>");
					sb.append("</tr>");
					
					sb.append("<tr>");
						sb.append("<td>");
							sb.append("<b>"+scrutinylabelsBundle.getString("label13")+"</b>");
							sb.append("</td>");
						sb.append("<td>");
							sb.append(lStrDOR);
						sb.append("</td>");
						sb.append("<td>");
							sb.append("<b>"+scrutinylabelsBundle.getString("label30")+"</b>");
						sb.append("</td>");
						sb.append("<td>");
							sb.append(lObjTrnPnsnprocPnsnrpayVO.getPrvsnlGratuityAmount());
						sb.append("</td>");
						sb.append("<td>");
							sb.append("<b>"+scrutinylabelsBundle.getString("label22")+"</b>");
						sb.append("</td>");
						sb.append("<td>");
							sb.append((lObjTrnPnsnProcInwardPensionVO.getCommensionDate()==null)?"":simpleDateFormat.format(lObjTrnPnsnProcInwardPensionVO.getCommensionDate()));
						sb.append("</td>");
					sb.append("</tr>");
					
					sb.append("<tr>");
						sb.append("<td>");
							sb.append("<b>"+scrutinylabelsBundle.getString("label15")+"</b>");
						sb.append("</td>");
						sb.append("<td>");
							sb.append((lObjTrnPnsnprocPnsnrpayVO.getQulifyYear() == null?0:lObjTrnPnsnprocPnsnrpayVO.getQulifyYear())+"-"+((lObjTrnPnsnprocPnsnrpayVO.getQulifyMonth() == null)?0:lObjTrnPnsnprocPnsnrpayVO.getQulifyMonth())+"-"+((lObjTrnPnsnprocPnsnrpayVO.getQulifyDay()==null)?0:lObjTrnPnsnprocPnsnrpayVO.getQulifyDay()));
						sb.append("</td>");
						sb.append("<td>");
							sb.append("<b>"+scrutinylabelsBundle.getString("label16")+"</b>");
						sb.append("</td>");
						sb.append("<td>");
							sb.append((lObjTrnPnsnprocPnsnrpayVO.getActualYear() == null?0:lObjTrnPnsnprocPnsnrpayVO.getActualYear())+"-"+((lObjTrnPnsnprocPnsnrpayVO.getActualMonth() == null)?0:lObjTrnPnsnprocPnsnrpayVO.getActualMonth())+"-"+((lObjTrnPnsnprocPnsnrpayVO.getActualDay()==null)?0:lObjTrnPnsnprocPnsnrpayVO.getActualDay()));
						sb.append("</td>");
						sb.append("<td>");
							sb.append("<b>"+scrutinylabelsBundle.getString("label17")+"</b>");
						sb.append("</td>");
						sb.append("<td>");
							sb.append((lObjTrnPnsnprocPnsnrpayVO.getBrkYear() == null?0:lObjTrnPnsnprocPnsnrpayVO.getBrkYear())+"-"+((lObjTrnPnsnprocPnsnrpayVO.getBrkMonth() == null)?0:lObjTrnPnsnprocPnsnrpayVO.getBrkMonth())+"-"+((lObjTrnPnsnprocPnsnrpayVO.getBrkDay()==null)?0:lObjTrnPnsnprocPnsnrpayVO.getBrkDay()));
						sb.append("</td>");				
					sb.append("</tr>");
				sb.append("</table>");
				
				
				sb.append("<table width='100%'>");
				
					sb.append("<tr>");
						sb.append("<td width='50%'>");
				
							sb.append("<b>"+scrutinylabelsBundle.getString("label58")+"</b>");
							
							sb.append("<table width='90%' border='1' style='border-collapse: collapse'>");
							
								sb.append("<tr>");
									sb.append("<td>");
										sb.append("<b>"+scrutinylabelsBundle.getString("label54")+"</b>");
									sb.append("</td>");
									sb.append("<td>");
										sb.append("<b>"+scrutinylabelsBundle.getString("label55")+"</b>");
									sb.append("</td>");
									sb.append("<td>");
										sb.append("<b>"+scrutinylabelsBundle.getString("label56")+"</b>");
									sb.append("</td>");
									sb.append("<td>");
										sb.append("<b>"+scrutinylabelsBundle.getString("label25")+"</b>");
									sb.append("</td>");
									sb.append("<td>");
										sb.append("<b>"+scrutinylabelsBundle.getString("label57")+"</b>");
									sb.append("</td>");			
								sb.append("</tr>");
								
								TrnPnsnprocAvgPayCalc lObjPnsnprocAvgPayCalc = new TrnPnsnprocAvgPayCalc();
								
								if(!lLstTrnPnsnprocAvgPayCalcVO.isEmpty()){	
									for(Integer lInt=0;lInt<lLstTrnPnsnprocAvgPayCalcVO.size();lInt++){							
										lObjPnsnprocAvgPayCalc = lLstTrnPnsnprocAvgPayCalcVO.get(lInt);
										sb.append("<tr>");
											sb.append("<td>");
												sb.append(simpleDateFormat.format(lObjPnsnprocAvgPayCalc.getFromDate()));
											sb.append("</td>");
											sb.append("<td>");
												sb.append(lObjPnsnprocAvgPayCalc.getBasic());
											sb.append("</td>");
											sb.append("<td>");
												sb.append(lObjPnsnprocAvgPayCalc.getDp());
											sb.append("</td>");
											sb.append("<td>");
												sb.append(lObjPnsnprocAvgPayCalc.getNpa());
											sb.append("</td>");
											sb.append("<td>");
												sb.append(lObjPnsnprocAvgPayCalc.getTotal());
											sb.append("</td>");			
										sb.append("</tr>");										
									}
								}
							sb.append("</table>");
						sb.append("</td>");	
						
						sb.append("<td width='50%' style='display:inline'>");
						
							sb.append("<b>"+scrutinylabelsBundle.getString("label59")+"</b>");
							
							sb.append("<table width='90%' border='1' style='border-collapse: collapse'>");
							
								sb.append("<tr>");
									sb.append("<td>");
										sb.append("<b>"+scrutinylabelsBundle.getString("label60")+"</b>");
									sb.append("</td>");
									sb.append("<td>");
										sb.append("<b>"+scrutinylabelsBundle.getString("label10")+"</b>");
									sb.append("</td>");
									sb.append("<td>");
										sb.append("<b>"+scrutinylabelsBundle.getString("label55")+"</b>");
									sb.append("</td>");
									sb.append("<td>");
										sb.append("<b>"+scrutinylabelsBundle.getString("label56")+"</b>");
									sb.append("</td>");
									sb.append("<td>");
										sb.append("<b>"+scrutinylabelsBundle.getString("label54")+"</b>");
									sb.append("</td>");
									sb.append("<td>");
										sb.append("<b>"+scrutinylabelsBundle.getString("label61")+"</b>");
									sb.append("</td>");												
									sb.append("</tr>");
								TrnPnsnprocEventdtls lObjPnsnprocEventdtls = new TrnPnsnprocEventdtls();
							
								if(!lLstTrnPnsnprocEventdtls.isEmpty()){	
									for(Integer lInt=0;lInt<lLstTrnPnsnprocEventdtls.size();lInt++){							
										lObjPnsnprocEventdtls = lLstTrnPnsnprocEventdtls.get(lInt);
										sb.append("<tr>");
											sb.append("<td>");
												sb.append(lObjTrnPnsnProcInwardPensionDAO.getLookupNameFromLookupId(lObjPnsnprocEventdtls.getEventId()));
											sb.append("</td>");
											sb.append("<td>");
											lLstPayScale =	lObjTrnPnsnProcInwardPensionDAO.getPayScaleDescFromScaleId(lObjPnsnprocEventdtls.getPayScaleId());
												if(!lLstPayScale.isEmpty()){
													Object lObjArrPayScale[] = (Object[]) lLstPayScale.get(0);
													lStrPayScale = (String) lObjArrPayScale[0];
													lLngPayCom = (Long) lObjArrPayScale[1];
												}											
												sb.append(lStrPayScale);
											sb.append("</td>");
											sb.append("<td>");
												sb.append(lObjPnsnprocEventdtls.getBasic());
											sb.append("</td>");
											sb.append("<td>");
												sb.append(lObjPnsnprocEventdtls.getDp());
											sb.append("</td>");
											sb.append("<td>");
												sb.append(simpleDateFormat.format(lObjPnsnprocEventdtls.getFromDate()));
											sb.append("</td>");	
											sb.append("<td>");
												sb.append(simpleDateFormat.format(lObjPnsnprocEventdtls.getToDate()));
											sb.append("</td>");	
										sb.append("</tr>");										
									}
								}
							sb.append("</table>");
				
				sb.append("</td>");	
				sb.append("</tr>");
			sb.append("</table>");
			
				sb.append("<table width='100%'>");
					
					sb.append("<tr>");
						sb.append("<td width='23%'>");
							sb.append("<b>"+scrutinylabelsBundle.getString("label33")+"</b>");
						sb.append("</td>");
						sb.append("<td width='15%'>");						
							sb.append(lObjTrnPnsnProcPnsnCalcVO.getCvpMonthAmnt());
						sb.append("</td>");
						sb.append("<td width='18%'>");
							sb.append("<b>"+scrutinylabelsBundle.getString("label32")+"</b>");
						sb.append("</td>");
						sb.append("<td width='10%'>");						
							sb.append(lObjTrnPnsnProcPnsnCalcVO.getCvpAmnt());
						sb.append("</td>");
						sb.append("<td width='15%'>");
							sb.append("<b>"+scrutinylabelsBundle.getString("label41")+"</b>");
						sb.append("</td>");
						sb.append("<td width='10%'>");						
							sb.append((lObjTrnPnsnProcPnsnCalcVO.getSrvcGratuityAmnt()==null)?"":lObjTrnPnsnProcPnsnCalcVO.getSrvcGratuityAmnt());
						sb.append("</td>");
							
					sb.append("</tr>");
					
					sb.append("<tr>");
						sb.append("<td>");
							sb.append("<b>"+scrutinylabelsBundle.getString("label62")+"</b>");
						sb.append("</td>");
						sb.append("<td>");						
							sb.append(lObjTrnPnsnProcPnsnCalcVO.getPensionerTotalAmnt());
						sb.append("</td>");
						sb.append("<td>");
							sb.append("<b>"+scrutinylabelsBundle.getString("label34")+"</b>");
						sb.append("</td>");
						sb.append("<td>");						
							sb.append(lObjTrnPnsnProcPnsnCalcVO.getPensionerReducedAmnt());
						sb.append("</td>");
						sb.append("<td>");
							sb.append("<b>"+scrutinylabelsBundle.getString("label63")+"</b>");
						sb.append("</td>");
						sb.append("<td>");						
							sb.append(lObjTrnPnsnProcPnsnCalcVO.getDcrgAmnt());
						sb.append("</td>");							
					sb.append("</tr>");
					
					sb.append("<tr>");
						sb.append("<td>");
							sb.append("<b>"+scrutinylabelsBundle.getString("label49")+"</b>");
						sb.append("</td>");
						sb.append("<td>");						
							sb.append(lObjTrnPnsnProcPnsnCalcVO.getFp1Amnt());
						sb.append("</td>");
						sb.append("<td>");
							sb.append("<b>"+scrutinylabelsBundle.getString("label51")+"</b>");
						sb.append("</td>");
						sb.append("<td>");						
							sb.append(lObjTrnPnsnProcPnsnCalcVO.getFp2Amnt());
						sb.append("</td>");
						sb.append("<td>");
							
						sb.append("</td>");
						sb.append("<td>");						
							
						sb.append("</td>");							
					sb.append("</tr>");
					
					sb.append("<tr>");
						sb.append("<td>");
							sb.append("<b>"+scrutinylabelsBundle.getString("label50")+"</b>");
						sb.append("</td>");
						sb.append("<td>");						
							sb.append((lObjTrnPnsnProcPnsnCalcVO.getFp1Date()==null)?"":simpleDateFormat.format(lObjTrnPnsnProcPnsnCalcVO.getFp1Date()));
						sb.append("</td>");
						sb.append("<td>");
							sb.append("<b>"+scrutinylabelsBundle.getString("label52")+"</b>");
						sb.append("</td>");
						sb.append("<td>");						
							sb.append((lObjTrnPnsnProcPnsnCalcVO.getFp2Date()==null)?"":simpleDateFormat.format(lObjTrnPnsnProcPnsnCalcVO.getFp2Date()));
						sb.append("</td>");
						sb.append("<td>");
						
						sb.append("</td>");
						sb.append("<td>");						
							
						sb.append("</td>");	
					sb.append("</tr>");
					
				sb.append("</table>");
				
				
				sb.append("<table width='100%'>");
				
				sb.append("<tr>");
					sb.append("<td width='50%'>");
			
						sb.append("<b>"+scrutinylabelsBundle.getString("label64")+"</b>");
						
						sb.append("<table width='90%' border='1' style='border-collapse: collapse'>");
						
							sb.append("<tr>");
								sb.append("<td>");
									sb.append("<b>"+scrutinylabelsBundle.getString("label65")+"</b>");
								sb.append("</td>");
								sb.append("<td>");
									sb.append("<b>"+scrutinylabelsBundle.getString("label66")+"</b>");
								sb.append("</td>");
								sb.append("<td>");
									sb.append("<b>"+scrutinylabelsBundle.getString("label67")+"</b>");
								sb.append("</td>");
								sb.append("<td>");
									sb.append("<b>"+scrutinylabelsBundle.getString("label68")+"</b>");
								sb.append("</td>");
								sb.append("<td>");
									sb.append("<b>"+scrutinylabelsBundle.getString("label69")+"</b>");
								sb.append("</td>");	
								sb.append("<td>");
									sb.append("<b>"+scrutinylabelsBundle.getString("label70")+"</b>");
								sb.append("</td>");
								sb.append("<td>");
									sb.append("<b>"+scrutinylabelsBundle.getString("label71")+"</b>");
								sb.append("</td>");	
							sb.append("</tr>");
							
							TrnPnsnprocFamilydtls lObjTrnPnsnprocFamilydtls = new TrnPnsnprocFamilydtls();
							
							if(!lLstPnsnrFamilyDtls.isEmpty()){	
								for(Integer lInt=0;lInt<lLstPnsnrFamilyDtls.size();lInt++){							
									lObjTrnPnsnprocFamilydtls = lLstPnsnrFamilyDtls.get(lInt);
									sb.append("<tr>");
										sb.append("<td>");
											sb.append(lObjTrnPnsnprocFamilydtls.getName());
										sb.append("</td>");
										sb.append("<td>");
											sb.append(lObjTrnPnsnprocFamilydtls.getRelation());
										sb.append("</td>");
										sb.append("<td>");
											sb.append((lObjTrnPnsnprocFamilydtls.getBirthDate()==null)?"":simpleDateFormat.format(lObjTrnPnsnprocFamilydtls.getBirthDate()));
										sb.append("</td>");
										sb.append("<td>");
											if(lObjTrnPnsnprocFamilydtls.getMinorFlag().equals("Y"))
												sb.append("Yes");
											else
												sb.append("No");
										sb.append("</td>");
										sb.append("<td>");
											if(lObjTrnPnsnprocFamilydtls.getMarriedFlag().equals("Y"))
												sb.append("Yes");
											else
												sb.append("No");
										sb.append("</td>");
										sb.append("<td>");
											if(lObjTrnPnsnprocFamilydtls.getHandicapeFlag().equals("Y"))
												sb.append("Yes");
											else
												sb.append("No");
										sb.append("</td>");	
										sb.append("<td>");
											sb.append((lObjTrnPnsnprocFamilydtls.getGuardianName() == null)?"":lObjTrnPnsnprocFamilydtls.getGuardianName());
										sb.append("</td>");
									sb.append("</tr>");										
								}
							}
						sb.append("</table>");
					sb.append("</td>");	
					
					sb.append("<td width='50%' style='display:inline'>");
					
						sb.append("<b>"+scrutinylabelsBundle.getString("label72")+"</b>");
						
						sb.append("<table width='90%' border='1' style='border-collapse: collapse'>");
						
							sb.append("<tr>");								
								sb.append("<td>");
									sb.append("<b>"+scrutinylabelsBundle.getString("label73")+"</b>");
								sb.append("</td>");
								sb.append("<td>");
									sb.append("<b>"+scrutinylabelsBundle.getString("label66")+"</b>");
								sb.append("</td>");
								sb.append("<td>");
									sb.append("<b>"+scrutinylabelsBundle.getString("label74")+"</b>");
								sb.append("</td>");
								sb.append("<td>");
									sb.append("<b>"+scrutinylabelsBundle.getString("label75")+"</b>");
								sb.append("</td>");
								sb.append("<td>");
									sb.append("<b>"+scrutinylabelsBundle.getString("label76")+"</b>");
								sb.append("</td>");	
								sb.append("<td>");
									sb.append("<b>"+scrutinylabelsBundle.getString("label77")+"</b>");
								sb.append("</td>");	
								sb.append("<td>");
									sb.append("<b>"+scrutinylabelsBundle.getString("label78")+"</b>");
								sb.append("</td>");	
								sb.append("</tr>");
				
								TrnPnsnprocNomineedtls lObjnPnsnprocNomineedtls= new TrnPnsnprocNomineedtls();
						
							if(!lLstPnsnrNomineeDtls.isEmpty()){	
								for(Integer lInt=0;lInt<lLstPnsnrNomineeDtls.size();lInt++){							
									lObjnPnsnprocNomineedtls = lLstPnsnrNomineeDtls.get(lInt);
									sb.append("<tr>");
										sb.append("<td>");
											sb.append(lObjnPnsnprocNomineedtls.getName());
										sb.append("</td>");		
										sb.append("<td>");
											sb.append(lObjnPnsnprocNomineedtls.getRelation());
										sb.append("</td>");
										sb.append("<td>");
											sb.append(lObjnPnsnprocNomineedtls.getPercentage());
										sb.append("</td>");																			
										sb.append("<td>");
											sb.append((lObjnPnsnprocNomineedtls.getBranchCode() == null)?"":lObjnPnsnprocNomineedtls.getBranchCode());
										sb.append("</td>");
										sb.append("<td>");
											sb.append(lObjTrnPnsnProcInwardPensionDAO.getBankNameFromBankCode(lObjnPnsnprocNomineedtls.getBankCode()));
										sb.append("</td>");
										sb.append("<td>");
											sb.append(lObjTrnPnsnProcInwardPensionDAO.getBranchNameFromBrachCode(lObjnPnsnprocNomineedtls.getBranchCode()));
										sb.append("</td>");	
										sb.append("<td>");
											sb.append((lObjnPnsnprocNomineedtls.getAccountNo() == null)?"":lObjnPnsnprocNomineedtls.getAccountNo());
										sb.append("</td>");	
									sb.append("</tr>");										
								}
							}
						sb.append("</table>");
					
						sb.append("</td>");	
					sb.append("</tr>");
				sb.append("</table>");
			
				if(!lLstTrnPnsnProcAdvnceBal.isEmpty()){
					sb.append("<b>"+scrutinylabelsBundle.getString("label79")+"</b>");
						
						sb.append("<table width='45%' border='1' style='border-collapse: collapse'>");
						
						sb.append("<tr>");								
							sb.append("<td>");
								sb.append("<b>"+scrutinylabelsBundle.getString("label80")+"</b>");
							sb.append("</td>");
							sb.append("<td>");
								sb.append("<b>"+scrutinylabelsBundle.getString("label81")+"</b>");
							sb.append("</td>");
							sb.append("<td>");
								sb.append("<b>"+scrutinylabelsBundle.getString("label82")+"</b>");
							sb.append("</td>");					
						sb.append("</tr>");
			
							TrnPnsnProcAdvnceBal lObjPnsnProcAdvnceBal= new TrnPnsnProcAdvnceBal();
										
							for(Integer lInt=0;lInt<lLstTrnPnsnProcAdvnceBal.size();lInt++){							
								lObjPnsnProcAdvnceBal = lLstTrnPnsnProcAdvnceBal.get(lInt);
								sb.append("<tr>");
									sb.append("<td>");
										sb.append(lObjPnsnProcAdvnceBal.getAdvanceBalTypeId());
									sb.append("</td>");		
									sb.append("<td>");
										sb.append(lObjPnsnProcAdvnceBal.getAdvanceAmnt());
									sb.append("</td>");
									sb.append("<td>");
										sb.append(lObjPnsnProcAdvnceBal.getAdvanceSchemeCode());
									sb.append("</td>");
								sb.append("</tr>");										
							}
						sb.append("</table>");
					}				
				
				sb.append("<b>"+scrutinylabelsBundle.getString("label83")+"</b>");
				
				sb.append("<table width=65%' border='1' style='border-collapse: collapse'>");
				
					sb.append("<tr>");								
						sb.append("<td>");
							sb.append("<b>"+scrutinylabelsBundle.getString("label62")+"</b>");
						sb.append("</td>");
						sb.append("<td>");
							sb.append("<b>"+scrutinylabelsBundle.getString("label33")+"</b>");
						sb.append("</td>");
						sb.append("<td>");
							sb.append("<b>"+scrutinylabelsBundle.getString("label34")+"</b>");
						sb.append("</td>");	
						sb.append("<td>");
							sb.append("<b>"+scrutinylabelsBundle.getString("label40")+"</b>");
						sb.append("</td>");
						sb.append("<td>");
							sb.append("<b>"+scrutinylabelsBundle.getString("label41")+"</b>");
						sb.append("</td>");
						sb.append("<td>");
							sb.append("<b>"+scrutinylabelsBundle.getString("label63")+"</b>");
						sb.append("</td>");
						sb.append("<td>");
							sb.append("<b>"+scrutinylabelsBundle.getString("label84")+"</b>");
						sb.append("</td>");
						sb.append("<td>");
							sb.append("<b>"+scrutinylabelsBundle.getString("label85")+"</b>");
						sb.append("</td>");
					sb.append("</tr>");
					
					sb.append("<tr>");								
						sb.append("<td>");
							sb.append(lObjTrnPnsnProcPnsnCalcVO.getPensionerTotalAmnt());
						sb.append("</td>");
						sb.append("<td>");
							sb.append(lObjTrnPnsnProcPnsnCalcVO.getCvpMonthAmnt());
						sb.append("</td>");
						sb.append("<td>");
							sb.append(lObjTrnPnsnProcPnsnCalcVO.getPensionerReducedAmnt());
						sb.append("</td>");	
						sb.append("<td>");
							sb.append(lObjTrnPnsnProcPnsnCalcVO.getCvpAmnt());
						sb.append("</td>");
						sb.append("<td>");							
							sb.append((lObjTrnPnsnProcPnsnCalcVO.getSrvcGratuityAmnt()==null)?"":lObjTrnPnsnProcPnsnCalcVO.getSrvcGratuityAmnt());
						sb.append("</td>");
						sb.append("<td>");
							sb.append(lObjTrnPnsnProcPnsnCalcVO.getDcrgAmnt());
						sb.append("</td>");
						sb.append("<td>");
							sb.append(lObjTrnPnsnProcPnsnCalcVO.getFp1Amnt() + " - " );
							sb.append((lObjTrnPnsnProcPnsnCalcVO.getFp1Date()==null)?"":simpleDateFormat.format(lObjTrnPnsnProcPnsnCalcVO.getFp1Date()));
						sb.append("</td>");
						sb.append("<td>");
							sb.append(lObjTrnPnsnProcPnsnCalcVO.getFp2Amnt() + " - " );
							sb.append((lObjTrnPnsnProcPnsnCalcVO.getFp2Date()==null)?"":simpleDateFormat.format(lObjTrnPnsnProcPnsnCalcVO.getFp2Date()));
						sb.append("</td>");
					sb.append("</tr>");
				
				sb.append("</table>");
				
			sb.append("</td></tr></table>");
		}
		catch(Exception e)
		{
			gLogger.info("Exception in ScrutinyReportPrintUtil:"+e,e);
		}
		return sb;
	}
}
