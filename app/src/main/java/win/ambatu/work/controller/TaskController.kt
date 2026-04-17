package win.ambatu.work.controller

import win.ambatu.work.data.model.Task
import win.ambatu.work.data.model.Team
import java.time.LocalDateTime

object TaskController {
    private val taskList = listOf(
        Task(
            id = 1,
            teamId = 1,
            title = "Laprak 1 Latar Belakang",
            detail = "Isi paragraf dasar teori juga",
            point = 25,
            due = LocalDateTime.of(2026, 4, 24, 23, 59)
        ),
        Task(
            id = 2,
            teamId = 1,
            title = "Record video demo",
            detail = "Jelasin flow dari app-nya",
            point = 30,
            due = LocalDateTime.of(2026, 4, 29, 20, 0)
        ),
        Task(
            id = 3,
            teamId = 3,
            title = "Software Risk",
            detail = "List risk dari project kita",
            point = 10,
            due = LocalDateTime.of(2026, 5, 8, 18, 30)
        ),
        Task(
            id = 4,
            teamId = 3,
            title = "Architecture Diagram",
            detail = "Matengin stacknya bro",
            point = 14,
            due = LocalDateTime.of(2026, 5, 15, 21, 0)
        ),
        Task(
            id = 5,
            teamId = 2,
            title = "UI Wireframe",
            detail = "Bikin draft tampilan buat semua halaman utama",
            point = 18,
            due = LocalDateTime.of(2026, 4, 26, 19, 30)
        ),
        Task(
            id = 6,
            teamId = 2,
            title = "Database Schema",
            detail = "Rapihin relasi tabel dan revisi kolom yang kurang",
            point = 22,
            due = LocalDateTime.of(2026, 5, 2, 17, 0)
        ),
        Task(
            id = 7,
            teamId = 1,
            title = "API Integration",
            detail = "Connect endpoint auth sama dashboard",
            point = 28,
            due = LocalDateTime.of(2026, 5, 5, 22, 15)
        ),
        Task(
            id = 8,
            teamId = 2,
            title = "Testing Login Flow",
            detail = "Cek validasi form dan error handling",
            point = 16,
            due = LocalDateTime.of(2026, 5, 7, 16, 45)
        ),
        Task(
            id = 9,
            teamId = 3,
            title = "Deployment Setup",
            detail = "Siapin config staging buat presentasi",
            point = 20,
            due = LocalDateTime.of(2026, 5, 12, 14, 0)
        ),
        Task(
            id = 10,
            teamId = 2,
            title = "Final Presentation Slide",
            detail = "Susun slide hasil progress dan pembagian materi",
            point = 24,
            due = LocalDateTime.of(2026, 5, 18, 20, 30)
        )
    )

    fun getAll() : List<Task> {
        return taskList
    }

    fun getById(id: Int) : Task? {
        return taskList.find { it.id == id }
    }

    fun getTeamFromTaskId(teamId: Int) : Team? {
        return TeamController.getAll().associateBy { it.id } [teamId]
    }
}