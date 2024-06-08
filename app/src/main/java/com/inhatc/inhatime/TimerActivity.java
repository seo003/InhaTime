package com.inhatc.inhatime;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.SubMenu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class TimerActivity extends AppCompatActivity {

    private Timer timerCall;
    private int nCnt;
    private TextView txtHour, txtMinute, txtSecond;
    private Button btnStart, btnStop;
    private Handler handler = new Handler();

    private DatabaseReference database;

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
        setContentView(R.layout.activity_timer);

        nCnt = 0; // 타이머 카운트 초기화

        // UI 요소 초기화
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        txtHour = findViewById(R.id.txtHour);
        txtMinute = findViewById(R.id.txtMinute);
        txtSecond = findViewById(R.id.txtSecond);

        // Firebase Database 초기화
        database = FirebaseDatabase.getInstance().getReference();

        // Start 버튼 클릭 이벤트 설정
        btnStart.setOnClickListener(view -> startTimer());

        // Stop 버튼 클릭 이벤트 설정
        btnStop.setOnClickListener(view -> stopTimer());

        // 타이머 값을 불러옴
        loadTimerValue();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTimerValue(); // 액티비티가 다시 활성화될 때 타이머 값을 불러옴
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveTimerValue(); // 액티비티가 일시 중지될 때 타이머 값을 저장
    }

    // 타이머 시작
    private void startTimer() {
        if (timerCall == null) {
            timerCall = new Timer();
            timerCall.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    handler.post(() -> {
                        nCnt++; // 타이머 증가
                        updateTimer(); // UI 업데이트
                    });
                }
            }, 0, 1000); // 1초마다 실행
        }
    }

    // 타이머 중지 및 값 저장
    private void stopTimer() {
        if (timerCall != null) {
            timerCall.cancel();
            timerCall = null;
        }
        saveTimerValue(); // 타이머를 중지한 후 저장
    }

    // 타이머 값을 UI에 업데이트
    private void updateTimer() {
        int hours = nCnt / 3600;
        int minutes = (nCnt % 3600) / 60;
        int seconds = nCnt % 60;

        txtHour.setText(String.format("%02d", hours));
        txtMinute.setText(String.format(":%02d", minutes));
        txtSecond.setText(String.format(":%02d", seconds));
    }

    // 타이머 값을 Firebase Realtime Database에 저장
    private void saveTimerValue() {
        String currentDateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new java.util.Date());
        database.child("timer").child(currentDateStr).setValue(nCnt)
                .addOnSuccessListener(aVoid -> Toast.makeText(TimerActivity.this, "Timer value saved", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(TimerActivity.this, "Error saving timer value: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    // 데이터베이스에서 타이머 값을 불러옴
    private void loadTimerValue() {
        String currentDateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new java.util.Date());
        database.child("timer").child(currentDateStr).get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        Integer savedCnt = dataSnapshot.getValue(Integer.class);
                        if (savedCnt != null) {
                            nCnt = savedCnt;
                            updateTimer();
                            Toast.makeText(TimerActivity.this, "Timer value loaded", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(TimerActivity.this, "No timer value found for today", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(TimerActivity.this, "Error loading timer value: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
