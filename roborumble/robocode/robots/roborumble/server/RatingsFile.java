import java.io.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

//import util.HTMLFilter;

/**
 * by Albert Perez
 * Retrieves the ratings file for a game, so it can be readed as a properties file,
 *
 */

public class RatingsFile extends HttpServlet {
    DataManager dataManager = DataManager.INSTANCE;	

    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	doPost(request, response); 
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	response.setContentType("text/html");
        	PrintWriter out = response.getWriter();
	String realpath = this.getServletContext().getRealPath("/rankings/")+"/";

	//get parameters
	String version = request.getParameter("version"); if (version == null) version = "1";
	String game = request.getParameter("game"); if (game == null) game ="";

	//Check data
	if (!version.equals("1")) { out.println("ERROR. Only version 1 is implemented."); return; }

	//Open ratings file
	Properties sumary = null;
	try { sumary = dataManager.getData(realpath,"ratings_"+game); } catch (Exception e) { }
	
	//Send file

	for (Enumeration e=sumary.propertyNames(); e.hasMoreElements();) {
		String key = (String) e.nextElement();
		String line = sumary.getProperty(key," ");
		String[] data = (line).split(",");
		if (data.length > 2) {
			out.println(key+"="+line);	
		}
	}
	
    }

}
