package win.ambatu.work.controller

import win.ambatu.work.R
import win.ambatu.work.data.model.Team

object TeamController {
    private val teamList = listOf(
        Team(
            id = 1,
            name = "PAB",
            topic = "Mobile App",
            picture = R.drawable.placeholder_banner
        ),
        Team(
            id = 2,
            name = "Pemweb",
            topic = "Web Development",
            picture = R.drawable.placeholder_banner
        ),
        Team(
            id = 3,
            name = "RPL",
            topic = "Software Engineering",
            picture = R.drawable.placeholder_banner
        ),
    )

    fun getAll() : List<Team> {
        return teamList
    }

    fun getById(id: Int) : Team? {
        return teamList.find { it.id == id }
    }
}