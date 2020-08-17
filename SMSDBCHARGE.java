import java.sql.*;
import com.ibm.as400.access.*;

public class SMSDBCHARGE {
	
	private Connection connection = null;
	
	public static void main(String[] args) {
		
		SMSDBCHARGE test = new SMSDBCHARGE();
		test.runQuery(args[0]);	
		test.cleanup();
	}

	public SMSDBCHARGE() {
			
		try {		
			DriverManager.registerDriver(new AS400JDBCDriver());
			connection = DriverManager.getConnection("jdbc:as400://10.10.201.234");
		} 
		catch (Exception e) {
			System.out.println("Caught exception: " + e.getMessage());
		}
	}

	public void runQuery( String ValueDate ) {
		
		String SCAB = "", SCAN = "", SCAS = "", SCAIA6 = "";
		String AcNumber = "", currency = "", currencycode = "", BranchName = "";
		double Charge = 0.0, AvailableBalance = 0.0, SCBAL = 0.0, SCSUMC = 0.0, SCSUMD = 0.0, SCSUMA = 0.0, SCSUM1 = 0.0, SCSUM2 = 0.0, SCSUML = 0.0, SCRBA = 0.0, SCAODL = 0.0;
		int count, movement = 0;
		try {
			Statement s = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
			Statement s1 = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
			Statement s2 = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
			
			ResultSet rs = s.executeQuery("select CAST(SCAN AS CHAR(6) CCSID 37) as SCAN from kfilm00.scpf where SCAI95 = 'Y' order by SCAN asc");
			ResultSet rs1;
			
			int i = 1;
			
			while (rs.next()) {
				
				if( !rs.getString(1).equals(AcNumber) ){
					SCAN = rs.getString(1);
					rs1 = s2.executeQuery("select CAST(SCAB AS CHAR(4) CCSID 37) as SCAB, CAST(SCAS AS CHAR(3) CCSID 37) as SCAS, CAST(SCCCY AS CHAR(3) CCSID 37) as SCCCY, CAST(CABRNM AS CHAR(4) CCSID 37) as CABRNM, CAST(C8CCYN AS CHAR(3) CCSID 37) as C8CCYN from kfilm00.scpf, kfilm00.capf, kfilm00.c8pf, kfilm00.gfpf where SCAN = '" + SCAN + "' and SCAN = GFCPNC and SCAI95 = 'Y' and SCAI17='N' and SCAI20='N' and SCAI30='N' and SCAB = CABBN and SCCCY = C8CCY and gfctp != 'PD' and gfctp != 'PF' and gfctp != 'PG' and gfctp != 'CX' and gfctp != 'PH' and scact != 'LK' and scact != 'BE' and scact != 'EB'");
					count = 0;
					movement = 0;
					while(rs1.next()){
						SCAB = rs1.getString(1);
						SCAS = rs1.getString(2);
						currency = rs1.getString(3);
						BranchName = rs1.getString(4);
						currencycode = rs1.getString(5);
						count++;
					}
					if( count == 1 ){
						if( currency.equals("LBP"))
							Charge = 150000;
						else {
							currency = "USD";
							currencycode = "840";
							Charge = 100;
						}
						s1.executeUpdate("insert into ambfil.xftep3 values('" + SCAB + "', '" + SCAN + "', '" + SCAS + "', '', '', " + i + ", 'UTFJ', '" + currency + "', '" + BranchName + "', '47', " + Charge + ", " + ValueDate + ", '', '', '', '', '', '910123', '947', '473', '872700', '900', '400', '" + currencycode + "')");
						i++;
					}
					else if( count > 1 ){
						rs1 = s2.executeQuery("select CAST(SCAB AS CHAR(4) CCSID 37) as SCAB, CAST(SCAS AS CHAR(3) CCSID 37) as SCAS, CAST(SCCCY AS CHAR(3) CCSID 37) as SCCCY, CAST(CABRNM AS CHAR(4) CCSID 37) as CABRNM, CAST(C8CCYN AS CHAR(3) CCSID 37) as C8CCYN, SCBAL, SCSUMC, SCSUMD, SCSUMA, SCSUM1, SCSUM2, SCSUML, SCRBA, SCAODL, SCAIA6 from kfilm00.scpf, kfilm00.capf, kfilm00.c8pf, kfilm00.gfpf where SCAN = '" + SCAN + "' and SCAN = GFCPNC and SCAB = CABBN and SCCCY = C8CCY and gfctp != 'PD' and gfctp != 'PF' and gfctp != 'PG' and gfctp != 'CX' and gfctp != 'PH' and (SCACT='CA' or SCACT='CC' or SCACT='CE' or SCACT='CF' or SCACT='CG' or scact='LA' or scact='LG') and SCAI17='N' and SCAI20='N' and SCAI30='N' order by SCAB, SCAN, SCAS asc");
						while(rs1.next()){
							SCAB = rs1.getString(1);
							SCAS = rs1.getString(2);
							currency = rs1.getString(3);
							BranchName = rs1.getString(4);
							currencycode = rs1.getString(5);
							SCBAL = Double.parseDouble(rs1.getString(6));
							SCSUMC = Double.parseDouble(rs1.getString(7));
							SCSUMD = Double.parseDouble(rs1.getString(8));
							SCSUMA = Double.parseDouble(rs1.getString(9));
							SCSUM1 = Double.parseDouble(rs1.getString(10));
							SCSUM2 = Double.parseDouble(rs1.getString(11));
							SCSUML = Double.parseDouble(rs1.getString(12));
							SCRBA = Double.parseDouble(rs1.getString(13));
							SCAODL = Double.parseDouble(rs1.getString(14));
							SCAIA6 = rs1.getString(15);
							AvailableBalance = SCBAL + SCSUMC + SCSUMD + SCSUMA - SCSUM1 - SCSUM2 - SCSUML - SCRBA - SCAODL;
							System.out.print( SCAB + " " + SCAN + " " + SCAS + " " );
							System.out.printf( "%.2f\n", AvailableBalance / 100 );
							if( currency.equals("LBP")){
								Charge = 300000;
								if( AvailableBalance < Charge )
									continue;
							}
							else {
								currency = "USD";
								currencycode = "840";
								Charge = 200;
								if( AvailableBalance < Charge )
									continue;
							}
							s1.executeUpdate("insert into ambfil.xftep3 values('" + SCAB + "', '" + SCAN + "', '" + SCAS + "', '', '', " + i + ", 'UTFJ', '" + currency + "', '" + BranchName + "', '47', " + Charge + ", " + ValueDate + ", '', '', '', '', '', '910123', '947', '473', '872700', '900', '400', '" + currencycode + "')");
							i++;
							movement = 1;
							break;
						}
						if ( movement == 0 ) {
							rs1 = s2.executeQuery("select CAST(SCAB AS CHAR(4) CCSID 37) as SCAB, CAST(SCAS AS CHAR(3) CCSID 37) as SCAS, CAST(SCCCY AS CHAR(3) CCSID 37) as SCCCY, CAST(CABRNM AS CHAR(4) CCSID 37) as CABRNM, CAST(C8CCYN AS CHAR(3) CCSID 37) as C8CCYN, SCBAL, SCSUMC, SCSUMD, SCSUMA, SCSUM1, SCSUM2, SCSUML, SCRBA, SCAODL, SCAIA6 from kfilm00.scpf, kfilm00.capf, kfilm00.c8pf, kfilm00.gfpf where SCAN = '" + SCAN + "' and SCAN = GFCPNC and SCAB = CABBN and SCCCY = C8CCY and gfctp != 'PD' and gfctp != 'PF' and gfctp != 'PG' and gfctp != 'CX' and gfctp != 'PH' and (SCACT ='CH' or SCACT='EA' or SCACT='ED' or SCACT='EF' or SCACT='EW') and SCAI17='N' and SCAI20='N' and SCAI30='N' order by SCAB, SCAN, SCAS asc");
							while(rs1.next()){
								SCAB = rs1.getString(1);
								SCAS = rs1.getString(2);
								currency = rs1.getString(3);
								BranchName = rs1.getString(4);
								currencycode = rs1.getString(5);
								SCBAL = Double.parseDouble(rs1.getString(6));
								SCSUMC = Double.parseDouble(rs1.getString(7));
								SCSUMD = Double.parseDouble(rs1.getString(8));
								SCSUMA = Double.parseDouble(rs1.getString(9));
								SCSUM1 = Double.parseDouble(rs1.getString(10));
								SCSUM2 = Double.parseDouble(rs1.getString(11));
								SCSUML = Double.parseDouble(rs1.getString(12));
								SCRBA = Double.parseDouble(rs1.getString(13));
								SCAODL = Double.parseDouble(rs1.getString(14));
								SCAIA6 = rs1.getString(15);
								AvailableBalance = SCBAL + SCSUMC + SCSUMD + SCSUMA - SCSUM1 - SCSUM2 - SCSUML - SCRBA - SCAODL;
								System.out.print( SCAB + " " + SCAN + " " + SCAS + " " );
								System.out.printf( "%.2f\n", AvailableBalance / 100 );
								if( currency.equals("LBP")){
									Charge = 300000;
									if( AvailableBalance < Charge && SCAIA6.equals("Y") )
										continue;
								}
								else {
									currency = "USD";
									currencycode = "840";							
									Charge = 200;
									if( AvailableBalance < Charge && SCAIA6.equals("Y") )
										continue;
								}
								s1.executeUpdate("insert into ambfil.xftep3 values('" + SCAB + "', '" + SCAN + "', '" + SCAS + "', '', '', " + i + ", 'UTFJ', '" + currency + "', '" + BranchName + "', '47', " + Charge + ", " + ValueDate + ", '', '', '', '', '', '910123', '947', '473', '872700', '900', '400', '" + currencycode + "')");
								i++;
								movement = 1;
								break;
							}
						}
						if ( movement == 0 ){
							rs1 = s2.executeQuery("select CAST(SCAB AS CHAR(4) CCSID 37) as SCAB, CAST(SCAS AS CHAR(3) CCSID 37) as SCAS, CAST(SCCCY AS CHAR(3) CCSID 37) as SCCCY, CAST(CABRNM AS CHAR(4) CCSID 37) as CABRNM, CAST(C8CCYN AS CHAR(3) CCSID 37) as C8CCYN, SCBAL, SCSUMC, SCSUMD, SCSUMA, SCSUM1, SCSUM2, SCSUML, SCRBA, SCAODL, SCAIA6 from kfilm00.scpf, kfilm00.capf, kfilm00.c8pf, kfilm00.gfpf where SCAN = '" + SCAN + "' and SCAN = GFCPNC and SCAB = CABBN and SCCCY = C8CCY and gfctp != 'PD' and gfctp != 'PF' and gfctp != 'PG' and gfctp != 'CX' and gfctp != 'PH' and (SCACT='CA' or SCACT='CC' or SCACT='CE' or SCACT='CF' or SCACT='CG' or scact='LA' or scact='LG') and SCAI17='N' and SCAI20='N' and SCAI30='N' order by SCAB, SCAN, SCAS asc");
							while(rs1.next()){
								SCAB = rs1.getString(1);
								SCAS = rs1.getString(2);
								currency = rs1.getString(3);
								BranchName = rs1.getString(4);
								currencycode = rs1.getString(5);
								SCBAL = Double.parseDouble(rs1.getString(6));
								SCSUMC = Double.parseDouble(rs1.getString(7));
								SCSUMD = Double.parseDouble(rs1.getString(8));
								SCSUMA = Double.parseDouble(rs1.getString(9));
								SCSUM1 = Double.parseDouble(rs1.getString(10));
								SCSUM2 = Double.parseDouble(rs1.getString(11));
								SCSUML = Double.parseDouble(rs1.getString(12));
								SCRBA = Double.parseDouble(rs1.getString(13));
								SCAODL = Double.parseDouble(rs1.getString(14));
								SCAIA6 = rs1.getString(15);
								AvailableBalance = SCBAL + SCSUMC + SCSUMD + SCSUMA - SCSUM1 - SCSUM2 - SCSUML - SCRBA - SCAODL;
								System.out.print( SCAB + " " + SCAN + " " + SCAS + " " );
								System.out.printf( "%.2f\n", AvailableBalance / 100 );
								if( currency.equals("LBP")){
									Charge = 300000;
									if( AvailableBalance < Charge && SCAIA6.equals("Y") )
										continue;
								}
								else {
									currency = "USD";
									currencycode = "840";
									Charge = 200;
									if( AvailableBalance < Charge && SCAIA6.equals("Y") )
										continue;
								}
								s1.executeUpdate("insert into ambfil.xftep3 values('" + SCAB + "', '" + SCAN + "', '" + SCAS + "', '', '', " + i + ", 'UTFJ', '" + currency + "', '" + BranchName + "', '47', " + Charge + ", " + ValueDate + ", '', '', '', '', '', '910123', '947', '473', '872700', '900', '400', '" + currencycode + "')");
								i++;
								movement = 1;
								break;
							}
						}
					}
				}
				AcNumber = rs.getString(1);
			}
			
			s.close();
			s1.close();
			s2.close();
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