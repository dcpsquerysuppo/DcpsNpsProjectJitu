package com.tcs.sgv.pensionpay.constants;


public class PensionConstants 
{
	public static final String CMNDAT = "DAT"; 
	public static final String CMNTO = "TO";
	public static final String CMNPPO = "PPO";
	public static final String ONLINEBILLSUBJECT = "PensionBill Processing";
	public static final String RECOVERYPENSION = "PENSION";
	public static final String RECOVERYMONTHLY = "Monthly";
	public static final String CUTPP="PP";
	public static final String CUTITCUT="IT Cut";
	public static final String CUTPT="PT";
	public static final String CUTPENSIONCUT="Pension Cut";
	public static final String CUTIT="IT";
	public static final String CUTST="ST";
	public static final String CUTCVP="CVP";
	public static final String CMNPBENEFIT = "PermanentBenefit";
	public static final String CMNTBENEFIT = "TemporaryBenefit";
	public static final String BUDGTPENSION = "PENSION";
	public static final String PENSIONCASESUBJECT = "PensionCase Processing";
	public static final String STATUSCONTINUE = "Continue";
	public static final String BILLPARTYTYPE = "PARTY";
	public static final String BILLTYPEPENSION = "9";
	public static final String BILLTYPEDCRG = "10";
	public static final String BILLTYPECVP = "11";
	
	public static final String IT_EDP_CODE = "9510";
	public static final String CONSTDATLOCCODE = "10051";
	public static final String ARREARTI = "TI";
	public static final String RECOVERYCHALLAN = "Challan";
	public static final String MNTHPENSION1 = "PENSION";
	public static final String MNTHMONTHLY = "Monthly";
	public static final String RECOVERYOMR = "OMR";
	public static final String RECOVERYDCRG = "DCRG";
	public static final String RELIEFBILL = "Relief Bill";
	public static final String BUDGTMR = "MR";
	public static final String ECS = "ECS";	
	
	public static final Long WFPENSIONAUDITORLEVEL = 50L;
	public static final Long WFDocIdPensionBill = 210004L;
	public static final Long PENSIONCASEDOCUMENTID = 210003L;
	//
	
	public static final String PENSION_Id = "9";
	public static final String CVP_Id = "11";
	public static final String DCRG_Id = "10";	
	public static final String Monthly_Id = "44";	
	public static final String MR_Id = "43";	
	public static final String OMR_Id = "100";	
	
	public static final String Id_9 = "PENSION";
	public static final String Id_11 = "CVP";
	public static final String Id_10 = "DCRG";	
	public static final String Id_44 = "Monthly";	
	public static final String Id_43 = "MR";	
	public static final String Id_100 = "OMR";
	
	public static final String PENSION = "PENSION";
	public static final String CVP = "CVP";
	public static final String DCRG = "DCRG";	
	public static final String Monthly = "Monthly";	
	public static final String MR = "MR";	
	public static final String OMR = "OMR";
	
	public static final Long DAT_TYPE_LOOKUP_ID = 100056L;
	public static final Long PPO_TYPE_LOOKUP_ID = 100100L;	
	
	public static final String CLOSEFORTRNFGUJ= "CloseDueToTransferInGUJ";
	public static final String CLOSEFORTRNFOTHR= "CloseDueToTransferInOTHER";
	public static final String CASETRNFGUJ = "CaseTransferInGUJ";
	public static final String CASETRNFOTHR= "CaseTransferInOTHER";
	public static final String TRANSFERED= "TRANSFERED";
	public static final String RECEIVED= "RECEIVED";
	public static final String REJECTED= "REJECTED";

	
	/*public static final STATUS.CONTINUE=Continue
	public static final STATUS.CLOSED=Closed
	public static final STATUS.CLOSEFORTRNFGUJ=CloseDueToTransferInGUJ
	public static final STATUS.CLOSEFORTRNFOTHR=CloseDueToTransferInOTHER
	
	public static final STATUS.CASETRNFOTHR=CaseTransferInOTHER
	public static final STATUS.TRANSFERED=TRANSFERED
	public static final STATUS.RECEIVED=RECEIVED
	public static final STATUS.REJECTED=REJECTED
	public static final STATUS.CRTD=Y
	public static final STATUS.SAVED=Saved
	public static final STATUS.PENCASEN=Normal
	public static final STATUS.PENCASEP=Provisional
	
	public static final BILLTYPE.OMR=100
	public static final ISSUBTRES.SHOW=none
	public static final STATUS.BillCreated=BCRTD
	public static final STATUS.BillWithAuditor=BAUD
	public static final STATUS.BillDiscarded=BDSCRD_DDO
	public static final STATUS.BillSentToTO=BSNT_TO
	public static final STATUS.BillApproved=BAPRVD_DDO
	public static final STATUS.BillApprovedByAuditor=BAPPRV_AUD
	public static final STATUS.BillRejected=BRJCT_AUD
	public static final STATUS.BillInwarded=BINWD
	public static final STATUS.APPROVED=APPROVED
	public static final STATUS.APPROVE=APPROVE
	public static final STATUS.CHANGE=CHANGED
	public static final CMN.TYPEFLAG=R
	public static final CMN.TYPEFLAGR=R
	public static final CMN.TYPEFLAGP=P
	public static final CMN.CONVERTTFLAG=C
	public static final CMN.OLD=OLD
	public static final CMN.REJECT=REJECT
	public static final CONST.DPPFLOCCODE=10052
	public static final CONST.PPOLOCCODE=10055
	public static final CMN.CASEINWD=CASEINWARD
	public static final FMLRL.WIFE.HUSBAND WIFE/HUSBAND
	public static final FMLRL.MOTHER MOTHER
	public static final FMLRL.FATHER FATHER
	public static final FMLRL.SON SON
	public static final FMLRL.DAUGHTER DAUGHTER
	public static final FMLRL.OTHER OTHER

	public static final ACTV.NO=N
	public static final ACTV.YES=Y

	public static final RECOVERY.PENSION=PENSION
	public static final RECOVERY.DCRG=DCRG
	public static final RECOVERY.CVP=CVP
	
	public static final RECOVERY.OMR=OMR

	
	public static final BUDGT.DCRG=DCRG
	public static final BUDGT.CVP=CVP
	public static final BUDGT.MR=MR

	public static final BUDGT.TYPECODE=1

	public static final LOOKUP.RECO_TYPE=Recovery_type
	public static final LOOKUP.DEDUCTION_TYPE=Deduction_type


	NETPNSN.BASIC=Basic
	NETPNSN.FP1=FP1
	NETPNSN.FP2=FP2


	public static final WF.ActionId.Create=1
	public static final WF.ActionId.Forward=2
	public static final WF.DocId.PensionBill=210004

	public static final Lookup.ACTIVE=Active
	public static final Lookup.DISACTIVE=Deactive
	public static final Lookup.GO_NGO=go_ngo
	public static final Lookup.BudgetType=OnlineBillBudgetType
	public static final Lookup.ClassOfExpenditure=ClassOfExp
	public static final Lookup.Fund=Fund
	public static final Lookup.TCBillType=TcBillType
	public static final Lookup.PartyChqType=PartyChqType

	
	public static final CMN.OnlineBillType=0

	public static final RECO.HBA=HBA
	public static final RECO.MCA=MCA
	public static final RECO.OTHERS=Others
	public static final RECO.DEDUCTIONTYPE=Deduction Type
	public static final RECO.A=A
	public static final RECO.B=B
	public static final RECO.HBAEDPCODE=9591
	public static final RECO.MCAEDPCODE=9592
	
	public static final PENSION.CASESUBJECT PensionCase Processing
	public static final PENSION.BILLSUBJECT PensionBill Processing
	public static final CMN.NEW NEW
	public static final STATUS.NEW NEW
	public static final CMN.Y Y

	public static final CMN.SAVED SAVED

	public static final RPT.AUDITOR=PENSION_INWARD
	public static final CMN.VOLUNTARY=Voluntary
	public static final CMN.FAMILYLOST=Family (Lost)
	public static final CMN.FAMILY=Family

	public static final CUT.PP=PP
	public static final CUT.ITCUT=IT Cut
	public static final CUT.PT=PT
	public static final CUT.PENSIONCUT=Pension Cut
	public static final CUT.IT=IT
	public static final CUT.ST=ST
	public static final CUT.CVP=CVP

	public static final ARREAR.MA=MA
	public static final ARREAR.PENSION=Pension
	public static final ARREAR.FP1=FP1
	public static final ARREAR.FP2=FP2
	public static final ARREAR.OMR=OMR

	
	public static final WFPENSION.CREATEACTION 1
	public static final WFPENSION.FORWARDACTION 2


	public static final FPATOAUDTIORSTATUS REGAUDITOR

	public static final MNTH.PENSION=Pension
	public static final MNTH.SCHEMETYPE=SchemeType
	
	
	public static final MNTH.Y=Y
	public static final MNTH.N=N
	public static final HDCD.PENSION=PENSION

	public static final CMN.INWARDMODEPHYSICAL 210014
	public static final CMN.INWARDMODEONLINE 210013
	public static final CMN.PBENEFIT PermanentBenefit
	public static final CMN.TBENEFIT TemporaryBenefit

	public static final MRCaseSubject=MRCaseWFlow
	public static final MRCounterLevelId=5
	public static final MRAuditorLevelId=20
	public static final WF.DocId.MRCase=210005

	
	public static final MR.CASESUBJECT=MR Case Ahmedabad Treasury Office*/

}
