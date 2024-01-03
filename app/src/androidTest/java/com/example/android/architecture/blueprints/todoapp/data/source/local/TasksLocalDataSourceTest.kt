package com.example.android.architecture.blueprints.todoapp.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.android.architecture.blueprints.todoapp.data.Result.Success
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.succeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class TasksLocalDataSourceTest {
    private lateinit var localDataSource: TasksLocalDataSource
    private lateinit var database: ToDoDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ToDoDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        localDataSource = TasksLocalDataSource(database.taskDao(), Dispatchers.Main)
    }

    @After
    fun cleanup() {
        database.close()
    }

    @Test
    fun saveTask_retrievesTask() = runTest {
        // Given a new task saved to the database
        val task = Task("title", "description", false)
        localDataSource.saveTask(task)
        // When the task is retrieved by id
        val result = localDataSource.getTask(task.id)
        // Then the same task is returned
        assertEquals(result.succeeded, true)
        result as Success
        assertEquals(result.data.title, "title")
        assertEquals(result.data.description, "description")
        assertEquals(result.data.isCompleted, false)
    }

    @Test
    fun completeTask_retrievedTaskIsComplete() = runTest {
        // Given a new active task saved to the local data source
        val task = Task("title", "description", false)
        localDataSource.saveTask(task)
        // When it is marked as complete
        localDataSource.completeTask(task)
        // Then the task retrieved from the database is complete
        val loadedTask = localDataSource.getTask(task.id)
        assertEquals(loadedTask.succeeded, true)
        loadedTask as Success
        assertEquals(loadedTask.data.id, task.id)
        assertEquals(loadedTask.data.title, task.title)
        assertEquals(loadedTask.data.description, task.description)
        assertEquals(loadedTask.data.isCompleted, true)
    }
}