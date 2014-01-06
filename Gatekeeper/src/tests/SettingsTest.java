package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.TreeSet;

import model.Landmark;
import model.Settings;
import model.Tape;

import org.junit.Test;

public class SettingsTest {

	@Test
	public void constructorTest()
	{
		Settings settings = new Settings();
		
		assertNotNull(settings.getTapeIncludes());
		assertEquals(0, settings.getTapeIncludes().size());
		
		assertNotNull(settings.getBiases());
		assertEquals(0, settings.getBiases().size());
		
		assertNotNull(settings.getLandmarks());
		assertEquals(0, settings.getLandmarks().length);
	}
	
	
	@Test
	public void tapeIncludesTest()
	{
		int numIndexes = 6;
		Settings settings = new Settings();
		Tape[] tapes = new Tape[]{new Tape(), new Tape(), new Tape(), new Tape(), new Tape(), new Tape()};
		ArrayList<EnumSet<Settings.ClipBaseTypes>> includeValues = new ArrayList<EnumSet<Settings.ClipBaseTypes>>();
		includeValues.add(EnumSet.of(Settings.ClipBaseTypes.INTRO));
		includeValues.add(EnumSet.of(Settings.ClipBaseTypes.INTRO, Settings.ClipBaseTypes.END, Settings.ClipBaseTypes.FILLER));
		includeValues.add(EnumSet.of(Settings.ClipBaseTypes.INTRO, Settings.ClipBaseTypes.END, Settings.ClipBaseTypes.FILLER, Settings.ClipBaseTypes.MISC));
		includeValues.add(EnumSet.of(Settings.ClipBaseTypes.END, Settings.ClipBaseTypes.FILLER, Settings.ClipBaseTypes.MISC));
		includeValues.add(EnumSet.of(Settings.ClipBaseTypes.INTRO, Settings.ClipBaseTypes.END));
		includeValues.add(EnumSet.of(Settings.ClipBaseTypes.FILLER, Settings.ClipBaseTypes.MISC));

		Tape tapeNotInTypeIDs = new Tape();

		
		for (int i = 0; i<numIndexes; i++)
		{
			settings.addTapeIncludes(tapes[i], includeValues.get(i));
			// getTapeIncludes
			assertEquals(i+1, settings.getTapeIncludes().size());
			assertTrue(settings.getTapeIncludes().containsKey(tapes[i]));
			assertEquals(includeValues.get(i),settings.getTapeIncludes().get(tapes[i]));
			// getTapeIncludes
			assertEquals(includeValues.get(i),settings.getTapeIncludes(tapes[i]));
		}
		assertNull(settings.getTapeIncludes(tapeNotInTypeIDs));	// getTapeIncludes should return null if key not found
		
		// setTapeIncludes
		HashMap<Tape, EnumSet<Settings.ClipBaseTypes>> testHashMap = new HashMap<>();
		for (int i = 0; i<numIndexes; i++)
		{
			testHashMap.put(tapes[i], includeValues.get((numIndexes-1)-i));
		}
		testHashMap.put(tapeNotInTypeIDs, includeValues.get(0));
		
		settings.addTapeIncludes(testHashMap);
		assertEquals(numIndexes+1,settings.getTapeIncludes().size());
		for(int i = 0; i<numIndexes; i++)
		{
			assertEquals(includeValues.get((numIndexes-1)-i), settings.getTapeIncludes(tapes[i]));
		}
		assertEquals(includeValues.get(0), settings.getTapeIncludes(tapeNotInTypeIDs));
		
		// getTapeIncludes - not testing for this anymore because I don't want to get a clone back, I want the actual collection.
//		testHashMap = settings.getTapeIncludes();
//		HashMap<Tape, Integer> testHashMap2 = settings.getTapeIncludes();
//		testHashMap.put(tapes[0], testHashMap.get(tapes[0])+1);
//		// assert that getTapeIncludes is a clone
//		assertEquals(settings.getTapeIncludes(tapes[0]), testHashMap2.get(tapes[0]));
//		assertFalse(settings.getTapeIncludes(tapes[0]) == testHashMap.get(tapes[0]));
//		testHashMap.clear();
//		testHashMap2.clear();
//		assertFalse(settings.getTapeIncludes().size() == 0);
		
		// removeTapeIncludes
		settings.removeTapeIncludes(tapeNotInTypeIDs);
		assertEquals(numIndexes, settings.getTapeIncludes().size());
		assertNull(settings.getTapeIncludes(tapeNotInTypeIDs));
		
		// clearTapeIncludes
		testHashMap = settings.clearTapeIncludes();
		assertEquals(0, settings.getTapeIncludes().size());
		assertEquals(numIndexes, testHashMap.values().size());
	}
	
	
	@Test
	public void biasesTest()
	{
		int numIndexes = 6;
		Settings settings = new Settings();
		String[] typeIDs = new String[]{"type1", "type2", "type3", "type4", "type5", ""};
		double[] percentageBiases = new double[]{0.0, 2.4, 94.2, 13.2, Double.MAX_VALUE, 0.01};
		String typeIDNotInTypeIDs = "notype";

		
		for (int i = 0; i<numIndexes; i++)
		{
			double ret = settings.addBias(typeIDs[i], percentageBiases[i]);
			assertEquals(100.0, ret, 0.001);
			// getBiases
			assertEquals(i+1, settings.getBiases().size());
			assertTrue(settings.getBiases().containsKey(typeIDs[i]));
			assertEquals(percentageBiases[i],settings.getBiases().get(typeIDs[i]), 0.001);
			// getBias
			assertEquals(percentageBiases[i],settings.getBias(typeIDs[i]), 0.001);
		}
		assertEquals(100.0, settings.getBias(typeIDNotInTypeIDs), 0.001);	// getBias should return null if key not found
		
		boolean exceptionThrown = false;
		try {
			settings.addBias(typeIDs[0], -1.0);
		} catch (IllegalArgumentException e) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);
		
		// addBiases
		HashMap<String, Double> testHashMap = new HashMap<>();
		for (int i = 0; i<numIndexes; i++)
		{
			testHashMap.put(typeIDs[i], percentageBiases[(numIndexes-1)-i]);
		}
		testHashMap.put(typeIDNotInTypeIDs, percentageBiases[0]);
		
		settings.addBiases(testHashMap);
		assertEquals(numIndexes+1,settings.getBiases().size());
		for(int i = 0; i<numIndexes; i++)
		{
			assertEquals(percentageBiases[(numIndexes-1)-i], settings.getBias(typeIDs[i]), 0.001);
		}
		assertEquals(percentageBiases[0], settings.getBias(typeIDNotInTypeIDs), 0.001);
		
		// getBiases - not testing for this anymore because I don't want to get a clone back, I want the actual collection.
//		testHashMap = settings.getBiases();
//		HashMap<Integer, Double> testHashMap2 = settings.getBiases();
//		testHashMap.put(typeIDs[0], testHashMap.get(typeIDs[0])+1.0);
//		// assert that getBiases is a clone
//		assertEquals(settings.getBias(typeIDs[0]), testHashMap2.get(typeIDs[0]), 0.001);
//		assertFalse(settings.getBias(typeIDs[0]) == testHashMap.get(typeIDs[0]));
//		testHashMap.clear();
//		testHashMap2.clear();
//		assertFalse(settings.getBiases().size() == 0);
		
		// removeBias
		settings.removeBias(typeIDNotInTypeIDs);
		assertEquals(numIndexes, settings.getBiases().size());
		assertEquals(100.0,settings.getBias(typeIDNotInTypeIDs),0.001);
		assertEquals(100.0,settings.removeBias(typeIDNotInTypeIDs),0.001);
		
		// clearBiases
		testHashMap = settings.clearBiases();
		assertEquals(0, settings.getBiases().size());
		assertEquals(numIndexes, testHashMap.values().size());
	}
	
	@Test
	public void landmarksTest()
	{
		Settings settings = new Settings();
		assertEquals(0, settings.getLandmarks().length);
		Landmark landmark1 = new Landmark("Intro", 152.5);
		Landmark landmark2 = new Landmark("Ending", 242.3);
		Landmark landmark3 = new Landmark("Filler", 365.2);
		Landmark landmark4 = new Landmark("Ending", 15.2);
		Landmark landmark5 = new Landmark("Misc", 306.4);
		Landmark[] landmarks = new Landmark[] {landmark1,landmark2,landmark3,landmark4,landmark5};
		boolean ret;
		
		for(int i = 0; i < landmarks.length; i++)
		{
			ret = settings.addLandmark(landmarks[i]);
			assertTrue(ret);
			assertEquals(i+1, settings.getLandmarks().length);
			boolean found = false;
			for (Landmark landmark : settings.getLandmarks())
			{
				if(landmark == landmarks[i])
				{
					found = true;
					break;
				}
			}
			assertTrue(found);
		}
		for(int i = landmarks.length - 1; i >= 0; i--)
		{
			ret = settings.removeLandmark(landmarks[i]);
			assertTrue(ret);
			assertEquals(i, settings.getLandmarks().length);
			boolean found = false;
			for (Landmark landmark : settings.getLandmarks())
			{
				if(landmark == landmarks[i])
				{
					found = true;
					break;
				}
			}
			assertFalse(found);
		}
		for(int i = 0; i < landmarks.length; i++)
		{
			ret = settings.addLandmark(landmarks[i]);
			assertEquals(i+1, settings.getLandmarks().length);
		}

		TreeSet<Landmark> retTypeList;
		retTypeList = settings.clearLandmarks();
		assertNotNull(retTypeList);
		assertEquals(0, settings.getLandmarks().length);
		assertEquals(landmarks.length, retTypeList.size());
		for(int i = 0; i < landmarks.length; i++)
		{
			assertTrue(retTypeList.contains(landmarks[i]));
		}
		
		// addLandmarks
		assertTrue(settings.addLandmarks(retTypeList));
		assertFalse(settings.addLandmarks(retTypeList));
		
		// addLandmark null exception
		boolean fail = true;
		try {
			settings.addLandmark(null);
		} catch (NullPointerException e) {
			fail = false;
		}
		if(fail)
		{
			fail();
		}
		
		// addLandmarks null exception1		THIS IS UMPOSSIBLE WITH TREESETS. OBSOLETE AFTER MOVING AWAY FROM ARRAYLIST
//		fail = true;
//		settings.clearLandmarks();
//		
//		TreeSet<Landmark> nullLandmarkList = new TreeSet<Landmark>();
//		nullLandmarkList.addAll(retTypeList);
//		nullLandmarkList.add(null);
//		
//		try {
//			settings.addLandmarks(nullLandmarkList);
//		} catch (NullPointerException e) {
//			fail = false;
//		}
//		if(fail)
//		{
//			fail();
//		}
		
		settings.clearLandmarks();
		
		assertTrue(settings.addLandmark(landmark1));
		assertFalse(settings.addLandmark(landmark1));
		assertTrue(settings.addLandmark(landmark2.getType(), landmark2.getTime()));
		assertFalse(settings.addLandmark(landmark1.getType(), landmark2.getTime()));
		assertTrue(settings.addLandmark(landmark1.getType(), landmark3.getTime()));
		
		assertEquals(3, settings.getLandmarks().length);
	}
}
