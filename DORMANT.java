import java.sql.*;
import com.ibm.as400.access.*;
import java.io.*;
import java.util.*;

public class DORMANT {
	
	private Connection connection = null;
	
	public static int SeqNum = 1;
	
	public static void main(String[] args) {
		
		DORMANT test = new DORMANT();
		System.out.println("DORMANT Check.");
		test.runQuery( args[ 0 ], args[ 1 ] );	
		test.cleanup();
	}

	public DORMANT() {
			
		try {		
			DriverManager.registerDriver(new AS400JDBCDriver());
			connection = DriverManager.getConnection("jdbc:as400://10.10.201.234");
		} 
		catch (Exception e) {
			System.out.println("Caught exception: " + e.getMessage());
		}
	}

	public void runQuery( String WID, String EXTJRN ) {
		
		try {
			
			Statement s = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
			ResultSet rs;
			
			rs = s.executeQuery("select CAST(GFCUS AS CHAR(6) CCSID 37) as GFCUS, CAST(GFCUZ AS CHAR(1) CCSID 37) as GFCUZ from AMBRPTS.DRMCUS12 where GFCUZ = 'N'");
			
			while( rs.next() ){
			//	System.out.println(rs.getString(1));
				UpdateJournal( WID, rs.getString(1), EXTJRN );
				SeqNum++;
			}
		//	UpdateJournal( WID, "001456", EXTJRN );
			s.close();
		}

		catch (Exception e) {
			System.out.println("Exception exception: ");
			System.out.println("Message:....." + e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	public void UpdateJournal( String WID, String Customer, String EXTJRN ){
		try {
			Calendar calendar = Calendar.getInstance();
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			int minute = calendar.get(Calendar.MINUTE);
			int second = calendar.get(Calendar.SECOND);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			String time = hour + "" + minute + "" + second;
		
			Statement s = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
			Statement s1 = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
		
			ResultSet rs;
			
			rs = s1.executeQuery("select GFCLC, GFCUN, GFCPNC, GFDAS, GFCTP, GFCUB, GFCUC, GFCUD, GFCUZ, GFSAC, GFACO, GFCRF, GFCA2, GFCNAP, GFCNAR, GFCNAL, GFCOD, GFDCC, GFGRP, GFBRNM, GFLNM, GFCRB1, GFCRB2, GFADJ, GFERCP, GFERCC, GFDRC, GFCUNA, GFDASA, GFCNAI, GFMTB, GFETX, GFYFON, GFDFRQ, GFFON, GFFOL, GFCREF, GFOATP, GFOCCL, GFHDD, GFDDED, GFRDDH, GFYTRI, GFYRET, GFYPLA, GFYOPI, GFYNET, GFYRI1, GFYRI2, GFYRI3, GFYRI4, GFCS, GFCFRQ, GFFCYC, GFCSSA, GFPSTM, GFNSTM, GFOCID, GFCLSF, GFCLST, GFCLTV, GFCLTP, GFDRTY, GFMRTY, GFCST from kfilm00.gfpf where GFCUS = '" + Customer + "'");
			
			if( rs.next() ){		
				s.executeUpdate("insert into " + EXTJRN + ".GYPF values('" + WID + "', '" + WID + "', " + day + ", " + time + ", " + SeqNum + ", 'G01M', 'M', '', '', '', 'MCD', '', '', '', '', 0, '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', 0, 0, 0, '', '', '', 'Y', '', 0, 0, '', 0, 0, '', '', '', '', '')");
				
				s.executeUpdate("insert into " + EXTJRN + ".GZG011 values('" 
					+ WID + "', " //Workstation id
					+ day + ", "  //Day in month
					+ time + ", " //Time
					+ SeqNum //Sequence number
					+ ", 'B', '" //Journal image
					+ Customer + "', '" //Customer mnemonic
					+ rs.getString(1) + "', '" //Customer location
					+ rs.getString(2) + "', '" //Customer full name
					+ rs.getString(3) + "', '" //Customer's basic number
					+ rs.getString(4) + "', '" //Default account short name
					+ rs.getString(5) + "', '" //Customer type
					+ rs.getString(6) + "', '" //Blocked for deal input?
					+ rs.getString(7) + "', '" //Closed?
					+ rs.getString(8) + "', '" //Deceased or in liquidation?
					+ rs.getString(9) + "', '" //Inactive?
					+ rs.getString(10) + "', '" //Sundry analysis code
					+ rs.getString(11) + "', '" //Responsibility code
					+ rs.getString(12) + "', '" //Customer reference
					+ rs.getString(13) + "', '" //Sundry reference code
					+ rs.getString(14) + "', '" //Parent country
					+ rs.getString(15) + "', '" //Risk country
					+ rs.getString(16) + "', " //Residence country
					+ rs.getString(17) + ", " //Date customer first put on the database
					+ rs.getString(18) + ", '" //Date customer closed
					+ rs.getString(19) + "', '" //Group name
					+ rs.getString(20) + "', '"//Branch mnemonic
				 	+ rs.getString(21) + "', '" //Language code
				 	+ rs.getString(22) + "', '" //Tax code 1
				 	+ rs.getString(23) + "', 'N', '', '', " //Tax code 2, Update all accounts?, Group 2 fields maintained?, Group 3 fields maintained?
				 	+ rs.getString(24) + ", '" //Customer interest rate adjustment
				 	+ rs.getString(25) + "', '" //Principal exchange rate code
				 	+ rs.getString(26) + "', '" //Charge exchange rate code
				 	+ rs.getString(27) + "', '" //Download to front office?
				 	+ rs.getString(28) + "', '" //Arabic customer full name
				 	+ rs.getString(29) + "', '', '', '', '', '', '', '', '', '', '', '" //Arabic account short name, First name, Family name, Father's name, Grandfather's name, First name - Arabic, Family name - Arabic, Father's name - Arabic, Grandfather's name - Arabic, Prefix title code, Suffix title code
				 	+ rs.getString(30) + "', '', '" //Internal risk country, Principal group
				 	+ rs.getString(31) + "', '" //Mail to branch
				 	+ rs.getString(32) + "', '" //Exempt from TF taxes
				 	+ rs.getString(33) + "', " //FONTIS Customer?
				 	+ rs.getString(34) + ", '" //FONTIS Download frequency
				 	+ rs.getString(35) + "', '" //Front office name
				 	+ rs.getString(36) + "', '', '', '', '', '" //Front office location, Next available number was used, User generated customer numbers are in force, Customer/Bank qualifier, Yes/no indicator?
				 	+ rs.getString(37) + "', '" //Loan Origination reference
				 	+ rs.getString(38) + "', '" //OPICS accounting type
				 	+ rs.getString(39) + "', '" //OPICS Customer classification
				 	+ rs.getString(40) + "', " //Hold direct debit
				 	+ rs.getString(41) + ", '" //Hold direct debit expiry date
				    + rs.getString(42) + "', '" //Reason for direct debit hold
				    + rs.getString(43) + "', '" //Trade finance
				    + rs.getString(44) + "', '" //Retail branch
				    + rs.getString(45) + "', '" //Market risk
				    + rs.getString(46) + "', '" //OPICS
				    + rs.getString(47) + "', '" //Internet
				    + rs.getString(48) + "', '" //Reserve interface 1
				    + rs.getString(49) + "', '" //Reserve interface 2
				    + rs.getString(50) + "', '" //Reserve interface 3
				    + rs.getString(51) + "', '" //Reserve interface 4
				    + rs.getString(52) + "', '" //Consolidated statements
				    + rs.getString(53) + "', '" //Consolidated statement frequency code
				    + rs.getString(54) + "', '" //Force cycle statement
				    + rs.getString(55) + "', " //Copy statement special address
				    + rs.getString(56) + ", " //Previous statement date
				    + rs.getString(57) + ", '" //Next statement date
				    + rs.getString(58) + "', 0, " //OPICS customer mnemonic, Loan Origination Reference sequence number 
				    + rs.getString(59) + ", " //CLS eligible from date
				    + rs.getString(60) + ", '" //CLS eligible to date
				    + rs.getString(61) + "', '" //CLS date trade/value
				    + rs.getString(62) + "', '" //CLS type
				    + rs.getString(63) + "', '" //Days to retry queued trans
				    + rs.getString(64) + "', '', '', '" //Months to retry Qd trans, Suppress Watch List Checking?, Suspect on External System?
				    + rs.getString(65) + "', '', '', '', '', '')"); //Case status, Watch list checked?, Nominated charge account branch, Nominated charge account number, Nominated charge account suffix, From EBA?
			
				s.executeUpdate("insert into " + EXTJRN + ".GZG011 values('" 
					+ WID + "', " //Workstation id
					+ day + ", "  //Day in month
					+ time + ", " //Time
					+ SeqNum //Sequence number
					+ ", 'A', '" //Journal image
					+ Customer + "', '" //Customer mnemonic
					+ rs.getString(1) + "', '" //Customer location
					+ rs.getString(2) + "', '" //Customer full name
					+ rs.getString(3) + "', '" //Customer's basic number
					+ rs.getString(4) + "', '" //Default account short name
					+ rs.getString(5) + "', '" //Customer type
					+ rs.getString(6) + "', '" //Blocked for deal input?
					+ rs.getString(7) + "', '" //Closed?
					+ rs.getString(8) + "', '" //Deceased or in liquidation?
					+ "Y', '" //Inactive?
					+ rs.getString(10) + "', '" //Sundry analysis code
					+ rs.getString(11) + "', '" //Responsibility code
					+ rs.getString(12) + "', '" //Customer reference
					+ rs.getString(13) + "', '" //Sundry reference code
					+ rs.getString(14) + "', '" //Parent country
					+ rs.getString(15) + "', '" //Risk country
					+ rs.getString(16) + "', " //Residence country
					+ rs.getString(17) + ", " //Date customer first put on the database
					+ rs.getString(18) + ", '" //Date customer closed
					+ rs.getString(19) + "', '" //Group name
					+ rs.getString(20) + "', '"//Branch mnemonic
				 	+ rs.getString(21) + "', '" //Language code
				 	+ rs.getString(22) + "', '" //Tax code 1
				 	+ rs.getString(23) + "', 'Y', '', 'Y', " //Tax code 2, Update all accounts?, Group 2 fields maintained?, Group 3 fields maintained?
				 	+ rs.getString(24) + ", '" //Customer interest rate adjustment
				 	+ rs.getString(25) + "', '" //Principal exchange rate code
				 	+ rs.getString(26) + "', '" //Charge exchange rate code
				 	+ rs.getString(27) + "', '" //Download to front office?
				 	+ rs.getString(28) + "', '" //Arabic customer full name
				 	+ rs.getString(29) + "', '', '', '', '', '', '', '', '', '', '', '" //Arabic account short name, First name, Family name, Father's name, Grandfather's name, First name - Arabic, Family name - Arabic, Father's name - Arabic, Grandfather's name - Arabic, Prefix title code, Suffix title code
				 	+ rs.getString(30) + "', '', '" //Internal risk country, Principal group
				 	+ rs.getString(31) + "', '" //Mail to branch
				 	+ rs.getString(32) + "', '" //Exempt from TF taxes
				 	+ rs.getString(33) + "', " //FONTIS Customer?
				 	+ rs.getString(34) + ", '" //FONTIS Download frequency
				 	+ rs.getString(35) + "', '" //Front office name
				 	+ rs.getString(36) + "', '', '', '', '', '" //Front office location, Next available number was used, User generated customer numbers are in force, Customer/Bank qualifier, Yes/no indicator?
				 	+ rs.getString(37) + "', '" //Loan Origination reference
				 	+ rs.getString(38) + "', '" //OPICS accounting type
				 	+ rs.getString(39) + "', '" //OPICS Customer classification
				 	+ rs.getString(40) + "', " //Hold direct debit
				 	+ rs.getString(41) + ", '" //Hold direct debit expiry date
				    + rs.getString(42) + "', '" //Reason for direct debit hold
				    + rs.getString(43) + "', '" //Trade finance
				    + rs.getString(44) + "', '" //Retail branch
				    + rs.getString(45) + "', '" //Market risk
				    + rs.getString(46) + "', '" //OPICS
				    + rs.getString(47) + "', '" //Internet
				    + rs.getString(48) + "', '" //Reserve interface 1
				    + rs.getString(49) + "', '" //Reserve interface 2
				    + rs.getString(50) + "', '" //Reserve interface 3
				    + rs.getString(51) + "', '" //Reserve interface 4
				    + rs.getString(52) + "', '" //Consolidated statements
				    + rs.getString(53) + "', '" //Consolidated statement frequency code
				    + rs.getString(54) + "', '" //Force cycle statement
				    + rs.getString(55) + "', " //Copy statement special address
				    + rs.getString(56) + ", " //Previous statement date
				    + rs.getString(57) + ", '" //Next statement date
				    + rs.getString(58) + "', 0, " //OPICS customer mnemonic, Loan Origination Reference sequence number 
				    + rs.getString(59) + ", " //CLS eligible from date
				    + rs.getString(60) + ", '" //CLS eligible to date
				    + rs.getString(61) + "', '" //CLS date trade/value
				    + rs.getString(62) + "', '" //CLS type
				    + rs.getString(63) + "', '" //Days to retry queued trans
				    + rs.getString(64) + "', '', '', '" //Months to retry Qd trans, Suppress Watch List Checking?, Suspect on External System?
				    + rs.getString(65) + "', '', '', '', '', '')"); //Case status, Watch list checked?, Nominated charge account branch, Nominated charge account number, Nominated charge account suffix, From EBA?
				}
			s.close();
			s1.close();
		} 
		catch (SQLException e) {
			System.out.println("SQLException exception: ");
			System.out.println("Message:....." + e.getMessage());
			System.out.println("SQLState:...." + e.getSQLState());
			System.out.println("Vendor Code:." + e.getErrorCode());
			e.printStackTrace();
		}
		catch (Exception e) {
			System.out.println("Exception exception: ");
			System.out.println("Message:....." + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void cleanup() {
		try {
			if (connection != null)
				connection.close();
		} 
		catch (Exception e) {
			System.out.println("Caught exception: ");
			e.printStackTrace();
		}
	}
}