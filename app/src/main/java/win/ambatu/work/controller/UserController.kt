package win.ambatu.work.controller

import win.ambatu.work.R
import win.ambatu.work.data.model.User

object UserController {
    fun getPlaceholderUser(): User {
        return User(
            id = 2,
            name = "Amba Singh",
            email = "amba@gmail.com",
            picture = null,
            points = 1200,
            rank = 45
        )
    }
}
