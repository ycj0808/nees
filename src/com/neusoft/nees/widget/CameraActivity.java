package com.neusoft.nees.widget;

import java.io.File;
import com.neusoft.nees.common.Const;
import com.neusoft.nees.common.Util;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

public class CameraActivity extends Activity {

	private ImageView img;
	private static final int TAKE_PICTURE = 3023;
	private String newImgPath;
	private Bitmap cameraBitmap;
	private String fileName;
	private static final int RESULT_CAMERA_OK = 101;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_camera);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		img = (ImageView) findViewById(R.id.img);
		checkSoftStage(Const.imgPath);
		try {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			String fileName = System.currentTimeMillis() + ".jpg";
			newImgPath = Const.imgPath + "/" + fileName;
			Uri uri = Uri.fromFile(new File(Const.imgPath, fileName));
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			startActivityForResult(intent, TAKE_PICTURE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.menu_camera, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * @Override public boolean onMenuItemSelected(int featureId, MenuItem item)
	 * { if (item.getItemId() == R.id.menu_camera) { Intent intent = new
	 * Intent(MediaStore.ACTION_IMAGE_CAPTURE); fileName =
	 * System.currentTimeMillis() + ".jpg"; newImgPath = Const.imgPath + "/" +
	 * fileName; Uri uri = Uri.fromFile(new File(Const.imgPath, fileName));
	 * intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
	 * startActivityForResult(intent, TAKE_PICTURE); } switch (item.getItemId())
	 * { case R.id.menu_camera: Intent intent = new
	 * Intent(MediaStore.ACTION_IMAGE_CAPTURE); fileName =
	 * System.currentTimeMillis() + ".jpg"; newImgPath = Const.imgPath + "/" +
	 * fileName; Uri uri = Uri.fromFile(new File(Const.imgPath, fileName));
	 * intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
	 * startActivityForResult(intent, TAKE_PICTURE); break; case
	 * android.R.id.home:
	 * 
	 * break; default: break; } /* if(item.getItemId()==R.id.menu_back){
	 * if("".equals(newImgPath)){ finish(); }else{ Intent intent=new Intent();
	 * Bundle bundle = new Bundle(); bundle.putString("fileName", newImgPath);
	 * intent.putExtra("result", bundle); setResult(RESULT_CAMERA_OK, intent);
	 * finish(); } }
	 */
	// return false;
	// }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
//		case R.id.menu_camera:
//			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//			fileName = System.currentTimeMillis() + ".jpg";
//			newImgPath = Const.imgPath + "/" + fileName;
//			Uri uri = Uri.fromFile(new File(Const.imgPath, fileName));
//			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//			startActivityForResult(intent, TAKE_PICTURE);
//			break;
		case android.R.id.home:
			if ("".equals(newImgPath)) {
				finish();
			} else {
				Intent resultIntent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("fileName", newImgPath);
				resultIntent.putExtra("result", bundle);
				setResult(RESULT_CAMERA_OK, resultIntent);
				finish();
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ("".equals(newImgPath)) {
				finish();
			} else {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("fileName", newImgPath);
				intent.putExtra("result", bundle);
				setResult(RESULT_CAMERA_OK, intent);
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == TAKE_PICTURE) {
			if (resultCode == -1) {
				// ����Activity����ͼ�����ݵ�key��data�����ص�����������Bitmap����
				cameraBitmap = BitmapFactory.decodeByteArray(
						Util.decodeBitmap(newImgPath), 0,
						Util.decodeBitmap(newImgPath).length);
				// ��ImageView�������ʾ�������Ƭ
				if (cameraBitmap != null) {
					img.setImageBitmap(cameraBitmap);
				}
			} else {
				finish();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
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
