package model;

import java.awt.List;
import java.awt.print.Book;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.InvalidPathException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
	
	private HashMap<String, Integer> typeClipCount;
	private ArrayList<Clip> allClipsArrayList;
	
	private Settings defaultSettings;

	
	public Tape()
	{
		this("");
	}
	
	public Tape(String name)
	{
		this.typeClipCount = new HashMap<String,Integer>();
		for (String defaultType : Constants.DEFAULT_TYPES)
		{
			this.typeClipCount.put(defaultType, 0);
		}
				
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
	 * Returns a sorted array containing all of the type strings added to this tape.
	 * @return the array of type strings associated with this tape, sorted alphabetically.
	 */
	public String[] getTypes()
	{
		ArrayList<String> list = new ArrayList<String>(typeClipCount.keySet());
		Collections.sort(list);
		return list.toArray(new String[0]);
	}
	
//	/**
//	 * Adds the type string to the Tape types if it doesn't exist and returns the type ID
//	 * assigned to the type.
//	 * @param typeString The string associated with the given type.
//	 * @return the type ID assigned to the type.
//	 */
//	private int getSetTypeID(String typeString)
//	{
//		return allTypesList.getsetID(typeString);
//	}

//	/**
//	 * Returns the type string assigned to the given type ID.
//	 * @param typeID The type ID.
//	 * @return The corresponding type string.
//	 */
//	public String getTypeString(int typeID)
//	{
//		return allTypesList.get(typeID);
//	}
	
	
	// Add Clip Methods
	
	public Clip addClip(String videoClipPath)
	{
		Clip clip = new Clip(videoClipPath);
		allClipsArrayList.add(clip);
		return clip;
	}
	
	public void addClip(Clip clip)
	{
		allClipsArrayList.add(clip);
		for (String type : clip.getTypes())
		{
			typeAdded(type);
		}
		clip.addClipListener(this);
	}
	
	public void addClips(Clip[] clips)
	{
		for (Clip clip : clips)
		{
			addClip(clip);
		}
	}

	public Clip[] addClips(String[] videoClipPaths)
	{
		return addClips(videoClipPaths, false);
	}
	
	public Clip[] addClips(String[] videoClipPaths, boolean calculateRelativeClipTimes)
	{
		Clip[] clips = new Clip[videoClipPaths.length];
		double startTime = 0.0;
		double totalTime = 0.0;
		
		// instantiate all of the clips and load their media, totaling the duration along the way
		for(int i = 0; i < videoClipPaths.length; i++)
		{
			if(calculateRelativeClipTimes)
			{
				clips[i] = new Clip(videoClipPaths[i]);
				clips[i].addClipListener(this);
				allClipsArrayList.add(clips[i]);
				
				totalTime += clips[i].getVideo().getDuration().toSeconds();
			}
			else {
				clips[i] = new Clip(videoClipPaths[i]); 
				clips[i].addClipListener(this);
				allClipsArrayList.add(clips[i]);
			}

		}
		
		if(calculateRelativeClipTimes)
		{
			// Set the correct startTime and totalTime for all of these clips.
			for(int i = 0; i < clips.length; i++)
			{
				clips[i].setPlacePercent(startTime, totalTime);
				startTime += clips[i].getVideo().getDuration().toSeconds();
			}
		}
		
		return clips;
	}
	
	
	/**
	 * Returns all clips stored in the tape.
	 * @return Array of all clips stored in this tape.
	 */
	public Clip[] getClips()
	{
		return allClipsArrayList.toArray(new Clip[0]);		
	}


	/**
	 * Removes the clip from the tape and clears its types.
	 * @param clip The clip to remove.
	 */
	public void removeClip(Clip clip)
	{
		for (String type : clip.getTypes())
		{
			typeRemoved(type);
		}
		clip.removeClipListener(this);
		allClipsArrayList.remove(clip);
	}
	
	/**
	 * Removes the clips from the tape and clears their types.
	 * @param clips The clips to remove.
	 */
	public void removeClips(Collection<Clip> clips)
	{
		for(Clip clip : clips)
		{
			for (String type : clip.getTypes())
			{
				typeRemoved(type);
			}
			clip.removeClipListener(this);
		}
		allClipsArrayList.removeAll(clips);
	}
	
	/**
	 * Clears all clips from the tape.
	 */
	public void clearClips()
	{
		allClipsArrayList.clear();
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
	public void typeAdded(String typeString) {
		int typeCount = typeClipCount.containsKey(typeString) ? typeClipCount.get(typeString) : 0;
		typeClipCount.put(typeString, typeCount + 1);
	}


	@Override
	public void typeRemoved(String typeString) {
		int typeCount = typeClipCount.containsKey(typeString) ? typeClipCount.get(typeString) : 0;
		if(typeCount <= 1)
		{
			boolean isDefaultType = false;
			for (String defaultType : Constants.DEFAULT_TYPES)
			{
				if(typeString.equals(defaultType))
				{
					isDefaultType = true;
					break;
				}
			}
			if(!isDefaultType)
			{
				typeClipCount.remove(typeString);
			}
		}
		else
		{
			typeClipCount.put(typeString, typeCount - 1);
		}
	}
	
}
