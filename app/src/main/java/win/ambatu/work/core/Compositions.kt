package win.ambatu.work.core

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.snapshots.Snapshot
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

val LocalBackStack = compositionLocalOf<NavBackStack<NavKey>> {
    error("error: LocalBackStack not provided")
}

/**
 * Pushes a new [key] to the backstack only if it's not already the top destination.
 * This prevents crashes or duplicate screens caused by rapid multiple clicks.
 */
fun <T : NavKey> NavBackStack<T>.pushUnique(key: T) {
    if (lastOrNull() != key) {
        add(key)
    }
}

/**
 * Pops the top destination only if there are more than 1 elements in the stack.
 * This ensures the backstack never becomes empty, preventing NavDisplay crashes.
 */
fun <T : NavKey> NavBackStack<T>.popSafe() {
    if (size > 1) {
        removeAt(size - 1)
    }
}

/**
 * Replaces the entire backstack with a single [key] safely.
 * Uses a snapshot to ensure the update is atomic and the stack is never observed as empty.
 */
fun <T : NavKey> NavBackStack<T>.replaceAll(key: T) {
    if (size == 1 && firstOrNull() == key) return
    Snapshot.withMutableSnapshot {
        clear()
        add(key)
    }
}
