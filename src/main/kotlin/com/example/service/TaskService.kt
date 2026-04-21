package com.example.service

import com.example.dto.PagedCollection
import com.example.dto.TaskCreateRequest
import com.example.dto.TaskResponse
import com.example.model.TaskStatus
import reactor.core.publisher.Mono

interface TaskService {
    fun createTask(req: TaskCreateRequest): Mono<TaskResponse>
    fun getTaskById(id: Long): Mono<TaskResponse>
    fun getTasks(page: Int, size: Int, status: TaskStatus?): Mono<PagedCollection<TaskResponse>>
    fun updateStatus(id: Long, status: TaskStatus): Mono<TaskResponse>
    fun deleteTask(id: Long): Mono<Void>
}
