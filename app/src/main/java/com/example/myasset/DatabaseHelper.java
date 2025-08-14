package com.example.myasset;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myasset.model.TaiKhoan;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;

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
                "SELECT idtk FROM taikhoan WHERE tk = ? AND matkhau = ?",
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

    public boolean registerUser(String tenTK, String tk, String matkhau) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Kiểm tra trùng tài khoản
        Cursor cursor = db.rawQuery("SELECT idtk FROM taikhoan WHERE tk = ?", new String[]{tk});
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
        db.close();
        return result != -1;
    }


}
