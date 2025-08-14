package space.webkombinat.feg2.View.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import space.webkombinat.feg2.View.Module.BottomVisibleAnimation
import space.webkombinat.feg2.View.Module.ChartCanvas
import space.webkombinat.feg2.View.Module.OpeBtn
import space.webkombinat.feg2.View.Module.TempMemoryCanvas
import space.webkombinat.feg2.View.Module.TimeMemoryCanvas
import space.webkombinat.feg2.ViewModel.LogDetailVM
import space.webkombinat.feg2.clickableNoRipple

@Composable
fun LogDetailScreen(
    modifier: Modifier = Modifier,
    navCont: NavController,
    vm: LogDetailVM,
    bottomVisible: MutableState<Boolean>,
    bottomToggle: () -> Unit
) {
    val scrollState = rememberScrollState()
    val uiState by vm.profileLinkChart.collectAsState()
    val range by vm.userSettings.collectAsState()
    val deleteProfileState by vm.deleteProfileState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickableNoRipple(onClick = bottomToggle) //MainActivity
//            .background(Color.Gray)
    ) {
        when (deleteProfileState) {
            LogDetailVM.DeleteProfileState.None -> {
                ChartCanvas(
                    scrollState = scrollState,
                    temp_f = uiState.temp_f,
                    temp_s = uiState.temp_s,
                    topRange = range.topRange,
                    bottomRange = range.bottomRange
                )
                TempMemoryCanvas(
                    topRange = range.topRange,
                    bottomRange = range.bottomRange
                )
                Column(
                    modifier = modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom
                ){
                    BottomVisibleAnimation(visible = bottomVisible.value) {
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.Bottom,
                        ) {
                            Column(
                                modifier = modifier
                                    .fillMaxWidth()
                                    .padding(end = 16.dp),
                                horizontalAlignment = Alignment.End
                            ) {
                                OpeBtn(onClick = {
                                    vm.deleteProfile(navCont)
                                }) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "delete profile")
                                }
                                Spacer(modifier = modifier.height(16.dp))

                                OpeBtn(onClick = {
                                    vm.bookmarkProfileId()
                                }) {
                                    Icon(imageVector = Icons.Default.Bookmark, contentDescription = "bookmark profile id")
                                }
                                Spacer(modifier = modifier.height(16.dp))

                                OpeBtn(onClick = {
                                    navCont.navigate("/logEdit/${vm.profileId}")
                                }) {
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "edit profile")
                                }
                            }
                            TimeMemoryCanvas(scrollState = scrollState)
                            Spacer(modifier.height(60.dp))
                        }
                    }
                }
            }
            LogDetailVM.DeleteProfileState.Deleting -> {
                Column(
                    modifier = modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "削除中",
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    )
                    Spacer(modifier = modifier.height(16.dp))
                    CircularProgressIndicator()
                }
            }
            LogDetailVM.DeleteProfileState.Deleted -> {
                navCont.popBackStack()
            }
        }
    }
}