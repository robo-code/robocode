import java.io.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * by Albert Perez
 * Periodically generates the league results, based on RR@H data
 *
 */

public class GenerateLeagueResults extends HttpServlet {
    DataManager dataManager = DataManager.INSTANCE;	
    
    public void init() {
	int initialDelay = 30000; // start after 30 seconds
	int period = 12*3600*1000;        // repeat every 12 hours
	System.out.println("ROBORUMBLE@HOME: Scheduling tasks ...");
	Timer timer = new Timer();
	TimerTask task = new TimerTask() {
  		public void run() {
			String realpath = getServletContext().getRealPath("/rankings/")+"/";
			String[] games = { "roborumble" , "minirumble", "microrumble" , "nanorumble", "teamrumble" };
			Vector procdata = new Vector();
			System.out.println("ROBORUMBLE@HOME: Generating league files ...");
			//Process all games
			for (int gnum = 0; gnum < games.length; gnum++) {
				procdata.clear();
				//Open ratings file
				Properties sumary = null;
				try { sumary = dataManager.getData(realpath,"ratings_"+games[gnum]); } catch (Exception e) { sumary = new Properties(); }	
				//For each bot
				for (Enumeration e=sumary.propertyNames(); e.hasMoreElements();) {
					String bot = (String) e.nextElement();
					int battles = 0;
					int wins = 0;
					double score = 0;
					int rw = 0; int rt = 0; int rl = 0;
					//Open details file
					Properties results = null;
					try { results = dataManager.getData(realpath,games[gnum]+"_"+bot); } catch (Exception e1) { results = new Properties();  }
					for (Enumeration k=results.propertyNames(); k.hasMoreElements();) {
						String enemy = (String) k.nextElement();
						if (!enemy.equals(bot) && sumary.containsKey(enemy)) {
							//increase number of battles
							battles++;
							//get the % score
							String[] items = ((String) results.getProperty(enemy)).split(",");
							double percentage = Double.NaN;
							try { percentage = Double.parseDouble(items[0]); } catch (Exception e3) {}
							//update # wins and score
							if (percentage != Double.NaN) {
								score += percentage;
								if (percentage > 0.5) { wins += 2; rw++; }
								else if (percentage == 0.5) { wins += 1; rt++; }
								else rl++;
							}
						}
					}

					String swins = Integer.toString(wins); while (swins.length()<5) swins = "0"+swins;
					//String sbattles = Integer.toString(battles); while (sbattles.length()<5) sbattles = "0"+sbattles;
					String sbattles = Integer.toString(100000 - battles);
					String sscore = Integer.toString((int) score); while (sscore.length()<5) sscore = "0"+sscore;
					String botdata = swins+","+sbattles+","+sscore+","+bot+","+rw+"/"+rt+"/"+rl;

					procdata.add(botdata);
				}
				//sort results for the game
				Collections.sort(procdata); 
				try {
           					PrintStream outtxt = new PrintStream(new BufferedOutputStream(new FileOutputStream(realpath + "league_"+games[gnum] + ".txt", false)), true);

					Calendar calendar = new GregorianCalendar(); calendar.setTime(new Date(System.currentTimeMillis()));
					int year = calendar.get(Calendar.YEAR);
					int month = calendar.get(Calendar.MONTH)+1;
					int day = calendar.get(Calendar.DAY_OF_MONTH);
					int hour = calendar.get(Calendar.HOUR_OF_DAY);
					int minute = calendar.get(Calendar.MINUTE);

					outtxt.println("<h2>ROBORUMBLE@HOME PREMIER LEAGUE ("+games[gnum]+") </h2>");
					outtxt.println("<p>This page was generated on "+year+"-"+month+"-"+day+"/"+hour+":"+minute+"</p>");


					outtxt.println("<table border=1><tr><td><b>pos</b></td><td></td><td><b>bot</b></td><td><b>score</b></td><td><b>wins/ties/loses</b></td><td><b>pairings</b></td><td><b>total %wins</b></td></tr>");
					int pos = 1;
					for (int i = procdata.size()-1; i>=0; i--) {
						String data = (String) procdata.get(i);
						String[] items = data.split(",");
						int pwins = Integer.parseInt(items[0]); 
						int pbattles =  100000 - Integer.parseInt(items[1]); 
						int pscore =   Integer.parseInt(items[2]); 
						String pbot = items[3].replaceAll("_"," ");
						String botlink = "<a href=RatingDetails?game="+games[gnum]+"&name="+ items[3].replaceAll("_","%20")+"><img src=../pkg-flag/"+items[3].substring(0,items[3].indexOf(".")) +".gif></a>";
						outtxt.println("<tr><td>"+pos+"</td><td>"+botlink+"</td><td>"+pbot+"</td><td>"+pwins+"</td><td>"+items[4]+"</td><td>"+pbattles+"</td><td>"+pscore+"</td></tr>");
						//outtxt.println(items[3]+" "+wins+" "+battles+" "+score);
						pos++;
					}
					outtxt.println("<table>");
					outtxt.println("<p>Note: This page was generated some time ago, but details are built on real time. There can be some differences between them.</p>");
					
            				outtxt.flush(); outtxt.close(); 
       				 } catch (Exception e) {}
    			} //!!! all games processed
  		} //end of run method
	};
	timer.scheduleAtFixedRate(task, initialDelay, period);
    }	

}
