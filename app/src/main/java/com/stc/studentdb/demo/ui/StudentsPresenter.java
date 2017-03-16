/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stc.studentdb.demo.ui;

import android.util.Log;

import com.stc.studentdb.demo.data.StudentsDataSource;
import com.stc.studentdb.demo.data.json.Course;
import com.stc.studentdb.demo.data.json.Student;

import java.util.List;
import java.util.Objects;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class StudentsPresenter implements StudentsContract.Presenter{
    private static final String TAG = "StudentsPresenter";
    private StudentsDataSource dataSource;
    private StudentsContract.View mView;
    private Course mFilter;

    public StudentsPresenter(StudentsDataSource dataSource, StudentsContract.View view) {
        this.dataSource = dataSource;
        this.mView = view;
        mView.setPresenter(this);
    }




    private void initFilterButton(Course selectedFilter) {
        dataSource.getAllCourses()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Course>>() {
                    @Override
                    public void accept(List<Course> courses) throws Exception {
                        mView.showFilterButton(courses, selectedFilter);
                    }
                });
    }

    @Override
    public void subscribe() {
        Log.d(TAG, "subscribe:");
        mFilter=null;
        processAction(mFilter, 0, false);
    }

    @Override
    public void loadMore(int offset) {
        Log.d(TAG, "loadMore: "+offset);
        processAction(mFilter, offset, true);
    }


    @Override
    public void filterSelected(Course course) {
        Log.d(TAG, "filterSelected:");
        if(!Objects.equals(course,mFilter)){
            Log.d(TAG, "new filter");
            mFilter=course;
            processAction(mFilter, 0, false);
        }

    }

    private void processAction(Course filter, int offset, boolean update){
        dataSource.getStudents(filter, offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Student>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe");
                    }
                    @Override
                    public void onNext(List<Student> value) {
                        mView.addItems(value, update);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ",e );
                        mView.showError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: finished with Students");
                        if(!update) initFilterButton(filter);
                    }
                });
    }

    @Override
    public void unsubscribe() {



    }
}

