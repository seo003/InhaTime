package com.inhatc.inhatime;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    TextView ojbTxtTitle;    //InhaTime Title

    @Override
    public boolean onPrepareOptionsMenu( Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SubMenu mnuCalendar = menu.addSubMenu("Calendar");
        SubMenu mnuTodolist = menu.addSubMenu("To Do List");
        SubMenu mnuTimer = menu.addSubMenu("Timer");
        SubMenu mnuFriends = menu.addSubMenu("Friends");

        // Calendar 메뉴 항목 클릭 리스너 설정
        mnuCalendar.getItem().setOnMenuItemClickListener(item -> {
            // CalendarActivity로 이동
            startActivity(new Intent(this, Calendar.class));
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

        //

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ojbTxtTitle = (TextView)findViewById(R.id.txtTitle);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}