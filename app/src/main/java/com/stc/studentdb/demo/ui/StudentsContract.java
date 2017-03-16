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

package com.stc.studentdb.demo.ui;

import com.stc.studentdb.demo.BasePresenter;
import com.stc.studentdb.demo.BaseView;
import com.stc.studentdb.demo.data.json.Course;
import com.stc.studentdb.demo.data.json.Student;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface StudentsContract {

    interface View extends BaseView<Presenter> {
        void showError(String msg);
        void addItems(List<Student> students, boolean update);
        void showFilterButton(List<Course> courses, Course selected);
    }

    interface Presenter extends BasePresenter {
        void filterSelected(Course course);
        void loadMore(int offset);
    }
}
