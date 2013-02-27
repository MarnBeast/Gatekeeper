package tests;

import static org.junit.Assert.*;
import model.Constants;
import model.Settings;
import model.Tape;

import org.junit.Test;

public class TapeTest {

	@Test
	public void constructorTest()
	{
		Tape tape = new Tape();
		assertEquals("", tape.getName());
		
		// Ensure only the default type strings are included
		String[] typeStrings = tape.getTypeStrings();
		assertNotNull(typeStrings);
		assertEquals(Constants.DEFAULT_TYPES.length, typeStrings.length);
		for(int i = 0; i < Constants.DEFAULT_TYPES.length; i++)
		{
			assertEquals(Constants.DEFAULT_TYPES[i], typeStrings[i]);
		}
		
		// Ensure Default Settings is new - empty
		Settings testSettings = tape.getDefaultSettings();
		assertNotNull(testSettings);
		assertEquals(0, testSettings.getBiases().size());
		assertEquals(0, testSettings.getLandmarks().size());
		assertEquals(0, testSettings.getTapeIncludes().size());
		
		assertNotNull(tape.getClips());
		assertEquals(0, tape.getClips().length);

		
		// Repeat test for the Name constructor
		String testName = "testname";
		tape = new Tape(testName);
		assertEquals(testName, tape.getName());
		
		// Ensure only the default type strings are included
		typeStrings = tape.getTypeStrings();
		assertNotNull(typeStrings);
		assertEquals(Constants.DEFAULT_TYPES.length, typeStrings.length);
		for(int i = 0; i < Constants.DEFAULT_TYPES.length; i++)
		{
			assertEquals(Constants.DEFAULT_TYPES[i], typeStrings[i]);
		}
		
		// Ensure Default Settings is new - empty
		testSettings = tape.getDefaultSettings();
		assertNotNull(testSettings);
		assertEquals(0, testSettings.getBiases().size());
		assertEquals(0, testSettings.getLandmarks().size());
		assertEquals(0, testSettings.getTapeIncludes().size());
		
		assertNotNull(tape.getClips());
		assertEquals(0, tape.getClips().length);
	}
}
