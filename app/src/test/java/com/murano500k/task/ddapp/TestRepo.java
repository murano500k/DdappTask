package com.murano500k.task.ddapp;

import android.util.Log;

import com.murano500k.task.ddapp.data.Student;
import com.murano500k.task.ddapp.data.remote.RetroHelper;

import junit.framework.TestCase;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by artem on 2/15/17.
 */

public class TestRepo extends TestCase{



    public void testRetro() throws Exception{
        RetroHelper retroHelper = new RetroHelper();
        List <Student> list = retroHelper.getStudents();
        assertNotNull(list);
        for (Student s :
                list) {
            Log.d(TAG, "student: "+s);
        }

    }

}
