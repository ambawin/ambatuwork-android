package win.ambatu.work.core

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import win.ambatu.work.data.model.Developer
import win.ambatu.work.data.model.User

@Serializable
sealed interface Routes : NavKey {
    @Serializable
    data object Login : Routes

    @Serializable
    data object Home : Routes

    @Serializable
    data class Profile(val user: User) : Routes
}