package com.stc.studentdb.demo.data.remote;

import com.stc.studentdb.demo.data.json.Student;

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