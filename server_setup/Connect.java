/**
TO COMPILE:
javac -classpath '/var/www/html/tankyudh_cfitbhu/virtual-combat/libs/*' Connect.java

DONOT Run this separately. This is Util class.

*/

import java.sql.*;
import java.util.*;
import java.io.*;
import java.net.*;

class InputTable{
	int matchID;
	String SecKey;
	String team1;
	String team2;
	public InputTable(int matchID,String SecKey, String team1, String team2){
		this.matchID=matchID;
		this.SecKey=SecKey;
		this.team1=team1;
		this.team2=team2;
	}
}
public class Connect{
	private int PENDING_THRESHOLD = 10*60;
	private Connection conn;
	public Connect(Connection conn){
		this.conn=conn;
	}
	public void updateStatus(int status,int match_ID){
		Statement s=null;
		try{
			s=conn.createStatement();
			s.executeUpdate("UPDATE BATTLEDATA SET status = "+status+ " where mid = "+match_ID);
		}
		catch(Exception err){
			System.err.println("e2"+err.getMessage());
		}
		finally{
			try {
				s.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public void updateProcess(int status,int process_ID){
		Statement s=null;

		try{
			s=conn.createStatement();
			System.out.println("PID ===== "+process_ID);
			s.executeUpdate("UPDATE processes SET status = "+status+ " where pid = "+process_ID);
		}
		catch(Exception err){
			System.err.println("e2"+err.getMessage());
		}
		finally{
			try {
				s.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}


	public void insertProcess(int status,int process_ID){
		Statement s=null;
		ResultSet rs=null;
		try{
			s=conn.createStatement();
			//System.out.println("PID ===== "+process_ID);
//			String qry="insert into processes values(default,"+process_ID+",CURRENT_TIMESTAMP,"+status+ ");";
			s.executeQuery("SELECT UNIX_TIMESTAMP(CURRENT_TIMESTAMP)");
			rs=s.getResultSet();
			rs.next();
			long time = rs.getLong("UNIX_TIMESTAMP(CURRENT_TIMESTAMP)");
			//System.out.println("insert into processes values(default,"+process_ID+","+time+","+status+ ");");
			s.executeUpdate("insert into processes values(default,"+process_ID+","+time+","+status+ ");");
			//System.out.println("PID ===== "+process_ID);
		}
		catch(Exception err){
			System.err.println("e2"+err.getMessage());
		}
		finally{
			try {
				if(s!=null) s.close();
				if(rs!=null) rs.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}


	public void saveResult(robocode.control.events.BattleCompletedEvent e,int match_ID,String team1){
		Statement s=null;
		String toSend=null;
		String winner=null;
		ResultSet rs=null;
		try{
			s=conn.createStatement();
			s.executeQuery("SELECT seckey FROM BATTLEDATA WHERE mid = "+match_ID);
			rs=s.getResultSet();
			rs.next();
			toSend="key="+URLEncoder.encode(rs.getString("seckey"),"UTF-8");
			System.out.println(toSend);
			String str="UPDATE BATTLEDATA SET status = 2";
			int I=1;
			for (robocode.BattleResults result : e.getIndexedResults()) {
				if(winner==null) winner=result.getTeamName();
				//if(result.getTeamName().equals(team1)) toSend+="&score1="+URLEncoder.encode(result.getCombinedScore()+"","UTF-8");
				toSend+="&score"+I +"="+URLEncoder.encode(result.getCombinedScore()+"","UTF-8");
				str+=",score" +I++ +" = " + (int)result.getCombinedScore();
				
			}
			str+=" WHERE mid = "+match_ID;
			//System.out.println(str);
			s.executeUpdate(str);
		}
		catch(Exception err){
			System.err.println("e1"+err.getMessage());
		}
		finally{
			try {
				if(s!=null) s.close();
				if(rs!=null) rs.close();
				//conn.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		sendPostRequest(match_ID,0,winner,toSend);
		
	}
	
	public void sendPostRequest(int match_ID,int error,String winner,String toSend){
		if(winner == null || toSend==null) error=1;
		DataOutputStream outStream=null;
		try {
			URL url= new URL("http://localhost/codefest/lib/vc/vc-oj-callback.php");
			URLConnection urlconn=url.openConnection();
			((HttpURLConnection)urlconn).setRequestMethod("POST");
			toSend+="&error="+URLEncoder.encode(error+"","UTF-8");
			toSend+="&data="+URLEncoder.encode(match_ID+".br","UTF-8");
			urlconn.setDoOutput(true);
			
			urlconn.setUseCaches(false);
			urlconn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			urlconn.setRequestProperty("Content-Length", ""+ toSend.length());
			outStream = new DataOutputStream(urlconn.getOutputStream());
			outStream.writeBytes(toSend);
			outStream.flush();

			StringBuffer ans= new StringBuffer();
			BufferedReader br=new BufferedReader(new InputStreamReader(urlconn.getInputStream()));
			String line;
			while((line=br.readLine())!=null) ans.append(line);
			br.close();
			outStream.close();
			System.out.println("URL connection response : "+ ans);			
		} catch (MalformedURLException e1) { 
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

	public ArrayList<InputTable> getUnprocessedBattles(int progPid)throws SQLException{
		Statement s=null,s1=null;
		ResultSet rs=null;
		ArrayList<InputTable> ll=new ArrayList<InputTable>();
		try{
			s=conn.createStatement();
			s.executeQuery("SELECT UNIX_TIMESTAMP(CURRENT_TIMESTAMP)");
			rs=s.getResultSet();
			rs.next();
			long time = rs.getLong("UNIX_TIMESTAMP(CURRENT_TIMESTAMP)");
			
			//System.out.println(time);
			s=conn.createStatement();
			s.executeQuery("SELECT * FROM BATTLEDATA WHERE status = 1  AND time < "+(time-600));
			//rs=null;
			rs=s.getResultSet();
			while(rs.next()){
				s1=conn.createStatement();
				if(rs.getInt("attempts")>=3){
					s1.executeUpdate("UPDATE BATTLEDATA SET status = 4  WHERE mid = "+ rs.getInt("mid"));
					sendPostRequest(rs.getInt("mid"),1,null,"key="+URLEncoder.encode(rs.getString("seckey"),"UTF-8"));
				}
				else{
					s1.executeUpdate("UPDATE BATTLEDATA SET status = 0 , attempts = "+ (rs.getInt("attempts")+1)+" WHERE mid = "+ rs.getInt("mid"));
					
				}
			}

			s=conn.createStatement();
			s.executeQuery("SELECT * FROM BATTLEDATA WHERE (status = 0 or status > 10 ) AND attempts>3");
			//rs=null;
			rs=s.getResultSet();
			while(rs.next()){
					s1=conn.createStatement();
					s1.executeUpdate("UPDATE BATTLEDATA SET status = 4  WHERE mid = "+ rs.getInt("mid"));
					sendPostRequest(rs.getInt("mid"),1,null,"key="+URLEncoder.encode(rs.getString("seckey"),"UTF-8"));
			}



			s=conn.createStatement();
			s.executeQuery("SELECT * FROM BATTLEDATA WHERE (status = 0 or status>10) and attempts < 4 LIMIT 10");
			rs=s.getResultSet();
			int counter=0;
			String tmp;
			while(rs.next() && rs.getInt("status") !=progPid && counter++<5){
				if(rs.getInt("status")==0) tmp="UPDATE BATTLEDATA SET time = " +time +" , attempts = "+ (rs.getInt("attempts")+1)+ " , status = 1 WHERE mid = "+ rs.getInt("mid");
				else tmp="UPDATE BATTLEDATA SET status = "+ progPid+ ", time = " +time +" , attempts = "+ (rs.getInt("attempts")+1)+ "  WHERE mid = "+ rs.getInt("mid");
				ll.add(new InputTable(rs.getInt("mid"),rs.getString("seckey"),rs.getString("team1"),rs.getString("team2")));
				s1=conn.createStatement();
				s1.executeUpdate(tmp);

			}
         }
         catch (Exception e){
               		System.err.println (e+"\n"+e.getMessage());
          }
         finally{
        	 if(s!=null) s.close();
        	 if(s1!=null) s1.close();
		if(rs!=null) rs.close();
        	// conn.close();
         }
         return ll;
	}

	public void endConnection(){
		try{
			conn.close();
		}
		catch(SQLException e){
			e.printStackTrace();
		}
	}
          
}
