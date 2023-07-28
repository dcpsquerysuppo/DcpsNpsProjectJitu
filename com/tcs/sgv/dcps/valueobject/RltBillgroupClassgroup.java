package com.tcs.sgv.dcps.valueobject;

import java.util.Date;

public class RltBillgroupClassgroup implements java.io.Serializable {

	private Long dcpsBillClassGroupId;	
	private	Long dcpsBillGroupId;
	private String dcpsClassGroup;
	private Long LangId;
	private Long LocId;
	private Long DbId;
	private Long PostId;
	private Long UserId;	
	private Date CreatedDate;
	private Long UpdatedPostId;
	private Long UpdatedUserId;
	private Date UpdatedDate;
	
	
	public Long getDcpsBillClassGroupId() {
		return dcpsBillClassGroupId;
	}
	public void setDcpsBillClassGroupId(Long dcpsBillClassGroupId) {
		this.dcpsBillClassGroupId = dcpsBillClassGroupId;
	}
	public Long getDcpsBillGroupId() {
		return dcpsBillGroupId;
	}
	public void setDcpsBillGroupId(Long dcpsBillGroupId) {
		this.dcpsBillGroupId = dcpsBillGroupId;
	}
	public String getDcpsClassGroup() {
		return dcpsClassGroup;
	}
	public void setDcpsClassGroup(String arrClassGroup) {
		//String[] arrClass = arrClassGroup.split(",");
		//this.dcpsClassGroup = arrClass;
		this.dcpsClassGroup = arrClassGroup;
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
	
}
