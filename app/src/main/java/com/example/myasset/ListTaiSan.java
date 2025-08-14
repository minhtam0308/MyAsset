package com.example.myasset;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
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
    private RecyclerView.Adapter adapterListTS;
    private ActivityResultLauncher<Intent> launcher;
    private TextView countTS;
    ArrayList<TaiSan> TSList = new ArrayList<TaiSan>();

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
        mapping();
        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), activityResult -> {
                    if (activityResult.getResultCode() == RESULT_OK && activityResult.getData() !=null) {
                        if(activityResult.getData().getSerializableExtra("savedTS") != null){
                            TSList.add((TaiSan) activityResult.getData().getSerializableExtra("savedTS"));
                            adapterListTS.notifyDataSetChanged();
                            countTS.setText(""+TSList.size());
                        }
                        if(activityResult.getData().getSerializableExtra("editedTS") != null){
                            TaiSan taiSanNew = (TaiSan) activityResult.getData().getSerializableExtra("editedTS");
                            TSList.set(posMenu, taiSanNew);
                            adapterListTS.notifyDataSetChanged();
                            countTS.setText(""+TSList.size());
                        }
                    }
                }
        );
        setListTS();
        countTS.setText(""+TSList.size());

    }
    private void setListTS() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        RvListTS = findViewById(R.id.rv_listTS);
        RvListTS.setLayoutManager(linearLayoutManager);
        TSList.add(new TaiSan(1, 1, 1, "tai san 1",3, "mo ta 1", 500, "10/5/1022", "dang su dung", "ha nam", null, "ghi chu", "2022", "2025"));
        TSList.add(new TaiSan(2, 1, 1, "tai san 2",3, "mo ta 1", 500, "10/5/1022", "dang su dung", "ha nam", null, "ghi chu", "2022", "2025"));
        TSList.add(new TaiSan(3, 1, 1, "tai san 3",3, "mo ta 1", 500, "10/5/1022", "dang su dung", "ha nam", null, "ghi chu", "2022", "2025"));
        TSList.add(new TaiSan(4, 1, 1, "tai san 3",3, "mo ta 1", 500, "10/5/1022", "dang su dung", "ha nam", null, "ghi chu", "2022", "2025"));
        TSList.add(new TaiSan(5, 1, 1, "tai san 3",3, "mo ta 1", 500, "10/5/1022", "dang su dung", "ha nam", null, "ghi chu", "2022", "2025"));
        TSList.add(new TaiSan(6, 1, 1, "tai san 3",3, "mo ta 1", 500, "10/5/1022", "dang su dung", "ha nam", null, "ghi chu", "2022", "2025"));
        adapterListTS = new ListTSAdapter(TSList, new ListTSAdapter.ItemClickListener() {
            @Override
            public void onItemClick(TaiSan taiSan, int posistion) {
//                Intent intent = new Intent(ListTaiSan.this, detailPage)
//                Toast.makeText(ListTaiSan.this, taiSan.toString(), Toast.LENGTH_LONG).show();
                //sang trang xem cgi tiết

            }
        }, new ListTSAdapter.ItemClickLongListener() {
            @Override
            public void onItemLongClick(TaiSan taiSan,int posistion) {
//                Toast.makeText(ListTaiSan.this, taiSan.toString(), Toast.LENGTH_SHORT).show();
                posMenu = posistion;

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
                public void onClick(DialogInterface dialogInterface, int i) {
                    TSList.remove(posMenu);
                    adapterListTS.notifyDataSetChanged();
                    if(!TSList.isEmpty()){
                        posMenu = 0;
                    }
                    countTS.setText(""+TSList.size());
                    Toast.makeText(ListTaiSan.this, "xóa thành công", Toast.LENGTH_SHORT).show();
                }
            });
            alert.setNegativeButton("Không", null);
            alert.show();

        }
        if (item.getItemId() == R.id.menu_them) {
            Intent them = new Intent(ListTaiSan.this, CreateTSActivity.class);
            launcher.launch(them);
        }

        return super.onContextItemSelected(item);
    }


    private void mapping(){
        countTS = findViewById(R.id.countTS);
    }


}