package com.inhatc.inhatime;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment"; //오류나 디버깅할 때 상황을 알기 위한 코드
    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private ArrayList<Note> todoList;
    private DatabaseReference database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);

        // Firebase Database 초기화
        //database = FirebaseDatabase.getInstance().getReference("todos");

        initUI(rootView);

        // Firebase에서 데이터 가져오기
        fetchDataFromFirebase();

        return rootView;
    }

    private void initUI(ViewGroup rootView) {
        // fragment_main.xml에 만들었던 RecyclerView를 연결.
        recyclerView = rootView.findViewById(R.id.recyclerView);

        // LinearLayout을 이용하여 LinearLayout에 recyclerView를 붙입니다.
        // 이 후 이것은 todo_item들이 세로로 하나하나 정렬하는 역할을 하게 됩니다.
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // 어댑터들을 연결하는 역할을 하게 됩니다.
        todoList = new ArrayList<>();
        adapter = new NoteAdapter(todoList);
        recyclerView.setAdapter(adapter);
    }

    private void fetchDataFromFirebase() {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                todoList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Note note = snapshot.getValue(Note.class);
                    todoList.add(note);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터 가져오기에 실패한 경우 처리
            }
        });
    }
}
