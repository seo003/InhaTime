package com.inhatc.inhatime;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

public class ToDoListActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ToDoListActivity";
    Fragment mainFragment;
    Button btnAdd = findViewById(R.id.btnAdd);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_to_do_list);

        mainFragment = new MainFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, mainFragment).commit();

        btnAdd.setOnClickListener(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onClick(View view) {
        if (view==btnAdd) {
            saveToDo();
            Toast.makeText(getApplicationContext(),"추가되었습니다", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveToDo() {

    }
}