package com.tcs.sgv.dcps.valueobject;

import java.util.Date;

public class RltBrokenPeriodAllow {
	private Long brokenPeriodAllowId;
	private MstBrokenPeriodPay rltBrokenPeriodId;
	private Long allowCode;
	private Long allowValue;
	private Long langId;
	private Long locId;
	private Long dbId;
	private Long createdPostId;
	private Long createdUserId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	public Long getBrokenPeriodAllowId() {
		return brokenPeriodAllowId;
	}
	public void setBrokenPeriodAllowId(Long brokenPeriodAllowId) {
		this.brokenPeriodAllowId = brokenPeriodAllowId;
	}
	public MstBrokenPeriodPay getRltBrokenPeriodId() {
		return rltBrokenPeriodId;
	}
	public void setRltBrokenPeriodId(MstBrokenPeriodPay rltBrokenPeriodId) {
		this.rltBrokenPeriodId = rltBrokenPeriodId;
	}
	public Long getAllowCode() {
		return allowCode;
	}
	public void setAllowCode(Long allowCode) {
		this.allowCode = allowCode;
	}
	public Long getAllowValue() {
		return allowValue;
	}
	public void setAllowValue(Long allowValue) {
		this.allowValue = allowValue;
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
}
