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

