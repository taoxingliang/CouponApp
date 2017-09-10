package api;

import okhttp3.Response;

/**
 * Created by Administrator on 2017/7/8.
 */

public interface RequestCallback {

    void onSuccess(Response response);

    void onFailure(int statuscode) ;

}
