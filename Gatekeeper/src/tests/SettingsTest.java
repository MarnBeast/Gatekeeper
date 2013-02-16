package tests;

import static org.junit.Assert.*;

import java.util.HashMap;

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
		int[] includeValues = new int[]{0,2,4,-2,Integer.MAX_VALUE, Integer.MIN_VALUE};
		Tape tapeNotInTypeIDs = new Tape();

		
		for (int i = 0; i<numIndexes; i++)
		{
			settings.addTapeIncludes(tapes[i], includeValues[i]);
			// getTapeIncludes
			assertEquals(i+1, settings.getTapeIncludes().size());
			assertTrue(settings.getTapeIncludes().containsKey(tapes[i]));
			assertEquals(includeValues[i],settings.getTapeIncludes().get(tapes[i]), 0.001);
			// getTapeIncludes
			assertEquals(includeValues[i],settings.getTapeIncludes(tapes[i]), 0.001);
		}
		assertNull(settings.getTapeIncludes(tapeNotInTypeIDs));	// getTapeIncludes should return null if key not found
		
		// setTapeIncludes
		HashMap<Tape, Integer> testHashMap = new HashMap<>();
		for (int i = 0; i<numIndexes; i++)
		{
			testHashMap.put(tapes[i], includeValues[(numIndexes-1)-i]);
		}
		testHashMap.put(tapeNotInTypeIDs, includeValues[0]);
		
		settings.addTapeIncludes(testHashMap);
		assertEquals(numIndexes+1,settings.getTapeIncludes().size());
		for(int i = 0; i<numIndexes; i++)
		{
			assertEquals(includeValues[(numIndexes-1)-i], settings.getTapeIncludes(tapes[i]), 0.001);
		}
		assertEquals(includeValues[0], settings.getTapeIncludes(tapeNotInTypeIDs), 0.001);
		
		// getTapeIncludes
		testHashMap = settings.getTapeIncludes();
		HashMap<Tape, Integer> testHashMap2 = settings.getTapeIncludes();
		testHashMap.put(tapes[0], testHashMap.get(tapes[0])+1);
		// assert that getTapeIncludes is a clone
		assertEquals(settings.getTapeIncludes(tapes[0]), testHashMap2.get(tapes[0]), 0.001);
		assertFalse(settings.getTapeIncludes(tapes[0]) == testHashMap.get(tapes[0]));
		testHashMap.clear();
		testHashMap2.clear();
		assertFalse(settings.getTapeIncludes().size() == 0);
		
		// removeTapeIncludes
		settings.removeTapeIncludes(tapeNotInTypeIDs);
		assertEquals(numIndexes, settings.getTapeIncludes().size());
		assertNull(settings.getTapeIncludes(tapeNotInTypeIDs));
		
		// clearTapeIncludes
		settings.clearTapeIncludes();
		assertEquals(0, settings.getTapeIncludes().size());
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
		
		// getBiases
		testHashMap = settings.getBiases();
		HashMap<Integer, Double> testHashMap2 = settings.getBiases();
		testHashMap.put(typeIDs[0], testHashMap.get(typeIDs[0])+1.0);
		// assert that getBiases is a clone
		assertEquals(settings.getBias(typeIDs[0]), testHashMap2.get(typeIDs[0]), 0.001);
		assertFalse(settings.getBias(typeIDs[0]) == testHashMap.get(typeIDs[0]));
		testHashMap.clear();
		testHashMap2.clear();
		assertFalse(settings.getBiases().size() == 0);
		
		// removeBias
		settings.removeBias(typeIDNotInTypeIDs);
		assertEquals(numIndexes, settings.getBiases().size());
		assertNull(settings.getBias(typeIDNotInTypeIDs));
		
		// clearBiases
		settings.clearBiases();
		assertEquals(0, settings.getBiases().size());
	}
}
