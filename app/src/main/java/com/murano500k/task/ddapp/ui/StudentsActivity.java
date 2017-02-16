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

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.NavigationView;
import android.support.test.espresso.IdlingResource;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.murano500k.task.ddapp.R;
import com.murano500k.task.ddapp.data.Student;
import com.murano500k.task.ddapp.util.EspressoIdlingResource;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.util.List;

public class StudentsActivity extends AppCompatActivity implements StudentsContract.View {

    private static final String CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY";
    private static final String TAG = "ActivityTEST";

    private DrawerLayout mDrawerLayout;

    private StudentsPresenter mStudentsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        try {
            Log.d(TAG, "onCreate: "+ getStudents(this));
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }




        /*StudentsFragment tasksFragment =
                (StudentsFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (tasksFragment == null) {
            // Create the fragment
            tasksFragment = StudentsFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), tasksFragment, R.id.contentFrame);
        }

        // Create the presenter
        mStudentsPresenter = new StudentsPresenter(
                Injection.provideTasksRepository(getApplicationContext()),
                tasksFragment,
                Injection.provideSchedulerProvider());

        // Load previously saved state, if available.
        if (savedInstanceState != null) {
            FilterType currentFiltering =
                    (FilterType) savedInstanceState.getSerializable(CURRENT_FILTERING_KEY);
            mStudentsPresenter.setFiltering(currentFiltering);
        }*/
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //outState.putSerializable(CURRENT_FILTERING_KEY, mStudentsPresenter.getFiltering());

        super.onSaveInstanceState(outState);
    }


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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Open the navigation drawer when the home icon is selected from the toolbar.
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.list_navigation_menu_item:
                            // Do nothing, we're already on that screen
                            break;
                        case R.id.statistics_navigation_menu_item:
                          /*  Intent intent =
                                    new Intent(StudentsActivity.this, StatisticsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);*/
                            break;
                        default:
                            break;
                    }
                    // Close the navigation drawer when an item is selected.
                    menuItem.setChecked(true);
                    mDrawerLayout.closeDrawers();
                    return true;
                });
    }

    @VisibleForTesting
    public IdlingResource getCountingIdlingResource() {
        return EspressoIdlingResource.getIdlingResource();
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
    public void showStudents(List<Student> students) {

    }

    @Override
    public void showFilterButton(List<Student.Course> courses) {

    }

    @Override
    public void setPresenter(StudentsContract.Presenter presenter) {

    }

    // TODO: 2/15/17 showInfoDialog
    // TODO: 2/15/17 showFilterDialog
}
