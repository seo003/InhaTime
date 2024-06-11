package com.inhatc.inhatime;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.SubMenu;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CalendarActivity extends AppCompatActivity {

    CalendarView calendarView;
    TextView today;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 메뉴 항목 생성
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
        setContentView(R.layout.activity_calendar);

        today = findViewById(R.id.today);
        calendarView = findViewById(R.id.calendarView);

        // 날짜 변환
        DateFormat formatter = new SimpleDateFormat("yyyy년M월dd일");
        Date date = new Date(calendarView.getDate());
        today.setText(formatter.format(date));

        // 날짜 클릭 리스너
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // 선택된 날짜를 특정 형식으로 변환
                String day = year + "년" + (month + 1) + "월" + dayOfMonth + "일";
                today.setText(day);

                // 인텐트를 사용하여 다른 액티비티로 이동
                Intent intent = new Intent(CalendarActivity.this, CalendarViewActivity.class);
                intent.putExtra("selectedDate", day);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}