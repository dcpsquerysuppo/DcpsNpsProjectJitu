package com.tcs.sgv.dcps.valueobject;

import java.util.Date;

public class NoticeBoard {
	
	private Long dcpsNoticeIdPk;
	private String noticeSubject;
	private String issuedBy;
	private Date issuedDate;
	private Date expiryDate;
	private String noticeType;
	private String fieldDeptCode;
	private String fileName;
	private Long PostId;
	private Long UserId;
	private Date CreatedDate;
	private Long UpdatedPostId;
	private Long UpdatedUserId;
	private Date UpdatedDate;

	
	public Long getDcpsNoticeIdPk() {
		return dcpsNoticeIdPk;
	}

	public void setDcpsNoticeIdPk(Long dcpsNoticeIdPk) {
		this.dcpsNoticeIdPk = dcpsNoticeIdPk;
	}
	public String getNoticeSubject() {
		return noticeSubject;
	}

	public void setNoticeSubject(String noticeSubject) {
		this.noticeSubject = noticeSubject;
	}

	public String getIssuedBy() {
		return issuedBy;
	}

	public void setIssuedBy(String issuedBy) {
		this.issuedBy = issuedBy;
	}

	public Date getIssuedDate() {
		return issuedDate;
	}

	public void setIssuedDate(Date issuedDate) {
		this.issuedDate = issuedDate;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getNoticeType() {
		return noticeType;
	}

	public void setNoticeType(String noticeType) {
		this.noticeType = noticeType;
	}

	public String getFieldDeptCode() {
		return fieldDeptCode;
	}

	public void setFieldDeptCode(String fieldDeptCode) {
		this.fieldDeptCode = fieldDeptCode;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Long getPostId() {
		return PostId;
	}

	public void setPostId(Long postId) {
		PostId = postId;
	}

	public Long getUserId() {
		return UserId;
	}

	public void setUserId(Long userId) {
		UserId = userId;
	}

	public Date getCreatedDate() {
		return CreatedDate;
	}

	public void setCreatedDate(Date createdDate) {
		CreatedDate = createdDate;
	}

	public Long getUpdatedPostId() {
		return UpdatedPostId;
	}

	public void setUpdatedPostId(Long updatedPostId) {
		UpdatedPostId = updatedPostId;
	}

	public Long getUpdatedUserId() {
		return UpdatedUserId;
	}

	public void setUpdatedUserId(Long updatedUserId) {
		UpdatedUserId = updatedUserId;
	}

	public Date getUpdatedDate() {
		return UpdatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		UpdatedDate = updatedDate;
	}

}
