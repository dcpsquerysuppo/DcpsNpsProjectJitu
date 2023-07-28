package com.tcs.sgv.pensionpay.comparator;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tcs.sgv.pensionpay.valueobject.MonthlyPensionBillVO;


public class AccountComparator implements Comparator {

	public int compare(Object o1, Object o2) {

		boolean alphaNumeric1 = false;
		boolean alphaNumeric2 = false;

		long headCode1 = Long.parseLong((((MonthlyPensionBillVO) o1).getHeadCode().toString()));
		long headCode2 = Long.parseLong((((MonthlyPensionBillVO) o2).getHeadCode().toString()));
		String acc1 = ((MonthlyPensionBillVO) o1).getAccountNo();
		String acc2 = ((MonthlyPensionBillVO) o2).getAccountNo();

		Pattern p = Pattern.compile("[^0-9]");
		Matcher m1 = p.matcher(acc1);
		Matcher m2 = p.matcher(acc2);

		if (m1.find())
			alphaNumeric1 = true;
		if (m2.find())
			alphaNumeric2 = true;

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
				if (Long.parseLong(acc1) > Long.parseLong(acc2))

					return 1;

				else if (Long.parseLong(acc1) < Long.parseLong(acc2))

					return -1;
				else
					return 0;
			}

		}
	}
}
