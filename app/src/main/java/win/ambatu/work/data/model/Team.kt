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

val teamList = listOf(
    Team(
        id = 1,
        name = "PAB",
        topic = "Mobile App",
        picture = R.drawable.profile_placeholder
    ),
    Team(
        id = 2,
        name = "Pemweb",
        topic = "Web Development",
        picture = R.drawable.profile_placeholder
    ),
    Team(
        id = 3,
        name = "RPL",
        topic = "Software Engineering",
        picture = R.drawable.profile_placeholder
    ),
)