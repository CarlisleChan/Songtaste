package com.carlisle.songtaste.provider;

import com.carlisle.songtaste.provider.exception.ResponseError;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.Converter;

/**
 * Created by chengxin on 2/26/15.
 */
public class RetrofitFactory {

    public static RestAdapter getRestAdapter(Converter converter) {
        RestAdapter.Builder builder = new RestAdapter.Builder().setEndpoint("http://www.songtaste.com/api/android");
        builder.setLogLevel(RestAdapter.LogLevel.FULL);
        builder.setConverter(converter);
//        builder.setClient(new OkClient());
        builder.setErrorHandler(new ErrorHandler() {
            @Override
            public Throwable handleError(RetrofitError cause) {
                Response response = cause.getResponse();
                if (response == null) {
                    return cause;
                }
                BufferedReader reader = null;
                String msg = null;
                try {
                    InputStream is = response.getBody().in();
                    reader = new BufferedReader(new InputStreamReader(is));
                    StringBuffer buffer = new StringBuffer();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    msg = buffer.toString();
                } catch (IOException e) {

                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {

                        }
                    }
                }
                ResponseError error = new ResponseError(response.getStatus(), msg);
                return error;
            }
        });

        RestAdapter restAdapter = builder.build();
        return restAdapter;

    }
}
