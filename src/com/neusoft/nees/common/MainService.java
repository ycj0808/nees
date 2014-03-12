package com.neusoft.nees.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import com.neusoft.nees.signName.FileUtil;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MainService extends Service {

	public static final String TAG = "TAG";
	public static Boolean mainThreadFlag = true;
	public static Boolean ioThreadFlag = true;
	ServerSocket serverSocket = null;
	final int SERVER_PORT = 10000;
	private MyApp app;
	public Socket client;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private void newDirectory(String name) {
		FileUtil.newFolder(name);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		newDirectory("nees");
		new Thread() {
			public void run() {
				doListen();
			}
		}.start();
	}

	private void doListen() {
		Log.d(TAG, Thread.currentThread().getName() + "---->"
				+ " doListen() START");
		serverSocket = null;
		try {
			Log.d(TAG, Thread.currentThread().getName() + "---->"
					+ " doListen() new serverSocket");
			serverSocket = new ServerSocket(SERVER_PORT);
			boolean mainThreadFlag = true;
			while (mainThreadFlag) {
				Log.d(TAG, Thread.currentThread().getName() + "---->"
						+ " doListen() listen");
				client = serverSocket.accept();
				// 先开启一个活动 线程
				new Thread(new ThreadStartActivty(MainService.this, client))
						.start();
				// 发送消息 线程
				new Thread(new ThreadWriterSocket(client)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mainThreadFlag = false;
		ioThreadFlag = false;
	}

	// 发送消息
	public void sendBroadCast(String name) {
		sendBroadcast(new Intent(name));
	}

	// File Md5 校验
	public boolean checkFile(JSONArray MD5Str, JSONArray filePaths)
			throws Exception {

		boolean flag = true;

		for (int i = 0; i < filePaths.length(); i++) {
			String filename = filePaths.get(i).toString();
			String MD5 = MD5Str.get(i).toString();
			if (!FileUtil.getMd5Value(filename).equals(MD5)) {
				flag = false;
				break;
			}
		}
		return flag;
	}

	// 填充数据池
	public void putData(String BoardcastType, JSONArray filePaths)
			throws Exception {
		int count = filePaths.length();
		List list = new ArrayList();
		for (int i = 0; i < count; i++) {
			String filepath = filePaths.getString(i);
			list.add(FileUtil.getData(filepath));
		}
		Map map = new HashMap();
		map.put("BoardcastType", BoardcastType);
		map.put("Data", list);
		map.put("flag", "revices");
		map.put("FilePath", filePaths);
		app.setMap(map);
	}

	// 活动线程

	public class ThreadStartActivty implements Runnable {
		private Socket client;
		private Context context;
		BufferedInputStream in;
		BufferedOutputStream out;
		private Intent bootStart;

		ThreadStartActivty(Context context, Socket client) {

			this.client = client;
			this.context = context;
			try {
				out = new BufferedOutputStream(client.getOutputStream());
				in = new BufferedInputStream(client.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			Log.d(TAG, Thread.currentThread().getName() + "---->"
					+ "a client has connected to server!");
			try {

				// testSocket();// 测试socket方法
				ioThreadFlag = true;
				while (ioThreadFlag) {
					try {
						if (!client.isConnected()) {
							break;
						}

						/* 接收PC发来的数据 */
						Log.v(TAG, Thread.currentThread().getName() + "---->"
								+ "will read......");
						/* 读操作命令 */

						String jsoncurrCMD = readCMDFromSocket(in); // 传来的json
																	// 字符串
						Log.v(TAG, Thread.currentThread().getName()
								+ "jsoncurrCMD---->" + jsoncurrCMD);
						/* 根据命令处理活动 */
						if (!"".equals(jsoncurrCMD)) {

							if ("1".equals(jsoncurrCMD)) {
								sendBroadCast("nees.takePhoto");
							} else if ("2".equals(jsoncurrCMD)) {
								sendBroadCast("nees.signName");
							} else if ("3".equals(jsoncurrCMD)) {
								sendBroadCast("nees.signName");
							} else {
								app = (MyApp) getApplication();
								String BoardcastType = JsonUtil
										.getRequestJson(jsoncurrCMD)
										.get("BoardcastType").toString();// 广播
								// 接下来就是文件校对，处理文件操作
								JSONArray MD5Str = (JSONArray) JsonUtil
										.getRequestJson(jsoncurrCMD).get(
												"MD5Str");// 获得md5
								JSONArray filePaths = (JSONArray) JsonUtil
										.getRequestJson(jsoncurrCMD).get(
												"FilePath"); // 获取filepath

								if (MD5Str.length() > 0) {
									if (checkFile(MD5Str, filePaths)) {
										// 处理数据填充共享池数据
										try {
											putData(BoardcastType, filePaths);

										} catch (Exception e) {
											out.write("data is error "
													.getBytes());
											out.flush();
										}
										sendBroadCast(BoardcastType); // 发送消息启动活动
										out.write("success ".getBytes());
										out.flush();
									} else {
										out.write("File is changed ".getBytes());
										out.flush();
									}

								} else {
									sendBroadCast(BoardcastType); // 发送消息启动活动
									out.write("success ".getBytes());
									out.flush();
								}
							}
						}
					} catch (Exception e) {
						Log.e(TAG, Thread.currentThread().getName() + "---->"
								+ "read receive error1" + e.getMessage());
						out.write("err".getBytes());
						out.flush();
						client.close();
						break;

					}
				}

				in.close();
			} catch (Exception e) {
				Log.e(TAG, Thread.currentThread().getName() + "---->"
						+ "read receive error2");
				e.printStackTrace();
			} finally {
				try {
					if (client != null) {
						Log.v(TAG, Thread.currentThread().getName() + "---->"
								+ "client.close()");
						client.close();
					}
				} catch (IOException e) {
					Log.e(TAG, Thread.currentThread().getName() + "---->"
							+ "read receive error3");
					e.printStackTrace();
				}
			}
		}

		/* 读取命令 */
		public String readCMDFromSocket(InputStream in) throws IOException {
			int MAX_BUFFER_BYTES = 4096;
			String msg = "";
			byte[] tempbuffer = new byte[MAX_BUFFER_BYTES];

			int numReadedBytes = in.read(tempbuffer, 0, tempbuffer.length);
			if (numReadedBytes != -1) {
				msg = new String(tempbuffer, 0, numReadedBytes, "utf-8");
			}

			tempbuffer = null;
			return msg;
		}

	}

	// 发送线程

	// 全局变量来完成数据传送

	public class ThreadWriterSocket implements Runnable {
		private Socket client;
		BufferedOutputStream out;

		ThreadWriterSocket(Socket client) {

			this.client = client;
			try {
				out = new BufferedOutputStream(client.getOutputStream());

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		public boolean sendflag(Map map) {
			boolean flag = false;
			if (!map.isEmpty()) {
				if (map.get("flag").equals("revices"))
					flag = true;
			}
			return flag;
		}

		@Override
		public void run() {
			Log.d(TAG, Thread.currentThread().getName() + "---->"
					+ "a client has connected to server!");
			try {

				ioThreadFlag = true;
				while (ioThreadFlag) {
					try {
						if (!client.isConnected()) {
							break;
						}

						app = (MyApp) getApplication();
						Map map = app.getMap();
						if (!map.isEmpty() && !sendflag(map)) {

							String jsons = JsonUtil.getResponse(map);

							out.write(jsons.getBytes());
							out.flush();

							Activity a = (Activity) map.get(map
									.get("BoardcastType"));

							a.finish();

							map.clear();
						}

					} catch (Exception e) {
						Log.e(TAG, Thread.currentThread().getName() + "---->"
								+ "read send  error111111" + e.getMessage());

						client.close();
						break;
					}
				}
				out.close();

			} catch (Exception e) {
				Log.e(TAG, Thread.currentThread().getName() + "---->"
						+ "read send error222222");
				e.printStackTrace();
			} finally {
				try {
					if (client != null) {
						Log.v(TAG, Thread.currentThread().getName() + "---->"
								+ "client.close()");
						client.close();
					}
				} catch (IOException e) {
					Log.e(TAG, Thread.currentThread().getName() + "---->"
							+ "read send error333333");
					e.printStackTrace();
				}
			}
		}

	}
}
