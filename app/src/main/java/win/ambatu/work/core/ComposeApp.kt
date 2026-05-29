package win.ambatu.work.core

import android.app.Activity
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import coil3.compose.AsyncImage
import win.ambatu.work.R
import win.ambatu.work.controller.UserController
import win.ambatu.work.data.model.User
import win.ambatu.work.data.repository.AuthRepository
import win.ambatu.work.data.repository.ProjectRepository
import win.ambatu.work.data.storage.SessionManager
import win.ambatu.work.feature.auth.LoginActivity
import win.ambatu.work.feature.home.HomeScreen
import win.ambatu.work.feature.home.HomeViewModel
import win.ambatu.work.feature.project.ProjectDetailActivity
import win.ambatu.work.feature.invitation.InvitationScreen
import win.ambatu.work.feature.invitation.InvitationViewModel
import win.ambatu.work.feature.scrum.ScrumGuideScreen
import win.ambatu.work.feature.network.NetworkModule
import win.ambatu.work.feature.profile.ProfileScreen
import win.ambatu.work.ui.theme.AmbatuWorkTheme

@Composable
fun ComposeApp(
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel()

    val backStack = rememberNavBackStack(Routes.Home as NavKey)

    val uiState by homeViewModel.uiState.collectAsState()
    val user = uiState.user ?: UserController.getPlaceholderUser()

    CompositionLocalProvider(LocalBackStack provides backStack) {
        AmbatuWorkTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                if (backStack.isNotEmpty()) {
                    NavDisplay(
                        backStack = backStack,
                        transitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
                        popTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
                        entryDecorators = listOf(
                            rememberSaveableStateHolderNavEntryDecorator(),
                            rememberViewModelStoreNavEntryDecorator()
                        ),
                        entryProvider = entryProvider {
                            entry<Routes.Home> {
                                HomeScreen(
                                    user = user,
                                    viewModel = homeViewModel,
                                    onProjectClick = { projectId ->
                                        context.startActivity(
                                            win.ambatu.work.feature.project.ProjectDetailActivity.createIntent(context, projectId)
                                        )
                                    },
                                    onProfileClick = {
                                        context.startActivity(
                                            win.ambatu.work.feature.profile.ProfileActivity.createIntent(context, user)
                                        )
                                    }
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}
