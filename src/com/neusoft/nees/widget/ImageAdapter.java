package com.neusoft.nees.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	private List<Map<String, Object>> picList = new ArrayList<Map<String, Object>>();
	private Context mContext;
	private boolean actionModeStarted = false;

	public boolean isActionModeStarted() {
		return actionModeStarted;
	}

	public void setActionModeStarted(boolean actionModeStarted) {
		this.actionModeStarted = actionModeStarted;
	}

	public ImageAdapter(Context mContext) {
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		return picList.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return picList.get(position);
	}

	/**
	 * 添加图片
	 * 
	 * @param bitmap
	 */
	public void addPhoto(Map<String, Object> map) {
		picList.add(map);
		notifyDataSetChanged();
	}

	/**
	 * 返回当前的数据 getList(这里用一句话描述这个方法的作用)
	 * 
	 * @Title: getList
	 * @Description: TODO
	 * @param @return 设定文件
	 * @return List<Bitmap> 返回类型
	 * @throws
	 */
	public List<Map<String, Object>> getList() {
		return picList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.grid_item, null);
			viewHolder = new ViewHolder();
			viewHolder.img = (ImageView) convertView.findViewById(R.id.img);
			viewHolder.img_check = (ImageView) convertView
					.findViewById(R.id.img_check);
			convertView.setTag(viewHolder);
			// imageView = new ImageView(mContext);
			// imageView.setLayoutParams(new GridView.LayoutParams(110, 110));
			// imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			// imageView.setPadding(5,5,5,5);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.img.setImageBitmap((Bitmap) picList.get(position).get(
				"bitmap"));
		viewHolder.img_check.setBackgroundResource(R.drawable.select_before);
		boolean check = (Boolean) picList.get(position).get("check");
		if (actionModeStarted) {
			viewHolder.img_check.setVisibility(View.VISIBLE);
			if (check) {
				viewHolder.img_check
						.setBackgroundResource(R.drawable.select_after);
			} else {
				viewHolder.img_check
						.setBackgroundResource(R.drawable.select_before);
			}
		} else {
			viewHolder.img_check.setVisibility(View.GONE);
			picList.get(position).put("check", false);
			viewHolder.img_check.setBackgroundResource(R.drawable.select_before);
		}
		return convertView;
	}

	class ViewHolder {
		ImageView img;
		ImageView img_check;
	}
}
