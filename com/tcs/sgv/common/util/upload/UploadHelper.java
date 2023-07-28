package com.tcs.sgv.common.util.upload;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.io.Util;

import com.tcs.sgv.common.constant.UploadConstants;

public class UploadHelper {
	public static Log logger = LogFactory.getLog(UploadHelper.class);
	
	/**
	 * This method fetch file(s) from the request
	 * @param uploadObjects List of UploadObject(s) containing file information.
	 * @param request HttpServletRequest object
	 * @param fileSeperator File Seperator (Use "\\" for windows, "/" for linux)
	 * @return List 
	 */
	public static List fetchFiles(List uploadObjs, HttpServletRequest request, String fileSeperator) {
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);		
		if (uploadObjs!=null) {
			/* get file item that are not form field .. */
			Map fileMap = UploadHelper.getFileItems(request);
			Iterator it = uploadObjs.iterator();
			while(it.hasNext()){
				UploadObject uploadObj = null;
				try {
					uploadObj= (UploadObject)it.next();
					uploadObj.setStatusCode(UploadObject.FAILURE);
					FileItem item = (FileItem) fileMap.get(uploadObj.getUploadName());
					
					/* fetch file if the restricted size is greater than actual file size  or there no file size restriction .. */
					if ((uploadObj.getRestrictSize()>item.getSize()) || uploadObj.getRestrictSize()==UploadObject.NO_RESTRICT) {						
						String fileName = parseFileName(item.getName(),fileSeperator);
						fileName = getUniqueFileInPath(uploadObj.getFileLocation(),fileSeperator,fileName);
						File file = new File(fileName);
						FileOutputStream out = new FileOutputStream(file);
						long bytesCopied =Util.copyStream(item.getInputStream(),out );
						out.close();
						
						if(bytesCopied==item.getSize()) {
							uploadObj.setFileName(file.getName());
							uploadObj.setSize(bytesCopied);
							uploadObj.setStatusCode(UploadObject.SUCCESS);
//							System.out.println("bytes Copied .. " + uploadObj.getFileName());
						}												
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					if(logger.isErrorEnabled()) logger.error("Exception occured in UploadHelper.fetchFiles #\n"+ex);
				}					
			}
		}
		return uploadObjs;
	}
  	
	/**
	 * This method returns map of FileItem that are not form field. 
	 * @param request
	 * @return Map
	 */
	public static Map getFileItems(HttpServletRequest request) {
		Map map = new HashMap();
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			Enumeration enumeration = request.getParameterNames();
			while (enumeration.hasMoreElements()) {
				String element = (String) enumeration.nextElement();
				System.out.println("PARAMETER : " + element + " " + request.getParameter(element));
			}
			List items = upload.parseRequest(request);
			System.out.println("Items : " + items);
			System.out.println("Items Size : " + items.size());
			if(items!=null && items.size()>0){
				Iterator it = items.iterator();
				while(it.hasNext()){
					FileItem item = (FileItem)it.next();
					if (!item.isFormField()) {
						map.put(item.getFieldName(), item);					
					} else {
						request.setAttribute(item.getFieldName(), item.getString());
						System.out.println("Field Name Is:-"+item.getFieldName());
						System.out.println("get String is:-"+item.getString());
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			if(logger.isErrorEnabled()) logger.error("Exception occured in UploadHelper.getFileItems() #\n"+ex);			
		}
		return map;		
	}
	
	/**
	 * This method returns the actual file name of the file uploaded
	 * @param name Name property of the FileItem object
	 * @param seperator File Seperator (Use "\\" for windows, "/" for linux)
	 * @return String 
	 */
	private static String parseFileName(String name, String seperator) {
		String fileName = name;
		try {
			if(fileName!=null) {
				while(fileName.contains(seperator)) {					
					fileName = fileName.substring(fileName.indexOf(seperator)+1, fileName.length());
				}
				fileName.replace(" ", "_");
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			fileName = null;
		}
		return fileName;		
	}
	
	/**
	 * This method checks if duplicate file name does not exist and returns unique file name with absolute path
	 * @param fileLocation File location 
	 * @param seperator File Seperator (Use "\\" for windows, "/" for linux)
	 * @param name Name of the file
	 * @return String
	 */
	private static String getUniqueFileInPath(String fileLocation, String seperator, String name) {
		String absolutePath = null;;
		String fileName = null;
		String extension = "";		
		try {
			if(name!=null && name.contains(".")) {
				fileName = name.substring(0,name.indexOf("."));
				extension = name.substring(name.indexOf("."),name.length());			
			}		
			absolutePath=fileLocation+seperator+fileName+extension;
			int count = 1;						
			while(true) {				
				if(new File(absolutePath).exists())
					absolutePath = fileLocation+seperator+fileName+"_"+(++count)+""+extension;
				else
					break;				
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			fileName = name;
		}
		return absolutePath;
	}
	
}
