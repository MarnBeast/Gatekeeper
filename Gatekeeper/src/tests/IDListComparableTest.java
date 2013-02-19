package tests;

import model.IDListComparable;

import org.junit.Test;

public class IDListComparableTest 
{
	private String[] testValues = new String[]
	{"The","Quick","Brown","Fox","Jumped","Over","Lazy","Fence."};
	private String[] testValues2 = new String[]
	{"A","Brown","Dog","Jumped","quick","Over","Fox's","Fence."};

	@Test
	private void constructorTest()
	{
		IDListComparable<String> idList = new IDListComparable<String>();
	}
}
