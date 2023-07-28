package com.tcs.sgv.pensionpay.service;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.utils.fileupload.AttachmentHelper;
import com.tcs.sgv.common.utils.fileupload.monitor.FileUploadListener;
import com.tcs.sgv.common.utils.fileupload.monitor.FileUploadListener.FileUploadStats;
import com.tcs.sgv.common.utils.fileupload.monitor.MonitoredDiskFileItemFactory;
import com.tcs.sgv.common.utils.fileupload.monitor.OutputStreamListener;
import com.tcs.sgv.common.utils.fileupload.validation.FormatDescription;
import com.tcs.sgv.common.utils.fileupload.validation.FormatIdentification;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UploadServlet1
  extends HttpServlet
{
  Log logger = LogFactory.getLog(getClass());
  private static final long serialVersionUID = 1L;
  private static ResourceBundle constResourceBundle;
  private static ResourceBundle constantBundle;
  ArrayList<FileItem> fileItemArrayList = new ArrayList();
  String fileName;
  String fileDescriptionString = null;
  AttachmentHelper attachmentHelper = new AttachmentHelper();
  
  protected void doGet(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws ServletException, IOException
  {
    doPost(paramHttpServletRequest, paramHttpServletResponse);
  }
  
  protected void doPost(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws ServletException, IOException
  {
    this.logger.info("UploadServlet: ..............................INSIDE UPLOAD SERVLET.....................................");
    HttpSession localHttpSession = paramHttpServletRequest.getSession(false);
    
    Locale localLocale = (Locale)localHttpSession.getAttribute("localeObj");
    
    constResourceBundle = ResourceBundle.getBundle("resources/common/CommonLables", localLocale);
    constantBundle = ResourceBundle.getBundle("resources/Constants");
    String str1 = paramHttpServletRequest.getParameter("attachmentNameHidden");
    
    String str2 = paramHttpServletRequest.getParameter("rowNumber");
    
    this.logger.info("UploadServlet:fileCategoryString=" + str1);
    this.fileDescriptionString = paramHttpServletRequest.getParameter("desc");
    
    String str3 = paramHttpServletRequest.getParameter("removeElement");
    this.logger.info("UploadServlet:removeElementString=" + str3);
    
    int i = 1;
    try
    {
      if (str3 != null) {
        if (str3.equalsIgnoreCase("removeElementFromArrayList"))
        {
          int j = Integer.parseInt(paramHttpServletRequest.getParameter("elementNumber"));
          

          String str5 = paramHttpServletRequest.getParameter("rowCount");
          if ((str5 != null) && (str5.trim().length() > 0))
          {
            int k = Integer.parseInt(str5);
            j -= k;
          }
          this.logger.info("UploadServlet:elementNumberInt=" + j);
          
          boolean bool = this.attachmentHelper.deleteFileItemFromArrayList(j, str1, localHttpSession, str2);
          
          this.logger.info("UploadServlet:The status of arraylist element removal---->" + bool);
          
          i = 0;
        }
      }
    }
    catch (Exception localException1)
    {
      this.logger.error(localException1.getMessage(), localException1);
      i = 0;
    }
    String str4 = paramHttpServletRequest.getParameter("attachmentMpgSrNo");
    if (str4 != null)
    {
      long l = Long.valueOf(str4).longValue();
      
      i = 1;
      try
      {
        this.attachmentHelper.deleteCmnAttachmentMpgFromAttMst(l, str1, localHttpSession);
        
        i = 0;
      }
      catch (Exception localException3)
      {
        this.logger.error(localException3.getMessage(), localException3);
        i = 0;
      }
    }
    if (i == 1) {
      try
      {
        if ("status".equals(paramHttpServletRequest.getParameter("c")))
        {
          this.logger.info("UploadServlet: In before calling doStatus=" + paramHttpServletRequest.getParameter("c"));
          doStatus(localHttpSession, paramHttpServletResponse);
        }
        else
        {
          this.logger.info("UploadServlet: Calling do file upload");
          doFileUpload(localHttpSession, paramHttpServletRequest, paramHttpServletResponse);
        }
      }
      catch (Exception localException2)
      {
        this.logger.error("UploadServlet: The exception in doStatus and doUpload are -->" + localException2.getMessage(), localException2);
      }
    }
  }
  
  private void doFileUpload(HttpSession paramHttpSession, HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws IOException
  {
    String str1 = paramHttpServletRequest.getParameter("attachmentNameHidden");
    
    
   
    
    String str2 = "";
    try
    {
      str2 = StringUtility.getParameter("fileDescription", paramHttpServletRequest);
    }
    catch (Exception localException1)
    {
      this.logger.error("UploadServlet:File Descritpion error" + localException1, localException1);
    }
    String str3 = paramHttpServletRequest.getParameter("rowNumber");
    String str4 = paramHttpServletRequest.getParameter("attachmentType");
    

    String str5 = paramHttpServletRequest.getParameter("fileNameForBio");
    

    int i = 0;
    ArrayList localArrayList = new ArrayList();
    /* localArrayList.add("DOC");
    localArrayList.add("PDF");
    localArrayList.add("RTF");
    localArrayList.add("XLS");
    localArrayList.add("BMP");
    localArrayList.add("GIF");
  */  localArrayList.add("JPEG");
  /*  localArrayList.add("PBM");
    localArrayList.add("PGM");
    localArrayList.add("PNG");
    localArrayList.add("TIFF");
    localArrayList.add("ZIP");
    localArrayList.add("RAR");
    localArrayList.add("XLXS");*/

    
    int j = 0;
    
    long l = Long.parseLong(constantBundle.getString("UPLOAD_FILE_SIZE"));
    
    String str6 = constantBundle.getString("FILE_STORAGE_FLAG");
    try
    {
      this.logger.info("UploadServlet:  In the beginning of file upload.......................");
      
      String str7 = paramHttpSession.getServletContext().getRealPath("UPLOADED-FILES");
      this.logger.info("UploadServlet:serverPathStr=" + str7);
      
      File localObject1 = new File(str7);
      if (!((File)localObject1).exists()) {
        ((File)localObject1).mkdir();
      }
      FileUploadListener localObject2 = new FileUploadListener(paramHttpServletRequest.getContentLength());
      paramHttpSession.setAttribute("FILE_UPLOAD_STATS", ((FileUploadListener)localObject2).getFileUploadStats());
      
      Object localObject3 = new ArrayList();
      Object localObject4;
      Object localObject6;
      Object localObject7;
      Object localObject8;
      if ((str4 != null) && (str4.equals("scanner")))
      {
        localObject4 = null;
        
        localObject4 = paramHttpServletRequest.getInputStream();
        
        int m = paramHttpServletRequest.getContentLength();
        localObject6 = new byte[m];
       
//        for ( int n = 0; ; n++)
//        {
//          int i1 = ((InputStream)localObject4).read();
//          if (i1 == -1) {
//            break;
//          }
//          localObject6[n] = ((byte)i1);
//         
//        }
        ((InputStream)localObject4).close();
        localObject4 = null;
//        byte[] arrayOfByte = new byte[n];
//        System.arraycopy(localObject6, 0, arrayOfByte, 0, n);
//        this.logger.info("Image Data length : " + arrayOfByte.length);
        


        localObject7 = null;
        if (str5 != null) {
          localObject7 = str5;
        } else {
          localObject7 = paramHttpServletRequest.getParameter("attachmentPrefix") + "_" + DBUtility.getCurrentDateFromDB().getTime() + ".jpg";
        }
        this.logger.info("File Name : " + (String)localObject7);
        
        localObject8 = new DiskFileItem("importFile" + str1, paramHttpServletRequest.getContentType(), false, (String)localObject7, paramHttpServletRequest.getContentLength(), (File)localObject1);
        OutputStream localOutputStream = ((FileItem)localObject8).getOutputStream();
//        localOutputStream.write(arrayOfByte);
        localOutputStream.close();
        localObject6 = null;
//        arrayOfByte = null;
        ((FileItem)localObject8).setFieldName("importFile" + str1);
        
        ((List)localObject3).add(localObject8);
        
        ServletOutputStream localServletOutputStream = paramHttpServletResponse.getOutputStream();
        ObjectOutputStream localObjectOutputStream = new ObjectOutputStream(localServletOutputStream);
        
        localObjectOutputStream.writeObject(localObject7);
        localObjectOutputStream.flush();
        localObjectOutputStream.close();
      }
      else
      {
        localObject4 = new MonitoredDiskFileItemFactory((OutputStreamListener)localObject2);
        
        MonitoredDiskFileItemFactory localObject5 = (MonitoredDiskFileItemFactory)localObject4;
        
        this.logger.info("UploadServlet:After MonitoredDiskFileItemFactory");
        


        ((MonitoredDiskFileItemFactory)localObject5).setRepository((File)localObject1);
        

        localObject6 = new ServletFileUpload((FileItemFactory)localObject4);
        
        this.logger.info("File SIZE: " + Long.parseLong(constantBundle.getString("UPLOAD_FILE_SIZE")));
        if ((str6 != null) && (str6.equalsIgnoreCase("N"))) {
          ((ServletFileUpload)localObject6).setSizeMax(100000);
        }
        localObject3 = ((ServletFileUpload)localObject6).parseRequest(paramHttpServletRequest);
      }
      int k = 0;
      for (Object localObject5 = ((List)localObject3).iterator(); ((Iterator)localObject5).hasNext();)
      {
        localObject6 = (FileItem)((Iterator)localObject5).next();
        if (!((FileItem)localObject6).isFormField())
        {
          this.fileName = ((FileItem)localObject6).getName();
          if (this.fileName != null)
          {
            this.fileName = FilenameUtils.getName(this.fileName);
            this.logger.info("UploadServlet:The file name is-->" + this.fileName);
            
            String str8 = ((FileItem)localObject6).getFieldName();
            if (str8.equals("importFile" + str1))
            {
              this.logger.info("fileItem.get().length: " + ((FileItem)localObject6).get().length + "attachmentSize: " + l);
              if (((FileItem)localObject6).get().length > l) {
                if ((str6 != null) && (str6.equalsIgnoreCase("Y"))) {
                  j = 1;
                }
              }
              this.logger.info("UploadServlet:The field name is------>" + ((FileItem)localObject6).getFieldName());
              if (this.attachmentHelper == null) {
                this.attachmentHelper = new AttachmentHelper();
              }
              this.logger.info("UploadServlet: Attachment helper-->" + this.attachmentHelper);
              ((FileItem)localObject6).setFieldName(str2);
              this.logger.info("UploadServlet: The field name is------>" + ((FileItem)localObject6).getFieldName());
              

              FormatDescription localFormatDescription = FormatIdentification.identify(((FileItem)localObject6).get());
              if (localFormatDescription != null)
              {
                this.logger.info("FILE NAME Format=" + localFormatDescription.getShortName() + ", MIME type=" + localFormatDescription.getMimeType());
                
                int i2 = 1;
                
                localObject7 = paramHttpServletRequest.getParameter("xmlAllowed");
                if ((localObject7 != null) && (((String)localObject7).trim().equalsIgnoreCase("Y"))) {
                  if ((localFormatDescription.getShortName().equalsIgnoreCase("XML")) || (localFormatDescription.getShortName().equalsIgnoreCase("XSL"))) {
                    i2 = 0;
                  }
                }
                if (i2 != 0) {
                  if (!localArrayList.contains(localFormatDescription.getShortName()))
                  {
                    i = 1;
                    localObject8 = "Invalid Attachment, Extensions: \"" + localFormatDescription.getShortName() + "\" type is not valid.";
                    sendCompleteResponse(paramHttpServletResponse, (String)localObject8, str1);
                    throw new Exception((String)localObject8);
                  }
                }
              }
              String str9;
              if (((FileItem)localObject6).get().length <= 0)
              {
                i = 1;
                str9 = "Invalid Attachment, Zero byte file is not valid Attachment : " + ((FileItem)localObject6).getName().substring(((FileItem)localObject6).getName().lastIndexOf("\\") + 1, ((FileItem)localObject6).getName().length());
                sendCompleteResponse(paramHttpServletResponse, str9, str1);
                throw new Exception(str9);
              }
              if (j != 0)
              {
                str9 = constantBundle.getString("FILE_STORAGE_PATH");
               this.logger.info("fileStoragePath: " + str9);
                
                localObject7 = new File(str9);
                if (!((File)localObject7).exists()) {
                  ((File)localObject7).mkdir();
                }
                localObject7 = File.createTempFile("Attachment", "", (File)localObject7);
                
                localObject8 = new FileOutputStream((File)localObject7);
                if (((File)localObject7).exists())
                {
                  ((FileOutputStream)localObject8).write(((FileItem)localObject6).get());
                  ((FileOutputStream)localObject8).flush();
                  ((FileOutputStream)localObject8).close();
                }
                this.logger.info("FILE CREATED NAME IS: " + ((File)localObject7).getName());
                
                str2 = str2 + "$" + ((File)localObject7).getName();
                
                this.logger.info("fileDescription: " + str2);
                ((FileItem)localObject6).setFieldName(str2);
              }
              boolean bool = this.attachmentHelper.addFileItemToArrayList((FileItem)localObject6, str1, paramHttpSession, str3);
              
              this.logger.info("UploadServlet: The status of arrayList addition------>" + bool);
              
             
            }
            else
            {
              ((FileItem)localObject6).delete();
            }
          }
        }
      }
      if (k == 0)
      {
        if (str4 == null) {
          sendCompleteResponse(paramHttpServletResponse, null, str1);
        }
      }
      else if (str4 == null) {
        sendCompleteResponse(paramHttpServletResponse, "Could not process uploaded file. Please see log for details.", str1);
      }
    }
    catch (Exception localException2)
    {
      Object localObject1;
      Object localObject2;
      this.logger.error(localException2.getMessage(), localException2);
      if (i == 0)
      {
        localObject1 = constResourceBundle.getString("FILE_SIZE_LIMIT");
        localObject2 = constantBundle.getString("FILE_SIZE_LIMIT");
//        localObject1 = "File Size should be below 17KB  Below";
//        localObject2 = "17KB";
        localObject1 = ((String)localObject1).replace("$SIZE", (CharSequence)localObject2);
        sendCompleteResponse(paramHttpServletResponse, (String)localObject1, str1);
      }
    }
  }
  
  private void doStatus(HttpSession paramHttpSession, HttpServletResponse paramHttpServletResponse)
    throws IOException
  {
    paramHttpServletResponse.setContentType("text/html;charset=utf-8");
    
    paramHttpServletResponse.addHeader("Expires", "0");
    paramHttpServletResponse.addHeader("Cache-Control", "no-store, no-cache, must-revalidate");
    
    paramHttpServletResponse.addHeader("Cache-Control", "post-check=0, pre-check=0");
    paramHttpServletResponse.addHeader("Pragma", "no-cache");
    
    FileUploadListener.FileUploadStats localFileUploadStats = (FileUploadListener.FileUploadStats)paramHttpSession.getAttribute("FILE_UPLOAD_STATS");
    if (localFileUploadStats != null)
    {
      long l1 = localFileUploadStats.getBytesRead();
      long l2 = localFileUploadStats.getTotalSize();
      



      l1 -= l2;
      



      long l3 = (long) Math.floor(l1 / l2 * 100.0D);
      
      long l4 = localFileUploadStats.getElapsedTimeInSeconds();
      double d1 = l1 / (l4 + 1.E-005D);
      double d2 = l2 / (d1 + 1.E-005D);
      if (localFileUploadStats.getBytesRead() != localFileUploadStats.getTotalSize())
      {
        paramHttpServletResponse.getWriter().println("<b>" + constResourceBundle.getString("UPLOAD_STATUS") + "</b><br/>");
        
        paramHttpServletResponse.getWriter().println("<div class=\"prog-border\"><div class=\"prog-bar\" style=\"width: " + l3 + "%;\"></div></div>");
        


        paramHttpServletResponse.getWriter().println("");
        paramHttpServletResponse.getWriter().print("Uploaded: " + l1 + " out of " + l2 + " bytes (" + l3 + "%) " + Math.round(d1 / 1024.0D) + " Kbs");
        



        paramHttpServletResponse.getWriter().print(", Runtime: " + formatTime(l4) + " out of " + formatTime(d2) + " " + formatTime(d2 - l4) + " remaining");
      }
    }
  }
  
  private void sendCompleteResponse(HttpServletResponse paramHttpServletResponse, String paramString1, String paramString2)
    throws IOException
  {
    paramHttpServletResponse.setContentType("text/html;charset=utf-8");
    if (paramString1 == null)
    {
      this.logger.info("UploadServlet: ................... Before kill update. in iF...............");
      paramHttpServletResponse.getOutputStream().print("<html><head><script type='text/javascript'>function killUpdate" + paramString2 + "() { window.parent.killUpdate" + paramString2 + "(''); }</script></head><body onload='killUpdate" + paramString2 + "()'></body></html>");
      



      this.logger.info("UploadServlet: ................... Before kill update. in iF..after done.............");
    }
    else
    {
      this.logger.info("................... Before kill update. in else...............");
      paramHttpServletResponse.getOutputStream().print("<html><head><script type='text/javascript'>function killUpdate" + paramString2 + "() { window.parent.killUpdate" + paramString2 + "('" + paramString1 + "'); }</script></head><body onload='killUpdate" + paramString2 + "()'></body></html>");
      





      this.logger.info("UploadServlet: ................... Before kill update. in else..after done.............");
    }
  }
  
  private String formatTime(double paramDouble)
  {
    long l1 = (long) Math.floor(paramDouble);
    long l2 = (long) Math.floor(paramDouble / 60.0D);
    long l3 = (long) Math.floor(l2 / 60.0D);
    if (l3 != 0L) {
      return l3 + "hours " + l2 % 60L + "minutes " + l1 % 60L + "seconds";
    }
    if (l2 % 60L != 0L) {
      return l2 % 60L + "minutes " + l1 % 60L + "seconds";
    }
    return l1 % 60L + " seconds";
  }
}
