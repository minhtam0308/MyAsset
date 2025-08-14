package com.example.myasset;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {
    private TextView btnSignupNavigate, tvError;
    private ActivityResultLauncher<Intent> launcher;
    private AppCompatButton btnLogin;
    private EditText edtUsername, edtPassword;
    private static final String ID_FILENAME = "MyAppPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
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
        btnSignupNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                launcher.launch(intent);
            }
        });
        btnLogin.setOnClickListener(v -> {
            String user = edtUsername.getText().toString().trim();
            String pass = edtPassword.getText().toString().trim();

            if (user.isEmpty()) {
                tvError.setText("Vui lòng nhập đủ thông tin!");
                edtUsername.setError("Tài khoản không được để trống");
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            if(pass.isEmpty()){
                tvError.setText("Vui lòng nhập đủ thông tin!");
                edtPassword.setError("Mật khẩu không được để trống");
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseHelper dbHelper = new DatabaseHelper(this);
            int userId = dbHelper.checkLogin(user, pass);

            if (userId != -1) {
                // Lưu vào SharedPreferences
                SharedPreferences prefs = getSharedPreferences(ID_FILENAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("USER_ID", userId);
                editor.apply();

                Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                // Chuyển sang màn hình chính
                startActivity(new Intent(this, UserActivity.class));
                finish();
            } else {
                tvError.setText("Sai tài khoản hoặc mật khẩu!");
                Toast.makeText(this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void mapping(){

        btnSignupNavigate = findViewById(R.id.btn_dangky_navigate);
        btnLogin = findViewById(R.id.btn_login);
        edtUsername = findViewById(R.id.edt_tkLogin);
        edtPassword = findViewById(R.id.edt_matkhauLogin);
        tvError = findViewById(R.id.tv_error);
    }
}