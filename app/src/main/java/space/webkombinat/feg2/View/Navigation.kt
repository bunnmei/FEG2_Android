package space.webkombinat.feg2.View

import android.annotation.SuppressLint
import androidx.annotation.RequiresPermission
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import space.webkombinat.feg2.Model.BottomNavItem
import space.webkombinat.feg2.View.Devide.BottomNavBar
import space.webkombinat.feg2.Model.LogNavItem
import space.webkombinat.feg2.View.Screen.ChartScreen
import space.webkombinat.feg2.View.Screen.LogDetailScreen
import space.webkombinat.feg2.View.Screen.LogEditScreen
import space.webkombinat.feg2.View.Screen.LogListScreen
import space.webkombinat.feg2.View.Screen.SettingScreen
import space.webkombinat.feg2.ViewModel.ChartVM
import space.webkombinat.feg2.ViewModel.LogDetailVM
import space.webkombinat.feg2.ViewModel.LogEditVM
import space.webkombinat.feg2.ViewModel.LogListVM
import space.webkombinat.feg2.ViewModel.SettingVM

@RequiresPermission(android.Manifest.permission.BLUETOOTH_SCAN)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Navigation(modifier: Modifier = Modifier) {
    val navCont = rememberNavController()
    val backStackEntry = navCont.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route
    val bottomVisible = remember { mutableStateOf(true) }
    val bottomVisibleClickableRoutes = setOf(BottomNavItem.Chart.route, LogNavItem.LogDetail.route)

    Scaffold(
        bottomBar = {
            BottomNavBar(
                visible = bottomVisible,
                navCont = navCont
            ){
                navCont.navigate(
                    route = it.route,
                ) {
                    popUpTo(navCont.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    ){ // Scaffold

        NavHost(
            modifier = modifier.windowInsetsPadding(WindowInsets.safeDrawing),
            navController = navCont,
            startDestination = BottomNavItem.Chart.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popExitTransition = { ExitTransition.None},
            popEnterTransition = { EnterTransition.None },
        ) {

            logScreens(navCont = navCont, bottomVisible = bottomVisible)

            composable(
                route = BottomNavItem.Chart.route,
            )  {
                val chartVM = hiltViewModel<ChartVM>()
                ChartScreen(
                    vm = chartVM,
                    bottomVisible = bottomVisible,
                ){
                    if(currentRoute in bottomVisibleClickableRoutes){
                        bottomVisible.value = !bottomVisible.value
                    }
                }
            }

            composable(
                route = BottomNavItem.Setting.route,
            ){
                val vm = hiltViewModel<SettingVM>()
                SettingScreen(vm = vm)
            }

        }
    }
}

fun NavGraphBuilder.logScreens(
    navCont: NavHostController,
    bottomVisible: MutableState<Boolean>
) {
    navigation(
        startDestination = LogNavItem.LogList.route,
        route = BottomNavItem.LogList.route,
    ) {
        composable(
            route = LogNavItem.LogList.route,
        ) {
            val vm = hiltViewModel<LogListVM>()
            LogListScreen(vm = vm, navCont = navCont)
        }

        composable(
            route = LogNavItem.LogDetail.route,
            arguments = listOf(navArgument("profileId"){ type = NavType.LongType })
        ) {
            val profileId = it.arguments?.getLong("profileId")
            val vm = hiltViewModel<LogDetailVM>()

            if(profileId != null){

                LogDetailScreen(
                    vm = vm,
                    navCont = navCont,
                    bottomVisible = bottomVisible
                ){
                    bottomVisible.value = !bottomVisible.value
                }
            } else {
                Text("ProfileId is null")
            }
        }

        composable(
            route = LogNavItem.LogEdit.route,
            arguments = listOf(navArgument("profileId"){ type = NavType.LongType })
        ) {
            val profileId = it.arguments?.getLong("profileId")
            val vm = hiltViewModel<LogEditVM>()

            if(profileId != null) {
                LogEditScreen(vm = vm)
            }
        }
    }
}