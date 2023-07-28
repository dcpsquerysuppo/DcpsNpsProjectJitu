/**
 * 
 */
package com.tcs.sgv.dcps.dao;

import java.util.List;


/**
 * @author 889550
 *
 */
public interface NsdlSrkaPranFileGenerationDAO {

	List getAllTreasuries();

	List getBatch(String treasuryno);

	List getEmpList(String treasurynos, String string, String string2);

	List getEmpListPrint(String treasurynos, String string, String string2, String file_name);

	String getFileId(String date);

	void updateFlagEmpListPrint(String treasurynos, String string,
			String string2, String file_name, String form_s1_id);

}
