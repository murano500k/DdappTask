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

package com.murano500k.task.ddapp.data.local;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * The contract used for the db to save the tasks locally.
 */
public final class StudentsPersistenceContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private StudentsPersistenceContract() {}
	public static final String CONTENT_AUTHORITY = "com.murano500k.task.ddapp";

	/**
	 * The content authority is used to create the base of all URIs which apps will use to
	 * contact this content provider.
	 */
	public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

	/**
	 * A list of possible paths that will be appended to the base URI for each of the different
	 * tables.
	 */
	public static final String PATH_STUDENT = "student";
	public static final String PATH_COURSE = "course";

    /* Inner class that defines the table contents */
    public static abstract class StudentEntry implements BaseColumns {
        public static final String TABLE_NAME = "student";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_LASTNAME = "lastname";
        public static final String COLUMN_NAME_BIRTHDAY = "birthday";
        public static final String COLUMN_NAME_AVG_MARK = "average-mark";

	    public static final Uri CONTENT_URI =
			    BASE_CONTENT_URI.buildUpon().appendPath(PATH_STUDENT).build();

	    public static final String CONTENT_TYPE =
			    "vnd.android.cursor.dir/" + CONTENT_URI  + "/" + PATH_STUDENT;
	    public static final String CONTENT_ITEM_TYPE =
			    "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_STUDENT;

	    public static Uri buildStudentUri(long id){
		    return ContentUris.withAppendedId(CONTENT_URI, id);
	    }



    }
    public static abstract class CourseEntry implements BaseColumns {
        public static final String TABLE_NAME = "studentcourse";
        public static final String COLUMN_NAME_STUDENT_ID = "studentId";
        public static final String COLUMN_NAME_COURSE_NAME = "courseId";
        public static final String COLUMN_NAME_MARK = "mark";

	    public static final Uri CONTENT_URI =
			    BASE_CONTENT_URI.buildUpon().appendPath(PATH_COURSE).build();

	    public static final String CONTENT_TYPE =
			    "vnd.android.cursor.dir/" + CONTENT_URI  + "/" + PATH_COURSE;
	    public static final String CONTENT_ITEM_TYPE =
			    "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_COURSE;

	    public static Uri buildStudentUri(long id){
		    return ContentUris.withAppendedId(CONTENT_URI, id);
	    }
    }

}
