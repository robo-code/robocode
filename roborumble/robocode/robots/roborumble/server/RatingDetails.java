import java.io.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

//import util.HTMLFilter;

/**
 * by Albert Perez
 * Prints the detail of the battles fought and the rating for a bot,
 *
 */

public class RatingDetails extends HttpServlet {
    //private static final double min_score = 40;
    //private static final double max_score = 60;
    //private static final double rating_change = 5.0;	 //3.0
    private static final double min_score = 0;
    private static final double max_score = 100;
    private static final double rating_change = 3.0;	 //3.0

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
	String name = request.getParameter("name"); if (name == null) name ="";
	String encodedname = name.replaceAll(" ","%20");
	//String sb = request.getParameter("frombattle"); if (sb == null) sb ="";	
	//int showbattles = 0; if (! sb.equals("")) showbattles = Integer.parseInt(sb); 

	//Check data
	if (!version.equals("1")) { out.println("ERROR. Only version 1 is implemented."); return; }

	//Open data file
	String bot1name = name.replace(' ','_');
	Properties bot = null;
	try { 
		bot = dataManager.getData(realpath,game+"_"+bot1name);
		//bot = new Properties(); bot.load(new FileInputStream(realpath+game+"_"+bot1name+".txt")); 
	} 
	catch (Exception e) { out.println("ERROR."); out.println("No data found for "+name+" in game "+game); return; }

	//Get data
	int battles = Integer.parseInt(bot.getProperty("battles","0"));
	double rating = Double.parseDouble(bot.getProperty("rating","1600"));
	double roundedrating = ((int)(rating*100))/100.0;
	long lastbattle = Long.parseLong(bot.getProperty("lastbattle","0"));
	Calendar calendar = new GregorianCalendar(); calendar.setTime(new Date(lastbattle));
	int year = calendar.get(Calendar.YEAR);
	int month = calendar.get(Calendar.MONTH)+1;
	int day = calendar.get(Calendar.DAY_OF_MONTH);
	int hour = calendar.get(Calendar.HOUR_OF_DAY);
	int minute = calendar.get(Calendar.MINUTE);

	out.println("<h2>RATING DETAILS FOR "+name+" IN GAME "+game+"</h2>");
	out.println("<h3>CURRENT RATING = "+roundedrating+"</h3>");
	out.println("<p>See more ratios at the end of the page</p>");
	out.println("<p>It has participated in "+battles+" battles. Last one hold on "+year+"-"+month+"-"+day+":"+hour+":"+minute+"</p>");

	//Open ratings file
	Properties sumary = null;
	try { 
		sumary = dataManager.getData(realpath,"ratings_"+game);
		//sumary = new Properties(); sumary.load(new FileInputStream(realpath+"ratings_"+game+".txt")); 
	} catch (Exception e) { }
	
	//Print the results for the enemies

	ArrayList bots = new ArrayList();
	for (Enumeration e=bot.propertyNames(); e.hasMoreElements();) {
		String key = (String) e.nextElement();
		if (!key.equals("rating") && !key.equals("battles")  && !key.equals("lastbattle")) bots.add(key+","+bot.getProperty(key));
	}
	String[] sortedbots = (String[]) bots.toArray(new String[1]);
	Arrays.sort(sortedbots);

	double specialized = 0;
	double momentum = 0;

	out.println("<table border=1>");
	out.println("<tr><td><b>enemy</b></td><td><b>% score</b></td><td><b>battles</b></td><td><b>last battle</b></td><td><b>expected %</b></td><td><b>ProblemBot Index</b></td></tr>");
	for (int i=0; i<sortedbots.length; i++) {
		String[] items = sortedbots[i].split(",");
		double roundedwins = ((int) (1000 * Double.parseDouble(items[1])))/10.0;
		calendar = new GregorianCalendar(); calendar.setTime(new Date(Long.parseLong(items[3])));
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH)+1;
		day = calendar.get(Calendar.DAY_OF_MONTH);
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		minute = calendar.get(Calendar.MINUTE);
		String enemystring = sumary.getProperty(items[0],"");
		if (!enemystring.equals("") /* && (System.currentTimeMillis()-Long.parseLong(items[3])) < 1000*3600*24*5 */)  { 
			String[] enemy = enemystring.split(",");
			double enemyrating = Double.NaN; try { if (enemy.length > 0) enemyrating = Double.parseDouble(enemy[0]); } catch (Exception e) {}
			double expectedwins = ((int)(1000 / (1+Math.pow(20,(enemyrating-rating)/800))))/10.0;
			//added one and removed next to limit max-min 
			double problembot =((int) (10*(Math.max(Math.min(roundedwins,max_score),min_score)-Math.max(Math.min(expectedwins,max_score),min_score))))/10.0;
			//double problembot =((int) (10*(roundedwins-expectedwins)))/10.0;
			String bgcolor1="<td>"; 
			//remove the OR conditions to remove the limits
			if (problembot > 10)  bgcolor1="<td bgcolor=99CC00>"; 
			else if (problembot < -10)  bgcolor1="<td bgcolor=FF6600>";
			String bgcolor2="<td>"; 
			if (roundedwins > 60)  bgcolor2="<td bgcolor=99CC00>"; else if (roundedwins < 40)  bgcolor2="<td bgcolor=FF6600>";
			out.println("<tr><td>"+items[0]+"</td>"+bgcolor2+roundedwins+"</td><td>"+items[2]+"</td><td>"+day+"-"+month+"-"+year+":"+hour+":"+minute+"</td><td>"+expectedwins+"</td>"+bgcolor1+problembot+"</td></tr>");
			specialized += problembot*problembot;
			//changed
			momentum += rating_change * problembot;
			//momentum += 3 * (roundedwins - expectedwins);
		}
	}
	out.println("</table>");

	specialized = ((int)(10*specialized/sortedbots.length))/10.0;
	momentum = ((int)10*momentum)/10.0;

	out.println("<p><b>Specialization Index = "+specialized+"</b></p>");
	out.println("<p><b>Momentum = "+momentum+"</b></p>");

	out.println("<p>Expected % is the expected percentage of points the bot should get against the enemy, acording to  both ratings.</p>");
	out.println("<p>Problem Bot Index is the difference between you real score percentage and the expected percentage. A positive index means you are outperforming (consdering the rating diference), so it contributes to improve your rating. A negative index means it is a ProblemBot, you are performing against it below expectations, and it is degrading your rating.</p>");
	out.println("<p>Specialization index measures how much specialized is your bot. A big index means your bot is highly specialized. A low index means your bot is a generalist. It is calculated as the average squared value for the ProblemBot index</p>");
	out.println("<p>Momentum is a calculation of the rating change speed. A low momentum means the bot has got into an stable rating. A big momentum means the bot is movig up (if positive) or down (if negative).</p>");
    }


}
