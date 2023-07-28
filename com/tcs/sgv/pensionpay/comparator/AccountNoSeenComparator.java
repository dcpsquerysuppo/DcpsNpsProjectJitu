package com.tcs.sgv.pensionpay.comparator;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tcs.sgv.pensionpay.valueobject.PensionerSeenDtlsVO;


public class AccountNoSeenComparator implements Comparator {

	public int compare(Object o1, Object o2) 
	{

		int branch1 = 0;
		int branch2 = 0;
		BigDecimal caseOwner1 = BigDecimal.ZERO;
		BigDecimal caseOwner2 = BigDecimal.ZERO;
		String acc1 = "0";
		String acc2 = "0";
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
		if(((PensionerSeenDtlsVO) o1).getHeadCode() != null)
			headCode1 = ((PensionerSeenDtlsVO) o1).getHeadCode();
		if(((PensionerSeenDtlsVO) o2).getHeadCode() != null)
			headCode2 = ((PensionerSeenDtlsVO) o2).getHeadCode();
		
		if(!((PensionerSeenDtlsVO) o1).getAccountNo().equals(""))
		  acc1 = ((PensionerSeenDtlsVO) o1).getAccountNo();
		if(!((PensionerSeenDtlsVO) o2).getAccountNo().equals(""))
		  acc2 = ((PensionerSeenDtlsVO) o2).getAccountNo();

		//New Code
		Pattern p = Pattern.compile("[0-9]");
		Matcher m1 = p.matcher(acc1);
		Matcher m2 = p.matcher(acc2);
		
		String lStrAcc1 = "";
		String lStrAcc2 = "";
		
		int flag1 = 0;
		while(m1.find())
		{	
			flag1 = 1;
			lStrAcc1 += m1.group();
		}
		if(flag1 == 0)
		{
			lStrAcc1 = acc1;
		}
		
		int flag2 = 0;
		while(m2.find())
		{
			flag2 = 1;
			lStrAcc2 += m2.group();
		}
		if(flag2 == 0)
		{
			lStrAcc2 = acc2;
		}
		//New Code		
		
		if(caseOwner1.compareTo(caseOwner2)>0)
			return 1;
		else if(caseOwner1.compareTo(caseOwner2)<0)
			return -1;
		else
		{
			if(branch1 > branch2)
				return 1;
			else if(branch1 < branch2)
				return -1;
			else {
				if (headCode1 > headCode2)
					return 1;
				else if (headCode1 < headCode2)
					return -1;	
				else
				{
					//New Code
					if (Long.parseLong(lStrAcc1) > Long.parseLong(lStrAcc2))
						return 1;
					else if (Long.parseLong(lStrAcc1) < Long.parseLong(lStrAcc2))
						return -1;
					else
						return 0;	
					//New Code
				}
			}
		}
	}
}
