import java.io.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

//import util.HTMLFilter;

/**
 * by Albert Perez
 * Gets the results uploaded from the RoboRumble client.
 * Returns the number of battles fought up to date by each bot.
 * updates rankings.
 */

public class UploadedResults extends HttpServlet implements SingleThreadModel {
    private static final double min_score = 0.0;
    private static final double max_score = 1.0;
    private static final double rating_change = 3.0; //3.0	

    DataManager dataManager = DataManager.INSTANCE;
    String realpath;
    PrintWriter out;
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	
	//doPost(request,response);

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
	String rounds = request.getParameter("rounds");
	String field = request.getParameter("field");
	String user = request.getParameter("user");
	String time = request.getParameter("time");
	String fname = request.getParameter("fname");
	String fscore = request.getParameter("fscore");
	String fbulletd = request.getParameter("fbulletd");
	String fsurvival = request.getParameter("fsurvival");
	String sname = request.getParameter("sname");
	String sscore = request.getParameter("sscore");
	String sbulletd = request.getParameter("sbulletd");
	String ssurvival = request.getParameter("ssurvival");
	
	//check results for correctness

	if (version == null || version.equals("") || game == null ||game.equals("") || rounds == null ||rounds.equals("") || field == null ||field.equals("") || user == null || user.equals("") || time == null ||time.equals("") || fname == null || fname.equals("") || fscore ==null || fscore.equals("") || fbulletd == null || fbulletd.equals("") || fsurvival == null || fsurvival.equals("") || sname == null || sname.equals("") || sscore ==null || sscore.equals("") || sbulletd == null || sbulletd.equals("") || ssurvival == null || ssurvival.equals("")) return;
	if (!version.equals("1")) { out.println("ERROR. Only version 1 implemented"); return; }
	try { Double.parseDouble(fscore); Double.parseDouble(sscore); } catch (Exception e) { return; } 

	if (game.indexOf("melee")!=-1) {
		if (!rounds.equals("35") || !field.equals("1000x1000")) {
			out.println("OK. Melee results have been received but they will be ignored because parameters are different. Please use 35 rounds and 1000x1000 field");
			return;
		}
	} else {
		if ( (!game.equals("teamrumble") && (!rounds.equals("35") || !field.equals("800x600"))) ||
		     (game.equals("teamrumble") && (!rounds.equals("10") || !field.equals("1200x1200")))	
	   	) {
			out.println("OK. Results have been received but they will be ignored because parameters are different. Please use 35(10) rounds and 800x600(1200x1200) field");
			return;
		}
	}
	
	//send OK
	out.println("OK. "+fname+" vs. "+sname+" received");

	String bot1name = fname.replace(' ','_');
	String bot2name = sname.replace(' ','_');

	//get data for bot1
	Properties bot1 = getBotData(bot1name, game, fname);
	if (bot1 == null) { out.println("ERROR reading bot1 properties file"); return; }
	int battles1 = 0; try { battles1 = Integer.parseInt(bot1.getProperty("battles","0"));  } catch (Exception e) {};
	double rating1 = Double.parseDouble(bot1.getProperty("rating","1600")); if (Double.isNaN(rating1)) rating1 = 1600;
	double real1 = Double.parseDouble(fscore) / (Double.parseDouble(fscore)+Double.parseDouble(sscore)); if (Double.isNaN(real1) || real1 == 0.0 || real1 == 1.0) return;
	String e1 = bot1.getProperty(bot2name,Double.toString(real1)+",0,0"); String[] ep1 = e1.split(",");
	double wins1 = Double.parseDouble(ep1[0]); if (Double.isNaN(wins1) || wins1 == 0) wins1 = real1; 
	 int benemy1 = 0; try { benemy1 = Integer.parseInt(ep1[1]); } catch (Exception e) { }
	
	//get data for bot2
	Properties bot2 = getBotData(bot2name, game, fname);
	if (bot1 == null) { out.println("ERROR reading bot1 properties file"); return; }
	int battles2 = 0; try { battles2 = Integer.parseInt(bot2.getProperty("battles","0")); } catch (Exception e) {};
	double rating2 = Double.parseDouble(bot2.getProperty("rating","1600")); if (Double.isNaN(rating2)) rating2 = 1600;
	double real2 = 1 - real1;
	String e2 = bot2.getProperty(bot1name,Double.toString(real2)+",0,0"); String[] ep2 = e2.split(",");
	double wins2 = Double.parseDouble(ep2[0]); if (Double.isNaN(wins2) || wins2 == 0) wins2 = real2;
	int benemy2 = 0; try { benemy2 = Integer.parseInt(ep2[1]); } catch (Exception e) { }; 

	//update wins %
	wins1 = 0.7 * wins1 + 0.3 * real1;
	wins2 = 0.7 * wins2 + 0.3 * real2;
	benemy1++; bot1.setProperty(bot2name,wins1+","+benemy1+","+Long.toString(System.currentTimeMillis()));
	benemy2++; bot2.setProperty(bot1name,wins2+","+benemy2+","+Long.toString(System.currentTimeMillis()));
	
	//open ratings file
	Properties sumary = null;
	try { sumary = getData("ratings_" + game); } catch (IOException e) { }
	if (sumary == null) { out.println("Not able to open/create ratings file"); return; }

	//calculate new wins and ranking
	double change1 = 0;
	double change2 = 0;
	for (Enumeration e = sumary.propertyNames(); e.hasMoreElements();) {
		//gets a bot
		String key = (String) e.nextElement(); 
		String[] values = (sumary.getProperty(key,"")).split(","); //gets its current ranking
		
		if (values.length == 3) { //checks for the correctness of the line

			double selectedrating = Double.parseDouble(values[0]);  

			//rating change for bot 1 (only bots with recent battles - 5 days - are considered)
			if (!key.equals(bot1name) && !Double.isNaN(selectedrating)) {
				double expected = 1.0 / (1+Math.pow(20,(selectedrating-rating1)/800)); 
				//next line to trim the results to the max-min values
				expected = Math.max(Math.min(expected,max_score),min_score);
				String[] selectedbot =(bot1.getProperty(key,"")).split(",");
				long lasttime = 0; try { lasttime = Long.parseLong(selectedbot[2]); } catch (Exception ex) {};
				if (!selectedbot[0].equals("") && !Double.isNaN(Double.parseDouble(selectedbot[0])) 
				    /* && (System.currentTimeMillis()-lasttime) < 1000*3600*24*5 */) {
					double selectedwins = Double.parseDouble(selectedbot[0]);
					//next line to trim the results to the max-min values
					selectedwins = Math.max(Math.min(selectedwins,max_score),min_score);
					change1 += rating_change * (selectedwins - expected);
				}
				//else there is no pairing for this bot, so the battle should be run
				else if (battles1 >= 500) out.println("["+bot1name+","+key+"]");

			}
			//rating change for bot 2 (only bots with recent battles - 5 days - are considered)
			if (!key.equals(bot2name) && !Double.isNaN(selectedrating)) {
				double expected = 1.0 / (1+Math.pow(20,(selectedrating-rating2)/800)); 
				//next line to trim the results to the max-min values
				expected = Math.max(Math.min(expected,max_score),min_score);
				String[] selectedbot = (bot2.getProperty(key,"")).split(",");
				long lasttime = 0; try { lasttime = Long.parseLong(selectedbot[2]); } catch (Exception ex) {};
				if (!selectedbot[0].equals("") && !Double.isNaN(Double.parseDouble(selectedbot[0])) 
				    /* && (System.currentTimeMillis() -lasttime) < 1000*3600*24*5 */) {
					double selectedwins = Double.parseDouble(selectedbot[0]);
					//next line to trim the results to the max-min values
					selectedwins = Math.max(Math.min(selectedwins,max_score),min_score);
					change2 += rating_change * (selectedwins - expected);
				}
				//else there is no pairing for this bot, so the battle should be run
				else if (battles2 >= 500) out.println("["+bot2name+","+key+"]");

			}
		}

	}
	if (battles2 >= 50) rating1 += change1;
	if (battles1 >= 50) rating2 += change2;
	
	//add battles and updated rankings to the files

	battles1++; bot1.setProperty("battles",Integer.toString(battles1));
	battles2++; bot2.setProperty("battles",Integer.toString(battles2));

	bot1.setProperty("rating",Double.toString(rating1));
	bot2.setProperty("rating",Double.toString(rating2));

	bot1.setProperty("lastbattle",Long.toString(System.currentTimeMillis()));
	bot2.setProperty("lastbattle",Long.toString(System.currentTimeMillis()));

	//store files
	storeData(bot1, game + "_" + bot1name, "Ratings file for "+fname);
	storeData(bot2, game + "_" + bot2name, "Ratings file for "+sname);

	//Update summary ratings file

	sumary.setProperty(bot1name,rating1+","+battles1+","+Long.toString(System.currentTimeMillis()));
	sumary.setProperty(bot2name,rating2+","+battles2+","+Long.toString(System.currentTimeMillis()));
	storeData(sumary, "ratings_" + game, "Rankings summary, updated " + Long.toString(System.currentTimeMillis()));

	//open log and add data to it
	String data = rounds+","+field+","+user+","+time+","+fname+","+fscore+","+fbulletd+","+fsurvival+","+sname+","+sscore+","+sbulletd+","+ssurvival;
	data = data.trim();
	try { logBattle(data, game); } catch (Exception e) { return; }

	//send back information about the number of battles fought
	out.println("<"+battles1+" "+battles2+">");
    }


    //-----------------------------------------------------------------------------------------------------
    //retrieves the ranking from the last version available. If not present, rating is set to default 1600
    //-----------------------------------------------------------------------------------------------------

    private synchronized void setInitialRanking(Properties bot, String botname, String realpath, String game) {

	String bottype = botname.substring(0,botname.indexOf(" ")); 
	String[] files = dataManager.getFiles(realpath);
	Arrays.sort(files);
	String lastversion = null;
	for (int i=0; i<files.length && lastversion == null; i++) { if (files[i].indexOf(game+"_"+bottype)!=-1) lastversion = files[i]; }
	
	if (lastversion == null) {
		bot.setProperty("battles","0");
		bot.setProperty("rating","1600");
	}
	else {

	   lastversion = lastversion.substring(0,lastversion.lastIndexOf(".txt")); //remove the file extension	
	
	    Properties old = null;
	    try {
		old = getData(lastversion);
	    }
	    catch (Exception e) {
		if (old == null) old = new Properties();
		old.setProperty("rating","1600");
		System.out.println("Not able to open previous version file. Initialize to 1600.");	
	    }
	    bot.setProperty("battles","0");
	    bot.setProperty("rating",old.getProperty("rating","1600"));
	}
	
    }

    synchronized Properties getBotData(String botName, String game, String fname) {
	Properties botData = null;
	try {
	    botData = getData(game + "_" + botName);
	}
	catch (IOException e) {
	    botData = new Properties();
	    setInitialRanking(botData, fname, realpath, game);
	}
	return botData;
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

    synchronized void logBattle(String data, String game) throws IOException {
	try {
	    dataManager.appendToLog(data, realpath, "battles_" + game);
	}
	catch (Exception e) {
	    out.println("ERROR. " + e);
	    throw new IOException("Failed writing log: " + "battles_" + game + ". " + e);
	}
    }
}
