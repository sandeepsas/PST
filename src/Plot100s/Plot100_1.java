/*
 * 
 * @Author: Sandeep Sasidharan
 */
package Plot100s;

import java.io.*;

import java.util.*;
import java.util.logging.Logger;

import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.joda.time.LocalDateTime;

import Graph.GraphNode;
import Graph.Pair;
import Graph.ShareabilityGraph;
import OSMProcessor.OsmConstants;
import Trip.KdTree;
import Trip.TaxiTrip;
import Trip.TripLoader;

public class Plot100_1 {
	public static final Logger LOGGER = Logger.getLogger(Plot100_1.class.getName());

	public static void main (String[] args0) throws IOException, ClassNotFoundException{
		PrintStream out = new PrintStream(new FileOutputStream("out_P100_P1_NONMHTT_3.csv"));
		System.setOut(out);
		LOGGER.info("Run started at "+ LocalDateTime.now() );
		ObjectInputStream ios_graph_read = new 
				ObjectInputStream(new 
						FileInputStream("ObjectWarehouse/TripData/Random100Pools.obj"));
		List<List<TaxiTrip>> random_100_pools = new ArrayList<List<TaxiTrip>>();
		random_100_pools =   (List<List<TaxiTrip>>) ios_graph_read.readObject();
		String print_date_time = new String();
		TripLoader tripLoader = new TripLoader();
		PrintWriter merge_trips_writer = new PrintWriter(new File ("Plot1_NEW.txt"));
		int ctrrt = 0;
		for(List<TaxiTrip> tripsy:random_100_pools){
			int total_no_passengers = 0;
			for(TaxiTrip i:tripsy){
				total_no_passengers+=i.getPassengerCount();
			}
			LOGGER.info("COUNT = "+ctrrt);
			ctrrt++;
			List<Pair<Integer,List<TaxiTrip>>> percent_wise_list = new ArrayList<Pair<Integer,List<TaxiTrip>>>();
			Iterator<TaxiTrip> trip_list_itr = tripsy.iterator();
			int ctr_90 = 0;
			List<TaxiTrip> trips_90 = tripsy;
			List<TaxiTrip> trips_70 = new ArrayList<TaxiTrip>();
			List<TaxiTrip> trips_50 = new ArrayList<TaxiTrip>();
			List<TaxiTrip> trips_30 = new ArrayList<TaxiTrip>();
			List<TaxiTrip> trips_20 = new ArrayList<TaxiTrip>();
			List<TaxiTrip> trips_10 = new ArrayList<TaxiTrip>();

			while(trip_list_itr.hasNext()){
				TaxiTrip next_trip = trip_list_itr.next();

				ctr_90 += next_trip.getPassengerCount();

				if(ctr_90<=Math.round((0.70/0.9)*total_no_passengers)){
					trips_70.add(next_trip);
				}
				if(ctr_90<=Math.round(0.50/0.9*total_no_passengers)){
					trips_50.add(next_trip);
				}
				if(ctr_90<=Math.round((0.30/0.9)*total_no_passengers)){
					trips_30.add(next_trip);
				}
				if(ctr_90<=Math.round((0.20/0.9)*total_no_passengers)){
					trips_20.add(next_trip);
				}
				if(ctr_90<=Math.round((0.10/0.9)*total_no_passengers)){
					trips_10.add(next_trip);
				}
			}
			percent_wise_list.add(new Pair<Integer, List<TaxiTrip>>(90,trips_90));
			percent_wise_list.add(new Pair<Integer, List<TaxiTrip>>(70,trips_70));
			percent_wise_list.add(new Pair<Integer, List<TaxiTrip>>(50,trips_50));
			percent_wise_list.add(new Pair<Integer, List<TaxiTrip>>(30,trips_30));
			percent_wise_list.add(new Pair<Integer, List<TaxiTrip>>(20,trips_20));
			percent_wise_list.add(new Pair<Integer, List<TaxiTrip>>(10,trips_10));


			for (Pair<Integer,List<TaxiTrip>> trips_pair:percent_wise_list){
				List<TaxiTrip> trips = trips_pair.getR();
				ShareabilityGraph sG = new ShareabilityGraph();
				List<Pair<TaxiTrip,TaxiTrip>> mergeable_trips = new ArrayList<Pair<TaxiTrip,TaxiTrip>>();
				for(int i = 0 ; i < trips.size(); i ++){
					for(int j = i+1 ; j < trips.size(); j ++){
						TaxiTrip trip_A = trips.get(i);
						TaxiTrip trip_B = trips.get(j);
						if(trip_A.getPassengerCount()+trip_B.getPassengerCount()<=4){
							if(sG.checkMergeable(trip_A,trip_B,tripLoader,merge_trips_writer)){
								print_date_time = trip_A.getPickupDate();
								mergeable_trips.add(new Pair<TaxiTrip,TaxiTrip>(trip_A,trip_B));
							}
						}
					}
				}
				sG.constructShareabilityGraph(mergeable_trips);

				int matches =  sG.findMaxMatch(merge_trips_writer);

				int reduced_trips = trips.size() - matches;
				
				/////CALCULATE VTT ********************///
				
				// Total VTT w/o RS
				/*double total_dist_wo_rs = 0;
				for(TaxiTrip trip_idx:trips){
					total_dist_wo_rs+=trip_idx.getTravelTime();
				}
				
				// Total VTT for Shared Service
				List<Pair<TaxiTrip,TaxiTrip>> unique_matches = sG.getUniqueSet();
				//Compute the VMT of Merged Unique Set
				double total_unique_dist = 0;
				for(Pair<TaxiTrip,TaxiTrip> unique_idx:unique_matches){
					
					GraphNode OSM_dest_A = unique_idx.getL().getDestNode();
					GraphNode OSM_dest_B = unique_idx.getR().getDestNode();
					
										
					double dist_A_B = new DijkstraShortestPath<GraphNode, DefaultWeightedEdge>(
							tripLoader.getGraph(), OSM_dest_A, OSM_dest_B).getPathLength();
					
					Map<String, String> dtMap = tripLoader.getDTMap();
					float driving_time_to_dest_A = Float.parseFloat(dtMap.get(""+OSM_dest_A.getId()));
					float driving_time_to_dest_B = Float.parseFloat(dtMap.get(""+OSM_dest_B.getId()));

					float travel_time_to_A_from_trip = unique_idx.getL().getTravelTime();
					float travel_time_to_B_from_trip = unique_idx.getR().getTravelTime();
					float travel_time_correction_ratio_A = travel_time_to_A_from_trip / driving_time_to_dest_A;
					float travel_time_correction_ratio_B = travel_time_to_B_from_trip / driving_time_to_dest_B;
					float travel_time_correction_ratio = (travel_time_correction_ratio_A + travel_time_correction_ratio_B) / 2;
					
					dist_A_B = dist_A_B*travel_time_correction_ratio;
					
					
					double trip_dist = travel_time_to_A_from_trip+ dist_A_B;
					total_unique_dist=total_unique_dist+trip_dist;
					trips.remove(unique_idx.getL());
					trips.remove(unique_idx.getR());
				}
				
				//Total VTT for Single Trips with RS
				double total_single_trip_dist = 0;
				for(TaxiTrip trip_idx:trips){
					total_single_trip_dist+=trip_idx.getTravelTime();
				}
				
				double slim_dist = total_single_trip_dist+total_unique_dist;
				double gain = total_dist_wo_rs-slim_dist;*/
				
				/*System.out.println(print_date_time+", "
						+trips.size()+", "
						+trips_pair.getL()+", "
						+matches+", "
						+reduced_trips+", "+gain);*/
				System.out.println(print_date_time+", "
						+trips.size()+", "
						+trips_pair.getL()+", "
						+matches+", "
						+reduced_trips);
			}
		}
		merge_trips_writer.close();
		out.close();
	}
}



