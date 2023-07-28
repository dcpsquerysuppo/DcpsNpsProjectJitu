package com.tcs.sgv.pensionpay.valueobject;

import java.math.BigDecimal;
import java.util.Date;


public class MstPensionMainCategory 
{

	private Long mainCatId;

	private String mainCatDesc;
	
	private String schemeCodePension;
	
	private String schemeCodeCVP;
	
	private String schemeCodeDCRG;

	private BigDecimal langId;

	private BigDecimal createdUserId;

	private BigDecimal createdPostId;

	private Date createdDate;

	private BigDecimal updatedUserId;

	private BigDecimal updatedPostId;

	private Date updatedDate;
	
	public MstPensionMainCategory()
	{
		
	}

	public MstPensionMainCategory(Long mainCatId, String mainCatDesc, String schemeCodePension, String schemeCodeCVP, String schemeCodeDCRG, BigDecimal langId, BigDecimal createdUserId,
			BigDecimal createdPostId, Date createdDate, BigDecimal updatedUserId, BigDecimal updatedPostId, Date updatedDate) {

		super();
		this.mainCatId = mainCatId;
		this.mainCatDesc = mainCatDesc;
		this.schemeCodePension = schemeCodePension;
		this.schemeCodeCVP = schemeCodeCVP;
		this.schemeCodeDCRG = schemeCodeDCRG;
		this.langId = langId;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
	}

	
	public Long getMainCatId() {
	
		return mainCatId;
	}

	
	public void setMainCatId(Long mainCatId) {
	
		this.mainCatId = mainCatId;
	}

	
	public String getMainCatDesc() {
	
		return mainCatDesc;
	}

	
	public void setMainCatDesc(String mainCatDesc) {
	
		this.mainCatDesc = mainCatDesc;
	}

	
	public String getSchemeCodePension() {
	
		return schemeCodePension;
	}

	
	public void setSchemeCodePension(String schemeCodePension) {
	
		this.schemeCodePension = schemeCodePension;
	}

	
	public String getSchemeCodeCVP() {
	
		return schemeCodeCVP;
	}

	
	public void setSchemeCodeCVP(String schemeCodeCVP) {
	
		this.schemeCodeCVP = schemeCodeCVP;
	}

	
	public String getSchemeCodeDCRG() {
	
		return schemeCodeDCRG;
	}

	
	public void setSchemeCodeDCRG(String schemeCodeDCRG) {
	
		this.schemeCodeDCRG = schemeCodeDCRG;
	}

	
	public BigDecimal getLangId() {
	
		return langId;
	}

	
	public void setLangId(BigDecimal langId) {
	
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