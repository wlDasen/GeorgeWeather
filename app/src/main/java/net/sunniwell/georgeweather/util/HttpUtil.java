package net.sunniwell.georgeweather.util;


import android.util.Log;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by admin on 2017/9/21.
 */

public class HttpUtil {
    public static final String TAG = "jpd-HttpUtil";

    public static void sendRequest(String requestUrl, Callback callback) {
        Log.d(TAG, "sendRequest: requestUrl:" + requestUrl);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(requestUrl)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
