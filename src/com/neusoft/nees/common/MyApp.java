package com.neusoft.nees.common;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;

public class MyApp extends Application {

	private String message;//存储信息
    private String[] jsonString;// 传输数据
	private Map  map;//发送用的
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String[] getJsonString() {
		return jsonString;
	}
	public void setJsonString(String[] jsonString) {
		this.jsonString = jsonString;
	}
	public Map getMap() {
		return map;
	}
	public void setMap(Map map) {
		this.map = map;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		map=new HashMap();
		message="";
		jsonString=null;
	}
}
