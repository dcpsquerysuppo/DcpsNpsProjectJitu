
package com.tcs.sgv.common.dao;
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		org.jasypt.encryption.pbe.StandardPBEStringEncryptor s = new org.jasypt.encryption.pbe.StandardPBEStringEncryptor();
		s.setPassword("password");
		System.out.println("========="+s.encrypt("adminifms"));
		
		System.out.println(s.decrypt("Xs7xV2vaWQMePSZ23dDKgleLr/1a8UGs"));
		System.out.println(s.decrypt("75W/ae254GkXLIr/NkNrRuHVrkR4n54C"));
		
		System.out.print(String.format("%050d", 21));
	}

}
