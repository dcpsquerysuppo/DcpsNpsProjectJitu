/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 11, 2011		Shripal Soni								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.tcs.sgv.core.service.ServiceImpl;


/**
 * Class Description -
 * 
 * 
 * @author Shripal Soni
 * @version 0.1
 * @since JDK 5.0 Feb 11, 2011
 */
public class CommonFunctionsServiceImpl extends ServiceImpl implements CommonFunctionsService {

	public List<Date> getWeekendHolidayList(Date lDtNext, List<Date> holidayList) {

		// boolean isHoliday = false;
		Calendar lObjCalendar = new GregorianCalendar();

		// Date d = new Date(lDtNext.getTime());

		lObjCalendar.setTime(lDtNext);

		lObjCalendar.set(lObjCalendar.get(Calendar.YEAR), lObjCalendar.get(Calendar.MONTH), lObjCalendar.get(Calendar.DATE));

		// ---------Addition of 2nd and 4th Saturdays and all Sundays of the
		// current year starting from current month into Holiday List starts
		// <<<<<<<<<<
		int lIntNextyears = lObjCalendar.get(Calendar.YEAR) + 3;
		for (int years = (lObjCalendar.get(Calendar.YEAR) + 1); years <= lIntNextyears; years++) {
			for (int months = lObjCalendar.get(Calendar.MONTH); months <= 11; months++) {
				lObjCalendar.set(Calendar.MONTH, months);
				int maxDays = lObjCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				int j = 0;
				lObjCalendar.set(Calendar.DATE, 1);
				for (int i = 1; i <= maxDays; i++) {
					if ((lObjCalendar.get(Calendar.DAY_OF_WEEK) == 7)) {
						j = j + 1;
						// If day is Even Saturday
						if ((j % 2) == 0) {
							holidayList.add(lObjCalendar.getTime());
						}
					}
					// ----If day is Sunday
					if ((lObjCalendar.get(Calendar.DAY_OF_WEEK) == 1)) {
						holidayList.add(lObjCalendar.getTime());
					}
					lObjCalendar.add(Calendar.DATE, 1);
				}
			}
			lObjCalendar.set(Calendar.YEAR, years);
			lObjCalendar.set(Calendar.MONTH, 0);
		}
		// ---------Addition of 2nd and 4th Saturday and all Sundays of the
		// current year starting from current month into Holiday List ends
		// >>>>>>>>>>>>>>>>>>>
		return holidayList;

	}
}
