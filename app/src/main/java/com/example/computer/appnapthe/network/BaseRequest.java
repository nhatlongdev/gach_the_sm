package com.example.computer.appnapthe.network;

import android.net.Uri;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.computer.appnapthe.R;
import com.example.computer.appnapthe.configs.GlobalValue;
import com.example.computer.appnapthe.util.NetworkUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Trangpv
 * <p>
 * API Helper class.
 */
public class BaseRequest {
    public static final String TAG = BaseRequest.class.getSimpleName();
    public static final int METHOD_POST = com.android.volley.Request.Method.POST;
    public static final int METHOD_GET = com.android.volley.Request.Method.GET;
    public static final int METHOD_PUT = com.android.volley.Request.Method.PUT;
    public static final int METHOD_DELETE = com.android.volley.Request.Method.DELETE;

    // default1 params
    private static final String PARAM_TOKEN = "token";
    private static final String PARAM_USER_ID = "user_id";


    private static BaseRequest mInstance;

    public static BaseRequest getInstance() {
        if (mInstance == null) {
            mInstance = new BaseRequest();
        }

        return mInstance;
    }

    /**
     * set default1 pamrams
     */
    public static void get(String url, HashMap<String, String> params, final CompleteListener listener) {
        BaseRequest.getInstance().request(METHOD_GET, url, params, true, false, listener, null);
    }

    public static void get(String url, HashMap<String, String> params, boolean isShowProgress, final CompleteListener listener) {
        BaseRequest.getInstance().request(METHOD_GET, url, params,isShowProgress, false, listener, null);
    }

    public static void get(String url, HashMap<String, String> params, boolean isShowProgress, boolean isCaching, final CompleteListener listener) {
        BaseRequest.getInstance().request(METHOD_GET, url, params,isShowProgress,isCaching, listener, null);
    }

    public static void post(String url, HashMap<String, String> params, final CompleteListener listener) {
        BaseRequest.getInstance().request(METHOD_POST, url, params, true, false, listener, null);
    }

    public static void post(String url, HashMap<String, String> params, boolean isShowProgress, final CompleteListener listener) {
        BaseRequest.getInstance().request(METHOD_POST, url, params,isShowProgress, false, listener, null);
    }

    public static void post(String url, HashMap<String, String> params, boolean isShowProgress, boolean isCaching, final CompleteListener listener) {
        BaseRequest.getInstance().request(METHOD_POST, url, params,isShowProgress, isCaching, listener, null);
    }


    public void request(final int method, String url, HashMap<String, String> params, final boolean isShowProgress, final boolean isCaching, final CompleteListener listener,
                        String tag) {
        if (NetworkUtility.isNetworkAvailable()) {
//            showProgress(isShowProgress);
//            addDefaultParams(params);

            Log.d(TAG, "request params:" + params.toString());
            if (method == METHOD_GET) {
                Uri.Builder b = Uri.parse(url).buildUpon();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    b.appendQueryParameter(entry.getKey(), entry.getValue());
                }
                url = b.toString();
                params.clear();
            }

            Log.d(TAG, "starting request url:" + url);
            final String finalUrl = url;

            Listener<JSONObject> completeListener = new Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
//                    hideProgress(isShowProgress);
                    Log.d(TAG, "RESULT: " + response);
                    if (listener != null) {
                        if (listener instanceof CompleteListener) {
                           ApiResponse apiResponse = new ApiResponse(response);
                            if (!apiResponse.isError()) {
//                                if (apiResponse.getDataObject() != null || apiResponse.getDataArray() != null || apiResponse.getValueFromRoot1("data")!=0) {
//                                    listener.onSuccess(apiResponse);
//
//                                }else if(apiResponse.getValueFromRoot("status").equals("true") && apiResponse.getValueFromRoot("Message").equals("Success")){
//                                    listener.onSuccess(apiResponse);
//                                }else
                                if(apiResponse.getValueFromRoot1("code")!= 0){
                                    listener.onSuccess(apiResponse);
                                } else {
                                    // this case when server response is "data is newest". No data and using caching
                                    if (isCaching) {
//                                        getOfflineResponse(listener, finalUrl);
                                    }
                                }

                                // if caching = true. data will be cached. save it to db
                                if (isCaching) {
                                    String objectRoot = apiResponse.getRoot().toString();
//                                    DataStoreManager.saveCaching(finalUrl, objectRoot, apiResponse.getValueFromRoot(Constant.Caching.KEY_TIME_UPDATED));
                                }

                            } else {
                                listener.onError(apiResponse.getMessage());
                            }
                        }

                    }
                }
            };
            ErrorListener errorListener = new ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
//                    hideProgress(isShowProgress);
                    Log.e(TAG, "volley error: " + error.getClass());
                    if (listener != null) {
                        String errorMessage = "";
                        int code = ApiResponse.ERROR_CODE_UNKOWN;
                        if (error.getClass() == TimeoutError.class) {
                            code = ApiResponse.ERROR_CODE_REQUEST_TIMEOUT;
                            errorMessage = "Request timeout";
                        } else if (error.getClass() == AuthFailureError.class) {
                            errorMessage = error.getMessage() != null ? error.getMessage() : "No internet permission ?";
                        } else {
                            errorMessage = error.getMessage();
                            if (errorMessage == null || errorMessage.length() == 0)
                                errorMessage = error.toString();
                            code = error.networkResponse != null ? error.networkResponse.statusCode
                                    : ApiResponse.ERROR_CODE_UNKOWN;
                        }

                        if (listener instanceof CompleteListener) {
                            if (!isCaching) {
                                listener.onError(new ApiResponse(code, errorMessage).getMessage());
                            } else {
//                                getOfflineResponse(listener, finalUrl);
                            }
                        }

                    }
                }
            };

            ApiHelperJsonObjectRequest jr = new ApiHelperJsonObjectRequest(method, url, params, completeListener,
                    errorListener);
            if (tag != null) {
                jr.setTag(tag);
            }
            jr.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleyRequestManager.getRequestQueue().add(jr);
        } else {
            if (isCaching) {
//                addDefaultParams(params);
//                getOfflineResponse(listener,getFullUrlGet(url, params));
            } else {
//                AppUtil.showToast(mActivity, R.string.msg_connection_network_error);
            }
        }
    }

    private String getFullUrlGet(String url, HashMap<String, String> fullParams) {
        Uri.Builder b = Uri.parse(url).buildUpon();
        for (Map.Entry<String, String> entry : fullParams.entrySet()) {
            b.appendQueryParameter(entry.getKey(), entry.getValue());
        }
        url = b.toString();
        fullParams.clear();
        return url;
    }


    public interface CompleteListener {
        public void onSuccess(ApiResponse response);

        public void onError(String message);

    }

    /**
     * A request for retrieving a {@link JSONObject} response body at a given
     * URL. Use for posting params along with request, instead of posting body
     * like {@link JsonObjectRequest}
     */
    private class ApiHelperJsonObjectRequest extends JsonObjectRequest {
        Map<String, String> mParams;

        public ApiHelperJsonObjectRequest(int method, String url, Map<String, String> params,
                                          Listener<JSONObject> listener, ErrorListener errorListener) {
            super(method, url, null, listener, errorListener);
            mParams = params;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            HashMap<String, String> params = new HashMap<String, String>();
            params.putAll(super.getHeaders());
            //params.put("apikey", new String(Constant.API_KEY));

            Log.d(TAG, "header params:" + params.toString());
            return params;
        }

        // override getBodyContentType and getBody for prevent posting body.
        @Override
        public String getBodyContentType() {
            return null;
        }

        @Override
        public byte[] getBody() {
            if (this.getMethod() == METHOD_POST && mParams != null && mParams.size() > 0) {
                return encodeParameters(mParams, getParamsEncoding());
            }
            return null;
        }

        /**
         * Converts <code>params</code> into an
         * application/x-www-form-urlencoded encoded string.
         */
        private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
            StringBuilder encodedParams = new StringBuilder();
            try {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                    encodedParams.append('=');
                    encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                    encodedParams.append('&');
                }
                return encodedParams.toString().getBytes(paramsEncoding);
            } catch (UnsupportedEncodingException uee) {
                throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
            }

        }

    }

}
