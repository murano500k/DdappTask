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
import android.util.Log;

import com.murano500k.task.ddapp.data.json.Course;
import com.murano500k.task.ddapp.data.json.Student;
import com.murano500k.task.ddapp.data.local.StudentDbHelper;
import com.murano500k.task.ddapp.data.local.StudentsPersistenceContract;
import com.murano500k.task.ddapp.data.remote.RetroHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Function;

/**
 * Concrete implementation to load tasks from the data sources into a cache.
 * <p/>
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 */
public class StudentsRepository implements StudentsDataSource {
    private static final String TAG = "StudentsRepository";
    private static final int LIMIT = 20;
    @Nullable
    private static StudentsRepository INSTANCE = null;



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


    private Cursor getCoursesCoursor(String studentId){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String selection = StudentsPersistenceContract.CourseEntry.COLUMN_NAME_STUDENT_ID + " = ?";
        String[] selectionArgs = {studentId};
        return  db.query(
                StudentsPersistenceContract.CourseEntry.TABLE_NAME,                     // The table to query
                getCourseProjection(),                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
    }

    private Cursor getCoursesCoursor(String courseName, int mark){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String selection = StudentsPersistenceContract.CourseEntry.COLUMN_NAME_COURSE_NAME + " = ?"+
                " AND "+StudentsPersistenceContract.CourseEntry.COLUMN_NAME_MARK + " = ?";
        String[] selectionArgs = {courseName,String.valueOf(mark)};
        return  db.query(
                StudentsPersistenceContract.CourseEntry.TABLE_NAME,                     // The table to query
                getCourseProjection(),                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
    }

    private String[] getCourseProjection(){
        return new String[]{
                StudentsPersistenceContract.CourseEntry.COLUMN_NAME_COURSE_NAME,
                StudentsPersistenceContract.CourseEntry.COLUMN_NAME_STUDENT_ID,
                StudentsPersistenceContract.CourseEntry.COLUMN_NAME_MARK
        };
    }

    @Override
    public Single<List<Course>> getAllCourses() {
        return Observable.fromCallable(new Callable<List<Course>>() {
            @Override
            public List<Course> call() throws Exception {
                List<Course> courses =new ArrayList<>();
                Cursor cursor = getCoursesCoursor();
                while (cursor.moveToNext()) courses.add(new Course(cursor.getString(0),cursor.getInt(1)));
                return courses;
            }
        }).flatMapIterable(new Function<List<Course>, Iterable<Course>>() {
            @Override
            public Iterable<Course> apply(List<Course> courses) throws Exception {
                return courses;
            }
        }).distinct(Course::getName).toList();
    }


    private Cursor getCoursesCoursor(){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String [] proj =new String[]{
                StudentsPersistenceContract.CourseEntry.COLUMN_NAME_COURSE_NAME,
                StudentsPersistenceContract.CourseEntry.COLUMN_NAME_MARK
        };
        return  db.query(
                true,
                StudentsPersistenceContract.CourseEntry.TABLE_NAME,                     // The table to query
                proj,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null,
                null
        );
    }

    private Student getStudent(Cursor studentCursor){
        if(!studentCursor.moveToNext()) return null;
        Student student =new Student(studentCursor.getString(0),
                studentCursor.getString(1),
                studentCursor.getString(2),
                studentCursor.getInt(3));
        List<Course> courses=getStudentCourses(studentCursor);
        student.setCourses(courses);
        student.setAvgMark(getAverageMark(courses));
        return student;
    }


    private String[] getStudentProjection(){
        return new String[]{
                StudentsPersistenceContract.StudentEntry._ID,
                StudentsPersistenceContract.StudentEntry.COLUMN_NAME_NAME,
                StudentsPersistenceContract.StudentEntry.COLUMN_NAME_LASTNAME,
                StudentsPersistenceContract.StudentEntry.COLUMN_NAME_BIRTHDAY,
        };
    }
    private float getAverageMark(List<Course> courses){
        int count = courses.size();
        if(count==0)return 0;
        int sum=0;
        for(Course course: courses){
            sum+=course.getMark();
        }
            return sum/count;
    }
    private List<Course> getStudentCourses(Cursor studentCursor){
            List<Course> courses =new ArrayList<>();
        //if(studentCursor==null || !studentCursor.moveToNext()) return null;
        String studentId = studentCursor.getString(0);
        Cursor courseCursor=getCoursesCoursor(studentId);
        while (courseCursor.moveToNext()){
            courses.add(new Course(courseCursor.getString(0), courseCursor.getInt(2)));
        }
        return courses;
    }
    private List<Student> getStudentsFromDb(Course filter, int offset) {
        SQLiteDatabase db=mDbHelper.getReadableDatabase();
        List<Student> students = new ArrayList<>();
        Cursor coursesCursor =getCoursesCoursor(filter.getName(), filter.getMark());
        coursesCursor.move(offset);
        int i=0;
        while (coursesCursor.moveToNext() && ++i<LIMIT){
            String selection = StudentsPersistenceContract.StudentEntry._ID + " = ?";
            String[] selectionArgs = {coursesCursor.getString(1)};
            Cursor studentCoursor =db.query(
                    StudentsPersistenceContract.StudentEntry.TABLE_NAME,
                    getStudentProjection(),
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null);

            students.add(getStudent(studentCoursor));
        }
        Log.d(TAG, "getStudentsFromDb: size="+students.size());
        return students;
    }
    private List<Student> getStudentsFromDb(int offset){
        SQLiteDatabase db=mDbHelper.getReadableDatabase();
        List<Student> students = new ArrayList<>();
        int i=0;
        Cursor studentCoursor =db.query(
                StudentsPersistenceContract.StudentEntry.TABLE_NAME,
                getStudentProjection(),
                null,
                null,
                null,
                null,
                null);
        studentCoursor.move(offset);
        while (++i<LIMIT && studentCoursor.moveToNext()){
            students.add(getStudent(studentCoursor));
        }
        Log.d(TAG, "getStudentsFromDb: size="+students.size());
        return students;
    }

    @Override
    public Observable<List<Student>> getStudents(Course filter, int offset) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        if(filter !=null)
            return Observable.fromCallable(new Callable<List<Student>>() {
            @Override
            public List<Student> call() throws Exception {
                return getStudentsFromDb(filter,offset);
            }
        });

        else
            return Observable.fromCallable(new Callable<List<Student>>() {
            @Override
            public List<Student> call() throws Exception {
                if(mDbHelper.shouldLoadFromRemote()){
                    List<Student> students=retroHeltper.getStudents();
                    if(students==null || students.size()==0) {
                        Log.e(TAG, "getStudentsAnySource: NULL STUDENTS from web");
                        return null;
                    } else saveStudents(students);
                }
                return getStudentsFromDb(offset);
            }
        });
    }

    private void saveStudents(List<Student> students){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        if(students==null ) {
            Log.e(TAG, "saveStudents: "+null );
            return;
        }
        Log.d(TAG, "saveStudents: "+students.size());
        int i=0;
        db.beginTransaction();
        try {


            for (Student s : students) {
                Log.d(TAG, "saveStudent" + i++);
                ContentValues studentValues = new ContentValues();
                studentValues.put(StudentsPersistenceContract.StudentEntry._ID, s.getId());
                studentValues.put(StudentsPersistenceContract.StudentEntry.COLUMN_NAME_NAME, s.getFirstName());
                studentValues.put(StudentsPersistenceContract.StudentEntry.COLUMN_NAME_LASTNAME, s.getLastName());
                studentValues.put(StudentsPersistenceContract.StudentEntry.COLUMN_NAME_BIRTHDAY, s.getBirthday());
                db.insert(StudentsPersistenceContract.StudentEntry.TABLE_NAME, null, studentValues);
                //Log.d(TAG, "new student: "+s);
                for (Course sCourse : s.getCourses()) {
                    //  Log.d(TAG, "new studentCourse: "+sCourse);
                    ContentValues studentCourseValues = new ContentValues();
                    studentCourseValues.put(StudentsPersistenceContract.CourseEntry.COLUMN_NAME_COURSE_NAME, sCourse.getName());
                    studentCourseValues.put(StudentsPersistenceContract.CourseEntry.COLUMN_NAME_STUDENT_ID, s.getId());
                    studentCourseValues.put(StudentsPersistenceContract.CourseEntry.COLUMN_NAME_MARK, sCourse.getMark());
                    db.insert(StudentsPersistenceContract.CourseEntry.TABLE_NAME, null, studentCourseValues);
                }

            }
            db.setTransactionSuccessful();
        }catch (Exception e){
            Log.e(TAG, "saveStudents: ",e );

        }finally {
            db.endTransaction();
        }
    }

}
