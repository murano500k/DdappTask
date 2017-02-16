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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;

import com.murano500k.task.ddapp.data.json.Course;
import com.murano500k.task.ddapp.data.json.Student;
import com.murano500k.task.ddapp.data.local.StudentDbHelper;
import com.murano500k.task.ddapp.data.local.StudentsPersistenceContract;
import com.murano500k.task.ddapp.data.remote.RetroHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation to load tasks from the data sources into a cache.
 * <p/>
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 */
public class StudentsRepository implements StudentsDataSource {
    private static final String TAG = "TasksRepository";
    @Nullable
    private static StudentsRepository INSTANCE = null;

    @VisibleForTesting
    @Nullable
    List<Student> mCachedStudents;

    @VisibleForTesting
    boolean mCacheIsDirty = false;

    StudentDbHelper mDbHelper;

    RetroHelper retroHeltper;



    // Prevent direct instantiation.
    public StudentsRepository(Context c) {
        mDbHelper =new StudentDbHelper(c);
        retroHeltper=new RetroHelper();
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
    public io.reactivex.Observable<List<Course>> getAllCourses() {
        return null;
    }

    public List<Student> getStudentsAnySource() throws IOException {
        List<Student> students= getStudentsFromDb();
        if(students==null || students.size()==0){
            Log.d(TAG, "getStudentsAnySource: No items in db");
            students=retroHeltper.getStudents();
            if(students==null) Log.e(TAG, "getStudentsAnySource: NULL STUDENTS from web" );
            else saveStudents(students);
        }
        return students;
    }


    public void saveStudents(List<Student> students){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        if(students==null ) {
            Log.e(TAG, "saveStudents: "+null );
            return;
        }
        List<Course> courses=new ArrayList<>();

        for (Student s:students) {
            ContentValues studentValues = new ContentValues();
            studentValues.put(StudentsPersistenceContract.StudentEntry.COLUMN_NAME_NAME, s.getFirstName());
            studentValues.put(StudentsPersistenceContract.StudentEntry.COLUMN_NAME_LASTNAME, s.getLastName());
            studentValues.put(StudentsPersistenceContract.StudentEntry.COLUMN_NAME_BIRTHDAY, s.getBirthday());
            studentValues.put(StudentsPersistenceContract.StudentEntry._ID, s.getId());
            db.insert(StudentsPersistenceContract.StudentEntry.TABLE_NAME, null, studentValues);
            Log.d(TAG, "new student: "+s);
            for(Course sCourse : s.getCourses()) {
                Log.d(TAG, "new studentCourse: "+sCourse);
                ContentValues studentCourseValues = new ContentValues();
                studentCourseValues.put(StudentsPersistenceContract.StudentCourseEntry.COLUMN_NAME_STUDENT_ID, s.getId());
                studentCourseValues.put(StudentsPersistenceContract.StudentCourseEntry.COLUMN_NAME_COURSE_NAME, sCourse.getName());
                studentCourseValues.put(StudentsPersistenceContract.StudentCourseEntry.COLUMN_NAME_MARK, sCourse.getMark());
                db.insert(StudentsPersistenceContract.StudentCourseEntry.TABLE_NAME, null, studentCourseValues);
                boolean shouldAddCourseToList = true;
                for (Course c: courses) {
                    if (TextUtils.equals(c.getName(), sCourse.getName())) {
                        shouldAddCourseToList = false;
                        break;
                    }
                }
                if(shouldAddCourseToList) {
                    Log.d(TAG, "new Course: "+sCourse);
                    courses.add(sCourse);
                    ContentValues courseValues = new ContentValues();
                    courseValues.put(StudentsPersistenceContract.CourseEntry.COLUMN_NAME_COURSE_NAME, sCourse.getName());
                    db.insert(StudentsPersistenceContract.CourseEntry.TABLE_NAME, null, courseValues);
                }
            }
        }
    }
    public List<Student> getStudentsFromDb(){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] studentProjection = {
                StudentsPersistenceContract.StudentEntry._ID,
                StudentsPersistenceContract.StudentEntry.COLUMN_NAME_NAME,
                StudentsPersistenceContract.StudentEntry.COLUMN_NAME_LASTNAME,
                StudentsPersistenceContract.StudentEntry.COLUMN_NAME_BIRTHDAY
        };
        String[] studentCourseProjection = {
                StudentsPersistenceContract.StudentCourseEntry.COLUMN_NAME_COURSE_NAME,
                StudentsPersistenceContract.StudentCourseEntry.COLUMN_NAME_MARK,
        };
        Cursor studentCursor = db.query(
                StudentsPersistenceContract.StudentEntry.TABLE_NAME,                     // The table to query
                studentProjection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        if(studentCursor!= null){
            List <Student> students = new ArrayList<>();
            while(studentCursor.moveToNext()) {
                String studentId = studentCursor.getString(0);
                if (studentId == null) return null;
                Log.d(TAG, "hasData: " + studentId);
                Student student = new Student();
                student.setId(studentCursor.getString(0));
                student.setFirstName(studentCursor.getString(1));
                student.setLastName(studentCursor.getString(2));
                student.setBirthday(studentCursor.getInt(3));
                List<Course> studentCourses = new ArrayList<>();

                String selection = StudentsPersistenceContract.StudentCourseEntry.COLUMN_NAME_STUDENT_ID + " = ?";
                String[] selectionArgs = {studentId};
                Cursor studentCourseCursor = db.query(
                        StudentsPersistenceContract.StudentCourseEntry.TABLE_NAME,                     // The table to query
                        studentCourseProjection,                               // The columns to return
                        selection,                                // The columns for the WHERE clause
                        selectionArgs,                            // The values for the WHERE clause
                        null,                                     // don't group the rows
                        null,                                     // don't filter by row groups
                        null                                 // The sort order
                );
                if (studentCourseCursor != null) {
                    while (studentCourseCursor.moveToNext()) {
                        Course course = new Course();
                        course.setName(studentCourseCursor.getString(0));
                        course.setMark(studentCourseCursor.getInt(1));
                        studentCourses.add(course);
                    }
                    student.setCourses(studentCourses);
                    studentCourseCursor.close();
                }
            }
            studentCursor.close();
            return students;
        }
        return null;
    }



}
