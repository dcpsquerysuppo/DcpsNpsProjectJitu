/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 14, 2011		Shripal Soni								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.pensionpay.valueobject.MstPensionChecklist;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionerChecklistDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPnsnpmntSchedular;


/**
 * Class Description -
 * 
 * 
 * @author Shripal Soni
 * @version 0.1
 * @since JDK 5.0 Feb 14, 2011
 */
public interface IdentificationSchedularDAO extends GenericDao {

	public List getScheduleDtls() throws Exception;

	public void updateScheduleStatus(Long argLocCode, Date argCurrDate) throws Exception;

	public ArrayList<TrnPnsnpmntSchedular> getLastAssignedSlotDetail(Long locCode) throws Exception;

	public List getSchedularObjFromPsnrCode(Long pnsrCode, Long locCode) throws Exception;

	public List<Object[]> getIdentificationLetterDtls(String[] lLstPnsrCode, String lStrLocCode) throws Exception;

	public Map<String, String> getValidFamilyMemberName(String[] lArrPnsrCode) throws Exception;

	List<MstPensionChecklist> getAllCheckList() throws Exception;

	void deletePnsrCheckList(String lStrPnsrCode) throws Exception;

	Map<String, List<String>> getSelectedDocsName(String[] lArrPnsrCode) throws Exception;
	
	public List<TrnPensionerChecklistDtls> getSelectedCheckList(String lStrPnsrCode) throws Exception;
	
	public List<String> getPensionerCode(String[] lLstPensionerCode,Integer lIntLocId, String lStrLetterType) throws Exception;

	public Long getLetterNo(String lStrPensionerCode, Integer lIntLocId, String lStrLetterType) throws Exception;
	
	public String getAuthorityName(Long lLngUserId) throws Exception;
	
	public List getAllDistrictName(String lStrStateCode) throws Exception;
	
	public List getAllStateName() throws Exception;
}