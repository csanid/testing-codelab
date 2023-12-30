package com.example.android.architecture.blueprints.todoapp.taskdetail

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.R
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class TaskDetailFragmentTest {

    @Test
    fun activeTaskDetails_DisplayedInUi() {
        // Given an active (incomplete) task added to the database
        val task = Task("Clean up living room", "Vacuum")
        // When Details fragment is launched to display task
        Thread.sleep(2000)
        val bundle = TaskDetailFragmentArgs(task.id).toBundle()
        launchFragmentInContainer<TaskDetailFragment>(bundle, R.style.AppTheme)
    }

}