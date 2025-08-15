package com.example.myasset.Adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myasset.R;
import com.example.myasset.model.TaiSan;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;


public class ListTSAdapter extends RecyclerView.Adapter<ListTSAdapter.ViewHolder> {

    ArrayList<TaiSan> taiSans;
    private ItemClickListener itemClickListener;
    private ItemClickLongListener itemClickLongListener;


    public ListTSAdapter(ArrayList<TaiSan> taiSans, ItemClickListener itemClickListener, ItemClickLongListener itemClickLongListener) {
        this.taiSans = taiSans;
        this.itemClickListener = itemClickListener;
        this.itemClickLongListener = itemClickLongListener;
    }

    public void setData(ArrayList<TaiSan> newList) {
        this.taiSans = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View inflate = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_list_ts, parent, false);
        return new ViewHolder(inflate);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){
        String baohanh = taiSans.get(position).getBaohanhEnd();
        if(baohanh.isEmpty()){
            baohanh = "Không có";
        }
        holder.tenTS.setText(taiSans.get(position).getTents());
        holder.giaTS.setText(formatVND(taiSans.get(position).getGiatri()));
        holder.baoHanh.setText("Hạn bảo hành: " + baohanh);
        holder.soTS.setText(String.valueOf(taiSans.get(position).getSoluong()));
        Bitmap img = getBitmapFromBytes(taiSans.get(position).getAnhts());
        if( img != null){
            Glide.with(holder.itemView.getContext()).load(img).into(holder.anhTS);
        }else{
            int drawableResourceId = holder.itemView.getContext().getResources()
                    .getIdentifier("no_img","drawable", holder.itemView.getContext().getPackageName());
            Glide.with(holder.itemView.getContext()).load(drawableResourceId).into(holder.anhTS);
        }
        holder.itemView.setOnClickListener(view -> {
            itemClickListener.onItemClick(taiSans.get(holder.getAbsoluteAdapterPosition()), holder.getAbsoluteAdapterPosition());
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                itemClickLongListener.onItemLongClick(taiSans.get(holder.getAbsoluteAdapterPosition()), holder.getAbsoluteAdapterPosition());
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return taiSans.size();
    }

    public interface ItemClickListener{
        void onItemClick(TaiSan taiSan, int pos);
    }
    public String formatVND(long amount) {
        NumberFormat vndFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return vndFormat.format(amount);
    }

    public interface ItemClickLongListener{
        void onItemLongClick(TaiSan taiSan, int pos);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        TextView tenTS, giaTS, baoHanh, soTS;
        ImageView anhTS;
        ConstraintLayout containListTS;
        public ViewHolder (@NonNull View itemView){
            super(itemView);
            tenTS = itemView.findViewById(R.id.tenTS);
            giaTS = itemView.findViewById(R.id.giaTS);
            baoHanh = itemView.findViewById(R.id.baoHanh);
            soTS = itemView.findViewById(R.id.soTS);
            anhTS = itemView.findViewById(R.id.anhTS);
            containListTS = itemView.findViewById(R.id.containListTS);
            itemView.setOnCreateContextMenuListener(this);
        }


        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuInflater inflater = new MenuInflater(view.getContext());
            inflater.inflate(R.menu.my_context_menu, contextMenu);
        }
    }
    public Bitmap getBitmapFromBytes(byte[] imageBytes) {
        if (imageBytes != null && imageBytes.length > 0) {
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        }
        return null;
    }
}
