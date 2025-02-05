package com.marianhello.bgloc;

import android.os.Build;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;

public class HttpPostService {
    public static final int BUFFER_SIZE = 1024;

    private String mUrl;
    private HttpURLConnection mHttpURLConnection;

    public interface UploadingProgressListener {
        void onProgress(int progress);
    }

    public HttpPostService(String url) {
        mUrl = url;
    }

    public HttpPostService(final HttpURLConnection httpURLConnection) {
        mHttpURLConnection = httpURLConnection;
    }

    private HttpURLConnection openConnection() throws IOException {
        if (mHttpURLConnection == null) {
            mHttpURLConnection = (HttpURLConnection) new URL(mUrl).openConnection();
        }
        return mHttpURLConnection;
    }

    public int postJSON(JSONObject json, Map headers) throws IOException {
        String jsonString = "null";
        if (json != null) {
            jsonString = json.toString();
        }
        return postJSONString(jsonString, headers);
    }

    public int postJSON(JSONArray json, Map headers) throws IOException {
        String jsonString = "null";
        if (json != null) {
            if (json.length() == 1) {
                JSONObject single = json.optJSONObject(0);
                if (single != null) {
                    jsonString = single.toString();
                } else {
                    jsonString = json.toString();
                }
            } else {
                jsonString = json.toString();
            }
        }
        return postJSONString(jsonString, headers);
    }

    public int postJSONString(String body, Map headers) throws IOException {
        if (headers == null) {
            headers = new HashMap();
        }
        
        String contentType = null;
        for (Object keyObj : headers.keySet()) {
            String key = (String) keyObj;
            if (key.equalsIgnoreCase("Content-Type")) {
                contentType = (String) headers.get(key);
                break;
            }
        }
        if (contentType == null) {
            contentType = "application/json";
        }
        
        String finalBody = body;
        if (contentType.equalsIgnoreCase("application/x-www-form-urlencoded")) {
            try {
                finalBody = jsonToUrlEncoded(body);
            } catch (Exception e) {
                finalBody = body;
            }
        }
        
        HttpURLConnection conn = this.openConnection();
        conn.setDoOutput(true);
        conn.setFixedLengthStreamingMode(finalBody.length());
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", contentType);
        
        Iterator<Map.Entry<String, String>> it = headers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> pair = it.next();
            if (!pair.getKey().equalsIgnoreCase("Content-Type")) {
                conn.setRequestProperty(pair.getKey(), pair.getValue());
            }
        }
        
        OutputStreamWriter os = null;
        try {
            os = new OutputStreamWriter(conn.getOutputStream());
            os.write(finalBody);
        } finally {
            if (os != null) {
                os.flush();
                os.close();
            }
        }
        return conn.getResponseCode();
    }
    
    private String jsonToUrlEncoded(String jsonString) throws Exception {
        JSONObject json = new JSONObject(jsonString);
        StringBuilder result = new StringBuilder();
        Iterator<String> keys = json.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = json.get(key).toString();
            if (result.length() > 0) {
                result.append("&");
            }
            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value, "UTF-8"));
        }
        return result.toString();
    }

    public int postJSONFile(File file, Map headers, UploadingProgressListener listener) throws IOException {
        return postJSONFile(new FileInputStream(file), headers, listener);
    }

    public int postJSONFile(InputStream stream, Map headers, UploadingProgressListener listener) throws IOException {
        if (headers == null) {
            headers = new HashMap();
        }

        final long streamSize = stream.available();
        HttpURLConnection conn = this.openConnection();

        conn.setDoInput(false);
        conn.setDoOutput(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            conn.setFixedLengthStreamingMode(streamSize);
        } else {
            conn.setChunkedStreamingMode(0);
        }
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        Iterator<Map.Entry<String, String>> it = headers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> pair = it.next();
            conn.setRequestProperty(pair.getKey(), pair.getValue());
        }

        long progress = 0;
        int bytesRead = -1;
        byte[] buffer = new byte[BUFFER_SIZE];

        BufferedInputStream is = null;
        BufferedOutputStream os = null;
        try {
            is = new BufferedInputStream(stream);
            os = new BufferedOutputStream(conn.getOutputStream());
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
                os.flush();
                progress += bytesRead;
                int percentage = (int) ((progress * 100L) / streamSize);
                if (listener != null) {
                    listener.onProgress(percentage);
                }
            }
        } finally {
            if (os != null) {
                os.flush();
                os.close();
            }
            if (is != null) {
                is.close();
            }
        }

        return conn.getResponseCode();
    }

    public static int postJSON(String url, JSONObject json, Map headers) throws IOException {
        HttpPostService service = new HttpPostService(url);
        return service.postJSON(json, headers);
    }

    public static int postJSON(String url, JSONArray json, Map headers) throws IOException {
        HttpPostService service = new HttpPostService(url);
        return service.postJSON(json, headers);
    }

    public static int postJSONFile(String url, File file, Map headers, UploadingProgressListener listener) throws IOException {
        HttpPostService service = new HttpPostService(url);
        return service.postJSONFile(file, headers, listener);
    }
}
