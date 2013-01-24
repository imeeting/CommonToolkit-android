package com.richitec.commontoolkit.activityextension;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.R;
import com.richitec.commontoolkit.utils.DataStorageUtils;
import com.richitec.commontoolkit.utils.VersionUtils;
import com.richitec.commontoolkit.utils.VersionUtils.VersionCompareException;

public abstract class AppLaunchActivity extends Activity {

	private static final String LOG_TAG = AppLaunchActivity.class
			.getCanonicalName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d("commontoolkit", "AppLaunchActivity onCreate");
		
		// set content view
		setContentView(R.layout.app_launch_activity_layout);

		// set loading splash image
		((ImageView) findViewById(R.id.appSplash_imageView))
				.setImageDrawable(splashImg());

		// new application launching instruction and task to do
		// didFinishLaunching in background
		new AppLaunchingTask().execute(instructionContentImgIds(),
				intentActivity());
	}

	
	@Override
	protected void onStop() {
		Log.d("commontoolkit", "AppLaunchActivity - onStop");
		super.onStop();
	}
	
	@Override
	protected void onResume() {
		Log.d("commontoolkit", "AppLaunchActivity - onResume");
		super.onResume();
	}
		
	@Override
    protected void onPause() {
		Log.d("commontoolkit", "AppLaunchActivity - onPause");
		super.onPause();
    }
	
	@Override
	protected void onRestart() {
		Log.d("commontoolkit", "AppLaunchActivity - onRestart");
		super.onRestart();
	}

	@Override
	protected void onStart() {
		Log.d("commontoolkit", "AppLaunchActivity - onStart");
		super.onStart();
	}
	
	// application loading splash image
	public abstract Drawable splashImg();

	// application instruction content image resource and layout ids
	// the last one is instruction image and go to target intent layout id,
	// others are instruction image resource id
	protected List<Integer> instructionContentImgIds() {
		// default return empty list
		return new ArrayList<Integer>();
	}

	// application intent activity
	public abstract Intent intentActivity();

	// application did finish launching
	public abstract void didFinishLaunching();

	public void doPostExecute() {};
	// inner class
	// application launching task
	class AppLaunchingTask extends AsyncTask<Object, Integer, Integer> {

		// instruction image resource and layout id list
		List<Integer> _mInstructionImgRes7LayoutIds;

		// target intent
		Intent _mIntent;

		@Override
		protected Integer doInBackground(Object... params) {
			// init return result
			Integer _ret = -1;

			// save instruction image list and target intent
			if (2 == params.length) {
				// workaround by ares
				@SuppressWarnings("unchecked")
				List<Integer> _instructionImgResIds = (List<Integer>) params[0];

				_mInstructionImgRes7LayoutIds = _instructionImgResIds;
				_mIntent = (Intent) params[params.length - 1];

				// did finish launch
				didFinishLaunching();

				// reset return result
				_ret = 0;
			} else {
				Log.e(LOG_TAG,
						"Open application launching task failed, param = "
								+ params.toString());
			}

			return _ret;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);

			// check result
			if (0 == result) {
				doPostExecute();
				// check intent activity
				if (null != _mIntent) {
					// define application instruction launch flag
					boolean _need2LaunchAppInstruction = true;

					// compare last storage and current application version name
					try {
						if (VersionUtils
								.compareVersionName(
										DataStorageUtils
												.getString(AppInstructionActivity.NEED2LAUNCH_APPINSTRUCTION),
										VersionUtils.versionName()) >= 0) {
							// go to target intent immediately
							_need2LaunchAppInstruction = false;
						}
					} catch (VersionCompareException e) {
						Log.e(LOG_TAG,
								"Compare application version name error, exception message = "
										+ e.getMessage());

						e.printStackTrace();
					}

					// need to launch application instruction
					if (true == _need2LaunchAppInstruction) {
						// check application instruction content image list
						if (null != _mInstructionImgRes7LayoutIds
								&& !_mInstructionImgRes7LayoutIds.isEmpty()) {
							// get application instruction active image parent
							// view
							try {
								View _appInstructionActiveImgParentView = ((LayoutInflater) CTApplication
										.getContext()
										.getSystemService(
												Context.LAYOUT_INFLATER_SERVICE))
										.inflate(
												_mInstructionImgRes7LayoutIds
														.get(_mInstructionImgRes7LayoutIds
																.size() - 1),
												null);

								// check application instruction active image
								// parent view
								if (null == _appInstructionActiveImgParentView) {
									throw new Exception(
											"Application instruction active image parent view is null");
								}

								// define application instruction intent
								Intent _appInstructionIntent = new Intent(
										AppLaunchActivity.this,
										AppInstructionActivity.class);

								// put instruction content image list and intent
								// activity
								_appInstructionIntent
										.putIntegerArrayListExtra(
												AppInstructionActivity.APPINSTRUCTION_ACTIVITY_IMGRESIDS_PARAM_KEY,
												(ArrayList<Integer>) _mInstructionImgRes7LayoutIds);
								_appInstructionIntent
										.putExtra(
												AppInstructionActivity.APPINSTRUCTION_ACTIVITY_TARGETINTENT_PARAM_KEY,
												_mIntent);

								// go to application instruction activity
								startActivity(_appInstructionIntent);
							} catch (Exception e) {
								Log.e(LOG_TAG,
										"Instruction active image not exist in image list and exception message = "
												+ e.getMessage());

								e.printStackTrace();
							}
						} else {
							Log.w(LOG_TAG,
									"Launch application instruction error, instruction image = "
											+ _mInstructionImgRes7LayoutIds);

							// save current application version
							DataStorageUtils
									.putObject(
											AppInstructionActivity.NEED2LAUNCH_APPINSTRUCTION,
											VersionUtils.versionName());

							// go to intent activity
							startActivity(_mIntent);
						}
					} else {
						// go to intent activity
						startActivity(_mIntent);
					}
				} else {
					Log.e(LOG_TAG, "Intent activity is null");
				}

				// finish application launch activity
				finish();
			}
		}

	}

}
