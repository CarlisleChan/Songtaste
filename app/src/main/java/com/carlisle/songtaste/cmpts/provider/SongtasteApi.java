package com.carlisle.songtaste.cmpts.provider;

import com.carlisle.songtaste.cmpts.modle.CollectionResult;
import com.carlisle.songtaste.cmpts.modle.FMHotResult;
import com.carlisle.songtaste.cmpts.modle.FMNewResult;
import com.carlisle.songtaste.cmpts.modle.Result;
import com.carlisle.songtaste.cmpts.modle.SongDetailInfo;
import com.carlisle.songtaste.cmpts.modle.User;

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

    @GET("/collection_song.php")
    public Observable<CollectionResult> collectionSong(@Query("uid") String uid, @Query("p") String page, @Query("n") String number,
                                                       @Query("tmp") String tmp, @Query("callback") String callback, @Query("code") String code);

    @GET("/isdmbind.php")
    public Observable<User> isDMBind(@Query("id") String id, @Query("format") String format);

    @GET("/songurl.php")
    public Observable<SongDetailInfo> songUrl(@Query("songid") String songid, @Query("uid") String uid, @Query("version") String version);

    @GET("/collection.php")
    public Observable<Result> collection(@Query("uid") String uid, @Query("songid") String songid, @Query("format") String format);

    @GET("/support.php")
    public Observable<Result> support(@Query("uid") String uid, @Query("songid") String songid, @Query("format") String format);

}
