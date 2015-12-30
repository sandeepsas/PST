package Trip;

import org.joda.time.DateTime;

public class FilterFunctions {
	
	/*Distance calculator*/
	public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
		double earthRadius = 3958.75;
		double dLat = Math.toRadians(lat2-lat1);
		double dLng = Math.toRadians(lng2-lng1);
		double sindLat = Math.sin(dLat / 2);
		double sindLng = Math.sin(dLng / 2);
		double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
				* Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double dist = earthRadius * c;

		return dist;
	}
	
	public static boolean inLaG(double lat1, double lng1) {
		double lat2 = NYCConstants.LaG_lat;
		double lng2 = NYCConstants.LaG_lng;
		double earthRadius = 3958.75;
		double dLat = Math.toRadians(lat2-lat1);
		double dLng = Math.toRadians(lng2-lng1);
		double sindLat = Math.sin(dLat / 2);
		double sindLng = Math.sin(dLng / 2);
		double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
				* Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double dist = earthRadius * c;
		
		if(dist<=0.5)
			return true;
		else
			return false;

	}
	public static boolean inLaGRange(double lat1, double lng1) {
		double lat2 = NYCConstants.LaG_lat;
		double lng2 = NYCConstants.LaG_lng;
		double earthRadius = 3958.75;
		double dLat = Math.toRadians(lat2-lat1);
		double dLng = Math.toRadians(lng2-lng1);
		double sindLat = Math.sin(dLat / 2);
		double sindLng = Math.sin(dLng / 2);
		double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
				* Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double dist = earthRadius * c;
		
		if(dist<75)
			return true;
		else
			return false;

	}
	
	public static boolean inNYBoundingBox(double lat1, double lng1){
		double north_lat = 40.915256;
		double south_lat = 40.496044;
		double east_lon = -73.700272 ;
		double west_lon = -74.255735;
		
		boolean lat_check = (lat1 <north_lat && lat1 >south_lat);
		boolean lon_check = (lng1 >west_lon && lng1 <east_lon);
		
		return (lat_check && lon_check);
		
	}
	public static boolean inManhattanBoundingBox(double lat1, double lng1){
		double north_lat = 40.882214;
		double south_lat = 40.680396;
		double east_lon = -73.907000 ;
		double west_lon = -74.047285;
		
		boolean lat_check = (lat1 <north_lat && lat1 >south_lat);
		boolean lon_check = (lng1 >west_lon && lng1 <east_lon);
		
		return (lat_check && lon_check);
		
	}
/*For data integrity*/
	
	public static boolean isMedallion(String medallion)
	{
		if(medallion.length()!=10)
		{
			return false;
		}
		return true;
	}
	public static boolean isLatitude(double latitude)
	{
		if(latitude == 0)
		{
			//Runner.LOGGER.info("Latitude = 0, Skipped!");
			return false;
		}
		return true;
	}
	public static boolean isLongitude(double longitude)
	{
		if(longitude == 0)
		{
			//Runner.LOGGER.info("Longitude = 0, Skipped!");
			return false;
		}
		return true;
	}
	public static boolean isTripTime(double trip_time_in_secs)
	{
		if(trip_time_in_secs == 0)
		{
			//Runner.LOGGER.info("trip_time_in_secs = 0, Check and Skip!");
			return false;
		}
		if(trip_time_in_secs < 10)
		{
			//Runner.LOGGER.info("trip_time_in_secs"+trip_time_in_secs+", Check and Skip!");
			return false;
		}
		return true;
	}
	
	public static boolean isTripDistance(double trip_distance)
	{
		if(trip_distance == 0)
		{
			//Runner.LOGGER.info("trip_distance = 0, Check and Skip!");
			return false;
		}
		return true;
	}
	
	public static boolean isWeekday(String dateTime)
	{
		DateTime f_date = NYCConstants.dt_formatter.parseDateTime(dateTime);
		int dayOfWeek = f_date.getDayOfWeek();
		
		if(dayOfWeek>5)
			return false;
		
		return true;
	}
}
