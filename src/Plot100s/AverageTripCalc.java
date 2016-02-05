package Plot100s;
/*
 * 
 * @Author: Sandeep Sasidharan
 */
/*
 * Reduction in the number of trips as a function the length 
 * of the average delay tolerated (5%-15% of length of shortest path)
 * */

import java.io.*;

import java.util.*;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import Graph.Pair;
import Graph.ShareabilityGraph;
import Trip.*;


public class AverageTripCalc {
	public static final Logger LOGGER = Logger.getLogger(AverageTripCalc.class.getName());

	public static void main (String[] args0) throws IOException, ClassNotFoundException{
		
		PrintStream out = new PrintStream(new FileOutputStream("output_tripCNT_MH_9min.txt"));
		System.setOut(out);

/*		PrintStream out = new PrintStream(new FileOutputStream("output_tripCNT.txt"));
		System.setOut(out);

		PrintWriter merge_trips_writer = new PrintWriter(new FileWriter ("Plot_3_tripCNT.txt"));
		PrintWriter merge_trips_coll= new PrintWriter(new FileWriter ("Plot_3_tripCNT.txt"));*/
		// Read Trip between 2013-01-01 08:50:00 and 2013-01-01 08:55:00
		//DateTime startTime = Constants.dt_formatter.parseDateTime("2013-01-01 10:00:00");
		//DateTime endTime = Constants.dt_formatter.parseDateTime("2013-12-31 22:00:00");
		TripLoader tripLoader = new TripLoader();
		ObjectInputStream ios_graph_read = new ObjectInputStream(new FileInputStream("ObjectWarehouse/TripData/DateSet_6min.obj"));
		List<Pair<DateTime,DateTime>> dates_100 = new ArrayList<Pair<DateTime,DateTime>>();
		dates_100 =   (List<Pair<DateTime, DateTime>>) ios_graph_read.readObject();
		Collections.shuffle(dates_100);
		int count =0;
		StringBuilder merge_writer_str  = new StringBuilder();
		
		int no_trips = 0;
		for(Pair<DateTime, DateTime> date_pair:dates_100){
			merge_writer_str = new StringBuilder();
			DateTime startTime = date_pair.getL();
			DateTime endTime = startTime.plusMinutes(9);

			List<TaxiTrip>  trips = tripLoader.loadTrips(startTime,endTime,0.9);
			if(trips.isEmpty()){
				continue;
			}
			if(count>100){
				break;
			}
			no_trips+=trips.size();
			count++;
			//AverageTripCalc.LOGGER.info("DATE = "+startTime.toString("yyyy-MM-dd HH:mm:ss"));

			//AverageTripCalc.LOGGER.info("Summary Printing Started");  

			System.out.println(merge_writer_str);
		}
		AverageTripCalc.LOGGER.info(""+no_trips);
		
/*		merge_trips_writer.flush();
		merge_trips_coll.flush();
		merge_trips_writer.close();
		merge_trips_coll.close();*/
	}
}
