package com.caocao.cleaner;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

public class VIPActivity extends Activity {
	private MediaPlayer mp;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.detail_vip);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// 1. init picture
		ImageView ivBirthday = (ImageView) this.findViewById(R.id.iv_birthday);
		ivBirthday.setBackgroundResource(R.drawable.happy_birthday);

		// 2. play music
		mp = MediaPlayer.create(this, R.raw.happy_birthday);
		mp.setLooping(true);
		mp.start();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mp != null) {
			mp.stop();
			mp = null;
		}
	}
}
