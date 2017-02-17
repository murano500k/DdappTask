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

package com.murano500k.task.ddapp.ui;

import android.util.Log;

import com.murano500k.task.ddapp.data.StudentsDataSource;
import com.murano500k.task.ddapp.data.json.Course;
import com.murano500k.task.ddapp.data.json.Student;

import java.util.List;
import java.util.Objects;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class StudentsPresenter implements StudentsContract.Presenter{
    private static final String TAG = "StudentsPresenter";
    private StudentsDataSource dataSource;
    private StudentsContract.View mView;
    private Course mFilter = null;

    public StudentsPresenter(StudentsDataSource dataSource, StudentsContract.View view) {
        this.dataSource = dataSource;
        this.mView = view;
        mView.setPresenter(this);
    }




    private void initFilterButton(Course selectedFilter) {
        dataSource.getAllCourses()
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<List<Course>>() {
                    @Override
                    public void accept(List<Course> courses) throws Exception {
                        mView.showFilterButton(courses, selectedFilter);
                    }
                });
    }

    @Override
    public void subscribe() {
        Log.d(TAG, "subscribe:");
        processAction(mFilter, 0, false);
    }

    @Override
    public void loadMore(int offset) {
        Log.d(TAG, "loadMore: "+offset);
        processAction(mFilter, offset, true);
    }


    @Override
    public void filterSelected(Course course) {
        Log.d(TAG, "filterSelected:");
        if(!Objects.equals(course,mFilter)){
            Log.d(TAG, "new filter");
            mFilter=course;
            subscribe();
        }

    }

    // TODO: 2/17/17 fix endlessScroll
    // TODO: 2/17/17 add edittext to filterdialog
    private void processAction(Course filter, int offset, boolean update){
        dataSource.getStudents(filter, offset)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<List<Student>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe load more");
                    }
                    @Override
                    public void onNext(List<Student> value) {
                        Log.d(TAG, "onNext: "+value);
                        mView.addItems(value, update);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: finished with Students");
                        if(!update) initFilterButton(filter);
                    }
                });
    }

    @Override
    public void unsubscribe() {


    }
}
    /*implements StudentsContract.Presenter {

    @NonNull
    private final StudentsRepository mTasksRepository;

    @NonNull
    private final StudentsContract.View mTasksView;

    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;

    @NonNull
    private FilterType mFilter = FilterType.ALL_TASKS;

    private boolean mFirstLoad = true;

    @NonNull
    private CompositeSubscription mSubscriptions;

    public StudentsPresenter(@NonNull StudentsRepository tasksRepository,
                          @NonNull StudentsContract.View tasksView,
                          @NonNull BaseSchedulerProvider schedulerProvider) {
        mTasksRepository = checkNotNull(tasksRepository, "tasksRepository cannot be null");
        mTasksView = checkNotNull(tasksView, "tasksView cannot be null!");
        mSchedulerProvider = checkNotNull(schedulerProvider, "schedulerProvider cannot be null");

        mSubscriptions = new CompositeSubscription();
        mTasksView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        loadStudents(false);
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    @Override
    public void result(int requestCode, int resultCode) {
        // If a task was successfully added, show snackbar

    }

    @Override
    public void loadStudents(boolean forceUpdate) {
        // Simplification for sample: a network reload will be forced on first load.
        loadTasks(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    *//**
     * @param forceUpdate   Pass in true to refresh the data in the {@link TasksDataSource}
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     *//*
    private void loadTasks(final boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            mTasksView.setLoadingIndicator(true);
        }
        if (forceUpdate) {
            mTasksRepository.refreshTasks();
        }

        // The network request might be handled in a different thread so make sure Espresso knows
        // that the app is busy until the response is handled.
        EspressoIdlingResource.increment(); // App is busy until further notice

        mSubscriptions.clear();
        Subscription subscription = mTasksRepository
                .getTasks()
                .flatMap(new Func1<List<Task>, Observable<Task>>() {
                    @Override
                    public Observable<Task> call(List<Task> tasks) {
                        return Observable.from(tasks);
                    }
                })
                .filter(task -> {
                    switch (mFilter) {
                        case ACTIVE_TASKS:
                            return task.isActive();
                        case COMPLETED_TASKS:
                            return task.isCompleted();
                        case ALL_TASKS:
                        default:
                            return true;
                    }
                })
                .toList()
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .doOnTerminate(() -> {
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                        EspressoIdlingResource.decrement(); // Set app as idle.
                    }
                })
                .subscribe(
                        // onNext
                        this::processTasks,
                        // onError
                        throwable -> mTasksView.showLoadingTasksError(),
                        // onCompleted
                        () -> mTasksView.setLoadingIndicator(false));
        mSubscriptions.add(subscription);
    }

    private void processTasks(@NonNull List<Task> tasks) {
        if (tasks.isEmpty()) {
            // Show a message indicating there are no tasks for that filter type.
            processEmptyTasks();
        } else {
            // Show the list of tasks
            mTasksView.showStudents(tasks);
            // Set the filter label's text.
            showFilterLabel();
        }
    }

    private void showFilterLabel() {
        switch (mFilter) {
            case ACTIVE_TASKS:
               // mTasksView.showActiveFilterLabel();
                break;
            case COMPLETED_TASKS:
                //mTasksView.showCompletedFilterLabel();
                break;
            default:
               // mTasksView.showAllFilterLabel();
                break;
        }
    }

    private void processEmptyTasks() {
        switch (mFilter) {
            case ACTIVE_TASKS:
                //mTasksView.showNoActiveTasks();
                break;
            case COMPLETED_TASKS:
                //mTasksView.showNoCompletedTasks();
                break;
            default:
                //mTasksView.showNoTasks();
                break;
        }
    }

    @Override
    public void openStudentDetails(@NonNull Task requestedTask) {
        checkNotNull(requestedTask, "requestedTask cannot be null!");
        mTasksView.showStudentDetailsUi(requestedTask.getId());
    }

    @Override
    public void setFiltering(@NonNull FilterType requestType) {
        mFilter = requestType;
    }

    @Override
    public FilterType getFiltering() {
        return mFilter;
    }

}
*/
