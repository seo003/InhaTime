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

    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private ArrayList<Note> todoList;
    private DatabaseReference database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);

        initUI(rootView);

        // Firebase에서 데이터 가져오기
        fetchDataFromFirebase();

        return rootView;
    }

    private void initUI(ViewGroup rootView) {
        // recyclerView를 연결
        recyclerView = rootView.findViewById(R.id.recyclerView);

        // LinearLayout에 recyclerView 추가
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // 어댑터 연결
        todoList = new ArrayList<>();
        adapter = new NoteAdapter(todoList);
        recyclerView.setAdapter(adapter);
    }

    private void fetchDataFromFirebase() {
        // Firebase Database에서 데이터를 가져오는 이벤트 리스너 설정
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
