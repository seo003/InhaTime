package com.inhatc.inhatime;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class TimerActivity extends AppCompatActivity {

    // Timer 및 데이터베이스 관련 변수 선언
    private Timer timerCall;
    private int nCnt;
    private TextView txtHour, txtMinute, txtSecond;
    private Button btnStart, btnStop;
    private Handler handler = new Handler();

    private static final String DB_URL = "jdbc:mysql://localhost:3306/InhatcTime";
    private static final String DB_USER = "inhatcM";
    private static final String DB_PASSWORD = "MobileProj!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // 엣지 투 엣지 레이아웃 활성화
        setContentView(R.layout.activity_timer);

        nCnt = 0; // 타이머 카운트 초기화

        // UI 요소 초기화
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        txtHour = findViewById(R.id.txtHour);
        txtMinute = findViewById(R.id.txtMinute);
        txtSecond = findViewById(R.id.txtSecond);

        // Start 버튼 클릭 이벤트 설정
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimer();
            }
        });

        // Stop 버튼 클릭 이벤트 설정
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTimer();
            }
        });

        // 시스템 바에 따른 인셋 적용
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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

    // 타이머 값을 데이터베이스에 저장
    private void saveTimerValue() {
        String currentDateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new java.util.Date());
        Date currentDate = Date.valueOf(currentDateStr); // java.sql.Date로 변환
        long millis = (nCnt / 3600) * 3600000L + ((nCnt % 3600) / 60) * 60000L + (nCnt % 60) * 1000L; // 밀리초로 변환
        Time currentTime = new Time(millis); // java.sql.Time으로 변환

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // 먼저 데이터가 존재하는지 확인
            String checkQuery = "SELECT COUNT(*) FROM timer WHERE date = ?";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                checkStatement.setDate(1, currentDate);
                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next() && resultSet.getInt(1) > 0) {
                        // 데이터가 존재하면 UPDATE
                        String updateQuery = "UPDATE timer SET time = ? WHERE date = ?";
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                            updateStatement.setTime(1, currentTime);
                            updateStatement.setDate(2, currentDate);
                            int rowsUpdated = updateStatement.executeUpdate();
                            if (rowsUpdated > 0) {
                                runOnUiThread(() -> Toast.makeText(this, "Timer value updated", Toast.LENGTH_SHORT).show());
                            }
                        }
                    } else {
                        // 데이터가 존재하지 않으면 INSERT
                        String insertQuery = "INSERT INTO timer (date, time) VALUES (?, ?)";
                        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                            insertStatement.setDate(1, currentDate);
                            insertStatement.setTime(2, currentTime);
                            int rowsInserted = insertStatement.executeUpdate();
                            if (rowsInserted > 0) {
                                runOnUiThread(() -> Toast.makeText(this, "Timer value saved", Toast.LENGTH_SHORT).show());
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(this, "Error saving timer value: " + e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    // 데이터베이스에서 타이머 값을 불러옴
    private void loadTimerValue() {
        String currentDateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new java.util.Date());
        Date currentDate = Date.valueOf(currentDateStr); // java.sql.Date로 변환
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT time FROM timer WHERE date = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setDate(1, currentDate);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        Time savedTime = resultSet.getTime("time");
                        int hours = savedTime.getHours();
                        int minutes = savedTime.getMinutes();
                        int seconds = savedTime.getSeconds();
                        nCnt = hours * 3600 + minutes * 60 + seconds; // 초 단위로 변환
                        updateTimer();
                        runOnUiThread(() -> Toast.makeText(this, "Timer value loaded", Toast.LENGTH_SHORT).show());
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "No timer value found for today", Toast.LENGTH_SHORT).show());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(this, "Error loading timer value: " + e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }
}
