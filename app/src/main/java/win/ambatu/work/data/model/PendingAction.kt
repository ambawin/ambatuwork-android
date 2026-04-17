package win.ambatu.work.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class PendingAction(
    val id: Int,
    val userId: Int,
    val title: String,
    val description: String,
    val submitText: String = "Submit",
    val due: LocalDateTime,
    val onClick: () -> Unit = {}
) : Parcelable

fun getPendingActionsByUserId(userId: Int): List<PendingAction> {
    return pendingActionList.filter { it.userId == userId }
}

val pendingActionList = listOf(
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
