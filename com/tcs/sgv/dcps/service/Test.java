package com.tcs.sgv.dcps.service;

import java.lang.reflect.Method;  
public class Test   
{  
public static void main(String[] args)  
{  
try   
{  
	String  s="A";
	s.toUpperCase();
	Library3 lib = null ;
	lib.printVersion();
Class<?> cls = Class.forName("java.lang.String");  

Object ob =cls.getClass().getInterfaces();
//args

 


// Try to instantiate Library2 with the new classloader    
//Class<?> cls = Class.forName("Library2", true, c);
//Library3 lib2 = (Library3) cls.newInstance();

//lib2.t

// If it worked, this should print "This is version 2."
// However, it still prints that it's version 1. Why?
//lib2.bar();


System.out.println("Class Name: " + cls.getName());  
System.out.println("Package Name: " + cls.getPackage());  
Method[] methods = cls.getDeclaredMethods();  




java.lang.String a=new String();


System.out.println("-----Methods of String class -------------");  
for (Method method : methods)   
{  
System.out.println(method.getName());  
if(method.getName().equalsIgnoreCase("toLowerCase"))
{
	
}
}  
}  
catch (ClassNotFoundException e)   
{  
e.printStackTrace();  
}  
}

public class Library3 {
	  public void printVersion() {
	    System.out.println("This is version 1.");
	  }
	}
}
