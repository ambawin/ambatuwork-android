package win.ambatu.work.feature.auth

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow
import win.ambatu.work.BuildConfig
import win.ambatu.work.ui.theme.AmbatuWorkTheme

@Composable
fun LoginScreen(
    viewModel: LoginViewModel
) {
    val uiState = viewModel.uiState.collectAsState()
    Content(
        uiState = uiState,
        onSignInWithGoogleClick = {
            viewModel.signInWithGoogle()
            Log.d("GoogleAuth", "Web client id = ${BuildConfig.GOOGLE_WEB_CLIENT_ID}")
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    uiState: State<LoginUiState>,
    onSignInWithGoogleClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                modifier = Modifier
                    .padding(top = 8.dp),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Login",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
            )
        }
    ) { innerPadding ->
        LazyColumn (
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Button (
                    onClick = onSignInWithGoogleClick,
                    enabled = !uiState.value.isLoading
                ) {
                    Text(
                        if (uiState.value.isLoading) "Signing in..."
                        else "Sign in with Google"
                    )
                }

                uiState.value.error?.let {
                    Text(text = it)
                }
            }
        }
    }
}

@Composable
@Preview(group = "Login")
private fun LoginPreview() {
    AmbatuWorkTheme {
//        Content()
    }
}