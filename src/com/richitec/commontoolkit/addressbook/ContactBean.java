package com.richitec.commontoolkit.addressbook;

import java.io.Serializable;
import java.util.List;

public class ContactBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8836226421304538227L;

	// id
	private Integer id;
	// groups
	private List<String> groups;
	// display name
	private String displayName;
	// full names
	private List<String> fullNames;
	// name phonetics
	private List<List<String>> namePhonetics;
	// phone numbers
	private List<String> phoneNumbers;
	// photo
	private Byte[] photo;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public List<String> getFullNames() {
		return fullNames;
	}

	public void setFullNames(List<String> fullNames) {
		this.fullNames = fullNames;
	}

	public List<List<String>> getNamePhonetics() {
		return namePhonetics;
	}

	public void setNamePhonetics(List<List<String>> namePhonetics) {
		this.namePhonetics = namePhonetics;
	}

	public List<String> getPhoneNumbers() {
		return phoneNumbers;
	}

	public void setPhoneNumbers(List<String> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	public Byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(Byte[] photo) {
		this.photo = photo;
	}

	@Override
	public String toString() {
		// init contact description
		StringBuilder _contactDescription = new StringBuilder();

		// append contact id, display name, groups, fullNames, name phonetics,
		// phone numbers and photo
		_contactDescription.append("contact id: ").append(id).append(", ");
		_contactDescription.append("display name: ").append(displayName)
				.append(", ");
		_contactDescription.append("groups: ").append(groups.toString())
				.append(", ");
		_contactDescription.append("fullNames: ").append(fullNames.toString())
				.append(", ");
		_contactDescription.append("name phonetics: ")
				.append(namePhonetics.toString()).append(", ");
		_contactDescription.append("phone numbers: ")
				.append(phoneNumbers.toString()).append(", ");
		_contactDescription.append("photo: ").append(photo).append("\n");

		return _contactDescription.toString();
	}

}
