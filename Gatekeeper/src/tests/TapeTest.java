package tests;

import static org.junit.Assert.*;

import java.io.File;

import javafx.application.Application;
import javafx.stage.Stage;

import model.Clip;
import model.Constants;
import model.Settings;
import model.Tape;

import org.junit.BeforeClass;
import org.junit.Test;

public class TapeTest extends Application{
	
	@BeforeClass
	public static void beforeClass()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				Application.launch();
			}
		}).start();
	}

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
	public void addClipTypeTest()
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
	}

	
	
	@Override
	public void start(Stage primaryStage) throws Exception
	{
	}
}
