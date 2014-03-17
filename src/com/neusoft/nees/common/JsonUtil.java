package com.neusoft.nees.common;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonUtil {
	public static Map getRequestJson(String json) {
		JSONTokener jsonParser = new JSONTokener(json);
		Map map = new HashMap();
		try {
			JSONObject obj = (JSONObject) jsonParser.nextValue();
			JSONArray MD5Strs = obj.getJSONArray("MD5Str"); // md5ֵ
			String BoardcastType = obj.getString("BoardcastType");// �㲥����
			JSONArray filePaths = obj.getJSONArray("FilePath");
			map.put("BoardcastType", BoardcastType);
			map.put("MD5Str", MD5Strs);
			map.put("FilePath", filePaths);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	public static String getResponse(Map obj) {

		JSONObject object = new JSONObject();
		String md5str = "";
		String filepath = "";

		try {
			if (obj != null) {
				md5str = obj.get("MD5Str").toString();
				filepath = obj.get("MD5Str").toString();
			}

			if (!"".equals(md5str)) {
				String[] md5 = (String[]) obj.get("MD5Str");
				object.put("MD5Str", getArray(md5));
			} else {
				object.put("MD5Str", md5str);
			}
			if (!"".equals(filepath)) {
				String[] path = (String[]) obj.get("FilePath");
				object.put("FilePath", getArray(path));
			} else {
				object.put("FilePath", filepath);
			}
			object.put("BoardcastType", obj.get("BoardcastType"));// ��һ���㲥
			object.put("Data", obj.get("Data")); // ���ĸ�������
		} catch (JSONException e) {
			e.printStackTrace();

		}

		return object.toString();
	}

	public static JSONArray getArray(String[] obj) {

		JSONArray array = new JSONArray(); // ����
		if (obj.length == 0)
			array.put("");
		else {

			for (int i = 0; i < obj.length; i++) {
				array.put(obj[i]);
			}
		}
		return array;
	}

}
