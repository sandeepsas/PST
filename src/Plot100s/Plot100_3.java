/*
 * 
 * @Author: Sandeep Sasidharan
 */
package Plot100s;

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


public class Plot100_3 {
	public static final Logger LOGGER = Logger.getLogger(Plot100_3.class.getName());

	public static void main (String[] args0) throws IOException, ClassNotFoundException{

		PrintStream out = new PrintStream(new FileOutputStream("output_THEROREM3_8minPOOL_TTESTT.csv"));
		System.setOut(out);

		PrintWriter merge_trips_writer = new PrintWriter(new FileWriter ("Plot_3_THEROREM3_8minPOOL.txt"));
		PrintWriter merge_trips_coll= new PrintWriter(new FileWriter ("Plot_3_THEROREM3_8minPOOL.txt"));
		// Read Trip between 2013-01-01 08:50:00 and 2013-01-01 08:55:00
		//DateTime startTime = Constants.dt_formatter.parseDateTime("2013-01-01 10:00:00");
		//DateTime endTime = Constants.dt_formatter.parseDateTime("2013-12-31 22:00:00");
		TripLoader tripLoader = new TripLoader();
		ObjectInputStream ios_graph_read = new ObjectInputStream(new FileInputStream("ObjectWarehouse/TripData/DateSet_6min.obj"));
		List<Pair<DateTime,DateTime>> dates_100 = new ArrayList<Pair<DateTime,DateTime>>();
		dates_100 =   (List<Pair<DateTime, DateTime>>) ios_graph_read.readObject();
		//Collections.shuffle(dates_100);
		int count =0;
		StringBuilder merge_writer_str  = new StringBuilder();
		for(Pair<DateTime, DateTime> date_pair:dates_100){
			long XstartTime = System.nanoTime();
			merge_writer_str = new StringBuilder();
			DateTime startTime = date_pair.getL();
			//DateTime endTime = date_pair.getR();
			DateTime endTime = startTime.plusMinutes(8);

			List<TaxiTrip>  trips = tripLoader.loadTrips(startTime,endTime,0.9);
			if(trips.isEmpty()){
				continue;
			}
			if(count>=100){
				break;
			}
			Plot100_3.LOGGER.info("COUNT = "+count);
			count++;
			//Plot100_3.LOGGER.info("DATE = "+startTime.toString("yyyy-MM-dd HH:mm:ss"));
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
		//	Plot100_3.LOGGER.info("Summary Printing Started");  
			long XendTime = System.nanoTime();
			long Xduration = (XendTime - XstartTime)/1000000;// milliseconds.
			Xduration = Xduration/1000; //Sec

			/*Construct Shareability Graph*/
			sG.constructShareabilityGraph(mergeable_trips);
			int matches =   sG.findMaxMatch(merge_trips_writer);
			merge_writer_str.append(matches+", ");
			merge_writer_str.append(Xduration);
			System.out.println(merge_writer_str);
		}
		merge_trips_writer.flush();
		merge_trips_coll.flush();
		merge_trips_writer.close();
		merge_trips_coll.close();
	}
}
