/*
 * Reduction in the number of trips (X) as a function the length of the pool 
 * (5,6,7,8,9,10mins)  for a given ride-share fraction
 * 
 * @Author: Sandeep Sasidharan
 */
package PlotGenerators;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import Graph.Pair;
import Graph.ShareabilityGraph;
import Trip.Constants;
import Trip.TaxiTrip;
import Trip.TripLoader;

public class Plot_Speed {
	public static final Logger LOGGER = Logger.getLogger(Plot_Speed.class.getName());

	public static void main (String[] args0) throws IOException, ClassNotFoundException{

		PrintStream out = new PrintStream(new FileOutputStream("output_PLOTSPEED_50per.csv"));
		System.setOut(out);
		
		PrintWriter merge_trips_writer = new PrintWriter(new FileWriter ("Plot_PLOTSPEED_50per.txt"));
		PrintWriter merge_trips_coll= new PrintWriter(new FileWriter ("Plot_2_PLOTSPEED_50per.txt"));

		// Read Trip between 2013-01-01 08:50:00 and 2013-01-01 08:55:00
		DateTime startTime = Constants.dt_formatter.parseDateTime("2013-01-01 10:00:00");
		DateTime endTime = Constants.dt_formatter.parseDateTime("2013-02-01 10:05:00");
		TripLoader tripLoader = new TripLoader();
		StringBuilder merge_writer_str = new StringBuilder();
		while(startTime.compareTo(endTime)<0){
			merge_writer_str = new StringBuilder();
			DateTime duration = startTime.plusMinutes(5);
			List<TaxiTrip>  trips = tripLoader.loadTrips(startTime,duration,0.90);
			Plot_Speed.LOGGER.info("Total No of trips in the pool = "+trips.size());
			Plot_Speed.LOGGER.info("DATE = "+startTime.toString("yyyy-MM-dd HH:mm:ss"));
			//merge_writer_str.append(startTime.toString("yyyy-MM-dd")+", ");
			merge_writer_str.append(trips.size()+", ");
			//merge_trips_writer.println("Precomputed files loading completed at "+ LocalDateTime.now() );
			// Generate possible trip combos and populate merge-able trips
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

			/*Construct Shareability Graph*/
			sG.constructShareabilityGraph(mergeable_trips);
			int matches =   sG.findMaxMatch(merge_trips_writer);
			merge_writer_str.append(matches);
			merge_trips_coll.println(""+merge_writer_str);
			System.out.println(merge_writer_str);
			startTime = startTime.plusDays(1);
		}
		merge_trips_writer.flush();
		merge_trips_coll.flush();
		merge_trips_writer.close();
		merge_trips_coll.close();
	}



}


