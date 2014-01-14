package model;

public class Constants {

	public static final String TEMP_LOCATION_WIN = "C:\\Temp\\Gatekeeper\\";
	public static final String TEMP_LOCATION_MAC_LIN = "$TMPDIR/Gatekeeper/";
	
	public static final String TAPE_EXTENSION = ".gktape";
	public static final String SUPPORTED_MEDIA_EXTENSIONS = "^.*\\.(aiff|AIFF|fxm|FXM|flv|FLV|mp3|MP3|mp4|MP4|wav|WAV)$";
	

	public static final String[] DEFAULT_TYPES = new String[]
	{ "Clip", "Intro", "Ending", "Filler"};
	
	public static final double DEFAULT_CLIP_TYPE_BIAS = 100.0;
	public static final double DEFAULT_POSITION_WINDOW = 40.0;
	
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
	
	public static final double DEFAULT_TRANSITION_TIME = 2.0;
	public static final double DEFAULT_TOTAL_GAME_TIME = 3600.0;
}
