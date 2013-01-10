package com.richitec.commontoolkit.addressbook;

import com.richitec.commontoolkit.CommonToolkitApplication;
import com.richitec.commontoolkit.addressbook.AddressBookManager;

import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;

public class ContactSyncService extends Service{
	
	private static Handler updateUIHandler;

	private ContentObserver mObserver = new ContentObserver(new Handler()) {
		@Override
		public void onChange(boolean selfChange) {
			// 当联系人表发生变化时进行相应的操作
			Log.d("ContactSynService", "Contacts Modified");
			if(CommonToolkitApplication.getContext() != null){
				int updateType = AddressBookManager.getInstance().syncContact();
				if(updateUIHandler!=null){
					Message m = updateUIHandler.obtainMessage(updateType, "updateABList");
					//Log.d("AddressBook", "send message!!!");
					updateUIHandler.sendMessage(m);
				}
			}
		}
	};
	
	public static void setHandler(Handler handler){
		updateUIHandler = handler;
	}
	
	public static void removeHandler(){
		updateUIHandler = null;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// If we get killed, after returning from here, restart
		Log.d("ContactSynService", "ContactSyncService started");

		getContentResolver().registerContentObserver(
				ContactsContract.Contacts.CONTENT_URI, true, mObserver);
		
		return START_STICKY;
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		getContentResolver().unregisterContentObserver(mObserver);
	}

	

}
