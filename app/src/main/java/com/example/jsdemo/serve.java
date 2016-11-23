package com.example.jsdemo;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 2014年6月26日 11:10:47
 *
 * @author yung
 */
@SuppressLint("JavascriptInterface")
public class serve extends Activity {

    public WebView myWebView;
    // 存在data/data目录下的html文件名
    String HTMLNAME = "serve.html";
    // 存在data/data目录下的填充html的js的文件名

    String JSNAME = "JSData.data";
    private String date = null;
    private String email = null;
    private String username = null;
    private String sex = null;
    private String cookieStr = null;
    private Handler mHanlder = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == 0) {
                String dataLink = (String) msg.obj;
                Log.d("jsinterface", dataLink);
                myWebView.loadUrl(dataLink);
            } else if (what == 1) {
                String encodedFormData = (String) msg.obj;
                postData(encodedFormData);
            }
        }
    };

    private void postData(final String encodedFormData) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://consumeprod.alipay.com/record/download.resource");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setUseCaches(false);
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("Connection", "keep-alive");
                    conn.setRequestProperty("Cookie", cookieStr);
                    conn.setRequestProperty("Author", "zhengkan");
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; HUAWEI NXT-AL10 Build/HUAWEINXT-AL10; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/53.0.2785.124 Mobile Safari/537.36");
                    conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
                    conn.setRequestProperty("Cache-Control", "max-age=0");
                    conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                    conn.setRequestProperty("Origin", "https://consumeprod.alipay.com");
                    conn.setRequestProperty("Upgrade-Insecure-Requests", "1");
                    conn.setRequestProperty("Referer", "https://consumeprod.alipay.com/record/download.htm?dateRange=sevenDays&endDate=2016.11.22&beginDate=2016.11.16&endTime=24%3A00&pageNum=1&status=all&beginTime=00%3A00&_input_charset=utf-8&tradeType=ALL&fundFlow=all&dateType=createDate&suffix=csv");
                    conn.setRequestProperty("Accept-Language", "en-US");
                    conn.connect();

                    DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                    dos.writeBytes(encodedFormData);
                    dos.flush();
                    dos.close();

                    int resultCode = conn.getResponseCode();
                    if (resultCode == 200) {
                        String contentType = conn.getHeaderField("Content-Type");
                        if (contentType == null || !contentType.contains("x-download")) {
                            return;
                        }
                        StringBuffer sb = new StringBuffer();
                        String readLine = "";

//                        BufferedReader responseReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                        while ((readLine = responseReader.readLine()) != null) {
//                            sb.append(readLine).append("\n");
//                        }
//                        Log.d("zkserve", "[Response]" + sb.toString());
//                        responseReader.close();\
                        File baseDir = Environment.getExternalStorageDirectory();
                        File filesDir = new File(baseDir, Environment.DIRECTORY_PICTURES);
                        if (!filesDir.exists()){
                            filesDir.mkdirs();
                        }
                        File alipayDownload = new File(filesDir, "alipayDownload.zip");
                        if (!alipayDownload.exists()) {
                            alipayDownload.createNewFile();
                        }
                        Log.d("Amor", "" + Environment.getRootDirectory().toString());
                        FileOutputStream fos = new FileOutputStream(alipayDownload);
                        InputStream is = conn.getInputStream();
                        int len = 0;
                        byte[] buf = new byte[1024];
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                        }
                        fos.close();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public String getCookieStr() {
        return cookieStr;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serve);
        myWebView = (WebView) findViewById(R.id.serve_vebview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        isExistsHTML();
        // myWebView.loadUrl("file:///android_asset/ss.html");
        String path = getFilesDir().getAbsolutePath() + HTMLNAME; // data/data目录
//		myWebView.loadUrl("file:///" + path);
//		myWebView.loadUrl("file:///android_asset/form.html");
        myWebView.addJavascriptInterface(new AndroidJavaScript(this, mHanlder), "Android");

        // myWebView.loadUrl("javascript:getStr('" + 122222 + "')");
        myWebView.setWebViewClient(webviewcilnt);
        String phon = "技术服务电话：,0731-22332233,0731-44332234;产品服务QQ:,5733935198,209384022;产品公众微信:,CSHNJK,yung7086,weixin";
//		String path1 = "file:///android_asset/wxicon.png";
        myWebView.loadUrl("https://auth.alipay.com/login/index.htm");

        saveHTMLData(phon);
    }

    WebViewClient webviewcilnt = new WebViewClient() {
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            if (url.contains("record/download.resource")) {
//				Log.d("jsinterface", "shouldInterceptRequest");
                String str = "Access Denied";
                InputStream data = null;
                try {
                    data = new ByteArrayInputStream(str.getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return new WebResourceResponse("text/html", "utf-8", data);
            }
            return super.shouldInterceptRequest(view, url);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            if (url != null && url.contains("record/download.resource")) {
                url = "";
            }
            super.onLoadResource(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d("jsinterface", "[onPageFinished] url = " + url);

            if (url.contains("record/advanced.htm")) {
                CookieManager cookieManager = CookieManager.getInstance();
                cookieStr = cookieManager.getCookie(url);
                Log.e("cookie", cookieStr);

                String js1 =
                        "function getElementByText(inner_html){\n" +
                                "var hlinks = document.getElementsByTagName('a'); \n" +
                                "var num = hlinks.length; \n" +
                                "for(var i = 0; i < num; i++) { \n" +
                                "var link = hlinks[i];\n" +
                                "if(link.innerHTML == inner_html){\n" +
                                "return link;\n" +
                                "}\n" +
                                "}\n" +
                                "}\n" +
                                "var theLink = getElementByText('Excel格式');\n" +
                                "theLink.innerHTML = 'Excel Format..';\n" +
                                "var dlink = theLink.getAttribute('data-link');\n" +
                                "Android.handleDataLink(dlink);\n";
                view.loadUrl("javascript:" + js1);
            } else if (url.contains("record/download.htm")) {
                String js = "var fm = document.getElementById('downloadForm');\n" +
                        "fm.onsubmit = function(e){\n" +
                        "var securityId = fm['securityId'].value;\n" +
                        "var xbox = fm['_xbox'].value;\n" +
                        "var rdsToken = fm['rdsToken'].value;\n" +
                        "var rdsUa = fm['rdsUa'].value;\n" +
                        "Android.sendFormData(securityId, xbox, rdsToken, rdsUa);\n" +
                        "var e = e || window.event;\n" +
                        "if(e.preventDefault) e.preventDefault();\n" +
                        "else { window.event.returnValue = false; }\n" +
                        "return false;\n" +
                        "};\n";
                view.loadUrl("javascript:" + js);

            }

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }
    };

    /**
     * 检查data/data目录是否存在html 不存在就把assets复制过来 * @throws IOException
     */
    private void isExistsHTML() {
        String path = getFilesDir().getAbsolutePath() + HTMLNAME; // data/data目录
        File file = new File(path);
        if (!file.exists() || true) {
            try {
                InputStream in = getAssets().open("serve.html"); // 从assets目录下复制
                FileOutputStream out = new FileOutputStream(file);
                int length = -1;
                byte[] buf = new byte[1024];
                while ((length = in.read(buf)) != -1) {
                    out.write(buf, 0, length);
                }
                out.flush();
                in.close();
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存从服务器获取的填充到html的数据
     *
     * @param phon
     */
    private void saveHTMLData(String phon) {
        try {
            FileOutputStream out = this.openFileOutput(JSNAME, MODE_PRIVATE);
            byte[] bytes = phon.getBytes();
            out.write(bytes);
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 获取填充到html的数据
     */
    private String loadHTMLData() {
        String jsstr = null;
        try {
            FileInputStream in = this.openFileInput(JSNAME);
            byte[] bytes = new byte[256];
            in.read(bytes);
            in.close();
            jsstr = new String(bytes);
            return jsstr;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jsstr;
    }

}
