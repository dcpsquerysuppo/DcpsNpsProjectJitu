package com.tcs.sgv.pensionpay.valueobject;

import java.math.BigDecimal;
import java.util.Date;

public class PensionerSeenDtlsVO 
{
	private Long srNo;
	private String ppoNo;
	private String seenFlag;
	private String name;
	private String accountNo;
	private Date seenDate;
	private Long pensionRequestId;
	private Long pensionerCode;
	private Long pensionerDtlsId;
	private String branchCode;
	private String fpDeathFlg;
	private String activeFlag;
	private String pageNo;
	private String ledgerNo;
	private BigDecimal caseOwner;
	private Long headCode;

	
	public PensionerSeenDtlsVO()
	{}	
	public PensionerSeenDtlsVO(Long srNo,String ppoNo,String seenFlag,String name,String accountNo,Date seenDate,Long pensionRequestId,
			Long pensionerCode,Long pensionerDtlsId,String branchCode,String fpDeathFlg,String activeFlag,
			String pageNo,String ledgerNo,BigDecimal caseOwner,Long headCode)
	{
		this.srNo = srNo;
		this.ppoNo = ppoNo;
		this.seenFlag = seenFlag;
		this.name = name;
		this.accountNo = accountNo;
		this.seenDate = seenDate;
		this.pensionRequestId = pensionRequestId;
		this.pensionerCode = pensionerCode;
		this.pensionerDtlsId = pensionerDtlsId;
		this.branchCode = branchCode;
		this.fpDeathFlg = fpDeathFlg;
		this.activeFlag =activeFlag;
		this.ledgerNo = ledgerNo;
		this.pageNo = pageNo;
		this.caseOwner = caseOwner;
		this.headCode = headCode;
	}	
	public void setSrNo(Long srNo)
	{
		this.srNo = srNo;
	}
	public Long getSrNo()
	{
		return this.srNo;
	}
	public void setPpoNo(String ppoNo)
	{
		this.ppoNo = ppoNo;
	}
	public String getPpoNo()
	{
		return this.ppoNo;
	}
	public void setSeenFlag(String seenFlag)
	{
		this.seenFlag = seenFlag;
	}
	public String getSeenFlag()
	{
		return this.seenFlag;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getName()
	{
		return this.name;
	}
	public void setAccountNo(String accountNo)
	{
		this.accountNo = accountNo;
	}
	public String getAccountNo()
	{
		return this.accountNo;
	}
	public void setSeenDate(Date seenDate)
	{
		this.seenDate = seenDate;
	}
	public Date getSeenDate()
	{
		return this.seenDate;
	}
	public void setPensionRequestId(Long pensionRequestId)
	{
		this.pensionRequestId = pensionRequestId;
	}
	public Long getPensionRequestId()
	{
		return this.pensionRequestId;
	}
	public void setPensionerCode(Long pensionerCode)
	{
		this.pensionerCode = pensionerCode;
	}
	public Long getPensionerCode()
	{
		return this.pensionerCode;
	}
	public Long getPensionerDtlsId()
	{
		return this.pensionerDtlsId;
	}

	public void setPensionerDtlsId(Long pensionerDtlsId) 
	{
		this.pensionerDtlsId = pensionerDtlsId;
	}
	public void setBranchCode(String branchCode)
	{
		this.branchCode = branchCode;
	}
	public String getBranchCode()
	{
		return this.branchCode;
	}
	
	public String getFpDeathFlg()
	{
		return this.fpDeathFlg;
	}
	public void setFpDeathFlg(String fpDeathFlg)
	{
		this.fpDeathFlg = fpDeathFlg;
	}
	public String getActiveFlag()
	{
		return this.activeFlag;
	}
	public void setActiveFlag(String activeFlag)
	{
		this.activeFlag = activeFlag;
	}
	public String getPageNo() {

		return this.pageNo;
	}

	public void setPageNo(String pageNo) {

		this.pageNo = pageNo;
	}
	
	public String getLedgerNo() {

		return this.ledgerNo;
	}

	public void setLedgerNo(String ledgerNo) {

		this.ledgerNo = ledgerNo;
	}
	
	public BigDecimal getCaseOwner() {

		return this.caseOwner;
	}

	public void setCaseOwner(BigDecimal caseOwner) {

		this.caseOwner = caseOwner;
	}
	public Long getHeadCode() {

		return this.headCode;
	}

	public void setHeadCode(Long headCode) {

		this.headCode = headCode;
	}
}
