package com.inhatc.inhatime;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment"; //오류나 디버깅할 때 상황을 알기 위한 코드

    // 화면이 생성된 이후 호출되는 메서드 -> 인플레이션
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);

        initUI(rootView);

        return rootView;

    }

    private void initUI(ViewGroup rootView) {


    }
}
