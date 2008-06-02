package robocode.pimods;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

public class PimodsClient {
	
	private static final String FILE = "config.txt";
	static PrintWriter out;
	static Socket socket;
	
	static{
        try {
//        	BufferedReader fileConfig = new BufferedReader( new FileReader( FILE ));
//        	StringTokenizer st = new StringTokenizer( fileConfig.readLine(), ":" );
//            socket = new Socket(st.nextToken(), Integer.parseInt( st.nextToken()));
        	socket = new Socket("127.0.0.1", 4444);
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (UnknownHostException e) {
            System.err.println("I can't find 3D-view server.");
//            System.exit(1);
        } catch (IOException e) {
            System.err.println("I can't find 3D-view server.");
//            System.exit(1);
        }
	}
	
	
	public static void sendMessage( String message ){
		if( out!=null )
			out.println( message );
	}
	
	public static void close(){
		try {
			if( out!=null ){
				out.close();
				socket.close();
			}
		} catch( Exception e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
