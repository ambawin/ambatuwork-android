package win.ambatu.work.controller

import win.ambatu.work.R
import win.ambatu.work.data.model.Developer

object DeveloperController {
    private val developerList = listOf(
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

    fun getAll(): List<Developer> {
        return developerList
    }
}
