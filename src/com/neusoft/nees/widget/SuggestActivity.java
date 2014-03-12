package com.neusoft.nees.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
/**
  * 满意度调查
  * @ClassName: SuggestActivity
  * @Description: TODO
  * @author Comsys-Administrator
  * @date 2014-3-10 上午10:22:00
  */
public class SuggestActivity extends Activity {

	private ListView listView;
	private Button btn_submit;
	private Context mContext;
	private boolean hasChoice=false;
	//private String[] datas = new String[]{"不满意","一般","满意","非常满意"};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_suggest);
		initView();
		setClickListener();
	}
	
	/**
	  * initView(这里用一句话描述这个方法的作用)
	  * @Title: initView
	  * @Description: TODO
	  * @param     设定文件
	  * @return void    返回类型
	  * @throws
	  */
	private void initView(){
		listView=(ListView) findViewById(R.id.list_01);
		btn_submit=(Button) findViewById(R.id.submit);
		mContext=this;
		listView.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_single_choice,getResources().getStringArray(R.array.datas)));
		listView.setItemsCanFocus(true);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);//设置项目只允许单选
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,long arg3) {
				hasChoice=true;
			}
		});
	}
	
	/**
	  * 监听事件
	  * setConentView(这里用一句话描述这个方法的作用)
	  * @Title: setConentView
	  * @Description: TODO
	  * @param     设定文件
	  * @return void    返回类型
	  * @throws
	 */
	private void setClickListener(){
		btn_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(hasChoice){
					Toast.makeText(mContext, "感谢参与", Toast.LENGTH_SHORT).show();
					finish();
				}else{
					Toast.makeText(mContext, "请选择一项,完成调查", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
}
