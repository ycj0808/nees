package com.neusoft.nees.common;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;

public class MyApp extends Application {

	private String message;//�洢��Ϣ
    private String[] jsonString;// ��������
	private Map  map;//�����õ�
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
