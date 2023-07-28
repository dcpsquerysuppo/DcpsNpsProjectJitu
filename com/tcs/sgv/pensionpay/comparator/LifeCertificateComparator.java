package com.tcs.sgv.pensionpay.comparator;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tcs.sgv.pensionpay.valueobject.LifeCertificateVO;


public class LifeCertificateComparator implements Comparator 
{


	public int compare(Object o1, Object o2) 
	{
		String branchName1 = "";
		String branchName2 = "";
		
		BigDecimal headCode1 = BigDecimal.ZERO;
		BigDecimal headCode2 = BigDecimal.ZERO;
		
		String ledger1 = "0";
		String ledger2 ="0";
		String page1 = "0";
		String page2 = "0";

		String ppoNo1 = "0";
		String ppoNo2 = "0";
		
		
		if(!((LifeCertificateVO) o1).getBranchName().equals(""))
			branchName1 = ((LifeCertificateVO) o1).getBranchName();
		if(!((LifeCertificateVO) o2).getBranchName().equals(""))
			branchName2 = ((LifeCertificateVO) o2).getBranchName();
		if(((LifeCertificateVO) o1).getHeadCode() != null)
			headCode1 = ((LifeCertificateVO) o1).getHeadCode();
		if(((LifeCertificateVO) o2).getHeadCode() != null)
			headCode2 = ((LifeCertificateVO) o2).getHeadCode();
		if(!((LifeCertificateVO) o1).getLedgerNo().equals(""))
			 ledger1 = ((LifeCertificateVO) o1).getLedgerNo();
		if(!((LifeCertificateVO) o2).getLedgerNo().equals(""))
		     ledger2 = ((LifeCertificateVO) o2).getLedgerNo();
		if(!((LifeCertificateVO) o1).getPageNo().equals(""))
			 page1 = ((LifeCertificateVO) o1).getPageNo();
		if(!((LifeCertificateVO) o2).getPageNo().equals(""))
			 page2 = ((LifeCertificateVO) o2).getPageNo();
		if(!((LifeCertificateVO) o1).getPpoNo().equals(""))
			 ppoNo1 = ((LifeCertificateVO) o1).getPpoNo();
		if(!((LifeCertificateVO) o2).getPpoNo().equals(""))
			 ppoNo2 = ((LifeCertificateVO) o2).getPpoNo();

		
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
		
		
		if (branchName1.compareTo(branchName2) > 0)
			return 1;
		else if (branchName1.compareTo(branchName2) < 0)
			return -1;
		else {
			if (headCode1.compareTo(headCode2) > 0)
				return 1;
			else if (headCode1.compareTo(headCode2) < 0)
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
							{
								if (ppoNo1.compareTo(ppoNo2) > 0)
									return 1;
								else if (ppoNo1.compareTo(ppoNo2) < 0)
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
