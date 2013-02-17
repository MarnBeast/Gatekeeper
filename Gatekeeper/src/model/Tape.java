package model;

import java.util.ArrayList;


public class Tape {	
	private String name;
	
	private IDListComparable<String> typesIdList;
	
	private ArrayList<Clip> introsArrayList;
	private ArrayList<Clip> endingsArrayList;
	private ArrayList<Clip> fillersArrayList;
	private ArrayList<Clip> miscArrayList;
	
	private Settings defaultSettings;
	
	
	public Tape()
	{
		typesIdList = new IDListComparable<String>();
		
		introsArrayList = new ArrayList<Clip>();
		endingsArrayList = new ArrayList<Clip>();
		fillersArrayList = new ArrayList<Clip>();
		miscArrayList = new ArrayList<Clip>();
		
		defaultSettings = new Settings();
		name = "";
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @return the typesIdList
	 */
	public IDListComparable<String> getTypesIdList() {
		return typesIdList;
	}


	/**
	 * @param typesIdList the typesIdList to set
	 */
	public void setTypesIdList(IDListComparable<String> typesIdList) {
		this.typesIdList = typesIdList;
	}


	/**
	 * @return the introsArrayList
	 */
	public ArrayList<Clip> getIntrosArrayList() {
		return introsArrayList;
	}


	/**
	 * @param introsArrayList the introsArrayList to set
	 */
	public void setIntrosArrayList(ArrayList<Clip> introsArrayList) {
		this.introsArrayList = introsArrayList;
	}


	/**
	 * @return the endingsArrayList
	 */
	public ArrayList<Clip> getEndingsArrayList() {
		return endingsArrayList;
	}


	/**
	 * @param endingsArrayList the endingsArrayList to set
	 */
	public void setEndingsArrayList(ArrayList<Clip> endingsArrayList) {
		this.endingsArrayList = endingsArrayList;
	}


	/**
	 * @return the fillersArrayList
	 */
	public ArrayList<Clip> getFillersArrayList() {
		return fillersArrayList;
	}


	/**
	 * @param fillersArrayList the fillersArrayList to set
	 */
	public void setFillersArrayList(ArrayList<Clip> fillersArrayList) {
		this.fillersArrayList = fillersArrayList;
	}


	/**
	 * @return the miscArrayList
	 */
	public ArrayList<Clip> getMiscArrayList() {
		return miscArrayList;
	}


	/**
	 * @param miscArrayList the miscArrayList to set
	 */
	public void setMiscArrayList(ArrayList<Clip> miscArrayList) {
		this.miscArrayList = miscArrayList;
	}


	/**
	 * @return the defaultSettings
	 */
	public Settings getDefaultSettings() {
		return defaultSettings;
	}


	/**
	 * @param defaultSettings the defaultSettings to set
	 */
	public void setDefaultSettings(Settings defaultSettings) {
		this.defaultSettings = defaultSettings;
	}
	
	
}
