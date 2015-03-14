package com.carlisle.songtaste.provider;

import com.carlisle.songtaste.modle.AlbumDetailInfo;
import com.carlisle.songtaste.modle.CollectionResult;
import com.carlisle.songtaste.modle.FMAlbumResult;
import com.carlisle.songtaste.modle.FMHotResult;
import com.carlisle.songtaste.modle.FMNewResult;
import com.carlisle.songtaste.modle.FMTagResult;
import com.carlisle.songtaste.modle.Result;
import com.carlisle.songtaste.modle.SongDetailInfo;
import com.carlisle.songtaste.modle.TagDetailResult;
import com.carlisle.songtaste.modle.User;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by chengxin on 2/26/15.
 */
public interface SongtasteApi {

    @GET("/rec_list.php")
    public Observable<FMNewResult> recList(@Query("p") String page, @Query("n") String number,
                                           @Query("tmp") String tmp, @Query("callback") String callback);

    @GET("/hot_song.php")
    public Observable<FMHotResult> hotSong(@Query("p") String page, @Query("n") String number,
                                           @Query("tmp") String tmp, @Query("callback") String callback);

    @GET("/tag_list.php")
    public Observable<FMTagResult> tagList(@Query("tmp") String tmp, @Query("callback") String callback);

    @GET("/tag.php")
    public Observable<TagDetailResult> tag(@Query("key") String key, @Query("t") String t, @Query("p") String p, @Query("n") String n,
                                     @Query("tmp") String tmp, @Query("callback") String callback, @Query("code") String code);

    @GET("/hot_albums.php")
    public Observable<FMAlbumResult> hotAlbums(@Query("tmp") String tmp, @Query("callback") String callback);

    @GET("/album_song.php")
    public Observable<AlbumDetailInfo> albumSong(@Query("aid") String aid, @Query("p") String p, @Query("n") String n,
                                                 @Query("tmp") String tmp, @Query("callback") String callback, @Query("code") String code);

    @GET("/collection_song.php")
    public Observable<CollectionResult> collectionSong(@Query("uid") String uid, @Query("p") String page, @Query("n") String number,
                                                       @Query("tmp") String tmp, @Query("callback") String callback, @Query("code") String code);

    @GET("/isdmbind.php")
    public Observable<User> isDMBind(@Query("id") String id, @Query("format") String format);

    @GET("/songurl.php")
    public Observable<SongDetailInfo> songUrl(@Query("songid") String songid, @Query("version") String version);

    @GET("/collection.php")
    public Observable<Result> collection(@Query("uid") String uid, @Query("songid") String songid, @Query("format") String format);

    @GET("/support.php")
    public Observable<Result> support(@Query("uid") String uid, @Query("songid") String songid, @Query("format") String format);

}
