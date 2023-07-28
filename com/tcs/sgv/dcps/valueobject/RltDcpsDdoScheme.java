package com.tcs.sgv.dcps.valueobject;

import java.util.Date;

public class RltDcpsDdoScheme  implements java.io.Serializable {

	private Long dcpsDdoSchemesId;
	private String dcpsSchemeCode;
	private String dcpsDdoCode;
	private Long langId;
	private Long locId;
	private Long dbId;
	private Long createdPostId;
	private Long createdUserId;
	private Date createdDate;
	private Long updatedPostId;
	private Long updatedUserId;
	private Date updatedDate;

	public Long getDcpsDdoSchemesId() {
		return dcpsDdoSchemesId;
	}

	public void setDcpsDdoSchemesId(Long dcpsDdoSchemesId) {
		this.dcpsDdoSchemesId = dcpsDdoSchemesId;
	}

	public String getDcpsSchemeCode() {
		return dcpsSchemeCode;
	}

	public void setDcpsSchemeCode(String dcpsSchemeCode) {
		this.dcpsSchemeCode = dcpsSchemeCode;
	}

	public String getDcpsDdoCode() {
		return dcpsDdoCode;
	}

	public void setDcpsDdoCode(String dcpsDdoCode) {
		this.dcpsDdoCode = dcpsDdoCode;
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

	public Long getUpdatedPostId() {
		return updatedPostId;
	}

	public void setUpdatedPostId(Long updatedPostId) {
		this.updatedPostId = updatedPostId;
	}

	public Long getUpdatedUserId() {
		return updatedUserId;
	}

	public void setUpdatedUserId(Long updatedUserId) {
		this.updatedUserId = updatedUserId;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

}
