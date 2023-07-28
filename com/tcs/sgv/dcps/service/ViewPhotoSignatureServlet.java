package com.tcs.sgv.dcps.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import com.tcs.sgv.common.utils.DBUtility;

/**
 * Servlet implementation class for Servlet: ViewPhotoSignature
 * 
 */
public class ViewPhotoSignatureServlet extends javax.servlet.http.HttpServlet
		implements javax.servlet.Servlet {

	private final static Logger gLogger = Logger
			.getLogger(ViewPhotoSignatureServlet.class);

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public ViewPhotoSignatureServlet() {
		super();
	}

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request,
	 * HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request,
	 * HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		gLogger.info("ViewPhotoSignatureServlet start...............");

		OutputStream lOutStream = null;
		List items = null;

		File fileDir = null;
		String filePath = null;
		File file = null;
		OutputStream fos = null;

		String fileDescription1 = null;
		String redirect = null;

		FileItemFactory factory = null;
		ServletFileUpload upload = null;

		Iterator iter = null;
		InputStream is = null;

		FileItem item1 = null;
		byte[] barray = null;
		int result = 0;
		try {

			response
					.setContentType("text/html;charset=utf-8;application/octet-stream;application/msword;text/plain;application/ms-excel");
			response.setHeader("Cache-Control", "no-cache");

			lOutStream = response.getOutputStream();

			gLogger.info("\n\n\n\n servlet start..... \n\n\n");

			fileDescription1 = "";
			redirect = request.getParameter("redirect");

			try {
				fileDescription1 = request.getParameter("descscan");
				gLogger.info("fileDescription1 : " + fileDescription1);
			} catch (Exception e) {
				gLogger.info("ViewPhotoSignature : File Description error" + e,
						e);
			}
			gLogger
					.info("UploadServlet:  In the beginning of file upload.......................");

			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			gLogger.info("getContentType : " + request.getContentType());
			gLogger.info("isMultipart : " + isMultipart);

			factory = new DiskFileItemFactory();

			// Create a new file upload handler
			upload = new ServletFileUpload(factory);

			items = upload.parseRequest(request);

			gLogger.info("\n\n\n====items===" + items);

			iter = items.iterator();

			iter = items.iterator();

			while (iter.hasNext()) {
				item1 = (FileItem) iter.next();
				gLogger.info("item1 : " + item1);
				if (!item1.isFormField()) {

					is = item1.getInputStream();
					int n = is.available();
					while (n > 0) {
						barray = new byte[n];
						result = is.read(barray);
						if (result == -1) {
							break;
						}
						n = is.available();
					}

					try {
						if (redirect.equalsIgnoreCase("false")) {
							lOutStream.write(barray);
							lOutStream.flush();
						} else {
							fileDir = new File(request
									.getRealPath("UPLOADED-FILES"));
							if (!fileDir.exists()) {
								fileDir.mkdir();
							}
							filePath = request.getRealPath("UPLOADED-FILES")
									+ File.separator
									+ "PFFile"
									+ DBUtility.getCurrentDateFromDB()
											.getTime() + ".txt";

							file = new File(filePath);
							fos = new FileOutputStream(file);
							if (barray != null) {
								fos.write(barray);
								fos.flush();
								fos.close();
							}
							gLogger.info("brfore redirect");
							response
									.sendRedirect(response
											.encodeRedirectURL(request
													.getContextPath()
													+ "/ifms.htm?actionFlag=uploadPayFixationFile&filePath="
													+ filePath));
						}
					} catch (IOException ex) {
						gLogger.info("EX : " + ex, ex);
					}

					is.close();
					gLogger.info("barray : " + barray);
				}
			}
		} catch (Exception e) {
			gLogger
					.error(
							"Exception occured in viewPhotoSignature exception is "
									+ e, e);
		} finally {
			if (lOutStream != null) {
				lOutStream.close();
				lOutStream = null;
			}
			items = null;

			fileDir = null;
			filePath = null;
			file = null;
			if (fos != null) {
				fos.close();
				fos = null;
			}

			fileDescription1 = null;
			redirect = null;

			factory = null;
			upload = null;

			iter = null;
			if (is != null) {
				is.close();
				is = null;
			}

			item1 = null;
			barray = null;
		}
	}
}