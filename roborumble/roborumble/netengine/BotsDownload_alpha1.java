package roborumble.netengine;

import java.net.*;
import java.util.*;
import java.util.Vector;
import java.util.jar.*;
import java.util.zip.*;
import java.io.*;
import robocode.util.*;

/**
 * BotsDownload - a class by Albert Perez
 * Manages the download operations (participants and JAR files)
 * Controlled by properties files
 */

public class BotsDownload {

	private String internetrepository;
	private String botsrepository;
	private String participantsfile;
	private String participantsurl;
	private String tempdir;
	private String tag;
	
	public BotsDownload(String propertiesfile) {
		//Read parameters
		Properties parameters = null;
		try { parameters = new Properties(); parameters.load(new FileInputStream(propertiesfile)); } 
		catch (Exception e) { System.out.println("Parameters File not found !!!"); }
		internetrepository = parameters.getProperty("BOTSURL","");
		botsrepository = parameters.getProperty("BOTSREP","");
		participantsurl = parameters.getProperty("PARTICIPANTSURL","");
		participantsfile = parameters.getProperty("PARTICIPANTSFILE","");
		tag = parameters.getProperty("STARTAG","pre");
		tempdir = parameters.getProperty("TEMP","");

	}

	public boolean downloadParticipantsList() {
		String begin = "<"+tag+">";
		String end = "</"+tag+">";
		Vector bots = new Vector();

		try {
        	URL url = new URL(participantsurl);

		HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
		urlc.setRequestMethod("GET");
		urlc.setDoInput(true);
		urlc.connect();

		boolean arebots = false;
        	//BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		BufferedReader in = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
        	String str;
        	while ((str = in.readLine()) != null) {
				if (str.indexOf(begin)!=-1) arebots = true;
				else if (str.indexOf(end)!=-1) arebots = false;
				else if (arebots) bots.add(str);
        	}
        	in.close();
		urlc.disconnect();

			PrintStream outtxt = new PrintStream(new BufferedOutputStream(new FileOutputStream(participantsfile)),false);  
			for (int i=0; i<bots.size(); i++) outtxt.println((String)bots.get(i));
			outtxt.close();			

		} catch (Exception e) { 
			System.out.println("Unable to retrieve participants list"); 
			System.out.println(e);
			return false; 
		}
		return true;	
	}
	
	
	public boolean downloadMissingBots() {
		Vector jars = new Vector();
		Vector ids = new Vector();
		Vector names = new Vector();
		//Read participants
		try {
			FileReader fr = new FileReader(participantsfile); 
			BufferedReader br = new BufferedReader(fr);
			String record = new String();
			while ( (record=br.readLine()) != null ) { 
				String id = record.substring(record.indexOf(",")+1);
				String name = record.substring(0,record.indexOf(","));
				String jar = name.replace(' ','_')+".jar";
				jars.add(jar);
				ids.add(id);
				names.add(name);
			}
			br.close();
		} catch (Exception e) { 
			System.out.println("Battles input file not found ... Aborting"); 
			System.out.println(e);
			return false; 
		}
		//check if the file exists in the repository and download if not present
		for (int i=0; i<jars.size(); i++) {
			String botjar = (String)jars.get(i);
			String botid = (String)ids.get(i);
			String botname = (String)names.get(i);
			String botpath = botsrepository+botjar;
			boolean exists = (new File(botpath)).exists();
			if (!exists) { 
				boolean downloaded = downloadBot(botname,botjar,botid,botsrepository,tempdir);
				if (!downloaded) System.out.println("Could not download bot "+botjar);
			}
		}
		return true;
	}
	
	private boolean downloadBot(String botname, String file,String id, String destination, String tempdir) {
		//String urlname = directory+file;
		String filed = tempdir+file;
		String finald = destination+file;

		//check if the bot exists in the repository
		boolean exists = (new File(finald)).exists();
		if (exists) { 
			System.out.println("The bot already exists in the repository.");
			return false;
		}

		//Download and save the bot			
		URL url = null;
		HttpURLConnection urlc = null;
		try { 
			url = new URL("http://www.robocoderepository.com/Controller.jsp?submitAction=downloadClass&id="+id);
			urlc = (HttpURLConnection) url.openConnection();
			urlc.setInstanceFollowRedirects(true); 
			urlc.setRequestProperty("Referer","http://www.robocoderepository.com/BotSearch.jsp?botName=&authorName=&uploadDate=");
			urlc.setRequestMethod("GET");
			urlc.setAllowUserInteraction(false);
			urlc.setDoInput(true);
			urlc.setDoOutput(false);
			urlc.setUseCaches(false);
			urlc.connect();
			int clen = urlc.getContentLength(); 
			//Debug Information
			String protocol = url.getProtocol(); System.out.println("protocol="+protocol);
			String host = url.getHost(); System.out.println("host="+host);
			String hfile = url.getFile(); System.out.println("file="+hfile);
			int port = url.getPort(); System.out.println("port="+port);
			//End of debug information
			if (clen>0) {
				byte [] znak = new byte[clen];
				DataInputStream st = new DataInputStream(urlc.getInputStream());
				DataOutputStream wt = new DataOutputStream(new FileOutputStream(filed));
				st.readFully(znak); 
				wt.write(znak); 
				wt.flush(); st.close(); wt.close(); 
				//Check the jar corresponds to the bot and copy it to the repository
				if (checkJarFile(filed,botname)) {
					Utils.copy(new File(filed), new File(finald));
					System.out.println("Downloaded "+botname+" into "+finald);
				} 
				else { System.out.println("Wrong file"+file); return false; } 
			} else { System.out.println("Wrong file lenght("+clen+"): "+file); return false; }
			System.out.println("HTTP Code="+urlc.getResponseCode());
			urlc.disconnect();	

		 } catch (Exception e) { 
			System.out.println("Unable to download "+file); 
			System.out.println(e);
			return false; 
		}

		return true;
	}

	private boolean checkJarFile(String file, String botname) {
		String bot = botname.substring(0,botname.indexOf(" "));
		bot = bot.replace('.','/');
		bot = bot + ".properties";

		try {
			JarFile jarf = new JarFile(file);
			ZipEntry zipe = jarf.getJarEntry(bot);
			if (zipe == null) { System.out.println("Not able to read properties"); return false; }
			InputStream properties = jarf.getInputStream(zipe);

			Properties parameters = null;
			parameters = new Properties(); parameters.load(properties); 

			String classname = parameters.getProperty("robot.classname","");
			String version = parameters.getProperty("robot.version","");
			if (botname.equals(classname+" "+version)) return true; else return false;
 		} catch (Exception e) { 
			System.out.println(e);
			return false; 
		}
	}
	
}



