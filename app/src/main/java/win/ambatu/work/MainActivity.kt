package win.ambatu.work

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import win.ambatu.work.controller.UserController
import win.ambatu.work.data.intent.MainIntents
import win.ambatu.work.data.repository.AuthRepository
import win.ambatu.work.data.storage.SessionManager
import win.ambatu.work.feature.auth.GoogleSignInManager
import win.ambatu.work.feature.auth.LoginScreen
import win.ambatu.work.feature.auth.LoginViewModel
import win.ambatu.work.feature.home.HomeScreen
import win.ambatu.work.feature.network.NetworkModule
import win.ambatu.work.ui.theme.AmbatuWorkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(applicationContext)
        val authRepository = AuthRepository(NetworkModule.apiService)
        val googleSignInManager = GoogleSignInManager(this)

        val loginViewModel = LoginViewModel(
            googleSignInManager = googleSignInManager,
            authRepository = authRepository,
            sessionManager = sessionManager
        )

        enableEdgeToEdge()
        setContent {
            AmbatuWorkTheme {
                val uiState = loginViewModel.uiState.collectAsState().value

                if (uiState.isCheckingSession) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.isLoggedIn) {
                    val user = uiState.user?.let { dto ->
                        win.ambatu.work.data.model.User(
                            id = dto.id?.toInt() ?: 0,
                            name = dto.name ?: "",
                            email = dto.email ?: "",
                            picture = dto.avatarUrl,
                            points = 0,
                            rank = 0
                        )
                    } ?: win.ambatu.work.controller.UserController.getPlaceholderUser()

                    HomeScreen(
                        user = user,
                        onProfileIconClick = {
                            startActivity(
                                MainIntents.toProfile(
                                    this,
                                    user
                                )
                            )
                        },
                        onTodaysFocusViewAllClick = {
                            startActivity(
                                MainIntents.toTodaysFocus(
                                    this
                                )
                            )
                        },
                        onActiveTeamsViewAllClick = {
                            startActivity(
                                MainIntents.toActiveTeams(
                                    this
                                )
                            )
                        }
                    )
                } else {
                    LoginScreen(
                        viewModel = loginViewModel
                    )
                }
            }
        }

//        val currentUser = UserController.getPlaceholderUser()
//
//        enableEdgeToEdge()
//        setContent {
//            AmbatuWorkTheme() {
//                HomeScreen(
//                    user = currentUser,
//                    onProfileIconClick = {
//                        startActivity(
//                            MainIntents.toProfile(
//                                this,
//                                currentUser
//                            )
//                        )
//                    },
//                    onTodaysFocusViewAllClick = {
//                        startActivity(
//                            MainIntents.toTodaysFocus(
//                                this
//                            )
//                        )
//                    },
//                    onActiveTeamsViewAllClick = {
//                        startActivity(
//                            MainIntents.toActiveTeams(
//                                this
//                            )
//                        )
//                    }
//                )
//            }
//        }
    }
}