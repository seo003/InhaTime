package com.inhatc.inhatime;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ToDoListActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ToDoListActivity";
    private Button btnAdd;
    private EditText editTextToDo;
    private DatabaseReference database;
    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private ArrayList<Note> todoList;
    private int lastUsedId = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SubMenu mnuCalendar = menu.addSubMenu("Calendar");
        SubMenu mnuTodolist = menu.addSubMenu("To Do List");
        SubMenu mnuTimer = menu.addSubMenu("Timer");

        // Calendar 메뉴 항목 클릭 리스너 설정
        mnuCalendar.getItem().setOnMenuItemClickListener(item -> {
            // CalendarActivity로 이동
            startActivity(new Intent(this, CalendarActivity.class));
            return true;
        });

        // To Do List 메뉴 항목 클릭 리스너 설정
        mnuTodolist.getItem().setOnMenuItemClickListener(item -> {
            // ToDoListActivity로 이동
            startActivity(new Intent(this, ToDoListActivity.class));
            return true;
        });

        // Timer 메뉴 항목 클릭 리스너 설정
        mnuTimer.getItem().setOnMenuItemClickListener(item -> {
            // TimerActivity로 이동
            startActivity(new Intent(this, TimerActivity.class));
            return true;
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);

        // Firebase Database 초기화
        database = FirebaseDatabase.getInstance().getReference("todos");

        btnAdd = findViewById(R.id.btnAdd);
        editTextToDo = findViewById(R.id.inputText); // 할 일 입력 EditText 추가
        recyclerView = findViewById(R.id.recyclerView); // RecyclerView를 올바르게 찾기

        // RecyclerView 설정
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        todoList = new ArrayList<>();
        adapter = new NoteAdapter(todoList);
        recyclerView.setAdapter(adapter);

        btnAdd.setOnClickListener(this);

        // Firebase에서 데이터 가져오기
        fetchDataFromFirebase();
    }

    @Override
    public void onClick(View view) {
        if (view == btnAdd) {
            saveToDo();
        }
    }

    private void saveToDo() {
        String todo = editTextToDo.getText().toString().trim();
        if (!todo.isEmpty()) {
            String currentDate = getCurrentDate(); // 현재 날짜를 가져오는 메소드
            int newId = lastUsedId + 1; // 새로운 ID 생성
            Note newNote = new Note(newId, todo, false, currentDate); // 날짜 필드 추가
            database.child(String.valueOf(newId)).setValue(newNote)
                    .addOnSuccessListener(aVoid -> {
                        todoList.add(newNote);
                        adapter.notifyItemInserted(todoList.size() - 1);
                        lastUsedId = newId; // 마지막 사용된 ID 업데이트
                        database.child("lastUsedId").setValue(lastUsedId); // Firebase에 저장
                        Toast.makeText(getApplicationContext(), "추가되었습니다", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "추가에 실패했습니다: " + e.getMessage(), Toast.LENGTH_LONG).show());
        } else {
            Toast.makeText(getApplicationContext(), "할 일을 입력해주세요", Toast.LENGTH_SHORT).show();
        }
    }

    private String getCurrentDate() {
        // 현재 날짜를 가져오는 메소드 (형식에 맞게 변경 가능)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void fetchDataFromFirebase() {
        if (database == null) {
            Log.e(TAG, "Database reference is null");
            return;
        }

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                todoList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        if (snapshot.getKey().equals("lastUsedId")) {
                            lastUsedId = snapshot.getValue(Integer.class);
                        } else {
                            Note note = snapshot.getValue(Note.class);
                            if (note != null) {
                                todoList.add(note);
                            } else {
                                // 로그 추가: 변환에 실패한 경우 확인
                                Log.e(TAG, "Note 변환 실패: " + snapshot.getValue());
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "오류 발생: ", e);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ToDoListActivity.this, "데이터를 가져오는데 실패했습니다: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /*
    private void initializeTodos() {
        ArrayList<Note> initialTodos = new ArrayList<>();

        for (Note note : initialTodos) {
            database.child(String.valueOf(note.get_id())).setValue(note)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Initial todo added"))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to add initial todo", e));
        }
        lastUsedId = initialTodos.size();
        database.child("lastUsedId").setValue(lastUsedId); // Firebase에 저장
    }

     */
}
