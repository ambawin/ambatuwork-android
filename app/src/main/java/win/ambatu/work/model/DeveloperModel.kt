package win.ambatu.work.model

import win.ambatu.work.R

data class DeveloperModel(
    val name: String,
    val studentId: String,
    val studyInfo: String,
    val githubUsername: String,
    val profileImage: Int
)

val developerModels = listOf(
    DeveloperModel(
        name = "Muhammad Daffa Rahman",
        studentId = "L0124062",
        studyInfo = "Informatika 2024",
        githubUsername = "daffarahman",
        profileImage = R.drawable.profile_dapa
    ),
    DeveloperModel(
        name = "Muhammad Irfan",
        studentId = "L0124063",
        studyInfo = "Informatika 2024",
        githubUsername = "Irfunzz",
        profileImage = R.drawable.profile_dapa
    ),
    DeveloperModel(
        name = "Naufal Ahmad Fakhriza",
        studentId = "L0124068",
        studyInfo = "Informatika 2024",
        githubUsername = "sinopalll",
        profileImage = R.drawable.profile_dapa
    ),
    DeveloperModel(
        name = "Syaikhasril Maulana Firdaus",
        studentId = "L0124077",
        studyInfo = "Informatika 2024",
        githubUsername = "Syaasr",
        profileImage = R.drawable.profile_dapa
    )
)
