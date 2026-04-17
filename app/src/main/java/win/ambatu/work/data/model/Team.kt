package win.ambatu.work.data.model

import win.ambatu.work.R
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Team(
    val id: Int,
    val name: String,
    val topic: String,
    val picture: Int
) : Parcelable

