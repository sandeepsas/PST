/*
 * 
 * @Author: Sandeep Sasidharan
 */
package Plot100s;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import Graph.Pair;
import Graph.ShareabilityGraph;
import PlotGenerators.Plot2;
import Trip.TaxiTrip;

public class PoolSizeGraphGen {
	
	public static final Logger LOGGER = Logger.getLogger(PoolSizeGraphGen.class.getName());
	public static void main (String[] args0) throws IOException, ClassNotFoundException{
		ObjectInputStream ios_graph_read = new 
				ObjectInputStream(new 
						FileInputStream("ObjectWarehouse/TripData/random10MinPools.obj"));
		List<List<TaxiTrip>> random_100_pools = new ArrayList<List<TaxiTrip>>();
		random_100_pools =   (List<List<TaxiTrip>>) ios_graph_read.readObject();
		String print_date_time = new String();
		int ctrr = 0;
		for(List<TaxiTrip> trips:random_100_pools){
			LOGGER.info("Trip set processing = "+ctrr);
			ctrr++;
			for(int i = 0 ; i < trips.size(); i ++){
				System.out.println(trips.get(i).getPickupDate());
			}
		}
	}

}
