package com.example.myasset;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myasset.model.DanhMuc;

import java.util.ArrayList;

public class EditDanhmucActivity extends AppCompatActivity {
    private EditText tenDanhMuc;
    private TextView btnLuu;
    private ImageView btnExit;
    private DanhMuc danhMuc;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_danhmuc);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mapping();
        dbHelper = new DatabaseHelper(this);
        btnLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tenDanhMuc.getText().toString().isEmpty()){
                    tenDanhMuc.setError("Tên danh mục không được để trống");
                    tenDanhMuc.requestFocus();
                    Toast.makeText(EditDanhmucActivity.this, "Tên danh mục không được để trống", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(danhMuc != null){
                    String danhmucNew = tenDanhMuc.getText().toString();
                    if(dbHelper.updateDanhMuc(danhMuc.getIddanhmuc(), danhmucNew)){
                        Toast.makeText(EditDanhmucActivity.this, "Sửa thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        tenDanhMuc.setError("Tên danh mục đã tồn tại");
                        tenDanhMuc.requestFocus();
                        Toast.makeText(EditDanhmucActivity.this, "Danh mục đã tồn tại", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if(intent.getSerializableExtra("EditDM") != null){
            danhMuc = (DanhMuc) intent.getSerializableExtra("EditDM");
            tenDanhMuc.setText(danhMuc.getTendm());
        }
    }
    private void mapping(){
        btnLuu = findViewById(R.id.editdanhmuc_btnsua);
        tenDanhMuc = findViewById(R.id.danhmuc_ten);
        btnExit = findViewById(R.id.editdm_exit);
    }
}