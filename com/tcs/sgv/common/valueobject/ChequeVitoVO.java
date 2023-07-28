package com.tcs.sgv.common.valueobject;

import java.math.BigDecimal;
import java.util.List;

public class ChequeVitoVO 
{
	private long srNo;
	private Long adivceNo;
	private Long chequeNo;
	private String payeeName;
	private BigDecimal totalAmount;
	private Long tokenNo;
	private String chequeDate;
	private BigDecimal chequeAmount;
	private BigDecimal billAmount;
	private String majorHead;
	private Long totalVoucher;
	private Long totalcheques;
	private String billType;
//	private String revChequeNo;
	private String strchequeAmount;
	private String strbillAmount;
	private String strtotalAmount;
	private String strChequeNos;
	
	public String getStrtotalAmount() {
		return strtotalAmount;
	}
	public void setStrtotalAmount(String strtotalAmount) {
		this.strtotalAmount = strtotalAmount;
	}
	public String getBillType() {
		return billType;
	}
	public void setBillType(String billType) {
		this.billType = billType;
	}
	public Long getAdivceNo() {
		return adivceNo;
	}
	public void setAdivceNo(Long adivceNo) {
		this.adivceNo = adivceNo;
	}
	public Long getChequeNo() {
		return chequeNo;
	}
	public void setChequeNo(Long chequeNo) {
		this.chequeNo = chequeNo;
	}
	public String getPayeeName() {
		return payeeName;
	}
	public void setPayeeName(String payeeName) {
		this.payeeName = payeeName;
	}
	public long getSrNo() {
		return srNo;
	}
	public void setSrNo(long srNo) {
		this.srNo = srNo;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public BigDecimal getBillAmount() {
		return billAmount;
	}
	public void setBillAmount(BigDecimal billAmount) {
		this.billAmount = billAmount;
	}
	public BigDecimal getChequeAmount() {
		return chequeAmount;
	}
	public void setChequeAmount(BigDecimal chequeAmount) {
		this.chequeAmount = chequeAmount;
	}
	
	public String getChequeDate() {
		return chequeDate;
	}
	public void setChequeDate(String chequeDate) {
		this.chequeDate = chequeDate;
	}
	public Long getTokenNo() {
		return tokenNo;
	}
	public void setTokenNo(Long tokenNo) {
		this.tokenNo = tokenNo;
	}
	
	public Long getTotalcheques() {
		return totalcheques;
	}
	public void setTotalcheques(Long totalcheques) {
		this.totalcheques = totalcheques;
	}
	public Long getTotalVoucher() {
		return totalVoucher;
	}
	public void setTotalVoucher(Long totalVoucher) {
		this.totalVoucher = totalVoucher;
	}
	public String getMajorHead() {
		return majorHead;
	}
	public void setMajorHead(String majorHead) {
		this.majorHead = majorHead;
	}
/*	public String getRevChequeNo() {
		return revChequeNo;
	}
	public void setRevChequeNo(String revChequeNo) {
		this.revChequeNo = revChequeNo;
	}*/
	public String getStrchequeAmount() {
		return strchequeAmount;
	}
	public void setStrchequeAmount(String strchequeAmount) {
		this.strchequeAmount = strchequeAmount;
	}
	public String getStrbillAmount() {
		return strbillAmount;
	}
	public void setStrbillAmount(String strbillAmount) {
		this.strbillAmount = strbillAmount;
	}
	public String getStrChequeNos() {
		return strChequeNos;
	}
	public void setStrChequeNos(String strChequeNos) {
		this.strChequeNos = strChequeNos;
	}
	
}	