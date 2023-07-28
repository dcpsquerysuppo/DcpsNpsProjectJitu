/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 16, 2012		383385								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.valueobject;

import java.io.Serializable;
import java.util.Date;


/**
 * Class Description - 
 *
 *
 * @author 383385
 * @version 0.1
 * @since JDK 5.0
 * Feb 16, 2012
 */
public class StgFileErrorDtls implements Serializable{
	
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = -1506356282520815159L;
	
	private Long stgFileErrorDtlsId;
	private Long delvId;
	private Long fileId;
	private Long transactionId;
	private Long errorCode;
	private String applnNo;
	private Date uploadDate;
	private Long uploadBy;
	private String errorFlag;
	
	public StgFileErrorDtls()
	{}

	/**
	 * 	
	 * @param stgFileErrorDtlsId
	 * @param delvId
	 * @param fileId
	 * @param transactionId
	 * @param errorCode
	 * @param applnNo
	 * @param uploadDate
	 * @param uploadBy
	 * @param errorFlag
	 */
	public StgFileErrorDtls(Long stgFileErrorDtlsId, Long delvId, Long fileId, Long transactionId, Long errorCode, String applnNo, Date uploadDate, Long uploadBy, String errorFlag) {

		super();
		this.stgFileErrorDtlsId = stgFileErrorDtlsId;
		this.delvId = delvId;
		this.fileId = fileId;
		this.transactionId = transactionId;
		this.errorCode = errorCode;
		this.applnNo = applnNo;
		this.uploadDate = uploadDate;
		this.uploadBy = uploadBy;
		this.errorFlag = errorFlag;
	}


	
	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((applnNo == null) ? 0 : applnNo.hashCode());
		result = prime * result + ((delvId == null) ? 0 : delvId.hashCode());
		result = prime * result + ((errorCode == null) ? 0 : errorCode.hashCode());
		result = prime * result + ((errorFlag == null) ? 0 : errorFlag.hashCode());
		result = prime * result + ((fileId == null) ? 0 : fileId.hashCode());
		result = prime * result + ((stgFileErrorDtlsId == null) ? 0 : stgFileErrorDtlsId.hashCode());
		result = prime * result + ((transactionId == null) ? 0 : transactionId.hashCode());
		result = prime * result + ((uploadBy == null) ? 0 : uploadBy.hashCode());
		result = prime * result + ((uploadDate == null) ? 0 : uploadDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StgFileErrorDtls other = (StgFileErrorDtls) obj;
		if (applnNo == null) {
			if (other.applnNo != null)
				return false;
		} else if (!applnNo.equals(other.applnNo))
			return false;
		if (delvId == null) {
			if (other.delvId != null)
				return false;
		} else if (!delvId.equals(other.delvId))
			return false;
		if (errorCode == null) {
			if (other.errorCode != null)
				return false;
		} else if (!errorCode.equals(other.errorCode))
			return false;
		if (errorFlag == null) {
			if (other.errorFlag != null)
				return false;
		} else if (!errorFlag.equals(other.errorFlag))
			return false;
		if (fileId == null) {
			if (other.fileId != null)
				return false;
		} else if (!fileId.equals(other.fileId))
			return false;
		if (stgFileErrorDtlsId == null) {
			if (other.stgFileErrorDtlsId != null)
				return false;
		} else if (!stgFileErrorDtlsId.equals(other.stgFileErrorDtlsId))
			return false;
		if (transactionId == null) {
			if (other.transactionId != null)
				return false;
		} else if (!transactionId.equals(other.transactionId))
			return false;
		if (uploadBy == null) {
			if (other.uploadBy != null)
				return false;
		} else if (!uploadBy.equals(other.uploadBy))
			return false;
		if (uploadDate == null) {
			if (other.uploadDate != null)
				return false;
		} else if (!uploadDate.equals(other.uploadDate))
			return false;
		return true;
	}

	public Long getStgFileErrorDtlsId() {
	
		return stgFileErrorDtlsId;
	}

	
	public void setStgFileErrorDtlsId(Long stgFileErrorDtlsId) {
	
		this.stgFileErrorDtlsId = stgFileErrorDtlsId;
	}

	
	public Long getDelvId() {
	
		return delvId;
	}

	
	public void setDelvId(Long delvId) {
	
		this.delvId = delvId;
	}

	
	public Long getFileId() {
	
		return fileId;
	}

	
	public void setFileId(Long fileId) {
	
		this.fileId = fileId;
	}

	
	public Long getTransactionId() {
	
		return transactionId;
	}

	
	public void setTransactionId(Long transactionId) {
	
		this.transactionId = transactionId;
	}

	
	public Long getErrorCode() {
	
		return errorCode;
	}

	
	public void setErrorCode(Long errorCode) {
	
		this.errorCode = errorCode;
	}

	
	public String getApplnNo() {
	
		return applnNo;
	}

	
	public void setApplnNo(String applnNo) {
	
		this.applnNo = applnNo;
	}

	
	public Date getUploadDate() {
	
		return uploadDate;
	}

	
	public void setUploadDate(Date uploadDate) {
	
		this.uploadDate = uploadDate;
	}

	
	public Long getUploadBy() {
	
		return uploadBy;
	}

	
	public void setUploadBy(Long uploadBy) {
	
		this.uploadBy = uploadBy;
	}

	
	public String getErrorFlag() {
	
		return errorFlag;
	}

	
	public void setErrorFlag(String errorFlag) {
	
		this.errorFlag = errorFlag;
	}
	

}
