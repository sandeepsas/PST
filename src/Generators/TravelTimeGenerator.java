/*
 * 
 * @Author: Sandeep Sasidharan
 */
package Generators;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Set;

import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.traverse.ClosestFirstIterator;

import Graph.GraphNode;

public class TravelTimeGenerator {
	public static void main(String[] args0) throws FileNotFoundException, IOException, ClassNotFoundException{

		PrintWriter shortPath = new PrintWriter("ObjectWarehouse/DriveTimeMap/TravelTimeMap_v1.csv");
		
		System.out.println("De-Serialization started at"+ LocalDateTime.now() );

		ObjectInputStream oos_graph_read = new ObjectInputStream(new FileInputStream("ObjectWarehouse/GraphObjects/SpeedLimtRoadGraph.obj"));

		//Construct Graph
		DefaultDirectedWeightedGraph <GraphNode,DefaultWeightedEdge> gr_t = new  
				DefaultDirectedWeightedGraph <GraphNode,DefaultWeightedEdge>(DefaultWeightedEdge.class);

		gr_t =  (DefaultDirectedWeightedGraph<GraphNode, DefaultWeightedEdge>) oos_graph_read.readObject();
		oos_graph_read.close();

		GraphNode hub_node = new GraphNode();
		hub_node.setLat(40.7743819);
		hub_node.setLon(-73.8729252);
		hub_node.setId(-343635);
		
		//-343635,40.7743819,-73.8729252
		
		Set<GraphNode> vertex_set = gr_t.vertexSet();
		
		Iterator<GraphNode> vertex_itr = vertex_set.iterator();
		int ctr = 0;
		while(vertex_itr.hasNext()){
			
			GraphNode vertex = vertex_itr.next();
			DijkstraShortestPath<GraphNode, DefaultWeightedEdge> dsp = new DijkstraShortestPath<GraphNode, DefaultWeightedEdge>(
					gr_t, hub_node, vertex);
			
			float driving_time_from_dropoff_B_to_dropoff_A = (float) dsp.getPathLength();
			shortPath.println(vertex.getId()+","+driving_time_from_dropoff_B_to_dropoff_A);
			System.out.println("Vertex Count -> "+ctr);
			ctr++;
		}
            
		shortPath.close();
	}

}
