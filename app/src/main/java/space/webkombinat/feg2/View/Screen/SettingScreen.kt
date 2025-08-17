package space.webkombinat.feg2.View.Screen

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.location.LocationManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import space.webkombinat.feg2.Model.AppTheme
import space.webkombinat.feg2.Model.BLEController
import space.webkombinat.feg2.Model.Constants
import space.webkombinat.feg2.View.Devide.SystemBroadCastReceiver
import space.webkombinat.feg2.View.Module.CheckBoxPanel
import space.webkombinat.feg2.View.Module.SettingPanel
import space.webkombinat.feg2.ViewModel.SettingVM
import space.webkombinat.feg2.bleAdapter
import space.webkombinat.feg2.getScreenSize
import space.webkombinat.feg2.gpsAdapter
import space.webkombinat.feg2.requestPermissions
import kotlin.math.roundToInt

@RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    vm : SettingVM
) {

    val userSettings by vm.userSettings.collectAsState()
    var sliderPosition by remember {
        mutableStateOf(userSettings.bottomRange.toFloat()..userSettings.topRange.toFloat()) }
    var BLE_Addr by remember { mutableStateOf("") }
    val ctx = LocalContext.current
    val act = ctx as Activity

    val columnModifier = if(ctx.getScreenSize().first > 600) {modifier.width(600.dp)} else {modifier.fillMaxWidth()}

    val bluetoothEnabled = remember { mutableStateOf(bleAdapter(ctx)) }
    val gpsEnabled = remember { mutableStateOf(gpsAdapter(ctx)) }
    var brightness  by remember { mutableStateOf(userSettings.isBrightness) }
    val carib_f = remember { mutableStateOf("") }
    val carib_s = remember { mutableStateOf("") }
    LaunchedEffect(userSettings) {
        sliderPosition = userSettings.bottomRange.toFloat()..userSettings.topRange.toFloat()
        BLE_Addr = if (userSettings.BLEAddress == "") "保存済みBLEデバイス無し" else "BLE Addr: ${userSettings.BLEAddress}"
    }

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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = columnModifier
        ) {
                Text(
                    text = "設定",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = modifier.padding(16.dp)
                )

                SettingPanel(
                    settingTitle = "グラフの温度 最低温度:${sliderPosition.start.toInt()} 最高温度:${sliderPosition.endInclusive.toInt()}",
                ) {
                    RangeSlider(
                        modifier = modifier.padding(start = 16.dp, end = 16.dp),
                        value = sliderPosition,
                        steps = 49,
                        onValueChange = { range ->
                            val start = (range.start / 10).roundToInt() * 10f
                            val end = (range.endInclusive / 10).roundToInt() * 10f
                            sliderPosition = start..end
                        },
                        valueRange = 0f..500f,
                        onValueChangeFinished = {
                            vm.updateRange(sliderPosition.start.toInt(), sliderPosition.endInclusive.toInt())
                        },
                    )
                }

                SettingPanel(
                    settingTitle = "テーマ"
                ) {
                    CheckBoxPanel(
                        value = userSettings.uiTheme == AppTheme.System.num,
                        title = "システム",
                        desc = "設定の状態(android 10以上)"
                    ) {
                        vm.setTheme(AppTheme.System)
                    }
                    CheckBoxPanel(
                        value = userSettings.uiTheme == AppTheme.Black.num,
                        title = "ダーク",
                        desc = null
                    ) {
                        vm.setTheme(AppTheme.Black)
                    }

                    CheckBoxPanel(
                        value = userSettings.uiTheme == AppTheme.White.num,
                        title = "ライト",
                        desc = "デフォルトの状態"
                    ) {
                        vm.setTheme(AppTheme.White)
                    }
                }

                SettingPanel(
                    settingTitle = "BLEデバイス"
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()

                            .combinedClickable(
                                onClick = {},
                                onLongClick = {vm.clearBLEAddress()}
                            )

                            .padding(horizontal = 16.dp)
                            .height(70.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = BLE_Addr
                        )
                    }
                }

                SettingPanel(
                    settingTitle = "デバイスの状態",
                ) {
                    Column(
                        modifier = modifier.fillMaxWidth()
                            .padding(16.dp),
                    ) {
                        Row(
                            modifier = modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Bluetooth")
                            if(bluetoothEnabled.value) {
                                Text(
                                    text = "OK",
                                    color = Color(0xFF00BB00)
                                )
                            } else {
                                Text(
                                    text = "OFF",
                                    color = Color.Red
                                )
                            }
                        }
                        if(Build.VERSION.SDK_INT in Build.VERSION_CODES.M .. Build.VERSION_CODES.R) {
                            Row(
                                modifier = modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("GPS")
                                if(gpsEnabled.value) {
                                    Text(
                                        text = "OK",
                                        color = Color(0xFF00BB00)
                                    )
                                } else {
                                    Text(
                                        text = "OFF",
                                        color = Color.Red
                                    )
                                }
                            }
                        }
                    }
                }


                SettingPanel(
                    settingTitle = "7セグの明るさ:${userSettings.isBrightness}"
                ) {
                    Slider(
                        modifier = modifier
                        .padding(horizontal = 16.dp),
                        enabled = vm.bleController.BLE_STATE.value == BLEController.BLE_STATUS.CONNECTED,
                        value = brightness.toFloat(),
                        valueRange = 0f..8f,
                        steps = 7,
                        onValueChange = {
                            brightness = (it.toInt())
                        },
                        onValueChangeFinished = {
                            vm.setBrightness(brightness)
                        }
                    )
                }

                SettingPanel(
                    settingTitle = "温度-1 キャリブレーション:${userSettings.isCaribF/10f}"
                ) {
                    Row(
                        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedTextField(
                            modifier = modifier.weight(1f),
                            label = { Text("-5.0 ~ 5.0 の数値を入力") },
                            value = carib_f.value,
                            onValueChange = { newValue ->
                                if (newValue == "-" || newValue == "." || newValue == "-.") {
                                    carib_f.value = newValue
                                } else {
                                    val num = newValue.toDoubleOrNull()
                                    if (num == null || (num in -5.0..5.0)) {
                                        carib_f.value = newValue
                                    }
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            maxLines = 1
                        )
                        Spacer(modifier = modifier.width(16.dp))
                        Button(
                            enabled = vm.bleController.BLE_STATE.value == BLEController.BLE_STATUS.CONNECTED,
                            modifier = modifier
                                .height(50.dp).width(50.dp),
        //                    enabled = enabled,
                            contentPadding = PaddingValues(0.dp),
                            shape = CircleShape,
                            onClick = {
                                val sendNum = carib_f.value.toDoubleOrNull()
                                if (sendNum != null) {
                                    println("${(sendNum*10).roundToInt()}")
                                    vm.setTempCarib((sendNum * 10).roundToInt(), Constants.CHARA_CARIB.TEMP_F)
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null)
                        }
                    }
                }



                SettingPanel(
                    settingTitle = "温度-2 キャリブレーション:${userSettings.isCaribS/10f}"
                ) {
                    Row(
                        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedTextField(
                            modifier = modifier.weight(1f),
                            label = { Text("-5.0 ~ 5.0 の数値を入力") },
                            value = carib_s.value,
                            onValueChange = { newValue ->
                                if (newValue == "-" || newValue == "." || newValue == "-.") {
                                    carib_s.value = newValue
                                } else {
                                    val num = newValue.toDoubleOrNull()
                                    if (num == null || (num in -5.0..5.0)) {
                                        carib_s.value = newValue
                                    }
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            maxLines = 1
                        )
                        Spacer(modifier = modifier.width(16.dp))
                        Button(
                            enabled = vm.bleController.BLE_STATE.value == BLEController.BLE_STATUS.CONNECTED,
                            modifier = modifier
                                .height(50.dp).width(50.dp),
        //                    enabled = enabled,
                            contentPadding = PaddingValues(0.dp),
                            shape = CircleShape,
                            onClick = {
                                val sendNum = carib_s.value.toDoubleOrNull()
                                if (sendNum != null) {
                                    println("${(sendNum*10).roundToInt()}")
                                    vm.setTempCarib((sendNum * 10).roundToInt(), Constants.CHARA_CARIB.TEMP_S)
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null)
                        }
                    }
                }

                SettingPanel(
                    settingTitle = "パーミッション"
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                requestPermissions(act)
                            }

                            .padding(horizontal = 16.dp)
                            .height(70.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "権限をリクエスト"
                        )
                    }


                }


                Spacer(modifier = modifier.height(60.dp))
            }
        }
}