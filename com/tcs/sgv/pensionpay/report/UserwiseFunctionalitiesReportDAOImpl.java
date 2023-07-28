package com.tcs.sgv.pensionpay.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.tcs.sgv.acl.valueobject.AclElementMst;
import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.business.reports.ReportDataFinder;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.core.service.ServiceLocator;

public class UserwiseFunctionalitiesReportDAOImpl extends DefaultReportDataFinder implements ReportDataFinder {
	private static final Logger gLogger = Logger.getLogger("UserwiseFunctionalitiesReportDAOImpl");
	ServiceLocator serviceLocator = null;
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/core/screen_number_labels");

	public Collection findReportData(ReportVO lObjReport, Object criteria) throws ReportException {
		List<Object> lLstRoleElementDtlsPenPay = new ArrayList<Object>();
		List<Object> lLstRoleElementDtlsPenProc = new ArrayList<Object>();
		List<Object> lLstDataList = new ArrayList<Object>();
		List<String> rowList = null;
		Map lMapRequestAttributes = null;
		Integer lIntSrNo = 1;
		String lStrScrNo = null;
		List<Object[]> lLstDtls = new ArrayList<Object[]>();
		Map<String, List<Object[]>> lMapRoleElementDtlsPay = new HashMap<String, List<Object[]>>();
		Map<String, List<Object[]>> lMapRoleElementDtlsProc = new HashMap<String, List<Object[]>>();
		Object[] lArrObj = null;
		String lStrScreenName = null;
		SortedSet<String> lSortedset = null;
		List<Object[]> lArrLstElementDtls = null;
		Iterator<Object[]> it = null;
		try {
			lMapRequestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
			serviceLocator = (ServiceLocator) lMapRequestAttributes.get("serviceLocator");

			UserwiseFunctionalitiesReportQueryDAOImpl lObjUserwiseFunctionalitiesReportQueryDAOImpl = new UserwiseFunctionalitiesReportQueryDAOImpl(AclElementMst.class,
					serviceLocator.getSessionFactory());

			if (lObjReport.getReportCode().equals("365470")) {
				lLstRoleElementDtlsPenPay = lObjUserwiseFunctionalitiesReportQueryDAOImpl.getRoleElementDtlsForPenPay();

				if (lLstRoleElementDtlsPenPay != null && !lLstRoleElementDtlsPenPay.isEmpty()) {
					for (Integer lInt = 0; lInt < lLstRoleElementDtlsPenPay.size(); lInt++) {
						lArrObj = (Object[]) lLstRoleElementDtlsPenPay.get(lInt);
						lStrScreenName = (String) lArrObj[0];
						lStrScrNo = gObjRsrcBndle.getString("PENSIONPAYMENT." + lStrScreenName.replace(" ", "").toUpperCase());
						lLstDtls = lMapRoleElementDtlsPay.get(lStrScrNo);
						if (lLstDtls != null) {
							lLstDtls.add(lArrObj);
						} else {
							lLstDtls = new ArrayList<Object[]>();
							lLstDtls.add(lArrObj);
						}
						lMapRoleElementDtlsPay.put(lStrScrNo, lLstDtls);

					}
					lSortedset = new TreeSet<String>(lMapRoleElementDtlsPay.keySet());
					for (String lStrScreenNo : lSortedset) {
						lArrLstElementDtls = lMapRoleElementDtlsPay.get(lStrScreenNo);
						if (lArrLstElementDtls != null && !lArrLstElementDtls.isEmpty()) {

							it = lArrLstElementDtls.iterator();
							while (it.hasNext()) {
								lArrObj = it.next();
								lStrScreenName = (String) lArrObj[0];
								lStrScrNo = gObjRsrcBndle.getString("PENSIONPAYMENT." + lStrScreenName.replace(" ", "").toUpperCase());
								rowList = new ArrayList<String>();
								rowList.add(lIntSrNo.toString());
								rowList.add(lStrScrNo);
								rowList.add(lStrScreenName);
								rowList.add((String) lArrObj[1]);
								lLstDataList.add(rowList);
								lIntSrNo++;
							}
						}
					}
				}

				lLstRoleElementDtlsPenProc = lObjUserwiseFunctionalitiesReportQueryDAOImpl.getRoleElementDtlsForPenProc();

				if (lLstRoleElementDtlsPenProc != null && !lLstRoleElementDtlsPenProc.isEmpty()) {
					for (Integer lInt = 0; lInt < lLstRoleElementDtlsPenProc.size(); lInt++) {
						lArrObj = (Object[]) lLstRoleElementDtlsPenProc.get(lInt);
						lStrScreenName = (String) lArrObj[0];
						lStrScrNo = gObjRsrcBndle.getString("PENSIONPROCESSING." + lStrScreenName.replace(" ", "").toUpperCase());
						lLstDtls = lMapRoleElementDtlsProc.get(lStrScrNo);
						if (lLstDtls != null) {
							lLstDtls.add(lArrObj);
						} else {
							lLstDtls = new ArrayList<Object[]>();
							lLstDtls.add(lArrObj);
						}
						lMapRoleElementDtlsProc.put(lStrScrNo, lLstDtls);

					}
					lSortedset = new TreeSet<String>(lMapRoleElementDtlsProc.keySet());
					for (String lStrScreenNo : lSortedset) {
						lArrLstElementDtls = lMapRoleElementDtlsProc.get(lStrScreenNo);
						if (lArrLstElementDtls != null && !lArrLstElementDtls.isEmpty()) {

							it = lArrLstElementDtls.iterator();
							while (it.hasNext()) {
								lArrObj = it.next();
								lStrScreenName = (String) lArrObj[0];
								lStrScrNo = gObjRsrcBndle.getString("PENSIONPROCESSING." + lStrScreenName.replace(" ", "").toUpperCase());
								rowList = new ArrayList<String>();
								rowList.add(lIntSrNo.toString());
								rowList.add(lStrScrNo);
								rowList.add(lStrScreenName);
								rowList.add((String) lArrObj[1]);
								lLstDataList.add(rowList);
								lIntSrNo++;
							}
						}
					}
				}
			}

		} catch (Exception e) {
			gLogger.error("UserwiseFunctionalitiesReportDAOImpl findReportData(): Exception is" + e, e);
		}
		return lLstDataList;
	}
}
