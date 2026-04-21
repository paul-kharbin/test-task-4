package com.example.controller

import com.example.dto.PagedCollection
import com.example.dto.TaskCreateRequest
import com.example.dto.TaskResponse
import com.example.dto.TaskStatusUpdateRequest
import com.example.model.TaskStatus
import com.example.service.TaskService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/tasks")
class TaskController(
    private val service: TaskService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createTask(@Valid @RequestBody req: TaskCreateRequest): Mono<TaskResponse> =
        service.createTask(req)

    @GetMapping
    fun getTasks(
        @RequestParam page: Int?,
        @RequestParam size: Int?,
        @RequestParam(required = false) status: TaskStatus?
    ): Mono<PagedCollection<TaskResponse>> {
        val p = page ?: throw IllegalArgumentException("page is required")
        val s = size ?: throw IllegalArgumentException("size is required")
        return service.getTasks(p, s, status)
    }

    @GetMapping("/{id}")
    fun getTask(@PathVariable id: Long): Mono<TaskResponse> =
        service.getTaskById(id)

    @PatchMapping("/{id}/status")
    fun updateStatus(@PathVariable id: Long, @Valid @RequestBody req: TaskStatusUpdateRequest): Mono<TaskResponse> =
        service.updateStatus(id, req.status)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTask(@PathVariable id: Long): Mono<Void> =
        service.deleteTask(id)
}
