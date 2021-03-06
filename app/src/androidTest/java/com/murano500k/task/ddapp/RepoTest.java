package com.murano500k.task.ddapp;

import android.app.Instrumentation;
import android.content.Context;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.stc.studentdb.demo.data.StudentsRepository;
import com.stc.studentdb.demo.data.json.Course;
import com.stc.studentdb.demo.data.json.Student;
import com.stc.studentdb.demo.util.EspressoIdlingResource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by artem on 2/16/17.
 */
@RunWith(AndroidJUnit4.class)
public class RepoTest {
    Instrumentation instrumentation;
    Context context;
    IdlingResource idlingResource;
    private static final String TAG = "RepoTest";

    @Before
    public void perpare(){
        instrumentation= InstrumentationRegistry.getInstrumentation();
        context=instrumentation.getTargetContext();
    }


    @Test
    public void testRepo(){
        List<Student> students=null;
        StudentsRepository repository = new StudentsRepository(context);
        EspressoIdlingResource.increment();
        repository.getStudents(null,0)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Student>>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe1: "+System.currentTimeMillis());

            }

            @Override
            public void onNext(List<Student> value) {
                    List<Student> students=value;

                    assertNotNull(students);
                    assertTrue(students.size()>0);
                    for (Student s :
                            students) {
                       // Log.d(TAG, "student: "+s);
                    }

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ", e);
                EspressoIdlingResource.decrement();

            }

            @Override
            public void onComplete() {
                EspressoIdlingResource.decrement();
                Log.d(TAG, "onComplete1: "+System.currentTimeMillis());


            }
        });

        while(!EspressoIdlingResource.getIdlingResource().isIdleNow()){
            SystemClock.sleep(1000);
            Log.d(TAG, "sleep");
        }
        assertNotNull(repository);

        EspressoIdlingResource.increment();
repository.getAllCourses()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Course>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe2: "+System.currentTimeMillis());

                    }

                    @Override
                    public void onNext(List<Course> value) {
                        for (Course c:value
                             ) {
                            Log.d(TAG, "onNext: "+c);
                            
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                        EspressoIdlingResource.decrement();

                    }

                    @Override
                    public void onComplete() {
                        EspressoIdlingResource.decrement();
                        Log.d(TAG, "onComplete2: "+System.currentTimeMillis());


                    }
                });

        while(!EspressoIdlingResource.getIdlingResource().isIdleNow()){
            SystemClock.sleep(1000);
            Log.d(TAG, "sleep");
        }




    }
}
