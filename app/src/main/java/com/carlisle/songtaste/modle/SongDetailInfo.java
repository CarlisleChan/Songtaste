package com.carlisle.songtaste.modle;

import android.os.Parcel;
import android.os.Parcelable;

import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

/**
 * Created by chengxin on 2/26/15.
 */
@ParcelablePlease
public class SongDetailInfo implements Parcelable {
    public enum SongType {
        LOCAL_SONG,
        SONGTASTE_SONG
    }

    public SongType songType = SongType.SONGTASTE_SONG;

    public int code;
    public String singer_name;
    public String song_name;
    public String url;
    public String Mlength;
    public String Msize;
    public String Mbitrate;
    public String iscollection;
    public String mediaId;

    // songtaste
    public String songname;
    public String singername;
    public String albumArt;

    // local
    public String album;
    public String albumid;
    public String size;

    public SongType getSongType() {
        return songType;
    }

    public void setSongType(SongType songType) {
        this.songType = songType;
    }

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

    public String getIscollection() {
        return iscollection;
    }

    public void setIscollection(String iscollection) {
        this.iscollection = iscollection;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
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

    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAlbumid() {
        return albumid;
    }

    public void setAlbumid(String albumid) {
        this.albumid = albumid;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        SongDetailInfoParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static final Creator<SongDetailInfo> CREATOR = new Creator<SongDetailInfo>() {
        public SongDetailInfo createFromParcel(Parcel source) {
            SongDetailInfo target = new SongDetailInfo();
            SongDetailInfoParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public SongDetailInfo[] newArray(int size) {
            return new SongDetailInfo[size];
        }
    };
}
