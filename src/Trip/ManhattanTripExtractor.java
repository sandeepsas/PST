package Trip;

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.joda.time.LocalDateTime;

public class ManhattanTripExtractor {
	public static void main(String[] args) throws IOException {

		System.out.println("Run started at"+ LocalDateTime.now() );
		PrintWriter manhattan = new PrintWriter("TripData/ManhattanTrips.csv");
		PrintWriter non_manhattan = new PrintWriter("TripData/NonManhattanTrips.csv");

		BufferedReader bf = new BufferedReader(new FileReader("TripData/TripData.csv"));
		String s = new String();
		s = bf.readLine();
		while((s=bf.readLine())!=null &&
				(s.length()!=0) ){
			String[] split_readline = s.split(",");

			double drop_lat = Double.parseDouble(split_readline[13]);
			double drop_lon = Double.parseDouble(split_readline[12]);
			if(FilterFunctions.inManhattanBoundingBox(drop_lat, drop_lon)){
				manhattan.println(s);
			}else{
				non_manhattan.println(s);
			}


		}
		bf.close();
		manhattan.close();
		non_manhattan.close();
	}

}
