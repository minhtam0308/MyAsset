package com.example.myasset;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.example.myasset.model.TaiKhoan;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditUserActivity extends AppCompatActivity {
    private EditText edtName, edtGender, edtDateOfBirth, edtPhone;
    private ImageView imgAvatar, imgEditIcon;
    private static final int PICK_IMAGE_REQUEST = 1;
    private byte[] selectedImageBytes = null;
    private ImageView btnDob;
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
        btnDob = findViewById(R.id.btn_dobEdit);

        // Load thông tin user
        loadUserInfo();

        //choose date
        btnDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int y = calendar.get(Calendar.YEAR);
                int m = calendar.get(Calendar.MONTH);
                int d = calendar.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(EditUserActivity.this, (view1,
                                                                                               year, month, day)->{
                    String s = day+"/"+(month+1)+"/"+year;
                    edtDateOfBirth.setText(s);
                }, y, m, d);
                datePickerDialog.show();
            }
        });

        // Nút Xác nhận
        btnConfirm.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String gender = edtGender.getText().toString().trim();
            String dob = edtDateOfBirth.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();

            if(validateAccount(name, dob)){
                boolean success = dbHelper.updateCurrentUserWithImage(name, gender, dob, phone, selectedImageBytes);
                if (success) {
                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditUserActivity.this, UserActivity.class);
                    startActivity(intent);
                    finish(); // đóng EditUserActivity
                } else {
                    Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, "Nhập thông tin không đúng định dạng!", Toast.LENGTH_SHORT).show();

            }



        });

        imgAvatar.setOnClickListener(v -> openImagePicker());
        imgEditIcon.setOnClickListener(v -> openImagePicker());

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            // Tạo Intent chuyển về UserActivity
            Intent intent = new Intent(EditUserActivity.this, UserActivity.class);
            startActivity(intent);
            finish(); // Kết thúc EditUserActivity để không quay lại khi nhấn back
        });

        imgAvatar.setClipToOutline(true);

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

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    imgAvatar.setImageBitmap(bitmap);
                    selectedImageBytes = bitmapToByteArray(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        // Giảm kích thước ảnh nếu quá lớn
        int maxSize = 800; // Chiều rộng/chiều cao tối đa (px)
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width > maxSize || height > maxSize) {
            float scale = Math.min((float) maxSize / width, (float) maxSize / height);
            width = Math.round(width * scale);
            height = Math.round(height * scale);
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        }

        // Nén ảnh xuống JPG với chất lượng vừa phải
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream); // 70% chất lượng để giảm dung lượng
        return stream.toByteArray();
    }

    public boolean validateAccount(String tenTaiKhoan, String ngaySinh) {
        // 1. Kiểm tra tên tài khoản
        if (tenTaiKhoan == null || tenTaiKhoan.trim().isEmpty()) {
            edtName.setError("Tên người dùng không đuợc để trống");
            return false;
        }
        if(!edtPhone.getText().toString().isEmpty()){
            if(edtPhone.getText().toString().length() < 10){
                edtPhone.setError("Tên người dùng không đuợc để trống");
                return false;
            }
        }

        if(ngaySinh != null || !ngaySinh.isEmpty()){
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false); // Bắt buộc đúng định dạng

            try {
                Date date = sdf.parse(ngaySinh);

                // So sánh với ngày hiện tại
                Date today = new Date();
                if (date.after(today)) {
                    edtDateOfBirth.setError( "Ngày sinh không được lớn hơn ngày hiện tại");
                    return false;
                }
            } catch (ParseException e) {
                edtDateOfBirth.setError( "Ngày sinh không đúng định dạng");
                return false;
            }
        }

        return true; // Hợp lệ
    }



}
