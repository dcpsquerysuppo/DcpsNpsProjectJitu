/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jun 17, 2011		Shivani Rana								
 *******************************************************************************
 */
package com.tcs.sgv.common.valueobject;

/**
 * Class Description -
 * 
 * 
 * @author Shivani Rana
 * @version 0.1
 * @since JDK 5.0 Jun 17, 2011
 */
public class MstScheme {

	private Long schemeId;
	private String schemeCode;
	private String schemeName;
	private String demandCode;
	private String majorHead;
	private String subMajorHead;

	private String minorHead;
	private String subMinorHead;

	private String subHead;
	private String charged;
	private String plan;
	private String schemeType;
	private String deptId;
	private String status;
	private String langId;
	private Long finYrId;

	public MstScheme()
	{}
	
	public MstScheme(String schemeCode, String schemeName)
	{
		super();
		this.schemeCode=schemeCode;
		this.schemeName=schemeName;
	}
	
	public Long getSchemeId() {

		return schemeId;
	}

	public void setSchemeId(Long schemeId) {

		this.schemeId = schemeId;
	}

	public String getSchemeCode() {

		return schemeCode;
	}

	public void setSchemeCode(String schemeCode) {

		this.schemeCode = schemeCode;
	}

	public String getSchemeName() {

		return schemeName;
	}

	public void setSchemeName(String schemeName) {

		this.schemeName = schemeName;
	}

	public String getDemandCode() {

		return demandCode;
	}

	public void setDemandCode(String demandCode) {

		this.demandCode = demandCode;
	}

	public String getMajorHead() {

		return majorHead;
	}

	public void setMajorHead(String majorHead) {

		this.majorHead = majorHead;
	}

	public String getSubMajorHead() {

		return subMajorHead;
	}

	public void setSubMajorHead(String subMajorHead) {

		this.subMajorHead = subMajorHead;
	}

	public String getMinorHead() {

		return minorHead;
	}

	public void setMinorHead(String minorHead) {

		this.minorHead = minorHead;
	}

	public String getSubMinorHead() {

		return subMinorHead;
	}

	public void setSubMinorHead(String subMinorHead) {

		this.subMinorHead = subMinorHead;
	}

	public String getSubHead() {

		return subHead;
	}

	public void setSubHead(String subHead) {

		this.subHead = subHead;
	}

	public String getCharged() {

		return charged;
	}

	public void setCharged(String charged) {

		this.charged = charged;
	}

	public String getPlan() {

		return plan;
	}

	public void setPlan(String plan) {

		this.plan = plan;
	}

	public String getSchemeType() {

		return schemeType;
	}

	public void setSchemeType(String schemeType) {

		this.schemeType = schemeType;
	}

	public String getDeptId() {

		return deptId;
	}

	public void setDeptId(String deptId) {

		this.deptId = deptId;
	}

	public String getStatus() {

		return status;
	}

	public void setStatus(String status) {

		this.status = status;
	}

	public String getLangId() {

		return langId;
	}

	public void setLangId(String langId) {

		this.langId = langId;
	}

	public Long getFinYrId() {

		return finYrId;
	}

	public void setFinYrId(Long finYrId) {

		this.finYrId = finYrId;
	}

}
