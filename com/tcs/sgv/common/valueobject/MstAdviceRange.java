package com.tcs.sgv.common.valueobject;

import java.util.Date;

// Generated January 08, 2008 15:41:26 PM by Hibernate Tools 3.2.0.beta8
/**
 * com.tcs.sgv.common.valueobject.MstAdviceRange
 * 
 * This is Java bean for getting Advice Range for different Post of outward
 * counter on different treasury locations
 * 
 * Date of Creation : 8th January 2008
 * 
 * Author : Hiral Shsh
 * 
 * Revision History =====================
 * 
 */
public class MstAdviceRange implements java.io.Serializable {
	// Fields

	private long adviceId;

	private long startAdviceNo;

	private long endAdviceNo;

	private long postId;

	private String locationCode;

	private long createdUserId;

	private long createdPostId;

	private Date createdDate;

	private Long updatedPostId;

	private Long updatedUserId;

	private Date updatedDate;

	private Long dbId;

	// Constructors

	/** default constructor */
	public MstAdviceRange() {
	}

	/** minimal constructor */
	public MstAdviceRange(long adviceId, long createdUserId,
			long createdPostId, Date createdDate) {
		this.adviceId = adviceId;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
	}

	/** full constructor */
	public MstAdviceRange(long adviceId, long startAdviceNo, long endAdviceNo,
			long postId, long createdUserId, long createdPostId,
			Date createdDate, Long updatedPostId, Long updatedUserId,
			Date updatedDate, Long dbId, String locationCode) {
		this.adviceId = adviceId;
		this.startAdviceNo = startAdviceNo;
		this.endAdviceNo = endAdviceNo;
		this.postId = postId;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedPostId = updatedPostId;
		this.updatedUserId = updatedUserId;
		this.updatedDate = updatedDate;
		this.dbId = dbId;
		this.locationCode = locationCode;
	}

	// Property accessors
	public long getAdviceId() {
		return this.adviceId;
	}

	public void setAdviceId(long adviceId) {
		this.adviceId = adviceId;
	}

	public long getStartAdviceNo() {
		return this.startAdviceNo;
	}

	public void setStartAdviceNo(long startAdviceNo) {
		this.startAdviceNo = startAdviceNo;
	}

	public long getEndAdviceNo() {
		return this.endAdviceNo;
	}

	public void setEndAdviceNo(long endAdviceNo) {
		this.endAdviceNo = endAdviceNo;
	}

	public long getPostId() {
		return this.postId;
	}

	public void setPostId(long postId) {
		this.postId = postId;
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

	public Long getUpdatedPostId() {
		return this.updatedPostId;
	}

	public void setUpdatedPostId(Long updatedPostId) {
		this.updatedPostId = updatedPostId;
	}

	public Long getUpdatedUserId() {
		return this.updatedUserId;
	}

	public void setUpdatedUserId(Long updatedUserId) {
		this.updatedUserId = updatedUserId;
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
		return locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}
}
