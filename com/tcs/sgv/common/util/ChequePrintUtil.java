/*
 * Author : Jignesh Sakhiya
 * 
 * Revision History:
 * 28/01/08 Modification for alinment of cheque
 * */
package com.tcs.sgv.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.text.RuleBasedNumberFormat;

import com.tcs.sgv.common.service.IFMSCommonServiceImpl;

public class ChequePrintUtil {
	
	private StringBuilder strSrcLogBuilder = null;
	
	private final static String INDIAN_ENG_RULE_SET_FOR_CHQ =   "%simplified:\n"
	    + "    -x: Minus >>;\n"
	    + "    x.x: << And >>;\n"
	    + "    Zero; One; Two; Three; Four; Five; Six; Seven; Eight; Nine;\n"
	    + "    Ten; Eleven; Twelve; Thirteen; Fourteen; Fifteen; Sixteen;\n"
	    + "    Seventeen; Eighteen; Nineteen;\n"
		+ "    20: Twenty[ >>];\n"
		+ "    30: Thirty[ >>];\n"
		+ "    40: Forty[ >>];\n"
		+ "    50: Fifty[ >>];\n"
		+ "    60: Sixty[ >>];\n"
		+ "    70: Seventy[ >>];\n"
		+ "    80: Eighty[ >>];\n"
		+ "    90: Ninety[ >>];\n"
	    + "    100: << Hundred[ >>];\n"
	    + "    1000: << Thousand[ >>];\n"
	    + "    1,00,000: << Lac[ >>];\n"
	    + "    1,00,00,000: << Crore[ >>];\n";
	
	public ChequePrintUtil() {
	}

	//EnglishDecimalFormat edf = new EnglishDecimalFormat();

	public String generatePDF(ArrayList lArrChequeList, ArrayList tokenList) throws Exception {
		StringBuffer sb = new StringBuffer();
		String lStrResult = "";
		try{
			strSrcLogBuilder = new StringBuilder("------------------------ SOURCE - START ------------------------------------");
			strSrcLogBuilder.append(System.getProperty("line.separator"));
		}catch (Exception e) {}
		try {
			for (int i = 0; i < lArrChequeList.size(); i++) {
				String table = "" ;//generateChequeTemplate((TrnChequeDtls) lArrChequeList.get(i), tokenList.get(i).toString());
				sb.append(table);
			}
			if (sb.toString().length() > 0) {
				lStrResult = sb.toString().substring(0, sb.length() - 1);
				//lStrResult = sb.toString();
			}
			
		} catch (Exception de) {
			throw de;
		}
		
		try{
			strSrcLogBuilder.append("------------------------ SOURCE - END ------------------------------------");
		//	new PrintLogger().writePrintLog(strSrcLogBuilder.toString());
		}catch (Exception e) {}

		return lStrResult;
	}

	 private static  List arrangeString(String  strValue,int Width)
	    {
	    	List al = new ArrayList();
	    	StringTokenizer oTokenizer = new StringTokenizer(strValue," ");
	    	String LastLine = "";
	    	while(oTokenizer.hasMoreElements())
	    	{
	    		String strToken =oTokenizer.nextToken();
	    		if(strToken.length() >= Width )
	    		{
	    			List oSpl =null;
	    			if(LastLine.equals(""))
	    				oSpl = getSplitedString(strToken,Width);
	    			else
	    				oSpl = getSplitedString(LastLine + "" + strToken,Width);
	    			for(int i = 0;i < oSpl.size()-1 ; i++)
	    				al.add(oSpl.get(i));
	    			
	    			LastLine = oSpl.get(oSpl.size()-1).toString().trim();
	    		}
	    		else
	    		{
	    			if((LastLine.length() + strToken.length()) < Width)
	    			{	
	    				if(!"".equals(LastLine))
	    					LastLine = LastLine + "" + strToken;
	    				else
	    					LastLine =  strToken;
	    				
	    			}
	    			else
	    			{
	    				al.add(String.format("%-"+Width + "s", LastLine.trim()));
	    				LastLine =strToken;
	    			}
	    		}
	    	}
	    	if(!LastLine.equals(""))al.add(String.format("%-"+Width + "s", LastLine));
	    	return al;
	    }
	 private static List getSplitedString(String  strValue,int Width)
	    {
	    	List al = new ArrayList();
	    	int beginIndex = 0, endIndex =Width;
	    	strValue = strValue.trim();
	    	int sLength = strValue.length();
	    	String sFormat = "%-" + Width + "s";
	    	
	    	if(sLength <= Width )
	    	{  
	    		al.add(String.format(sFormat , strValue));
	    		return al;
	    	}
	    	 while(endIndex < sLength)
	    	 {
	    	    String token = strValue.substring(beginIndex,endIndex);
	    		al.add(String.format(sFormat , token));
	    		beginIndex = endIndex;
	    		endIndex = beginIndex + Width;
	    	 }
	    	 if(beginIndex < sLength)
	    		 al.add(String.format(sFormat ,strValue.substring(beginIndex)));
	    	 
	    	 return al;
	    }
}