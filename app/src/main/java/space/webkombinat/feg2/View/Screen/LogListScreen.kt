package space.webkombinat.feg2.View.Screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import space.webkombinat.feg2.View.Module.ProfileCard
import space.webkombinat.feg2.ViewModel.LogListVM
import space.webkombinat.feg2.getScreenSize
import java.text.SimpleDateFormat

@Composable
fun LogListScreen(
    modifier: Modifier = Modifier,
    navCont: NavController,
    vm: LogListVM
) {
    val profiles by vm.profiles.collectAsState(emptyList())
    val profileId by vm.profileId.collectAsState(initial = -1)
    val ctx = LocalContext.current
    val columnModifier = if(ctx.getScreenSize().first > 600) {modifier.width(600.dp)} else {modifier.fillMaxWidth()}

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = columnModifier,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Spacer(modifier = modifier.height((ctx.getScreenSize().first / 20).dp))
            }
            if (profiles.isEmpty()) {
                item {
                    ProfileCard(
                        name = "No Profile",
                        description = "保存されているプロファイルはありません。",
                        navNext = {},
                        rmProfileId = {},
                        createAt = -1L,
                        profileId = -1L,
                        keptProfileId = -1L
                    )
                }
            }
            items(profiles) {profiles ->
                ProfileCard(
                    name = profiles.name,
                    description = profiles.description,
                    navNext = {
                        navCont.navigate("/logDetail/${profiles.id}")
                    },
                    rmProfileId = {
                        vm.clearProfileId()
                    },
                    createAt = profiles.createAt,
                    profileId = profiles.id,
                    keptProfileId = profileId
                )
            }
            item {
                Spacer(modifier = modifier.height( 60.dp ))
            }
        }
    }
}