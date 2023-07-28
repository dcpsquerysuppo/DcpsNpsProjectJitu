package com.tcs.sgv.common.util;

import java.math.BigDecimal;

import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.text.RuleBasedNumberFormat;
import com.tcs.sgv.common.util.reports.IReportConstants;

public class Test {
	
	public static void main(String[] args) {
		
		String INDIAN_ENG_RULE_SET =   "%simplified:\n"
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
		
		EnglishDecimalFormat edf = new EnglishDecimalFormat();
		Converter con = new Converter();
		BigDecimal numberCrore = new BigDecimal(15000000000L);

		//Long number = 200000000000l;
		
		//System.out.println(con.convert1WithSpace(number));
		
		NumberFormat formatter = new RuleBasedNumberFormat(INDIAN_ENG_RULE_SET);
		
		System.out.println(formatter.format(numberCrore));
		
	}

}
