package space.webkombinat.feg2.View.Devide

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import space.webkombinat.feg2.Model.BottomNavItem
import space.webkombinat.feg2.Model.LogNavItem
import space.webkombinat.feg2.View.Module.BottomVisibleAnimation

@Composable
fun BottomNavBar(
    visible: MutableState<Boolean>,
    navCont: NavController,
    items: List<BottomNavItem> = listOf(
        BottomNavItem.LogList,
        BottomNavItem.Chart,
        BottomNavItem.Setting
    ),
    modifier: Modifier = Modifier,
    onItemClick: (BottomNavItem) -> Unit,
) {
    var selectedItem by rememberSaveable {
        mutableStateOf(value = BottomNavItem.Chart.number)
    }
    val backStackEntry = navCont.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route

    // ネストされたナビゲーションに入った時SelectedItemの色をつけるため
    LaunchedEffect(key1 = currentRoute) {
    // println("current route $currentRoute")
        if (currentRoute == null) return@LaunchedEffect
        if(currentRoute in LogNavItem.logRoutes){
            selectedItem = BottomNavItem.LogList.number
        } else if(currentRoute == BottomNavItem.Chart.route){
            selectedItem = BottomNavItem.Chart.number
        } else {
            selectedItem = BottomNavItem.Setting.number
        }
        visible.value = true

    }

    BottomVisibleAnimation(
        visible = visible.value,
    ) {
        NavigationBar(
            modifier = modifier
                .navigationBarsPadding()
                .height(60.dp)
        ) {
            print("current item num bar $selectedItem")
            items.forEach { item ->
                NavigationBarItem(
                    selected = selectedItem == item.number,
                    onClick = {
                        selectedItem = item.number
                        onItemClick(item)
                    },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label
                        )
                    }
                )
            }
        }
    }
}