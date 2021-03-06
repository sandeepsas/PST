/*
 * 
 * @Author: Sandeep Sasidharan
 */
package Generators;

import java.io.*;
import java.util.*;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.ClosestFirstIterator;
import org.joda.time.LocalDateTime;

import Graph.GraphNode;

public class IntersectionMapGenerator {
	public static void main(String[] args0) throws FileNotFoundException, IOException, ClassNotFoundException{

		PrintWriter DropOffPoints = new PrintWriter("ObjectWarehouse/WalkableIntersectionMap/IntrMap_0min.csv");

		System.out.println("De-Serialization started at"+ LocalDateTime.now() );

		ObjectInputStream oos_graph_read = new ObjectInputStream(new FileInputStream("ObjectWarehouse/GraphObjects/SluggingGraph.obj"));

		SimpleWeightedGraph <GraphNode,DefaultWeightedEdge> gr_t = new  
				SimpleWeightedGraph <GraphNode,DefaultWeightedEdge>(DefaultWeightedEdge.class);

		gr_t =  (SimpleWeightedGraph<GraphNode, DefaultWeightedEdge>) oos_graph_read.readObject();
		oos_graph_read.close();

		List<String>listIntersections = new ArrayList<String>();
		BufferedReader bfS = new BufferedReader(new FileReader("ObjectWarehouse/IntersectionVertexMap/NYCIntersectionMap.csv"));
		String s = new String();
		while((s=bfS.readLine())!=null &&
				(s.length()!=0) ){
			String[] split_readline = s.split(",");
			listIntersections.add(split_readline[0].trim());
		}

		Set<GraphNode> vertex_set = gr_t.vertexSet();

		Iterator<GraphNode> vertex_itr = vertex_set.iterator();
		int ctr = 0;
		while(vertex_itr.hasNext()){

			StringBuilder tt = new StringBuilder();

			GraphNode vertex = vertex_itr.next();
			ClosestFirstIterator<GraphNode, DefaultWeightedEdge> bfs= new 
					ClosestFirstIterator<GraphNode, DefaultWeightedEdge>(gr_t,vertex,0);

			GraphNode startNode = bfs.next();
			tt.append(startNode.getId()+"->"+bfs.getShortestPathLength(startNode)+", ");
			while(bfs.hasNext()){
				GraphNode bfs_next_node = bfs.next();
				if(nodeIsIntersection(bfs_next_node,listIntersections)){
					double walk_dist = bfs.getShortestPathLength(bfs_next_node);
					tt.append(bfs_next_node.getId()+"->"+walk_dist+", ");
				}
			}
			DropOffPoints.println(tt);
			System.out.println("Vertex Count -> "+ctr);
			ctr++;
		}

		DropOffPoints.close();
	}

	private static boolean nodeIsIntersection(GraphNode bfs_next_node,
			List<String>listIntersections) {
		// TODO Auto-generated method stub
		if(listIntersections.contains(""+bfs_next_node.getId())){
			return true;
		}
		return false;
	}
}
