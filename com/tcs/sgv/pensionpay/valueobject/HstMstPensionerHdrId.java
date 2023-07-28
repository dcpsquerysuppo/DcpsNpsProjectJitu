/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jun 19, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.valueobject;

import java.io.Serializable;

/**
 * Class Description - 
 *
 *
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0
 * Jun 19, 2011
 */
public class HstMstPensionerHdrId implements Serializable{
	
    /**
	 * serial version uid
	 */
	private static final long serialVersionUID = 8521592161059444147L;

	// Fields    
	
	private Long pensionerId;

    private Integer trnCounter;

    public HstMstPensionerHdrId()
    {}
    
    /**
     * 
     * @param pensionerId
     * @param trnCounter
     */
	public HstMstPensionerHdrId(Long pensionerId, Integer trnCounter) {
		super();
		this.pensionerId = pensionerId;
		this.trnCounter = trnCounter;
	}

	public Long getPensionerId() {
		return pensionerId;
	}

	public void setPensionerId(Long pensionerId) {
		this.pensionerId = pensionerId;
	}

	public Integer getTrnCounter() {
		return trnCounter;
	}

	public void setTrnCounter(Integer trnCounter) {
		this.trnCounter = trnCounter;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((pensionerId == null) ? 0 : pensionerId.hashCode());
		result = prime * result
				+ ((trnCounter == null) ? 0 : trnCounter.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HstMstPensionerHdrId other = (HstMstPensionerHdrId) obj;
		if (pensionerId == null) {
			if (other.pensionerId != null)
				return false;
		} else if (!pensionerId.equals(other.pensionerId))
			return false;
		if (trnCounter == null) {
			if (other.trnCounter != null)
				return false;
		} else if (!trnCounter.equals(other.trnCounter))
			return false;
		return true;
	}

    
}
