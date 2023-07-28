package com.tcs.sgv.common.constant;

/**
 * A class that contains constants or values pertaining to database field values
 * 
 * @author 206819
 */
public class DBConstants {

	public static final Short ST_BCRTD = 1;
	public static final Short ST_BAPRVD_DDO = 5;
	public static final Short ST_BS_TO = 10;
	public static final Short ST_BINWD = 20;
	public static final Short ST_BCRDX = 30;

	// ADDED BY NIRAV - 22-MAY-08 - Bill with Cheque Branch HA for Correction
	public static final Short ST_BILL_WITH_HA = 40;

	public static final Short ST_BAUD = 60;
	public static final Short ST_BAUD_ACC = 62;
	public static final Short ST_BAUD_ATO = 64;
	public static final Short ST_BAUD_TO = 66;
	public static final Short ST_BAPRV_AUD = 70;
	public static final Short ST_BRJCT_AUD = 80;
	public static final Short ST_CHQ_WITH_WRTR = 85;
	public static final Short ST_CHQ_WRTN = 90;
	public static final Short ST_CHQ_PRNT = 100;
	public static final Short ST_CHQ_CSTDN = 110;
	public static final Short ST_CHQ_CNTR = 120;
	public static final Short ST_RJTD_BILL_DSPTCH = 250;

	// Added for GPF Cheque dispatched
	public static final Short ST_GPF_CHQ_DSPTCH = 260;

	public static final Short ST_CHQ_DSPTCH_DDO = 130;
	public static final Short ST_VCHR_GEN = 140;
	public static final Short ST_VCHR_DSTRBTD = 150;
	public static final Short ST_DTL_PSTNG_DONE = 160;

	/* AUDIT STATUS FOR BILLS */
	public static final Short BILL_AUD_FOR = 0;
	public static final Short BILL_AUD_APPR = 1;
	public static final Short BILL_AUD_RJCT = 2;
	/* AUDIT STATUS FOR BILLS */

	public static final Short ST_CHQ_CAN = 210;
	public static final Short ST_CHQ_REVAL = 220;
	public static final Short ST_CHQ_RENM = 230;
	public static final Short ST_CHQ_DUP = 240;

	/* CONSTANTS FOR CATEGORIES OF BILL */
	public static final String CAT_TC_BILL = "TC";
	public static final String CAT_Regular_BILL = "Regular";
	public static final String CAT_Nil_BILL = "Nil";
	public static final String CAT_Regular_Chln = "Regular/Challan";
	/* CONSTANTS FOR CATEGORIES OF BILL */

	/* CONSTANTS FOR IFMS MODULES */
	public static final String BILL_PROCESSING = "TBP";
	public static final Long SUBTRSY_DEPARTMENT_ID = 100006l;
	/* CONSTANTS FOR IFMS MODULES : ENDS */

	// Following 3 Statuses for LC Cheques.
	public static final Short ST_LC_UNAPPROVED = 310;
	public static final Short ST_LC_PAID = 320;
	public static final Short ST_LC_UNPAID = 330;

	// PDPLA Cheque Status
	public static final Short ST_PDPLA_PAID = 500;

	public static String CATEGORY_BILL = "BILL";
	public static String CATEGORY_PPO = "PPO";
	public static String CATEGORY_MRCASE = "MRCASE";
	public static String CATEGORY_PNSNBILL = "PENSIONBILL";
	/*
	 * public static String ST_CHALLAN_GEN = "Challan Created"; public static
	 * String ST_CHALLAN_DIS = "Challan Distributed";
	 */

	/**
	 * User Location constants - Added by Sandeep Used in PDPLA and EMD to Check
	 * whether User is treasury user or sub treasury user
	 */
	public static final String TREASURY_USER = "TO";
	public static final String SUB_TREASURY_USER = "STO";

	// Look up Ids from cmn_lookup_mst
	public static final String INT_PAY_HEAD_2049 = "2049";
	public static final Long BUD_TYPE_LOOKUP = new Long(110001);
	public static final Long BILL_CODE_LOOKUP = new Long(100027);
	public static final Long IFMS_MODULE_LOOKUP = new Long(110041);

	public static final String MAJOR_HEAD_2030 = "2030";

	public static final String HEAD_CHARGEBLE_CFR_REF = "20300110201";
	public static final String HEAD_CHARGEBLE_JUDI_DIS = "20300110202";
	public static final String HEAD_CHARGEBLE_MJSR_REF = "20300210201";
	public static final String HEAD_CHARGEBLE_NJ_DIS = "20300210202";

	public static final String EDP_9999 = "9999";

	public static String BRCH_ID_CNTR = "100001";
	public static String BRCH_ID_BOOK_PAYMENT = "100002";
	public static String BRCH_ID_DPST = "100003";
	public static String BRCH_ID_AUDIT = "100004";
	public static String BRCH_ID_CHEQUE = "100005";
	public static String BRCH_ID_STAMP = "100006";
	public static String BRCH_ID_PENSION = "100007";
	public static String BRCH_ID_BOOK_RECEIPT = "100017";
	public static String BRCH_ID_PDPLA = "100022";
	public static String LC_DEPT_ID = "100008";

	public static String DEPT_TREASURY_OFFICE = "Treasury Office";
	public static String DEPT_PAO_OFFICE = "Pay and Accounts Office";

	public static String CHEQ_TYPE = "Cheque Type";
	public static String CHEQ_TYPE_BILL = "Bill";
	public static String CHEQ_TYPE_TC = "T Cheque";
	public static String CHEQ_TYPE_ECS = "ECS";
	public static String CHEQ_TYPE_LC = "LC";
	public static String CHEQ_TYPE_PDPLA = "PDPLA";
	public static String CHEQ_TYPE_EMD = "EMD";
	public static String CHEQ_TYPE_OTHER = "Other";

	public static String RPT_RCPT_TYPE_BILL = "Bill";
	public static String RPT_RCPT_TYPE_TE = "TE";
	public static String RPT_RCPT_TYPE_CHALLAN = "Challan";
	public static String RCPT_TCBILL = "TcBill";
	public static String RCPT_APPROVED = "Approved";
	public static String RCPT_PROCESSING = "Challan Processing";

	public static Integer CHALN_CRTED = 1;
	public static Integer CHALN_DSTIBUTED = 2;
	public static Integer CHALN_DTL_PSTNG_DONE = 3;

	public static Long RPT_DEPT_ID_8782 = 100008l;
	public static Long RPT_SUB_DEPT_ID_8782 = 100009l;

	// public static String VITO_CHQ_DRAWN = "CDRV";
	public static Short VITO_CHQ_DRAWN = Short.valueOf("0");
	// public static String VITO_CHQ_ADV = "CAV";
	public static Short VITO_CHQ_ADV = Short.valueOf("1");
	// public static String VITO_CHQ_DELVRY_ACC = "CDAV";
	public static Short VITO_CHQ_DELVRY_ACC = Short.valueOf("2");

	public static Short VITO_ECS_ADV = Short.valueOf("4");
	public static Short VITO_ECS_DELVRY_ACC = Short.valueOf("8");

	public static String VITO_AUD_DAILY = "AWDV";
	public static String VITO_AUD_BILLTYPE = "AWBTV";
	public static String VITO_BILL_TRANSREG_FOR_AUD = "BTRVBA";
	public static String VITO_BILL_TRANSREG_AFTER_AUD = "BTRVAA";
	public static String VITO_MRCASE_FOR_AUD = "MRCASEV";

	public static String VITO_INWD_REG_CRDX = "INWDREGCRDX";

	public static String OBJ_ATTACH_SHEET = "OBJ128";
	public static String DEFALT_BANK_CODE = "12000";
	public static String DEFAULT_BRANCH_CODE = "12001";
	public static String DEFAULT_RBI_CODE = "33000";

	// Constant For Budget Types Used in DSS starts

	public static short BUD_TYPE_PLAN_State_Plan = 6;
	public static short BUD_TYPE_PLAN_PlanBud_80 = 7;
	public static short BUD_TYPE_PLAN_PlanBud_15 = 8;
	public static short BUD_TYPE_PLAN_StatePlan_5 = 9;
	public static short BUD_TYPE_NONPLAN_State_Nonplan = 1;
	public static short BUD_TYPE_NONPLAN_Other_Agencies = 5;
	// public static short BUD_TYPE_NONPLAN_PlanBud_80 = 7;
	public static short BUD_TYPE_CSS_Central_Plan = 2;
	public static short BUD_TYPE_CSS_Fully_Centrally = 3;
	public static short BUD_TYPE_CSS_Partly_Centrally = 4;

	// Constant for Bill Type 99
	public static short BILL_TYPE_99 = 40; // Subject Id for bill type 99
	public final static String CHEQ_TYPE_GPF = "GPF Cheque";
	public final static String CHEQ_PARTY = "Party";
	public final static short BILL_TYPE_EMD = 23; // Subject Id for bill type
	// 81(EMD)

	// Rate of Interst of Bank
	public final static Double RATE_OF_INTREST = 6.00;

	public final static String EDP_EXP = "EXP";
	public final static String EDP_REC = "REC";
	public final static String EDP_RCP = "RCP";

	public final static String EDP_CAT_A = "A";
	public final static String EDP_CAT_B = "B";
	public final static String EDP_CAT_O = "O";
	// Constant For Budget Types Used in DSS ends

	public static final short CMN_OnlineBillType = 0;
	public static final short CMN_PhyBillType = 1;
	public static final short CMN_PensionBillType = 2;
	public static final String Demand_999 = "999";
	public static final String Lookup_ACTIVE = "Active";
	public static final String Lookup_DISACTIVE = "Deactive";
	public static final String Lookup_GO_NGO = "go_ngo";
	public static final String Lookup_BudgetType = "OnlineBillBudgetType";
	public static final String Lookup_ClassOfExpenditure = "ClassOfExp";
	public static final String Lookup_Fund = "Fund";
	public static final String Lookup_TCBillType = "TcBillType";
	public static final String Lookup_Contingency = "Contingency";
	public static final String Lookup_Contigancy = "Contigancy";
	public static final String Lookup_TempPermanent = "TempPermStatus";
	public static final String Lookup_EMPGROUP = "EmployeeGroup";
	public static final String Lookup_ClaimType = "ClaimType";
	public static final String Lookup_SchlrStpndType = "SchlrStpndType";
	public static final String CONST_Challan = "Challan";
	public static final String CONST_Bill = "Bill";

	public static final int WF_ActionId_Create = 1;
	public static final int WF_ActionId_Forward = 2;

	public static final String WF_DocId_OnlineBill = "110001";
	public static final String WF_DocId_PhysicalBill = "100003";
	public static final String WF_DocId_PensionBill = "210004";
	public static final String WF_DocId_PensionCase = "210003";
	public static final String WF_DocId_MRCase = "210005";
	public static final String WF_DocId_ChallanProc = "100005";
	public static final String WF_DocId_LF = "230001";
	public static final String WF_DocId_PFREVISION = "400001";

	public static final String FUND_PUBLIC = "Public Accounts";
	public static final String REPORT_BILLTRACKING_DDO = "110001";
	public static final String REPORT_BILLTRACKING_CASHBRANCH = "110002";

	public static final int WF_ORDINARY_DOC_TYPE = 1;

	public static final Short CHQ_REQ_STATUS_CLOSE = 0;
	public static final Short CHQ_REQ_STATUS_WITH_HA = 1;
	public static final Short CHQ_REQ_STATUS_WITH_PRNTR = 2;
	public static final Short CHQ_REQ_STATUS_WITH_CNTR = 3;

	public static final Short CHQ_REQ_TYPE_CANCEL = 1;
	public static final Short CHQ_REQ_TYPE_DUPLICATE = 2;
	public static final Short CHQ_REQ_TYPE_RENAME = 3;
	public static final Short CHQ_REQ_TYPE_REVALIDATE = 4;

	// For Expenditure Accounting

	public static final String DIS_ONSCREEN = "screen";
	public static final String DIS_TEXTFILE = "textfile";
	public static final String DIS_EXCELFILE = "excelfile";
	public static final String DIS_PDFFILE = "pdffile";

	public static final String REQUEST_OBJ = "requestObj";
	public static final String RESPONSE_OBJ = "responseObj";
	public static final String SERV_LOCATOR = "serviceLocator";
	public static final String ERROR_PAGE = "errorPage";
	public static final String CONST_FAIL = "FAIL";
	public static final String FROM_DATE = "fromDate";
	public static final String TO_DATE = "toDate";
	public static final String FROM_ISSUE_DATE = "fromIssueDate";
	public static final String TO_ISSUE_DATE = "toIssueDate";
	public static final String CONDENCE_ON = "<condence>";
	public static final String CONDENCE_OFF = "</condence>";

	// By Jignesh

	public static final String EDPCODE5701 = "5701";
	public static final String EDPCODE5801 = "5801";

	public static final String LEVEL_CRDX = "40";
	public static final String LEVEL_AUDITOR = "50";
	public static final String LEVEL_CHK_WR = "90";
	public static final String LEVEL_CNTR_OUT = "120";
	public static final String LEVEL_CHK_DA = "150";

	public static final String ACTION_APPRV = "1";
	public static final String ACTION_REJ = "2";
	public static final String ACTION_CORR = "3";
	public static final String ACTION_REPRSS = "4";

	public static final String HYR_DESC_BILL = "Bill Processing";
	public static final String HYR_DESC_TC = "TC Bill Processing";

	// By Varalika
	public static final String HYR_PENSION_BILL = "PensionBill Processing";
	public static final String HYR_PENSION_CASE = "PensionCase Processing";
	public static final String HYR_NIL_PENSION_CASE = "Nill PensionBill Processing";
	public static final String HYR_PENSION_MRCASE = "MRCaseWFlow";
	public static final String HYR_ONLINE_BILL = "Online Bill Creation";
	public static final String HYR_PENSION_LF = "Pension Processing";
	public static final String HYR_PFREVISION = "HYR_PFREVISION";

	public static final String DATE_FORMAT = "dd/MM/yyyy";
	public static final int DATA_TYPE_STRING = 1;
	public static final int DATA_TYPE_LONG = 2;
	public static final int DATA_TYPE_INTEGER = 3;
	public static final int DATA_TYPE_BIGDECIMAL = 4;
	public static final String DATA_indianCurrencyStyle = "#,##,##0.00";

	// krupa
	public static String VITO_PENSIONRQST_FOR_AUDIT = "PENSIONRQST";

	// smit
	public static String PPO_LOC = "10055";
	public static String AHM_LOC = "10101";
	public static String VACANT_USER_ID = "100055";

	public static Short BILL_TYPE_ADVANCE = 85;
	public static Short ADVANCE_BILL_TYPE_SUB_ID = 25;
	public static Short BILL_TYPE_PAYBILL = 30;
	public static Long FIFTH_PAY_LOOKUP_ID = Long.valueOf("120100");
	public static Long SIXTH_PAY_LOOKUP_ID = Long.valueOf("120200");

	// Status For ECS File
	public static final Short ST_ECS_GEN = 10;
	public static final Short ST_ECS_FRWD = 20;
	public static final Short ST_ECS_AUTH = 30;
	public static final Short ST_ECS_MAN = 40;
	public static final Short ST_ECS_DISCARD = 50;

	// Bill Status-Shivani

	public static final Short ST_BILL_CREATED = 5;
	public static final Short ST_BILL_FORW_TO_ATO = 10;
	public static final Short ST_BILL_APPROVED = 20;
	public static final Short ST_BILL_FORW_TO_TAUD = 30;
	public static final Short ST_BILL_FORW_TO_USER = 40;
	public static final Short ST_BILL_ECS_GENERTED = 50;
	public static final Short ST_BILL_ECS_AUTH = 80;
	public static final Short ST_BILL_REJECTED = 60;
	public static final Short ST_BILL_REJECTED_BY_TRSRY = 70;
	public static final Short ST_BILL_DISCARD = -1;
	public static final Short ST_BILL_ARCHEIVED = -15;

	// Supplementary Bill Status
	
	public static final Short SUPPL_REQ_GENERATED = 1;
	public static final Short SUPPL_BILL_UPDATED = 2;
	
}