package com.tcs.sgv.common.valueobject;

// Generated May 28, 2007 3:37:06 PM by Hibernate Tools 3.2.0.beta8

import java.util.Date;

/**
 * MstObjection generated by hbm2java
 */
public class MstObjection implements java.io.Serializable {

	// Fields    

	private long objectionId;

	private String objectionDesc;

	private String objectionCode;

	private Long langId;

	private long activateFlag;

	private Date startDate;

	private Date endDate;

	private long createdUserId;

	private long createdPostId;

	private Date createdDate;

	private Long updatedUserId;

	private Long updatedPostId;

	private Date updatedDate;

	private Long dbId;
	
	private String locationCode;
	
	private String moduleName;

	// Constructors

	/** default constructor */
	public MstObjection() {
	}

	/** minimal constructor */
	public MstObjection(long objectionId, String objectionDesc,
			String objectionCode, long activateFlag, long createdUserId,
			long createdPostId, String locationCode, Date createdDate, Date startDate) {
		this.objectionId = objectionId;
		this.objectionDesc = objectionDesc;
		this.objectionCode = objectionCode;
		this.activateFlag = activateFlag;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.locationCode = locationCode;
		this.createdDate = createdDate;
		this.startDate = startDate;
	}

	/** full constructor */
	public MstObjection(long objectionId, String objectionDesc,
			String objectionCode, Long langId, long activateFlag,
			Date startDate, Date endDate, long createdUserId,
			long createdPostId, Date createdDate, Long updatedUserId,
			Long updatedPostId, Date updatedDate, Long dbId, String locationCode, String moduleName) {
		this.objectionId = objectionId;
		this.objectionDesc = objectionDesc;
		this.objectionCode = objectionCode;
		this.langId = langId;
		this.activateFlag = activateFlag;
		this.startDate = startDate;
		this.endDate = endDate;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
		this.dbId = dbId;
		this.locationCode = locationCode;
		this.moduleName = moduleName;
	}

	// Property accessors
	public long getObjectionId() {
		return this.objectionId;
	}

	public void setObjectionId(long objectionId) {
		this.objectionId = objectionId;
	}

	public String getObjectionDesc() {
		return this.objectionDesc;
	}

	public void setObjectionDesc(String objectionDesc) {
		this.objectionDesc = objectionDesc;
	}

	public String getObjectionCode() {
		return this.objectionCode;
	}

	public void setObjectionCode(String objectionCode) {
		this.objectionCode = objectionCode;
	}

	public Long getLangId() {
		return this.langId;
	}

	public void setLangId(Long langId) {
		this.langId = langId;
	}

	public long getActivateFlag() {
		return this.activateFlag;
	}

	public void setActivateFlag(long activateFlag) {
		this.activateFlag = activateFlag;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public long getCreatedUserId() {
		return this.createdUserId;
	}

	public void setCreatedUserId(long createdUserId) {
		this.createdUserId = createdUserId;
	}

	public long getCreatedPostId() {
		return this.createdPostId;
	}

	public void setCreatedPostId(long createdPostId) {
		this.createdPostId = createdPostId;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Long getUpdatedUserId() {
		return this.updatedUserId;
	}

	public void setUpdatedUserId(Long updatedUserId) {
		this.updatedUserId = updatedUserId;
	}

	public Long getUpdatedPostId() {
		return this.updatedPostId;
	}

	public void setUpdatedPostId(Long updatedPostId) {
		this.updatedPostId = updatedPostId;
	}

	public Date getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Long getDbId() {
		return this.dbId;
	}

	public void setDbId(Long dbId) {
		this.dbId = dbId;
	}
	public String getLocationCode() {
		return this.locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}
	public String getModuleName() {
		return this.moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
}
