package com.test_1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class UserViewModel(private val userDao: UserDao,  private val taskDao: TaskDao) : ViewModel() {

    fun registerUser(user: User, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                userDao.insertUser(user)
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun loginUser(email: String, password: String, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            val user = userDao.loginUser(email, password)
            onResult(user)
        }
    }

    fun addTask(task: Task, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                taskDao.insertTask(task)
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun getTasksForUser(userId: Int, onResult: (List<Task>) -> Unit) {
        viewModelScope.launch {
            val tasks = taskDao.getTasksForUser(userId)
            onResult(tasks)
        }
    }

    fun updateTask(task: Task, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                taskDao.updateTask(task)
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun deleteTask(task: Task, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                taskDao.deleteTask(task)
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }
}