package tests;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import model.Landmark;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;


@RunWith(value = Parameterized.class)
public class LandmarkTest {

	private double testLandmarkTime;
	private double testLandmarkTime2;
	private String testTypeID;
	private String testTypeID2;
	
	
	@Parameters
	public static Collection<Object[]> data() 		// Iterates tests through the specified values.
	{
	   Object[][] data = new Object[][] 
	   { 
		   {-42.0, -15.0, "val", "val2"},{-0.5, Double.MAX_VALUE, "", "val3"},{0.0, 4.0, "", ""},
		   {Double.MIN_VALUE, 42.2, "", ""},{142.0, -42332.2, "", ""},{34242.4, 0.0, "", ""},
		   {Double.MIN_VALUE, Double.MAX_VALUE, "", ""}
	   };
	   return Arrays.asList(data);
	}
	
	
	public LandmarkTest(double landMarkTime, double landMarkTime2, String typeID, String typeID2)
	{
		testLandmarkTime = landMarkTime;
		testLandmarkTime2 = landMarkTime2;
		
		testTypeID = typeID;
		testTypeID2 = typeID2;
	}
	
	
	@Test
	public void constructorTest()
	{
		Landmark Landmark = new Landmark(testTypeID, testLandmarkTime);
		assertEquals(testTypeID,Landmark.getType());
		assertEquals(testLandmarkTime, Landmark.getTime(), 0.001);
		
		Landmark = new Landmark(testTypeID2, testLandmarkTime2);
		assertEquals(testTypeID2,Landmark.getType());
		assertEquals(testLandmarkTime2, Landmark.getTime(), 0.001);
	}
	
	@Test 
	public void TypeIDTest()
	{
		Landmark Landmark = new Landmark(testTypeID, testLandmarkTime);
		assertEquals(testTypeID,Landmark.getType());
		Landmark.setType(testTypeID2);
		assertEquals(testTypeID2,Landmark.getType());
	}
	
	@Test 
	public void timeTest()
	{
		Landmark Landmark = new Landmark(testTypeID, testLandmarkTime);
		assertEquals(testLandmarkTime, Landmark.getTime(), 0.001);
		Landmark.setTime(testLandmarkTime2);
		assertEquals(testLandmarkTime2, Landmark.getTime(), 0.001);
		Landmark.setTime(Double.NaN);
		assertEquals(Double.NaN, Landmark.getTime(), 0.001);
	}
	
}
