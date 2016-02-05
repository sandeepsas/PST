/*
 * Computation time as a function the length of the pool 
 * (5,6,7,8,9,10mins)  for a given ride-share fraction
 * 
 * @Author: Sandeep Sasidharan
 */
package PlotGenerators;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import Graph.GraphNode;
import Graph.Pair;
import Graph.ShareabilityGraph;
import Trip.Constants;
import Trip.KdTree;
import Trip.TaxiTrip;
import Trip.TripLoader;

public class ComputeTimeTes {
	public static final Logger LOGGER = Logger.getLogger(Plot2.class.getName());

	public static void main (String[] args0) throws IOException, ClassNotFoundException{

		PrintStream out = new PrintStream(new FileOutputStream("output_plot_COMPTEST_7NEW.txt"));
		System.setOut(out);
		
		PrintWriter merge_trips_writer = new PrintWriter(new FileWriter ("COMP_TEST_7NEW.txt"));

		// Read Trip between 2013-01-01 08:50:00 and 2013-01-01 08:55:00
		DateTime startTime = Constants.dt_formatter.parseDateTime("2013-01-01 10:00:00");
		DateTime endTime = Constants.dt_formatter.parseDateTime("2013-02-01 10:05:00");
		TripLoader tripLoader = new TripLoader();
		
		while(startTime.compareTo(endTime)<0){
			
			long XstartTime = System.nanoTime();
			DateTime duration = startTime.plusMinutes(7);
			List<TaxiTrip>  trips = loadTrips(startTime,duration,tripLoader);
			ComputeTimeTes.LOGGER.info("DATE = "+startTime.toString("yyyy-MM-dd HH:mm:ss")+
					"->Total #trips = "+trips.size());

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
			int matches = sG.findMaxMatch(merge_trips_writer);
			
			long XendTime = System.nanoTime();

			long Xduration = (XendTime - XstartTime)/1000000;// milliseconds.
			Xduration = Xduration/1000;
			System.out.println(trips.size()+", "+matches+", "+Xduration);
			//System.out.println(startTime+", "+Xduration);
			//ComputeTimeTes.LOGGER.info("Execution Time = "+Xduration);
			System.out.println();
			startTime = startTime.plusDays(1);
		}
		merge_trips_writer.flush();
		merge_trips_writer.close();
	}


	public static List<TaxiTrip> loadTrips(DateTime startTime, DateTime endTime, 
			TripLoader tripLoader) throws IOException {
		// TODO Auto-generated method stub
		List<TaxiTrip> trips = new ArrayList<TaxiTrip>();
		int total_no_passengers = 0;
		BufferedReader bf = new BufferedReader(new 
				FileReader("ObjectWarehouse/TripData/TripDataID.csv"));
		String s = new String();
		s = bf.readLine();
		while((s=bf.readLine())!=null &&
				(s.length()!=0) ){
			String[] split_readline = s.split(",");
			DateTime trip_start_time =  Constants.dt_formatter.parseDateTime(split_readline[6]);

			TaxiTrip trip = new TaxiTrip();

			if(trip_start_time.compareTo(startTime)>0 &&
					trip_start_time.compareTo(endTime)<=0 	){
				int paasenger_count = Integer.parseInt(split_readline[8]);
				if(paasenger_count<4){
					total_no_passengers+=paasenger_count;
					trip = new TaxiTrip(split_readline[0],
							split_readline[6],
							split_readline[7],
							split_readline[8],
							split_readline[9],
							split_readline[10],
							split_readline[11],
							split_readline[12],
							split_readline[13],
							split_readline[14]);
					trips.add(trip);
				}
			}

		}
		//Extract 90% of the passengers
		//Collections.shuffle(trips);
		int ctr_90 = 0;
		Iterator<TaxiTrip> trip_list_itr = trips.iterator();
		List<TaxiTrip> trips_90 = new ArrayList<TaxiTrip>();
		while(trip_list_itr.hasNext()){
			TaxiTrip next_trip = trip_list_itr.next();
			ctr_90 += next_trip.getPassengerCount();
			if(ctr_90<=Math.round(0.90*total_no_passengers)){
				KdTree.XYZPoint dest_A = new KdTree.XYZPoint(next_trip.getMedallion(), 
						next_trip.getDropOffLat(), next_trip.getDropOffLon(), 0);
				GraphNode OSM_dest_A = tripLoader.getNNNode(dest_A).toGraphNode();
				next_trip.setDestNode(OSM_dest_A);
				trips_90.add(next_trip);
			}
		}
		bf.close();
		return trips_90;

	}

}


