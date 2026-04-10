package win.ambatu.work.intent

import android.content.Context
import android.content.Intent
import win.ambatu.work.AboutActivity
import win.ambatu.work.model.Developer

object MainIntents {
    const val EXTRA_DEVELOPER_LIST = "EXTRA_DEVELOPER_LIST"

    fun toAbout(
        context: Context,
        developerList: List<Developer>
    ): Intent {
        return Intent(context, AboutActivity::class.java).apply {
            putParcelableArrayListExtra(MainIntents.EXTRA_DEVELOPER_LIST, ArrayList(developerList))
        }
    }
}