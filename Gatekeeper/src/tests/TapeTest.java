package tests;

import static org.junit.Assert.*;

import java.awt.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javafx.embed.swing.JFXPanel;

import model.Clip;
import model.Constants;
import model.Settings;
import model.Tape;
import model.Settings.ClipBaseTypes;

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
		assertEquals(0, testSettings.getLandmarks().length);
		assertEquals(0, testSettings.getTapeIncludes().size());
		
		assertNotNull(tape.getClips(ClipBaseTypes.MISC));
		assertEquals(0, tape.getClips(ClipBaseTypes.MISC).length);

		
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
		assertEquals(0, testSettings.getLandmarks().length);
		assertEquals(0, testSettings.getTapeIncludes().size());
		
		assertNotNull(tape.getClips(ClipBaseTypes.MISC));
		assertEquals(0, tape.getClips(ClipBaseTypes.MISC).length);
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
		String clipURI = new File(clipPath).toURI().toString();
		String clip2URI = new File(clip2Path).toURI().toString();
		
		Clip testClip = tape.addClip(clipPath, ClipBaseTypes.MISC);	
		assertEquals(clipURI, testClip.getVideo().getSource());
		
		Clip testClip2 = tape.addClip(clip2Path, ClipBaseTypes.MISC);
		assertEquals(clip2URI, testClip2.getVideo().getSource());
		
		Clip[] tapeClips = tape.getClips(ClipBaseTypes.MISC);
		assertEquals(2.0, tapeClips.length, 0.0);
		assertSame(testClip, tapeClips[0]);
		assertSame(testClip2, tapeClips[1]);
		
		Clip[] addedClips = tape.addClips(new String[]{clipPath, clip2Path}, ClipBaseTypes.MISC);
		tapeClips = tape.getClips(ClipBaseTypes.MISC);
		assertEquals(4.0, tapeClips.length, 0.0);
		assertEquals(2.0, addedClips.length, 0.0);
		assertEquals(clipURI, addedClips[0].getVideo().getSource());
		assertEquals(clip2URI, addedClips[1].getVideo().getSource());
		assertSame(addedClips[0], tapeClips[2]);
		assertSame(addedClips[1], tapeClips[3]);
		
		addedClips = tape.addClips(new String[]{clipPath, clip2Path, clipPath, clip2Path}, ClipBaseTypes.MISC, true);
		tapeClips = tape.getClips(ClipBaseTypes.MISC);
		assertEquals(8.0, tapeClips.length, 0.0);
		assertEquals(4.0, addedClips.length, 0.0);
		assertEquals(clipURI, addedClips[0].getVideo().getSource());
		assertEquals(clip2URI, addedClips[1].getVideo().getSource());
		assertEquals(clipURI, addedClips[2].getVideo().getSource());
		assertEquals(clip2URI, addedClips[3].getVideo().getSource());
		
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
		//clipPath = new File(clipPath).toURI().toString();
		//clip2Path = new File(clip2Path).toURI().toString();
		
		Tape tape = new Tape();
		Clip[] addedClips = tape.addClips(new String[]{clipPath, clip2Path, clipPath, clip2Path}, ClipBaseTypes.MISC);
		
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
		
		tape.removeClip(addedClips[1], ClipBaseTypes.MISC);
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
		tape.removeClip(addedClips[1], ClipBaseTypes.MISC);
		
		tape.addClip(addedClips[1], ClipBaseTypes.MISC);
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
	
	@Test
	public void serializationTest()
	{
		String clipPath = "src/tests/Clip01.mp4";
		String clip2Path = "src/tests/Clip 02.mp4";
		String tapeSavePath = "src/tests/saveTape" + Constants.TAPE_EXTENSION;
		//clipPath = new File(clipPath).toURI().toString();
		//clip2Path = new File(clip2Path).toURI().toString();
		
		Tape tapeOut = new Tape();
		Clip[] addedClips = tapeOut.addClips(new String[]{clipPath, clip2Path, clipPath, clip2Path}, ClipBaseTypes.MISC);
		
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
		
		addedClips[0].addChainedClip(addedClips[2]);
		addedClips[1].addChainedClip(addedClips[3]);
		
		try
		{
			tapeOut.saveTape(tapeSavePath);
		} catch (IOException e)
		{
			String msg = e.getMessage();
			fail();
		}
		
		Tape tapeIn = null;
		FileInputStream fis;
		try
		{
			fis = new FileInputStream(tapeSavePath);
			ObjectInputStream ois = new ObjectInputStream(fis);
			tapeIn = (Tape) ois.readObject();
		} catch (IOException | ClassNotFoundException e)
		{
			String msg = e.getMessage();
			fail();
		}
		
		assertEquals(tapeOut.getName(), tapeIn.getName());
		assertArrayEquals(tapeOut.getTypes(), tapeIn.getTypes());
		
		Settings setOut = tapeOut.getDefaultSettings();
		Settings setIn = tapeIn.getDefaultSettings();
		assertArrayEquals(setOut.getBiases().values().toArray(), setIn.getBiases().values().toArray());
		assertArrayEquals(setOut.getBiases().keySet().toArray(), setIn.getBiases().keySet().toArray());

		Clip[] clipsOut = tapeOut.getClips(ClipBaseTypes.MISC);
		Clip[] clipsIn = tapeIn.getClips(ClipBaseTypes.MISC);
		assertEquals(clipsOut.length, clipsIn.length);
		
		for (int i = 0; i<clipsOut.length; i++)
		{
			Clip clipOut = clipsOut[i];
			Clip clipIn = clipsIn[i];
			assertNotNull(clipIn);
			assertEquals(clipOut.getStartTime(), clipIn.getStartTime(), 0.001);
			assertEquals(clipOut.getTotalTime(), clipIn.getTotalTime(), 0.001);
			assertEquals(clipOut.getPlacePercent(), clipIn.getPlacePercent(), 0.001);
			
			Clip[] chainedOut = new Clip[1];
			clipOut.getChainedClips().toArray(chainedOut);
			Clip[] chainedIn = new Clip[1];
			clipIn.getChainedClips().toArray(chainedIn);
			
			assertEquals(chainedOut.length, chainedIn.length);
			
			java.util.List<Clip> clipsOutList = Arrays.asList(clipsOut);
			java.util.List<Clip> clipsInList = Arrays.asList(clipsIn);
			
			for(int j=0; j<chainedOut.length; j++)
			{
				Clip chainClipOut = chainedOut[j];
				Clip chainClipIn = chainedIn[j];
				
				assertEquals(clipsOutList.indexOf(chainClipOut), clipsInList.indexOf(chainClipIn));
			}
		}
	}
	
}
