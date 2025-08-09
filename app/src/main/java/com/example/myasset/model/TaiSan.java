package com.example.myasset.model;

import java.sql.Blob;

public class TaiSan {
    private int idts;
    private int iddanhmuc;
    private int idtk;
    private String tents;
    private String mota;
    private int giatri;
    private String ngaymua;
    private String tinhtrang;
    private String vitri;
    private Blob anhts;
    private String ghichu;
    private String baohanhStart;
    private String baohanhEnd;

    public TaiSan() {
    }

    public TaiSan(int idts, int iddanhmuc, int idtk, String tents, String mota, int giatri, String ngaymua, String tinhtrang, String vitri, Blob anhts, String ghichu, String baohanhStart, String baohanhEnd) {
        this.idts = idts;
        this.iddanhmuc = iddanhmuc;
        this.idtk = idtk;
        this.tents = tents;
        this.mota = mota;
        this.giatri = giatri;
        this.ngaymua = ngaymua;
        this.tinhtrang = tinhtrang;
        this.vitri = vitri;
        this.anhts = anhts;
        this.ghichu = ghichu;
        this.baohanhStart = baohanhStart;
        this.baohanhEnd = baohanhEnd;
    }

    public int getIdts() {
        return idts;
    }

    public void setIdts(int idts) {
        this.idts = idts;
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

    public String getTents() {
        return tents;
    }

    public void setTents(String tents) {
        this.tents = tents;
    }

    public String getMota() {
        return mota;
    }

    public void setMota(String mota) {
        this.mota = mota;
    }

    public int getGiatri() {
        return giatri;
    }

    public void setGiatri(int giatri) {
        this.giatri = giatri;
    }

    public String getNgaymua() {
        return ngaymua;
    }

    public void setNgaymua(String ngaymua) {
        this.ngaymua = ngaymua;
    }

    public String getTinhtrang() {
        return tinhtrang;
    }

    public void setTinhtrang(String tinhtrang) {
        this.tinhtrang = tinhtrang;
    }

    public String getVitri() {
        return vitri;
    }

    public void setVitri(String vitri) {
        this.vitri = vitri;
    }

    public Blob getAnhts() {
        return anhts;
    }

    public void setAnhts(Blob anhts) {
        this.anhts = anhts;
    }

    public String getGhichu() {
        return ghichu;
    }

    public void setGhichu(String ghichu) {
        this.ghichu = ghichu;
    }

    public String getBaohanhStart() {
        return baohanhStart;
    }

    public void setBaohanhStart(String baohanhStart) {
        this.baohanhStart = baohanhStart;
    }

    public String getBaohanhEnd() {
        return baohanhEnd;
    }

    public void setBaohanhEnd(String baohanhEnd) {
        this.baohanhEnd = baohanhEnd;
    }
}
