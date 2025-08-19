package com.example.myasset;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myasset.model.DanhMuc;
import com.example.myasset.model.TaiSan;

import java.util.ArrayList;

public class DanhMucActivity extends AppCompatActivity {

    private LinearLayout navHome, navUser, navDanhMuc;
    private Button btnCreateDanhmuc;
    private ListView lvDanhMuc;
    DatabaseHelper dbHelper;
    private ArrayList<DanhMuc> lstDanhMuc = new ArrayList<DanhMuc>();
    private ArrayAdapter adapter;
    private int pos = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_danh_muc);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mapping();
        dbHelper = new DatabaseHelper(this);
        lstDanhMuc =(ArrayList<DanhMuc>) dbHelper.getAllDanhMuc();
        adapter = new ArrayAdapter(DanhMucActivity.this, android.R.layout.simple_list_item_1, lstDanhMuc);
        lvDanhMuc.setAdapter(adapter);
        lvDanhMuc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(DanhMucActivity.this, ListTaiSan.class);
                intent.putExtra("getTSByDanhMuc", lstDanhMuc.get(i));
                setResult(RESULT_OK, intent);
                startActivity(intent);
            }
        });
        lvDanhMuc.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                pos = i;
                return false;
            }
        });
        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DanhMucActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        navUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DanhMucActivity.this, UserActivity.class);
                startActivity(intent);

            }
        });
        btnCreateDanhmuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DanhMucActivity.this, CreateDanhMuc.class);
                startActivity(intent);
            }
        });
        registerForContextMenu(lvDanhMuc);

    }
    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<DanhMuc> newList =(ArrayList<DanhMuc>) dbHelper.getAllDanhMuc();

        lstDanhMuc.clear();        // xoá list cũ
        lstDanhMuc.addAll(newList); // nạp dữ liệu mới
        adapter.notifyDataSetChanged();
    }
        @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.my_context_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_sua) {
            Intent sua = new Intent(DanhMucActivity.this, EditDanhmucActivity.class);
            sua.putExtra("EditDM", lstDanhMuc.get(pos));
            startActivity(sua);
        }
        if (item.getItemId() == R.id.menu_xoa) {

            AlertDialog.Builder alert =new AlertDialog.Builder(DanhMucActivity.this);
            alert.setTitle("Xác nhận");
            alert.setMessage("Bạn muốn xóa danh mục này?");
            alert.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {;
                    if(dbHelper.deleteDanhMuc(lstDanhMuc.get(pos).getIddanhmuc())){
                        lstDanhMuc.clear();
                        lstDanhMuc.addAll((ArrayList<DanhMuc>) dbHelper.getAllDanhMuc()); // lấy từ SQLite
                        adapter.notifyDataSetChanged();
                        if(!lstDanhMuc.isEmpty()){
                            pos = 0;
                        }
                        Toast.makeText(DanhMucActivity.this, "xóa thành công", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(DanhMucActivity.this, "Không tìm thấy id danh mục", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            alert.setNegativeButton("Không", null);
            alert.show();

        }

        return super.onContextItemSelected(item);
    }

    private void mapping(){
        navHome = findViewById(R.id.navHome);
        navUser = findViewById(R.id.navUser);
        navDanhMuc = findViewById(R.id.navDanhMuc);
        lvDanhMuc = findViewById(R.id.lvDanhMuc);
        btnCreateDanhmuc = findViewById(R.id.danhmuc_btn_createDanhmuc);
    }
}