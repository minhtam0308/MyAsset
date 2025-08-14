package com.example.myasset;

import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class EditTSActivity extends AppCompatActivity {

    private ImageView editImgTS;
    private EditText editTenTS, editSoLuongTS,
            editMoTaTS, editGiaTriTS,
            editVitriTS, editGhiChuTS,
            editNgayMua, editBaoHanhStartTS,editBaoHanhEndTS;
    private TextView btnLuuTS, btnThuVien, btnChup;
    private ImageView btnNgayMuaTS, btnBaoHanhStart, btnBaoHanhEnd, btnExit;
    private Date today = new Date();
    private Spinner editDanhmucTS, editTinhTrangts;
    private boolean check = false;
    private ArrayList<DanhMuc> danhmucs = new ArrayList<>();
    ActivityResultLauncher<Intent> imagePickerLauncher;
    private TaiSan taiSan;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_tsactivity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mapping();
        SetDisplayaiSan();
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
                            editImgTS.setImageURI(selectedImageUri);
                            check=true;

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
                    }
                } else {
                    // Android 12 trở xuống
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                    } else {
                        openImagePicker();
                    }
                }
            }
        });

        btnChup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraintent = new Intent(ACTION_IMAGE_CAPTURE);
                if (ActivityCompat.checkSelfPermission(EditTSActivity.this,
                        android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(EditTSActivity.this,new
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
                if(editTenTS.getText().toString().isEmpty()){
                    editTenTS.setError("Tên tài sản không được để trống");
                    editTenTS.requestFocus();
                    Toast.makeText(EditTSActivity.this, "Tên tài sản không được để trống", Toast.LENGTH_SHORT).show();
                    return;
                }
                //validate số lượng tài sản
                if(editSoLuongTS.getText() == null || !editSoLuongTS.getText().toString().matches("\\d+")){
                    editSoLuongTS.setError("Số tài sản là một số");
                    editSoLuongTS.requestFocus();
                    Toast.makeText(EditTSActivity.this, "Số lượng tài sản không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
                //validate giá trị tài sản
                if(!isValidMoney(editGiaTriTS.getText().toString())){
                    editGiaTriTS.setError("Giá trị tài sản là một số");
                    editGiaTriTS.requestFocus();
                    Toast.makeText(EditTSActivity.this, "Giá trị tài sản không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }

                //validate ngày mua tài sản
                if(!editNgayMua.getText().toString().isEmpty()){
                    if(!isValidDate(editNgayMua.getText().toString())){
                        editNgayMua.setError("Ngày mua không hợp lệ");
                        editNgayMua.requestFocus();
                        Toast.makeText(EditTSActivity.this, "Ngày mua tài sản không hợp lệ", Toast.LENGTH_SHORT).show();
                        return;
                    }else {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        sdf.setLenient(false);
                        try{
                            Date input = sdf.parse(editNgayMua.getText().toString());
                            today = sdf.parse(sdf.format(today));
                            if(input.compareTo(today) > 0){
                                editNgayMua.setError("Ngày mua không hợp lệ");
                                editNgayMua.requestFocus();
                                Toast.makeText(EditTSActivity.this, "Ngày mua tài sản không hợp lệ", Toast.LENGTH_SHORT).show();
                                return;
                            }else{
                                editNgayMua.setError(null);
                            }
                        }catch (Exception ex){
                            Toast.makeText(EditTSActivity.this, "Ngày mua tài sản không hợp lệ", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    }
                }


                //validate ngày bắt đầu và kết thúc bảo hành tài sản
                if(!editBaoHanhStartTS.getText().toString().isEmpty() || !editBaoHanhEndTS.getText().toString().isEmpty()){
                    if(!isValidDate(editBaoHanhStartTS.getText().toString())){
                        editBaoHanhStartTS.setError("Ngày bắt đầu bảo hành không hợp lệ");
                        editBaoHanhStartTS.requestFocus();
                        Toast.makeText(EditTSActivity.this, "Ngày bắt đầu bảo hành không hợp lệ", Toast.LENGTH_SHORT).show();
                        return;
                    }else {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        sdf.setLenient(false);
                        try{
                            Date input = sdf.parse(editBaoHanhStartTS.getText().toString());
                            today = sdf.parse(sdf.format(today));
                            if(input.compareTo(today) > 0){
                                editBaoHanhStartTS.setError("Ngày bắt đầu bảo hành không hợp lệ");
                                editBaoHanhStartTS.requestFocus();
                                Toast.makeText(EditTSActivity.this, "Ngày bắt đầu bảo hành không hợp lệ", Toast.LENGTH_SHORT).show();
                                return;
                            }else{
                                editBaoHanhStartTS.setError(null);

                            }
                        }catch (Exception ex){
                            Toast.makeText(EditTSActivity.this, "Ngày bắt đầu bảo hành không hợp lệ", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    }

                    if(!isValidDate(editBaoHanhEndTS.getText().toString())){
                        editBaoHanhEndTS.setError("Ngày hết hạn hành không hợp lệ");
                        editBaoHanhEndTS.requestFocus();
                        Toast.makeText(EditTSActivity.this, "Ngày hết hạn bảo hành không hợp lệ", Toast.LENGTH_SHORT).show();
                        return;
                    }else {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        sdf.setLenient(false);
                        try{
                            Date input = sdf.parse(editBaoHanhEndTS.getText().toString());
                            Date baoHanhStart = sdf.parse(editBaoHanhStartTS.getText().toString());
                            if(input.compareTo(baoHanhStart) < 0){
                                editBaoHanhEndTS.setError("Ngày hết hạn bảo hành không hợp lệ");
                                editBaoHanhEndTS.requestFocus();
                                Toast.makeText(EditTSActivity.this, "Ngày hết hạn bảo hành không hợp lệ", Toast.LENGTH_SHORT).show();
                                return;
                            }else{
                                editBaoHanhEndTS.setError(null);
                            }
                        }catch (Exception ex){
                            Toast.makeText(EditTSActivity.this, "Ngày hết hạn bảo hành không hợp lệ", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    }
                }

                TaiSan savedTS = gatherTaiSan();
                if (dbHelper.updateTaiSan(savedTS)) {
                    Intent edit = new Intent(EditTSActivity.this, ListTaiSan.class);
                    edit.putExtra("editedTS", savedTS);
                    setResult(RESULT_OK, edit);
                    finish();
                }else{
                    Toast.makeText(EditTSActivity.this,"Có lỗi xảy ra" , Toast.LENGTH_SHORT).show();
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
            editImgTS.setImageBitmap(photo);
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
        if(check || taiSan.getAnhts() != null){
            Bitmap photo = ((BitmapDrawable) editImgTS.getDrawable()).getBitmap();
            photo = resizeBitmap(photo, 800, 800);
            // Chuyển bitmap -> byte[]
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 70, stream); // PNG hoặc JPEG
            byte[] imageBytes = stream.toByteArray();
            result.setAnhts(imageBytes);
        }else{
            result.setAnhts(null);
        }
        result.setTents(editTenTS.getText().toString());
        result.setIdtk(1);
        result.setIdts(taiSan.getIdts());
        result.setMota(editMoTaTS.getText().toString());
        DanhMuc selectDanhMuc =(DanhMuc) editDanhmucTS.getSelectedItem();
        result.setIddanhmuc(selectDanhMuc.getIddanhmuc());
        result.setGiatri(Integer.parseInt(editGiaTriTS.getText().toString()));
        result.setNgaymua(editNgayMua.getText().toString());
        result.setTinhtrang(editTinhTrangts.getSelectedItem().toString());
        result.setVitri(editVitriTS.getText().toString());
        result.setGhichu(editGhiChuTS.getText().toString());
        result.setBaohanhStart(editBaoHanhStartTS.getText().toString());
        result.setBaohanhEnd(editBaoHanhEndTS.getText().toString());
        result.setSoluong(Integer.parseInt(editSoLuongTS.getText().toString()));
        return result;
    }

    private void SetDisplayaiSan(){
        Intent intent = getIntent();
        taiSan =(TaiSan) intent.getSerializableExtra("EditTS");
            // Chuyển bitmap -> byte[]
        if(taiSan != null){
            if(taiSan.getAnhts() != null && taiSan.getAnhts().length > 0){
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Bitmap bitmap = BitmapFactory.decodeByteArray(taiSan.getAnhts(), 0, taiSan.getAnhts().length);
                editImgTS.setImageBitmap(bitmap);
            }
            editTenTS.setText(taiSan.getTents());
            editMoTaTS.setText(taiSan.getMota());
            editGiaTriTS.setText(String.valueOf(taiSan.getGiatri()));
            editNgayMua.setText(taiSan.getNgaymua());
            editVitriTS.setText(taiSan.getVitri());
            editGhiChuTS.setText(taiSan.getGhichu());
            editBaoHanhStartTS.setText(taiSan.getBaohanhStart());
            editBaoHanhEndTS.setText(taiSan.getBaohanhEnd());
            editSoLuongTS.setText(String.valueOf(taiSan.getSoluong()));
        }
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
        danhmucs = (ArrayList<DanhMuc>) dbHelper.getAllDanhMuc();
        ArrayAdapter<DanhMuc> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, danhmucs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editDanhmucTS.setAdapter(adapter);
        editDanhmucTS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DanhMuc selectedItem = danhmucs.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không chọn gì
            }
        });
        editDanhmucTS.setDropDownVerticalOffset(50);
        int position = -1;
        for (int i = 0; i < danhmucs.size(); i++) {
            if (danhmucs.get(i).getIddanhmuc() == taiSan.getIddanhmuc()) {
                position = i;
                break;
            }
        }
        editDanhmucTS.setSelection(position);
    }
    public void settingSprinnerTinhTrangTS() {
        ArrayList<String> tinhtrangs = new ArrayList<>();
        tinhtrangs.add("Đang sử đụng");
        tinhtrangs.add("Đã thanh lý");
        tinhtrangs.add("Đã hỏng");
        tinhtrangs.add("Đã mất");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tinhtrangs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editTinhTrangts.setAdapter(adapter);
        editTinhTrangts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = tinhtrangs.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không chọn gì
            }
        });
        editTinhTrangts.setDropDownVerticalOffset(50);
        editTinhTrangts.setSelection(0);
        int position = tinhtrangs.indexOf(taiSan.getTinhtrang());
        if(position >= 0){
            editTinhTrangts.setSelection(position);
        }
    }

    private void settingChoseDate(){

        btnNgayMuaTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int y = calendar.get(Calendar.YEAR);
                int m = calendar.get(Calendar.MONTH);
                int d = calendar.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(EditTSActivity.this, (view1,
                                                                                                 year, month, day)->{
                    String s = day+"/"+(month+1)+"/"+year;
                    editNgayMua.setText(s);
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


                DatePickerDialog datePickerDialog = new DatePickerDialog(EditTSActivity.this, (view1,
                                                                                                 year, month, day)->{
                    String s = day+"/"+(month+1)+"/"+year;
                    editBaoHanhStartTS.setText(s);
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


                DatePickerDialog datePickerDialog = new DatePickerDialog(EditTSActivity.this, (view1,
                                                                                                 year, month, day)->{
                    String s = day+"/"+(month+1)+"/"+year;
                    editBaoHanhEndTS.setText(s);
                }, y, m, d);
                datePickerDialog.show();
            }
        });
    }


    private void mapping(){
        editImgTS = findViewById(R.id.edit_img_ts);
        editTenTS = findViewById(R.id.edit_tents);
        editSoLuongTS = findViewById(R.id.edit_soluongts);
        editMoTaTS = findViewById(R.id.edit_mota);
        editDanhmucTS = findViewById(R.id.edit_danhmucts);
        editGiaTriTS = findViewById(R.id.edit_giatrits);
        editTinhTrangts = findViewById(R.id.edit_tinhtrangts);
        editVitriTS = findViewById(R.id.edit_vitrits);
        editGhiChuTS = findViewById(R.id.edit_ghichuts);
        editNgayMua = findViewById(R.id.edit_ngaymuats);
        editBaoHanhStartTS = findViewById(R.id.edit_baohanhstartts);
        editBaoHanhEndTS = findViewById(R.id.edit_baohanhendts);
        btnLuuTS = findViewById(R.id.edit_btnluu);
        btnNgayMuaTS = findViewById(R.id.edit_btndatemua);
        btnBaoHanhStart = findViewById(R.id.edit_btndatebaohanhStart);
        btnBaoHanhEnd = findViewById(R.id.edit_btndatebaohanhend);
        btnThuVien = findViewById(R.id.edit_laytutv);
        btnChup = findViewById(R.id.edit_chup);
        btnExit = findViewById(R.id.edit_btnExit);
    }
}