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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.murano500k.task.ddapp.Injection;
import com.murano500k.task.ddapp.R;
import com.murano500k.task.ddapp.data.json.Course;
import com.murano500k.task.ddapp.data.json.Student;

import java.util.List;
import java.util.Objects;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.view.View.GONE;

public class StudentsActivity extends AppCompatActivity
        implements StudentsContract.View , MyListAdapter.ListClickListener{
    public static final int LIMIT = 20;

    private static final String TAG = "StudentsActivity";
    private StudentsContract.Presenter presenter;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private MyListAdapter adapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    private ImageView button;
    private String COURSE_NAME_ALL= "No filter";
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d(TAG, "onLoadMore: page="+page+" totalItemsCount="+totalItemsCount);
                presenter.loadMore(page);
            }
        };
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(scrollListener);
        adapter = new MyListAdapter(this);
        recyclerView.setAdapter(adapter);

        new StudentsPresenter(Injection.provideTasksRepository(getApplicationContext()),this);
    }


    @Override
    public void addItems(List<Student> students, boolean update) {
        progressBar.setVisibility(GONE);
        if(!update) {
            Log.d(TAG, "new Items: clear list");
            adapter.clearItems();
            adapter.notifyDataSetChanged();
            scrollListener.resetState();
        }
        int curSize = adapter.getItemCount();
        adapter.addItems(students);
        Log.d(TAG, "adapter startIndex="+curSize+" total "+ adapter.getItemCount());

        adapter.notifyItemRangeInserted(curSize, adapter.getItemCount());

        if(students.size()==0 && curSize==0){
            showError("no items matched");
        }
    }


    @Override
    public void showError(String msg) {
        Log.e(TAG, "showError: "+msg );
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(StudentsActivity.this)
                        .setMessage(msg)
                        .setTitle("Error")
                        .setPositiveButton("reset", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                presenter.subscribe();
                            }
                        })
                        .create().show();
                progressBar.setVisibility(GONE);
            }
        });
    }
    @Override
    public void showFilterButton(List<Course> courses, Course selected) {
        progressBar.setVisibility(GONE);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (button!=null) {
                    Log.d(TAG, "button already added ");
                    toolbar.removeView(button);
                }
            }
        });
        button = new ImageView(this);
        button.setImageResource(android.R.drawable.ic_menu_more);
        button.setScaleType(ImageView.ScaleType.FIT_XY);
        button.setPadding(10,10,10,10);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterCourseDialog(courses, selected);
            }
        });

        ActionBar.LayoutParams layoutParams=new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT,
                Gravity.END);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toolbar.addView(button, layoutParams);
            }
        });
    }

    private void showFilterCourseDialog(List<Course> courses, Course selected){
        String[] courseNames=new String [courses.size()+1];
        int i=1;
        int checked=-1;
        courseNames[0] = COURSE_NAME_ALL;
        for(Course c: courses){
            courseNames[i]=c.getName();
            if(Objects.equals(c,selected))checked=i;
            i++;
        }
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(courseNames,checked,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0) presenter.filterSelected(null);
                else showFilterMarkDialog(courseNames[which]);
                dialog.dismiss();
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

    private void showFilterMarkDialog(String courseName) {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        final EditText etMark = new EditText(this);
        etMark.setInputType(TYPE_CLASS_NUMBER);
        button.setPadding(10,10,10,10);
        builder.setPositiveButton("FILTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int mark = -1;
                if(etMark.getText()!=null && etMark.getText().toString().length()>0){
                    String editTextValue = etMark.getText().toString();
                    Log.d(TAG, "YouEditTextValue: "+editTextValue);
                    mark = Integer.valueOf(editTextValue);
                }
                if(mark<=0) {
                    Toast.makeText(StudentsActivity.this, "mark not set", Toast.LENGTH_SHORT).show();
                }else {
                    Course course = new Course(courseName, mark);
                    Log.d(TAG, "filter selected "+ course);
                    presenter.filterSelected(course);
                    progressBar.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                }
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setView(etMark);
        builder.setTitle("Select mark");
        builder.show();
    }

    private void showInfoDialog(Student student){
        CharSequence[] courseNames=new CharSequence[student.getCourses().size()+1];
        int i=0;
        for(Course c: student.getCourses()){
            courseNames[i]=c.getName()+"  -  "+c.getMark();
            i++;
        }
        courseNames[i]="Average  -  "+student.getAvgMark();
        new AlertDialog.Builder(this)
            .setItems(courseNames,null)
            .setCancelable(true)
            .setTitle(student.getFirstName()+" "+student.getLastName())
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
