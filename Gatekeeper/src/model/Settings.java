package model;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Holds all of the user specified settings for generating a timeline.
 * These include biases, tape includes, and landmarks.
 * 
 * Used in the TimelineBuilder, as well as in the Tape class to specify default settings.
 * @author MarnBeast
 */
public class Settings {
	
	private HashMap<Tape, EnumSet<TapeInclude>> TapeIncludes;
	private HashMap<Integer, Double> Biases;
	private ArrayList<Landmark> Landmarks;
	
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
		Landmarks = new ArrayList<Landmark>();
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
	public HashMap<Tape, EnumSet<TapeInclude>> getTapeIncludes() {
		return (HashMap<Tape, EnumSet<TapeInclude>>) TapeIncludes.clone();
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
	public EnumSet<TapeInclude> addTapeIncludes(Tape tape, EnumSet<TapeInclude> includes)
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
	public void addTapeIncludes(HashMap<Tape, EnumSet<TapeInclude>> tapeIncludes)
	{
		TapeIncludes.putAll(tapeIncludes);
	}
	
	/**
	 * Remove the tape include settings EnumSet for the specified tape.
	 * @param tape
	 * @return The EnumSet that was just removed from the settings.
	 */
	public EnumSet<TapeInclude> removeTapeIncludes(Tape tape)
	{
		return TapeIncludes.remove(tape);
	}
	
	/**
	 * Get the tape include settings EnumSet for the specified tape.
	 * @param tape
	 * @return The includes settings EnumSet specifying what elements of the tape should be available
	 * for inclusion when the timeline is generated.
	 */
	public EnumSet<TapeInclude> getTapeIncludes(Tape tape)
	{
		return TapeIncludes.get(tape);
	}
	
	/**
	 * Clear all of the tape include settings for all tapes.
	 * @return HashMap of the tape include settings before being cleared, keyed by tape.
	 */
	@SuppressWarnings("unchecked")
	public HashMap<Tape, EnumSet<Settings.TapeInclude>> clearTapeIncludes()
	{
		HashMap<Tape, EnumSet<Settings.TapeInclude>> retMap;
		retMap = (HashMap<Tape, EnumSet<Settings.TapeInclude>>) TapeIncludes.clone();
		TapeIncludes.clear();
		return retMap;
	}

	
	// BIASES
	
	/**
	 * Returns a HashMap of all bias percentages saved in the settings, keyed by typeID.
	 * 
	 * This HashMap is a clone. To manipulate the biases in the settings, use addBias, addBiases,
	 * removeBias, and clearBiases.
	 * @return HashMap<Integer, Double> of all bias percentages saved in the settings, keyed by typeID.
	 */
	@SuppressWarnings("unchecked")
	public HashMap<Integer, Double> getBiases() {
		return (HashMap<Integer, Double>) Biases.clone();
	}
	
	/**
	 * Add a bias to the settings.
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
	public double addBias(int typeID, double percentageBias)
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
	
	public void addBiases(HashMap<Integer, Double> biases)
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
	
	public double removeBias(int typeID)
	{
		Double retValDouble = Biases.remove(typeID);
		if(retValDouble == null)
		{
			return 100.0;
		}
		else {
			return retValDouble;
		}
	}
	
	public double getBias(int typeID)
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
	
	@SuppressWarnings("unchecked")
	public HashMap<Integer, Double> clearBiases()
	{
		HashMap<Integer, Double> retMap;
		retMap = (HashMap<Integer, Double>)Biases.clone();
		Biases.clear();
		return retMap;
	}

	
	// LANDMARKS
	
	/**
	 * @return the landmarks
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Landmark> getLandmarks() {
		return (ArrayList<Landmark>) Landmarks.clone();
	}
	
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
	
	public boolean addLandmark(int typeID, double time)
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
		
		Landmark landmark = new Landmark(typeID, time);
		return Landmarks.add(landmark);
	}
	
	public boolean addLandmarks(ArrayList<Landmark> landmarks) {
		return Landmarks.addAll(landmarks);
	}
	
	public boolean removeLandmark(Landmark landmark)
	{
		return Landmarks.remove(landmark);
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Landmark> clearLandmarks()
	{
		ArrayList<Landmark> retList;
		retList = (ArrayList<Landmark>)Landmarks.clone();
		Landmarks.clear();
		return retList;	
	}
	
	public static enum TapeInclude
	{
		INTRO,
		END,
		FILLER,
		MISC;
	}
}