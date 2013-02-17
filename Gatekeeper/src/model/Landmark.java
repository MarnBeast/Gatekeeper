package model;

public class Landmark {

	private int TypeID;
	private double Time;
	
	/**
	 * Maps a TypeID to a time in seconds.
	 * Used for the Settings Landmarks methods.
	 * 
	 * Landmarks are used to specify that a clip with the given type ID MUST be played
	 * at the given time in the timeline.
	 * @param TypeID TypeID to be assigned a time.
	 * @param time The time that the TypeID should occur in seconds.
	 */
	public Landmark(int typeID, double time)
	{
		TypeID = typeID;
		Time = time;
	}

	
	/**
	 * @return the TypeID
	 */
	public int getTypeID() {
		return TypeID;
	}

	/**
	 * @param TypeID the type ID to set
	 */
	public void setTypeID(int typeID) {
		TypeID = typeID;
	}

	/**
	 * @return the time
	 */
	public double getTime() {
		return Time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(double time) {
		Time = time;
	}
}
