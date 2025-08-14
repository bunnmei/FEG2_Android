package space.webkombinat.feg2.View.Screen

import android.Manifest
import android.bluetooth.BluetoothAdapter
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import space.webkombinat.feg2.Model.ChartState
import space.webkombinat.feg2.View.Devide.SystemBroadCastReceiver
import space.webkombinat.feg2.View.Devide.TempOpeButtons
import space.webkombinat.feg2.View.Module.BottomVisibleAnimation
import space.webkombinat.feg2.View.Module.ChartCanvas
import space.webkombinat.feg2.View.Module.OperateVisibleBtn
import space.webkombinat.feg2.View.Module.StatusPanel
import space.webkombinat.feg2.View.Module.TempMemoryCanvas
import space.webkombinat.feg2.View.Module.TimeMemoryCanvas
import space.webkombinat.feg2.ViewModel.ChartVM
import space.webkombinat.feg2.clickableNoRipple

@RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
@Composable
fun ChartScreen(
    modifier: Modifier = Modifier,
    vm: ChartVM,
    bottomVisible: MutableState<Boolean>,
    bottomToggle: () -> Unit
) {
    val scrollState = rememberScrollState()
    val uiState by vm.uiState.collectAsState()
    val chart by vm.profileLinkChart.collectAsState()
    val range by vm.userSettings.collectAsState()

    val rotate = remember { mutableStateOf(false) }


    Box(
        modifier = modifier
            .fillMaxSize()
            .clickableNoRipple(onClick = bottomToggle) //MainActivity
//            .background(Color.Gray)
    ) {
        when (vm.chartState.charData.value) {
            ChartState.CharData.SAVING -> {
                Column(
                    modifier = modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "保存中",
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    )
                    Spacer(modifier = modifier.height(16.dp))
                    CircularProgressIndicator()
                }
            }
            else -> {
                ChartCanvas(
                    scrollState = scrollState,
                    temp_f = vm.chartState.temp_f_chart,
                    temp_s = vm.chartState.temp_s_chart,
                    topRange = range.topRange,
                    bottomRange = range.bottomRange,
                    temp_f_watermark = chart.temp_f,
                    temp_s_watermark = chart.temp_s
                )
                Row(modifier = Modifier.fillMaxSize()) {
                    TempMemoryCanvas(
                        topRange = range.topRange,
                        bottomRange = range.bottomRange
                    ) //fixed left temp memory
                    Spacer(modifier = modifier.weight(1f))
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        // time temp1 temp2
                        StatusPanel(str = vm.stopWatch.displayTime.value, color = Color.Gray)
                        StatusPanel(str = "%.1f".format(uiState.temp_f), color = Color(0xFFDC5785))
                        StatusPanel(str = "%.1f".format(uiState.temp_s), color = Color(0xFF548DB1))
                    }
                }
                Column(
                    modifier = modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    BottomVisibleAnimation(visible = bottomVisible.value) {
                        Column {
                            OperateVisibleBtn(rotate = rotate)
                            {
                                TempOpeButtons(
                                    vm = vm,
                                    visible = rotate
                                )
                            }
                            TimeMemoryCanvas(scrollState = scrollState)
                            Spacer(modifier.height(60.dp))
                        }
                    }
                }

            }
        }
    }
}

