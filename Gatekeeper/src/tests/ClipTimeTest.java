package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import model.Clip;
import model.ClipTime;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;

import tests.PartiallyParameterized.NonParameterized;


@RunWith(value = PartiallyParameterized.class)
public class ClipTimeTest {

	private double testClipTime;
	private double testClipTime2;
	private Clip testClip;
	private Clip testClip2;
	
	
	@Parameters
	public static Collection<Object[]> data() 		// Iterates constructorTest and setPlacementPathTest through the specified values.
	{
	   Object[][] data = new Object[][] 
	   { 
		   {-42.0, -15.0},{-0.5, Double.MAX_VALUE},{0.0, 4.0},{0.5, 23332.2},{Double.MIN_VALUE,42.2},{142.0,-42332.2},{34242.4,0.0}, {Double.MIN_VALUE, Double.MAX_VALUE}
	   };
	   return Arrays.asList(data);
	}
	
	
	public ClipTimeTest(double clipTime, double clipTime2)
	{
		testClipTime = clipTime;
		testClipTime2 = clipTime2;
		
		String clipPath = "src/tests/Clip01.mp4";
		String clip2Path = "src/tests/Clip 02.mp4";
		clipPath = new File(clipPath).toURI().toString();
		clip2Path = new File(clip2Path).toURI().toString();
		testClip = new Clip(clipPath, 15.0, 160.0);
		testClip2 = new Clip(clip2Path, 45.2, 160.0);
	}
	
	@Before
	public void noSetup()
	{
		String clipPath = "src/tests/Clip01.mp4";
		String clip2Path = "src/tests/Clip 02.mp4";
		clipPath = new File(clipPath).toURI().toString();
		clip2Path = new File(clip2Path).toURI().toString();
	}
	
	@Test
	public void constructorTest()
	{
		ClipTime clipTime = new ClipTime(testClip, testClipTime);
		assertSame(testClip,clipTime.getClip());
		assertEquals(testClipTime, clipTime.getTime(), 0.001);
		
		clipTime = new ClipTime(testClip2, testClipTime + 5);
		assertSame(testClip2,clipTime.getClip());
		assertEquals(testClipTime + 5, clipTime.getTime(), 0.001);
	}
	
	@Test 
	@NonParameterized
	public void clipTest()
	{
		ClipTime clipTime = new ClipTime(testClip, testClipTime);
		assertSame(testClip,clipTime.getClip());
		clipTime.setClip(testClip2);
		assertSame(testClip2,clipTime.getClip());
		clipTime.setClip(null);
		assertNull(clipTime.getClip());
	}
	
	@Test 
	public void timeTest()
	{
		ClipTime clipTime = new ClipTime(testClip, testClipTime);
		assertEquals(testClipTime, clipTime.getTime(), 0.001);
		clipTime.setTime(testClipTime2);
		assertEquals(testClipTime2, clipTime.getTime(), 0.001);
		clipTime.setTime(Double.NaN);
		assertEquals(Double.NaN, clipTime.getTime(), 0.001);
	}
	
}
