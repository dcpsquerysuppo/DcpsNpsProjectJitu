package com.tcs.sgv.dcps.dao;

import java.util.List;

public interface DcpsEmpStatReportDao {

List getDataOnLoad();

List getDataOnLoadForTo(String strLocationCode);

}
