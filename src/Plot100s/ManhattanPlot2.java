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

import org.joda.time.DateTime;

import Graph.Pair;
import Graph.ShareabilityGraph;
import Trip.TaxiTrip;
import Trip.TripLoader;

public class ManhattanPlot2 {
	public static final Logger LOGGER = Logger.getLogger(ManhattanPlot2.class.getName());

	public static void main (String[] args0) throws IOException, ClassNotFoundException{

		PrintStream out = new PrintStream(new FileOutputStream("out_MANHATTAN_8WalkTime.csv"));
		System.setOut(out);
		PrintWriter merge_trips_writer = new PrintWriter(new FileWriter ("Plot_3_8Walk11.txt"));

		TripLoader tripLoader = new TripLoader();
		ObjectInputStream ios_graph_read = new ObjectInputStream(new FileInputStream("ObjectWarehouse/TripData/DateSet_6min.obj"));
		List<Pair<DateTime,DateTime>> dates_100 = new ArrayList<Pair<DateTime,DateTime>>();
		dates_100 =   (List<Pair<DateTime, DateTime>>) ios_graph_read.readObject();
		Collections.shuffle(dates_100);
		int count=0;
		double awesome = 0;
		StringBuilder merge_writer_str  = new StringBuilder();
		for(Pair<DateTime, DateTime> date_pair:dates_100){
			long XstartTime = System.nanoTime();
			merge_writer_str = new StringBuilder();
			DateTime startTime = date_pair.getL();
			DateTime endTime = startTime.plusMinutes(5);


			List<TaxiTrip>  trips = tripLoader.loadTrips(startTime,endTime,0.9);
			
			if(trips.size()==0){
				continue;
			}

			if(trips.isEmpty()){
				continue;
			}
			if(count>100){
				break;
			}
			count++;
			merge_writer_str.append(startTime+", ");
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
			/*Construct Shareability Graph*/
			sG.constructShareabilityGraph(mergeable_trips);
			int matches =   sG.findMaxMatch(merge_trips_writer);
			merge_writer_str.append(matches+", ");
			int reduced_trips = trips.size() - matches;
			merge_writer_str.append(reduced_trips+", ");

			double result = 100-((reduced_trips/trips.size())*100);

			awesome+=result;
			merge_writer_str.append(awesome);
			System.out.println(merge_writer_str);
		}

	}
}
