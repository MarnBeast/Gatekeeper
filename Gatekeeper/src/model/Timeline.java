package model;

import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;


public class Timeline 
{
	private int transitionTime;
	private SortedMap<Double, Clip> clipTimes;
	
	public Timeline(int transitionTime)
	{
		this.transitionTime = transitionTime;
		clipTimes = new TreeMap<Double, Clip>();
	}
	
	public Timeline()
	{
		this(Constants.DEFAULT_TRANSITION_TIME);
	}
	
	public void setTransitionTime(int transitionTime)
	{
		this.transitionTime = transitionTime;
	}
	
	public int getTransitionTime()
	{
		return transitionTime;
	}
	
	public void addClip(Clip clip, double time)
	{
		clipTimes.put(time, clip);
	}
	
	public Clip removeClip(double time)
	{
		return clipTimes.remove(time);
	}
	
	public void clearClips()
	{
		clipTimes.clear();
	}
	
	public Set<Entry<Double, Clip>> getSortedClipTimes()
	{
		return clipTimes.entrySet();
	}
}