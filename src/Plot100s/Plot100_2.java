package Plot100s;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.joda.time.LocalDateTime;

import Graph.Pair;
import Graph.ShareabilityGraph;
import PlotGenerators.Plot2;
import Trip.TaxiTrip;
import Trip.TripLoader;

public class Plot100_2 {
	public static final Logger LOGGER = Logger.getLogger(Plot2.class.getName());

	public static void main (String[] args0) throws IOException, ClassNotFoundException{
		PrintStream out = new PrintStream(new FileOutputStream("out_P100_p2.csv"));
		System.setOut(out);
		LOGGER.info("Run started at "+ LocalDateTime.now() );
		ObjectInputStream ios_graph_read = new 
				ObjectInputStream(new 
						FileInputStream("ObjectWarehouse/TripData/all5MinPools.obj"));
		List<List<TaxiTrip>> out_trips_100 = new ArrayList<List<TaxiTrip>>();
		out_trips_100 =   (List<List<TaxiTrip>>) ios_graph_read.readObject();
		List<List<TaxiTrip>> random_100_pools =  new ArrayList<List<TaxiTrip>>();
		Collections.shuffle(out_trips_100);
		int ctr = 0;
		for(List<TaxiTrip> trip_list_idx:out_trips_100){
			if(!trip_list_idx.isEmpty()){
				if(ctr<100){
					ctr++;
					random_100_pools.add(trip_list_idx);
				}else{
					break;
				}
			}
		}
		/////////////     START MERGE TEST   /////////////////////
		Collections.shuffle(random_100_pools);
		PrintWriter merge_trips_writer = new PrintWriter(new FileWriter ("Plot100_P2.csv"));
		String print_date_time = new String();
		TripLoader tripLoader = new TripLoader();
		for(List<TaxiTrip> trips:random_100_pools){
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
			System.out.println(print_date_time+", "+trips.size()+", "+matches+", "+red_trips);

		}
		LOGGER.info("Run Ended at "+ LocalDateTime.now() );
		merge_trips_writer.close();
	}
}
