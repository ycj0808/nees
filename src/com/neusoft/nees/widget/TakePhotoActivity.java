package com.neusoft.nees.widget;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.neusoft.nees.common.Const;
import com.ycj.android.ui.utils.DialogUtils;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.MultiChoiceModeListener;

public class TakePhotoActivity extends Activity {

	private GridView gridView;
	private ImageAdapter imgAdapter;
	private List<String> fileNameList = new ArrayList<String>();
	private static final int RESULT_CAMERA_OK = 101;
	private List<Map<String, Object>> checkedList = new ArrayList<Map<String, Object>>();
	private MultiCallBack multiCallBack;
	private Context mContext;
	private Dialog dialog;
	private boolean select_all=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_takephoto);
		mContext=this;
		ActionBar actionBar=getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("图片浏览");
		multiCallBack = new MultiCallBack();
		gridView = (GridView) findViewById(R.id.picture_grid);
		imgAdapter = new ImageAdapter(this);
		gridView.setAdapter(imgAdapter);
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (imgAdapter.isActionModeStarted()) {
					Map<String, Object> map = (Map<String, Object>) imgAdapter.getItem(position);
					boolean check = (Boolean) (map.get("check"));
					if (check) {
						check = false;
						map.put("check", check);
					} else {
						check = true;
						map.put("check", check);
					}
					imgAdapter.notifyDataSetChanged();
				} else {
					String fileName = fileNameList.get(position);
					Toast.makeText(TakePhotoActivity.this, fileName,
							Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(TakePhotoActivity.this,
							PhotoDetailActivity.class);
					intent.putExtra("fileName", fileName);
					startActivity(intent);
					finish();
				}
			}
		});
		gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
		gridView.setMultiChoiceModeListener(multiCallBack);
		new AsyncLoadedImage().execute();
	}

	/**
	 * 向适配器添加图片
	 * 
	 * @param bitmap
	 */
	private void addImage(Map<String, Object>... maps) {
		for (Map<String, Object> map : maps) {
			imgAdapter.addPhoto(map);
		}
	}

	/**
	 * 释放内存
	 */
	protected void onDestroy() {
		super.onDestroy();
//		final GridView grid = gridView;
//		final int count = grid.getChildCount();
//		ImageView v = null;
//		for (int i = 0; i < count; i++) {
//			v = (ImageView) grid.getChildAt(i);
//			((BitmapDrawable) v.getDrawable()).setCallback(null);
//		}
	}

	/**
	 * 异步加载图片展示
	 * 
	 * @date： 2012-8-1
	 */
	class AsyncLoadedImage extends
			AsyncTask<Object, Map<String, Object>, Boolean> {

		@SuppressWarnings("unchecked")
		@Override
		protected Boolean doInBackground(Object... params) {
			File fileDir = new File(Const.imgPath);
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}
			File[] files = fileDir.listFiles();
			boolean result = false;
			if (files != null) {
				for (File file : files) {
					String fileName = file.getName();
					if (fileName.lastIndexOf(".") > 0
							&& fileName.substring(
									fileName.lastIndexOf(".") + 1,
									fileName.length()).equals("jpg")) {
						Bitmap bitmap;
						Bitmap newBitmap;
						try {
							BitmapFactory.Options options = new BitmapFactory.Options();
							options.inSampleSize = 10;
							bitmap = BitmapFactory.decodeFile(file.getPath(),
									options);
							newBitmap = ThumbnailUtils.extractThumbnail(bitmap,
									100, 100);
							bitmap.recycle();
							if (newBitmap != null) {
								fileNameList.add(file.getPath());
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("bitmap", newBitmap);
								map.put("filePath", file.getPath());
								map.put("check", false);
								publishProgress(map);
								result = true;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			return result;
		}

		@Override
		public void onProgressUpdate(Map<String, Object>... map) {
			addImage(map);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (!result) {
				showDialog(1);
			}
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog dialog = new AlertDialog.Builder(TakePhotoActivity.this)
				.setTitle("温馨提示")
				.setMessage("暂时还没有照片,请先采集照片！")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						startActivity(new Intent(TakePhotoActivity.this,
								CameraActivity.class));
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}).show();
		return dialog;
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		imgAdapter.notifyDataSetChanged();
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
			Intent intent = new Intent(TakePhotoActivity.this,CameraActivity.class);
			startActivityForResult(intent, RESULT_CAMERA_OK);
			break;

		case android.R.id.home:
			finish();
			break;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT_CAMERA_OK) {
			Bundle bundle = data.getBundleExtra("result");
			String fileName = bundle.getString("fileName");
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 10;
			Bitmap bitmap;
			Bitmap newBitmap;
			bitmap = BitmapFactory.decodeFile(fileName, options);
			newBitmap = ThumbnailUtils.extractThumbnail(bitmap, 100, 100);
			bitmap.recycle();
			if (newBitmap != null) {
				fileNameList.add(fileName);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("bitmap", newBitmap);
				map.put("filePath", fileName);
				map.put("check", false);
				addImage(map);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onActionModeFinished(ActionMode mode) {
		imgAdapter.setActionModeStarted(false);
		imgAdapter.notifyDataSetChanged();
		gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
		gridView.setMultiChoiceModeListener(multiCallBack);
		super.onActionModeFinished(mode);
	}

	@Override
	public void onActionModeStarted(ActionMode mode) {
		imgAdapter.setActionModeStarted(true);
		imgAdapter.notifyDataSetChanged();
		super.onActionModeStarted(mode);
	}

	private class MultiCallBack implements MultiChoiceModeListener {
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.menu_delete:
				if (imgAdapter != null) {
					imgAdapter.getList().removeAll(checkedList);
					imgAdapter.notifyDataSetChanged();
					new delImage().execute();
				}
				mode.finish();
				return true;
			case R.id.menu_select_all:
				if(imgAdapter.getList()!=null){
					checkedList.clear();
					select_all=!select_all;
					for(int i=0;i<imgAdapter.getList().size();i++){
						Map<String, Object> map = (Map<String, Object>) imgAdapter.getItem(i);
						map.put("check", select_all);
						checkedList.add(map);
					}
					imgAdapter.notifyDataSetChanged();
				}
				return true;
			}
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.context_menu_individual, menu);
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {

		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			select_all=false;
			return false;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void onItemCheckedStateChanged(ActionMode actionmode,
				int position, long id, boolean checked) {
			Map<String, Object> map = (Map<String, Object>) imgAdapter
					.getItem(position);
			if (checked) {
				map.put("check", true);
				checkedList.add(map);
			} else {
				map.put("check", false);
				checkedList.remove(map);
			}
			if (imgAdapter != null) {
				imgAdapter.notifyDataSetChanged();
			}
		}
	}

	private void delImage(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}
	}

	class delImage extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			showDialog("文件删除中,请稍候...");
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			for (int i = 0; i < checkedList.size(); i++) {
				delImage(checkedList.get(i).get("filePath").toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			checkedList.clear();
			closeDialog();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			closeDialog();
		}

	}

	public void showDialog(String msg) {
		View view = View.inflate(mContext, R.layout.layout_progress, null);
		TextView dialog_content = (TextView) view.findViewById(R.id.message);
		dialog_content.setText(msg);
		dialog = DialogUtils.showProgressBar(mContext, view);
	}

	public void closeDialog() {
		if (dialog != null & dialog.isShowing()) {
			dialog.dismiss();
		}
	}

}
