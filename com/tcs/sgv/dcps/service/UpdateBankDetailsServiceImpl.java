
package com.tcs.sgv.dcps.service;

import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.UpdateBankDetailsDAOImpl;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;
import edu.emory.mathcs.backport.java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class UpdateBankDetailsServiceImpl
extends ServiceImpl {
    private final Log gLogger;
    Session ghibSession;

    public UpdateBankDetailsServiceImpl() {
        this.gLogger = LogFactory.getLog(this.getClass());
        this.ghibSession = null;
    }

    public ResultObject loadBankDetails(Map inputMap) {
        ResultObject resObj = new ResultObject(0, (Object)"FAIL");
        ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
        HttpServletRequest request = (HttpServletRequest)inputMap.get("requestObj");
        try {
            DcpsCommonDAOImpl lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
            UpdateBankDetailsDAOImpl lObjUpdateBankDetailsDAO = new UpdateBankDetailsDAOImpl(null, serv.getSessionFactory());
            Long bankId = 0L;
            List lLstBankNames = lObjDcpsCommonDAO.getBankNames();
            inputMap.put("BANKNAMES", lLstBankNames);
            if (StringUtility.getParameter((String)"bankId", (HttpServletRequest)request).toString() != null && StringUtility.getParameter((String)"bankId", (HttpServletRequest)request).toString() != "") {
                bankId = Long.parseLong(StringUtility.getParameter((String)"bankId", (HttpServletRequest)request).toString());
                inputMap.put("bankId", bankId);
            }
            if (bankId != -1 || bankId != null) {
                List lLstBranchNames = lObjUpdateBankDetailsDAO.getBranchNames(bankId);
                int size = lLstBranchNames.size();
                inputMap.put("BRANCHNAMESMST", lLstBranchNames);
                inputMap.put("branchListSize", size);
            }
            resObj.setResultValue((Object)inputMap);
            resObj.setViewName("UpdateBankDetails");
        }
        catch (Exception e) {
            resObj.setResultValue((Object)null);
            resObj.setThrowable((Throwable)e);
            resObj.setResultCode(-1);
            resObj.setViewName("errorPage");
        }
        return resObj;
    }

    public ResultObject SaveBankDetails(Map inputMap) {
        ResultObject resObj = new ResultObject(0, (Object)"FAIL");
        ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
        HttpServletRequest request = (HttpServletRequest)inputMap.get("requestObj");
        Object[] lStrArrBranchId = null;
        Object[] lStrArrNewAddress = null;
        Object[] lStrArrPinCode = null;
        Long[] lStrBranchIdFinal = null;
        String[] lStrNewAddressFinal = null;
        Long[] lStrPinCodeFinal = null;
        Boolean lBFlag = null;
        try {
            UpdateBankDetailsDAOImpl lObjUpdateBankDetailsDAO = new UpdateBankDetailsDAOImpl(null, serv.getSessionFactory());
            DcpsCommonDAOImpl lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
            String lStrBranchId = StringUtility.getParameter((String)"branchList", (HttpServletRequest)request);
            String lStrNewAddress = StringUtility.getParameter((String)"newAddress", (HttpServletRequest)request);
            String lStrPinCode = StringUtility.getParameter((String)"pinCode", (HttpServletRequest)request);
            lStrArrBranchId = lStrBranchId.split("~");
            lStrArrNewAddress = lStrNewAddress.split("~");
            lStrArrPinCode = lStrPinCode.split("~");
            this.gLogger.info((Object)("lStrArrBranchId----" + Arrays.toString((Object[])lStrArrBranchId)));
            this.gLogger.info((Object)("lStrArrNewAddress----" + Arrays.toString((Object[])lStrArrNewAddress)));
            this.gLogger.info((Object)("lStrArrPinCode----" + Arrays.toString((Object[])lStrArrPinCode)));
            lObjUpdateBankDetailsDAO.updateBankDetails((String[])lStrArrBranchId, (String[])lStrArrNewAddress, (String[])lStrArrPinCode);
        }
        catch (Exception e) {
            resObj.setResultValue((Object)null);
            resObj.setThrowable((Throwable)e);
            resObj.setResultCode(-1);
            resObj.setViewName("errorPage");
        }
        String lSBStatus = this.getResponseXMLDoc(lBFlag).toString();
        String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
        inputMap.put("ajaxKey", lStrResult);
        resObj.setResultValue((Object)inputMap);
        resObj.setViewName("ajaxData");
        return resObj;
    }

    private StringBuilder getResponseXMLDoc(Boolean lBlFlag) {
        StringBuilder lStrBldXML = new StringBuilder();
        lStrBldXML.append("<XMLDOC>");
        lStrBldXML.append("  <txtGroup>");
        lStrBldXML.append(lBlFlag);
        lStrBldXML.append("  </txtGroup>");
        lStrBldXML.append("</XMLDOC>");
        return lStrBldXML;
    }
}