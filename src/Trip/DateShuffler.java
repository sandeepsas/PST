/*
 * 
 * @Author: Sandeep Sasidharan
 */
package Trip;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.joda.time.DateTime;

import Graph.Pair;
import PlotGenerators.Plot2;

public class DateShuffler {
	public static final Logger LOGGER = Logger.getLogger(DateShuffler.class.getName());
	public static void main (String[] args0) throws IOException, ClassNotFoundException{
		PrintStream out = new PrintStream(new FileOutputStream("output_date_pairs.txt"));
		System.setOut(out);
		List<Pair<DateTime,DateTime>> date_list = new ArrayList<Pair<DateTime,DateTime>>();
		DateTime startTime = Constants.dt_formatter.parseDateTime("2013-01-01 10:00:00");
		DateTime endTime = Constants.dt_formatter.parseDateTime("2013-12-31 22:00:00");
		DateTime poolStartTime = startTime;
		while(poolStartTime.compareTo(endTime)<0){
			DateTime poolEndTime = poolStartTime.plusHours(12);
			while(poolStartTime.compareTo(poolEndTime)<0){

				DateTime duration = poolStartTime.plusMinutes(9);
				
				
				date_list.add(new Pair<DateTime, DateTime>(poolStartTime,duration));
				
				
				
				poolStartTime = poolStartTime.plusMinutes(9);

				//out.close();
			}
			LOGGER.info(poolStartTime.toString("yyyy-MM-dd HH:mm:ss"));
			poolStartTime = poolStartTime.plusHours(12);
		}
		ObjectOutputStream oos_graph = new ObjectOutputStream(new FileOutputStream("ObjectWarehouse/TripData/DateSet_9min.obj"));
		oos_graph.writeObject(date_list);
		oos_graph.close();

	}

}
