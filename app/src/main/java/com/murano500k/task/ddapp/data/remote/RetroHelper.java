package com.murano500k.task.ddapp.data.remote;

import com.murano500k.task.ddapp.data.Student;
import com.murano500k.task.ddapp.data.StudentArray;

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

    public List<Student> getStudents() throws IOException {
        List<Student> students=null;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .client(new OkHttpClient())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        retrofit2.Call<StudentArray> call=apiService.getStudentArray();
        if(call==null) return null;

        Response<StudentArray> response=call.execute();
        if(response!=null && response.isSuccessful()){
            StudentArray studentArray =response.body();
            if(studentArray!=null && studentArray.getStudents()!=null){
                students=studentArray.getStudents();
            }
        }
        return students;
    }
}
