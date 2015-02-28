package com.carlisle.songtaste.provider.converter;

import android.util.Log;

import com.carlisle.songtaste.modle.Result;
import com.carlisle.songtaste.modle.Song;
import com.carlisle.songtaste.modle.User;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Type;

import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

/**
 * Created by chengxin on 2/27/15.
 */
public class XmlConverter implements Converter {

    public enum ConvterType {
        SONG,
        USER,
        COLLECTION,
        SUPPORT
    }

    private ConvterType convterType;

    public XmlConverter(ConvterType convterType) {
        this.convterType = convterType;
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

        Log.d("fromBody====>", strResult);

        switch (convterType) {
            case SONG:
                return parseSongXml(strResult);
            case SUPPORT:
                return parseResultXml(strResult);
            case USER:
                Log.d("USER====>", "====");
                return parseUserXml(strResult);
            case COLLECTION:
                return parseResultXml(strResult);
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

    private Song parseSongXml(String xml) {
        Song song = new Song();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));

            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:

                        String tagName = parser.getName();

                        if (tagName.equals("code")) {
                            song.setCode(Integer.valueOf(parser.nextText()));
                        } else if (tagName.equals("singer_name")) {
                            song.setSinger_name(parser.nextText());
                        } else if (tagName.equals("song_name")) {
                            song.setSong_name(parser.nextText());
                        } else if (tagName.equals("url")) {
                            song.setUrl(parser.nextText());
                        } else if (tagName.equals("Mlength")) {
                            song.setMlength(parser.nextText());
                        } else if (tagName.equals("Msize")) {
                            song.setMsize(parser.nextText());
                        } else if (tagName.equals("Mbitrate")) {
                            song.setMbitrate(parser.nextText());
                        } else if (tagName.equals("iscollection")) {
                            song.setCollection(parser.nextText());
                        }

                        break;

                    case XmlPullParser.END_TAG:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return song;
    }

    private User parseUserXml(String xml) {
        User user = new User();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));

            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:

                        String tagName = parser.getName();

                        if (tagName.equals("code")) {
                            user.setCode(Integer.valueOf(parser.nextText()));
                        } else if (tagName.equals("uid")) {
                            user.getData().setUid(Long.valueOf(parser.nextText()));
                        } else if (tagName.equals("name")) {
                            user.getData().setName(parser.nextText());
                        } else if (tagName.equals("avatar_small")) {
                            user.getData().setAvatar_small(parser.nextText());
                        } else if (tagName.equals("avatar_big")) {
                            user.getData().setAvatar_big(parser.nextText());
                        }

                        break;

                    case XmlPullParser.END_TAG:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return user;
    }


    private Result parseResultXml(String xml) {
        Result result = new Result();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));

            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:

                        String tagName = parser.getName();

                        if (tagName.equals("code")) {
                            result.setCode(Integer.valueOf(parser.nextText()));
                        } else if (tagName.equals("msg")) {
                            result.setMsg(parser.nextText());
                        }

                        break;

                    case XmlPullParser.END_TAG:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;
    }


}
