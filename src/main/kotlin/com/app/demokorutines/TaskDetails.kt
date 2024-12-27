package com.app.demokorutines

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class TaskDetails(
    val id: Long? = null,
    val fullTitle: String,
    val description: String?,
    val completed: Boolean = false
)
