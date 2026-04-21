package com.example.repository

import com.example.model.Task
import com.example.model.TaskStatus
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import org.springframework.jdbc.core.JdbcTemplate
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.sql.ResultSet
import java.time.LocalDateTime

@Repository
class TaskRepositoryImpl(
    private val jdbcTemplate: JdbcTemplate
) : TaskRepository {

    private val rowMapper = RowMapper { rs: ResultSet, _: Int ->
        Task(
            id = rs.getLong("id"),
            title = rs.getString("title"),
            description = rs.getString("description"),
            status = TaskStatus.valueOf(rs.getString("status")),
            createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
            updatedAt = rs.getTimestamp("updated_at").toLocalDateTime()
        )
    }

    override fun save(task: Task): Mono<Task> {
        val sql = """
            INSERT INTO tasks (title, description, status, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?)
        """

        return Mono.fromCallable {
            val keyHolder = java.sql.Statement.RETURN_GENERATED_KEYS
            val ps = jdbcTemplate.dataSource!!.connection.prepareStatement(sql, keyHolder)
            ps.setString(1, task.title)
            ps.setString(2, task.description)
            ps.setString(3, task.status.name)
            ps.setTimestamp(4, java.sql.Timestamp.valueOf(task.createdAt))
            ps.setTimestamp(5, java.sql.Timestamp.valueOf(task.updatedAt))
            ps.executeUpdate()
            val rs = ps.generatedKeys
            val id = if (rs.next()) rs.getLong(1) else throw RuntimeException("Failed to obtain id")
            rs.close(); ps.close()
            task.copy(id = id)
        }.subscribeOn(Schedulers.boundedElastic())
    }

    override fun findById(id: Long): Mono<Task> {
        val sql = "SELECT * FROM tasks WHERE id = ?"

        return Mono.fromCallable {
            val list = jdbcTemplate.query(sql, rowMapper, id)
            if (list.isEmpty()) throw NoSuchElementException("Task not found")
            list[0]
        }.subscribeOn(Schedulers.boundedElastic())
    }

    override fun findAll(page: Int, size: Int, status: TaskStatus?): Mono<Pair<List<Task>, Long>> {
        val offset = page.toLong() * size
        val base = StringBuilder("FROM tasks")
        val params = mutableListOf<Any>()
        if (status != null) {
            base.append(" WHERE status = ?")
            params.add(status.name)
        }
        val countSql = "SELECT COUNT(*) $base"
        val dataSql = "SELECT * $base ORDER BY created_at DESC LIMIT ? OFFSET ?"

        return Mono.fromCallable {
            val total = jdbcTemplate.queryForObject(countSql, params.toTypedArray(), Long::class.java) ?: 0L
            params.add(size)
            params.add(offset)
            val rows = jdbcTemplate.query(dataSql, rowMapper, *params.toTypedArray())
            Pair(rows, total)
        }.subscribeOn(Schedulers.boundedElastic())
    }

    override fun updateStatus(id: Long, status: TaskStatus, updatedAt: LocalDateTime): Mono<Int> {
        val sql = "UPDATE tasks SET status = ?, updated_at = ? WHERE id = ?"

        return Mono.fromCallable {
            jdbcTemplate.update(sql, status.name, java.sql.Timestamp.valueOf(updatedAt), id)
        }.subscribeOn(Schedulers.boundedElastic())
    }

    override fun deleteById(id: Long): Mono<Int> {
        val sql = "DELETE FROM tasks WHERE id = ?"

        return Mono.fromCallable {
            jdbcTemplate.update(sql, id)
        }.subscribeOn(Schedulers.boundedElastic())
    }
}
