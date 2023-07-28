package com.tcs.sgv.dcps.service;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.*;
import java.util.*;

import javax.imageio.ImageIO;

import com.ibm.wsdl.util.StringUtils;


public class demo {

	

	
	        public static void main (String [] args) throws IOException
	        {	
	        	File imagefile = new File("E:/st/.metadata/.plugins/org.eclipse.wst.server.core/tmp4/wtpwebapps/dcps/32021/71012021310084/71012021310084_photo/2.jpg");
	        	
	        	
	        	System.out.println("image-->"+ImageIO.read(imagefile));

	        	 BufferedImage   image1 = ImageIO.read(imagefile);
		        	System.out.println("image-->"+image1);


	        	
	        	
	        	//FileInputStream input= new FileInputStream ("E:/st/.metadata/.plugins/org.eclipse.wst.server.core/tmp4/wtpwebapps/dcps/32021/71012021310084/71012021310084_photo/2.jpg");
	        	FileInputStream input= new FileInputStream ("E:/st/.metadata/.plugins/org.eclipse.wst.server.core/tmp4/wtpwebapps/dcps/32021/71012021310084/71012021310084_photo/1.jpg");
	        	System.out.println("input-->"+input);
		        BufferedImage image = ImageIO.read(input);
		        BufferedImage resized = resize(image, 150, 150);
	        	System.out.println("image-->"+image);
	        	
	        	File output = new File("C:/Users/c.akhilesh/Desktop/photo/1.jpg");
		        ImageIO.write(resized, "jpg", output);
	        }

	        private static BufferedImage resize(BufferedImage img, int height, int width) {
	            Image tmp = img.getScaledInstance(width, height, Image.SCALE_FAST);
	            BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);   // TYPE_USHORT_555_RGB   // for no color TYPE_INT_ARGB   // working fine TYPE_USHORT_565_RGB
	            Graphics2D g2d = resized.createGraphics();
	            g2d.drawImage(tmp, 0, 0, null);
	            g2d.dispose();
	            
	            return resized;
	        }
	        
	        
	        
	        
	        
	        
	        
	        
	        
	        
	        public static String caesarEncipher (String input, int shift, String out) throws FileNotFoundException
	        {
	                File newfile = new File(out);
	                PrintStream encoded = new PrintStream(newfile);  // creates new file
	                File reader = new File (input);  // creates file to scan
	                Scanner in = new Scanner(reader); // creates scanner
	                while (in.hasNextLine())  // runs while the file has another line
	                {
	                        String word = in.nextLine();            // gets next line of file
	                        String cipher = word.toUpperCase();     // makes all letters uppercase
	                        String encipher = "";                   // String to be written into new file
	                        int loop;
	                        for (loop = 0; loop < cipher.length(); loop++) // loops through the line to check each character
	                        {
	                                char curr = cipher.charAt(loop);  // current character
	                                char newChar = crypt(curr, shift);  // runs through crypt method (Below)
	                                encipher = encipher + newChar; // adds the new character to outgoing string
	                        }
	                        encoded.println(encipher);
	                }
	                encoded.close();
	                return "DONE";
	        }

	        public static String caesarDecipher (String input, int shift, String out) throws FileNotFoundException
	        {
	                PrintStream decoded = new PrintStream(out);
	                File read = new File (input);
	                Scanner in = new Scanner(read);
	                while (in.hasNextLine())
	                {
	                        String word = in.nextLine();
	                        word = word.toUpperCase();                                     
	                        String cipher = word;
	                        String decipher = "";
	                        int loop;
	                        for (loop = 0; loop < cipher.length(); loop++)
	                        {
	                                char curr = cipher.charAt(loop);
	                                char newChar = decrypt(curr, shift);
	                                decipher = decipher + newChar;
	                        }
	                        decoded.println(decipher);
	                }
	                decoded.close();
	                return "DONE";
	        }

	        public static char crypt(char c, int shift)
	        {
	                char encrypted = c;  // character passed in from for loop
	                int alter = shift; // shift passed in
	                if (Character.isLetter(encrypted)) // checks for only letters
	                {
	                        int enc;
	                        if ((int) encrypted + alter > 91) // if ASCII value after altering is beyond 'Z' value, run this to loop back to 'A'
	                        {
	                                enc = encrypted - 65;
	                                enc += alter;
	                                enc = enc % 26;
	                                enc += 65;
	                                encrypted = (char) enc;
	                        }
	                        else enc =  encrypted + alter;
	                        encrypted = (char) enc;
	                }
	                return encrypted;
	        }

	        public static char decrypt(char c, int shift)
	        {
	                char decrypted = c;
	                int alter = shift;
	                if (Character.isLetter(decrypted))
	                {
	                        int dec;
	                        if ((int) decrypted - alter < 65)  // if ASCII value after altering is below 'A' value, run this to correct overflow
	                        {
	                                dec = decrypted + 65;
	                                dec -= alter;
	                                dec = dec % 26;
	                                dec -= 65;
	                                decrypted = (char) dec;
	                        }
	                        else dec = decrypted - alter;
	                        decrypted = (char) dec;
	                }
	                return decrypted;
	        }
	}



/*
	    private static Charset UTF8 = Charset.forName("UTF-8");

	    public static void main(String args[]) throws IOException {
	        writeFile("./test.txt");
	        readFile("./test.txt");
	    }

	    public static void writeFile(String path) throws IOException {
	        Writer writer = new OutputStreamWriter(new FileOutputStream(path), UTF8);
	        try {
	            writer.write((char) 1474849);
	        } finally {
	            writer.close();
	        }
	    }

	    public static void readFile(String path) throws IOException {
	        Reader reader = new InputStreamReader(new FileInputStream(path), UTF8);
	        try {
	            int c = reader.read();
	            System.out.println(c);
	        } finally {
	            reader.close();
	        }
	    }*/
	

	

