package com.example.myasset;

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

public class CreateDanhMuc extends AppCompatActivity {

    private EditText tenDanhMuc;
    private TextView btnThem;
    private ImageView btnExit;
    DatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_danh_muc);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mapping();
        dbHelper = new DatabaseHelper(this);
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //validate tên dm
                if(tenDanhMuc.getText().toString().isEmpty()){
                    tenDanhMuc.setError("Tên danh mục không được để trống");
                    tenDanhMuc.requestFocus();
                    Toast.makeText(CreateDanhMuc.this, "Tên danh mục không được để trống", Toast.LENGTH_SHORT).show();
                    return;
                }
                DanhMuc danhmucNew = gatherDanhMuc();
                if(dbHelper.insertDanhMuc(danhmucNew.getTendm())){
                    Toast.makeText(CreateDanhMuc.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    tenDanhMuc.setError("Tên danh mục đã tồn tại");
                    tenDanhMuc.requestFocus();
                    Toast.makeText(CreateDanhMuc.this, "Danh mục đã tồn tại", Toast.LENGTH_SHORT).show();

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
    private DanhMuc gatherDanhMuc() {
        DanhMuc result = new DanhMuc();
        result.setTendm(tenDanhMuc.getText().toString());
        return result;
    }
    private void mapping(){
        btnThem = findViewById(R.id.danhmuc_btnthem);
        tenDanhMuc = findViewById(R.id.danhmuc_ten);
        btnExit = findViewById(R.id.createdm_exit);
    }

}