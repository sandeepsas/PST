package PlotGenerators;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.joda.time.LocalDateTime;

import Trip.TaxiTrip;

public class Masterji {
	public static final Logger LOGGER = Logger.getLogger(Plot2.class.getName());
	public static void main (String[] args0) throws IOException, ClassNotFoundException{
		PrintStream out = new PrintStream(new FileOutputStream("Masterji.csv"));
		System.setOut(out);
		LOGGER.info("Run started at "+ LocalDateTime.now() );
		ObjectInputStream ios_graph_read = new 
				ObjectInputStream(new 
						FileInputStream("ObjectWarehouse/TripData/all5MinPools.obj"));
		List<List<TaxiTrip>> out_trips_100 = new ArrayList<List<TaxiTrip>>();
		out_trips_100 =   (List<List<TaxiTrip>>) ios_graph_read.readObject();
		List<List<TaxiTrip>> random_100_pools =  new ArrayList<List<TaxiTrip>>();
		Collections.shuffle(out_trips_100);
		int ctr = 0;
		for(List<TaxiTrip> trip_list_idx:out_trips_100){
			if(!trip_list_idx.isEmpty()){
				if(ctr<100){
					ctr++;
					random_100_pools.add(trip_list_idx);
				}else{
					break;
				}
			}
		}
		/////////////     START MERGE TEST   /////////////////////
		Collections.shuffle(random_100_pools);
		ObjectOutputStream oos_graph = new ObjectOutputStream(new FileOutputStream("ObjectWarehouse/TripData/Random100Pools.obj"));
		oos_graph.writeObject(random_100_pools);
		oos_graph.close();
	}

}
