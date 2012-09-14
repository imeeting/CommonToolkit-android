package com.richitec.commontoolkit.addressbook;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8836226421304538227L;

	// aggregated id
	private Long id;
	// raw ids map. key: rawId and value: account name
	private Map<Long, String> rawIds;
	// groups
	private List<String> groups;
	// display name
	private String displayName;
	// full names
	private List<String> fullNames;
	// name phonetics
	private List<List<String>> namePhonetics;
	// name phonetics string
	private String namePhoneticsString;
	// phone numbers
	private List<String> phoneNumbers;
	// photo
	private byte[] photo;

	// extension map
	private Map<String, Object> extension;

	// ContactBean constructor
	public ContactBean() {
		super();

		// init extension map
		extension = new HashMap<String, Object>();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Map<Long, String> getRawIds() {
		return rawIds;
	}

	public void setRawIds(Map<Long, String> rawIds) {
		this.rawIds = rawIds;
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

		// set name phonetics string
		this.namePhoneticsString = generateNamePhoneticsString(namePhonetics);
	}

	public String getNamePhoneticsString() {
		return namePhoneticsString;
	}

	public List<String> getPhoneNumbers() {
		return phoneNumbers;
	}

	public void setPhoneNumbers(List<String> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	public Map<String, Object> getExtension() {
		return extension;
	}

	public void setExtension(Map<String, Object> extension) {
		this.extension = extension;
	}

	// generate contact name phonetics string
	private String generateNamePhoneticsString(List<List<String>> namePhonetics) {
		StringBuilder _namePhoneticsStringBuilder = null;

		if (null != namePhonetics) {
			// init name phonetics string builder
			_namePhoneticsStringBuilder = new StringBuilder();

			for (List<String> _nameCharPhoneticsList : namePhonetics) {
				_namePhoneticsStringBuilder.append(_nameCharPhoneticsList
						.get(0));
			}
		}

		return null == _namePhoneticsStringBuilder ? null
				: _namePhoneticsStringBuilder.toString();
	}

	@Override
	public String toString() {
		// init contact description
		StringBuilder _contactDescription = new StringBuilder();

		// append contact id, display name, groups, fullNames, name phonetics,
		// phone numbers and photo
		_contactDescription.append("contact aggregated id: ").append(id)
				.append(", ");
		_contactDescription.append("raw ids: ").append(rawIds).append(", ");
		_contactDescription.append("display name: ").append(displayName)
				.append(", ");
		_contactDescription.append("groups: ").append(groups).append(", ");
		_contactDescription.append("fullNames: ").append(fullNames)
				.append(", ");
		_contactDescription.append("name phonetics: ").append(namePhonetics)
				.append(", ");
		_contactDescription.append("phone numbers: ").append(phoneNumbers)
				.append(", ");
		_contactDescription.append("photo: ").append(photo).append(", ");
		_contactDescription.append("extension: ").append(extension)
				.append("\n");

		return _contactDescription.toString();
	}

}
