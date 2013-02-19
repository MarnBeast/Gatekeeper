package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import model.IDList;

import org.junit.Test;

public class IDListTest
{
	private Object[] testValues = new Object[]
	{new Object(),new Object(),new Object(),new Object(),new Object(),
	 new Object(),new Object(),new Object(),new Object(),new Object()};
	
	private Object[] testValues2 = new Object[]
			{new Object(),new Object(),new Object(),new Object(),new Object(),
			 new Object(),new Object(),new Object(),new Object(),new Object()};

	@Test
	public void constructorTest()
	{
		IDList<Object> idList = new IDList<Object>();
		assertNotNull(idList.values());
		assertEquals(0, idList.values().size());
		assertEquals(0, idList.getsetID(testValues[0]));
		assertEquals(1, idList.getsetID(testValues[1]));
	}
	
	@Test
	public void clearTest1()
	{
		IDList<Object> idList = new IDList<Object>();
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
		IDList<Object> idList = new IDList<Object>();
		
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
	}
	
	@Test
	public void addValuesArrayTest() 
	{
		IDList<Object> idList = new IDList<Object>();
		
		idList.addValues(testValues);
		assertEquals(testValues.length, idList.values().size());
		for(int i = 0; i < testValues.length; i++)
		{
			assertSame(testValues[i],idList.get(i));
		}
		Object[] semiTestValues = new Object[]{new Object(), testValues[2], testValues[4], new Object()};
		idList.addValues(semiTestValues);
		assertEquals(testValues.length+2, idList.values().size());
		
		for(int i = 0; i < testValues.length; i++)
		{
			assertSame(testValues[i],idList.get(i));
		}
		assertSame(semiTestValues[0],idList.get(testValues.length));
		assertSame(semiTestValues[3],idList.get(testValues.length+1));
	}
	
	
	@Test
	public void addValuesItterableTest() 
	{
		IDList<Object> idList = new IDList<Object>();
		ArrayList<Object> testValuesArrayList = new ArrayList<Object>();
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
		ArrayList<Object> semiTestValuesList = new ArrayList<Object>();
		semiTestValuesList.add(new Object());
		semiTestValuesList.add(testValues[2]);
		semiTestValuesList.add(testValues[4]);
		semiTestValuesList.add(new Object());
		idList.addValues(semiTestValuesList);
		assertEquals(testValues.length+2, idList.values().size());
		
		for(int i = 0; i < testValues.length; i++)
		{
			assertSame(testValues[i],idList.get(i));
		}
		assertSame(semiTestValuesList.get(0),idList.get(testValues.length));
		assertSame(semiTestValuesList.get(3),idList.get(testValues.length+1));
	}	
	
	
	@Test
	public void removeTest()
	{
		IDList<Object> idList = new IDList<Object>();
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
		IDList<Object> idList = new IDList<Object>();
		idList.addValues(testValues);
		
		assertSame(testValues[3], idList.remove(3));
		assertSame(1, idList.remove(testValues[1]));
		
		
		assertEquals(-1, idList.getID(testValues2[0]));
		assertEquals(3, idList.getsetID(testValues2[0]));
		assertEquals(3, idList.getID(testValues2[0]));
		assertSame(testValues2[0], idList.get(3));

		assertEquals(-1, idList.getID(testValues2[1]));
		assertEquals(1, idList.getsetID(testValues2[1]));
		assertEquals(1, idList.getID(testValues2[1]));
		assertSame(testValues2[1], idList.get(1));
		
		assertEquals(-1, idList.getID(testValues2[2]));
		assertEquals(testValues.length, idList.getsetID(testValues2[2]));
		assertEquals(testValues.length, idList.getID(testValues2[2]));
		assertSame(testValues2[2], idList.get(testValues.length));
	}
	
	@Test
	public void cloneTest()
	{
		IDList<Object> idList = new IDList<Object>();
		idList.addValues(testValues);
		
		IDList<Object> clone = idList.clone();
		
		idList.remove(1);
		assertNull(idList.get(1));
		assertSame(testValues[1], clone.get(1));
		
		clone.clear();
		assertEquals(0,clone.values().size());
		assertEquals(testValues.length-1, idList.values().size());
	}
}
