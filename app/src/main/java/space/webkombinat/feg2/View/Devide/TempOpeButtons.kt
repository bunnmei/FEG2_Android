package space.webkombinat.feg2.View.Devide

import android.bluetooth.BluetoothAdapter
import android.location.LocationManager
import android.os.Build
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothAudio
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import space.webkombinat.feg2.Model.BLEController
import space.webkombinat.feg2.Model.ChartState
import space.webkombinat.feg2.Model.StopWatch
import space.webkombinat.feg2.View.Module.OpeBtn
import space.webkombinat.feg2.ViewModel.ChartVM
import space.webkombinat.feg2.bleAdapter
import space.webkombinat.feg2.gpsAdapter

@Composable
fun TempOpeButtons(
    modifier: Modifier = Modifier,
    visible: MutableState<Boolean>,
    vm: ChartVM,
) {
    val ctx = LocalContext.current
    val bluetoothEnabled = remember { mutableStateOf(bleAdapter(ctx)) }
    val gpsEnabled = remember { mutableStateOf(gpsAdapter(ctx)) }

    SystemBroadCastReceiver { intent ->
        when (intent?.action) {
            BluetoothAdapter.ACTION_STATE_CHANGED -> {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                when (state) {
                    BluetoothAdapter.STATE_OFF -> {
                        bluetoothEnabled.value = false
                    }
                    BluetoothAdapter.STATE_ON -> {
                        bluetoothEnabled.value = true
                    }
                }
            }
            LocationManager.PROVIDERS_CHANGED_ACTION -> {
                val isGpsEnabled = gpsAdapter(ctx)
                gpsEnabled.value = isGpsEnabled
            }
        }

    }
    // Bluetooth Button
    OpeBtn(
        onClick = { vm.ble(ctx = ctx) },
        visible = visible.value,
        enabled = bluetoothEnabled.value &&
        if(Build.VERSION.SDK_INT in Build.VERSION_CODES.P .. Build.VERSION_CODES.R) {
            gpsEnabled.value
        } else {
            true
        }
    ) {
        when(vm.bleController.BLE_STATE.value) {
            BLEController.BLE_STATUS.SCANNING -> {
                Icon(imageVector = Icons.Default.BluetoothAudio, contentDescription = "Bluetooth Scan")
            }
            BLEController.BLE_STATUS.CONNECTED -> {
                Icon(imageVector = Icons.Default.BluetoothConnected, contentDescription = "Bluetooth Connected")
            }
            BLEController.BLE_STATUS.DISCONNECTED -> {
                Icon(imageVector = Icons.Default.Bluetooth, contentDescription = "Bluetooth Disconnected")
            }

            BLEController.BLE_STATUS.DISCONNECTING -> {
                Icon(imageVector = Icons.Default.Bluetooth, contentDescription = "Bluetooth Disconnected")
            }
        }
    }
    Spacer(modifier = modifier.height(16.dp))
    // StopWatch Button
    OpeBtn(
        onClick = { vm.stopWatchOpe() },
        visible = visible.value
    ) {
        when(vm.stopWatch.state.value) {
            StopWatch.StopWatchState.STOPED -> {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "StopWatch Start")
            }
            StopWatch.StopWatchState.STARTED -> {
                Icon(imageVector = Icons.Default.Pause, contentDescription = "StopWatch Stop")
            }
            StopWatch.StopWatchState.PAUSED -> {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "StopWatch Start")
            }
        }
    }
    Spacer(modifier = modifier.height(16.dp))
    // Chart Keep Button
    OpeBtn(
        onClick = { vm.keepData() },
        enabled =
            vm.chartState.temp_f_chart.isNotEmpty() &&
            vm.chartState.charData.value == ChartState.CharData.UNSAVED &&
            vm.stopWatch.state.value == StopWatch.StopWatchState.PAUSED,
        visible = visible.value
    ){
        Icon(imageVector = Icons.Default.Download, contentDescription = "Bluetooth")
    }
    Spacer(modifier = modifier.height(16.dp))
    // Clear Time And Chart Button
    OpeBtn(
        onClick = { vm.clear() },
        enabled =
            vm.chartState.temp_f_chart.isNotEmpty() &&
            vm.stopWatch.state.value == StopWatch.StopWatchState.PAUSED,
        visible = visible.value
    ){
        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Bluetooth")
    }
    Spacer(modifier = modifier.height(16.dp))
}