/*
 * Copyright (C) 2019 The Android Open Source Project
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
package com.example.android.architecture.blueprints.todoapp.data.source.local

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Result.Error
import com.example.android.architecture.blueprints.todoapp.data.Result.Success
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Concrete implementation of a data source as a db.
 */
private const val TAG = "InfoLocalDataSource"

class TasksLocalDataSource internal constructor(
    private val tasksDao: TasksDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TasksDataSource {

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        return tasksDao.observeTasks().map {
            Success(it)
        }
    }

    @SuppressLint("LogNotTimber")
    override fun observeTask(taskId: String): LiveData<Result<Task>> {
        return try {
            tasksDao.observeTaskById(taskId).map {
                Log.i(
                    TAG,
                    "Success result about to be returned for tasksDao: $tasksDao, taskId: $taskId, it: $it"
                )
                Success(it)
            }
        } catch (e: Exception) {
            val errorMessage = "Error observing task with ID: $taskId"
            Log.e(TAG, errorMessage, e)
            // Display Snackbar or handle the error in another way
            MutableLiveData<Result<Task>>().apply {
                value = Error(e)
            }
        }
    }

//    @SuppressLint("LogNotTimber")
//    override fun observeTask(taskId: String): LiveData<Result<Task>> {
//        return try {
//            tasksDao.observeTaskById(taskId).map {
//                Log.i(
//                    TAG,
//                    "Success result about to be returned for tasksDao: $tasksDao, taskId: $taskId, it: $it"
//                )
//                Success(it)
//            }
//        } catch (e: Exception) {
//            val errorMessage = "Error observing task with ID: $taskId"
//            Log.e(TAG, errorMessage, e)
//            // Display Snackbar or handle the error in another way
//            MutableLiveData<Result<Task>>().apply {
//                value = Error(e)
//            }
//        }
//    }

//    @SuppressLint("LogNotTimber")
//    override fun observeTask(taskId: String): LiveData<Result<Task>> {
//        return tasksDao.observeTaskById(taskId).map {
//            Log.i(TAG, "Success result about to be returned for tasksDao: $tasksDao, taskId: $taskId, it: $it")
//            Success(it)
//        }
//    }

    override suspend fun refreshTask(taskId: String) {
        //NO-OP
    }

    override suspend fun refreshTasks() {
        //NO-OP
    }

    override suspend fun getTasks(): Result<List<Task>> = withContext(ioDispatcher) {
        return@withContext try {
            Success(tasksDao.getTasks())
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getTask(taskId: String): Result<Task> = withContext(ioDispatcher) {
        try {
            val task = tasksDao.getTaskById(taskId)
            if (task != null) {
                return@withContext Success(task)
            } else {
                return@withContext Error(Exception("Task not found"))
            }
        } catch (e: Exception) {
            return@withContext Error(e)
        }
    }

    @SuppressLint("LogNotTimber")
    override suspend fun saveTask(task: Task) = withContext(ioDispatcher) {
        Log.i(TAG, "About to save task $task to local data source")
        tasksDao.insertTask(task)
    }

    override suspend fun completeTask(task: Task) = withContext(ioDispatcher) {
        tasksDao.updateCompleted(task.id, true)
    }

    override suspend fun completeTask(taskId: String) {
        tasksDao.updateCompleted(taskId, true)
    }

    override suspend fun activateTask(task: Task) = withContext(ioDispatcher) {
        tasksDao.updateCompleted(task.id, false)
    }

    override suspend fun activateTask(taskId: String) {
        tasksDao.updateCompleted(taskId, false)
    }

    override suspend fun clearCompletedTasks() = withContext<Unit>(ioDispatcher) {
        tasksDao.deleteCompletedTasks()
    }

    override suspend fun deleteAllTasks() = withContext(ioDispatcher) {
        tasksDao.deleteTasks()
    }

    @SuppressLint("LogNotTimber")
    override suspend fun deleteTask(taskId: String) = withContext<Unit>(ioDispatcher) {
        Log.i(TAG, "About to delete task $taskId from local data source")
        tasksDao.deleteTaskById(taskId)
        Log.i(TAG, "Task $taskId deleted from local data source")
    }
}



