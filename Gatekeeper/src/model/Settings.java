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
	 * Returns a HashMap of all of the include settings bitmaps keyed by Tape.
	 * @return the tapeIncludes
	 */
	@SuppressWarnings("unchecked")
	public HashMap<Tape, Integer> getTapeIncludes() {
		return (HashMap<Tape, Integer>) TapeIncludes.clone();
	}
	
	/**
	 * Assign the includes settings bitmap to the specified tape.
	 * 
	 * The includes settings bitmap specifies what elements of the tape should be available
	 * for inclusion when the timeline is generated. This value currently corresponds to the
	 * following: Misc=1, Filler=2, End=4, Intro=8. The full number should be each of the
	 * desired settings options ORed together.
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
	
	public EnumSet<TapeInclude> removeTapeIncludes(Tape tape)
	{
		return TapeIncludes.remove(tape);
	}
	
	public EnumSet<TapeInclude> getTapeIncludes(Tape tape)
	{
		return TapeIncludes.get(tape);
	}
	
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
	 * @return the biases
	 */
	@SuppressWarnings("unchecked")
	public HashMap<Integer, Double> getBiases() {
		return (HashMap<Integer, Double>) Biases.clone();
	}
	
	public Double addBias(int typeID, double percentageBias)
	{
		if(percentageBias < 0)
		{
			throw new IllegalArgumentException("A type cannot have a negative percentage bias.");
		}
		return Biases.put(typeID, percentageBias);
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
	
	public Double removeBias(int typeID)
	{
		return Biases.remove(typeID);
	}
	
	public Double getBias(int typeID)
	{
		return Biases.get(typeID);
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