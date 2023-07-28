package com.tcs.sgv.pensionpay.comparator;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tcs.sgv.pensionpay.valueobject.PensionerSeenDtlsVO;


public class LedgerNoSeenComparator implements Comparator 
{


	public int compare(Object o1, Object o2) 
	{

		String ledger1 = "0";
		String ledger2 ="0";
		String page1 = "0";
		String page2 = "0";
		int branch1 = 0;
		int branch2 = 0;
		BigDecimal caseOwner1 = BigDecimal.ZERO;
		BigDecimal caseOwner2 = BigDecimal.ZERO;
		Long headCode1 = 0L;
		Long headCode2 = 0L;
		
		if(((PensionerSeenDtlsVO) o1).getBranchCode() != "")
			branch1 = Integer.parseInt((((PensionerSeenDtlsVO) o1).getBranchCode().toString()));
		if(((PensionerSeenDtlsVO) o2).getBranchCode() != "")
			branch2 = Integer.parseInt((((PensionerSeenDtlsVO) o2).getBranchCode().toString()));
		if(!((PensionerSeenDtlsVO) o1).getCaseOwner().equals(""))
			caseOwner1 = ((PensionerSeenDtlsVO) o1).getCaseOwner();
		if(!((PensionerSeenDtlsVO) o2).getCaseOwner().equals(""))
			caseOwner2 = ((PensionerSeenDtlsVO) o2).getCaseOwner();
		if(!((PensionerSeenDtlsVO) o1).getLedgerNo().equals(""))
			 ledger1 = ((PensionerSeenDtlsVO) o1).getLedgerNo();
		if(!((PensionerSeenDtlsVO) o2).getLedgerNo().equals(""))
		     ledger2 = ((PensionerSeenDtlsVO) o2).getLedgerNo();
		if(!((PensionerSeenDtlsVO) o1).getPageNo().equals(""))
			 page1 = ((PensionerSeenDtlsVO) o1).getPageNo();
		if(!((PensionerSeenDtlsVO) o2).getPageNo().equals(""))
			 page2 = ((PensionerSeenDtlsVO) o2).getPageNo();
		if(((PensionerSeenDtlsVO) o1).getHeadCode() != null)
			headCode1 = ((PensionerSeenDtlsVO) o1).getHeadCode();
		if(((PensionerSeenDtlsVO) o2).getHeadCode() != null)
			headCode2 = ((PensionerSeenDtlsVO) o2).getHeadCode();

		
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
		
		
		if (caseOwner1.compareTo(caseOwner2) > 0)
			return 1;
		else if (caseOwner1.compareTo(caseOwner2) < 0)
			return -1;
		else {
			if (branch1 > branch2)
				return 1;
			else if (branch1 < branch2)
				return -1;
			else {
				if (headCode1 > headCode2)
					return 1;
				else if (headCode1 < headCode2)
					return -1;
				else {
					if (alphaNumeric2) {
						return -1;
					} else if (alphaNumeric1) {
						return 1;
					} else {
						if (Long.parseLong(ledger1) > Long.parseLong(ledger2))
							return 1;
						else if (Long.parseLong(ledger1) < Long.parseLong(ledger2))
							return -1;
						else {
							if (alphaNumeric4) {
								return -1;
							} else if (alphaNumeric3) {
								return 1;
							} else {
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
	}
}
