package com.example.repository

import com.example.model.Task
import com.example.model.TaskStatus
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface TaskRepository {
    fun save(task: Task): Mono<Task>
    fun findById(id: Long): Mono<Task>
    fun findAll(page: Int, size: Int, status: TaskStatus?): Mono<Pair<List<Task>, Long>>
    fun updateStatus(id: Long, status: TaskStatus, updatedAt: java.time.LocalDateTime): Mono<Int>
    fun deleteById(id: Long): Mono<Int>
}