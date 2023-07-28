package com.tcs.sgv.dcps.service;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.UpdatePranForEmpDaoImpl;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UpdatePranForEmpServiceImpl extends ServiceImpl
{
  private final Log gLogger = LogFactory.getLog(getClass());
  private String gStrPostId = null;
  private String gStrLocationCode = null;
  private Long gLngPostId = null;
  private HttpServletRequest request = null;
  private ServiceLocator serv = null;
  private HttpSession session = null;
  private Date gDtCurDate = null;
  private String gStrUserId = null;
  Long gLngUserId = null;
  private Long gLngDBId = null;
  private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/dcps/DCPSConstants");

  private void setSessionInfo(Map inputMap)
  {
    try
    {
      this.request = ((HttpServletRequest)inputMap.get("requestObj"));
      this.serv = ((ServiceLocator)inputMap.get("serviceLocator"));
      this.session = this.request.getSession();
      this.gStrPostId = SessionHelper.getPostId(inputMap).toString();
      this.gLngPostId = SessionHelper.getPostId(inputMap);
      this.gStrLocationCode = SessionHelper.getLocationCode(inputMap);
      this.gLngUserId = SessionHelper.getUserId(inputMap);
      this.gStrUserId = this.gLngUserId.toString();
      this.gLngDBId = SessionHelper.getDbId(inputMap);
      this.gDtCurDate = SessionHelper.getCurDate();
    } catch (Exception e) {
      this.gLogger.error(" Error is : " + e, e);
    }
  }

  private void setSessionInfoSchdlr(Map inputMap)
  {
    try {
      this.serv = ((ServiceLocator)inputMap.get("serviceLocator"));

      Map SchedlrLoginMap = (HashMap)inputMap.get("baseLoginMap");
      this.gStrPostId = String.valueOf(SchedlrLoginMap.get("postId"));
      this.gLngPostId = Long.valueOf(String.valueOf(SchedlrLoginMap.get("postId")));
      this.gStrLocationCode = String.valueOf(SchedlrLoginMap.get("locationCode"));
      this.gLngUserId = Long.valueOf(String.valueOf(SchedlrLoginMap.get("userId")));
      this.gStrUserId = String.valueOf(SchedlrLoginMap.get("userId"));
      this.gLngDBId = Long.valueOf(String.valueOf(SchedlrLoginMap.get("dbId")));
      this.gDtCurDate = new Date();
    } catch (Exception e) {
      this.gLogger.error(" Error is : " + e, e);
    }
  }

  public ResultObject getEmpInfoFromSevaarthId(Map<String, Object> inputMap)
  {
    this.gLogger.error(" getEmpInfoFromSevaarthId is : ");

    ResultObject resObj = new ResultObject(0, "FAIL");
    HttpServletRequest request = (HttpServletRequest)inputMap.get("requestObj");
    ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");

    UpdatePranForEmpDaoImpl lObjEmployeeInfoDAO = new UpdatePranForEmpDaoImpl(null, serv.getSessionFactory());
    try
    {
      String lStrSevaarthId = StringUtility.getParameter("SevaarthId", request);
      this.gLogger.error(" lStrSevaarthId is : " + lStrSevaarthId);
      String lStrPranNo = StringUtility.getParameter("PranNo", request);
      this.gLogger.error(" lStrPranNo is : " + lStrPranNo);

      List lLstEmpDeselect = null;

      lLstEmpDeselect = lObjEmployeeInfoDAO.getEmpInfoFromSevaarthId(lStrSevaarthId, lStrPranNo);

      if ((lLstEmpDeselect == null) || (lLstEmpDeselect.size() <= 0)) 
      inputMap.put("DESELECTEMPLIST", lLstEmpDeselect);
    }
    catch (Exception e)
    {
      IFMSCommonServiceImpl.setErrorProperties(this.gLogger, resObj, e, "Error is : ");
    }
    label187: resObj.setResultValue(inputMap);
    resObj.setViewName("UpdatePranOfEmp");
    return resObj;
  }

  public ResultObject CheckPranActiveOrNot(Map objectArgs)
    throws Exception
  {
    this.gLogger.info("inside CheckPranActiveOrNot");
    ResultObject resultObject = new ResultObject(0);
    ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
    Map loginDetailsMap = (Map)objectArgs.get("baseLoginMap");
    HttpServletRequest request = (HttpServletRequest)objectArgs.get("requestObj");

    String lStrSevaarthId = StringUtility.getParameter("SevaarthId", request);
    String PranNo = StringUtility.getParameter("PranNo", request);
    this.gLogger.info("lStrSevaarthIdis  inside the CheckPranActiveOrNot:" + lStrSevaarthId);
    this.gLogger.info("lStrSevaarthIdis  inside the CheckPranActiveOrNot PranNo:" + PranNo);

    int PranStatus = 0;
    String RegStatus = "";
    String DcpsOrGpf = "";
    String flag = "valid";
    this.gLogger.info("flag after intiliaziation is :" + flag);
    try
    {
      UpdatePranForEmpDaoImpl lObjEmployeeInfoDAO = new UpdatePranForEmpDaoImpl(null, serv.getSessionFactory());

      List lLstEmployeeInfo = lObjEmployeeInfoDAO.CheckPranActiveOrNot(lStrSevaarthId, PranNo);

      List lLstEmployeePostInfo = lObjEmployeeInfoDAO.CheckPostActiveOrNot(lStrSevaarthId, PranNo);
      if ((lLstEmployeePostInfo != null) && (!(lLstEmployeePostInfo.isEmpty())))
      {
        this.gLogger.info("Inside post is active :");
        flag = "valid";
      }
      else
      {
        this.gLogger.info("Inside post is Inactive :");
        flag = "InactivePost";
      }

      if ((lLstEmployeeInfo != null) && (!(lLstEmployeeInfo.isEmpty())))
      {
        this.gLogger.info("INSIDE THE IF OF");
        for (int i = 0; i < lLstEmployeeInfo.size(); ++i)
        {
          this.gLogger.info("INSIDE THE IF FOR OF");
          Object[] obj = (Object[])lLstEmployeeInfo.get(i);

          this.gLogger.info("lArrObj[0] is :" + obj[0]);

          this.gLogger.info(" Insid  the for of NoOfPayess BFGHFGHNG VALUE OF I" + i);

          this.gLogger.info(" Insid  the for of NoOfPayess BFGHFGHNG VALUE OF I nmbm");
          if (obj[0] != null)
          {
            String PranStatustemp = "";
            this.gLogger.info("lArrObj[0] is :" + obj[0]);
            PranStatustemp = obj[0].toString();
            this.gLogger.info("PranStatustemp is :" + PranStatustemp);
            PranStatus = Integer.parseInt(PranStatustemp);
            this.gLogger.info("PranStatus is :" + PranStatus);

            if (PranStatus == 1)
            {
              this.gLogger.info("iNSIDE THE SETTING FLAG DEACTIVATE");

              flag = "activate";
            }

          }

        }

        this.gLogger.info("flag after if intiliaziation is :" + flag); 
      }

      flag = "Invalid";
      this.gLogger.info("Going Inside the if of else");
    }
    catch (Exception e)
    {
      e.printStackTrace();
      this.gLogger.info("Exception is " + e);
    }

    this.gLogger.info("Value of at the end " + PranStatus);
    this.gLogger.info("Value of at the flag " + flag);

    String lSBStatus = getResponseXMLDocForGroup(flag).toString();
    String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString
      ();

    objectArgs.put("ajaxKey", lStrResult);
    resultObject.setResultValue(objectArgs);
    resultObject.setViewName("ajaxData");
    return resultObject;
  }

  private StringBuilder getResponseXMLDocForGroup(String flag)
  {
    StringBuilder lStrBldXML = new StringBuilder();

    lStrBldXML.append("<XMLDOC>");

    lStrBldXML.append("<lStrRetiringYear>");
    lStrBldXML.append(flag);
    lStrBldXML.append("</lStrRetiringYear>");
    lStrBldXML.append("</XMLDOC>");

    return lStrBldXML;
  }

  public ResultObject UpdatePranDeActiveStatus(Map<String, Object> inputMap)
    throws Exception
  {
    this.gLogger.info("Going InsideupdateDCPSAccountMaintainByForDepu ");
    ResultObject resObj = new ResultObject(0, "FAIL");
    HttpServletRequest request = (HttpServletRequest)inputMap.get("requestObj");
    ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");

    Map loginDetailsMap = (Map)inputMap.get("baseLoginMap");
    long postId = StringUtility.convertToLong(loginDetailsMap.get("primaryPostId").toString()).longValue();
    long userId = StringUtility.convertToLong(loginDetailsMap.get("userId").toString()).longValue();
    UpdatePranForEmpDaoImpl lObjEmployeeInfoDAO = new UpdatePranForEmpDaoImpl(null, serv.getSessionFactory());
    Boolean lBlFlag = Boolean.valueOf(false);
    try {
      String lStrSevaarthId = StringUtility.getParameter("txtSevaarthId", request);
      this.gLogger.info("Going InsideupdateDCPSAccountMaintainByForDepu " + lStrSevaarthId);
      String DcpsEmpId = StringUtility.getParameter("txtDcpsEmpId", request);
      this.gLogger.info("Going InsideupdateDCPSAccountMaintainByForDepu " + DcpsEmpId);
      String PranNo = StringUtility.getParameter("txtPranNo", request);
      this.gLogger.info("Going InsideupdateDCPSAccountMaintainByForDepu " + PranNo);
      String remarks = StringUtility.getParameter("txtRemark", request);
      this.gLogger.info("Going InsideupdateDCPSAccountMaintainByForDepu " + remarks);
      this.gLogger.info("Going postId " + postId);
      this.gLogger.info("Going userId " + userId);

      if ((!(PranNo.equals(""))) && (!(DcpsEmpId.equals(""))) && (!(remarks.equals("")))) {
        this.gLogger.info("Going InsideupdateDCPSAccountMaintainByForDepu  update of ");
        Long MstDcpsEmpId = Long.valueOf(Long.parseLong(DcpsEmpId));
        lObjEmployeeInfoDAO.UpdatePranDeActiveStatus(MstDcpsEmpId.longValue(), lStrSevaarthId, PranNo, remarks, userId, postId);
        lBlFlag = Boolean.valueOf(true);
      }

      this.gLogger.info("at the end flag is   update of " + lBlFlag);
    }
    catch (Exception e) {
      IFMSCommonServiceImpl.setErrorProperties(this.gLogger, resObj, e, "Error is : ");
    }
    String lSBStatus = getUpdateResponseXMLDoc(lBlFlag).toString();
    String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

    inputMap.put("ajaxKey", lStrResult);
    resObj.setResultValue(inputMap);
    resObj.setViewName("ajaxData");
    return resObj;
  }

  private StringBuilder getUpdateResponseXMLDoc(Boolean flag) {
    StringBuilder lStrBldXML = new StringBuilder();

    lStrBldXML.append("<XMLDOC>");
    lStrBldXML.append("<FLAG>");
    lStrBldXML.append(flag);
    lStrBldXML.append("</FLAG>");
    lStrBldXML.append("</XMLDOC>");
    return lStrBldXML;
  }
}