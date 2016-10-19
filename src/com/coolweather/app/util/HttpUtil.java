package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class HttpUtil {
	
	public static void sendHttpRequest(final String address,
			final HttpCallbackListener listener)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run() {
				HttpURLConnection connection = null;
				try 
				{
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null)
					{
						response.append(line);
					}
					if (listener != null)
					{
						// 回调onFinish()方法
						listener.onFinish(response.toString());
					}
				} catch (Exception e) 
				{
					if (listener != null)
					{
						// 回调onError()方法
						listener.onError(e);
					}
				} finally
				{
					if (connection != null)
					{
						connection.disconnect();
					}
				}
			}
		}).start();
	}
	
	
	/**
	 * 解析服务器返回的Json数据，并将解析出的数据存储到本地。
	 */
	public static void handleWeatherResponse(Context context, String response)
	{
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherDesp = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2,
					weatherDesp, publishTime);
		} catch (JSONException e) {
			// TODO: handle exception
		}
	}
	
	
	/**
	 * 将服务器返回的所有天气信息存储到SharedPreferences文件中。
	 */
	public static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2, String weatherDesp,
			String publishTime)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
	}
	
}
