package com.example.computer.appnapthe.fragment;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
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
import com.example.computer.appnapthe.base.BaseFragment;
import com.example.computer.appnapthe.configs.Apis;
import com.example.computer.appnapthe.configs.GlobalValue;
import com.example.computer.appnapthe.datastore.DataStoreManager;
import com.example.computer.appnapthe.modelmanager.RequestQueueSingleton;
import com.example.computer.appnapthe.util.SetupHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NapTheFragment extends BaseFragment implements View.OnClickListener{

    public RequestQueue requestQueue;
    public TextView tvMaThe, tvMang, tvMaKH, tvMsgNoCard, tvResultNapThe, tvTtKh, tvSoLanNapSaiLienTiep;
    public LinearLayout liContentValueText;
    public Button btnUpdate, btnNapThe, btnReload, btnLayTheDung, btnTheNayLoi, btnTraCuu, btnTraThe;
    public int tkChinhTruoc =0, tkChinhSau = 0, valueCode = 101;
    public Dialog dialog, dialogMsgFailseTwo, dialogTheSai, dialogSoLanNapSaiLienTiep, dialogTraThe;
    public String texDislay = "";
    public int checkClickButton = -1;
    public int countCardLoadedFailse = 0;
    public String codeCard = "";
    public JSONObject jsonCard = new JSONObject();
    public String keyCardCurrent = "";
    public String keyObjectDataCakeCurrent = "";
    public int countCheckUpdateFailse = 0;
    public boolean checkClickTheSai = false, checkClickLayTheDung = false;
    public boolean checkClickBtnTraCuu = false;
    public boolean checkClickBtnTraThe = false;

    /*check da click hay chua click the sai*/
    public boolean adminClickTheSai = false;

    /*------progress---*/
    private ProgressDialog progressDialog;
    /*handle*/
    private Handler handler, handlerCallUssd;
    private Runnable myTask1, myTaskCallUssd;

    /*Animation*/
    Animation mAnimation;

    /*json sim nap*/
    public  JSONObject jsonCakeNapTheoSim =  null;

    /*bien kiem tra truong hop goi *101# hoac *100 tu app hay dien thoai*/
    public boolean checkCallNapTheTuApp = false;

    /*thang hien tai*/
    private String monthCurrent = "";

    /*bien kiem tra xem goi ussd co tra ve ket qua hay ko*/
    public boolean checkCallBackUssd = false;

    @Override
    protected int getLayoutInflate() {
        return R.layout.fragment_nap_the;
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initView(View view) {
        progressDialog = new ProgressDialog(self);
        progressDialog.setCanceledOnTouchOutside(false);
        handler = new Handler();
        handlerCallUssd = new Handler();
        requestQueue = RequestQueueSingleton.getInstance(getMainActivity()).getRequestQueue();
        DateFormat df1=new SimpleDateFormat("MMyyyy");//foramt date
        monthCurrent = df1.format(Calendar.getInstance().getTime());
        tvSoLanNapSaiLienTiep = view.findViewById(R.id.tv_so_lan_nap_sai_lien_tiep);
        tvMaThe = view.findViewById(R.id.tv_ma_the);
        tvMang = view.findViewById(R.id.tv_nha_mang);
        tvMaKH = view.findViewById(R.id.tv_ma_kh);
        tvTtKh = view.findViewById(R.id.tv_tt_kh);
        tvResultNapThe = view.findViewById(R.id.tv_result_nap_the);
        liContentValueText = view.findViewById(R.id.li_content_card);
        tvMsgNoCard = view.findViewById(R.id.tv_msg_no_card);
        btnUpdate = view.findViewById(R.id.btn_update);
        btnNapThe = view.findViewById(R.id.btn_nap_the);
        btnReload = view.findViewById(R.id.btn_reload);
        btnLayTheDung = view.findViewById(R.id.btn_lay_the_dung);
        btnTheNayLoi = view.findViewById(R.id.btn_the_nay_loi);
        btnTraCuu = view.findViewById(R.id.btn_tra_cuu);
        btnTraThe = view.findViewById(R.id.btn_tra_the);
        /*animation nhap nhay*/
        mAnimation = new AlphaAnimation(1, 0);
        mAnimation.setDuration(200);
        mAnimation.setRepeatCount(Animation.INFINITE);
        mAnimation.setRepeatMode(Animation.REVERSE);

    }

    @Override
    protected void getData() {
        Log.d("inForSIm", "Global.jsonNapCake: =====>>>" + GlobalValue.jsonNapCake.toString());
        /*kiem tra xem so lan nap sai lien tiep cua sim dang su dung*/
        String key_sim_nap = GlobalValue.jsonSimSelected.optString("code") + "_" + GlobalValue.jsonSimSelected.optString("serial_number")+"_nap";
        Log.d("inForSIm", "Key sim nap:==>> " + key_sim_nap);
        Log.d("inForSIm", "Key sim nap:==>> " + GlobalValue.jsonNapCake.optJSONObject(key_sim_nap.toString()) + "-----" + GlobalValue.isUpdateToServer);
        if(GlobalValue.jsonNapCake.optJSONObject(key_sim_nap) != null){
            if(GlobalValue.jsonNapCake.optJSONObject(key_sim_nap).optInt("so_the_sai_lien_tiep") ==3){
                checkClickButton = -1;
                showHiddenButton(checkClickButton);
                btnLayTheDung.startAnimation(mAnimation);
                showHiddenButtonLayTheDung(false);
                showHiddenButtonTheSai(true);
                updateStatusBtnTraThe(false);
                updateStatusBtnTraCuu(false);
                liContentValueText.setVisibility(View.INVISIBLE);
                tvMsgNoCard.setVisibility(View.VISIBLE);
                tvMsgNoCard.setText("Cảnh báo!!!\nBạn đã nạp sai liên tiếp " + GlobalValue.jsonNapCake.optJSONObject(key_sim_nap).optInt("so_the_sai_lien_tiep") + " lần\n" +
                        "Vui lòng bấm [LẤY THẺ ĐÚNG] để lấy mã thẻ chính xác!");
            }else if(GlobalValue.jsonNapCake.optJSONObject(key_sim_nap).optInt("so_the_sai_lien_tiep") >= 4){
                liContentValueText.setVisibility(View.INVISIBLE);
                tvMsgNoCard.setVisibility(View.VISIBLE);
                checkClickButton = -1;
                showHiddenButton(checkClickButton);
                showHiddenButtonLayTheDung(true);
                showHiddenButtonTheSai(true);
                updateStatusBtnTraCuu(false);
                updateStatusBtnTraThe(false);
                tvMsgNoCard.setText("Cảnh báo!!!\nTài khoản của bạn đã bị khóa do nạp sai quá số lần quy định, Vui lòng liên hệ Admin để được mở khóa");
            }else {
                /*get card*/
//                runPostDelay("Đang lấy mã thẻ, vui lòng đợi ...", 0);
//                getCard(GlobalValue.jsonSimSelected.optString("code"), GlobalValue.jsonSimSelected.optString("serial_number"));
                showHiddenButtonLayTheDung(true);
                showHiddenButtonTheSai(true);
                updateStatusBtnTraCuu(false);
                updateStatusBtnTraThe(false);
                checkClickButton = 2;
                showHiddenButton(checkClickButton);
            }
        }else {
            /*get card*/
//            runPostDelay("Đang lấy mã thẻ, vui lòng đợi ...", 0);
//            getCard(GlobalValue.jsonSimSelected.optString("code"), GlobalValue.jsonSimSelected.optString("serial_number"));
            showHiddenButtonLayTheDung(true);
            showHiddenButtonTheSai(true);
            updateStatusBtnTraCuu(false);
            updateStatusBtnTraThe(false);
            checkClickButton = 2;
            showHiddenButton(checkClickButton);
        }

        /*on Broastcash close activity main*/
        LocalBroadcastManager.getInstance(getMainActivity()).registerReceiver(
                mMessgeResultText, new IntentFilter("RESULT_TEXT"));
        btnNapThe.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnReload.setOnClickListener(this);
        btnTheNayLoi.setOnClickListener(this);
        btnLayTheDung.setOnClickListener(this);
        btnTraCuu.setOnClickListener(this);
        tvSoLanNapSaiLienTiep.setOnClickListener(this);
        btnTraThe.setOnClickListener(this);

        /*set trang thai textview hien thi so lan nap sai lien tiep*/
        if(GlobalValue.jsonSimSelected != null && GlobalValue.jsonSimSelected.optString("code")!=null){
            String key_sim_nap_current = GlobalValue.jsonSimSelected.optString("code") + "_" + GlobalValue.jsonSimSelected.optString("serial_number")+ "_nap";
            if(GlobalValue.jsonNapCake.optJSONObject(key_sim_nap_current) != null && GlobalValue.jsonNapCake.optJSONObject(key_sim_nap_current).optInt("so_the_sai_lien_tiep") > 0){
                tvSoLanNapSaiLienTiep.setVisibility(View.VISIBLE);
                tvSoLanNapSaiLienTiep.setText(GlobalValue.jsonNapCake.optJSONObject(key_sim_nap_current).optInt("so_the_sai_lien_tiep") + "");
                if(GlobalValue.jsonNapCake.optJSONObject(key_sim_nap_current).optInt("so_the_sai_lien_tiep") >= 3){
                    tvSoLanNapSaiLienTiep.startAnimation(mAnimation);
                }
            }
        }
    }

    /*Broastcash activity*/
    private BroadcastReceiver mMessgeResultText = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String text = bundle.getString("result_text");
            Log.d("inForSIm", "CHECK RESULT NHAN VE tu BroadcastReceiver====>>>>>>>: " + text);
            if(text != null && !text.equals("") && checkStrinhResult(text) == true && checkCallNapTheTuApp == true){
                Log.d("inForSIm", "THOA MAN DK checkStrinhResult(text) == true");
                if(valueCode == 101 && checkStringResult101(text) == true){
                    texDislay = "";
                    showHiddenButton(checkClickButton);
                    tkChinhTruoc = findMoney(text);
                    texDislay ="- *101#: " + text + "<br/><br/>";
                    try {
                        jsonCard.put("ussdRes1","*101#: " + text);
                        Log.d("inForSIm", "OBJ CARD Sau khi goi *101: voi value la: " + valueCode + "--------" + jsonCard.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else if(valueCode == 100 && checkStringResult100(text) == true){
                    checkCallNapTheTuApp = false;
                    GlobalValue.isUpdateToServer = false;
                    checkClickButton = 1;
                    showHiddenButton(checkClickButton);
                    tkChinhSau = findMoney(text);
                    int moneyCardSuccess = tkChinhSau - tkChinhTruoc;
                    texDislay = texDislay + "- *100: " + text + "<br/><br/>";
                    String key_sim_nap = GlobalValue.jsonSimSelected.optString("code") + "_" + GlobalValue.jsonSimSelected.optString("serial_number")+ "_nap";
                    Date date_ngay = new Date();
                    String strDateFormat_ = "ddMMyyyy";
                    /*hhhhhh*/
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(strDateFormat_);
                    String key_ngay = "tong_so_lan_nap_sai_trong_ngay_" + simpleDateFormat.format(date_ngay);

                    Date date_thang = new Date();
                    String strMonthFormat = "MMyyyy";
                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(strMonthFormat);
                    String key_month = "tong_so_tien_nap_trong_thang_" + simpleDateFormat1.format(date_thang);

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
                    if(tkChinhSau == 0){
                        /*Truong hop the da sai, the nap da nap roi*/
                        try {
                            jsonCakeNapTheoSim.put("so_the_sai_lien_tiep", jsonCakeNapTheoSim.optInt("so_the_sai_lien_tiep") + 1);
                            jsonCakeNapTheoSim.put(key_ngay, jsonCakeNapTheoSim.optInt(key_ngay) + 1);
                            jsonCakeNapTheoSim.put("tong_so_lan_nap_sai", jsonCakeNapTheoSim.optInt("tong_so_lan_nap_sai") + 1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        /*so lan nap sai lien tiep lon hon 0 hien text so lan nap sai*/
                        tvSoLanNapSaiLienTiep.setVisibility(View.VISIBLE);
                        tvSoLanNapSaiLienTiep.setText(jsonCakeNapTheoSim.optInt("so_the_sai_lien_tiep")+"");
                        if(jsonCakeNapTheoSim.optInt("so_the_sai_lien_tiep") >=3){
                            tvSoLanNapSaiLienTiep.setAnimation(mAnimation);
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
                        texDislay = statusNap + "<br/><br/>" + texDislay + textStatusNap + textStatusUpdate;
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
                        /*an text hien thi so lan nap sai lien tiep*/
                        tvSoLanNapSaiLienTiep.setVisibility(View.INVISIBLE);


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
                        texDislay = statusNap + "<br/><br/>" + texDislay + textStatusNap + textStatusUpdate;
                    }

                    /*save cake nap*/
                    try {
                        GlobalValue.jsonNapCake.put(key_sim_nap,jsonCakeNapTheoSim);
                        /*luu cache resference*/
                        DataStoreManager.saveInforNapTheTheoSim(GlobalValue.jsonNapCake.toString());
                        Log.d("inForSIm", "JSON CAKE SIM NAP: =====>>>>" + GlobalValue.jsonNapCake.toString());
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
                        jsonCard.put("time_loaded", simpleDateFormat.format(date));
                        jsonCard.put("ussdRes2","*100: " + text);
                        jsonCard.put("updatedCount", countCardLoadedFailse);
                        jsonCard.put("status",0);
                        GlobalValue.jsonDataCardLoaded.put(keyCardCurrent, jsonCard);
                        /*save cache*/
                        DataStoreManager.saveCardNapTheoSim(GlobalValue.jsonDataCardLoaded.toString());
                        Log.d("inForSIm", "OBJ CARD Sau khi nap the *100*: ====>>>" + jsonCard.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    /*update btn tra the*/
                    updateStatusBtnTraThe(false);

                    /*dong thread check callback to call ussd*/
                    checkCallBackUssd = false;
                    closeDelayUssd();
                }
                Log.d("inForSIm", "Data Card Cake Sau Nạp Thẻ:=====>>>> " + GlobalValue.jsonDataCardLoaded.toString());
                tvResultNapThe.setText(Html.fromHtml(texDislay));

                if(valueCode == 101){
                    valueCode = 100;
                    callPhoneNumber(100);
                }
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
                if (ActivityCompat.checkSelfPermission(getMainActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling

                    ActivityCompat.requestPermissions(getMainActivity(), new String[]{Manifest.permission.CALL_PHONE}, 101);

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
                    ussdCode = "*" + value + "*" + codeCard + Uri.encode("#");
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
                    ussdCode = "*" + value + "*" + codeCard + Uri.encode("#");
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
        if (SetupHelper.isPermissionGranted(getMainActivity()) && SetupHelper.isAccessibilityServiceEnabled(getMainActivity())) {
            /*ok thi lam gi*/
            return true;
        } else {
            return false;
        }
    }

    /*ham change status btn tra cuu*/
    public void updateStatusBtnTraCuu(boolean status){
        if(status == true){
            checkClickBtnTraCuu = true;
            btnTraCuu.setBackgroundResource(R.drawable.press_button_color);
            btnTraCuu.setTextColor(Color.parseColor("#ffffff"));
        }else {
            checkClickBtnTraCuu = false;
            btnTraCuu.setBackgroundResource(R.drawable.press_button_disable);
            btnTraCuu.setTextColor(Color.parseColor("#9E9E9E"));
        }
    }

    /*ham change status btn tra the*/
    public void updateStatusBtnTraThe(boolean status){
        if(status == true){
            checkClickBtnTraThe = true;
            btnTraThe.setBackgroundResource(R.drawable.press_button_color);
            btnTraThe.setTextColor(Color.parseColor("#ffffff"));
        }else {
            checkClickBtnTraThe = false;
            btnTraThe.setBackgroundResource(R.drawable.press_button_disable);
            btnTraThe.setTextColor(Color.parseColor("#9E9E9E"));
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            
            case R.id.btn_tra_the:
                /*tra the server*/
                Log.d("inForSIm", "Click btn tra the: " + checkClickBtnTraThe);
                if(checkClickBtnTraThe == true){
                    dialogTraThe = new Dialog(getMainActivity());
                    dialogTraThe.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialogTraThe.setContentView(R.layout.dialog_msg_tra_the);
                    final Button btnYes_the_loi, btnNo_huy_bo;
                    btnYes_the_loi = dialogTraThe.findViewById(R.id.btn_yes);
                    btnNo_huy_bo = dialogTraThe.findViewById(R.id.btn_no);
                    btnYes_the_loi.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkClickButton = -1;
                            showHiddenButton(checkClickButton);
                            showHiddenButtonTheSai(true);
                            updateStatusBtnTraCuu(false);
                            updateStatusBtnTraThe(false);
                            runPostDelay("Đang xử lý trả thẻ, vui lòng đợi ...", 3);
                            traTheServer();
                            dialogTraThe.dismiss();
                        }
                    });

                    btnNo_huy_bo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogTraThe.dismiss();
                        }
                    });

                    Window window_the_loi = dialogTraThe.getWindow();
                    window_the_loi.setBackgroundDrawableResource(R.color.transparent);
                    window_the_loi.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
                    window_the_loi.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    WindowManager.LayoutParams wmlpMsgwindow = dialogTraThe.getWindow().getAttributes();
                    wmlpMsgwindow.gravity = Gravity.CENTER;
                    dialogTraThe.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            dialog.dismiss();
                        }
                    });
                    dialogTraThe.setCancelable(false);
                    dialogTraThe.show();
                }
                break;
            
            case R.id.btn_tra_cuu:
                if(checkClickBtnTraCuu == true){
                    getMainActivity().switchFragment(1);
                }
                break;

            case R.id.tv_so_lan_nap_sai_lien_tiep:
                v.clearAnimation();
                dialogSoLanNapSaiLienTiep = new Dialog(getMainActivity());
                dialogSoLanNapSaiLienTiep.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogSoLanNapSaiLienTiep.setContentView(R.layout.dialog_msg_so_lan_nap_sai_lien_tiep);
                RelativeLayout rltParent = dialogSoLanNapSaiLienTiep.findViewById(R.id.rlt_parent);
                TextView tvSLNapSai = dialogSoLanNapSaiLienTiep.findViewById(R.id.tv_msg);
                String text = "";
                if(tvSoLanNapSaiLienTiep.getText().toString()!=null && !tvSoLanNapSaiLienTiep.getText().toString().equals("")){
                    text = "Số thẻ nạp sai liên tiếp: " + tvSoLanNapSaiLienTiep.getText().toString() + "\n\n3 thẻ sai liên tiếp => phải [LẤY THẺ ĐÚNG]\n" +
                            "4 thẻ sai liên tiếp => Bị khóa sim đến khi ADMIN mở lại";
                }
                tvSLNapSai.setText(text);
                Window window_sl_nap_sai_lien_tiep = dialogSoLanNapSaiLienTiep.getWindow();
                window_sl_nap_sai_lien_tiep.setBackgroundDrawableResource(R.color.transparent);
                window_sl_nap_sai_lien_tiep.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
                window_sl_nap_sai_lien_tiep.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
                WindowManager.LayoutParams wmlpMsgwindow_nap_sai_liep_tiep = dialogSoLanNapSaiLienTiep.getWindow().getAttributes();
                wmlpMsgwindow_nap_sai_liep_tiep.gravity = Gravity.CENTER;
                dialogSoLanNapSaiLienTiep.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialogSoLanNapSaiLienTiep.dismiss();
                    }
                });
                rltParent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogSoLanNapSaiLienTiep.dismiss();
                    }
                });
                dialogSoLanNapSaiLienTiep.setCancelable(true);
                dialogSoLanNapSaiLienTiep.show();
                break;

            case R.id.btn_lay_the_dung:
                Log.d("inForSIm", "GIA TRI checkClickLayTheDung: =====>>>>" + checkClickLayTheDung);
                if(checkClickLayTheDung == false){
                    v.clearAnimation();
                    /*goi ham lay the dung*/
                    getOkCard(GlobalValue.jsonSimSelected.optString("code"), GlobalValue.jsonSimSelected.optString("serial_number"));
                }
                break;

            case R.id.btn_the_nay_loi:
                Log.d("inForSIm", "GIA TRI checkClickTheNaySai: =====>>>>" + checkClickTheSai);
                if(checkClickTheSai == false){
                    dialogTheSai = new Dialog(getMainActivity());
                    dialogTheSai.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialogTheSai.setContentView(R.layout.dialog_msg_the_sai);
                    final Button btnYes_the_loi, btnNo_huy_bo;
                    btnYes_the_loi = dialogTheSai.findViewById(R.id.btn_yes);
                    btnNo_huy_bo = dialogTheSai.findViewById(R.id.btn_no);
                    btnYes_the_loi.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            adminClickTheSai = true;
                            updateStatusBtnTraThe(false);
                            showHiddenButtonTheSai(true);
                            dialogTheSai.dismiss();
                            checkClickButton = 1;
                            showHiddenButton(checkClickButton);
                            /*tao obj card lay ve*/
                            Date date = new Date();
                            String strDateFormat = "dd/MM/yyyy hh:mm:ss ";
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(strDateFormat);
                            try {
                                jsonCard.put("ussdRes1","Thẻ này sai");
                                jsonCard.put("comment_loaded","");
                                jsonCard.put("comment_update","");
                                jsonCard.put("faceValue",0);
                                jsonCard.put("time_loaded", simpleDateFormat.format(date));
                                jsonCard.put("ussdRes2","Thẻ này sai");
                                jsonCard.put("updatedCount", 0);
                                jsonCard.put("status",0);
                                GlobalValue.jsonDataCardLoaded.put(keyCardCurrent, jsonCard);
                                Log.d("inForSIm", "JSON CARTDDD: " + jsonCard.toString());
                                Log.d("inForSIm", "JSON GlobalValue.jsonDataCardLoaded: " + GlobalValue.jsonDataCardLoaded.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    btnNo_huy_bo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogTheSai.dismiss();
                        }
                    });

                    Window window_the_loi = dialogTheSai.getWindow();
                    window_the_loi.setBackgroundDrawableResource(R.color.transparent);
                    window_the_loi.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
                    window_the_loi.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    WindowManager.LayoutParams wmlpMsgwindow = dialogTheSai.getWindow().getAttributes();
                    wmlpMsgwindow.gravity = Gravity.CENTER;
                    dialogTheSai.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            dialogTheSai.dismiss();
                        }
                    });
                    dialogTheSai.setCancelable(false);
                    dialogTheSai.show();
                }
                break;
            case R.id.btn_nap_the:
                Log.d("inForSIm", "checkClickButton: " + checkClickButton);
                if(checkClickButton == 0 && !codeCard.equalsIgnoreCase("")){
                    if(checkAvailability() == true){
                        /*chay thread check goi ussd sau 10s co tra ve ket qua ko*/
                        runDelayCallUssd();

                        checkCallBackUssd = true;
                        checkCallNapTheTuApp = true;
                        Log.d("inForSIm", "CO CHAY VAO checkAvailability == true");
                        texDislay = getResources().getString(R.string.msg_the_chua_xu_ly);
                        tvResultNapThe.setText(texDislay);
                        checkClickButton = -1;
                        showHiddenButton(checkClickButton);
                        callPhoneNumber(101);

                        /*disable btn lay the dung vs btn the nay sai*/
                        showHiddenButtonLayTheDung(true);
                        showHiddenButtonTheSai(true);
                    }else {
                        dialog = new Dialog(getMainActivity());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_accessibility);
                        Button btnYes = dialog.findViewById(R.id.btn_yes);
                        btnYes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SetupHelper.openAccessibilitySettings(getContext());
                                dialog.dismiss();
                            }
                        });
                        Window window = dialog.getWindow();
                        window.setBackgroundDrawableResource(R.color.transparent);
                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
                        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        WindowManager.LayoutParams wmlpMsgwindow_the_loi = dialog.getWindow().getAttributes();
                        wmlpMsgwindow_the_loi.gravity = Gravity.CENTER;
                        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                dialog.dismiss();
                            }
                        });
                        dialog.setCancelable(false);
                        dialog.show();
                    }
                }
                break;
            case R.id.btn_update:
                Log.d("inForSIm", "checkClickButton: " + checkClickButton + " Gia tri checkCLickTheSai = " + checkClickTheSai);
                if(checkClickButton == 1 && GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent)!= null){
                    checkClickButton = -1;
                    valueCode = 101;
                    showHiddenButton(checkClickButton);
                    Log.d("inForSIm", "CHAY VAO HAM UPDATE: ");
                    /*chay ham update*/
                    runPostDelay("Đang cập nhật dữ liệu lên server ...", 1);
                    if(GlobalValue.isGetOkCard == false){
                        Log.d("inForSIm", "CHAY VAO HAM UPDATE: ");
                        updateCardToServer(GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent), adminClickTheSai);
                    }else {
                        Log.d("inForSIm", "CHAY VAO HAM OK UPDATE: ");
                        updateOkCardToServer(GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent));
                    }
                }
                break;
            case R.id.btn_reload:
                Log.d("inForSIm", "checkClickButton: " + checkClickButton);
                if(checkClickButton == 2){
                    checkClickButton = -1;
                    texDislay = getResources().getString(R.string.msg_the_chua_xu_ly);
                    tvResultNapThe.setText(texDislay);
                    showHiddenButton(checkClickButton);
                    /*get card*/
                    runPostDelay("Đang lấy mã thẻ, vui lòng đợi ...", 2);
                    getCard(GlobalValue.jsonSimSelected.optString("code"), GlobalValue.jsonSimSelected.optString("serial_number"));
                }
                break;
        }
    }

    /*ham an hien nut*/
    public void showHiddenButton(int value){
        if(value == 0){
            btnNapThe.setBackgroundResource(R.drawable.press_button_color);
            btnNapThe.setTextColor(Color.parseColor("#ffffff"));
            btnUpdate.setBackgroundResource(R.drawable.press_button_disable);
            btnUpdate.setTextColor(Color.parseColor("#9E9E9E"));
            btnReload.setBackgroundResource(R.drawable.press_button_disable);
            btnReload.setTextColor(Color.parseColor("#9E9E9E"));
        }else if(value == 1){
            btnNapThe.setBackgroundResource(R.drawable.press_button_disable);
            btnNapThe.setTextColor(Color.parseColor("#9E9E9E"));
            btnUpdate.setBackgroundResource(R.drawable.press_button_color);
            btnUpdate.setTextColor(Color.parseColor("#ffffff"));
            btnReload.setBackgroundResource(R.drawable.press_button_disable);
            btnReload.setTextColor(Color.parseColor("#9E9E9E"));
        }else if(value == 2){
            btnNapThe.setBackgroundResource(R.drawable.press_button_disable);
            btnNapThe.setTextColor(Color.parseColor("#9E9E9E"));
            btnUpdate.setBackgroundResource(R.drawable.press_button_disable);
            btnUpdate.setTextColor(Color.parseColor("#9E9E9E"));
            btnReload.setBackgroundResource(R.drawable.press_button_color);
            btnReload.setTextColor(Color.parseColor("#ffffff"));
        }else if(value == -1){
            btnNapThe.setBackgroundResource(R.drawable.press_button_disable);
            btnNapThe.setTextColor(Color.parseColor("#9E9E9E"));
            btnUpdate.setBackgroundResource(R.drawable.press_button_disable);
            btnUpdate.setTextColor(Color.parseColor("#9E9E9E"));
            btnReload.setBackgroundResource(R.drawable.press_button_disable);
            btnReload.setTextColor(Color.parseColor("#9E9E9E"));
        }
    }

    /*ham showHidden button lay the dung*/
    public void showHiddenButtonLayTheDung(boolean check){
        if(check == false){
            checkClickLayTheDung = false;
            btnLayTheDung.setBackgroundResource(R.drawable.press_button_color);
            btnLayTheDung.setTextColor(Color.parseColor("#ffffff"));
        }else {
            checkClickLayTheDung = true;
            btnLayTheDung.setBackgroundResource(R.drawable.press_button_disable);
            btnLayTheDung.setTextColor(Color.parseColor("#9E9E9E"));
        }
    }

    /*ham showHidden button the nay sai*/
    public void showHiddenButtonTheSai(boolean check){
        if(check == false){
            checkClickTheSai = false;
            btnTheNayLoi.setBackgroundResource(R.drawable.press_button_color_red);
            btnTheNayLoi.setTextColor(Color.parseColor("#ffffff"));
        }else {
            checkClickTheSai = true;
            btnTheNayLoi.setBackgroundResource(R.drawable.press_button_disable);
            btnTheNayLoi.setTextColor(Color.parseColor("#9E9E9E"));
        }
    }

    /*ham get one card */
    public void getCard(String providerID, String simNumber){
        showHiddenButton(-1);
        Log.d("inForSIm", "JSON CARD CAKE: " + GlobalValue.jsonDataCardLoaded.toString() + "    ==== " + GlobalValue.jsonDataCardLoaded.length());
        codeCard = "";
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
                        Log.d("inForSIm", "SUCCESS - GET CARD: " + response.toString() + "----checkClickButton: " + checkClickButton);
                        boolean status = response.optBoolean("s");
                        if(status == true){
                            if(response.optJSONObject("res") != null && response.optJSONObject("res").optString("cardCode")!= null){
                                GlobalValue.isGetOkCard = false;
                                GlobalValue.isUpdateToServer = false;
                                updateStatusBtnTraThe(true);
                                updateStatusBtnTraCuu(true);
                                closeHandle();
                                liContentValueText.setVisibility(View.VISIBLE);
                                showHiddenButtonTheSai(false);
                                tvMsgNoCard.setVisibility(View.INVISIBLE);
                                checkClickButton = 0;
                                showHiddenButton(checkClickButton);
                                JSONObject jsonObjectRes = response.optJSONObject("res");
                                tvMaThe.setText(jsonObjectRes.optString("cardCode"));
                                codeCard = jsonObjectRes.optString("cardCode");
                                GlobalValue.cardCodeCurrent = jsonObjectRes.optString("cardCode");
                                String provider = jsonObjectRes.optString("providerID");
                                if(provider.equals("GPC")){
                                    tvMang.setText("VINAPHONE");
                                }else if(provider.equals("VTEL")){
                                    tvMang.setText("VIETTEL");
                                }else if(provider.equals("VMS")){
                                    tvMang.setText("VINAPHONE");
                                }
                                tvMaKH.setText(jsonObjectRes.optString("customerID"));
                                tvTtKh.setText(jsonObjectRes.optString("accountInfo"));
                                /*tao obj card lay ve*/
                                jsonCard = new JSONObject();
                                try {
                                    keyCardCurrent = jsonObjectRes.optString("cardCode") + "_" + jsonObjectRes.optString("providerID");
                                    jsonCard.put("key",keyCardCurrent);
                                    jsonCard.put("providerID",jsonObjectRes.optString("providerID"));
                                    jsonCard.put("cardCode",jsonObjectRes.optString("cardCode"));
                                    jsonCard.put("simNumber",GlobalValue.jsonSimSelected.optString("serial_number"));
                                    Log.d("inForSIm", "OBJ CARD GET: " + jsonCard.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                /*kiem tra xem card lay ve da co trong cake chua*/
                                if(GlobalValue.jsonDataCardLoaded.length()>0){
                                    /*biên check de kiem tra nêu card chua co trong data cake thì set tvResultNapThe = "The chua xu ly"*/
                                    boolean check = false;
                                    Log.d("inForSIm", "COMMENT: chay vao day kiem tra xem card co trung trong cake ko");
                                    for (int i=0; i<GlobalValue.jsonDataCardLoaded.length(); i++){
                                        if(GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent) != null){
                                            check = true;
                                            Log.d("inForSIm", "COMMENT: The nay da nap va chua cap nhat len server");
                                            tvResultNapThe.setText(Html.fromHtml(GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).optString("status_nap") + "<br/><br/>" + GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).optString("ussdRes1") +"<br/><br/>"+
                                                    "- " +GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).optString("ussdRes2") + "<br/><br/>" +
                                                    GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).optString("comment_loaded") +
                                                    GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).optString("comment_update")));
                                            if(GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).optInt("status") == 0){
                                                checkClickButton = 1;
                                            }else {
                                                Log.d("inForSIm", "COMMENT: The nay da nap va da duoc cap nhat len server cho phep lay the moi");
                                                checkClickButton = 2;
                                            }
                                            showHiddenButtonTheSai(true);
                                            showHiddenButton(checkClickButton);
                                        }
                                    }
                                    if(check == false){
                                        tvResultNapThe.setText(getResources().getString(R.string.msg_the_chua_xu_ly));
                                        showHiddenButtonTheSai(false);
                                    }
                                }
                            }else {
                                updateStatusBtnTraThe(false);
                                updateStatusBtnTraCuu(false);
                                tvMsgNoCard.setVisibility(View.VISIBLE);
                                tvResultNapThe.setText(getResources().getString(R.string.msg_khong_co_the_can_nap));
                                liContentValueText.setVisibility(View.INVISIBLE);
                                showHiddenButtonTheSai(true);
                                checkClickButton = 2;
                                showHiddenButton(checkClickButton);
                                Log.d("inForSIm", "co chay vao ham close handle th s = true và data rong: " + response.toString());
                                if(response.optJSONObject("error") != null && response.optJSONObject("error").optString("text")!= null){
                                    Toast.makeText(self, response.optJSONObject("error").optString("text"), Toast.LENGTH_SHORT).show();
                                }
                                closeHandle();
                            }
                        }else {
                            if(response.optJSONObject("error") != null && response.optJSONObject("error").optString("text")!= null){
                                Toast.makeText(self, response.optJSONObject("error").optString("text"), Toast.LENGTH_SHORT).show();
                            }
                            updateStatusBtnTraThe(false);
                            updateStatusBtnTraCuu(false);
                            checkClickButton = 2;
                            tvResultNapThe.setText(getResources().getString(R.string.msg_khong_co_the_can_nap));
                            Log.d("inForSIm", "co chay vao ham close handle truong hop s =false: " + response.toString());
                            closeHandle();
                            showHiddenButtonTheSai(true);
                            showHiddenButton(checkClickButton);
                            tvMsgNoCard.setVisibility(View.VISIBLE);
                            liContentValueText.setVisibility(View.INVISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("inForSIm", error.toString());
                updateStatusBtnTraThe(false);
                updateStatusBtnTraCuu(false);
                checkClickButton = 2;
                showHiddenButton(checkClickButton);
                showHiddenButtonTheSai(true);
                tvResultNapThe.setText("Lỗi không lấy được mã thẻ");
            }
        });
        jsonObjectRequest.setTag("APP");
        Log.d("inForSIm", "request: " + jsonObjectRequest.toString());
        requestQueue.add(jsonObjectRequest);
    }

    /* ham update card sau khi nap xong*/
    /*ham get one card */
    public void updateCardToServer(final JSONObject jsonCard, final boolean adminClickTheSai_){
        keyObjectDataCakeCurrent = jsonCard.optString("key");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("providerID", jsonCard.optString("providerID"));
            jsonObject.put("cardCode", jsonCard.optString("cardCode"));
            jsonObject.put("simNumber", jsonCard.optString("simNumber"));
            jsonObject.put("faceValue", jsonCard.optInt("faceValue"));
            jsonObject.put("ussdRes1", jsonCard.optString("ussdRes1"));
            jsonObject.put("ussdRes2", jsonCard.optString("ussdRes2"));
            jsonObject.put("updatedCount", jsonCard.optString("updatedCount"));
            jsonObject.put("admin", adminClickTheSai_);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Apis.URL_UPDATE_CARD, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        closeHandle();
                        Log.d("inForSIm", "SUCCESS- UPDATE CARD: " + response.toString());
                        boolean status = response.optBoolean("s");
                        if(status == true){
                            /*giải phóng 1 card trong GlobalValue.soCardLayLanGanNhat*/
                            GlobalValue.soCardLayLanGanNhat = GlobalValue.soCardLayLanGanNhat - 1;

                            adminClickTheSai = false;
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
                                /*save cache*/
                                DataStoreManager.saveCardNapTheoSim(GlobalValue.jsonDataCardLoaded.toString());
                                tvResultNapThe.setText("");
                                Log.d("inForSIm", "GlobalValue.jsonDataCardLoaded lay raDate update ====: " + GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).toString());
                                tvResultNapThe.setText(Html.fromHtml(GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).optString("status_nap") + "<br/><br/>" + GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).optString("ussdRes1") +"<br/><br/>"+
                                        "- " +GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).optString("ussdRes2") + "<br/><br/>" +
                                        GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).optString("comment_loaded") +
                                        GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).optString("comment_update")));
                                Log.d("inForSIm", "Data Card Cake Sau Update:=====>>>> " + GlobalValue.jsonDataCardLoaded.toString());
                                Toast.makeText(getMainActivity(), "Bạn đã cập nhật thông tin thẻ thành công!", Toast.LENGTH_SHORT).show();


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            /*kiem tra xem so lan nap sai liep tiep la bao nhieu*/
                            if(jsonCakeNapTheoSim!= null && jsonCakeNapTheoSim.optInt("so_the_sai_lien_tiep") == 3){
                                checkClickButton = -1;
                                showHiddenButton(checkClickButton);
                                btnLayTheDung.startAnimation(mAnimation);
                                showHiddenButtonLayTheDung(false);
                                showHiddenButtonTheSai(true);
                                liContentValueText.setVisibility(View.INVISIBLE);
                                tvMsgNoCard.setVisibility(View.VISIBLE);
                                tvMsgNoCard.setText("Cảnh báo!!!\nBạn đã nạp sai liên tiếp 3 lần\n" +
                                        "Vui lòng bấm [LẤY THẺ ĐÚNG] để lấy mã thẻ chính xác!");
                            }else if(jsonCakeNapTheoSim!= null && jsonCakeNapTheoSim.optInt("so_the_sai_lien_tiep") >= 4){
                                checkClickButton = -1;
                                showHiddenButton(checkClickButton);
                                showHiddenButtonLayTheDung(true);
                                showHiddenButtonTheSai(true);
                                liContentValueText.setVisibility(View.INVISIBLE);
                                tvMsgNoCard.setVisibility(View.VISIBLE);
                                tvMsgNoCard.setText("Cảnh báo!!!\nTài khoản của bạn đã bị khóa do nạp sai quá số " +
                                        "lần quy định, Vui lòng liên hệ Admin để được mở khóa");
                            }else {
                                checkClickButton = 2;
                                showHiddenButton(checkClickButton);
                                showHiddenButtonLayTheDung(true);
                                showHiddenButtonTheSai(true);
                            }
                        }else {
                            countCheckUpdateFailse = countCheckUpdateFailse + 1;
                            checkClickButton = 1;
                            showHiddenButton(checkClickButton);
                            showDialogAfterUpdateServerTwoFailse(countCheckUpdateFailse, keyObjectDataCakeCurrent);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                closeHandle();
                Toast.makeText(getMainActivity(), "Cập nhật thẻ lỗi!", Toast.LENGTH_SHORT).show();
                Log.d("inForSIm", error.toString());
                checkClickButton = 1;
                showHiddenButton(checkClickButton);
                countCheckUpdateFailse = countCheckUpdateFailse + 1;
                showDialogAfterUpdateServerTwoFailse(countCheckUpdateFailse, keyObjectDataCakeCurrent);
            }
        });
        jsonObjectRequest.setTag("APP");
        requestQueue.add(jsonObjectRequest);
    }


    /*GET OK CARD*/
    public void getOkCard(String providerID, String simNumber){
        showHiddenButton(-1);
        Log.d("inForSIm", "JSON CARD CAKE: " + GlobalValue.jsonDataCardLoaded.toString() + "    ==== " + GlobalValue.jsonDataCardLoaded.length());
        codeCard = "";
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
                        Log.d("inForSIm", "SUCCESS - GET OK CARD: " + response.toString() + "----checkClickButton: " + checkClickButton);
                        boolean status = response.optBoolean("s");
                        if(status == true){
                            if(response.optJSONObject("res") != null && response.optJSONObject("res").optString("cardCode")!= null){
                                updateStatusBtnTraThe(true);
                                updateStatusBtnTraCuu(true);
                                GlobalValue.isGetOkCard = true;
                                GlobalValue.isUpdateToServer = false;
                                closeHandle();
                                tvMsgNoCard.setVisibility(View.INVISIBLE);
                                liContentValueText.setVisibility(View.VISIBLE);
                                checkClickButton = 0;
                                showHiddenButton(checkClickButton);
                                showHiddenButtonTheSai(false);
                                showHiddenButtonLayTheDung(true);
                                JSONObject jsonObjectRes = response.optJSONObject("res");
                                tvMaThe.setText(jsonObjectRes.optString("cardCode"));
                                codeCard = jsonObjectRes.optString("cardCode");
                                GlobalValue.cardCodeCurrent = jsonObjectRes.optString("cardCode");
                                String provider = jsonObjectRes.optString("providerID");
                                if(provider.equals("GPC")){
                                    tvMang.setText("VINAPHONE");
                                }else if(provider.equals("VTEL")){
                                    tvMang.setText("VIETTEL");
                                }else if(provider.equals("VMS")){
                                    tvMang.setText("VINAPHONE");
                                }
                                tvMaKH.setText(jsonObjectRes.optString("customerID"));
                                tvTtKh.setText(jsonObjectRes.optString("accountInfo"));
                                /*tao obj card lay ve*/
                                jsonCard = new JSONObject();
                                try {
                                    keyCardCurrent = jsonObjectRes.optString("cardCode") + "_" + jsonObjectRes.optString("providerID");
                                    jsonCard.put("key",keyCardCurrent);
                                    jsonCard.put("providerID",jsonObjectRes.optString("providerID"));
                                    jsonCard.put("cardCode",jsonObjectRes.optString("cardCode"));
                                    jsonCard.put("simNumber",GlobalValue.jsonSimSelected.optString("serial_number"));
                                    Log.d("inForSIm", "OBJ CARD GET: " + jsonCard.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                /*kiem tra xem card lay ve da co trong cake chua*/
                                if(GlobalValue.jsonDataCardLoaded.length()>0){
                                    /*biên check de kiem tra nêu card chua co trong data cake thì set tvResultNapThe = "The chua xu ly"*/
                                    boolean check = false;
                                    Log.d("inForSIm", "COMMENT: chay vao day kiem tra xem card co trung trong cake ko");
                                    for (int i=0; i<GlobalValue.jsonDataCardLoaded.length(); i++){
                                        if(GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent) != null){
                                            check = true;
                                            Log.d("inForSIm", "COMMENT: The nay da nap va chua cap nhat len server");
                                            tvResultNapThe.setText(Html.fromHtml(GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).optString("status_nap") + "<br/><br/>" + GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).optString("ussdRes1") +"<br/><br/>"+
                                                    "- " +GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).optString("ussdRes2") + "<br/><br/>" +
                                                    GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).optString("comment_loaded") +
                                                    GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).optString("comment_update")));
                                            if(GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).optInt("status") == 0){
                                                checkClickButton = 1;
                                            }else {
                                                Log.d("inForSIm", "COMMENT: The nay da nap va da duoc cap nhat len server cho phep lay the moi");
                                                checkClickButton = 2;
                                            }
                                            showHiddenButtonTheSai(true);
                                            showHiddenButton(checkClickButton);
                                        }
                                    }
                                    if(check == false){
                                        tvResultNapThe.setText(getResources().getString(R.string.msg_the_chua_xu_ly));
                                        showHiddenButtonTheSai(false);
                                    }
                                }
                            }else {
                                updateStatusBtnTraThe(false);
                                updateStatusBtnTraCuu(false);
                                tvMsgNoCard.setVisibility(View.VISIBLE);
                                tvResultNapThe.setText(getResources().getString(R.string.msg_khong_co_the_can_nap));
                                liContentValueText.setVisibility(View.INVISIBLE);
                                showHiddenButtonTheSai(true);
                                showHiddenButtonLayTheDung(false);
                                checkClickButton = 2;
                                showHiddenButton(checkClickButton);
                                Log.d("inForSIm", "co chay vao ham close handle th s = true và data rong: " + response.toString());
                                closeHandle();
                            }
                        }else {
                            updateStatusBtnTraThe(false);
                            updateStatusBtnTraCuu(false);
                            checkClickButton = 2;
                            tvResultNapThe.setText(getResources().getString(R.string.msg_khong_co_the_can_nap));
                            Log.d("inForSIm", "co chay vao ham close handle truong hop s =false: " + response.toString());
                            showHiddenButtonTheSai(true);
                            showHiddenButtonLayTheDung(false);
                            closeHandle();
                            showHiddenButton(checkClickButton);
                            tvMsgNoCard.setVisibility(View.VISIBLE);
                            liContentValueText.setVisibility(View.INVISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("inForSIm", error.toString());
                updateStatusBtnTraThe(false);
                updateStatusBtnTraCuu(false);
                showHiddenButtonTheSai(true);
                showHiddenButtonLayTheDung(false);
                checkClickButton = 2;
                showHiddenButton(checkClickButton);
                tvResultNapThe.setText("Lỗi không lấy được mã thẻ");
            }
        });
        jsonObjectRequest.setTag("APP");
        Log.d("inForSIm", "request: " + jsonObjectRequest.toString());
        requestQueue.add(jsonObjectRequest);
    }


    /*update ok card*/
    public void updateOkCardToServer(final JSONObject jsonCard){
        keyObjectDataCakeCurrent = jsonCard.optString("key");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("providerID", jsonCard.optString("providerID"));
            jsonObject.put("cardCode", jsonCard.optString("cardCode"));
            jsonObject.put("simNumber", jsonCard.optString("simNumber"));
            jsonObject.put("faceValue", jsonCard.optInt("faceValue"));
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
                        closeHandle();
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
                                /*save cache*/
                                DataStoreManager.saveCardNapTheoSim(GlobalValue.jsonDataCardLoaded.toString());

                                tvResultNapThe.setText("");
                                Log.d("inForSIm", "GlobalValue.jsonDataCardLoaded lay raDate update ====: " + GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).toString());
                                tvResultNapThe.setText(Html.fromHtml(GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).optString("status_nap") + "<br/><br/>" + GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).optString("ussdRes1") +"<br/><br/>"+
                                        "- " +GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).optString("ussdRes2") + "<br/><br/>" +
                                        GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).optString("comment_loaded") +
                                        GlobalValue.jsonDataCardLoaded.optJSONObject(keyCardCurrent).optString("comment_update")));
                                Log.d("inForSIm", "Data Card Cake Sau Update:=====>>>> " + GlobalValue.jsonDataCardLoaded.toString());
                                Toast.makeText(getMainActivity(), "Bạn đã cập nhật thông tin thẻ thành công!", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            /*kiem tra xem so lan nap sai liep tiep la bao nhieu*/
                           if(jsonCakeNapTheoSim!= null && jsonCakeNapTheoSim.optInt("so_the_sai_lien_tiep") >= 4){
                                checkClickButton = -1;
                                showHiddenButton(checkClickButton);
                                showHiddenButtonLayTheDung(true);
                                showHiddenButtonTheSai(true);
                                liContentValueText.setVisibility(View.INVISIBLE);
                                tvMsgNoCard.setVisibility(View.VISIBLE);
                                tvMsgNoCard.setText("Cảnh báo!!!\nTài khoản của bạn đã bị khóa do nạp sai quá số " +
                                        "lần quy định, Vui lòng liên hệ Admin để được mở khóa");
                            }else {
                                checkClickButton = 2;
                                showHiddenButton(checkClickButton);
                                showHiddenButtonLayTheDung(true);
                                showHiddenButtonTheSai(true);
                            }

                        }else {
                            countCheckUpdateFailse = countCheckUpdateFailse + 1;
                            checkClickButton = 1;
                            showHiddenButton(checkClickButton);
                            showDialogAfterUpdateServerTwoFailse(countCheckUpdateFailse, keyObjectDataCakeCurrent);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                closeHandle();
                Toast.makeText(getMainActivity(), "Cập nhật thẻ lỗi!", Toast.LENGTH_SHORT).show();
                Log.d("inForSIm", error.toString());
                checkClickButton = 1;
                showHiddenButton(checkClickButton);
                countCheckUpdateFailse = countCheckUpdateFailse + 1;
                showDialogAfterUpdateServerTwoFailse(countCheckUpdateFailse, keyObjectDataCakeCurrent);
            }
        });
        jsonObjectRequest.setTag("APP");
        requestQueue.add(jsonObjectRequest);
    }

    /*trả thẻ*/
    public void traTheServer(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("providerID", jsonCard.optString("providerID"));
            jsonObject.put("cardCode", jsonCard.optString("cardCode"));
            jsonObject.put("simNumber", jsonCard.optString("simNumber"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("inForSIm", "OBJECT REQUEST TRA THE- TRA THE: " + jsonObject.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Apis.URL_TRA_THE, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        closeHandle();
                        Log.d("inForSIm", "SUCCESS- TRA THE: " + response.toString());
                        boolean status = response.optBoolean("s");
                        if(status == true){
                            GlobalValue.isUpdateToServer = true;
                            checkClickBtnTraThe = false;
                            checkClickButton = 2;
                            showHiddenButton(checkClickButton);
                            tvMsgNoCard.setVisibility(View.VISIBLE);
                            tvResultNapThe.setText(getResources().getString(R.string.msg_khong_co_the_can_nap));
                            liContentValueText.setVisibility(View.INVISIBLE);
                            Toast.makeText(self, "Thẻ đã được trả lại hệ thống", Toast.LENGTH_SHORT).show();
                        }else {
                            updateStatusBtnTraThe(true);
                            showHiddenButtonTheSai(false);
                            updateStatusBtnTraCuu(true);
                            checkClickButton = 0;
                            showHiddenButton(checkClickButton);
                            Toast.makeText(self, "Trả thẻ lỗi, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                closeHandle();
                Log.d("inForSIm", error.toString());
                updateStatusBtnTraThe(true);
                showHiddenButtonTheSai(false);
                updateStatusBtnTraCuu(true);
                checkClickButton = 0;
                showHiddenButton(checkClickButton);
                Toast.makeText(self, "Trả thẻ lỗi, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjectRequest.setTag("APP");
        requestQueue.add(jsonObjectRequest);
    }

    /*thread mytask check call ussd callback*/
    public void runDelayCallUssd(){
        myTaskCallUssd = new Runnable() {
            @Override
            public void run() {
                Log.d("inForSIm","THREAD DELAY USSD myTaskCallUssd: ==" + checkCallBackUssd);
                if(checkCallBackUssd == true){
                    checkClickButton = 0;
                    showHiddenButton(checkClickButton);
                }
            }
        };
        handlerCallUssd.postDelayed(myTaskCallUssd, 15000);
    }

    public void closeDelayUssd(){
        if(handlerCallUssd!=null){
            handlerCallUssd.removeCallbacks(myTaskCallUssd);
            Log.d("inForSIm","Dong THREAD DELAY USSD");
        }
    }


    /*run post delay*/
    public void runPostDelay(final String msg, final int value){
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
                    if(value == 0){
                        checkClickButton = 2;
                        tvMsgNoCard.setText("Không thể kết nối lên server");
                        tvResultNapThe.setText(getResources().getString(R.string.msg_the_chua_xu_ly));
                        tvMsgNoCard.setVisibility(View.VISIBLE);
                    }else {
                        checkClickButton = value;
                    }
                    if(value == 1){
                        Toast.makeText(getMainActivity(), "Cập nhật thẻ lỗi!", Toast.LENGTH_SHORT).show();
                        countCheckUpdateFailse = countCheckUpdateFailse + 1;
                        showDialogAfterUpdateServerTwoFailse(countCheckUpdateFailse, keyObjectDataCakeCurrent);
                    }
                    if(value == 3){
                        Toast.makeText(self, "Trả thẻ lỗi, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                    }
                    showHiddenButton(checkClickButton);
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

    /*ham show dialog thong bao da update to server 2 lan lien tiep failse*/
    public void showDialogAfterUpdateServerTwoFailse(int value, final String keyObject){
        if(value>=2){
            dialogMsgFailseTwo = new Dialog(getMainActivity());
            dialogMsgFailseTwo.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogMsgFailseTwo.setContentView(R.layout.dialog_msg_update_failse_two);
            Button btnYes, btnNo;
            TextView tvMsg;
            tvMsg = dialogMsgFailseTwo.findViewById(R.id.tv_msg);
            btnYes = dialogMsgFailseTwo.findViewById(R.id.btn_yes);
            btnNo = dialogMsgFailseTwo.findViewById(R.id.btn_no);
            String msg = "Bạn đã cập nhật " + value + " liên tiếp lỗi!\n\nLấy mã thẻ mới bấm [THẺ MỚI]\n" +
                    "Cập nhật lại thẻ cũ bấm [CẬP NHẬT LẠI]";
            tvMsg.setText(msg);
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogMsgFailseTwo.dismiss();
                    checkClickButton = 2;
                    keyCardCurrent = "";
                    showHiddenButton(-1);
                    runPostDelay("Đang lấy thẻ mới, vui lòng đợi ...", 2);
                    getCard(GlobalValue.jsonSimSelected.optString("code"), GlobalValue.jsonSimSelected.optString("serial_number"));

                }
            });
            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogMsgFailseTwo.dismiss();
                    if(GlobalValue.jsonDataCardLoaded.optJSONObject(keyObject) != null){
                        runPostDelay("Đang cập nhật, vui lòng đợi ...", 1);
                        showHiddenButton(-1);
                        updateCardToServer(GlobalValue.jsonDataCardLoaded.optJSONObject(keyObject), adminClickTheSai);
                    }
                }
            });
            Window window = dialogMsgFailseTwo.getWindow();
            window.setBackgroundDrawableResource(R.color.transparent);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            WindowManager.LayoutParams wmlpMsgwindow = dialogMsgFailseTwo.getWindow().getAttributes();
            wmlpMsgwindow.gravity = Gravity.CENTER;
            dialogMsgFailseTwo.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dialogMsgFailseTwo.dismiss();
                }
            });
            dialogMsgFailseTwo.setCancelable(false);
            dialogMsgFailseTwo.show();
        }
    }
}