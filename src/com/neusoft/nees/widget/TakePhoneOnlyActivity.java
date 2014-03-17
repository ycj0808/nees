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
	private Map datasMap;//��������
	private MyApp myApp;//ȫ��Ӧ��,���ڻ�ȡ��Ϣ��
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
	 * ����ֻ��Ƿ����SD��,���������Ƿ��
	 */
	private void checkSoftStage(String path) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) { // �ж��Ƿ����SD��
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}
		} else {
			new AlertDialog.Builder(this)
					.setMessage("��⵽�ֻ�û�д洢����������ֻ��洢���ٿ�����Ӧ�á�")
					.setPositiveButton("ȷ��",
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
			}else{//��ȡ��Ƭ��Ĳ���
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
				// ����Activity����ͼ�����ݵ�key��data�����ص�����������Bitmap����
				Toast.makeText(mContext, newImgPath, Toast.LENGTH_SHORT).show();
				cameraBitmap = BitmapFactory.decodeByteArray(
						Util.decodeBitmap(newImgPath), 0,
						Util.decodeBitmap(newImgPath).length);
				// ��ImageView�������ʾ�������Ƭ
				if (cameraBitmap != null) {
					img.setImageBitmap(cameraBitmap);
				}
			}else {
				Toast.makeText(mContext, "��ȡ��Ƭʧ��", Toast.LENGTH_SHORT).show();
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
			}else{//��ȡ��Ƭ��Ĳ���
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
			
		case R.id.save://�������
			Toast.makeText(mContext, "����"+newImgPath, Toast.LENGTH_SHORT).show();
			String str="";
			if ("".equals(newImgPath)) {
				finish();
			}else{//��ȡ��Ƭ��Ĳ���
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
				message="����ɹ���";	
			} catch (IOException e) {
				flag="no";
				message = "����ʧ�ܣ�";
				e.printStackTrace();
			}
		}else{
			flag="no";
			message = "����ʧ�ܣ�";
		}
		retStr = flag+"~"+message+"~"+md5Str+"~"+strPathDetail;
		return retStr;
	}
}
