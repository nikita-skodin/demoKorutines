package com.app.demokorutines

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.withTimeoutOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

@Service
class TaskService(private val repository: TaskRepository) {

    private val webClient = WebClient.create()

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

    suspend fun getTaskWithDetails(id: Long): TaskDetails {
        return coroutineScope {
            val taskDeferred =
                async { repository.findById(id) ?: throw IllegalArgumentException("Task not found") }
            val titleDeferred = async {
                try {
                    getThirdPartyTaskTitle(id)
                } catch (e: RuntimeException) {
                    "no third party title found"
                }
            }

            val task = taskDeferred.await()
            val title = titleDeferred.await()

            TaskDetails(task.id, getTitle(task.title, title), task.description, task.completed)
        }
    }

    fun getTitle(dbDetails: String, apiDetails: String): String {
        return "$dbDetails; $apiDetails"
    }

    suspend fun getThirdPartyTaskTitle(id: Long): String {
        val url = "https://jsonplaceholder.typicode.com/to1dos/$id"
        return webClient
            .get()
            .uri(url)
            .retrieve()
            .onStatus({ it.is4xxClientError || it.is5xxServerError }) {
                Mono.error(RuntimeException("Request failed with status: ${it.statusCode()}"))
            }
            .bodyToMono(Todo::class.java)
            .map { it.title }
            .awaitSingle()
    }

    suspend fun getTaskWithTimeout(id: Long): Task? {
        return withTimeoutOrNull(3000) {
            repository.findById(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found")
        } ?: throw ResponseStatusException(HttpStatus.REQUEST_TIMEOUT, "Timeout reached")
    }

    data class Todo(
        val userId: Int,
        val id: Int,
        val title: String,
        val completed: Boolean
    )
}
