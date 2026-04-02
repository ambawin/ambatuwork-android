package win.ambatu.work.intent

import android.content.Context
import android.content.Intent
import win.ambatu.work.AboutActivity

object MainIntents {
    const val EXTRA_NAME = "EXTRA_NAME"
    const val EXTRA_STUDENT_ID = "EXTRA_STUDENT_ID"
    const val EXTRA_STUDY_INFO = "EXTRA_STUDY_INFO"
    const val EXTRA_GITHUB_USERNAME = "EXTRA_GITHUB_USERNAME"
    const val EXTRA_PROFILE_IMAGE = "EXTRA_PROFILE_IMAGE"

    fun toAbout(
        context: Context,
        nameArray: Array<String>,
        studentIdArray: Array<String>,
        studyInfoArray: Array<String>,
        githubUsernameArray: Array<String>,
        profileImageArray: IntArray
    ): Intent {
        return Intent(context, AboutActivity::class.java).apply {
            putExtra(MainIntents.EXTRA_NAME, nameArray)
            putExtra(MainIntents.EXTRA_STUDENT_ID, studentIdArray)
            putExtra(MainIntents.EXTRA_STUDY_INFO, studyInfoArray)
            putExtra(MainIntents.EXTRA_GITHUB_USERNAME, githubUsernameArray)
            putExtra(MainIntents.EXTRA_PROFILE_IMAGE, profileImageArray)
        }
    }
}