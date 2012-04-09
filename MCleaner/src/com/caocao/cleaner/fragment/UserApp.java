package com.caocao.cleaner.fragment;

import java.util.ArrayList;
import java.util.List;

import com.caocao.cleaner.AppAdapter;
import com.caocao.cleaner.AppVO;
import com.caocao.cleaner.R;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

public class UserApp extends Fragment {
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
		// 2. get package name of installed app
		List<PackageInfo> packs = this.getActivity().getPackageManager()
				.getInstalledPackages(0);
		List<AppVO> list = new ArrayList<AppVO>();
		for (PackageInfo p : packs) {
			if ((ApplicationInfo.FLAG_SYSTEM & p.applicationInfo.flags) > 0) {
				continue;
			} else {
				// 3. ignore self
				if (!p.packageName.equals(this.getActivity()
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

}
