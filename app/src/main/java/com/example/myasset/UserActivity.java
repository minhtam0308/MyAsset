package com.example.myasset;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myasset.model.TaiKhoan;
import com.example.myasset.model.TaiSan;

import java.io.IOException;
import java.util.ArrayList;

public class UserActivity extends AppCompatActivity {
    TextView tvValueName, tvValuePhone, tvValueGender, tvValueBirth;
    ImageView imgAvatar;
    DatabaseHelper dbHelper;

    //them (tam)
    private static final String ID_FILENAME = "MyAppPrefs";
    private LinearLayout navDanhMuc, navUser, navHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user);



        imgAvatar = findViewById(R.id.imgAvatar);
        imgAvatar.setClipToOutline(true);



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
            //them (tam)
            AlertDialog.Builder alert =new AlertDialog.Builder(UserActivity.this);
            alert.setTitle("Xác nhận");
            alert.setMessage("Bạn có chắc muốn đăng xuất không?");
            alert.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {;

                    // Xóa USER_ID trong SharedPreferences
                    SharedPreferences prefs = getSharedPreferences(ID_FILENAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.remove("USER_ID");
                    editor.apply();

                    // Tạo intent về LoginActivity
                    Intent intent = new Intent(UserActivity.this, LoginActivity.class);
                    // Xóa lịch sử để không thể back lại màn hình UserActivity sau khi logout
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            });
            alert.setNegativeButton("Không", null);
            alert.show();
        });


        tvValueName = findViewById(R.id.tvValueName);
        tvValuePhone = findViewById(R.id.tvValuePhone);
        tvValueGender = findViewById(R.id.tvValueGender);
        tvValueBirth = findViewById(R.id.tvValueBirth);

        dbHelper = new DatabaseHelper(this);
        loadUserInfo();

        //them Tam
        mapping();
        onClickNav();
    }
    private void loadUserInfo() {
        TaiKhoan taiKhoan = dbHelper.getCurrentUserObject();

        if (taiKhoan != null) {
            tvValueName.setText(taiKhoan.getTk());
            tvValuePhone.setText(taiKhoan.getSdt());
            tvValueGender.setText(taiKhoan.getGioitinh());
            tvValueBirth.setText(taiKhoan.getNgaysinh());

            if (taiKhoan.getAnhtk() != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(taiKhoan.getAnhtk(), 0, taiKhoan.getAnhtk().length);

                imgAvatar.setImageBitmap(bitmap);
            }
        }
    }

    //them Tam
    private void mapping(){
        navUser = findViewById(R.id.navUser);
        navDanhMuc = findViewById(R.id.navDanhMuc);
        navHome = findViewById(R.id.navHome);
    }

    private void onClickNav(){
        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        navDanhMuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserActivity.this, DanhMucActivity.class);
                startActivity(intent);
            }
        });
    }

}
