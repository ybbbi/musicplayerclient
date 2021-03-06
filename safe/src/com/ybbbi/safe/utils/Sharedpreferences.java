package com.ybbbi.safe.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Sharedpreferences {
	private static SharedPreferences sp;

	public static void saveBoolean(Context context, String key, boolean value) {
		if (sp == null) {
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		sp.edit().putBoolean(key, value).commit();
	}

	public static boolean getBoolean(Context context, String key,
			boolean defValue) {
		if (sp == null) {
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		return sp.getBoolean(key, defValue);
	}
	public static void saveString (Context context,String key,String defValue){
		if(sp==null){
			sp=context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		sp.edit().putString(key, defValue).commit();
	}
	public static String getString(Context context,String key,String defValue){
		if(sp==null){
			sp=context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		return sp.getString(key, defValue);
	}
	public static void saveInt(Context context,String key,int value){
		if(sp==null){
			sp=context.getSharedPreferences("config", Context.MODE_PRIVATE);
			
		}
		 sp.edit().putInt(key, value).commit();
		
	}
	public static int getInt(Context context,String key,int defValue){
		if(sp==null){
			sp=context.getSharedPreferences("config", Context.MODE_PRIVATE);
			
		}
		
		return sp.getInt(key, defValue);
	}
}
