package com.tcs.sgv.pensionpay.valueobject;

public class HstRltAuditorBankId implements java.io.Serializable {

	// Fields

	private Long auditorBankId;

	private Integer trnCounter;

	// Constructors

	/** default constructor */
	public HstRltAuditorBankId() {

	}

	/** full constructor */
	public HstRltAuditorBankId(Long auditorBankId, Integer trnCounter) {

		this.auditorBankId = auditorBankId;
		this.trnCounter = trnCounter;
	}

	// Property accessors
	public Long getAuditorBankId() {

		return this.auditorBankId;
	}

	public void setAuditorBankId(Long auditorBankId) {

		this.auditorBankId = auditorBankId;
	}

	public Integer getTrnCounter() {

		return this.trnCounter;
	}

	public void setTrnCounter(Integer trnCounter) {

		this.trnCounter = trnCounter;
	}

	public boolean equals(Object other) {

		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof HstRltAuditorBankId))
			return false;
		HstRltAuditorBankId castOther = (HstRltAuditorBankId) other;

		return ((this.getAuditorBankId() == castOther.getAuditorBankId()) || (this.getAuditorBankId() != null && castOther.getAuditorBankId() != null && this.getAuditorBankId().equals(
				castOther.getAuditorBankId())))
				&& ((this.getTrnCounter() == castOther.getTrnCounter()) || (this.getTrnCounter() != null && castOther.getTrnCounter() != null && this.getTrnCounter().equals(castOther.getTrnCounter())));
	}

	public int hashCode() {

		int result = 17;

		result = 37 * result + (getAuditorBankId() == null ? 0 : this.getAuditorBankId().hashCode());
		result = 37 * result + (getTrnCounter() == null ? 0 : this.getTrnCounter().hashCode());
		return result;
	}

}
