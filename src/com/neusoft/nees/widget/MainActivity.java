package com.neusoft.nees.widget;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.neusoft.nees.common.Const;
import com.neusoft.nees.common.MainService;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

public class MainActivity extends Activity {

	/** Viewpager���� */
	private ViewPager viewPager;
	private ImageView imageView;
	/** ����һ�����飬�������ÿ��ҳ��Ҫ��ʾ��View */
	private ArrayList<View> pageViews;
	/** ����һ��imageview���͵����飬������ʾ����СԲ�� */
	private ImageView[] imageViews;
	/** װ��ʾͼƬ��viewgroup */
	private ViewGroup viewPictures;
	private Timer mTimer;
	private TimerTask mTask;
	int pageIndex = 0;
	boolean isTaskRun;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		LayoutInflater inflater = getLayoutInflater();
		pageViews = new ArrayList<View>();
		pageViews.add(inflater.inflate(R.layout.viewpagers00, null));
		pageViews.add(inflater.inflate(R.layout.viewpagers01, null));
		// ��ָ����XML�ļ��м�����ͼ
		viewPictures = (ViewGroup) inflater.inflate(R.layout.layout_main, null);
		viewPager=(ViewPager) viewPictures.findViewById(R.id.guidePagers);
		setContentView(viewPictures);
		viewPager.setAdapter(new NavigationPageAdapter());
		viewPager.setOnPageChangeListener(new NavigationPageChangeListener());
		startTask();
		Intent bootser = new Intent(MainActivity.this,MainService.class);
		startService(bootser);
	}

	public class NavigationPageAdapter extends PagerAdapter{
		@Override
		public int getCount() {
			return pageViews.size();
		}
		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return view==obj;
		}
		@Override
		public Object instantiateItem(View container, int position) {
			((ViewPager) container).addView(pageViews.get(position));
			return pageViews.get(position);
		}
		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(pageViews.get(position));
		}
	}
	
	public class NavigationPageChangeListener implements OnPageChangeListener{
		@Override
		public void onPageScrollStateChanged(int state) {
			if (state == 0 && !isTaskRun) {
				setCurrentItem();
				startTask();
			} else if (state == 1 && isTaskRun)
				stopTask();
		}
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}
		@Override
		public void onPageSelected(int index) {
			pageIndex = index;
		}
	}
	
	/**
	 * ������ʱ����
	 */
	private void startTask() {
		// TODO Auto-generated method stub
		isTaskRun = true;
		mTimer = new Timer();
		mTask = new TimerTask() {
			@Override
			public void run() {
				pageIndex++;
				mHandler.sendEmptyMessage(0);
			}
		};
		mTimer.schedule(mTask, 2 * 1000, 2 * 1000);// ���������Զ��л���ʱ�䣬��λ�Ǻ��룬2*1000��ʾ2��
	}

	/**
	 * ֹͣ��ʱ����
	 */
	private void stopTask() {
		// TODO Auto-generated method stub
		isTaskRun = false;
		mTimer.cancel();
	}

	// ����EmptyMessage(0)
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			 setCurrentItem();
		}
	};

	/**
	 * ����Page���л��߼�
	 */
	private void setCurrentItem() {
	   if (pageIndex == new NavigationPageAdapter().getCount()) {
			pageIndex = 0;
		}
	   viewPager.setCurrentItem(pageIndex, false);// ȡ������
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		File file=new File(Const.tempPath);
		if(file.exists()){
			File files[]=file.listFiles();
			for(File f : files){
				if(f.exists()){
					f.delete();
				}
			}
		}
	}
}
