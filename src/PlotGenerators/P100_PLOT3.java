/*package PlotGenerators;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import Graph.Pair;
import Graph.ShareabilityGraph;
import Trip.Constants;
import Trip.FilterFns;
import Trip.TaxiTrip;
import Trip.TripLoader;

public class P100_PLOT3 {

	public static void main (String[] args0) throws IOException, ClassNotFoundException{

		PrintStream out = new PrintStream(new FileOutputStream("out_P100csv"));
		System.setOut(out);

		PrintWriter merge_trips_writer = new PrintWriter(new FileWriter ("Plot_2_0min_v1.txt"));
		PrintWriter merge_trips_coll= new PrintWriter(new FileWriter ("Plot_2_coll0min_v1.txt"));

		// Read Trip between 2013-01-01 08:50:00 and 2013-01-01 08:55:00

		TripLoader tripLoader = new TripLoader();
		StringBuilder merge_writer_str = new StringBuilder();
		while(startTime.compareTo(endTime)<0){
			if(!FilterFns.isWeekday(startTime)){
				startTime = startTime.plusDays(1);
				continue;
			}
			merge_writer_str = new StringBuilder();
			DateTime duration = startTime.plusMinutes(5);
			List<TaxiTrip>  trips = tripLoader.loadTrips(startTime,duration,0.90);
			Plot2.LOGGER.info("DATE = "+startTime.toString("yyyy-MM-dd HH:mm:ss"));
			merge_writer_str.append(trips.size()+", ");
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

			Construct Shareability Graph
			sG.constructShareabilityGraph(mergeable_trips);
			int matches =   sG.findMaxMatch(merge_trips_writer);
			merge_writer_str.append(matches);
			System.out.println(merge_writer_str);
			startTime = startTime.plusDays(1);
		}
		merge_trips_writer.flush();
		merge_trips_coll.flush();
		merge_trips_writer.close();
		merge_trips_coll.close();
	}

}
*/