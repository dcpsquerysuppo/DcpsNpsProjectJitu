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

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.pensionpay.valueobject.CmnHolidayMst;


/**
 * Class Description -
 * 
 * 
 * @author Shripal Soni
 * @version 0.1
 * @since JDK 5.0 Feb 14, 2011
 */
public interface CmnHolidayMstDAO extends GenericDao<CmnHolidayMst, Long> {

	public ArrayList<Date> getHolidayList(Date lDtCurrDate, Date lDtAf3Years) throws Exception;
}
