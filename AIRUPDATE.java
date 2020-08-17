import java.sql.*;
import com.ibm.as400.access.*;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.io.File;
		
public class AIRUPDATE {
	
	private Connection connection = null;
	
	public static int SeqNum = 1;
	
	String RecCCY = "", RecAmount = "", BenAccount = "", RecValueDate = "", RecAcSuffix = "";
	String OrderAccountAC = "", OrderAccountAdd = "", SwiftAddress = "", RecNar1 = "", RecNar2 = "", RecNar3 = "";
	String PayValueDate = "", PaymentDetails = "", PaymentDetails1 = "", PaymentDetails2 = "", PayNar1 = "", PayNar2 = "", PayNar3 = "", PurposeCode = "";
	String Scab = "", Scan = "", Scas = "", RecEqDate = "", PayEqDate = "", SwiftBank = "", SwiftCode = "", SwiftLocation = "", SwiftBranch = "";
	String WID1 = "", EXTJRN1 = "", Obsolete = "", Address1 = "", Address2 = "", Address3 = "", Address4 = "", SenderRef = "";	
	Double RecAmountDB = 0.0;
	String Filename = "", RecDayTemp = "", PayDayTemp = "";
	Calendar c1 = Calendar.getInstance();
	SimpleDateFormat dateformatter = new SimpleDateFormat ("yyyy.MM.dd");
	
	public static void main(String[] args) {
		
		AIRUPDATE test = new AIRUPDATE();
		test.runQuery(args[0], "EXTJRNF01");	
		test.cleanup();
	}

	public AIRUPDATE() {
			
		try {		
			DriverManager.registerDriver(new AS400JDBCDriver());
			connection = DriverManager.getConnection("jdbc:as400://10.10.201.204");
		} 
		catch (Exception e) {
			System.out.println("Caught exception: " + e.getMessage());
		}
	}

	public void runQuery( String WorkstationID, String ExternalJrn ) {
		
		WID1 = WorkstationID;
		EXTJRN1 = ExternalJrn;
			
		String files;
    	File folder = new File("/BULKCREDIT");
    	File[] listOfFiles = folder.listFiles(); 

       	for (int i = 0; i < listOfFiles.length; i++) {

       		if (listOfFiles[i].isFile())  {

                System.out.println(listOfFiles[i]);
				Filename = listOfFiles[i].getName();
				RecCCY = ""; 
				RecAmount = ""; 
				BenAccount = ""; 
				RecValueDate = "";
				OrderAccountAC = ""; 
				OrderAccountAdd = ""; 
				SwiftAddress = ""; 
				RecNar1 = ""; 
				RecNar2 = ""; 
				RecNar3 = "";
				PayValueDate = ""; 
				PaymentDetails = "";
				PaymentDetails1 = "";
				PaymentDetails2	= ""; 
				PayNar1 = ""; 
				PayNar2 = ""; 
				PayNar3 = ""; 
				PurposeCode = "";
				Scab = ""; 
				Scan = ""; 
				Scas = ""; 
				RecEqDate = ""; 
				PayEqDate = ""; 
				SwiftBank = ""; 
				SwiftCode = ""; 
				SwiftLocation = ""; 
				SwiftBranch = "";
				Obsolete = ""; 
				Address1 = ""; 
				Address2 = ""; 
				Address3 = ""; 
				Address4 = "";
				RecAcSuffix = "";
				SenderRef = "";
				RecAmountDB = 0.0;
				RecDayTemp = "";
				PayDayTemp = "";
				c1 = Calendar.getInstance();
					
				try {
					SAXParserFactory factory = SAXParserFactory.newInstance();
					SAXParser saxParser = factory.newSAXParser();
				 
					DefaultHandler handler = new DefaultHandler() {
				 
						boolean EndToEndId = false;
						boolean IntrBkSttlmAmt = false;
						boolean Dbtr = false;
						boolean DbtrNm = false;
						boolean DbtrAcct = false;
						boolean DbtrAcctIBAN = false;
						boolean DbtrAgt = false;
						boolean DbtrAgtBIC = false;
						boolean IntrBkSttlmDt = false;
						boolean Cdtr = false;
						boolean CdtrNm = false;
						boolean CdtrAcct = false;
						boolean CdtrAcctIBAN = false;
						boolean InstrInf = false;
					 
						public void startElement(String uri, String localName,String qName, 
					        Attributes attributes) throws SAXException {
					 
					 		if (qName.equalsIgnoreCase("IntrBkSttlmDt")) {
								IntrBkSttlmDt = true;
							}
							
							if (qName.equalsIgnoreCase("EndToEndId")) {
								EndToEndId = true;
							}
					 
							else if (qName.equalsIgnoreCase("IntrBkSttlmAmt")) {
								IntrBkSttlmAmt = true;
								RecCCY = attributes.getValue(0);
								if(RecCCY.equals("LBP"))
									RecAcSuffix = "014";
								else RecAcSuffix = "016";
								System.out.println("CCY = " + RecCCY);
							}
					 
							else if (qName.equalsIgnoreCase("Dbtr")) {
								Dbtr = true;
							}
					 
					 		else if (Dbtr){
								if (qName.equalsIgnoreCase("Nm")) {
									DbtrNm = true;
								}
					 		}
							
							else if (qName.equalsIgnoreCase("DbtrAcct")) {
								DbtrAcct = true;
							}
							
							else if (DbtrAcct){
								if (qName.equalsIgnoreCase("IBAN")) {
									DbtrAcctIBAN = true;
								}
							}
					 
					 		else if (qName.equalsIgnoreCase("DbtrAgt")) {
								DbtrAgt = true;
							}
							
							else if(DbtrAgt){
								if (qName.equalsIgnoreCase("BIC")) {
									DbtrAgtBIC = true;
								}
							}
							
							else if (qName.equalsIgnoreCase("Cdtr")) {
								Cdtr = true;
							}
					 
					 		else if(Cdtr){
								if (qName.equalsIgnoreCase("Nm")) {
									CdtrNm = true;
								}
					 		}
							
							else if (qName.equalsIgnoreCase("CdtrAcct")) {
								CdtrAcct = true;
							}
							
							else if(CdtrAcct){
								if (qName.equalsIgnoreCase("IBAN")) {
									CdtrAcctIBAN = true;
								}
							}
							
							else if (qName.equalsIgnoreCase("InstrInf")) {
								InstrInf = true;
							}
						}
					 
						public void endElement(String uri, String localName,
							String qName) throws SAXException {
							
							if (qName.equalsIgnoreCase("Dbtr")) {
								Dbtr = false;
							}
							
							else if (qName.equalsIgnoreCase("DbtrAcct")) {
								DbtrAcct = false;
							}
							
							else if (qName.equalsIgnoreCase("DbtrAgt")) {
								DbtrAgt = false;
							}
							
							else if (qName.equalsIgnoreCase("Cdtr")) {
								Cdtr = false;
							}
							
							else if (qName.equalsIgnoreCase("CdtrAcct")) {
								CdtrAcct = false;
							}
							
							else if (qName.equalsIgnoreCase("CdtTrfTxInf")) {
								UpdateJournal( WID1, EXTJRN1 );
								SeqNum++;
								System.out.println("***********************");
							}
					 
						}
					 
						public void characters(char ch[], int start, int length) throws SAXException {
					 
					 		if (IntrBkSttlmDt) {
					 			//c1.add(Calendar.DATE, 2);
								//PayValueDate = dateformatter.format(c1.getTime());
								RecValueDate = new String(ch, start, length);
								c1.set( Integer.parseInt(RecValueDate.substring(0,4)), Integer.parseInt(RecValueDate.substring(5,7)) - 1, Integer.parseInt(RecValueDate.substring(8,10)) );
								c1.add(Calendar.DATE, 2);
								PayValueDate = dateformatter.format(c1.getTime());
								RecEqDate = "1" + RecValueDate.substring(2,4) + RecValueDate.substring(5,7) + RecValueDate.substring(8,10);
								PayEqDate = "1" + PayValueDate.substring(2,4) + PayValueDate.substring(5,7) + PayValueDate.substring(8,10);	
								RecDayTemp = RecEqDate;
								PayDayTemp = PayEqDate;
								System.out.println("IntrBkSttlmDt : " + RecValueDate);
								System.out.println("RecEqDate : " + RecEqDate);
								System.out.println("PayValueDate : " + PayValueDate);
								System.out.println("PayEqDate : " + PayEqDate);
								IntrBkSttlmDt = false;
							}
							
							else if (EndToEndId) {
								RecNar1 = new String(ch, start, length);
								if(RecNar1.substring(0,6).equals("SALARY"))
									PurposeCode = "10";
								else PurposeCode = "07";
								SenderRef = RecNar1.substring(7);
								if(SenderRef.length() > 16)
									SenderRef = SenderRef.substring(0,16);
								System.out.println("EndToEndId : " + RecNar1);
								System.out.println("PurposeCode : " + PurposeCode);
								System.out.println("SenderRef : " + SenderRef);
								EndToEndId = false;
							}
					 
							else if (IntrBkSttlmAmt) {
								RecAmount = new String(ch, start, length);
								RecAmountDB = Double.parseDouble(RecAmount) * 100;
								System.out.println("IntrBkSttlmAmt : " + RecAmount);
								IntrBkSttlmAmt = false;
							}
					 
							else if (DbtrNm) {
								OrderAccountAdd = new String(ch, start, length);
								OrderAccountAdd = "B/O " + OrderAccountAdd;
								if(OrderAccountAdd.length() > 35)
									OrderAccountAdd = OrderAccountAdd.substring(0,35);
								RecNar3 = OrderAccountAdd;
								PayNar3 = RecNar3;
								System.out.println("DbtrNm : " + OrderAccountAdd);
								DbtrNm = false;
							}
					 
							else if (DbtrAcctIBAN) {
								OrderAccountAC = new String(ch, start, length);
								System.out.println("DbtrAcctIBAN : " + OrderAccountAC);
								DbtrAcctIBAN = false;
							}
							
							else if (DbtrAgtBIC) {
								SwiftAddress = new String(ch, start, length);
								SwiftBank = SwiftAddress.substring(0,4);
								SwiftCode = SwiftAddress.substring(4,6);
								SwiftLocation = SwiftAddress.substring(6,8);
								SwiftBranch = SwiftAddress.substring(8,11);
								
								System.out.println("DbtrAgtBIC : " + SwiftAddress);
								System.out.println("SwiftBank : " + SwiftBank);
								System.out.println("SwiftCode : " + SwiftCode);
								System.out.println("SwiftLocation : " + SwiftLocation);
								System.out.println("SwiftBranch : " + SwiftBranch);
								DbtrAgtBIC = false;
							}
					 
							else if (CdtrNm) {
								RecNar2 = new String(ch, start, length);
								RecNar2 = "F/O " + RecNar2;
								if(RecNar2.length() > 35)
									RecNar2 = RecNar2.substring(0,35);
								PayNar2 = RecNar2;
								System.out.println("CdtrNm : " + RecNar2);
								CdtrNm = false;
							}
					 
							else if (CdtrAcctIBAN) {
								BenAccount = new String(ch, start, length);
								BenAccount = BenAccount.substring(15);
								Scab = BenAccount.substring(0,4);
							    Scan = BenAccount.substring(4,10);
								Scas = BenAccount.substring(10,13);
								System.out.println("CdtrAcctIBAN : " + BenAccount);
								System.out.println("Scab : " + Scab);
								System.out.println("Scan : " + Scan);
								System.out.println("Scas : " + Scas);
								CdtrAcctIBAN = false;
							}
							
							else if (InstrInf) {
								PaymentDetails = new String(ch, start, length);
								if(PaymentDetails.length() > 35){
									PaymentDetails1 = PaymentDetails.substring(0,35);
									PaymentDetails2 = PaymentDetails.substring(35);
									if(PaymentDetails2.length() > 35)
										PaymentDetails2 = PaymentDetails2.substring(0,35);
								}
								else {
									PaymentDetails1 = PaymentDetails;
									PaymentDetails2 = "";
								}
								System.out.println("InstrInf : " + PaymentDetails);
								InstrInf = false;
							}
						}
				 	};
				 
				    saxParser.parse(listOfFiles[i], handler);
				} 
			
				catch (Exception e) {
					System.out.println("Exception exception: ");
					System.out.println("Message:....." + e.getMessage());
					e.printStackTrace();
				}
       		}
       	}
	}

	public void UpdateJournal( String WID, String EXTJRN ){
		try {
			Calendar calendar = Calendar.getInstance();
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			int minute = calendar.get(Calendar.MINUTE);
			int second = calendar.get(Calendar.SECOND);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			String time = hour + "" + minute + "" + second;
		
			Statement s = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
			Statement s1 = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
			ResultSet rs1, rs2;
			
			rs1 = s1.executeQuery("select SXSEQ from kfilf04.sxpf where SXCUS = '" + Scan + "'"); 
			if(rs1.next()){
				Obsolete = rs1.getString(1);
				rs2 = s1.executeQuery("select SVNA1, SVNA2, SVNA3, SVNA4 from kfilf04.svpf where SVSEQ = " + Obsolete); 
				if(rs2.next()){
					Address1 = rs2.getString(1).replace("\'", "");
					Address2 = rs2.getString(2).replace("\'", "");
					Address3 = rs2.getString(3).replace("\'", "");
					Address4 = rs2.getString(4).replace("\'", "");
				}
			}
			
			if( PurposeCode.equals("10"))
				PayEqDate = RecDayTemp;
		
			else PayEqDate = PayDayTemp;
				
			s.executeUpdate("insert into jurdy.bulkpf values ('" + Filename + "', '" + WID + "', " + day + ", " + time + ", " + SeqNum + ", 'ICS', " + RecAmountDB + ", '" + RecCCY + "', '0000', '799999', '" + RecAcSuffix + "', 'AC', " + RecEqDate + ", '" + SwiftBank + "', '" + SwiftCode + "', '" + SwiftLocation + "', '" + SwiftBranch + "', '" + OrderAccountAdd + "', '" + SenderRef + "', 'SW', '" + Scab + "', '" + Scan + "', '" + Scas + "', '" + RecNar1 + "', '" + RecNar2 + "', '" + RecNar3 + "', " + PayEqDate + ", '" + PaymentDetails1 + "', '" + PaymentDetails2 + "', '" + PurposeCode + "', '" + OrderAccountAC + "', '', '')");
			
			s.executeUpdate("insert into " + EXTJRN + ".GYPF values('" + WID + "', '', " + day + ", " + time + ", " + SeqNum + ", 'K61A', 'A', '', '', '', 'AIR', '', '', '', '', 0, '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', 0, 0, 0, '', '', '', 'Y', '', 0, 0, '', 0, 0, '', '', '', '', '')");
				
			s.executeUpdate("insert into " + EXTJRN + ".GZK611 values('" 
					+ WID + "', " //Workstation id -------62 fields
					+ day + ", "  //Day in month
					+ time + ", " //Time
					+ SeqNum //Sequence number
					+ ", 'A', " //Journal image
					+ "'', " //Internal payment reference
					+ "'', " //Payment reference
					+ "'ICS', " //Payment type
					+ "'', " //Template reference
					+ "'" + RecCCY + "', " //Receive currency
					+ RecAmountDB //Receive total amount
					+ ", 0, " //Receive charge amount
					+ RecAmountDB //Receive Net amount
					+ ", '" + RecCCY + "', " //Pay currency
					+ RecAmountDB //Pay total amount
					+ ", 0, " //Pay charge amount
					+ RecAmountDB //Pay Net amount
					+ ", '', '', '', " //Exchange deal branch, type, reference
					+ "1.0" //Exchange rate
					+ ", 'Y', 'Y', 'Y', " //Multiply or divide?, Payment confirmation required?, Beneficiary advice required?
					+ "'', '', " //Remitter or beneficiary id, Receive nostro
					+ "'0000', " //Receive a/c branch
					+ "'799999', " //Receive a/c number
					+ "'" + RecAcSuffix + "', " //Receive a/c suffix
					+ "'AC', " //Receive transfer method
					+ RecEqDate //Receive value date
					+ ", 'N', " //Notice to receive required?
					+ "'" + SwiftBank + "', '" + SwiftCode + "', '" + SwiftLocation + "', '" + SwiftBranch + "', '', '', '', '', " //Ordering institution SWIFT bank id, country code, location, branch id, address 1, address 2, address 3, address 4
					+ "'" + OrderAccountAdd + "', '', '', '', '" + SenderRef + "', " //Ordering customer name & address 1, 2, 3, 4, Sender Ref
					+ "'SW', " //Sender confirmation method
					+ "'" + SwiftBank + "', " //Sender SWIFT bank id
					+ "'" + SwiftCode + "', " //Sender SWIFT country code
					+ "'" + SwiftLocation + "', " //Sender SWIFT location
					+ "'" + SwiftBranch + "', " //Sender SWIFT branch id
					+ "'', '', '', '', '', '', '', " //Sender fax number, telex number, answerback, address 1, address 2, address 3, address 4
					+ "'" + RecNar2 + "', " //Receive settlement narrative 1
					+ "'" + RecNar3 + "', " //Receive settlement narrative 2
					+ "'" + RecNar1 + "', '', '', " //Receive settlement narrative 3, 4, Pay Nostro
					+ "'" + Scab + "', " //Pay a/c branch -------100 fields
					+ "'" + Scan + "', " //Pay a/c account
					+ "'" + Scas + "', " //Pay a/c suffix
					+ "'AC', " //Pay transfer method
					+ PayEqDate //Pay value date
					+ ", '', " //Charges for BEN/OUR/SHA
					+ "'" + PaymentDetails1 + "', '" + PaymentDetails2 + "', '', '', " //Details of payment 1, 2, 3, 4
					+ "'" + PurposeCode + "', '', '', '', '', " //Payment purpose, details 1, 2, 3, 4
					+ "'', '', '', '', '', "
					+ "'" + Address1 + "', "
					+ "'" + Address2 + "', "
					+ "'" + Address3 + "', "
					+ "'" + Address4 + "', '', "
					+ "'', '', '', '', '', '', '', '', '', '', "
					+ "'', '', '', '', '', '', '', '', '', '', "
					+ "'', '', '', '', '', '', '', '', '', '', "
					+ "'', '', '', '', '', '', '', '', '', '', "
					+ "'', '', '', '', '', '', '', '" + PayNar3 + "', " //Pay settlement narrative 1
					+ "'" + PaymentDetails1 + "', " //Pay settlement narrative 2
					+ "'', '', " //Pay settlement narrative 3, 4
					+ "'1', 'N', " //Amount entered, Reffered
					+ "'" + WID + "', 0, '', 0, " //Logged by, logged date, Reviewed or maintained by, Date reviewed or maintained
					+ "'CP', '', '', '', '', '0000', '', '', '', '', '', '', '', '', " //Application Code
					+ "'" + Scab + "', " //Charge a/c branch
					+ "'" + Scan + "', " //Charge a/c number
					+ "'" + Scas + "', " //Charge a/c suffix 
					+ "'" + RecCCY + "', " //Charge a/c currency
					+ "'', '', '', '', '', 0.0, 0.0, 0.0, '', '', '', '', 0, '1', " //-------100 fields
					+ "'', '', '', '', '', '', '', '', '', "
					+ "'" + RecCCY + "', " //Instructed currency
					+ RecAmountDB //Instructed amount
					+ ", 1.0, " //Instructed amount exchange rate
					+ "'', '', '', '', '', '', '', '', '', "
					+ "0, '', 0, '', 0, '', 0, '', 0, "
					+ "'', 0, '', 0, '', '', '', '', '', "
					+ "'', '', '', '', '', "
					+ "'1', '" + OrderAccountAC + "', "  //Inward tfr, Ordering Customer Account Number 
					+ "'', '', '', '', '', '', '', '', '', '', "
					+ "'', '', '', '', '', '', '', '', '', '', "
					+ "'', '', '', '', '', '', '', '', '', '', "
					+ "'', '', '', '', '', '', '', '', 0 )");
				
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