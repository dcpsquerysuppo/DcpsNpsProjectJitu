package biometric;

import java.awt.*;

import oracle.sql.ARRAY;

import org.apache.naming.factory.BeanFactory;
import org.apache.poi.hwpf.usermodel.DateAndTime;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.jfree.data.time.Month;

//import org.apache.axis.types.Month;






















import com.ibm.db2.jcc.sqlj.e;

import java.sql.Timestamp;
import java.text.DateFormat;
//import org.apache.axis2.databinding.types.Month;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/*
 1.bubble sort
 2.selection sort
 3.insertion sort
 */
//Java program to sort hashmap by values
import java.util.*;
import java.lang.*;
//Java Code to sort Map by key value
import java.util.*;
//Java Code to sort Map by key value
import java.util.*;
//Java Code to sort Map by key value
import java.util.*;
//Java Code to sort Map by key value
import java.util.*;
import java.util.Map.Entry;


class Test{
/*	String name;int sal;
	public Employee(String name,int sal) {
	this.name=name;
	this.sal=sal;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSal() {
		return sal;
	}
	public void setSal(int sal) {
		this.sal = sal;
	}
	@Override
	public String toString() {
		return name+"--"+sal;
	}
}
class Test{
	public static void main(String args[]){
		Map<Employee,Integer> hm=new TreeMap<>((o1,o2)->o1.getSal()-o2.getSal());
			
		hm.put(new Employee("Jayant", 80),1);
		hm.put(new Employee("Abhishek", 90),2);
		hm.put(new Employee("Anushka", 100),3);
		hm.put(new Employee("Amit", 75),4);
		System.out.println(hm);
		System.out.println("---------------------------");
		hm.entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.comparing(Employee::getSal))).forEach(System.out::println);
		System.out.println("---------------------------");
		hm.entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.comparing(Employee::getSal).reversed())).forEach(System.out::println);
	}
}
*/
public static void main(String args[]) {
	
//	System.out.println(test("vikas"));
	char a1 = 's'+'z';
	char a2 = 'g';
	int val=a1*a2;
	
	char ch1='s';
	char ch2='g';
	char ch3='?';
	System.out.println(val);
	
	int asciivalue1=ch1;
	int asciivalue2=ch2;
	System.out.println("The ASCII value of " + a1 + " is: " + asciivalue1);  

	System.out.println("The ASCII value of " + ch2 + " is: " + asciivalue2);  
}
}
/*class Test{
	//Using ArrayList
	static HashMap<String, Integer> map = new HashMap<>();
	public static void main(String args[])
	{
		map.put("Jayant", 80);
		map.put("Abhishek", 90);
		map.put("Anushka", 80);
		map.put("Amit", 75);
		map.put("Danish", 40);
		sortbykey();
	}
	public static void sortbykey()
	{
		ArrayList<String> sortedKeys= new ArrayList<String>(map.keySet());
		Collections.sort(sortedKeys);
		// Display the TreeMap which is naturally sorted
		for (String x : sortedKeys)
			System.out.println("Key = " + x+ ", Value = " + map.get(x));
   }
}*/

/*class Test {
	//Using TreeMap (Constructor) 
	static HashMap<String, Integer> map = new HashMap<>();
	public static void main(String args[]) {
		map.put("Jayant", 80);
		map.put("Abhishek", 90);
		map.put("Anushka", 80);
		map.put("Amit", 75);
		map.put("Danish", 40);
		sortbykey();
	}
	public static void sortbykey() {
		TreeMap<String, Integer> sorted = new TreeMap<>(map);
		// Display the TreeMap which is naturally sorted
		for (Map.Entry<String, Integer> entry : sorted.entrySet())
			System.out.println("Key = " + entry.getKey() + ", Value = "+ entry.getValue());
	}

}*/

/*public class Test {
		static HashMap<String, Integer> map = new HashMap<>();
		public static void main(String args[])
		{
			map.put("Jayant", 80);
			map.put("Abhishek", 90);
			map.put("Anushka", 80);
			map.put("Amit", 75);
			map.put("Danish", 40);
			sortbykey();
		}
	// Function to sort map by Key
	public static void sortbykey()
	{
		TreeMap<String, Integer> sorted = new TreeMap<>();
		// Copy all data from hashMap into TreeMap
		sorted.putAll(map);
		for (Map.Entry<String, Integer> entry : sorted.entrySet())
			System.out.println("Key = " + entry.getKey() +", Value = " + entry.getValue());	
	}
}*/







/*class Test {
	public static void main(String[] args) throws Exception {
   ArrayList<String> l=new ArrayList<String>();
   l.add("Lusi");
   l.add("Vettel");
   l.add("ZO");
   l.add("MASKERANESE");
   System.out.println("1-->"+l);
   l.stream().forEach(System.out::println);
   
  Comparator<String> c1=(v1,v2)->-v1.compareTo(v2);
  List<String> l2=l.stream().sorted(c1).collect(Collectors.toList());
  System.out.println("12-->"+l2);
  }
}*/



/*class DAT {
    void m1()throws Exception {

    }
}

class MAHAIT extends DAT {
    @Override
    void m1() throws NullPointerException{

    }
}*/

/*abstract class School{
    int i = 0;
    public School(){
        
        System.out.println("School constructor");
        display();
    }
    
    abstract void myMethod();
    
    void display()
    {
        System.out.println("display-> "+i);
        
    }
    
}

interface Student{
    abstract void myMethod();
}

class Teacher extends School implements Student
{
    public Teacher()
    {
        super();
        i=1;
        System.out.println("Teacher constructor");
    }

    @Override
    public void myMethod() {
        // TODO Auto-generated method stub
        System.out.println("Teacher myMethod");
        new Teacher();
    }	
}
class Marks extends Teacher
{
    public Marks(){
        System.out.println("marks constructor");
        i=5;
        myMethod();
    }
}
 class Test {   
  public static void main(String args[])
  {
      Marks m = new Marks();
 }
 }*/

/*public class Test {
	static void m1(){
		System.out.println("Exception 2");
	}
	
	public static void main(String[] args){
		try {
			int a=10/args.length;
			System.out.println("Exception 1"+a);
			m1();
		} catch (ArithmeticException e) {
			e.printStackTrace();
		}
	}
}*/

/*public class Test {
	public static void main(String[] args) throws Exception {
		try {
			throwableTest();
		} catch (Exception e) {
			System.err.println("Cause : " + e.getCause());
		}
	}

	public static void throwableTest() throws Exception {
		int i;
		try {
			i = 4 / 0;
		} catch (ArithmeticException ae) {
			IOException ioe = new IOException();
			throw (IOException) 
			ioe.initCause(ae);
		}
	}
}*/

/*//parent class  
class Demo {
	public static void method1() {
		System.out.println("Method-1 of the Demo class is executed.");
	}

	public void method2() {
		System.out.println("Method-2 of the Demo class is executed.");
	}
}

// child class
public class Test extends Demo {
	public static void method1() {
		System.out.println("Method-1 of the Sample class is executed.");
	}

	public void method2() {
		System.out.println("Method-2 of the Sample class is executed.");
	}

	public static void main(String args[]) {
		Demo d1 = new Demo();
		// d2 is reference variable of class Demo that points to object of class
		// Sample
		Demo d2 = new Test();
		// method calling with reference (method hiding)
		d1.method1();
		d2.method1();
		// method calling with object (method overriding)
		d1.method2();
		d2.method2();
	}
}*/

/*class Super{
	   public static int sample(){
	      System.out.println("Method of the superclass");
	      return 10;
	   }
	}
	public class Test extends Super {
	   public static int sample(){
	      System.out.println("Method of the subclass");
	      return 10;
	   }  
	   public static void main(String args[]){
	      Super obj1 = (Super) new Test();
	      Test obj2 = new Test();
	      obj1.sample();
	      obj2.sample();
	   }
	}*/
/*public class Test {

	   public static void main(String args[]) {
		   FourWheeler FourWheeler = new Car();
		   FourWheeler.print();
	   }
	}

	interface Vehicle {

	   default void print() {
	      System.out.println("I am a vehicle!");
	   }
		
	   static void blowHorn() {
	      System.out.println("Blowing horn!!!");
	   }
	}

	interface FourWheeler {

	   default void print() {
	      System.out.println("I am a four wheeler!");
	   }
	}

	class Car implements Vehicle,FourWheeler  {

	   public void print() {
		   FourWheeler.super.print();
	     Vehicle.super.print();
	     
	      Vehicle.blowHorn();
	      System.out.println("I am a car!");
	   }
	}
*/

/*interface InterfaceX
{
    public int geek();
}
interface InterfaceY
{
    public int geek();
}


public class Test implements InterfaceX, InterfaceY
{
public int geek()
    {
        return 10;
    }
}*/

/*class I1{
void m1(){
	System.out.println("I1");
}	
}

class I2 extends I1{
   @Override
	void m1(){
		System.out.println("I2");
	}	
}

class Test {
	public static void main(String args[]) {
	I1 i1=new I1();
	I2 i2=new I2();
	}

	
}*/

/*interface I1{
void m1();	
}

interface I2{
void m1();	
}

class Test implements I1,I2{
	public static void main(String args[]) {
	Test t=new Test();
	t.m1();
	}

	@Override
	public void m1() {
		
		System.out.println("sys");
	}
}*/


/*class Test {
	
	static boolean test(String s){
		String reverse = "";
		for (int i = s.length(); i >0; i--) {
		 System.out.println(i+"-->"+s.charAt(i-1));
		 //reverse =reverse+s.charAt(i-1);
		 reverse +=s.charAt(i-1);
		}
		if (s.equals(reverse))
			return true;
		else
			return false;
	}
	public static void main(String args[]) {
		
		System.out.println(test("vikas"));
		char a = 's' * 'g'
	}
}*/

/*class Test {
	public static void main(String args[]) {
		String s;
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter a String: ");
		s = sc.nextLine(); // reading string from user
		System.out.print("After reverse string is: ");
		for (int i = s.length(); i > 0; --i) // i is the length of the string
		{
			System.out.print(s.charAt(i - 1)); // printing the character at
												// index i-1
		}
	}
}*/



/*class Test {
public static void main(String[] args) throws ParseException {

SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
Date myDefaultDate = format.parse("00/00/0000");
System.out.println(myDefaultDate);


String sDate1="00/00/0000";  
Date date1=new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);  
System.out.println(sDate1+"\t"+date1);
}
}*/



//class Test {
//public static void main(String[] args) throws ParseException {
//String [] data={"jituuuuuuuu","mitu"};
//
////anonymous class
//Arrays.sort(data,new Comparator<String>() {	
//	@Override
//	public int compare(String s1, String s2) {
//		return s1.length()-s2.length();
//	}
//});
////using lambda
//Arrays.sort(data,(s1,s2)->s1.length()-s2.length());
//
//for (String v : data) {
//	System.out.println(v);
//}
//}	
//}

/*abstract class School{
	int i=0;
	public School(){
		display();
		i=2;
	}
	abstract void myMethod();
	void display(){
		System.out.println(i);
	}
}
interface Student{
	abstract void myMethod();
}
class Teacher extends School implements Student{
	public Teacher(){
		super();
		i=1;
	}
	public void myMethod(){
	new Teacher();	
	}
}
class Marks extends Teacher{
	public Marks(){
		i=5;
		myMethod();
	}
}
class Test {
public static void main(String[] args) throws ParseException {
Marks stu =new Marks();
}	
}*/


/*class Test {

	public static void main(String[] args) throws ParseException {

		String vouchedate="15/02/2022";
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    Date parsedDate = dateFormat.parse(vouchedate);
	    Timestamp timestampvouchedate = new java.sql.Timestamp(parsedDate.getTime());
	    System.out.println("timestampvouchedate-->"+timestampvouchedate);
	}	
}*/

/*class Test{
	public static void main(String[] args){
		
		String s = "25808.0";
		double d = Double.parseDouble(s);
		int i = (int) d;
		System.out.println(i);
		
		String s = "25808.0";
		int i=(int) (Double.parseDouble(s));
		System.out.println(i);
		
		
		Double v=Double.parseDouble(new DecimalFormat("##.##").format(25808.0));
		//Printing value of i  
		System.out.println(Math.round(v));  
		int value = (int)(Math.round(v));
		System.out.println(value);
		
		int v=(int)(Math.round(Double.parseDouble(new DecimalFormat("##.##").format(23.60))));
		System.out.println(v);
	}	
}*/

/*class A{
	
}

class B extends A{
	
}

class C extends B {
	
}*/

/*class Test{
	public static void main(String[] args) throws ParseException
	{
		String strDateString="01-04-2021";
		
		Date date = Calendar.getInstance().getTime();  
		//DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String endDate = sdf.format(date);
		System.out.println("equals2-->"+endDate);
		
		 String date1 = "04/01/2021";
	        //String time1 = "11:00:01";
	        //String date2 = "10/31/2021";
	        //String time2 = "22:15:10";
	        //String format = "MM/dd/yyyy HH:mm:ss";
	        //SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy HH:mm:ss");
	        Date fromDate = sdf.parse(date1 + " " + "11:00:01");
	        Date toDate = sdf.parse(endDate + " " + "22:15:10");

	        long diff = toDate.getTime() - fromDate.getTime();
	        String dateFormat="duration: ";
	        int diffDays = (int) (diff / (24 * 60 * 60 * 1000));

	}
}*/

//Simple Java Class to represent Person with name, id and date of birth.
/*class Test implements Comparable<Test>{ 
private String name; private int id;

public Test(String name, int id) { 
this.name = name; 
this.id = id;
} 
 
@Override public boolean equals(Object other){
	
System.out.println("equals1-->"+this.getClass());
System.out.println("equals2-->"+other.getClass());
	
if(this == other) 
return true;
if(other == null || (this.getClass() != other.getClass())){
return false; 
} 
 
Test guest = (Test) other; 
return (this.id == guest.id) && (this.name != null && name.equals(guest.name)); 
} 
  
@Override public int hashCode(){
int result = 0; 
result = 31*result + id;
result = 31*result + (name !=null ? name.hashCode() : 0);
System.out.println("hashCode1-->"+name.hashCode());
System.out.println("hashCode2-->"+result);
return result; 
} 
 
@Override public int compareTo(Test o) { 
return this.id - o.id;
} 
 
public static void main(String[] args) throws ParseException
{
	 Test t = new Test("st", 1);
	 System.out.println("here1-->"+t.equals("at"));
	 System.out.println("here1-->"+t.equals("st"));
	 System.out.println("here2-->"+t.hashCode());
  }
 }
*/



/*class Test {
    public static void main(String[] args) throws ParseException
    {	
//    	String s1=new String("cat");
//    	String s2=new String("cat");
    	String s1="cat";
    	String s2="cat";
    	
    	if(s1==s2)
    		System.out.println("here1");
    	else
    		System.out.println("here2");
    	
//    	s1=new String("DOG");
//    	 //s1="dog";
//    	
//    		System.out.println("here1"+s1);
    	
    }
}*/
//class Test implements Comparable<Test>
// {
//	private String name; private int id;
//	public Test(String name, int id) { 
//		 this.name = name; 
//		 this.id = id;
//		} 
//    public static void main(String[] args) throws ParseException
//    {	
//    }
//
//	@Override
//	public int compareTo(Test o) {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//}
/*class Test {
    public static void main(String[] args) throws ParseException
    {
    	String no=Long.toString(9971018700001L);
    	
    	System.out.println(no);
    	
    	System.out.println(no.length());
    	
    	System.out.println(no.substring(no.length()-4, no.length()));
    }
}*/

/*class Test {
    public static void main(String[] args) throws ParseException
    {
    	int month=11;
    	
    String	monthString = new DateFormatSymbols().getMonths()[month-1];
  
        // Print the month Instance
        
    	
    	String  monthName="November";
    	
    	Date date = new SimpleDateFormat("MMMM").parse(monthName);
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(date);
    	System.out.println(cal.get(Calendar.MONTH)+1);
    }
}*/


/*public class Test  {
	
	public static void main(String args[]) throws ParseException
	{
		 SimpleDateFormat obj = new SimpleDateFormat("dd/MM/yyyy");   
		 String DOB ="1/1/2021";
         String TodaysDate ="31/03/2021";
		 Date date1 = obj.parse(DOB);   
	     Date date2 = obj.parse(TodaysDate); 
         long diff = date2.getTime() - date1.getTime();
         long days= TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
         System.out.println ("Days D: " + days);
		
		String A="(24/11/2021)";
		
		 
		 System.out.println ("Days D: " + A.substring(1,11).trim());

	}
	
	}
*/



/*class Account {
	public int balance;
	public int accountNo;
	void displayBalance() {
		System.out.println("Account No:" + accountNo + "Balance: " + balance);
	}

	   synchronized void deposit(int amount){
			balance = balance + amount;
			System.out.println( amount + " is deposited");
			displayBalance();
	   }

	   synchronized void withdraw(int amount){
			  balance = balance - amount;
			  System.out.println( amount + " is withdrawn");
			  displayBalance();
	   }
}

class TransactionDeposit implements Runnable{
	int amount;
	Account accountX;
	TransactionDeposit(Account x, int amount){
		accountX = x;
		this.amount = amount;
		new Thread(this).start();
	}
	
	public void run(){
		accountX.deposit(amount);
	}
}

class TransactionWithdraw implements Runnable{
	int amount;
	Account accountY;
	
	TransactionWithdraw(Account y, int amount) {
		accountY = y;
		this.amount = amount;
		new Thread(this).start();
	}
	
	public void run(){
		accountY.withdraw(amount);
	}
}

class Demonstration_119{
	public static void main(String args[]) {
		Account ABC = new Account();
		ABC.balance = 1000;
		ABC.accountNo = 111;
		TransactionDeposit t1;
		TransactionWithdraw t2;
		t1 = new TransactionDeposit(ABC, 500);
		t2 = new TransactionWithdraw(ABC,900);
	}
}
*/
//public class Test {
//	public static void main(String args[]) {
//		String s = "ABC";
//		long l = Long.parseLong(s);
//		System.out.println(l);
//		
//	}
//}


/*public class Test {
/////Pallindrome
	public static void main(String[] args) {
		int num=454;--MOM
		String original =Integer.toString(num);
		String reverse = "";
		
		int len=original.length();
		System.out.println("len-->"+len);
		for (int i = len-1; i >=0; i--) {
			System.out.println("i-->"+i);
			reverse=reverse+original.charAt(i);
			System.out.println("reverse-->"+reverse);
		}
	}
}*/

/*public class Test {

public static void main(String[] args) {
int a=100;
int b=100;

if(a>b){
	System.out.println("str-->");
}

//demo.put("123");

}
}*/

//public class Test{
//	public static void main(String args[]) throws Exception {
//		System.out.println("str-->"+Test.area(1.5f,3.6f));
//	}
//		public static float area(float length,float width){
//			return (length*width);
//		}
//}
/*public class Test{
	public static void main(String args[]) throws Exception {
		System.out.println("str-->"+Test.area(1.5f,3.6f));
	}
		public static float area(float length,float width){
			return (length*width);
		}
}
*/
/*public class Test{
	public static void main(String args[]) throws Exception {
		double conti1 = 0;
		
		List one =new ArrayList();
		one.add("10#");
		
		String str= one.get(0).toString();
		str.length();
		System.out.println("str.length()-->"+str.length());
		System.out.println("str-->"+str.charAt(0));
		
	}
}*/


/*public class Test{
	public static void main(String args[]) throws Exception {
		double conti1 = 0;
		
		int directoryYear = Calendar.getInstance().get(Calendar.YEAR);
		int directoryMonth = Calendar.getInstance().get(Calendar.MONTH)+1;
		String directoryFolder=Integer.toString(directoryMonth)+Integer.toString(directoryYear);
		System.out.println(directoryFolder);
	}
}*/

/*public class Test{
	public static void main(String args[]) throws Exception {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int month = Calendar.getInstance().get(Calendar.MONTH)+1;
		String seq;
		String treasury="11";
		DecimalFormat df1 = new DecimalFormat("00");
		System.out.println(df1.format(month));
		seq=treasury+year+df1.format(month);
		System.out.println(seq);
		
	}
}*/

/*public class Test{
	public static void main(String args[]) throws JSchException, FileNotFoundException, SftpException {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int month = Calendar.getInstance().get(Calendar.MONTH)+1;
		String SFTPHOST= "100.70.201.169";
		int SFTPPORT = 22;
		String SFTPUSER = "mahait";
		String SFTPPASS = "Mahait@9999";
		String SFTPWORKINGDIR = "/OPGM"+"/"+month+year;
		Session session = null;
		Channel channel = null;
		ChannelSftp channelSftp = null;
		JSch jsch = new JSch();
		session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
		session.setPassword(SFTPPASS);
		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		session.connect();
		channel = session.openChannel("sftp");
		channel.connect();
		channelSftp = (ChannelSftp) channel;
		
		String[] folders = SFTPWORKINGDIR.split( "/" );
		for ( String folder : folders ) {
		   if ( folder.length() > 0 ) {
		       try {
		        channelSftp.cd( folder );
		       }
		       catch ( SftpException e ) {
		        channelSftp.mkdir( folder );
		        channelSftp.cd( folder );
		       }
		   }
		}
		//channelSftp.cd(SFTPWORKINGDIR);
		String authNo1 = "Dnyanesh1";
		String fileName=authNo1+".txt";
		String content="hi this is forst time i am doing this";
		final OutputStream os = new FileOutputStream(fileName);
		final PrintStream printStream = new PrintStream(os);
		printStream.print(content);
		printStream.close();
		File f1 = new File(fileName.toString());
		channelSftp.put(new FileInputStream(f1), f1.getName());
		channelSftp.exit();
		channel.disconnect();
		session.disconnect();		
	}
}
*/


/*
 public class Test{ public static void main(String args[]) { 
	String text = "This - text ! has \\ /allot # of % special % characters"; text = text.replaceAll("[^a-zA-Z0-9]", ""); 
	System.out.println(text); 
	String html = "T,,hi--s i^s 777& b.1}}old"; 
	html = html.replaceAll("[^a-zA-Z0-9\\s+]", "");
	System.out.println(html); 
	} 
}
*/

/*class Test {
	public static void main(String[] args) throws IOException, ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateInString = "25/03/2013";
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");

        try {

            Date date = formatter.parse(dateInString);
            System.out.println(date);
            System.out.println(formatter1.format(date));

        } catch (ParseException e) {
            e.printStackTrace();
        }
		}
	}
*/



/*class Test {
	public static void main(String[] args) throws IOException {
		try {
			
		   
			
			String srno="99100001247395";
			 byte[] lBytes = (byte[])null;
			    
			    String SFTPHOST = "10.34.82.225";
			    
			    int SFTPPORT = 8888;
			    
			    String SFTPUSER = "tcsadmin";
			    
			    String SFTPPASS = "Tcsadmin@123";
			    
			    String SFTPWORKINGDIR = "/home/EmployeeConfigurationForm";
			    
			    Session session = null;
			    
			    Channel channel = null;
			    
			    ChannelSftp channelSftp = null;
			    byte[] data = (byte[])null;
			    byte[] lBytes1 = (byte[])null;
			    JSch jsch = new JSch();
			    session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
			    
			    session.setPassword(SFTPPASS);
			    
			    Properties config = new Properties();
			    
			    config.put("StrictHostKeyChecking", "no");
			    
			    session.setConfig(config);
			    
			    session.connect();
			    
			    byte[] buffer = new byte['?'];
			    
			    channel = session.openChannel("sftp");
			    channelSftp = (ChannelSftp)channel;
			    channel.connect();
			    channelSftp.cd(SFTPWORKINGDIR);
			    
			    String fileName = srno;
			    BufferedInputStream bis = new BufferedInputStream(channelSftp.get(fileName));
			    
			    File newFile = new File(fileName);
			    
//			    String filePath = request.getSession().getServletContext().getRealPath("/") + fileName;
			    String filePath =  fileName;
			    
			    String home = System.getProperty("user.home");
			    
			    channelSftp.get("/home/EmployeeConfigurationForm/" + fileName, "E:/New folder");
			    byte[] arrayOfByte = Files.readAllBytes(new File("E:/New folder/" + fileName).toPath());
			    System.out.println("arrayOfByte size is " + arrayOfByte.length);
			    
			    String directoryPathPhoto="E:/New folder";
			    String jpg = "_photo.jpg";
			    
			    FileOutputStream foutImage = new FileOutputStream(directoryPathPhoto+ "/"  + jpg);
				foutImage.write(arrayOfByte);
				foutImage.close();
						
			    
//			    IOUtils.copy(fis, response.getOutputStream());
//			    
//			    response.flushBuffer();
//			    byte[] arrayOfByte = Files.readAllBytes(new File("/tmp/" + fileName).toPath());
			
			Connection con = null;
			PreparedStatement ps=null;
			ResultSet rs=null;
			PreparedStatement ps1 =null;
			ResultSet rs1=null;
			Class.forName("com.ibm.db2.jcc.DB2Driver");
			//con = DriverManager.getConnection("jdbc:db2://100.70.201.168:50000/IFMSMIGR","ifms", "Mahasevaarth@999");
			
			con = DriverManager.getConnection("jdbc:db2://10.34.82.235:50015/IFMSMIGR","ifms", "IFMS@dat#@!");// where

			 ps = con.prepareStatement("select doc.final_attachment,SEVARTH_ID from CMN_ATTACHMENT_MPG mpg  join MST_DCPS_EMP emp on emp.SEVARTH_ID = 'MLJSVSM8401'   "
							+ " and emp.PHOTO_ATTACHMENTID=mpg.ATTACHMENT_ID  join CMN_ATTDOC_MST doc on doc.SR_NO=mpg.SR_NO ");
			 rs = ps.executeQuery();
			if (rs.next()) {
				Blob b = rs.getBlob(1);// 1 means 1nd column
				byte barr[] = b.getBytes(1, (int) b.length());// 1
				String jpg = "_photo.jpg";
				
				// to upload image on real path
				FileOutputStream foutImage = new FileOutputStream("C:/Users/c.akhilesh/Desktop/Interest Calculation" + jpg);
				foutImage.write(barr);
				foutImage.close();
			}
			File DFfile = new File("E:/st/.metadata/.plugins/org.eclipse.wst.server.core/tmp4/wtpwebapps/dcps/SAMPLE/Processed");// Processed
			
			File downloadErrFile=new File("E:/st/.metadata/.plugins/org.eclipse.wst.server.core/tmp4/wtpwebapps/dcps/SAMPLE/Error");
			//(fMainDirectory+ "/" + "Error" + "/sample_Resp.html");
			
			//File downloadErrFile = new File("E:/st/.metadata/.plugins/org.eclipse.wst.server.core/tmp4/wtpwebapps/dcps/SAMPLE/Error/sample_Resp.html");// Processed

			String ZIpPath=downloadErrFile+"/sample_Resp.html";
			
			if (DFfile.isDirectory() && DFfile.list().length == 0) {
				System.out.print("Directory is empty");
				
				if (ZIpPath == null|| ZIpPath.equals("")) {
					System.out.print("here");
				}
			} else {
				System.out.print("Directory is not empty");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
*/

//class Test {
//
//public static void main(String[] args) throws IOException {
//
//try {
//	File DFfile = new File("E:/st/.metadata/.plugins/org.eclipse.wst.server.core/tmp4/wtpwebapps/dcps/SAMPLE");
//	if (!DFfile.exists()) {
//		throw new ServletException("Folder doesn't exists on server.");
//	}
//	FileUtils.deleteDirectory(DFfile);
//	System.out.print("done");
//} catch (Exception e) {
//	e.printStackTrace();
//}
///* end Delete directory folder */
//
///* Delete ZIP  */
//try{
//File DZfile = new File("E:/st/.metadata/.plugins/org.eclipse.wst.server.core/tmp4/wtpwebapps/dcps/SAMPLE.zip");
//if (!DZfile.exists()) {
//	throw new ServletException("Zip  doesn't exists on server.");
//}
//DZfile.delete();
//}catch(Exception e){
//	e.printStackTrace();
//}
//}
//}

//class Test {
////delete Folder
//	public static void main(String[] args) throws IOException {
//		try {
//			//String deleteFolder = "E:/st/.metadata/.plugins/org.eclipse.wst.server.core/tmp4/wtpwebapps/dcps/DAMPLE";
//			File file1 = new File("E:/st/.metadata/.plugins/org.eclipse.wst.server.core/tmp4/wtpwebapps/dcps/DAMPLE");
//			if (!file1.exists()) {
//				throw new ServletException("File doesn't exists on server.");
//			}
//			FileUtils.deleteDirectory(file1);
//			System.out.print("done");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//}

//class Test{
///////delete zip file
//	public static void main(String[] args) throws IOException {
//		try{
//		//String deleteZip = "E:/st/.metadata/.plugins/org.eclipse.wst.server.core/tmp4/wtpwebapps/dcps/one.zip";
//        File file1 = new File("E:/st/.metadata/.plugins/org.eclipse.wst.server.core/tmp4/wtpwebapps/dcps/one.zip");
//        if (!file1.exists()) {
//			throw new ServletException("File doesn't exists on server.");
//		}
//        file1.deleteOnExit();
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}
//}

//class Test{
//	public static void main(String[] args) throws IOException {
//		String[] inputParameters = {"E:/st/.metadata/.plugins/org.eclipse.wst.server.core/tmp4/wtpwebapps/dcps/SAMPLE/"};
//		RunSubsRegFvu.main(inputParameters);
//	}
//}

//class Test {
////compress code
//public static void main(String[] args) throws IOException {
//
// File input = new File("F:/sample/sample_photo/one_photo.jpg");
// BufferedImage image = ImageIO.read(input);
//
// File compressedImageFile = new File("F:/sample/sample_photo/compressed_image.jpg");
// OutputStream os = new FileOutputStream(compressedImageFile);
//
// Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
// ImageWriter writer = (ImageWriter) writers.next();
//
// ImageOutputStream ios = ImageIO.createImageOutputStream(os);
// writer.setOutput(ios);
//
// ImageWriteParam param = writer.getDefaultWriteParam();
//
// param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
// param.setCompressionQuality(0.40f);  // Change the quality value you prefer
// writer.write(null, new IIOImage(image, null, null), param);
//
// os.close();
// ios.close();
// writer.dispose();
//}
//}


//class Test {
//	public static void main(String args[]) throws ParseException {
//		int contributionDays=31;
//		if(contributionDays<32){
//			System.out.println("--> here 1");	
//		}else{
//			System.out.println("--> here 2");
//		}	
//	}
//}



//class Test {
//	public static void main(String args[]) throws ParseException {
//		
////		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
////		DecimalFormat deci = new DecimalFormat("##.##");
////		String septEDate = "31/03/2019";
////		String lStrTypeOfPayment="700047";
////		lStrtxtStartDate
////
////		if ((lStrTypeOfPayment.equals("700047") || lStrTypeOfPayment.equals("700048")||lStrTypeOfPayment.equals("700049"))&& (sdf1.parse(lStrtxtStartDate).after(sdf1.parse(septEDate)))) {
////			
////		}
//		
//		
//		DecimalFormat deci = new DecimalFormat("##");
//		
//		Double Total=10.1;
//		
//		//Double Total1=11.12;
//		
//		//Double Total2=12.11544542;
//		
//		
////		System.out.println("-->"+Math.ceil(deci.format(Total)));
////		System.out.println("-->"+deci.format(Total1));
////		System.out.println("-->"+deci.format(Total2));
//		
//		
//		System.out.println("-->"+Math.ceil(Double.parseDouble(deci.format((Total)))));
//		
//		
////		Double a=50633D;
////		Double b=4557D;
////		Double Total1=a+b;
////		double perc=(Total1*0.10);
////		System.out.println("-->"+Math.ceil(perc));	
//	}
//}




//class Test {
//	public static void main(String args[]) throws ParseException {
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		String septEDate = "30/09/2019";
//		String lStrtxtStartDate = "21/10/2019";
//		String lStrTypeOfPayment = "700046";
//		
//		if ((lStrTypeOfPayment.equals("700047")|| lStrTypeOfPayment.equals("700048") || lStrTypeOfPayment.equals("700049"))
//				&& (sdf.parse(lStrtxtStartDate).after(sdf.parse(septEDate)))) {
//			
//			System.out.println("last:"+lStrTypeOfPayment);
//
//		}else{
//			System.out.println("no");
//		}
//	}
//}




//class Test {
//public static void main(String args[]) {
//
//	String sample = "Avenger";
//	
//	String last = (sample.charAt(sample.length() -1))+"";
//    last.trim();
//	System.out.println(last);
//	if(!last.equals("s")){
//		System.out.println("first:"+sample);
//	}else{
//		System.out.println("last:"+sample);
//		}
//}
//}



//class Test {
//	public static void main(String args[]) {
//
//		List one = new List();
//		List two = new List();
//
//		one.add("4");
//		one.add("1");
//		two.add("3");
//		two.add("0");
//
//		ListIterator itr=((Object) one).listIterator();    
//        System.out.println("Traversing elements in forward direction");    
//        while(itr.hasNext()){    
//              
//        System.out.println("index:"+itr.nextIndex()+" value:"+itr.next());    
//        }    
//		
//		// System.out.println("one :"+one.toString());
//		// System.out.println("two :"+two.toString());
//
//		
//	}
//}




//class Test {
//	public static void main(String args[]) {
//		String yrCode = "2019";
//		String month = "10";
//		if (yrCode == "2019" && month == "10") {
//
//			System.out.println("here");
//
//		} else {
//			
//			System.out.println("here1");
//		}
//	}
//}

//public class Test { 
//    public static void main(String args[]) 
//    { 
//  
//    	String CSV = "500-600";
//
//        String[] values = CSV.split("-");
//
//        System.out.println(values[0]);
//        System.out.println(values[1]);
//    } 
//} 
//class Test {
//	public static void main(String[] args) throws ParseException {
//		DecimalFormat deci = new DecimalFormat("##.##");
//
//		double conti21,conti31 = 15183,conti11 = 2274;
//		conti21 = Math.ceil(Double.parseDouble(deci.format(conti31-((conti11*0.40)+conti11))));
//		System.out.println("format-->" + deci.format(conti31-((conti11*0.40)+conti11)));
//		System.out.println("here2-->" + Double.parseDouble(deci.format(conti31-((conti11*0.40)+conti11))));
//		System.out.println("conti21-->" + conti21);
//		
//		double conti211,conti311 = 22077,conti111 = 2912;
//		conti211 = Math.ceil(Double.parseDouble(deci.format(conti311-((conti111*0.40)+conti111))));
//		System.out.println("format-->" + deci.format(conti311-((conti111*0.40)+conti111)));
//		System.out.println("here2-->" + Double.parseDouble(deci.format(conti311-((conti111*0.40)+conti111))));
//		System.out.println("conti21-->" + conti211);
//	}
//}

//class Test{
//   public static void main(String args[]) throws ParseException{
//	 SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//	 String aprSDate="01/04/2019";
//		String marEDate="31/03/2019";
//		String lStrtxtStartDate="13/03/2019";
//		String lStrtxtEndDate="14/04/2019";
//	 if( (sdf.parse(aprSDate).after(sdf.parse(lStrtxtEndDate)))){//(sdf.parse(lStrtxtStartDate).before(sdf.parse(marEDate)))&&
//		 System.out.println("Number of Days between dates: ");
//	    }
//	 try {
//////	       Date dateBefore = myFormat.parse(dateBeforeString);
//////	       Date dateAfter = myFormat.parse(dateAfterString);
////	       long difference = dateAfter.getTime() - dateBefore.getTime();
////	       float daysBetween = ((difference / (1000*60*60*24))+1);
////               /* You can also convert the milliseconds to days using this method
////                * float daysBetween = 
////                *         TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS)
////                */
////	       System.out.println("Number of Days between dates: "+daysBetween);
//	 } catch (Exception e) {
//	       e.printStackTrace();
//	 }
//   }
//}

//class Test{
//	public static void main(String[] args) throws ParseException {
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		String aprSDate="01/04/2019";
//		String marEDate="31/03/2019";
//		String lStrtxtStartDate="01/01/2019";
//		String lStrtxtEndDate="31/01/2019";
//		
//		GregorianCalendar startCalendar = new GregorianCalendar();
//		startCalendar.setTime(sdf.parse(lStrtxtStartDate));
//		GregorianCalendar endCalendar = new GregorianCalendar();
//		endCalendar.setTime(sdf.parse(lStrtxtEndDate));
//		int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
//		int diffMonth = (diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH))+1;
//		
//		System.out.println("here-->"+diffYear);
//		System.out.println("here1-->"+diffMonth);
//		
//		
//		//GregorianCalendar startCalendar = new GregorianCalendar();
//		startCalendar.setTime(sdf.parse(aprSDate));
//		//GregorianCalendar endCalendar = new GregorianCalendar();
//		endCalendar.setTime(sdf.parse(lStrtxtEndDate));
//		int diffYear1 = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
//		int diffMonth1 = (diffYear1 * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH))+1;
//		System.out.println("here-->"+diffYear1);
//		System.out.println("here1-->"+diffMonth1);
//		
//	}
//	}

//class Test {
//	public static void main(String[] args) throws ParseException {
//		String ifDeputation="2";
//		if("2".equalsIgnoreCase(ifDeputation)){
//			System.out.println("here-->");
//		}
//		System.out.println("heeeeeeeeeeeeeeee-->");	
////		for (int i = 0; i < 10; ++i)
////        {
////          //Object[] obj1 = (Object[])lListTotalDdowiseEntries.get(i);
////          System.out.println("obj1 lListTotalDdowiseEntries-->"+i);
////          for (int m = 0; m < 10; ++m)
////          {
////        	  System.out.println("obj1 lListTotalDdowiseEntries-->"+m);
////            if (m == i)
////            {
////            	System.out.println("obj1 bf continue-->");
////                continue;
////            }
////            System.out.println("obj1 ouside if -->");
////          }
////        }
//	}
//}



//class Test{
//	public static void main(String[] args) throws ParseException {
//		String date1="02/06/2019";
//		String date2="31/03/2019";
//		
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		System.out.println(sdf.parse(date1));
//		System.out.println(sdf.parse(date2));
//		
//		
//		if(sdf.parse(date1).after(sdf.parse(date2))){
//			System.out.println("here");
//		}
//		
//	}
//	
//}




// class Compresssion {
//
//  public static void main(String[] args) throws IOException {
//
//    File input = new File("original_image.jpg");
//    BufferedImage image = ImageIO.read(input);
//
//    File compressedImageFile = new File("compressed_image.jpg");
//    OutputStream os = new FileOutputStream(compressedImageFile);
//
//    Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
//    ImageWriter writer = (ImageWriter) writers.next();
//
//    ImageOutputStream ios = ImageIO.createImageOutputStream(os);
//    writer.setOutput(ios);
//
//    ImageWriteParam param = writer.getDefaultWriteParam();
//
//    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
//    param.setCompressionQuality(0.05f);  // Change the quality value you prefer
//    writer.write(null, new IIOImage(image, null, null), param);
//
//    os.close();
//    ios.close();
//    writer.dispose();
//  }
//}


//class Test {
//	public static void main(String[] args) throws ParseException {
//		if (true) {
//			String str="two";
//			check(str);
//		}
//	}
//
//	static boolean check(String str) {
//		boolean ret = false;
//		if(str.equals("one")){
//			System.out.println("here");
//		ret= true;
//		}
//		return ret;
//	}
//	
//
//}

//class Test {
//	public static void main(String[] args) throws ParseException {
//		//Fri Mar 01 2019,Sat Mar 2019,
//		
////		starts         = Wed Jul 01 00:00:00 IST 2015;
////	    contriStartDate= Fri Mar 01 00:00:00 IST 2019;
////	    ends           = Wed Sep 30 00:00:00 IST 2015;
////	    contriEndDate  = Sun Mar 31 00:00:00 IST 2019
//		
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");//Wed Jul 01 00:00:00 IST 2015
//		Date contriStartDate = sdf.parse("Fri Mar 01 00:00:00 IST 2019");
//		Date contriEndDate= sdf.parse("Sat Mar 30 00:00:00 IST 2019");
//		System.out.println("contriStartDate.. "+contriStartDate);
//		System.out.println("contriEndDate.. "+contriEndDate);
//		Date strts = null;
//		Date strts16 = null;
//		
//		try {
//			strts = sdf.parse("01/07/2015");
//			strts16 = sdf.parse("01/07/2016");
//			System.out.println("strts.. "+strts);
//		} catch (ParseException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		Date ends = null;
//		Date ends16 = null;
//		try {
//			ends = sdf.parse("30/09/2015");
//			ends16 = sdf.parse("31/03/2017");
//			System.out.println("ends.. "+ends);
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		int comparison1 = contriStartDate.compareTo(strts);
//		int comparison2 = contriEndDate.compareTo(ends);
//		
//		System.out.println("comparison1.. "+comparison1);
//		System.out.println("comparison2.. "+comparison2);
//	}
//}

 /*public class Test {

 public static void main(String[] args) {

 try {
 FileReader fr = new FileReader("E://1010113/stockFile.txt");
 BufferedReader br = new BufferedReader(fr);
 FileWriter fw = new FileWriter("E://1010113/photo/output.txt", true);
 String s;

 while ((s = br.readLine()) != null) { // read a line
	 System.out.print(s);
 fw.write(s); // write to output file
 fw.flush();
 }
 br.close();
 fw.close();
 System.out.println("file copied");
 } catch (IOException e) {
 // TODO Auto-generated catch block
 e.printStackTrace();
 }
 }
 }*/

// public class Test {
//
// public static void main(String[] args) {
// String str = "two";
// if (str.equals("one")) {
// System.out.println("here1");
// } else if (str.equals("two")) {
//
// System.out.println("else if 1");
// }else if (str.equals("thre")) {
//
// System.out.println("else if 2");
// }else {
// System.out.println("here else");
// }
// }
// }

// public class Test extends Component {
//
// BufferedImage img;
//
// public void paint(Graphics g) {
// g.drawImage(img, 0, 0, null);
// }
//
// public Test() {
// try {
// img = ImageIO.read(new File("C:/Users/476039/Desktop/strawberry.jpg"));
// System.out.println("img.."+img);
// } catch (IOException e) {
// }
//
// }
//
// public Dimension getPreferredSize() {
// if (img == null) {
// return new Dimension(100,100);
// } else {
// return new Dimension(img.getWidth(null), img.getHeight(null));
// }
// }
//
// public static void main(String[] args) {
//
// JFrame f = new JFrame("Load Image Sample");
//
// f.addWindowListener(new WindowAdapter(){
// public void windowClosing(WindowEvent e) {
// System.exit(0);
// }
// });
//
// f.add(new Test());
// f.pack();
// f.setVisible(true);
// }
// }

