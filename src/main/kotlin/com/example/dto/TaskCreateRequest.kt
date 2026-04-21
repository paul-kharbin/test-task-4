package com.example.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class TaskCreateRequest(
    @field:NotBlank(message = "title is required")
    @field:Size(min = 3, max = 100, message = "title length must be between 3 and 100")
    val title: String,
    val description: String?
)