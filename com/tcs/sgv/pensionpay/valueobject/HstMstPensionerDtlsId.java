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
public class HstMstPensionerDtlsId implements Serializable {

    
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 8400654211380439381L;

	// Fields    
	
	private Long pensionerDtlsId;

    private Integer trnCounter;

    public HstMstPensionerDtlsId()
    { }
    
    /**
     * 
     * @param pensionerDtlsId
     * @param trnCounter
     */
    public HstMstPensionerDtlsId(Long pensionerDtlsId, Integer trnCounter) {
		super();
		this.pensionerDtlsId = pensionerDtlsId;
		this.trnCounter = trnCounter;
	}

	public Long getPensionerDtlsId() {
		return pensionerDtlsId;
	}

	public void setPensionerDtlsId(Long pensionerDtlsId) {
		this.pensionerDtlsId = pensionerDtlsId;
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
				+ ((pensionerDtlsId == null) ? 0 : pensionerDtlsId.hashCode());
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
		HstMstPensionerDtlsId other = (HstMstPensionerDtlsId) obj;
		if (pensionerDtlsId == null) {
			if (other.pensionerDtlsId != null)
				return false;
		} else if (!pensionerDtlsId.equals(other.pensionerDtlsId))
			return false;
		if (trnCounter == null) {
			if (other.trnCounter != null)
				return false;
		} else if (!trnCounter.equals(other.trnCounter))
			return false;
		return true;
	}
  
    
}
