package win.ambatu.work.data.model

import android.os.Parcelable
import win.ambatu.work.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val picture: Int,
    val points: Int = 0,
    val rank: Int = 0
) : Parcelable
