/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 31, 2011		387502								
 *******************************************************************************
 */
package com.tcs.sgv.common.util;

/**
 * Class Description - This class contains various methods for Date and/or Time
 * Utilities and functionalities.
 * 
 * 
 * @author Vrajesh Raval
 * @version 0.1
 * @since JDK 5.0 Mar 31, 2011
 */
public class DateTimeUtil {

	private static Integer SECOND_CONVERT = 1000;

	/**
	 * 
	 * <H3>Description -</H3> This method will delay the processing for the
	 * provided time.
	 * 
	 * 
	 * @author Vrajesh Raval
	 * @param time
	 *            Time in seconds, for which delay is required.
	 */
	public static void waitFor(Integer time) {

		Long waitEndTime = System.currentTimeMillis() + (SECOND_CONVERT * time);
		while (System.currentTimeMillis() < waitEndTime) {

		}

	}

}
