package com.app.demokorutines

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/tasks")
class TaskController(private val service: TaskService) {

    @GetMapping
    suspend fun getAllTasks(@RequestParam completed: Boolean?): List<Task> = service.getTasks(completed)

    @GetMapping("/{id}")
    suspend fun getTaskById(@PathVariable id: Long): Task? = service.getTaskById(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createTask(@RequestBody task: Task): Task = service.createTask(task)

    @PutMapping("/{id}")
    suspend fun updateTask(@PathVariable id: Long, @RequestBody task: Task): Task = service.updateTask(id, task)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteTask(@PathVariable id: Long) = service.deleteTask(id)
}
