package com.tcs.sgv.pensionproc.valueobject;

import java.util.Date;

public class TrnPnsnprocAuthorityDtls {

	private Long authorityDtlsId;	
	private String locationCode;
	private Long inwardPensionId;
	private String flag;
	private String orderNo;
	private Date orderDate;
	private String pnsnrName;
	private Long basicAmt;
	private Long efp;
	private Long fp;
	private Long createdUserId;
    private Long createdPostId;
    private Date createdDate;
    private Long updatedUserId;
    private Long updatedPostId;
    private Date updatedDate;
	
	
	public Long getAuthorityDtlsId() {
		return authorityDtlsId;
	}
	public void setAuthorityDtlsId(Long authorityDtlsId) {
		this.authorityDtlsId = authorityDtlsId;
	}
	public String getLocationCode() {
		return locationCode;
	}
	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}
	public Long getInwardPensionId() {
		return inwardPensionId;
	}
	public void setInwardPensionId(Long inwardPensionId) {
		this.inwardPensionId = inwardPensionId;
	}
	
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getFlag() {
		return flag;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public Date getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}
	public String getPnsnrName() {
		return pnsnrName;
	}
	public void setPnsnrName(String pnsnrName) {
		this.pnsnrName = pnsnrName;
	}
	public Long getBasicAmt() {
		return basicAmt;
	}
	public void setBasicAmt(Long basicAmt) {
		this.basicAmt = basicAmt;
	}
	public Long getEfp() {
		return efp;
	}
	public void setEfp(Long efp) {
		this.efp = efp;
	}
	public Long getFp() {
		return fp;
	}
	public void setFp(Long fp) {
		this.fp = fp;
	}
	public Long getCreatedUserId() {
		return createdUserId;
	}
	public void setCreatedUserId(Long createdUserId) {
		this.createdUserId = createdUserId;
	}
	public Long getCreatedPostId() {
		return createdPostId;
	}
	public void setCreatedPostId(Long createdPostId) {
		this.createdPostId = createdPostId;
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
