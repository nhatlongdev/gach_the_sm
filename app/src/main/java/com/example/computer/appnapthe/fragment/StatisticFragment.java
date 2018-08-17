package com.example.computer.appnapthe.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.computer.appnapthe.R;
import com.example.computer.appnapthe.adapter.CardStatisticAdapter;
import com.example.computer.appnapthe.base.BaseFragment;
import com.example.computer.appnapthe.configs.Apis;
import com.example.computer.appnapthe.configs.GlobalValue;
import com.example.computer.appnapthe.listener.IOnItemClickedListener;
import com.example.computer.appnapthe.modelmanager.RequestQueueSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StatisticFragment extends BaseFragment implements View.OnClickListener{

    /*mảng option card*/
    public String arr[]={
            "Thẻ chưa gửi sang PaymentCard",
            "Thẻ chưa gửi sang Game",
            "Thẻ chưa nạp"};
    public Spinner spinnerOption;
    public TextView tvtitleOption;
    public Button btnSearch;
    public RecyclerView rcvListCard;
    public List<String> listIdCard;
    public JSONObject jsonListCard;
    public CardStatisticAdapter cardStatisticAdapter;

    /*------progress---*/
    private ProgressDialog progressDialog;
    /*handle*/
    private Handler handler;
    private Runnable myTask1;
    public RequestQueue requestQueue;

    public static StatisticFragment newInstance() {
        Bundle args = new Bundle();
        StatisticFragment fragment = new StatisticFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutInflate() {
        return R.layout.fragment_statistic;
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initView(View view) {
        requestQueue = RequestQueueSingleton.getInstance(getMainActivity()).getRequestQueue();
        progressDialog = new ProgressDialog(self);
        progressDialog.setCanceledOnTouchOutside(false);
        handler = new Handler();

        btnSearch = view.findViewById(R.id.tv_search);
        rcvListCard = view.findViewById(R.id.rcv_result);
        tvtitleOption = view.findViewById(R.id.tv_title_option);
        spinnerOption = view.findViewById(R.id.spinner_option);
        listIdCard = new ArrayList<>();
        jsonListCard = new JSONObject();
        //Gán Data source (arr) vào Adapter
        ArrayAdapter<String> adapter=new ArrayAdapter<String>
                (
                        getMainActivity(),
                        android.R.layout.simple_spinner_item,
                        arr
                );
        //phải gọi lệnh này để hiển thị danh sách cho Spinner
        adapter.setDropDownViewResource
                (android.R.layout.simple_list_item_single_choice);
        //Thiết lập adapter cho Spinner
        spinnerOption.setAdapter(adapter);
        spinnerOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tvtitleOption.setText(arr[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        /*set list view*/
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rcvListCard.setLayoutManager(linearLayoutManager);
        cardStatisticAdapter = new CardStatisticAdapter(getContext(), listIdCard, jsonListCard, new IOnItemClickedListener() {
            @Override
            public void onItemClicked(int position, View view) {

            }
        });
        rcvListCard.setAdapter(cardStatisticAdapter);
    }

    @Override
    protected void getData() {
        btnSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_search:
                runPostDelay("Đang tìm kiếm danh sách thẻ, vui lòng đợi ...");
                String status = "";
                if(tvtitleOption.getText().toString().equals("Thẻ chưa gửi sang PaymentCard")){

                }else if(tvtitleOption.getText().toString().equals("Thẻ chưa gửi sang Game")){

                }else if(tvtitleOption.getText().toString().equals("Thẻ chưa nạp")){
                    status = "CHUA+GIU";
                }
                getListCard(GlobalValue.jsonSimSelected.optString("serial_number"), status, 1, 20);
                break;
        }
    }

    /*GET DS CARD*/
    public void getListCard(String simNumber, String status, int page, int limit){
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("simNumber", simNumber);
            jsonObject.put("status", status);
            jsonObject.put("page", page);
            jsonObject.put("limit", limit);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("inForSIm", "JSON OBJECT: " + jsonObject.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://192.168.0.111:8080/getcardsbystatus", jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        closeHandle();
                        Log.d("inForSIm", "SUCCESS - JSON DS CARD===>>>>>: " + response.toString());
                        boolean status = response.optBoolean("s");
                        if(status == true){
                            if(response.optJSONObject("res") != null){

                            }
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