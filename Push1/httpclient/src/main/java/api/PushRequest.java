package api;

import java.util.HashMap;
import java.util.Map;

import http.OkhttpStack;
import okhttp3.RequestBody;


/**
 * Created by Administrator on 2017/7/7.
 */

public class PushRequest {

    private Map<String, String> mHeader;
    private RequestBody mBody;
    private String mUrl;
    private String mMethod;

    protected void setHeader(Map header) {
        mHeader = header;
    }

    protected void setBody(RequestBody body) {
        mBody = body;
    }

    protected void setUrl(String url) {
        mUrl = url;
    }

    public Map<String, String> getmHeader() {
        return mHeader;
    }

    public RequestBody getmBody() {
        return mBody;
    }

    public String getmUrl() {
        return mUrl;
    }

    public String getmMethod() {
        return mMethod;
    }

    protected  void setMethod(String method) {

        mMethod = method;
    }

    public static Builder newBuilder(String url) {
        return new Builder(url);
    }

    public void excuteAsyncRequest(RequestCallback callback) {

        OkhttpStack.performAsyncRequest(this, callback);

    }


    public static class Builder {

        private Map<String, String> mBHeader;
        private String mBMethod;
        private RequestBody mBBody;
        private String mBUrl;

        public Builder(String url) {
            if (url == null) {
                throw new NullPointerException("url == null");
            }
            mBUrl = url;
            mBHeader = new HashMap<String, String>();
        }

        public Builder setAccept(String accept) {
            if (accept == null) {
                throw new NullPointerException("accept == null");
            }
            mBHeader.put("accept", accept);
            return this;
        }

        public  Builder setContentType(String type) {
            if (type == null) {
                throw new NullPointerException("ContentType == null");
            }
            mBHeader.put("content-type", type);
            return this;
        }

        public Builder setMethod(String method) {
            if (method == null) {
                throw new NullPointerException("method == null");
            }
            mBMethod = method;
            return this;
        }

        public Builder setBody(RequestBody body) {
            if (body == null) {
                throw new NullPointerException("body == null");
            }
            mBBody = body;
            return this;
        }

        public PushRequest build() {
            PushRequest request = new PushRequest();
            request.setBody(mBBody);
            request.setHeader(mBHeader);
            request.setMethod(mBMethod);
            request.setUrl(mBUrl);
            return request;
        }

    }

}
