package com.example.jsdemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * JS的调用的方法
 * 
 * @author yung
 *         <p>
 *         2014年6月24日 09:26:14
 *         <p>
 *         此类中的打开的QQ 和微信是直接通过包名和类名调用虽然QQ微信包名不容易变 但是主界面好事可能会变
 *         如果发现打不开QQ微信应用可以查看是否是QQ微信升级更改了类名
 */
public class AndroidJavaScript {
	private static final String TAG = AndroidJavaScript.class.getSimpleName();

	private Handler mHandler;
	Context c;
	String[] qqpackage = new String[] { "com.tencent.mobileqq",
			"com.tencent.mobileqq.activity.SplashActivity" };
	String[] wxpackage = new String[] { "com.tencent.mm",
			"com.tencent.mm.ui.LauncherUI" };
	private String securityId;
	private String xbox;
	private String rdsToken;
	private String rdsUa;

	public AndroidJavaScript(Context c, Handler handler) {
		this.c = c;
		this.mHandler = handler;
	}

	@JavascriptInterface
	public void sendFormData(String secId, String _xbox, String rdsToken, String rdsUa){
		this.securityId = secId;
		this.xbox = _xbox;
		this.rdsToken = rdsToken;
		this.rdsUa = rdsUa;

		String encodedFormData = "";
		try {
			String charset = "utf-8";
			encodedFormData = String.format("securityId=%s&_xbox=%s&rdsToken=%s&rdsUa=%s", URLEncoder.encode(securityId, charset), URLEncoder.encode(xbox, charset), URLEncoder.encode(rdsToken, charset), URLEncoder.encode(rdsUa, charset) );
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Log.d(TAG, encodedFormData);
		if(!TextUtils.isEmpty(encodedFormData)){
			Message msg = mHandler.obtainMessage();
			msg.obj = encodedFormData;
			msg.what = 1;
			mHandler.sendMessage(msg);
		}
	}

	@JavascriptInterface
	public void handleDataLink(final String dataLink){
		Log.d("jsinterface", "num = " + dataLink);
		if(mHandler != null){
			Message msg = mHandler.obtainMessage();
			msg.obj = dataLink;
			msg.what = 0;
			mHandler.sendMessage(msg);
		}
//		String resultData = "";
//		try {
//			URL url = new URL(dataLink);
//			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//			String cookie = ((serve)c).getCookieStr();
//			conn.setRequestProperty("Cookie", cookie);
//			conn.connect();
//			InputStream is = conn.getInputStream();
//			InputStreamReader isreader = new InputStreamReader(is, "gbk");
//			BufferedReader breader = new BufferedReader(isreader);
//			String inputLine = "";
//			while((inputLine = breader.readLine()) != null) {
//				resultData += inputLine + "\n";
//			}
//
//			Log.d("jsinterface", "result: " + resultData);
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	@JavascriptInterface
	public void NumOfHLinks(final int num){
		Log.d("jsinterface", "num = " + num);
	}

	@JavascriptInterface
	public void callPhone(final String telphone) {

		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
				+ telphone));
		c.startActivity(intent);

	}

	@JavascriptInterface
	public void callQQ(String qq) {
		// 实现调用电话号码

		if (!checkBrowser(qqpackage[0])) {

		} else {
			Intent intent = new Intent();
			ComponentName cmp = new ComponentName(qqpackage[0], qqpackage[1]);
			intent.setAction(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setComponent(cmp);
			c.startActivity(intent);
		}

	}

	@JavascriptInterface
	public void callWeixin(String weixin) {

		if (!checkBrowser(wxpackage[0])) {

		} else {
			Intent intent = new Intent();
			ComponentName cmp = new ComponentName(wxpackage[0], wxpackage[1]);
			intent.setAction(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setComponent(cmp);
			c.startActivity(intent);

		}

	}

	// 获取在webview上获取js生成的html的源码
	@JavascriptInterface
	public void getSource(String htmlstr) {
		// Log.e("html", htmlstr);
		// String path = c.getFilesDir().getAbsolutePath() + "/serve.html"; //
		// data/data目录

	}

	public boolean checkBrowser(String packageName) {
		if (packageName == null || "".equals(packageName))
			return false;
		try {
			ApplicationInfo info = c.getPackageManager().getApplicationInfo(
					packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}
}