package com.caocao.cleaner.fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.caocao.cleaner.AppAdapter;
import com.caocao.cleaner.AppVO;
import com.caocao.cleaner.R;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

public class NotificationApp extends Fragment {
	private ListView lvApp;
	private LinearLayout llProgressBar;
	private AppAdapter appAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.list_app, container, false);
		lvApp = (ListView) v.findViewById(R.id.lv_app);
		llProgressBar = (LinearLayout) v.findViewById(R.id.ll_progressBar);
		return v;
	}

	@Override
	public void onStart() {
		super.onStart();
		ShowView sv = new ShowView();
		sv.execute();
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
		appAdapter = new AppAdapter(this.getActivity(), list);
		return appAdapter;
	}

	private List<AppVO> getAppList() {
		// 1. get package name of activied app
		Set<String> activiedSet = getDataFromLog();
		// 2. get package name of installed app
		List<PackageInfo> packs = this.getActivity().getPackageManager()
				.getInstalledPackages(0);
		List<AppVO> list = new ArrayList<AppVO>();
		for (PackageInfo p : packs) {
			if ((ApplicationInfo.FLAG_SYSTEM & p.applicationInfo.flags) > 0) {
				continue;
			} else {
				// 3. ignore self
				if (activiedSet.contains(p.packageName)
						&& !p.packageName.equals(this.getActivity()
								.getApplicationInfo().packageName)) {
					AppVO appVO = new AppVO();
					appVO.name = p.applicationInfo.loadLabel(
							this.getActivity().getPackageManager()).toString();
					appVO.packageName = p.packageName;

					appVO.icon = p.applicationInfo.loadIcon(this.getActivity()
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
			commandLine.add("-b");
			commandLine.add("events");
			commandLine.add("-d");
			commandLine.add("-s");
			commandLine.add("notification_enqueue");//$NON-NLS-1$
			Process process = Runtime.getRuntime().exec(
					commandLine.toArray(new String[0]));
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));
			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				String name = getActivedPackageName(line);
				Log.d("lkp", "name"+name);
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
		Log.d("lkp", input);
//		Pattern pattern = Pattern.compile("^\\[[[a-z]\\.]+[a-z],$"); //laobao
		Pattern pattern = Pattern.compile("\\[(.*?(?=,))");
		Matcher matcher = pattern.matcher(input);
		if (matcher.find())
			return matcher.group(1);
		else
			return null;
	}
	
	private String getPackageStr(String input){
		int indexS=input.indexOf("[");
        int indexE=input.indexOf(",");
        return input.substring(indexS+1, indexE);

	}
}
