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
import win.ambatu.work.feature.auth.GoogleSignInManager
import win.ambatu.work.feature.auth.LoginScreen
import win.ambatu.work.feature.auth.LoginViewModel
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
fun ComposeApp() {
    val context = LocalContext.current
    val activity = context as Activity
    val sessionManager = remember { SessionManager(context) }
    val authRepository = remember { AuthRepository(NetworkModule.apiService) }
    val projectRepository = remember { ProjectRepository(NetworkModule.apiService) }
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

            Scaffold(
                bottomBar = {
                    if (uiState.isLoggedIn && backStack.none { it is Routes.Login }) {
                        NavigationBar {
                            NavigationBarItem(
                                selected = backStack.lastOrNull() is Routes.Home,
                                onClick = { backStack.replaceAll(Routes.Home) },
                                icon = { Icon(Icons.Default.Home, contentDescription = null) },
                                label = { Text("Home") }
                            )
                            NavigationBarItem(
                                selected = backStack.lastOrNull() is Routes.ScrumGuide,
                                onClick = { backStack.replaceAll(Routes.ScrumGuide) },
                                icon = { Icon(Icons.Default.Book, contentDescription = null, modifier = Modifier.size(24.dp)) },
                                label = { Text("SCRUM Guide") }
                            )
                            NavigationBarItem(
                                selected = backStack.lastOrNull() is Routes.Invitations,
                                onClick = { backStack.replaceAll(Routes.Invitations) },
                                icon = { Icon(Icons.Default.Mail, contentDescription = null) },
                                label = { Text("Invitations") }
                            )
                            NavigationBarItem(
                                selected = backStack.lastOrNull() is Routes.Profile,
                                onClick = { backStack.replaceAll(Routes.Profile(user)) },
                                icon = {
                                    AsyncImage(
                                        model = user.picture,
                                        placeholder = painterResource(id = R.drawable.profile_placeholder),
                                        error = painterResource(id = R.drawable.profile_placeholder),
                                        contentDescription = "User profile picture",
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                },
                                label = { Text("Profile") }
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Surface(modifier = Modifier.padding(innerPadding)) {
                    if (uiState.isCheckingSession) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else if (backStack.isNotEmpty()) {
                        NavDisplay(
                            backStack = backStack,
                            transitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
                            popTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
                            entryDecorators = listOf(
                                rememberSaveableStateHolderNavEntryDecorator(),
                                rememberViewModelStoreNavEntryDecorator()
                            ),
                            entryProvider = entryProvider {
                                entry<Routes.Login> {
                                    LoginScreen(viewModel = loginViewModel)
                                }
                                entry<Routes.Home> {
                                    val homeViewModel: HomeViewModel = viewModel {
                                        HomeViewModel(projectRepository, sessionManager)
                                    }

                                    HomeScreen(
                                        user = user,
                                        viewModel = homeViewModel,
                                        onProjectClick = { projectId ->
                                            context.startActivity(
                                                ProjectDetailActivity.createIntent(context, projectId)
                                            )
                                        }
                                    )
                                }
                                entry<Routes.ScrumGuide> {
                                    ScrumGuideScreen()
                                }
                                entry<Routes.Invitations> {
                                    val invitationViewModel: InvitationViewModel = viewModel {
                                        InvitationViewModel(projectRepository, sessionManager)
                                    }
                                    InvitationScreen(
                                        viewModel = invitationViewModel,
                                        onInvitationAccepted = {
                                            backStack.replaceAll(Routes.Home)
                                        }
                                    )
                                }
                                entry<Routes.Profile> { route ->
                                    ProfileScreen(
                                        user = route.user,
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
}
