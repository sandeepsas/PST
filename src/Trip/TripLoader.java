/*
 * Loads all the precomputed data for Algorithm1
 * 
 * @Author Sandeep Sasidharan
 */
package Trip;

import java.io.BufferedReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.joda.time.DateTime;

import Graph.GraphNode;
import Graph.Pair;
import StartHere.RUNSETTINGS;
import Trip.TaxiTrip;

public class TripLoader {

	private Map<String, List<Pair<String,String>>> intrMap; 
	private Map<String, Pair<Double,Double>> vertexMap; 
	private Map<String, String> DTMap; 

	private DefaultDirectedWeightedGraph <GraphNode,DefaultWeightedEdge> gr_t; 


	/*Database Loader*/
	public List<KdTree.XYZPoint> listTime;
	//public Collection <KdTree.XYZPoint> listTime1;
	//For kDTree
	public static KdTree<KdTree.XYZPoint> kdtree;

	public TripLoader() throws IOException, ClassNotFoundException{

		//OSM K-dTree Loader
		listTime = new ArrayList<KdTree.XYZPoint>();
		//For kDTree
		kdtree = new KdTree<KdTree.XYZPoint>();

		//Constructor - Loads Intersection as HashMaps
		intrMap = new HashMap<String, List<Pair<String,String>>>();

		BufferedReader bfi = new BufferedReader(new FileReader(RUNSETTINGS.IntersectionMapFile));
		String si = new String();

		while((si=bfi.readLine())!=null &&
				(si.length()!=0) ){
			String[] split_readline = si.split(",");
			List<Pair<String,String>> intr_list = new ArrayList<Pair<String,String>>();
			for(int i=split_readline.length-2; i>=0;i--){
				String[] sub_split = split_readline[i].split("->");
				intr_list.add(new Pair<String,String>(sub_split[0].trim(),sub_split[1].trim()));
			}
			String[] key = split_readline[0].split("->");
			intrMap.put(key[0].trim(), intr_list);
		}
		bfi.close();
		//Load Vertex Map
		vertexMap = new HashMap<String, Pair<Double,Double>>();
		//bf = new BufferedReader(new FileReader("ObjectWarehouse/VertexMap_v1.csv"));
		BufferedReader bf = new BufferedReader(new FileReader(RUNSETTINGS.VertexMapFile));
		String s = new String();
		while((s=bf.readLine())!=null &&
				(s.length()!=0) ){
			String[] split_readline = s.split(",");
			String key = split_readline[0].trim();
			vertexMap.put(key, 
					new Pair<Double, Double>(Double.parseDouble(split_readline[1]),
							Double.parseDouble(split_readline[2])));
			listTime.add(new KdTree.XYZPoint(split_readline[0],Double.parseDouble(split_readline[1])
					,Double.parseDouble(split_readline[2]),0));
		}
		kdtree = new KdTree<KdTree.XYZPoint>(listTime);
		bf.close();
		// Load ShortestPath Map
		DTMap = new HashMap<String, String>();
		bf = new BufferedReader(new FileReader("ObjectWarehouse/DriveTimeMap/TravelTimeMap_v1.csv"));
		s = new String();
		bf.readLine();// header eliminate
		while((s=bf.readLine())!=null &&
				(s.length()!=0) ){
			String[] split_readline = s.split(",");
			String key = split_readline[0].trim();
			String val = split_readline[1].trim();
			DTMap.put(key,val );
		}

		bf.close();
		// Load the Graph
		//Construct Graph
		ObjectInputStream oos_graph_read = new ObjectInputStream(new FileInputStream(RUNSETTINGS.GraphObjectFile));
		gr_t = new  DefaultDirectedWeightedGraph <GraphNode,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		gr_t =  (DefaultDirectedWeightedGraph<GraphNode, DefaultWeightedEdge>) oos_graph_read.readObject();
		oos_graph_read.close();
	}

	public Map<String, List<Pair<String, String>>>  getIntrMap(){
		return this.intrMap;
	}
	public Map<String, Pair<Double, Double>>  getVertexMap(){
		return this.vertexMap;
	}
	public Map<String, String>  getDTMap(){
		return this.DTMap;
	}
	public DefaultDirectedWeightedGraph<GraphNode, DefaultWeightedEdge>  getGraph(){
		return this.gr_t;
	}
	public KdTree.XYZPoint getNNNode(KdTree.XYZPoint node){

		//Search for nearest vertex
		Collection<KdTree.XYZPoint> near_bys = kdtree.nearestNeighbourSearch(node,0.06);
		Iterator<KdTree.XYZPoint> near_bys_itr =
				near_bys.iterator();
		KdTree.XYZPoint elt = near_bys_itr.next();

		return elt;

	}

	public GraphNode nodeIDtoGraphNode(String nodeID) {
		// TODO Auto-generated method stub
		Pair<Double,Double> node_pos = vertexMap.get(nodeID.trim());
		GraphNode node = new GraphNode();
		node.setId(Long.parseLong(nodeID));
		node.setLat(node_pos.getL());
		node.setLon(node_pos.getR());
		return node;
	}
	public List<TaxiTrip> loadTrips(DateTime startTime, DateTime endTime, double frac) throws IOException {
		// TODO Auto-generated method stub
		List<TaxiTrip> trips = new ArrayList<TaxiTrip>();
		int total_no_passengers = 0;
		/*BufferedReader bf = new BufferedReader(new 
				FileReader("ObjectWarehouse/TripData/Manhattan/NonManhattanTrips.csv"));*/
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
			if(trip_start_time.compareTo(endTime)>0){
				break;
			}

		}
		//Extract 90% of the passengers
		Collections.shuffle(trips);
		int ctr_90 = 0;
		Iterator<TaxiTrip> trip_list_itr = trips.iterator();
		List<TaxiTrip> trips_90 = new ArrayList<TaxiTrip>();
		while(trip_list_itr.hasNext()){
			TaxiTrip next_trip = trip_list_itr.next();
			ctr_90 += next_trip.getPassengerCount();
			if(ctr_90<=Math.round(frac*total_no_passengers)){
				KdTree.XYZPoint dest_A = new KdTree.XYZPoint(next_trip.getMedallion(), 
						next_trip.getDropOffLat(), next_trip.getDropOffLon(), 0);
				GraphNode OSM_dest_A = this.getNNNode(dest_A).toGraphNode();
				next_trip.setDestNode(OSM_dest_A);
				trips_90.add(next_trip);
			}
		}
		bf.close();
		return trips_90;

	}
}
