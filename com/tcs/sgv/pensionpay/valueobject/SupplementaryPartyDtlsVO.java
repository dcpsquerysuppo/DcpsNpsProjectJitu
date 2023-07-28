/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jun 2, 2011		Shivani Rana								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.valueobject;
//com.tcs.sgv.pensionpay.valueobject.SupplementaryPartyDtlsVO
import java.math.BigDecimal;
import java.util.Date;


/**
 * Class Description -
 * 
 * 
 * @author Shivani Rana
 * @version 0.1
 * @since JDK 5.0 Jun 2, 2011
 */
public class SupplementaryPartyDtlsVO {

	private String beneifiicaryName;
	private String branchCode;
	private String bankCode;
	private String bankName;
	private String branchName;
	private String accountNo;
	private BigDecimal amount;
	private Long micrCode;
	private Date dateOfDeath;
	private String schemeCode;
	private BigDecimal headCode;
	private String ledgerNo;
	private String pageNo;
	
	public SupplementaryPartyDtlsVO()
	{}
	// rbb.branchName,mpd.accountNo,mpd.branchCode
	// mph.firstName,mpd.bankCode,rbb.branchName,mpd.accountNo,mpd.branchCode,rbb.micrCode
	public SupplementaryPartyDtlsVO(String beneifiicaryName, String bankCode, String branchName, String accountNo, String branchCode, Long micrCode,Date dateOfDeath,String schemeCode,BigDecimal headCode,String ledgerNo,String pageNo) {

		this.beneifiicaryName = beneifiicaryName;
		this.branchCode = branchCode;
		this.bankCode = bankCode;
		this.branchName = branchName;
		this.accountNo = accountNo;
		this.micrCode = micrCode;
		this.dateOfDeath = dateOfDeath;
		this.schemeCode = schemeCode;
		this.headCode = headCode;
		this.ledgerNo = ledgerNo;
		this.pageNo = pageNo;
	}
	
	public SupplementaryPartyDtlsVO(String beneifiicaryName, String bankCode, String branchName, String accountNo, String branchCode, Long micrCode,String schemeCode,BigDecimal headCode,String ledgerNo,String pageNo) {

		this.beneifiicaryName = beneifiicaryName;
		this.branchCode = branchCode;
		this.bankCode = bankCode;
		this.branchName = branchName;
		this.accountNo = accountNo;
		this.micrCode = micrCode;
		this.schemeCode = schemeCode;
		this.headCode = headCode;
		this.ledgerNo = ledgerNo;
		this.pageNo = pageNo;
	}

	public SupplementaryPartyDtlsVO(String beneifiicaryName, String bankCode, String branchName, String accountNo, String branchCode, Long micrCode, BigDecimal amount) {

		this.beneifiicaryName = beneifiicaryName;
		this.branchCode = branchCode;
		this.bankCode = bankCode;
		this.branchName = branchName;
		this.accountNo = accountNo;
		this.micrCode = micrCode;
		this.amount = amount;
	}

	public String getBeneifiicaryName() {

		return beneifiicaryName;
	}

	public void setBeneifiicaryName(String beneifiicaryName) {

		this.beneifiicaryName = beneifiicaryName;
	}

	public String getBranchCode() {

		return branchCode;
	}

	public void setBranchCode(String branchCode) {

		this.branchCode = branchCode;
	}

	public String getBankCode() {

		return bankCode;
	}

	public void setBankCode(String bankCode) {

		this.bankCode = bankCode;
	}

	public String getBankName() {

		return bankName;
	}

	public void setBankName(String bankName) {

		this.bankName = bankName;
	}

	public String getBranchName() {

		return branchName;
	}

	public void setBranchName(String branchName) {

		this.branchName = branchName;
	}

	public String getAccountNo() {

		return accountNo;
	}

	public void setAccountNo(String accountNo) {

		this.accountNo = accountNo;
	}

	public BigDecimal getAmount() {

		return amount;
	}

	public void setAmount(BigDecimal amount) {

		this.amount = amount;
	}

	public Long getMicrCode() {

		return micrCode;
	}

	public void setMicrCode(Long micrCode) {

		this.micrCode = micrCode;
	}

	public Date getDateOfDeath() {
		return dateOfDeath;
	}

	public void setDateOfDeath(Date dateOfDeath) {
		this.dateOfDeath = dateOfDeath;
	}
	
	public String getSchemeCode() {
		return schemeCode;
	}
	
	public void setSchemeCode(String schemeCode) {
		this.schemeCode = schemeCode;
	}
	
	public BigDecimal getHeadCode() {
		return headCode;
	}
	
	public void setHeadCode(BigDecimal headCode) {
		this.headCode = headCode;
	}
	
	public String getLedgerNo() {
		return ledgerNo;
	}
	
	public void setLedgerNo(String ledgerNo) {
		this.ledgerNo = ledgerNo;
	}
	
	public String getPageNo() {
		return pageNo;
	}
	
	public void setPageNo(String pageNo) {
		this.pageNo = pageNo;
	}

}
