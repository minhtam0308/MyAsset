package com.example.myasset;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class UserActivity extends AppCompatActivity {
    TextView tvValueName, tvValuePhone, tvValueGender, tvValueBirth;
    DatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user);

        dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.createDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Button btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(v -> {
            // Tạo intent để chuyển sang EditUserActivity
            Intent intent = new Intent(UserActivity.this, EditUserActivity.class);
            startActivity(intent);
        });

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            // Tạo intent về LoginActivity
            Intent intent = new Intent(UserActivity.this, LoginActivity.class);
            // Xóa lịch sử để không thể back lại màn hình UserActivity sau khi logout
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });


        tvValueName = findViewById(R.id.tvValueName);
        tvValuePhone = findViewById(R.id.tvValuePhone);
        tvValueGender = findViewById(R.id.tvValueGender);
        tvValueBirth = findViewById(R.id.tvValueBirth);

        dbHelper = new DatabaseHelper(this);
        loadUserInfo();
    }
    private void loadUserInfo() {
        Cursor cursor = dbHelper.getCurrentUser();
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("tk"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("sdt"));
            String gender = cursor.getString(cursor.getColumnIndexOrThrow("gioitinh"));
            String birth = cursor.getString(cursor.getColumnIndexOrThrow("ngaysinh"));

            tvValueName.setText(name);
            tvValuePhone.setText(phone);
            tvValueGender.setText(gender);
            tvValueBirth.setText(birth);
        }
        if (cursor != null) cursor.close();
    }
}
