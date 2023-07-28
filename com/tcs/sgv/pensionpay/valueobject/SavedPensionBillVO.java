package com.tcs.sgv.pensionpay.valueobject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SavedPensionBillVO {

	private String billCtgry;

	private Long billNo;

	private BigDecimal billGrossAmt;

	private BigDecimal billNetAmt;

	private Date billDate;

	private String bdjtMjrHd;

	private Long tokenNum;

	private Long refNum;

	private Long audPostId;

	private Long subjectId;

	private Short currBillStatus;

	private Long hierarchyRefId;

	private String ppoNo;

	private String billDesc;

	private String audtrName;

	private int objCount;

	private String billCntrlNo;

	private BigDecimal headCode;

	private String scheme;

	private String partyName;

	private String billType;

	private BigDecimal partyAmount;

	private List<String> partyNames = new ArrayList<String>();

	private List<BigDecimal> chequeAmounts = new ArrayList<BigDecimal>();

	private List<Long> chequeNos = new ArrayList<Long>();

	private Long chequeNo;

	private Date fromDt;

	private Long voucherNo;

	private Date voucherDate;

	private Long ecsNo;
	
	private List<Long> billPartyIds = new ArrayList<Long>();
	
	private Long billPartyId;

	// private Date paymentDate;

	public void addPartyName(String partyName) {

		this.partyNames.add(partyName);
	}

	public void addChequeAmounts(BigDecimal billNetAmount) {

		this.chequeAmounts.add(billNetAmount);
	}

	public void addChequeNo(Long chequeNo) {

		this.chequeNos.add(chequeNo);
	}
	
	public void addBillPartyId(Long billPartyId) {

		this.billPartyIds.add(billPartyId);
	}

	public SavedPensionBillVO(String ppoNo, String partyName, String billType, Date billDate, BigDecimal billNetAmt, String billCtgry, String audtrName, Long subjectId, Long billNo,
			Short currBillStatus, BigDecimal partyAmount, String billCntrlNo) {

		super();
		this.ppoNo = ppoNo;
		this.partyName = partyName;
		this.billType = billType;
		this.billNetAmt = billNetAmt;
		this.billDate = billDate;
		this.billCtgry = billCtgry;
		this.audtrName = audtrName;
		this.subjectId = subjectId;
		this.billNo = billNo;
		this.currBillStatus = currBillStatus;
		this.partyAmount = partyAmount;
		this.billCntrlNo = billCntrlNo;
	}

	// reg.ppoNo,rlt.partyName,reg.billDate,reg.billNetAmount,reg.tcBill,concat(concat(concat(concat(oem.empFname,' '),oem.empMname),' '),oem.empLname),reg.subjectId,reg.billNo,reg.currBillStatus,rlt.partyAmt,reg.billCntrlNo
	public SavedPensionBillVO(String ppoNo, String partyName, Date billDate, BigDecimal billNetAmt, String billCtgry, String audtrName, Long subjectId, Long billNo, Short currBillStatus,
			BigDecimal partyAmount, String billCntrlNo) {

		super();
		this.ppoNo = ppoNo;
		this.partyName = partyName;
		this.billNetAmt = billNetAmt;
		this.billDate = billDate;
		this.billCtgry = billCtgry;
		this.audtrName = audtrName;
		this.subjectId = subjectId;
		this.billNo = billNo;
		this.currBillStatus = currBillStatus;
		this.partyAmount = partyAmount;
		this.billCntrlNo = billCntrlNo;
	}

	// reg.ppoNo,rlt.partyName,reg.billNo,reg.currBillStatus,rlt.partyAmt,tcd.chequeNo,tcd.fromDt
	public SavedPensionBillVO(String ppoNo, String partyName, Long billNo, Short currBillStatus, BigDecimal partyAmount, Long chequeNo, Date fromDt, String billCntrlNo, Long subjectId) {

		this.ppoNo = ppoNo;
		this.partyName = partyName;
		this.billNo = billNo;
		this.currBillStatus = currBillStatus;
		this.partyAmount = partyAmount;
		this.chequeNo = chequeNo;
		this.fromDt = fromDt;
		this.billCntrlNo = billCntrlNo;
		this.subjectId = subjectId;
	}

	// For Cash Payment
	public SavedPensionBillVO(String ppoNo, String partyName, Date billDate, BigDecimal billNetAmt, String billCtgry, Long subjectId, Long billNo, Short currBillStatus, BigDecimal partyAmount,
			String billCntrlNo) {

		super();
		this.ppoNo = ppoNo;
		this.partyName = partyName;
		this.billNetAmt = billNetAmt;
		this.billDate = billDate;
		this.billCtgry = billCtgry;
		this.subjectId = subjectId;
		this.billNo = billNo;
		this.currBillStatus = currBillStatus;
		this.partyAmount = partyAmount;
		this.billCntrlNo = billCntrlNo;
	}

	// bill-voucher mapping
	public SavedPensionBillVO(Long billNo, String billCntrlNo, String partyName, Long voucherNo, Date voucherDate, BigDecimal billNetAmt, Long ecsNo, Long chequeNo) {

		this.billNo = billNo;
		this.billCntrlNo = billCntrlNo;
		this.partyName = partyName;
		this.voucherNo = voucherNo;
		this.voucherDate = voucherDate;
		this.billNetAmt = billNetAmt;
		this.ecsNo = ecsNo;
		this.chequeNo = chequeNo;
	}

	// bill-voucher mapping without ecs no
	public SavedPensionBillVO(Long billNo, String billCntrlNo, String partyName, Long voucherNo, Date voucherDate, BigDecimal billNetAmt, Long chequeNo, BigDecimal billPartyAmt,Long billPartyId,Long subjectId,String ppoNo) {

		this.billNo = billNo;
		this.billCntrlNo = billCntrlNo;
		this.partyName = partyName;
		this.voucherNo = voucherNo;
		this.voucherDate = voucherDate;
		this.billNetAmt = billNetAmt;
		this.chequeNo = chequeNo;
		this.partyAmount = billPartyAmt;
		this.billPartyId = billPartyId;
		this.subjectId = subjectId;
		this.ppoNo = ppoNo;
	}

	public void setBillDate(Date billDate) {

		this.billDate = billDate;
	}

	public Date getBillDate() {

		return this.billDate;
	}

	public void setCurrBillStatus(Short currBillStatus) {

		this.currBillStatus = currBillStatus;
	}

	public Short getCurrBillStatus() {

		return this.currBillStatus;
	}

	public void setObjCount(int oBjCount) {

		this.objCount = oBjCount;
	}

	public int getObjCount() {

		return this.objCount;
	}

	public void setHierarchyRefId(Long hierarchyRefId) {

		this.hierarchyRefId = hierarchyRefId;
	}

	public Long getHierarchyRefId() {

		return this.hierarchyRefId;
	}

	public void setSubjectId(Long subjectId) {

		this.subjectId = subjectId;
	}

	public Long getSubjectId() {

		return this.subjectId;
	}

	public void setAudPostId(Long audPostId) {

		this.audPostId = audPostId;
	}

	public Long getAudPostId() {

		return this.audPostId;
	}

	public void setRefNum(Long refNum) {

		this.refNum = refNum;
	}

	public Long getRefNum() {

		return this.refNum;
	}

	public void setTokenNum(Long tokenNum) {

		this.tokenNum = tokenNum;
	}

	public Long getTokenNum() {

		return this.tokenNum;
	}

	public void setBillGrossAmt(BigDecimal billGrossAmt) {

		this.billGrossAmt = billGrossAmt;
	}

	public void setBillNetAmt(BigDecimal billNetAmt) {

		this.billNetAmt = billNetAmt;
	}

	public void setHeadCode(BigDecimal headCode) {

		this.headCode = headCode;
	}

	public BigDecimal getBillGrossAmt() {

		return this.billGrossAmt;
	}

	public BigDecimal getBillNetAmt() {

		return this.billNetAmt;
	}

	public BigDecimal getHeadCode() {

		return this.headCode;
	}

	public void setBillCtgry(String billCtgry) {

		this.billCtgry = billCtgry;
	}

	public void setBdjtMjrHd(String bdjtMjrHd) {

		this.bdjtMjrHd = bdjtMjrHd;
	}

	public void setAudtrName(String audtrName) {

		this.audtrName = audtrName;
	}

	public void setBillDesc(String billDesc) {

		this.billDesc = billDesc;
	}

	public void setPpoNo(String ppoNo) {

		this.ppoNo = ppoNo;
	}

	public void setScheme(String scheme) {

		this.scheme = scheme;
	}

	public void setBillCntrlNo(String billCntrlNo) {

		this.billCntrlNo = billCntrlNo;
	}

	public void setBillNo(Long billNo) {

		this.billNo = billNo;
	}

	public String getBillCtgry() {

		return this.billCtgry;
	}

	public String getBdjtMjrHd() {

		return this.bdjtMjrHd;
	}

	public String getAudtrName() {

		return this.audtrName;
	}

	public String getBillDesc() {

		return this.billDesc;
	}

	public String getPpoNo() {

		return this.ppoNo;
	}

	public String getScheme() {

		return this.scheme;
	}

	public String getBillCntrlNo() {

		return this.billCntrlNo;
	}

	public Long getBillNo() {

		return this.billNo;
	}

	public String getPartyName() {

		return this.partyName;
	}

	public void setPartyName(String partyName) {

		this.partyName = partyName;
	}

	public String getBillType() {

		return billType;
	}

	public void setBillType(String billType) {

		this.billType = billType;
	}

	public List<BigDecimal> getChequeAmounts() {

		return chequeAmounts;
	}

	public void setChequeAmounts(List<BigDecimal> chequeAmounts) {

		this.chequeAmounts = chequeAmounts;
	}

	public List<String> getPartyNames() {

		return partyNames;
	}

	public void setPartyNames(List<String> partyNames) {

		this.partyNames = partyNames;
	}

	public BigDecimal getPartyAmount() {

		return partyAmount;
	}

	public void setPartyAmount(BigDecimal partyAmount) {

		this.partyAmount = partyAmount;
	}

	public Long getChequeNo() {

		return chequeNo;
	}

	public void setChequeNo(Long chequeNo) {

		this.chequeNo = chequeNo;
	}

	public Date getFromDt() {

		return fromDt;
	}

	public void setFromDt(Date fromDt) {

		this.fromDt = fromDt;
	}

	public List<Long> getChequeNos() {

		return chequeNos;
	}

	public void setChequeNos(List<Long> chequeNos) {

		this.chequeNos = chequeNos;
	}

	public Long getVoucherNo() {

		return voucherNo;
	}

	public void setVoucherNo(Long voucherNo) {

		this.voucherNo = voucherNo;
	}

	public Date getVoucherDate() {

		return voucherDate;
	}

	public void setVoucherDate(Date voucherDate) {

		this.voucherDate = voucherDate;
	}

	public Long getEcsNo() {

		return ecsNo;
	}

	public void setEcsNo(Long ecsNo) {

		this.ecsNo = ecsNo;
	}
	
	public Long getBillPartyId() {
		return billPartyId;
	}

	public void setBillPartyId(Long billPartyId) {
		this.billPartyId = billPartyId;
	}
	
	public List<Long> getBillPartyIds() {
		return billPartyIds;
	}

	public void setBillPartyIds(List<Long> billPartyIds) {
		this.billPartyIds = billPartyIds;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((audPostId == null) ? 0 : audPostId.hashCode());
		result = prime * result + ((audtrName == null) ? 0 : audtrName.hashCode());
		result = prime * result + ((bdjtMjrHd == null) ? 0 : bdjtMjrHd.hashCode());
		result = prime * result + ((billCntrlNo == null) ? 0 : billCntrlNo.hashCode());
		result = prime * result + ((billCtgry == null) ? 0 : billCtgry.hashCode());
		result = prime * result + ((billDate == null) ? 0 : billDate.hashCode());
		result = prime * result + ((billDesc == null) ? 0 : billDesc.hashCode());
		result = prime * result + ((billGrossAmt == null) ? 0 : billGrossAmt.hashCode());
		result = prime * result + ((billNetAmt == null) ? 0 : billNetAmt.hashCode());
		result = prime * result + ((billNo == null) ? 0 : billNo.hashCode());
		result = prime * result + ((billType == null) ? 0 : billType.hashCode());
		result = prime * result + ((currBillStatus == null) ? 0 : currBillStatus.hashCode());
		result = prime * result + ((headCode == null) ? 0 : headCode.hashCode());
		result = prime * result + ((hierarchyRefId == null) ? 0 : hierarchyRefId.hashCode());
		result = prime * result + objCount;
		result = prime * result + ((partyName == null) ? 0 : partyName.hashCode());
		result = prime * result + ((ppoNo == null) ? 0 : ppoNo.hashCode());
		result = prime * result + ((refNum == null) ? 0 : refNum.hashCode());
		result = prime * result + ((scheme == null) ? 0 : scheme.hashCode());
		result = prime * result + ((subjectId == null) ? 0 : subjectId.hashCode());
		result = prime * result + ((tokenNum == null) ? 0 : tokenNum.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SavedPensionBillVO other = (SavedPensionBillVO) obj;
		if (audPostId == null) {
			if (other.audPostId != null) {
				return false;
			}
		} else if (!audPostId.equals(other.audPostId)) {
			return false;
		}
		if (audtrName == null) {
			if (other.audtrName != null) {
				return false;
			}
		} else if (!audtrName.equals(other.audtrName)) {
			return false;
		}
		if (bdjtMjrHd == null) {
			if (other.bdjtMjrHd != null) {
				return false;
			}
		} else if (!bdjtMjrHd.equals(other.bdjtMjrHd)) {
			return false;
		}
		if (billCntrlNo == null) {
			if (other.billCntrlNo != null) {
				return false;
			}
		} else if (!billCntrlNo.equals(other.billCntrlNo)) {
			return false;
		}
		if (billCtgry == null) {
			if (other.billCtgry != null) {
				return false;
			}
		} else if (!billCtgry.equals(other.billCtgry)) {
			return false;
		}
		if (billDate == null) {
			if (other.billDate != null) {
				return false;
			}
		} else if (!billDate.equals(other.billDate)) {
			return false;
		}
		if (billDesc == null) {
			if (other.billDesc != null) {
				return false;
			}
		} else if (!billDesc.equals(other.billDesc)) {
			return false;
		}
		if (billGrossAmt == null) {
			if (other.billGrossAmt != null) {
				return false;
			}
		} else if (!billGrossAmt.equals(other.billGrossAmt)) {
			return false;
		}
		if (billNetAmt == null) {
			if (other.billNetAmt != null) {
				return false;
			}
		} else if (!billNetAmt.equals(other.billNetAmt)) {
			return false;
		}
		if (billNo == null) {
			if (other.billNo != null) {
				return false;
			}
		} else if (!billNo.equals(other.billNo)) {
			return false;
		}
		if (billType == null) {
			if (other.billType != null) {
				return false;
			}
		} else if (!billType.equals(other.billType)) {
			return false;
		}
		if (currBillStatus == null) {
			if (other.currBillStatus != null) {
				return false;
			}
		} else if (!currBillStatus.equals(other.currBillStatus)) {
			return false;
		}
		if (headCode == null) {
			if (other.headCode != null) {
				return false;
			}
		} else if (!headCode.equals(other.headCode)) {
			return false;
		}
		if (hierarchyRefId == null) {
			if (other.hierarchyRefId != null) {
				return false;
			}
		} else if (!hierarchyRefId.equals(other.hierarchyRefId)) {
			return false;
		}
		if (objCount != other.objCount) {
			return false;
		}
		if (partyName == null) {
			if (other.partyName != null) {
				return false;
			}
		} else if (!partyName.equals(other.partyName)) {
			return false;
		}
		if (ppoNo == null) {
			if (other.ppoNo != null) {
				return false;
			}
		} else if (!ppoNo.equals(other.ppoNo)) {
			return false;
		}
		if (refNum == null) {
			if (other.refNum != null) {
				return false;
			}
		} else if (!refNum.equals(other.refNum)) {
			return false;
		}
		if (scheme == null) {
			if (other.scheme != null) {
				return false;
			}
		} else if (!scheme.equals(other.scheme)) {
			return false;
		}
		if (subjectId == null) {
			if (other.subjectId != null) {
				return false;
			}
		} else if (!subjectId.equals(other.subjectId)) {
			return false;
		}
		if (tokenNum == null) {
			if (other.tokenNum != null) {
				return false;
			}
		} else if (!tokenNum.equals(other.tokenNum)) {
			return false;
		}
		return true;
	}

}
