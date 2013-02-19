package model;

import java.util.Iterator;

public class IDListComparable<T extends Comparable<T>> extends IDList<T>{
	
	public IDListComparable()
	{
		super();
	}
	
	public int getsetID(T value)
	{
		if(value == null)
		{
			throw new NullPointerException("typeT cannot be null.");
		}
		
		// If value is equal to a value in the IDList, return
		// the ID of that value.
		Iterator<T> thisIter = this.values().iterator();
		while(thisIter.hasNext())
		{
			T thisValue = thisIter.next();
			if(value.equals(thisValue))
			{
				return super.getsetID(thisValue);
			}
		}
		
		// Equal value does not exist. Add this to the list
		// and return the new ID.
		return super.getsetID(value);
	}
	
	
	public int getID(T value)
	{
		if(value == null)
		{
			throw new NullPointerException("typeT cannot be null.");
		}
		
		// If value is equal to a value in the IDList, return
		// the ID of that value.
		Iterator<T> thisIter = this.values().iterator();
		while(thisIter.hasNext())
		{
			T thisValue = thisIter.next();
			if(value.equals(thisValue))
			{
				return super.getID(thisValue);
			}
		}
		
		// Equal value does not exist. Return -1
		return -1;
	}
	
	
	
	public void merge(IDList<T> list)
	{
		addValues(list.values());
	}
	
	
	public void addValues(Iterable<T> values)
	{
		Iterator<T> valuesIter = values.iterator();
		while(valuesIter.hasNext())
		{
			T value = valuesIter.next();
			this.getsetID(value);
		}
	}
	
	
	public void addValues(T[] values)
	{
		for(int i = 0; i < values.length; i++)
		{
			this.getsetID(values[i]);
		}
	}
	
	
	public int convertID(IDList<T> list, int ID)
	{
		T value = list.get(ID);
		if(value == null)
		{
			throw new IllegalArgumentException("ID was invalid for the passed in list.");
		}
		return getsetID(value);
	}
	
	
	public IDList<T> clone()
	{
		IDListComparable<T> clone = new IDListComparable<T>();
		clone.merge(this);
		return clone;
	}
}
