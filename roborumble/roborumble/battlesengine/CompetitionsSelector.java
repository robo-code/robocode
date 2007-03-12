package roborumble.battlesengine;
import robocode.*;
import robocode.control.*;
import robocode.battle.*;
import codesize.*;
import java.util.*;
import java.util.zip.*;
import java.io.*;
import codesize.*;
import codesize.Codesize.*;

/**
 * BattlesRunner - a class by Albert Perez
 * Reads a file with the battles to be runned and outputs the results in another file.
 * Controlled by properties files
 */

public class CompetitionsSelector
{
	private String repository;
	private String sizesfile;
	private Properties sizes;
	
	public CompetitionsSelector(String sizesfile, String repository) { 
		this.repository = repository;
		//open sizes file
		this.sizesfile  = sizesfile;
		try { sizes = new Properties(); sizes.load(new FileInputStream(sizesfile)); } 
		catch (Exception e) { System.out.println("Sizes File not found !!!"); }
	}

	public boolean CheckCompetitorsForSize(String bot1, String bot2, long maxsize) {
		String bot1name = bot1.replace(' ','_');
		String bot2name = bot2.replace(' ','_');

		//Read sizes
		long size1 = Long.parseLong(sizes.getProperty(bot1name,"0"));
		long size2 = Long.parseLong(sizes.getProperty(bot2name,"0"));

		//find out the size if not in the file
		boolean fileneedsupdate = false;
		if (size1 == 0) {
			fileneedsupdate = true;
			File f = new File(repository+bot1name+".jar");
			try { 	Item s1 = Codesize.processZipFile(f); size1 = s1.getCodeSize(); } catch (Exception e) {}
			if (size1 !=0) sizes.setProperty(bot1name, Long.toString(size1));
		}
		if (size2 == 0) {
			fileneedsupdate = true;
			File f = new File(repository+bot2name+".jar");
			try { 	Item s2 = Codesize.processZipFile(f); size2 = s2.getCodeSize(); } catch (Exception e) {}
			if (size2 !=0) sizes.setProperty(bot2name, Long.toString(size2));
		}
		
		//if the file needs update, then save the file
		try {
			if (fileneedsupdate && size1 !=0 && size2 != 0) sizes.store(new FileOutputStream(sizesfile),"Bots code size");
		} catch (Exception e) {}

		//check the values
		if (size1 != 0 && size1 < maxsize && size2 !=0 && size2 < maxsize) return true;
		else return false;
	}
		
}



