/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 5, 2011		Vihan Khatri								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.valueobject;

/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 Mar 5, 2011
 */
public class MstDcpsSchemes {

	private Long schemeDcpsId;
	private String schemeCode;
	private String schemeName;

	public Long getSchemeDcpsId() {
		return schemeDcpsId;
	}

	public void setSchemeDcpsId(Long schemeDcpsId) {
		this.schemeDcpsId = schemeDcpsId;
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

}
