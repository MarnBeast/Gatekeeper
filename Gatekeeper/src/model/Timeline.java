package model;

import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.management.openmbean.OpenDataException;

import model.Settings.ClipBaseTypes;


public class Timeline
{
	private double transitionTime;
	private double totalGameTime;
	private SortedMap<Double, Clip> clipTimes;
	
	
	public Timeline(double transitionTime)
	{
		this.transitionTime = transitionTime;
		clipTimes = new TreeMap<Double, Clip>();
	}
	
	public Timeline()
	{
		this(Constants.DEFAULT_TRANSITION_TIME);
	}
	
	
//	public void setTransitionTime(double transitionTime)
//	{
//		this.transitionTime = transitionTime;
//	}
	
	public double getTransitionTime()
	{
		return transitionTime;
	}
	
	
//	public void setTotalGameTime(double totalGameTime)
//	{
//		this.totalGameTime = totalGameTime;
//	}
	
	public double getTotalGameTime()
	{
		return clipTimes.lastKey() + clipTimes.get(clipTimes.lastKey()).getLength();
	}

	/**
	 * In some cases, the video may start before the game. For example, if an intro is provided the video start time
	 * will be negative. If no intro is provided and the video starts with the game, the start time will be 0.0.
	 * @return
	 */
	public double getVideoStartTime()
	{
		return clipTimes.firstKey();
	}
	
	
	public void addClip(Clip clip, double time)
	{
		clipTimes.put(time, clip);
	}
	
	public Clip getClip(double time)
	{
		return clipTimes.get(time);
	}
	
	public Clip removeClip(double time)
	{
		return clipTimes.remove(time);
	}
	
	public void clearClips()
	{
		clipTimes.clear();
	}
	
	
	public Set<Double> getClipTimes()
	{
		return clipTimes.keySet();
	}
	
	public void closeClips()
	{
		for(Clip clip : clipTimes.values())
		{
			clip.close();
		}
	}

}























