package http;

import android.content.Context;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import api.PushRequest;
import api.RequestCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/7/8.
 */

public class OkhttpStack {

    private static Context mContext;

    private static OkHttpClient okHttpClient;

    private OkhttpStack() {

    }

    public static void initialize(Context context) {
        mContext = context;
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder().build();;
        }
    }

    public static OkHttpClient getInstance() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder().build();;
        }
        return okHttpClient;
    }

    public static void performAsyncRequest(PushRequest request, final RequestCallback callback) {
        Request okRequest = null;
//        if (request.getmHeader() != null) {
//            Headers.Builder headersBuilder = .newBuilder();
//            HashMap map = (HashMap) request.getmHeader();
//            Iterator iter = map.entrySet().iterator();
//            while (iter.hasNext()) {
//                Map.Entry entry = (Map.Entry) iter.next();
//                headersBuilder.set((String)entry.getKey(), (String)entry.getValue());
//            }
//            headers = headersBuilder.build();
//        }
//        if (headers != null && request.getmBody() != null) {
            okRequest = new Request.Builder()
                    .url(request.getmUrl())
                    .post(request.getmBody())
                    .build();
//        }else if (headers != null) {
//            okRequest = new Request.Builder()
//                    .url(request.getmUrl())
//                    .method(request.getmMethod(), null)
//                    .headers(headers)
//                    .build();
//        } else {
//            okRequest = new Request.Builder()
//                    .url(request.getmUrl())
//                    .method(request.getmMethod(), null)
//                    .build();
//        }
        getInstance().newCall(okRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(1000);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSuccess(response);
                } else {
                    callback.onFailure(response.code());
                }
            }
        });


    }

}
