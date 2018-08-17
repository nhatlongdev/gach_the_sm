package com.example.computer.appnapthe.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.computer.appnapthe.modelmanager.RequestManager;
import com.example.computer.appnapthe.modelmanager.RequestQueueSingleton;
import com.example.computer.appnapthe.network.ApiResponse;
import com.example.computer.appnapthe.network.BaseRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class GuiTheFragment extends BaseFragment implements View.OnClickListener{

    public RelativeLayout rltVtel, rltVina, rltMobi, rltChan, rltPhom, rltCo, rltParentGuiThe;
    public ImageView imgVtel, imgVina, imgMobi, imgChan, imgPhom, imgCo;
    public boolean checkVtelSelected = false, checkMobiSelected = false, checkVinaSelected = true
            , checkChanSelected = true, checkPhomSelected = false, checkCoSelected = false;
    public Button btnGuiThe;
    public EditText edtIdDevice;
    public AutoCompleteTextView edtMaThe, edtMaKH;
    public String ncc="", typeGame = "";
    public RequestQueue requestQueue;
    public boolean checkClickGuiThe = false;
    public Dialog dialogMsgCodeCustomerNone;

    /*------progress---*/
    private ProgressDialog progressDialog;
    /*handle*/
    private Handler handler;
    private Runnable myTask1, myTaskDelayThreeSeconds;

    public static GuiTheFragment newInstance() {
        Bundle args = new Bundle();
        GuiTheFragment fragment = new GuiTheFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutInflate() {
        return R.layout.fragment_gui_the;
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initView(View view) {
        progressDialog = new ProgressDialog(self);
        progressDialog.setCanceledOnTouchOutside(false);
        handler = new Handler();

        rltParentGuiThe = view.findViewById(R.id.rlt_parent_gui_the);
        rltVtel = view.findViewById(R.id.rlt_vtel);
        rltVina = view.findViewById(R.id.rlt_gpc);
        rltMobi = view.findViewById(R.id.rlt_vms);
        imgVina = view.findViewById(R.id.icon_click_vina);
        imgVtel = view.findViewById(R.id.icon_click_vtel);
        imgMobi = view.findViewById(R.id.icon_click_mobi);
        rltChan = view.findViewById(R.id.rlt_chan_leo_tom);
        rltPhom = view.findViewById(R.id.rlt_phom);
        rltCo = view.findViewById(R.id.rlt_co);
        imgChan = view.findViewById(R.id.icon_click_chan);
        imgPhom = view.findViewById(R.id.icon_click_phom);
        imgCo = view.findViewById(R.id.icon_click_co);
        btnGuiThe = view.findViewById(R.id.btn_gui_the);
        edtMaThe = view.findViewById(R.id.edt_ma_the);
        edtMaKH = view.findViewById(R.id.edt_ma_kh);
        edtIdDevice = view.findViewById(R.id.edt_id_device);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, GlobalValue.arrSuggestCodeCard);
        edtMaThe.setThreshold(1);
        edtMaThe.setAdapter(arrayAdapter);
        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, GlobalValue.arrSuggestCustomer);
        edtMaKH.setThreshold(1);
        edtMaKH.setAdapter(arrayAdapter1);
        ncc = "GPC";
        typeGame = "chanleotom";
        edtIdDevice.setText(GlobalValue.deviceId);
        requestQueue = RequestQueueSingleton.getInstance(getMainActivity())
                .getRequestQueue();
    }

    @Override
    protected void getData() {
        rltVtel.setOnClickListener(this);
        rltVina.setOnClickListener(this);
        rltMobi.setOnClickListener(this);
        rltChan.setOnClickListener(this);
        rltPhom.setOnClickListener(this);
        rltCo.setOnClickListener(this);
        btnGuiThe.setOnClickListener(this);

        rltParentGuiThe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMainActivity().hideKeyboard(getMainActivity());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rlt_vtel:
                getMainActivity().hideKeyboard(getMainActivity());
                imgVtel.setVisibility(View.VISIBLE);
                imgVina.setVisibility(View.INVISIBLE);
                imgMobi.setVisibility(View.INVISIBLE);
                checkVtelSelected = true;
                checkVinaSelected = false;
                checkMobiSelected = false;
                ncc = "VTEL";
                break;
            case R.id.rlt_gpc:
                getMainActivity().hideKeyboard(getMainActivity());
                imgVtel.setVisibility(View.INVISIBLE);
                imgVina.setVisibility(View.VISIBLE);
                imgMobi.setVisibility(View.INVISIBLE);
                checkVtelSelected = false;
                checkVinaSelected = true;
                checkMobiSelected = false;
                ncc = "GPC";
                break;
            case R.id.rlt_vms:
                getMainActivity().hideKeyboard(getMainActivity());
                imgVtel.setVisibility(View.INVISIBLE);
                imgVina.setVisibility(View.INVISIBLE);
                imgMobi.setVisibility(View.VISIBLE);
                checkVtelSelected = false;
                checkVinaSelected = false;
                checkMobiSelected = true;
                ncc = "VMS";
                break;
            case R.id.rlt_chan_leo_tom:
                getMainActivity().hideKeyboard(getMainActivity());
                imgChan.setVisibility(View.VISIBLE);
                imgPhom.setVisibility(View.INVISIBLE);
                imgCo.setVisibility(View.INVISIBLE);
                checkChanSelected = true;
                checkPhomSelected = false;
                checkCoSelected = false;
                typeGame = "chanleotom";
                break;
            case R.id.rlt_phom:
                getMainActivity().hideKeyboard(getMainActivity());
                imgChan.setVisibility(View.INVISIBLE);
                imgPhom.setVisibility(View.VISIBLE);
                imgCo.setVisibility(View.INVISIBLE);
                checkChanSelected = false;
                checkPhomSelected = true;
                checkCoSelected = false;
                typeGame = "phomtuoi";
                break;
            case R.id.rlt_co:
                getMainActivity().hideKeyboard(getMainActivity());
                imgChan.setVisibility(View.INVISIBLE);
                imgPhom.setVisibility(View.INVISIBLE);
                imgCo.setVisibility(View.VISIBLE);
                checkChanSelected = false;
                checkPhomSelected = false;
                checkCoSelected = true;
                typeGame = "cotuong";
                break;
            case R.id.btn_gui_the:
                getMainActivity().hideKeyboard(getMainActivity());
                if(edtMaThe.getText().toString().length() == 0){
                    getMainActivity().setTextForBottom("Lỗi gửi thẻ: Chưa nhập mã thẻ!");
                }else if(ncc.equals("VTEL") && (edtMaThe.getText().toString().length() !=13 && edtMaThe.getText().toString().length() != 15)){
                    getMainActivity().setTextForBottom("Lỗi gửi thẻ: Mã thẻ mạng Viettel phải bao gồm 13 hoặc 15 số!");
                }else if(ncc.equals("GPC") && (edtMaThe.getText().toString().length() !=12 && edtMaThe.getText().toString().length() != 14)){
                    getMainActivity().setTextForBottom("Lỗi gửi thẻ: Mã thẻ mạng Vinafone phải bao gồm 12 hoặc 14 số!");
                }else if(ncc.equals("VMS") && (edtMaThe.getText().toString().length() !=12)){
                    getMainActivity().setTextForBottom("Lỗi gửi thẻ: Mã thẻ mạng Mobifone phải bao gồm 12 số!");
                }else if(edtMaKH.getText().toString().length() == 0){
                    showDialogWhenCodeCustomerNone();
                }else if(edtMaKH.getText().toString().length() < 8){
                    getMainActivity().setTextForBottom("Lỗi gửi thẻ: Mã khách hàng không đúng\nVui lòng kiểm tra lại!");
                }else {
                    if(checkClickGuiThe == false){
                        checkClickGuiThe = true;
                        runPostDelay("Đang gửi thẻ, vui lòng đợi ...");
                        sendCardToServer(edtMaKH.getText().toString(), ncc, edtMaThe.getText().toString(), edtIdDevice.getText().toString(), typeGame);
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

                        if(GlobalValue.arrSuggestCustomer.size()>0){
                            boolean check = false;
                            for (int i=0;i<GlobalValue.arrSuggestCustomer.size(); i++){
                                if(GlobalValue.arrSuggestCustomer.get(i).equals(edtMaKH.getText().toString())){
                                    check = true;
                                }
                            }
                            if(check == false){
                                GlobalValue.arrSuggestCustomer.add(edtMaKH.getText().toString());
                            }
                        }else {
                            GlobalValue.arrSuggestCustomer.add(edtMaKH.getText().toString());
                        }
                        /*save cache */
                        DataStoreManager.saveListSuggestCard(getMainActivity(), GlobalValue.arrSuggestCodeCard);
                        DataStoreManager.saveListSuggestCodeCustomer(getMainActivity(), GlobalValue.arrSuggestCustomer);
                    }
                }
                break;
        }
    }

    /*Gui the leen server*/
    public void sendCardToServer(String acc, String ncc, String code, String deviceId, String gameName){
        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("aid", "");
            jsonObject1.put("acc", acc);
            jsonObject1.put("ncc",ncc);
            jsonObject1.put("code", code);
            jsonObject1.put("did", deviceId);
            jsonObject1.put("from","app");
            jsonObject1.put("key","app25062018OK");
            jsonObject1.put("gameName",gameName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Apis.URL_SENT_CARD, jsonObject1,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        closeHandle();
                        checkClickGuiThe = false;
                        Log.d("inForSIm", "SUCCESS: " + response.toString());
                        String msg = "";
                        boolean success = response.optBoolean("success");
                        if(success == false){
                            msg = response.optString("error");
                        }else {
                            JSONObject jsonObjectData = response.optJSONObject("data");
                            msg = jsonObjectData.optString("message");
                            edtMaThe.setText("");
                        }
                        getMainActivity().setTextForBottom(msg);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("inForSIm", error.toString());
                checkClickGuiThe = false;
            }
        });
        jsonObjectRequest.setTag("APP");
        Log.d("inForSIm", "request: " + jsonObjectRequest.toString());
        requestQueue.add(jsonObjectRequest);
    }

    /*ham show dialog thong bao da update to server 2 lan lien tiep failse*/
    public void showDialogWhenCodeCustomerNone(){
        dialogMsgCodeCustomerNone = new Dialog(getMainActivity());
        dialogMsgCodeCustomerNone.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogMsgCodeCustomerNone.setContentView(R.layout.dialog_msg_code_customer_none);
            Button btnYes, btnNo;
            TextView tvMsg;
            tvMsg = dialogMsgCodeCustomerNone.findViewById(R.id.tv_msg);
            btnYes = dialogMsgCodeCustomerNone.findViewById(R.id.btn_yes);
            btnNo = dialogMsgCodeCustomerNone.findViewById(R.id.btn_no);
            String msg = "Mã khách hàng chưa nhập\nSẽ mặc định nạp vào tài khoản ADMIN\n\nNạp vào Admin bấm [OK]\n" +
                    "Không nạp bấm [HỦY]";
            tvMsg.setText(msg);
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogMsgCodeCustomerNone.dismiss();
                    if(checkClickGuiThe == false){
                        checkClickGuiThe = true;
                        sendCardToServer("", ncc, edtMaThe.getText().toString(), edtIdDevice.getText().toString(), typeGame);
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
                    }
                }
            });
            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogMsgCodeCustomerNone.dismiss();

                }
            });
            Window window = dialogMsgCodeCustomerNone.getWindow();
            window.setBackgroundDrawableResource(R.color.transparent);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            WindowManager.LayoutParams wmlpMsgwindow = dialogMsgCodeCustomerNone.getWindow().getAttributes();
            wmlpMsgwindow.gravity = Gravity.CENTER;
            dialogMsgCodeCustomerNone.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialogMsgCodeCustomerNone.dismiss();
                    }
                });
            dialogMsgCodeCustomerNone.setCancelable(false);
            dialogMsgCodeCustomerNone.show();
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
                    checkClickGuiThe = false;
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