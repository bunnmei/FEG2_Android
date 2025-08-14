package space.webkombinat.feg2.Model

sealed class LogNavItem(
    val route: String,
    val label: String
) {
    object LogList:  LogNavItem(route = "logList", label = "top")
    object LogDetail:  LogNavItem(route = "/logDetail/{profileId}", label = "logDetail")
    object LogEdit:  LogNavItem(route = "/logEdit/{profileId}", label = "logEdit")

    companion object {
        val logRoutes by lazy { setOf(LogList.route, LogDetail.route, LogEdit.route) }
    }
}