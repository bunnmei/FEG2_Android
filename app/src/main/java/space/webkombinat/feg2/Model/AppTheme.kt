package space.webkombinat.feg2.Model

import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppTheme(
    val name: String,
    val num: Int
) {
    object System : AppTheme("System", 0)
    object Black : AppTheme("Black", 1)
    object White : AppTheme("White", 2)
}
