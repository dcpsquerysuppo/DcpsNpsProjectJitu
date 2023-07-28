package com.tcs.sgv.dcps.service;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttachmentMstDAOImpl;
import com.tcs.sgv.common.valueobject.CmnAttachmentMpg;
import com.tcs.sgv.common.valueobject.CmnAttachmentMst;
import com.tcs.sgv.common.valueobject.CmnAttdocMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.FormS1DAO;
import com.tcs.sgv.dcps.dao.FormS1DAOImpl;
import com.tcs.sgv.dcps.dao.FormS1UpdateDAO;
import com.tcs.sgv.dcps.dao.FormS1UpdateDAOImpl;
import com.tcs.sgv.dcps.dao.UploadPranDAO;
import com.tcs.sgv.dcps.dao.UploadPranDaoImpl;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;
import com.tcs.sgv.dcps.valueobject.FrmFormS1Dtls;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.UploadPranNo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class FormS1Update
extends ServiceImpl {
    private final Log gLogger;
    private String gStrPostId;
    private String gStrUserId;
    private String gStrLocale;
    private Locale gLclLocale;
    private Long gLngLangId;
    private Long gLngDBId;
    private Date gDtCurDate;
    private HttpServletRequest request;
    private ServiceLocator serv;
    private HttpSession session;
    Long gLngPostId;
    Long gLngUserId;
    Date gDtCurrDt;
    String gStrLocationCode;
    static HashMap sMapUserLoc = new HashMap();
    String gStrUserLocation;

    public FormS1Update() {
        this.gLogger = LogFactory.getLog(this.getClass());
        this.gStrPostId = null;
        this.gStrUserId = null;
        this.gStrLocale = null;
        this.gLclLocale = null;
        this.gLngLangId = null;
        this.gLngDBId = null;
        this.gDtCurDate = null;
        this.request = null;
        this.serv = null;
        this.session = null;
        this.gLngPostId = null;
        this.gLngUserId = null;
        this.gDtCurrDt = null;
        this.gStrLocationCode = null;
        this.gStrUserLocation = null;
    }

    private void setSessionInfo(Map inputMap) {
        try {
            this.request = (HttpServletRequest)inputMap.get("requestObj");
            this.session = this.request.getSession();
            this.serv = (ServiceLocator)inputMap.get("serviceLocator");
            this.gLclLocale = new Locale(SessionHelper.getLocale((HttpServletRequest)this.request));
            this.gStrLocale = SessionHelper.getLocale((HttpServletRequest)this.request);
            this.gLngLangId = SessionHelper.getLangId((Map)inputMap);
            this.gLngPostId = SessionHelper.getPostId((Map)inputMap);
            this.gStrPostId = this.gLngPostId.toString();
            this.gLngUserId = SessionHelper.getUserId((Map)inputMap);
            this.gStrUserId = this.gLngUserId.toString();
            this.gStrLocationCode = SessionHelper.getLocationCode((Map)inputMap);
            this.gLngDBId = SessionHelper.getDbId((Map)inputMap);
            this.gDtCurDate = SessionHelper.getCurDate();
            this.gDtCurrDt = SessionHelper.getCurDate();
        }
        catch (Exception var2_2) {
            // empty catch block
        }
    }

    public ResultObject getEmpListForFormS1Edit(Map inputMap) throws Exception {
        ResultObject resObj = new ResultObject(0, (Object)"FAIL");
        List lstEmpForFrmS1Edit = null;
        List empDesigList = null;
        String strDDOCode = null;
        String txtSearch = null;
        String flag = null;
        String sevaarthId = null;
        String IsDeputation = null;
        int DepSize = 100;
        try {
            this.setSessionInfo(inputMap);
            IsDeputation = StringUtility.getParameter((String)"IsDeputation", (HttpServletRequest)this.request);
            DcpsCommonDAOImpl lObjDcpsCommonDao = new DcpsCommonDAOImpl(null, this.serv.getSessionFactory());
            FormS1UpdateDAOImpl lObjFormS1UpdateDAO = new FormS1UpdateDAOImpl(null, this.serv.getSessionFactory());
            strDDOCode = lObjDcpsCommonDao.getDdoCodeForDDO(this.gLngPostId);
            this.gLogger.info((Object)("logged in ddo code: " + strDDOCode));
            txtSearch = StringUtility.getParameter((String)"searchTxt", (HttpServletRequest)this.request);
            sevaarthId = StringUtility.getParameter((String)"txtSevaarthId1", (HttpServletRequest)this.request);
            this.gLogger.info((Object)("txtSearch is ***" + txtSearch));
            this.gLogger.info((Object)("sevaarthId is ***" + sevaarthId));
            flag = StringUtility.getParameter((String)"flag", (HttpServletRequest)this.request);
            if (sevaarthId != null && !sevaarthId.equals("")) {
                this.gLogger.info((Object)("sevaarthId is ***" + sevaarthId));
                txtSearch = sevaarthId;
            }
            this.gLogger.info((Object)("IsDeputation is Y#########"+IsDeputation));
            if(IsDeputation.equals("Y"))
            {   this.gLogger.info((Object)("txtSearch is Y in if#########"+txtSearch));
            this.gLogger.info((Object)("IsDeputation is Y#########"+IsDeputation));
            	 if (txtSearch != null && !txtSearch.equals("")) {
            	lstEmpForFrmS1Edit = lObjFormS1UpdateDAO.getEmpListForFrmS1Edit(strDDOCode, flag, txtSearch, IsDeputation);
                empDesigList = lObjFormS1UpdateDAO.getEmpDesigList(strDDOCode);
            	DepSize=lstEmpForFrmS1Edit.size();
            	 }
            }else
            {
            lstEmpForFrmS1Edit = lObjFormS1UpdateDAO.getEmpListForFrmS1Edit(strDDOCode, flag, txtSearch, IsDeputation);
            empDesigList = lObjFormS1UpdateDAO.getEmpDesigList(strDDOCode);
            }
            gLogger.info("##################DepSizein update"+DepSize);
            inputMap.put("DepSize", DepSize);
            inputMap.put("IsDeputation", IsDeputation);
            inputMap.put("empList", lstEmpForFrmS1Edit);
            inputMap.put("DDOCode", strDDOCode);
            inputMap.put("empDesigList", empDesigList);
            resObj.setResultValue((Object)inputMap);
            resObj.setViewName("empListForFormS1Update");
        }
        catch (Exception e) {
            resObj.setResultValue((Object)null);
            resObj.setThrowable((Throwable)e);
            resObj.setResultCode(-1);
            resObj.setViewName("errorPage");
        }
        return resObj;
    }

    public ResultObject getFormS1EditForEmp(Map inputMap) throws Exception {
        ResultObject resObj = new ResultObject(0, (Object)"FAIL");
        String strEmpSevarthId = null;
        String strEmpName = null;
        String strDDOCode = null;
        String DOJ = null;
        String dsgnName = null;
        String DcpsId = null;
        //String PanNo = null;////$t27Jun2022
        List lstRelationList = null;
        List lstRelationListNS=null;////$t 4-6-2021 remove spouse
        
        String IsDeputation = null;
        try {
            this.setSessionInfo(inputMap);
            DcpsCommonDAOImpl lObjDcpsCommonDao = new DcpsCommonDAOImpl(null, this.serv.getSessionFactory());
            FormS1UpdateDAOImpl lObjFormS1UpdateDAO = new FormS1UpdateDAOImpl(null, this.serv.getSessionFactory());
            strDDOCode = lObjDcpsCommonDao.getDdoCodeForDDO(this.gLngPostId);
            this.gLogger.info((Object)("logged in ddo code: " + strDDOCode));
            
            IsDeputation = StringUtility.getParameter((String)"IsDeputation", (HttpServletRequest)this.request);
            gLogger.info("####################IsDeputation is "+IsDeputation);
            strEmpSevarthId = StringUtility.getParameter((String)"empSevarthId", (HttpServletRequest)this.request);
            strEmpName = StringUtility.getParameter((String)"empName", (HttpServletRequest)this.request);
            DOJ = StringUtility.getParameter((String)"DOJ", (HttpServletRequest)this.request);
            dsgnName = StringUtility.getParameter((String)"dsgnName", (HttpServletRequest)this.request);
            DcpsId = StringUtility.getParameter((String)"DcpsId", (HttpServletRequest)this.request);
         //   PanNo = StringUtility.getParameter((String)"PanNo", (HttpServletRequest)this.request);////$t27Jun2022
            this.gLogger.info((Object)("EmpSevarthId is: " + strEmpSevarthId));
            this.gLogger.info((Object)("strEmpName is: " + strEmpName));
            this.gLogger.info((Object)("DOJ is: " + DOJ));
            this.gLogger.info((Object)("dsgnName is: " + dsgnName));
            this.gLogger.info((Object)("DcpsId is: " + DcpsId));
            lstRelationList = lObjFormS1UpdateDAO.getRelationList();
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();
            this.gLogger.info((Object)("Current date: " + dateFormat.format(date)));
            inputMap.put("IsDeputation", IsDeputation);
            inputMap.put("curretDate", dateFormat.format(date).toString());
            inputMap.put("EmpSevarthId", strEmpSevarthId);
            inputMap.put("EmpName", strEmpName);
            inputMap.put("DDOCode", strDDOCode);
            inputMap.put("DOJ", DOJ);
            inputMap.put("dsgnName", dsgnName);
            inputMap.put("DcpsId", DcpsId);
          //  inputMap.put("PanNo", PanNo);////$t27Jun2022
            inputMap.put("RelationList", lstRelationList);
            
            lstRelationListNS=(List) lstRelationList.subList(1, 20);////$t 4-6-2021 remove spouse
            inputMap.put("RelationListNS", lstRelationListNS);////$t 4-6-2021 remove spouse
            resObj.setResultValue((Object)inputMap);
            resObj.setViewName("empFormS1Update");
        }
        catch (Exception e) {
            resObj.setResultValue((Object)null);
            resObj.setThrowable((Throwable)e);
            resObj.setResultCode(-1);
            resObj.setViewName("errorPage");
        }
        return resObj;
    }

    public ResultObject saveFormS1Dtls(Map inputMap) throws Exception {
        
        /*Opgm save doc*/
ResultObject resObj = new ResultObject(0, (Object)"FAIL");
        gLogger.info("Inside Get uploadGr");
        //ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
        ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
        HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
        Long attachment_Id_order=0l;
        Object[][] xlsData = null;
        UploadPranNo objUploadPranNo=null;
        String desc="";
        Long attachId=0l;
        String isExcel="Yes";
     
            setSessionInfo(inputMap);
           // PRTrackingDAO lobjPRTrackingDAOImpl = new PRTrackingDAOImpl(PRTrackingDAOImpl.class, this.serv.getSessionFactory());
        //	UploadPranDAO lObjUploadPranNo = new UploadPranDaoImpl(UploadPranNo.class, serv.getSessionFactory());
            String currRowNum="1";
            inputMap.put("rowNumber",currRowNum);
            inputMap.put("attachmentName","orderId");
            try{
            resObj = serv.executeService("FILE_UPLOAD_VOGEN",inputMap);
            Map resultMap=(Map)resObj.getResultValue();
            resObj = serv.executeService("FILE_UPLOAD_SRVC", inputMap);
            resultMap = (Map) resObj.getResultValue();
            if(resultMap.get("AttachmentId_orderId")!=null)
                attachment_Id_order = (Long) resultMap.get("AttachmentId_orderId"); 
            gLogger.info("attachment_Id_order is "+attachment_Id_order);

            
            if (attachment_Id_order != null) {
             
            	gLogger.info("attachment_Id_order inside ******************* " );
                
                CmnAttachmentMstDAOImpl mnAttachmentMstDAO = new CmnAttachmentMstDAOImpl(CmnAttachmentMst.class, serv.getSessionFactory());
                CmnAttachmentMst cmnAttachmentMst = mnAttachmentMstDAO.findByAttachmentId(attachment_Id_order);
                Iterator lObjIterator = cmnAttachmentMst.getCmnAttachmentMpgs().iterator();
                if(cmnAttachmentMst.getAttachmentId() > 0)
                {
                	attachId=cmnAttachmentMst.getAttachmentId();
                }
                gLogger.info("attachment_Id_order inside **********2********* " );
                while (lObjIterator != null && lObjIterator.hasNext()) {
                    CmnAttachmentMpg cmnAttachmentMpg = (CmnAttachmentMpg) lObjIterator.next();
                    if(cmnAttachmentMpg.getAttachmentDesc()!=null)
                    {
                    	 desc=cmnAttachmentMpg.getAttachmentDesc();
                    	
                    }
                    
                    CmnAttdocMst cmnAttDocMst = (CmnAttdocMst) cmnAttachmentMpg.getCmnAttdocMsts().iterator().next();
                    gLogger.info("attachment_Id_order inside **********3******** " );
                    String lStrFileName = cmnAttachmentMpg.getOrgFileName().trim();
                    int lIntDotPos = lStrFileName.lastIndexOf(".");
                    String lStrExtension = lStrFileName.substring(lIntDotPos);
                    Integer lIntRowSize = 0;
                    Long fileId=0l;	       
                    }
            }
    }catch(Exception e){
    	
    	e.getStackTrace();
    }
            /*Opgm save doc*/
    	
    	
        List lstEmpForFrmS1Edit = null;
        List empDesigList = null;
        String strEmpSevarthId = null;
        String strEmpName = null;
        String empFatherName = null;
        String strDDOCode = null;
        String DcpsId = null;
        String dsgnName = null;
        String DOJ = null;
        String presentAddFlatNo = null;
        String presentAddBuilding = null;
        String presentAddTaluka = null;
        String presentAddDist = null;
        String presentAddState = null;
        String presentAddCountry = null;
        String presentAddPin = null;
        Object presentNotSamePerm = null;
        String permanentAddFlatNo = null;
        String permanentAddBuilding = null;
        String permanentAddTaluka = null;
        String permanentAddDist = null;
        String permanentAddState = null;
        String permanentAddCountry = null;
        String permanentAddPin = null;
        String phoneSTDCode = null;
        String phoneNo = null;
        String mobileNo = null;
        String emailId = null;
        String nominee1Name = null;
        String nominee1DOB = null;
        String nominee1Relation = null;
        String nominee1Percent = null;
        String nominee1Guardian = null;
        String nominee1InvalidCondition = null;
        String nominee2Name = null;
        String nominee2DOB = null;
        String nominee2Relation = null;
        String nominee2Percent = null;
        String nominee2Guardian = null;
        String nominee2InvalidCondition = null;
        String nominee3Name = null;
        String nominee3DOB = null;
        String nominee3Relation = null;
        String nominee3Percent = null;
        String nominee3Guardian = null;
        String nominee3InvalidCondition = null;
        Object formS1Id = null;
        //--------------------------------------------
        String empMotherName = null;
        String empCityOfBirth = null;
        String empCountryOfBirth = null;
        String empSpouseName = null;
        String marriedOrNot = null;

        String empCountryOfTax = null;
        String TaxResAddress = null;
        String TaxResCity = null;
        String TaxResState = null;
        String TaxResPostCode = null;
        String empTINOrPAN = null;
        String empTIN = null;
        String usPerson = null;
        //jitu
        String OrphanPerson=null;
        
        //-------------------------------------------
        
        //new start
        
        String IsDeputation = null; 
        String FormS1DepUpdateFlag = null;
        try {
            this.setSessionInfo(inputMap);
            DcpsCommonDAOImpl lObjDcpsCommonDao = new DcpsCommonDAOImpl(null, this.serv.getSessionFactory());
            FormS1UpdateDAOImpl lObjFormS1UpdateDAO = new FormS1UpdateDAOImpl((Class)FrmFormS1Dtls.class, this.serv.getSessionFactory());
            FrmFormS1Dtls ffs = new FrmFormS1Dtls();
            strDDOCode = lObjDcpsCommonDao.getDdoCodeForDDO(this.gLngPostId);
            this.gLogger.info((Object)("logged in ddo code: " + strDDOCode));
            IsDeputation = StringUtility.getParameter((String)"IsDeputation", (HttpServletRequest)this.request);
            this.gLogger.info((Object)("######IsDeputation " + IsDeputation));
            strEmpSevarthId = StringUtility.getParameter((String)"sevarthId", (HttpServletRequest)this.request);
            strEmpName = StringUtility.getParameter((String)"empName", (HttpServletRequest)this.request);
            empFatherName = StringUtility.getParameter((String)"empFatherName", (HttpServletRequest)this.request);
            DOJ = this.parseDate(StringUtility.getParameter((String)"DOJ", (HttpServletRequest)this.request));
            dsgnName = StringUtility.getParameter((String)"dsgnName", (HttpServletRequest)this.request);
            gLogger.info("########DESIGNATION####################"+dsgnName);
            DcpsId = StringUtility.getParameter((String)"DcpsId", (HttpServletRequest)this.request);
            presentAddFlatNo = StringUtility.getParameter((String)"presentAddFlatNo", (HttpServletRequest)this.request);
            presentAddBuilding = StringUtility.getParameter((String)"presentAddBuilding", (HttpServletRequest)this.request);
            presentAddTaluka = StringUtility.getParameter((String)"presentAddTaluka", (HttpServletRequest)this.request);
            presentAddDist = StringUtility.getParameter((String)"presentAddDist", (HttpServletRequest)this.request);
            presentAddState = StringUtility.getParameter((String)"presentAddState", (HttpServletRequest)this.request);
            presentAddCountry = StringUtility.getParameter((String)"presentAddCountry", (HttpServletRequest)this.request);
            presentAddPin = StringUtility.getParameter((String)"presentAddPin", (HttpServletRequest)this.request);
            permanentAddFlatNo = StringUtility.getParameter((String)"permanentAddFlatNo", (HttpServletRequest)this.request);
            permanentAddBuilding = StringUtility.getParameter((String)"permanentAddBuilding", (HttpServletRequest)this.request);
            permanentAddTaluka = StringUtility.getParameter((String)"permanentAddTaluka", (HttpServletRequest)this.request);
            permanentAddDist = StringUtility.getParameter((String)"permanentAddDist", (HttpServletRequest)this.request);
            permanentAddState = StringUtility.getParameter((String)"permanentAddState", (HttpServletRequest)this.request);
            permanentAddCountry = StringUtility.getParameter((String)"permanentAddCountry", (HttpServletRequest)this.request);
            permanentAddPin = StringUtility.getParameter((String)"permanentAddPin", (HttpServletRequest)this.request);
            phoneSTDCode = StringUtility.getParameter((String)"phoneSTDCode", (HttpServletRequest)this.request);
            phoneNo = StringUtility.getParameter((String)"phoneNo", (HttpServletRequest)this.request);
            mobileNo = StringUtility.getParameter((String)"mobileNo", (HttpServletRequest)this.request);
            emailId = StringUtility.getParameter((String)"emailId", (HttpServletRequest)this.request);
            nominee1Name = StringUtility.getParameter((String)"nominee1Name", (HttpServletRequest)this.request);
            nominee1DOB = this.parseDate(StringUtility.getParameter((String)"nominee1DOB", (HttpServletRequest)this.request));
            nominee1Relation = StringUtility.getParameter((String)"nominee1Relation", (HttpServletRequest)this.request);
            if(nominee1Relation.equals("-1"))////$t 4-6-2019 remove spouse
            nominee1Relation = StringUtility.getParameter((String)"nominee1RelationNS", (HttpServletRequest)this.request);
            
            nominee1Percent = StringUtility.getParameter((String)"nominee1Percent", (HttpServletRequest)this.request);
            nominee1Guardian = StringUtility.getParameter((String)"nominee1Guardian", (HttpServletRequest)this.request);
            nominee1InvalidCondition = StringUtility.getParameter((String)"nominee1InvalidCondition", (HttpServletRequest)this.request);
            nominee2Name = StringUtility.getParameter((String)"nominee2Name", (HttpServletRequest)this.request);
            nominee2DOB = this.parseDate(StringUtility.getParameter((String)"nominee2DOB", (HttpServletRequest)this.request));
            nominee2Relation = StringUtility.getParameter((String)"nominee2Relation", (HttpServletRequest)this.request);
            if(nominee2Relation.equals("-1"))////$t 4-6-2019 remove spouse
            nominee2Relation = StringUtility.getParameter((String)"nominee2RelationNS", (HttpServletRequest)this.request);
            
            nominee2Percent = StringUtility.getParameter((String)"nominee2Percent", (HttpServletRequest)this.request);
            nominee2Guardian = StringUtility.getParameter((String)"nominee2Guardian", (HttpServletRequest)this.request);
            nominee2InvalidCondition = StringUtility.getParameter((String)"nominee2InvalidCondition", (HttpServletRequest)this.request);
            nominee3Name = StringUtility.getParameter((String)"nominee3Name", (HttpServletRequest)this.request);
            nominee3DOB = this.parseDate(StringUtility.getParameter((String)"nominee3DOB", (HttpServletRequest)this.request));
            nominee3Relation = StringUtility.getParameter((String)"nominee3Relation", (HttpServletRequest)this.request);
            if(nominee3Relation.equals("-1"))////$t 4-6-2019 remove spouse
            nominee3Relation = StringUtility.getParameter((String)"nominee3RelationNS", (HttpServletRequest)this.request);
            
            nominee3Percent = StringUtility.getParameter((String)"nominee3Percent", (HttpServletRequest)this.request);
            nominee3Guardian = StringUtility.getParameter((String)"nominee3Guardian", (HttpServletRequest)this.request);
            nominee3InvalidCondition = StringUtility.getParameter((String)"nominee3InvalidCondition", (HttpServletRequest)this.request);
            //----------------------------------------------------------------------------------------------------------------------------
            empMotherName = StringUtility.getParameter((String)"empMotherName", (HttpServletRequest)this.request);
            empCityOfBirth = StringUtility.getParameter((String)"empCityOfBirth", (HttpServletRequest)this.request);
            empCountryOfBirth = StringUtility.getParameter((String)"empCountryOfBirth", (HttpServletRequest)this.request);
            empSpouseName = StringUtility.getParameter((String)"empSpouseName", (HttpServletRequest)this.request);
            marriedOrNot = StringUtility.getParameter((String)"marriedOrNot", (HttpServletRequest)this.request);

            empCountryOfTax = StringUtility.getParameter((String)"empCountryOfTax", (HttpServletRequest)this.request);
            TaxResAddress = StringUtility.getParameter((String)"TaxResAddress", (HttpServletRequest)this.request);
            TaxResCity = StringUtility.getParameter((String)"TaxResCity", (HttpServletRequest)this.request);
            TaxResState = StringUtility.getParameter((String)"TaxResState", (HttpServletRequest)this.request);
            TaxResPostCode = StringUtility.getParameter((String)"TaxResPostCode", (HttpServletRequest)this.request);
            empTINOrPAN = StringUtility.getParameter((String)"empTINOrPAN", (HttpServletRequest)this.request);
            empTIN = StringUtility.getParameter((String)"empTIN", (HttpServletRequest)this.request);
            usPerson = StringUtility.getParameter((String)"usPerson", (HttpServletRequest)this.request);
            //jitu
            OrphanPerson = StringUtility.getParameter((String)"OrphanPerson", (HttpServletRequest)this.request);
            System.out.println("OrphanPerson + ==="+OrphanPerson);
            //----------------------------------------------------------------------------------------------------------------------------
            this.gLogger.info((Object)("EmpSevarthId is: " + strEmpSevarthId));
            this.gLogger.info((Object)("strEmpName is: " + strEmpName));
            this.gLogger.info((Object)("DOJ is: " + DOJ));
            this.gLogger.info((Object)("dsgnName is: " + dsgnName));
            this.gLogger.info((Object)("DcpsId is: " + DcpsId));
            this.gLogger.info((Object)("empFatherName is: " + empFatherName));
            this.gLogger.info((Object)("presentAddFlatNo is: " + presentAddFlatNo));
            this.gLogger.info((Object)("presentAddBuilding is: " + presentAddBuilding));
            this.gLogger.info((Object)("presentAddTaluka is: " + presentAddTaluka));
            this.gLogger.info((Object)("presentAddDist is: " + presentAddDist));
            this.gLogger.info((Object)("presentAddState is: " + presentAddState));
            this.gLogger.info((Object)("presentAddCountry is: " + presentAddCountry));
            this.gLogger.info((Object)("presentAddPin is: " + presentAddPin));
            this.gLogger.info((Object)("permanentAddFlatNo is: " + permanentAddFlatNo));
            this.gLogger.info((Object)("permanentAddBuilding is: " + permanentAddBuilding));
            this.gLogger.info((Object)("permanentAddTaluka is: " + permanentAddTaluka));
            this.gLogger.info((Object)("permanentAddDist is: " + permanentAddDist));
            this.gLogger.info((Object)("permanentAddState is: " + permanentAddState));
            this.gLogger.info((Object)("permanentAddCountry is: " + permanentAddCountry));
            this.gLogger.info((Object)("permanentAddPin is: " + permanentAddPin));
            this.gLogger.info((Object)("phoneSTDCode is: " + phoneSTDCode));
            this.gLogger.info((Object)("phoneNo is: " + phoneNo));
            this.gLogger.info((Object)("mobileNo is: " + mobileNo));
            this.gLogger.info((Object)("emailId is: " + emailId));
            this.gLogger.info((Object)("nominee1Name is: " + nominee1Name));
            this.gLogger.info((Object)("nominee1DOB is: " + nominee1DOB));
            this.gLogger.info((Object)("nominee1Relation is: " + nominee1Relation));
            this.gLogger.info((Object)("nominee1Percent is: " + nominee1Percent));
            this.gLogger.info((Object)("nominee1Guardian is: " + nominee1Guardian));
            this.gLogger.info((Object)("nominee1InvalidCondition is: " + nominee1InvalidCondition));
            this.gLogger.info((Object)("nominee2Name is: " + nominee2Name));
            this.gLogger.info((Object)("nominee2DOB is: " + nominee2DOB));
            this.gLogger.info((Object)("nominee2Relation is: " + nominee2Relation));
            this.gLogger.info((Object)("nominee2Percent is: " + nominee2Percent));
            this.gLogger.info((Object)("nominee2Guardian is: " + nominee2Guardian));
            this.gLogger.info((Object)("nominee2InvalidCondition is: " + nominee2InvalidCondition));
            this.gLogger.info((Object)("nominee3Name is: " + nominee3Name));
            this.gLogger.info((Object)("nominee3DOB is: " + nominee3DOB));
            this.gLogger.info((Object)("nominee3Relation is: " + nominee3Relation));
            this.gLogger.info((Object)("nominee3Percent is: " + nominee3Percent));
            this.gLogger.info((Object)("nominee3Guardian is: " + nominee3Guardian));
            this.gLogger.info((Object)("nominee3InvalidCondition is: " + nominee3InvalidCondition));
            //-----------------------------------------------------------------------------------------------
            
            this.gLogger.info((Object)("empMotherName"+empMotherName));
            this.gLogger.info((Object)("empCityOfBirth"+empCityOfBirth));
            this.gLogger.info((Object)("empCountryOfBirth"+empCountryOfBirth));
            this.gLogger.info((Object)("empSpouseName"+empSpouseName));
            this.gLogger.info((Object)("marriedOrNot"+marriedOrNot));

            this.gLogger.info((Object)(" empCountryOfTax"+empCountryOfTax));
            this.gLogger.info((Object)("TaxResAddress"+TaxResAddress));
            this.gLogger.info((Object)("TaxResCity"+TaxResCity));
            this.gLogger.info((Object)("TaxResState"+TaxResState));
            this.gLogger.info((Object)(" TaxResPostCode"+TaxResPostCode));
            this.gLogger.info((Object)("empTINOrPAN"+empTINOrPAN));
            this.gLogger.info((Object)("empTIN"+empTIN));
            this.gLogger.info((Object)("usPerson"+usPerson));
            this.gLogger.info((Object)("OrphanPerson"+OrphanPerson));
            
            //-----------------------------------------------------------------------------------------------
            
            ffs.setSevarthId(strEmpSevarthId);
            ffs.setEmpName(strEmpName);
            ffs.setDesignation(dsgnName);
            ffs.setDcpsId(DcpsId);
            ffs.setDDOCode(strDDOCode);
            ffs.setDateOfJoining(DOJ);
            ffs.setEmpFatherName(empFatherName);
            ffs.setPresentAddFlatNo(presentAddFlatNo);
            ffs.setPresentAddBuilding(presentAddBuilding);
            ffs.setPresentAddTaluka(presentAddTaluka);
            ffs.setPresentAddDist(presentAddDist);
            ffs.setPresentAddState(presentAddState);
            ffs.setPresentAddCountry(presentAddCountry);
            ffs.setPresentAddPin(presentAddPin);
            ffs.setPermanentAddFlatNo(permanentAddFlatNo);
            ffs.setPermanentAddBuilding(permanentAddBuilding);
            ffs.setPermanentAddTaluka(permanentAddTaluka);
            ffs.setPermanentAddDist(permanentAddDist);
            ffs.setPermanentAddState(permanentAddState);
            ffs.setPresentAddCountry(permanentAddCountry);
            ffs.setPermanentAddPin(permanentAddPin);
            ffs.setPhoneSTDCode(phoneSTDCode);
            ffs.setPhoneNo(phoneNo);
            ffs.setMobileNo(mobileNo);
            ffs.setEmailId(emailId);
            ffs.setNominee1Name(nominee1Name);
            ffs.setDateOfJoining(nominee1DOB);
            ffs.setNominee1Relation(nominee1Relation);
            ffs.setNominee1Percent(nominee1Percent);
            ffs.setNominee1Guardian(nominee1Guardian);
            ffs.setNominee1InvalidCondition(nominee1InvalidCondition);
            ffs.setNominee2Name(nominee2Name);
            ffs.setDateOfJoining(nominee2DOB);
            ffs.setNominee2Relation(nominee2Relation);
            ffs.setNominee2Percent(nominee2Percent);
            ffs.setNominee2Guardian(nominee2Guardian);
            ffs.setNominee2InvalidCondition(nominee2InvalidCondition);
            ffs.setNominee3Name(nominee3Name);
            ffs.setDateOfJoining(nominee3DOB);
            ffs.setNominee3Relation(nominee3Relation);
            ffs.setNominee3Percent(nominee3Percent);
            ffs.setNominee3Guardian(nominee3Guardian);
            ffs.setNominee3InvalidCondition(nominee3InvalidCondition);
            //-----------------------------------------
            ffs.setEmpMotherName(empMotherName);
            ffs.setEmpCityOfBirth(empCityOfBirth);
            ffs.setEmpCountryOfBirth(empCountryOfBirth);
            ffs.setEmpSpouseName(empSpouseName);
            ffs.setMarriedOrNot(marriedOrNot);
            ffs.setEmpCountryOfTax(empCountryOfTax);
            ffs.setTaxResAddress(TaxResAddress);
            ffs.setTaxResCity(TaxResCity);
            ffs.setTaxResState(TaxResState);
            ffs.setTaxResPostCode(TaxResPostCode);
            ffs.setEmpTINOrPAN(empTINOrPAN);
            ffs.setEmpTIN(empTIN);
            ffs.setUsPerson(usPerson);
            
            
            Date date = new Date();
            ffs.setCreatedDate(date);
           // lObjFormS1UpdateDAO.insertRecordToS1(ffs, DOJ, nominee1DOB, nominee2DOB, nominee3DOB, strDDOCode,attachment_Id_order);
            lObjFormS1UpdateDAO.insertRecordToS1(ffs, DOJ, nominee1DOB, nominee2DOB, nominee3DOB, strDDOCode,OrphanPerson,attachment_Id_order);
            if (attachment_Id_order!=null) {
            	lObjFormS1UpdateDAO.updateOrphanDoc(attachment_Id_order,strEmpSevarthId);
			}
            if(IsDeputation.equals("Y")){
            lObjFormS1UpdateDAO.insertDepFlagDdo(strDDOCode,strEmpSevarthId);
             FormS1DepUpdateFlag="Y";
             
            }
            else{
            lstEmpForFrmS1Edit = lObjFormS1UpdateDAO.getEmpListForFrmS1Edit(strDDOCode, "0", "0","N");
            empDesigList = lObjFormS1UpdateDAO.getEmpDesigList(strDDOCode);
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            this.gLogger.info((Object)("Current date: " + dateFormat.format(date)));
            
            inputMap.put("FormS1DepUpdateFlag", FormS1DepUpdateFlag);
            inputMap.put("IsDeputation", IsDeputation);
            inputMap.put("curretDate", dateFormat.format(date).toString());
            inputMap.put("EmpSevarthId", strEmpSevarthId);
            inputMap.put("EmpName", strEmpName);
            inputMap.put("DDOCode", strDDOCode);
            inputMap.put("empList", lstEmpForFrmS1Edit);
            inputMap.put("empDesigList", empDesigList);
            resObj.setResultValue((Object)inputMap);
            resObj.setViewName("empListForFormS1Update");
        }
        catch (Exception e) {
            resObj.setResultValue((Object)null);
            resObj.setThrowable((Throwable)e);
            resObj.setResultCode(-1);
            resObj.setViewName("errorPage");
        }
        return resObj;
    }

    private String parseDate(String strDate) {
        if (strDate != "") {
            String[] tmp = strDate.split("/");
            return String.valueOf(tmp[2]) + "-" + tmp[1] + "-" + tmp[0];
        }
        return null;
    }

    public ResultObject validateFormS1ForEdit(Map objectArgs) {
        this.gLogger.info((Object)"inside validateUIDUniqeness");
        ResultObject objRes = new ResultObject(0, (Object)"FAIL");
        ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
        HttpServletRequest request = (HttpServletRequest)objectArgs.get("requestObj");
        FormS1UpdateDAOImpl lObjFormS1UpdateDAO = new FormS1UpdateDAOImpl((Class)FrmFormS1Dtls.class, serv.getSessionFactory());
        String strSevarthId = null;
        Long finalCheckFlag = null;
        String lStrResult = null;
        try {
            strSevarthId = StringUtility.getParameter((String)"empSevarthId", (HttpServletRequest)request).trim();
            this.gLogger.info((Object)("--------empSevarthId--------:" + strSevarthId));
            finalCheckFlag = lObjFormS1UpdateDAO.checkFormS1(strSevarthId);
            this.gLogger.info((Object)("--------finalCheckFlag--------:" + finalCheckFlag));
            String status = null;
            status = finalCheckFlag > 0 ? "wrong" : "correct";
            String lSBStatus = this.getResponseXMLDoc(status).toString();
            lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
            objectArgs.put("ajaxKey", lStrResult);
            objRes.setResultValue((Object)objectArgs);
            objRes.setViewName("ajaxData");
        }
        catch (Exception e) {
            objRes.setResultValue((Object)null);
            objRes.setThrowable((Throwable)e);
            objRes.setResultCode(-1);
            objRes.setViewName("errorPage");
        }
        return objRes;
    }

    private StringBuilder getResponseXMLDoc(String status) {
        StringBuilder lStrBldXML = new StringBuilder();
        lStrBldXML.append("<XMLDOC>");
        lStrBldXML.append("<Flag>");
        lStrBldXML.append(status);
        lStrBldXML.append("</Flag>");
        lStrBldXML.append("</XMLDOC>");
        return lStrBldXML;
    }
    
	public ResultObject chkFrmUpdatedByLgnDdoEdit(Map inputMap) throws Exception
	{
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List lstEmpForFrmS1Edit=null;
		List empDesigList=null;
		String strDDOCode=null;
		String txtSearch=null;
		String flag=null;
		String sevarthId=null;
		String IsDeputation = null;
		int DepSize = 100;
		String CheckFormUpdatedByLgnDDO=null;
		String lSBStatus=null;

		try
		{
			setSessionInfo(inputMap);
			DcpsCommonDAO lObjDcpsCommonDao = new DcpsCommonDAOImpl(null,serv.getSessionFactory());
			FormS1UpdateDAO lObjSearchEmployeeDAO = new FormS1UpdateDAOImpl(
					MstEmp.class, serv.getSessionFactory());
			strDDOCode=lObjDcpsCommonDao.getDdoCodeForDDO(gLngPostId);
			gLogger.info("logged in ddo code: "+strDDOCode);
			txtSearch=StringUtility.getParameter("searchTxt", request);

			CheckFormUpdatedByLgnDDO = lObjSearchEmployeeDAO.chkFrmUpdatedByLgnDdo(txtSearch);
			gLogger.info("CheckFormUpdatedByLgnDDO flag is **********"+CheckFormUpdatedByLgnDDO);
			if(CheckFormUpdatedByLgnDDO.equals("S")){
				lSBStatus = getResponseUpdatedDdoCode("S").toString();
			}
			else if(CheckFormUpdatedByLgnDDO.equals("Z")){
				lSBStatus = getResponseUpdatedDdoCode("Z").toString();
			}
			else if(!CheckFormUpdatedByLgnDDO.equals(strDDOCode))
			{
				lSBStatus = getResponseUpdatedDdoCode(CheckFormUpdatedByLgnDDO).toString();
			}
			else if(CheckFormUpdatedByLgnDDO.equals(strDDOCode))
			{
				lSBStatus = getResponseUpdatedDdoCode("M").toString();
			}
		

			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",lSBStatus).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");

			resObj.setResultValue(inputMap);
		}
		catch (Exception e)
		{
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;

	}

	private StringBuilder getResponseUpdatedDdoCode(String flag) {
		gLogger.info("Flag is  in AJAX********"+flag);
		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");

		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

    
}