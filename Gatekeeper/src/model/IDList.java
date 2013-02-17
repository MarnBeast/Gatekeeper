package model;

import java.util.ArrayList;
import java.util.Iterator;

public class IDList<T> {

	private ArrayList<T> valuesArrayList;
	
	
	public IDList()
	{
		valuesArrayList = new ArrayList<T>();
	}
	
	
	@SuppressWarnings("unchecked")
	public ArrayList<T> toArrayList()
	{
		return (ArrayList<T>) valuesArrayList.clone();
	}
	
	
	public int getsetID(T value)
	{
		if(value == null)
		{
			throw new NullPointerException("typeT cannot be null.");
		}
		if(!valuesArrayList.contains(value))
		{
			valuesArrayList.add(value);
		}

		return valuesArrayList.indexOf(value);
	}
	
	public T get(int ID)
	{
		try {
			return valuesArrayList.get(ID);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	public T remove(int ID)
	{
		try {
			return valuesArrayList.remove(ID);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	public int remove(T value)
	{
		int ID = valuesArrayList.indexOf(value);
		if(ID != -1)
		{
			valuesArrayList.remove(value);
		}

		return ID;
	}
	
	
	public void merge(IDList<T> list)
	{
		Iterator<T> listIter = list.toArrayList().iterator();
		while(listIter.hasNext())
		{
			T listValue = listIter.next();
			if(!valuesArrayList.contains(listValue))
			{
				valuesArrayList.add(listValue);
			}
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
}
