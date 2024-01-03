package com.example.android.architecture.blueprints.todoapp.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.android.architecture.blueprints.todoapp.data.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class TasksDaoTest {
    // Executes each task synchronously
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: ToDoDatabase

    @Before
    fun initDatabase() {
        database = Room.inMemoryDatabaseBuilder(getApplicationContext(), ToDoDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDatabase() = database.close()

    @Test
    fun insertTaskAndGetById() = runTest {
        // Given a new task inserted into the database
        val task = Task("title", "description")
        database.taskDao().insertTask(task)
        // When the task is requested by id from the database
        val loadedTask = database.taskDao().getTaskById(task.id)
        // Then the loaded data contains the expected values
        assertThat<Task>(loadedTask as Task, notNullValue())
        assertThat(loadedTask.id, `is`(task.id))
        assertThat(loadedTask.title, `is`(task.title))
        assertThat(loadedTask.description, `is`(task.description))
        assertThat(loadedTask.isCompleted, `is`(task.isCompleted))
    }
}