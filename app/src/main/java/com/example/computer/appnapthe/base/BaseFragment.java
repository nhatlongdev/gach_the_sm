package com.example.computer.appnapthe.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.computer.appnapthe.activity.MainActivity;

/**
 * Created by Trang on 6/29/2016.
 */
public abstract class BaseFragment extends Fragment {

    protected Context self;

    protected abstract int getLayoutInflate();
    protected abstract void init();
    protected abstract void initView(View view);
    protected abstract void getData();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = getActivity();
        init();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutInflate(),container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initView(view);
        getData();
    }

    public MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

}
