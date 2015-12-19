/*
 * This class checks if a pair of trips are mergeable or not
 * 
 * @Author: Sandeep Sasidharan
 */
package StartHere;

import java.io.*;

import java.util.*;
import java.util.logging.Logger;

import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import Graph.*;
import Trip.*;

public class CheckTripMergeable {
	public static final Logger LOGGER = Logger.getLogger(CheckTripMergeable.class.getName());
	private static final String LINE_SEPARATOR = "\r\n";
	public static void main (String[] args0) throws IOException, ClassNotFoundException{

		PrintWriter merge_trips_writer = new PrintWriter(new File ("MergeableTrips_set_25.txt"));
		merge_trips_writer.println("Run started at "+ LocalDateTime.now() );

		// Read Trip between 2013-01-01 08:50:00 and 2013-01-01 08:55:00
		DateTime startTime = Constants.dt_formatter.parseDateTime("2013-01-01 10:00:00");
		DateTime endTime = Constants.dt_formatter.parseDateTime("2013-01-01 10:05:00");
		List<TaxiTrip>  trips = loadTrips(startTime,endTime);
		CheckTripMergeable.LOGGER.info("Total No of trips in the pool = "+trips.size());
		
		TripLoader tripLoader = new TripLoader();
		// Generate possible trip combos and populate merge-able trips
		List<Pair<TaxiTrip,TaxiTrip>>  dispatchList = new ArrayList<Pair<TaxiTrip,TaxiTrip>>();
		
		for(int i = 0 ; i < trips.size(); i ++){
			for(int j = i+1 ; j < trips.size(); j ++){
				TaxiTrip trip_A = trips.get(i);
				TaxiTrip trip_B = trips.get(j);
				if(trip_A.getPassengerCount()+trip_B.getPassengerCount()<=4)
					dispatchList.add(new Pair<TaxiTrip, TaxiTrip>(trip_A,trip_B));
			}
		}
		ShareabilityGraph sG = new ShareabilityGraph();
		List<Pair<TaxiTrip,TaxiTrip>> mergeable_trips = new ArrayList<Pair<TaxiTrip,TaxiTrip>>();

		for(int j = 0 ; j < dispatchList.size(); j ++){
			TaxiTrip trip_A = dispatchList.get(j).getL();
			TaxiTrip trip_B = dispatchList.get(j).getR();
			//CheckTripMergeable.LOGGER.info("Processing "+trip_A+"and "+trip_B);  
			if(sG.euclideanCheckSucess(trip_A,trip_B)) {
				if(sG.checkMergeable(trip_A,trip_B,tripLoader,merge_trips_writer)){
					mergeable_trips.add(new Pair<TaxiTrip,TaxiTrip>(trip_A,trip_B));
				}
			}
		}
		CheckTripMergeable.LOGGER.info("Summary Printing Started");  
		//Print Results
		merge_trips_writer.println("************************************* ");
		merge_trips_writer.println("************* TRIP SUMMARY ********** ");
		merge_trips_writer.println("*************************************");

		merge_trips_writer.println(startTime.toString("yyyy-MM-dd HH:mm:ss")+" and "+ endTime.toString("yyyy-MM-dd HH:mm:ss"));
		Iterator <Pair<TaxiTrip,TaxiTrip>> merge_list_itr = mergeable_trips.iterator();
/*		while(merge_list_itr.hasNext()){
			Pair<TaxiTrip,TaxiTrip> merge_pair = merge_list_itr.next();
			merge_trips_writer.println("\n"+merge_pair.getL()+" and "+merge_pair.getR());
		}*/
		/*Construct Shareability Graph*/
		sG.constructShareabilityGraph(mergeable_trips);
		
		int matches = sG.findMaxMatch(merge_trips_writer);
		merge_trips_writer.println("Total Number of Trips = "+trips.size());
		merge_trips_writer.println("No of merged Pairs = "+matches);
		
		merge_trips_writer.println("Run ended at "+ LocalDateTime.now() );
		merge_trips_writer.close();
	}


	public static List<TaxiTrip> loadTrips(DateTime startTime, DateTime endTime) throws IOException {
		// TODO Auto-generated method stub
		List<TaxiTrip> trips = new ArrayList<TaxiTrip>();
		BufferedReader bf = new BufferedReader(new FileReader("ObjectWarehouse/TripData/TripDataID.csv"));
		String s = new String();
		s = bf.readLine();
		while((s=bf.readLine())!=null &&
				(s.length()!=0) ){
			String[] split_readline = s.split(",");
			DateTime trip_start_time =  Constants.dt_formatter.parseDateTime(split_readline[6]);

			TaxiTrip trip = new TaxiTrip();

			if(trip_start_time.compareTo(startTime)>0 &&
					trip_start_time.compareTo(endTime)<=0 	){
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

				int paasenger_count = trip.getPassengerCount();
				if(paasenger_count<=4)
					trips.add(trip);
			}

		}
		bf.close();
		return trips;

	}

}


