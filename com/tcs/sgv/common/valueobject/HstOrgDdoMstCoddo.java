// default package
// Generated Sep 18, 2009 3:53:28 PM by Hibernate Tools 3.2.0.beta8
package com.tcs.sgv.common.valueobject;
import java.util.Date;

/**
 * HstOrgDdoMstCoddo generated by hbm2java
 */
public class HstOrgDdoMstCoddo implements java.io.Serializable {

	// Fields    

	private HstOrgDdoMstCoddoId id;

	private String ddoCode;

	private String ddoName;

	private Integer postId;

	private Long attachmentId;

	private Byte langId;

	private Date startDate;

	private Date endDate;

	private Byte activateFlag;

	private Integer createdBy;

	private Integer createdByPost;

	private Date createdDate;

	private Integer updatedBy;

	private Integer updatedByPost;

	private Date updatedDate;

	private Short dbId;

	private String shortName;

	private String majorHead;

	private String demand;

	private Short ddoNo;

	private Short cardexNo;

	private Byte adminFlag;

	private String officeCode;

	private String locationCode;

	private String deptLocCode;

	private String hodLocCode;

	private Byte isCo;

	private Byte isCs;

	private Byte type;

	private boolean verified;

	private String dsgnCode;

	private String dsgnName;

	private String ddoOffice;

	private Date verifiedDate;

	private String subOfficeCode;

	private String attachedParentLocCode;

	private String officeUniqueCode;

	private String remarks;
	
	private Long fixedid;
	
	public Long getFixedid() {
		return fixedid;
	}

	public void setFixedid(Long fixedid) {
		this.fixedid = fixedid;
	}

	// Constructors

	/** default constructor */
	public HstOrgDdoMstCoddo() {
	}

	/** minimal constructor */
	public HstOrgDdoMstCoddo(HstOrgDdoMstCoddoId id, String ddoCode,
			Byte langId, Date startDate, Byte activateFlag, Integer createdBy,
			Integer createdByPost, Date createdDate, Short dbId, Byte adminFlag,
			String officeCode, String locationCode, Byte isCo, Byte isCs,
			Byte type, boolean verified) {
		this.id = id;
		this.ddoCode = ddoCode;
		this.langId = langId;
		this.startDate = startDate;
		this.activateFlag = activateFlag;
		this.createdBy = createdBy;
		this.createdByPost = createdByPost;
		this.createdDate = createdDate;
		this.dbId = dbId;
		this.adminFlag = adminFlag;
		this.officeCode = officeCode;
		this.locationCode = locationCode;
		this.isCo = isCo;
		this.isCs = isCs;
		this.type = type;
		this.verified = verified;
	}

	/** full constructor */
	public HstOrgDdoMstCoddo(HstOrgDdoMstCoddoId id, String ddoCode,
			String ddoName, Integer postId, Long attachmentId, Byte langId,
			Date startDate, Date endDate, Byte activateFlag, Integer createdBy,
			Integer createdByPost, Date createdDate, Integer updatedBy,
			Integer updatedByPost, Date updatedDate, Short dbId,
			String shortName, String majorHead, String demand, Short ddoNo,
			Short cardexNo, Byte adminFlag, String officeCode,
			String locationCode, String deptLocCode, String hodLocCode,
			Byte isCo, Byte isCs, Byte type, boolean verified, String dsgnCode,
			String dsgnName, String ddoOffice, Date verifiedDate,
			String subOfficeCode, String attachedParentLocCode,
			String officeUniqueCode, String remarks) {
		this.id = id;
		this.ddoCode = ddoCode;
		this.ddoName = ddoName;
		this.postId = postId;
		this.attachmentId = attachmentId;
		this.langId = langId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.activateFlag = activateFlag;
		this.createdBy = createdBy;
		this.createdByPost = createdByPost;
		this.createdDate = createdDate;
		this.updatedBy = updatedBy;
		this.updatedByPost = updatedByPost;
		this.updatedDate = updatedDate;
		this.dbId = dbId;
		this.shortName = shortName;
		this.majorHead = majorHead;
		this.demand = demand;
		this.ddoNo = ddoNo;
		this.cardexNo = cardexNo;
		this.adminFlag = adminFlag;
		this.officeCode = officeCode;
		this.locationCode = locationCode;
		this.deptLocCode = deptLocCode;
		this.hodLocCode = hodLocCode;
		this.isCo = isCo;
		this.isCs = isCs;
		this.type = type;
		this.verified = verified;
		this.dsgnCode = dsgnCode;
		this.dsgnName = dsgnName;
		this.ddoOffice = ddoOffice;
		this.verifiedDate = verifiedDate;
		this.subOfficeCode = subOfficeCode;
		this.attachedParentLocCode = attachedParentLocCode;
		this.officeUniqueCode = officeUniqueCode;
		this.remarks = remarks;
	}

	// Property accessors
	public HstOrgDdoMstCoddoId getId() {
		return this.id;
	}

	public void setId(HstOrgDdoMstCoddoId id) {
		this.id = id;
	}

	public String getDdoCode() {
		return this.ddoCode;
	}

	public void setDdoCode(String ddoCode) {
		this.ddoCode = ddoCode;
	}

	public String getDdoName() {
		return this.ddoName;
	}

	public void setDdoName(String ddoName) {
		this.ddoName = ddoName;
	}

	public Integer getPostId() {
		return this.postId;
	}

	public void setPostId(Integer postId) {
		this.postId = postId;
	}

	public Long getAttachmentId() {
		return this.attachmentId;
	}

	public void setAttachmentId(Long attachmentId) {
		this.attachmentId = attachmentId;
	}

	public Byte getLangId() {
		return this.langId;
	}

	public void setLangId(Byte langId) {
		this.langId = langId;
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

	public Byte getActivateFlag() {
		return this.activateFlag;
	}

	public void setActivateFlag(Byte activateFlag) {
		this.activateFlag = activateFlag;
	}

	public Integer getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Integer getCreatedByPost() {
		return this.createdByPost;
	}

	public void setCreatedByPost(Integer createdByPost) {
		this.createdByPost = createdByPost;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Integer getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(Integer updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Integer getUpdatedByPost() {
		return this.updatedByPost;
	}

	public void setUpdatedByPost(Integer updatedByPost) {
		this.updatedByPost = updatedByPost;
	}

	public Date getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Short getDbId() {
		return this.dbId;
	}

	public void setDbId(Short dbId) {
		this.dbId = dbId;
	}

	public String getShortName() {
		return this.shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getMajorHead() {
		return this.majorHead;
	}

	public void setMajorHead(String majorHead) {
		this.majorHead = majorHead;
	}

	public String getDemand() {
		return this.demand;
	}

	public void setDemand(String demand) {
		this.demand = demand;
	}

	public Short getDdoNo() {
		return this.ddoNo;
	}

	public void setDdoNo(Short ddoNo) {
		this.ddoNo = ddoNo;
	}

	public Short getCardexNo() {
		return this.cardexNo;
	}

	public void setCardexNo(Short cardexNo) {
		this.cardexNo = cardexNo;
	}

	public Byte getAdminFlag() {
		return this.adminFlag;
	}

	public void setAdminFlag(Byte adminFlag) {
		this.adminFlag = adminFlag;
	}

	public String getOfficeCode() {
		return this.officeCode;
	}

	public void setOfficeCode(String officeCode) {
		this.officeCode = officeCode;
	}

	public String getLocationCode() {
		return this.locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

	public String getDeptLocCode() {
		return this.deptLocCode;
	}

	public void setDeptLocCode(String deptLocCode) {
		this.deptLocCode = deptLocCode;
	}

	public String getHodLocCode() {
		return this.hodLocCode;
	}

	public void setHodLocCode(String hodLocCode) {
		this.hodLocCode = hodLocCode;
	}

	public Byte getIsCo() {
		return this.isCo;
	}

	public void setIsCo(Byte isCo) {
		this.isCo = isCo;
	}

	public Byte getIsCs() {
		return this.isCs;
	}

	public void setIsCs(Byte isCs) {
		this.isCs = isCs;
	}

	public Byte getType() {
		return this.type;
	}

	public void setType(Byte type) {
		this.type = type;
	}

	public boolean isVerified() {
		return this.verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public String getDsgnCode() {
		return this.dsgnCode;
	}

	public void setDsgnCode(String dsgnCode) {
		this.dsgnCode = dsgnCode;
	}

	public String getDsgnName() {
		return this.dsgnName;
	}

	public void setDsgnName(String dsgnName) {
		this.dsgnName = dsgnName;
	}

	public String getDdoOffice() {
		return this.ddoOffice;
	}

	public void setDdoOffice(String ddoOffice) {
		this.ddoOffice = ddoOffice;
	}

	public Date getVerifiedDate() {
		return this.verifiedDate;
	}

	public void setVerifiedDate(Date verifiedDate) {
		this.verifiedDate = verifiedDate;
	}

	public String getSubOfficeCode() {
		return this.subOfficeCode;
	}

	public void setSubOfficeCode(String subOfficeCode) {
		this.subOfficeCode = subOfficeCode;
	}

	public String getAttachedParentLocCode() {
		return this.attachedParentLocCode;
	}

	public void setAttachedParentLocCode(String attachedParentLocCode) {
		this.attachedParentLocCode = attachedParentLocCode;
	}

	public String getOfficeUniqueCode() {
		return this.officeUniqueCode;
	}

	public void setOfficeUniqueCode(String officeUniqueCode) {
		this.officeUniqueCode = officeUniqueCode;
	}

	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}