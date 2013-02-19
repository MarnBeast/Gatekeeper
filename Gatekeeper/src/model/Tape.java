package model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.InvalidPathException;
import java.util.ArrayList;
import javafx.scene.media.Media;


public class Tape implements Serializable
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	
	final private String[] defaultTypes = new String[]
	{ "Intro", "Ending", "Filler" };
	
	private IDListComparable<String> allTypesIdList;
	private ArrayList<Clip> miscArrayList;
	
	private Settings defaultSettings;

	
	public Tape()
	{
		allTypesIdList = new IDListComparable<String>();
		allTypesIdList.addValues(defaultTypes);
		miscArrayList = new ArrayList<Clip>();
		
		defaultSettings = new Settings();
		name = "";
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


	
	
	// Add Clip Methods
	
	public Clip addClip(String videoClipPath)
	{
		Media media = new Media(videoClipPath);
		Clip clip = new Clip(media, 0.0, media.durationProperty().get().toSeconds());
		miscArrayList.add(clip);
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
				miscArrayList.add(clips[i]);
				totalTime += media.durationProperty().get().toSeconds();
			}
			else {
				clips[i] = new Clip(media, 0.0, media.durationProperty().get().toSeconds()); 
				miscArrayList.add(clips[i]);
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
	 * 
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
	 * 
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
	
}
