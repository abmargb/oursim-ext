package br.edu.ufcg.lsd.oursim.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtils {

	public static String getJSONString(JSONObject object, String key) {
		try {
			return object.getString(key);
		} catch (JSONException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static JSONObject getJSONObject(JSONObject object, String key) {
		try {
			return object.getJSONObject(key);
		} catch (JSONException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static JSONObject getJSONObject(JSONArray object, int index) {
		try {
			return object.getJSONObject(index);
		} catch (JSONException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public static JSONArray getJSONArray(JSONObject object, String key) {
		try {
			return object.getJSONArray(key);
		} catch (JSONException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static JSONObject asJSON(String gridStr) {
		try {
			return new JSONObject(gridStr);
		} catch (JSONException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static int getJSONInteger(JSONObject object, String key) {
		try {
			return object.getInt(key);
		} catch (JSONException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static long getJSONLong(JSONObject object, String key) {
		try {
			return object.getLong(key);
		} catch (JSONException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static double getJSONDouble(JSONObject object, String key) {
		try {
			return object.getDouble(key);
		} catch (JSONException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
