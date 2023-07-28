package com.tcs.sgv.lna.valueobject;

import java.util.Date;

public class MstLnaDocChecklist {

	private Long MstLnaDocChecklistId;
	private String checklistName;
	private String sevaarthID;
	private Long lnaReqType;
	private Long reqSubType;
	private String checked;
	private Long createdPostId;
	private Long createdUserId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;

	public Long getMstLnaDocChecklistId() {
		return MstLnaDocChecklistId;
	}

	public void setMstLnaDocChecklistId(Long mstLnaDocChecklistId) {
		MstLnaDocChecklistId = mstLnaDocChecklistId;
	}

	public String getChecklistName() {
		return checklistName;
	}

	public void setChecklistName(String checklistName) {
		this.checklistName = checklistName;
	}

	public String getSevaarthID() {
		return sevaarthID;
	}

	public void setSevaarthID(String sevaarthID) {
		this.sevaarthID = sevaarthID;
	}

	public Long getLnaReqType() {
		return lnaReqType;
	}

	public void setLnaReqType(Long lnaReqType) {
		this.lnaReqType = lnaReqType;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}

	public String getChecked() {
		return checked;
	}

	public Long getCreatedPostId() {
		return createdPostId;
	}

	public void setCreatedPostId(Long createdPostId) {
		this.createdPostId = createdPostId;
	}

	public Long getCreatedUserId() {
		return createdUserId;
	}

	public void setCreatedUserId(Long createdUserId) {
		this.createdUserId = createdUserId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Long getUpdatedUserId() {
		return updatedUserId;
	}

	public void setUpdatedUserId(Long updatedUserId) {
		this.updatedUserId = updatedUserId;
	}

	public Long getUpdatedPostId() {
		return updatedPostId;
	}

	public void setUpdatedPostId(Long updatedPostId) {
		this.updatedPostId = updatedPostId;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public void setReqSubType(Long reqSubType) {
		this.reqSubType = reqSubType;
	}

	public Long getReqSubType() {
		return reqSubType;
	}

}
