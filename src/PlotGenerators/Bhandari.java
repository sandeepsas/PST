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

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.traverse.ClosestFirstIterator;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import Graph.GraphNode;
import Graph.Pair;
import Graph.ShareabilityGraph;
import StartHere.CheckTripMergeable;
import StartHere.RUNSETTINGS;
import Trip.Constants;
import Trip.KdTree;
import Trip.TaxiTrip;
import Trip.TripLoader;

public class Bhandari {
	public static final Logger LOGGER = Logger.getLogger(Plot2.class.getName());

	public static void main (String[] args0) throws IOException, ClassNotFoundException{

		PrintStream out = new PrintStream(new FileOutputStream("output_Bhandari.txt"));
		System.setOut(out);

		DateTime startTime = Constants.dt_formatter.parseDateTime("2013-01-01 10:00:00");
		DateTime endTime = Constants.dt_formatter.parseDateTime("2013-12-31 22:00:00");
		TripLoader tripLoader = new TripLoader();

		List<List<TaxiTrip>> trips_100 = new ArrayList<List<TaxiTrip>>();
		DateTime poolStartTime = startTime;
		
		while(poolStartTime.compareTo(endTime)<0){
			DateTime poolEndTime = poolStartTime.plusHours(12);
			while(poolStartTime.compareTo(poolEndTime)<0){

				DateTime duration = poolStartTime.plusMinutes(5);
				List<TaxiTrip>  trips = tripLoader.loadTrips(poolStartTime,duration,0.9);
				
				trips_100.add(trips);
				poolStartTime = poolStartTime.plusMinutes(5);

				//out.close();
			}
			LOGGER.info(poolStartTime.toString("yyyy-MM-dd HH:mm:ss"));
			poolStartTime = poolStartTime.plusHours(12);
		}

		System.out.println("Total Size = "+trips_100.size());
		
		ObjectOutputStream oos_graph = new ObjectOutputStream(new FileOutputStream("ObjectWarehouse/TripData/all5MinPools.obj"));
		oos_graph.writeObject(trips_100);
		oos_graph.close();
		
		
		ObjectInputStream ios_graph_read = new ObjectInputStream(new FileInputStream("ObjectWarehouse/TripData/all5MinPools.obj"));
		List<List<TaxiTrip>> out_trips_100 = new ArrayList<List<TaxiTrip>>();
		out_trips_100 =   (List<List<TaxiTrip>>) ios_graph_read.readObject();

		System.out.println("Total Size = "+out_trips_100.size());
		ios_graph_read.close();
	}
}


