package com.example.myasset;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myasset.model.DanhMuc;
import com.example.myasset.model.TaiSan;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DetailTSActivity extends AppCompatActivity {
    private ImageView imgTSDetail;
    private TextView nameDeatil, danhMucDetail, giatriDetail,
            ngayMuaDetail, trangThaiDetail,
            baohanhStart, baohanhEnd, vitriDetail,
    ghichuDetail, motaDetail, soluongDetail, trangthaiBaoHanh;
    private AppCompatButton editButton, deleteButton;
    private AppCompatImageButton exitButton;
    private ActivityResultLauncher<Intent> launcher;
    TaiSan taiSan;
    DatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_tsactivity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mapping();
        dbHelper = new DatabaseHelper(this);
        Intent intent = getIntent();
        if(intent.getSerializableExtra("TSDetail") != null) {
            taiSan = (TaiSan) intent.getSerializableExtra("TSDetail");
        }
        setTS();
//        Toast.makeText(this, "chạy", Toast.LENGTH_SHORT).show();

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sua = new Intent(DetailTSActivity.this, EditTSActivity.class);
                sua.putExtra("EditTS", taiSan);
                launcher.launch(sua);
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert =new AlertDialog.Builder(DetailTSActivity.this);
                alert.setTitle("Xác nhận");
                alert.setMessage("Bạn muốn xóa tài sản này?");
                alert.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {;
                        if(dbHelper.deleteTaiSan(taiSan.getIdts())){
                            finish();
                            Toast.makeText(DetailTSActivity.this, "xóa thành công", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(DetailTSActivity.this, "Không tìm thấy id tài sản", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alert.setNegativeButton("Không", null);
                alert.show();
            }
        });

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), activityResult -> {
                    if (activityResult.getResultCode() == RESULT_OK && activityResult.getData() !=null) {

                        if(activityResult.getData().getSerializableExtra("editedTS") != null){
//                            TaiSan taiSan1 =(TaiSan) activityResult.getData().getSerializableExtra("editedTS");
                            Toast.makeText(this, "Sửa thành công", Toast.LENGTH_SHORT).show();

                        }
                    }
                }
        );
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(taiSan != null){
            taiSan = dbHelper.getTaiSanById(taiSan.getIdts());
            setTS();
        }

    }
    private void setTS(){

            if (taiSan != null) {
                DanhMuc danhMuc = dbHelper.getDanhMucById(taiSan.getIddanhmuc());
                nameDeatil.setText(taiSan.getTents());
                danhMucDetail.setText(danhMuc.getTendm());
                giatriDetail.setText(formatVND(taiSan.getGiatri()));
                ngayMuaDetail.setText(taiSan.getNgaymua());
                trangThaiDetail.setText(taiSan.getTinhtrang());
                vitriDetail.setText(taiSan.getVitri());
                motaDetail.setText(taiSan.getMota());
                ghichuDetail.setText(taiSan.getGhichu());
                baohanhStart.setText(taiSan.getBaohanhStart());
                baohanhEnd.setText(taiSan.getBaohanhEnd());
                soluongDetail.setText(String.valueOf(taiSan.getSoluong()));
                long nghet = soNgayConLai(taiSan.getBaohanhEnd());
                if(nghet > 0){
                    trangthaiBaoHanh.setText("Còn "+nghet+ " ngày");
                    trangthaiBaoHanh.setBackgroundColor(Color.BLUE);
                } else if (nghet == 0) {
                    trangthaiBaoHanh.setText("Ngày hôm nay");
                    trangthaiBaoHanh.setBackgroundColor(Color.YELLOW);
                }else{
                    trangthaiBaoHanh.setText("Đã hết hạn bảo hành");
                    trangthaiBaoHanh.setBackgroundColor(Color.RED);
                }

                if (taiSan.getAnhts() != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(taiSan.getAnhts(), 0, taiSan.getAnhts().length);
                    imgTSDetail.setImageBitmap(bitmap);
                }
            }

    }

    public long soNgayConLai(String ngayHetHan) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date hetHan = sdf.parse(ngayHetHan);
            Date today = new Date();

            // Lấy chênh lệch thời gian tính bằng millis
            long diffInMillis = hetHan.getTime() - today.getTime();

            // Đổi millis thành ngày
            return TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
            return -1; // lỗi parse date
        }
    }

    public boolean daHetHan(String ngayHetHan) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date dateHetHan = sdf.parse(ngayHetHan);
            return new Date().after(dateHetHan);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
    public String formatVND(long amount) {
        NumberFormat vndFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return vndFormat.format(amount);
    }
    private void mapping(){
        imgTSDetail = findViewById(R.id.iv_asset_image);
        nameDeatil = findViewById(R.id.tv_asset_name);
        danhMucDetail= findViewById(R.id.tv_category);
        giatriDetail = findViewById(R.id.tv_asset_value);
        ngayMuaDetail =findViewById(R.id.tv_purchase_date);
        trangThaiDetail = findViewById(R.id.tv_status);
        vitriDetail = findViewById(R.id.tv_location);
        motaDetail = findViewById(R.id.tv_description);
        baohanhStart = findViewById(R.id.tv_warranty_start);
        baohanhEnd = findViewById(R.id.tv_warranty_end);
        ghichuDetail = findViewById(R.id.tv_notes);
        editButton = findViewById(R.id.btn_edit_asset);
        deleteButton = findViewById(R.id.btn_delete_asset);
        exitButton = findViewById(R.id.btn_back);
        soluongDetail = findViewById(R.id.tv_asset_soluong);
        trangthaiBaoHanh = findViewById(R.id.tv_warranty_status);

    }
}