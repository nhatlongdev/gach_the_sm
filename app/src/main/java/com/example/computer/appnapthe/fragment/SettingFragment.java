package com.example.computer.appnapthe.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.computer.appnapthe.R;
import com.example.computer.appnapthe.base.BaseFragment;
import com.example.computer.appnapthe.configs.GlobalValue;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SettingFragment extends BaseFragment implements View.OnClickListener{

    public Button btnThongTinSim;
    public Dialog dialogInfoSim;

    public static SettingFragment newInstance() {
        Bundle args = new Bundle();
        SettingFragment fragment = new SettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutInflate() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initView(View view) {
        btnThongTinSim = view.findViewById(R.id.btn_tt_sim);
    }

    @Override
    protected void getData() {
        btnThongTinSim.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_tt_sim:
                dialogInfoSim = new Dialog(getMainActivity());
                dialogInfoSim.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogInfoSim.setContentView(R.layout.dialog_infor_sim);
                RelativeLayout rltParentDialogInforSim;
                TextView tvNhaMang, tvSerialNumber, tvTienNap, tvThang;
                tvNhaMang = dialogInfoSim.findViewById(R.id.tv_nha_mang);
                tvSerialNumber = dialogInfoSim.findViewById(R.id.tv_serial_number);
                tvTienNap = dialogInfoSim.findViewById(R.id.tv_money);
                tvThang = dialogInfoSim.findViewById(R.id.tv_month);
                rltParentDialogInforSim = dialogInfoSim.findViewById(R.id.rlt_parent_dialog);
                if(GlobalValue.jsonSimSelected != null && GlobalValue.jsonSimSelected.optString("name") != null){
                    tvNhaMang.setText(GlobalValue.jsonSimSelected.optString("name"));
                    tvSerialNumber.setText(GlobalValue.jsonSimSelected.optString("serial_number"));

                    /*lay time hien tai theo format*/
                    DateFormat df1=new SimpleDateFormat("MMyyyy");//foramt date
                    String monthCurrent = df1.format(Calendar.getInstance().getTime());
                    /*dinh dang money*/
                    final NumberFormat formatter = new DecimalFormat("###,###,###.##");
                    String tongTienTheoThang = formatter.format(GlobalValue.jsonSimSelected.optInt("total_money_month_" + monthCurrent));
                    tvTienNap.setText(tongTienTheoThang + " vnđ");

                    /*lay time hien tai theo format de hien thi*/
                    DateFormat df2=new SimpleDateFormat("MM/yyyy");//foramt date
                    String monthCurrent2 = df2.format(Calendar.getInstance().getTime());
                    tvThang.setText(monthCurrent2);
                }
                rltParentDialogInforSim.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogInfoSim.dismiss();
                    }
                });
                Window window_the_loi = dialogInfoSim.getWindow();
                window_the_loi.setBackgroundDrawableResource(R.color.transparent);
                window_the_loi.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
                window_the_loi.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
                WindowManager.LayoutParams wmlpMsgwindow = dialogInfoSim.getWindow().getAttributes();
                wmlpMsgwindow.gravity = Gravity.CENTER;
                dialogInfoSim.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialogInfoSim.dismiss();
                    }
                });
                dialogInfoSim.setCancelable(true);
                dialogInfoSim.show();
                break;
        }
    }
}