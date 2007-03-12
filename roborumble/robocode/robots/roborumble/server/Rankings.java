import java.io.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

//import util.HTMLFilter;

/**
 * by Albert Perez
 * Prints the rankings for a given competition
 *
 */

public class Rankings extends HttpServlet {
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

	Calendar calendar = new GregorianCalendar(); calendar.setTime(new Date(System.currentTimeMillis()));
	int year = calendar.get(Calendar.YEAR);
	int month = calendar.get(Calendar.MONTH)+1;
	int day = calendar.get(Calendar.DAY_OF_MONTH);
	int hour = calendar.get(Calendar.HOUR_OF_DAY);
	int minute = calendar.get(Calendar.MINUTE);

	out.println("<h2>CURRENT RANKINGS FOR GAME "+game+"</h2>");
	out.println("<p>This page is generated dynamically. The results can change from display to display. To see stable OFFICIAL rankings, check the Wiki pages. </p>");
	out.println("<p>Generation time: "+year+"-"+month+"-"+day+":"+hour+":"+minute+"</p>");

	//Open ratings file
	Properties sumary = null;
	try { sumary = dataManager.getData(realpath,"ratings_"+game); } catch (Exception e) { }
	
	//Sort the results

	TreeMap bots = new TreeMap();

	for (Enumeration e=sumary.propertyNames(); e.hasMoreElements();) {
		String key = (String) e.nextElement();
		String line = sumary.getProperty(key," ");
		String[] data = (line).split(",");
		if (data.length > 2) {
			Double score = new Double(Double.NaN) ; try { score = Double.valueOf(data[0]); } catch (Exception ex) {};
			if (!score.isNaN()) {
				bots.put(score,key+","+line);	
			}
		}
	}
	
	String[] sortedbots = (String[]) bots.values().toArray(new String[1]); //sorted in ascending order
	
	double numbattles = 0;
	int count = 1;

	out.println("<table border=1>");
	out.println("<tr><td><b>rank</b><td></td></td><td><b>bot</b></td><td><b>rating</b></td><td></td><td></td><td><b># battles</b></td><td><b>last battle</b></td></tr>");
	for (int i=sortedbots.length-1; i>=0; i--) {
		String[] items = sortedbots[i].split(",");
		double roundedrating = ((int) (100 * Double.parseDouble(items[1])))/100.0;
		calendar = new GregorianCalendar(); calendar.setTime(new Date(Long.parseLong(items[3])));
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH)+1;
		day = calendar.get(Calendar.DAY_OF_MONTH);
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		minute = calendar.get(Calendar.MINUTE);
		String battles = items[2];

		String botname = items[0].replaceAll("_"," ");
		String flag = "<img src=../pkg-flag/"+items[0].substring(0,items[0].indexOf(".")) +".gif>";
		String details = "<a href=RatingDetails?game="+game+"&name="+ items[0].replaceAll("_","%20")+">details</a>";
		String lrp = "<a href=/lrp?game="+game+"&name="+ items[0].replaceAll("_","%20")+">graph</a>";
		
		if ( !botname.equals("") /* && (System.currentTimeMillis()-Long.parseLong(items[3])) < 1000*3600*24*5 */) {
		
			out.println("<tr><td>"+(count)+"</td><td>"+flag+"</td><td>"+botname+"</td><td>"+roundedrating+"</td><td>"+details+"</td><td>"+lrp+"</td><td>"+battles+"</td><td>"+day+"-"+month+"-"+year+":"+hour+":"+minute+"</td></tr>");
			numbattles += Double.parseDouble(battles); count++;

		}
	}
	out.println("</table>");

	out.println("<p><b>Total battles = "+(numbattles/2)+"</b></p>");
	
    }


}
