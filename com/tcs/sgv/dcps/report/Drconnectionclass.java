package com.tcs.sgv.dcps.report;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;	
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.loader.custom.Return;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.dao.AddNewAdminFieldDeptDAO;


	public class Drconnectionclass extends GenericDaoHibernateImpl 
	{
		
		 static Session ghibSession = null; 
		public Drconnectionclass(Class type,SessionFactory sessionFactory) {
			super(type);
			// TODO Auto-generated constructor stub
			ghibSession = sessionFactory.getCurrentSession();
			setSessionFactory(sessionFactory);
		}

		
		
		public static Connection getConnection(String USER ,String PASS) throws Exception  {
			
			 System.out.println("passward"+PASS+"USER"+USER);
			 
			final String URL = "jdbc:db2://10.34.82.226:60000/IFMSMIGR"; //"jdbc:db2://100.70.201.168:50000/IFMSMIGR" local
	              try {
		        	  Class.forName("com.ibm.db2.jcc.DB2Driver");  
                     // Connection con=DriverManager.getConnection("jdbc:db2://10.34.92.17:50015/IFMSMIGR","db2mahait","Sevaarth@99");   // For live     
		              return DriverManager.getConnection(URL, USER, PASS);
		          } catch (SQLException ex) {
		              throw new RuntimeException("Error connecting to the database", ex);
		          }
		        }
		
		
	}	
	
	/*<connection-url>jdbc:db2://10.34.82.235:50015/IFMSMIGR</connection-url>
        <driver-class>com.ibm.db2.jcc.DB2Driver</driver-class>
*/
		
	


		
	





//$t 2019 old 
//package com.tcs.sgv.dcps.report;
//
//import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//
//public class Drconnectionclass
//  extends GenericDaoHibernateImpl
//{
//  static Session ghibSession = null;
//  
//  public Drconnectionclass(Class type, SessionFactory sessionFactory)
//  {
//    super(type);
//    
//    ghibSession = sessionFactory.getCurrentSession();
//    setSessionFactory(sessionFactory);
//  }
//  
//  public static Connection getConnection(String USER, String PASS)
//    throws Exception
//  {
//    String URL = "jdbc:db2://10.34.92.17:50015/IFMSMIGR";
//    try
//    {
//      Class.forName("com.ibm.db2.jcc.DB2Driver");
//      
//      return DriverManager.getConnection("jdbc:db2://10.34.92.17:50015/IFMSMIGR", USER, PASS);
//    }
//    catch (SQLException ex)
//    {
//      throw new RuntimeException("Error connecting to the database", ex);
//    }
//  }
//}
