package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.Serializable;

import com.sun.media.jfxmedia.events.PlayerEvent;

/**
 * A Clip represents a segment of a Tape. A clip has a video clip associated with it,
 * as well as optionally a list of types describing the contents of the video clip and
 * a list of chains linking this clip with other clips that must only be shown after
 * this clip. A clip must have a start time set, as well as the total runtime of the
 * tape that it was taken from. This helps the user determine where the clip occurred
 * in the original video, and is used for pacing purposes when picking clips for the
 * timeline.
 * 
 * @author Martin Dillon
 *
 */
public class Clip implements Serializable
{
	private static final long SERIAL_VERSION_UID = 1L;
	private static final int LOAD_MEDIA_TIMEOUT = 5000;
	private ArrayList<Clip> Chains;
	private Media VideoClip;
	private ArrayList<String> Types;
	private double PlacePercent = 0.0;
	private double StartTime = 0.0;
	private double TotalTime = 0.0;

	
	private boolean mediaLoaded = false;
	
	private Vector<ClipListener> listeners;
	
	
	public interface ClipListener
	{
		public void typeAdded(String typeString);
		public void typeRemoved(String typeString);
	}
	
	
	/**
	 * A Clip represents a segment of a Tape. A clip has a video clip associated with it,
	 * as well as optionally a list of types describing the contents of the video clip and
	 * a list of chains linking this clip with other clips that must only be shown after
	 * this clip. A clip must have a start time set, as well as the total runtime of the
	 * tape that it was taken from. This helps the user determine where the clip occurred
	 * in the original video, and is used for pacing purposes when picking clips for the
	 * timeline.
	 * 
	 * @param videoClip The specific video that this clip is referencing.
	 * @param startTime The time that the video occurs in the original unshuffled tape.
	 * @param totalTime The entire length of the original tape that this clip came from.
	 * 
	 * @throws IllegalArgumentException
	 */
	public Clip(Media videoClip, double startTime, double totalTime)
	{
		setVideo(videoClip);
		Types = new ArrayList<String>();
		Chains = new ArrayList<Clip>();
		this.setPlacePercent(startTime, totalTime);
	}
	
	/**
	 * A Clip represents a segment of a Tape. A clip has a video clip associated with it,
	 * as well as optionally a list of types describing the contents of the video clip and
	 * a list of chains linking this clip with other clips that must only be shown after
	 * this clip. A clip must have a start time set, as well as the total runtime of the
	 * tape that it was taken from. This helps the user determine where the clip occurred
	 * in the original video, and is used for pacing purposes when picking clips for the
	 * timeline.
	 * 
	 * @param videoPath The path to the specific video that this clip is referencing.
	 * 
	 * @throws IllegalArgumentException
	 * @throws MediaException
	 * @throws NullPointerException
	 */
	public Clip(String videoPath, double startTime, double totalTime)
	{
		setVideo(videoPath);
		Types = new ArrayList<String>();
		Chains = new ArrayList<Clip>();
		this.setPlacePercent(startTime, totalTime);
	}
	
	/**
	 * A Clip represents a segment of a Tape. A clip has a video clip associated with it,
	 * as well as optionally a list of types describing the contents of the video clip and
	 * a list of chains linking this clip with other clips that must only be shown after
	 * this clip. A clip must have a start time set, as well as the total runtime of the
	 * tape that it was taken from. This helps the user determine where the clip occurred
	 * in the original video, and is used for pacing purposes when picking clips for the
	 * timeline. Without these specified, the Clip will attempt to load the media metadata
	 * and set totalTime to the media duration. If that fails, it will simply be set to 1.0.
	 * 
	 * 
	 * @param videoClip The specific video that this clip is referencing.
	 * 
	 * @throws IllegalArgumentException
	 * @throws MediaException
	 * @throws NullPointerException
	 */
	public Clip(Media videoClip)
	{
		this(videoClip, 0.0, 1.0);
		if(this.loadMediaMetaData() && VideoClip != null)
		{
			this.setPlacePercent(0.0, VideoClip.getDuration().toSeconds());
		}
	}
	
	/**
	 * A Clip represents a segment of a Tape. A clip has a video clip associated with it,
	 * as well as optionally a list of types describing the contents of the video clip and
	 * a list of chains linking this clip with other clips that must only be shown after
	 * this clip. A clip must have a start time set, as well as the total runtime of the
	 * tape that it was taken from. This helps the user determine where the clip occurred
	 * in the original video, and is used for pacing purposes when picking clips for the
	 * timeline. Without these specified, the Clip will attempt to load the media metadata
	 * and set totalTime to the media duration. If that fails, it will simply be set to 1.0.
	 * 
	 * @param videoPath The path to the specific video that this clip is referencing.
	 * @param startTime The time that the video occurs in the original unshuffled tape.
	 * @param totalTime The entire length of the original tape that this clip came from.
	 * 
	 * @throws IllegalArgumentException
	 * @throws MediaException
	 * @throws NullPointerException
	 */
	public Clip(String videoPath)
	{
		this(videoPath, 0.0, 1.0);
		if(this.loadMediaMetaData() && VideoClip != null)
		{
			this.setPlacePercent(0.0, VideoClip.getDuration().toSeconds());
		}
	}
	
	
	/*
	 * VIDEO CLIP METHODS
	 */
	
	/**
	 * @return the videoClip
	 */
	public Media getVideo() {
		return VideoClip;
	}
	
	/**
	 * @param videoClip the videoClip to set
	 */
	public void setVideo(Media videoClip) {
		mediaLoaded = false;
		VideoClip = videoClip;
	}
	
	/**
	 * @param videoClip the videoClip to set
	 * 
	 * @throws IllegalArgumentException
	 * @throws MediaException
	 * @throws NullPointerException
	 */
	public void setVideo(String videoClipPath) {
		Media videoClip = new Media(videoClipPath);
		setVideo(videoClip);
	}
	
	/**
	 * Loads the media into a MediaPlayer and calls setOnReady, loading the media such that
	 * its metadata may be determined. If this has been called before with the currently set
	 * media and the metadata was successfully loaded, this will just return true rather than
	 * attempt to load the media again.
	 * 
	 * @param timeout The maximum time to wait for the media to load, in milliseconds.
	 * @return Boolean stating whether or not the media was successfully loaded.
	 */
	public boolean loadMediaMetaData(int timeout)
	{		
		if(!mediaLoaded && this.VideoClip != null)
		{
			MediaPlayer player = new MediaPlayer(this.VideoClip);
			player.setOnReady(new Runnable()
			{
				@Override
				public void run()
				{
					mediaLoaded = true;
				}
			});
			
			int waitTime = 0;
			while(!mediaLoaded && waitTime < timeout)
			{
				try
				{
					Thread.sleep(10);
					waitTime += 10;
				} catch (InterruptedException e)
				{
					// Just keep checking and waiting
				}
			}
			
			player.setOnReady(null);
		}
		
		return mediaLoaded;
	}
	
	/**
	 * Loads the media into a MediaPlayer and calls setOnReady, loading the media such that
	 * its metadata may be determined. If this has been called before with the currently set
	 * media and the metadata was successfully loaded, this will just return true rather than
	 * attempt to load the media again.
	 * 
	 * This uses the default timeout of 5 seconds.
	 * 
	 * @return Boolean stating whether or not the media was successfully loaded.
	 */
	public boolean loadMediaMetaData()
	{
		return loadMediaMetaData(LOAD_MEDIA_TIMEOUT);
	}
	
	
	
	/*
	 * CHAIN CLIPS METHODS
	 */
	
	/**
	 * @return ArrayList of Clips that are chained directly to this clip.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Clip> getChainedClips() {
		return (ArrayList<Clip>) Chains.clone();
	}
	
	/**
	 * Specifies that the passed in clip should be chained after this clip, such that
	 * it will not play before this clip and is likely to play after.
	 * @param clip The clip to be chained after this clip.
	 * @return true if the add was successful.
	 * @throws NullPointerException
	 */
	public boolean addChainedClip(Clip clip) {
		if(clip == null)
		{
			throw new NullPointerException("Clip must not be null");
		}
		if(Chains.contains(clip))
		{
			return false;
		}
		else
		{
			return Chains.add(clip);
		}
	}
	
	/**
	 * Removes the passed in clip from the chained clips collection.
	 * @param clip
	 * @return true if the removal was successful.
	 */
	public boolean removeChainedClip(Clip clip) {
		return Chains.remove(clip);
	}
	
	/**
	 * Removes all clips from the chained clips collection.
	 * @return ArrayList of removed clips.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Clip> clearChainedClips() {
		ArrayList<Clip> retList;
		retList = (ArrayList<Clip>) Chains.clone();
		Chains.clear();
		return retList;
	}
	
	
	
	/*
	 * TYPE ID METHODS
	 */
	
	/**
	 * Gets the type IDs associated with this clip.
	 * Types are defined in the tape holding this clip. The type IDs associated
	 * with this clip map to those types and describe the contents of this clip.
	 * @return the ArrayList of clip type IDs
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> getTypes() {
		return (ArrayList<String>) Types.clone();
	}
	
	/**
	 * Add a type describing this clip.
	 * Types are defined in the tape holding this clip. The type IDs associated
	 * with this clip map to those types and describe the contents of this clip.
	 * @param typeID The ID of the type being added to this clip.
	 * @return
	 */
	public boolean addType(String typeString) {
		
		if(Types.contains(typeString))
		{
			return false;
		}
		else
		{
			boolean ret = Types.add(typeString);
			if(ret)
			{
				fireTypeAddedEvent(typeString);
			}
			return ret;
		}
	}
	
	/**
	 * Removes the type ID from the list.
	 * Types are defined in the tape holding this clip. The type IDs associated
	 * with this clip map to those types and describe the contents of this clip.
	 * @param typeID The ID of the type being removed from this clip
	 * @return
	 */
	public boolean removeType(String type) {
		boolean ret = Types.remove(type);
		if(ret)
		{
			fireTypeRemovedEvent(type);
		}
		return ret;
	}
	
	/**
	 * Removes all type IDs from the type ID collection.
	 * Types are defined in the tape holding this clip. The type IDs associated
	 * with this clip map to those types and describe the contents of this clip.
	 * @return ArrayList of removed type IDs.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> clearTypes() {
		ArrayList<String> retList;
		retList = (ArrayList<String>) Types.clone();
		Types.clear();
		for (String type : retList) 
		{
			fireTypeRemovedEvent(type);
		}
		return retList;
	}
	
	
	
	/*
	 * PLACE PERCENTAGE METHODS
	 */
	
	/*
	 * Note, I don't include setStartTime, setTotalTime, or a public direct setPlacePercent because I want to
	 * ensure that we always have all three. Thus, the only setter, setPlacePecent(startTime, totalTime),
	 * sets all three variables at the same time. To change one, you must reset the contents of all three.
	 */
	
	/**
	 * Gets the start time of this clip.
	 * This is the time that this specific clip occurs in the original unshuffled tape.
	 * @return the clip start time in seconds
	 */
	public double getStartTime() {
		return StartTime;
	}

	/**
	 * Gets the total runtime of the tape this clip is to be included in.
	 * @return the tape total time in seconds
	 */
	public double getTotalTime() {
		return TotalTime;
	}

	/**
	 * Gets the place percent of this clip.
	 * This value is generated from the start time of this clip and the total time of
	 * the tape this clip is to be included in. The value represents the percent of the
	 * way through the original unshuffled tape that this clip occurs.
	 * @return the place percent integer generated from the start and total times
	 */
	public double getPlacePercent() {
		return PlacePercent;
	}
	
	/**
	 * Sets the place percent of this clip.
	 * This value is generated from the start time of this clip and the total time of
	 * the tape this clip is to be included in. The value represents the percent of the
	 * way through the original unshuffled tape that this clip occurs.
	 * @param startTime the clip start time in seconds
	 * @param totalTime
	 */
	public void setPlacePercent(double startTime, double totalTime) {
		if(startTime >= totalTime)
		{
			throw new IllegalArgumentException("startTime must be less than totalTime.");
		}
		else if(startTime < 0 || totalTime <= 0)
		{
			throw new IllegalArgumentException("startTime and totalTime must not be negative and totalTime must be greater than 0.");
		}
		StartTime = startTime;
		TotalTime = totalTime;
		setPlacePercent(startTime * 100.0 / totalTime);
	}

	/**
	 * Sets the place percentage directly.
	 * I made this method private because I want to force the user of this class to set
	 * the start and total times and allow the class to generate the place percentage
	 * itself. However, I left the setter in here in case I want to add setter functionality
	 * to placePercent and use it internally.
	 * @param placePercent
	 */
	private void setPlacePercent(double placePercent) {
		PlacePercent = placePercent;
	}
	
	
	
	
	/**
	 * Adds a ClipListener to the Clip, such that the listener may be notified when a listener
	 * event is triggered.
	 * @param listener
	 */
	public void addClipListener(ClipListener listener)
	{
		if(listeners == null)
		{
			listeners = new Vector<ClipListener>();
		}
		listeners.add(listener);
	}
	
	/**
	 * Removes a ClipListener from the Clip, such that the listener will no longer be notified when
	 * a listener event is triggered.
	 * @param listener
	 */
	public void removeClipListener(ClipListener listener)
	{
		if(listeners != null)
		{
			listeners.remove(listener);
		}
	}
	
	
	/**
	 * Iterates through all of the clip listeners firing the typeAdded event.
	 * This should be called whenever a clip has a type ID added to TypeIDs.
	 * @param typeID
	 */
	private void fireTypeAddedEvent(String typeString)
	{
		if(listeners != null && !listeners.isEmpty())
		{
			Iterator<ClipListener> iterator = listeners.iterator();
			while(iterator.hasNext())
			{
				ClipListener listener = (ClipListener) iterator.next();
				listener.typeAdded(typeString);
			}
		}
	}
	
	
	/**
	 * Iterates through all of the clip listeners firing the typeRemoved event.
	 * This should be done whenever a clip has a type ID removed from TypeIDs.
	 * @param typeID
	 */
	private void fireTypeRemovedEvent(String typeString)
	{
		if(listeners != null && !listeners.isEmpty())
		{
			Iterator<ClipListener> iterator = listeners.iterator();
			while(iterator.hasNext())
			{
				ClipListener listener = (ClipListener) iterator.next();
				listener.typeRemoved(typeString);
			}
		}
	}
	
}
