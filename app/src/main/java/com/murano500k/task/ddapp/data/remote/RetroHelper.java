package com.murano500k.task.ddapp.data.remote;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by artem on 2/15/17.
 */

public class RetroHelper {
    private static final String API_BASE_URL="https://ddapp-sfa-api-dev.azurewebsites.net/api/test/";

    public List<com.murano500k.task.ddapp.data.json.Student> getStudents() throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        retrofit2.Call<List<com.murano500k.task.ddapp.data.json.Student>> call=apiService.getStudentArray();
        if(call==null) return null;

        Response<List<com.murano500k.task.ddapp.data.json.Student>> response=call.execute();
        if(response!=null && response.isSuccessful()){
            List<com.murano500k.task.ddapp.data.json.Student>students =response.body();
            return students;

        }
        return null;
    }
}
