package win.ambatu.work.data.intent

import android.content.Context
import android.content.Intent
import win.ambatu.work.AboutActivity
import win.ambatu.work.ActiveTeamsActivity
import win.ambatu.work.ProfileActivity
import win.ambatu.work.TodaysFocusActivity
import win.ambatu.work.data.model.Developer
import win.ambatu.work.data.model.User

object MainIntents {
    const val EXTRA_DEVELOPER_LIST = "EXTRA_DEVELOPER_LIST"

    const val EXTRA_USER_ID = "EXTRA_USER_ID"
    const val EXTRA_USER_NAME = "EXTRA_USER_NAME"
    const val EXTRA_USER_EMAIL = "EXTRA_USER_EMAIL"
    const val EXTRA_USER_PICTURE = "EXTRA_USER_PICTURE"

    fun toAbout(
        context: Context,
        developerList: List<Developer>
    ): Intent {
        return Intent(context, AboutActivity::class.java).apply {
            putParcelableArrayListExtra(EXTRA_DEVELOPER_LIST, ArrayList(developerList))
        }
    }

    fun toProfile(
        context: Context,
        user: User
    ): Intent {
        return Intent(
            context,
            ProfileActivity::class.java
        ).apply {
            putExtra(EXTRA_USER_ID, user.id)
            putExtra(EXTRA_USER_NAME, user.name)
            putExtra(EXTRA_USER_EMAIL, user.email)
            putExtra(EXTRA_USER_PICTURE, user.picture)
        }
    }

    fun toTodaysFocus(
        context: Context
    ): Intent {
        return Intent(
            context,
            TodaysFocusActivity::class.java
        )
    }

    fun toActiveTeams(
        context: Context
    ): Intent {
        return Intent(
            context,
            ActiveTeamsActivity::class.java
        )
    }
}