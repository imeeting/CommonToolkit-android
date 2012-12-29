package com.richitec.commontoolkit.utils;

import java.util.List;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.provider.Settings;
import android.util.Log;

import com.richitec.commontoolkit.activityextension.AppLaunchActivity;

public class ToneGeneratorUtils {

	private static final String LOG_TAG = "ToneGeneratorUtils";

	// sound play duration and volume
	private final Integer SOUND_DURATION_MS = 120;

	private final Integer SOUND_VOLUME = 80;

	// dtmf sound array
	@SuppressWarnings("unchecked")
	private static final List<Integer> DTMF_SOUND_LIST = (List<Integer>) CommonUtils
			.array2List(new Integer[] { ToneGenerator.TONE_DTMF_1,
					ToneGenerator.TONE_DTMF_2, ToneGenerator.TONE_DTMF_3,
					ToneGenerator.TONE_DTMF_4, ToneGenerator.TONE_DTMF_5,
					ToneGenerator.TONE_DTMF_6, ToneGenerator.TONE_DTMF_7,
					ToneGenerator.TONE_DTMF_8, ToneGenerator.TONE_DTMF_9,
					ToneGenerator.TONE_DTMF_S, ToneGenerator.TONE_DTMF_0,
					ToneGenerator.TONE_DTMF_P });

	// singleton instance
	private static volatile ToneGeneratorUtils _singletonInstance;

	// media sound tone generator
	private ToneGenerator _mToneGenerator;

	// tone generator media sound list
	private List<Integer> _mTGMediaSoundList;

	// audio manager
	private AudioManager _mAudioManager;

	// private constructor
	private ToneGeneratorUtils() {
		// init audio manager
		_mAudioManager = (AudioManager) AppLaunchActivity.getAppContext()
				.getSystemService(Context.AUDIO_SERVICE);
	}

	// get ToneGeneratorUtils singleton instance
	public static ToneGeneratorUtils getInstance() {
		if (null == _singletonInstance) {
			synchronized (ToneGeneratorUtils.class) {
				if (null == _singletonInstance) {
					_singletonInstance = new ToneGeneratorUtils();
				}
			}
		}

		return _singletonInstance;
	}

	public void setTGMediaSoundList(List<Integer> tgMediaSoundList) {
		_mTGMediaSoundList = tgMediaSoundList;
	}

	// play sound with tone id and sound list
	public void playSound(Integer toneId, List<Integer> tgMediaSoundList) {
		// check tone generator media sound list
		if (null != tgMediaSoundList) {
			// update tone generator media sound list
			_mTGMediaSoundList = tgMediaSoundList;
		}

		// get and check ringer mode
		Integer _ringerMode = _mAudioManager.getRingerMode();

		if (AudioManager.RINGER_MODE_SILENT == _ringerMode
				|| AudioManager.RINGER_MODE_VIBRATE == _ringerMode) {
			// android system ringer mode is silent or vibrate
			Log.i(LOG_TAG, "Android system ringer mode is silent or vibrate");

			return;
		}

		// check media sound list and tone id
		if (null == _mTGMediaSoundList || null == toneId || toneId < 0
				|| toneId > _mTGMediaSoundList.size() - 1) {
			Log.e(LOG_TAG, "Play media sound error, media sound tone id = "
					+ toneId + " and sound list = " + _mTGMediaSoundList);
		} else {
			if (null == _mToneGenerator) {
				try {
					// init media sound tone generator
					_mToneGenerator = new ToneGenerator(
							AudioManager.STREAM_MUSIC, SOUND_VOLUME);
				} catch (Exception e) {
					_mToneGenerator = null;

					e.printStackTrace();

					Log.e(LOG_TAG,
							"Creating local media sound tone generator error, exception message = "
									+ e.getMessage());
				}
			}

			// check media sound tone generator
			if (null != _mToneGenerator) {
				synchronized (_mToneGenerator) {
					// play media sound
					_mToneGenerator.startTone(_mTGMediaSoundList.get(toneId),
							SOUND_DURATION_MS);
				}
			}
		}
	}

	// play sound with tone id
	public void playSound(Integer toneId) {
		playSound(toneId, null);
	}

	// play dtmf sound with tone id
	public void playDTMFSound(Integer toneId) {
		// get and check system dtmf sound enable or disable when dial
		if (1 == Settings.System.getInt(AppLaunchActivity.getAppContext()
				.getContentResolver(), Settings.System.DTMF_TONE_WHEN_DIALING,
				1)) {
			// play dtmf sound with tone id
			playSound(toneId, DTMF_SOUND_LIST);
		} else {
			// android system dtmf sound disable when dial
			Log.w(LOG_TAG, "Android system dtmf sound disable when dial");

			return;
		}
	}

}
