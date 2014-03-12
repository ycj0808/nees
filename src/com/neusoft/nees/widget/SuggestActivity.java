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
  * ����ȵ���
  * @ClassName: SuggestActivity
  * @Description: TODO
  * @author Comsys-Administrator
  * @date 2014-3-10 ����10:22:00
  */
public class SuggestActivity extends Activity {

	private ListView listView;
	private Button btn_submit;
	private Context mContext;
	private boolean hasChoice=false;
	//private String[] datas = new String[]{"������","һ��","����","�ǳ�����"};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_suggest);
		initView();
		setClickListener();
	}
	
	/**
	  * initView(������һ�仰�����������������)
	  * @Title: initView
	  * @Description: TODO
	  * @param     �趨�ļ�
	  * @return void    ��������
	  * @throws
	  */
	private void initView(){
		listView=(ListView) findViewById(R.id.list_01);
		btn_submit=(Button) findViewById(R.id.submit);
		mContext=this;
		listView.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_single_choice,getResources().getStringArray(R.array.datas)));
		listView.setItemsCanFocus(true);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);//������Ŀֻ����ѡ
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,long arg3) {
				hasChoice=true;
			}
		});
	}
	
	/**
	  * �����¼�
	  * setConentView(������һ�仰�����������������)
	  * @Title: setConentView
	  * @Description: TODO
	  * @param     �趨�ļ�
	  * @return void    ��������
	  * @throws
	 */
	private void setClickListener(){
		btn_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(hasChoice){
					Toast.makeText(mContext, "��л����", Toast.LENGTH_SHORT).show();
					finish();
				}else{
					Toast.makeText(mContext, "��ѡ��һ��,��ɵ���", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
}
