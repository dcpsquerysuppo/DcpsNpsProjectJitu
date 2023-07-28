package com.tcs.sgv.dcps.valueobject;

import java.util.Date;

public class Feedback {
	
	private Long dcpsFeedbackIdPk;
	private String name;
	private String ddo;
	private String treasury;
	private String deptName;
	private String comments;
	private Long langId;
	private Long locId;
	private Long dbId;
	private Long PostId;
	private Long UserId;
	private Date CreatedDate;
	private Long UpdatedPostId;
	private Long UpdatedUserId;
	private Date UpdatedDate;
	
	private Date feedBackDate;
	private String emailAddress;
	private Long contactNo;

	public Long getDcpsFeedbackIdPk() {
		return dcpsFeedbackIdPk;
	}

	public void setDcpsFeedbackIdPk(Long dcpsFeedbackIdPk) {
		this.dcpsFeedbackIdPk = dcpsFeedbackIdPk;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		
	}
	public String getDdo() {
		return ddo;
	}

	public void setDdo(String ddo) {
		this.ddo = ddo;
		
	}
	public String getTreasury() {
		return treasury;
	}

	public void setTreasury(String treasury) {
		this.treasury = treasury;
		
	}
	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
		
	}
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
		
	}
	public Long getLangId() {

		return langId;
	}

	public void setLangId(Long langId) {

		this.langId = langId;
	}

	public Long getLocId() {

		return locId;
	}

	public void setLocId(Long locId) {

		this.locId = locId;
	}

	public Long getDbId() {

		return dbId;
	}

	public void setDbId(Long dbId) {

		this.dbId = dbId;
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
	
	public Date getFeedBackDate() {
		return feedBackDate;
	}

	public void setFeedBackDate(Date feedBackDate) {
		this.feedBackDate = feedBackDate;
	}
	
	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
		
	}
	public Long getContactNo() {
		return contactNo;
	}

	public void setContactNo(Long contactNo) {
		this.contactNo = contactNo;
	}


}


