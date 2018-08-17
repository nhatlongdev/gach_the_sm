package com.example.computer.appnapthe.modelmanager;

import android.content.Context;
import android.util.Log;

import com.example.computer.appnapthe.configs.Apis;
import com.example.computer.appnapthe.network.BaseRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class RequestManager extends BaseRequest {
    // Params
    private static final String PARAM_STATUS = "status";
    private static final String PARAM_MESSAGE = "message";
    public static final String PARAM_DATA = "data";
    private static final String PARAM_PAGE = "page";
    public static final String PARAM_TOKEN = "token";
    public static final String PARAM_TYPE = "type";
    public static final String PARAM_LIMIT = "limit";
    public static final String TYPE = "all";
    public static final String TYPE_REGISTER_DEVICE = "1";
    public static final String STATUS = "all";
    public static final String LIMIT = "10";
    public static final String PARAM_GCM_ID = "gcm_id";
    public static final String PARAM_IME = "ime";
    public static final String PARAM_ACCESS_TOKEN = "access_token";
    public static final String KEY_PATH = "path";
    public static final String KEY_API_NAME = "api_name";
    public static final String KEY_REQUEST_BODY = "request_body";
    public static final String KEY_FACEBOOK_ACCESS_TOKEN = "facebook_access_token";
    public static final String KEY_BODY = "body";


    public static HashMap<String, String> createJsonObjectToString(JSONObject jsonObject){
        JSONObject jsonObjectBig = new JSONObject();
        try {
            jsonObjectBig.put(KEY_REQUEST_BODY, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String toJsonString = jsonObjectBig.toString();
        HashMap<String, String> params = new HashMap<>();
        params.put("Content-Type", "application/json; charset=utf-8");
        params.put("body",toJsonString);
        return params;
    }

    /*ADD CARD TO SERVER GAME*/
    public static void addCardToServerGame(String typeGame, String acc, String ncc, String code, String deviceId, final  CompleteListener completeListener){
        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("aid", "");
            jsonObject1.put("acc", acc);
            jsonObject1.put("ncc",ncc);
            jsonObject1.put("code", code);
            jsonObject1.put("did", deviceId);
            jsonObject1.put("from","app");
            jsonObject1.put("key","app25062018OK");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = "";
        if(typeGame.equals("chan")){
            url = Apis.URL_ADD_CARD_CHAN;
        }else if(typeGame.equals("phom")){
            url = Apis.URL_ADD_CARD_PHOM;
        }else if(typeGame.equals("co")){
            url = Apis.URL_ADD_CARD_CO;
        }
        HashMap<String, String> params = createJsonObjectToString(jsonObject1);
        post(url,params,false,completeListener);
        Log.d("log_request","Request Add Card: " + jsonObject1.toString());
    }

}
