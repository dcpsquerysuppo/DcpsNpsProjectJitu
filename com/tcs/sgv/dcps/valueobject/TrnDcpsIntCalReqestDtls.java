package com.tcs.sgv.dcps.valueobject;

import java.util.Date;

public class TrnDcpsIntCalReqestDtls implements java.io.Serializable {

	private Long requestDtlsId;
	private String requestId;
	private Long dcpsEmpId;
	private Long status;
	private String reason;
	private String srkaRemarks;
	private Long attachmentId;
	private Long locId;
	private Date createdDate;
	private Long createdPost;
	private Date updatedDate;
	private Long updatedPost;
	
	public TrnDcpsIntCalReqestDtls() {
	}

	
	

	public TrnDcpsIntCalReqestDtls(Long requestDtlsId, String requestId,
			Long dcpsEmpId, Long status, String reason, String srkaRemarks,
			Long attachmentId, Long locId, Date createdDate, Long createdPost,
			Date updatedDate, Long updatedPost) {
		super();
		this.requestDtlsId = requestDtlsId;
		this.requestId = requestId;
		this.dcpsEmpId = dcpsEmpId;
		this.status = status;
		this.reason = reason;
		this.srkaRemarks = srkaRemarks;
		this.attachmentId = attachmentId;
		this.locId = locId;
		this.createdDate = createdDate;
		this.createdPost = createdPost;
		this.updatedDate = updatedDate;
		this.updatedPost = updatedPost;
	}




	public Long getRequestDtlsId() {
		return requestDtlsId;
	}

	public void setRequestDtlsId(Long requestDtlsId) {
		this.requestDtlsId = requestDtlsId;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public Long getDcpsEmpId() {
		return dcpsEmpId;
	}

	public void setDcpsEmpId(Long dcpsEmpId) {
		this.dcpsEmpId = dcpsEmpId;
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getSrkaRemarks() {
		return srkaRemarks;
	}

	public void setSrkaRemarks(String srkaRemarks) {
		this.srkaRemarks = srkaRemarks;
	}

	public Long getAttachmentId() {
		return attachmentId;
	}

	public void setAttachmentId(Long attachmentId) {
		this.attachmentId = attachmentId;
	}

	public Long getLocId() {
		return locId;
	}

	public void setLocId(Long locId) {
		this.locId = locId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Long getCreatedPost() {
		return createdPost;
	}

	public void setCreatedPost(Long createdPost) {
		this.createdPost = createdPost;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Long getUpdatedPost() {
		return updatedPost;
	}

	public void setUpdatedPost(Long updatedPost) {
		this.updatedPost = updatedPost;
	}


}
