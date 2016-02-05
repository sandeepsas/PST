package StartHere;

/*
 * Settings for running Rideshare check
 * @Author: Sandeep Sasidharan
 */

public class RUNSETTINGS {
	
	public static double PERCENTAGE_TRIP_DELAY = 0.1;
	public static double PERCENTAGE_MAX_SPEED = 1;
	public static double MAX_WALK_TIME = 5; // IntrMap and BFSMap to be polpulated for various Walktimes
	public static String GraphObjectFile = "ObjectWarehouse/GraphObjects/SpeedLimtRoadGraph.obj";
	public static String IntersectionMapFile = "ObjectWarehouse/WalkableIntersectionMap/IntrMap_5min.csv";//walk depd
	public static String VertexMapFile = "ObjectWarehouse/IntersectionVertexMap/VertexMap_5min.csv";//walk depd

}
