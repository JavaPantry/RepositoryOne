package com.avp.excel.template.engine;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Constants and utility methods for parsing template DSL
 * 
 * @author ptitchkin
 */
public abstract class Descriptor {

	protected static final int NOT_FOUND = -1;
	protected static final String START = "start";
	protected static final String END = "end";
	protected static final String TABLE = "table";
	protected static final String CLASS_NAME = "className";
	protected static final String TOKEN_DELIMITER = ";";
	protected static final String TAG_VALUE_DELIMITER = ":";
	protected static final String START_TAG = ".{";
	protected static final String START_VAR = "${"; //Note: same length as START_TAG
	protected static final String CLOSE_TAG = "}";

	/**
	 * remove starting '.{' and trailing '}'
	 * 
	 * @param inputStr
	 * @return
	 */
	public static String stripDecoration(String inputStr) {
		return inputStr.substring(START_TAG.length(), inputStr.length()-CLOSE_TAG.length());
	}
	public static String stripPrefixChar(String inputStr) {
		return inputStr.substring(1, inputStr.length());
	}
	public static <T> T json2Obj(String json, Type type) {
		Gson gson = new GsonBuilder().create();
		return gson.fromJson(json, type);
	}
}