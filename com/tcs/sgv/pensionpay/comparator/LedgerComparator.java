package com.tcs.sgv.pensionpay.comparator;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tcs.sgv.pensionpay.valueobject.MonthlyPensionBillVO;
import com.tcs.sgv.pensionpay.valueobject.PensionerSeenDtlsVO;


public class LedgerComparator implements Comparator {

	public int compare(Object o1, Object o2) {
		
		String ledger1 = "0";
		String ledger2 ="0";
		String page1 = "0";
		String page2 = "0";

		int headCode1 = Integer.parseInt((((MonthlyPensionBillVO) o1).getHeadCode().toString()));
		int headCode2 = Integer.parseInt((((MonthlyPensionBillVO) o2).getHeadCode().toString()));
		if(!((MonthlyPensionBillVO) o1).getLedgerNo().equals(""))
			 ledger1 = ((MonthlyPensionBillVO) o1).getLedgerNo();
		if(!((MonthlyPensionBillVO) o2).getLedgerNo().equals(""))
		     ledger2 = ((MonthlyPensionBillVO) o2).getLedgerNo();
		if(!((MonthlyPensionBillVO) o1).getPageNo().equals(""))
			 page1 = ((MonthlyPensionBillVO) o1).getPageNo();
		if(!((MonthlyPensionBillVO) o2).getPageNo().equals(""))
			 page2 = ((MonthlyPensionBillVO) o2).getPageNo();
		
		
		boolean alphaNumeric1 = false;
		boolean alphaNumeric2 = false;
		boolean alphaNumeric3 = false;
		boolean alphaNumeric4 = false;
		
		Pattern p = Pattern.compile("[^0-9]");
		Matcher m1 = p.matcher(ledger1);
		Matcher m2 = p.matcher(ledger2);
		
		Matcher p1 = p.matcher(page1);
		Matcher p2 = p.matcher(page2);
		
		if (m1.find())
			alphaNumeric1 = true;
		if (m2.find())
			alphaNumeric2 = true;
		
		if (p1.find())
			alphaNumeric3 = true;
		if (p2.find())
			alphaNumeric4 = true;
		
		if (headCode1 > headCode2)

			return 1;

		else if (headCode1 < headCode2)

			return -1;

		else {
			if (alphaNumeric2) 
			{
				return -1;
			} 
			else if (alphaNumeric1) 
			{
				return 1;
			} 
			else 
			{
				if (Long.parseLong(ledger1) > Long.parseLong(ledger2))
					return 1;
				else if (Long.parseLong(ledger1) < Long.parseLong(ledger2))
					return -1;
				else 
				{
					if (alphaNumeric4) 
					{
						return -1;
					} 
					else if (alphaNumeric3) 
					{
						return 1;
					} 
					else 
					{
						if (Long.parseLong(page1) > Long.parseLong(page2))
							return 1;
						else if (Long.parseLong(page1) < Long.parseLong(page2))
							return -1;
						else
							return 0;
					}
				}
			}
		}

	}

}
