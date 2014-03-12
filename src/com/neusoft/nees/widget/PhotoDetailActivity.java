package com.neusoft.nees.widget;

import com.neusoft.nees.common.Util;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class PhotoDetailActivity extends Activity {

	private Button btn_back;
	private ImageView imgView;
	private String fileName = "";
	private Bitmap bitmap;
	private Context mContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_photo_detail);
		mContext=this;
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle("图片详情");
		// actionBar.setDisplayShowHomeEnabled(false);
		Intent intent = getIntent();
		if (intent != null) {
			fileName = intent.getStringExtra("fileName");
		}
		btn_back = (Button) findViewById(R.id.btn_back);
		imgView = (ImageView) findViewById(R.id.img_detail);
		btn_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PhotoDetailActivity.this,
						TakePhotoActivity.class);
				startActivity(intent);
				finish();
			}
		});
		if (!"".equals(fileName)) {
			bitmap = BitmapFactory.decodeByteArray(Util.decodeBitmap(fileName),
					0, Util.decodeBitmap(fileName).length);
			imgView.setImageBitmap(bitmap);
		} else {
			AlertDialog dialog = new AlertDialog.Builder(
					PhotoDetailActivity.this)
					.setTitle("温馨提示")
					.setMessage("没有要展现的图片照片！")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent(
											PhotoDetailActivity.this,
											TakePhotoActivity.class);
									startActivity(intent);
									finish();
								}
							}).show();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			Intent intent=new Intent(PhotoDetailActivity.this,TakePhotoActivity.class);
			startActivity(intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_select, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent=new Intent(PhotoDetailActivity.this,TakePhotoActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.menu_select:
			Toast.makeText(mContext, "您选择了"+fileName, Toast.LENGTH_SHORT).show();
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (bitmap.isRecycled()) {
			bitmap.recycle();
		}
	}
}
