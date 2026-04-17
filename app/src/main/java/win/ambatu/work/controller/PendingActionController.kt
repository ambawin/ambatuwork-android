package win.ambatu.work.controller

import win.ambatu.work.data.model.PendingAction
import java.time.LocalDateTime

object PendingActionController {
    private val pendingActionList = listOf(
        PendingAction(
            id = 1,
            userId = 1,
            title = "Daily Proof",
            description = "Submit your daily proof",
            due = LocalDateTime.of(2026, 4, 17, 23, 59)
        ),
        PendingAction(
            id = 2,
            userId = 1,
            title = "Daily Proof",
            description = "Submit your daily proof",
            due = LocalDateTime.of(2026, 4, 12, 23, 59)
        ),
        PendingAction(
            id = 3,
            userId = 2,
            title = "Daily Proof",
            description = "Submit your daily proof",
            due = LocalDateTime.of(2026, 4, 20, 23, 59)
        ),
    )

    fun getAll(): List<PendingAction> {
        return pendingActionList
    }

    fun getByUserId(userId: Int): List<PendingAction> {
        return pendingActionList.filter { it.userId == userId }
    }
}
