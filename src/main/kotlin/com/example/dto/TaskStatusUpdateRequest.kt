package com.example.dto

import com.example.model.TaskStatus
import jakarta.validation.constraints.NotNull

data class TaskStatusUpdateRequest(
    @field:NotNull(message = "status is required")
    val status: TaskStatus
)