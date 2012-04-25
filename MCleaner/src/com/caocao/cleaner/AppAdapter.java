package com.caocao.cleaner;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AppAdapter extends BaseAdapter {

//	private static final String TAG = "AppAdapter";

	private Activity activity;
	private List<AppVO> list;
	private AppItem appItem;

	public AppAdapter(Activity activity, List<AppVO> list) {
		this.activity = activity;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 1. init view
		if (convertView == null) {
			convertView = LayoutInflater.from(activity).inflate(
					R.layout.list_recent_app_item, null);
			appItem = new AppItem();
			appItem.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
			appItem.tvPackageName = (TextView) convertView
					.findViewById(R.id.tv_small);
			appItem.tvName = (TextView) convertView.findViewById(R.id.tv_large);
			appItem.bUninstall = (Button) convertView
					.findViewById(R.id.b_app_delete);
			convertView.setTag(appItem);
		} else {
			appItem = (AppItem) convertView.getTag();
		}

		// 2.show view
		AppVO appInfo = list.get(position);
		appItem.tvName.setText(appInfo.name);
		appItem.tvPackageName.setText(appInfo.packageName);
		appItem.ivIcon.setImageDrawable(appInfo.icon);
		appItem.bUninstall.setTag(appInfo.packageName);
		appItem.bUninstall.setOnClickListener(uninstallListener);
		return convertView;
	}

	private OnClickListener uninstallListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String uninstallPackageName = (String) v.getTag();
			Uri packageURI = Uri.parse("package:" + uninstallPackageName);
			Intent uninstallIntent = new Intent(Intent.ACTION_DELETE,
					packageURI);
			activity.startActivity(uninstallIntent);
		}
	};

	private class AppItem {
		ImageView ivIcon;
		TextView tvName;
		TextView tvPackageName;
		Button bUninstall;
	}
}
