package com.tcs.sgv.common.helper;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.TreeSet;

/**
 * A class for report related computation.
 * @author Jignesh Sakhiya
 * @author 219480
 * @version 1.0.1
 */
public class ReportHelper
{

  public static final Log log = LogFactory.getLog(ReportHelper.class);

  public static java.util.TimeZone  istTime = TimeZone.getTimeZone("IST");
  public static SimpleDateFormat oTimeFormat  =new SimpleDateFormat("h:mm a");
  public static SimpleDateFormat oDateTimeFormat  =new SimpleDateFormat("dd/MM/yyyy h:mm a");
  public static SimpleDateFormat oDateFormat  =new SimpleDateFormat("dd/MM/yyyy");
  static
  {
	  oTimeFormat.setTimeZone(istTime);
	  oDateTimeFormat.setTimeZone(istTime);
  }
  /**
   * This method generate list of payment/receipt report
   * 
   * @param payList
   * @param rptMtData
   */
  public static void procLstOfPayRcpt(List payList, List rptMtData)
  {}
  
  public static void procLstOfPayRcpt(List payList, List rptMtData,boolean firstList)
  {}

  /**
   * This method generate list of payment/receipt report
   * 
   * @param payList
   * @param rptMtData
   */
  @SuppressWarnings("unchecked")
public static void procCashBookPayRec(List payList, List rptMtData,
      List DateRange)
  {}
  public static String numberFormat(BigDecimal args)
  {
		boolean isNeg = (args.signum() == -1 ? true : false);
		if (isNeg)
			args = args.negate();
		StringBuffer sbInput = new StringBuffer(args.toString());
		if (sbInput.length() <= 3)
		{
			if(isNeg)
	    		return '-' + args.toString();
	        else	
	        	return args.toString();
		}
		StringBuffer sbOutput = new StringBuffer();
		int nSize = sbInput.length() - 1;

		for (int i = nSize; i > nSize - 3; i--)
			sbOutput.append(sbInput.charAt(i));
		sbOutput.append(',');

		for (int i = nSize - 3, j = 1; i >= 0; i--, j++) {
			sbOutput.append(sbInput.charAt(i));
			if (j % 2 == 0 && i != 0)
				sbOutput.append(',');
		}
		if (isNeg)
			return '-' + sbOutput.reverse().toString();
		else
			return sbOutput.reverse().toString();

	}
  public static String numberFormat(BigDecimal args,int nFractionalDigit)
  {
    
    try
    {
	boolean isNeg = (args.signum() == -1 ? true : false);
	if(isNeg) args= args.negate();
    StringBuffer sbInput = new StringBuffer(String.format("%." + nFractionalDigit + "f",  args));
    if(sbInput.length() <= 4 + nFractionalDigit ) 
    {
    	if(isNeg)
    		return '-' + sbInput.toString();
        else	
        	return sbInput.toString();
    }
    StringBuffer sbOutput = new StringBuffer();
    int nSize = sbInput.length()-1;
    
    for(int i=nSize; i > (nSize-4-nFractionalDigit)  ; i--)sbOutput.append(sbInput.charAt(i));
    sbOutput.append(',');
    
    for(int i=(nSize-4-nFractionalDigit),j=1; i >= 0  ; i--,j++)
    {
      sbOutput.append(sbInput.charAt(i));
       if(j %2 == 0 && i !=0 ) sbOutput.append(',');
    }
    if(isNeg)
    	return '-' + sbOutput.reverse().toString();
    else	
    	return sbOutput.reverse().toString();
    
    }
    catch(Exception e)
    {
    	log.error("Exception occured # \n" + e,e);
    }
    return args.toString();
  } 
 
}
