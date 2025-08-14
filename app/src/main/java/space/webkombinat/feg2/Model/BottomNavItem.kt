package space.webkombinat.feg2.Model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SsidChart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String,
    val number: Int,
) {
    object LogList: BottomNavItem(route = "list", icon = Icons.Default.List, label = "LOG", number = 0)
    object Chart: BottomNavItem(route = "chart", icon = Icons.Default.SsidChart,label = "CHART", number = 1)
    object Setting: BottomNavItem(route = "setting", icon = Icons.Default.Settings, label = "SETTING", number = 2)
}