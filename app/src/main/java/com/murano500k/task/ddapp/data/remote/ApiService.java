package com.murano500k.task.ddapp.data.remote;

import com.murano500k.task.ddapp.data.StudentArray;

import retrofit2.http.GET;

/**
 * Created by artem on 2/15/17.
 */

public interface ApiService {

    @GET("students")
    retrofit2.Call<StudentArray> getStudentArray();
}