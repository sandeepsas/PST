/*
 * 
 * @Author: Sandeep Sasidharan
 */
package Generators;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.joda.time.LocalDateTime;

public class TripIDGenerator {
	public static void main(String[] args) throws IOException {

		System.out.println("Run started at"+ LocalDateTime.now() );
		PrintWriter trip_ID = new PrintWriter("ObjectWarehouse/TripData/Manhattan/NonManhattanTripsID.csv");

		BufferedReader bf = new BufferedReader(new FileReader("ObjectWarehouse/TripData/Manhattan/NonManhattanTrips.csv"));
		String s = new String();
		s = bf.readLine();
		long id_gen = 1000000000;
		while((s=bf.readLine())!=null &&
				(s.length()!=0) ){
			id_gen++;
			trip_ID.println(id_gen+","+s);
		}
		bf.close();
		trip_ID.close();

	}

}
