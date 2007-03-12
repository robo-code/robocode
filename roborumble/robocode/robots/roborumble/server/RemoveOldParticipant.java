import java.io.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

//import util.HTMLFilter;

/**
 * by Albert Perez
 * Removes a participant from the ratings file.
 */

public class RemoveOldParticipant extends HttpServlet implements SingleThreadModel {

    DataManager dataManager = DataManager.INSTANCE;
    String realpath;
    PrintWriter out;
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        response.setContentType("text/html");
        out = response.getWriter();
        out.println("ERROR. GET request not supported.");
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		//basics
		response.setContentType("text/html");

       	PrintWriter out = response.getWriter();

		realpath = this.getServletContext().getRealPath("/rankings/")+"/";

		//get results
		String version = request.getParameter("version");
        String game = request.getParameter("game");
		String name = request.getParameter("name");
	
		//check data for correctness

		if (version == null || version.equals("") || game == null ||game.equals("") || name == null || name.equals("")) return;
		if (!version.equals("1")) { out.println("ERROR. Only version 1 implemented"); return; }
	
		//send OK
		out.println("OK. "+name+" will be removed from "+game);

		String botname = name.replace(' ','_');
	
		//open ratings file

		Properties sumary = null;
		try { sumary = getData("ratings_" + game); } catch (IOException e) { }
		if (sumary == null) { out.println("Not able to open/create ratings file"); return; }

		//Update summary ratings file

		sumary.remove(botname);
		storeData(sumary, "ratings_" + game, "Rankings summary, updated " + Long.toString(System.currentTimeMillis()));

    }


     synchronized Properties getData(String identifier) throws IOException {
	Properties data = null;
	try {
	    data = dataManager.getData(realpath, identifier);
	}
	catch (DataManagerException e) {
	    out.println("ERROR. " + e);
	}
	return data; // Returns null if things go totally wrong in the data manager. Feels a bit ugly...
    }

    synchronized void storeData(Properties data, String identifier, String header) throws IOException {
	try {
	    dataManager.storeData(data, realpath, identifier, header);
	}
	catch (Exception e) {
	    out.println("ERROR. " + e);
	    throw new IOException("Failed writing data: " + header + ". " + e);
	}
    }

}
