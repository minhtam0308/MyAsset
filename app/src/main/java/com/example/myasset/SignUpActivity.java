package com.example.myasset;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignUpActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> launcher;
    private TextView btnLoginNavigate, errorMess;
    private Button btnSignUp;
    private EditText edtTenTK, edtTK, edtMK, edtMK2;

    DatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        dbHelper = new DatabaseHelper(this);
        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), activityResult -> {
                    if (activityResult.getResultCode() == RESULT_OK && activityResult.getData() !=null) {
                    }
                }
        );
        mapping();
        btnLoginNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                launcher.launch(intent);
            }
        });
        btnSignUp.setOnClickListener(v -> {
            String tenTK = edtTenTK.getText().toString().trim();
            String tk = edtTK.getText().toString().trim();
            String mk = edtMK.getText().toString().trim();
            if(checkSignUp()){
                boolean success = dbHelper.registerUser(tenTK, tk, mk);
                if (success) {
                    Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Tài khoản đã tồn tại", Toast.LENGTH_SHORT).show();
                    errorMess.setText("Tài khoản đã tồn tại!");
                }
            }
        });
    }

    private boolean checkSignUp(){
        String tenTK = edtTenTK.getText().toString().trim();
        String tk = edtTK.getText().toString().trim();
        String mk = edtMK.getText().toString().trim();
        String mk2 = edtMK2.getText().toString().trim();

        if(tenTK.isEmpty()){
            edtTenTK.setError("Tên tài khoản không để trống");
        }
        if(tk.isEmpty()){
            edtTK.setError("Tên tài khoản không để trống");
        }
        if(mk.isEmpty()){
            edtMK.setError("Tên tài khoản không để trống");
        }
        if(mk2.isEmpty()){
            edtMK2.setError("Tên tài khoản không để trống");
        }
        if (tenTK.isEmpty() || tk.isEmpty() || mk.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            errorMess.setText("Vui lòng nhập đủ thông tin");
            return false;
        }
        if(!mk2.equals(mk)){
            edtMK2.setError("Mật khẩu không trùng khớp");
            errorMess.setText("Mật khẩu không trùng khớp");
            return false;
        }
        return true;

    }
    private void mapping(){
        btnLoginNavigate = findViewById(R.id.btn_dangnhap_navigate);
        btnSignUp = findViewById(R.id.btn_signup);
        edtTenTK =  findViewById(R.id.edt_tentkSignup);
        edtTK = findViewById(R.id.edt_tksignup);
        edtMK = findViewById(R.id.edt_matkhauSignUp);
        edtMK2 = findViewById(R.id.edt_matkhau2SignUp);
        errorMess = findViewById(R.id.tv_errorSignup);
    }
}