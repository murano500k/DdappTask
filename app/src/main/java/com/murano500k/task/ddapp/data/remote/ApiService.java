package com.murano500k.task.ddapp.data.remote;

import com.murano500k.task.ddapp.data.json.Student;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by artem on 2/15/17.
 */

public interface ApiService {

    @GET("students")
    Call<List<Student>> getStudentArray();
}