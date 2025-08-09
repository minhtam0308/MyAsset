package com.example.myasset.model;

public class DanhMuc {
    private int iddanhmuc;
    private int idtk;
    private String tendm;

    public DanhMuc(int iddanhmuc, int idtk, String tendm) {
        this.iddanhmuc = iddanhmuc;
        this.idtk = idtk;
        this.tendm = tendm;
    }

    public DanhMuc() {
    }

    public int getIddanhmuc() {
        return iddanhmuc;
    }

    public void setIddanhmuc(int iddanhmuc) {
        this.iddanhmuc = iddanhmuc;
    }

    public int getIdtk() {
        return idtk;
    }

    public void setIdtk(int idtk) {
        this.idtk = idtk;
    }

    public String getTendm() {
        return tendm;
    }

    public void setTendm(String tendm) {
        this.tendm = tendm;
    }
}
