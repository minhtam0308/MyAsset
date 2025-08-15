package com.example.myasset;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.myasset.model.TaiSan;

import java.util.ArrayList;

public class ListTaiSan extends AppCompatActivity {
    private RecyclerView RvListTS;
    private ListTSAdapter adapterListTS;
    private ActivityResultLauncher<Intent> launcher;
    private EditText edtSearch;
    private TextView countTS;
    ArrayList<TaiSan> TSList = new ArrayList<TaiSan>();
    private ImageView exitListTS, btnSearchListTS;
    DatabaseHelper dbHelper;

    private int posMenu = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_tai_san);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        dbHelper = new DatabaseHelper(this);
        mapping();
        Intent resCreate = getIntent();
        if(resCreate.getSerializableExtra("savedTS") != null){
            Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show();
        } else if (resCreate.getSerializableExtra("timkiem") != null) {
            edtSearch.requestFocus();
        }
        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), activityResult -> {
                    if (activityResult.getResultCode() == RESULT_OK && activityResult.getData() !=null) {
                        if(activityResult.getData().getSerializableExtra("savedTS") != null){
                            TSList =(ArrayList<TaiSan>) dbHelper.getTaiSanAll(); // lấy từ SQLite
                            adapterListTS.setData(TSList);
                            Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                            countTS.setText(""+TSList.size());
                        }
                        if(activityResult.getData().getSerializableExtra("editedTS") != null){
//                            TaiSan test =(TaiSan) activityResult.getData().getSerializableExtra("editedTS");
//                            Toast.makeText(this, test.getTinhtrang(), Toast.LENGTH_SHORT).show();
                            TSList =(ArrayList<TaiSan>) dbHelper.getTaiSanAll(); // lấy từ SQLite
                            adapterListTS.setData(TSList);
                            Toast.makeText(this, "Sửa thành công", Toast.LENGTH_SHORT).show();
                            countTS.setText(""+TSList.size());
                        }
                    }
                }
        );
        setListTS();

        btnSearchListTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keyword = edtSearch.getText().toString();
                TSList =(ArrayList<TaiSan>) dbHelper.searchTaiSanByName(keyword);
                adapterListTS.setData(TSList);
                countTS.setText(""+TSList.size());
            }
        });
        countTS.setText(""+TSList.size());

    }
    @Override
    protected void onResume() {
        super.onResume();
        setListTS();

    }
    private void setListTS() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        RvListTS = findViewById(R.id.rv_listTS);
        RvListTS.setLayoutManager(linearLayoutManager);
        //lấy all ts
        TSList =(ArrayList<TaiSan>) dbHelper.getTaiSanAll();
        adapterListTS = new ListTSAdapter(TSList, new ListTSAdapter.ItemClickListener() {
            @Override
            public void onItemClick(TaiSan taiSan, int posistion) {
                Intent intent = new Intent(ListTaiSan.this, DetailTSActivity.class);
                intent.putExtra("TSDetail", TSList.get(posistion));
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
        exitListTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Tạo intent về LoginActivity
                Intent intent = new Intent(ListTaiSan.this, HomeActivity.class);
                // Xóa lịch sử để không thể back lại màn hình UserActivity sau khi logout
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        RvListTS.setAdapter(adapterListTS);
        registerForContextMenu(RvListTS);

    }
//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        getMenuInflater().inflate(R.menu.my_context_menu, menu);
//        super.onCreateContextMenu(menu, v, menuInfo);
//    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_sua) {
            Intent sua = new Intent(ListTaiSan.this, EditTSActivity.class);
            sua.putExtra("EditTS", TSList.get(posMenu));
            launcher.launch(sua);
        }
        if (item.getItemId() == R.id.menu_xoa) {
//            Intent sua = new Intent(MainActivity.this, SuaActivity.class);
//            sua.putExtra("book", listBook.get(pos));
//            launcher.launch(sua);
            AlertDialog.Builder alert =new AlertDialog.Builder(ListTaiSan.this);
            alert.setTitle("Xác nhận");
            alert.setMessage("Bạn muốn xóa tài sản này?");
            alert.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {;
                    if(dbHelper.deleteTaiSan(TSList.get(posMenu).getIdts())){
                        TSList =(ArrayList<TaiSan>) dbHelper.getTaiSanAll(); // lấy từ SQLite
                        adapterListTS.setData(TSList);
                        if(!TSList.isEmpty()){
                            posMenu = 0;
                        }
                        countTS.setText(""+TSList.size());
                        Toast.makeText(ListTaiSan.this, "xóa thành công", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(ListTaiSan.this, "Không tìm thấy id tài sản", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            alert.setNegativeButton("Không", null);
            alert.show();

        }

        return super.onContextItemSelected(item);
    }


    private void mapping(){

        countTS = findViewById(R.id.countTS);
        exitListTS = findViewById(R.id.btn_exitlistts);
        edtSearch = findViewById(R.id.edtSearch);
        btnSearchListTS = findViewById(R.id.btn_search_listts);

    }


}