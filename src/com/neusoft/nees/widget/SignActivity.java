package com.neusoft.nees.widget;

import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import com.neusoft.nees.common.MyApp;
import com.neusoft.nees.signName.MyView;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SignActivity extends Activity {

	private ViewPager viewPager;
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
	private LinearLayout layout_paint;
	private MyView myView;
	private Context mContext;
	private Button btn_save;
	private Button btn_cancel;
	private Map datasMap;//��������
	private MyApp myApp;//ȫ��Ӧ��,���ڻ�ȡ��Ϣ��
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_sign_fragment);
		mContext=this;
		myApp=(MyApp) getApplication();
		datasMap=myApp.getMap();
		Log.i("TAG", datasMap.toString());
		
		ActionBar actionBar=getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("�ͻ�ǩ��");
		LayoutInflater inflater=getLayoutInflater();
		viewPager=(ViewPager) findViewById(R.id.adPagers);
		pageViews = new ArrayList<View>();
		pageViews.add(inflater.inflate(R.layout.viewpagers00, null));
		pageViews.add(inflater.inflate(R.layout.viewpagers01, null));
		viewPager.setAdapter(new NavigationPageAdapter());
		viewPager.setOnPageChangeListener(new NavigationPageChangeListener());
		layout_paint=(LinearLayout) findViewById(R.id.layout_ver4);
		myView=new MyView(mContext);
		myView.setBackgroundColor(Color.TRANSPARENT);
		layout_paint.removeAllViews();
		layout_paint.addView(myView);
		btn_save=(Button) findViewById(R.id.btn_save);
		btn_cancel=(Button) findViewById(R.id.btn_cancel);
		startTask();
		btn_save.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onClick(View v) {
				if(!myView.getSign()){
					Toast.makeText(mContext, "û��ǩ��,��������",Toast.LENGTH_SHORT).show();
				}else{
					String str="";
					try {
						str = myView.savePicture("sign", 0);
					} catch (Exception e) {
						e.printStackTrace();
					}
					String mess[] = str.split("~");
					String flag = mess[0];
					String message = mess[1];
					String[] md5Str = {mess[2]};
					String filePath = mess[3].substring(1, mess[3].length());			
					String[] pathStr = {filePath};
					datasMap.put("MD5Str", md5Str);
					datasMap.put("FilePath", pathStr);
					datasMap.put("Data","");
					datasMap.put("flag", "send");
					datasMap.put("BoardcastType","nees.signName");
					Toast.makeText(mContext, message,Toast.LENGTH_SHORT).show();
					Toast.makeText(mContext, "ͼƬ����ɹ�", Toast.LENGTH_LONG).show();
				}
			}
		});
		btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				layout_paint.removeAllViews();
				myView=new MyView(mContext);
				layout_paint.addView(myView);
			}
		});
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
		mTimer.schedule(mTask, 10 * 1000, 10 * 1000);// ���������Զ��л���ʱ�䣬��λ�Ǻ��룬2*1000��ʾ2��
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_sign,menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Intent intent=null;
		switch (item.getItemId()) {
		case R.id.brower:
			intent=new Intent(SignActivity.this,BrowseActivity.class);
			startActivity(intent);
			break;
		case android.R.id.home:
			finish();
			break;
		/*case R.id.take_photo:
			intent=new Intent(SignActivity.this,PhotoTakeActivity.class);
			startActivity(intent);
			break;*/
		}
		return false;
	}
}
