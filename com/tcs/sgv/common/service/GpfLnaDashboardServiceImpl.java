package com.tcs.sgv.common.service;

import com.tcs.sgv.common.dao.GpfLnaDashboardDao;
import com.tcs.sgv.common.dao.GpfLnaDashboardDaoImpl;
import com.tcs.sgv.common.dao.IFMSCommonDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.OrgDdoMst;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;
import com.tcs.sgv.eis.dao.PayBillDAOImpl;
import com.tcs.sgv.eis.valueobject.HrPayPaybill;
import com.tcs.sgv.ess.valueobject.OrgUserMst;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GpfLnaDashboardServiceImpl
  extends ServiceImpl
  implements GpfLnaDashboardService
{
  Log gLogger = LogFactory.getLog(getClass());
  private String gStrPostId = null;
  private Date gDtCurDate = null;
  private HttpServletRequest request = null;
  private ServiceLocator serv = null;
  Long gLngPostId = null;
  Long gLngUserId = null;
  Date gDtCurrDt = null;
  String gStrLocationCode = null;
  Long gLngLangId = null;
  Long gLngLocationCode = null;
  Integer lIntSubtypeSelection = Integer.valueOf(0);
  private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/lna/LNAConstants");
  
  private void setSessionInfo(Map inputMap)
  {
    this.request = ((HttpServletRequest)inputMap.get("requestObj"));
    this.serv = ((ServiceLocator)inputMap.get("serviceLocator"));
    this.gLngPostId = SessionHelper.getPostId(inputMap);
    this.gStrPostId = this.gLngPostId.toString();
    this.gLngUserId = SessionHelper.getUserId(inputMap);
    this.gDtCurDate = SessionHelper.getCurDate();
    this.gStrLocationCode = SessionHelper.getLocationCode(inputMap);
    this.gLngLocationCode = Long.valueOf(Long.parseLong(this.gStrLocationCode));
    this.gLngLangId = SessionHelper.getLangId(inputMap);
  }
  
  public ResultObject getUserType(Map<String, Object> inputMap)
  {
    this.gLogger.info("getUserType service method called");
    ResultObject resObj = new ResultObject(0);
    ServiceLocator serviceLocator = (ServiceLocator)inputMap.get("serviceLocator");
    try
    {
      setSessionInfo(inputMap);
      
      GpfLnaDashboardDao lObjGpfLnaDashboardDao = new GpfLnaDashboardDaoImpl(null, this.serv.getSessionFactory());
      
      ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
      Map loginMap = (Map)inputMap.get("baseLoginMap");
      IFMSCommonDAOImpl ifmsCommonDAOImpl = new IFMSCommonDAOImpl(OrgUserMst.class, serv.getSessionFactorySlave());
      String loggedInPost = loginMap.get("loggedInPost").toString();
      long langId = Long.parseLong(loginMap.get("langId").toString());
      long locId = StringUtility.convertToLong(loginMap.get("locationId").toString()).longValue();
      
      long loggedInPostId = Long.parseLong(loginMap.get("primaryPostId").toString());
      DcpsCommonDAOImpl commonDao = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
      PayBillDAOImpl payDAO = new PayBillDAOImpl(HrPayPaybill.class, serv.getSessionFactory());
      List<OrgDdoMst> ddoList = payDAO.getDDOCodeByLoggedInlocId(locId);
      OrgDdoMst ddoMst = null;
      if ((ddoList != null) && (ddoList.size() > 0)) {
        ddoMst = (OrgDdoMst)ddoList.get(0);
      }
      String ddoCode = null;
      if (ddoMst != null) {
        ddoCode = ddoMst.getDdoCode();
      }
      String roleId = ifmsCommonDAOImpl.getRoleID(this.gStrPostId);
      long totalFilesNotSend = 0L;
      long filesNotSendToTreasury = 0L;
      long filesNotSendTO3DaysAlert = 0L;
      long filesNotSendTO2DaysAlert = 0L;
      long filesNotSendTO1DaysAlert = 0L;
      
      
      totalFilesNotSend = ifmsCommonDAOImpl.findFilesCount(locId, roleId);
      filesNotSendToTreasury = ifmsCommonDAOImpl.findTreasuryCount(locId, roleId);
      filesNotSendTO3DaysAlert = ifmsCommonDAOImpl.findTreasuryFilesCount(locId, roleId);
      filesNotSendTO2DaysAlert = ifmsCommonDAOImpl.filesNotSendTO2DaysAlert(locId, roleId);
      filesNotSendTO1DaysAlert = ifmsCommonDAOImpl.filesNotSendTO1DaysAlert(locId, roleId);
      
     
      
      
      this.gLogger.info("totalFilesNotSend-----" + totalFilesNotSend);
      this.gLogger.info("filesNotSendToTreasury-----" + filesNotSendToTreasury);
      this.gLogger.info("filesNotSendTO3DaysAlert-----" + filesNotSendTO3DaysAlert);
      this.gLogger.info("filesNotSendTO2DaysAlert-----" + filesNotSendTO2DaysAlert);
      this.gLogger.info("filesNotSendTO1DaysAlert-----" + filesNotSendTO1DaysAlert);
      
      
      inputMap.put("totalFilesNotSend", Long.valueOf(totalFilesNotSend));
      inputMap.put("filesNotSendToTreasury", Long.valueOf(filesNotSendToTreasury));
      inputMap.put("filesNotSendTO3DaysAlert", Long.valueOf(filesNotSendTO3DaysAlert));
      inputMap.put("filesNotSendTO2DaysAlert", Long.valueOf(filesNotSendTO2DaysAlert));
      inputMap.put("filesNotSendTO1DaysAlert", Long.valueOf(filesNotSendTO1DaysAlert));
      
      String lStrUserType = lObjGpfLnaDashboardDao.getUserType(this.gLngUserId);
      this.gLogger.info("lStrUserType" + lStrUserType);
      inputMap.put("lStrUserType", lStrUserType);
      if ((!lStrUserType.equalsIgnoreCase("DDO")) && (!lStrUserType.equalsIgnoreCase("DDO Assistant")))
      {
        if (lStrUserType.equalsIgnoreCase("HO"))
        {
          String PendingCount = lObjGpfLnaDashboardDao.getCount(this.gStrPostId);
          inputMap.put("PendingCount", PendingCount);
          
          String AppCount = lObjGpfLnaDashboardDao.getLNANoOfApprovedRequests(this.gStrLocationCode, this.gStrPostId, lStrUserType);
          inputMap.put("AppCount", AppCount);
          
          String DraftCount = lObjGpfLnaDashboardDao.getLNADraftRequestCount(this.gStrLocationCode, this.gStrPostId);
          inputMap.put("DraftCount", DraftCount);
          
          String RejectCount = lObjGpfLnaDashboardDao.getRejectedReqCount(this.gStrLocationCode, this.gStrPostId);
          inputMap.put("RejectCount", RejectCount);
          
          String OrdersCount = lObjGpfLnaDashboardDao.getNoOfOrdersGenerated(this.gStrLocationCode, this.gStrPostId, lStrUserType);
          inputMap.put("OrdersCount", OrdersCount);
          
          String OrdersPendingCount = String.valueOf(Integer.valueOf(AppCount).intValue() - Integer.valueOf(OrdersCount).intValue());
          inputMap.put("OrdersPendingCount", OrdersPendingCount);
        }
        if (lStrUserType.equalsIgnoreCase("HOD 2"))
        {
          String PendingCount = lObjGpfLnaDashboardDao.getCount(this.gStrPostId);
          inputMap.put("PendingCount", PendingCount);
          
          String AppCount = lObjGpfLnaDashboardDao.getLNANoOfApprovedRequests(this.gStrLocationCode, this.gStrPostId, lStrUserType);
          inputMap.put("AppCount", AppCount);
          
          String OrdersCount = lObjGpfLnaDashboardDao.getNoOfOrdersGenerated(this.gStrLocationCode, this.gStrPostId, lStrUserType);
          inputMap.put("OrdersCount", OrdersCount);
          
          String OrdersPendingCount = String.valueOf(Integer.valueOf(AppCount).intValue() - Integer.valueOf(OrdersCount).intValue());
          inputMap.put("OrdersPendingCount", OrdersPendingCount);
        }
        if (lStrUserType.equalsIgnoreCase("RHO"))
        {
          String PendingCount = lObjGpfLnaDashboardDao.getCount(this.gStrPostId);
          inputMap.put("PendingCount", PendingCount);
        }
      }
      if (lStrUserType.equalsIgnoreCase("DDO"))
      {
        String PendingCount = lObjGpfLnaDashboardDao.getCount(this.gStrPostId);
        inputMap.put("PendingCount", PendingCount);
      }
      resObj.setResultValue(inputMap);
      resObj.setViewName("Home2");
    }
    catch (Exception e)
    {
      IFMSCommonServiceImpl.setErrorProperties(this.gLogger, resObj, e, "Error is: ");
    }
    return resObj;
  }
}
