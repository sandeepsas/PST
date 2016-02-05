/*
 * 
 * @Author: Sandeep Sasidharan
 */
package Graph;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.EdmondsBlossomShrinking;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;

import MaximumMatching.EdmondsMatching;
import MaximumMatching.MUndirectedGraph;
import StartHere.RUNSETTINGS;
import Trip.FilterFns;
import Trip.TaxiTrip;
import Trip.TripLoader;

public class ShareabilityGraph {
	List<DefaultWeightedEdge> shareable_graph_edges;
	List<Pair<TaxiTrip,TaxiTrip>> unique_set;

	MUndirectedGraph uNshareGraph;

	public ShareabilityGraph() {
		shareable_graph_edges = new ArrayList<DefaultWeightedEdge>();
		new ArrayList <Pair<TaxiTrip,TaxiTrip>>();
		uNshareGraph = new MUndirectedGraph<TaxiTrip>();
	}

	public boolean checkMergeable(TaxiTrip trip_A, TaxiTrip trip_B, TripLoader tripLoader,
			PrintWriter merge_trips_writer) throws ClassNotFoundException, IOException {


		// Load the maps
		Map<String, List<Pair<String, String>>> intrMap = tripLoader.getIntrMap();
		Map<String, String> dtMap = tripLoader.getDTMap();
		DefaultDirectedWeightedGraph<GraphNode, DefaultWeightedEdge> gr_t = tripLoader.getGraph();

		GraphNode OSM_dest_A = trip_A.getDestNode();
		GraphNode OSM_dest_B = trip_B.getDestNode();

		float driving_time_to_dest_A = Float.parseFloat(dtMap.get(""+OSM_dest_A.getId()));
		float driving_time_to_dest_B = Float.parseFloat(dtMap.get(""+OSM_dest_B.getId()));

		float travel_time_to_A_from_trip = (float) trip_A.getTravelTime();
		float travel_time_to_B_from_trip = (float) trip_B.getTravelTime();
		float travel_time_correction_ratio_A = travel_time_to_A_from_trip / driving_time_to_dest_A;
		float travel_time_correction_ratio_B = travel_time_to_B_from_trip / driving_time_to_dest_B;
		float travel_time_correction_ratio = (travel_time_correction_ratio_A + travel_time_correction_ratio_B) / 2;

		travel_time_correction_ratio =(float) (travel_time_correction_ratio*RUNSETTINGS.PERCENTAGE_MAX_SPEED);
		//travel_time_correction_ratio = (float) (1/RUNSETTINGS.PERCENTAGE_MAX_SPEED);

		// Update the driving times to destinations
		driving_time_to_dest_A = driving_time_to_dest_A * travel_time_correction_ratio;
		driving_time_to_dest_B = driving_time_to_dest_B * travel_time_correction_ratio;

		float max_delay_trip_A = (float) ((driving_time_to_dest_A) * RUNSETTINGS.PERCENTAGE_TRIP_DELAY); 
		float max_delay_trip_B = (float) ((driving_time_to_dest_B) * RUNSETTINGS.PERCENTAGE_TRIP_DELAY); 


		//*****************************EUCLIDEAN TEST *************************************************
		//Check if DT<WT
		// TODO Auto-generated method stub
		//Calculate by Distance/speed method(60mph) travel time in mins. So 60 cancels.
		float time_from_H_to_A = (float) (travel_time_correction_ratio*FilterFns.inLaGDist(trip_A.getDropOffLat(), trip_A.getDropOffLon()));
		float time_from_A_to_B = (float) (travel_time_correction_ratio*FilterFns.distFrom(trip_A.getDropOffLat(),
				trip_A.getDropOffLon(),trip_B.getDropOffLat(), trip_B.getDropOffLon()));
		float shortest_time_from_H_to_B = Float.parseFloat(dtMap.get(""+OSM_dest_B.getId()));
		
		float shortest_time_from_H_to_A = Float.parseFloat(dtMap.get(""+OSM_dest_A.getId()));
		
		float lhs_EU_TH3 = (float) (shortest_time_from_H_to_A + time_from_A_to_B -(2*RUNSETTINGS.MAX_WALK_TIME)- shortest_time_from_H_to_B) ;



		float lhs_EU_TH = (float) (time_from_H_to_A + time_from_A_to_B -(2*RUNSETTINGS.MAX_WALK_TIME)- shortest_time_from_H_to_B) ;
		if(lhs_EU_TH3 > RUNSETTINGS.PERCENTAGE_TRIP_DELAY*shortest_time_from_H_to_B){
			//CheckTripMergeable.LOGGER.info("Euclidean Rejected ");
			//System.out.println("Outer Euclidean Rejected ");
			return false;
		}
		//*****************************EUCLIDEAN TEST *************************************************

		DijkstraShortestPath<GraphNode, DefaultWeightedEdge> dsp_dest = new DijkstraShortestPath<GraphNode, DefaultWeightedEdge>(
				gr_t, OSM_dest_A, OSM_dest_B);
		float driving_time_A_B = (float) dsp_dest.getPathLength();
		driving_time_A_B = driving_time_A_B* travel_time_correction_ratio;
		float lhs_rej = driving_time_to_dest_A+driving_time_A_B-driving_time_to_dest_B;
		if(lhs_rej < max_delay_trip_B){
			//System.out.println("Step 1 Rejection");
			return true;
		}

		// Drop-off points compuatations
		List<Pair<String, String>> dropOffPoints_A = intrMap.get(""+OSM_dest_A.getId());
		List<Pair<String, String>> dropOffPoints_B = intrMap.get(""+OSM_dest_B.getId());
		Iterator<Pair<String, String>> d_A_itr = dropOffPoints_A.iterator();
		List<Pair<String, String>> possible_dropoffs_A = new ArrayList<Pair<String, String>>();

		while (d_A_itr.hasNext()) {

			Pair<String, String> dropoff_pair = d_A_itr.next();
			String dropoff_node_id = dropoff_pair.getL();
			String walk_time = dropoff_pair.getR();
			float driving_time_to_dropoff = travel_time_correction_ratio*Float.parseFloat(dtMap.get(dropoff_node_id.trim()));
			float walking_time_to_dest_A = Float.parseFloat(walk_time);
			float lhs = driving_time_to_dropoff + walking_time_to_dest_A - driving_time_to_dest_A;

			if (lhs <= max_delay_trip_A) {
				possible_dropoffs_A.add(dropoff_pair);
			}

		}
		// Constraints for Trip B

		Iterator<Pair<String, String>> d_B_itr = dropOffPoints_B.iterator();

		while (d_B_itr.hasNext()) {
			Pair<String, String> dropoff_B_pair = d_B_itr.next();
			String walk_time = dropoff_B_pair.getR();
			float walking_time_to_dest_B = Float.parseFloat(walk_time);
			// Iterate through posssible ddrop offs for trip A
			Iterator<Pair<String, String>> pdropoff_dest_A = possible_dropoffs_A.iterator();
			GraphNode drop_B = tripLoader.nodeIDtoGraphNode(dropoff_B_pair.getL());
			while (pdropoff_dest_A.hasNext()) {
				Pair<String, String> dropoff_A_pair = pdropoff_dest_A.next();

				GraphNode drop_A = tripLoader.nodeIDtoGraphNode(dropoff_A_pair.getL());

				//PST Check - No need to divide by speed = 60 because it cancels with mins conversion
				double eu_DT_i_j = travel_time_correction_ratio*(FilterFns.distFrom(drop_A.getLat(), drop_A.getLon(), drop_B.getLat(), drop_B.getLon()));
				double pst_lhs = driving_time_to_dest_A+eu_DT_i_j+walking_time_to_dest_B-driving_time_to_dest_B;

				if(pst_lhs > max_delay_trip_B){
					continue;
				}
				DijkstraShortestPath<GraphNode, DefaultWeightedEdge> dsp = new DijkstraShortestPath<GraphNode, DefaultWeightedEdge>(
						gr_t, drop_A, drop_B);
				float driving_time_from_dropoff_B_to_dropoff_A = (float) dsp.getPathLength();
				driving_time_from_dropoff_B_to_dropoff_A = driving_time_from_dropoff_B_to_dropoff_A*travel_time_correction_ratio;

				float lhs = driving_time_to_dest_A+driving_time_from_dropoff_B_to_dropoff_A
						+ walking_time_to_dest_B - driving_time_to_dest_B;
				if (lhs <= max_delay_trip_B) {
					return true;
				}
			}
		}
		return false;
	}

	public int findMaxMatch(PrintWriter merge_trips_writer) throws IOException {
		// TODO Auto-generated method stub

		MUndirectedGraph<TaxiTrip> max_match_graph = EdmondsMatching.maximumMatching(this.uNshareGraph);
		Iterator<TaxiTrip> t = max_match_graph.iterator();

		unique_set = new ArrayList <Pair<TaxiTrip,TaxiTrip>>();
		while(t.hasNext()){
			TaxiTrip elt = t.next();
			Set<TaxiTrip> eges = max_match_graph.edgesFrom(elt);
			if(!eges.isEmpty()){
				TaxiTrip edge = eges.iterator().next();
				//merge_trips_writer.println(elt+"->"+edge);
				if(!unique_set.contains(new Pair<TaxiTrip, TaxiTrip>(edge,elt)))
					unique_set.add(new Pair<TaxiTrip, TaxiTrip>(elt,edge));
			}
		}
		return unique_set.size();
	}
	public List<Pair<TaxiTrip,TaxiTrip>> getUniqueSet(){
		return this.unique_set;
	}

	public void constructShareabilityGraph(List<Pair<TaxiTrip, TaxiTrip>> mergeable_pair_list) {
		// TODO Auto-generated method stub
		Iterator<Pair<TaxiTrip, TaxiTrip>> obj_list_itr = mergeable_pair_list.iterator();

		while (obj_list_itr.hasNext()) {
			Pair<TaxiTrip, TaxiTrip> trip_pair = obj_list_itr.next();

			TaxiTrip trip_A = trip_pair.getL();
			TaxiTrip trip_B = trip_pair.getR();

			this.uNshareGraph.addNode(trip_A);
			this.uNshareGraph.addNode(trip_B);
			this.uNshareGraph.addEdge(trip_A, trip_B);
		}

	}
}
