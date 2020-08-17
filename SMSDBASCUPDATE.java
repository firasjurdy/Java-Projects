import java.sql.*;
import com.ibm.as400.access.*;
import java.io.*;
import java.util.*;

public class SMSDBASCUPDATE {
	
	private Connection connection = null;
	
	public static int SeqNum = 1;
	public static String [] ASC = {"SCAI10","SCAI11","SCAI12","SCAI13","SCAI14","SCAI15","SCAI16","SCAI17","SCAI20","SCAI21","S5II22","S5II23","SCAI24","SCAI25","SCAI26","S5II27","SCAI30","SCAI31","SCAI32","SCAI33","S5II34","SCAI35","SCAI36","SCAI37","","","","","","","SCAI46","SCAI47","S5II50","","","S5II53","SCAI54","","","","SCAI60","SCAI61","SCAI62","SCAI63","SCAI64","SCAI65","SCAI66","SCAI67","","SCAI71","SCAI72","SCAI73","","","","","SCAI80","SCAI81","SCAI82","SCAI83","SCAI84","SCAI85","SCAI86","SCAI87","SCAI90","SCAI91","SCAI92","SCAI93","SCAI94","SCAI95","SCAI96","SCAI97","SCAIA0","SCAIA1","SCAIA2","SCAIA3","S5IIA4","SCAIA5","SCAIA6", "", "SCAIB0","SCAIB1","SCAIB2","SCAIB3","SCAIB4","","","","","","","","SCAIC4","SCAIC5","","SCAIC7","S5IID0","","SCAID2","SCAID3","SCAID4","SCAID5","SCAID6","SCAID7","SCAIE0","SCAIE1","","","SCAIE4","","SCAIE6","","SCAIF0","","SCAIF2","S5IIF3","","SCAIF5","SCAIF6","SCAIF7","SCAIG0","SCAIG1","SCAIG2","SCAIG3","SCAIG4","SCAIG5","SCAIG6","SCAIG7","S5IIH0","SCAIH1","SCAIH2","SCAIH3","SCAIH4","SCAIH5","SCAIH6","","","","","SCAIB7","SCAII4","SCAII5","SCAII6","SCAII7","SCAIJ0","SCAIJ1","SCAIJ2","SCAIJ3","SCAIJ4","SCAIJ5","SCAIJ6","SCAIJ7","SCAIK0","SCAIK1","SCAIK2","SCAIK3","SCAIK4","SCAIK5","SCAIK6","SCAIK7","SCAIL0","SCAIL1","SCAIL2","SCAIL3","SCAIL4","SCAIL5","SCAIL6","SCAIL7","SCAIM0","SCAIM1","SCAIM2","SCAIM3","SCAIM4","SCAIM5","SCAIM6","SCAIM7","SCAIN0","SCAIN1","SCAIN2","SCAIN3","SCAIN4","SCAIN5","SCAIN6","SCAIN7"};
	public static String [] ASCNUM = {"10","11","12","13","14","15","16","17","20","21","22","23","24","25","26","27","30","31","32","33","34","35","36","37","40","41","42","43","44","45","46","47","50","51","52","53","54","55","56","57","60","61","62","63","64","65","66","67","70","71","72","73","74","75","76","77","80","81","82","83","84","85","86","87","90","91","92","93","94","95","96","97","100","101","102","103","104","105","106","107","110","111","112","113","114","115","116","183","120","121","122","123","124","125","126","127","130","131","132","133","134","135","136","137","140","141","142","143","144","145","146","147","150","151","152","153","154","155","156","157","160","161","162","163","164","165","166","167","170","171","172","173","174","175","176","177","180","181","182","117","184","185","186","187","190","191","192","193","194","195","196","197","200","201","202","203","204","205","206","207","210","211","212","213","214","215","216","217","220","221","222","223","224","225","226","227","230","231","232","233","234","235","236","237"};
	public String [] Mask = new String[ 184 ];
	
	public static void main(String[] args) {
		
		SMSDBASCUPDATE test = new SMSDBASCUPDATE();
		System.out.println("ASCUPDATE Check.");
		test.runQuery( args[ 0 ], args[ 1 ], args[ 2 ], args[ 3 ], args[ 4 ] );	
		test.cleanup();
	}

	public SMSDBASCUPDATE() {
			
		try {		
			DriverManager.registerDriver(new AS400JDBCDriver());
			connection = DriverManager.getConnection("jdbc:as400://10.10.201.234");
		} 
		catch (Exception e) {
			System.out.println("Caught exception: " + e.getMessage());
		}
	}

	public void runQuery( String WID, String unit, String cond, String flag, String EXTJRN ) {
		
		String Line = "";
		
		try {
			FileInputStream fstream = new FileInputStream("input.txt");
    		DataInputStream inFile = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(inFile));
			
			FileWriter outFile = new FileWriter("log.txt");
            PrintWriter out = new PrintWriter(outFile);   
			
			Statement s = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
			ResultSet rs;
			
			Line = br.readLine();
			while( Line != null ){
				rs = s.executeQuery("select CAST(SCAB AS CHAR(4) CCSID 37) as SCAB, CAST(SCAN AS CHAR(6) CCSID 37) as SCAN, CAST(SCAS AS CHAR(3) CCSID 37) as SCAS, CAST(SCAI17 AS CHAR(1) CCSID 37) as SCAI17, CAST(SCAI30 AS CHAR(1) CCSID 37) as SCAI30 from kfil" + unit + ".scpf where SCACT = '" + Line + "'");
				out.print( "Account Type: " + Line + "\r\n" );
				while( rs.next() ){
					out.print( rs.getString(1) + " " + rs.getString(2) + " " + rs.getString(3) + "\r\n" );
					if( !(rs.getString(4).equals("Y") || rs.getString(5).equals("Y")) ){
						SpecialConditions( WID, rs.getString(1), rs.getString(2), rs.getString(3), unit, cond, flag, EXTJRN, out );
						SeqNum++;
					}
				}
				Line = br.readLine();
			}
			out.close();
			s.close();
		}
		catch (IOException e){
			System.out.println("Message:....." + e.getMessage());
            e.printStackTrace();
        }
		catch (Exception e) {
			System.out.println("Exception exception: ");
			System.out.println("Message:....." + e.getMessage());
			e.printStackTrace();
		}
		
	}

	public void SpecialConditions( String WID, String branch, String id, String suffix, String unit, String cond, String flag, String EXTJRN, PrintWriter out ) {
		
		try {
			int j, index = -1;
			for( j = 0; j < ASCNUM.length; j++ )
				if( ASCNUM[ j ].equals(cond) ){
					index = j;
					break;
				}
			if( index != -1 ){
				Statement s = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
				Statement s1 = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
				ResultSet rs = s.executeQuery("select CAST(SCAI10 AS CHAR(1) CCSID 37) as SCAI10, " +
													 "CAST(SCAI11 AS CHAR(1) CCSID 37) as SCAI11, " +
													 "CAST(SCAI12 AS CHAR(1) CCSID 37) as SCAI12, " +
													 "CAST(SCAI13 AS CHAR(1) CCSID 37) as SCAI13, " +
													 "CAST(SCAI14 AS CHAR(1) CCSID 37) as SCAI14, " +												 
													 "CAST(SCAI15 AS CHAR(1) CCSID 37) as SCAI15, " +
													 "CAST(SCAI16 AS CHAR(1) CCSID 37) as SCAI16, " +
													 "CAST(SCAI17 AS CHAR(1) CCSID 37) as SCAI17, " +
													 "CAST(SCAI20 AS CHAR(1) CCSID 37) as SCAI20, " +
													 "CAST(SCAI21 AS CHAR(1) CCSID 37) as SCAI21, " +
													 "CAST(SCAI24 AS CHAR(1) CCSID 37) as SCAI24, " +
													 "CAST(SCAI25 AS CHAR(1) CCSID 37) as SCAI25, " +
													 "CAST(SCAI26 AS CHAR(1) CCSID 37) as SCAI26, " +
													 "CAST(SCAI30 AS CHAR(1) CCSID 37) as SCAI30, " +
													 "CAST(SCAI31 AS CHAR(1) CCSID 37) as SCAI31, " +
													 "CAST(SCAI32 AS CHAR(1) CCSID 37) as SCAI32, " +
													 "CAST(SCAI33 AS CHAR(1) CCSID 37) as SCAI33, " +
													 "CAST(SCAI35 AS CHAR(1) CCSID 37) as SCAI35, " +
													 "CAST(SCAI36 AS CHAR(1) CCSID 37) as SCAI36, " +
													 "CAST(SCAI37 AS CHAR(1) CCSID 37) as SCAI37, " +
													 "CAST(SCAI46 AS CHAR(1) CCSID 37) as SCAI46, " +
													 "CAST(SCAI47 AS CHAR(1) CCSID 37) as SCAI47, " +
													 "CAST(SCAI54 AS CHAR(1) CCSID 37) as SCAI54, " +
													 "CAST(SCAI60 AS CHAR(1) CCSID 37) as SCAI60, " +
													 "CAST(SCAI61 AS CHAR(1) CCSID 37) as SCAI61, " +
													 "CAST(SCAI62 AS CHAR(1) CCSID 37) as SCAI62, " +
													 "CAST(SCAI63 AS CHAR(1) CCSID 37) as SCAI63, " +
													 "CAST(SCAI64 AS CHAR(1) CCSID 37) as SCAI64, " +
													 "CAST(SCAI65 AS CHAR(1) CCSID 37) as SCAI65, " +
													 "CAST(SCAI66 AS CHAR(1) CCSID 37) as SCAI66, " +
													 "CAST(SCAI67 AS CHAR(1) CCSID 37) as SCAI67, " +
													 "CAST(SCAI71 AS CHAR(1) CCSID 37) as SCAI71, " +
													 "CAST(SCAI72 AS CHAR(1) CCSID 37) as SCAI72, " +
													 "CAST(SCAI73 AS CHAR(1) CCSID 37) as SCAI73, " +
													 "CAST(SCAI80 AS CHAR(1) CCSID 37) as SCAI80, " +
													 "CAST(SCAI81 AS CHAR(1) CCSID 37) as SCAI81, " +
													 "CAST(SCAI82 AS CHAR(1) CCSID 37) as SCAI82, " +
													 "CAST(SCAI83 AS CHAR(1) CCSID 37) as SCAI83, " +
													 "CAST(SCAI84 AS CHAR(1) CCSID 37) as SCAI84, " +
													 "CAST(SCAI85 AS CHAR(1) CCSID 37) as SCAI85, " +
													 "CAST(SCAI86 AS CHAR(1) CCSID 37) as SCAI86, " +
													 "CAST(SCAI87 AS CHAR(1) CCSID 37) as SCAI87, " +
													 "CAST(SCAI90 AS CHAR(1) CCSID 37) as SCAI90, " +
													 "CAST(SCAI91 AS CHAR(1) CCSID 37) as SCAI91, " +
													 "CAST(SCAI92 AS CHAR(1) CCSID 37) as SCAI92, " +
													 "CAST(SCAI93 AS CHAR(1) CCSID 37) as SCAI93, " +
													 "CAST(SCAI94 AS CHAR(1) CCSID 37) as SCAI94, " +
													 "CAST(SCAI95 AS CHAR(1) CCSID 37) as SCAI95, " +
													 "CAST(SCAI96 AS CHAR(1) CCSID 37) as SCAI96, " +
													 "CAST(SCAI97 AS CHAR(1) CCSID 37) as SCAI97, " +
													 "CAST(SCAIA0 AS CHAR(1) CCSID 37) as SCAIA0, " +
													 "CAST(SCAIA1 AS CHAR(1) CCSID 37) as SCAIA1, " +
													 "CAST(SCAIA2 AS CHAR(1) CCSID 37) as SCAIA2, " +
													 "CAST(SCAIA3 AS CHAR(1) CCSID 37) as SCAIA3, " +
													 "CAST(SCAIA5 AS CHAR(1) CCSID 37) as SCAIA5, " +
													 "CAST(SCAIA6 AS CHAR(1) CCSID 37) as SCAIA6, " +
													 "CAST(SCAIB0 AS CHAR(1) CCSID 37) as SCAIB0, " +
													 "CAST(SCAIB1 AS CHAR(1) CCSID 37) as SCAIB1, " +
													 "CAST(SCAIB2 AS CHAR(1) CCSID 37) as SCAIB2, " +
													 "CAST(SCAIB3 AS CHAR(1) CCSID 37) as SCAIB3, " +
													 "CAST(SCAIB4 AS CHAR(1) CCSID 37) as SCAIB4, " +
													 "CAST(SCAIC4 AS CHAR(1) CCSID 37) as SCAIC4, " +
													 "CAST(SCAIC5 AS CHAR(1) CCSID 37) as SCAIC5, " +
													 "CAST(SCAIC7 AS CHAR(1) CCSID 37) as SCAIC7, " +
													 "CAST(SCAID2 AS CHAR(1) CCSID 37) as SCAID2, " +
													 "CAST(SCAID3 AS CHAR(1) CCSID 37) as SCAID3, " +
													 "CAST(SCAID4 AS CHAR(1) CCSID 37) as SCAID4, " +
													 "CAST(SCAID5 AS CHAR(1) CCSID 37) as SCAID5, " +
													 "CAST(SCAID6 AS CHAR(1) CCSID 37) as SCAID6, " +
													 "CAST(SCAID7 AS CHAR(1) CCSID 37) as SCAID7, " +
													 "CAST(SCAIE0 AS CHAR(1) CCSID 37) as SCAIE0, " +
													 "CAST(SCAIE1 AS CHAR(1) CCSID 37) as SCAIE1, " +
													 "CAST(SCAIE4 AS CHAR(1) CCSID 37) as SCAIE4, " +
													 "CAST(SCAIE6 AS CHAR(1) CCSID 37) as SCAIE6, " +
													 "CAST(SCAIF0 AS CHAR(1) CCSID 37) as SCAIF0, " +
													 "CAST(SCAIF2 AS CHAR(1) CCSID 37) as SCAIF2, " +
													 "CAST(SCAIF5 AS CHAR(1) CCSID 37) as SCAIF5, " +
													 "CAST(SCAIF6 AS CHAR(1) CCSID 37) as SCAIF6, " +
													 "CAST(SCAIF7 AS CHAR(1) CCSID 37) as SCAIF7, " +
													 "CAST(SCAIG0 AS CHAR(1) CCSID 37) as SCAIG0, " +
													 "CAST(SCAIG1 AS CHAR(1) CCSID 37) as SCAIG1, " +
													 "CAST(SCAIG2 AS CHAR(1) CCSID 37) as SCAIG2, " +
													 "CAST(SCAIG3 AS CHAR(1) CCSID 37) as SCAIG3, " +
													 "CAST(SCAIG4 AS CHAR(1) CCSID 37) as SCAIG4, " +
													 "CAST(SCAIG5 AS CHAR(1) CCSID 37) as SCAIG5, " +
													 "CAST(SCAIG6 AS CHAR(1) CCSID 37) as SCAIG6, " +
													 "CAST(SCAIG7 AS CHAR(1) CCSID 37) as SCAIG7, " +
													 "CAST(SCAIH1 AS CHAR(1) CCSID 37) as SCAIH1, " +
													 "CAST(SCAIH2 AS CHAR(1) CCSID 37) as SCAIH2, " +
													 "CAST(SCAIH3 AS CHAR(1) CCSID 37) as SCAIH3, " +
													 "CAST(SCAIH4 AS CHAR(1) CCSID 37) as SCAIH4, " +
													 "CAST(SCAIH5 AS CHAR(1) CCSID 37) as SCAIH5, " +
													 "CAST(SCAIH6 AS CHAR(1) CCSID 37) as SCAIH6, " +
													 "CAST(SCAIB7 AS CHAR(1) CCSID 37) as SCAIB7, " +
													 "CAST(SCAII4 AS CHAR(1) CCSID 37) as SCAII4, " +
													 "CAST(SCAII5 AS CHAR(1) CCSID 37) as SCAII5, " +
													 "CAST(SCAII6 AS CHAR(1) CCSID 37) as SCAII6, " +
													 "CAST(SCAII7 AS CHAR(1) CCSID 37) as SCAII7, " +
													 "CAST(SCAIJ0 AS CHAR(1) CCSID 37) as SCAIJ0, " +
													 "CAST(SCAIJ1 AS CHAR(1) CCSID 37) as SCAIJ1, " +
													 "CAST(SCAIJ2 AS CHAR(1) CCSID 37) as SCAIJ2, " +
													 "CAST(SCAIJ3 AS CHAR(1) CCSID 37) as SCAIJ3, " +
													 "CAST(SCAIJ4 AS CHAR(1) CCSID 37) as SCAIJ4, " +
													 "CAST(SCAIJ5 AS CHAR(1) CCSID 37) as SCAIJ5, " +
													 "CAST(SCAIJ6 AS CHAR(1) CCSID 37) as SCAIJ6, " +
													 "CAST(SCAIJ7 AS CHAR(1) CCSID 37) as SCAIJ7, " +
													 "CAST(SCAIK0 AS CHAR(1) CCSID 37) as SCAIK0, " +
													 "CAST(SCAIK1 AS CHAR(1) CCSID 37) as SCAIK1, " +
													 "CAST(SCAIK2 AS CHAR(1) CCSID 37) as SCAIK2, " +
													 "CAST(SCAIK3 AS CHAR(1) CCSID 37) as SCAIK3, " +
													 "CAST(SCAIK4 AS CHAR(1) CCSID 37) as SCAIK4, " +
													 "CAST(SCAIK5 AS CHAR(1) CCSID 37) as SCAIK5, " +
													 "CAST(SCAIK6 AS CHAR(1) CCSID 37) as SCAIK6, " +
													 "CAST(SCAIK7 AS CHAR(1) CCSID 37) as SCAIK7, " +
													 "CAST(SCAIL0 AS CHAR(1) CCSID 37) as SCAIL0, " +
													 "CAST(SCAIL1 AS CHAR(1) CCSID 37) as SCAIL1, " +
													 "CAST(SCAIL2 AS CHAR(1) CCSID 37) as SCAIL2, " +
													 "CAST(SCAIL3 AS CHAR(1) CCSID 37) as SCAIL3, " +
													 "CAST(SCAIL4 AS CHAR(1) CCSID 37) as SCAIL4, " +
													 "CAST(SCAIL5 AS CHAR(1) CCSID 37) as SCAIL5, " +
													 "CAST(SCAIL6 AS CHAR(1) CCSID 37) as SCAIL6, " +
													 "CAST(SCAIL7 AS CHAR(1) CCSID 37) as SCAIL7, " +
													 "CAST(SCAIM0 AS CHAR(1) CCSID 37) as SCAIM0, " +
													 "CAST(SCAIM1 AS CHAR(1) CCSID 37) as SCAIM1, " +
													 "CAST(SCAIM2 AS CHAR(1) CCSID 37) as SCAIM2, " +
													 "CAST(SCAIM3 AS CHAR(1) CCSID 37) as SCAIM3, " +
													 "CAST(SCAIM4 AS CHAR(1) CCSID 37) as SCAIM4, " +
													 "CAST(SCAIM5 AS CHAR(1) CCSID 37) as SCAIM5, " +
													 "CAST(SCAIM6 AS CHAR(1) CCSID 37) as SCAIM6, " +
													 "CAST(SCAIM7 AS CHAR(1) CCSID 37) as SCAIM7, " +
													 "CAST(SCAIN0 AS CHAR(1) CCSID 37) as SCAIN0, " +
													 "CAST(SCAIN1 AS CHAR(1) CCSID 37) as SCAIN1, " +
													 "CAST(SCAIN2 AS CHAR(1) CCSID 37) as SCAIN2, " +
													 "CAST(SCAIN3 AS CHAR(1) CCSID 37) as SCAIN3, " +
													 "CAST(SCAIN4 AS CHAR(1) CCSID 37) as SCAIN4, " +
													 "CAST(SCAIN5 AS CHAR(1) CCSID 37) as SCAIN5, " +
													 "CAST(SCAIN6 AS CHAR(1) CCSID 37) as SCAIN6, " +
													 "CAST(SCAIN7 AS CHAR(1) CCSID 37) as SCAIN7 " +
													 "from kfil" + unit + ".scpf where scan = '" + id + "' and scab = '" + branch + "' and scas = '" + suffix + "'");
				
				ResultSet rs1 = s1.executeQuery("select CAST(S5II22 AS CHAR(1) CCSID 37) as S5II22, " +
													  "CAST(S5II23 AS CHAR(1) CCSID 37) as S5II23, " +
													  "CAST(S5II27 AS CHAR(1) CCSID 37) as S5II27, " +
													  "CAST(S5II34 AS CHAR(1) CCSID 37) as S5II34, " +
													  "CAST(S5II50 AS CHAR(1) CCSID 37) as S5II50, " +
													  "CAST(S5II53 AS CHAR(1) CCSID 37) as S5II53, " +
													  "CAST(S5IIA4 AS CHAR(1) CCSID 37) as S5IIA4, " +
													  "CAST(S5IID0 AS CHAR(1) CCSID 37) as S5IID0, " +
													  "CAST(S5IIF3 AS CHAR(1) CCSID 37) as S5IIF3, " +
													  "CAST(S5IIH0 AS CHAR(1) CCSID 37) as S5IIH0 " +
													  "from kfil" + unit + ".s5pf where s5an = '" + id + "' and s5ab = '" + branch + "' and s5as = '" + suffix + "'");
				int i;
				
				if( rs.next() ){
					if ( !rs.getString(rs.findColumn(ASC[index])).equals(flag)){
					
						if( rs1.next() ){
							for( i = 0; i < 184; i++ )
								Mask[ i ] = "";
							
							for( i = 0; i < ASC.length; i++ )
								if( ASC[ i ] == "" )
									Mask[ i ] = " ";
								else if( ASC[ i ].substring(0,2).equals("SC") ){
									Mask[ i ] = rs.getString(rs.findColumn(ASC[i]));
								}
								else if( ASC[ i ].substring(0,2).equals("S5") ){
									Mask[ i ] = rs1.getString(rs1.findColumn(ASC[i]));
								}
								
							UpdateJournal( WID, branch, id, suffix, unit, index, flag, EXTJRN, rs.getString(rs.findColumn("SCAI47")) );	
						}
						else {
							for( i = 0; i < 184; i++ )
								Mask[ i ] = "";
							
							for( i = 0; i < ASC.length; i++ )
								if( ASC[ i ] == "" )
									Mask[ i ] = " ";
								else if( ASC[ i ].substring(0,2).equals("SC") ){
									Mask[ i ] = rs.getString(rs.findColumn(ASC[i]));
								}
								else if( ASC[ i ].substring(0,2).equals("S5") ){
									Mask[ i ] = "N";
								}
								
							UpdateJournal( WID, branch, id, suffix, unit, index, flag, EXTJRN, rs.getString(rs.findColumn("SCAI47")) );
						}
					}
					else out.print(" Ignore Journal Update\r\n");
				}
				else out.print(" Account Not Found\r\n");
			
				s.close();
				s1.close();
			}
			else System.out.println("Special Condition not found");
		}
		catch (Exception e) {
			System.out.println("Exception exception: ");
			System.out.println("Message:....." + e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	public void UpdateJournal( String WID, String Branch, String Primary_Number, String Suffix, String unit, int index, String flag, String EXTJRN, String Internal ){
		try {
			Calendar calendar = Calendar.getInstance();
			int i;
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			int minute = calendar.get(Calendar.MINUTE);
			int second = calendar.get(Calendar.SECOND);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			String time = hour + "" + minute + "" + second;
		
			String SCMB = "", SCMA = "";
			
			for( i = 0; i < Mask.length; i++ )
				if( i == index )
					SCMB += "N";
				else SCMB += Mask[ i ];
			
			for( i = 0; i < Mask.length; i++ ) 
				if( i == index )
					SCMA += "Y";
				else SCMA += "N";
			
			Statement s = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
			if (Internal.equals("N")){
				if( flag.equals("Y") ){
					s.executeUpdate("insert into " + EXTJRN + ".GYPF values('" + WID + "', '', " + day + ", " + time + ", " + SeqNum + ", 'H35M', 'M', '', '', '', 'ASC', '', '', '', '', 0, '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', 0, 0, 0, '', '', '', 'Y', '', 0, 0, '', 0, 0, '', '', '', '', '')");
					s.executeUpdate("insert into " + EXTJRN + ".GZH351 values('" + WID + "', " + day + ", " + time + ", " + SeqNum + ", 'B', '" + Branch + "', '" + Primary_Number + "', '" + Suffix + "', 'Y', '" + SCMB + "', 'N', '')");	
					s.executeUpdate("insert into " + EXTJRN + ".GZH351 values('" + WID + "', " + day + ", " + time + ", " + SeqNum + ", 'A', '" + Branch + "', '" + Primary_Number + "', '" + Suffix + "', 'Y', '" + SCMA + "', 'N', '')");	
				}
				else {
					s.executeUpdate("insert into " + EXTJRN + ".GYPF values('" + WID + "', '', " + day + ", " + time + ", " + SeqNum + ", 'H35M', 'M', '', '', '', 'ASC', '', '', '', '', 0, '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', 0, 0, 0, '', '', '', 'Y', '', 0, 0, '', 0, 0, '', '', '', '', '')");
					s.executeUpdate("insert into " + EXTJRN + ".GZH351 values('" + WID + "', " + day + ", " + time + ", " + SeqNum + ", 'B', '" + Branch + "', '" + Primary_Number + "', '" + Suffix + "', 'N', '" + SCMB + "', 'N', '')");	
					s.executeUpdate("insert into " + EXTJRN + ".GZH351 values('" + WID + "', " + day + ", " + time + ", " + SeqNum + ", 'A', '" + Branch + "', '" + Primary_Number + "', '" + Suffix + "', 'N', '" + SCMA + "', 'N', '')");	
				}
			}
			else if (Internal.equals("Y")){
				if( flag.equals("Y") ){
					s.executeUpdate("insert into " + EXTJRN + ".GYPF values('" + WID + "', '', " + day + ", " + time + ", " + SeqNum + ", 'H35M', 'M', '', '', '', 'ISC', '', '', '', '', 0, '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', 0, 0, 0, '', '', '', 'Y', '', 0, 0, '', 0, 0, '', '', '', '', '')");
					s.executeUpdate("insert into " + EXTJRN + ".GZH351 values('" + WID + "', " + day + ", " + time + ", " + SeqNum + ", 'B', '" + Branch + "', '" + Primary_Number + "', '" + Suffix + "', 'Y', '" + SCMB + "', 'Y', '')");	
					s.executeUpdate("insert into " + EXTJRN + ".GZH351 values('" + WID + "', " + day + ", " + time + ", " + SeqNum + ", 'A', '" + Branch + "', '" + Primary_Number + "', '" + Suffix + "', 'Y', '" + SCMA + "', 'Y', '')");	
				}
				else {
					s.executeUpdate("insert into " + EXTJRN + ".GYPF values('" + WID + "', '', " + day + ", " + time + ", " + SeqNum + ", 'H35M', 'M', '', '', '', 'ISC', '', '', '', '', 0, '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', 0, 0, 0, '', '', '', 'Y', '', 0, 0, '', 0, 0, '', '', '', '', '')");
					s.executeUpdate("insert into " + EXTJRN + ".GZH351 values('" + WID + "', " + day + ", " + time + ", " + SeqNum + ", 'B', '" + Branch + "', '" + Primary_Number + "', '" + Suffix + "', 'N', '" + SCMB + "', 'Y', '')");	
					s.executeUpdate("insert into " + EXTJRN + ".GZH351 values('" + WID + "', " + day + ", " + time + ", " + SeqNum + ", 'A', '" + Branch + "', '" + Primary_Number + "', '" + Suffix + "', 'N', '" + SCMA + "', 'Y', '')");	
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