package com.example.myasset;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class EditUserActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user); // layout chỉnh sửa bạn đã tạo

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            // Tạo Intent chuyển về UserActivity
            Intent intent = new Intent(EditUserActivity.this, UserActivity.class);
            startActivity(intent);
            finish(); // Kết thúc EditUserActivity để không quay lại khi nhấn back
        });
    }
}
