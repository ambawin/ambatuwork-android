package win.ambatu.work.controller

import win.ambatu.work.R
import win.ambatu.work.data.model.User

object UserController {
    private val userList = listOf(
        User(
            id = 1,
            name = "Perrel Brown",
            email = "dreamy@gmail.com",
            picture = null,
            points = 2450,
            rank = 12
        ),
        User(
            id = 2,
            name = "Amba Singh",
            email = "amba@gmail.com",
            picture = null,
            points = 1200,
            rank = 45
        )
    )

    fun getAll(): List<User> {
        return userList
    }

    fun getById(id: Int): User? {
        return userList.find { it.id == id }
    }

    fun getPlaceholderUser(): User {
        return userList.first()
    }
}
