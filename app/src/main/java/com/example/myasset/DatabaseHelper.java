package com.example.myasset;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static String DB_NAME = "quanly_taisan.sqlite";
    private String DB_PATH;
    private Context mContext;
    private

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.mContext = context;
        this.DB_PATH = context.getDatabasePath(DB_NAME).getPath();
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

    public Cursor getCurrentUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        // Lấy thông tin user có ID = 1 (hoặc bạn có thể dùng SharedPreferences lưu userID đang đăng nhập)
        return db.rawQuery("SELECT tk, sdt, gioitinh, ngaysinh FROM taikhoan WHERE idtk = 2", null);
    }
}
