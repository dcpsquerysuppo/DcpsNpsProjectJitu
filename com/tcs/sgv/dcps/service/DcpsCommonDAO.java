/* Decompiler 2ms, total 787ms, lines 262 */
package com.tcs.sgv.dcps.service;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.valueobject.OrgDdoMst;
import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.dcps.valueobject.DdoOffice;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import java.util.Date;
import java.util.List;

public interface DcpsCommonDAO extends GenericDao {
   List getMonths();

   List getMonths1();

   List getYears();

   List getYears1();

   List getFinyears();

   List getFinyearsAfterCurrYear();

   String getDdoCode(Long var1);

   String getDdoCodeForDDO(Long var1);

   List<ComboValuesVO> getAllDesignation(Long var1, Long var2) throws Exception;

   List<ComboValuesVO> getAllDepartment(Long var1, Long var2) throws Exception;

   List<ComboValuesVO> getAllHODDepartment(Long var1, Long var2) throws Exception;

   List getBillGroups() throws Exception;

   Date getLastDate(Integer var1, Integer var2);

   Date getFirstDate(Integer var1, Integer var2);

   Object[] getSchemeNameFromBillGroup(Long var1);

   String getYearCodeForYearId(Long var1);

   String getMonthForId(Long var1);

   List getCadres();

   List getBankNames() throws Exception;

   List getBranchNames(Long var1) throws Exception;

   List getBranchNamesWithBsrCodes(Long var1) throws Exception;

   Long getIFSCCodeForBranch(Long var1) throws Exception;

   List getStateNames(Long var1) throws Exception;

   List getDistricts(Long var1) throws Exception;

   List getTaluka(Long var1) throws Exception;

   List getDesignations(String var1) throws Exception;

   String getCmnLookupNameFromId(Long var1);

   String getDesigNameFromId(Long var1);

   List getCurrentOffices(String var1);

   OrgDdoMst getDDOInfoVOForDDOCode(String var1);

   String getDdoNameForCode(String var1);

   OrgDdoMst getDdoVOForDdoCode(String var1);

   String getTreasuryNameForDDO(String var1);

   String getTreasuryCodeForDDO(String var1);

   List getCadreForDept(Long var1) throws Exception;

   List getParentDeptForDDO(String var1);

   List getDesigsForPFDAndCadre(Long var1) throws Exception;

   List getDeptNameFromDdoCode(String var1);

   List getAllTreasuries() throws Exception;

   List getAllTreasuriesAndSubTreasuries() throws Exception;

   String getLocNameforLocId(Long var1);

   String getCadreNameforCadreId(Long var1);

   Long getCadreCodeforCadreId(Long var1);

   Long getCadreIdforCadreCodeAndFieldDept(Long var1, Long var2);

   String getGroupIdforCadreId(Long var1);

   String getDddoOfficeNameNameforId(Long var1);

   List getOfficesForPost(Long var1) throws Exception;

   List<ComboValuesVO> getAllOrgType() throws Exception;

   MstEmp getEmpVOForEmpId(Long var1) throws Exception;

   List getDatesFromFinYearId(Long var1) throws Exception;

   String getCurrentInterestRate();

   String getFinYearForYearId(Long var1);

   Float getCurrentDARate(String var1);

   String getTreasuryNameForTreasuryId(Long var1);

   String getBranchNameForBranchCode(String var1);

   String getBankNameForBankCode(String var1);

   List getAllDDOForTreasury(String var1);

   List getLookupValuesForParent(Long var1) throws Exception;

   Boolean checkPFDForDDO(String var1);

   List getBillGroups(String var1) throws Exception;

   Long getDDOPostIdForDDOAsst(Long var1);

   Long getFinYearIdForYearCode(String var1);

   DdoOffice getDdoMainOffice(String var1);

   String getTreasuryCityForDDO(String var1);

   String getTreasuryShortNameForDDO(String var1);

   String getFinYearCodeForYearId(Long var1);

   List getAllOffices();

   String getFinYearIdForDate(Date var1);

   List getStates(Long var1);

   String getAdminBudgetCodeForDDO(String var1) throws Exception;

   Long getDDOAsstPostIdForDDO(String var1);

   Long getUserIdForPostId(Long var1);

   Long getDDOPostIdForDDO(String var1);

   String getFieldDeptOfDDO(String var1);

   String getFinYearDescForYearCode(String var1);

   List getBillGroupsNotDeletedAndNotDCPS(String var1) throws Exception;

   List getFinyearsForDelayedType();

   List getAllAdminDeptsForReportIncludingAllDepts() throws Exception;

   List getAllDDOInclAll(String var1);

   String getFinYearIdForYearDesc(String var1);

   String getMonthIdForName(String var1);

   List getFinyear();

   List getAllTreasuriesForInterest() throws Exception;

   List getAllTreasuriesAndSubTreasuriesForInterest() throws Exception;

   void UpdatePwd(String var1);

   String getAstDDONameForAstDDO(String var1, String var2);

   String validateEmpDobDojForResetPwd(String var1, String var2, String var3, String var4);

   List<ComboValuesVO> getReasonValues(Long var1);

   List getDDOoficeDesgn(String var1);

   List<ComboValuesVO> getDeptReasonValues(Long var1);

   String checkDDOCodePresent(String var1);

   List getDDOoficeTreasury(String var1);

   List getNewTreasury(String var1);

   List<ComboValuesVO> getTreasuryList();

   List<ComboValuesVO> getSubTreasuryList(Long var1);

   List getDdoCodeForAutoCompleteTresury(String var1, Long var2);

   String getMaxDDOCode(String var1);

   int updateDDOCode(String var1, String var2, String var3);

   String getAstUsername(String var1);

   int updateAstUserName(String var1, String var2);

   String getDDOUsername(String var1, String var2);

   String getLocationCode(String var1);

   int updatenewTreasuryDDOCode(String var1, String var2, String var3, String var4, String var5, String var6);

   List getPostId(String var1);

   void updateOldDDOCodePostIdLocId(String var1, String var2, String var3);

   List getnewDDOPostId(String var1);

   void updatenewTreasuryAsstDDOPostId(String var1, String var2);

   String checkAstUserName(String var1);

   String checkdeleteDDOcode();

   Long getdeleteDDOcode();

   int updateNewDDOCodeDelete(String var1, Long var2, String var3);

   List getLevel();

   List getPIPBForSevenPayEmployee(String var1);

   String getLevelForPayBand(String var1, String var2);

   String getStateLevelForPayBand(String var1, String var2);

   int getLevelIdForGivenLevel(String var1);

   int getStateLevelIdForGivenLevel(String var1);

   List getStateLevel();

   List getNEwMEDHTEBasicAsPerMAtrixForBunchPayPost(String var1, String var2, String var3);

   int getHTEMEDLevelIdForGivenLevel(String var1, String var2);

   List getHTEMEDLevel(String var1);

   String getHTEDMELevelForPayBand(String var1, String var2, String var3);

   List getAllDDO(String var1);

   List getAllTreasury(String var1, String var2);

   MstEmp getEmployeeDetails(String var1);
}