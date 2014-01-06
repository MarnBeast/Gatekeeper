package model;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Holds all of the user specified settings for generating a timeline.
 * These include biases, tape includes, and landmarks.
 * 
 * Used in the TimelineBuilder, as well as in the Tape class to specify default settings.
 * @author MarnBeast
 */
public class Settings implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private HashMap<Tape, EnumSet<ClipBaseTypes>> TapeIncludes;
	private HashMap<String, Double> Biases;
	private TreeSet<Landmark> Landmarks;
	private double PositionWindow = Constants.DEFAULT_POSITION_WINDOW;
	
	/**
	 * Holds all of the user specified settings for generating a timeline.
	 * These include biases, tape include settings, and landmarks.
	 * 
	 * Used in the TimelineBuilder, as well as in the Tape class to specify default settings.
	 */
	public Settings()
	{
		TapeIncludes = new HashMap<>();
		Biases = new HashMap<>();
		Landmarks = new TreeSet<Landmark>();
		
		addBias(Constants.DEFAULT_TYPES[0], Constants.DEFAULT_CLIP_TYPE_BIAS);
		// Add default "Clip" type. This is done to ensure that, if a clip has no types
		// assigned to it, it at least has a default bias so it will be included in the clippool.
	}

	
	// TAPE INCLUDES
	
	/**
	 * Returns a HashMap of all of the tape include settings EnumSets keyed by Tape.
	 * 
	 * This value is a clone. To manipulate the tape includes settings, use the addTapeIncludes,
	 * removeTapeIncludes, and clearTapeIncludes methods.
	 * @return the tapeIncludes
	 */
	@SuppressWarnings("unchecked")
	public HashMap<Tape, EnumSet<ClipBaseTypes>> getTapeIncludes() {
		return (HashMap<Tape, EnumSet<ClipBaseTypes>>) TapeIncludes.clone();
	}
	
	/**
	 * Assign the tape include settings EnumSet to the specified tape.
	 * 
	 * The includes settings EnumSet specifies what elements of the tape should be available
	 * for inclusion when the timeline is generated.
	 * @param tape
	 * @param includesBitmap
	 * @return The previous include settings assigned to that tape..
	 */
	public EnumSet<ClipBaseTypes> addTapeIncludes(Tape tape, EnumSet<ClipBaseTypes> includes)
	{
		return TapeIncludes.put(tape, includes);
	}
	
	/**
	 * Assign the tape includes listed in the hashmap to the current settings.
	 * 
	 * Any include settings mapped to a tape already in the current settings will overwrite
	 * whatever is currently mapped to that tape.
	 * @param tapeIncludes HashMap mapping a number of Tapes to includes bitmaps.
	 */
	public void addTapeIncludes(HashMap<Tape, EnumSet<ClipBaseTypes>> tapeIncludes)
	{
		TapeIncludes.putAll(tapeIncludes);
	}
	
	/**
	 * Remove the tape include settings EnumSet for the specified tape.
	 * @param tape
	 * @return The EnumSet that was just removed from the settings.
	 */
	public EnumSet<ClipBaseTypes> removeTapeIncludes(Tape tape)
	{
		return TapeIncludes.remove(tape);
	}
	
	/**
	 * Get the tape include settings EnumSet for the specified tape.
	 * @param tape
	 * @return The includes settings EnumSet specifying what elements of the tape should be available
	 * for inclusion when the timeline is generated.
	 */
	public EnumSet<ClipBaseTypes> getTapeIncludes(Tape tape)
	{
		return TapeIncludes.get(tape);
	}
	
	/**
	 * Clear all of the tape include settings for all tapes.
	 * @return HashMap of the tape include settings before being cleared, keyed by tape.
	 */
	@SuppressWarnings("unchecked")
	public HashMap<Tape, EnumSet<Settings.ClipBaseTypes>> clearTapeIncludes()
	{
		HashMap<Tape, EnumSet<Settings.ClipBaseTypes>> retMap;
		retMap = (HashMap<Tape, EnumSet<Settings.ClipBaseTypes>>) TapeIncludes.clone();
		TapeIncludes.clear();
		return retMap;
	}

	
	// BIASES
	
	public void setPostionWindow(double posWindow)
	{
		PositionWindow = posWindow;
	}
	
	public double getPositionWindow()
	{
		return PositionWindow;
	}
	
	/**
	 * Returns a HashMap of all bias percentages saved in the settings, keyed by typeID. This only
	 * includes percentages explicitly set using addBias or addBiases, not default biases.
	 * 
	 * This HashMap is a clone. To manipulate the biases in the settings, use addBias, addBiases,
	 * removeBias, and clearBiases.
	 * @return HashMap<Integer, Double> of all bias percentages saved in the settings, keyed by typeID.
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Double> getBiases() {
		return (HashMap<String, Double>) Biases.clone();
	}
	
	/**
	 * Add a type bias to the settings.
	 * 
	 * This is used to skew the chances that a specific clip type is chosen for the timeline.
	 * A bias percentage may be any number from 0 to Double.MAX_VALUE.
	 * 
	 * A bias of 0 means that no clips of the specified type should be added to the timeline.
	 * 
	 * A bias between 1 and 100 means that the chances that a clip of that type are chosen should be lowered.
	 * Ex. 50.0 means that clips of that type are half as likely to be chosen.
	 * 
	 * A bias of 100 means that the chances that a clip of that type is chosen should be unaltered.
	 * 
	 * A bias between 100 and Double.MAX_VALUE means that the chances that a clip of that type is chosen should
	 * be raised.
	 * Ex. 200.0 means that clips of that type are twice as likely to be added to the timeline.
	 * 
	 * @param typeID
	 * @param percentageBias
	 * @return The previous percentage bias assigned to that type. If no assignment exists, 100.0 is returned.
	 * @throws IllegalArgumentException If percentageBias is negative, IllegalArgumentException is thrown.
	 */
	public double addBias(String typeID, double percentageBias)
	{
		if(percentageBias < 0)
		{
			throw new IllegalArgumentException("A type cannot have a negative percentage bias.");
		}
		
		Double retValDouble = Biases.put(typeID, percentageBias);
		if(retValDouble == null)
		{
			return 100.0;
		}
		else { 
			return retValDouble;
		}
	}
	
	/**
	 * Add a range of type biases to the settings.
	 * @param biases A HashMap<Integer, Double> of the type biases to add to the settings, keyed by type ID.
	 * @throws IllegalArgumentException if a bias value in the passed in HashMap is negative.
	 */
	public void addBiases(HashMap<String, Double> biases)
	{
		Iterator<Double> percentageBiases = biases.values().iterator();
		while(percentageBiases.hasNext())
		{
			if(percentageBiases.next() < 0)
			{
				throw new IllegalArgumentException("A type cannot have a negative percentage bias. An element in this hashmap has a negative value.");
			}
		}
		Biases.putAll(biases);
	}
	
	/**
	 * Removes the bias assigned to the specified type ID. This resets the bias to the default 100.0 and removes it from the
	 * HashMap returned by getBiases.
	 * @param typeID The type of clip to remove bias.
	 * @return The previous bias percentage assigned to the specified type ID.
	 */
	public double removeBias(String typeID)
	{
		// Let's actually allow the user to specifically remove the "Clip" bias if they want to.
		Double retValDouble = Biases.remove(typeID);
		if(retValDouble == null)
		{
			return 100.0;
		}
		else {
			return retValDouble;
		}
	}
	
	/**
	 * Returns the bias assigned to the given type. If no bias exists, 100.0 is returned as the default.
	 * @param typeID The type for which a bias should be retrieved.
	 * @return The bias assigned to the given type, or 100.0 if no bias is found.
	 */
	public double getBias(String typeID)
	{
		Double retValDouble = Biases.get(typeID);
		if(retValDouble == null)
		{
			return 100.0;
		}
		else {
			return retValDouble;
		}
	}
	
	/**
	 * Clears all assigned biases in the settings. After clearing, all calls to getBias will return 100.0,
	 * and all calls to getBiases will return an empty HashMap, until new biases are assigned with setBias
	 * or setBiases.
	 * @return The HashMap of bias percentages mapped to type IDs prior to being cleared.
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Double> clearBiases()
	{
		HashMap<String, Double> retMap;
		retMap = (HashMap<String, Double>)Biases.clone();
		Biases.clear();
		addBias(Constants.DEFAULT_TYPES[0], Constants.DEFAULT_CLIP_TYPE_BIAS);
		return retMap;
	}

	
	// LANDMARKS
	
	/**
	 * Returns an ArrayList of Landmarks saved in the settings.
	 * @return ArrayList of Landmarks.
	 */
	@SuppressWarnings("unchecked")
	public Landmark[] getLandmarks() {
		return Landmarks.toArray(new Landmark[0]);
	}
	
	/**
	 * Adds a landmark to the settings.
	 * @param landmark
	 * @return True if the landmark was added successfully. False if a landmark
	 * already exists with a landmark time equal to the one specified in the passed
	 * in landmark.
	 * @throws NullPointerException if the landmark is null.
	 */
	public boolean addLandmark(Landmark landmark)
	{
		if(landmark == null)
		{
			throw new NullPointerException("Landmark must not be null");
		}
		
		double time = landmark.getTime();
		Iterator<Landmark> landIter = Landmarks.iterator();
		while(landIter.hasNext())
		{
			Landmark nextLandmark = landIter.next();
			if(nextLandmark.getTime() == time)
			{
				return false;
			}
		}
		
		return Landmarks.add(landmark);
	}
	
	/**
	 * Adds a landmark to the settings.
	 * 
	 * Landmarks are used to specify that a clip with the given type ID MUST be played
	 * at the given time in the timeline.
	 * @param typeID
	 * @param time
	 * @return True if the landmark was added successfully. False if a landmark
	 * already exists with a landmark time equal to the time specified.
	 */
	public boolean addLandmark(String type, double time)
	{
		Iterator<Landmark> landIter = Landmarks.iterator();
		while(landIter.hasNext())
		{
			Landmark nextLandmark = landIter.next();
			if(nextLandmark.getTime() == time)
			{
				return false;
			}
		}
		
		Landmark landmark = new Landmark(type, time);
		return Landmarks.add(landmark);
	}
	
	/**
	 * Add a list of Landmarks to the settings landmarks list.
	 * @param landmarks
	 * @return True if the landmark was added successfully. False if a landmark
	 * already exists with a landmark time equal to the time specified in one of
	 * the landmarks in the passed in list.
	 * @throws NullPointerException if one of the landmarks in the passed in list
	 * is null.
	 */
	public boolean addLandmarks(TreeSet<Landmark> landmarks) {
		Iterator<Landmark> landIter = landmarks.iterator();
		while(landIter.hasNext())
		{
			Landmark nextLandmark = landIter.next();
//			if(nextLandmark == null)   THIS IS IMPOSSIBLE WITH TREESETS. OBSOLETE AFTER MOVING AWAY FROM ARRAYLIST
//			{
//				throw new NullPointerException("Landmark must not be null");
//			}
			double time = nextLandmark.getTime();
			
			Iterator<Landmark> landIter2 = Landmarks.iterator();
			while(landIter2.hasNext())
			{
				Landmark nextLandmark2 = landIter2.next();
				if(nextLandmark2.getTime() == time)
				{
					return false;
				}
			}
		}
		return Landmarks.addAll(landmarks);
	}
	
	/**
	 * Remove the specified landmark from the settings landmarks.
	 * @param landmark The landmark to remove.
	 * @return true if the settings contained the specified landmark.
	 */
	public boolean removeLandmark(Landmark landmark)
	{
		return Landmarks.remove(landmark);
	}
	
	/**
	 * Clears all landmarks from the settings.
	 * @return An ArrayList of the Landmarks that existed in the settings
	 * before being cleared.
	 */
	@SuppressWarnings("unchecked")
	public TreeSet<Landmark> clearLandmarks()
	{
		TreeSet<Landmark> retList;
		retList = (TreeSet<Landmark>)Landmarks.clone();
		Landmarks.clear();
		return retList;	
	}
	
	public void setLandmarkClips(Tape[] tapes)
	{
		for (Tape tape : tapes)
		{
			setLandmarkClips(tape.getClips(ClipBaseTypes.MISC));
		}
	}
	public void setLandmarkClips(Clip[] clips)
	{
		for (Clip clip : clips)
		{
			for (String typeID : clip.getTypes())
			{
				for (Landmark landmark : getLandmarks())
				{
					String landmarkType = landmark.getType();
					if(typeID.equals(landmarkType))
					{
						landmark.addClip(clip);
						break; // breaks out of the types loop,
							   // but we'll keep looping through landmarks
					}
				}
			}
		}
	}
	
	/**
	 * The available Tape Include options. These are used in the settings TapeIncludes methods
	 * to specify what base types of the given Tapes should be made available when choosing clips
	 * for the timeline.
	 * @author MarnBeast
	 *
	 */
	public static enum ClipBaseTypes
	{
		INTRO,
		END,
		FILLER,
		MISC;
	}
}