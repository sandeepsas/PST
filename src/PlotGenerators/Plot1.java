/*
 * Reduction in the number of trips (X) as a function of the % of passengers willing
 *  to ride-share (assume that max walk-time is Gaussian, with an avg of 5 mins;
 *  and max delay is Guassian with an avg of 10% of shortest path to destination).
 * 
 * @Author: Sandeep Sasidharan
 */
package PlotGenerators;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

public class Plot1 {
	public static final Logger LOGGER = Logger.getLogger(Plot1.class.getName());

	public static void main (String[] args0) throws IOException, ClassNotFoundException{

		PrintWriter merge_trips_writer = new PrintWriter(new File ("Plot1_v2_v2.txt"));
		PrintWriter COL_writer = new PrintWriter(new File ("Plot1_Collector_v1_v2.txt"));
		merge_trips_writer.println("\n \n  Run started at"+ LocalDateTime.now() );
		merge_trips_writer.println("\n \n********** TRIPS MERGEABLE ********** ");
		merge_trips_writer.println("************************************* \n");

		DateTime startTime = Constants.dt_formatter.parseDateTime("2013-01-17 10:00:00");
		DateTime endTime = Constants.dt_formatter.parseDateTime("2013-11-05 10:05:00");
		TripLoader tripLoader = new TripLoader();

		while(startTime.compareTo(endTime)<0){
			DateTime duration = startTime.plusMinutes(5);
			List<Pair<Integer,List<TaxiTrip>>>    trip_list = loadTripsSet(startTime,duration);
			// Read Trip between 2013-01-01 08:50:00 and 2013-01-01 08:55:00
			if(trip_list.isEmpty())
				continue;

			Plot1.LOGGER.info("Total No of trip Pairs = "+trip_list.size());
			Plot1.LOGGER.info("\n Processing date = "+startTime.toString("yyyy-MM-dd HH:mm:ss"));
			
			merge_trips_writer.println("Precomputed files loading completed at "+ LocalDateTime.now() );

			for (Pair<Integer,List<TaxiTrip>> trips_pair:trip_list){
				Plot1.LOGGER.info("Processing % = "+trips_pair.getL());
				List<TaxiTrip> trips = trips_pair.getR();
				Plot1.LOGGER.info("# TRIPS = "+trips.size());
				// Generate possible trip combos and populate merge-able trips
				//List<Pair<TaxiTrip,TaxiTrip>>  dispatchList = new ArrayList<Pair<TaxiTrip,TaxiTrip>>();
				ShareabilityGraph sG = new ShareabilityGraph();
				List<Pair<TaxiTrip,TaxiTrip>> mergeable_trips = new ArrayList<Pair<TaxiTrip,TaxiTrip>>();
				for(int i = 0 ; i < trips.size(); i ++){
					for(int j = i+1 ; j < trips.size(); j ++){
						TaxiTrip trip_A = trips.get(i);
						TaxiTrip trip_B = trips.get(j);
						if(trip_A.getPassengerCount()+trip_B.getPassengerCount()<=4){
							//dispatchList.add(new Pair<TaxiTrip, TaxiTrip>(trip_A,trip_B));
							Plot1.LOGGER.info("Processing "+trip_A+"and "+trip_B);  

							if(sG.euclideanCheckSucess(trip_A,trip_B)) {
								if(sG.checkMergeable(trip_A,trip_B,tripLoader,merge_trips_writer)){
									mergeable_trips.add(new Pair<TaxiTrip,TaxiTrip>(trip_A,trip_B));
								}
							}
						}
					}
				}
				/*			ShareabilityGraph sG = new ShareabilityGraph();
			List<Pair<TaxiTrip,TaxiTrip>> mergeable_trips = new ArrayList<Pair<TaxiTrip,TaxiTrip>>();

			for(int j = 0 ; j < dispatchList.size(); j ++){
				TaxiTrip trip_A = dispatchList.get(j).getL();
				TaxiTrip trip_B = dispatchList.get(j).getR();
				Plot1.LOGGER.info("Processing "+trip_A+"and "+trip_B);  
				if(sG.euclideanCheckSucess(trip_A,trip_B)) {
					if(sG.checkMergeable(trip_A,trip_B,tripLoader,merge_trips_writer)){
						mergeable_trips.add(new Pair<TaxiTrip,TaxiTrip>(trip_A,trip_B));
					}
				}
			}*/
				Plot1.LOGGER.info("Summary Printing Started");  
				//Print Results
				merge_trips_writer.println("\n************************************* ");
				merge_trips_writer.println("************* TRIP SUMMARY ********** ");
				//merge_trips_writer.println("************************************* \n");
				//merge_trips_writer.println("************* Time Interval ********* ");
				merge_trips_writer.println("************************************* ");
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
				int matches =  sG.findMaxMatch(merge_trips_writer);
				merge_trips_writer.println("************************************* ");
				//sG.useJgraphBlossom(mergeable_trips,merge_trips_writer);
				merge_trips_writer.println("Run ended at"+ LocalDateTime.now() );
				int reduced_trips = trips.size() - matches;
				COL_writer.println("Date = "+startTime.toString("yyyy-MM-dd HH:mm:ss")+", "
						+"#TRIPS =  "+trips.size()+", "
						+"%PERCENT = "+trips_pair.getL()+", "
						+"Matches = "+matches+", "
						+ "TRIPS REDUCED = "+reduced_trips);
				
			}
			startTime = startTime.plusDays(1);
		}

		merge_trips_writer.close();
		COL_writer.close();
	}


	public static List<Pair<Integer,List<TaxiTrip>>>  loadTripsSet(DateTime startTime, DateTime endTime) throws IOException {
		// TODO Auto-generated method stub
		List<TaxiTrip> trips = new ArrayList<TaxiTrip>();
		BufferedReader bf = new BufferedReader(new FileReader("TripData/TripDataID.csv"));
		String s = new String();
		s = bf.readLine();
		int total_no_passengers = 0;
		while((s=bf.readLine())!=null &&
				(s.length()!=0) ){
			String[] split_readline = s.split(",");
			//String[] time = split_readline[6].split("\\s+");
			//DateTime trip_start_time =  Constants.time_formatter.parseDateTime(time[1]);
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
				if(paasenger_count<=4){
					total_no_passengers+=paasenger_count;
					trips.add(trip);
				}
			}

		}
		bf.close();
		List<Pair<Integer,List<TaxiTrip>>> percent_wise_list = new ArrayList<Pair<Integer,List<TaxiTrip>>>();
		Iterator<TaxiTrip> trip_list_itr = trips.iterator();
		int ctr_90 = 0;
		List<TaxiTrip> trips_90 = new ArrayList<TaxiTrip>();
		List<TaxiTrip> trips_70 = new ArrayList<TaxiTrip>();
		List<TaxiTrip> trips_50 = new ArrayList<TaxiTrip>();
		List<TaxiTrip> trips_30 = new ArrayList<TaxiTrip>();
		List<TaxiTrip> trips_20 = new ArrayList<TaxiTrip>();
		List<TaxiTrip> trips_10 = new ArrayList<TaxiTrip>();

		while(trip_list_itr.hasNext()){
			TaxiTrip next_trip = trip_list_itr.next();
			ctr_90 += next_trip.getPassengerCount();
			if(ctr_90<=Math.round(0.90*total_no_passengers)){
				trips_90.add(next_trip);
			}
			if(ctr_90<=Math.round(0.70*total_no_passengers)){
				trips_70.add(next_trip);
			}
			if(ctr_90<=Math.round(0.50*total_no_passengers)){
				trips_50.add(next_trip);
			}
			if(ctr_90<=Math.round(0.30*total_no_passengers)){
				trips_30.add(next_trip);
			}
			if(ctr_90<=Math.round(0.20*total_no_passengers)){
				trips_20.add(next_trip);
			}
			if(ctr_90<=Math.round(0.10*total_no_passengers)){
				trips_10.add(next_trip);
			}
		}
		percent_wise_list.add(new Pair<Integer, List<TaxiTrip>>(90,trips_90));
		percent_wise_list.add(new Pair<Integer, List<TaxiTrip>>(70,trips_70));
		percent_wise_list.add(new Pair<Integer, List<TaxiTrip>>(50,trips_50));
		percent_wise_list.add(new Pair<Integer, List<TaxiTrip>>(30,trips_30));
		//percent_wise_list.add(new Pair<Integer, List<TaxiTrip>>(20,trips_20));
		percent_wise_list.add(new Pair<Integer, List<TaxiTrip>>(10,trips_10));
		return percent_wise_list;

	}

}


