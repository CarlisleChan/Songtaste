package com.carlisle.songtaste.helpers;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.modle.SongDetailInfo;
import com.carlisle.songtaste.utils.Utils;
import com.google.gson.Gson;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlisle on 3/17/15.
 */
public class LocalSongHelper {
    private static LocalSongHelper localSongHelper;

    public static LocalSongHelper getInstance() {
        if (localSongHelper != null) {
            return localSongHelper;
        }
        return null;
    }

    public static void scanSongs(Context context) {

        // 版本兼容处理
        if (Utils.getSystemVersion() < 19) {
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri
                    .parse("file://" + Environment.getExternalStorageDirectory())));

        } else {
            MediaScannerConnection.scanFile(context,
                    new String[]{Environment.getExternalStorageDirectory().toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {

                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });
        }

    }

    public static List<SongDetailInfo> getSongList(Context context) {

        Cursor audioCursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.AudioColumns.TITLE);// 排序方式

        List<SongDetailInfo> songList = new ArrayList<SongDetailInfo>();
        for (int i = 0; i < audioCursor.getCount(); i++) {
            audioCursor.moveToNext();

            String strID = audioCursor.getString(audioCursor
                    .getColumnIndex(MediaStore.Audio.AudioColumns._ID));
            String strTitle = audioCursor.getString(audioCursor
                    .getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
            String strArtist = audioCursor.getString(audioCursor
                    .getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
            String strAlbum = audioCursor.getString(audioCursor
                    .getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM));
            String strAlbumID = audioCursor.getString(audioCursor
                    .getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
            String strDuration = audioCursor.getString(audioCursor
                    .getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
            String strSize = audioCursor.getString(audioCursor
                    .getColumnIndex(MediaStore.Audio.AudioColumns.SIZE));
            String strData = audioCursor.getString(audioCursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
            String strIsMusic = audioCursor.getString(audioCursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.IS_MUSIC));

            SongDetailInfo songDetailInfo = new SongDetailInfo();
            songDetailInfo.setId(strID);
            songDetailInfo.setSong_name(strTitle);
            songDetailInfo.setSinger_name(strArtist);
            songDetailInfo.setAlbum(strAlbum);
            songDetailInfo.setAlbumid(strAlbumID);
            songDetailInfo.setSize(strSize);
            songDetailInfo.setUrl(strData);
            songList.add(songDetailInfo);

            Log.d("==>", "" + new Gson().toJson(songDetailInfo));
        }
        audioCursor.close();
        return songList;
    }

    /**
     * get the album of the song
     *
     * @param context
     * @param song_id
     * @param album_id
     * @param allowdefault
     * @return
     */
    public static Bitmap getArtwork(Context context, long song_id,
                                    long album_id, boolean allowdefault) {
        if (album_id < 0) {
            // This is something that is not in the database, so get the album
            // art directly
            // from the file.
            if (song_id >= 0) {
                Bitmap bm = getArtworkFromFile(context, song_id, -1);
                if (bm != null) {
                    return bm;
                }
            }
            if (allowdefault) {
                return getDefaultArtwork(context);
            }
            return null;
        }
        ContentResolver res = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
        if (uri != null) {
            InputStream in = null;
            try {
                in = res.openInputStream(uri);
                return BitmapFactory.decodeStream(in, null, sBitmapOptions);
            } catch (FileNotFoundException ex) {
                // The album art thumbnail does not actually exist. Maybe the
                // user deleted it, or
                // maybe it never existed to begin with.
                Bitmap bm = getArtworkFromFile(context, song_id, album_id);
                if (bm != null) {
                    if (bm.getConfig() == null) {
                        bm = bm.copy(Bitmap.Config.RGB_565, false);
                        if (bm == null && allowdefault) {
                            return getDefaultArtwork(context);
                        }
                    }
                } else if (allowdefault) {
                    bm = getDefaultArtwork(context);
                }
                return bm;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ex) {
                }
            }
        }

        return null;
    }

    /**
     * get the album of the song from file
     *
     * @param context
     * @param songid
     * @param albumid
     * @return
     */
    private static Bitmap getArtworkFromFile(Context context, long songid,
                                             long albumid) {
        Bitmap bm = null;
        if (albumid < 0 && songid < 0) {
            throw new IllegalArgumentException(
                    "Must specify an album or a song id");
        }
        try {
            if (albumid < 0) {
                Uri uri = Uri.parse("content://media/external/audio/media/"
                        + songid + "/albumart");
                ParcelFileDescriptor pfd = context.getContentResolver()
                        .openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                }
            } else {
                Uri uri = ContentUris.withAppendedId(sArtworkUri, albumid);
                ParcelFileDescriptor pfd = context.getContentResolver()
                        .openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                }
            }
        } catch (FileNotFoundException ex) {

        }
        if (bm != null) {
        }
        return bm;
    }

    /**
     * get Default album of song
     *
     * @param context
     * @return
     */
    private static Bitmap getDefaultArtwork(Context context) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeStream(context.getResources()
                .openRawResource(R.drawable.bottom_no_album_big), null, opts);
    }

    private static final Uri sArtworkUri = Uri
            .parse("content://media/external/audio/albumart");
    private static final BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();

}
