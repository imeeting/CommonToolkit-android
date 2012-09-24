package com.richitec.commontoolkit.addressbook;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sourceforge.pinyin4j.PinyinHelper;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.Groups;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;
import android.util.SparseArray;

import com.richitec.commontoolkit.activityextension.AppLaunchActivity;
import com.richitec.commontoolkit.addressbook.ContactBean.ContactDirtyType;
import com.richitec.commontoolkit.utils.PinyinUtils;
import com.richitec.commontoolkit.utils.StringUtils;

public class AddressBookManager {

	private static final String LOG_TAG = "AddressBookManager";

	// singleton instance
	private static volatile AddressBookManager _singletonInstance;

	// contact sqlite query content resolver
	private ContentResolver _mContentResolver;

	// collator instance
	private Collator _mCollator;

	// all contacts detail info array
	private final List<ContactBean> _mAllContactsInfoArray = new ArrayList<ContactBean>();

	// all contacts detail info map. key: aggregated id and value: contacts
	// detail info
	private final Map<Long, ContactBean> _mAllContactsInfoMap = new HashMap<Long, ContactBean>();

	// all groups members map. key: group id and value: ownership contact
	// aggregated id list
	private final Map<Long, List<Long>> _mAllGroupsMembersMap = new HashMap<Long, List<Long>>();

	// contacts search result map. key: search keyword (String), value: array of
	// contact bean (ContactBean) and contact matching index array map
	private final Map<String, List<Map<String, Object>>> _mContactsSearchResultMap = new HashMap<String, List<Map<String, Object>>>();

	// matching result contact bean and index array key
	private final String MATCHING_RESULT_CONTACT = "matchingResultContact";
	private final String MATCHING_RESULT_INDEXES = "matchingResultIndexs";

	// rawIds value map keys string
	private final String RAWCONTACT_ACCOUNTNAME = "rawContact_accountName";
	private final String RAWCONTACT_VERSION = "rawContact_version";
	private final String RAWCONTACT_DIRTYTYPE = "rawContact_dirtyType";

	// contact name phonetic comparator
	public static final Comparator<ContactBean> CONTACTNAMEPHONETIC_COMPARATOR = new Comparator<ContactBean>() {

		@Override
		public int compare(ContactBean lhs, ContactBean rhs) {
			Integer _ret = -1;

			// get left and right compare contact name phonetics string
			String _leftContactNamePhoneticsString = lhs
					.getNamePhoneticsString();
			String _rightContactNamePhoneticsString = rhs
					.getNamePhoneticsString();

			// compare
			if (null == _leftContactNamePhoneticsString
					&& null == _rightContactNamePhoneticsString) {
				_ret = (int) (lhs.getId() - rhs.getId());
			} else if (null == _leftContactNamePhoneticsString
					&& null != _rightContactNamePhoneticsString) {
				_ret = 1;
			} else if (null != _leftContactNamePhoneticsString
					&& null != _rightContactNamePhoneticsString) {
				_ret = AddressBookManager
						.getInstance()
						.getCollator()
						.compare(_leftContactNamePhoneticsString,
								_rightContactNamePhoneticsString);
				// _ret = _leftContactNamePhoneticsString
				// .compareTo(_rightContactNamePhoneticsString);
			}

			return _ret;
		}
	};

	// contact phone number matching index array key
	public static final String PHONENUMBER_MATCHING_INDEXES = "phoneNumberMatchingIndexes";
	// contact name matching index array key
	public static final String NAME_MATCHING_INDEXES = "nameMatchingIndexes";
	// contact name character fuzzy matched length
	public static Integer NAME_CHARACTER_FUZZYMATCHED_LENGTH = -1;

	// private constructor
	private AddressBookManager() {
		// init content resolver
		_mContentResolver = AppLaunchActivity.getAppContext()
				.getContentResolver();

		// init pinyin4j
		PinyinHelper.toHanyuPinyinStringArray(PinyinUtils.PINYINUTILS_INIT);

		// init collator instance
		_mCollator = Collator.getInstance();
	}

	// get addressBookManager singleton instance
	public static AddressBookManager getInstance() {
		if (null == _singletonInstance) {
			synchronized (AddressBookManager.class) {
				if (null == _singletonInstance) {
					_singletonInstance = new AddressBookManager();
				}
			}
		}

		return _singletonInstance;
	}

	public Collator getCollator() {
		return _mCollator;
	}

	// get original all contacts detail info array
	public List<ContactBean> getAllContactsInfoArray() {
		return _mAllContactsInfoArray;
	}

	// get all name phonetic sorted contacts detail info array
	public List<ContactBean> getAllNamePhoneticSortedContactsInfoArray() {
		List<ContactBean> _allNamePhoneticSortedContactsInfoArray = new ArrayList<ContactBean>();
		_allNamePhoneticSortedContactsInfoArray.addAll(_mAllContactsInfoArray);
		Collections.sort(_allNamePhoneticSortedContactsInfoArray,
				CONTACTNAMEPHONETIC_COMPARATOR);

		return _allNamePhoneticSortedContactsInfoArray;
	}

	// traversal addressBook, important, do it first
	public void traversalAddressBook() {
		Log.d("AddressBookManager", "traversal addressBook");

		// get all contacts detail info
		Log.d(LOG_TAG, "getAllContactsDetailInfo - begin");
		getAllContactsDetailInfo();

		// // add contacts changed ContentObserver
		// _mContentResolver.registerContentObserver(Contacts.CONTENT_URI,
		// false,
		// new ContactsContentObserver());

		Log.d(LOG_TAG, "getAllContactsDetailInfo - end");
	}

	// get all contacts detail info
	private void getAllContactsDetailInfo() {
		// complete contacts detail info step by step
		getAllContactsId7DisplayName();
		getAllContactsRawIds();
		getAllContactsStructuredName();
		getAllContactsPhoneNumbers();
		getAllContactsGroups();
		getAllContactsPhoto();
	}

	// get all contact aggregated id and displayName
	private void getAllContactsId7DisplayName() {
		// define constant
		final String[] _projection = new String[] { Contacts._ID,
				Contacts.DISPLAY_NAME };

		// use contentResolver to query contacts table
		Cursor _contactCursor = _mContentResolver.query(Contacts.CONTENT_URI,
				_projection, null, null, null);

		// check contact cursor and traverse result
		if (null != _contactCursor) {
			while (_contactCursor.moveToNext()) {
				// get aggregated id and display name
				Long _aggregatedId = _contactCursor.getLong(_contactCursor
						.getColumnIndex(Contacts._ID));
				String _displayName = _contactCursor.getString(_contactCursor
						.getColumnIndex(Contacts.DISPLAY_NAME));

				// Log.d(LOG_TAG,
				// "getAllContactsId7DisplayName - aggregatedId: "
				// + _aggregatedId + " and displayName: " + _displayName);

				// check contact has been existed in all contacts detail info
				// map
				if (!_mAllContactsInfoMap.containsKey(_aggregatedId)) {
					// new contact bean
					ContactBean _contact = new ContactBean();

					// set aggregated id
					_contact.setId(_aggregatedId);
					// set displayName
					_contact.setDisplayName(_displayName);

					// add to all contacts detail info map
					_mAllContactsInfoMap.put(_aggregatedId, _contact);

					// add to all contacts detail info array
					_mAllContactsInfoArray.add(_contact);
				}
			}

			// close contact cursor
			_contactCursor.close();
		}
	}

	// get all contact raw ids
	private void getAllContactsRawIds() {
		// define constant
		final String[] _projection = new String[] { RawContacts._ID,
				RawContacts.CONTACT_ID, RawContacts.ACCOUNT_NAME,
				RawContacts.VERSION };

		// use contentResolver to query raw_contacts table
		Cursor _rawIdsCursor = _mContentResolver.query(RawContacts.CONTENT_URI,
				_projection, null, null, null);

		// check rawId cursor and traverse result
		if (null != _rawIdsCursor) {
			while (_rawIdsCursor.moveToNext()) {
				// get aggregated id, raw id, raw contact ownership account name
				// and version
				Long _aggregatedId = _rawIdsCursor.getLong(_rawIdsCursor
						.getColumnIndex(RawContacts.CONTACT_ID));
				Long _rawId = _rawIdsCursor.getLong(_rawIdsCursor
						.getColumnIndex(RawContacts._ID));
				Integer _version = _rawIdsCursor.getInt(_rawIdsCursor
						.getColumnIndex(RawContacts.VERSION));
				String _ownershipAccountName = _rawIdsCursor
						.getString(_rawIdsCursor
								.getColumnIndex(RawContacts.ACCOUNT_NAME));

				// Log.d(LOG_TAG, "getAllContactsRawIds - aggregatedId: "
				// + _aggregatedId + " , rawId: " + _rawId
				// + " and ownership account name: "
				// + _ownershipAccountName);

				// check contact has been existed in all contacts detail info
				// map
				if (_mAllContactsInfoMap.containsKey(_aggregatedId)) {
					// get the contact
					ContactBean _contact = _mAllContactsInfoMap
							.get(_aggregatedId);

					// generate rawIds value map and add ownership account name,
					// version and dirty type to it
					Map<String, Object> _rawIdsValueMap = new HashMap<String, Object>();
					_rawIdsValueMap.put(RAWCONTACT_ACCOUNTNAME,
							_ownershipAccountName);
					_rawIdsValueMap.put(RAWCONTACT_VERSION, _version);
					_rawIdsValueMap.put(RAWCONTACT_DIRTYTYPE,
							ContactDirtyType.NORMAL);

					// check contact raw ids map
					if (null == _contact.getRawIds()) {
						// generate rawIds map and put rawId and rawIds value
						// map to it
						Map<Long, Map<String, Object>> _rawIdsMap = new HashMap<Long, Map<String, Object>>();
						_rawIdsMap.put(_rawId, _rawIdsValueMap);

						// set contact rawIds map
						_contact.setRawIds(_rawIdsMap);
					} else {
						// put rawId and rawIds value list to rawIds map
						_contact.getRawIds().put(_rawId, _rawIdsValueMap);
					}
				}
			}

			// close rawIds cursor
			_rawIdsCursor.close();
		}
	}

	// get all contact structured name
	private void getAllContactsStructuredName() {
		// define constant
		final String[] _projection = new String[] { StructuredName.CONTACT_ID,
				StructuredName.GIVEN_NAME, StructuredName.FAMILY_NAME };
		final String _selection = Data.MIMETYPE + "=?";
		final String[] _selectionArgs = new String[] { StructuredName.CONTENT_ITEM_TYPE };

		// use contentResolver to query data table
		Cursor _nameCursor = _mContentResolver.query(Data.CONTENT_URI,
				_projection, _selection, _selectionArgs, null);

		// check name cursor and traverse result
		if (null != _nameCursor) {
			while (_nameCursor.moveToNext()) {
				// get aggregated id, given name and family name
				Long _aggregatedId = _nameCursor.getLong(_nameCursor
						.getColumnIndex(StructuredName.CONTACT_ID));
				String _givenName = _nameCursor.getString(_nameCursor
						.getColumnIndex(StructuredName.GIVEN_NAME));
				String _familyName = _nameCursor.getString(_nameCursor
						.getColumnIndex(StructuredName.FAMILY_NAME));

				// Log.d(LOG_TAG,
				// "getAllContactsStructuredName - aggregatedId: "
				// + _aggregatedId + " , given name: " + _givenName
				// + " and family name: " + _familyName);

				// check contact has been existed in all contacts detail info
				// map
				if (_mAllContactsInfoMap.containsKey(_aggregatedId)) {
					// get the contact
					ContactBean _contact = _mAllContactsInfoMap
							.get(_aggregatedId);

					// check contact full name list
					if (null == _contact.getFullNames()) {
						// generate full name list, put given name and family
						// name to it and generate name phonetics
						List<String> _fullNamesList = new ArrayList<String>();
						List<List<String>> _namePhoneticsList = new ArrayList<List<String>>();

						// check locale language
						if (Locale.CHINESE
								.getLanguage()
								.equals(AppLaunchActivity.getAppContext()
										.getResources().getConfiguration().locale
										.getLanguage())) {
							// display name
							StringBuilder _displayName = new StringBuilder();

							if (null != _familyName) {
								_fullNamesList.addAll(StringUtils
										.toStringList(_familyName));

								_namePhoneticsList.addAll(PinyinUtils
										.pinyins4String(_familyName));

								_displayName.append(_familyName);
							}
							if (null != _givenName) {
								_fullNamesList.addAll(StringUtils
										.toStringList(_givenName));

								_namePhoneticsList.addAll(PinyinUtils
										.pinyins4String(_givenName));

								if (0 != _displayName.length()) {
									_displayName.append(' ');
								}
								_displayName.append(_givenName);
							}

							// update contact display name
							if (0 != _fullNamesList.size()
									&& (null == _familyName || !_familyName
											.matches("[\u4e00-\u9fa5]"))
									&& (null == _givenName || !_givenName
											.matches("[\u4e00-\u9fa5]"))) {
								_contact.setDisplayName(_displayName.toString());
							}
						} else {
							if (null != _givenName) {
								_fullNamesList.addAll(StringUtils
										.toStringList(_givenName));

								_namePhoneticsList.addAll(PinyinUtils
										.pinyins4String(_givenName));
							}
							if (null != _familyName) {
								_fullNamesList.addAll(StringUtils
										.toStringList(_familyName));

								_namePhoneticsList.addAll(PinyinUtils
										.pinyins4String(_familyName));
							}
						}

						// set contact full names list and name phonetics if
						// have
						if (0 != _fullNamesList.size()) {
							_contact.setFullNames(trimContactFullNames(_fullNamesList));
						}
						if (0 != _namePhoneticsList.size()) {
							_contact.setNamePhonetics(trimContactNamePhonetics(_namePhoneticsList));
						}
					}
				}
			}

			// close name cursor
			_nameCursor.close();
		}
	}

	// get all contact phone numbers
	private void getAllContactsPhoneNumbers() {
		// define constant
		final String[] _projection = new String[] { Phone.CONTACT_ID,
				Phone.NUMBER };
		final String _selection = Data.MIMETYPE + "=?";
		final String[] _selectionArgs = new String[] { Phone.CONTENT_ITEM_TYPE };

		// use contentResolver to query data table
		Cursor _phoneCursor = _mContentResolver.query(Data.CONTENT_URI,
				_projection, _selection, _selectionArgs, null);

		// check phone cursor and traverse result
		if (null != _phoneCursor) {
			while (_phoneCursor.moveToNext()) {
				// get aggregated id and phone number
				Long _aggregatedId = _phoneCursor.getLong(_phoneCursor
						.getColumnIndex(Phone.CONTACT_ID));
				String _phoneNumber = _phoneCursor.getString(_phoneCursor
						.getColumnIndex(Phone.NUMBER));

				// Log.d(LOG_TAG,
				// "getAllContactsPhoneNumbers - aggregated id = "
				// + _aggregatedId + " and phone number = " + _phoneNumber);

				// check contact has been existed in all contacts detail info
				// map
				if (_mAllContactsInfoMap.containsKey(_aggregatedId)) {
					// get the contact
					ContactBean _contact = _mAllContactsInfoMap
							.get(_aggregatedId);

					// check contact phone number list
					if (null == _contact.getPhoneNumbers()) {
						// generate phone number list and add phone number to it
						List<String> _phoneNumbers = new ArrayList<String>();
						_phoneNumbers.add(_phoneNumber);

						// set contact phone numbers list
						_contact.setPhoneNumbers(_phoneNumbers);
					} else {
						// add phone number to phone number list
						_contact.getPhoneNumbers().add(_phoneNumber);
					}
				}
			}

			// close phone cursor
			_phoneCursor.close();
		}
	}

	// get all contact groups
	private void getAllContactsGroups() {
		// define constant
		final String[] _projection = new String[] { GroupMembership.CONTACT_ID,
				GroupMembership.GROUP_ROW_ID };
		final String _selection = Data.MIMETYPE + "=?";
		final String[] _selectionArgs = new String[] { GroupMembership.CONTENT_ITEM_TYPE };
		final String[] _groupProjection = new String[] { Groups._ID,
				Groups.TITLE };

		// use contentResolver to query data table
		Cursor _groupsCursor = _mContentResolver.query(Data.CONTENT_URI,
				_projection, _selection, _selectionArgs, null);

		// check groups cursor and traverse result
		if (null != _groupsCursor) {
			while (_groupsCursor.moveToNext()) {
				// get aggregated id and group row id
				Long _aggregatedId = _groupsCursor.getLong(_groupsCursor
						.getColumnIndex(GroupMembership.CONTACT_ID));
				Long _groupRowId = _groupsCursor.getLong(_groupsCursor
						.getColumnIndex(GroupMembership.GROUP_ROW_ID));

				// Log.d(LOG_TAG, "getAllContactsGroups - aggregated id = "
				// + _aggregatedId + " and group row id = " + _groupRowId);

				// check group has been existed in all group members map
				if (_mAllGroupsMembersMap.containsKey(_groupRowId)) {
					// put the contact aggregated id to contacts aggregated ids
					// list
					_mAllGroupsMembersMap.get(_groupRowId).add(_aggregatedId);
				} else {
					// generate member contacts aggregated ids list and put
					// contact aggregated id to it
					List<Long> _membersAggregatedIdsList = new ArrayList<Long>();
					_membersAggregatedIdsList.add(_aggregatedId);

					// add member contacts aggregated ids list to all groups
					// members map
					_mAllGroupsMembersMap.put(_groupRowId,
							_membersAggregatedIdsList);
				}
			}

			// close groups cursor
			_groupsCursor.close();
		}

		// use contentResolver to query groups table
		_groupsCursor = _mContentResolver.query(Groups.CONTENT_URI,
				_groupProjection, null, null, null);

		// check groups cursor and traverse result
		if (null != _groupsCursor) {
			while (_groupsCursor.moveToNext()) {
				// get group row id and title
				Long _groupRowId = _groupsCursor.getLong(_groupsCursor
						.getColumnIndex(Groups._ID));
				String _groupTitle = _groupsCursor.getString(_groupsCursor
						.getColumnIndex(Groups.TITLE));

				// Log.d(LOG_TAG, "getAllContactsGroups - group row id = "
				// + _groupRowId + " and group title = " + _groupTitle);

				// check group has been existed in all group members map
				if (_mAllGroupsMembersMap.containsKey(_groupRowId)) {
					// get members contacts aggregated ids list
					List<Long> _membersAggregatedIdsList = _mAllGroupsMembersMap
							.get(_groupRowId);

					// add group title to members contacts groups list
					for (Long _membersAggregatedId : _membersAggregatedIdsList) {
						// get contact from all contacts info map
						if (_mAllContactsInfoMap
								.containsKey(_membersAggregatedId)) {
							// get the contact
							ContactBean _contact = _mAllContactsInfoMap
									.get(_membersAggregatedId);

							// get contact group list
							if (null == _contact.getGroups()) {
								// generate contact group list and put group
								// title to it
								List<String> _groupsList = new ArrayList<String>();
								_groupsList.add(_groupTitle);

								// set contact group list
								_contact.setGroups(_groupsList);
							} else {
								// put group title to member contact group list
								_contact.getGroups().add(_groupTitle);
							}
						}
					}
				}
			}

			// close group cursor
			_groupsCursor.close();
		}
	}

	// get all contact photo
	private void getAllContactsPhoto() {
		// define constant
		final String[] _projection = new String[] { Photo.CONTACT_ID,
				Photo.PHOTO };
		final String _selection = Data.MIMETYPE + "=?";
		final String[] _selectionArgs = new String[] { Photo.CONTENT_ITEM_TYPE };

		// use contentResolver to query data table
		Cursor _photoCursor = _mContentResolver.query(Data.CONTENT_URI,
				_projection, _selection, _selectionArgs, null);

		// check photo cursor and traverse result
		if (null != _photoCursor) {
			while (_photoCursor.moveToNext()) {
				// get aggregated id and photo
				Long _aggregatedId = _photoCursor.getLong(_photoCursor
						.getColumnIndex(Photo.CONTACT_ID));
				byte[] _photoData = _photoCursor.getBlob(_photoCursor
						.getColumnIndex(Photo.PHOTO));

				// Log.d(LOG_TAG, "getAllContactsPhoto - aggregated id = "
				// + _aggregatedId + " and photo string = " + _photoData);

				// check contact has been existed in all contacts detail info
				// map
				if (_mAllContactsInfoMap.containsKey(_aggregatedId)) {
					// set contact photo
					_mAllContactsInfoMap.get(_aggregatedId)
							.setPhoto(_photoData);
				}
			}

			// close photo cursor
			_photoCursor.close();
		}
	}

	// trim contact full names list empty string
	private List<String> trimContactFullNames(List<String> origFullNames) {
		for (int i = 0; i < origFullNames.size(); i++) {
			if (origFullNames.get(i).equalsIgnoreCase(" ")) {
				origFullNames.remove(i);
			}
		}

		return origFullNames;
	}

	// trim contact name phonetics list empty phonetic
	private List<List<String>> trimContactNamePhonetics(
			List<List<String>> origNamePhonetics) {
		for (int i = 0; i < origNamePhonetics.size(); i++) {
			if (1 == origNamePhonetics.get(i).size()
					&& origNamePhonetics.get(i).get(0).equalsIgnoreCase(" ")) {
				origNamePhonetics.remove(i);
			}
		}

		return origNamePhonetics;
	}

	// get contacts list by given phone number: full matching
	private List<ContactBean> getContactsListByPhone(String phoneNumber) {
		List<ContactBean> _contacts = new ArrayList<ContactBean>();

		// traversal all contacts detail info array
		for (ContactBean _contact : _mAllContactsInfoArray) {
			// get contact phone numbers list
			List<String> _contactPhoneNumbers = _contact.getPhoneNumbers();

			// check the contact phone numbers
			if (null != _contactPhoneNumbers
					&& 0 != _contactPhoneNumbers.size()
					&& _contactPhoneNumbers.contains(phoneNumber)) {
				_contacts.add(_contact);
			}
		}

		return _contacts;
	}

	// get contact bean object by aggregated id
	public ContactBean getContactByAggregatedId(Long aggregatedId) {
		ContactBean _contact = null;

		if (_mAllContactsInfoMap.containsKey(aggregatedId)) {
			_contact = _mAllContactsInfoMap.get(aggregatedId);
		}

		return _contact;
	}

	// get contacts display name list by given phone number
	public List<String> getContactsDisplayNamesByPhone(String phoneNumber) {
		List<String> _displayNames = new ArrayList<String>();

		// traversal all matched contacts detail info array
		for (ContactBean _contact : getContactsListByPhone(phoneNumber)) {
			_displayNames.add(_contact.getDisplayName());
		}

		// check return display names list
		if (0 == _displayNames.size()) {
			_displayNames.add(phoneNumber);
		}

		return _displayNames;
	}

	// get contacts photo list by given phone number
	public List<byte[]> getContactsPhotosByPhone(String phoneNumber) {
		List<byte[]> _photos = new ArrayList<byte[]>();

		// traversal all matched contacts detail info array
		for (ContactBean _contact : getContactsListByPhone(phoneNumber)) {
			// get contact photo
			byte[] _photo = _contact.getPhoto();

			// check contact photo
			if (null != _photo) {
				_photos.add(_photo);
			}
		}

		// check return photos list
		if (0 == _photos.size()) {
			_photos.add(null);
		}

		return _photos;
	}

	// is contact with the given phone number in address book, return the
	// contact aggregated id if true else return null
	public Long isContactWithPhoneInAddressBook(String phoneNumber) {
		Long _ret = null;

		// traversal all contacts detail info array
		for (ContactBean _contact : _mAllContactsInfoArray) {
			// get contact phone numbers list
			List<String> _contactPhoneNumbers = _contact.getPhoneNumbers();

			// check the contact phone numbers
			if (null != _contactPhoneNumbers
					&& 0 != _contactPhoneNumbers.size()
					&& _contactPhoneNumbers.contains(phoneNumber)) {
				_ret = _contact.getId();

				break;
			}
		}

		return _ret;
	}

	// get contacts list by phone number with sorted type
	public List<ContactBean> getContactsByPhone(String phoneNumber,
			ContactSortedType sortedType) {
		List<ContactBean> _searchedContacts = new ArrayList<ContactBean>();

		// check all contacts detail info array
		if (0 == _mAllContactsInfoArray.size()) {
			getAllContactsDetailInfo();
		}

		// check contacts search phone number
		if (_mContactsSearchResultMap.containsKey(phoneNumber)) {
			for (Map<String, Object> _resultMap : _mContactsSearchResultMap
					.get(phoneNumber)) {
				// get matching result contact
				ContactBean _contact = (ContactBean) _resultMap
						.get(MATCHING_RESULT_CONTACT);

				// add contact to searched result list and reset its phone
				// matching indexes
				_searchedContacts.add(_contact);
				_contact.getExtension().put(PHONENUMBER_MATCHING_INDEXES,
						_resultMap.get(MATCHING_RESULT_INDEXES));
			}
		} else {
			// define contacts search scope
			List<ContactBean> _searchScope = _mAllContactsInfoArray;

			// check search scope and reset scope
			if (phoneNumber.length() >= 2
					&& _mContactsSearchResultMap.keySet().contains(
							phoneNumber.substring(0, phoneNumber.length()))) {
				_searchScope = new ArrayList<ContactBean>();
				for (Map<String, Object> _resultMap : _mContactsSearchResultMap
						.get(phoneNumber.substring(0, phoneNumber.length()))) {
					_searchScope.add((ContactBean) _resultMap
							.get(MATCHING_RESULT_CONTACT));
				}
			}

			// define contacts searched results list
			List<Map<String, Object>> _contactsSearchedResults = new ArrayList<Map<String, Object>>();

			// search in scope
			for (ContactBean _contact : _searchScope) {
				// phone number matching indexe's array array
				List<List<Integer>> _phoneNumberMatchingIndexesList = new ArrayList<List<Integer>>();

				// has phone number matched flag
				boolean _hasOnePhoneNumberMatched = false;

				// get the contact in search scope phone numbers list
				List<String> _contactPhones = _contact.getPhoneNumbers();
				// traversal the contact in search scope each phone number
				if (null != _contactPhones) {
					for (String _contactPhone : _contactPhones) {
						if (_contactPhone.contains(phoneNumber)) {
							_hasOnePhoneNumberMatched = true;

							// add phone number matching index array to phone
							// number matching index array's array
							_phoneNumberMatchingIndexesList
									.add(generateIntRangeList(
											_contactPhone.indexOf(phoneNumber),
											phoneNumber.length()));
						} else {
							// add empty matching index array to phone number
							// matching index array's array
							_phoneNumberMatchingIndexesList
									.add(new ArrayList<Integer>());
						}
					}
				}

				// has one phone number in contact matched the phone number
				// parameter
				if (_hasOnePhoneNumberMatched) {
					// add contact to result
					_searchedContacts.add(_contact);

					// append contact matching indexes array
					_contact.getExtension().put(PHONENUMBER_MATCHING_INDEXES,
							_phoneNumberMatchingIndexesList);

					// generate contact searched result and add it to searched
					// contact array
					Map<String, Object> _contactsSearchedResult = new HashMap<String, Object>();
					_contactsSearchedResult.put(MATCHING_RESULT_CONTACT,
							_contact);
					_contactsSearchedResult.put(MATCHING_RESULT_INDEXES,
							_phoneNumberMatchingIndexesList);

					_contactsSearchedResults.add(_contactsSearchedResult);
				}
			}

			// add contact searched results to contacts search result map
			_mContactsSearchResultMap
					.put(phoneNumber, _contactsSearchedResults);
		}

		// check sorted type
		if (ContactSortedType.PHONETICS == sortedType) {
			Collections.sort(_searchedContacts, CONTACTNAMEPHONETIC_COMPARATOR);
		}

		return _searchedContacts;
	}

	// get contacts list by phone number: sub matching
	public List<ContactBean> getContactsByPhone(String phoneNumber) {
		return this
				.getContactsByPhone(phoneNumber, ContactSortedType.PHONETICS);
	}

	// get contacts list by name with matching type and sorted type
	public List<ContactBean> getContactsByName(String name,
			ContactNameMatchingType nameMatchingType,
			ContactSortedType sortedType) {
		List<ContactBean> _searchedContacts = new ArrayList<ContactBean>();

		// name to lower case
		name = name.toLowerCase();

		// check all contacts detail info array
		if (0 == _mAllContactsInfoArray.size()) {
			getAllContactsDetailInfo();
		}

		// check contacts search name
		if (_mContactsSearchResultMap.containsKey(name)) {
			for (Map<String, Object> _resultMap : _mContactsSearchResultMap
					.get(name)) {
				// get matching result contact
				ContactBean _contact = (ContactBean) _resultMap
						.get(MATCHING_RESULT_CONTACT);

				// add contact to searched result list and reset its name
				// matching indexes
				_searchedContacts.add(_contact);
				_contact.getExtension().put(NAME_MATCHING_INDEXES,
						_resultMap.get(MATCHING_RESULT_INDEXES));
			}
		} else {
			// define contacts search scope
			List<ContactBean> _searchScope = _mAllContactsInfoArray;

			// check search scope and reset scope
			if (name.length() >= 2
					&& _mContactsSearchResultMap.keySet().contains(
							name.substring(0, name.length()))) {
				_searchScope = new ArrayList<ContactBean>();
				for (Map<String, Object> _resultMap : _mContactsSearchResultMap
						.get(name.substring(0, name.length()))) {
					_searchScope.add((ContactBean) _resultMap
							.get(MATCHING_RESULT_CONTACT));
				}
			}

			// define contacts searched results list
			List<Map<String, Object>> _contactsSearchedResults = new ArrayList<Map<String, Object>>();

			// split contact search name
			List<List<String>> _ContactSearchNameSplitList = null;
			if (_searchScope.size() >= 1) {
				// init contact search name split array
				_ContactSearchNameSplitList = splitContactSearchName(name);
			}

			// search in scope
			for (ContactBean _contact : _searchScope) {
				// skip the contact has not structured name
				if (null == _contact.getNamePhonetics()) {
					continue;
				}

				// traversal all search name split array
				for (List<String> _splitObjects : _ContactSearchNameSplitList) {
					// split name unmatch flag
					boolean _splitNameUnmatch = false;

					// name matching indexes map
					SparseArray<Integer> _nameMatchingIndexesMap = new SparseArray<Integer>();

					// compare split objects(List) count with contact name
					// phonetics(List) count
					if (_splitObjects.size() > _contact.getNamePhonetics()
							.size()) {
						continue;
					}

					// check name matching type
					switch (nameMatchingType) {
					case FUZZY:
						// check contact search name matching
						if (!matchSplitNameListWithContactNamePhonetics(
								_splitObjects, _contact.getNamePhonetics())) {
							_splitNameUnmatch = true;
						}
						// fuzzy matched, set name matching indexes list
						else {
							// last split objects list element matched index
							int _lastElementMatchedIndex = 0;

							for (int i = 0; i < _splitObjects.size(); i++) {
								for (int j = _lastElementMatchedIndex; j < _contact
										.getNamePhonetics().size(); j++) {
									// split objects list element matched flag
									boolean _elementMatched = false;

									for (int k = 0; k < _contact
											.getNamePhonetics().get(j).size(); k++) {
										// get the contact name character
										// phonetic
										String _nameCharPhonetic = _contact
												.getNamePhonetics().get(j)
												.get(k);

										// check split objects list each element
										// matched index
										if (_nameCharPhonetic
												.startsWith(_splitObjects
														.get(i))) {
											_elementMatched = true;

											// save split name element matching
											// index
											_lastElementMatchedIndex = j + 1;

											// put matching indexes integer
											// range in name matching indexes
											// map
											_nameMatchingIndexesMap
													.put(j,
															_nameCharPhonetic
																	.equalsIgnoreCase(_contact
																			.getFullNames()
																			.get(j)) ? _splitObjects
																	.get(i)
																	.length()
																	: NAME_CHARACTER_FUZZYMATCHED_LENGTH);

											break;
										}
									}

									// find element matched index, if matched
									if (_elementMatched) {
										break;
									}
								}
							}
						}
						break;

					case ORDER:
					default:
						// slide split objects list on contact name phonetics
						// list
						for (int slideIndex = 0; slideIndex < _contact
								.getNamePhonetics().size()
								- _splitObjects.size() + 1; slideIndex++) {
							// split objects list match flag in particular
							// contact name phonetics list
							boolean _splitObjectsMatched = false;

							// compare each split object in list
							for (int splitObjectIndex = 0; splitObjectIndex < _splitObjects
									.size(); splitObjectIndex++) {
								// one split object in split objects unmatch
								// flag
								boolean _oneSplitObjectUnmatched = false;

								// traversal the particular contact phonetics
								// list
								for (int contactNameCharPhoneticsIndex = 0; contactNameCharPhoneticsIndex < _contact
										.getNamePhonetics()
										.get(slideIndex + splitObjectIndex)
										.size(); contactNameCharPhoneticsIndex++) {
									// matched, contact name char one phonetic
									// has prefix with split object
									if (_contact
											.getNamePhonetics()
											.get(slideIndex + splitObjectIndex)
											.get(contactNameCharPhoneticsIndex)
											.startsWith(
													_splitObjects
															.get(splitObjectIndex))) {
										break;
									} else if (contactNameCharPhoneticsIndex == _contact
											.getNamePhonetics()
											.get(slideIndex + splitObjectIndex)
											.size() - 1) {
										_oneSplitObjectUnmatched = true;
									}
								}

								// one split object in split objects unmatch,
								// break, slide split name array
								if (_oneSplitObjectUnmatched) {
									break;
								}

								// all split object in lists matched, break
								if (!_oneSplitObjectUnmatched
										&& splitObjectIndex == _splitObjects
												.size() - 1) {
									_splitObjectsMatched = true;
									break;
								}
							}

							// one particular split objects list matched, break
							if (_splitObjectsMatched) {
								// set name matching index array
								for (int i = 0; i < _splitObjects.size(); i++) {
									// traversal the contact each name phonetics
									for (String _phonetic : _contact
											.getNamePhonetics().get(
													slideIndex + i)) {
										// get the contact matched name
										// phonetics and add matching indexes to
										// name matching indexes list
										if (_phonetic.startsWith(_splitObjects
												.get(i))) {
											// put matching indexes integer
											// range in name matching indexes
											// map
											_nameMatchingIndexesMap
													.put(slideIndex + i,
															_phonetic
																	.equalsIgnoreCase(_contact
																			.getFullNames()
																			.get(slideIndex
																					+ i)) ? _splitObjects
																	.get(i)
																	.length()
																	: NAME_CHARACTER_FUZZYMATCHED_LENGTH);
										}
									}
								}

								break;
							}

							// all split objects list unmatch, break, goto next
							// contact
							if (!_splitObjectsMatched
									&& slideIndex == _contact
											.getNamePhonetics().size()
											- _splitObjects.size()) {
								_splitNameUnmatch = true;
								break;
							}
						}
						break;
					}

					// has one contact name matched, add it in contact search
					// result list
					if (!_splitNameUnmatch) {
						// add contact to result
						_searchedContacts.add(_contact);

						// append contact matching indexes map
						_contact.getExtension().put(NAME_MATCHING_INDEXES,
								_nameMatchingIndexesMap);

						// generate contact searched result and add it to
						// searched contact array
						Map<String, Object> _contactsSearchedResult = new HashMap<String, Object>();
						_contactsSearchedResult.put(MATCHING_RESULT_CONTACT,
								_contact);
						_contactsSearchedResult.put(MATCHING_RESULT_INDEXES,
								_nameMatchingIndexesMap);

						_contactsSearchedResults.add(_contactsSearchedResult);

						break;
					}
				}
			}

			// add contact searched results to contacts search result map
			_mContactsSearchResultMap.put(name, _contactsSearchedResults);
		}

		// check sorted type
		if (ContactSortedType.PHONETICS == sortedType) {
			Collections.sort(_searchedContacts, CONTACTNAMEPHONETIC_COMPARATOR);
		}

		return _searchedContacts;
	}

	// get contacts list by name(not Chinese character): fuzzy matching
	public List<ContactBean> getContactsByName(String name) {
		return this.getContactsByName(name, ContactNameMatchingType.FUZZY,
				ContactSortedType.PHONETICS);
	}

	// get contact end
	public void getContactEnd() {
		// clear contacts search result map
		_mContactsSearchResultMap.clear();
	}

	// generate integer range array list with location and length
	private List<Integer> generateIntRangeList(Integer location, Integer length) {
		List<Integer> _intRangeList = new ArrayList<Integer>();

		if (length >= 1) {
			for (int i = 0; i < length; i++) {
				_intRangeList.add(location + i);
			}
		}

		return _intRangeList;
	}

	// split contact search name
	private List<List<String>> splitContactSearchName(String contactSearchName) {
		List<List<String>> _splitNamesList = new ArrayList<List<String>>();

		// check contact search name
		if (null == contactSearchName || 0 == contactSearchName.length()) {
			Log.d(LOG_TAG, "null or empty search name string mustn't split");
		} else if (contactSearchName.length() > 1) {
			// get first character and others
			String _firster = contactSearchName.substring(0, 1);
			String _others = contactSearchName.substring(1);

			// add others all
			_splitNamesList.addAll(multipliedFirster7SubSplit(_firster,
					splitContactSearchName(_others)));
		} else {
			// generate only character string list with string
			List<String> _oneCharStringList = new ArrayList<String>();
			_oneCharStringList.add(contactSearchName);

			// put the string list to split names list
			_splitNamesList.add(_oneCharStringList);
		}

		return _splitNamesList;
	}

	// multiplied first string and sub split list
	private List<List<String>> multipliedFirster7SubSplit(String firster,
			List<List<String>> subSplit) {
		List<List<String>> _subSplit = new ArrayList<List<String>>();

		for (List<String> _subSplitList : subSplit) {
			// {x1, x2}
			List<String> _multipliedResult = new ArrayList<String>();
			_multipliedResult.add(firster);
			_multipliedResult.addAll(_subSplitList);

			_subSplit.add(_multipliedResult);

			// check sub split list count
			if (1 == _subSplitList.size()) {
				// {x1x2}
				List<String> _multipliedResult2 = new ArrayList<String>();
				_multipliedResult2.add(firster + _subSplitList.get(0));

				_subSplit.add(_multipliedResult2);
			} else {
				// {x1x2}
				List<String> _multipliedResult2 = new ArrayList<String>();
				_multipliedResult2.add(firster + _subSplitList.get(0));
				_multipliedResult2.addAll(_subSplitList.subList(1,
						_subSplitList.size()));

				_subSplit.add(_multipliedResult2);
			}
		}

		return _subSplit;
	}

	// match split objects list with contact name phonetics and return matching
	// indexes if matched
	private boolean matchSplitNameListWithContactNamePhonetics(
			List<String> splitObjects, List<List<String>> contactNamePhonetics) {
		boolean _ret = false;

		// check split objects list count
		if (1 <= splitObjects.size()
				&& splitObjects.size() <= contactNamePhonetics.size()) {
			// split objects list just has one element
			if (1 == splitObjects.size()) {
				// split objects list matched
				boolean _matched = false;

				for (int i = 0; i < contactNamePhonetics.size(); i++) {
					for (int j = 0; j < contactNamePhonetics.get(i).size(); j++) {
						if (contactNamePhonetics.get(i).get(j)
								.startsWith(splitObjects.get(0))) {
							_ret = _matched = true;

							break;
						}
					}

					// if matched, break immediately
					if (_matched) {
						break;
					}
				}
			} else {
				// slide split objects list in contact name phonetics list
				for (int i = 0; i < contactNamePhonetics.size()
						- splitObjects.size() + 1; i++) {
					// check first element in split objects list and contact
					// name phonetics
					boolean _headerMatched = false;

					for (int j = 0; j < contactNamePhonetics.get(i).size(); j++) {
						if (contactNamePhonetics.get(i).get(j)
								.startsWith(splitObjects.get(0))) {
							_headerMatched = true;

							break;
						}
					}

					// if header not matched, slide split objects list
					if (!_headerMatched) {
						continue;
					}

					// remove the header, compare others left
					List<String> _leftSplitObjects = splitObjects.subList(1,
							splitObjects.size());
					List<List<String>> _contactLeftNamePhonetics = contactNamePhonetics
							.subList(i + 1, contactNamePhonetics.size());

					// left matched
					if (matchSplitNameListWithContactNamePhonetics(
							_leftSplitObjects, _contactLeftNamePhonetics)) {
						_ret = true;

						break;
					}
				}
			}
		}

		return _ret;
	}

	// inner class
	// contact searched sorted type
	public static enum ContactSortedType {
		IDENTITY, PHONETICS
	}

	// contact searched name matching type
	public static enum ContactNameMatchingType {
		FUZZY, ORDER
	}

	// contacts db changed observer
	class ContactsContentObserver extends ContentObserver {

		public ContactsContentObserver() {
			super(new Handler());
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);

			// contacts db changed
			Log.d(LOG_TAG, "contacts database changed");

			// reset all aggregated contacts raw contacts dirty type, deleted
			// first
			for (ContactBean _contact : _mAllContactsInfoArray) {
				for (Map<String, Object> _rawIdValueMap : _contact.getRawIds()
						.values()) {
					_rawIdValueMap.put(RAWCONTACT_DIRTYTYPE,
							ContactDirtyType.DELETEED);
				}
			}

			// all dirty contact aggregated ids list
			List<Long> _allDirtyContactsIdsList = new ArrayList<Long>();

			// traversal raw_contacts table to get contact dirty flag
			// define constant
			final String[] _projection = new String[] { RawContacts.CONTACT_ID,
					RawContacts.VERSION, RawContacts.DELETED };

			// use contentResolver to query raw_contacts table
			Cursor _versionCursor = _mContentResolver.query(
					RawContacts.CONTENT_URI, _projection, null, null, null);

			// check version cursor and traverse result
			if (null != _versionCursor) {
				while (_versionCursor.moveToNext()) {
					// get aggregated id, version and deleted flag
					Long _aggregatedId = _versionCursor.getLong(_versionCursor
							.getColumnIndex(RawContacts.CONTACT_ID));
					Integer _version = _versionCursor.getInt(_versionCursor
							.getColumnIndex(RawContacts.VERSION));
					Integer _deleted = _versionCursor.getInt(_versionCursor
							.getColumnIndex(RawContacts.DELETED));

					// Log.d(LOG_TAG,
					// "ContactsContentObserver - onChange - aggregated id = "
					// + _aggregatedId + " and deleted flag = "
					// + _deleted);

					// skip synchronous deleted
					if (1 == _deleted) {
						continue;
					}

					// check contact has been existed in all contacts detail
					// info map
					if (_mAllContactsInfoMap.containsKey(_aggregatedId)) {
						// one version matched flag
						boolean _oneVersionMatched = false;

						// process the contact rawIds each value list
						for (Map<String, Object> _rawIdValueMap : _mAllContactsInfoMap
								.get(_aggregatedId).getRawIds().values()) {
							// version not equal
							if (_rawIdValueMap.get(RAWCONTACT_VERSION).equals(
									_version)) {
								_oneVersionMatched = true;

								// update the contact's raw contact dirty flag,
								// normal
								_rawIdValueMap.put(RAWCONTACT_DIRTYTYPE,
										ContactDirtyType.NORMAL);

								break;
							}
						}

						// if none version matched, mark as dirty data
						if (!_oneVersionMatched) {
							Log.d(LOG_TAG, "the contact aggregated id = "
									+ _aggregatedId + " changed");

							// update the contact dirty flag, modified
							_mAllContactsInfoMap.get(_aggregatedId).setDirty(
									ContactDirtyType.MODIFIED);
						}
					} else {
						Log.d(LOG_TAG, "the contact aggregated id = "
								+ _aggregatedId + " new added");

						// add to all dirty contacts aggregated ids list
						_allDirtyContactsIdsList.add(_aggregatedId);
					}
				}

				// close version cursor
				_versionCursor.close();
			}

			// check contact and its raw contact dirty type and get all dirty
			// contact ids
			for (ContactBean _contact : _mAllContactsInfoArray) {
				// normal and deleted contact
				if (ContactDirtyType.NORMAL == _contact.getDirty()) {
					// deleted flag
					boolean _deletedContact = true;

					for (Map<String, Object> _rawIdValueMap : _contact
							.getRawIds().values()) {
						if (ContactDirtyType.NORMAL == _rawIdValueMap
								.get(RAWCONTACT_DIRTYTYPE)) {
							_deletedContact = false;

							break;
						}
					}

					if (_deletedContact) {
						Log.d(LOG_TAG, "the contact aggregated id = "
								+ _contact.getId() + " deleted");

						// add to all dirty contacts aggregated ids list
						_allDirtyContactsIdsList.add(_contact.getId());
					}
				} else {
					// add to all dirty contacts aggregated ids list
					_allDirtyContactsIdsList.add(_contact.getId());
				}
			}

			// recover all aggregated contacts raw contacts dirty type
			for (ContactBean _contact : _mAllContactsInfoArray) {
				_contact.setDirty(ContactDirtyType.NORMAL);

				for (Map<String, Object> _rawIdValueMap : _contact.getRawIds()
						.values()) {
					_rawIdValueMap.put(RAWCONTACT_DIRTYTYPE,
							ContactDirtyType.NORMAL);
				}
			}

			Log.d(LOG_TAG, "all dirty contacts ids list = "
					+ _allDirtyContactsIdsList);

			// update dirty contact data
			for (Long _dirtyContactId : _allDirtyContactsIdsList) {
				// define the updating contact
				ContactBean _contact = null;
				// contact new phone numbers list
				List<String> _newPhoneNumbers = new ArrayList<String>();

				// define constant
				final String[] _dataProjection = new String[] { Data.MIMETYPE,
						Data.DISPLAY_NAME, Data.RAW_CONTACT_ID,
						RawContacts.ACCOUNT_NAME, RawContacts.VERSION,
						StructuredName.GIVEN_NAME, StructuredName.FAMILY_NAME,
						Phone.NUMBER, Photo.PHOTO };
				final String _dataSelection = RawContacts.CONTACT_ID + "=?";
				final String[] _dataSelectionArgs = new String[] { _dirtyContactId
						.toString() };

				// use contentResolver to query data table
				Cursor _dataCursor = _mContentResolver.query(Data.CONTENT_URI,
						_dataProjection, _dataSelection, _dataSelectionArgs,
						null);

				// check version cursor and traverse result
				if (null != _dataCursor) {
					while (_dataCursor.moveToNext()) {
						// get mime type
						String _mimeType = _dataCursor.getString(_dataCursor
								.getColumnIndex(Data.MIMETYPE));
						String _displayName = _dataCursor.getString(_dataCursor
								.getColumnIndex(Data.DISPLAY_NAME));
						Long _rawId = _dataCursor.getLong(_dataCursor
								.getColumnIndex(Data.RAW_CONTACT_ID));
						Integer _version = _dataCursor.getInt(_dataCursor
								.getColumnIndex(RawContacts.VERSION));
						String _ownershipAccountName = _dataCursor
								.getString(_dataCursor
										.getColumnIndex(RawContacts.ACCOUNT_NAME));
						String _givenName = _dataCursor.getString(_dataCursor
								.getColumnIndex(StructuredName.GIVEN_NAME));
						String _familyName = _dataCursor.getString(_dataCursor
								.getColumnIndex(StructuredName.FAMILY_NAME));
						String _phoneNumber = _dataCursor.getString(_dataCursor
								.getColumnIndex(Phone.NUMBER));
						byte[] _photoData = _dataCursor.getBlob(_dataCursor
								.getColumnIndex(Photo.PHOTO));

						// Log.d(LOG_TAG, "contact mime type = " + _mimeType
						// + " , display name =" + _displayName
						// + " , raw id = " + _rawId + " , version = "
						// + _version + " , account name = "
						// + _ownershipAccountName + " , given name = "
						// + _givenName + " , family name = "
						// + _familyName + " , number = " + _phoneNumber
						// + " and photo = " + _photoData);

						// check contact has been existed in all contacts detail
						// info
						// map
						if (_mAllContactsInfoMap.containsKey(_dirtyContactId)) {
							// get the contact
							_contact = _mAllContactsInfoMap
									.get(_dirtyContactId);
						} else {
							// generate new contact
							_contact = new ContactBean();
							_contact.setId(_dirtyContactId);
							// generate rawIds value map and add ownership
							// account name, version and dirty type to it
							Map<String, Object> _rawIdsValueMap = new HashMap<String, Object>();
							_rawIdsValueMap.put(RAWCONTACT_ACCOUNTNAME,
									_ownershipAccountName);
							_rawIdsValueMap.put(RAWCONTACT_VERSION, _version);
							_rawIdsValueMap.put(RAWCONTACT_DIRTYTYPE,
									ContactDirtyType.NORMAL);
							// generate rawIds map and put rawId and rawIds
							// value map to it
							Map<Long, Map<String, Object>> _rawIdsMap = new HashMap<Long, Map<String, Object>>();
							_rawIdsMap.put(_rawId, _rawIdsValueMap);
							// set contact rawIds map
							_contact.setRawIds(_rawIdsMap);

							// add the new contact to all contacts detail info
							// map and list
							_mAllContactsInfoMap.put(_dirtyContactId, _contact);
							for (int i = 0; i < _mAllContactsInfoArray.size(); i++) {
								// get the contact in all contacts detail info
								// list
								ContactBean _contactInList = _mAllContactsInfoArray
										.get(i);

								// replace
								if (_dirtyContactId == _contactInList.getId()) {
									_contactInList = _contact;
								} else if (_dirtyContactId < _contactInList
										.getId()) {
									_mAllContactsInfoArray.add(i, _contact);
								}
							}
						}

						// set attributes
						// set display name
						_contact.setDisplayName(_displayName);

						// set contact rawIds map
						if (!_contact.getRawIds().containsKey(_rawId)) {
							// generate rawIds value map and add ownership
							// account name, version and dirty type to it
							Map<String, Object> _rawIdsValueMap = new HashMap<String, Object>();
							_rawIdsValueMap.put(RAWCONTACT_ACCOUNTNAME,
									_ownershipAccountName);
							_rawIdsValueMap.put(RAWCONTACT_VERSION, _version);
							_rawIdsValueMap.put(RAWCONTACT_DIRTYTYPE,
									ContactDirtyType.NORMAL);

							_contact.getRawIds().put(_rawId, _rawIdsValueMap);
						}

						// set full names and name phonetics
						if (StructuredName.CONTENT_ITEM_TYPE.equals(_mimeType)) {
							// generate full name list, put given name and
							// family name to it and generate name phonetics
							List<String> _fullNamesList = new ArrayList<String>();
							List<List<String>> _namePhoneticsList = new ArrayList<List<String>>();
							if (null != _familyName) {
								_fullNamesList.addAll(StringUtils
										.toStringList(_familyName));

								_namePhoneticsList.addAll(PinyinUtils
										.pinyins4String(_familyName));
							}
							if (null != _givenName) {
								_fullNamesList.addAll(StringUtils
										.toStringList(_givenName));

								_namePhoneticsList.addAll(PinyinUtils
										.pinyins4String(_givenName));
							}
							// set contact full names list and name phonetics if
							// have
							if (0 != _fullNamesList.size()) {
								_contact.setFullNames(_fullNamesList);
							}
							if (0 != _namePhoneticsList.size()) {
								_contact.setNamePhonetics(_namePhoneticsList);
							}
						}

						// set phone numbers
						if (Phone.CONTENT_ITEM_TYPE.equals(_mimeType)) {
							// add to contact new phone numbers list
							_newPhoneNumbers.add(_phoneNumber);
						}

						// set photo
						if (Photo.CONTENT_ITEM_TYPE.equals(_mimeType)) {
							_contact.setPhoto(_photoData);
						}

						// set dirty type
						_contact.setDirty(ContactDirtyType.NORMAL);
					}

					// close data cursor
					_dataCursor.close();
				}

				// set the contact phone numbers list
				_contact.setPhoneNumbers(_newPhoneNumbers);
			}
		}

	}

}
