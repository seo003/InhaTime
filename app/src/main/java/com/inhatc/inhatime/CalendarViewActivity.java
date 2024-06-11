package com.inhatc.inhatime;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CalendarViewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CalendarViewActivity";
    private Button btnAdd;
    private EditText editTextToDo;
    private DatabaseReference database;
    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private ArrayList<Note> todoList;
    private int lastUsedId = 0;
    private TextView txtDate;
    private TextView txtTime;
    private EditText txtInput;
    private String selectedDate;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SubMenu mnuCalendar = menu.addSubMenu("Calendar");
        SubMenu mnuTimer = menu.addSubMenu("Timer");

        // Calendar 메뉴 항목 클릭 리스너 설정
        mnuCalendar.getItem().setOnMenuItemClickListener(item -> {
            // CalendarActivity로 이동
            startActivity(new Intent(this, CalendarActivity.class));
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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calendar_view);

        // Firebase Database 초기화
        database = FirebaseDatabase.getInstance().getReference();

        btnAdd = findViewById(R.id.btnAdd);
        editTextToDo = findViewById(R.id.inputText); // 할 일 입력 EditText 추가
        recyclerView = findViewById(R.id.recyclerView); // RecyclerView를 올바르게 찾기
        txtDate = findViewById(R.id.txtDate); // 날짜를 표시할 TextView 추가
        txtTime = findViewById(R.id.txtTime); // 타이머 값을 표시할 TextView 추가
        txtInput = findViewById(R.id.inputText);

        // 전달된 날짜 받아서 TextView에 표시
        Intent intent = getIntent();
        selectedDate = intent.getStringExtra("selectedDate");
        txtDate.setText(selectedDate);

        // 날짜 형식을 yyyy-MM-dd로 변환
        if (selectedDate != null) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy년MM월dd일", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                Date date = inputFormat.parse(selectedDate);
                if (date != null) {
                    selectedDate = outputFormat.format(date);
                } else {
                    txtDate.setText("Invalid Date");
                }
            } catch (ParseException e) {
                Log.e(TAG, "날짜 형식 변환 오류", e);
                txtDate.setText("Error parsing date");
            }
        } else {
            txtDate.setText("Date not provided");
        }

        // RecyclerView 설정
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        todoList = new ArrayList<>();
        adapter = new NoteAdapter(todoList);
        recyclerView.setAdapter(adapter);

        btnAdd.setOnClickListener(this);

        // Firebase에서 데이터 가져오기
        fetchDataFromFirebase();

        // 타이머 값 로드
        loadTimerValue(selectedDate);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onClick(View view) {
        if (view == btnAdd) {
            saveToDo();
        }
    }

    private void saveToDo() {
        String todo = editTextToDo.getText().toString().trim();
        if (!todo.isEmpty() && selectedDate != null) {
            Note newNote = new Note(lastUsedId + 1, todo, false, selectedDate);

            // 로그로 선택된 날짜와 저장할 데이터 출력
            Log.d(TAG, "Saving ToDo: " + newNote.getTodo());
            Log.d(TAG, "Saving ToDo: " + newNote.getDate());

            database.child("todos").child(String.valueOf(newNote.get_id())).setValue(newNote)
                    .addOnSuccessListener(aVoid -> {
                        todoList.add(newNote);
                        adapter.notifyItemInserted(todoList.size() - 1);
                        lastUsedId = newNote.get_id();
                        database.child("todos").child("lastUsedId").setValue(lastUsedId);
                        Toast.makeText(getApplicationContext(), "추가되었습니다", Toast.LENGTH_SHORT).show();

                        txtInput.setText("");
                    })
                    .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "추가에 실패했습니다: " + e.getMessage(), Toast.LENGTH_LONG).show());
        } else {
            Toast.makeText(getApplicationContext(), "할 일을 입력해주세요 또는 날짜가 선택되지 않았습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchDataFromFirebase() {
        if (database == null) {
            Log.e(TAG, "Database reference is null");
            return;
        }

        database.child("todos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                todoList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        if (snapshot.getKey().equals("lastUsedId")) {
                            lastUsedId = snapshot.getValue(Integer.class);
                        } else {
                            Note note = snapshot.getValue(Note.class);
                            if (note != null && selectedDate != null && selectedDate.equals(note.getDate())) {
                                todoList.add(note);
                            } else if (note == null) {
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
                Toast.makeText(CalendarViewActivity.this, "데이터를 가져오는데 실패했습니다: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadTimerValue(String date) {
        database.child("timer").child(date).get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        Integer savedCnt = dataSnapshot.getValue(Integer.class);
                        if (savedCnt != null) {
                            String formattedTime = formatSecondsToHHMMSS(savedCnt);
                            txtTime.setText(formattedTime);
                            Toast.makeText(CalendarViewActivity.this, "Timer value loaded", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        txtTime.setText("00:00:00");
                        Toast.makeText(CalendarViewActivity.this, "No timer value found for selected date", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    txtTime.setText("Error");
                    Toast.makeText(CalendarViewActivity.this, "Error loading timer value: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private String formatSecondsToHHMMSS(int seconds) {
        long hours = TimeUnit.SECONDS.toHours(seconds);
        long minutes = TimeUnit.SECONDS.toMinutes(seconds) % 60;
        long secs = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, secs);
    }
}
