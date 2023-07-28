package com.tcs.sgv.common.util;

import java.util.*;
import java.math.BigDecimal;

public class ConvertAmountInWords {
	
	private static ResourceBundle  myResources= null;

	private static String convertLessThanOneThousand(long number) 
	{
		String soFar;

		if (number % 100 < 20){
			soFar = myResources.getString("CMN.NUM_NAMES_WITH_SPACE["+(int)number % 100+"]");
			number /= 100;
		}
		else {
			soFar = myResources.getObject("CMN.NUM_NAMES_WITH_SPACE["+(int)number % 10+"]").toString();
			number /= 10;

			soFar = myResources.getObject("CMN.TENS_NAMES_WITH_SPACE["+(int)number % 10 +"]") + soFar;
			number /= 10;
		}
		if (number == 0) return soFar;
		return myResources.getObject("CMN.NUM_NAMES_WITH_SPACE["+(int)number +"]").toString() + myResources.getObject("CMN.HUNDRED_WITH_SPACE") + soFar;
	}
	/*public static String convert(long number) 
	{
		Locale locale=new Locale("en_US");
		return convert(number,locale);
	}*/
	public static String convert(long number,Locale locale) 
	{
		String s ="";
		myResources=ResourceBundle.getBundle("com.tcs.sgv.common.util.classBundle.CurrencyLabel", locale);
		if(number  > 10000000 )
		{
			s = convert1(number/10000000) + myResources.getObject("CMN.MAJOR_NAMES_WITH_SPACE[3]"); 
			if (number%100000000 != 0)
				s+=convert1(number%10000000);  
		}else
			s = convert1(number); 
		return s;
	}
	
	/*public static String convert(BigDecimal number) 
	{
		Locale locale=new Locale("en_US");
		return convert(number,locale);
	}*/
	public static String convert(BigDecimal number,Locale locale) 
	{
		myResources=ResourceBundle.getBundle("com.tcs.sgv.common.util.classBundle.CurrencyLabel", locale);
		String s ="";
		number = number.setScale(2,BigDecimal.ROUND_HALF_UP);
		int f = (int)((number.doubleValue()- number.longValue()) * 100);

		long n = number.longValue();
		if(n  > 10000000 )
		{
			s = convert1(n/10000000) + myResources.getObject("CMN.MAJOR_NAMES_WITH_SPACE[3]"); 
			if (n%100000000 != 0)
				s+=convert1(n%10000000);  
		}else
			s = convert1(n);

		if(f > 0)
		{
			String s1 = convert(f,locale);
			s = s + " " + s1 + myResources.getObject("CMN.PAISA_WITH_SPACE");
		}

		return s ;
	}


	public static String convert1(long number) {

		//if (number == 0) { return myResources.getObject("CMN.ZERO_WITH_SPACE").toString(); }
		if (number == 0) { return ""; }// done because its not working properly with 10000000 amount

		String prefix = "";

		if (number < 0) {
			number = -number;
			prefix = myResources.getObject("CMN.NEGATIVE_WITH_SPACE").toString();
		}

		String soFar = "";
		int place = 0;

		long n = (long)number % 1000;
		if (n != 0)
		{
			String s = convertLessThanOneThousand(n);
			soFar = s + myResources.getObject("CMN.MAJOR_NAMES_WITH_SPACE["+place+"]") + soFar;
		}
		place++;
		number /= 1000;

		while (number > 0)
		{
			n = (int)number % 100;
			if (n != 0)
			{
				String s = convertLessThanOneThousand(n);
				soFar = s + myResources.getObject("CMN.MAJOR_NAMES_WITH_SPACE["+place+"]") + soFar;
			}
			place++;
			number /= 100;
		} 
		return (prefix + soFar).trim();
	}
	/*public static String convertWithSpace(BigDecimal number) // A class method expecting the "locale" (The session scope variable)  
	{
		Locale local_locale=new Locale("en_US");
		return convertWithSpace(number,local_locale);
	}*/
	public static String convertWithSpace(BigDecimal number, Locale locale) // A class method expecting the "locale" (The session scope variable)  
	{
		myResources=ResourceBundle.getBundle("com.tcs.sgv.common.util.classBundle.CurrencyLabel", locale);
		String s ="";
		number = number.setScale(2,BigDecimal.ROUND_HALF_UP);
		int f = (int)((number.doubleValue()- number.longValue()) * 100);//(f>0) if paisa amount

		long n = number.longValue();
		if(n  > 10000000 )
		{
			s = convert1WithSpace(n/10000000) + myResources.getObject("CMN.MAJOR_NAMES_WITH_SPACE[3]"); 
			if (n%100000000 != 0)
				s+=convert1WithSpace(n%10000000);  
		}else
			s = convert1WithSpace(n);

		if(f > 0)
		{
			String s1 = convert1WithSpace(f);
			s = s + myResources.getObject("CMN.AND_WITH_SPACE") + s1 + myResources.getObject("CMN.PAISA_WITH_SPACE");
		}

		return s ;
	}
	public static String convert1WithSpace(long number) {
		/* special case */
		//if (number == 0) { return myResources.getObject("CMN.ZERO_WITH_SPACE").toString(); }
		if (number == 0) { return ""; }// done because its not working properly with 10000000 amount

		String prefix = "";

		if (number < 0) {
			number = -number;
			prefix = myResources.getObject("CMN.NEGATIVE_WITH_SPACE").toString();
		}

		String soFar = "";
		int place = 0;

		int n = (int)number % 1000;
		if (n != 0)
		{
			String s = convertLessThanOneThousandWithSpace(n);
			soFar = s + myResources.getObject("CMN.MAJOR_NAMES_WITH_SPACE["+place+"]") + soFar;
		}
		place++;
		number /= 1000;

		while (number > 0)
		{
			n = (int)number % 100;
			if (n != 0)
			{
				String s = convertLessThanOneThousandWithSpace(n);
				soFar = s + myResources.getObject("CMN.MAJOR_NAMES_WITH_SPACE["+place+"]") + soFar;
			}
			place++;
			number /= 100;
		} 

		return (prefix + soFar);
	}

	private static String convertLessThanOneThousandWithSpace(int number) {
		String soFar;

		if (number % 100 < 20){
			soFar = myResources.getObject("CMN.NUM_NAMES_WITH_SPACE["+ number % 100 +"]").toString();
			number /= 100;
		}
		else {
			soFar = myResources.getObject("CMN.NUM_NAMES_WITH_SPACE["+ number % 10 +"]").toString();
			number /= 10;

			soFar = myResources.getObject("CMN.TENS_NAMES_WITH_SPACE["+ number % 10 +"]") + soFar;
			number /= 10;
		}
		if (number == 0) return soFar;
		return ( myResources.getObject("CMN.NUM_NAMES_WITH_SPACE["+ number +"]").toString() + myResources.getObject("CMN.HUNDRED_WITH_SPACE") + soFar);
	}
	
	public static void main(String args[]){
		Locale local_locale=new Locale("en_US");
		System.out.println(convertWithSpace(new BigDecimal(100.21), local_locale));
	}



}

