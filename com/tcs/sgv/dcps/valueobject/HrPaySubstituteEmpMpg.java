package com.tcs.sgv.dcps.valueobject;
import java.util.Date;

public class HrPaySubstituteEmpMpg implements java.io.Serializable {
	private long subEmpMapId;
  	private long subLookupId;
	private long subPostId ;
	private long empPostId ;
	private Date createdDate ;
	private Date updatedDate;
	private int activateFlag;
	private Date startDate;
	private Date endDate ;
	private long createdByUser;
	private long createdByPost;
	
	public HrPaySubstituteEmpMpg() {
		
	}
	
	public HrPaySubstituteEmpMpg(long subEmpMapId, long subLookupId,
			long subPostId, long empPostId, Date createdDate, Date updatedDate,
			int activateFlag, Date startDate, Date endDate) {
		super();
		this.subEmpMapId = subEmpMapId;
		this.subLookupId = subLookupId;
		this.subPostId = subPostId;
		this.empPostId = empPostId;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		this.activateFlag = activateFlag;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public HrPaySubstituteEmpMpg(long subEmpMapId, long subLookupId,
			long subPostId, long empPostId, Date createdDate, Date updatedDate,
			int activateFlag, Date startDate, Date endDate, long createdByUser,
			long createdByPost) {
		super();
		this.subEmpMapId = subEmpMapId;
		this.subLookupId = subLookupId;
		this.subPostId = subPostId;
		this.empPostId = empPostId;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		this.activateFlag = activateFlag;
		this.startDate = startDate;
		this.endDate = endDate;
		this.createdByUser = createdByUser;
		this.createdByPost = createdByPost;
	}

	public long getSubEmpMapId() {
		return subEmpMapId;
	}
	public void setSubEmpMapId(long subEmpMapId) {
		this.subEmpMapId = subEmpMapId;
	}
	public long getSubLookupId() {
		return subLookupId;
	}
	public void setSubLookupId(long subLookupId) {
		this.subLookupId = subLookupId;
	}
	public long getSubPostId() {
		return subPostId;
	}
	public void setSubPostId(long subPostId) {
		this.subPostId = subPostId;
	}
	public long getEmpPostId() {
		return empPostId;
	}
	public void setEmpPostId(long empPostId) {
		this.empPostId = empPostId;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Date getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	public int getActivateFlag() {
		return activateFlag;
	}
	public void setActivateFlag(int activateFlag) {
		this.activateFlag = activateFlag;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public long getcreatedByUser() {
		return createdByUser;
	}
	public void setcreatedByUser(long createdByUser) {
		this.createdByUser = createdByUser;
	}
	public long getCreatedByPost() {
		return createdByPost;
	}
	public void setCreatedByPost(long createdByPost) {
		this.createdByPost = createdByPost;
	}	
	
	
}
