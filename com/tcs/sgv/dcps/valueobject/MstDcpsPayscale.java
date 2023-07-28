/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 17, 2011		Kapil Devani								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.valueobject;

import java.util.Date;


/**
 * Class Description -
 * 
 * 
 * @author Kapil Devani
 * @version 0.1
 * @since JDK 5.0 Feb 17, 2011
 */
public class MstDcpsPayscale {

	private Long payScaleId;
	private String payDescription;
	private Date effectiveFrom;

	/**
	 * @return the payScaleId
	 */
	public Long getPayScaleId() {

		return payScaleId;
	}

	/**
	 * @param payScaleId
	 *            the payScaleId to set
	 */
	public void setPayScaleId(Long payScaleId) {

		this.payScaleId = payScaleId;
	}

	/**
	 * @return the payDescription
	 */
	public String getPayDescription() {

		return payDescription;
	}

	/**
	 * @param payDescription
	 *            the payDescription to set
	 */
	public void setPayDescription(String payDescription) {

		this.payDescription = payDescription;
	}

	/**
	 * @return the effectiveFrom
	 */
	public Date getEffectiveFrom() {

		return effectiveFrom;
	}

	/**
	 * @param effectiveFrom
	 *            the effectiveFrom to set
	 */
	public void setEffectiveFrom(Date effectiveFrom) {

		this.effectiveFrom = effectiveFrom;
	}

}