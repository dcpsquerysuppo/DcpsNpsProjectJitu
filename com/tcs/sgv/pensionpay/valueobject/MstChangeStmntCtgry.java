/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Aug 2, 2011		376388								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.valueobject;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Class Description - 
 *
 *
 * @author 376388
 * @version 0.1
 * @since JDK 5.0
 * Aug 2, 2011
 */
public class MstChangeStmntCtgry 
{
	private Long changeStmntCtgryId;

	private Integer  categoryId;
	
	private String categoryDesc;

	private Long locationCode;
	
	private BigDecimal createdUserId;

	private BigDecimal createdPostId;

	private Date createdDate;

	private BigDecimal updatedUserId;

	private BigDecimal updatedPostId;

	private Date updatedDate;

	private BigDecimal dbId;

	public MstChangeStmntCtgry()
	{
		
	}
	
	public MstChangeStmntCtgry(Long changeStmntCtgryId, Integer categoryId,
			String categoryDesc, Long locationCode, BigDecimal createdUserId,
			BigDecimal createdPostId, Date createdDate,
			BigDecimal updatedUserId, BigDecimal updatedPostId,
			Date updatedDate, BigDecimal dbId) {
		super();
		this.changeStmntCtgryId = changeStmntCtgryId;
		this.categoryId = categoryId;
		this.categoryDesc = categoryDesc;
		this.locationCode = locationCode;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
		this.dbId = dbId;
	}

	public Long getChangeStmntCtgryId() {
		return changeStmntCtgryId;
	}

	public void setChangeStmntCtgryId(Long changeStmntCtgryId) {
		this.changeStmntCtgryId = changeStmntCtgryId;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryDesc() {
		return categoryDesc;
	}

	public void setCategoryDesc(String categoryDesc) {
		this.categoryDesc = categoryDesc;
	}

	public Long getLocationCode() {
		return locationCode;
	}

	public void setLocationCode(Long locationCode) {
		this.locationCode = locationCode;
	}

	public BigDecimal getCreatedUserId() {
		return createdUserId;
	}

	public void setCreatedUserId(BigDecimal createdUserId) {
		this.createdUserId = createdUserId;
	}

	public BigDecimal getCreatedPostId() {
		return createdPostId;
	}

	public void setCreatedPostId(BigDecimal createdPostId) {
		this.createdPostId = createdPostId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public BigDecimal getUpdatedUserId() {
		return updatedUserId;
	}

	public void setUpdatedUserId(BigDecimal updatedUserId) {
		this.updatedUserId = updatedUserId;
	}

	public BigDecimal getUpdatedPostId() {
		return updatedPostId;
	}

	public void setUpdatedPostId(BigDecimal updatedPostId) {
		this.updatedPostId = updatedPostId;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public BigDecimal getDbId() {
		return dbId;
	}

	public void setDbId(BigDecimal dbId) {
		this.dbId = dbId;
	}
	
	
	
	
	
}
