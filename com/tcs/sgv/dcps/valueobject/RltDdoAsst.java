/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 4, 2011		Kapil Devani								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.valueobject;

/**
 * Class Description -
 * 
 * 
 * @author Kapil Devani
 * @version 0.1
 * @since JDK 5.0 Mar 4, 2011
 */
public class RltDdoAsst {

	private Long rltDdoAsstId;
	private Long asstPostId;
	private Long ddoPostId;

	/**
	 * @return the rltDdoAsstId
	 */
	public Long getRltDdoAsstId() {
		return rltDdoAsstId;
	}

	/**
	 * @param rltDdoAsstId
	 *            the rltDdoAsstId to set
	 */
	public void setRltDdoAsstId(Long rltDdoAsstId) {
		this.rltDdoAsstId = rltDdoAsstId;
	}

	/**
	 * @return the asstPostId
	 */
	public Long getAsstPostId() {
		return asstPostId;
	}

	/**
	 * @param asstPostId
	 *            the asstPostId to set
	 */
	public void setAsstPostId(Long asstPostId) {
		this.asstPostId = asstPostId;
	}

	/**
	 * @return the ddoPostId
	 */
	public Long getDdoPostId() {
		return ddoPostId;
	}

	/**
	 * @param ddoPostId
	 *            the ddoPostId to set
	 */
	public void setDdoPostId(Long ddoPostId) {
		this.ddoPostId = ddoPostId;
	}

}
