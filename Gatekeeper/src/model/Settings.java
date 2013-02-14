package model;

import java.util.ArrayList;
import java.util.Hashtable;


public class Settings {
	
	private Hashtable<Tape, Integer> TapeIncludes;
	private Hashtable<Integer, Integer> Biases;
	private ArrayList<Landmark> Landmarks;
	
	public Settings()
	{
		TapeIncludes = new Hashtable<>();
		Biases = new Hashtable<>();
		Landmarks = new ArrayList<Landmark>();
	}
}