package com.tcs.sgv.pensionpay.comparator;

import java.util.Comparator;

import com.tcs.sgv.pensionpay.valueobject.MonthlyPensionBillVO;


public class PPONumberComparator implements Comparator {

	public int compare(Object o1, Object o2) {

		int headCode1 = Integer.parseInt((((MonthlyPensionBillVO) o1).getHeadCode().toString()));
		int headCode2 = Integer.parseInt((((MonthlyPensionBillVO) o2).getHeadCode().toString()));
		String ppo1 = ((MonthlyPensionBillVO) o1).getPpoNo();
		String ppo2 = ((MonthlyPensionBillVO) o2).getPpoNo();

		if (headCode1 > headCode2)

			return 1;

		else if (headCode1 < headCode2)

			return -1;

		else {
			return ppo1.compareTo(ppo2);
		}
	}
}
