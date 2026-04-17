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
) : Parcelable
