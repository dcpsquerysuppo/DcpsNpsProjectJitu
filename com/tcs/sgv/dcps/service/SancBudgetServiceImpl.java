package com.tcs.sgv.dcps.service;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.SancBudgetDAO;
import com.tcs.sgv.dcps.dao.SancBudgetDAOImpl;
import com.tcs.sgv.dcps.valueobject.SanctionedBudget;
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

public class SancBudgetServiceImpl
  extends ServiceImpl
  implements SancBudgetService
{
  private final Log gLogger = LogFactory.getLog(getClass());
  private HttpServletRequest request = null;
  Long gLngPostId = null;
  Long gLngUserId = null;
  Date gDtCurrDt = null;
  String gStrLocationCode = null;
  static HashMap sMapUserLoc = new HashMap();
  String gStrUserLocation = null;
  private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/dcps/DCPSConstants");
  
  private StringBuilder getResponseXMLDoc(boolean flag, String finyearId, String lStrOrganizationId, String lStrSchemeCode)
  {
    StringBuilder lStrBldXML = new StringBuilder();
    
    lStrBldXML.append("<XMLDOC>");
    lStrBldXML.append("<Flag>");
    lStrBldXML.append(flag);
    lStrBldXML.append("</Flag>");
    lStrBldXML.append("<finYear>");
    lStrBldXML.append(finyearId);
    lStrBldXML.append("</finYear>");
    lStrBldXML.append("<lStrOrganizationId>");
    lStrBldXML.append(lStrOrganizationId);
    lStrBldXML.append("</lStrOrganizationId>");
    lStrBldXML.append("<lStrSchemeCode>");
    lStrBldXML.append(lStrSchemeCode);
    lStrBldXML.append("</lStrSchemeCode>");
    lStrBldXML.append("</XMLDOC>");
    
    return lStrBldXML;
  }
  
  public ResultObject loadSanctionedBudget(Map<String, Object> inputMap)
    throws Exception
  {
    ResultObject resObj = new ResultObject(0);
    ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
    this.request = ((HttpServletRequest)inputMap.get("requestObj"));
    List lLstFinYear = null;
    List lLstOrgType = null;
    try
    {
      DcpsCommonDAO objDcpsCommonDAO = new DcpsCommonDAOImpl(null, 
        serv.getSessionFactory());
      
      SancBudgetDAO lObjcmnDCPSSancBudgetDAO = new SancBudgetDAOImpl(
        null, serv.getSessionFactory());
      
      lLstOrgType = objDcpsCommonDAO.getAllOrgType();
      lLstFinYear = objDcpsCommonDAO.getFinyearsAfterCurrYear();
      inputMap.put("lLstDepartment", lLstOrgType);
      inputMap.put("lLstFinYear", lLstFinYear);
      
      Long finYear = null;
      Long orgType = null;
      String schemeCode = null;
      String lStrBudgetType = "Credit";
      
      SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
      Date lDtcurDate = SessionHelper.getCurDate();
      inputMap.put("lDtCurDate", lObjDateFormat.format(lDtcurDate));
      if ((!StringUtility.getParameter("cmbFinyear", this.request).trim().equalsIgnoreCase("")) && 
        (StringUtility.getParameter("cmbFinyear", this.request) != null)) {
        finYear = Long.valueOf(Long.parseLong(StringUtility.getParameter(
          "cmbFinyear", this.request).trim()));
      }
      if ((!StringUtility.getParameter("cmbOrgType", this.request).trim().equalsIgnoreCase("")) && 
        (StringUtility.getParameter("cmbOrgType", this.request) != null))
      {
        orgType = Long.valueOf(Long.parseLong(StringUtility.getParameter(
          "cmbOrgType", this.request).trim()));
        schemeCode = lObjcmnDCPSSancBudgetDAO.getSchemeCodeForOrgId(orgType);
      }
      lStrBudgetType = 
        StringUtility.getParameter("radioButtonType", this.request).trim();
      if (finYear != null)
      {
        List lLstSavedBudgets = lObjcmnDCPSSancBudgetDAO
          .getAllSanctionedBudgets(finYear, orgType);
        inputMap.put("SancBudgetList", lLstSavedBudgets);
        inputMap.put("finYear", finYear);
        inputMap.put("orgType", orgType);
        inputMap.put("schemeCode", schemeCode);
        inputMap.put("budgetType", lStrBudgetType);
      }
    }
    catch (Exception e)
    {
      this.gLogger.error("Error is;" + e, e);
      e.printStackTrace();
      return resObj;
    }
    resObj.setResultValue(inputMap);
    resObj.setViewName("DCPSSanctionedBudget");
    return resObj;
  }
  
  public ResultObject saveSanctionedBudget(Map<String, Object> inputMap)
    throws Exception
  {
    LogFactory.getLog(getClass());
    ResultObject resObj = new ResultObject(0);
    ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
    Boolean lBFlag = Boolean.valueOf(false);
    Long lLongSancBudgetIdPk = null;
    Long finYear = null;
    String lStrOrganizationId = "";
    String lStrSchemeCode = "";
    try
    {
      SancBudgetDAO dcpsSanctionedBudget = new SancBudgetDAOImpl(
        SancBudgetDAO.class, serv.getSessionFactory());
      SanctionedBudget lArrDcpsSancBudget = 
        (SanctionedBudget)inputMap.get("DCPSSanctionedBudget");
      
      finYear = Long.valueOf(Long.parseLong(lArrDcpsSancBudget
        .getDcpsSancBudgetFinYear().toString()));
      lStrOrganizationId = lArrDcpsSancBudget.getDcpsSancBudgetOrgId().toString();
      Long lLngCurrentTotal = dcpsSanctionedBudget
        .getTotalBudget(finYear, lStrOrganizationId);
      this.gLogger.info("lLngCurrentTotal******************" + lLngCurrentTotal);
      String lStrCurrType = lArrDcpsSancBudget.getDcpsSancBudgetType();
      
      Long lLngCurrBudget = lArrDcpsSancBudget.getDcpsSancBudgetAmount();
      this.gLogger.info("hiii here**lLngCurrBudget" + lLngCurrBudget);
      
      lStrSchemeCode = lArrDcpsSancBudget.getDcpsSancBudgetSchemeCode();
      if (lStrCurrType.trim().equals("Credit"))
      {
        lLngCurrentTotal = Long.valueOf(lLngCurrentTotal.longValue() + lLngCurrBudget.longValue());
        this.gLogger.info("hiii here**" + lLngCurrentTotal);
      }
      if (lStrCurrType.trim().equals("Debit")) {
        lLngCurrentTotal = Long.valueOf(lLngCurrentTotal.longValue() - lLngCurrBudget.longValue());
      }
      this.gLogger.info("lLngCurrentTotal*******************************" + lLngCurrentTotal);
      lArrDcpsSancBudget.setTotalBudget(lLngCurrentTotal);
      
      lLongSancBudgetIdPk = IFMSCommonServiceImpl.getNextSeqNum(
        "MST_DCPS_SANC_BUDGET", inputMap);
      lArrDcpsSancBudget.setDcpsSanctionedBudgetIdPk(lLongSancBudgetIdPk);
      dcpsSanctionedBudget.create(lArrDcpsSancBudget);
      
      dcpsSanctionedBudget.updateExpenditure(finYear);
      lBFlag = Boolean.valueOf(true);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      resObj.setResultValue(null);
      resObj.setThrowable(e);
      resObj.setResultCode(-1);
      resObj.setViewName("errorPage");
      return resObj;
    }
    String lSBStatus = getResponseXMLDoc(lBFlag.booleanValue(), finYear.toString(), lStrOrganizationId, lStrSchemeCode)
      .toString();
    String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
      .toString();
    
    inputMap.put("ajaxKey", lStrResult);
    resObj.setResultValue(inputMap);
    resObj.setViewName("ajaxData");
    return resObj;
  }
  
  public ResultObject populateSchemeCode(Map<String, Object> inputMap)
    throws Exception
  {
    ResultObject resObj = new ResultObject(0);
    
    ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
    this.request = ((HttpServletRequest)inputMap.get("requestObj"));
    
    String lStrSchemeCode = null;
    Long lLongOrgId = null;
    try
    {
      String lStrOrgId = 
        StringUtility.getParameter("cmbOrgType", this.request).trim();
      if (!lStrOrgId.equalsIgnoreCase("")) {
        lLongOrgId = Long.valueOf(lStrOrgId);
      }
      SancBudgetDAO lobjSancBudgetDAO = new SancBudgetDAOImpl(null, 
        serv.getSessionFactory());
      lStrSchemeCode = lobjSancBudgetDAO
        .getSchemeCodeForOrgId(lLongOrgId);
    }
    catch (Exception ex)
    {
      resObj.setResultValue(null);
      resObj.setThrowable(ex);
      resObj.setResultCode(-1);
      resObj.setViewName("errorPage");
      ex.printStackTrace();
      return resObj;
    }
    String lSBStatus = getResponseXMLDocForSchemeCode(lStrSchemeCode)
      .toString();
    String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
      .toString();
    
    inputMap.put("ajaxKey", lStrResult);
    resObj.setResultValue(inputMap);
    resObj.setViewName("ajaxData");
    return resObj;
  }
  
  private StringBuilder getResponseXMLDocForSchemeCode(String lStrSchemeCode)
  {
    StringBuilder lStrBldXML = new StringBuilder();
    
    lStrBldXML.append("<XMLDOC>");
    lStrBldXML.append("<txtSchemeCode>");
    lStrBldXML.append(lStrSchemeCode);
    lStrBldXML.append("</txtSchemeCode>");
    lStrBldXML.append("</XMLDOC>");
    
    return lStrBldXML;
  }
}
