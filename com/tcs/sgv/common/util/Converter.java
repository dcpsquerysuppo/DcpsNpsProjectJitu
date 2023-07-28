package com.tcs.sgv.common.util;

import java.math.BigDecimal;
import java.util.Locale;

public class Converter 
{

  private static final String[] majorNames = {
    "",
    "Thousand",
    "Lac",
    "Crore",
    "Trillion",
    "Quadrillion",
    "Quintillion"
    };

  private static final String[] tensNames = {
    "",
    "Ten",
    "Twenty",
    "Thirty",
    "Fourty",
    "Fifty",
    "Sixty",
    "Seventy",
    "Eighty",
    "Ninety"
    };

  private static final String[] numNames = {
    "",
    "One",
    "Two",
    "Three",
    "Four",
    "Five",
    "Six",
    "Seven",
    "Eight",
    "Nine",
    "Ten",
    "Eleven",
    "Twelve",
    "Thirteen",
    "Fourteen",
    "Fifteen",
    "Sixteen",
    "Seventeen",
    "Eighteen",
    "Nineteen"
    };
  
  private static final String[] majorNamesWithSpace = {
	    " ",
	    " Thousand",
	    " Lac",
	    " Crore",
	    " Hundred Crore",
	    " Ten Thousand Crore",
	    " Quintillion"
	    };

	  private static final String[] tensNamesWithSpace = {
	    " ",
	    " Ten",
	    " Twenty",
	    " Thirty",
	    " Fourty",
	    " Fifty",
	    " Sixty",
	    " Seventy",
	    " Eighty",
	    " Ninety"
	    };

	  private static final String[] numNamesWithSpace = {
	    " ",
	    " One",
	    " Two",
	    " Three",
	    " Four",
	    " Five",
	    " Six",
	    " Seven",
	    " Eight",
	    " Nine",
	    " Ten",
	    " Eleven",
	    " Twelve",
	    " Thirteen",
	    " Fourteen",
	    " Fifteen",
	    " Sixteen",
	    " Seventeen",
	    " Eighteen",
	    " Nineteen"
	    };

 private static String convertLessThanOneThousand(long number) {
    String soFar;

    if (number % 100 < 20){
        soFar = numNames[(int)number % 100];
        number /= 100;
       }
    else {
        soFar = numNames[(int)number % 10];
        number /= 10;

        soFar = tensNames[(int)number % 10] + soFar;
        number /= 10;
       }
    if (number == 0) return soFar;
    return numNames[(int)number] + "Hundred" + soFar;
}
 public static String convert(long number) {
	 String s ="";
	
	 if(number  > 10000000 )
	 {
		s = convert1(number/10000000) + " Crore"; 
		if (number%100000000 != 0)
			s+=convert1(number%10000000);  
	 }else
		s = convert1(number); 
	 return s;
 }
 public static String convert(BigDecimal number) 
 {
	 String s ="";
	 number = number.setScale(2,BigDecimal.ROUND_HALF_UP);
	 long f = (long)((number.doubleValue()- number.longValue()) * 100);
	
	 long n = number.longValue();
	 if(n  > 10000000 )
	 {
		s = convert1(n/10000000) + " Crore"; 
		if (n%100000000 != 0)
			s+=convert1(n%10000000);  
	 }else
		s = convert1(n);
	 
	 if(f > 0)
	 {
		 String s1 = convert(f);
		 s = s + " " + s1 + " paisa";
	 }
	 
	 return s ;
 }
public static String convert1(long number) {
    /* special case */
    //if (number == 0) { return " Zero "; }
	if (number == 0) { return ""; } // done because its not working properly with 10000000 amount

    String prefix = "";

    if (number < 0) {
        number = -number;
        prefix = "negative";
      }

    String soFar = "";
    long place = 0;
    
    long n = (long)number % 1000;
    if (n != 0)
    {
       String s = convertLessThanOneThousand(n);
       soFar = s + majorNames[(int)place] + soFar;
     }
    place++;
    number /= 1000;
    
    while (number > 0)
    {
    	n = (long)number % 100;
      if (n != 0)
      {
         String s = convertLessThanOneThousand(n);
         soFar = s + majorNames[(int)place] + soFar;
      }
      place++;
      number /= 100;
     } 

    return (prefix + soFar).trim();
  }
	


    public static void main(String a[])
	{
		// 	System.out.println(convertWithSpace(new BigDecimal(800000000000000000l)  ));
	}
    
    public static String convertWithSpace(BigDecimal number) 
    {
   	 String s ="";
   	 number = number.setScale(2,BigDecimal.ROUND_HALF_UP);
   	 long f = (long)((number.doubleValue()- number.longValue()) * 100);
   	
   	 long n = number.longValue();
   	 if(n  > 10000000 )
   	 {
   		s = convert1WithSpace(n/10000000) + " Crore "; 
   		if (n%100000000 != 0)
   			s+=convert1WithSpace(n%10000000);  
   	 }else
   		s = convert1WithSpace(n);
   	 
   	 if(f > 0)
   	 {
   		 String s1 = convert1WithSpace(f);
   		 s = s + " And " + s1 + " Paisa";
   	 }
   	 
   	 return s ;
    }
    
    public static String convert1WithSpace(long number) {
        /* special case */
        //if (number == 0) { return " zero "; }
    	if (number == 0) { return ""; } // done because its not working properly with 10000000 amount

        String prefix = "";

        if (number < 0) {
            number = -number;
            prefix = "negative";
          }

        String soFar = "";
        long place = 0;
        
        long n = (long)number % 1000;
        if (n != 0)
        {
           String s = convertLessThanOneThousandWithSpace(n);
           soFar = s + majorNamesWithSpace[(int)place] + soFar;
         }
        place++;
        number /= 1000;
        
        while (number > 0)
        {
        	n = (long)number % 100;
          if (n != 0)
          {
             String s = convertLessThanOneThousandWithSpace(n);
             soFar = s + majorNamesWithSpace[(int)place] + soFar;
          }
          place++;
          number /= 100;
         } 

        return (prefix + soFar).trim();
      }
    
    private static String convertLessThanOneThousandWithSpace(long number) {
        String soFar;

        if (number % 100 < 20){
            soFar = numNamesWithSpace[(int)number % 100];
            number /= 100;
           }
        else {
            soFar = numNamesWithSpace[(int)number % 10];
            number /= 10;

            soFar = tensNamesWithSpace[(int)number % 10] + soFar;
            number /= 10;
           }
        if (number == 0) return soFar;
        return numNamesWithSpace[(int)number] + " Hundred" + soFar;
    }
    public static String convert(long number,Locale locale) 
	{
		return ConvertAmountInWords.convert(number,locale);
	}
    public static String convert(BigDecimal number,Locale locale)
    {
    	return ConvertAmountInWords.convert(number,locale);
    }
    public static String convertWithSpace(BigDecimal number, Locale locale)  
	{
    	return ConvertAmountInWords.convertWithSpace(number,locale);
	}
    
    
}

