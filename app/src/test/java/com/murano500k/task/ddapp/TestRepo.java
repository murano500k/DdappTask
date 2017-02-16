package com.murano500k.task.ddapp;

import com.murano500k.task.ddapp.data.json.Student;
import com.murano500k.task.ddapp.data.remote.RetroHelper;

import junit.framework.TestCase;

import java.util.List;

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
            System.out.println("student: "+s);
        }

    }

}
