package com.neusoft.nees.widget;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import com.neusoft.nees.common.Const;
import com.neusoft.nees.common.MyApp;
import com.neusoft.nees.common.Util;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

public class TakePhoneOnlyActivity extends Activity {

	private ImageView img;
	private static final int TAKE_PICTURE = 3023;
	private String newImgPath;
	private Bitmap cameraBitmap;
	private Context mContext;
	private Map datasMap;//传送数据
	private MyApp myApp;//全局应用,用于获取消息体
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_take_photo_only);
		initView();

	}

	protected void initView() {
		mContext=this;
		myApp=(MyApp) getApplication();
		datasMap=myApp.getMap();
		Log.i("TAG", datasMap.toString());
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		img = (ImageView) findViewById(R.id.img);
		checkSoftStage(Const.tempPath);
		try {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			String fileName = System.currentTimeMillis() + ".jpg";
			newImgPath = Const.tempPath + "/" + fileName;
			Uri uri = Uri.fromFile(new File(Const.tempPath, fileName));
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			startActivityForResult(intent, TAKE_PICTURE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 检测手机是否存在SD卡,网络连接是否打开
	 */
	private void checkSoftStage(String path) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) { // 判断是否存在SD卡
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}
		} else {
			new AlertDialog.Builder(this)
					.setMessage("检测到手机没有存储卡！请插入手机存储卡再开启本应用。")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							}).show();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ("".equals(newImgPath)) {
				finish();
			}else{//获取照片后的操作
				new Thread(new Runnable() {
					@Override
					public void run() {
						File file=new File(newImgPath);
						if(file.exists()){
							file.delete();
						}
					}
				}).start();
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == TAKE_PICTURE) {
			if (resultCode == -1) {
				// 拍照Activity保存图像数据的key是data，返回的数据类型是Bitmap对象
				Toast.makeText(mContext, newImgPath, Toast.LENGTH_SHORT).show();
				cameraBitmap = BitmapFactory.decodeByteArray(
						Util.decodeBitmap(newImgPath), 0,
						Util.decodeBitmap(newImgPath).length);
				// 在ImageView组件中显示拍摄的照片
				if (cameraBitmap != null) {
					img.setImageBitmap(cameraBitmap);
				}
			}else {
				Toast.makeText(mContext, "获取相片失败", Toast.LENGTH_SHORT).show();
				finish();
			}
		}
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_take_photo,menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
		case android.R.id.home:
			if ("".equals(newImgPath)) {
				finish();
			}else{//获取照片后的操作
				new Thread(new Runnable() {
					@Override
					public void run() {
						File file=new File(newImgPath);
						if(file.exists()){
							file.delete();
						}
					}
				}).start();
				finish();
			}
			break;
			
		case R.id.save://保存操作
			Toast.makeText(mContext, "发送"+newImgPath, Toast.LENGTH_SHORT).show();
			String str="";
			if ("".equals(newImgPath)) {
				finish();
			}else{//获取照片后的操作
				File file=new File(newImgPath);
				if(!file.exists()){
					finish();
				}else{
					try {
						str=sendPhoto(file);
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
					datasMap.put("BoardcastType","nees.takePhoto");
					Toast.makeText(TakePhoneOnlyActivity.this, message,Toast.LENGTH_SHORT).show();
				}
				
				finish();
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public String sendPhoto(File file) throws Exception{
		FileOutputStream fos = null;
		String md5Str = "";
		String strPathDetail = "";
		String type="0";
		String flag="";
		String message="";
		String retStr="";
		if(file.exists()){
			strPathDetail=file.getPath();
			try {
				md5Str=Util.getMd5Value(strPathDetail);
				flag = "ok";
				message="受理成功！";	
			} catch (IOException e) {
				flag="no";
				message = "受理失败！";
				e.printStackTrace();
			}
		}else{
			flag="no";
			message = "受理失败！";
		}
		retStr = flag+"~"+message+"~"+md5Str+"~"+strPathDetail;
		return retStr;
	}
}
