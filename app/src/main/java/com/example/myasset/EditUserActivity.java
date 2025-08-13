package com.example.myasset;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myasset.model.TaiKhoan;

public class EditUserActivity extends AppCompatActivity {
    private EditText edtName, edtGender, edtDateOfBirth, edtPhone;
    private ImageView imgAvatar, imgEditIcon;
    private DatabaseHelper dbHelper;
    private Button btnConfirm, btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user);

        dbHelper = new DatabaseHelper(this);

        // Ánh xạ view
        edtName = findViewById(R.id.edtName);
        edtGender = findViewById(R.id.edtGender);
        edtDateOfBirth = findViewById(R.id.edtDateOfBirth);
        edtPhone = findViewById(R.id.edtPhone);
        imgAvatar = findViewById(R.id.imgAvatar);
        imgEditIcon = findViewById(R.id.imgEditIcon);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnBack = findViewById(R.id.btnBack);

        // Load thông tin user
        loadUserInfo();

        // Nút Xác nhận
        btnConfirm.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String gender = edtGender.getText().toString().trim();
            String dob = edtDateOfBirth.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();

            if (dbHelper.updateCurrentUser(name, gender, dob, phone)) {
                Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
            }
        });




        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            // Tạo Intent chuyển về UserActivity
            Intent intent = new Intent(EditUserActivity.this, UserActivity.class);
            startActivity(intent);
            finish(); // Kết thúc EditUserActivity để không quay lại khi nhấn back
        });
    }
    private void loadUserInfo() {
        TaiKhoan taiKhoan = dbHelper.getCurrentUserObject();

        if (taiKhoan != null) {
            edtName.setText(taiKhoan.getTk());
            edtGender.setText(taiKhoan.getGioitinh());
            edtDateOfBirth.setText(taiKhoan.getNgaysinh());
            edtPhone.setText(taiKhoan.getSdt());

            // Hiển thị ảnh nếu có
            if (taiKhoan.getAnhtk() != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(
                        taiKhoan.getAnhtk(), 0, taiKhoan.getAnhtk().length
                );
                imgAvatar.setImageBitmap(bitmap);
            }
        }
    }
}
