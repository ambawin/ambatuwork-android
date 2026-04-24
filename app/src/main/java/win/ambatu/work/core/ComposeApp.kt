package win.ambatu.work.core

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import win.ambatu.work.controller.DeveloperController
import win.ambatu.work.controller.UserController
import win.ambatu.work.data.model.User
import win.ambatu.work.data.repository.AuthRepository
import win.ambatu.work.data.storage.SessionManager
import win.ambatu.work.feature.about.AboutScreen
import win.ambatu.work.feature.auth.GoogleSignInManager
import win.ambatu.work.feature.auth.LoginScreen
import win.ambatu.work.feature.auth.LoginViewModel
import win.ambatu.work.feature.home.HomeScreen
import win.ambatu.work.feature.network.NetworkModule
import win.ambatu.work.feature.profile.ProfileScreen
import win.ambatu.work.feature.team.ActiveTeamsScreen
import win.ambatu.work.feature.team.TodaysFocusScreen
import win.ambatu.work.ui.theme.AmbatuWorkTheme

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ComposeApp() {
    val context = LocalContext.current
    val activity = context as Activity
    val sessionManager = remember { SessionManager(context) }
    val authRepository = remember { AuthRepository(NetworkModule.apiService) }
    val googleSignInManager = remember { GoogleSignInManager(activity) }

    val loginViewModel: LoginViewModel = viewModel {
        LoginViewModel(
            googleSignInManager = googleSignInManager,
            authRepository = authRepository,
            sessionManager = sessionManager
        )
    }

    val uiState by loginViewModel.uiState.collectAsState()
    val backStack = rememberNavBackStack(Routes.Login as NavKey)

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            if (backStack.last() is Routes.Login) {
                backStack.clear()
                backStack.add(Routes.Home)
            }
        } else if (!uiState.isCheckingSession && !uiState.isLoggedIn) {
            backStack.clear()
            backStack.add(Routes.Login)
        }
    }

    CompositionLocalProvider(LocalBackStack provides backStack) {
        AmbatuWorkTheme {
            Surface {
                if (uiState.isCheckingSession) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    NavDisplay(
                        backStack = backStack,
                        entryDecorators = listOf(
                            rememberSaveableStateHolderNavEntryDecorator(),
                            rememberViewModelStoreNavEntryDecorator()
                        ),
                        entryProvider = entryProvider {
                            entry<Routes.Login> {
                                LoginScreen(viewModel = loginViewModel)
                            }
                            entry<Routes.Home> {
                                val user = uiState.user?.let { dto ->
                                    User(
                                        id = dto.id?.toInt() ?: 0,
                                        name = dto.name ?: "",
                                        email = dto.email ?: "",
                                        picture = dto.avatarUrl,
                                        points = 0,
                                        rank = 0
                                    )
                                } ?: UserController.getPlaceholderUser()

                                HomeScreen(
                                    user = user,
                                    onProfileIconClick = {
                                        backStack.add(Routes.Profile(user))
                                    },
                                    onTodaysFocusViewAllClick = {
                                        backStack.add(Routes.TodaysFocus)
                                    },
                                    onActiveTeamsViewAllClick = {
                                        backStack.add(Routes.ActiveTeams)
                                    }
                                )
                            }
                            entry<Routes.Profile> { route ->
                                ProfileScreen(
                                    user = route.user,
                                    onNavigationBackClick = {
                                        backStack.removeAt(backStack.size - 1)
                                    },
                                    onProfileIconClick = { /* Already on profile */ },
                                    onAboutClick = {
                                        backStack.add(Routes.About(DeveloperController.getAll()))
                                    },
                                    onLogoutClick = {
                                        loginViewModel.logout()
                                    }
                                )
                            }
                            entry<Routes.TodaysFocus> {
                                TodaysFocusScreen(
                                    onNavigationBackClick = {
                                        backStack.removeAt(backStack.size - 1)
                                    }
                                )
                            }
                            entry<Routes.ActiveTeams> {
                                ActiveTeamsScreen(
                                    onNavigationBackClick = {
                                        backStack.removeAt(backStack.size - 1)
                                    }
                                )
                            }
                            entry<Routes.About> { route ->
                                Scaffold(
                                    topBar = {
                                        TopAppBar(
                                            colors = TopAppBarDefaults.topAppBarColors(
                                                containerColor = MaterialTheme.colorScheme.background,
                                                titleContentColor = MaterialTheme.colorScheme.primary,
                                            ),
                                            title = {
                                                Text(
                                                    "About",
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            },
                                            navigationIcon = {
                                                IconButton(onClick = {
                                                    backStack.removeAt(backStack.size - 1)
                                                }) {
                                                    Icon(
                                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                        contentDescription = "Back",
                                                        tint = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                            },
                                        )
                                    }
                                ) { innerPadding ->
                                    AboutScreen(
                                        innerPadding = innerPadding,
                                        developers = route.developers,
                                        onShareAppButtonClick = {
                                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                                putExtra(Intent.EXTRA_SUBJECT, "Download Insider Preview of our App")
                                                putExtra(Intent.EXTRA_TEXT, "Check out AmbatuWork App!\n\nhttps://github.com/ambawin/ambatuwork-android")
                                                type = "text/plain"
                                            }
                                            context.startActivity(Intent.createChooser(sendIntent, "Share this app"))
                                        }
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
