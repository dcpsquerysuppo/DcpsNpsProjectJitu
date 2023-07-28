/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jun 24, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.valueobject;

import java.util.Date;
import java.math.BigDecimal;


/**
 * Class Description - 
 *
 *
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0
 * Jun 24, 2011
 */
public class PensionAuditRegisterVO {

	private String pensionerCode;
	
	private String ppoNo;
	
	private String pensionerName;
	
	private String ledgerNo;
	
	private String pageNo;
	
	private String applicationNo;
	
	private String pnsrFileNo;
	
	private String guardianName;
	
	private String ropType;
	
	private String bankName;
	
	private String branchCode;
	
	private String branchName;
	
	private String pensionType;
	
	private String accountNo;
	
	private String designation;
	
	private Date joiningDate;
	
	private Date retirementDate;
	
	private Date birthDate;
	
	private Date identificationDate;
	
	private String identityMark;
	
	private String pnsnrAddr1;
	
	private String pnsnrAddr2;
	
	private String pnsnrAddrTown;
	
	private Integer pinCode; 
	 
	private String stateName;
	
	private String districtName;
	
	private String panNo;
	
	private String mobileNo;
	
	private String teleNo;
	
	private String pnsnrEmailId;
	
	private BigDecimal basicPensionAmount;
	
	private Date commencementDate;
	
	private BigDecimal cvpAmount;
	
	private String dcrgOrderNo;
	
	private BigDecimal gratuityAmount;
	
	private String familyMemName;
	
	private BigDecimal fp1Amount;
	
	private Date fp1Date;
	
	private BigDecimal fp2Amount;
	
	private Date fp2Date;
		
	private BigDecimal orgBf1436;
	
	private BigDecimal orgAf1436;
	
	private BigDecimal orgAf1156;
	
	private BigDecimal orgBf1560;  
	
	private BigDecimal orgAfZp;
	
	private BigDecimal reducedPension;
	
	private String pnsnSanctionAuthority;
	
	private String commutationAuthority;
	
	private String libraryNo;
	
	private String series;
	
	private String cvpAuthority;
	
	public PensionAuditRegisterVO()
	{}
	
	/**
	 * 
	 * @param pensionerCode
	 * @param ppoNo
	 * @param pensionerName
	 * @param ledgerNo
	 * @param pageNo
	 * @param applicationNo
	 * @param pnsrFileNo
	 * @param guardianName
	 * @param bankName
	 * @param branchName
	 * @param pensionType
	 * @param accountNo
	 * @param designation
	 * @param joiningDate
	 * @param retirementDate
	 * @param birthDate
	 * @param identificationDate
	 * @param identityMark
	 * @param pnsnrAddr1
	 * @param pnsnrAddr2
	 * @param pnsnrAddrTown
	 * @param pinCode
	 * @param stateName
	 * @param districtName
	 * @param panNo
	 * @param mobileNo
	 * @param teleNo
	 * @param pnsnrEmailId
	 * @param basicPensionAmount
	 * @param commencementDate
	 * @param cvpAmount
	 * @param dcrgOrderNo
	 * @param gratuityAmount
	 * @param familyMemName
	 * @param fp1Amount
	 * @param fp1Date
	 * @param fp2Amount
	 * @param fp2Date
	 * @param orgBf1436
	 * @param orgAf1436
	 * @param orgAf1156
	 * @param orgBf1560
	 * @param orgAfZp
	 * @param reducedPension
	 * @param pnsnSanctionAuthority
	 * @param commutationAuthority
	 * @param libraryNo
	 * @param branchCode
	 * @param ropType
	 * @param series
	 * @param cvpAuthority
	 */


	public PensionAuditRegisterVO(String pensionerCode, String ppoNo, String pensionerName, String ledgerNo, String pageNo, String applicationNo, String pnsrFileNo, String guardianName,
			String bankName, String branchName, String pensionType, String accountNo, String designation, Date joiningDate, Date retirementDate, Date birthDate, Date identificationDate,
			String identityMark, String pnsnrAddr1, String pnsnrAddr2, String pnsnrAddrTown, Integer pinCode, String stateName, String districtName, String panNo, String mobileNo, String teleNo,
			String pnsnrEmailId, BigDecimal basicPensionAmount, Date commencementDate, BigDecimal cvpAmount, String dcrgOrderNo, BigDecimal gratuityAmount, String familyMemName, BigDecimal fp1Amount,
			Date fp1Date, BigDecimal fp2Amount, Date fp2Date, BigDecimal orgBf1436, BigDecimal orgAf1436, BigDecimal orgAf1156, BigDecimal orgBf1560, BigDecimal orgAfZp, BigDecimal reducedPension,
			String pnsnSanctionAuthority, String commutationAuthority,String libraryNo,String branchCode,String ropType,String series,String cvpAuthority) {

		super();
		this.pensionerCode = pensionerCode;
		this.ppoNo = ppoNo;
		this.pensionerName = pensionerName;
		this.ledgerNo = ledgerNo;
		this.pageNo = pageNo;
		this.applicationNo = applicationNo;
		this.pnsrFileNo = pnsrFileNo;
		this.guardianName = guardianName;
		this.bankName = bankName;
		this.branchName = branchName;
		this.pensionType = pensionType;
		this.accountNo = accountNo;
		this.designation = designation;
		this.joiningDate = joiningDate;
		this.retirementDate = retirementDate;
		this.birthDate = birthDate;
		this.identificationDate = identificationDate;
		this.identityMark = identityMark;
		this.pnsnrAddr1 = pnsnrAddr1;
		this.pnsnrAddr2 = pnsnrAddr2;
		this.pnsnrAddrTown = pnsnrAddrTown;
		this.pinCode = pinCode;
		this.stateName = stateName;
		this.districtName = districtName;
		this.panNo = panNo;
		this.mobileNo = mobileNo;
		this.teleNo = teleNo;
		this.pnsnrEmailId = pnsnrEmailId;
		this.basicPensionAmount = basicPensionAmount;
		this.commencementDate = commencementDate;
		this.cvpAmount = cvpAmount;
		this.dcrgOrderNo = dcrgOrderNo;
		this.gratuityAmount = gratuityAmount;
		this.familyMemName = familyMemName;
		this.fp1Amount = fp1Amount;
		this.fp1Date = fp1Date;
		this.fp2Amount = fp2Amount;
		this.fp2Date = fp2Date;
		this.orgBf1436 = orgBf1436;
		this.orgAf1436 = orgAf1436;
		this.orgAf1156 = orgAf1156;
		this.orgBf1560 = orgBf1560;
		this.orgAfZp = orgAfZp;
		this.reducedPension = reducedPension;
		this.pnsnSanctionAuthority = pnsnSanctionAuthority;
		this.commutationAuthority = commutationAuthority;
		this.libraryNo = libraryNo;
		this.branchCode = branchCode;
		this.ropType = ropType;
		this.series = series;
		this.cvpAuthority = cvpAuthority;
	}




	public String getPensionerCode() {
	
		return pensionerCode;
	}
	
	public void setPensionerCode(String pensionerCode) {
	
		this.pensionerCode = pensionerCode;
	}
	
	public String getPpoNo() {
	
		return ppoNo;
	}
	
	public void setPpoNo(String ppoNo) {
	
		this.ppoNo = ppoNo;
	}
	
	public String getPensionerName() {
	
		return pensionerName;
	}
	
	public void setPensionerName(String pensionerName) {
	
		this.pensionerName = pensionerName;
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
	
	public String getApplicationNo() {
	
		return applicationNo;
	}
	
	public void setApplicationNo(String applicationNo) {
	
		this.applicationNo = applicationNo;
	}
	
	public String getPnsrFileNo() {
	
		return pnsrFileNo;
	}
	
	public void setPnsrFileNo(String pnsrFileNo) {
	
		this.pnsrFileNo = pnsrFileNo;
	}
	
	public String getGuardianName() {
	
		return guardianName;
	}
	
	public void setGuardianName(String guardianName) {
	
		this.guardianName = guardianName;
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
	
	public String getPensionType() {
	
		return pensionType;
	}
	
	public void setPensionType(String pensionType) {
	
		this.pensionType = pensionType;
	}
	
	public String getAccountNo() {
	
		return accountNo;
	}
	
	public void setAccountNo(String accountNo) {
	
		this.accountNo = accountNo;
	}
	
	public String getDesignation() {
	
		return designation;
	}
	
	public void setDesignation(String designation) {
	
		this.designation = designation;
	}
	
	public Date getJoiningDate() {
	
		return joiningDate;
	}
	
	public void setJoiningDate(Date joiningDate) {
	
		this.joiningDate = joiningDate;
	}
	
	public Date getRetirementDate() {
	
		return retirementDate;
	}
	
	public void setRetirementDate(Date retirementDate) {
	
		this.retirementDate = retirementDate;
	}
	
	public Date getBirthDate() {
	
		return birthDate;
	}
	
	public void setBirthDate(Date birthDate) {
	
		this.birthDate = birthDate;
	}
	
	public Date getIdentificationDate() {
	
		return identificationDate;
	}
	
	public void setIdentificationDate(Date identificationDate) {
	
		this.identificationDate = identificationDate;
	}
	
	public String getIdentityMark() {
	
		return identityMark;
	}
	
	public void setIdentityMark(String identityMark) {
	
		this.identityMark = identityMark;
	}
	
	public String getPnsnrAddr1() {
	
		return pnsnrAddr1;
	}
	
	public void setPnsnrAddr1(String pnsnrAddr1) {
	
		this.pnsnrAddr1 = pnsnrAddr1;
	}
	
	public String getPnsnrAddr2() {
	
		return pnsnrAddr2;
	}
	
	public void setPnsnrAddr2(String pnsnrAddr2) {
	
		this.pnsnrAddr2 = pnsnrAddr2;
	}
	
	public String getPnsnrAddrTown() {
	
		return pnsnrAddrTown;
	}

	public void setPnsnrAddrTown(String pnsnrAddrTown) {
	
		this.pnsnrAddrTown = pnsnrAddrTown;
	}

	public Integer getPinCode() {
	
		return pinCode;
	}
	
	public void setPinCode(Integer pinCode) {
	
		this.pinCode = pinCode;
	}
	
	public String getStateName() {
	
		return stateName;
	}
	
	public void setStateName(String stateName) {
	
		this.stateName = stateName;
	}
	
	public String getDistrictName() {
	
		return districtName;
	}
	
	public void setDistrictName(String districtName) {
	
		this.districtName = districtName;
	}
	
	public String getPanNo() {
	
		return panNo;
	}
	
	public void setPanNo(String panNo) {
	
		this.panNo = panNo;
	}
	
	public String getMobileNo() {
	
		return mobileNo;
	}
	
	public void setMobileNo(String mobileNo) {
	
		this.mobileNo = mobileNo;
	}
	
	public String getTeleNo() {
	
		return teleNo;
	}
	
	public void setTeleNo(String teleNo) {
	
		this.teleNo = teleNo;
	}
	
	public String getPnsnrEmailId() {
	
		return pnsnrEmailId;
	}
	
	public void setPnsnrEmailId(String pnsnrEmailId) {
	
		this.pnsnrEmailId = pnsnrEmailId;
	}
	
	public BigDecimal getBasicPensionAmount() {
	
		return basicPensionAmount;
	}
	
	public void setBasicPensionAmount(BigDecimal basicPensionAmount) {
	
		this.basicPensionAmount = basicPensionAmount;
	}
	
	public Date getCommencementDate() {
	
		return commencementDate;
	}
	
	public void setCommencementDate(Date commencementDate) {
	
		this.commencementDate = commencementDate;
	}
	
	public BigDecimal getCvpAmount() {
	
		return cvpAmount;
	}
	
	public void setCvpAmount(BigDecimal cvpAmount) {
	
		this.cvpAmount = cvpAmount;
	}
	
	public String getDcrgOrderNo() {
	
		return dcrgOrderNo;
	}
	
	public void setDcrgOrderNo(String dcrgOrderNo) {
	
		this.dcrgOrderNo = dcrgOrderNo;
	}
	
	public BigDecimal getGratuityAmount() {
	
		return gratuityAmount;
	}
	
	public void setGratuityAmount(BigDecimal gratuityAmount) {
	
		this.gratuityAmount = gratuityAmount;
	}
	
	public String getFamilyMemName() {
	
		return familyMemName;
	}
	
	public void setFamilyMemName(String familyMemName) {
	
		this.familyMemName = familyMemName;
	}
	
	public BigDecimal getFp1Amount() {
	
		return fp1Amount;
	}
	
	public void setFp1Amount(BigDecimal fp1Amount) {
	
		this.fp1Amount = fp1Amount;
	}
	
	public Date getFp1Date() {
	
		return fp1Date;
	}
	
	public void setFp1Date(Date fp1Date) {
	
		this.fp1Date = fp1Date;
	}
	
	public BigDecimal getFp2Amount() {
	
		return fp2Amount;
	}
	
	public void setFp2Amount(BigDecimal fp2Amount) {
	
		this.fp2Amount = fp2Amount;
	}
	
	public Date getFp2Date() {
	
		return fp2Date;
	}
	
	public void setFp2Date(Date fp2Date) {
	
		this.fp2Date = fp2Date;
	}
	
	public BigDecimal getOrgBf1436() {
	
		return orgBf1436;
	}
	
	public void setOrgBf1436(BigDecimal orgBf1436) {
	
		this.orgBf1436 = orgBf1436;
	}
	
	public BigDecimal getOrgAf1436() {
	
		return orgAf1436;
	}
	
	public void setOrgAf1436(BigDecimal orgAf1436) {
	
		this.orgAf1436 = orgAf1436;
	}
	
	public BigDecimal getOrgAf1156() {
	
		return orgAf1156;
	}
	
	public void setOrgAf1156(BigDecimal orgAf1156) {
	
		this.orgAf1156 = orgAf1156;
	}
	
	public BigDecimal getOrgBf1560() {
	
		return orgBf1560;
	}
	
	public void setOrgBf1560(BigDecimal orgBf1560) {
	
		this.orgBf1560 = orgBf1560;
	}
	
	public BigDecimal getOrgAfZp() {
	
		return orgAfZp;
	}
	
	public void setOrgAfZp(BigDecimal orgAfZp) {
	
		this.orgAfZp = orgAfZp;
	}
	
	public BigDecimal getReducedPension() {
	
		return reducedPension;
	}
	
	public void setReducedPension(BigDecimal reducedPension) {
	
		this.reducedPension = reducedPension;
	}

	
	public String getPnsnSanctionAuthority() {
	
		return pnsnSanctionAuthority;
	}

	
	public void setPnsnSanctionAuthority(String pnsnSanctionAuthority) {
	
		this.pnsnSanctionAuthority = pnsnSanctionAuthority;
	}

	
	public String getCommutationAuthority() {
	
		return commutationAuthority;
	}

	
	public void setCommutationAuthority(String commutationAuthority) {
	
		this.commutationAuthority = commutationAuthority;
	}

	public String getLibraryNo() {
		return libraryNo;
	}

	public void setLibraryNo(String libraryNo) {
		this.libraryNo = libraryNo;
	}

	public String getRopType() {
		return ropType;
	}

	public void setRopType(String ropType) {
		this.ropType = ropType;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public String getCvpAuthority() {
		return cvpAuthority;
	}

	public void setCvpAuthority(String cvpAuthority) {
		this.cvpAuthority = cvpAuthority;
	}

	
}
