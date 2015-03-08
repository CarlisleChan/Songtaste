package com.carlisle.songtaste.provider.converter;

import android.util.Log;

import com.carlisle.songtaste.modle.CollectionResult;
import com.carlisle.songtaste.modle.FMAlbumResult;
import com.carlisle.songtaste.modle.FMHotResult;
import com.carlisle.songtaste.modle.FMNewResult;
import com.carlisle.songtaste.modle.FMTagResult;
import com.carlisle.songtaste.modle.Song;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

/**
 * Created by chengxin on 2/26/15.
 */
public class GsonConverter implements Converter {

    public enum ConverterType {
        COLLECTION_RESULT,
        FM_ALBUM_RESULT,
        FM_HOT_RESULT,
        FM_NEW_RESULT,
        FM_TAG_RESULT
    }

    ConverterType converterType = null;

    public GsonConverter(ConverterType converterType) {
        this.converterType = converterType;
    }

    @Override
    public Object fromBody(TypedInput body, Type type)
            throws ConversionException {

        String strResult = null;
        try {
            strResult = fromStream(body.in());

        } catch (IOException e) {
            e.printStackTrace();
        }

        strResult = strResult.substring(strResult.indexOf("(") + 1, strResult.lastIndexOf(")"));

        Log.i("strResult--->    ",strResult);
        Log.i("converterType--->    ","" + converterType);

        CollectionResult test = new CollectionResult();
        ArrayList<Song> data = new ArrayList<>();
        Song song = new Song();
        song.setSong_name("123");
        song.setSinger_name("ewqewq");
        data.add(song);
        test.setData(data);
        test.setSupport_total("11");
        String testStr = new Gson().toJson(test);
        Log.i("Gson--->    ", testStr);

        switch (converterType) {
            case COLLECTION_RESULT:
                CollectionResult collectionResult = new Gson().fromJson(strResult, CollectionResult.class);
//                Log.i("collection------>",collectionResult.getCollection_total());
                return collectionResult;
            case FM_ALBUM_RESULT:
                FMAlbumResult fmAlbumResult = new Gson().fromJson(strResult, FMAlbumResult.class);
                return fmAlbumResult;
            case FM_HOT_RESULT:
                FMHotResult fmHotResult = new Gson().fromJson(strResult, FMHotResult.class);
                return fmHotResult;
            case FM_NEW_RESULT:
                FMNewResult fmNewResult = new Gson().fromJson(strResult, FMNewResult.class);
                return fmNewResult;
            case FM_TAG_RESULT:
                FMTagResult fmTagResult = new Gson().fromJson(strResult, FMTagResult.class);
                return fmTagResult;
        }

        return null;
    }

    @Override
    public TypedOutput toBody(Object o) {
        return null;
    }

    // Custom method to convert stream from request to string
    public static String fromStream(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
            out.append(newLine);
        }
        return out.toString();
    }
}
