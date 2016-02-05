/*
 * 
 * @Author: Sandeep Sasidharan
 */
/*
 * Reduction in the number of trips as a function the length 
 * of the average delay tolerated (5%-15% of length of shortest path)
 * */
package PlotGenerators;


import java.io.*;

import java.util.*;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import Graph.Pair;
import Graph.ShareabilityGraph;
import Trip.*;


public class Plot3 {
	public static final Logger LOGGER = Logger.getLogger(Plot2.class.getName());

	public static void main (String[] args0) throws IOException, ClassNotFoundException{

		PrintStream out = new PrintStream(new FileOutputStream("output_Plot3_15per.txt"));
		System.setOut(out);

		PrintWriter merge_trips_writer = new PrintWriter(new FileWriter ("Plot_3_8NEW.txt"));
		PrintWriter merge_trips_coll= new PrintWriter(new FileWriter ("Plot_3_12NEW1.txt"));
		// Read Trip between 2013-01-01 08:50:00 and 2013-01-01 08:55:00
		DateTime startTime = Constants.dt_formatter.parseDateTime("2013-01-01 10:00:00");
		DateTime endTime = Constants.dt_formatter.parseDateTime("2013-02-01 10:05:00");
		TripLoader tripLoader = new TripLoader();
		StringBuilder merge_writer_str = new StringBuilder();
		while(startTime.compareTo(endTime)<0){
			merge_writer_str = new StringBuilder();
			DateTime duration = startTime.plusMinutes(5);
			List<TaxiTrip>  trips = tripLoader.loadTrips(startTime,duration,0.9);
			Plot2.LOGGER.info("DATE = "+startTime.toString("yyyy-MM-dd HH:mm:ss"));
			merge_writer_str.append(startTime+", ");
			merge_writer_str.append(trips.size()+", ");
			//merge_trips_writer.println("Precomputed files loading completed at "+ LocalDateTime.now() );
			
			ShareabilityGraph sG = new ShareabilityGraph();
			List<Pair<TaxiTrip,TaxiTrip>> mergeable_trips = new ArrayList<Pair<TaxiTrip,TaxiTrip>>();
			
			for(int i = 0 ; i < trips.size(); i ++){
				for(int j = i+1 ; j < trips.size(); j ++){
					TaxiTrip trip_A = trips.get(i);
					TaxiTrip trip_B = trips.get(j);
					if(trip_A.getPassengerCount()+trip_B.getPassengerCount()<=4){
						if(sG.checkMergeable(trip_A,trip_B,tripLoader,merge_trips_writer)){
							mergeable_trips.add(new Pair<TaxiTrip,TaxiTrip>(trip_A,trip_B));
						}
					}
				}
			}
			Plot3.LOGGER.info("Summary Printing Started");  

			/*Construct Shareability Graph*/
			sG.constructShareabilityGraph(mergeable_trips);
			int matches =   sG.findMaxMatch(merge_trips_writer);
			merge_writer_str.append(matches+", ");
			startTime = startTime.plusDays(1);
			System.out.println(merge_writer_str);
		}
		merge_trips_writer.flush();
		merge_trips_coll.flush();
		merge_trips_writer.close();
		merge_trips_coll.close();
	}
}