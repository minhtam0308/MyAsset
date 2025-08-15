package com.example.myasset;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private ImageView btnStart;
    private ActivityResultLauncher<Intent> launcher;
    private static final String ID_FILENAME = "MyAppPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.createDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SharedPreferences prefs = getSharedPreferences(ID_FILENAME, MODE_PRIVATE);
        int userId = prefs.getInt("USER_ID", -1);
        if (userId != -1) {
            // Đã đăng nhập → chuyển thẳng sang MainActivity
            startActivity(new Intent(this, HomeActivity.class));
            finish(); // Không cho quay lại màn hình login
            return;
        }
        // 2. Nếu chưa đăng nhập thì hiển thị layout login
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), activityResult -> {
                    if (activityResult.getResultCode() == RESULT_OK && activityResult.getData() !=null) {
                    }
                }
        );
        mapping();
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                launcher.launch(intent);
            }
        });
    }

    private void mapping(){
        btnStart = findViewById(R.id.btn_start_navigate);
    }
}