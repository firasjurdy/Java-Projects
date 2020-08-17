import java.sql.*;
import com.ibm.as400.access.*;
import java.io.*;
import java.text.*;
import java.util.Date;

public class SANAD {
	
	private Connection connection = null;
	
	public static void main(String[] args) {
		
		SANAD test = new SANAD();	
		test.runQuery( args[ 0 ], args[ 1 ], args[ 2 ] );	
		test.cleanup();
	}

	public SANAD() {
			
		try {		
			DriverManager.registerDriver(new AS400JDBCDriver());
			connection = DriverManager.getConnection("jdbc:as400://10.10.201.234");
		} 
		catch (Exception e) {
			System.out.println("Caught exception: " + e.getMessage());
		}
	}

	public void runQuery( String Branch, String Type, String Refn ) {
		
		try {
			String Customer = "", FirstRepaymentDate = "", LastRepaymentDate = "", DealTypeDescription = "";
			String Obsolete = "", Address1 = "", Address2 = "", Address3 = "", Address4 = "", Address5 = "", DealTypeCurrency = ""; 
			String Tel = "", DealAccountSuffix = "", DealAccountBranch = "", DealAccountNumber = "", temp = "", GWSQN = "";
			Double ConstantRepaymentAmount = 0.0, LastRepaymentAmount = 0.0, InterestRate = 0.0;
			Double GrossAmount = 0.0, RepaymentAmount = 0.0, RepaymentAmount1 = 0.0, RepaymentAmount2 = 0.0;
			int NumberOfPayments = 0;
			SimpleDateFormat dateformatter = new SimpleDateFormat ("dd MMMM yyyy");
			java.util.Date RL_StartDate = new java.util.Date();
			java.util.Date RL_EndDate = new java.util.Date();
			String testdate = "", RepaymentAmountstr = "", ConstantRepaymentAmountstr = "";
			DecimalFormat myFormatter = new DecimalFormat("###,###,###,###.##");
			String GrossAmountstr = "", ConstantRepaymentFlag = "", RepaymentDate = "", RepaymentDate1 = "", RepaymentDate2 = ""; 
			int OMPFSize = 0, count = 0;
			String [][] OMPF;
			String [] Principals;
			String [] Interest;
			String [] Dates;
			int i = 0, j = 0, k = 0;
			Statement s = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = s.executeQuery("select OSCUS from kfilm00.ospf where OSBRNM = '" + Branch + "' and OSDLP = '" + Type + "' and OSDLR = '" + Refn + "'");
			ResultSet rs1, rs2, rs3, rs5, rs6, rs7, rs8, rs9, rs10, rs11, rs12;
			if(rs.next()){
				Customer = rs.getString(1);
				rs1 = s.executeQuery("select SXSEQ from kfilm00.sxpf where SXCUS = '" + Customer + "'"); 
				if(rs1.next()){
					Obsolete = rs1.getString(1);
					rs2 = s.executeQuery("select SVNA1, SVNA2, SVNA3, SVNA4, SVNA5 from kfilm00.svpf where SVSEQ = " + Obsolete); 
					if(rs2.next()){
						Address1 = rs2.getString(1);
						Address2 = rs2.getString(2);
						Address3 = rs2.getString(3);
						Address4 = rs2.getString(4);
						Address5 = rs2.getString(5);
						rs3 = s.executeQuery("select AAZMPHN from kfilm00.aazpf where AAZCUS = '" + Customer + "'");
				 		if(rs3.next()){
				 			Tel = rs3.getString(1);
				 		}
					}			
				}
					
				rs5 = s.executeQuery("select IZNDT, IZCRA, IZCRAL, IZNPY, IZMDT, IZRAT, IZYCRL from kfilm00.izpf where IZBRNM = '" + Branch + "' and IZDLP = '" + Type + "' and IZDLR = '" + Refn + "'");
				if(rs5.next()){	
					FirstRepaymentDate = rs5.getString(1);
					testdate = "20" + FirstRepaymentDate.substring(1,3) + "/" + FirstRepaymentDate.substring(3,5) + "/" + FirstRepaymentDate.substring(5,7);
					RL_StartDate = new Date(testdate);
					ConstantRepaymentAmount = Double.parseDouble(rs5.getString(2))/100;
					LastRepaymentAmount = Double.parseDouble(rs5.getString(3))/100;
					NumberOfPayments = Integer.parseInt(rs5.getString(4));
					LastRepaymentDate = rs5.getString(5);
					testdate = "20" + LastRepaymentDate.substring(1,3) + "/" + LastRepaymentDate.substring(3,5) + "/" + LastRepaymentDate.substring(5,7);
					RL_EndDate = new Date(testdate);
					InterestRate = Double.parseDouble(rs5.getString(6));
					ConstantRepaymentFlag = rs5.getString(7);
					GrossAmount = ConstantRepaymentAmount * (NumberOfPayments - 1) + LastRepaymentAmount;
					ConstantRepaymentAmountstr = myFormatter.format(ConstantRepaymentAmount);
					GrossAmountstr = myFormatter.format(GrossAmount);
				}
				rs6 = s.executeQuery("select V5ABD, V5AND, V5ASD from kfilm00.v5pf where V5BRNM = '" + Branch + "' and V5DLP = '" + Type + "' and V5DLR = '" + Refn + "'");
				if(rs6.next()){
					DealAccountBranch = rs6.getString(1);
					DealAccountNumber = rs6.getString(2);
					DealAccountSuffix = rs6.getString(3);
				}
				rs7 = s.executeQuery("select OBDPD from kfilm00.obpf where OBDLP = '" + Type + "'");
				if(rs7.next()){
					DealTypeDescription = rs7.getString(1);
				}
				rs8 = s.executeQuery("select R8CCY from kfilm00.r8pf where R8LNP = '" + Type + "'");
				if(rs8.next()){
					DealTypeCurrency = rs8.getString(1);
				}
			}
			
			rs10 = s.executeQuery("select GWSQN from kfilm00.GWR85P where GWBRNM = '" + Branch + "' and GWLNP = '" + Type + "' and GWLNR = '" + Refn + "' order by GWSQN desc");
			if( rs10.next()){
				GWSQN = rs10.getString(1);
			}
			rs9 = s.executeQuery("select GWEDTE, GWPRA, GWIRA from kfilm00.GWR85P where GWBRNM = '" + Branch + "' and GWLNP = '" + Type + "' and GWLNR = '" + Refn + "' and GWSQN = " + GWSQN );
			
			temp = "";
			count = 1;
			i = 0;
			if( ConstantRepaymentFlag.equals("N") )
				NumberOfPayments++;
			Principals = new String [NumberOfPayments];
			Dates = new String[NumberOfPayments];
			Interest = new String [NumberOfPayments];
			
			while( rs9.next() ){
				RepaymentDate1 = rs9.getString(1);
				
				if( !RepaymentDate1.equals(temp) ){
					Dates[ i ] = RepaymentDate1;
					Principals[ i ] = rs9.getString(2);
					Interest[ i ] = rs9.getString(3);
				}
				else {
					i--;
					Principals[ i ] = rs9.getString(2);
					Interest[ i ] = rs9.getString(3);
				}
				
				i++;
				temp = RepaymentDate1;
				
			}
			
			GrossAmount = 0.0;
			for( i = 0; i < NumberOfPayments; i++ ){
				RepaymentAmount1 = Double.parseDouble(Interest[ i ])/100;
				RepaymentAmount2 = Double.parseDouble(Principals[ i ])/100;
				GrossAmount += RepaymentAmount1 + RepaymentAmount2;
			}
			GrossAmountstr = myFormatter.format(GrossAmount);
			System.out.printf("\n\n\n\n\n%60s %s\n", GrossAmountstr, DealTypeCurrency);
			System.out.print("\n\n\n");	
			System.out.printf("%41s\n", "|");
			System.out.printf("%41s\n", "|");
			
			for( i = 0; i < NumberOfPayments; i++ ){
				if( count == 13 || count == 37 || count == 61 ){
					System.out.printf("%72s\n", "_______________________________|_______________________________");
					count += 12;
				}
				if( count > NumberOfPayments )
					break;	
				if( count + 11 < NumberOfPayments ){	
					RepaymentDate1 = Dates[ count + 11 ];
					RepaymentDate = RepaymentDate1.substring(5,7) + "/" + RepaymentDate1.substring(3,5) + "/" + "20" + RepaymentDate1.substring(1,3);			
					RepaymentAmount1 = Double.parseDouble(Interest[ count + 11 ])/100;
					RepaymentAmount2 = Double.parseDouble(Principals[ count + 11 ])/100;
					RepaymentAmount = RepaymentAmount1 + RepaymentAmount2;
					RepaymentAmountstr = myFormatter.format(RepaymentAmount);
					
					System.out.printf("%19s%14s%3d/%d", RepaymentDate, RepaymentAmountstr, count + 12, NumberOfPayments);
						
					RepaymentDate1 = Dates[ count - 1 ];
					RepaymentDate = RepaymentDate1.substring(5,7) + "/" + RepaymentDate1.substring(3,5) + "/" + "20" + RepaymentDate1.substring(1,3);			
					RepaymentAmount1 = Double.parseDouble(Interest[ count - 1 ])/100;
					RepaymentAmount2 = Double.parseDouble(Principals[ count - 1 ])/100;
					RepaymentAmount = RepaymentAmount1 + RepaymentAmount2;
					RepaymentAmountstr = myFormatter.format(RepaymentAmount);
								
					System.out.printf("%2s%11s%14s%3d/%d\n", "|", RepaymentDate, RepaymentAmountstr, count, NumberOfPayments);
				
					count++;							
				}
				else {
					RepaymentDate1 = Dates[ count - 1 ];
					RepaymentDate = RepaymentDate1.substring(5,7) + "/" + RepaymentDate1.substring(3,5) + "/" + "20" + RepaymentDate1.substring(1,3);			
					RepaymentAmount1 = Double.parseDouble(Interest[ count - 1 ])/100;
					RepaymentAmount2 = Double.parseDouble(Principals[ count - 1 ])/100;
					RepaymentAmount = RepaymentAmount1 + RepaymentAmount2;
					RepaymentAmountstr = myFormatter.format(RepaymentAmount);
							
					System.out.printf("%41s%11s%14s%3d/%d\n", "|", RepaymentDate, RepaymentAmountstr, count, NumberOfPayments);
			
					count++;
				}
			}
				
			s.close();
		} 
		catch (SQLException e) {
			System.out.println("SQLException exception: ");
			System.out.println("Message:....." + e.getMessage());
			System.out.println("SQLState:...." + e.getSQLState());
			System.out.println("Vendor Code:." + e.getErrorCode());
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