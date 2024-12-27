package com.app.demokorutines

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("tasks")
data class Task(
    @Id val id: Long = 0L,
    val title: String,
    val description: String?,
    val completed: Boolean = false
)
