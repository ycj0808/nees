package com.neusoft.nees.widget;
import com.viewpagerindicator.TabPageIndicator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

/**
 * @ClassName: SignActivity
 * @Description: TODO 签名
 * @author yangchj
 * @date 2014-3-6 上午11:21:14
 */
public class BrowseActivity extends FragmentActivity {

	private static final String[] CONTENT = new String[] { "协议", "受理单"};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_sign);
		SignAdapter adapter = new SignAdapter(getSupportFragmentManager());
		ViewPager pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);
		TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(pager);
	}

	class SignAdapter extends FragmentPagerAdapter {

		public SignAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Log.i("TAG", "这是第"+position+"个fragment");
			Fragment fragment = null;
			switch (position) {
			case 0:
				fragment=new ProtocolFragment();
				break;
			case 1:
				fragment=new ReportFragment();
				break;
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return CONTENT.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return CONTENT[position].toString();
		}
	}
}
