package win.ambatu.work.core

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import win.ambatu.work.ui.theme.AmbatuWorkTheme

@Composable
fun ComposeApp() {
    val backStack = rememberNavBackStack(Routes.AuthRoute)

    CompositionLocalProvider(LocalBackStack provides backStack) {
        AmbatuWorkTheme() {
            Surface {
                NavDisplay(
                    backStack = backStack,
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator()
                    ),
                    entryProvider = entryProvider {
                        // auth
//                        entry<Routes.AuthRoute> { AuthScreen() }
//
//                        // todo
//                        entry<Routes.ListTodoRoute> { ListTodoScreen() }
//                        entry<Routes.CreateTodoRoute> { CreateTodoScreen() }
//                        entry<Routes.DetailTodoRoute> {
//                            val id = it.id
//
//                            DetailTodoScreen(id = id)
//                        }
                    }
                )
            }
        }
    }
}