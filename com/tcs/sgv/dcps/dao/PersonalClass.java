package com.tcs.sgv.dcps.dao;

public class PersonalClass {
	/*
	 333
	 313
	 323
	 333
	 */
	public void getOutput(){
		
		int n = 3;
		int m=1;
		for(int i=0;i<=n+1;i++){
			
			for(int j=1;j<=n;j++){
				if((i>=2&&i<=n)&&j ==(n+1)/2){
					System.out.print(m);
					m++;
				}else{
					System.out.print(n);
				}
				System.out.println("\n");
			}

		}
		
		
	}
	
	
	
	
	
	
	
	
	public static void main(String args[]){

		int n = 3;
		int m=1;
		for(int i=0;i<=n+1;i++){
			
			for(int j=1;j<=n;j++){
				if((i>=2&&i<=n)&&j ==(n+1)/2){
					System.out.print(m);
					m++;
				}else{
					System.out.print(n);
				}
				System.out.println();
			}
		}

	
		
	}
}




