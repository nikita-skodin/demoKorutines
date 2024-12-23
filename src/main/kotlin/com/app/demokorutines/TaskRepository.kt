package com.app.demokorutines

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface TaskRepository : CoroutineCrudRepository<Task, Long> {
    fun findByCompleted(completed: Boolean): Flow<Task>
}
