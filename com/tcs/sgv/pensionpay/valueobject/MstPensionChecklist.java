/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Aug 4, 2011		Shivani Rana								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.valueobject;

import java.util.Date;


/**
 * Class Description -
 * 
 * 
 * @author 365450
 * @version 0.1
 * @since JDK 5.0 Aug 4, 2011
 */
public class MstPensionChecklist {

	private Long docId;

	private String docDesc;

	private String activeFlag;

	private Long createdUserId;

	private Long createdPostId;

	private Date createdDate;

	private Long updatedUserId;

	private Long updatedPostId;

	private Date updatedDate;

	private Integer dbId;
	
	private Integer langId;

	public MstPensionChecklist() {

	}

	/**
	 * @param docId
	 * @param docDesc
	 * @param createdUserId
	 * @param createdPostId
	 * @param createdDate
	 * @param updatedUserId
	 * @param updatedPostId
	 * @param updatedDate
	 * @param dbId
	 */
	public MstPensionChecklist(Long docId, String docDesc, String activeFlag, Long createdUserId, Long createdPostId, Date createdDate, Long updatedUserId, Long updatedPostId, Date updatedDate,
			Integer dbId) {

		super();
		this.docId = docId;
		this.docDesc = docDesc;
		this.activeFlag = activeFlag;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
		this.dbId = dbId;
	}

	public Long getDocId() {

		return docId;
	}

	public void setDocId(Long docId) {

		this.docId = docId;
	}

	public String getDocDesc() {

		return docDesc;
	}

	public void setDocDesc(String docDesc) {

		this.docDesc = docDesc;
	}

	public String getActiveFlag() {

		return activeFlag;
	}

	public void setActiveFlag(String activeFlag) {

		this.activeFlag = activeFlag;
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

	public Integer getDbId() {

		return dbId;
	}

	public void setDbId(Integer dbId) {

		this.dbId = dbId;
	}

	public Integer getLangId() {
		return langId;
	}

	public void setLangId(Integer langId) {
		this.langId = langId;
	}
}
