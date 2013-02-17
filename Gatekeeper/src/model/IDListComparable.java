package model;

import java.util.Iterator;

public class IDListComparable<T extends Comparable<T>> extends IDList<T>{
	
	public void merge(IDList<T> list)
	{
		Iterator<T> listIter = list.toArrayList().iterator();
		while(listIter.hasNext())
		{
			T listValue = listIter.next();
			boolean containsEqual = false;
			Iterator<T> thisIter = this.toArrayList().iterator();
			while(thisIter.hasNext())
			{
				T thisValue = thisIter.next();
				if(listValue.equals(thisValue))
				{
					containsEqual = true;
					break;
				}
			}
			if(!containsEqual)
			{
				this.getsetID(listValue);
			}
		}
	}
	
	public int getsetID(T value)
	{
		if(value == null)
		{
			throw new NullPointerException("typeT cannot be null.");
		}
		
		// If value is equal to a value in the IDList, return
		// the ID of that value.
		Iterator<T> thisIter = this.toArrayList().iterator();
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
}
