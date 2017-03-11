package com.murano500k.task.ddapp.cp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.murano500k.task.ddapp.data.local.StudentDbHelper;
import com.murano500k.task.ddapp.data.local.StudentsPersistenceContract;

/**
 * Created by artem on 3/1/17.
 */

public class StudentContentProvider extends ContentProvider {
	private static final int STUDENT = 100;
	private static final int STUDENT_ID = 101;
	private static final int COURSE = 200;
	private static final int COURSE_ID = 201;
	private StudentDbHelper mOpenHelper;
	private UriMatcher sUriMatcher;

	@Override
	public boolean onCreate() {
		mOpenHelper = new StudentDbHelper(getContext());
		sUriMatcher=buildUriMatcher();
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		Cursor retCursor;
		switch(sUriMatcher.match(uri)){
			case STUDENT:
				retCursor = db.query(
						StudentsPersistenceContract.StudentEntry.TABLE_NAME,
						projection,
						selection,
						selectionArgs,
						null,
						null,
						sortOrder
				);
				break;
			case STUDENT_ID:
				long _id = ContentUris.parseId(uri);
				retCursor = db.query(
						StudentsPersistenceContract.StudentEntry.TABLE_NAME,
						projection,
						StudentsPersistenceContract.StudentEntry._ID + " = ?",
						new String[]{String.valueOf(_id)},
						null,
						null,
						sortOrder
				);
				break;
			case COURSE:
				retCursor = db.query(
						StudentsPersistenceContract.CourseEntry.TABLE_NAME,
						projection,
						selection,
						selectionArgs,
						null,
						null,
						sortOrder
				);
				break;
			case COURSE_ID:
				_id = ContentUris.parseId(uri);
				retCursor = db.query(
						StudentsPersistenceContract.CourseEntry.TABLE_NAME,
						projection,
						StudentsPersistenceContract.CourseEntry._ID + " = ?",
						new String[]{String.valueOf(_id)},
						null,
						null,
						sortOrder
				);
				break;
			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}

		// Set the notification URI for the cursor to the one passed into the function. This
		// causes the cursor to register a content observer to watch for changes that happen to
		// this URI and any of it's descendants. By descendants, we mean any URI that begins
		// with this path.
		retCursor.setNotificationUri(getContext().getContentResolver(), uri);
		return retCursor;

	}

	@Override
	public String getType(Uri uri) {
		switch(sUriMatcher.match(uri)){
			case STUDENT:
				return StudentsPersistenceContract.StudentEntry.CONTENT_TYPE;
			case STUDENT_ID:
				return StudentsPersistenceContract.StudentEntry.CONTENT_ITEM_TYPE;
			case COURSE:
				return StudentsPersistenceContract.CourseEntry.CONTENT_TYPE;
			case COURSE_ID:
				return StudentsPersistenceContract.CourseEntry.CONTENT_ITEM_TYPE;
			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long _id;
		Uri returnUri;

		switch(sUriMatcher.match(uri)){
			case STUDENT:
				_id = db.insert(StudentsPersistenceContract.StudentEntry.TABLE_NAME, null, values);
				if(_id > 0){
					returnUri =  StudentsPersistenceContract.StudentEntry.buildStudentUri(_id);
				} else{
					throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
				}
				break;
			case COURSE:
				_id = db.insert(StudentsPersistenceContract.CourseEntry.TABLE_NAME, null, values);
				if(_id > 0){
					returnUri = StudentsPersistenceContract.CourseEntry.buildStudentUri(_id);
				} else{
					throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
				}
				break;
			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}

		// Use this on the URI passed into the function to notify any observers that the uri has
		// changed.
		getContext().getContentResolver().notifyChange(uri, null);
		return returnUri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int rows; // Number of rows effected

		switch(sUriMatcher.match(uri)){
			case STUDENT:
				rows = db.delete(StudentsPersistenceContract.StudentEntry.TABLE_NAME, selection, selectionArgs);
				break;
			case COURSE:
				rows = db.delete(StudentsPersistenceContract.CourseEntry.TABLE_NAME, selection, selectionArgs);
				break;
			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}

		// Because null could delete all rows:
		if(selection == null || rows != 0){
			getContext().getContentResolver().notifyChange(uri, null);
		}

		return rows;

	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int rows;

		switch(sUriMatcher.match(uri)){
			case STUDENT:
				rows = db.update(StudentsPersistenceContract.StudentEntry.TABLE_NAME, values, selection, selectionArgs);
				break;
			case COURSE:
				rows = db.update(StudentsPersistenceContract.CourseEntry.TABLE_NAME, values, selection, selectionArgs);
				break;
			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}

		if(rows != 0){
			getContext().getContentResolver().notifyChange(uri, null);
		}

		return rows;
	}

	public static UriMatcher buildUriMatcher(){
		String content = StudentsPersistenceContract.CONTENT_AUTHORITY;

		// All paths to the UriMatcher have a corresponding code to return
		// when a match is found (the ints above).
		UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		matcher.addURI(content, StudentsPersistenceContract.PATH_STUDENT, STUDENT);
		matcher.addURI(content, StudentsPersistenceContract.PATH_STUDENT + "/#", STUDENT_ID);
		matcher.addURI(content, StudentsPersistenceContract.PATH_COURSE, COURSE);
		matcher.addURI(content, StudentsPersistenceContract.PATH_COURSE + "/#", COURSE_ID);

		return matcher;
	}
}
