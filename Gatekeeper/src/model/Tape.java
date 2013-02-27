package model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.InvalidPathException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import model.Clip.ClipListener;
import javafx.scene.media.Media;


public class Tape implements Serializable, ClipListener
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	
	
	private IDListComparable<String> allTypesIdList;
	private HashMap<Integer, Integer> typeClipCount;
	private ArrayList<Clip> allClipsArrayList;
	
	private Settings defaultSettings;

	
	public Tape()
	{
		this("");
	}
	
	public Tape(String name)
	{
		this.allTypesIdList = new IDListComparable<String>();
		this.allTypesIdList.addValues(Constants.DEFAULT_TYPES);
		
		this.typeClipCount = new HashMap<Integer,Integer>();
				
		this.allClipsArrayList = new ArrayList<Clip>();
		
		this.defaultSettings = new Settings();
		this.name = name;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	
	
	
	// Manage Type IDs
	
	/**
	 * Returns a list containing all of the type strings added to this tape.
	 * @return the list of type strings associated with this tape.
	 */
	public String[] getTypeStrings()
	{
		return allTypesIdList.values().toArray(new String[0]);
	}
	
	/**
	 * Adds the type string to the Tape types if it doesn't exist and returns the type ID
	 * assigned to the type.
	 * @param typeString The string associated with the given type.
	 * @return the type ID assigned to the type.
	 */
	public int getTypeID(String typeString)
	{
		return allTypesIdList.getsetID(typeString);
	}

	/**
	 * Returns the type string assigned to the given type ID.
	 * @param typeID The type ID.
	 * @return The corresponding type string.
	 */
	public String getTypeString(int typeID)
	{
		return allTypesIdList.get(typeID);
	}
	
	
	// Add Clip Methods
	
	public Clip addClip(String videoClipPath)
	{
		Media media = new Media(videoClipPath);
		Clip clip = new Clip(media, 0.0, media.durationProperty().get().toSeconds());
		allClipsArrayList.add(clip);
		return clip;
	}

	public Clip[] addClips(String[] videoClipPaths)
	{
		return addClips(videoClipPaths, false);
	}
	
	public Clip[] addClips(String[] videoClipPaths, boolean calculateRelativeClipTimes)
	{
		Clip[] clips = new Clip[videoClipPaths.length];
		double totalTime = 0.0;
		for(int i = 0; i < videoClipPaths.length; i++)
		{
			Media media = new Media(videoClipPaths[i]);
			if(calculateRelativeClipTimes)
			{	
				// startTime should be the total time accumulated up until this clip.
				// We don't care about totalTime's value at this time because it will
				// be changed later. Thus, totalTime + 1 is a placeholder.
				clips[i] = new Clip(media, totalTime, totalTime + 1); 
				clips[i].addClipListener(this);
				allClipsArrayList.add(clips[i]);
				totalTime += media.durationProperty().get().toSeconds();
			}
			else {
				clips[i] = new Clip(media, 0.0, media.durationProperty().get().toSeconds()); 
				clips[i].addClipListener(this);
				allClipsArrayList.add(clips[i]);
			}

		}
		
		if(calculateRelativeClipTimes)
		{
			// Set the correct totalTime for all of these clips.
			for(int i = 0; i < clips.length; i++)
			{
				clips[i].setPlacePercent(clips[i].getStartTime(), totalTime);
			}
		}
		
		return clips;
	}


	/**
	 * Removes the clip from the tape and clears its type IDs.
	 * @param clip The clip to remove.
	 */
	public void removeClip(Clip clip)
	{
		clip.clearTypeIDs();
		allClipsArrayList.remove(clip);
	}
	
	/**
	 * Removes the clips from the tape and clears their type IDs.
	 * @param clips The clips to remove.
	 */
	public void removeClips(Collection<Clip> clips)
	{
		for(Clip clip : clips)
		{
			clip.clearTypeIDs();
		}
		allClipsArrayList.removeAll(clips);
	}
	
	/**
	 * Clears all clips from the tape.
	 */
	public void clearClips()
	{
		allClipsArrayList.clear();
		allTypesIdList.clear();
		allTypesIdList.addValues(Constants.DEFAULT_TYPES);
		typeClipCount.clear();
	}

	
	/**
	 * @return the defaultSettings
	 */
	public Settings getDefaultSettings() {
		return defaultSettings;
	}


	/**
	 * @param defaultSettings the defaultSettings to set
	 */
	public void setDefaultSettings(Settings defaultSettings) {
		this.defaultSettings = defaultSettings;
	}


	/**
	 * Serializes tape to a file.
	 * @param filepath
	 * @throws IOException 
	 */
	public void saveTape(String filepath) throws IOException
	{
		if(!filepath.endsWith(Constants.TAPE_EXTENSION))
		{
			filepath = filepath.concat(Constants.TAPE_EXTENSION);
		}
		FileOutputStream fileOut = new FileOutputStream(filepath);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(this);
		out.close();
		fileOut.close();
	}
	
	/**
	 * Deserializes tape from a file.
	 * @param filepath
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InvalidPathException
	 */
	public static Tape loadTape(String filepath) throws IOException, ClassNotFoundException
	{
		if(!filepath.endsWith(Constants.TAPE_EXTENSION))
		{
			throw new InvalidPathException(filepath, "The supplied filepath must point to a valid Gatekeeper tape file with " + 
											Constants.TAPE_EXTENSION + " extension.");
		}
		FileInputStream fileIn = new FileInputStream(filepath);
		ObjectInputStream in = new ObjectInputStream(fileIn);

		Tape tape = (Tape) in.readObject();
		in.close();
		fileIn.close();
		return tape;
	}


	@Override
	public void typeAdded(int typeID) {
		int typeCount = typeClipCount.containsKey(typeID) ? typeClipCount.get(typeID) : 0;
		typeClipCount.put(typeID, typeCount + 1);
	}


	@Override
	public void typeRemoved(int typeID) {
		int typeCount = typeClipCount.containsKey(typeID) ? typeClipCount.get(typeID) : 0;
		if(typeCount < 1)
		{
			if(typeID >= Constants.DEFAULT_TYPES.length)
			{
				allTypesIdList.remove(typeID);
				typeClipCount.remove(typeID);
			}
		}
		else
		{
			typeClipCount.put(typeID, typeCount - 1);
		}
	}
	
}
