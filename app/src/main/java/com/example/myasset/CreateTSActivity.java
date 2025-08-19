package com.example.myasset;

import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myasset.model.DanhMuc;
import com.example.myasset.model.TaiSan;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CreateTSActivity extends AppCompatActivity {

    private ImageView createImgTS;
    private EditText createTenTS, createSoLuongTS,
            createMoTaTS, createGiaTriTS,
            createVitriTS, createGhiChuTS,
            createNgayMua, createBaoHanhStartTS,createBaoHanhEndTS;
    private TextView btnLuuTS, btnThuVien, btnChup;
    private ImageView btnNgayMuaTS, btnBaoHanhStart, btnBaoHanhEnd, btnExit;
    private Date today = new Date();
    private Spinner createDanhmucTS, createTinhTrangts;
    private boolean check = false;
    private  ArrayList<DanhMuc> danhmucs = new ArrayList<>();
    ActivityResultLauncher<Intent> imagePickerLauncher;
    private TaiSan taiSan;
    DatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_tsactivity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mapping();
        dbHelper = new DatabaseHelper(this);
        settingSprinnerDanhMucTS();
        settingSprinnerTinhTrangTS();
        settingChoseDate();
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            createImgTS.setImageURI(selectedImageUri);
                        }
                    }
                }
        );

        btnThuVien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // Android 13+
                    if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 100);
                    } else {
                        openImagePicker();
                        check=true;
                    }
                } else {
                    // Android 12 trở xuống
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                    } else {
                        openImagePicker();
                        check=true;
                    }
                }
            }
        });

        btnChup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraintent = new Intent(ACTION_IMAGE_CAPTURE);
                if (ActivityCompat.checkSelfPermission(CreateTSActivity.this,
                        android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(CreateTSActivity.this,new
                            String[]{android.Manifest.permission.CAMERA}, 1);
                    return;
                }
                startActivityForResult(cameraintent,99);
            }

        });
        btnLuuTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //validate tên tài sản
                if(createTenTS.getText().toString().isEmpty()){
                    createTenTS.setError("Tên tài sản không được để trống");
                    createTenTS.requestFocus();
                    Toast.makeText(CreateTSActivity.this, "Tên tài sản không được để trống", Toast.LENGTH_SHORT).show();
                    return;
                }
                //validate số lượng tài sản
                if(createSoLuongTS.getText() == null || !createSoLuongTS.getText().toString().matches("\\d+")){
                    createSoLuongTS.setError("Số tài sản là một số");
                    createSoLuongTS.requestFocus();
                    Toast.makeText(CreateTSActivity.this, "Số lượng tài sản không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
                //validate giá trị tài sản
                if(!isValidMoney(createGiaTriTS.getText().toString())){
                    createGiaTriTS.setError("Giá trị tài sản là một số");
                    createGiaTriTS.requestFocus();
                    Toast.makeText(CreateTSActivity.this, "Giá trị tài sản không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }

                //validate ngày mua tài sản
                if(!createNgayMua.getText().toString().isEmpty()){
                    if(!isValidDate(createNgayMua.getText().toString())){
                        createNgayMua.setError("Ngày mua không hợp lệ");
                        createNgayMua.requestFocus();
                        Toast.makeText(CreateTSActivity.this, "Ngày mua tài sản không hợp lệ", Toast.LENGTH_SHORT).show();
                        return;
                    }else {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        sdf.setLenient(false);
                        try{
                            Date input = sdf.parse(createNgayMua.getText().toString());
                            today = sdf.parse(sdf.format(today));
                            if(input.compareTo(today) > 0){
                                createNgayMua.setError("Ngày mua không hợp lệ");
                                createNgayMua.requestFocus();
                                Toast.makeText(CreateTSActivity.this, "Ngày mua tài sản không hợp lệ", Toast.LENGTH_SHORT).show();
                                return;
                            }else{
                                createNgayMua.setError(null);
                            }
                        }catch (Exception ex){
                            Toast.makeText(CreateTSActivity.this, "Ngày mua tài sản không hợp lệ", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    }
                }


                //validate ngày bắt đầu và kết thúc bảo hành tài sản
                if(!createBaoHanhStartTS.getText().toString().isEmpty() || !createBaoHanhEndTS.getText().toString().isEmpty()){
                    if(!isValidDate(createBaoHanhStartTS.getText().toString())){
                        createBaoHanhStartTS.setError("Ngày bắt đầu bảo hành không hợp lệ");
                        createBaoHanhStartTS.requestFocus();
                        Toast.makeText(CreateTSActivity.this, "Ngày bắt đầu bảo hành không hợp lệ", Toast.LENGTH_SHORT).show();
                        return;
                    }else {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        sdf.setLenient(false);
                        try{
                            Date input = sdf.parse(createBaoHanhStartTS.getText().toString());
                            today = sdf.parse(sdf.format(today));
                            if(input.compareTo(today) > 0){
                                createBaoHanhStartTS.setError("Ngày bắt đầu bảo hành không hợp lệ");
                                createBaoHanhStartTS.requestFocus();
                                Toast.makeText(CreateTSActivity.this, "Ngày bắt đầu bảo hành không hợp lệ", Toast.LENGTH_SHORT).show();
                                return;
                            }else{
                                createBaoHanhStartTS.setError(null);

                            }
                        }catch (Exception ex){
                            Toast.makeText(CreateTSActivity.this, "Ngày bắt đầu bảo hành không hợp lệ", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    }

                    if(!isValidDate(createBaoHanhEndTS.getText().toString())){
                        createBaoHanhEndTS.setError("Ngày hết hạn hành không hợp lệ");
                        createBaoHanhEndTS.requestFocus();
                        Toast.makeText(CreateTSActivity.this, "Ngày hết hạn bảo hành không hợp lệ", Toast.LENGTH_SHORT).show();
                        return;
                    }else {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        sdf.setLenient(false);
                        try{
                            Date input = sdf.parse(createBaoHanhEndTS.getText().toString());
                            Date baoHanhStart = sdf.parse(createBaoHanhStartTS.getText().toString());
                            if(input.compareTo(baoHanhStart) < 0){
                                createBaoHanhEndTS.setError("Ngày hết hạn bảo hành không hợp lệ");
                                createBaoHanhEndTS.requestFocus();
                                Toast.makeText(CreateTSActivity.this, "Ngày hết hạn bảo hành không hợp lệ", Toast.LENGTH_SHORT).show();
                                return;
                            }else{
                                createBaoHanhEndTS.setError(null);
                            }
                        }catch (Exception ex){
                            Toast.makeText(CreateTSActivity.this, "Ngày hết hạn bảo hành không hợp lệ", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    }
                }

                TaiSan savedTS = gatherTaiSan();
                if(dbHelper.insertTaiSan(savedTS)){
//                    Intent tao = new Intent(CreateTSActivity.this, ListTaiSan.class);
//                    tao.putExtra("savedTS", savedTS);
                    Toast.makeText(CreateTSActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
//                    startActivityForResult(tao, RESULT_OK);
                    finish();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable
    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 99 && resultCode == Activity.RESULT_OK)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            createImgTS.setImageBitmap(photo);
            check = true;
        }

    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }


    private TaiSan gatherTaiSan(){
        TaiSan result = new TaiSan();
        if(check){
            Bitmap photo = ((BitmapDrawable) createImgTS.getDrawable()).getBitmap();
            photo = resizeBitmap(photo, 800, 800);
            // Chuyển bitmap -> byte[]
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 70, stream); // PNG hoặc JPEG
            byte[] imageBytes = stream.toByteArray();
            result.setAnhts(imageBytes);
        }else{
            result.setAnhts(null);
        }
        result.setTents(createTenTS.getText().toString());
        result.setIdtk(1);
        result.setIdts(-1);
        result.setMota(createMoTaTS.getText().toString());
        DanhMuc selectedDanhMuc = (DanhMuc) createDanhmucTS.getSelectedItem();
        result.setIddanhmuc(selectedDanhMuc.getIddanhmuc());
        result.setGiatri(Integer.parseInt(createGiaTriTS.getText().toString()));
        result.setNgaymua(createNgayMua.getText().toString());
        result.setTinhtrang(createTinhTrangts.getSelectedItem().toString());
        result.setVitri(createVitriTS.getText().toString());
        result.setGhichu(createGhiChuTS.getText().toString());
        result.setBaohanhStart(createBaoHanhStartTS.getText().toString());
        result.setBaohanhEnd(createBaoHanhEndTS.getText().toString());
        result.setSoluong(Integer.parseInt(createSoLuongTS.getText().toString()));
        return result;
    }
    private Bitmap resizeBitmap(Bitmap original, int maxWidth, int maxHeight) {
        int width = original.getWidth();
        int height = original.getHeight();

        // Giữ tỷ lệ ảnh
        float ratio = (float) width / (float) height;
        if (ratio > 1) {
            width = maxWidth;
            height = (int) (width / ratio);
        } else {
            height = maxHeight;
            width = (int) (height * ratio);
        }

        return Bitmap.createScaledBitmap(original, width, height, true);
    }
    private boolean isValidMoney(String moneyStr) {
        if (moneyStr == null || moneyStr.trim().isEmpty()) {
            return false; // Không được để trống
        }
        try {
            double money = Double.parseDouble(moneyStr.replace(",", "")); // bỏ dấu phẩy nếu có
            return money > 0; // Chỉ hợp lệ nếu > 0
        } catch (NumberFormatException e) {
            return false; // Không phải số
        }
    }

    public boolean isValidDate(String day){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        try{
            Date date = sdf.parse(day);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public void settingSprinnerDanhMucTS() {
        danhmucs =  (ArrayList<DanhMuc>) dbHelper.getAllDanhMuc();
        ArrayAdapter<DanhMuc> adapter = new ArrayAdapter<DanhMuc>(this,
                android.R.layout.simple_spinner_dropdown_item,
                danhmucs);

        createDanhmucTS.setAdapter(adapter);
        createDanhmucTS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DanhMuc selectedItem = danhmucs.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không chọn gì
            }
        });
        createDanhmucTS.setDropDownVerticalOffset(50);
        createDanhmucTS.setSelection(0);
    }
    public void settingSprinnerTinhTrangTS() {
        ArrayList<String> tinhtrangs = new ArrayList<>();
        tinhtrangs.add("Đang sử đụng");
        tinhtrangs.add("Đã thanh lý");
        tinhtrangs.add("Đã hỏng");
        tinhtrangs.add("Đã mất");
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tinhtrangs);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                tinhtrangs);
        createTinhTrangts.setAdapter(adapter);
        createTinhTrangts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = tinhtrangs.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không chọn gì
            }
        });
        createTinhTrangts.setDropDownVerticalOffset(50);
        createTinhTrangts.setSelection(0);
    }

    private void settingChoseDate(){

        btnNgayMuaTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int y = calendar.get(Calendar.YEAR);
                int m = calendar.get(Calendar.MONTH);
                int d = calendar.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateTSActivity.this, (view1,
                                                                                             year, month, day)->{
                    String s = day+"/"+(month+1)+"/"+year;
                    createNgayMua.setText(s);
                }, y, m, d);
                datePickerDialog.show();
            }
        });

        btnBaoHanhStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int y = calendar.get(Calendar.YEAR);
                int m = calendar.get(Calendar.MONTH);
                int d = calendar.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateTSActivity.this, (view1,
                                                                                                 year, month, day)->{
                    String s = day+"/"+(month+1)+"/"+year;
                    createBaoHanhStartTS.setText(s);
                }, y, m, d);
                datePickerDialog.show();
            }
        });
        btnBaoHanhEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int y = calendar.get(Calendar.YEAR);
                int m = calendar.get(Calendar.MONTH);
                int d = calendar.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateTSActivity.this, (view1,
                                                                                                 year, month, day)->{
                    String s = day+"/"+(month+1)+"/"+year;
                    createBaoHanhEndTS.setText(s);
                }, y, m, d);
                datePickerDialog.show();
            }
        });
    }


    private void mapping(){
        createImgTS = findViewById(R.id.create_img_ts);
        createTenTS = findViewById(R.id.create_tents);
        createSoLuongTS = findViewById(R.id.create_soluongts);
        createMoTaTS = findViewById(R.id.create_mota);
        createDanhmucTS = findViewById(R.id.create_danhmucts);
        createGiaTriTS = findViewById(R.id.create_giatrits);
        createTinhTrangts = findViewById(R.id.create_tinhtrangts);
        createVitriTS = findViewById(R.id.create_vitrits);
        createGhiChuTS = findViewById(R.id.create_ghichuts);
        createNgayMua = findViewById(R.id.create_ngaymuats);
        createBaoHanhStartTS = findViewById(R.id.create_baohanhstartts);
        createBaoHanhEndTS = findViewById(R.id.create_baohanhendts);
        btnLuuTS = findViewById(R.id.create_btnluu);
        btnNgayMuaTS = findViewById(R.id.create_btndatemua);
        btnBaoHanhStart = findViewById(R.id.create_btndatebaohanhStart);
        btnBaoHanhEnd = findViewById(R.id.create_btndatebaohanhend);
        btnThuVien = findViewById(R.id.create_laytutv);
        btnChup = findViewById(R.id.create_chup);
        btnExit = findViewById(R.id.create_btnExit);
    }
}