package win.ambatu.work.data.model

import android.os.Parcelable
import win.ambatu.work.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class Developer(
    val name: String,
    val studentId: String,
    val studyInfo: String,
    val githubUsername: String,
    val profileImage: Int
) : Parcelable {
}

val developers = listOf(
    Developer(
        name = "Muhammad Daffa Rahman",
        studentId = "L0124062",
        studyInfo = "Informatika 2024",
        githubUsername = "daffarahman",
        profileImage = R.drawable.profile_dapa
    ),
    Developer(
        name = "Muhammad Irfan",
        studentId = "L0124063",
        studyInfo = "Informatika 2024",
        githubUsername = "Irfunzz",
        profileImage = R.drawable.profile_irpan
    ),
    Developer(
        name = "Naufal Ahmad Fakhriza",
        studentId = "L0124068",
        studyInfo = "Informatika 2024",
        githubUsername = "sinopalll",
        profileImage = R.drawable.profile_nopal
    ),
    Developer(
        name = "Syaikhasril Maulana Firdaus",
        studentId = "L0124077",
        studyInfo = "Informatika 2024",
        githubUsername = "Syaasr",
        profileImage = R.drawable.profile_asril
    )
)
