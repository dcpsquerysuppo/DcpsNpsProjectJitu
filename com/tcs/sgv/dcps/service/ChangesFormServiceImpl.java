/* Decompiler 1633ms, total 3122ms, lines 3006 */
package com.tcs.sgv.dcps.service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.helper.WorkFlowHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttachmentMstDAOImpl;
import com.tcs.sgv.common.valueobject.CmnAttachmentMpg;
import com.tcs.sgv.common.valueobject.CmnAttachmentMst;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.ChangesFormDAO;
import com.tcs.sgv.dcps.dao.ChangesFormDAOImpl;
import com.tcs.sgv.dcps.dao.DdoProfileDAO;
import com.tcs.sgv.dcps.dao.DdoProfileDAOImpl;
import com.tcs.sgv.dcps.dao.LedgerReportDAOImpl;
import com.tcs.sgv.dcps.dao.NewRegDdoDAO;
import com.tcs.sgv.dcps.dao.NewRegDdoDAOImpl;
import com.tcs.sgv.dcps.valueobject.DcpsCadreMst;
import com.tcs.sgv.dcps.valueobject.DdoOffice;
import com.tcs.sgv.dcps.valueobject.HstDcpsChanges;
import com.tcs.sgv.dcps.valueobject.HstDcpsNomineeChanges;
import com.tcs.sgv.dcps.valueobject.HstDcpsOfficeChanges;
import com.tcs.sgv.dcps.valueobject.HstDcpsOtherChanges;
import com.tcs.sgv.dcps.valueobject.HstDcpsPersonalChanges;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.MstEmpNmn;
import com.tcs.sgv.dcps.valueobject.RltDcpsPayrollEmp;
import com.tcs.sgv.dcps.valueobject.TrnDcpsChanges;
import com.tcs.sgv.eis.dao.OrderMstDAO;
import com.tcs.sgv.eis.dao.OrderMstDAOImpl;
import com.tcs.sgv.eis.valueobject.HrPayOrderMst;
import com.tcs.sgv.wf.delegate.WorkFlowDelegate;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ChangesFormServiceImpl extends ServiceImpl implements ChangesFormService {
   private final Log gLogger = LogFactory.getLog(this.getClass());
   private String gStrPostId = null;
   private String gStrUserId = null;
   private String gStrLocale = null;
   private Locale gLclLocale = null;
   private Long gLngLangId = null;
   private Long gLngDBId = null;
   private Date gDtCurDate = null;
   private HttpServletRequest request = null;
   private ServiceLocator serv = null;
   private HttpSession session = null;
   Long gLngPostId = null;
   Long gLngUserId = null;
   Date gDtCurrDt = null;
   String gStrLocationCode = null;
   static HashMap sMapUserLoc = new HashMap();
   String gStrUserLocation = null;
   private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/dcps/DCPSConstants");

   private void setSessionInfo(Map inputMap) {
      try {
         this.request = (HttpServletRequest)inputMap.get("requestObj");
         this.session = this.request.getSession();
         this.serv = (ServiceLocator)inputMap.get("serviceLocator");
         this.gLclLocale = new Locale(SessionHelper.getLocale(this.request));
         this.gStrLocale = SessionHelper.getLocale(this.request);
         this.gLngLangId = SessionHelper.getLangId(inputMap);
         this.gLngPostId = SessionHelper.getPostId(inputMap);
         this.gStrPostId = this.gLngPostId.toString();
         this.gLngUserId = SessionHelper.getUserId(inputMap);
         this.gStrUserId = this.gLngUserId.toString();
         this.gStrLocationCode = SessionHelper.getLocationCode(inputMap);
         this.gLngDBId = SessionHelper.getDbId(inputMap);
         this.gDtCurDate = SessionHelper.getCurDate();
         this.gDtCurrDt = SessionHelper.getCurDate();
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   public ResultObject loadChangesForm(Map inputMap) {
      ResultObject resObj = new ResultObject(0, "FAIL");
      List Srno = null;
      String srno = "";

      try {
         this.setSessionInfo(inputMap);
         ChangesFormDAO lObjChangesDAO = new ChangesFormDAOImpl((Class)null, this.serv.getSessionFactory());
         DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl((Class)null, this.serv.getSessionFactory());
         String lStrUserType = StringUtility.getParameter("User", this.request);
         this.gLogger.info("lStrUserType is " + lStrUserType);
         this.gLogger.info("Executing the  loadChangesForm " + lStrUserType);
         inputMap.put("UserType", lStrUserType);
         List lLstChanges = IFMSCommonServiceImpl.getLookupValues("DCPS_Changes", SessionHelper.getLangId(inputMap), inputMap);
         inputMap.put("CHANGESLIST", lLstChanges);
         NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class, this.serv.getSessionFactory());
         OrderMstDAO orderMasterDAO = new OrderMstDAOImpl(HrPayOrderMst.class, this.serv.getSessionFactory());
         String flag = StringUtility.getParameter("flag", this.request);
         String lStrDdoCode;
         String sevarthId;
         String employeeName;
         String SFTPWORKINGDIR;
         if (flag.equals("true")) {
            lStrDdoCode = StringUtility.getParameter("photoattachmentId", this.request);
            String signAttachId = StringUtility.getParameter("signattachmentId", this.request);
            this.gLogger.info("photoAttachId is " + lStrDdoCode);
            this.gLogger.info("signAttachId is " + signAttachId);
            File f;
            Blob blob;
            String SFTPHOST;
            short SFTPPORT;
            Session session;
            Channel channel;
            ChannelSftp channelSftp;
            int blobLength;
            byte[] blobAsBytes;
            JSch jsch;
            Properties config;
            String[] folders;
            String folder;
            int var29;
            int var30;
            String[] var31;
            int i;
            ByteArrayOutputStream baos;
            byte[] lBytes;
            FileOutputStream out;
            if (lStrDdoCode != null && lStrDdoCode != "") {
               Srno = lObjNewRegDdoDAO.getmaxSRno(lStrDdoCode);
               srno = Srno.get(0).toString();
               this.gLogger.info("srno is " + srno);
               blob = (orderMasterDAO).getAttachment(srno);
               if (blob != null) {
                  SFTPHOST = "10.34.82.225";
                  this.gLogger.info("Blob Length is " + blob.length());
                  this.gLogger.info("Blob is " + blob);
                  SFTPPORT = 8888;
                  sevarthId = "tcsadmin";
                  employeeName = "Tcsadmin@123";
                  SFTPWORKINGDIR = "/home/EmployeeConfigurationForm";
                  session = null;
                  channel = null;
                  channelSftp = null;
                  this.gLogger.error("preparing the host information for sftp.");

                  try {
                     blobLength = (int)blob.length();
                     blobAsBytes = blob.getBytes(1L, blobLength);
                     jsch = new JSch();
                     this.gLogger.error("before session:");
                     session = jsch.getSession(sevarthId, SFTPHOST, SFTPPORT);
                     this.gLogger.error("After session:" + session);
                     session.setPassword(employeeName);
                     config = new Properties();
                     config.put("StrictHostKeyChecking", "no");
                     session.setConfig(config);
                     this.gLogger.error("before Host connected");
                     session.connect();
                     this.gLogger.error("after Host connected.");
                     channel = session.openChannel("sftp");
                     channel.connect();
                     this.gLogger.error("sftp channel opened and connected.");
                     channelSftp = (ChannelSftp)channel;
                     this.gLogger.error("FILE NAME4444444444 ::::::::::::");
                     folders = SFTPWORKINGDIR.split("/");
                     var31 = folders;
                     var30 = folders.length;

                     for(var29 = 0; var29 < var30; ++var29) {
                        folder = var31[var29];
                        if (folder.length() > 0) {
                           try {
                              this.gLogger.error("FILE folder:" + folder);
                              channelSftp.cd(folder);
                           } catch (SftpException var49) {
                              this.gLogger.error("FILE folder:" + folder);
                              channelSftp.mkdir(folder);
                              channelSftp.cd(folder);
                           }
                        }
                     }

                     channelSftp.cd(SFTPWORKINGDIR);
                     this.gLogger.error("FILE NAME55555555 ::::::::::::");
                     baos = new ByteArrayOutputStream();
                     lBytes = blobAsBytes;
                     baos.toByteArray();
                     this.gLogger.error("lBytes length:" + blobAsBytes.length);
                     this.gLogger.error("lBytes length:" + blobAsBytes.length);
                     out = new FileOutputStream(srno);

                     for(i = 0; i < 2; ++i) {
                        out.write(lBytes);
                     }

                     out.close();
                     f = new File(srno.toString());
                     this.gLogger.error("FILE NAME222222222 ::::::::::::" + srno);
                     this.gLogger.error("FILE NAME ::::::::::::" + f.getName());
                     channelSftp.put(new FileInputStream(f), f.getName());
                     this.gLogger.error("FILE NAME333333333 ::::::::::::" + srno);
                  } catch (Exception var52) {
                     var52.printStackTrace();
                     this.gLogger.error("Exception found while tranfer the response.");
                     throw new Exception();
                  } finally {
                     channelSftp.exit();
                     this.gLogger.error("sftp Channel exited.");
                     channel.disconnect();
                     this.gLogger.error("Channel disconnected.");
                     session.disconnect();
                     this.gLogger.error("Host Session disconnected.");
                  }
               }

               orderMasterDAO.updateBlob(Long.parseLong(srno));
            }

            if (signAttachId != null && signAttachId != "") {
               Srno = lObjNewRegDdoDAO.getmaxSRno(signAttachId);
               srno = Srno.get(0).toString();
               this.gLogger.info("srno is " + srno);
               blob = orderMasterDAO.getAttachment(srno);
               if (blob != null) {
                  SFTPHOST = "10.34.82.225";
                  this.gLogger.info("Blob Length is " + blob.length());
                  this.gLogger.info("Blob is " + blob);
                  SFTPPORT = 8888;
                  sevarthId = "tcsadmin";
                  employeeName = "Tcsadmin@123";
                  SFTPWORKINGDIR = "/home/EmployeeConfigurationForm";
                  session = null;
                  channel = null;
                  channelSftp = null;
                  this.gLogger.error("preparing the host information for sftp.");

                  try {
                     blobLength = (int)blob.length();
                     blobAsBytes = blob.getBytes(1L, blobLength);
                     jsch = new JSch();
                     this.gLogger.error("before session:");
                     session = jsch.getSession(sevarthId, SFTPHOST, SFTPPORT);
                     this.gLogger.error("After session:" + session);
                     session.setPassword(employeeName);
                     config = new Properties();
                     config.put("StrictHostKeyChecking", "no");
                     session.setConfig(config);
                     this.gLogger.error("before Host connected");
                     session.connect();
                     this.gLogger.error("after Host connected.");
                     channel = session.openChannel("sftp");
                     channel.connect();
                     this.gLogger.error("sftp channel opened and connected.");
                     channelSftp = (ChannelSftp)channel;
                     this.gLogger.error("FILE NAME4444444444 ::::::::::::");
                     folders = SFTPWORKINGDIR.split("/");
                     var31 = folders;
                     var30 = folders.length;

                     for(var29 = 0; var29 < var30; ++var29) {
                        folder = var31[var29];
                        if (folder.length() > 0) {
                           try {
                              this.gLogger.error("FILE folder:" + folder);
                              channelSftp.cd(folder);
                           } catch (SftpException var48) {
                              this.gLogger.error("FILE folder:" + folder);
                              channelSftp.mkdir(folder);
                              channelSftp.cd(folder);
                           }
                        }
                     }

                     channelSftp.cd(SFTPWORKINGDIR);
                     this.gLogger.error("FILE NAME55555555 ::::::::::::");
                     baos = new ByteArrayOutputStream();
                     lBytes = blobAsBytes;
                     baos.toByteArray();
                     this.gLogger.error("lBytes length:" + blobAsBytes.length);
                     this.gLogger.error("lBytes length:" + blobAsBytes.length);
                     out = new FileOutputStream(srno);

                     for(i = 0; i < 2; ++i) {
                        out.write(lBytes);
                     }

                     out.close();
                     f = new File(srno.toString());
                     this.gLogger.error("FILE NAME222222222 ::::::::::::" + srno);
                     this.gLogger.error("FILE NAME ::::::::::::" + f.getName());
                     channelSftp.put(new FileInputStream(f), f.getName());
                     this.gLogger.error("FILE NAME333333333 ::::::::::::" + srno);
                  } catch (Exception var50) {
                     var50.printStackTrace();
                     this.gLogger.error("Exception found while tranfer the response.");
                     throw new Exception();
                  } finally {
                     channelSftp.exit();
                     this.gLogger.error("sftp Channel exited.");
                     channel.disconnect();
                     this.gLogger.error("Channel disconnected.");
                     session.disconnect();
                     this.gLogger.error("Host Session disconnected.");
                  }
               }

               orderMasterDAO.updateBlob(Long.parseLong(srno));
            }
         }

         lStrDdoCode = null;
         if (lStrUserType.equals("DDOAsst")) {
            lStrDdoCode = lObjDcpsCommonDAO.getDdoCode(this.gLngPostId);
         } else if (lStrUserType.equals("DDO")) {
            lStrDdoCode = lObjDcpsCommonDAO.getDdoCodeForDDO(this.gLngPostId);
         }

         List lLstParentDept = lObjDcpsCommonDAO.getParentDeptForDDO(lStrDdoCode);
         Object[] objParentDept = (Object[])lLstParentDept.get(0);
         List lLstDesignation = lObjDcpsCommonDAO.getAllDesignation((Long)objParentDept[0], this.gLngLangId);
         inputMap.put("lLstDesignation", lLstDesignation);
         String lStrDesignationId = StringUtility.getParameter("DesignationId", this.request);
         this.gLogger.info("lStrDesignationId is " + lStrDesignationId);
         sevarthId = StringUtility.getParameter("sevarthId", this.request);
         this.gLogger.info("sevarth Id is " + sevarthId);
         employeeName = StringUtility.getParameter("employeeName", this.request);
         this.gLogger.info("employeeName is " + employeeName);
         if (!lStrDesignationId.equals("")) {
            List empList = lObjChangesDAO.getAllDcpsEmployees(lStrDesignationId, lStrDdoCode, sevarthId, employeeName);
            inputMap.put("empList", empList);
            inputMap.put("DesignationId", lStrDesignationId);
         }

         SFTPWORKINGDIR = StringUtility.getParameter("Changes", this.request);
         this.gLogger.info("lStrChanges is " + SFTPWORKINGDIR);
         if (!SFTPWORKINGDIR.equals("")) {
            inputMap.put("Changes", SFTPWORKINGDIR);
         }

         if (!sevarthId.equals("")) {
            inputMap.put("sevarthId", sevarthId);
         }

         if (!employeeName.equals("")) {
            inputMap.put("employeeName", employeeName);
         }

         String lStrType = StringUtility.getParameter("Type", this.request);
         inputMap.put("Type", lStrType);
         resObj.setResultValue(inputMap);
         resObj.setViewName("DCPSChanges");
      } catch (Exception var54) {
         resObj.setResultValue((Object)null);
         resObj.setThrowable(var54);
         resObj.setResultCode(-1);
         resObj.setViewName("errorPage");
      }

      return resObj;
   }

   public ResultObject loadChangesDrafts(Map inputMap) {
      ResultObject resObj = new ResultObject(0, "FAIL");
      List lLstDesignation = null;
      List ChangesDraftsList = null;
      List Srno = null;
      String srno = "";

      try {
         this.setSessionInfo(inputMap);
         String hidElementId = StringUtility.getParameter("elementId", this.request);
         inputMap.put("hidElementId", hidElementId);
         String lStrUserType = StringUtility.getParameter("User", this.request);
         inputMap.put("UserType", lStrUserType);
         ChangesFormDAO lObjChangesDAO = new ChangesFormDAOImpl((Class)null, this.serv.getSessionFactory());
         DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl((Class)null, this.serv.getSessionFactory());
         this.gLogger.info("Executing the  loadChangesDraft" + lStrUserType);
         String lStrDdoCode = null;
         NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class, this.serv.getSessionFactory());
         OrderMstDAO orderMasterDAO = new OrderMstDAOImpl(HrPayOrderMst.class, this.serv.getSessionFactory());
         String flag = StringUtility.getParameter("flag", this.request);
         if (flag.equals("true")) {
            String photoAttachId = StringUtility.getParameter("photoattachmentId", this.request);
            String signAttachId = StringUtility.getParameter("signattachmentId", this.request);
            this.gLogger.info("photoAttachId is " + photoAttachId);
            this.gLogger.info("signAttachId is " + signAttachId);
            File f;
            Blob blob;
            String SFTPHOST;
            short SFTPPORT;
            String SFTPUSER;
            String SFTPPASS;
            String SFTPWORKINGDIR;
            Session session;
            Channel channel;
            ChannelSftp channelSftp;
            int blobLength;
            byte[] blobAsBytes;
            JSch jsch;
            Properties config;
            String[] folders;
            String folder;
            int var32;
            int var33;
            String[] var34;
            int i;
            ByteArrayOutputStream baos;
            byte[] lBytes;
            FileOutputStream out;
            if (photoAttachId != null && photoAttachId != "") {
               Srno = lObjNewRegDdoDAO.getmaxSRno(photoAttachId);
               srno = Srno.get(0).toString();
               this.gLogger.info("srno is " + srno);
               blob = orderMasterDAO.getAttachment(srno);
               if (blob != null) {
                  SFTPHOST = "10.34.82.225";
                  this.gLogger.info("Blob Length is " + blob.length());
                  this.gLogger.info("Blob is " + blob);
                  SFTPPORT = 8888;
                  SFTPUSER = "tcsadmin";
                  SFTPPASS = "Tcsadmin@123";
                  SFTPWORKINGDIR = "/home/EmployeeConfigurationForm";
                  session = null;
                  channel = null;
                  channelSftp = null;
                  this.gLogger.error("preparing the host information for sftp.");

                  try {
                     blobLength = (int)blob.length();
                     blobAsBytes = blob.getBytes(1L, blobLength);
                     jsch = new JSch();
                     this.gLogger.error("before session:");
                     session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
                     this.gLogger.error("After session:" + session);
                     session.setPassword(SFTPPASS);
                     config = new Properties();
                     config.put("StrictHostKeyChecking", "no");
                     session.setConfig(config);
                     this.gLogger.error("before Host connected");
                     session.connect();
                     this.gLogger.error("after Host connected.");
                     channel = session.openChannel("sftp");
                     channel.connect();
                     this.gLogger.error("sftp channel opened and connected.");
                     channelSftp = (ChannelSftp)channel;
                     this.gLogger.error("FILE NAME4444444444 ::::::::::::");
                     folders = SFTPWORKINGDIR.split("/");
                     var34 = folders;
                     var33 = folders.length;

                     for(var32 = 0; var32 < var33; ++var32) {
                        folder = var34[var32];
                        if (folder.length() > 0) {
                           try {
                              this.gLogger.error("FILE folder:" + folder);
                              channelSftp.cd(folder);
                           } catch (SftpException var52) {
                              this.gLogger.error("FILE folder:" + folder);
                              channelSftp.mkdir(folder);
                              channelSftp.cd(folder);
                           }
                        }
                     }

                     channelSftp.cd(SFTPWORKINGDIR);
                     this.gLogger.error("FILE NAME55555555 ::::::::::::");
                     baos = new ByteArrayOutputStream();
                     lBytes = blobAsBytes;
                     baos.toByteArray();
                     this.gLogger.error("lBytes length:" + blobAsBytes.length);
                     this.gLogger.error("lBytes length:" + blobAsBytes.length);
                     out = new FileOutputStream(srno);

                     for(i = 0; i < 2; ++i) {
                        out.write(lBytes);
                     }

                     out.close();
                     f = new File(srno.toString());
                     this.gLogger.error("FILE NAME222222222 ::::::::::::" + srno);
                     this.gLogger.error("FILE NAME ::::::::::::" + f.getName());
                     channelSftp.put(new FileInputStream(f), f.getName());
                     this.gLogger.error("FILE NAME333333333 ::::::::::::" + srno);
                  } catch (Exception var55) {
                     var55.printStackTrace();
                     this.gLogger.error("Exception found while tranfer the response.");
                     throw new Exception();
                  } finally {
                     channelSftp.exit();
                     this.gLogger.error("sftp Channel exited.");
                     channel.disconnect();
                     this.gLogger.error("Channel disconnected.");
                     session.disconnect();
                     this.gLogger.error("Host Session disconnected.");
                  }
               }

               orderMasterDAO.updateBlob(Long.parseLong(srno));
            }

            if (signAttachId != null && signAttachId != "") {
               Srno = lObjNewRegDdoDAO.getmaxSRno(signAttachId);
               srno = Srno.get(0).toString();
               this.gLogger.info("srno is " + srno);
               blob = orderMasterDAO.getAttachment(srno);
               if (blob != null) {
                  SFTPHOST = "10.34.82.225";
                  this.gLogger.info("Blob Length is " + blob.length());
                  this.gLogger.info("Blob is " + blob);
                  SFTPPORT = 8888;
                  SFTPUSER = "tcsadmin";
                  SFTPPASS = "Tcsadmin@123";
                  SFTPWORKINGDIR = "/home/EmployeeConfigurationForm";
                  session = null;
                  channel = null;
                  channelSftp = null;
                  this.gLogger.error("preparing the host information for sftp.");

                  try {
                     blobLength = (int)blob.length();
                     blobAsBytes = blob.getBytes(1L, blobLength);
                     jsch = new JSch();
                     this.gLogger.error("before session:");
                     session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
                     this.gLogger.error("After session:" + session);
                     session.setPassword(SFTPPASS);
                     config = new Properties();
                     config.put("StrictHostKeyChecking", "no");
                     session.setConfig(config);
                     this.gLogger.error("before Host connected");
                     session.connect();
                     this.gLogger.error("after Host connected.");
                     channel = session.openChannel("sftp");
                     channel.connect();
                     this.gLogger.error("sftp channel opened and connected.");
                     channelSftp = (ChannelSftp)channel;
                     this.gLogger.error("FILE NAME4444444444 ::::::::::::");
                     folders = SFTPWORKINGDIR.split("/");
                     var34 = folders;
                     var33 = folders.length;

                     for(var32 = 0; var32 < var33; ++var32) {
                        folder = var34[var32];
                        if (folder.length() > 0) {
                           try {
                              this.gLogger.error("FILE folder:" + folder);
                              channelSftp.cd(folder);
                           } catch (SftpException var51) {
                              this.gLogger.error("FILE folder:" + folder);
                              channelSftp.mkdir(folder);
                              channelSftp.cd(folder);
                           }
                        }
                     }

                     channelSftp.cd(SFTPWORKINGDIR);
                     this.gLogger.error("FILE NAME55555555 ::::::::::::");
                     baos = new ByteArrayOutputStream();
                     lBytes = blobAsBytes;
                     baos.toByteArray();
                     this.gLogger.error("lBytes length:" + blobAsBytes.length);
                     this.gLogger.error("lBytes length:" + blobAsBytes.length);
                     out = new FileOutputStream(srno);

                     for(i = 0; i < 2; ++i) {
                        out.write(lBytes);
                     }

                     out.close();
                     f = new File(srno.toString());
                     this.gLogger.error("FILE NAME222222222 ::::::::::::" + srno);
                     this.gLogger.error("FILE NAME ::::::::::::" + f.getName());
                     channelSftp.put(new FileInputStream(f), f.getName());
                     this.gLogger.error("FILE NAME333333333 ::::::::::::" + srno);
                  } catch (Exception var53) {
                     var53.printStackTrace();
                     this.gLogger.error("Exception found while tranfer the response.");
                     throw new Exception();
                  } finally {
                     channelSftp.exit();
                     this.gLogger.error("sftp Channel exited.");
                     channel.disconnect();
                     this.gLogger.error("Channel disconnected.");
                     session.disconnect();
                     this.gLogger.error("Host Session disconnected.");
                  }
               }

               orderMasterDAO.updateBlob(Long.parseLong(srno));
            }
         }

         if (lStrUserType.equals("DDOAsst")) {
            lStrDdoCode = lObjDcpsCommonDAO.getDdoCode(this.gLngPostId);
         } else if (lStrUserType.equals("DDO")) {
            lStrDdoCode = lObjDcpsCommonDAO.getDdoCodeForDDO(this.gLngPostId);
         }

         List lLstParentDept = lObjDcpsCommonDAO.getParentDeptForDDO(lStrDdoCode);
         Object[] objParentDept = (Object[])lLstParentDept.get(0);
         lLstDesignation = lObjDcpsCommonDAO.getAllDesignation((Long)objParentDept[0], this.gLngLangId);
         inputMap.put("lLstDesignation", lLstDesignation);
         if (!StringUtility.getParameter("DesignationId", this.request).equalsIgnoreCase("") && StringUtility.getParameter("DesignationId", this.request) != null) {
            String lStrDesignationId = StringUtility.getParameter("DesignationId", this.request);
            if (!lStrDesignationId.equals("")) {
               ChangesDraftsList = lObjChangesDAO.getChangesDraftsForDesig(lStrDesignationId, lStrUserType, lStrDdoCode);
               inputMap.put("ChangesDraftsList", ChangesDraftsList);
               inputMap.put("DesignationId", lStrDesignationId);
            }
         }

         resObj.setResultValue(inputMap);
         resObj.setViewName("DCPSChangesDrafts");
      } catch (Exception var57) {
         resObj.setResultValue((Object)null);
         resObj.setThrowable(var57);
         resObj.setResultCode(-1);
         resObj.setViewName("errorPage");
      }

      return resObj;
   }

   public ResultObject changesPersonalDetails(Map inputMap) {
      ResultObject resObj = new ResultObject(0, "FAIL");
      Long dcpsChangesId = null;
      HstDcpsChanges lObjHstDcpsChanges = null;
      HstDcpsPersonalChanges lObjHstDcpsPersonalChanges = null;
      Long HstDcpsPersonalChangesId = null;
      String lStrDesignationDraft = null;
      MstEmp MstEmpObj = null;
      String lStrDdoCode = null;
      RltDcpsPayrollEmp lObjRltDcpsPayrollEmp = null;

      try {
         this.setSessionInfo(inputMap);
         ChangesFormDAO lObjChangesDAO = new ChangesFormDAOImpl(HstDcpsPersonalChanges.class, this.serv.getSessionFactory());
         DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl((Class)null, this.serv.getSessionFactory());
         SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
         Long lLngEmpID = Long.parseLong(StringUtility.getParameter("EmpId", this.request));
         if (!StringUtility.getParameter("dcpsChangesId", this.request).equalsIgnoreCase("") && StringUtility.getParameter("dcpsChangesId", this.request) != null) {
            dcpsChangesId = Long.valueOf(StringUtility.getParameter("dcpsChangesId", this.request));
            lObjHstDcpsChanges = lObjChangesDAO.getChangesDetails(dcpsChangesId);
            HstDcpsPersonalChangesId = lObjChangesDAO.getPersonalChangesIdforChangesId(dcpsChangesId);
            lObjHstDcpsPersonalChanges = (HstDcpsPersonalChanges)lObjChangesDAO.read(HstDcpsPersonalChangesId);
            lStrDesignationDraft = StringUtility.getParameter("designationDraft", this.request);
         }

         MstEmpObj = lObjChangesDAO.getEmpDetails(lLngEmpID);
         lObjRltDcpsPayrollEmp = lObjChangesDAO.getEmpPayrollDetailsForEmpId(lLngEmpID);
         inputMap.put("lStrDesignationDraft", lStrDesignationDraft);
         inputMap.put("dcpsChangesId", dcpsChangesId);
         inputMap.put("lObjHstDcpsChanges", lObjHstDcpsChanges);
         inputMap.put("lObjHstDcpsPersonalChanges", lObjHstDcpsPersonalChanges);
         inputMap.put("lObjEmpData", MstEmpObj);
         inputMap.put("lObjRltDcpsPayrollEmp", lObjRltDcpsPayrollEmp);
         List lLstState = lObjDcpsCommonDAO.getStateNames(1L);
         inputMap.put("STATENAMES", lLstState);
         List listSalutation = IFMSCommonServiceImpl.getLookupValues("Salutation", SessionHelper.getLangId(inputMap), inputMap);
         inputMap.put("listSalutation", listSalutation);
         inputMap.put("lDtJoiDtLimit", "01/01/2005");
         Date lDtcurDate = SessionHelper.getCurDate();
         inputMap.put("lDtCurDate", lObjDateFormat.format(lDtcurDate));
         String lStrDesignation = StringUtility.getParameter("designationId", this.request);
         inputMap.put("lStrDesignation", lStrDesignation);
         String lStrChangesType = StringUtility.getParameter("changesType", this.request);
         inputMap.put("lStrChangesType", lStrChangesType);
         String lStrUserType = StringUtility.getParameter("UserType", this.request);
         inputMap.put("UserType", lStrUserType);
         List lLstParentDept;
         if (lStrUserType.equals("DDOAsst")) {
            inputMap.put("EditForm", "Y");
            lStrDdoCode = lObjDcpsCommonDAO.getDdoCode(this.gLngPostId);
            lLstParentDept = this.getHierarchyUsers(inputMap, lStrUserType);
            inputMap.put("UserList", lLstParentDept);
            inputMap.put("ForwardToPost", lLstParentDept.get(lLstParentDept.size() - 1));
         } else if (lStrUserType.equals("DDO")) {
            inputMap.put("EditForm", "N");
            lStrDdoCode = lObjDcpsCommonDAO.getDdoCodeForDDO(this.gLngPostId);
         }

         inputMap.put("DDOCODE", lStrDdoCode);
         lLstParentDept = lObjDcpsCommonDAO.getParentDeptForDDO(lStrDdoCode);
         Object[] objParentDept = (Object[])lLstParentDept.get(0);
         List lLstDesignation = lObjDcpsCommonDAO.getAllDesignation((Long)objParentDept[0], this.gLngLangId);
         inputMap.put("lLstDesignation", lLstDesignation);
         String sevarthId = StringUtility.getParameter("sevarthId", this.request);
         inputMap.put("sevarthId", sevarthId);
         this.gLogger.info("Inside the sevarth_id of change personal details:" + sevarthId);
         String employeeName = StringUtility.getParameter("employeeName", this.request);
         inputMap.put("employeeName", employeeName);
         this.gLogger.info("Inside the sevarth_id of change personal details:" + employeeName);
         resObj.setResultValue(inputMap);
         resObj.setViewName("ChangePersonalDetails");
      } catch (Exception var26) {
         resObj.setResultValue((Object)null);
         resObj.setThrowable(var26);
         resObj.setResultCode(-1);
         resObj.setViewName("errorPage");
      }

      return resObj;
   }

   public ResultObject changesOfficeDetails(Map inputMap) {
      ResultObject resObj = new ResultObject(0, "FAIL");
      Long dcpsChangesId = null;
      HstDcpsChanges lObjHstDcpsChanges = null;
      HstDcpsOfficeChanges lObjHstDcpsOfficeChanges = null;
      Long HstDcpsOfficeChangesId = null;
      String lStrDesignationDraft = null;
      MstEmp MstEmpObj = null;
      RltDcpsPayrollEmp lObjRltDcpsPayrollEmp = null;
      DdoOffice lObjDdoOfficeVO = null;
      String lStrDdoCode = null;
      Long lLongDdoOfficeId = null;

      try {
         this.setSessionInfo(inputMap);
         ChangesFormDAO lObjChangesDAO = new ChangesFormDAOImpl(HstDcpsOfficeChanges.class, this.serv.getSessionFactory());
         NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class, this.serv.getSessionFactory());
         DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl((Class)null, this.serv.getSessionFactory());
         NewRegDdoDAO lObjNewRegDdoDAOForCadre = new NewRegDdoDAOImpl(DcpsCadreMst.class, this.serv.getSessionFactory());
         DdoProfileDAO lObjDdoProfileDAO = new DdoProfileDAOImpl((Class)null, this.serv.getSessionFactory());
         String hidDcpsId = StringUtility.getParameter("hidDcpsId", this.request);
         String hidEmpName = StringUtility.getParameter("hidEmpName", this.request);
         String hidBirthDate = StringUtility.getParameter("hidBirthDate", this.request);
         inputMap.put("hidDcpsId", hidDcpsId);
         inputMap.put("hidEmpName", hidEmpName);
         inputMap.put("hidBirthDate", hidBirthDate);
         String hidSevarthId = StringUtility.getParameter("hidSevarthId", this.request).trim();
         inputMap.put("hidSevarthId", hidSevarthId);
         String hidName = StringUtility.getParameter("hidName", this.request).trim();
         inputMap.put("hidName", hidName);
         String FromSearchEmp = StringUtility.getParameter("FromSearchEmp", this.request).trim();
         inputMap.put("FromSearchEmp", FromSearchEmp);
         String FromChangesOfficeElement = StringUtility.getParameter("FromChangesOfficeElement", this.request).trim();
         inputMap.put("FromChangesOfficeElement", FromChangesOfficeElement);
         Long lLngEmpID = Long.parseLong(StringUtility.getParameter("EmpId", this.request));
         if (!StringUtility.getParameter("dcpsChangesId", this.request).equalsIgnoreCase("") && StringUtility.getParameter("dcpsChangesId", this.request) != null) {
            dcpsChangesId = Long.valueOf(StringUtility.getParameter("dcpsChangesId", this.request));
            lObjHstDcpsChanges = lObjChangesDAO.getChangesDetails(dcpsChangesId);
            HstDcpsOfficeChangesId = lObjChangesDAO.getOfficeChangesIdforChangesId(dcpsChangesId);
            lObjHstDcpsOfficeChanges = (HstDcpsOfficeChanges)lObjChangesDAO.read(HstDcpsOfficeChangesId);
            lStrDesignationDraft = StringUtility.getParameter("designationDraft", this.request);
         }

         MstEmpObj = lObjChangesDAO.getEmpDetails(lLngEmpID);
         lObjRltDcpsPayrollEmp = lObjChangesDAO.getEmpPayrollDetailsForEmpId(lLngEmpID);
         Date dobOfEmployee = lObjChangesDAO.getDobForTheEmployee(lLngEmpID);
         inputMap.put("lStrDesignationDraft", lStrDesignationDraft);
         inputMap.put("dcpsChangesId", dcpsChangesId);
         inputMap.put("lObjHstDcpsChanges", lObjHstDcpsChanges);
         inputMap.put("lObjHstDcpsOfficeChanges", lObjHstDcpsOfficeChanges);
         inputMap.put("lObjEmpData", MstEmpObj);
         inputMap.put("lObjRltDcpsPayrollEmp", lObjRltDcpsPayrollEmp);
         inputMap.put("empDOB", dobOfEmployee);
         inputMap.put("parentDeptId", MstEmpObj.getParentDept());
         inputMap.put("parentDeptDesc", lObjDcpsCommonDAO.getLocNameforLocId(Long.valueOf(MstEmpObj.getParentDept())));
         inputMap.put("cadreId", MstEmpObj.getCadre());
         inputMap.put("cadreDesc", lObjDcpsCommonDAO.getCadreNameforCadreId(Long.valueOf(MstEmpObj.getCadre())));
         inputMap.put("designationId", MstEmpObj.getDesignation());
         inputMap.put("designationDesc", lObjDcpsCommonDAO.getDesigNameFromId(Long.valueOf(MstEmpObj.getDesignation())));
         inputMap.put("payCommissionId", MstEmpObj.getPayCommission());
         inputMap.put("payCommissionDesc", lObjDcpsCommonDAO.getCmnLookupNameFromId(Long.valueOf(MstEmpObj.getPayCommission())));
         inputMap.put("ddoOfficeIdMst", MstEmpObj.getCurrOff());
         inputMap.put("ddoOfficeDescMst", lObjDcpsCommonDAO.getDddoOfficeNameNameforId(Long.valueOf(MstEmpObj.getCurrOff())));
         List listPayCommission = IFMSCommonServiceImpl.getLookupValues("PayCommissionDCPS", SessionHelper.getLangId(inputMap), inputMap);
         inputMap.put("listPayCommission", listPayCommission);
         SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
         DcpsCadreMst lObjMstCadre = null;
         DdoOffice lObjDdoOfficeVOMst = null;
         String PayScaleList;
         String lLstOfficesForPost;
         String listCadres;
         Long lLongBirthYear;
         List lListReasonsForSalaryChange;
         HashMap lLongPaycommission;
         if (MstEmpObj != null) {
            if (MstEmpObj.getCurrOff() != null) {
               lLongDdoOfficeId = Long.valueOf(MstEmpObj.getCurrOff());
               lObjDdoOfficeVOMst = lObjNewRegDdoDAO.getDdoOfficeVO(lLongDdoOfficeId);
            }

            if (MstEmpObj.getCadre() != null && !MstEmpObj.getCadre().equalsIgnoreCase("")) {
               PayScaleList = lObjSimpleDateFormat.format(MstEmpObj.getDob());
               lLstOfficesForPost = PayScaleList.substring(0, 6);
               lObjMstCadre = (DcpsCadreMst)lObjNewRegDdoDAOForCadre.read(Long.valueOf(MstEmpObj.getCadre()));
               listCadres = lObjDcpsCommonDAO.getCmnLookupNameFromId(Long.valueOf(lObjMstCadre.getGroupId().trim()));
               Long SuperAnnuationAgeMst = lObjMstCadre.getSuperAntunAge();
               Long lLongBirthYear = Long.valueOf(PayScaleList.substring(6));
               lLongBirthYear = lLongBirthYear + SuperAnnuationAgeMst;
               String lStrRetiringYear = lLstOfficesForPost + lLongBirthYear.toString();
               inputMap.put("GroupNameMst", listCadres.trim());
               inputMap.put("SuperAnnAgeMst", SuperAnnuationAgeMst);
               inputMap.put("SuperAnnDateMst", lStrRetiringYear);
               Long lLngFieldDept = Long.parseLong(MstEmpObj.getParentDept());
               Long.parseLong(MstEmpObj.getCadre());
               lListReasonsForSalaryChange = lObjDcpsCommonDAO.getDesigsForPFDAndCadre(lLngFieldDept);
               inputMap.put("lLstDesignationMst", lListReasonsForSalaryChange);
               if (MstEmpObj.getPayCommission() != null && !MstEmpObj.getPayCommission().equalsIgnoreCase("")) {
                  lLongPaycommission = new HashMap();
                  lLongPaycommission.put("commissionId", MstEmpObj.getPayCommission());
                  inputMap.put("voToServiceMap", lLongPaycommission);
                  resObj = this.serv.executeService("GetScalefromDesg", inputMap);
                  List PayScaleListMst = (List)inputMap.get("PayScaleList");
                  inputMap.put("PayScaleListMst", PayScaleListMst);
                  String payScaleId = null;
                  String payScaleDesc = null;
                  Iterator var45 = PayScaleListMst.iterator();

                  while(var45.hasNext()) {
                     Object lObj = var45.next();
                     ComboValuesVO lObjComboVo = (ComboValuesVO)lObj;
                     if (lObjComboVo.getId().equals(MstEmpObj.getPayScale())) {
                        payScaleId = lObjComboVo.getId();
                        payScaleDesc = lObjComboVo.getDesc();
                     }
                  }

                  inputMap.put("payScaleId", payScaleId);
                  inputMap.put("payScaleDesc", payScaleDesc);
                  List lListReasonsForSalaryChangeMst = null;
                  Long lLongPaycommissionMst = Long.valueOf(MstEmpObj.getPayCommission());
                  if (lLongPaycommissionMst == 700015L) {
                     lListReasonsForSalaryChangeMst = IFMSCommonServiceImpl.getLookupValues("ReasonsForSalaryChangeIn5PC", SessionHelper.getLangId(inputMap), inputMap);
                  }

                  if (lLongPaycommissionMst == 700016L) {
                     lListReasonsForSalaryChangeMst = IFMSCommonServiceImpl.getLookupValues("ReasonsForSalaryChangeIn6PC", SessionHelper.getLangId(inputMap), inputMap);
                  }

                  inputMap.put("lListReasonsForSalaryChangeMst", lListReasonsForSalaryChangeMst);
               }

               List listCadresMst = lObjDcpsCommonDAO.getCadreForDept(Long.valueOf(MstEmpObj.getParentDept()));
               inputMap.put("CADRELISTMST", listCadresMst);
            }
         }

         List PayScaleList;
         List listCadres;
         String lStrRetiringYear;
         if (lObjHstDcpsOfficeChanges != null) {
            if (lObjHstDcpsOfficeChanges.getCurrOff() != null) {
               lLongDdoOfficeId = Long.valueOf(lObjHstDcpsOfficeChanges.getCurrOff());
               lObjDdoOfficeVO = lObjNewRegDdoDAO.getDdoOfficeVO(lLongDdoOfficeId);
               inputMap.put("ddoOfficeId", lObjHstDcpsOfficeChanges.getCurrOff());
               inputMap.put("ddoOfficeDesc", lObjDcpsCommonDAO.getDddoOfficeNameNameforId(Long.valueOf(lObjHstDcpsOfficeChanges.getCurrOff())));
            }

            if (lObjHstDcpsOfficeChanges.getCadre() != null) {
               MstEmp MstEmpForGettingDOB = (MstEmp)lObjNewRegDdoDAO.read(lObjHstDcpsOfficeChanges.getDcpsEmpId());
               lObjMstCadre = (DcpsCadreMst)lObjNewRegDdoDAOForCadre.read(Long.valueOf(lObjHstDcpsOfficeChanges.getCadre()));
               lLstOfficesForPost = lObjDcpsCommonDAO.getCmnLookupNameFromId(Long.valueOf(lObjMstCadre.getGroupId().trim()));
               Long SuperAnnuationAge = lObjMstCadre.getSuperAntunAge();
               String lStrDobEmp = lObjSimpleDateFormat.format(MstEmpForGettingDOB.getDob());
               String lStrWithoutYear = lStrDobEmp.substring(0, 6);
               lLongBirthYear = Long.valueOf(lStrDobEmp.substring(6));
               Long lLongRetiringYear = lLongBirthYear + SuperAnnuationAge;
               lStrRetiringYear = lStrWithoutYear + lLongRetiringYear.toString();
               inputMap.put("GroupName", lLstOfficesForPost.trim());
               inputMap.put("SuperAnnAge", SuperAnnuationAge);
               inputMap.put("SuperAnnDate", lStrRetiringYear);
               lListReasonsForSalaryChange = null;
               lLongPaycommission = null;
               Long lLongPaycommission;
               if (lObjHstDcpsOfficeChanges.getPayCommission() != null) {
                  lLongPaycommission = Long.valueOf(lObjHstDcpsOfficeChanges.getPayCommission());
               } else {
                  lLongPaycommission = Long.valueOf(MstEmpObj.getPayCommission());
               }

               if (lLongPaycommission == 700015L) {
                  lListReasonsForSalaryChange = IFMSCommonServiceImpl.getLookupValues("ReasonsForSalaryChangeIn5PC", SessionHelper.getLangId(inputMap), inputMap);
               }

               if (lLongPaycommission == 700016L) {
                  lListReasonsForSalaryChange = IFMSCommonServiceImpl.getLookupValues("ReasonsForSalaryChangeIn6PC", SessionHelper.getLangId(inputMap), inputMap);
               }

               inputMap.put("lListReasonsForSalaryChange", lListReasonsForSalaryChange);
            }

            PayScaleList = null;
            HashMap voToServiceMap;
            if (lObjHstDcpsOfficeChanges.getPayCommission() != null && !lObjHstDcpsOfficeChanges.getPayCommission().equalsIgnoreCase("")) {
               voToServiceMap = new HashMap();
               voToServiceMap.put("commissionId", lObjHstDcpsOfficeChanges.getPayCommission());
               inputMap.put("voToServiceMap", voToServiceMap);
               resObj = this.serv.executeService("GetScalefromDesg", inputMap);
               PayScaleList = (List)inputMap.get("PayScaleList");
               inputMap.put("PayScaleList", PayScaleList);
            } else {
               voToServiceMap = new HashMap();
               voToServiceMap.put("commissionId", MstEmpObj.getPayCommission());
               inputMap.put("voToServiceMap", voToServiceMap);
               resObj = this.serv.executeService("GetScalefromDesg", inputMap);
               PayScaleList = (List)inputMap.get("PayScaleList");
               inputMap.put("PayScaleList", PayScaleList);
            }

            lLstOfficesForPost = null;
            listCadres = null;
            if (lObjHstDcpsOfficeChanges.getParentDept() != null) {
               listCadres = lObjDcpsCommonDAO.getCadreForDept(Long.valueOf(lObjHstDcpsOfficeChanges.getParentDept()));
            } else {
               listCadres = lObjDcpsCommonDAO.getCadreForDept(Long.valueOf(MstEmpObj.getParentDept()));
            }

            inputMap.put("CADRELIST", listCadres);
            List lLstOfficesForPost;
            if (lObjHstDcpsOfficeChanges.getPostId() != null && lObjHstDcpsOfficeChanges.getPostId() != -1L) {
               lLstOfficesForPost = lObjDcpsCommonDAO.getOfficesForPost(lObjHstDcpsOfficeChanges.getPostId());
            } else {
               lLstOfficesForPost = lObjDcpsCommonDAO.getOfficesForPost(lObjRltDcpsPayrollEmp.getPostId());
            }

            inputMap.put("lLstOfficesForPost", lLstOfficesForPost);
         }

         PayScaleList = lObjDdoProfileDAO.getAllDesignation(this.gLngLangId);
         inputMap.put("lLstAllDesignations", PayScaleList);
         inputMap.put("lObjDdoOfficeVOMst", lObjDdoOfficeVOMst);
         inputMap.put("lObjDdoOfficeVO", lObjDdoOfficeVO);
         lLstOfficesForPost = StringUtility.getParameter("UserType", this.request);
         inputMap.put("UserType", lLstOfficesForPost);
         if (lLstOfficesForPost.equals("DDOAsst")) {
            inputMap.put("EditForm", "Y");
            lStrDdoCode = lObjDcpsCommonDAO.getDdoCode(this.gLngPostId);
            listCadres = this.getHierarchyUsers(inputMap, lLstOfficesForPost);
            inputMap.put("UserList", listCadres);
            inputMap.put("ForwardToPost", listCadres.get(listCadres.size() - 1));
         } else if (lLstOfficesForPost.equals("DDO")) {
            inputMap.put("EditForm", "N");
            lStrDdoCode = lObjDcpsCommonDAO.getDdoCodeForDDO(this.gLngPostId);
         }

         inputMap.put("DDOCODE", lStrDdoCode);
         listCadres = lObjDcpsCommonDAO.getCurrentOffices(lStrDdoCode);
         inputMap.put("OFFICELIST", listCadres);
         List lLstParentDept = lObjDcpsCommonDAO.getParentDeptForDDO(lStrDdoCode);
         Object[] objParentDept = (Object[])lLstParentDept.get(0);
         List lLstDesignation = lObjDcpsCommonDAO.getAllDesignation((Long)objParentDept[0], this.gLngLangId);
         inputMap.put("lLstDesignation", lLstDesignation);
         List listParentDept = lObjDcpsCommonDAO.getAllHODDepartment(Long.parseLong(this.gObjRsrcBndle.getString("DCPS.CurrentFieldDeptID")), this.gLngLangId);
         inputMap.put("listParentDept", listParentDept);
         inputMap.put("lDtJoiDtLimit", "01/01/2005");
         lStrRetiringYear = StringUtility.getParameter("designationId", this.request);
         inputMap.put("lStrDesignation", lStrRetiringYear);
         String lStrChangesType = StringUtility.getParameter("changesType", this.request);
         inputMap.put("lStrChangesType", lStrChangesType);
         resObj.setResultValue(inputMap);
         resObj.setViewName("ChangeOfficeDetails");
      } catch (Exception var47) {
         resObj.setResultValue((Object)null);
         resObj.setThrowable(var47);
         resObj.setResultCode(-1);
         resObj.setViewName("errorPage");
      }

      return resObj;
   }

   public ResultObject changesOtherDetails(Map inputMap) {
      ResultObject resObj = new ResultObject(0, "FAIL");
      Long dcpsChangesId = null;
      HstDcpsChanges lObjHstDcpsChanges = null;
      HstDcpsOtherChanges lObjHstDcpsOtherChanges = null;
      Long HstDcpsOtherChangesId = null;
      String lStrDesignationDraft = null;
      MstEmp MstEmpObj = null;
      RltDcpsPayrollEmp RltDcpsPayrollEmpObj = null;
      String lStrDdoCode = null;

      try {
         this.gLogger.info(" in changesOtherDetails 1");
         this.setSessionInfo(inputMap);
         ChangesFormDAO lObjChangesDAO = new ChangesFormDAOImpl(HstDcpsOtherChanges.class, this.serv.getSessionFactory());
         DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl((Class)null, this.serv.getSessionFactory());
         NewRegDdoDAO lObjNewRegPayrollDdoDAO = new NewRegDdoDAOImpl(RltDcpsPayrollEmp.class, this.serv.getSessionFactory());
         NewRegDdoDAO lObjNewRegDdoDAOForCadre = new NewRegDdoDAOImpl(DcpsCadreMst.class, this.serv.getSessionFactory());
         this.gLogger.info(" in changesOtherDetails 2");
         Long lLngEmpID = Long.parseLong(StringUtility.getParameter("EmpId", this.request));
         if (!StringUtility.getParameter("dcpsChangesId", this.request).equalsIgnoreCase("") && StringUtility.getParameter("dcpsChangesId", this.request) != null) {
            dcpsChangesId = Long.valueOf(StringUtility.getParameter("dcpsChangesId", this.request));
            lObjHstDcpsChanges = lObjChangesDAO.getChangesDetails(dcpsChangesId);
            HstDcpsOtherChangesId = lObjChangesDAO.getOtherChangesIdforChangesId(dcpsChangesId);
            lObjHstDcpsOtherChanges = (HstDcpsOtherChanges)lObjChangesDAO.read(HstDcpsOtherChangesId);
            lStrDesignationDraft = StringUtility.getParameter("designationDraft", this.request);
         }

         this.gLogger.info(" in changesOtherDetails 3 ");
         MstEmpObj = lObjChangesDAO.getEmpDetails(lLngEmpID);
         RltDcpsPayrollEmpObj = lObjNewRegPayrollDdoDAO.getPayrollVOForEmpId(MstEmpObj.getDcpsEmpId());
         inputMap.put("lStrDesignationDraft", lStrDesignationDraft);
         inputMap.put("dcpsChangesId", dcpsChangesId);
         inputMap.put("lObjHstDcpsChanges", lObjHstDcpsChanges);
         inputMap.put("lObjHstDcpsOtherChanges", lObjHstDcpsOtherChanges);
         inputMap.put("lObjEmpData", MstEmpObj);
         inputMap.put("lObjRltDcpsPayrollEmp", RltDcpsPayrollEmpObj);
         DcpsCadreMst lObjMstCadre = null;
         lObjMstCadre = (DcpsCadreMst)lObjNewRegDdoDAOForCadre.read(Long.valueOf(MstEmpObj.getCadre()));
         String lStrUserType;
         if (lObjMstCadre != null) {
            inputMap.put("SuperAnnAge", lObjMstCadre.getSuperAntunAge());
            lStrUserType = lObjDcpsCommonDAO.getCmnLookupNameFromId(Long.valueOf(lObjMstCadre.getGroupId().trim()));
            inputMap.put("GroupName", lStrUserType.trim());
         }

         this.gLogger.info(" in changesOtherDetails 4");
         lStrUserType = StringUtility.getParameter("UserType", this.request);
         inputMap.put("UserType", lStrUserType);
         List lLstBankNames;
         if (lStrUserType.equals("DDOAsst")) {
            inputMap.put("EditForm", "Y");
            lStrDdoCode = lObjDcpsCommonDAO.getDdoCode(this.gLngPostId);
            lLstBankNames = this.getHierarchyUsers(inputMap, lStrUserType);
            inputMap.put("UserList", lLstBankNames);
            inputMap.put("ForwardToPost", lLstBankNames.get(lLstBankNames.size() - 1));
         } else if (lStrUserType.equals("DDO")) {
            inputMap.put("EditForm", "N");
            lStrDdoCode = lObjDcpsCommonDAO.getDdoCodeForDDO(this.gLngPostId);
         }

         inputMap.put("DDOCODE", lStrDdoCode);
         lLstBankNames = lObjDcpsCommonDAO.getBankNames();
         inputMap.put("BANKNAMES", lLstBankNames);
         List lLstPFAccntMntdByDCPS;
         if (MstEmpObj != null && MstEmpObj.getBankName() != null) {
            lLstPFAccntMntdByDCPS = lObjDcpsCommonDAO.getBranchNames(Long.valueOf(MstEmpObj.getBankName()));
            inputMap.put("BRANCHNAMESMST", lLstPFAccntMntdByDCPS);
         }

         if (lObjHstDcpsOtherChanges != null && lObjHstDcpsOtherChanges.getBankName() != null) {
            lLstPFAccntMntdByDCPS = lObjDcpsCommonDAO.getBranchNames(Long.valueOf(lObjHstDcpsOtherChanges.getBankName()));
            inputMap.put("BRANCHNAMES", lLstPFAccntMntdByDCPS);
         }

         lLstPFAccntMntdByDCPS = IFMSCommonServiceImpl.getLookupValues("AccountMaintainedByForDCPSEmp", SessionHelper.getLangId(inputMap), inputMap);
         inputMap.put("lLstPFAccntMntdByDCPS", lLstPFAccntMntdByDCPS);
         List lLstPFAccntMntdBy = IFMSCommonServiceImpl.getLookupValues("AccountMaintaindedBy", SessionHelper.getLangId(inputMap), inputMap);
         inputMap.put("lLstPFAccntMntdBy", lLstPFAccntMntdBy);
         List lLstPFSeriesMst = null;
         String lStrAcMntndByMst = RltDcpsPayrollEmpObj.getAcMaintainedBy();
         this.gLogger.info("accountMaintainedBy 1 : " + lStrAcMntndByMst);
         String MumbaiOrNagpurAGMst = null;
         if (lStrAcMntndByMst != null && !lStrAcMntndByMst.equals("")) {
            if (lStrAcMntndByMst.equals("700092")) {
               lLstPFSeriesMst = IFMSCommonServiceImpl.getLookupValues("PF_Series", SessionHelper.getLangId(inputMap), inputMap);
               this.gLogger.info(" lLstPFSeriesMst  : " + lLstPFSeriesMst);
               MumbaiOrNagpurAGMst = "Yes";
               this.gLogger.info(" MumbaiOrNagpurAGMst  : " + MumbaiOrNagpurAGMst);
            } else if (lStrAcMntndByMst.equals("700093")) {
               lLstPFSeriesMst = IFMSCommonServiceImpl.getLookupValues("PF_Series_AG_Nagpur", SessionHelper.getLangId(inputMap), inputMap);
               MumbaiOrNagpurAGMst = "Yes";
            } else {
               MumbaiOrNagpurAGMst = "No";
            }
         }

         inputMap.put("lLstPFSeriesMst", lLstPFSeriesMst);
         inputMap.put("MumbaiOrNagpurAGMst", MumbaiOrNagpurAGMst);
         List lLstPFSeries = null;
         String MumbaiOrNagpurAG = null;
         this.gLogger.info(" lObj_Hst_Dcps_Other_Changes  : " + lObjHstDcpsOtherChanges);
         String lStrAcMntndBy;
         if (lObjHstDcpsOtherChanges != null) {
            if (lObjHstDcpsOtherChanges.getAcMaintainedBy() != null) {
               lStrAcMntndBy = lObjHstDcpsOtherChanges.getAcMaintainedBy();
               this.gLogger.info("accountMaintainedBy 2 : " + lStrAcMntndBy);
               if (lStrAcMntndBy != null && !lStrAcMntndBy.equals("")) {
                  if (lStrAcMntndBy.equals("700092")) {
                     lLstPFSeries = IFMSCommonServiceImpl.getLookupValues("PF_Series", SessionHelper.getLangId(inputMap), inputMap);
                     MumbaiOrNagpurAG = "Yes";
                  } else if (lStrAcMntndBy.equals("700093")) {
                     lLstPFSeries = IFMSCommonServiceImpl.getLookupValues("PF_Series_AG_Nagpur", SessionHelper.getLangId(inputMap), inputMap);
                     MumbaiOrNagpurAG = "Yes";
                  } else {
                     MumbaiOrNagpurAG = "No";
                  }
               }
            }
         } else {
            MumbaiOrNagpurAG = MumbaiOrNagpurAGMst;
            lLstPFSeries = lLstPFSeriesMst;
         }

         inputMap.put("lLstPFSeries", lLstPFSeries);
         inputMap.put("MumbaiOrNagpurAG", MumbaiOrNagpurAG);
         lStrAcMntndBy = StringUtility.getParameter("designationId", this.request);
         inputMap.put("lStrDesignation", lStrAcMntndBy);
         String lStrChangesType = StringUtility.getParameter("changesType", this.request);
         inputMap.put("lStrChangesType", lStrChangesType);
         resObj.setResultValue(inputMap);
         resObj.setViewName("ChangeOtherDetails");
      } catch (Exception var28) {
         resObj.setResultValue((Object)null);
         resObj.setThrowable(var28);
         resObj.setResultCode(-1);
         resObj.setViewName("errorPage");
      }

      return resObj;
   }

   public ResultObject changesNomineeDetails(Map inputMap) {
      ResultObject resObj = new ResultObject(0, "FAIL");
      Long dcpsChangesId = null;
      HstDcpsChanges lObjHstDcpsChanges = null;
      String lStrDesignationDraft = null;
      MstEmp MstEmpObj = null;
      List<MstEmpNmn> NomineesList = null;
      List<HstDcpsNomineeChanges> NomineesHstList = null;
      Integer lIntTotalNominees = null;
      String lStrDdoCode = null;

      try {
         this.setSessionInfo(inputMap);
         SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
         ChangesFormDAO lObjChangesDAO = new ChangesFormDAOImpl((Class)null, this.serv.getSessionFactory());
         DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl((Class)null, this.serv.getSessionFactory());
         Long lLngEmpID = Long.parseLong(StringUtility.getParameter("EmpId", this.request));
         if (!StringUtility.getParameter("dcpsChangesId", this.request).equalsIgnoreCase("") && StringUtility.getParameter("dcpsChangesId", this.request) != null) {
            dcpsChangesId = Long.valueOf(StringUtility.getParameter("dcpsChangesId", this.request));
            Long latestRefIdForNomineeChanges = lObjChangesDAO.getLatestRefIdForNomineeChanges(lLngEmpID, dcpsChangesId);
            NomineesHstList = lObjChangesDAO.getNomineesFromHst(latestRefIdForNomineeChanges, lLngEmpID);
            lIntTotalNominees = NomineesHstList.size();
            lObjHstDcpsChanges = lObjChangesDAO.getChangesDetails(dcpsChangesId);
            lStrDesignationDraft = StringUtility.getParameter("designationDraft", this.request);
            this.gLogger.info("dcps_changes_id: " + dcpsChangesId);
         }

         this.gLogger.info("dcps_changes_id 2: " + dcpsChangesId);
         MstEmpObj = lObjChangesDAO.getEmpDetails(lLngEmpID);
         NomineesList = lObjChangesDAO.getNominees(lLngEmpID.toString());
         lIntTotalNominees = NomineesList.size();
         inputMap.put("NomineesHstList", NomineesHstList);
         inputMap.put("NomineesList", NomineesList);
         inputMap.put("lStrDesignationDraft", lStrDesignationDraft);
         inputMap.put("dcpsChangesId", dcpsChangesId);
         inputMap.put("lObjHstDcpsChanges", lObjHstDcpsChanges);
         inputMap.put("lObjEmpData", MstEmpObj);
         inputMap.put("EmployeeID", lLngEmpID);
         inputMap.put("lIntTotalNominees", lIntTotalNominees);
         List listRelationship = IFMSCommonServiceImpl.getLookupValues("Relationship", SessionHelper.getLangId(inputMap), inputMap);
         inputMap.put("listRelationship", listRelationship);
         Date lDtcurDate = SessionHelper.getCurDate();
         inputMap.put("lDtCurDate", lObjDateFormat.format(lDtcurDate));
         String lStrUserType = StringUtility.getParameter("UserType", this.request);
         inputMap.put("UserType", lStrUserType);
         if (lStrUserType.equals("DDOAsst")) {
            inputMap.put("EditForm", "Y");
            lStrDdoCode = lObjDcpsCommonDAO.getDdoCode(this.gLngPostId);
            List UserList = this.getHierarchyUsers(inputMap, lStrUserType);
            inputMap.put("UserList", UserList);
            inputMap.put("ForwardToPost", UserList.get(UserList.size() - 1));
         } else if (lStrUserType.equals("DDO")) {
            inputMap.put("EditForm", "N");
            lStrDdoCode = lObjDcpsCommonDAO.getDdoCodeForDDO(this.gLngPostId);
         }

         inputMap.put("DDOCODE", lStrDdoCode);
         String lStrDesignation = StringUtility.getParameter("designationId", this.request);
         inputMap.put("lStrDesignation", lStrDesignation);
         String lStrChangesType = StringUtility.getParameter("changesType", this.request);
         inputMap.put("lStrChangesType", lStrChangesType);
         resObj.setResultValue(inputMap);
         resObj.setViewName("ChangeNomineeDetails");
      } catch (Exception var20) {
         var20.printStackTrace();
         resObj.setResultValue((Object)null);
         resObj.setThrowable(var20);
         resObj.setResultCode(-1);
         resObj.setViewName("errorPage");
      }

      return resObj;
   }

   public ResultObject changesPhotoAndSignatureDetails(Map inputMap) {
      ResultObject resObj = new ResultObject(0, "FAIL");

      try {
         resObj.setResultValue(inputMap);
         this.setSessionInfo(inputMap);
         List lLngNewPhotoSignList = null;
         Long lLngNewPhotoAttachmentId = null;
         Long lLngNewSignAttachmentId = null;
         Long lLngPhotoAttachmentId = null;
         Long lLngSignAttachmentId = null;
         Long dcpsChangesId = null;
         String photoAttachId = "";
         String signAttachId = "";
         boolean SFTPflag = false;
         boolean SFTPflag1 = false;
         MstEmp MstEmpObj = null;
         String lStrDesignationDraft = null;
         HstDcpsChanges lObjHstDcpsChanges = null;
         ChangesFormDAO lObjChangesDAO = new ChangesFormDAOImpl(TrnDcpsChanges.class, this.serv.getSessionFactory());
         DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl((Class)null, this.serv.getSessionFactory());
         OrderMstDAO orderMasterDAO = new OrderMstDAOImpl(HrPayOrderMst.class, this.serv.getSessionFactory());
         Long srNo = 0L;
         Long lLngEmpID = Long.parseLong(StringUtility.getParameter("EmpId", this.request));
         Set cmnAttachmentMpgs;
         CmnAttachmentMpg cmnAttachmentMpg;
         Iterator cmnAttachmentMpgIterator;
         CmnAttachmentMstDAOImpl lObjCmnAttachmentMstDAO;
         CmnAttachmentMst lObjCmnAttachmentMst;
         if (!StringUtility.getParameter("dcpsChangesId", this.request).equalsIgnoreCase("") && StringUtility.getParameter("dcpsChangesId", this.request) != null) {
            dcpsChangesId = Long.valueOf(StringUtility.getParameter("dcpsChangesId", this.request));
            this.gLogger.info("dcpsChangesId" + dcpsChangesId);
            lObjHstDcpsChanges = lObjChangesDAO.getChangesDetails(dcpsChangesId);
            StringUtility.getParameter("designationDraft", this.request);
            lLngNewPhotoSignList = lObjChangesDAO.getPhotoSignNewValue(dcpsChangesId);
            List srno;
            Blob blob;
            if (lLngNewPhotoSignList.get(0) != null) {
               lLngNewPhotoAttachmentId = Long.parseLong(lLngNewPhotoSignList.get(0).toString());
               photoAttachId = Long.toString(lLngNewPhotoAttachmentId);
               srno = orderMasterDAO.getSRno(photoAttachId);
               if (srno != null && srno.size() > 0) {
                  this.gLogger.info("inside first if srno size is " + srno.size());
                  blob = orderMasterDAO.getAttachment(srno.get(0).toString());
                  if (blob != null) {
                     this.gLogger.info("inside second if blob length is " + blob.length());
                     SFTPflag1 = true;
                  }
               }
            }

            if (lLngNewPhotoSignList.get(1) != null) {
               lLngNewSignAttachmentId = Long.parseLong(lLngNewPhotoSignList.get(1).toString());
               photoAttachId = Long.toString(lLngNewSignAttachmentId);
               srno = orderMasterDAO.getSRno(photoAttachId);
               if (srno != null && srno.size() > 0) {
                  this.gLogger.info("inside first if srno size is " + srno.size());
                  blob = orderMasterDAO.getAttachment(srno.get(0).toString());
                  if (blob != null) {
                     this.gLogger.info("inside second if blob length is " + blob.length());
                     SFTPflag1 = true;
                  }
               }
            }

            lObjCmnAttachmentMstDAO = new CmnAttachmentMstDAOImpl(CmnAttachmentMst.class, this.serv.getSessionFactory());
            lObjCmnAttachmentMst = new CmnAttachmentMst();
            if (lLngNewPhotoAttachmentId != null) {
               lObjCmnAttachmentMst = lObjCmnAttachmentMstDAO.findByAttachmentId(Long.parseLong(lLngNewPhotoAttachmentId.toString()));
            }

            new HashSet();
            cmnAttachmentMpgs = lObjCmnAttachmentMst.getCmnAttachmentMpgs();
            new CmnAttachmentMpg();
            cmnAttachmentMpgIterator = cmnAttachmentMpgs.iterator();
            srNo = 0L;

            int j;
            for(j = 0; j < cmnAttachmentMpgs.size(); ++j) {
               cmnAttachmentMpg = (CmnAttachmentMpg)cmnAttachmentMpgIterator.next();
               if (cmnAttachmentMpg.getAttachmentDesc().equalsIgnoreCase("photo")) {
                  srNo = cmnAttachmentMpg.getSrNo();
               }
            }

            inputMap.put("Photo", lObjCmnAttachmentMst);
            inputMap.put("PhotoId", lLngNewPhotoAttachmentId);
            inputMap.put("PhotosrNo", srNo);
            inputMap.put("lObjHstDcpsChanges", lObjHstDcpsChanges);
            inputMap.put("dcpsChangesId", dcpsChangesId);
            lObjCmnAttachmentMstDAO = new CmnAttachmentMstDAOImpl(CmnAttachmentMst.class, this.serv.getSessionFactory());
            lObjCmnAttachmentMst = new CmnAttachmentMst();
            if (lLngNewSignAttachmentId != null) {
               lObjCmnAttachmentMst = lObjCmnAttachmentMstDAO.findByAttachmentId(Long.parseLong(lLngNewSignAttachmentId.toString()));
            }

            new HashSet();
            cmnAttachmentMpgs = lObjCmnAttachmentMst.getCmnAttachmentMpgs();
            new CmnAttachmentMpg();
            cmnAttachmentMpgIterator = cmnAttachmentMpgs.iterator();

            for(j = 0; j < cmnAttachmentMpgs.size(); ++j) {
               cmnAttachmentMpg = (CmnAttachmentMpg)cmnAttachmentMpgIterator.next();
               if (cmnAttachmentMpg.getAttachmentDesc().equalsIgnoreCase("signature")) {
                  srNo = cmnAttachmentMpg.getSrNo();
               }
            }

            inputMap.put("Sign", lObjCmnAttachmentMst);
            inputMap.put("SignId", lLngNewSignAttachmentId);
            inputMap.put("SignsrNo", srNo);
            lStrDesignationDraft = StringUtility.getParameter("designationDraft", this.request);
         }

         MstEmpObj = lObjChangesDAO.getEmpDetails(lLngEmpID);
         lObjCmnAttachmentMstDAO = new CmnAttachmentMstDAOImpl(CmnAttachmentMst.class, this.serv.getSessionFactory());
         new CmnAttachmentMst();
         cmnAttachmentMpgs = null;
         cmnAttachmentMpg = null;
         cmnAttachmentMpgIterator = null;
         lLngPhotoAttachmentId = MstEmpObj.getPhotoAttachmentID();
         Blob blob;
         List srno;
         int j;
         if (lLngPhotoAttachmentId != null) {
            lObjCmnAttachmentMst = lObjCmnAttachmentMstDAO.findByAttachmentId(Long.parseLong(lLngPhotoAttachmentId.toString()));
            photoAttachId = Long.toString(lLngPhotoAttachmentId);
            srno = orderMasterDAO.getSRno(photoAttachId);
            if (srno != null && srno.size() > 0) {
               this.gLogger.info("inside first if srno size is " + srno.size());
               blob = orderMasterDAO.getAttachment(srno.get(0).toString());
               if (blob != null) {
                  this.gLogger.info("inside second if blob length is " + blob.length());
                  SFTPflag = true;
               }
            }

            new HashSet();
            this.gLogger.info("outside if loop SFTP flag value is " + SFTPflag);
            cmnAttachmentMpgs = lObjCmnAttachmentMst.getCmnAttachmentMpgs();
            new CmnAttachmentMpg();
            cmnAttachmentMpgIterator = cmnAttachmentMpgs.iterator();
            srNo = 0L;

            for(j = 0; j < cmnAttachmentMpgs.size(); ++j) {
               cmnAttachmentMpg = (CmnAttachmentMpg)cmnAttachmentMpgIterator.next();
               if (cmnAttachmentMpg.getAttachmentDesc().equalsIgnoreCase("photo")) {
                  srNo = cmnAttachmentMpg.getSrNo();
               }
            }
         }

         inputMap.put("PhotoId1", lLngPhotoAttachmentId);
         inputMap.put("PhotosrNo1", srNo);
         lObjCmnAttachmentMstDAO = new CmnAttachmentMstDAOImpl(CmnAttachmentMst.class, this.serv.getSessionFactory());
         new CmnAttachmentMst();
         lLngSignAttachmentId = MstEmpObj.getSignatureAttachmentID();
         if (lLngSignAttachmentId != null) {
            lObjCmnAttachmentMst = lObjCmnAttachmentMstDAO.findByAttachmentId(Long.parseLong(lLngSignAttachmentId.toString()));
            signAttachId = Long.toString(lLngSignAttachmentId);
            srno = orderMasterDAO.getSRno(signAttachId);
            if (srno != null && srno.size() > 0) {
               blob = orderMasterDAO.getAttachment(srno.get(0).toString());
               if (blob != null) {
                  SFTPflag = true;
               }
            }

            new HashSet();
            cmnAttachmentMpgs = lObjCmnAttachmentMst.getCmnAttachmentMpgs();
            new CmnAttachmentMpg();
            cmnAttachmentMpgIterator = cmnAttachmentMpgs.iterator();

            for(j = 0; j < cmnAttachmentMpgs.size(); ++j) {
               cmnAttachmentMpg = (CmnAttachmentMpg)cmnAttachmentMpgIterator.next();
               if (cmnAttachmentMpg.getAttachmentDesc().equalsIgnoreCase("signature")) {
                  srNo = cmnAttachmentMpg.getSrNo();
               }
            }
         }

         inputMap.put("SignId1", lLngSignAttachmentId);
         inputMap.put("SignsrNo1", srNo);
         inputMap.put("lStrDesignationDraft", lStrDesignationDraft);
         String lStrUserType = StringUtility.getParameter("UserType", this.request);
         inputMap.put("UserType", lStrUserType);
         String lStrDesignation = StringUtility.getParameter("designationId", this.request);
         inputMap.put("lStrDesignation", lStrDesignation);
         String lStrChangesType = StringUtility.getParameter("changesType", this.request);
         inputMap.put("lStrChangesType", lStrChangesType);
         inputMap.put("dcpsEmpId", lLngEmpID);
         String lStrDdoCode = null;
         if (lStrUserType.equals("DDOAsst")) {
            inputMap.put("EditForm", "Y");
            lStrDdoCode = lObjDcpsCommonDAO.getDdoCode(this.gLngPostId);
            List UserList = this.getHierarchyUsers(inputMap, lStrUserType);
            inputMap.put("UserList", UserList);
            inputMap.put("ForwardToPost", UserList.get(UserList.size() - 1));
         } else if (lStrUserType.equals("DDO")) {
            inputMap.put("EditForm", "N");
            lStrDdoCode = lObjDcpsCommonDAO.getDdoCodeForDDO(this.gLngPostId);
         }

         inputMap.put("SFTPflag", SFTPflag);
         inputMap.put("SFTPflag1", SFTPflag1);
         inputMap.put("DDOCODE", lStrDdoCode);
         resObj.setResultValue(inputMap);
         resObj.setViewName("ChangePhotoAndSignatureDetails");
      } catch (Exception var31) {
         resObj.setResultValue((Object)null);
         resObj.setThrowable(var31);
         resObj.setResultCode(-1);
         resObj.setViewName("errorPage");
      }

      return resObj;
   }

   public ResultObject updatePersonalDtls(Map inputMap) {
      ResultObject resObj = new ResultObject(0, "FAIL");
      Boolean lBlFlag = false;
      HstDcpsChanges lObjChangesData = null;
      Long lLongEmployeeId = null;
      Long lLongDcpsHstChangesId = null;
      Long lLongChangesId = null;
      Long lLongPersonalChangesId = null;

      String lStrResult;
      try {
         this.setSessionInfo(inputMap);
         ChangesFormDAO lObjChangesFormDAO = new ChangesFormDAOImpl(HstDcpsChanges.class, this.serv.getSessionFactory());
         lStrResult = StringUtility.getParameter("empId", this.request);
         lLongEmployeeId = Long.valueOf(lStrResult);
         this.gLogger.info("EmployeeId:" + lLongEmployeeId);
         this.gLogger.info("EmployeeId:" + lLongEmployeeId);
         if (!StringUtility.getParameter("dcpsHstChangesId", this.request).equalsIgnoreCase("") && StringUtility.getParameter("dcpsHstChangesId", this.request) != null) {
            lLongDcpsHstChangesId = Long.valueOf(StringUtility.getParameter("dcpsHstChangesId", this.request));
         }

         lObjChangesData = (HstDcpsChanges)inputMap.get("lObjChangesData");
         List<TrnDcpsChanges> lObjTrnDcpsChangesList = (List)inputMap.get("lObjTrnDcpsChangesList");
         Integer lInt;
         Long lLongChangesIdpK;
         if (lLongDcpsHstChangesId == null) {
            lLongChangesId = IFMSCommonServiceImpl.getNextSeqNum("HST_DCPS_CHANGES", inputMap);
            lObjChangesData.setDcpsChangesId(lLongChangesId);
            lObjChangesData.setDcpsEmpId(lLongEmployeeId);
            lObjChangesData.setTypeOfChanges("PersonalDetails");
            lObjChangesFormDAO.create(lObjChangesData);

            for(lInt = 0; lInt < lObjTrnDcpsChangesList.size(); lInt = lInt + 1) {
               lLongChangesIdpK = IFMSCommonServiceImpl.getNextSeqNum("TRN_DCPS_CHANGES", inputMap);
               this.gLogger.info("Changes Id pK:" + lLongChangesIdpK);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setDcpsChangesIdPk(lLongChangesIdpK);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setDcpsChangesId(lLongChangesId);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setDcpsEmpId(lLongEmployeeId);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setTypeOfChanges("PersonalDetails");
               lObjChangesFormDAO.create(lObjTrnDcpsChangesList.get(lInt));
               this.gLogger.info("TRN_DCPS_CHANGES created");
            }
         }

         if (lLongDcpsHstChangesId != null) {
            lObjChangesData.setDcpsChangesId(lLongDcpsHstChangesId);
            lObjChangesData.setDcpsEmpId(lLongEmployeeId);
            lObjChangesData.setTypeOfChanges("PersonalDetails");
            lObjChangesFormDAO.update(lObjChangesData);
            lObjChangesFormDAO.deleteTrnVOForDcpsChangesId(lLongDcpsHstChangesId);

            for(lInt = 0; lInt < lObjTrnDcpsChangesList.size(); lInt = lInt + 1) {
               lLongChangesIdpK = IFMSCommonServiceImpl.getNextSeqNum("TRN_DCPS_CHANGES", inputMap);
               this.gLogger.info("Changes Id pK not new :" + lLongChangesIdpK);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setDcpsChangesIdPk(lLongChangesIdpK);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setDcpsChangesId(lLongDcpsHstChangesId);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setDcpsEmpId(lLongEmployeeId);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setTypeOfChanges("PersonalDetails");
               lObjChangesFormDAO.create(lObjTrnDcpsChangesList.get(lInt));
            }
         }

         HstDcpsPersonalChanges lObjHstDcpsPersonalChanges = (HstDcpsPersonalChanges)inputMap.get("lObjHstDcpsPersonalChanges");
         if (lLongDcpsHstChangesId == null) {
            lLongPersonalChangesId = IFMSCommonServiceImpl.getNextSeqNum("HST_DCPS_PERSONAL_CHANGES", inputMap);
            lObjHstDcpsPersonalChanges.setDcpsEmpId(lLongEmployeeId);
            lObjHstDcpsPersonalChanges.setDcpsPersonalChangesId(lLongPersonalChangesId);
            lObjHstDcpsPersonalChanges.setDcpsChangesId(lLongChangesId);
            lObjChangesFormDAO.create(lObjHstDcpsPersonalChanges);
         }

         if (lLongDcpsHstChangesId != null) {
            lLongPersonalChangesId = lObjChangesFormDAO.getPersonalChangesIdforChangesId(lLongDcpsHstChangesId);
            lObjHstDcpsPersonalChanges.setDcpsEmpId(lLongEmployeeId);
            lObjHstDcpsPersonalChanges.setDcpsPersonalChangesId(lLongPersonalChangesId);
            lObjHstDcpsPersonalChanges.setDcpsChangesId(lLongDcpsHstChangesId);
            lObjChangesFormDAO.update(lObjHstDcpsPersonalChanges);
         }

         lBlFlag = true;
      } catch (Exception var14) {
         resObj.setResultValue((Object)null);
         resObj.setThrowable(var14);
         resObj.setResultCode(-1);
         resObj.setViewName("errorPage");
         this.gLogger.error(" Error is : " + var14, var14);
      }

      String lSBStatus = this.getResponseXMLDocUpdtOrFwd(lBlFlag, lObjChangesData.getDcpsChangesId()).toString();
      lStrResult = (new AjaxXmlBuilder()).addItem("ajax_key", lSBStatus).toString();
      inputMap.put("ajaxKey", lStrResult);
      resObj.setResultValue(inputMap);
      resObj.setViewName("ajaxData");
      return resObj;
   }

   public ResultObject updateOfficeDtls(Map inputMap) {
      ResultObject resObj = new ResultObject(0, "FAIL");
      HstDcpsChanges lObjChangesData = null;
      Boolean lBlFlag = false;
      Long lLongDcpsHstChangesId = null;
      Long lLongChangesId = null;
      Long lLongOfficeChangesId = null;

      String lStrResult;
      try {
         this.setSessionInfo(inputMap);
         ChangesFormDAO lObjChangesFormDAO = new ChangesFormDAOImpl(HstDcpsChanges.class, this.serv.getSessionFactory());
         lStrResult = StringUtility.getParameter("empId", this.request);
         Long lLongEmployeeId = Long.valueOf(lStrResult);
         if (!StringUtility.getParameter("dcpsHstChangesId", this.request).equalsIgnoreCase("") && StringUtility.getParameter("dcpsHstChangesId", this.request) != null) {
            lLongDcpsHstChangesId = Long.valueOf(StringUtility.getParameter("dcpsHstChangesId", this.request));
         }

         lObjChangesData = (HstDcpsChanges)inputMap.get("lObjChangesData");
         List<TrnDcpsChanges> lObjTrnDcpsChangesList = (List)inputMap.get("lObjTrnDcpsChangesList");
         Integer lInt;
         Long lLongChangesIdpK;
         if (lLongDcpsHstChangesId == null) {
            lLongChangesId = IFMSCommonServiceImpl.getNextSeqNum("HST_DCPS_CHANGES", inputMap);
            lObjChangesData.setDcpsChangesId(lLongChangesId);
            lObjChangesData.setDcpsEmpId(lLongEmployeeId);
            lObjChangesData.setTypeOfChanges("OfficeDetails");
            lObjChangesFormDAO.create(lObjChangesData);

            for(lInt = 0; lInt < lObjTrnDcpsChangesList.size(); lInt = lInt + 1) {
               lLongChangesIdpK = IFMSCommonServiceImpl.getNextSeqNum("TRN_DCPS_CHANGES", inputMap);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setDcpsChangesIdPk(lLongChangesIdpK);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setDcpsChangesId(lLongChangesId);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setDcpsEmpId(lLongEmployeeId);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setTypeOfChanges("OfficeDetails");
               lObjChangesFormDAO.create(lObjTrnDcpsChangesList.get(lInt));
            }
         }

         if (lLongDcpsHstChangesId != null) {
            lObjChangesData.setDcpsChangesId(lLongDcpsHstChangesId);
            lObjChangesData.setDcpsEmpId(lLongEmployeeId);
            lObjChangesData.setTypeOfChanges("OfficeDetails");
            lObjChangesFormDAO.update(lObjChangesData);
            lObjChangesFormDAO.deleteTrnVOForDcpsChangesId(lLongDcpsHstChangesId);

            for(lInt = 0; lInt < lObjTrnDcpsChangesList.size(); lInt = lInt + 1) {
               lLongChangesIdpK = IFMSCommonServiceImpl.getNextSeqNum("TRN_DCPS_CHANGES", inputMap);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setDcpsChangesIdPk(lLongChangesIdpK);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setDcpsChangesId(lLongDcpsHstChangesId);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setDcpsEmpId(lLongEmployeeId);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setTypeOfChanges("OfficeDetails");
               lObjChangesFormDAO.create(lObjTrnDcpsChangesList.get(lInt));
            }
         }

         HstDcpsOfficeChanges lObjHstDcpsOfficeChanges = (HstDcpsOfficeChanges)inputMap.get("lObjHstDcpsOfficeChanges");
         if (lLongDcpsHstChangesId == null) {
            lLongOfficeChangesId = IFMSCommonServiceImpl.getNextSeqNum("HST_DCPS_OFFICE_CHANGES", inputMap);
            lObjHstDcpsOfficeChanges.setDcpsEmpId(lLongEmployeeId);
            lObjHstDcpsOfficeChanges.setDcpsOfficeChangesId(lLongOfficeChangesId);
            lObjHstDcpsOfficeChanges.setDcpsChangesId(lLongChangesId);
            lObjChangesFormDAO.create(lObjHstDcpsOfficeChanges);
         }

         if (lLongDcpsHstChangesId != null) {
            lLongOfficeChangesId = lObjChangesFormDAO.getOfficeChangesIdforChangesId(lLongDcpsHstChangesId);
            lObjHstDcpsOfficeChanges.setDcpsEmpId(lLongEmployeeId);
            lObjHstDcpsOfficeChanges.setDcpsOfficeChangesId(lLongOfficeChangesId);
            lObjHstDcpsOfficeChanges.setDcpsChangesId(lLongDcpsHstChangesId);
            lObjChangesFormDAO.update(lObjHstDcpsOfficeChanges);
         }

         lBlFlag = true;
      } catch (Exception var14) {
         resObj.setResultValue((Object)null);
         resObj.setThrowable(var14);
         resObj.setResultCode(-1);
         resObj.setViewName("errorPage");
         this.gLogger.error(" Error is : " + var14, var14);
      }

      String lSBStatus = this.getResponseXMLDocUpdtOrFwd(lBlFlag, lObjChangesData.getDcpsChangesId()).toString();
      lStrResult = (new AjaxXmlBuilder()).addItem("ajax_key", lSBStatus).toString();
      inputMap.put("ajaxKey", lStrResult);
      resObj.setResultValue(inputMap);
      resObj.setViewName("ajaxData");
      return resObj;
   }

   public ResultObject updateOtherDtls(Map inputMap) {
      ResultObject resObj = new ResultObject(0, "FAIL");
      Boolean lBlFlag = false;
      HstDcpsChanges lObjChangesData = null;

      String lStrResult;
      try {
         this.setSessionInfo(inputMap);
         ChangesFormDAO lObjChangesFormDAO = new ChangesFormDAOImpl(HstDcpsChanges.class, this.serv.getSessionFactory());
         lStrResult = StringUtility.getParameter("empId", this.request);
         Long lLongEmployeeId = Long.valueOf(lStrResult);
         lObjChangesData = (HstDcpsChanges)inputMap.get("lObjChangesData");
         Long lLongDcpsHstChangesId = null;
         Long lLongChangesId = null;
         Long lLongOtherChangesId = null;
         if (!StringUtility.getParameter("dcpsHstChangesId", this.request).equalsIgnoreCase("") && StringUtility.getParameter("dcpsHstChangesId", this.request) != null) {
            lLongDcpsHstChangesId = Long.valueOf(StringUtility.getParameter("dcpsHstChangesId", this.request));
         }

         List<TrnDcpsChanges> lObjTrnDcpsChangesList = (List)inputMap.get("lObjTrnDcpsChangesList");
         Integer lInt;
         Long lLongChangesIdpK;
         if (lLongDcpsHstChangesId == null) {
            lLongChangesId = IFMSCommonServiceImpl.getNextSeqNum("HST_DCPS_CHANGES", inputMap);
            lObjChangesData.setDcpsChangesId(lLongChangesId);
            lObjChangesData.setDcpsEmpId(lLongEmployeeId);
            lObjChangesData.setTypeOfChanges("OtherDetails");
            lObjChangesFormDAO.create(lObjChangesData);

            for(lInt = 0; lInt < lObjTrnDcpsChangesList.size(); lInt = lInt + 1) {
               lLongChangesIdpK = IFMSCommonServiceImpl.getNextSeqNum("TRN_DCPS_CHANGES", inputMap);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setDcpsChangesIdPk(lLongChangesIdpK);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setDcpsChangesId(lLongChangesId);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setDcpsEmpId(lLongEmployeeId);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setTypeOfChanges("OtherDetails");
               lObjChangesFormDAO.create(lObjTrnDcpsChangesList.get(lInt));
            }
         }

         if (lLongDcpsHstChangesId != null) {
            lObjChangesData.setDcpsChangesId(lLongDcpsHstChangesId);
            lObjChangesData.setDcpsEmpId(lLongEmployeeId);
            lObjChangesData.setTypeOfChanges("OtherDetails");
            lObjChangesFormDAO.update(lObjChangesData);
            lObjChangesFormDAO.deleteTrnVOForDcpsChangesId(lLongDcpsHstChangesId);

            for(lInt = 0; lInt < lObjTrnDcpsChangesList.size(); lInt = lInt + 1) {
               lLongChangesIdpK = IFMSCommonServiceImpl.getNextSeqNum("TRN_DCPS_CHANGES", inputMap);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setDcpsChangesIdPk(lLongChangesIdpK);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setDcpsChangesId(lLongDcpsHstChangesId);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setDcpsEmpId(lLongEmployeeId);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setTypeOfChanges("OtherDetails");
               lObjChangesFormDAO.create(lObjTrnDcpsChangesList.get(lInt));
            }
         }

         HstDcpsOtherChanges lObjHstDcpsOtherChanges = (HstDcpsOtherChanges)inputMap.get("lObjHstDcpsOtherChanges");
         if (lLongDcpsHstChangesId == null) {
            lLongOtherChangesId = IFMSCommonServiceImpl.getNextSeqNum("HST_DCPS_OTHER_CHANGES", inputMap);
            lObjHstDcpsOtherChanges.setDcpsEmpId(lLongEmployeeId);
            lObjHstDcpsOtherChanges.setDcpsOtherChangesId(lLongOtherChangesId);
            lObjHstDcpsOtherChanges.setDcpsChangesId(lLongChangesId);
            lObjChangesFormDAO.create(lObjHstDcpsOtherChanges);
         }

         if (lLongDcpsHstChangesId != null) {
            lLongOtherChangesId = lObjChangesFormDAO.getOtherChangesIdforChangesId(lLongDcpsHstChangesId);
            lObjHstDcpsOtherChanges.setDcpsEmpId(lLongEmployeeId);
            lObjHstDcpsOtherChanges.setDcpsOtherChangesId(lLongOtherChangesId);
            lObjHstDcpsOtherChanges.setDcpsChangesId(lLongDcpsHstChangesId);
            lObjChangesFormDAO.update(lObjHstDcpsOtherChanges);
         }

         lBlFlag = true;
      } catch (Exception var14) {
         resObj.setResultValue((Object)null);
         resObj.setThrowable(var14);
         resObj.setResultCode(-1);
         resObj.setViewName("errorPage");
         this.gLogger.error(" Error is : " + var14, var14);
      }

      String lSBStatus = this.getResponseXMLDocUpdtOrFwd(lBlFlag, lObjChangesData.getDcpsChangesId()).toString();
      lStrResult = (new AjaxXmlBuilder()).addItem("ajax_key", lSBStatus).toString();
      inputMap.put("ajaxKey", lStrResult);
      resObj.setResultValue(inputMap);
      resObj.setViewName("ajaxData");
      return resObj;
   }

   public ResultObject updateNomineeDtls(Map inputMap) {
      ResultObject resObj = new ResultObject(0, "FAIL");
      Boolean lBlFlag = false;
      HstDcpsChanges lObjChangesData = null;
      Long lLongDcpsHstChangesId = null;
      Long lLongChangesId = null;
      Long dcpsNomineeChangesId = null;
      Long lLongChangesNomineeRefId = null;
      Long lLongChangesNomineeRefIdForChanges = null;

      String lStrResult;
      try {
         this.setSessionInfo(inputMap);
         ChangesFormDAO lObjChangesFormDAO = new ChangesFormDAOImpl(HstDcpsChanges.class, this.serv.getSessionFactory());
         lStrResult = StringUtility.getParameter("empId", this.request);
         Long lLngEmpID = Long.parseLong(lStrResult);
         lObjChangesData = (HstDcpsChanges)inputMap.get("lObjChangesData");
         if (!StringUtility.getParameter("dcpsHstChangesId", this.request).equalsIgnoreCase("") && StringUtility.getParameter("dcpsHstChangesId", this.request) != null) {
            lLongDcpsHstChangesId = Long.valueOf(StringUtility.getParameter("dcpsHstChangesId", this.request));
            this.gLogger.info("Dcps1 :" + dcpsNomineeChangesId);
         }

         List<TrnDcpsChanges> lObjTrnDcpsChangesList = (List)inputMap.get("lObjTrnDcpsChangesList");
         lLongChangesNomineeRefId = lObjChangesFormDAO.getNextRefIdForHstNomineeChanges(lLngEmpID);
         lLongChangesNomineeRefIdForChanges = lLongChangesNomineeRefId + 1L;
         Integer lInt;
         Long lLongChangesIdpK;
         if (lLongDcpsHstChangesId == null) {
            lLongChangesId = IFMSCommonServiceImpl.getNextSeqNum("HST_DCPS_CHANGES", inputMap);
            lObjChangesData.setDcpsChangesId(lLongChangesId);
            lObjChangesData.setDcpsEmpId(lLngEmpID);
            lObjChangesData.setTypeOfChanges("NomineeDetails");
            lObjChangesFormDAO.create(lObjChangesData);

            for(lInt = 0; lInt < lObjTrnDcpsChangesList.size(); lInt = lInt + 1) {
               lLongChangesIdpK = IFMSCommonServiceImpl.getNextSeqNum("TRN_DCPS_CHANGES", inputMap);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setDcpsChangesIdPk(lLongChangesIdpK);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setDcpsChangesId(lLongChangesId);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setDcpsEmpId(lLngEmpID);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setTypeOfChanges("NomineeDetails");
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setFieldName("changesNomineeRefId");
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setOldValue(lLongChangesNomineeRefId.toString());
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setNewValue(lLongChangesNomineeRefIdForChanges.toString());
               lObjChangesFormDAO.create(lObjTrnDcpsChangesList.get(lInt));
            }
         }

         if (lLongDcpsHstChangesId != null) {
            lObjChangesData.setDcpsChangesId(lLongDcpsHstChangesId);
            lObjChangesData.setDcpsEmpId(lLngEmpID);
            lObjChangesData.setTypeOfChanges("NomineeDetails");
            lObjChangesFormDAO.update(lObjChangesData);

            for(lInt = 0; lInt < lObjTrnDcpsChangesList.size(); lInt = lInt + 1) {
               lLongChangesIdpK = IFMSCommonServiceImpl.getNextSeqNum("TRN_DCPS_CHANGES", inputMap);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setDcpsChangesIdPk(lLongChangesIdpK);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setDcpsChangesId(lLongDcpsHstChangesId);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setDcpsEmpId(lLngEmpID);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setTypeOfChanges("NomineeDetails");
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setFieldName("changesNomineeRefId");
               Long HighestRefId = lObjChangesFormDAO.getNextRefIdForHstNomineeChanges(lLngEmpID) - 1L;
               Long NextRefId = HighestRefId + 1L;
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setOldValue(HighestRefId.toString());
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setNewValue(NextRefId.toString());
               lObjChangesFormDAO.create(lObjTrnDcpsChangesList.get(lInt));
            }
         }

         HstDcpsNomineeChanges[] lArrHstDcpsNomineeChanges = (HstDcpsNomineeChanges[])inputMap.get("lArrHstDcpsNomineeChanges");
         HstDcpsNomineeChanges[] lArrDcpsNomineesFromMst = (HstDcpsNomineeChanges[])inputMap.get("lArrDcpsNomineesFromMst");
         Integer lInt;
         if (lLongDcpsHstChangesId == null) {
            for(lInt = 0; lInt < lArrDcpsNomineesFromMst.length; lInt = lInt + 1) {
               dcpsNomineeChangesId = IFMSCommonServiceImpl.getNextSeqNum("HST_DCPS_NOMINEE_CHANGES", inputMap);
               lArrDcpsNomineesFromMst[lInt].setDcpsNomineeChangesId(dcpsNomineeChangesId);
               lArrDcpsNomineesFromMst[lInt].setDcpsChangesId(lLongChangesId);
               lArrDcpsNomineesFromMst[lInt].setChangesNomineeRefId(lLongChangesNomineeRefId);
               lArrDcpsNomineesFromMst[lInt].setDcpsEmpId(lLngEmpID);
               this.gLogger.info("empid :" + lArrDcpsNomineesFromMst[lInt].getDcpsEmpId());
               lObjChangesFormDAO.create(lArrDcpsNomineesFromMst[lInt]);
            }

            for(lInt = 0; lInt < lArrHstDcpsNomineeChanges.length; lInt = lInt + 1) {
               dcpsNomineeChangesId = IFMSCommonServiceImpl.getNextSeqNum("HST_DCPS_NOMINEE_CHANGES", inputMap);
               lArrHstDcpsNomineeChanges[lInt].setDcpsNomineeChangesId(dcpsNomineeChangesId);
               lArrHstDcpsNomineeChanges[lInt].setDcpsChangesId(lLongChangesId);
               lArrHstDcpsNomineeChanges[lInt].setChangesNomineeRefId(lLongChangesNomineeRefIdForChanges);
               lArrHstDcpsNomineeChanges[lInt].setDcpsEmpId(lLngEmpID);
               this.gLogger.info("emp_id :" + lArrHstDcpsNomineeChanges[lInt].getDcpsEmpId());
               lObjChangesFormDAO.create(lArrHstDcpsNomineeChanges[lInt]);
            }
         }

         this.gLogger.info("Dcps :" + dcpsNomineeChangesId);
         if (lLongDcpsHstChangesId != null) {
            for(lInt = 0; lInt < lArrHstDcpsNomineeChanges.length; lInt = lInt + 1) {
               dcpsNomineeChangesId = IFMSCommonServiceImpl.getNextSeqNum("HST_DCPS_NOMINEE_CHANGES", inputMap);
               lArrHstDcpsNomineeChanges[lInt].setDcpsNomineeChangesId(dcpsNomineeChangesId);
               lArrHstDcpsNomineeChanges[lInt].setDcpsChangesId(lLongDcpsHstChangesId);
               lArrHstDcpsNomineeChanges[lInt].setChangesNomineeRefId(lLongChangesNomineeRefId);
               lArrHstDcpsNomineeChanges[lInt].setDcpsEmpId(lLngEmpID);
               this.gLogger.info("emp_id :" + lArrHstDcpsNomineeChanges[lInt].getDcpsEmpId());
               this.gLogger.info(lArrHstDcpsNomineeChanges[lInt].getCreatedDate());
               this.gLogger.info(lArrHstDcpsNomineeChanges[lInt].getCreatedPostId());
               lObjChangesFormDAO.create(lArrHstDcpsNomineeChanges[lInt]);
            }
         }

         lBlFlag = true;
      } catch (Exception var18) {
         resObj.setResultValue((Object)null);
         resObj.setThrowable(var18);
         resObj.setResultCode(-1);
         resObj.setViewName("errorPage");
         this.gLogger.error(" Error is : " + var18, var18);
      }

      String lSBStatus = this.getResponseXMLDocUpdtOrFwd(lBlFlag, lObjChangesData.getDcpsChangesId()).toString();
      lStrResult = (new AjaxXmlBuilder()).addItem("ajax_key", lSBStatus).toString();
      inputMap.put("ajaxKey", lStrResult);
      resObj.setResultValue(inputMap);
      resObj.setViewName("ajaxData");
      return resObj;
   }

   public ResultObject updatePhotoAndSignDtls(Map inputMap) {
      ResultObject resObj = new ResultObject(0, "FAIL");
      Boolean lBlFlag = false;
      HstDcpsChanges lObjChangesData = null;
      Long lLongDcpsHstChangesId = null;
      Long lLongChangesId = null;
      Long lLongOldPhotoAttachmentId = null;
      Long lLongOldSignAttachmentId = null;
      Long lLongNewPhotoAttachmentId = null;
      Long lLongNewSignAttachmentId = null;
      String photoAttachmentId = "";
      String signAttachmentId = "";

      String lStrResult;
      try {
         this.setSessionInfo(inputMap);
         ChangesFormDAO lObjChangesFormDAO = new ChangesFormDAOImpl(HstDcpsChanges.class, this.serv.getSessionFactory());
         lStrResult = StringUtility.getParameter("empId", this.request);
         Long lLongEmployeeId = Long.valueOf(lStrResult);
         lObjChangesData = (HstDcpsChanges)inputMap.get("lObjChangesData");
         if (!StringUtility.getParameter("dcpsHstChangesId", this.request).equalsIgnoreCase("") && StringUtility.getParameter("dcpsHstChangesId", this.request) != null) {
            lLongDcpsHstChangesId = Long.valueOf(StringUtility.getParameter("dcpsHstChangesId", this.request));
         }

         List<TrnDcpsChanges> lObjTrnDcpsChangesList = (List)inputMap.get("lObjTrnDcpsChangesList");
         if (!StringUtility.getParameter("oldPhotoAttachmentId", this.request).equals("") && StringUtility.getParameter("oldPhotoAttachmentId", this.request) != null) {
            lLongOldPhotoAttachmentId = Long.valueOf(StringUtility.getParameter("oldPhotoAttachmentId", this.request));
         }

         if (!StringUtility.getParameter("oldSignAttachmentId", this.request).equals("") && StringUtility.getParameter("oldSignAttachmentId", this.request) != null) {
            lLongOldSignAttachmentId = Long.valueOf(StringUtility.getParameter("oldSignAttachmentId", this.request));
         }

         this.serv.executeService("FILE_UPLOAD_VOGEN", inputMap);
         resObj = this.serv.executeService("FILE_UPLOAD_SRVC", inputMap);
         Map attachMap = (Map)resObj.getResultValue();
         if (attachMap.get("AttachmentId_Photo") != null) {
            lLongNewPhotoAttachmentId = Long.parseLong(String.valueOf(attachMap.get("AttachmentId_Photo")));
            photoAttachmentId = Long.toString(lLongNewPhotoAttachmentId);
            this.gLogger.info("photoAttachmentId is " + photoAttachmentId);
         }

         if (attachMap.get("AttachmentId_Sign") != null) {
            lLongNewSignAttachmentId = Long.parseLong(String.valueOf(attachMap.get("AttachmentId_Sign")));
            signAttachmentId = Long.toString(lLongNewSignAttachmentId);
            this.gLogger.info("signAttachmentId is " + signAttachmentId);
         }

         Integer lInt;
         Long lLongChangesIdpK;
         if (lLongDcpsHstChangesId == null) {
            lLongChangesId = IFMSCommonServiceImpl.getNextSeqNum("HST_DCPS_CHANGES", inputMap);
            lObjChangesData.setDcpsChangesId(lLongChangesId);
            lObjChangesData.setDcpsEmpId(lLongEmployeeId);
            lObjChangesData.setTypeOfChanges("PhotoAndSignDetails");
            lObjChangesFormDAO.create(lObjChangesData);

            for(lInt = 0; lInt < lObjTrnDcpsChangesList.size(); lInt = lInt + 1) {
               lLongChangesIdpK = IFMSCommonServiceImpl.getNextSeqNum("TRN_DCPS_CHANGES", inputMap);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setDcpsChangesIdPk(lLongChangesIdpK);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setDcpsChangesId(lLongChangesId);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setDcpsEmpId(lLongEmployeeId);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setTypeOfChanges("PhotoAndSignDetails");
               if (lInt == 0) {
                  ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setFieldName("PhotoId");
                  if (lLongOldPhotoAttachmentId != null) {
                     ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setOldValue(lLongOldPhotoAttachmentId.toString());
                  } else {
                     ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setOldValue("");
                  }

                  if (lLongNewPhotoAttachmentId != null) {
                     ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setNewValue(lLongNewPhotoAttachmentId.toString());
                  } else {
                     ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setNewValue("");
                  }
               }

               if (lInt == 1) {
                  ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setFieldName("SignatureId");
                  if (lLongOldSignAttachmentId != null) {
                     ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setOldValue(lLongOldSignAttachmentId.toString());
                  } else {
                     ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setOldValue("");
                  }

                  if (lLongNewSignAttachmentId != null) {
                     ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setNewValue(lLongNewSignAttachmentId.toString());
                  } else {
                     ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setNewValue("");
                  }
               }

               lObjChangesFormDAO.create(lObjTrnDcpsChangesList.get(lInt));
            }
         }

         if (lLongDcpsHstChangesId != null) {
            lObjChangesData.setDcpsChangesId(lLongDcpsHstChangesId);
            lObjChangesData.setDcpsEmpId(lLongEmployeeId);
            lObjChangesData.setTypeOfChanges("PhotoAndSignDetails");
            lObjChangesFormDAO.update(lObjChangesData);
            lObjChangesFormDAO.deleteTrnVOForDcpsChangesId(lLongDcpsHstChangesId);

            for(lInt = 0; lInt < lObjTrnDcpsChangesList.size(); lInt = lInt + 1) {
               lLongChangesIdpK = IFMSCommonServiceImpl.getNextSeqNum("TRN_DCPS_CHANGES", inputMap);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setDcpsChangesIdPk(lLongChangesIdpK);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setDcpsChangesId(lLongDcpsHstChangesId);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setDcpsEmpId(lLongEmployeeId);
               ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setTypeOfChanges("PhotoAndSignDetails");
               if (lInt == 0) {
                  ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setFieldName("PhotoId");
                  if (lLongOldPhotoAttachmentId != null) {
                     ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setOldValue(lLongOldPhotoAttachmentId.toString());
                  } else {
                     ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setOldValue("");
                  }

                  if (lLongNewPhotoAttachmentId != null) {
                     ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setNewValue(lLongNewPhotoAttachmentId.toString());
                  } else {
                     ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setNewValue("");
                  }
               }

               if (lInt == 1) {
                  ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setFieldName("SignatureId");
                  if (lLongOldSignAttachmentId != null) {
                     ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setOldValue(lLongOldSignAttachmentId.toString());
                  } else {
                     ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setOldValue("");
                  }

                  if (lLongNewSignAttachmentId != null) {
                     ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setNewValue(lLongNewSignAttachmentId.toString());
                  } else {
                     ((TrnDcpsChanges)lObjTrnDcpsChangesList.get(lInt)).setNewValue("");
                  }
               }

               lObjChangesFormDAO.create(lObjTrnDcpsChangesList.get(lInt));
            }
         }

         lBlFlag = true;
      } catch (Exception var20) {
         resObj.setResultValue((Object)null);
         resObj.setThrowable(var20);
         resObj.setResultCode(-1);
         resObj.setViewName("errorPage");
         this.gLogger.error(" Error is : " + var20, var20);
      }

      String lSBStatus = this.getResponseXMLDocUpdtOrFwdforAttachment(lBlFlag, lObjChangesData.getDcpsChangesId(), photoAttachmentId, signAttachmentId).toString();
      lStrResult = (new AjaxXmlBuilder()).addItem("ajax_key", lSBStatus).toString();
      inputMap.put("ajaxKey", lStrResult);
      resObj.setResultValue(inputMap);
      resObj.setViewName("ajaxData");
      return resObj;
   }

   public ResultObject forwardChangesToDDO(Map objectArgs) {
      ResultObject resObj = new ResultObject(0, "FAIL");
      HstDcpsChanges lObjHstDcpsChanges = null;

      Boolean lBlFlag;
      String lStrResult;
      try {
         this.setSessionInfo(objectArgs);
         lBlFlag = false;
         ChangesFormDAO lObjChangesFormDAO = new ChangesFormDAOImpl(HstDcpsChanges.class, this.serv.getSessionFactory());
         lStrResult = StringUtility.getParameter("ForwardToPost", this.request).toString();
         String toLevel = this.gObjRsrcBndle.getString("DCPS.DDO");
         objectArgs.put("toPost", lStrResult);
         objectArgs.put("toPostId", lStrResult);
         objectArgs.put("toLevel", toLevel);
         objectArgs.put("jobTitle", this.gObjRsrcBndle.getString("DCPS.ChangesForm"));
         objectArgs.put("Docid", Long.parseLong(this.gObjRsrcBndle.getString("DCPS.ChangesFormID")));
         Long lLongDcpsChangesId = Long.valueOf(StringUtility.getParameter("dcpsChangesId", this.request));
         objectArgs.put("Pkvalue", lLongDcpsChangesId);
         this.createWF(objectArgs);
         WorkFlowDelegate.forward(objectArgs);
         new HstDcpsChanges();
         lObjHstDcpsChanges = (HstDcpsChanges)lObjChangesFormDAO.read(lLongDcpsChangesId);
         lObjHstDcpsChanges.setFormStatus(0L);
         lObjChangesFormDAO.update(lObjHstDcpsChanges);
         List<TrnDcpsChanges> TrnDcpsChangesList = lObjChangesFormDAO.getChangesFromTrnForChangesId(lObjHstDcpsChanges.getDcpsChangesId());
         Integer lInt = 0;

         while(true) {
            if (lInt >= TrnDcpsChangesList.size()) {
               lBlFlag = true;
               break;
            }

            ((TrnDcpsChanges)TrnDcpsChangesList.get(lInt)).setFormStatus(0L);
            lObjChangesFormDAO.update(TrnDcpsChangesList.get(lInt));
            lInt = lInt + 1;
         }
      } catch (Exception var11) {
         this.gLogger.error(" Error is : " + var11, var11);
         resObj.setResultValue((Object)null);
         resObj.setThrowable(var11);
         resObj.setResultCode(-1);
         resObj.setViewName("errorPage");
         return resObj;
      }

      String lSBStatus = this.getResponseXMLDoc(lBlFlag).toString();
      lStrResult = (new AjaxXmlBuilder()).addItem("ajax_key", lSBStatus).toString();
      objectArgs.put("ajaxKey", lStrResult);
      resObj.setResultValue(objectArgs);
      resObj.setViewName("ajaxData");
      return resObj;
   }

   public ResultObject rejectChangesToDDOAsst(Map objectArgs) {
      ResultObject resObj = new ResultObject(0, "FAIL");

      String lSBStatus;
      try {
         this.setSessionInfo(objectArgs);
         String strPKValue = StringUtility.getParameter("dcpsChangesId", this.request).toString().trim();
         lSBStatus = StringUtility.getParameter("sentBackRemarks", this.request).toString().trim();
         objectArgs.put("FromPostId", this.gStrPostId);
         objectArgs.put("SendNotification", lSBStatus);
         objectArgs.put("jobTitle", this.gObjRsrcBndle.getString("DCPS.ChangesForm"));
         objectArgs.put("Docid", Long.parseLong(this.gObjRsrcBndle.getString("DCPS.ChangesFormID")));
         objectArgs.put("Pkvalue", strPKValue);
         WorkFlowDelegate.returnDoc(objectArgs);
         ChangesFormDAO lObjChangesFormDAO = new ChangesFormDAOImpl(HstDcpsChanges.class, this.serv.getSessionFactory());
         Long lLongPKValue = Long.parseLong(strPKValue);
         HstDcpsChanges lObjHstDcpsChanges = (HstDcpsChanges)lObjChangesFormDAO.read(lLongPKValue);
         lObjHstDcpsChanges.setFormStatus(-1L);
         lObjHstDcpsChanges.setUpdatedUserId(this.gLngUserId);
         lObjHstDcpsChanges.setUpdatedPostId(this.gLngPostId);
         lObjHstDcpsChanges.setUpdatedDate(this.gDtCurrDt);
         lObjHstDcpsChanges.setSentBackRemarks(lSBStatus);
         lObjChangesFormDAO.update(lObjHstDcpsChanges);
      } catch (Exception var8) {
         resObj.setResultValue((Object)null);
         resObj.setThrowable(var8);
         resObj.setResultCode(-1);
         resObj.setViewName("errorPage");
         var8.printStackTrace();
         return resObj;
      }

      lSBStatus = this.getResponseXMLDoc(true).toString();
      String lStrResult = (new AjaxXmlBuilder()).addItem("ajax_key", lSBStatus).toString();
      objectArgs.put("ajaxKey", lStrResult);
      resObj.setViewName("ajaxData");
      resObj.setResultValue(objectArgs);
      return resObj;
   }

   public ResultObject approveChangesByDDO(Map objectArgs) {
      ResultObject resObj = new ResultObject(0, "FAIL");
      Boolean lBlFlag = null;
      HstDcpsChanges lObjHstDcpsChanges = null;

      try {
         this.setSessionInfo(objectArgs);
         lBlFlag = false;
         ChangesFormDAO lObjChangesFormDAO = new ChangesFormDAOImpl(HstDcpsChanges.class, this.serv.getSessionFactory());
         Long lLongDcpsChangesId = Long.valueOf(StringUtility.getParameter("dcpsChangesId", this.request).toString().trim());
         lObjHstDcpsChanges = (HstDcpsChanges)lObjChangesFormDAO.read(lLongDcpsChangesId);
         if (lObjHstDcpsChanges.getTypeOfChanges().equals("PersonalDetails")) {
            this.ExchangePersonalDetailsBtnMstAndHst(lObjHstDcpsChanges, objectArgs);
            this.gLogger.info("************Values Saved*************");
         }

         if (lObjHstDcpsChanges.getTypeOfChanges().equals("OfficeDetails")) {
            this.ExchangeOfficeDetailsBtnMstAndHst(lObjHstDcpsChanges, objectArgs);
         }

         if (lObjHstDcpsChanges.getTypeOfChanges().equals("OtherDetails")) {
            this.ExchangeOtherDetailsBtnMstAndHst(lObjHstDcpsChanges, objectArgs);
         }

         if (lObjHstDcpsChanges.getTypeOfChanges().equals("NomineeDetails")) {
            this.ExchangeNomineeDetailsBtnMstAndHst(lObjHstDcpsChanges, objectArgs);
         }

         if (lObjHstDcpsChanges.getTypeOfChanges().equals("PhotoAndSignDetails")) {
            this.ExchangePhotoAndSignDetailsBtnMstAndHst(lObjHstDcpsChanges);
         }

         List<TrnDcpsChanges> TrnDcpsChangesList = lObjChangesFormDAO.getChangesFromTrnForChangesId(lObjHstDcpsChanges.getDcpsChangesId());
         lObjHstDcpsChanges.setFormStatus(1L);
         lObjChangesFormDAO.update(lObjHstDcpsChanges);
         Integer lInt = 0;

         while(true) {
            if (lInt >= TrnDcpsChangesList.size()) {
               lBlFlag = true;
               break;
            }

            ((TrnDcpsChanges)TrnDcpsChangesList.get(lInt)).setFormStatus(1L);
            lObjChangesFormDAO.update(TrnDcpsChangesList.get(lInt));
            lInt = lInt + 1;
         }
      } catch (Exception var9) {
         this.gLogger.error(" Error is : " + var9, var9);
         resObj.setResultValue((Object)null);
         resObj.setThrowable(var9);
         resObj.setResultCode(-1);
         resObj.setViewName("errorPage");
         return resObj;
      }

      String lSBStatus = this.getResponseXMLDoc(lBlFlag).toString();
      String lStrResult = (new AjaxXmlBuilder()).addItem("ajax_key", lSBStatus).toString();
      objectArgs.put("ajaxKey", lStrResult);
      resObj.setViewName("ajaxData");
      resObj.setResultValue(objectArgs);
      return resObj;
   }

   private void createWF(Map inputMap) {
      try {
         this.setSessionInfo(inputMap);
         Long PKValue = Long.parseLong(inputMap.get("Pkvalue").toString());
         String subjectName = this.gObjRsrcBndle.getString("DCPS.ChangesForm");
         String lStrPostId = SessionHelper.getPostId(inputMap).toString();
         Long lLngHierRefId = WorkFlowHelper.getHierarchyByPostIDAndDescription(lStrPostId, subjectName, inputMap);
         inputMap.put("Hierarchy_ref_id", lLngHierRefId);
         inputMap.put("Docid", Long.parseLong(this.gObjRsrcBndle.getString("DCPS.ChangesFormID")));
         inputMap.put("Pkvalue", PKValue);
         inputMap.put("DisplayJobTitle", this.gObjRsrcBndle.getString("DCPS.ChangesForm"));
         WorkFlowDelegate.create(inputMap);
      } catch (Exception var6) {
         this.gLogger.error(" Error is : " + var6, var6);
      }

   }

   private List getHierarchyUsers(Map inputMap, String lStrUser) {
      List UserList = null;
      String subjectName = null;

      try {
         this.setSessionInfo(inputMap);
         Integer llFromLevelId = 0;
         UserList = new ArrayList();
         if (lStrUser.equals("DDOAsst")) {
            subjectName = this.gObjRsrcBndle.getString("DCPS.ChangesForm");
         }

         Long lLngHierRefId = WorkFlowHelper.getHierarchyByPostIDAndDescription(this.gStrPostId, subjectName, inputMap);
         llFromLevelId = WorkFlowHelper.getLevelFromPostMpg(this.gStrPostId, lLngHierRefId, inputMap);
         List rsltList = WorkFlowHelper.getUpperPost(this.gStrPostId, lLngHierRefId, llFromLevelId, inputMap);
         Object[] lObjNextPost = null;

         for(Integer lInt = 0; lInt < rsltList.size(); lInt = lInt + 1) {
            lObjNextPost = (Object[])rsltList.get(lInt);
            if (!lObjNextPost.equals((Object)null)) {
               UserList.add(lObjNextPost[0].toString());
            }
         }
      } catch (Exception var10) {
         this.gLogger.error(" Error is : " + var10, var10);
      }

      return UserList;
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

   private StringBuilder getResponseXMLDocUpdtOrFwd(Boolean lBlFlag, Long lLngDcpsChangesId) {
      StringBuilder lStrBldXML = new StringBuilder();
      lStrBldXML.append("<XMLDOC>");
      lStrBldXML.append("  <lBlFlag>");
      lStrBldXML.append(lBlFlag);
      lStrBldXML.append("  </lBlFlag>");
      lStrBldXML.append("  <lLngDcpsChangesId>");
      lStrBldXML.append(lLngDcpsChangesId);
      lStrBldXML.append("  </lLngDcpsChangesId>");
      lStrBldXML.append("</XMLDOC>");
      return lStrBldXML;
   }

   private StringBuilder getResponseXMLDocUpdtOrFwdforAttachment(Boolean lBlFlag, Long lLngDcpsChangesId, String photoAttachmentId, String signAttachmentId) {
      StringBuilder lStrBldXML = new StringBuilder();
      lStrBldXML.append("<XMLDOC>");
      lStrBldXML.append("  <lBlFlag>");
      lStrBldXML.append(lBlFlag);
      lStrBldXML.append("  </lBlFlag>");
      lStrBldXML.append("  <lLngDcpsChangesId>");
      lStrBldXML.append(lLngDcpsChangesId);
      lStrBldXML.append("  </lLngDcpsChangesId>");
      lStrBldXML.append("<PhotoAttachmentId>");
      lStrBldXML.append(photoAttachmentId);
      lStrBldXML.append("</PhotoAttachmentId>");
      lStrBldXML.append("<SignAttachmentId>");
      lStrBldXML.append(signAttachmentId);
      lStrBldXML.append("</SignAttachmentId>");
      lStrBldXML.append("</XMLDOC>");
      return lStrBldXML;
   }

   private void ExchangePersonalDetailsBtnMstAndHst(HstDcpsChanges lObjHstDcpsChanges, Map objectArgs) throws Exception {
      HstDcpsPersonalChanges lObjHstDcpsPersonalChanges = null;
      HstDcpsPersonalChanges lObjTempHstDcpsPersonalChanges = null;
      MstEmp lObjMstEmp = null;
      Long dcpsPersonalChangesIdPk = null;
      RltDcpsPayrollEmp lObjRltDcpsPayrollEmp = null;
      ChangesFormDAO lObjPersonalChangesFormDAO = new ChangesFormDAOImpl(HstDcpsPersonalChanges.class, this.serv.getSessionFactory());
      dcpsPersonalChangesIdPk = lObjPersonalChangesFormDAO.getPersonalChangesIdforChangesId(lObjHstDcpsChanges.getDcpsChangesId());
      lObjHstDcpsPersonalChanges = (HstDcpsPersonalChanges)lObjPersonalChangesFormDAO.read(dcpsPersonalChangesIdPk);
      NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class, this.serv.getSessionFactory());
      lObjMstEmp = (MstEmp)lObjNewRegDdoDAO.read(lObjHstDcpsChanges.getDcpsEmpId());
      lObjRltDcpsPayrollEmp = lObjNewRegDdoDAO.getPayrollVOForEmpId(lObjHstDcpsChanges.getDcpsEmpId());
      lObjTempHstDcpsPersonalChanges = (HstDcpsPersonalChanges)lObjHstDcpsPersonalChanges.clone();
      lObjHstDcpsPersonalChanges.setBuilding_address(lObjMstEmp.getBuilding_address());
      lObjHstDcpsPersonalChanges.setBuilding_street(lObjMstEmp.getBuilding_street());
      lObjHstDcpsPersonalChanges.setCellNo(lObjMstEmp.getCellNo());
      lObjHstDcpsPersonalChanges.setCntctNo(lObjMstEmp.getCntctNo());
      lObjHstDcpsPersonalChanges.setDistrict(lObjMstEmp.getDistrict());
      lObjHstDcpsPersonalChanges.setDob(lObjMstEmp.getDob());
      lObjHstDcpsPersonalChanges.setDoj(lObjMstEmp.getDoj());
      lObjHstDcpsPersonalChanges.setPhychallanged(lObjRltDcpsPayrollEmp.getPhychallanged());
      lObjHstDcpsPersonalChanges.setEmailId(lObjMstEmp.getEmailId());
      lObjHstDcpsPersonalChanges.setFather_or_husband(lObjMstEmp.getFather_or_husband());
      lObjHstDcpsPersonalChanges.setMotherName(lObjMstEmp.getMotherName());
      lObjHstDcpsPersonalChanges.setSpouseName(lObjMstEmp.getSpouseName());
      lObjHstDcpsPersonalChanges.setGender(lObjMstEmp.getGender());
      lObjHstDcpsPersonalChanges.setLandmark(lObjMstEmp.getLandmark());
      lObjHstDcpsPersonalChanges.setLocality(lObjMstEmp.getLocality());
      lObjHstDcpsPersonalChanges.setName(lObjMstEmp.getName());
      lObjHstDcpsPersonalChanges.setName_marathi(lObjMstEmp.getName_marathi());
      lObjHstDcpsPersonalChanges.setPANNo(lObjMstEmp.getPANNo());
      lObjHstDcpsPersonalChanges.setPincode(lObjMstEmp.getPincode());
      lObjHstDcpsPersonalChanges.setSalutation(lObjMstEmp.getSalutation());
      lObjHstDcpsPersonalChanges.setState(lObjMstEmp.getState());
      lObjHstDcpsPersonalChanges.setUIDNo(lObjMstEmp.getUIDNo());
      lObjHstDcpsPersonalChanges.setEIDNo(lObjMstEmp.getEIDNo());
      lObjHstDcpsPersonalChanges.setUpdatedDate(this.gDtCurDate);
      lObjHstDcpsPersonalChanges.setUpdatedPostId(this.gLngPostId);
      lObjHstDcpsPersonalChanges.setUpdatedUserId(this.gLngUserId);
      lObjHstDcpsPersonalChanges.setPBuildingAddress(lObjMstEmp.getPBuildingAddress());
      lObjHstDcpsPersonalChanges.setPBuildingStreet(lObjMstEmp.getPBuildingStreet());
      lObjHstDcpsPersonalChanges.setPAddressVTC(lObjMstEmp.getPAddressVTC());
      lObjHstDcpsPersonalChanges.setAddressVTC(lObjMstEmp.getAddressVTC());
      lObjHstDcpsPersonalChanges.setPCellNo(lObjMstEmp.getPCellNo());
      lObjHstDcpsPersonalChanges.setPCntctNo(lObjMstEmp.getPCntctNo());
      lObjHstDcpsPersonalChanges.setPDistrict(lObjMstEmp.getPDistrict());
      lObjHstDcpsPersonalChanges.setPLandmark(lObjMstEmp.getPLandmark());
      lObjHstDcpsPersonalChanges.setPLocality(lObjMstEmp.getPLocality());
      lObjHstDcpsPersonalChanges.setPPincode(lObjMstEmp.getPPincode());
      lObjHstDcpsPersonalChanges.setPState(lObjMstEmp.getPState());
      lObjHstDcpsPersonalChanges.setIsAddressSame(lObjMstEmp.getIsAddressSame());
      lObjPersonalChangesFormDAO.update(lObjHstDcpsPersonalChanges);
      if (lObjTempHstDcpsPersonalChanges.getBuilding_address() != null) {
         lObjMstEmp.setBuilding_address(lObjTempHstDcpsPersonalChanges.getBuilding_address());
      }

      if (lObjTempHstDcpsPersonalChanges.getBuilding_street() != null) {
         lObjMstEmp.setBuilding_street(lObjTempHstDcpsPersonalChanges.getBuilding_street());
      }

      if (lObjTempHstDcpsPersonalChanges.getCellNo() != null) {
         lObjMstEmp.setCellNo(lObjTempHstDcpsPersonalChanges.getCellNo());
      }

      if (lObjTempHstDcpsPersonalChanges.getCntctNo() != null) {
         lObjMstEmp.setCntctNo(lObjTempHstDcpsPersonalChanges.getCntctNo());
      }

      if (lObjTempHstDcpsPersonalChanges.getDistrict() != null) {
         lObjMstEmp.setDistrict(lObjTempHstDcpsPersonalChanges.getDistrict());
      }

      if (lObjTempHstDcpsPersonalChanges.getDob() != null) {
         lObjMstEmp.setDob(lObjTempHstDcpsPersonalChanges.getDob());
      }

      if (lObjTempHstDcpsPersonalChanges.getDoj() != null) {
         lObjMstEmp.setDoj(lObjTempHstDcpsPersonalChanges.getDoj());
      }

      if (lObjTempHstDcpsPersonalChanges.getEmailId() != null) {
         lObjMstEmp.setEmailId(lObjTempHstDcpsPersonalChanges.getEmailId());
      }

      if (lObjTempHstDcpsPersonalChanges.getFather_or_husband() != null) {
         lObjMstEmp.setFather_or_husband(lObjTempHstDcpsPersonalChanges.getFather_or_husband());
      }

      if (lObjTempHstDcpsPersonalChanges.getMotherName() != null) {
         lObjMstEmp.setMotherName(lObjTempHstDcpsPersonalChanges.getMotherName());
      }

      if (lObjTempHstDcpsPersonalChanges.getSpouseName() != null) {
         lObjMstEmp.setSpouseName(lObjTempHstDcpsPersonalChanges.getSpouseName());
         this.gLogger.info("setting spouse Name :" + lObjMstEmp.getSpouseName());
      }

      if (lObjTempHstDcpsPersonalChanges.getAddressVTC() != null) {
         lObjMstEmp.setAddressVTC(lObjTempHstDcpsPersonalChanges.getAddressVTC());
         this.gLogger.info("setting AddressVTC :" + lObjMstEmp.getAddressVTC());
      }

      if (lObjTempHstDcpsPersonalChanges.getGender() != null) {
         lObjMstEmp.setGender(lObjTempHstDcpsPersonalChanges.getGender());
      }

      if (lObjTempHstDcpsPersonalChanges.getLandmark() != null) {
         lObjMstEmp.setLandmark(lObjTempHstDcpsPersonalChanges.getLandmark());
      }

      if (lObjTempHstDcpsPersonalChanges.getLocality() != null) {
         lObjMstEmp.setLocality(lObjTempHstDcpsPersonalChanges.getLocality());
      }

      if (lObjTempHstDcpsPersonalChanges.getName() != null) {
         lObjMstEmp.setName(lObjTempHstDcpsPersonalChanges.getName());
      }

      if (lObjTempHstDcpsPersonalChanges.getName_marathi() != null) {
         lObjMstEmp.setName_marathi(lObjTempHstDcpsPersonalChanges.getName_marathi());
      }

      if (lObjTempHstDcpsPersonalChanges.getPANNo() != null) {
         lObjMstEmp.setPANNo(lObjTempHstDcpsPersonalChanges.getPANNo());
      }

      if (lObjTempHstDcpsPersonalChanges.getPincode() != null) {
         lObjMstEmp.setPincode(lObjTempHstDcpsPersonalChanges.getPincode());
      }

      if (lObjTempHstDcpsPersonalChanges.getSalutation() != null) {
         lObjMstEmp.setSalutation(lObjTempHstDcpsPersonalChanges.getSalutation());
      }

      if (lObjTempHstDcpsPersonalChanges.getState() != null) {
         lObjMstEmp.setState(lObjTempHstDcpsPersonalChanges.getState());
      }

      if (lObjTempHstDcpsPersonalChanges.getUIDNo() != null) {
         lObjMstEmp.setUIDNo(lObjTempHstDcpsPersonalChanges.getUIDNo());
      }

      if (lObjTempHstDcpsPersonalChanges.getEIDNo() != null) {
         lObjMstEmp.setEIDNo(lObjTempHstDcpsPersonalChanges.getEIDNo());
      }

      lObjMstEmp.setUpdatedDate(this.gDtCurDate);
      lObjMstEmp.setUpdatedPostId(this.gLngPostId);
      lObjMstEmp.setUpdatedUserId(this.gLngUserId);
      if (lObjTempHstDcpsPersonalChanges.getPhychallanged() != null) {
         lObjRltDcpsPayrollEmp.setPhychallanged(lObjTempHstDcpsPersonalChanges.getPhychallanged());
      }

      if (lObjTempHstDcpsPersonalChanges.getPBuildingAddress() != null) {
         lObjMstEmp.setPBuildingAddress(lObjTempHstDcpsPersonalChanges.getPBuildingAddress());
      }

      if (lObjTempHstDcpsPersonalChanges.getPBuildingStreet() != null) {
         lObjMstEmp.setPBuildingStreet(lObjTempHstDcpsPersonalChanges.getPBuildingStreet());
      }

      if (lObjTempHstDcpsPersonalChanges.getPAddressVTC() != null) {
         lObjMstEmp.setPAddressVTC(lObjTempHstDcpsPersonalChanges.getPAddressVTC());
      }

      if (lObjTempHstDcpsPersonalChanges.getPCellNo() != null) {
         lObjMstEmp.setPCellNo(lObjTempHstDcpsPersonalChanges.getPCellNo());
      }

      if (lObjTempHstDcpsPersonalChanges.getPCntctNo() != null) {
         lObjMstEmp.setPCntctNo(lObjTempHstDcpsPersonalChanges.getPCntctNo());
      }

      if (lObjTempHstDcpsPersonalChanges.getPDistrict() != null) {
         lObjMstEmp.setPDistrict(lObjTempHstDcpsPersonalChanges.getPDistrict());
      }

      if (lObjTempHstDcpsPersonalChanges.getPLandmark() != null) {
         lObjMstEmp.setPLandmark(lObjTempHstDcpsPersonalChanges.getPLandmark());
      }

      if (lObjTempHstDcpsPersonalChanges.getPLocality() != null) {
         lObjMstEmp.setPLocality(lObjTempHstDcpsPersonalChanges.getPLocality());
      }

      if (lObjTempHstDcpsPersonalChanges.getPPincode() != null) {
         lObjMstEmp.setPPincode(lObjTempHstDcpsPersonalChanges.getPPincode());
      }

      if (lObjTempHstDcpsPersonalChanges.getPState() != null) {
         lObjMstEmp.setPState(lObjTempHstDcpsPersonalChanges.getPState());
      }

      if (lObjTempHstDcpsPersonalChanges.getIsAddressSame() != null) {
         lObjMstEmp.setIsAddressSame(lObjTempHstDcpsPersonalChanges.getIsAddressSame());
      }

      ChangesFormDAOImpl lObjChangesDAO1 = new ChangesFormDAOImpl((Class)null, this.serv.getSessionFactory());
      MstEmp mstEmp = lObjNewRegDdoDAO.getEmpVOForEmpId(lObjTempHstDcpsPersonalChanges.getDcpsEmpId());
      String empSevarthId = mstEmp.getSevarthId().toString();
      List EmplistForPanNo = lObjChangesDAO1.getdetailstoUpdatepanno(empSevarthId);
      this.gLogger.error("in if------" + EmplistForPanNo.size());
      if (EmplistForPanNo != null && !EmplistForPanNo.isEmpty()) {
         this.gLogger.error("in if------" + EmplistForPanNo.size());
         lObjChangesDAO1.UpdatePanNoDcpsEmp(empSevarthId, lObjTempHstDcpsPersonalChanges.getPANNo());
      }

      this.gLogger.error("in if------" + EmplistForPanNo);
      objectArgs.put("ChangedPersonalVO", lObjTempHstDcpsPersonalChanges);
      this.serv.executeService("changePersonalDtlsInPayroll", objectArgs);
      lObjPersonalChangesFormDAO.update(lObjRltDcpsPayrollEmp);
      lObjPersonalChangesFormDAO.update(lObjMstEmp);
   }

   private void ExchangeOfficeDetailsBtnMstAndHst(HstDcpsChanges lObjHstDcpsChanges, Map objectArgs) throws Exception {
      HstDcpsOfficeChanges lObjHstDcpsOfficeChanges = null;
      HstDcpsOfficeChanges lObjTempHstDcpsOfficeChanges = null;
      MstEmp lObjMstEmp = null;
      RltDcpsPayrollEmp lObjRltDcpsPayrollEmp = null;
      Long dcpsOfficeChangesIdPk = null;
      ChangesFormDAO lObjOfficeChangesFormDAO = new ChangesFormDAOImpl(HstDcpsOfficeChanges.class, this.serv.getSessionFactory());
      dcpsOfficeChangesIdPk = lObjOfficeChangesFormDAO.getOfficeChangesIdforChangesId(lObjHstDcpsChanges.getDcpsChangesId());
      lObjHstDcpsOfficeChanges = (HstDcpsOfficeChanges)lObjOfficeChangesFormDAO.read(dcpsOfficeChangesIdPk);
      NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class, this.serv.getSessionFactory());
      lObjMstEmp = (MstEmp)lObjNewRegDdoDAO.read(lObjHstDcpsChanges.getDcpsEmpId());
      lObjRltDcpsPayrollEmp = lObjNewRegDdoDAO.getPayrollVOForEmpId(lObjHstDcpsChanges.getDcpsEmpId());
      lObjTempHstDcpsOfficeChanges = (HstDcpsOfficeChanges)lObjHstDcpsOfficeChanges.clone();
      lObjHstDcpsOfficeChanges.setParentDept(lObjMstEmp.getParentDept());
      lObjHstDcpsOfficeChanges.setCadre(lObjMstEmp.getCadre());
      lObjHstDcpsOfficeChanges.setGroup(lObjMstEmp.getGroup());
      lObjHstDcpsOfficeChanges.setCurrOff(lObjMstEmp.getCurrOff());
      lObjHstDcpsOfficeChanges.setRemarks(lObjMstEmp.getRemarks());
      lObjHstDcpsOfficeChanges.setFirstDesignation(lObjMstEmp.getFirstDesignation());
      lObjHstDcpsOfficeChanges.setAppointmentDate(lObjMstEmp.getAppointmentDate());
      lObjHstDcpsOfficeChanges.setCurrPostJoiningDate(lObjRltDcpsPayrollEmp.getCurrPostJoiningDate());
      lObjHstDcpsOfficeChanges.setCurrCadreJoiningDate(lObjRltDcpsPayrollEmp.getCurrCadreJoiningDate());
      lObjHstDcpsOfficeChanges.setDesignation(lObjMstEmp.getDesignation());
      lObjHstDcpsOfficeChanges.setPayCommission(lObjMstEmp.getPayCommission());
      lObjHstDcpsOfficeChanges.setPayScale(lObjMstEmp.getPayScale());
      lObjHstDcpsOfficeChanges.setBasicPay(lObjMstEmp.getBasicPay());
      lObjHstDcpsOfficeChanges.setUpdatedDate(this.gDtCurDate);
      lObjHstDcpsOfficeChanges.setUpdatedPostId(this.gLngPostId);
      lObjHstDcpsOfficeChanges.setUpdatedUserId(this.gLngUserId);
      lObjOfficeChangesFormDAO.update(lObjHstDcpsOfficeChanges);
      String groupId = null;
      String designationToBePassed = null;
      String payScaleToBePassed = null;
      String payCommissionToBePassed = null;
      String basicToBePassed = null;
      if (lObjTempHstDcpsOfficeChanges.getDesignation() != null) {
         designationToBePassed = lObjTempHstDcpsOfficeChanges.getDesignation();
      } else {
         designationToBePassed = lObjMstEmp.getDesignation();
      }

      if (lObjTempHstDcpsOfficeChanges.getPayScale() != null) {
         payScaleToBePassed = lObjTempHstDcpsOfficeChanges.getPayScale();
      } else {
         payScaleToBePassed = lObjMstEmp.getPayScale();
      }

      if (lObjTempHstDcpsOfficeChanges.getCadre() != null) {
         if (!"".equals(lObjTempHstDcpsOfficeChanges.getCadre())) {
            groupId = lObjOfficeChangesFormDAO.getGroupIdForCadreId(Long.valueOf(lObjTempHstDcpsOfficeChanges.getCadre()));
         }
      } else if (!"".equals(lObjMstEmp.getCadre())) {
         groupId = lObjOfficeChangesFormDAO.getGroupIdForCadreId(Long.valueOf(lObjMstEmp.getCadre()));
      }

      if (lObjTempHstDcpsOfficeChanges.getPayCommission() != null) {
         payCommissionToBePassed = lObjTempHstDcpsOfficeChanges.getPayCommission();
      } else {
         payCommissionToBePassed = lObjMstEmp.getPayCommission();
      }

      if (lObjTempHstDcpsOfficeChanges.getBasicPay() != null) {
         basicToBePassed = lObjTempHstDcpsOfficeChanges.getBasicPay().toString();
      } else {
         basicToBePassed = lObjMstEmp.getBasicPay().toString();
      }

      if (lObjTempHstDcpsOfficeChanges.getParentDept() != null) {
         lObjMstEmp.setParentDept(lObjTempHstDcpsOfficeChanges.getParentDept());
      }

      if (lObjTempHstDcpsOfficeChanges.getCadre() != null) {
         lObjMstEmp.setCadre(lObjTempHstDcpsOfficeChanges.getCadre());
      }

      if (lObjTempHstDcpsOfficeChanges.getGroup() != null) {
         lObjMstEmp.setGroup(lObjTempHstDcpsOfficeChanges.getGroup());
      }

      if (lObjTempHstDcpsOfficeChanges.getCurrOff() != null) {
         lObjMstEmp.setCurrOff(lObjTempHstDcpsOfficeChanges.getCurrOff());
      }

      if (lObjTempHstDcpsOfficeChanges.getDesignation() != null) {
         lObjMstEmp.setDesignation(lObjTempHstDcpsOfficeChanges.getDesignation());
      }

      if (lObjTempHstDcpsOfficeChanges.getPayCommission() != null) {
         lObjMstEmp.setPayCommission(lObjTempHstDcpsOfficeChanges.getPayCommission());
      }

      if (lObjTempHstDcpsOfficeChanges.getRemarks() != null) {
         lObjMstEmp.setRemarks(lObjTempHstDcpsOfficeChanges.getRemarks());
      }

      if (lObjTempHstDcpsOfficeChanges.getFirstDesignation() != null) {
         lObjMstEmp.setFirstDesignation(lObjTempHstDcpsOfficeChanges.getFirstDesignation());
      }

      if (lObjTempHstDcpsOfficeChanges.getAppointmentDate() != null) {
         lObjMstEmp.setAppointmentDate(lObjTempHstDcpsOfficeChanges.getAppointmentDate());
      }

      if (lObjTempHstDcpsOfficeChanges.getPayScale() != null) {
         lObjMstEmp.setPayScale(lObjTempHstDcpsOfficeChanges.getPayScale());
      }

      if (lObjTempHstDcpsOfficeChanges.getBasicPay() != null) {
         lObjMstEmp.setBasicPay(lObjTempHstDcpsOfficeChanges.getBasicPay());
      }

      if (lObjTempHstDcpsOfficeChanges.getCurrPostJoiningDate() != null) {
         lObjRltDcpsPayrollEmp.setCurrPostJoiningDate(lObjTempHstDcpsOfficeChanges.getCurrPostJoiningDate());
      }

      if (lObjTempHstDcpsOfficeChanges.getCurrCadreJoiningDate() != null) {
         lObjRltDcpsPayrollEmp.setCurrCadreJoiningDate(lObjTempHstDcpsOfficeChanges.getCurrCadreJoiningDate());
      }

      lObjMstEmp.setUpdatedDate(this.gDtCurDate);
      lObjMstEmp.setUpdatedPostId(this.gLngPostId);
      lObjMstEmp.setUpdatedUserId(this.gLngUserId);
      lObjOfficeChangesFormDAO.update(lObjRltDcpsPayrollEmp);
      lObjOfficeChangesFormDAO.update(lObjMstEmp);
      objectArgs.put("empId", lObjTempHstDcpsOfficeChanges.getDcpsEmpId());
      objectArgs.put("dsgnId", designationToBePassed);
      objectArgs.put("scaleId", payScaleToBePassed);
      objectArgs.put("groupId", groupId);
      objectArgs.put("orderId", lObjHstDcpsChanges.getLetterNo());
      objectArgs.put("reasonForScaleChange", lObjTempHstDcpsOfficeChanges.getReasonForPSChange());
      if (lObjTempHstDcpsOfficeChanges.getOtherReasonForPSChange() != null) {
         objectArgs.put("reasonForScaleChangeOther", lObjTempHstDcpsOfficeChanges.getOtherReasonForPSChange());
      } else {
         objectArgs.put("reasonForScaleChangeOther", "");
      }

      objectArgs.put("commissionId", payCommissionToBePassed);
      objectArgs.put("WEFDate", lObjTempHstDcpsOfficeChanges.getWithEffectFromDate());
      objectArgs.put("FromDDO", "YES");
      objectArgs.put("basic", basicToBePassed);
      if (lObjTempHstDcpsOfficeChanges.getPayScale() != null) {
         ResultObject objRes = this.serv.executeService("scaleChangeService", objectArgs);
         if (objRes.getResultCode() == -1) {
            throw new Exception();
         }
      }

   }

   private void ExchangeOtherDetailsBtnMstAndHst(HstDcpsChanges lObjHstDcpsChanges, Map objectArgs) throws Exception {
      HstDcpsOtherChanges lObjHstDcpsOtherChanges = null;
      HstDcpsOtherChanges lObjTempHstDcpsOtherChanges = null;
      MstEmp lObjMstEmp = null;
      RltDcpsPayrollEmp lObjRltDcpsPayrollEmp = null;
      Long dcpsOtherChangesIdPk = null;
      ChangesFormDAO lObjOtherChangesFormDAO = new ChangesFormDAOImpl(HstDcpsOtherChanges.class, this.serv.getSessionFactory());
      dcpsOtherChangesIdPk = lObjOtherChangesFormDAO.getOtherChangesIdforChangesId(lObjHstDcpsChanges.getDcpsChangesId());
      lObjHstDcpsOtherChanges = (HstDcpsOtherChanges)lObjOtherChangesFormDAO.read(dcpsOtherChangesIdPk);
      NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class, this.serv.getSessionFactory());
      lObjMstEmp = (MstEmp)lObjNewRegDdoDAO.read(lObjHstDcpsChanges.getDcpsEmpId());
      lObjRltDcpsPayrollEmp = lObjNewRegDdoDAO.getPayrollVOForEmpId(lObjHstDcpsChanges.getDcpsEmpId());
      lObjTempHstDcpsOtherChanges = (HstDcpsOtherChanges)lObjHstDcpsOtherChanges.clone();
      lObjHstDcpsOtherChanges.setBankName(lObjMstEmp.getBankName());
      lObjHstDcpsOtherChanges.setBranchName(lObjMstEmp.getBranchName());
      lObjHstDcpsOtherChanges.setBankAccountNo(lObjMstEmp.getBankAccountNo());
      lObjHstDcpsOtherChanges.setIFSCCode(lObjMstEmp.getIFSCCode());
      lObjHstDcpsOtherChanges.setAcDcpsMaintainedBy(lObjMstEmp.getAcDcpsMaintainedBy());
      lObjHstDcpsOtherChanges.setAcMntndByOthers(lObjMstEmp.getAcMntndByOthers());
      lObjHstDcpsOtherChanges.setAcNonSRKAEmp(lObjMstEmp.getAcNonSRKAEmp());
      lObjHstDcpsOtherChanges.setAcMaintainedBy(lObjRltDcpsPayrollEmp.getAcMaintainedBy());
      lObjHstDcpsOtherChanges.setPfSeries(lObjRltDcpsPayrollEmp.getPfSeries());
      lObjHstDcpsOtherChanges.setPfSeriesDesc(lObjRltDcpsPayrollEmp.getPfSeriesDesc());
      lObjHstDcpsOtherChanges.setPfAcNo(lObjRltDcpsPayrollEmp.getPfAcNo());
      lObjHstDcpsOtherChanges.setUpdatedDate(this.gDtCurDate);
      lObjHstDcpsOtherChanges.setUpdatedPostId(this.gLngPostId);
      lObjHstDcpsOtherChanges.setUpdatedUserId(this.gLngUserId);
      lObjOtherChangesFormDAO.update(lObjHstDcpsOtherChanges);
      if (lObjTempHstDcpsOtherChanges.getBankName() != null) {
         lObjMstEmp.setBankName(lObjTempHstDcpsOtherChanges.getBankName());
      }

      if (lObjTempHstDcpsOtherChanges.getBranchName() != null) {
         lObjMstEmp.setBranchName(lObjTempHstDcpsOtherChanges.getBranchName());
      }

      if (lObjTempHstDcpsOtherChanges.getBankAccountNo() != null) {
         lObjMstEmp.setBankAccountNo(lObjTempHstDcpsOtherChanges.getBankAccountNo());
      }

      if (lObjTempHstDcpsOtherChanges.getIFSCCode() != null) {
         lObjMstEmp.setIFSCCode(lObjTempHstDcpsOtherChanges.getIFSCCode());
      }

      if (lObjTempHstDcpsOtherChanges.getAcDcpsMaintainedBy() != null) {
         lObjMstEmp.setAcDcpsMaintainedBy(lObjTempHstDcpsOtherChanges.getAcDcpsMaintainedBy());
      }

      if (lObjTempHstDcpsOtherChanges.getAcMntndByOthers() != null) {
         lObjMstEmp.setAcMntndByOthers(lObjTempHstDcpsOtherChanges.getAcMntndByOthers());
      }

      if (lObjTempHstDcpsOtherChanges.getAcNonSRKAEmp() != null) {
         lObjMstEmp.setAcNonSRKAEmp(lObjTempHstDcpsOtherChanges.getAcNonSRKAEmp());
      }

      if (lObjTempHstDcpsOtherChanges.getAcMaintainedBy() != null) {
         lObjRltDcpsPayrollEmp.setAcMaintainedBy(lObjTempHstDcpsOtherChanges.getAcMaintainedBy());
      }

      if (lObjTempHstDcpsOtherChanges.getPfSeries() != null) {
         lObjRltDcpsPayrollEmp.setPfSeries(lObjTempHstDcpsOtherChanges.getPfSeries());
      }

      if (lObjTempHstDcpsOtherChanges.getPfSeriesDesc() != null) {
         lObjRltDcpsPayrollEmp.setPfSeriesDesc(lObjTempHstDcpsOtherChanges.getPfSeriesDesc());
      }

      if (lObjTempHstDcpsOtherChanges.getPfAcNo() != null) {
         lObjRltDcpsPayrollEmp.setPfAcNo(lObjTempHstDcpsOtherChanges.getPfAcNo());
      }

      LedgerReportDAOImpl lObjLedgerReportDAOImpl = new LedgerReportDAOImpl((Class)null, this.serv.getSessionFactory());
      MstEmp mstEmp = lObjNewRegDdoDAO.getEmpVOForEmpId(lObjTempHstDcpsOtherChanges.getDcpsEmpId());
      String empSevarthId = mstEmp.getSevarthId().toString();
      List DupliGPFEmpList = lObjLedgerReportDAOImpl.getGPFDuplEmpList(empSevarthId);
      this.gLogger.error("DupliGPFEmpList------" + DupliGPFEmpList.size());
      if (DupliGPFEmpList != null && !DupliGPFEmpList.isEmpty()) {
         this.gLogger.error("in if------" + DupliGPFEmpList.size());
         lObjLedgerReportDAOImpl.updateGPFDuplEmpDetails(empSevarthId, lObjTempHstDcpsOtherChanges.getPfAcNo(), lObjTempHstDcpsOtherChanges.getPfSeriesDesc());
      }

      this.gLogger.error("lObjMstEmp.setDcpsId--" + lObjMstEmp.getDcpsId());
      if (lObjMstEmp.getEmpEligibilityFlag().equals("Y")) {
         int count = lObjNewRegDdoDAO.getCountTempEmpR3Data(lObjMstEmp.getDcpsId());
         this.gLogger.error("lObjMstEmp.setDcpsId count--" + count);
         if (count > 0) {
            lObjNewRegDdoDAO.updateTempEmpR3Data(lObjMstEmp.getDcpsId());
         }

         lObjMstEmp.setOldDcpsId(lObjMstEmp.getDcpsId());
         lObjMstEmp.setDcpsId(lObjHstDcpsChanges.getDcpsEmpId().toString());
         lObjMstEmp.setDcpsOrGpf('N');
         lObjMstEmp.setRegStatus(2L);
      }

      ChangesFormDAOImpl lObjChangesDAO = new ChangesFormDAOImpl((Class)null, this.serv.getSessionFactory());
      List Emplist = lObjChangesDAO.getdetails(empSevarthId);
      this.gLogger.error("in if------" + Emplist.size());
      if (Emplist != null && !Emplist.isEmpty()) {
         this.gLogger.error("in if------" + Emplist.size());
         lObjChangesDAO.updateMstEmpGpfEmpAcc(empSevarthId, lObjTempHstDcpsOtherChanges.getPfAcNo(), lObjTempHstDcpsOtherChanges.getPfSeriesDesc());
      }

      this.gLogger.error("in if------" + Emplist);
      List Emplist1 = lObjChangesDAO.getdetails1(empSevarthId);
      this.gLogger.error("in if------" + Emplist1.size());
      if (Emplist1 != null && !Emplist1.isEmpty()) {
         this.gLogger.error("in if------" + Emplist1.size());
         lObjChangesDAO.updateMstGpfYearly(empSevarthId, lObjTempHstDcpsOtherChanges.getPfAcNo(), lObjTempHstDcpsOtherChanges.getPfSeriesDesc());
      }

      this.gLogger.error("in if------" + Emplist1);
      List Emplist11 = lObjChangesDAO.getdetails11(empSevarthId);
      this.gLogger.error("in if------" + Emplist11.size());
      if (Emplist11 != null && !Emplist11.isEmpty()) {
         this.gLogger.error("in if------" + Emplist11.size());
         lObjChangesDAO.updateTrnEmpGpfAcc(empSevarthId, lObjTempHstDcpsOtherChanges.getPfAcNo(), lObjTempHstDcpsOtherChanges.getPfSeriesDesc());
      }

      this.gLogger.error("in if------" + Emplist11);
      List Emplistgpfadvance = lObjChangesDAO.getdetailsforgpfadvance(empSevarthId);
      this.gLogger.error("in if------" + Emplistgpfadvance.size());
      if (Emplistgpfadvance != null && !Emplistgpfadvance.isEmpty()) {
         this.gLogger.error("in if------" + Emplistgpfadvance.size());
         lObjChangesDAO.updategpfadvance(empSevarthId, lObjTempHstDcpsOtherChanges.getPfAcNo(), lObjTempHstDcpsOtherChanges.getPfSeriesDesc());
      }

      this.gLogger.error("in if------" + Emplistgpfadvance);
      List Emplistgpfmonthly = lObjChangesDAO.getdetailsforgpfmonthly(empSevarthId);
      this.gLogger.error("in if------" + Emplistgpfmonthly.size());
      if (Emplistgpfmonthly != null && !Emplistgpfmonthly.isEmpty()) {
         this.gLogger.error("in if------" + Emplistgpfmonthly.size());
         lObjChangesDAO.updategpfmonthly(empSevarthId, lObjTempHstDcpsOtherChanges.getPfAcNo(), lObjTempHstDcpsOtherChanges.getPfSeriesDesc());
      }

      this.gLogger.error("in if------" + Emplistgpfadvance);
      List Emplistgpfinterestdtls = lObjChangesDAO.getdetailsforgpfinterestdtls(empSevarthId);
      this.gLogger.error("in if------" + Emplistgpfinterestdtls.size());
      if (Emplistgpfinterestdtls != null && !Emplistgpfinterestdtls.isEmpty()) {
         this.gLogger.error("in if------" + Emplistgpfinterestdtls.size());
         lObjChangesDAO.updategpfinterestdtls(empSevarthId, lObjTempHstDcpsOtherChanges.getPfAcNo(), lObjTempHstDcpsOtherChanges.getPfSeriesDesc());
      }

      this.gLogger.error("in if------" + Emplistgpfinterestdtls);
      List Emplist7Pcgpfinterestdtls = lObjChangesDAO.getdtlsfor7Pcgpfinterest(empSevarthId);
      this.gLogger.error("in if------" + Emplist7Pcgpfinterestdtls.size());
      if (Emplist7Pcgpfinterestdtls != null && !Emplist7Pcgpfinterestdtls.isEmpty()) {
         this.gLogger.error("in if------" + Emplist7Pcgpfinterestdtls.size());
         lObjChangesDAO.update7pcgpfinterestdtls(empSevarthId, lObjTempHstDcpsOtherChanges.getPfAcNo(), lObjTempHstDcpsOtherChanges.getPfSeriesDesc());
      }

      this.gLogger.error("in if------" + Emplist7Pcgpfinterestdtls);
      List Emplisttrngpfwithdrawal = lObjChangesDAO.getdetailsfortrngpfwithdrawal(empSevarthId);
      this.gLogger.error("in if------" + Emplisttrngpfwithdrawal.size());
      if (Emplisttrngpfwithdrawal != null && !Emplisttrngpfwithdrawal.isEmpty()) {
         this.gLogger.error("in if------" + Emplisttrngpfwithdrawal.size());
         lObjChangesDAO.updatetrngpfwithdrawal(empSevarthId, lObjTempHstDcpsOtherChanges.getPfAcNo(), lObjTempHstDcpsOtherChanges.getPfSeriesDesc());
      }

      this.gLogger.error("in if------" + Emplisttrngpfwithdrawal);
      List transactionid = lObjChangesDAO.getTransactionIdDetails(empSevarthId);
      String tranid = "";
      String sevarthid = "";
      BigInteger billdtlsid = null;
      if (transactionid != null && !transactionid.isEmpty()) {
         Iterator iterator = transactionid.iterator();

         while(iterator.hasNext()) {
            Object[] tuple = (Object[])iterator.next();
            if (tuple[0] != null) {
               tranid = tuple[0].toString();
               sevarthid = tuple[1].toString();
               billdtlsid = (BigInteger)tuple[2];
            }

            if (tranid != null && !tranid.isEmpty()) {
               lObjChangesDAO.updateTrngpfwithdrawal(tranid, lObjTempHstDcpsOtherChanges.getPfAcNo(), lObjTempHstDcpsOtherChanges.getPfSeriesDesc());
            }
         }

         this.gLogger.info(" Trnasactionid ---- " + tranid);
      }

      List Transactid = lObjChangesDAO.getTransactionIddtsforgpfadvance(empSevarthId);
      String TransId = "";
      String SevarthId = "";
      BigInteger Billdtsid = null;
      if (Transactid != null && !Transactid.isEmpty()) {
         Iterator iterator = Transactid.iterator();

         while(iterator.hasNext()) {
            Object[] tuple = (Object[])iterator.next();
            if (tuple[0] != null) {
               TransId = tuple[0].toString();
               SevarthId = tuple[1].toString();
               Billdtsid = (BigInteger)tuple[2];
            }

            if (TransId != null && !TransId.isEmpty()) {
               lObjChangesDAO.updateTransactionidmstbilldetails(TransId, lObjTempHstDcpsOtherChanges.getPfAcNo(), lObjTempHstDcpsOtherChanges.getPfSeriesDesc());
            }
         }

         this.gLogger.info(" Trnasactionid ---- " + tranid);
      }

      lObjMstEmp.setUpdatedDate(this.gDtCurDate);
      lObjMstEmp.setUpdatedPostId(this.gLngPostId);
      lObjMstEmp.setUpdatedUserId(this.gLngUserId);
      objectArgs.put("ChangedOtherVO", lObjTempHstDcpsOtherChanges);
      this.serv.executeService("changePersonalDtlsInPayroll", objectArgs);
      lObjOtherChangesFormDAO.update(lObjRltDcpsPayrollEmp);
      lObjOtherChangesFormDAO.update(lObjMstEmp);
   }

   private void ExchangeNomineeDetailsBtnMstAndHst(HstDcpsChanges lObjHstDcpsChanges, Map objectArgs) throws Exception {
      List<HstDcpsNomineeChanges> lListNomineeFromHst = null;
      List<MstEmpNmn> lArrMstEmpNmn = new ArrayList();
      Long lLongLatestRefId = null;
      Long lLongEmpId = null;
      MstEmpNmn lObjMstEmpNmn = null;
      Long lLngNomineeId = null;
      ChangesFormDAO lObjOtherChangesFormDAO = new ChangesFormDAOImpl(HstDcpsOtherChanges.class, this.serv.getSessionFactory());
      NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmpNmn.class, this.serv.getSessionFactory());
      lLongLatestRefId = lObjOtherChangesFormDAO.getLatestRefIdForNomineeChanges(lObjHstDcpsChanges.getDcpsEmpId(), lObjHstDcpsChanges.getDcpsChangesId());
      lLongEmpId = lObjHstDcpsChanges.getDcpsEmpId();
      lListNomineeFromHst = lObjOtherChangesFormDAO.getNomineesFromHst(lLongLatestRefId, lLongEmpId);
      lObjNewRegDdoDAO.deleteNomineesForGivenEmployee(lLongEmpId);

      Integer lInt;
      for(lInt = 0; lInt < lListNomineeFromHst.size(); lInt = lInt + 1) {
         lObjMstEmpNmn = new MstEmpNmn();
         lObjMstEmpNmn.setDcpsEmpId(lObjNewRegDdoDAO.getEmpVOForEmpId(((HstDcpsNomineeChanges)lListNomineeFromHst.get(lInt)).getDcpsEmpId()));
         lObjMstEmpNmn.setAddress1(((HstDcpsNomineeChanges)lListNomineeFromHst.get(lInt)).getAddress1());
         lObjMstEmpNmn.setName(((HstDcpsNomineeChanges)lListNomineeFromHst.get(lInt)).getName());
         lObjMstEmpNmn.setRlt(((HstDcpsNomineeChanges)lListNomineeFromHst.get(lInt)).getRlt());
         lObjMstEmpNmn.setShare(((HstDcpsNomineeChanges)lListNomineeFromHst.get(lInt)).getShare());
         lObjMstEmpNmn.setGuardian(((HstDcpsNomineeChanges)lListNomineeFromHst.get(lInt)).getGuardian());
         lObjMstEmpNmn.setInValid(((HstDcpsNomineeChanges)lListNomineeFromHst.get(lInt)).getInValid());
         lObjMstEmpNmn.setDob(((HstDcpsNomineeChanges)lListNomineeFromHst.get(lInt)).getDob());
         lObjMstEmpNmn.setLangId(this.gLngLangId);
         lObjMstEmpNmn.setLocId(Long.valueOf(this.gStrLocationCode));
         lObjMstEmpNmn.setDbId(this.gLngDBId);
         lObjMstEmpNmn.setCreatedDate(this.gDtCurDate);
         lObjMstEmpNmn.setCreatedPostId(this.gLngPostId);
         lObjMstEmpNmn.setCreatedUserId(this.gLngUserId);
         lObjMstEmpNmn.setUpdatedDate(this.gDtCurDate);
         lObjMstEmpNmn.setUpdatedPostId(this.gLngPostId);
         lObjMstEmpNmn.setUpdatedUserId(this.gLngUserId);
         lArrMstEmpNmn.add(lObjMstEmpNmn);
      }

      for(lInt = 0; lInt < lArrMstEmpNmn.size(); lInt = lInt + 1) {
         lLngNomineeId = IFMSCommonServiceImpl.getNextSeqNum("MST_DCPS_EMP_NMN", objectArgs);
         ((MstEmpNmn)lArrMstEmpNmn.get(lInt)).setDcpsEmpNmnId(lLngNomineeId);
         lObjNewRegDdoDAO.create(lArrMstEmpNmn.get(lInt));
      }

   }

   private void ExchangePhotoAndSignDetailsBtnMstAndHst(HstDcpsChanges lObjHstDcpsChanges) throws Exception {
      MstEmp lObjMstEmp = null;
      TrnDcpsChanges lObjTrnDcpsChanges = null;
      String fieldNamePhoto = "PhotoId";
      String fieldNameSign = "SignatureId";
      Long lLongTrnChangesInPhotoIdPk = null;
      Long lLongTrnChangesInSignIdPk = null;
      Long lLongChangedPhotoId = null;
      Long lLongChangedSignId = null;
      ChangesFormDAO lObjPhotoAndSignChangesFormDAO = new ChangesFormDAOImpl(TrnDcpsChanges.class, this.serv.getSessionFactory());
      lLongTrnChangesInPhotoIdPk = lObjPhotoAndSignChangesFormDAO.getPkFromTrnForTheChangeInPhotoSign(lObjHstDcpsChanges.getDcpsChangesId(), fieldNamePhoto);
      lObjTrnDcpsChanges = (TrnDcpsChanges)lObjPhotoAndSignChangesFormDAO.read(lLongTrnChangesInPhotoIdPk);
      if (lObjTrnDcpsChanges.getNewValue() != null) {
         lLongChangedPhotoId = Long.valueOf(lObjTrnDcpsChanges.getNewValue());
      }

      lObjTrnDcpsChanges = null;
      lLongTrnChangesInSignIdPk = lObjPhotoAndSignChangesFormDAO.getPkFromTrnForTheChangeInPhotoSign(lObjHstDcpsChanges.getDcpsChangesId(), fieldNameSign);
      lObjTrnDcpsChanges = (TrnDcpsChanges)lObjPhotoAndSignChangesFormDAO.read(lLongTrnChangesInSignIdPk);
      if (lObjTrnDcpsChanges.getNewValue() != null) {
         lLongChangedSignId = Long.valueOf(lObjTrnDcpsChanges.getNewValue());
      }

      NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class, this.serv.getSessionFactory());
      lObjMstEmp = (MstEmp)lObjNewRegDdoDAO.read(lObjHstDcpsChanges.getDcpsEmpId());
      if (lLongChangedPhotoId != null) {
         lObjMstEmp.setPhotoAttachmentID(lLongChangedPhotoId);
      }

      if (lLongChangedSignId != null) {
         lObjMstEmp.setSignatureAttachmentID(lLongChangedSignId);
      }

      lObjMstEmp.setUpdatedDate(this.gDtCurDate);
      lObjMstEmp.setUpdatedPostId(this.gLngPostId);
      lObjMstEmp.setUpdatedUserId(this.gLngUserId);
      lObjPhotoAndSignChangesFormDAO.update(lObjMstEmp);
   }

   public ResultObject popUpReasonsForSalaryChange(Map inputMap) {
      ResultObject resObj = new ResultObject(0, "FAIL");
      List lListReasonsForSalaryChange = null;
      Integer lNoOfReasons = 0;
      Long lLongPaycommission = null;

      try {
         this.setSessionInfo(inputMap);
         this.gLogger.info("paycommsion in service:" + StringUtility.getParameter("cmbPayCommission", this.request));
         if (!StringUtility.getParameter("cmbPayCommission", this.request).equalsIgnoreCase("")) {
            lLongPaycommission = Long.valueOf(StringUtility.getParameter("cmbPayCommission", this.request));
         }

         if (lLongPaycommission == 700015L) {
            lListReasonsForSalaryChange = IFMSCommonServiceImpl.getLookupValues("ReasonsForSalaryChangeIn5PC", SessionHelper.getLangId(inputMap), inputMap);
         } else if (lLongPaycommission != 700016L && lLongPaycommission != 700349L) {
            lListReasonsForSalaryChange = IFMSCommonServiceImpl.getLookupValues("ReasonsForSalaryChangeIn6PC", SessionHelper.getLangId(inputMap), inputMap);
         } else {
            lListReasonsForSalaryChange = IFMSCommonServiceImpl.getLookupValues("ReasonsForSalaryChangeIn6PC", SessionHelper.getLangId(inputMap), inputMap);
         }

         lNoOfReasons = lListReasonsForSalaryChange.size();
         resObj.setResultValue(inputMap);
      } catch (Exception var8) {
         resObj.setResultValue((Object)null);
         resObj.setThrowable(var8);
         resObj.setResultCode(-1);
         resObj.setViewName("errorPage");
         this.gLogger.error(" Error is " + var8, var8);
      }

      String lSBStatus = this.getResponseXMLDocToDisplayReasons(lNoOfReasons, lListReasonsForSalaryChange).toString();
      String lStrResult = (new AjaxXmlBuilder()).addItem("ajax_key", lSBStatus).toString();
      this.gLogger.info("lSBStatus:" + lSBStatus);
      this.gLogger.info("lStrResult:" + lStrResult);
      inputMap.put("ajaxKey", lStrResult);
      resObj.setResultValue(inputMap);
      resObj.setViewName("ajaxData");
      return resObj;
   }

   private StringBuilder getResponseXMLDocToDisplayReasons(Integer lNoOfReasons, List lListReasonsForSalaryChange) {
      StringBuilder lStrBldXML = new StringBuilder();
      lNoOfReasons = lListReasonsForSalaryChange.size();
      lStrBldXML.append("<XMLDOC>");
      lStrBldXML.append("<lNoOfReasons>");
      lStrBldXML.append(lNoOfReasons);
      lStrBldXML.append("</lNoOfReasons>");
      Iterator it = lListReasonsForSalaryChange.iterator();

      while(it.hasNext()) {
         CmnLookupMst lObjCmnLookupMst = (CmnLookupMst)it.next();
         lStrBldXML.append("<ReasonId>");
         lStrBldXML.append("<![CDATA[");
         lStrBldXML.append(lObjCmnLookupMst.getLookupId());
         lStrBldXML.append("]]>");
         lStrBldXML.append(" </ReasonId>");
         lStrBldXML.append(" <ReasonDesc>");
         lStrBldXML.append(lObjCmnLookupMst.getLookupDesc());
         lStrBldXML.append(" </ReasonDesc>");
      }

      lStrBldXML.append("</XMLDOC>");
      return lStrBldXML;
   }

   public ResultObject changeBasicPayAsPerLevel(Map inputMap) {
      this.gLogger.info("inside changeBasicPayAsPerLevel");
      this.setSessionInfo(inputMap);
      ResultObject resObj = new ResultObject(0, "FAIL");
      ChangesFormDAO lObjChangesDAO = new ChangesFormDAOImpl((Class)null, this.serv.getSessionFactory());
      String grade = "";
      List basicpay = null;

      try {
         String level = StringUtility.getParameter("levelDetails", this.request);
         String GisApplicable = StringUtility.getParameter("GisApplicable", this.request);
         this.gLogger.info("level is " + level + "--GisApplicable--" + GisApplicable);
         if (Long.parseLong(GisApplicable) == 700217L) {
            grade = lObjChangesDAO.getGradeForGivenStateLevel(level);
            basicpay = lObjChangesDAO.getStateBasicAsPerGrade(grade);
         } else {
            grade = lObjChangesDAO.getGradeForGivenLevel(level);
            basicpay = lObjChangesDAO.getBasicAsPerGrade(grade);
         }

         this.gLogger.info("grade is " + grade + "basicpay size is " + basicpay.size());
         String lStrTempResult = null;
         if (basicpay != null) {
            lStrTempResult = (new AjaxXmlBuilder()).addItems(basicpay, "desc", "id", true).toString();
         }

         inputMap.put("ajaxKey", lStrTempResult);
         resObj.setResultValue(inputMap);
         resObj.setViewName("ajaxData");
      } catch (Exception var9) {
         resObj.setResultValue((Object)null);
         resObj.setThrowable(var9);
         resObj.setResultCode(-1);
         resObj.setViewName("errorPage");
         this.gLogger.error(" Error is " + var9, var9);
      }

      return resObj;
   }
}