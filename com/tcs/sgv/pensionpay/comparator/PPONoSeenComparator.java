package com.tcs.sgv.pensionpay.comparator;

import java.math.BigDecimal;
import java.util.Comparator;

import com.tcs.sgv.pensionpay.valueobject.MonthlyPensionBillVO;
import com.tcs.sgv.pensionpay.valueobject.PensionerSeenDtlsVO;;


public class PPONoSeenComparator implements Comparator 
{

	public int compare(Object o1, Object o2) {

		
		int branch1 = 0;
		int branch2 = 0;
		BigDecimal caseOwner1 = BigDecimal.ZERO;
		BigDecimal caseOwner2 = BigDecimal.ZERO;
		Long headCode1 = 0L;
		Long headCode2 = 0L;
		
		if(!((PensionerSeenDtlsVO) o1).getBranchCode().equals(""))
			branch1 = Integer.parseInt((((PensionerSeenDtlsVO) o1).getBranchCode().toString()));
		if(!((PensionerSeenDtlsVO) o2).getBranchCode().equals(""))
			branch2 = Integer.parseInt((((PensionerSeenDtlsVO) o2).getBranchCode().toString()));
		
		if(!((PensionerSeenDtlsVO) o1).getCaseOwner().equals(""))
			caseOwner1 = ((PensionerSeenDtlsVO) o1).getCaseOwner();
		if(!((PensionerSeenDtlsVO) o2).getCaseOwner().equals(""))
			caseOwner2 = ((PensionerSeenDtlsVO) o2).getCaseOwner();
		
		if(((PensionerSeenDtlsVO) o1).getHeadCode() != null)
			headCode1 = ((PensionerSeenDtlsVO) o1).getHeadCode();
		if(((PensionerSeenDtlsVO) o2).getHeadCode() != null)
			headCode2 = ((PensionerSeenDtlsVO) o2).getHeadCode();
		
		
		String ppo1 = ((PensionerSeenDtlsVO) o1).getPpoNo();
		String ppo2 = ((PensionerSeenDtlsVO) o2).getPpoNo();
		
		
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
			else
			{
				if (headCode1 > headCode2)
					return 1;
				else if (headCode1 < headCode2)
					return -1;	
				else
					return ppo1.compareTo(ppo2);	
						
			}
		}
	}
}
