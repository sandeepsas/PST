package Trip;


/*
 * Computation time as a function the length of the pool 
 * (5,6,7,8,9,10mins)  for a given ride-share fraction
 * 
 * @Author: Sandeep Sasidharan
 */
import java.io.*;

import java.util.*;
import java.util.logging.Logger;

import Trip.TaxiTrip;

public class TripObjMaker {
	public static final Logger LOGGER = Logger.getLogger(TripObjMaker.class.getName());

	public static void main (String[] args0) throws IOException, ClassNotFoundException{

		//PrintStream out = new PrintStream(new FileOutputStream("output_Bhandari.txt"));
		//System.setOut(out);

		ObjectInputStream ios_graph_read = new ObjectInputStream(new FileInputStream("ObjectWarehouse/TripData/all5MinPools.obj"));
		List<List<TaxiTrip>> out_trips_100 = new ArrayList<List<TaxiTrip>>();
		out_trips_100 =   (List<List<TaxiTrip>>) ios_graph_read.readObject();
		//Collections.shuffle(out_trips_100);
		ios_graph_read.close();
		
		List<List<TaxiTrip>> out_trips_100_10min = new ArrayList<List<TaxiTrip>>();
		for( int i=0;i<out_trips_100.size();i=i+2){
			List<TaxiTrip> list_1 = out_trips_100.get(i);
			List<TaxiTrip> list_2 = out_trips_100.get(i+1);
			List<TaxiTrip> temp_comb = new ArrayList<TaxiTrip> ();
			temp_comb.addAll(list_1);
			temp_comb.addAll(list_2);
			
			out_trips_100_10min.add(temp_comb);
		}

		System.out.println("Total Size = "+out_trips_100.size());
		System.out.println("Total Size = "+out_trips_100_10min.size());
		Collections.shuffle(out_trips_100_10min);
		
		List<List<TaxiTrip>> random_100_pools_10min = new ArrayList<List<TaxiTrip>>();
		int ctr = 0;
		for(int i=0;i<out_trips_100_10min.size();i++){
			if(ctr<100){
				if(!out_trips_100_10min.get(i).isEmpty()){
					random_100_pools_10min.add(out_trips_100_10min.get(i));
					ctr++;
				}
			}else{
				break;
			}
		}
		System.out.println("Total Size = "+random_100_pools_10min.size());
		
		ObjectOutputStream oos_graph = new ObjectOutputStream(new FileOutputStream("ObjectWarehouse/TripData/random10MinPools.obj"));
		oos_graph.writeObject(random_100_pools_10min);
		oos_graph.close();
	}
}