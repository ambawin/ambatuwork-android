package win.ambatu.work.data.model

import android.os.Parcelable
import win.ambatu.work.R
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val picture: String?,
    val points: Int = 0,
    val rank: Int = 0
) : Parcelable
