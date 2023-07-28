/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 14, 2011		Shripal Soni								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.valueobject;

import java.util.Date;

import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.common.valueobject.CmnLookupMst;

/**
 * Class Description - CmnHolidayMst is the class for common holidays.
 * 
 * @author Shripal Soni
 * @version 0.1
 * @since JDK 5.0 Feb 14, 2011
 */
public class CmnHolidayMst {

	/**
	 * 
	 */
	private Long srNo;
	private Date hldyDt;
	private CmnLookupMst hldyType;
	private String hldyOccsn;
	private Long langId;
	private CmnLocationMst locId;
	private Long createdBy;
	private Long createdByPost;
	private Date createdDate;
	private Long updatedBy;
	private Long updatedByPost;
	private Date updatedDate;
	private String holidayCode;
	private Long dbId;
	private String hldyGreetingMsg;

	public Long getSrNo() {
		return srNo;
	}

	public void setSrNo(Long srNo) {
		this.srNo = srNo;
	}

	public Date getHldyDt() {
		return hldyDt;
	}

	public void setHldyDt(Date hldyDt) {
		this.hldyDt = hldyDt;
	}

	public CmnLookupMst getHldyType() {
		return hldyType;
	}

	public void setHldyType(CmnLookupMst hldyType) {
		this.hldyType = hldyType;
	}

	public String getHldyOccsn() {
		return hldyOccsn;
	}

	public void setHldyOccsn(String hldyOccsn) {
		this.hldyOccsn = hldyOccsn;
	}

	public Long getLangId() {
		return langId;
	}

	public void setLangId(Long langId) {
		this.langId = langId;
	}

	public CmnLocationMst getLocId() {
		return locId;
	}

	public void setLocId(CmnLocationMst locId) {
		this.locId = locId;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Long getCreatedByPost() {
		return createdByPost;
	}

	public void setCreatedByPost(Long createdByPost) {
		this.createdByPost = createdByPost;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Long getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Long getUpdatedByPost() {
		return updatedByPost;
	}

	public void setUpdatedByPost(Long updatedByPost) {
		this.updatedByPost = updatedByPost;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getHolidayCode() {
		return holidayCode;
	}

	public void setHolidayCode(String holidayCode) {
		this.holidayCode = holidayCode;
	}

	public Long getDbId() {
		return dbId;
	}

	public void setDbId(Long dbId) {
		this.dbId = dbId;
	}

	public String getHldyGreetingMsg() {
		return hldyGreetingMsg;
	}

	public void setHldyGreetingMsg(String hldyGreetingMsg) {
		this.hldyGreetingMsg = hldyGreetingMsg;
	}

}
