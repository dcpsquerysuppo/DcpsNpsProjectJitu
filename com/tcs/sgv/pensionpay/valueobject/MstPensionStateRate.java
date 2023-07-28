package com.tcs.sgv.pensionpay.valueobject;

import java.math.BigDecimal;
import java.util.Date;


public class MstPensionStateRate 
{

	private Long pensionStateRateId;

	private BigDecimal dbId;
	
	private BigDecimal stateCode;

	private String stateDesc;
	
	private Long langId;
	
	private BigDecimal createdUserId;

	private BigDecimal createdPostId;

	private Date createdDate;

	private BigDecimal updatedUserId;

	private BigDecimal updatedPostId;

	private Date updatedDate;

	public MstPensionStateRate()
	{
		
	}

	public MstPensionStateRate(Long pensionStateRateId, BigDecimal dbId, BigDecimal stateCode, String stateDesc, Long langId,BigDecimal createdUserId, BigDecimal createdPostId,
			Date createdDate, BigDecimal updatedUserId, BigDecimal updatedPostId, Date updatedDate) {

		super();
		this.pensionStateRateId = pensionStateRateId;
		this.dbId = dbId;
		this.stateCode = stateCode;
		this.stateDesc = stateDesc;
		this.langId = langId;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
	}

	
	public Long getPensionStateRateId() {
	
		return pensionStateRateId;
	}

	
	public void setPensionStateRateId(Long pensionStateRateId) {
	
		this.pensionStateRateId = pensionStateRateId;
	}

	
	public BigDecimal getDbId() {
	
		return dbId;
	}

	
	public void setDbId(BigDecimal dbId) {
	
		this.dbId = dbId;
	}

	
	public BigDecimal getStateCode() {
	
		return stateCode;
	}

	
	public void setStateCode(BigDecimal stateCode) {
	
		this.stateCode = stateCode;
	}

	
	public String getStateDesc() {
	
		return stateDesc;
	}

	
	public void setStateDesc(String stateDesc) {
	
		this.stateDesc = stateDesc;
	}


	public Long getLangId() {
	
		return langId;
	}

	
	public void setLangId(Long langId) {
	
		this.langId = langId;
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
	
	

}
