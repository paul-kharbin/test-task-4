package com.example.service

import com.example.dto.PagedCollection
import com.example.dto.TaskCreateRequest
import com.example.dto.TaskResponse
import com.example.model.Task
import com.example.model.TaskStatus
import com.example.repository.TaskRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class TaskServiceImpl(
    private val repo: TaskRepository
) : TaskService {

    override fun createTask(req: TaskCreateRequest): Mono<TaskResponse> {
        val now = LocalDateTime.now()
        val task = Task(
            id = null,
            title = req.title,
            description = req.description,
            status = TaskStatus.NEW,
            createdAt = now,
            updatedAt = now
        )

        return repo.save(task).map { it.toResponse() }
    }

    override fun getTaskById(id: Long): Mono<TaskResponse> =
        repo.findById(id).map { it.toResponse() }

    override fun getTasks(page: Int, size: Int, status: TaskStatus?): Mono<PagedCollection<TaskResponse>> =
        repo.findAll(page, size, status)
            .map { (list, total) ->
                val content = list.map { it.toResponse() }
                PagedCollection(
                    content = content,
                    page = page,
                    size = size,
                    totalElements = total,
                    totalPages = if (total == 0L) 0 else ((total + size - 1) / size).toInt()
                )
            }

    override fun updateStatus(id: Long, status: TaskStatus): Mono<TaskResponse> {
        val now = LocalDateTime.now()

        return repo.updateStatus(id, status, now)
            .flatMap { updated ->
                if (updated == 0) Mono.error(NoSuchElementException("Task not found"))
                else repo.findById(id).map { it.toResponse() }
            }
    }

    override fun deleteTask(id: Long): Mono<Void> =
        repo.deleteById(id)
            .flatMap { deleted -> if (deleted == 0) Mono.error(NoSuchElementException("Task not found")) else Mono.empty<Void>() }
}

private fun Task.toResponse() = TaskResponse(
    id = this.id!!,
    title = this.title,
    description = this.description,
    status = this.status,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)
