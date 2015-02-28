package com.carlisle.songtaste.modle;

/**
 * Created by chengxin on 2/26/15.
 */
public class Song {
    public int code;
    public String singer_name;
    public String song_name;
    public String url;
    public String Mlength;
    public String Msize;
    public String Mbitrate;
    public String iscollection;

    public String songid;
    public String songname;
    public String singername;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getSinger_name() {
        return singer_name;
    }

    public void setSinger_name(String singer_name) {
        this.singer_name = singer_name;
    }

    public String getSong_name() {
        return song_name;
    }

    public void setSong_name(String song_name) {
        this.song_name = song_name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMlength() {
        return Mlength;
    }

    public void setMlength(String mlength) {
        Mlength = mlength;
    }

    public String getMsize() {
        return Msize;
    }

    public void setMsize(String msize) {
        Msize = msize;
    }

    public String getMbitrate() {
        return Mbitrate;
    }

    public void setMbitrate(String mbitrate) {
        Mbitrate = mbitrate;
    }

    public String getCollection() {
        return iscollection;
    }

    public void setCollection(String iscollection) {
        this.iscollection = iscollection;
    }

    public String getSongid() {
        return songid;
    }

    public void setSongid(String songid) {
        this.songid = songid;
    }

    public String getSongname() {
        return songname;
    }

    public void setSongname(String songname) {
        this.songname = songname;
    }

    public String getSingername() {
        return singername;
    }

    public void setSingername(String singername) {
        this.singername = singername;
    }
}
