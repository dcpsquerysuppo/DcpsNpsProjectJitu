package biometric;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Image {

	public static void main(String[] args) {
		System.out.println("hi");
		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver");
			Connection con = DriverManager.getConnection("jdbc:db2://100.70.201.168:50000/IFMSMIGR","ifms","Maha#999");
			PreparedStatement ps = con.prepareStatement("select doc.final_attachment,SEVARTH_ID from CMN_ATTACHMENT_MPG mpg  join MST_DCPS_EMP emp on emp.SEVARTH_ID in ('MLJNPMF9301') and emp.PHOTO_ATTACHMENTID=mpg.ATTACHMENT_ID "+ " join CMN_ATTDOC_MST doc on doc.SR_NO=mpg.SR_NO");
			ResultSet rs = ps.executeQuery();
			String createFolder = "1010113";
			String photo = "photo";
			String path = "E:\\" + createFolder;
			System.out.println("path " + path);
			if (rs.next()) {// now on 1st row
				File f = new File(path);
				System.out.println("f: " + f);
				if (!f.exists())
					f.mkdirs();
				String path1 = path + "\\" + photo;
				System.out.println("path1: " + path1);
				File f1 = new File(path1);
				System.out.println("f1: " + f1);
				if (!f1.exists())
					f1.mkdirs();
				Blob b = rs.getBlob(1);// 1 means 1nd column
				byte barr[] = b.getBytes(1, (int) b.length());// 1
				FileOutputStream fout = new FileOutputStream(path1 + "\\"+ photo + ".jpg");
				fout.write(barr);
				fout.close();
			}
			PreparedStatement ps1 =con.prepareStatement("select doc.final_attachment,SEVARTH_ID from CMN_ATTACHMENT_MPG mpg   join MST_DCPS_EMP emp on emp.SEVARTH_ID in ('MLJNPMF9301') and emp.SIGNATURE_ATTACHMENTID=mpg.ATTACHMENT_ID  "+
			 " join CMN_ATTDOC_MST doc on doc.SR_NO=mpg.SR_NO");
			 ResultSet rs1 = ps1.executeQuery();
			 String Signature = "Signature";
			 String path2 = path + "\\" + Signature;
			 System.out.println("path2: "+path2);
			 File f2 = new File(path2);
			 System.out.println("f2: "+f2);
			 if (rs1.next()){
			 if (!f2.exists()) 
			 f2.mkdirs();
			 Blob b = rs1.getBlob(1);// 1 means 1nd column
			 byte barr[] = b.getBytes(1, (int) b.length());// 1
			 FileOutputStream fout = new FileOutputStream(path2 + "\\" +Signature + ".jpg");
			 fout.write(barr);
			 fout.close();
			 }
			System.out.println("ok");
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
