package com.neusoft.nees.widget;

import com.neusoft.nees.common.Util;
import com.neusoft.nees.signName.MyView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

public class LookProtocalActivity extends Activity {

	private Button btn;
	private PopupWindow popWindow;
	private LinearLayout layout;
	private Context mContext;
	private MyView myView;
	private Button btn_save;
	private Button btn_cancel;
	private ImageView img;
	private Bitmap cameraBitmap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_look_protocal);
		 mContext=this;
		btn=(Button) findViewById(R.id.button1);
		img=(ImageView) findViewById(R.id.img);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LayoutInflater layoutInflater = (LayoutInflater)(LookProtocalActivity.this).getSystemService(LAYOUT_INFLATER_SERVICE); 
				 // ��ȡ�Զ��岼���ļ�poplayout.xml����ͼ  
	            View popview = layoutInflater.inflate(R.layout.poplayout, null);  
	            popWindow = new PopupWindow(popview,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);  
	            layout=(LinearLayout) popview.findViewById(R.id.layout_draw);
	            myView=new MyView(mContext);
	            layout.removeAllViews();
	            layout.addView(myView);
	            //�涨������λ��  
	            popWindow.showAtLocation(findViewById(R.id.main), Gravity.CENTER,  0, 0);  
	            btn_save=(Button) popview.findViewById(R.id.btn_save);
	            btn_cancel=(Button) popview.findViewById(R.id.btn_cancel);
	            btn_save.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
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
//							datasMap.put("MD5Str", md5Str);
//							datasMap.put("FilePath", pathStr);
//							datasMap.put("Data","");
//							datasMap.put("flag", "send");
//							datasMap.put("BoardcastType","nees.signName");
//							Toast.makeText(mContext, message,Toast.LENGTH_SHORT).show();
							Toast.makeText(mContext, "ͼƬ����ɹ�", Toast.LENGTH_LONG).show();
							cameraBitmap = BitmapFactory.decodeByteArray(
									Util.decodeBitmap(filePath), 0,
									Util.decodeBitmap(filePath).length);
							// ��ImageView�������ʾ�������Ƭ
							if (cameraBitmap != null) {
								img.setImageBitmap(cameraBitmap);
							}
							popWindow.dismiss();
						}
					}
				});
	            
	            btn_cancel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						layout.removeAllViews();
						myView=new MyView(mContext);
						layout.addView(myView);
					}
				});
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(popWindow.isShowing()){
				popWindow.dismiss();
			}else{
				finish();
			}
		}
		return false;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (cameraBitmap != null) {
			if (cameraBitmap.isRecycled()) {
				cameraBitmap.recycle();
			}
		}
	}
}
