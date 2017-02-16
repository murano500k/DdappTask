package com.murano500k.task.ddapp.data.remote;

import android.content.Context;

import com.murano500k.task.ddapp.R;
import com.murano500k.task.ddapp.data.Student;
import com.murano500k.task.ddapp.data.StudentArray;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by artem on 2/15/17.
 */

public class RetroHelper {
    private static final String API_BASE_URL="https://ddapp-sfa-api-dev.azurewebsites.net/api/test/";
/*
    public List<Student> getStudents() throws IOException {
        Serializer serializer = new Persister(new Format("<?xml version=\"1.0\" encoding= \"UTF-8\" ?>"));

        List<Student> students=null;
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(new CallAdapter.Factory() {
                    @Override
                    public CallAdapter<?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
                        return null;
                    }
                })
                .baseUrl(API_BASE_URL)
                .client(new OkHttpClient())
                .addConverterFactory(SimpleXmlConverterFactory.create(serializer ))

                .build();
        R
        ApiService apiService = retrofit.create(ApiService.class);
        retrofit2.Call<StudentArray> call=apiService.getStudentArray();
        if(call==null) return null;
        System.out.println("call = "+call.toString());
        System.out.println("request = "+call.request().toString());

        Response<StudentArray> response=call.execute();

        System.out.println("response = "+response.toString());
        if(response!=null && response.isSuccessful()){
            StudentArray studentArray =response.body();

            if(studentArray!=null && studentArray.getStudents()!=null){
                students=studentArray.getStudents();
            }
        }
        else System.out.println(response.errorBody());
        return students;
    }*/
public String getStudents(Context r) throws XmlPullParserException, IOException {
    /*String xml = r.getString(R.xml.data);
    */XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
    factory.setNamespaceAware(true);
    //XmlResourceParser xrp = context.getResources().getXml(R.xml.encounters);
    XmlPullParser xpp = r.getResources().getXml(R.xml.data);


    int eventType = xpp.getEventType();
    while (eventType != XmlPullParser.END_DOCUMENT) {
        if(eventType == XmlPullParser.START_DOCUMENT) {
            System.out.println("Start document");
        } else if(eventType == XmlPullParser.START_TAG) {
            System.out.println("Start tag "+xpp.getName());
        } else if(eventType == XmlPullParser.END_TAG) {
            System.out.println("End tag "+xpp.getName());
        } else if(eventType == XmlPullParser.TEXT) {
            System.out.println("Text "+xpp.getText());
        }
        eventType = xpp.next();
    }
    System.out.println("End document");
    return xpp.toString();
}
    /*
    public void resttemplate(){
        SimpleXmlConverterFactory.create(serializer );
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .client(new OkHttpClient())
                .addConverterFactory(SimpleXmlConverterFactory.create(serializer ))
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        restTemplate.getMessageConverters().add(new SimpleXmlHttpMessageConverter(serializer));
        restTemplate.postForObject(URL, udata, String.class);
    }*/
    public void test1(){
        List<Student> students=null;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .client(new OkHttpClient())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        retrofit2.Call<StudentArray> call=apiService.getStudentArray();
        System.out.println("call = "+call.toString());
        System.out.println("request = "+call.request().toString());

        Response<StudentArray> response= null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("response = "+response.toString());
        if(response!=null && response.isSuccessful()){
            StudentArray studentArray =response.body();

            if(studentArray!=null && studentArray.getStudents()!=null){
                students=studentArray.getStudents();
            }
        }
        else System.out.println(response.errorBody());
    }
}
