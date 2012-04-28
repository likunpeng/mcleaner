package com.caocao.cleaner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.caocao.cleaner.fragment.NotificationApp;
import com.caocao.cleaner.fragment.RecentApp;
import com.caocao.cleaner.fragment.RunningApp;
import com.caocao.cleaner.fragment.UserApp;

public class MCleanerActivity extends FragmentActivity {
	private static final String TAG = "MCleanerActivity";
	private static final int GROUP_ID = 0;
	private static final int ABOUT = 1;
	private static final int VIP = 2;
	private ViewPager mViewPager;
	private FragmentAdapter fAdapter;
	private Activity activity;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		this.activity = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		// 将要分页显示的View装入数组中
		fAdapter = new FragmentAdapter(this.getSupportFragmentManager());
		mViewPager.setAdapter(fAdapter);
		mViewPager.setPageMargin(10);
		mViewPager.setPageMarginDrawable(R.drawable.title);
		  ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);  

	}

	class FragmentAdapter extends FragmentPagerAdapter {
		final ArrayList<String> titles = new ArrayList<String>();
		private List<Fragment> mListFragments;
		private RecentApp ra;
		private UserApp ua;
		private NotificationApp na;
		private RunningApp runapp;
		
		public FragmentAdapter(FragmentManager fm) {
			super(fm);
			mListFragments = new ArrayList<Fragment>();
			ra = new RecentApp();
			ua = new UserApp();
			na = new NotificationApp();
			runapp = new RunningApp();
			mListFragments.add(ra);
			mListFragments.add(ua);
			mListFragments.add(na);
			mListFragments.add(runapp);
			titles.add(getString(R.string.recent_use));
			titles.add(getString(R.string.installed_app));
			titles.add("通知栏应用");
			titles.add("正在运行");
		}

		@Override
		public Fragment getItem(int position) {
			return mListFragments.get(position);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return titles.get(position);
		}

		@Override
		public int getCount() {
			return mListFragments.size();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(GROUP_ID, VIP, 0, getString(R.string.vip));
		menu.add(GROUP_ID, ABOUT, 0, getString(R.string.about));
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case ABOUT:
			Toast.makeText(this, "Wait for later...", Toast.LENGTH_SHORT)
					.show();
			break;
		case VIP:
			showVIP();
			break;
		}
		return true;
	}

	private void showVIP() {
		View contentView = LayoutInflater.from(getApplicationContext())
				.inflate(R.layout.pop_vip, null);
		final PopupWindow popupWindow = new PopupWindow(contentView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
		popupWindow
				.showAtLocation(this.getCurrentFocus(), Gravity.CENTER, 0, 0);
		// Init date
		final DatePicker dp = (DatePicker) contentView
				.findViewById(R.id.dp_birthday);
		dp.init(1980, 0, 1, null);
		// Init button
		Button bOK = (Button) contentView.findViewById(R.id.b_ok);
		bOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isVIP(dp)) {
					Intent intent = new Intent(activity, VIPActivity.class);
					activity.startActivity(intent);
				} else {
					Toast.makeText(activity, "Sorry, you are NOT our VIP!",
							Toast.LENGTH_SHORT).show();
				}
				popupWindow.dismiss();
			}
		});
	}

	private boolean isVIP(DatePicker dp) {
		String[] birthdates = activity.getResources().getStringArray(
				R.array.vip_birthdate);
		List<String> birthdateList = Arrays.asList(birthdates);
		String date = dp.getYear() + "/" + (dp.getMonth() + 1) + "/"
				+ dp.getDayOfMonth();
		Log.w("Date", date);
		return birthdateList.contains(date);
	}
}