package com.example.computer.appnapthe.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AdminFragment extends BaseFragment implements View.OnClickListener{

    public Button btnUnlockFourToThree, btnUnlockFourToNone, btnUnblockThree;
    public Dialog dialogInputPassAdmin, dialogUnlockFourToNone;
    public int checkClickBtnUnlockFour = 0;
    public int checkClickBtnUnlockThree = 0;

    public String pass = "";
    public RelativeLayout rltParent, rltParent1;

    /*------progress---*/
    private ProgressDialog progressDialog;
    /*handle*/
    private Handler handler;
    private Runnable myTask1;

    public RequestQueue requestQueue;

    /*json sim nap*/
    public  JSONObject jsonCakeNapTheoSim =  null;


    public static AdminFragment newInstance() {
        Bundle args = new Bundle();
        AdminFragment fragment = new AdminFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutInflate() {
        return R.layout.fragment_admin;
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initView(View view) {
        progressDialog = new ProgressDialog(self);
        progressDialog.setCanceledOnTouchOutside(false);
        handler = new Handler();
        requestQueue = RequestQueueSingleton.getInstance(getMainActivity()).getRequestQueue();
        btnUnlockFourToThree = view.findViewById(R.id.btn_unblock_four_to_three);
        btnUnlockFourToNone = view.findViewById(R.id.btn_unblock_four_reset_0);
        btnUnblockThree = view.findViewById(R.id.btn_unblock_three_reset_0);
    }

    @Override
    protected void getData() {
        btnUnlockFourToThree.setOnClickListener(this);
        btnUnlockFourToNone.setOnClickListener(this);
        btnUnblockThree.setOnClickListener(this);
        if(GlobalValue.jsonSimSelected != null){
            String key_sim_nap = GlobalValue.jsonSimSelected.optString("code") + "_" + GlobalValue.jsonSimSelected.optString("serial_number")+ "_nap";
            /*tao du lieu cake nap*/
            if(GlobalValue.jsonNapCake.optJSONObject(key_sim_nap) != null){
                try {
                    jsonCakeNapTheoSim = new JSONObject(GlobalValue.jsonNapCake.optJSONObject(key_sim_nap).toString());
                    if(jsonCakeNapTheoSim.optInt("so_the_sai_lien_tiep") == 4){
                        checkClickBtnUnlockFour = 1;
                        checkClickBtnUnlockThree = 1;
                    }else if(jsonCakeNapTheoSim.optInt("so_the_sai_lien_tiep") == 3){
                        checkClickBtnUnlockThree = 1;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("inForSIm", "JSON INFOR NAP THE THEO SIM HIEN TAI: " +jsonCakeNapTheoSim.toString());
            }
        }
        updateBtnUnlockFour(checkClickBtnUnlockFour, checkClickBtnUnlockThree);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_unblock_four_to_three:
                if(checkClickBtnUnlockFour == 1){
                    updateBtnUnlockFour(0,0);
                    if(pass.equals("")){
                        showDialogCheckPass(0);
                    }else {
                        runPostDelay("Đang kiểm tra mật khẩu, vui lòng đợi ...");
                        checkPass(pass, GlobalValue.jsonSimSelected.optString("serial_number"), 0);
                    }

                }
                break;

            case R.id.btn_unblock_four_reset_0:
                if(checkClickBtnUnlockFour == 1){
                    if(pass.equals("")){
                        showDialogCheckPass(1);
                    }else {
                        showDialogUnlock();
                    }
                }
                break;

            case R.id.btn_unblock_three_reset_0:
                if(checkClickBtnUnlockThree == 1){
                    if(pass.equals("")){
                        showDialogCheckPass(2);
                    }else {
                        runPostDelay("Đang xử lý, vui lòng đợi ...");
                        unLock(pass,GlobalValue.jsonSimSelected.optString("serial_number"));
                    }
                }
                break;
        }
    }

    /*show dialog check pass*/
    public void showDialogCheckPass(final int type){
        dialogInputPassAdmin = new Dialog(getMainActivity());
        dialogInputPassAdmin.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogInputPassAdmin.setContentView(R.layout.dialog_input_pass_admin);
        RelativeLayout rltParentDialog = dialogInputPassAdmin.findViewById(R.id.rlt_parent_dialog);
        final EditText edtPass = dialogInputPassAdmin.findViewById(R.id.edt_pass);
        Button btnOk = dialogInputPassAdmin.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pass = edtPass.getText().toString();
                dialogInputPassAdmin.dismiss();
                runPostDelay("Đang kiểm tra mật khẩu, vui lòng đợi ...");
                checkPass(edtPass.getText().toString(), GlobalValue.jsonSimSelected.optString("serial_number"), type);
            }
        });

        rltParentDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInputPassAdmin.dismiss();
                updateBtnUnlockFour(1,0);
            }
        });
        Window window_the_loi = dialogInputPassAdmin.getWindow();
        window_the_loi.setBackgroundDrawableResource(R.color.transparent);
        window_the_loi.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        window_the_loi.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager.LayoutParams wmlpMsgwindow = dialogInputPassAdmin.getWindow().getAttributes();
        wmlpMsgwindow.gravity = Gravity.CENTER;
        dialogInputPassAdmin.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialogInputPassAdmin.dismiss();
            }
        });
        dialogInputPassAdmin.setCancelable(true);
        dialogInputPassAdmin.show();
    }

    /*show dialog unlock*/
    public void showDialogUnlock(){
        dialogUnlockFourToNone = new Dialog(getMainActivity());
        dialogUnlockFourToNone.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogUnlockFourToNone.setContentView(R.layout.dialog_unlock_reset_server);
        RelativeLayout rltParentDialogFourToNone = dialogUnlockFourToNone.findViewById(R.id.rlt_parent_dialog);
        rltParent = dialogUnlockFourToNone.findViewById(R.id.rlt_parent);
        rltParent1 = dialogUnlockFourToNone.findViewById(R.id.rlt_parent_1);
        TextView tvMsgContent = dialogUnlockFourToNone.findViewById(R.id.tv_title_content);
        TextView tvMsgContent1 = dialogUnlockFourToNone.findViewById(R.id.tv_title_content1);
        TextView tvResetTo0 = dialogUnlockFourToNone.findViewById(R.id.btn_reset_ve_0);
        Button btnDongY = dialogUnlockFourToNone.findViewById(R.id.btn_dong_y);
        Button btnHuyBo = dialogUnlockFourToNone.findViewById(R.id.btn_huy_bo);
        tvMsgContent.setText("Để mở khóa bạn phải thực hiện theo 2 bước sau\n\n Bước 1: Ra khỏi app tự nạp một thẻ đúng bằng tay\n\n Bước 2: Bấm nút [MỞ KHÓA] để mở khóa nạp thẻ sai 4 lần về 0");
        tvMsgContent1.setText("Bạn chắc chắn đã nạp một thẻ đúng bằng tay\nĐã nạp vui lòng bấm [Đồng ý]\nChưa nạp bấm [Hủy bỏ]");
        rltParent.setVisibility(View.VISIBLE);
        rltParent1.setVisibility(View.INVISIBLE);
        tvResetTo0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rltParent.setVisibility(View.INVISIBLE);
                rltParent1.setVisibility(View.VISIBLE);
            }
        });

        btnDongY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogUnlockFourToNone.dismiss();
                runPostDelay("Đang xử lý, vui lòng đợi ...");
                unLock(pass,GlobalValue.jsonSimSelected.optString("serial_number"));
            }
        });

        btnHuyBo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogUnlockFourToNone.dismiss();
            }
        });

        rltParentDialogFourToNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogUnlockFourToNone.dismiss();
            }
        });
        Window window_FourToNone = dialogUnlockFourToNone.getWindow();
        window_FourToNone.setBackgroundDrawableResource(R.color.transparent);
        window_FourToNone.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        window_FourToNone.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager.LayoutParams wmlpMsgwindow_four_to_none = dialogUnlockFourToNone.getWindow().getAttributes();
        wmlpMsgwindow_four_to_none.gravity = Gravity.CENTER;
        dialogUnlockFourToNone.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialogUnlockFourToNone.dismiss();
            }
        });
        dialogUnlockFourToNone.setCancelable(true);
        dialogUnlockFourToNone.show();
    }

    /*input pass admin*/
    public void checkPass(final String pass, String serialNumber, final int type){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("password", pass);
            jsonObject.put("simNumber", serialNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("inForSIm", "OBJECT REQUEST CHECK PASS: " + jsonObject.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Apis.URL_CHECK_PASS, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        closeHandle();
                        Log.d("inForSIm", "SUCCESS- cHECK PASS: " + response.toString());
                        boolean status = response.optBoolean("s");
                        if(status == true){
                            if(type == 0){
                                String key_sim_nap = GlobalValue.jsonSimSelected.optString("code") + "_" + GlobalValue.jsonSimSelected.optString("serial_number")+"_nap";
                                Log.d("inForSIm", "Key sim nap:==>> " + key_sim_nap);
                                if(GlobalValue.jsonNapCake.optJSONObject(key_sim_nap) != null){
                                    try {
                                        GlobalValue.jsonNapCake.optJSONObject(key_sim_nap).put("so_the_sai_lien_tiep",3);
                                        /*save cache*/
                                        DataStoreManager.saveInforNapTheTheoSim(GlobalValue.jsonNapCake.toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                Toast.makeText(getMainActivity(), "Mở khóa nạp sai liên tiếp 4 lần về 3 lần thành công", Toast.LENGTH_SHORT).show();
                            }else if(type == 1){
                                showDialogUnlock();
                            }else if(type == 2){
                                unLock(pass, GlobalValue.jsonSimSelected.optString("serial_number"));
                            }
                        }else {
                            Toast.makeText(getMainActivity(), "Mật khẩu Admin không đúng, vui lòng kiểm tra lại", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                closeHandle();
                Log.d("inForSIm", error.toString());
            }
        });
        jsonObjectRequest.setTag("APP");
        requestQueue.add(jsonObjectRequest);
    }

    /*UNLOCK*/
    public void unLock(String pass, String serialNumber){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("password", pass);
            jsonObject.put("simNumber", serialNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("inForSIm", "OBJECT REQUEST UNLOCK: " + jsonObject.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Apis.URL_UNLOCK, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        closeHandle();
                        Log.d("inForSIm", "SUCCESS- UNLOCK: " + response.toString());
                        boolean status = response.optBoolean("s");
                        if(status == true){
                            String key_sim_nap = GlobalValue.jsonSimSelected.optString("code") + "_" + GlobalValue.jsonSimSelected.optString("serial_number")+"_nap";
                            Log.d("inForSIm", "Key sim nap:==>> " + key_sim_nap);
                            if(GlobalValue.jsonNapCake.optJSONObject(key_sim_nap) != null){
                                try {
                                    GlobalValue.jsonNapCake.optJSONObject(key_sim_nap).put("so_the_sai_lien_tiep",0);
                                        /*save cache*/
                                    DataStoreManager.saveInforNapTheTheoSim(GlobalValue.jsonNapCake.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            Toast.makeText(self, "Mở khóa thành công", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(self, "Mở khóa lỗi, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                closeHandle();
                Log.d("inForSIm", error.toString());
            }
        });
        jsonObjectRequest.setTag("APP");
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

    /*ham update btn mo khoa nap sai 4 lan*/
    public void updateBtnUnlockFour(int value, int value1){
        if(value == 0){
            btnUnlockFourToThree.setBackgroundResource(R.drawable.press_button_disable);
            btnUnlockFourToThree.setTextColor(Color.parseColor("#9E9E9E"));
            btnUnlockFourToNone.setBackgroundResource(R.drawable.press_button_disable);
            btnUnlockFourToNone.setTextColor(Color.parseColor("#9E9E9E"));
            if(value1 == 0){
                btnUnblockThree.setBackgroundResource(R.drawable.press_button_disable);
                btnUnblockThree.setTextColor(Color.parseColor("#9E9E9E"));
            }else {
                btnUnblockThree.setBackgroundResource(R.drawable.press_button_color);
                btnUnblockThree.setTextColor(Color.parseColor("#ffffff"));
            }
        }else {
            btnUnlockFourToThree.setBackgroundResource(R.drawable.press_button_color);
            btnUnlockFourToThree.setTextColor(Color.parseColor("#ffffff"));
            btnUnlockFourToNone.setBackgroundResource(R.drawable.press_button_color);
            btnUnlockFourToNone.setTextColor(Color.parseColor("#ffffff"));
            btnUnblockThree.setBackgroundResource(R.drawable.press_button_color);
            btnUnblockThree.setTextColor(Color.parseColor("#ffffff"));
        }
    }
}