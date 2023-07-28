package com.tcs.sgv.dcps.valueobject;

import java.util.Date;

public class MstDcpsBillGroup implements java.io.Serializable {

	private Long dcpsDdoBillGroupId;
	private String dcpsDdoCode;
	private String dcpsDdoBillDescription;
	private String dcpsDdoBillSchemeName;
	private String dcpsDdoSchemeCode;
	private String dcpsDdoBillTypeOfPost;
	private Long subBGOrNot;
	private Long LangId;
	private Long LocId;
	private Long DbId;
	private Long PostId;
	private Long UserId;
	private Date CreatedDate;
	private Long UpdatedPostId;
	private Long UpdatedUserId;
	private Date UpdatedDate;
	private Long billNo;
	
	private Character billDeleted;
	private Character billDcps;
	private String typeOfBill;
	
	public String getTypeOfBill() {
		return typeOfBill;
	}
	public void setTypeOfBill(String typeOfBill) {
		this.typeOfBill = typeOfBill;
	}
	public Long getDcpsDdoBillGroupId() {
		return dcpsDdoBillGroupId;
	}

	public void setDcpsDdoBillGroupId(Long dcpsDdoBillGroupId) {
		this.dcpsDdoBillGroupId = dcpsDdoBillGroupId;
	}

	public String getDcpsDdoCode() {
		return dcpsDdoCode;
	}

	public void setDcpsDdoCode(String dcpsDdoCode) {
		this.dcpsDdoCode = dcpsDdoCode;
	}

	public String getDcpsDdoBillDescription() {
		return dcpsDdoBillDescription;
	}

	public void setDcpsDdoBillDescription(String dcpsDdoBillDescription) {
		this.dcpsDdoBillDescription = dcpsDdoBillDescription;
	}

	public String getDcpsDdoBillSchemeName() {
		return dcpsDdoBillSchemeName;
	}

	public void setDcpsDdoBillSchemeName(String dcpsDdoBillSchemeName) {
		this.dcpsDdoBillSchemeName = dcpsDdoBillSchemeName;
	}

	public String getDcpsDdoSchemeCode() {
		return dcpsDdoSchemeCode;
	}

	public void setDcpsDdoSchemeCode(String dcpsDdoSchemeCode) {
		this.dcpsDdoSchemeCode = dcpsDdoSchemeCode;
	}

	public String getDcpsDdoBillTypeOfPost() {
		return dcpsDdoBillTypeOfPost;
	}

	public void setDcpsDdoBillTypeOfPost(String dcpsDdoBillTypeOfPost) {
		this.dcpsDdoBillTypeOfPost = dcpsDdoBillTypeOfPost;
	}
	
	public Long getSubBGOrNot() {
		return subBGOrNot;
	}

	public void setSubBGOrNot(Long subBGOrNot) {
		this.subBGOrNot = subBGOrNot;
	}
	
	public Long getLangId() {
		return LangId;
	}

	public void setLangId(Long langId) {
		LangId = langId;
	}

	public Long getLocId() {
		return LocId;
	}

	public void setLocId(Long locId) {
		LocId = locId;
	}

	public Long getDbId() {
		return DbId;
	}

	public void setDbId(Long dbId) {
		DbId = dbId;
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
	
	public Long getBillNo() {
		return billNo;
	}

	public void setBillNo(Long billNo) {
		this.billNo = billNo;
	}
	
	public Character getBillDeleted() {

		return billDeleted;
	}

	public void setBillDeleted(Character billDeleted) {

		this.billDeleted = billDeleted;
	}
	
	public Character getBillDcps() {

		return billDcps;
	}

	public void setBillDcps(Character billDcps) {

		this.billDcps = billDcps;
	}
	
}
