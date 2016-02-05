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


public class Plot4 {
	public static final Logger LOGGER = Logger.getLogger(Plot2.class.getName());

	public static void main (String[] args0) throws IOException, ClassNotFoundException{

		PrintStream out = new PrintStream(new FileOutputStream("output_Plot4_TEST.txt"));
		System.setOut(out);
		
		PrintWriter merge_trips_writer = new PrintWriter(new FileWriter ("Plot_4_TEST1.txt"));
		PrintWriter merge_trips_coll= new PrintWriter(new FileWriter ("Plot_4_coll_TEST1.txt"));
		merge_trips_writer.println("Run started at"+ LocalDateTime.now() );
		merge_trips_writer.println("\n********** TRIPS MERGEABLE ********** ");
		merge_trips_writer.println("************************************* \n");

		// Read Trip between 2013-01-01 08:50:00 and 2013-01-01 08:55:00
		DateTime startTime = Constants.dt_formatter.parseDateTime("2013-01-01 10:00:00");
		DateTime endTime = Constants.dt_formatter.parseDateTime("2013-01-06 10:05:00");
		TripLoader tripLoader = new TripLoader();
		StringBuilder merge_writer_str = new StringBuilder();
		while(startTime.compareTo(endTime)<0){
			merge_writer_str = new StringBuilder();
			DateTime duration = startTime.plusMinutes(5);
			List<TaxiTrip>  trips = loadTrips(startTime,duration);
			Plot2.LOGGER.info("Total No of trips in the pool = "+trips.size());
			Plot2.LOGGER.info("DATE = "+startTime.toString("yyyy-MM-dd HH:mm:ss"));
			merge_writer_str.append(startTime+", ");
			merge_writer_str.append(trips.size()+", ");
			//merge_trips_writer.println("Precomputed files loading completed at "+ LocalDateTime.now() );
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
				//Plot2.LOGGER.info("Processing "+trip_A+"and "+trip_B);  
				//if(sG.euclideanCheckSucess(trip_A,trip_B)) {
					if(sG.checkMergeable(trip_A,trip_B,tripLoader,merge_trips_writer)){
						mergeable_trips.add(new Pair<TaxiTrip,TaxiTrip>(trip_A,trip_B));
					}
				//}
			}
			Plot2.LOGGER.info("Summary Printing Started");  
			//Print Results
			/*merge_trips_writer.println("\n************************************* ");
			merge_trips_writer.println("************* TRIP SUMMARY ********** ");
			merge_trips_writer.println("************************************* \n");
			merge_trips_writer.println("************* Time Interval ********* ");
			merge_trips_writer.println("************************************* ");*/
			merge_trips_writer.println(startTime.toString("yyyy-MM-dd HH:mm:ss")+" and "+ duration.toString("yyyy-MM-dd HH:mm:ss"));
			merge_trips_writer.println("************************************* ");
			merge_trips_writer.println("Total Number of Trips = "+trips.size());
			merge_trips_writer.println("************************************* ");
			merge_trips_writer.println("\n ************************************* ");
			merge_trips_writer.println("Number of Mergeable Pairs = "+mergeable_trips.size());
			merge_trips_writer.println("************************************* ");
			Iterator <Pair<TaxiTrip,TaxiTrip>> merge_list_itr = mergeable_trips.iterator();
/*			while(merge_list_itr.hasNext()){
				Pair<TaxiTrip,TaxiTrip> merge_pair = merge_list_itr.next();
				merge_trips_writer.println("\n"+merge_pair.getL()+" and "+merge_pair.getR());
			}*/
			/*Construct Shareability Graph*/
			sG.constructShareabilityGraph(mergeable_trips);
			merge_trips_writer.println("\n ************************************* ");
			merge_trips_writer.println(" MAXIMUM MATCH PAIRS");
			merge_trips_writer.println("************************************* ");
			int matches =   sG.findMaxMatch(merge_trips_writer);
			merge_trips_writer.println("************************************* ");
			//sG.useJgraphBlossom(mergeable_trips,merge_trips_writer);
			merge_writer_str.append(matches+", ");
			merge_trips_writer.println("Run ended at"+ LocalDateTime.now() );
			startTime = startTime.plusDays(1);
			merge_trips_coll.println(""+merge_writer_str);
			System.out.println(merge_writer_str);
		}
		merge_trips_writer.flush();
		merge_trips_coll.flush();
		merge_trips_writer.close();
		merge_trips_coll.close();
	}


	public static List<TaxiTrip> loadTrips(DateTime startTime, DateTime endTime) throws IOException {
		// TODO Auto-generated method stub
		List<TaxiTrip> trips = new ArrayList<TaxiTrip>();
		int total_no_passengers = 0;
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
				if(paasenger_count<4){
					total_no_passengers+=paasenger_count;
					trips.add(trip);
				}
					
			}

		}
		int ctr_90 = 0;
		Iterator<TaxiTrip> trip_list_itr = trips.iterator();
		List<TaxiTrip> trips_90 = new ArrayList<TaxiTrip>();
		while(trip_list_itr.hasNext()){
			TaxiTrip next_trip = trip_list_itr.next();
			ctr_90 += next_trip.getPassengerCount();
			if(ctr_90<=Math.round(0.90*total_no_passengers)){
				trips_90.add(next_trip);
			}
		}
		bf.close();
		return trips_90;

	}

}

