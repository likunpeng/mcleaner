package com.caocao.cleaner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

public class MCleanerActivity extends Activity {
	private static final String TAG = "MCleanerActivity";
	private ListView lvApp;
	private LinearLayout llProgressBar;
	private AppAdapter appAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// 1. set View
		initView();
	}

	@Override
	protected void onStart() {
		super.onStart();
		ShowView sv = new ShowView();
		sv.execute();
	}

	private void initView() {
		lvApp = (ListView) this.findViewById(R.id.lv_app);
		llProgressBar = (LinearLayout) findViewById(R.id.ll_progressBar);
	}

	private class ShowView extends AsyncTask<String, Integer, String> {
		List<AppVO> list = new ArrayList<AppVO>();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			llProgressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {
			list = getAppList();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			llProgressBar.setVisibility(View.GONE);
			lvApp.setAdapter(getAppAdapter(list));
			appAdapter.notifyDataSetChanged();
		}

	}

	private AppAdapter getAppAdapter(List<AppVO> list) {
		appAdapter = new AppAdapter(this, list);
		return appAdapter;
	}

	private List<AppVO> getAppList() {
		// 1. get package name of activied app
		Set<String> activiedSet = getDataFromLog();
		// 2. get package name of installed app
		List<PackageInfo> packs = this.getPackageManager()
				.getInstalledPackages(0);
		List<AppVO> list = new ArrayList<AppVO>();
		for (PackageInfo p : packs) {
			if ((ApplicationInfo.FLAG_SYSTEM & p.applicationInfo.flags) > 0) {
				continue;
			} else {
				// 3. ignore self
				if (activiedSet.contains(p.packageName)
						&& !p.packageName
								.equals(this.getApplicationInfo().packageName)) {
					AppVO appVO = new AppVO();
					appVO.name = p.applicationInfo.loadLabel(
							this.getPackageManager()).toString();
					appVO.packageName = p.packageName;

					appVO.icon = p.applicationInfo.loadIcon(this
							.getPackageManager());
					list.add(appVO);
				}
			}
		}
		return list;
	}

	private Set<String> getDataFromLog() {
		Set<String> set = new HashSet<String>();
		try {
			ArrayList<String> commandLine = new ArrayList<String>();
			commandLine.add("logcat");
			commandLine.add("-d");
			commandLine.add("-s");
			commandLine.add("ActivityManager:d");//$NON-NLS-1$
			Process process = Runtime.getRuntime().exec(
					commandLine.toArray(new String[0]));
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));
			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				String name = getActivedPackageName(line);
				if (name != null) {
					set.add(name);
				}
				if (set.size() > 10) {
					break;
				}
			}
		} catch (IOException e) {
		}
		return set;
	}

	private String getActivedPackageName(String input) {
		Pattern pattern = Pattern.compile("cmp=(.+?)/");
		Matcher matcher = pattern.matcher(input);
		if (matcher.find())
			return matcher.group(1);
		else
			return null;
	}
}