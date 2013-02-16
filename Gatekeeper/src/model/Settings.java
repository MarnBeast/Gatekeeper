package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class Settings {
	
	private HashMap<Tape, Integer> TapeIncludes;
	private HashMap<Integer, Double> Biases;
	private ArrayList<Landmark> Landmarks;
	
	public Settings()
	{
		TapeIncludes = new HashMap<>();
		Biases = new HashMap<>();
		Landmarks = new ArrayList<Landmark>();
	}

	
	// TAPE INCLUDES
	
	/**
	 * @return the tapeIncludes
	 */
	@SuppressWarnings("unchecked")
	public HashMap<Tape, Integer> getTapeIncludes() {
		return (HashMap<Tape, Integer>) TapeIncludes.clone();
	}
	
	public Integer addTapeIncludes(Tape tape, int includesBitmap)
	{
		return TapeIncludes.put(tape, includesBitmap);
	}
	
	public void addTapeIncludes(HashMap<Tape, Integer> tapeIncludes)
	{
		TapeIncludes.putAll(tapeIncludes);
	}
	
	public Integer removeTapeIncludes(Tape tape)
	{
		return TapeIncludes.remove(tape);
	}
	
	public Integer getTapeIncludes(Tape tape)
	{
		return TapeIncludes.get(tape);
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<Tape, Integer> clearTapeIncludes()
	{
		HashMap<Tape, Integer> retMap;
		retMap = (HashMap<Tape, Integer>) TapeIncludes.clone();
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
		return retList;	}
}