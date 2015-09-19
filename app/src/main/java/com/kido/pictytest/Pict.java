package com.kido.pictytest;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pict {

    @SerializedName("id")
    @Expose(serialize = true, deserialize = true)
    private long id;
    @SerializedName("url")
    @Expose(serialize = true, deserialize = true)
    private String url;
    @SerializedName("flag")
    @Expose(serialize = true, deserialize = true)
    private String flag;
    @SerializedName("os")
    @Expose(serialize = true, deserialize = true)
    private int os;

    public Pict(long id, String url, String flag, int os) {
        this.id = id;
        this.url = url;
        this.flag = flag;
        this.os = os;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public int getOs() {
        return os;
    }

    public void setOs(int os) {
        this.os = os;
    }
}
