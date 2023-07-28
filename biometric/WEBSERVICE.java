package biometric;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.print.attribute.standard.Media;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.DatatypeConverter;
@Path("/hi")
public class WEBSERVICE{
@GET
@Path("/web")
@Produces(MediaType.TEXT_PLAIN)
public String sayHi(@QueryParam("sevarthid") String sevarthid) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, Exception
{
		
	String Status= sevarthid+"|FAIL";
	String encText="";
	if(sevarthid!=null)
	{
		System.out.println("Id-->"+sevarthid);
		 Class.forName("com.ibm.db2.jcc.DB2Driver");  
//   	  Connection con=DriverManager.getConnection("jdbc:db2://100.70.201.168:50000/IFMSMIGR","ifms","Mahasevarth@99");;
   	Connection con=DriverManager.getConnection("jdbc:db2://100.70.201.168:50000/IFMSMIGR","ifms","Seva@345"); 
//		  Connection con=DriverManager.getConnection("jdbc:db2://10.34.82.226:60000/IFMSMIGR","db2mahait",	"Mahasevaarth@123");   // For live

	try {
		String EmployeeName="FAIL";
		Statement stmt=con.createStatement(); 
	   	  ResultSet rs = null;
	   	//select EMp_name from MST_DCPS_EMP where SEVARTH_ID ='DGPSDGM6211'
	  // 	http://localhost:9999/Pension/rs/hi/web?sevarthid=DGPSDGM6211
	   	  
	   	  //http://100.70.201.169:8080/ifmsmaha/rs/hi/web?sevarthid=DGPSDGM6211
	   		  rs=stmt.executeQuery(" select EMp_name from MST_DCPS_EMP where SEVARTH_ID ='"+sevarthid+"'");
	   		 while(rs.next())
 	  		{
	   			EmployeeName =rs.getString(1);
 	  		}
	   		if(!EmployeeName.equalsIgnoreCase("FAIL"))
	   		{
		 		 System.out.println("--EmployeeName-->"+EmployeeName);

	   		 Status=sevarthid+"|"+EmployeeName.toUpperCase().replaceAll("[^a-zA-Z0-9 ]", "");
	 		 encText = encryptText(Status, producePublic());
	 		 System.out.println("---->"+encText);

	   		}
	   		 else
	   		 {
		   		 Status=sevarthid+"|FAIL";
		   		 encText = encryptText(Status, producePublic());

	   		 }

	
	} catch (SQLException e) {	
		// TODO Auto-generated catch block
		e.printStackTrace();
		con.close();
	}  //for local
	  finally{
	      	con.close();
	      }
	
	
		
		
	}
	else
	{
		Status= "Fail";
	}
	return encText;



}

@GET
@Path("/web1")
@Produces(MediaType.TEXT_PLAIN)
public String sayHi1(@QueryParam("sevarthid") String sevarthid) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, Exception
{
		
	String Status= sevarthid+"|FAIL";
	String encText="";
	if(sevarthid!=null)
	{
		System.out.println("Id-->"+sevarthid);
		 Class.forName("com.ibm.db2.jcc.DB2Driver");  
//   	  Connection con=DriverManager.getConnection("jdbc:db2://100.70.201.168:50000/IFMSMIGR","ifms","Mahasevarth@99");;
   	Connection con=DriverManager.getConnection("jdbc:db2://100.70.201.168:50000/IFMSMIGR","ifms","Seva@345"); 
//	  Connection con=DriverManager.getConnection("jdbc:db2://10.34.82.226:60000/IFMSMIGR","db2mahait",	"Mahasevaarth@123");   // For live

	try {
		String EmployeeName="FAIL";
		Statement stmt=con.createStatement(); 
	   	  ResultSet rs = null;
	   	//select EMp_name from MST_DCPS_EMP where SEVARTH_ID ='DGPSDGM6211'
	  // 	http://localhost:9999/Pension/rs/hi/web?sevarthid=DGPSDGM6211
	   	  
	   	  //http://100.70.201.169:8080/ifmsmaha/rs/hi/web?sevarthid=DGPSDGM6211
	   		  rs=stmt.executeQuery(" select EMp_name from MST_DCPS_EMP where SEVARTH_ID ='"+sevarthid+"'");
	   		 while(rs.next())
 	  		{
	   			EmployeeName =rs.getString(1);
 	  		}
	   		if(!EmployeeName.equalsIgnoreCase("FAIL"))
	   		{
		 		 System.out.println("--EmployeeName-->"+EmployeeName);

	   		 Status=sevarthid+"|"+EmployeeName.toUpperCase().replaceAll("[^a-zA-Z0-9 ]", "");
	 		 encText = Status;
	 		 System.out.println("---->"+encText);

	   		}
	   		 else
	   		 {
		   		 Status=sevarthid+"|FAIL";
		   		 encText = encryptText(Status, producePublic());

	   		 }

	   		con.close();	  
	
	} catch (SQLException e) {	
		// TODO Auto-generated catch block
		e.printStackTrace();
		con.close();
	}  //for local
	  finally{
	      	con.close();
	      }
	
	
		
		
	}
	else
	{
		Status= "Fail";
	}
	return encText;



}
public String encryptText(String msg, PublicKey key) throws NoSuchAlgorithmException, NoSuchPaddingException,
UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
Cipher  cipher = Cipher.getInstance("RSA");
cipher.init(Cipher.ENCRYPT_MODE, key);

return DatatypeConverter.printBase64Binary((cipher.doFinal(msg.getBytes("UTF-8"))));
}

private static PublicKey producePublic() throws Exception {
	byte[] certData = getFileBytes();
	CertificateFactory cf = CertificateFactory.getInstance("X509");
	X509Certificate cert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(certData));
	PublicKey key = cert.getPublicKey();
	return key;
}
private static byte[] getFileBytes() throws Exception {
//	ResourceBundle bundleApplicationDB = ResourceBundle.getBundle("ApplicationDB");
//	InputStream fileinputstream =  new FileInputStream("C:/Users/g.dhyanesh/Downloads/NEWCMPcer.txt"); 
//	 String pathFile = getServletContext().getRealPath("/UserManual/payroll/PublicKey");
//	  File file = new File(pathFile);
	//you can specify this above method as annotation
	//for Local Path
//	 URL u = new URL("http://100.70.201.169:8080/ifmsmaha/UserManual/pension/PubliKeyGenerator.txt");
	 //For Live Path
	 URL u = new URL("http://10.34.82.167:8580/IFMS-GPFLNA/UserManual/pension/PubliKeyGenerator.txt");
//	 10.34.82.167:8580/IFMS-GPFLNA/login.jsp#
	
	
	InputStream fileinputstream = u.openStream(); 
	byte[] abyte = new byte[fileinputstream.available()];
	fileinputstream.read(abyte);
	fileinputstream.close();
	return abyte;
}

}
