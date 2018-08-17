package com.example.computer.appnapthe.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.computer.appnapthe.R;
import com.example.computer.appnapthe.base.BaseFragment;
import com.example.computer.appnapthe.configs.Apis;
import com.example.computer.appnapthe.configs.GlobalValue;
import com.example.computer.appnapthe.datastore.DataStoreManager;
import com.example.computer.appnapthe.modelmanager.RequestQueueSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends BaseFragment implements View.OnClickListener{

    private RelativeLayout liGame, liCard, liApp, rltParentSearch;
    private ImageView imgGame, imgCard, imgApp;
    private TextView tvGame, tvCard, tvApp, tvGuiLaiCard;
    public RelativeLayout rltVtel, rltVina, rltMobi;
    public ImageView imgVtel, imgVina, imgMobi;
//    public EditText edtMaThe;
    public AutoCompleteTextView edtMaThe;
    public Button btnTraCuu;
    private LinearLayout liParentInfor, liChildren7;
    private TextView tvTitle1, tvTitle2, tvTitle3, tvTitle4, tvTitle5, tvTitle6, tvTitle7, tvContent1, tvContent2, tvContent3, tvContent4, tvContent5, tvContent6, tvContent7, tvNoneData;
    private int checkOption = 1;
    private String ncc = "GPC", cardCode = "";
    public RequestQueue requestQueue;
    public boolean isData = false;
    public JSONObject jsonObjectResponsd = new JSONObject();
    /*------progress---*/
    private ProgressDialog progressDialog;
    /*handle*/
    private Handler handler;
    private Runnable myTask1;

    /*Json game, card, app*/
    public JSONObject jsonGame = null, jsonCard = null, jsonApp = null;

    public String[] langu = null;
    public List<String> listTex = new ArrayList<>();

    public static SearchFragment newInstance() {
        Bundle args = new Bundle();
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutInflate() {
        return R.layout.fragment_search;
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initView(View view) {
        langu = new String[]{"1223", "12234", "345","324","4600"};
        listTex.add("1223");
        listTex.add("1243");
        listTex.add("12237");
        progressDialog = new ProgressDialog(self);
        progressDialog.setCanceledOnTouchOutside(false);
        handler = new Handler();
        requestQueue = RequestQueueSingleton.getInstance(getMainActivity()).getRequestQueue();
        jsonGame = new JSONObject();
        jsonCard = new JSONObject();
        jsonApp = new JSONObject();
        rltParentSearch = view.findViewById(R.id.rlt_parent_search);
        liGame = view.findViewById(R.id.li_game);
        liCard = view.findViewById(R.id.li_card);
        liApp = view.findViewById(R.id.li_app);
        imgGame = view.findViewById(R.id.img_game);
        imgCard = view.findViewById(R.id.img_card);
        imgApp = view.findViewById(R.id.img_app);
        tvGame = view.findViewById(R.id.tv_game);
        tvCard = view.findViewById(R.id.tv_card);
        tvApp = view.findViewById(R.id.tv_app);
        rltVtel = view.findViewById(R.id.rlt_vtel);
        rltVina = view.findViewById(R.id.rlt_gpc);
        rltMobi = view.findViewById(R.id.rlt_vms);
        imgVina = view.findViewById(R.id.icon_click_vina);
        imgVtel = view.findViewById(R.id.icon_click_vtel);
        imgMobi = view.findViewById(R.id.icon_click_mobi);
        edtMaThe = view.findViewById(R.id.edt_ma_the);
        btnTraCuu = view.findViewById(R.id.btn_tra_cuu);
        liParentInfor = view.findViewById(R.id.li_parent_infor);
        liChildren7 = view.findViewById(R.id.li_children_7);
        tvTitle1 = view.findViewById(R.id.tv_title_1);
        tvContent1 = view.findViewById(R.id.tv_content_1);
        tvTitle2 = view.findViewById(R.id.tv_title_2);
        tvContent2 = view.findViewById(R.id.tv_content_2);
        tvTitle3 = view.findViewById(R.id.tv_title_3);
        tvContent3 = view.findViewById(R.id.tv_content_3);
        tvTitle4 = view.findViewById(R.id.tv_title_4);
        tvContent4 = view.findViewById(R.id.tv_content_4);
        tvTitle5 = view.findViewById(R.id.tv_title_5);
        tvContent5 = view.findViewById(R.id.tv_content_5);
        tvTitle6 = view.findViewById(R.id.tv_title_6);
        tvContent6 = view.findViewById(R.id.tv_content_6);
        tvTitle7 = view.findViewById(R.id.tv_title_7);
        tvContent7 = view.findViewById(R.id.tv_content_7);
        tvNoneData = view.findViewById(R.id.tv_none_data);
        tvGuiLaiCard = view.findViewById(R.id.tv_gui_lai_card);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, GlobalValue.arrSuggestCodeCard);
        edtMaThe.setThreshold(1);
        edtMaThe.setAdapter(arrayAdapter);
        edtMaThe.setText(GlobalValue.cardCodeCurrent);
    }

    @Override
    protected void getData() {
        liGame.setOnClickListener(this);
        liCard.setOnClickListener(this);
        liApp.setOnClickListener(this);
        rltVtel.setOnClickListener(this);
        rltVina.setOnClickListener(this);
        rltMobi.setOnClickListener(this);
        btnTraCuu.setOnClickListener(this);
        tvGuiLaiCard.setOnClickListener(this);

        rltParentSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMainActivity().hideKeyboard(getMainActivity());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_tra_cuu:
                getMainActivity().hideKeyboard(getMainActivity());
                if(edtMaThe.getText().toString().length() == 0){
                    getMainActivity().setTextForBottom("Vui lòng nhập mã thẻ!");
                }else if(ncc.equals("VTEL") && (edtMaThe.getText().toString().length() !=13 && edtMaThe.getText().toString().length() != 15)){
                    getMainActivity().setTextForBottom("Mã thẻ mạng Viettel phải bao gồm 13 hoặc 15 số!");
                }else if(ncc.equals("GPC") && (edtMaThe.getText().toString().length() !=12 && edtMaThe.getText().toString().length() != 14)){
                    getMainActivity().setTextForBottom("Mã thẻ mạng Vinafone phải bao gồm 12 hoặc 14 số!");
                }else if(ncc.equals("VMS") && (edtMaThe.getText().toString().length() !=12)){
                    getMainActivity().setTextForBottom("Mã thẻ mạng Mobifone phải bao gồm 12 số!");
                }else {
                    runPostDelay("Đang tìm kiếm dữ liệu, vui lòng đợi ...");
                    findCard(ncc, edtMaThe.getText().toString(), GlobalValue.jsonSimSelected.optString("serial_number"));
                    cardCode = edtMaThe.getText().toString();
                    if(GlobalValue.arrSuggestCodeCard.size()>0){
                        boolean check = false;
                        for (int i=0;i<GlobalValue.arrSuggestCodeCard.size(); i++){
                            if(GlobalValue.arrSuggestCodeCard.get(i).equals(edtMaThe.getText().toString())){
                                check = true;
                            }
                        }
                        if(check == false){
                            GlobalValue.arrSuggestCodeCard.add(edtMaThe.getText().toString());
                        }
                    }else {
                        GlobalValue.arrSuggestCodeCard.add(edtMaThe.getText().toString());
                    }
                    Log.d("inForSIm", "GLOBAL ARR SUGGEST CODECARD===>>>>" + GlobalValue.arrSuggestCodeCard.size());

                     /*save cache */
                    DataStoreManager.saveListSuggestCard(getMainActivity(), GlobalValue.arrSuggestCodeCard);
                }
                break;
            case R.id.li_game:
                getMainActivity().hideKeyboard(getMainActivity());
                if(checkOption != 1 && isData == true){
                    setTextView();
                }
                break;
            case R.id.li_card:
                getMainActivity().hideKeyboard(getMainActivity());
                if(checkOption != 2 && isData == true){
                    liGame.setBackgroundColor(Color.parseColor("#9E9E9E"));
                    tvGame.setTextColor(Color.parseColor("#ffffff"));
                    liCard.setBackgroundColor(Color.parseColor("#f8e71c"));
                    tvCard.setTextColor(Color.parseColor("#000000"));
                    liApp.setBackgroundColor(Color.parseColor("#9E9E9E"));
                    tvApp.setTextColor(Color.parseColor("#ffffff"));
                    imgGame.setVisibility(View.INVISIBLE);
                    imgCard.setVisibility(View.VISIBLE);
                    imgApp.setVisibility(View.INVISIBLE);
                    tvGuiLaiCard.setVisibility(View.VISIBLE);
                    checkOption = 2;
                    if(jsonObjectResponsd.optJSONObject("res") == null || jsonObjectResponsd.optJSONObject("res").optJSONObject("card") == null || jsonObjectResponsd.optJSONObject("res").optJSONObject("card").optString("_id")== null){
                        tvNoneData.setVisibility(View.VISIBLE);
                        liParentInfor.setVisibility(View.INVISIBLE);
                    }else {
                        JSONObject jsonCard_ = jsonObjectResponsd.optJSONObject("res").optJSONObject("card");
                        tvNoneData.setVisibility(View.INVISIBLE);
                        liParentInfor.setVisibility(View.VISIBLE);
                        tvTitle1.setText("Tên: ");
                        tvTitle2.setText("Tình Trạng: ");
                        tvTitle3.setText("Sim: ");
                        tvTitle4.setText("Mệnh giá: ");
                        tvTitle5.setText("PostGame: ");
                        tvTitle6.setText("PostTime: ");
                        liParentInfor.removeView(liChildren7);
                        if(jsonCard_.optInt("sentGame") == 0){
                            tvGuiLaiCard.setVisibility(View.VISIBLE);
                        }else if(jsonCard_.optInt("sentGame") == 1 && jsonGame.optInt("status") == 2){
                            tvGuiLaiCard.setVisibility(View.VISIBLE);
                        }else {
                            tvGuiLaiCard.setVisibility(View.INVISIBLE);
                        }
                        /*set tt 1*/
                        if(jsonCard_.optString("gameName") != null || !jsonCard_.optString("gameName").equals("")){
                            tvContent1.setText(jsonGame.optString("gameName"));
                        }else {
                            tvContent1.setText("Không có");
                        }

                        /*set tt 2*/
                        if(jsonCard_.optInt("status") == 0){
                            tvContent2.setText("Chưa gạch");
                        }else if(jsonCard_.optInt("status") == 1){
                            tvContent2.setText("Đang gửi thẻ");
                        }else if(jsonCard_.optInt("status") == 2){
                            tvContent2.setText("Đã nạp sai");
                        }else if(jsonCard_.optInt("status") == 8){
                            tvContent2.setText("Đã nạp đúng");
                        }else {
                            tvContent2.setText("Không có");
                        }

                        if(jsonCard_.optString("simNumber") != null && jsonCard_.optString("simNumber").length() != 0){
                            Log.d("inForSIm", "CO CHAY VAO DAY===>>>>" + jsonCard_.optString("simNumber"));
                            tvContent3.setText(jsonCard_.optString("simNumber"));
                        }else {
                            Log.d("inForSIm", "CO CHAY VAO DAY 111===>>>>" + GlobalValue.jsonSimSelected.toString());
                            tvContent3.setText("Không có");
                        }

                        if(jsonCard_.optString("faceValueServer") != null && !jsonCard_.optString("faceValueServer").equals("")){
                            tvContent4.setText(jsonCard_.optString("faceValueServer"));
                        }else {
                            tvContent4.setText("Không có");
                        }

                        if(jsonCard_.optInt("sentGame") == 0){
                            tvContent5.setText("Chưa gửi");
                        }else if(jsonCard_.optInt("sentGame") == 1){
                            tvContent5.setText("Đã gửi");
                        }else {
                            tvContent5.setText("Không có");
                        }

                        if(jsonCard_.optString("sentGameDate") != null || !jsonCard_.optString("sentGameDate").equals("")){
                            tvContent6.setText(jsonCard_.optString("sentGameDate"));
                        }else {
                            tvContent6.setText("Không có");
                        }
                    }
                }
                break;
            case R.id.li_app:
                getMainActivity().hideKeyboard(getMainActivity());
                if(checkOption != 3 && isData == true){
                    liGame.setBackgroundColor(Color.parseColor("#9E9E9E"));
                    tvGame.setTextColor(Color.parseColor("#ffffff"));
                    liCard.setBackgroundColor(Color.parseColor("#9E9E9E"));
                    tvCard.setTextColor(Color.parseColor("#ffffff"));
                    liApp.setBackgroundColor(Color.parseColor("#f8e71c"));
                    tvApp.setTextColor(Color.parseColor("#000000"));
                    imgGame.setVisibility(View.INVISIBLE);
                    imgCard.setVisibility(View.INVISIBLE);
                    imgApp.setVisibility(View.VISIBLE);
                    checkOption = 3;
                    tvNoneData.setVisibility(View.VISIBLE);
                    liParentInfor.setVisibility(View.INVISIBLE);
                    String keyCard = cardCode + "_" + ncc;
                    if(GlobalValue.jsonDataCardLoaded.optJSONObject(keyCard) == null){
                        tvNoneData.setVisibility(View.VISIBLE);
                        liParentInfor.setVisibility(View.INVISIBLE);
                    }else {
                        JSONObject jsonApp = GlobalValue.jsonDataCardLoaded.optJSONObject(keyCard);
                        tvNoneData.setVisibility(View.INVISIBLE);
                        liParentInfor.setVisibility(View.VISIBLE);
                        tvTitle1.setText("*101#: ");
                        tvTitle2.setText("*100#: ");
                        tvTitle3.setText("TG gạch: ");
                        tvTitle4.setText("TG update: ");
                        tvTitle5.setText("Sim: ");
                        tvTitle6.setText("Mệnh giá: ");
                        liParentInfor.removeView(liChildren7);
                        tvContent1.setText(jsonApp.optString("ussdRes1"));
                        tvContent2.setText(jsonApp.optString("ussdRes2"));
                        tvContent3.setText(jsonApp.optString("time_loaded"));
                        tvContent4.setText(jsonApp.optString("time_update"));
                        tvContent5.setText(jsonApp.optString("simNumber"));
                        tvContent6.setText(jsonApp.optString("menh_gia_the"));
                    }
                }
                break;
            case R.id.tv_gui_lai_card:
                /*send card to game*/
                runPostDelay("Đang gửi yêu cầu, vui lòng đợi ...");
                sendCardToGame(ncc, cardCode);
                break;
            case R.id.rlt_vtel:
                getMainActivity().hideKeyboard(getMainActivity());
                if(!ncc.equals("VTEL")){
                    imgVtel.setVisibility(View.VISIBLE);
                    imgVina.setVisibility(View.INVISIBLE);
                    imgMobi.setVisibility(View.INVISIBLE);
                    ncc = "VTEL";
                }
                break;
            case R.id.rlt_gpc:
                getMainActivity().hideKeyboard(getMainActivity());
                if(!ncc.equals("GPC")){
                    imgVtel.setVisibility(View.INVISIBLE);
                    imgVina.setVisibility(View.VISIBLE);
                    imgMobi.setVisibility(View.INVISIBLE);
                    ncc = "GPC";
                }
                break;
            case R.id.rlt_vms:
                getMainActivity().hideKeyboard(getMainActivity());
                if(!ncc.equals("VMS")){
                    imgVtel.setVisibility(View.INVISIBLE);
                    imgVina.setVisibility(View.INVISIBLE);
                    imgMobi.setVisibility(View.VISIBLE);
                    ncc = "VMS";
                }
                break;
        }
    }

    /*ham find card */
    public void findCard(String providerID, String cardCode, String simNumber){
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("providerID", providerID);
            jsonObject.put("cardCode", cardCode);
            jsonObject.put("simNumber", simNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("inForSIm", "JSON FIND CARD: " + jsonObject.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Apis.URL_FIND_CARD, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        closeHandle();
                        Log.d("inForSIm", "SUCCESS - FIND CARD===>>>>>: " + response.toString());
                        boolean status = response.optBoolean("s");
                        if(status == true){
                            jsonGame = new JSONObject();
                            jsonCard = new JSONObject();
                            if(response.optJSONObject("res") != null){
                                isData = true;
                                if(response.optJSONObject("res").optJSONObject("game")!= null){
                                    try {
                                        jsonObjectResponsd = new JSONObject(response.toString());
                                        setTextView();
                                        jsonGame = response.optJSONObject("res").optJSONObject("game");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                if(response.optJSONObject("res").optJSONObject("card")!= null){
                                    try {
                                        jsonObjectResponsd = new JSONObject(response.toString());
                                        setTextView();
                                        jsonCard = response.optJSONObject("res").optJSONObject("card");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }else {
                            jsonGame = new JSONObject();
                            jsonCard = new JSONObject();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("inForSIm", error.toString());
                closeHandle();
            }
        });
        jsonObjectRequest.setTag("APP");
        Log.d("inForSIm", "request: " + jsonObjectRequest.toString());
        requestQueue.add(jsonObjectRequest);
    }

    /*ham gui lai card to server*/
    public void sendCardToGame(String providerID, String cardCode){
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("providerID", providerID);
            jsonObject.put("cardCode", cardCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("inForSIm", "JSON SEND CARD TO GAME: " + jsonObject.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Apis.URL_SEND_CARD_TO_GAME, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        closeHandle();
                        Log.d("inForSIm", "SUCCESS - SEND CARD TO GAME===>>>>>: " + response.toString());
                        boolean status = response.optBoolean("s");
                        if(status == true){

                        }else {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("inForSIm", error.toString());
                closeHandle();
            }
        });
        jsonObjectRequest.setTag("APP");
        Log.d("inForSIm", "request: " + jsonObjectRequest.toString());
        requestQueue.add(jsonObjectRequest);
    }

    /*ham set data cho textView*/
    public void setTextView(){
        liGame.setBackgroundColor(Color.parseColor("#f8e71c"));
        tvGame.setTextColor(Color.parseColor("#000000"));
        liCard.setBackgroundColor(Color.parseColor("#9E9E9E"));
        tvCard.setTextColor(Color.parseColor("#ffffff"));
        liApp.setBackgroundColor(Color.parseColor("#9E9E9E"));
        tvApp.setTextColor(Color.parseColor("#ffffff"));
        tvGuiLaiCard.setVisibility(View.INVISIBLE);
        imgGame.setVisibility(View.VISIBLE);
        imgCard.setVisibility(View.INVISIBLE);
        imgApp.setVisibility(View.INVISIBLE);
        checkOption = 1;
        if(jsonObjectResponsd.optJSONObject("res") == null || jsonObjectResponsd.optJSONObject("res").optJSONObject("game") == null || jsonObjectResponsd.optJSONObject("res").optJSONObject("game").optString("_id")== null){
            tvNoneData.setVisibility(View.VISIBLE);
            liParentInfor.setVisibility(View.INVISIBLE);
        }else {
            if(liChildren7 == null){
                liParentInfor.addView(liChildren7);
            }
            JSONObject jsonGame = jsonObjectResponsd.optJSONObject("res").optJSONObject("game");
            tvNoneData.setVisibility(View.INVISIBLE);
            liParentInfor.setVisibility(View.VISIBLE);
            tvTitle1.setText("Tên: ");
            tvTitle2.setText("Tình Trạng: ");
            tvTitle3.setText("Mệnh giá: ");
            tvTitle4.setText("Bảo: ");
            tvTitle5.setText("Tên KH: ");
            tvTitle6.setText("Mã KH: ");
            tvTitle7.setText("Thời gian nạp: ");
            /*set tt 1*/
            if(jsonGame.optString("gameName") != null || !jsonGame.optString("gameName").equals("")){
                tvContent1.setText(jsonGame.optString("gameName"));
            }else {
                tvContent1.setText("Không có");
            }

            /*set tt 2*/
            if(jsonGame.optInt("status") == 1){
                tvContent2.setText("Chờ nạp");
            }else if(jsonGame.optInt("status") == 2){
                tvContent2.setText("Đang xử lý");
            }else if(jsonGame.optInt("status") == 3){
                tvContent2.setText("Đã nạp đúng");
            }else if(jsonGame.optInt("status") == 4){
                tvContent2.setText("Đã nạp sai");
            }else {
                tvContent2.setText("Không có");
            }

            if(jsonGame.optString("card_value") != null && !jsonGame.optString("card_value").equals("")){
                tvContent3.setText(jsonGame.optString("card_value"));
            }else {
                tvContent3.setText("Không có");
            }

            if(jsonGame.optString("amount") != null && !jsonGame.optString("amount").equals("")){
                tvContent4.setText(jsonGame.optString("amount")+"");
            }else {
                tvContent4.setText("Không có");
            }

            if(jsonGame.optString("account_name") != null || !jsonGame.optString("account_name").equals("")){
                tvContent5.setText(jsonGame.optString("account_name"));
            }else {
                tvContent5.setText("Không có");
            }

            if(jsonGame.optString("account_code") != null || !jsonGame.optString("account_code").equals("")){
                tvContent6.setText(jsonGame.optString("account_code"));
            }else {
                tvContent6.setText("Không có");
            }

            if(jsonGame.optString("receive_from_client_time") != null || !jsonGame.optString("receive_from_client_time").equals("")){

                tvContent7.setText(jsonGame.optString("receive_from_client_time"));
            }else {
                tvContent7.setText("Không có");
            }

        }
    }

    /*run post delay*/
    public void runPostDelay(final String msg){
        if(!getMainActivity().isFinishing()){
            getMainActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.setMessage(msg);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            });
            myTask1 = new Runnable() {
                @Override
                public void run() {
                    if(progressDialog!=null && progressDialog.isShowing() && !getMainActivity().isFinishing()){
                        progressDialog.dismiss();
                    }
                }
            };
            handler.postDelayed(myTask1,15000);
        }
    }

    /*close handle*/
    public void closeHandle(){
        if(progressDialog!=null && progressDialog.isShowing() && !getMainActivity().isFinishing()){
            progressDialog.dismiss();
        }
        if(handler!=null){
            handler.removeCallbacks(myTask1);
        }
    }
}