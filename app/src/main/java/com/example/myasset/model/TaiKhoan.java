package com.example.myasset.model;

import java.sql.Blob;

public class TaiKhoan {
    private int idtk;
    private String tentk;
    private Blob anhtk;
    private String taikhoan;
    private String matkhau;

    public TaiKhoan() {
    }

    public TaiKhoan(int idtk, String tentk, Blob anhtk, String taikhoan, String matkhau) {
        this.idtk = idtk;
        this.tentk = tentk;
        this.anhtk = anhtk;
        this.taikhoan = taikhoan;
        this.matkhau = matkhau;
    }

    public int getIdtk() {
        return idtk;
    }

    public void setIdtk(int idtk) {
        this.idtk = idtk;
    }

    public String getTentk() {
        return tentk;
    }

    public void setTentk(String tentk) {
        this.tentk = tentk;
    }

    public Blob getAnhtk() {
        return anhtk;
    }

    public void setAnhtk(Blob anhtk) {
        this.anhtk = anhtk;
    }

    public String getTaikhoan() {
        return taikhoan;
    }

    public void setTaikhoan(String taikhoan) {
        this.taikhoan = taikhoan;
    }

    public String getMatkhau() {
        return matkhau;
    }

    public void setMatkhau(String matkhau) {
        this.matkhau = matkhau;
    }
}
