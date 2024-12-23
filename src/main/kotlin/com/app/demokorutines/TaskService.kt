package com.app.demokorutines

import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class TaskService(private val repository: TaskRepository) {

    suspend fun createTask(task: Task): Task = repository.save(task)

    suspend fun updateTask(id: Long, task: Task): Task {
        val existingTask = repository.findById(id) ?: throw IllegalArgumentException("Task not found")
        return repository.save(
            existingTask.copy(
                title = task.title,
                description = task.description,
                completed = task.completed
            )
        )
    }

    suspend fun deleteTask(id: Long) = repository.deleteById(id)

    suspend fun getTaskById(id: Long): Task? = repository.findById(id)

    suspend fun getTasks(completed: Boolean? = null): List<Task> =
        if (completed != null) repository.findByCompleted(completed).toList() else repository.findAll().toList()
}
