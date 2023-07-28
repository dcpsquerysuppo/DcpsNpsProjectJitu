package com.tcs.sgv.common.util.upload;

import java.io.File;

public class UploadObject {
	public static int SUCCESS=1;
	public static int FAILURE=-1;
	public static int NO_RESTRICT=0;
	
	private String uploadName;
	private String fileName;
	private String fileLocation;
	private long size;
	private long restrictSize;
	private int statusCode;
	
	public UploadObject(){
		this.setStatusCode(UploadObject.FAILURE);
		this.setRestrictSize(UploadObject.NO_RESTRICT);
	}
	
	public String getUploadName() {
		return uploadName;
	}

	public void setUploadName(String uploadName) {
		this.uploadName = uploadName;
	}

	public String getFileLocation() {
		return fileLocation;
	}
	
	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	
	public long getRestrictSize() {
		return restrictSize;
	}

	public void setRestrictSize(long restrictSize) {
		this.restrictSize = restrictSize;
	}
	
	public int getStatusCode() {
		return statusCode;
	}
	
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
}
