package com.inhatc.inhatime;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment"; //오류나 디버깅할 때 상황을 알기 위한 코드
    RecyclerView recyclerView;
    NoteAdapter adapter;

    // 화면이 생성된 이후 호출되는 메서드 -> 인플레이션
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);

        initUI(rootView);

        return rootView;

    }

    private void initUI(ViewGroup rootView) {
        //fragment_main.xml에 만들었던  RecyclerView를 연결.
        recyclerView = rootView.findViewById(R.id.recyclerView);

        //LinearLayout을 이용하여 LinearLayout에 recyclerView를 붙입니다.
        //이 후 이것은 todo_item들이 세로로 하나하나 정렬하는 역할을 하게 됩니다.
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        //어댑터들을 연결하는 역할을 하게 됩니다.
        adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

    }
}
