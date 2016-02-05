/*
 * 
 * @Author: Sandeep Sasidharan
 */
package Plot100s;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.joda.time.LocalDateTime;

import Graph.Pair;
import Graph.ShareabilityGraph;
import PlotGenerators.Plot2;
import Trip.TaxiTrip;
import Trip.TripLoader;

public class Pool100_10min {
	public static final Logger LOGGER = Logger.getLogger(Pool100_10min.class.getName());

	public static void main (String[] args0) throws IOException, ClassNotFoundException{
		PrintStream out = new PrintStream(new FileOutputStream("out_P100_10minPoolSize_exe.csv"));
		System.setOut(out);
		LOGGER.info("Run started at "+ LocalDateTime.now() );
		ObjectInputStream ios_graph_read = new 
				ObjectInputStream(new 
						FileInputStream("ObjectWarehouse/TripData/random10MinPools.obj"));
		List<List<TaxiTrip>> random_100_pools = new ArrayList<List<TaxiTrip>>();
		random_100_pools =   (List<List<TaxiTrip>>) ios_graph_read.readObject();

		/////////////     START MERGE TEST   /////////////////////
		//Collections.shuffle(random_100_pools);
		PrintWriter merge_trips_writer = new PrintWriter(new FileWriter ("Plot100_10minPoolSize.csv"));
		String print_date_time = new String();
		TripLoader tripLoader = new TripLoader();
		int ctrr = 0;
		
		for(List<TaxiTrip> trips:random_100_pools){
			LOGGER.info("Trip set processing = "+ctrr);
			ctrr++;
			long XstartTime = System.nanoTime();
			ShareabilityGraph sG = new ShareabilityGraph();
			List<Pair<TaxiTrip,TaxiTrip>> mergeable_trips = new ArrayList<Pair<TaxiTrip,TaxiTrip>>();
			for(int i = 0 ; i < trips.size(); i ++){
				for(int j = i+1 ; j < trips.size(); j ++){
					TaxiTrip trip_A = trips.get(i);
					TaxiTrip trip_B = trips.get(j);
					print_date_time = trip_A.getPickupDate();
					if(trip_A.getPassengerCount()+trip_B.getPassengerCount()<=4){
						if(sG.checkMergeable(trip_A,trip_B,tripLoader,merge_trips_writer)){
							mergeable_trips.add(new Pair<TaxiTrip,TaxiTrip>(trip_A,trip_B));
						}
					}
				}
			}
			sG.constructShareabilityGraph(mergeable_trips);
			int matches =   sG.findMaxMatch(merge_trips_writer);
			int red_trips = trips.size() - matches;
			long XendTime = System.nanoTime();
			long Xduration = (XendTime - XstartTime)/1000000;// milliseconds.
			Xduration = Xduration/1000; //Sec
			System.out.println(print_date_time+", "+trips.size()+", "+matches+", "+red_trips+", "+Xduration);

		}
		LOGGER.info("Run Ended at "+ LocalDateTime.now() );
		merge_trips_writer.close();
		out.close();
	}
}
