import java.io.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

//import util.HTMLFilter;

/**
 * by Albert Perez
 * Retrieves a roborumble file for debug. 
 * The servlet is not syncronized, so it is not using the data management classes
 *
 */

public class RoborumbleDebug extends HttpServlet {

    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	doPost(request, response); 
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	response.setContentType("text/html");
        	PrintWriter out = response.getWriter();
	String realpath = this.getServletContext().getRealPath("/rankings/")+"/";

	//get parameters
	String filename = request.getParameter("filename"); 
	if (filename == null) {
		out.println("Error. You must specify a file name.");
		return;
	}
	try {
		FileReader fr = new FileReader(realpath+filename); 
		BufferedReader br = new BufferedReader(fr);
		String record = new String();
		while ( (record=br.readLine()) != null ) { 
			out.println(record);
		}
		out.println("*** END OF FILE ***");
		br.close();
	} catch (Exception e) { 
		out.println("Error. File not found."); 
	}
    }

}
