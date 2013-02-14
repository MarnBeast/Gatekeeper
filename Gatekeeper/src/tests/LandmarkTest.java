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
	private int testTypeID;
	private int testTypeID2;
	
	
	@Parameters
	public static Collection<Object[]> data() 		// Iterates tests through the specified values.
	{
	   Object[][] data = new Object[][] 
	   { 
		   {-42.0, -15.0, -42, -15},{-0.5, Double.MAX_VALUE, 0, Integer.MAX_VALUE},{0.0, 4.0, 0, 4},
		   {Double.MIN_VALUE, 42.2, Integer.MIN_VALUE, 42},{142.0, -42332.2, 142, -42332},{34242.4, 0.0, 34242, 0},
		   {Double.MIN_VALUE, Double.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE}
	   };
	   return Arrays.asList(data);
	}
	
	
	public LandmarkTest(double landMarkTime, double landMarkTime2, int typeID, int typeID2)
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
		assertEquals(testTypeID,Landmark.getTypeID());
		assertEquals(testLandmarkTime, Landmark.getTime(), 0.001);
		
		Landmark = new Landmark(testTypeID2, testLandmarkTime2);
		assertEquals(testTypeID2,Landmark.getTypeID());
		assertEquals(testLandmarkTime2, Landmark.getTime(), 0.001);
	}
	
	@Test 
	public void TypeIDTest()
	{
		Landmark Landmark = new Landmark(testTypeID, testLandmarkTime);
		assertEquals(testTypeID,Landmark.getTypeID());
		Landmark.setTypeID(testTypeID2);
		assertEquals(testTypeID2,Landmark.getTypeID());
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
