package com.example.myasset;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.myasset.model.DanhMuc;
import com.example.myasset.model.TaiKhoan;
import com.example.myasset.model.TaiSan;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static String DB_NAME = "quanly_taisan.sqlite";
    private String DB_PATH;
    private Context mContext;

    // them moi (tam)
    private static final String ID_FILENAME = "MyAppPrefs";
    private int idTK;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.mContext = context;
        this.DB_PATH = context.getDatabasePath(DB_NAME).getPath();
        //them (tam)
        idTK = getUserIdFromPrefs(context);
    }

    public void createDatabase() throws IOException {
        if (!checkDatabase()) {
            // Tạo thư mục chứa DB nếu chưa có
            File dbDir = new File(mContext.getDatabasePath(DB_NAME).getParent());
            if (!dbDir.exists()) dbDir.mkdirs();

            // Copy DB từ assets
            copyDatabase();
        }
    }


    private boolean checkDatabase() {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            // Không tồn tại DB
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null;
    }

    private void copyDatabase() throws IOException {
        InputStream myInput = mContext.getAssets().open(DB_NAME);
        OutputStream myOutput = new FileOutputStream(DB_PATH);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Không cần tạo bảng vì DB có sẵn
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Nâng cấp nếu cần
    }

//    public Cursor getCurrentUser() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        // Lấy thông tin user có ID = 1 (hoặc bạn có thể dùng SharedPreferences lưu userID đang đăng nhập)
//        return db.rawQuery("SELECT tk, sdt, gioitinh, ngaysinh FROM taikhoan WHERE idtk = 2", null);
//    }

    public TaiKhoan getCurrentUserObject() {
        TaiKhoan taiKhoan = null;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM taikhoan WHERE idtk = ?", new String[]{String.valueOf(idTK)});

        if (cursor != null && cursor.moveToFirst()) {
            taiKhoan = new TaiKhoan();
            taiKhoan.setIdtk(cursor.getInt(cursor.getColumnIndexOrThrow("idtk")));
            taiKhoan.setTentk(cursor.getString(cursor.getColumnIndexOrThrow("tentk")));
            taiKhoan.setMatkhau(cursor.getString(cursor.getColumnIndexOrThrow("matkhau")));
            taiKhoan.setAnhtk(cursor.getBlob(cursor.getColumnIndexOrThrow("anhtk"))); // lấy byte[]
            taiKhoan.setTk(cursor.getString(cursor.getColumnIndexOrThrow("tk")));
            taiKhoan.setSdt(cursor.getString(cursor.getColumnIndexOrThrow("sdt")));
            taiKhoan.setGioitinh(cursor.getString(cursor.getColumnIndexOrThrow("gioitinh")));
            taiKhoan.setNgaysinh(cursor.getString(cursor.getColumnIndexOrThrow("ngaysinh")));
        }

        if (cursor != null) cursor.close();
        db.close();

        return taiKhoan;
    }

    public boolean updateCurrentUserWithImage(String name, String gender, String dateOfBirth, String phone, byte[] imageBytes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tk", name);
        values.put("gioitinh", gender);
        values.put("ngaysinh", dateOfBirth);
        values.put("sdt", phone);
        if (imageBytes != null) {
            values.put("anhtk", imageBytes);
        }
        int rows = db.update("taikhoan", values, "idtk = ?", new String[]{String.valueOf(idTK)});
        db.close();
        return rows > 0;
    }

    //code moi (tam)
    public int checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT idtk FROM taikhoan WHERE tentk = ? AND matkhau = ?",
                new String[]{username, password}
        );

        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0); // Lấy cột id
        }

        cursor.close();
        db.close();

        return userId;
    }
    public int getUserIdFromPrefs(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(ID_FILENAME, Context.MODE_PRIVATE);
        return prefs.getInt("USER_ID", -1); // -1 nếu chưa đăng nhập
    }

    public boolean registerUser(String tk, String tenTK, String matkhau) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Kiểm tra trùng tài khoản
        Cursor cursor = db.rawQuery("SELECT idtk FROM taikhoan WHERE tentk = ?", new String[]{tenTK});
        if (cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return false; // Tài khoản đã tồn tại
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put("tentk", tenTK);
        values.put("tk", tk);
        values.put("matkhau", matkhau);

        long result = db.insert("taikhoan", null, values);

        String sql = "INSERT INTO danhmuc (tendm, idtk) VALUES (?, ?)";
        db.execSQL(sql, new Object[]{"Nhà ở", result});
        db.execSQL(sql, new Object[]{"Trang sức", result});
        db.execSQL(sql, new Object[]{"Xe cộ", result});
        db.close();
        return result != -1;
    }

    public List<DanhMuc> getAllDanhMuc() {
        List<DanhMuc> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM danhmuc WHERE idtk = ?", new String[]{String.valueOf(idTK)});

        if (cursor.moveToFirst()) {
            do {
                DanhMuc dm = new DanhMuc();
                dm.setIddanhmuc(cursor.getInt(cursor.getColumnIndexOrThrow("iddanhmuc")));
                dm.setTendm(cursor.getString(cursor.getColumnIndexOrThrow("tendm")));
                dm.setIdtk(cursor.getInt(cursor.getColumnIndexOrThrow("idtk")));
                list.add(dm);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return list;
    }
    public List<TaiSan> getTaiSanAll() {
        List<TaiSan> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM taisan WHERE idtk = ?", new String[]{String.valueOf(idTK)});

        if (cursor.moveToFirst()) {
            do {
                TaiSan ts = new TaiSan();
                ts.setIdts(cursor.getInt(cursor.getColumnIndexOrThrow("idts")));
                ts.setTents(cursor.getString(cursor.getColumnIndexOrThrow("tents")));
                ts.setMota(cursor.getString(cursor.getColumnIndexOrThrow("mota")));
                ts.setIddanhmuc(cursor.getInt(cursor.getColumnIndexOrThrow("iddanhmuc")));
                ts.setNgaymua(cursor.getString(cursor.getColumnIndexOrThrow("ngaymua")));
                ts.setTinhtrang(cursor.getString(cursor.getColumnIndexOrThrow("tinhtrang")));
                ts.setGiatri(cursor.getInt(cursor.getColumnIndexOrThrow("giatri")));
                ts.setVitri(cursor.getString(cursor.getColumnIndexOrThrow("vitri")));
                ts.setGhichu(cursor.getString(cursor.getColumnIndexOrThrow("ghichu")));
                ts.setBaohanhStart(cursor.getString(cursor.getColumnIndexOrThrow("baohanhStart")));
                ts.setBaohanhEnd(cursor.getString(cursor.getColumnIndexOrThrow("baohanhEnd")));
                ts.setIdtk(cursor.getInt(cursor.getColumnIndexOrThrow("idtk")));
                ts.setSoluong(cursor.getInt(cursor.getColumnIndexOrThrow("soluong")));

                // Lấy ảnh dạng byte[]
                int colIndexAnh = cursor.getColumnIndex("anhts");
                if (colIndexAnh != -1 && !cursor.isNull(colIndexAnh)) {
                    ts.setAnhts(cursor.getBlob(colIndexAnh));
                }
                list.add(ts);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return list;
    }

    public List<TaiSan> getTaiSanByDanhMuc(int idDanhMuc) {
        List<TaiSan> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM taisan WHERE idtk = ? AND iddanhmuc = ?", new String[]{String.valueOf(idTK), String.valueOf(idDanhMuc)});

        if (cursor.moveToFirst()) {
            do {
                TaiSan ts = new TaiSan();
                ts.setIdts(cursor.getInt(cursor.getColumnIndexOrThrow("idts")));
                ts.setTents(cursor.getString(cursor.getColumnIndexOrThrow("tents")));
                ts.setMota(cursor.getString(cursor.getColumnIndexOrThrow("mota")));
                ts.setIddanhmuc(cursor.getInt(cursor.getColumnIndexOrThrow("iddanhmuc")));
                ts.setNgaymua(cursor.getString(cursor.getColumnIndexOrThrow("ngaymua")));
                ts.setTinhtrang(cursor.getString(cursor.getColumnIndexOrThrow("tinhtrang")));
                ts.setGiatri(cursor.getInt(cursor.getColumnIndexOrThrow("giatri")));
                ts.setVitri(cursor.getString(cursor.getColumnIndexOrThrow("vitri")));
                ts.setGhichu(cursor.getString(cursor.getColumnIndexOrThrow("ghichu")));
                ts.setBaohanhStart(cursor.getString(cursor.getColumnIndexOrThrow("baohanhStart")));
                ts.setBaohanhEnd(cursor.getString(cursor.getColumnIndexOrThrow("baohanhEnd")));
                ts.setIdtk(cursor.getInt(cursor.getColumnIndexOrThrow("idtk")));
                ts.setSoluong(cursor.getInt(cursor.getColumnIndexOrThrow("soluong")));

                // Lấy ảnh dạng byte[]
                int colIndexAnh = cursor.getColumnIndex("anhts");
                if (colIndexAnh != -1 && !cursor.isNull(colIndexAnh)) {
                    ts.setAnhts(cursor.getBlob(colIndexAnh));
                }
                list.add(ts);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return list;
    }

    public boolean updateTaiSan(TaiSan ts) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("tents", ts.getTents());
        values.put("mota", ts.getMota());
        values.put("iddanhmuc", ts.getIddanhmuc());
        values.put("giatri", ts.getGiatri());
        values.put("ngaymua", ts.getNgaymua());
        values.put("tinhtrang", ts.getTinhtrang());
        values.put("vitri", ts.getVitri());
        values.put("ghichu", ts.getGhichu());
        values.put("baohanhStart", ts.getBaohanhStart());
        values.put("baohanhEnd", ts.getBaohanhEnd());
        values.put("idtk", idTK);
        values.put("soluong", ts.getSoluong());

        // Nếu có ảnh thì cập nhật
        if (ts.getAnhts() != null && ts.getAnhts().length > 0) {
            values.put("anhts", ts.getAnhts());
        }

        int rows = db.update(
                "taisan",
                values,
                "idts = ?",
                new String[]{String.valueOf(ts.getIdts())}
        );

        db.close();
        return rows > 0; // Trả về true nếu có bản ghi được cập nhật
    }

    public boolean insertTaiSan(TaiSan ts) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("tents", ts.getTents());
        values.put("mota", ts.getMota());
        values.put("iddanhmuc", ts.getIddanhmuc());
        values.put("giatri", ts.getGiatri());
        values.put("ngaymua", ts.getNgaymua());
        values.put("tinhtrang", ts.getTinhtrang());
        values.put("vitri", ts.getVitri());
        values.put("ghichu", ts.getGhichu());
        values.put("baohanhStart", ts.getBaohanhStart());
        values.put("baohanhEnd", ts.getBaohanhEnd());
        values.put("idtk", idTK);
        values.put("soluong", ts.getSoluong());

        // Nếu có ảnh thì cập nhật
        if (ts.getAnhts() != null && ts.getAnhts().length > 0) {
            values.put("anhts", ts.getAnhts());
        }

        long result = db.insert("taisan", null, values);
        db.close();

        return result != -1; // true nếu thành công
    }

    public boolean deleteTaiSan(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete("taisan", "idts = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0; // true nếu xóa thành công
    }

    public long getTongGiaTriTaiSan() {
        long tongGiaTri = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT SUM(giatri) AS tong FROM taisan WHERE idtk = ? ", new String[]{String.valueOf(idTK)});

        if (cursor != null && cursor.moveToFirst()) {
            tongGiaTri = cursor.getLong(cursor.getColumnIndexOrThrow("tong"));
            cursor.close();
        }

        db.close();
        return tongGiaTri;
    }

    public List<TaiSan> searchTaiSanByName(String keyword) {
        List<TaiSan> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM taisan WHERE tents LIKE ? AND idtk = ?";
        String[] args = new String[]{"%" + keyword + "%", String.valueOf(idTK)};
        Cursor cursor = db.rawQuery(sql, args);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                TaiSan ts = new TaiSan();
                ts.setIdts(cursor.getInt(cursor.getColumnIndexOrThrow("idts")));
                ts.setTents(cursor.getString(cursor.getColumnIndexOrThrow("tents")));
                ts.setMota(cursor.getString(cursor.getColumnIndexOrThrow("mota")));
                ts.setIddanhmuc(cursor.getInt(cursor.getColumnIndexOrThrow("iddanhmuc")));
                ts.setNgaymua(cursor.getString(cursor.getColumnIndexOrThrow("ngaymua")));
                ts.setTinhtrang(cursor.getString(cursor.getColumnIndexOrThrow("tinhtrang")));
                ts.setGiatri(cursor.getInt(cursor.getColumnIndexOrThrow("giatri")));
                ts.setVitri(cursor.getString(cursor.getColumnIndexOrThrow("vitri")));
                ts.setGhichu(cursor.getString(cursor.getColumnIndexOrThrow("ghichu")));
                ts.setBaohanhStart(cursor.getString(cursor.getColumnIndexOrThrow("baohanhStart")));
                ts.setBaohanhEnd(cursor.getString(cursor.getColumnIndexOrThrow("baohanhEnd")));
                ts.setIdtk(cursor.getInt(cursor.getColumnIndexOrThrow("idtk")));
                ts.setSoluong(cursor.getInt(cursor.getColumnIndexOrThrow("soluong")));

                // Nếu có ảnh BLOB
                int idxAnh = cursor.getColumnIndex("anhts");
                if (idxAnh != -1 && !cursor.isNull(idxAnh)) {
                    ts.setAnhts(cursor.getBlob(idxAnh));
                }
                list.add(ts);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return list;
    }

    public DanhMuc getDanhMucById(int id) {
        DanhMuc danhMuc = null;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM danhmuc WHERE iddanhmuc = ?",
                new String[]{String.valueOf(id)}
        );

        if (cursor != null && cursor.moveToFirst()) {
            danhMuc = new DanhMuc();
            danhMuc.setIddanhmuc(cursor.getInt(cursor.getColumnIndexOrThrow("iddanhmuc")));
            danhMuc.setTendm(cursor.getString(cursor.getColumnIndexOrThrow("tendm")));
            danhMuc.setIdtk(cursor.getInt(cursor.getColumnIndexOrThrow("idtk")));
        }

        if (cursor != null) cursor.close();
        db.close();

        return danhMuc;
    }

    public TaiSan getTaiSanById(int id) {
        TaiSan ts = new TaiSan();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM taisan WHERE idts = ? AND idtk = ?",
                new String[]{String.valueOf(id),String.valueOf(idTK)});

        if (cursor != null && cursor.moveToFirst()) {
            ts.setIdts(cursor.getInt(cursor.getColumnIndexOrThrow("idts")));
            ts.setTents(cursor.getString(cursor.getColumnIndexOrThrow("tents")));
            ts.setMota(cursor.getString(cursor.getColumnIndexOrThrow("mota")));
            ts.setIddanhmuc(cursor.getInt(cursor.getColumnIndexOrThrow("iddanhmuc")));
            ts.setNgaymua(cursor.getString(cursor.getColumnIndexOrThrow("ngaymua")));
            ts.setTinhtrang(cursor.getString(cursor.getColumnIndexOrThrow("tinhtrang")));
            ts.setGiatri(cursor.getInt(cursor.getColumnIndexOrThrow("giatri")));
            ts.setVitri(cursor.getString(cursor.getColumnIndexOrThrow("vitri")));
            ts.setGhichu(cursor.getString(cursor.getColumnIndexOrThrow("ghichu")));
            ts.setBaohanhStart(cursor.getString(cursor.getColumnIndexOrThrow("baohanhStart")));
            ts.setBaohanhEnd(cursor.getString(cursor.getColumnIndexOrThrow("baohanhEnd")));
            ts.setIdtk(cursor.getInt(cursor.getColumnIndexOrThrow("idtk")));
            ts.setSoluong(cursor.getInt(cursor.getColumnIndexOrThrow("soluong")));
            // Nếu có ảnh BLOB
            int idxAnh = cursor.getColumnIndex("anhts");
            if (idxAnh != -1 && !cursor.isNull(idxAnh)) {
                ts.setAnhts(cursor.getBlob(idxAnh));
            }
        }

        if (cursor != null) cursor.close();
        db.close();

        return ts;
    }

    public boolean insertDanhMuc(String tenDanhMuc) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Kiểm tra trùng tên danh mục với idtk
        Cursor cursor = db.rawQuery(
                "SELECT * FROM danhmuc WHERE tendm = ? AND idtk = ?",
                new String[]{tenDanhMuc, String.valueOf(idTK)}
        );

        boolean exists = (cursor != null && cursor.moveToFirst());
        if (cursor != null) cursor.close();

        if (exists) {
            // Trùng tên => return false
            return false;
        } else {
            // Không trùng => insert
            ContentValues values = new ContentValues();
            values.put("tendm", tenDanhMuc);
            values.put("idtk", idTK);

            long result = db.insert("danhmuc", null, values);
            return result != -1; // thành công => true, thất bại => false
        }
    }

    // Hàm update danh mục
    public boolean updateDanhMuc(int idDanhMuc, String tenMoi) {
        SQLiteDatabase db = this.getWritableDatabase();

        // 1. Kiểm tra trùng tên với danh mục khác
        Cursor cursor = db.rawQuery("SELECT * FROM danhmuc WHERE tendm = ? AND idtk = ?",
                new String[]{tenMoi, String.valueOf(idTK)});

        boolean exists = cursor.moveToFirst(); // nếu có bản ghi → tên bị trùng
        cursor.close();

        if (exists) {
            db.close();
            return false; // tên bị trùng → không cho sửa
        }

        // 2. Không trùng thì tiến hành update
        ContentValues values = new ContentValues();
        values.put("tendm", tenMoi);

        int rows = db.update("danhmuc", values, "iddanhmuc = ?", new String[]{String.valueOf(idDanhMuc)});
        db.close();

        return rows > 0; // true nếu update thành công
    }
    public boolean deleteDanhMuc(int idDanhMuc) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("danhmuc", "iddanhmuc = ?", new String[]{String.valueOf(idDanhMuc)});
        int rows = db.delete("taisan", "iddanhmuc = ?", new String[]{String.valueOf(idDanhMuc)});
        db.close();
        return result > 0 && rows > 0; // true nếu xóa thành công
    }

}
