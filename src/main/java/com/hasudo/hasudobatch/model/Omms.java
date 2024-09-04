package com.hasudo.hasudobatch.model;

import java.time.LocalDateTime;

public class Omms {
    private String bscFctNm;  // BSC_FCT_NM
    private String bscFctCd;  // BSC_FCT_CD
    private String sido;      // SIDO
    private String gugun;     // GUGUN
    private int fctCpc;       // FCT_CPC
    private double x;         // X
    private double y;         // Y
    private String geom;      // GEOM
    private int fid;          // FID

    private LocalDateTime createAt;

    public String getBscFctNm() {
        return bscFctNm;
    }

    public void setBscFctNm(String bscFctNm) {
        this.bscFctNm = bscFctNm;
    }

    public String getBscFctCd() {
        return bscFctCd;
    }

    public void setBscFctCd(String bscFctCd) {
        this.bscFctCd = bscFctCd;
    }

    public String getSido() {
        return sido;
    }

    public void setSido(String sido) {
        this.sido = sido;
    }

    public String getGugun() {
        return gugun;
    }

    public void setGugun(String gugun) {
        this.gugun = gugun;
    }

    public int getFctCpc() {
        return fctCpc;
    }

    public void setFctCpc(int fctCpc) {
        this.fctCpc = fctCpc;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getGeom() {
        return geom;
    }

    public void setGeom(String geom) {
        this.geom = geom;
    }

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    @Override
    public String toString() {
        return "Omms{" +
                "bscFctNm='" + bscFctNm + '\'' +
                ", bscFctCd='" + bscFctCd + '\'' +
                ", sido='" + sido + '\'' +
                ", gugun='" + gugun + '\'' +
                ", fctCpc=" + fctCpc +
                ", x=" + x +
                ", y=" + y +
                ", geom='" + geom + '\'' +
                ", fid=" + fid +
                ", createAt='" + createAt + '\'' +
                '}';
    }
}
