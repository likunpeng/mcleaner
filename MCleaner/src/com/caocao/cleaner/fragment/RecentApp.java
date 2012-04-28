package com.caocao.cleaner.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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

public class RecentApp extends Fragment {
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
		PackageManager pm = context.getPackageManager();
		List<RecentTaskInfo> list = am.getRecentTasks(64, 0);
		List<AppVO> listAppVO = new ArrayList<AppVO>();
		for (RecentTaskInfo ti : list) {
			Intent intent = ti.baseIntent;
			ResolveInfo resolveInfo = pm.resolveActivity(intent, 0);
			ApplicationInfo p = resolveInfo.activityInfo.applicationInfo;
			AppVO appVO = new AppVO();
			appVO.name = p.loadLabel(this.getActivity().getPackageManager())
					.toString();
			appVO.packageName = p.packageName;
			appVO.icon = p.loadIcon(this.getActivity().getPackageManager());
			listAppVO.add(appVO);
		}
		return listAppVO;
	}
}
