package win.ambatu.work.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {
    fun formatDueDate(dueDateTime: LocalDateTime): String {
        val today = LocalDate.now()
        val dueDate = dueDateTime.toLocalDate()

        return when {
            dueDate.isBefore(today) -> "Missing"
            dueDate == today -> "Today"
            dueDate == today.plusDays(1) -> "Tomorrow"
            else -> dueDateTime.format(DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH))
        }
    }
}
