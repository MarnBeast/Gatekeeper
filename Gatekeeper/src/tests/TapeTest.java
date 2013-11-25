package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javafx.embed.swing.JFXPanel;

import model.Clip;
import model.Constants;
import model.Settings;
import model.Tape;

import org.junit.Before;
import org.junit.Test;


public class TapeTest
{
	@Before		
	public void noSetup() 
	{
		new JFXPanel();	// prepare JavaFX toolkit and environment
	}
	
	@Test
	public void constructorTest()
	{
		Tape tape = new Tape();
		assertEquals("", tape.getName());
		
		// Ensure only the default type strings are included and that they are sorted appropriately
		String[] typeStrings = tape.getTypes();
		assertNotNull(typeStrings);
		assertEquals(Constants.DEFAULT_TYPES.length, typeStrings.length);
		ArrayList<String> sortedDefaults = new ArrayList(Arrays.asList(Constants.DEFAULT_TYPES));
		Collections.sort(sortedDefaults);
		for(int i = 0; i < sortedDefaults.size(); i++)
		{
			assertEquals(sortedDefaults.get(i), typeStrings[i]);
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
		
		// Ensure only the default type strings are included and that they are sorted appropriately
		typeStrings = tape.getTypes();
		assertNotNull(typeStrings);
		assertEquals(Constants.DEFAULT_TYPES.length, typeStrings.length);
		for(int i = 0; i < sortedDefaults.size(); i++)
		{
			assertEquals(sortedDefaults.get(i), typeStrings[i]);
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
	
	@Test
	public void nameTest()
	{
		String testName = "test name";
		Tape tape = new Tape(testName);
		assertEquals(testName, tape.getName());
		testName = "test name 2!";
		tape.setName(testName);
		assertEquals(testName, tape.getName());
		testName = null;
		tape.setName(testName);
		assertEquals(testName, tape.getName());
	}
	
	@Test
	public void addAndGetClipTest()
	{
		Tape tape = new Tape();
		
		String clipPath = "src/tests/Clip01.mp4";
		String clip2Path = "src/tests/Clip 02.mp4";
		clipPath = new File(clipPath).toURI().toString();
		clip2Path = new File(clip2Path).toURI().toString();
		
		Clip testClip = tape.addClip(clipPath);	
		assertEquals(clipPath, testClip.getVideo().getSource());
		
		Clip testClip2 = tape.addClip(clip2Path);
		assertEquals(clip2Path, testClip2.getVideo().getSource());
		
		Clip[] tapeClips = tape.getClips();
		assertEquals(2.0, tapeClips.length, 0.0);
		assertSame(testClip, tapeClips[0]);
		assertSame(testClip2, tapeClips[1]);
		
		Clip[] addedClips = tape.addClips(new String[]{clipPath, clip2Path});
		tapeClips = tape.getClips();
		assertEquals(4.0, tapeClips.length, 0.0);
		assertEquals(2.0, addedClips.length, 0.0);
		assertEquals(clipPath, addedClips[0].getVideo().getSource());
		assertEquals(clip2Path, addedClips[1].getVideo().getSource());
		assertSame(addedClips[0], tapeClips[2]);
		assertSame(addedClips[1], tapeClips[3]);
		
		addedClips = tape.addClips(new String[]{clipPath, clip2Path, clipPath, clip2Path}, true);
		tapeClips = tape.getClips();
		assertEquals(8.0, tapeClips.length, 0.0);
		assertEquals(4.0, addedClips.length, 0.0);
		assertEquals(clipPath, addedClips[0].getVideo().getSource());
		assertEquals(clip2Path, addedClips[1].getVideo().getSource());
		assertEquals(clipPath, addedClips[2].getVideo().getSource());
		assertEquals(clip2Path, addedClips[3].getVideo().getSource());
		
		testClip = addedClips[0];
		assertSame(testClip, tapeClips[4]);
		assertEquals(0.0, testClip.getStartTime(), 0.0);
		assertEquals(142.8, testClip.getTotalTime(), 0.1);
		testClip = addedClips[1];
		assertSame(testClip, tapeClips[5]);
		assertEquals(14.5, testClip.getStartTime(), 0.1);
		assertEquals(142.8, testClip.getTotalTime(), 0.1);
		testClip = addedClips[2];
		assertSame(testClip, tapeClips[6]);
		assertEquals(71.3, testClip.getStartTime(), 0.1);
		assertEquals(142.8, testClip.getTotalTime(), 0.1);
		testClip = addedClips[3];
		assertSame(testClip, tapeClips[7]);
		assertEquals(85.9, testClip.getStartTime(), 0.1);
		assertEquals(142.8, testClip.getTotalTime(), 0.1);
	}
	
	@Test
	public void typeTest()
	{
		String clipPath = "src/tests/Clip01.mp4";
		String clip2Path = "src/tests/Clip 02.mp4";
		clipPath = new File(clipPath).toURI().toString();
		clip2Path = new File(clip2Path).toURI().toString();
		
		Tape tape = new Tape();
		Clip[] addedClips = tape.addClips(new String[]{clipPath, clip2Path, clipPath, clip2Path});
		
		String[] types = new String[]
		{"onetype", "notherT", "T3JudgementDay", "Here's a Type", "Type!", "", "5", "test"};
		
//		ArrayList<String> sortedTypesList = new ArrayList(Arrays.asList(types));
//		sortedTypesList.addAll(Arrays.asList(Constants.DEFAULT_TYPES));
//		Collections.sort(sortedTypesList);
//		String[] sortedTypes = sortedTypesList.toArray(new String[0]);
		
		addedClips[0].addType(types[0]);
		addedClips[0].addType(types[1]);
		addedClips[0].addType(types[2]);

		addedClips[1].addType(types[0]);
		addedClips[1].addType(types[1]);
		addedClips[1].addType(types[3]);

		addedClips[2].addType(types[0]);
		addedClips[2].addType(types[2]);
		addedClips[2].addType(types[6]);
		
		addedClips[3].addType(types[5]);
		addedClips[3].addType(types[6]);
		addedClips[3].addType(types[2]);
		
		String[] tapeTypes = tape.getTypes();
		assertEquals(6 + Constants.DEFAULT_TYPES.length, tapeTypes.length);
		assertEquals(types[5], tapeTypes[0]);
		assertEquals(types[6], tapeTypes[1]);
		assertEquals(Constants.DEFAULT_TYPES[1], tapeTypes[2]);
		assertEquals(Constants.DEFAULT_TYPES[2], tapeTypes[3]);
		assertEquals(types[3], tapeTypes[4]);
		assertEquals(Constants.DEFAULT_TYPES[0], tapeTypes[5]);
		assertEquals(types[2], tapeTypes[6]);
		assertEquals(types[1], tapeTypes[7]);
		assertEquals(types[0], tapeTypes[8]);
		
		tape.removeClip(addedClips[1]);
		tapeTypes = tape.getTypes();
		boolean type0remains = false;
		boolean type1remains = false;
		for (String type : tapeTypes)
		{
			if(type == types[3])
			{
				fail();
			}
			else if(type == types[0])
			{
				type0remains = true;
			}
			else if(type == types[1])
			{
				type1remains = true;
			}
		}
		assertTrue(type0remains && type1remains);
		tape.removeClip(addedClips[1]);
		
		tape.addClip(addedClips[1]);
		tapeTypes = tape.getTypes();
		assertEquals(types[3], tapeTypes[4]);
		
		addedClips[1].removeType(types[3]);
		tapeTypes = tape.getTypes();
		for (String type : tapeTypes)
		{
			if(type == types[3])
			{
				fail();
			}
		}
	}
	
}
