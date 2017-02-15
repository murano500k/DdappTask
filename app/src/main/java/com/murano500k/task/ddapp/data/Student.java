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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "TestController.Student")
public final class Student {

    @Root(name = "TestController.Course")
    public final class Course {

        @NonNull
        @Element(name = "Name")
        private final String mName;

        @Element(name = "Mark")
        private final int mMark;


        public Course(@NonNull String mName, int mark) {
            this.mName = mName;
            this.mMark = mark;
        }

        @NonNull
        public String getCourseName() {
            return mName;
        }

        public int getMark() {
            return mMark;
        }

        @Override
        public String toString() {
            return "Course with title " + mName;
        }
    }

    @NonNull
    @Attribute(name = "Id")
    private final String mId;

    @Nullable
    @Element(name = "FirstName")
    private final String mName;

    @Nullable
    @Element(name = "LastName")
    private final String mLastname;

    @Nullable
    @ElementList(name = "Courses")
    private List<Course> mCourses;


    public Student(@NonNull String mId, @Nullable String mName, @Nullable String mLastname) {
        this.mId = mId;
        this.mName = mName;
        this.mLastname = mLastname;
    }

    @Nullable
    public List<Course> getCourses() {
        return mCourses;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @Nullable
    public String getLastname() {
        return mLastname;
    }

    @Nullable
    public String getName() {
        return mName;
    }

    public void setCourses(@Nullable List<Course> mCourses) {
        this.mCourses = mCourses;
    }



}
