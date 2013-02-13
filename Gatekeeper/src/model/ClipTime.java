package model;

public class ClipTime {

	private Clip Clip;
	private double Time;
	
	/**
	 * Maps a clip to a time in seconds.
	 * Used for Landmarks and the Timeline.
	 * @param clip Clip to be assigned a time.
	 * @param time The time that the clip should occur in seconds.
	 */
	public ClipTime(Clip clip, double time)
	{
		Clip = clip;
		Time = time;
	}

	
	/**
	 * @return the clip
	 */
	public Clip getClip() {
		return Clip;
	}

	/**
	 * @param clip the clip to set
	 */
	public void setClip(Clip clip) {
		Clip = clip;
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
