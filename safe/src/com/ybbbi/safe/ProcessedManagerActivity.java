package com.ybbbi.safe;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.Toast;

import android.widget.TextView;

import com.ybbbi.safe.bean.ProcessAppInfo;
import com.ybbbi.safe.manager.ProcessManager;
import com.ybbbi.safe.service.ScreenLockService;
import com.ybbbi.safe.utils.Sharedpreferences;
import com.ybbbi.safe.utils.blacknum_shieldUtils;
import com.ybbbi.safe.utils.constants;
import com.ybbbi.safe.view.MyProgressBar;
import com.ybbbi.safe.view.SettingView;

public class ProcessedManagerActivity extends Activity {

	private List<ProcessAppInfo> list;
	private ArrayList<ProcessAppInfo> Userlist;
	private ArrayList<ProcessAppInfo> Systemlist;

	private myAdapter adapter;
	private MyProgressBar memory;
	private MyProgressBar sdcard;

	private StickyListHeadersListView listview;
	private int process;
	private int processAll;
	private ImageView iv1;
	private ImageView iv2;
	private SlidingDrawer drawer;
	private SettingView lock;
	private SettingView system;
	private boolean systemProcess =true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_processed_manager);
		init();
		initdata();
	
	}

	private void init() {
		
		
		
		
		
		drawer = (SlidingDrawer) findViewById(R.id.slidingDrawer);
		drawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {

			@Override
			public void onDrawerOpened() {
				iv1.clearAnimation();
				iv2.clearAnimation();
				iv1.setImageResource(R.drawable.drawer_arrow_down);
				iv2.setImageResource(R.drawable.drawer_arrow_down);
				Animation();
			}
		});
		drawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {

			@Override
			public void onDrawerClosed() {
				iv1.setImageResource(R.drawable.drawer_arrow_up);
				iv2.setImageResource(R.drawable.drawer_arrow_up);
				Animation();
			}
		});

		lock = (SettingView) findViewById(R.id.process_settingview_lock);
		lock();
		system = (SettingView) findViewById(R.id.process_settingview_system);
		click();
		
		
		iv1 = (ImageView) findViewById(R.id.process_iv1);
		iv2 = (ImageView) findViewById(R.id.process_iv2);
		Animation();

		memory = (MyProgressBar) findViewById(R.id.process_memory);
		sdcard = (MyProgressBar) findViewById(R.id.process_sdcard);
		listview = (StickyListHeadersListView) findViewById(R.id.processed_manager_lv);
		setdata();
		setdata2();
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ProcessAppInfo processInfo;
				if (position < Userlist.size()) {
					processInfo = Userlist.get(position);

				} else {

					processInfo = Systemlist.get(position - Userlist.size());
				}
				if (processInfo.isChecked) {
					processInfo.isChecked = false;
				} else {
					if (!processInfo.packageName.equals(getPackageName())) {

						processInfo.isChecked = true;
					}
				}
				adapter.notifyDataSetChanged();
			}
		});
	}

	private void lock() {
		lock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(ProcessedManagerActivity.this,ScreenLockService.class);
				if(blacknum_shieldUtils.isRunning(ProcessedManagerActivity.this, "com.ybbbi.safe.service.ScreenLockService")){
					stopService(intent);
				}else{
					startService(intent);
				}
				lock.setButtonstate();
			}
		});
	}
	@Override
	protected void onResume() {
		super.onResume();
		boolean b = blacknum_shieldUtils.isRunning(ProcessedManagerActivity.this, "com.ybbbi.safe.service.ScreenLockService");
		lock.isButtonOn(b);
	}

	private void click() {
		
		
		boolean boolean1 = Sharedpreferences.getBoolean(getApplicationContext(), constants.PROCESSSYSTEM, true);
		system.isButtonOn(boolean1);
		systemProcess=boolean1;
		
		system.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				system.setButtonstate();
				boolean buttonstate = system.Buttonstate();
				systemProcess=buttonstate;
				adapter.notifyDataSetChanged();
				Sharedpreferences.saveBoolean(getApplicationContext(), constants.PROCESSSYSTEM, system.Buttonstate());
			}
		});
	}

	private void Animation() {
		AlphaAnimation am1 = new AlphaAnimation(0.1f, 1.0f);
		am1.setDuration(500);
		am1.setRepeatCount(android.view.animation.Animation.INFINITE);
		am1.setRepeatMode(android.view.animation.Animation.REVERSE);
		iv1.startAnimation(am1);

		AlphaAnimation am2 = new AlphaAnimation(1.0f, 0.1f);
		am2.setDuration(500);
		am2.setRepeatCount(android.view.animation.Animation.INFINITE);
		am2.setRepeatMode(android.view.animation.Animation.REVERSE);
		iv2.startAnimation(am2);

	}

	private void initdata() {

		new Thread() {

			public void run() {

				list = ProcessManager
						.getProcessApp(ProcessedManagerActivity.this);
				Systemlist = new ArrayList<ProcessAppInfo>();
				Userlist = new ArrayList<ProcessAppInfo>();

				for (ProcessAppInfo info : list) {
					System.out.println(info);
					if (info.isSystem) {
						Systemlist.add(info);

					} else {
						Userlist.add(info);

					}

				}
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						adapter = new myAdapter();
						listview.setAdapter(adapter);

					}
				});
			};
		}.start();

	}

	private class myAdapter extends BaseAdapter implements
			StickyListHeadersAdapter {

		@Override
		public int getCount() {
			return systemProcess?Userlist.size() + Systemlist.size():Userlist.size();
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

			ProcessAppInfo processInfo;
			holder h;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.processapp_listview, null);
				h = new holder();
				h.icon = (ImageView) convertView.findViewById(R.id.image_app);

				h.size = (TextView) convertView.findViewById(R.id.size);
				h.packagename = (TextView) convertView
						.findViewById(R.id.tv_name_app);
				h.check = (CheckBox) convertView
						.findViewById(R.id.checkBox1_process);

				convertView.setTag(h);
			} else {
				h = (holder) convertView.getTag();
			}

			if (position < Userlist.size()) {
				// 通过系统list长度作比较判断是否为系统程序
				processInfo = Userlist.get(position);

			} else {

				processInfo = Systemlist.get(position - Userlist.size());
			}

			h.icon.setImageDrawable(processInfo.icon);
			h.size.setText(processInfo.size);
			h.check.setVisibility(View.GONE);
			if (processInfo.isChecked) {
				h.check.setVisibility(View.VISIBLE);

				h.check.setChecked(processInfo.isChecked);
			}
			h.packagename.setText(processInfo.name);

			return convertView;

		}

		@Override
		public View getHeaderView(int position, View convertView,
				ViewGroup parent) {
			TextView text = new TextView(getApplicationContext());
			text.setTextColor(Color.GRAY);
			text.setBackgroundResource(R.drawable.commonnum_bkg_group);
			text.setPadding(8, 8, 8, 8);
			text.setTextSize(20);
			ProcessAppInfo processInfo;
			if (position < Userlist.size()) {
				// 通过系统list长度作比较判断是否为系统程序
				processInfo = Userlist.get(position);

			} else {

				processInfo = Systemlist.get(position - Userlist.size());
			}
			text.setText(processInfo.isSystem ? "系统进程(" + Systemlist.size()
					+ ")" : "用户进程(" + Userlist.size() + ")");

			return text;
		}

		@Override
		public long getHeaderId(int position) {
			ProcessAppInfo processInfo;
			if (position < Userlist.size()) {
				// 通过系统list长度作比较判断是否为系统程序
				processInfo = Userlist.get(position);

			} else {

				processInfo = Systemlist.get(position - Userlist.size());
			}
			// 返回id,不相同就在getHeaderView方法中加载一个布局textview,相同则不加载
			return processInfo.isSystem ? 2 : 1;
		}

	}

	static class holder {
		public ImageView icon;
		public TextView packagename, size;
		public CheckBox check;
	}

	private void setdata() {
		process = ProcessManager.getProcess(this);
		processAll = ProcessManager.getAllProcess(this);
		memory.setText("进程数:");
		memory.setText_left("正在运行" + process + "个");
		memory.setText_right("总进程数" + processAll + "个");
		memory.setProgressBar((int) (process * 100f / processAll + 0.5f));

	}

	private void setdata2() {
		long m = ProcessManager.getMemory(this);
		long mAll = ProcessManager.getMemoryAll(this);
		long mUse = mAll - m;
		sdcard.setText("内存:  ");
		String use = Formatter.formatFileSize(this, mUse);
		String m2 = Formatter.formatFileSize(this, m);

		sdcard.setText_left("占用内存" + use);
		sdcard.setText_right("可用内存" + m2);
		sdcard.setProgressBar((int) (mUse * 100f / mAll + 0.5f));
	}

	public void clean(View v) {
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		List<ProcessAppInfo> Dlist = new ArrayList<ProcessAppInfo>();

		for (ProcessAppInfo info : Userlist) {
			if (info.isChecked) {
				am.killBackgroundProcesses(info.packageName);
				Dlist.add(info);
			}
		}if(systemProcess){
			
			for (ProcessAppInfo info : Systemlist) {
				if (info.isChecked) {
					am.killBackgroundProcesses(info.packageName);
					Dlist.add(info);
					
				}
			}
		}
		double Isize = 0;
		String Ssize;
		DecimalFormat df = new DecimalFormat("0.00");
		for (ProcessAppInfo processAppInfo : Dlist) {
			if (processAppInfo.isSystem) {
				Systemlist.remove(processAppInfo);
			} else {
				Userlist.remove(processAppInfo);
			}
			String[] split = processAppInfo.size.split(" ");
			Ssize = split[0];

			Isize += Double.parseDouble(Ssize);

		}

		Toast.makeText(getApplicationContext(),
				"清理" + Dlist.size() + "个进程,释放" + df.format(Isize) + "MB内存", 0)
				.show();

		setdata2();

		process = process - Dlist.size();
		memory.setText_left("正在运行" + process + "个");
		int process2 = (int) (process * 100f / processAll + 0.5f);
		memory.setProgressBar(process2);
		adapter.notifyDataSetChanged();

	}

	public void All(View v) {
		for (ProcessAppInfo info : Userlist) {
			if (!info.packageName.equals(getPackageName())) {
				info.isChecked = true;
			}
		}
		if(systemProcess){
			
			for (ProcessAppInfo info : Systemlist) {
				if (!info.packageName.equals(getPackageName())) {
					info.isChecked = true;
				}
			}
		}
		adapter.notifyDataSetChanged();
	}

	public void unAll(View v) {
		for (ProcessAppInfo info : Userlist) {
			if (!info.packageName.equals(getPackageName())) {
				info.isChecked = !info.isChecked;
			}
		}
		if(systemProcess){
			
			for (ProcessAppInfo info : Systemlist) {
				if (!info.packageName.equals(getPackageName())) {
					info.isChecked = !info.isChecked;
				}
			}
		}
		adapter.notifyDataSetChanged();
	}
}
