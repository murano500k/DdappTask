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

package com.murano500k.task.ddapp.data;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.murano500k.task.ddapp.data.local.StudentDbHelper;

import java.util.List;

/**
 * Concrete implementation to load tasks from the data sources into a cache.
 * <p/>
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 */
public class StudentsRepository implements StudentsDataSource {

    @Nullable
    private static StudentsRepository INSTANCE = null;

    @VisibleForTesting
    @Nullable
    List<Student> mCachedStudents;

    @VisibleForTesting
    boolean mCacheIsDirty = false;

    StudentDbHelper dbHelper;



    // Prevent direct instantiation.
    private StudentsRepository(Context c) {
        dbHelper=new StudentDbHelper(c);
    }

    public static StudentsRepository getInstance(Context c) {
        if (INSTANCE == null) {
            INSTANCE = new StudentsRepository(c);
        }
        return INSTANCE;
    }


    public static void destroyInstance() {
        INSTANCE = null;
    }


    @Override
    public io.reactivex.Observable<List<Student>> getStudents(int offset) {
        return null;
    }

    @Override
    public io.reactivex.Observable<List<Student.Course>> getAllCourses() {

        return null;
    }

    @Override
    public String getString() {
        return null;
    }


}
