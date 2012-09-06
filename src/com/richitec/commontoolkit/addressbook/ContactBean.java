package com.richitec.commontoolkit.addressbook;

import java.io.Serializable;
import java.util.ArrayList;

public class ContactBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8836226421304538227L;

	// id
	private Integer id;
	// groups
	private ArrayList<String> groups;
	// display name
	private String displayName;
	// full names
	private ArrayList<String> fullNames;
	// name phonetics
	private ArrayList<String[]> namePhonetics;
	// phone numbers
	private ArrayList<String> phoneNumbers;
	// photo
	private Byte[] photo;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ArrayList<String> getGroups() {
		return groups;
	}

	public void setGroups(ArrayList<String> groups) {
		this.groups = groups;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public ArrayList<String> getFullNames() {
		return fullNames;
	}

	public void setFullNames(ArrayList<String> fullNames) {
		this.fullNames = fullNames;
	}

	public ArrayList<String[]> getNamePhonetics() {
		return namePhonetics;
	}

	public void setNamePhonetics(ArrayList<String[]> namePhonetics) {
		this.namePhonetics = namePhonetics;
	}

	public ArrayList<String> getPhoneNumbers() {
		return phoneNumbers;
	}

	public void setPhoneNumbers(ArrayList<String> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	public Byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(Byte[] photo) {
		this.photo = photo;
	}

}
