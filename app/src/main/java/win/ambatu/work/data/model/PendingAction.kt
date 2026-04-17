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
