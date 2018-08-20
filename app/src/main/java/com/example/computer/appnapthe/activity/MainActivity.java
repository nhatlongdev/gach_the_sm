package com.example.computer.appnapthe.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.computer.appnapthe.R;
import com.example.computer.appnapthe.configs.Apis;
import com.example.computer.appnapthe.configs.Constant;
import com.example.computer.appnapthe.configs.GlobalValue;
import com.example.computer.appnapthe.datastore.DataStoreManager;
import com.example.computer.appnapthe.fragment.AdminFragment;
import com.example.computer.appnapthe.fragment.SearchFragment;
import com.example.computer.appnapthe.fragment.StatisticFragment;
import com.example.computer.appnapthe.fragment.GuiTheFragment;
import com.example.computer.appnapthe.fragment.NapTheFragment;
import com.example.computer.appnapthe.fragment.SettingFragment;
import com.example.computer.appnapthe.modelmanager.RequestQueueSingleton;
import com.example.computer.appnapthe.util.SetupHelper;
import com.example.computer.appnapthe.writefile.WriteFile;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.microedition.khronos.opengles.GL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public boolean showBack = false;
    public ImageView imgBack, imgSelectSim1, imgSelectSim2;
    public LinearLayout liTop, liSim1, liSim2, liParentSelectSim1, liParentSelectSim2;
    public FragmentManager fragmentManager;
    public FragmentTransaction fragmentTransaction;
    public Fragment fragment = null;
    public int simSelected = 0;
    public TextView tvNetSim1, tvNetSim2, tvMoneySim1, tvMoneySim2, tvNumSim1, tvNumSim2, tvDateSim1, tvDateSim2, tvResult, tvHintSim1, tvHintSim2, tvCountCardNew;
    public Dialog dialog_permisstion, dialogSwitchNet, dialogSwitchMode;
    public Button btnSwitchModeNap, btnNapThe;
    public RelativeLayout rltDialogPhoneSim1, rltDialogPhoneSim2;
    public LinearLayout rltParent, liParentHeaderSim;
    public String providerIDSim1 = "", providerIDSim2 = "";
    public String hintSim1 = "", hintSim2 = "";
    public int slotSim = 0, countSimNew = 0;
    private Animation shake;
    private RelativeLayout rltBell;
    private Timer timer, timerNapAuto;
    public RequestQueue requestQueue;
    private int soLanCardGiongNhauLonHonKhongLienTiep = 0;

    // lưu log gửi đi nhận về khi gọi 101,100
    public String logApp = "";

    /*thang hien tai*/
    private String monthCurrent = "";

    /*QUEUE CARD*/
    public boolean isProgressing = false;
    public List<String> listCard;

    // key card current
    public String keyCardCurrent = "";

    //SO LAN LIEN TIEP UPDATE CARD LEN SERVER SAI
    public int countCheckUpdateFailse = 0;

    //OBJ LƯU CARD LẤY GET TỪ SERVER
    public JSONObject jsonCard = null;
    public int tkChinhTruoc =0, tkChinhSau = 0, valueCode = 101;
    public String msg_101 = "";

    /*json sim nap hiện tại*/
    public  JSONObject jsonCakeNapTheoSim =  null;
    public int countCardLoadedFailse = 0;

    /*bien kiem tra truong hop goi *101# hoac *100 tu app hay dien thoai*/
    public boolean checkCallNapTheTuApp = false;

    public String key_sim_nap ="", key_ngay = "", key_month = "";

    //biến kiểm soát trường hợp lấy card về mà đã có trong cake thì delay 10s mới xử lý
    public int delay_10s = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        listCard = new ArrayList<>();
        requestQueue = RequestQueueSingleton.getInstance(this).getRequestQueue();
        DateFormat df1=new SimpleDateFormat("MMyyyy");//foramt date
        monthCurrent = df1.format(Calendar.getInstance().getTime());
        tvResult = findViewById(R.id.tv_result);
        imgBack = findViewById(R.id.img_back);
        liTop = findViewById(R.id.rlt_top);
        liTop.removeView(imgBack);
        liSim1 = findViewById(R.id.li_sim1);
        liSim2 = findViewById(R.id.li_sim2);
        liParentHeaderSim = findViewById(R.id.li_parent_header_sim);
        tvNetSim1 = findViewById(R.id.tv_network_sim1);
        tvNetSim2 = findViewById(R.id.tv_network_sim2);
        tvMoneySim1 = findViewById(R.id.tv_money_sim1);
        tvMoneySim2 = findViewById(R.id.tv_money_sim2);
        tvNumSim1 = findViewById(R.id.tv_number_sim1);
        tvNumSim2 = findViewById(R.id.tv_number_sim2);
        tvDateSim1 = findViewById(R.id.tv_date_sim1);
        tvDateSim2 = findViewById(R.id.tv_date_sim2);
        tvCountCardNew = findViewById(R.id.tv_count_card_new);
        liParentSelectSim1 = findViewById(R.id.li_parent_selected_sim1);
        liParentSelectSim2 = findViewById(R.id.li_parent_selected_sim2);
        imgSelectSim1 = findViewById(R.id.img_check_sim1);
        imgSelectSim2 = findViewById(R.id.img_check_sim2);
        liParentSelectSim2.removeView(imgSelectSim2);
        rltBell = findViewById(R.id.rlt_bell);
        btnSwitchModeNap = findViewById(R.id.btn_switch_mode_nap);
        btnNapThe = findViewById(R.id.btn_nap_the);

        imgBack.setOnClickListener(this);
        liSim1.setOnClickListener(this);
        liSim2.setOnClickListener(this);
        rltBell.setOnClickListener(this);
        btnSwitchModeNap.setOnClickListener(this);
        showBack = false;
        shake = AnimationUtils.loadAnimation(this, R.anim.anim_shake_bell);
        timer = new Timer();
        timerNapAuto = new Timer();

        /*kiem tra cache co du lieu ko, neu co thi lay ra, chua co thi tao moi*/
        /*cache sim*/
        if(DataStoreManager.getCacheJsonSim() != null && !DataStoreManager.getCacheJsonSim().equals("")){
            try {
                GlobalValue.jsonDataSimCake = new JSONObject(DataStoreManager.getCacheJsonSim());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            /*Doc file cache trong sdcard*/
//            String resultCacheSdCard = "";
//            resultCacheSdCard = WriteFile.readData(Constant.NAME_FILE_LIST_SIM);
//            if(!resultCacheSdCard.equals("") && resultCacheSdCard != null){
//                try {
//                    JSONObject jsonTest = new JSONObject(resultCacheSdCard.toString());
//                    Log.d("inForSIm", "TEST READ CACHE FILE: " + jsonTest.toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }

            GlobalValue.jsonDataSimCake = new JSONObject();
        }

        /*cahe thong tin nap(so lan nap sai lien tiep, tong so tien nap .......) theo sim*/
        if(DataStoreManager.getInforNapTheTheoSim() != null && !DataStoreManager.getInforNapTheTheoSim().equals("")){
            try {

                GlobalValue.jsonNapCake = new JSONObject(DataStoreManager.getInforNapTheTheoSim().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            GlobalValue.jsonNapCake = new JSONObject();
        }

        /*cache thong tin the da nap theo sim*/
        if(DataStoreManager.getCardNapTheoSim() != null && !DataStoreManager.getCardNapTheoSim().equals("")){
            try {
                GlobalValue.jsonDataCardLoaded = new JSONObject(DataStoreManager.getCardNapTheoSim().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            GlobalValue.jsonDataCardLoaded = new JSONObject();
        }

        /*lay cache suggest card neu co*/
        Log.d("inForSIm", "Data list suggest: " + DataStoreManager.getListSuggestCard(MainActivity.this).size());
        if(DataStoreManager.getListSuggestCard(MainActivity.this) != null && DataStoreManager.getListSuggestCard(MainActivity.this).size() >0){
            GlobalValue.arrSuggestCodeCard = DataStoreManager.getListSuggestCard(MainActivity.this);
        }else {
            GlobalValue.arrSuggestCodeCard = new ArrayList<>();
        }

        /*lay cache suggest code customer neu co*/
        Log.d("inForSIm", "Data list suggest customer: " + DataStoreManager.getListSuggestCodeCustomer(MainActivity.this).size());
        if(DataStoreManager.getListSuggestCodeCustomer(MainActivity.this) != null && DataStoreManager.getListSuggestCodeCustomer(MainActivity.this).size() >0){
            GlobalValue.arrSuggestCustomer = DataStoreManager.getListSuggestCodeCustomer(MainActivity.this);
        }else {
            GlobalValue.arrSuggestCustomer = new ArrayList<>();
        }



        /*get infor sim, phone*/
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 109);
        } else {
            //TODO
            detectSimAndCreateJsonData();
        }

        /*kiểm tra cake chế độ nạp đã có chưa nếu chưa thì set mặc định là nap tay*/
        DataStoreManager.saveCheDoNap("0");
        btnSwitchModeNap.setText("Chuyển chế độ nạp thẻ tự động");

        /*set bg cho btn nap the*/
        setBackgroundBtnNapThe(DataStoreManager.getCheDoNap());

        /*on Broastcash*/
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessgeResultTextAuto, new IntentFilter("RESULT_TEXT_AUTO"));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 109:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //TODO
                    detectSimAndCreateJsonData();
                }
                break;
            default:
                break;
        }
    }


    //ham set giao diện cho btn nạp thẻ
    public void setBackgroundBtnNapThe(String mode){
        if(mode.equals("0")){
            btnNapThe.setBackgroundResource(R.drawable.press_button_color);
            btnNapThe.setTextColor(Color.parseColor("#ffffff"));
        }else {
            btnNapThe.setBackgroundResource(R.drawable.press_button_disable);
            btnNapThe.setTextColor(Color.parseColor("#9E9E9E"));
        }
    }

    /*detect sim and json */
    public void detectSimAndCreateJsonData(){
        TelephonyManager telemamanger = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        GlobalValue.deviceId = telemamanger.getDeviceId();
        GlobalValue.jsonDataSim = new JSONArray();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1){
            List<SubscriptionInfo> subscription = SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoList();
            JSONObject jsonSim = new JSONObject();
            for (int i = 0; i < subscription.size(); i++) {
                SubscriptionInfo info = subscription.get(i);
                // get serial number
                String serialNumber = info.getIccId();
                // get line number
                String lineNumber = info.getNumber();
                Log.d("inForSIm", "SerialNumber_SubscriptionInfo: " + serialNumber);
                Log.d("inForSIm", "Number_SubscriptionInfo: " + lineNumber);
                Log.d("inForSIm", "Name_SubscriptionInfo: " + info.getDisplayName());
                Log.d("inForSIm", "Nha mang _SubscriptionInfo: " + info.getCarrierName());
                Log.d("inForSIm", "country iso_SubscriptionInfo: " + info.getCountryIso());
                JSONObject jsonSim1 = new JSONObject();
                try {
                    jsonSim1.put("serial_number",serialNumber);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(i == 0){
                    if(info.getCarrierName().toString().toLowerCase().contains("Viettel".toLowerCase())){
                        providerIDSim1 = "VTEL";
                        try {
                            jsonSim1.put("code","VTEL");
                            jsonSim1.put("name","VIETTEL");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if(info.getCarrierName().toString().toLowerCase().contains("VINAPHONE".toLowerCase())){
                        providerIDSim1 = "GPC";
                        try {
                            jsonSim1.put("code","GPC");
                            jsonSim1.put("name","VINAPHONE");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if(info.getCarrierName().toString().toLowerCase().contains("MOBIPHONE".toLowerCase())){
                        providerIDSim1 = "VMS";
                        try {
                            jsonSim1.put("code","VMS");
                            jsonSim1.put("name","MOBIPHONE");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        jsonSim.put("sim1",jsonSim1);
                        tvNetSim1.setText(jsonSim1.optString("name"));
                        tvNumSim1.setText(jsonSim1.optString("serial_number"));
                        hintSim1 = "Số điện thoại - sim1 - " + jsonSim1.optString("name");
                        DateFormat df1=new SimpleDateFormat("MM/yyyy");//foramt date
                        String date=df1.format(Calendar.getInstance().getTime());
                        tvDateSim1.setText(date);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    if(info.getCarrierName().toString().toLowerCase().contains("Viettel".toLowerCase())){
                        providerIDSim2 = "VTEL";
                        try {
                            jsonSim1.put("code","VTEL");
                            jsonSim1.put("name","VIETTEL");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if(info.getCarrierName().toString().toLowerCase().contains("VINAPHONE".toLowerCase())){
                        providerIDSim2 = "GPC";
                        try {
                            jsonSim1.put("code","GPC");
                            jsonSim1.put("name","VINAPHONE");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if(info.getCarrierName().toString().toLowerCase().contains("MOBIPHONE".toLowerCase())){
                        providerIDSim2 = "VMS";
                        try {
                            jsonSim1.put("code","VMS");
                            jsonSim1.put("name","MOBIPHONE");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        jsonSim.put("sim2",jsonSim1);
                        tvNetSim2.setText(jsonSim1.optString("name"));
                        tvNumSim2.setText(jsonSim1.optString("serial_number"));
                        hintSim2 = "Số điện thoại - sim2 - " + jsonSim1.optString("name");
                        DateFormat df1=new SimpleDateFormat("MM/yyyy");//foramt date
                        String date=df1.format(Calendar.getInstance().getTime());
                        tvDateSim2.setText(date);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                GlobalValue.jsonDataSim.put(jsonSim1);

            }
        }else {
            liParentHeaderSim.removeView(liSim2);
            JSONObject jsonSim = new JSONObject();
            Log.d("inForSIm", " Dattttaaaaaaaaaaaaaaaaa: " + telemamanger.getNetworkCountryIso());
            Log.d("inForSIm", " Dattttaaaaaaaaaaaaaaaaa: " + telemamanger.getNetworkOperatorName());
            Log.d("inForSIm", " Dattttaaaaaaaaaaaaaaaaa: " + telemamanger.getNetworkOperator());
            Log.d("inForSIm", " Dattttaaaaaaaaaaaaaaaaa: " + telemamanger.getSimSerialNumber());
            try {
                jsonSim.put("serial_number",telemamanger.getSimSerialNumber());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(telemamanger.getNetworkOperatorName().toString().toLowerCase().contains("Viettel".toLowerCase())){
                providerIDSim2 = "VTEL";
                try {
                    jsonSim.put("code","VTEL");
                    jsonSim.put("name","VIETTEL");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if(telemamanger.getNetworkOperatorName().toString().toLowerCase().contains("VINAPHONE".toLowerCase())){
                providerIDSim2 = "GPC";
                try {
                    jsonSim.put("code","GPC");
                    jsonSim.put("name","VINAPHONE");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if(telemamanger.getNetworkOperatorName().toString().toLowerCase().contains("MOBIPHONE".toLowerCase())){
                providerIDSim2 = "VMS";
                try {
                    jsonSim.put("code","VMS");
                    jsonSim.put("name","MOBIPHONE");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            tvNetSim1.setText(jsonSim.optString("name"));
            tvNumSim1.setText(jsonSim.optString("serial_number"));
            hintSim1 = "Số điện thoại - sim1 - " + jsonSim.optString("name");
            DateFormat df1=new SimpleDateFormat("MM/yyyy");//foramt date
            String date=df1.format(Calendar.getInstance().getTime());
            tvDateSim1.setText(date);
            GlobalValue.jsonDataSim.put(jsonSim);
        }

        /*code chinh*/
        if(GlobalValue.jsonDataSim.length() == 1){
            liParentHeaderSim.removeView(liSim2);
        }
        GlobalValue.providerId = providerIDSim1;
        if(GlobalValue.jsonDataSim.length()>0) {
            Log.d("inForSIm","Gia tri JSON DATA: " +  GlobalValue.jsonDataSim.toString());
            countSimNew = 0;
            for (int i=0; i<GlobalValue.jsonDataSim.length(); i++){
                String key = GlobalValue.jsonDataSim.optJSONObject(i).optString("serial_number");
                if(GlobalValue.jsonDataSimCake.optJSONObject(key) == null){ /*sim chua co trong cake*/
                    Log.d("inForSIm", "TH SIM KO CO TRONG CACHE");
                    try {
                        GlobalValue.jsonDataSim.optJSONObject(i).put("total_money_month" +  "_" + monthCurrent,0);
                        GlobalValue.jsonDataSimCake.put(key, GlobalValue.jsonDataSim.optJSONObject(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else { /*sim da co trong cake*/
                    Log.d("inForSIm", "TH SIM CO TRONG CACHE-CACHE: " + GlobalValue.jsonDataSimCake.toString());
                    try {
                        GlobalValue.jsonDataSim.optJSONObject(i).put("total_money_month" +  "_" + monthCurrent,
                                GlobalValue.jsonDataSimCake.optJSONObject(key).optInt("total_money_month" +  "_" + monthCurrent));
                        Log.d("inForSIm", "GIA TRI GlobalValue.jsonDataSim.optJSONObject(i) : " + GlobalValue.jsonDataSim.optJSONObject(i).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(i==0){
                    tvNumSim1.setText(GlobalValue.jsonDataSim.optJSONObject(i).optString("serial_number"));
                    GlobalValue.jsonSimSelected = GlobalValue.jsonDataSim.optJSONObject(i);
                    final NumberFormat formatter = new DecimalFormat("###,###,###.##");
                    String menhGiaThe = formatter.format(GlobalValue.jsonSimSelected.optInt("total_money_month" +  "_" + monthCurrent));
                    tvMoneySim1.setText(menhGiaThe);
                    Log.d("inForSIm", "DT: " + GlobalValue.jsonDataSim.optJSONObject(i).toString());
                    Log.d("inForSIm", "DT11: " + GlobalValue.jsonSimSelected.toString());
                    Log.d("inForSIm", "START TIMER");
                    /*bat dau chay timer lay count card*/
                    startTimer();
                }else if(i == 1){
                    tvNumSim2.setText(GlobalValue.jsonDataSim.optJSONObject(i).optString("serial_number"));
                }
            }

            /*save cache data sim*/
            DataStoreManager.saveCacheJsonSim(GlobalValue.jsonDataSimCake.toString());

            try {
                JSONObject jsonTest = new JSONObject(DataStoreManager.getCacheJsonSim());
                Log.d("inForSIm", "TEST -====================>>> " + jsonTest.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("inForSIm", "CACHE -====================>>> " + DataStoreManager.getCacheJsonSim());
            try{
//                WriteFile.saveData(this,"listSim.json",DataStoreManager.getCacheJsonSim().toString());
//                WriteFile.progressWriteFile(this, Constant.NAME_FILE_LIST_SIM,DataStoreManager.getCacheJsonSim().toString());
            }catch (Exception e){

            }

        }

        /*lay cake json nap theo sim*/
        key_sim_nap = GlobalValue.jsonSimSelected.optString("code") + "_" + GlobalValue.jsonSimSelected.optString("serial_number")+ "_nap";
        Date date_ngay = new Date();
        String strDateFormat_ = "ddMMyyyy";
        /*hhhhhh*/
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(strDateFormat_);
        key_ngay = "tong_so_lan_nap_sai_trong_ngay_" + simpleDateFormat.format(date_ngay);

        Date date_thang = new Date();
        String strMonthFormat = "MMyyyy";
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(strMonthFormat);
        key_month = "tong_so_tien_nap_trong_thang_" + simpleDateFormat1.format(date_thang);

        /*tao du lieu cake nap*/
        if(GlobalValue.jsonNapCake.optJSONObject(key_sim_nap) != null){
            try {
                /*lay ra mot card da luu trong cache*/
                jsonCakeNapTheoSim = new JSONObject(GlobalValue.jsonNapCake.optJSONObject(key_sim_nap).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            jsonCakeNapTheoSim = new JSONObject();
            try {
                jsonCakeNapTheoSim.put("so_the_sai_lien_tiep",0);
                jsonCakeNapTheoSim.put(key_ngay,0);
                jsonCakeNapTheoSim.put("tong_so_lan_nap_sai",0);
                jsonCakeNapTheoSim.put(key_month,0);
                jsonCakeNapTheoSim.put("tong_so_tien_nap",0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*add fragment vao activity*/
    public void AddFragment(View view){
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        boolean check = false;
        switch (view.getId()){
            case R.id.btn_gui_the:
                fragment = new GuiTheFragment();
                if(showBack == false){
                    liTop.addView(imgBack,0);
                    showBack = true;
                }
                break;
            case R.id.btn_nap_the:
                if(DataStoreManager.getCheDoNap().equals("0")){
                    fragment = new NapTheFragment();
                    if(showBack == false){
                        liTop.addView(imgBack,0);
                        showBack = true;
                    }
                }else {
                    check = true;
                }
                break;
            case R.id.btn_account:
                fragment = new StatisticFragment();
                if(showBack == false){
                    liTop.addView(imgBack,0);
                    showBack = true;
                }
                break;
            case R.id.btn_setting:
                fragment = new SettingFragment();
                if(showBack == false){
                    liTop.addView(imgBack,0);
                    showBack = true;
                }
                break;
            case R.id.btn_search:
                fragment = new SearchFragment();
                if(showBack == false){
                    liTop.addView(imgBack,0);
                    showBack = true;
                }
                break;
            case R.id.btn_admin:
                fragment = new AdminFragment();
                if(showBack == false){
                    liTop.addView(imgBack,0);
                    showBack = true;
                }
                break;
        }
        if(check == false){
            fragmentTransaction.replace(R.id.fr_layout, fragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d("inForSIm", "APP KILLED1");
        if(MainActivity.this.isFinishing()){
            //do your stuff here
            Log.d("inForSIm", "APP KILLED");
            timerNapAuto.cancel();
            timer.cancel();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back:
                hideKeyboard(this);
                if (fragment != null) {
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
                if(showBack == true){
                    liTop.removeView(imgBack);
                    showBack = false;
                }
                break;
            case R.id.li_sim1:
                hideKeyboard(this);
                if(simSelected != 0){
                    switchNet(0);
                }
                break;
            case R.id.li_sim2:
                hideKeyboard(this);
                if(simSelected != 1){
                    switchNet(1);
                }
                break;
            case R.id.rlt_bell:
                if(DataStoreManager.getCheDoNap().equals("0")){
                    fragmentManager = getSupportFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragment = new NapTheFragment();
                    if(showBack == false){
                        liTop.addView(imgBack,0);
                        showBack = true;
                    }
                    fragmentTransaction.replace(R.id.fr_layout, fragment);
                    fragmentTransaction.commit();
                }
                break;
            case R.id.btn_switch_mode_nap:
                if(checkAvailability() == true){
                    dialogSwitchMode = new Dialog(this);
                    dialogSwitchMode.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialogSwitchMode.setContentView(R.layout.dialog_switch_mode_nap);
                    final Button btnYes_the_loi, btnNo_huy_bo;
                    btnYes_the_loi = dialogSwitchMode.findViewById(R.id.btn_yes);
                    btnNo_huy_bo = dialogSwitchMode.findViewById(R.id.btn_no);
                    TextView tvMsg = dialogSwitchMode.findViewById(R.id.tv_msg);
                    if(DataStoreManager.getCheDoNap().equals("0")){
                        tvMsg.setText("Bạn có chắc chắn muốn chuyển sang chế độ nạp thẻ tự động không ?");
                    }else {
                        tvMsg.setText("Bạn có chắc chắn muốn chuyển sang chế độ nạp thẻ bằng tay không ?");
                    }
                    btnYes_the_loi.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            GlobalValue.isUpdateToServer = false;
                            if(DataStoreManager.getCheDoNap().equals("0")){
                                DataStoreManager.saveCheDoNap("1");
                                btnSwitchModeNap.setText("Chuyển chế độ nạp thẻ bằng tay");
                                tvResult.setText("Hệ thống đang ở chế độ gạch thẻ tự động");
                                //run process nap tu dong
                                progressAutoPayCard();
                            }else {
                                DataStoreManager.saveCheDoNap("0");
                                btnSwitchModeNap.setText("Chuyển chế độ nạp thẻ tự động");
                                tvResult.setText("Hệ thống đang ở chế độ gạch thẻ bằng tay");
                            }
                            /*set bg btn nap the*/
                            setBackgroundBtnNapThe(DataStoreManager.getCheDoNap());
                            dialogSwitchMode.dismiss();
                        }
                    });

                    btnNo_huy_bo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogSwitchMode.dismiss();
                        }
                    });

                    Window window_switch_mode = dialogSwitchMode.getWindow();
                    window_switch_mode.setBackgroundDrawableResource(R.color.transparent);
                    window_switch_mode.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
                    window_switch_mode.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    WindowManager.LayoutParams wmlpMsgwindowSwitchMode = dialogSwitchMode.getWindow().getAttributes();
                    wmlpMsgwindowSwitchMode.gravity = Gravity.CENTER;
                    dialogSwitchMode.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            dialogSwitchMode.dismiss();
                        }
                    });
                    dialogSwitchMode.setCancelable(false);
                    dialogSwitchMode.show();
                }else {
                    dialog_permisstion = new Dialog(this);
                    dialog_permisstion.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog_permisstion.setContentView(R.layout.dialog_accessibility);
                    Button btnYes = dialog_permisstion.findViewById(R.id.btn_yes);
                    btnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SetupHelper.openAccessibilitySettings(MainActivity.this);
                            dialog_permisstion.dismiss();
                        }
                    });
                    Window window = dialog_permisstion.getWindow();
                    window.setBackgroundDrawableResource(R.color.transparent);
                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
                    window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    WindowManager.LayoutParams wmlpMsgwindow_the_loi = dialog_permisstion.getWindow().getAttributes();
                    wmlpMsgwindow_the_loi.gravity = Gravity.CENTER;
                    dialog_permisstion.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            dialog.dismiss();
                        }
                    });
                    dialog_permisstion.setCancelable(true);
                    dialog_permisstion.show();
                }
                break;
        }
    }

    /*ham chuyen doi fragment*/
    public void switchFragment(int value){
        if(value == 1){
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragment = new SearchFragment();
            if(showBack == false){
                liTop.addView(imgBack,0);
                showBack = true;
            }
            fragmentTransaction.replace(R.id.fr_layout, fragment);
            fragmentTransaction.commit();
        }
    }

    /*hidden keyboard*/
    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View currentFocusedView = activity.getCurrentFocus();
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void switchNet(final int sim){
        dialogSwitchNet = new Dialog(this);
        dialogSwitchNet.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSwitchNet.setContentView(R.layout.dialog_msg_switch_sim);
        Button btnYes, btnNo;
        btnYes = dialogSwitchNet.findViewById(R.id.btn_yes);
        btnNo = dialogSwitchNet.findViewById(R.id.btn_no);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSwitchNet.dismiss();
            }
        });
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSwitchNet.dismiss();
                if(sim == 0){
                    simSelected = 0;
                    liParentSelectSim1.addView(imgSelectSim1,0);
                    liParentSelectSim2.removeView(imgSelectSim2);
                    tvNetSim1.setTextColor(Color.parseColor("#000088"));
                    tvMoneySim1.setTextColor(Color.parseColor("#000088"));
                    tvNumSim1.setTextColor(Color.parseColor("#000088"));
                    tvDateSim1.setTextColor(Color.parseColor("#000088"));
                    tvNetSim2.setTextColor(Color.parseColor("#ffffff"));
                    tvMoneySim2.setTextColor(Color.parseColor("#ffffff"));
                    tvNumSim2.setTextColor(Color.parseColor("#ffffff"));
                    tvDateSim2.setTextColor(Color.parseColor("#ffffff"));
                    GlobalValue.providerId = providerIDSim1;
                    JSONObject jsonObjectSim1 = GlobalValue.jsonDataSim.optJSONObject(0);
                    try {
                        GlobalValue.jsonSimSelected = new JSONObject(jsonObjectSim1.toString());
                        Log.d("inForSIm", "Json sim duoc chon: " + GlobalValue.jsonSimSelected.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    simSelected = 1;
                    liParentSelectSim2.addView(imgSelectSim2,0);
                    liParentSelectSim1.removeView(imgSelectSim1);
                    tvNetSim1.setTextColor(Color.parseColor("#ffffff"));
                    tvMoneySim1.setTextColor(Color.parseColor("#ffffff"));
                    tvNumSim1.setTextColor(Color.parseColor("#ffffff"));
                    tvDateSim1.setTextColor(Color.parseColor("#ffffff"));
                    tvNetSim2.setTextColor(Color.parseColor("#000088"));
                    tvMoneySim2.setTextColor(Color.parseColor("#000088"));
                    tvNumSim2.setTextColor(Color.parseColor("#000088"));
                    tvDateSim2.setTextColor(Color.parseColor("#000088"));
                    GlobalValue.providerId = providerIDSim2;
                    JSONObject jsonObjectSim2 = GlobalValue.jsonDataSim.optJSONObject(1);
                    try {
                        GlobalValue.jsonSimSelected = new JSONObject(jsonObjectSim2.toString());
                        Log.d("inForSIm", "Json sim duoc chon: " + GlobalValue.jsonSimSelected.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Window window = dialogSwitchNet.getWindow();
        window.setBackgroundDrawableResource(R.color.transparent);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager.LayoutParams wmlpMsgwindow = dialogSwitchNet.getWindow().getAttributes();
        wmlpMsgwindow.gravity = Gravity.CENTER;
        dialogSwitchNet.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialogSwitchNet.dismiss();
            }
        });
        dialogSwitchNet.setCancelable(true);
        dialogSwitchNet.show();
    }

    /*ham set text for bottom*/
    public void setTextForBottom(String text){
        tvResult.setText(text);
    }

    /*Chay timer get count card*/
    public void startTimer(){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(GlobalValue.jsonSimSelected != null){
                            final NumberFormat formatter = new DecimalFormat("###,###,###.##");
                            String menhGiaThe = formatter.format(GlobalValue.jsonSimSelected.optInt("total_money_month" +  "_" + monthCurrent));
                            tvMoneySim1.setText(menhGiaThe);
                        }
                    }
                });
                Log.d("test_media","Bat Dau GOi");
                getCountCard(GlobalValue.jsonSimSelected);
            }
        },1000, 10000);

    }

    /*ham get countCard*/
    public void getCountCard(JSONObject jsonSim){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("providerID", jsonSim.optString("code"));
            jsonObject.put("simNumber", jsonSim.optString("serial_number"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Log.d("inForSIm", "PArams: ==>getcount card" + jsonObject.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Apis.URL_GET_COUNT_CARD, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.d("inForSIm", "Result Get count card: " + response.toString());
                        boolean status = response.optBoolean("s");
                        if(status == true){
                            if(response.optJSONObject("res") != null){
                                int countCard = 0;
                                countCard = response.optJSONObject("res").optInt("count");

                                /*test kiem tra medi*/
                                Calendar c = Calendar.getInstance();
                                SimpleDateFormat dateformat = new SimpleDateFormat("hh:mm:ss aa");
                                String datetime = dateformat.format(c.getTime());
                                Log.d("test_media",response.toString() + " luc " + datetime + ", miliseconds: " + c.getTimeInMillis());
                                Log.d("test_media","TH1 CHUA SO SANH ben ngay if===> countCard: " + countCard + " soCardLayLanGanNhat: " + GlobalValue.soCardLayLanGanNhat);
                                if(countCard>0){
                                    Log.d("test_media","countCard >0");
                                    Log.d("test_media","TH1 CHUA SO SANH===> countCard: " + countCard + " soCardLayLanGanNhat: " + GlobalValue.soCardLayLanGanNhat);
                                    if(countCard > GlobalValue.soCardLayLanGanNhat){
                                        Log.d("test_media","TH1===> countCard: " + countCard + " soCardLayLanGanNhat: " + GlobalValue.soCardLayLanGanNhat);
                                        /*xu ly am thanh thong bao co the*/
                                        MediaPlayer player = MediaPlayer.create(getApplicationContext(), R.raw.huy_mon_huy_bill);
                                        player.start();
                                        Vibrator v = (Vibrator)getApplication().getSystemService(Context.VIBRATOR_SERVICE);
                                        // Vibrate for 1 seconds
                                        v.vibrate(1000);
                                        rltBell.startAnimation(shake);
                                    }else if(countCard == GlobalValue.soCardLayLanGanNhat){
                                        Log.d("test_media","TH2===> countCard: " + countCard + " soCardLayLanGanNhat: " + GlobalValue.soCardLayLanGanNhat);
                                        soLanCardGiongNhauLonHonKhongLienTiep = soLanCardGiongNhauLonHonKhongLienTiep + 1;
                                        if( soLanCardGiongNhauLonHonKhongLienTiep == 10){
                                            Log.d("test_media","TH2===> soLanCardGiongNhauLonHonKhongLienTiep: " + soLanCardGiongNhauLonHonKhongLienTiep);
                                            soLanCardGiongNhauLonHonKhongLienTiep = 0;
                                            /*xu ly am thanh thong bao co the*/
                                            MediaPlayer player = MediaPlayer.create(getApplicationContext(), R.raw.huy_mon_huy_bill);
                                            player.start();
                                            Vibrator v = (Vibrator)getApplication().getSystemService(Context.VIBRATOR_SERVICE);
                                            // Vibrate for 1 seconds
                                            v.vibrate(1000);
                                            rltBell.startAnimation(shake);
                                        }
                                    }
                                    tvCountCardNew.setVisibility(View.VISIBLE);
                                    tvCountCardNew.setText(countCard+"");
                                }else {
                                    tvCountCardNew.setVisibility(View.INVISIBLE);
                                }
                                GlobalValue.soCardLayLanGanNhat = countCard;
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        jsonObjectRequest.setTag("APP");
        requestQueue.add(jsonObjectRequest);
    }

    /*PROGRESS MODE AUTO PAY CARD*/
    public void progressAutoPayCard(){
        timerNapAuto.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d("inForSIm", "CAKE THE DA LOAD: " + DataStoreManager.getCardNapTheoSim());
                String key_sim_nap = GlobalValue.jsonSimSelected.optString("code") + "_" + GlobalValue.jsonSimSelected.optString("serial_number")+ "_nap";
                if(DataStoreManager.getCheDoNap().equals("1")){
                    if(GlobalValue.jsonNapCake.optJSONObject(key_sim_nap) != null && GlobalValue.jsonNapCake.optJSONObject(key_sim_nap).optInt("so_the_sai_lien_tiep")< 4){
                        if(GlobalValue.jsonNapCake.optJSONObject(key_sim_nap) != null && GlobalValue.jsonNapCake.optJSONObject(key_sim_nap).optInt("so_the_sai_lien_tiep")== 3){
                            writeLog("- Gọi getOkCard lúc ");
                            getOkCard(GlobalValue.jsonSimSelected.optString("code"), GlobalValue.jsonSimSelected.optString("serial_number"));
                        }else {
                            Log.d("inForSIm", "BẮT ĐẦU GỌI LỆNH GET CARD");
                            getCard(GlobalValue.jsonSimSelected.optString("code"), GlobalValue.jsonSimSelected.optString("serial_number"));
                        }
                    }else {
                        Log.d("inForSIm", "BẮT ĐẦU GỌI LỆNH GET CARD Lan dau tien cua sim");
                        getCard(GlobalValue.jsonSimSelected.optString("code"), GlobalValue.jsonSimSelected.optString("serial_number"));
                    }
                }
            }
        },1000, 20000);

    }

    //Hàm ghi log
    public void writeLog(String msg){
        logApp = logApp + "\n" + msg + DateFormat.getDateTimeInstance().format(new Date());
        WriteFile.progressWriteFile(this, Constant.NAME_FILE_LOG_APP,logApp);
    }

    /*QUEUE CARD*/
    public void runSendRequest(){
        Log.d("queue_tru","goi ham runSendRequest " + listCard.size());
        if(isProgressing == false && listCard.size()>0){
            isProgressing = true;
            String currentDateTimeString1 = DateFormat.getDateTimeInstance().format(new Date());
            Log.d("showItem","Thời gian show :" + currentDateTimeString1);
            List<String> listFirstElement = getFirstElementAndDeleteElementRequest(listCard);
            Log.d("queue_tru","goi ham sendRequest " + listCard.size());
            sendRequest(listFirstElement.get(0));
        }
    }

    /*truong hop doi tuong la request*/
    public List<String> getFirstElementAndDeleteElementRequest(List<String> listCard){
        List<String> listRequest = new ArrayList<>();
        listRequest.add(listCard.get(0));
        listCard.remove(0);
        return listRequest;
    }

    /*hàm send request*/
    public void sendRequest(String card){
        String currentDateTimeString2 = DateFormat.getDateTimeInstance().format(new Date());
        Log.d("log_request","gui lenh order di " + listCard.size() + " luc " + currentDateTimeString2);
        if(card!=null && !card.equals("")){
            //TO DO
            checkCallNapTheTuApp = true;
            valueCode = 101;
            callPhoneNumber(101);
        }
        if(listCard.size()>0){
            Log.d("queue_tru","add phan tu tiep de gui di " + listCard.size());
            List<String> listFirstElement = getFirstElementAndDeleteElementRequest(listCard);
            sendRequest(listFirstElement.get(0));
        }else {
            isProgressing = false;
        }
    }

    /*ham get one card */
    public void getCard(String providerID, String simNumber){
        Log.d("inForSIm", "JSON CARD CAKE: " + GlobalValue.jsonDataCardLoaded.toString() + "    ==== " + GlobalValue.jsonDataCardLoaded.length());
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("providerID", providerID);
            jsonObject.put("simNumber", simNumber);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("inForSIm", "JSON: " + jsonObject.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Apis.URL_GET_CARD, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        boolean status = response.optBoolean("s");

                        if(status == true){
                            if(response.optJSONObject("res") != null && response.optJSONObject("res").optString("cardCode")!= null){


                                GlobalValue.isGetOkCard = false;
                                GlobalValue.isUpdateToServer = false;
                                JSONObject jsonObjectRes = response.optJSONObject("res");
                                GlobalValue.cardCodeCurrent = jsonObjectRes.optString("cardCode");
                                /*tao obj card lay ve*/
                                jsonCard = new JSONObject();
                                try {
                                    keyCardCurrent = jsonObjectRes.optString("cardCode") + "_" + jsonObjectRes.optString("providerID");
                                    jsonCard.put("key",keyCardCurrent);
                                    jsonCard.put("providerID",jsonObjectRes.optString("providerID"));
                                    jsonCard.put("cardCode",jsonObjectRes.optString("cardCode"));
                                    jsonCard.put("simNumber",GlobalValue.jsonSimSelected.optString("serial_number"));
                                    jsonCard.put("ussdRes1","");
                                    jsonCard.put("comment_loaded","");
                                    jsonCard.put("comment_update","");
                                    jsonCard.put("faceValue",0);
                                    jsonCard.put("time_loaded","");
                                    jsonCard.put("ussdRes2","");
                                    jsonCard.put("updatedCount",0);
                                    jsonCard.put("status",0);
                                    jsonCard.put("time_update","");
                                    Log.d("inForSIm", "OBJ CARD GET: " + jsonCard.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Log.d("inForSIm", "OK de chay nap the====>>>>");
                                /*kiem tra xem card lay ve da co trong cake chua*/
                                if(GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent) != null){
                                    if(delay_10s <2){
                                        delay_10s = delay_10s + 1;
                                    }else {
                                        delay_10s = 0;
                                        // Thẻ đã nạp với nhà mạng nhưng chưa cập nhật lên server
                                        if(GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).optString("ussdRes2").equals("")){ // chưa gọi nạp với nhà mạng
                                            Log.d("inForSIm", "ĐÃ CÓ TRONG CAKE, TRƯỜNG USSDRES2 TRỐNG: " + GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).toString());
                                            //TO DO
                                            checkCallNapTheTuApp = true;
                                            valueCode = 101;
                                            callPhoneNumber(101);
                                        }else { // đã nạp với nhà mạng nhưng chưa update tới server--> Tiến hành update
                                            Log.d("inForSIm", "ĐÃ CÓ TRONG CAKE, TRƯỜNG USSDRES2 CÓ DỮ LIỆU: " + GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).toString());
                                            updateCardToServer(GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent));
                                        }
                                    }
                                }else {
                                    //Thẻ mới lưu vào cake
                                    try {
                                        Log.d("inForSIm", "SIZE GlobalValue.jsonDataCardLoaded TRƯỚC ADD : " + GlobalValue.jsonDataCardLoaded.length() );
                                        GlobalValue.jsonDataCardLoaded.put(keyCardCurrent, jsonCard);
                                        Log.d("inForSIm", "SIZE GlobalValue.jsonDataCardLoaded sau ADD : " + GlobalValue.jsonDataCardLoaded.length() );
                                        Log.d("inForSIm", "LOG RA OBJ CARD VUA ADD : " + GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).toString() );
                                        //Lưu vào cake
                                        DataStoreManager.saveCardNapTheoSim(GlobalValue.jsonDataCardLoaded.toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Log.d("inForSIm", "THẺ MỚI GIA TRI JSON CARD: " + jsonCard.toString() );
                                    Log.d("inForSIm", "THẺ MỚI GIA TRI GLOBAL VALUE.jsonDataCard: " + GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).toString() );
                                    //TO DO
                                    checkCallNapTheTuApp = true;
                                    valueCode = 101;
                                    callPhoneNumber(101);
                                }
                            }else {
                                Log.d("inForSIm", "KO CÓ THẺ CẦN NẠP: " + response.toString());
                                //HIỆN KHÔNG CÓ THẺ CẦN NẠP
                            }
                        }else {
                            //LỖI KHÔNG LẤY ĐƯỢC THẺ
                            Log.d("inForSIm", "LỖI KO LẤY ĐƯỢC THẺ: " + response.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("inForSIm", error.toString());
            }
        });
        jsonObjectRequest.setTag("APP");
        Log.d("inForSIm", "request: " + jsonObjectRequest.toString());
        requestQueue.add(jsonObjectRequest);
    }

    /* ham update card sau khi nap xong*/
    public void updateCardToServer(final JSONObject jsonCard){
        valueCode = 101;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("providerID", jsonCard.optString("providerID"));
            jsonObject.put("cardCode", jsonCard.optString("cardCode"));
            jsonObject.put("simNumber", jsonCard.optString("simNumber"));
            jsonObject.put("faceValue", jsonCard.optInt("faceValue"));
            if(jsonCard.optString("ussdRes1").equals("") && jsonCard.optInt("faceValue") > 0){
                jsonCard.put("ussdRes1", msg_101);
            }
            jsonObject.put("ussdRes1", jsonCard.optString("ussdRes1"));
            jsonObject.put("ussdRes2", jsonCard.optString("ussdRes2"));
            jsonObject.put("updatedCount", jsonCard.optString("updatedCount"));
            jsonObject.put("admin", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Apis.URL_UPDATE_CARD, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("inForSIm", "SUCCESS- UPDATE CARD: " + response.toString());

                        boolean status = response.optBoolean("s");
                        if(status == true){
                            //giai phong bien dem so lan update sai lien tiep
                            countCheckUpdateFailse = 0;
                            /*giải phóng 1 card trong GlobalValue.soCardLayLanGanNhat*/
                            GlobalValue.soCardLayLanGanNhat = GlobalValue.soCardLayLanGanNhat - 1;
                            GlobalValue.isUpdateToServer = true;
                            try {
                                Date date = new Date();
                                String strDateFormat = "dd/MM/yyyy hh:mm:ss ";
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(strDateFormat);
                                Log.d("inForSIm", "Date update ====: " + simpleDateFormat.format(date));
                                GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).put("time_update", simpleDateFormat.format(date));
                                GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).put("status",1);
                                Log.d("inForSIm", "Json Card cake: " + GlobalValue.jsonDataCardLoaded.toString());
                                String textStatusUpdate = "- Update: Đã cập nhật ngày " + simpleDateFormat.format(date);
                                GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).put("comment_update", textStatusUpdate);
                                //giai phong cake jsonDataCardLoaded nếu dung lượng lơn
                                if(GlobalValue.jsonDataCardLoaded.length() >500){
                                    GlobalValue.jsonDataCardLoaded = new JSONObject();
                                }
                                /*save cache*/
                                DataStoreManager.saveCardNapTheoSim(GlobalValue.jsonDataCardLoaded.toString());
                                Log.d("inForSIm", "OBJ CARD SAU KHI UPDATE ====: " + GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).toString());
                                Log.d("inForSIm", "GIA TRI VALUE CODE ====: " + valueCode);
                                Log.d("inForSIm", "JSON DS CARD ĐÃ CAKE ====: " + GlobalValue.jsonDataCardLoaded.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        jsonObjectRequest.setTag("APP");
        requestQueue.add(jsonObjectRequest);
    }


    /*GET OK CARD*/
    public void getOkCard(String providerID, String simNumber){
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("providerID", providerID);
            jsonObject.put("simNumber", simNumber);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("inForSIm", "JSON: " + jsonObject.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Apis.URL_GET_OK_CARD, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        boolean status = response.optBoolean("s");
                        if(status == true){
                            if(response.optJSONObject("res") != null && response.optJSONObject("res").optString("cardCode")!= null){
                                GlobalValue.isGetOkCard = true;
                                GlobalValue.isUpdateToServer = false;
                                JSONObject jsonObjectRes = response.optJSONObject("res");
                                GlobalValue.cardCodeCurrent = jsonObjectRes.optString("cardCode");
                                /*tao obj card lay ve*/
                                jsonCard = new JSONObject();
                                try {
                                    keyCardCurrent = jsonObjectRes.optString("cardCode") + "_" + jsonObjectRes.optString("providerID");
                                    jsonCard.put("key",keyCardCurrent);
                                    jsonCard.put("providerID",jsonObjectRes.optString("providerID"));
                                    jsonCard.put("cardCode",jsonObjectRes.optString("cardCode"));
                                    jsonCard.put("simNumber",GlobalValue.jsonSimSelected.optString("serial_number"));
                                    jsonCard.put("ussdRes1","");
                                    jsonCard.put("comment_loaded","");
                                    jsonCard.put("comment_update","");
                                    jsonCard.put("faceValue",0);
                                    jsonCard.put("time_loaded","");
                                    jsonCard.put("ussdRes2","");
                                    jsonCard.put("updatedCount",0);
                                    jsonCard.put("status",0);
                                    jsonCard.put("time_update","");
                                    Log.d("inForSIm", "OBJ CARD GET: " + jsonCard.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                /*kiem tra xem card lay ve da co trong cake chua*/
                                Log.d("inForSIm", "OK de chay nap the====>>>>");
                                /*kiem tra xem card lay ve da co trong cake chua*/
                                if(GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent) != null){
                                    if(delay_10s == 0){
                                        delay_10s = delay_10s + 1;
                                    }else {
                                        delay_10s = 0;
                                        // Thẻ đã nạp với nhà mạng nhưng chưa cập nhật lên server
                                        Log.d("inForSIm", "Thẻ này nạp rồi nhưng chưa cập nhật");
                                        if(GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).optString("ussdRes2").equals("")){ // chưa gọi nạp với nhà mạng
                                            Log.d("inForSIm", "CÓ TRONG CAKE NHƯNG CHƯA NẠP VỚI NHÀ MẠNG: " + GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).toString());
                                            //Thực hiện nạp
                                            if(jsonObjectRes.optString("cardCode")!=null && !jsonObjectRes.optString("cardCode").equals("")){
                                                //TO DO
                                                checkCallNapTheTuApp = true;
                                                valueCode = 101;
                                                callPhoneNumber(101);
                                            }
                                        }else { // đã nạp với nhà mạng nhưng chưa update tới server--> Tiến hành update
                                            updateOkCardToServer(GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent));
                                            Log.d("inForSIm", "ĐÃ NẠP VỚI NHÀ MẠNG RỒI NHƯNG CHƯA UPDATE: " + GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).toString());
                                        }
                                    }

                                }else {
                                    //Thẻ mới lưu vào cake
                                    try {
                                        GlobalValue.jsonDataCardLoaded.put(keyCardCurrent, jsonCard);
                                        //Lưu vào cake
                                        DataStoreManager.saveCardNapTheoSim(GlobalValue.jsonDataCardLoaded.toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Log.d("inForSIm", "TIEN HANH NAP THE");
                                    //TO DO
                                    checkCallNapTheTuApp = true;
                                    valueCode = 101;
                                    callPhoneNumber(101);
                                }
                            }
                        }else {
                            //LỖI KHÔNG LẤY ĐƯỢC THẺ

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //LỖI KHÔNG LẤY ĐƯỢC THẺ
            }
        });
        jsonObjectRequest.setTag("APP");
        Log.d("inForSIm", "request: " + jsonObjectRequest.toString());
        requestQueue.add(jsonObjectRequest);
    }


    /*update ok card*/
    public void updateOkCardToServer(final JSONObject jsonCard){
        valueCode = 101;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("providerID", jsonCard.optString("providerID"));
            jsonObject.put("cardCode", jsonCard.optString("cardCode"));
            jsonObject.put("simNumber", jsonCard.optString("simNumber"));
            jsonObject.put("faceValue", jsonCard.optInt("faceValue"));
            if(jsonCard.optString("ussdRes1").equals("") && jsonCard.optInt("faceValue") > 0){
                jsonCard.put("ussdRes1", msg_101);
            }
            jsonObject.put("ussdRes1", jsonCard.optString("ussdRes1"));
            jsonObject.put("ussdRes2", jsonCard.optString("ussdRes2"));
            jsonObject.put("updatedCount", jsonCard.optString("updatedCount"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Apis.URL_UPDATE_OK_CARD, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("inForSIm", "SUCCESS- UPDATE OK CARD: " + response.toString());
                        boolean status = response.optBoolean("s");
                        if(status == true){

                            GlobalValue.isUpdateToServer = true;
                            countCheckUpdateFailse = 0;
                            try {
                                Date date = new Date();
                                String strDateFormat = "dd/MM/yyyy hh:mm:ss ";
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(strDateFormat);
                                Log.d("inForSIm", "Date update ====: " + simpleDateFormat.format(date));
                                GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).put("time_update", simpleDateFormat.format(date));
                                GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).put("status",1);
                                Log.d("inForSIm", "Json Card cake: " + GlobalValue.jsonDataCardLoaded.toString());
                                String textStatusUpdate = "- Update: Đã cập nhật ngày " + simpleDateFormat.format(date);
                                GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).put("comment_update", textStatusUpdate);
                                //giai phong cake jsonDataCardLoaded nếu dung lượng lơn
                                if(GlobalValue.jsonDataCardLoaded.length() >500){
                                    GlobalValue.jsonDataCardLoaded = new JSONObject();
                                }
                                /*save cache*/
                                DataStoreManager.saveCardNapTheoSim(GlobalValue.jsonDataCardLoaded.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        jsonObjectRequest.setTag("APP");
        requestQueue.add(jsonObjectRequest);
    }

    /*Broastcash activity*/
    private BroadcastReceiver mMessgeResultTextAuto = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String text = bundle.getString("result_text_auto");
            Log.d("inForSIm", "CHECK RESULT NHAN VE tu BroadcastReceiver====>>>>>>>: " + text);
            if(text != null && !text.equals("") && checkStrinhResult(text) == true && checkCallNapTheTuApp == true){
                Log.d("inForSIm", "THOA MAN DK checkStrinhResult(text) == true==>>" + valueCode);
                if(valueCode == 101 && checkStringResult101(text) == true){
                    tkChinhTruoc = findMoney(text);
                    msg_101 = "*101#: " + text;
                    try {
                        jsonCard.put("ussdRes1","*101#: " + text);
                        Log.d("inForSIm", "GIÁ TRỊ JSON CARD SAU KHI GOI 101: " + valueCode + "--------" + jsonCard.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        DataStoreManager.saveErrorPutValeuToJson(text);
                    }
                    valueCode = 100;
                    callPhoneNumber(100);
                }else if(valueCode == 100 && checkStringResult100(text) == true){
                    checkCallNapTheTuApp = false;
                    GlobalValue.isUpdateToServer = false;
                    tkChinhSau = findMoney(text);
                    int moneyCardSuccess = tkChinhSau - tkChinhTruoc;

                    if(tkChinhSau == 0){
                        /*Truong hop the da sai, the nap da nap roi*/
                        try {
                            jsonCakeNapTheoSim.put("so_the_sai_lien_tiep", jsonCakeNapTheoSim.optInt("so_the_sai_lien_tiep") + 1);
                            jsonCakeNapTheoSim.put(key_ngay, jsonCakeNapTheoSim.optInt(key_ngay) + 1);
                            jsonCakeNapTheoSim.put("tong_so_lan_nap_sai", jsonCakeNapTheoSim.optInt("tong_so_lan_nap_sai") + 1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        countCardLoadedFailse = countCardLoadedFailse + 1;
                        Date date = new Date();
                        String strDateFormat = "dd/MM/yyyy hh:mm:ss ";
                        SimpleDateFormat simpleDateFormat_11 = new SimpleDateFormat(strDateFormat);
                        final NumberFormat formatter = new DecimalFormat("###,###,###.##");
                        String menhGiaThe = formatter.format(tkChinhSau);
                        String textStatusNap = " - Mã thẻ: " + jsonCard.optString("cardCode") + " mệnh giá " + menhGiaThe + " nạp lỗi ngày " + simpleDateFormat_11.format(date)
                                + "<br/><br/>";
                        String textStatusUpdate = "- Update: Chưa cập nhật";
                        String statusNap = "- Mệnh giá: " + "<font color=#D32F2F><b><big>Nạp sai</font></b></big>";
                        try {
                            jsonCard.put("status_nap", statusNap);
                            jsonCard.put("comment_loaded",textStatusNap);
                            jsonCard.put("menh_gia_the","Không có");
                            jsonCard.put("comment_update",textStatusUpdate);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("inForSIm", "BƯỚC NAP 100 TRA VỀ KẾT QUẢ THẺ SAI : =====>>>>" + jsonCard.toString());
                    }else {
                        /*nap the thanh cong */
                        try {
                            jsonCakeNapTheoSim.put("so_the_sai_lien_tiep", 0);
                            jsonCakeNapTheoSim.put(key_month, jsonCakeNapTheoSim.optInt(key_month) + moneyCardSuccess);
                            jsonCakeNapTheoSim.put("tong_so_tien_nap", jsonCakeNapTheoSim.optInt("tong_so_tien_nap") + moneyCardSuccess);
                            GlobalValue.jsonSimSelected.put(key_month,  jsonCakeNapTheoSim.optInt(key_month) + moneyCardSuccess );
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        countCardLoadedFailse = 0;
                        /*nap the thanh cong --> cong tien vao sim*/
                        int moneyOfMonth = GlobalValue.jsonSimSelected.optInt("total_money_month" +  "_" + monthCurrent) + moneyCardSuccess;
                        try {
                            GlobalValue.jsonSimSelected.put("total_money_month" +  "_" + monthCurrent, moneyOfMonth);
//                            GlobalValue.jsonSimSelected.put("total_money_sim_nap",GlobalValue.jsonSimSelected.optInt("total_money_sim_nap") + moneyOfMonth);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String key = GlobalValue.jsonSimSelected.optString("serial_number");
                        try {
                            GlobalValue.jsonDataSimCake.put(key, GlobalValue.jsonSimSelected);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        /*save cache sim*/
                        DataStoreManager.saveCacheJsonSim(GlobalValue.jsonDataSimCake.toString());

                        final NumberFormat formatter = new DecimalFormat("###,###,###.##");
                        String menhGiaThe = formatter.format(moneyCardSuccess);
                        Date date = new Date();
                        String strDateFormat = "dd/MM/yyyy hh:mm:ss ";
                        SimpleDateFormat simpleDateFormat_22 = new SimpleDateFormat(strDateFormat);
                        String textStatusNap = " - Mã thẻ: " + jsonCard.optString("cardCode") + " mệnh giá " + menhGiaThe +  " nạp thành công ngày " + simpleDateFormat_22.format(date)
                                + "<br/><br/>";
                        String textStatusUpdate = "- Update: Chưa cập nhật";
                        String statusNap = "- Mệnh giá: " + "<font color=#000088><b><big>" + menhGiaThe + "</font></b></big>";
                        try {
                            jsonCard.put("status_nap",statusNap);
                            jsonCard.put("comment_loaded",textStatusNap);
                            jsonCard.put("menh_gia_the",menhGiaThe);
                            jsonCard.put("comment_update",textStatusUpdate);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("inForSIm", "JSON CAKE THÔNG TIN SIM ĐÃ NẠP(SAI BAO LẦN ....): =====>>>>" + GlobalValue.jsonNapCake.toString());
                    }

                    /*save cake nap*/
                    try {
                        GlobalValue.jsonNapCake.put(key_sim_nap,jsonCakeNapTheoSim);
                        /*luu cache resference*/
                        DataStoreManager.saveInforNapTheTheoSim(GlobalValue.jsonNapCake.toString());
                        Log.d("inForSIm", "JSON CAKE THÔNG TIN SIM ĐÃ NẠP(SAI BAO LẦN ....): =====>>>>" + GlobalValue.jsonNapCake.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        Date date = new Date();
                        String strDateFormat = "dd/MM/yyyy hh:mm:ss ";
                        SimpleDateFormat simpleDateFormat_33 = new SimpleDateFormat(strDateFormat);
                        if(moneyCardSuccess >0){
                            jsonCard.put("faceValue",moneyCardSuccess);
                        }else {
                            jsonCard.put("faceValue",0);
                        }
                        jsonCard.put("time_loaded", simpleDateFormat_33.format(date));
                        jsonCard.put("ussdRes2","*100: " + text);
                        jsonCard.put("updatedCount", countCardLoadedFailse);
                        jsonCard.put("status",0);
                        GlobalValue.jsonDataCardLoaded.put(keyCardCurrent, jsonCard);
                        /*save cache*/
                        DataStoreManager.saveCardNapTheoSim(GlobalValue.jsonDataCardLoaded.toString());
                        Log.d("inForSIm", "JSON CARD SAU KHI NAP *100* TRA VE KET QUA : ====>>>" + jsonCard.toString());

                        //NẠP THẺ TỰ ĐỘNG THÀNH CÔNG TIẾN HÀNH UPDATE TO SERVER
                        if(GlobalValue.isGetOkCard == true){
                            Log.d("inForSIm", "GỌI UPDATE OK CARD TAI BROASCASD: " + jsonCard.toString());
                            updateOkCardToServer(jsonCard);
                        }else {
                            Log.d("inForSIm", "GỌI UPDATE CARD TAI BROASCASD:: " + jsonCard.toString());
                            updateCardToServer(jsonCard);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.d("inForSIm", "Data Card Cake Sau Nạp Thẻ:=====>>>> " + GlobalValue.jsonDataCardLoaded.toString());
                Log.d("inForSIm", "CO CHAY VAO DAY SS GOI 100 GlobalValue.jsonNapCake--->>" + GlobalValue.jsonNapCake.toString());
                Log.d("inForSIm", "CO CHAY VAO DAY SS GOI 100 jsonCard--->>" + jsonCard.toString());
                Log.d("inForSIm", "CO CHAY VAO DAY SS GOI 100 keyCardCurrent--->>" + keyCardCurrent);

            }
        }
    };

    /*ham check chuoi tra ve thoa man dieu kien*/
    public boolean checkStrinhResult(String text){
        if(text.contains("TK chinh=") || text.contains("Nap the thanh cong (Successful refill)") || text.contains("da duoc su dung")
                || text.contains("The nap khong dung") || text.contains("TK goc la") || text.contains("Tai khoan cua Quy khach la")
                || text.contains("so the cao khong hop le")){
            return true;
        }else {
            return false;
        }
    }

    /*ham check chuoi tra ve thoa man khi goi 101*/
    public boolean checkStringResult101(String text){
        if(text.contains("TK chinh=") || text.contains("TK goc la")){
            return true;
        }else {
            return false;
        }
    }

    /*ham check chuoi tra ve thoa man khi goi 100*/
    public boolean checkStringResult100(String text){
        if(text.contains("Nap the thanh cong (Successful refill)") || text.contains("da duoc su dung")
                || text.contains("The nap khong dung") || text.contains("Tai khoan cua Quy khach la")
                || text.contains("so the cao khong hop le")){
            return true;
        }else {
            return false;
        }
    }

    /*ham lay so tien tu text*/
    public int findMoney(String text){
        int money = 0;
        if(text != null && text != ""){
            String reg = "";
            reg = "(la|chinh=)\\s*(\\d+)";
            Pattern pat = Pattern.compile(reg);
            Matcher matcher = pat.matcher(text);
            while(matcher.find()) {
                money = Integer.parseInt(matcher.group(2));
            }
        }
        return money;
    }

    /*call phone number*/
    public void callPhoneNumber(int value)
    {
        try
        {
            if(Build.VERSION.SDK_INT > 22)
            {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 101);

                    return;
                }
                Log.d("inForSIm", "Chay vao API > 22 voi gia tri value = " + value);
                String ussdCode = "";
                if(value == 101){
                    Log.d("datax", "Chay vao day");
                    ussdCode = "*" + value + Uri.encode("#");
                }else if(value == 102){
                    ussdCode = "*" + value + Uri.encode("#");
                }else if(value == 100) {
                    ussdCode = "*" + value + "*" + GlobalValue.cardCodeCurrent + Uri.encode("#");
                }

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + ussdCode));
                callIntent.putExtra("simSlot", 0); //For sim 1
                startActivity(callIntent);

            }else {
                Log.d("inForSIm", "Chay vao API < 22 voi gia tri value = " + value);
                String ussdCode = "";
                if(value == 101){
                    ussdCode = "*" + value + Uri.encode("#");
                }else if(value == 102){
                    ussdCode = "*" + value + Uri.encode("#");
                }else if(value == 100) {
                    ussdCode = "*" + value + "*" + GlobalValue.cardCodeCurrent + Uri.encode("#");
                }

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + ussdCode));
                callIntent.putExtra("simSlot", 0); //For sim 1
                startActivity(callIntent);

            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /*ham check quyen va app trong accessibility da on chua*/
    public boolean checkAvailability() {
        if (SetupHelper.isPermissionGranted(this) && SetupHelper.isAccessibilityServiceEnabled(this)) {
            /*ok thi lam gi*/
            return true;
        } else {
            return false;
        }
    }
}
