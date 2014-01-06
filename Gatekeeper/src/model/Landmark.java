package model;

import java.util.ArrayList;

public class Landmark implements Comparable<Landmark> {

	private String Type;
	private double Time;
	private ArrayList<Clip> clips;
	
	/**
	 * Maps a TypeID to a time in seconds.
	 * Used for the Settings Landmarks methods.
	 * 
	 * Landmarks are used to specify that a clip with the given type ID MUST be played
	 * at the given time in the timeline.
	 * @param type Type to be assigned a time.
	 * @param time The time that the TypeID should occur in seconds.
	 */
	public Landmark(String type, double time)
	{
		Type = type;
		Time = time;
		clips = new ArrayList<>();
	}

	
	/**
	 * @return the Type
	 */
	public String getType() {
		return Type;
	}

	/**
	 * @param type The type to set
	 */
	public void setType(String type) {
		Type = type;
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
	
	public void addClip(Clip clip)
	{
		clips.add(clip);
	}
	
	public void removeClip(Clip clip)
	{
		clips.clear();
	}
	
	public Clip[] getClips()
	{
		return clips.toArray(new Clip[0]);
	}
	
	public Clip[] clearClips()
	{
		Clip[] ret = getClips();
		clips.clear();
		return ret;
	}


	@Override
	public int compareTo(Landmark o)
	{
		double diff = Time - o.Time;
		if(diff > 0)
		{
			return 1;
		}
		else if(diff < 0)
		{
			return -1;
		}
		else
		{
			return 0;
		}
	}
}
