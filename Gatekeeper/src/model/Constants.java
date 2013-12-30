package model;

public class Constants {

	public static final String TAPE_EXTENSION = ".gktape";
	public static final String TEMP_LOCATION_WIN = "C:\\Temp\\";
	public static final String TEMP_LOCATION_MAC_LIN = "$TMPDIR/";
	

	public static final String[] DEFAULT_TYPES = new String[]
	{ "Intro", "Ending", "Filler" };
	
	public static String getTempLocation()
	{
		String osName = System.getProperty("os.name");
		if(osName.contains("Windows"))
		{
			return TEMP_LOCATION_WIN;
		}
		else
		{
			return TEMP_LOCATION_MAC_LIN;
		}
	}
	
	public static final int DEFAULT_TRANSITION_TIME = 5;
}
