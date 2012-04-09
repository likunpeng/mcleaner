package com.caocao.cleaner;

import java.util.ArrayList;
import java.util.List;

import com.caocao.cleaner.fragment.RecentApp;
import com.caocao.cleaner.fragment.UserApp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class MCleanerActivity extends FragmentActivity {
	private static final String TAG = "MCleanerActivity";
	private ViewPager mViewPager;
	private FragmentAdapter fAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		// 将要分页显示的View装入数组中
		fAdapter = new FragmentAdapter(this.getSupportFragmentManager());
		mViewPager.setPageMargin(50);
		mViewPager.setAdapter(fAdapter);

	}

	class FragmentAdapter extends FragmentPagerAdapter {
		final ArrayList<String> titles = new ArrayList<String>();
		private List<Fragment> mListFragments;
		private RecentApp ra;
		private UserApp ua;

		public FragmentAdapter(FragmentManager fm) {
			super(fm);
			mListFragments = new ArrayList<Fragment>();
			ra = new RecentApp();
			ua = new UserApp();
			mListFragments.add(ra);
			mListFragments.add(ua);
			titles.add("Recent Use");
			titles.add("All Installed");
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
}