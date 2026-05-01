package win.ambatu.work.core

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import win.ambatu.work.controller.UserController
import win.ambatu.work.data.model.User
import win.ambatu.work.data.repository.AuthRepository
import win.ambatu.work.data.storage.SessionManager
import win.ambatu.work.feature.auth.GoogleSignInManager
import win.ambatu.work.feature.auth.LoginScreen
import win.ambatu.work.feature.auth.LoginViewModel
import win.ambatu.work.feature.home.HomeScreen
import win.ambatu.work.feature.network.NetworkModule
import win.ambatu.work.feature.profile.ProfileScreen
import win.ambatu.work.ui.theme.AmbatuWorkTheme

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

    LaunchedEffect(uiState.isLoggedIn, uiState.isCheckingSession) {
        if (uiState.isCheckingSession) return@LaunchedEffect

        if (uiState.isLoggedIn) {
            if (backStack.any { it is Routes.Login }) {
                backStack.replaceAll(Routes.Home)
            }
        } else {
            if (backStack.none { it is Routes.Login }) {
                backStack.replaceAll(Routes.Login)
            }
        }
    }

    CompositionLocalProvider(LocalBackStack provides backStack) {
        AmbatuWorkTheme {
            Surface {
                if (uiState.isCheckingSession) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (backStack.isNotEmpty()) {
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
                                        backStack.pushUnique(Routes.Profile(user))
                                    }
                                )
                            }
                            entry<Routes.Profile> { route ->
                                ProfileScreen(
                                    user = route.user,
                                    onNavigationBackClick = {
                                        backStack.popSafe()
                                    },
                                    onLogoutClick = {
                                        loginViewModel.logout()
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
