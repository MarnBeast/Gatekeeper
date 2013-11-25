package tests;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;

import tests.PartiallyParameterized.NonParameterized;


import model.Clip;
import model.Clip.ClipListener;

@RunWith(value = PartiallyParameterized.class)
public class ClipTest 
{
	//Clip clip;
	String fullClipPath;
	String fullClip2Path;
	double testStartTime;
	double testTotalTime;
	double testPlacePercent;
	
	public ClipTest(double startTime, double totalTime)
	{
		testStartTime = startTime;
		testTotalTime = totalTime;
	}
	
	@Parameters
	 public static Collection<Object[]> data() 		// Iterates constructorTest and setPlacementPathTest through the specified values.
	{
	   Object[][] data = new Object[][] 
	   { 
		   { -1.0, 162.0 }, { 0.0, Double.MAX_VALUE }, { Double.NaN, 162.0 }, { 162.0, 162.0 }, { 170.0, 162.0 },
		   { -1.0, 0.0 }, { Double.MIN_VALUE, Double.MAX_VALUE }, { 42.0, 0.0 }, { 162.0, -1.0 }, { -15.0, -1.0 },
		   { Double.MAX_VALUE, 3826.0 }, { 3827.0, 3826.0 }, { 42.0, 3826.0 }, { 3826.0, Double.MIN_VALUE }, { 3826.0, Double.NaN }
	   };
	   return Arrays.asList(data);
	 }
	 	
	@Before		
	public void noSetup() 
	{
		String clipPath = "src/tests/Clip01.mp4";
		String clip2Path = "src/tests/Clip 02.mp4";
		testPlacePercent = testStartTime * 100.0 / testTotalTime;
		fullClipPath = new File(clipPath).toURI().toString();
		fullClip2Path = new File(clip2Path).toURI().toString();
		
		new JFXPanel();	// prepare JavaFX toolkit and environment
	}
	
	
	
	
	@Test
	public void contructorTest() 
	{
		Clip clip;
		if(testStartTime < 0 || testTotalTime <= 0 || testStartTime >= testTotalTime)
		{
			boolean fail = true;
			try
			{
				clip = new Clip(fullClipPath, testStartTime, testTotalTime);
			}
			catch(IllegalArgumentException ex)
			{
				fail = false;
			}
			if(fail)
			{
				fail();
			}
		}
		else
		{
			clip = new Clip(fullClipPath, testStartTime, testTotalTime);
			assertEquals(testStartTime, clip.getStartTime(), 0);
			assertEquals(testTotalTime, clip.getTotalTime(), 0);
			assertEquals(testPlacePercent, clip.getPlacePercent(), 0);

			assertNotNull(clip.getChainedClips());
			assertEquals(0, clip.getChainedClips().size());
			assertNotNull(clip.getTypes());
			assertEquals(0, clip.getTypes().size());
			
			Media vid = clip.getVideo();
			assertNotNull(vid);
			assertEquals(vid.getSource(), fullClipPath);
			
			clip = new Clip(vid, testStartTime, testTotalTime);
			assertEquals(testStartTime, clip.getStartTime(), 0);
			assertEquals(testTotalTime, clip.getTotalTime(), 0);
			assertEquals(testPlacePercent, clip.getPlacePercent(), 0);
			
			assertNotNull(clip.getChainedClips());
			assertEquals(0, clip.getChainedClips().size());
			assertNotNull(clip.getTypes());
			assertEquals(0, clip.getTypes().size());
			
			Media retvid = clip.getVideo();
			assertNotNull(retvid);
			assertSame(vid, retvid);
		}
	}
	
	@Test
	public void setPlacePercentTest()
	{
		Clip clip = new Clip(fullClipPath, 0.0, 160.0);
		assertEquals(0.0, clip.getPlacePercent(), 0.001);
		assertEquals(0.0, clip.getStartTime(), 0.001);
		assertEquals(160.0, clip.getTotalTime(), 0.001);
		
		if(testStartTime < 0 || testTotalTime <= 0 || testStartTime >= testTotalTime)
		{
			boolean fail = true;
			try
			{
				clip.setPlacePercent(testStartTime, testTotalTime);
			}
			catch(IllegalArgumentException ex)
			{
				fail = false;
			}
			if(fail)
			{
				fail();
			}
		}
		else
		{
			clip.setPlacePercent(testStartTime, testTotalTime);
			assertEquals(testPlacePercent, clip.getPlacePercent(), 0.001);
			assertEquals(testStartTime, clip.getStartTime(), 0.001);
			assertEquals(testTotalTime, clip.getTotalTime(), 0.001);
			
		}
	}
	
	@Test
	@NonParameterized
	public void videoTest()
	{
		Clip clip = new Clip(fullClipPath, 0.0, 160.0);
		Media ret = clip.getVideo();
		assertNotNull(ret);
		assertEquals(fullClipPath, ret.getSource()); 
		
		clip.setVideo(fullClip2Path);
		ret = clip.getVideo();
		assertNotNull(ret);
		assertEquals(fullClip2Path, ret.getSource());
		
		Media set = new Media(fullClipPath);
		clip.setVideo(set);
		ret = clip.getVideo();
		assertSame(set, ret);
		
		// Null Video
		clip.setVideo((Media)null);
		ret = clip.getVideo();
		assertNull(ret);
		
		// Null String
		boolean fail = true;
		try {
			clip.setVideo((String)null);
		} catch (NullPointerException e) {
			fail = false;
		}
		if(fail)
		{
			fail();
		}
		
		// Empty String
		fail = true;
		try {
			clip.setVideo("");
		} catch (IllegalArgumentException e) {
			fail = false;
		}
		if(fail)
		{
			fail();
		}
		
		// Invalid file
		fail = true;
		try {
			clip.setVideo(fullClipPath + "2");
		} catch (MediaException e) {
			fail = false;
		}
		if(fail)
		{
			fail();
		}
	}
	
	@Test
	@NonParameterized
	public void chainsTest()
	{
		double totalTime = 160.0;
		double startTime = 45.0;
		Clip clip = new Clip(fullClipPath, 0.0, 160.0);
		assertEquals(0, clip.getTypes().size());
		Clip clip1 = new Clip(fullClipPath, 0, totalTime);
		Clip clip2 = new Clip(fullClipPath, 1, totalTime);
		Clip clip3 = new Clip(fullClipPath, startTime, totalTime);
		Clip clip4 = new Clip(fullClipPath, startTime+1, totalTime);
		Clip clip5 = new Clip(fullClipPath, totalTime-1, totalTime);
		Clip[] testTypes = new Clip[] {clip1,clip2,clip3,clip4,clip5};
		boolean ret;
		
		for(int i = 0; i < testTypes.length; i++)
		{
			ret = clip.addChainedClip(testTypes[i]);
			assertTrue(ret);
			assertEquals(i+1, clip.getChainedClips().size());
			assertTrue(clip.getChainedClips().contains(testTypes[i]));
		}
		for(int i = 0; i < testTypes.length; i++)
		{
			ret = clip.addChainedClip(testTypes[i]);
			assertFalse(ret);
			assertTrue(clip.getChainedClips().contains(testTypes[i]));
		}
		for(int i = testTypes.length - 1; i >= 0; i--)
		{
			ret = clip.removeChainedClip(testTypes[i]);
			assertTrue(ret);
			assertEquals(i, clip.getChainedClips().size());
			assertFalse(clip.getChainedClips().contains(testTypes[i]));
		}
		for(int i = 0; i < testTypes.length; i++)
		{
			ret = clip.addChainedClip(testTypes[i]);
			assertEquals(i+1, clip.getChainedClips().size());
		}

		ArrayList<Clip> retClipsList;
		// verify that we are getting a clone.
		retClipsList = clip.getChainedClips();
		retClipsList.clear();
		assertTrue(clip.getChainedClips().size() > retClipsList.size());
		
		// clearChainedClips
		retClipsList = clip.clearChainedClips();
		assertNotNull(retClipsList);
		assertEquals(0, clip.getChainedClips().size());
		assertEquals(testTypes.length, retClipsList.size());
		for(int i = 0; i < testTypes.length; i++)
		{
			assertTrue(retClipsList.contains(testTypes[i]));
		}
		
		boolean fail = true;
		try {
			clip.addChainedClip(null);
		} catch (NullPointerException e) {
			fail = false;
		}
		if(fail)
		{
			fail();
		}

	}
	
	@Test
	@NonParameterized
	public void typesTest()
	{
		Clip clip = new Clip(fullClipPath, 0.0, 160.0);
		assertEquals(0, clip.getTypes().size());
		String[] testTypes = new String[] {"type", "2", "Type", "", "More type"};
		boolean ret;

		for(int i = 0; i < testTypes.length; i++)
		{
			ret = clip.addType(testTypes[i]);
			assertTrue(ret);
			assertEquals(i+1, clip.getTypes().size());
			assertTrue(clip.getTypes().contains(testTypes[i]));
		}
		for(int i = 0; i < testTypes.length; i++)
		{
			ret = clip.addType(testTypes[i]);
			assertFalse(ret);
			assertTrue(clip.getTypes().contains(testTypes[i]));
		}
		for(int i = testTypes.length - 1; i >= 0; i--)
		{
			ret = clip.removeType(testTypes[i]);
			assertTrue(ret);
			assertEquals(i, clip.getTypes().size());
			assertFalse(clip.getTypes().contains(testTypes[i]));
		}
		for(int i = 0; i < testTypes.length; i++)
		{
			ret = clip.addType(testTypes[i]);
			assertEquals(i+1, clip.getTypes().size());
		}

		ArrayList<String> retTypeList;
		// verify that we are getting a clone
		retTypeList = clip.getTypes();
		retTypeList.clear();
		assertTrue(clip.getTypes().size() > retTypeList.size());
		
		// clearTypes
		retTypeList = clip.clearTypes();
		assertNotNull(retTypeList);
		assertEquals(0, clip.getTypes().size());
		assertEquals(testTypes.length, retTypeList.size());
		for(int i = 0; i < testTypes.length; i++)
		{
			assertTrue(retTypeList.contains(testTypes[i]));
		}
	}
	
	
	@Test
	@NonParameterized
	public void clipListenerTest()
	{
		double totalTime = 160.0;
		Clip clip1 = new Clip(fullClipPath, 0, totalTime);
		
		class TestListener implements ClipListener
		{
			public String typeAdded = "";
			public String typeRemoved = "";
			@Override
			public void typeRemoved(String type)
			{
				typeRemoved = type;
			}
			
			@Override
			public void typeAdded(String type)
			{
				typeAdded = type;
			}
		};

		TestListener listener1 = new TestListener();
		TestListener listener2 = new TestListener();
		
		clip1.addClipListener(listener1);
		clip1.addClipListener(listener2);
		
		String testType = "Filler";
		clip1.addType(testType);
		assertEquals(testType, listener1.typeAdded);
		assertEquals(testType, listener2.typeAdded);
		assertEquals("", listener1.typeRemoved);
		assertEquals("", listener2.typeRemoved);
		
		String testType2 = "Funny";
		clip1.addType(testType2);
		assertEquals(testType2, listener1.typeAdded);
		assertEquals(testType2, listener2.typeAdded);
		assertEquals("", listener1.typeRemoved);
		assertEquals("", listener2.typeRemoved);
		
		clip1.removeType(testType);
		assertEquals(testType2, listener1.typeAdded);
		assertEquals(testType2, listener2.typeAdded);
		assertEquals(testType, listener1.typeRemoved);
		assertEquals(testType, listener2.typeRemoved);
		
		clip1.removeType(testType2);
		assertEquals(testType2, listener1.typeAdded);
		assertEquals(testType2, listener2.typeAdded);
		assertEquals(testType2, listener1.typeRemoved);
		assertEquals(testType2, listener2.typeRemoved);
	}
	
	
	@Test
	@NonParameterized
	public void loadMediaMetaDataTest()
	{
		double totalTime = 160.0;
		Clip clip1 = new Clip(fullClipPath, 0, totalTime);

		assertThat(clip1.getVideo().getDuration().toSeconds(), equalTo(Double.NaN));
		clip1.loadMediaMetaData();
		assertThat(clip1.getVideo().getDuration().toSeconds(), not(equalTo(Double.NaN)));
		
		Clip clip2 = new Clip(fullClip2Path);
		assertThat(clip2.getStartTime(), equalTo(0.0));
		assertThat(clip2.getTotalTime(), not(equalTo(1.0)));
	}
}
