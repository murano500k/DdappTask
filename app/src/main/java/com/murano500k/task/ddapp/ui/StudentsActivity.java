/*
 * Copyright (C) 2015 The Android Open Source Project
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

package com.murano500k.task.ddapp.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.murano500k.task.ddapp.Injection;
import com.murano500k.task.ddapp.R;
import com.murano500k.task.ddapp.data.json.Course;
import com.murano500k.task.ddapp.data.json.Student;

import java.util.List;
import java.util.Objects;

public class StudentsActivity extends AppCompatActivity
        implements StudentsContract.View , MyListAdapter.ListClickListener{

    private static final String CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY";
    private static final String TAG = "ActivityTEST";

    private StudentsContract.Presenter presenter;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private MyListAdapter adapter;
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(linearLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                presenter.loadMore(page);
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
        adapter = new MyListAdapter(this);
        recyclerView.setAdapter(adapter);
        new StudentsPresenter(Injection.provideTasksRepository(getApplicationContext()),this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void showError(String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(StudentsActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void addItems(List<Student> students, boolean update) {
        if(update) {
            adapter.addItems(students);
        }else {
            adapter.newItems(students);
            scrollListener.resetState();

        }
    }

    @Override
    public void showFilterButton(List<Course> courses, Course selected) {
        Button button = new Button(this);
        button.setBackgroundResource(android.R.drawable.ic_menu_more);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog(courses, selected);
            }
        });
        ActionBar.LayoutParams layoutParams=new ActionBar.LayoutParams(Gravity.END);
        toolbar.addView(button);
    }

    private void showFilterDialog(List<Course> courses, Course selected) {
        String[] courseNames=new String [courses.size()];
        int i=0;
        int checked=-1;
        for(Course c: courses){
            courseNames[i]=c.getName();
            if(Objects.equals(c,selected))checked=i;
            i++;
        }
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(courseNames,checked,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                presenter.filterSelected(courses.get(which));
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setTitle("Select filter");
        builder.show();
    }
    private void showInfoDialog(Student student){
        CharSequence[] courseNames=new CharSequence[student.getCourses().size()+1];
        int i=0;
        for(Course c: student.getCourses()){
            courseNames[i]=c.getName()+" "+c.getMark();
            i++;
        }
        courseNames[i]="Avg "+student.getAvgMark();

        new AlertDialog.Builder(this)
            .setItems(courseNames,null)
            .setCancelable(true)
            .setTitle("Student "+student.getLastName())
            .create()
            .show();
    }


    @Override
    public void setPresenter(StudentsContract.Presenter presenter) {
        this.presenter=presenter;
        presenter.subscribe();
    }

    @Override
    public void ListClicked(Student s) {
        showInfoDialog(s);
    }
}
