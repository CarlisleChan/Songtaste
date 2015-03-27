package com.carlisle.songtaste.cmpts.provider.converter;

import com.alibaba.fastjson.JSON;
import com.carlisle.songtaste.cmpts.modle.AlbumDetailInfo;
import com.carlisle.songtaste.cmpts.modle.CollectionResult;
import com.carlisle.songtaste.cmpts.modle.FMAlbumResult;
import com.carlisle.songtaste.cmpts.modle.FMHotResult;
import com.carlisle.songtaste.cmpts.modle.FMNewResult;
import com.carlisle.songtaste.cmpts.modle.FMTagResult;
import com.carlisle.songtaste.cmpts.modle.TagDetailResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

/**
 * Created by chengxin on 2/26/15.
 */
public class JsonConverter implements Converter {

    public enum ConverterType {
        COLLECTION_RESULT,
        FM_ALBUM_RESULT,
        FM_HOT_RESULT,
        FM_NEW_RESULT,
        FM_TAG_RESULT,
        ALBUM_DETAIL,
        TAG_DETAIL
    }

    ConverterType converterType = null;

    public JsonConverter(ConverterType converterType) {
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

        switch (converterType) {
            case COLLECTION_RESULT:
                CollectionResult collectionResult = JSON.parseObject(strResult, CollectionResult.class);
                return collectionResult;
            case FM_ALBUM_RESULT:
                FMAlbumResult fmAlbumResult = JSON.parseObject(strResult, FMAlbumResult.class);
                return fmAlbumResult;
            case FM_HOT_RESULT:
                FMHotResult fmHotResult = JSON.parseObject(strResult, FMHotResult.class);
                return fmHotResult;
            case FM_NEW_RESULT:
                FMNewResult fmNewResult = JSON.parseObject(strResult, FMNewResult.class);
                return fmNewResult;
            case FM_TAG_RESULT:
                FMTagResult fmTagResult = JSON.parseObject(strResult, FMTagResult.class);
                return fmTagResult;
            case ALBUM_DETAIL:
                AlbumDetailInfo albumDetailInfo = JSON.parseObject(strResult, AlbumDetailInfo.class);
                return albumDetailInfo;
            case TAG_DETAIL:
                TagDetailResult tagDetailResult = JSON.parseObject(strResult, TagDetailResult.class);
                return tagDetailResult;
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
