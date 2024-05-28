package com.inhatc.inhatime;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class ToDoListActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ToDoListActivity";
    Fragment mainFragment;
    Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);

        mainFragment = new MainFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, mainFragment).commit();

        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == btnAdd) {
            saveToDo();
            Toast.makeText(getApplicationContext(), "추가되었습니다", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveToDo() {
        // TODO: Add your save logic here
    }
}