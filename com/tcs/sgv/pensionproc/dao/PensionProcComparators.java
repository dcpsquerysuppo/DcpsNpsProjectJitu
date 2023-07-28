package com.tcs.sgv.pensionproc.dao;

import java.util.Comparator;

import java.util.Date;

import org.apache.log4j.Logger;
public class PensionProcComparators 
{
	
	public static class ObjectArrayComparator implements Comparator
	{
		boolean secondLevel=false;
		private static final Logger gLogger = Logger
		.getLogger(ObjectArrayComparator.class);
		int index1=0; // index of 1st column
		int index2=0; // index of 2nd column
		int type1=2; //type of 1st column 0-Date 1-Long else-String
		int type2=2; //type of 2nd column 0-Date 1-Long else-String
		boolean asc=true; //true for asc and false for desc
		
		public ObjectArrayComparator(boolean secondLevel,int index1,int index2,int type1,int type2,boolean asc)
		{
			this.secondLevel=secondLevel;
			this.index1=index1;
			this.index2=index2;
			this.type1=type1;
			this.type2=type2;
			this.asc=asc;
		}
		public int compare(Object o1, Object o2) 
		{       
			Object[] val1 = (Object[])o1;
			Object[] val2 = (Object[])o2;
			
			try
			{
		    	if(o1!=null & o2!=null)
		    	{
		    		if(type1==0) //0-Date
		    		{
			    		if(((Date)val1[index1]).compareTo((Date)val2[index1]) > 0)	
			    		{
			    			if(asc)
			    				return 1;
			    			else
			    				return -1;
			    		}
			    		else if(((Date)val1[index1]).compareTo((Date)val2[index1]) < 0)
			    		{
			    			if(asc)
			    				return -1;
			    			else
			    				return 1;
			    		}
			    		else
			    		{
			    			if(secondLevel)
			    			{
			    				if(type2==0)// 0 - Date
			    				{
			    					if(((Date)val1[index2]).compareTo((Date)val2[index2]) > 0)	
			    		    		{
			    						if(asc)
			    		    				return 1;
			    		    			else
			    		    				return -1;
			    		    		}
			    		    		else if(((Date)val1[index2]).compareTo((Date)val2[index2]) < 0)
			    		    		{
			    		    			if(asc)
			    		    				return -1;
			    		    			else
			    		    				return 1;
			    		    		}
					    			else
					    			{
					    				return 0;
					    			}
			    				}
			    				else if(type2==1) // 1 - Long
			    				{
			    					if(Long.valueOf(val1[index2].toString()).compareTo(Long.valueOf(val2[index2].toString())) > 0)
					    			{
			    						if(asc)
			    		    				return 1;
			    		    			else
			    		    				return -1;
					    			}
					    			else if(Long.valueOf(val1[index2].toString()).compareTo(Long.valueOf(val2[index2].toString())) < 0)
					    			{
					    				if(asc)
						    				return -1;
						    			else
						    				return 1;
					    			}
					    			else
					    			{
					    				return 0;
					    			}
			    				}
			    				else //  String
			    				{
			    					if(((String)val1[index2]).compareTo((String)val2[index2]) > 0)	
			    		    		{
			    						if(asc)
			    		    				return 1;
			    		    			else
			    		    				return -1;
			    		    		}
			    		    		else if(((String)val1[index2]).compareTo((String)val2[index2]) < 0)
			    		    		{
			    		    			if(asc)
			    		    				return -1;
			    		    			else
			    		    				return 1;
			    		    		}
					    			else
					    			{
					    				return 0;
					    			}
			    				}
			    			}
			    			else
			    			{
			    				return 0;
			    			}
			    		}
		    		}
		    		
		    		else if(type1==1) //1 - Long
		    		{
			    		if(Long.valueOf(val1[index1].toString()).compareTo(Long.valueOf(val2[index1].toString())) > 0)	
			    		{
			    			if(asc)
			    				return 1;
			    			else
			    				return -1;
			    		}
			    		else if(Long.valueOf(val1[index1].toString()).compareTo(Long.valueOf(val2[index1].toString())) < 0)
			    		{
			    			if(asc)
			    				return -1;
			    			else
			    				return 1;
			    		}
			    		else
			    		{
			    			if(secondLevel)
			    			{
			    				if(type2==0)// 0 - Date
			    				{
			    					if(((Date)val1[index2]).compareTo((Date)val2[index2]) > 0)	
			    		    		{
			    						if(asc)
			    		    				return 1;
			    		    			else
			    		    				return -1;
			    		    		}
			    		    		else if(((Date)val1[index2]).compareTo((Date)val2[index2]) < 0)
			    		    		{
			    		    			if(asc)
			    		    				return -1;
			    		    			else
			    		    				return 1;
			    		    		}
					    			else
					    			{
					    				return 0;
					    			}
			    				}
			    				else if(type2==1) // 1 - Long
			    				{
			    					if(Long.valueOf(val1[index2].toString()).compareTo(Long.valueOf(val2[index2].toString()))>0)
					    			{
			    						if(asc)
			    		    				return 1;
			    		    			else
			    		    				return -1;
					    			}
					    			else if(Long.valueOf(val1[index2].toString()).compareTo(Long.valueOf(val2[index2].toString()))<0)
					    			{
					    				if(asc)
						    				return -1;
						    			else
						    				return 1;
					    			}
					    			else
					    			{
					    				return 0;
					    			}
			    				}
			    				else //  String
			    				{
			    					if(((String)val1[index2]).compareTo((String)val2[index2]) > 0)	
			    		    		{
			    						if(asc)
			    		    				return 1;
			    		    			else
			    		    				return -1;
			    		    		}
			    		    		else if(((String)val1[index2]).compareTo((String)val2[index2]) < 0)
			    		    		{
			    		    			if(asc)
			    		    				return -1;
			    		    			else
			    		    				return 1;
			    		    		}
					    			else
					    			{
					    				return 0;
					    			}
			    				}
			    			}
			    			else
			    			{
			    				return 0;
			    			}
			    		}
		    		}
		    		
		    		else //String
		    		{
			    		if(((String)val1[index1]).compareTo((String)val2[index1]) > 0)	
			    		{
			    			if(asc)
			    				return 1;
			    			else
			    				return -1;
			    		}
			    		else if(((String)val1[index1]).compareTo((String)val2[index1]) < 0)
			    		{
			    			if(asc)
			    				return -1;
			    			else
			    				return 1;
			    		}
			    		else
			    		{
			    			if(secondLevel)
			    			{
			    				if(type2==0)// 0 - Date
			    				{
			    					if(((Date)val1[index2]).compareTo((Date)val2[index2]) > 0)	
			    		    		{
			    						if(asc)
			    		    				return 1;
			    		    			else
			    		    				return -1;
			    		    		}
			    		    		else if(((Date)val1[index2]).compareTo((Date)val2[index2]) < 0)
			    		    		{
			    		    			if(asc)
			    		    				return -1;
			    		    			else
			    		    				return 1;
			    		    		}
					    			else
					    			{
					    				return 0;
					    			}
			    				}
			    				else if(type2==1) // 1 - Long
			    				{
			    					if(Long.valueOf(val1[index2].toString()).compareTo(Long.valueOf(val2[index2].toString()))>0)
					    			{
			    						if(asc)
			    		    				return 1;
			    		    			else
			    		    				return -1;
					    			}
					    			else if(Long.valueOf(val1[index2].toString()).compareTo(Long.valueOf(val2[index2].toString()))<0)
					    			{
					    				if(asc)
						    				return -1;
						    			else
						    				return 1;
					    			}
					    			else
					    			{
					    				return 0;
					    			}
			    				}
			    				else //  String
			    				{
			    					if(((String)val1[index2]).compareTo((String)val2[index2]) > 0)	
			    		    		{
			    						if(asc)
			    		    				return 1;
			    		    			else
			    		    				return -1;
			    		    		}
			    		    		else if(((String)val1[index2]).compareTo((String)val2[index2]) < 0)
			    		    		{
			    		    			if(asc)
			    		    				return -1;
			    		    			else
			    		    				return 1;
			    		    		}
					    			else
					    			{
					    				return 0;
					    			}
			    				}
			    			}
			    			else
			    			{
			    				return 0;
			    			}
			    		}
		    		}
		    	}
		    	else
		    		return 0;
			}
			catch(Exception e)
			{
				gLogger.error("Error in ObjectArrayComparator compare : "+e,e);
				if(asc)
					return -1;
				else
					return 1;
			}
		}
	}
	
}
