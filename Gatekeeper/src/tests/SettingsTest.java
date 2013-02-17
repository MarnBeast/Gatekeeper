package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

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
		assertEquals(0, settings.getLandmarks().size());
	}
	
	
	@Test
	public void tapeIncludesTest()
	{
		int numIndexes = 6;
		Settings settings = new Settings();
		Tape[] tapes = new Tape[]{new Tape(), new Tape(), new Tape(), new Tape(), new Tape(), new Tape()};
		ArrayList<EnumSet<Settings.TapeInclude>> includeValues = new ArrayList<EnumSet<Settings.TapeInclude>>();
		includeValues.add(EnumSet.of(Settings.TapeInclude.INTRO));
		includeValues.add(EnumSet.of(Settings.TapeInclude.INTRO, Settings.TapeInclude.END, Settings.TapeInclude.FILLER));
		includeValues.add(EnumSet.of(Settings.TapeInclude.INTRO, Settings.TapeInclude.END, Settings.TapeInclude.FILLER, Settings.TapeInclude.MISC));
		includeValues.add(EnumSet.of(Settings.TapeInclude.END, Settings.TapeInclude.FILLER, Settings.TapeInclude.MISC));
		includeValues.add(EnumSet.of(Settings.TapeInclude.INTRO, Settings.TapeInclude.END));
		includeValues.add(EnumSet.of(Settings.TapeInclude.FILLER, Settings.TapeInclude.MISC));

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
		HashMap<Tape, EnumSet<Settings.TapeInclude>> testHashMap = new HashMap<>();
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
		int[] typeIDs = new int[]{0,2,4,-2,Integer.MAX_VALUE, Integer.MIN_VALUE};
		double[] percentageBiases = new double[]{0.0, 2.4, 94.2, 13.2, Double.MAX_VALUE, 0.01};
		int typeIDNotInTypeIDs = 15;

		
		for (int i = 0; i<numIndexes; i++)
		{
			settings.addBias(typeIDs[i], percentageBiases[i]);
			// getBiases
			assertEquals(i+1, settings.getBiases().size());
			assertTrue(settings.getBiases().containsKey(typeIDs[i]));
			assertEquals(percentageBiases[i],settings.getBiases().get(typeIDs[i]), 0.001);
			// getBias
			assertEquals(percentageBiases[i],settings.getBias(typeIDs[i]), 0.001);
		}
		assertNull(settings.getBias(typeIDNotInTypeIDs));	// getBias should return null if key not found
		
		boolean exceptionThrown = false;
		try {
			settings.addBias(typeIDs[0], -1.0);
		} catch (IllegalArgumentException e) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);
		
		// setBiases
		HashMap<Integer, Double> testHashMap = new HashMap<>();
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
		assertNull(settings.getBias(typeIDNotInTypeIDs));
		
		// clearBiases
		testHashMap = settings.clearBiases();
		assertEquals(0, settings.getBiases().size());
		assertEquals(numIndexes, testHashMap.values().size());
	}
	
	@Test
	public void landmarksTest()
	{
		Settings settings = new Settings();
		assertEquals(0, settings.getLandmarks().size());
		Landmark landmark1 = new Landmark(0, 152.5);
		Landmark landmark2 = new Landmark(1, 242.3);
		Landmark landmark3 = new Landmark(2, 365.2);
		Landmark landmark4 = new Landmark(1, 15.2);
		Landmark landmark5 = new Landmark(3, 306.4);
		Landmark[] landmarks = new Landmark[] {landmark1,landmark2,landmark3,landmark4,landmark5};
		boolean ret;
		
		for(int i = 0; i < landmarks.length; i++)
		{
			ret = settings.addLandmark(landmarks[i]);
			assertTrue(ret);
			assertEquals(i+1, settings.getLandmarks().size());
			assertTrue(settings.getLandmarks().contains(landmarks[i]));
		}
		for(int i = landmarks.length - 1; i >= 0; i--)
		{
			ret = settings.removeLandmark(landmarks[i]);
			assertTrue(ret);
			assertEquals(i, settings.getLandmarks().size());
			assertFalse(settings.getLandmarks().contains(landmarks[i]));
		}
		for(int i = 0; i < landmarks.length; i++)
		{
			ret = settings.addLandmark(landmarks[i]);
			assertEquals(i+1, settings.getLandmarks().size());
		}

		ArrayList<Landmark> retTypeList;
		retTypeList = settings.clearLandmarks();
		assertNotNull(retTypeList);
		assertEquals(0, settings.getLandmarks().size());
		assertEquals(landmarks.length, retTypeList.size());
		for(int i = 0; i < landmarks.length; i++)
		{
			assertTrue(retTypeList.contains(landmarks[i]));
		}
		
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
		
		assertTrue(settings.addLandmark(landmark1));
		assertFalse(settings.addLandmark(landmark1));
		assertTrue(settings.addLandmark(landmark2.getTypeID(), landmark2.getTime()));
		assertFalse(settings.addLandmark(landmark1.getTypeID(), landmark2.getTime()));
		assertTrue(settings.addLandmark(landmark1.getTypeID(), landmark3.getTime()));
		
		assertEquals(3, settings.getLandmarks().size());
	}
}
