package com.neusoft.nees.widget;

/**
 * @author Jose Davis Nidhin
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import com.neusoft.nees.common.Const;
import com.neusoft.nees.common.MainService;
import com.neusoft.nees.common.MyApp;
import com.neusoft.nees.common.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class CamTestActivity extends Activity {
	private static final String TAG = "CamTestActivity";
	static Preview preview;
	Button buttonClick;
	static Camera camera;
	public static Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}
	static String fileName;
	Activity act;
	static Context ctx;
	private static Map datasMap;//传送数据
	private MyApp myApp;//全局应用,用于获取消息体

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ctx = this;
		act = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.main);
		
		myApp=(MyApp) getApplication();
		datasMap=myApp.getMap();
		Log.i("TAG", datasMap.toString());
		
		preview = new Preview(this, (SurfaceView)findViewById(R.id.surfaceView));
		preview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		((FrameLayout) findViewById(R.id.preview)).addView(preview);
		preview.setKeepScreenOn(true);
		
		buttonClick = (Button) findViewById(R.id.buttonClick);
		
		buttonClick.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//				preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
				camera.takePicture(shutterCallback, rawCallback, jpegCallback);
			}
		});
		
		buttonClick.setOnLongClickListener(new OnLongClickListener(){
			@Override
			public boolean onLongClick(View arg0) {
				camera.autoFocus(new AutoFocusCallback(){
					@Override
					public void onAutoFocus(boolean success, Camera camera) {
						camera.takePicture(shutterCallback, rawCallback, jpegCallback);
					}
				});
				return true;
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		//      preview.camera = Camera.open();
		camera = Camera.open();
		camera.startPreview();
		preview.setCamera(camera);
	}

	@Override
	protected void onPause() {
		if(camera != null) {
			camera.stopPreview();
			preview.setCamera(null);
			camera.release();
			camera = null;
		}
		super.onPause();
	}

	private static void resetCam() {
		camera.startPreview();
		preview.setCamera(camera);
	}

	public static ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			// Log.d(TAG, "onShutter'd");
		}
	};

	public static PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			// Log.d(TAG, "onPictureTaken - raw");
		}
	};

	public static PictureCallback jpegCallback = new PictureCallback() {
		@SuppressWarnings("unchecked")
		@SuppressLint("SdCardPath")
		public void onPictureTaken(byte[] data, Camera camera) {
			FileOutputStream outStream = null;
			String str="";
			try {
				// Write to SD Card
				File fileDir=new File(Const.tempPath);
				if(!fileDir.exists()){
					fileDir.mkdirs();
				}
				fileName = String.format(Const.tempPath+"/%d.jpg", System.currentTimeMillis());
				outStream = new FileOutputStream(fileName);
				outStream.write(data);
				outStream.close();
				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
				resetCam();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				File file=new File(fileName);
				if(file.exists()){
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
					datasMap.put("BoardcastType","nees.autoTakePhoto");
					Toast.makeText(ctx, message,Toast.LENGTH_SHORT).show();
				}
			}
			Log.d(TAG, "onPictureTaken - jpeg");
		}
	};
	
	public static String sendPhoto(File file) throws Exception{
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
