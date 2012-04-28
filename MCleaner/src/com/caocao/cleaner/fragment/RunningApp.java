package com.caocao.cleaner.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.caocao.cleaner.AppAdapter;
import com.caocao.cleaner.AppVO;
import com.caocao.cleaner.R;

public class RunningApp extends Fragment {
	private ListView lvApp;
	private LinearLayout llProgressBar;
	private AppAdapter appAdapter;
	private Context context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.list_app, container, false);
		lvApp = (ListView) v.findViewById(R.id.lv_app);
		llProgressBar = (LinearLayout) v.findViewById(R.id.ll_progressBar);
		this.context = getActivity().getApplicationContext();
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
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
		PackageManager pm = context.getPackageManager();
		List<AppVO> listAppVO = new ArrayList<AppVO>();
		for (RunningAppProcessInfo ti : list) {
			if (pm.getLaunchIntentForPackage(ti.processName) != null) {
				try {
					ApplicationInfo ai = pm.getApplicationInfo(ti.processName,
							PackageManager.GET_META_DATA);
					if (ai != null) {
						AppVO appVO = new AppVO();
						appVO.name = ai.loadLabel(
								this.getActivity().getPackageManager())
								.toString();
						appVO.packageName = ai.packageName;
						appVO.icon = ai.loadIcon(this.getActivity()
								.getPackageManager());
						listAppVO.add(appVO);
					}
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		return listAppVO;
	}
}