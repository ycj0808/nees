package com.neusoft.nees.widget;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;
import it.sephiroth.android.library.imagezoom.utils.DecodeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class PhotoTakeActivity extends Activity implements
		OnItemSelectedListener {

	private Context mContext;
	/* 用来标识请求照相功能的activity */
	private static final int CAMERA_WITH_DATA = 3023;
	/* 拍照的照片存储位置 */
	private static final File PHOTO_DIR = new File(
			Environment.getExternalStorageDirectory() + "/nees");
	private File photoFile;
	private ImageViewTouch img_photo;
	private ImageView imgView;
	private Matrix imageMatrix;
	// private ListView listView;
	private List<Bitmap> list = new ArrayList<Bitmap>();
	private ImageAdapter adpter;
	@SuppressWarnings("deprecation")
	Gallery gallery;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_photo_take);
		mContext = this;
		img_photo = (ImageViewTouch) findViewById(R.id.img_photo);
		gallery = (Gallery) findViewById(R.id.gallery);
		adpter = new ImageAdapter(mContext, list, img_photo);
		gallery.setAdapter(adpter);

		// listView=(ListView) findViewById(R.id.listView_photo);
		// listView.setAdapter(new PhotoAdapter(list));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_photo, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.take_photo:
			doTakePhoto();
			// Intent intent=new
			// Intent(PhotoTakeActivity.this,BrowseActivity.class);
			// startActivity(intent);
			Toast.makeText(mContext, "准备拍照", Toast.LENGTH_LONG).show();
			break;
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case CAMERA_WITH_DATA:
			Log.i("TAG", "图片路径:" + photoFile.getPath());
			Uri imageUri = Uri.parse(photoFile.getPath());
			final int size = -1; // use the original image size
			Bitmap bitmap = DecodeUtils.decode(this, imageUri, size, size);
			list.add(bitmap);
			// listView.setAdapter(new PhotoAdapter(list));
			// adpter=new ImageAdapter(mContext, list);
			// gallery.setAdapter(adpter);
			adpter.notifyDataSetChanged();
			if (null != bitmap) {
				if (null == imageMatrix) {
					imageMatrix = new Matrix();
				}
				img_photo.setImageBitmap(bitmap,
						imageMatrix.isIdentity() ? null : imageMatrix,
						ImageViewTouchBase.ZOOM_INVALID,
						ImageViewTouchBase.ZOOM_INVALID);
			}
			break;
		}
	}

	private void doTakePhoto() {
		if (!PHOTO_DIR.exists()) {
			PHOTO_DIR.mkdirs();
		}
		photoFile = new File(PHOTO_DIR, System.currentTimeMillis() + ".jpg");
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
		startActivityForResult(intent, CAMERA_WITH_DATA);
	}

	class PhotoAdapter extends BaseAdapter {
		private List<Bitmap> list;

		public PhotoAdapter(List<Bitmap> list_photo) {
			list = list_photo;
		}

		@Override
		public int getCount() {
			if (list != null) {
				return list.size();
			} else {
				return 0;
			}
		}

		@Override
		public Object getItem(int position) {
			if (list != null) {
				return list.get(position);
			} else {
				return null;
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("NewApi")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imgView = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.item_list_photo, null);
				imgView = (ImageView) convertView.findViewById(R.id.img);
				convertView.setTag(imgView);
			} else {
				imgView = (ImageView) convertView.getTag();
			}
			if (list != null) {
				// imgView.setImageBitmap(list.get(position));
				imgView.setBackground(new BitmapDrawable(null, list
						.get(position)));
			}
			return convertView;
		}
	}

	class ImageAdapter extends BaseAdapter {

		private Context context;
		private int galleryBackground;
		private List<Bitmap> list;
		private int selectItem;
		private ImageViewTouch img;

		public ImageAdapter(Context context, List<Bitmap> list,
				ImageViewTouch img) {
			this.context = context;
			this.list = list;
			this.img = img;
			TypedArray array = obtainStyledAttributes(R.styleable.Gallery1);
			galleryBackground = array.getResourceId(
					R.styleable.Gallery1_android_galleryItemBackground, 0);
			array.recycle();
		}

		@Override
		public int getCount() {
			if (list != null) {
				return list.size();
			} else {
				return 0;
			}
		}

		@Override
		public Object getItem(int position) {
			if (list != null) {
				return list.get(position);
			} else {
				return null;
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		private void setSelectItem(int selectItem) {
			if (this.selectItem != selectItem) {
				this.selectItem = selectItem;
				notifyDataSetChanged();
			}
		}

		@SuppressWarnings("deprecation")
		@Override
		public View getView(int position, View view, ViewGroup arg2) {
			imgView = new ImageView(context);
			if (list != null && list.size() > 0) {
				// 设置图像资源
				imgView.setImageDrawable(new BitmapDrawable(null, list
						.get(position)));
				imgView.setLayoutParams(new Gallery.LayoutParams(120, 120));
				imgView.setScaleType(ImageView.ScaleType.FIT_XY);
				imgView.setBackgroundResource(galleryBackground);
				if (selectItem != position) {
					img.setImageBitmap(list.get(position),
							imageMatrix.isIdentity() ? null : imageMatrix,
							ImageViewTouchBase.ZOOM_INVALID,
							ImageViewTouchBase.ZOOM_INVALID);
				}
			}
			return imgView;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		adpter.setSelectItem(position);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

}
