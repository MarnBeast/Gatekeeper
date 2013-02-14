package model;

public class Landmark {

	private int TypeID;
	private double Time;
	
	/**
	 * Maps a TypeID to a time in seconds.
	 * Used for the Landmarks.
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
	 * @param TypeID the TypeID to set
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
