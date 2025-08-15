package com.example.myasset;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myasset.Adapter.ListTSAdapter;
import com.example.myasset.model.TaiKhoan;
import com.example.myasset.model.TaiSan;
import com.google.android.material.imageview.ShapeableImageView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    private ShapeableImageView imgAvatar;
    private TextView tvUserName, tvTotalValue, tvLatestAsset, btnSeeAll;
    private LinearLayout btnAddAsset, btn_timkiemTrangchu, btnAssetList, navDanhMuc, navUser, navHome;
    private ListTSAdapter adapterListTS;
    private RecyclerView RvListGanDay;
    private ActivityResultLauncher<Intent> launcher;

    DatabaseHelper dbHelper;

    TaiKhoan taiKhoan;
    ArrayList<TaiSan> lstTS;
    int posMenu = 0;
    ArrayList<TaiSan> lastestTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        dbHelper = new DatabaseHelper(this);
        mapping();
        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), activityResult -> {
                    if (activityResult.getResultCode() == RESULT_OK && activityResult.getData() !=null) {
                        if(activityResult.getData().getSerializableExtra("savedTS") != null){
                            Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                        }
                        if(activityResult.getData().getSerializableExtra("editedTS") != null){
//                            TaiSan test =(TaiSan) activityResult.getData().getSerializableExtra("editedTS");
//                            Toast.makeText(this, test.getTinhtrang(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(this, "Sửa thành công", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );


        btnAssetList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ListTaiSan.class);
                launcher.launch(intent);
            }
        });

        btnSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ListTaiSan.class);
                launcher.launch(intent);
            }
        });

        btnAddAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CreateTSActivity.class);
                launcher.launch(intent);
            }
        });
        btn_timkiemTrangchu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ListTaiSan.class);
                intent.putExtra("timkiem", "OK");
                launcher.launch(intent);
            }
        });
        navUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, UserActivity.class);
                launcher.launch(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInfoHome();

    }
    private void setListTS() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        RvListGanDay.setLayoutManager(linearLayoutManager);
        lastestTS =(ArrayList<TaiSan>) dbHelper.getTaiSanAll();
        adapterListTS = new ListTSAdapter(lastestTS, new ListTSAdapter.ItemClickListener() {
            @Override
            public void onItemClick(TaiSan taiSan, int posistion) {
//                Intent intent = new Intent(ListTaiSan.this, detailPage)
//                Toast.makeText(ListTaiSan.this, taiSan.toString(), Toast.LENGTH_LONG).show();
                //sang trang xem cgi tiết
                Intent intent = new Intent(HomeActivity.this, DetailTSActivity.class);
                intent.putExtra("TSDetail", lastestTS.get(posistion));
                setResult(RESULT_OK, intent);
                launcher.launch(intent);

            }
        }, new ListTSAdapter.ItemClickLongListener() {
            @Override
            public void onItemLongClick(TaiSan taiSan,int posistion) {
//                Toast.makeText(ListTaiSan.this, taiSan.toString(), Toast.LENGTH_SHORT).show();
                posMenu = posistion;

            }
        });

        RvListGanDay.setAdapter(adapterListTS);
        registerForContextMenu(RvListGanDay);

    }

    private void loadInfoHome() {
        taiKhoan = dbHelper.getCurrentUserObject();
        lstTS =(ArrayList<TaiSan>) dbHelper.getTaiSanAll();
        lastestTS =new ArrayList<>(get5TaiSanGanNhat(lstTS));

        if(taiKhoan == null){
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            // Xóa lịch sử để không thể back lại màn hình UserActivity sau khi logout
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }else{
            long tongts = dbHelper.getTongGiaTriTaiSan();
            tvUserName.setText(taiKhoan.getTk());
            tvTotalValue.setText(formatVND(tongts));
            if (lastestTS != null && !lastestTS.isEmpty()) {
                tvLatestAsset.setText(lastestTS.get(0).getTents());
            } else {
                tvLatestAsset.setText("Chưa có tài sản");
            }

            if (taiKhoan.getAnhtk() != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(taiKhoan.getAnhtk(), 0, taiKhoan.getAnhtk().length);
                imgAvatar.setImageBitmap(bitmap);
            }
            setListTS();
            if(adapterListTS != null){
                adapterListTS.setData(lastestTS);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_sua) {
            Intent sua = new Intent(HomeActivity.this, EditTSActivity.class);
            sua.putExtra("EditTS", lastestTS.get(posMenu));
            launcher.launch(sua);
        }
        if (item.getItemId() == R.id.menu_xoa) {
//            Intent sua = new Intent(MainActivity.this, SuaActivity.class);
//            sua.putExtra("book", listBook.get(pos));
//            launcher.launch(sua);
            AlertDialog.Builder alert =new AlertDialog.Builder(HomeActivity.this);
            alert.setTitle("Xác nhận");
            alert.setMessage("Bạn muốn xóa tài sản này?");
            alert.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {;
                    if(dbHelper.deleteTaiSan(lastestTS.get(posMenu).getIdts())){
                        loadInfoHome();
                        Toast.makeText(HomeActivity.this, "xóa thành công", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(HomeActivity.this, "Không tìm thấy id tài sản", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            alert.setNegativeButton("Không", null);
            alert.show();

        }
        return super.onContextItemSelected(item);
    }


    public String formatVND(long amount) {
        NumberFormat vndFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return vndFormat.format(amount);
    }

    public List<TaiSan> get5TaiSanGanNhat(List<TaiSan> listTaiSan) {
        if (listTaiSan == null || listTaiSan.isEmpty()) return new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Sắp xếp giảm dần theo ngày mua
        Collections.sort(listTaiSan, (ts1, ts2) -> {
            try {
                Date d1 = sdf.parse(ts1.getNgaymua());
                Date d2 = sdf.parse(ts2.getNgaymua());
                return d2.compareTo(d1); // giảm dần
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        });

        // Lấy 5 phần tử đầu tiên (hoặc ít hơn nếu list nhỏ)
        return listTaiSan.subList(0, Math.min(5, listTaiSan.size()));
    }
    private void mapping(){
        imgAvatar = findViewById(R.id.imgAvatar);
        tvUserName = findViewById(R.id.tvUserName);
        tvTotalValue = findViewById(R.id.tvTotalValue);
        tvLatestAsset = findViewById(R.id.tvLatestAsset);
        RvListGanDay = findViewById(R.id.RvListGanDay);

        btnAddAsset = findViewById(R.id.btnAddAsset);
        btn_timkiemTrangchu = findViewById(R.id.btn_timkiemTrangchu);
        btnSeeAll = findViewById(R.id.btnSeeAll);
        btnAssetList = findViewById(R.id.btnAssetList);
        btn_timkiemTrangchu = findViewById(R.id.btn_timkiemTrangchu);
        navUser = findViewById(R.id.navUser);
        navDanhMuc = findViewById(R.id.navDanhMuc);
        navHome = findViewById(R.id.navHome);

    }
}