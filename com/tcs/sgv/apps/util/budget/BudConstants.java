/**
 * PACKAGE      : com.tcs.sgv.apps.util
 * FILENAME     : BudConstants.java
 * @VERSION     : 1.0
 * @AUTHOR      : Keyur
 * DATE         : 1/12/2005
 * REV.HISTORY  : 
 * --------------------------------------------------------------
 *    DATE          AUTHOR          DESCRIPTION
 *    
 * --------------------------------------------------------------
 */


/**
 * com.tcs.sgv.apps.util.BudConstants class contains all constant fields related to BUDGET
 * 
 */

package com.tcs.sgv.apps.util.budget;

import java.util.Locale;
import java.util.ResourceBundle;

public class BudConstants 
{
  static ResourceBundle lBudConstantsBundle= ResourceBundle.getBundle("resources/common/budget/SGVConstantsBud", Locale.getDefault());
  
  // Common Entries For Budget Applications
  public final static String BUDEST_FINYRDAO = lBudConstantsBundle.getString("BUDEST.FINYRDAO");
  public final static String BUDEST_HDDAO = lBudConstantsBundle.getString("BUDEST.HDDAO");
  public static String BUDEST_SUBJECTDAO = lBudConstantsBundle.getString("BUDEST.SUBJECTDAO");
  
  // For Standing Charges (Expenditure Estimation)
  public final static String EXPEST_EXPESTDAO = lBudConstantsBundle.getString("EXPEST.EXPESTDAO");
  
  // For Receipt Estimation
  public final static String RCPTEST_RCPTESTDAO = lBudConstantsBundle.getString("RCPTEST.RCPTESTDAO");
  public final static String RCPTEST_BPNCode = lBudConstantsBundle.getString("RCPTEST.BPNCode");
  public final static String RCPTEST_DemandCode = lBudConstantsBundle.getString("RCPTEST.DemandCode");
  public final static String RCPTEST_TXNLangID = lBudConstantsBundle.getString("RCPTEST.TXNLangID");
  public final static String RCPTEST_TXNLocID = lBudConstantsBundle.getString("RCPTEST.TXNLocID");

  public final static String RCPTEST_Status = lBudConstantsBundle.getString("RCPTEST.Status");
  public final static String RCPTEST_NotificationID =  lBudConstantsBundle.getString("RCPTEST.NotificationID");
  public final static String RCPTEST_Priority =  lBudConstantsBundle.getString("RCPTEST.Priority");

 //For Revised Receipt Estimation 
 public final static String REVRCPTEST_RCPTESTDAO = lBudConstantsBundle.getString("REVRCPT.REVRCPTESTDAO");
 
 // Form E Added by Vidhya for Item Continuous
  public final static String FRMEEST_FRMEDAO = lBudConstantsBundle.getString("FRMEEST.DAO");
  
 // Revised Expenditure Estimation 
  public final static String REXPEST_REXPESTDAO = lBudConstantsBundle.getString("REXPEST.REXPESTDAO");

 //For New Item Form CF(Expenditure Estimation)
  public final static String EXPFRMCF_EXPFRMCFDAO = lBudConstantsBundle.getString("EXPFRMCF.EXPFRMCFDAO");
  
  // Ronak Shah For Work in progress Form I (Expenditure Estimation)
  public final static String EXPFRMI_EXPFRMIDAO = lBudConstantsBundle.getString("EXPFRMI.EXPFRMIDAO");

 //For AG Upload
 public final static String BUD_UPLDACCNTSAG = lBudConstantsBundle.getString("BUD.UPLDACCNTSAG");
 
 // For Book Print
 public final static String BUD_BOOKPRINTDAO = lBudConstantsBundle.getString("EXPEST.BOOKPRINTDAO");
 
  //For New Work Form GH(Expenditure Estimation)
  public final static String EXPFRMGH_EXPFRMGHDAO = lBudConstantsBundle.getString("EXPFRMGH.EXPFRMGHDAO");
  public final static String EXPFRMGH_FrmTypeG = lBudConstantsBundle.getString("EXPFRMGH.FrmTypeG");
  public final static String EXPFRMGH_FrmTypeH = lBudConstantsBundle.getString("EXPFRMGH.FrmTypeH");
  public final static String EXPFRMGH_MjrHdType = lBudConstantsBundle.getString("EXPFRMGH.MjrhdType");
 // For Rcpt Book Print
 public final static String BUD_RCPTBOOKPRINTDAO = lBudConstantsBundle.getString("RCPTEST.BOOKPRINTDAO");

 // For Green  Book Print
 public final static String BUD_CMNBOOKPRINTDAO = lBudConstantsBundle.getString("BUDESTCMN.BOOKPRINTDAO");
 
 //For Surrender of Funds.
 public final static String BUDSURF_DAO= lBudConstantsBundle.getString("BUDSFUND.BUDSFUNDDAO");
 
 // For Reappropriation of Funds
 public final static String BUDREAPP_DAO= lBudConstantsBundle.getString("BUDREAPP.BUDREAPPDAO");

// For Brief Report
 public final static String BUD_BRFRPTDAO= lBudConstantsBundle.getString("BUDEST.BRFRPTDAO");
 
 // For Final Amount Admin Screen-->Added By Purav
public final static String BUDEXPFINALAMT_DAO= lBudConstantsBundle.getString("BUDEXPFINALAMT.BUDEXPFINALAMTDAO");

// For District Master
public final static String BUDDISTMST_DAO= lBudConstantsBundle.getString("BUDDISTMST.BUDDISTMSTDAO");

}