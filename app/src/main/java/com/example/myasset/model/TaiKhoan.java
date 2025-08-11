package com.example.myasset.model;

public class TaiKhoan {
    private int idtk;
    private String tentk;
    private String matkhau;
    private byte[] anhtk; // đổi từ Blob sang byte[]
    private String tk;
    private String sdt;
    private String gioitinh;
    private String ngaysinh;

    public TaiKhoan() {
    }

    public TaiKhoan(int idtk, String tentk, String matkhau, byte[] anhtk,
                    String tk, String sdt, String gioitinh, String ngaysinh) {
        this.idtk = idtk;
        this.tentk = tentk;
        this.matkhau = matkhau;
        this.anhtk = anhtk;
        this.tk = tk;
        this.sdt = sdt;
        this.gioitinh = gioitinh;
        this.ngaysinh = ngaysinh;
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

    public String getMatkhau() {
        return matkhau;
    }

    public void setMatkhau(String matkhau) {
        this.matkhau = matkhau;
    }

    public byte[] getAnhtk() {
        return anhtk;
    }

    public void setAnhtk(byte[] anhtk) {
        this.anhtk = anhtk;
    }

    public String getTk() {
        return tk;
    }

    public void setTk(String tk) {
        this.tk = tk;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getGioitinh() {
        return gioitinh;
    }

    public void setGioitinh(String gioitinh) {
        this.gioitinh = gioitinh;
    }

    public String getNgaysinh() {
        return ngaysinh;
    }

    public void setNgaysinh(String ngaysinh) {
        this.ngaysinh = ngaysinh;
    }
}
