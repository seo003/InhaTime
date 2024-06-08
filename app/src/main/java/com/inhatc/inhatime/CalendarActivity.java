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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calendar);

        today = findViewById(R.id.today);
        calendarView = findViewById(R.id.calendarView);

        // 날짜 변환
        DateFormat formatter = new SimpleDateFormat("yyyy년 MM월 dd일");
        Date date = new Date(calendarView.getDate());
        today.setText(formatter.format(date));

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String day;
                day = year + "년" + (month + 1) + "월" + dayOfMonth + "일";
                today.setText(day);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}