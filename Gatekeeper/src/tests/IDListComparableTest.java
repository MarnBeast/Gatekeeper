package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import model.IDListComparable;

import org.junit.Test;



public class IDListComparableTest 
{
	private String[] testValues = new String[]
	{"The","Quick","Brown","Fox","Jumped","Over","Lazy","Fence."};
	private String[] testValues2 = new String[]
	{"A","Brown","Dog","Jumped","quick","Over","Fox's","Dog"};

	@Test
	public void constructorTest()
	{
		IDListComparable<String> idList = new IDListComparable<String>();
		assertNotNull(idList.values());
		assertEquals(0, idList.values().size());
		assertEquals(0, idList.getsetID(testValues[0]));
		assertEquals(1, idList.getsetID(testValues[1]));
	}
	
	@Test
	public void clearTest1()
	{
		IDListComparable<String> idList = new IDListComparable<String>();
		assertEquals(0, idList.values().size());
		assertEquals(-1, idList.getID(testValues[0]));
		assertEquals(0, idList.getsetID(testValues[0]));
		assertEquals(-1, idList.getID(testValues[1]));
		assertEquals(1, idList.getsetID(testValues[1]));
		idList.clear();
		assertEquals(0, idList.values().size());
	}
	
	/**
	 * Tests the basic id assignment and retrieval of getsetID and getID.
	 */
	@Test
	public void getsetTest1()
	{
		IDListComparable<String> idList = new IDListComparable<String>();
		
		for(int i = 0; i < testValues.length; i++)
		{
			assertEquals(i, idList.values().size());
			assertEquals(-1, idList.getID(testValues[i]));
			assertNull(idList.get(i));
			assertEquals(i, idList.getsetID(testValues[i]));
			assertEquals(i, idList.getID(testValues[i]));
			assertSame(testValues[i], idList.get(i));
			if(i > 0)
			{
				assertNotSame(testValues[i-1], idList.get(i));
			}
		}
		
		try 
		{
			idList.getsetID(null);
			fail();
		}
		catch (NullPointerException e) {}
	}
	
	
	@Test
	public void removeTest()
	{
		IDListComparable<String> idList = new IDListComparable<String>();
		idList.addValues(testValues);
		
		assertSame(testValues[3], idList.get(3));
		assertSame(testValues[3], idList.remove(3));
		assertNull(idList.get(3));
		assertEquals(-1,idList.getID(testValues[3]));

		assertSame(testValues[1], idList.get(1));
		assertSame(1, idList.remove(testValues[1]));
		assertNull(idList.get(1));
		assertEquals(-1,idList.getID(testValues[1]));
	}
	
	
	/**
	 * Tests the ability to recycle cleared IDs.
	 */
	@Test
	public void getsetTest2()
	{
		IDListComparable<String> idList = new IDListComparable<String>();
		idList.addValues(testValues);
		
		assertSame(testValues[3], idList.remove(3));
		assertSame(5, idList.remove(testValues[5]));
		
		
		assertEquals(-1, idList.getID(testValues2[0]));
		assertEquals(3, idList.getsetID(testValues2[0]));
		assertEquals(3, idList.getID(testValues2[0]));
		assertSame(testValues2[0], idList.get(3));
		
		

		assertEquals(-1, idList.getID(testValues2[2]));
		assertEquals(5, idList.getsetID(testValues2[2]));
		assertEquals(5, idList.getID(testValues2[2]));
		assertSame(testValues2[2], idList.get(5));
		
		assertEquals(-1, idList.getID(testValues2[4]));
		assertEquals(testValues.length, idList.getsetID(testValues2[4]));
		assertEquals(testValues.length, idList.getID(testValues2[4]));
		assertSame(testValues2[4], idList.get(testValues.length));
	}
	
	
	@Test
	public void cloneTest()
	{
		IDListComparable<String> idList = new IDListComparable<String>();
		idList.addValues(testValues);
		
		IDListComparable<String> clone = (IDListComparable<String>) idList.clone();
		
		idList.remove(1);
		assertNull(idList.get(1));
		assertSame(testValues[1], clone.get(1));
		
		clone.clear();
		assertEquals(0,clone.values().size());
		assertEquals(testValues.length-1, idList.values().size());
	}
	
	
	@Test
	public void addValuesArrayTest() 
	{
		IDListComparable<String> idList = new IDListComparable<String>();
		
		idList.addValues(testValues);
		assertEquals(testValues.length, idList.values().size());
		for(int i = 0; i < testValues.length; i++)
		{
			assertSame(testValues[i],idList.get(i));
		}
		idList.addValues(testValues2);
		
		// Assert
		assertEquals(testValues.length+testValues2.length/2, idList.values().size());
		for(int i = 0; i < testValues.length; i++)
		{
			assertSame(testValues[i],idList.get(i));
		}
		for(int i = 0; i < testValues2.length/2; i++)
		{
			assertSame(testValues2[i*2],idList.get(i+testValues.length)); 			// all of the evens are in the idList
			assertEquals(i+testValues.length, idList.getID(testValues2[(i*2)])); 	// None of the odds are in the idList
		}
	}
	
	
	@Test
	public void addValuesItterableTest() 
	{
		// ArrayList1
		IDListComparable<String> idList = new IDListComparable<String>();
		ArrayList<String> testValuesArrayList = new ArrayList<String>();
		for(int i = 0; i < testValues.length; i++)
		{
			testValuesArrayList.add(testValues[i]);
		}
		idList.addValues(testValuesArrayList);
		
		assertEquals(testValues.length, idList.values().size());
		for(int i = 0; i < testValues.length; i++)
		{
			assertSame(testValues[i],idList.get(i));
		}
		
		//ArrayList2
		ArrayList<String> testValuesArrayList2 = new ArrayList<String>();
		for(int i = 0; i < testValues.length; i++)
		{
			testValuesArrayList2.add(testValues2[i]);
		}
		idList.addValues(testValuesArrayList2);
		
		// Assert
		assertEquals(testValues.length + testValues2.length/2, idList.values().size());
		for(int i = 0; i < testValues.length; i++)
		{
			assertSame(testValues[i],idList.get(i));
		}
		for(int i = 0; i < testValues2.length/2; i++)
		{
			assertSame(testValues2[i*2],idList.get(i+testValues.length)); 			// all of the evens are in the idList
			assertEquals(i+testValues.length, idList.getID(testValues2[(i*2)])); 	// None of the odds are in the idList
		}
	}	
	
	
	@Test
	public void mergeTest()
	{
		IDListComparable<String> idList = new IDListComparable<String>();
		idList.addValues(testValues);
		IDListComparable<String> idList2 = new IDListComparable<String>();
		idList2.addValues(testValues2);
		
		idList.merge(idList2);
		
		// Assert
		assertEquals(testValues.length+testValues2.length/2, idList.values().size());
		for(int i = 0; i < testValues.length; i++)
		{
			assertSame(testValues[i],idList.get(i));
		}
		for(int i = 0; i < testValues2.length/2; i++)
		{
			assertSame(testValues2[i*2],idList.get(i+testValues.length)); 			// all of the evens are in the idList
			assertEquals(i+testValues.length, idList.getID(testValues2[(i*2)])); 	// None of the odds are in the idList
		}
	}
	
	
	@Test
	public void convertIDTest()
	{
		IDListComparable<String> idList = new IDListComparable<String>();
		idList.addValues(testValues);
		IDListComparable<String> idList2 = new IDListComparable<String>();
		idList2.addValues(testValues2);
		
		idList.merge(idList2);
		
		// Assert
		int x = testValues.length;
		assertEquals(x, idList.convertID(idList2, 0));
		assertEquals(2, idList.convertID(idList2, 1));
		assertEquals(x+1, idList.convertID(idList2, 2));
		assertEquals(4, idList.convertID(idList2, 3));
		assertEquals(x+2, idList.convertID(idList2, 4));
		assertEquals(5, idList.convertID(idList2, 5));
		assertEquals(x+3, idList.convertID(idList2, 6));
		
		try 
		{
			idList.convertID(idList2, 7);
			fail();
		}
		catch (IllegalArgumentException e) {}
	}
}
