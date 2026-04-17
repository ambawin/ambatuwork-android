package win.ambatu.work.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class Task(
    val id: Int,
    val teamId: Int,
    val title: String,
    val detail: String,
    val point: Int,
    val due: LocalDateTime
) : Parcelable

val taskList = listOf(
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
)