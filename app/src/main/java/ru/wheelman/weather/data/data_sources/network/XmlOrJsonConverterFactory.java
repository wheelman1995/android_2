package ru.wheelman.weather.data.data_sources.network;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class XmlOrJsonConverterFactory extends Converter.Factory {

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {

        for (int i = 0; i < annotations.length; i++) {
            Class<? extends Annotation> annotationType = annotations[i].annotationType();

            if (annotationType == Xml.class) {
                return SimpleXmlConverterFactory.create().responseBodyConverter(type, annotations, retrofit);
            }

            if (annotationType == Json.class) {
                return GsonConverterFactory.create().responseBodyConverter(type, annotations, retrofit);
            }
        }

        return super.responseBodyConverter(type, annotations, retrofit);
    }
}
