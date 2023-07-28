package com.tcs.sgv.dcps.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.dcps.valueobject.FrmFormS1Dtls;

public interface FormS1UpdateDAO extends GenericDao{

	List getEmpListForFrmS1Edit(String strDDOCode, String flag, String txtSearch, String isDeputation);

	List getRelationList();
	//jitu add the arg  in mathode
	void insertRecordToS1(FrmFormS1Dtls ffs, String doj, String nominee1DOB, String nominee2DOB, String nominee3DOB,String strDDOCode,String OrphanPerson, Long attachment_Id_order);

	Long checkFormS1(String strSevarthId);

	List getEmpDesigList(String strDDOCode);

	String chkFrmUpdatedByLgnDdo(String txtSearch);

	

	

}
