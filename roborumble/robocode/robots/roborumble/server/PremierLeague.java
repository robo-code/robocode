import java.io.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

//import util.HTMLFilter;

/**
 * by Albert Perez
 * Prints the premier league results from the generated file
 *
 */

public class PremierLeague extends HttpServlet {
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	doPost(request, response); 
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	response.setContentType("text/html");
        	PrintWriter out = response.getWriter();
	String realpath = this.getServletContext().getRealPath("/rankings/")+"/";

	//get parameter
	String game = request.getParameter("game"); if (game == null) game ="";
	
	try {
		FileReader fr = new FileReader(realpath + "league_" + game + ".txt"); 
		BufferedReader br = new BufferedReader(fr);
		String record = new String();
		while ( (record=br.readLine()) != null ) { 
			out.println(record);
		}
		br.close();
	} catch (Exception e) {  out.println("File not found for game "+game); }
	
    }


}
