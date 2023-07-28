/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 15, 2011		Shripal Soni								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.dao;

import java.util.HashMap;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.pensionpay.valueobject.MstPnsnpmntSlots;


/**
 * Class Description -
 * 
 * 
 * @author Shripal Soni
 * @version 0.1
 * @since JDK 5.0 Feb 15, 2011
 */
public interface MstPnsnpmntSlotsDAO extends GenericDao<MstPnsnpmntSlots, Long> {

	public HashMap<Integer, MstPnsnpmntSlots> getMaxSlotNumber() throws Exception;
}
