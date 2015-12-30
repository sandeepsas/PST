package Trip;

import java.io.File;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class NYCConstants {
	
	/*Data Directories*/
	public static final File data_folder = new File("E:/Data Repo/Taxi-Trajectories/NYC/FOIL2013/FOIL2013/Trip-Files");
	public static final File[] csv_files = data_folder.listFiles();
	
	/*LatLong for LaG airport*/
	public static double LaG_lat = 40.776927;
	public static double LaG_lng = -73.873966;
	
	/*Time Zone Constants*/
	public static DateTimeFormatter dt_formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
	public static DateTimeFormatter date_formatter = DateTimeFormat.forPattern("yyyy-MM-dd");

}